# Hello World

本教程面向初次接触FISCO-BCOS的用户，通过在本机部署FISCO-BCOS以及部署和调用Hello World合约，帮助初学者快速入门FISCO BCOS。

## 首次部署FISCO-BCOS联盟链

### 1. 使用`build_chain`脚本

本节使用[`build_chain`](build_chain.md)脚本在本地搭建一条4节点的FISCO-BCOS链，以`Ubuntu 16.04`系统为例操作。本节使用预编译的静态`fisco-bcos`二进制，在CentOS 7和Ubuntu 16.04上经过测试。

- 准备环境

[`build_chain`](build_chain.md)脚本依赖于`openssl`，推荐根据自己操作系统安装`openssl 1.0.2`以上版本。

```bash
# Ubuntu16安装依赖
$ sudo apt install -y openssl curl
# 准备环境
$ cd ~ && mkdir fisco && cd fisco
# 下载build_chain.sh脚本
$ curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh && chmod u+x build_chain.sh
# 准备fisco-bcos二进制
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh) -b release-2.0.1
# 检查二进制是否可执行 执行下述命令，看是否输出版本信息
$ ./bin/fisco-bcos -v
```

执行完上述步骤后，fisco目录下结构如下

```bash
fisco
├── bin
│   └── fisco-bcos
└── build_chain.sh
```

- 搭建4节点FISCO-BCOS链

```bash
# 生成一条4节点的FISCO链 4个节点都属于group1 下面指令在fisco目录下执行
# -e 指定fisco-bcos路径 -p指定起始端口，分别是p2p_port,channel_port,jsonrpc_port
# 根据下面的指令，需要保证机器的30300-30303 20200-20203 8545-8548端口没有被占用
$ ./build_chain.sh -e bin/fisco-bcos -l "127.0.0.1:4" -p 30300,20200,8545
```

关于`build_chain.sh`脚本选项，请[参考这里](build_chain.md)。命令正常执行会输出`All completed`。（如果没有输出，则参考`nodes/build.log`检查）。

```bash
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
[INFO] Output Dir        : /mnt/d/fisco/nodes
[INFO] CA Key Path       : /mnt/d/fisco/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /mnt/d/fisco/nodes
```

### 2. 启动并检查状态

- 启动FISCO-BCOS链

```bash
# 进入节点目录 当前目录fisco
$ cd nodes/127.0.0.1
# 启动所有节点
$ ./start_all.sh
```

- 检查进程及端口监听

```bash
# 检查进程是否启动 如果进程数不为4，那么进程没启动的原因一般是端口被占用
$ ps -ef | grep -v grep | grep fisco
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini

# 检查监听的端口，当前版本每个节点监听3个端口，分别用于p2p、jsonrpc、channel
$ netstat -ntlp | grep fisco
(Not all processes could be identified, non-owned process info
 will not be shown, you would have to be root to see it all.)
tcp        0      0 0.0.0.0:30300           0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 127.0.0.1:20200         0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 127.0.0.1:8545         0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 0.0.0.0:30301           0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 127.0.0.1:20201         0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 127.0.0.1:8546         0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 0.0.0.0:30302           0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 127.0.0.1:20202         0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 127.0.0.1:8547         0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 0.0.0.0:30303           0.0.0.0:*               LISTEN      5476/fisco-bcos
tcp        0      0 127.0.0.1:20203         0.0.0.0:*               LISTEN      5476/fisco-bcos
tcp        0      0 127.0.0.1:8548         0.0.0.0:*               LISTEN      5476/fisco-bcos
```

- 检查日志输出

```bash
# 查看节点node0链接的节点数，从下面的输出可以看出node0与另外3个节点有链接
$ tail -f node0/log/log*  | grep connected
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat connected count,size=3

# 检查是否在共识，如果不停输出++++Generating seal 表示正常输出
$ tail -f node0/log/log*  | grep +++
info|2019-01-21 17:23:32.576197| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=13dcd2da...
info|2019-01-21 17:23:36.592280| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=31d21ab7...
info|2019-01-21 17:23:40.612241| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=49d0e830...
```

### 3. 使用控制台

控制台通过Java SDK链接FISCO BCOS节点，实现查询区块链状态、部署调用合约等功能，能够快速获取到所需要的信息。控制台依赖于**Java8**以上版本(**CentOS**请安装**Oracle Java 8**以上版本)，对于Ubuntu 16.04系统安装**openjdk 8**即可。控制台详细文档[参考这里](console.md)。

- 准备依赖

```bash
# 回到fisco目录
$ cd ~/fisco
# 安装openjdk
$ sudo apt install -y default-jdk
$ curl -LO https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/tools/console.tar.gz
$ tar -zxf console.tar.gz && chmod u+x console/start
# 配置控制台证书
$ cp nodes/127.0.0.1/sdk/* console/conf/
```

- 启动控制台

```bash
# # 回到fisco目录
$ cd ~/fisco/console
$ bash ./start
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

- 使用控制台获取信息

```bash
# 获取客户端版本
> getClientVersion
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

## 部署调用Hello World合约

### 1. HelloWorld合约

HelloWorld合约提供两个接口，分别是`get()`和`set()`，用于获取/设置合约变量`name`。合约内容如下

```solidity
pragma solidity ^0.4.2;

contract HelloWorld{
    string name;

    function HelloWorld(){
       name = "Hello, World!";
    }

    function get()constant returns(string){
        return name;
    }

    function set(string n){
    	name = n;
    }
}
```

### 2. 使用控制台部署HelloWorld合约

为了方便用户快速体验，HelloWorld合约已经通过Solidity编译并转为Java接口内置于控制台中，所以接下来参考下面命令部署即可。关于Solidity合约转Java接口，[参考这里](../sdk/index.html)。

```bash
# 在控制台输入以下指令 部署成功则返回合约地址
> deploy HelloWorld
0xb3c223fc0bf6646959f254ac4e4a7e355b50a344
```

### 3. 使用控制台调用HelloWorld合约

```bash
# 查看当前块高
> getBlockNumber
1
# 调用get接口获取name变量
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, World!
# 调用set设置name
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 set "Hello,FISCO-BCOS"
0x21dca087cb3e44f44f9b882071ec6ecfcb500361cad36a52d39900ea359d0895
# 调用get接口获取name变量，检查设置是否生效
> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello,FISCO-BCOS
# 查看当前块高
> getBlockNumber
2
```
