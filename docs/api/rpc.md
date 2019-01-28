# RPC

## 1 名词解释
- **JSON**(JavaScript Object Notation)：一种轻量级的数据交换格式。它可以表示数字、字符串、有序序列和键值对。    
- **JSON RPC**：一种无状态、轻量级的远程过程调用(Remote Procedure Call， RPC)协议。 该规范主要定义了几个数据结构及其处理规则。它允许运行在基于socket，http等诸多不同消息传输环境的同一进程中。它使用JSON ([RFC 4627](http://www.ietf.org/rfc/rfc4627.txt))作为数据格式。FISCO BCOS采用JSON RPC2.0协议。

## 2 模块架构
 ![](../../../images/rpc/rpc.png)
 
 RPC模块负责提供FISCO BCOS的外部接口，客户端通过RPC发送请求，RPC通过调用账本管理模块和p2p模块获取相关响应，并将响应返回给客户端。其中账本管理模块通过多账本机制管理区块链底层的相关模块，具体包括共识模块，同步模块，区块管理模块，交易池模块以及区块验证模块。

## 3 数据定义
### 3.1 请求对象
发送一个请求至区块链节点代表一个RPC调用，一个请求对象包含下列数据成员：   
- jsonrpc: 指定JSON-RPC协议版本的字符串，必须准确写为“2.0”。         
- method: 调用方法的名称。          
- params: 调用方法所需要的参数，方法参数可选。 FISCO BCOS2.0采用多张本机制，第一个参数如果存在，必须为群组ID。           
- id: 已建立客户端的唯一标识id，id必须是一个字符串、数值或NULL空值。如果不包含该成员则被认定为是一个通知。该值一般不为NULL[[1](#id1)]，若为数值则不应该包含小数[[2](#id2)]。     

RPC请求包格式示例:
```
{"jsonrpc": "2.0", "method": "getBlockNumber", "params": [1], "id": 1}
```
**注：**       
- <span id="id1">[1] 在请求对象中不建议使用NULL作为id值，因为该规范将使用空值认定为未知id的请求。另外，由于JSON RPC 1.0 的通知使用了空值，这可能引起处理上的混淆。</span>  
- <span id="id2"> [2] 使用小数具有不确定性，因为许多十进制小数不能精准的表达为二进制小数。 </span>

### 3.2 响应对象
当发起一个RPC调用时，除通知之外，区块链节点都必须回复响应。响应表示为一个JSON对象，使用以下成员：
- jsonrpc: 指定JSON RPC协议版本的字符串，必须准确写为“2.0”。       
- result: 该成员在响应处理成功时必须包含，当调用方法引起错误时必须不包含该成员。       
- error: 该成员在失败是必须包含，当没有引起错误的时必须不包含该成员。该成员参数值必须为[3.3](#33-错误对象)节中定义的对象。     
- id: 该成员必须包含，该成员值必须于请求对象中的id成员值一致，若在检查请求对象id时错误（例如参数错误或无效请求），则该值必须为空值。     

RPC响应包格式示例:
```
{"jsonrpc": "2.0", "result": "0x1", "id": 1}
```
**注：**
响应对象必须包含result或error成员，但两个成员不能同时包含。

### 3.3 错误对象
当一个RPC调用遇到错误时，返回的响应对象必须包含错误成员参数，并且为带有下列成员参数的对象：

- code: 使用数值表示该异常的错误类型，必须为整数。          
- message: 对该错误的简单描述字符串。   
- data: 包含关于错误附加信息的基本类型或结构化类型，该成员可选。        

错误对象分两类，分别是JSON RPC标准错误响应和FISCO BCOS自定义错误响应。
#### 3.3.1 JSON RPC标准错误响应    
    
标准错误列表如下：  


```eval_rst

+--------+------------------------+--------------------------+      
|code    |message                 |含义                      |
+========+========================+==========================+ 
|-32600  |INVALID_JSON_REQUEST    |发送无效的请求对象        |
+--------+------------------------+--------------------------+ 
|-32601  |METHOD_NOT_FOUND        |该方法不存在或无效        |
+--------+------------------------+--------------------------+ 
|-32602  |INVALID_PARAMS          |无效的方法参数            |
+--------+------------------------+--------------------------+ 
|-32603  |INTERNAL ERROR          |内部调用错误              |
+--------+------------------------+--------------------------+ 
|-32604  |PROCEDURE_IS_METHOD     |请求未提供id字段          |
+--------+------------------------+--------------------------+ 
|-32700  |JSON_PARSE_ERROR        |服务端接收到的json无法解析|
+--------+------------------------+--------------------------+ 

```

#### 3.3.2 FISCO BCOS自定义错误响应     
自定义错误列表如下：


```eval_rst

+-----+------------------------------------------------------------------------+-------------------------------------------+   
|code |message                                                                 |含义                                       |
+=====+========================================================================+===========================================+ 
|1    |GroupID does not exist                                                  |GroupID不存在                              |
+-----+------------------------------------------------------------------------+-------------------------------------------+ 
|2    |Response json parse error                                               |JSON RPC获取的json数据解析错误             |
+-----+------------------------------------------------------------------------+-------------------------------------------+ 
|3    |BlockHash does not exist                                                |区块哈希不存在                             |
+-----+------------------------------------------------------------------------+-------------------------------------------+ 
|4    |BlockNumber does not exist                                              |区块高度不存在                             |
+-----+------------------------------------------------------------------------+-------------------------------------------+ 
|5    |TransactionIndex is out of range                                        |交易索引越界                               |
+-----+------------------------------------------------------------------------+-------------------------------------------+ 
|6    |Call needs a 'from' field                                               |call接口需要提供from字段                   |
+-----+------------------------------------------------------------------------+-------------------------------------------+ 
|7    |Only pbft consensus supports the view property                          |getPbftView接口，只有pbft共识机制有view属性|
+-----+------------------------------------------------------------------------+-------------------------------------------+ 
|8    |Invalid System Config                                                   |getSystemConfigByKey接口，查询无效的key    |
+-----+------------------------------------------------------------------------+-------------------------------------------+   
|9    |Don't send requests to this group, the node doesn't belong to the group |非群组内节点发起无效的请求                 |
+-----+------------------------------------------------------------------------+-------------------------------------------+  

```


## 4 RPC接口的设计
FISCO BCOS提供丰富的RPC接口供客户端调用。其中分为两大类，分别是以get开头命名的查询接口（例如getBlockNumber接口）和两个与合约执行相关的接口，分别是call接口和sendRawTransaction接口。其中call接口执行一个请求将不会创建一笔交易，等待区块链共识，而是获取响应立刻返回，例如合约中的查询方法发出的请求将调用call接口。sendRawTransaction接口执行一笔签名的交易，将等待区块链共识才返回响应。

## 5 RPC接口列表
下列接口的示例中采用curl命令，curl是一个利用url语法在命令行下运行的数据传输工具，通过curl命令发送http post请求，可以访问FISCO BCOS的JSON RPC接口。curl命令的url地址设置为节点配置文件[rpc]部分的listen_ip和jsonrpc listen port端口。为了格式化json，可以使用jq工具进行格式化显示。

### getClientVersion
返回节点的版本信息
#### 参数        
无          
#### 返回值          
- `object` - 版本信息，字段如下：
    - `Build Time`: `string` - 编译时间            
    - `Build Type`: `string` - 编译机器环境            
    - `FISCO-BCOS Version`: `string` - FISCO BCOS版本            
    - `Git Branch`: `string` - 版本分支            
    - `Git Commit Hash`: `string` - 版本最新commit哈希            

- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getClientVersion","params":[],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
  "id": 83,
  "jsonrpc": "2.0",
  "result": {
    "Build Time": "20190106 20:49:10",
    "Build Type": "Linux/g++/RelWithDebInfo",
    "FISCO-BCOS Version": "2.0.0",
    "Git Branch": "release-2.0.1",
    "Git Commit Hash": "693a709ddab39965d9c39da0104836cfb4a72054"
  }
}
```

### getBlockNumber
返回节点指定群组内的最新区块高度
#### 参数  
- `groupID`: `unsigned int` - 群组ID                 
#### 返回值               
- `string` - 最新区块高度                
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockNumber","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x1"
}
```

### getPbftView
返回节点所在指定群组内的最新pbft视图
#### 参数         
- `groupID`: `unsigned int` - 群组ID         
#### 返回值         
- `string` - 最新的pbft视图   
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPbftView","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x1a0"
}
```
**注：** FISCO BCOS支持pbft共识和raft共识，当访问的区块链采用raft共识时，该接口返回FISCO BCOS自定义错误响应如下:
```
{
  "error": {
    "code": 7,
    "data": null,
    "message": "Only pbft consensus supports the view property"
  },
  "id": 1,
  "jsonrpc": "2.0"
}
```

### getMinerList
返回指定群组内的记账节点列表
#### 参数          
- `groupID`: `unsigned int` - 群组ID         
#### 返回值          
- `array` - 记账节点nodeID数组         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getMinerList","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1, 
    "jsonrpc": "2.0", 
    "result": [
        "037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772", 
        "0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801", 
        "622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73"
    ]
}
```

### getObserverList
返回指定群组内的观察节点列表
#### 参数          
- `groupID`: `unsigned int` - 群组ID         
#### 返回值          
- `array` - 观察节点nodeID数组       
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getObserverList","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1, 
    "jsonrpc": "2.0", 
    "result": [
        "10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34"
    ]
}
```

### getConsensusStatus
返回指定群组内的共识状态信息          
#### 参数          
- `groupID`: `unsigned int` - 群组ID          
#### 返回值          
- `object` - 共识状态信息。
- 1. 当共识机制为pbft时（pbft详细设计参考[pbft设计文档](../consensus/pbft.md)），字段如下：            
   -  `accountType`: `unsigned int` - 账户类型            
   -  `allowFutureBlocks`: `bool` - 允许未来块标志            
   -  `cfgErr`: `bool` - 配置错误标志            
   -  `connectedNodes`: `unsigned int` - 连接的节点数            
   -  `consensusedBlockNumber`: `unsigned int` - 下一个共识的最新块高            
   -  `currentView`: `unsigned int` - 当前视图            
   -  `groupId`: `unsigned int` - 群组ID            
   -  `highestblockHash`: `string` - 最新块哈希            
   -  `highestblockNumber`: `unsigned int` - 最新区块高度            
   -  `leaderFailed`: `bool` - leader失败标志            
   -  `max_faulty_leader`: `unsigned int` - 最大容错节点数            
   -  `miner.index`: `string` - 节点序号为index的nodeID            
   -  `node index`: `unsigned int` - 节点的序号            
   -  `nodeID`: `string` - 节点的nodeID            
   -  `nodeNum`: `unsigned int` - 节点的数            
   -  `omitEmptyBlock`: `bool` - 忽略空块标志位            
   -  `protocolId`: `unsigned int` - 协议ID号            
   -  `toView`: `unsigned int` - 目前到达的view值            
   -  `prepareCache_blockHash`: `string` - prepareCache哈希            
   -  `prepareCache_height`: `int`- prepareCache高度            
   -  `prepareCache_idx`: `unsigned int` - prepareCache序号            
   -  `prepareCache_view`: `unsigned int` - prepareCache视图            
   -  `rawPrepareCache_blockHash`: `string` - rawPrepareCache哈希            
   -  `rawPrepareCache_height`: `int`- rawPrepareCache高度            
   -  `rawPrepareCache_idx`: `unsigned int` - rawPrepareCache序号            
   -  `rawPrepareCache_view`: `unsigned int` - rawPrepareCache视图            
   -  `committedPrepareCache_blockHash`: `string` - committedPrepareCache哈希            
   -  `committedPrepareCache_height`: `int`- committedPrepareCache高度            
   -  `committedPrepareCache_idx`: `unsigned int` - committedPrepareCache序号            
   -  `committedPrepareCache_view`: `unsigned int` - committedPrepareCache视图            
   -  `futureCache_blockHash`: `string` -futureCache哈希            
   -  `futureCache_height`: `int`- futureCache高度            
   -  `futureCache_idx`: `unsigned int` - futureCache序号            
   -  `signCache_cachedSize`: `unsigned int` - signCache_cached大小            
   -  `commitCache_cachedSize`: `unsigned int` - commitCache_cached大小            
   -  `viewChangeCache_cachedSize`: `unsigned int` - viewChangeCache_cached大小            

- 2. 当共识机制为raft时（raft详细设计参考[raft设计文档](../consensus/raft.md)），字段如下：     
    - `accountType`: `unsigned int` - 账户类型            
    - `allowFutureBlocks`: `bool` - 允许未来块标志            
    - `cfgErr`: `bool` - 配置错误标志                        
    - `consensusedBlockNumber`: `unsigned int` - 下一个共识的最新块高            
    - `groupId`: `unsigned int` - 群组ID            
    - `highestblockHash`: `string` - 最新块哈希            
    - `highestblockNumber`: `unsigned int` - 最新区块高度            
    - `leaderId`: `string` - leader的nodeID            
    - `leaderIdx`: `unsigned int` - leader的序号            
    - `max_faulty_leader`: `unsigned int` - 最大容错节点数            
    - `miner.index`: `string` - 节点序号为index的nodeID            
    - `node index`: `unsigned int` - 节点的index            
    - `nodeID`: `string` - 节点的nodeID            
    - `nodeNum`: `unsigned int` - 节点的数            
    - `omitEmptyBlock`: `bool` - 忽略空块标志位            
    - `protocolId`: `unsigned int` - 协议ID号            

- 示例
```
// Request pbft
curl -X POST --data '{"jsonrpc":"2.0","method":"getConsensusStatus","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        {
            "accountType":1,
            "allowFutureBlocks":true,
            "cfgErr":false,
            "connectedNodes":3,
            "consensusedBlockNumber":4,
            "currentView":153,
            "groupId":1,
            "highestblockHash":"98e186095a88f7b1b4cd02e3c405f031950577626dab55b639e024b9f2f8788b",
            "highestblockNumber":3,
            "leaderFailed":false,
            "max_faulty_leader":1,
            "miner.0":"29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0",
            "miner.1":"41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
            "miner.2":"87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1",
            "miner.3":"d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
            "node index":1,
            "nodeID":"41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
            "nodeNum":4,
            "omitEmptyBlock":true,
            "protocolId":264,
            "toView":153
        },
        {
            "prepareCache_blockHash":"0000000000000000000000000000000000000000000000000000000000000000",
            "prepareCache_height":-1,
            "prepareCache_idx":"65535",
            "prepareCache_view":"9223372036854775807"
        },
        {
            "rawPrepareCache_blockHash":"0000000000000000000000000000000000000000000000000000000000000000",
            "rawPrepareCache_height":-1,
            "rawPrepareCache_idx":"65535",
            "rawPrepareCache_view":"9223372036854775807"
        },
        {
            "committedPrepareCache_blockHash":"2e4c63cfac7726691d1fe436ec05a7c5751dc4150d724822ff6c36a608bb39f2",
            "committedPrepareCache_height":3,
            "committedPrepareCache_idx":"2",
            "committedPrepareCache_view":"60"
        },
        {
            "futureCache_blockHash":"0000000000000000000000000000000000000000000000000000000000000000",
            "futureCache_height":-1,
            "futureCache_idx":"65535",
            "futureCache_view":"9223372036854775807"
        },
        {
            "signCache_cachedSize":"0"
        },
        {
            "commitCache_cachedSize":"0"
        },
        {
            "viewChangeCache_cachedSize":"0"
        }
    ]
}

// Request raft
curl -X POST --data '{"jsonrpc":"2.0","method":"getConsensusStatus","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "accountType": 1,
      "allowFutureBlocks": true,
      "cfgErr": false,
      "consensusedBlockNumber": 1,
      "groupId": 1,
      "highestblockHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
      "highestblockNumber": 0,
      "leaderId": "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
      "leaderIdx": 3,
      "max_faulty_leader": 1,
      "miner.0": "29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0",
      "miner.1": "41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
      "miner.2": "87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1",
      "miner.3": "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
      "node index": 1,
      "nodeID": "41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
      "nodeNum": 4,
      "omitEmptyBlock": true,
      "protocolId": 267
    }
  ]
}
```
### getSyncStatus
返回指定群组内的同步状态信息
#### 参数        
- `groupID`: `unsigned int` - 群组ID          
#### 返回值          
- `object` - 同步状态信息，字段如下：            
    - `blockNumber`: `unsigned int` - 最新区块高度            
    - `genesisHash`: `string` - 创世块哈希            
    - `isSyncing`: `bool` - 正在同步标志            
    - `latestHash`: `string` - 最新区块哈希            
    - `nodeId`: `string` - 节点的nodeID            
    - `protocolId`: `unsigned int` - 协议ID号            
    - `txPoolSize`: `string` - 交易池中交易的数量            
    - `peers`: `array` - 已连接的指定群组内p2p节点，节点信息字段如下: 
        - `blockNumber`: `unsigned int` - 最新区块高度            
        - `genesisHash`: `string` - 创始区块哈希            
        - `latestHash`: `string` - 最新块哈希            
        - `nodeId`: `string` - 节点的nodeID            
       
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSyncStatus","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "blockNumber": 0,
    "genesisHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
    "isSyncing": false,
    "latestHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
    "nodeId": "41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
    "peers": [
      {
        "blockNumber": 0,
        "genesisHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "latestHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "nodeId": "29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0"
      },
      {
        "blockNumber": 0,
        "genesisHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "latestHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "nodeId": "87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1"
      },
      {
        "blockNumber": 0,
        "genesisHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "latestHash": "4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "nodeId": "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8"
      }
    ],
    "protocolId": 265,
    "txPoolSize": "0"
  }
}
```
### getPeers
返回已连接的p2p节点信息         
#### 参数          
- `groupID`: `unsigned int` - 群组ID            
#### 返回值          
- `array` - 已连接的p2p节点信息，字段如下：
    - `IPAndPort`: `string` - 节点连接的ip和端口            
    - `NodeID`: `string` - 节点的nodeID            
    - `Topic`: `array` - 节点关注的topic信息            
      
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
格式化JSON：
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        {
            "IPAndPort": "127.0.0.1:30308",
            "NodeID": "0701cc9f05716690437b78db5b7c9c97c4f8f6dd05794ba4648b42b9267ae07cfcd589447ac36c491e7604242149601d67c415504a838524939ef2230d36ffb8",
            "Topic": [ ]
        },
        {
            "IPAndPort": "127.0.0.1:58348",
            "NodeID": "353ab5990997956f21b75ff5d2f11ab2c6971391c73585963e96fe2769891c4bc5d8b7c3d0d04f50ad6e04c4445c09e09c38139b1c0a5937a5778998732e34da",
            "Topic": [ ]
        },
        {
            "IPAndPort": "127.0.0.1:30300",
            "NodeID": "73aebaea2baa9640df416d0e879d6e0a6859a221dad7c2d34d345d5dc1fe9c4cda0ab79a7a3f921dfc9bdea4a49bb37bdb0910c338dadab2d8b8e001186d33bd",
            "Topic": [ ]
        }
    ]
}
```
### getGroupPeers
返回指定群组内的记账节点和观察节点列表         
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
#### 返回值          
- `array` - 记账节点和观察节点的nodeID数组     
        
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupPeers","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1, 
    "jsonrpc": "2.0", 
    "result": [
        "0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801", 
        "037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772", 
        "622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73", 
        "10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34"
    ]
}
```
### getNodeIDList
返回节点本身和已连接的p2p节点列表
#### 参数          
- `groupID`: `unsigned int` - 群组ID         
#### 返回值          
- `array` - 节点本身和已连接p2p节点的nodeID数组 

- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getNodeIDList","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1, 
    "jsonrpc": "2.0", 
    "result": [
        "0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801", 
        "037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772", 
        "622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73", 
        "10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34"
    ]
}
```
### getGroupList
返回节点所属群组的群组ID列表
#### 参数          
- `groupID`: `unsigned int` - 群组ID          
#### 返回值          
- `array` - 节点所属群组的群组ID数组 

- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupList","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [1]
}
```
### getBlockByHash
返回根据区块哈希查询的区块信息
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockHash`: `string` - 区块哈希       
- `includeTransactions`: `bool` - 包含交易标志(true显示交易详细信息，false仅显示交易的hash)          
#### 返回值          
- `object` - 区块信息，字段如下：
    - `extraData`: `array` - 附加数据      
    - `gasLimit`: `string` - 区块中允许的gas最大值     
    - `gasUsed`: `string` - 区块中所有交易消耗的gas                
    - `hash`: `string` - 区块哈希      
    - `logsBloom`: `string` - log的布隆过滤器值     
    - `number`: `string` - 区块高度               
    - `parentHash`: `string` - 父区块哈希      
    - `sealer`: `string` - 记账节点序号     
    - `stateRoot`: `string` - 状态根哈希              
    - `timestamp`: `string` - 时间戳      
    - `transactions`: `array` - 交易列表，当`includeTransactions`为`false`时，显示交易的哈希。当`includeTransactions`为`true`时，显示交易详细信息（详细字段见[getTransactionByHash](#getTransactionByHash)）
              
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByHash","params":[1,"0x910ea44e2a83618c7cc98456678c9984d94977625e224939b24b3c904794b5ec",true],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "extraData": [],
    "gasLimit": "0x0",
    "gasUsed": "0x0",
    "hash": "0x910ea44e2a83618c7cc98456678c9984d94977625e224939b24b3c904794b5ec",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number": "0x1",
    "parentHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
    "sealer": "0x3",
    "stateRoot": "0xfb7ca5a7a271c8ffb51bc689b78d0aeded23497c9c22e67dff8b1c7b4ec88a2a",
    "timestamp": "0x1687e801d99",
    "transactions": [
      {
        "blockHash": "0x910ea44e2a83618c7cc98456678c9984d94977625e224939b24b3c904794b5ec",
        "blockNumber": "0x1",
        "from": "0xadf06b974703a1c25c621ce53676826198d4b046",
        "gas": "0x1c9c380",
        "gasPrice": "0x1",
        "hash": "0x022dcb1ad2d940ce7b2131750f7458eb8ace879d129ee5b650b84467cb2184d7",
        "input": "0x608060405234801561001057600080fd5b5060016000800160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506402540be40060006001018190555060028060000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060006002600101819055506103bf806100c26000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100515780636d4ce63c1461007e575b600080fd5b34801561005d57600080fd5b5061007c600480360381019080803590602001909291905050506100a9565b005b34801561008a57600080fd5b506100936102e1565b6040518082815260200191505060405180910390f35b8060006001015410806100c757506002600101548160026001015401105b156100d1576102de565b8060006001015403600060010181905550806002600101600082825401925050819055507fc77b710b83d1dc3f3fafeccd08a6c469beb873b2f0975b50d1698e46b3ee5b4c816040518082815260200191505060405180910390a160046080604051908101604052806040805190810160405280600881526020017f323031373034313300000000000000000000000000000000000000000000000081525081526020016000800160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001838152509080600181540180825580915050906001820390600052602060002090600402016000909192909190915060008201518160000190805190602001906102419291906102ee565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b6000600260010154905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061032f57805160ff191683800117855561035d565b8280016001018555821561035d579182015b8281111561035c578251825591602001919060010190610341565b5b50905061036a919061036e565b5090565b61039091905b8082111561038c576000816000905550600101610374565b5090565b905600a165627a7a72305820fb983c66bee66788f407721b23b10a8aae3dc9ef8f1b09e08ec6a6c0b0ec70100029",
        "nonce": "0x1a9d06264238ea69c1bca2a74cfced979d6b6a66ce8ad6f5a30e8017b5a98d8",
        "to": null,
        "transactionIndex": "0x0",
        "value": "0x0"
      }
    ],
    "transactionsRoot": "0x07506c27626365c4f0db788619a96df1e6f8f62c583f158192700e08c10fec6a"
  }
}

// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByHash","params":[1,"0x910ea44e2a83618c7cc98456678c9984d94977625e224939b24b3c904794b5ec",false],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "extraData": [],
    "gasLimit": "0x0",
    "gasUsed": "0x0",
    "hash": "0x910ea44e2a83618c7cc98456678c9984d94977625e224939b24b3c904794b5ec",
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number": "0x1",
    "parentHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
    "sealer": "0x3",
    "stateRoot": "0xfb7ca5a7a271c8ffb51bc689b78d0aeded23497c9c22e67dff8b1c7b4ec88a2a",
    "timestamp": "0x1687e801d99",
    "transactions": [
      "0x022dcb1ad2d940ce7b2131750f7458eb8ace879d129ee5b650b84467cb2184d7"
    ],
    "transactionsRoot": "0x07506c27626365c4f0db788619a96df1e6f8f62c583f158192700e08c10fec6a"
  }
}
```
### getBlockByNumber     
返回根据区块高度查询的区块信息     
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockNumber`: `string` - 区块高度       
- `includeTransactions`: `bool` - 包含交易标志(true显示交易详细信息，false仅显示交易的hash)         
#### 返回值          
见[getBlockByHash](#getBlockByHash)  
  
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByNumber","params":[1,"0x0",true],"id":1}' http://127.0.0.1:30302 |jq
```
Result见[getBlockByHash](#getBlockByHash)  

### getBlockHashByNumber
返回根据区块高度查询的区块哈希          
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockNumber`: `string` - 区块高度                   
#### 返回值          
- `blockHash`: `string` - 区块哈希         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockHashByNumber","params":[1,"0x1"],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa"
}
```
### getTransactionByHash
返回根据交易哈希查询的交易信息
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `transactionHash`: `string` - 交易哈希        
#### 返回值          
- `object`: - 交易信息，其字段如下：  
    - `blockHash`: `string` - 包含该交易的区块哈希      
    - `blockNumber`: `string` - 包含该交易的区块哈希     
    - `from`: `string` - 发送者的地址                
    - `gas`: `string` - 发送者提供的gas     
    - `gasPrice`: `string` - 发送者提供的gas的价格     
    - `hash`: `string` - 交易哈希               
    - `input`: `string` - 交易的输入      
    - `nonce`: `string` - 交易的nonce值     
    - `to`: `string` - 接收者的地址，创建合约交易的该值为null         
    - `transactionIndex`: `string` - 交易的序号
    - `value`: `string` - 转移的值           
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByHash","params":[1,"0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f"],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "blockHash": "0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa",
        "blockNumber": "0x1",
        "from": "0x6bc952a2e4db9c0c86a368d83e9df0c6ab481102",
        "gas": "0x9184e729fff",
        "gasPrice": "0x174876e7ff",
        "hash": "0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f",
        "input": "0x48f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a37",
        "nonce": "0x65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f",
        "to": "0xd6f1a71052366dbae2f7ab2d5d5845e77965cf0d",
        "transactionIndex": "0x0",
        "value": "0x0"
    }
}
```
### getTransactionByBlockHashAndIndex
返回根据区块哈希和交易序号查询的交易信息
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockHash`: `string` - 区块哈希          
- `transactionIndex`: `string` - 交易序号          
#### 返回值          
见[getTransactionByHash](#getTransactionByHash)       
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockHashAndIndex","params":[1,"0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa","0x0"],"id":1}' http://127.0.0.1:30302 |jq
```
Result见[getTransactionByHash](#getTransactionByHash) 

### getTransactionByBlockNumberAndIndex
返回根据区块高度和交易序号查询的交易信息
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockNumber`: `string` - 区块高度          
- `transactionIndex`: `string` - 交易序号          
#### 返回值          
见[getTransactionByHash](#getTransactionByHash)            
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockNumberAndIndex","params":[1,"0x1","0x0"],"id":1}' http://127.0.0.1:30302 |jq
}
```
Result见[getTransactionByHash](#getTransactionByHash)

### getTransactionReceipt
返回根据交易哈希查询的交易回执信息
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `transactionHash`: `string` - 交易哈希          
#### 返回值          
- `object`: - 交易信息，其字段如下：  
    - `blockHash`: `string` - 包含该交易的区块哈希      
    - `blockNumber`: `string` - 包含该交易的区块哈希  
    - `contractAddress`: `string` - 合约地址，如果创建合约，则为"0x0000000000000000000000000000000000000000"     
    - `from`: `string` - 发送者的地址                     
    - `gasUsed`: `string` - 交易消耗的gas     
    - `logs`: `array` - 交易产生的log               
    - `logsBloom`: `string` - log的布隆过滤器值      
    - `status`: `string` - 交易的状态值     
    - `to`: `string` - 接收者的地址，创建合约交易的该值为null
    - `transactionHash`: `string` - 交易哈希          
    - `transactionIndex`: `string` - 交易序号
         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceipt","params":[1,"0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f"],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "blockHash": "0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa",
        "blockNumber": "0x1",
        "contractAddress": "0x0000000000000000000000000000000000000000",
        "from": "0x6bc952a2e4db9c0c86a368d83e9df0c6ab481102",
        "gasUsed": "0x64d8",
        "logs": [ ],
        "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        "status": "0x0",
        "to": "0xd6f1a71052366dbae2f7ab2d5d5845e77965cf0d",
        "transactionHash": "0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f",
        "transactionIndex": "0x0"
    }
}
```
### getPendingTransactions
返回待打包的交易信息
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
#### 返回值          
- `object`: - 带打包的交易信息，其字段如下：
    - `blockHash`: `string` - 包含该交易的区块哈希      
    - `blockNumber`: `string` - 包含该交易的区块哈希  
    - `contractAddress`: `string` - 合约地址，如果创建合约，则为"0x0000000000000000000000000000000000000000"     
    - `from`: `string` - 发送者的地址                     
    - `gas`: `string` - 发送者提供的gas     
    - `gasPrice`: `string` - 发送者提供的gas的价格               
    - `hash`: `string` - 交易哈希      
    - `input`: `string` - 交易的输入     
    - `nonce`: `string` - 交易的nonce值
    - `to`: `string` - 接收者的地址，创建合约交易的该值为null        
    - `value`: `string` - 转移的值         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPendingTransactions","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        [
            {
                "from": "0x6bc952a2e4db9c0c86a368d83e9df0c6ab481102",
                "gas": "0x9184e729fff",
                "gasPrice": "0x174876e7ff",
                "hash": "0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f",
                "input": "0x48f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a37",
                "nonce": "0x65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f",
                "to": "0xd6f1a71052366dbae2f7ab2d5d5845e77965cf0d",
                "value": "0x0"
            }
        ]
    }
}
```

### getPendingTxSize
返回待打包的交易数量
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
#### 返回值          
- `string`: - 待打包的交易数量         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":""getPendingTxSize","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": "0x1"
}
```
### getCode
返回根据合约地址查询的合约数据
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `address`: `string` - 合约地址
#### 返回值          
- `string`: - 合约数据         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getCode","params":[1,"0xa94f5374fce5edbc8e2a8697c15331677e6ebf0b"],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x60606040523415600b57fe5b5b60928061001a6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680636d4ce63c14603a575bfe5b3415604157fe5b6047605d565b6040518082815260200191505060405180910390f35b60004290505b905600a165627a7a723058203d9c292921247163d180a161baa8db840c9da6764cab1d23f1e11a5cff13c7910029"
}
```
### getTotalTransactionCount
返回当前交易总数和区块高度
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
#### 返回值          
- `object`: - 当前交易总数和区块高度信息，其字段如下：
    - `txSum`: `string` - 交易总数      
    - `blockNumber`: `string` - 区块高度          
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTotalTransactionCount","params":[1],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "txSum": "0x1",
        "blockNumber": "0x1"
    }
}
```
### getSystemConfigByKey
返回根据key值查询的value值
#### 参数          
- `groupID`: `unsigned int` - 群组ID       
- `key`: `string` - 支持tx_count_limit和tx_gas_limit     
#### 返回值          
- `string` - value值     
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSystemConfigByKey","params":[1,"tx_count_limit"],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": "1000"
}
```
### call
执行一个可以立即获得结果的请求，无需区块链共识        
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `object`: - 请求信息，其字段如下：
    - `from`: `string` - 发送者的地址  
    - `to`: `string` - 接收者的地址 
    - `value`: `string` - (可选)转移的值 
    - `data`: `string` - (可选)编码的参数，编码规范参考[Ethereum Contract ABI](https://solidity.readthedocs.io/en/develop/abi-spec.html) 

#### 返回值          
- `string` - 执行的结果           
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"call","params":[1,{"from":"0x6bc952a2e4db9c0c86a368d83e9df0c6ab481102","to":"0xd6f1a71052366dbae2f7ab2d5d5845e77965cf0d","value":"0x1","data":"0x3"}],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "currentBlockNumber": "0x1",
        "output": "0x"
    }
}
```
### sendRawTransaction
执行一个签名的交易，需要区块链共识          
#### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `rlp`: `string` - 签名的交易数据
#### 返回值          
- `string` - 交易哈希          
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f8ef9f65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f85174876e7ff8609184e729fff82020394d6f1a71052366dbae2f7ab2d5d5845e77965cf0d80b86448f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a371ca00b2b3fabd8598fefdda4efdb54f626367fc68e1735a8047f0f1c4f840255ca1ea0512500bc29f4cfe18ee1c88683006d73e56c934100b8abf4d2334560e1d2f75e"],"id":1}' http://127.0.0.1:30302 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f"
}

// FISCO BCOS支持国密算法，采用国密算法的区块链请求示例
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f8ef9f65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f85174876e7ff8609184e729fff82020394d6f1a71052366dbae2f7ab2d5d5845e77965cf0d80b86448f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a371ca00b2b3fabd8598fefdda4efdb54f626367fc68e1735a8047f0f1c4f840255ca1ea0512500bc29f4cfe18ee1c88683006d73e56c934100b8abf4d2334560e1d2f75e"],"id":1}' http://127.0.0.1:30302 |jq
```
