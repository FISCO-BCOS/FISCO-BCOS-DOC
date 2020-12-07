# 控制台 - 常规问题

## 1. 控制台指令是否区分大小写

区分大小写，命令是完全匹配，可采用`tab`补全命令。

## 2. 控制台是否支持群组切换功能

- **通过switch命令切换群组:** 控制台提供了`switch`命令支持群组切换，可通过`switch [groupID]`(`[groupID]`是切换到的群组ID)，详细请参考[这里](../manual/console_of_java_sdk.html#switch)

- **启动控制台指定需要访问的群组ID:** 使用`./start [groupID]`(`[groupID]`是切换到的群组ID)启动控制台


--------

# 控制台 - `1.x`国密版控制台使用问题

## 1. `1.x`国密版本控制台启动报证书配置错误

**问题描述**

搭建国密版本节点，并将`config.ini`中的`chain.sm_crypto_channel`配置为`true`，且参考[安装教程](../installation.md)与[配置国密版控制台](../manual/console.html#id10)拷贝了证书、配置了国密开关(将`applicationContext.xml`中的`encryptType`设置为1)，启动节点报错如下：

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

--------

## 2. 没有替换国密版`solc`导致合约调用回滚

**问题描述**

版本号不大于`1.0.10`国密版控制台调用合约时，报错`The execution of the contract rolled back`，具体如下:

```bash
[group:1]> deploy HelloWorld
contract address: 0x8396a8793ce537bed6d894e456ea1ba70a449447

[group:1]> call HelloWorld 0x8396a8793ce537bed6d894e456ea1ba70a449447 get
The execution of the contract rolled back.
```

**问题分析**

版本号不大于`1.0.10`国密版控制台调用合约时，须将`solc`包替换为国密版本，否则部署到节点的交易字节码为非国密字节码，国密节点使用非国密字节码执行交易时，交易执行出错，导致交易回滚。

**解决方案**

**方法一:** 升级到最新版本控制台，可参考[配置和使用控制台](../installation.html#id8)
**方法二：**
将`console/lib`目录下的`solcJ-all-0.4.25.jar`替换为国密版本的`solc`，具体操作步骤如下：

```bash
# 获取国密版solc(须保证安装了curl)
# 若curl的时候报错：curl: (7) Failed to connect to raw.githubusercontent.com port 443: Connection refused
# 可以修改/etc/hosts文件，追加如下配置: 
# 199.232.28.133 raw.githubusercontent.com
curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/solcj/solcJ-all-0.4.25-gm.jar

# 替换非国密的`solc`为国密的solc
cp solcJ-all-0.4.25-gm.jar lib/ && rm -rf lib/solcJ-all-0.4.25.jar

# 重新启动控制台，部署并调用合约
bash start.sh
```

--------

# 控制台 - 节点准入操作相关问题

## 1. 节点加入共识列表报错

**问题分析**

为保证新加入共识节点不影响共识，要求新加入节点必须处于运行状态，且与其他共识节点建立网络连接，当新加入的节点未启动或没有与共识节点建立连接时，会报出`nodeID is not in network`的错误。

**解决方案**

参考[这里](../manual/configuration.html#p2p)，为新加入节点配置所有其他共识节点的连接信息，启动新加入节点，重新通过控制台发送`addSealer`命令，将新节点加入到共识节点列表。


## 2. 删除节点报错`nodeID is not in group peers`

**正常报错**，当被删除的节点不属于群组时，会报出该错误，可通过[getGroupPeers](..//manual/console_of_java_sdk.html#getgrouppeers)命令确定群组是否在该群组中.

## 3. 游离节点是否可以同步group数据

游离节点（非群组节点）不参与group内的共识、同步和出块，游离节点可以通过控制台`addSealer/addObserver`命令可以将退出的节点添加为共识/观察节点。
