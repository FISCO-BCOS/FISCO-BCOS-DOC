# 极端异常下的共识恢复应急方案

标签：``共识恢复`` ``存活性`` 

----

```eval_rst
.. important::
   - 使用此方案之前，请确保您对区块链共识、存储、以及区块链无法篡改特性有深刻理解。该方案所涉及的操作都是高危操作，仅用于发生极端异常情况导致区块链网络无法正常运行时的紧急修复。切勿在其它场合使用，使用不当可能导致数据异常甚至系统异常，请明确知悉，谨慎使用。确定使用该恢复方案之前，建议先对已有数据进行备份。
   - 此方案并不会篡改区块链数据，详细请阅读文章 `《如何解释“我篡改了区块链”这个问题》 <https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247487013&idx=1&sn=5053998bca25292adf5ccdd6df6c14f0&chksm=9f2efc39a859752f6bf8beb6d0d93f89078af62ac6956aa52a760deaf5409f9e71a53db78eea&scene=0&xtrack=1&exportkey=AziYJ2CsQ49Bh9aecsY0FhE%3D&pass_ticket=uclaLyUq0o08UBOWwIv0omAOK2QTL6FsKb9DrO56Rv8P4kUCDAzUKcgra8hcQLzZ&wx_header=0#rd>`_
```

**背景**：FISCO BCOS 支持的 [PBFT 共识算法](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/consensus/pbft.html) 需要区块链网络中至少存在 `2*f+1` 个共识节点正常工作（假定总节点数为 `3*f+1` ）才能维持网络的良性运转。但是实际生产环境中往往会出现各种特殊情况，如网络脑裂、节点网络中断、节点硬件崩溃，从而导致网络中节点数量少于 `2*f+1`，这时网络将无法对交易和区块达成共识，网络陷入瘫痪。

**目标**：处理 FISCO BCOS 区块链网络中由于节点数量不足 `2*f+1` 而引起的 PBFT 网络共识异常问题。

**解决方案**：手动关闭正常共识节点，修改正常共识节点数据库 `_sys_consensus_` 表中的异常共识节点信息，将异常共识节点的 type 字段值从 sealer 修改为 observer，也就是手动将异常共识节点修改为观察者节点，然后启动正常共识节点。

FISCO BCOS 支持不同的数据库存储模式，不同的数据库模式在操作和存储方面存在差异。接下来分别讲解 Mysql 和 RocksDB 数据库存储模式在面对 PBFT 共识节点不足的情况下可采取的处理方案。

## Mysql存储模式

**处理步骤**：假设网络中存在A、B、C、D四个共识节点，C、D节点由于某些原因发生崩溃，导致网络无法正常共识打包出块。

1. **关闭节点A和节点B**：为了防止操作数据库给正在运行的网络造成影响，建议先关闭节点A和节点B；
2. **手动修改节点A和节点B的数据库**：确保A、B、C、D节点块高一致的前提下修改节点A和节点B数据库中 `_sys_consensus_` 表，将节点C和节点D的 type 字段值从 sealer 修改为 observer；
3. **启动节点A和节点B**：重启节点之后，网络中共识节点只有节点A和节点B，满足 PBFT 共识条件，网络可正常共识打包出块；
4. **手动修改节点C和节点D的数据库**：修改节点C和节点D中数据库  `_sys_consensus_` 表，将节点C和节点D的 type 字段值从 sealer 修改为 observer；
5. **启动节点C和节点D**：节点C和节点D作为观察者节点，可以顺利同步区块，但无法参与网络共识；
6. **添加共识节点**：通过控制台调用 addSealer 命令，将节点C和节点D转换为共识节点。

```eval_rst
.. note::
    **该解决方案会引入一个新的问题**：当对现有网络进行共识节点扩容时，新加入的共识节点在同步区块的过程中会在某一个区块高度出现同步失败的情况，此时需要手动将新节点数据库 `_sys_consensus_` 表中节点C和节点D的 type 字段值从 sealer 修改为 observer，然后重启新加入的共识节点。重启后共识节点将继续同步区块，当同步的区块高度追上网络中最新的区块高度时，新加入的共识节点方可参与网络共识。
```

### 关闭节点A和节点B

网络在运行的过程中直接修改数据库表中字段值是非常危险的，所以先关闭节点A和节点B。

```shell
# 关闭节点A
bash ./node0/stop.sh

# 关闭节点B
bash ./node1/stop.sh
```

### 手动修改节点A和节点B的数据库

在A、B、C、D节点拥有相同的最新区块高度前提下，为了可以让由节点A和节点B组成的网络可以继续共识，需要手动修改节点A和节点B数据库中 `_sys_consensus_` 表的 type 字段，将该字段值从 sealer 修改为 observer。

1. 查看节点C和节点D的 node_id


```shell
# 节点C编号
> cat ./node2/conf/node.nodeid
da8688893fcf5b4194e4da1b79e77e81be9cb740fb373e0f59bf3d28a349e7fba9b79ec36e05715fcbe7dbb68cae661d451893128e5ccf81bc1eef7b32163496

# 节点D编号
> cat ./node3/conf/node.nodeid
17f4ad3e71418be3e074c897c73fdd07a3a69a20524476dfbd4dc18fc6284714acf58d753bbc651549f7b5e30bfd82b84fdc5ac44e67aab8c1299ef92fd4e30e
```

2. 修改节点A和节点B数据库表 `_sys_consensus_` 

```shell
# 登录 mysql
> mysql -u username -p password  # 根据实际情况填写 username 和 password

# 选择数据库
> use database-name # 根据实际情况填写 database-name

# 查看 _sys_consensus_ 表信息
> select * from _sys_consensus_;
+--------+-------+----------+------+----------+----------------------------------------------------------------------------------------------------------------------------------+------------+
| _id_   | _num_ | _status_ | name | type     | node_id                                                                                                                          | enable_num |
+--------+-------+----------+------+----------+----------------------------------------------------------------------------------------------------------------------------------+------------+
| 100004 |     0 |        0 | node | sealer   | 060a3dc076c57e42dabb9a54d5d0460dae97f1d7f63a5f4d24f4c126f7ee5d5c2f0ced34f7a661b59fe10962f26dd2f3433f6b127c860eb0a029ad3afeb05e30 | 0          |
| 100005 |     0 |        0 | node | sealer | 17f4ad3e71418be3e074c897c73fdd07a3a69a20524476dfbd4dc18fc6284714acf58d753bbc651549f7b5e30bfd82b84fdc5ac44e67aab8c1299ef92fd4e30e | 0          |
| 100006 |     0 |        0 | node | sealer   | 205d9fdeb3e3598bb6e50b4f54dab64841187cb3f8fa7f1eac71dd45fda07b81d3615857891fcd8c7cdcbb2374d8a5df0c2009d80f40e7d83db350532bc2659d | 0          |
| 100007 |     6 |        0 | node | sealer   | da8688893fcf5b4194e4da1b79e77e81be9cb740fb373e0f59bf3d28a349e7fba9b79ec36e05715fcbe7dbb68cae661d451893128e5ccf81bc1eef7b32163496 | 6          |
+--------+-------+----------+------+----------+----------------------------------------------------------------------------------------------------------------------------------+------------+
4 rows in set (0.00 sec)

# 将节点C和节点D的 type 字段修改为 observer
> update _sys_consensus_ set type="observer" where _id_="100005";
> update _sys_consensus_ set type="observer" where _id_="100007";

# 再次查看 _sys_consensus_  表信息
> select * from _sys_consensus_;
+--------+-------+----------+------+----------+----------------------------------------------------------------------------------------------------------------------------------+------------+
| _id_   | _num_ | _status_ | name | type     | node_id                                                                                                                          | enable_num |
+--------+-------+----------+------+----------+----------------------------------------------------------------------------------------------------------------------------------+------------+
| 100004 |     0 |        0 | node | sealer   | 060a3dc076c57e42dabb9a54d5d0460dae97f1d7f63a5f4d24f4c126f7ee5d5c2f0ced34f7a661b59fe10962f26dd2f3433f6b127c860eb0a029ad3afeb05e30 | 0          |
| 100005 |     0 |        0 | node | observer | 17f4ad3e71418be3e074c897c73fdd07a3a69a20524476dfbd4dc18fc6284714acf58d753bbc651549f7b5e30bfd82b84fdc5ac44e67aab8c1299ef92fd4e30e | 0          |
| 100006 |     0 |        0 | node | sealer   | 205d9fdeb3e3598bb6e50b4f54dab64841187cb3f8fa7f1eac71dd45fda07b81d3615857891fcd8c7cdcbb2374d8a5df0c2009d80f40e7d83db350532bc2659d | 0          |
| 100007 |     6 |        0 | node | observer   | da8688893fcf5b4194e4da1b79e77e81be9cb740fb373e0f59bf3d28a349e7fba9b79ec36e05715fcbe7dbb68cae661d451893128e5ccf81bc1eef7b32163496 | 6          |
+--------+-------+----------+------+----------+----------------------------------------------------------------------------------------------------------------------------------+------------+
4 rows in set (0.00 sec)

# 继续修改另外一个节点的数据库表

```

### 启动节点A和节点B

启动节点A和节点B，从数据库中读取新的节点配置信息。通过查看节点A的日志发现网络共识已恢复正常。

```shell
# 启动节点A
bash ./node0/start.sh

# 启动节点B
bash ./node1/start.sh
```

### 手动修改节点C和节点D的数据库

如果此时将节点C和节点D启动，因为节点C和节点D数据库表中记录的共识节点包括A、B、C、D，所以节点C和节点D无法同步网络中共识产生的新区块，也无法参与到新区块的共识中。为此，用户需手动修改节点C和节点D数据库中 `_sys_consensus_` 表的字段，将节点C和节点D的 type 字段值从 sealer 修改为 observer。

修改步骤同 **手动修改节点A和节点B的数据库** 小节，不重复占用篇幅。

### 启动节点C和节点D

再次启动节点C和节点D之后，节点C和节点D作为观察者节点可顺利同步区块，但是无法参与区块共识。

```shell
# 启动节点C
bash ./node2/start.sh

# 启动节点D
bash ./node3/start.sh
```

```eval_rst
.. note::
    **特殊情况处理**：节点C和节点D启动之后，此时网络已经可以正常共识出块，共识节点为A和B。假如节点A开启了 **binary_log=true** 配置项用于备份数据，某一个时刻节点A的主数据发生异常损坏，节点A需要从备份数据中进行数据恢复，在数据恢复完成之后，节点A需要再次手动修改数据库 `_sys_consensus_` 表中节点C和节点D的 type 字段值，从 sealer 修改为 observer，然后重启节点A。同理，如果节点B、C、D遇到这种情况，也需要进行相同的操作。
```

### 添加共识节点

现在节点C和节点D是观察者节点，只能同步区块却无法参与区块共识。为此，可通过在控制台执行 addSealer 命令将节点C和节点D转换为共识节点。关于节点角色转换可参考：[组员管理](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/node_management.html)。

```shell
# 转换节点C为共识节点
[group:1]> addSealer da8688893fcf5b4194e4da1b79e77e81be9cb740fb373e0f59bf3d28a349e7fba9b79ec36e05715fcbe7dbb68cae661d451893128e5ccf81bc1eef7b32163496
{
    "code":0,
    "msg":"success"
}

# 转换节点D为共识节点
[group:1]> addSealer 17f4ad3e71418be3e074c897c73fdd07a3a69a20524476dfbd4dc18fc6284714acf58d753bbc651549f7b5e30bfd82b84fdc5ac44e67aab8c1299ef92fd4e30e
{
    "code":0,
    "msg":"success"
}
```

## RocksDB存储模式

**处理步骤**：假设网络中存在A、B、C、D四个共识节点，C、D节点由于某些原因发生崩溃，导致网络无法正常共识打包出块。

1. **获取rocksdb-storage工具**：编译FISCO BCOS源码，获取 rocksdb-storage 工具，使用该工具查询、修改 rocksdb 数据库中信息；
2. **关闭节点A和节点B**：为了防止操作数据库给正在运行的网络造成影响，建议先关闭节点A和节点B；
3. **手动修改节点A和节点B的数据库**：确保A、B、C、D节点块高一致的前提下修改节点A和节点B数据库中 `_sys_consensus_` 表，将节点C和节点D的 type 字段值从 sealer 修改为 observer；
4. **启动节点A和节点B**：启动节点之后，网络中共识节点只有节点A和节点B，满足 PBFT 共识条件，网络可正常共识打包出块；
5. **手动修改节点C和节点D的数据库**：修改节点C和节点D中数据库  `_sys_consensus_` 表，将节点C和节点D的 type 字段值从 sealer 修改为 observer；
6. **启动节点C和节点D**：节点C和节点D作为观察者节点，可以顺利同步区块，但无法参与网络共识；
7. **添加共识节点**：通过控制台调用 addSealer 命令，将节点C和节点D转换为共识节点。

```eval_rst
.. note::
    **该解决方案会引入一个新的问题**：当对现有网络进行共识节点扩容时，新加入的共识节点在同步区块的过程中会在某一个区块高度出现同步失败的情况，此时需要手动将新节点数据库 `_sys_consensus_` 表中节点C和节点D的 type 字段值从 sealer 修改为 observer，然后重启新加入的共识节点。重启后共识节点将继续同步区块，当同步的区块高度追上网络中最新的区块高度时，新加入的共识节点方可参与网络共识。
```

### 获取rocksdb-storage工具

FISCO BCOS 提供查询、修改 rocksdb 数据库信息的 rocksdb-storage 工具。该工具需要手动编译FISCO BCOS源码获取：[FISCO BCOS GitHub源码链接](https://github.com/FISCO-BCOS/FISCO-BCOS)或[FISCO BCOS Gitee源码链接](https://gitee.com/FISCO-BCOS/FISCO-BCOS)，编译时通过 `cmake -DTOOL=on ..` 打开工具开关，编译成功后 rocksdb-storage 工具位于 `FISCO-BCOS/build/bin/`。详细编译步骤可参考：[编译](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html#id5)。

```shell
#  编译成功后将 rocksdb-storage 工具移动到节点主目录，如 ~/fisco/nodes/127.0.0.1/
cp ./bin/rocksdb-storage ~/fisco/nodes/127.0.0.1/
```

有关 rocksdb-storage 工具的使用说明可以参考本页第三章：rocksdb-storage 工具

### 关闭节点A和节点B

网络在运行的过程中直接修改数据库表中字段值是非常危险的，所以先关闭节点A和节点B。

```shell
# 关闭节点A
bash ./node0/stop.sh

# 关闭节点B
bash ./node1/stop.sh
```

### 手动修改节点A和节点B的数据库

在A、B、C、D节点拥有相同的最新区块高度前提下，为了可以让由节点A和节点B组成的网络可以继续共识，需要使用 rocksdb-storage 工具手动修改节点A和节点B数据库中 `_sys_consensus_` 表的 type 字段，将该字段值从 sealer 修改为 observer。

1. 查看节点C和节点D的 node_id


```shell
# 节点C编号
> cat ./node2/conf/node.nodeid
700076fb7e70c073dd04858f1030cfe51b482e6ec80e20cdcfa33335091f930b67620dd5b1627607195b1512816bb11cc4bc569d71708a302181a6e645b0a1c7

# 节点D编号
> cat ./node3/conf/node.nodeid
b88921fc39d90190fa908b24cbbd8282d823984e24f56a01f622c87c2d0f255e02eec03dbf427e29a56180b8dbc743ed054c43faa36df4ba1e81ae78ed0ad3aa
```

2. 修改节点A和节点B数据库表 `_sys_consensus_` 

```shell
# 使用 rocksdb-storage 工具查询节点A数据库 _sys_consensus_ 表数据
> ./rocksdb-storage -p ./node0/data/group1/block/RocksDB -s _sys_consensus_ node
DB path : ./node0/data/group1/block/RocksDB
select [_sys_consensus_,node] || params num : 2
================ open Table [_sys_consensus_] success! key node
***0 [ enable_num=0 ][ name=node ][ node_id=700076fb7e70c073dd04858f1030cfe51b482e6ec80e20cdcfa33335091f930b67620dd5b1627607195b1512816bb11cc4bc569d71708a302181a6e645b0a1c7 ][ type=sealer ]
***1 [ enable_num=0 ][ name=node ][ node_id=b64c6d23f479e4740c09662bcbf721c265abf34d511644d92344a20bb7c661de33d72cf56d3ba0d3eb4cb553cd4913dc30519351270e02af2c6bb45706ad4896 ][ type=sealer ]
***2 [ enable_num=0 ][ name=node ][ node_id=b88921fc39d90190fa908b24cbbd8282d823984e24f56a01f622c87c2d0f255e02eec03dbf427e29a56180b8dbc743ed054c43faa36df4ba1e81ae78ed0ad3aa ][ type=sealer ]
***3 [ enable_num=0 ][ name=node ][ node_id=e794f3666fb0c9d36e8f5e9a99d49bc15304a8332f3945c260bfdafd66dc04b5a6c1397e8900a835cce54b58b94d7993097b1706bc1398d5c3ddf1e002902b03 ][ type=sealer ]

# 修改节点A数据库 _sys_consensus_ 表中节点C的 type 字段值
> ./rocksdb-storage -p ./node0/data/group1/block/RocksDB -u _sys_consensus_ node node_id 700076fb7e70c073dd04858f1030cfe51b482e6ec80e20cdcfa33335091f930b67620dd5b1627607195b1512816bb11cc4bc569d71708a302181a6e645b0a1c7 type observer
DB path : ./node0/data/group1/block/RocksDB
update [_sys_consensus_,node,node_id,700076fb7e70c073dd04858f1030cfe51b482e6ec80e20cdcfa33335091f930b67620dd5b1627607195b1512816bb11cc4bc569d71708a302181a6e645b0a1c7,type,observer] || params num : 6
open Table [_sys_consensus_] success!
condition is [node_id=700076fb7e70c073dd04858f1030cfe51b482e6ec80e20cdcfa33335091f930b67620dd5b1627607195b1512816bb11cc4bc569d71708a302181a6e645b0a1c7]
update [type:observer]

# 修改节点A数据库 _sys_consensus_ 表中节点D的 type 字段值
> ./rocksdb-storage -p ./node0/data/group1/block/RocksDB -u _sys_consensus_ node node_id b88921fc39d90190fa908b24cbbd8282d823984e24f56a01f622c87c2d0f255e02eec03dbf427e29a56180b8dbc743ed054c43faa36df4ba1e81ae78ed0ad3aa type observer
DB path : ./node0/data/group1/block/RocksDB
update [_sys_consensus_,node,node_id,b88921fc39d90190fa908b24cbbd8282d823984e24f56a01f622c87c2d0f255e02eec03dbf427e29a56180b8dbc743ed054c43faa36df4ba1e81ae78ed0ad3aa,type,observer] || params num : 6
open Table [_sys_consensus_] success!
condition is [node_id=b88921fc39d90190fa908b24cbbd8282d823984e24f56a01f622c87c2d0f255e02eec03dbf427e29a56180b8dbc743ed054c43faa36df4ba1e81ae78ed0ad3aa]
update [type:observer]

# 再次查询节点A数据库 _sys_consensus_ 表数据
> ./rocksdb-storage -p ./node0/data/group1/block/RocksDB -s _sys_consensus_ node
DB path : ./node0/data/group1/block/RocksDB
select [_sys_consensus_,node] || params num : 2
================ open Table [_sys_consensus_] success! key node
***0 [ enable_num=0 ][ name=node ][ node_id=700076fb7e70c073dd04858f1030cfe51b482e6ec80e20cdcfa33335091f930b67620dd5b1627607195b1512816bb11cc4bc569d71708a302181a6e645b0a1c7 ][ type=observer ]
***1 [ enable_num=0 ][ name=node ][ node_id=b64c6d23f479e4740c09662bcbf721c265abf34d511644d92344a20bb7c661de33d72cf56d3ba0d3eb4cb553cd4913dc30519351270e02af2c6bb45706ad4896 ][ type=sealer ]
***2 [ enable_num=0 ][ name=node ][ node_id=b88921fc39d90190fa908b24cbbd8282d823984e24f56a01f622c87c2d0f255e02eec03dbf427e29a56180b8dbc743ed054c43faa36df4ba1e81ae78ed0ad3aa ][ type=observer ]
***3 [ enable_num=0 ][ name=node ][ node_id=e794f3666fb0c9d36e8f5e9a99d49bc15304a8332f3945c260bfdafd66dc04b5a6c1397e8900a835cce54b58b94d7993097b1706bc1398d5c3ddf1e002902b03 ][ type=sealer ]

# 继续修改节点B的数据库表

```

### 启动节点A和节点B

启动节点A和节点B，从数据库中读取新的节点配置信息。启动成功后，通过查看节点A的日志发现网络共识已恢复正常。

```shell
# 启动节点A
bash ./node0/start.sh

# 启动节点B
bash ./node1/start.sh
```

### 手动修改节点C和节点D的数据库

如果此时将节点C和节点D启动，因为节点C和节点D数据库表中记录的共识节点包括A、B、C、D，所以节点C和节点D无法同步网络中共识产生的新区块，也无法参与到新区块的共识中。为此，用户需手动修改节点C和节点D数据库中 `_sys_consensus_` 表的字段，将节点C和节点D的 type 字段值从 sealer 修改为 observer。

修改步骤同 **手动修改节点A和节点B的数据库** 小节，不重复占用篇幅。

### 启动节点C和节点D

启动节点C和节点D之后，节点C和节点D作为观察者节点可顺利同步区块，但是无法参与区块共识。

```shell
# 启动节点C
bash ./node2/start.sh

# 启动节点D
bash ./node3/start.sh
```

```eval_rst
.. note::
    **特殊情况处理**：节点C和节点D启动之后，此时网络已经可以正常共识出块，共识节点为A和B。假如节点A开启了 **binary_log=true** 配置项用于备份数据，某一个时刻节点A的主数据发生异常损坏，节点A需要从备份数据中进行数据恢复，在数据恢复完成之后，节点A需要再次手动修改数据库 `_sys_consensus_` 表中节点C和节点D的 type 字段值，从 sealer 修改为 observer，然后重启节点A。同理，如果节点B、C、D遇到这种情况，也需要进行相同的操作。
```

### 添加共识节点

现在节点C和节点D是观察者节点，只能同步区块却无法参与区块共识。为此，可通过在控制台执行 addSealer 命令将节点C和节点D转换为共识节点。关于节点角色转换可参考：[组员管理](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/node_management.html)。

```shell
# 转换节点C为共识节点
[group:1]> addSealer 700076fb7e70c073dd04858f1030cfe51b482e6ec80e20cdcfa33335091f930b67620dd5b1627607195b1512816bb11cc4bc569d71708a302181a6e645b0a1c7
{
    "code":0,
    "msg":"success"
}

# 转换节点D为共识节点
[group:1]> addSealer b88921fc39d90190fa908b24cbbd8282d823984e24f56a01f622c87c2d0f255e02eec03dbf427e29a56180b8dbc743ed054c43faa36df4ba1e81ae78ed0ad3aa
{
    "code":0,
    "msg":"success"
}
```

## rocksdb-storage 工具

FISCO BCOS 提供 rocksdb-storage 工具帮助用户对 rocksdb 数据库进行常规 CRUD 操作，此外 rocksdb-storage 工具也支持手动创建数据表。

**获取途径**：手动编译FISCO BCOS源码：[FISCO BCOS GitHub源码链接](https://github.com/FISCO-BCOS/FISCO-BCOS)或[FISCO BCOS Gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS)。编译时通过 `cmake -DTOOL=on ..` 打开工具开关，编译成功后 rocksdb-storage 工具位于 `FISCO-BCOS/build/bin/`。详细编译步骤可参考：[编译](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html#id5)。

### 帮助

```markdown
Usage:
      -h [ --help ]              help of rocksdb reader
      -c [ --createTable ] arg   [TableName] [KeyField] [ValueField]
      -p [ --path ] arg (=data/) [RocksDB path]
      -s [ --select ] arg        [TableName] [priKey]
      -u [ --update ] arg        [TableName] [priKey] [keyEQ] [valueEQ] [Key] 
                                 [NewValue]
      -i [ --insert ] arg        [TableName] [priKey] [Key]:[Value],...,[Key]:[Valu
                                 e]
      -r [ --remove ] arg        [TableName] [priKey]
      -e [ --encrypt ] arg       [encryptKey] [SMCrypto]
e.g
	./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s _sys_consensus_ node
```

### 选项介绍

#### `-c`选项[Optional]

同 `--createTable` 选项，用于创建数据库表。参数包括：

- TableName：表名；
- KeyField：主键字段名；
- ValueField：其余字段名。如果有多个字段名则以符号 `,` 进行分隔。

#### `-p`选项

同 `--path` 选项，用于指定节点 rocksdb 数据文件所在存储目录。rocksdb 默认文件路径为 `./node*/data/group*/block/RocksDB/`。

#### `-s`选项[Optional]

同 `--select` 选项，用于查询数据库表中数据项记录。参数包括：

- TableName：表名；
- priKey：主键字段值；

```eval_rst
.. note::
    priKey 是 KeyFiled 的具体值。比如 _sys_consensus_ 系统表的主键是 name，主键值为 node。详细请参考：https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/storage/storage.html。
```

#### `-u`选项[Optional]

同 `--update` 选项，用于更新数据库表中数据项记录。参数包括：

- TableName：表名；
- priKey：主键字段值；
- keyEQ：key 值，用于指定非主键字段名；
- valueEQ：value 值，用于指定非主键字段值，和 keyEQ 组合使用；
- Key：key 值，待修改的字段名；
- NewValue：value 值，待修改值。

```eval_rst
.. note::
    keyEQ 和 valueEQ 组合在一起的作用可以类比为 mysql 中的 where 条件。
```

#### `-i`选项[Optional]

同 `--insert` 选项，用于新增数据库表中数据项记录。参数包括：

- TableName：表名；
- priKey：主键字段值，同时也是待插入的数据；
- `Key:Value`：以符号 `,` 连接的多个待插入数据。

#### `-r`选项[Optional]

同 `--remove` 选项，用于删除数据库表中数据项记录。参数包括：

- TableName：表名；
- priKey：主键字段值，此处用于指定待删除的数据项。

#### `-e`选项[Optional]

同 `--encrypt` 选项，用于标识已开启落盘加密的节点，有关落盘加密可 [参考](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/features/storage_security.html)。参数包括：

- encryptKey：dataKey；
- SMCrypto：是否开启国密。

### 使用举例

```eval_rst
.. note::
    使用 rocksdb-storage 工具操作网络中开启落盘加密节点的数据库时，需要额外指定 `-e` 选项，同时建议将 -e 选项放置在命令行最后。
```

1. 搭建单群组四节点区块链网络，可参考：[安装](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)。搭建完之后需要将所有节点关闭，rocksdb 数据库无法同时被打开两次。

2. 编译 FISCO BCOS 源码，获取 rocksdb-storage 工具，移动工具至 `~/fisco/` 文件夹目录

3. **查询数据库表中数据项记录**：使用 `-s` 参数查看 `_sys_consensus_` 系统表中的节点数据。关于该系统表的说明可参考：[AMDB](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/storage/storage.html)

   ```shell
   # -p 指定 rocksdb 文件夹，-s 指定 TableName 和 priKey
   # 非国密落盘加密 ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s _sys_consensus_ node -e 123456
   # 国密落盘加密 ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s _sys_consensus_ node -e 123456 SMCrypto
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s _sys_consensus_ node
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   select [_sys_consensus_,node] || params num : 2
   ================ open Table [_sys_consensus_] success! key node
   ***0 [ enable_num=0 ][ name=node ][ node_id=5ea630a8cad6681c9e54a30009caf2afd3fe75a01a46375a5c9ad947d42b3bae8830cd9dbf67ea5d3cccb3eb02cca9a0d91a65e56c97e3005cb85455b52fa89b ][ type=sealer ]
   ***1 [ enable_num=0 ][ name=node ][ node_id=6a6bfde992a8bc7b5fa77a5dfc5512df588b7ea8229ae48925929f77762f40e9bdecef420f5af7b752d9335f74cea270f3dd3a306db647e205dc8255acaefe32 ][ type=sealer ]
   ***2 [ enable_num=0 ][ name=node ][ node_id=6b5612f7206ef9559f1e33efc7f427bc4ab2e3a4674be4c7939f2ea49cb23dab341ab8f6f4b1586b38822e516b6a8391226bea2036df42fe295bf973b2a15ceb ][ type=sealer ]
   ***3 [ enable_num=0 ][ name=node ][ node_id=8e30de9cf192ee644b735d8a4765c7160a3227fa398e040eafa52cd834fd04260b5b960a6dc72c1a3ed8cbb713e29a875bdf8fb0b69f1e46c389068b551e90c7 ][ type=sealer ]
   ```

4. **更新数据库表中数据项记录**：使用 `-u` 参数修改 `_sys_consensus_` 系统表中的节点数据

   ```shell
   # -p 指定 rocksdb 文件夹，-u 指定 TableName、priKey、keyEQ、valueEQ、Key 和 NewValue
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -u _sys_consensus_ node node_id 5ea630a8cad6681c9e54a30009caf2afd3fe75a01a46375a5c9ad947d42b3bae8830cd9dbf67ea5d3cccb3eb02cca9a0d91a65e56c97e3005cb85455b52fa89b type observer
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   update [_sys_consensus_,node,node_id,5ea630a8cad6681c9e54a30009caf2afd3fe75a01a46375a5c9ad947d42b3bae8830cd9dbf67ea5d3cccb3eb02cca9a0d91a65e56c97e3005cb85455b52fa89b,type,observer] || params num : 6
   open Table [_sys_consensus_] success!
   condition is [node_id=5ea630a8cad6681c9e54a30009caf2afd3fe75a01a46375a5c9ad947d42b3bae8830cd9dbf67ea5d3cccb3eb02cca9a0d91a65e56c97e3005cb85455b52fa89b]
   update [type:observer]
   
   # 查看修改后的 _sys_consensus_ 系统表
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s _sys_consensus_ node
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   select [_sys_consensus_,node] || params num : 2
   ================ open Table [_sys_consensus_] success! key node
   ***0 [ enable_num=0 ][ name=node ][ node_id=5ea630a8cad6681c9e54a30009caf2afd3fe75a01a46375a5c9ad947d42b3bae8830cd9dbf67ea5d3cccb3eb02cca9a0d91a65e56c97e3005cb85455b52fa89b ][ type=observer ]
   ***1 [ enable_num=0 ][ name=node ][ node_id=6a6bfde992a8bc7b5fa77a5dfc5512df588b7ea8229ae48925929f77762f40e9bdecef420f5af7b752d9335f74cea270f3dd3a306db647e205dc8255acaefe32 ][ type=sealer ]
   ***2 [ enable_num=0 ][ name=node ][ node_id=6b5612f7206ef9559f1e33efc7f427bc4ab2e3a4674be4c7939f2ea49cb23dab341ab8f6f4b1586b38822e516b6a8391226bea2036df42fe295bf973b2a15ceb ][ type=sealer ]
   ***3 [ enable_num=0 ][ name=node ][ node_id=8e30de9cf192ee644b735d8a4765c7160a3227fa398e040eafa52cd834fd04260b5b960a6dc72c1a3ed8cbb713e29a875bdf8fb0b69f1e46c389068b551e90c7 ][ type=sealer ]
   ```

5. **新增数据库表中数据项记录**：使用 `-i` 参数新增 `_sys_consensus_` 系统表中的节点数据

   ```shell
   # -p 指定 rocksdb 文件夹，-i 指定 TableName、priKey 和 [Key]:[Value],...,[Key]:[Value]，注意 priKey 为 peer
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -i _sys_consensus_ peer node_id:17f4ad3e71418be3e074c897c73fdd07a3a69a20524476dfbd4dc18fc6284714acf58d753bbc651549f7b5e30bfd82b84fdc5ac44e67aab8c1299ef92fd4e30e,type:sealer,enable_num:0
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   insert [_sys_consensus_,peer,node_id:17f4ad3e71418be3e074c897c73fdd07a3a69a20524476dfbd4dc18fc6284714acf58d753bbc651549f7b5e30bfd82b84fdc5ac44e67aab8c1299ef92fd4e30e,type:sealer,enable_num:0] || params num : 3
   open Table [_sys_consensus_] success!
   
   # 查看新增后的 _sys_consensus_ 系统表，注意 priKey 为 peer
   >  ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s _sys_consensus_ peer
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   select [_sys_consensus_,peer] || params num : 2
   ================ open Table [_sys_consensus_] success! key peer
   ***0 [ enable_num=0 ][ name=peer ][ node_id=17f4ad3e71418be3e074c897c73fdd07a3a69a20524476dfbd4dc18fc6284714acf58d753bbc651549f7b5e30bfd82b84fdc5ac44e67aab8c1299ef92fd4e30e ][ type=sealer ]
   ```

6. **删除数据库表中数据项记录**：使用 `-r` 参数删除 `_sys_consensus_` 系统表中的节点数据

   ```shell
   # -p 指定 rocksdb 文件夹，-r 指定 TableName 和 priKey，注意 priKey 为 peer
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -r _sys_consensus_ peer
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   remove [_sys_consensus_,peer] || params num : 2
   open Table [_sys_consensus_] success!
   
   # 查看删除后的 _sys_consensus_ 系统表，注意 priKey 为 peer
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s _sys_consensus_ peer
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   select [_sys_consensus_,peer] || params num : 2
   ================ open Table [_sys_consensus_] success! key peer is empty!
   ```

7. **创建数据库表**：使用 `-c` 参数为数据库创建新的数据表 `user`，包括 username、age 和 sex 三个字段，其中 username 是主键

   ```shell
   # -p 指定 rocksdb 文件夹，-c 指定 TableName、KeyField 和 ValueField
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -c user username age,sex
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   createTable [user,username,age,sex] || params num : 3
   KeyField:[username]
   ValueField:[age,sex]
   createTable [user] success!
   
   # 插入数据
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -i user Bob age:20,sex:male
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   insert [user,Bob,age:20,sex:male] || params num : 3
   open Table [user] success!
   
   # 查看新插入的数据
   > ./rocksdb-storage -p ./nodes/127.0.0.1/node0/data/group1/block/RocksDB -s user Bob
   
   DB path : ./nodes/127.0.0.1/node0/data/group1/block/RocksDB
   select [user,Bob] || params num : 2
   ================ open Table [user] success! key Bob
   ***0 [ age=20 ][ sex=male ][ username=Bob ]
   ```