# 数据治理

随着业务量的增长，链上数据占用硬盘存储越来越大，同时节点的处理能力出现一定程度的衰减。对于这两点，我们从节点存储和外部存储两方面考虑，引入数据治理方案。数据治理方案的预期在于：

1. 节点存储通过控制本地存储的数据容量，稳定联机交易性能；
2. 外部存储通过导出节点数据，实现对历史数据的记录及容量的扩展；
3. 节点访问外部可信数据，实现数据闭环。

![](../../images/storage/data_governance_1.png)

上述目标的实践包括如下环节：

- 开启binlog机制
- 基于可缩容的存储模式裁剪节点数据
- 入网节点快速恢复数据

假设下述操作所涉及的节点目录为`~/fisco/nodes/127.0.0.1/node0`。

## 开启binlog机制

binlog机制开启后，链上数据在写入缓存/落盘之前先写binlog。binlog机制有助于提升缓存机制的可靠性，同时为外部存储提供区块维度的数据操作记录。<font color="#FF0000">相关配置项</font>为节点配置项[storage]段的`binary_log`。

- 配置项默认为`false`，此时打开RocksDB的WAL；
- 当设置为`true`时开启binlog机制，此时关闭RocksDB的WAL；
- 该配置项各群组独立，不需群组内共识，节点启动时读取配置生效。

使用时，修改`~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini`文件中[storage]段的内容如下，再重启节点。
```
[storage]
...
    ; set true to turn on binary log
    binary_log=true
...
```

开启binlog机制后，生成的binlog文件以其记录的起始区块高度命名，位于节点的`~/fisco/nodes/127.0.0.1/node0/data/group1/BinaryLogs/`目录下。

```eval_rst
.. important::
    1. binlog机制需在链首次启动时开启，开启后不建议关闭。

    2. 该目录下的binlog文件不建议删除，如删除，需进行备份，及确认被删除binlog文件所记录区块的高度，需小于外部存储已同步的区块高度。binlog如在尚未同步到外部存储前被删除，将可能影响节点后续状态。如出现此情况，需删除该节点的数据及binlog，然后从其他节点同步数据。
```

外部存储的区块高度查询方式见[文末](./data_governance.html#id9)。

## 基于可缩容的存储模式裁剪节点数据

使用可缩容的scalable存储模式，节点的区块数据和状态数据存储在不同的本地数据库中，同时需配置数据代理访问远端mysql，用于查询本地已裁剪的区块数据。<font color="#FF0000">相关配置项</font>为节点配置项[storage]段的`type`和`scroll_threshold_multiple`。相关配置项各群组独立，不需群组内共识，启动时读取配置生效。

使用时，修改`~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini`文件中[storage]段的内容，设置为如下内容；同时需配置数据代理，配置代理方法请参考[这里](./distributed_storage.html#amdb)。

```
[storage]
    ; storage db type, rocksdb / mysql / external / scalable, rocksdb is recommended
    type=scalable
    ; set true to turn on binary log
    binary_log=true
    ; scroll_threshold=scroll_threshold_multiple*1000, only for scalable
    scroll_threshold_multiple=2
    ; only for external
    max_retry=60
    topic=DB
...
```

使用可缩容的存储模式后，节点的`~/fisco/nodes/127.0.0.1/node0/data/group1/`目录组织如下。其中blockDB目录下数据库以其记录起始区块高度命名，所记录的内容是区块数据，可进行裁剪。

```
.
|-- BinaryLogs
|-- block
|   |-- Scalable
|       |-- blocksDB
|       |   |-- 0
|       |   |-- 2000
|       |   |-- 4000
|       |   |-- 6000
|       |   |-- 6255
|       |   |-- 6320
|       |   |-- 8000
|       |-- state
|-- pbftMsgBackup
```

```eval_rst
.. important::
    裁剪blockDB前需确认被裁剪数据的区块高度小于外部存储已同步的区块高度。节点如访问已被裁剪且尚未被外部存储同步的数据，将退出。如出现此情况，需删除该节点的数据及binlog，然后从其他节点同步数据。
```

外部存储的区块高度查询方式见[文末](./data_governance.html#id9)。

## 入网节点快速恢复数据

为方便加入群组的新节点快速同步数据，FISCO-BCOS v2.2.0提供fisco-sync工具，用于从外部存储拉取指定区块高度的最新数据到节点指定的存储模式。新节点启动注册成为群组节点后，再从群组其他节点获取区块数据，并基于新节点当前数据的基础上执行区块中的交易，直至追平群组块高后参与群组共识。

### 工具获取

fisco-sync工具需拉取FISCO-BCOS源码，打开编译开关后自行编译。<font color="#FF0000">备注：v2.2.0打tag发布后，用户可直接拉取fisco-sync的二进制，不再需自行编译源码。</font>

- 整体编译流程请参考[源码编译](./get_executable.html#id2)；
- <font color="#FF0000">注意</font>：fisco-sync工具编译涉及的编译开关为TOOL，默认OFF，可通过`cmake3 -DTOOL=ON ..`打开该编译开关；
- 编译后，fisco-sync工具路径的生成路径`build/bin/fisco-sync`。
  
```
$ git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
$ cd FISCO-BCOS
$ git checkout release-2.2.0
$ mkdir -p build && cd build
# CentOS请使用cmake3
$ cmake -DTOOL=ON ..
# 高性能机器可添加-j4使用4核加速编译
$ make
```

### 工具说明

```bash
[app@VM_centos node0]$ ./fisco-sync -h
fisco-sync version : 0.1.0
Build Time         : 20191107 16:23:00
Commit Hash        : 09d32e6b7f43d46fc70fbc8a35fea79c35e1f800
[2019-11-07 16:27:15] The sync-tool is Initializing...
Usage of fisco-sync:
  -h [ --help ]                       print help information
  -c [ --config ] arg (=./config.ini) config file path, eg. config.ini
  -v [ --verify ] arg (=1000)         verify number of blocks, minimum is 100
  -l [ --limit ] arg (=10000)         page counts of table
  -s [ --sys_limit ] arg (=50)        page counts of system table
  -g [ --group ] arg (=1)             sync specific group
```
- **`h`选项**，打印输出选项说明；
- **`c`选项**，用于指定节点配置文件`config.ini`的路径，默认为本节点的配置文件；
- **`l`选项**，用于指定除表`_sys_hash_2_block_`和表`_sys_block_2_nonces_`之外其他表的分页拉取行数，默认为10000行每页；
- **`s`选项**，用于指定表`_sys_hash_2_block_`和表`_sys_block_2_nonces_`的分页拉取行数，默认为50行每页；
- **`v`选项**，用于指定验证区块数（原有同步逻辑需拉取的区块数），默认为1000，最小值为100；举例：假设当前外部存储的数据库块高为3560，v设置为1000，表示fisco-sync工具从全量数据服务拉取块高为3560-1000=2560的状态，后续的1000（第2561-第3560）块将从其他节点拉取；
- **`g`选项**，用于指定生成的群组ID，默认为1。

### 配置修改

- [storage]段的`type`：存储的DB类型，可选择rocksDB、mysql和scalable，但不可选择external，该类型已用于访问外部存储。其中，rocksDB和mysql类型拉取的是全量数据，scalable类型拉取的是经裁剪后的数据。

### 前期准备

- 搭建并启动外部存储的服务，外部存储已拉取节点binlog导入数据；
- 修改节点配置，搭建并启动数据代理(amdb-proxy)服务，操作方法请参考[这里](./distributed_storage.html#id16)；
- 按照[文档](./node_management.html#a)生成一个新节点，操作到第5步的拷贝genenin和ini配置；
- 编译生成fisco-sync工具。

### 同步数据流程

1. 将fisco-sync拷贝到新节点目录`~/fisco/nodes/127.0.0.1/node0/`下；
2. 修改群组配置信息，包括存储模式和`topic`；
3. 指定验证区块数及群组ID，后台启动fisco-sync。fisco-sync日志写入nohup.out，执行完毕后生成相应的data和log目录；
```
nohup ./fisco-sync -v 400 -s 10 >>nohup.out 2>&1 &
tail -f nohup.out 
```
4. 不需改动群组配置信息，启动新节点的`fisco-bcos`，进行入网操作，新节点执行原有的同步流程及后续的共识流程。

```eval_rst
.. important::
    fisco-sync工具限于在空节点上使用，如对已完成拉取操作存有数据的节点再次使用fisco-sync工具，可能影响该节点后续的同步和共识流程。如出现此情况，需清空节点数据，重新使用fisco-sync。
```

## 关于外部存储的说明

- 在本数据治理方案中，外部存储的载体为全量数据服务[<font color="#FF0000">补充链接</font>]()的数据库，
- 外部存储的区块高度查询方法：登录对应数据库，执行`select * from _sys_current_state_;`，返回的结果中key为`current_number`的那行的value值-1，即为外部存储的区块高度。