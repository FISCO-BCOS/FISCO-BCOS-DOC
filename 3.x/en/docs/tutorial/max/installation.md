# Manually Build a Max Blockchain Network

Tags: "Max version of the blockchain network" "deployment"

------------

In order to be able to support a large number of transactions on the chain scene, v3.x launched the Max version FISCO BCOS, Max version FISCO BCOS is designed to provide**Mass storage services, high-performance and scalable execution modules**、**Highly available fault recovery mechanism**。
Max version FISCO BCOS nodes use distributed storage TiKV, execution modules are independent into services, storage and execution are scalable, and support automated master and standby recovery.。

This chapter builds the Max version of the single-node FISCO BCOS alliance chain on a single machine to help users master the deployment process of the Max version of the FISCO BCOS blockchain.。Please refer to [here](../../quick_start/hardware_requirements.md)Use supported**Hardware and platforms**Conduct operation。

```eval_rst
.. note::
   - Max version FISCO BCOS uses the "BcosBuilder / max" tool for chain building and expansion. For more information about this tool, see 'BcosBuilder <. / max _ builder.html >' _
   - FISCO BCOS 3.x builds and manages microservices based on tars. Before building the Max version of FISCO BCOS, you need to install the tars service. This chapter describes the process of building the docker version of tars service. For more information about deploying and building tars, please refer to 'Here < https://doc.tarsyun.com/#/markdown/TarsCloud/TarsDocs/installation/README.md>`_
   - In this chapter, you can build the TARS service based on Docker. Make sure that the system user has the Docker permission.
   - To build a Max version of FISCO BCOS, you must first deploy a TiKV cluster. For details about how to deploy a TiKV cluster, see 'Here < https://tikv.org/docs/5.1/deploy/install/install/>`_
```

## 1. Installation Dependencies

Deployment tool 'BcosBuilder' depends on 'python3, curl, docker, docker-compose ', depending on the operating system you are using, use the following command to install the dependency。

**Install Ubuntu Dependencies(Version not less than Ubuntu18.04)**

```shell
sudo apt-get update
sudo apt-get install -y curl docker.io docker-compose python3 wget
```

**Installing CentOS Dependencies(Version not less than CentOS 7)**

```shell
sudo yum install -y curl docker docker-compose python3 python3-devel wget
```

**Install macOS dependencies**

```shell
brew install curl docker docker-compose python3 wget
```

## 2. Download the blockchain builder BcosBuilder

```eval_rst
.. note::
   - Deployment tool "BcosBuilder" configuration and use please refer to 'here <. / max _ builder.html >' _
   - If the network speed of "BcosBuilder" downloaded from github is too slow, try: curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.6.0/BcosBuilder.tgz && tar -xvf BcosBuilder.tgz
```

```shell
# Create action directory
mkdir -p ~/fisco && cd ~/fisco

# Download Block Chain Builder BcosBuilder
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.6.0/BcosBuilder.tgz && tar -xvf BcosBuilder.tgz

# Note: If the network speed is too slow, try the following command to download the deployment script:
curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.6.0/BcosBuilder.tgz && tar -xvf BcosBuilder.tgz

# Install Build Tools Dependency Pack
cd BcosBuilder && pip3 install -r requirements.txt
```

## 3. Install, start, and configure the tars service

**Please refer to [here] for the installation, startup and configuration of tars service.(../pro/installation.html#id2).**

## 4. Deploy TiKV

```eval_rst
.. note::
   - For the convenience of demonstration, use TiUP playground to start TiKV nodes. The playground is only used in the test environment. For the production environment, please refer to the official TiKV documentation to deploy the cluster.
   - It is recommended to modify the 'coprocessor.region of TiKV-split-size 'is 256MB, modify' coprocessor.enable-region-bucket 'to' true 'to reduce the time it takes to submit transactions and receipts
   - It is recommended to modify 'raftstore.raft of TiKV-entry-max-size 'is 64MB to avoid possible raft entry out-of-limit issues
   - It is recommended to turn on the compression function of TiKV to reduce disk occupation
```

**Download and install tiup**

```bash
$ curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh
```

**Start tikv v6.5.0**

```bash
# Deploy and start tikv(Let the physical ip of the machine be 172.25.0.3)
$ nohup tiup playground v6.5.0 --mode tikv-slim --host=172.25.0.3 -T tikv_demo --without-monitor > ~/tikv.log 2>&1 &
# Obtain the tikv listening port(The default listening port of tikv is 2379)
$ cat ~/tikv.log
tiup is checking updates for component playground ...timeout!
Starting component `playground`: /home/fisco/.tiup/components/playground/v1.9.4/tiup-playground v6.5.0 --mode tikv-slim --host=172.25.0.3 -T tikv_demo --without-monitor
Playground Bootstrapping...
Start pd instance:v6.5.0
Start tikv instance:v6.5.0
PD client endpoints: [172.25.0.3:2379]
```

## 5. Deploy Max version blockchain system

Max version FISCO BCOS includes RPC service, Gateway service, blockchain node service 'BcosMaxNodeService' and blockchain execution service 'BcosExecutorService':

- RPC service: It is responsible for receiving client requests and forwarding the requests to nodes for processing. RPC services can be scaled horizontally, and one RPC service can access multiple blockchain node services.;
- Gateway service: It is responsible for network communication between blockchain nodes across institutions. Gateway services can be scaled horizontally. One Gateway service can access multiple blockchain node services.;
- Blockchain node service 'BcosMaxNodeService': provides services related to blockchain scheduling, including block packaging, consensus, execution scheduling, and submission scheduling.;
- Blockchain execution service 'BcosExecutorService': responsible for block execution, scalable horizontally and dynamically。

This chapter takes deploying a single-node blockchain on a single machine as an example to introduce the Max version FISCO BCOS build deployment process.。

```eval_rst
.. note::
   - Before deploying the Max version blockchain system, please refer to 'here <.. / pro / installation.html#id2 > '_ Set up the tars service and apply for a token
   - If you do not apply for a token, refer to [3.2 Configuring Tars Service] to apply for a token.
   - If you forget to access the token of the tars service, you can use the [admin] of the tars web management platform.-> [user center]-> [token management] to obtain the token list
   - Before deploying the Max version of the blockchain, make sure that your tars service is in the startup state
   - The application token must be configured to the "tars.tars _ token" configuration option of the "config.toml" configuration file before all subsequent deployment steps can be performed.
   - Before deploying the Max version blockchain, make sure that tikv is deployed by reference, and ensure that each Max node corresponds to a tikv service. Multiple Max nodes cannot share the tikv service.
```

### 5.1 Download Binary

Before building the Max version of FISCO BCOS, you need to download the binary package. BcosBuilder provides a static binary package download function based on Linux, which can be deployed to 'tarsnode'. The command to download the latest binary is as follows:

```eval_rst
.. note::
   - You can use the python3 build _ chain.py-h "View deployment script usage
   - The binary is downloaded to the "binary" directory by default
   - If downloading the binary is slow, try: ``python3 build_chain.py download_binary -t cdn``
```

```shell
# Enter the operation directory
cd ~/fisco/BcosBuilder/max

# Run the build _ chain.py script to download the binary. The binary package is downloaded to the binary directory by default.
python3 build_chain.py download_binary
```

### 5.2 Deploying RPC Services

Similar to the Pro version FISCO BCOS, the Max version blockchain system also includes RPC services, which can be deployed and built through the chain building script 'BcosBuilder'. The sample configuration file 'config' is provided in the 'BcosBuilder / max / conf' directory.-deploy-example.toml ', which can be deployed on the' 172.25.0.3 'machine of the organization' agencyA '. The listening port occupied by RPC is' 20200'。

```eval_rst
.. note::
   Make sure that the default port 20200 is not occupied. If it is occupied, manually modify the configuration "config.toml" to configure ports that are not occupied.
```

```shell
# Enter the operation directory
cd ~/fisco/BcosBuilder/max

# Copy configuration from conf directory
cp conf/config-deploy-example.toml config.toml
```

The copied 'config.toml' is the configuration file used by the entire 'BcosBuilder / max'. For more information about the configuration, see the following link: [Introduction to Configuration](./max_builder.md)。

```shell
# Configure the generated token to the tars _ token field of config.toml
# The token generated here is eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MJAyODU1fQ.430Actual
# Linux system(macOS system Skip this step):
sed -i 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml
# macos system(Linux system skips this step):
sed -i .bkp 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml

# Configure the RPC service for the etcd cluster of dynamic watch Max node master and slave information. The etcd cluster of tikv pd is reused here, and the access address of tikv pd is set to 172.25.0.3:2379. If the access port of pd is another port, you need to manually configure the 'failover _ cluster _ url' configuration option of 'config.toml'. The specific configuration options are as follows:
[[agency]]
name = "agencyA"
failover_cluster_url = "172.25.0.3:2379"

#Deploy and start the RPC service
python3 build_chain.py chain -o deploy -t rpc
```

After the preceding command is executed, when the script outputs' deploy service success, type: rpc ', the RPC service is successfully deployed. The detailed log output is as follows:

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
----------- generate service config success -----------
=========================================================
deploy_service to 172.25.0.3, app: chain0, name: agencyABcosRpcService
deploy service agencyABcosRpcService
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: ca.crt
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: ssl.key
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: ssl.crt
* add config for service agencyABcosRpcService, node: 172.25.0.3, config: config.ini
upload tar package generated/./agencyABcosRpcService.tgz success, config id: 14
----------- deploy service success, type: rpc -----------
=========================================================
```

RPC service-related configuration generated during deployment is located at 'generated / rpc / ${chainID}'Directory, as follows:

```shell
$ tree generated/rpc/chain0
generated/rpc/chain0
├── 172.25.0.3
│   ├── agencyABcosRpcService # RPC Service Directory for Institution A
│   │   ├── config.ini.tmp    # Configuration file for RPC service of institution A
│   │   ├── sdk               # The SDK certificate directory. The SDK client can copy certificates from this directory to connect to the RPC service.
│   │   │   ├── ca.crt
│   │   │   ├── cert.cnf
│   │   │   ├── sdk.crt
│   │   │   └── sdk.key
│   │   └── ssl               # RPC Service Certificate Directory
│   │       ├── ca.crt
│   │       ├── cert.cnf
│   │       ├── ssl.crt
│   │       └── ssl.key
└── ca                       # The CA certificate directory, which mainly includes the CA certificate and the CA private key. Keep the CA certificate and the CA private key properly.
    ├── ca.crt
    ├── ca.key
    ├── ca.srl
    └── cert.cnf
```

After the RPC service is started successfully, you can view the service list 'agencyABcosRpcService' on the tars web management platform, and each service is in the 'active' state.


```eval_rst
.. note::
   - If you forget to access the token of the tars service, you can use the [admin] of the tars web management platform.-> [user center]-> [token management] to obtain the token list
   - **Keep the RPC service CA certificate and CA private key generated during service deployment for SDK certificate application, RPC service expansion, and other operations.**
```

### 5.3 Deploying Gateway Services

After the RPC service is deployed, you need to deploy the Gateway service to establish network connections between organizations.。Run the following command in the 'BcosBuilder / max' directory to deploy and start the Gateway service of the two organizations. The corresponding Gateway service name is' agencyABcosGatewayService ', the ip address is' 172.25.0.3 ', and the occupied ports are' 30300'(Before performing this operation, please make sure that the '30300' port of the machine is not occupied)。

```shell
# Enter the operation directory
cd ~/fisco/BcosBuilder/max

# Deploy and start the Gateway service
python3 build_chain.py chain -o deploy -t gateway
```

After the preceding command is executed, when the script outputs' deploy service success, type: gateway ', the RPC service is deployed successfully. The detailed log output is as follows:

```shell
=========================================================
----------- deploy service, type: gateway -----------
=========================================================
----------- generate service config -----------
* generate service config for 172.25.0.3 : agencyABcosGatewayService
* generate config for the gateway service
* generate generated/gateway/chain0/172.25.0.3/agencyABcosGatewayService/config.ini
* generate cert, output path: generated/gateway/chain0/172.25.0.3/agencyABcosGatewayService
* generate gateway connection file: generated/gateway/chain0/172.25.0.3/agencyABcosGatewayService/nodes.json
* generate config for the gateway service success
gen configuration for service agencyABcosGatewayService success
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
----------- deploy service success, type: gateway -----------
=========================================================
```

RPC service-related configuration generated during deployment is located in 'generated / gateway / ${chainID}'Directory, as follows:

```shell
$ tree generated/gateway/chain0
generated/gateway/chain0
├── 172.25.0.3
│   ├── agencyABcosGatewayService # Gateway service configuration path of organization A
│   │   ├── config.ini       # Gateway Profile for Institution A
│   │   ├── nodes.json        # Gateway Service Connection Configuration for Institution A
│   │   └── ssl                   # Gateway Service Certificate Configuration for Institution A
│   │       ├── ca.crt
│   │       ├── cert.cnf
│   │       ├── ssl.crt
│   │       └── ssl.key
└── ca                          # Configure the root certificate of the Gateway service. Save the root certificate and the root certificate private key.
    ├── ca.crt
    ├── ca.key
    ├── ca.srl
    └── cert.cnf
```

```eval_rst
.. note::
   - **Keep the RPC service CA certificate and CA private key generated during service deployment for operations such as gateway service expansion.**
```

After the Gateway service is successfully started, you can view the service list 'agencyABcosGatewayService' on the tars web management platform, and each service is in the 'active' state.

### 5.4 Deploying Blockchain Node Services

After the RPC service and the Gateway service are deployed, you can deploy the blockchain node service.。Run the following command in the 'BcosBuilder / max' directory to deploy and start a single-node blockchain service. The corresponding service names are 'agencyAgroup0node0BcosMaxNodeService' and 'agencyAgroup0node0BcosExecutorService'. The chain ID is' chain0 'and the group ID is' group0'。

```shell
# Enter the operation directory
cd ~/fisco/BcosBuilder/max

# Configure the deployment IP address of the Max node and the corresponding tikv storage pd access address, as follows:
    [[agency.group]]
        group_id = "group0"

        [[agency.group.node]]
        node_name = "node0"
        # the tikv storage pd-addresses
        pd_addrs="172.25.0.3:2379"
        key_page_size=10240
        deploy_ip = ["172.25.0.3"]
        executor_deploy_ip=["172.25.0.3"]
        monitor_listen_port = "3901"
        # the tikv storage pd-addresses
        # monitor log path example:"/home/fisco/tars/framework/app_log/"
        monitor_log_path = ""

# Deploy and launch the blockchain node service
python3 build_chain.py chain -o deploy -t node
```
After the preceding command is executed, when the script outputs' deploy all nodes of the given group success', the blockchain node service is deployed successfully. The detailed log output is as follows:

```shell
=========================================================
----------- generate config for all nodes -----------
----------- generate genesis config for group group0 -----------
* generate pem file for agencyAgroup0node0BcosMaxNodeService
	- pem_path: ./generated/chain0/group0/agencyAgroup0node0BcosMaxNodeService/node.pem
	- node_id_path: ./generated/chain0/group0/agencyAgroup0node0BcosMaxNodeService/node.nodeid
	- node_id: 9b30cccfd752b9715e6c2fc79b20d5deccf03225015afe6add3aaf0324264b60fff684f4ddee31b18170cac896341b26a24b65c9746dc8563e46e2d3c321f673

	- sm_crypto: 0
* generate_genesis_config_nodeid
* consensus_type: pbft
* block_tx_count_limit: 1000
* leader_period: 1
* gas_limit: 3000000000
* compatibility_version: 3.0.0
* generate_genesis_config_nodeid success
* store genesis config for chain0.group0
	 path: generated/chain0/group0/config.genesis
* store genesis config for chain0.group0 success
* store genesis config for agencyAgroup0node0BcosMaxNodeService
	 path: ./generated/chain0/group0/agencyAgroup0node0BcosMaxNodeService/config.genesis
* store genesis config for agencyAgroup0node0BcosMaxNodeService success
----------- generate genesis config for group0 success -----------
----------- generate ini config for BcosMaxNodeService of group group0 -----------
* store ini config for agencyAgroup0node0BcosMaxNodeService
	 path: ./generated/chain0/group0/agencyAgroup0node0BcosMaxNodeService/config.ini
* store ini config for agencyAgroup0node0BcosMaxNodeService success
----------- generate ini config for BcosMaxNodeService of group group0 success -----------
----------- generate ini config for BcosExecutorService of group group0 -----------
* generate pem file for agencyAgroup0node0BcosMaxNodeService
	- pem_path: ./generated/chain0/group0/agencyAgroup0node0BcosMaxNodeService/node.pem
	- node_id_path: ./generated/chain0/group0/agencyAgroup0node0BcosMaxNodeService/node.nodeid
	- node_id: 9b30cccfd752b9715e6c2fc79b20d5deccf03225015afe6add3aaf0324264b60fff684f4ddee31b18170cac896341b26a24b65c9746dc8563e46e2d3c321f673

	- sm_crypto: 0
* store executor ini config for agencyAgroup0node0BcosExecutorService
	 path: ./generated/chain0/group0/agencyAgroup0node0BcosExecutorService/config.ini
* store executor ini config for agencyAgroup0node0BcosExecutorService success
* store executor genesis config for agencyAgroup0node0BcosExecutorService
	 path: ./generated/chain0/group0/agencyAgroup0node0BcosExecutorService/config.genesis
* store executor genesis config for agencyAgroup0node0BcosExecutorService success
----------- generate ini config for BcosExecutorService of group group0 success -----------
----------- generate config for all nodes success -----------
deploy services for all the group nodes
deploy service agencyAgroup0node0BcosNodeService
upload tar package generated/./agencyAgroup0node0BcosNodeService.tgz success, config id: 16
deploy service agencyBgroup0node0BcosNodeService
upload tar package generated/./agencyBgroup0node0BcosNodeService.tgz success, config id: 17
=========================================================
```

The service-related configuration generated during deployment is located in 'generated / ${chainID}`('chainID 'defaults to' chain')Directory, as follows:

```shell
$ tree generated/chain0
generated/chain0
└── group0
    ├── agencyAgroup0node0BcosExecutorService
    │   ├── config.genesis
    │   └── config.ini
    ├── agencyAgroup0node0BcosMaxNodeService
    │   ├── config.genesis  # Genesis Block Configuration
    │   ├── config.ini      # Blockchain node configuration
    │   ├── node.nodeid
    │   └── node.pem        # Blockchain Node Service Signature Private Key
    └── config.genesis
```

```eval_rst
.. note::
   - It is recommended to deploy the blockchain node service after the RPC and Gateway services are deployed.
   - Before deploying a Max version blockchain node, make sure that tikv is deployed and started
```

After the blockchain node service is successfully started, you can view the service lists' agencyAgroup0node0BcosMaxNodeService 'and' agencyAgroup0node0BcosExecutorService 'on the tars web page management platform, and each service is in the' active 'status.。


## 6. Configure and use the console

The console is also suitable for Air / Pro / Max versions of FISCO BCOS blockchain, and the experience is completely consistent。After the Max version blockchain experience environment is built, you can configure and use the console to send transactions to the Max version blockchain.。

### 6.1 Installation Dependencies

```eval_rst
.. note::
   - For console configuration methods and commands, please refer to 'here <.. /.. / operation _ and _ maintenance / console / console _ config.html >' _
```

Before using the console, you need to install the java environment:

```shell
# Ubuntu system installation java
sudo apt install -y default-jdk

#Centos system installed java
sudo yum install -y java java-devel
```

### 6.2 Download, configure, and use the console

**Step 1: Download the Console**

```shell
cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh && bash download_console.sh
```
```eval_rst
.. note::
    - If you cannot download for a long time due to network problems, try 'cd ~ / fisco & & curl-#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh`
```

**Step 2: Configure the Console**

- Copy console configuration file

If the RPC service does not use the default port, replace 20200 in the file with the RPC service listening port。

```shell
# The latest version of the console uses the following command to copy the configuration file
cp -n console/conf/config-example.toml console/conf/config.toml
```

- Configure Console Certificates

```shell
# The command find.-name sdk Find all SDK certificate paths
cp -r ~/fisco/BcosBuilder/max/generated/rpc/chain0/agencyBBcosRpcService/172.25.0.3/sdk/* console/conf
```

**Step 3: Launch and use the console**

```shell
cd ~/fisco/console && bash start.sh
```

The output indicates that the startup is successful. Otherwise, check whether the node port configuration in conf / config.toml is correct and whether the SDK certificate is configured:

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.4.0)!
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

- Get information with the console

```shell
# Get network connection information:
[group0]: /> getPeers
PeersInfo{
    p2pNodeID='3082010a0282010100c1d64abf0af11ceaa69b237090a5078ccbc122aedbf93486100ae65cb38cbf2a6969b80f2beca1abba7f0c1674876b332380a4b76387d62445ba8da7190b54850ed8c3fb4d6f6bafbd4744249a55805c0d804db9aa0f105c44c3381de20c763469892fc11a2bc8467c523592c9b2738069d6beb4cfb413f90e0be53205eca1cf3618100c625667f0592fd682aabe9cfbca7f7c53d79eeb5961ed9f144681b32c9fa55fc4d80b5ffbf32a9f71e900bc1c9a92ce0a485bb1003a915f9215bd7c42461cd52d1b2add644e8c1c273aa3668d4a707771b1a99d6bfcbfdf28be5b9c619eefb0c182ea7e666c5753c79499b1959df17ad5bd0996b9d7f0d62aa53d2b450203010001',
    endPoint='0.0.0.0:30300',
    groupNodeIDInfo=[
        NodeIDInfo{
            group='group',
            nodeIDList=[
                4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a
            ]
        }
    ],
    peers=[]
}

# Obtaining Node List Information
[group0]: /> getGroupPeers
peer0: 4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a

[group0]: /> getSealerList
[
    Sealer{
        nodeID='4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a',
        weight=1
    }
]
```

### 6.3. Deploy and invoke contracts

**Step 1: Write the HelloWorld contract**

HelloWorld contract provides two interfaces' get()'and' set()', used to get / set the contract variable' name ', the contract content is as follows:

```c++
pragma solidity >=0.6.10 <0.8.20;
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

**Step 2: Deploying HelloWorld Contracts**

For quick user experience, the HelloWorld contract has been built into the console, located in the console directory 'contracts / consolidation / HelloWorld。sol ', refer to the following command to deploy:

```shell
# Enter the following command in the console to return the contract address if the deployment is successful
[group0]: /> deploy HelloWorld
transaction hash: 0x0fe66c42f2678b8d041624358837de34ac7db195abb6f5a57201952062190590
contract address: 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1
currentAccount: 0x537149148696c7e6c3449331d77ddfaabc3c7a75

# View current block height
[group0]: /> getBlockNumber
1
```

**Step 3. Invoke the HelloWorld contract**

```shell
# Call the get interface to get the name variable, where the contract address is the address returned by the deploy command
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

# Check the current block height, the block height remains unchanged, because the get interface does not change the ledger status
[group0]: /> getBlockNumber
1

# Call the set method to set the name
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

# View the current block height, because the set interface has modified the ledger status and the block height has increased to 2
[group0]: /> getBlockNumber
2

# Exit Console
[group0]: /> exit
```
