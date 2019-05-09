# 使用企业级部署工具

FISCO BCOS企业级部署工具面向于真实的多机构生产环境。为了保证机构的秘钥安全，企业级部署工具提供了一种机构间相互合作的的搭链方式。

企业级部署工具与[```build_chain.sh```](group_use_cases.md)不同。``` build_chain.sh ```面向于初学者和开发者。为了简单，``` build_chain.sh ```牺牲了实际生产环境中需要的安全性和功能的全面性。

本章以部署6节点3机构2群组的组网模式，演示企业级部署工具的使用方法。具体使用说明，请参考：[FISCO BCOS企业级部署工具](../enterprise_tools/index.md)。

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

![](D:/out-branch/FISCO-BCOS-DOC/images/enterprise/tutorial_step_2.png)

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

- 证书颁发机构
- 搭建节点的机构（简称“机构”）

### 关键流程

本流程简要的给出**证书颁发机构**，节点机构间如何相互配合搭建区块链。

**一、初始化链证书**

**二、生成群组1（Group 1）**

1. 证书**颁发机构**操作：**颁发机构证书**
   - 生成机构证书
   - 发送证书
2. **机构**间独立操作：**生成节点证书**
3.  选取**一个机构**进行操作：为群组**生成创世块**
   - 收集群组内所有节点证书
   - 为群组生成创世块文件
   - 分发创世块文件
4. **机构**间独立操作：**生成节点**
   - 汇总所有组员节点的URL
   - 生成节点数据包
   - 分发节点数据包至对应生产环境的机器
   - 生产机器启动节点

## 具体流程

为了操作简洁，本示例所有操作在同一台机器上进行，用不同的目录区分不同的机构环境。每个机构目录都是一个generator。进行了“[下载安装]()”后，复制多个generator作为多个机构的generator。

```shell
cp -r ~/generator ~/generator_agency_cert # 证书颁发机构
cp -r ~/generator ~/generator_agency_A    # 机构 A
cp -r ~/generator ~/generator_agency_B    # 机构 B
cp -r ~/generator ~/generator_agency_C    # 机构 C
```

### 初始化链证书

在证书颁发机构上进行操作

用 XXX 命令生成链证书

```shell
$ cd ~/generator_agency_cert/
$ ./generator --generate_chain_certificate ./dir_chain_ca #生成到dir_chain_ca目录下
```

查看链证书

```shell
$ ls ./dir_chain_ca
ca.crt  ca.key   cert.cnf #链证书、链私钥、证书配置文件
```

### 生成群组1（Group 1）

#### 1. 证书颁发机构操作：颁发机构证书

在证书颁发机构上进行操作

```shell
$ cd ~/generator_agency_cert/
```

##### 生成机构证书

生成和机构A证书

```shell
# ./generator --generate_agency_certificate 机构证书生成目录 用到的链证书目录 本机构名称
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyA
```

机构证书生成至`dir_agency_ca/agencyA`中

```shell
ls ./dir_agency_ca/agencyA/
agency.crt  agency.key  ca-agency.crt  ca.crt  cert.cnf # 机构证书、机构私钥、链证书签发机构证书中间文件、链证书、证书配置文件
```

##### 发送证书

发送证书至A机构的generator，放到meta目录中，包括以下三个文件。

```shell
cp ./dir_chain_ca/ca.crt ~/generator_agency_A/meta                # 链证书
cp ./dir_agency_ca/agencyA/agency.crt ~/generator_agency_A/meta   # 机构证书
cp ./dir_agency_ca/agencyA/agency.key ~/generator_agency_A/meta   # 机构私钥
```

对机构B也采用相同的操作

```shell
$ ./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyB
cp ./dir_chain_ca/ca.crt ~/generator_agency_B/meta                # 链证书
cp ./dir_agency_ca/agencyB/agency.crt ~/generator_agency_B/meta   # 机构证书
cp ./dir_agency_ca/agencyB/agency.key ~/generator_agency_B/meta   # 机构私钥
```

#### 2. 机构间独立操作：生成节点证书

每个机构独立的在自己的机器上操作，此处以机构A为例，机构B的操作与机构A相同。

```shell
$ cd ~/generator_agency_A/
```

##### 检查机构证书是否存在

```shell
$ ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在
```

##### 生成节点证书

编写机构节点配置文件`node_deployment.ini`，将本机构的所有节点配置入文件中。配置文件格式请参考手册XXX

```shell
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

```shell
# ./generator --generate_all_certificates 生成的目录
$ ./generator --generate_all_certificates ./nodes_cert
```

查看生成的文件

```shell
ls ./nodes_cert
cert_127.0.0.1_30300.crt  cert_127.0.0.1_30301.crt  peers.txt # 节点0证书 节点1证书 节点P2P连接URL列表
```

机构B的操作与机构A相同

```shell
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

```shell
$ cd ~/generator_agency_A/
```

##### 收集群组内所有节点证书

根据搭链拓扑结构，收集群组1中包含的4个节点的证书

```shell
$ cp ~/generator_agency_A/nodes_cert/cert_127.0.0.1_30300.crt ./meta/
$ cp ~/generator_agency_A/nodes_cert/cert_127.0.0.1_30301.crt ./meta/
$ cp ~/generator_agency_B/nodes_cert/cert_127.0.0.1_30302.crt ./meta/
$ cp ~/generator_agency_B/nodes_cert/cert_127.0.0.1_30303.crt ./meta/
```

##### 为群组生成创世块文件

修改创世块文件配置`group_genesis.ini`，配置项可参考XXX

```shell
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

用XXX命令，生成创世块文件，

```shell
# ./generator --create_group_genesis 创世块文件生成目录
$ ./generator --create_group_genesis ./group1
```

查看生成的文件

```shell
ls ./group1
group.1.genesis # 自动命名规则：group_id为n命名为 group.n.genesis
```

##### 分发创世块文件

将创世块文件分发给**所有**属于此组的机构，放到meta下

```shell
$ cp ./group1/group.1.genesis ~/generator_agency_A/meta/
$ cp ./group1/group.1.genesis ~/generator_agency_B/meta/
```

#### 4. 机构间独立操作：生成节点

机构A的操作

```shell
$ cd ~/generator_agency_A/
```

##### 汇总所有组员节点的URL

按照网络拓扑，汇总**所有组员**节点**P2P端口**的URL，生成peers.txt文件

```shell
$ cat > ./meta/peers.txt << EOF
127.0.0.1:30300
127.0.0.1:30301
127.0.0.1:30302
127.0.0.1:30303
EOF
```

##### 生成节点数据包

检查依赖的文件

```shell
ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在
```

用XXX命令，生成节点数据包

```shell
# ./generator --build_install_package 提供的peers.txt文件 生成的节点数据包
$ ./generator --build_install_package ./meta/peers.txt ./nodes
```

查看生成的节点数据包

```shell
$ tree ./nodes -L 1
nodes
├── monitor
├── node_127.0.0.1_30300 # 节点 0 数据包
├── node_127.0.0.1_30301 # 节点 1 数据包
├── scripts
├── start_all.sh
└── stop_all.sh
```

##### 分发节点数据包至对应生产环境的机器

```shell
$ mkdir -p ~/fisco # 假设生产环境机器的目录是 ~fisco
$ cp -r nodes/node_127.0.0.1_30300 ~/fisco/
$ cp -r nodes/node_127.0.0.1_30301 ~/fisco/
```

##### 生产机器启动节点

```shell
$ cd ~/fisco/node_127.0.0.1_30300/
$ bash start.sh
node_127.0.0.1_30300 start successfully # 节点0成功启动
$ cd ~/fisco/node_127.0.0.1_30301/
$ bash start.sh
node_127.0.0.1_30300 start successfully # 节点1成功启动
```

检查节点进程

```shell
$ ps -ef |grep "fisco-bcos"
jimmyshi 29966     1  0 22:04 tty1     00:00:00 /home/jimmyshi/fisco/node_127.0.0.1_30300/fisco-bcos -c config.ini
jimmyshi 30031     1  0 22:06 tty1     00:00:00 /home/jimmyshi/fisco/node_127.0.0.1_30301/fisco-bcos -c config.ini
```

**机构B也采用相同操作生成并启动节点**

```shell
$ cd ~/generator_agency_B/

# 生成peers.txt，相同的组有相同的peers.txt
$ cat > ./meta/peers.txt << EOF
127.0.0.1:30300
127.0.0.1:30301
127.0.0.1:30302
127.0.0.1:30303
EOF

# 检查依赖文件
ls ./meta/
agency.crt agency.key ca.crt # 此三个文件必须存在

# 生成节点数据包
$ ./generator --build_install_package ./meta/peers.txt ./nodes

#  分发节点数据包至对应生产环境的机器
$ mkdir -p ~/fisco # 假设生产环境机器的目录是 ~fisco
$ cp -r nodes/node_127.0.0.1_30302 ~/fisco/
$ cp -r nodes/node_127.0.0.1_30303 ~/fisco/

# 启动节点2 
$ cd ~/fisco/node_127.0.0.1_30302/
$ bash start.sh
# 启动节点3
$ cd ~/fisco/node_127.0.0.1_30303/
$ bash start.sh

# 检查进程
$ ps -ef |grep "fisco-bcos"
```



(未完待续)