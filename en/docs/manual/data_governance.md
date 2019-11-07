# Data governance

With the growth of business volume, chain data occupies more and more hard disk storage, and at the same time, the processing capacity of nodes has declined to some extent. To make the business side have a good user experience, we introduce a data governance solution from both node storage and external storage. The expectations of this data governance program are:

1. Node storage stabilizes online transaction performance by reducing the amount of local data;
2. External storage records historical data and expanded capacity by exporting node data;
3. Nodes access external trusted data.

![](../../images/storage/data_governance_architecture.png)

Data governance practices include the following:

- Enable binlog
- External storage generates a database based on the pulled binlog
- Reduce node data based on scalable storage mode
- Quickly recover node data

Assume that the node directory involved in the following operations is `~/fisco/nodes/127.0.0.1/node0`.

## Enable binlog

<font color="#FF0000">The binlog needs to be enabled when the chain is first started.</font> After the binlog is enabled, the data on the chain is written to the binlog before it is written to the cache. The binlog helps to improve the reliability of the cache, and at the same time provides a block-dimensional data operation record for external storage. The **binary_log** in [storage] of the node configuration is related to this.

- The configuration defaults to false, and WAL of RocksDB is turned on at this time;
- When set to true, the binlog is enabled, and WAL of RocksDB is turned off at this time;
- Each group of this configuration is independent, no consensus is needed, and the configuration takes effect when the node starts.

To use binlog, we modify the [storage] section in `~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini` file as follows, and restart the node.
```
[storage]
...
 ; set true to turn on binary log
 binary_log=true
...
```

After the binlog is enabled, the generated binlog file is located in the node's `~/fisco/nodes/127.0.0.1/node0/data/group1/BinaryLogs/` directory and is named after the height of the starting block it records.

```eval_rst
.. important::
    1. The binlog needs to be enabled when the chain is first started. It is not recommended to disable it after it has been enabled. If closed, some block information may be missing from external storage.

    2. It is not recommended to delete the binlog file. If you delete it, you need to make a backup and confirm that the block recorded in the deleted binlog file must be stored in external storage. If the binlog is deleted before it is stored in external storage, it may affect the subsequent status of the node. If this happens, delete the node's data and binlog file, and then synchronize the data from other nodes.
```

For the query method of the block height of external storage, please refer to [End of Article](./ data_governance.html#id9).

## External storage generates database based on the pulled binlog

This process is implemented with full data services. Please refer to the document for full data service.

## Reduce node data based on scalable storage mode

Using the scalable mode, the node's block data and state data are stored in different local databases. At the same time, a data proxy needs to access the remote mysql to query the locally reduced block data. The **type** and **scroll_threshold_multiple** in [storage] of the node configuration is related to this. Relevant configuration is independent of each group, and no consensus is needed. The read configuration takes effect at startup.

To use scalable mode, we modify the [storage] section in `~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini` file as follows. At the same time, we need to configure the data proxy. For the method of configuring the proxy, see [Here](./distributed_storage.html#amdb).

```
[storage]
    ; storage db type, rocksdb / mysql / external / scalable, rocksdb is recommended
    type = scalable
    ; set true to turn on binary log
    binary_log = true
    ; scroll_threshold = scroll_threshold_multiple * 1000, only for scalable
    scroll_threshold_multiple = 2
    ; only for external
    max_retry = 60
    topic = DB
...
```

After using the scalable storage mode, the `~/fisco/nodes/127.0.0.1/node0/data/group1/` directory of the nodes is organized as follows. The database in the blockDB directory is named after the height of the starting block of the record. The recorded content is block data and can be trimmed.

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
    Before trimming the blockDB, make sure that the trimmed data block is stored in external storage. If a node accesses data that has been trimmed and has not been stored in external storage, it will exit. If this happens, delete the node's data and binlog, and then synchronize the data from other nodes.
```

For the query method of the block height of external storage, please refer to [End of Article](./data_governance.html#id9).

## Quickly recover node data

To speed up the synchronization data progress of new nodes, FISCO-BCOS v2.2.0 provides the fisco-sync tool, which is used to pull the latest data of the specified block from external storage to node storage. After a new node starts to register as a group node, it obtains block data from other nodes in the group, and executes transactions in the block based on the latest state data of the new node, and participates in the group consensus after leveling the group block.

### Tool acquisition

The fisco-sync tool needs to pull the FISCO-BCOS source code and compile it by itself after turning on the compilation switch. <font color = "#FF0000">Remarks: After v2.2.0 tag release, users can directly pull the fisco-sync binary, no longer need to compile the source code by itself.</font>

- For the overall compilation process, please refer to [source compilation] (./ get_executable.html # id2);
- <font color = "#FF0000">Note</font>: The compilation switch involved in fisco-sync tool compilation is TOOL, which is OFF by default. You can enable this compilation switch by `cmake3 -DTOOL = ON ..`
- After compiling, the build path of the fisco-sync tool path is `build/bin/fisco-sync`.
  
```
$ git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
$ cd FISCO-BCOS
$ git checkout release-2.2.0
$ mkdir -p build && cd build
# CentOS Please use cmake3
$ cmake -DTOOL = ON ..
# High-performance machines can add -j4 to accelerate compilation with 4 cores
$ make
```

### Tool description

```bash
[app @ VM_centos node0] $ ./fisco-sync -h
fisco-sync version: 0.1.0
Build Time: 20191107 16:23:00
Commit Hash: 09d32e6b7f43d46fc70fbc8a35fea79c35e1f800
[2019-11-07 16:27:15] The sync-tool is Initializing ...
Usage of fisco-sync:
  -h [ --help ]                       print help information
  -c [ --config ] arg (=./config.ini) config file path, eg. config.ini
  -v [ --verify ] arg (=1000)         verify the number of blocks, the minimum is 100
  -l [ --limit ] arg (=10000)         page counts of table
  -s [ --sys_limit ] arg (=50)        page counts of system table
  -g [ --group ] arg (=1)             sync specific group
```
- **`h` option**, print out the option description;
- **`c` option**, used to specify the path of node configuration file `config.ini`, the default is the configuration file of this node;
- **`l` option**, which is used to specify the size of rows to be paged for other tables except for table _sys_hash_2_block_ and table _sys_block_2_nonces_. The default is 10,000 rows per page;
- **`s` option**, which is used to specify the size of rows to be paged for the table _sys_hash_2_block_ and table _sys_block_2_nonces_. The default is 50 rows per page;
- **`v` option**, used to specify the size of verification blocks (the size of blocks to be pulled by the original block synchronization), the default is 1000, and the minimum value is 100; for example: assuming the current external storage database block height is 3560 and v is set to 1000, which indicates that the fisco-sync tool pulls 2560(=3560-1000) blocks from the full data service, and the subsequent 1000(2561 to 3560) blocks will be pulled from other nodes;
- **`g` option**, used to specify the group ID, the default is 1.

### Configuration modification

- `type` in the [storage] section: The DB type of the storage. You can select rocksDB, mysql, and scalable, but you cannot select external. This type is used to access external storage. The rocksDB and mysql types pull the full amount of data, and the scalable types pull the clipped data.

### Preparation

- Set up and start the external storage service, the external storage has generated database based on the pulled binlog;
- Modify the node configuration, set up and start the data proxy service(amdb-proxy), please refer to [here](./distributed_storage.html#id16) for the operation method;
- Generate a new node according to [Document](./node_management.html#a), and copy the genesis and ini configuration to step 5;
- Compile the fisco-sync tool.

### Synchronous data

1. Copy fisco-sync to the new node directory `~/fisco/nodes/127.0.0.1/node0/`;
2. Modify group configuration, including storage mode and **topic**;
3. Specify the size of verification blocks and group ID, and start fisco-sync in the background. The fisco-sync log is written to nohup.out, and the corresponding data and log directories are generated after execution is completed;
```
nohup ./fisco-sync -v 400 -s 10 >> nohup.out 2> & 1 &
tail -f nohup.out
```
4. Without changing the group configuration, start the `fisco-bcos` of the new node to join a group. The new node performs the original block synchronization and the subsequent consensus process.

```eval_rst
.. important::
    The fisco-sync tool is expected to use on empty nodes. If a fisco-sync tool is used on a node that has stored data, it may affect the subsequent block synchronization and consensus process of the node. If this happens, you need to clear the node data and use fisco-sync again.
```

## Notes on external storage

- In this data governance solution, the external storage carrier is the [full data service <font color ="#FF0000">completion link</font>]()'s database,
- The block height query method of external storage: Login corresponding database and execute `select * from _sys_current_state_;`. The returned value minus one in the row whose key is `current_number` is the external storage block height.