# 兼容性方案
标签：``兼容性`` ``版本升级`` 

## 1. 设计目标
FISCO BCOS 3.X版本迭代，为实现各版本之间的兼容，FISCO BCOS针对网络层面与数据层面的兼容性设计了相应的兼容性方案。 方案的目标主要包括两点，第一，
可保证各个版本之间的网络、数据、执行模块、公用的编解码协议(scale/abi)均可向后兼容，第二，支持可灰度升级，且灰度升级过程中，系统可正常共识、出块。


## 2. 网络兼容性设计
FISCO BCOS版本更新时，网络模块会在建立/断开网络连接期间交换、协商版本信息，实现网络模块版本之间的兼容性，具体设计如下：
   1. 建立连接/断开重连后，节点间(or 节点与SDK之间)交换版本信息，进行版本协商
   2. 每个网络相关的功能模块均对应兼容性版本

具体而言，通过设计ProtocolVersion来标识不同的版本，实现各个版本的兼容：
```c++
enum ProtocolVersion: uint16_t
{
V1 = 1,
V2 = 2,
... ...
};
```

### 2.1 RPC服务接口兼容性
   对于RPC服务接口，也需要保证其可向后兼容，并调研、测试tars服务的兼容性，参考：[Versioning gRPC services](https://learn.microsoft.com/en-us/aspnet/core/grpc/versioning?view=aspnetcore-6.0)
   - gRPC :
   - tars : 不支持接口修改，但可以通过传结构体tars做到接口兼容
   

### 2.2 网络编解码协议兼容性
   对于网络编解码协议兼容性，FISCO BCOS通过在P2PMessage/WSMessage中设计添加version 字段实现兼容，当节点接收到消息后，根据消息中 version 调用相应的编解码方法。
   P2PMessage/WSMessage 具体数据结构设计如下：
   ```c++
class P2PMessageFactory
{
public:
P2PMessageFactory()
{
// 设置m_protocol2Codec
}
// 网络消息包版本到其对应编解码协议之间的映射，初始化时设置
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
// 消息编码
bool encode(bytes& _buffer) override
{
auto encoder = m_protocol2Codec.at(m_version);
return encoder->encode(_buffer, std::weak_ptr<P2PMessage>
(shared_from_this()));
}
// 消息解码
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

总的来说，FISCOBCOS在网络应用层（同步、共识）消息均使用PB编码，具备向后兼容性；对于AMOP消息包而言，其采用二进制编码，设计在AMOP信息中添加version字段，便于扩展；
FISCO BCOS在块高推送、群组信息、EventLog推送均采用json编解码，具备向后兼容性。

###2.3 网络层协议兼容性
FISCO BCOS通过握手协商Peer的版本信息：主要涉及的模块包括 Gateway , AMOP , EventSub , RPC；而握手协商的版本信息保存在 Session 中，具体相关协议信息设计如下：
ProtocolInfo定义具体的协议信息:
```c++
   class ProtocolInfo
   {
   // 模块ID
   ModuleID m_moduleID;
   // 当前使用的版本号
   ProtocolVersion m_currentVersion;
   // 支持的最小版本号
   ProtocolVersion m_minVersion;
   // 支持的最大版本
   ProtocolVersion m_maxVersion;
   };
```
定义本地支持的协议版本:
```c++
class Protocol
   {
   public:
   Protocol()
   {
   // 初始化本地协议信息，如：
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
        
        NodeA->>NodeB: 建立连接
        NodeA->>NodeB: NodeA 协议列表
        NodeB->>NodeB: 计算NodeA协议列表
        NodeB->>NodeA: NodeB 协议列表
        NodeA->>NodeA: 计算NodeB 协议列表
```

协议协商方法设计：
```c++
ProtocolVerison handleProtocol(ModuleID _module, ProtocolInfo::Ptr _protocol)
{
// 找到本地module对应的协议信息
auto localProtocol = m_supportedProtocols.at(_module);
// case1: 握手协商失败
// 本地的最大版本号小于源节点的最小版本号，或者本地的最小版本号大于源节点的最小版本号
if(_protocol->minVersion() > localProtocol->maxVersion() || _protocol->maxVersion() < localProtocol->minVersion())
{
// disconnect session
return -1;
}
// case2: 两者版本号有重叠
return std::min(localProtocol->maxVersion(), _protocol->maxVersion());
}
```

扩充当前modeleID

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
AMOPClient = 3001, // SDK/bcos-rpc中AMOP协议ID
EventSub = 5000, // 事件监听协议
RPC = 6000, // RPC协议，包括块高推送、握手协议、群组信息推送等
// gatewayservice
AMOPServer = 3000, // bcos-gateway之间AMOP协议ID
Gateway = 4000, // bcos-gateway之间的协议(如交换基本信息等)
};
```

session维护协商的版本信息:
```c++
// bcos-gateway
class Session
{
// 提供接口，可以获取每个session协商的版本号
ProtocolInfo::Ptr getProtocolInfoByModule(ModuleID){}
// 模块ID到协商的版本信息
std::map<ModuleID, ProtocolInfo::Ptr> m_negotiatedVersion;
};
```

共识/同步可向bcos-gateway查询协议版本,bcos-gateway可推送协议版本
```c++
class FrontServiceInterface
{
// 提供获取Protocol的接口
virtual void asyncGetProtocols(ModuleID _module,
std::function<void(Error::Ptr, std::map<NodeID, ProtocolInfo>)>) = 0;
// 可以向注册模块推送Protocol信息
}

```

### 3. 数据兼容性设计
相比于网络层面的兼容行设计，数据层面无法做到像网络层那样平滑升级,须通过系统合约触发系统升级
具体而言FISCO BCOS数据兼容性主要涉及到:
- block、blockHeader、transaction、receipt等基本数据结构：
  
  (1) 这些字段中均带有version，可通过version做到编解码层面兼容；
  
  (2) block, blockHeader, transaction 由共识打包模块 sealer 产生
  
  (3) receipt 由执行模块 Executor 产生
- 交易执行：可根据 BlockContext 中的 m_blockVersion 实现执行层面兼容性

###3.1 数据协议版本定义
FISCOBCOS 设计主版本号与次版本号，例如针对FISCOBCOS 3.x，版本号设计如下：
```c++
3.0.0: 0x03000000
3.1.0: 0x03010000
3.1.1: 0x03010100
3.2.0: 0x03020000
...
```

####3.2 数据协议存储与变更
FISCOBCOS 设计 sys_config 系统表存储记录数据版本号的信息，其key与value如下：
- key: compatibility_version
- value: 下一个块使用的协议版本号，如： 3.0.x , 3.1.x 等

如果发生版本更新，需要变更数据协议，修改 sys_config 的 compatibility_version 配置项即可，对于compability_version的维护，主要涉及打包模块与BlockContext:
- 打包模块：Sealer，用于支持区块、交易等数据结构变更
- BlockContext ：用于支持执行兼容性(如Precompiled合约等)

## 4. 结论
综上，FISCOBCOS设计兼容性方案，在上层各模块的编解码协议统一采用PB编码；对于网络协议兼容性，则按照服务进程进行划分，根据服务进程实现不同服务的兼容性，对于数据层面的兼容性，通过block、blockHeader、transaction、receipt等基本数据结构中以及sys_config 系统表中的版本信息实现版本兼容。

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
AMOPClient = 3001, // SDK/bcos-rpc中AMOP协议ID
EventSub = 5000, // 事件监听协议
RPC = 6000, // RPC协议，包括块高推送、握手协议、群组信息推送等
// gatewayservice
AMOPServer = 3000, // bcos-gateway之间AMOP协议ID
Gateway = 4000, // bcos-gateway之间的协议(如交换基本信息等)
// SDK
};
```

