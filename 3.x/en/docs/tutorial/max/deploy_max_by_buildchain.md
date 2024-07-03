# Build a Max version blockchain network with one click

Tags: "build _ chain" "build version of blockchain network"

----

```eval_rst
    The deployment tool build _ chain script aims to enable users to deploy and use FISCO BCOS Pro / max version blockchain without tars as quickly as possible
```

## 1. Script function introduction

The 'build _ chain.sh' script is used to quickly generate a configuration file for a node in a chain. The following describes the function of the pro / max script option for tarless versions:

### **'C 'Option [**Optional**]**

Script command, which supports' deploy '. The default value is' deploy':

- `deploy`: For deploying new nodes。

### **'g 'option [**Optional**]**

Set the group ID. If no group ID is set, the default value is group0。

### **'I 'option [**Optional**]**

Used to set the chain ID. If it is not set, the default value is chain0。

### **'V 'Options [**Optional**]**

Specifies the chain version (air, pro, max). The default value is air。

### **'l 'Options [**Optional**]**

The IP address of the generated node and the number of blockchain nodes deployed on the corresponding IP address. The parameter format is' ip1:nodeNum1, ip2:nodeNum2`。

The 'l' option for deploying two nodes on a machine with IP address' 192.168.0.1 'and four nodes on a machine with IP address' 127.0.0.1 'is as follows:
`192.168.0.1:2, 127.0.0.1:4`

### **'p 'option [**Optional**]**

Specifies the start port for listening to P2P, RPC, tars, tikv, and monitor services. The default start ports are 30300, 20200, 40400, 2379, and 3901。

Specify 30300 as the starting port for P2P service listening；An example of the starting port on which 20200 listens for the RPC service is as follows:

```
# Specify the P2P and RPC ports of the node. The remaining ports are the default values
-p 30300,20200
```

### **'e 'option [**Optional**]**

Specifies the path of the binary executable files of the existing local Pro / Max versions such as rpc, gateway, and nodef. If no path is specified, the latest version of the binary is pulled by default. The default address is in the binary folder. For example, the default address of the binary for the Pro version is BcosBuilder / pro / binary。

### **'y 'Options [**Optional**]**

Specifies the binary download method of rpc, gateway, and nodef, git, or cdn. Default value: cdn。

### **'v 'option [**Optional**]**

Specifies the binary download version of rpc, gateway, and nodef. The default value is v3.4.0。

### **'r 'Option [**Optional**]**

Specifies the binary download path of the rpc, gateway, or nodef service. By default, the file is downloaded to the binary folder。

### **'c 'option [**Optional**]**

Configuration file path for the specified service, which must include config.toml。

### **'t 'option [**Optional**]**

Specifies the service type of the operation(rpc，gateway，node)The default is All,

### **'o 'option [**Optional**]**

Specifies the directory where the generated node artifacts are located. The default directory is'. / generated'。

### **'s' option [**Optional**]**

Specify whether to build a full-link state-secret blockchain. The state-secret blockchain has the following features:

- **Blockchain Ledger Uses State Secret Algorithm**: Using sm2 signature verification algorithm, sm3 hash algorithm and sm4 symmetric encryption and decryption algorithm。
- **The state-secret SSL connection is used between the SDK client and the node**。
- **State-secret SSL connection between blockchain nodes**。

### **'h 'option [**Optional**]**

View Script Usage。

## 2. Build Max version of the block chain network

### 2.1 Installation Dependencies

The deployment tool 'BcosBuilder' depends on 'python3, curl, docker, docker-compose'. Depending on the operating system you are using, use the following command to install the dependency。

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

### 2.2 Deploy TiKV

**Download and install tiup**

```
$ curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh
```

**Start tikv v6.5.0**

```
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

### 2.3 Deploy Max Version Blockchain System

Here are four examples of deployment chains

1. Specify the ip and port of the service and automatically generate the configuration file

Execute the following command to deploy RPC services, gateway services, and node services
The starting ports of, tars and tikv are 30300, 20200, 40400 and 2379 respectively, and the ip addresses of the four institutions are 172.31.184.227, 172.30.93.111, 172.31.184.54 and 172.31.185.59, which automatically download the latest binary；

```
bash build_chain.sh -p 30300,20200,40400,2379 -l 172.31.184.227:1,172.30.93.111:1,172.31.184.54:1,172.31.185.59:1 -C deploy -V max -o generate -t all
```

2. Deployment of State Secret Chain

Execute the following command, specify the deployment state secret chain through -s, and specify the existing binary path through -e

```
bash build_chain.sh -p 30300,20200,40400,2379 -l 172.31.184.227:1,172.30.93.111:1,172.31.184.54:1,172.31.185.59:1 -C deploy -V max -o generate -t all -e ./binary -s
```

3. Specify the download binary version

Run the following command to specify the method for downloading the binary as cdn, version v3.4.0, and download path binaryPath

```
bash build_chain.sh -p 30300,20200,40400,2379 -l 172.31.184.227:1,172.30.93.111:1,172.31.184.54:1,172.31.185.59:1 -C deploy -V max -o generate -y cdn -v v3.4.0 -r ./binaryPath 
```

4. Specify the existing configuration file

Run the following command to deploy the max chain based on the existing configuration file

```
bash build_chain.sh -c config.toml -C deploy -V max -o generate -t all
```