# SDK连接节点失败排查思路 

## Important: 关于jdk版本的说明

Java SDK原则上支持从jdk 1.8到jdk 15版本的`oracle jdk`和`Openjdk`，但中间有部分版本禁用了`secp256k1`曲线，会导致SDK与节点之间握手失败，目前推荐如下版本jdk：

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

- **JDK下载链接:**
  - OracleJDK官网:  
    - <https://www.oracle.com/java/technologies/downloads/archive/>
  - 国内镜像:
    - <http://www.codebaoku.com/jdk/jdk-oracle.html>
    - <http://www.codebaoku.com/jdk/jdk-openjdk.html>

若从`1.8`到`jdk 15`的某些小版本jdk不支持`secp256k1`曲线，也可以参考 [#issue 470](https://github.com/FISCO-BCOS/java-sdk/issues/470)通过手动修改`java.security`属性的方式启用jdk.

---------------

## step1：检查是否拷贝证书

若sdk抛出如下错误，说明没有拷贝证书，需要将证书拷贝到`src/main/resources/conf`子目录或`conf`子目录下：

```bash
create BcosSDK failed, error info: init channel network error: Not providing all the certificates to connect to the node! Please provide the certificates to connect with the block-chain.
```



证书拷贝步骤可参考[FISCO BCOS文档：搭建第一个区块链网络](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)

-----------------

## step2: 检查节点是否启动或者SDK与节点之间网络是否连通

若SDK抛出如下错误，说明SDK与节点之间网络不连通，或者节点没有启动：

```
create BcosSDK failed, error info: init channel network error:  Failed to connect to all the nodes! errorMessage:
connect to 127.0.0.1:20200 failed
connect to 127.0.0.1:20201 failed

* If your blockchain is NON-SM,please provide the NON-SM certificates: CryptoMaterialConfig{certPath='conf', caCertPath='conf/ca.crt', sdkCertPath='conf/sdk.crt', sdkPrivateKeyPath='conf/sdk.key', enSSLCertPath='null', enSSLPrivateKeyPath='null', sslCryptoType=0}.

* If your blockchain is SM, please provide the SM certificates: CryptoMaterialConfig{certPath='conf', caCertPath='conf/gm/gmca.crt', sdkCertPath='conf/gm/gmsdk.crt', sdkPrivateKeyPath='conf/gm/gmsdk.key', enSSLCertPath='conf/gm/gmensdk.crt', enSSLPrivateKeyPath='conf/gm/gmensdk.key', sslCryptoType=1}

[System Information]:
[Java Version] : 15.0.1
[OS Name] : Mac OS X
[OS Arch] : x86_64
[OS Version] : 10.16
[Vendor Name] : Oracle Corporation
[Vendor URL] : https://java.oracle.com/
[JVM Version] : 15.0.1+9-18
[JVM Name] : Java HotSpot(TM) 64-Bit Server VM
[JVM Vendor] : Oracle Corporation
[JAVA Library Path] : /Users/chenyujie/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
[JDK Disabled NamedCurves] : null
[JDK DisableNative Option] : false
[Support secp256k1] : true
[Support secp256r1] : true
```


**通过ps命令检查节点进程是否启动**:

```bash
# 到节点所在机器运行ps命令
ps aux |grep -i fisco-bcos
```

**通过telnet命令检查sdk到节点的网络是否连通**:

```bash
telnet ${节点ip} ${节点channel rpc监听端口}
```

--------------------

## step3:  检查证书是否拷贝正确

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

## step4： 非国密区块链-检查jdk是否支持secp256k1曲线

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

##  step5： 国密区块链 && 启用国密SSL连接 --- 检查netty库是否冲突

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
		<version>2.8.0</version>
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