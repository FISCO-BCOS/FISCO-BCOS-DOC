# 5. 控制台部署调用合约

-----

本文档描述如何配置控制台，介绍控制台如何部署合约与调用合约

## 1. 下载配置控制台
### 第一步. 安装控制台依赖

控制台运行依赖java环境(推荐使用java 14)，安装命令如下：

```shell
# ubuntu系统安装java
sudo apt install -y default-jdk

# centos系统安装java
sudo yum install -y java java-devel
```

### 第二步. 下载控制台

```shell
cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v3.5.0/download_console.sh && bash download_console.sh
```

```eval_rst
.. note::
   - 如果因为网络问题导致长时间无法下载，请尝试 cd ~/fisco && curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh
```

### 第三步. 配置控制台

- 拷贝控制台配置文件

```shell
cp -n console/conf/config-example.toml console/conf/config.toml
```

```eval_rst
.. note::
   若节点未采用默认端口，请将文件中的20200替换成节点对应的rpc端口，可通过节点config.ini的 ``[rpc].listen_port``配置项获取节点的rpc端口。
```

- 配置控制台证书

控制台与节点之间默认开启SSL连接，控制台需要配置证书才可连接节点。开发建链脚本在生成节点的同时，生成了SDK证书，可直接拷贝生成的证书供控制台使用：

```shell
cp -r nodes/127.0.0.1/sdk/* console/conf
```

### 第四步. 启动并使用控制台

```eval_rst
.. note::
   - 请确保机器的30300~30303，20200~20203端口没有被占用。
   - 控制台的配置方法和命令请参考 `这里 <../operation_and_maintenance/console/index.html>`_ 实现。
```

- 启动

```shell
cd ~/fisco/console && bash start.sh
```

输出下述信息表明启动成功 否则请检查conf/config.toml中节点端口配置是否正确

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.4.0)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=============================================================================================
```


## 2. 部署和调用合约

### 第一步. 编写HelloWorld合约

HelloWorld合约提供了两个接口`get()`和`set()`，用于获取/设置合约变量`name`，合约内容如下：

```shell
pragma solidity >=0.6.10 <0.8.20;
contract HelloWorld {
    string name;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public {
        name = n;
    }
}
```

### 第二步. 部署HelloWorld合约

为了方便用户快速体验，HelloWorld合约已经内置于控制台中，位于控制台目录`contracts/solidity/HelloWorld.sol`，参考下面命令部署：

```shell
# 在控制台输入以下指令 部署成功则返回合约地址
[group0]: /> deploy HelloWorld
transaction hash: 0x796b573aece250bba891b9251b8fb464d22f41cb36e7cae407b2bd0a870f5b72
contract address: 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1
currentAccount: 0x7b047472a4516e9697446576f8c7fcc064f967fa

# 查看当前块高
[group0]: /> getBlockNumber
1
```

### 第三步. 调用HelloWorld合约

```shell
# 调用get接口获取name变量，此处的合约地址是deploy指令返回的地址
[group0]: /> call HelloWorld 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, World!)
---------------------------------------------------------------------------------------------

# 查看当前块高，块高不变，因为get接口不更改账本状态
[group0]: /> getBlockNumber
1

# 调用set方法设置name
[group0]: /> call HelloWorld 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 set "Hello, FISCO BCOS"
transaction hash: 0x2f7c85c2c59a76ccaad85d95b09497ad05ca7983c5ec79c8f9d102d1c8dddc30
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
---------------------------------------------------------------------------------------------
Event logs
Event: {}

# 查看当前块高，因为set接口修改了账本状态，块高增加到2
[group0]: /> getBlockNumber
2

# 退出控制台
[group0]: /> exit
```

至此，我们完成了控制台的配置和使用，并介绍了如何通过控制台部署和调用合约，关于更多的控制台使用教程可参考[这里](../operation_and_maintenance/console/index.md)。

