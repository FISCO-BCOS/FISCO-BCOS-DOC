# JSON-RPC API

The following examples in this chapter adopt [curl](https://curl.haxx.se/) command, which is a data transfer tool run under command line in url language. JSON-RPC API of FISCO BCOS can be accessed by sending http post request through curl command. The url address of curl command is set as `[listen_ip]` and `[jsonrpc listen port]` of `[rpc]` in node config file. To format json, [jq](https://stedolan.github.io/jq/) is used as a formatter. For the error codes, please check [RPC Design Documentation](design/rpc.html#json-rpc). For transaction return list, please check [here](./api.html#id51).

## getClientVersion
return version information of node
### Parameter        
no          
### Return value          
- `object` - version information, fields:
    - `Build Time`: `string` - compile time            
    - `Build Type`: `string` - compile machine environment           
    - `FISCO-BCOS Version`: `string` - FISCO BCOS version            
    - `Git Branch`: `string` - version branch            
    - `Git Commit Hash`: `string` - the newest commit hash of version           

- Example          
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
return the newest block number of specific group
### Parameter  
- `groupID`: `unsigned int` - group ID                
### Return value              
- `string` - the highest block number (hexadecimal string start with 0x)            
- Example
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
return the newest [PBFT View](design/consensus/pbft.html#view) of the specific group
### Parameter         
- `groupID`: `unsigned int` - group ID         
### Return value         
- `string` - the newest PBFT view
- Example         
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
**Note:** FISCO BCOS supports [PBFT Consensus](design/consensus/pbft.md) and [Raft Consensus](design/consensus/raft.md). When the blockchain adopts Raft consensus, the custom error returned by the API is:
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
return the consensus nodes list of the specific group
### Parameter          
- `groupID`: `unsigned int` - group ID         
### Return value         
- `array` - consensus node ID list         
- Example          
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
return observer node list of the specific group
### Parameter          
- `groupID`: `unsigned int` - group ID         
### Return value          
- `array` - observer node ID list      
- Example          
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
return consensus status information of the specific group         
### Parameter          
- `groupID`: `unsigned int` - group ID          
### Return value          
- `object` - consensus status information.
- 1. When PBFT consensus mechanism is used,（PBFT design is introduced in [PBFT Design Documentation](design/consensus/pbft.md)), the fields are:           
   -  `accountType`: `unsigned int` - account type            
   -  `allowFutureBlocks`: `bool` - allow future blocks           
   -  `cfgErr`: `bool` - configure errors            
   -  `connectedNodes`: `unsigned int` - connected nodes            
   -  `consensusedBlockNumber`: `unsigned int` - the newest consensus block number            
   -  `currentView`: `unsigned int` - the current view            
   -  `groupId`: `unsigned int` - group ID            
   -  `highestblockHash`: `string` - the hash of the highest block            
   -  `highestblockNumber`: `unsigned int` - the highest block number            
   -  `leaderFailed`: `bool` - leader failed            
   -  `max_faulty_leader`: `unsigned int` - the max number of faulty nodes            
   -  `sealer.index`: `string` - node ID with sequence number "index"            
   -  `node index`: `unsigned int` - sequence number of node            
   -  `nodeId`: `string` - node ID            
   -  `nodeNum`: `unsigned int` - number of nodes            
   -  `omitEmptyBlock`: `bool` - omit empty block            
   -  `protocolId`: `unsigned int` - protocol ID           
   -  `toView`: `unsigned int` - current view value            
   -  `prepareCache_blockHash`: `string` - prepareCache hash           
   -  `prepareCache_height`: `int`- prepareCache height            
   -  `prepareCache_idx`: `unsigned int` - prepareCache sequence number            
   -  `prepareCache_view`: `unsigned int` - prepareCache view            
   -  `rawPrepareCache_blockHash`: `string` - rawPrepareCache hash            
   -  `rawPrepareCache_height`: `int`- rawPrepareCache height           
   -  `rawPrepareCache_idx`: `unsigned int` - rawPrepareCache sequence number            
   -  `rawPrepareCache_view`: `unsigned int` - rawPrepareCache view            
   -  `committedPrepareCache_blockHash`: `string` - committedPrepareCache hash           
   -  `committedPrepareCache_height`: `int`- committedPrepareCache height            
   -  `committedPrepareCache_idx`: `unsigned int` - committedPrepareCache sequence number           
   -  `committedPrepareCache_view`: `unsigned int` - committedPrepareCache view            
   -  `futureCache_blockHash`: `string` -futureCache hash           
   -  `futureCache_height`: `int`- futureCache height            
   -  `futureCache_idx`: `unsigned int` - futureCache sequence number            
   -  `signCache_cachedSize`: `unsigned int` - signCache_cached size           
   -  `commitCache_cachedSize`: `unsigned int` - commitCache_cached size            
   -  `viewChangeCache_cachedSize`: `unsigned int` - viewChangeCache_cached size            

- 2. When Raft consensus mechanism is adopted (Raft design is introduced in [Raft Design Documentation](design/consensus/raft.md)), the fields are:     
    - `accountType`: `unsigned int` - account type            
    - `allowFutureBlocks`: `bool` - allow future blocks            
    - `cfgErr`: `bool` - configure error                        
    - `consensusedBlockNumber`: `unsigned int` - the newest consensus block number            
    - `groupId`: `unsigned int` - group ID            
    - `highestblockHash`: `string` - hash of the newest block            
    - `highestblockNumber`: `unsigned int` - the highest block number           
    - `leaderId`: `string` - leader node ID            
    - `leaderIdx`: `unsigned int` - leader node sequence number         
    - `max_faulty_leader`: `unsigned int` - the max number of faulty nodes           
    - `sealer.index`: `string` - node ID with sequence number "index"           
    - `node index`: `unsigned int` - index of node            
    - `nodeId`: `string` - node ID            
    - `nodeNum`: `unsigned int` - number of nodes            
    - `omitEmptyBlock`: `bool` - omit empty block            
    - `protocolId`: `unsigned int` - protocol ID            

- Example
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
return the consensus status information of the specific group
### Parameter        
- `groupID`: `unsigned int` - group ID          
### Return value          
- `object` - consensus status information, fields are:            
    - `blockNumber`: `unsigned int` - the highest block number            
    - `genesisHash`: `string` - hash of genesis block            
    - `isSyncing`: `bool` - syncing
    - `knownHighestNumber`: `unsigned int` - the highest number of the blockchain known by the node
    - `knownLatestHash`: `string` - the latest hash of the blockchain known by the node
    - `latestHash`: `string` - hash of the newest block            
    - `nodeId`: `string` - node ID            
    - `protocolId`: `unsigned int` - protocol ID            
    - `txPoolSize`: `string` - transaction volume in txPool            
    - `peers`: `array` - connected p2p nodes in the specific group, fields of node information are:
        - `blockNumber`: `unsigned int` - the newest block number            
        - `genesisHash`: `string` - hash of genesis block            
        - `latestHash`: `string` - hash of the newest block            
        - `nodeId`: `string` - node ID           

- Example
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
return connected p2p node information        
### Parameter          
- `groupID`: `unsigned int` - group ID            
### Return value         
- `array` - connected p2p node information, fields are:
    - `IPAndPort`: `string` - IP and port of connected node            
    - `nodeId`: `string` - node ID            
    - `Topic`: `array` - Topic information followed by node            

- Example          
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
return consensus node and observer node list in specific group         
### Parameter          
- `groupID`: `unsigned int` - group ID           
### Return value          
- `array` - consensus node and observer node ID list     

- Example          
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
return node and connected p2p node list
### Parameter          
- `groupID`: `unsigned int` -  group ID         
### Return value          
- `array` - node and connected p2p node ID list

- Example         
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
return belonged group ID list
### Parameter          
no       
### Return value          
- `array` - group ID list

- Example         
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
return block information inquired by block hash
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockHash`: `string` - block hash       
- `includeTransactions`: `bool` - include transactions ("true" shows transaction details; "false" shows the hash of transaction only)
### Return value         
- `object` - block information, fields are:
    - `extraData`: `array` - extra data      
    - `gasLimit`: `string` - the maximum gas allowed in block     
    - `gasUsed`: `string` - gas used by all transactions                
    - `hash`: `string` - block hash      
    - `logsBloom`: `string` - bloom filter value of log    
    - `number`: `string` - block height               
    - `parentHash`: `string` - hash of parent block      
    - `sealer`: `string` - consensus node sequence number
    - `sealerList`: `array` - consensus nodes list      
    - `stateRoot`: `string` - hash of state root              
    - `timestamp`: `string` - time stamp     
    - `transactions`: `array` - transaction list, when `includeTransactions` is `false`, it shows the hash of transaction; when `includeTransactions` is `true`, it shows transaction details (detail fields please check [getTransactionByHash](./api.html#gettransactionbyhash))

- Example
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
return block information inquired by block number     
### Parameter          
- `groupID`: `unsigned int` - group ID           
- `blockNumber`: `string` - block number (hexadecimal string starts with 0x)       
- `includeTransactions`: `bool` - include transactions ("true" shows transaction details; "false" shows the hash of transaction only)         
### Return value          
please check [getBlockByHash](./api.html#getblockbyhash)  

- Example          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByNumber","params":[1,"0x0",true],"id":1}' http://127.0.0.1:8545 |jq
```
See the result in [getBlockByHash](./api.html#getblockbyhash)  

## getBlockHashByNumber
return block hash inquired by block number          
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockNumber`: `string` - block number (hexadecimal string starts with 0x)                  
### Return value          
- `blockHash`: `string` - hash of block         
- Example          
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
return transaction information inquired by transaction hash
### Parameters         
- `groupID`: `unsigned int` - group ID           
- `transactionHash`: `string` - transaction hash        
### Return value          
- `object`: - transaction information, fields are:  
    - `blockHash`: `string` - include block hash of this transaction      
    - `blockNumber`: `string` - include block number of this transaction    
    - `from`: `string` - address of sender               
    - `gas`: `string` - gas provided by sender    
    - `gasPrice`: `string` - price of gas from sender     
    - `hash`: `string` - transaction hash               
    - `input`: `string` - transaction input      
    - `nonce`: `string` - nonce value of transaction     
    - `to`: `string` - address of receiver, return null for transactions of creating contract         
    - `transactionIndex`: `string` - transaction sequence number
    - `value`: `string` - transfer value          
- Example          
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
return transaction information inquired by blockhash and transaction sequence number
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockHash`: `string` - block hash          
- `transactionIndex`: `string` - transaction sequence number          
### Return value          
please see [getTransactionByHash](./api.html#gettransactionbyhash)       
- Example
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockHashAndIndex","params":[1,"0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa","0x0"],"id":1}' http://127.0.0.1:8545 |jq
```
see result in [getTransactionByHash](./api.html#gettransactionbyhash)

## getTransactionByBlockNumberAndIndex
return transaction information inquired by block number and transaction sequence number
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockNumber`: `string` - block number (hexadecimal string starts with 0x)          
- `transactionIndex`: `string` - transaction sequence number          
### Return value          
please see [getTransactionByHash](./api.html#gettransactionbyhash)            
- Example          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockNumberAndIndex","params":[1,"0x1","0x0"],"id":1}' http://127.0.0.1:8545 |jq
}
```
see result in [getTransactionByHash](./api.html#gettransactionbyhash)

## getTransactionReceipt
return transaction receipt inquired by transaction hash
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `transactionHash`: `string` - transaction hash          
### Return value          
- `object`: - transaction information, fields are:  
    - `blockHash`: `string` - include block hash of this transaction      
    - `blockNumber`: `string` - include block hash of this transaction  
    - `contractAddress`: `string` - contract address, for creating contract, return "0x0000000000000000000000000000000000000000"     
    - `from`: `string` - address of sender                     
    - `gasUsed`: `string` - gas used by transaction     
    - `logs`: `array` - log created by transaction               
    - `logsBloom`: `string` - bloom filter value of log      
    - `status`: `string` - status value of transaction     
    - `to`: `string` - address of receiver, for creating contract, return null
    - `transactionHash`: `string` - transaction hash          
    - `transactionIndex`: `string` - transaction sequence number

- Example          
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
return pending transaction information
### Parameter         
- `groupID`: `unsigned int` - group ID           
### Return value          
- `object`: - pending transaction information, fields are:
    - `blockHash`: `string` - include block hash of this transaction     
    - `blockNumber`: `string` - include block number of this transaction  
    - `contractAddress`: `string` - contract address. If creating contract, return "0x0000000000000000000000000000000000000000"     
    - `from`: `string` - address of sender                     
    - `gas`: `string` - gas provided by sender   
    - `gasPrice`: `string` - price of gas from sender               
    - `hash`: `string` - transaction hash      
    - `input`: `string` - transaction input     
    - `nonce`: `string` - nonce value of transaction
    - `to`: `string` - address of receiver, for transactions of creating contract return null        
    - `value`: `string` - transfer value         
- Example          
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
return number of pending transactions
### Parameter         
- `groupID`: `unsigned int` - group ID           
### Return value         
- `string`: - number of pending transactions         
- Example          
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
return contract data inquired by contract address
### Parameter          
- `groupID`: `unsigned int` - group ID           
- `address`: `string` - contract address
### Return value          
- `string`: - contract data         
- Example          
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
return current transaction total and block number
### Parameter          
- `groupID`: `unsigned int` - group ID           
### Return value          
- `object`: - current transaction total and block number. fields are:
    - `txSum`: `string` - transaction total      
    - `blockNumber`: `string` - block number          
- Example          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTotalTransactionCount","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
      "blockNumber": "0x1",
      "failedTxSum": "0x0",
      "txSum": "0x1"
    }
}
```
## getSystemConfigByKey
return value inquired by key value
### Parameters          
- `groupID`: `unsigned int` - group ID       
- `key`: `string` - support tx_count_limit and tx_gas_limit     
### Return value          
- `string` - value     
- Example          
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
execute a request that can be returned with result instantly regardless of consensus        
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `object`: - request information, fields are:
    - `from`: `string` - address of sender  
    - `to`: `string` - address of receiver
    - `value`: `string` - (optional) transfer value
    - `data`: `string` - (optional) code parameter. You can read the coding convention in [Ethereum Contract ABI](https://solidity.readthedocs.io/en/develop/abi-spec.html)

### Return value          
- `string` - executed result           
- Example          
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
execute a transaction of signing, need consensus         
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `rlp`: `string` - transaction data of signing
### Return value          
- `string` - hash of transaction          
- Example
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

// FISCO BCOS supports OSCCA algorithm, request of OSCCA is exemplified here
// RC1 Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f8ef9f65f0d06e39dc3c08e32ac10a5070858962bc6c0f5760baca823f2d5582d03f85174876e7ff8609184e729fff82020394d6f1a71052366dbae2f7ab2d5d5845e77965cf0d80b86448f85bce000000000000000000000000000000000000000000000000000000000000001bf5bd8a9e7ba8b936ea704292ff4aaa5797bf671fdc8526dcd159f23c1f5a05f44e9fa862834dc7cb4541558f2b4961dc39eaaf0af7f7395028658d0e01b86a371ca00b2b3fabd8598fefdda4efdb54f626367fc68e1735a8047f0f1c4f840255ca1ea0512500bc29f4cfe18ee1c88683006d73e56c934100b8abf4d2334560e1d2f75e"],"id":1}' http://127.0.0.1:8545 |jq
// RC2 Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendRawTransaction","params":[1,"f90114a003eebc46c9c0e3b84799097c5a6ccd6657a9295c11270407707366d0750fcd598411e1a30084b2d05e008201f594bab78cea98af2320ad4ee81bba8a7473e0c8c48d80a48fff0fc400000000000000000000000000000000000000000000000000000000000000040101a48fff0fc40000000000000000000000000000000000000000000000000000000000000004b8408234c544a9f3ce3b401a92cc7175602ce2a1e29b1ec135381c7d2a9e8f78f3edc9c06ee55252857c9a4560cb39e9d70d40f4331cace4d2b3121b967fa7a829f0a00f16d87c5065ad5c3b110ef0b97fe9a67b62443cb8ddde60d4e001a64429dc6ea03d2569e0449e9a900c236541afb9d8a8d5e1a36844439c7076f6e75ed624256f"],"id":1}' http://127.0.0.1:8545 |jq
```

## Error codes

### RPC error code

When error occurs in a RPC call, the returned response object should contain error result field, which includes following member parameters:

- code: to show error type in integer       
- message: to briefly describe the error in string   
- data: contains the basic type and structured type of additional information of the error; optional parameter

There are 2 types of error code for error objects: JSON-RPC standard error code and FISCO BCOS RPC error code.

#### JSON-RPC standard error code

Standard error codes and their definitions:

| code   | message              | definition                       |
| :----- | :------------------- | :------------------------- |
| -32600 | INVALID_JSON_REQUEST | send invalid request object         |
| -32601 | METHOD_NOT_FOUND     | method not exist or valid         |
| -32602 | INVALID_PARAMS       | invalid method parameter             |
| -32603 | INTERNAL ERROR       | internal call error               |
| -32604 | PROCEDURE_IS_METHOD  | internal error; ID field not provided in the request |
| -32700 | JSON_PARSE_ERROR     | json received by server fails to be parsed |

#### FISCO BCOS RPC error code

FISCO BCOS RPC error codes and their definitions:

| code  | message                                                      | definition                                        |
| :---- | :----------------------------------------------------------- | :------------------------------------------ |
| -40001 | GroupID does not exist                                       | GroupID does not exist                               |
| -40002 | Response json parse error                                    | json acquired by JSON RPC parses error             |
| -40003 | BlockHash does not exist                                     | block hash doesn't exist                              |
| -40004 | BlockNumber does not exist                                   | block number doesn't exist     |
| -40005 | TransactionIndex is out of range                             | transaction index is out of range                           |
| -40006 | Call needs a 'from' field                                    | call needs a 'from' field                   |
| -40007 | Only pbft consensus supports the view property               | getPbftView interface; only pbft consensus supports the view property |
| -40008 | Invalid System Config                                        | getSystemConfigByKey interface, inquire invalid key    |
| -40009 | Don't send requests to this group, <br>the node doesn't belong to the group | invalid request from non-group-member node                  |

## Transaction receipt status list

|status (decimal/hexadecimal)  |message   |definition  |
|:----|:-----|:----|
|0(0x0)  |None |normal |
|1(0x1)  |Unknown |unknown exception |
|2(0x2)  |BadRLP|invalid RLP exception |
|3(0x3)  |InvalidFormat |invalid format exception |
|4(0x4)  |OutOfGasIntrinsic |not-enough cash exception |
|5(0x5)  |InvalidSignature |invalid signature exception |
|6(0x6)  |InvalidNonce |invalid nonce exception |
|7(0x7)  |NotEnoughCash |not-enough cash exception |
|8(0x8)  |OutOfGasBase |not-enough gas exception |
|9(0x9)  |BlockGasLimitReached|GasLimit exception |
|10(0xa)  |BadInstruction |wrong instruction exception |
|11(0xb)  |BadJumpDestination |wrong jump destination exception |
|12(0xc)  |OutOfGas |out-of-gas exception |
|13(0xd)  |OutOfStack |out-of-stack exception |
|14(0xe)  |StackUnderflow |Stack under flow exception |
|15(0xf)  |NonceCheckFail |nonce check fail exception |
|16(0x10)  |BlockLimitCheckFail |blocklimit check fail exception |
|17(0x11)  |FilterCheckFail |filter check fail exception |
|18(0x12)  |NoDeployPermission |deploy contract with no permission |
|19(0x13)  |NoCallPermission |call contract with no permission |
|20(0x14)  |NoTxPermission |transaction with no permission |
|21(0x15)  |PrecompiledError|precompiled error exception |
|22(0x16)  |RevertInstruction |revert instruction exception |
|23(0x17)  |InvalidZeroSignatureFormat |invalid signature format |
|24(0x18)  |AddressAlreadyUsed |address is already used |
|25(0x19)  |PermissionDenied |no permission |
|26(0x20)  |CallAddressError   contract address not exist |


### Precompiled Service API error code

| error code | message                                        | Remarks |
| :----- | :---------------------------------------------- | :-----   |
| 0      | success                                         |          |
| -50000  | permission denied                               |          |
| -50001  | table name already exist                        |          |
| -50100  | unknown function call                            |          |
| -50101  | table does not exist                            |          |
| -51000  | table name and address already exist            |          |
| -51001  | table name and address does not exist           |          |
| -51100  | invalid node ID                                 |SDK Error Code |
| -51101  | the last sealer cannot be removed               |          |
| -51102  | the node is not reachable                       |SDK Error Code |
| -51103  | the node is not a group peer                    |SDK Error Code |
| -51104  | the node is already in the sealer list          |SDK Error Code |
| -51105  | the node is already in the observer list        |SDK Error Code |
| -51200  | contract name and version already exist         |SDK Error Code |
| -51201  | version string length exceeds the maximum limit |SDK Error Code |
| -51300  | invalid configuration entry                     |          |
| -51500  | contract name and version already exist         |          |
| -51501  | condition parse error                           |          |
| -51502  | condition operation undefined                   |          |
