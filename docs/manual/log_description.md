# 查看日志

标签：``日志格式`` ``日志关键词`` ``问题排查`` ``查看日志``

----

FISCO BCOS的所有群组日志都输出到log目录下`log_%YYYY%mm%dd%HH.%MM`的文件中，且定制了日志格式，方便用户通过日志查看各群组状态。日志配置说明请参考[日志配置说明](./configuration.html#id6)

## 日志格式

每一条日志记录格式如下：

```bash
# 日志格式：
log_level|time|[g:group_id][module_name] content

# 日志示例：
info|2019-06-26 16:37:08.253147|[g:3][CONSENSUS][PBFT]^^^^^^^^Report,num=0,sealerIdx=0,hash=a4e10062...,next=1,tx=0,nodeIdx=2
```

各字段含义如下：

- `log_level`: 日志级别，目前主要包括`trace`, `debug`, `info`, `warning`, `error`和`fatal`，其中在发生极其严重错误时会输出`fatal`

- `time`: 日志输出时间，精确到纳秒

- `group_id`: 输出日志记录的群组ID

- `module_name`：模块关键字，如同步模块关键字为`SYNC`，共识模块关键字为`CONSENSUS`

- `content`：日志记录内容


## 常见日志说明

**共识打包日志**

```eval_rst
.. note::

    - 仅共识节点会周期性输出共识打包日志(节点目录下可通过命令 ``tail -f log/* | grep "${group_id}.*++"`` 查看指定群组共识打包日志)

    - 打包日志可检查指定群组的共识节点是否异常，**异常的共识节点不会输出打包日志**
```

下面是共识打包日志的示例：
```bash
info|2019-06-26 18:00:02.551399|[g:2][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=1f9c2b14...
```

日志中各字段的含义如下：
- `blkNum`: 打包区块的高度
- `tx`: 打包区块中包含的交易数
- `nodeIdx`: 当前共识节点的索引
- `hash`: 打包区块的哈希


**共识异常日志**

网络抖动、网络断连或配置出错(如同一个群组的创世块文件不一致)均有可能导致节点共识异常，PBFT共识节点会输出`ViewChangeWarning`日志，示例如下：

```bash
warning|2019-06-26 18:00:06.154102|[g:1][CONSENSUS][PBFT]ViewChangeWarning: not caused by omit empty block ,v=5,toV=6,curNum=715,hash=ed6e856d...,nodeIdx=3,myNode=e39000ea...
```
该日志各字段含义如下：

- v: 当前节点PBFT共识视图
- toV: 当前节点试图切换到的视图
- curNum: 节点最高块高
- hash: 节点最高块哈希
- nodeIdx: 当前共识节点索引
- myNode: 当前节点Node ID


**区块落盘日志**

区块共识成功或节点正在从其他节点同步区块，均会输出落盘日志。

```eval_rst
.. note::

    向节点发交易，若交易被处理，非游离节点均会输出落盘日志(节点目录下可通过命令 ``tail -f log/* | grep "${group_id}.*Report"`` 查看节点出块情况)，若没有输出该日志，说明节点已处于异常状态，请优先检查网络连接是否正常、节点证书是否有效

```

下面是区块落盘日志：
```bash
info|2019-06-26 18:00:07.802027|[g:1][CONSENSUS][PBFT]^^^^^^^^Report,num=716,sealerIdx=2,hash=dfd75e06...,next=717,tx=8,nodeIdx=3
```

日志中各字段说明如下：
- `num`: 落盘区块块高
- `sealerIdx`: 打包该区块的共识节点索引
- `hash`: 落盘区块哈希
- `next`: 下一个区块块高
- `tx`: 落盘区块中包含的交易数
- `nodeIdx`: 当前共识节点索引


**网络连接日志**

```eval_rst
.. note::

    节点目录下可通过命令 ``tail -f log/* | grep "connected count"`` 检查网络状态，若日志输出的网络连接数目不符合预期，请通过 ``netstat -anp | grep fisco-bcos`` 命令检查节点连接
```

日志示例如下：
```bash
info|2019-06-26 18:00:01.343480|[P2P][Service] heartBeat,connected count=3
```

日志中各字段含义如下：
- `connected count`: 与当前节点建立P2P网络连接的节点数


## 日志模块关键字

FISCO BCOS日志中核心模块关键字如下：

| 模块 | 模块关键字 |
| :--- | :---- |
| 区块链初始化模块 | INITIALIZER |
| 网络基础模块 | NETWORK |
| P2P网络模块 | P2P |
| ChannelRPC模块 |  CHANNEL |
| RPC模块| RPC |
| 账本模块 |LEDGER|
| 共识区块打包模块 |CONSENSUS, SEALER|
| PBFT共识处理模块 | CONSENSUS, PBFT|
| RAFT共识处理模块 | CONSENSUS, RAFTENGINE|
| 区块/交易同步模块 |SYNC|
| 交易池 |TXPOOL|
| 区块链模块 | BLOCKCHAIN |
| 区块验证器模块    | BLOCKVERIFIER | 
| DAG模块 |DAG |
| 区块执行模块 | EXECUTIVECONTEXT|
| Precompile合约 |PRECOMPILED|
| 存储中间件模块 |STORAGE|
| External存储引擎 |SQLConnectionPool|
| MySQL存储引擎  |ZdbStorage|
