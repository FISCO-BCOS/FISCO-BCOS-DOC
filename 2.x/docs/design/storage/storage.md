# AMDB

标签：``存储模块`` ``分布式存储`` ``合约数据`` ``状态数据`` 

----

分布式存储（Advanced Mass Database，AMDB）通过对表结构的设计，既可以对应到关系型数据库的表，又可以拆分使用KV数据库存储。通过实现对应于不同数据库的存储驱动，AMDB理论上可以支持所有关系型和KV的数据库。

- CRUD数据、区块数据默认情况下都保存在AMDB，无需配置，合约局部变量存储可根据需要配置为MPTState或StorageState，无论配置哪种State，合约代码都不需要变动。
- 当使用MPTState时，合约局部变量保存在MPT树中。当使用StorageState时，合约局部变量保存在AMDB表中。
- 尽管MPTState和AMDB最终数据都会写向RocksDB，但二者使用不同的RocksDB实例，没有事务性，因此当配置成使用MPTState时，提交数据时异常可能导致两个RocksDB数据不一致。

## 名词解释

### Table

存储表中的所有数据。Table中存储AMDB主key到对应Entries的映射，可以基于AMDB主key进行增删改查，支持条件筛选。

### Entries

Entries中存放主Key相同的Entry，数组。AMDB的主Key与Mysql中的主key不同，AMDB主key用于标示Entry属于哪个key，相同key的Entry会存放在同一个Entries中。

### Entry

对应于表中的一行，每行以列名作为key，对应的值作为value，构成KV结构。每个Entry拥有自己的AMDB主key，不同Entry允许拥有相同的AMDB主key。

### Condition

Table中的删改查接口支持传入条件，这三种接口会返回根据条件筛选后的结果。如果条件为空，则不做任何筛选。

数据更新或者插入过程中，需要根据主Key获取数据并将对数据更新或者append操作，然后再写回存储系统。因此，在一个主Key对应的Entries中Entry个数很多的时候，执行效率会受到影响；同时，会加大内存的使用。所以，实际生产过程中主Key对应的Entries中Entry个数不宜过多。

### 举例

以某公司员工领用物资登记表为例，解释上述名词。

|Name*|item_id|item_name|
|:--|:---|:---|
|Alice|1001001|laptop|
|Alice|1001002|screen|
|Bob|1002001|macbook|
|Chris|1003001|PC|

解释如下：
- 表中**Name**是AMDB主key。
- 表中的每一行为一个Entry。一共有4个Entry，每个Entry以Map存储数据。4个Entry如下：
    + Entry1：`{Name:Alice，item_id:1001001,item_name:laptop}`
    + Entry2：`{Name:Alice，item_id:1001002,item_name:screen}`
    + Entry3：`{Name:Bob，item_id:1002001,item_name:macbook}`
    + Entry4：`{Name:Chris，item_id:1003001,item_name:PC}`
- Table中以**Name**为主key，存有3个Entries对象。第1个Entries中存有Alice的2条记录，第2个Entries中存有Bob的1条记录，第3个Entries中存有Chris的一条记录。
- 调用Table类的查询接口时，查接口需要指定AMDB主key和条件，设置查询的AMDB主key为Alice，条件为`item_id = 1001001`，会查询出Entry1。

## AMDB表分类

表中的所有`entry`，都会有`_status_`,`_num_`,`_hash_`内置字段。

### 系统表

系统表默认存在，由存储驱动保证系统表的创建。

|表名                   |  keyField  | valueField            |  存储数据说明                            |  AMDB主key                              |
|:--------|:--------|:--------|:--------|:--------|
|`_sys_tables_`         | table_name |key_field,value_field  | 存储所有表的结构，以表名为主键           |    所有表的表名                         |    
|`_sys_consensus_`      | name       |type,node_id,enable_num| 存储共识节点和观察节点的列表             |    node                                 |  
|`_sys_table_access_`   | table_name |address,enable_num     | 存储每个表的具有写权限的外部账户地址     |     表的表名                            |       
|`_sys_cns_`            | name       |version,address,abi    | 存储CNS映射关系                          | 合约名                                  | 
|`_sys_config_`         | key        |value,enable_num       | 存储需要共识的群组配置项                 |   配置项                                |   
|`_sys_current_state_`  | key        |value                  | 存储最新的状态                           |  current_number/total_transaction_count |
|`_sys_tx_hash_2_block_`| hash       |value,index            | 存储交易hash到区块号的映射               |   交易hash的16进制                      |  
|`_sys_number_2_hash_`  | hash       |value                  | 存储区块号到区块头hash的16进制表示的映射 |     区块高                              |   
|`_sys_hash_2_block_`   | key        |value                  | 存储hash到序列化的区块数据               |   区块头hash的16进制                    |  
|`_sys_block_2_nonces_` | number     |value                  | 存储区块中交易的nonces                   |  区块高                      |

### 用户表

用户调用CRUD接口所创建的表，从2.2版本开始以`u_<TableName>`为表名，底层自动添加`u_`前缀。

### StorageState账户表

从2.2版本开始以`c_`+`Address`作为表名。表中存储外部账户相关信息。表结构如下

|key*|value|
|:---|:---|
|balance||
|nonce||
|code||
|codeHash||
|alive||

# StorageState

StorageState是一种使用AMDB实现的存储账户状态的方式。相比于MPTState主要有以下区别：

|      |StorageState|MPTState|
|:-------|:------|:--------|
|账户数据组织方式|AMDB表|MPT树|
|历史状态|不支持，不维护历史状态|支持|

MPTState每个账户使用MPT树存储其数据，当历史数据逐渐增多时，会因为存储方式和磁盘IO导致性能问题。StorageState每个账户对应一个Table存储其相关数据，包括账户的`nonce`,`code`,`balance`等内容，而AMDB可以通过实现对应的存储驱动支持不同的数据库以提高性能，我们使用RocksDB测试发现，StorageState性能大约是MPTState的两倍。
