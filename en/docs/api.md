# JSON-RPC API

The following examples in this chapter adopt the [curl](https://curl.haxx.se/) command, which is a data transfer tool that runs the command line by the URL language. JSON-RPC API of FISCO BCOS can be accessed by sending HTTP post request through curl commands. The URL address of the curl command is set as `[jsonrpc_listen_ip]`(If the node is less than v2.3.0, set as the configuration item `listen_ip`) and `[jsonrpc listen port]` of `[rpc]` in a node config file. To format the json data, [jq](https://stedolan.github.io/jq/) is used as a formatter. For the error codes, please check the [RPC Design Documentation](design/rpc.html#json-rpc). For the transaction receipt status, please check [here](./api.html#transaction-receipt-status-list).

## getClientVersion
Returns the current node version.
### Parameters       
none          
### Returns          
- `object` - An object with version data:
    - `Build Time`: `string` - compile time            
    - `Build Type`: `string` - compile machine environment           
    - `Chain Id`: `string` - blockchain id           
    - `FISCO-BCOS Version`: `string` - The version of the node            
    - `Git Branch`: `string` - version branch            
    - `Git Commit Hash`: `string` - latest commit hash        
    - `Supported Version`: `string` - The supported version of the node       

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
    "Chain Id": "1",
    "FISCO-BCOS Version": "2.0.0",
    "Git Branch": "master",
    "Git Commit Hash": "693a709ddab39965d9c39da0104836cfb4a72054",
    "Supported Version": "2.0.0-rc3"
  }
}
```

## getBlockNumber
Returns the number of most recent block in the specific group.
### Parameters  
- `groupID`: `unsigned int` - group ID                
### Returns              
- `string` - the highest block number (a hexadecimal string start with 0x)            
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
Returns the latest [PBFT View](design/consensus/pbft.html#view) in the specific group.
### Parameters         
- `groupID`: `unsigned int` - group ID         
### Returns         
- `string` - PBFT view (a hexadecimal string start with 0x) 
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
**Note:** FISCO BCOS supports [PBFT Consensus](design/consensus/pbft.md) and [Raft Consensus](design/consensus/raft.md). When the blockchain adopts Raft consensus, the error is:
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
Returns the consensus node list in the specific group.
### Parameters          
- `groupID`: `unsigned int` - group ID         
### Returns         
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
Returns the observer node list in the specific group.
### Parameters          
- `groupID`: `unsigned int` - group ID         
### Returns          
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
Returns the consensus status data in the specific group.         
### Parameters          
- `groupID`: `unsigned int` - group ID          
### Returns          
- `object` - An object with consensus status data.
- When PBFT consensus mechanism is used,（PBFT design is introduced in [PBFT Design Documentation](design/consensus/pbft.md)), the fields as follows:           
   -  `accountType`: `unsigned int` - account type            
   -  `allowFutureBlocks`: `bool` - allow future blocks           
   -  `cfgErr`: `bool` - configure errors            
   -  `connectedNodes`: `unsigned int` - connected nodes            
   -  `consensusedBlockNumber`: `unsigned int` - the latest consensus block number            
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

- When Raft consensus mechanism is adopted (Raft design is introduced in [Raft Design Documentation](design/consensus/raft.md)), the fields as follows:     
    - `accountType`: `unsigned int` - account type            
    - `allowFutureBlocks`: `bool` - allow future blocks            
    - `cfgErr`: `bool` - configure error                        
    - `consensusedBlockNumber`: `unsigned int` - the latest consensus block number            
    - `groupId`: `unsigned int` - group ID            
    - `highestblockHash`: `string` - hash of the latest block            
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
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "accountType": 1,
      "allowFutureBlocks": true,
      "cfgErr": false,
      "connectedNodes": 3,
      "consensusedBlockNumber": 38207,
      "currentView": 54477,
      "groupId": 1,
      "highestblockHash": "0x19a16e8833e671aa11431de589c866a6442ca6c8548ba40a44f50889cd785069",
      "highestblockNumber": 38206,
      "leaderFailed": false,
      "max_faulty_leader": 1,
      "nodeId": "f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13",
      "nodeNum": 4,
      "node_index": 3,
      "omitEmptyBlock": true,
      "protocolId": 65544,
      "sealer.0": "6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d",
      "sealer.1": "8a453f1328c80b908b2d02ba25adca6341b16b16846d84f903c4f4912728c6aae1050ce4f24cd9c13e010ce922d3393b846f6f5c42f6af59c65a814de733afe4",
      "sealer.2": "ed483837e73ee1b56073b178f5ac0896fa328fc0ed418ae3e268d9e9109721421ec48d68f28d6525642868b40dd26555c9148dbb8f4334ca071115925132889c",
      "sealer.3": "f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13",
      "toView": 54477
    },
    [
      {
        "nodeId": "6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d",
        "view": 54474
      },
      {
        "nodeId": "8a453f1328c80b908b2d02ba25adca6341b16b16846d84f903c4f4912728c6aae1050ce4f24cd9c13e010ce922d3393b846f6f5c42f6af59c65a814de733afe4",
        "view": 54475
      },
      {
        "nodeId": "ed483837e73ee1b56073b178f5ac0896fa328fc0ed418ae3e268d9e9109721421ec48d68f28d6525642868b40dd26555c9148dbb8f4334ca071115925132889c",
        "view": 54476
      },
      {
        "nodeId": "f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13",
        "view": 54477
      }
    ]
  ]
}
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
Returns the sync status data in the specific group.
### Parameters        
- `groupID`: `unsigned int` - group ID          
### Returns          
- `object` - An object with sync status information:            
    - `blockNumber`: `unsigned int` - the highest block number            
    - `genesisHash`: `string` - hash of genesis block            
    - `isSyncing`: `bool` - syncing
    - `knownHighestNumber`: `unsigned int` - the highest number of the blockchain known by the node
    - `knownLatestHash`: `string` - the latest hash of the blockchain known by the node
    - `latestHash`: `string` - hash of the latest block            
    - `nodeId`: `string` - node ID            
    - `protocolId`: `unsigned int` - protocol ID            
    - `txPoolSize`: `string` - transaction volume in txPool            
    - `peers`: `array` - connected p2p nodes in the specific group, fields of node information are:
        - `blockNumber`: `unsigned int` - the latest block number            
        - `genesisHash`: `string` - hash of genesis block            
        - `latestHash`: `string` - hash of the latest block            
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
Returns the connected p2p node data.        
### Parameters          
- `groupID`: `unsigned int` - group ID            
### Returns         
- `array` - The connected p2p node data:
    - `IPAndPort`: `string` - The IP and port of a node            
    - `nodeId`: `string` - node ID            
    - `Topic`: `array` - The topic data followed by a node            

- Example          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq

// Result
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
Returns the consensus node and observer node list in specific group.         
### Parameters          
- `groupID`: `unsigned int` - group ID           
### Returns          
- `array` - The consensus node and observer node ID list     

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
Returns the node and connected p2p node list.
### Parameters          
- `groupID`: `unsigned int` -  group ID         
### Returns          
- `array` - The node and connected p2p node ID list

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
Returns the group ID list where the node belongs.
### Parameters          
none       
### Returns          
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
Returns information about a block by hash.
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockHash`: `string` - hash of a block      
- `includeTransactions`: `bool` - If `true` it returns the full transaction objects, if `false` only the hashes of the transactions.
### Returns         
- `object` - A block object:
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
Returns information about a block by block number.     
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockNumber`: `string` - block number (hexadecimal string starts with 0x)       
- `includeTransactions`: `bool` - If `true` it returns the full transaction objects, if `false` only the hashes of the transactions.        
### Returns          
please check [getBlockByHash](./api.html#getblockbyhash)  

- Example          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByNumber","params":[1,"0x0",true],"id":1}' http://127.0.0.1:8545 |jq
```
See the result in [getBlockByHash](./api.html#getblockbyhash)  

## getBlockHashByNumber
Returns a block hash by a block number.          
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockNumber`: `string` - block number (hexadecimal string starts with 0x)                  
### Returns          
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
Returns the information about a transaction by transaction hash.
### Parameters         
- `groupID`: `unsigned int` - group ID           
- `transactionHash`: `string` - transaction hash        
### Returns          
- `object`: - A transaction object:  
    - `blockHash`: `string` - hash of the block where this transaction was in.  
    - `blockNumber`: `string` -  block number where this transaction was in.    
    - `from`: `string` - address of the sender               
    - `gas`: `string` - gas provided by the sender    
    - `gasPrice`: `string` - gas price provided by the sender     
    - `hash`: `string` - hash of the transaction               
    - `input`: `string` - the data send along with the transaction      
    - `nonce`: `string` - the number of transactions made by the sender prior to this one     
    - `to`: `string` - address of the receiver, `0x0000000000000000000000000000000000000000` when its a contract creation transaction`        
    - `transactionIndex`: `string` - integer of the transaction's index position in the block
    - `value`: `string` - value transferred          
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
Returns information about a transaction by block hash and transaction index position.
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockHash`: `string` - hash of a block          
- `transactionIndex`: `string` - integer of the transaction index position          
### Returns          
please see [getTransactionByHash](./api.html#gettransactionbyhash)       
- Example
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockHashAndIndex","params":[1,"0x10bfdc1e97901ed22cc18a126d3ebb8125717c2438f61d84602f997959c631fa","0x0"],"id":1}' http://127.0.0.1:8545 |jq
```
see result in [getTransactionByHash](./api.html#gettransactionbyhash)

## getTransactionByBlockNumberAndIndex
Returns information about a transaction by block number and transaction index position.
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `blockNumber`: `string` - a block number (hexadecimal string starts with 0x)          
- `transactionIndex`: `string` - the transaction index position (hexadecimal string starts with 0x)          
### Returns          
please see [getTransactionByHash](./api.html#gettransactionbyhash)            
- Example          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByBlockNumberAndIndex","params":[1,"0x1","0x0"],"id":1}' http://127.0.0.1:8545 |jq
```
see result in [getTransactionByHash](./api.html#gettransactionbyhash)

## getTransactionReceipt
Returns the receipt of a transaction by transaction hash.
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `transactionHash`: `string` - hash of a transaction          
### Returns          
- `object`: - transaction information:  
    - `blockHash`: `string` - hash of the block where this transaction was in      
    - `blockNumber`: `string` -  block number where this transaction was in  
    - `contractAddress`: `string` - contract address, the contract address created, if the transaction was a contract creation, otherwise "0x0000000000000000000000000000000000000000"     
    - `from`: `string` -  address of the sender                     
    - `gasUsed`: `string` - The amount of gas used by this specific transaction
    - `input`: `string` - the data send along with the transaction     
    - `logs`: `array` - Array of log objects, which this transaction generated   
    - `logsBloom`: `string` -  Bloom filter for light clients to quickly retrieve related logs      
    - `output`: `string` - the data result along with the transaction  
    - `root`: `string` - state root   
    - `status`: `string` - status value of the transaction     
    - `to`: `string` - address of the receiver. `0x0000000000000000000000000000000000000000` when its a contract creation.
    - `transactionHash`: `string` - hash of the transaction          
- `transactionIndex`: `string` - integer of the transaction's index position in the block

- Example          
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceipt","params":[1,"0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f"],"id":1}' http://127.0.0.1:8545 |jq

// Result
{
    "id": 1, 
    "jsonrpc": "2.0", 
    "result": {
        "blockHash": "0x977efec48c248ea4be87016446b40d7785d7b71b7d4e3aa0b103b9cf0f5fe19e", 
        "blockNumber": "0xa", 
        "contractAddress": "0x0000000000000000000000000000000000000000", 
        "from": "0xcdcce60801c0a2e6bb534322c32ae528b9dec8d2", 
        "gasUsed": "0x1fb8d", 
        "input": "0xb602109a000000000000000000000000000000000000000000000000000000000000008000000000000000000000000000000000000000000000000000000000000000c00000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000014000000000000000000000000000000000000000000000000000000000000000203078313030303030303030303030303030303030303030303030303030303030000000000000000000000000000000000000000000000000000000000000000832303139303733300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002616100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000026262000000000000000000000000000000000000000000000000000000000000", 
        "logs": [ ], 
        "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", 
        "output": "0x0000000000000000000000000000000000000000000000000000000000000000", 
        "root":"0x38723a2e5e8a17aa7950dc008209944e898f69a7bd10a23c839d341e935fd5ca",
        "status": "0x0", 
        "to": "0x15538acd403ac1b2ff09083c70d04856b8c0bdfd", 
        "transactionHash": "0x708b5781b62166bd86e543217be6cd954fd815fd192b9a124ee9327580df8f3f", 
        "transactionIndex": "0x0"
    }
}
```
## getPendingTransactions
Returns the pending transactions list.
### Parameters         
- `groupID`: `unsigned int` - group ID           
### Returns          
- `object`: - pending transaction information:
    - `from`: `string` - address of the sender               
    - `gas`: `string` - gas provided by the sender    
    - `gasPrice`: `string` - gas price provided by the sender     
    - `hash`: `string` - hash of the transaction               
    - `input`: `string` - the data send along with the transaction      
    - `nonce`: `string` - the number of transactions made by the sender prior to this one     
    - `to`: `string` - address of the receiver, `0x0000000000000000000000000000000000000000` when its a contract creation transaction`        
    - `value`: `string` - value transferred          
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
Returns the size of pending transactions
### Parameters         
- `groupID`: `unsigned int` - group ID           
### Returns         
- `string`: - size of pending transactions         
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
Returns code by a contract address.
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `address`: `string` - contract address
### Returns          
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
Returns current total size of transaction and block number.
### Parameters          
- `groupID`: `unsigned int` - group ID           
### Returns          
- `object`: - current total size of transaction and block number:
    - `blockNumber`: `string` - block number          
    - `failedTxSum`: `string` - the failed total of transaction      
    - `txSum`: `string` - the total of transaction      
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
Returns value by a key value
### Parameters          
- `groupID`: `unsigned int` - group ID       
- `key`: `string` - support tx_count_limit and tx_gas_limit     
### Returns          
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
Executes a new message call immediately without creating a transaction on the block chain.

### Parameters          
- `groupID`: `unsigned int` - group ID           
- `object`: - the transaction call object:
    - `from`: `string` - address of the sender  
    - `to`: `string` - address of the receiver
    - `value`: `string` - (optional) transfer value
    - `data`: `string` - (optional) code parameter. You can read the coding convention in [Ethereum Contract ABI](https://solidity.readthedocs.io/en/develop/abi-spec.html)

### Returns          
- `object`: - result object
    - `currentBlockNumber`: `string` - the current block number  
    - `output`: `string` - result           
    - `status`: `string` - status value of the message(the same as transaction status)              
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
        "output": "0x",
        "status": "0x0"
    }
}
```
## sendRawTransaction
Creates new message call transaction or a contract creation for signed transactions. 
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `rlp`: `string` - transaction data of signing
### Returns          
- `string` - the signed transaction data    
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

## sendRawTransactionAndGetProof

To execute a signed transaction, after the transaction is chained, push the transaction receipt, transaction Merkle certificate, and transaction receipt Merkle certificate. For the Merkle certificate, please refer to [here] (./design/merkle_proof.md).

```eval_rst
.. note::
    - ``supported_version < 2.2.0``: Call the ``sendRawTransactionAndGetProof`` interface, only push the transaction receipt after the transaction is chained
    - ``supported_version >= 2.2.0``: Call the ``sendRawTransactionAndGetProof`` interface to push the transaction receipt, transaction Merkle certificate, and transaction receipt Merkle certificate after the transaction is chained
```

### Parameters
- `groupID`: `unsigned int` - group ID           
- `rlp`: `string` - transaction data of signing

### Returns          
- `string` - Transaction hash
- Example: Same as `sendRawTransaction`, refer to [here](./api.html#sendrawtransaction)


## getTransactionByHashWithProof
Returns the information about the transaction and its proof by a transaction hash. Please note that this function is supported since 2.2.0.
### Parameters          
- `groupID`: `unsigned int` - group ID           
- `transactionHash`: `string` - transaction hash        
### Returns          
- `object`: - A transaction object：  
    - `blockHash`: `string` - hash of the block where this transaction was in
    - `blockNumber`: `string` - block number where this transaction was in     
    - `from`: `string` - address of the sender                
    - `gas`: `string` - gas provided by the sender      
    - `gasPrice`: `string` - gas price provided by the sender       
    - `hash`: `string` - hash of the transaction                  
    - `input`: `string` - the data send along with the transaction          
    - `nonce`: `string` - the number of transactions made by the sender prior to this one      
    - `to`: `string` - address of the receiver, `0x0000000000000000000000000000000000000000` when its a contract creation transaction`        
    - `transactionIndex`: `string` - integer of the transaction's index position in the block
    - `value`: `string` - value transferred 
- `array` - proof of transaction: 
   - `left`: `array` - the hash list of left            
   - `right`: `array` - the hash list of right    
- Examples
```
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionByHashWithProof","params":[1,"0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c"],"id":1}' http://127.0.0.1:8585 |jq
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "transaction": {
      "blockHash": "0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a",
      "blockNumber": "0x5",
      "from": "0x148947262ec5e21739fe3a931c29e8b84ee34a0f",
      "gas": "0x1c9c380",
      "gasPrice": "0x1c9c380",
      "hash": "0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c",
      "input": "0x8a42ebe90000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000003b9aca00000000000000000000000000000000000000000000000000000000000000000a3564646636663863653800000000000000000000000000000000000000000000",
      "nonce": "0x208f6fd78d48aad370df51c6fdf866f8ab022de765c2959841ff2e81bfd9af9",
      "to": "0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f",
      "transactionIndex": "0x32",
      "value": "0x0"
    },
    "txProof": [
      {
        "left": [
          "30f0abfcf4ca152815548620e33d21fd0feaa7c78867791c751e57cb5aa38248c2",
          "31a864156ca9841da8176738bb981d5da9102d9703746039b3e5407fa987e5183e"
        ],
        "right": [
          "33d8078d7e71df3544f8845a9db35aa35b2638e8468a321423152e64b9004367b4",
          "34343a4bce325ec8f6cf48517588830cd15f69b60a05598b78b03c3656d1fbf2f5",
          "35ac231554047ce77c0b31cd1c469f1f39ebe23404fa8ff6cc7819ad83e2c029e7",
          "361f6c588e650323e03afe6460dd89a9c061583e0d62c117ba64729d2c9d79317c",
          "377606f79f3e08b1ba3759eceada7fde3584f01822467855aa6356652f2499c738",
          "386722fe270659232c5572ba54ce23b474c85d8b709e7c08e85230afb1c155fe18",
          "39a9441d668e5e09a5619c365577c8c31365f44a984bde04300d4dbba190330c0b",
          "3a78a8c288120cbe612c24a33cce2731dd3a8fe6927d9ee25cb2350dba08a541f5",
          "3bd9b67256e201b5736f6081f39f83bcb917261144384570bdbb8766586c3bb417",
          "3c3158e5a82a1ac1ed41c4fd78d5be06bf79327f60b094895b886e7aae57cff375",
          "3de9a4d98c5ae658ffe764fbfa81edfdd4774e01b35ccb42beacb67064a5457863",
          "3e525e60c0f7eb935125f1156a692eb455ab4038c6b16390ce30937b0d1b314298",
          "3f1600afe67dec2d21582b8c7b76a15e569371d736d7bfc7a96c0327d280b91dfc"
        ]
      },
      {
        "left": [
          "3577673b86ad4d594d86941d731f17d1515f4669483aed091d49f279d677cb19",
          "75603bfea5b44df4c41fbb99268364641896334f006af3a3f67edaa4b26477ca",
          "1339d43c526f0f34d8a0f4fb3bb47b716fdfde8d35697be5992e0888e4d794c9"
        ],
        "right": [
          "63c8e574fb2ef52e995427a8acaa72c27073dd8e37736add8dbf36be4f609ecb",
          "e65353d911d6cc8ead3fad53ab24cab69a1e31df8397517b124f578ba908558d"
        ]
      },
      {
        "left": [],
        "right": []
      }
    ]
  }
}

```

## getTransactionReceiptByHashWithProof
Returns the information about the receipt and its proof by a transaction hash. Please note that this function is supported since 2.2.0.
### Parameters 
- `groupID`: `unsigned int` - group ID           
- `transactionHash`: `string` - transaction hash          
### Returns
- `array` - proof of receipt: 
   - `left`: `array` - the hash list of left             
   - `right`: `array` - the hash list of right          
- `object`: - transaction information:  
    - `blockHash`: `string` - hash of the block where this transaction was in
    - `blockNumber`: `string` -  block number where this transaction was in  
    - `contractAddress`: `string` - contract address, the contract address created, if the transaction was a contract creation, otherwise "0x0000000000000000000000000000000000000000"     
    - `from`: `string` -  address of the sender                     
    - `gasUsed`: `string` - The amount of gas used by this specific transaction
    - `input`: `string` - the data send along with the transaction     
    - `logs`: `array` - Array of log objects, which this transaction generated   
    - `logsBloom`: `string` -  Bloom filter for light clients to quickly retrieve related logs      
    - `output`: `string` - the data result along with the transaction     
    - `status`: `string` - status value of the transaction     
    - `to`: `string` - address of the receiver. `0x0000000000000000000000000000000000000000` when its a contract creation.
    - `transactionHash`: `string` - hash of the transaction          
    - `transactionIndex`: `string` - integer of the transaction's index position in the block
- Examples
```
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceiptByHashWithProof","params":[1,"0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c"],"id":1}' http://127.0.0.1:8585 |jq

{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "receiptProof": [
      {
        "left": [
          "3088b5c8f9d92a3411a911f35ff0119a02e8f8f04852cf2fdfaa659843eac6a3ad",
          "31170ac8fd555dc50e59050841da0d96e4c4bc7e6266e1c6865c08c3b2391801dd"
        ],
        "right": [
          "33c572c8f961e0c56689d641fcf274916857819769a74e6424c58659bf530e90e3",
          "341233933ea3d357b4fdd6b3d1ed732dcff15cfd54e527c93c15a4e0238585ed11",
          "351e7ba09965cce1cfb820aced1d37204b06d96a21c5c2cf36850ffc62cf1fc84c",
          "361f65633d9ae843d4d3679b255fd448546a7b531c0056e8161ea0adbf1af12c0f",
          "37744f6e0d320314536b230d28b2fd6ac90b0111fb1e3bf4a750689abc282d8589",
          "386e60d9daa0be9825019fcf3d08cdaf51a90dc62a22a6e11371f94a8e516679cc",
          "391ef2f2cee81f3561a9900d5333af18f59aa3cd14e70241b5e86305ba697bf5f2",
          "3ac9999d4f36d76c95c61761879eb9ec60b964a489527f5af844398ffaa8617f0d",
          "3b0039ce903e275170640f3a464ce2e1adc2a7caee41267c195469365074032401",
          "3ca53017502028a0cb5bbf6c47c4779f365138da6910ffcfebf9591b45b89abd48",
          "3de04fc8766a344bb73d3fe6360c61d036e2eeedfd9ecdb86a0498d7849ed591f0",
          "3e2fc73ee22c4986111423dd20e8db317a313c9df29fa5aa3090f27097ecc4e1a9",
          "3fa7d31ad5c6e7bba3f99f9efc03ed8dd97cb1504003c34ad6bde5a662481f00a0"
        ]
      },
      {
        "left": [
          "cd46118c0e99be585ffcf50423630348dbc486e54e9d9293a6a8754020a68a92",
          "3be78209b3e3c83af3668ec3192b5bf232531323ef66b66de80a11f386270132",
          "bd3a11d74a3fd79b1e1ea17e45b76eda4d25f6a5ec7fc5f067ea0d086b1ce70f"
        ],
        "right": [
          "6a6cefef8b48e455287a8c8694b06f4f7cb7950017ab048d6e6bdd8029f9f8c9",
          "0a27c5ee02e618d919d228e6a754dc201d299c91c9e4420a48783bb6fcd09be5"
        ]
      },
      {
        "left": [],
        "right": []
      }
    ],
    "transactionReceipt": {
      "blockHash": "0xcd31b05e466bce99460b1ed70d6069fdfbb15e6eef84e9b9e4534358edb3899a",
      "blockNumber": "0x5",
      "contractAddress": "0x0000000000000000000000000000000000000000",
      "from": "0x148947262ec5e21739fe3a931c29e8b84ee34a0f",
      "gasUsed": "0x21dc1b",
      "input": "0x8a42ebe90000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000003b9aca00000000000000000000000000000000000000000000000000000000000000000a3564646636663863653800000000000000000000000000000000000000000000",
      "logs": [],
      "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
      "output": "0x",
      "root": "0xc3b4185963c78a4ca8eb90240e5cd95371d7217a9ce2bfa1149d53f79c73afbb",
      "status": "0x0",
      "to": "0xd6c8a04b8826b0a37c6d4aa0eaa8644d8e35b79f",
      "transactionHash": "0xd2c12e211315ef09dbad53407bc820d062780232841534954f9c23ab11d8ab4c",
      "transactionIndex": "0x32"
    }
  }
}

```

## generateGroup
Generate new group with group ID and parameters of genesis block. Please note that this function is supported since 2.2.0
### Parameters
- `groupID`: `unsigned int` - ID of the group
- `params`: `object` - parameters of genesis block
    - `timestamp`: `unsigned int` - timestamp of genesis block
    - `sealers`: `array` - node ID of sealers, sealers should have valid P2P connection to each other
    - `enable_free_storage`: `bool` - optional，whether to enable "free storage" mode, after activation, the gas usage of storage instructions will be decreased
### Returns
- `object`: - result of calling
    - `code`: - status，it's meaning can be referenced from [Dynamice group management API status code](#Dynamice\ group management\ API\ status\ code)
    - `message`: - message
- Example
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"generateGroup","params":[2, '{"timestamp":"1585214879000","sealers":["70f18c055d366615e86df99f91b6d3f16f07d66293b203b73498442c0366d2c8ff7a21bb56923d9d81b1c2916251888e47adf66c350738c898defac50aead5ab","dde37f534885f08db914566efeb03183d59363a4be972bbcdde25c37f0b350e1980a7de4fdc4aaf956b931aab00b739a8af475ed2461b8591d8c734b27285f57","d41672b29b3b1bfe6cad563d0f0b2a2735865b27918307b85085f892043a63f681ac8799243e920f7bb144b111d854d0592ba5f28aa7a4e0f9f533f9fdf76ead","7ba2717f81f38e7371ccdbe173751f051b86819f709e940957664dbde028698fd31ba3042f7dd9accd73741ba42afc35a8ef67fe7abbdeb76344169773aa0eca"],"enable_free_storage":true}'],"id":1}' http://127.0.0.1:8545 | jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "code": "0x0",
    "message": "Group 2 generated successfully"
  }
}

```

## startGroup
Starts group. Please note that this function is supported since 2.2.0
### Parameters
- `groupID`: `unsigned int` - ID of the group
### Returns
- `object`: - result of calling
    - `code`: - status，it's meaning can be referenced from [Dynamice group management API status code](#Dynamice\ group management\ API\ status\ code)
    - `message`: - message
- Example
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"startGroup","params":[2],"id":1}' http://127.0.0.1:8545 | jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "code": "0x0",
    "message": "Group 2 started successfully"
  }
}

```
## stopGroup
Stops group. Please note that this function is supported since 2.2.0
### Parameters
- `groupID`: `unsigned int` - ID of the group
### Returns
- `object`: - result of calling
    - `code`: - status，it's meaning can be referenced from [Dynamice group management API status code](#Dynamice\ group management\ API\ status\ code)
    - `message`: - message
- Example
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"stopGroup","params":[2],"id":1}' http://127.0.0.1:8545 | jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "code": "0x0",
    "message": "Group 2 stopped successfully"
  }
}

```

## removeGroup
Deletes group, but the data of group will be reserved for future recover. Please note that this function is supported since 2.2.0
### Parameters
- `groupID`: `unsigned int` - ID of the group
### Returns
- `object`: - result of calling
    - `code`: - status，it's meaning can be referenced from [Dynamice group management API status code](#Dynamice\ group management\ API\ status\ code)
    - `message`: - message
- Example
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"removeGroup","params":[2],"id":1}' http://127.0.0.1:8545 | jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "code": "0x0",
    "message": "Group 2 deleted successfully"
  }
}

```
## recoverGroup
Recovers group. Please note that this function is supported since 2.2.0
### Parameters
- `groupID`: `unsigned int` - ID of the group
### Returns
- `object`: - result of calling
    - `code`: - status，it's meaning can be referenced from [Dynamice group management API status code](#Dynamice\ group management\ API\ status\ code)
    - `message`: - message
- Example
```
// Request
curl -Ss -X POST --data '{"jsonrpc":"2.0","method":"recoverGroup","params":[2],"id":1}' http://127.0.0.1:8545 | jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "code": "0x0",
    "message": "Group 2 recovered successfully"
  }
}

```

## queryGroupStatus
Queries status of group. Please note that this function is supported since 2.2.0
### Parameters
- `groupID`: `unsigned int` - ID of the group
### Returns
- `object`: - result of calling
    - `code`: - status，it's meaning can be referenced from [Dynamice group management API status code](#Dynamice\ group management\ API\ status\ code)
    - `message`: - message
    - `status`: - status of the group:
        - `INEXISTENT`: the group is inexistent
        - `STOPPING`: the group is stopping
        - `RUNNING`: the group is running
        - `STOPPED`: the group is stopped
        - `DELETED`: the group is deleted
- Example
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"queryGroupStatus","params":[2],"id":1}' http://127.0.0.1:8545 | jq

// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "code": "0x0",
    "message": "",
    "status": "STOPPED"
  }
}

```

## Error codes

### RPC error code

When a rpc call is made, the Server will reply with a response, which contains error result field, which includes as follows:

- code: A Number that indicates the error type that occurred.       
- message: A String providing a short description of the error.   
- data: A Primitive or Structured value that contains additional information about the error. This may be omitted.

There are 2 types of error code: JSON-RPC standard error code and FISCO BCOS RPC error code.

#### JSON-RPC standard error code

Standard error codes and their corresponding meanings are as follows:

| code   | message              | definition                       |
| :----- | :------------------- | :------------------------- |
| -32600 | INVALID_JSON_REQUEST | send invalid request object         |
| -32601 | METHOD_NOT_FOUND     | method not exist or valid         |
| -32602 | INVALID_PARAMS       | invalid method parameter             |
| -32603 | INTERNAL ERROR       | internal call error               |
| -32604 | PROCEDURE_IS_METHOD  | internal error; ID field not provided in the request |
| -32700 | JSON_PARSE_ERROR     | json received by server fails to be parsed |

#### FISCO BCOS RPC error code

FISCO BCOS RPC  error codes and their corresponding meanings are as follows:

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
| -40010 | RPC module initialization is incomplete                                    | RPC module initialization is incomplete     |
| -40011 | Over QPS limit                                       | The request rate from the SDK to the node exceeds the request rate limit of the node     |

## Transaction receipt status list

|status (decimal/hexadecimal)  |message   |definition  |
|:----|:-----|:----|
|0(0x0)  |None |normal |
|1(0x1)  |Unknown |unknown exception |
|2(0x2)  |BadRLP|invalid RLP exception |
|3(0x3)  |InvalidFormat |invalid format exception |
|4(0x4)  |OutOfGasIntrinsic |contract to deploy is too long / input data is too long |
|5(0x5)  |InvalidSignature |invalid signature exception |
|6(0x6)  |InvalidNonce |invalid nonce exception |
|7(0x7)  |NotEnoughCash |not-enough cash exception |
|8(0x8)  |OutOfGasBase |input data is too long (RC version) |
|9(0x9)  |BlockGasLimitReached|GasLimit exception |
|10(0xa)  |BadInstruction |wrong instruction exception |
|11(0xb)  |BadJumpDestination |wrong jump destination exception |
|12(0xc)  |OutOfGas |out-of-gas during EVM execution / contract to deploy exceeds max contract length |
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
|26(0x1a)  |CallAddressError   contract address not exist ||
|30(0x1e)  |ContractFrozen             | contract has been frozen |
|31(0x1f)  |AccountFrozen              | account has been frozen |


### Precompiled Service API error code

| error code | message                                        | Remarks |
| :----- | :---------------------------------------------- | :-----   |
| 0      | success                                         |          |
| -50000  | permission denied                               |          |
| -50001  | table name already exist                        |          |
| -50100  | table does not exist                            |          |
| -50101  | unknow function call                            |          |
| -50102  | address invalid                                 |          |
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

### Dynamice group management API status code

| status code | message                      | definition                                           |
| :---------- | :--------------------------- | :--------------------------------------------------- |
| 0x0         | SUCCESS                      | Success                                              |
| 0x1         | INTERNAL_ERROR               | Internal error                                       |
| 0x2         | GROUP_ALREADY_EXISTS         | The group already existed when calling generateGroup |
| 0x3         | GROUP_ALREADY_RUNNING        | The group already run when calling startGroup        |
| 0x4         | GROUP_ALREADY_STOPPED        | The group already stopppd when calling stopGroup     |
| 0x5         | GROUP_ALREADY_DELETED        | The group already deleted when calling removeGroup   |
| 0x6         | GROUP_NOT_FOUND              | The group not exists                                 |
| 0x7         | INVALID_PARAMS               | Parameters are illegal                               |
| 0x8         | PEERS_NOT_CONNECTED          | No valid P2P connections between sealers             |
| 0x9         | GENESIS_CONF_ALREADY_EXISTS  | Configuration of genesis block already existed       |
| 0xa         | GROUP_CONF_ALREADY_EXIST     | Configuration of group already existed               |
| 0xb         | GENESIS_CONF_NOT_FOUND       | Configuration of genesis block not found             |
| 0xc         | GROUP_CONF_NOT_FOUND         | Configuration of group not found                     |
| 0xd         | GROUP_IS_STOPPING            | The group is stopping                                |
| 0xf         | GROUP_NOT_DELETED            | The group hasn't been deleted when call recoverGroup |