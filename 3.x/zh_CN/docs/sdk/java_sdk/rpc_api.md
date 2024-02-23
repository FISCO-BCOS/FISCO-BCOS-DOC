# RPC接口说明

标签：``RPC`` ``Interface``

---------

Java SDK为区块链应用开发者提供了Java API接口，其中JSON-RPC接口的封装在Client类中，提供访问FISCO BCOS 3.x节点JSON-RPC接口支持、提供部署及调用合约的支持。

```eval_rst
.. note::
    - Client接口声明位于 `Client.java` 文件中
    - Client是群组维度的对象，可参考 `快速入门 <quick_start.html>`_ 初始化Client，初始化Client时，必须传入群组名
```

**特别注意：Client接口均有两种，一种是带有node的接口，另一种是不带node的接口。带有node的接口可以让节点RPC发送请求到指定已连接的节点。如果不指定，节点RPC则会随机发送请求到节点。**

**此外，Client对于每个接口都提供同步和异步的接口，开发者可以根据方法名是否以Asyn结尾，或者具有Callback回调参数辨别。下面的接口距离均以异步、指定node的接口为例。**

**curl调用说明：节点的rpc接口访问默认开启ssl认证，下面使用curl发送接口命令没有ssl证书，需要关闭节点的rpc接口ssl认证。关闭方法是修改配置文件/fisco/nodes/127.0.0.1/ node0/config.ini, 修改配置文件后重新启动节点即可**

```
[rpc]
 ; ssl connection switch, if disable the ssl connection, default: false
 disable_ssl=true
```

## 1. 合约操作接口

下面的接口距离均以异步、指定node的接口为例。

### 1.1 sendTransactionAsync

交易发布异步接口, 收到节点的响应之后，调用指定的callback。

**参数**

- node：可让RPC发送请求到指定节点
- signedTransactionData：签名后的交易
- withProof：返回是否带上默克尔树证明
- callback: SDK收到节点的回包后，调用的回调函数，回调函数时将会带上交易回执。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"sendTransaction","params":["group0","","0x1a1c2606636861696e30360667726f7570304101fb564d36323332373833373230383636323235323233313231343039373038383134363030353536383536313037383031313639373432363032343636323131353337373138313837323836303337397d0001046b608060405234801561001057600080fd5b5060408051808201909152600d8082526c48656c6c6f2c20576f726c642160981b60209092019182526100459160009161004b565b5061011f565b828054610057906100e4565b90600052602060002090601f01602090048101928261007957600085556100bf565b82601f1061009257805160ff19168380011785556100bf565b828001600101855582156100bf579182015b828111156100bf5782518255916020019190600101906100a4565b506100cb9291506100cf565b5090565b5b808211156100cb57600081556001016100d0565b600181811c908216806100f857607f821691505b602082108114156101195763b95aa35560e01b600052602260045260246000fd5b50919050565b61033d8061012e6000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063299f7f9d1461003b5780633590b49f14610059575b600080fd5b61004361006e565b60405161005091906101b0565b60405180910390f35b61006c61006736600461021b565b610100565b005b60606000805461007d906102cc565b80601f01602080910402602001604051908101604052809291908181526020018280546100a9906102cc565b80156100f65780601f106100cb576101008083540402835291602001916100f6565b820191906000526020600020905b8154815290600101906020018083116100d957829003601f168201915b5050505050905090565b8051610113906000906020840190610117565b5050565b828054610123906102cc565b90600052602060002090601f016020900481019282610145576000855561018b565b82601f1061015e57805160ff191683800117855561018b565b8280016001018555821561018b579182015b8281111561018b578251825591602001919060010190610170565b5061019792915061019b565b5090565b5b80821115610197576000815560010161019c565b600060208083528351808285015260005b818110156101dd578581018301518582016040015282016101c1565b818111156101ef576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561022d57600080fd5b813567ffffffffffffffff8082111561024557600080fd5b818401915084601f83011261025957600080fd5b81358181111561026b5761026b610205565b604051601f8201601f19908116603f0116810190838211818310171561029357610293610205565b816040528281528760208487010111156102ac57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600181811c908216806102e057607f821691505b602082108114156103015763b95aa35560e01b600052602260045260246000fd5b5091905056fea2646970667358221220ad3331f4f52a10ab9c50f2e63a46fd49fab3847ff4e17912290db8f009f89c9464736f6c634300080b003387000001565b7b22696e70757473223a5b5d2c2273746174654d75746162696c697479223a226e6f6e70617961626c65222c2274797065223a22636f6e7374727563746f72227d2c7b22696e70757473223a5b5d2c226e616d65223a22676574222c226f757470757473223a5b7b22696e7465726e616c54797065223a22737472696e67222c226e616d65223a22222c2274797065223a22737472696e67227d5d2c2273746174654d75746162696c697479223a2276696577222c2274797065223a2266756e6374696f6e227d2c7b22696e70757473223a5b7b22696e7465726e616c54797065223a22737472696e67222c226e616d65223a226e222c2274797065223a22737472696e67227d5d2c226e616d65223a22736574222c226f757470757473223a5b5d2c2273746174654d75746162696c697479223a226e6f6e70617961626c65222c2274797065223a2266756e6374696f6e227d5d0b2d000020867fc7059f04e9f172202b777673a8413c9e47990dcd09b11311a2f1f5b55a4f3d0001008015e0c4a3a51b5b5b157502aae04f3905bae8daf1389bddf12b0eb7069ead63b76d42383dcd38520df339d571d37ea85334dba126edcc323a98b91e51d32ec074b5f7430ea78c64ccc8d364bf4563dbffb33be503344b72f3384f987c38af98db3f0f00169f2a6545d0920a1a6cb7f338b8b717f03d05fded80ddbbb171a099c1"，true],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x1"
}
```

### 1.2 callAsync

向节点发送请求，调用合约常量接口。

**参数**

- node：可让RPC发送请求到指定节点
- transaction: 合约调用信息，包含合约地址、合约调用者以及调用的合约接口和参数的abi编码
- sign：对(合约地址、调用参数)的byte拼接后的哈希进行的签名，在链上可以恢复出签名对应的用户地址，该参数的接口只在节点3.4.0版本以后可用。
- callback: 回调函数，将返回合约常量接口的返回结果，包括当前块高、接口执行状态信息以及接口执行结果

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"call","params":["group0","","0xc0523dbdd94ba27e14b0336d799489340ca24cdf","aaaa"],"id":1}' http://127.0.0.1:20200

# Result
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

注意：在3.4.0版本以后，支持Call with sign接口，支持在发起static call请求时使用私钥对请求体(to+data)进行签名，在节点侧将会对应恢复出签名对应的用户地址，合约中可以取到call请求时的tx.origin和msg.sender，达到用户身份认证的目的。

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"call","params":["group0","","0xc0523dbdd94ba27e14b0336d799489340ca24cdf","aaaa", "0x"],"id":1}' http://127.0.0.1:20200

# Result
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

### 1.3 getCodeAsync

异步查询指定合约地址对应的合约代码信息。

**参数**

- node：可让RPC发送请求到指定节点
- address: 合约地址
- callback：回调函数，包含合约地址对应的合约代码。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getCode","params":["group0","node0","0x41a1281dba209614f2ada8ecc75fd957ad179d7b"],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x608060405234801561001057600080fd5b50600436106100365760003560e01c8063299f7f9d1461003b5780633590b49f14610059575b600080fd5b61004361006e565b60405161005091906101b0565b60405180910390f35b61006c61006736600461021b565b610100565b005b60606000805461007d906102cc565b80601f01602080910402602001604051908101604052809291908181526020018280546100a9906102cc565b80156100f65780601f106100cb576101008083540402835291602001916100f6565b820191906000526020600020905b8154815290600101906020018083116100d957829003601f168201915b5050505050905090565b8051610113906000906020840190610117565b5050565b828054610123906102cc565b90600052602060002090601f016020900481019282610145576000855561018b565b82601f1061015e57805160ff191683800117855561018b565b8280016001018555821561018b579182015b8281111561018b578251825591602001919060010190610170565b5061019792915061019b565b5090565b5b80821115610197576000815560010161019c565b600060208083528351808285015260005b818110156101dd578581018301518582016040015282016101c1565b818111156101ef576000604083870101525b50601f01601f1916929092016040019392505050565b63b95aa35560e01b600052604160045260246000fd5b60006020828403121561022d57600080fd5b813567ffffffffffffffff8082111561024557600080fd5b818401915084601f83011261025957600080fd5b81358181111561026b5761026b610205565b604051601f8201601f19908116603f0116810190838211818310171561029357610293610205565b816040528281528760208487010111156102ac57600080fd5b826020860160208301376000928101602001929092525095945050505050565b600181811c908216806102e057607f821691505b602082108114156103015763b95aa35560e01b600052602260045260246000fd5b5091905056fea2646970667358221220ad3331f4f52a10ab9c50f2e63a46fd49fab3847ff4e17912290db8f009f89c9464736f6c634300080b0033"
}
```
### 1.4 getABIAsync

异步查询指定合约地址对应的合约ABI信息。

**参数**

- node：可让RPC发送请求到指定节点
- address: 合约地址
- callback：回调函数，合约地址对应的合约ABI JSON。

**返回值**

- 无
  

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getABI","params":["group0","","0x41a1281dba209614f2ada8ecc75fd957ad179d7b"],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "[{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"internalType\":\"string\",\"name\":\"\",\"type\":\"string\"}],\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[{\"internalType\":\"string\",\"name\":\"n\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]"
}
```

## 2. 区块链查询接口

下面的接口距离均以异步、指定node的接口为例。

### 2.1 getBlockNumberAsync

获取Client对象对应的群组最新块高。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取块高后的回调，Client对象对应的群组最新区块高度。

**返回值**

- 无


**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockNumber","params":[],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": 8
}
```

### 2.2 getTotalTransactionCountAsync

获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取交易信息后的回调，TotalTransactionCount: 交易统计信息，包括：
  - txSum: 上链的交易总量
  - blockNumber: 群组的当前区块高度
  - failedTxSum: 上链执行异常的交易总量

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTotalTransactionCount","params":[],"id":1}' http://127.0.0.1:20200

# Result
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

### 2.3 getBlockByNumberAsync

根据区块高度获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点；
- blockNumber: 区块高度；
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash：true/false，表明获取的区块信息中是否包含完整的交易信息；
  - false: 节点返回的区块中包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希。
- callback：获取区块完成后的回调，查询获取的区块信息

**返回值**

- 无
  

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByNumber","params":["group0","",4,false,false],"id":1}' http://127.0.0.1:20200

# Result
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

### 2.4 getBlockByHashAsync

根据区块哈希获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点
- blockHash: 区块哈希
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希;
  - false: 节点返回的区块中包含完整的交易信息。
- callback：获取区块完成后的回调，查询获取的区块信息

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockByHash","params":["group0","","0x2fbafbf71395bb07d1d6e142a06fa3cd9436aee3e91b5b9e6ffc5c47133c3738",true,true],"id":1}' http://127.0.0.1:20200

# Result
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
### 2.5 getBlockHashByNumberAsync

根据区块高度获取区块哈希

**参数**

- node：可让RPC发送请求到指定节点
- blockNumber: 区块高度
- callback：获取之后的回调，指定区块高度对应的区块哈希

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getBlockHashByNumber","params":["group0","",0],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "0x47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5"
}
```

### 2.6 getTransactionAsync

根据交易哈希获取交易信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：是否带上默克尔树证明
- callback：获取到交易时的回调，指定哈希对应的交易信息。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransaction","params":["group0","","0x51a375e5cf28b9c810734875c143d7456aa80911e02eab32a8b7ba1b789e5888",true],"id":1}' http://127.0.0.1:20200
# Response
{
	"id" : 1,
	"jsonrpc" : "2.0",
	"result" :
	{
		"abi" : "",
		"blockLimit" : 525,
		"chainID" : "chain0",
		"extraData" : "",
		"from" : "0x6e6057d5d09257d38299fa36edc7d8e9e78cbe7d",
		"groupID" : "group0",
		"hash" : "0x51a375e5cf28b9c810734875c143d7456aa80911e02eab32a8b7ba1b789e5888",
		"importTime" : 1706709904483,
		"input" : "0x4ed3885e000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000",
		"nonce" : "323131333232363538383034323639333631393735343837383035373733393836383439",
		"signature" : "0x75ee993f9c81aa9547eff99d96b5ca44b891eb4efd08cdeb3b99c971d9c2ab7237143aad55aa4cc70c7af706c11efa4459661121265ebfdf15498623559edef701",
		"to" : "0x41a1281dba209614f2ada8ecc75fd957ad179d7b",
		"transactionProof" : [],
		"txProof" :
		[
			"51a375e5cf28b9c810734875c143d7456aa80911e02eab32a8b7ba1b789e5888"
		],
		"version" : 0
	}
}%
```



### 2.7 getTransactionReceiptAsync

根据交易哈希获取交易回执信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：返回是否带上默克尔树证明
- callback：获取交易回执时的回调，BcosTransactionReceipt: 交易哈希对应的回执信息。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getTransactionReceipt","params":["group0","node0","0x51a375e5cf28b9c810734875c143d7456aa80911e02eab32a8b7ba1b789e5888",true],"id":1}' http://127.0.0.1:20200

# Response
{
	"id" : 1,
	"jsonrpc" : "2.0",
	"result" :
	{
		"blockNumber" : 26,
		"checksumContractAddress" : "",
		"contractAddress" : "",
		"extraData" : "",
		"from" : "0x6e6057d5d09257d38299fa36edc7d8e9e78cbe7d",
		"gasUsed" : "13088",
		"hash" : "0x3949c140dc53a7e313d0b8262d4f28b4bf1908a78f7098f1a16bcbc78f631a41",
		"input" : "0x4ed3885e000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000",
		"logEntries" : [],
		"message" : "",
		"output" : "0x",
		"receiptProof" : [],
		"status" : 0,
		"to" : "0x41a1281dba209614f2ada8ecc75fd957ad179d7b",
		"transactionHash" : "0x51a375e5cf28b9c810734875c143d7456aa80911e02eab32a8b7ba1b789e5888",
		"transactionProof" : [],
		"txReceiptProof" :
		[
			"3949c140dc53a7e313d0b8262d4f28b4bf1908a78f7098f1a16bcbc78f631a41"
		],
		"version" : 0
	}
}%
```

### 2.8 getPendingTxSizeAsync

获取交易池内未处理的交易数目。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取交易回执时的回调，PendingTxSize: 交易池内未处理的交易数目。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPendingTxSize","params":["group0",""],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": 0
}
```

### 2.9 getBlockLimit

获取Client对应群组的BlockLimit，BlockLimit主要用于交易防重。

**参数**

- 无

**返回值**

- BigInteger: 群组的BlockLimit。

### 2.10 getPeersAsync

获取指定节点的网络连接信息。

**参数**

- callback：获取之后的回调，Peers: 指定节点的网络连接信息。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[""],"id":1}' http://127.0.0.1:20200

# Result
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
### 2.11 getSyncStatusAsync

获取节点同步状态。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取同步信息之后的回调 ，SyncStatus: 区块链节点同步状态。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSyncStatus","params":["group0"],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"isSyncing\":false,\"knownHighestNumber\":7,\"knownLatestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"peers\":[{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331\"},{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258\"},{\"archivedBlockNumber\":0,\"blockNumber\":7,\"genesisHash\":\"47c6d9493171f62bf7201faa2e4766bbf058d95c0d76d605f965ff4b212e01f5\",\"latestHash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"nodeID\":\"dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3\"}]}\n"
}
```

### 2.12 getSystemConfigByKeyAsync

根据指定配置关键字获取系统配置项的值。

**参数**

- node：可让RPC发送请求到指定节点
- key: 系统配置项
- callback：获取配置项之后的回调，SystemConfig: 系统配置项的值。

**返回值**

- 无
  

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSystemConfigByKey","params":["group0","","tx_count_limit"],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": {
        "blockNumber": 0,
        "value": "1000"
    }
}
```

### 2.13 getChainCompatibilityVersionAsync

获取当前区块链的数据版本号。

**参数**

- callback：EnumNodeVersion.Version，区块链的数据版本号

**返回值**

- 无

## 3. 共识查询接口

### 3.1 getObserverListAsync

获取Client对应群组的观察节点列表。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取节点列表之后的回调，ObserverList: 观察节点列表。

**返回值**

- 无
  

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getObserverList","params":[],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": [
        "dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3"
    ]
}
```
### 3.2 getSealerListAsync

获取Client对应群组的共识节点列表。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取节点列表之后的回调

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getSealerList","params":[],"id":1}' http://127.0.0.1:20200

# Result
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

### 3.3 getPbftViewAsync

节点使用PBFT共识算法时，获取PBFT视图信息。

**参数**

- node：可让RPC发送请求到指定节点
- callback：PbftView: PBFT视图信息。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getPbftView","params":["group0","node0"],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": 3
}
```

### 3.4 getConsensusStatusAsync

获取节点共识状态。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取状态后的回调，ConsensusStatus: 节点共识状态。

**返回值**

- 无


**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getConsensusStatus","params":["group0",""],"id":1}' http://127.0.0.1:20200

# Result
{
    "id": 1,
    "jsonrpc": "2.0",
    "result": "{\"blockNumber\":7,\"changeCycle\":0,\"connectedNodeList\":4,\"consensusNodeList\":[{\"index\":0,\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"weight\":1},{\"index\":1,\"nodeID\":\"2a7aecf4acf010b0c13697e84ffd4f18544835662845621f936166d88cf073f16b1daa6883de457e6129377d21412b4c77099e8a50e8ea521752adcddeb8b331\",\"weight\":1},{\"index\":2,\"nodeID\":\"535439908979b89171283dca78520763e0a32c64631d7f34ee1d3f74c408a31aaa8565c50924ba6817cd33c13fe9dc928e36b8a1df022fc825d3687c2b273258\",\"weight\":1},{\"index\":3,\"nodeID\":\"dd99ab883677a8aef2c2a3847b9671e501bd6930792700398627eb0a1ba04fe81015745b6287be5f36786c0cc8ff12e9dd3215dc5a4210b012d77330a739a2c3\",\"weight\":1}],\"consensusNodesNum\":4,\"hash\":\"ef49fef70085ec4ae736c1e82ba74e98a860a53763f2484b8254b0d08a2c6865\",\"index\":0,\"isConsensusNode\":true,\"leaderIndex\":3,\"maxFaultyQuorum\":1,\"minRequiredQuorum\":3,\"nodeID\":\"0055dcaf073a332aa1ad90ff53ef028680d24f9bf2fcbc07ec4bbd4879413f75118a570bcf8001c4526fdb8c1319e51ae63444431ec8ab839465c05e178e7c49\",\"timeout\":false,\"view\":3}\n"
}
```
## 4. 群组查询接口

### 4.1 getGroupInfoAsync

查询当前群组的状态信息。

**参数**

- callback：查询到状态信息之后的回调，BcosGroupInfo: 被查询的群组状态信息。

**返回值**

- BcosGroupInfo: 被查询的群组状态信息。

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupInfo","params":["group0",""],"id":1}' http://127.0.0.1:20200

# Result
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
### 4.2 getGroupListAsync

获取当前节点的群组列表。

**参数**

- callback：获取群组列表之后的回调，BcosGroupList: 当前节点的群组列表。
  

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupList","params":["group0",""],"id":1}' http://127.0.0.1:20200

# Result
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
### 4.3 getGroupPeersAsync

获取当前节点指定群组连接的节点列表。

**参数**

- callback：获取节点列表之后的回调，GroupPeers: 指定群组连接的节点列表。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupPeers","params":["group0",""],"id":1}' http://127.0.0.1:20200

# Result
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
### 4.4 getGroupInfoListAsync

获取当前节点群组信息列表。

**参数**

- callback：获取群组信息之后的回调，BcosGroupInfoList: 当前节点群组信息列表。

**返回值**

- 无

**curl调用示例：**

```shell
# Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupInfoList","params":["group0","node1"],"id":1}' http://127.0.0.1:20200

# Result
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
### 4.5 getGroupNodeInfoAsync

获取群组内指定节点的信息。

**参数**

- node: 指定节点名
- callback：获取信息后的回调，BcosGroupNodeInfo: 查询获取的节点信息。

**返回值**

- BcosGroupNodeInfo: 查询获取的节点信息。

**curl调用示例：**

```shell
curl -X POST --data '{"jsonrpc":"2.0","method":"getGroupNodeInfo","params":["group0","ffa9aa23918afcfa5c20a07177e83731c46f153b3ce33b98cb3c4b61c767d06296ef9c1b7f7c6737c3077a6ec61c1a86d665475629cecd1c209b3f9a3b8688dc"],"id":1}' http://127.0.0.1:20200

{
	"id" : 1,
	"jsonrpc" : "2.0",
	"result" :
	{
		"featureKeys" :
		[
			"bugfix_revert",
			"bugfix_statestorage_hash",
			"bugfix_evm_create2_delegatecall_staticcall_codecopy",
			"bugfix_event_log_order",
			"bugfix_call_noaddr_return",
			"bugfix_precompiled_codehash",
			"feature_dmc2serial",
			"feature_sharding",
			"feature_rpbft",
			"feature_paillier",
			"feature_balance",
			"feature_balance_precompiled",
			"feature_balance_policy1",
			"feature_paillier_add_raw"
		],
		"iniConfig" : "{\"binaryInfo\":{\"buildTime\":\"20240131 20:44:06\",\"gitCommitHash\":\"090ce6c4033881c88feecccdefb430c4f064b4e4\",\"platform\":\"Darwin/appleclang\",\"version\":\"3.6.0\"},\"chainID\":\"chain0\",\"gatewayServiceName\":\"\",\"groupID\":\"group0\",\"isAuthCheck\":true,\"isSerialExecute\":true,\"isWasm\":false,\"nodeID\":\"ffa9aa23918afcfa5c20a07177e83731c46f153b3ce33b98cb3c4b61c767d06296ef9c1b7f7c6737c3077a6ec61c1a86d665475629cecd1c209b3f9a3b8688dc\",\"nodeName\":\"ffa9aa23918afcfa5c20a07177e83731c46f153b3ce33b98cb3c4b61c767d06296ef9c1b7f7c6737c3077a6ec61c1a86d665475629cecd1c209b3f9a3b8688dc\",\"rpcServiceName\":\"\",\"smCryptoType\":false}\n",
		"microService" : false,
		"name" : "ffa9aa23918afcfa5c20a07177e83731c46f153b3ce33b98cb3c4b61c767d06296ef9c1b7f7c6737c3077a6ec61c1a86d665475629cecd1c209b3f9a3b8688dc",
		"nodeID" : "ffa9aa23918afcfa5c20a07177e83731c46f153b3ce33b98cb3c4b61c767d06296ef9c1b7f7c6737c3077a6ec61c1a86d665475629cecd1c209b3f9a3b8688dc",
		"protocol" :
		{
			"compatibilityVersion" : 50724864,
			"maxSupportedVersion" : 2,
			"minSupportedVersion" : 0
		},
		"serviceInfo" :
		[
			{
				"serviceName" : "LedgerServiceObj",
				"type" : 2
			},
			{
				"serviceName" : "SchedulerServiceObj",
				"type" : 3
			},
			{
				"serviceName" : "FrontServiceObj",
				"type" : 4
			},
			{
				"serviceName" : "",
				"type" : 6
			},
			{
				"serviceName" : "",
				"type" : 7
			},
			{
				"serviceName" : "TxPoolServiceObj",
				"type" : 8
			}
		],
		"type" : 0
	}
}
```
## 5. 其他功能接口

### 5.1 set/getExtraData

可写入额外字段到交易中，仅作为存储使用。

### 5.2 isWasm

判断链上的执行虚拟机是WASM还是EVM

### 5.3 isAuthCheck

判断链上是否有权限检查

### 5.4 isEnableCommittee

判断链上是否有启用治理委员

### 5.5 isSerialExecute

判断链上是否是串行执行

### 5.6 getNegotiatedProtocol

获取SDK与节点握手后的协议号的最大最小值，获取的int前16字节为最大值，后16字节为最小值
