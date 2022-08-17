# java-sdk 常见问题

标签：``JAVA SDK`` ``问题排查``

------------

## 1. java sdk如何解析交易的input

**解决方法**
`2.8.1` 添加了如下接口支持交易input解析:

```java
public Pair<List<Object>, List<ABIObject>> decodeTransactionInput(String ABI, String input);
public List<String> decodeTransactionInputToString(String ABI, String input);
```

对应的PR可参考 [PR #360](https://github.com/FISCO-BCOS/java-sdk/pull/360/files)
目前版本已经正式发布，可引入`2.8.1`以及以上的版本。

---------

## 2. maven环境 java-sdk2.7.2 引用 io.netty4.1.66 错误

**问题描述**
`gradle` 环境，`java-sdk2.7.2` 引用的 `io.netty4.1.53` 运行正常；
`maven` 环境，`java-sdk2.7.2` 引用的 `io.netty4.1.66` 运行失败。

**问题分析**
原因：`io.netty4.1.66` 缺失 `internal` 包；

**解决方法**

第一种:
升级`java-sdk`版本至`java-sdk 2.9.0+`

Maven:

```shell
 <dependency>
  <groupId>org.fisco-bcos.java-sdk</groupId>
  <artifactId>fisco-bcos-java-sdk</artifactId>
  <version>2.9.1</version>
 </dependency>
```

Gradle:

```shell
implementation("org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:2.9.1")
```

第二种:

强制使用低版本netty

```shell
<dependency>
  <groupId>io.netty</groupId>
  <artifactId>netty-all</artifactId>
  <version>4.1.53.Final</version>
 </dependency>

 <dependency>
  <groupId>org.fisco-bcos.java-sdk</groupId>
  <artifactId>fisco-bcos-java-sdk</artifactId>
  <version>2.7.2</version>
  <exclusions>
   <exclusion>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
   </exclusion>
  </exclusions>
 </dependency>
```

---------

## 3. Java SDK没有原生支持从 jar 包中加载证书

**问题描述**
将 **Java SDK** 以及 **SDK证书** 打入到 jar 包后，通过 `java -jar` 的方式运行demo，提示报错:

```
org.fisco.bcos.sdk.channel.ChannelImp    : init channel network error, Not providing all the certificates to connect to the node! Please provide the certificates to connect with the block-chain.
```

---------

**解决办法**
目前 `2.8.0+` 已经支持直接加载jar包中的证书，可以通过下面方式引入：

Gradle:

```shell
implementation('org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:2.9.1')
```

Maven:

```shell
<dependency>
    <groupId>org.fisco-bcos.java-sdk</groupId>
    <artifactId>fisco-bcos-java-sdk</artifactId>
    <version>2.9.1</version>
</dependency>
```

基于 `Spring boot` 封装 `java sdk` 请参考: <https://github.com/FISCO-BCOS/spring-boot-crud/tree/master-2.0>

---------

## 4. sdk国密版连接时所需的包重复依赖netty，导致冲突

**问题描述**
![](../../images/java-sdk/import_package_conflict.png)

**解决办法**
SDK国密SSL连接修改了tcnative，可能会导致netty冲突，若出现无法加载bcosSDK，可以通过依赖解析来查找对应冲突，建议指定netty版本到 SDK依赖的最低netty版本，即 `4.1.53`

---------

## 5. 创建BcosSDK时出现warning，是否有影响

**问题描述**
执行下列代码时出现警告信息 `get method for EC5Util failed, method name: convertSpec`

```java
String configFile = Test.class.getClassLoader().getResource("config.toml").getPath();
BcosSDK sdk = BcosSDK.build(configFile);
```

日志如下：

```
2021-03-29 16:39:27,703 INFO [org.fisco.bcos.sdk.BcosSDK] - create BcosSDK, configPath: /C:/Users/Wang/IdeaProjects/fisco-bcos/target/classes/config.toml
2021-03-29 16:39:28,094 INFO [org.fisco.bcos.sdk.network.ConnectionManager] - all connections, size: 1, list: [ConnectionInfo{host='172.16.40.100', port=20200}]
2021-03-29 16:39:28,828 INFO [org.fisco.bcos.sdk.network.ConnectionManager] - build ECDSA ssl context with configured certificates
2021-03-29 16:39:31,499 INFO [org.fisco.bcos.sdk.network.ChannelHandler] - handshake success, host: 172.16.40.100, port: 20200, ctx: 2102505486
2021-03-29 16:39:31,609 INFO [org.fisco.bcos.sdk.channel.ChannelImp] - node: ClientVersion{version='2.7.2', supportedVersion='2.7.2', chainId='1', buildTime='20210201 10:03:03', buildType='Linux/clang/Release', gitBranch='HEAD', gitCommitHash='4c8a5bbe44c19db8a002017ff9dbb16d3d28e9da'}, content: {"id":0,"jsonrpc":"2.0","result":{"Build Time":"20210201 10:03:03","Build Type":"Linux/clang/Release","Chain Id":"1","FISCO-BCOS Version":"2.7.2","Git Branch":"HEAD","Git Commit Hash":"4c8a5bbe44c19db8a002017ff9dbb16d3d28e9da","Supported Version":"2.7.2"}}

2021-03-29 16:39:31,609 INFO [org.fisco.bcos.sdk.channel.ChannelImp] - support channel handshake node
2021-03-29 16:39:31,609 INFO [org.fisco.bcos.sdk.channel.ChannelImp] - channel protocol handshake success, set socket channel protocol, host: 172.16.40.100:20200, channel protocol: ChannelProtocol [protocol=3, nodeVersion=2.7.2, EnumProtocol=VERSION_3]
2021-03-29 16:39:31,656 INFO [org.fisco.bcos.sdk.channel.ChannelImp] - Connect to nodes: 172.16.40.100:20200, java version: 1.8.0_241 ,java vendor: Oracle Corporation
2021-03-29 16:39:31,656 INFO [org.fisco.bcos.sdk.BcosSDK] - create BcosSDK, start channel success, cryptoType: 0
2021-03-29 16:39:31,671 INFO [org.fisco.bcos.sdk.BcosSDK] - create BcosSDK, start channel succ, channelProcessorThreadSize: 4, receiptProcessorThreadSize: 4
2021-03-29 16:39:31,687 INFO [org.fisco.bcos.sdk.service.GroupManagerServiceImpl] - registerBlockNumberNotifyHandler
2021-03-29 16:39:31,687 INFO [org.fisco.bcos.sdk.service.GroupManagerServiceImpl] - registerTransactionNotifyHandler
2021-03-29 16:39:31,718 INFO [org.fisco.bcos.sdk.BcosSDK] - create BcosSDK, create groupManagerService success
2021-03-29 16:39:32,315 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,315 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,505 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,505 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,521 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,537 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,552 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,552 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,552 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,552 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,583 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,583 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,599 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,599 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,630 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,630 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,646 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,646 WARN [org.fisco.bcos.sdk.crypto.keystore.KeyTool] - get method for EC5Util failed, method name: convertSpec
2021-03-29 16:39:32,646 INFO [org.fisco.bcos.sdk.amop.AmopImp] - update subscribe inform 1 peers
2021-03-29 16:39:32,646 INFO [org.fisco.bcos.sdk.amop.AmopImp] - amop module started
2021-03-29 16:39:32,646 INFO [org.fisco.bcos.sdk.amop.AmopImp] - update subscribe inform 1 peers
2021-03-29 16:39:32,646 INFO [org.fisco.bcos.sdk.BcosSDK] - create BcosSDK, create Amop success
```

**回答**
不影响。
这个 `WARN` 是为了解决对**bc库**的兼容引入的，没有任何影响。后续版本我们看看优化日志输出。
