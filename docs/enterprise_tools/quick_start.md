# 教程示例

在本节中，我们将在本机IP为`127.0.0.1`生成一个如图所示网络拓扑结构为2群组6节点的组网模式，每个节点的ip，端口号分别为：

![](../../images/enterprise/simple1.png)

group1 :

| 节点序号 | P2P IP | RPC IP | P2P Port | Channel Port | RPC Port |
| :-: | :-: | :-: | :-: | :-: | :-: |
| node0 | 127.0.0.1 | 127.0.0.1 | 30300 | 20200 | 8545 |
| node1 | 127.0.0.1 | 127.0.0.1 | 30301 | 20201 | 8546 |
| node2 | 127.0.0.1 | 127.0.0.1 | 30302 | 20202 | 8547 |
| node3 | 127.0.0.1 | 127.0.0.1 | 30303 | 20203 | 8548 |
| node4 | 127.0.0.1 | 127.0.0.1 | 30304 | 20204 | 8549 |
| node5 | 127.0.0.1 | 127.0.0.1 | 30305 | 20205 | 8550 |

group2 :

| 节点序号 | P2P IP | RPC IP | P2P Port | Channel Port | RPC Port |
| :-: | :-: | :-: | :-: | :-: | :-: |
| node0 | 127.0.0.1 | 127.0.0.1 | 30300 | 20200 | 8545 |
| node1 | 127.0.0.1 | 127.0.0.1 | 30301 | 20201 | 8546 |
| node2 | 127.0.0.1 | 127.0.0.1 | 30302 | 20202 | 8547 |
| node3 | 127.0.0.1 | 127.0.0.1 | 30303 | 20203 | 8548 |

配置文件中字段的含义解释如下：

| | |
| :-: | :-: |
|节点序号 | 节点在配置文件中的序号|
| P2P IP | 节点之间p2p通信ip |
| RPC IP | 节点与sdk通信ip |
| P2P Port | 节点之间p2p通信端口 |
| Channel Port | sdk与节点通信端口 |
| RPC Port | 节点rpc端口 |

假设如图所示，联盟链中共有2个群组，4个节点。

group1中有6个节点，节点序号为0、1、2、3，之后扩容节点4、5。

group2中有3个节点，节点序号为0、1、2、3。


组网步骤如下：

## 安装generator

```s
$ git clone https://github.com/FISCO-BCOS/generator.git
$ cd generator
$ bash ./scripts/install.sh
$ ./generator -h
```

## 导入fisco-bcos二进制文件

工具启动时需要导入可用的fisco-bcos二进制文件，以从官网下载为例(mac用户需自己[手动编译](../../manual/install.md))，操作如下

```s
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh) -o ./meta
$ ./meta/fisco-bcos -v
```

## 构建第一个群组group1

1. 修改mchain.ini中的配置项，使其指向对应节点的ip，端口号，指定组id为group1

```s
$ vim ./conf/mchain.ini
```
修改为

```ini
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

[node2]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547

[node3]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548

[group]
group_id=1
```

2. 生成节点序号为0、1、2、3的证书和私钥，并导入meta文件夹

```eval_rst
.. important::
    实际应用中，证书和私钥应该由用户自己生成，只需将证书导入工具。
    本示例中生成的证书机构名默认为agency_fisco，根证书私钥和机构证书私钥默认放置在meta文件夹下，节点证书和私钥放置在用户指定目录下，本例中为./mycert
```

```s
$ ./generator --certbuild ./mycert
```

3. 使用build命令，在data下生成group1节点安装包

```s
$ ./generator --build ./data
```

执行成功后在./data目录下可以看到

```s
.
|-- config.ini
|-- group.1.genesis
|-- group.1.ini
|-- monitor
|-- node_127.0.0.1_30300
|-- node_127.0.0.1_30301
|-- node_127.0.0.1_30302
|-- node_127.0.0.1_30303
|-- start_all.sh
`-- stop_all.sh
```

|-- node_127.0.0.1_30300
|-- node_127.0.0.1_30301
|-- node_127.0.0.1_30302
|-- node_127.0.0.1_30303

即为group1的节点安装包

4. 导入节点私钥到对应节点安装包的conf文件夹下

上述3.中生成的安装包是不含节点私钥的，需要导入2.中的节点私钥，命令如下

```s
$ ./generator --deploykey ./mycert ./data
```

5. 启动节点

导入私钥后即可启动节点

```s
cd ./data
$ ./start_all.sh
```

查看节点进程

```s
$ ps -ef | grep fisco
# 可以看到如下所示的三个进程
fisco  15347     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30300/fisco-bcos -c config.ini
fisco  15402     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30301/fisco-bcos -c config.ini
fisco  15457     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30302/fisco-bcos -c config.ini
fisco  15498     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30303/fisco-bcos -c config.ini
```

查看节点log

```s
$ tail -f data/node*/log/log*  | grep +++
info|2019-02-25 17:25:56.028692| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=833bd983...
info|2019-02-25 17:25:59.058625| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=343b1141...
info|2019-02-25 17:25:57.038284| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=1,hash=ea85c27b...
```

至此 我们完成了如图所示构建group1的操作。

![](../../images/enterprise/simple1.png)

## 为group1扩容两个节点

```s
# 回到上级目录
cd ..
# 生成扩容节点所需证书和私钥
./generator --certexpand ./myexpandcert
# 拷贝扩容所需文件到同一文件夹
cp ./meta/fisco-bcos ./data
# 生成扩容安装包
./generator --expand ./data ./expand
# 导入私钥至扩容安装包
./generator --deploykey ./myexpandcert ./expand
# 启动节点
cd ./expand
./start_all.sh
# 查看节点进程
ps aux| grep fisco-bcos |grep -v grep
# 返回上级目录
cd ..
```

可以看到现在一共有六个fisco bcos进程存在，但扩容了两个节点尚未经过group1中的节点共识

```eval_rst
.. important::
    生成扩容安装包时需要fisco-bcos可执行文件、group.1.genesis和group.1.ini
```

## 构建第二个群组group2

[构建group1]的操作中，我们已经生成了一条具有6个节点，处于group1中的联盟链，接下来将生成有4个节点的group2。

![](../../images/enterprise/simple2.png)

1. 修改mchain.ini中的配置项，使其指向对应节点的ip，端口号，指定组id为group2

```s
$ vim ./conf/mgroup.ini
```
修改为

```ini
[group]
group_id=2

[member]
member0=127.0.0.1:30300
member1=127.0.0.1:30301
member2=127.0.0.1:30302
member3=127.0.0.1:30303
```

操作步骤如下：

```s
# 生成group2群组配置文件
./generator --create ./data
cd ./data
# 拷贝群组配置至节点文件夹
cp group.2.ini group.2.genesis ./node_127.0.0.1_30300/conf/
cp group.2.ini group.2.genesis ./node_127.0.0.1_30301/conf/
cp group.2.ini group.2.genesis ./node_127.0.0.1_30302/conf/
cp group.2.ini group.2.genesis ./node_127.0.0.1_30303/conf/
# 重启节点
./stop_all.sh
./start_all.sh
```

此时，可以看到查看节点进程已经从新启动

```s
$ ps -ef | grep fisco
# 可以看到如下所示的四个进程
fisco  16356     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30300/fisco-bcos -c config.ini
fisco  16422     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30301/fisco-bcos -c config.ini
fisco  16467     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30302/fisco-bcos -c config.ini
fisco  16489     1  0 17:22 pts/2    00:00:00 ~/generator/data/node_127.0.0.1_30303/fisco-bcos -c config.ini
```

查看节点log

```s
$ tail -f data/node*/log/log*  | grep +++
info|2019-02-25 17:25:56.028692| [g:2][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=833bd983...
info|2019-02-25 17:25:59.058625| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=0,hash=343b1141...
info|2019-02-25 17:25:57.038284| [g:2][p:264][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,myIdx=1,hash=ea85c27b...
```

至此 我们完成了所示构建group2的操作。

通过上述命令，我们在本机生成一个网络拓扑结构为2群组6节点的多群组架构联盟链。