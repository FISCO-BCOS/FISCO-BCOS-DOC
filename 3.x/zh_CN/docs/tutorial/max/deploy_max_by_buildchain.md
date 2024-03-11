# build_chain一键搭建Max版本区块链网络

标签：``build_chain`` ``搭建版区块链网络``

----

```eval_rst
    部署工具build_chain 脚本目标是让用户最快的部署和使用FISCO BCOS 无tars版Pro/max版本区块链
```

## 1. 脚本功能介绍

`build_chain.sh`脚本用于快速生成一条链中节点的配置文件，以下为用于无tars版pro/max 脚本选项功能介绍：

### **`C`选项[**Optional**]**

脚本的命令，支持 `deploy` ，默认为`deploy`:

- `deploy`: 用于部署新节点。

### **`g`选项[**Optional**]**

用于设置群组ID，若不设置，则默认为group0。

### **`I`选项[**Optional**]**

用于设置链ID，若未设置，则默认为chain0。

### **`V`选项[**Optional**]**

指定链版本（air、pro、max）,默认为air。

### **`l`选项[**Optional**]**

生成节点的IP与对应IP上部署的区块链节点数目，参数格式为 `ip1:nodeNum1, ip2:nodeNum2`。

在IP为`192.168.0.1`的机器上部署2个节点，IP为`127.0.0.1`的机器上部署4个节点的`l`选项示例如下：
`192.168.0.1:2, 127.0.0.1:4`

### **`p`选项[**Optional**]**

指定节点P2P、RPC、tars、tikv、monitor服务的监听的起始端口，默认起始端口分别为30300、20200、40400、2379、3901。

指定30300为P2P服务监听的起始端口；20200为RPC服务监听的起始端口示例如下：

```
# 指定节点P2P、RPC端口，其余端口为默认值
-p 30300,20200
```

### **`e`选项[**Optional**]**

指定本地已存在的Pro/Max版本rpc、gateway、nodef服务的二进制可执行文件路径，若不指定，则默认拉取最新版本的二进制，默认地址为binary文件夹内，例如pro版的二进制默认地址为BcosBuilder/pro/binary。

### **`y`选项[**Optional**]**

指定rpc、gateway、nodef服务的二进制下载方式，git或cdn，默认cdn。

### **`v`选项[**Optional**]**

指定rpc、gateway、nodef服务的二进制下载版本，默认为v3.4.0。

### **`r`选项[**Optional**]**

指定rpc、gateway、nodef服务的二进制下载路径，默认下载到binary文件夹。

### **`c`选项[**Optional**]**

用于指定服务的配置文件路径，此路径须包括config.toml。

### **`t`选项[**Optional**]**

指定操作的服务类型(rpc，gateway，node)，默认为All,

### **`o`选项[**Optional**]**

指定生成的节点产物所在的目录，默认目录为 `./generated` 。

### **`s`选项[**Optional**]**

指定是否搭建全链路的国密区块链，国密区块链有如下特性：

- **区块链账本使用国密算法**: 使用sm2签名验签算法、sm3哈希算法以及sm4对称加解密算法。
- **SDK客户端与节点间采用国密SSL连接**。
- **区块链节点之间采用国密SSL连接**。

### **`h`选项[**Optional**]**

查看脚本使用用法。

## 2.搭建Max版区块链网络

### 2.1 安装依赖

部署工具`BcosBuilder`依赖`python3, curl, docker, docker-compose`，根据您使用的操作系统，使用以下命令安装依赖。

**安装Ubuntu依赖(版本不小于Ubuntu18.04)**

```shell
sudo apt-get update
sudo apt-get install -y curl docker.io docker-compose python3 wget
```

**安装CentOS依赖(版本不小于CentOS 7)**

```shell
sudo yum install -y curl docker docker-compose python3 python3-devel wget
```

**安装macOS依赖**

```
brew install curl docker docker-compose python3 wget
```

### 2.2部署TiKV

**下载和安装tiup**

```
$ curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh
```

**启动tikv v6.5.0**

```
# 部署并启动tikv(这里设机器的物理ip为172.25.0.3)
$ nohup tiup playground v6.5.0 --mode tikv-slim --host=172.25.0.3 -T tikv_demo --without-monitor > ~/tikv.log 2>&1 &
# 获取tikv监听端口(tikv的默认监听端口是2379)
$ cat ~/tikv.log
tiup is checking updates for component playground ...timeout!
Starting component `playground`: /home/fisco/.tiup/components/playground/v1.9.4/tiup-playground v6.5.0 --mode tikv-slim --host=172.25.0.3 -T tikv_demo --without-monitor
Playground Bootstrapping...
Start pd instance:v6.5.0
Start tikv instance:v6.5.0
PD client endpoints: [172.25.0.3:2379]
```

### 2.3部署Max版本区块链系统

以下为4种部署链的示例

1、指定服务的ip和端口，自动生成配置文件

执行如下命令，可部署4机构RPC服务、Gateway服务和节点服务，p2p、gateway
、tars和tikv起始端口分别为30300、20200、40400、2379，4机构的ip为172.31.184.227、172.30.93.111、172.31.184.54、172.31.185.59，自动下载最新的二进制；

```
bash build_chain.sh -p 30300,20200,40400,2379 -l 172.31.184.227:1,172.30.93.111:1,172.31.184.54:1,172.31.185.59:1 -C deploy -V max -o generate -t all
```

2、部署国密链

执行如下命令，通过-s 指定部署国密链，通过-e 指定已有二进制路径

```
bash build_chain.sh -p 30300,20200,40400,2379 -l 172.31.184.227:1,172.30.93.111:1,172.31.184.54:1,172.31.185.59:1 -C deploy -V max -o generate -t all -e ./binary -s
```

3、指定下载二进制版本

执行如下命令，指定下载二进制的方式为cdn、版本v3.4.0和下载路径binaryPath

```
bash build_chain.sh -p 30300,20200,40400,2379 -l 172.31.184.227:1,172.30.93.111:1,172.31.184.54:1,172.31.185.59:1 -C deploy -V max -o generate -y cdn -v v3.4.0 -r ./binaryPath 
```

4、指定已有的配置文件

执行如下命令，根据已有的配置文件进行部署max链

```
bash build_chain.sh -c config.toml -C deploy -V max -o generate -t all
```