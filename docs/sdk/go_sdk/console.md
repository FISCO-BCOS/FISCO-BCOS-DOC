# 控制台

标签：``go-sdk`` ``Go SDK控制台``

----

   [Go SDK](https://github.com/FISCO-BCOS/go-sdk) 通过 `console.go` 实现了一个简单的控制台，支持区块链和节点信息查询。

## 使用

- 拉取代码并编译

```shell
# 拉取代码
git clone https://github.com/FISCO-BCOS/go-sdk.git

# 若因为网络问题导致长时间无法执行上面的命令，请尝试以下命令：
git clone https://gitee.com/FISCO-BCOS/go-sdk.git

# 切换目录
cd go-sdk
# 编译 cmd/console.go
go build cmd/console.go
```

- 搭建FISCO BCOS 2.2以上版本节点，请 [参考](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)

- config.toml 配置文件默认使用 channel 连接模式，请拷贝对应的 ca.crt、sdk.crt 和 sdk.key 证书至 go-sdk 目录

- FISCO-BCOS 2.5及之后的版本，添加了SDK只能连本机构节点的限制，操作时需确认拷贝证书的路径，否则建联报错

## getBlockByHash

根据区块哈希获取区块信息：

```shell
./console getBlockByHash [blockHash] [true/false]
```

参数包括：

- blockHash：区块 hash 值；
- true/false：true 会返回区块中所有交易的详细内容，false 只会返回区块中所有交易的 hash 值，默认为 true。

```shell
> ./console getBlockByHash 0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848 true

Block:
{
  "extraData": [],
  "gasLimit": "0x0",
  "gasUsed": "0x0",
  "hash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "number": "0x3",
  "parentHash": "0x57016509418eb81c0353b1252a364383fcfc5c71035c8a01d24e785ac6e2ce4a",
  "receiptsRoot": "0x4e430ca6474d7013a819a7c602497b7bfdfa14a1197a1edc35444b756cf7e6fc",
  "sealer": "0x1",
  "sealerList": [
    "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302",
    "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
    "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
    "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0"
  ],
  "stateRoot": "0x2d23b1248fe53a1769db06af5c0e99261678643f405f058cfa89193592d13fa7",
  "timestamp": "0x172fe19b855",
  "transactions": [
    {
      "blockHash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
      "blockNumber": "0x3",
      "from": "0x4ca29e9e8cb79c863c04f83827ab540315f25e67",
      "gas": "0x11e1a300",
      "gasPrice": "0x11e1a300",
      "hash": "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434",
      "input": "0x2800efc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000803432616532616533393530613239333362366635373664633934366239306232343262376639613261386434356161653533633161313636346364353832626437353966613639653462353266376134353364636537303238373862396566313166643334666365316264313563396264636162636262656534336531333032",
      "nonce": "0x359fbc4677e4f4ca87a96a31372b1194f03ba200db94a18ad0b30f2e858ac32",
      "to": "0x0000000000000000000000000000000000001003",
      "transactionIndex": "0x0",
      "value": "0x0"
    }
  ],
  "transactionsRoot": "0xcf057dc481d7a97700e93a1ea65f331c3cfee2fee80e3bb80c30748e4988fe9d"
}

> ./console getBlockByHash 0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848 false

Block:
{
  "extraData": [],
  "gasLimit": "0x0",
  "gasUsed": "0x0",
  "hash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "number": "0x3",
  "parentHash": "0x57016509418eb81c0353b1252a364383fcfc5c71035c8a01d24e785ac6e2ce4a",
  "receiptsRoot": "0x4e430ca6474d7013a819a7c602497b7bfdfa14a1197a1edc35444b756cf7e6fc",
  "sealer": "0x1",
  "sealerList": [
    "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302",
    "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
    "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
    "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0"
  ],
  "stateRoot": "0x2d23b1248fe53a1769db06af5c0e99261678643f405f058cfa89193592d13fa7",
  "timestamp": "0x172fe19b855",
  "transactions": [
    "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434"
  ],
  "transactionsRoot": "0xcf057dc481d7a97700e93a1ea65f331c3cfee2fee80e3bb80c30748e4988fe9d"
}
```

## getBlockByNumber

根据区块高度获取区块信息：

```shell
./console getBlockByNumber [blockNumber] [true/false]
```

参数包括：

- blockNumber：区块高度；
- true/false：true 会返回区块中所有交易的详细内容，false 只会返回区块中所有交易的 hash 值，默认为 true。

```shell
> ./console getBlockByNumber 3 true

Block:
{
  "dbHash": "0x2d23b1248fe53a1769db06af5c0e99261678643f405f058cfa89193592d13fa7",
  "extraData": [],
  "gasLimit": "0x0",
  "gasUsed": "0x0",
  "hash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "number": "0x3",
  "parentHash": "0x57016509418eb81c0353b1252a364383fcfc5c71035c8a01d24e785ac6e2ce4a",
  "receiptsRoot": "0x4e430ca6474d7013a819a7c602497b7bfdfa14a1197a1edc35444b756cf7e6fc",
  "sealer": "0x1",
  "sealerList": [
    "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302",
    "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
    "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
    "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0"
  ],
  "stateRoot": "0x2d23b1248fe53a1769db06af5c0e99261678643f405f058cfa89193592d13fa7",
  "timestamp": "0x172fe19b855",
  "transactions": [
    {
      "blockHash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
      "blockNumber": "0x3",
      "from": "0x4ca29e9e8cb79c863c04f83827ab540315f25e67",
      "gas": "0x11e1a300",
      "gasPrice": "0x11e1a300",
      "hash": "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434",
      "input": "0x2800efc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000803432616532616533393530613239333362366635373664633934366239306232343262376639613261386434356161653533633161313636346364353832626437353966613639653462353266376134353364636537303238373862396566313166643334666365316264313563396264636162636262656534336531333032",
      "nonce": "0x359fbc4677e4f4ca87a96a31372b1194f03ba200db94a18ad0b30f2e858ac32",
      "to": "0x0000000000000000000000000000000000001003",
      "transactionIndex": "0x0",
      "value": "0x0"
    }
  ],
  "transactionsRoot": "0xcf057dc481d7a97700e93a1ea65f331c3cfee2fee80e3bb80c30748e4988fe9d"
}

> ./console getBlockByNumber 3 false

Block:
{
  "dbHash": "0x2d23b1248fe53a1769db06af5c0e99261678643f405f058cfa89193592d13fa7",
  "extraData": [],
  "gasLimit": "0x0",
  "gasUsed": "0x0",
  "hash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
  "number": "0x3",
  "parentHash": "0x57016509418eb81c0353b1252a364383fcfc5c71035c8a01d24e785ac6e2ce4a",
  "receiptsRoot": "0x4e430ca6474d7013a819a7c602497b7bfdfa14a1197a1edc35444b756cf7e6fc",
  "sealer": "0x1",
  "sealerList": [
    "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302",
    "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
    "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
    "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0"
  ],
  "stateRoot": "0x2d23b1248fe53a1769db06af5c0e99261678643f405f058cfa89193592d13fa7",
  "timestamp": "0x172fe19b855",
  "transactions": [
    "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434"
  ],
  "transactionsRoot": "0xcf057dc481d7a97700e93a1ea65f331c3cfee2fee80e3bb80c30748e4988fe9d"
}
```

## getBlockHashByNumber

根据区块高度获取区块哈希：

```shell
./console getBlockHashByNumber [blockNumber]
```

参数包括：

- blockNumber：区块高度。

```shell
> ./console getBlockHashByNumber 3

Block Hash:
"0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848"
```

## getBlockNumber

获取最新块高：

```shell
> ./console getBlockNumber

blocknumber:
    hex: "0x3"
decimal:  3
```

## getClientVersion

获取区块链节点版本信息：

```shell
> ./console getClientVersion

Client Version:
{
  "Build Time": "20200610 15:42:05",
  "Build Type": "Linux/g++/RelWithDebInfo",
  "Chain Id": "1",
  "FISCO-BCOS Version": "2.5.0",
  "Git Branch": "master",
  "Git Commit Hash": "b0978f773ca1dbb499a4343b9fb3a12c40b8fc97",
  "Supported Version": "2.4.0"
}
```

## getCode

根据合约地址查询合约数据：

```shell
./console getCode [contract address]
```

参数包括：

- contract address：合约地址。

```shell
> ./console getCode 0x65474dbd4f08170bc2dc30f9ae32f8e2206b15a6

Contract Code:
"0x60806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634ed3885e146100515780636d4ce63c146100ba575b600080fd5b34801561005d57600080fd5b506100b8600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061014a565b005b3480156100c657600080fd5b506100cf610164565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561010f5780820151818401526020810190506100f4565b50505050905090810190601f16801561013c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b8060009080519060200190610160929190610206565b5050565b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101fc5780601f106101d1576101008083540402835291602001916101fc565b820191906000526020600020905b8154815290600101906020018083116101df57829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024757805160ff1916838001178555610275565b82800160010185558215610275579182015b82811115610274578251825591602001919060010190610259565b5b5090506102829190610286565b5090565b6102a891905b808211156102a457600081600090555060010161028c565b5090565b905600a165627a7a72305820d0c58adfbd1215902f16e710a4e52b14e5c9ad7f0f3363c86d2b3156894bd0610029"
```

## getConsensusStatus

获取区块链节点共识状态

```shell
> ./console getConsensusStatus

Consensus Status:
[
  {
    "accountType": 1,
    "allowFutureBlocks": true,
    "cfgErr": false,
    "connectedNodes": 3,
    "consensusedBlockNumber": 4,
    "currentView": 80,
    "groupId": 1,
    "highestblockHash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
    "highestblockNumber": 3,
    "leaderFailed": false,
    "max_faulty_leader": 0,
    "nodeId": "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0",
    "nodeNum": 3,
    "node_index": 2,
    "omitEmptyBlock": true,
    "protocolId": 65544,
    "sealer.0": "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
    "sealer.1": "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
    "sealer.2": "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0",
    "toView": 80
  },
  [
    {
      "nodeId": "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
      "view": 78
    },
    {
      "nodeId": "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
      "view": 79
    },
    {
      "nodeId": "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0",
      "view": 80
    }
  ]
]
```

## getGroupID

获取配置文件中指定的连接节点群组ID：

```shell
> ./console getGroupID

Group ID:
1
```

## getGroupList

获取节点所属群组的群组ID列表：

```shell
> ./console getGroupList

Group ID List:
[
  1
]
```

## getGroupPeers

获取指定群组的共识节点和观察节点列表：

```shell
> ./console getGroupPeers

Peers:
[
  "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302",
  "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
  "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
  "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0"
]
```

## getNodeIDList

获取节点及其连接节点的列表：

```shell
> ./console getNodeIDList

Node ID list:
[
  "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0",
  "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
  "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302",
  "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7"
]
```

## getObserverList

  获取观察者节点列表：

```shell
> ./console getObserverList

Observer List:
[
  "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302"
]
```

## getPbftView

获取PBFT视图：

```shell
> ./console getPbftView

PBFT view:
"0x30"
```

## getPeers

获取区块链节点的连接信息：

```shell
> ./console getPeers

Peers:
[
  {
    "Agency": "agency",
    "IPAndPort": "127.0.0.1:51808",
    "Node": "node3",
    "NodeID": "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
    "Topic": []
  },
  {
    "Agency": "agency",
    "IPAndPort": "127.0.0.1:30302",
    "Node": "node2",
    "NodeID": "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302",
    "Topic": []
  },
  {
    "Agency": "agency",
    "IPAndPort": "127.0.0.1:30301",
    "Node": "node1",
    "NodeID": "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
    "Topic": []
  }
]
```

## getPendingTransactions

获取交易池内所有未上链的交易：

```shell
> ./console getPendingTransactions

Pending Transactions:
[]
```

## getPendingTxSize

获取交易池内未上链的交易数目：

```shell
> ./console getPendingTxSize

Pending Transactions Count:
    hex: "0x0"
decimal:  0
```

## getSealerList

获取共识节点列表：

```shell
>  ./console getSealerList

Sealer List:
[
  "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14",
  "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7",
  "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0"
]
```

## getSyncStatus

获取区块链节点同步状态：

```shell
> ./console getSyncStatus

Synchronization Status:
{
  "blockNumber": 3,
  "genesisHash": "65e9c13da61b1f47564ccc6498260d739ce3dfd3366accc539a64412c1ef8e88",
  "isSyncing": false,
  "knownHighestNumber": 3,
  "knownLatestHash": "ce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "latestHash": "ce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "nodeId": "955ab783c6adc7a5f817773a5fbe32ecd9310f9392406eb1f9fa3d2b21539577a70c933158c1bd3a0bf183d5498bf57202b88401e1cb20e8972aab43ba3354f0",
  "peers": [
    {
      "blockNumber": 3,
      "genesisHash": "65e9c13da61b1f47564ccc6498260d739ce3dfd3366accc539a64412c1ef8e88",
      "latestHash": "ce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
      "nodeId": "42ae2ae3950a2933b6f576dc946b90b242b7f9a2a8d45aae53c1a1664cd582bd759fa69e4b52f7a453dce702878b9ef11fd34fce1bd15c9bdcabcbbee43e1302"
    },
    {
      "blockNumber": 3,
      "genesisHash": "65e9c13da61b1f47564ccc6498260d739ce3dfd3366accc539a64412c1ef8e88",
      "latestHash": "ce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
      "nodeId": "8b5e90815966004e807803aba5f003bc271d0b0aa82805c85764b21187bd504f79ec46eaf1e60752956af174a927d7b16c072c0bca1601968b29342521639c14"
    },
    {
      "blockNumber": 3,
      "genesisHash": "65e9c13da61b1f47564ccc6498260d739ce3dfd3366accc539a64412c1ef8e88",
      "latestHash": "ce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
      "nodeId": "95381c1d22d10ad73171f0d34ec1f2e5809f47ee76264aeed4bb0daaf594bfb5da89f6a65ff4a056952f66a6a99fc927320d002191cecc7a48905edd61ad84b7"
    }
  ],
  "protocolId": 65545,
  "txPoolSize": "0"
}
```

## getSystemConfigByKey

根据关键字获取区块链系统配置：

```shell
./console getSystemConfigByKey [tx_count_limit/tx_gas_limit]
```

参数包括：

- tx_count_limit/tx_gas_limit：单个区块中交易数量限制/单笔交易中可消耗的 gas 限制

```shell
> ./console getSystemConfigByKey tx_count_limit
Result:
"1000"

> ./console getSystemConfigByKey tx_gas_limit
Result:
"300000000"
```

## getTotalTransactionCount

获取指定群组的上链交易数目：

```shell
> ./console getTotalTransactionCount

Latest Statistics on Transaction and Block Height:
{
  "blockNumber": "0x5",
  "failedTxSum": "0x0",
  "txSum": "0x5"
}
```

## getTransactionByBlockHashAndIndex

根据交易所属的区块哈希、 交易索引获取交易信息：

```shell
./console getTransactionByBlockHashAndIndex [blockHash] [transactionIndex]
```

参数包括：

- blockHash：区块 hash 值；
- transactionIndex：交易索引值，注意需要转换为16进制。

```shell
> ./console getTransactionByBlockHashAndIndex 0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848 0x0

Transaction:
{
  "blockHash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "blockNumber": "0x3",
  "from": "0x4ca29e9e8cb79c863c04f83827ab540315f25e67",
  "gas": "0x11e1a300",
  "gasPrice": "0x11e1a300",
  "hash": "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434",
  "input": "0x2800efc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000803432616532616533393530613239333362366635373664633934366239306232343262376639613261386434356161653533633161313636346364353832626437353966613639653462353266376134353364636537303238373862396566313166643334666365316264313563396264636162636262656534336531333032",
  "nonce": "0x359fbc4677e4f4ca87a96a31372b1194f03ba200db94a18ad0b30f2e858ac32",
  "to": "0x0000000000000000000000000000000000001003",
  "transactionIndex": "0x0",
  "value": "0x0"
}
```

## getTransactionByBlockNumberAndIndex

根据交易所属的区块高度、 交易索引获取交易信息：

```shell
./console getTransactionByBlockNumberAndIndex [blockNumber] [transactionIndex]
```

参数包括：

- blockNumber：区块高度；
- transactionIndex：交易索引值，注意需要转换为16进制。

```shell
> ./console getTransactionByBlockNumberAndIndex 3 0x0

Transaction:
{
  "blockHash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "blockNumber": "0x3",
  "from": "0x4ca29e9e8cb79c863c04f83827ab540315f25e67",
  "gas": "0x11e1a300",
  "gasPrice": "0x11e1a300",
  "hash": "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434",
  "input": "0x2800efc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000803432616532616533393530613239333362366635373664633934366239306232343262376639613261386434356161653533633161313636346364353832626437353966613639653462353266376134353364636537303238373862396566313166643334666365316264313563396264636162636262656534336531333032",
  "nonce": "0x359fbc4677e4f4ca87a96a31372b1194f03ba200db94a18ad0b30f2e858ac32",
  "to": "0x0000000000000000000000000000000000001003",
  "transactionIndex": "0x0",
  "value": "0x0"
}
```

## getTransactionByHash

根据交易哈希获取交易信息：

```shell
./console getTransactionByHash [transactionHash]
```

参数包括：

- transactionHash：交易 hash 值。

```shell
> ./console getTransactionByHash 0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434

Transaction:
{
  "blockHash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
  "blockNumber": "0x3",
  "from": "0x4ca29e9e8cb79c863c04f83827ab540315f25e67",
  "gas": "0x11e1a300",
  "gasPrice": "0x11e1a300",
  "hash": "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434",
  "input": "0x2800efc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000803432616532616533393530613239333362366635373664633934366239306232343262376639613261386434356161653533633161313636346364353832626437353966613639653462353266376134353364636537303238373862396566313166643334666365316264313563396264636162636262656534336531333032",
  "nonce": "0x359fbc4677e4f4ca87a96a31372b1194f03ba200db94a18ad0b30f2e858ac32",
  "to": "0x0000000000000000000000000000000000001003",
  "transactionIndex": "0x0",
  "value": "0x0"
}
```

## getTransactionReceipt

根据交易哈希获取交易回执：

```shell
./console getTransactionReceipt [transactionHash]
```

参数包括：

- transactionHash：交易 hash 值。

```shell
> ./console getTransactionReceipt 0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434

Transaction Receipt:
{
        "transactionHash": "0x5518df7c2063efeb6481c35c4c58f378fac5f476c023c2019b9b01d221478434",
        "transactionIndex": "0x0",
        "blockHash": "0xce28a18b54ee72450c403968f705253a59c87a22801a88cc642ae800bb8b4848",
        "blockNumber": "0x3",
        "gasUsed": "0x765b",
        "contractAddress": "0x0000000000000000000000000000000000000000",
        "root": "0x2d23b1248fe53a1769db06af5c0e99261678643f405f058cfa89193592d13fa7",
        "status": "0x0",
        "from": "0x4ca29e9e8cb79c863c04f83827ab540315f25e67",
        "to": "0x0000000000000000000000000000000000001003",
        "input": "0x2800efc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000803432616532616533393530613239333362366635373664633934366239306232343262376639613261386434356161653533633161313636346364353832626437353966613639653462353266376134353364636537303238373862396566313166643334666365316264313563396264636162636262656534336531333032",
        "output": "0x0000000000000000000000000000000000000000000000000000000000000001",
        "logs": [],
        "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
}
```

## setSystemConfigByKey

根据关键字设置区块链系统配置：

```shell
./console setSystemConfigByKey [tx_count_limit/tx_gas_limit/rpbft_epoch_sealer_num/rpbft_epoch_block_num]
```

参数包括：

- tx_count_limit/tx_gas_limit/rpbft_epoch_sealer_num/rpbft_epoch_block_num：单个区块中交易数量限制/单笔交易中可消耗的 gas 限制

```shell
> ./console setSystemConfigByKey tx_count_limit 999
success

> ./console setSystemConfigByKey tx_gas_limit 30000000
success

> ./console setSystemConfigByKey rpbft_epoch_sealer_num 20
success

> ./console setSystemConfigByKey rpbft_epoch_block_num 30
success
```

## grantUserTableManager

根据用户表名和外部账户地址设置权限信息

```shell
> ./console grantUserTableManager [tableName] [accountAddress]
```

参数包括：

- tableName：表名
- accountAddress：外部账户地址

```shell
> ./console grantUserTableManager t_test 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064d
success
```

## revokeUserTableManager

根据用户表名和外部账户地址撤销权限信息

```shell
> ./console revokeUserTableManager [tableName] [accountAddress]
```

参数包括：

- tableName：表名
- accountAddress：外部账户地址

```shell
> ./console revokeUserTableManager t_test 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064d
success
```

## listUserTableManager

根据用户表名查询设置的权限记录列表

```shell
./console listUserTableManager [tableName]
```

参数包括：

- tableName：表名

```shell
> ./console listUserTableManager t_test
{"user table managers":[{"address":"0xfbb18d54e9ee57529cda8c7c52242efe879f064d","enable_num":"11","table_name":"u_t_test"}]}
```

## grantContractWritePermission

根据合约地址和外部账户地址设置合约写权限信息

```shell
> ./console grantContractWritePermission [contractAddress] [accountAddress]
```

参数包括：

- contractAddress：合约地址
- accountAddress：外部账户地址

```shell
> ./console grantContractWritePermission 0x0a68F060B46e0d8f969383D260c34105EA13a9dd 0xFbb18d54e9Ee57579cda8c7c52242EFE879f064a
success
```

## revokeContractWritePermission

根据合约地址和外部账户地址去除合约写权限信息

```shell
> ./console revokeContractWritePermission [contractAddress] [accountAddress]
```

参数包括：

- contractAddress：合约地址
- accountAddress：外部账户地址

```shell
> ./console revokeContractWritePermission 0x0a68F060B46e0d8f969383D260c34105EA13a9dd 0xFbb18d54e9Ee57579cda8c7c52242EFE879f064a              
success
```

## listSysConfigManager

根据合约地址查询拥有合约写权限的记录列表

```shell
./console listSysConfigManager [contractAddress]
```

参数包括：

- contractAddress：合约地址

```shell
> ./console listContractWritePermission 0x0a68F060B46e0d8f969383D260c34105EA13a9dd
{"managers":[{"address":"0xfbb18d54e9ee57529cda8c7c52242efe879f064f","enable_num":"32","table_name":"c_0a68f060b46e0d8f969383d260c34105ea13a9dd"},{"address":"0xfbb18d54e9ee57579cda8c7c52242efe879f064a","enable_num":"38","table_name":"c_0a68f060b46e0d8f969383d260c34105ea13a9dd"}]}
```

## grantDeployAndCreateManager

增加外部账户地址部署合约和创建用户表的权限

```shell
./console grantDeployAndCreateManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console grantDeployAndCreateManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## revokeDeployAndCreateManager

移除外部账户地址部署合约和创建用户表的权限

```shell
./console revokeDeployAndCreateManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console revokeDeployAndCreateManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## listDeployAndCreateManager

查询拥有部署合约和创建用户表权限的记录列表

```shell
> ./console listDeployAndCreateManager
{"managers":[{"address":"0xfbb18d54e9ee57529cda8c7c52242efe879f064f","enable_num":"15","table_name":"_sys_tables_"}]}
```

## grantPermissionManager

授予外部账户地址链管理员权限，链管理员可以使用权限分配功能。该命令只支持 FISCO BCOS 2.4.0 和以下版本。

```shell
./console grantPermissionManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console grantPermissionManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## revokePermissionManager

撤销链外部账户地址链管理员权限

```shell
./console revokePermissionManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console revokePermissionManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## listPermissionManager

查询拥有链管理权限的记录列表

```shell
> ./console listPermissionManager
{"managers":[{"address":"0x83309d045a19c44dc3722d15a6abd472f95866ac","enable_num":"24","table_name":"_sys_table_access_"}]}
```

## grantNodeManager

增加外部账户地址的节点管理权限

```shell
./console grantNodeManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console grantNodeManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## revokeNodeManager

移除外部账户地址的节点管理权限

```shell
./console revokeNodeManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console revokeNodeManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## listNodeManager

查询拥有节点管理的权限记录列表

```shell
> ./console listNodeManager
{"managers":[{"address":"0xfbb18d54e9ee57529cda8c7c52242efe879f064f","enable_num":"18","table_name":"_sys_consensus_"}]}
```

## grantCNSManager

增加外部账户地址使用CNS的权限

```shell
./console grantCNSManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console grantCNSManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## revokeCNSManager

移除外部账户地址使用CNS的权限

```shell
./console revokeCNSManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console revokeCNSManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## listCNSManager

查询拥有使用CNS权限的记录列表

```shell
> ./console listCNSManager
{"managers":[{"address":"0xfbb18d54e9ee57529cda8c7c52242efe879f064f","enable_num":"19","table_name":"_sys_cns_"}]}
```

## grantSysConfigManager

 增加外部账户地址的系统参数管理权限

```shell
./console grantSysConfigManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console grantSysConfigManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## revokeSysConfigManager

 移除外部账户地址的系统参数管理权限

```shell
./console revokeSysConfigManager [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console revokeSysConfigManager 0xFbb18d54e9Ee57529cda8c7c52242EFE879f064F
success
```

## listSysConfigManager

 查询拥有系统参数管理的权限记录列表

```shell
> ./console listSysConfigManager
{"managers":[{"address":"0xfbb18d54e9ee57529cda8c7c52242efe879f064f","enable_num":"21","table_name":"_sys_config_"}]}
```

## queryCNS

根据合约名和版本号查询CNS信息

```shell
./console queryCNS [name] [version]
```

参数包括：

- name：合约名（必须）
- version：版本号（可选）

```shell
> ./console queryCNS store 5.0
name: store, version: 5.0, address: 0x0626918C51A1F36c7ad4354BB1197460A533a2B9

> ./console queryCNS store
name: store, version: 5.0, address: 0x0626918C51A1F36c7ad4354BB1197460A533a2B9
```

## getAddressByContractNameAndVersion

根据合约名和合约版本号查询合约地址

```shell
./console getAddressByContractNameAndVersion [name] [version]
```

参数包括：

- name：合约名
- version：版本号

```shell
> ./console getAddressByContractNameAndVersion store 5.0
0626918c51a1f36c7ad4354bb1197460a533a2b9
```

## addObserver

根据节点NodeID设置对应节点为观察者节点

```shell
./console addObserver [Node ID]
```

参数包括：

- Node ID：节点 ID。

```shell
> ./console addObserver 58108297d9b545dc6e9a7ee4fea539c7886ced0c4cfeb33acd16ad23158247901d7d45dfbacc2fe97e38afaf163a4608f2fc2338d3ca37245d44e983adbde202
success
```

## addSealer

根据节点NodeID设置对应节点为共识节点

```shell
./console addSealer [Node ID]
```

参数包括：

- Node ID：节点 ID。

```shell
> ./console addSealer 58108297d9b545dc6e9a7ee4fea539c7886ced0c4cfeb33acd16ad23158247901d7d45dfbacc2fe97e38afaf163a4608f2fc2338d3ca37245d44e983adbde202
success
```

## removeNode

 根据节点NodeID设置对应节点为游离节点

```shell
./console removeNode [Node ID]
```

参数包括：

- Node ID：节点 ID。

```shell
> ./console removeNode 58108297d9b545dc6e9a7ee4fea539c7886ced0c4cfeb33acd16ad23158247901d7d45dfbacc2fe97e38afaf163a4608f2fc2338d3ca37245d44e983adbde202
success
```

## grantCommitteeMember

根据外部账户地址新增委员会成员

```shell
./console grantCommitteeMember [accountAddress]
```

参数包括：

- accountAddress：外部账户地址。

```shell
> ./console grantCommitteeMember 0x83309d045a19c44dc3722d15a6abd472f95866ac
success
```

## revokeCommitteeMember

 根据外部账户地址撤销委员会成员

```shell
./console revokeCommitteeMember [accountAddress]
```

参数包括：

- accountAddress：外部账户地址。

```shell
> ./console revokeCommitteeMember 0x83309d045a19c44dc3722d15a6abd472f95866ac
success
```

## listCommitteeMembers

查询委员会成员列表

```shell
> ./console listCommitteeMembers
{"committee_members":[{"address":"0x83309d045a19c44dc3722d15a6abd472f95866ac","enable_num":"24"}]}
```

## queryCommitteeMemberWeight

 根据外部账户地址查询委员会成员投票权值

```shell
./console queryCommitteeMemberWeight [accountAddress]
```

参数包括：

- accountAddress：外部账户地址。

```shell
> ./console queryCommitteeMemberWeight 0x83309d045a19c44dc3722d15a6abd472f95866ac
success, the weight 0x83309d045a19c44dc3722d15a6abd472f95866ac is 1
```

## updateCommitteeMemberWeight

  根据外部账户地址更新委员会成员投票权值

```shell
./console updateCommitteeMemberWeight [accountAddress] [weight]
```

参数包括：

- accountAddress：外部账户地址
- weight：权值，大于 0 的整数

```shell
> ./console updateCommitteeMemberWeight 0x83309d045a19c44dc3722d15a6abd472f95866ac 2
success
```

## queryThreshold

 查询委员会全体委员投票时总票数生效的阈值

```shell
> ./console queryThreshold
success, the effective threshold of voting is 50
```

## updateThreshold

更新委员会全体委员投票时票数占比生效的阈值

```shell
./console updateThreshold [threshold]
```

参数包括：

- threshold：阈值，支持范围 [0, 99)。

```shell
> ./console updateThreshold 60
success
```

## grantOperator

根据外部账户地址新增运维权限

```shell
./console grantOperator [accountAddress]
```

参数包括：

- accountAddress：外部账户地址。

```shell
> ./console grantOperator 0x112fb844934c794a9e425dd6b4e57eff1b519f17
success
```

## revokeOperator

 根据外部账户地址撤销运维权限

```shell
./console revokeOperator [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console revokeOperator 0x112fb844934c794a9e425dd6b4e57eff1b519f17
success
```

## listOperators

查询全体运维成员列表

```shell
> ./console listOperators
{"operators":[{"address":"0x112fb844934c794a9e425dd6b4e57eff1b519f17","enable_num":"32"}]}
```

## freezeAccount

根据外部账户地址冻结账户，该外部账号需要是部署过合约的账号

```shell
./console freezeAccount [accountAddress]
```

参数包括：

- accountAddress：外部账户地址。

```shell
> ./console freezeAccount 0x112fb844934c794a9e425dd6b4e57eff1b519f17
success
```

## unfreezeAccount

 根据外部账户地址解冻合账户

```shell
./console unfreezeAccount [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console unfreezeAccount 0x112fb844934c794a9e425dd6b4e57eff1b519f17
success
```

## getAccountStatus

 根据外部账户地址查询账户状态

```shell
./console getAccountStatus [accountAddress]
```

参数包括：

- accountAddress：外部账户地址

```shell
> ./console getAccountStatus 0x112fb844934c794a9e425dd6b4e57eff1b519f17
The account has been frozen. You can use this account after unfreezing it.
```

## freezeContract

根据合约地址冻结合约

```shell
./console freezeContract [contract address]
```

参数包括：

- contract address：合约地址

```shell
> ./console freezeContract 0x54Fb7aAAF3D2d6663E3472d641b7fB54cB246Ff0
success
```

## unfreezeContract

根据合约地址解冻合约

```shell
./console unfreezeContract [contract address]
```

参数包括：

- contract address：合约地址

```shell
> ./console unfreezeContract 0x54Fb7aAAF3D2d6663E3472d641b7fB54cB246Ff0
success
```

## grantContractStatusManager

 根据合约地址和外部账户地址授予账户合约管理权限

```shell
./console grantContractStatusManager [contract address] [accountAddress]
```

参数包括：

- contract address：合约地址

- accountAddress：外部账户地址

```shell
> ./console grantContractStatusManager 0x54Fb7aAAF3D2d6663E3472d641b7fB54cB246Ff0 0xae66fbe9ee2b5007e245d98bf7cf9904cc61e394
success
```

## getContractStatus

根据合约地址查询合约状态

```shell
./console getContractStatus [contract address]
```

参数包括：

- contract address：合约地址

```shell
> ./console getContractStatus 0x54Fb7aAAF3D2d6663E3472d641b7fB54cB246Ff0
The contract is available.
```

## ListManager

 查询拥有合约地址管理权限的外部账号

```shell
./console ListManager [contract address]
```

参数包括：

- contract address：合约地址

```shell
> ./console ListManager 0x54Fb7aAAF3D2d6663E3472d641b7fB54cB246Ff0
{"managers":["0x112fb844934c794a9e425dd6b4e57eff1b519f17","0x83309d045a19c44dc3722d15a6abd472f95866ac","0xae66fbe9ee2b5007e245d98bf7cf9904cc61e394"]}
```
