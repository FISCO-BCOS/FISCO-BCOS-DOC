# 搭建多机区块链网络

标签：``搭建多机区块链网络`` ``区块链教程`` ``HelloWorld`` ``控制台调用合约``

----

[搭建第一个区块链网络](../../quick_start/air_installation.md)章节详细介绍了如何搭建Air版本单机4节点区块链网络，本章以搭建多机4节点区块链网络为例，详细介绍如何在多台机器上部署FISCO BCOS。

## 1. 搭建多机4节点区块链网络

本节详细介绍如何基于[部署工具(build_chain.sh)](./build_chain.md)搭建多机4节点区块链系统。

本教程，假设四台物理机器的IP分别为`196.168.0.1`, `196.168.0.3`, `196.168.0.4`和`196.168.0.2`，每台机器部署一个区块链节点。

```eval_rst
.. note::
    - 请确保每台机器的 ``30300``, ``20200`` 端口没有被占用。
    - 请确保每台机器开通了 ``30300``, ``20200`` 端口的网络访问权限
    - 请确保生成区块链节点配置的机器可以访问外网(用于下载建链脚本)
```

### 第一步. 下载部署工具并生成多机节点配置

**创建操作路径，下载fisco-bcos、开发部署工具build_chain**

```bash
# 创建操作路径~/fisco
mkdir -p ~/fisco && cd ~/fisco

# download_bin.sh, 下载fisco-bcos二进制程序, v指定FISCO-BCOS版本
./download_bin.sh -v 3.2.0

# 下载开发部署工具build_chain
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.2.0/build_chain.sh && chmod u+x build_chain.sh
```

**生成多机节点配置**

```bash
bash build_chain.sh -l "196.168.0.1:1,196.168.0.2:1,196.168.0.3:1,196.168.0.4:1" -o 4nodes
```

`-l`: 参数为要生成节点的ip地址，以及每台机器生成的节点数量；
`-o`: 输出的节点文件路径；
参数详情见[部署工具(build_chain.sh)](./build_chain.md)。

命令执行成功会输出`All completed`。如果执行出错，请检查`4nodes/build.log`文件中的错误信息。

```bash
[INFO] Use binary ./fisco-bcos
[INFO] Generate ca cert successfully!
Processing IP:196.168.0.1 Total:1
[INFO] Generate 4nodes/196.168.0.1/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.1/node0/conf cert successful!
Processing IP:196.168.0.2 Total:1
[INFO] Generate 4nodes/196.168.0.2/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.2/node0/conf cert successful!
Processing IP:196.168.0.3 Total:1
[INFO] Generate 4nodes/196.168.0.3/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.3/node0/conf cert successful!
Processing IP:196.168.0.4 Total:1
[INFO] Generate 4nodes/196.168.0.4/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.4/node0/conf cert successful!
==============================================================
[INFO] fisco-bcos Path     : ./fisco-bcos
[INFO] Auth Mode           : false
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 196.168.0.1:1 196.168.0.2:1 196.168.0.3:1 196.168.0.4:1
[INFO] SM Model            : false
[INFO] output dir          : 4nodes
[INFO] All completed. Files in 4node
```

至此，成功生成了多机4节点配置，每台机器的区块链节点配置均位于`4nodes`文件夹下，如下：

```bash
$ ls 4nodes/
196.168.0.1  196.168.0.2  196.168.0.3  196.168.0.4  ca
```

### 第二步. 拷贝区块链节点配置

生成区块链节点配置后，需要将每个节点配置拷贝到对应机器上，可通过`scp`命令执行拷贝，具体如下：

```bash
# 为每台机器创建操作目录~/fisco
# (注: 这里使用fisco用户进行操作; 实际操作时，可使用自己的账户进行类似操作, IP也需要替换成自己的机器IP)
ssh fisco@196.168.0.1 "mkdir -p ~/fisco"
ssh fisco@196.168.0.2 "mkdir -p ~/fisco"
ssh fisco@196.168.0.3 "mkdir -p ~/fisco"
ssh fisco@196.168.0.4 "mkdir -p ~/fisco"

# 拷贝节点配置
# 拷贝节点配置到196.168.0.1的~/fisco路径
scp -r 4nodes/196.168.0.1/ fisco@196.168.0.1:~/fisco/196.168.0.1
# 拷贝节点配置到196.168.0.2的~/fisco路径
scp -r 4nodes/196.168.0.2/ fisco@196.168.0.2:~/fisco/196.168.0.2
# 拷贝节点配置到196.168.0.3的~/fisco路径
scp -r 4nodes/196.168.0.3/ fisco@196.168.0.3:~/fisco/196.168.0.3
# 拷贝节点配置到196.168.0.4的~/fisco路径
scp -r 4nodes/196.168.0.4/ fisco@196.168.0.4:~/fisco/196.168.0.4
```

### 第三步. 启动多机4节点区块链系统

区块链节点配置拷贝成功后，需要启动所有节点，可通过某台机器发起`ssh`操作远程启动区块链节点，也可登录上所有物理机后，在对应的物理机上启动区块链节点。

**方法一: 远程启动区块链节点**

这里同样从`196.168.0.1`发起节点启动命令，具体如下：

```bash
# (注: 这里使用fisco用户进行操作; 实际操作时，可使用自己的账户进行类似操作, IP也需要替换成自己的机器IP)
# 启动196.168.0.1机器上部署的区块链节点
$ ssh fisco@196.168.0.1 "bash ~/fisco/196.168.0.1/start_all.sh"
try to start node0
 node0 start successfully

# 启动196.168.0.2机器上部署的区块链节点
$ ssh fisco@196.168.0.2 "bash ~/fisco/196.168.0.2/start_all.sh"
try to start node0
 node0 start successfully

# 启动196.168.0.3机器上部署的区块链节点
$ ssh fisco@196.168.0.3 "bash ~/fisco/196.168.0.3/start_all.sh"
try to start node0
 node0 start successfully

# 启动196.168.0.4机器上部署的区块链节点
$ ssh fisco@196.168.0.4 "bash ~/fisco/196.168.0.4/start_all.sh"
try to start node0
 node0 start successfully
```

**方法二: 直接登录机器启动区块链节点**

```bash
# (注: 这里使用fisco用户进行操作; 实际操作时，可使用自己的账户进行类似操作, IP也需要替换成自己的机器IP)
# 登录196.168.0.1并启动区块链节点
$ ssh fisco@196.168.0.1
$ bash ~/fisco/196.168.0.1/start_all.sh

# 登录196.168.0.2并启动区块链节点
$ ssh fisco@196.168.0.2
$ bash ~/fisco/196.168.0.2/start_all.sh

# 登录196.168.0.3并启动区块链节点
$ ssh fisco@196.168.0.3
$ bash ~/fisco/196.168.0.3/start_all.sh

# 登录196.168.0.4并启动区块链节点
$ ssh fisco@196.168.0.4
$ bash ~/fisco/196.168.0.4/start_all.sh

```

至此，已经搭建了一条多机4节点区块链系统，接下来需要检查区块链节点是否正常工作。

### 第四步. 检查区块链节点

**检查进程是否启动成功**

登录每台机器，执行如下命令判断进程是否启动成功：

```bash
ps aux | grep fisco | grep -v grep
```

正常情况，每台机器都会有类似下面的输出；

```bash
fisco     29306  0.8  0.1 747008 31488 ?        Sl   17:08   0:05 /home/ubuntu/fisco/196.168.0.1/node0/../fisco-bcos -c config.ini
```

若某些机器没有类似以上的输出，请检查机器的``30300``, ``20200``端口是否被占用。

**检查网络连接是否正常**

登录每台机器，执行如下命令判断节点网络连接是否正常：

```bash
tail -f ~/fisco/*/node0/log/* |grep -i connected
```

正常情况会不停地输出连接信息，从输出可以看出该节点与其他机器节点连接正常,可通过控制台对其发起交易。

```bash
info|2019-01-21 17:30:58.316769| [P2PService][Service] heartBeat,connected count=3
info|2019-01-21 17:31:08.316922| [P2PService][Service] heartBeat,connected count=3
info|2019-01-21 17:31:18.317105| [P2PService][Service] heartBeat,connected count=3
```

## 2. 配置和使用控制台

本章介绍如何为多机4节点区块链系统配置控制台，并使用控制台对多机区块链系统发起交易。

### 第一步. 准备依赖

- 安装java （推荐使用java 14）.

```bash
# ubuntu系统安装java
sudo apt install -y default-jdk

#centos系统安装java
sudo yum install -y java java-devel
```

### 第二步. 下载并配置控制台

**下载控制台**

```bash
# 创建操作目录
mkdir -p ~/fisco && cd ~/fisco

curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.2.0/download_console.sh && bash download_console.sh

# 若因为网络问题导致长时间无法下载，请尝试以下命令：
curl -#LO  https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh
```

**配置控制台: 拷贝SDK证书**

这里选择从`196.168.0.1`拷贝SDK证书，具体操作如下：

```bash
# 从196.168.0.1拷贝证书到conf目录下
$ scp 196.168.0.1:~/fisco/196.168.0.1/sdk/* ~/fisco/console/conf
```

**配置控制台: 修改控制台配置**

```bash
# 拷贝控制台配置
$ cp -n ~/fisco/console/conf/config-example.toml ~/fisco/console/conf/config.toml

# 修改控制台连接信息(操作中，控制台连接的IP信息请根据实际情况填写)
sed -i 's/peers=\["127.0.0.1:20200", "127.0.0.1:20201"\]/peers=["196.168.0.1:20200", "196.168.0.2:20200", "196.168.0.3:20200", "196.168.0.4:20200"]/g' ~/fisco/console/conf/config.toml
```

### 第三步. 启动并使用控制台

**启动控制台**

```bash
bash ~/fisco/console/start.sh
```

控制台启动成功后，会输出如下信息：

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.1.0)!
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

**使用控制台发送交易**

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

# 在控制台输入以下指令 部署成功则返回合约地址
[group0]: /> deploy HelloWorld
transaction hash: 0x796b573aece250bba891b9251b8fb464d22f41cb36e7cae407b2bd0a870f5b72
contract address: 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1
currentAccount: 0x7b047472a4516e9697446576f8c7fcc064f967fa

# 查看当前块高
[group0]: /> getBlockNumber
1

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

至此，我们完成了多机区块链网络的搭建、控制台的配置和使用。
