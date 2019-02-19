# AMDB

分布式存储（Advanced Mass Database，AMDB）通过对表结构的设计，既可以对应到关系型数据库的表，又可以拆分使用KV数据库存储。通过实现对应于不通数据库的存储驱动，AMDB理论上可以支持所有关系型和KV的数据库。

- CRUD数据、区块数据和合约代码数据存储默认情况下都保存在AMDB，无需配置，合约局部变量存储可根据需要配置为MPTState或StorageState，无论配置哪种State，合约代码都不需要变动。
- 当使用MPTState时，合约局部变量保存在MPT树中。当使用StorageState时，合约局部变量保存在AMDB表中。
- 尽管MPTState和AMDB最终数据都会写向LevelDB，但二者使用不同的LevelDB实例，没有事务性，因此当配置成使用MPTState时，提交数据时异常可能导致两个LevelDB数据不一致。

## 名词解释

- Entry，对应于表中的一行，每行以列名作为key，对应的值作为value，构成KV结构。每个Entry拥有自己的AMDB主key，不同Entry允许拥有相同的AMDB主key。
- Entries，Entries中存放主Key相同的Entry，数组。AMDB的主Key与Mysql中的主key不同，AMDB主key用于标示Entry属于哪个key，相同key的Entry会存放在同一个Entries中。
- Table，存储表中的所有数据，KV结构，kV由AMDB主key和Entries对象构成。Table中存储AMDB主key到对应Entries的映射。可以基于AMDB主key进行增删改查，支持条件筛选。

## AMDB表分类

表中的所有`entry`，都会有`_status_`,`_num_`,`_hash_`内置字段。

### 系统表

系统表默认存在，由存储驱动保证系统表的创建。

```eval_rst
======================== =========== ======================== ========================================= ====================================== 
表名                      keyField    valueField               存储数据说明                                AMDB主key                              
======================== =========== ======================== ========================================= ====================================== 
`_sys_tables_`           table_name  key_field value_field    存储所有表的结构，以表名为主键                 所有表的表名                             
`_sys_consensus_`        name        type,node_id enable_num  存储共识节点和观察节点的列表                   node                                   
`_sys_current_state_`    key         value                    存储最新的状态                               current_number/total_transaction_count 
`_sys_tx_hash_2_block_`  hash        value,index              存储交易hash到区块号的映射                    交易hash的16进制                        
`_sys_number_2_hash_`    hash        value                    存储区块号到区块头hash的16进制表示的映射        区块高                                 
`_sys_hash_2_block_`     key         value                    存储hash到序列化的区块数据                    区块头hash的16进制                      
======================== =========== ======================== ========================================= ====================================== 
```

### 用户表

用户CRUD合约所创建的表，以`_user_<TableName>`为表名，底层自动添加`_user_`前缀。

### StorageState合约表

`_contract_data_`+`Address`+`_`作为表名。表中存储外部账户相关信息。

# StorageState

StorageState是基于AMDB实现的存储账户状态的方式，相比于MPTState去掉了MPT树，每个账户会有一个AMDB的Table来存储其相关数据。包括账户的`nonce`,`code`,`balance`等内容。
