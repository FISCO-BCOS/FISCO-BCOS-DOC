# 构建使用硬件密码模块的国密链

标签：``硬件加密`` ``HSM`` 

---

国密版的FISCO BCOS 2.8.0新增了使用硬件加密模块（Hardware secure module, HSM）进行密码计算的功能，这使得FISCO BCOS拥有更快的密码计算速度，更安全的密钥保护。本教程主主要介绍FISCO BCOS 2.8.0-GMT0018版本的使用方法。

## 1. 选择节点和Java SDK版本

- 当您的节点需要使用硬件加密模块时，您需要运行硬件加密版fisco-bcos (2.8.0-hsm)节点.硬件加密版节点可以与普通版fisco-bcos 2.8.0，以及2.7.0等旧版本节点建立连接构建网络。
- 当您的SDK需要使用硬件加密模块时，您需要使用硬件加密版Java SDK（fisco-bcos-java-sdk-2.8.0-GMT0018.jar）。硬件加密版Java SDK可以连接2.8.0及旧版本节并使用。


## 2. 安装密码卡/密码机
构建使用硬件密码模块的国密链，你需要在节点所在的服务器安装上密码卡或密码机。FISCO BCOS支持了《GMT0018-2012 密码设备应用接口规范》的密码卡/密码机。

### 第一步. 请根据您密码卡/密码机的安装指引安装好密码机.
确保将符合了GMT0018-2012规范的头文件和库文件安装在了动态库默认的搜索路径中。比如：
1. 确保头文件``gmt0018.h``在目录``/usr/include``中，并保证所有用户都有读权限。
2. 请将库文件``libgmt0018.so``放在默认的库搜索路径下，并保证用户具有读和执行权限。如，放在Ubuntu操作系统的``/usr/lib``目录下，放在CentOS操作系统，``/lib64``或``/usr/lib64``目录下。

### 第二步. 请初始化密码卡/密码机，运行其测试程序确保功能正常.
请根据密码卡/密码机厂商的指引初始化设备，并创建你所需要的内部密钥。然后运行测试程序，确保功能正常，确保能通过libgmt0018.so的库正确调用密码机所提供GMT0018-2012的接口方法。

## 3. 从源码编译FISCO BCOS 2.8.0 HSM版二进制
### 第一步. 获取源码
```bash
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
cd FISCO-BCOS
# 切换到2.0版本
git checkout master-2.0
```

### 第二步. 编译源码
```bash
mkdir build && cd build
cmake .. -DUSE_HSM_SDF=on  
# 如果使用CentOS, 请使用cmake3命令
cmake3 .. -DUSE_HSM_SDF=on  
make -j6
```
### 第三步. 确认源码版本
```bash
#当前目录FISCO-BCOS/build
./bin/fisco-bcos --version
```
得到FISCO-BCOS二进制的版本为2.8.0-hsm，则您的支持硬件密码模块的FISCO BCOS编译成功。
```
FISCO-BCOS Version : 2.8.0-hsm
Build Time         : 20210629 18:07:28
Build Type         : Linux/g++/RelWithDebInfo
Git Branch         : release-2.8.0
Git Commit Hash    : 9f1273d8bc9dcef3d6a36e1f32f4f23deaa7d6bb
```

## 4. 创建使用密码机的FISCO BCOS区块链节点
### 第一步. 生成节点
```bash
cd ~/fisco

curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.8.0/build_chain.sh && chmod u+x build_chain.sh

./build_chain.sh -e ~/fisco/FISCO-BCOS/build/bin/fisco-bcos -l 127.0.0.1:4 -g -G
```
### 第二步. 配置密钥类型和密钥索引
例如，配置节点node0使用密码机内部密钥。

首先，请将node0的节点密钥导入密码机。比如，将``~/fisco/nodes/127.0.0.1/node0/conf/gmnode.key``和``~/fisco/nodes/127.0.0.1/node0/conf/gmennode.key``，分别导入到密码机的43和44号密钥索引位置。

然后，修改node0的配置文件，``~/fisco/nodes/127.0.0.1/node0/config.ini``。设置crypto_provider、key_id、enckey_id三个配置项，指定节点使用硬件加密模块，以及硬件加密模块内的密钥索引。
```
[network_security]
    ; directory the certificates located in
    data_path=conf/
    ; the node private key file
    key=gmnode.key
    ; the node certificate file
    cert=gmnode.crt
    ; the ca certificate file
    ca_cert=gmca.crt
    ; use hardware secure module
    crypto_provider=hsm
    ; sign key index
    key_id=43
    ; encrypt key index
    enckey_id=44
```

### 第三步. 启动节点
```bash
./nodes/127.0.0.1/start_all.sh 
```
启动成功
```bash
try to start node0
try to start node1
try to start node2
try to start node3
 node0 start successfully
 node1 start successfully
 node2 start successfully
 node3 start successfully
 ```

 ### 第四步. 确认节点运行正常
 检查进程是否启动

```bash
ps -ef | grep -v grep | grep fisco-bcos
```

正常情况会有类似下面的输出；
如果进程数不为4，则进程没有启动（一般是端口被占用导致的）

```bash
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

如下，查看节点node0链接的节点数

```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```

正常情况会不停地输出连接信息，从输出可以看出node0与另外3个节点有连接。
```bash
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat,connected count=3
```

执行下面指令，检查是否在共识


```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```

正常情况会不停输出`++++Generating seal`，表示共识正常。
```bash
info|2020-12-22 17:24:43.729402|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=2e133146...
info|2020-12-22 17:24:47.740603|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=eb199760...
```

## 4. 通过控制台给区块链网络发送交易
命令行使用Java SDK与节点交互。我们用命令行交互控制台展示Java SDK与FISCO BCOS HSM节点建立连接和发送交易等功能。
### 第一步. 准备依赖

首先，安装java （推荐使用java 14）.

```bash
# ubuntu系统安装java
sudo apt install -y default-jdk

#centos系统安装java
sudo yum install -y java java-devel
```

### 第二步. 构建并启动控制台
然后，获取控制台并构建控制台

```bash
git clone https://github.com/FISCO-BCOS/console.git
cd console
# 切换到2.0版本
git checkout master-2.0
./gradlew build
```

配置控制台
```bash
cp dist/conf/config-example.toml dist/conf/config.toml
# 将SDK的证书从节点复制到dist/conf/目录下
# 假设节点目录w为～/fisco/nodes/
cp -r ～/fisco/nodes/127.0.0.1/sdk/* dist/conf
```

启动并使用控制台

- 启动
```bash
cd dist && bash start.sh
```

输出下述信息表明启动成功 否则请检查conf/config.toml中节点端口配置是否正确

```bash
=============================================================================================
Welcome to FISCO BCOS console(2.8.0)！
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
### 第三步. 使用控制台获取节点信息，并发送交易。

```bash
# 获取节点信息
[group:1]> getNodeVersion
ClientVersion{
    version='2.8.0-hsm gm',
    supportedVersion='2.8.0',
    chainId='1',
    buildTime='20210629 18:07:28',
    buildType='Linux/g++/RelWithDebInfo',
    gitBranch='newoct',
    gitCommitHash='9f1273d8bc9dcef3d6a36e1f32f4f23deaa7d6bb'
}
# 部署HelloWorld
[group:1]> deploy HelloWorld
0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
```

