# 节点配置介绍

标签：``Air版区块链网络`` ``配置`` ``config.ini`` ``config.genesis`` ``端口配置`` ``日志级别`` ``配置``

----

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

Air版本FISCO BCOS主要包括创世块配置文件`config.genesis`和节点配置文件`config.ini`:

- `config.ini`：节点配置文件，主要配置RPC、P2P、SSL证书、账本数据路径、落盘加密等信息;
- `config.genesis`：创世块配置文件，**须保证群组内所有节点的创世块配置一致**, **创世块配置文件在链初始化后不可更改**, 链初始化后，即使更改了创世块配置，新的配置不会生效，系统仍然使用初始化链时的genesis配置。

## 1. 创世块配置

节点创世块配置位于配置文件`config.genesis`中。

```eval_rst
.. note::
    - **须保证群组内所有节点的创世块配置一致**
    - **创世块配置文件在链初始化后不可更改**
    - 链初始化后，即使更改了创世块配置，新的配置不会生效，系统仍然使用初始化链时的genesis配置
```
### 1.1 配置链信息

`[chain]`配置节点的链信息，**该配置下的字段信息，一旦确定就不应该再更改**：

- `[chain].sm_crypto`: 节点是否使用国密账本，默认为`false`;
- `[chain].group_id`：群组ID，默认为`group0`;
- `[chain].chain_id`：链ID，默认为`chain0`.

```ini
[chain]
    ; use SM crypto or not, should nerver be changed
    sm_crypto=false
    ; the group id, should nerver be changed
    group_id=group0
    ; the chain id, should nerver be changed
    chain_id=chain0
```

### 1.2 共识配置

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

### 1.3 gas配置

FISCO BCOS兼容EVM和WASM虚拟机，为了防止针对EVM/WASM的DOS攻击，EVM在执行交易时，引入了gas概念，用来度量智能合约执行过程中消耗的计算和存储资源，包括交易最大gas限制，若交易或区块执行消耗的gas超过限制(gas limit)，则丢弃交易，创世块的`[tx].gas_limit`可配置交易最大gas限制，默认是3000000000，链初始化完毕后，可通过[控制台指令](../../develop/console/console_commands.html#setsystemconfigbykey)动态调整gas限制。

- `[tx].gas_limit`: 交易执行时gas限制，默认设置为3000000000

`[tx].gas_limit`配置示例如下：

```ini
[tx]
gas_limit = 3000000000
```

### 1.4 数据兼容性配置

FISCO BCOS v3.0.0设计并实现了兼容性框架，可支持数据版本的动态升级，该配置项位于`[version]`下：

- `[version].compatibility_version`: 数据兼容版本号，默认为`v3.0.0`，新版本升级时，替换所有二进制后，可通过[控制台指令setSystemConfigByKey](../../develop/console/console_commands.html#setsystemconfigbykey)动态升级数据版本。

### 1.5 执行模块配置

`[executor]`配置项涉及执行相关的创世块配置，主要包括：

- `[executor].is_wasm`: 用于配置虚拟机类型，`true`表明使用WASM虚拟机，`false`表明使用EVM虚拟机，该配置选项不可动态调整，默认为`false`;
- `[executor].is_auth_check`: 权限控制的配置开关，`true`表明开启权限控制，`false`表明关闭权限控制，该配置选项不可动态调整，默认关闭权限控制功能;
- `[executor].is_serial_execute`: 交易执行串行与并行模式的配置开关，`true`表明进入串行执行模式，`false`表明进入DMC并行执行模式，该配置选希望不可动态调整，默认为`false`;
- `[executor].auth_admin_account`: 权限管理员账户地址，仅用于权限控制场景中。

## 2. 节点配置文件

`config.ini`采用`ini`格式，主要包括 **p2p、rpc、cert、chain、security、consensus、storage、txpool和log** 配置项。

```eval_rst
.. important::
    - 云主机的公网IP均为虚拟IP，若listen_ip填写外网IP，会绑定失败，须填写0.0.0.0
    - RPC/P2P监听端口必须位于1024-65535范围内，且不能与机器上其他应用监听端口冲突
    - 为便于开发和体验，listen_ip参考配置是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如：内网IP或特定的外网IP
```

### 2.1 配置P2P

P2P相关配置包括：

- `[p2p].listen_ip`：P2P监听IP，默认设置为`0.0.0.0`;
- `[p2p].listen_port`：节点P2P监听端口;
- `[p2p].sm_ssl`: 节点之间的SSL连接是否使用国密SSL协议，`true`表示开启国密SSL连接; `false`表示采用非国密SSL连接，默认为`false`;
- `[p2p].nodes_path`：节点连接信息文件`nodes.json`所在目录，默认为当前文件夹;
- `[p2p].nodes_file`：`P2P`连接信息文件`nodes.json`所在路径。

P2P配置示例如下：

```ini
[p2p]
    listen_ip=0.0.0.0
    listen_port=30300
    ; ssl or sm ssl
    sm_ssl=false
    nodes_path=./
    nodes_file=nodes.json
```

`p2p`连接配置文件`nodes_file`格式:

```shell
{"nodes":[连接列表]}
```

示例:

```shell
{"nodes":["127.0.0.1:30300","127.0.0.1:30301","127.0.0.1:30302","127.0.0.1:30303"]}
```

`P2P`支持可配置的网络连接，并且支持服务运行期间动态新增/删减连接节点，流程如下：

- 修改`[p2p].nodes_file`配置中的连接信息
- 向服务进程发送信号`USR1`:

```shell
kill -USR1 网关节点pid
```

服务会重新加载`P2P`连接信息。

### 2.2 配置RPC

RPC配置选项位于`[rpc]`，主要包括:

- `[rpc].listen_ip`: RPC监听IP，为方便节点和SDK跨机器部署，默认设置为`0.0.0.0`;
- `[rpc].listen_port`: RPC监听端口，默认设置为`20200`;
- `[rpc].thread_count`: RPC服务线程数，默认为 4;
- `[rpc].sm_ssl`: SDK与节点之间的连接是否使用国密SSL连接，`true`表示开启国密SSL连接; `false`表示采用非国密SSL连接，默认为`false`.

RPC配置示例如下：

```ini
[rpc]
    listen_ip=0.0.0.0
    listen_port=20200
    thread_count=4
    ; ssl or sm ssl
    sm_ssl=false
    ; ssl connection switch, if disable the ssl connection, default: false
    ;disable_ssl=true
```

### 2.3 配置证书信息

基于安全考虑，FISCO BCOS节点间采用SSL加密通信，`[cert]`配置SSL连接的证书信息：
- `[cert].ca_path`: 证书路径，默认为`conf`;
- `[cert].ca_cert`: ca证书名，默认为`ca.crt`;
- `[cert].node_key`: 节点SSL连接私钥，默认为`ssl.key`;
- `[cert].node_cert`: 节点SSL连接证书，默认为`ssl.cert`.

```ini
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

`[security]` 配置私钥路径，该私钥主要用于共识模块的消息签名，如下:

- `[security].private_key_path`：私钥文件路径，默认为`conf/node.pem`。

```ini
[security]
    private_key_path=conf/node.pem
```


### 2.4 配置共识信息

考虑到PBFT模块打包太快会导致某些区块中仅打包1到2个很少的交易，浪费存储空间，FISCO BCOS在可变配置`config.ini`的`[consensus]`下引入`min_seal_time`配置项来控制PBFT共识打包的最短时间，即：共识节点打包时间超过`min_seal_time`且打包的交易数大于0才会开始共识流程，处理打包生成的新区块。

```eval_rst
.. important::
   - ``min_seal_time`` 默认为500ms
   - ``min_seal_time`` 不可超过出空块时间1000ms，若设置值超过1000ms，系统默认min_seal_time为500ms

```

```ini
[consensus]
    ; min block generation time(ms)
    min_seal_time=500
```

### 2.5 配置存储信息

存储配置位于`[storage]`，具体包括:

- `[storage].data_path`: 区块链节点数据存储路径，默认为data;
- `[storage].enable_cache`: 是否开启缓存，默认为`true`;
- `[storage].key_page_size`: KeyPage存储方案中，存储页大小，单位是字节，要求不小于`4096`(4KB)，默认为`10240`(10KB);

```ini
[storage]
    data_path=data
    enable_cache=true
    key_page_size=10240
```

### 2.6 配置落盘加密

落盘加密配置选项位于`[storage_security]`:

- `[storage_security].enable`: 是否启用落盘加密，默认关闭落盘加密;
- `[storage_security].key_manager_url`: 开启落盘加密时，`key_center_url`配置了[Key Manager](../../design/storage_security.md)的url，用于获取数据加解密密钥;
- `[storage_security].cipher_data_key`: 数据落盘加密的私钥。

```ini
[storage_security]
    ; enable data disk encryption or not, default is false
    enable=false
    ; url of the key center, in format of ip:port
    key_center_url=
    cipher_data_key=
```

### 2.7 配置交易池信息

交易池配置选项位于`[txpool]`:

- `[txpool].limit`: 交易池的容量限制, 默认为`15000`;
- `[txpool].notify_worker_num`: 交易通知线程数量，默认为2;
- `[txpool].verify_worker_num`: 交易验证线程数量，默认为机器CPU核数;
- `[txpool].txs_expiration_time`: 交易过期时间，以秒为单位，默认10分钟，即：超过十分钟没有被共识模块打包的交易将会被直接丢弃。 

```ini
[txpool]
; size of the txpool, default is 15000
limit = 15000
; txs notification threads num, default is 2
notify_worker_num = 2
; txs verification threads num, default is the number of cpu cores
;verify_worker_num = 2
; txs expiration time, in seconds, default is 10 minutes
txs_expiration_time = 600
```

### 2.8 配置日志信息

FISCO BCOS支持功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，日志配置主要位于`config.ini`的`[log]`配置项中。

- `[log].enable`: 启用/禁用日志，设置为`true`表示启用日志；设置为`false`表示禁用日志，**默认设置为true，性能测试可将该选项设置为`false`，降低打印日志对测试结果的影响**
- `[log].log_path`:日志文件路径。
- `[log].level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `[log].max_log_file_size`：每个日志文件最大容量，**计量单位为MB，默认为200MB**。

日志配置示例如下：

```ini
[log]
    enable=true
    log_path=./log
    ; info debug trace
    level=DEBUG
    ; MB
    max_log_file_size=200
```
