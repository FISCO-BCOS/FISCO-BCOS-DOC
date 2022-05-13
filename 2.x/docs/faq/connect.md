# SDK连接节点失败排查思路

## **Important: 前置说明**

### **1. jdk版本支持**

JavaSDK原则上支持从jdk 1.8到jdk 15的`OracleJDK`和`OpenJDK`，但中间有部分版本禁用或者不支持`secp256k1`曲线，会导致sdk与节点之间握手失败，**请首先检查当前使用的jdk是否支持`secp256k1`曲线**，目前已经覆盖测试的jdk版本如下，推荐大家使用：

- **`OracleJDK`**:  
  - 1.8.0_141
  - 1.8.0_202
  - 11.0.2
  - 14.0.2
  - 15.0.2
- **`OpenJDK`**:
  - 11.0.2
  - 14.0.2
  - 15.0.2

- **jdk下载链接:**
  - OracleJDK官网:  
    - https://www.oracle.com/java/technologies/downloads/archive/
  - 国内镜像:
    - http://www.codebaoku.com/jdk/jdk-oracle.html
    - http://www.codebaoku.com/jdk/jdk-openjdk.html

若使用的jdk禁用了`secp256k1`曲线(注意: 如果jdk不支持secp256k1曲线，不适用该方法)，可以参考 [#issue 470](https://github.com/FISCO-BCOS/java-sdk/issues/470)通过手动修改`java.security`属性的方式重新启用`secp256k1`曲线.

### **2. JavaSDK版本说明**

JavaSDK 2.8.1优化了sdk连接失败的日志和报错提示，欢迎使用2.8.1及以上版本的sdk，更便于定位错误问题。

maven方式
```xml
<dependencies>
      <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.53.Final</version>
        </dependency>
 	<dependency>
            <groupId>org.fisco-bcos.java-sdk</groupId>
            <artifactId>fisco-bcos-java-sdk</artifactId>
            <version>2.9.0</version>
            <exclusions>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>io.netty</groupId>
                    <artifactId>netty-all</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
</dependencies>
```

gradle方式
```shell
dependencies {
    compile ('org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:2.9.0')
}
configurations.all {
    resolutionStrategy {
        force 'io.netty:netty-all:4.1.53.Final'
    }
}
```

---------------

## 排查步骤

JavaSDK启动失败时会有类似于如下的异常信息:

```shell
* TRACE INFORMATION:
----------------------------
====> STEP1: try to connect nodes with ecdsa context...
<==== STEP1 Result: try to connect nodes with ecdsa context failed for cert missing
* Missed certificates: [conf/ca.crt,conf/sdk.crt,conf/sdk.key,]
currentPath: /Users/octopus/fisco/asset-app/dist

----------------------------
====> STEP2: connect nodes with ecdsa context failed, try to connect nodes with sm-context...
<==== STEP2 Result: connect with sm context failed for cert missing.
* Missed certificates:
[conf/gm/gmca.crt,conf/gm/gmsdk.crt,conf/gm/gmsdk.key,conf/gm/gmensdk.key,conf/gm/gmensdk.crt,]
currentPath: /Users/octopus/fisco/asset-app/dist
----------------------------
<====> Error: try to connect nodes with both ecdsa and sm context failed <====>
<====> Please refer to github issue: https://github.com/FISCO-BCOS/java-sdk/issues/536
<====> Please refer to fisco-docs: https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/faq/connect.html
----------------------------
* FISCO BCOS Java SDK Version: 2.8.1
* Support secp256k1 : true
* Java Version : 15.0.2
* JDK Disabled NamedCurves : null
* JDK DisableNative Option : null
* OS Name : Mac OS X
* OS Arch : x86_64
* OS Version : 10.16
* JVM Version : 15.0.2+7-27
* JVM Vendor : Oracle Corporation
* JVM Vendor URL : https://java.oracle.com/
```

不同的项目对错误的处理方式不同，这段错误信息可能会重复显示多次，用户从最后的`RACE INFORMATION:`开始排查即可。

### step1：检查是否拷贝证书

若sdk同时抛出下面两个错误，说明没有拷贝证书，需要将证书拷贝到`src/main/resources/conf`子目录或`conf`子目录下：

```bash
<==== STEP1 Result: try to connect nodes with ecdsa context failed for cert missing
* Missed certificates: xxxxxx

<==== STEP2 Result: connect with sm context failed for cert missing.
* Missed certificates: xxxxxx
```

证书拷贝步骤可参考[FISCO BCOS文档：搭建第一个区块链网络](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)


-----------------

### step2: 检查节点是否启动或者SDK与节点之间网络是否连通

若SDK抛出如下错误，说明SDK与节点之间网络不连通，或者节点没有启动：

```shell
* TRACE INFORMATION:
----------------------------
====> STEP1: try to connect nodes with ecdsa context...
<==== STEP1-1: Load certificates for ecdsa context success...
<==== connect nodes failed, reason:
Failed to connect to all the nodes!
* connect to 127.0.0.1:20200 failed! Please make sure the nodes have been started, and the network between the SDK and the nodes are connected normally.reason: Connection refused: /127.0.0.1:20200

* connect to 127.0.0.1:20201 failed! Please make sure the nodes have been started, and the network between the SDK and the nodes are connected normally.reason: Connection refused: /127.0.0.1:20201
```

**通过ps命令检查节点进程是否启动**:

```bash
# 到节点所在机器运行ps命令
ps aux |grep -i fisco-bcos
```

**通过telnet命令检查sdk到节点的网络是否连通**:

```bash
telnet ${节点ip} ${节点channel_listen_port}
```

注意：`channel_listen_port`，在节点config.ini配置文件中：

```bash
$ cat node0/config.ini | egrep channel_listen
   channel_listen_ip=0.0.0.0
   channel_listen_port=20208
```

--------------------

### step3:  检查证书是否拷贝正确

保证项目打开java-sdk日志，在日志中`grep `SSLHandshakeException` 或`ValidatorException`或者`secp256k1`：

```
 grep -iE 'SSLHandshakeException|ValidatorException|secp256k1' sdk.log
```

若输出如下错误说明放置在SDK中的证书错误，需要重新配置证书：

```bash
...
sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: Path does not chain with any of the trust anchors
Caused by: java.security.cert.CertPathValidatorException: Path does not chain with any of the trust anchors
sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: Path does not chain with any of the trust anchors
Caused by: java.security.cert.CertPathValidatorException: Path does not chain with any of the trust anchors
javax.net.ssl.SSLHandshakeException: General OpenSslEngine problem
Caused by: sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: Path does not chain with any of the trust anchors
	Suppressed: javax.net.ssl.SSLHandshakeException: error:14090086:SSL routines:ssl3_get_server_certificate:certificate verify failed
Caused by: java.security.cert.CertPathValidatorException: Path does not chain with any of the trust anchors
javax.net.ssl.SSLHandshakeException: General OpenSslEngine problem
...
```

**Note: 若这里输出disable secp256k1的日志，说明握手失败是由于jdk禁用secp256k1曲线导致的，可继续转step4排查。**

--------------------------------

### step4： 非国密区块链-检查jdk是否支持secp256k1曲线

**Note: 国密区块链且采用国密SSL连接可跳过本检查步骤。**

**FISCO BCOS非国密默认采用secp256k1曲线**，但随着jdk版本的升级，secp256k1曲线逐渐被弃用（因此2.9.0版本在保证向下兼容的同时，非国密连接采用RSA曲线），**对于采用非国密SSL的区块链(节点的config.ini配置`sm_channel_crypto=false`)**，在SDK日志中`grep`关键字`disable.*secp256k1`：

```
cat sdk.log|grep -i 'disable.*secp256k1'
```

若有如下输出，说明当前jdk不支持`secp256k1`曲线，则需要参考[#issue 470](https://github.com/FISCO-BCOS/java-sdk/issues/470) 手动修改`java.security`属性启用`secp256k1`曲线或者替换到支持`secp256k1`曲线的jdk版本：

```bash
Caused by: sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: Algorithm constraints check failed on disabled algorithm: secp256k1
Caused by: java.security.cert.CertPathValidatorException: Algorithm constraints check failed on disabled algorithm: secp256k1
Caused by: java.security.cert.CertPathValidatorException: Algorithm constraints check failed on disabled algorithm: secp256k1
Caused by: sun.security.validator.ValidatorException: PKIX path validation failed: java.security.cert.CertPathValidatorException: Algorithm constraints check failed on disabled algorithm: secp256k1
Caused by: java.security.cert.CertPathValidatorException: Algorithm constraints check failed on disabled algorithm: secp256k1
Caused by: java.security.cert.CertPathValidatorException: Algorithm constraints check failed on disabled algorithm: secp256k1
```

Note: jdk曲线的变更均会记录在`release notes`中，具体可参考 https://www.oracle.com/java/technologies/javase/jdk-relnotes-index.html。

-----------------------------

### step5： 国密区块链 && 启用国密SSL连接 --- 检查netty库是否冲突

**Note: 非国密区块链或者国密区块链但没有开启国密SSL连接可跳过本检查步骤。**

**FISCO BCOS国密区块链若开启国密SSL连接(节点的config.ini配置`sm_channel_crypto=true`)，可能因为netty库冲突，导致sdk连接节点失败**。

**Java SDK默认的netty库版本是`netty-4.1.53.Final`，Spring-boot等其他组件若和Java SDK一起使用，会引入高版本或者低版本的netty，从而因netty冲突导致sdk连接节点失败。**

对于本情况，可在SDK日志中grep关键字`NoSuchMethodError`或`netty`关键字：

```
cat sdk.log|grep -E 'NoSuchMethodError|netty'
```

若输出如下日志，说明netty库冲突：(这里的示例是spring采用了高版本的`netty-4.1.60.Final`，java-sdk不支持该版本的`netty`)。

```bash
	at io.netty.handler.ssl.SslHandler.channelInactive(SslHandler.java:1113) ~[netty-all-4.1.60.Final.jar:4.1.60.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:262) ~[netty-all-4.1.60.Final.jar:4.1.60.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:248) ~[netty-all-4.1.60.Final.jar:4.1.60.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:241) ~[netty-all-4.1.60.Final.jar:4.1.60.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelInactive(DefaultChannelPipeline.java:1405) ~[netty-all-4.1.60.Final.jar:4.1.60.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:262) ~[netty-all-4.1.60.Final.jar:4.1.60.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:248) ~[netty-all-4.1.60.Final.jar:4.1.60.Final]
	...

Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'bcosSDK' defined in class path resource [applicationContext.xml]: Bean instantiation via constructor failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.fisco.bcos.sdk.BcosSDK]: Constructor threw exception; nested exception is java.lang.NoSuchMethodError: 'void io.netty.handler.ssl.OpenSslContext.<init>(java.lang.Iterable, io.netty.handler.ssl.CipherSuiteFilter, io.netty.handler.ssl.ApplicationProtocolConfig, long, long, int, java.security.cert.Certificate[], io.netty.handler.ssl.ClientAuth, java.lang.String[], boolean, boolean)'
Caused by: org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.fisco.bcos.sdk.BcosSDK]: Constructor threw exception; nested exception is java.lang.NoSuchMethodError: 'void io.netty.handler.ssl.OpenSslContext.<init>(java.lang.Iterable, io.netty.handler.ssl.CipherSuiteFilter, io.netty.handler.ssl.ApplicationProtocolConfig, long, long, int, java.security.cert.Certificate[], io.netty.handler.ssl.ClientAuth, java.lang.String[], boolean, boolean)'
Caused by: java.lang.NoSuchMethodError: 'void io.netty.handler.ssl.OpenSslContext.<init>(java.lang.Iterable, io.netty.handler.ssl.CipherSuiteFilter, io.netty.handler.ssl.ApplicationProtocolConfig, long, long, int, java.security.cert.Certificate[], io.netty.handler.ssl.ClientAuth, java.lang.String[], boolean, boolean)'
```


此时参考[#issue 332](https://github.com/FISCO-BCOS/java-sdk/issues/332)，指定netty版本为`4.1.53`解决冲突，大致写法如下：

```xml
<dependency>
		<groupId>io.netty</groupId>
		<artifactId>netty-all</artifactId>
		<version>4.1.53.Final</version>
	</dependency>

	<dependency>
		<groupId>org.fisco-bcos.java-sdk</groupId>
		<artifactId>fisco-bcos-java-sdk</artifactId>
		<version>2.9.0</version>
		<exclusions>
			<exclusion>
				<groupId>io.netty</groupId>
				<artifactId>netty-all</artifactId>
			</exclusion>
		</exclusions>
	</dependency>
```

------------------

**spring + java-sdk** 构建应用，强烈推荐参考应用**spring-boot-crud**:
- [github链接](https://github.com/FISCO-BCOS/spring-boot-crud/tree/master-2.0)
- [gitee链接](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/master-2.0)