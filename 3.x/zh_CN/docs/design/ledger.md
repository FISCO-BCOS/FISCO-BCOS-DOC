# 交易、区块数据结构

标签：``数据结构`` ``RLP`` ``区块结构`` ``消息包`` ``交易结构``

----

## 交易结构描述

FISCO BCOS的交易结构在原以太坊的交易结构的基础上，有所增减字段。FISCO BCOS 3.0+的交易结构由以下两部分组成：

| name            | type    | description                                               | 
| :-------------  |:--------| :-------------------------------------------------------- |
| data            | TransactionData|包含版本信息、链配置信息、相关交易信息，具体内容如下所示      |
| dataHash        | vector< byte >  | data结构体所对应的hash值                             | 
| signature       | vector< byte >  | 交易携带的签名信息                                    | 
| sender          | vector< byte >  | 交易发起者地址                                       |
| importTime      | u256            | 交易进入交易池的unix时间戳，FISCO BCOS新增字段          | 
| attribute       | int             |                       |
| source          | string          |                       |
其中TransactionData具体信息如下所示：

| name           | type            | description     |
| :------------- |:--------| :--------------------------------------------------------|
| version        | int             | 记录本次交易所属的链的版本信息                         |
| chainId        | u256            | 记录本次交易所属的链信息/业务信息                       |
| groupId        | u256            | 记录本次交易所属的群组                                |
| blockLimit     | long            | 交易生命周期，该交易最晚被处理的块高，FISCO BCOS新增字段  | 
| nonce          | string          | 消息发送方提供的随机数，用于唯一标识交易                 |
| to             | string          | 交易接受方地址                                      |
| input          | vector< byte >  | 一笔交易输入参数，前四个字节代表函数名，剩余字节为输入参数  |
| abi            | string          | 智能合约交易所对应的abi                              |
## 区块结构描述

FISCO BCOS的区块由以下五部分组成


| name           | type            | description     |
| :------------- |:--------| :--------------------------------------------------------|
| version              | int         | 记录本区块所属链版本信息        | 
| type                 | int         | 记录本区块类型                |
| blockHeader          | BLockHeader | 区块头信息                   |
| transactions         | vector<Transaction>        | 交易列表      |                                 
| transactionReceipts  | vector<transactionReceipt> | 交易回执列表   |
| transactionsMetaData | vector<byte>               | 区块所含交易的元数据信息   |
| receiptsHash         | vector<transactionReceipt> | 交易回执的hash值         |
| nonceList            | vector<string>             | 区块所包含交易的nonce列表  |
| transactionsMerkle   | vector<vector<byte>>       | 交易形成的merkle树        |
| receiptsMerkle       | vector<vector<byte>>       | 交易回执形成的merkle树     |

FISCO BCOS的区块头BlockHeader中每个字段意义如下： 

| name           | type            | description     |
| :------------- |:--------| :--------------------------------------------------------|
| data                 | blockHeaderData      | 记录本区块头的数据             | 
| dataHash             | vector<byte>         | 区块头数据生成的hash           |
| signatureList        | vector<signature>    | 区块中所携带的签名信息列表       |

BlockHeader中结构体blockHeaderData中每个字段意义如下：

| name             | type          | description                                                          | 
| :--------------- | :------------ | :------------------------------------------------------------------- | 
| version          | int           | 记录此区块版本信息                                                        |
| parentInfo       | vector<ParentInfo>          |  记录本区块父块信息                                        | 
| txsRoot          | vector<byte>           | 交易树的根哈希值                                                  |
| receiptsRoot     | vector<byte>          | 收据树的根哈希值                                                  | 
| stateRoot        | vector<byte>         | 状态树的跟的哈希值 |
| blockNumber      | long                 |   区块块高       | 
| gasUsed          | string           | 本区块中所有交易使用的Gas之和    |
| timestamp        | long             | 打包区块的unix时间戳                  |
| sealer           | long            | 打包区块的节点在共识节点列表中的索引，FISCO BCOS新增字段           | 
| sealerList       | vector<vector<byte>> | 区块的共识节点列表（不含观察节点），FISCO BCOS新增字段        |
| extraData        | vector<bytes> |  区块的附加数据，FISCO BCOS目前只用于在第0块中记录群组genesis文件信息 |
| consensusWeights | vector<long>  |  记录本区块经过共识节点的共识情况  |


FISCO BCOS中blockHeaderData中结构体ParentInfo中字段意义如下：

| name            | type          | description                    |
| :---------------| :------------ | :------------------------------| 
| blockNumber       | long          | 父块的块高                      | 
| blockHash         | vector<byte>  | 父块的哈希值                    |


BlockHeader中结构体signature每个字段意义如下

| name            | type          | description                    |
| :---------------| :------------ | :------------------------------| 
| sealerIndex       | long          | 共识节点的索引                      | 
| signature         | vector<byte>  | 共识节点签名信息                | 

区块与交易数据结构如上，此数据结构保证FISCO BCOS具有校验数据完整性功能，区块hash、交易默克尔树根、收据默克尔树根、状态默克尔树根、父块信息等字段，可以有效的验证区块的有效性与完整性，防止数据被篡改。
并且，用户可通过在控制台调用相关接口查看区块信息，校验数据一致性。



