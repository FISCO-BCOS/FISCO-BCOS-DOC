# 扩容新节点

标签：``Pro版区块链网络`` ``扩容节点``

------------

`BcosBuilder`提供了在已有群组基础上扩容新节点的功能，本章在[搭建Pro版区块链网络](./installation.md)的基础上扩容两个新的区块链节点，帮助用户掌握Pro版本FISCO BCOS区块链的节点扩容步骤。

```eval_rst
.. note::
   进行节点扩容操作前，请先参考 `搭建Pro版区块链网络 <./installation.html>`_ 部署Pro版本区块链。
```

## 1. 部署tarsnode

扩容区块链节点服务前，需要在部署扩容的区块链服务节点的机器上安装tarsnode，为了帮助用户能够在单机上快速体验服务扩容流程，本章直接通过桥接网络虚拟化出IP为`172.25.0.5`的容器作为安装扩容的区块链服务节点的物理机器。

```eval_rst
.. note::
   - 实际生产环境中tarsnode的安装请参考 `tars安装部署 <https://doc.tarsyun.com/#/markdown/ TarsCloud/TarsDocs/installation/README.md>`_
   - 若已经安装过tarsnode，且tarsnode处于启动的状态，可忽略本步骤
```

```shell
# 进入操作目录
cd ~/fisco/BcosBuilder

# linux系统: 进入到tarsnode docker-compose所在目录(macos系统可跳过)
cd docker/bridge/linux/node

# macos系统: 进入到tarsnode docker-compose所在目录(linux系统可跳过)
cd docker/bridge/mac/node

# 安装并启动tarsnode
docker-compose up -d
```

## 2. 设置区块链节点扩容配置

```eval_rst
.. note::
   实际操作过程中，须将tars token替换为从tars网页管理平台【admin】->【用户中心】->【token管理】获取可用的token。
```

区块链节点服务的扩容配置可参考`BcosBuilder`的扩容模板`conf/config-node-expand-example.toml`，具体配置步骤如下：

```shell
# 进入操作目录
cd ~/fisco/BcosBuilder/pro

# 拷贝模板配置
cp conf/config-node-expand-example.toml config.toml

# 配置tars token: 可通过tars网页管理平台的【admin】->【用户中心】->【token管理】获取可用的token
# 这里的token为：eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls
# linux系统(macOS系统跳过本步骤):
sed -i 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml
# macos系统(linux系统跳过本步骤):
sed -i .bkp 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml
```

扩容配置`config.toml`如下：

```ini
[tars]
tars_url = "http://127.0.0.1:3000"
# 访问tars服务的token，部署过程中请替换为从tars网页管理平台【admin】->【用户中心】->【token管理】获取可用的token
tars_token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"
tars_pkg_dir = ""

[chain]
# 链ID
chain_id="chain0"

[[group]]
group_id="group0"
# the genesis configuration path of the expanded group
genesis_config_path = "generated/chain0/group0/config.genesis"
# use sm-crypto or not
sm_crypto=false

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

## 3. 扩容区块链节点服务

```shell
# 进入操作目录
cd ~/fisco/BcosBuilder/pro

# 扩容并部署RPC服务
python3 build_chain.py chain -o expand -t node
```

执行上述命令后，当脚本输出`expand nodes for the given group success`时，则说明区块链节点服务扩容成功，详细日志输出如下：

```shell
=========================================================
----------- expand nodes for the given group -----------
* reload node config
* generate ini config, service: agencyAgroup0node1BcosNodeService, path: generated/chain0/group0/172.25.0.5/agencyAgroup0node1BcosNodeService/config.ini
* generate ini config for service: agencyAgroup0node1BcosNodeService success
* generate genesis config, service: agencyAgroup0node1BcosNodeService, path: generated/chain0/group0/172.25.0.5/agencyAgroup0node1BcosNodeService/config.genesis
* generate genesis config for service: agencyAgroup0node1BcosNodeService success
* reload node config
* generate ini config, service: agencyAgroup0node2BcosNodeService, path: generated/chain0/group0/172.25.0.5/agencyAgroup0node2BcosNodeService/config.ini
* generate ini config for service: agencyAgroup0node2BcosNodeService success
* generate genesis config, service: agencyAgroup0node2BcosNodeService, path: generated/chain0/group0/172.25.0.5/agencyAgroup0node2BcosNodeService/config.genesis
* generate genesis config for service: agencyAgroup0node2BcosNodeService success
* generate pem file for agencyAgroup0node1BcosNodeService
	- pem_path: generated/chain0/group0/172.25.0.5/agencyAgroup0node1BcosNodeService
	- node_id_path: generated/node.nodeid
	- sm_crypto: 0
* generate pem file for agencyAgroup0node2BcosNodeService
	- pem_path: generated/chain0/group0/172.25.0.5/agencyAgroup0node2BcosNodeService
	- node_id_path: generated/node.nodeid
	- sm_crypto: 0
deploy service agencyAgroup0node1BcosNodeService
deploy service agencyAgroup0node1BcosNodeService
upload tar package generated/./agencyAgroup0node1BcosNodeService.tgz success, config id: 22
deploy service agencyAgroup0node2BcosNodeService
deploy service agencyAgroup0node2BcosNodeService
upload tar package generated/./agencyAgroup0node2BcosNodeService.tgz success, config id: 23
----------- expand nodes for the given group success -----------
=========================================================
```

扩容过程中生成的RPC服务相关的配置位于`generated/${chainID}/${groupID}/${deploy_ip}`目录，具体如下：

```shell
$ tree generated/chain0/group0/172.25.0.5
generated/chain0/group0/172.25.0.5
├── agencyAgroup0node1BcosNodeService
│   ├── config.genesis
│   ├── config.ini
│   ├── node.nodeid
│   └── node
└── agencyAgroup0node2BcosNodeService
    ├── config.genesis
    ├── config.ini
    ├── node.nodeid
    └── node
s
```

区块链服务扩容成功后，可在tars网页管理平台看到新增的区块链服务`agencyAgroup0node1BcosNodeService`和`agencyAgroup0node2BcosNodeService`:

![](../../../images/tutorial/expand_node.png)

## 4. 将新扩容节点加入到群组

```eval_rst
.. note::
   扩容新节点时，不建议直接将节点加为共识节点，当扩容节点的块高与链上已有节点最高块高一致时候，才可将其加入为共识节点。
```

**步骤1：获取扩容节点的NodeID**

新节点扩容成功后，可通过控制台的`getGroupPeers`命令查看新增的节点列表：

```shell
[group0]: /> getGroupPeers
peer0: 0662759d9ff150b4e004f1e84a84dab15188e04c3772da1a10300302d6677f90dd2a36b75cefe5fcc08310999edf407dda9025332244186430561554f48ebc27
peer1: 23d88283fe20c959734c3a3e6f34fb1cb0db9e286ec6e31aa140245ca0bd8aa2a35e288e4d0e10430b8143e06aa46d1b80434a86a8f39980686b43577b415b90
peer2: 4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a
peer3: 8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3

[group0]: /> getSealerList
[
    Sealer{
        nodeID='8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3',
        weight=1
    },
    Sealer{
        nodeID='4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a',
        weight=1
    }
]
[group0]: /> getObserverList
[]
```

从控制台输出可看出，nodeID为`0662759d9ff150b4e004f1e84a84dab15188e04c3772da1a10300302d6677f90dd2a36b75cefe5fcc08310999edf407dda9025332244186430561554f48ebc27`和`23d88283fe20c959734c3a3e6f34fb1cb0db9e286ec6e31aa140245ca0bd8aa2a35e288e4d0e10430b8143e06aa46d1b80434a86a8f39980686b43577b415b90`的节点不在群组内，使用控制台`addObserver`命令将其加入到观察节点如下：


**步骤2: 将扩容节点加入为观察节点**

```shell
[group0]: /> addObserver 0662759d9ff150b4e004f1e84a84dab15188e04c3772da1a10300302d6677f90dd2a36b75cefe5fcc08310999edf407dda9025332244186430561554f48ebc27
{
    "code":0,
    "msg":"Success"
}

[group0]: /> addObserver 23d88283fe20c959734c3a3e6f34fb1cb0db9e286ec6e31aa140245ca0bd8aa2a35e288e4d0e10430b8143e06aa46d1b80434a86a8f39980686b43577b415b90
{
    "code":0,
    "msg":"Success"
}

[group0]: /> getObserverList
[
    0662759d9ff150b4e004f1e84a84dab15188e04c3772da1a10300302d6677f90dd2a36b75cefe5fcc08310999edf407dda9025332244186430561554f48ebc27,
    23d88283fe20c959734c3a3e6f34fb1cb0db9e286ec6e31aa140245ca0bd8aa2a35e288e4d0e10430b8143e06aa46d1b80434a86a8f39980686b43577b415b90
]
```

**步骤3：扩容节点同步到最高块后，将扩容节点加入为共识节点**

```shell
[group0]: /> addSealer 0662759d9ff150b4e004f1e84a84dab15188e04c3772da1a10300302d6677f90dd2a36b75cefe5fcc08310999edf407dda9025332244186430561554f48ebc27 1
{
    "code":0,
    "msg":"Success"
}

[group0]: /> addSealer 23d88283fe20c959734c3a3e6f34fb1cb0db9e286ec6e31aa140245ca0bd8aa2a35e288e4d0e10430b8143e06aa46d1b80434a86a8f39980686b43577b415b90 1
{
    "code":0,
    "msg":"Success"
}

[group0]: /> getSealerList
[
    Sealer{
        nodeID='8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3',
        weight=1
    },
    Sealer{
        nodeID='4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a',
        weight=1
    },
    Sealer{
        nodeID='0662759d9ff150b4e004f1e84a84dab15188e04c3772da1a10300302d6677f90dd2a36b75cefe5fcc08310999edf407dda9025332244186430561554f48ebc27',
        weight=1
    },
    Sealer{
        nodeID='23d88283fe20c959734c3a3e6f34fb1cb0db9e286ec6e31aa140245ca0bd8aa2a35e288e4d0e10430b8143e06aa46d1b80434a86a8f39980686b43577b415b90',
        weight=1
    }
]
```

**步骤4：部署和调用合约**

```shell
[group0]: /> deploy HelloWorld
transaction hash: 0x62b2fe3514ca22ea2126b7d6bcab1f2912e03503045b3b806ff2d87d605aca5c
contract address: 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01
currentAccount: 0x537149148696c7e6c3449331d77ddfaabc3c7a75

[group0]: /> call HelloWorld 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01 set "Hello, Fisco"
transaction hash: 0x21a44704b4d2f56108f071a61987e20be7627c2049437cc1a73db9b0e00f55f8
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

[group0]: /> call HelloWorld 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, Fisco)
---------------------------------------------------------------------------------------------
```