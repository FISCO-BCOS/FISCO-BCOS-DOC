# 2. Blockchain RPC interface

Tags: "RPC"

---------
The Java SDK provides Java API interfaces for blockchain application developers. By function, Java APIs can be divided into the following categories:

- Client: Provides support for accessing the JSON-RPC interface for FISCO BCOS 3.x nodes, providing support for deploying and invoking contracts；
- Precompiled: Provides calls to FISCO BCOS 3.x Precompiled contract(Precompiled Contracts)interfaces, including 'SensusService', 'SystemConfigService', 'BFSService', and 'KVTableService'。
- AuthManager: Provides FISCO BCOS 3.x permissions to control invocation of pre-deployment contracts。

```eval_rst
.. note::
    - Client interface declarations are in the 'Client.java' file
```

**Special attention:**
**1. There are two types of Client interfaces, one is an interface with node, and the other is an interface without node。Interface with node allows node RPC to send requests to specified connected nodes。If not specified, the node RPC randomly sends requests to the node。**
**2. The following interface examples are all closed under the premise of node SSL communication。To turn off the node's ssl communication configuration item, set the '[rpc]' entry of the node configuration file 'config.ini' to' disable _ ssl = true'。**
## 1. Contract operation interface

### sendTransaction

Sending transactions to the blockchain。

**Parameters**

-node: allows RPC to send requests to the specified node
-signedTransactionData: transactions after signature
-withProof: return whether to bring Merkel tree proof

**Return value**

- BcosTransactionReceipt: After receiving the transaction, the node returns the packet to the SDK, including the transaction hash information。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendTransaction","params":["group0","","0x1a1c2606636861696e30360667726f7570304101fb564d36323332373833373230383636323235323233313231343039373038383134363030353536383536313037383031313639373432363032343636323131353337373138313837323836303337397d0001046b608060405234801561001057600080fd5b5060408051808201909152600d8082526c48656c6c6f2c20576f726c642160981b60209092019182526100459160009161004b565b5061011f565b828054610057906100e4565b90600052602060002090601f01602090048101928261007957600085556100bf565b82601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b5b808211156100cb57600081556001016100d0565b600181811c908216806100f857607f821691505b602082108114156101195763b95aa35560e01b600052602260045260246000fd5b50919050565b61033d8061012e6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063299f7f9d1461003b5780633590b49f14610059575b600080fd5b61004361006e565b60405161005091906101b0565b60405180910390f35b61006c61006736600461021b565b610100565b005b60606000805461007d906102cc565b80601f01602080910402602001604051908101604052809291908181526020018280546100a9906102cc565b80156100f65780601f106100cb576101008083540402835291602001916100f6565b820191906000526020600020905b8154815290600101906020018083116100d957829003601f168201915b5050505050905090565b8051610113906000906020840190610117565b5050565b828054610123906102cc565b90600052602060002090601f016020900481019282610145576000855561018b565b82601f1061015e57805160ff191683800117855561018b565b8280016001018555821561018b579182015b8281111561018b578251825591602001919060010190610170565b5061019792915061019b565b5090565b5b80821115610197576000815560010161019c565b600060208083528351808285015260005b818110156101dd578581018301518582016040015282016101c1565b818111156101ef576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561022d57600080fd5b813567ffffffffffffffff8082111561024557600080fd5b818401915084601f83011261025957600080fd5b81358181111561026b5761026b610205565b604051601f8201601f19908116603f0116810190838211818310171561029357610293610205565b816040528281528760208487010111156102ac57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600181811c908216806102e057607f821691505b602082108114156103015763b95aa35560e01b600052602260045260246000fd5b5091905056fea2646970667358221220ad3331f4f52a10ab9c50f2e63a46fd49fab3847ff4e17912290db8f009f89c9464736f6c634300080b003387000001565b7b22696e70757473223a5b5d2c2273746174654d75746162696c697479223a226e6f6e70617961626c65222c2274797065223a22636f6e7374727563746f72227d2c7b22696e70757473223a5b5d2c226e616d65223a22676574222c226f757470757473223a5b7b22696e7465726e616c54797065223a22737472696e67222c226e616d65223a22222c2274797065223a22737472696e67227d5d2c2273746174654d75746162696c697479223a2276696577222c2274797065223a2266756e6374696f6e227d2c7b22696e70757473223a5b7b22696e7465726e616c54797065223a22737472696e67222c226e616d65223a226e222c2274797065223a22737472696e67227d5d2c226e616d65223a22736574222c226f757470757473223a5b5d2c2273746174654d75746162696c697479223a226e6f6e70617961626c65222c2274797065223a2266756e6374696f6e227d5d0b2d000020867fc7059f04e9f172202b777673a8413c9e47990dcd09b11311a2f1f5b55a4f3d0001008015e0c4a3a51b5b5b157502aae04f3905bae8daf1389bddf12b0eb7069ead63b76d42383dcd38520df339d571d37ea85334dba126edcc323a98b91e51d32ec074b5f7430ea78c64ccc8d364bf4563dbffb33be503344b72f3384f987c38af98db3f0f00169f2a6545d0920a1a6cb7f338b8b717f03d05fded80ddbbb171a099c1",true],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x1"
}
```

### sendTransactionAsync

The transaction publishing asynchronous interface, after receiving the response from the node, calls the specified callback。

**Parameters**

-node: allows RPC to send requests to the specified node
- signedTransactionData: Transaction string after signature;
-withProof: return whether to bring Merkel tree proof
- callback: After the SDK receives the packet return from the node, it calls the callback function. The callback function will bring the transaction receipt。

**Return value**

- None

### call

Send a request to the node, call the contract constant interface。

**Parameters**

-node: allows RPC to send requests to the specified node
- transaction: Contract invocation information, including the contract address, the contract caller, and the abi encoding of the invoked contract interface and parameters

**Return value**

- Call: The return result of the contract constant interface, including the current block height, interface execution status information, and interface execution results

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"call","params":["group0","","0xc0523dbdd94ba27e14b0336d799489340ca24cdf","aaaa"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "blockNumber": 8,
        "output": "0x",
        "status": 16
    }
}
```

### callAsync

The contract constant interface is called asynchronously. After receiving the execution result of the contract interface returned by the node, the specified callback function is executed

**Parameters**

-node: allows RPC to send requests to the specified node
- transaction: Contract invocation information, including contract address, contract caller, and invocation interface and parameter information；
- callback: callback function。

**Return value**

- None

### getCode

Query contract code information corresponding to a specified contract address。

**Parameters**

-node: allows RPC to send requests to the specified node
- address: Contract Address。

**Return value**

- Code: Contract code corresponding to the contract address。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getCode","params":["group0","node0","0x17826374dbb2025b30ddec39ba662349d76d8fc6"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x608060405234801561001057600080fd5b50600436106100365760003560e01c8063299f7f9d1461003b5780633590b49f14610059575b600080fd5b61004361006e565b60405161005091906101b0565b60405180910390f35b61006c61006736600461021b565b610100565b005b60606000805461007d906102cc565b80601f01602080910402602001604051908101604052809291908181526020018280546100a9906102cc565b80156100f65780601f106100cb576101008083540402835291602001916100f6565b820191906000526020600020905b8154815290600101906020018083116100d957829003601f168201915b5050505050905090565b8051610113906000906020840190610117565b5050565b828054610123906102cc565b90600052602060002090601f016020900481019282610145576000855561018b565b82601f1061015e57805160ff191683800117855561018b565b8280016001018555821561018b579182015b8281111561018b578251825591602001919060010190610170565b5061019792915061019b565b5090565b5b80821115610197576000815560010161019c565b600060208083528351808285015260005b818110156101dd578581018301518582016040015282016101c1565b818111156101ef576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561022d57600080fd5b813567ffffffffffffffff8082111561024557600080fd5b818401915084601f83011261025957600080fd5b81358181111561026b5761026b610205565b604051601f8201601f19908116603f0116810190838211818310171561029357610293610205565b816040528281528760208487010111156102ac57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600181811c908216806102e057607f821691505b602082108114156103015763b95aa35560e01b600052602260045260246000fd5b5091905056fea2646970667358221220ad3331f4f52a10ab9c50f2e63a46fd49fab3847ff4e17912290db8f009f89c9464736f6c634300080b0033"
}
```
## 2. Blockchain query interface

### getBlockNumber

Obtain the latest block height of the group corresponding to the client object。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- BlockNumber: The latest block height of the group corresponding to the client object。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockNumber","params":[],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": 8
}
```

### getBlockNumberAsync

Asynchronously obtains the latest block height of the group corresponding to the client object。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: get callback after block high

**Return value**

- None

### getTotalTransactionCount

Obtain the transaction statistics of the client group, including the number of transactions on the chain and the number of failed transactions on the chain。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- TotalTransactionCount: Transaction statistics, including:
    - txSum: Total amount of transactions on the chain
    - blockNumber: Current block height of the group
    - failedTxSum: Total amount of abnormal transactions executed on the chain

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTotalTransactionCount","params":[],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "blockNumber": 8,
        "failedTransactionCount": 0,
        "transactionCount": 8
    }
}
```

### getTotalTransactionCountAsync

Asynchronously obtains the transaction statistics of the client corresponding to the group, including the number of transactions on the chain and the number of failed transactions on the chain。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: callback after getting transaction information

**Return value**

- None

### getBlockByNumber

Obtain block information according to block height。

**Parameters**

-node: allows RPC to send requests to the specified node；
- blockNumber: Block height；
-onlyHeader: true / false, indicating whether only the block header data is obtained in the obtained block information；
-onlyTxHash: true / false, indicating whether the obtained block information contains complete transaction information；
  - false: The block returned by the node contains complete transaction information；
  - true: The block returned by the node contains only the transaction hash。

**Return value**

- BcosBlock: Query the obtained block information

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByNumber","params":["group0","node0",4,false,false],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "consensusWeights": [],
        "extraData": "0x5b636861696e5d0a736d5f63727970746f3a310a636861696e49443a20636861696e300a67726f7549443a2067726f7570300a5b636f6e73656e7379735d0a636f6e73656e7375735f747970653a20706266740a626c6f636b5f74785f636f756e745f6c696d69743a313030300a6c65616465725f706572696f643a310a5b76657273696f6e5d0a636f6d7061746962696c6974795f76657273696f6e3a332e332e300a5b74785d0a6761736c696d69743a333030303030303030300a5b6578656375746f725d0a69737761736d3a20300a697341757468436865636b3a310a6175746841646d696e4163636f756e743a3078623161656264613131326232306131303665653937666334353430343738613166636162343963630a697353657269616c457865637574653a310a6e6f64652e303a30303535646361663037336133333261613161643930666635336566303238363830643234663962663266636263303765633462626434383739343133663735313138613537306263663830303163343532366664623863313331396535316165363334343434333165633861623833393436356330356531373865376334392c310a6e6f64652e313a32613761656366346163663031306230633133363937653834666664346631383534343833353636323834353632316639333631363664383863663037336631366231646161363838336465343537653631323933373764323134313262346337373039396538613530653865613532313735326164636464656238623333312c310a6e6f64652e323a35333534333939303839373962383931373132383364636137383532303736336530613332633634363331643766333465653164336637346334303861333161616138353635633530393234626136383137636433336331336665396463393238653336623861316466303232666338323564333638376332623237333235382c310a6e6f64652e333a64643939616238383336373761386165663263326133383437623936373165353031626436393330373932373030333938363237656230613162613034666538313031353734356236323837626535663336373836633063633866663132653964643332313564633561343231306230313264373733333061373339613263332c310a",
        "gasUsed": "0",
        "hash": "0x47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5",
        "number": 0,
        "parentInfo": [],
        "receiptsRoot": "0x0000000000000000000000000000000000000000000000000000000000000000",
        "sealer": 0,
        "sealerList": [],
        "signatureList": [],
        "stateRoot": "0x0000000000000000000000000000000000000000000000000000000000000000",
        "timestamp": 0,
        "transactions": [],
        "txsRoot": "0x0000000000000000000000000000000000000000000000000000000000000000",
        "version": 50528256
    }
}
```
### getBlockByNumberAsync

Obtain block information asynchronously according to block height。

**Parameters**

-node: allows RPC to send requests to the specified node
- blockNumber: Block height；
-onlyHeader: true / false, indicating whether only the block header data is obtained in the obtained block information；
- onlyTxHash: true / false, indicating whether the obtained block information contains complete transaction information；
  - false: The block returned by the node contains complete transaction information；
  - true: The block returned by the node contains only the transaction hash；
-callback: get the callback after the block is completed。

**Return value**

- None

### getBlockByHash

Obtain block information based on block hash。

**Parameters**

-node: allows RPC to send requests to the specified node
- blockHash: Block Hash
-onlyHeader: true / false, indicating whether only the block header data is obtained in the obtained block information；
- onlyTxHash: true / false, indicating whether the obtained block information contains complete transaction information；
    - true: The block returned by the node contains only the transaction hash;
    - false: The block returned by the node contains complete transaction information。

**Return value**

- BcosBlock: Query the obtained block information。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByHash","params":["group0","node0","0x2fbafbf71395bb07d1d6e142a06fa3cd9436aee3e91b5b9e6ffc5c47133c3738",true,true],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "consensusWeights": [
            1,
            1,
            1,
            1
        ],
        "extraData": "0x",
        "gasUsed": "24363",
        "hash": "0x2fbafbf71395bb07d1d6e142a06fa3cd9436aee3e91b5b9e6ffc5c47133c3738",
        "number": 2,
        "parentInfo": [
            {
                "blockHash": "0x1498f2ff420fb9c240c7081b99bd76819c7348fb1a899095476afa29146c8874",
                "blockNumber": 1
            }
        ],
        "receiptsRoot": "0xf18d1984c7745a826e4b8d608b823534f1e44b273f75972e16cf2ac3ff313e14",
        "sealer": 1,
        "sealerList": [
            "0x0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49",
            "0x2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331",
            "0x535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258",
            "0xdd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3"
        ],
        "signatureList": [
            {
                "sealerIndex": 0,
                "signature": "0xbc1c559adb2723b31b6f75d4f7bd06b204563d238ebc53774b0a621d586482573ae320a6dcca9bd03a2a6af0d8c368cdd660c56b27c8af0c154947f3e6252074"
            },
            {
                "sealerIndex": 1,
                "signature": "0x3490744fc8370eb65649b8bb89a0a53623838e37e37bbb66e3f26f67e81dabe8b34a7e10d85520c475bf92cc89da705aa3d76a0b8316784733301e7af57004a6"
            },
            {
                "sealerIndex": 2,
                "signature": "0x9b7f9b3d18b619ea66f2a028f9980c09b3a83c0a6595da0fed4441a175a165154973a5b21eabf817311d0305b6b194818fab7308335760d10b584c954cf052eb"
            }
        ],
        "stateRoot": "0xc81a18b17f22851cd19f48d2fcf875bebea711d7b5823b99a983c2ecbfc48f1a",
        "timestamp": 1680598474810,
        "txsRoot": "0xdce357d4a81bfe2c9b9cc83fde7576a8ae8dede910b70cdf9abed71a32ed10bf",
        "version": 50528256
    }
}
```

### getBlockByHashAsync

Asynchronously obtain block information based on block hash。

**Parameters**

-node: allows RPC to send requests to the specified node
- blockHash: Block Hash
-onlyHeader: true / false, indicating whether only the block header data is obtained in the obtained block information；
- onlyTxHash: true / false, indicating whether the obtained block information contains complete transaction information；
  - true: The block returned by the node contains only the transaction hash;
  - false: The block returned by the node contains complete transaction information；
-callback: get the callback after the block is completed。

**Return value**

- None

### getBlockHashByNumber

Obtain block hash based on block height

**Parameters**

-node: allows RPC to send requests to the specified node
- blockNumber: Block height

**Return value**

- BlockHash: Block hash corresponding to the specified block height

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockHashByNumber","params":["group0","",0],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5"
}
```

### getBlockHashByNumberAsync

Obtain block hash asynchronously based on block height

**Parameters**

-node: allows RPC to send requests to the specified node
- blockNumber: Block height
-callback: callback after getting

**Return value**

- None

### getTransaction

Get transaction information based on transaction hash。

**Parameters**

-node: allows RPC to send requests to the specified node
- transactionHash: Transaction Hash
-withProof: whether to bring Merkel Tree Proof

**Return value**

- BcosTransaction: Transaction information corresponding to the specified hash。

### getTransactionAsync

Asynchronous acquisition of transaction information based on transaction hash。

**Parameters**

-node: allows RPC to send requests to the specified node
- transactionHash: Transaction Hash
-withProof: whether to bring Merkel Tree Proof
-callback: Get the callback at the time of the transaction

**Return value**

- None

### getTransactionReceipt

Get transaction receipt information based on transaction hash。

**Parameters**

-node: allows RPC to send requests to the specified node
- transactionHash: Transaction Hash
-withProof: return whether to bring Merkel tree proof

**Return value**

- BcosTransactionReceipt: Receipt information corresponding to the transaction hash。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceipt","params":["group0","node0","0xdce357d4a81bfe2c9b9cc83fde7576a8ae8dede910b70cdf9abed71a32ed10bf",true],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "blockNumber": 2,
        "checksumContractAddress": "0x82A3c3289a9F234A38AcEED3016D1fF0E0F922B3",
        "contractAddress": "0x82a3c3289a9f234a38aceed3016d1ff0e0f922b3",
        "extraData": "",
        "from": "0xf3293c3452b3b0479e64a4c612e8c1b0da5fe1af",
        "gasUsed": "24363",
        "hash": "0xf18d1984c7745a826e4b8d608b823534f1e44b273f75972e16cf2ac3ff313e14",
        "input": "0x608060405234801561001057600080fd5b5060408051808201909152600d8082526c48656c6c6f2c20576f726c642160981b60209092019182526100459160009161004b565b5061011f565b828054610057906100e4565b90600052602060002090601f01602090048101928261007957600085556100bf565b82601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b5b808211156100cb57600081556001016100d0565b600181811c908216806100f857607f821691505b602082108114156101195763b95aa35560e01b600052602260045260246000fd5b50919050565b61033d8061012e6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063299f7f9d1461003b5780633590b49f14610059575b600080fd5b61004361006e565b60405161005091906101b0565b60405180910390f35b61006c61006736600461021b565b610100565b005b60606000805461007d906102cc565b80601f01602080910402602001604051908101604052809291908181526020018280546100a9906102cc565b80156100f65780601f106100cb576101008083540402835291602001916100f6565b820191906000526020600020905b8154815290600101906020018083116100d957829003601f168201915b5050505050905090565b8051610113906000906020840190610117565b5050565b828054610123906102cc565b90600052602060002090601f016020900481019282610145576000855561018b565b82601f1061015e57805160ff191683800117855561018b565b8280016001018555821561018b579182015b8281111561018b578251825591602001919060010190610170565b5061019792915061019b565b5090565b5b80821115610197576000815560010161019c565b600060208083528351808285015260005b818110156101dd578581018301518582016040015282016101c1565b818111156101ef576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561022d57600080fd5b813567ffffffffffffffff8082111561024557600080fd5b818401915084601f83011261025957600080fd5b81358181111561026b5761026b610205565b604051601f8201601f19908116603f0116810190838211818310171561029357610293610205565b816040528281528760208487010111156102ac57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600181811c908216806102e057607f821691505b602082108114156103015763b95aa35560e01b600052602260045260246000fd5b5091905056fea2646970667358221220ad3331f4f52a10ab9c50f2e63a46fd49fab3847ff4e17912290db8f009f89c9464736f6c634300080b0033",
        "logEntries": [],
        "message": "",
        "output": "0x",
        "receiptProof": [],
        "status": 0,
        "to": "",
        "transactionHash": "0xdce357d4a81bfe2c9b9cc83fde7576a8ae8dede910b70cdf9abed71a32ed10bf",
        "transactionProof": [],
        "txReceiptProof": [
            "f18d1984c7745a826e4b8d608b823534f1e44b273f75972e16cf2ac3ff313e14"
        ],
        "version": 0
    }
}
```
### getTransactionReceiptAsync

Asynchronously obtain transaction receipt information based on transaction hash。

**Parameters**

-node: allows RPC to send requests to the specified node
- transactionHash: Transaction Hash
-withProof: return whether to bring Merkel tree proof
-callback: callback when getting transaction receipt

**Return value**

- None

### getPendingTxSize

Get the number of unprocessed transactions in the transaction pool。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- PendingTxSize: Number of unprocessed transactions in the trading pool。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPendingTxSize","params":["group0","node0"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": 0
}
```
### getPendingTxSizeAsync

Get the number of unprocessed transactions in the transaction pool。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: callback when getting transaction receipt

**Return value**

- None

### getBlockLimit
Obtain the BlockLimit of the corresponding group of the client. BlockLimit is mainly used for transaction anti-duplication。

**Parameters**

- None

**Return value**

- BigInteger: BlockLimit for groups。

### getPeers

Obtain the network connection information of the specified node。

**Parameters**

- None

**Return value**

- Peers: Network connection information for the specified node。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":["group0"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "endPoint": "0.0.0.0:30300",
        "groupNodeIDInfo": [
            {
                "group": "group0",
                "nodeIDList": [
                    "0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49"
                ]
            }
        ],
        "p2pNodeID": "0463f180720373a05c4e0dd5244a88b278eae3f28f162d3b7c51d53a150f7dca570ef9d0f438170d6108a7a32a856b193486bc69624c2893669dcc32790222c6f9",
        "peers": [
            {
                "endPoint": "127.0.0.1:30301",
                "groupNodeIDInfo": [
                    {
                        "group": "group0",
                        "nodeIDList": [
                            "535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258"
                        ]
                    }
                ],
                "p2pNodeID": "046a4ed7691832667c6cb49fadd367f761f6118138256f040e3afdbea3852416d2796907fd0d968b951e08ab5efd8071e408a25288b0bf19b38aee00a0b2700a0b"
            },
            {
                "endPoint": "127.0.0.1:50168",
                "groupNodeIDInfo": [
                    {
                        "group": "group0",
                        "nodeIDList": [
                            "dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3"
                        ]
                    }
                ],
                "p2pNodeID": "04b59db5c18b0ebdf20f8dbd5bfa2e00d5ee68d98abe8029785f7cb387910c4f6f6512a07363711e9cdb96c9e5595827f8cd3075e70629899891169d8b512cc140"
            },
            {
                "endPoint": "127.0.0.1:30302",
                "groupNodeIDInfo": [
                    {
                        "group": "group0",
                        "nodeIDList": [
                            "2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331"
                        ]
                    }
                ],
                "p2pNodeID": "045b7fa3b9a7e9d37b2be0d4d5eeb1bc01e2d01567ed2b638c5e5e1a26a9e14a9735decac90a3aba6b4dac09a23250e8c665bd67f75a1e1d5f4027ea724968ff45"
            }
        ]
    }
}
```

### getPeersAsync

Asynchronously obtain the network connection information of a specified node。

**Parameters**

-callback: callback after getting

**Return value**

- None

### getSyncStatus

Get Node Synchronization Status。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- SyncStatus: Blockchain node synchronization status。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSyncStatus","params":["group0"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"isSyncing\":false,\"knownHighestNumber\":7,\"knownLatestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"peers\":[{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331\"},{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258\"},{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3\"}]}\n"
}
```
### getSyncStatusAsync

Asynchronously get node synchronization status。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: callback after getting synchronization information

**Return value**

- None

### getSystemConfigByKey

Gets the value of the system configuration item based on the specified configuration keyword。

**Parameters**

-node: allows RPC to send requests to the specified node
- key: System configuration items, including 'tx _ count _ limit' and 'consensus _ leader _ period'

**Return value**

- SystemConfig: Value of System Configuration Item。

```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSystemConfigByKey","params":["group0","node0","tx_count_limit"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "blockNumber": 0,
        "value": "1000"
    }
}
```
### getSystemConfigByKeyAsync

Asynchronously gets the value of the system configuration item based on the specified configuration keyword。

**Parameters**

-node: allows RPC to send requests to the specified node
- key: System configuration items, including 'tx _ count _ limit' and 'consensus _ leader _ period'
-callback: callback after getting the configuration item

**Return value**

- None

## 3. Consensus query interface

### getObserverList

Obtain the observation node list of the group corresponding to the client。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- ObserverList: Watch Node List。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getObserverList","params":[],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        "dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3"
    ]
}
```
### getObserverListAsync

Asynchronously obtain the observation node list of the client corresponding to the group。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: callback after getting the node list

**Return value**

- None

### getSealerList

Obtain the consensus node list of the client group。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- SealerList: Consensus Node List。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSealerList","params":[],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        {
            "nodeID": "0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49",
            "weight": 1
        },
        {
            "nodeID": "2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331",
            "weight": 1
        },
        {
            "nodeID": "535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258",
            "weight": 1
        }
    ]
}
```

### getSealerListAsync

Asynchronously obtain the consensus node list of the corresponding client group。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: callback after getting the node list

**Return value**

- None

### getPbftView

Obtain PBFT view information when a node uses the PBFT consensus algorithm。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- PbftView: PBFT View Information。
  
**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPbftView","params":["group0","node0"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": 3
}
```

### getPbftViewAsync

Asynchronously obtains PBFT view information when a node uses the PBFT consensus algorithm。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: callback after obtaining PBFT view information

**Return value**

- None

### getConsensusStatus

Get Node Consensus Status。

**Parameters**

-node: allows RPC to send requests to the specified node

**Return value**

- ConsensusStatus: Node consensus state。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getConsensusStatus","params":["group0","node0"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "{\"blockNumber\":7,\"changeCycle\":0,\"connectedNodeList\":4,\"consensusNodeList\":[{\"index\":0,\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"weight\":1},{\"index\":1,\"nodeID\":\"2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331\",\"weight\":1},{\"index\":2,\"nodeID\":\"535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258\",\"weight\":1},{\"index\":3,\"nodeID\":\"dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3\",\"weight\":1}],\"consensusNodesNum\":4,\"hash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"index\":0,\"isConsensusNode\":true,\"leaderIndex\":3,\"maxFaultyQuorum\":1,\"minRequiredQuorum\":3,\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"timeout\":false,\"view\":3}\n"
}
```

### getConsensusStatusAsync

Asynchronously get node consensus state。

**Parameters**

-node: allows RPC to send requests to the specified node
-callback: callback after getting status

**Return value**

- None

## 4. Group query interface

### getGroupInfo

Query the status information of the current group。

**Parameters**

- None

**Return value**

- BcosGroupInfo: Queried group status information。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupInfo","params":["group0","node1"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "chainID": "chain0",
        "genesisConfig": "{\"blockTxCountLimit\":1000,\"consensusLeaderPeriod\":1,\"consensusType\":\"pbft\",\"sealerList\":[{\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"weight\":1},{\"nodeID\":\"2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331\",\"weight\":1},{\"nodeID\":\"535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258\",\"weight\":1},{\"nodeID\":\"dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3\",\"weight\":1}],\"txGasLimit\":3000000000}\n",
        "groupID": "group0",
        "iniConfig": "",
        "nodeList": [
            {
                "iniConfig": "{\"binaryInfo\":{\"buildTime\":\"20230403 11:55:56\",\"gitCommitHash\":\"114364de7db1ba3c0461b3813621ce7caedc4f2f\",\"platform\":\"Darwin/appleclang\",\"version\":\"3.3.0\"},\"chainID\":\"chain0\",\"gatewayServiceName\":\"\",\"groupID\":\"group0\",\"isAuthCheck\":true,\"isSerialExecute\":true,\"isWasm\":false,\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"nodeName\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"rpcServiceName\":\"\",\"smCryptoType\":true}\n",
                "microService": false,
                "name": "0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49",
                "nodeID": "0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49",
                "protocol": {
                    "compatibilityVersion": 50528256,
                    "maxSupportedVersion": 1,
                    "minSupportedVersion": 0
                },
                "serviceInfo": [
                    {
                        "serviceName": "LedgerServiceObj",
                        "type": 2
                    },
                    {
                        "serviceName": "SchedulerServiceObj",
                        "type": 3
                    },
                    {
                        "serviceName": "FrontServiceObj",
                        "type": 4
                    },
                    {
                        "serviceName": "",
                        "type": 6
                    },
                    {
                        "serviceName": "",
                        "type": 7
                    },
                    {
                        "serviceName": "TxPoolServiceObj",
                        "type": 8
                    }
                ],
                "type": 1
            }
        ]
    }
}
```
### getGroupInfoAsync

Query the status information of the current group asynchronously。

**Parameters**

-callback: callback after status information is queried

**Return value**

- None

### getGroupList

Get the list of groups for the current node。

**Parameters**

- None

**Return value**

- BcosGroupList: List of groups for the current node。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupList","params":["group0","node1"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "code": 0,
        "groupList": [
            "group0"
        ],
        "msg": "success"
    }
}
```

### getGroupListAsync

Asynchronously obtain the group list of the current node。

**Parameters**

-callback: callback after getting group list

**Return value**

- None

### getGroupPeers

Gets the list of nodes connected to the specified group of the current node。

**Parameters**

- None

**Return value**

- GroupPeers: Specify the list of nodes to which the group is connected。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupPeers","params":["group0","node1"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        "0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49",
        "2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331",
        "535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258",
        "dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3"
    ]
}
```
### getGroupPeersAsync

Asynchronously obtains the list of nodes connected to the specified group of the current node。

**Parameters**

-callback: callback after getting the node list

**Return value**

- None

### getGroupInfoList

Obtain the current node group information list。

**Parameters**

- None

**Return value**

- BcosGroupInfoList: Current node group information list。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupInfoList","params":["group0","node1"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        {
            "chainID": "chain0",
            "genesisConfig": "{\"blockTxCountLimit\":1000,\"consensusLeaderPeriod\":1,\"consensusType\":\"pbft\",\"sealerList\":[{\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"weight\":1},{\"nodeID\":\"2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331\",\"weight\":1},{\"nodeID\":\"535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258\",\"weight\":1},{\"nodeID\":\"dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3\",\"weight\":1}],\"txGasLimit\":3000000000}\n",
            "groupID": "group0",
            "iniConfig": "",
            "nodeList": [
                {
                    "iniConfig": "{\"binaryInfo\":{\"buildTime\":\"20230403 11:55:56\",\"gitCommitHash\":\"114364de7db1ba3c0461b3813621ce7caedc4f2f\",\"platform\":\"Darwin/appleclang\",\"version\":\"3.3.0\"},\"chainID\":\"chain0\",\"gatewayServiceName\":\"\",\"groupID\":\"group0\",\"isAuthCheck\":true,\"isSerialExecute\":true,\"isWasm\":false,\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"nodeName\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"rpcServiceName\":\"\",\"smCryptoType\":true}\n",
                    "microService": false,
                    "name": "0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49",
                    "nodeID": "0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49",
                    "protocol": {
                        "compatibilityVersion": 50528256,
                        "maxSupportedVersion": 1,
                        "minSupportedVersion": 0
                    },
                    "serviceInfo": [
                        {
                            "serviceName": "LedgerServiceObj",
                            "type": 2
                        },
                        {
                            "serviceName": "SchedulerServiceObj",
                            "type": 3
                        },
                        {
                            "serviceName": "FrontServiceObj",
                            "type": 4
                        },
                        {
                            "serviceName": "",
                            "type": 6
                        },
                        {
                            "serviceName": "",
                            "type": 7
                        },
                        {
                            "serviceName": "TxPoolServiceObj",
                            "type": 8
                        }
                    ],
                    "type": 1
                }
            ]
        }
    ]
}
```
### getGroupInfoListAsync

Asynchronously obtain the current node group information list。

**Parameters**

-callback: callback after getting group information

**Return value**

- None

### getGroupNodeInfo

Obtain information about a specified node in a group。

**Parameters**

- node: Specify node name

**Return value**

- BcosGroupNodeInfo: Query the obtained node information。

**Example:**
```
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupNodeInfo","params":["group0","node1"],"id":1}' http://127.0.0.1:20200

// Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": null
}
```
### getGroupNodeInfoAsync

Asynchronously obtain information about a specified node in a group。

**Parameters**

- node: Specify node name
-callback: callback after getting information

**Return value**

- None

## 5. Pre-compiled contract service interface

### 5.1 BFSService

#### mkdir

Creates a directory at the specified absolute path。

**Parameters**

-path: absolute path

**Return value**

- RetCode: Create directory results。

#### list

View the information of the specified absolute path. If it is a directory file, the meta information of all sub-resources in the directory is returned. If it is another file, the meta information of the file is returned。

**Parameters**

-path: absolute path

**Return value**

- List\<BFSPrecompiled.BfsInfo\>Returns a list of meta information for a file。

### link

Create a link file for the contract, in the absolute path / apps / directory。For example, for a contract with a contract address of 0x123456, create the contract name of Hello and the version number of v1.0, then in the BFS system, it will be created with the absolute path of / apps / Hello / v1.0

**Parameters**

-name: contract name
-version: Version name
-contractAddress: contract address
-abi: Contract ABI

**Return value**

- RetCode: Create linked file results。

### readlink

Read the real contract address pointed to by the linked file

**Parameters**

-path: absolute path

**Return value**

-String: contract address

### 5.2 ConsensusService

#### addSealer
 Add the specified node as a consensus node。

**Parameters**

-nodeId: the ID of the node added as the consensus node
-weight: Add the weight of the consensus node

**Return value**

- RetCode: Consensus Node Add Result。

```eval_rst
.. note::
    In order to ensure that the new node does not affect the consensus, the node to be added as a consensus node must establish a P2P network connection with other nodes in the group, otherwise it cannot be added as a consensus node。
```

#### addObserver

Add the specified node as an observation node。

**Parameters**

- nodeId: The ID of the node added as an observation node

**Return value**

- RetCode: Watch Node Add Results。

#### removeNode

Move the specified node out of the group。

**Parameters**

- nodeId: The node ID of the node removed from the group

**Return value**

- RetCode: Execution result of node removed from group。

### 5.3 SystemConfigService

#### setValueByKey

Sets the value of the specified system configuration item。

**Parameters**

- key: Configuration item. Currently, 'tx _ count _ limit' and 'consensus _ leader _ period' are supported；

- value: The value to which the system configuration item is set。

**Return value**

- RetCode: System Configuration Item Setting Results。

### 5.4 KVTableService

#### createTable

Create User Table。

**Parameters**

- tableName: Name of the created user table;
- keyFieldName: Primary key name of the user table;
- valueFields: The fields of the user table

**Return value**

- RetCode: User Table Creation Results。

#### set

Write a record to the specified user table。

**Parameters**

- kvTablePrecompiled: Name of the table in which the record needs to be inserted;
- key: The value to which the primary key is set;
- fieldNameToValue: Mapping of each field to its corresponding value。

**Return value**

- RetCode: Whether the record is inserted successfully。

#### get

Query specified records in the user table。

**Parameters**

- tableName: Queried user table name;
- key: the primary key value to be queried;

**Return value**

- String: Query Results。     

#### desc

Obtain the description information of the specified user table。

**Parameters**

- tableName: Queried user table name。

**Return value**

- Map<String, String>: Description of the user table. The mapping between 'PrecompiledConstant.KEY _ NAME' and the mapping between 'PrecompiledConstant.FIELD _ NAME' and all fields. The fields are separated by commas。

#### asyncSet

'set 'asynchronous interface, writes the specified record to the specified table, and calls the specified callback function after receiving the receipt of the node。

**Parameters**

- tableName: Name of the table in which the record needs to be inserted;
- key: The value to which the primary key is set;
- fieldNameToValue: Mapping of each field to its corresponding value;
- callback: callback function。

**Return value**

- None

### 5.5 CNSService

**Note:** Starting with version 3.0.0-rc3, CNS is no longer supported。Please refer to the BFSService link function for the corresponding contract alias function。

## 6. AuthManager Rights Management Interface

Rights management interfaces include the following three interfaces:

- Query interface without permission；
- Governance Committee-specific interface: an interface that has the private key of the governance committee to initiate transactions in order to execute correctly；
- Administrator-specific interface: an interface where transactions initiated by an administrator's private key with administrative rights to the corresponding contract can be executed correctly。

### 6.1 Query interface without permission

#### getCommitteeInfo

At initialization, a governance committee is deployed whose address information is automatically generated or specified at build _ chain.sh。Initialize only one member, and the weight of the member is 1。

**Parameters**

- None

**Return value**

- CommitteeInfo: governance committee details

#### getProposalInfo

Get information about a specific proposal。

**Parameters**

-proposalID: ID number of the proposal

**Return value**

- ProposalInfo: details of the proposal

#### getDeployAuthType

Get the permissions policy for the current global deployment

**Parameters**

- None

**Return value**

-BigInteger: Policy type: 0 is no policy, 1 is whitelist mode, 2 is blacklist mode

#### checkDeployAuth

Check whether an account has deployment permissions

**Parameters**

-account: account address

**Return value**

-Boolean: Permission

#### checkMethodAuth

Check whether an account has the permission to call an interface of a contract

**Parameters**

-contractAddr: contract address
-func: function selector for the interface, 4 bytes
-account: account address

**Return value**

-Boolean: Permission

#### getAdmin

Get the administrator address for a specific contract

**Parameters**

-contractAddr: contract address

**Return value**

-account: account address

### 6.2 Special interface for accounts of governance committee members

There must be an account in the Governance Committee's Governors to call, and if there is only one Governance Committee member and the proposal was initiated by that member, then the proposal will be successful。

#### updateGovernor

In the case of a new governance committee, add an address and weight。If you are deleting a governance member, you can set the weight of a governance member to 0。

**Parameters**

-account: account address
-weight: account weight

**Return value**

-proposalId: Returns the ID number of the proposal

#### setRate

Set proposal threshold, which is divided into participation threshold and weight threshold。

**Parameters**

-participatesRate: participation threshold, percentage unit
-winRate: by weight threshold, percentage unit

**Return value**

-proposalId: Returns the ID number of the proposal

#### setDeployAuthType

Set the ACL policy for deployment. Only white _ list and black _ list policies are supported

**Parameters**

-deployAuthType: When type is 1, it is set as a white list, and when type is 2, it is set as a black list。

**Return value**

-proposalId: Returns the ID number of the proposal

#### modifyDeployAuth

Modify a deployment permission proposal for an administrator account

**Parameters**

-account: account address
-openFlag: whether to turn permissions on or off

**Return value**

-proposalId: Returns the ID number of the proposal

#### resetAdmin

Resetting an administrator account proposal for a contract

**Parameters**

-newAdmin: Account address
-contractAddr: contract address

**Return value**

-proposalId: Returns the ID number of the proposal

#### revokeProposal

Undo the initiation of a proposal, an operation that only the governance committee that initiated the proposal can operate

**Parameters**

-proposalId: ID number of the proposal

**Return value**

- TransactionReceipt: execute receipt

#### voteProposal

vote on a proposal

**Parameters**

-proposalId: ID number of the proposal
-agree: Do you agree to this proposal?

**Return value**

- TransactionReceipt: execute receipt

### 6.3 Special interface for contract administrator account

Each contract has an independent administrator. Only the administrator account of a contract can set the interface permissions of the contract。

#### setMethodAuthType

Set the API call ACL policy of a contract. Only white _ list and black _ list policies are supported

**Parameters**

-contractAddr: contract address
-func: function selector for contract interface, length is four bytes。
-authType: When type is 1, it is set as a white list, and when type is 2, it is set as a black list。

**Return value**

-result: If it is 0, the setting is successful。

#### setMethodAuth

Modify the interface call ACL policy of a contract。

**Parameters**

-contractAddr: contract address
-func: function selector for contract interface, length is four bytes。
-account: account address
-isOpen: whether the permission is enabled or disabled

**Return value**

-result: If it is 0, the setting is successful。
