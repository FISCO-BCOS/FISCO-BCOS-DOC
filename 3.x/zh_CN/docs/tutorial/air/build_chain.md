# 部署工具(build_chain.sh)

标签：``build_chain`` ``搭建Air版区块链网络``

----

```eval_rst
.. important::
    本部署工具 build_chain.sh 脚本目标是让用户最快的使用FISCO BCOS Air版。
```

FISCO BCOS提供了`build_chain.sh`脚本帮助用户快速搭建FISCO BCOS联盟链，该脚本默认从[GitHub](https://github.com/FISCO-BCOS/FISCO-BCOSS)下载`release-3.1.0`分支最新版本，执行脚本文件。

## 功能介绍

- `build_chain.sh`脚本用于快速生成一条链中节点的配置文件，脚本依赖于`openssl`请根据自己的操作系统安装`openssl 1.0.2`以上版本。脚本的源码位于[github源码](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-3.1.0/tools/BcosAirBuilder/build_chain.sh), [gitee源码](https://gitee.com/FISCO-BCOS/FISCO-BCOS/blob/release-3.1.0/tools/BcosAirBuilder/build_chain.sh)

```eval_rst
.. note::
    为便于开发和体验，p2p模块默认监听IP是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如内网IP或特定的外网IP
```

```bash
# 键入bash build_chain.sh -h展示脚本用法及参数
Usage:
    -C <Command>                        [Optional] the command, support 'deploy' and 'expand' now, default is deploy
    -v <FISCO-BCOS binary version>      Default is the latest v3.0.0-rc1
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -o <output dir>                     [Optional] output directory, default ./nodes
    -e <fisco-bcos exec>                [Required] fisco-bcos binary exec
    -p <Start Port>                     Default 30300,20200 means p2p_port start from 30300, rpc_port from 20200
    -s <SM model>                       [Optional] SM SSL connection or not, default no
    -c <Config Path>                    [Required when expand node] Specify the path of the expanded node config.ini, config.genesis and p2p connection file nodes.json
    -d <CA cert path>                   [Required when expand node] When expanding the node, specify the path where the CA certificate and private key are located
    -D <docker mode>                    Default off. If set -d, build with docker
    -A <Auth mode>                      Default off. If set -A, build chain with auth, and generate admin account.
    -a <Auth account>                   [Optional when Auth mode] Specify the admin account address.
    -h Help

deploy nodes e.g
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -s
expand node e.g
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -s
```


### **`C`选项[**Optional**]:**

`command`: 脚本的命令，支持 `deploy` 与 `expand`。`deploy` 用于部署新节点，`expand` 用于节点扩容。不输入的话默认为`deploy`。

### **`v`选项**
用于指定搭建FISCO BCOS时使用的二进制版本。build_chain默认下载[Release页面](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)最新版本。

### **`l`选项**

生成节点的ip与数量列表。参数格式为 `ip1:nodeNum1, ip2:nodeNum2`，例如：`192.168.0.1:2, 127.0.0.1:4`。

### **`o`选项**

指定生成的配置所在的目录。默认为当前目录下生成的 `./nodes` 目录。

### **`e`选项[**Optional**]**

fisco-bcos二进制可执行文件所在的目录。

### **`p`选项**

指定节点的起始端口，指定两个端口，包括P2P、RPC端口。默认设置为30300，20200，表示P2P以30300为起始端口、RPC以20200为起始端口。

```bash
# 两个节点的P2P、RPC分别占用`30300,20200`和`30301,20201`。
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2
```

### **`s`选项[**Optional**]**

是否使用SM SSL连接，默认不使用。

```bash
# 使用示例
$ bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -s
```

### **`c`选项**

新增节点时必须输入，用于新增节点时指定扩容节点的config.ini, config.genesis以及p2p连接文件的nodes.json配置路径。

### **`d`选项**

新增节点时必须输入，用于新增节点时指定CA证书和私钥的文件路径。

### **`D`选项[**Optional**]**

使用docker模式搭建FISCO BCOS，使用该选项时不再拉取二进制，但要求用户启动节点机器安装docker且账户有docker权限，即用户加入docker群组。
在节点目录下执行如下命令启动节点

```bash
./start.sh
```

该模式下 start.sh 脚本启动节点的命令如下

```bash
docker run -d --rm --name ${nodePath} -v ${nodePath}:/data --network=host -w=/data fiscoorg/fiscobcos:latest -c config.ini
```

### **`A`选项[**Optional**]**

使用权限模式，该模式下会以权限搭建区块链，并且产生admin账号。默认关闭权限模式。


### **`a`选项[**Optional**]**

在权限模式为可选参数，该模式下用于指定admin的账号地址。

### **`h`选项[**Optional**]**

`help` 脚本使用用法。

## 节点文件组织结构

- ca文件夹下存放链的根证书和机构证书。
- 以IP命名的文件夹下存储该服务器所有节点相关配置、`fisco-bcos`可执行程序、SDK所需的证书文件。
- 每个IP文件夹下的`node*`文件夹下存储节点所需的配置文件。其中`config.ini`为节点的主配置，`conf`目录下存储证书文件和群组相关配置。配置文件详情，请[参考这里](./config.md)。每个节点中还提供`start.sh`和`stop.sh`脚本，用于启动和停止节点。
- 每个IP文件夹下的提供`start_all.sh`和`stop_all.sh`两个脚本用于启动和停止所有节点。

```bash
nodes/
├── 127.0.0.1
│   ├── fisco-bcos # 二进制程序
│   ├── node0 # 节点0文件夹
│   │   ├── conf # 配置文件夹
│   │   │   ├── ca.crt # 链根证书
│   │   │   ├── cert.cnf # 群组1初始化配置，该文件不可更改
│   │   │   ├── ssl.crt # ssl证书
│   │   │   ├── ssl.key # ssl连接证书私钥
│   │   │   ├── node.pem # 节点pem格式证书
│   │   │   ├── node.nodeid # 节点id，公钥的16进制表示
│   │   │   ├── sm_ca.crt # sm链根证书
│   │   │   ├── sm_enssl.crt # sm ssl连接加密证书
│   │   │   ├── sm_enssl.key # sm ssl连接加密证书对应的私钥
│   │   │   ├── sm_ssl.crt # sm ssl连接证书
│   │   │   ├── sm_ssl.key # sm ssl连接私钥
│   │   │   ├── sm_ssl.nodeid # sm ssl连接节点id
│   │   ├── config.ini # 节点主配置文件，配置监听IP、端口、证书、日志等
│   │   ├── config.genesis # 创世配置文件，共识算法类型、共识超时时间和交易gas限制等
│   │   ├── nodes.json # 节点json信息，展示节点的ip和端口，示例：{"nodes": [127.0.0.1:30300]}
│   │   ├── start.sh # 启动脚本，用于启动节点
│   │   └── stop.sh # 停止脚本，用于停止节点
│   ├── node1 # 节点1文件夹
│   │.....
│   ├── node2 # 节点2文件夹
│   │.....
│   ├── node3 # 节点3文件夹
│   │.....
│   ├── sdk # SDK证书
│   │   ├── ca.crt # SSL连接根证书
│   │   ├── cert.cnf # 证书配置
│   │   ├── sdk.crt # SDK根证书
│   │   ├── sdk.key # SDK证书私钥
│   │   ├── sm_ca.crt # sm根证书
│   │   ├── sm_ensdk.key # 证书配置
│   │   ├── sm_ensdk.crt # SDK根证书
│   │   ├── sm_sdk.crt # sm SDK证书
│   │   ├── sm_sdk.key # sm SDK私钥
│   │   ├── sm_sdk.nodeid # sm SDK节点ID
│   ├── start_all.sh # 启动脚本，用于启动所有节点
│   ├── stop_all.sh # 停止脚本，用于停止所有节点
```
## build_chain.sh使用教程

下载`FISCO-BCOS`项目并执行脚本`build_chain.sh`指令如下：
```bash
# 拉取 FISCO-BCOS 项目
# 项目的gitee地址待提供
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git

cd FISCO-BCOS
# 切换到 release-3.1.0 分支
git checkout release-3.1.0
# 进入 tools/.ci/ 目录
cd tools/.ci
# 拉取air版静态二进制
python3 download_binary.py --command download_air
# 解压二进制
# linux系统
unzip fisco-bcos-linux.zip
# macOS系统
unzip fisco-bcos-MacOS.zip

tar -xvf fisco-bcos.tar.gz

# 进入到air版本脚本文件
cd ../BcosAirBuilder
# 执行脚本 build_chain.sh，构建FISCO BCOS联盟链
bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2 -o nodes -e ../.ci/fisco-bcos

# 生成成功后，输出`All completed`提示
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:2
[INFO] Generate nodes/127.0.0.1/sdk cert successful!
[INFO] Generate nodes/127.0.0.1/node0/conf cert successful!
WARNING: can't open config file: /usr/local/ssl/openssl.cnf
[INFO] Generate nodes/127.0.0.1/node1/conf cert successful!
[INFO] Downloading get_account.sh from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh...
############################################################################################################################################################################ 100.0%
mv: rename accounts* to nodes/ca/accounts*: No such file or directory
mv: rename accounts* to nodes/ca/accounts*: No such file or directory
==============================================================
[INFO] fisco-bcos Path     : ../.ci/fisco-bcos
[INFO] Auth Mode           : false
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 127.0.0.1:2
[INFO] SM Model            : false
[INFO] output dir          : nodes
[INFO] All completed. Files in nodes
```

### 群组新增节点
使用build_chain.sh脚本的`expand`命令，进行节点的拓展。
命令如下：
```bash
$ bash build_chain.sh -C expand -c nodes/127.0.0.1/node0 -d nodes/ca -o nodes/127.0.0.1/node5 -e ../.ci/fisco-bcos -s

# 执行指令后，显示All completed.则新增成功。完成信息如下
[INFO] generate_node_scripts ...
[INFO] generate_node_scripts success...
[INFO] generate_node_cert ...
[INFO] generate_node_cert success...
[INFO] generate_node_account ...
[INFO] generate_node_account success...
[INFO] copy configurations ...
[INFO] copy configurations success...
==============================================================
[INFO] fisco-bcos Path       : ../.ci/fisco-bcos
[INFO] sdk dir         : nodes/127.0.0.1/sdk
[INFO] SM Model         : true
[INFO] output dir         : nodes/127.0.0.1/node5
[INFO] All completed. Files in nodes/127.0.0.1/node5

# 127.0.0.1目录下，看到生成了新的节点node5
ll nodes/127.0.0.1/

# 目录展示信息
total 105248
drwxr-xr-x  12 leevaygr  staff       384 12  2 12:13 ./
drwxr-xr-x   4 leevaygr  staff       128 12  1 15:52 ../
drwxr-xr-x   9 leevaygr  staff       288 12  2 11:03 ca/
-rwxr-xr-x   1 leevaygr  staff  53877208 12  2 11:03 fisco-bcos*
drwxr-xr-x   8 leevaygr  staff       256 12  1 15:52 node0/
drwxr-xr-x   8 leevaygr  staff       256 12  1 15:52 node1/
drwxr-xr-x   8 leevaygr  staff       256 12  2 11:03 node2/
drwxr-xr-x   8 leevaygr  staff       256 12  2 11:03 node3/
drwxr-xr-x   8 leevaygr  staff       256 12  2 12:13 node5/
drwxr-xr-x  12 leevaygr  staff       384 12  2 11:03 sdk/
-rwxr--r--   1 leevaygr  staff       326 12  2 11:03 start_all.sh*
-rwxr--r--   1 leevaygr  staff       323 12  2 11:03 stop_all.sh*
```

## 相关工具脚本

介绍与使用build_chain.sh相关，以及build_chain.sh生成的脚本。

### start_all.sh

启动当前目录下的所有节点。

### stop_all.sh

停止当前目录下的所有节点。

### download_binary.py

用于下载fisco-bcos二进制程序，支持下载**air**与**pro**版fisco-bcos。选项如下：

```bash
usage: download_binary.py [-h] --command COMMAND

download_binary

optional arguments:
  -h, --help         show this help message and exit
  --command COMMAND  [required]the command, support download_air and download_pro
```