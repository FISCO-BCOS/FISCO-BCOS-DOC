# 配置文件与配置项

FISCO BCOS支持多账本，每条链包括多个独立账本，账本间数据相互隔离，群组间交易处理相互隔离，每个节点包括一个主配置`config.ini`和多个账本配置`group.group_id.genesis`、`group.group_id.ini`。

- `config.ini`：主配置文件，主要配置RPC、P2P、SSL证书、账本配置文件路径、兼容性等信息。
- `group.group_id.genesis`：群组配置文件，群组内所有节点一致，节点启动后，不可手动更改该配置。主要包括群组共识算法、存储类型、最大gas限制等配置项。
- `group.group_id.ini`：群组可变配置文件，包括交易池大小等，配置后重启节点生效。

## 主配置文件config.ini

`config.ini`采用`ini`格式，主要包括 **rpc、p2p、group、network_security和log** 配置项。

```eval_rst
.. important::
    - 云主机的公网IP均为虚拟IP，若listen_ip填写外网IP，会绑定失败，须填写0.0.0.0
    - RPC/P2P/Channel监听端口必须位于1024-65535范围内，且不能与机器上其他应用监听端口冲突
```

### 配置RPC

- `listen_ip`: 安全考虑，建链脚本默认监听127.0.0.1，如果需要外网访问RPC或外网使用SDK请监听**节点的外网IP**或`0.0.0.0`；
- `channel_listen_port`: Channel端口，对应到[Web3SDK](../sdk/java_sdk.html#id2)配置中的`channel_listen_port`；
- `jsonrpc_listen_port`: JSON-RPC端口。


RPC配置示例如下：

```ini
[rpc]
    listen_ip=127.0.0.1
    channel_listen_port=30301
    jsonrpc_listen_port=30302
```

### 配置P2P

当前版本FISCO BCOS必须在`config.ini`配置中配置连接节点的`IP`和`Port`，P2P相关配置包括：

- `listen_ip`：P2P监听IP，默认设置为`0.0.0.0`。
- `listen_port`：节点P2P监听端口。
- `node.*`: 节点需连接的所有节点`IP:Port`或`DomainName:Port`。该选项支持域名，但建议需要使用的用户[手动**编译源码**](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html#id2)。
- `enable_compress`：开启网络压缩的配置选项，配置为true，表明开启网络压缩功能，配置为false，表明关闭网络压缩功能，网络压缩详细介绍请参考[这里](../design/features/network_compress.md)。

P2P配置示例如下：

```ini
[p2p]
    listen_ip=0.0.0.0
    listen_port=30300
    node.0=127.0.0.1:30300
    node.1=127.0.0.1:30304
    node.2=127.0.0.1:30308
    node.3=127.0.0.1:30312
```

### 配置账本文件路径

`[group]`配置本节点所属的所有群组配置路径：

- `group_data_path`: 群组数据存储路径。
- `group_config_path`: 群组配置文件路径。

> 节点根据`group_config_path`路径下的所有`.genesis`后缀文件启动群组。

```ini
[group]
    ; 所有群组数据放置于节点的data子目录
    group_data_path=data/
    ; 程序自动加载该路径下的所有.genesis文件
    group_config_path=conf/
```


### 配置证书信息

基于安全考虑，FISCO BCOS节点间采用SSL加密通信，`[network_security]`配置SSL连接的证书信息：

- `data_path`：证书和私钥文件所在目录。
- `key`: 节点私钥相对于`data_path`的路径。
- `cert`: 证书`node.crt`相对于`data_path`的路径。
- `ca_cert`: ca证书文件路径。
- `ca_path`: ca证书文件夹，多ca时需要。

```ini
[network_security]
    data_path=conf/
    key=node.key
    cert=node.crt
    ca_cert=ca.crt
    ;ca_path=
```

### 配置黑名单列表

基于防作恶考虑，FISCO BCOS允许节点将不受信任的节点加入到黑名单列表，并拒绝与这些黑名单节点建立连接，通过`[certificate_blacklist]`配置：

> `crl.idx`: 黑名单节点的Node ID, 节点Node ID可通过`node.nodeid`文件获取; `idx`是黑名单节点的索引。

黑名单的详细信息还可参考[CA黑名单](./certificate_list.md)

黑名单列表配置示例如下：

```ini
; 证书黑名单
[certificate_blacklist]
    crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e
3787c338a4
```

### 配置日志信息

FISCO BCOS支持功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，主要配置项如下：

- `enable`: 启用/禁用日志，设置为`true`表示启用日志；设置为`false`表示禁用日志，**默认设置为true，性能测试可将该选项设置为`false`，降低打印日志对测试结果的影响**
- `log_path`:日志文件路径。
- `level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `max_log_file_size`：每个日志文件最大容量，**计量单位为MB，默认为200MB**。
- `flush`：boostlog默认开启日志自动刷新，若需提升系统性能，建议将该值设置为false。

boostlog示例配置如下：

```ini
[log]
    ; 是否启用日志，默认为true
    enable=true
    log_path=./log
    level=info
    ; 每个日志文件最大容量，默认为200MB
    max_log_file_size=200
    flush=true
```

### 配置节点兼容性

FISCO BCOS 2.0+所有版本向前兼容，可通过`config.ini`中的`[compatibility]`配置节点的兼容性，此配置项建链时工具会自动生成，用户不需修改。

- `supported_version`：当前节点运行的版本

```eval_rst
.. important::
    - 可通过 ``./fisco-bcos --version | grep "Version" `` 命令查看FISCO BCOS的当前支持的最高版本
    - build_chain.sh生成的区块链节点配置中，supported_version配置为FISCO BCOS当前的最高版本
    - 旧节点升级为新节点时，直接将旧的FISCO BCOS二进制替换为最新FISCO BCOS二进制即可，
```

`FISCO BCOS 2.2.0`节点的`[compatibility]`配置如下：

```ini

[compatibility]
    supported_version=2.2.0
```

### 可选配置：落盘加密

为了保障节点数据机密性，FISCO BCOS引入[落盘加密](../design/features/storage_security.md)保障节点数据的机密性，**落盘加密**操作手册请[参考这里](./storage_security.md)。

`config.ini`中的`storage_security`用于配置落盘加密，主要包括：

- `enable`： 是否开启落盘加密，默认不开启；

- `key_manager_ip`：[Key Manager](https://github.com/FISCO-BCOS/key-manager)服务的部署IP；

- `key_manager_port`：[Key Manager](https://github.com/FISCO-BCOS/key-manager)服务的监听端口；

- `cipher_data_key`: 节点数据加密密钥的密文，`cipher_data_key`的产生参考[落盘加密操作手册](./storage_security.md)。

落盘加密节点配置示例如下：

```ini
[storage_security]
enable=true
key_manager_ip=127.0.0.1
key_manager_port=31443
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

## 群组系统配置说明

每个群组都有单独的配置文件，按照启动后是否可更改，可分为**群组系统配置**和**群组可变配置**。
群组系统配置一般位于节点的`conf`目录下`.genesis`后缀配置文件中。

如：`group1`的系统配置一般命名为`group.1.genesis`，群组系统配置主要包括**群组ID、共识、存储和gas**相关的配置。

```eval_rst
.. important::
    配置系统配置时，需注意：

    - **配置群组内一致** ：群组系统配置用于产生创世块(第0块)，因此必须保证群组内所有节点的该配置一致
    - **节点启动后不可更改** ：系统配置已经作为创世块写入了系统表，链初始化后不可更改
    - 链初始化后，即使更改了genesis配置，新的配置不会生效，系统仍然使用初始化链时的genesis配置
    - 由于genesis配置要求群组内所有节点一致，建议使用 `build_chain <build_chain.html>`_ 生成该配置
```

### 群组配置

`[group]`配置**群组ID**，节点根据该ID初始化群组。

群组2的群组配置示例如下：

```ini
[group]
id=2
```

### 共识配置

`[consensus]`涉及共识相关配置，包括：

- `consensus_type`：共识算法类型，目前支持[PBFT](../design/consensus/pbft.md)和[Raft](../design/consensus/raft.md)，默认使用PBFT共识算法；

- `max_trans_num`：一个区块可打包的最大交易数，默认是1000，链初始化后，可通过[控制台](./console.html#setsystemconfigbykey)动态调整该参数；

- `node.idx`：共识节点列表，配置了参与共识节点的[Node ID](../design/consensus/pbft.html#id1)，节点的Node ID可通过`${data_path}/node.nodeid`文件获取(其中`${data_path}`可通过主配置`config.ini`的`[network_security].data_path`配置项获取)

```ini
; 共识协议配置
[consensus]
    ; 共识算法，目前支持PBFT(consensus_type=pbft)和Raft(consensus_type=raft)
    consensus_type=pbft
    ; 单个块最大交易数
    max_trans_num=1000
    ; leader节点的ID列表
    node.0=123d24a998b54b31f7602972b83d899b5176add03369395e53a5f60c303acb719ec0718ef1ed51feb7e9cf4836f266553df44a1cae5651bc6ddf50e01789233a
    node.1=70ee8e4bf85eccda9529a8daf5689410ff771ec72fc4322c431d67689efbd6fbd474cb7dc7435f63fa592b98f22b13b2ad3fb416d136878369eb413494db8776
    node.2=7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
    node.3=fd6e0bfe509078e273c0b3e23639374f0552b512c2bea1b2d3743012b7fed8a9dec7b47c57090fa6dcc5341922c32b89611eb9d967dba5f5d07be74a5aed2b4a
```

### 状态模式配置

[state](../design/storage/mpt.html)用于存储区块链状态信息，位于genesis文件中`[state]`：

- `type`：state类型，目前支持[storage state](../design/storage/storage.html#id6)和[MPT state](../design/storage/mpt.html)，**默认为storage state**，storage state将交易执行结果存储在系统表中，效率较高，MPT state将交易执行结果存储在MPT树中，效率较低，但包含完整的历史信息。

```eval_rst
.. important::
   MPT State将会在v2.3.0弃用，请使用 **storage state**
```

```ini
[state]
    type=storage
```

### gas配置

FISCO BCOS兼容以太坊虚拟机([EVM](../design/virtual_machine/evm.md))，为了防止针对[EVM](../design/virtual_machine/evm.md)的DOS攻击，EVM在执行交易时，引入了gas概念，用来度量智能合约执行过程中消耗的计算和存储资源，包括交易最大gas限制和区块最大gas限制，若交易或区块执行消耗的gas超过限制(gas limit)，则丢弃交易或区块。FISCO BCOS是联盟链，简化了gas设计，**仅保留交易最大gas限制，区块最大gas通过[共识配置的max_trans_num](./configuration.html#id11)和交易最大gas限制一起约束**。FISCO BCOS通过genesis的`[tx].gas_limit`来配置交易最大gas限制，默认是300000000，链初始化完毕后，可通过[控制台指令](./console.html#setsystemconfigbykey)动态调整gas限制。

```ini
[tx]
    gas_limit=300000000
```

## 账本可变配置说明

账本可变配置位于节点`conf`目录下`.ini`后缀的文件中。

如：`group1`可变配置一般命名为`group.1.ini`，可变配置主要包括交易池大小、PBFT共识消息转发的TTL、PBFT共识打包时间设置、PBFT交易打包动态调整设置、并行交易设置等。

### 配置storage

存储目前支持RocksDB、MySQL、External三种模式，用户可以根据需要选择使用的DB，其中RocksDB性能最高；MySQL支持用户使用MySQL数据库，方便数据的查看；External通过数据代理访问mysql，用户需要在启动并配置数据代理。设计文档参考[AMDB存储设计](../design/storage/storage.html)。RC3版本起我们使用RocksDB替代LevelDB以获得更好的性能表现，仍支持旧版本LevelDB。

#### 公共配置项

```eval_rst
.. important::
   External模式将会在v2.3.0弃用，若要使用Mysql请用直连模式，配置type为MySQL。
```

- `type`：存储的DB类型，支持`RocksDB`、`MySQL`、`External`和`scalable`，不区分大小写。DB类型为RocksDB时，区块链系统所有数据存储于RocksDB本地数据库中；type为`MySQL`时，节点根据配置访问mysql数据库；type为`external`时，节点通过数据代理访问mysql数据库，AMDB代理配置请参考[这里](./distributed_storage.html#id14)；type为`scalable`时，需要设置`binary_log=true`，此时状态数据和区块数据分别存储在不同的RocksDB实例中，存储区块数据的RocksDB实例根据配置项`scroll_threshold_multiple`*1000切换实例，实例以存储的起始区块高度命名。
- `max_capacity`：配置允许节点用于内存缓存的空间大小。
- `max_forward_block`：配置允许节点用于内存区块的大小，当节点出的区块超出该数值时，节点停止共识等待区块写入数据库。
- `binary_log`：当设置为`true`时打开binary_log，此时关闭RocksDB的WAL。
- `cached_storage`：控制是否使用缓存，默认`true`。

#### 数据库相关配置项

- `topic`：当type为`External`时，需要配置该字段，表示区块链系统关注的AMDB代理topic，详细请参考[这里](./distributed_storage.html#id3)。
- `max_retry`：当type为`External`时，需要配置该字段，表示写入失败时的重试次数，详细请参考[这里](./distributed_storage.html#id3)。 
- `scroll_threshold_multiple`：当type为`Scalable`时，此配置项用于配置区块数据库的切换阈值，按`scroll_threshold_multiple*1000`。默认为2，区块数据按每2000块存储在不同的RocksDB实例中。
- `db_ip`：当type为`MySQL`时，需要配置该字段，表示MySQL的IP地址。
- `db_port`：当type为`MySQL`时，需要配置该字段，表示MySQL的端口号。
- `db_username`：当type为`MySQL`时，需要配置该字段，表示MySQL的用户名。
- `db_passwd`：当type为`MySQL`时，需要配置该字段，表示MySQL用户对应的密码。
- `db_name`：当type为`MySQL`时，需要配置该字段，表示MySQL中使用的数据库名。
- `init_connections`：当type为`MySQL`时，可选配置该字段，表示与MySQL建立的初始连接数，默认15。使用默认值即可。
- `max_connections`：当type为`MySQL`时，可选配置该字段，表示与MySQL建立的最大连接数，默认20。使用默认值即可。

#### 下面是[storage]的配置示例：

```ini
[storage]
    ; storage db type, rocksdb / mysql / external, rocksdb is recommended
    type=RocksDB
    max_capacity=256
    max_forward_block=10
    ; only for external
    max_retry=100
    topic=DB
    ; only for mysql
    db_ip=127.0.0.1
    db_port=3306
    db_username=
    db_passwd=
    db_name=
```

### 交易池配置

FISCO BCOS将交易池容量配置开放给用户，用户可根据自己的业务规模需求、稳定性需求以及节点的硬件配置动态调整交易池大小。

交易池配置示例如下：

```ini
[tx_pool]
    limit=150000
```

### PBFT共识消息广播配置

PBFT共识算法为了保证共识过程最大网络容错性，每个共识节点收到有效的共识消息后，会向其他节点广播该消息，在网络较好的环境下，共识消息转发机制会造成额外的网络带宽浪费，因此在群组可变配置项中引入了`ttl`来控制消息最大转发次数，消息最大转发次数为`ttl-1`，**该配置项仅对PBFT有效**。

设置共识消息最多转发一次，配置示例如下：

```ini
; the ttl for broadcasting pbft message
[consensus]
ttl=2
```

### PBFT共识打包时间配置

考虑到PBFT模块打包太快会导致某些区块中仅打包1到2个很少的交易，浪费存储空间，FISCO BCOS v2.0.0-rc2在群组可变配置`group.group_id.ini`的`[consensus]`下引入`min_block_generation_time`配置项来控制PBFT共识打包的最短时间，即：共识节点打包时间超过`min_block_generation_time`且打包的交易数大于0才会开始共识流程，处理打包生成的新区块。

```eval_rst
.. important::
   - ``min_block_generation_time`` 默认为500ms
   - 共识节点最长打包时间为1000ms，若超过1000ms新区块中打包到的交易数仍为0，共识模块会进入出空块逻辑，空块并不落盘；
   - ``min_block_generation_time`` 不可超过出空块时间1000ms，若设置值超过1000ms，系统默认min_block_generation_time为500ms

```

```ini
[consensus]
;min block generation time(ms), the max block generation time is 1000 ms
min_block_generation_time=500
```

### PBFT交易打包动态调整

考虑到CPU负载和网络延迟对系统处理能力的影响，PBFT提供了动态调整一个区块内可打包最大交易数的算法，该算法会根据历史交易处理情况动态调整区块内可打包的最大交易数，默认开启，也可通过将可变配置`group.group_id.ini`的`[consensus].enable_dynamic_block_size`配置项修改为`false`来关闭该算法，此时区块内可打包的最大交易数为`group.group_id.genesis`的`[consensus].max_trans_num`。

关闭区块打包交易数动态调整算法的配置如下：

```ini
[consensus]
    enable_dynamic_block_size=false
```

### PBFT消息转发配置

FISCO BCOS v2.2.0优化了PBFT消息转发机制，保证网络断连场景下PBFT消息包能尽量到达每个共识节点的同时，降低网络中冗余的PBFT消息包，PBFT消息转发优化策略请参考[这里](../design/consensus/pbft_optimize.md)。可通过`group.group_id.ini`的`[consensus].enable_ttl_optimization`配置项开启或关闭PBFT消息转发优化策略。

- `[consensus].enable_ttl_optimization`配置为`true`：打开PBFT消息转发优化策略
- `[consensus].enable_ttl_optimization`配置为`false`：关闭PBFT消息转发优化策略
- `supported_version`不小于v2.2.0时，默认打开PBFT消息转发策略；`supported_version`小于v2.2.0时，默认关闭PBFT消息转发优化策略

关闭PBFT消息转发优化策略配置如下：

```ini
[consensus]
    enable_ttl_optimization=false
```

### PBFT Prepare包结构优化

考虑到PBFT算法中，Leader广播的Prepare包内区块的交易有极大概率在其他共识节点的交易池中命中，为了节省网络带宽，FISCO BCOS v2.2.0优化了Prepare包结构：Prepare包内的区块仅包含交易哈希列表，其他共识节点收到Prepare包后，优先从本地交易池获取命中的交易，并向Leader请求缺失的交易，详细设计请参考[这里](../design/consensus/pbft_optimize.md)。可通过`group.group_id.ini`的`[consensus].enable_prepare_with_txsHash`配置项开启或关闭该策略。

- `[consensus].enable_prepare_with_txsHash`配置为`true`：打开Prepare包结构优化，Prepare消息包内区块仅包含交易哈希列表
- `[consensus].enable_prepare_with_txsHash`配置为`false`：关闭Prepare包结构优化，Prepare消息包内区块包含全量的交易
- `supported_version`不小于v2.2.0时，`[consensus].enable_prepare_with_txsHash`默认为`true`；`supported_version`小于v2.2.0时，`[consensus].enable_prepare_with_txsHash`默认为`false`

关闭PBFT Prepare包结构优化配置如下：
```ini
[consensus]
    enable_prepare_with_txsHash=false
```

### 区块同步优化配置

为了增强区块链系统在网络带宽受限情况下的可扩展性，FISCO BCOS v2.2.0对区块同步进行了优化，详细的优化策略请参考[这里](../design/sync/sync_block_optimize.md)。可通过`group.group_id.ini`的`[sync].sync_block_by_tree`开启或关闭区块同步优化策略。

- `[sync].sync_block_by_tree`配置为`true`：打开区块同步优化策略
- `[sync].sync_block_by_tree`配置为`false`：关闭区块同步优化策略
-  `supported_version`不小于v2.2.0时，`[sync].sync_block_by_tree`默认为`true`；`supported_version`小于v2.2.0时，`[sync].sync_block_by_tree`默认为`false`

此外，为了保障树状拓扑区块同步的健壮性，FISCO BCOS v2.2.0还引入了gossip协议定期同步区块状态，gossip协议相关配置项均位于`group.group_id.ini`的`[sync]`中，具体如下：

```eval_rst
.. note::

    gossip协议配置项，仅在开启区块树状广播优化时生效
```
- `gossip_interval_ms`：gossip协议同步区块状态周期，默认为1000ms
- `gossip_peers_number`：节点每次同步区块状态时，随机选取的邻居节点数目，默认为3

开启区块树状广播优化配置如下：

```ini
[sync]
    sync_block_by_tree=true
    gossip_interval_ms=1000
    gossip_peers_number=3
```

### 交易树状广播优化配置

为了降低SDK直连节点的峰值出带宽，提升区块链系统可扩展性，FISCO BCOS v2.2.0引入了交易树状广播优化策略，详细设计请参考[这里](../design/sync/sync_trans_optimize.md)。可通过`group.group_id.ini`的`[sync].send_txs_by_tree`开启或关闭交易树状广播策略。

- `[sync].send_txs_by_tree`设置为`true`：打开交易树状广播策略
- `[sync].send_txs_by_tree`设置为`false`：关闭交易树状广播优化策略
- `supported_version`不小于v2.2.0时，默认打开交易树状广播优化策略；`supported_version`小于v2.2.0时，默认关闭交易树状广播策略。

关闭交易树状广播策略的配置如下：

```ini
[sync]
    send_txs_by_tree=false
```

### 并行交易配置

FISCO BCOS支持交易的并行执行。开启交易并行执行开关，能够让区块内的交易被并行的执行，提高吞吐量，**交易并行执行仅在storage state模式下生效**。

``` ini
[tx_execute]
    enable_parallel=true
```


## 动态配置系统参数

FISCO BCOS系统目前主要包括如下系统参数(未来会扩展其他系统参数)：


```eval_rst
+-----------------+-----------+---------------------------------+
| 系统参数        | 默认值    |             含义                |
+=================+===========+=================================+
| tx_count_limit  | 1000      | 一个区块中可打包的最大交易数目  |
+-----------------+-----------+---------------------------------+
| tx_gas_limit    | 300000000 | 一个交易最大gas限制             |
+-----------------+-----------+---------------------------------+

```

控制台提供 **[setSystemConfigByKey](./console.html#setsystemconfigbykey)** 命令来修改这些系统参数，**[getSystemConfigByKey](./console.html#getsystemconfigbykey)** 命令可查看系统参数的当前值：


```eval_rst
.. important::

    不建议随意修改tx_count_limit和tx_gas_limit，如下情况可修改这些参数：

    - 机器网络或CPU等硬件性能有限：调小tx_count_limit，或降低业务压力；
    - 业务逻辑太复杂，执行交易时gas不足：调大tx_gas_limit。
```

```bash
# 设置一个区块可打包最大交易数为500
[group:1]> setSystemConfigByKey tx_count_limit 500
# 查询tx_count_limit
[group:1]> getSystemConfigByKey tx_count_limit
[500]

# 设置交易gas限制为400000000
[group:1]> getSystemConfigByKey tx_gas_limit 400000000
[group:1]> getSystemConfigByKey
[400000000]
```