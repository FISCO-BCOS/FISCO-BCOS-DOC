# 配置介绍

标签：``Pro版区块链网络`` ``扩容群组`` ``多群组微服务区块链`` ``tars`` ``RpcService`` ``GatewayService`` ``NodeService``

------------

FISCO BCOS使用tars构建多群组微服务区块链，搭建FISCO BCOS Pro版区块链请[参考这里](./installation.md)

## 1. 多群组微服务文件组织结构

利用区块链构建脚本 `BcosBuilder/pro/build_chain.py` 可构建 **RpcService** RPC服务，**GatewayService** 网关服务，以及 **NodeService** 节点服务。构建后生成 `generated` 目录，服务配置信息在此目录下。

```shell
generated/
├── chain0 # 链
│   ├── group0 # 群组
│   │   ├── 172.25.0.3 # 本机的eth0 ip地址
│   │   │   ├── agencyAgroup0node0BcosNodeService # 区块链节点配置目录
│   │   │   │   ├── config.genesis # 创世块配置文件
│   │   │   │   ├── config.ini # 配置文件
│   │   │   │   ├── node.nodeid # 节点id
│   │   │   │   ├── node.pem # 节点私钥，用于共识模块签名
│   │   │   ├── agencyBgroup0node0BcosNodeService # 区块链节点配置目录
│   │   │   │.....
├── gateway # 网关
│   ├── chain # 链
│   │   ├── 172.25.0.3 # 本机的eth0 ip地址
│   │   │   ├── agencyABcosGatewayService # 网关配置目录
│   │   │   │   ├── ssl
│   │   │   │   │   ├── ca.crt # CA证书
│   │   │   │   │   ├── cert.cnf # 证书配置
│   │   │   │   │   ├── ssl.crt # ssl 证书
│   │   │   │   │   ├── ssl.key # ssl 私钥
│   │   │   │   ├── config.ini # 配置文件
│   │   │   │   ├── nodes.json # 节点连接信息，{"nodes": ["172.25.0.30:30300", "172.25.0.3:30301"]}
│   │   │   ├── agencyBBcosGatewayService # 网关配置目录
│   │   │   │.....
│   │   ├── ca # CA 证书
│   │   │   ├── ca.crt # CA 证书
│   │   │   ├── ca.key # CA 私钥
│   │   │   ├── cert.cnf # 证书私钥
├── rpc
│   ├── chain
│   │   ├── 172.25.0.30 # 本机的eth0 ip地址
│   │   │   ├── agencyABcosRpcService # RPC服务配置路径
│   │   │   │   ├── ssl       
│   │   │   │   │   ├── ca.crt # CA证书
│   │   │   │   │   ├── cert.cnf # 证书配置
│   │   │   │   │   ├── ssl.crt # ssl 证书
│   │   │   │   │   ├── ssl.key # ssl 私钥
│   │   │   │   ├── sdk        # SDK证书目录
│   │   │   │   │   ├── ca.crt # CA证书
│   │   │   │   │   ├── cert.cnf # 证书配置
│   │   │   │   │   ├── sdk.crt # sdk 证书
│   │   │   │   │   ├── sdk.key # sdk 私钥
│   │   │   │   ├── config.ini.tmp # 配置文件
│   │   │   ├── agencyBBcosRpcService # RpcService B
│   │   │   │.....
│   │   ├── ca # CA 证书
│   │   │   ├── ca.crt # CA 证书
│   │   │   ├── ca.key # CA 私钥
│   │   │   ├── cert.cnf # 证书私钥
├── agencyABcosGatewayService.tgz # 生成的网关服务A的tgz包
├── agencyABcosRpcService.tgz # 生成的RPC服务A的tgz包
├── agencyBBcosGatewayService.tgz # 生成的网关服务B的tgz包
├── agencyBBcosRpcService.tgz # 生成的RPC服务B的tgz包
├── node.nodeid # 节点id
├── node.pem # 节点pem格式私钥
```

## 2. 区块链节点服务(BcosNodeService)配置

区块链节点服务搭建请参考[这里](./installation.html#id6)，其主要包括创世块配置`config.genesis`和节点配置`config.ini`。


### 2.1 创世块配置

```eval_rst
.. note::
    - **须保证群组内所有节点的创世块配置一致**
    - **创世块配置文件在链初始化后不可更改**
    - 链初始化后，即使更改了创世块配置，新的配置不会生效，系统仍然使用初始化链时的genesis配置
```
#### 2.1.1 链配置选项

链配置选项位于`[chain]`，主要包括：

- `[chain].sm_crypto`: 用于配置账本密码学类型，`true`表明账本使用国密算法，`false`表明账本使用非国密算法，默认为`false`;
- `[chain].group_id`: 群组id;
- `[chain].chain_id`: 链id.

节点链配置选项示例如下：

```ini
[chain]
sm_crypto=false
group_id=group0
chain_id=chain0
```

#### 2.1.2 共识配置

`[consensus]`涉及共识相关配置，包括：

- `[consensus].consensus_type`: 共识类型，默认设置为`pbft`，目前FISCO BCOS v3.x仅支持PBFT共识算法;
- `[consensus].block_tx_count_limit`: 每个区块里可包含的最大交易数，默认设置为1000;
- `[consensus].leader_period`: 共识过程中每个leader连续打包的区块数目，默认为 5;
- `[consensus].node.idx`：共识节点列表，配置了参与共识节点的NodeID。


`[consensus]`配置示例如下：
```ini
[consensus]
consensus_type = pbft
block_tx_count_limit = 1000
leader_period = 5
node.0 = 94172c95917fbf47b4b98aba0cc68f83f61a06b0bc373695590f343464b52c9b40d5f4dd98384c037d4cad938b329c6af826f695a7123007b7e06f24c6a48f20:1
node.1 = 74034fb43f75c63bb2259a63f71d9d1c658945409889d3028d257914be1612d1f2e80c4a777cb3e7929a0f0d671eac2fb9a99fa45d39f5451b6357b00c389a84:1
```

#### 2.1.3 gas配置

FISCO BCOS兼容EVM和WASM虚拟机，为了防止针对EVM/WASM的DOS攻击，EVM在执行交易时，引入了gas概念，用来度量智能合约执行过程中消耗的计算和存储资源，包括交易最大gas限制，若交易或区块执行消耗的gas超过限制(gas limit)，则丢弃交易，创世块的`[tx].gas_limit`可配置交易最大gas限制，默认是3000000000，链初始化完毕后，可通过[控制台指令](../../develop/console/console_commands.html#setsystemconfigbykey)动态调整gas限制。

- `[tx].gas_limit`: 交易执行时gas限制，默认设置为3000000000

`[tx].gas_limit`配置示例如下：

```ini
[tx]
gas_limit=3000000000
```

#### 2.1.4 数据兼容性配置

FISCO BCOS v3.0.0设计并实现了兼容性框架，可支持数据版本的动态升级，该配置项位于`[version]`下：

- `[version].compatibility_version`: 数据兼容版本号，默认为`v3.0.0`，新版本升级时，替换所有二进制后，可通过[控制台指令setSystemConfigByKey](../../develop/console/console_commands.html#setsystemconfigbykey)动态升级数据版本。

#### 2.1.5 执行模块配置

`[executor]`配置项涉及执行相关的创世块配置，主要包括：

- `[executor].is_wasm`: 用于配置虚拟机类型，`true`表明使用WASM虚拟机，`false`表明使用EVM虚拟机，该配置选希望不可动态调整，默认为`false`;
- `[executor].is_auth_check`: 权限控制的配置开关，`true`表明开启权限控制，`false`表明关闭权限控制，该配置选项不可动态调整，默认关闭权限控制功能;
- `[executor].is_serial_execute`: 交易执行串行与并行模式的配置开关，`true`表明进入串行执行模式，`false`表明进入DMC并行执行模式，该配置选希望不可动态调整，默认为`false`;
- `[executor].auth_admin_account`: 权限管理员账户地址，仅用于权限控制场景中。

### 2.2 节点配置

节点配置`config.ini`主要用来配置节点的链ID、群组ID、账本类型(国密/非国密)等，主要包括服务配置选项、共识配置选项、存储配置选项、交易池配置选项、日志配置选项等。

#### 2.2.1 服务配置选项

服务配置选项位于`[service]`，主要用于配置该节点对应的RPC/Gateway服务名，具体包括: 

- `[service].rpc`: RPC服务名;
- `[service].gateway`: Gateway服务名;

```ini
[service]
rpc = chain0.agencyABcosRpcService
gateway = chain0.agencyABcosGatewayService
```

#### 2.2.2 落盘加密配置选项

落盘加密配置选项位于`[storage_security]`，主要包括：

- `[storage_security].enable`: 落盘加密开关，`true`表明开启落盘加密，`false`表明关闭落盘加密，默认为`false`;
- `[storage_security].key_center_url`: 开启落盘加密时，`key_center_url`配置了[Key Manager](../../design/storage_security.md)的url，用于获取数据加解密密钥;
- `[storage_security].cipher_data_key`: 数据落盘加密的私钥。

#### 2.2.3 共识配置选项

考虑到PBFT模块打包太快会导致某些区块中仅打包1到2个很少的交易，浪费存储空间，FISCO BCOS 在配置`config.ini`的`[consensus]`下引入`min_seal_time`配置项来控制PBFT共识打包的最短时间，即：共识节点打包时间超过`min_seal_time`且打包的交易数大于0才会开始共识流程，处理打包生成的新区块。

- `[consensus].min_seal_time`: 最小的区块生成时间，默认为500ms。

```ini
[consensus]
min_seal_time=500
```

#### 2.2.4 存储配置选项

存储配置选项位于`[storage]`中，主要用于配置链上数据路径：

- `[storage].data_path`: 账本数据存储路径;
- `[storage].enable_cache`: 是否开启缓存，默认为`true`;
- `[storage].type`: 底层存储数据库类型，默认为RocksDB;
- `pd_addrs`: Pro为空，max版本字段；
- `key_page_size`: key_page存储中每个page的size，默认为10240k。

```ini
[storage]
    data_path=data
    enable_cache=true
    type=RocksDB
    pd_addrs=
    key_page_size=10240
```

#### 2.2.5 交易池配置选项

交易池配置选项位于`[txpool]`:

- `[txpool].limit`: 交易池的容量限制, 默认为`15000`;
- `[txpool].notify_worker_num`: 交易通知线程数量，默认为2;
- `[txpool].verify_worker_num`: 交易验证线程数量，默认为机器CPU核数;
- `[txpool].txs_expiration_time`: 交易过期时间，以秒为单位，默认10分钟，即：超过十分钟没有被共识模块打包的交易将会被直接丢弃。

```ini
[txpool]
    ; size of the txpool, default is 15000
    limit=15000
    ; txs notification threads num, default is 2
    notify_worker_num=2
    ; txs verification threads num, default is the number of CPU cores
    verify_worker_num=2
    ; txs expiration time, in seconds, default is 10 minutes
    txs_expiration_time = 600
```

#### 2.2.6 日志配置选项

日志配置选项位于`[log]`，具体包括：

- `[log].enable`: 启用/禁用日志，设置为`true`表示启用日志；设置为`false`表示禁用日志，**默认设置为true，性能测试可将该选项设置为`false`，降低打印日志对测试结果的影响**
- `[log].log_path`:日志文件路径。
- `[log].level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `[log].max_log_file_size`：每个日志文件最大容量，**计量单位为MB，默认为200MB**。

```ini
[log]
enable = true
log_path = ./log
; info debug trace
level = DEBUG
; MB
max_log_file_size = 200
```


## 3. RPC/网关服务配置

RPC/网关服务搭建请参考[这里](./installation.html)，其主要包括节点配置`config.ini`。

### 3.1 网络连接配置

网络连接配置位于`[p2p]`，主要包括：

- `[p2p].listen_ip`: RPC/网关监听IP，为保证节点跨机器部署通信正常，监听IP默认设置为`0.0.0.0`;
- `[p2p].listen_port`:  RPC/网关监听端口，默认设置为`30300`;
- `[p2p].sm_ssl`: 节点之间、SDK与RPC服务间是否使用国密SSL连接，默认为`false`;
- `[p2p].nodes_path`: 网关连接文件`nodes.json`所在目录，默认为当前目录;
- `[p2p].nodes_file`: 网关连接信息文件`nodes.json`文件名，默认为`nodes.json`;
- `[p2p].thread_count`: RPC/网关网络处理线程数，默认为4.

```ini
[p2p]
listen_ip = 0.0.0.0
listen_port = 30301
; ssl or sm ssl
sm_ssl = false
nodes_path = ./
nodes_file = nodes.json
thread_count = 4
```

### 3.2 RPC/网关服务配置

RPC/网关服务配置位于`service`配置中，主要包括: 

- `[service].rpc`: 网关对应的RPC服务名;
- `[service].gateway`: RPC对应的网关服务名;

```ini
[service]
gateway = chain.agencyABcosGatewayService
rpc = chain.agencyABcosRpcService
```

### 3.3 链配置

链配置位于`[chain]`配置选项下，主要包括:

- `[chain].chain_id`: 链id;
- `[chain].sm_crypto`: 开启落盘加密时，证书加密类型。

```ini
[chain]
; use sm crypto or not, should nerver be changed
sm_crypto = false
chain_id = chain0
```

### 3.4 落盘加密配置

FISCO BCOS v3.0.0版本开始支持落盘加密功能，可对RPC/网关服务的SSL连接私钥进行加密，保障SSL连接私钥的机密性，主要通过`[storage_security]`配置:

- `[storage_security].enable`: 是否开启落盘加密功能，默认关闭;
- `[storage_security].key_center_url`: 开启落盘加密时，`key_center_url`配置了[Key Manager](../../design/storage_security.md)的url，用于获取数据加解密密钥;
- `[storage_security].cipher_data_key`: 数据落盘加密的私钥。

落盘加密配置示例如下：

```ini
[storage_security]
; enable data disk encryption or not, default is false
enable = false
; url of the key center, in format of ip = port
;key_center_url =
;cipher_data_key =
```

### 日志配置选项

日志配置位于`[log]`选项中:

- `[log].enable`: 启用/禁用日志，设置为`true`表示启用日志；设置为`false`表示禁用日志，**默认设置为true，性能测试可将该选项设置为`false`，降低打印日志对测试结果的影响**
- `[log].log_path`:日志文件路径。
- `[log].level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `[log].max_log_file_size`：每个日志文件最大容量，**计量单位为MB，默认为200MB**。

```ini
[log]
enable = true
log_path = ./log
; info debug trace
level = DEBUG
; mb
max_log_file_size = 200
```
