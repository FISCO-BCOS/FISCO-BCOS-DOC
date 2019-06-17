# JSON-RPC API

下列接口的示例中采用[curl](https://curl.haxx.se/)命令，curl是一个利用url语法在命令行下运行的数据传输工具，通过curl命令发送http post请求，可以访问FISCO BCOS的JSON RPC接口。curl命令的url地址设置为节点配置文件`[rpc]`部分的`[listen_ip]`和`[jsonrpc listen port]`端口。为了格式化json，使用[jq](https://stedolan.github.io/jq/)工具进行格式化显示。错误码参考[RPC设计文档](design/rpc.html#json-rpc)。交易回执状态列表[参考这里](./api.html#id51)。

## getClientVersion
返回节点的版本信息
### 参数        
无          
### 返回值          
- `object` - 版本信息，字段如下：
    - `Build Time`: `string` - 编译时间            
    - `Build Type`: `string` - 编译机器环境            
    - `FISCO-BCOS Version`: `string` - FISCO BCOS版本            
    - `Git Branch`: `string` - 版本分支            
    - `Git Commit Hash`: `string` - 版本最新commit哈希            

- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getClientVersion","params":[],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
  "id": 83,
  "jsonrpc": "2.0",
  "result": {
    "Build Time": "20190106 20:49:10",
    "Build Type": "Linux/g++/RelWithDebInfo",
    "FISCO-BCOS Version": "2.0.0",
    "Git Branch": "master",
    "Git Commit Hash": "693a709ddab39965d9c39da0104836cfb4a72054"
  }
}
```

## getBlockNumber
返回节点指定群组内的最新区块高度
### 参数  
- `groupID`: `unsigned int` - 群组ID                 
### 返回值               
- `string` - 最新区块高度(0x开头的十六进制字符串)             
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockNumber","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x1"
}
```

## getPbftView
返回节点所在指定群组内的最新[PBFT视图](design/consensus/pbft.html#view)
### 参数         
- `groupID`: `unsigned int` - 群组ID         
### 返回值         
- `string` - 最新的PBFT视图   
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPbftView","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x1a0"
}
```
**注：** FISCO BCOS支持[PBFT共识](design/consensus/pbft.md)和[Raft共识](design/consensus/raft.md)，当访问的区块链采用Raft共识时，该接口返回FISCO BCOS自定义错误响应如下:
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

## getSealerList
返回指定群组内的共识节点列表
### 参数          
- `groupID`: `unsigned int` - 群组ID         
### 返回值          
- `array` - 共识节点ID列表         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSealerList","params":[1],"id":1}' http://127.0.0.1:8545 |jq

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

## getObserverList
返回指定群组内的观察节点列表
### 参数          
- `groupID`: `unsigned int` - 群组ID         
### 返回值          
- `array` - 观察节点ID列表       
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getObserverList","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1, 
    "jsonrpc": "2.0", 
    "result": [
        "10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34"
    ]
}
```

## getConsensusStatus
返回指定群组内的共识状态信息          
### 参数          
- `groupID`: `unsigned int` - 群组ID          
### 返回值          
- `object` - 共识状态信息。
- 1. 当共识机制为PBFT时（PBFT详细设计参考[PBFT设计文档](design/consensus/pbft.md)），字段如下：            
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
   -  `sealer.index`: `string` - 节点序号为index的nodeId            
   -  `node index`: `unsigned int` - 节点的序号            
   -  `nodeId`: `string` - 节点的ID            
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

- 2. 当共识机制为Raft时（Raft详细设计参考[Raft设计文档](design/consensus/raft.md)），字段如下：     
    - `accountType`: `unsigned int` - 账户类型            
    - `allowFutureBlocks`: `bool` - 允许未来块标志            
    - `cfgErr`: `bool` - 配置错误标志                        
    - `consensusedBlockNumber`: `unsigned int` - 下一个共识的最新块高            
    - `groupId`: `unsigned int` - 群组ID            
    - `highestblockHash`: `string` - 最新块哈希            
    - `highestblockNumber`: `unsigned int` - 最新区块高度            
    - `leaderId`: `string` - leader的nodeId            
    - `leaderIdx`: `unsigned int` - leader的序号            
    - `max_faulty_leader`: `unsigned int` - 最大容错节点数            
    - `sealer.index`: `string` - 节点序号为index的nodeId            
    - `node index`: `unsigned int` - 节点的index            
    - `nodeId`: `string` - 节点的ID            
    - `nodeNum`: `unsigned int` - 节点的数            
    - `omitEmptyBlock`: `bool` - 忽略空块标志位            
    - `protocolId`: `unsigned int` - 协议ID号            

- 示例
```
// Request PBFT
curl -X POST --data '{"jsonrpc":"2.0","method":"getConsensusStatus","params":[1],"id":1}' http://127.0.0.1:8545 |jq

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
            "highestblockHash":"0x98e186095a88f7b1b4cd02e3c405f031950577626dab55b639e024b9f2f8788b",
            "highestblockNumber":3,
            "leaderFailed":false,
            "max_faulty_leader":1,
            "sealer.0":"29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0",
            "sealer.1":"41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
            "sealer.2":"87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1",
            "sealer.3":"d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
            "node index":1,
            "nodeId":"41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
            "nodeNum":4,
            "omitEmptyBlock":true,
            "protocolId":264,
            "toView":153
        },
        {
            "prepareCache_blockHash":"0x0000000000000000000000000000000000000000000000000000000000000000",
            "prepareCache_height":-1,
            "prepareCache_idx":"65535",
            "prepareCache_view":"9223372036854775807"
        },
        {
            "rawPrepareCache_blockHash":"0x0000000000000000000000000000000000000000000000000000000000000000",
            "rawPrepareCache_height":-1,
            "rawPrepareCache_idx":"65535",
            "rawPrepareCache_view":"9223372036854775807"
        },
        {
            "committedPrepareCache_blockHash":"0x2e4c63cfac7726691d1fe436ec05a7c5751dc4150d724822ff6c36a608bb39f2",
            "committedPrepareCache_height":3,
            "committedPrepareCache_idx":"2",
            "committedPrepareCache_view":"60"
        },
        {
            "futureCache_blockHash":"0x0000000000000000000000000000000000000000000000000000000000000000",
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

// Request Raft
curl -X POST --data '{"jsonrpc":"2.0","method":"getConsensusStatus","params":[1],"id":1}' http://127.0.0.1:8545 |jq

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
      "highestblockHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
      "highestblockNumber": 0,
      "leaderId": "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
      "leaderIdx": 3,
      "max_faulty_leader": 1,
      "sealer.0": "29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0",
      "sealer.1": "41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
      "sealer.2": "87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1",
      "sealer.3": "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8",
      "node index": 1,
      "nodeId": "41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
      "nodeNum": 4,
      "omitEmptyBlock": true,
      "protocolId": 267
    }
  ]
}
```
## getSyncStatus
返回指定群组内的同步状态信息
### 参数        
- `groupID`: `unsigned int` - 群组ID          
### 返回值          
- `object` - 同步状态信息，字段如下：            
    - `blockNumber`: `unsigned int` - 最新区块高度            
    - `genesisHash`: `string` - 创世块哈希            
    - `isSyncing`: `bool` - 正在同步标志         
    - `knownHighestNumber`: `unsigned int` - 此节点已知的当前区块链最高块高
    - `knownLatestHash`: `string` - 此节点已知的当前区块链最高块哈希 
    - `latestHash`: `string` - 最新区块哈希            
    - `nodeId`: `string` - 节点的ID            
    - `protocolId`: `unsigned int` - 协议ID号            
    - `txPoolSize`: `string` - 交易池中交易的数量            
    - `peers`: `array` - 已连接的指定群组内p2p节点，节点信息字段如下: 
        - `blockNumber`: `unsigned int` - 最新区块高度            
        - `genesisHash`: `string` - 创始区块哈希            
        - `latestHash`: `string` - 最新块哈希            
        - `nodeId`: `string` - 节点的ID            
    
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSyncStatus","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "blockNumber": 0,
    "genesisHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
    "isSyncing": false,
    "knownHighestNumber":0,
    "knownLatestHash":"0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
    "latestHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
    "nodeId": "41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba",
    "peers": [
      {
        "blockNumber": 0,
        "genesisHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "latestHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "nodeId": "29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0"
      },
      {
        "blockNumber": 0,
        "genesisHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "latestHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "nodeId": "87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1"
      },
      {
        "blockNumber": 0,
        "genesisHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "latestHash": "0x4765a126a9de8d876b87f01119208be507ec28495bef09c1e30a8ab240cf00f2",
        "nodeId": "d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8"
      }
    ],
    "protocolId": 265,
    "txPoolSize": "0"
  }
}
```
## getPeers
返回已连接的p2p节点信息         
### 参数          
- `groupID`: `unsigned int` - 群组ID            
### 返回值          
- `array` - 已连接的p2p节点信息，字段如下：
    - `IPAndPort`: `string` - 节点连接的ip和端口            
    - `nodeId`: `string` - 节点的ID            
    - `Topic`: `array` - 节点关注的topic信息            
    
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
格式化JSON：
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        {
            "IPAndPort": "127.0.0.1:30308",
            "nodeId": "0701cc9f05716690437b78db5b7c9c97c4f8f6dd05794ba4648b42b9267ae07cfcd589447ac36c491e7604242149601d67c415504a838524939ef2230d36ffb8",
            "Topic": [ ]
        },
        {
            "IPAndPort": "127.0.0.1:58348",
            "nodeId": "353ab5990997956f21b75ff5d2f11ab2c6971391c73585963e96fe2769891c4bc5d8b7c3d0d04f50ad6e04c4445c09e09c38139b1c0a5937a5778998732e34da",
            "Topic": [ ]
        },
        {
            "IPAndPort": "127.0.0.1:30300",
            "nodeId": "73aebaea2baa9640df416d0e879d6e0a6859a221dad7c2d34d345d5dc1fe9c4cda0ab79a7a3f921dfc9bdea4a49bb37bdb0910c338dadab2d8b8e001186d33bd",
            "Topic": [ ]
        }
    ]
}
```
## getGroupPeers
返回指定群组内的共识节点和观察节点列表         
### 参数          
- `groupID`: `unsigned int` - 群组ID           
### 返回值          
- `array` - 共识节点和观察节点的ID列表     
  
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq

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
## getNodeIDList
返回节点本身和已连接的p2p节点列表
### 参数          
- `groupID`: `unsigned int` - 群组ID         
### 返回值          
- `array` - 节点本身和已连接p2p节点的ID列表 

- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getNodeIDList","params":[1],"id":1}' http://127.0.0.1:8545 |jq

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
## getGroupList
返回节点所属群组的群组ID列表
### 参数          
无       
### 返回值          
- `array` - 节点所属群组的群组ID列表 

- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupList","params":[],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [1]
}
```
## getBlockByHash
返回根据区块哈希查询的区块信息
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockHash`: `string` - 区块哈希       
- `includeTransactions`: `bool` - 包含交易标志(true显示交易详细信息，false仅显示交易的hash)          
### 返回值          
- `object` - 区块信息，字段如下：
    - `extraData`: `array` - 附加数据      
    - `gasLimit`: `string` - 区块中允许的gas最大值     
    - `gasUsed`: `string` - 区块中所有交易消耗的gas                
    - `hash`: `string` - 区块哈希      
    - `logsBloom`: `string` - log的布隆过滤器值     
    - `number`: `string` - 区块高度               
    - `parentHash`: `string` - 父区块哈希      
    - `sealer`: `string` - 共识节点序号
    - `sealerList`: `array` - 共识节点列表      
    - `stateRoot`: `string` - 状态根哈希              
    - `timestamp`: `string` - 时间戳      
    - `transactions`: `array` - 交易列表，当`includeTransactions`为`false`时，显示交易的哈希。当`includeTransactions`为`true`时，显示交易详细信息（详细字段见[getTransactionByHash](./api.html#gettransactionbyhash)）
    
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByHash","params":[1,"0x910ea44e2a83618c7cc98456678c9984d94977625e224939b24b3c904794b5ec",true],"id":1}' http://127.0.0.1:8545 |jq

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
    "sealerList":[
    "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
    "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
    "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
    "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],
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
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByHash","params":[1,"0x910ea44e2a83618c7cc98456678c9984d94977625e224939b24b3c904794b5ec",false],"id":1}' http://127.0.0.1:8545 |jq

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
    "sealerList":[
    "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
    "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
    "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
    "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],    
    "stateRoot": "0xfb7ca5a7a271c8ffb51bc689b78d0aeded23497c9c22e67dff8b1c7b4ec88a2a",
    "timestamp": "0x1687e801d99",
    "transactions": [
      "0x022dcb1ad2d940ce7b2131750f7458eb8ace879d129ee5b650b84467cb2184d7"
    ],
    "transactionsRoot": "0x07506c27626365c4f0db788619a96df1e6f8f62c583f158192700e08c10fec6a"
  }
}
```
## getBlockByNumber     
返回根据区块高度查询的区块信息     
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockNumber`: `string` - 区块高度(十进制字符串或0x开头的十六进制字符串)       
- `includeTransactions`: `bool` - 包含交易标志(true显示交易详细信息，false仅显示交易的hash)         
### 返回值          
见[getBlockByHash](./api.html#getblockbyhash)  

- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByNumber","params":[1,"0x0",true],"id":1}' http://127.0.0.1:8545 |jq
```
Result见[getBlockByHash](./api.html#getblockbyhash)  

## getBlockHashByNumber
返回根据区块高度查询的区块哈希          
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockNumber`: `string` - 区块高度(十进制字符串或0x开头的十六进制字符串)                  
### 返回值          
- `blockHash`: `string` - 区块哈希         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockHashByNumber","params":[1,"0x1"],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa"
}
```
## getTransactionByHash
返回根据交易哈希查询的交易信息
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `transactionHash`: `string` - 交易哈希        
### 返回值          
- `object`: - 交易信息，其字段如下：  
    - `blockHash`: `string` - 包含该交易的区块哈希      
    - `blockNumber`: `string` - 包含该交易的区块高度     
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
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByHash","params":[1,"0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f"],"id":1}' http://127.0.0.1:8545 |jq

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
## getTransactionByBlockHashAndIndex
返回根据区块哈希和交易序号查询的交易信息
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockHash`: `string` - 区块哈希          
- `transactionIndex`: `string` - 交易序号          
### 返回值          
见[getTransactionByHash](./api.html#gettransactionbyhash)       
- 示例
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockHashAndIndex","params":[1,"0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa","0x0"],"id":1}' http://127.0.0.1:8545 |jq
```
Result见[getTransactionByHash](./api.html#gettransactionbyhash) 

## getTransactionByBlockNumberAndIndex
返回根据区块高度和交易序号查询的交易信息
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `blockNumber`: `string` - 区块高度(十进制字符串或0x开头的十六进制字符串)          
- `transactionIndex`: `string` - 交易序号          
### 返回值          
见[getTransactionByHash](./api.html#gettransactionbyhash)            
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockNumberAndIndex","params":[1,"0x1","0x0"],"id":1}' http://127.0.0.1:8545 |jq
}
```
Result见[getTransactionByHash](./api.html#gettransactionbyhash)

## getTransactionReceipt
返回根据交易哈希查询的交易回执信息
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `transactionHash`: `string` - 交易哈希          
### 返回值          
- `object`: - 交易信息，其字段如下：  
    - `blockHash`: `string` - 包含该交易的区块哈希      
    - `blockNumber`: `string` - 包含该交易的区块哈希  
    - `contractAddress`: `string` - 合约地址，如果创建合约，则为"0x0000000000000000000000000000000000000000"     
    - `from`: `string` - 发送者的地址     
    - `input`: `string` - 交易输入                
    - `gasUsed`: `string` - 交易消耗的gas     
    - `logs`: `array` - 交易产生的log               
    - `logsBloom`: `string` - log的布隆过滤器值      
    - `status`: `string` - 交易的状态值，参考：[交易回执状态](./api.html#id52)    
    - `to`: `string` - 接收者的地址，创建合约交易的该值为null
    - `transactionHash`: `string` - 交易哈希          
    - `transactionIndex`: `string` - 交易序号
    
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceipt","params":[1,"0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f"],"id":1}' http://127.0.0.1:8545 |jq

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
## getPendingTransactions
返回待打包的交易信息
### 参数          
- `groupID`: `unsigned int` - 群组ID           
### 返回值          
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
curl -X POST --data '{"jsonrpc":"2.0","method":"getPendingTransactions","params":[1],"id":1}' http://127.0.0.1:8545 |jq

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

## getPendingTxSize
返回待打包的交易数量
### 参数          
- `groupID`: `unsigned int` - 群组ID           
### 返回值          
- `string`: - 待打包的交易数量         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPendingTxSize","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": "0x1"
}
```
## getCode
返回根据合约地址查询的合约数据
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `address`: `string` - 合约地址
### 返回值          
- `string`: - 合约数据         
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getCode","params":[1,"0xa94f5374fce5edbc8e2a8697c15331677e6ebf0b"],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x60606040523415600b57fe5b5b60928061001a6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680636d4ce63c14603a575bfe5b3415604157fe5b6047605d565b6040518082815260200191505060405180910390f35b60004290505b905600a165627a7a723058203d9c292921247163d180a161baa8db840c9da6764cab1d23f1e11a5cff13c7910029"
}
```
## getTotalTransactionCount
返回当前交易总数和区块高度
### 参数          
- `groupID`: `unsigned int` - 群组ID           
### 返回值          
- `object`: - 当前交易总数和区块高度信息，其字段如下：
    - `txSum`: `string` - 交易总数      
    - `blockNumber`: `string` - 区块高度          
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTotalTransactionCount","params":[1],"id":1}' http://127.0.0.1:8545 |jq

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
## getSystemConfigByKey
返回根据key值查询的value值
### 参数          
- `groupID`: `unsigned int` - 群组ID       
- `key`: `string` - 支持tx_count_limit和tx_gas_limit     
### 返回值          
- `string` - value值     
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSystemConfigByKey","params":[1,"tx_count_limit"],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": "1000"
}
```
## call
执行一个可以立即获得结果的请求，无需区块链共识        
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `object`: - 请求信息，其字段如下：
    - `from`: `string` - 发送者的地址  
    - `to`: `string` - 接收者的地址 
    - `value`: `string` - (可选)转移的值 
    - `data`: `string` - (可选)编码的参数，编码规范参考[Ethereum Contract ABI](https://solidity.readthedocs.io/en/develop/abi-spec.html) 

### 返回值          
- `string` - 执行的结果           
- 示例          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"call","params":[1,{"from":"0x6bc952a2e4db9c0c86a368d83e9df0c6ab481102","to":"0xd6f1a71052366dbae2f7ab2d5d5845e77965cf0d","value":"0x1","data":"0x3"}],"id":1}' http://127.0.0.1:8545 |jq

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
## sendRawTransaction
执行一个签名的交易，需要区块链共识          
### 参数          
- `groupID`: `unsigned int` - 群组ID           
- `rlp`: `string` - 签名的交易数据
### 返回值          
- `string` - 交易哈希          
- 示例
```
// RC1 Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f8ef9f65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f85174876e7ff8609184e729fff82020394d6f1a71052366dbae2f7ab2d5d5845e77965cf0d80b86448f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a371ca00b2b3fabd8598fefdda4efdb54f626367fc68e1735a8047f0f1c4f840255ca1ea0512500bc29f4cfe18ee1c88683006d73e56c934100b8abf4d2334560e1d2f75e"],"id":1}' http://127.0.0.1:8545 |jq

// RC1 Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x7536cf1286b5ce6c110cd4fea5c891467884240c9af366d678eb4191e1c31c6f"
}

// RC2 Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f8d3a003922ee720bb7445e3a914d8ab8f507d1a647296d563100e49548d83fd98865c8411e1a3008411e1a3008201f894d6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f80a466c9913900000000000000000000000000000000000000000000000000000000000000040101a466c9913900000000000000000000000000000000000000000000000000000000000000041ba08e0d3fae10412c584c977721aeda88df932b2a019f084feda1e0a42d199ea979a016c387f79eb85078be5db40abe1670b8b480a12c7eab719bedee212b7972f775"],"id":1}' http://127.0.0.1:8545 |jq 

// RC2 Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x0accad4228274b0d78939f48149767883a6e99c95941baa950156e926f1c96ba"
}

// FISCO BCOS支持国密算法，采用国密算法的区块链请求示例
// RC1 Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f8ef9f65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f85174876e7ff8609184e729fff82020394d6f1a71052366dbae2f7ab2d5d5845e77965cf0d80b86448f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a371ca00b2b3fabd8598fefdda4efdb54f626367fc68e1735a8047f0f1c4f840255ca1ea0512500bc29f4cfe18ee1c88683006d73e56c934100b8abf4d2334560e1d2f75e"],"id":1}' http://127.0.0.1:8545 |jq
// RC2 Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f90114a003eebc46c9c0e3b84799097c5a6ccd6657a9295c11270407707366d0750fcd598411e1a30084b2d05e008201f594bab78cea98af2320ad4ee81bba8a7473e0c8c48d80a48fff0fc400000000000000000000000000000000000000000000000000000000000000040101a48fff0fc40000000000000000000000000000000000000000000000000000000000000004b8408234c544a9f3ce3b401a92cc7175602ce2a1e29b1ec135381c7d2a9e8f78f3edc9c06ee55252857c9a4560cb39e9d70d40f4331cace4d2b3121b967fa7a829f0a00f16d87c5065ad5c3b110ef0b97fe9a67b62443cb8ddde60d4e001a64429dc6ea03d2569e0449e9a900c236541afb9d8a8d5e1a36844439c7076f6e75ed624256f"],"id":1}' http://127.0.0.1:8545 |jq 
```

## 错误码描述

### RPC 错误码

当一个RPC调用遇到错误时，返回的响应对象必须包含error错误结果字段，该字段有下列成员参数：

- code: 使用数值表示该异常的错误类型，必须为整数。          
- message: 对该错误的简单描述字符串。   
- data: 包含关于错误附加信息的基本类型或结构化类型，该成员可选。        

错误对象包含两类错误码，分别是JSON-RPC标准错误码和FISCO BCOS RPC错误码。

#### JSON-RPC标准错误码

标准错误码及其对应的含义如下：  

| code   | message              | 含义                       |
| :----- | :------------------- | :------------------------- |
| -32600 | INVALID_JSON_REQUEST | 发送无效的请求对象         |
| -32601 | METHOD_NOT_FOUND     | 该方法不存在或无效         |
| -32602 | INVALID_PARAMS       | 无效的方法参数             |
| -32603 | INTERNAL ERROR       | 内部调用错误               |
| -32604 | PROCEDURE_IS_METHOD  | 内部错误，请求未提供id字段 |
| -32700 | JSON_PARSE_ERROR     | 服务端接收到的json无法解析 |

#### FISCO BCOS RPC错误码

FISCO BCOS RPC接口错误码及其对应的含义如下：

| code  | message                                                                     | 含义                                        |
| :---- | :-------------------------------------------------------------------------- | :------------------------------------------ |
| -40001 | GroupID does not exist                                                      | GroupID不存在                               |
| -40002 | Response json parse error                                                   | JSON RPC获取的json数据解析错误              |
| -40003 | BlockHash does not exist                                                    | 区块哈希不存在                              |
| -40004 | BlockNumber does not exist                                                  | 区块高度不存在                              |
| -40005 | TransactionIndex is out of range                                            | 交易索引越界                                |
| -40006 | Call needs a 'from' field                                                   | call接口需要提供from字段                    |
| -40007 | Only pbft consensus supports the view property                              | getPbftView接口，只有pbft共识机制有view属性 |
| -40008 | Invalid System Config                                                       | getSystemConfigByKey接口，查询无效的key     |
| -40009 | Don't send requests to this group, <br>the node doesn't belong to the group | 非群组内节点发起无效的请求                  |


### 交易回执状态

| status(十进制/十六进制) | message                    | 含义                   |
| :---------------------- | :------------------------- | :--------------------- |
| 0(0x0)                  | None                       | 正常                   |
| 1(0x1)                  | Unknown                    | 未知异常               |
| 2(0x2)                  | BadRLP                     | 无效RLP异常            |
| 3(0x3)                  | InvalidFormat              | 无效格式异常           |
| 4(0x4)                  | OutOfGasIntrinsic          | gas不足异常            |
| 5(0x5)                  | InvalidSignature           | 无效的签名异常         |
| 6(0x6)                  | InvalidNonce               | 无效nonce异常          |
| 7(0x7)                  | NotEnoughCash              | cash不足异常           |
| 8(0x8)                  | OutOfGasBase               | gas不足异常            |
| 9(0x9)                  | BlockGasLimitReached       | GasLimit异常           |
| 10(0xa)                 | BadInstruction             | 错误指令异常           |
| 11(0xb)                 | BadJumpDestination         | 错误目的跳转异常       |
| 12(0xc)                 | OutOfGas                   | gas不足异常            |
| 13(0xd)                 | OutOfStack                 | 栈溢出异常             |
| 14(0xe)                 | StackUnderflow             | 栈下限溢位异常         |
| 15(0xf)                 | NonceCheckFail             | nonce检测失败异常      |
| 16(0x10)                | BlockLimitCheckFail        | blocklimit检测失败异常 |
| 17(0x11)                | FilterCheckFail            | filter检测失败异常     |
| 18(0x12)                | NoDeployPermission         | 非法部署合约异常       |
| 19(0x13)                | NoCallPermission           | 非法call合约异常       |
| 20(0x14)                | NoTxPermission             | 非法交易异常           |
| 21(0x15)                | PrecompiledError           | precompiled错误异常    |
| 22(0x16)                | RevertInstruction          | revert指令异常         |
| 23(0x17)                | InvalidZeroSignatureFormat | 无效签名格式异常       |
| 24(0x18)                | AddressAlreadyUsed         | 地址占用异常           |
| 25(0x19)                | PermissionDenied           | 无权限异常             |
| 26(0x1a)                | CallAddressError           | 被调用的合约地址不存在 |

### Precompiled Service API 错误码

| 错误码 | 消息内容                                          | 备注      |
| :----- | :----------------------------------------------  | :-----   |
| 0      | success                                          |          |
| -50000  | permission denied                               |          |
| -50001  | table name already exist                        |          |
| -50100  | unknow function call                            |          |
| -50101  | table does not exist                            |          |
| -51000  | table name and address already exist            |          |
| -51001  | table name and address does not exist           |          |
| -51100  | invalid node ID                                 | SDK错误码 |
| -51101  | the last sealer cannot be removed               |           |
| -51102  | the node is not reachable                       | SDK错误码 |
| -51103  | the node is not a group peer                    | SDK错误码 |
| -51104  | the node is already in the sealer list          | SDK错误码 |
| -51105  | the node is already in the observer list        | SDK错误码 |
| -51200  | contract name and version already exist         | SDK错误码 |
| -51201  | version string length exceeds the maximum limit | SDK错误码 |
| -51300  | invalid configuration entry                     |          |
| -51500  | contract name and version already exist         |          |
| -51501  | condition parse error                           |          |
| -51502  | condition operation undefined                   |          |
