# 控制台手册

控制台是FISCO BCOS 2.0重要的交互式客户端工具，它通过[SDK](../sdk/index.html)与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将solidity合约文件编译为java合约文件。

### 控制台命令
控制台命令由两部分组成，即指令和指令相关的参数：   
- **指令**: 指令是执行的操作命令，包括查询区块链相关信息，部署合约和调用合约的指令等，其中部分指令调用JSON-RPC接口，因此与JSON-RPC接口同名。
**使用提示： 指令可以使用tab键补全，并且支持按上下键显示历史输入指令。**
    
- **指令相关的参数**: 指令调用接口需要的参数，指令与参数以及参数与参数之间均用空格分隔。其中**调用合约方法时，其方法的字符串参数需要加上双引号**。与JSON-RPC接口同名命令的输入参数和获取信息字段的详细解释参考[JSON-RPC API](../api.md)。

### 控制台响应
当发起一个控制台命令时，控制台会获取命令执行的结果，并且在终端展示执行结果，执行结果分为2类：
- **正确结果:** 命令返回正确的执行结果，以字符串或是json的形式返回。       
- **错误结果:** 命令返回错误的执行结果，以字符串或是json的形式返回。 

**注：**
- 控制台的命令调用JSON-RPC接口时，当JSON-RPC返回错误响应(具体错误码见[JSON-RPC设计文档](../design/rpc.md))，将以json格式显示错误响应的error字段信息。

- 命令操作系统功能时，会返回json字段，其中code是错误码，msg是错误码的描述信息。

|错误码|消息内容|
|:----|:---|
|0|success|
|50000|permission denied|
|51000|table name and address exist|
|51001|table name and address does not exist|
|51100|invalid nodeId|
|51101|the last sealer cannot be removed|
|51102|table name and address does not exist|
|51103|the node is not in group peers|
|51104|the node is already in sealer list|
|51105|the node is already in observer list|
|51200|contract name and version exist|
|51201|version exceeds maximum(40) length|
|51300|invalid configuration key|

## 控制台配置与运行

```eval_rst
.. important::
    前置条件：搭建FISCO BCOS区块链请参考 `建链脚本 <./build_chain.html>`_ 或 `企业工具 <../enterprise_tools/index.html>`_。
```
### 获取控制台

```bash
# 下载控制台压缩包
$ cd ~ && mkdir fisco && cd fisco
$ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/console/console-0.4.25.tar.gz
$ tar -zxf console-0.4.25.tar.gz
```
目录结构如下：
```bash
|-- apps # 依赖的sdk jar包目录
|   -- web3sdk.jar 
|-- lib # 相关依赖的jar包目录
|-- conf
|   |-- ca.crt   # ca证书文件
|   |-- node.crt # 节点证书文件
|   |-- node.key # 节点私钥文件
|   |-- applicationContext.xml # 配置文件
|   |-- log4j.properties  # 日志配置文件
|   |-- privateKey.properties # 发送交易的私钥存储文件
|-- solidity # 控制台命令部署和调用的合约所在目录
|   -- contracts  # 部署和调用合约的solidity合约存储目录
|       -- HelloWorld.sol # 普通合约：HelloWorld合约，可部署和调用
|       -- TableTest.sol # CRUD合约：TableTest合约，可部署和调用
|       -- Table.sol # CRUD合约需要引入的合约接口：Table合约接口
|-- start.sh # 控制台启动脚本
|-- tools # 控制台工具目录
    |-- contracts # 用户编写的solidity合约存放目录
    |   |-- Table.sol # 默认提供CRUD的合约接口Table.sol文件
    |-- sol2java.sh # solidity合约文件编译为java合约文件的工具脚本
```
**注：** 
- 使用控制台部署和调用合约请将solidity合约文件放在`solidity/contract`目录下，然后运行部署和调用合约命令。
- **控制台提供一个专门的编译合约工具，方便开发者将solidity合约文件编译为java合约文件。** 使用该工具，分为两步：
  - 将solidity合约文件放在`tools/contracts`目录下。
  - 通过运行`tools`目录下的`sol2java.sh`脚本(**需要指定一个java的包名**)完成编译合约任务。例如，拷贝`HelloWorld.sol`合约到`tools/contracts`目录下，指定包名为`org.com.fisco`，命令如下：
    ```bash
    $ cd ~/fisco/console-0.4.25
    $ cp solidity/contracts/HelloWorld.sol tools/contracts/
    $ ./sol2java.sh org.com.fisco
    ```
    运行成功之后，将会在`console-0.4.25/tools`目录生成java、abi和bin目录，如下所示。
    ```bash
    |-- abi # 编译生成的abi目录，存放solidity合约编译的abi文件
    |   |-- HelloWorld.abi
    |   |-- Table.abi
    |-- bin # 编译生成的bin目录，存放solidity合约编译的bin文件
    |   |-- HelloWorld.bin
    |   |-- Table.bin
    |-- java  # 存放编译的包路径及Java合约文件
    |   |-- org
    |       |-- com
    |           |-- fisco
    |               |-- HelloWorld.java # 编译成功的目标Java文件
    |               |-- Table.java  # 编译成功的系统CRUD合约接口Java文件
    ```
    java目录下生成了`org/com/fisco/`包路径目录。包路径目录下将会生成java合约文件`HelloWorld.java`和`Table.java`。其中`HelloWorld.java`是java应用所需要的java合约文件。

### 配置控制台
- 区块链节点和证书的配置：
  - **通过[建链脚本](../manual/build_chain.md)搭建的节点证书配置：** 需要将节点所在目录`nodes/${ip}/sdk`下的`ca.crt`、`node.crt`和`node.key`文件拷贝到conf目录下。
  - **通过[企业工具](../enterprise/index.html)搭建的区块节点证书配置：** 企业工具的demo命令生成的证书和私钥与建链脚本相同。如果使用企业工具的build和expand命令，则需要自己生成证书和私钥，或者使用企业工具的--sdkca命令(具体参考企业工具的[证书生成相关命令](../enterprise_tools/operation.html#id2))生成证书和私钥，将生成sdk目录下的`ca.crt`、`node.crt`和`node.key`文件拷贝到conf目录下。
- 配置conf目录下的`applicationContext.xml`文件，配置如下图所示，其中添加注释的内容根据区块链节点配置做相应修改。

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
           xmlns:tx="http://www.springframework.org/schema/tx" xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
         http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
         http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">


        <bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
                <constructor-arg value="0"/> <!-- 0:standard 1:guomi -->
        </bean>

        <bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
                <property name="allChannelConnections">
                        <list>
                                <bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                                        <property name="groupId" value="1" /> <!-- 群组的groupID -->
                                        <property name="connectionsStr">
                                                <list>
                                                        <value>127.0.0.1:20200</value>  <!-- IP:channel_port -->
                                                </list>
                                        </property>
                                </bean>
                        </list>
                </property>
        </bean>

        <bean id="channelService" class="org.fisco.bcos.channel.client.Service" depends-on="groupChannelConnectionsConfig">
                <property name="groupId" value="1" /> <!-- 连接ID为1的群组 -->
                <property name="orgID" value="fisco" />
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>

</beans>
```
  配置项详细说明如下:
  - encryptType: 国密算法开关(默认为0)                              
    - 0: 不使用国密算法发交易                              
    - 1: 使用国密算法发交易(开启国密功能，需要连接的区块链节点是国密节点，搭建国密版FISCO BCOS区块链[参考这里](./guomi.md))。
  - groupChannelConnectionsConfig: 
    - 配置待连接的群组，可以配置一个或多个群组，每个群组需要配置群组ID 
    - 每个群组可以配置一个或多个节点，设置群组节点的配置文件`config.ini`中`[rpc]`部分的`listen_ip`和`channel_listen_port`。
  - channelService: 通过指定群组ID配置SDK实际连接的群组，指定的群组ID是groupChannelConnectionsConfig配置中的群组ID。SDK将与群组中配置的节点均建立连接，然后随机选择一个节点发送请求。

```eval_rst
.. important::
    控制台配置说明

    - **说明：** 当控制台配置文件在一个群组内配置多个节点连接时，由于群组内的某些节点在操作过程中可能退出群组，因此控制台轮询节点查询时，其返回信息可能不一致，属于正常现象。建议使用控制台时，配置一个节点或者保证配置的节点始终在群组中，这样在同步时间内查询的群组内信息保持一致。
``` 
### 启动控制台
```
$ ./start.sh
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

### 启动脚本说明
```
$ ./start.sh [groupID] [privateKey]   
```
启动命令可以指定两个可选参数：           
- `groupId`: 群组ID, 不指定则默认为群组1。           
- `privateKey`: 交易发送者外部账号的私钥，不指定则默认从conf目录下的privateKey.properties中读取私钥，如果该文件内容被清空，则随机生成外部账号私钥并将生产的私钥保持在该私钥配置文件中。 

示例
```
# 以群组2，私钥账号地址为3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4登录控制台
$ ./start.sh 2 3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4  
```

## 控制台命令
### **help**
输入help或者h，查看控制台所有的命令。

```text
[group:1]> help
-------------------------------------------------------------------------------------
help(h)                                  Provide help information.
switch(s)                                Switch to a specific group by group ID.
getBlockNumber                           Query the number of most recent block.
getPbftView                              Query the pbft view of node.
getSealerList                            Query nodeId list for sealer nodes.
getObserverList                          Query nodeId list for observer nodes.
getNodeIDList                            Query nodeId list for all connected nodes.
getGroupPeers                            Query nodeId list for sealer and observer nodes.
getPeers                                 Query peers currently connected to the client.
getConsensusStatus                       Query consensus status.
getSyncStatus                            Query sync status.
getNodeVersion                         Query the current client version.
getGroupList                             Query group list.
getBlockByHash                           Query information about a block by hash.
getBlockByNumber                         Query information about a block by block number.
getBlockHashByNumber                     Query block hash by block number.
getTransactionByHash                     Query information about a transaction requested by transaction hash.
getTransactionByBlockHashAndIndex        Query information about a transaction by block hash and transaction index position.
getTransactionByBlockNumberAndIndex      Query information about a transaction by block number and transaction index position.
getTransactionReceipt                    Query the receipt of a transaction by transaction hash.
getPendingTransactions                   Query pending transactions.
getPendingTxSize                         Query pending transactions size.
getCode                                  Query code at a given address.
getTotalTransactionCount                 Query total transaction count.
deploy                                   Deploy a contract on blockchain.
call                                     Call a contract by a function and paramters.
deployByCNS                              Deploy a contract on blockchain by CNS.
callByCNS                                Call a contract by a function and paramters by CNS.
queryCNS                                 Query CNS information by contract name and contract version.
addSealer                                Add a sealer node.
addObserver                              Add an observer node.
removeNode                               Remove a node.
setSystemConfigByKey                     Set a system config.
getSystemConfigByKey                     Query a system config value by key.
grantPermissionManager                   Grant permission for permission configuration by address.
revokePermissionManager                  Revoke permission for permission configuration by address.
listPermissionManager                    Query permission information for permission configuration.
grantUserTableManager                    Grant permission for user table by table name and address.
revokeUserTableManager                   Revoke permission for user table by table name and address.
listUserTableManager                     Query permission for user table information.
grantDeployAndCreateManager              Grant permission for deploy contract and create user table by address.
revokeDeployAndCreateManager             Revoke permission for deploy contract and create user table by address.
listDeployAndCreateManager               Query permission information for deploy contract and create user table.
grantNodeManager                         Grant permission for node configuration by address.
revokeNodeManager                        Revoke permission for node configuration by address.
listNodeManager                          Query permission information for node configuration.
grantCNSManager                          Grant permission for CNS by address.
revokeCNSManager                         Revoke permission for CNS by address.
listCNSManager                           Query permission information for CNS.
grantSysConfigManager                    Grant permission for system configuration by address.
revokeSysConfigManager                   Revoke permission for system configuration by address.
listSysConfigManager                     Query permission information for system configuration.
quit(q)                                  Quit console.
-------------------------------------------------------------------------------------
```
**注：**                                       
- help显示每条命令的含义是：命令 命令功能描述                   
- 查看具体命令的使用介绍说明，输入命令 -h或--help查看。例如：   
```bash
[group:1]> getBlockByNumber -h
Query information about a block by block number.
Usage: getBlockByNumber blockNumber [boolean]
blockNumber -- Integer of a block number.
boolean -- (optional) If true it returns the full transaction objects, if false only the hashes of the transactions.
```
### **switch**
运行switch或者s，切换到连接节点的指定群组。群组号显示在命令提示符前面。
```bash
[group:1]> switch 2
Switched to group 2.

[group:2]> 
```

### **getBlockNumber**
运行getBlockNumber，查看区块高度。

```bash
[group:1]> getBlockNumber
90
```
### **getSealerList**
运行getSealerList，查看共识节点列表。

```bash
[group:1]> getSealerList 
[
	0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801,
	10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34,
	622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73
]
```

### **getObserverList**
运行getObserverList，查看观察节点列表。
```bash
[group:1]> getObserverList
[
037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772
]
```
### **getNodeIDList**
运行getNodeIDList，查看节点及连接p2p节点的nodeId列表。
```bash
[group:1]> getNodeIDList
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8
]
```

### **getPbftView**
运行getPbftView，查看pbft视图。

```bash
[group:1]> getPbftView  
2730
```
### **getConsensusStatus**
运行getConsensusStatus，查看共识状态。

```bash
[group:1]> getConsensusStatus
[
    {
        "accountType":1,
        "allowFutureBlocks":true,
        "cfgErr":false,
        "connectedNodes":3,
        "consensusedBlockNumber":6,
        "currentView":40,
        "groupId":1,
        "highestblockHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
        "highestblockNumber":5,
        "leaderFailed":false,
        "max_faulty_leader":1,
        "node index":3,
        "nodeId":"ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec",
        "nodeNum":4,
        "omitEmptyBlock":true,
        "protocolId":264,
        "sealer.0":"0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
        "sealer.1":"2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
        "sealer.2":"cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
        "sealer.3":"ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec",
        "toView":40
    },
    [
        {
            "0x0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278":39
        },
        {
            "0x2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108":36
        },
        {
            "0xcf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd":37
        },
        {
            "0xed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec":40
        }
    ],
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
        "committedPrepareCache_blockHash":"0xbbf80db21fa393143280e01b4b711eaddd54103e95f370b389af5c0504b1eea5",
        "committedPrepareCache_height":5,
        "committedPrepareCache_idx":"1",
        "committedPrepareCache_view":"17"
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
运行getSyncStatus，查看同步状态。

```bash
[group:1]> getSyncStatus
{
    "blockNumber":5,
    "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "isSyncing":false,
    "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
    "nodeId":"cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
    "peers":[
        {
            "blockNumber":5,
            "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
            "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
            "nodeId":"0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278"
        },
        {
            "blockNumber":5,
            "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
            "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
            "nodeId":"2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108"
        },
        {
            "blockNumber":5,
            "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
            "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
            "nodeId":"ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
        }
    ],
    "protocolId":265,
    "txPoolSize":"0"
}
```

### **getNodeVersion**
运行getNodeVersion，查看节点的版本。
```bash
[group:1]> getNodeVersion
{
	"Build Time":"20190107 10:15:23",
	"Build Type":"Linux/g++/RelWithDebInfo",
	"FISCO-BCOS Version":"2.0.0",
	"Git Branch":"release-2.0.1",
	"Git Commit Hash":"be95a6e3e85b621860b101c3baeee8be68f5f450"
}
```
### **getPeers**
运行getPeers，查看节点的peers。
```bash
[group:1]> getPeers
[
	{
		"IPAndPort":"127.0.0.1:50723",
		"nodeId":"8718579e9a6fee647b3d7404d59d66749862aeddef22e6b5abaafe1af6fc128fc33ed5a9a105abddab51e12004c6bfe9083727a1c3a22b067ddbaac3fa349f7f",
		"Topic":[
			
		]
	},
	{
		"IPAndPort":"127.0.0.1:50719",
		"nodeId":"697e81e512cffc55fc9c506104fb888a9ecf4e29eabfef6bb334b0ebb6fc4ef8fab60eb614a0f2be178d0b5993464c7387e2b284235402887cdf640f15cb2b4a",
		"Topic":[
			
		]
	},
	{
		"IPAndPort":"127.0.0.1:30304",
		"nodeId":"8fc9661baa057034f10efacfd8be3b7984e2f2e902f83c5c4e0e8a60804341426ace51492ffae087d96c0b968bd5e92fa53ea094ace8d1ba72de6e4515249011",
		"Topic":[
			
		]
	}
]
```
### **getGroupPeers**
运行getGroupPeers，查看节点所在group的共识节点和观察节点列表。
```bash
[group:1]> getGroupPeers
[
    cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd,
    ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec,
    0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278,
    2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108
]
```
### **getGroupList**
运行getGroupList，查看群组列表:
```bash
[group:1]> getGroupList
[1]
```
### **getBlockByHash**
运行getBlockByHash，根据区块哈希查询区块信息。              
参数：
- 区块哈希：0x开头的区块哈希值。   
- 交易标志：默认false，区块中的交易只显示交易哈希，设置为true，显示交易具体信息。
```bash
[group:1]> getBlockByHash 0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855
{
    "extraData":[
        
    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":"0x1",
    "parentHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "sealer":"0x0",
    "sealerList":[
        "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
        "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
        "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
        "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],
    "stateRoot":"0x9711819153f7397ec66a78b02624f70a343b49c60bc2f21a77b977b0ed91cef9",
    "timestamp":"0x1692f119c84",
    "transactions":[
        "0xa14638d47cc679cf6eeb7f36a6d2a30ea56cb8dcf0938719ff45023a7a8edb5d"
    ],
    "transactionsRoot":"0x516787f85980a86fd04b0e9ce82a1a75950db866e8cdf543c2cae3e4a51d91b7"
}
[group:1]> getBlockByHash 0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855 true
{
    "extraData":[
        
    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":"0x1",
    "parentHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "sealer":"0x0",
    "sealerList":[
        "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
        "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
        "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
        "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],
    "stateRoot":"0x9711819153f7397ec66a78b02624f70a343b49c60bc2f21a77b977b0ed91cef9",
    "timestamp":"0x1692f119c84",
    "transactions":[
        {
            "blockHash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
            "blockNumber":"0x1",
            "from":"0x7234c32327795e4e612164e3442cfae0d445b9ad",
            "gas":"0x1c9c380",
            "gasPrice":"0x1",
            "hash":"0xa14638d47cc679cf6eeb7f36a6d2a30ea56cb8dcf0938719ff45023a7a8edb5d",
            "input":"0x608060405234801561001057600080fd5b506040805190810160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6102d7806101166000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634ed3885e146100515780636d4ce63c146100ba575b600080fd5b34801561005d57600080fd5b506100b8600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061014a565b005b3480156100c657600080fd5b506100cf610164565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561010f5780820151818401526020810190506100f4565b50505050905090810190601f16801561013c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b8060009080519060200190610160929190610206565b5050565b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101fc5780601f106101d1576101008083540402835291602001916101fc565b820191906000526020600020905b8154815290600101906020018083116101df57829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024757805160ff1916838001178555610275565b82800160010185558215610275579182015b82811115610274578251825591602001919060010190610259565b5b5090506102829190610286565b5090565b6102a891905b808211156102a457600081600090555060010161028c565b5090565b905600a165627a7a72305820fd74886bedbe51a7f3d834162de4d9af7f276c70133e04fd6776b5fbdd3653000029",
            "nonce":"0x3443a1391c9c29f751e8350304efb310850b8afbaa7738f5e89ddfce79b1d6",
            "to":null,
            "transactionIndex":"0x0",
            "value":"0x0"
        }
    ],
    "transactionsRoot":"0x516787f85980a86fd04b0e9ce82a1a75950db866e8cdf543c2cae3e4a51d91b7"
}
```
### **getBlockByNumber**
运行getBlockByNumber，根据区块高度查询区块信息。                            
参数：     
- 区块高度：十进制整数。    
- 交易标志：默认false，区块中的交易只显示交易哈希，设置为true，显示交易具体信息。      
```bash
[group:1]> getBlockByNumber 1  
{
    "extraData":[
        
    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":"0x1",
    "parentHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "sealer":"0x0",
    "sealerList":[
        "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
        "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
        "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
        "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],
    "stateRoot":"0x9711819153f7397ec66a78b02624f70a343b49c60bc2f21a77b977b0ed91cef9",
    "timestamp":"0x1692f119c84",
    "transactions":[
        "0xa14638d47cc679cf6eeb7f36a6d2a30ea56cb8dcf0938719ff45023a7a8edb5d"
    ],
    "transactionsRoot":"0x516787f85980a86fd04b0e9ce82a1a75950db866e8cdf543c2cae3e4a51d91b7"
}
```
### **getBlockHashByNumber**
运行getBlockHashByNumber，通过区块高度获得区块哈希。              
参数：           
- 区块高度：十进制整数。
```bash
[group:1]> getBlockHashByNumber 1
0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855
```
### **getTransactionByHash**
运行getTransactionByHash，通过交易哈希查询交易信息。                   
参数：        
- 交易哈希：0x开头的交易哈希值。
```bash
[group:1]> getTransactionByHash 0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac
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
运行getTransactionByBlockHashAndIndex，通过区块哈希和交易索引查询交易信息。              
参数：         
- 区块哈希：0x开头的区块哈希值。       
- 交易索引：十进制整数。     
```bash
[group:1]> getTransactionByBlockHashAndIndex 0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02 0
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
运行getTransactionByBlockNumberAndIndex，通过区块高度和交易索引查询交易信息。              
参数：      
- 区块高度：十进制整数。
- 交易索引：十进制整数。
```bash
[group:1]> getTransactionByBlockNumberAndIndex 2 0
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
运行getTransactionReceipt，通过交易哈希查询交易回执。              
参数：
- 交易哈希：0x开头的交易哈希值。
```bash
[group:1]> getTransactionReceipt 0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac
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
运行getPendingTransactions，查询等待处理的交易。              
```bash
[group:1]> getPendingTransactions
[]
```

### **getPendingTxSize**
运行getPendingTxSize，查询等待处理的交易数量。              
```bash
[group:1]> getPendingTxSize
0
```

### **getCode**
运行getCode，根据合约地址查询合约代码。                                                   
参数：
- 合约地址：0x的合约地址(部署合约可以获得合约地址)。
- 
```bash
[group:1]> getCode 0x97b8c19598fd781aaeb53a1957ef9c8acc59f705
0x60606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100465780636d4ce63c14610066575bfe5b341561004e57fe5b610064600480803590602001909190505061008c565b005b341561006e57fe5b61007661028c565b6040518082815260200191505060405180910390f35b8060006001015410806100aa57506002600101548160026001015401105b156100b457610289565b806000600101540360006001018190555080600260010160008282540192505081905550600480548060010182816100ec919061029a565b916000526020600020906004020160005b608060405190810160405280604060405190810160405280600881526020017f32303137303431330000000000000000000000000000000000000000000000008152508152602001600060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815250909190915060008201518160000190805190602001906101ec9291906102cc565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b600060026001015490505b90565b8154818355818115116102c7576004028160040283600052602060002091820191016102c6919061034c565b5b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061030d57805160ff191683800117855561033b565b8280016001018555821561033b579182015b8281111561033a57825182559160200191906001019061031f565b5b50905061034891906103d2565b5090565b6103cf91905b808211156103cb57600060008201600061036c91906103f7565b6001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556002820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600382016000905550600401610352565b5090565b90565b6103f491905b808211156103f05760008160009055506001016103d8565b5090565b90565b50805460018160011615610100020316600290046000825580601f1061041d575061043c565b601f01602090049060005260206000209081019061043b91906103d2565b5b505600a165627a7a723058203c1f95b4a803493db0120df571d9f5155734152548a532412f2f9fa2dbe1ac730029
```

### **getTotalTransactionCount**
运行getTotalTransactionCount，查询当前块高和总交易数。                          
```bash
[group:1]> getTotalTransactionCount
{
	"blockNumber":1,
	"txSum":1
}
```
### **deploy**
运行deploy，部署合约。(默认提供HelloWorld合约和TableTest.sol进行示例使用)
参数：
- 合约名称：部署的合约名称(可以带.sol后缀)，即HelloWorld或者HelloWorld.sol均可。                          
                           
```bash
# 部署HelloWorld合约
[group:1]> deploy HelloWorld.sol
0xb3c223fc0bf6646959f254ac4e4a7e355b50a344

# 部署TableTest合约
[group:1]> deploy TableTest.sol 
0x3554a56ea2905f366c345bd44fa374757fb4696a
```
**注：部署用户编写的合约，只需要将solidity合约文件放到控制台根目录的`solidity/contracts/`目录下，然后进行部署即可。按tab键可以搜索`solidity/contracts`目录下的合约名称。**

### **call**
运行call，调用合约。                                
参数： 
- 合约名称：部署的合约名称(可以带.sol后缀)。
- 合约地址: 部署合约获取的地址。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定，**参数空格分隔，字符串需要双引号标识。**
```bash
# 调用HelloWorld的get接口获取name字符串
[group:1]> call HelloWorld.sol 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, World!

# 调用HelloWorld的set设置name字符串
[group:1]> call HelloWorld.sol 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 set "Hello,FISCO-BCOS"
0x21dca087cb3e44f44f9b882071ec6ecfcb500361cad36a52d39900ea359d0895

# 调用HelloWorld的get接口获取name字符串，检查设置是否生效
[group:1]> call HelloWorld.sol 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello,FISCO-BCOS

# 调用TableTest的create接口创建用户表t_test
[group:1]> call TableTest.sol 0x3554a56ea2905f366c345bd44fa374757fb4696a create
0x09fea224ce266c26a927c01668f4b28224f4b7b58399790e8534c055a698fc37

# 调用TableTest的insert接口插入记录，字段为name, item_id, item_name
[group:1]> call TableTest.sol 0x3554a56ea2905f366c345bd44fa374757fb4696a insert "fruit" 1 "apple"
0x7206d0a6e30f57795475a66ae18169dd65d9994f4ea5af1e3e469364d9f0b392

# 调用TableTest的select接口查询记录
[group:1]> call TableTest.sol 0x3554a56ea2905f366c345bd44fa374757fb4696a select "fruit"
[[fruit], [1], [apple]]
```

### **deployByCNS**
deployByCNS，利用[CNS](../design/features/CNS_contract_name_service.md)部署合约。                                 
参数：
- 合约名称：部署的合约名称。
- 合约版本号：部署的合约版本号(长度不能超过40)。
```bash
# 部署HelloWorld合约
[group:1]> deploy HelloWorld.sol 1.0
0x3554a56ea2905f366c345bd44fa374757fb4696a

# 部署TableTest合约
[group:1]> deploy TableTest.sol 1.0
0x0b33d383e8e93c7c8083963a4ac4a58b214684a8
```

### **queryCNS**
运行queryCNS，根据合约名称和合约版本号（可选参数）查询CNS表记录信息。                                 
参数：
- 合约名称：部署的合约名称。
- 合约版本号：(可选)部署的合约版本号。
```bash
[group:1]> queryCNS HelloWorld.sol 
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x3554a56ea2905f366c345bd44fa374757fb4696a  |
---------------------------------------------------------------------------------------------

[group:1]> queryCNS HelloWorld 1.0
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x3554a56ea2905f366c345bd44fa374757fb4696a  |
---------------------------------------------------------------------------------------------
```
### **callByCNS**
运行callByCNS，利用CNS调用合约。                                 
参数： 
- 合约名称：部署的合约名称。
- 合约版本号：部署的合约版本号。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定，参数空格分隔，字符串需要双引号标识。
```bash
# 调用HelloWorld的get接口获取name字符串
[group:1]> callByCNS HelloWorld 1.0 set "Hello,CNS"
0x80bb37cc8de2e25f6a1cdcb6b4a01ab5b5628082f8da4c48ef1bbc1fb1d28b2d

# 调用HelloWorld的set设置name字符串
[group:1]> callByCNS HelloWorld 1.0 get
Hello,CNS
```

### **addSealer**
运行addSealer，将节点添加为共识节点。                                 
参数： 
- 节点nodeId
```bash
[group:1]> addSealer
ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":0,
	"msg":"success"
}
```

### **addObserver**
运行addObserver，将节点添加为观察节点。                                 
参数： 
- 节点nodeId
```bash
[group:1]> addObserver ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":0,
	"msg":"success"
}
```

### **removeNode**
运行removeNode，节点退出。通过addSealer命令可以将退出的节点添加为共识节点，通过addObserver命令将节点添加为观察节点。                                  
参数： 
- 节点nodeId
```bash
[group:1]> removeNode ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":0,
	"msg":"success"
}
```
### **setSystemConfigByKey**
运行setSystemConfigByKey，以键值对方式设置系统配置。目前设置的系统配置支持`tx_count_limit`和`tx_gas_limit`。这个两个配置键名可以通过tab键补全。                          
参数： 
- key
- value
```bash
[group:1]> setSystemConfigByKey tx_count_limit 100
{
	"code":0,
	"msg":"success"
}
```
### **getSystemConfigByKey**
运行getSystemConfigByKe，根据键查询系统配置的值。                                  
参数： 
- key
```bash
[group:1]> getSystemConfigByKey tx_count_limit
100
```
### **grantPermissionManager**
运行grantPermissionManager，赋予外部账号地址的管理权限的权限。**即设置权限管理员账号。**

参数： 
- 外部账号地址
```bash
[group:1]> grantPermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
**注意：0xc0d0e6ccc0b44c12196266548bec4a3616160e7d地址为管理员账号，该账号登录控制台可以操作后续相关的权限功能。可以使用该账号对应的私钥登录控制台，私钥为ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e，登录控制台命令如下：其中启动脚本第一个参数为群组ID，第二个参数为账号对应的私钥。**
```
./start.sh 1 ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e
```
### **listPermissionManager**
运行listPermissionManager，查询拥有管理权限的权限记录列表。                                  
```bash
[group:1]> listPermissionManager 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokePermissionManager**
运行revokePermissionManager，撤销外部账号地址的权限管理权限。                                                                 
参数： 
- 外部账号地址
```bash
[group:1]> revokePermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantUserTableManager**
运行grantUserTableManager，根据用户表名和外部账号地址赋予权限。                                  
参数： 
- 表名
- 外部账号地址
```bash
[group:1]> grantUserTableManager t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listUserTableManager**
运行listUserTableManager，根据用户表名查询赋予的权限记录列表。                                  
参数： 
- 表名
```bash
[group:1]> listUserTableManager t_test 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeUserTableManager**
运行revokeUserTableManager，根据用户表名和外部账号地址撤销权限。                                                                 
参数： 
- 表名
- 外部账号地址
```bash
[group:1]> revokeUserTableManager t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantDeployAndCreateManager**
运行grantDeployAndCreateManager，赋予外部账号地址的部署合约和创建用户表权限。

参数： 
- 外部账号地址
```bash
[group:1]> grantDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listDeployAndCreateManager**
运行listDeployAndCreateManager，查询拥有部署合约和创建用户表权限的权限记录列表。                                  
```bash
[group:1]> listDeployAndCreateManager 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeDeployAndCreateManager**
运行revokeDeployAndCreateManager，撤销外部账号地址的部署合约和创建用户表权限。                                                                 
参数： 
- 外部账号地址
```bash
[group:1]> revokeDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantNodeManager**
运行grantNodeManager，赋予外部账号地址的节点管理权限。

参数： 
- 外部账号地址
```bash
[group:1]> grantNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listNodeManager**
运行listNodeManager，查询拥有节点管理的权限记录列表。

```bash
[group:1]> listNodeManager 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeNodeManager**
运行revokeNodeManager，撤销外部账号地址的节点管理权限。                                                                 
参数： 
- 外部账号地址
```bash
[group:1]> revokeNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantCNSManager**
运行grantCNSManager，赋予外部账号地址的使用CNS权限。

参数： 
- 外部账号地址
```bash
[group:1]> grantCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listCNSManager**
运行listCNSManager，查询拥有使用CNS的权限记录列表。
                                  
```bash
[group:1]> listCNSManager 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeCNSManager**
运行revokeCNSManager，撤销外部账号地址的使用CNS权限。                                                                 
参数： 
- 外部账号地址
```bash
[group:1]> revokeCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantSysConfigManager**
运行grantSysConfigManager，赋予外部账号地址的系统参数管理权限。

参数： 
- 外部账号地址
```bash
[group:1]> grantSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listSysConfigManager**
运行listSysConfigManager，查询拥有系统参数管理的权限记录列表。
                                  
```bash
[group:1]> listSysConfigManager 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeSysConfigManager**
运行revokeSysConfigManager，撤销外部账号地址的系统参数管理权限。                                                                 
参数： 
- 外部账号地址
```bash
[group:1]> revokeSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **quit**
运行quit或q，退出控制台。
```bash
quit
```