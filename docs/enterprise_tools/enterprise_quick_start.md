# 一键部署教程

本章主要以部署2机构1群组4节点的组网模式，为用户讲解单机构一键部署企业级部署工具的使用方法。


## 安装准备

```bash
$ cd ~/
$ git clone https://github.com/FISCO-BCOS/generator.git && cd generator
```

## 示例分析

在本节中，我们将在本机IP为`127.0.0.1`生成一个如图所示网络拓扑结构为2机构1群组4节点的组网模式，每个节点的ip，端口号分别为：


| 节点序号 |   P2P地址     |   RPC/channel监听地址     |   所属机构     | 所属群组 |
| :-----------: | :-------------: | :-------------: | :-------------: | :-------------: |
|   节点0     | 127.0.0.1:30300| 127.0.0.1:8545/:20200 | 机构A | 群组1 |
|   节点1     | 127.0.0.1:30301| 127.0.0.1:8546/:20201 | 机构A | 群组1 |
|   节点2     | 127.0.0.1:30302| 127.0.0.1:8547/:20202 | 机构B | 群组1 |
|   节点3     | 127.0.0.1:30303| 127.0.0.1:8548/:20203 | 机构B | 群组1 |


配置文件中字段的含义解释如下：

|              |                        |
| :----------: | :--------------------: |
|   节点序号   | 节点在配置文件中的序号 |
|    P2P监听地址    |   节点之间p2p通信地址    |
|    RPC监听地址    |    节点开启的RPC/channel监听地址     |

```eval_rst
.. important::

    针对云服务器中的vps服务器，RPC监听地址需要写网卡中的真实地址(如内网地址或127.0.0.1)，可能与用户登录的ssh服务器不一致。
```

## 机构填写节点信息

教程中将配置文件放置与one_click文件夹下的agencyA, agencyB下

```bash
# 机构A执行下述操作，教程中采用模板文件
cat > ./one_click/agencyAnode_deployment.ini << EOF
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

```bash
# 机构B执行下述操作，教程中采用模板文件
cat > ./one_click/agencyB/node_deployment.ini << EOF
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
```

如果一键部署时需要多个机构，可以通过创建如下所示的文件夹，在每个文件夹中放置不同的node_deployment.ini文件，执行一键部署脚本。

```bash
├── agencyA # 机构文件夹
│   ├── node_deployment.ini # 机构节点配置文件
└── agencyB
    ├── node_deployment.ini
└── agencyC # 如果需要多个机构，需要手动创建该文件夹
    ├── node_deployment.ini
```

## 搭建节点

```bash
bash ./one_click_generator.sh ./one_click
```

执行完毕后，./one_click文件夹结构如下：

```bash
├── agencyA # A机构文件夹
│   ├── agency_cert # A机构证书及私钥
│   ├── generator-agency # 自动代替A机构进行操作的generator文件夹
│   ├── node # A机构生成的节点，多机部署时推送至对应服务器即可
│   ├── node_deployment.ini # A机构的节点配置信息
│   └── sdk # A机构的sdk或控制台配置文件
└── agencyB
    ├── agency_cert
    ├── generator-agency
    ├── node
    ├── node_deployment.ini
    └── sdk
```

## 启动节点


```bash
# 启动节点
$ bash ./one_click/agencyA/node/start_all.sh
$ bash ./one_click/agencyB/node/start_all.sh
# 查看节点进程
$ ps -ef | grep fisco
fisco  15347     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30300/fisco-bcos -c config.ini
fisco  15402     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30301/fisco-bcos -c config.ini
fisco  15442     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30302/fisco-bcos -c config.ini
fisco  15456     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30303/fisco-bcos -c config.ini
```

## 查看节点运行状态

查看节点log：

```bash
$ tail -f ~/generator/one_click/agency*/node/node*/log/log*  | grep +++
# +++即为节点正常共识
info|2019-02-25 17:25:56.028692| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=833bd983...
info|2019-02-25 17:25:59.058625| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=343b1141...
info|2019-02-25 17:25:57.038284| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=1,hash=ea85c27b...
```

## 使用控制台

由于控制台体积较大，一键部署中没有直接集成，用户可以使用以下命令获取控制台

```bash
# 获取控制台
$ ./generator --download_console ./
# 配置A机构控制台
$ cp -rf ./console ./one_click/agencyA/console
$ cp ./one_click/agencyA/sdk/* ./one_click/agencyA/console/conf
$ cd ./one_click/agencyA/console
# 启动控制台前需要安装java
$ bash ./start.sh
# 同理，配置B机构控制台
$ cp -rf ./console ./one_click/agencyB/console
$ cp ./one_click/agencyB/sdk/* ./one_click/agencyB/console/conf
```

## 新建群组及扩容

企业部署工具的后续操作与[企业部署工具教程](../tutorial/enterprise_quick_start.md)相同。

后续节点的扩容及新群组的划分操作，可以参考[操作手册](./operation.md)，或[教程](../tutorial/enterprise_quick_start.md)。

新建群组的操作用户可以在执行`click2start.sh`脚本的目录下，通过修改./conf/group_genesis.ini文件，并执行create_group_genesis命令。

扩容新节点的操作可以通过修改./conf/node_deployment.ini文件，先使用generate_all_certificates生成证书，再使用build_install_package生成节点。

如果使用该教程遇到问题，请查看[FAQ](../faq.md)
