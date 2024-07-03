# 4. Storage read and write tools

Tags: "storage" "read and write tools" "data read"

----------

## 简介

The storage read / write tool is used to read and write storage data and supports the following functions:

- Read a single key in a table
- Read full table data
- Modify a single KV in a table
- Compare data of two nodes
- Statistics of state data and chain data in the node database

## 使用

The tool needs to set the '-DTOOLS = on' compilation option when compiling the source code. The tool needs to run under the node directory to read the configuration file of the node。

```bash
# Open TOOLS option at compile time
cmake -DTOOLS=on ..
# The compiled tools are located in build / tools / storage-tool / storageTool
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

### Read a single piece of data

'-r 'option is used to read a single piece of data in the table, the parameter is' [table name] [key] '。Can be used in conjunction with the '-H' option, which will use the key in the parameter to read data after hex decoding。Examples are as follows:

```bash
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
[tableName=s_current_state] [key=current_number] [value=1711]
```

### Traverse Table

The '-i' option is used to traverse the data in the table. The parameter is' [table name] '. The data in the table is written to the' [table name] .txt 'file in the current directory。Can be used in conjunction with the '-H' option, which hex encodes the read data for easy viewing of binary data。Examples are as follows:

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

### Modify data

The '-w' option is used to modify a piece of data in the table. The parameter is' [table name] [key] [value] '. If the value is an empty string, the data is deleted. It can be used in combination with the' -H 'option. The' -H 'option will decode the value in the parameter using hex and write it。Examples are as follows:

```bash
# Read
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
[tableName=s_current_state] [key=current_number] [value=1711]

# Write
$ ./storageTool -w s_current_state current_number 2000
config file     : ./config.ini
genesis file    : ./config.genesis
set successfully

# Verify
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
[tableName=s_current_state] [key=current_number] [value=2000]

# Delete
$ ./storageTool -w s_current_state current_number ""
config file     : ./config.ini
genesis file    : ./config.genesis
set successfully

# Verify
$ ./storageTool -r s_current_state current_number
config file     : ./config.ini
genesis file    : ./config.genesis
read s_current_state, key is current_number
get row not found,,table=s_current_state,key=current_number
```

### statistical function

'-s' option is used in statistical storage**Non-state data**The size of the storage occupied, the '-S' option is used for statistical storage**Status Data**Size of storage occupied。The statistical result is the size of the data in memory, and the sum will be larger than the actual size of RocksDB, because RocksDB will have compression。Examples are as follows:

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

### Contrast function

The '-C' option is used to compare the data of two nodes, which will be compared table by table, where the difference of 'importTime' is ignored during transaction comparison, and the difference of signature list is ignored during block header comparison。Examples are as follows:

```bash
$ ./storageTool -C rocksdb ../node1/data/
config file     : ./config.ini
genesis file    : ./config.genesis
remoteDBPath:../node1/data/
table u_/tables/t_test is equall equalequall2ea76fe2 is equal
compare data success, all data is same
```
