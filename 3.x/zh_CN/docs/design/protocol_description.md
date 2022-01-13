# 数据结构的编码协议

标签：``数据结构`` ``编码``

----------

```eval_rst
.. note::
   FISCO BCOS 3.x数据和编码协议的实现位于仓库 `bcos-tars-protocol <https://github.com/FISCO-BCOS/bcos-tars-protocol>`_
```

FISCO BCOS 3.x默认采用[tars](https://newdoc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/base/tars-protocol.md)编码协议，本章主要介绍FISCO BCOS 3.x基础数据结构的编码协议。
## 1. 区块头数据结构

区块头的tars定义可参考[这里](https://github.com/FISCO-BCOS/bcos-tars-protocol/blob/main/bcos-tars-protocol/tars/Block.tars)。

### 1.1 BlockHeaderData

区块头中需要计算哈希的字段：

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
| version  | int |区块头版本号    |
| parentInfo  | vector<ParentInfo> |父区块信息，包括父区块的块高和哈希|
| txsRoot  | vector<byte> |区块内所有交易MerkleRoot的哈希  |
| receiptRoot  | vector<byte> |区块内所有回执MerkleRoot的哈希|
| stateRoot  | vector<byte> |区块内所有交易状态变化对应的根哈希|
| blockNumber  | long |区块高度 |
| gasUsed  | string |区块内所有交易消耗的gas总和|
| timestamp  | long |区块头时间戳|
| sealer  |  long|产生该区块头的共识节点的ID|
| sealerList  | vector<vector<byte>>  |产生该区块头时，系统所有共识节点列表|
| extraData  | vector<byte> |扩展字段, FISCO BCOS目前只用于在第0块中记录群组genesis文件信息|
| consensusWeights  | vector<long> |所有共识节点的权重信息|

### 1.2 BlockHeader

区块头所有字段的定义:

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
|data |BlockHeaderData |区块头用于计算哈希的所有字段编码后对应的数据|
|dataHash |<byte>|区块内所有交易状态变化对应的根哈希|
|signatureList |vector<Signature>  |区块头共识成功后，产生的签名列表|


## 2. 区块数据结构

区块的tars定义可参考[这里](https://github.com/FISCO-BCOS/bcos-tars-protocol/blob/main/bcos-tars-protocol/tars/Block.tars)，区块中既可以包括完整的区块头、交易、回执信息，也可以作为共识模块的Proposal仅包括交易的元数据信息，具体如下:

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
|version |int|可选字段，区块版本号|
|type |int|可选字段，区块类型|
|blockHeader |BlockHeader|可选字段，区块头数据|
|transactions |vector<Transaction>|可选字段，交易列表|
|receipts |vector<TransactionReceipt> |可选字段，交易回执列表|
|transactionsMetaData |vector<TransactionMetaData> |可选字段，交易元数据信息|
|receiptsHash |vector<vector<byte>> |交易回执的哈希列表|
|nonceList |vector<string> |所有交易的nonce列表，在区块同步的场景下，作为区块的payload，向交易池推送所有交易的nonce并触发nonce更新逻辑|

## 3. 交易数据结构

交易的tars定义可参考[这里](https://github.com/FISCO-BCOS/bcos-tars-protocol/blob/main/bcos-tars-protocol/tars/Transaction.tars)，类似于区块头，交易的数据协议字段也划分为用于计算哈希的字段和不参与哈希计算的字段两部分。

### 3.1 TransactionData

定义了交易中用于计算哈希的所有字段，具体如下

|  字段   | 类型  |   说明 |
|  ----  | ----  | ----  |
|version |int |require，交易版本号|
|chainID |string |require，交易对应的链ID |
|groupID |string |require，交易对应的群组ID |
|blockLimit |long |require，交易的blockLimit，用于防止交易重复 |
|nonce |string |require，消息发送方提供的随机数，用于唯一标识交易，也用于防止交易重复 |
|to |string | optional，交易接收方地址 |
|input |vector<byte> | require，与交易相关的数据，包含了交易调用的函数、参数等信息|


### 3.2 Transaction

交易所有字段的定义:

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
| data | TransactionData| optional，交易用于计算哈希的字段编码后的数据|
| dataHash | vector<byte> |optional，交易哈希|
| signature|vector<byte> |optional，交易的签名|
| sender |vector<byte> |optional，发送交易的账户地址|
| importTime |long | optional，交易发送到节点的时间戳|
| attribute |int | optional，交易的属性，用于标记交易的类型、交易的并行冲突域等|
| source |string |optional，交易接收方地址，用于DMC调度|

### 3.3 TransactionMetaData

共识打包的proposal中仅包括交易元数据信息，交易元数据信息字段定义如下：

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
|hash |vector<byte> |optional，交易哈希|
|to |string |optional，交易接收方地址|
|source |string |optional，交易接收方地址，用于DMC调度|
|attribute |unsigned int |optional，交易的属性，用于标记交易的类型、交易的并行冲突域等|

## 4. 交易回执数据结构

交易回执的tars定义可参考[这里](https://github.com/FISCO-BCOS/bcos-tars-protocol/blob/main/bcos-tars-protocol/tars/TransactionReceipt.tars)，类似于区块头和交易，交易回执的数据协议字段也划分为用于计算哈希的字段和不参与哈希计算的字段两部分。

### 4.1 LogEntry

定义event log，具体如下: 

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
|address |string|事件对应的合约地址 |
|topic|vector<vector<byte>>|事件topic|
|data|vector<byte>|eventlog对应的数据|

### 4.2 TransactionReceiptData

交易回执中用于计算哈希的字段：

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
|version|int|require，交易回执版本号|
|gasUsed|string|require，回执对应的交易消耗的Gas|
|contractAddress|string|optional，交易回执对应的合约地址|
|status|int|require，交易执行状态|
|output|vector<byte>|optional，交易执行结果输出|
|logEntries|vector<LogEntry>|optional，交易执行过程中产生的eventlogs|
|blockNumber|long|require，交易回执所在的区块高度|

### 4.3 TransactionReceipt

交易回执的数据结构定义如下:

|  字段   | 类型  |   说明 | 
|  ----  | ----  | ----  |
|data|TransactionReceiptData|交易回执中所有用于计算哈希的字段编码数据|
|dataHash|vector<byte>|交易回执编码|