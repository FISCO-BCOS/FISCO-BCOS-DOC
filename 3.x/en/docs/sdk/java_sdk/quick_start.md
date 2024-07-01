# Quick Start

Tag: "java-sdk "" Introducing Java SDK "

----

## 1. Installation environment

- Java recommendation: JDK 11 (JDK8 to JDK 14 are supported)

  First, download JDK11 on the official website and install

- IDE：IntelliJ IDE.

  Enter [IntelliJ IDE official website](https://www.jetbrains.com/idea/download/)to download and install the Community Edition IntelliJ IDE

## 2. Build a FISCO BCOS chain

Please refer to [Building the First Blockchain Network](../../quick_start/air_installation.md)Build。

## 3. Develop smart contract applications

### Step 1. Create a Gradle application

Creating a gradle project in the IntelliJ IDE。Check Gradle and Java

### Step 2. Introducing the Java SDK

Introducing the Java SDK in build.gradle

```gradle
compile ('org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:3.6.0')
```

If you use maven to introduce the Java SDK by

``` xml
<dependency>
    <groupId>org.fisco-bcos.java-sdk</groupId>
    <artifactId>fisco-bcos-java-sdk</artifactId>
    <version>3.6.0</version>
</dependency>
```

### Step 3. Configure the SDK certificate

Reference [SDK Connection Certificate Configuration](../cert_config.md) Configure the Java SDK certificate. For more information, see [Java SDK certificate configuration](./config.html#id5)。

An example of copying an SDK certificate to a Java SDK is as follows(It is assumed that the SDK certificate is located in the '~ / fisco / nodes / 127.0.0.1 / sdk' directory)：

```shell
# Assume that the SDK certificate is located in the ~ / fisco / nodes / 127.0.0.1 / sdk / directory
mkdir -p conf && cp -r ~/fisco/nodes/127.0.0.1/sdk/* conf
```

### Step 4: Prepare Smart Contracts

There are two ways to use smart contracts in the SDK:

- (Suitable for specific contract scenarios) Generate Java interface files for smart contracts. Java applications can directly deploy and invoke contracts based on Java interface files.。Reference: [Java interface file for generating smart contracts](./contracts_to_java.html)
- (Suitable for general contract scenarios) Initiate a transaction request on its own according to the contract ABI assembly parameters。Reference: [Constructing Transactions and Calls](./assemble_transaction.html)

### Step 5. Create a configuration file

Create the configuration file "config.toml" in the project. For details, see [Configuration Wizard].(./config.html)For configuration, you can also refer to ["config-example.toml``](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/config-example.toml)

Please refer to Section 4 of this document, "Appendix III. Configuring with xml configuration" for configuration via "xml"。

### Step 6. Deploy and invoke smart contracts using the Java SDK

To use the Java SDK to call the 'getBlockNumber' interface of group 1 to obtain the latest block height of group 1, deploy and call the 'HelloWorld' contract to group 1, for example, the corresponding sample code is as follows:

```java
public class BcosSDKTest
{
    / / Get configuration file path
    public final String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.toml").getPath();
     public void testClient() throws ConfigException {
         / / Initialize the BcosSDK
        BcosSDK sdk =  BcosSDK.build(configFile);
        / / Initialize the client for the group
        Client client = sdk.getClient("group0");
    
        / / Get the block height of group 1
        BlockNumber blockNumber = client.getBlockNumber();

        / / Deploy the HelloWorld contract
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);

        / / Call the get interface of the HelloWorld contract
        String getValue = helloWorld.get();
        
        / / Call the set interface of the HelloWorld contract
        TransactionReceipt receipt = helloWorld.set("Hello, fisco");
     }
}
```

## 4. APPENDIX

### Appendix I. Configuring with XML Configurations

To adapt to more scenarios, the Java SDK supports initializing the 'BcosSDK' with 'xml'. For example, see ['applicationContext' in the Java SDK source code.-sample.xml`](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/applicationContext-sample.xml), refer to [Configuration Description] for the meaning of configuration items.(./config.md).

Before initializing the 'BcosSDK' through the 'xml' configuration file, you need to introduce 'spring'。

**Using the 'applicationContext-sample 'Initialize' BcosSDK 'as follows**：

```java
ApplicationContext context =
    new ClassPathXmlApplicationContext("classpath:applicationContext-sample.xml");
BcosSDK sdk = context.getBean(BcosSDK.class);
```

### Appendix II. Initializing 'BcosSDK' with 'ConfigOption'

Java SDK provides a flexible way to initialize 'BcosSDK'. In addition to directly initializing 'BcosSDK' through 'toml' and 'xml', applications can also load 'ConfigOption' through the 'ConfigProperty' object and use 'ConfigOption' to initialize 'BcosSDK'。

'ConfigProperty 'maintains the' key, value 'type configuration map, and its core data structure is as follows:

```java
public class ConfigProperty {
    / / Certificate configuration options, which mainly include the following configuration items:
    // certPath: Certificate Path
    // caCert: CA Certificate Path
    // sslCert: SDK Certificate
    // sslKey: SDK Private Key
    // enSslCert: State Secret SSL SDK Certificate
    // enSslKey: State Secret SSL SDK private key
    public Map<String, Object> cryptoMaterial;

    / / Network configuration options for the SDK-to-node. Currently, the following configuration options are included:
    // peers: Configure the node list to which the SDK is connected
    public Map<String, Object> network;

    / / AMOP configuration options, which currently include the following:
    // topicName: Subscribed AMOP topic
    // publicKeys: In the private AMOP topic, define the list of public keys of other clients that are allowed to receive messages from this client, which is used for topic authentication
    // privateKey: In the private AMOP topic, define the private key of the client for topic authentication.
    // password: If the client private key is a p12 file, this configuration item defines the password for loading the private key file.
    public List<AmopTopic> amop;

    / / Account configuration items, including the following:
    // keyStoreDir: Save path of the account private key. The default value is account.
    // accountFilePath: Load the account road from the profile.
    // accountFileFormat: Account format, currently supports pem and p12
    // accountAddress: Loaded account address
    // password: Define the password to access the account private key when loading the p12 type account private key
    public Map<String, Object> account;

    / / Thread pool configuration items, which mainly include the following:
    // threadPoolSize: The number of threads that process RPC message packets. The default value is the number of CPU core threads.
    public Map<String, Object> threadPool;
}
```

The application can initialize 'ConfigProperty' according to the actual situation. After 'ConfigProperty' is initialized, you can load and generate 'ConfigOption'. The sample code is as follows:

```java
/ / load ConfigOption from ConfigProperty
public ConfigOption loadConfigOption(ConfigProperty configProperty)throws ConfigException
{
    return new ConfigOption(configProperty);
}
```

After 'ConfigOption' is initialized, you can use 'ConfigOption' to create 'BcosSDK'. The sample code is as follows:

```java
public BcosSDK createBcosSDK(ConfigOption configOption)throws BcosSDKException
{
    return new BcosSDK(configOption);
}
```
