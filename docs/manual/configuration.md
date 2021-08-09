# 节点配置

标签：``节点配置`` ``开发手册`` ``config.ini`` ``端口配置`` ``日志级别`` ``配置``

----
FISCO BCOS支持多账本，每条链包括多个独立账本，账本间数据相互隔离，群组间交易处理相互隔离，每个节点包括一个主配置`config.ini`和多个账本配置`group.group_id.genesis`、`group.group_id.ini`。

- `config.ini`：主配置文件，主要配置RPC、P2P、SSL证书、账本配置文件路径、兼容性等信息。
- `group.group_id.genesis`：群组配置文件，群组内所有节点一致，节点启动后，不可手动更改该配置。主要包括群组共识算法、存储类型、最大gas限制等配置项。
- `group.group_id.ini`：群组可变配置文件，包括交易池大小等，配置后重启节点生效。

## 主配置文件config.ini

`config.ini`采用`ini`格式，主要包括 **rpc、p2p、group、network_security和log** 配置项。

```eval_rst
.. important::
    - 云主机的公网IP均为虚拟IP，若listen_ip/jsonrpc_listen_ip/channel_listen_ip填写外网IP，会绑定失败，须填写0.0.0.0
    - RPC/P2P/Channel监听端口必须位于1024-65535范围内，且不能与机器上其他应用监听端口冲突
    - 为便于开发和体验，listen_ip/channel_listen_ip参考配置是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如：内网IP或特定的外网IP
```

### 配置RPC

- `channel_listen_ip`: Channel监听IP，为方便节点和SDK跨机器部署，默认设置为`0.0.0.0`；

- `jsonrpc_listen_ip`：RPC监听IP，安全考虑，默认设置为127.0.0.1，若有外网访问需求，请监听**节点外网IP**或`0.0.0.0`；

- `channel_listen_port`: Channel端口，对应到[Java SDK](../sdk/java_sdk.html#id2)配置中的`channel_listen_port`；

- `jsonrpc_listen_port`: JSON-RPC端口。

```eval_rst
.. note::
    出于安全性和易用性考虑，v2.3.0版本最新配置将listen_ip拆分成jsonrpc_listen_ip和channel_listen_ip，但仍保留对listen_ip的解析功能：

     - 配置中仅包含listen_ip：RPC和Channel的监听IP均为配置的listen_ip
     - 配置中同时包含listen_ip、channel_listen_ip或jsonrpc_listen_ip：优先解析channel_listen_ip和jsonrpc_listen_ip，没有配置的配置项用listen_ip的值替代
     - v2.6.0 版本开始，RPC 支持 ipv4 和 ipv6
```

RPC配置示例如下：

```ini
# ipv4
[rpc]
    channel_listen_ip=0.0.0.0
    jsonrpc_listen_ip=127.0.0.1
    channel_listen_port=30301
    jsonrpc_listen_port=30302

# ipv6
[rpc]
    channel_listen_ip=::1
    jsonrpc_listen_ip=::1
    channel_listen_port=30301
    jsonrpc_listen_port=30302
```

### 配置P2P

当前版本FISCO BCOS必须在`config.ini`配置中配置连接节点的`IP`和`Port`，P2P相关配置包括：

```eval_rst
.. note::
    - 为便于开发和体验，listen_ip参考配置是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如：内网IP或特定的外网IP
    - v2.6.0 版本开始，P2P 支持 ipv4 和 ipv6
```

- `listen_ip`：P2P监听IP，默认设置为`0.0.0.0`。
- `listen_port`：节点P2P监听端口。
- `node.*`: 节点需连接的所有节点`IP:Port`或`DomainName:Port`。该选项支持域名，但建议需要使用的用户[手动**编译源码**](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html#id2)。
- `enable_compress`：开启网络压缩的配置选项，配置为true，表明开启网络压缩功能，配置为false，表明关闭网络压缩功能，网络压缩详细介绍请参考[这里](../design/features/network_compress.md)。

- v2.6.0 版本开始，P2P 支持 ipv4 和 ipv6

P2P配置示例如下：

```ini
# ipv4
[p2p]
    listen_ip=0.0.0.0
    listen_port=30300
    node.0=127.0.0.1:30300
    node.1=127.0.0.1:30304
    node.2=127.0.0.1:30308
    node.3=127.0.0.1:30312

# ipv6
[p2p]
    listen_ip=::1
    listen_port=30300
    node.0=[::1]:30300
    node.1=[::1]:30304
    node.2=[::1]:30308
    node.3=[::1]:30312
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
- `check_cert_issuer`：设置SDK是否只能连本机构节点，默认为开启（check_cert_issuer=true）。

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

FISCO BCOS支持功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，日志配置主要位于`config.ini`的`[log]`配置项中。

#### 日志通用配置

FISCO BCOS通用日志配置项如下：

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

#### 统计日志配置

考虑到实时监控系统资源使用情况在实际生产系统中非常重要，FISCO BCOS v2.4.0引入了统计日志，统计日志配置项位于`config.ini`中。

##### 配置统计日志开关

考虑到并非所有场景都需要网络流量和Gas统计功能，FISCO BCOS在`config.ini`中提供了`enable_statistic`选项来开启和关闭该功能，默认关闭该功能。

- `log.enable_statistic`配置成true，开启网络流量和Gas统计功能
- `log.enable_statistic`配置成false，关闭网络流量和Gas统计功能

配置示例如下：

```ini
[log]
    ; enable/disable the statistics function
    enable_statistic=false
```

##### 配置网络统计日志输出间隔

由于网络统计日志周期性输出，引入了`log.stat_flush_interval`来控制统计间隔和日志输出频率，单位是秒，默认为60s，配置示例如下：

```ini
[log]
    ; network statistics interval, unit is second, default is 60s
    stat_flush_interval=60
```

### 配置链属性

可通过`config.ini`中的`[chain]`配置节点的链属性。此配置项建链时工具会自动生成，用户不需修改。

- `id`，链ID，默认为1；
- `sm_crypto`，2.5.0版本以后，节点支持以国密模式或非国密模式启动，`true`表示节点使用国密模式，`false`表示节点使用非国密模式，默认为`false`；
- `sm_crypto_channel`，2.5.0版本以后，节点支持与SDK连接使用国密SSL，此选项用于配置是否使用国密SSL与SDK连接，默认为false。

### 配置节点兼容性

FISCO BCOS 2.0+所有版本向前兼容，可通过`config.ini`中的`[compatibility]`配置节点的兼容性，此配置项建链时工具会自动生成，用户不需修改。

- `supported_version`：当前节点运行的版本

```eval_rst
.. important::
    - 可通过 `./fisco-bcos --version | grep "Version" ` 命令查看FISCO BCOS的当前支持的最高版本
    - build_chain.sh生成的区块链节点配置中，supported_version配置为FISCO BCOS当前的最高版本
    - 旧节点升级为新节点时，直接将旧的FISCO BCOS二进制替换为最新FISCO BCOS二进制即可，千万不可修改supported_version
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
key_manager_port=8150
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```


### 可选配置：流量控制

为实现区块链系统柔性服务，防止多群组间资源相互影响，FISCO BCOS v2.5.0引入了流量控制功能，主要包括SDK到节点请求速率的限制以及节点间流量限制，配置项位于`config.ini`的`[flow_control]`，默认关闭，流控的详细设计请参考[这里](../design/features/flow_control.md)。

#### SDK请求速率限制配置

SDK请求速率限制位于配置项`[flow_control].limit_req`中，用于限制SDK每秒到节点的最大请求数目，当每秒到节点的请求超过配置项的值时，请求会被拒绝，**SDK请求速率限制默认关闭，若要开启该功能，需要将`limit_req`配置项前面的`;`去掉**，打开SDK请求速率限制并设计节点每秒可接受2000个SDK请求的示例如下：

```ini
[flow_control]
    ; restrict QPS of the node
    limit_req=2000
```

#### 节点间流量限制配置

为了防止区块同步、AMOP消息传输占用过多的网络流量，并影响共识模块的消息包传输，FISCO BCOS v2.5.0引入了节点间流量限制的功能，该配置项用于配置节点平均出带宽的上限，但不限制区块共识、交易同步的流量，当节点平均出带宽超过配置值时，会暂缓区块发送、AMOP消息传输。

- `[flow_control].outgoing_bandwidth_limit`：节点出带宽限制，单位为`Mbit/s`，当节点出带宽超过该值时，会暂缓区块发送，也会拒绝客户端发送的[AMOP](../manual/amop_protocol.md)请求，但不会限制区块共识和交易广播的流量，**该配置项默认关闭，若要打开流量限制功能，请将`outgoing_bandwidth_limit`配置项前面的`;`去掉**。

打开节点出带宽流量限制，并将其设置为`5MBit/s`的配置示例如下：

```ini
[flow_control]
    ; Mb, can be a decimal
    ; when the outgoing bandwidth exceeds the limit, the block synchronization operation will not proceed
    outgoing_bandwidth_limit=5
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
    - 由于genesis配置要求群组内所有节点一致，建议使用 `开发部署工具 build_chain <../manual/build_chain.html>`_ 生成该配置
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

- `consensus_type`：共识算法类型，目前支持[PBFT](../design/consensus/pbft.md)，[Raft](../design/consensus/raft.md)和[rPBFT](../design/consensus/rpbft.md)，默认使用PBFT共识算法；

- `max_trans_num`：一个区块可打包的最大交易数，默认是1000，链初始化后，可通过[控制台](../console/console.html#setsystemconfigbykey)动态调整该参数；

- `consensus_timeout`：PBFT共识过程中，每个区块执行的超时时间，默认为3s，单位为秒，可通过[控制台](../console/console.html#setsystemconfigbykey)动态调整该参数；

- `node.idx`：共识节点列表，配置了参与共识节点的[Node ID](../design/consensus/pbft.html#id1)，节点的Node ID可通过`${data_path}/node.nodeid`文件获取(其中`${data_path}`可通过主配置`config.ini`的`[network_security].data_path`配置项获取)

FISCO BCOS v2.3.0引入了rPBFT共识算法，具体可参考[这里](../design/consensus/rpbft.md)，rPBFT相关配置如下：

- `epoch_sealer_num`：一个共识周期内选择参与共识的节点数目，默认是所有共识节点总数，链初始化后可通过[控制台](../console/console.html#setsystemconfigbykey)动态调整该参数；
- `epoch_block_num`：一个共识周期出块数目，默认为1000，可通过[控制台](../console/console.html#setsystemconfigbykey)动态调整该参数；

```eval_rst
.. note::

    rPBFT配置对其他共识算法不生效。
```

配置节点使用PBFT共识算法如下：

```ini
; 共识协议配置
[consensus]
    ; 共识算法，目前支持PBFT(consensus_type=pbft), Raft(consensus_type=raft)和rPBFT(consensus_type=rpbft)
    consensus_type=pbft
    ; 单个块最大交易数
    max_trans_num=1000
    ;共识过程中区块最长执行时间，默认为3秒
    consensus_timeout=3
    ; 一个共识周期内选取参与共识的节点数，rPBFT配置项，对其他共识算法不生效
    epoch_sealer_num=4
    ; 一个共识周期出块数，rPBFT配置项，对其他共识算法不生效
    epoch_block_num=1000
    ; leader节点的ID列表
    node.0=123d24a998b54b31f7602972b83d899b5176add03369395e53a5f60c303acb719ec0718ef1ed51feb7e9cf4836f266553df44a1cae5651bc6ddf50e01789233a
    node.1=70ee8e4bf85eccda9529a8daf5689410ff771ec72fc4322c431d67689efbd6fbd474cb7dc7435f63fa592b98f22b13b2ad3fb416d136878369eb413494db8776
    node.2=7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
    node.3=fd6e0bfe509078e273c0b3e23639374f0552b512c2bea1b2d3743012b7fed8a9dec7b47c57090fa6dcc5341922c32b89611eb9d967dba5f5d07be74a5aed2b4a
```

配置节点开启rPBFT共识算法如下：
```ini
; 共识协议配置
[consensus]
    ; 共识算法，目前支持PBFT(consensus_type=pbft), Raft(consensus_type=raft)和rPBFT(consensus_type=rpbft)
    consensus_type=rpbft
    ; 单个块最大交易数
    max_trans_num=1000
    ; 一个共识周期内选取参与共识的节点数，rPBFT配置项，对其他共识算法不生效
    epoch_sealer_num=4
    ; 一个共识周期出块数，rPBFT配置项，对其他共识算法不生效
    epoch_block_num=1000
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
   推荐使用 **storage state**
```

```ini
[state]
    type=storage
```

### gas配置

FISCO BCOS兼容以太坊虚拟机([EVM](../design/virtual_machine/evm.md))，为了防止针对[EVM](../design/virtual_machine/evm.md)的DOS攻击，EVM在执行交易时，引入了gas概念，用来度量智能合约执行过程中消耗的计算和存储资源，包括交易最大gas限制和区块最大gas限制，若交易或区块执行消耗的gas超过限制(gas limit)，则丢弃交易或区块。FISCO BCOS是联盟链，简化了gas设计，**仅保留交易最大gas限制，区块最大gas通过[共识配置的max_trans_num](../manual/configuration.html#id11)和交易最大gas限制一起约束**。FISCO BCOS通过genesis的`[tx].gas_limit`来配置交易最大gas限制，默认是300000000，链初始化完毕后，可通过[控制台指令](../console/console.html#setsystemconfigbykey)动态调整gas限制。

```ini
[tx]
    gas_limit=300000000
```

### EVM配置

FISCO BCOS v2.4.0引入`Free Storage` Gas衡量模式，提升CPU和内存在Gas消耗中的占比，详细可参考[这里](../design/virtual_machine/gas.html#evm-gas)。`Free Storage` Gas模式的开启和关闭通过`genesis`文件的`evm.enable_free_storage`配置项控制。

```eval_rst
.. note::
    - ``evm.enable_free_storage`` v2.4.0开始支持，当 ``supported_version`` 小于v2.4.0，或者旧链直接替换二进制升级时，不支持该特性
    - 链初始化时，``evm.enable_free_storage`` 写入创世块中；链初始化后，节点从创世块中读取 ``evm.enable_free_storage`` 配置项，手动修改 ``genesis`` 配置项不会生效
    - ``evm.enable_free_storage`` 默认设置为false
```

- `evm.enable_free_storage`设置为true：开启`Free Storage` Gas模式
- `evm.enable_free_storage`设置为false：关闭`Free Storage` Gas模式


配置示例如下：

```ini
[evm]
    enable_free_storage=false
```


## 账本可变配置说明

账本可变配置位于节点`conf`目录下`.ini`后缀的文件中。

如：`group1`可变配置一般命名为`group.1.ini`，可变配置主要包括交易池大小、PBFT共识消息转发的TTL、PBFT共识打包时间设置、PBFT交易打包动态调整设置、并行交易设置等。

### 配置storage

存储目前支持RocksDB、MySQL、External三种模式，用户可以根据需要选择使用的DB，其中RocksDB性能最高；MySQL支持用户使用MySQL数据库，方便数据的查看；External通过数据代理访问MySQL，用户需要在启动并配置数据代理。设计文档参考[AMDB存储设计](../design/storage/storage.html)。RC3版本起我们使用RocksDB替代LevelDB以获得更好的性能表现，仍支持旧版本LevelDB。

```eval_rst
.. note::
    - v2.3.0版本开始，为便于链的维护，推荐使用 `MySQL` 存储模式替代 `External` 存储模式
```

#### 公共配置项

```eval_rst
.. important::
   推荐使用Mysql直连模式，配置type为MySQL。
```

- `type`：存储的DB类型，支持`RocksDB`、`MySQL`和`Scalable`，不区分大小写。DB类型为RocksDB时，区块链系统所有数据存储于RocksDB本地数据库中；type为`MySQL`时，节点根据配置访问MySQL数据库；type为`Scalable`时，需要设置`binary_log=true`，此时状态数据和区块数据分别存储在不同的RocksDB实例中，存储区块数据的RocksDB实例根据配置项`scroll_threshold_multiple`\*1000切换实例，实例以存储的起始区块高度命名。
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
    ; storage db type, RocksDB / MySQL / Scalable, RocksDB is recommended
    type=RocksDB
    max_capacity=256
    max_forward_block=10
    ; only for external
    max_retry=100
    topic=DB
    ; only for MySQL
    db_ip=127.0.0.1
    db_port=3306
    db_username=
    db_passwd=
    db_name=
```

### 交易池配置

FISCO BCOS将交易池容量配置开放给用户，用户可根据自己的业务规模需求、稳定性需求以及节点的硬件配置动态调整交易池配置。

#### 交易池容量限制

为防止过多交易堆积在交易池内占用太多内存，FISCO BCOS提供了`[tx_pool].limit`和`[tx_pool].memory_limit`两个配置项来限制交易池容量：

- `[tx_pool].limit`: 限制交易池内可以容纳的最大交易数目，默认为`150000`，超过该限制后，客户端发到节点的交易会被拒绝。
- `[tx_pool].memory_limit`: 交易池内交易占用的内存大小限制，默认为`512MB`，超过该限制后，客户端发到节点的交易会被拒绝。

交易池容量配置如下：

```ini
[tx_pool]
    limit=150000
    ; transaction pool memory size limit, MB
    memory_limit=512
```

#### 交易池推送线程数配置


为提升区块链系统性能，FISCO BCOS采用了交易回执异步推送逻辑，当交易上链后，交易池内的推送线程会把交易上链的回执异步推送给客户端，为防止推送线程过多占用较多的系统资源，也为了防止推送线程过少影响交易推送的时效性，FISCO BCOS提供了`[tx_pool].notify_worker_num`配置项来配置异步推送线程数目：

- `[tx_pool].notify_worker_num`：异步推送线程数目，默认为2，建议该值不超过8

交易池推送线程数配置如下：

```ini
[tx_pool]
    ; number of threads responsible for transaction notification,
    ; default is 2, not recommended for more than 8
    notify_worker_num=2
```

### PBFT共识配置

为提升PBFT算法的性能、可用性、网络效率，FISCO BCOS针对区块打包算法和网络做了一系列优化，包括PBFT区块打包动态调整策略、PBFT消息转发优化、PBFT Prepare包结构优化等。

```eval_rst
.. note::

    因协议和算法一致性要求，建议保证所有节点PBFT共识配置一致。
```

#### PBFT共识消息转发配置

PBFT共识算法为了保证共识过程最大网络容错性，每个共识节点收到有效的共识消息后，会向其他节点广播该消息，在网络较好的环境下，共识消息转发机制会造成额外的网络带宽浪费，因此在群组可变配置项中引入了`ttl`来控制消息最大转发次数，消息最大转发次数为`ttl-1`，**该配置项仅对PBFT有效**。

设置共识消息最多转发一次，配置示例如下：

```ini
; the ttl for broadcasting pbft message
[consensus]
ttl=2
```

#### PBFT共识打包时间配置

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

#### PBFT交易打包动态调整

考虑到CPU负载和网络延迟对系统处理能力的影响，PBFT提供了动态调整一个区块内可打包最大交易数的算法，该算法会根据历史交易处理情况动态调整区块内可打包的最大交易数，默认开启，也可通过将可变配置`group.group_id.ini`的`[consensus].enable_dynamic_block_size`配置项修改为`false`来关闭该算法，此时区块内可打包的最大交易数为`group.group_id.genesis`的`[consensus].max_trans_num`。

关闭区块打包交易数动态调整算法的配置如下：

```ini
[consensus]
    enable_dynamic_block_size=false
```

#### PBFT消息转发配置

FISCO BCOS v2.2.0优化了PBFT消息转发机制，保证网络断连场景下PBFT消息包能尽量到达每个共识节点的同时，降低网络中冗余的PBFT消息包，PBFT消息转发优化策略请参考[这里](../design/consensus/pbft_optimize.md)。可通过`group.group_id.ini`的`[consensus].enable_ttl_optimization`配置项开启或关闭PBFT消息转发优化策略。

- `[consensus].enable_ttl_optimization`配置为`true`：打开PBFT消息转发优化策略
- `[consensus].enable_ttl_optimization`配置为`false`：关闭PBFT消息转发优化策略
- `supported_version`不小于v2.2.0时，默认打开PBFT消息转发策略；`supported_version`小于v2.2.0时，默认关闭PBFT消息转发优化策略

关闭PBFT消息转发优化策略配置如下：

```ini
[consensus]
    enable_ttl_optimization=false
```

#### PBFT Prepare包结构优化

考虑到PBFT算法中，Leader广播的Prepare包内区块的交易有极大概率在其他共识节点的交易池中命中，为了节省网络带宽，FISCO BCOS v2.2.0优化了Prepare包结构：Prepare包内的区块仅包含交易哈希列表，其他共识节点收到Prepare包后，优先从本地交易池获取命中的交易，并向Leader请求缺失的交易，详细设计请参考[这里](../design/consensus/pbft_optimize.md)。可通过`group.group_id.ini`的`[consensus].enable_prepare_with_txsHash`配置项开启或关闭该策略。

- `[consensus].enable_prepare_with_txsHash`配置为`true`：打开Prepare包结构优化，Prepare消息包内区块仅包含交易哈希列表
- `[consensus].enable_prepare_with_txsHash`配置为`false`：关闭Prepare包结构优化，Prepare消息包内区块包含全量的交易
- `supported_version`不小于v2.2.0时，`[consensus].enable_prepare_with_txsHash`默认为`true`；`supported_version`小于v2.2.0时，`[consensus].enable_prepare_with_txsHash`默认为`false`

```eval_rst
.. note::

    因协议一致性要求，须保证所有节点 `enable_prepare_with_txsHash` 配置一致
```

关闭PBFT Prepare包结构优化配置如下：
```ini
[consensus]
    enable_prepare_with_txsHash=false
```

### rPBFT共识配置

FISCO BCOS v2.3.0引入rPBFT共识算法，具体可参考[这里](../design/consensus/rpbft.md)，为保证rPBFT算法网络流量负载均衡，引入了Prepare包树状广播策略以及该策略相对应的容错方案。

- `[consensus].broadcast_prepare_by_tree`：Prepare包树状广播策略开启/关闭开关，设置为`true`，开启Prepare包树状广播策略；设置为`false`，关闭Prepare包树状广播策略，默认为`true`

下面为开启Prepare包树状广播策略后的容错配置：
- `[consensus].prepare_status_broadcast_percent`：Prepare状态包随机广播的节点占共识节点总数的百分比，取值在25到100之间，默认为33
- `[consensus].max_request_prepare_waitTime`：节点Prepare缓存缺失时，等待父节点发送Prepare包的最长时延，默认为100ms，超过这个时延后，节点会向其他拥有该Prepare包的节点请求


下面为rPBFT模式下开启[Prepare包结构优化](../manual/configuration.html#pbft-prepare)后，负载均衡相关配置：

- `[consensus].max_request_missedTxs_waitTime`：节点Prepare包内交易缺失后，等待父节点或其他非leader节点同步Prepare包状态的最长时延，默认为100ms，若在等待时延窗口内同步到父节点或非leader节点Prepare包状态，则会随机选取一个节点请求缺失交易；若等待超时，直接向leader请求缺失交易。


rPBFT默认配置如下:
```ini
; 默认开启Prepare包树状广播策略
broadcast_prepare_by_tree=true
; 仅在开启prepare包树状广播时生效
; 每个节点随机选取33%共识节点同步prepare包状态
prepare_status_broadcast_percent=33
; prepare包树状广播策略下，缺失prepare包的节点超过100ms没等到父节点转发的prepare包，会向其他节点请求缺失的prepare包
max_request_prepare_waitTime=100
; 节点等待父节点或其他非leader节点同步prepare包最长时延为100ms
max_request_missedTxs_waitTime=100
```

### 同步配置

同步模块是"网络消耗大户"，包括区块同步和交易同步，FISCO BCOS秉着负载均衡的原则优化了共识模块网络使用效率。

```eval_rst
.. note::

    因协议一致性要求，建议保证所有节点PBFT共识配置一致。
```

#### 区块同步优化配置

为了增强区块链系统在网络带宽受限情况下的可扩展性，FISCO BCOS v2.2.0对区块同步进行了优化，详细的优化策略请参考[这里](../design/sync/sync_block_optimize.md)。可通过`group.group_id.ini`的`[sync].sync_block_by_tree`开启或关闭区块同步优化策略。

- `[sync].sync_block_by_tree`配置为`true`：打开区块同步优化策略
- `[sync].sync_block_by_tree`配置为`false`：关闭区块同步优化策略
-  `supported_version`不小于v2.2.0时，`[sync].sync_block_by_tree`默认为`true`；`supported_version`小于v2.2.0时，`[sync].sync_block_by_tree`默认为`false`

此外，为了保障树状拓扑区块同步的健壮性，FISCO BCOS v2.2.0还引入了gossip协议定期同步区块状态，gossip协议相关配置项均位于`group.group_id.ini`的`[sync]`中，具体如下：
- `gossip_interval_ms`：gossip协议同步区块状态周期，默认为1000ms
- `gossip_peers_number`：节点每次同步区块状态时，随机选取的邻居节点数目，默认为3

```eval_rst
.. note::

    1. gossip协议配置项，仅在开启区块树状广播优化时生效
    2. 必须保证所有节点 `sync_block_by_tree` 配置一致
```

开启区块树状广播优化配置如下：

```ini
[sync]
    ; 默认开启区块树状同步策略
    sync_block_by_tree=true
    ; 每个节点每隔1000ms同步一次最新区块状态
    gossip_interval_ms=1000
    ; 每个节点每次随机选择3个邻居节点同步最新区块状态
    gossip_peers_number=3
```

#### 交易树状广播优化配置

为了降低SDK直连节点的峰值出带宽，提升区块链系统可扩展性，FISCO BCOS v2.2.0引入了交易树状广播优化策略，详细设计请参考[这里](../design/sync/sync_trans_optimize.md)。可通过`group.group_id.ini`的`[sync].send_txs_by_tree`开启或关闭交易树状广播策略，详细配置如下：

- `[sync].sync_block_by_tree`：设置为`true`，打开交易树状广播策略；设置为`false`，关闭交易树状广播优化策略

关闭交易树状广播策略的配置如下：

```ini
[sync]
    ; 默认开启交易树状广播策略
    send_txs_by_tree=false
```

```eval_rst
.. note::
    - 由于协议一致性需求，须保证所有节点交易树状广播开关`send_txs_by_tree`配置一致
    -  `supported_version` 不小于v2.2.0时，默认打开交易树状广播优化策略； `supported_version` 小于v2.2.0时，默认关闭交易树状广播策略
```

#### 交易转发优化配置

为降低交易转发导致的流量开销，FISCO BCOS v2.2.0引入基于状态包的交易转发策略，具体设计可参考[这里](../design/sync/sync_trans_optimize.md)。可通过`group.group_id.ini`的`[sync].txs_max_gossip_peers_num`配置交易状态最多转发节点数目，默认为5。

```eval_rst
.. note::
    为保障交易到达每个节点的同时，尽量降低交易状态转发引入的流量开销，不建议将 `txs_max_gossip_peers_num` 设置太小或太大，直接使用默认配置即可
```

交易状态转发最大节点数配置如下：

```ini
[sync]
    ; 每个节点每轮最多随机选择5个邻居节点同步最新交易状态
    txs_max_gossip_peers_num=5
```

### 并行交易配置

FISCO BCOS支持交易的并行执行。开启交易并行执行开关，能够让区块内的交易被并行的执行，提高吞吐量，**交易并行执行仅在storage state模式下生效**。

```eval_rst
.. note::
    为简化系统配置，v2.3.0去除了 `enable_parallel` 配置项，该配置项仅在 `supported_version < v2.3.0` 时生效，v2.3.0版本中：
     - storageState模式：开启并行交易
     - mptState模式: 关闭并行交易
```

``` ini
[tx_execute]
    enable_parallel=true
```

### 可选配置：群组流量控制

为了防止多群组间资源相互影响，FISCO BCOS v2.5.0引入了流量控制功能，支持群组级别的SDK请求速率限制以及流量限制，配置位于`group.{group_id}.ini`的`[flow_control]`，默认关闭，流控的详细设计请参考[这里](../design/features/flow_control.md)。

#### SDK到群组的请求速率限制配置

群组内的SDK请求速率限制位于配置项`[flow_control].limit_req`中，用于限制SDK每秒到群组的最大请求数目，当每秒到节点的请求超过配置项的值时，请求会被拒绝，**SDK到群组请求速率限制默认关闭，若要开启该功能，需要将`limit_req`配置项前面的`;`去掉**，打开SDK请求速率限制并配置群组每秒可接受1000个SDK请求的示例如下：

```ini
[flow_control]
    ; restrict QPS of the group
    limit_req=1000
```

#### 群组间流量限制配置

为了防止区块同步占用过多的网络流量影响到共识模块的消息包传输，FISCO BCOS v2.5.0引入了群组级流量限制的功能，其配置了群组平均出带宽的上限，但不限制区块共识、交易同步的流量，当群组平均出带宽超过配置值时，会暂缓区块发送。

- `[flow_control].outgoing_bandwidth_limit`：群组出带宽限制，单位为`Mbit/s`，当群组出带宽超过该值时，会暂缓发送区块，但不会限制区块共识和交易广播的流量，**该配置项默认关闭，若要打开流量限制功能，请将`outgoing_bandwidth_limit`配置项前面的`;`去掉**。

打开群组出带宽流量限制，并将其设置为`2MBit/s`的配置示例如下：

```ini
[flow_control]
    ; Mb, can be a decimal
    ; when the outgoing bandwidth exceeds the limit, the block synchronization operation will not proceed
    outgoing_bandwidth_limit=2
```

### 可选配置：SDK白名单配置

为了实现sdk到群组的访问控制，FISCO BCOS v2.6.0引入了群组级的SDK白名单访问控制机制，配置位于`group.{group_id}.ini`的`[sdk_allowlist]`，默认关闭，群组级别SDK白名单机制请参考[这里](./sdk_allowlist.md)。

```eval_rst
.. important::
    FISCO BCOS v2.6.0默认关闭SDK到群组的白名单访问控制功能，即默认情况下sdk与所有群组均可通信，若要开启sdk与群组间基于白名单的访问控制功能，需要将 `;public_key.0` 等配置项前面的分号去掉
```
- `public_key.0`、`public_key.1`、...、`public_key.i`：配置允许与该群组进行通信的SDK公钥公钥列表。

**SDK白名单配置示例如下：**

```ini
[sdk_allowlist]
; When sdk_allowlist is empty, all SDKs can connect to this node
; when sdk_allowlist is not empty, only the SDK in the allowlist can connect to this node
; public_key.0 should be nodeid, nodeid's length is 128
public_key.0=b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f3
```

## 动态配置系统参数

FISCO BCOS系统目前主要包括如下系统参数(未来会扩展其他系统参数)：

|  系统参数    | 默认值  | 含义 |
|  ----  | ----  | ---- |
| tx_count_limit  | 1000 | 一个区块中可打包的最大交易数目 |
| tx_gas_limit  | 300000000 | 一个交易最大gas限制 |
| rpbft_epoch_sealer_num | 链共识节点总数 | rPBFT系统配置，一个共识周期内选取参与共识的节点数目，rPBFT每个共识周期都会动态切换参与共识的节点数目 |
| rpbft_epoch_block_num | 1000 | rPBFT系统配置，一个共识周期内出块数目|
| consensus_timeout | 3 | PBFT共识过程中，区块执行的超时时间，最少为3s, supported_version>=v2.6.0时，配置项生效 |


控制台提供 **[setSystemConfigByKey](../console/console.html#setsystemconfigbykey)** 命令来修改这些系统参数，**[getSystemConfigByKey](../console/console.html#getsystemconfigbykey)** 命令可查看系统参数的当前值：


```eval_rst
.. important::

    不建议随意修改tx_count_limit和tx_gas_limit，如下情况可修改这些参数：

    - 机器网络或CPU等硬件性能有限：调小tx_count_limit，或降低业务压力；
    - 业务逻辑太复杂，执行交易时gas不足：调大tx_gas_limit。

    `rpbft_epoch_sealer_num` 和 `rpbft_epoch_block_num` 仅对rPBFT共识算法生效，为了保障共识性能，不建议频繁动态切换共识列表，即不建议 `rpbft_epoch_block_num` 配置值太小
```

```bash
# 设置一个区块可打包最大交易数为500
[group:1]> setSystemConfigByKey tx_count_limit 500
# 查询tx_count_limit
[group:1]> getSystemConfigByKey tx_count_limit
[500]

# 设置交易gas限制为400000000
[group:1]> setSystemConfigByKey tx_gas_limit 400000000
[group:1]> getSystemConfigByKey tx_gas_limit
[400000000]

# rPBFT共识算法下，设置一个共识周期选取参与共识的节点数目为4
[group:1]> setSystemConfigByKey rpbft_epoch_sealer_num 4
Note: rpbft_epoch_sealer_num only takes effect when rPBFT is used
{
    "code":0,
    "msg":"success"
}
# 查询rpbft_epoch_sealer_num
[group:1]> getSystemConfigByKey rpbft_epoch_sealer_num
Note: rpbft_epoch_sealer_num only takes effect when rPBFT is used
4

# rPBFT共识算法下，设置一个共识周期出块数目为10000
[group:1]> setSystemConfigByKey rpbft_epoch_block_num 10000
Note: rpbft_epoch_block_num only takes effect when rPBFT is used
{
    "code":0,
    "msg":"success"
}
# 查询rpbft_epoch_block_num
[group:1]> getSystemConfigByKey rpbft_epoch_block_num
Note: rpbft_epoch_block_num only takes effect when rPBFT is used
10000
# 获取区块执行超时时间
[group:1]> getSystemConfigByKey consensus_timeout
3

# 设置区块执行超时时间为5s
[group:1]> setSystemConfigByKey consensus_timeout 5
{
    "code":0,
    "msg":"success"
}
```
