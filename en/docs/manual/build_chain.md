# Chain building script

```eval_rst
.. important::
    The goal of the script is to let users apply FISCO BCOS as quickly as possible. For the enterprise applications deploying FISCO BCOS, please refer to `Enterprise Deployment Tools <../enterprise_tools/index.html>`_ .
```

FISCO BCOS has provided `build_chain` script to help users quickly build FISCO BCOS alliance chain. By default, the script downloads `master` branch of the latest version pre-compiles executable program from [GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS)for building related environment.

## Script introduction

- `build_chain` script is used to quickly generate configuration files of a chain node. For the script that depends on `openssl`, please according your own operating system to install `openssl 1.0.2` version and above. The source code of script is located at [FISCO-BCOS/tools/build_chain.sh][build_chain].
- For quick experience can use the `-l` option to specify the node IP and number. `-f` option supports the creation of FISCO BCOS chains for complex business scenarios by using a configuration file in a specified format. **`-l` and `-f` options must be specified uniquely and cannot coexist**.
- It is recommended to use `-T` and `-i` options for testing. `-T` enables log level to DEBUG. `-i` sets RPC and channel to listen for `0.0.0.0` while p2p module listens for `0.0.0.0` by default.

## Help

```bash
Usage:
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -f <IP list file>                   [Optional] split by line, every line should be "ip:nodeNum agencyName groupList". eg "127.0.0.1:4 agency1 1,2"
    -e <FISCO-BCOS binary path>         Default download fisco-bcos from GitHub. If set -e, use the binary at the specified location
    -o <Output Dir>                     Default ./nodes/
    -p <Start Port>                     Default 30300,20200,8545 means p2p_port start from 30300, channel_port from 20200, jsonrpc_port from 8545
    -i <Host ip>                        Default 127.0.0.1. If set -i, listen 0.0.0.0
    -v <FISCO-BCOS binary version>      Default get version from https://github.com/FISCO-BCOS/FISCO-BCOS/releases. If set, use specified version binary
    -s <DB type>                        Default rocksdb. Options can be rocksdb / mysql / external, rocksdb is recommended
    -d <docker mode>                    Default off. If set -d, build with docker
    -c <Consensus Algorithm>            Default PBFT. If set -c, use Raft
    -C <Chain id>                       Default 1. Can set uint.
    -g <Generate guomi nodes>           Default no
    -z <Generate tar packet>            Default no
    -t <Cert config file>               Default auto generate
    -T <Enable debug log>               Default off. If set -T, enable debug log
    -F <Disable log auto flush>         Default on. If set -F, disable log auto flush
    -h Help
e.g
    ./tools/build_chain.sh -l "127.0.0.1:4"
```

## Option introduction

### **`l`option:**
Use to specify the chain to be generated and the number of nodes under each IP, separated by commas. The script generates configuration file of corresponding node according to the input parameters. The port number of each node is incremented from 30300 by default. All nodes belong to the same organization and Group.

### **`f`option**
    + Use to generate node according to configuration file. It supports more customization than `l` option.
    + Split by row. Each row represents a server, in the format of `IP:NUM AgencyName GroupList`. Items in each line are separated by spaces, and there must be **no blank lines**.
    + `IP:NUM` represents the IP address of the machine and the number of nodes on the machine.`AgencyName`represents the name of the institution to specifies the institution certificate to use. `GroupList` represents the group that the generated node belong to, split by`,`. For example, `192.168.0.1:2 agency1 1,2` represents that a machine with `ip` which is `192.168.0.1` exists two nodes. For example, 192.168.0.1:2 agency1 1,2 represents that there are two nodes on the machine with ip 192.168.0.1. These two nodes belong to agency `agency1` and belong to group1 and group2.

The following is an example of a configuration file. Each configuration item separated by a space, where `GroupList` represents the group that the server belongs to.

```bash
192.168.0.1:2 agency1 1,2
192.168.0.1:2 agency1 1,3
192.168.0.2:3 agency2 1
192.168.0.3:5 agency3 2,3
192.168.0.4:2 agency2 3
```

**Suppose the above file is named `ipconf`**, using the following command to build a chain, which indicates to use configuration file, to set the log level to `DEBUG`, and to listen for `0.0.0.0`.

```bash
$ bash build_chain.sh -f ipconf -T -i
```

### **`e`option[**Optional**]**
is used to specify **full path** where `fisco-bcos` binary is located.Script will cope `fisco-bcos` to the directory named by IP number. If no path to be specified, the latest binary program of `master` branch is downloaded from GitHub by default.

```bash
# download the latest release binary from GitHub to generate native 4 nodes
$ bash build_chain.sh -l "127.0.0.1:4"
# use bin/fisco-bcos binary to generate native 4 nodes
$ bash build_chain.sh -l "127.0.0.1:4" -e bin/fisco-bcos
```

### **`o`option[**Optional**]**
specifies the directory where the generated configuration is located.

### **`p`option[**Optional**]**
specifies the starting port of the node. Each node occupies three ports which are p2p, channel, and jsonrpc, respectively. The ports are split by, and three ports must be specified. The ports used by different nodes under the same IP address are incremented from the starting port.

```bash
# Two nodes occupies `30300,20200,8545` and `30301,20201,8546` respectively.
$ bash build_chain -l 127.0.0.1:2 -p 30300,20200,8545
```

### **`i`option[**Optional**]**
No parameter option. When setting this option, set the node's RPC and channel to listen to `0.0.0.0`.

### **`v`option[**Optional**]**

Used to specify the binary version used when building FISCO BCOS. build_chain downloads the latest version of [Release Page] (https://github.com/FISCO-BCOS/FISCO-BCOS/releases) by default. When setting this option, the download parameter specifies the `version` version and sets `[compatibility].supported_version=${version}` in the configuration file `config.ini`. If you specify the binary with the `-e` option, to use the binary and configure `[compatibility].supported_version=${version}` as the latest version number of [Release page](https://github.com/FISCO-BCOS/FISCO-BCOS /releases).

### **`d`option[**Optional**]**

Use the docker mode to build FISCO BCOS. When using this option, the binary is no longer extracted, but users are required to start the node machine to install docker, and their accounts have docker permission, which means their accounts should in the docker group.
Use following command to start node at node home.
```bash
$ ./start.sh
```
The command to start the node in script start.sh is as follows

```bash
$ docker run -d --rm --name ${nodePath} -v ${nodePath}:/data --network=host -w=/data fiscoorg/fiscobcos:latest -c config.ini
```

### **`s`option[**Optional**]**
There are parameter options. The parameter is the name of db. Currently it supports three modes: rocksdb, mysql, external and scalable. RocksDB is used by default. 

- rocksdb use RocksDB as backend database.
- mysql needs to configure the information relates to mysql in the group ini file. 
- external needs to configure topic information and start amdb-proxy.
- scalable mode, block data and state data are stored in different RocksDB databases, and block data is stored in rocksdb instance named after block height. The rocksdb instance used to store block data is scroll according to the configuration `scroll_threshold_multiple`*1000 and block height. If chain data need to be tailored, the scalable mode must be used.

### **`c`option[**Optional**]**
No parameter option. When setting this option, the consensus algorithm for setting the node is [Raft](../design/consensus/raft.md), and the default setting is [PBFT](../design/consensus/pbft.md).

### **`C`option[**Optional**]**

Used to specify the chain identifier when building FISCO BCOS. When this option is set, using parameter to set `[chain].id` in the configuration file `config.ini`. The parameter range is a positive integer and the default setting is 1.

```bash
# The chain is identified as 2
$ bash build_chain.sh -l 127.0.0.1:2 -C 2
```

### **`g`option[**Optional**]**
No parameter option. When setting this option, to build the national cryptography version of FISCO BCOS. **The binary fisoc-bcos is required to be national cryptography version when using the `g` option.**

### **`z`option[**Optional**]**
No parameter option. When setting this option, the tar package of node is generated.

### **`t`option[**Optional**]**
This option is used to specify the certificate configuration file when certificate is generated.

### **`T`option[**Optional**]**

No parameter option. When setting this option, set the log level of node to DEBUG. The related configuration of log [reference here](./configuration.html#id6).

## Node file organization

- cert folder stores root certificate and organization certificate of the chain.
- The folder named by IP address stores the certificate configuration file required by related configuration of all nodes , `fisco-bcos` executable program, and SDK in the server.
- The `node*` folder under each IP folder stores configuration file required by the node. `config.ini` is the main configuration of node. In `conf` directory, to store certificate files and group related configurations. For the configuration detail, please refer to [here](configuration.md). Each node provides two scripts which are used to start and stop the node.
- Under each IP folder, two scripts providing `start_all.sh` and `stop_all.sh` are used to start and stop all nodes.

```bash
nodes/
├── 127.0.0.1
│   ├── fisco-bcos # binary program
│   ├── node0 # node0 folder
│   │   ├── conf # configuration folder
│   │   │   ├── ca.crt # chain root certificate
│   │   │   ├── group.1.genesis # the initialized configuration of group1, the file cannot be changed
│   │   │   ├── group.1.ini # the configuration file of group1
│   │   │   ├── node.crt # node certificate
│   │   │   ├── node.key # node private key
│   │   │   ├── node.nodeid # node id, represented by hexadecimal of public key
│   │   ├── config.ini # node main configuration file, to configure listening IP, port, etc.
│   │   ├── start.sh # start script, uses for starting node
│   │   └── stop.sh # stop script, uses for stopping node
│   ├── node1 # node1 folder
│   │.....
│   ├── node2 # node2 folder
│   │.....
│   ├── node3 # node3 folder
│   │.....
│   ├── sdk # SDK needs to be used
│   │   ├── ca.crt # chain root certificate
│   │   ├── sdk.crt # The certificate file required by SKD, to use when establishing a connection
│   │   └── sdk.key # The private key file required by SKD, to use when establishing a connection
├── cert # certificate folder
│   ├── agency # agency certificate folder
│   │   ├── agency.crt # agency certificate
│   │   ├── agency.key # agency private key
│   │   ├── agency.srl
│   │   ├── ca-agency.crt
│   │   ├── ca.crt
│   │   └── cert.cnf
│   ├── ca.crt # chain certificate
│   ├── ca.key # chain private key
│   ├── ca.srl
│   └── cert.cnf
```

## Example

### Single-server and single-group

To build a 4-node FISCO BCOS alliance chain on native machine for using the default start port `30300,20200,8545` (4 nodes will occupy `30300-30303`,`20200-20203`,`8545-8548`) and listening to the external network `Channel` and `jsonrpc` ports while allowing the external network interacts with node through SDK or API.

```bash
# to build FISCO BCOS alliance chain
$ bash build_chain.sh -l "127.0.0.1:4" -i
# after generating successes, to output `All completed` to mention
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 0.0.0.0
[INFO] Output Dir        : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
[INFO] CA Key Path       : /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /Users/fisco/WorkSpace/FISCO-BCOS/tools/nodes
```

### Add new node into Groups

This section takes Group1 generated in the previous section as an example to add a consensus node.

#### Generate private key certificates for new node

The next operation is done under the `nodes/127.0.0.1` directory generated in the previous section.

1. Acquisition certificate generation script

```bash
curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh
```

2. Generating new node private key certificates

```bash
# -c specify the path where the certificate and private key are located
# -o Output to the specified folder, where new certificates and private keys issued by agency agency1 exist in newNode/conf

bash gen_node_cert.sh -c nodes/cert/agency -o newNode
```

If you use guomi version of fisco, please execute below command to generate cert.
```bash
bash gen_node_cert.sh -c nodes/cert/agency -o newNodeGm -g nodes/gmcert/agency/
```

#### Preparing configuration files

1. Copy Node 0 Profile and Tool Script in Group 1

```bash
cp node0/config.ini newNode/config.ini
cp node0/conf/group.1.genesis newNode/conf/group.1.genesis
cp node0/conf/group.1.ini newNode/conf/group.1.ini
cp node0/*.sh newNode/
cp -r node0/scripts newNode/
```

2. Update IP and ports monitored in `newNode/config.ini`, include IP and Port in `[rpc]` and `[p2p]`。
3. Add new nodes to group 1 through console, refer to [here](./console.html#addsealer) and [here](./node_management.html#id7)
4. Add IP and Port in the new node's P2P configuration to the [p2p] field in the original node's config.ini. Assuming that the new node IP: Port is 127.0.0.1:30304, the modified [P2P] configuration is

    ```bash
    [p2p]
        listen_ip=0.0.0.0
        listen_port=30300
        ;enable_compress=true
        ; nodes to connect
        node.0=127.0.0.1:30300
        node.1=127.0.0.1:30301
        node.2=127.0.0.1:30302
        node.3=127.0.0.1:30303
        node.4=127.0.0.1:30304
    ```

#### Start a new node, check links and consensus

### Multi-server and multi-group

Using the build_chain script to build a multi-server and multi-group FISCO BCOS alliance chain requires the script configuration file. For details, please refer to [here](../manual/group_use_cases.md).

[build_chain]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/build_chain.sh
