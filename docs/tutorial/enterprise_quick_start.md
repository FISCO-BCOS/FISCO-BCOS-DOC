# 使用企业级部署工具

考虑到联盟链多个企业地位对等安全的诉求，[FISCO BCOS企业级部署工具](../enterprise_tools/index.md)提供一种多机构间合作部署联盟链的方式。

本章主要以部署3机构2群组6节点的组网模式，为用户讲解企业级部署工具的使用方法。

## 下载安装

使用前请确认已经满足[环境依赖](../enterprise_tools/installation.md)

```bash
$ cd ~/
$ git clone https://github.com/FISCO-BCOS/generator.git
$ cd generator
$ bash ./scripts/install.sh
$ ./generator -h
```

在使用本工具时，需要在meta文件夹下放置`fisco-bcos`二进制程序（除demo命令外），`fisco-bcos`二进制程序的生成方式可以通过以下方式获取:

用户可以自由选择以下任一方式获取FISCO BCOS可执行程序。推荐从GitHub下载预编译二进制。

- 官方提供的静态链接的预编译文件，可以在Ubuntu 16.04和CentOS 7.2以上版本运行。

```bash
# 准备fisco-bcos二进制文件
$ $ ./generator --download_fisco ./meta
# 检查二进制是否可执行 执行下述命令，看是否输出版本信息
$ ./meta/fisco-bcos -v
```

- 源码编译获取可执行程序，参考[源码编译](../manual/get_executable.md)。

## 示例分析

在本节中，我们将在本机IP为`127.0.0.1`生成一个如图所示网络拓扑结构为3机构2群组6节点的组网模式，每个节点的ip，端口号分别为：

![](../../images/enterprise/tutorial_step_2.png)

| 节点序号 |   P2P地址     |   RPC/channel地址     |   所属机构     | 所属群组 |
| :-----------: | :-------------: | :-------------: | :-------------: | :-------------: |
|   节点0     | 127.0.0.1:30300| 127.0.0.1:8545/:20200 | 机构A | 群组1、2 |
|   节点1     | 127.0.0.1:30301| 127.0.0.1:8546/:20201 | 机构A | 群组1、2 |
|   节点2     | 127.0.0.1:30302| 127.0.0.1:8547/:20202 | 机构B | 群组1 |
|   节点3     | 127.0.0.1:30303| 127.0.0.1:8548/:20203 | 机构B | 群组1 |
|  节点4      | 127.0.0.1:30304| 127.0.0.1:8549/:20204 | 机构C | 群组2 |
|  节点5      | 127.0.0.1:30305| 127.0.0.1:8550/:20205 | 机构C | 群组2 |


配置文件中字段的含义解释如下：

|              |                        |
| :----------: | :--------------------: |
|   节点序号   | 节点在配置文件中的序号 |
|    P2P地址    |   节点之间p2p通信地址    |
|    RPC地址    |    节点与sdk通信地址     |

假设如图所示，联盟链中共有3个机构、2个群组、6个节点。

群组1，包含A，B两个机构共4个节点

群组2，包含A，C两个机构共4个节点。

在上述场景中，A机构有两个节点复用，参与到了两个群组。

组网步骤如下：

```eval_rst
.. important::

    使用时建议用户开启三个终端，分别代表机构A、机构B及机构C，以下操作$前表示为generator-A的为机构A进行的操作，generator-B的为机构B进行的操作，generator-C的为机构C进行的操作，没有前缀的为初始化操作。
```

机构A作为收集其他机构证书，生成创世区块的机构，整体流程图如下图所示：

![](../../images/enterprise/agencyA.png)

机构B、C不需要收集证书，只需要与其他机构交换节点的连接地址peers，整体流程如下图所示：

![](../../images/enterprise/agencyOthers.png)

## 联盟链初始化

```bash
# 证书授权机构准备生成证书
# 初始化链证书
$ ./generator --generate_chain_certificate ./dir_chain_ca
# 查看链证书及私钥
$ ls ./dir_chain_ca
ca.crt  ca.key   cert.cnf # 从左至右分别为链证书、链私钥、证书配置文件
```

## 机构A、B初始化

```bash
# 初始化机构A
$ git clone https://github.com/FISCO-BCOS/generator.git ~/generator-A
$ cp ./meta/fisco-bcos ~/generator-A/meta
# 初始化机构A机构证书
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyA
# 查看机构证书及私钥
$ ls dir_agency_ca/agencyA/
agency.crt    agency.key    ca-agency.crt ca.crt    cert.cnf # 从左至右分别为机构证书、机构私钥、链证书签发机构证书中间文件、链证书、证书配置文件
# 发送链证书、机构证书、机构私钥至机构A
# 示例是通过文件拷贝的方式，从证书授权机构将机构证书发送给对应的机构，放到机构的工作目录的meta子目录下
$ cp ./dir_chain_ca/ca.crt ./dir_agency_ca/agencyA/agency.crt ./dir_agency_ca/agencyA/agency.key ~/generator-A/meta/
# 初始化机构B
$ git clone https://github.com/FISCO-BCOS/generator.git ~/generator-B
$ cp ./meta/fisco-bcos ~/generator-B/meta
# 初始化机构B机构证书
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyB
# 发送链证书、机构证书、机构私钥至机构B
$ cp ./dir_chain_ca/ca.crt ./dir_agency_ca/agencyB/agency.crt ./dir_agency_ca/agencyB/agency.key ~/generator-B/meta/
```

## 机构A、B构建群组1

### 机构B修改配置文件

机构B修改conf文件夹下的`node_deployment.ini`如下图所示:

```bash
# 请在~/generator-B目录下执行下述命令
$ cd ~/generator-B
$ vi ./conf/node_deployment.ini
```

```ini
[group]
group_id=1

[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548
```

### 机构B生成并交换配置文件

```bash
# 机构B生成交换文件
generator-B$ ./generator --generate_all_certificates ./agencyB_send
# 机构B利用meta下的机构证书和私钥生成sdk证书
generator-B$ ./generator --generate_sdk_certificate ./meta ./agencyB_sdk
# 查看需要生成文件
$ ls ./agencyB_send
cert_127.0.0.1_30302.crt cert_127.0.0.1_30303.crt peers.txt # 从左至右分别为需要交互给机构A的节点证书，节点连接文件
# 交换证书与peers至机构A
generator-B$ cp -r ./agencyB_send ~/generator-A/
generator-B$ cp -r ./agencyB_send/peers.txt ~/generator-A/meta/peersB.txt
```

### 机构A修改配置文件

机构A修改conf文件夹下的`node_deployment.ini`如下图所示:

```bash
# 请在~/generator-A目录下执行下述命令
$ cd ~/generator-A
$ vi ./conf/node_deployment.ini
```

```ini
[group]
group_id=1

[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
```

### 机构A生成并交换配置文件

```bash
# 机构A生成交换文件
generator-A$ ./generator --generate_all_certificates ./agencyA_send
# 机构A利用meta下的机构证书和私钥生成sdk证书
generator-A$ ./generator --generate_sdk_certificate ./meta ./agencyA_sdk
# 由于A机构不需要生成创世区块，因此只需交换peers至机构B
generator-A$ cp -r ./agencyA_send/peers.txt ~/generator-B/meta/peersA.txt
```

### 机构A生成群组1创世区块

机构A修改conf文件夹下的`group_genesis.ini`如下图所示:

```bash
# 请在~/generator-A目录下执行下述命令
$ cd ~/generator-A
$ vi ./conf/group_genesis.ini
```

```ini
[group]
group_id=1

[nodes]
node0=127.0.0.1:30300
;机构A节点p2p地址
node1=127.0.0.1:30301
;机构A节点p2p地址
node2=127.0.0.1:30302
;机构B节点p2p地址
node3=127.0.0.1:30303
;机构B节点p2p地址
```

```bash
# 将机构B证书放置于meta文件夹
generator-A$ cp ./agencyB_send/* ./meta/

# 生成群组1群组创世区块
generator-A$ ./generator --create_group_genesis ./group
# 将群组1创世区块发送给机构B
generator-A$ cp ./meta/group.1.genesis ~/generator-B/meta
```

### 机构A生成所属节点

```bash
# 请在~/generator-A目录下执行下述命令
$ cd ~/generator-A
```

```bash
# 生成机构A所属节点
generator-A$ ./generator --build_install_package ./meta/peersB.txt ./nodeA
# 启动节点
generator-A$ bash ./nodeA/start_all.sh
# 查看节点进程
$ ps -ef | grep fisco
fisco  15347     1  0 17:22 pts/2    00:00:00 ~/generator-A/nodeA/node_127.0.0.1_30300/fisco-bcos -c config.ini
fisco  15402     1  0 17:22 pts/2    00:00:00 ~/generator-A/nodeA/node_127.0.0.1_30301/fisco-bcos -c config.ini
```

### 机构B生成所属节点

```bash
# 请在~/generator-B目录下执行下述命令
$ cd ~/generator-B
```

```bash
# 查看机构A节点连接文件peersA
generator-B$ cat ./meta/peersA.txt
# 生成机构B所属节点
generator-B$ ./generator --build_install_package ./meta/peersA.txt ./nodeB
# 启动节点
generator-B$ bash ./nodeB/start_all.sh
```

### 查看群组1节点运行状态

查看进程：

```bash
$ ps -ef | grep fisco
# 可以看到如下所示的三个进程
fisco  15347     1  0 17:22 pts/2    00:00:00 ~/generator-A/nodeA/node_127.0.0.1_30300/fisco-bcos -c config.ini
fisco  15402     1  0 17:22 pts/2    00:00:00 ~/generator-A/nodeA/node_127.0.0.1_30301/fisco-bcos -c config.ini
fisco  15457     1  0 17:22 pts/2    00:00:00 ~/generator-B/nodeB/node_127.0.0.1_30302/fisco-bcos -c config.ini
fisco  15498     1  0 17:22 pts/2    00:00:00 ~/generator-B/nodeB/node_127.0.0.1_30303/fisco-bcos -c config.ini
```

查看节点log：

```bash
$ tail -f ./node*/node*/log/log*  | grep +++
# +++即为节点正常共识
info|2019-02-25 17:25:56.028692| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=833bd983...
info|2019-02-25 17:25:59.058625| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=343b1141...
info|2019-02-25 17:25:57.038284| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=1,hash=ea85c27b...
```

至此，我们完成了如图所示机构A、B搭建群组1的操作：

![](../../images/enterprise/tutorial_step_1.png)

## 机构A、C构建群组2

接下来，机构C需要与A进行新群组建立操作，示例中以C生成创世区块为例。

### 证书授权机构初始化机构C

```bash
# 请回到拥有链证书及私钥的目录下操作
# 初始化机构C
$ cd ~/generator
$ git clone https://github.com/FISCO-BCOS/generator.git ~/generator-C
$ cp ./meta/fisco-bcos ~/generator-C/meta
# 初始化机构C机构证书
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyC
$ cp ./dir_chain_ca/ca.crt ./dir_agency_ca/agencyC/agency.crt ./dir_agency_ca/agencyC/agency.key ~/generator-C/meta/
```

### 机构A交换配置文件

由于机构A已经生成过节点证书及peers文件，操作如下：

```bash
# 请在~/generator-A目录下执行下述命令
$ cd ~/generator-A
# 交换证书与peers至机构C
generator-A$ cp -r ./agencyA_send ~/generator-C/
generator-A$ cp -r ./agencyA_send/peers.txt ~/generator-C/meta/peersA.txt
```

### 机构C修改配置文件

机构C修改conf文件夹下的`node_deployment.ini`如下图所示:

```bash
# 请在~/generator-C目录下执行下述命令
$ cd ~/generator-C
$ vi ./conf/node_deployment.ini
```

```ini
[group]
group_id=2

[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30304
channel_listen_port=20204
jsonrpc_listen_port=8549

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30305
channel_listen_port=20205
jsonrpc_listen_port=8550
```

### 机构C生成配置文件

```bash
# 请在~/generator-C目录下执行下述命令
$ cd ~/generator-C
# 机构C生成交换文件
generator-C$ ./generator --generate_all_certificates ./agencyC_send
# 机构B利用meta下的机构证书和私钥生成sdk证书
generator-C$ ./generator --generate_sdk_certificate ./meta ./agencyC_sdk
# 交换机构Cpeers至机构A
generator-C$ cp -r ./agencyC_send/peers.txt ~/generator-A/meta/peersC.txt
```

### 机构C生成群组2创世区块

机构A修改conf文件夹下的`group_genesis.ini`如下图所示:

```bash
# 请在~/generator-A目录下执行下述命令
$ cd ~/generator-C
$ vi ./conf/group_genesis.ini
```

```ini
[group]
group_id=2

[nodes]
node0=127.0.0.1:30300
;机构A节点p2p地址
node1=127.0.0.1:30301
;机构A节点p2p地址
node2=127.0.0.1:30304
;机构C节点p2p地址
node3=127.0.0.1:30305
;机构C节点p2p地址
```

```bash
# 将机构C证书放置于meta文件夹
generator-C$ cp ./agencyA_send/* ./meta/
# 生成群组2创世区块
generator-C$ ./generator --create_group_genesis ./data
# 将群组2创世区块发送给机构A
generator-C$ cp ./data/group.2.genesis ~/generator-A/meta/

```

### 机构C生成所属节点

```bash
# 请在~/generator-C目录下执行下述命令
$ cd ~/generator-C
```

```bash
# 查看机构A节点连接文件peersA
generator-C$ cat ./meta/peersA.txt
# 生成机构C所属节点
generator-C$ ./generator --build_install_package ./meta/peersA.txt ./nodeC
# 启动节点
generator-C$ bash ./nodeC/start_all.sh
```

### 机构A为现有节点初始化群组2

```bash
# 请在~/generator-A目录下执行下述命令
$ cd ~/generator-A
# 添加群组2配置文件至已有节点
generator-A$ ./generator --add_group ./meta/group.2.genesis ./nodeA
# 添加机构C节点连接文件peers至已有节点
generator-A$ ./generator --add_peers ./meta/peersC.txt ./nodeA
# 从启节点
generator-A$ bash ./nodeA/stop_all.sh
generator-A$ bash ./nodeA/start_all.sh
```

### 查看群组2节点运行状态

查看进程：

```bash
$ ps -ef | grep fisco
# 可以看到如下所示的三个进程
fisco  15347     1  0 17:22 pts/2    00:00:00 ~/generator-A/nodeA/node_127.0.0.1_30300/fisco-bcos -c config.ini
fisco  15402     1  0 17:22 pts/2    00:00:00 ~/generator-A/nodeA/node_127.0.0.1_30301/fisco-bcos -c config.ini
fisco  15457     1  0 17:22 pts/2    00:00:00 ~/generator-B/nodeB/node_127.0.0.1_30302/fisco-bcos -c config.ini
fisco  15498     1  0 17:22 pts/2    00:00:00 ~/generator-B/nodeB/node_127.0.0.1_30303/fisco-bcos -c config.ini
fisco  15550     1  0 17:22 pts/2    00:00:00 ~/generator-C/nodeC/node_127.0.0.1_30304/fisco-bcos -c config.ini
fisco  15589     1  0 17:22 pts/2    00:00:00 ~/generator-C/nodeC/node_127.0.0.1_30305/fisco-bcos -c config.ini
```

查看节点log：

```bash
generator-C$ tail -f ./node*/node*/log/log*  | grep +++
# +++即为节点正常共识
info|2019-02-25 17:25:56.028692| [g:2][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=833bd983...
info|2019-02-25 17:25:59.058625| [g:2][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=343b1141...
info|2019-02-25 17:25:57.038284| [g:2][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=1,hash=ea85c27b...
```

至此，我们完成了如图所示的机构A、C搭建群组2构建：

![](../../images/enterprise/tutorial_step_2.png)

## 扩展教程--机构C节点加入群组1

将节点加入已有群组需要用户使用控制台发送指令，将节点加入群组，示例如下：

```bash
# 请在~/generator-A目录下执行下述命令
$ cd ~/generator-A
# 发送群组1配置文件至机构C节点
generator-A$ ./generator --add_group ./group/group.1.genesis  ~/generator-C/nodeC
# 从启机构C节点
generator-C$ bash ~/generator-C/nodeC/stop_all.sh
generator-C$ bash ~/generator-C/nodeC/start_all.sh
```

此时节点进程存在，但扩容了两个节点尚未经过group1中的节点共识，需要等待群组1的节点使用[控制台](../manual/console.md)将扩容节点加入group1。

Tips:

- 机构生成的sdk证书在./agency_sdk/目录下

可以看到现在一共有六个fisco-bcos进程存在，但扩容了两个节点尚未经过group1中的节点共识，需要等待群组1的节点使用[控制台](../manual/console.md)将扩容节点加入群组1中，扩容的节点才会正常工作。

至此 我们完成了所示构建教程中的所有操作。

![](../../images/enterprise/tutorial_step_3.png)

通过本节教程，我们在本机生成一个网络拓扑结构为3机构2群组6节点的多群组架构联盟链。

如果使用该教程遇到问题，请查看[FAQ](../faq.md)