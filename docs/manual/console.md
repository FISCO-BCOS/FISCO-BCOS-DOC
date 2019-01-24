# 控制台

## 1 控制台简介
控制台是FISCO BCOS2.0重要的交互式客户端工具，通过Java SDK连接区块链节点。控制台拥有丰富的命令，可以查询区块链状态，管理区块链节点，部署并调用合约等功能。

## 2 数据定义
### 2.1 控制台命令
控制台命令由两部分组成，即指令和指令相关的参数：   
- **命令指令**: 命令指令是执行的操作命令，包括查询区块链相关信息，部署合约和调用合约的指令等。其中大部分指令调用rpc接口，因此与rpc接口同名，但同时为了输入简洁，提供对应的缩写指令。      
- **指令相关的参数**: 指令调用接口需要的参数，指令与参数，参数与参数之间均用空格分隔。其中字符串参数需要加上双引号。          

### 2.2 控制台响应
当发起一个控制台命令时，控制台会获取命令执行的结果，执行结果分为2类：
- 正确结果: 命令返回正确的执行结果，以字符串或是json的形式返回。       
- 错误结果: 命令返回错误的执行结果，以字符串或是json的形式返回。 

**注**：
- 控制台的命令调用rpc接口时，当rpc返回错误响应(具体错误响应见[rpc接口文档](http://***REMOVED***/books/fisco-bcos/page/fisco-bcos20-json-rpc-%E6%8E%A5%E5%8F%A3)),将以json格式显示错误响应的error字段信息。   
- 命令操作系统表时，会返回操作系统表的json字段，其中code是返回码，msg是返回码的描述信息。响应分为三类：  
1. 操作成功响应：  
code大于等于0表示操作成功，其code值为成功操作的记录数, msg为“success”。    
2. 系统性错误响应:  
无权限操作，其code为-1， msg是“non-authorized”。
3. 逻辑性错误响应： 定义如下

|table| commands|code|msg|
|:---|:------|:------|:---------|
|**\_sys_table_access_**|addAuthority(aa)| -30| table name and address exist|
|**\_sys_table_access_**|removeAuthority(ra)| -31| table name and address does not exist|
|**\_sys_miners_**| addMiner(am)/addObserver(ao)/removeNode(rn)|-40| invalid nodeID|
|**\_sys_miners_**| addObserver(ao)/removeNode(rn) |-41|last miner cannot be removed |
|**\_sys_miners_**| addMiner(am)/addObserver(ao) |-42|nodeID is not in network |
|**\_sys_miners_**| removeNode(rn) |-43|nodeID is not in group peers |
|**\_sys_miners_**| addMiner(am) |-44|nodeID is already in miner list |
|**\_sys_miners_**| addObserver(ao) |-45|nodeID is already in observer list |
|**\_sys_cns_**| deployByCNS(dbc) |-50|address and version exist|
|**\_sys_config_**| setSystemConfigByKey(ssc) |-60|set invalid configuration values|


## 3 配置与运行
### 3.1 配置
控制台的配置与运行参考[快速入门文档](../quick_start.md)。   
**说明1：**   
控制台的运行目录dist在web3sdk编译后生成，控制台使用的配置文件在dist/conf目录下，由web3sdk编译过程中从src/test/resources目录下拷贝过来。因此后续需要更改控制台连接的节点，建议直接修改dist/conf目录下的配置文件。

**说明2：**   
当sdk配置文件在一个群组内配置多个节点连接时，由于群组内的某些节点在操作过程中可能退出群组，因此sdk轮询节点查询时，其返回信息可能不一致，属于正常现象。建议使用控制台时，配置一个节点或者保证配置的节点始终在群组中，这样在同步时间内查询的群组内信息保持一致。

### 3.2 控制台运行
启动控制台之前，确保节点已正常启动，sdk配置完成。在dist/bin目录，给web3sdk脚本赋予执行权限。
```
chmod +x web3sdk
```
运行以下命令启动控制台
```
./web3sdk -c
```
**注：**  
**1. 启动命令说明**
```
./web3sdk -c [groupID] [privateKey]   
```
启动命令可以带两个可选参数，可以指定group id和外部账户私钥启动控制台。
显示以下界面则控制台启动成功。
![sdk_console2.png](http://***REMOVED***/uploads/images/gallery/2018-12-Dec/scaled-840-0/MOdytXuSFf8kRKip-sdk_console2.png)

**2. 启用国密版控制台**   
控制台连接的节点需要是国密节点，控制台的配置文件打开国密开关（具体配置方式见sdk文档），即可使用国密版控制台，其命令使用方式与非国密一致。

## 4 控制台命令
### **help**
首先输入help或h，查看可用的命令。

```bash
> help
---------------------------------------------------------------------------------------------
help(h)                                       Provide help information.
getBlockNumber(gbn)                           Query the number of most recent block.
getPbftView(gpv)                              Query the pbft view of node.
getMinerList(gml)                             Query nodeID list for miner nodes.
getObserverList(gol)                          Query nodeID list for observer nodes.
getNodeIDList(gnl)                            Query nodeID list for all connected nodes.
getGroupPeers(ggp)                            Query nodeID list for miner and observer nodes.
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
addMiner(am)                                  Add a miner node.
addObserver(ao)                               Add an observer node.
removeNode(rn)                                Remove a node.
addAuthority(aa)                              Add authority for table by address.
removeAuthority(ra)                           Remove authority for table by address.
queryAuthority(qa)                            Query authority information.
setSystemConfigByKey(ssc)                     Set a system config.
getSystemConfigByKey(gsc)                     Query a system config value by key.
quit(q)                                       Quit console.
---------------------------------------------------------------------------------------------
```
**注：**    
① help显示的每条信息的含义是：命令全称名(缩写名)  命令功能描述   
② 带参数命令，需查看命令参数说明，可输入命令 -h或--help查看。例如
```bash
> gbbh -h     
Query information about a block by hash.
Usage: gbbh blockHash [boolean]
blockHash -- 32 Bytes - Hash of a block.
boolean -- If true it returns the full transaction objects, if false only the hashes of the transactions.
```