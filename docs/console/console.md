# 控制台1.x版本

标签：``控制台1``

----
```eval_rst
.. important::
    - ``控制台2.6+`` 基于 `Java SDK <../sdk/java_sdk/index.html>`_ 实现，``控制台1.x`` 系列基于 `Web3SDK <../sdk/java_sdk.html>`_ 实现，本教程针对 **1.x版本控制台**，2.6及其以上版本控制台使用文档请 `参考这里 <./console_of_java_sdk.html>`_ 
    - 可通过命令 ``./start.sh --version`` 查看当前控制台版本
    - 基于 `Web3SDK <../sdk/java_sdk.html>`_ 开发应用过程中将 ``solidity`` 代码转换为 ``java`` 代码时，必须使用 ``1.x`` 版本控制台，具体请参考  `这里 <../console/download_console.html>`_ 
```

[控制台](https://github.com/FISCO-BCOS/console)是FISCO BCOS 2.0重要的交互式客户端工具，它通过[Web3SDK](../sdk/java_sdk.md)与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity合约文件编译为Java合约文件。

## 控制台配置与运行

```eval_rst
.. important::
    前置条件：搭建FISCO BCOS区块链，请参考 `搭建第一个区块链网络 <../installation.html>`_
    建链工具参考：`开发部署工具 <../manual/build_chain.html>`_ 或 `运维部署工具 <../enterprise_tools/index.html>`_。
```

### 获取控制台

```bash
cd ~ && mkdir -p fisco && cd fisco
# 获取控制台
curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.7.2/download_console.sh && bash download_console.sh -c 1.2.0
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh -c 1.2.0`
```

目录结构如下：
```bash
|-- apps # 控制台jar包目录
|   -- console.jar
|-- lib # 相关依赖的jar包目录
|-- conf
|   |-- applicationContext-sample.xml # 配置文件
|   |-- log4j.properties  # 日志配置文件
|-- contracts # 合约所在目录
|   -- solidity  # solidity合约存放目录
|       -- HelloWorld.sol # 普通合约：HelloWorld合约，可部署和调用
|       -- TableTest.sol # 使用CRUD接口的合约：TableTest合约，可部署和调用
|       -- Table.sol # 提供CRUD操作的接口合约
|   -- console  # 控制台部署合约时编译的合约abi, bin，java文件目录
|   -- sdk      # sol2java.sh脚本编译的合约abi, bin，java文件目录
|-- start.sh # 控制台启动脚本
|-- get_account.sh # 账户生成脚本
|-- get_gm_account.sh # 账户生成脚本，国密版
|-- sol2java.sh # solidity合约文件编译为java合约文件的开发工具脚本
```

**注意：默认下载的控制台内置`0.4.25`版本的`solidity`编译器，用户需要编译`0.5`或者`0.6`版本的合约时，可以通过下列命令获取内置对应编译器版本的控制台**

```bash
# 0.5
curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v1.1.0/download_console.sh && bash download_console.sh -v 0.5
# 0.6
curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v1.1.0/download_console.sh && bash download_console.sh -v 0.6
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，0.5版本请尝试命令： `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/v1.1.0/tools/download_console.sh && bash download_console.sh -v 0.5` 
    0.6版本请尝试命令： `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/v1.1.0/tools/download_console.sh && bash download_console.sh -v 0.6`

```

### 配置控制台
- 区块链节点和证书的配置：
  - 将节点sdk目录下的`ca.crt`、`sdk.crt`和`sdk.key`文件拷贝到`conf`目录下。
  - 将`conf`目录下的`applicationContext-sample.xml`文件重命名为`applicationContext.xml`文件。配置`applicationContext.xml`文件，其中添加注释的内容根据区块链节点配置做相应修改。**提示：如果搭链时设置的channel_listen_ip(若节点版本小于v2.3.0，查看配置项listen_ip)为127.0.0.1或者0.0.0.0，channel_port为20200， 则`applicationContext.xml`配置不用修改。**
  - FISCO-BCOS 2.5及之后的版本，添加了SDK只能连本机构节点的限制，操作时需确认拷贝证书的路径，否则建链报错。

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
                <property name="agencyName" value="fisco" />
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>

</beans>
```
  配置项详细说明[参考这里](../sdk/java_sdk.html#spring)。

```eval_rst
.. important::

    控制台说明

    - 控制台启动失败

      参考，`附录：JavaSDK启动失败场景 <../sdk/java_sdk.html#id22>`_。

    - 当控制台配置文件在一个群组内配置多个节点连接时，由于群组内的某些节点在操作过程中可能退出群组，因此控制台轮询节点查询时，其返回信息可能不一致，属于正常现象。建议使用控制台时，配置一个节点或者保证配置的节点始终在群组中，这样在同步时间内查询的群组内信息保持一致。

```

### 配置国密版控制台
国密版的控制台配置与非国密版控制台的配置流程有一些区别，流程如下：
- 区块链节点和证书的配置：
  - 将节点sdk目录下的`ca.crt`、`sdk.crt`和`sdk.key`文件拷贝到`conf`目录下。
  - 将`conf`目录下的`applicationContext-sample.xml`文件重命名为`applicationContext.xml`文件。配置`applicationContext.xml`文件，其中添加注释的内容根据区块链节点配置做相应修改。**提示：如果搭链时设置的channel_listen_ip(若节点版本小于v2.3.0，查看配置项listen_ip)为127.0.0.1或者0.0.0.0，channel_port为20200， 则`applicationContext.xml`配置不用修改。**
  - FISCO-BCOS 2.5及之后的版本，添加了SDK只能连本机构节点的限制，操作时需确认拷贝证书的路径，否则建链报错。

- 打开国密开关
```
<bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
    <!-- encryptType值设置为1，打开国密开关 -->
    <constructor-arg value="1"/> <!-- 0:standard 1:guomi -->
</bean>
```

```eval_rst
.. important::

    控制台编译工具重要说明

    - 控制台自1.1.0版本起，移除对solcJ-all-0.x.x.jar、solcJ-all-0.x.x-gm.jar的依赖，控制台使用新的合约编译工具，新编译工具上传至maven仓库进行管理，不再需要进行替换文件操作

    - 新编译工具支持0.4.25、0.5.2、0.6.10三个版本，与同版本的solidity编译器对应

    - 控制台默认配置0.4.25版本编译工具，用户可以修改build.gradle配置的版本号重新编译，也可以通过download_console.sh脚本指定-v参数，下载配置对应编译器版本的控制台

    - 新的编译工具同时支持国密、非国密编译功能，控制台国密或者非国密环境运行时，不再需要solcJ国密与非国密版本的替换
```

#### 合约编译工具

**控制台提供一个专门的编译合约工具，方便开发者将solidity合约文件编译为java合约文件。** 
```shell
$ bash sol2java.sh -h
# Compile Solidity Tool
./sol2java.sh [packageName] [solidityFilePath] [javaCodeOutputDir]
 	 packageName:
 		 the package name of the generated Java class file
 	 solidityFilePath:
 		 (optional) the solidity file path or the directory where solidity files located, default: contracts/solidity
 	 javaCodeOutputDir:
 		 (optional) the directory where the generated Java files located, default: contracts/sdk/java
```
参数
- `packageName`: 生成`Java`文件的包名
- `solidityFilePath`: (可选)`solidity`文件的路径，支持文件路径和目录路径两种方式，参数为目录时将目录下所有的`solidity`文件进行编译转换。默认目录为`contracts/solidity`。
- `javaCodeOutputDir`: (可选)生成`Java`文件的目录，默认生成在`contracts/sdk/java`目录。 

使用
```bash
$ cd ~/fisco/console
$ ./sol2java.sh org.com.fisco # 指定java包名
```
运行成功之后，将会在`console/contracts/sdk`目录生成java、abi和bin目录，如下所示。
```bash
|-- abi # 编译生成的abi目录，存放solidity合约编译的abi文件
|   |-- HelloWorld.abi
|   |-- Table.abi
|   |-- TableTest.abi
|-- bin # 编译生成的bin目录，存放solidity合约编译的bin文件
|   |-- HelloWorld.bin
|   |-- Table.bin
|   |-- TableTest.bin
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|       |-- com
|           |-- fisco
|               |-- HelloWorld.java # 编译的HelloWorld Java文件
|               |-- Table.java  # 编译的CRUD接口合约 Java文件
|               |-- TableTest.java  # 编译的TableTest Java文件
```
java目录下生成了`org/com/fisco/`包路径目录。包路径目录下将会生成java合约文件`HelloWorld.java`、`TableTest.java`和`Table.java`。其中`HelloWorld.java`和`TableTest.java`是java应用所需要的java合约文件。

```eval_rst
.. important::

    Java合约文件说明

    - 控制台自1.1.0版本起，生成的Java合约文件国密、非国密环境均可以运行，国密与非国密环境生成一份合约代码即可
```

### 启动控制台

在节点正在运行的情况下，启动控制台：

```text
$ ./start.sh
# 输出下述信息表明启动成功
=====================================================================================
Welcome to FISCO BCOS console(1.0.4)!
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
#### 查看当前控制台版本：
```bash
./start.sh --version
console version: 1.2.0
```
#### 账户使用方式

##### 控制台加载私钥
控制台提供账户生成脚本get_account.sh(脚本用法请参考[账户管理文档](../manual/account.md)，生成的的账户文件在accounts目录下，控制台加载的账户文件必须放置在该目录下。
控制台启动方式有如下几种：
```
./start.sh
./start.sh groupID
./start.sh groupID -pem pemName
./start.sh groupID -p12 p12Name
```
##### 默认启动
使用控制台配置文件指定的默认群组号启动。
```bash
./start.sh
```

**注意**: 控制台启动未指定私钥账户时，会尝试从`accounts`目录下加载一个可用的私钥账户用于发送交易，加载失败则会创建一个新的`PEM`格式的账户文件，将其保存在`accounts`目录下。

##### 指定群组号启动
使用命令行指定的群组号启动。
```bash
./start.sh 2
```
- 注意：指定的群组在控制台配置文件中需要配置bean。

##### 使用PEM格式私钥文件启动
- 使用指定的pem文件的账户启动，输入参数：群组号、-pem、pem文件路径
```bash
./start.sh 1 -pem accounts/0xebb824a1122e587b17701ed2e512d8638dfb9c88.pem
```
##### 使用PKCS12格式私钥文件启动
- 使用指定的p12文件的账户，需要输入密码，输入参数：群组号、-p12、p12文件路径
```bash
./start.sh 1 -p12 accounts/0x5ef4df1b156bc9f077ee992a283c2dbb0bf045c0.p12
Enter Export Password:
```
**注意：**
控制台启动时加载p12文件出现下面报错：
```shell
exception unwrapping private key - java.security.InvalidKeyException: Illegal key size
```
可能是Java版本的原因，参考解决方案：[https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size](https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size)

### 控制台命令结构
控制台命令由两部分组成，即指令和指令相关的参数：
- **指令**: 指令是执行的操作命令，包括查询区块链相关信息，部署合约和调用合约的指令等，其中部分指令调用JSON-RPC接口，因此与JSON-RPC接口同名。
**使用提示： 指令可以使用tab键补全，并且支持按上下键显示历史输入指令。**

- **指令相关的参数**: 指令调用接口需要的参数，指令与参数以及参数与参数之间均用空格分隔，与JSON-RPC接口同名命令的输入参数和获取信息字段的详细解释参考[JSON-RPC API](../api.md)。

### 常用命令链接
#### 合约相关命令
  - 利用[CNS](../design/features/cns_contract_name_service.md)部署和调用合约(**推荐**)
    - 部署合约: [deployByCNS](./console.html#deploybycns)
    - 调用合约: [callByCNS](./console.html#callbycns)
    - 查询CNS部署合约信息: [queryCNS](./console.html#querycns)
  - 普通部署和调用合约
    - 部署合约: [deploy](./console.html#deploy)
    - 调用合约: [call](./console.html#call)
#### 其他命令
- 查询区块高度：[getBlockNumber](./console.html#getblocknumber)
- 查询共识节点列表：[getSealerList](./console.html#getsealerlist)
- 查询交易回执信息: [getTransactionReceipt](./console.html#gettransactionreceipt)
- 切换群组: [switch](./console.html#switch)

### 快捷键
- `Ctrl+A`：光标移动到行首
- `Ctrl+D`：退出控制台
- `Ctrl+E`：光标移动到行尾
- `Ctrl+R`：搜索输入的历史命令
- &uarr;：向前浏览历史命令
- &darr;：向后浏览历史命令


### 控制台响应
当发起一个控制台命令时，控制台会获取命令执行的结果，并且在终端展示执行结果，执行结果分为2类：
- **正确结果:** 命令返回正确的执行结果，以字符串或是json的形式返回。
- **错误结果:** 命令返回错误的执行结果，以字符串或是json的形式返回。
  - 控制台的命令调用JSON-RPC接口时，错误码[参考这里](../api.html#rpc)。
  - 控制台的命令调用Precompiled Service接口时，错误码[参考这里](../api.html#precompiled-service-api)。


## 控制台命令列表
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
desc                                     Description table information.
exit                                     Quit console.
getBlockHeaderByHash                     Query information about a block header by hash.
getBlockHeaderByNumber                   Query information about a block header by block number.
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
getNodeInfo                              Query the specified node information.
getObserverList                          Query nodeId list for observer nodes.
getPbftView                              Query the pbft view of node.
getPeers                                 Query peers currently connected to the client.
getPendingTransactions                   Query pending transactions.
getPendingTxSize                         Query pending transactions size.
getSealerList                            Query nodeId list for sealer nodes.
getSyncStatus                            Query sync status.
getSystemConfigByKey                     Query a system config value by key.
setSystemConfigByKey                     Set a system config value by key.
getTotalTransactionCount                 Query total transaction count.
getTransactionByBlockHashAndIndex        Query information about a transaction by block hash and transaction index position.
getTransactionByBlockNumberAndIndex      Query information about a transaction by block number and transaction index position.
getTransactionByHash                     Query information about a transaction requested by transaction hash.
getTransactionReceipt                    Query the receipt of a transaction by transaction hash.
getTransactionByHashWithProof            Query the transaction and transaction proof by transaction hash.
getTransactionReceiptByHashWithProof     Query the receipt and transaction receipt proof by transaction hash.
grantCNSManager                          Grant permission for CNS by address.
grantDeployAndCreateManager              Grant permission for deploy contract and create user table by address.
grantNodeManager                         Grant permission for node configuration by address.
grantSysConfigManager                    Grant permission for system configuration by address.
grantUserTableManager                    Grant permission for user table by table name and address.
help(h)                                  Provide help information.
listCNSManager                           Query permission information for CNS.
listDeployAndCreateManager               Query permission information for deploy contract and create user table.
listNodeManager                          Query permission information for node configuration.
listSysConfigManager                     Query permission information for system configuration.
listUserTableManager                     Query permission for user table information.
queryCNS                                 Query CNS information by contract name and contract version.
quit(q)                                  Quit console.
removeNode                               Remove a node.
revokeCNSManager                         Revoke permission for CNS by address.
revokeDeployAndCreateManager             Revoke permission for deploy contract and create user table by address.
revokeNodeManager                        Revoke permission for node configuration by address.
revokeSysConfigManager                   Revoke permission for system configuration by address.
revokeUserTableManager                   Revoke permission for user table by table name and address.
listContractWritePermission              Query the account list which have write permission of the contract.
grantContractWritePermission             Grant the account the contract write permission.
revokeContractWritePermission            Revoke the account the contract write permission.
grantContractStatusManager               Grant contract authorization to the user.
getContractStatus                        Get the status of the contract.
listContractStatusManager                List the authorization of the contract.
grantCommitteeMember                     Grant the account committee member
revokeCommitteeMember                    Revoke the account from committee member
listCommitteeMembers                     List all committee members
grantOperator                            Grant the account operator
revokeOperator                           Revoke the operator
listOperators                            List all operators
updateThreshold                          Update the threshold
queryThreshold                           Query the threshold
updateCommitteeMemberWeight              Update the committee member weight
queryCommitteeMemberWeight               Query the committee member weight
freezeAccount                            Freeze the account.
unfreezeAccount                          Unfreeze the account.
getAccountStatus                         GetAccountStatus of the account.
freezeContract                           Freeze the contract.
unfreezeContract                         Unfreeze the contract.
switch(s)                                Switch to a specific group by group ID.
[create sql]                             Create table by sql.
[delete sql]                             Remove records by sql.
[insert sql]                             Insert records by sql.
[select sql]                             Select records by sql.
[update sql]                             Update records by sql.
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
**注：** 需要切换的群组，请确保在`console/conf`目录下的`applicationContext.xml`(该配置文件初始状态只提供群组1的配置)文件中配置了该群组的信息，并且该群组中配置的节点ip和端口正确，该节点正常运行。

### **newAccount**
创建新的发送交易的账户，默认会以`PEM`格式将账户保存在`accounts`目录下。

```text
[group:1]> newAccount
 new account successfully, account address:0x4cb7d6c013d9c7fa4ec75a3df3d0fddf39674c14

# 私钥文件自动保存在accounts目录下
$ ls -al accounts/0x4cb7d6c013d9c7fa4ec75a3df3d0fddf39674c14.pem
$ -rw-r--r--  1 octopus  staff  258  9 30 16:34 accounts/0x4cb7d6c013d9c7fa4ec75a3df3d0fddf39674c14.pem
```

### **loadAccount**
加载`PEM`或者`P12`格式的私钥文件，加载的私钥可以用于发送交易签名。
参数：

- 私钥文件路径: 支持相对路径、绝对路径和默认路径三种方式。用户输入文件名时，会从默认目录获取文件，默认目录为: `accounts`。
- 账户密码: (可选)`P12`私钥文件的密码。

```text
[group:1]> loadAccount 0xa0f749a6eb735d578b81239c1661c726c4f05d0e.pem
 load 0xa0f749a6eb735d578b81239c1661c726c4f05d0e.pem successfully, account address: 0xa0f749a6eb735d578b81239c1661c726c4f05d0e
```
**注意：加载的私钥需要使用`switchAccount`才可以用与发送交易，也可以使用`listAccount`查看当前加载的所有私钥**

### **switchAccount**
切换发送交易的私钥账户。
参数：

- 账户地址
```text
[group:1]> switchAccount 0xa0f749a6eb735d578b81239c1661c726c4f05d0e
switch to account: 0xa0f749a6eb735d578b81239c1661c726c4f05d0e successfully.
```

### **listAccount**
查看当前加载的所有账户信息

```text
[group:1]> listAccount
 account list:
	 0xa0f749a6eb735d578b81239c1661c726c4f05d0e <=
	 0x4cb7d6c013d9c7fa4ec75a3df3d0fddf39674c14
```

**注意：带有`<=`后缀标记的为当前用于发送交易的私钥账户，可以使用`switchAccount`进行切换**

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
	"FISCO-BCOS Version":"2.0.0",
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

### **getBlockHeaderByHash**
运行getBlockHeaderByHash，根据区块哈希查询区块头信息。
参数：
- 区块哈希：0x开头的区块哈希值
- 签名列表标志：默认为false，即：区块头信息中不显示区块签名列表信息，设置为true，则显示区块签名列表。

```text
[group:1]> getBlockHeaderByHash 0x99576e7567d258bd6426ddaf953ec0c953778b2f09a078423103c6555aa4362d
{
    "dbHash":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "extraData":[

    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0x99576e7567d258bd6426ddaf953ec0c953778b2f09a078423103c6555aa4362d",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":1,
    "parentHash":"0x4f6394763c33c1709e5a72b202ad4d7a3b8152de3dc698cef6f675ecdaf20a3b",
    "receiptsRoot":"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2",
    "sealer":"0x2",
    "sealerList":[
        "11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f",
        "78a313b426c3de3267d72b53c044fa9fe70c2a27a00af7fea4a549a7d65210ed90512fc92b6194c14766366d434235c794289d66deff0796f15228e0e14a9191",
        "95b7ff064f91de76598f90bc059bec1834f0d9eeb0d05e1086d49af1f9c2f321062d011ee8b0df7644bd54c4f9ca3d8515a3129bbb9d0df8287c9fa69552887e",
        "b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36"
    ],
    "stateRoot":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "timestamp":"0x173ad8703d6",
    "transactionsRoot":"0xb563f70188512a085b5607cac0c35480336a566de736c83410a062c9acc785ad"
}
```

### **getBlockHeaderByNumber**
运行getBlockHeaderByNumber，根据区块高度查询区块头信息。
参数：
- 区块高度
- 签名列表标志：默认为false，即：区块头信息中不显示区块签名列表信息，设置为true，则显示区块签名列表。

```text
[group:1]> getBlockHeaderByNumber 1 true
{
    "dbHash":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "extraData":[

    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0x99576e7567d258bd6426ddaf953ec0c953778b2f09a078423103c6555aa4362d",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":1,
    "parentHash":"0x4f6394763c33c1709e5a72b202ad4d7a3b8152de3dc698cef6f675ecdaf20a3b",
    "receiptsRoot":"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2",
    "sealer":"0x2",
    "sealerList":[
        "11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f",
        "78a313b426c3de3267d72b53c044fa9fe70c2a27a00af7fea4a549a7d65210ed90512fc92b6194c14766366d434235c794289d66deff0796f15228e0e14a9191",
        "95b7ff064f91de76598f90bc059bec1834f0d9eeb0d05e1086d49af1f9c2f321062d011ee8b0df7644bd54c4f9ca3d8515a3129bbb9d0df8287c9fa69552887e",
        "b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36"
    ],
    "signatureList":[
        {
            "index":"0x3",
            "signature":"0xb5b41e49c0b2bf758322ecb5c86dc3a3a0f9b98891b5bbf50c8613a241f05f595ce40d0bb212b6faa32e98546754835b057b9be0b29b9d0c8ae8b38f7487b8d001"
        },
        {
            "index":"0x0",
            "signature":"0x411cb93f816549eba82c3bf8c03fa637036dcdee65667b541d0da06a6eaea80d16e6ca52bf1b08f77b59a834bffbc124c492ea7a1601d0c4fb257d97dc97cea600"
        },
        {
            "index":"0x1",
            "signature":"0xea3c27c2a1486c7942c41c4dc8f15fbf9a668aff2ca40f00701d73fa659a14317d45d74372d69d43ced8e81f789e48140e7fa0c61997fa7cde514c654ef9f26d00"
        }
    ],
    "stateRoot":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "timestamp":"0x173ad8703d6",
    "transactionsRoot":"0xb563f70188512a085b5607cac0c35480336a566de736c83410a062c9acc785ad"
}
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
- 合约名：可选，发送交易产生该交易的合约名称，使用该参数可以将交易中的input解析并输出。如果是部署合约交易则不解析。
```text
[group:1]> getTransactionByHash 0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gas":"0x11e1a300",
    "gasPrice":"0x11e1a300",
    "hash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "nonce":"0x1aec6e447da49b9a140bf39a91a4d75fd19ea77f7dc38ccf940d8d510d78bd0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionIndex":"0x0",
    "value":"0x0"
}
# input字段是合约接口的编码，解析后的内容包括接口签名，输入参数值。
[group:1]> getTransactionByHash 0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5 TableTest
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gas":"0x11e1a300",
    "gasPrice":"0x11e1a300",
    "hash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "nonce":"0x1aec6e447da49b9a140bf39a91a4d75fd19ea77f7dc38ccf940d8d510d78bd0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionIndex":"0x0",
    "value":"0x0"
}
---------------------------------------------------------------------------------------------
Input
function: insert(string,int256,string)
input value: (fruit, 1, apple)
---------------------------------------------------------------------------------------------
```
### **getTransactionByBlockHashAndIndex**
运行getTransactionByBlockHashAndIndex，通过区块哈希和交易索引查询交易信息。
参数：
- 区块哈希：0x开头的区块哈希值。
- 交易索引：十进制整数。
- 合约名：可选，发送交易产生该交易的合约名称，使用该参数可以将交易中的input解析并输出。如果是部署合约交易则不解析。
```text
[group:1]> getTransactionByBlockHashAndIndex 0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b 0
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gas":"0x11e1a300",
    "gasPrice":"0x11e1a300",
    "hash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "nonce":"0x1aec6e447da49b9a140bf39a91a4d75fd19ea77f7dc38ccf940d8d510d78bd0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionIndex":"0x0",
    "value":"0x0"
}

[group:1]> getTransactionByBlockHashAndIndex 0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b 0 TableTest
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gas":"0x11e1a300",
    "gasPrice":"0x11e1a300",
    "hash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "nonce":"0x1aec6e447da49b9a140bf39a91a4d75fd19ea77f7dc38ccf940d8d510d78bd0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionIndex":"0x0",
    "value":"0x0"
}
---------------------------------------------------------------------------------------------
Input
function: insert(string,int256,string)
input value: (fruit, 1, apple)
---------------------------------------------------------------------------------------------
```
### **getTransactionByBlockNumberAndIndex**
运行getTransactionByBlockNumberAndIndex，通过区块高度和交易索引查询交易信息。
参数：
- 区块高度：十进制整数。
- 交易索引：十进制整数。
- 合约名：可选，发送交易产生该交易的合约名称，使用该参数可以将交易中的input解析并输出。如果是部署合约交易则不解析。
```text
[group:1]> getTransactionByBlockNumberAndIndex 8 0
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gas":"0x11e1a300",
    "gasPrice":"0x11e1a300",
    "hash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "nonce":"0x1aec6e447da49b9a140bf39a91a4d75fd19ea77f7dc38ccf940d8d510d78bd0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionIndex":"0x0",
    "value":"0x0"
}

[group:1]> getTransactionByBlockNumberAndIndex 8 0 TableTest
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gas":"0x11e1a300",
    "gasPrice":"0x11e1a300",
    "hash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "nonce":"0x1aec6e447da49b9a140bf39a91a4d75fd19ea77f7dc38ccf940d8d510d78bd0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionIndex":"0x0",
    "value":"0x0"
}
---------------------------------------------------------------------------------------------
Input
function: insert(string,int256,string)
input value: (fruit, 1, apple)
---------------------------------------------------------------------------------------------
```
### **getTransactionReceipt**
运行getTransactionReceipt，通过交易哈希查询交易回执。
参数：
- 交易哈希：0x开头的交易哈希值。
- 合约名：可选，发送交易产生该交易回执的合约名称，使用该参数可以将交易回执中的input、output和event log解析并输出。（注：input字段在web3sdk 2.0.4版本中新增加的字段，之前版本无该字段则只解析output和event log。）
```text
[group:1]> getTransactionReceipt 0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "contractAddress":"0x0000000000000000000000000000000000000000",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gasUsed":"0x94f5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "logs":[
        {
            "address":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
            "data":"0x0000000000000000000000000000000000000000000000000000000000000001",
            "topics":[
                "0xc57b01fa77f41df77eaab79a0e2623fab2e7ae3e9530d9b1cab225ad65f2b7ce"
            ]
        }
    ],
    "logsBloom":"0x00000000000000800000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000800000000000000000000000000000000002000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "output":"0x0000000000000000000000000000000000000000000000000000000000000001",
    "status":"0x0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionHash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "transactionIndex":"0x0"
}

[group:1]> getTransactionReceipt 0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5 TableTest
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "contractAddress":"0x0000000000000000000000000000000000000000",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gasUsed":"0x94f5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "logs":[
        {
            "address":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
            "data":"0x0000000000000000000000000000000000000000000000000000000000000001",
            "topics":[
                "0xc57b01fa77f41df77eaab79a0e2623fab2e7ae3e9530d9b1cab225ad65f2b7ce"
            ]
        }
    ],
    "logsBloom":"0x00000000000000800000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000800000000000000000000000000000000002000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "output":"0x0000000000000000000000000000000000000000000000000000000000000001",
    "status":"0x0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionHash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "transactionIndex":"0x0"
}
---------------------------------------------------------------------------------------------
Input
function: insert(string,int256,string)
input value: (fruit, 1, apple)
---------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------
Output
function: insert(string,int256,string)
return type: (int256)
return value: (1)
---------------------------------------------------------------------------------------------
Event logs
event signature: InsertResult(int256) index: 0
event value: (1)
---------------------------------------------------------------------------------------------
```
### **getPendingTransactions**
运行getPendingTransactions，查询等待处理的交易。
```text
[group:1]> getPendingTransactions
[]
```

### **getPendingTxSize**
运行getPendingTxSize，查询等待处理的交易数量（交易池中的交易数量）。
```text
[group:1]> getPendingTxSize
0
```

### **getCode**

运行getCode，根据合约地址查询合约二进制代码。
参数：

- 合约地址：0x的合约地址(部署合约可以获得合约地址)。
```text
[group:1]> getCode 0x97b8c19598fd781aaeb53a1957ef9c8acc59f705
0x60606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100465780636d4ce63c14610066575bfe5b341561004e57fe5b610064600480803590602001909190505061008c565b005b341561006e57fe5b61007661028c565b6040518082815260200191505060405180910390f35b8060006001015410806100aa57506002600101548160026001015401105b156100b457610289565b806000600101540360006001018190555080600260010160008282540192505081905550600480548060010182816100ec919061029a565b916000526020600020906004020160005b608060405190810160405280604060405190810160405280600881526020017f32303137303431330000000000000000000000000000000000000000000000008152508152602001600060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815250909190915060008201518160000190805190602001906101ec9291906102cc565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b600060026001015490505b90565b8154818355818115116102c7576004028160040283600052602060002091820191016102c6919061034c565b5b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061030d57805160ff191683800117855561033b565b8280016001018555821561033b579182015b8281111561033a57825182559160200191906001019061031f565b5b50905061034891906103d2565b5090565b6103cf91905b808211156103cb57600060008201600061036c91906103f7565b6001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556002820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600382016000905550600401610352565b5090565b90565b6103f491905b808211156103f05760008160009055506001016103d8565b5090565b90565b50805460018160011615610100020316600290046000825580601f1061041d575061043c565b601f01602090049060005260206000209081019061043b91906103d2565b5b505600a165627a7a723058203c1f95b4a803493db0120df571d9f5155734152548a532412f2f9fa2dbe1ac730029
```

### **getTotalTransactionCount**

运行getTotalTransactionCount，查询当前块高和累计交易数（从块高为0开始）。
```text
[group:1]> getTotalTransactionCount
{
	"blockNumber":1,
	"txSum":1,
	"failedTxSum":0
}
```
### **deploy**

部署合约。(默认提供HelloWorld合约和TableTest.sol进行示例使用)
参数：

- 合约路径：合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`，比如：HelloWorld。

```text
# 部署HelloWorld合约，默认路径
[group:1]> deploy HelloWorld.sol
contract address:0xc0ce097a5757e2b6e189aa70c7d55770ace47767

# 部署HelloWorld合约，相对路径
[group:1]> deploy contracts/solidity/HelloWorld.sol
contract address:0xd653139b9abffc3fe07573e7bacdfd35210b5576

# 部署HelloWorld合约，绝对路径
[group:1]> deploy /root/fisco/console/contracts/solidity/HelloWorld.sol
contract address:0x85517d3070309a89357c829e4b9e2d23ee01d881
```

**注：**
- 部署用户编写的合约，可以将solidity合约文件放到控制台根目录的`contracts/solidity/`目录下，然后进行部署即可。按tab键可以搜索`contracts/solidity/`目录下的合约名称。
- 若需要部署的合约引用了其他合约或library库，引用格式为`import "./XXX.sol";`。其相关引入的合约和library库均放在`contracts/solidity/`目录。
- 如果合约引用了library库，library库文件的名称必须以`Lib`字符串开始，以便于区分是普通合约与library库文件。library库文件不能单独部署和调用。

### **listAbi**
显示合约接口和Event列表
参数：

- 合约路径：合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`，比如：TableTest。
- 合约名：(可选)合约名称，默认情况下使用合约文件名作为合约名参数

```text
[group:1]> listAbi TableTest
Method list:
 name                |    constant  |    methodId    |    signature
  --------------------------------------------------------------
 remove              |    false     |    0x0fe1160f  |    remove(string,int256)
 update              |    false     |    0x49cc36b5  |    update(string,int256,string)
 select              |    true      |    0x5b325d78  |    select(string)
 insert              |    false     |    0xe020d464  |    insert(string,int256,string)

Event list:
 name                |   topic                                                                   signature
  --------------------------------------------------------------
 remove              |   0x0fe1160f9655e87c29e76aca1cab34fb2a644d375da7a900c7076bad17cad26b  |   remove(string,int256)
 update              |   0x49cc36b56a9320d20b2d9a1938a972c849191bceb97500bfd38fa8a590dac73a  |   update(string,int256,string)
 select              |   0x5b325d7821528d3b52d0cc7a83e1ecef0438f763796770201020ac8b8813ac0a  |   select(string)
 insert              |   0xe020d464e502c11b54a7e37e568c78f0fcd360213eb5f4ac0a25a17733fc19f7  |   insert(string,int256,string)
```

### **getDeployLog**

运行getDeployLog，查询群组内**由当前控制台**部署合约的日志信息。日志信息包括部署合约的时间，群组ID，合约名称和合约地址。参数：
- 日志行数，可选，根据输入的期望值返回最新的日志信息，当实际条数少于期望值时，按照实际数量返回。当期望值未给出时，默认按照20条返回最新的日志信息。

```text
[group:1]> getDeployLog 2

2019-05-26 08:37:03  [group:1]  HelloWorld            0xc0ce097a5757e2b6e189aa70c7d55770ace47767
2019-05-26 08:37:45  [group:1]  TableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576

[group:1]> getDeployLog 1

2019-05-26 08:37:45  [group:1]  TableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576
```
**注：** 如果要查看所有的部署合约日志信息，请查看`console`目录下的`deploylog.txt`文件。该文件只存储最近10000条部署合约的日志记录。

### **call**

运行call，调用合约。
参数：
- 合约路径：合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`。
- 合约地址: 部署合约获取的地址，合约地址可以省略前缀0，例如，0x000ac78可以简写成0xac78。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定。**参数由空格分隔，其中字符串、字节类型参数需要加上双引号；数组参数需要加上中括号，比如[1,2,3]，数组中是字符串或字节类型，加双引号，例如[“alice”,”bob”]，注意数组参数中不要有空格；布尔类型为true或者false。**
```text
# 调用HelloWorld的get接口获取name字符串
[group:1]> call HelloWorld.sol 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 get
Hello, World!

# 调用HelloWorld的set接口设置name字符串
[group:1]> call HelloWorld.sol 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 set "Hello, FISCO BCOS"
transaction hash:0xa7c7d5ef8d9205ce1b228be1fe90f8ad70eeb6a5d93d3f526f30d8f431cb1e70
---------------------------------------------------------------------------------------------
transaction status: 0x0
description: transaction executed successfully

# 调用HelloWorld的get接口获取name字符串，检查设置是否生效
[group:1]> call HelloWorld.sol 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 get
Hello, FISCO BCOS

# 调用TableTest的insert接口插入记录，字段为name, item_id, item_name
[group:1]> call TableTest.sol 0xd653139b9abffc3fe07573e7bacdfd35210b5576 insert "fruit" 1 "apple"
transaction hash:0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1
---------------------------------------------------------------------------------------------
transaction status: 0x0
description: transaction executed successfully
---------------------------------------------------------------------------------------------

Output
function: insert(string,int256,string)
return type: (int256)
return value: (1)
---------------------------------------------------------------------------------------------
Event logs
event signature: InsertResult(int256) index: 0
event value: (1)
---------------------------------------------------------------------------------------------

# 调用TableTest的select接口查询记录
[group:1]> call TableTest.sol 0xd653139b9abffc3fe07573e7bacdfd35210b5576 select "fruit"
[[fruit], [1], [apple]]
```
**注：** TableTest.sol合约代码[参考这里](../manual/smart_contract.html#solidity)。

### **deployByCNS**

运行deployByCNS，采用[CNS](../design/features/cns_contract_name_service.md)部署合约。用CNS部署的合约，可用合约名直接调用。
参数：

- 合约路径：合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`。
- 合约版本号：部署的合约版本号(长度不能超过40)。
```text
# 部署HelloWorld合约1.0版
[group:1]> deployByCNS HelloWorld.sol 1.0
contract address:0x3554a56ea2905f366c345bd44fa374757fb4696a
---------------------------------------------------------------------------------------------
register contract to cns:
{
    "code":0,
    "msg":"success"
}
# 部署HelloWorld合约2.0版
[group:1]> deployByCNS HelloWorld.sol 2.0
contract address:0x07625453fb4a6459cbf14f5aa4d574cae0f17d92
---------------------------------------------------------------------------------------------
register contract to cns:
{
    "code":0,
    "msg":"success"
}
# 部署TableTest合约
[group:1]> deployByCNS TableTest.sol 1.0
contract address:0x0b33d383e8e93c7c8083963a4ac4a58b214684a8
---------------------------------------------------------------------------------------------
register contract to cns:
{
    "code":0,
    "msg":"success"
}
```
**注：**
- 部署用户编写的合约，可以将solidity合约文件放到控制台根目录的`contracts/solidity/`目录下，然后进行部署即可。按tab键可以搜索`contracts/solidity/`目录下的合约名称。
- 若需要部署的合约引用了其他合约或library库，引用格式为`import "./XXX.sol";`。其相关引入的合约和library库均放在`contracts/solidity/`目录。
- 如果合约引用了library库，library库文件的名称必须以`Lib`字符串开始，以便于区分是普通合约与library库文件。library库文件不能单独部署和调用。

### **queryCNS**
运行queryCNS，根据合约名称和合约版本号（可选参数）查询CNS表记录信息（合约名和合约地址的映射）。
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
运行callByCNS，采用CNS调用合约，即用合约名直接调用合约。
参数：

- 合约名称与合约版本号：合约名称与版本号用英文冒号分隔，例如`HelloWorld:1.0`。当省略合约版本号时，例如`HelloWorld`，则调用最新版本的合约。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定。**参数由空格分隔，其中字符串、字节类型参数需要加上双引号；数组参数需要加上中括号，比如[1,2,3]，数组中是字符串或字节类型，加双引号，例如["alice","bob"]；布尔类型为true或者false。**
```text
# 调用HelloWorld合约1.0版，通过set接口设置name字符串
[group:1]> callByCNS HelloWorld:1.0 set "Hello,CNS"
transaction hash:0x80bb37cc8de2e25f6a1cdcb6b4a01ab5b5628082f8da4c48ef1bbc1fb1d28b2d
---------------------------------------------------------------------------------------------
transaction status: 0x0
description: transaction executed successfully

# 调用HelloWorld合约2.0版，通过set接口设置name字符串
[group:1]> callByCNS HelloWorld:2.0 set "Hello,CNS2"
transaction hash:0x43000d14040f0c67ac080d0179b9499b6885d4a1495d3cfd1a79ffb5f2945f64
---------------------------------------------------------------------------------------------
transaction status: 0x0
description: transaction executed successfully

# 调用HelloWorld合约1.0版，通过get接口获取name字符串
[group:1]> callByCNS HelloWorld:1.0 get
Hello,CNS

# 调用HelloWorld合约最新版(即2.0版)，通过get接口获取name字符串
[group:1]> callByCNS HelloWorld get
Hello,CNS2
```

### **registerCNS**
注册合约至CNS。

参数：
- 合约路径: 合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`。
- 合约地址: 注册合约地址
- 合约版本号: 注册合约版本号(长度不能超过40)

```text
[group:1]> registerCNS HelloWorld 0xf19a7ec01f0b1adb16a033f0a30fb321ec6edcbf v1.0.0
{
    "code":0,
    "msg":"success"
}
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

运行setSystemConfigByKey，以键值对方式设置系统参数。目前设置的系统参数支持`tx_count_limit`,`tx_gas_limit`, `rpbft_epoch_sealer_num`, `rpbft_epoch_block_num`和`consensus_timeout`。这些系统参数的键名可以通过tab键补全：

* tx_count_limit：区块最大打包交易数
* tx_gas_limit：交易执行允许消耗的最大gas数
* rpbft_epoch_sealer_num: [rPBFT](../design/consensus/rpbft.md)系统配置，一个共识周期内选取的共识节点数目
* rpbft_epoch_block_num: [rPBFT](../design/consensus/rpbft.md)系统配置，一个共识周期出块数目
* consensus_timeout：PBFT共识过程中，每个区块执行的超时时间，默认为3s，单位为秒

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

运行getSystemConfigByKey，根据键查询系统参数的值。
参数：

- key
```text
[group:1]> getSystemConfigByKey tx_count_limit
100
```
### **grantPermissionManager**

```eval_rst	
.. note::	
    从 ``v1.0.9`` 开始，控制台不再支持 ``grantPermissionManager`` 命令，请使用 ``grantCommitteeMembers`` 和 ``grantOperator`` 等区块链委员权限管理相关的命令替代该命令。	
```

运行grantPermissionManager，授权账户的链管理员权限。参数：
- 账户地址
```text
[group:1]> grantPermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
**注：权限控制相关命令的示例使用可以参考[权限控制使用文档](./permission_control.md)。**

### **listPermissionManager**

```eval_rst	
.. note::	
    从``v1.0.9``开始，控制台不再支持 ``listPermissionManager`` 命令，请使用 ``listCommitteeMembers`` 和 ``listOperator`` 等区块链委员权限查询相关的命令替代该命令。	
```

运行listPermissionManager，查询拥有链管理员权限的账户列表。
```text
[group:1]> listPermissionManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokePermissionManager**

```eval_rst	
.. note::	
    从``v1.0.9``开始，控制台不再支持 ``revokePermissionManager`` 命令，请使用 ``revokeCommitteeMembers`` 和 ``revokeOperator`` 等区块链委员权限管理相关的命令替代该命令。	
```

运行revokePermissionManager，撤销账户的链管理员权限。
参数：
- 账户地址
```text
[group:1]> revokePermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantUserTableManager**

运行grantUserTableManager，授权账户对用户表的写权限。
参数：
- 表名
- 账户地址
```text
[group:1]> grantUserTableManager t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listUserTableManager**

运行listUserTableManager，查询拥有对用户表写权限的账号列表。
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

运行revokeUserTableManager，撤销账户对用户表的写权限。
参数：

- 表名
- 账户地址
```text
[group:1]> revokeUserTableManager t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantDeployAndCreateManager**
运行grantDeployAndCreateManager，授权账户的部署合约和创建用户表权限。

参数：
- 账户地址
```text
[group:1]> grantDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listDeployAndCreateManager**
运行listDeployAndCreateManager，查询拥有部署合约和创建用户表权限的账户列表。
```text
[group:1]> listDeployAndCreateManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeDeployAndCreateManager**
运行revokeDeployAndCreateManager，撤销账户的部署合约和创建用户表权限。
参数：
- 账户地址
```text
[group:1]> revokeDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantNodeManager**

运行grantNodeManager，授权账户的节点管理权限。参数：
- 账户地址
```text
[group:1]> grantNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listNodeManager**
运行listNodeManager，查询拥有节点管理的账户列表。

```text
[group:1]> listNodeManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeNodeManager**

运行revokeNodeManager，撤销账户的节点管理权限。
参数：
- 账户地址
```text
[group:1]> revokeNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantCNSManager**
运行grantCNSManager，授权账户的使用CNS权限。参数：
- 账户地址
```text
[group:1]> grantCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listCNSManager**
运行listCNSManager，查询拥有使用CNS的账户列表。

```text
[group:1]> listCNSManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeCNSManager**
运行revokeCNSManager，撤销账户的使用CNS权限。参数：
- 账户地址
```text
[group:1]> revokeCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantSysConfigManager**
运行grantSysConfigManager，授权账户的修改系统参数权限。参数：
- 账户地址
```text
[group:1]> grantSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listSysConfigManager**

运行listSysConfigManager，查询拥有修改系统参数的账户列表。

```text
[group:1]> listSysConfigManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```

### **revokeSysConfigManager**
运行revokeSysConfigManager，撤销账户的修改系统参数权限。参数：
- 账户地址
```text
[group:1]> revokeSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```

### **grantContractWritePermission**
运行grantContractWritePermission，添加账户对合约写接口的调用权限。参数：
- 合约地址
- 账户地址

```bash
[group:1]> grantContractWritePermission 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```

### **listContractWritePermission**
运行listContractWritePermission，显示对某个合约的写接口有调用权限的账户。参数：
- 合约地址
```bash
[group:1]> listContractWritePermission 0xc0ce097a5757e2b6e189aa70c7d55770ace47767
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                     11                      |
---------------------------------------------------------------------------------------------
```

### **revokeContractWritePermission**
运行revokeContractWritePermission，撤销账户对合约的写接口调用权限。参数：
- 合约地址
- 账户地址
```bash
[group:1]> revokeContractWritePermission 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
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

### **[create sql]**
运行create sql语句创建用户表，使用mysql语句形式。

```text
# 创建用户表t_demo，其主键为name，其他字段为item_id和item_name
[group:1]> create table t_demo(name varchar, item_id varchar, item_name varchar, primary key(name))
Create 't_demo' Ok.
```
**注意：**
- 创建表的字段类型均为字符串类型，即使提供数据库其他字段类型，也按照字符串类型设置。
- 必须指定主键字段。例如创建t_demo表，主键字段为name。
- 表的主键与关系型数据库中的主键不是相同概念，这里主键取值不唯一，区块链底层处理记录时需要传入主键值。
- 可以指定字段为主键，但设置的字段自增，非空，索引等修饰关键字不起作用。

### **desc**
运行desc语句查询表的字段信息，使用mysql语句形式。

```text
# 查询t_demo表的字段信息，可以查看表的主键名和其他字段名
[group:1]> desc t_demo
{
    "key":"name",
    "valueFields":"item_id,item_name"
}
```
### **[insert sql]**
运行insert sql语句插入记录，使用mysql语句形式。

```text
[group:1]> insert into t_demo (name, item_id, item_name) values (fruit, 1, apple1)
Insert OK, 1 row affected.
```
**注意：**
- 插入记录sql语句必须插入表的主键字段值。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

### **[select sql]**
运行select sql语句查询记录，使用mysql语句形式。

```text
# 查询包含所有字段的记录
select * from t_demo where name = fruit
{item_id=1, item_name=apple1, name=fruit}
1 row in set.

# 查询包含指定字段的记录
[group:1]> select name, item_id, item_name from t_demo where name = fruit
{name=fruit, item_id=1, item_name=apple1}
1 row in set.

# 插入一条新记录
[group:1]> insert into t_demo values (fruit, 2, apple2)
Insert OK, 1 row affected.

# 使用and关键字连接多个查询条件
[group:1]> select * from t_demo where name = fruit and item_name = apple2
{item_id=2, item_name=apple2, name=fruit}
1 row in set.

# 使用limit字段，查询第1行记录，没有提供偏移量默认为0
[group:1]> select * from t_demo where name = fruit limit 1
{item_id=1, item_name=apple1, name=fruit}
1 row in set.

# 使用limit字段，查询第2行记录，偏移量为1
[group:1]> select * from t_demo where name = fruit limit 1,1
{item_id=2, item_name=apple2, name=fruit}
1 rows in set.
```
**注意：**
- 查询记录sql语句必须在where子句中提供表的主键字段值。
- 关系型数据库中的limit字段可以使用，提供两个参数，分别offset(偏移量)和记录数(count)。
- where条件子句只支持and关键字，其他or、in、like、inner、join，union以及子查询、多表联合查询等均不支持。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

### **[update sql]**
运行update sql语句更新记录，使用mysql语句形式。

```text
[group:1]> update t_demo set item_name = orange where name = fruit and item_id = 1
Update OK, 1 row affected.
```
**注意：**
- 更新记录sql语句的where子句必须提供表的主键字段值。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

### **[delete sql]**
运行delete sql语句删除记录，使用mysql语句形式。

```text
[group:1]> delete from t_demo where name = fruit and item_id = 1
Remove OK, 1 row affected.
```
**注意：**
- 删除记录sql语句的where子句必须提供表的主键字段值。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

```eval_rst
.. important::
   执行`freezeContract`、`unfreezeContract`和`grantContractStatusManager`三个合约管理的控制台命令，需指定私钥启动控制台，用于进行操作权限判断。该私钥为部署指定合约时所用的账号私钥，即部署合约时也许指定私钥启动控制台。
```

### **freezeContract**
运行freezeContract，对指定合约进行冻结操作。参数：

- 合约地址：部署合约可以获得合约地址，其中0x前缀非必须。

```text
[group:1]> freezeContract 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **unfreezeContract**
运行unfreezeContract，对指定合约进行解冻操作。参数：

- 合约地址：部署合约可以获得合约地址，其中0x前缀非必须。

```text
[group:1]> unfreezeContract 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **grantContractStatusManager**
运行grantContractStatusManager，用于已有权限账号给其他账号授予指定合约的合约管理权限。参数：

- 合约地址：部署合约可以获得合约地址，其中0x前缀非必须。
- 账号地址：tx.origin，其中0x前缀非必须。

```text
[group:1]> grantContractStatusManager 0x30d2a17b6819f0d77f26dd3a9711ae75c291f7f1 0x965ebffc38b309fa706b809017f360d4f6de909a
{
    "code":0,
    "msg":"success"
}
```

### **getContractStatus**
运行getContractStatus，查询指定合约的状态。参数：

- 合约地址：部署合约可以获得合约地址，其中0x前缀非必须。

```text
[group:1]> getContractStatus 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
The contract is available.
```

### **listContractStatusManager**
运行listContractStatusManager，查询能管理指定合约的权限账号列表。参数：

- 合约地址：部署合约可以获得合约地址，其中0x前缀非必须。

```text
[group:1]> listContractStatusManager 0x30d2a17b6819f0d77f26dd3a9711ae75c291f7f1
[
    "0x0cc9b73b960323816ac5f52806257184c08b5ac0",
    "0x965ebffc38b309fa706b809017f360d4f6de909a"
]
```

### grantCommitteeMember

添加治理委员会委员，如果当前没有委员，则直接添加成功，否则判断投票账号是否有权限投票，如有则记录投票并检查投票是否生效。委员账号可以添加运维、管理链系统配置、节点增删等，详情[参考这里](../permission_control.md)。参数：

- 账号地址：投票添加该账号为委员

```bash
[group:1]> grantCommitteeMember 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a
{
    "code":0,
    "msg":"success"
}
```

### revokeCommitteeMember

撤销治理委员会委员判断投票账号是否有权限投票，如有则记录投票并检查投票是否生效。参数：

- 账号地址：投票撤销该账号的委员权限

```bash
[group:1]> revokeCommitteeMember 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a
{
    "code":0,
    "msg":"success"
}
```

### listCommitteeMembers

查询所有治理委员会委员。

```bash
[group:1]> listCommitteeMembers
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a  |                      1                      |
| 0x85961172229aec21694d742a5bd577bedffcfec3  |                      2                      |
---------------------------------------------------------------------------------------------
```

### updateThreshold

投票更新生效阈值，判断投票账号是否是委员，是则计票并判断是否生效。参数：

- 生效阈值：取值范围[0,99]

```bash
[group:1]> updateThreshold 75
{
    "code":0,
    "msg":"success"
}
```

### queryThreshold

查询生效阈值。

```bash
[group:1]> queryThreshold
Effective threshold : 50%
```

### queryCommitteeMemberWeight

查询治理委员会委员的票数。

```bash
[group:1]> queryCommitteeMemberWeight 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a
Account: 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a Weight: 1
```

### updateCommitteeMemberWeight

投票更新委员账号的票数，检查投票账号是否有权限，有则计票并检查是否生效，生效后该委员账号投票操作相当于设置的票数。参数：

- 委员账号：被投票修改票数的委员账号
- 投票权重：希望修改的权重

```bash
[group:1]> updateCommitteeMemberWeight 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a 2
{
    "code":0,
    "msg":"success"
}
```


### grantOperator

添加运维账号，运维角色拥有部署合约、创建用户表和管理CNS的权限，治理委员会委员可以添加运维，如果当前没有委员，则不限制。参数：

- 账号地址：添加该账号为运维

```bash
[group:1]> grantOperator 0x283f5b859e34f7fd2cf136c07579dcc72423b1b2
{
    "code":0,
    "msg":"success"
}
```

### revokeOperator

撤销账号的运维权限，委员可以操作，如果当前没有委员，则不限制。参数：

- 账号地址：撤销该账号的运维权限

```bash
[group:1]> revokeOperator 0x283f5b859e34f7fd2cf136c07579dcc72423b1b2
{
    "code":0,
    "msg":"success"
}
```

### listOperators

查询有运维权限的账号。

```bash
[group:1]> listOperators
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x283f5b859e34f7fd2cf136c07579dcc72423b1b2  |                      1                      |
---------------------------------------------------------------------------------------------
```

### **freezeAccount**
运行freezeAccount，对指定账号进行冻结操作。对没有发送过交易的账号，冻结操作将提示该账号地址不存在。参数：

- 账号地址：tx.origin，其中0x前缀必须。

```text
[group:1]> freezeAccount 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **unfreezeAccount**
运行unfreezeAccount，对指定账号进行解冻操作。对没有发送过交易的账号，解冻操作将提示该账号地址不存在。参数：

- 账号地址：tx.origin，其中0x前缀必须。

```text
[group:1]> unfreezeAccount 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **getAccountStatus**
运行getAccountStatus，查询指定账号的状态。对没有发送过交易的账号，查询操作将提示该账号地址不存在。参数：

- 账号地址：tx.origin，其中0x前缀必须。

```text
[group:1]> getAccountStatus 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
The account is available.
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
**注意：CentOS下OpenJDK无法正常工作，需要安装OracleJDK**[下载链接](https://www.oracle.com/technetwork/java/javase/downloads/index.html)。
```bash
# 创建新的文件夹，安装Java 8或以上的版本，将下载的jdk放在software目录
# 从Oracle官网(https://www.oracle.com/technetwork/java/javase/downloads/index.html)选择Java 8或以上的版本下载，例如下载jdk-8u201-linux-x64.tar.gz
$ mkdir /software
# 解压jdk
$ tar -zxvf jdk-8u201-linux-x64.tar.gz
# 配置Java环境，编辑/etc/profile文件
$ vim /etc/profile
# 打开以后将下面三句输入到文件里面并退出
export JAVA_HOME=/software/jdk-8u201  #这是一个文件目录，非文件
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
# 生效profile
$ source /etc/profile
# 查询Java版本，出现的版本是自己下载的版本，则安装成功。
java -version
```
