# 搭建第一个区块链网络

标签：``搭建区块链网络`` ``区块链教程`` ``HelloWorld`` ``控制台调用合约``

----

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

本章介绍使用FISCO BCOS底层区块链系统所需要的必要安装和配置。本章通过在单机上部署一条4节点的FISCO BCOS联盟链，帮助用户掌握FISCO BCOS部署流程，请参考[硬件和系统要求](./hardware_requirements.md)使用支持的硬件和平台操作。

```eval_rst
.. note::
   - FISCO BCOS 3.x的系统架构，请参考 `这里 <../design/architecture.html>`_
   - FISCO BCOS 3.x Air版本搭建和使用教程，请参考 `这里 <../tutorial/air/index.html>`_
   - FISCO BCOS 3.x Pro版本搭建和使用教程，请参考 `这里 <../tutorial/pro/index.html>`_
   - FISCO BCOS 3.x Max版本搭建和使用教程，请参考 `这里 <../tutorial/max/index.html>`_
```

## 1. 搭建Air版本FISCO BCOS联盟链

本节以搭建单群组FISCO BCOS链为例操作，使用`开发部署工具build_chain.sh`脚本在本地搭建一条Air版本的4节点的FISCO BCOS链，以Ubuntu 18.04 64bit系统为例操作。

### 第一步. 安装依赖

**安装macOS依赖**

```shell
# 最新homebrew默认下载的为openssl@3，需要指定版本openssl@1.1下载
brew install openssl@1.1 curl wget
```

**安装ubuntu依赖**

```shell
sudo apt install -y curl openssl wget

```

**安装centos依赖**

```shell
sudo yum install -y curl openssl openssl-devel wget
```

### 第二步. 创建操作目录，下载安装脚本

```eval_rst
.. note::
   如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.2.0/build_chain.sh && chmod u+x build_chain.sh
```

```shell
# 创建操作目录
cd ~ && mkdir -p fisco && cd fisco

# 下载建链脚本
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.2.0/build_chain.sh && chmod u+x build_chain.sh

# Note: 若访问git网速太慢，可尝试如下命令下载建链脚本:
curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.2.0/build_chain.sh && chmod u+x build_chain.sh
```

### 第三步. 搭建4节点非国密联盟链

```eval_rst
.. note::
   请确保机器的30300~30303，20200~20203，8545~8548端口没有被占用。
```

在fisco目录下执行下面的指令，生成一条单群组4节点的FISCO链:

```shell
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200
```

```eval_rst
.. note::
   - 其中-p选项指定起始端口，分别是p2p监听端口、rpc监听端口
   - Air版搭建脚本build_chain.sh介绍文档 `参考这里 <../tutorial/air/build_chain.html>`_
```

命令成功后会输出`All completed`：

```shell
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate ./nodes/127.0.0.1/sdk cert successful!
[INFO] Generate ./nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate ./nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate ./nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate ./nodes/127.0.0.1/node3/conf cert successful!
[INFO] Generate uuid success: e273464c-827d-47ce-921f-8c16d72234b0
[INFO] Generate uuid success: b3a54d03-dadb-4cfb-9ae9-86d8ce507110
[INFO] Generate uuid success: 1bf757a5-6649-4d35-9514-d7de95ea7306
[INFO] Generate uuid success: 3c90d210-a202-4d46-a04b-89fcc8c47dd9
==============================================================
[INFO] fisco-bcos Path     : bin/fisco-bcos
[INFO] Auth Mode           : false
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 127.0.0.1:4
[INFO] SM Model            : false
[INFO] output dir          : ./nodes
[INFO] All completed. Files in ./nodes
```

### 第四步. 启动FISCO BCOS链

- 启动所有节点

```shell
bash nodes/127.0.0.1/start_all.sh
```
启动成功会输出如下信息。否则请使用`netstat -an |grep tcp`检查机器`30300~30303, 20200~20203`端口是否被占用。

```shell
try to start node0
try to start node1
try to start node2
try to start node3
 node3 start successfully pid=36430
 node2 start successfully pid=36427
 node1 start successfully pid=36433
 node0 start successfully pid=36428
```

### 第五步. 检查节点进程

- 检查进程是否启动

```shell
ps aux |grep -v grep |grep fisco-bcos
```

正常情况会有类似下面的输出； 如果进程数不为4，则进程没有启动（一般是端口被占用导致的）

```
fisco        35249   7.1  0.2  5170924  57584 s003  S     2:25下午   0:31.63 /home/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini -g config.genesis
fisco        35218   6.8  0.2  5301996  57708 s003  S     2:25下午   0:31.78 /home/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini -g config.genesis
fisco        35277   6.7  0.2  5301996  57660 s003  S     2:25下午   0:31.85 /home/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini -g config.genesis
fisco        35307   6.6  0.2  5301996  57568 s003  S     2:25下午   0:31.93 /home/fisco/nodes//127.0.0.1/node3/../fisco-bcos -c config.ini -g config.genesis
```

### 第六步. 检查日志输出

- 查看每个节点的网络连接数目

以node0为例：

```shell
tail -f nodes/127.0.0.1/node0/log/* |grep -i "heartBeat,connected count"
```

正常情况下会每间隔10秒输出连接信息，从输出日志可看出node0与另外3个节点均有连接，网络连接正常：

```shell
info|2022-08-15 19:38:59.270112|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:09.270210|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:19.270335|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:29.270427|[P2PService][Service][METRIC]heartBeat,connected count=3
```

## 2. 配置和使用控制台

控制台提供了向FISCO BCOS节点部署合约、发起合约调用、查询链状态等功能。

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
cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v3.2.0/download_console.sh && bash download_console.sh
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
   - 请确保机器的30300~30303，20200~20203，8545~8548端口没有被占用。
   - 控制台的配置方法和命令请参考 `这里 <../develop/console/console_config.html>`_
```

- 启动

```shell
cd ~/fisco/console && bash start.sh
```

输出下述信息表明启动成功 否则请检查conf/config.toml中节点端口配置是否正确

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.2.0)!
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

- 用控制台获取信息

```shell
# 获取节点列表信息
[group0]: /> getGroupPeers
peer0: 48fde62f1d2dc59a65bed2e3bb9bd199de26de6b9894e2201199726d6e9e98d090bb1d7e22c931b412728a832ffacdd2727b34fc808f5a755af6bc623c44aba6
peer1: 8e510d66644a8a6caa7e031f097f604501bc42a3851b817f65a18eede0c539f2d011349d470da74cb84a3cf88dbd64a4cc18369fa09b17dac4eec9f65975ecc2
peer2: 9d7ff3f0d3abd54054a99d17a6af27c839f8f7702a4335bdb502323c87a0d7978534a2479cfedb614e1548a869efe038fc49da442b5770aa52c0cc793ca13602
peer3: f0ffa45cee35dcc1bcf1e1ef7b7c3d96590c25ba75198a28ef5ceb89dc6bec310619cb6850231018c8d5a5d698eaf1e5669118e17ea79379211bd332896aa56a

# 获取共识节点列表信息
[group0]: /> getSealerList
[
    Sealer{
        nodeID='9d7ff3f0d3abd54054a99d17a6af27c839f8f7702a4335bdb502323c87a0d7978534a2479cfedb614e1548a869efe038fc49da442b5770aa52c0cc793ca13602',
        weight=1
    },
    Sealer{
        nodeID='f0ffa45cee35dcc1bcf1e1ef7b7c3d96590c25ba75198a28ef5ceb89dc6bec310619cb6850231018c8d5a5d698eaf1e5669118e17ea79379211bd332896aa56a',
        weight=1
    },
    Sealer{
        nodeID='8e510d66644a8a6caa7e031f097f604501bc42a3851b817f65a18eede0c539f2d011349d470da74cb84a3cf88dbd64a4cc18369fa09b17dac4eec9f65975ecc2',
        weight=1
    },
    Sealer{
        nodeID='48fde62f1d2dc59a65bed2e3bb9bd199de26de6b9894e2201199726d6e9e98d090bb1d7e22c931b412728a832ffacdd2727b34fc808f5a755af6bc623c44aba6',
        weight=1
    }
]
```

## 3. 部署和调用合约

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

至此，我们完成了第一条FISCO-BCOS链的搭建、控制台的配置和使用，并部署和调用了第一个合约。关于**Pro版本FISCO BCOS的搭建、配置和使用请参考[这里](../tutorial/pro/index.md)。**
