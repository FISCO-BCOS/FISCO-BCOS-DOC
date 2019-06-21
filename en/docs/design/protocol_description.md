# Protocol description

## Transaction structure and its RLP coding description

The transaction structure of FISCO BCOS has been increased or decreased some fields based on the transaction structure of the original Ethereum. The transaction structure fields of FISCO BCOS 2.0.0 are as follows:

| name           | type            | description                                                  | RLP index |
| :------------- | :-------------- | :----------------------------------------------------------- | --------- |
| type           | enum            | Transaction type, represents whether the transaction is a contract creation or a contract transaction, initially an empty contract. | -         |
| nonce          | u256            | A random number provided by the message sender, to uniquely identify the transaction. | 0         |
| value          | u256            | The amount of the transfer. FISCO BCOS does not use this field.| 5         |
| receiveAddress | h160            | The receiver address. type is 0x0 when the contract is created.| 4         |
| gasPrice       | u256            | The unit price of gas in this transaction. In FISCO BCOS, it is fixed as 300000000.| 1         |
| gas            | u256            | This transaction allows the maximum amount of gas consumed. In FISCO BCOS, this value can be configured. | 2         |
| data           | vector< byte >  | It is the data related to the transaction, or the initialization parameter when creating the contract.| 6         |
| chainId        | u256            | It records chain/transactional information of the transaction                            | -             | 7             |
| groupId        | u256            | It records the group of the transaction                                       | -             | 8             |
| extraData      | vector< byte >  | Reserved field, recording transaction information, using “#” internally to separate information                | -             | 9             |
| vrs            | SignatureStruct | Data that generated after transaction sender signs the hash on 7 field RLP code of the transaction        | 7,8,9         | 10,11,12      |
| hashWith       | h256            | The hash of all fields (containing signature) after RPL code             | -             | -             |
| sender         | h160            | Transaction sender's address based on vrs                                 | -             | -             |
| blockLimit     | u256            | Transaction life cycle, the last processed block number of this transaction, FISCO BCOS new field     | 3             | 3             |
| importTime     | u256            | Unix timestamp when transaction enters txPool, FISCO BCOS new field               | -             | -             |
| rpcCallback    | function        | RPC callback after block generation, FISCO BCOS new field                        | -             | -             |

The generation process of the hashWith field (also called transaction hash/transaction unique identifier) in RC1 is as follows:

![](../../images/node_management/generate_hash_process.png)

It is similar with RC2 generation process, only that the transaction struct of `rlp+hash` in the first step is added with chainId, groupId, extraData the three fields.

## Block structure and its RLP coding description

The block of FISCO BCOS consists of the following five parts:

| name                | description                                      | RLP index |
| :------------------ | :----------------------------------------------- | --------- |
| blockHeader         | Block header RLP coding                                  | 0         |
| transactions        | Transaction list RLP code                                 | 1         |
| transactionReceipts | Transaction receipt list RLP code                             | 2         |
| hash                | The hash encoded by block header RLP encoded                         | 3         |
| sigList             | The node signature list that is collected during PBFT consensus. Raft does not use this. | 4         |


The description of each field in the block header of FISCO BCOS is as follows:

| name             | type          | description                                                          | RLP index |
| :--------------- | :------------ | :------------------------------------------------------------------- | --------- |
| parentHash       | h256          | parent blocks hash                                                       | 0         |
| stateRoot        | h256          | The root hash of the state tree                                          | 1         |
| transactionsRoot | h256          | The root hash of the transaction tree                                    | 2         |
| receiptsRoot     | h256          | The root hash of the receipt tree                                        | 3         |
| dbHash           | h256          | Distributed storage records the data written in a block by calculating the hash. A new field in FISCO BCOS. | 4         |
| logBloom         | LogBloom      | The Bloom filter consisting of transaction receipt logs. It is not used in FISCO BCOS currently.               | 5         |
| number           | int64_t       | The block number, counts from 0.                                     | 6         |
| gasLimit         | u256          | The upper limit of Gas consumed by all transactions in this block.  | 7         |
| gasUsed          | u256          | The sum of the Gas used in all transactions in this block.     | 8         |
| timestamp        | int64_t       | The unix timestamp of the packed block.                                | 9         |
| extraData        | vector<bytes> | Additional data for the block. Currently, it is only used to record group genesis file information in block 0 in FISCO BCOS | 10        |
| sealer           | u256          | The index of the node of packed block in the sealer list. A new field in FISCO BCOS.             | 11        |
| sealerList       | vector<h512>  | The list of sealer nodes (without observing nodes). A new field in FISCO BCOS. | 12        |
| hash             | h256          | The hash of the first 13 fields of the block header after RLP encoding. A new field in FISCO BCOS.               | -         |

## Network transmission protocol

FISCO BCOS currently has two types of data packet formats. The data packets communicated among nodes are in the P2PMessage format, and the data packets communicated between nodes and SDK are in the ChannelMessage format.

![](../../images/node_management/message_type.png)

### P2PMessage: v2.0.0-rc1

The header of v2.0.0-rc1 P2PMessage package contains 12 bytes. The basic form is:

![](../../images/node_management/p2p_message_rc1.png)

| name       | type         | description                          |
| :--------- | :----------- | :----------------------------------- |
| Length     | uint32_t     | Data packet length, including header and data             |
| groupID    | int8_t       | Group ID, its range is 1-127                   |
| ModuleID   | uint8_t      | Module ID，its range is 1-255                    |
| packetType | uint16_t     | Data packet type, is the identifier of sub-protocol under the same module ID |
| seq        | uint32_t     | Data packet serial number, increased by each packet        |
| data       | vector<byte> | Data, its length is length-12            |

The module ID is divided as follows:

| ModuleID | message           |
| :------- | :---------------- |
| 1        | AMOP submodule of P2P   |
| 2        | Topic submodule of P2P  |
| 3~7      | reserve for other P2P submodules |
| 8        | PBFT submodule in consensus  |
| 9        | Block synchronization module     |
| 10       | Transaction pool module        |
| 11       | Raft submodule in consensus |
| 12~255   | reserve for other modules      |


### P2PMessage: v2.0.0-rc2

V2.0.0-rc2 has expanded the range of **group ID and model ID**, **supporting 32767 groups at most**. It has also increased **Version** field for other features (like network compression) with package header being 16 bytes. The network data package structure of v2.0.0-rc2 is as below:

![](../../images/features/network_packet.png)

| name       | type         | description                          |
| :--------- | :----------- | :----------------------------------- |
| Length     | uint32_t     | length of data package, containing header and data             |
| Version    | uint16_t     | it records version and features information of data package, currently the highest 0x8000 is to record if the data package is compressed or not|
| groupID (GID)    | int16_t  | group ID, ranging from 1-32767   |
| ModuleID (MID)   | uint16_t | model ID, ranging from 1-65535   |
| PacketType | uint16_t     | data packet type, sub-protocol identification with same mode ID  |
| Seq        | uint32_t     | data packet serial number, each increment itself         |
| Data       | vector<byte> | data itself, length length-12           |



**Additional**

1. P2PMessage does not limit the packet size, and the packet size management is performed by the upper layer calling module (consensus/synchronization/AMOP, etc.);
2. The group ID and module ID can uniquely identify the protocol ID, and the relationship among them is `protocolID = (groupID << sizeof(groupID)*8) | ModuleID`;
3. The data packet distinguishes between request packet and response packet by the 16-bit binary value where the protocolID is located.  The data greater than 0 is the request packet, and less than 0 is the corresponding packet.
4. The packetType currently used by AMOP include `SendTopicSeq = 1，RequestTopics = 2，SendTopics = 3`.

### ChannelMessage

| name   | type         | description                                  |
| :----- | :----------- | :------------------------------------------- |
| length| uint32_t     | Data packet length, including header and data, up to 10M Byte |
| type   | uint16_t     | Data packet type                                   |
| seq    | string       | Data packet serial number, 32 bytes, introduced by SDK                |
| result | int          | Process result                                     |
| data   | vector<byte> | Data                                     |

The packet type enumeration value and its corresponding description are as follows:

| code    | message       | direction |
| :------ | :------------ | :-------- |
| 0x12    | Ethereum message    | SDK->node |
| 0x13    | Heartbeat packet        | SDK->node |
| 0x30    | AMOP request packet    | SDK->node |
| 0x31    | AMOP response packet    | SDK->node |
| 0x32    | Report Topic information | SDK->node |
| 0x10000 | Transaction on chain callback | node->SDK |

The process result enumeration value and its corresponding description are as follows:

| code | message    |
| :--- | :--------- |
| 0    | successful       |
| 100  | node unreachable |
| 101  | SDK unreachable |
| 102  | time out       |
