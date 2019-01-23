## 建链脚本

### 1. 脚本功能简介

- [`build_chain`][build_chain]脚本用于快速生成一条链中节点的配置文件，脚本依赖于`openssl`。
- 快速体验可以使用`-l`选项指定节点IP和数目。`-f`选项通过使用一个指定格式的配置文件，提供了创建更加复杂的链的功能。**`-l`和`-f`选项必须指定一个且不可共存**。
- 建议测试时使用`-T`和`-i`选项选项，`-T`开启log级别到DEBUG，`-i`设置RPC和channel监听`0.0.0.0`。p2p模块默认监听`0.0.0.0`。

### 2. help

```bash
Usage:
    -l <IP list>                        [Optional] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:2"
    -f <IP list file>                   [Required] split by line, every line should be "ip:nodeNum agencyName groupList". eg "127.0.0.1:4 agency1 1,2"
    -e <FISCO-BCOS binary path>         Default download from GitHub
    -o <Output Dir>                     Default ./nodes/
    -p <Start Port>                     Default 30300
    -i <Host ip>                        Default 127.0.0.1. If set -i, listen 0.0.0.0
    -c <Consensus Algorithm>            Default PBFT. If set -c, use raft
    -s <State type>                     Default mpt. if set -s, use storage
    -g <Generate guomi nodes>           Default no
    -z <Generate tar packet>            Default no
    -t <Cert config file>               Default auto generate
    -T <Enable debug log>               Default off. If set -T, enable debug log
    -P <PKCS12 passwd>                  Default generate PKCS12 file without passwd, use -P to set custom passwd
    -h Help
e.g
    build_chain.sh -l "127.0.0.1:4"
```

### 3. 选项介绍

- **`l`选项:** 
用于指定要生成的链的IP列表以及每个IP下的节点数，以逗号分隔。脚本根据输入的参数生成对应的节点配置文件，其中每个节点的端口号默认从30300开始递增，所有节点属于同一个机构和Group。

- **`f`选项** 
    + 用于根据配置文件生成节点，相比于`l`选项支持更多的定制。
    + 按行分割，每一行表示一个服务器，格式为`IP:NUM AgencyName GroupList`，每行内的项使用空格分割，不可有空行。
    + `IP:NUM`表示机器的IP地址以及该机器上的节点数。`AgencyName`表示机构名，用于指定使用的机构证书。`GroupList`表示该行生成的节点所属的组，以`,`分割。例如`192.168.0.1:2 agency1 1,2`表示`ip`为`192.168.0.1`的机器上有两个节点，这两个节点属于机构`agency1`，属于group1和group2。

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
用于指定`fisco-bcos`二进制所在的路径，脚本会将`fisco-bcos`拷贝以IP为名的目录下。不指定时，默认从GitHub下载最新的二进制程序。

- **`o`选项[**Optional**]**
指定生成的配置所在的目录。

- **`p`选项[**Optional**]**
指定节点的起始端口，每个节点占用三个端口，分别是p2p,channel,jsonrpc。同一个IP下的不同节点所使用端口累加递增。

- **`i`选项[**Optional**]**
无参数选项，设置该选项时，设置节点的RPC和channel监听`0.0.0.0`

- **`c`选项[**Optional**]**
无参数选项，设置该选项时，设置节点的共识算法为RAFT，默认设置为PBFT。

- **`s`选项[**Optional**]**
无参数选项，设置该选项时，节点使用`storagestate`存储形式，默认使用`mptstate`存储形式

- **`g`选项[**Optional**]**
无参数选项，设置该选项时，编译国密版本。<font color=#FF0000>使用`g`选项时要求二进制fisoc-bcos为国密版本。</font>

- **`z`选项[**Optional**]**
无参数选项，设置该选项时，生成节点的tar包。

- **`t`选项[**Optional**]**
该选项用于指定生成证书时的证书配置文件。

- **`T`选项[**Optional**]**
无参数选项，设置该选项时，设置节点的log级别为DEBUG

- **`P`选项[**Optional**]**
设置SDK需要的PKCS12文件的密码。

### 4. 使用举例
- 生成
```bash
# 一键建链脚本
#例: 建立本机四节点区块链，并开启外网RPC端口监听
# -e: fisco-bcos可执行文件路径，不设置则从GitHub下载最新的二进制
# -l: 物理机的ip:物理机的节点数目
# -i: JsonRPC和channel server监听ip为外网IP
bash build_chain.sh -e ../build/bin/fisco-bcos -l "127.0.0.1:4" -i
```

- 生成成功后，输出如下
```bash
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : ../build/bin/fisco-bcos
[INFO] Start Port        : 30300
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : mpt
[INFO] RPC listen IP     : 0.0.0.0
[INFO] Output Dir        : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
[INFO] CA Key Path       : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
```

- 目录结构
```bash
nodes/
├── 127.0.0.1
│   ├── fisco-bcos
│   ├── node0
│   │   ├── conf
│   │   │   ├── agency.crt
│   │   │   ├── ca.crt
│   │   │   ├── group.1.ini
│   │   │   ├── node.crt
│   │   │   ├── node.key
│   │   │   ├── node.nodeid
│   │   │   └── node.serial
│   │   ├── config.ini
│   │   ├── sdk
│   │   │   ├── ca.crt
│   │   │   └── keystore.p12
│   │   ├── start.sh
│   │   └── stop.sh
│   ├── node1
│   │.....
│   ├── node2
│   │.....
│   ├── node3
│   │.....
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

[build_chain]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/build_chain.sh
