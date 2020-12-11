# 控制台报错

标签：``控制台报错`` ``问题排查``

----

## 控制台疑问

**问题：控制台指令是否区分大小写**

**答案：** 区分大小写，命令是完全匹配，可采用`tab`补全命令。

**问题：控制台是否支持群组切换功能**

**答案：** 可以切换群组。
- **通过switch命令切换群组:** 控制台提供了`switch`命令支持群组切换，可通过`switch [groupID]`(`[groupID]`是切换到的群组ID)，详细请参考[这里](../console/console_of_java_sdk.html#switch)
- **启动控制台指定需要访问的群组ID:** 使用`./start [groupID]`(`[groupID]`是切换到的群组ID)启动控制台

**问题：游离节点是否可以同步group数据**

**答案：**
游离节点（非群组节点）不参与group内的共识、同步和出块，游离节点可以通过控制台`addSealer/addObserver`命令可以将退出的节点添加为共识/观察节点。
<hr>

## 合约调用回滚
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
<hr>

## 节点加入共识列表报错

**问题分析**

为保证新加入共识节点不影响共识，要求新加入节点必须处于运行状态，且与其他共识节点建立网络连接，当新加入的节点未启动或没有与共识节点建立连接时，会报出`nodeID is not in network`的错误。

**解决方案**

参考[这里](../blockchain_dev/configuration.html#p2p)，为新加入节点配置所有其他共识节点的连接信息，启动新加入节点，重新通过控制台发送`addSealer`命令，将新节点加入到共识节点列表。
<hr>

## 删除节点报错

**问题描述**

删除节点报错`nodeID is not in group peers`

**问题分析**

正常报错，当被删除的节点不属于群组时，会报出该错误，可通过[getGroupPeers](../console/console_of_java_sdk.html#getgrouppeers)命令确定群组是否在该群组中.
