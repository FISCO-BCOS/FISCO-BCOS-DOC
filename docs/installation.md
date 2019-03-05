# 安装

本章将介绍使用FISCO BCOS所需的必要安装和配置。本章通过在单机上部署一条4节点的FISCO BCOS联盟链，帮助用户掌握FISCO BCOS部署流程。

## 单群组FISCO BCOS联盟链的搭建

本节以搭建单群组FISCO BCOS链为例操作。使用`build_chain`脚本在本地搭建一条4节点的FISCO BCOS链，以`Ubuntu 16.04`系统为例操作。本节使用预编译的静态`fisco-bcos`二进制文件，在CentOS 7和Ubuntu 16.04上经过测试。

```eval_rst
.. note::
    - macOS请参考 `源码编译 <manual/get_executable.html>`_ 并配合 `build_chain <manual/build_chain.html#id4>`_ 的-e选项操作。
    - 搭建多群组的链操作类似，感兴趣可以 `参考这里 <tutorial/group_use_cases.html>`_ 。
```

### 准备环境

`build_chain`脚本依赖于`openssl`，推荐根据自己操作系统安装`openssl 1.0.2`以上版本。

```bash
# Ubuntu 16.04 安装依赖 CentOS将下面命令中的apt替换为yum执行即可
$ sudo apt install -y openssl curl
# 准备环境
$ cd ~ && mkdir fisco && cd fisco
# 下载build_chain.sh脚本
$ curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh && chmod u+x build_chain.sh
# TODO: 发布后删除下面两个步骤
# 准备fisco-bcos二进制文件
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh) -b release-2.0.1
# 检查二进制是否可执行 执行下述命令，看是否输出类似下面的版本信息
$ ./bin/fisco-bcos -v
FISCO-BCOS Version : 2.0.0
Build Time         : 20190226 04:01:24
Build Type         : Linux/clang/RelWithDebInfo
Git Branch         : release-2.0.1
Git Commit Hash    : c213e033328631b1b8c2ee936059d7126fd98d1a
```

### 搭建4节点FISCO BCOS链

```bash
# 生成一条4节点的FISCO链 4个节点都属于同一群组 下面指令在fisco目录下执行
# TODO: 发布后删除-e选项
# -e 指定fisco-bcos路径 -p指定起始端口，分别是p2p_port,channel_port,jsonrpc_port
# 根据下面的指令，需要保证机器的30300~30303，20200~20203，8545~8548端口没有被占用
$ ./build_chain.sh -e bin/fisco-bcos -l "127.0.0.1:4" -p 30300,20200,8545
```

如果命令执行成功会输出`All completed`。如果执行出错，请检查`nodes/build.log`文件中的错误信息。

```bash
Checking fisco-bcos binary...
Binary check passed.
==============================================================
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /home/ubuntu16/fisco/nodes
[INFO] CA Key Path       : /home/ubuntu16/fisco/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /home/ubuntu16/fisco/nodes
```

### 启动FISCO BCOS链

```bash
# 进入节点目录 当前目录fisco
$ cd nodes/127.0.0.1
# 启动所有节点
$ ./start_all.sh
```

### 检查进程及端口监听

```bash
# 检查进程是否启动 如果进程数不为4，那么进程没启动的原因一般是端口被占用
$ ps -ef | grep -v grep | grep fisco-bcos
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini

# 检查监听的端口，当前版本每个节点监听3个端口，分别用于p2p，channel，jsonrpc通信
$ netstat -ntlp | grep fisco-bcos
(Not all processes could be identified, non-owned process info
 will not be shown, you would have to be root to see it all.)
tcp        0      0 0.0.0.0:30300           0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 127.0.0.1:20200         0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 127.0.0.1:8545          0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 0.0.0.0:30301           0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 127.0.0.1:20201         0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 127.0.0.1:8546          0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 0.0.0.0:30302           0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 127.0.0.1:20202         0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 127.0.0.1:8547          0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 0.0.0.0:30303           0.0.0.0:*               LISTEN      5476/fisco-bcos
tcp        0      0 127.0.0.1:20203         0.0.0.0:*               LISTEN      5476/fisco-bcos
tcp        0      0 127.0.0.1:8548          0.0.0.0:*               LISTEN      5476/fisco-bcos
```

### 检查日志输出

```bash
# 查看节点node0链接的节点数，从下面的输出可以看出node0与另外3个节点有链接
$ tail -f node0/log/log*  | grep connected
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat connected count,size=3

# 检查是否在共识，如果不停输出++++Generating seal表示正常输出
$ tail -f node0/log/log*  | grep +++
info|2019-01-21 17:23:32.576197| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=13dcd2da...
info|2019-01-21 17:23:36.592280| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=31d21ab7...
info|2019-01-21 17:23:40.612241| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=49d0e830...
```

## 配置及使用控制台

控制台通过Java SDK链接FISCO BCOS节点，实现查询区块链状态、部署调用合约等功能，能够快速获取到所需要的信息。控制台指令详细介绍[参考这里](manual/console.md)。

```eval_rst
.. important::
    控制台依赖于Java 8以上版本，Ubuntu 16.04系统安装openjdk 8即可。CentOS请安装Oracle Java 8以上版本。
```

### 准备依赖

```bash
# 回到fisco目录
$ cd ~/fisco
# 安装openjdk
$ sudo apt install -y default-jdk
$ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/console-0.4.25.tar.gz
$ tar -zxf console-0.4.25.tar.gz && chmod u+x console-0.4.25/start.sh
# 配置控制台证书
$ cp nodes/127.0.0.1/sdk/* console-0.4.25/conf/
```

### 启动控制台

```bash
# # 回到fisco目录
$ cd ~/fisco/console-0.4.25
$ ./start.sh
# 输出下述信息表明启动成功
=============================================================================================
Welcome to FISCO BCOS console！
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________  ______   ______    ______    ______         _______    ______    ______    ______
|        \|      \ /      \  /      \  /      \       |       \  /      \  /      \  /      \
| $$$$$$$$ \$$$$$$|  $$$$$$\|  $$$$$$\|  $$$$$$\      | $$$$$$$\|  $$$$$$\|  $$$$$$\|  $$$$$$\
| $$__      | $$  | $$___\$$| $$   \$$| $$  | $$      | $$__/ $$| $$   \$$| $$  | $$| $$___\$$
| $$  \     | $$   \$$    \ | $$      | $$  | $$      | $$    $$| $$      | $$  | $$ \$$    \
| $$$$$     | $$   _\$$$$$$\| $$   __ | $$  | $$      | $$$$$$$\| $$   __ | $$  | $$ _\$$$$$$\
| $$       _| $$_ |  \__| $$| $$__/  \| $$__/ $$      | $$__/ $$| $$__/  \| $$__/ $$|  \__| $$
| $$      |   $$ \ \$$    $$ \$$    $$ \$$    $$      | $$    $$ \$$    $$ \$$    $$ \$$    $$
 \$$       \$$$$$$  \$$$$$$   \$$$$$$   \$$$$$$        \$$$$$$$   \$$$$$$   \$$$$$$   \$$$$$$

=============================================================================================
```

### 使用控制台获取信息

```json
# 获取客户端版本
> getNodeVersion
{
    "Build Time":"20190121 06:21:05",
    "Build Type":"Linux/clang/Debug",
    "FISCO-BCOS Version":"2.0.0",
    "Git Branch":"release-2.0.0",
    "Git Commit Hash":"a322f0bff5cb395157fb5734219fcb2f2686ef08"
}
# 获取节点链接信息
> getPeers
[
    {
        "IPAndPort":"127.0.0.1:49948",
        "NodeID":"b5872eff0569903d71330ab7bc85c5a8be03e80b70746ec33cafe27cc4f6f8a71f8c84fd8af9d7912cb5ba068901fe4131ef69b74cc773cdfb318ab11968e41f",
        "Topic":[]
    },
    {
        "IPAndPort":"127.0.0.1:49940",
        "NodeID":"912126291183b673c537153cf19bf5512d5355d8edea7864496c257630d01103d89ae26d17740daebdd20cbc645c9a96d23c9fd4c31d16bccf1037313f74bb1d",
        "Topic":[]
    },
    {
        "IPAndPort":"127.0.0.1:49932",
        "NodeID":"db75ab16ed7afa966447c403ca2587853237b0d9f942ba6fa551dc67ed6822d88da01a1e4da9b51aedafb8c64e9d208d9d3e271f8421f4813dcbc96a07d6a603",
        "Topic":[]
    }
]
```

## 部署及调用HelloWorld合约

### HelloWorld合约

HelloWorld合约提供两个接口，分别是`get()`和`set()`，用于获取/设置合约变量`name`。合约内容如下:

```solidity
pragma solidity ^0.4.2;

contract HelloWorld {
    string name;

    function HelloWorld() {
        name = "Hello, World!";
    }

    function get()constant returns(string) {
        return name;
    }

    function set(string n) {
        name = n;
    }
}
```

### 部署HelloWorld合约

为了方便用户快速体验，HelloWorld合约已经内置于控制台中，位于控制台目录下`solidity/contracts/HelloWorld.sol`，所以参考下面命令部署即可。

```bash
# 在控制台输入以下指令 部署成功则返回合约地址
> deploy HelloWorld
0xb3c223fc0bf6646959f254ac4e4a7e355b50a344
```

### 调用HelloWorld合约

```bash
# 查看当前块高
> getBlockNumber
1
# 调用get接口获取name变量 此处的合约地址是deploy指令返回的地址
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, World!
# 查看当前块高，块高不变，因为get接口不更改账本状态
> getBlockNumber
1
# 调用set设置name
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 set "Hello,FISCO-BCOS"
0x21dca087cb3e44f44f9b882071ec6ecfcb500361cad36a52d39900ea359d0895
# 再次查看当前块高，块高增加表示已出块，账本状态已更改
> getBlockNumber
2
# 调用get接口获取name变量，检查设置是否生效
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello,FISCO-BCOS
```

[build_chain_code]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/tools/build_chain.sh
