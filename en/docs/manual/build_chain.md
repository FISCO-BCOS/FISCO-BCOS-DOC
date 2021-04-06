# Chain building script

```eval_rst
.. important::
    The goal of the script is to let users apply FISCO BCOS as quickly as possible. For the enterprise applications deploying FISCO BCOS, please refer to `Enterprise Deployment Tools <../enterprise_tools/index.html>`_ .
```

FISCO BCOS has provided `build_chain` script to help users quickly build FISCO BCOS alliance chain. By default, the script downloads `master` branch of the latest version pre-compiles executable program from [GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS)for building related environment.

## Script introduction

- `build_chain.sh` is used to quickly generate configuration files of a chain node. For the script that depends on `openssl`, please according your own operating system to install `openssl 1.0.2` version and above. The source code of script is located at [here](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/build_chain.sh).

- For quick experience can use the `-l` option to specify the node IP and number. `-f` option supports the creation of FISCO BCOS chains for complex business scenarios by using a configuration file in a specified format. **`-l` and `-f` options must be specified uniquely and cannot coexist**.
- It is recommended to use `-T` option for testing. `-T` enables log level to DEBUG, **p2p module listens for `0.0.0.0` by default**.

```eval_rst
.. note::
    In order to facilitate development and experience, the default listening IP of the P2P module is `0.0.0.0`. For security reasons, please modify it to a safe listening address according to the actual business network situation, such as the internal IP or a specific external IP.
```

## Help

```bash
Usage:
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -f <IP list file>                   [Optional] split by line, every line should be "ip:nodeNum agencyName groupList p2p_port,channel_port,jsonrpc_port". eg "127.0.0.1:4 agency1 1,2 30300,20200,8545"
    -v <FISCO-BCOS binary version>      Default is the latest v${default_version}
    -e <FISCO-BCOS binary path>         Default download fisco-bcos from GitHub. If set -e, use the binary at the specified location
    -o <Output Dir>                     Default ./nodes/
    -p <Start Port>                     Default 30300,20200,8545 means p2p_port start from 30300, channel_port from 20200, jsonrpc_port from 8545
    -q <List FISCO-BCOS releases>       List FISCO-BCOS released versions
    -i <Host ip>                        Default 127.0.0.1. If set -i, listen 0.0.0.0
    -s <DB type>                        Default RocksDB. Options can be RocksDB / mysql / Scalable, RocksDB is recommended
    -d <docker mode>                    Default off. If set -d, build with docker
    -c <Consensus Algorithm>            Default PBFT. Options can be pbft / raft /rpbft, pbft is recommended
    -C <Chain id>                       Default 1. Can set uint.
    -g <Generate guomi nodes>           Default no
    -z <Generate tar packet>            Default no
    -t <Cert config file>               Default auto generate
    -6 <Use ipv6>                       Default no. If set -6, treat IP as IPv6
    -k <The path of ca root>            Default auto generate, the ca.crt and ca.key must in the path, if use intermediate the root.crt must in the path
    -K <The path of sm crypto ca root>  Default auto generate, the gmca.crt and gmca.key must in the path, if use intermediate the gmroot.crt must in the path
    -D <Use Deployment mode>            Default false, If set -D, use deploy mode directory struct and make tar
    -G <channel use sm crypto ssl>      Default false, only works for guomi mode
    -X <Certificate expiration time>    Default 36500 days
    -T <Enable debug log>               Default off. If set -T, enable debug log
    -S <Enable statistics>              Default off. If set -S, enable statistics
    -F <Disable log auto flush>         Default on. If set -F, disable log auto flush
    -E <Enable free_storage_evm>        Default off. If set -E, enable free_storage_evm
    -h Help
e.g
    ./manual/build_chain.sh -l 127.0.0.1:4
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

**Suppose the above file is named `ipconf`**, using the following command to build a chain, which indicates to use configuration file, to set the log level to `DEBUG`.

```bash
$ bash build_chain.sh -f ipconf -T
```

### **`e`option[**Optional**]**

is used to specify **full path** where `fisco-bcos` binary is located.Script will cope `fisco-bcos` to the directory named by IP number. If no path to be specified, the latest binary program of `master` branch is downloaded from GitHub by default.

```bash
# download the latest release binary from GitHub to generate native 4 nodes
$ bash build_chain.sh -l 127.0.0.1:4
# use bin/fisco-bcos binary to generate native 4 nodes
$ bash build_chain.sh -l 127.0.0.1:4 -e bin/fisco-bcos
```

### **`o`option[**Optional**]**

specifies the directory where the generated configuration is located.

### **`p`option[**Optional**]**
specifies the starting port of the node. Each node occupies three ports which are p2p, channel, and jsonrpc, respectively. The ports are split by, and three ports must be specified. The ports used by different nodes under the same IP address are incremented from the starting port.

```bash
# Two nodes occupies `30300,20200,8545` and `30301,20201,8546` respectively.
$ bash build_chain -l 127.0.0.1:2 -p 30300,20200,8545
```

### **`q`选项[**Optional**]**

List FISCO BCOS released version numbers.

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
There are parameter options. The parameter is the name of db. Currently it supports three modes: RocksDB, mysql and Scalable. RocksDB is used by default.

- RocksDB use RocksDB as backend database.
- mysql needs to configure the information relates to mysql in the group ini file.
- Scalable mode, block data and state data are stored in different RocksDB databases, and block data is stored in RocksDB instance named after block height. The RocksDB instance used to store block data is scroll according to the configuration `scroll_threshold_multiple`*1000 and block height. If chain data need to be tailored, the Scalable mode must be used.

### **`c`option[**Optional**]**
There are parameter options. The parameter is the consensus algorithm type, and currently supports PBFT, Raft, rPBFT. The default consensus algorithm is PBFT.

- `PBFT`：Set the node consensus algorithm to [PBFT](../design/consensus/pbft.md).
- `Raft`：Set the node consensus algorithm to [Raft](../design/consensus/raft.md).
- `rPBFT`：Set the node consensus algorithm to rPBFT.

### **`C`option[**Optional**]**

Used to specify the chain identifier when building FISCO BCOS. When this option is set, using parameter to set `[chain].id` in the configuration file `config.ini`. The parameter range is a positive integer and the default setting is 1.

```bash
# The chain is identified as 2
$ bash build_chain.sh -l 127.0.0.1:2 -C 2
```

### **`g`option[**Optional**]**
No parameter option. When setting this option, to build the national cryptography version of FISCO BCOS.

### **`z`option[**Optional**]**
No parameter option. When setting this option, the tar package of node is generated.

### **`t`option[**Optional**]**
This option is used to specify the certificate configuration file when certificate is generated.

```bash
[ca]
default_ca=default_ca
[default_ca]
default_days = 365
default_md = sha256

[req]
distinguished_name = req_distinguished_name
req_extensions = v3_req
[req_distinguished_name]
countryName = CN
countryName_default = CN
stateOrProvinceName = State or Province Name (full name)
stateOrProvinceName_default =GuangDong
localityName = Locality Name (eg, city)
localityName_default = ShenZhen
organizationalUnitName = Organizational Unit Name (eg, section)
organizationalUnitName_default = fisco-bcos
commonName =  Organizational  commonName (eg, fisco-bcos)
commonName_default = fisco-bcos
commonName_max = 64

[ v3_req ]
basicConstraints = CA:FALSE
keyUsage = nonRepudiation, digitalSignature, keyEncipherment

[ v4_req ]
basicConstraints = CA:TRUE
```

### **`6`选项[**Optional**]**

Use IPv6 mode, listen `::`

### **`T`option[**Optional**]**

No parameter option. When setting this option, set the log level of node to DEBUG. The related configuration of log [reference here](../manual/configuration.html#id6).

### **`k`option[**Optional**]**
Use the private key specified by the user and the certificate issued the agency and node certification. The parameter is the path of ca.crt/ca.key. If the specified private key and certificate are intermediate Ca, root.crt should also be included in this folder to store the upper certificate chain.

### **`K`option[**Optional**]**
Use the private key specified by the user and the certificate issued the agency and node certification in guomi mode. The parameter is the path of gmca.crt/gmca.key. If the specified private key and certificate are intermediate Ca, gmroot.crt should also be included in this folder to store the upper certificate chain.

### **`G`选项[**Optional**]**
From 2.5.0, when use smcrypto mode, user can config to use GM SSL between node and sdk, the option set `chain.sm_crypto_channel=true`.

### **`D`option[**Optional**]**
No parameter option. When this option is set, the directory name of the generated node is IP_P2P-port.


### **`E`option[**Optional**]**

No parameter option, when setting this option, [Free Storage] (../design/virtual_machine/gas.html#evm-gas) Gas mode is enabled, and `Free Storage` Gas mode is disabled by default.

## Node file organization

- cert folder stores root certificate and organization certificate of the chain.
- The folder named by IP address stores the certificate configuration file required by related configuration of all nodes , `fisco-bcos` executable program, and SDK in the server.
- The `node*` folder under each IP folder stores configuration file required by the node. `config.ini` is the main configuration of node. In `conf` directory, to store certificate files and group related configurations. For the configuration detail, please refer to [here](../manual/configuration.md). Each node provides two scripts which are used to start and stop the node.
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
|   |   ├── gm # SDK sm ssl connection with nodes configuration，note：this directory is only generated when sm blockchain environment is generated for the node to make SSL connection with the SDK
|   |   │   ├── gmca.crt # sm ssl connection root certificate
|   |   │   ├── gmensdk.crt # sm ssl connection encrypt certificate
|   |   │   ├── gmensdk.key # sm ssl connection encrypt certificate key
|   |   │   ├── gmsdk.crt # sm ssl connection sign certificate
|   |   │   └── gmsdk.key # sm ssl connection sign certificate key
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

## Scripts generated by build_chain

### start_all.sh

start all nodes in current directory

### stop_all.sh

stop all nodes in current directory

### download_console.sh

download console

- `v` specific version of console
- `f` automatically configure console

### download_bin.sh

download fisco-bcos precompiled binary

```bash
Usage:
    -v <Version>           Download binary of spectfic version, default latest
    -b <Branch>            Download binary of spectfic branch
    -o <Output Dir>        Default ./bin
    -l                     List FISCO-BCOS released versions
    -m                     Download mini binary, only works with -b option
    -h Help
e.g
    ./download_bin.sh -v 2.7.1
```


## Example

### Four nodes of group 1 on a local server

To build a 4-node FISCO BCOS alliance chain on native machine for using the default start port `30300,20200,8545` (4 nodes will occupy `30300-30303`,`20200-20203`,`8545-8548`) and listening to the external network `Channel` and `jsonrpc` ports while allowing the external network interacts with node through SDK or API.

```bash
# to build FISCO BCOS alliance chain
$ bash build_chain.sh -l 127.0.0.1:4
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
[INFO] RPC listen IP     : 127.0.0.1
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
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh
```

```eval_rst
.. note::
    - If the script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_node_cert.sh`
```

2. Generating new node private key certificates

```bash
# -c specify the path where the certificate and private key are located
# -o Output to the specified folder, where new certificates and private keys issued by agency agency1 exist in newNode/conf

bash gen_node_cert.sh -c ../cert/agency -o newNode
```

If you use guomi version of fisco, please execute below command to generate cert.
```bash
bash gen_node_cert.sh -c ../cert/agency -o newNodeGm -g ../gmcert/agency/
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
3. Add IP and Port in the new node's P2P configuration to the [p2p] field in the original node's config.ini. Assuming that the new node IP: Port is 127.0.0.1:30304, the modified [P2P] configuration is

    ```bash
    [p2p]
        listen_ip=0.0.0.0
        listen_port=30304
        ;enable_compress=true
        ; nodes to connect
        node.0=127.0.0.1:30300
        node.1=127.0.0.1:30301
        node.2=127.0.0.1:30302
        node.3=127.0.0.1:30303
        node.4=127.0.0.1:30304
    ```

4. Start node, use `newNode/start.sh`
5. Add new nodes to group 1 through console, refer to [here](../console/console.html#addsealer) and [here](./node_management.html#id7)

#### Start a new node, check links and consensus

### generate new SDK certificate of agency

The next operation is done under the `nodes/127.0.0.1` directory generated in the previous section.

1. Acquisition certificate generation script

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh
```

```eval_rst
.. note::
    - If the script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_node_cert.sh`
```

2. Generating new node private key certificates

```bash
# -c specify the path where the certificate and private key are located
# -o Output to the specified folder, where new certificates and private keys issued by agency exist in newSDK

bash gen_node_cert.sh -c ../cert/agency -o newSDK -s
```

If you use guomi version of fisco, please execute below command to generate cert.
```bash
bash gen_node_cert.sh -c ../cert/agency -o newSDK -g ../gmcert/agency/ -s
```


### Generating new agency private key certificates

1. Acquisition agency certificate generation script

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_agency_cert.sh
```

```eval_rst
.. note::
    - If the script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_agency_cert.sh`
```

2. Generating new agency private key certificates

```bash
# -c path must have ca.crt and ca.key， if use intermediate ca，then root.crt is needed
# -g path must have gmca.crt and gmca.key， if use intermediate ca，then gmroot.crt is needed
# -a newAgencyName
bash gen_agency_cert.sh -c nodes/cert/ -a newAgencyName
```

国密版本请执行下面的指令。
```bash
bash gen_agency_cert.sh -c nodes/cert/ -a newAgencyName -g nodes/gmcert/
```


### Multi-server and multi-group

Using the build_chain script to build a multi-server and multi-group FISCO BCOS alliance chain requires the script configuration file. For details, please refer to [here](../manual/group_use_cases.md).

[build_chain]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/manual/build_chain.sh

