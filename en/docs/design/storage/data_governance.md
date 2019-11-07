# data governance

With the growth of business volume, chain data occupies more and more hard disk storage, and at the same time, the processing capacity of nodes has declined to some extent. To make the business side have a good user experience, we introduce a data governance solution from both node storage and external storage. The program has the following goals:

1. Node storage stabilizes online transaction performance by reducing the amount of local data;
2. External storage records historical data and expanded capacity by exporting node data.

The realization of the above goals involves the following core mechanisms on node level:

- binlog
- scalable storage mode
- Snapshot

## Data export implementation————binlog

The binlog provides a block-dimensional record of data manipulation history for external storage. Dates are written to binlog files before it is written to the cache after binlog enabled.

![](../../../images/storage/storage_architecture.png)

Binlog encodes the block data in a TLV manner and writes it to files. The encoded field content and its length are described in the following figure:

![](../../../images/storage/binlog_TLV.png)

- `_id_` and other black labels, integer type, the number of bytes consumed is recorded above the field;
- `tableName` and other blue labels, string type, and bytes consumed are calculated based on the actual length of the field string;
- `entries` and other yellow labels, object array type, the previous count field records the size of objects;
- Unified network order(big-endian mode);
- `version` used for version control;
- `CRC32` records the summary of `_num_`, `dataCount` and `datas`;
- `blockLength` is the sum of the lengths of `_num_`, `dataCount`, `datas`, and `CRC32`;
- The size of a binlog file is not greater than 128M.

External storage currently supports the mysql database. Node data and its manipulation history will be restored and archived based on the pulled binlog files. We take the configuration of the blockchain system below as an example. It can be known that tx_count_limit is updated at block heights 2 and 3 and tx_gas_limit is updated at block height 5. The latest values of the two configurations are 15000 and 500000000 respectively.

```sql
select * from _sys_config_;
+------+--------+----------+-------+----------------+-----------+------------+
| _id_ | _hash_ | _status_ | _num_ | key            | value     | enable_num |
+------+--------+----------+-------+----------------+-----------+------------+
|    2 | 0x00   |        0 |     3 | tx_count_limit | 15000     | 3          |
|    3 | 0x00   |        0 |     5 | tx_gas_limit   | 500000000 | 5          |
+------+--------+----------+-------+----------------+-----------+------------+
2 rows in set (0.02 sec)

select * from _sys_config_d_;
+-------+------+--------+----------+-------+----------------+-----------+------------+
| pk_id | _id_ | _hash_ | _status_ | _num_ | key            | value     | enable_num |
+-------+------+--------+----------+-------+----------------+-----------+------------+
|     1 |    2 | 0x00   |        0 |     0 | tx_count_limit | 1000      | 0          |
|     2 |    3 | 0x00   |        0 |     0 | tx_gas_limit   | 300000000 | 0          |
|     3 |    2 | 0x00   |        0 |     2 | tx_count_limit | 10000     | 2          |
|     4 |    2 | 0x00   |        0 |     3 | tx_count_limit | 15000     | 3          |
|     5 |    3 | 0x00   |        0 |     5 | tx_gas_limit   | 500000000 | 5          |
+-------+------+--------+----------+-------+----------------+-----------+------------+
5 rows in set (0.02 sec)
```

## Local data management————scalable storage mode

To reduce the amount of local data, the data governance solution trims the local related data after the data is synchronized to the external storage for archiving through the binlog file. To support the reducing operation of local data, this solution adds scalable storage mode to the original storage mode, and stores local data in different instances.

In scalable mode, the node's block data and state data are stored in multiple local rocksDB databases. At the same time, a data proxy needs to be configured to access the externally stored mysql database for querying locally cropped block data.

![](../../../images/storage/scalable.png)

**archive Storage**

- The Storage holds one or more databases and store data of two tables `_sys_hash_2_block_` and` _sys_block_2_nonces_`;
- When the node restarts or the block height reaches threshold configured, local storage will split the new database; a data will be routed to a specific database, depending on the write block height of that data;
-The storage database can be deleted, but the object of the delete operation is the entire database instead of some keys in the database.

**state Storage**

- Store other tables except table `_sys_hash_2_block_` and` _sys_block_2_nonces_`.

**remote Storage**

- The Storage store archives the node data and accesses it through a data agent. It can be used to query the locally cropped block data.
- When the node is started, a threshold will be determined according to local storage data. Subsequent query requests will determine whether to access local storage or remote storage based on the block height of the query and the aforementioned threshold.

## Quickly recover data————Snapshot

In addition to providing query support for the local cropped node's data, the externally stored data can also provide a snapshot at a specific block height, which is used by new nodes to quickly synchronize data on the chain by pulling snapshot.

![](../../../images/storage/fast_sync.png)

1. The node accesses external storage through the configuration data proxy to pull a snapshot at a specific height, which is stored in the rocksDB database or the mysql database;
2. The node synchronizes the subsequent 1000 blocks based on the snapshot information and plays a certain verification role by replaying the transactions in the above blocks.