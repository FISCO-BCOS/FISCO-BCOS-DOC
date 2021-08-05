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

# 若因为网络问题导致长时间无法下载，请尝试以下命令：
curl -LO https://gitee.com/FISCO-BCOS/LargeFiles/raw/master/tools/solcj/solcJ-all-0.4.25-gm.jar

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

参考[这里](../manual/configuration.html#p2p)，为新加入节点配置所有其他共识节点的连接信息，启动新加入节点，重新通过控制台发送`addSealer`命令，将新节点加入到共识节点列表。
<hr>

## 删除节点报错

**问题描述**

删除节点报错`nodeID is not in group peers`

**问题分析**

正常报错，当被删除的节点不属于群组时，会报出该错误，可通过[getGroupPeers](../console/console_of_java_sdk.html#getgrouppeers)命令确定群组是否在该群组中.

## sol2java.sh报错

**问题描述**

使用脚本sol2java.sh生成java wrapper类时报错: `Unsupported type encountered: tuple`

**答案**

合约的接口参数或者返回值包含struct类型时，sol2java.sh无法将该合约转换生成java wrapper类。

举个例子:
```shell
pragma solidity>=0.4.24 <0.6.11;
pragma experimental ABIEncoderV2;

contract StructSample {
    struct C {
        int c;
    };
    
    C c;
    
    function setC(C _c) public {
        c = _c;
    }
    
    function getC() view public returns(C) {
        return c;
    }
}
```

StructSample合约接口setC参数类型为C，getC返回值类型也为C，使用sol2java.sh转换StructSample合约时提示
```shell
$ bash sol2java.sh aa
Unsupported type encountered: tuple
```

这个问题会在后续版本解决，用户可以关注后续控制台的更新。

## Permission denied错误

**问题描述**

使用控制台进行操作时，出现`Permission denied`的错误

举个例子:
```shell
[group:1]> addObserver 36b58afe86395dd8740967c10557410bc28fb102378b074f8a35651aa963a505330fd17a878f28ec0bef201236fc13d6c85ae87590b240e586cd7ac3fb27950c
{
    "code":-50000,
    "msg":"Permission denied"
}
```

**解决方法**

`Permission denied`错误是因为节点开启了权限控制，控制台当前用来发送交易的账户没有权限，参考下面的链接解决:
[权限控制特性](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/security_control/permission_control.html)
[控制台加载账户](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/console.html#loadaccount)
[控制台切换账户](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/console.html#switchaccount)
