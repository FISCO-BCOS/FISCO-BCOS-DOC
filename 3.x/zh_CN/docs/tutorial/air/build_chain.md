# 部署工具(build_chain.sh)

标签：``build_chain`` ``搭建Air版区块链网络``

----

```eval_rst
.. important::
    本部署工具 build_chain.sh 脚本目标是让用户最快的使用FISCO BCOS Air版。
```

FISCO BCOS提供了`build_chain.sh`脚本帮助用户快速搭建FISCO BCOS联盟链。

## 1. 脚本功能介绍

`build_chain.sh`脚本用于快速生成一条链中节点的配置文件，脚本的源码位于[github源码](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh), [gitee源码](https://gitee.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh)。

```eval_rst
.. note::
    为便于开发和体验，p2p模块默认监听IP是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如内网IP或特定的外网IP
```

```shell
# 键入bash build_chain.sh -h展示脚本用法及参数
$ bash build_chain.sh
Usage:
    -C <Command>                        [Optional] the command, support 'deploy' and 'expand' now, default is deploy
    -v <FISCO-BCOS binary version>      Default is the latest v3.0.0-rc2
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -o <output dir>                     [Optional] output directory, default ./nodes
    -e <fisco-bcos exec>                [Required] fisco-bcos binary exec
    -p <Start Port>                     Default 30300,20200 means p2p_port start from 30300, rpc_port from 20200
    -s <SM model>                       [Optional] SM SSL connection or not, default is false
    -c <Config Path>                    [Required when expand node] Specify the path of the expanded node config.ini, config.genesis and p2p connection file nodes.json
    -d <CA cert path>                   [Required when expand node] When expanding the node, specify the path where the CA certificate and private key are located
    -D <docker mode>                    Default off. If set -d, build with docker
    -A <Auth mode>                      Default off. If set -A, build chain with auth, and generate admin account.
    -a <Auth account>                   [Optional when Auth mode] Specify the admin account address.
    -w <WASM mode>                      [Optional] Whether to use the wasm virtual machine engine, default is false
    -h Help

deploy nodes e.g
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -s
expand node e.g
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -s
```


### **`C`选项[**Optional**]:**

脚本的命令，支持 `deploy` 与 `expand`，默认为`deploy`:
- `deploy`: 用于部署新节点。
- `expand` 用于节点扩容。

### **`v`选项**

用于指定搭建FISCO BCOS时使用的二进制版本。build_chain默认下载[Release页面](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)最新版本。

### **`l`选项**

生成节点的IP与对应IP上部署的区块链节点数目，参数格式为 `ip1:nodeNum1, ip2:nodeNum2`。

在IP为`192.168.0.1`的机器上部署2个节点，IP为`127.0.0.1`的机器上部署4个节点的`l`选项示例如下：
`192.168.0.1:2, 127.0.0.1:4`

### **`o`选项**

指定生成的节点配置所在的目录，默认目录为 `./nodes` 。

### **`e`选项[**Optional**]**

指定Air版本FISCO BCOS的二进制可执行文件路径，若不指定，则默认拉取最新版本的FISCO BCOS。

### **`p`选项**

指定节点P2P和RPC服务的监听的起始端口，P2P服务默认以30300为起始端口，RPC服务默认以20200为起始端口。

指定30300为P2P服务监听的起始端口；20200为RPC服务监听的起始端口示例如下：

```shell
# 两个节点的P2P服务分别占用30300和30301端口
# RPC服务分别占用20200和20201端口
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2
```

### **`s`选项[**Optional**]**

指定是否搭建全链路的国密区块链，国密区块链有如下特性：

- **区块链账本使用国密算法**: 使用sm2签名验签算法、sm3哈希算法以及sm4对称加解密算法。
- **SDK客户端与节点间采用国密SSL连接**。
- **区块链节点之间采用国密SSL连接**。

搭建单机四节点国密区块链节点的示例如下：
```shell
$ bash build_chain.sh -l 127.0.0.1:4 -s -o gm_nodes
```

### **`c`扩容选项**

扩容节点选项，用于指定扩容节点的配置文件路径，此路径须包括`config.ini, config.genesis, nodes.json`。


### **`d`扩容选项**

扩容节点选项，用于指定扩容节点的CA证书和CA私钥所在目录。
### **`D`选项[**Optional**]**

使用docker模式搭建Air版本FISCO BCOS区块链，使用该选项时不再拉取二进制，但要求用户启动节点机器安装docker且账户有docker权限。

可在节点目录下执行如下命令启动docker节点:

```shell
./start.sh
```

该模式下 start.sh 脚本启动节点的命令如下

```shell
docker run -d --rm --name ${nodePath} -v ${nodePath}:/data --network=host -w=/data fiscoorg/fiscobcos:v3.0.0-rc2 -c config.ini -g config.genesis
```

### **`A`权限控制选项[**Optional**]**

表示区块链节点启用权限模式。

```eval_rst
.. note::
   - 区块链节点默认关闭权限模式
   - 使用权限控制时，请保证所有节点均启用了权限模式，即须保证节点目录下config.ini的 ``executor.is_auth_check`` 选项均是true.
```

部署开启权限控制的Air版本区块链示例如下：

```shell
$ bash build_chain.sh -l 127.0.0.1:4 -A
[INFO] Downloading fisco-bcos binary from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.0.0-rc2/fisco-bcos-macOS-x86_64.tar.gz ...
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate ./nodes/127.0.0.1/sdk cert successful!
[INFO] Generate ./nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate ./nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate ./nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate ./nodes/127.0.0.1/node3/conf cert successful!
[INFO] Downloading get_account.sh from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh...
==============================================================
[INFO] fisco-bcos Path     : bin/fisco-bcos
[INFO] Auth Mode           : true
[INFO] Auth init account   : 0x5b606554358c2c492444f071a332285aad21a78b
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 127.0.0.1:4
[INFO] SM Model            : false
[INFO] output dir          : ./nodes
[INFO] All completed. Files in ./nodes
```

### **`a`权限控制选项[**Optional**]**

可选参数，当区块链节点启用权限控制时，可通过`-a`选项指定admin账号的地址，若不指定改选项，`build_chain`脚本随机会生成一个账户地址作为admin账号。

### **`h`选项[**Optional**]**

查看脚本使用用法。

## 2. 节点配置文件组织结构

`build_chain`生成的节点配置主要如下：

- **根证书和根证书私钥**: 位于指定的配置生成目录的`ca`文件夹。
- **节点连接证书**: 每个节点`conf`目录下均存放节点的网络连接证书，非国密节点证书和私钥主要包括`ca.crt, ssl.crt, sslkey`，国密节点证书和私钥主要包括`sm_ca.crt, sm_ssl.crt, sm_enssl.crt, sm_enssl.key, sm_ssl.key`。
- **节点签名私钥**: 节点`conf`目录下的`node.pem`，主要位于共识模块的签名。
- **SDK连接证书**: 由`build_chain.sh`生成，客户端可拷贝该证书与节点建立SSL连接。
- **节点配置文件**: 节点目录下的`config.ini`和`config.genesis`配置，前者主要配置链信息，后者主要配置创世块信息，具体可参考[Air版本区块链节点配置介绍](./config.md)。
- **启停脚本**: `start.sh`和`stop.sh`，用于启动和停止节点。

单机四节点Air版本非国密区块链的配置文件组织示例如下:

```shell
nodes/
├── 127.0.0.1
│   ├── fisco-bcos # 二进制程序
│   ├── node0 # 节点0文件夹
│   │   ├── conf # 配置文件夹
│   │   │   ├── ca.crt # 链根证书
│   │   │   ├── cert.cnf
│   │   │   ├── ssl.crt # ssl证书
│   │   │   ├── ssl.key # ssl连接证书私钥
│   │   │   ├── node.pem # 节点签名私钥文件
│   │   │   ├── node.nodeid # 节点id，公钥的16进制表示
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
│   ├── start_all.sh # 启动脚本，用于启动所有节点
│   ├── stop_all.sh # 停止脚本，用于停止所有节点
```
