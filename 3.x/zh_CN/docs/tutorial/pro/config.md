# 配置介绍

标签：``Pro版区块链网络`` ``扩容群组`` ``多群组微服务区块链`` ``tars`` ``RpcService`` ``GatewayService`` ``NodeService``

------------

FISCO BCOS使用tars构建多群组微服务区块链，搭建FISCO BCOS Pro版区块链请[参考这里](./installation.md)

## 多群组微服务文件组织结构
利用区块链构建脚本 `build_chain.py` 可构建 **RpcService** RPC服务，**GatewayService** 网关服务，以及 **NodeService** 节点服务。构建后生成 `generated` 目录，服务配置信息在此目录下。

```shell
generated/
├── chain # 链
│   ├── group # 群组
│   │   ├── 10.39.190.60 # 本机的eth0 ip地址
│   │   │   ├── group0node00BcosNodeService # NodeService 00
│   │   │   │   ├── config.genesis.tmp # 创世块配置文件
│   │   │   │   ├── config.ini.tmp # 配置文件
│   │   │   │   ├── node.nodeid # 节点id
│   │   │   │   ├── node.pem # 节点pem格式私钥
│   │   │   ├── group0node10BcosNodeService # # NodeService 10
│   │   │   │.....
├── gateway # 网关
│   ├── chain # 链
│   │   ├── 10.39.190.60 # 本机的eth0 ip地址
│   │   │   ├── agencyABcosGatewayService # GatewayService A
│   │   │   │   ├── ssl
│   │   │   │   │   ├── ca.crt # CA证书
│   │   │   │   │   ├── cert.cnf # 证书配置
│   │   │   │   │   ├── ssl.crt # ssl 证书
│   │   │   │   │   ├── ssl.key # ssl 私钥
│   │   │   │   ├── config.ini.tmp # 配置文件
│   │   │   │   ├── nodes.json.tmp # 节点json信息，{"nodes": ["10.39.190.60:30300", "10.39.190.60:30301"]}
│   │   │   ├── agencyBBcosGatewayService # GatewayService B
│   │   │   │.....
│   │   ├── ca # CA 证书
│   │   │   ├── ca.crt # CA 证书
│   │   │   ├── ca.key # CA 私钥
│   │   │   ├── cert.cnf # 证书私钥
├── rpc
│   ├── chain
│   │   ├── 10.39.190.60 # 本机的eth0 ip地址
│   │   │   ├── agencyABcosRpcService # RpcService A
│   │   │   │   ├── ssl
│   │   │   │   │   ├── ca.crt # CA证书
│   │   │   │   │   ├── cert.cnf # 证书配置
│   │   │   │   │   ├── ssl.crt # ssl 证书
│   │   │   │   │   ├── ssl.key # ssl 私钥
│   │   │   │   ├── sdk
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

## tgz文件
build_chain.py 生成gateway、rpc会生成tgz文件，可上传到tars使用:
`agencyABcosGatewayService.tgz` 、`agencyABcosRpcService.tgz`

## node节点服务
使用 `-t node` 搭建节点服务，其生成的配置文件有 `config.genesis.tmp` `config.ini.tmp`

### config.genesis.tmp 创世块配置文件

#### consensus 共识

- `consensus_type`: 共识类型，默认设置为`pbft`；
- `block_tx_count_limit`: 每个区块里可包含的最大交易数，默认设置为 1000;
- `leader_period`: 共识过程，每个leader连续打包的区块数目，默认为 5；
- `node.idx`：共识节点列表，配置了参与共识节点的NodeID。


consensus配置示例如下：
```ini
[consensus]
consensus_type = pbft
block_tx_count_limit = 1000
leader_period = 5
node.0 = 94172c95917fbf47b4b98aba0cc68f83f61a06b0bc373695590f343464b52c9b40d5f4dd98384c037d4cad938b329c6af826f695a7123007b7e06f24c6a48f20:1
node.1 = 74034fb43f75c63bb2259a63f71d9d1c658945409889d3028d257914be1612d1f2e80c4a777cb3e7929a0f0d671eac2fb9a99fa45d39f5451b6357b00c389a84:1
```

#### tx 交易

- `gas_limit`: 交易执行时gas限制，默认设置为 300000000；

consensus配置示例如下：
```ini
[tx]
gas_limit = 300000000
```

### config.ini.tmp 配置文件

#### chain

- `sm_crypto`: 是否使用国密算法加密，默认设置为`false`；
- `group_id`: 群组id;
- `chain_id`: 链id；

```ini
[chain]
sm_crypto = false
group_id = group0
chain_id = chain0
```

#### service

- `rpc`: RPC服务名。
- `gateway`: Gateway服务名。
- `node_name`: 节点服务名。

```ini
[service]
rpc = chain.agencyABcosRpcService
gateway = chain.agencyABcosGatewayService
node_name = node00
```

#### security

- `private_key_path`: 节点共识模块用于签名的私钥文件路径。

```ini
[security]
private_key_path = conf/node.pem
```

#### consensus

- `min_seal_time`: 最小的区块生成时间，默认为500ms。

```ini
[consensus]
min_seal_time = 500
```

#### executor

- `is_wasm`: 节点是否使用WASM虚拟机，默认使用EVM虚拟机。
- `is_auth_check`: 是否开启权限检查，默认为false。权限使用文档请参考链接：[权限治理使用指南](../../develop/committee_usage.md)。
- `auth_admin_account`: 权限管理员账户地址，默认为空。权限使用文档请参考链接：[权限治理使用指南](../../develop/committee_usage.md)。

```ini
[executor]
is_wasm = false
is_auth_check=false
auth_admin_account=
```

#### storage

- `data_path`: 节点数据路径。

```ini
[storage]
data_path = data
```

#### txpool

- `limit`: 交易池的容量限制。
- `notify_worker_num`: 交易通知线程数量，默认为2。
- `verify_worker_num`: 交易验证线程数量，默认为2。

```ini
[txpool]
limit = 15000
notify_worker_num = 2
verify_worker_num = 2
```

#### log

- `enable`: 启用/禁用日志，设置为`true`表示启用日志；设置为`false`表示禁用日志，**默认设置为true，性能测试可将该选项设置为`false`，降低打印日志对测试结果的影响**
- `log_path`:日志文件路径。
- `level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `max_log_file_size`：每个日志文件最大容量，**计量单位为MB，默认为200MB**。

```ini
[log]
enable = true
log_path = ./log
level = DEBUG
max_log_file_size = 200
```


## gateway网关服务
使用 `-t gateway` 搭建网关服务，其生成的配置文件有 `config.ini.tmp`
### config.ini.tmp 配置文件

#### p2p

- `listen_ip`: P2P监听IP，为保证节点跨机器部署通信正常，监听IP默认设置为`0.0.0.0`。
- `listen_port`: P2P监听端口，默认设置为`30300`。
- `sm_ssl`: 节点之间是否使用国密SSL连接，默认为`false`。
- `nodes_path`: 节点连接文件`nodes.json`所在目录，默认为当前目录。
- `nodes_file`: 节点连接信息文件`nodes.json`文件名，默认为`nodes.json`。
- `thread_count`: P2P网络处理线程数，默认为 4。

```ini
[p2p]
listen_ip = 0.0.0.0
listen_port = 30300
sm_ssl = false
nodes_path = ./
nodes_file = nodes.json
thread_count = 4
```

#### service

- `rpc`: RPC服务名。
- `gateway`: Gateway服务名。

```ini
[service]
gateway = chain.agencyABcosGatewayService
rpc = chain.agencyABcosRpcService
```

#### chain

- `chain_id`: 链id；

```ini
[chain]
chain_id = chain0
```

#### log

- `enable`: 启用/禁用日志，设置为`true`表示启用日志；设置为`false`表示禁用日志，**默认设置为true，性能测试可将该选项设置为`false`，降低打印日志对测试结果的影响**
- `log_path`:日志文件路径。
- `level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `max_log_file_size`：每个日志文件最大容量，**计量单位为MB，默认为200MB**。

```ini
[log]
enable = true
log_path = ./log
level = DEBUG
max_log_file_size = 200
```


## rpc服务
使用 `-t rpc` 搭建RPC服务，其生成的配置文件有 `config.ini.tmp`
### config.ini.tmp 配置文件
rpc服务配置文件里有 `rpc`、`service`、`chain`、`cert`、`log` ，除了`rpc`其他都雷同，不做赘述。

#### rpc

- `listen_ip`: RPC服务监听IP，为方便节点和SDK跨机器部署，默认设置为`0.0.0.0`；
- `listen_port`: 监听端口，默认设置为`20200`;
- `thread_count`: 线程数，默认为 4；
- `sm_ssl`: 使用 ssl 还是 sm ssl，默认为 false；

```ini
[rpc]
listen_ip = 0.0.0.0
listen_port = 20200
thread_count = 4
sm_ssl = false
```

