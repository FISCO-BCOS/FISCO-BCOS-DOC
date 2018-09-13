# 基本操作

本文提供了FISCO-BCOS的基本操作。包括证书操作，节点操作，链操作及信息查看。

## 证书操作

### 生成链证书 （CA）

**脚本**：generate_chain_cert.sh

**说明**：在指定位置生成链的根证书CA

**参数**：

必要参数

| flag | 参数         | 说明   |
| ---- | ---------- | ---- |
| -o   | 指定链证书生成的位置 |      |

可选参数

| flag | 参数   | 说明                   |
| ---- | ---- | -------------------- |
| -m   | 无    | 证书生成时，手动输入参数，不采用默认参数 |
| -g   |      |                      |
| -d   |      |                      |
| -h   | 无    | 查看帮助                 |

**操作**：

查看用法

``` shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_chain_cert.sh -h
```

生成证书

``` shell
bash generate_chain_cert.sh -o /mydata
```

在目录下生成

``` log
#tree /mydata
/mydata
|-- ca.crt
`-- ca.key
```

### 生成机构证书

**脚本**：generate_agency_cert.sh

**说明**：用链的根证书，在指定的位置生成机构的证书

**参数**：

必要参数

| flag | 参数             | 说明            |
| ---- | -------------- | ------------- |
| -c   | 链证书（CA证书）所在目录  | 脚本用CA证书生成机构证书 |
| -o   | 指定的机构证书文件夹生成位置 |               |
| -n   | 机构名            | 机构证书文件夹用此名字命名 |

可选参数

| flag | 参数   | 说明                   |
| ---- | ---- | -------------------- |
| -m   | 无    | 证书生成时，手动输入参数，不采用默认参数 |
| -g   |      |                      |
| -d   |      |                      |
| -h   | 无    | 查看帮助                 |

**操作**：

查看用法

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_agency_cert.sh -h
```

生成证书

``` shell
bash generate_agency_cert.sh -c /mydata -o /mydata -n test_agency
```

在目录下生成

``` log
#tree test_agency
test_agency/
|-- agency.crt
|-- agency.csr
|-- agency.key
|-- ca.crt
`-- cert.cnf
```

### 生成节点证书

**脚本**：generate_node_cert.sh

**说明**：用机构证书，在节点的data目录下生成节点证书。在使用本脚本前，请先用generate_node_basic.sh生成节点目录。

**参数**：

必要参数

| flag | 参数                 | 说明   |
| ---- | ------------------ | ---- |
| -a   | 机构名                |      |
| -d   | 机构证书文件夹            |      |
| -n   | 节点名                |      |
| -o   | 指定的节点证书输出到的data文件夹 |      |

可选参数

| flag | 参数   | 说明                   |
| ---- | ---- | -------------------- |
| -r   |      |                      |
| -s   |      |                      |
| -g   |      |                      |
| -m   | 无    | 证书生成时，手动输入参数，不采用默认参数 |
| -h   | 无    | 查看帮助                 |

**操作**：

查看用法

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_node_cert.sh -h
```

生成证书

``` shell
bash generate_node_cert.sh -a test_agency -d /mydata/test_agency -n node0 -o /mydata/node0/data
```

节点目录下生成证书，身份，功能等文件，用*标出

``` log
node0/
|-- config.json
|-- data
|   |-- agency.crt *
|   |-- bootstrapnodes.json
|   |-- ca.crt *
|   |-- node.ca *
|   |-- node.crt *
|   |-- node.csr *
|   |-- node.json *
|   |-- node.key *
|   |-- node.nodeid *
|   |-- node.param *
|   |-- node.private *
|   |-- node.pubkey *
|   `-- node.serial *
|-- keystore
|-- log
|-- log.conf
|-- start.sh
`-- stop.sh
```

### 生成SDK证书

**脚本**：generate_sdk_cert.sh

**说明**：在指定机构的证书目录下生成机构对应的SDK证书。此脚本相对独立，只有需要使用web3sdk，才需使用此脚本。

**参数**：

必要参数

| flag | 参数      | 说明             |
| ---- | ------- | -------------- |
| -d   | 机构证书文件夹 | 用机构名生成机构的SDK证书 |

可选参数

| flag | 参数   | 说明                   |
| ---- | ---- | -------------------- |
| -m   | 无    | 证书生成时，手动输入参数，不采用默认参数 |
| -h   | 无    | 查看帮助                 |

**操作**：

查看用法

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_sdk_cert.sh -h
```

生成证书

``` shell
bash generate_sdk_cert.sh -d /mydata/test_agency
```

输入一系列密码后，在机构证书目录下生成sdk文件夹

``` log
#tree test_agency/
test_agency/
|-- agency.crt
|-- agency.csr
|-- agency.key
|-- agency.srl
|-- ca-agency.crt
|-- ca.crt
|-- cert.cnf
`-- sdk
    |-- ca.crt
    |-- client.keystore
    |-- keystore.p12
    |-- sdk.crt
    |-- sdk.csr
    |-- sdk.key
    |-- sdk.param
    |-- sdk.private
    `-- sdk.pubkey
```

## 节点操作

### 生成创世节点

**脚本**：generate_genesis_node.sh

**说明**：生成创世节点，并自动内置系统合约。其中会调用generate_node_basic.sh，generate_node_cert.sh、generate_genesis.sh和deploy_system_contract.sh生成创世节点的目录、文件、证书和系统合约。生成的创世节点是关闭状态。创世节点生成后，需启动，并注册，将其加入联盟中参与共识。

**参数**：

必要参数

| flag | 参数             | 说明             |
| ---- | -------------- | -------------- |
| -o   | 指定创世节点的文件夹生成位置 |                |
| -n   | 创世节点名          |                |
| -l   | 节点监听端口对应的IP    |                |
| -r   | 节点RPC端口号       | 根据情况自定，端口不冲突即可 |
| -p   | 节点P2P端口号       | 根据情况自定，端口不冲突即可 |
| -c   | 节点channel端口号   | 根据情况自定，端口不冲突即可 |
| -a   | 节点所属机构名        |                |
| -d   | 节点所属机构证书文件夹    |                |

可选参数

| flag | 参数      | 说明                   |
| ---- | ------- | -------------------- |
| -s   | god账号地址 | 手动指定god账号地址          |
| -g   |         |                      |
| -m   | 无       | 证书生成时，手动输入参数，不采用默认参数 |
| -h   | 无       | 查看帮助                 |

**操作**：查看用法

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_genesis_node.sh -h
```

生成创世节点

``` shell
bash generate_genesis_node.sh -o /mydata -n node0 -l 127.0.0.1 -r 8545 -p 30303 -c 8891 -d /mydata/test_agency -a test_agency
```

生成创世节点所有的文件，并自动部署上了系统合约

``` log
#tree node0/
node0/
|-- config.json
|-- data
|   |-- 4bcbbeb4
|   |   |-- 12041
|   |   |   |-- extras
|   |   |   |   |-- 000003.log
|   |   |   |   |-- CURRENT
|   |   |   |   |-- LOCK
|   |   |   |   |-- LOG
|   |   |   |   `-- MANIFEST-000002
|   |   |   `-- state
|   |   |       |-- 000003.log
|   |   |       |-- CURRENT
|   |   |       |-- LOCK
|   |   |       |-- LOG
|   |   |       `-- MANIFEST-000002
|   |   `-- blocks
|   |       |-- 000003.log
|   |       |-- CURRENT
|   |       |-- LOCK
|   |       |-- LOG
|   |       `-- MANIFEST-000002
|   |-- abiname
|   |   |-- 000003.log
|   |   |-- CURRENT
|   |   |-- LOCK
|   |   |-- LOG
|   |   `-- MANIFEST-000002
|   |-- agency.crt
|   |-- bootstrapnodes.json
|   |-- ca.crt
|   |-- event.log
|   |-- geth.ipc
|   |-- IPC_MappedFile
|   |-- keys.info
|   |-- keys.info.salt
|   |-- node.ca
|   |-- node.crt
|   |-- node.csr
|   |-- node.json
|   |-- node.key
|   |-- node.nodeid
|   |-- node.param
|   |-- node.private
|   |-- node.pubkey
|   |-- node.serial
|   |-- pbftMsgBackup
|   |   |-- 000003.log
|   |   |-- CURRENT
|   |   |-- LOCK
|   |   |-- LOG
|   |   `-- MANIFEST-000002
|   |-- RPC_MappedFile
|   `-- UTXO
|       |-- db
|       |   |-- 000003.log
|       |   |-- CURRENT
|       |   |-- LOCK
|       |   |-- LOG
|       |   `-- MANIFEST-000002
|       |-- extra
|       |   |-- 000003.log
|       |   |-- CURRENT
|       |   |-- LOCK
|       |   |-- LOG
|       |   `-- MANIFEST-000002
|       `-- vault
|           |-- 000003.log
|           |-- CURRENT
|           |-- LOCK
|           |-- LOG
|           `-- MANIFEST-000002
|-- fisco-bcos.log
|-- genesis.json
|-- keystore
|-- log
|   |-- debug_log_2018081521.log
|   |-- error_log_2018081521.log
|   |-- fatal_log_2018081521.log
|   |-- info_log_2018081521.log
|   |-- log_2018081521.log
|   |-- stat_log_2018081521.log
|   |-- trace_log_2018081521.log
|   |-- verbose_log_2018081521.log
|   `-- warn_log_2018081521.log
|-- log.conf
|-- myeasylog.log
|-- start.sh
`-- stop.sh
```

### 生成普通节点

**脚本**：generate_node.sh

**说明**：用创世节点的nodeid、系统代理合约地址、创世节点的p2p地址，生成普通节点。其中会调用generate_node_basic.sh、generate_node_cert.sh和generate_genesis.sh，生成节点的目录、文件和证书。生成的节点是关闭状态。节点启动后，会自动连接创世节点，同步系统合约。

**参数**：

必要参数

| flag | 参数              | 说明                                       |
| ---- | --------------- | ---------------------------------------- |
| -o   | 指定节点的文件夹生成位置    |                                          |
| -n   | 节点名             |                                          |
| -l   | 节点监听端口对应的IP     |                                          |
| -r   | 节点RPC端口号        | 根据情况自定，端口不冲突即可                           |
| -p   | 节点P2P端口号        | 根据情况自定，端口不冲突即可                           |
| -c   | 节点channel端口号    | 根据情况自定，端口不冲突即可                           |
| -a   | 节点所属机构名         |                                          |
| -d   | 节点所属机构证书文件夹     |                                          |
| -e   | 链上节点P2P接口URL的列表 | 节点启动时，主动连接链上节点的P2P接口同步数据。此列表用","分割，如：127.0.0.1:30303,127.0.0.1:30304 |
| -i   | 创世节点nodeid      | 用于生成与创世节点相同的创世块文件。创世节点的nodeid可用node_info.sh去查 |
| -s   | god账号地址         | 用于生成与创世节点相同的创世块文件。创世节点的god账号地址可用node_info.sh去查 |
| -x   | 系统代理合约地址        | 用于配置与链相同的系统代理合约地址。可用node_info.sh去查任意的节点获取。 |

可选参数

| flag | 参数   | 说明                   |
| ---- | ---- | -------------------- |
| -g   |      |                      |
| -m   | 无    | 证书生成时，手动输入参数，不采用默认参数 |
| -h   | 无    | 查看帮助                 |

**操作**：

查看用法

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_node.sh -h
```

生成节点

``` shell
bash generate_node.sh -o /mydata -n node1 -l 127.0.0.1 -r 8546 -p 30304 -c 8892 -e 127.0.0.1:30303,127.0.0.1:30304 -d /mydata/test_agency -a test_agency -x 0x919868496524eedc26dbb81915fa1547a20f8998 -i xxxxxx
```

生成节点的全部文件

``` log
tree node1
node1
|-- config.json
|-- data
|   |-- agency.crt
|   |-- bootstrapnodes.json
|   |-- ca.crt
|   |-- node.ca
|   |-- node.crt
|   |-- node.csr
|   |-- node.json
|   |-- node.key
|   |-- node.nodeid
|   |-- node.param
|   |-- node.private
|   |-- node.pubkey
|   `-- node.serial
|-- genesis.json
|-- keystore
|-- log
|-- log.conf
|-- start.sh
`-- stop.sh
```

### 生成节点基本文件

**脚本**：generate_node_basic.sh

**说明**：在指定目录下，生成节点目录，并在目录下生成节点的配置文件和操作脚本。此时，节点还缺少证书文件，功能文件和信息文件，需要继续使用generate_node_cert.sh来生成。或直接用generate_node.sh直接生成节点的所有文件。

**参数**：

必要参数

| flag | 参数              | 说明                                       |
| ---- | --------------- | ---------------------------------------- |
| -o   | 指定节点的文件夹生成位置    |                                          |
| -n   | 节点名             |                                          |
| -l   | 节点监听端口对应的IP     |                                          |
| -r   | 节点RPC端口号        | 根据情况自定，端口不冲突即可                           |
| -p   | 节点P2P端口号        | 根据情况自定，端口不冲突即可                           |
| -c   | 节点channel端口号    | 根据情况自定，端口不冲突即可                           |
| -e   | 链上节点P2P接口URL的列表 | 节点启动时，主动连接链上节点的P2P接口同步数据。此列表用","分割，如：127.0.0.1:30303,127.0.0.1:30304 |

可选参数

| flag | 参数       | 说明                                       |
| ---- | -------- | ---------------------------------------- |
| -x   | 系统代理合约地址 | 用于配置与链相同的系统代理合约地址。可用node_info.sh去查任意的节点获取。 |
| -h   | 无        | 查看帮助                                     |

**操作**：

查看用法

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_node_basic.sh -h
```

生成节点基本文件

``` shell
bash generate_node_basic.sh -o /mydata -n node0 -l 127.0.0.1 -r 8545 -p 30303 -c 8891 -e 127.0.0.1:30303,127.0.0.1:30304
```

在目录下生成

```log
#tree node0/
node0/
|-- config.json
|-- data
|   `-- bootstrapnodes.json
|-- keystore
|-- log
|-- log.conf
|-- start.sh
`-- stop.sh
```

## 链操作

### 生成god账号

**说明**：在生产条件下，需要手动生成god账号，提供给相关脚本生成节点文件。

**操作**：

到指定目录下生成god账号

``` shell
cd /mydata/FISCO-BCOS/tools/contract
node accountManager.js > godInfo.txt
cat godInfo.txt
```

得到生成的god账号信息，此信息请妥善保存，在对链操作的某些场景

``` log
privKey : 0xc8a92524ac634721a9eac94c9d8c09ea719f3a01e0ed1f576f673af6eb90aeea
pubKey : 0xb2795b4000981fb56f386a00e5064bd66b7754db6532bb17f9df1975ca884fc7b3b3291f9f3b20ee0278e610b8814ff62fa7dfbbcda959766c0555eb5f48147d
address : 0xb862b65912e0857a49458346fcf578d199dba024
```

其中god账号地址是需要作为参数提供给其它脚本的

``` log
address : 0xb862b65912e0857a49458346fcf578d199dba024
```

### 生成创世块文件

**脚本**：generate_genesis.sh

**说明**：用god账号地址，创世节点的nodeid，生成创世块文件，若不指定god账号地址，用默认的god账号地址。

**参数**：

必要参数

| flag    | 参数               | 说明                |
| ------- | ---------------- | ----------------- |
| -o      | 指定创世块文件生成位置      | 创世块文件genesis.json |
| -i 或 -d | 创世节点的nodeid 或文件夹 | 二选一               |

可选参数

| flag | 参数         | 说明          |
| ---- | ---------- | ----------- |
| -i   | 创世节点nodeid |             |
| -d   | 创世节点文件夹    |             |
| -s   | god账号地址    | 手动指定god账号地址 |
| -g   |            |             |
| -h   | 无          | 查看帮助        |

**操作**：查看用法

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
bash generate_genesis.sh -h
```

生成创世块文件

``` shell
bash generate_genesis.sh -i 4af70363e2266e62aaca5870d660cc4ced35deae83b67f3dffebd0dcfa3b16d96d8fe726f9fea0def06a3bbde47261b9722ddbb9461af131c9645eb660644842 -o /mydata/node1 -r 0xb862b65912e0857a49458346fcf578d199dba024
```

生成的创世块文件genesis.json

``` json
{
     "nonce": "0x0",
     "difficulty": "0x0",
     "mixhash": "0x0",
     "coinbase": "0x0",
     "timestamp": "0x0",
     "parentHash": "0x0",
     "extraData": "0x0",
     "gasLimit": "0x13880000000000",
     "god":"0xb862b65912e0857a49458346fcf578d199dba024",
     "alloc": {}, 	
     "initMinerNodes":["4af70363e2266e62aaca5870d660cc4ced35deae83b67f3dffebd0dcfa3b16d96d8fe726f9fea0def06a3bbde47261b9722ddbb9461af131c9645eb660644842"]
}
```

### 配置待操作链的端口

**脚本**：config_rpc_address.sh

**说明**：对正在运行中的链进行操作（如：注册节点，部署合约，调用合约）时，需先将全局proxy变量指向待操作的链。具体的，是将全局proxy变量设置为链上某个节点的RPC端口，即设置/mydata/FISCO-BCOS/tools/web3sdk/config.js的proxy为相应的节点RPC的URL。设置一次即可永久生效，无需重复设置。

**参数**：

必要参数

| flag | 参数         | 说明                      |
| ---- | ---------- | ----------------------- |
| -o   | 某节点RPC的URL | 全局proxy变量指向待操作的链RPC的URL |

可选参数

| flag | 参数           | 说明                          |
| ---- | ------------ | --------------------------- |
| -w   | web3lib文件夹地址 | 手动指定web3lib地址，默认在../web3lib |
| -h   | 无            | 查看帮助                        |

**操作**：

查看用法

``` shell
cd /mydata/FISCO-BCOS/tools/scripts
bash set_proxy_address.sh -h
```

设置RPC的URL（节点的RPC的URL用node_info.sh获取）

``` shell
bash set_proxy_address.sh -o 127.0.0.1:8545
```

yes回车确认后，写入全局的proxy变量中

``` nodejs
proxy="http://127.0.0.1:8545"
```

### 部署系统合约

**脚本**：deploy_system_contract.sh

**说明**：用在生成创世节点时（generate_generate_node.sh），已经自动调用此脚本部署了系统合约。若需要重新手动部署系统合约，则调用此脚本。并同时将链上其它节点的config.json修改为与创世节点相同的systemproxyaddress，并重启节点使其生效。一般来说，不推荐重新部署系统合约。重新部署系统合约意味着对链管理的重置，需要链上所有机构都同意，且需要重启链上所有的节点。在使用此脚本前，需调set_proxy_address.sh设置全局proxy地址（若设置则无需重复设置）。

**参数**：

必要参数

| flag | 参数      | 说明   |
| ---- | ------- | ---- |
| -d   | 创世节点文件夹 |      |

可选参数

| flag | 参数                  | 说明                                       |
| ---- | ------------------- | ---------------------------------------- |
| -w   | web3lib文件夹地址        | 手动指定web3lib地址，默认在../web3lib              |
| -s   | systemcontract文件夹地址 | 手动指定systemcontract地址，默认在../systemcontract |
| -h   | 无                   | 查看帮助                                     |

**操作**：

查看用法

``` shell
cd /mydata/FISCO-BCOS/tools/scripts
bash set_proxy_address.sh -h
```

部署系统合约

``` shell
bash deploy_systemcontract.sh -d /mydata/node0
```

得到系统代理合约地址

``` log
SystemProxy address: 0xbac830dee59a0f2a33beddcf53b329a4e1787ce2
```

将链上其它节点的systemproxyaddress也修改为相同的地址

``` shell
sed -i '/systemproxyaddress/c  \\t\"systemproxyaddress\":\"0xbac830dee59a0f2a33beddcf53b329a4e1787ce2\",'  /mydata/node1/config.json
sed -i '/systemproxyaddress/c  \\t\"systemproxyaddress\":\"0xbac830dee59a0f2a33beddcf53b329a4e1787ce2\",'  /mydata/node2/config.json
```

### 注册节点

**脚本**：register_node.sh

**说明**：此脚本将某个指定的节点注册入网，让此节点参与到区块链的共识中。在链初始化初期，链上无任何一个节点被注册，此时由创世节点进行共识，其它节点都是观察者节点。当有一个节点被注册后，由注册的节点进行共识。此时，创世节点退化成一个普通的节点，也需被注册后才能参与共识。在使用此脚本前，需调set_proxy_address.sh设置全局proxy地址（若设置则无需重复设置）。

**参数**：

必要参数

| flag | 参数         | 说明   |
| ---- | ---------- | ---- |
| -d   | 需要注册节点的文件夹 |      |

可选参数

| flag | 参数                  | 说明                                       |
| ---- | ------------------- | ---------------------------------------- |
| -w   | web3lib文件夹地址        | 手动指定web3lib地址，默认在../web3lib              |
| -s   | systemcontract文件夹地址 | 手动指定systemcontract地址，默认在../systemcontract |
| -g   |                     |                                          |
| -h   | 无                   | 查看帮助                                     |

**操作**：

查看用法

``` shell
cd /mydata/FISCO-BCOS/tools/scripts
bash register_node.sh -h
```

将指定节点目录，将节点注册入网

``` shell
bash register_node.sh -d /mydata/node0 
```

用node_all.sh脚本可看到节点入网情况

``` log
----------node 0---------
id=4af70363e2266e62aaca5870d660cc4ced35deae83b67f3dffebd0dcfa3b16d96d8fe726f9fea0def06a3bbde47261b9722ddbb9461af131c9645eb660644842
name=node0
agency=test_agency
caHash=BE4790D7B2BA3D1A
Idx=0
blocknumber=59
```



## 状态查询

### 查看节点存活

**说明**：直接查看节点进程

**操作**：

``` shell
ps -ef |grep fisco-bcos
```

可看到相应的节点进程，用启动目录区分不同的节点

``` log
app  57342      1 23 15:24 ?        00:00:02 fisco-bcos --genesis /mydata/node0/genesis.json --config /mydata/node0/config.json
app  57385      1 37 15:24 ?        00:00:01 fisco-bcos --genesis /mydata/node1/genesis.json --config /mydata/node1/config.json
```

### 查看节点基本信息

**脚本**：node_info.sh

**说明**：指定节点目录，输出节点的基本信息。

**参数**：

必要参数

| flag | 参数           | 说明   |
| ---- | ------------ | ---- |
| -d   | 需要查看信息的节点文件夹 |      |

可选参数

| flag | 参数   | 说明         |
| ---- | ---- | ---------- |
| -g   | 无    | 输出国密版的节点信息 |
| -h   | 无    | 查看帮助       |

**操作**：

查看用法

``` shell
cd /mydata/FISCO-BCOS/tools/scripts
bash node_info.sh -h
```

指定节点目录，查看节点信息

``` shell
bash node_info.sh -d /mydata/node1/
```

得到节点关键信息

``` log
-----------------------------------------------------------------
Name:			node1
Agency:			test_agency
CA hash:		BE4790D7B2BA3D1B
Node ID:		f56014d3ef617a8d461c6531a68b183594203b269e82ead83cc37f47cd67c3f45798b8a1c20edc5a0d93d68fcd253dd80378e96ca26bdaf0453d6661112f8f66
RPC address:		127.0.0.1:8546
P2P address:		127.0.0.1:30304
Channel address:	127.0.0.1:8892
SystemProxy address:	0xbac830dee59a0f2a33beddcf53b329a4e1787ce2
Node dir:		/mydata/node1/
State:			Running (pid: 69599)
-----------------------------------------------------------------
```

### 查看链上被注册的节点

**脚本**：node_all.sh

**说明**：输出链上所有被注册的节点，即链上参与共识的节点。在使用此脚本前，需调set_proxy_address.sh设置全局proxy地址（若设置则无需重复设置）。

**参数**：

可选参数

| flag | 参数                  | 说明                                       |
| ---- | ------------------- | ---------------------------------------- |
| -w   | web3lib文件夹地址        | 手动指定web3lib地址，默认在../web3lib              |
| -s   | systemcontract文件夹地址 | 手动指定systemcontract地址，默认在../systemcontract |
| -h   | 无                   | 查看帮助                                     |

**操作**：

查看用法

``` shell
cd /mydata/FISCO-BCOS/tools/scripts
bash node_all.sh -h
```

直接调用脚本查看

``` shell
bash node_all.sh
```

得到被注册的节点

``` log
NodeIdsLength= 2
----------node 0---------
id=4af70363e2266e62aaca5870d660cc4ced35deae83b67f3dffebd0dcfa3b16d96d8fe726f9fea0def06a3bbde47261b9722ddbb9461af131c9645eb660644842
name=node0
agency=test_agency
caHash=BE4790D7B2BA3D1A
Idx=0
blocknumber=59
----------node 1---------
id=f56014d3ef617a8d461c6531a68b183594203b269e82ead83cc37f47cd67c3f45798b8a1c20edc5a0d93d68fcd253dd80378e96ca26bdaf0453d6661112f8f66
name=node1
agency=test_agency
caHash=BE4790D7B2BA3D1B
Idx=1
blocknumber=60
```

### 查看节点连接状态

**说明**：通过查看日志，过滤某个关键字，查看指定节点的链接情况

**操作**：

``` shell
cat /mydata/node1/log/* | grep "topics Send to"
```

看到发送topic的日志，表示节点已经连接了相应的另一个节点

```log
DEBUG|2018-08-10 15:42:05:621|topics Send to:1 nodes
DEBUG|2018-08-10 15:42:06:621|topics Send tod23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd@127.0.0.1:30303
```

### 查看节点共识状态

**说明**：通过查看日志，过滤某个关键字，查看指定节点的共识状态

**操作**：

``` shell
tail -f /mydata/node1/log/* |grep +++
```

可看到周期性的出现如下日志，表示节点间在周期性的进行共识，节点运行正确

```log
INFO|2018-08-10 15:48:52:108|+++++++++++++++++++++++++++ Generating seal on57b9e818999467bff75f58b08b3ca79e7475ebfefbb4caea6d628de9f4456a1d#32tx:0,maxtx:1000,tq.num=0time:1533887332108
INFO|2018-08-10 15:48:54:119|+++++++++++++++++++++++++++ Generating seal on273870caa50741a4841c3b54b7130ab66f08227601b01272f62d31e48d38e956#32tx:0,maxtx:1000,tq.num=0time:1533887334119
```

### 查看节点启动日志

**说明**：节点启动日志在节点每次启动时刷新，若节点无法启动，可查看此日志。

**操作**：

``` shell
cat /mydata/node0/fisco-bcos.log
```

### 查看节点运行日志

**说明**：节点在运行时，在所在目录下的log文件夹里实时的打印一系列的日志。根据日志等级的划分，可查看相应的日志输出。

**操作**：

查看目录下的日志

``` log
log
|-- debug_log_2018081521.log
|-- error_log_2018081521.log
|-- fatal_log_2018081521.log
|-- info_log_2018081521.log
|-- log_2018081521.log        #全局日志
|-- stat_log_2018081521.log
|-- trace_log_2018081521.log
|-- verbose_log_2018081521.log
`-- warn_log_2018081521.log
```

按日期和时刻查看日志

``` shell
cat log_2018081521.log
```

查看实时刷出的日志

``` shell
tail -f log_2018081521.log
```

















