# 开发部署工具

```eval_rst
.. important::
    开发部署工具 build_chain脚本目标是让用户最快的使用FISCO BCOS，对于企业级应用部署FISCO BCOS请参考 `运维部署工具 <../enterprise_tools/index.html>`_ 。
```

FISCO BCOS提供了`build_chain.sh`脚本帮助用户快速搭建FISCO BCOS联盟链，该脚本默认从[GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS)下载`master`分支最新版本预编译可执行程序进行相关环境的搭建。

## 脚本功能简介

- `build_chain.sh`脚本用于快速生成一条链中节点的配置文件，脚本依赖于`openssl`请根据自己的操作系统安装`openssl 1.0.2`以上版本。脚本的源码位于[FISCO-BCOS/tools/build_chain.sh][build_chain]。
- 快速体验可以使用`-l`选项指定节点IP和数目。`-f`选项通过使用一个指定格式的配置文件，支持创建各种复杂业务场景FISCO BCOS链。**`-l`和`-f`选项必须指定一个且不可共存**。
- 建议测试时使用`-T`，`-T`开启log级别到DEBUG，**p2p模块默认监听 `0.0.0.0`**。

```eval_rst
.. note::
    为便于开发和体验，p2p模块默认监听IP是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如内网IP或特定的外网IP
```

## 帮助

```bash
Usage:
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -f <IP list file>                   [Optional] split by line, every line should be "ip:nodeNum agencyName groupList p2p_port,channel_port,jsonrpc_port". eg "127.0.0.1:4 agency1 1,2 30300,20200,8545"
    -v <FISCO-BCOS binary version>      Default is the latest v${default_version}
    -e <FISCO-BCOS binary path>         Default download fisco-bcos from GitHub. If set -e, use the binary at the specified location
    -o <Output Dir>                     Default ./nodes/
    -p <Start Port>                     Default 30300,20200,8545 means p2p_port start from 30300, channel_port from 20200, jsonrpc_port from 8545
    -q <List FISCO-BCOS releases>       List FISCO-BCOS released versions
    -i <Host ip>                        Default 127.0.0.1. If set -i, listen 0.0.0.0
    -s <DB type>                        Default RocksDB. Options can be RocksDB / MySQL / Scalable, RocksDB is recommended
    -d <docker mode>                    Default off. If set -d, build with docker
    -c <Consensus Algorithm>            Default PBFT. Options can be pbft / raft /rpbft, pbft is recommended
    -C <Chain id>                       Default 1. Can set uint.
    -g <Generate guomi nodes>           Default no
    -z <Generate tar packet>            Default no
    -t <Cert config file>               Default auto generate
    -6 <Use ipv6>                       Default no. If set -6, treat IP as IPv6
    -k <The path of ca root>            Default auto generate, the ca.crt and ca.key must in the path, if use intermediate the root.crt must in the path
    -K <The path of sm crypto ca root>  Default auto generate, the gmca.crt and gmca.key must in the path, if use intermediate the gmroot.crt must in the path
    -D <Use Deployment mode>            Default false, If set -D, use deploy mode directory struct and make tar
    -G <channel use sm crypto ssl>      Default false, only works for guomi mode
    -X <Certificate expiration time>    Default 36500 days
    -T <Enable debug log>               Default off. If set -T, enable debug log
    -S <Enable statistics>              Default off. If set -S, enable statistics
    -F <Disable log auto flush>         Default on. If set -F, disable log auto flush
    -E <Enable free_storage_evm>        Default off. If set -E, enable free_storage_evm
    -h Help
e.g
    ./build_chain.sh -l 127.0.0.1:4
```

## 选项介绍

### **`l`选项:**

用于指定要生成的链的IP列表以及每个IP下的节点数，以逗号分隔。脚本根据输入的参数生成对应的节点配置文件，其中每个节点的端口号默认从30300开始递增，所有节点属于同一个机构和群组。

### **`f`选项**

    + 用于根据配置文件生成节点，相比于`l`选项支持更多的定制。
    + 按行分割，每一行表示一个服务器，格式为`IP:NUM AgencyName GroupList`，每行内的项使用空格分割，**不可有空行**。
    + `IP:NUM`表示机器的IP地址以及该机器上的节点数。`AgencyName`表示机构名，用于指定使用的机构证书。`GroupList`表示该行生成的节点所属的组，以`,`分割。例如`192.168.0.1:2 agency1 1,2`表示`ip`为`192.168.0.1`的机器上有两个节点，这两个节点属于机构`agency1`，属于group1和group2。

下面是一个配置文件的例子，每个配置项以空格分隔。

```bash
192.168.0.1:1 agency1 1,2 30300,20200,8545
192.168.0.2:1 agency1 1,2 30300,20200,8545
192.168.0.3:2 agency1 1,3 30300,20200,8545
192.168.0.4:1 agency2 1   30300,20200,8545
192.168.0.5:1 agency3 2,3 30300,20200,8545
192.168.0.6:1 agency2 3   30300,20200,8545
```

**假设上述文件名为`ipconf`**，则使用下列命令建链，表示使用配置文件，设置日志级别为`DEBUG`。

```bash
bash build_chain.sh -f ipconf -T
```

### **`e`选项[**Optional**]**

用于指定`fisco-bcos`二进制所在的**完整路径**，脚本会将`fisco-bcos`拷贝以IP为名的目录下。不指定时，默认从GitHub下载最新的二进制程序。

```bash
# 从GitHub下载最新release二进制，生成本机4节点
$ bash build_chain.sh -l 127.0.0.1:4
# 使用 bin/fisco-bcos 二进制，生成本机4节点
$ bash build_chain.sh -l 127.0.0.1:4 -e bin/fisco-bcos
```

### **`o`选项[**Optional**]**

指定生成的配置所在的目录。

### **`p`选项[**Optional**]**

指定节点的起始端口，每个节点占用三个端口，分别是p2p,channel,jsonrpc使用`,`分割端口，必须指定三个端口。同一个IP下的不同节点所使用端口从起始端口递增。

```bash
# 两个节点分别占用`30300,20200,8545`和`30301,20201,8546`。
$ bash build_chain.sh -l 127.0.0.1:2 -p 30300,20200,8545
```

### **`q`选项[**Optional**]**

列出FISCO BCOS已经发布的所有版本号。

### **`v`选项[**Optional**]**

用于指定搭建FISCO BCOS时使用的二进制版本。build_chain默认下载[Release页面](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)最新版本，设置该选项时下载参数指定`version`版本并设置`config.ini`配置文件中的`[compatibility].supported_version=${version}`。如果同时使用`-e`选项，则配置`[compatibility].supported_version=${version}`为[Release页面](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)最新版本号。

### **`d`选项[**Optional**]**

使用docker模式搭建FISCO BCOS，使用该选项时不再拉取二进制，但要求用户启动节点机器安装docker且账户有docker权限，即用户加入docker群组。
在节点目录下执行如下命令启动节点

```bash
./start.sh
```

该模式下 start.sh 脚本启动节点的命令如下

```bash
docker run -d --rm --name ${nodePath} -v ${nodePath}:/data --network=host -w=/data fiscoorg/fiscobcos:latest -c config.ini
```

### **`s`选项[**Optional**]**

有参数选项，参数为db名，目前支持RocksDB、mysql、Scalable。默认使用rocks。

- RocksDB模式，使用RocksDB作为后端数据库。
- MySQL模式，使用MySQL作为后端数据库，节点直连MySQL，需要在群组ini文件中配置数据库相关信息。
- Scalable模式，区块数据和状态数据存储在不同的数据库中，区块数据根据配置存储在以块高命名的RocksDB实例中。如需使用裁剪数据的功能，必须使用Scalable模式。

### **`c`选项[**Optional**]**

有参数选项，参数为共识算法类型，目前支持PBFT、Raft、rPBFT。默认共识算法是PBFT。

- `PBFT`：设置节点共识算法为[PBFT](../design/consensus/pbft.md)。
- `Raft`：设置节点共识算法为[Raft](../design/consensus/raft.md)。
- `rPBFT`：设置节点共识算法为[rPBFT](../design/consensus/rpbft.md)。

### **`C`选项[**Optional**]**
用于指定搭建FISCO BCOS时的链标识。设置该选项时将使用参数设置`config.ini`配置文件中的`[chain].id`，参数范围为正整数，默认设置为1。

```bash
# 该链标识为2。
bash build_chain.sh -l 127.0.0.1:2 -C 2
```

### **`g`选项[**Optional**]**

无参数选项，设置该选项时，搭建国密版本的FISCO BCOS。**确认sdk支持的情况下（web3sdk v2.5.0+），可以指定-g -G参数，连接也使用国密SSL**，`-G`设置`chain.sm_crypto_channel=true`

```bash
bash build_chain.sh -l 127.0.0.1:2 -g -G
```

### **`z`选项[**Optional**]**

无参数选项，设置该选项时，生成节点的tar包。

### **`t`选项[**Optional**]**

该选项用于指定生成证书时的证书配置文件。

### **`6`选项[**Optional**]**

该选项表示使用IPv6模式，监听`::`

### **`T`选项[**Optional**]**

无参数选项，设置该选项时，设置节点的log级别为DEBUG。log相关配置[参考这里](./configuration.html#id6)。

### **`k`选项[**Optional**]**

使用用户指定的链证书和私钥签发机构和节点的证书，参数指定路径，路径下必须包括ca.crt/ca.key，如果所指定的私钥和证书是中间ca，那么此文件夹下还需要包括root.crt，用于存放上级证书链。

### **`K`选项[**Optional**]**

国密模式使用用户指定的链证书和私钥签发机构和节点的证书，参数指定路径，路径下必须包括gmca.crt/gmca.key，如果所指定的私钥和证书是中间ca，那么此文件夹下还需要包括gmroot.crt，用于存放上级证书链。

### **`G`选项[**Optional**]**

从2.5.0开始，国密模式下，用户可以配置节点与SDK连接是否使用国密SSL，设置此选项则`chain.sm_crypto_channel=true`。默认节点与SDK的channel连接使用secp256k1的证书。**确认sdk支持的情况下（web3sdk v2.5.0+），可以指定-g -G参数，连接也使用国密SSL**

### **`D`选项[**Optional**]**

无参数选项，设置该选项时，生成节点的目录名为IP_P2P端口，默认为节点从0开始的编号。

### **`E`选项[**Optional**]**

无参数选项，设置该选项时，启用[Free Storage](design/virtual_machine/gas.html#evm-gas) Gas模式，默认关闭`Free Storage` Gas模式

## 节点文件组织结构

- cert文件夹下存放链的根证书和机构证书。
- 以IP命名的文件夹下存储该服务器所有节点相关配置、`fisco-bcos`可执行程序、SDK所需的证书文件。
- 每个IP文件夹下的`node*`文件夹下存储节点所需的配置文件。其中`config.ini`为节点的主配置，`conf`目录下存储证书文件和群组相关配置。配置文件详情，请[参考这里](configuration.md)。每个节点中还提供`start.sh`和`stop.sh`脚本，用于启动和停止节点。
- 每个IP文件夹下的提供`start_all.sh`和`stop_all.sh`两个脚本用于启动和停止所有节点。

```bash
nodes/
├── 127.0.0.1
│   ├── fisco-bcos # 二进制程序
│   ├── node0 # 节点0文件夹
│   │   ├── conf # 配置文件夹
│   │   │   ├── ca.crt # 链根证书
│   │   │   ├── group.1.genesis # 群组1初始化配置，该文件不可更改
│   │   │   ├── group.1.ini # 群组1配置文件
│   │   │   ├── node.crt # 节点证书
│   │   │   ├── node.key # 节点私钥
│   │   │   ├── node.nodeid # 节点id，公钥的16进制表示
│   │   ├── config.ini # 节点主配置文件，配置监听IP、端口等
│   │   ├── start.sh # 启动脚本，用于启动节点
│   │   └── stop.sh # 停止脚本，用于停止节点
│   ├── node1 # 节点1文件夹
│   │.....
│   ├── node2 # 节点2文件夹
│   │.....
│   ├── node3 # 节点3文件夹
│   │.....
│   ├── sdk # SDK与节点SSL连接配置，FISCO-BCOS 2.5及之后的版本，添加了SDK只能连本机构节点的限制，操作时需确认拷贝证书的路径，否则建联报错
│   │   ├── ca.crt # SSL连接根证书
│   │   ├── sdk.crt # SSL连接证书
│   │   └── sdk.key # SSL连接证书私钥
|   |   ├── gm # SDK与节点国密SSL连接配置，注意：生成国密区块链环境时才会生成该目录，用于节点与SDK的国密SSL连接
|   |   │   ├── gmca.crt # 国密SSL连接根证书
|   |   │   ├── gmensdk.crt # 国密SSL连接加密证书
|   |   │   ├── gmensdk.key # 国密SSL连接加密证书私钥
|   |   │   ├── gmsdk.crt # 国密SSL连接签名证书
|   |   │   └── gmsdk.key # 国密SSL连接签名证书私钥
├── cert # 证书文件夹
│   ├── agency # 机构证书文件夹
│   │   ├── agency.crt # 机构证书
│   │   ├── agency.key # 机构私钥
│   │   ├── agency.srl
│   │   ├── ca-agency.crt
│   │   ├── ca.crt
│   │   └── cert.cnf
│   ├── ca.crt # 链证书
│   ├── ca.key # 链私钥
│   ├── ca.srl
│   └── cert.cnf
```

## 工具脚本

介绍由build_chain.sh生成的脚本。

### start_all.sh

启动当前目录下的所有节点。

### stop_all.sh

停止当前目录下的所有节点。

### download_console.sh

下载console的脚本。

- `v`选项支持下载指定版本的console
- `f`选项自动配置下载的console的证书和端口

### download_bin.sh

用于下载fisco-bcos二进制程序，选项如下。

```bash
Usage:
    -v <Version>           Download binary of spectfic version, default latest
    -b <Branch>            Download binary of spectfic branch
    -o <Output Dir>        Default ./bin
    -l                     List FISCO-BCOS released versions
    -m                     Download mini binary, only works with -b option
    -h Help
e.g
    ./download_bin.sh -v 2.6.0
```


## 使用举例

### 无外网条件的单群组

**最简单的操作方式是在有外网的Linux机器上使用build_chain建好链，借助-z选项打包，然后拷贝到无外网的机器上运行。**

1. 针对某些场景下无外网条件下建链，请从[发布页面](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)下载最新的目标操作系统的二进制，例如对于Linux系统下载fisco-bcos.tar.gz。
1. 请从[发布页面](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)下载最新版本的build_chain脚本。
1. 上传fisco-bcos.tar.gz和build_chain.sh到目标服务器，需要注意目标服务器要求64位，要求安装有openssl 1.0.2以上版本。
1. 解压fisco-bcos.tar.gz得到fisco-bcos可执行文件，作为-e选项的参数。
1. 构建本机上4节点的FISCO BCOS联盟链，使用默认起始端口`30300,20200,8545`（4个节点会占用`30300-30303`,`20200-20203`,`8545-8548`）。
1. 执行下面的指令，假设最新版本是2.2.0，则将`2.2.0`作为-v选项参数。

```bash
# 构建FISCO BCOS联盟链
$ bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545 -e ./fisco-bcos -v 2.2.0
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
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
[INFO] CA Key Path       : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
```

### 群组新增节点

本节以为上一小节生成的群组1新增一个共识节点为例操作。

#### 为新节点生成私钥证书

接下来的操作，都在上一节生成的`nodes/127.0.0.1`目录下进行

1. 获取证书生成脚本

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_node_cert.sh`
```

2. 生成新节点私钥证书

```bash
# -c指定机构证书及私钥所在路径
# -o输出到指定文件夹，其中newNode/conf中会存在机构agency新签发的证书和私钥
bash gen_node_cert.sh -c ../cert/agency -o newNode
```

国密版本请执行下面的指令生成证书。
```bash
bash gen_node_cert.sh -c ../cert/agency -o newNodeGm -g ../gmcert/agency/
```

#### 准备配置文件

1. 拷贝群组1中节点node0配置文件与工具脚本

    ```bash
    cp node0/config.ini newNode/config.ini
    cp node0/conf/group.1.genesis newNode/conf/group.1.genesis
    cp node0/conf/group.1.ini newNode/conf/group.1.ini
    cp node0/*.sh newNode/
    cp -r node0/scripts newNode/
    ```

2. 更新`newNode/config.ini`中监听的IP和端口，对于`[rpc]`模块，修改`listen_ip`、`channel_listen_port`和`jsonrpc_listen_port`；对于`[p2p]`模块，修改`listen_port`
3. 将新节点的P2P配置中的IP和Port加入原有节点的config.ini中的[p2p]字段。假设新节点IP:Port为127.0.0.1:30304则，修改后的[P2P]配置为

```eval_rst
.. note::
    为便于开发和体验，p2p模块默认监听IP是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如：内网IP或特定的外网IP
```

    ```bash
    [p2p]
        listen_ip=0.0.0.0
        listen_port=30304
        ;enable_compress=true
        ; nodes to connect
        node.0=127.0.0.1:30300
        node.1=127.0.0.1:30301
        node.2=127.0.0.1:30302
        node.3=127.0.0.1:30303
        node.4=127.0.0.1:30304
    ```
4. 启动新节点，执行`newNode/start.sh`
5. 通过console将新节点加入群组1，2.6版本控制台指令详细介绍[参考这里](manual/console_of_java_sdk.md)，1.x版本控制台指令详细介绍[参考这里](manual/console.md)，`nodeID`可以通过命令`cat newNode/conf/node.nodeid`来获取
6. 检查连接和共识

### 为机构生成新的SDK证书

接下来的操作，都在上一节生成的`nodes/127.0.0.1`目录下进行

1. 获取证书生成脚本

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_node_cert.sh`
```

2. 生成新节点私钥证书

```bash
# -c指定机构证书及私钥所在路径
# -o输出到指定文件夹，其中newSDK中会存在机构agency新签发的证书和私钥
bash gen_node_cert.sh -c ../cert/agency -o newSDK -s
```

国密版本请执行下面的指令生成证书。
```bash
bash gen_node_cert.sh -c ../cert/agency -o newSDK -g ../gmcert/agency/ -s
```

### 生成新机构证书

1. 获取机构证书生成脚本

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_agency_cert.sh
```

2. 生成新机构私钥和证书

```bash
# -c 指定链证书及私钥所在路径，目录下必须有ca.crt 和 ca.key， 如果ca.crt是二级CA，则还需要root.crt(根证书)
# -g 指定国密链证书及私钥所在路径，目录下必须有gmca.crt 和 gmca.key，如果gmca.crt是二级CA，则还需要gmroot.crt(根证书)
# -a 新机构的机构名
bash gen_agency_cert.sh -c nodes/cert/ -a newAgencyName
```

国密版本请执行下面的指令。
```bash
bash gen_agency_cert.sh -c nodes/cert/ -a newAgencyName -g nodes/gmcert/
```

### 多服务器多群组

使用`build_chain`脚本构建多服务器多群组的FISCO BCOS联盟链需要借助脚本配置文件，详细使用方式可以[参考这里](../manual/group_use_cases.md)。

[build_chain]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/build_chain.sh
