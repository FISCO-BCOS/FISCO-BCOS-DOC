# build _ chain One-click Chain Building Tool

Tags: "build _ chain" "Build an Air version of the blockchain network"

----

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. important::
    The build _ chain.sh script goal of this deployment tool is to enable users to use FISCO BCOS Air version as quickly as possible.。
```

FISCO BCOS provides' build _ chain.sh 'script to help users quickly build FISCO BCOS alliance chain。

## 1. Script function introduction

The 'build _ chain.sh' script is used to quickly generate a configuration file for a node in the chain. The source code of the script is located in [github source code](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh), [gitee source code](https://gitee.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/BcosAirBuilder/build_chain.sh)。

```eval_rst
.. note::
    For the convenience of development and experience, the default listening IP address of the p2p module is' 0.0.0.0 '. For security reasons, please change it to a safe listening address according to the actual business network situation, such as the internal network IP or a specific external network IP
```

```shell
# Download the chain building script
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.6.0/build_chain.sh && chmod u+x build_chain.sh

# Note: If the speed of accessing git is too slow, try the following command to download the link creation script:
curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.6.0/build_chain.sh && chmod u+x build_chain.sh

# Type bash build _ chain.sh-h shows script usage and parameters
$ bash build_chain.sh
Usage:
    -C <Command>                        [Optional] the command, support 'deploy' and 'expand' now, default is deploy
    -g <group id>                       [Optional] set the group id, default: group0
    -I <chain id>                       [Optional] set the chain id, default: chain0
    -v <FISCO-BCOS binary version>      [Optional] Default is the latest v3.6.0
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -L <fisco bcos lightnode exec>      [Optional] fisco bcos lightnode executable, input "download_binary" to download lightnode binary or assign correct lightnode binary path
    -e <fisco-bcos exec>                [Optional] fisco-bcos binary exec
    -t <mtail exec>                     [Optional] mtail binary exec
    -o <output dir>                     [Optional] output directory, default ./nodes
    -p <Start port>                     [Optional] Default 30300,20200 means p2p_port start from 30300, rpc_port from 20200
    -s <SM model>                       [Optional] SM SSL connection or not, default is false
    -H <HSM model>                      [Optional] Whether to use HSM(Hardware secure module), default is false
    -c <Config Path>                    [Required when expand node] Specify the path of the expanded node config.ini, config.genesis and p2p connection file nodes.json
    -d <CA cert path>                   [Required when expand node] When expanding the node, specify the path where the CA certificate and private key are located
    -D <docker mode>                    Default off. If set -d, build with docker
    -a <Auth account>                   [Optional] when Auth mode Specify the admin account address.
    -w <WASM mode>                      [Optional] Whether to use the wasm virtual machine engine, default is false
    -R <Serial_mode>                    [Optional] Whether to use serial execute,default is true
    -k <key page size>                  [Optional] key page size, default size is 10240
    -m <fisco-bcos monitor>             [Optional] node monitor or not, default is false
    -i <fisco-bcos monitor ip/port>     [Optional] When expanding the node, should specify ip and port
    -M <fisco-bcos monitor>             [Optional] When expanding the node, specify the path where prometheus are located
    -z <Generate tar packet>            [Optional] Pack the data on the chain to generate tar packet
    -n <node key path>                  [Optional] set the path of the node key file to load nodeid
    -h Help

deploy nodes e.g
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -m (Deployment node with monitoring function)
    bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -s
expand node e.g
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -m -i 127.0.0.1:5 -M monitor/prometheus/prometheus.yml (Deployment node with monitoring function)
    bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node5 -e ./fisco-bcos -s
    bash build_chain.sh -C expand_lightnode -c config -d config/ca -o nodes/lightnode1
    bash build_chain.sh -C expand_lightnode -c config -d config/ca -o nodes/lightnode1 -L ./fisco-bcos-lightnode
```


### **'C 'Option [**Optional**]**

Script command, which supports' deploy 'and' expand '. The default value is' deploy':
- `deploy`: For deploying new nodes。
- 'expand 'for node expansion。

### **'g 'option [**Optional**]**
Set the group ID. If no group ID is set, the default value is group0.。

### **'c 'option [**Optional**]**
Used to set the chain ID. If it is not set, the default value is chain0.。
### **'v 'option [**Optional**]**

Used to specify the binary version used when building FISCO BCOS。build _ chain default download [Release page](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)Latest Version。

### **'l 'option**

The IP address of the generated node and the number of blockchain nodes deployed on the corresponding IP address. The parameter format is' ip1.:nodeNum1, ip2:nodeNum2`。

The 'l' option for deploying two nodes on a machine with IP address' 192.168.0.1 'and four nodes on a machine with IP address' 127.0.0.1 'is as follows:
`192.168.0.1:2, 127.0.0.1:4`

### **'L 'Options [**Optional**]**
Used to configure to turn on FISCO BCOS light node mode,-You can specify the binary executable path of the Air version light node after L, or enter"download_binary", The latest version of the light node binary is downloaded by default, as shown in the figure below。

```shell
# The P2P service of the two nodes occupies ports 30300 and 30301 respectively, and the RPC service occupies ports 20200 and 20201 respectively.
# -l Start the light node module,"download_binary" By default, the latest version of the binary file is pulled.
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2 -L download_binary
# Specify Light Node Binary Path
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2 -L /bin/fisco-bcos-lightnode
```

### **'e 'option [**Optional**]**

Specifies the binary executable path of the FISCO BCOS of the Air version. If no path is specified, the latest FISCO BCOS is pulled by default.。

### **'t 'option [**Optional**]**

Specifies the path, function, and-E is similar, if not specified, the latest version of FISCO BCOS is pulled by default。
### **'o 'option [**Optional**]**

Specifies the directory where the generated node configuration is located. The default directory is'. / nodes'。

### **'p 'option**

Specifies the start port for listening to P2P and RPC services on the node. By default, the start port for P2P services is 30300, and the start port for RPC services is 20200.。

Specify 30300 as the starting port for P2P service listening；An example of the starting port on which 20200 listens for the RPC service is as follows:

```shell
# P2P services of two nodes occupy ports 30300 and 30301 respectively
# RPC services occupy ports 20200 and 20201 respectively
$ bash build_chain.sh -p 30300,20200 -l 127.0.0.1:2
```

### **'s' option [**Optional**]**

Specify whether to build a full-link state-secret blockchain. The state-secret blockchain has the following features:

- **Blockchain Ledger Uses State Secret Algorithm**: Using sm2 signature verification algorithm, sm3 hash algorithm and sm4 symmetric encryption and decryption algorithm。
- **The state-secret SSL connection is used between the SDK client and the node.**。
- **State-secret SSL connection between blockchain nodes**。

An example of building a stand-alone four-node state-secret blockchain node is as follows:

```shell
$ bash build_chain.sh -l 127.0.0.1:4 -s -o gm_nodes
```

### **'H 'Options [**Optional**]**

cipher machine option, which indicates the use of a cipher machine。To turn this option on, add '-S 'means to open the national secret, and then to add'-n 'option is used to load the node.pem file to generate the nodeid of the cipher key。The command to open the cipher machine by loading the certificate file path is as follows
```shell
./build_chain.sh -e ./fisco-bcos -p 30300,20200 -l 127.0.0.1:4 -s -H -n nodeKeyDir/
```

### **'n 'Options [**Optional**]**

The node certificate directory option, which indicates that nodeid is generated by loading the node certificate in the folder. This option can be used for national secret and non-national secret without specifying-s。This option is followed by the certificate folder path。

### **'c 'Expansion options**

The expansion node option, which is used to specify the configuration file path of the expansion node. This path must include 'config.ini, config.genesis, and nodes.json'。

### **'d 'Expansion options**

Scale-out node option, which is used to specify the directory where the CA certificate and CA private key of the scale-out node are located。
### **'D 'Option [**Optional**]**

Use docker mode to build the FISCO BCOS blockchain. When this option is used, the binary is no longer pulled, but the user is required to start the node machine to install docker and the account has docker permission.。

Run the following command in the node directory to start the docker node:

```shell
./start.sh
```

The start.sh script in this mode starts the node with the following command

```shell
docker run -d --rm --name ${nodePath} -v ${nodePath}:/data --network=host -w=/data fiscoorg/fiscobcos:v3.6.0 -c config.ini -g config.genesis
```

### **'a 'Permission Control Options [**Optional**]**

Optional parameter. When permission control is enabled for a blockchain node, the-The 'a' option specifies the address of the admin account. If this option is not specified, the 'build _ chain' script will generate an account address as the admin account.。

### **'w 'Virtual Machine Options [**Optional**]**

Optional parameter, when the blockchain needs to enable the wasm virtual machine engine, you can use the '-w 'option is enabled. If this option is not specified, EVM is used by default。

### **'R 'Execution Mode Options [**Optional**]**

Optional parameter, when the blockchain starts serial execution mode, you can use the-The R 'option specifies the execution mode, which defaults to serial mode (true), and if set to false, DMC parallel mode is enabled。

### **'k 'Storage Control Options [**Optional**]**

Optional parameter, when you need to set the key-The size of the page in the page store.-K 'option sets the size of the page, if not specified, the default page size is 10240。

### **'m 'Node Monitoring Options [**Optional**]**

Optional parameter. When the blockchain node is enabled for node monitoring, the-m 'option to deploy nodes with monitoring. If this option is not selected, only nodes without monitoring are deployed。

An example of deploying an Air version blockchain with monitoring enabled is as follows:

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

After generating the blockchain node file, start the node (nodes / 127.0.0.1 / start _ all.sh) and node monitoring (nodes / monitor / start _ monitor.sh), log in to grafana (username and password are admin / admin) and import the dashboard ([github source code](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/template/Dashboard.json)) and configure the prometheus source(http://ip:9090/)View real-time display of each indicator。

### **'I'Expansion node monitoring options [**Optional**]**

Optional parameter. When the blockchain scaling node needs to be monitored, use the-i 'option to specify expansion node monitoring, parameter format is' ip1:nodeNum1 ', scale out the monitoring of the second node on the machine with IP address' 192.168.0.1 ', the' l 'option example is as follows:' 192.168.0.1:2`。

### **'M 'Node Monitoring Profile Options [**Optional**]**

Optional parameter. When the blockchain expansion node needs to be monitored, you can use the-M 'option to specify the relative path of the prometheus configuration file in the nodes directory。

### **'z 'Generate node directory package [**Optional**]**

Optional parameter to generate the corresponding compressed package while generating the node directory, which is convenient to copy during multi-machine deployment.。

### **'h 'option [**Optional**]**

View Script Usage。

## 2. Node Profile Organization Structure

The node configurations generated by build _ chain are as follows:

- **Root certificate and root certificate private key**: 'ca 'folder in the specified configuration build directory。
- **Node Connection Certificate**: The 'conf' directory of each node stores the network connection certificate of the node. The certificate and private key of the non-state secret node mainly include 'ca.crt, ssl.crt, sslkey'. The certificate and private key of the state secret node mainly include 'sm _ ca.crt, sm _ ssl.crt, sm _ enssl.crt, sm _ enssl.key, sm _ ssl.key'。
- **Node Signature Private Key**: 'node.pem 'in the' conf 'directory of the node, mainly in the signature of the consensus module。
- **SDK connection certificate**: Generated by 'build _ chain.sh', the client can copy the certificate to establish an SSL connection with the node。
- **Node Profile**: Configure 'config.ini' and 'config.genesis' in the node directory. The former mainly configures the chain information, while the latter mainly configures the creation block information. For details, see [Air version blockchain node configuration introduction](./config.md)。
- **Start Stop Script**: 'start.sh 'and' stop.sh 'for starting and stopping nodes。
- **Start-stop monitoring script**: 'monitor / start _ monitor.sh 'and' monitor / stop _ monitor.sh 'for starting and stopping node monitoring。

The configuration file organization example of the single-machine four-node Air version non-national secret blockchain is as follows:

```shell
nodes/
├── monitor
│   ├── grafana # grafana configuration file
│   ├── prometheus # Prometheus Configuration File
│   ├── start_monitor.sh # Startup script to turn on monitoring
│   ├── stop_monitor.sh # Stop script to stop monitoring
│   ├── compose.yaml # docker-compose configuration file
├── 127.0.0.1
│   ├── fisco-bcos # binary program
│   ├── mtail # binary program
│   ├── node0 # Node 0 Folder
│   │   ├── mtail # mtail configuration folder
│   │   │   ├── start_mtail_monitor.sh  # Startup script to start the mtail program for this node
│   │   │   ├── stop_mtail_monitor.sh   # Stop script to stop the mtail program for this node
│   │   │   ├── node.mtail # mtail configuration file
│   │   ├── conf # Configuration Folder
│   │   │   ├── ca.crt # Chain Root Certificate
│   │   │   ├── cert.cnf
│   │   │   ├── ssl.crt # ssl certificate
│   │   │   ├── ssl.key # ssl connection certificate private key
│   │   │   ├── node.pem # node signature private key file
│   │   │   ├── node.nodeid # Node id, hexadecimal representation of the public key
│   │   ├── config.ini # Node master configuration file, configure listening IP, port, certificate, log, etc.
│   │   ├── config.genesis # Genesis profile, consensus algorithm type, consensus timeout, and trading gas limits
│   │   ├── nodes.json # The json information of the node, showing the ip address and port of the node. Example:{"nodes": [127.0.0.1:30300]}
│   │   ├── start.sh # Startup script to start the node
│   │   └── stop.sh # Stop script to stop the node
│   ├── node1 # Node 1 Folder
│   │.....
│   ├── node2 # Node 2 Folder
│   │.....
│   ├── node3 # Node 3 Folder
│   │.....
│   ├── sdk # SDK Certificate
│   │   ├── ca.crt # SSL Connection Root Certificate
│   │   ├── cert.cnf # Certificate Configuration
│   │   ├── sdk.crt # SDK Root Certificate
│   │   ├── sdk.key # SDK certificate private key
│   ├── start_all.sh # Startup script to start all nodes
│   ├── stop_all.sh # Stop script to stop all nodes
```
