# 4. 开发第一个WBC-Liquid区块链应用

标签：``开发第一个应用`` ``WBC-Liquid`` ``合约开发`` ``区块链应用`` ``WASM``

---

```eval_rst
.. warning::
    自 v3.17.0 起，WASM / WBC-Liquid 合约执行（``WITH_WASM`` 编译选项及 WBC-Liquid 合约的部署与执行支持）计划在 v3.18.0 中移除；Solidity / EVM 合约不受影响，建议迁移至 Solidity 合约开发。详见 `版本功能变更说明 <../introduction/change_log/3_17_0.html>`_ 。
```

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

本章将会介绍一个基于FISCO BCOS区块链的业务应用场景开发全过程，从业务场景分析，到合约的设计实现，然后介绍合约编译以及如何部署到区块链，最后介绍一个应用模块的实现，通过我们提供的[Java SDK](../sdk/java_sdk/index.md)实现对区块链上合约的调用访问。

本教程要求用户熟悉Linux操作环境，具备Java开发的基本技能，能够使用Gradle工具，熟悉webankblockchain-liquid语法（以下简称WBC-Liquid），并且进行了[WBC-Liquid的环境配置](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/quickstart/prerequisite.html)。

开发WBC-Liquid应用，需要搭建**WASM**配置的区块链网络。步骤如下：
1. 如果您还未搭建区块链网络，或未下载控制台，请先走完教程[搭建第一个区块链网络](./air_installation.md)，再回到本教程。（若已搭建则忽略本步骤）；
2. 开启节点wasm配置项：修改节点创世块的配置文件 `config.genesis` 的 `[executor]` 为`is_wasm=true`；
3. 删除data数据、重启节点；
```shell
# 到节点的目录
cd ~/fisco/nodes/127.0.0.1
# 停掉节点
bash stop_all.sh
# 删除节点旧数据
rm -rf node*/data
# 重启节点
bash start_all.sh
```
至此，区块链网络就开启了WASM配置。

## 1. 了解应用需求

区块链天然具有防篡改，可追溯等特性，这些特性决定其更容易受金融领域的青睐。本示例中，将会提供一个简易的资产管理的开发示例，并最终实现以下功能：

- 能够在区块链上进行资产注册
- 能够实现不同账户的转账
- 可以查询账户的资产金额

## 2. 设计与开发智能合约

在区块链上进行应用开发时，结合业务需求，首先需要设计对应的智能合约，确定合约需要储存的数据，在此基础上确定智能合约对外提供的接口，最后给出各个接口的具体实现。

### 第一步. 设计智能合约

#### 存储设计

针对本应用需要设计一个存储资产管理的表，该表字段如下：

- account: 主键，资产账户(string类型)
- asset_value: 资产金额(uint256类型)

其中account是主键，即操作表时需要传入的字段，区块链根据该主键字段查询表中匹配的记录。存储表示例如下：

| account | asset_value |
|---------|-------------|
| Alice   | 10000       |
| Bob     | 20000       |

在本示例中，使用rust自带的Mapping作为存储字段进行记录。

#### 接口设计

 按照业务的设计目标，需要实现资产注册，转账，查询功能，对应功能的接口如下：

```solidity
// 查询资产金额
pub fn select(&mut self, account: String) -> (bool, u128)
// 资产注册
pub fn register(&mut self, account: String, asset_value: u128) -> i16
// 资产转移
pub fn transfer(&mut self, from: String, to: String, value: u128) -> i16
```

### 第二步. 开发源码

#### 创建

根据我们第一步的存储和接口设计，创建一个Asset的智能合约项目。
在终端中执行以下命令创建 WBC-Liquid 智能合约项目：

**特别注意：** 为了用户方便使用，控制台已经在 `console/contracts/liquid`路径下准备好了`asset`示例，下面的流程为创建一个全新WBC-Liquid合约的流程。

```shell
# 创建工作目录~/fisco
mkdir -p ~/fisco

# 下载控制台
cd ~/fisco && curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.10.0/download_console.sh && bash download_console.sh

# 切换到fisco/console/目录
cd ~/fisco/console/

# 进入console/contracts目录
cd ~/fisco/console/contracts/liquid

# 创建新的合约 (控制台已经准备好asset目录，如果是下载控制台的话可跳过该步骤)
cargo liquid new contract asset
```

asset目录内的文件结构如下所示：

```shell
asset/
├── .gitignore
├── .liquid
│   └── abi_gen
│       ├── Cargo.toml
│       └── main.rs
├── Cargo.toml
└── src
│   └──lib.rs
```

其中各文件的功能如下：

- `.gitignore`：隐藏文件，用于告诉版本管理软件[Git](https://git-scm.com/)哪些文件或目录不需要被添加到版本管理中。WBC-Liquid 会默认将某些不重要的问题件（如编译过程中生成的临时文件）排除在版本管理之外，如果不需要使用 Git 管理对项目版本进行管理，可以忽略该文件；

- `.liquid/`：隐藏目录，用于实现 WBC-Liquid 智能合的内部功能，其中`abi_gen`子目录下包含了 ABI 生成器的实现，该目录下的编译配置及代码逻辑是固定的，如果被修改可能会造成无法正常生成 ABI；

- `Cargo.toml`：项目配置清单，主要包括项目信息、外部库依赖、编译配置等，一般而言无需修改该文件，除非有特殊的需求（如引用额外的第三方库、调整优化等级等）；

- `src/lib.rs`：WBC-Liquid 智能合约项目根文件，合约代码存放于此文件中。智能合约项目创建完毕后，`lib.rs`文件中会自动填充部分样板代码，我们可以基于这些样板代码做进一步的开发。

我们将Asset liquid中的代码复制至`lib.rs`文件中后，便可进行后续步骤。

Asset liquid的内容如下：

```rust
#![cfg_attr(not(feature = "std"), no_std)]

use liquid::storage;
use liquid_lang as liquid;
use liquid_prelude::string::ToString;

#[liquid::interface(name = auto)]
mod table_manager {

    extern "liquid" {
        fn createKVTable(
            &mut self,
            table_name: String,
            key: String,
            value_fields: String,
        ) -> i32;
    }
}
#[liquid::interface(name = auto)]
mod kv_table {

    extern "liquid" {
        fn get(&self, key: String) -> (bool, String);
        fn set(&mut self, key: String, value: String) -> i32;
    }
}

#[liquid::contract]
mod asset {
    use super::{kv_table::*, table_manager::*, *};

    #[liquid(event)]
    struct RegisterEvent {
        ret_code: i16,
        #[liquid(indexed)]
        account: String,
        #[liquid(indexed)]
        asset_value: u128,
    }

    #[liquid(event)]
    struct TransferEvent {
        ret_code: i16,
        #[liquid(indexed)]
        from: String,
        #[liquid(indexed)]
        to: String,
        value: u128,
    }

    #[liquid(storage)]
    struct Asset {
        table: storage::Value<KvTable>,
        tm: storage::Value<TableManager>,
        table_name: storage::Value<String>,
    }

    #[liquid(methods)]
    impl Asset {
        pub fn new(&mut self) {
            self.table_name.initialize(String::from("t_asset"));
            self.tm
                .initialize(TableManager::at("/sys/table_manager".parse().unwrap()));
            self.tm.createKVTable(
                self.table_name.clone(),
                String::from("account"),
                String::from("asset_value"),
            );
            self.table
                .initialize(KvTable::at("/tables/t_asset".parse().unwrap()));
        }

        pub fn select(&self, account: String) -> (bool, u128) {
            if let Some((result, value)) = (*self.table).get(account) {
                if value.len() == 0 {
                    return (false, Default::default());
                }
                return (result, u128::from_str_radix(&value, 10).ok().unwrap());
            }
            return (false, Default::default());
        }

        pub fn register(&mut self, account: String, asset_value: u128) -> i16 {
            let ret_code: i16;
            let (ok, _) = self.select(account.clone());
            if ok == false {
                let result = (*self.table)
                    .set(account.clone(), asset_value.to_string())
                    .unwrap();

                if result == 1.into() {
                    ret_code = 0;
                } else {
                    ret_code = -2;
                }
            } else {
                ret_code = -1;
            }
            let ret = ret_code.clone();
            self.env().emit(RegisterEvent {
                ret_code,
                account,
                asset_value,
            });
            return ret;
        }

        pub fn transfer(&mut self, from: String, to: String, value: u128) -> i16 {
            let mut ret_code: i16 = 0;
            let (ok, from_value) = self.select(from.clone());
            if ok != true.into() {
                ret_code = -1;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            let (ret, to_value) = self.select(to.clone());
            if ret != true {
                ret_code = -2;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            if from_value < value.clone() {
                ret_code = -3;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            if to_value.clone() + value.clone() < to_value.clone() {
                ret_code = -4;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            let from_u = self.update(from.clone(), from_value - value.clone());
            if from_u != 1 {
                ret_code = -5;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            let to_u = self.update(to.clone(), to_value.clone() + value.clone());
            if to_u != 1 {
                ret_code = -6;
            }
            self.env().emit(TransferEvent {
                ret_code,
                from,
                to,
                value,
            });
            return ret_code;
        }

        pub fn update(&mut self, account: String, value: u128) -> i16 {
            let r = (*self.table).set(account, value.to_string()).unwrap();
            if r == 1.into() {
                return 1;
            }
            return -1;
        }
    }
}
```

#### 构建

在 asset 项目根目录下执行以下命令即可开始进行构建：

```shell
# 编译国密版本的wasm二进制文件
cargo liquid build -g
```

该命令会引导 Rust 语言编译器以`wasm32-unknown-unknown`为目标对智能合约代码进行编译，最终生成 Wasm 格式字节码及 ABI。`-g` 构建出能够在国密版FISCO BCOS区块链底层平台上运行的智能合约。命令执行完成后，会显示如下形式的内容：

```shell
[1/4] 🔍  Collecting crate metadata
[2/4] 🚚  Building cargo project
[3/4] 🔗  Optimizing Wasm bytecode
[4/4] 📃  Generating ABI file

✨ Done in 30 seconds, your project is ready now:
Binary: ~/fisco/console/contracts/liquid/asset/target/asset_gm.wasm
   ABI: ~/fisco/console/contracts/liquid/asset/target/asset.abi
```

其中，“Binary:”后为生成的字节码文件的绝对路径，“ABI:”后为生成的 ABI 文件的绝对路径。为尽量简化 FISCO BCOS 各语言 SDK 的适配工作，WBC-Liquid 采用了与 Solidity ABI 规范兼容的 ABI 格式.

接着生成非国密的 Binary、ABI 文件：

```shell
cargo liquid build
```

注意：不带 `-g`。
执行命令后，生成信息和上述雷同，进入 `target` ，看到生成新的 Binary、ABI，以及刚刚的 `asset_gm.wasm`

## 3. 编译智能合约

``Liquid``的智能合约需要编译成ABI和WASM文件才能部署至区块链网络上，有了这两个文件即可凭借Java SDK进行合约部署和调用。Liquid项目的环境构建与编译请参考：[部署Liquid编译环境](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/quickstart/prerequisite.html) 、[Liquid开发指南](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/dev_testing/development.html)。

控制台提供的Java生成工具可以将`cargo liquid build`编译出ABI和WASM文件自动生成一个与编译的智能合约同名的合约Java类。这个Java类是根据ABI生成的，帮助用户解析好了参数，提供同名的方法。当应用需要部署和调用合约时，可以调用该合约类的对应方法，传入指定参数即可。使用这个合约Java类来开发应用，可以极大简化用户的代码。我们利用console控制台的脚本 `contract2java.sh` 生成Java 文件。

```shell
# 假设你已经完成控制台的下载操作，若还没有请查看本文第二节的开发源码步骤

# 切换到fisco/console/目录
cd ~/fisco/console/

# 编译合约(后面指定BINARY、abi 文件路径，可以根据实际项目路径指定路径)如下：
bash contract2java.sh liquid -a ~/fisco/console/contracts/liquid/asset/target/asset.abi -b ~/fisco/console/contracts/liquid/asset/target/asset.wasm -s ~/fisco/console/contracts/liquid/asset/target/asset_gm.wasm -p org.fisco.bcos.asset.liquid.contract

# 脚本用法：
$ bash contract2java.sh liquid -h
Missing required options: b, a, s
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -a,--abi <arg>       [Required] The ABI file path of WBC-Liquid contract.
 -b,--bin <arg>       [Required] The binary file path of WBC-Liquid contract.
 -h,--help
 -o,--output <arg>    [Optional] The file path of the generated java code,
                      default is contracts/sdk/java/
 -p,--package <arg>   [Optional] The package name of the generated java
                      code, default is com
 -s,--sm-bin <arg>   [Required] The SM binary file path of WBC-Liquid
                      contract.
```

运行成功之后，将会在`console/contracts/sdk/java/com`目录生成Asset.java文件。

`Asset.java`的主要接口：

```java
package org.fisco.bcos.asset.contract;

public class Asset extends Contract {
    // Asset合约 transfer接口生成
    public TransactionReceipt transfer(String from, String to, BigInteger value);
    // Asset合约 register接口生成
    public TransactionReceipt register(String account, BigInteger asset_value);
    // Asset合约 select接口生成
    public Tuple2<Boolean, BigInteger> select(String account) throws ContractException;

    // 加载Asset合约地址，生成Asset对象
    public static Asset load(String contractAddress, Client client, CryptoKeyPair credential);

    // 部署Asset合约，生成Asset对象
    public static Asset deploy(Client client, CryptoKeyPair credential, String contractPath) throws ContractException;
}
```

其中load与deploy函数用于构造Asset对象，其他接口分别用来调用对应的合约的接口。

## 4. 创建区块链应用项目

### 第一步. 安装环境

首先，我们需要安装JDK以及集成开发环境

- Java：JDK 11 （JDK1.8 至JDK 14都支持）

  首先，在官网上下载JDK11并安装，并自行修改JAVA_HOME环境变量

- IDE：IntelliJ IDE.

  进入[IntelliJ IDE官网](https://www.jetbrains.com/idea/download/)，下载并安装社区版IntelliJ IDE。

### 第二步. 创建一个Java工程

在IntelliJ IDE中创建一个gradle项目，勾选Gradle和Java，并输入工程名``asset-app-liquid``。

注意：（此步骤为非必须步骤）该项目的源码可以用以下方法获得并参考。

```shell
$ cd ~/fisco

$ curl -o asset-app-3.0-liquid.zip -#LO https://github.com/FISCO-BCOS/asset-app-demo/archive/refs/heads/main-liquid.zip

# 解压得到Java工程项目asset-app-liquid
$ unzip asset-app-3.0-liquid.zip && mv asset-app-demo-main-liquid  asset-app-liquid
```

```eval_rst
.. note::
- 如果因为网络问题导致长时间无法下载，请尝试将`185.199.108.133 raw.githubusercontent.com`追加到`/etc/hosts`中，或者请尝试 `curl -o asset-app-3.0-liquid.zip -#LO https://gitee.com/FISCO-BCOS/asset-app-demo/repository/archive/main-liquid.zip`
```

### 第三步. 引入FISCO BCOS Java SDK

修改``build.gradle``文件，引入Spring框架，在的``dependencies``下加入对FISCO BCOS Java SDK的引用（注意java-sdk版本号）。

```groovy
repositories {
    mavenCentral()
    maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
    maven { url "https://oss.sonatype.org/service/local/staging/deploy/maven2" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

def spring_version = "5.3.25"
List spring = [
        "org.springframework:spring-core:$spring_version",
        "org.springframework:spring-beans:$spring_version",
        "org.springframework:spring-context:$spring_version",
        "org.springframework:spring-tx:$spring_version",
]

dependencies {
    compile logger
    runtime logger
    compile ("org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:3.3.0")
    compile spring
}
```

### 第四步. 配置SDK证书
在``asset-app-liquid/src/test/resources``目录下创建配置文件``applicationContext.xml``，写入配置内容。

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

**注意：** liquid合约，节点需要开启**wasm**选项。如果搭链时设置的 rpc listen_ip 为127.0.0.1或者0.0.0.0，listen_port 为20200，则`applicationContext.xml`配置不用修改。若区块链节点配置有改动，需要同样修改配置`applicationContext.xml`的`network`属性下的`peers`配置选项，配置所连接节点的 `[rpc]`配置的`listen_ip:listen_port`。

在以上配置文件中，我们指定了证书存放的位``certPath``的值为``conf``。接下来我们需要把SDK用于连接节点的证书放到指定的``conf``目录下。

```shell
# 假设我们将asset-app-liquid放在~/fisco目录下 进入~/fisco目录
$ cd ~/fisco
# 创建放置证书的文件夹
$ mkdir -p asset-app-liquid/src/test/resources/conf
# 拷贝节点证书到项目的资源目录
$ cp -r nodes/127.0.0.1/sdk/* asset-app-liquid/src/test/resources/conf
# 若在IDE直接运行，拷贝证书到resources路径
$ mkdir -p asset-app-liquid/src/main/resources/conf
$ cp -r nodes/127.0.0.1/sdk/* asset-app-liquid/src/main/resources/conf
```

## 5. 业务逻辑开发

我们已经介绍了如何在自己的项目中引入以及配置Java SDK，本节介绍如何通过Java程序调用合约，同样以示例的资产管理说明。

### 第一步 将3编译好的Java合约引入项目中

```shell
cd ~/fisco
# 将编译好的合约Java类引入项目中。
cp console/contracts/sdk/java/org/fisco/bcos/asset/liquid/contract/Asset.java asset-app-liquid/src/main/java/org/fisco/bcos/asset/liquid/contract/Asset.java
```

### 第二步 开发业务逻辑

在路径`/src/main/java/org/fisco/bcos/asset/liquid/client`目录下，创建`AssetClient.java`类，通过调用`Asset.java`实现对合约的部署与调用

`AssetClient.java` 代码如下：

```java
package org.fisco.bcos.asset.liquid.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

import org.fisco.bcos.asset.liquid.contract.Asset;
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
        logger.debug("create client for group1, account address is {}", cryptoKeyPair.getAddress());
    }

    public void deployAssetAndRecordAddr() {

        try {
            String assetPath = "/asset" + new Random().nextInt(10000);
            Asset asset = Asset.deploy(client, cryptoKeyPair, assetPath);
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
        if (!contractAddress.startsWith("/")) {
            contractAddress = "/" + contractAddress;
        }
        logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    public void queryAssetAmount(String assetAccount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            Tuple2<Boolean, BigInteger> result = asset.getSelectOutput(asset.select(assetAccount));
            if (result.getValue1()) {
                System.out.printf(" asset account %s, value %s %n", assetAccount, result.getValue2());
            } else {
                System.out.printf(" %s asset account is not exist %n", assetAccount);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
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
                if (registerEventEvents.get(0).ret_code.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " register asset account success => asset: %s, value: %s %n", assetAccount, amount);
                } else {
                    System.out.printf(
                            " register asset account failed, ret code is %s %n", registerEventEvents.get(0).ret_code.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, receipt status is: " + receipt.getStatus());
            }
        } catch (Exception e) {
            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.println(" register asset account failed, error message is " + e.getMessage());
        }
    }

    public void transferAsset(String fromAssetAccount, String toAssetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
            List<Asset.TransferEventEventResponse> transferEventEvents = asset.getTransferEventEvents(receipt);
            if (!transferEventEvents.isEmpty()) {
                if (transferEventEvents.get(0).ret_code.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " transfer success => from_asset: %s, to_asset: %s, amount: %s %n",
                            fromAssetAccount, toAssetAccount, amount);
                } else {
                    System.out.printf(
                            " transfer asset account failed, ret code is %s %n", transferEventEvents.get(0).ret_code.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, status is: " + receipt.getStatus());
            }
        } catch (Exception e) {

            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.println(" register asset account failed, error message is " + e.getMessage());
        }
    }

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient deploy");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient query account");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient register account value");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient transfer from_account to_account amount");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            Usage();
        }

        AssetClient client = new AssetClient();
        client.initialize();
        try {

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
        } catch (Exception e) {
            System.out.println("Error: " + e);
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
Asset asset = Asset.deploy(client, cryptoKeyPair, assetPath);
// 加载合约地址
Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
```

- 接口调用

使用合约对象调用对应的接口，处理返回结果。

```java
// select接口调用
Tuple2<Boolean, BigInteger> result = asset.select(assetAccount);
// register接口调用
TransactionReceipt receipt = asset.register(assetAccount, amount);
// transfer接口
TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
```

在``asset-app-liquid/tool``目录下添加一个调用AssetClient的脚本``asset_run.sh``。

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

    java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:conf/:lib/*' org.fisco.bcos.asset.liquid.client.AssetClient $@
```

接着，配置好log。在``asset-app-liquid/src/test/resources``目录下创建``log4j.properties``

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

接着，通过配置gradle中的Jar命令，指定复制和编译任务。并引入日志库，在``asset-app-liquid/src/test/resources``目录下，创建一个空的``contract.properties``文件，用于应用在运行时存放合约地址。

至此，我们已经完成了这个应用的开发。最后，我们得到的asset-app-liquid的目录结构如下：

```shell
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
|   |   |     |-- org
|   |   |          |-- fisco
|   |   |                |-- bcos
|   |   |                      |-- asset
|   |   |                           |-- liquid
|   |   |                               |-- client // 放置客户端调用类
|   |   |                                   |-- AssetClient.java
|   |   |                               |-- contract // 放置Java合约类
|   |   |                                   |-- Asset.java
|   |   |-- resources
|   |        |-- conf
|   |               |-- ca.crt
|   |               |-- cert.cnf
|   |               |-- sdk.crt
|   |               |-- sdk.key
|   |               |-- sdk.nodeid
|   |        |-- applicationContext.xml // 项目配置文件
|   |        |-- contract.properties // 存储部署合约地址的文件
|   |        |-- log4j.properties // 日志配置文件
|   |        |-- contract //存放 WBC-Liquid 合约文件
|   |               |-- asset
|   |                   |-- src
|   |                       |-- lib.rs WBC-Liquid文件
|   |-- test
|       |-- resources // 存放代码资源文件
|           |-- conf
|                   |-- ca.crt
|                   |-- cert.cnf
|                   |-- sdk.crt
|                   |-- sdk.key
|                   |-- sdk.nodeid
|           |-- applicationContext.xml // 项目配置文件
|           |-- contract.properties // 存储部署合约地址的文件
|           |-- log4j.properties // 日志配置文件
|           |-- contract //存放 WBC-Liquid 合约文件
|                   |-- asset
|                       |-- src
|                           |-- lib.rs WBC-Liquid文件
|
|-- tool
    |-- asset_run.sh // 项目运行脚本
```

## 6. 运行应用

至此我们已经介绍使用区块链开发资产管理应用的所有流程并实现了功能，接下来可以运行项目，测试功能是否正常。

- 编译

```shell
# 切换到项目目录
$ cd ~/fisco/asset-app-liquid
# 编译项目
$ ./gradlew build
```

编译成功之后，将在项目根目录下生成`dist`目录。dist目录下有一个`asset_run.sh`脚本，简化项目运行。现在开始一一验证本文开始定下的需求。

- 部署`Asset.liquid`合约

```shell
# 进入dist目录
$ cd dist
$ bash asset_run.sh deploy
deploy Asset success, contract address is /asset/liquid180
```

- 注册资产

```shell
$ bash asset_run.sh register Alice 100000
register asset account success => asset: Alice, value: 100000
$ bash asset_run.sh register Bob 100000
register asset account success => asset: Bob, value: 100000
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
Transfer success => from_account: Alice, to_account: Bob, amount: 50000
$ bash asset_run.sh query Alice
account Alice, value 50000
$ bash asset_run.sh query Bob
asset account Bob,, value 150000
```

**总结：** 至此，我们通过WBC-Liquid合约开发，合约编译，SDK配置与业务开发构建了一个基于FISCO BCOS联盟区块链的WBC-Liquid应用。
