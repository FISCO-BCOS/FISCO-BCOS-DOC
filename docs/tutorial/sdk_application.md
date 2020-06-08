# 构建第一个区块链应用

本章将会介绍一个基于FISCO BCOS区块链的业务应用场景开发全过程，从业务场景分析，到合约的设计实现，然后介绍合约编译以及如何部署到区块链，最后介绍一个应用模块的实现，通过我们提供的Web3SDK实现对区块链上合约的调用访问。

本教程要求用户熟悉Linux操作环境，具备Java开发的基本技能，能够使用Gradle工具，熟悉[Solidity语法](https://solidity.readthedocs.io/en/latest/)。

**通过学习教程，你将会了解到以下内容：**

1. 如何将一个业务场景的逻辑用合约的形式表达
2. 如何将Solidity合约转化成Java类
3. 如何配置Web3SDK
4. 如何构建一个应用，并集成Web3SDK到应用工程
5. 如何通过Web3SDK调用合约接口，了解Web3SDK调用合约接口的原理

教程中会提供示例的完整项目源码，用户可以在此基础上快速开发自己的应用。

```eval_rst
.. important::
    请参考 `安装文档 <../installation.html>`_ 完成FISCO BCOS区块链的搭建和控制台的下载工作，本教程中的操作假设在该文档搭建的环境下进行。
```

## 示例应用需求

区块链天然具有防篡改，可追溯等特性，这些特性决定其更容易受金融领域的青睐，本文将会提供一个简易的资产管理的开发示例，并最终实现以下功能：

-   能够在区块链上进行资产注册
-   能够实现不同账户的转账
-   可以查询账户的资产金额

## 合约设计与实现

在区块链上进行应用开发时，结合业务需求，首先需要设计对应的智能合约，确定合约需要储存的数据，在此基础上确定智能合约对外提供的接口，最后给出各个接口的具体实现。

### 存储设计

FISCO BCOS提供[合约CRUD接口](../manual/smart_contract.html#crud)开发模式，可以通过合约创建表，并对创建的表进行增删改查操作。针对本应用需要设计一个存储资产管理的表`t_asset`，该表字段如下：

-   account: 主键，资产账户(string类型)
-   asset_value: 资产金额(uint256类型)

其中account是主键，即操作`t_asset`表时需要传入的字段，区块链根据该主键字段查询表中匹配的记录。`t_asset`表示例如下：

| account | asset_value |
| ------- | ----------- |
| Alice   | 10000       |
| Bob     | 20000       |

### 接口设计

 按照业务的设计目标，需要实现资产注册，转账，查询功能，对应功能的接口如下：

```js
// 查询资产金额
function select(string account) public constant returns(int256, uint256)
// 资产注册
function register(string account, uint256 amount) public returns(int256)
// 资产转移
function transfer(string from_asset_account, string to_asset_account, uint256 amount) public returns(int256)
```

### 完整源码

```js
pragma solidity ^0.4.24;

import "./Table.sol";

contract Asset {
    // event
    event RegisterEvent(int256 ret, string account, uint256 asset_value);
    event TransferEvent(int256 ret, string from_account, string to_account, uint256 amount);

    constructor() public {
        // 构造函数中创建t_asset表
        createTable();
    }

    function createTable() private {
        TableFactory tf = TableFactory(0x1001);
        // 资产管理表, key : account, field : asset_value
        // |  资产账户(主键)      |     资产金额       |
        // |-------------------- |-------------------|
        // |        account      |    asset_value    |
        // |---------------------|-------------------|
        //
        // 创建表
        tf.createTable("t_asset", "account", "asset_value");
    }

    function openTable() private returns(Table) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_asset");
        return table;
    }

    /*
    描述 : 根据资产账户查询资产金额
    参数 ：
            account : 资产账户

    返回值：
            参数一： 成功返回0, 账户不存在返回-1
            参数二： 第一个参数为0时有效，资产金额
    */
    function select(string account) public constant returns(int256, uint256) {
        // 打开表
        Table table = openTable();
        // 查询
        Entries entries = table.select(account, table.newCondition());
        uint256 asset_value = 0;
        if (0 == uint256(entries.size())) {
            return (-1, asset_value);
        } else {
            Entry entry = entries.get(0);
            return (0, uint256(entry.getInt("asset_value")));
        }
    }

    /*
    描述 : 资产注册
    参数 ：
            account : 资产账户
            amount  : 资产金额
    返回值：
            0  资产注册成功
            -1 资产账户已存在
            -2 其他错误
    */
    function register(string account, uint256 asset_value) public returns(int256){
        int256 ret_code = 0;
        int256 ret= 0;
        uint256 temp_asset_value = 0;
        // 查询账户是否存在
        (ret, temp_asset_value) = select(account);
        if(ret != 0) {
            Table table = openTable();

            Entry entry = table.newEntry();
            entry.set("account", account);
            entry.set("asset_value", int256(asset_value));
            // 插入
            int count = table.insert(account, entry);
            if (count == 1) {
                // 成功
                ret_code = 0;
            } else {
                // 失败? 无权限或者其他错误
                ret_code = -2;
            }
        } else {
            // 账户已存在
            ret_code = -1;
        }

        emit RegisterEvent(ret_code, account, asset_value);

        return ret_code;
    }

    /*
    描述 : 资产转移
    参数 ：
            from_account : 转移资产账户
            to_account ： 接收资产账户
            amount ： 转移金额
    返回值：
            0  资产转移成功
            -1 转移资产账户不存在
            -2 接收资产账户不存在
            -3 金额不足
            -4 金额溢出
            -5 其他错误
    */
    function transfer(string from_account, string to_account, uint256 amount) public returns(int256) {
        // 查询转移资产账户信息
        int ret_code = 0;
        int256 ret = 0;
        uint256 from_asset_value = 0;
        uint256 to_asset_value = 0;

        // 转移账户是否存在?
        (ret, from_asset_value) = select(from_account);
        if(ret != 0) {
            ret_code = -1;
            // 转移账户不存在
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;

        }

        // 接受账户是否存在?
        (ret, to_asset_value) = select(to_account);
        if(ret != 0) {
            ret_code = -2;
            // 接收资产的账户不存在
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        if(from_asset_value < amount) {
            ret_code = -3;
            // 转移资产的账户金额不足
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        if (to_asset_value + amount < to_asset_value) {
            ret_code = -4;
            // 接收账户金额溢出
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        Table table = openTable();

        Entry entry0 = table.newEntry();
        entry0.set("account", from_account);
        entry0.set("asset_value", int256(from_asset_value - amount));
        // 更新转账账户
        int count = table.update(from_account, entry0, table.newCondition());
        if(count != 1) {
            ret_code = -5;
            // 失败? 无权限或者其他错误?
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        Entry entry1 = table.newEntry();
        entry1.set("account", to_account);
        entry1.set("asset_value", int256(to_asset_value + amount));
        // 更新接收账户
        table.update(to_account, entry1, table.newCondition());

        emit TransferEvent(ret_code, from_account, to_account, amount);

        return ret_code;
    }
}
```

 **注：** `Asset.sol`合约的实现需要引入FISCO BCOS提供的一个系统合约接口文件 `Table.sol` ，该系统合约文件中的接口由FISCO BCOS底层实现。当业务合约需要操作CRUD接口时，均需要引入该接口合约文件。`Table.sol` 合约详细接口[参考这里](../manual/smart_contract.html#crud)。

## 合约编译

上一小节，我们根据业务需求设计了合约`Asset.sol`的存储与接口，给出了完整实现，但是Java程序无法直接调用Solidity合约，需要先将Solidity合约文件编译为Java文件。

控制台提供了编译工具，可以将`Asset.sol`合约文件存放在`console/contracts/solidity`目录。利用console目录下提供的`sol2java.sh`脚本进行编译，操作如下：
```bash
# 切换到fisco/console/目录
$ cd ~/fisco/console/
# 编译合约，后面指定一个Java的包名参数，可以根据实际项目路径指定包名
$ ./sol2java.sh org.fisco.bcos.asset.contract
```

运行成功之后，将会在`console/contracts/sdk`目录生成java、abi和bin目录，如下所示。

```bash
|-- abi # 生成的abi目录，存放solidity合约编译生成的abi文件
|   |-- Asset.abi
|   |-- Table.abi
|-- bin # 生成的bin目录，存放solidity合约编译生成的bin文件
|   |-- Asset.bin
|   |-- Table.bin
|-- contracts # 存放solidity合约源码文件，将需要编译的合约拷贝到该目录下
|   |-- Asset.sol # 拷贝进来的Asset.sol合约，依赖Table.sol
|   |-- Table.sol # 实现系统CRUD操作的合约接口文件
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|        |--fisco
|             |--bcos
|                  |--asset
|                       |--contract
|                             |--Asset.java  # Asset.sol合约生成的Java文件
|                             |--Table.java  # Table.sol合约生成的Java文件
|-- sol2java.sh
```

java目录下生成了`org/fisco/bcos/asset/contract/`包路径目录，该目录下包含`Asset.java`和`Table.java`两个文件，其中`Asset.java`是Java应用调用`Asset.sol`合约需要的文件。

`Asset.java`的主要接口：

```java
package org.fisco.bcos.asset.contract;

public class Asset extends Contract {
    // Asset.sol合约 transfer接口生成
    public RemoteCall<TransactionReceipt> transfer(String from_account, String to_account, BigInteger amount);
    // Asset.sol合约 register接口生成
    public RemoteCall<TransactionReceipt> register(String account, BigInteger asset_value);
    // Asset.sol合约 select接口生成
    public RemoteCall<Tuple2<BigInteger, BigInteger>> select(String account);

    // 加载Asset合约地址，生成Asset对象
    public static Asset load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider);

    // 部署Assert.sol合约，生成Asset对象
    public static RemoteCall<Asset> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider);
}
```

其中load与deploy函数用于构造Asset对象，其他接口分别用来调用对应的solidity合约的接口，详细使用在下文会有介绍。

## SDK配置

我们提供了一个Java工程项目供开发使用，首先获取Java工程项目：

```bash
    # 获取Java工程项目压缩包
    $ cd ~
    $ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/asset-app.tar.gz
    # 解压得到Java工程项目asset-app目录
    $ tar -zxf asset-app.tar.gz
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -LO https://www.fisco.com.cn/cdn/deps/tools/asset-app.tar.gz`
```

asset-app项目的目录结构如下：

```bash
|-- build.gradle // gradle配置文件
|-- gradle
|   |-- wrapper
|       |-- gradle-wrapper.jar // 用于下载Gradle的相关代码实现
|       |-- gradle-wrapper.properties // wrapper所使用的配置信息，比如gradle的版本等信息
|-- gradlew // Linux或者Unix下用于执行wrapper命令的Shell脚本
|-- gradlew.bat // Windows下用于执行wrapper命令的批处理脚本
|-- src
|   |-- main
|   |   |-- java
|   |         |-- org
|   |             |-- fisco
|   |                   |-- bcos
|   |                         |-- asset
|   |                               |-- client // 放置客户端调用类
|   |                                      |-- AssetClient.java
|   |                               |-- contract // 放置Java合约类
|   |                                      |-- Asset.java
|   |-- test
|       |-- resources // 存放代码资源文件
|           |-- applicationContext.xml // 项目配置文件
|           |-- contract.properties // 存储部署合约地址的文件
|           |-- log4j.properties // 日志配置文件
|           |-- contract //存放solidity约文件
|                   |-- Asset.sol
|                   |-- Table.sol
|
|-- tool
    |-- asset_run.sh // 项目运行脚本
```

### 项目引入Web3SDK

**项目的`build.gradle`文件已引入Web3SDK，不需修改**。其引入方法介绍如下：

-   Web3SDK引入了以太坊的solidity编译器相关jar包，因此在`build.gradle`文件需要添加以太坊的远程仓库：

```java
repositories {
    maven {
        url "http：//maven.aliyun.com/nexus/content/groups/public/"
    }
    maven { url "https：//dl.bintray.com/ethereum/maven/" }
    mavenCentral()
}
```

-   引入Web3SDK jar包

```java
compile ('org.fisco-bcos：web3sdk：2.1.0')
```

### 证书与配置文件

-   区块链节点证书配置

拷贝区块链节点对应的SDK证书

```bash
# 进入~目录
# 拷贝节点证书到项目的资源目录
$ cd ~
$ cp fisco/nodes/127.0.0.1/sdk/* asset-app/src/test/resources/
```

-   applicationContext.xml

**注意：** 如果搭链时设置的jsonrpc_listen_ip为127.0.0.1或者0.0.0.0，channel_port为20200， 则`applicationContext.xml`配置不用修改。若区块链节点配置有改动，需要同样修改配置`applicationContext.xml`，具体请参考[SDK使用文档](../sdk/java_sdk.html#spring)。

## 业务开发

我们已经介绍了如何在自己的项目中引入以及配置Web3SDK，本节介绍如何通过Java程序调用合约，同样以示例的资产管理说明。asset-app项目已经包含示例的完整源码，用户可以直接使用，现在介绍核心类`AssetClient`的设计与实现。

`AssetClient.java`: 通过调用`Asset.java`实现对合约的部署与调用，路径`/src/main/java/org/fisco/bcos/asset/client`，初始化以及调用流程都在该类中进行。

-   初始化

初始化代码的主要功能为构造Web3j与Credentials对象，这两个对象在创建对应的合约类对象(调用合约类的deploy或者load函数)时需要使用。

```java
// 函数initialize中进行初始化
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
Service service = context.getBean(Service.class);
service.run();

ChannelEthereumService channelEthereumService = new ChannelEthereumService();
channelEthereumService.setChannelService(service);
// 初始化Web3j对象
Web3j web3j = Web3j.build(channelEthereumService, 1);
// 初始化Credentials对象
Credentials credentials = Credentials.create(Keys.createEcKeyPair());
```

-   构造合约类对象

可以使用deploy或者load函数初始化合约对象，两者使用场景不同，前者适用于初次部署合约，后者在合约已经部署并且已知合约地址时使用。

```java
// 部署合约
Asset asset = Asset.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();
// 加载合约地址
Asset asset = Asset.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
```

-   接口调用

使用合约对象调用对应的接口，处理返回结果。

```java
// select接口调用
Tuple2<BigInteger, BigInteger> result = asset.select(assetAccount).send();
// register接口调用
TransactionReceipt receipt = asset.register(assetAccount, amount).send();
// transfer接口
TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount).send();
```

## 运行

至此我们已经介绍使用区块链开发资产管理应用的所有流程并实现了功能，接下来可以运行项目，测试功能是否正常。

-   编译

```bash
# 切换到项目目录
$ cd ~/asset-app
# 编译项目
$ ./gradlew build
```

编译成功之后，将在项目根目录下生成`dist`目录。dist目录下有一个`asset_run.sh`脚本，简化项目运行。现在开始一一验证本文开始定下的需求。

-   部署`Asset.sol`合约

```bash
# 进入dist目录
$ cd dist
$ bash asset_run.sh deploy
Deploy Asset successfully, contract address is 0xd09ad04220e40bb8666e885730c8c460091a4775
```

-   注册资产

```bash
$ bash asset_run.sh register Alice 100000
Register account successfully => account: Alice, value: 100000
$ bash asset_run.sh register Bob 100000
Register account successfully => account: Bob, value: 100000
```

-   查询资产

```bash
$ bash asset_run.sh query Alice
account Alice, value 100000
$ bash asset_run.sh query Bob
account Bob, value 100000
```

-   资产转移

```bash
$ bash asset_run.sh transfer Alice Bob  50000
Transfer successfully => from_account: Alice, to_account: Bob, amount: 50000
$ bash asset_run.sh query Alice
account Alice, value 50000
$ bash asset_run.sh query Bob
account Bob, value 150000
```

**总结：** 至此，我们通过合约开发，合约编译，SDK配置与业务开发构建了一个基于FISCO BCOS联盟区块链的应用。
