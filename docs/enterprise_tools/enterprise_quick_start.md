# 一键部署

本章主要以部署**2机构1群组4节点**的组网模式，为用户讲解单机构一键部署企业级部署工具的使用方法。企业级部署工具多机构对等部署教程可以参考[使用企业级部署工具](../tutorial/enterprise_quick_start.md)。

## 下载安装

**下载**

```bash
cd ~/ && git clone https://github.com/FISCO-BCOS/generator.git && cd ./generator
```

## 示例分析

在本节中，我们将在本机IP为`127.0.0.1`生成一个如图所示网络拓扑结构为2机构1群组4节点的组网模式，每个节点的ip，端口号分别为：

| 机构  | 节点  | 所属群组  | P2P地址           | RPC/channel监听地址       |
| --- | --- | ----- | --------------- | --------------------- |
| 机构A | 节点0 | 群组1、2 | 127.0.0.1:30300 | 127.0.0.1:8545/:20200 |
|     | 节点1 | 群组1、2 | 127.0.0.1:30301 | 127.0.0.1:8546/:20201 |
| 机构B | 节点2 | 群组1   | 127.0.0.1:30302 | 127.0.0.1:8547/:20202 |
|     | 节点3 | 群组1   | 27.0.0.1:30303  | 127.0.0.1:8548/:20203 |

```eval_rst
.. important::

    针对云服务器中的vps服务器，RPC监听地址需要写网卡中的真实地址(如内网地址或127.0.0.1)，可能与用户登录的ssh服务器不一致。
```

## 使用说明

使用前用户需准备如图如`one_click`的文件夹，在文件夹下分别拥有不同机构的目录，每个机构目录下需要有对应的配置文件[```node_deployment.ini```](../enterprise_tools/config.md#node-deployment-ini)。使用前需要保证generator的meta文件夹没有进行过任何操作。

查看一键部署模板文件夹：

```bash
ls ./one_click
```

```bash
# 参数解释
# 如需多个机构，需要手动创建该文件夹
one_click # 用户指定进行一键部署操作的文件夹
├── agencyA # 机构A目录，命令执行后会在该目录下生成机构A的节点及相关文件
│   └── node_deployment.ini # 机构A节点配置文件，一键部署命令会根据该文件生成相应节点
└── agencyB # 机构B目录，命令执行后会在该目录下生成机构B的节点及相关文件
    └── node_deployment.ini # 机构B节点配置文件，一键部署命令会根据该文件生成相应节点
```

## 机构填写节点信息

教程中将配置文件放置与one_click文件夹下的agencyA, agencyB下

```bash
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

## 搭建节点

```bash
bash ./one_click_generator.sh ./one_click
```

执行完毕后，./one_click文件夹结构如下：

查看执行后的一键部署模板文件夹：

``bash
ls ./one_click
``

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

调用脚本启动节点：

```bash
bash ./one_click/agencyA/node/start_all.sh
```

```bash
bash ./one_click/agencyB/node/start_all.sh
```

查看节点进程：

```bash
ps -ef | grep fisco
```

```bash
# 命令解释
# 可以看到如下进程
fisco  15347     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30300/fisco-bcos -c config.ini
fisco  15402     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30301/fisco-bcos -c config.ini
fisco  15442     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30302/fisco-bcos -c config.ini
fisco  15456     1  0 17:22 pts/2    00:00:00 ~/generator/one_click/agencyA/node/node_127.0.0.1_30303/fisco-bcos -c config.ini
```

## 查看节点运行状态

查看节点log：

```bash
tail -f ~/generator/one_click/agency*/node/node*/log/log*  | grep +++
```

```bash
# 命令解释
# +++即为节点正常共识
info|2019-02-25 17:25:56.028692| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=833bd983...
info|2019-02-25 17:25:59.058625| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=343b1141...
info|2019-02-25 17:25:57.038284| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=1,hash=ea85c27b...
```

## 使用控制台

由于控制台体积较大，一键部署中没有直接集成，用户可以使用以下命令获取控制台

获取控制台，可能需要较长时间：

```bash
./generator --download_console ./
```

配置A机构控制台:

拷贝控制台至对应机构A：

```bash
cp -rf ./console ./one_click/agencyA/console
```

配置机构A控制台对应文件，控制台启动时需要相关证书、私钥以及控制台配置文件：

```bash
cp ./one_click/agencyA/sdk/* ./one_click/agencyA/console/conf
```

启动控制台，需要安装java：

```bash
cd ./one_click/agencyA/console && bash ./start.sh 1
```

同理，配置B机构控制台:

```bash
cd ~/generator && cp -rf ./console ./one_click/agencyB/console
```

配置机构B控制台对应文件，控制台启动时需要相关证书、私钥以及控制台配置文件：

```bash
cp ./one_click/agencyB/sdk/* ./one_click/agencyB/console/conf
```

## 新建群组及扩容

企业部署工具的后续操作与[企业部署工具教程](../tutorial/enterprise_quick_start.md)相同。

后续节点的扩容及新群组的划分操作，可以参考[操作手册](./operation.md)，或[企业工具对等部署教程](../tutorial/enterprise_quick_start.md)。

新建群组的操作用户可以在执行`click2start.sh`脚本的目录下，通过修改./conf/group_genesis.ini文件，并执行create_group_genesis命令。

扩容新节点的操作可以通过修改./conf/node_deployment.ini文件，先使用generate_all_certificates生成证书，再使用build_install_package生成节点。

如果使用该教程遇到问题，请查看[FAQ](../faq.md)
