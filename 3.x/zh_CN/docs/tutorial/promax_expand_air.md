# pro链或max链扩容air节点

build_chian.sh脚本提供了pro链/max链扩容air节点的功能，本章在搭建pro链/max链的基础上扩容一个新air区块链节点，帮助用户掌握pro版本链/max版本链扩容air节点的扩容步骤。

## pro扩容air节点

### 1. 部署pro链

 指定服务的ip和端口，自动生成配置文件，执行如下命令，可部署无tars pro链，p2p、rpc和tars起始端口分别为31300、21200、41400，机构的ip为172.31.184.227，机构下有两个节点，自动下载最新的二进制服务（具体的部署依赖项目可查看具体的pro链部署文档）；

```eval_rst
bash build_chain.sh -p 31300,21200,41400 -l 172.31.184.227:2 -C deploy -V pro -o generate -t all
```

生成的节点目录如下

```
$ tree generate/172.31.184.227 
generate/172.31.184.227
├── gateway_31300
│   ├── BcosGatewayService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── nodes.json
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_node_41402
│   ├── BcosNodeService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── node.nodeid
│   │   ├── node.pem
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_node_41407
│   ├── BcosNodeService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── node.nodeid
│   │   ├── node.pem
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── rpc_21200
│   ├── BcosRpcService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── sdk
│   │   │   ├── ca.crt
│   │   │   ├── cert.cnf
│   │   │   ├── sdk.crt
│   │   │   ├── sdk.key
│   │   │   └── sdk.nodeid
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── start_all.sh
└── stop_all.sh
```

### 2. 所需准备文件

Air版本区块链扩容时，需要提前准备证书和配置文件，用于生成扩容节点配置，需要准备的文件包括：

- **CA证书和CA私钥**: 用于为新扩容的节点颁发节点证书
- **节点配置文件`config.ini`**: 可从已有的节点目录中拷贝。
- **节点创世块配置文件`config.genesis`**: 可从已有的节点目录中拷贝。
- **节点连接配置`nodes.json`**: 配置所有节点连接的IP和端口信息，可从已有的节点目录中拷贝，并加上新节点的IP和端口。
- **fisco-bcos 二进制文件**

```shell
# 创建扩容配置存放目录
$ mkdir config

# 拷贝gateway中根证书、根证书私钥
$ cp -r generate/gateway/chain0/ca config

# 从被扩容节点group0_node_41402拷贝节点配置文件config.ini，创世块配置文件config.genesis
$ cp generate/172.31.184.227/group0_node_41402/conf/config.ini config/
$ cp generate/172.31.184.227/group0_node_41402/conf/config.genesis config/

# 从被扩容节点gateway_31300拷贝节点连接配置文件nodes.json
# 此处记得查看gateway_31300中nodes.json中的值，如果为{"nodes": 1} ，要修改为具体的ip和端口，例如本例子中为{"nodes": ["172.31.184.227:31300"]}
$ cp generate/172.31.184.227/gateway_31300/conf/nodes.json config/

```

### 3.扩容air节点

```ini
# 命令如下

# 调用build_chain.sh扩容节点，新节点扩容到nodes/127.0.0.1/node4目录
# -c: 指定扩容配置config.ini, config.genesis和nodes.json路径
# -d: 指定CA证书和私钥的路径
# -o: 指定扩容节点配置所在目录
# -e: 指定扩容节点fisco-bcos二进制路径
bash build_chain.sh -C expand -c config -d config/ca -o expandAirNode/node0 -e fisco-bcos 

```

### 4. 修改相关配置

```shell
# 复制fisco-bcos二进制到扩容节点
cp ./fisco-bcos ./expandAirNode

# 复制tars_proxy.ini文件到扩容节点配置文件目录
cp generate/172.31.184.227/group0_node_41402/conf/tars_proxy.ini  ./expandAirNode/node0/conf/

# 在扩容节点config.ini中添加ca路径
[cert]
    ; directory the certificates located in
    ca_path=./conf
    ; the ca certificate file
    ca_cert=ca.crt
    ; the node private key file
    node_key=ssl.key
    ; the node certificate file
    node_cert=ssl.crt

```

### 5. 将新扩容节点加入到群组

将pro链中的rpc中的sdk证书复制到控制台，修改控制台中的配置文件config.toml 中的[network]中的peers中的ip和端口，之后启动控制台；

**步骤1：获取扩容节点的NodeID**

新节点扩容成功后，可通过控制台的`getGroupPeers`命令查看新增的节点列表，`getSealerList`命令查看已有共识节点：

```shell
[group0]: /apps> getGroupPeers
peer0: 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f
peer1: 8a63a72ca74ec925e355da4c20bce34e3ff4d287d3c5adbc4b27fa263726a5eff0f25065d77a10c07ab86f9f1f0913382dfaca2985520eedf388e4c2781f4373
peer2: bf403537b4de11d45ccb1b67dfd5bafc77ea31b4388aca291a9f54759fa03e736a7fd319ce6bdf46777c093963f6a9c9fafea9ab8c9b0814a335c503e7e33442

[group0]: /apps> getSealerList 
[
    Sealer{
        nodeID='8a63a72ca74ec925e355da4c20bce34e3ff4d287d3c5adbc4b27fa263726a5eff0f25065d77a10c07ab86f9f1f0913382dfaca2985520eedf388e4c2781f4373',
        weight=1
    },
    Sealer{
   nodeID='bf403537b4de11d45ccb1b67dfd5bafc77ea31b4388aca291a9f54759fa03e736a7fd319ce6bdf46777c093963f6a9c9fafea9ab8c9b0814a335c503e7e33442',
        weight=1
    }
]
```

从控制台输出可看出，peer0: 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f 的节点不在群组内，使用控制台`addObserver`命令将其加入到观察节点如下：


**步骤2: 将扩容节点加入为观察节点**

```shell
[group0]: /apps> addObserver 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f
{
    "code":0,
    "msg":"Success"
}
```

**步骤3：扩容节点同步到最高块后，将扩容节点加入为共识节点**

```shell
[group0]: /apps> addSealer 4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f 1
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> getSealerList 
[
    Sealer{
        nodeID='8a63a72ca74ec925e355da4c20bce34e3ff4d287d3c5adbc4b27fa263726a5eff0f25065d77a10c07ab86f9f1f0913382dfaca2985520eedf388e4c2781f4373',
        weight=1
    },
    Sealer{
        nodeID='bf403537b4de11d45ccb1b67dfd5bafc77ea31b4388aca291a9f54759fa03e736a7fd319ce6bdf46777c093963f6a9c9fafea9ab8c9b0814a335c503e7e33442',
        weight=1
    },
    Sealer{
        nodeID='4bd3f875ffaf3e00ad8330188df432cf4fb74b9cfdb958ae79af77fc9508a7f9a55826e4b91b0b48a1e526773849d576e528e3911deb54edd2e052065a28761f',
        weight=1
    }
]

```

**步骤4：部署和调用合约**

```shell
[group0]: /apps> deploy HelloWorld
transaction hash: 0x35facffef4b3d021ded67d088bd978958753a74b80ea2ddcef4bb2c28d76ed4c
contract address: 0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085
currentAccount: 0xd302016b12b6c65605c4f5e4f04b05bf3ba195d1
```

## max链扩容air节点

### 1.部署max链

以机器172.30.35.60为例，部署max链，部署单机构RPC服务、Gateway服务和节点服务，p2p、rpc、tars和tikv起始端口分别为31300、21200、41400、2379，机构的ip为172.30.35.60，自动下载最新的二进制服务（具体的部署依赖项目可查看具体的max链部署文档）；

#### 部署TiKV

```
# 下载和安装tiup
$ curl --proto '=https' --tlsv1.2 -sSf https://tiup-mirrors.pingcap.com/install.sh | sh

# 启动tikv v6.5.0
# 部署并启动tikv(这里设机器的物理ip为172.30.35.60)
$ nohup tiup playground v6.5.0 --mode tikv-slim --host=172.30.35.60 -T tikv_demo --without-monitor > ~/tikv.log 2>&1 &

# 获取tikv监听端口(tikv的默认监听端口是2379)
$ cat ~/tikv.log
tiup is checking updates for component playground ...timeout!
Starting component `playground`: /home/fisco/.tiup/components/playground/v1.9.4/tiup-playground v6.5.0 --mode tikv-slim --host=172.30.35.60 -T tikv_demo --without-monitor
Playground Bootstrapping...
Start pd instance:v6.5.0
Start tikv instance:v6.5.0
PD client endpoints: [172.30.35.60:2379]
```

#### 部署Max版本区块链系统

执行如下命令，可部署单机构RPC服务、Gateway服务和节点服务，p2p、rpc、tars和tikv起始端口分别为30300、20200、40400、2379；

```
bash build_chain.sh -p 31300,21200,41400,2379 -l 172.30.35.60:1 -C deploy -V max -o generate -t all
```

生成的节点目录如下

```
$ tree generate/172.30.35.60
generate/172.30.35.60
├── gateway_31300
│   ├── BcosGatewayService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── nodes.json
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_executor_41402
│   ├── BcosExecutorService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── group0_max_node_41402
│   ├── BcosMaxNodeService
│   ├── conf
│   │   ├── config.genesis
│   │   ├── config.ini
│   │   ├── node.nodeid
│   │   ├── node.pem
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── rpc_21200
│   ├── BcosRpcService
│   ├── conf
│   │   ├── ca.crt
│   │   ├── cert.cnf
│   │   ├── config.ini
│   │   ├── sdk
│   │   │   ├── ca.crt
│   │   │   ├── cert.cnf
│   │   │   ├── sdk.crt
│   │   │   ├── sdk.key
│   │   │   └── sdk.nodeid
│   │   ├── ssl.crt
│   │   ├── ssl.key
│   │   ├── ssl.nodeid
│   │   ├── tars.conf
│   │   └── tars_proxy.ini
│   ├── start.sh
│   └── stop.sh
├── start_all.sh
└── stop_all.sh
```

### 2. 所需准备文件

Air版本区块链扩容时，需要提前准备证书和配置文件，用于生成扩容节点配置，步骤如下：

```
# 创建扩容配置存放目录
$ mkdir config

# 拷贝gateway中根证书、根证书私钥
$ cp -r generate/gateway/chain0/ca ./config 

# 从被扩容节点group0_max_node_41402拷贝节点配置文件config.ini，创世块配置文件config.genesis
$ cp generate/172.30.35.60/group0_max_node_41402/conf/config.ini config/
$ cp generate/172.30.35.60/group0_max_node_41402/conf/config.genesis config/

# 从被扩容节点gateway_31300拷贝节点连接配置文件nodes.json
# 此处记得查看gateway_31300中nodes.json中的值，如果为{"nodes": 1} ，要修改为具体的ip和端口，例如本例子中为{"nodes": ["172.30.35.60:31300"]}
$ cp generate/172.30.35.60/gateway_31300/conf/nodes.json config/
```

### 3.扩容air节点

```ini
# 命令如下

# 调用build_chain.sh扩容节点，新节点扩容到nodes/127.0.0.1/node4目录
# -c: 指定扩容配置config.ini, config.genesis和nodes.json路径
# -d: 指定CA证书和私钥的路径
# -o: 指定扩容节点配置所在目录
# -e: 指定扩容节点fisco-bcos二进制路径
bash build_chain.sh -C expand -c config -d config/ca -o expandAirNode/node0 -e fisco-bcos 

```

### 4. 修改相关配置

```
# 复制fisco-bcos二进制到扩容节点
cp ./fisco-bcos ./expandAirNode

# 复制tars_proxy.ini文件到扩容节点配置文件目录
cp generate/172.30.35.60/group0_max_node_41402/conf/tars_proxy.ini  ./expandAirNode/node0/conf/

# 在扩容节点config.ini中添加ca路径
[cert]
    ; directory the certificates located in
    ca_path=./conf
    ; the ca certificate file
    ca_cert=ca.crt
    ; the node private key file
    node_key=ssl.key
    ; the node certificate file
    node_cert=ssl.crt
```

### 5. 将新扩容节点加入到群组

将max链中的rpc中的sdk证书复制到控制台，修改控制台中的配置文件config.toml 中的[network]中的peers中的ip和端口，之后启动控制台；

**步骤1：获取扩容节点的NodeID**

新节点扩容成功后，可通过控制台的`getGroupPeers`命令查看新增的节点列表，`getSealerList`命令查看已有共识节点：

```
[group0]: /apps> getGroupPeers
peer0: 5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948
peer1: fa5a748954513531d502358dad8eaa4f1bc94d92f8b18c3069b586877486950eb905ba2e04c88b6ff921b8f9084f175ddafbe105dc28b03016e2f19995637786

[group0]: /apps> getSealerList 
[
    Sealer{
        nodeID='fa5a748954513531d502358dad8eaa4f1bc94d92f8b18c3069b586877486950eb905ba2e04c88b6ff921b8f9084f175ddafbe105dc28b03016e2f19995637786',
        weight=1
    }
]
```

从控制台输出可看出，peer0: 5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948 的节点不在群组内，使用控制台`addObserver`命令将其加入到观察节点如下：

**步骤2: 将扩容节点加入为观察节点**

```

[group0]: /apps> addObserver  5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948
{
    "code":0,
    "msg":"Success"
}
```

**步骤3: 将扩容节点加入为共识节点**

```
[group0]: /apps> addSealer 5447ea538ec7f5b5bb9dd1562d7341f98f020e9e59a78f991d2dde4e0aadf04164e284df51e4d79dfdaf0767be7a110368e21cbaa20453c786788caa1a8b7948 1
{
    "code":0,
    "msg":"Success"
}
```

