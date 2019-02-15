# 配置文件与配置项

FISCO BCOS支持多账本架构，每条链包括多个独立的账本，账本间数据和交易相互隔离，每条链主要包括一个总体配置`config.ini`和各个账本的配置group.${group_id}.genesis、group.${group id}.ini。

- config.ini：主配置文件，主要配置RPC、P2P、SSL连接证书、账本配置文件路径等信息
- group.${group_id}.genesis：群组不可变配置(创世)文件，群组内所有节点该配置必须相同，节点启动后，不可更改该配置，主要配置群组共识算法、存储类型、最大gas限制等系统信息。
- group.${group_id}.ini：群组可变配置文件，包括交易池大小等，可根据节点性能动态调整。


 **PS** : 由于多群组共享网络带宽、CPU和内存资源，因此为了保证服务的稳定性，一台机器上不推荐配置过多群组。

下表是单群组单节点推荐的配置，节点耗费资源与群组个数呈线性关系，您可根据实际的业务需求和机器资源，合理地配置群组数目:

```eval_rst
  +-----------------+--------+
  | CPU             | 1核    |
  +=================+========+
  | 内存            | 1G     |
  +-----------------+--------+
  | 网络带宽        | 5M     |
  +-----------------+--------+
```


## 主配置config.ini

config.ini采用ini格式的配置，主要包括rpc、p2p、group、secure和log五段配置。

### 配置RPC

- listen_ip: RPC监听ip，若为127.0.0.1，则仅监听本机RPC请求；为0.0.0.0和内网ip时，监听所有请求.
**注：云主机的公网ip均为虚拟ip，若listen_ip填写公网ip，会绑定失败，此时须填写0.0.0.0**

- channel_listen_port: channelserver监听端口，配置web3sdk时候使用的channelPort，即为该端口，端口必须位于0-65536范围内，且不能与机器上其他端口冲突
- jsonrpc_listen_port: RPC监听端口，端口必须位于0-65535范围内，且不能与机器上其他端口冲突。

```ini
[rpc]
    ;rpc listen ip
    listen_ip=127.0.0.1
    ;channelserver listen port
    channel_listen_port=30301
    ;rpc listen port
    jsonrpc_listen_port=30302
```

### 配置P2P

FISCO BCOS从静态文件中加载P2P配置，节点需要连接的所有节点均在config.ini中配置。

- listen_ip：P2P监听端口，若需接收所有机器请求，须填写0.0.0.0
- listen_port：节点P2P监听端口
- node.* : 节点要连接的所有节点信息({ip:port}),  **端口必须在0-65535范围内，且不同节点端口不能重复** 

下面配置中，监听30300端口的节点会与监听30304、30308和30312的本地节点建立P2P连接。

```ini
[p2p]
    ;p2p listen ip
    listen_ip=0.0.0.0
    ;p2p listen port
    listen_port=30300
    ;nodes to connect
    node.0=127.0.0.1:30300
    node.1=127.0.0.1:30304
    node.2=127.0.0.1:30308
    node.3=127.0.0.1:30312
```

### 配置账本文件路径

通过[group]段配置本节点所属的所有群组配置路径：

- group_data_path: 群组数据存储路径
- group_config.group_id: 指定群组的配置文件路径(**注: 必须保证该配置项制定的文件存在，否则启动会出错**)

```ini
;group configurations
;if need add a new group, eg. group2, can add the following configuration:
;group_config.2=conf/group.2.genesis
;group.2.genesis can be populated from group.1.genesis
;WARNING: group 0 is forbided
[group]
    ;所有群组数据放置于节点的data子目录
    group_data_path=data/
    ;该节点属于群组1，群组配置文件是conf/group.1.genesis
    group_config.1=conf/group.1.genesis
```


### 配置证书信息

FISCO BCOS节点之间采用SSL安全通道发送和接收消息，证书主要配置项集中在[secure]段：

- data_path：证书文件所在路径
- key：节点私钥相对于${data_path}的路径
- cert: 证书node.crt相对于${data_path}的路径
- ca_cert: ca证书路径

```ini
;certificate configuration
[secure]
   ;directory the certificates located in
    data_path=conf/
    ;the node private key file
    key=node.key
    ;the node certificate file
    cert=node.crt
    ;the ca certificate file
    ca_cert=ca.crt
```

### 配置黑名单列表

FISCO BCOS允许节点配置不信任的黑名单节点列表，并拒绝与这些黑名单节点建立连接, 主要在[crl]段配置:

- crl.${idx}: 黑名单节点的nodeID, 节点node id可通过`node.node_id`文件获取; ${idx}是黑名单节点的索引。

黑名单的详细信息还可参考[CA黑名单](./certificate_rejected_list.md)

```bash
# node1将node0列为黑名单节点(设node0和node1均位于~目录)
$ cat ~/node0/conf/node.node_id
4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e

# 将node0配置为node1黑名单
$sed -i '/\[crl\]/a\crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e' config.ini

# 查看node1配置
$ cat ~/node1/config.ini | grep ctl
;certificate rejected list    
[crl]
    crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e
3787c338a4

```

### 配置日志信息

FISCO BCOS同时支持轻量级的[easylogging++](https://github.com/zuhd-org/easyloggingpp)，也支持功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，可通过编译开关配置使用这两种日志，详细可参考[日志操作手册](log.md)。

#### 配置easylogging++

为了尽量减少配置文件，FISCO BCOS将easyloggin++的配置信息都集中到了config.ini的[log]段，一般建议不手动更改除了日志级别设置之外的其他配置，开启easylogging++的方法可参考[启用easylogging++](log.md)，日志级别主要由以下关键字设置：

- INFO-ENABLED：true表明开启INFO级别日志；false表明关闭INFO级别日志
- ERROR-ENABLED：true表明开启ERROR级别日志；false表明关闭ERROR级别日志
- DEBUG-ENABLED：true表明开启DEBUG级别日志；false表明关闭DEBUG级别日志
- TRACE-ENABLED：true表明开启TRACE级别日志；false表明关闭TRACE级别日志
- FATAL-ENABLED：true表明开启FATAL级别日志；false表明关闭FATAL级别日志。

其他配置项包括：

- LOG_PATH：日志文件路径
- GLOBAL-ENABLED：是否采用全局日志配置
- GLOBAL-FORMAT：全局日志格式
- GLOBAL-MAX_LOG_FILE_SIZE：每个日志文件最大容量(默认是200MB)
- GLOBAL-LOG_FLUSH_THRESHOLD：日志刷新频率设置，即每GLOBAL-LOG_FLUSH_THRESHOLD行刷新日志到文件一次


```ini
;log configurations
[log]
    ;the directory of the log
    LOG_PATH=./log
    GLOBAL-ENABLED=true
    GLOBAL-FORMAT=%level|%datetime{%Y-%M-%d %H:%m:%s:%g}|%msg
    GLOBAL-MILLISECONDS_WIDTH=3
    GLOBAL-PERFORMANCE_TRACKING=false
    GLOBAL-MAX_LOG_FILE_SIZE=209715200
    GLOBAL-LOG_FLUSH_THRESHOLD=100

    ;log level configuration, enable(true)/disable(false) corresponding level log
    INFO-ENABLED=true
    WARNING-ENABLED=true
    ERROR-ENABLED=true
    DEBUG-ENABLED=true
    TRACE-ENABLED=false
    FATAL-ENABLED=false
```

#### 配置boostlog

FISCO BCOS默认使用boostlog，开启和关闭boostlog请参考[boostlog](log.md)。相较于easylogging++，boostlog配置项很简单，主要如下：

- Level: 日志级别，当前主要包括TRACE/DEBUG/INFO/WARNING/ERROR五种日志级别，设置某种日志级别后，日志文件中会输≥该级别的日志，日志级别从大到小排序`ERROR > WARNING > INFO > DEBUG > TRACE`

- MaxLogFileSize：每个日志文件最大容量

```ini
    ;log level for boost log 
    Level=TRACE
    MaxLogFileSize=1677721600
```


## 群组不可变配置说明

每个群组都有单独的配置文件，按照启动后是否可更改配置，可分为群组不可变配置和群组可变配置。
群组不可变配置一般位于节点的conf目录下genesis后缀的文件中，如：group1的不可变配置一般命名为group.1.genesis，genesis配置主要包括共识、存储和gas相关的配置。配置genesis配置时，需注意：

- **配置群组内一致**：群组不可变配置用于产生创世块(第0块)，因此必须保证群组内所有节点的该配置一致
- **节点启动后不可更改**：由于genesis配置已经作为创世块写入了系统表，链初始化后，该配置不能更改
- 链初始化后，即使更改了genesis配置，新的配置不会生效，系统仍然使用初始化链时的genesis配置
- 由于genesis配置要求群组内所有节点一致，建议使用 [build_chain](./build_chain.md) 在搭建节点时生成该配置

### 共识配置

[consensus]段主要涉及共识相关的配置，包括：

- consensus_type：共识算法类型，目前支持[PBFT](../design/consensus/pbft.md)和[Raft](../design/consensus/raft.md)，默认是PBFT
- max_trans_num：一个区块中可打包的最大交易数，默认是1000，链初始化后，可通过[控制台](./console.md)动态调整该参数
- node.${idx}：共识节点列表，配置了参与共识节点的[Node ID](../design/consensus/pbft.html#id1)，节点的Node ID可通过 ${data_path}/node.nodeid文件获取(其中${data_path}可通过主配置config.ini的[secure].data_path选项获取)

```ini
;consensus configuration
[consensus]
    ;consensus algorithm type, now support PBFT(consensus_type=pbft) and Raft(consensus_type=raft)
    consensus_type=pbft
    ;the max number of transactions of a block
    max_trans_num=1000
    ;the node id of leaders
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

存储主要包括两大块，即：[state存储](../design/storage/mpt.md)和[storage存储](../design/storage/storage.md)，state存储涉及到交易执行，storage存储涉及到系统表，分别在[storage]和[state]段中配置：

- [storage].type：存储的DB类型，目前仅支持levelDB，后续会做[AMDB](../design/storage/storage.md)支持
- [state].type：state类型，目前支持[mpt state](../design/storage/mpt.md)和[storage state](../design/storage/storage.md)，mpt state会将交易执行结果存储在[mpt树](../design/storage/mpt.md)中，效率较低，但包含完整的历史信息; storage state则将交易执行结果存储在系统表中，效率较高，但是不包含任何历史信息。

```ini
[storage]
    ;storage db type, now support leveldb 
    type=LevelDB
[state]
    ;support mpt/storage
    type=mpt
```

### gas配置

FISCO BCOS兼容以太坊虚拟机([evm](../design/virtual_machine/evm.md))，为了防止针对[evm](../design/virtual_machine/evm.md)的DOS攻击，evm在执行交易时，引入了gas概念，用来度量智能合约执行过程中消耗的计算和存储资源，包括交易最大gas限制和区块最大gas限制，若交易或区块执行消耗的gas超过限制(gas limit)，则丢弃交易或区块。FISCO BCOS是联盟链，简化了gas设计，仅保留了交易最大gas限制，区块最大gas通过[共识配置的max_trans_num](./configs.html#id8)和交易最大gas限制一起约束。FISCO BCOS通过genesis的[tx].gas_limit来配置交易最大gas限制，默认是300000000，链初始化完毕后，可通过[控制台指令](./console.md)动态调整gas限制。

```ini
;tx gas limit
[tx]
    gas_limit=300000000
```


## 账本可变配置说明

账本可变配置位于节点conf目录下ini后缀的文件中，如：group1的可变配置一般命名为group.1.ini，主要包括交易池大小、同步频率等配置。

### 交易池配置

FISCO-BCOS将交易池容量配置暴露给用户，用户可根据自己的业务规模需求、稳定性需求以及节点的硬件配置动态调整交易池大小。

```ini
;txpool limit
[tx_pool]
    limit=1000
```
