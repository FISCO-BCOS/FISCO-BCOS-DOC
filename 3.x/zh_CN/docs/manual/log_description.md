# 查看日志

标签：``日志格式`` ``日志关键词`` ``问题排查`` ``查看日志``

----

FISCO BCOS的所有群组日志都输出到log目录下`log_%YYYY%mm%dd%HH.%MM`的文件中，且定制了日志格式，方便用户通过日志查看链运行状态。

## 日志格式

每一条日志记录格式如下：

```bash
# 日志格式：
log_level|time|[module_name] content

# 日志示例：

info|2022-11-21 20:00:35.479505|[SCHEDULER][blk-1]BlockExecutive prepare: fillBlock end,txNum=1,cost=0,fetchNum=1
```

各字段含义如下：

- `log_level`: 日志级别，目前主要包括`trace`, `debug`, `info`, `warning`, `error`和`fatal`，其中在发生极其严重错误时会输出`fatal`

- `time`: 日志输出时间，精确到纳秒

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
info|2022-11-21 20:00:45.530293|[CONSENSUS][PBFT]addCheckPointMsg,reqHash=c2e031c8...,reqIndex=2,reqV=9,fromIdx=3,Idx=1,weight=4,minRequiredWeight=3
```
- `reqHash`：PBFT请求的哈希
- `reqIndex`：PBFT请求对应的块高
- `reqV`:  PBFT请求对应的视图
- `fromIdx`: 产生该PBFT请求的节点索引编号
- `Idx`: 当前节点索引编号
- `weight`: 该请求对应的proposal的共识总权重
- `minRequiredWeight`: 该请求对应的proposal达成共识所需的最小投票权重


**异常日志**

网络抖动、网络断连或配置出错(如同一个群组的创世块文件不一致)均有可能导致节点共识异常，PBFT共识节点会输出`ViewChangeWarning`日志，示例如下：

```bash
warning|2022-11-17 00:58:03.621465|[CONSENSUS][PBFT]onCheckPointTimeout: resend the checkpoint message package,index=176432,hash=d411d77d...,committedIndex=176431,consNum=176432,committedHash=ecac3705...,view=1713,toView=1713,changeCycle=0,expectedCheckPoint=176433,Idx=0,unsealedTxs=168,sealUntil=176432,waitResealUntil=176431,nodeId=0318568d...
```
- `index`：共识索引号
- `hash`： 共识区块哈希
- `committedIndex`: 落盘区块块高
- `consNum`:  下一个共识区块块高
- `committedHash`: 落盘区块哈希
- `view`: 当前视图
- `toview`:  下一个视图
- `changeCycle`: 当前的超时时钟周期
- `expectedCheckPoint`: 下一个待执行的区块块高
- `Idx`: 当前节点的索引编号
- `sealUntil`:  可以打包产生下一个区块的区块高度，在有系统区块的场景下，当且仅当落盘高度超过sealUntil时才可以打包产生下一个区块
- `waitResealUntil`: 同上，可以打包产生下一个区块的区块高度，在有视图切换 + 系统区块的场景下，当且仅当落盘高度超过waitResealUntil时才可以打包产生下一个区块
- `unsealedTxs`: 交易池里未打包的交易数目
- `nodeId`: 当前共识节点id


**区块落盘日志**

区块共识成功或节点正在从其他节点同步区块，均会输出落盘日志。

```eval_rst
.. note::

    向节点发交易，若交易被处理，非游离节点均会输出落盘日志(节点目录下可通过命令 ``tail -f log/* | grep "Report"`` 查看节点出块情况)，若没有输出该日志，说明节点已处于异常状态，请优先检查网络连接是否正常、节点证书是否有效

```

下面是区块落盘日志：
```bash
info|2022-11-21 20:00:45.531121|[CONSENSUS][PBFT][METRIC]^^^^^^^^Report,sealer=3,txs=1,committedIndex=2,consNum=3,committedHash=c2e031c8...,view=9,toView=9,changeCycle=0,expectedCheckPoint=3,Idx=1,unsealedTxs=0,sealUntil=0,waitResealUntil=0,nodeId=8f69046f...
```

日志中各字段说明如下：
- `sealer`： 产生该proposal的共识节点索引编号
- `txs`： 块内包含的交易数
- `committedIndex`: 落盘区块块高
- `consNum`:  下一个共识区块块高
- `committedHash`: 落盘区块哈希
- `view`: 当前视图
- `toview`:  下一个视图
- `changeCycle`: 当前的超时时钟周期
- `expectedCheckPoint`: 下一个待执行的区块块高
- `Idx`: 当前节点的索引编号
- `sealUntil`:  可以打包产生下一个区块的区块高度，在有系统区块的场景下，当且仅当落盘高度超过sealUntil时才可以打包产生下一个区块
- `waitResealUntil`: 同上，可以打包产生下一个区块的区块高度，在有视图切换 + 系统区块的场景下，当且仅当落盘高度超过waitResealUntil时才可以打包产生下一个区块
- `unsealedTxs`: 交易池里未打包的交易数目
- `nodeId`: 当前共识节点id


**网络连接日志**

```eval_rst
.. note::

    节点目录下可通过命令 ``tail -f log/* | grep "connected count"`` 检查网络状态，若日志输出的网络连接数目不符合预期，请通过 ``netstat -anp | grep fisco-bcos`` 命令检查节点连接
```

日志示例如下：
```bash
info|2022-08-15 19:38:59.270112|[P2PService][Service][METRIC]heartBeat,connected count=3
```

日志中各字段含义如下：
- `connected count`: 与当前节点建立P2P网络连接的节点数


## 日志模块关键字

FISCO BCOS日志中核心模块关键字如下：

| 模块 | 模块关键字 |
| :--- | :---- |
| 区块链初始化模块 | INITIALIZER |
| 网络基础模块 | NETWORK |
| P2P网络模块 | P2PService |
| 账本模块 |LEDGER|
| 共识区块打包模块 |CONSENSUS, SEALER|
| PBFT共识处理模块 | CONSENSUS, PBFT|
| 区块/交易同步模块 |SYNC|
| 交易池 |TXPOOL|
| Amop模块 | AMOP |
| 调度器    | SCHEDULER |
| 执行器    | EXECUTOR |
| 网关 |Gateway |
| 存储中间件模块 |STORAGE|
| 链工具 |TOOL|