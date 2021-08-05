# 搭建多机区块链网络

标签：``搭建多机区块链网络`` ``区块链教程`` ``HelloWorld`` ``控制台调用合约``

----

[搭建第一个区块链网络](../installation.md)章节详细介绍了如何搭建单机4节点区块链网络，本章以搭建多机4节点区块链网络为例，详细介绍如何在多台机器上部署FISCO BCOS。


## 1. 搭建多机4节点区块链网络

本节详细介绍如何基于[开发部署工具build_chain](../manual/build_chain.md)搭建多机4节点区块链系统，生产环境建议使用[运维部署工具](../enterprise_tools/index.html)进行区块链搭建和部署操作。

本教程，假设四台物理机器的IP分别为`196.168.0.1`, `196.168.0.3`, `196.168.0.4`和`196.168.0.2`，每台机器部署一个区块链节点。

```eval_rst
.. note::
    - 请确保每台机器的 ``30300``, ``20200``, ``8545`` 端口没有被占用。
    - 请确保每台机器开通了 ``30300``, ``20200`` 和 ``22`` 端口的网络访问权限
    - 请确保生成区块链节点配置的机器可以访问外网(用于下载建链脚本)
```

### 第一步. 生成区块链节点配置

类似于[单机区块链节点的部署](../installation.md)，部署多机区块链系统之前，首先要生成节点配置，这里假设统一在IP为`196.168.0.1`机器上生成节点配置，具体操作流程如下：

**创建操作路径，下载开发部署工具build_chain**

```bash
# 创建操作路径~/fisco
mkdir -p ~/fisco && cd ~/fisco

# 下载开发部署工具build_chain
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.7.2/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/build_chain.sh && chmod u+x build_chain.sh`
```

**生成区块链网络配置文件**

```bash
# 这里所有区块链节点均属于agencyA，并仅启动了群组1
# (注: 下面的ip信息需要根据真实的机器IP填写)
cat >> ipconf << EOF
196.168.0.1 agencyA 1 
196.168.0.3 agencyA 1 
196.168.0.4 agencyA 1 
196.168.0.2 agencyA 1
EOF
```

**基于配置文件生成区块链节点配置**

```bash
bash build_chain.sh -f ipconf -p 30300,20200,8545
```

命令执行成功会输出`All completed`。如果执行出错，请检查`nodes/build.log`文件中的错误信息。
```bash
Checking fisco-bcos binary...
Binary check passed.
==============================================================
Generating CA key...
==============================================================
Generating keys and certificates ...
Processing IP=196.168.0.1 Total=1 Agency=agencyA Groups=1
Processing IP=196.168.0.3 Total=1 Agency=agencyA Groups=1
Processing IP=196.168.0.4 Total=1 Agency=agencyA Groups=1
Processing IP=196.168.0.2 Total=1 Agency=agencyA Groups=1
==============================================================
Generating configuration files ...
Processing IP=196.168.0.1 Total=1 Agency=agencyA Groups=1
Processing IP=196.168.0.3 Total=1 Agency=agencyA Groups=1
Processing IP=196.168.0.4 Total=1 Agency=agencyA Groups=1
Processing IP=196.168.0.2 Total=1 Agency=agencyA Groups=1
==============================================================
Group:1 has 4 nodes
==============================================================
[INFO] FISCO-BCOS Path : bin/fisco-bcos
[INFO] IP List File    : ipconf
[INFO] Start Port      : 30300 20200 8545
[INFO] Server IP       : 196.168.0.1 196.168.0.3 196.168.0.4 196.168.0.2
[INFO] Output Dir      : /home/ubuntu/fisco/nodes
[INFO] CA Path         : /home/ubuntu/nodes/cert/
==============================================================
[INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/ubuntu/fisco/nodes/196.168.0.1/download_console.sh -f
==============================================================
[INFO] All completed. Files in /root/fisco/nodes
```

至此，成功生成了多机4节点配置，每台机器的区块链节点配置均位于`nodes`文件夹下，如下：

```bash
$ ls nodes/
196.168.0.1  196.168.0.2  196.168.0.3  196.168.0.4  cert
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
scp -r nodes/196.168.0.1/ fisco@196.168.0.1:~/fisco/196.168.0.1
# 拷贝节点配置到196.168.0.2的~/fisco路径
scp -r nodes/196.168.0.2/ fisco@196.168.0.2:~/fisco/196.168.0.2
# 拷贝节点配置到196.168.0.3的~/fisco路径
scp -r nodes/196.168.0.3/ fisco@196.168.0.3:~/fisco/196.168.0.3
# 拷贝节点配置到196.168.0.4的~/fisco路径
scp -r nodes/196.168.0.4/ fisco@196.168.0.4:~/fisco/196.168.0.4
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

若某些机器没有类似以上的输出，请检查机器的``30300``, ``20200``, ``8545``端口是否被占用。

**检查网络连接是否正常**

登录每台机器，执行如下命令判断节点网络连接是否正常：

```bash
tail -f ~/fisco/*/node0/log/* |grep -i connected
```

正常情况会不停地输出连接信息，从输出可以看出该节点与其他机器节点连接正常。
```bash
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat,connected count=3
```

**检查区块链共识是否正常**

登录每台机器，进入操作目录，执行如下命令判断节点共识是否正常：
```bash
tail -f ~/fisco/*/node0/log/* |grep -i +++
```
正常情况会不停输出`++++Generating seal`，表示共识正常。
```bash
info|2020-12-22 17:24:43.729402|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=2e133146...
info|2020-12-22 17:24:47.740603|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=eb199760...
```

若以上检查均正常，说明多机4节点区块链系统部署成功，可通过控制台对其发起交易。

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

curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.7.1/download_console.sh

# 若因为网络问题导致长时间无法下载，请尝试以下命令：
curl -#LO  https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh

bash download_console.sh
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

```bash
=============================================================================================
Welcome to FISCO BCOS console(2.7.0)!
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
若控制台启动失败，请参考[控制台FAQ](../faq/console.md)和[连接问题](../faq/connect.md)进行排查。

**使用控制台发送交易**

```bash
# 获取节点信息
[group:1]> getNodeVersion
ClientVersion{
    version='2.6.0',
    supportedVersion='2.6.0',
    chainId='1',
    buildTime='20200819 15:47:59',
    buildType='Darwin/appleclang/RelWithDebInfo',
    gitBranch='HEAD',
    gitCommitHash='e4a5ef2ef64d1943fccc4ebc61467a91779fb1c0'
}

# 获取当前块高
[group:1]> getBlockNumber
0

# 部署和调用HelloWorld合约
[group:1]> deploy HelloWorld
transaction hash: 0x0e1f192c97a625471597a3483ec230604ff4bb3d22826d118e606ba64bdb0bf9
contract address: 0x302b771c6e7525dc16f945510c210bae0976ad64

[group:1]> call HelloWorld 0x302b771c6e7525dc16f945510c210bae0976ad64 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return values:
[
    "Hello,World!"
]
---------------------------------------------------------------------------------------------

# 获取当前块高(块高增加1)
[group:1]> getBlockNumber
1

# 修改HelloWorld合约状态变量的值
[group:1]> call HelloWorld 0x302b771c6e7525dc16f945510c210bae0976ad64 set "hello, fisco"
transaction hash: 0xaa318a24b8a049778c7167bc2631eedadaeaba326a6cf17c79772036a0edc239
---------------------------------------------------------------------------------------------
transaction status: 0x0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Output
Receipt message: Success
Return message: Success
Return value: []
---------------------------------------------------------------------------------------------
Event logs
Event: {}

# 获取HelloWorld合约状态变量的值
[group:1]>  call HelloWorld 0x302b771c6e7525dc16f945510c210bae0976ad64 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return values:
[
    "hello,fisco"
]
---------------------------------------------------------------------------------------------

# 获取当前块高
[group:1]> getBlockNumber
2
```