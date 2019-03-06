# 配置文件与配置项

FISCO BCOS支持多账本，每条链包括多个独立账本，账本间数据相互隔离，群组间交易处理相互隔离，每个节点包括一个主配置`config.ini`和多个账本配置`group.group_id.genesis`、`group.group_id.ini`。

- `config.ini`：主配置文件，主要配置RPC、P2P、SSL证书、账本配置文件路径等信息。
- `group.group_id.genesis`：群组配置文件，群组内所有节点一致，节点启动后，不可手动更改该配置。主要包括群组共识算法、存储类型、最大gas限制等配置项。
- `group.group_id.ini`：群组可变配置文件，包括交易池大小等，配置后重启节点生效。

## 硬件要求

```eval_rst
.. note::
    由于节点多群组共享网络带宽、CPU和内存资源，因此为了保证服务的稳定性，一台机器上不推荐配置过多节点。
```

下表是单群组单节点推荐的配置，节点耗费资源与群组个数呈线性关系，您可根据实际的业务需求和机器资源，合理地配置节点数目。

```eval_rst
+----------+---------+---------------------------------------------+
| 配置     | 最低配置| 推荐配置                                    |
+==========+=========+=============================================+
| CPU      | 1.5GHz  | 2.4GHz                                      |
+----------+---------+---------------------------------------------+
| 内存     | 1GB     | 8GB                                         |
+----------+---------+---------------------------------------------+
| 核心     | 1核     | 4核                                         |
+----------+---------+---------------------------------------------+
| 带宽     | 1Mb     | 10Mb                                        |
+----------+---------+---------------------------------------------+
```

## 主配置文件config.ini

`config.ini`采用`ini`格式，主要包括 **rpc、p2p、group、secure和log** 配置项。


```eval_rst
.. important::
    - 云主机的公网IP均为虚拟IP，若listen_ip填写外网IP，会绑定失败，须填写0.0.0.0
    - RPC/P2P/Channel监听端口必须位于1024-65535范围内，且不能与机器上其他应用监听端口冲突
```

### 配置RPC

- `listen_ip`: 安全考虑，建链脚本默认监听127.0.0.1，如果需要外网访问RPC或外网使用SDK请监听**节点的外网IP**或`0.0.0.0`；
- `channel_listen_port`: Channel端口，对应到[Web3SDK](../sdk/config.html#id1)配置中的`channel_listen_port`；
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
- `node.*`: 节点需连接的所有节点`IP:port`。

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

基于安全考虑，FISCO BCOS节点间采用SSL加密通信，`[secure]`配置SSL连接的证书信息：

- `data_path`：证书和私钥文件所在目录。
- `key`: 节点私钥相对于`data_path`的路径。
- `cert`: 证书`node.crt`相对于`data_path`的路径。
- `ca_cert`: ca证书文件路径。
- `ca_path`: ca证书文件夹，多ca时需要。

```ini
[secure]
    data_path=conf/
    key=node.key
    cert=node.crt
    ca_cert=ca.crt
    ca_path=
```

### 配置黑名单列表

基于防作恶考虑，FISCO BCOS允许节点配置不受信任的节点黑名单列表，拒绝与这些黑名单节点建立连接，通过`[crl]`配置：

> `crl.idx`: 黑名单节点的Node ID, 节点Node ID可通过`node.nodeid`文件获取; `idx`是黑名单节点的索引。

黑名单的详细信息还可参考[CA黑名单](./certificate_blacklist.md)

黑名单列表配置示例如下：

```ini
; 证书黑名单
[crl]
    crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e
3787c338a4
```

### 配置日志信息

FISCO BCOS支持轻量级的[easylogging++](https://github.com/zuhd-org/easyloggingpp)，也支持功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，可通过编译开关配置使用这两种日志，FISCO BCOS默认使用boostlog，详细可参考[日志操作手册](log_access.md)。

- `log_path`:日志文件路径。
- `level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `max_log_file_size`：每个日志文件最大容量，**计量单位为字节，默认为200MB**。
- `flush`：boostlog默认开启日志自动刷新，若需提升系统性能，建议将该值设置为false。

boostlog示例配置如下：

```ini
[log]
    log_path=./log
    level=info
    ; 每个日志文件最大容量，默认为200MB
    max_log_file_size=209715200
    flush=true
```

#### 配置easylogging++

为了尽量减少配置文件，FISCO BCOS将easyloggin++的配置信息都集中到了config.ini的`[log]`配置，一般建议不手动更改除了日志级别外的其他配置，开启easylogging++的方法可参考[启用easylogging++](log.html#easylogging)。

- `format`：全局日志格式。
- `log_flush_threshold`：日志刷新频率设置，即每`log_flush_threshold`行刷新日志到文件一次。

easylogging++示例配置如下：

```ini
[log]
    log_path=./log
    level=info
    max_log_file_size=209715200
    ; easylog 配置
    format=%level|%datetime{%Y-%M-%d %H:%m:%s:%g}|%msg
    log_flush_threshold=100
```

### 可选配置：落盘加密

为了保障节点数据机密性，FISCO BCOS引入[落盘加密](../design/features/storage_security.md)保障节点数据的机密性，**落盘加密**操作手册请[参考这里](./storage_security.md)。

`config.ini`中的`data_secure`用于配置落盘加密，主要包括（落盘加密具体操作请参考[操作手册](./storage_security.md)）：

- `enable`： 是否开启落盘加密，默认不开启；

- `key_manager_ip`：[Key Manager](https://github.com/FISCO-BCOS/key-manager)服务的部署IP；

- `key_manager_port`：[Key Manager](https://github.com/FISCO-BCOS/key-manager)服务的监听端口；

- `cipher_data_key`: 节点数据加密密钥的密文，`cipher_data_key`的产生参考[落盘加密操作手册](./storage_security.md)。

落盘加密节点配置示例如下：

```ini
[data_secure]
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
index=2
```

### 共识配置

`[consensus]`涉及共识相关配置，包括：

- `consensus_type`：共识算法类型，目前支持[PBFT](../design/consensus/pbft.md)和[Raft](../design/consensus/raft.md)，默认使用PBFT共识算法；

- `max_trans_num`：一个区块可打包的最大交易数，默认是1000，链初始化后，可通过[控制台](./console.html#setsystemconfigbykey)动态调整该参数；

- `node.idx`：共识节点列表，配置了参与共识节点的[Node ID](../design/consensus/pbft.html#id1)，节点的Node ID可通过`${data_path}/node.nodeid`文件获取(其中`${data_path}`可通过主配置`config.ini`的`[secure].data_path`配置项获取)

```ini
; 共识协议配置
[consensus]
    ; 共识算法，目前支持PBFT(consensus_type=pbft)和Raft(consensus_type=raft)
    consensus_type=pbft
    ; 单个块最大交易数
    max_trans_num=1000
    ; leader节点的ID列表
    node.0=123d24a998b54b31f7602972b83d899b5176add03369395e53a5f60c303acb719ec0718ef1ed51feb7e9cf4836f266553df44a1cae5651bc6ddf50
e01789233a
    node.1=70ee8e4bf85eccda9529a8daf5689410ff771ec72fc4322c431d67689efbd6fbd474cb7dc7435f63fa592b98f22b13b2ad3fb416d136878369eb41
3494db8776
    node.2=7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2
922aa0ef50
    node.3=fd6e0bfe509078e273c0b3e23639374f0552b512c2bea1b2d3743012b7fed8a9dec7b47c57090fa6dcc5341922c32b89611eb9d967dba5f5d07be7
4a5aed2b4a
```

### 存储模块配置

存储主要包括[state](../design/storage/mpt.html)和[AMDB](../design/storage/storage.html)，`state`涉及交易状态存储，AMDB存储涉及系统表，分别在`[storage]`和`[state]`中配置：

- `[storage].type`：存储的DB类型，目前仅支持LevelDB，后续会支持Mysql；

- `[state].type`：state类型，目前支持[storage state](../design/storage/storage.html#id6)和[MPT state](../design/storage/mpt.html)，**默认为storage state**，storage state将交易执行结果存储在系统表中，效率较高，MPT state将交易执行结果存储在MPT树中，效率较低，但包含完整的历史信息。


```eval_rst
.. important::
   推荐使用 **storage state** ，除有特殊需求，不建议使用MPT State

```

```ini
[storage]
    ; db type, now support leveldb 
    type=LevelDB
[state]
    type=storage
```

### gas配置

FISCO BCOS兼容以太坊虚拟机([EVM](../design/virtual_machine/evm.md))，为了防止针对[EVM](../design/virtual_machine/evm.md)的DOS攻击，EVM在执行交易时，引入了gas概念，用来度量智能合约执行过程中消耗的计算和存储资源，包括交易最大gas限制和区块最大gas限制，若交易或区块执行消耗的gas超过限制(gas limit)，则丢弃交易或区块。FISCO BCOS是联盟链，简化了gas设计，**仅保留交易最大gas限制，区块最大gas通过[共识配置的max_trans_num](./configs.html#id8)和交易最大gas限制一起约束**。FISCO BCOS通过genesis的`[tx].gas_limit`来配置交易最大gas限制，默认是300000000，链初始化完毕后，可通过[控制台指令](./console.html#setsystemconfigbykey)动态调整gas限制。

```ini
[tx]
    gas_limit=300000000
```


## 账本可变配置说明

账本可变配置位于节点`conf`目录下`.ini`后缀的文件中。

如：`group1`可变配置一般命名为`group.1.ini`，可变配置主要包括交易池大小、共识消息转发的TTL。

### 交易池配置

FISCO-BCOS将交易池容量配置开放给用户，用户可根据自己的业务规模需求、稳定性需求以及节点的硬件配置动态调整交易池大小。

交易池配置示例如下：

```ini
[tx_pool]
    limit=10000
```

### PBFT共识消息广播配置

PBFT共识算法为了保证共识过程最大网络容错性，每个共识节点收到有效的共识消息后，会向其他节点广播该消息，在网络较好的环境下，共识消息转发机制会造成额外的网络带宽浪费，因此在群组可变配置项中引入了`TTL`来控制消息最大转发次数，消息最大转发次数为`TTL-1`，**该配置项仅对PBFT有效**。

设置共识消息最多转发一次，配置示例如下：

```ini
; the ttl for broadcasting pbft message
[consensus]
ttl=2
```

## 动态配置系统参数

FISCO BCOS系统目前主要包括如下系统参数(未来会扩展其他系统参数)：


```eval_rst
+-----------------+-----------+---------------------------------+
| 系统参数        | 默认值    |             含义                |
+=================+===========+=================================+
| tx_count_limit  | 1000      | 一个区块中可打包的最大交易数目  |
+-----------------+-----------+---------------------------------+
| tx_gas_limit    | 300000000 | 一个区块最大gas限制             |
+-----------------+-----------+---------------------------------+

```

控制台提供 **[setSystemConfigByKey](./console.html#setsystemconfigbykey)** 命令来修改这些系统参数，**[getSystemConfigByKey](./console.html#getsystemconfigbykey)** 命令可查看系统参数的当前值：


```eval_rst
.. important::

    不建议随意修改tx_count_limit和tx_gas_limit，如下情况可修改这些参数：

    - 机器网络或CPU等硬件性能有限：调小tx_count_limit，或降低业务压力；
    - 业务逻辑太复杂，执行区块时gas不足：调大tx_gas_limit。
```

```bash
# 设置一个区块可打包最大交易数为500
> setSystemConfigByKey tx_count_limit 500
# 查询tx_count_limit
> getSystemConfigByKey tx_count_limit
[500]

# 设置区块gas限制为400000000
> getSystemConfigByKey tx_gas_limit 400000000
> getSystemConfigByKey
[400000000]
```