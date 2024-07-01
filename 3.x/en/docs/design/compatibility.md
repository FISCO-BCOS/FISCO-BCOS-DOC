# 16. Compatibility program
Tags: "Compatibility" "Version Upgrade"

-----
## 1. Design Objectives
FISCO BCOS version 3.X iteration, in order to achieve compatibility between versions, FISCO BCOS for the network level and data level compatibility designed the corresponding compatibility program。 the objectives of the program mainly include two points, first,
It can guarantee the network, data, execution module and common codec protocol between various versions.(scale/abi)All can be backward compatible, second, support can be gray-scale upgrade, and gray-scale upgrade process, the system can be normal consensus, out of the block.。


## 2. Network Compatibility Design
When the FISCO BCOS version is updated, the network module will exchange and negotiate version information during the establishment / disconnection of the network connection to achieve compatibility between network module versions. The specific design is as follows:
   1. After establishing connection / disconnecting reconnection, between nodes(Between or node and SDK)Exchange version information and conduct version negotiation
   2. Each network-related functional module corresponds to a compatibility version.

Specifically, ProtocolVersion is designed to identify different versions and achieve compatibility between versions:
```c++
enum ProtocolVersion: uint16_t
{
V1 = 1,
V2 = 2,
... ...
};
```

### 2.1 RPC Service Interface Compatibility
   For RPC service interfaces, you must ensure that they are backward compatible, and investigate and test the compatibility of the tars service. For more information, see [Versioning gRPC services](https://learn.microsoft.com/en-us/aspnet/core/grpc/versioning?view=aspnetcore-6.0)
   - gRPC :
   - tars : Interface modification is not supported, but interface compatibility can be achieved by passing structure tars
   

### 2.2 Network Codec Protocol Compatibility
   For network codec protocol compatibility, FISCO BCOS implements compatibility by adding the version field in the P2PMessage / WSMessage design, and when the node receives the message, it calls the corresponding codec method according to the version in the message.。
   The specific data structure of P2PMessage / WSMessage is designed as follows:
   ```c++
class P2PMessageFactory
{
public:
P2PMessageFactory()
{
/ / set m _ protocol2Codec
}
/ / The mapping between the network message package version and its corresponding codec protocol.
std::shared_ptr<std::map<ProtocolVersion, P2PCodec::Ptr>> m_protocol2Codec;
}

class P2PCodec
{
public:
virtual bool encode(bytes& _buffer, std::weak_ptr<P2PMessage>) = 0;
virtual ssize_t decode(bytesConstRef _buffer, std::weak_ptr<P2PMessage>) = 0;
};

class P2PMessage
{
public:
P2PMessage: m_version(ProtocolVersion::V1){}
/ / Message encoding
bool encode(bytes& _buffer) override
{
auto encoder = m_protocol2Codec.at(m_version);
return encoder->encode(_buffer, std::weak_ptr<P2PMessage>
(shared_from_this()));
}
/ / Message decoding
ssize_t decode(bytesConstRef _buffer) override
{
// decode version
auto version = ...
auto codec = m_protocol2Codec.at(version);
return codec->decode(_buffer, std::weak_ptr<P2PMessage>
(shared_from_this()));
}
protected:
ProtocolVersion m_version;
uint32_t m_length;
... ...
std::shared_ptr<std::map<ProtocolVersion, P2PCodec::Ptr>> m_protocol2Codec;
};

```

In general, FISCOBCOS uses PB encoding in the network application layer (synchronization, consensus) messages, with backward compatibility.；For AMOP message packets, it uses binary encoding, and the design adds a version field to the AMOP information for easy expansion.；
FISCO BCOS uses JSON encoding and decoding in block high push, group information, and EventLog push, with backward compatibility.。

###2.3 Network Layer Protocol Compatibility
FISCO BCOS negotiates Peer version information through handshake: the main modules involved include Gateway, AMOP, EventSub, RPC；The version information of handshake negotiation is stored in the Session. The specific related protocol information is designed as follows:
ProtocolInfo Defines Specific Protocol Information:
```c++
   class ProtocolInfo
   {
   / / Module ID
   ModuleID m_moduleID;
   / / the version number currently in use
   ProtocolVersion m_currentVersion;
   / / Minimum supported version number
   ProtocolVersion m_minVersion;
   / / Maximum supported version
   ProtocolVersion m_maxVersion;
   };
```
Define locally supported protocol versions:
```c++
class Protocol
   {
   public:
   Protocol()
   {
   / / Initialize local protocol information, such as:
   m_supportedProtocols.insert({ModuleID::Gateway, {ModuleID::Gateway,
   ProtocolVersion::V1, ProtocolVersion::V1, ProtocolVersionV1}});
   ... ..
   }
   ~Protocol(){}
   private:
   std::map<ModuleID, ProtocolInfo::Ptr> m_supportedProtocols;
```
```eval_rst
.. mermaid::

    sequenceDiagram
        participant NodeA
        participant NodeB
        
        NodeA->>NodeB: Establish a connection
        NodeA->>NodeB: NodeA Protocol List
        NodeB->>NodeB: Calculate NodeA Protocol List
        NodeB->>NodeA: NodeB Protocol List
        NodeA->>NodeA: Calculate NodeB Protocol List
```

agreement negotiation method design
```c++
ProtocolVerison handleProtocol(ModuleID _module, ProtocolInfo::Ptr _protocol)
{
/ / Find the protocol information corresponding to the local module
auto localProtocol = m_supportedProtocols.at(_module);
// case1: Handshake negotiation failed
/ / The local maximum version number is less than the minimum version number of the source node, or the local minimum version number is greater than the minimum version number of the source node
if(_protocol->minVersion() > localProtocol->maxVersion() || _protocol->maxVersion() < localProtocol->minVersion())
{
// disconnect session
return -1;
}
// case2: Both version numbers overlap
return std::min(localProtocol->maxVersion(), _protocol->maxVersion());
}
```

Extend the current modeleID

```c++
enum ModuleID
{
// nodeservice
PBFT = 1000,
Raft = 1001,
BlockSync = 2000,
TxsSync = 2001,
// executorservice
Executor = xxx,
// rpcservice
AMOPClient = 3001, // SDK/bcos-AMOP protocol ID in rpc
EventSub = 5000, / / Event Listening Protocol
RPC = 6000, / / RPC protocol, including block high push, handshake protocol, group information push, etc.
// gatewayservice
AMOPServer = 3000, // bcos-AMOP protocol ID between gateway
Gateway = 4000, // bcos-Agreement between gateways(such as exchanging basic information)
};
```

session Maintains Negotiated Version Information:
```c++
// bcos-gateway
class Session
{
/ / Provide an interface to obtain the version number negotiated for each session
ProtocolInfo::Ptr getProtocolInfoByModule(ModuleID){}
/ / Module ID to negotiated version information
std::map<ModuleID, ProtocolInfo::Ptr> m_negotiatedVersion;
};
```

consensus / synchronization can be bcos-gateway query protocol version, bcos-Gateway Pushable Protocol Version
```c++
class FrontServiceInterface
{
/ / Provide the interface for obtaining the Protocol
virtual void asyncGetProtocols(ModuleID _module,
std::function<void(Error::Ptr, std::map<NodeID, ProtocolInfo>)>) = 0;
/ / You can push the Protocol information to the registration module
}

```

### 3. Data Compatibility Design
Compared with the compatible line design at the network level, the data level cannot be upgraded smoothly like the network layer, and the system upgrade must be triggered through the system contract.
Specifically, FISCO BCOS data compatibility is mainly related to:
- Basic data structures such as block, blockHeader, transaction, and receive:
  
  (1) Each of these fields has a version. You can use version to achieve codec compatibility.；
  
  (2) block, blockHeader, transaction generated by the consensus packaging module sealer
  
  (3) receipt is generated by the execution module Executor
- Transaction execution: execution level compatibility can be achieved based on m _ blockVersion in BlockContext

###3.1 Data Protocol Version Definition
FISCOBCOS designs major and minor version numbers. For example, for FISCOBCOS 3.x, the version numbers are designed as follows:
```c++
3.0.0: 0x03000000
3.1.0: 0x03010000
3.1.1: 0x03010100
3.2.0: 0x03020000
3.3.0: 0x03030000
...
```

####3.2 Data protocol storage and change
The sys _ config system table designed by FISCOBCOS stores information about the version number of record data. The key and value are as follows:
- key: compatibility_version
- value: The protocol version used by the next block, such as 3.0.x, 3.1.x, etc.

If a version update occurs, you need to change the data protocol and modify the compatibility _ version configuration item of sys _ config.:
- Packaging module: Sealer, used to support data structure changes such as blocks and transactions
- BlockContext: Used to support execution compatibility(Precompiled contracts, etc.)

## 4. 结论
In summary, FISCOBCOS design compatibility scheme, in the upper module of the codec protocol unified use of PB encoding；For network protocol compatibility, it is divided according to the service process, according to the service process to achieve the compatibility of different services, for data compatibility, through the block, blockHeader, transaction, receipt and other basic data structures and sys _ config system table version information to achieve version compatibility。

```c++
enum ModuleID
{
// nodeservice
PBFT = 1000,
Raft = 1001,
BlockSync = 2000,
TxsSync = 2001,
// executorservice
Executor = xxx,
// rpcservice
AMOPClient = 3001, // SDK/bcos-AMOP protocol ID in rpc
EventSub = 5000, / / Event Listening Protocol
RPC = 6000, / / RPC protocol, including block high push, handshake protocol, group information push, etc.
// gatewayservice
AMOPServer = 3000, // bcos-AMOP protocol ID between gateway
Gateway = 4000, // bcos-Agreement between gateways(such as exchanging basic information)
// SDK
};
```

