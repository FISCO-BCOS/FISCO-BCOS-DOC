# 快速入门

标签：``java-sdk`` ``引入Java SDK``

----

## 1. 安装环境

- Java推荐：JDK 11 （JDK8 至 JDK 14 都支持）

  首先，在官网上下载JDK11并安装

- IDE：IntelliJ IDE.

  进入[IntelliJ IDE官网](https://www.jetbrains.com/idea/download/)，下载并安装社区版IntelliJ IDE

## 2. 搭建一条FISCO BCOS链

请参考[搭建第一个区块链网络](../../quick_start/air_installation.md)搭建。

## 3. 开发智能合约应用

### 第一步. 创建一个Gradle应用

在IntelliJ IDE中创建一个gradle项目。勾选Gradle和Java

### 第二步. 引入Java SDK

在build.gradle中引入Java SDK

```gradle
compile ('org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:3.6.0')
```

如果您使用maven 通过以下方法引入Java SDK

``` xml
<dependency>
    <groupId>org.fisco-bcos.java-sdk</groupId>
    <artifactId>fisco-bcos-java-sdk</artifactId>
    <version>3.6.0</version>
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

在SDK使用智能合约可以有两种调用方式：

- （适合特定合约场景）生成智能合约的Java接口文件，Java应用可以直接根据Java接口文件部署、调用合约。可参考：[生成智能合约的Java接口文件](./contracts_to_java.html)
- （适合通用合约场景）自行根据合约ABI拼装参数，发起交易请求。可参考：[构造交易与调用](./assemble_transaction.html)

### 第五步. 创建配置文件

在项目中创建配置文件``config.toml``, 可参照[配置向导](./config.html)进行配置，也可以参照[``config-example.toml``](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/config-example.toml)

通过``xml``配置请参照本文档第4节“附录三. 使用xml配置进行配置”。

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

        // 部署HelloWorld合约
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

### 附录一. 使用xml配置进行配置

为了适配更多场景，Java SDK支持使用`xml`初始化`BcosSDK`, `xml`配置示例请参考Java SDK源码的[`applicationContext-sample.xml`](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/applicationContext-sample.xml), 配置项的含义参考[配置说明](./config.md).

通过`xml`配置文件初始化`BcosSDK`之前，需要先引入`spring`。

**使用`applicationContext-sample`初始化`BcosSDK`如下**：

```java
ApplicationContext context =
    new ClassPathXmlApplicationContext("classpath:applicationContext-sample.xml");
BcosSDK sdk = context.getBean(BcosSDK.class);
```

### 附录二. 使用`ConfigOption`初始化`BcosSDK`

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
