# 开发第一个Solidity区块链应用

标签：``开发第一个应用`` ``Solidity`` ``合约开发`` ``区块链应用`` ``EVM``

---

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

本章将会介绍一个基于FISCO BCOS区块链的业务应用场景开发全过程，从业务场景分析，到合约的设计实现，然后介绍合约编译以及如何部署到区块链，最后介绍一个应用模块的实现，通过我们提供的[Java SDK](../develop/sdk/java_sdk/index.md)实现对区块链上合约的调用访问。

本教程要求用户熟悉Linux操作环境，具备Java开发的基本技能，能够使用Gradle工具，熟悉[Solidity语法](https://solidity.readthedocs.io/en/latest/)。

如果您还未搭建区块链网络，或未下载控制台，请先走完教程[搭建第一个区块链网络](./air_installation.md)，再回到本教程。

## 1. 了解应用需求

区块链天然具有防篡改，可追溯等特性，这些特性决定其更容易受金融领域的青睐。本示例中，将会提供一个简易的资产管理的开发示例，并最终实现以下功能：

- 能够在区块链上进行资产注册
- 能够实现不同账户的转账
- 可以查询账户的资产金额

## 2. 设计与开发智能合约

在区块链上进行应用开发时，结合业务需求，首先需要设计对应的智能合约，确定合约需要储存的数据，在此基础上确定智能合约对外提供的接口，最后给出各个接口的具体实现。

### 第一步. 设计智能合约

#### 存储设计

FISCO BCOS提供[合约KV存储接口](../develop/precompiled/use_kv_precompiled.md)开发模式，可以通过合约创建表，并对创建的表进行增删改查操作。针对本应用需要设计一个存储资产管理的表`t_asset`，该表字段如下：

- account: 主键，资产账户(string类型)
- asset_value: 资产金额(uint256类型)

其中account是主键，即操作`t_asset`表时需要传入的字段，区块链根据该主键字段查询表中匹配的记录。`t_asset`表示例如下：

| account | asset_value |
|---------|-------------|
| Alice   | 10000       |
| Bob     | 20000       |

#### 接口设计

 按照业务的设计目标，需要实现资产注册，转账，查询功能，对应功能的接口如下：

```js
// 查询资产金额
function select(string memory account) public view returns(int256, uint256);
// 资产注册
function register(string memory account, uint256 asset_value) public returns(int256);
// 资产转移
function transfer(string memory from_account, string memory to_account, uint256 amount) public returns(int256);
```

### 第二步. 开发源码

根据我们第一步的存储和接口设计，创建一个Asset的智能合约，实现注册、转账、查询功能，并引入一个叫KVTable的系统合约，这个合约提供了KV存储接口。

```shell
# 创建工作目录~/fisco
mkdir -p ~/fisco

# 下载控制台
cd ~/fisco && curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.2.0/download_console.sh && bash download_console.sh

# 切换到fisco/console/目录
cd ~/fisco/console/

# 进入console/contracts目录
cd ~/fisco/console/contracts/solidity
# 创建Asset.sol合约文件
vi Asset.sol

# 将Asset.sol合约内容写入。
# 并键入wq保存退出。
```

Asset.sol的内容如下：

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

import "./Table.sol";

contract Asset {
    // event
    event RegisterEvent(
        int256 ret,
        string indexed account,
        uint256 indexed asset_value
    );
    event TransferEvent(
        int256 ret,
        string indexed from_account,
        string indexed to_account,
        uint256 indexed amount
    );

    KVTable kvTable;
    TableManager tm;
    string constant tableName = "t_asset";

    constructor() public {
        // 构造函数中创建t_asset表
        tm = TableManager(address(0x1002));

        // 资产管理表, key : account, field : asset_value
        // |  资产账户(主键)      |     资产金额       |
        // |-------------------- |-------------------|
        // |        account      |    asset_value    |
        // |---------------------|-------------------|
        //

        // create table
        tm.createKVTable(tableName, "account", "asset_value");

        // get table address
        address t_address = tm.openTable(tableName);
        kvTable = KVTable(t_address);
    }

    /*
    描述 : 根据资产账户查询资产金额
    参数 ：
            account : 资产账户

    返回值：
            参数一： 成功返回0, 账户不存在返回-1
            参数二： 第一个参数为0时有效，资产金额
    */
    function select(string memory account) public view returns (bool, uint256) {
        // 查询
        bool result;
        string memory value;
        (result, value) = kvTable.get(account);
        uint256 asset_value = 0;

        asset_value = safeParseInt(value);
        return (result, asset_value);
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
    function register(string memory account, uint256 asset_value)
    public
    returns (int256)
    {
        int256 ret_code = 0;
        bool ret = true;
        uint256 temp_asset_value = 0;
        // 查询账号是否存在
        (ret, temp_asset_value) = select(account);
        if (ret != true) {
            // 不存在，创建
            string memory asset_value_str = uint2str(asset_value);

            // 插入
            int32 count = kvTable.set(account, asset_value_str);
            if (count == 1) {
                // 成功
                ret_code = 0;
            } else {
                // 失败? 无权限或者其他错误
                ret_code = - 2;
            }
        } else {
            // 账户已存在
            ret_code = - 1;
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
    function transfer(
        string memory from_account,
        string memory to_account,
        uint256 amount
    ) public returns (int16) {
        // 查询转移资产账户信息
        bool ret = true;
        uint256 from_asset_value = 0;
        uint256 to_asset_value = 0;

        // 转移账户是否存在?
        (ret, from_asset_value) = select(from_account);
        if (ret != true) {
            // 转移账户不存在
            emit TransferEvent(- 1, from_account, to_account, amount);
            return - 1;
        }

        // 接受账户是否存在?
        (ret, to_asset_value) = select(to_account);
        if (ret != true) {
            // 接收资产的账户不存在
            emit TransferEvent(- 2, from_account, to_account, amount);
            return - 2;
        }

        if (from_asset_value < amount) {
            // 转移资产的账户金额不足
            emit TransferEvent(- 3, from_account, to_account, amount);
            return - 3;
        }

        if (to_asset_value + amount < to_asset_value) {
            // 接收账户金额溢出
            emit TransferEvent(- 4, from_account, to_account, amount);
            return - 4;
        }

        string memory f_new_value_str = uint2str(from_asset_value - amount);

        // 更新转账账户
        int32 count = kvTable.set(from_account, f_new_value_str);
        if (count != 1) {
            // 失败? 无权限或者其他错误?
            emit TransferEvent(- 5, from_account, to_account, amount);
            return - 5;
        }

        string memory to_new_value_str = uint2str(to_asset_value + amount);

        // 更新接收账户
        kvTable.set(to_account, to_new_value_str);

        emit TransferEvent(0, from_account, to_account, amount);

        return 0;
    }

    function uint2str(uint256 _i)
    internal
    pure
    returns (string memory _uintAsString)
    {
        if (_i == 0) {
            return "0";
        }
        uint j = _i;
        uint len;
        while (j != 0) {
            len++;
            j /= 10;
        }
        bytes memory bstr = new bytes(len);
        uint k = len;
        while (_i != 0) {
            k = k-1;
            uint8 temp = (48 + uint8(_i - _i / 10 * 10));
            bytes1 b1 = bytes1(temp);
            bstr[k] = b1;
            _i /= 10;
        }
        return string(bstr);
    }

    function safeParseInt(string memory _a)
    internal
    pure
    returns (uint256 _parsedInt)
    {
        return safeParseInt(_a, 0);
    }

    function safeParseInt(string memory _a, uint256 _b)
    internal
    pure
    returns (uint256 _parsedInt)
    {
        bytes memory bresult = bytes(_a);
        uint256 mint = 0;
        bool decimals = false;
        for (uint256 i = 0; i < bresult.length; i++) {
            if (
                (uint256(uint8(bresult[i])) >= 48) &&
                (uint256(uint8(bresult[i])) <= 57)
            ) {
                if (decimals) {
                    if (_b == 0) break;
                    else _b--;
                }
                mint *= 10;
                mint += uint256(uint8(bresult[i])) - 48;
            } else if (uint256(uint8(bresult[i])) == 46) {
                require(
                    !decimals,
                    "More than one decimal encountered in string!"
                );
                decimals = true;
            } else {
                revert("Non-numeral character encountered in string!");
            }
        }
        if (_b > 0) {
            mint *= 10 ** _b;
        }
        return mint;
    }
}
```

Asset.sol所引用的Table.sol已在``~/fisco/console/contracts/solidity``目录下。该系统合约文件中的接口由FISCO BCOS底层实现。当业务合约需要操作KV存储接口时，均需要引入该接口合约文件。Table.sol 合约详细接口参考[这里](../develop/precompiled/precompiled_contract_api.md)。

运行``ls``命令，确保``Asset.sol``和``Table.sol``在目录``~/fisco/console/contracts/solidity``下。

## 3. 编译智能合约

``.sol``的智能合约需要编译成ABI和BIN文件才能部署至区块链网络上。有了这两个文件即可凭借Java SDK进行合约部署和调用。但这种调用方式相对繁琐，需要用户根据合约ABI来传参和解析结果。为此，控制台提供的编译工具不仅可以编译出ABI和BIN文件，还可以自动生成一个与编译的智能合约同名的合约Java类。这个Java类是根据ABI生成的，帮助用户解析好了参数，提供同名的方法。当应用需要部署和调用合约时，可以调用该合约类的对应方法，传入指定参数即可。使用这个合约Java类来开发应用，可以极大简化用户的代码。

```shell
# 假设你已经完成控制台的下载操作，若还没有请查看本文第二节的开发源码步骤
# 切换到fisco/console/目录
cd ~/fisco/console/

# 可通过bash contract2java.sh -h命令查看该脚本使用方法
bash contract2java.sh solidity -p org.fisco.bcos.asset.contract
```

运行成功之后，将会在`console/contracts/sdk`目录生成java、abi和bin目录，如下所示。

```shell
# 其它无关文件省略
|-- abi # 生成的abi目录，存放solidity合约编译生成的abi文件
|   |-- Asset.abi
|   |-- Table.abi
|-- bin # 生成的bin目录，存放solidity合约编译生成的bin文件
|   |-- Asset.bin
|   |-- Table.bin
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|        |--fisco
|             |--bcos
|                  |--asset
|                       |--contract
|                             |--Asset.java  # Asset.sol合约生成的Java文件
|                             |--Table.java  # Table.sol合约生成的Java文件
```

java目录下生成了`org/fisco/bcos/asset/contract/`包路径目录，该目录下包含`Asset.java`和`KVTable.java`两个文件，其中`Asset.java`是Java应用调用`Asset.sol`合约需要的文件。

`Asset.java`的主要接口：

```java
package org.fisco.bcos.asset.contract;

public class Asset extends Contract {
    // Asset.sol合约 transfer接口生成
    public TransactionReceipt transfer(String from_account, String to_account, BigInteger amount);
    // Asset.sol合约 register接口生成
    public TransactionReceipt register(String account, BigInteger asset_value);
    // Asset.sol合约 select接口生成
    public Tuple2<BigInteger, BigInteger> select(String account) throws ContractException;

    // 加载Asset合约地址，生成Asset对象
    public static Asset load(String contractAddress, Client client, CryptoKeyPair credential);

    // 部署Asset.sol合约，生成Asset对象
    public static Asset deploy(Client client, CryptoKeyPair credential) throws ContractException;
}
```

其中load与deploy函数用于构造Asset对象，其他接口分别用来调用对应的solidity合约的接口。

## 4. 创建区块链应用项目

### 第一步. 安装环境

首先，我们需要安装JDK以及集成开发环境

- Java：推荐JDK 11 （JDK1.8 至JDK 14都支持）
  首先，在官网上下载JDK并安装，并自行修改JAVA_HOME环境变量

- IDE：IntelliJ IDE

  进入[IntelliJ IDE官网](https://www.jetbrains.com/idea/download/)，下载并安装社区版IntelliJ IDE

### 第二步. 创建一个Java工程

在IntelliJ IDE中创建一个gradle项目，勾选Gradle和Java，并输入工程名``asset-app-3.0``。

注意：（此步骤为非必须步骤）该项目的源码可以用以下方法获得并参考。

```bash
$ cd ~/fisco

$ curl -o asset-app-3.0-solidity.zip -#LO https://github.com/FISCO-BCOS/asset-app-demo/archive/refs/heads/main.zip
# 解压得到Java工程项目asset-app-3.0
$ unzip asset-app-3.0-solidity.zip && mv asset-app-demo-main asset-app-3.0
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试将`185.199.108.133 raw.githubusercontent.com`追加到`/etc/hosts`中，或者请尝试 `curl -o asset-app-3.0-solidity.zip -#LO https://gitee.com/FISCO-BCOS/asset-app-demo/repository/archive/main.zip`
```

### 第三步. 引入FISCO BCOS Java SDK

在build.gradle文件中的``dependencies``下加入对FISCO BCOS Java SDK的引用。

```groovy
repositories {
    mavenCentral()
    maven {
        allowInsecureProtocol = true
        url "http://maven.aliyun.com/nexus/content/groups/public/"
    }
    maven {
        allowInsecureProtocol = true
        url "https://oss.sonatype.org/content/repositories/snapshots" 
    }
}
```

### 第四步. 配置SDK证书

修改``build.gradle``文件，引入Spring框架。

```groovy
def spring_version = "4.3.27.RELEASE"
List spring = [
        "org.springframework:spring-core:$spring_version",
        "org.springframework:spring-beans:$spring_version",
        "org.springframework:spring-context:$spring_version",
        "org.springframework:spring-tx:$spring_version",
]

dependencies {
    compile logger
    runtime logger
    compile ("org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:3.2.0")
    compile spring
}
```

在``asset-app-3.0/src/test/resources``目录下创建配置文件``applicationContext.xml``，写入配置内容。

applicationContext.xml的内容如下：

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">
  <bean id="defaultConfigProperty" class="org.fisco.bcos.sdk.v3.config.model.ConfigProperty">
    <property name="cryptoMaterial">
      <map>
        <entry key="certPath" value="conf" />
        <entry key="useSMCrypto" value="false"/>
        <!-- SSL certificate configuration -->
        <!-- entry key="caCert" value="conf/ca.crt" /-->
        <!-- entry key="sslCert" value="conf/sdk.crt" /-->
        <!-- entry key="sslKey" value="conf/sdk.key" /-->
        <!-- GM SSL certificate configuration -->
        <!-- entry key="caCert" value="conf/gm/gmca.crt" /-->
        <!-- entry key="sslCert" value="conf/gm/gmsdk.crt" /-->
        <!-- entry key="sslKey" value="conf/gm/gmsdk.key" /-->
        <!--entry key="enSslCert" value="conf/gm/gmensdk.crt" /-->
        <!--entry key="enSslKey" value="conf/gm/gmensdk.key" /-->
      </map>
    </property>
    <property name="network">
      <map>
        <entry key="peers">
          <list>
            <value>127.0.0.1:20200</value>
            <value>127.0.0.1:20201</value>
          </list>
        </entry>
        <entry key="defaultGroup" value="group0" />
      </map>
    </property>
    <!--
    <property name="amop">
      <list>
        <bean id="amopTopic1" class="org.fisco.bcos.sdk.v3.config.model.AmopTopic">
          <property name="topicName" value="PrivateTopic1" />
          <property name="password" value="" />
          <property name="privateKey" value="" />
          <property name="publicKeys">
            <list>
              <value>conf/amop/consumer_public_key_1.pem</value>
            </list>
          </property>
        </bean>
      </list>
    </property>
    -->
    <property name="account">
      <map>
        <entry key="keyStoreDir" value="account" />
        <entry key="accountAddress" value="" />
        <entry key="accountFileFormat" value="pem" />
        <entry key="password" value="" />
        <entry key="accountFilePath" value="" />
      </map>
    </property>
    <property name="threadPool">
      <map>
      <entry key="threadPoolSize" value="16" />
      </map>
    </property>
  </bean>

  <bean id="defaultConfigOption" class="org.fisco.bcos.sdk.v3.config.ConfigOption">
    <constructor-arg name="configProperty">
      <ref bean="defaultConfigProperty"/>
    </constructor-arg>
  </bean>

  <bean id="bcosSDK" class="org.fisco.bcos.sdk.v3.BcosSDK">
    <constructor-arg name="configOption">
      <ref bean="defaultConfigOption"/>
    </constructor-arg>
  </bean>
</beans>
```

**注意：** 如果搭链时设置的 rpc listen_ip 为127.0.0.1或者0.0.0.0，listen_port 为20200，则`applicationContext.xml`配置不用修改。若区块链节点配置有改动，需要同样修改配置`applicationContext.xml`的`network`属性下的`peers`配置选项，配置所连接节点的 `[rpc]`配置的`listen_ip:listen_port`。

在以上配置文件中，我们指定了证书存放的位``certPath``的值为``conf``。接下来我们需要把SDK用于连接节点的证书放到指定的``conf``目录下。

```shell
# 假设我们将asset-app-3.0放在~/fisco目录下 进入~/fisco目录
$ cd ~/fisco
# 创建放置证书的文件夹(默认解压项目即存在)
$ mkdir -p asset-app-3.0/src/test/resources
# 拷贝节点证书到项目的资源目录
$ cp -r nodes/127.0.0.1/sdk/* asset-app-3.0/src/test/resources/conf
# 若在IDE直接运行，拷贝证书到resources路径
$ mkdir -p asset-app-3.0/src/main/resources
$ cp -r nodes/127.0.0.1/sdk/* asset-app-3.0/src/main/resources/conf
```

## 5. 业务逻辑开发

我们已经介绍了如何在自己的项目中引入以及配置Java SDK，本节介绍如何通过Java程序调用合约，同样以示例的资产管理说明。

### 第一步.将3编译好的Java合约引入项目中

```shell
cd ~/fisco  
# 将编译好的合约Java类引入项目中。
cp console/contracts/sdk/java/org/fisco/bcos/asset/contract/Asset.java asset-app-3.0/src/main/java/org/fisco/bcos/asset/contract/Asset.java
```

### 第二步.开发业务逻辑

在路径`/src/main/java/org/fisco/bcos/asset/client`目录下，创建`AssetClient.java`类，通过调用`Asset.java`实现对合约的部署与调用

`AssetClient.java` 代码如下：

```java
package org.fisco.bcos.asset.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.fisco.bcos.asset.contract.Asset;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class AssetClient {

    static Logger logger = LoggerFactory.getLogger(AssetClient.class);

    private BcosSDK bcosSDK;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    public void initialize() {
        @SuppressWarnings("resource")
        ApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        bcosSDK = context.getBean(BcosSDK.class);
        client = bcosSDK.getClient();
        cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        logger.debug("create client for group, account address is {}", cryptoKeyPair.getAddress());
    }

    public void deployAssetAndRecordAddr() {

        try {
            Asset asset = Asset.deploy(client, cryptoKeyPair);
            System.out.println(
                    " deploy Asset success, contract address is " + asset.getContractAddress());
            recordAssetAddr(asset.getContractAddress());
        } catch (Exception e) {
            System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
        }
    }

    public void recordAssetAddr(String address) throws IOException {
        Properties prop = new Properties();
        prop.setProperty("address", address);
        final Resource contractResource = new ClassPathResource("contract.properties");
        try (FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile())) {
            prop.store(fileOutputStream, "contract address");
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public String loadAssetAddr() throws Exception {
        // load Asset contact address from contract.properties
        Properties prop = new Properties();
        final Resource contractResource = new ClassPathResource("contract.properties");
        prop.load(contractResource.getInputStream());

        String contractAddress = prop.getProperty("address");
        if (contractAddress == null || contractAddress.trim().equals("")) {
            throw new Exception(" load Asset contract address failed, please deploy it first. ");
        }
        logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    public void queryAssetAmount(String assetAccount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            Tuple2<Boolean, BigInteger> result = asset.select(assetAccount);
            if (result.getValue1()) {
                System.out.printf(" asset account %s, value %s %n", assetAccount, result.getValue2());
            } else {
                System.out.printf(" %s asset account is not exist %n", assetAccount);
            }
        } catch (Exception e) {
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query asset account failed, error message is %s%n", e.getMessage());
        }
    }

    public void registerAssetAccount(String assetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();

            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.register(assetAccount, amount);
            List<Asset.RegisterEventEventResponse> registerEventEvents = asset.getRegisterEventEvents(receipt);
            if (!registerEventEvents.isEmpty()) {
                if (registerEventEvents.get(0).ret.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " register asset account success => asset: %s, value: %s %n", assetAccount, amount);
                } else {
                    System.out.printf(
                            " register asset account failed, ret code is %s %n", registerEventEvents.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, receipt status is: " + receipt.getStatus());
            }
        } catch (Exception e) {
            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s%n", e.getMessage());
        }
    }

    public void transferAsset(String fromAssetAccount, String toAssetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
            List<Asset.TransferEventEventResponse> transferEventEvents = asset.getTransferEventEvents(receipt);
            if (!transferEventEvents.isEmpty()) {
                if (transferEventEvents.get(0).ret.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " transfer success => from_asset: %s, to_asset: %s, amount: %s %n",
                            fromAssetAccount, toAssetAccount, amount);
                } else {
                    System.out.printf(
                            " transfer asset account failed, ret code is %s %n", transferEventEvents.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, status is: " + receipt.getStatus());
            }
        } catch (Exception e) {

            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s%n", e.getMessage());
        }
    }

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient deploy");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient query account");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient register account value");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient transfer from_account to_account amount");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            Usage();
        }

        AssetClient client = new AssetClient();
        client.initialize();

        switch (args[0]) {
            case "deploy":
                client.deployAssetAndRecordAddr();
                break;
            case "query":
                if (args.length < 2) {
                    Usage();
                }
                client.queryAssetAmount(args[1]);
                break;
            case "register":
                if (args.length < 3) {
                    Usage();
                }
                client.registerAssetAccount(args[1], new BigInteger(args[2]));
                break;
            case "transfer":
                if (args.length < 4) {
                    Usage();
                }
                client.transferAsset(args[1], args[2], new BigInteger(args[3]));
                break;
            default: {
                Usage();
            }
        }
        System.exit(0);
    }
}
```

让我们通过AssetClient这个例子，来了解FISCO BCOS Java SDK的调用：

- 初始化

初始化代码的主要功能为构造Client与CryptoKeyPair对象，这两个对象在创建对应的合约类对象(调用合约类的deploy或者load函数)时需要使用。

```java
// 函数initialize中进行初始化 
// 初始化BcosSDK
@SuppressWarnings("resource")
ApplicationContext context =
    new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
bcosSDK = context.getBean(BcosSDK.class);
client = bcosSDK.getClient();
cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
logger.debug("create client for group, account address is " + cryptoKeyPair.getAddress());
```

- 构造合约类对象

可以使用deploy或者load函数初始化合约对象，两者使用场景不同，前者适用于初次部署合约，后者在合约已经部署并且已知合约地址时使用。

```java
// 部署合约
Asset asset = Asset.deploy(client, cryptoKeyPair);
// 加载合约地址
Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
```

- 接口调用

使用合约对象调用对应的接口，处理返回结果。

```java
// select接口调用
 Tuple2<BigInteger, BigInteger> result = asset.select(assetAccount);
// register接口调用
TransactionReceipt receipt = asset.register(assetAccount, amount);
// transfer接口
TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
```

在``asset-app-3.0/tool``目录下添加一个调用AssetClient的脚本``asset_run.sh``。

```shell
#!/bin/bash 

function usage() 
{
    echo " Usage : "
    echo "   bash asset_run.sh deploy"
    echo "   bash asset_run.sh query    asset_account "
    echo "   bash asset_run.sh register asset_account asset_amount "
    echo "   bash asset_run.sh transfer from_asset_account to_asset_account amount "
    echo " "
    echo " "
    echo "examples : "
    echo "   bash asset_run.sh deploy "
    echo "   bash asset_run.sh register  Asset0  10000000 "
    echo "   bash asset_run.sh register  Asset1  10000000 "
    echo "   bash asset_run.sh transfer  Asset0  Asset1 11111 "
    echo "   bash asset_run.sh query Asset0"
    echo "   bash asset_run.sh query Asset1"
    exit 0
}

    case $1 in
    deploy)
            [ $# -lt 1 ] && { usage; }
            ;;
    register)
            [ $# -lt 3 ] && { usage; }
            ;;
    transfer)
            [ $# -lt 4 ] && { usage; }
            ;;
    query)
            [ $# -lt 2 ] && { usage; }
            ;;
    *)
        usage
            ;;
    esac

    java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:conf/:lib/*' org.fisco.bcos.asset.client.AssetClient $@
```

接着，配置好log。在``asset-app-3.0/src/test/resources``目录下创建``log4j.properties``

```properties
### set log levels ###
log4j.rootLogger=DEBUG, file

### output the log information to the file ###
log4j.appender.file=org.apache.log4j.DailyRollingFileAppender
log4j.appender.file.DatePattern='_'yyyyMMddHH'.log'
log4j.appender.file.File=./log/sdk.log
log4j.appender.file.Append=true
log4j.appender.file.filter.traceFilter=org.apache.log4j.varia.LevelRangeFilter
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p] [%-d{yyyy-MM-dd HH:mm:ss}] %C{1}.%M(%L) | %m%n

###output the log information to the console ###
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[%p] [%-d{yyyy-MM-dd HH:mm:ss}] %C{1}.%M(%L) | %m%n
```

接着，通过配置gradle中的Jar命令，指定复制和编译任务。并引入日志库，在``asset-app-3.0/src/test/resources``目录下，创建一个空的``contract.properties``文件，用于应用在运行时存放合约地址。

至此，我们已经完成了这个应用的开发。最后，我们得到的asset-app-3.0的目录结构如下：

```shell
|-- build.gradle // gradle配置文件
|-- gradle
|   |-- wrapper
|       |-- gradle-wrapper.jar // 用于下载Gradle的相关代码实现
|       |-- gradle-wrapper.properties // wrapper所使用的配置信息，比如gradle的版本等信息
|-- gradlew // Linux或者Unix下用于执行wrapper命令的Shell脚本
|-- gradlew.bat // Windows下用于执行wrapper命令的批处理脚本
├── LICENSE
├── README.md
|-- src
|   |-- main
|   |   |-- java
|   |   |     |-- org
|   |   |          |-- fisco
|   |   |                |-- bcos
|   |   |                      |-- asset
|   |   |                            |-- client // 放置客户端调用类
|   |   |                                   |-- AssetClient.java
|   |   |                            |-- contract // 放置Java合约类
|   |   |                                   |-- Asset.java
|   |   |-- resources
|   |        |-- conf
|   |               |-- ca.crt
|   |               |-- cert.cnf
|   |               |-- sdk.crt
|   |               |-- sdk.key
|   |        |-- applicationContext.xml // 项目配置文件
|   |        |-- contract.properties // 存储部署合约地址的文件
|   |        |-- log4j.properties // 日志配置文件
|   |        |-- contract //存放solidity约文件
|   |                |-- Asset.sol
|   |                |-- Table.sol
|   |-- test
|   |    |-- resources // 存放代码资源文件
|   |       |-- conf
|   |               |-- ca.crt
|   |               |-- cert.cnf
|   |               |-- sdk.crt
|   |               |-- sdk.key
|   |       |-- applicationContext.xml // 项目配置文件
|   |       |-- contract.properties // 存储部署合约地址的文件
|   |       |-- log4j.properties // 日志配置文件
|   |       |-- contract //存放solidity约文件
|   |               |-- Asset.sol
|   |               |-- Table.sol
|
|-- tool
    |-- asset_run.sh // 项目运行脚本
```

## 6. 运行应用

至此我们已经介绍使用区块链开发资产管理应用的所有流程并实现了功能，接下来可以运行项目，测试功能是否正常。

- 编译

```shell
# 切换到项目目录
$ cd ~/fisco/asset-app-3.0
# 编译项目
$ ./gradlew build
```

编译成功之后，将在项目根目录下生成`dist`目录。dist目录下有一个`asset_run.sh`脚本，简化项目运行。现在开始一一验证本文开始定下的需求。

- 部署`Asset.sol`合约

```shell
# 进入dist目录
$ cd dist
$ bash asset_run.sh deploy
Deploy Asset successfully, contract address is 0xd09ad04220e40bb8666e885730c8c460091a4775
```

- 注册资产

```shell
$ bash asset_run.sh register Alice 100000
Register account successfully => account: Alice, value: 100000
$ bash asset_run.sh register Bob 100000
Register account successfully => account: Bob, value: 100000
```

- 查询资产

```shell
$ bash asset_run.sh query Alice
asset account Alice, value 100000
$ bash asset_run.sh query Bob
asset account Bob, value 100000
```

- 资产转移

```shell
$ bash asset_run.sh transfer Alice Bob  50000
 transfer success => from_asset: Alice, to_asset: Bob, amount: 50000
$ bash asset_run.sh query Alice
account Alice, value 50000
$ bash asset_run.sh query Bob
asset account Bob, value 150000
```

**总结：** 至此，我们通过合约开发，合约编译，SDK配置与业务开发构建了一个基于FISCO BCOS联盟区块链的solidity应用。
