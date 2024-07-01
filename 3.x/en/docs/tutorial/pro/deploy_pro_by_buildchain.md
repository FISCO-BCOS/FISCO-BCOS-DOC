# Build a Pro version of the block chain network with one click

Tags: "build _ chain" "build version of blockchain network"

----

```eval_rst
    The deployment tool build _ chain script aims to enable users to deploy and use FISCO BCOS Pro / max version blockchain without tars as quickly as possible.
```

## 1. Script function introduction

The 'build _ chain.sh' script is used to quickly generate a configuration file for a node in a chain. The following describes the function of the pro / max script option for tarless versions:

### **'C 'Option [**Optional**]**

Script command, which supports' deploy '. The default value is' deploy':

- `deploy`: For deploying new nodes。

### **'g 'option [**Optional**]**

Set the group ID. If no group ID is set, the default value is group0.。

### **'I 'option [**Optional**]**

Used to set the chain ID. If it is not set, the default value is chain0.。

### **'V 'Options [**Optional**]**

Specifies the chain version (air, pro, max). The default value is air.。

### **'l 'Options [**Optional**]**

The IP address of the generated node and the number of blockchain nodes deployed on the corresponding IP address. The parameter format is' ip1.:nodeNum1, ip2:nodeNum2`。

The 'l' option for deploying two nodes on a machine with IP address' 192.168.0.1 'and four nodes on a machine with IP address' 127.0.0.1 'is as follows:
`192.168.0.1:2, 127.0.0.1:4`

### **'p 'option [**Optional**]**

Specifies the start port for listening to P2P, RPC, tars, tikv, and monitor services. The default start ports are 30300, 20200, 40400, 2379, and 3901.。

Specify 30300 as the starting port for P2P service listening；An example of the starting port on which 20200 listens for the RPC service is as follows:

```
# Specify the P2P and RPC ports of the node. The remaining ports are the default values.
-p 30300,20200
```

### **'e 'option [**Optional**]**

Specifies the path of the binary executable files of the existing local Pro / Max versions such as rpc, gateway, and nodef. If no path is specified, the latest version of the binary is pulled by default. The default address is in the binary folder. For example, the default address of the binary for the Pro version is BcosBuilder / pro / binary.。

### **'y 'Options [**Optional**]**

Specifies the binary download method of rpc, gateway, and nodef, git, or cdn. Default value: cdn。

### **'v 'option [**Optional**]**

Specifies the binary download version of rpc, gateway, and nodef. The default value is v3.4.0.。

### **'r 'Option [**Optional**]**

Specifies the binary download path of the rpc, gateway, or nodef service. By default, the file is downloaded to the binary folder.。

### **'c 'option [**Optional**]**

Configuration file path for the specified service, which must include config.toml。

### **'t 'option [**Optional**]**

Specifies the service type of the operation(rpc，gateway，node)The default is All,

### **'o 'option [**Optional**]**

Specifies the directory where the generated node artifacts are located. The default directory is'. / generated'。

### **'s' option [**Optional**]**

Specify whether to build a full-link state-secret blockchain. The state-secret blockchain has the following features:

- **Blockchain Ledger Uses State Secret Algorithm**: Using sm2 signature verification algorithm, sm3 hash algorithm and sm4 symmetric encryption and decryption algorithm。
- **The state-secret SSL connection is used between the SDK client and the node.**。
- **State-secret SSL connection between blockchain nodes**。

### **'h 'option [**Optional**]**

View Script Usage。

## 2. Build a Pro version of the block chain network

### 2.1 Installation Dependencies

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

```
brew install curl docker docker-compose python3 wget
```

### 2.2 Deploy a blockchain network without tars Pro

Here are four examples of deployment chains

1. Specify the ip and port of the service and automatically generate the configuration file

Run the following command to deploy the RPC service, gateway service, and node service.
and tars start ports are 30300, 20200, 40400, respectively, the ip of the two institutions is 172.31.184.227, 172.30.93.111, each institution has two nodes, automatically download the latest binary；

```
bash build_chain.sh -p 30300,20200,40400 -l 172.31.184.227:2,172.30.93.111:2 -C deploy -V pro -o generate -t all
```

2. Deployment of State Secret Chain

Execute the following command through-s designated deployment state-secret chain, through-e specifies that a binary path already exists

```
bash build_chain.sh -p 30300,20200,40400 -l 172.31.184.227:2,172.30.93.111:2 -C deploy -V pro -o generate -t all -e ./binary -s
```

3. Specify the download binary version

Run the following command to deploy the RPC service, the Gateway service, and the node service. Specify the download method of the binary as cdn, v3.4.0, and the download path binaryPath.

```
bash build_chain.sh -p 30300,20200 -l 172.31.184.227:2,172.30.93.111:2 -C deploy -V pro -o generate -y cdn -v v3.4.0 -r ./binaryPath 
```

4. Specify the existing configuration file

Run the following command to deploy the pro chain based on the existing configuration file

```
bash build_chain.sh -c config.toml -C deploy -V pro -o generate -t all
```