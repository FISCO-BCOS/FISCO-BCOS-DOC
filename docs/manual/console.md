# 控制台手册

控制台是FISCO BCOS 2.0重要的交互式客户端工具，它通过[SDK](../sdk/index.html)与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等功能。

### 控制台命令
控制台命令由两部分组成，即指令和指令相关的参数：   
- **指令**: 指令是执行的操作命令，包括查询区块链相关信息，部署合约和调用合约的指令等，其中部分指令调用RPC接口，因此与RPC接口同名。
**使用提示： 指令可以使用tab键补全，并且支持按上下键显示历史输入指令。**
    
- **指令相关的参数**: 指令调用接口需要的参数，指令与参数以及参数与参数之间均用空格分隔。其中**调用合约方法时，其方法的字符串参数需要加上双引号，字符串不能带空格**。与RPC接口同名命令的输入参数和获取信息字段的详细解释参考[RPC接口](../api.md)。

### 控制台响应
当发起一个控制台命令时，控制台会获取命令执行的结果，并且在终端展示执行结果，执行结果分为2类：
- **正确结果:** 命令返回正确的执行结果，以字符串或是json的形式返回。       
- **错误结果:** 命令返回错误的执行结果，以字符串或是json的形式返回。 

**注：**
- 控制台的命令调用RPC接口时，当RPC返回错误响应(具体错误码见[RPC设计文档](../design/rpc.md))，将以json格式显示错误响应的error字段信息。
- 命令操作系统功能时，会返回json字段，其中code是返回码，msg是返回码的描述信息。响应分为三类：
    - 操作成功响应：code大于等于0表示操作成功，其code值为成功操作的记录数，msg为“success”。    
    - 系统性错误响应：无权限操作，其code为-1， msg是“non-authorized”。
    - 逻辑性错误响应：定义如下。


```eval_rst

+------+------------------------------------------+
|code  |msg                                       |
+======+==========================================+
|-30   |table name and address exist              |
+------+------------------------------------------+
|-31   |table name and address does not exist     |
+------+------------------------------------------+
|-40   |invalid nodeID                            |
+------+------------------------------------------+
|-41   |last sealer cannot be removed             |
+------+------------------------------------------+
|-42   |nodeID is not in network                  |
+------+------------------------------------------+
|-43   |nodeID is not in group peers              |
+------+------------------------------------------+
|-44   |nodeID is already in sealer list          |
+------+------------------------------------------+
|-45   |nodeID is already in observer list        |
+------+------------------------------------------+
|-50   |address and version exist                 |
+------+------------------------------------------+
|-51   |version exceeds maximum(40) length        |
+------+------------------------------------------+
|-60   |set invalid configuration values          |
+------+------------------------------------------+

```

## 控制台配置与运行

### 配置运行
搭建FISCO BCOS区块链请参考[建链脚本](./build_chain.md)。
#### 控制台配置
**获取控制台**
```bash
curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/console.tar.gz
tar -zxf console.tar.gz
```
目录结构如下：
```
|-- apps //依赖的sdk jar包目录
|   -- web3sdk.jar 
|-- lib // 相关依赖的jar包目录
|-- conf
|   |-- ca.crt   //ca证书文件
|   |-- node.crt // 节点证书文件
|   |-- node.key // 节点私钥文件
|   |-- applicationContext.xml //配置文件
|   |-- log4j.properties  // 日志配置文件
|   |-- privateKey.properties // 发送交易的私钥存储文件
|-- solidity
|   -- contracts  // 合约存储目录
|       -- HelloWorld.sol // 提供的HelloWorld合约
-- start

```
**配置控制台：**
- 区块链节点和证书的配置：
  - **通过[建链脚本](../manual/build_chain.md)搭建的节点证书配置：** 需要将节点所在目录nodes/${ip}/sdk下的ca.crt、node.crt和node.key文件拷贝到conf目录下。
  - **通过[企业工具](../enterprise/index.html)搭建的区块节点证书配置：** 企业工具的demo命令生成的证书和私钥与建链脚本相同。如果使用企业工具的build和expand命令，则需要自己生成证书和私钥，或者使用企业工具的--sdkca命令(具体参考企业工具的[证书生成相关命令](../enterprise/manual/cert.md))生成证书和私钥，将生成sdk目录下的ca.crt、node.crt和node.key文件拷贝到conf目录下。
- 配置conf目录下的applicationContext.xml文件，配置如下图所示，其中红框标记的内容根据区块链节点配置做相应修改。
  ![](../../images/sdk/sdk_xml.png)
  配置项详细说明如下:
  - encryptType: 国密算法开关(默认为0)                              
    - 0: 不使用国密算法发交易                              
    - 1: 使用国密算法发交易(开启国密功能，需要连接的区块链节点是国密节点，搭建国密版FISCO BCOS区块链[参考这里](./guomi.md))。
  - groupChannelConnectionsConfig: 
    - 配置待连接的群组，可以配置一个或多个群组，每个群组需要配置群组ID 
    - 每个群组可以配置一个或多个节点，设置群组节点的配置文件**config.ini**中`[rpc]`部分的`listen_ip`和`channel_listen_port`。
  - channelService: 通过指定群组ID配置SDK实际连接的群组，指定的群组ID是groupChannelConnectionsConfig配置中的群组ID。SDK将与群组中配置的节点均建立连接，然后随机选择一个节点发送请求。

```eval_rst
.. important::
    控制台配置说明

    - **说明：** 当控制台配置文件在一个群组内配置多个节点连接时，由于群组内的某些节点在操作过程中可能退出群组，因此控制台轮询节点查询时，其返回信息可能不一致，属于正常现象。建议使用控制台时，配置一个节点或者保证配置的节点始终在群组中，这样在同步时间内查询的群组内信息保持一致。
``` 
#### 启动控制台
```
$ bash start
# 输出下述信息表明启动成功
=====================================================================================
Welcome to FISCO BCOS console!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______  
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \ 
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \ 
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=====================================================================================
```

#### 启动脚本说明
```
$ bash start [groupID] [privateKey]   
```
启动命令可以指定两个可选参数：           
- `groupId`: - 群组ID, 不指定则默认为群组1。           
- `privateKey`: - 交易发送者外部账号的私钥，不指定则默认从conf目录下的privateKey.properties中读取私钥，如果该文件内容被清空，则随机生成外部账号私钥并将生产的私钥保持在该私钥配置文件中。 

示例
```
# 以群组2，私钥账号地址为3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4登录控制台
$ bash start 2 3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4  
```

## 控制台命令
### **help**
输入help或h，查看控制台所有的命令。

```text
> help
-------------------------------------------------------------------------------------
help(h)                                       Provide help information.
getBlockNumber(gbn)                           Query the number of most recent block.
getPbftView(gpv)                              Query the pbft view of node.
getSealerList(gsl)                            Query nodeID list for sealer nodes.
getObserverList(gol)                          Query nodeID list for observer nodes.
getNodeIDList(gnl)                            Query nodeID list for all connected nodes.
getGroupPeers(ggp)                            Query nodeID list for sealer and observer nodes.
getPeers(gps)                                 Query peers currently connected to the client.
getConsensusStatus(gcs)                       Query consensus status.
getSyncStatus(gss)                            Query sync status.
getClientVersion(gcv)                         Query the current client version.
getGroupList(ggl)                             Query group list.
getBlockByHash(gbbh)                          Query information about a block by hash.
getBlockByNumber(gbbn)                        Query information about a block by block number.
getBlockHashByNumber(ghbn)                    Query block hash by block number.
getTransactionByHash(gtbh)                    Query information about a transaction requested by transaction hash.
getTransactionByBlockHashAndIndex(gthi)       Query information about a transaction by block hash and transaction index position.
getTransactionByBlockNumberAndIndex(gtni)     Query information about a transaction by block number and transaction index position.
getTransactionReceipt(gtr)                    Query the receipt of a transaction by transaction hash.
getPendingTransactions(gpt)                   Query pending transactions.
getPendingTxSize(gpts)                        Query pending transactions size.
getCode(gc)                                   Query code at a given address.
getTotalTransactionCount(gtc)                 Query total transaction count.
deploy(d)                                     Deploy a contract on blockchain.
call(c)                                       Call a contract by a function and paramters.
deployByCNS(dbc)                              Deploy a contract on blockchain by CNS.
callByCNS(cbc)                                Call a contract by a function and paramters by CNS.
queryCNS(qcs)                                 Query cns information by contract name and contract version.
addSealer(as)                                 Add a sealer node.
addObserver(ao)                               Add an observer node.
removeNode(rn)                                Remove a node.
setSystemConfigByKey(ssc)                     Set a system config.
getSystemConfigByKey(gsc)                     Query a system config value by key.
addUserTableManager(aum)                      Add authority for user table by table name and address.
removeUserTableManager(rum)                   Remove authority for user table by table name and address.
queryUserTableManager(qum)                    Query authority for user table information.
addDeployAndCreateManager(adm)                Add authority for deploy contract and create user table by address.
removeDeployAndCreateManager(rdm)             Remove authority for deploy contract and create user table by address.
queryDeployAndCreateManager(qdm)              Query authority information for deploy contract and create user table.
addAuthorityManager(aam)                      Add authority for authority configuration by address.
removeAuthorityManager(ram)                   Remove authority for authority configuration by address.
queryAuthorityManager(qam)                    Query authority information for authority configuration.
addNodeManager(anm)                           Add authority for node configuration by address.
removeNodeManager(rnm)                        Remove authority for node configuration by address.
queryNodeManager(qnm)                         Query authority information for node configuration.
addCNSManager(acm)                            Add authority for CNS by address.
removeCNSManager(rcm)                         Remove authority for CNS by address.
queryCNSManager(qcm)                          Query authority information for CNS.
addSysConfigManager(asm)                      Add authority for system configuration by address.
removeSysConfigManager(rsm)                   Remove authority for system configuration by address.
querySysConfigManager(qsm)                    Query authority information for system configuration.
quit(q)                                       Quit console.
-------------------------------------------------------------------------------------
```
**注：**                                       
- help显示每条命令的含义是：命令全名(缩写名) 命令功能描述                   
- 查看具体命令的使用介绍说明，输入命令全名或缩写名 -h或--help查看。例如：   
```bash
> getBlockByNumber -h
Query information about a block by block number.
Usage: gbbn blockNumber [boolean]
blockNumber -- Integer of a block number.
boolean -- (optional) If true it returns the full transaction objects, if false only the hashes of the transactions.

> gbbh -h     
Query information about a block by block number.
Usage: gbbn blockNumber [boolean]
blockNumber -- Integer of a block number.
boolean -- (optional) If true it returns the full transaction objects, if false only the hashes of the transactions.
```

### **getBlockNumber**
运行getBlockNumber或gbn，查看区块高度。

```bash
> gbn
90
```
### **getSealerList**
运行getSealerList或gsl，查看共识节点列表。

```bash
> gsl 
[
	0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801,
	10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34,
	622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73
]
```

### **getObserverList**
运行getObserverList或gol，查看观察节点列表。
```bash
> gol
[
037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772
]
```
### **getNodeIDList**
运行getNodeIDList或gnl，查看节点及连接p2p节点的nodeID列表。
```bash
> gnl
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8
]
```

### **getPbftView**
运行getPbftView或gpv，查看pbft视图。

```bash
> gpv  
2730
```
### **getConsensusStatus**
运行getConsensusStatus或gcs，查看共识状态。

```bash
> gcs
[
	{
		"accountType":1,
		"allowFutureBlocks":true,
		"cfgErr":false,
		"connectedNodes":3,
		"consensusedBlockNumber":91,
		"currentView":2834,
		"f":1,
		"highestblockHash":"77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
		"highestblockNumber":90,
		"leaderFailed":false,
		"sealer.0":"3106a6310b5edc07658d09cf6a96ba597a5cfc8fbec8587c6786112808286c11f2bc81db9133328983bc641f1c97ce38fe41d74a4a71027def6ee85cc0579215",
		"sealer.1":"697e81e512cffc55fc9c506104fb888a9ecf4e29eabfef6bb334b0ebb6fc4ef8fab60eb614a0f2be178d0b5993464c7387e2b284235402887cdf640f15cb2b4a",
		"sealer.2":"8718579e9a6fee647b3d7404d59d66749862aeddef22e6b5abaafe1af6fc128fc33ed5a9a105abddab51e12004c6bfe9083727a1c3a22b067ddbaac3fa349f7f",
		"sealer.3":"8fc9661baa057034f10efacfd8be3b7984e2f2e902f83c5c4e0e8a60804341426ace51492ffae087d96c0b968bd5e92fa53ea094ace8d1ba72de6e4515249011",
		"nodeNum":4,
		"omitEmptyBlock":true,
		"protocolId":264,
		"toView":2834
	},
	{
		"prepareCache_blockHash":"0000000000000000000000000000000000000000000000000000000000000000",
		"prepareCache_height":-1,
		"prepareCache_idx":"115792089237316195423570985008687907853269984665640564039457584007913129639935",
		"prepareCache_view":"115792089237316195423570985008687907853269984665640564039457584007913129639935"
	},
	{
		"rawPrepareCache_blockHash":"0000000000000000000000000000000000000000000000000000000000000000",
		"rawPrepareCache_height":-1,
		"rawPrepareCache_idx":"115792089237316195423570985008687907853269984665640564039457584007913129639935",
		"rawPrepareCache_view":"115792089237316195423570985008687907853269984665640564039457584007913129639935"
	},
	{
		"committedPrepareCache_blockHash":"3ef0d7dd2bd4ef5b86290da3964043d42ed3282a96584fc773923f45e9b13624",
		"committedPrepareCache_height":90,
		"committedPrepareCache_idx":"0",
		"committedPrepareCache_view":"15"
	},
	{
		"futureCache_blockHash":"0000000000000000000000000000000000000000000000000000000000000000",
		"futureCache_height":-1,
		"futureCache_idx":"115792089237316195423570985008687907853269984665640564039457584007913129639935",
		"futureCache_view":"115792089237316195423570985008687907853269984665640564039457584007913129639935"
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
```

### **getSyncStatus**
运行getSyncStatus或gss，查看同步状态。

```bash
> gss
{
	"blockNumber":90,
	"genesisHash":"ca0577fa37367e89628e2db0a7adfccdd4b53e5fe781d7e34a3d5edbe29d1961",
	"isSyncing":false,
	"knownHighestNumber":0,
	"knownLatestHash":"ca0577fa37367e89628e2db0a7adfccdd4b53e5fe781d7e34a3d5edbe29d1961",
	"latestHash":"77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"nodeId":"3106a6310b5edc07658d09cf6a96ba597a5cfc8fbec8587c6786112808286c11f2bc81db9133328983bc641f1c97ce38fe41d74a4a71027def6ee85cc0579215",
	"peers":[
		{
			"blockNumber":90,
			"genesisHash":"ca0577fa37367e89628e2db0a7adfccdd4b53e5fe781d7e34a3d5edbe29d1961",
			"latestHash":"77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
			"nodeId":"697e81e512cffc55fc9c506104fb888a9ecf4e29eabfef6bb334b0ebb6fc4ef8fab60eb614a0f2be178d0b5993464c7387e2b284235402887cdf640f15cb2b4a"
		},
		{
			"blockNumber":90,
			"genesisHash":"ca0577fa37367e89628e2db0a7adfccdd4b53e5fe781d7e34a3d5edbe29d1961",
			"latestHash":"77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
			"nodeId":"8718579e9a6fee647b3d7404d59d66749862aeddef22e6b5abaafe1af6fc128fc33ed5a9a105abddab51e12004c6bfe9083727a1c3a22b067ddbaac3fa349f7f"
		},
		{
			"blockNumber":90,
			"genesisHash":"ca0577fa37367e89628e2db0a7adfccdd4b53e5fe781d7e34a3d5edbe29d1961",
			"latestHash":"77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
			"nodeId":"8fc9661baa057034f10efacfd8be3b7984e2f2e902f83c5c4e0e8a60804341426ace51492ffae087d96c0b968bd5e92fa53ea094ace8d1ba72de6e4515249011"
		}
	],
	"protocolId":265,
	"txPoolSize":0
}
```

### **getClientVersion**
运行getClientVersion或gcv，查看节点的版本。
```bash
> gcv
{
	"Build Time":"20190107 10:15:23",
	"Build Type":"Linux/g++/RelWithDebInfo",
	"FISCO-BCOS Version":"2.0.0",
	"Git Branch":"release-2.0.1",
	"Git Commit Hash":"be95a6e3e85b621860b101c3baeee8be68f5f450"
}
```
### **getPeers**
运行getPeers或gps，查看节点的peers。
```bash
> gps
[
	{
		"IPAndPort":"127.0.0.1:50723",
		"NodeID":"8718579e9a6fee647b3d7404d59d66749862aeddef22e6b5abaafe1af6fc128fc33ed5a9a105abddab51e12004c6bfe9083727a1c3a22b067ddbaac3fa349f7f",
		"Topic":[
			
		]
	},
	{
		"IPAndPort":"127.0.0.1:50719",
		"NodeID":"697e81e512cffc55fc9c506104fb888a9ecf4e29eabfef6bb334b0ebb6fc4ef8fab60eb614a0f2be178d0b5993464c7387e2b284235402887cdf640f15cb2b4a",
		"Topic":[
			
		]
	},
	{
		"IPAndPort":"127.0.0.1:30304",
		"NodeID":"8fc9661baa057034f10efacfd8be3b7984e2f2e902f83c5c4e0e8a60804341426ace51492ffae087d96c0b968bd5e92fa53ea094ace8d1ba72de6e4515249011",
		"Topic":[
			
		]
	}
]
```
### **getGroupPeers**
运行getGroupPeers或ggp，查看节点所在group的peers。
```bash
> ggp
[3106a6310b5edc07658d09cf6a96ba597a5cfc8fbec8587c6786112808286c11f2bc81db9133328983bc641f1c97ce38fe41d74a4a71027def6ee85cc0579215, 8fc9661baa057034f10efacfd8be3b7984e2f2e902f83c5c4e0e8a60804341426ace51492ffae087d96c0b968bd5e92fa53ea094ace8d1ba72de6e4515249011, 8718579e9a6fee647b3d7404d59d66749862aeddef22e6b5abaafe1af6fc128fc33ed5a9a105abddab51e12004c6bfe9083727a1c3a22b067ddbaac3fa349f7f, 697e81e512cffc55fc9c506104fb888a9ecf4e29eabfef6bb334b0ebb6fc4ef8fab60eb614a0f2be178d0b5993464c7387e2b284235402887cdf640f15cb2b4a]
```
### **getGroupList**
运行getGroupList或ggl，查看群组列表:
```bash
> ggl
[1]
```
### **getBlockByHash**
运行getBlockByHash或gbbh，根据区块哈希查询区块信息。              
参数：
- 区块哈希：0x开头的区块哈希值。   
- 交易标志：默认false，区块中的交易只显示交易哈希，设置为true，显示交易具体信息。
```bash
> gbbh 0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02
{
	"extraData":[
		
	],
	"gasLimit":"0x0",
	"gasUsed":"0x0",
	"hash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
	"number":"0x5a",
	"parentHash":"0xf828e5ab29802b4cdd5275afed4b6dbc4b54e1a5b549fdc41530d2e6b5aab649",
	"sealer":"0x0",
	"stateRoot":"0x43be8be5a9edb857e7df502fab33775490ead2c37be6714a73c7893783a7845f",
	"timestamp":"0x16764e744d0",
	"transactions":[
		"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac"
	],
	"transactionsRoot":"0xe9d94814e825682d378fada5b680cc5359baca971de71804d683d3b87d1fd326"
}
> gbbh 0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02 true
{
	"extraData":[
		
	],
	"gasLimit":"0x0",
	"gasUsed":"0x0",
	"hash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
	"number":"0x5a",
	"parentHash":"0xf828e5ab29802b4cdd5275afed4b6dbc4b54e1a5b549fdc41530d2e6b5aab649",
	"sealer":"0x0",
	"stateRoot":"0x43be8be5a9edb857e7df502fab33775490ead2c37be6714a73c7893783a7845f",
	"timestamp":"0x16764e744d0",
	"transactions":[
		{
			"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
			"blockNumber":"0x5a",
			"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
			"gas":"0x1c9c380",
			"gasPrice":"0x1",
			"hash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
			"input":"0x10009562616c6963650000000000000000000000000000000000000000000000000000006a6f726500000000000000000000000000000000000000000000000000000000",
			"nonce":"0x18711fff2ea68dc8b8dce4a3d3845c62a0630766a448bd9453a9127efe6f9e5",
			"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
			"transactionIndex":"0x0",
			"value":"0x0"
		}
	],
	"transactionsRoot":"0xe9d94814e825682d378fada5b680cc5359baca971de71804d683d3b87d1fd326"
}
```
### **getBlockByNumber**
运行getBlockByNumber或gbbn，根据区块高度查询区块信息。                            
参数：     
- 区块高度：十进制整数。    
- 交易标志：默认false，区块中的交易只显示交易哈希，设置为true，显示交易具体信息。      
```bash
> gbbn 90  
{
	"extraData":[
		
	],
	"gasLimit":"0x0",
	"gasUsed":"0x0",
	"hash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
	"number":"0x5a",
	"parentHash":"0xf828e5ab29802b4cdd5275afed4b6dbc4b54e1a5b549fdc41530d2e6b5aab649",
	"sealer":"0x0",
	"stateRoot":"0x43be8be5a9edb857e7df502fab33775490ead2c37be6714a73c7893783a7845f",
	"timestamp":"0x16764e744d0",
	"transactions":[
		"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac"
	],
	"transactionsRoot":"0xe9d94814e825682d378fada5b680cc5359baca971de71804d683d3b87d1fd326"
}
```
### **getBlockHashByNumber**
运行getBlockHashByNumber或ghbn，通过区块高度获得区块哈希。              
参数：           
- 区块高度：十进制整数。
```bash
> ghbn 90
0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02
```
### **getTransactionByHash**
运行getTransactionByHash或gtbh，通过交易哈希查询交易信息。                   
参数：        
- 交易哈希：0x开头的交易哈希值。
```bash
> gtbh 0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac
{
	"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"blockNumber":"0x5a",
	"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
	"gas":"0x1c9c380",
	"gasPrice":"0x1",
	"hash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
	"input":"0x10009562616c6963650000000000000000000000000000000000000000000000000000006a6f726500000000000000000000000000000000000000000000000000000000",
	"nonce":"0x18711fff2ea68dc8b8dce4a3d3845c62a0630766a448bd9453a9127efe6f9e5",
	"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
	"transactionIndex":"0x0",
	"value":"0x0"
}
```
### **getTransactionByBlockHashAndIndex**
运行getTransactionByBlockHashAndIndex或gthi，通过区块哈希和交易索引查询交易信息。              
参数：         
- 区块哈希：0x开头的区块哈希值。       
- 交易索引：十进制整数。     
```bash
> gthi 0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02 0
{
	"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"blockNumber":"0x5a",
	"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
	"gas":"0x1c9c380",
	"gasPrice":"0x1",
	"hash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
	"input":"0x10009562616c6963650000000000000000000000000000000000000000000000000000006a6f726500000000000000000000000000000000000000000000000000000000",
	"nonce":"0x18711fff2ea68dc8b8dce4a3d3845c62a0630766a448bd9453a9127efe6f9e5",
	"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
	"transactionIndex":"0x0",
	"value":"0x0"
}

```
### **getTransactionByBlockNumberAndIndex**
运行getTransactionByBlockNumberAndIndex或gtni，通过区块高度和交易索引查询交易信息。              
参数：      
- 区块高度：十进制整数。
- 交易索引：十进制整数。
```bash
> gtni 90 0
{
	"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"blockNumber":"0x5a",
	"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
	"gas":"0x1c9c380",
	"gasPrice":"0x1",
	"hash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
	"input":"0x10009562616c6963650000000000000000000000000000000000000000000000000000006a6f726500000000000000000000000000000000000000000000000000000000",
	"nonce":"0x18711fff2ea68dc8b8dce4a3d3845c62a0630766a448bd9453a9127efe6f9e5",
	"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
	"transactionIndex":"0x0",
	"value":"0x0"
}
```
### **getTransactionReceipt**
运行getTransactionReceipt或gtr，通过交易哈希查询交易回执。              
参数：
- 交易哈希：0x开头的交易哈希值。
```bash
> gtr 0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac
{
	"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"blockNumber":"0x5a",
	"contractAddress":"0x0000000000000000000000000000000000000000",
	"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
	"gasUsed":"0xf401",
	"logs":[
		
	],
	"logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
	"status":"0x0",
	"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
	"transactionHash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
	"transactionIndex":"0x0"
}
```
### **getPendingTransactions**
运行getPendingTransactions或gpt，查询等待处理的交易。              
```bash
> gpt
[]
```

### **getPendingTxSize**
运行getPendingTxSize或gpts，查询等待处理的交易数量。              
```bash
> gpts
0
```

### **getCode**
运行getCode或gc，根据合约地址查询合约代码。                                                   
参数：
- 合约地址：0x的合约地址(部署合约可以获得合约地址)。
- 
```bash
> gc 0x97b8c19598fd781aaeb53a1957ef9c8acc59f705
0x60606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100465780636d4ce63c14610066575bfe5b341561004e57fe5b610064600480803590602001909190505061008c565b005b341561006e57fe5b61007661028c565b6040518082815260200191505060405180910390f35b8060006001015410806100aa57506002600101548160026001015401105b156100b457610289565b806000600101540360006001018190555080600260010160008282540192505081905550600480548060010182816100ec919061029a565b916000526020600020906004020160005b608060405190810160405280604060405190810160405280600881526020017f32303137303431330000000000000000000000000000000000000000000000008152508152602001600060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815250909190915060008201518160000190805190602001906101ec9291906102cc565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b600060026001015490505b90565b8154818355818115116102c7576004028160040283600052602060002091820191016102c6919061034c565b5b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061030d57805160ff191683800117855561033b565b8280016001018555821561033b579182015b8281111561033a57825182559160200191906001019061031f565b5b50905061034891906103d2565b5090565b6103cf91905b808211156103cb57600060008201600061036c91906103f7565b6001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556002820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600382016000905550600401610352565b5090565b90565b6103f491905b808211156103f05760008160009055506001016103d8565b5090565b90565b50805460018160011615610100020316600290046000825580601f1061041d575061043c565b601f01602090049060005260206000209081019061043b91906103d2565b5b505600a165627a7a723058203c1f95b4a803493db0120df571d9f5155734152548a532412f2f9fa2dbe1ac730029
```

### **getTotalTransactionCount**
运行getTotalTransactionCount或gtc，查询当前块高和总交易数。                          
```bash
> gtc
{
	"blockNumber":1,
	"txSum":1
}
```
### **deploy**
运行deploy或d，部署合约。(默认提供HelloWorld合约进行示例使用)
参数：
- 合约名称：部署的合约名称。                            
                           
```bash
> deploy HelloWorld
0xb3c223fc0bf6646959f254ac4e4a7e355b50a344
```
**注：** 部署用户编写的合约，只需要将solidity合约文件放到solidity/contracts/目录下，然后进行部署即可。

### **call**
运行call或c，调用合约。                                
参数： 
- 合约名称：部署的合约名称。
- 合约地址: 部署合约获取的地址。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定，参数空格分隔，字符串需要双引号标识。
```bash
# 调用get接口获取name变量
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, World!
# 调用set设置name
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 set "Hello,FISCO-BCOS"
0x21dca087cb3e44f44f9b882071ec6ecfcb500361cad36a52d39900ea359d0895
# 调用get接口获取name变量，检查设置是否生效
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello,FISCO-BCOS
```

### **deployByCNS**
deployByCNS或dbc，利用[CNS](../design/features/CNS_contract_name_service.md)部署合约。(默认提供HelloWorld合约进行示例使用)                                 
参数：
- 合约名称：部署的合约名称。
- 合约版本号：部署的合约版本号(长度不能超过40)。
```bash
> dbc HelloWorld 1.0
0x7956881392a8e2893d6c3f514ef5c37f9d5e52ef
```
**注：** 部署用户编写的合约，只需要将solidity合约文件放到solidity/contracts/目录下，然后进行部署即可。

### **queryCNS**
运行queryCNS或qcs，根据合约名称和合约版本号（可选参数）查询CNS表记录信息。                                 
参数：
- 合约名称：部署的合约名称。
- 合约版本号：(可选)部署的合约版本号。
```bash
> qcs HelloWorld
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x7956881392a8e2893d6c3f514ef5c37f9d5e52ef  |
|                     2.0                     | 0x18f18eb950ae04b3b45837261e441faf2d316341  |
---------------------------------------------------------------------------------------------

> qcs HelloWorld 1.0
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x7956881392a8e2893d6c3f514ef5c37f9d5e52ef  |
---------------------------------------------------------------------------------------------
```
### **callByCNS**
运行callByCNS或cbc，利用CNS调用合约。                                 
参数： 
- 合约名称：部署的合约名称。
- 合约版本号：部署的合约版本号。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定，参数空格分隔，字符串需要双引号标识。
```bash
> cbc HelloWorld 1.0 set "Hello,FISCO-BCOS"
0x80bb37cc8de2e25f6a1cdcb6b4a01ab5b5628082f8da4c48ef1bbc1fb1d28b2d

> cbc HelloWorld 1.0 get
Hello,FISCO-BCOS
```

### **addSealer**
运行addSealer或as，将节点添加为共识节点。                                 
参数： 
- 节点nodeID
```bash
> as ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":1,
	"msg":"success"
}
```

### **addObserver**
运行addObserver或ao，将节点添加为观察节点。                                 
参数： 
- 节点nodeID
```bash
> ao ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":1,
	"msg":"success"
}
```

### **removeNode**
运行removeNode或rn，节点退出。通过am/ao命令可以将退出的节点添加为共识/观察节点。                                  
参数： 
- 节点nodeID
```bash
> rn ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":1,
	"msg":"success"
}
```
### **setSystemConfigByKey**
运行setSystemConfigByKey或ssc，以键值对方式设置系统配置。目前设置的系统配置支持tx_count_limit和tx_gas_limit，系统配置用法见[多群组操作指南](build_group.md)                                  
参数： 
- key
- value
```bash
> ssc tx_count_limit 100
{
	"code":1,
	"msg":"success"
}
```
### **getSystemConfigByKey**
运行getSystemConfigByKey或gsc，根据键查询系统配置的值。                                  
参数： 
- key
```bash
> gsc tx_count_limit
100
```
### **addUserTableManager**
运行addUserTableManager或aum，根据用户表名和外部账号地址设置权限信息。                                  
参数： 
- 表名
- 外部账号地址
```bash
> aum t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```

### **removeUserTableManager**
运行removeUserTableManager或rum，根据用户表名和外部账号地址移除权限信息。                                                                 
参数： 
- 表名
- 外部账号地址
```bash
> rum t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```
### **queryUserTableManager**
运行queryUserTableManager或qum，根据用户表名查询设置的权限记录列表。                                  
参数： 
- 表名
```bash
> qum t_test 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **addDeployAndCreateManager**
运行addDeployAndCreateManager或adm，增加外部账号地址的部署合约和创建用户表权限。

参数： 
- 外部账号地址
```bash
> adm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```

### **removeDeployAndCreateManager**
运行removeDeployAndCreateManager或rdm，移除外部账号地址的部署合约和创建用户表权限。                                                                 
参数： 
- 外部账号地址
```bash
> rdm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```
### **queryDeployAndCreateManager**
运行queryDeployAndCreateManager或qdm，查询拥有部署合约和创建用户表权限的权限记录列表。                                  
```bash
> qdm 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **addNodeManager**
运行addNodeManager或anm，增加外部账号地址的节点管理权限。

参数： 
- 外部账号地址
```bash
> anm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```

### **removeNodeManager**
运行removeNodeManager或rnm，移除外部账号地址的节点管理权限。                                                                 
参数： 
- 外部账号地址
```bash
> rnm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```
### **queryNodeManager**
运行queryNodeManager或qnm，查询拥有节点管理的权限记录列表。

```bash
> qnm 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **addCNSManager**
运行addCNSManager或acm，增加外部账号地址的使用CNS权限。

参数： 
- 外部账号地址
```bash
> acm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```

### **removeCNSManager**
运行removeCNSManager或rcm，移除外部账号地址的使用CNS权限。                                                                 
参数： 
- 外部账号地址
```bash
> rcm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```
### **queryCNSManager**
运行queryCNSManager或qcm，查询拥有使用CNS的权限记录列表。
                                  
```bash
> qcm 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **addSysConfigManager**
运行addSysConfigManager或asm，增加外部账号地址的系统参数管理权限。

参数： 
- 外部账号地址
```bash
> asm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```

### **removeSysConfigManager**
运行removeSysConfigManager或rsm，移除外部账号地址的系统参数管理权限。                                                                 
参数： 
- 外部账号地址
```bash
> rsm 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":1,
	"msg":"success"
}
```
### **querySysConfigManager**
运行querySysConfigManager或qsm，查询拥有系统参数管理的权限记录列表。
                                  
```bash
> qsm 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **quit**
运行quit或q，退出控制台。
```bash
quit
```
