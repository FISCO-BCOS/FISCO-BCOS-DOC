# 扩容新群组

标签：``Pro版区块链网络`` ``扩容群组``

--------------

Pro版本FISCO BCOS区块链系统支持动态多群组区块链网络，可在系统运行时扩容产生新群组、下线群组。`BcosProBuilder`提供了新群组上线和下线功能，本章在[搭建Pro版区块链网络](./installation.md)的基础上扩容、下线`wasm`群组`group2`，帮助用户掌握Pro版本FISCO BCOS区块链系统群组扩容、下线步骤。


```eval_rst
.. note::
   - 扩容新群组前，请参考 `这里 <./installation.html>`_ 搭建Pro版区块链网络
```

## 1. 扩容新群组

这里以在IP为`172.25.0.3`的机器(容器)中扩容2个链ID为`chain`、群组ID为`group2`的区块链节点为例介绍新群组扩容。
### 1.1 设置新群组配置

```eval_rst
.. note::
   实际操作过程中，须将tars token替换为从tars网页管理平台【admin】->【用户中心】->【token管理】获取可用的token。
```

扩容新群组可直接使用服务部署配置模板`conf/config-deploy-example.toml`，配置其群组ID为`group2`，具体如下：

**macOS系统:**

```shell
# 进入操作目录
$ cd ~/fisco/BcosProBuilder

# 拷贝配置文件
$ cp conf/config-deploy-example.toml config.toml

# 配置tars token: 可通过tars网页管理平台的【admin】->【用户中心】->【token管理】获取可用的token
# 这里的token为：eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls
$ sed -i .bkp 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml

# 配置群组ID
$ sed -i .bkp 's/group_id="group"/group_id="group2"/g' config.toml

# 确认群组ID修改成功: 输出group2
$ cat config.toml | grep -i group_id
group_id="group2"

# 配置虚拟机类型为wasm
$ sed -i .bkp 's/vm_type="evm"/vm_type="wasm"/g' config.toml

# 确认虚拟机类型修改成功：
$ cat config.toml|grep vm_type
vm_type="wasm"
```

**linux系统:**

```shell
# 进入操作目录
$ cd ~/fisco/BcosProBuilder
# 拷贝配置文件
$ cp conf/config-deploy-example.toml config.toml

# 配置tars token: 可通过tars网页管理平台的【admin】->【用户中心】->【token管理】获取可用的token
# 这里的token为：eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls
$ sed -i 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml

# 配置群组ID
$ sed -i 's/group_id="group"/group_id="group2"/g' config.toml

# 确认群组ID修改成功: 输出group2
$ cat config.toml | grep -i group_id
group_id="group2"

# 配置虚拟机类型为wasm
$ sed -i 's/vm_type="evm"/vm_type="wasm"/g' config.toml

# 确认虚拟机类型修改成功：
$ cat config.toml|grep vm_type
vm_type="wasm"
```


新群组`group2`的配置`config.toml`如下：

```ini
[chain]
chain_id="chain0"

[group]
group_id="group2"
vm_type="wasm"
sm_crypto=false

# the genesis config
leader_period = 5
block_tx_count_limit = 1000
consensus_type = "pbft"
gas_limit = "300000000"

[[group.deploy_info]]
# node_name = "node0"
deploy_ip = "172.25.0.3"
node_count=1
rpc_service_name = "agencyABcosRpcService"
gateway_service_name = "agencyABcosGatewayService"

[[group.deploy_info]]
# node_name = "node1"
deploy_ip = "172.25.0.3"
node_count=1
rpc_service_name = "agencyBBcosRpcService"
gateway_service_name = "agencyBBcosGatewayService"
```

### 1.2 扩容新群组

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 部署新群组
python3 build_chain.py chain -o deploy -t node
```

执行上述命令后，当脚本输出`deploy all nodes of the given group success`时，则说明区块链节点扩容新群组成功，详细日志输出如下：

```shell
=========================================================
----------- deploy all nodes of the given group -----------
generate config for chain = chain0, group = group2
* generate pem file for group2node00BcosNodeService
	- pem_path: generated/chain0/group2/172.25.0.3/group2node00BcosNodeService
	- node_id_path: generated/node.nodeid
	- sm_crypto: 0
* generate pem file for group2node10BcosNodeService
	- pem_path: generated/chain0/group2/172.25.0.3/group2node10BcosNodeService
	- node_id_path: generated/node.nodeid
	- sm_crypto: 0
* generate genesis config for group2node00BcosNodeService
	 path: generated/chain0/group2/172.25.0.3/group2node00BcosNodeService/config.genesis.tmp
* generate ini config for service group2node00BcosNodeService
	config path: generated/chain0/group2/172.25.0.3/group2node00BcosNodeService/config.ini.tmp
* generate genesis config for group2node10BcosNodeService
	 path: generated/chain0/group2/172.25.0.3/group2node10BcosNodeService/config.genesis.tmp
* generate ini config for service group2node10BcosNodeService
	config path: generated/chain0/group2/172.25.0.3/group2node10BcosNodeService/config.ini.tmp
deploy services for all the group nodes
deploy service group2node00BcosNodeService
deploy service group2node00BcosNodeService
upload tar package generated/./group2node00BcosNodeService.tgz success, config id: 24
deploy service group2node10BcosNodeService
deploy service group2node10BcosNodeService
upload tar package generated/./group2node10BcosNodeService.tgz success, config id: 25
----------- deploy all nodes of the given group success -----------
=========================================================
```

群组扩容过程中生成的区块链节点相关的配置位于`generated/${chainID}/${groupID}`目录，具体如下：

```shell
$ tree generated/chain0/group2
generated/chain0/group2
└── 172.25.0.3
    ├── group2node00BcosNodeService
    │   ├── config.genesis.tmp
    │   ├── config.ini.tmp
    │   ├── node.nodeid
    │   └── node.pem
    └── group2node10BcosNodeService
        ├── config.genesis.tmp
        ├── config.ini.tmp
        ├── node.nodeid
        └── node.pem
```

新群组扩容成功后，可在tars网页管理平台看到新增的区块链服务`group2node00BcosNodeService`和`group2node10BcosNodeService`:

![](../../../images/tutorial/expand_group.png)

## 2. 向新群组发送交易

新群组部署成功后，可使用控制台直接连接新群组`group2`，向新群组发送交易。控制台`contract/liquid`目录下提供了已经编译好的`hello_world`合约，这里直接使用即可。关于`webankblockchain-liquid（以下简称WBC-Liquid）`合约编写和编译，可参考[这里](https://liquid-doc.readthedocs.io/zh_CN/latest/)。

WBC-Liquid hello_world合约如下：

```rust
#![cfg_attr(not(feature = "std"), no_std)]

use liquid::storage;
use liquid_lang as liquid;

#[liquid::contract]
mod hello_world {
    use super::*;

    #[liquid(storage)]
    struct HelloWorld {
        name: storage::Value<String>,
    }

    #[liquid(methods)]
    impl HelloWorld {
        pub fn new(&mut self) {
            self.name.initialize(String::from("Alice"));
        }

        pub fn get(&self) -> String {
            self.name.clone()
        }

        pub fn set(&mut self, name: String) {
            self.name.set(name)
        }
    }

    #[cfg(test)]
    mod tests {
        use super::*;

        #[test]
        fn get_works() {
            let contract = HelloWorld::new();
            assert_eq!(contract.get(), "Alice");
        }

        #[test]
        fn set_works() {
            let mut contract = HelloWorld::new();

            let new_name = String::from("Bob");
            contract.set(new_name.clone());
            assert_eq!(contract.get(), "Bob");
        }
    }
}
```

```shell
$ bash start group2
=============================================================================================
Welcome to FISCO BCOS console(3.0.0-rc2)!
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
# 获取当前群组列表
[group2]: /> getGroupList
["group", "group2"]

# 获取群组2的区块链节点NodeID列表
[group2]: /> getGroupPeers
peer0: 93d51a1a3af71dda7015d7be88818ef4f3e674cd0681e8184690ac0f504aee1148023eda30e3075958accda62e35161f42312ed4b56f44e23ae2e81c07734c60
peer1: a6448fd526eb90438621d653e59cd2c850cf60953cdf702471e62731b4363412f05319f92aa141c38031d9bf1d3d2067e5430585cfe8f38d74ebc20e6dacf9b3

# 创建文件目录tmp
[group2]: /> mkdir tmp
Success

# 部署hello_world WBC-Liquid合约
[group2]: /> deploy contracts/liquid/hello_world/hello_world.wasm contracts/liquid/hello_world/hello_world.abi tmp
transaction hash: 0x39bdce5954e39bfe0e2291a0373a5107fc7f29522f9dfddc69dcf1dd33e21bd4
contract address: /tmp
currentAccount: 0x537149148696c7e6c3449331d77ddfaabc3c7a75

# 获取当前块高
[group2]: /> getBlockNumber
2

# 调用tmp2目录下hello_world合约的get方法是，输出Alice
[group2]: /> call tmp get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Alice)
---------------------------------------------------------------------------------------------

# 设置hello_world的name为"Hello, Fisco"
[group2]: /> call tmp set "Hello, Fisco"
transaction hash: 0x87ee80ed586b185cbc17d33ade0b97b8436cc4c568538f68ae16cb6d9cfffcf0
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

# 获取更新后的name
[group2]: /> call tmp get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, Fisco)
---------------------------------------------------------------------------------------------

# 获取当前块高
[group2]: /> getBlockNumber
3
```

## 3. 下线群组

`BcosProBuilder`也提供了新群组下线功能，**进行群组下线操作前，请确保群组已经不再会被使用。**

下线群组`group2`的步骤如下：

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 下线群组group2: 须保证config.toml是群组group2扩容时使用的配置文件
python3 build_chain.py chain -o undeploy -t node
```

执行上述命令后，当脚本输出`undeploy all nodes of the given group success`时，则说明区块链节点下线新群组成功，详细日志输出如下：
```shell
=========================================================
----------- undeploy all nodes of the given group -----------
undeploy services for all the group nodes
undeploy service group2node00BcosNodeService
undeploy service group2node00BcosNodeService success
undeploy service group2node10BcosNodeService
undeploy service group2node10BcosNodeService success
----------- undeploy all nodes of the given group success -----------
=========================================================
```
此时通过控制台，无法查询到下线群组`group2`的信息：

```
$ bash start.sh
# 获取当前群组列表
[group0]: /> getGroupList
["group"]
```