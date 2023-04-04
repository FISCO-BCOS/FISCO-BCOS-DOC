# 部署工具BcosBuilder

标签：``Pro版区块链网络`` ``部署工具``

------------

```eval_rst
.. important::
    部署工具BcosBuilder目标是让用户最快的部署和使用FISCO BCOS Pro/max版本区块链，其功能包括：部署/启动/关闭/更新/扩容RPC服务、Gateway服务以及区块链节点服务。
```

FISCO BCOS提供了`BcosBuilder`工具帮助用户快速部署、启停、更新和扩容FISCO BCOS Pro版本区块链联盟链，该工具可直接从FISCO BCOS的release tags中下载。

## 1. 配置介绍

`BcosBuilder`在`pro/conf`目录下提供了一些配置模板，用于帮助用户快速完成Pro版本区块链的部署、扩容。本章从tars服务配置项、区块链部署配置项、区块链扩容配置项三个角度详细介绍`BcosBuilder`的配置项。

### 1.1 tars服务配置项

- `[tars].tars_url`: 访问tars网页控制台的url，默认为`http://127.0.0.1:3000`。
- `[tars].tars_token`: 访问tars服务的token，可通过tars网页控制台的【admin】->【用户中心】->【token管理】进行token申请和查询。
- `[tars].tars_pkg_dir`: 放置Pro版本二进制包的路径，若配置了该配置项，默认会从指定的目录下获取FISCO BCOS Pro版本二进制进行服务部署、扩容等操作。

下面是tars服务配置项的示例：

```shell
[tars]
tars_url = "http://127.0.0.1:3000"
tars_token = ""
tars_pkg_dir = ""
```

### 1.2 区块链服务部署配置

区块链服务部署相关的配置项主要包括链配置项、RPC/Gateway服务配置项以及区块链节点服务配置项，其配置模板位于`BcosBuilder/pro`的`conf/config-deploy-example.toml`路径下。

**链配置项**

链配置项位于配置`[chain]`中，主要包括：

- `[chain].chain_id`: 区块链服务所属的链的ID，默认为`chain0`，**不能包括除字母和数字之外的所有特殊字符**;
- `[chain].rpc_sm_ssl`: RPC服务与SDK客户端之间采用的SSL连接类型，若设置为`false`，表明采用RSA加密连接；若设置为`true`，表明采用国密SSL连接，默认为`false`;
- `[chain].gateway_sm_ssl`: Gateway服务之间的SSL连接类型，设置为`false`表明采用RSA加密连接；设置为`true`表明采用国密SSL连接，默认为`false`;
- `[chain].rpc_ca_cert_path`: RPC服务的CA证书路径，若该路径下有完整的CA证书、CA私钥，`BcosBuilder`部署工具基于该路径下的CA证书生成RPC服务SSL连接证书；否则`BcosBuilder`部署工具会生成CA证书，并基于生成的CA证书为RPC服务颁发SSL连接证书;
- `[chain].gateway_ca_cert_path`:  Gateway服务的CA证书路径，若该路径下有完整的CA证书、CA私钥，`BcosBuilder`部署工具基于该路径下的CA证书生成Gateway服务SSL连接证书；否则`BcosBuilder`部署工具会生成CA证书，并基于生成的CA证书为Gateway服务颁发SSL连接证书;

链ID为`chain0`, RPC与SDK之间、Gateway服务之间均采用RSA加密连接的配置项如下：

```shell
[chain]
chain_id="chain0"
# the rpc-service enable sm-ssl or not, default disable sm-ssl
rpc_sm_ssl=false
# the gateway-service enable sm-ssl or not, default disable sm-ssm
gateway_sm_ssl=false
# the existed rpc service ca path, will generate new ca if not configurated
#rpc_ca_cert_path=""
# the existed gateway service ca path, will generate new ca if not configurated
#gateway_ca_cert_path=""
```

**RPC服务配置项**

```eval_rst
.. note::
   - 当部署一个RPC服务到多台机器时，请确保这些机器都安装了tarsnode服务，tarsnode部署请参考 `这里 <https://doc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/node.md>`_
```

RPC服务的配置项位于`[[agency]].[agency.rpc]`中，一个机构可部署一个RPC服务，一条链可包含多个机构，主要配置项包括：

- `[[agency]].[agency.rpc].deploy_ip`: RPC服务的部署IP，若配置多个，则会在多台机器上部署RPC服务，达到平行扩展的目标。
- `[[agency]].[agency.rpc].listen_ip`: RPC服务的监听IP，默认为`0.0.0.0`。
- `[[agency]].[agency.rpc].listen_port`: RPC服务的监听端口，默认为`20200`。
- `[[agency]].[agency.rpc].thread_count`: RPC服务进程内的工作线程数目，默认为`4`。


为机构`agencyA`部署RPC服务的配置如下：

```ini
[[agency]]
name = "agencyA"
# enable data disk encryption for rpc/gateway or not, default is false
enable_storage_security = false
# url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
# key_center_url =
# cipher_data_key =

    [agency.rpc]
    # 服务部署IP，支持多个，单须确保每个IP对应的机器上均安装了tarsnode服务
    deploy_ip=["172.25.0.3"]
    # RPC服务监听IP
    listen_ip="0.0.0.0"
    # RPC服务监听端口
    listen_port=20200
    # 工作线程数目
    thread_count=4
```

**Gateway服务配置项**

RPC服务的配置项位于`[[agency]].[agency.gateway]`中，一个机构可部署一个Gateway服务，一条链可部署多个Gateway服务，主要配置项包括：

- `[[agency]].[agency.gateway].deploy_ip`: Gateway服务的部署IP，若配置多个，则会在多台机器上部署Gateway服务，达到平行扩展的目标。
- `[[agency]].[agency.gateway].listen_ip`: Gateway服务的监听IP，默认为`0.0.0.0`。
- `[[agency]].[agency.gateway].listen_port`: Gateway服务的监听端口，默认为`30300`。
- `[[agency]].[agency.gateway].peers`: 所有Gateway服务的连接信息。

为机构`agencyA`部署Gateway服务的配置示例如下：

```ini
[[agency]]
name = "agencyA"
# enable data disk encryption for rpc/gateway or not, default is false
enable_storage_security = false
# url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
# key_center_url =
# cipher_data_key =

    [agency.gateway]
    deploy_ip=["172.25.0.3"]
    listen_ip="0.0.0.0"
    listen_port=30300
    peers=["172.25.0.3:30300", "172.25.0.3:30301"]
```

**区块链节点服务配置项：群组配置**

```eval_rst
.. note::
    区块链群组配置不可修改。
```

FISCO BCOS Pro版本区块链中每个区块链节点服务均属于一个群组，因此部署区块链节点前，首先需配置群组信息，群组配置项位于`[[group]]`中，具体如下：

- `[[group]].group_id`: 区块链节点所属的群组ID，默认为`group`。
- `[[group]].sm_crypto`: 节点账本是否采用国密类型签名、验签、哈希、加密算法，默认为`false`。


群组配置中还包括了创世块相关的配置：
- `[[group]].leader_period`: 每个leader可以连续打包的区块数目，默认为5。
- `[[group]].block_tx_count_limit`: 每个区块中可包含的最大交易数目，默认为1000。
- `[[group]].consensus_type`: 共识算法类型，目前仅支持`pbft`共识算法。
- `[[group]].gas_limit`: 每笔交易运行时消耗的gas上限，默认为300000000。
- `[[group]].vm_type`: 区块链节点运行的虚拟机类型，目前支持`evm`和`wasm`两种类型，且一个群组仅可运行一种类型的虚拟机，不可以部分节点运行EVM虚拟机、部分节点运行WASM虚拟机。
- `[[group]].auth_check`: 是否开启权限治理模式，权限使用文档请参考链接：[权限治理使用指南](../../develop/committee_usage.md)。
- `[[group]].init_auth_address`: 开启权限治理时，指定的初始化治理委员账号地址，权限使用文档请参考链接：[权限治理使用指南](../../develop/committee_usage.md)。
- `[[group]].compatibility_version`: 数据兼容版本号，默认为`3.0.0`，可通过控制台`setSystemConfigByKey`命令运行时升级数据兼容版本。

```ini
[[group]]
group_id="group0"
# the genesis configuration path of the group, will generate new genesis configuration if not configurated
# genesis_config_path = ""
# VM type, now only support evm/wasm
vm_type="evm"
# use sm-crypto or not
sm_crypto=false
# enable auth-check or not
auth_check=false
init_auth_address=""

# the genesis config
# the number of blocks generated by each leader
leader_period = 1
# the max number of transactions of a block
block_tx_count_limit = 1000
# consensus algorithm now support PBFT(consensus_type=pbft)
consensus_type = "pbft"
# transaction gas limit
gas_limit = "3000000000"
# compatible version, can be dynamically upgraded through setSystemConfig
# the default is 3.0.0
compatibility_version="3.0.0"
```

**区块链节点服务配置项：部署配置**

区块链节点服务部署配置项位于`[[agency]].[[agency.group]].[[agency.group.node]]`中，具体如下：
- `node_name`: 节点服务名，在服务部署的场景下可不配置，**若配置了该选项，须确保不同节点服务的服务名不重复**。
- `deploy_ip`: 节点服务部署ip
- `key_page_size`: KeyPage的粒度，默认10KB;
- `enable_storage_security`: 是否开启落盘加密，默认为`false`
- `key_center_url`: 若开启了落盘加密，这里可配置key-manager的url
- `cipher_data_key`: 若开启了落盘加密，这里配置数据加密密钥
- `monitor_listen_port`: 监控服务的监听端口，默认为`3902`
- `monitor_log_path`: 需要监控的区块链节点日志所在路径

区块链节点服务配置示例如下：
```ini
[[agency]]
name = "agencyA"
   [[agency.group]]
        group_id = "group0"
        [[agency.group.node]]
        node_name = "node0"
        deploy_ip = "172.25.0.3"
        # enable data disk encryption for bcos node or not, default is false
        enable_storage_security = false
        # url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
        # key_center_url =
        # cipher_data_key =
        monitor_listen_port = "3902"
        # monitor log path example:"/home/fisco/tars/framework/app_log/"
        monitor_log_path = ""
```

### 1.3 区块链服务扩容配置

`BcosBuilder`提供了区块链节点服务扩容、RPC/Gateway服务扩容功能，区块链节点服务扩容的配置模板在`conf/config-node-expand-example.toml`路径下，RPC/Gateway服务扩容配置模板在`conf/config-service-expand-example.toml`路径下。

**RPC服务扩容配置**

FISCO BCOS Pro版本区块链中，一个RPC服务可包含多个RPC服务节点，`BcosBuilder`提供了RPC服务扩容功能，可在已有RPC服务基础上扩容RPC服务节点，其配置选项主要位于`[chain]`和`[[agency]].[agency.rpc]`配置下，主要包括：

- `[chain].chain_id`: 扩容的RPC服务所属的链ID。
- `[chain].rpc_sm_ssl`: 扩容的RPC服务与SDK客户端之间是否采用国密SSL连接。
- `[chain].rpc_ca_cert_path`: 指定扩容的RPC服务的CA证书和CA私钥所在路径。
- `[[agency]].[agency.rpc].deploy_ip`: 扩容的RPC服务的部署IP。
- `[[agency]].[agency.rpc].listen_ip`: 指定扩容的RPC服务的监听IP。
- `[[agency]].[agency.rpc].listen_port`: 指定扩容的RPC服务的监听端口。
- `[[agency]].[agency.rpc].thread_count`: 扩容的RPC服务的工作线程数目。

扩容RPC服务`agencyABcosRpcService`到`172.25.0.5`的配置示例如下：

```ini
[chain]
chain_id="chain0"
rpc_sm_ssl=false
gateway_sm_ssl=false
# the ca path of the expanded rpc service
# must ensure that the path configuration is correct, otherwise the ssl verification will fail
rpc_ca_cert_path="generated/rpc/chain0/ca"
# the ca path of the expanded gateway service
# must ensure that the path configuration is correct, otherwise the ssl verification will fail
gateway_ca_cert_path="generated/gateway/chain0/ca"

[[agency]]
name = "agencyA"
# enable data disk encryption for rpc/gateway or not, default is false
enable_storage_security = false
# url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
# key_center_url =
# cipher_data_key =

    [agency.rpc]
    deploy_ip=["172.25.0.5"]
    listen_ip="0.0.0.0"
    listen_port=10200
    thread_count=4
```

**Gateway服务扩容配置**

类似于RPC服务，Gateway服务的扩容配置选项主要位于`[chain]`和`[[agency]].[agency.gateway]`配置下，主要包括：

- `[chain].chain_id`: 扩容的Gateway服务所属的链ID。
- `[chain].gateway_sm_ssl`: 扩容的Gateway服务与SDK客户端之间是否采用国密SSL连接。
- `[chain].gateway_ca_cert_path`: 指定扩容的Gateway服务的CA证书和CA私钥所在路径。
- `[[agency]].[agency.gateway].deploy_ip`: 扩容的Gateway服务的部署IP。
- `[[agency]].[agency.gateway].listen_ip`: Gateway服务节点的监听IP，默认为`0.0.0.0`。
- `[[agency]].[agency.gateway].listen_port`: Gateway服务的监听端口，默认为`30300`。
- `[[agency]].[agency.gateway].peers`: Gateway服务的连接信息，须配置所有Gateway服务节点的连接IP和连接端口信息。

扩容Gateway服务`agencyABcosGatewayService`到`172.25.0.5`的配置示例如下：

```ini
[chain]
chain_id="chain0"
rpc_sm_ssl=false
gateway_sm_ssl=false
# the ca path of the expanded rpc service
# must ensure that the path configuration is correct, otherwise the ssl verification will fail
rpc_ca_cert_path="generated/rpc/chain0/ca"
# the ca path of the expanded gateway service
# must ensure that the path configuration is correct, otherwise the ssl verification will fail
gateway_ca_cert_path="generated/gateway/chain0/ca"

[[agency]]
name = "agencyA"
# enable data disk encryption for rpc/gateway or not, default is false
enable_storage_security = false
# url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
# key_center_url =
# cipher_data_key = 

    [agency.gateway]
    deploy_ip=["172.25.0.5"]
    listen_ip="0.0.0.0"
    listen_port=40300
    peers=["172.25.0.3:30300", "172.25.0.3:30301", "172.25.0.5:40300"]
```

**区块链节点扩容配置**

`BcosBuilder/pro`提供了区块链节点扩容功能，可为指定群组扩容新的区块链节点服务，区块链节点扩容配置模板位于`conf/config-node-expand-example.toml`路径下，主要包括**链配置**和**扩容部署配置**，具体如下：

- `[chain].chain_id`: 扩容的区块链节点所属的链ID。
- `[[group]].group_id`: 扩容节点所属群组ID。
- `[[group]].genesis_config_path`: 扩容节点的创世块配置路径。
- `[[group]].sm_crypto`: 扩容节点是否为国密节点，默认为`false`。

- `[[agency]].[[agency.group]].group_id`: 扩容节点所在群组ID。
- `[[agency]].[[agency.group.node]].node_name`: 扩容的区块链节点服务名，**不能和已有的区块链节点服务名冲突**。
- `[[agency]].[[agency.group.node]].deploy_ip`: 扩容的区块链节点服务部署IP。
- `[[agency]].[[agency.group.node]].enable_storage_security`: 扩容节点是否开启落盘加密。
- `[[agency]].[[agency.group.node]].key_center_url`: key-manager的url，在开启落盘加密场景中需配置。
- `[[agency]].[[agency.group.node]].cipher_data_key`: 数据落盘加密密钥，在开启落盘加密场景中需配置。


为机构`agencyA`的`group0`群组扩容节点名为`node1`和`node2`的区块链节点服务到`172.25.0.5`的配置示例如下：
```ini
[[agency]]
name = "agencyA"
    [[agency.group]]
        group_id = "group0"

        [[agency.group.node]]
        node_name = "node1"
        deploy_ip = "172.25.0.5"
        # enable data disk encryption for bcos node or not, default is false
        enable_storage_security = false
        # url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
        # key_center_url =
        # cipher_data_key =

        [[agency.group.node]]
        node_name = "node2"
        deploy_ip = "172.25.0.5"
        # enable data disk encryption for bcos node or not, default is false
        enable_storage_security = false
        # url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
        # key_center_url =
        # cipher_data_key =
        monitor_listen_port = "3901"
        # monitor log path example:"/home/fisco/tars/framework/app_log/"
        monitor_log_path = ""
```

## 2. 使用介绍

可使用`python3 build_chain.py -h`查看`BcosBuilder/pro`的使用方法: 

```shell
----------- help for subcommand 'download_binary' -----------
usage: build_chain.py download_binary [-h] [-t TYPE] [-v VERSION] [-p PATH]

Download binary, eg: python3 build_chain.py download_binary -t cdn

optional arguments:
  -h, --help            show this help message and exit
  -t TYPE, --type TYPE  [Optional] Specify the source of the download, support cdn,git now, default type is git
  -v VERSION, --version VERSION
                        [Optional] Specify the version of the binary, default is v3.2.0
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
- `-v`, `--version`: 指定下载的二进制版本，默认下载最新二进制，**FISCO BCOS 3.x默认的二进制最低版本不小于3.0.0-rc1**。
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

用于指定配置文件路径，默认为`config.toml`，`BcosBuilder`提供了四类配置模板：

- `conf/config-build-example.toml`: 去tarsRPC、Gateway和区块链节点管理服务部署建立配置模板。
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

FISCO BCOS Pro版本区块链基于[tars](https://doc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/README.md)构建和部署，为简化tars部署，`BcosBuilder`提供了tars的docker-compose配置。

### 3.1 桥接组网的tars docker-compose配置

```eval_rst
.. note::
   - **推荐体验环境使用桥接组网的方式搭建tars**。
   - 由于macOS docker跨文件系统io速度慢的问题，不推荐macOS体验环境挂载volume。
   - 使用docker-compose启动容器前，请先确保桥接网络 ``tars-network`` 已经被创建，可使用 ``BcosBuilder`` 的 ``create-subnet`` 命令创建桥接网络。
   - 桥接网络仅可保证本机容器网间络连通，若需进行跨机器网络通信，推荐使用 ``host`` 网络模式或在两台机器间进行``vxlan``网络打通。
```

**桥接模式下，tarsFramework的docker-compose配置如下**：

```yml
version: "3"
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
      MYSQL_ROOT_PASSWORD: ""
    restart: always
    volumes:
      - ~/app/tars/framework-mysql:/var/lib/mysql
      - /etc/localtime:/etc/localtime 

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
      MYSQL_ROOT_PASSWORD: ""
      MYSQL_PORT: 3306
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

**桥接模式下，tarsnode的docker-compose配置如下**：

```yml
version: "3"
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
    volumes:
      - ~/app/tars:/data/tars
      - /etc/localtime:/etc/localtime
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
