# 轻节点部署工具(build_chain.sh)

标签：``build_chain`` ``搭建Air版区块链网络``

----

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. important::
    本部署工具 build_chain.sh 脚本目标是让用户最快的使用FISCO BCOS 轻节点。
```

FISCO BCOS提供了`build_chain.sh`脚本帮助用户快速搭建FISCO BCOS轻节点。

本文仅介绍如何使用build_chain.sh搭建轻节点，如果要查询build_chian.sh的完整用法，`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tutorial/air/build_chain.html>`

## 1. 编译轻节点

`请查看编译文档 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compile_binary.html>`

cmake配置时，增加选项-DWITH_LIGHTNODE=ON，轻节点程序将生成到build/lightnode/fisco-bcos-lightnode目录中。

## 2. 搭建轻节点

`build_chain.sh`脚本用于快速轻节点，脚本的源码位于[github源码](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh), [gitee源码](https://gitee.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh)。

```shell
# 键入bash build_chain.sh -h展示脚本用法及参数
$ bash build_chain.sh
Usage:
    -C <Command>                        [Optional] the command, support 'deploy' and 'expand' now, default is deploy
    -g <group id>                       [Optional] set the group id, default: group0
    -I <chain id>                       [Optional] set the chain id, default: chain0
    -v <FISCO-BCOS binary version>      [Optional] Default is the latest v3.1.1
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -L <fisco bcos lightnode exec>      [Optional] fisco bcos light node executable
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
    -R <Serial_mode>                    [Optional] Whether to use serial execute,default is false
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

### **`L`选项[**Optional**]**
用于配置开启FISCO BCOS轻节点模式，需要开启轻节点时，使用L选项指定轻节点程序的路径，build_chain.sh将自动生成轻节点到o选项指定的目录中。

案例：

```shell
# 两个节点的P2P服务分别占用30300和30301端口
# RPC服务分别占用20200和20201端口
# 轻节点将生成到nodes/lightnode中
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2 -L /home/user/FISCO-BCOS/build/lightnode/fisco-bcos-lightnode
```

build_chain.sh将生成nodes目录，轻节点位于nodes目录的lightnode中。

## 3. 轻节点配置文件组织结构

`build_chain`生成的节点配置主要如下：

- **根证书和根证书私钥**: 位于指定的配置生成目录的`ca`文件夹。
- **节点连接证书**: 每个节点`conf`目录下均存放节点的网络连接证书，非国密节点证书和私钥主要包括`ca.crt, ssl.crt, sslkey`，国密节点证书和私钥主要包括`sm_ca.crt, sm_ssl.crt, sm_enssl.crt, sm_enssl.key, sm_ssl.key`。
- **节点签名私钥**: 节点`conf`目录下的`node.pem`，主要位于共识模块的签名。
- **SDK连接证书**: 由`build_chain.sh`生成，客户端可拷贝该证书与节点建立SSL连接。
- **节点配置文件**: 节点目录下的`config.ini`和`config.genesis`配置，前者主要配置链信息，后者主要配置创世块信息，轻节点的信息要与全节点保持一致，具体可参考[Air版本区块链节点配置介绍](./config.md)。
- **启停脚本**: `start.sh`和`stop.sh`，用于启动和停止节点。

轻的配置文件组织示例如下:

```shell
lightnode/
├── conf # 证书配置，与全节点相同
│   ├── ca.crt
│   ├── cert.cnf
│   ├── node.nodeid
│   ├── node.pem
│   ├── ssl.crt
│   └── ssl.key
├── config.genesis # 创世块配置，与全节点相同
├── config.ini # 轻节点配置
├── data # 轻节点数据目录
├── fisco-bcos-lightnode # 轻节点程序
├── log # 轻节点日志
└── nodes.json # 轻节点P2P配置，填写全节点的ip和端口列表
```
## 4. 启动轻节点

确保全节点已经全部启动，进入lightnode目录，执行：

```shell
./fisco-bcos-lightnode
```

即可启动轻节点，随后按类似全节点的方法，使用控制台或SDK连接轻节点来进行使用，使用体验与全节点基本类似。

## 5. 轻节点限制

轻节点不存储全量账本数据，意味着大部分信息要从全节点获取，轻节点保证从全节点获取的信息可信不可篡改，如果轻节点连接的全节点发生故障不可用，轻节点也将无法使用。

轻节点支持以下RPC接口：

- getBlockByHash
- getBlockNumber
- getBlockByNumber
- getBlockHashByNumber
- getBlockHeaderByHash
- getBlockHeaderByNumber
- getTransactionByHash
- getTransactionReceipt
- getTransactionByHashWithProof
- getTransactionReceiptByHashWithProof
- call
- sendTransaction