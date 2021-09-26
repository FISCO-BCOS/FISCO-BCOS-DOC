# 证书问题

标签：``证书问题`` ``问题排查`` ``证书位置`` ``证书过期``

----

## 证书放置位置出错（Java SDK）

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
<hr>

## 证书过期

**问题描述**

节点或SDK使用的OpenSSL证书过期了，如何续期？

**解决方法:**

证书续期操作可以参考[证书续期操作](../manual/certificates.html#id9)
<hr>

## 证书验证失败（运维部署工具）

**问题描述**

使用运维部署工具下载命令提示certificate verify failed

**解决方法:**

在 ./pys/tool/utils.py 这个文件的开头中加入如下两行

```python
import ssl
ssl._create_default_https_context = ssl._create_unverified_context
```
<hr>

## 证书配置错误（1.x国密版控制台）
**问题描述**

搭建国密版本节点，并将`config.ini`中的`chain.sm_crypto_channel`配置为`true`，且参考[安装教程](../installation.md)与[配置国密版控制台](../console/console.html#id10)拷贝了证书、配置了国密开关(将`applicationContext.xml`中的`encryptType`设置为1)，启动节点报错如下：

```
Failed to init the console！  Failed to connect to nodes: [ ssl handshake failed:/127.0.0.1:20200]The reasons for failure may be:
	1. the configured certificate is not the same set of certificates as the node's certificate;
	2. the configured certificate is not issued by the same authority as the node's certificate.
	Please refer to https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk.html#id24
```

**问题分析**

节点的`chain.sm_crypto_channel=true`时，SDK与节点之间应采用国密SSL的方式通信，控制台应该加载国密证书，默认的`applicationContext.xml`从如下配置中加载国密证书：

```xml
        <property name="gmCaCert" value="gmca.crt" />
        <property name="gmEnSslCert" value="gmensdk.crt" />
        <property name="gmEnSslKey" value="gmensdk.key" />
        <property name="gmSslCert" value="gmsdk.crt" />
        <property name="gmSslKey" value="gmsdk.key" />
```

生成的SDK国密证书位于`sdk/gm`子目录下，直接拷贝SDK证书到`console/conf`文件夹下，会找不到`gmca.crt`、`gmensdk.crt`等证书，当控制台配置路径下不存在这些证书时，控制台会采用非国密SSL的方式连接节点，握手失败。

**解决方案**

当使用国密SSL连接节点时候，须将国密SDK证书拷贝到`console/conf`子文件夹，操作如下：
```bash
cp console/conf/gm/* console/conf
```

**注:当节点的`chain.sm_crypto_channel=false`时，须保证`console/conf`子文件夹下不存在`gmca.crt`、`gmensdk.crt`等国密证书与私钥，否则控制台会以国密SSL的方式初始化证书，抛出类似的错误，若出现该问题，可通过删除国密SDK证书，执行`rm -rf console/conf/gm*`的方式来解决**
