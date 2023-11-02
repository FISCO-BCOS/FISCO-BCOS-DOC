# 2. 区块链RPC接口

标签：``RPC``

---------
Java SDK为区块链应用开发者提供了Java API接口，按照功能，Java API可分为如下几类：

- Client: 提供访问FISCO BCOS 3.x节点JSON-RPC接口支持、提供部署及调用合约的支持；
- Precompiled: 提供调用FISCO BCOS 3.x Precompiled合约(预编译合约)的接口，主要包括`ConsensusService`、`SystemConfigService`、`BFSService`、`KVTableService`。
- AuthManager: 提供FISCO BCOS 3.x 权限控制预部署合约的调用。

```eval_rst
.. note::
    - Client接口声明位于 `Client.java` 文件中
```

**特别注意：**
**1. Client接口均有两种，一种是带有node的接口，另一种是不带node的接口。带有node的接口可以让节点RPC发送请求到指定已连接的节点。如果不指定，节点RPC则会随机发送请求到节点。**
**2. 以下接口示例，都是关闭了节点ssl通信的前提下。若要关闭节点的ssl通信配置项，请将节点配置文件`config.ini`的 `[rpc]` 项设置为 `disable_ssl=true`。**
## 1. 合约操作接口

### sendTransaction

发送交易到区块链。

**参数**

- node：可让RPC发送请求到指定节点
- signedTransactionData：签名后的交易
- withProof：返回是否带上默克尔树证明

**返回值**

- BcosTransactionReceipt: 节点收到交易后，回复给SDK的回包，包括交易哈希信息。

**示例：**
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

交易发布异步接口, 收到节点的响应之后，调用指定的callback。

**参数**

- node：可让RPC发送请求到指定节点
- signedTransactionData: 签名后的交易字符串;
- withProof：返回是否带上默克尔树证明
- callback: SDK收到节点的回包后，调用的回调函数，回调函数时将会带上交易回执。

**返回值**

- 无

### call

向节点发送请求，调用合约常量接口。

**参数**

- node：可让RPC发送请求到指定节点
- transaction: 合约调用信息，包含合约地址、合约调用者以及调用的合约接口和参数的abi编码

**返回值**

- Call: 合约常量接口的返回结果，包括当前块高、接口执行状态信息以及接口执行结果

**示例：**
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

合约常量接口异步调用，接收到节点返回的合约接口执行结果后，执行指定的回调函数

**参数**

- node：可让RPC发送请求到指定节点
- transaction: 合约调用信息，包含合约地址、合约调用者以及调用的接口和参数信息；
- callback: 回调函数。

**返回值**

- 无

### getCode

查询指定合约地址对应的合约代码信息。

**参数**

- node：可让RPC发送请求到指定节点
- address: 合约地址。

**返回值**

- Code: 合约地址对应的合约代码。

**示例：**
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
## 2. 区块链查询接口

### getBlockNumber

获取Client对象对应的群组最新块高。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- BlockNumber: Client对象对应的群组最新区块高度。

**示例：**
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

异步获取Client对象对应的群组最新块高。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取块高后的回调

**返回值**

- 无

### getTotalTransactionCount

获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- TotalTransactionCount: 交易统计信息，包括：
    - txSum: 上链的交易总量
    - blockNumber: 群组的当前区块高度
    - failedTxSum: 上链执行异常的交易总量

**示例：**
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

异步获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取交易信息后的回调

**返回值**

- 无

### getBlockByNumber

根据区块高度获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点；
- blockNumber: 区块高度；
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash：true/false，表明获取的区块信息中是否包含完整的交易信息；
  - false: 节点返回的区块中包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希。

**返回值**

- BcosBlock: 查询获取的区块信息

**示例：**
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

根据区块高度异步获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点
- blockNumber: 区块高度；
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；
  - false: 节点返回的区块中包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希；
- callback：获取区块完成后的回调。

**返回值**

- 无

### getBlockByHash

根据区块哈希获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点
- blockHash: 区块哈希
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；
    - true: 节点返回的区块中仅包含交易哈希;
    - false: 节点返回的区块中包含完整的交易信息。

**返回值**

- BcosBlock: 查询获取的区块信息。

**示例：**
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

根据区块哈希异步获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点
- blockHash: 区块哈希
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希;
  - false: 节点返回的区块中包含完整的交易信息；
- callback：获取区块完成后的回调。

**返回值**

- 无

### getBlockHashByNumber

根据区块高度获取区块哈希

**参数**

- node：可让RPC发送请求到指定节点
- blockNumber: 区块高度

**返回值**

- BlockHash: 指定区块高度对应的区块哈希

**示例：**
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

根据区块高度异步获取区块哈希

**参数**

- node：可让RPC发送请求到指定节点
- blockNumber: 区块高度
- callback：获取之后的回调

**返回值**

- 无

### getTransaction

根据交易哈希获取交易信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：是否带上默克尔树证明

**返回值**

- BcosTransaction: 指定哈希对应的交易信息。

### getTransactionAsync

根据交易哈希异步获取交易信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：是否带上默克尔树证明
- callback：获取到交易时的回调

**返回值**

- 无

### getTransactionReceipt

根据交易哈希获取交易回执信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：返回是否带上默克尔树证明

**返回值**

- BcosTransactionReceipt: 交易哈希对应的回执信息。

**示例：**
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

根据交易哈希异步获取交易回执信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：返回是否带上默克尔树证明
- callback：获取交易回执时的回调

**返回值**

- 无

### getPendingTxSize

获取交易池内未处理的交易数目。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- PendingTxSize: 交易池内未处理的交易数目。

**示例：**
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

获取交易池内未处理的交易数目。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取交易回执时的回调

**返回值**

- 无

### getBlockLimit
获取Client对应群组的BlockLimit，BlockLimit主要用于交易防重。

**参数**

- 无

**返回值**

- BigInteger: 群组的BlockLimit。

### getPeers

获取指定节点的网络连接信息。

**参数**

- 无

**返回值**

- Peers: 指定节点的网络连接信息。

**示例：**
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

异步获取指定节点的网络连接信息。

**参数**

- callback：获取之后的回调

**返回值**

- 无

### getSyncStatus

获取节点同步状态。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- SyncStatus: 区块链节点同步状态。

**示例：**
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

异步获取节点同步状态。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取同步信息之后的回调

**返回值**

- 无

### getSystemConfigByKey

根据指定配置关键字获取系统配置项的值。

**参数**

- node：可让RPC发送请求到指定节点
- key: 系统配置项，目前包括`tx_count_limit`, `consensus_leader_period`.

**返回值**

- SystemConfig: 系统配置项的值。

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

根据指定配置关键字异步获取系统配置项的值。

**参数**

- node：可让RPC发送请求到指定节点
- key: 系统配置项，目前包括`tx_count_limit`, `consensus_leader_period`.
- callback：获取配置项之后的回调

**返回值**

- 无

## 3. 共识查询接口

### getObserverList

获取Client对应群组的观察节点列表。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- ObserverList: 观察节点列表。

**示例：**
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
### getObserverList

异步获取Client对应群组的观察节点列表。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取节点列表之后的回调

**返回值**

- 无

### getSealerList

获取Client对应群组的共识节点列表。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- SealerList: 共识节点列表。

**示例：**
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

异步获取Client对应群组的共识节点列表。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取节点列表之后的回调

**返回值**

- 无

### getPbftView

节点使用PBFT共识算法时，获取PBFT视图信息。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- PbftView: PBFT视图信息。
  
**示例：**
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

节点使用PBFT共识算法时，异步获取PBFT视图信息。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取PBFT视图信息之后的回调

**返回值**

- 无

### getConsensusStatus

获取节点共识状态。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- ConsensusStatus: 节点共识状态。

**示例：**
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

异步获取节点共识状态。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取状态后的回调

**返回值**

- 无

## 4. 群组查询接口

### getGroupInfo

查询当前群组的状态信息。

**参数**

- 无

**返回值**

- BcosGroupInfo: 被查询的群组状态信息。

**示例：**
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

异步查询当前群组的状态信息。

**参数**

- callback：查询到状态信息之后的回调

**返回值**

- 无

### getGroupList

获取当前节点的群组列表。

**参数**

- 无

**返回值**

- BcosGroupList: 当前节点的群组列表。

**示例：**
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

异步获取当前节点的群组列表。

**参数**

- callback：获取群组列表之后的回调

**返回值**

- 无

### getGroupPeers

获取当前节点指定群组连接的节点列表。

**参数**

- 无

**返回值**

- GroupPeers: 指定群组连接的节点列表。

**示例：**
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

异步获取当前节点指定群组连接的节点列表。

**参数**

- callback：获取节点列表之后的回调

**返回值**

- 无

### getGroupInfoList

获取当前节点群组信息列表。

**参数**

- 无

**返回值**

- BcosGroupInfoList: 当前节点群组信息列表。

**示例：**
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

异步获取当前节点群组信息列表。

**参数**

- callback：获取群组信息之后的回调

**返回值**

- 无

### getGroupNodeInfo

获取群组内指定节点的信息。

**参数**

- node: 指定节点名

**返回值**

- BcosGroupNodeInfo: 查询获取的节点信息。

**示例：**
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

异步获取群组内指定节点的信息。

**参数**

- node: 指定节点名
- callback：获取信息后的回调

**返回值**

- 无

## 5. 预编译合约服务接口

### 5.1 BFSService

#### mkdir

在指定的绝对路径下创建目录。

**参数**

- path：绝对路径

**返回值**

- RetCode：创建目录结果。

#### list

查看指定的绝对路径的信息，如果是目录文件，则返回目录下所有子资源元信息，如果是其他文件，则返回文件的元信息。

**参数**

- path：绝对路径

**返回值**

- List\<BFSPrecompiled.BfsInfo\>：返回文件的元信息列表。

### link

创建合约的链接文件，在绝对路径 /apps/ 目录下创建。例如，对于合约地址0x123456的合约创建Hello的合约名，以及v1.0的版本号，那么在BFS系统中，将会以/apps/Hello/v1.0的绝对路径创建

**参数**

- name：合约名
- version：版本名
- contractAddress：合约地址
- abi：合约ABI

**返回值**

- RetCode：创建链接文件结果。

### readlink

读取链接文件指向的真实合约地址

**参数**

- path：绝对路径

**返回值**

- String：合约地址

### 5.2 ConsensusService

#### addSealer
 将指定节点添加为共识节点。

**参数**

- nodeId：被添加为共识节点的node ID.
- weight：添加共识节点的权重

**返回值**

- RetCode: 共识节点添加结果。

```eval_rst
.. note::
    为了保证新节点加入不影响共识，即将被添加为共识节点的节点必须与群组内其他节点建立P2P网络连接，否则其无法被添加为共识节点。
```

#### addObserver

将指定节点添加为观察节点。

**参数**

- nodeId: 被添加为观察节点的node ID.

**返回值**

- RetCode: 观察节点添加结果。

#### removeNode

将指定节点移出群组。

**参数**

- nodeId: 被移出群组的节点的node ID.

**返回值**

- RetCode: 节点被移出群组的执行结果。

### 5.3 SystemConfigService

#### setValueByKey

设置指定系统配置项的值。

**参数**

- key: 配置项，目前支持`tx_count_limit`, `consensus_leader_period`；

- value: 系统配置项被设置的值。

**返回值**

- RetCode: 系统配置项设置结果。

### 5.4 KVTableService

#### createTable

创建用户表。

**参数**

- tableName: 创建的用户表名;
- keyFieldName: 用户表的主key名;
- valueFields: 用户表的fields.

**返回值**

- RetCode: 用户表创建结果。

#### set

向指定用户表中写入一条记录。

**参数**

- kvTablePrecompiled: 需要插入记录的表名;
- key: 主key被设置的值;
- fieldNameToValue: 每个field到其对应值的映射。

**返回值**

- RetCode: 记录是否插入成功。

#### get

查询用户表指定记录。

**参数**

- tableName: 被查询的用户表名;
- key: 被查询的主key值;

**返回值**

- String: 查询结果。     

#### desc

获取指定用户表的描述信息。

**参数**

- tableName: 被查询的用户表名。

**返回值**

- Map<String, String>: 用户表描述信息，记录了`PrecompiledConstant.KEY_NAME`到主key的映射，以及`PrecompiledConstant.FIELD_NAME`到所有field的映射，field之间用逗号分隔开。

#### asyncSet

`set`的异步接口，向指定表写入指定记录，并在接收到节点的回执后，调用指定回调函数。

**参数**

- tableName: 需要插入记录的表名;
- key: 主key被设置的值;
- fieldNameToValue: 每个field到其对应值的映射;
- callback: 回调函数。

**返回值**

- 无      

### 5.5 CNSService

**注意：** 从3.0.0-rc3版本开始，不再支持CNS。相应的合约别名功能请参考BFSService link功能。

## 6. AuthManager权限管理接口

权限管理接口包含以下三种接口：

- 无需权限的查询接口；
- 治理委员会专用接口：拥有治理委员会的私钥发起的交易才能正确执行的接口；
- 管理员专用接口：拥有相应合约的管理权限的管理员私钥发起的交易才能被正确执行的接口。

### 6.1 无需权限的查询接口

#### getCommitteeInfo

在初始化时，将会部署一个治理委员，该治理委员的地址信息在 build_chain.sh时自动生成或者指定。初始化只有一个委员，并且委员的权重为1。

**参数**

- 无

**返回值**

- CommitteeInfo：治理委员会的详细信息

#### getProposalInfo

获取某个特定的提案信息。

**参数**

- proposalID：提案的ID号

**返回值**

- ProposalInfo：提案的详细信息

#### getDeployAuthType

获取当前全局部署的权限策略

**参数**

- 无

**返回值**

- BigInteger：策略类型：0则无策略，1则为白名单模式，2则为黑名单模式

#### checkDeployAuth

检查某个账号是否有部署权限

**参数**

- account：账号地址

**返回值**

- Boolean：是否有权限

#### checkMethodAuth

检查某个账号是否有某个合约的一个接口的调用权限

**参数**

- contractAddr：合约地址
- func：接口的函数选择器，为4个字节
- account：账号地址

**返回值**

- Boolean：是否有权限

#### getAdmin

获取特定合约的管理员地址

**参数**

- contractAddr：合约地址

**返回值**

- account：账号地址

### 6.2 治理委员账号专用接口

必须要有治理委员会的Governors中的账户才可以调用，如果治理委员只有一个，且提案是该委员发起的，那么这个提案一定能成功。

#### updateGovernor

如果是新加治理委员，新增地址和权重即可。如果是删除治理委员，将一个治理委员的权重设置为0 即可。

**参数**

- account：账号地址
- weight：账号权重

**返回值**

- proposalId：返回提案的ID号

#### setRate

设置提案阈值，提案阈值分为参与阈值和权重阈值。

**参数**

- participatesRate：参与阈值，百分比单位
- winRate：通过权重阈值，百分比单位

**返回值**

- proposalId：返回提案的ID号

#### setDeployAuthType

设置部署的ACL策略，只支持 white_list 和 black_list 两种策略

**参数**

- deployAuthType：type为1时，设置为白名单，type为2时，设置为黑名单。

**返回值**

- proposalId：返回提案的ID号

#### modifyDeployAuth

修改某个管理员账户的部署权限提案

**参数**

- account：账号地址
- openFlag：是开启权限还是关闭权限

**返回值**

- proposalId：返回提案的ID号

#### resetAdmin

重置某个合约的管理员账号提案

**参数**

- newAdmin：账号地址
- contractAddr：合约地址

**返回值**

- proposalId：返回提案的ID号

#### revokeProposal

撤销提案的发起，该操作只有发起提案的治理委员才能操作

**参数**

- proposalId：提案的ID号

**返回值**

- TransactionReceipt：执行回执

#### voteProposal

向某个提案进行投票

**参数**

- proposalId：提案的ID号
- agree：是否同意这个提案

**返回值**

- TransactionReceipt：执行回执

### 6.3 合约管理员账号专用接口

每个合约都有独立的管理员，持有某个合约的管理员账号才能对合约的接口权限进行设置。

#### setMethodAuthType

设置某个合约的接口调用ACL策略，只支持 white_list 和 black_list 两种策略

**参数**

- contractAddr：合约地址
- func：合约接口的函数选择器，长度是四个字节。
- authType：type为1时，设置为白名单，type为2时，设置为黑名单。

**返回值**

- result：如果是0则设置成功。

#### setMethodAuth

修改某个合约的接口调用ACL策略。

**参数**

- contractAddr：合约地址
- func：合约接口的函数选择器，长度是四个字节。
- account：账号地址
- isOpen：是开启权限，还是关闭权限

**返回值**

- result：如果是0则设置成功。
