# 搭建Pro版区块链网络

标签：``Pro版区块链网络`` ``部署``

------------

FISCO BCOS 3.0支持Pro版本微服务区块链架构，Pro版本FISCO BCOS包含RPC服务、Gateway服务和节点服务，每个服务均可独立部署和扩容，本章通过单机搭建Pro版本2节点FISCO BCOS联盟链，帮助用户掌握Pro版本FISCO BCOS区块链的部署流程。请参考[这里](../../quick_start/hardware_requirements.md)使用支持的**硬件和平台**进行操作。

```eval_rst
.. note::
   - Pro版本FISCO BCOS使用 ``BcosProBuilder`` 工具进行建链和扩容等相关操作，该工具的介绍请参考 `部署工具BcosProBuilder <./pro_builder.html>`_ 
   - FISCO BCOS 3.x基于tars进行微服务构建和管理，搭建Pro版本FISCO BCOS之前，需先安装tars服务，本章介绍了docker版本tars服务的搭建流程，若需要了解更多tars部署、构建相关的信息，请参考 `这里 <https://newdoc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/README.md>`_
   - 本章基于Docker搭建tars服务，请确保拥有 ``root`` 权限
```

## 1. 安装依赖

部署工具`BcosProBuilder`依赖`python3, curl, docker, docker-compose`，根据您使用的操作系统，使用以下命令安装依赖。

**安装Ubuntu依赖(版本不小于Ubuntu18.04)**

```shell
sudo apt-get update
sudo apt-get install -y curl docker.io docker-compose python3 wget
```

**安装CentOS依赖(版本不小于CentOS 7)**

```shell
sudo yum install -y curl docker docker-compose python3 python3-devel wget
```

**安装macOS依赖**

```shell
brew install curl docker docker-compose python3 wget
```

## 2. 下载Pro版区块链构建工具BcosBuilder

```eval_rst
.. note::
   - 部署工具 ``BcosProBuilder`` 配置和使用请参考 `这里 <./pro_builder.html>`_
   - 若从github下载部署工具 ``BcosProBuilder`` 网速太慢，请尝试: curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.0.0-rc2/BcosProBuilder.tgz && tar -xvf BcosProBuilder.tgz
```

```shell
# 创建操作目录
mkdir -p ~/fisco && cd ~/fisco

# 下载Pro版区块链构建工具BcosBuilder
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.0.0-rc2/BcosProBuilder.tgz && tar -xvf BcosProBuilder.tgz

# Note: 若网速太慢，可尝试如下命令下载部署脚本:
curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.0.0-rc2/BcosProBuilder.tgz && tar -xvf BcosProBuilder.tgz

# 安装构建工具依赖包
cd BcosProBuilder && pip3 install -r requirements.txt
```
## 3. 安装、启动并配置tars服务

Pro版本的FISCO BCOS使用tars服务进行微服务构建和管理，tars服务主要包括`TarsFramework`和`TarsNode`，关于tars服务更详细的介绍请参考[这里](https://newdoc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/README.md).

**`BcosProBuilder`提供了`bridge`和`host`两种网络模式的tars docker配置，单机体验版推荐使用`bridge`网络模式的tars docker配置，生产环境推荐使用`host`网络模式的tars docker配置**。

- `bridge`网络模式的docker配置路径：`docker/bridge`，其中`docker/bridge/linux`供linux用户使用，`docker/bridge/mac`供mac用户使用
- `host`网络模式的docker配置路径：`docker/host/linux`，目前仅提供了适用于linux系统的docker配置

### 3.1 安装/启动tars服务

```eval_rst
.. note::
   - 这里采用docker安装/启动tars服务，请确保拥有root权限，并确保docker服务处于启动状态
   - 安装tars服务后，大概经过一分钟左右才可通过http://127.0.0.1:3000/访问tars网页管理平台
```

**安装tars服务：若是首次运行tars服务，请您运行如下命令安装并启动tars服务。**

```shell
# 进入BcosProBuilder目录
cd ~/fisco/BcosProBuilder

# Note: 这里需要保证docker服务处于启动状态
# 若之前已经操作过，可跳过本步骤
# 创建网段为172.25.0.0/16的bridge类型网络tars-network
python3 build_chain.py create-subnet -n tars-network -s 172.25.0.0/16

# Note: 这里需要保证docker服务处于启动状态
# linux系统：进入到docker配置文件路径(macOS系统可跳过本步骤)
cd docker/bridge/linux/framework
# macOS系统：进入到docker配置文件路径(linux系统可跳过本步骤)
cd docker/bridge/mac/framework

# 配置MYSQL密码，这里假设密码设置为FISCO
# linux系统(macOS系统可跳过本步骤)
sed -i 's/MYSQL_ROOT_PASSWORD: ""/MYSQL_ROOT_PASSWORD: "FISCO"/g' docker-compose.yml
# macOS系统(linux系统可跳过本步骤)
sed -i .bkp 's/MYSQL_ROOT_PASSWORD: ""/MYSQL_ROOT_PASSWORD: "FISCO"/g' docker-compose.yml

# 创建并启动tars管理服务
docker-compose up -d
```

**启动tars服务：若之前已经安装过tars服务，请您运行如下命令直接启动tars服务。**

```shell
# 进入BcosProBuilder目录
cd ~/fisco/BcosProBuilder

# Note: 这里需要保证docker服务处于启动状态
# linux系统：进入到docker配置文件路径(macOS系统可跳过本步骤)
cd docker/bridge/linux/framework
# macOS系统：进入到docker配置文件路径(linux系统可跳过本步骤)
cd docker/bridge/mac/framework

# 启动tars管理服务
docker-compose start
```

tars服务安装启动完成后，本机环境可通过http://127.0.0.1:3000/访问tars网页管理平台，跨机器环境可通过http://${ip}:3000/访问tars管理平台，其中`${ip}`是部署TarsFramework服务机器的物理IP。

### 3.2 配置tars服务

```eval_rst
.. note::
   - tars服务仅需配置一次，若之前已经配置过，不需要重新配置
   - 本步骤必须申请token，用于获取基于tars服务的区块链服务的操作权限
```

初次安装tars管理服务后，需要进行如下配置：

- **登录配置**：初始化管理员用户`admin`的登录密码。
- **申请token**：登录到tars网页管理平台申请用于服务构建和管理的token。


初次使用tars管理平台，输入网址http://127.0.0.1:3000/，参考下图初始化管理员密码，并进入到【admin】->【用户中心】->【token管理】申请token。


![](../../../images/tutorial/tars_config.gif)



## 4. 部署Pro版本区块链节点

Pro版本FISCO BCOS包括RPC服务、Gateway服务以及区块链节点服务BcosNodeService。

- RPC服务负责接收客户端请求，并将请求转发到节点进行处理， RPC服务可横向扩展，一个RPC服务可接入多个区块链节点服务
- Gateway服务负责跨机构区块链节点之间的网络通信，Gateway服务横向可扩展，一个Gateway服务可接入多个区块链节点服务
- 区块链节点服务BcosNodeService提供区块链相关的服务，包括共识、执行、交易上链等，节点服务通过接入到RPC服务和Gateway服务获取网络通信功能。每一个BcosNodeService表示一个群组，可以部署多个BcosNodeService扩展多群组。

关于Pro版本FISCO BCOS的总体架构设计可参考[这里](../../design/architecture.md)。


本章以在单机上部署2机构2节点区块链服务为例，介绍Pro版本FISCO BCOS搭建部署流程，对应的服务组网模式如下:

![](../../../images/tutorial/pro_topology.png)

```eval_rst
.. note::
   - 如果没有申请token，请参考【3.2 配置tars服务】申请token
   - 如果忘记了访问tars服务的token，可通过tars网页管理平台的【admin】->【用户中心】->【token管理】获取token列表
   - 部署Pro版本区块链节点之前，请先确保您的tars服务是启动的状态，安装/启动和配置tars服务请参考3.2节
```

### 4.1 下载二进制

构建Pro版本FISCO BCOS前，需要先下载二进制包，`BcosProBuilder`的提供了基于linux的静态二进制包下载功能，可部署到`tarsnode`中，下载最新二进制的命令如下：

```eval_rst
.. note::
   - 可通过 ``python3 build_chain.py -h`` 查看部署脚本使用方法
   - 二进制默认下载到 ``binary`` 目录
   - 若下载二进制比较慢，请尝试: ``python3 build_chain.py download_binary -t cdn``
```

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 运行build_chain.py脚本下载二进制，二进制包默认下载到binary目录
python3 build_chain.py download_binary

# Note: 若网速太慢，可尝试如下命令下载二进制:
python3 build_chain.py download_binary -t cdn
```

### 4.2 部署RPC服务

在建链工具BcosProBuilder目录，执行如下命令，可部署并启动2机构RPC服务，对应的RPC服务名分别为`agencyABcosRpcService`和`agencyBBcosRpcService`，ip均为`172.25.0.3`，占用的监听端口分别为`20200`和`20201`(进行本操作前，请确保机器的`20200`和`20201`端口没被占用)。

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 从conf目录拷贝配置
cp conf/config-deploy-example.toml config.toml
```

此时拷贝的`config.toml`为整个`BcosBuilder`使用的配置文件，配置详情请参考链接：[配置介绍](./pro_builder.html#id1)。

```shell

# 将步骤3.2生成的token配置到config.toml的tars_token字段
# 这里生成的token是eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls，实际使用时，请替换为实际申请的token
# linux系统(macOS系统跳过本步骤):
sed -i 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml
# macos系统(linux系统跳过本步骤):
sed -i .bkp 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml

#部署并启动RPC服务
python3 build_chain.py chain -o deploy -t rpc
```

执行上述命令后，当脚本输出`deploy service success, type: rpc`时，则说明RPC服务部署成功，详细日志输出如下：

```shell
=========================================================
----------- deploy service, type: rpc -----------
=========================================================
----------- generate service config -----------
* generate service config for 172.25.0.3 : agencyABcosRpcService
* generate config for the rpc service
* generate generated/rpc/chain0/172.25.0.3/agencyABcosRpcService/config.ini.tmp
* generate cert, output path: generated/rpc/chain0/172.25.0.3/agencyABcosRpcService
* generate sdk cert, output path: generated/rpc/chain0/172.25.0.3/agencyABcosRpcService
* generate config for the rpc service success
gen configuration for service agencyABcosRpcService success
* generate service config for 172.25.0.3 : agencyBBcosRpcService
* generate config for the rpc service
* generate generated/rpc/chain0/172.25.0.3/agencyBBcosRpcService/config.ini.tmp
* generate cert, output path: generated/rpc/chain0/172.25.0.3/agencyBBcosRpcService
* generate sdk cert, output path: generated/rpc/chain0/172.25.0.3/agencyBBcosRpcService
* generate config for the rpc service success
gen configuration for service agencyBBcosRpcService success
----------- generate service config success -----------
=========================================================
deploy_service to 172.25.0.3, app: chain0, name: agencyABcosRpcService
deploy service agencyABcosRpcService
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: ca.crt
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: ssl.key
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: ssl.crt
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: config.ini
upload tar package generated/./agencyABcosRpcService.tgz success, config id: 14
deploy_service to 172.25.0.3, app: chain0, name: agencyBBcosRpcService
deploy service agencyBBcosRpcService
* add config for service agencyBBcosRpcService, node: 172.25.0.3, config: ca.crt
* add config for service agencyBBcosRpcService, node: 172.25.0.3, config: ssl.key
* add config for service agencyBBcosRpcService, node: 172.25.0.3, config: ssl.crt
* add config for service agencyBBcosRpcService, node: 172.25.0.3, config: config.ini
upload tar package generated/./agencyBBcosRpcService.tgz success, config id: 15
----------- deploy service success, type: rpc -----------
=========================================================
```

部署过程中生成的RPC服务相关的配置位于`generated/rpc/${chainID}`目录，具体如下：

```shell
$ tree generated/rpc/chain0
generated/rpc/chain0
├── 172.25.0.3 
│   ├── agencyABcosRpcService # 机构A的RPC服务目录
│   │   ├── config.ini.tmp    # 机构A的RPC服务的配置文件
│   │   ├── sdk               # SDK证书目录，SDK客户端可从本目录拷贝证书连接RPC服务
│   │   │   ├── ca.crt
│   │   │   ├── cert.cnf
│   │   │   ├── sdk.crt
│   │   │   └── sdk.key
│   │   └── ssl               # RPC服务证书目录
│   │       ├── ca.crt
│   │       ├── cert.cnf
│   │       ├── ssl.crt
│   │       └── ssl.key
│   └── agencyBBcosRpcService # 机构B的RPC服务配置目录
│       ├── config.ini.tmp    # 机构B的RPC服务的配置文件
│       ├── sdk               # SDK证书目录，SDK客户端从本目录拷贝证书连接RPC服务
│       │   ├── ca.crt
│       │   ├── cert.cnf
│       │   ├── sdk.crt
│       │   └── sdk.key
│       └── ssl              # RPC服务证书目录
│           ├── ca.crt
│           ├── cert.cnf
│           ├── ssl.crt
│           └── ssl.key
└── ca                       # CA证书目录，主要包括CA证书、CA私钥，请妥善保存CA证书和CA私钥，RPC服务的扩容等操作均需要提供CA证书和CA私钥
    ├── ca.crt
    ├── ca.key
    ├── ca.srl
    └── cert.cnf
```

RPC服务启动成功后，可在tars网页管理平台看到服务列表`agencyABcosRpcService`和`agencyBBcosRpcService`，且每个服务均是`active`的状态：
![](../../../images/tutorial/rpc_service.png)


```eval_rst
.. note::
   - 如果忘记了访问tars服务的token，可通过tars网页管理平台的【admin】->【用户中心】->【token管理】获取token列表
   - 部署Pro版本区块链节点之前，请先确保您的tars服务是启动的状态，安装/启动和配置tars服务请参考3.2节
   - **请妥善保存部署服务过程中生成的RPC服务CA证书和CA私钥，用于SDK证书申请、RPC服务扩容等操作**
```

### 4.3 部署Gateway服务

RPC服务部署完成后，需要再部署Gateway服务，用于建立机构之间的网络连接。在建链工具BcosProBuilder目录下，执行如下命令，可部署并启动2机构Gateway服务，对应的Gateway服务名分别为`agencyABcosGatewayService`和`agencyBBcosGatewayService`，ip均为`172.25.0.3`，占用的端口分别为`30300`和`30301`(进行本操作前，请确保机器的`30300`和`30301`端口没被占用)。

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 部署并启动Gateway服务
python3 build_chain.py chain -o deploy -t gateway
```

执行上述命令后，当脚本输出`deploy service success, type: gateway`时，则说明RPC服务部署成功，详细日志输出如下：

```shell
=========================================================
----------- deploy service, type: gateway -----------
=========================================================
----------- generate service config -----------
* generate service config for 172.25.0.3 : agencyABcosGatewayService
* generate config for the gateway service
* generate generated/gateway/chain0/172.25.0.3/agencyABcosGatewayService/config.ini.tmp
* generate cert, output path: generated/gateway/chain0/172.25.0.3/agencyABcosGatewayService
* generate gateway connection file: generated/gateway/chain0/172.25.0.3/agencyABcosGatewayService/nodes.json.tmp
* generate config for the gateway service success
gen configuration for service agencyABcosGatewayService success
* generate service config for 172.25.0.3 : agencyBBcosGatewayService
* generate config for the gateway service
* generate generated/gateway/chain0/172.25.0.3/agencyBBcosGatewayService/config.ini.tmp
* generate cert, output path: generated/gateway/chain0/172.25.0.3/agencyBBcosGatewayService
* generate gateway connection file: generated/gateway/chain0/172.25.0.3/agencyBBcosGatewayService/nodes.json.tmp
* generate config for the gateway service success
gen configuration for service agencyBBcosGatewayService success
----------- generate service config success -----------
=========================================================
deploy_service to 172.25.0.3, app: chain0, name: agencyABcosGatewayService
deploy service agencyABcosGatewayService
* add config for service agencyABcosGatewayService, node: 172.25.0.3, config: nodes.json
* add config for service agencyABcosGatewayService, node: 172.25.0.3, config: ca.crt
* add config for service agencyABcosGatewayService, node: 172.25.0.3, config: ssl.key
* add config for service agencyABcosGatewayService, node: 172.25.0.3, config: ssl.crt
* add config for service agencyABcosGatewayService, node: 172.25.0.3, config: config.ini
upload tar package generated/./agencyABcosGatewayService.tgz success, config id: 14
deploy_service to 172.25.0.3, app: chain0, name: agencyBBcosGatewayService
deploy service agencyBBcosGatewayService
* add config for service agencyBBcosGatewayService, node: 172.25.0.3, config: nodes.json
* add config for service agencyBBcosGatewayService, node: 172.25.0.3, config: ca.crt
* add config for service agencyBBcosGatewayService, node: 172.25.0.3, config: ssl.key
* add config for service agencyBBcosGatewayService, node: 172.25.0.3, config: ssl.crt
* add config for service agencyBBcosGatewayService, node: 172.25.0.3, config: config.ini
upload tar package generated/./agencyBBcosGatewayService.tgz success, config id: 15
----------- deploy service success, type: gateway -----------
=========================================================
```

部署过程中生成的RPC服务相关的配置位于`generated/gateway/${chainID}`目录，具体如下：

```shell
$ tree generated/gateway/chain0
generated/gateway/chain0
├── 172.25.0.3
│   ├── agencyABcosGatewayService # 机构A的Gateway服务配置路径
│   │   ├── config.ini.tmp        # 机构A的Gateway配置文件
│   │   ├── nodes.json.tmp        # 机构A的Gateway服务连接配置
│   │   └── ssl                   # 机构A的Gateway服务证书配置
│   │       ├── ca.crt
│   │       ├── cert.cnf
│   │       ├── ssl.crt
│   │       └── ssl.key
│   └── agencyBBcosGatewayService # 机构B的Gateway服务配置路径
│       ├── config.ini.tmp
│       ├── nodes.json.tmp
│       └── ssl
│           ├── ca.crt
│           ├── cert.cnf
│           ├── ssl.crt
│           └── ssl.key
└── ca                          # Gateway服务的根证书配置，请妥善保存根证书和根证书私钥
    ├── ca.crt
    ├── ca.key
    ├── ca.srl
    └── cert.cnf
```

```eval_rst
.. note::
   - 本步骤在步骤4.2的基础上进行
   - 部署Pro版本区块链节点之前，请先确保您的tars服务是启动的状态，安装/启动和配置tars服务请参考3.2节
   - **请妥善保存部署服务过程中生成的RPC服务CA证书和CA私钥，用于Gateway服务扩容等操作**
```

Gateway服务启动成功后，可在tars网页管理平台看到服务列表`agencyABcosGatewayService`和`agencyBBcosGatewayService`，且每个服务均是`active`的状态：
![](../../../images/tutorial/gateway_service.png)

### 4.4 部署区块链节点服务

RPC服务和Gateway服务均部署完成后，可部署区块链节点服务。在建链工具BcosProBuilder目录下，执行如下命令，可部署并启动2机构2节点区块链服务，对应的服务名分别为`group0node00BcosNodeService`和`group0node10BcosNodeService`，链ID均为`chain0`，群组ID均为`group0`。

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 部署并启动区块链节点服务
python3 build_chain.py chain -o deploy -t node
```
执行上述命令后，当脚本输出`deploy all nodes of the given group success`时，则说明区块链节点服务部署成功，详细日志输出如下：

```shell
=========================================================
----------- deploy all nodes of the given group -----------
generate config for chain = chain0, group = group0
* generate pem file for group0node00BcosNodeService
	- pem_path: generated/chain0/group0/172.25.0.3/group0node00BcosNodeService
	- node_id_path: generated/node.nodeid
	- sm_crypto: 0
* generate pem file for group0node10BcosNodeService
	- pem_path: generated/chain0/group0/172.25.0.3/group0node10BcosNodeService
	- node_id_path: generated/node.nodeid
	- sm_crypto: 0
* generate genesis config for group0node00BcosNodeService
	 path: generated/chain0/group0/172.25.0.3/group0node00BcosNodeService/config.genesis.tmp
* generate ini config for service group0node00BcosNodeService
	config path: generated/chain0/group0/172.25.0.3/group0node00BcosNodeService/config.ini.tmp
* generate genesis config for group0node10BcosNodeService
	 path: generated/chain0/group0/172.25.0.3/group0node10BcosNodeService/config.genesis.tmp
* generate ini config for service group0node10BcosNodeService
	config path: generated/chain0/group0/172.25.0.3/group0node10BcosNodeService/config.ini.tmp
deploy services for all the group nodes
deploy service group0node00BcosNodeService
deploy service group0node00BcosNodeService
upload tar package generated/./group0node00BcosNodeService.tgz success, config id: 16
deploy service group0node10BcosNodeService
deploy service group0node10BcosNodeService
upload tar package generated/./group0node10BcosNodeService.tgz success, config id: 17
----------- deploy all nodes of the given group success -----------
=========================================================
```
部署过程中生成的RPC服务相关的配置位于`generated/${chainID}`(`chainID`默认为`chain`)目录，具体如下：

```shell
$ tree generated/chain0
generated/chain0
└── group0
    └── 172.25.0.3
        ├── group0node00BcosNodeService
        │   ├── config.genesis.tmp       # 创世块配置
        │   ├── config.ini.tmp           # 区块链节点配置
        │   ├── node.nodeid
        │   └── node.pem                 # 区块链节点服务签名私钥
        └── group0node10BcosNodeService
            ├── config.genesis.tmp
            ├── config.ini.tmp
            ├── node.nodeid
            └── node.pem
```

```eval_rst
.. note::
   - 建议部署RPC和Gateway服务之后再部署区块链节点服务
   - 部署Pro版本区块链节点之前，请先确保您的tars服务是启动的状态，安装/启动和配置tars服务请参考3.2节
```

区块链节点服务启动成功后，可在tars网页管理平台看到服务列表`group0node00BcosNodeService`和`group0node10BcosNodeService`，且每个服务均是`active`的状态：
![](../../../images/tutorial/chain_service.png)

## 5. 配置及使用控制台

控制台同时适用于Pro版本和Air版本的FISCO BCOS区块链，且在体验上完全一致。Pro版本区块链体验环境搭建完毕后，可配置并使用控制台向Pro版本区块链发送交易。
### 5.1 安装依赖

```eval_rst
.. note::
   - 控制台的配置方法和命令请参考 `这里 <../../develop/console/console_config.html>`_
```

使用控制台之前，需先安装java环境：

```shell
# ubuntu系统安装java
sudo apt install -y default-jdk

#centos系统安装java
sudo yum install -y java java-devel
```

### 5.2 下载、配置并使用控制台

**步骤1：下载控制台**

```shell
cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v3.0.0-rc2/download_console.sh && bash download_console.sh
```
```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `cd ~/fisco && curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh`
```

**步骤2：配置控制台**

- 拷贝控制台配置文件

若RPC服务未采用默认端口，请将文件中的20200替换成RPC服务监听端口。

```shell
# 最新版本控制台使用如下命令拷贝配置文件
cp -n console/conf/config-example.toml console/conf/config.toml
```

- 配置控制台证书

```shell
# 可通过命令 find . -name sdk找到所有SDK证书路径
cp -r BcosProBuilder/generated/rpc/chain/172.25.0.3/agencyBBcosRpcService/sdk/* console/conf
```

**步骤3：启动并使用控制台**

```shell
cd ~/fisco/console && bash start.sh
```

输出下述信息表明启动成功 否则请检查conf/config.toml中节点端口配置是否正确以及是否配置了SDK证书：

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.0.0-rc2)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=============================================================================================
[group0]: />
```

- 用控制台获取信息

```shell
# 获取网络连接信息：
[group0]: /> getPeers
PeersInfo{
    p2pNodeID='3082010a0282010100c1d64abf0af11ceaa69b237090a5078ccbc122aedbf93486100ae65cb38cbf2a6969b80f2beca1abba7f0c1674876b332380a4b76387d62445ba8da7190b54850ed8c3fb4d6f6bafbd4744249a55805c0d804db9aa0f105c44c3381de20c763469892fc11a2bc8467c523592c9b2738069d6beb4cfb413f90e0be53205eca1cf3618100c625667f0592fd682aabe9cfbca7f7c53d79eeb5961ed9f144681b32c9fa55fc4d80b5ffbf32a9f71e900bc1c9a92ce0a485bb1003a915f9215bd7c42461cd52d1b2add644e8c1c273aa3668d4a707771b1a99d6bfcbfdf28be5b9c619eefb0c182ea7e666c5753c79499b1959df17ad5bd0996b9d7f0d62aa53d2b450203010001',
    endPoint='0.0.0.0:30301',
    groupNodeIDInfo=[
        NodeIDInfo{
            group='group',
            nodeIDList=[
                4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a
            ]
        }
    ],
    peers=[
        PeerInfo{
            p2pNodeID='3082010a0282010100cd8978651421e2861330242b1736a1814e4f2654476ae177c81494c52aef4e30821689029bff53a6b8d671967e5ca52d40c821775013f663ce003acd40a037a2cf19ed2bb20b92cb519636aa7402499df8e9324a0120cd199de97330f5942cca54ebfd5abb425f5c9128cd7bfa3956281614d492c9858d24ddea336651926869cde4e329550708b78d657019ee19ab9024305b32ffa96ed81d04be9124dc74d8d320b570b446a0b95af051fecbf3e49a3d64daea8b044ae09e891a89e59e87ca3db65bc67c16b6787f804b19a158e957a7b8d734159677837d2cd28f85cc79559d38f64e757c4fa7246604542c01cec2a3805066c4d026b9a90091b0bf67efe30203010001',
            endPoint='172.25.0.3:30300',
            groupNodeIDInfo=[
                NodeIDInfo{
                    group='group',
                    nodeIDList=[
                        8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3
                    ]
                }
            ]
        }
    ]
}

# 获取节点列表信息
[group0]: /> getGroupPeers
peer0: 4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a
peer1: 8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3

[group0]: /> getSealerList
[
    Sealer{
        nodeID='8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3',
        weight=1
    },
    Sealer{
        nodeID='4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a',
        weight=1
    }
]
```

### 5.3. 部署和调用合约

**步骤1：编写HelloWorld合约**

HelloWorld合约提供了两个接口`get()`和`set()`，用于获取/设置合约变量`name`，合约内容如下：

```c++
pragma solidity>=0.4.24 <0.6.11;
contract HelloWorld {
    string name;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public {
        name = n;
    }
}
```

**步骤2: 部署HelloWorld合约**

为了方便用户快速体验，HelloWorld合约已经内置于控制台中，位于控制台目录`contracts/solidity/HelloWorld。sol`，参考下面命令部署：

```shell
# 在控制台输入以下指令 部署成功则返回合约地址
[group0]: /> deploy HelloWorld
transaction hash: 0x0fe66c42f2678b8d041624358837de34ac7db195abb6f5a57201952062190590
contract address: 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1
currentAccount: 0x537149148696c7e6c3449331d77ddfaabc3c7a75

# 查看当前块高
[group0]: /> getBlockNumber
1
```

**步骤3. 调用HelloWorld合约**

```shell
# 调用get接口获取name变量，此处的合约地址是deploy指令返回的地址
[group0]: /> call HelloWorld 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, World!)
---------------------------------------------------------------------------------------------

# 查看当前块高，块高不变，因为get接口不更改账本状态
[group0]: /> getBlockNumber
1

# 调用set方法设置name
[group0]: /> call HelloWorld 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 set "Hello, FISCO BCOS"
transaction hash: 0x2f7c85c2c59a76ccaad85d95b09497ad05ca7983c5ec79c8f9d102d1c8dddc30
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
---------------------------------------------------------------------------------------------
Event logs
Event: {}

# 查看当前块高，因为set接口修改了账本状态，块高增加到2
[group0]: /> getBlockNumber
2

# 退出控制台
[group0]: /> exit
```
