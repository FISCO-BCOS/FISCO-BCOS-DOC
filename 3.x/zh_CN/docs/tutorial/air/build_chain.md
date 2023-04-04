# 部署工具(build_chain.sh)

标签：``build_chain`` ``搭建Air版区块链网络``

----

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

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
# 下载建链脚本
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.2.0/build_chain.sh && chmod u+x build_chain.sh

# Note: 若访问git网速太慢，可尝试如下命令下载建链脚本:
curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.2.0/build_chain.sh && chmod u+x build_chain.sh

# 键入bash build_chain.sh -h展示脚本用法及参数
$ bash build_chain.sh
Usage:
    -C <Command>                        [Optional] the command, support 'deploy' and 'expand' now, default is deploy
    -g <group id>                       [Optional] set the group id, default: group0
    -I <chain id>                       [Optional] set the chain id, default: chain0
    -v <FISCO-BCOS binary version>      [Optional] Default is the latest v3.2.0
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -L <fisco bcos lightnode exec>      [Optional] fisco bcos lightnode executable, input "download_binary" to download lightnode binary or assign correct lightnode binary path
    -e <fisco-bcos exec>                [Optional] fisco-bcos binary exec
    -t <mtail exec>                     [Optional] mtail binary exec
    -o <output dir>                     [Optional] output directory, default ./nodes
    -p <Start port>                     [Optional] Default 30300,20200 means p2p_port start from 30300, rpc_port from 20200
    -s <SM model>                       [Optional] SM SSL connection or not, default is false
    -c <Config Path>                    [Required when expand node] Specify the path of the expanded node config.ini, config.genesis and p2p connection file nodes.json
    -d <CA cert path>                   [Required when expand node] When expanding the node, specify the path where the CA certificate and private key are located
    -D <docker mode>                    Default off. If set -d, build with docker
    -A <Auth mode>                      Default off. If set -A, build chain with auth, and generate admin account.
    -a <Auth account>                   [Optional] when Auth mode Specify the admin account address.
    -w <WASM mode>                      [Optional] Whether to use the wasm virtual machine engine, default is false
    -R <Serial_mode>                    [Optional] Whether to use serial execute,default is true
    -k <key page size>                  [Optional] key page size, default size is 10240
    -m <fisco-bcos monitor>             [Optional] node monitor or not, default is false
    -i <fisco-bcos monitor ip/port>     [Optional] When expanding the node, should specify ip and port
    -M <fisco-bcos monitor>             [Optional] When expanding the node, specify the path where prometheus are located
    -h Help

deploy nodes e.g
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -s
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -m(部署节点带监控功能)
expand node e.g
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos
        bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -s
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -m -i 127.0.0.1:5 -M monitor/prometheus/prometheus.yml(部署节点带监控功能)
```


### **`C`选项[**Optional**]**

脚本的命令，支持 `deploy` 与 `expand`，默认为`deploy`:
- `deploy`: 用于部署新节点。
- `expand` 用于节点扩容。

### **`g`选项[**Optional**]**
用于设置群组ID，若不设置，则默认为group0。

### **`c`选项[**Optional**]**
用于设置链ID，若未设置，则默认为chain0。
### **`v`选项[**Optional**]**

用于指定搭建FISCO BCOS时使用的二进制版本。build_chain默认下载[Release页面](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)最新版本。

### **`l`选项**

生成节点的IP与对应IP上部署的区块链节点数目，参数格式为 `ip1:nodeNum1, ip2:nodeNum2`。

在IP为`192.168.0.1`的机器上部署2个节点，IP为`127.0.0.1`的机器上部署4个节点的`l`选项示例如下：
`192.168.0.1:2, 127.0.0.1:4`

### **`L`选项[**Optional**]**
用于配置开启FISCO BCOS轻节点模式，-L 后面可指定Air版本轻节点的二进制可执行文件路径，也可输入"download_binary"，则默认下载最新版本的轻节点二进制，如下图所示。

```shell
# 两个节点的P2P服务分别占用30300和30301端口，RPC服务分别占用20200和20201端口
# -L 启动轻节点模块，"download_binary" 默认拉去最新版本二进制文件
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2 -L download_binary
# 指定轻节点二进制路径
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2 -L /bin/fisco-bcos-lightnode
```


### **`e`选项[**Optional**]**

指定Air版本FISCO BCOS的二进制可执行文件路径，若不指定，则默认拉取最新版本的FISCO BCOS。

### **`t`选项[**Optional**]**

指定Air版本监控依赖的二进制mtail 所在路径，功能和-e类似，若不指定，则默认拉取最新版本的FISCO BCOS。
### **`o`选项[**Optional**]**

指定生成的节点配置所在的目录，默认目录为 `./nodes` 。

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
docker run -d --rm --name ${nodePath} -v ${nodePath}:/data --network=host -w=/data fiscoorg/fiscobcos:v3.2.0 -c config.ini -g config.genesis
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
[INFO] Downloading fisco-bcos binary from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.2.0/fisco-bcos-macOS-x86_64.tar.gz ...
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

可选参数，当区块链节点启用权限控制时，可通过`-a`选项指定admin账号的地址，若不指定该选项，`build_chain`脚本随机会生成一个账户地址作为admin账号。

### **`w`权限控制选项[**Optional**]**
可选参数，当区块链需要启用wasm虚拟机引擎时，可通过`-w`选项开启，若不指定该选项，则默认使用EVM。
### **`R`权限控制选项[**Optional**]**
可选参数，当区块链启动串行执行模式时，可通过`-R`选项指定执行模式，默认为串行模式（true），若设置为false，则开启DMC并行模式。
### **`k`权限控制选项[**Optional**]**
可选参数，当需要设置key-page存储中page的大小时，可通过`-k`选项设置page的大小，若不指定，默认page大小为10240。
### **`m`节点监控选项[**Optional**]**

可选参数，当区块链节点启用节点监控时，可通过`-m`选项来部署带监控的节点，若不选择该选项则只部署不带监控的节点。

部署开启监控的Air版本区块链示例如下：

```shell
[root@172 air]# bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -t ./mtail -m
[INFO] Use binary ./fisco-bcos
[INFO] Use binary ./mtail
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate nodes/127.0.0.1/sdk cert successful!
[INFO] Generate nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node3/conf cert successful!
[INFO] Begin generate uuid
[INFO] Generate uuid success: 1357cd37-6991-44c0-b14a-5ea81355c12c
[INFO] Begin generate uuid
[INFO] Generate uuid success: c68ebc3f-2258-4e34-93c9-ba5ab6d2f503
[INFO] Begin generate uuid
[INFO] Generate uuid success: 5311259c-02a5-4556-9726-daa1ee8fbefc
[INFO] Begin generate uuid
[INFO] Generate uuid success: d4e5701b-bbce-4dcc-a94f-21160425cdb9
==============================================================
[INFO] fisco-bcos Path     : ./fisco-bcos
[INFO] Auth Mode           : false
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 127.0.0.1:4
[INFO] SM Model            : false
[INFO] output dir          : nodes
[INFO] All completed. Files in nodes
```
生成完区块链节点文件，启动节点（nodes/127.0.0.1/start_all.sh）和节点监控（nodes/monitor/start_monitor.sh），根据提示登录grafana（用户名密码为admin/admin）导入Dashboard（[github源码](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/template/Dashboard.json)）和配置prometheus源(http://ip:9090/)查看各个指标实时展示。

### **`i`扩容节点监控选项[**Optional**]**

可选参数，当区块链扩容节点需要带监控时，通过`-i`选项来指定扩容节点监控，参数格式为 `ip1:nodeNum1`，在IP为`192.168.0.1`的机器上扩容第2个节点监控，`l`选项示例如下：`192.168.0.1:2`。

### **`M`节点监控配置文件选项[**Optional**]**

可选参数，当区块链扩容节点需要带监控时，可通过`-M`选项来指定prometheus配置文件在nodes目录的相对路径。

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
- **启停监控脚本**: `monitor/start_monitor.sh`和`monitor/stop_monitor.sh`，用于启动和停止节点监控。

单机四节点Air版本非国密区块链的配置文件组织示例如下:

```shell
nodes/
├── monitor
│   ├── grafana # grafana配置文件
│   ├── prometheus # prometheus配置文件
│   ├── start_monitor.sh # 启动脚本，用于开启监控
│   ├── stop_monitor.sh # 停止脚本，用于停止监控
│   ├── compose.yaml # docker-compose配置文件
├── 127.0.0.1
│   ├── fisco-bcos # 二进制程序
│   ├── mtail # 二进制程序
│   ├── node0 # 节点0文件夹
│   │   ├── mtail # mtail配置文件夹
│   │   │   ├── start_mtail_monitor.sh # 启动脚本，用于启动该节点mtail程序
│   │   │   ├── stop_mtail_monitor.sh	# 停止脚本，用于停止该节点mtail程序
│   │   │   ├── node.mtail # mtail配置文件
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
