# 教程

本教程帮助你开始使用FISCO-BCOS，教程的第一部分面向对FISCO-BCOS感兴趣但缺乏了解的用户。通过在本机部署FISCO-BCOS以及运行FISCO-BCOS HellowWorld帮助初学者快速学习使用FISCO-BCOS。

## 第一次部署FISCO-BCOS联盟链

### 1. 使用[`build-chain`][build_chain]脚本

本节使用[`build-chain`](../manual/buildchain.md)脚本在本地搭建一条4节点的FISCO-BCOS链。我们以Ubuntu 16.04操作系统为例。

- 准备环境

```bash
# 准备环境
cd ~ && mkdir fisco && cd fisco
# 下载build_chain.sh脚本
curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh && chmod u+x build_chain.sh
# 准备fisco-bcos二进制
bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh)
# 检查二进制是否可执行 执行下述命令，看是否输出版本信息
./bin/fisco-bcos -v
```

执行完上述步骤后，fisco目录下结构如下
```bash
fisco
├── bin
│   ├── fisco-bcos
│   └── fisco-static.tar.gz
└── build_chain.sh
```

- 搭建4节点FISCO-BCOS链

```bash
# 生成一条4节点的FISCO链 4个节点都属于group1 下面指令在fisco目录下执行
# -e 指定fisco-bcos路径 -p指定起始端口
./build_chain.sh -e bin/fisco-bcos -l "127.0.0.1:4" -p 30300
```

关于`build-chain`脚本选项，请[参考这里](../manual/buildchain.md)。命令正常执行输出如下，如果没有输出下面的提示，则表示执行过程中出现了错误，错误提示位于`nodes/build.log`。
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
[INFO] Start Port        : 30300
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : mpt
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
cd nodes/127.0.0.1
# 启动所有节点
./start_all.sh
```

- 检查进程及端口监听

```bash
# 检查进程是否启动
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
tcp        0      0 127.0.0.1:30301         0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 127.0.0.1:30302         0.0.0.0:*               LISTEN      5453/fisco-bcos
tcp        0      0 0.0.0.0:30303           0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 127.0.0.1:30304         0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 127.0.0.1:30305         0.0.0.0:*               LISTEN      5459/fisco-bcos
tcp        0      0 0.0.0.0:30306           0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 127.0.0.1:30307         0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 127.0.0.1:30308         0.0.0.0:*               LISTEN      5464/fisco-bcos
tcp        0      0 0.0.0.0:30309           0.0.0.0:*               LISTEN      5476/fisco-bcos
tcp        0      0 127.0.0.1:30310         0.0.0.0:*               LISTEN      5476/fisco-bcos
tcp        0      0 127.0.0.1:30311         0.0.0.0:*               LISTEN      5476/fisco-bcos
```

- 检查日志输出
```bash
# 检查是否在共识，如果不停输出++++Generating seal 表示正常输出
$ tail -f node0/log/log*  | grep +++
info|2019-01-21 17:23:32.576197| [g:1][p:264][CONSENSUS][SEALER]++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=13dcd2da...
info|2019-01-21 17:23:36.592280| [g:1][p:264][CONSENSUS][SEALER]++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=31d21ab7...
info|2019-01-21 17:23:40.612241| [g:1][p:264][CONSENSUS][SEALER]++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=49d0e830...

# 查看节点node0链接的节点数，从下面的输出可以看出node0与另外3个节点有链接
$ tail -f node0/log/log*  | grep connected
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat connected count,size=3
```

### 3. 使用控制台

控制台通过Java SDK链接FISCO-BCOS节点，可以实现查询区块链状态、部署调用合约等功能，能够快速获取到所需要的信息，实乃FISCO-BCOS爱好者必备之良品。控制台依赖于Java，对于Ubuntu 16.04系统安装openjdk即可

- 准备依赖
```bash
# 回到fisco目录
cd ~/fisco
# 安装openjdk
sudo apt install -y default-jdk
# 准备web3sdk TODO: make web3sdk more simple
git clone https://github.com/FISCO-BCOS/web3sdk.git
cd web3sdk
git checkout release-2.0.1
gradle build -x test
mv dist ~/fisco/console
```

- 启动控制台
```bash
# # 回到fisco目录
cd ~/fisco
cp nodes/127.0.0.1/sdk/* console/conf
# TODO: modify execute path to console/
cd console/bin
# TODO: modify ./web3sdk.sh to console
./web3sdk
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

## FISCO-BCOS HelloWorld

### 1. ok合约
ok合约对FISCO-BCOS相当于其他编程语言的Hello World。该合约内部有两个账户`from/to`，提供一个接口`trans(uint num)`，每次调用会从`from`减去`num`并给`to`增加`num`。提供接口`get()`可以获取`to`账户的余额。

```solidity
pragma solidity ^0.4.24;
contract Ok{
    
    struct Account{
        address account;
        uint balance;
    }
    
    struct  Translog {
        string time;
        address from;
        address to;
        uint amount;
    }
    
    Account from;
    Account to;
    event TransEvent(uint num);
    Translog[] log;

    function Ok(){
        from.account=0x1;
        from.balance=10000000000;
        to.account=0x2;
        to.balance=0;

    }
    
    function get()constant returns(uint){
        return to.balance;
    }
    
    function trans(uint num){
        if (from.balance < num || to.balance + num < to.balance)
            return; // Deny overflow

    	from.balance=from.balance-num;
    	to.balance+=num;
        TransEvent(num);
    	log.push(Translog("20170413",from.account,to.account,num));
    }
}
```

### 2. 使用控制台部署ok合约

```bash
# 在控制台输入以下指令 部署成功则返回合约地址
> deploy Ok
0xcb40116051581f37878f2138d0e16949e4eab791
```

### 3. 使用控制台调用ok合约

```bash
# 查看当前块高
> getBlockNumber
1
# 调用trans接口
> call Ok 0xcb40116051581f37878f2138d0e16949e4eab791 trans 10
0xe34c1b6f5b29edbdac5267de729ea93f6bc5d670773a75b15dfbd4065814b51d
# 调用get接口查询to的余额
> call Ok 0xcb40116051581f37878f2138d0e16949e4eab791 get
10
# 查看当前块高
> getBlockNumber
2
```

[build_chain]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/build_chain.sh
