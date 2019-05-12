# 使用企业级部署工具

FISCO BCOS企业级部署工具面向于真实的多机构生产环境。为了保证机构的秘钥安全，企业级部署工具提供了一种机构间相互合作的的搭链方式。

企业级部署工具与[```build_chain.sh```](group_use_cases.md)不同。``` build_chain.sh ```面向于初学者和开发者。为了简单，``` build_chain.sh ```牺牲了实际生产环境中需要的安全性和功能的全面性。

本章以部署6节点3机构2群组的组网模式，演示企业级部署工具的使用方法。具体使用说明，请参考：[FISCO BCOS企业级部署工具](../enterprise_tools/index.md)。

本章节为多机构对等部署的过程，由单机构一键生成节点配置文件的教程可以参考[FISCO BCOS企业级部署工具一键部署](../enterprise_tools/enterprise_quick_start.md)

## 下载安装

**下载**

```bash
$ cd ~
$ git clone https://github.com/FISCO-BCOS/generator.git
```

**安装**

```bash
$ cd generator
$ bash ./scripts/install.sh # 需输入root密码
```

检查是否安装成功

```bash
$ ./generator -h
# 若成功，输出 usage: generator xxx
```

**拉取节点二进制**

拉取最新fisco-bcos二进制文件到meta中

```bash
$ ./generator --download_fisco ./meta
```

检查二进制版本

```bash
$ ./meta/fisco-bcos -v
# 若成功，输出 FISCO-BCOS Version : x.x.x-x
```

**PS**：[源码编译](../manual/get_executable.md)节点二进制的用户，只需要用编译出来的二进制替换掉``` meta ```文件夹下的二进制即可。

## 典型示例

为了保证机构的秘钥安全，企业级部署工具提供了一种机构间相互合作的的搭链方式。本节以部署6节点3机构2群组的组网模式，演示企业间如何相互配合，搭建区块链。

### 节点组网拓扑结构

一个如图所示的6节点3机构2群组的组网模式。机构B和机构C分别位于群组1和群组2中。机构A为中央机构，同属于群组1和群组2中。

![](../../images/enterprise/tutorial_step_2.png)

### 机器环境

每个节点的IP，端口号为如下。（为了方便操作，所有的节点都部署在```127.0.0.1```）

| 机构  | 节点  | 所属群组 | P2P地址         | RPC/channel监听地址   |
| ----- | ----- | -------- | --------------- | --------------------- |
| 机构A | 节点0 | 群组1、2 | 127.0.0.1:30300 | 127.0.0.1:8545/:20200 |
|       | 节点1 | 群组1、2 | 127.0.0.1:30301 | 127.0.0.1:8546/:20201 |
| 机构B | 节点2 | 群组1    | 127.0.0.1:30302 | 127.0.0.1:8547/:20202 |
|       | 节点3 | 群组1    | 27.0.0.1:30303  | 127.0.0.1:8548/:20203 |
| 机构C | 节点4 | 群组2    | 127.0.0.1:30304 | 127.0.0.1:8549/:20204 |
|       | 节点5 | 群组2    | 127.0.0.1:30305 | 127.0.0.1:8550/:20205 |

### 涉及机构

搭链操作涉及多个机构的合作，包括：

* 证书颁发机构
* 搭建节点的机构（简称“机构”）

### 关键流程

本流程简要的给出**证书颁发机构**，**节点机构间**如何相互配合搭建区块链。

**一、初始化链证书**

**二、生成群组：搭建包含群组1的区块链**

1. 证书颁发机构操作：颁发机构证书
   - 生成机构证书
   - 发送证书
2. 机构间独立操作：生成节点证书
3.  选取一个机构进行操作：为群组生成创世块
   - 收集群组内所有节点证书
   - 为群组生成创世块文件
   - 分发创世块文件
4. 机构间独立操作：生成节点
   - 汇总所有组员节点的URL
   - 生成节点
   - 启动节点

**三、新增群组：在已有区块链上新增群组2**

1. 证书颁发机构操作：颁发新机构证书
   - 生成机构证书
   - 发送证书
2. 新机构独立操作：生成节点证书
3. 选取一个机构进行操作：为群组生成创世块
   - 收集群组内所有节点证书
   - 为群组生成创世块文件
   - 分发创世块文件
4. 新机构独立操作：生成节点
   - 汇总所有组员节点的URL
   - 生成节点
   - 启动节点
5. 已有机构操作：配置新群组
   * 汇总所有组员节点的URL
   * 配置新群组
   * 重启节点

## 具体流程

为了操作简洁，本示例所有操作在同一台机器上进行，用不同的目录区分不同的机构环境。每个机构目录都是一个generator。进行了“[下载安装]()”后，复制多个generator作为多个机构的generator。

``` shell
$ cp -r ~/generator ~/generator_agency_cert # 证书颁发机构
$ cp -r ~/generator ~/generator_agency_A    # 机构 A
$ cp -r ~/generator ~/generator_agency_B    # 机构 B
$ cp -r ~/generator ~/generator_agency_C    # 机构 C
```

### 初始化链证书

在证书颁发机构上进行操作

用 [`--generate_chain_certificate`](../enterprise_tools/operation.html#generate-chain-certificate) 命令生成链证书

```shell
$ cd ~/generator_agency_cert/
$ ./generator --generate_chain_certificate ./dir_chain_ca #生成到dir_chain_ca目录下
```

查看链证书

```shell
$ ls ./dir_chain_ca
ca.crt  ca.key   cert.cnf #链证书、链私钥、证书配置文件
```

### 生成群组：搭建包含群组1的区块链

根据示例的节点组网拓扑结构，搭建区块链。首先生成一个包含群组1的区块链。群组1包含机构A和机构B的所有节点。

#### 1. 证书颁发机构操作：颁发机构证书

在证书颁发机构上进行操作

``` shell
$ cd ~/generator_agency_cert/
```

##### 生成机构证书

生成和机构A证书

``` shell
# ./generator --generate_agency_certificate 机构证书生成目录 用到的链证书目录 本机构名称
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyA
```

机构证书生成至`dir_agency_ca/agencyA`中

``` shell
$ ls ./dir_agency_ca/agencyA/
agency.crt  agency.key  ca-agency.crt  ca.crt  cert.cnf # 机构证书、机构私钥、链证书签发机构证书中间文件、链证书、证书配置文件
```

##### 发送证书

发送证书至A机构的generator，放到meta目录中，包括以下三个文件。

``` shell
$ cp ./dir_chain_ca/ca.crt ~/generator_agency_A/meta                # 链证书
$ cp ./dir_agency_ca/agencyA/agency.crt ~/generator_agency_A/meta   # 机构证书
$ cp ./dir_agency_ca/agencyA/agency.key ~/generator_agency_A/meta   # 机构私钥
```

对机构B也采用相同的操作

``` shell
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyB
$ cp ./dir_chain_ca/ca.crt ~/generator_agency_B/meta                # 链证书
$ cp ./dir_agency_ca/agencyB/agency.crt ~/generator_agency_B/meta   # 机构证书
$ cp ./dir_agency_ca/agencyB/agency.key ~/generator_agency_B/meta   # 机构私钥
```

#### 2. 机构间独立操作：生成节点证书

每个机构独立的在自己的机器上操作，此处以机构A为例，机构B的操作与机构A相同。

``` shell
$ cd ~/generator_agency_A/
```

##### 检查机构证书是否存在

``` shell
$ ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在
```

##### 生成节点证书

编写机构节点配置文件`node_deployment.ini`，将本机构的所有节点配置入文件中。配置文件格式请参考[手册](../enterprise_tools/config.html#node-deployment-ini)。

``` shell
$ cat > ./conf/node_deployment.ini << EOF
[group]
group_id=1

[node0]
; host ip for the communication among peers.
; Please use your ssh login ip.
p2p_ip=127.0.0.1
; listen ip for the communication between sdk clients.
; This ip is the same as p2p_ip for physical host.
; But for virtual host e.g. vps servers, it is usually different from p2p_ip.
; You can check accessible addresses of your network card.
; Please see https://tecadmin.net/check-ip-address-ubuntu-18-04-desktop/
; for more instructions.
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
EOF
```

生成节点证书

``` shell
# ./generator --generate_all_certificates 生成的目录
$ ./generator --generate_all_certificates ./nodes_cert
```

查看生成的文件

``` shell
$ ls ./nodes_cert
cert_127.0.0.1_30300.crt  cert_127.0.0.1_30301.crt  peers.txt # 节点0证书 节点1证书 节点P2P连接URL列表
```

机构B的操作与机构A相同

``` shell
$ cd ~/generator_agency_B/
# 检查证书
$ ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在
# 编写配置
$ cat > ./conf/node_deployment.ini << EOF
[group]
group_id=1

[node0]
; host ip for the communication among peers.
; Please use your ssh login ip.
p2p_ip=127.0.0.1
; listen ip for the communication between sdk clients.
; This ip is the same as p2p_ip for physical host.
; But for virtual host e.g. vps servers, it is usually different from p2p_ip.
; You can check accessible addresses of your network card.
; Please see https://tecadmin.net/check-ip-address-ubuntu-18-04-desktop/
; for more instructions.
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
EOF
# 生成证书
$ ./generator --generate_all_certificates ./nodes_cert
```

#### 3. 选取一个机构进行操作：为群组生成创世块

任意选取此组涉及到机构的其中之一即可，组1有A、B两机构，此处**选取机构A**为例。

``` shell
$ cd ~/generator_agency_A/
```

##### 收集群组内所有节点证书

根据搭链拓扑结构，收集群组1中包含的4个节点的证书

``` shell
$ cp ~/generator_agency_A/nodes_cert/cert_127.0.0.1_30300.crt ./meta/
$ cp ~/generator_agency_A/nodes_cert/cert_127.0.0.1_30301.crt ./meta/
$ cp ~/generator_agency_B/nodes_cert/cert_127.0.0.1_30302.crt ./meta/
$ cp ~/generator_agency_B/nodes_cert/cert_127.0.0.1_30303.crt ./meta/
```

##### 为群组生成创世块文件

修改创世块文件配置`group_genesis.ini`，配置项可参考[手册](../enterprise_tools/config.html#group-genesis-ini)。

``` shell
$ cat > ./conf/group_genesis.ini << EOF
[group]
group_id=1

[nodes]
node0=127.0.0.1:30300
node1=127.0.0.1:30301
node2=127.0.0.1:30302
node3=127.0.0.1:30303
EOF
```

用[`--create_group_genesis`](../enterprise_tools/operation.html#create-group-genesis-c)命令，生成创世块文件，

``` shell
# ./generator --create_group_genesis 创世块文件生成目录
$ ./generator --create_group_genesis ./group1
```

查看生成的文件

``` shell
$ ls ./group1
group.1.genesis # 自动命名规则：group_id为n命名为 group.n.genesis
```

##### 分发创世块文件

将创世块文件分发给**所有**属于此组的机构，放到meta下

``` shell
$ cp ./group1/group.1.genesis ~/generator_agency_A/meta/
$ cp ./group1/group.1.genesis ~/generator_agency_B/meta/
```

#### 4. 机构间独立操作：生成节点

机构A的操作

``` shell
$ cd ~/generator_agency_A/
```

##### 汇总所有组员节点的URL

按照网络拓扑，汇总**所有组员**节点**P2P端口**的URL，排除自己，生成peers文件

``` shell
$ cat > ./conf/peers_group1.txt << EOF
127.0.0.1:30302
127.0.0.1:30303
EOF
```

##### 生成节点

检查依赖的文件

``` shell
$ ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在
$ cat ./conf/node_deployment.ini # 检查已经配置了需要生成的节点（本例中生成证书步骤时已配置好）
```

用[`--build_install_package`](../enterprise_tools/operation.html#build-install-package-b)命令，生成节点

``` shell
# ./generator --build_install_package 提供的peers文件 生成的节点
$ ./generator --build_install_package ./conf/peers_group1.txt ./nodes
```

查看生成的节点目录

``` shell
$ tree ./nodes -L 1
nodes
├── monitor
├── node_127.0.0.1_30300 # 节点 0
├── node_127.0.0.1_30301 # 节点 1
├── scripts
├── start_all.sh
└── stop_all.sh
```

##### 启动节点

``` shell
$ cd ./nodes
$ bash start_all.sh
node_127.0.0.1_30300 start successfully # 节点0成功启动
node_127.0.0.1_30301 start successfully # 节点1成功启动
```

检查节点进程

``` shell
$ ps -ef |grep "fisco-bcos"
app   994     1  4 22:57 tty1     00:00:00 /home/app/generator_agency_A/nodes/node_127.0.0.1_30300/fisco-bcos -c config.ini
app   995     1  4 22:57 tty1     00:00:00 /home/app/generator_agency_A/nodes/node_127.0.0.1_30301/fisco-bcos -c config.ini
```

**机构B也采用相同操作生成并启动节点**

``` shell
$ cd ~/generator_agency_B/

# 生成peers文件
$ cat > ./conf/peers_group1.txt << EOF
127.0.0.1:30300
127.0.0.1:30301
EOF

# 检查依赖文件
$ ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在

# 生成节点
$ ./generator --build_install_package ./conf/peers_group1.txt ./nodes

# 启动节点
$ cd ./nodes
$ bash start_all.sh

# 检查进程
$ ps -ef |grep "fisco-bcos"
```



### 三、新增群组：在已有区块链上新增群组2

根据示例的节点组网拓扑结构，新增群组。在已有群组1的区块链上，新增群组2。群组2包含机构A和机构C的所有节点。

#### 1. 证书颁发机构操作：颁发新机构证书

在证书颁发机构上进行操作，与生成机构A、B的步骤相同

```shell
$ cd ~/generator_agency_cert/
# 生成证书
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyC
# 发送正在
$ cp ./dir_chain_ca/ca.crt ~/generator_agency_C/meta                # 链证书
$ cp ./dir_agency_ca/agencyC/agency.crt ~/generator_agency_C/meta   # 机构证书
$ cp ./dir_agency_ca/agencyC/agency.key ~/generator_agency_C/meta   # 机构私钥
```

#### 2. 新机构独立操作：生成节点证书

与生成机构A和B的步骤相同

``` shell
$ cd ~/generator_agency_C/
# 检查证书
$ ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在
$ cat ./conf/node_deployment.ini # 检查已经配置了需要生成的节点（本例中生成证书步骤时已配置好）
# 编写配置
$ cat > ./conf/node_deployment.ini << EOF
[group]
group_id=2

[node0]
; host ip for the communication among peers.
; Please use your ssh login ip.
p2p_ip=127.0.0.1
; listen ip for the communication between sdk clients.
; This ip is the same as p2p_ip for physical host.
; But for virtual host e.g. vps servers, it is usually different from p2p_ip.
; You can check accessible addresses of your network card.
; Please see https://tecadmin.net/check-ip-address-ubuntu-18-04-desktop/
; for more instructions.
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
EOF
# 生成证书
$ ./generator --generate_all_certificates ./nodes_cert
```

#### 3. 选取一个机构进行操作：为群组生成创世块

任意选取一个机构操作即可，此处以选取机构C为例。

``` shell
$ cd ~/generator_agency_C/
```

##### 收集群组内所有节点证书

根据搭链拓扑结构，收集群组1中包含的4个节点的证书

```shell
$ cp ~/generator_agency_A/nodes_cert/cert_127.0.0.1_30300.crt ./meta/
$ cp ~/generator_agency_A/nodes_cert/cert_127.0.0.1_30301.crt ./meta/
$ cp ~/generator_agency_C/nodes_cert/cert_127.0.0.1_30304.crt ./meta/
$ cp ~/generator_agency_C/nodes_cert/cert_127.0.0.1_30305.crt ./meta/
```

##### 为群组生成创世块文件

修改创世块文件配置`group_genesis.ini`，配置项可参考[手册](../enterprise_tools/config.html#group-genesis-ini)。

```shell
$ cat > ./conf/group_genesis.ini << EOF
[group]
group_id=2

[nodes]
node0=127.0.0.1:30300
node1=127.0.0.1:30301
node2=127.0.0.1:30304
node3=127.0.0.1:30305
EOF
```

用[--create_group_genesis](../enterprise_tools/operation.html#create-group-genesis-c)命令，生成创世块文件，

```shell
# ./generator --create_group_genesis 创世块文件生成目录
$ ./generator --create_group_genesis ./group2
```

查看生成的文件

```shell
$ ls ./group2
group.2.genesis # 自动命名规则：group_id为n命名为 group.n.genesis
```

##### 分发创世块文件

将创世块文件分发给**所有**属于此组的机构，放到meta下

```shell
$ cp ./group2/group.2.genesis ~/generator_agency_A/meta/
$ cp ./group2/group.2.genesis ~/generator_agency_C/meta/
```

#### 4. 新机构独立操作：生成节点

新机构是机构C，在机构C上操作，操作步骤与机构A、B生成节点的步骤相同

``` shell
$ cd  ~/generator_agency_C/
```

##### 汇总所有组员节点的URL

按照网络拓扑，汇总**所有组员**节点**P2P端口**的URL，排除自己，生成peers文件，组2的配置与组1有不同

``` shell
$ cat > ./conf/peers_group2.txt << EOF
127.0.0.1:30300
127.0.0.1:30301
EOF
```

##### 生成节点

检查依赖的文件

```shell
$ ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在
$ cat ./conf/node_deployment.ini # 检查已经配置了需要生成的节点（本例中生成证书步骤时已配置好）
```

用[`--build_install_package`](../enterprise_toolhtml#peration.md#build-install-package-b)命令，生成节点

```shell
# ./generator --build_install_package 提供的peers文件 生成的节点
$ ./generator --build_install_package ./conf/peers_group2.txt ./nodes
```

##### 启动节点

```shell
$ cd ./nodes
$ bash start_all.sh
node_127.0.0.1_30304 start successfully # 节点4成功启动
node_127.0.0.1_30305 start successfully # 节点5成功启动
```

#### 5. 已有机构操作：配置新群组

新增的群组包含了已有的机构A，在机构A上进行操作

``` shell
$ cd  ~/generator_agency_A/
```

##### 汇总所有组员节点的URL

按照网络拓扑，汇总**所有组员**节点**P2P端口**的URL，排除自己，生成peers文件

``` shell
$ cat > ./conf/peers_group2.txt << EOF
127.0.0.1:30304
127.0.0.1:30305
EOF
```

##### 配置新群组

用[`--add_group`](../enterprise_tohtml#/operation.md#add-group-a)命令将创世块文件导入已生成的节点目录中

``` shell
$ ./generator --add_group ./meta/group.2.genesis  ./nodes
```

查看导入的文件

``` shell
$ ls nodes/node_*/conf/
nodes/node_127.0.0.1_30300/conf/:
group.2.ini  group.2.genesis  # 生成了此2个文件

nodes/node_127.0.0.1_30301/conf/:
group.2.ini  group.2.genesis  # 生成了此2个文件
```

用[`--add_peers`](../enterprise_html#ls/operation.md#add-peers-p)命令修改所有需要新增群组的节点配置

``` shell
$ ./generator --add_peers ./conf/peers_group2.txt ./nodes
```

##### 重启节点

``` shell
$ cd ./nodes
$ bash stop_all.sh
$ bash start_all.sh
```

验证新群组已配置成功，周期性的打印出组2（g:2）的打包信息

``` shell
$ tail -f node_127.0.0.1_30300/log/* |grep "g:2" |grep ++++
info|2019-05-12 23:06:16.053115| [g:2][p:131080][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=c6679ca5...
info|2019-05-12 23:06:20.092477| [g:2][p:131080][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=3e475c70...
```

至此，我们完成了如图所示的机构A、C搭建群组2构建

![](../../images/enterprise/tutorial_step_2.png)

