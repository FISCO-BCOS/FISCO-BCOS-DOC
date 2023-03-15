# 存储读写工具

标签：``存储`` ``读写工具`` ``数据读取``

----------

## 简介

存储读写工具用于读取写入存储数据，支持下述功能：

- 读取表中的单个key
- 读取全表数据
- 修改表中的单个KV
- 比较两个节点的数据
- 统计节点数据库中状态数据、链数据大小

## 使用

工具需要在编译源码时设置`-DTOOLS=on`编译选项，工具需要在节点目录下运行以读取节点的配置文件。

```bash
# 编译时打开TOOLS选项
cmake -DTOOLS=on ..
# 编译得到的工具位于 build/tools/storage-tool/storageTool
$ ./tools/storage-tool/storageTool  -h
storage tool used to read/write the data of storage:
  -h [ --help ]                         help of storage tool
  -s [ --statistic ]                    statistic the data usage of the storage
  -S [ --stateSize ]                    statistic the data usage of the
                                        contracts state
  -r [ --read ] arg                     [TableName] [Key]
  -w [ --write ] arg                    [TableName] [Key] [Value]
  -i [ --iterate ] arg                  [TableName]
  -H [ --hex ] arg (=0)                 if read display value use hex, if write
                                        decode hex value
  -C [ --compare ] arg                  [RocksDB] [path] or [TiKV] [pd
                                        addresses] [ca path if use ssl] [cert
                                        path if use ssl] [key path if use ssl]
  -c [ --config ] arg (=./config.ini)   config file path
  -g [ --genesis ] arg (=./config.genesis)
                                        genesis config file path
```

### 读取单条数据

`-r`选项用于读取表中的单条数据，参数为`[表名][键]`。可以结合`-H`选项使用，`-H`选项将会将参数中的key使用hex解码后用于读取数据。示例如下：

```bash
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
[tableName=s_current_state] [key=current_number] [value=1711]
```

### 遍历表

`-i`选项用于遍历表中的数据，参数为`[表名]`，表中的数据会写入当前目录下`[表名].txt`的文件中，如果表名中有`/`则会被替换为下划线。可以结合`-H`选项使用，`-H`选项将会将读取到的数据hex编码，以便于查看二进制数据。示例如下：

```bash
$ ./storageTool -i s_current_state
config file     : ./config.ini
genesis file    : ./config.genesis
result in ./s_current_state.txt

$ cat s_current_state.txt
db path : data, table : s_current_state
[key=s_current_state:current_number] [value=1711]
[key=s_current_state:total_failed_transaction_count] [value=1001]
[key=s_current_state:total_transaction_count] [value=44235]
```

### 修改数据

`-w`选项用于修改表中的某条数据，参数为`[表名][键][值]`，如果值为空字符串则删除此数据，可以结合`-H`选项使用，`-H`选项将会将参数中的值使用hex解码后写入。示例如下：

```bash
# 读取
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
[tableName=s_current_state] [key=current_number] [value=1711]

# 写入
$ ./storageTool -w s_current_state current_number 2000
config file     : ./config.ini
genesis file    : ./config.genesis
set successfully

# 验证
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
[tableName=s_current_state] [key=current_number] [value=2000]

# 删除
$ ./storageTool -w s_current_state current_number ""
config file     : ./config.ini
genesis file    : ./config.genesis
set successfully

# 验证
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
get row not found,,table=s_current_state,key=current_number
```

### 统计功能

`-s`选项用于统计存储中**非状态数据**所占用的存储大小，`-S`选项用于统计存储中**状态数据**所占用的存储大小。统计结果是数据在内存中的大小，总和会比RocksDB实际大小更大，原因是RocksDB会有压缩。示例如下：

```bash
$ du -sh ./data/
50G     ./data/

$ ./storageTool -s -S
config file     : ./config.ini
genesis file    : ./config.genesis
s_tables                       size is 661.029MB
s_consensus                    size is 794B
s_config                       size is 210B
s_current_state                size is 137B
s_hash_2_number                size is 53.1716MB
s_number_2_hash                size is 53.1716MB
s_block_number_2_nonces        size is 33.4512MB
s_number_2_txs                 size is 4.94278GB
s_number_2_header              size is 671.36MB
s_hash_2_tx                    size is 46.5201GB
s_hash_2_receipt               size is 16.5311GB
/apps                          size is 40.3474GB
```

### 对比功能

`-C`选项用于比较两个节点的数据，会逐个表比较，其中交易比较时忽略了`importTime`的差异，区块头比较时忽略了签名列表的差异。示例如下：

```bash
$ ./storageTool -C rocksdb ../node1/data/
config file     : ./config.ini
genesis file    : ./config.genesis
remoteDBPath:../node1/data/
table u_/tables/t_test is equall equalequall2ea76fe2 is equal
compare data success, all data is same
```
