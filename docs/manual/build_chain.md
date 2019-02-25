# 建链脚本

```eval_rst
.. important::
    
    build_chain脚本目标是让用户最快的使用FISCO BCOS，对于企业级应用部署FISCO BCOS请参考 `企业工具 <../enterprise/index.html>`_ 。
```

## 脚本功能简介

- `build_chain`脚本用于快速生成一条链中节点的配置文件，脚本依赖于`openssl`请根据自己的操作系统安装`openssl 1.0.2`以上版本。脚本的源码位于[FISCO-BCOS/tools/build_chain.sh][build_chain]。
- 快速体验可以使用`-l`选项指定节点IP和数目。`-f`选项通过使用一个指定格式的配置文件，提供了创建更加复杂的链的功能。**`-l`和`-f`选项必须指定一个且不可共存**。
- 建议测试时使用`-T`和`-i`选项，`-T`开启log级别到DEBUG，`-i`设置RPC和channel监听`0.0.0.0`。p2p模块默认监听`0.0.0.0`。

## 帮助

```bash
Usage:
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -f <IP list file>                   [Optional] split by line, every line should be "ip:nodeNum agencyName groupList". eg "127.0.0.1:4 agency1 1,2"
    -e <FISCO-BCOS binary path>         Default download fisco-bcos from GitHub. If set -e, use the binary at the specified location
    -o <Output Dir>                     Default ./nodes/
    -p <Start Port>                     Default 30300,20200,8545 means p2p_port start from 30300, channel_port from 20200, jsonrpc_port from 8545
    -i <Host ip>                        Default 127.0.0.1. If set -i, listen 0.0.0.0
    -c <Consensus Algorithm>            Default PBFT. If set -c, use Raft
    -s <State type>                     Default storage. if set -s, use mpt 
    -g <Generate guomi nodes>           Default no
    -z <Generate tar packet>            Default no
    -t <Cert config file>               Default auto generate
    -T <Enable debug log>               Default off. If set -T, enable debug log
    -h Help
e.g
    build_chain.sh -l "127.0.0.1:4"
```

## 选项介绍

- **`l`选项:** 
用于指定要生成的链的IP列表以及每个IP下的节点数，以逗号分隔。脚本根据输入的参数生成对应的节点配置文件，其中每个节点的端口号默认从30300开始递增，所有节点属于同一个机构和Group。

- **`f`选项** 
    + 用于根据配置文件生成节点，相比于`l`选项支持更多的定制。
    + 按行分割，每一行表示一个服务器，格式为`IP:NUM AgencyName GroupList`，每行内的项使用空格分割，不可有空行。
    + `IP:NUM`表示机器的IP地址以及该机器上的节点数。`AgencyName`表示机构名，用于指定使用的机构证书。`GroupList`表示该行生成的节点所属的组，以`,`分割。例如`192.168.0.1:2 agency1 1,2`表示`ip`为`192.168.0.1`的机器上有两个节点，这两个节点属于机构`agency1`，属于group1和group2。

下面是一个配置文件的例子，每个配置项以空格分隔，其中`GroupList`表示该服务器所属的组。

```bash
192.168.0.1:2 agency1 1,2
192.168.0.1:2 agency1 1,3
192.168.0.2:3 agency2 1
192.168.0.3:5 agency3 2,3
192.168.0.4:2 agency2 3
```
**假设上述文件名为`ipconf`**，则使用下列命令建链
```bash
bash build_chain.sh -f ipconf -T -i
```

- **`e`选项[**Optional**]**
用于指定`fisco-bcos`二进制所在的**完整路径**，脚本会将`fisco-bcos`拷贝以IP为名的目录下。不指定时，默认从GitHub下载`master`分支最新的二进制程序。

- **`o`选项[**Optional**]**
指定生成的配置所在的目录。

- **`p`选项[**Optional**]**
指定节点的起始端口，每个节点占用三个端口，分别是p2p,channel,jsonrpc使用`,`分割端口，必须指定三个端口。同一个IP下的不同节点所使用端口从起始端口递增。
例如`build_chain -l 127.0.0.1:2 -p 30300,20200,8545`，那么两个节点分别占用`30300,20200,8545`和`30301,20201,8546`。

- **`i`选项[**Optional**]**
无参数选项，设置该选项时，设置节点的RPC和channel监听`0.0.0.0`

- **`c`选项[**Optional**]**
无参数选项，设置该选项时，设置节点的共识算法为[Raft](../design/consensus/raft.md)，默认设置为[PBFT](../design/consensus/pbft.md)。

- **`s`选项[**Optional**]**
无参数选项，设置该选项时，节点使用[mptstate](../design/storage/mpt.md)存储合约局部变量，默认使用[storagestate](../design/storage/storage.md)存储合约局部变量。

- **`g`选项[**Optional**]**
无参数选项，设置该选项时，编译[国密版本](guomi.md)。<font color=#FF0000>使用`g`选项时要求二进制fisoc-bcos为国密版本。</font>

- **`z`选项[**Optional**]**
无参数选项，设置该选项时，生成节点的tar包。

- **`t`选项[**Optional**]**
该选项用于指定生成证书时的证书配置文件。

- **`T`选项[**Optional**]**
无参数选项，设置该选项时，设置节点的log级别为DEBUG。log相关配置[参考这里](log.md)。

## 节点文件组织结构

- cert文件夹下存放链的根证书和机构证书。
- 以IP命名的文件夹下存储该服务器所有节点相关配置、`fisco-bcos`可执行文件、sdk所需的证书文件。
- 每个IP文件夹下的`node*`文件夹下存储节点所需的配置文件。其中`config.ini`为节点的主配置，`conf`目录下存储证书文件和群组相关配置。配置文件详情，请[参考这里](configs.md)。每个节点中还提供两个脚本，用于启动和停止节点。
- 每个IP文件夹下的提供`start_all.sh`和`stop_all.sh`两个脚本用于启动和停止所有节点。

```bash
nodes/
├── 127.0.0.1
│   ├── fisco-bcos
│   ├── node0
│   │   ├── conf
│   │   │   ├── ca.crt
│   │   │   ├── group.1.ini
│   │   │   ├── node.crt
│   │   │   ├── node.key
│   │   │   ├── node.nodeid
│   │   ├── config.ini
│   │   ├── start.sh
│   │   └── stop.sh
│   ├── node1
│   │.....
│   ├── node2
│   │.....
│   ├── node3
│   │.....
│   ├── sdk
│   │   ├── ca.crt
│   │   ├── node.crt
│   │   └── node.key
├── cert
│   ├── agency
│   │   ├── agency.crt
│   │   ├── agency.key
│   │   ├── agency.srl
│   │   ├── ca-agency.crt
│   │   ├── ca.crt
│   │   └── cert.cnf
│   ├── ca.crt
│   ├── ca.key
│   ├── ca.srl
│   └── cert.cnf
└── replace_all.sh
```

## 使用举例

### 单服务器单群组

构建本机上4节点的FISCO BCOS联盟链，使用默认起始端口`30300,20200,8545`（4个节点会占用`30300-30303`,`20200-20203`,`8545-8548`），监听外网`Channel`和`jsonrpc`端口允许外网通过SDK或API与节点交互。

```bash
# 下载最新预编译二进制
bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh) -b release-2.0.1
# 构建FISCO-BCOS联盟链
$ bash build_chain.sh -e bin/fisco-bcos -l "127.0.0.1:4" -i
# 生成成功后，输出`All completed`提示
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 0.0.0.0
[INFO] Output Dir        : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
[INFO] CA Key Path       : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
```

### 多服务器多群组

使用build_chain脚本构建多服务器多群组的FISCO BCOS联盟链需要借助脚本配置文件，详细使用方式可以参考[多群组使用案例](group.md)。

[build_chain]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/tools/build_chain.sh
