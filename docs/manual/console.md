# 控制台

[控制台](https://github.com/FISCO-BCOS/console)是FISCO BCOS 2.0重要的交互式客户端工具，它通过[Web3SDK](../sdk/sdk.md)与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity合约文件编译为Java合约文件。

### 控制台命令
控制台命令由两部分组成，即指令和指令相关的参数：   
- **指令**: 指令是执行的操作命令，包括查询区块链相关信息，部署合约和调用合约的指令等，其中部分指令调用JSON-RPC接口，因此与JSON-RPC接口同名。
**使用提示： 指令可以使用tab键补全，并且支持按上下键显示历史输入指令。**
    
- **指令相关的参数**: 指令调用接口需要的参数，指令与参数以及参数与参数之间均用空格分隔，与JSON-RPC接口同名命令的输入参数和获取信息字段的详细解释参考[JSON-RPC API](../api.md)。

常用命令链接：
- 查看区块高度：[getBlockNumber](./console.html#getblocknumber)
- 查看共识节点列表：[getSealerList](./console.html#getsealerlist)
- 部署合约: [deploy](./console.html#deploy)
- 调用合约: [call](./console.html#call)
- 切换群组: [switch](./console.html#switch)

### 控制台响应
当发起一个控制台命令时，控制台会获取命令执行的结果，并且在终端展示执行结果，执行结果分为2类：
- **正确结果:** 命令返回正确的执行结果，以字符串或是json的形式返回。       
- **错误结果:** 命令返回错误的执行结果，以字符串或是json的形式返回。 
  - 控制台的命令调用JSON-RPC接口时，错误码[参考这里](../design/rpc.html#id6)。
  - 控制台的命令调用Precompiled Service接口时，错误码[参考这里](../sdk/sdk.html#precompiled-service-api)。

## 控制台配置与运行

```eval_rst
.. important::
    前置条件：搭建FISCO BCOS区块链请参考 `建链脚本 <./build_chain.html>`_ 或 `企业工具 <../enterprise_tools/index.html>`_。
```
### 获取控制台

```bash
$ cd ~ && mkdir fisco && cd fisco
# 获取控制台
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/download_console.sh)
```
目录结构如下：
```bash
|-- apps # 控制台jar包目录
|   -- console.jar 
|-- lib # 相关依赖的jar包目录
|-- conf
|   |-- applicationContext-sample.xml # 配置文件
|   |-- log4j.properties  # 日志配置文件
|   |-- privateKey.properties # 发送交易的私钥存储文件
|-- solidity # 控制台命令部署和调用的合约所在目录
|   -- contracts  # 部署和调用合约的solidity合约存储目录
|       -- HelloWorld.sol # 普通合约：HelloWorld合约，可部署和调用
|       -- TableTest.sol # 使用CRUD接口的合约：TableTest合约，可部署和调用
|       -- Table.sol # CRUD需要引入的合约接口：Table合约接口
|-- start.sh # 控制台启动脚本
|-- replace_solc_jar.sh # 编译jar包替换脚本
|-- tools # 控制台工具目录
    |-- contracts # 用户编写的solidity合约存放目录
    |   |-- Table.sol # 默认提供CRUD的合约接口Table.sol文件
    |-- sol2java.sh # solidity合约文件编译为java合约文件的工具脚本
```

#### 合约编译工具

**控制台提供一个专门的编译合约工具，方便开发者将Solidity合约文件编译为Java合约文件。** 使用该工具，分为两步：
  - 将Solidity合约文件放在`tools/contracts`目录下。
  - 通过运行`tools`目录下的`sol2java.sh`脚本(**需要指定一个Java的包名**)完成编译合约任务。例如，拷贝`HelloWorld.sol`合约到`tools/contracts`目录下，指定包名为`org.com.fisco`，命令如下：
    ```bash
    $ cd ~/fisco/console
    $ cp solidity/contracts/HelloWorld.sol tools/contracts/
    $ cd tools
    $ ./sol2java.sh org.com.fisco
    ```
    运行成功之后，将会在`console/tools`目录生成java、abi和bin目录，如下所示。
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
    java目录下生成了`org/com/fisco/`包路径目录。包路径目录下将会生成Java合约文件`HelloWorld.java`和`Table.java`。其中`HelloWorld.java`是java应用所需要的java合约文件。

**注：** 下载的控制台其`console/lib`目录下包含`solcJ-all-0.4.25.jar`，因此支持0.4版本的合约编译。如果使用0.5版本合约编译器或国密合约编译器，请下载相关合约编译器jar包，然后替换`console/lib`目录下的`solcJ-all-0.4.25.jar`。可以通过`./replace_solc_jar.sh`脚本进行替换，指定下载的编译器jar包路径，命令如下：
```bash
# 下载solcJ-all-0.5.2.jar放在console目录下，示例用法如下
$ ./replace_solc_jar.sh solcJ-all-0.5.2.jar
```

#### 下载合约编译jar包
0.4版本合约编译jar包
```bash
$ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/solcj/solcJ-all-0.4.25.jar
```
0.5版本合约编译jar包
```bash
$ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/solcj/solcJ-all-0.5.2.jar
```
国密0.4版本合约编译jar包
```bash
$ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/solcj/solcJ-all-0.4.25-gm.jar
```
国密0.5版本合约编译jar包
```bash
$ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/solcj/solcJ-all-0.5.2-gm.jar
```

### 配置控制台
- 区块链节点和证书的配置：
  - 将节点sdk目录下的`ca.crt`、`node.crt`和`node.key`文件拷贝到`conf`目录下。
  - 将`conf`目录下的`applicationContext-sample.xml`文件重命名为`applicationContext.xml`文件。配置`applicationContext.xml`文件，其中添加注释的内容根据区块链节点配置做相应修改。

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
                        <list>  <!-- 每个群组需要配置一个bean -->
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
  配置项详细说明[参考这里](../sdk/sdk.html#spring)。

```eval_rst
.. important::

    控制台配置说明

    - 如果控制台配置正确，但是在CentOS系统上启动控制台出现如下错误：
    
      Failed to connect to the node. Please check the node status and the console configruation.

     则是因为使用了CentOS系统自带的JDK版本(会导致控制台与区块链节点认证失败)，请从 `OpenJDK官网 <https://jdk.java.net/java-se-ri/8>`_ 或 `Oracle官网 <https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`_ 下载并安装Java 8或以上版本(具体安装步骤 `参考附录 <./console.html#java>`_ )，安装完毕后再启动控制台。

    - 当控制台配置文件在一个群组内配置多个节点连接时，由于群组内的某些节点在操作过程中可能退出群组，因此控制台轮询节点查询时，其返回信息可能不一致，属于正常现象。建议使用控制台时，配置一个节点或者保证配置的节点始终在群组中，这样在同步时间内查询的群组内信息保持一致。

``` 
### 启动控制台
```text
$ ./start.sh
# 输出下述信息表明启动成功
=====================================================================================
Welcome to FISCO BCOS console(1.0.3)!
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
#### 查看启动脚本帮助说明：
```bash
$ ./start.sh --help
Usage
start console: 	./start.sh [groupID] [privateKey]
print console version: 	./start.sh --version
```
#### 查看当前控制台版本：
```bash
./start.sh --version
console version: 1.0.3
```
#### 启动控制台：
```bash
$ ./start.sh [groupID] [privateKey]   
```
启动命令可以指定两个可选参数：           
- `groupId`: 群组ID, 不指定则默认为群组1。           
- `privateKey`: 交易发送者外部账号的私钥，不指定则默认从`conf`目录下的privateKey.properties中读取私钥，如果该文件内容被清空，则随机生成外部账号私钥并将生产的私钥保持在该私钥配置文件中。 

示例
```bash
# 以群组2，私钥账号地址为3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4登录控制台
$ ./start.sh 2 3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4  
```

## 控制台命令
### **help**
输入help或者h，查看控制台所有的命令。

```text
[group:1]> help
-------------------------------------------------------------------------------------
addObserver                              Add an observer node.
addSealer                                Add a sealer node.
call                                     Call a contract by a function and paramters.
callByCNS                                Call a contract by a function and paramters by CNS.
deploy                                   Deploy a contract on blockchain.
deployByCNS                              Deploy a contract on blockchain by CNS.
exit                                     Quit console.
getBlockByHash                           Query information about a block by hash.
getBlockByNumber                         Query information about a block by block number.
getBlockHashByNumber                     Query block hash by block number.
getBlockNumber                           Query the number of most recent block.
getCode                                  Query code at a given address.
getConsensusStatus                       Query consensus status.
getDeployLog                             Query the log of deployed contracts.
getGroupList                             Query group list.
getGroupPeers                            Query nodeId list for sealer and observer nodes.
getNodeIDList                            Query nodeId list for all connected nodes.
getNodeVersion                           Query the current node version.
getObserverList                          Query nodeId list for observer nodes.
getPbftView                              Query the pbft view of node.
getPeers                                 Query peers currently connected to the client.
getPendingTransactions                   Query pending transactions.
getPendingTxSize                         Query pending transactions size.
getSealerList                            Query nodeId list for sealer nodes.
getSyncStatus                            Query sync status.
getSystemConfigByKey                     Query a system config value by key.
getTotalTransactionCount                 Query total transaction count.
getTransactionByBlockHashAndIndex        Query information about a transaction by block hash and transaction index position.
getTransactionByBlockNumberAndIndex      Query information about a transaction by block number and transaction index position.
getTransactionByHash                     Query information about a transaction requested by transaction hash.
getTransactionReceipt                    Query the receipt of a transaction by transaction hash.
grantCNSManager                          Grant permission for CNS by address.
grantDeployAndCreateManager              Grant permission for deploy contract and create user table by address.
grantNodeManager                         Grant permission for node configuration by address.
grantPermissionManager                   Grant permission for permission configuration by address.
grantSysConfigManager                    Grant permission for system configuration by address.
grantUserTableManager                    Grant permission for user table by table name and address.
help(h)                                  Provide help information.
listCNSManager                           Query permission information for CNS.
listDeployAndCreateManager               Query permission information for deploy contract and create user table.
listNodeManager                          Query permission information for node configuration.
listPermissionManager                    Query permission information for permission configuration.
listSysConfigManager                     Query permission information for system configuration.
listUserTableManager                     Query permission for user table information.
queryCNS                                 Query CNS information by contract name and contract version.
quit(q)                                  Quit console.
removeNode                               Remove a node.
revokeCNSManager                         Revoke permission for CNS by address.
revokeDeployAndCreateManager             Revoke permission for deploy contract and create user table by address.
revokeNodeManager                        Revoke permission for node configuration by address.
revokePermissionManager                  Revoke permission for permission configuration by address.
revokeSysConfigManager                   Revoke permission for system configuration by address.
revokeUserTableManager                   Revoke permission for user table by table name and address.
setSystemConfigByKey                     Set a system config.
switch(s)                                Switch to a specific group by group ID.
-------------------------------------------------------------------------------------
```
**注：**                                       
- help显示每条命令的含义是：命令 命令功能描述                   
- 查看具体命令的使用介绍说明，输入命令 -h或\--help查看。例如：   

```text
[group:1]> getBlockByNumber -h
Query information about a block by block number.
Usage: getBlockByNumber blockNumber [boolean]
blockNumber -- Integer of a block number, from 0 to 2147483647.
boolean -- (optional) If true it returns the full transaction objects, if false only the hashes of the transactions.
```
### **switch**
运行switch或者s，切换到指定群组。群组号显示在命令提示符前面。

```text
[group:1]> switch 2
Switched to group 2.

[group:2]> 
```
**注：** 需要切换的群组，请确保在`console/conf`目录下的`applicationContext.xml`(该配置文件初始状态只提供群组1的配置)文件中配置了该群组的信息，并且该群组中中配置的节点ip和端口正确，该节点正常运行。

### **getBlockNumber**
运行getBlockNumber，查看区块高度。

```text
[group:1]> getBlockNumber
90
```
### **getSealerList**
运行getSealerList，查看共识节点列表。

```text
[group:1]> getSealerList 
[
    0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801,
    10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34,
    622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73
]
```

### **getObserverList**
运行getObserverList，查看观察节点列表。

```text
[group:1]> getObserverList
[
    037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772
]
```
### **getNodeIDList**
运行getNodeIDList，查看节点及连接p2p节点的nodeId列表。
```text
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

```text
[group:1]> getPbftView  
2730
```
### **getConsensusStatus**
运行getConsensusStatus，查看共识状态。

```text
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
            "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278":39
        },
        {
            "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108":36
        },
        {
            "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd":37
        },
        {
            "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec":40
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

```text
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
```text
[group:1]> getNodeVersion
{
	"Build Time":"20190107 10:15:23",
	"Build Type":"Linux/g++/RelWithDebInfo",
	"FISCO-BCOS Version":"2.0.0-rc1",
	"Git Branch":"master",
	"Git Commit Hash":"be95a6e3e85b621860b101c3baeee8be68f5f450"
}
```
### **getPeers**
运行getPeers，查看节点的peers。
```text
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
```text
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
```text
[group:1]> getGroupList
[1]
```
### **getBlockByHash**
运行getBlockByHash，根据区块哈希查询区块信息。              
参数：
- 区块哈希：0x开头的区块哈希值。   
- 交易标志：默认false，区块中的交易只显示交易哈希，设置为true，显示交易具体信息。
```text
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
```text
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
```text
[group:1]> getBlockHashByNumber 1
0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855
```
### **getTransactionByHash**
运行getTransactionByHash，通过交易哈希查询交易信息。                   
参数：        
- 交易哈希：0x开头的交易哈希值。
```text
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
```text
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
```text
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
```text
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
```text
[group:1]> getPendingTransactions
[]
```

### **getPendingTxSize**
运行getPendingTxSize，查询等待处理的交易数量。              
```text
[group:1]> getPendingTxSize
0
```

### **getCode**
运行getCode，根据合约地址查询合约代码。                                                   
参数：
- 合约地址：0x的合约地址(部署合约可以获得合约地址)。
```text
[group:1]> getCode 0x97b8c19598fd781aaeb53a1957ef9c8acc59f705
0x60606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100465780636d4ce63c14610066575bfe5b341561004e57fe5b610064600480803590602001909190505061008c565b005b341561006e57fe5b61007661028c565b6040518082815260200191505060405180910390f35b8060006001015410806100aa57506002600101548160026001015401105b156100b457610289565b806000600101540360006001018190555080600260010160008282540192505081905550600480548060010182816100ec919061029a565b916000526020600020906004020160005b608060405190810160405280604060405190810160405280600881526020017f32303137303431330000000000000000000000000000000000000000000000008152508152602001600060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815250909190915060008201518160000190805190602001906101ec9291906102cc565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b600060026001015490505b90565b8154818355818115116102c7576004028160040283600052602060002091820191016102c6919061034c565b5b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061030d57805160ff191683800117855561033b565b8280016001018555821561033b579182015b8281111561033a57825182559160200191906001019061031f565b5b50905061034891906103d2565b5090565b6103cf91905b808211156103cb57600060008201600061036c91906103f7565b6001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556002820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600382016000905550600401610352565b5090565b90565b6103f491905b808211156103f05760008160009055506001016103d8565b5090565b90565b50805460018160011615610100020316600290046000825580601f1061041d575061043c565b601f01602090049060005260206000209081019061043b91906103d2565b5b505600a165627a7a723058203c1f95b4a803493db0120df571d9f5155734152548a532412f2f9fa2dbe1ac730029
```

### **getTotalTransactionCount**
运行getTotalTransactionCount，查询当前块高和总交易数。                          
```text
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
                           
```text
# 部署HelloWorld合约
[group:1]> deploy HelloWorld.sol
contract address:0xb3c223fc0bf6646959f254ac4e4a7e355b50a344

# 部署TableTest合约
[group:1]> deploy TableTest.sol 
contract address:0x3554a56ea2905f366c345bd44fa374757fb4696a
```
**注：**
- 部署用户编写的合约，只需要将solidity合约文件放到控制台根目录的`solidity/contracts/`目录下，然后进行部署即可。按tab键可以搜索`solidity/contracts`目录下的合约名称。
- 若需要部署的合约引用了其他其他合约或library库，引用格式为`import "./XXX.sol";`。其相关引入的合约和library库均放在`solidity/contracts/`目录。
- 如果合约引用了library库，library库文件的名称需要包含`Lib`字符串，以便于区分是普通合约与library库文件。library库文件不能单独部署和调用。

### **getDeployLog**
运行getDeployLog，查询群组内部署合约的日志信息。日志信息包括部署合约的时间，群组ID，合约名称和合约地址。参数：
- 日志行数，可选，根据输入的期望值返回最新的日志信息，当实际条数少于期望值时，按照实际数量返回。当期望值未给出时，默认按照20条返回最新的日志信息。
                           
```text
[group:1]> getDeployLog 2

2019-03-19 23:04:10  [group:1]  TableTest             0x7eec2ac59357866677ab0fa3db4e7dc2b391f7c2
2019-03-19 23:04:14  [group:1]  HelloWorld            0x9fde55d2bc8650fc71cc8a4b6dbe9662b5a3b615

[group:1]> getDeployLog 1

2019-03-19 23:04:14  [group:1]  HelloWorld            0x9fde55d2bc8650fc71cc8a4b6dbe9662b5a3b615
```
**注：** 如果要查看所有的部署合约日志信息，请查看`console`目录下的`deploylog.txt`文件。该文件只存储最近10000条部署合约的日志记录。

### **call**
运行call，调用合约。                                
参数： 
- 合约名称：部署的合约名称(可以带.sol后缀)。
- 合约地址: 部署合约获取的地址，合约地址可以省略前缀0，例如，0x000ac78可以简写成0xac78。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定。**参数由空格分隔，其中字符串、字节类型参数需要加上双引号；数组参数需要加上中括号，比如[1,2,3]，数组中是字符串或字节类型，加双引号，例如[“alice”,”bob”]；布尔类型为true或者false。**
```text
# 调用HelloWorld的get接口获取name字符串
[group:1]> call HelloWorld.sol 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, World!

# 调用HelloWorld的set接口设置name字符串
[group:1]> call HelloWorld.sol 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 set "Hello, FISCO BCOS"
0x21dca087cb3e44f44f9b882071ec6ecfcb500361cad36a52d39900ea359d0895

# 调用HelloWorld的get接口获取name字符串，检查设置是否生效
[group:1]> call HelloWorld.sol 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, FISCO BCOS

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
**注：** TableTest.sol合约代码[参考这里](smart_contract.html#solidity)。

### **deployByCNS**
运行deployByCNS，利用[CNS](../design/features/cns_contract_name_service.md)部署合约。                                 
参数：
- 合约名称：部署的合约名称。
- 合约版本号：部署的合约版本号(长度不能超过40)。
```text
# 部署HelloWorld合约1.0版
[group:1]> deployByCNS HelloWorld.sol 1.0
contract address:0x3554a56ea2905f366c345bd44fa374757fb4696a

# 部署HelloWorld合约2.0版
[group:1]> deployByCNS HelloWorld.sol 2.0
contract address:0x07625453fb4a6459cbf14f5aa4d574cae0f17d92

# 部署TableTest合约
[group:1]> deployByCNS TableTest.sol 1.0
contract address:0x0b33d383e8e93c7c8083963a4ac4a58b214684a8
```

### **queryCNS**
运行queryCNS，根据合约名称和合约版本号（可选参数）查询CNS表记录信息。                                 
参数：
- 合约名称：部署的合约名称。
- 合约版本号：(可选)部署的合约版本号。
```text
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
- 合约名称与合约版本号：合约名称与版本号用英文冒号分隔，例如`HelloWorld:1.0`或`HelloWorld.sol:1.0`。当省略合约版本号时，例如`HelloWorld`或`HelloWorld.sol`，则调用最新版本的合约。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定。**参数由空格分隔，其中字符串、字节类型参数需要加上双引号；数组参数需要加上中括号，比如[1,2,3]，数组中是字符串或字节类型，加双引号，例如["alice","bob"]；布尔类型为true或者false。**
```text
# 调用HelloWorld合约1.0版，通过set接口设置name字符串
[group:1]> callByCNS HelloWorld:1.0 set "Hello,CNS"
0x80bb37cc8de2e25f6a1cdcb6b4a01ab5b5628082f8da4c48ef1bbc1fb1d28b2d

# 调用HelloWorld合约2.0版，通过set接口设置name字符串
[group:1]> callByCNS HelloWorld:2.0 set "Hello,CNS2"
0x43000d14040f0c67ac080d0179b9499b6885d4a1495d3cfd1a79ffb5f2945f64

# 调用HelloWorld合约1.0版，通过get接口获取name字符串
[group:1]> callByCNS HelloWorld:1.0 get
Hello,CNS

# 调用HelloWorld合约最新版(即2.0版)，通过get接口获取name字符串
[group:1]> callByCNS HelloWorld get
Hello,CNS2
```

### **addSealer**
运行addSealer，将节点添加为共识节点。                                 
参数： 
- 节点nodeId
```text
[group:1]> addSealer ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":0,
	"msg":"success"
}
```

### **addObserver**
运行addObserver，将节点添加为观察节点。                                 
参数： 
- 节点nodeId
```text
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
```text
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
```text
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
```text
[group:1]> getSystemConfigByKey tx_count_limit
100
```
### **grantPermissionManager**
运行grantPermissionManager，赋予外部账号地址的管理权限的权限。**即设置权限管理员账号。** 参数： 
- 外部账号地址
```text
[group:1]> grantPermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```

```eval_rst
.. important::
    0xc0d0e6ccc0b44c12196266548bec4a3616160e7d地址为管理员账号，该账号登录控制台可以操作后续相关的权限功能。可以使用该账号对应的私钥登录控制台，私钥为ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e，登录控制台命令如下：其中启动脚本第一个参数为群组ID，第二个参数为账号对应的私钥。
```

```
./start.sh 1 ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e
```
### **listPermissionManager**
运行listPermissionManager，查询拥有管理权限的权限记录列表。                                  
```text
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
```text
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
```text
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
```text
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
```text
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
```text
[group:1]> grantDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listDeployAndCreateManager**
运行listDeployAndCreateManager，查询拥有部署合约和创建用户表权限的权限记录列表。                                  
```text
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
```text
[group:1]> revokeDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantNodeManager**
运行grantNodeManager，赋予外部账号地址的节点管理权限。参数： 
- 外部账号地址
```text
[group:1]> grantNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listNodeManager**
运行listNodeManager，查询拥有节点管理的权限记录列表。

```text
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
```text
[group:1]> revokeNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantCNSManager**
运行grantCNSManager，赋予外部账号地址的使用CNS权限。参数： 
- 外部账号地址
```text
[group:1]> grantCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listCNSManager**
运行listCNSManager，查询拥有使用CNS的权限记录列表。
                                  
```text
[group:1]> listCNSManager 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeCNSManager**
运行revokeCNSManager，撤销外部账号地址的使用CNS权限。参数： 
- 外部账号地址
```text
[group:1]> revokeCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantSysConfigManager**
运行grantSysConfigManager，赋予外部账号地址的系统参数管理权限。参数： 
- 外部账号地址
```text
[group:1]> grantSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listSysConfigManager**
运行listSysConfigManager，查询拥有系统参数管理的权限记录列表。
                                  
```text
[group:1]> listSysConfigManager 
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeSysConfigManager**
运行revokeSysConfigManager，撤销外部账号地址的系统参数管理权限。参数： 
- 外部账号地址
```text
[group:1]> revokeSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **quit**
运行quit、q或exit，退出控制台。
```text
quit
```

## 附录：Java环境配置

### Ubuntu环境安装Java
```bash
# 安装默认Java版本(Java 8或以上)
sudo apt install -y default-jdk
# 查询Java版本
java -version 
```

### CentOS环境安装Java
```bash
# 查询centos原有的Java版本
$ rpm -qa|grep java
# 删除查询到的Java版本
$ rpm -e --nodeps java版本
# 查询Java版本，没有出现版本号则删除完毕
$ java -version
# 创建新的文件夹，安装Java 8或以上的版本，将下载的jdk放在software目录
# 从openJDK官网(https://jdk.java.net/java-se-ri/8)或Oracle官网(https://www.oracle.com/technetwork/java/javase/downloads/index.html)选择Java 8或以上的版本下载，例如下载jdk-8u201-linux-x64.tar.gz
$ mkdir /software
# 解压jdk 
$ tar -zxvf jdk-8u201-linux-x64.tar.gz
# 配置Java环境，编辑/etc/profile文件 
$ vim /etc/profile 
# 打开以后将下面三句输入到文件里面并退出
export JAVA_HOME=/software/jdk-8u201-linux-x64.tar.gz
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
# 生效profile
$ source /etc/profile 
# 查询Java版本，出现的版本是自己下载的版本，则安装成功。
java -version 
```

