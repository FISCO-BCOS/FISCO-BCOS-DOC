# AMDB

分布式存储（Advanced Mass Database，AMDB）通过对表结构的设计，既可以对应到关系型数据库的表，又可以拆分使用KV数据库存储。通过实现对应于不通数据库的存储驱动，AMDB理论上可以支持所有关系型和KV的数据库。

## 名词解释

- Entry，对应于表中的一行，每行以列名作为key，对应的值作为value，构成KV结构。每个Entry拥有自己的AMDB主key，不同Entry允许拥有相同的AMDB主key。
- Entries，Entries中存放主Key相同的Entry，数组。AMDB的主Key与Mysql中的主key不同，AMDB主key用于标示Entry属于哪个key，相同key的Entry会存放在同一个Entries中。
- Table，存储表中的所有数据，KV结构，kV由AMDB主key和Entries对象构成。

## 一个例子

![](../../../images/storage/example.png)

- 表格中每一行就是一个Entry，每个Entry里面存储列名到值的映射。例如Entry0中就会存储`{_id_:0,_hash_:"...",_status_:0,name:小明,address:南山,,age:18}`，其中"..."表示hash的缩写。
- 以address作为AMDB的主key。
- Entry0和Entry2的AMDB主key相同，所以会放在同一个Entries中。
- 整个表构成一个AMDB的Table，Table中存储AMDB主key到对应Entries的映射。可以基于AMDB主key进行增删改查，支持条件筛选。

## AMDB系统表

### 表结构
表中的所有`entry`，都会有`_status_`,`_num_`,`_hash_`字段。

### 用户表
用户合约所创建的表，以`_user_<TableName>`为表名，底层自动添加`_user_`前缀。

### 系统表

系统表默认存在，由存储驱动保证系统表的创建。
1. 底层平台所使用的表

| 表名                    | keyField   | valueField              | key的取值                                      | 权限控制是否生效 | 表存储数据说明                           |
| :---------------------- | :--------- | :---------------------- | :--------------------------------------------- | :--------------- | :--------------------------------------- |
| `_sys_tables_`          | table_name | key_field value_field   | 所有表的表名                                   | 是               | 存储所有表的结构，以表名为主键           |
| `_sys_miners_`          | name       | type,node_id enable_num | node                                           | 是               | 存储共识节点和观察节点的列表             |
| `_sys_current_state_`   | key        | value                   | current_number total_current_transaction_count | 否               | 存储最新的状态                           |
| `_sys_tx_hash_2_block_` | hash       | value,index             | 交易hash的16进制                               | 否               | 存储交易hash到区块号的映射               |
| `_sys_number_2_hash_`   | hash       | value                   | 区块高                                         | 否               | 存储区块号到区块头hash的16进制表示的映射 |
| `_sys_hash_2_block_`    | key        | value                   | 区块头hash的16进制                             | 否               | 存储hash到序列化的区块数据               |

2. 权限控制表

3. storagestate模式下的合约表
`_contract_data_`+`Address`作为表名。

| key      | value | 说明           |
| :------- | :---- | :------------- |
| balance  | value |
| codeHash | value | 合约代码的hash |
| code     | value | 合约代码       |
| nonce    | value | 账户nonce      |
| alive    | value |

# StorageState

StorageState是基于AMDB实现的存储账户状态的方式，相比于MPTState去掉了MPT树，每个账户会有一个AMDB的Table来存储其相关数据。

