# Java SDK - Java版本要求

Java SDK要求JDK版本大于等于1.8，推荐使用`<= OpenJDK/OracleJDK 15`的JDK，不同系统的Java安装请参考[这里](../installation.html#id9)

------------

# Java SDK - 证书配置问题

**问题描述**

将证书放置于`resources`目录，SDK连接节点时，报出如下错误：

```bash
org.fisco.bcos.sdk.channel.ChannelImp    : init channel network error, Not providing all the certificates to connect to the node! Please provide the certificates to connect with the block-chain.
```

**问题分析:** 

Java SDK默认的证书配置路径是`conf`，将证书放置与`resources`目录下但没有修改证书配置，导致SDK与节点建连时无法读取证书

**解决方法:**

- **方法一**：在项目根目录下新建`conf`文件夹，并将SDK证书拷贝到该目录，详细参考[Java SDK快速入门章节的证书配置操作](../sdk/java_sdk/quick_start.html#sdk)
- **方法二**：修改证书配置选项`certPath`，使其指向当前证书路径，证书配置选项说明参考[这里](../sdk/java_sdk/configuration.html#id5)

- **方法三**: 使用`>= 2.7.1`版本的Java SDK

---------

# Java SDK - SSL握手失败

## 1. SSL_MODE_ENABLE_FALSE_START

**问题描述:**

基于**Java SDK 2.6.1-rc1**开发应用时，已经按照[Java SDK快速入门](../sdk/java_sdk/quick_start.md)配置了证书，启动报错如下：

 ```
 java.lang.NoSuchFieldError: SSL_MODE_ENABLE_FALSE_START
    at io.netty.handler.ssl.ReferenceCountedOpenSslEngine.(ReferenceCountedOpenSslEngine.java:355) ~[netty-all-4.1.52.Final.jar:4.1.52.Final]
    at io.netty.handler.ssl.OpenSslEngine.(OpenSslEngine.java:32) ~[netty-all-4.1.52.Final.jar:4.1.52.Final]
 ```

**问题分析:**

应用中引入了4.1.52版本的netty，使用了较高版本的`tcnative`，与Java SDK 2.6.1-rc1中的tcnative版本冲突，导致Java SDK初始化失败

**解决方法：**

升级Java SDK 2.6.1-rc1到Java SDK 2.6.1或者最新，请参考[引入Java SDK](../sdk/java_sdk/quick_start.html#java-sdk).

---------

## 2. PrivateKey type not supported PKCS#8

**问题描述**

应用中同时Java SDK 2.6.1和Web3SDK 2.6.0(或Web3SDK 2.6.1)，已经按照[Java SDK快速入门](../sdk/java_sdk/quick_start.md)配置了证书，SDK启动报错如下：

```bash
 create BcosSDK failed, error info: init channel network error: SSL context init failed, please make sure your cert and key files are properly configured. error info: PrivateKey type not supported PKCS#8
 ```

**问题分析**

Java SDK 2.6.1使用了较高版本的netty和tcnative，Web3SDK 2.6.0与Web3SDK 2.6.1使用的netty和tcnative版本较低，两者同时使用时jar包冲突，导致SDK启动报错。

**解决方法**

由于Java SDK和Web3SDK包含相同的功能，但Java SDK功能更全且会持续迭代，因此不建议同时使用Web3SDK与Java SDK，**建议使用Java SDK替代Web3SDK**.

- **方法一:** 将Web3SDK升级到`2.6.2`版本，使其与Java SDK基于同样版本的netty与tcnative
- **方法二:** 去除对Web3SDK的依赖，仅依赖Java SDK
