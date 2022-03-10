# 部署工具BcosProBuilder

标签：``Pro版区块链网络`` ``部署工具``

------------

```eval_rst
.. important::
    部署工具BcosProBuilder目标是让用户最快的部署和使用FISCO BCOS Pro版本区块链，其功能包括：部署/启动/关闭/更新/扩容RPC服务、Gateway服务以及区块链节点服务。
```

FISCO BCOS提供了`BcosProBuilder`工具帮助用户快速部署、启停、更新和扩容FISCO BCOS Pro版本区块链联盟链，该工具可直接从FISCO BCOS的release tags中下载。

## 1. 配置介绍

`BcosProBuilder`在`conf`目录下提供了一些配置模板，用于帮助用户快速完成Pro版本区块链的部署、扩容。本章从tars服务配置项、区块链部署配置项、区块链扩容配置项三个角度详细介绍`BcosProBuilder`的配置项。

### 1.1 tars服务配置项

- `tars_url`: 访问tars网页控制台的url，默认为`http://127.0.0.1:3000`。
- `tars_token`: 访问tars服务的token，可通过tars网页控制台的【admin】->【用户中心】->【token管理】进行token申请和查询。
- `tars_pkg_dir`: 放置Pro版本二进制包的路径，若配置了该配置项，默认会从指定的目录下获取FISCO BCOS Pro版本二进制进行服务部署、扩容等操作。

下面是tars服务配置项的示例：

```shell
[tars]
tars_url = "http://127.0.0.1:3000"
tars_token = ""
tars_pkg_dir = ""
```

### 1.2 区块链服务部署配置

区块链服务部署相关的配置项主要包括链配置项、RPC/Gateway服务配置项以及区块链节点服务配置项，其配置模板位于`BcosProBuilder`的`conf/config-deploy-example.toml`路径下。

**链配置项**

链配置项位于配置`[chain]`中，主要包括：

- `chain_id`: 区块链服务所属的链的ID，默认为`chain0`，**不能包括除字母和数字之外的所有特殊字符**。
- `rpc_sm_ssl`: RPC服务与SDK客户端之间采用的SSL连接类型，若设置为`false`，表明采用RSA加密连接；若设置为`true`，表明采用国密SSL连接，默认为`false`。
- `gateway_sm_ssl`: Gateway服务之间的SSL连接类型，设置为`false`表明采用RSA加密连接；设置为`true`表明采用国密SSL连接，默认为`false`

链ID为`chain0`, RPC与SDK之间、Gateway服务之间均采用RSA加密连接的配置项如下：

```shell
[chain]
chain_id="chain0"
rpc_sm_ssl=false
gateway_sm_ssl=false
```

**RPC服务配置项**

```eval_rst
.. note::
   - 当部署一个RPC服务到多台机器时，请确保这些机器都安装了tarsnode服务，tarsnode部署请参考 `这里 <https://newdoc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/node.md>`_
```

RPC服务的配置项位于`[[chain.rpc]]`中，一条链可部署多个RPC服务，主要配置项包括：

- `name`: RPC服务的名字，**不能包括除字母和数字之外的所有特殊字符**。
- `deploy_ip`: RPC服务的部署IP，若配置多个，则会在多台机器上部署RPC服务，达到平行扩展的目标。
- `listen_ip`: RPC服务的监听IP，默认为`0.0.0.0`。
- `listen_port`: RPC服务的监听端口，默认为`20200`。
- `thread_count`: RPC服务进程内的工作线程数目，默认为`4`。
- `gateway_service_name`: RPC服务所连接的Gateway服务名称，一般一个机构内会部署1个RPC服务、1个Gateway和多个区块链节点服务

部署于`172.25.0.3`、可连接到服务名为`agencyABcosGatewayService`的Gateway服务，服务名为`agencyABcosRpcService`的RPC服务配置示例如下：

```toml
[[chain.rpc]]
# 服务名称
name="agencyABcosRpcService"
# 服务部署IP，支持多个，单须确保每个IP对应的机器上均安装了tarsnode服务
deploy_ip=["172.25.0.3"]
# RPC服务监听IP
listen_ip="0.0.0.0"
# RPC服务监听端口
listen_port=20200
# 工作线程数目
thread_count=4
# RPC服务对应的Gateway服务名称
gateway_service_name = "agencyABcosGatewayService"
```

**Gateway服务配置项**

RPC服务的配置项位于`[[chain.gateway]]`中，一条链可部署多个RPC服务，主要配置项包括：

- `name`: Gateway服务名称，**不能包括除字母和数字之外的所有特殊字符**。
- `deploy_ip`: Gateway服务的部署IP，若配置多个，则会在多台机器上部署Gateway服务，达到平行扩展的目标。
- `listen_ip`: Gateway服务的监听IP，默认为`0.0.0.0`。
- `listen_port`: Gateway服务的监听端口，默认为`30300`。
- `peers`: 所有Gateway服务的连接信息。
- `rpc_service_name`: Gateway服务可连接的RPC服务名称。

部署于`172.25.0.3`、可连接到服务名为`agencyABcosRpcService`的RPC服务，服务名为`agencyABcosGatewayService`的Gateway服务配置示例如下：

```toml
[[chain.gateway]]
# Gateway服务名
name="agencyABcosGatewayService"
# Gateway服务部署IP
deploy_ip=["172.25.0.3"]
# 监听IP
listen_ip="0.0.0.0"
# 监听端口
listen_port=30300
# 连接信息
peers=["172.25.0.3:30300", "172.25.0.3:30301"]
# Gateway服务对应的RPC服务名
rpc_service_name = "agencyABcosRpcService"
```

**区块链节点服务配置项：群组配置**

```eval_rst
.. note::
    区块链群组配置不可修改。
```

FISCO BCOS Pro版本区块链中每个区块链节点服务均属于一个群组，因此部署区块链节点前，首先需配置群组信息，群组配置项位于`[group]`中，具体如下：

- `group_id`: 区块链节点所属的群组ID，默认为`group`。
- `vm_type`: 区块链节点运行的虚拟机类型，目前支持`evm`和`wasm`两种类型，且一个群组仅可运行一种类型的虚拟机，不可以部分节点运行EVM虚拟机、部分节点运行WASM虚拟机。
- `sm_crypto`: 节点账本是否采用国密类型签名、验签、哈希、加密算法，默认为`false`。
- `auth_check`: 是否开启权限治理模式，权限使用文档请参考链接：[权限治理使用指南](../../develop/committee_usage.md)。
- `init_auth_address`: 开启权限治理时，指定的初始化治理委员账号地址，权限使用文档请参考链接：[权限治理使用指南](../../develop/committee_usage.md)。

群组配置中还包括了创世块相关的配置：
- `leader_period`: 每个leader可以连续打包的区块数目，默认为5。
- `block_tx_count_limit`: 每个区块中可包含的最大交易数目，默认为1000。
- `consensus_type`: 共识算法类型，目前仅支持`pbft`共识算法。
- `gas_limit`: 每笔交易运行时消耗的gas上限，默认为300000000。

```toml
[group]
# 群组ID
group_id="group0"
# 虚拟机类型
vm_type="evm"
# 是否时国密账本
sm_crypto=false

# 创世块相关配置项
leader_period = 5
block_tx_count_limit = 1000
consensus_type = "pbft"
gas_limit = "300000000"
```

**区块链节点服务配置项：部署配置**

区块链节点服务部署配置项位于`[[group.deploy_info]]`中，具体如下：
- `node_name`: 节点服务名，在服务部署的场景下可不配置，**若配置了该选项，须确保不同节点服务的服务名不重复**。
- `deploy_ip`: 节点服务部署ip。
- `node_count`: 部署的区块链节点服务数目。
- `rpc_service_name`: 区块链节点服务对应的RPC服务名，**须确保RPC服务存在，不然无法正常接收客户端请求**。
- `gateway_service_name`: 区块链节点服务对应的Gateway服务名，**须确保Gateway服务存在，且连接正常，不然无法进行跨节点、跨机构的网络访问**。

区块链节点服务配置示例如下：
```toml
[[group.deploy_info]]
# 部署的场景下，可不配置该选项
# node_name = "node0"
# 区块链节点服务部署IP
deploy_ip = "172.25.0.3"
# 部署的区块链节点服务数目
node_count=1
# 区块链节点服务对应的RPC服务名
rpc_service_name = "agencyABcosRpcService"
# 区块链节点服务对应的Gateway服务名
gateway_service_name = "agencyABcosGatewayService"
```

### 1.3 区块链服务扩容配置

`BcosProBuilder`提供了区块链节点服务扩容、RPC/Gateway服务扩容功能，区块链节点服务扩容的配置模板在`conf/config-node-expand-example.toml`路径下，RPC/Gateway服务扩容配置模板在`conf/config-service-expand-example.toml`路径下。

**RPC服务扩容配置**

FISCO BCOS Pro版本区块链中，一个RPC服务可包含多个RPC服务节点，`BcosProBuilder`提供了RPC服务扩容功能，可在已有RPC服务基础上扩容RPC服务节点，其配置选项主要位于`[chain]`和`[[chain.rpc]]`配置下，主要包括：

- `[chain].chain_id`: 扩容的RPC服务所属的链ID。
- `[chain].rpc_sm_ssl`: 扩容的RPC服务与SDK客户端之间是否采用国密SSL连接。
- `[chain].rpc_ca_cert_path`: 指定扩容的RPC服务的CA证书和CA私钥所在路径。
- `[[chain.rpc]].name`: 扩容的RPC服务名。
- `[[chain.rpc]].deploy_ip`: 扩容的RPC服务的部署IP。
- `[[chain.rpc]].expanded_ip`: 指定被扩容的RPC服务的IP，扩容时，会从该IP对应的RPC服务拉取配置文件，若RPC服务已经部署到多台机器上，从中随机选取一个IP作为`expanded_ip`即可。
- `[[chain.rpc]].listen_ip`: 指定扩容的RPC服务的监听IP。
- `[[chain.rpc]].listen_port`: 指定扩容的RPC服务的监听端口。
- `[[chain.rpc]].thread_count`: 扩容的RPC服务的工作线程数目。
- `[[chain.rpc]].gateway_service_name`: 扩容的RPC服务对应的Gateway服务名。

扩容RPC服务`agencyABcosRpcService`到`172.25.0.5`的配置示例如下：

```toml
[chain]
chain_id="chain0"
rpc_sm_ssl=false
rpc_ca_cert_path="generated/rpc/chain0/ca"

[[chain.rpc]]
name="agencyABcosRpcService"
deploy_ip=["172.25.0.5"]
expanded_ip = "172.25.0.3"
listen_ip="0.0.0.0"
listen_port=20200
thread_count=4
gateway_service_name = "agencyABcosGatewayService"
```

**Gateway服务扩容配置**

类似于RPC服务，Gateway服务的扩容配置选项主要位于`[chain]`和`[[chain.gateway]]`配置下，主要包括：

- `[chain].chain_id`: 扩容的Gateway服务所属的链ID。
- `[chain].gateway_sm_ssl`: 扩容的Gateway服务与SDK客户端之间是否采用国密SSL连接。
- `[chain].gateway_ca_cert_path`: 指定扩容的Gateway服务的CA证书和CA私钥所在路径。
- `[[chain.gateway]].name`: 扩容的Gateway服务名。
- `[[chain.gateway]].deploy_ip`: 扩容的Gateway服务的部署IP。
- `[[chain.gateway]].expanded_ip`: 指定被扩容的Gateway服务的IP，扩容时会从该IP对应的Gateway服务拉取配置文件，若Gateway服务已经部署到多台机器上，从中随机选取一个IP作为`expanded_ip`即可。
- `[[chain.gateway]].listen_ip`: Gateway服务节点的监听IP，默认为`0.0.0.0`。
- `[[chain.gateway]].listen_port`: Gateway服务的监听端口，默认为`30300`。
- `[[chain.gateway]].peers`: Gateway服务的连接信息，须配置所有Gateway服务节点的连接IP和连接端口信息。
- `[[chain.gateway]].rpc_service_name`: Gateway服务对应的RPC服务名。

扩容Gateway服务`agencyABcosGatewayService`到`172.25.0.5`的配置示例如下：

```toml
[chain]
chain_id="chain0"
gateway_sm_ssl=false
gateway_ca_cert_path="generated/gateway/chain0/ca"

[[chain.gateway]]
name="agencyABcosGatewayService"
deploy_ip=["172.25.0.5"]
expanded_ip = "172.25.0.3"
listen_ip="0.0.0.0"
listen_port=30300
peers=["172.25.0.3:30300", "172.25.0.3:30301", "172.25.0.5:30300"]
rpc_service_name = "agencyABcosRpcService"
```

**区块链节点扩容配置**

`BcosProBuilder`提供了区块链节点扩容功能，可为指定群组扩容新的区块链节点服务，区块链节点扩容配置模板位于`conf/config-node-expand-example.toml`路径下，主要包括**链配置**和**扩容部署配置**，具体如下：

- `[chain].chain_id`: 扩容的区块链节点所属的链ID。
- `[[group.deploy_info]].node_name`: 扩容的区块链节点服务名，**不能和已有的区块链节点服务名冲突**。
- `[[group.deploy_info]].deploy_ip`: 扩容的区块链节点服务部署IP。
- `[[group.deploy_info]].expanded_service`: 指定作为扩容模板的已有的区块链节点服务名，扩容时会从该区块链节点服务拉取配置文件，**若指定群组已经有多个区块链节点服务，从中随机选取一个服务作为`expanded_service`即可**。
- `[[group.deploy_info]].node_count`: 扩容的区块链节点数目。

以群组ID为`group`的区块链节点服务`group0node00BcosNodeService`为模板，扩容节点名为`node3`的区块链节点服务到`172.25.0.5`的配置示例如下：
```toml
[[group.deploy_info]]
node_name = "node3"
deploy_ip = "172.25.0.5"
expanded_service = "group0node00BcosNodeService"
node_count=1
```

## 2. 使用介绍

可使用`python3 build_chain.py -h`查看`BcosProBuilder`的使用方法: 

```shell
----------- help for subcommand 'download_binary' -----------
usage: build_chain.py download_binary [-h] [-t TYPE] [-v VERSION] [-p PATH]

Download binary, eg: python3 build_chain.py download_binary -t cdn

optional arguments:
  -h, --help            show this help message and exit
  -t TYPE, --type TYPE  [Optional] Specify the source of the download, support cdn,git now, default type is git
  -v VERSION, --version VERSION
                        [Optional] Specify the version of the binary, default is v3.0.0-rc2
  -p PATH, --path PATH  [Optional] Specify the path of the binary, default is binary

----------- help for subcommand 'chain' -----------
usage: build_chain.py chain [-h] -o OP [-c CONFIG] [-t TYPE]

e.g:
* deploy node: python3 build_chain.py chain -o deploy -t node
* deploy rpc: python3 build_chain.py chain -o deploy -t rpc
* deploy gateway: python3 build_chain.py chain -o deploy -t gateway

optional arguments:
  -h, --help            show this help message and exit
  -o OP, --op OP        [required] specify the command:
                        * command list: gen-config, upload, deploy, upgrade, undeploy, expand, start, stop
  -c CONFIG, --config CONFIG
                        [optional] the config file, default is config.toml:
                         * config to deploy chain example: conf/config-deploy-example.toml
                         * config to expand node example: conf/config-node-expand-example.toml
                         * config to expand rpc/gateway example: conf/config-service-expand-example.toml
  -t TYPE, --type TYPE  [required] the service type:
                        * now support: node, rpc, gateway

----------- help for subcommand 'create-subnet' -----------
usage: build_chain.py create-subnet [-h] [-n NAME] -s SUBNET

e.g: python3 build_chain.py create-subnet -n tars-network -s 172.25.0.0/16

optional arguments:
  -h, --help            show this help message and exit
  -n NAME, --name NAME  [optional] specified the network name, default is tars-network
  -s SUBNET, --subnet SUBNET
                        [required] specified the subnet, e.g. 172.25.0.0/16
```

### 2.1 **`download_binary`命令**

二进制下载命令，目前包括`-t`(`--type`), `-v`(`--version`)和`-p`(`--path`)三个选项，所有选项均是可选的，默认从FISCO BCOS github release tags下载最新版本二进制到`binary`文件夹，各选项功能如下：

- `-t`, `--type`: 指定下载类型，目前支持`git`和`cdn`两种下载类型，默认从FISCO BCOS github release tags下载最新版本二进制到，**若搭建和部署Pro版本区块链时访问git慢，可以使用cdn选项加速下载**。
- `-v`, `--version`: 指定下载的二进制版本，默认下载最新二进制，**FISCO BCOS 3.0默认的二进制最低版本不小于3.0.0-rc1**。
- `-p`, `--path`: 指定二进制下载路径，默认下载到`binary`文件夹。

### 2.2 **`-o, --op`选项**

用于指定操作命令，目前支持`gen-config, upload, deploy, upgrade, undeploy, expand, start, stop`:

- `gen-config`: 产生配置文件。
- `upload`: 在已经存在服务配置的场景下，上传并发布服务，一般和`gen-config`配合使用，先通过`gen-config`产生配置文件，然后通过`upload`指令上传并发布服务配置。
- `deploy`: 部署服务，包括服务配置生成、服务发布两个步骤。
- `undeploy`: 下线服务。
- `upgrade`: 升级服务，用于升级服务的二进制。
- `expand`: 扩容服务。
- `start`: 启动服务。
- `stop`: 停止服务。

### 2.3 **`-t, --type`选项**

用于指定操作的服务类型，当使用`-o`(`--op`)选项时，必须设置该选项，目前包括`rpc, gateway, node`:

- **rpc**: 指定操作的服务类型为RPC服务。
- **gateway**: 指定操作的服务类型为Gateway服务。
- **node**: 指定操作的服务类型为区块链节点服务。


### 2.4 **`-c, --config`选项[**Optional**]:**

用于指定配置文件路径，默认为`config.toml`，`BcosProBuilder`提供了三类配置模板：

- `conf/config-deploy-example.toml`: RPC、Gateway和区块链节点管理服务部署配置模板。
- `conf/config-service-expand-example.toml`: RPC、Gateway服务扩容配置模板。
- `conf/config-node-expand-example.toml`: 区块链节点管理服务配置模板。


### 2.5 **`create-subnet`命令**

```eval_rst
.. note::
   为了简化运维部署，一般不建议在生产环境中使用桥接网络，推荐使用host网络模式。
```

- `-n/--name`: 指定桥接网络的名称，如: `tars-network`。
- `-s/--subnet`: 指定桥接网络的网段，如: `172.25.0.0/16`。

## 3. tars docker-compose配置介绍

FISCO BCOS Pro版本区块链基于[tars](https://newdoc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/README.md)构建和部署，为简化tars部署，`BcosProBuilder`提供了tars的docker-compose配置。

### 3.1 桥接组网的tars docker-compose配置

```eval_rst
.. note::
   - **推荐体验环境使用桥接组网的方式搭建tars**。
   - 由于macOS docker跨文件系统io速度慢的问题，不推荐macOS体验环境挂载volume。
   - 使用docker-compose启动容器前，请先确保桥接网络 ``tars-network`` 已经被创建，可使用 ``BcosProBuilder`` 的 ``create-subnet`` 命令创建桥接网络。
   - 桥接网络仅可保证本机容器网间络连通，若需进行跨机器网络通信，推荐使用 ``host`` 网络模式或在两台机器间进行``vxlan``网络打通。
```

**桥接模式下，tarsFramework的docker-compose配置如下**：

```yml
version: "2"
networks:
  tars_net:
    external:
      name: tars-network
      
services:     
  tars-mysql:
    image: mysql:5.6
    ports:
      - "3310:3306"
    networks:
      tars_net:
        ipv4_address: 172.25.0.2
    environment:
      MYSQL_ROOT_PASSWORD: "FISCO"
    restart: always

  tars-framework:
    image: tarscloud/framework:v3.0.1
    networks:
      tars_net:
        ipv4_address: 172.25.0.3
    # 3000 is the tarsWeb port
    ports:
      - "3000:3000"
      - "3001:3001"
      - "20200-20205:20200-20205"
      - "30300-30305:30300-30305"
    environment:
      MYSQL_HOST: "172.25.0.2"
      MYSQL_ROOT_PASSWORD: "FISCO"
      MYSQL_PORT: 3306
      REBUILD: "false"
      INET: eth0
      SLAVE: "false"
    restart: always
    depends_on:
      - tars-mysql
```

**桥接模式下，tarsnode的docker-compose配置如下**：

```yml
version: "2"
networks:
  tars_net:
    external:
      name: tars-network

services:
  tars-node:
    image: tarscloud/tars-node:latest
    networks:
      tars_net:
        ipv4_address: 172.25.0.5
    ports:
      - "10200-10205:10200-10205"
      - "40300-40305:40300-40305"
      - "9000-9010:9000-9010"
    environment:
      INET: eth0
      WEB_HOST: "http://172.25.0.3:3000"
    restart: always
```

### 3.2 host组网的tars docker-compose配置

```eval_rst
.. note::
   - **推荐生产环境使用hosts组网的方式搭建tars**。
   - 由于macOS docker跨文件系统io速度慢的问题，不推荐macOS体验环境挂载volume。
   - 实际使用中，请将以下配置示例中的``172.25.0.2, 172.25.0.3``替换成物理机器IP。
```

**host模式下，tarsFramework的docker-compose配置如下**：

```yml
version: "3"
services:
  tars-mysql:
    image: mysql:5.6
    network_mode: "host"
    environment:
      MYSQL_ROOT_PASSWORD: ""
      MYSQL_TCP_PORT: 3310
    restart: always
    volumes:
      - ~/app/tars/framework-mysql:/var/lib/mysql
      - /etc/localtime:/etc/localtime

  tars-framework:
    image: tarscloud/framework:v3.0.1
    network_mode: "host"
    environment:
      MYSQL_HOST: "172.25.0.2"
      MYSQL_ROOT_PASSWORD: ""
      MYSQL_PORT: 3310
      REBUILD: "false"
      INET: eth0
      SLAVE: "false"
    restart: always
    volumes:
      - ~/app/tars/framework:/data/tars
      - /etc/localtime:/etc/localtime
    depends_on:
      - tars-mysql

```

**host模式下，tarsnode的docker-compose配置如下**：

```yml
version: "3"
services:
  tars-node:
    image: tarscloud/tars-node:latest
    network_mode: "host"
    environment:
      INET: eth0
      WEB_HOST: "http://172.25.0.3:3000"
    restart: always
    volumes:
      - ~/app/tars:/data/tars
      - /etc/localtime:/etc/localtime
```
