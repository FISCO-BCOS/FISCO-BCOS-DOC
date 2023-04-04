# 快速入门

标签：``java-sdk`` ``引入Java SDK``

----

## 1. 安装环境

- Java推荐：JDK 11 （JDK8 至 JDK 14 都支持）

  首先，在官网上下载JDK11并安装

- IDE：IntelliJ IDE.

  进入[IntelliJ IDE官网](https://www.jetbrains.com/idea/download/)，下载并安装社区版IntelliJ IDE

## 2. 搭建一条FISCO BCOS链

请参考[搭建第一个区块链网络](../../../quick_start/air_installation.html#airfisco-bcos)搭建。

## 3. 开发智能合约应用

### 第一步. 创建一个Gradle应用

在IntelliJ IDE中创建一个gradle项目。勾选Gradle和Java

### 第二步. 引入Java SDK

在build.gradle中引入Java SDK

```gradle
compile ('org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:3.1.0')
```

如果您使用maven 通过以下方法引入Java SDK

``` xml
<dependency>
    <groupId>org.fisco-bcos.java-sdk</groupId>
    <artifactId>fisco-bcos-java-sdk</artifactId>
    <version>3.1.0</version>
</dependency>
```

### 第三步. 配置SDK证书

参考[SDK连接证书配置](../cert_config.md) 进行配置即可，Java SDK证书配置项细则参考[Java SDK证书配置](./config.html#id5)。

将SDK证书拷贝到Java SDK的示例如下(这里假设SDK证书位于`~/fisco/nodes/127.0.0.1/sdk`目录)：

```shell
# 假设SDK证书位于~/fisco/nodes/127.0.0.1/sdk/目录
mkdir -p conf && cp -r ~/fisco/nodes/127.0.0.1/sdk/* conf
```

### 第四步. 准备智能合约

控制台`console`和``java-sdk-demo``均提供了工具，可以将`solidity`合约生成出调用该合约`java`工具类。本例中使用``console``做为例子，使用``java-sdk-demo``的例子请看第6章“附录一. 使用``java-sdk-demo``给智能合约生成调用它的Java工具类”

#### 1. 下载控制台

```shell
$ mkdir -p ~/fisco && cd ~/fisco
# 获取控制台
$ curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.2.0/download_console.sh

# 若因为网络问题导致长时间无法执行以上命令，请尝试以下命令：
$ curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh

$ bash download_console.sh
$ cd ~/fisco/console
```

**然后，将您要用到的Solidity智能合约放入``~/fisco/console/contracts/solidity``的目录**。本次我们用console中的HelloWorld.sol作为例子。保证HelloWorld.sol在指定的目录下。

```shell
# 当前目录~/fisco/console
$ ls contracts/solidity 
```

得到返回

```shell
HelloWorld.sol  KVTableTest.sol ShaTest.sol KVTable.sol
```

**特别的： 如果你想体验 webankblockchain-liquid（以下简称WBC-Liquid）的部署操作，控制台也为你提供了例子。**

在使用之前，请先保证cargo liquid的编译环境，使用搭建请参考：https://liquid-doc.readthedocs.io/。

可在控制台dist目录下contracts/liquid下查看，下面以hello_world为例子：

```shell
$ ls contracts/liquid
asset_test    hello_world   kv_table_test

$ cd contracts/liquid/hello_world

# 使用cargo liquid 编译
$ cargo liquid build
[1/4] 🔍  Collecting crate metadata
[2/4] 🚚  Building cargo project
[3/4] 🔗  Optimizing Wasm bytecode
[4/4] 📃  Generating ABI file

✨ Done in 1 minute, your project is ready now:
Binary: ~/fisco/contracts/liquid/hello_world/target/hello_world.wasm
   ABI: ~/fisco/console/dist/contracts/liquid/hello_world/target/hello_world.abi
```

生成`hello_world.wasm`和`hello_world.abi`两个文件

#### 2. 生成调用该智能合约的java类

```shell
# 当前目录~/fisco/console
$ bash contract2java.sh solidity -p org.com.fisco
# 以上命令中参数“org.com.fisco”是指定产生的java类所属的包名。
# 通过命令./contract2java.sh -h可查看该脚本使用方法
```

得到返回

```shell

*** Compile solidity KVTableTest.sol*** 
INFO: Compile for solidity KVTableTest.sol success.
*** Convert solidity to java  for KVTableTest.sol success ***

*** Compile solidity HelloWorld.sol*** 
INFO: Compile for solidity HelloWorld.sol success.
*** Convert solidity to java  for HelloWorld.sol success ***

*** Compile solidity KVTable.sol*** 
INFO: Compile for solidity Table.sol success.
*** Convert solidity to java  for Table.sol success ***

*** Compile solidity ShaTest.sol*** 
INFO: Compile for solidity ShaTest.sol success.
*** Convert solidity to java  for ShaTest.sol success ***
```

`contract2java.sh`使用方法将在附录2中详细介绍。

查看编译结果

```shell
$ ls contracts/sdk/java/org/com/fisco 
# 得到返回
# HelloWorld.java   KVTableTest.java    ShaTest.java    KVTable.java    TableTest.java
```

**特别的，如果你想使用WBC-Liquid合约编译后的wasm二进制和abi文件生成Java合约**

```shell
# 当前目录~/fisco/console
$ bash contract2java.sh liquid -b ./contracts/liquid/hello_world/hello_world.wasm -a ./contracts/liquid/hello_world/hello_world.abi -s ./contracts/liquid/hello_world/hello_world_sm.wasm -p org.com.fisco
# 通过命令./contract2java.sh -h可查看该脚本使用方法

$ ls contracts/sdk/java/org/com/fisco 
# 得到返回
# HelloWorld.java
```

**最后, 将编译得到的HelloWorld.java放入应用中。**

### 第五步. 创建配置文件

在项目中创建配置文件``config.toml``, 可参照[配置向导](./config.html)进行配置，也可以参照[``config-example.toml``](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/config-example.toml)

通过``xml``配置请参照第4章“附录三. 使用xml配置进行配置”。

### 第六步. 使用Java SDK部署和调用智能合约

以使用Java SDK调用群组1的`getBlockNumber`接口获取群组1最新块高，并向群组1部署和调用`HelloWorld`合约为例，对应的示例代码如下：

```java
public class BcosSDKTest
{
    // 获取配置文件路径
    public final String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.toml").getPath();
     public void testClient() throws ConfigException {
         // 初始化BcosSDK
        BcosSDK sdk =  BcosSDK.build(configFile);
        // 为群组group初始化client
        Client client = sdk.getClient("group0");
    
        // 获取群组1的块高
        BlockNumber blockNumber = client.getBlockNumber();

        // 向群组1部署HelloWorld合约
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);

        // 调用HelloWorld合约的get接口
        String getValue = helloWorld.get();
        
        // 调用HelloWorld合约的set接口
        TransactionReceipt receipt = helloWorld.set("Hello, fisco");
     }
}
```

## 4. 附录

### 附录一. 使用``java-sdk-demo``给智能合约生成调用它的Java工具类

```shell
$ mkdir -p ~/fisco && cd ~/fisco
# 获取java-sdk代码
$ git clone https://github.com/FISCO-BCOS/java-sdk-demo

# 若因为网络问题导致长时间无法执行以上命令，请尝试以下命令：
$ git clone https://gitee.com/FISCO-BCOS/java-sdk-demo

$ cd java-sdk-demo
# 编译
$ ./gradlew clean build -x test
# 进入sdk-demo/dist目录，创建合约存放目录
$ cd dist && mkdir -p contracts/solidity
# 将需要转换为java代码的sol文件拷贝到~/fisco/java-sdk/dist/contracts/solidity路径下
# 转换sol, 其中${packageName}是生成的java代码包路径
# 生成的java代码位于 ~/fisco/java-sdk/dist/contracts/sdk/java目录下
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.codegen.DemoSolcToJava ${packageName}
```

### 附录二. ``contract2java.sh``脚本的使用方法

控制台提供一个专门的生成Java合约工具，方便开发者将Solidity和WBC-Liquid合约文件编译为Java合约文件。

当前合约生成工具支持Solidity的自动编译并生成Java文件、支持指定WBC-Liquid编译后的WASM文件以及ABI文件生成Java文件。

**Solidity合约使用**

```shell
$ bash contract2java.sh solidity -h 
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -h,--help
 -l,--libraries <arg>   [Optional] Set library address information built
                        into the solidity contract
                        eg:
                        --libraries lib1:lib1_address lib2:lib2_address
 -o,--output <arg>      [Optional] The file path of the generated java
                        code, default is contracts/sdk/java/
 -p,--package <arg>     [Optional] The package name of the generated java
                        code, default is com
 -s,--sol <arg>         [Optional] The solidity file path or the solidity
                        directory path, default is contracts/solidity/
```

参数详细：

- `package`: 生成`Java`文件的包名。
- `sol`: (可选)`solidity`文件的路径，支持文件路径和目录路径两种方式，参数为目录时将目录下所有的`solidity`文件进行编译转换。默认目录为`contracts/solidity`。
- `output`: (可选)生成`Java`文件的目录，默认生成在`contracts/sdk/java`目录。

**WBC-Liquid合约使用**

```shell
$ bash contract2java.sh liquid -h
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -a,--abi <arg>       [Required] The ABI file path of WBC-Liquid contract.
 -b,--bin <arg>       [Required] The binary file path of WBC-Liquid contract.
 -h,--help
 -o,--output <arg>    [Optional] The file path of the generated java code,
                      default is contracts/sdk/java/
 -p,--package <arg>   [Optional] The package name of the generated java
                      code, default is com
 -s,--sm-bin <arg>    [Required] The SM binary file path of WBC-Liquid
                      contract.
```

参数详细：

- `abi `：（必选）WBC-Liquid合约`ABI`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `bin`：（必选）WBC-Liquid合约`wasm bin`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `package`：（可选）生成`Java`文件的包名，默认为`org`。
- `sm-bin`：（必选）WBC-Liquid合约`wasm sm bin`文件的路径，在使用`cargo liquid build -g`命令之后生成在target文件夹中。

**使用**

```shell
$ cd ~/fisco/console

# 生成Solidity合约的Java代码
$ bash contract2java.sh solidity -p org.com.fisco

# 生成WBC-Liquid合约的Java代码
$ bash contract2java.sh liquid -p org.com.fisco -b ./contracts/liquid/asset_test/asset_test.wasm -a ./contracts/liquid/asset_test/asset_test.abi -s ./contracts/liquid/asset_test/asset_test_sm.wasm 
```

运行成功之后，将会在`console/contracts/sdk`目录生成java、abi和bin目录，如下所示。

```shell
|-- abi # 编译生成的abi目录，存放solidity合约编译的abi文件
|   |-- HelloWorld.abi
|   |-- KVTable.abi
|   |-- KVTableTest.abi
|-- bin # 编译生成的bin目录，存放solidity合约编译的bin文件
|   |-- HelloWorld.bin
|   |-- KVTable.bin
|   |-- KVTableTest.bin
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|       |-- com
|           |-- fisco
|               |-- HelloWorld.java # Solidity编译的HelloWorld Java文件
|               |-- KVTable.java    # Solidity编译的KV存储接口合约 Java文件
|               |-- KVTableTest.java  # Solidity编译的KVTableTest Java文件
|               |-- AssetTest.java  # WBC-Liquid生成的AssetTest文件
```

Java目录下生成了`org/com/fisco/`包路径目录。包路径目录下将会生成Java合约文件`HelloWorld.java`、`KVTableTest.java`、`KVTable.java`和`AssetTest.java`。其中`HelloWorld.java`、`KVTableTest.java`和`AssetTest.java`是Java应用所需要的Java合约文件。

### 附录三. 使用xml配置进行配置

为了适配更多场景，Java SDK支持使用`xml`初始化`BcosSDK`, `xml`配置示例请参考Java SDK源码的[`applicationContext-sample.xml`](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/applicationContext-sample.xml), 配置项的含义参考[配置说明](./config.md).

通过`xml`配置文件初始化`BcosSDK`之前，需要先引入`spring`。

**通过gradle引入`spring`如下**：

```shell
def spring_version = "4.3.27.RELEASE"
List spring = [
        "org.springframework:spring-core:$spring_version",
        "org.springframework:spring-beans:$spring_version",
        "org.springframework:spring-context:$spring_version",
        "org.springframework:spring-tx:$spring_version",
]
compile spring
```

**通过maven引入`spring`如下**：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.3.27.RELEASE</version>

    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>4.3.27.RELEASE</version>

    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>4.3.27.RELEASE</version>

    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>4.3.27.RELEASE</version>
</dependency>
```

**使用`applicationContext-sample`初始化`BcosSDK`如下**：

```java
ApplicationContext context =
    new ClassPathXmlApplicationContext("classpath:applicationContext-sample.xml");
BcosSDK sdk = context.getBean(BcosSDK.class);
```

### 附录四. 使用`ConfigOption`初始化`BcosSDK`

Java SDK提供了灵活的`BcosSDK`初始化方式，应用除了直接通过`toml`和`xml`直接初始化`BcosSDK`外，还可通过`ConfigProperty`对象加载`ConfigOption`，并使用`ConfigOption`初始化`BcosSDK`。

`ConfigProperty`维护了`key, value`类型配置映射，其核心的数据结构如下：

```java
public class ConfigProperty {
    // 证书配置选项，目前主要包括以下几个配置项：
    // certPath: 证书路径
    // caCert: CA证书路径
    // sslCert: SDK证书
    // sslKey: SDK私钥
    // enSslCert: 国密SSL的SDK证书
    // enSslKey: 国密SSL的SDK私钥
    public Map<String, Object> cryptoMaterial;

    // SDK到节点的网络配置选项，目前包含以下配置选项：
    // peers: 配置SDK连接的节点列表
    public Map<String, Object> network;

    // AMOP配置选项，目前包括以下配置项：
    // topicName: 订阅的AMOP topic
    // publicKeys: 私有AMOP topic中，定义允许接收本客户端消息的其他客户端的公钥列表，用于进行topic认证
    // privateKey: 私有AMOP topic中，定义本客户端的私钥，用于进行topic认证
    // password: 若客户端私钥是p12文件，此配置项定义私钥文件的加载密码
    public List<AmopTopic> amop;

    // 账户配置项，目前包括以下配置项：
    // keyStoreDir: 账户私钥保存路径，默认为account
    // accountFilePath: 从配置文件中加载的账户路劲
    // accountFileFormat: 账户格式，目前支持pem和p12
    // accountAddress: 加载的账户地址
    // password: 加载p12类型账户私钥时，定义访问账户私钥的口令
    public Map<String, Object> account;

    // 线程池配置项，目前主要包括以下配置项:
    // threadPoolSize: 处理RPC消息包的线程数目，默认为CPU核心线程数目
    public Map<String, Object> threadPool;
}
```

应用可根据实际情况初始化`ConfigProperty`，`ConfigProperty`初始化完毕后，可加载产生`ConfigOption`，示例代码如下：

```java
// 从ConfigProperty加载ConfigOption
public ConfigOption loadConfigOption(ConfigProperty configProperty)throws ConfigException
{
    return new ConfigOption(configProperty);
}
```

初始化`ConfigOption`后，可通过`ConfigOption`创建`BcosSDK`，示例代码如下：

```java
public BcosSDK createBcosSDK(ConfigOption configOption)throws BcosSDKException
{
    return new BcosSDK(configOption);
}
```
