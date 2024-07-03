# Deployment Tools BcosBuilder

Tags: "Pro version of the blockchain network" "Deployment tool"

------------

```eval_rst
.. important::
    The deployment tool BcosBuilder aims to enable users to deploy and use the FISCO BCOS Pro / max version of the blockchain as quickly as possible. Its functions include: deploying / starting / shutting down / updating / scaling RPC services, Gateway services, and blockchain node services。
```

FISCO BCOS provides the 'BcosBuilder' tool to help users quickly deploy, start, stop, update and scale the FISCO BCOS Pro version of the blockchain consortium chain, which can be downloaded directly from the release tags of FISCO BCOS。

## 1. Configuration Introduction

'BcosBuilder 'provides some configuration templates in the' pro / conf 'directory to help users quickly complete the deployment and expansion of the Pro version of the blockchain。This chapter introduces the configuration items of 'BcosBuilder' in detail from three perspectives: tars service configuration items, blockchain deployment configuration items, and blockchain expansion configuration items。

### 1.1 tars service configuration item

- `[tars].tars_url`: The URL for accessing the tars web console. The default value is' http '://127.0.0.1:3000`。
- `[tars].tars_token`: To access the token of the tars service, you can use the [admin] ->User Center ->[token management] token application and query。
- `[tars].tars_pkg_dir`: Path to place the Pro version binary package. If this configuration item is configured, the FISCO BCOS Pro version binary is obtained from the specified directory by default for service deployment, expansion, and other operations。

The following is an example of a configuration item for the tars service:

```shell
[tars]
tars_url = "http://127.0.0.1:3000"
tars_token = ""
tars_pkg_dir = ""
```

### 1.2 Blockchain Service Deployment Configuration

Configuration items related to blockchain service deployment mainly include chain configuration items, RPC / Gateway service configuration items, and blockchain node service configuration items. The configuration template is located in the 'conf / config-deploy-example.toml' path of 'BcosBuilder / pro'。

**Chain Configuration Item**

Chain configuration items are located in the configuration '[chain]' and mainly include:

- `[chain].chain_id`: The ID of the chain to which the blockchain service belongs. The default value is' chain0 '**Cannot include all special characters except letters and numbers**;
- `[chain].rpc_sm_ssl`: The type of SSL connection used between the RPC service and the SDK client. If the value is set to 'false', RSA encryption is used；If it is set to 'true', it indicates that the state-secret SSL connection is used. The default value is' false ';
- `[chain].gateway_sm_ssl`: SSL connection type between Gateway services. Set to 'false' to use RSA encryption；Set to 'true' to indicate that a state-secret SSL connection is used. The default value is' false ';
- `[chain].rpc_ca_cert_path`: The path of the CA certificate of the RPC service. If a complete CA certificate and CA private key are available in this path, the 'BcosBuilder' deployment tool generates the RPC service SSL connection certificate based on the CA certificate in this path；Otherwise, the 'BcosBuilder' deployment tool generates a CA certificate and issues an SSL connection certificate for the RPC service based on the generated CA certificate;
- `[chain].gateway_ca_cert_path`:  The CA certificate path of the Gateway service. If there is a complete CA certificate and CA private key in this path, the 'BcosBuilder' deployment tool generates the Gateway service SSL connection certificate based on the CA certificate in this path；Otherwise, the 'BcosBuilder' deployment tool generates a CA certificate and issues an SSL connection certificate for the Gateway service based on the generated CA certificate;

The chain ID is' chain0 '. The configuration items for RSA encrypted connections between RPC and SDK and between Gateway services are as follows:

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

**RPC Service Configuration Item**

```eval_rst
.. note::
   -When deploying an RPC service to multiple machines, make sure that the tarsnode service is installed on these machines. For tarsnode deployment, please refer to 'here<https://doc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/node.md>`_
```

RPC service configuration items are located in '[[agency]]. [agency.rpc]'. An organization can deploy an RPC service, and a chain can contain multiple organizations. The main configuration items include:

- `[[agency]].[agency.rpc].deploy_ip`: The deployment IP address of the RPC service. If multiple IP addresses are configured, the RPC service is deployed on multiple machines to achieve the goal of parallel expansion。
- `[[agency]].[agency.rpc].listen_ip`: The listening IP address of the RPC service. The default value is' 0.0.0.0'。
- `[[agency]].[agency.rpc].listen_port`: The listening port of the RPC service. The default value is 20200。
- `[[agency]].[agency.rpc].thread_count`: Number of worker threads in RPC service process, default is' 4'。


The configuration for deploying RPC services for agency 'agencyA' is as follows:

```ini
[[agency]]
name = "agencyA"
# enable data disk encryption for rpc/gateway or not, default is false
enable_storage_security = false
# url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
# key_center_url =
# cipher_data_key =

    [agency.rpc]
    # You can deploy multiple IP addresses. You must ensure that the tarsnode service is installed on the machine corresponding to each IP address
    deploy_ip=["172.25.0.3"]
    # RPC Service Listening IP
    listen_ip="0.0.0.0"
    # RPC service listening port
    listen_port=20200
    # Number of worker threads
    thread_count=4
```

**Gateway Service Configuration Item**

The configuration items of the Gateway service are located in '[[agency]]. [agency.gateway]'. An organization can deploy one Gateway service and a chain can deploy multiple Gateway services. The main configuration items include:

- `[[agency]].[agency.gateway].deploy_ip`: The deployment IP address of the Gateway service. If multiple IP addresses are configured, the Gateway service is deployed on multiple machines to achieve the goal of parallel expansion。
- `[[agency]].[agency.gateway].listen_ip`: The listening IP address of the Gateway service. The default value is' 0.0.0.0'。
- `[[agency]].[agency.gateway].listen_port`: The listening port of the Gateway service. The default value is' 30300'。
- `[[agency]].[agency.gateway].peers`: Connection information for all Gateway services。

A sample configuration for deploying the Gateway service for agency 'agencyA' is as follows:

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

**Blockchain node service configuration item: group configuration**

```eval_rst
.. note::
    Blockchain group configuration cannot be modified。
```

Each blockchain node service in the blockchain of FISCO BCOS Pro belongs to a group. Therefore, before deploying a blockchain node, you must first configure the group information. The group configuration items are located in '[[group]]', as follows:

- `[[group]].group_id`: The ID of the group to which the blockchain node belongs. The default value is "group."。
- `[[group]].sm_crypto`: Whether the node ledger adopts the signature, signature verification, hash, or encryption algorithm of the national secret type. The default value is' false'。


The group configuration also includes configurations related to the Genesis block:
- `[[group]].leader_period`: The number of blocks that each leader can package consecutively. The default value is 5。
- `[[group]].block_tx_count_limit`: The maximum number of transactions that can be included in each block, which defaults to 1000。
- `[[group]].consensus_type`: Consensus algorithm type. Currently, only the 'pbft' consensus algorithm is supported。
- `[[group]].gas_limit`: The maximum amount of gas consumed during the run of each transaction. The default value is 300000000。
- `[[group]].vm_type`: The type of virtual machine running on a blockchain node. Currently, two types are supported: 'evm' and 'wasm'. A group can run only one type of virtual machine. Some nodes cannot run EVM virtual machines and some nodes cannot run WASM virtual machines。
- `[[group]].auth_check`: To enable the permission governance mode, please refer to the link [Permission Governance User Guide](../../develop/committee_usage.md)。
- `[[group]].init_auth_address`: When permission governance is enabled, specify the account address of the initialization governance committee. For permission usage documents, please refer to the link: [Permission Governance Usage Guide](../../develop/committee_usage.md)。
- `[[group]].compatibility_version`: The data-compatible version number. The default value is 3.0.0. You can upgrade the data-compatible version when running the 'setSystemConfigByKey' command in the console。

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

**Blockchain Node Service Configuration Item: Deployment Configuration**

The blockchain node service deployment configuration item is located in '[[agency]]. [[agency.group]]. [[agency.group.node]]', as follows:
- `node_name`: The name of the node service, which is not configured in the service deployment scenario**If this option is configured, make sure that the service names of different node services are not duplicated**。
- `deploy_ip`: node service deployment ip
- `key_page_size`: The granularity of the KeyPage. The default value is 10KB;
- `enable_storage_security`: Whether to enable disk placement encryption. The default value is false
- `key_center_url`: If disk encryption is enabled, the url of the key-manager can be configured here
- `cipher_data_key`: If disk encryption is enabled, configure the data encryption key here
- `monitor_listen_port`: The listening port of the monitoring service, which is' 3902 'by default
- `monitor_log_path`: Path of the blockchain node logs to be monitored

The blockchain node service configuration example is as follows:
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

### 1.3 Block chain service expansion configuration

'BcosBuilder 'provides blockchain node service expansion and RPC / Gateway service expansion functions. The configuration template for blockchain node service expansion is in the' conf / config-node-expand-example.toml 'path, and the configuration template for RPC / Gateway service expansion is in the' conf / config-service-expand-example.toml 'path。

**RPC Service Expansion Configuration**

In FISCO BCOS Pro version blockchain, an RPC service can contain multiple RPC service nodes. BcosBuilder provides the RPC service scaling function, which can scale out RPC service nodes based on existing RPC services. The configuration options are mainly located in the configurations of '[chain]' and '[[agency]]. [agency.rpc]', mainly including:

- `[chain].chain_id`: The ID of the chain to which the expanded RPC service belongs。
- `[chain].rpc_sm_ssl`: Whether the expanded RPC service and SDK client use the state-secret SSL connection。
- `[chain].rpc_ca_cert_path`: Specify the path to the CA certificate and CA private key of the expanded RPC service。
- `[[agency]].[agency.rpc].deploy_ip`: Deployment IP of Scaled RPC Service。
- `[[agency]].[agency.rpc].listen_ip`: Specify the listening IP address of the expanded RPC service。
- `[[agency]].[agency.rpc].listen_port`: Specify the listening port of the expanded RPC service。
- `[[agency]].[agency.rpc].thread_count`: Number of worker threads for an expanded RPC service。

The sample configuration for scaling the RPC service 'agencyABcosRpcService' to '172.25.0.5' is as follows:

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

**Gateway Service Expansion Configuration**

Similar to the RPC service, the scaling configuration options of the Gateway service are mainly located in the configurations of '[chain]' and '[[agency]]. [agency.gateway]', mainly including:

- `[chain].chain_id`: The ID of the chain to which the expanded Gateway service belongs。
- `[chain].gateway_sm_ssl`: Whether the state-secret SSL connection is used between the expanded Gateway service and the SDK client。
- `[chain].gateway_ca_cert_path`: Specify the path of the CA certificate and the CA private key of the extended Gateway service。
- `[[agency]].[agency.gateway].deploy_ip`: Deployment IP address of the scaled-out Gateway service。
- `[[agency]].[agency.gateway].listen_ip`: The listening IP address of the Gateway service node. The default value is' 0.0.0.0'。
- `[[agency]].[agency.gateway].listen_port`: The listening port of the Gateway service. The default value is' 30300'。
- `[[agency]].[agency.gateway].peers`: The connection information of the Gateway service. You must configure the connection IP address and connection port information of all Gateway service nodes。

A sample configuration for scaling the Gateway service 'agencyABcosGatewayService' to '172.25.0.5' is as follows:

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

**Blockchain node expansion configuration**

'BcosBuilder / pro 'provides the blockchain node expansion function to expand new blockchain node services for a specified group. The blockchain node expansion configuration template is located in the' conf / config-node-expand-example.toml 'path**chain configuration**和**Scale-out deployment configuration**, as follows:

- `[chain].chain_id`: The ID of the chain to which the expanded blockchain node belongs。
- `[[group]].group_id`: Group ID of the expansion node。
- `[[group]].genesis_config_path`: Path to configure the Genesis block of the scaling node。
- `[[group]].sm_crypto`: Whether the scaling node is a state secret node. The default value is' false '。

- `[[agency]].[[agency.group]].group_id`: Group ID of the scaling node。
- `[[agency]].[[agency.group.node]].node_name`: The service name of the expanded blockchain node**Cannot conflict with the service name of an existing blockchain node**。
- `[[agency]].[[agency.group.node]].deploy_ip`: Deployment IP address of the expanded blockchain node service。
- `[[agency]].[[agency.group.node]].enable_storage_security`: Whether disk encryption is enabled on the expansion node。
- `[[agency]].[[agency.group.node]].key_center_url`: The url of the key-manager. You need to configure the。
- `[[agency]].[[agency.group.node]].cipher_data_key`: Data disk encryption key. You need to configure the data disk encryption key in the disk encryption scenario。


The following is an example of how to scale up blockchain nodes named 'node1' and 'node2' to '172.25.0.5' for the 'group0' group of institution 'agencyA':
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

## 2. Introduction to Use

Use 'python3 build _ chain.py -h' to see how 'BcosBuilder / pro': 

```shell
----------- help for subcommand 'download_binary' -----------
usage: build_chain.py download_binary [-h] [-t TYPE] [-v VERSION] [-p PATH]

Download binary, eg: python3 build_chain.py download_binary -t cdn

optional arguments:
  -h, --help            show this help message and exit
  -t TYPE, --type TYPE  [Optional] Specify the source of the download, support cdn,git now, default type is git
  -v VERSION, --version VERSION
                        [Optional] Specify the version of the binary, default is v3.6.0
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

### 2.1 **'download _ binary 'command**

Binary download command, currently includes' -t'(`--type`), `-v`(`--version`)and '-p'(`--path`)Three options, all of which are optional. By default, download the latest version of binary from FISCO BCOS github release tags to the 'binary' folder. Each option has the following functions:

- `-t`, `--type`: Specifies the download type. Currently, 'git' and 'cdn' are supported. By default, you can download the latest version of binary from FISCO BCOS github release tags**If the access to git is slow when building and deploying the Pro version of the blockchain, you can use the cdn option to speed up the download**。
- `-v`, `--version`: Specifies the binary version to download. By default, the latest binary is downloaded**FISCO BCOS 3.x default binary minimum version is not less than 3.0.0-rc1**。
- `-p`, `--path`: Specifies the binary download path, which is downloaded to the binary folder by default。

### 2.2 **'-o, --op' option**

Used to specify operation commands. Currently, 'gen-config, upload, deploy, upgrade, undeploy, expand, start, and stop' are supported:

- `gen-config`: Generate Profile。
- `upload`: In the scenario where the service configuration already exists, upload and publish the service, which is usually used in conjunction with 'gen-config'. First, use 'gen-config' to generate the configuration file, and then use the 'upload' command to upload and publish the service configuration。
- `deploy`: Deploy a service, including two steps: service configuration generation and service release。
- `undeploy`: Offline service。
- `upgrade`: Upgrade service, binary for upgrade service。
- `expand`: Expansion service。
- `start`: Start Service。
- `stop`: Stop Service。

### 2.3 **'-t, --type' option**

Used to specify the service type of the operation when using '-o'(`--op`)option, you must set this option, which currently includes' rpc, gateway, node':

- **rpc**: Specifies that the service type of the operation is an RPC service。
- **gateway**: Specifies that the service type of the operation is a Gateway service。
- **node**: The service type of the specified operation is blockchain node service。


### 2.4 **'-c, --config' option [**Optional**]:**

Used to specify the configuration file path. The default value is' config.toml '. BcosBuilder provides four types of configuration templates:

- `conf/config-build-example.toml`: Go to tarsRPC, Gateway, and blockchain node management service deployment to establish a configuration template。
- `conf/config-deploy-example.toml`: RPC, Gateway, and Blockchain Node Management Service Deployment Configuration Templates。
- `conf/config-service-expand-example.toml`: RPC, Gateway Service Expansion Configuration Template。
- `conf/config-node-expand-example.toml`: Blockchain node management service configuration template。

### 2.5 **'create-subnet 'command**

```eval_rst
.. note::
   To simplify O & M deployment, we recommend that you do not use a bridged network in a production environment. We recommend that you use the host network mode。
```

- `-n/--name`: Specifies the name of the bridged network, such as: `tars-network`。
- `-s/--subnet`: Specifies the segment of the bridged network, such as: `172.25.0.0/16`。

## 3. tars docker-compose configuration introduction

FISCO BCOS Pro version blockchain based on [tars](https://doc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/README.md)To simplify the deployment of tars, 'BcosBuilder' provides the docker-compose configuration of tars。

### 3.1 Tars docker-compose configuration for bridged networking

```eval_rst
.. note::
   - **recommend the experience environment to build tars by using bridge networking**。
   - Due to the slow speed of macOS docker cross-file system io, it is not recommended to mount the volume in the macOS experience environment。
   - Before starting the container using docker-compose, make sure that the bridge network "tars-network" has been created. You can use the "create-subnet" command of "BcosBuilder" to create a bridge network。
   - The bridge network can only ensure the connectivity between local container networks. If cross-machine network communication is required, it is recommended to use "host" network mode or "vxlan" network connection between two machines。
```

**In bridge mode, the docker-compose configuration of tarsFramework is as follows**：

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

**In bridge mode, the docker-compose configuration of tarsnode is as follows**：

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

### 3.2 Tars docker-compose configuration for host networking

```eval_rst
.. note::
   - **it is recommended that the production environment use hosts networking to build tars**。
   - Due to the slow speed of macOS docker cross-file system io, it is not recommended to mount the volume in the macOS experience environment。
   - In actual use, replace "172.25.0.2, 172.25.0.3" in the following configuration example with the physical machine IP address。
```

**In host mode, the docker-compose configuration of tarsFramework is as follows**：

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

**In host mode, the docker-compose configuration of tarsnode is as follows**：

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
