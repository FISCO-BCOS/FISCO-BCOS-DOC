# 4. Light Node Deployment Tool

Tags: "light node" "" build light node "

----

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. important::
    The build _ chain.sh script goal of this deployment tool is to enable users to use FISCO BCOS light nodes as quickly as possible。
```

FISCO BCOS provides' build _ chain.sh 'script to help users quickly build FISCO BCOS light nodes。

This article only describes how to use build _ chain.sh to build a light node. If you want to query the full usage of build _ chian.sh, please see<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tutorial/air/build_chain.html>`

## 1. Compile light nodes

'Please check the compilation documentation<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compile_binary.html>`

When configuring cmake, increase the option -DWITH _ LIGHTNODE = ON, and the light node program will be generated in the build / lightnode / fisco-bcos-lightnode directory。

## 2. Build light nodes

The 'build _ chain.sh' script is used for fast light nodes. The source code of the script is located in [github source code](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh), [gitee source code](https://gitee.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh)。

```shell
# Type bash build _ chain.sh -h to show script usage and parameters
$ bash build_chain.sh
Usage:
    -C <Command>                        [Optional] the command, support 'deploy' and 'expand' now, default is deploy
    -g <group id>                       [Optional] set the group id, default: group0
    -I <chain id>                       [Optional] set the chain id, default: chain0
    -v <FISCO-BCOS binary version>      [Optional] Default is the latest v3.6.0
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -L <fisco bcos lightnode exec>      [Optional] fisco bcos lightnode executable,input "download_binary" to download lightnode binary or assign correct lightnode binary path
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
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -m(Deployment node with monitoring function)
expand node e.g
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos
        bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -s
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -m -i 127.0.0.1:5 -M monitor/prometheus/prometheus.yml(Deployment node with monitoring function)
```

### **'L 'Options [**Optional**]**
Used to configure to enable FISCO BCOS light node mode, when the light node needs to be enabled, use the L option to specify the path or input of the light node program"download_binary"Automatically download the latest binary, build _ chain.sh will automatically generate light nodes to the directory specified by the o option。Execute the following command to generate light nodes while building the air blockchain。

Case:

```shell
# P2P services of four nodes occupy ports 30300-30303 respectively
# RPC services occupy ports 20200 - 20203 respectively
# Light nodes will be generated in nodes / lightnodes, and P2P and RPC services occupy ports 30304 and 20204 respectively
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -e ./bin/fisco-bcos -L ./bin/fisco-bcos-lightnode
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -L download_binary
```

build _ chain.sh will generate the nodes directory with the light nodes in lightnode in the nodes directory。

```shell
[INFO] Use binary ./bin/fisco-bcos
[INFO] Use lightnode binary ./bin/fisco-bcos-lightnode
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/sdk cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node0/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node1/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node2/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node3/conf cert successful!
[INFO] Generate uuid success: F3D04B9D-3DE5-47CD-9A32-F4A06B55B6C1
[INFO] Generate uuid success: 7B886EA7-1ADD-4537-AF55-4D6E5569B03F
[INFO] Generate uuid success: F6EAECD5-CA49-43CC-8452-4D0E273CA201
[INFO] Generate uuid success: 42A82084-D647-4233-83F0-32F0A19D76B8
writing RSA key
[INFO] Generate ./nodes/lightnode/conf cert successful!
[INFO] Generate uuid success: 60DF6258-D573-487A-9894-DFA5DEFC27EE
==============================================================
[INFO] GroupID               : group0
[INFO] ChainID               : chain0
[INFO] fisco-bcos path      : ../../build/fisco-bcos-air/fisco-bcos
[INFO] Auth mode            : false
[INFO] Start port           : 30300 20200
[INFO] Server IP            : 127.0.0.1:4
[INFO] SM model             : false
[INFO] enable HSM           : false
[INFO] Output dir           : ./nodes
[INFO] All completed. Files in ./nodes
```

## 3. Light node profile organization structure

The node configurations generated by build _ chain are as follows:

- **Root certificate and root certificate private key**: 'ca 'folder in the specified configuration build directory。
- **Node Connection Certificate**: The 'conf' directory of each node stores the network connection certificate of the node. The certificate and private key of the non-state secret node mainly include 'ca.crt, ssl.crt, sslkey'. The certificate and private key of the state secret node mainly include 'sm _ ca.crt, sm _ ssl.crt, sm _ enssl.crt, sm _ enssl.key, sm _ ssl.key'。
- **Node Signature Private Key**: 'node.pem 'in the' conf 'directory of the node, mainly in the signature of the consensus module。
- **SDK connection certificate**: Generated by 'build _ chain.sh', the client can copy the certificate to establish an SSL connection with the node。
- **Node Profile**: Configure 'config.ini' and 'config.genesis' in the node directory. The former mainly configures chain information, while the latter mainly configures creation block information. The information of light nodes should be consistent with that of all nodes. For details, please refer to [Air version blockchain node configuration introduction](./config.md)。
- **Start Stop Script**: 'start.sh 'and' stop.sh 'for starting and stopping nodes。

An example of light profile organization is as follows:

```shell
lightnode/
├── conf # Certificate configuration, same as full node
│   ├── ca.crt
│   ├── cert.cnf
│   ├── node.nodeid
│   ├── node.pem
│   ├── ssl.crt
│   └── ssl.key
├── config.genesis # Genesis block configuration, same as full node
├── config.ini # Light Node Configuration
├── data # Light Node Data Directory
├── fisco-bcos-lightnode # light node program
├── log # light node log
└── nodes.json # Configure P2P for light nodes and fill in the IP address and port list of all nodes
```
## 4. Start Light Node

Make sure that all nodes have been started. Enter the lightnode directory and execute:

```shell
bash start.sh

lightnode start successfully pid=72369
```

You can start the light node, and then use the console or SDK to connect to the light node in a similar way to the full node. The experience is basically similar to that of the full node。

## 5. Light Node Limit

Light nodes do not store full ledger data, which means that most of the information is obtained from all nodes, and light nodes ensure that the information obtained from all nodes is trustworthy and cannot be tampered with。

Light nodes support the following RPC interfaces:

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
- listAbi

For specific usage of each RPC interface, please refer to the document [console command list](../../develop/console/console_commands.md)。

## 6. Expansion of light nodes
Starting from FISCOBCOS version 3.3, you can use the build _ chain.sh script to scale up light nodes. The specific operation process is as follows:
1. Create a new folder config in the same directory as the build _ chain.sh build chain script；
2. Copy the root certificate folder ca under nodes to the config folder, "'cp -r nodes / ca config"';
3. Copy config.genesis, config.ini, and nodes.json from the existing lightnode folder to the config folder；
```shell
cp nodes/lightnode/config.* config
cp nodes/lightnode/nodes.json config 
```
4. Modify the port fields of rpc and p2p in config.ini to change the port number+1 (for example, the rpc port in the original config.ini file is 20202, which is modified to 20203)；
5. Execute script commands, specifically two expansion methods: executing the light node binary file path and automatically pulling the latest light node binary, as shown below:
```shell
bash build_chain.sh -C expand_lightnode -c config(config folder) -d config/ca -o nodes/lightnode1
bash build_chain.sh -C expand_lightnode -c config(config folder) -d config/ca -o nodes/lightnode1 -L + Specify the light node binary download path

```

6. After the expansion is successful, generate a new light node directory nodes / lightnode1

```shell
[INFO] generate_lightnode_scripts ...
[INFO] generate_lightnode_scripts success...
[INFO] generate_lightnode_cert ...
writing RSA key
[INFO] Generate nodes/lightnode1/conf cert successful!
[INFO] generate_lightnode_cert success...
[INFO] generate_lightnode_account ...
[INFO] generate_lightnode_account success...
[INFO] copy configurations ...
[INFO] copy configurations success...
==============================================================
[INFO] SM Model         : false
[INFO] output dir         : nodes/lightnode1
[INFO] All completed. Files in nodes/lightnode1
```
7. Enter lightnode1, the light node generated by the expansion, and start bash start.sh, the light node generated by the expansion
```shell
bash start.sh

lightnode start successfully pid=72370
```
