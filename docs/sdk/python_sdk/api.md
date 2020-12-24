# Python API

标签：``Python API`` ``Client``

----

Python SDK为区块链应用开发者提供了Python API接口，主要包括：

- Python API：封装了访问FISCO BCOS 2.0+节点[JSON-RPC](../../api.md)的Python API
- 交易结构定义：定义了FISCO BCOS 2.0+的交易数据结构
- 交易输入输出解析：提供ABI、Event Log、交易输入和输出解析功能
- ChannelHandler：FISCO BCOS channel协议实现类，支持节点之间SSL加密通信


## Python API：BcosClient
实现于`client/bcosclient.py`，封装了访问FISCO BCOS 2.0+节点[JSON-RPC](../../api.md)的Python API，主要接口包括：

| 接口名        | 描述| 参数 |
| :--| :--| :-- | 
| getNodeVersion | 获取区块链节点版本信息 | 无 |
| getBlockNumber  | 获取最新块高  | 无 | 
| getPbftView  | 获取PBFT视图  | 无 | 
| getSealerList | 获取共识节点列表  | 无 | 
| getObserverList | 获取观察者节点列表  | 无 | 
| getConsensusStatus | 获取区块链节点共识状态 | 无 | 
| getSyncStatus |  获取区块链节点同步状态 | 无 | 
| getPeers |  获取区块链节点的连接信息 | 无 | 
| getGroupPeers | 获取指定群组的共识节点<br>和观察节点列表| 无 |
| getNodeIDList |  获取节点及其连接节点的列表 | 无 | 
| getGroupList |  获取节点所属群组的群组ID列表 |  无 |
| getBlockByHash |  根据区块哈希获取区块信息 | 区块哈希 |
| getBlockByNumber |  根据区块高度获取区块信息 | 区块高度 |
| getBlockHashByNumber | 根据区块高度获取区块哈希 | 区块高度 |
| getTransactionByHash | 根据交易哈希获取交易信息  | 交易哈希|
| getTransactionByBlockHashAndIndex |根据交易所属的区块哈希、<br>交易索引获取交易信息  | 交易所属的区块哈希<br>交易索引 |
| getTransactionByBlockNumberAndIndex | 根据交易所属的区块高度、<br>交易索引获取交易信息 | 交易所属的区块高度<br>交易索引 |
| getTransactionReceipt | 根据交易哈希获取交易回执  | 交易哈希|
| getPendingTransactions | 获取交易池内所有未上链的交易  | 无 |
| getPendingTxSize |  获取交易池内未上链的交易数目 | 无 |
| getCode |  根据合约地址查询的合约数据 | 合约地址 | 
| getTotalTransactionCount | 获取指定群组的上链交易数目  | 无 |
| getSystemConfigByKey |  获取系统配置 | 系统配置关键字<br>如：<br> - tx_count_limit <br> - tx_gas_limit|
| deploy |  部署合约  | 合约binary code|
| call | 调用合约  | 合约地址<br>合约abi<br>调用接口名称<br>参数列表 |
| sendRawTransaction | 发送交易  | 合约地址<br>合约abi<br>接口名<br>参数列表<br>合约binary code  |
| sendRawTransactionGetReceipt |  发送交易<br>并获取交易执行结果 | 合约地址<br>合约abi接口名<br>参数列表<br>合约binary code |


## Precompile Service

### CNS

**类名**
```
client.precompile.cns.cns_service.CnsService
```

**功能接口**
- register_cns：注册合约名到(合约地址，合约版本)的映射到CNS系统表中
- query_cns_by_name：根据合约名查询CNS信息
- query_cns_by_nameAndVersion：根据合约名和合约名查询CNS信息

### 共识

**类名**
```
client.precompile.consensus.consensus_precompile.ConsensusPrecompile
```

**功能接口**

- addSealer：添加共识节点
- addObserver：添加观察者节点
- removeNode：将节点从群组中删除

### 权限控制

**类名**
```
client.precompile.permission.permission_service.PermissionService
```
**功能接口**

- grant: 将指定表的权限授权给用户
- revoke：收回指定用户对指定表的写权限
- list_permission: 显示对指定表有写权限的账户信息

### CRUD

**类名**
```
client.precompile.crud.crud_service.Entry
```
**功能接口**
- create_table：创建用户表
- insert：向用户表插入记录
- update：更新用户表记录
- remove：删除用户表指定记录
- select：查询用户表指定记录
- desc: 查询用户表信息

### 系统配置

**类名**
```
client.precompile.config.config_precompile.ConfigPrecompile
```

**功能接口**
- setValueByKey: 设置系统配置项的值

## 交易结构定义：BcosTransaction
实现于`client/bcostransaction.py`，定义了FISCO BCOS 2.0+的交易数据结构：

| 字段        | 描述    |
| :--   | :--   |
| randomid | 随机数，用于交易防重|
| gasPrice | 默认为30000000|
| gasLimit | 交易消耗gas上限，默认为30000000|
| blockLimit | 交易防重上限，默认为500|
| to | 一般为合约地址|
| value | 默认为0|
| data | 交易数据 |
| fiscoChainId | 链ID，通过配置`client_config.py`加载|
| groupId | 群组ID，通过配置`client_config.py`加载|
| extraData | 附加数据，默认为空字符串|


## 交易输入输出解析：DatatypeParser

提供ABI、Event Log、交易输入和输出解析功能，实现于`client/datatype_parser.py`：

| 接口        | 参数    | 描述 | 
| :--   | :--  |  :-- |
| load_abi_file | abi文件路径 | 从指定路径加载并解析ABI文件<br>建立函数名、selector到函数abi映射列表|
| parse_event_logs | event log| 解析event log |  
| parse_transaction_input | 交易输入| 解析交易输入<br>返回交易调用的接口名、交易参数等|
| parse_receipt_output | 交易调用的接口名<br>交易输出 | 解析交易输出 | 


## ChannelHandler

FISCO BCOS channel协议实现类，支持节点之间SSL加密通信，并可接收节点推送的消息，主要实现于`client/channelhandler.py`，channel协议编解码参考[这里](../../design/protocol_description.html#channelmessage)


## 合约历史查询


- **client/contratnote.py：** 采用ini配置文件格式保存合约的最新地址和历史地址，以便加载（如console命令里可以用(合约名 last)指代某个合约最新部署的地址）

## 日志模块

- **client/clientlogger.py：** logger定义，目前包括客户端日志和统计日志两种
- **client/stattool.py** 一个简单的统计数据收集和打印日志的工具类
