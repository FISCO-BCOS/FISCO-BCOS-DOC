# 15. 快照功能

标签：``运维``，``快照``

---

## 简介

v3.11.0开始支持了快照的导入和导出功能，快照功能可以帮助用户快速备份和恢复链上数据，当新节点加入时也可以通过快照减少同步到最新状态所需要的时间。

## 生成快照

### 生成完整快照

生成完整快照是将区块链上的状态数据、交易和收据完整的导出，快照的大小与节点数据目录(例如`./data`)的大小相近，生成快照的时间与数据目录的大小成正比。操作命令如下，其中`-s`表示是否携带交易与收据，`-o`表示快照的输出目录：

```bash
# 生成完整快照，要在节点目录下执行
../fisco-bcos -s true -o ./
```

输出如下：

```bash
[2024-08-26 16:41:07] generating snapshot to ./ ...
current block number: 7883
Traverse RocksDB: data
".//snapshot/state/000000.sst" Finished. 1
".//snapshot/block/000000.sst" Finished. 2
generate snapshot success, the snapshot is in .//snapshot
[2024-08-26 16:41:09] generate snapshot success.
```

产生的快照位于输出目录下，例如`./snapshot`。

```bash
$ ls snapshot
block meta  state
```

snapshot目录下包含了三个文件夹，分别是`block`、`meta`和`state`，其中：

- `block`文件夹包含区块中的交易和收据数据
- `meta`文件夹包含了快照的元信息
- `state`文件夹包含了区块链的状态数据

### 生成状态快照，不包含历史区块的交易和收据

生成状态快照是将区块链上的状态数据导出，不包含历史区块的交易和收据，相比于完整快照，状态快照的空间占用会小很多。操作命令如下，其中`-s`表示是否携带交易与收据，`-o`表示快照的输出目录：

```bash
# 生成状态快照，要在节点目录下执行
../fisco-bcos -s false -o ./
```

输出如下：

```bash
[2024-08-26 16:40:12] generating snapshot to ./ ...
current block number: 7883
Traverse RocksDB: data
".//snapshot/state/000000.sst" Finished. 1
generate snapshot success, the snapshot is in .//snapshot
[2024-08-26 16:40:12] generate snapshot success.
```

snapshot文件夹与完整快照类似，只是没有了block文件夹。

## 导入快照

导入快照功能可以从快照中恢复区块链的状态数据，导入快照的时间与快照的大小成正比。操作命令如下，其中`-i`表示快照的输入目录：

```bash
# 导入快照，要在节点目录下执行
../fisco-bcos -i ./snapshot
```

建议用户在通过快照生成新节点时，设置`config.ini`文件中`[storage].enable_separate_block_state=true`以避免快照导入过程中，可能出现的因为交易、收据和状态数据触发RocksDB的compaction，导致导入过程变慢。

导入过程中会提示用户选择导入方式，yes表示使用move模式，no表示使用copy模式，move模式会尝试移动快照中的文件，当可以移动时导入会很快，copy模式会拷贝快照中的数据，耗时取决于数据量大小。输出如下，

```bash
[2024-08-26 16:41:52] importing snapshot from ./snapshot ...
The block number of snapshot: 7883
the snapshot will be ingested into data, if yes the snapshot will be moved, if no the snapshot will be copy(yes/no)
no
check sst files success, ingest sst files
check sst files success, ingest sst files
The block number of this node: 7883
[2024-08-26 16:42:14] import snapshot success.
```

## 归档区块同步

在支持快照的同时，节点`config.ini`新增了配置项`[storage].sync_archived_blocks`，默认值为`false`，当设置为`true`时，节点会通过p2p请求同步已经归档的区块。

节点执行过下面两种操作的任意一种，就会存在归档区块：

- 节点从状态快照生成时，没有交易和收据的区块会被认为是归档区块。
- 节点通过归档工具做过归档。

从这种节点可以查询区块头，但当查询归档区块中的交易和收据时，节点会返回错误。

## 历史nonce清理功能

FISCO BCOS会存储最近1000个区块中交易的nonce，用来防范交易重放攻击，v3.11.0版本新增了历史nonce清理功能，会自动清理掉超出最近1000个块的nonce列表，减小节点存储压力，例如当前块高为1001，则提交1002时，会自动清理块高2的nonce列表。同时在命令行添加了手动清理历史区块nonce列表的命令，操作命令如下：

```bash
# 导入快照，要在节点目录下执行
../fisco-bcos --prune
```
