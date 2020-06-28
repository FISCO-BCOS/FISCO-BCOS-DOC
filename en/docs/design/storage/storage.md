# AMDB

Distributed storage (Advanced Mass Database, AMDB) can adapt to relational database or split to KV database through design on table structure. Theoretically AMDB supports any relational and KV databases by realizing the storage drive for different database.

- CRUD data and block data  are defaulted to be stored in AMDB without configuration. Local variables of contract can be configured as MPTState or StorageState if necessary. But contract code remains the same no matter what kind of state.
- When it's MPTState, contract local variables are stored in MPT tree. When it's StorageState, contract local variables are stored in AMDB table.
- Although data of MPTState AMDB will all be written to RocksDB finally, but they use different RocksDB instances with no transactional characteristics. Therefore, when it's configured to use MPTState, exceptions during committing data may lead to difference in data of the 2 RocksDB.

## Terms definition

### Table

Store data in storage table; store mapping of primary key of AMDB to Entries; support CRUD operations based on AMDB primary key; support filtering.

### Entries

Store Entry, array same with primary key; different from the primary key in Mysql, AMDB primary key is used to signify which key the Entry belongs to; Entry of the same key will be stored in one Entries.

### Entry

a row in a table; entry name as the key and entry value as the value to form KV structure; each entry owns a AMDB primary key; different entry can have the same AMDB primary key.

### Condition

RUD APIs of Table support inputing of condition, the APIs will return filtered result according to the conditions; if the condition is empty, there won't be filtering.

### Example

To illustrate the above terms in example, here is a procurement form of office supplies in a company.

|Name*|item_id|item_name|
|:--|:---|:---|
|Alice|1001001|laptop|
|Alice|1001002|screen|
|Bob|1002001|macbook|
|Chris|1003001|PC|

Explanation
- In this table **Name** is the AMDB primary key.
- Each row is an Entry. There are 4 Entries which store data by Map:
    + Entry1：`{Name:Alice，item_id:1001001,item_name:laptop}`
    + Entry2：`{Name:Alice，item_id:1001002,item_name:screen}`
    + Entry3：`{Name:Bob，item_id:1002001,item_name:macbook}`
    + Entry4：`{Name:Chris，item_id:1003001,item_name:PC}`
- In the Table **Name** is the primary key with 3 Entries objects. The first Entries stores 2 records of Alice; the second Entries stores one record of Bob; the third Entries stores one record of Chris.
- When calling retrieve API of Table, the API should set AMDB primary key and condition. Set AMDB primary key as Alice, condition as `item_id = 1001001`, it will retrieve Entry1.

## Types of AMDB table

Each `entry` of table contains built-in fields of `_status_`, `_num_`, `_hash_`.

### System table

The default system table is created in the promise of storage drive.

|table name                   |  keyField  | valueField            |  storage description                            |  AMDB primary key                              |
|:--------|:--------|:--------|:--------|:--------|
|`_sys_tables_`         | table_name |key_field,value_field  | store structures of all tables, table name being the primary key           |    tale name of all tables                         |    
|`_sys_consensus_`      | name       |type,node_id,enable_num| store lists of consensus nodes and observer nodes             |    node                                 |  
|`_sys_table_access_`   | table_name |address,enable_num     | store exterior account addresses with writing permission of each table     |     table name                            |       
|`_sys_cns_`            | name       |version,address,abi    | store CNS mapping relation                          | contract name                                  |
|`_sys_config_`         | key        |value,enable_num       | store group config items for consensus                |   config items                                |   
|`_sys_current_state_`  | key        |value                  | store the latest status                           |  current_number/total_transaction_count |
|`_sys_tx_hash_2_block_`| hash       |value,index            | store map of transaction hash to block number               |   hexadecimal of transaction hash                      |  
|`_sys_number_2_hash_`  | hash       |value                  | store map of block number to block head hash in hexadecimal |     block number                              |   
|`_sys_hash_2_block_`   | key        |value                  | store block data hash to sequential              |   block head hash in hexadecimal                    |  
|`_sys_block_2_nonces_` | number     |value                  | store nonces of transaction in block                   |  block number                      |

### User table

Table created when user calls CRUD APIs, from v2.2.0 use `u_<TableName>` as table name, prefixed with `u_` in bottom layer.

### StorageState account table

From v2.2.0 `c_`+`Address` is table name. The table stores exterior accounts information. Table structure:

|key*|value|
|:---|:---|
|balance||
|nonce||
|code||
|codeHash||
|alive||

# StorageState

StorageState is a method to store account status by AMDB. It has following difference with MPTState:

|      |StorageState|MPTState|
|:-------|:------|:--------|
|organization method of account data|AMDB table|MPT table|
|historical status|not support/maintain historical status|support|

Every account in MPTState uses MPT tree to store data. As historical data increases, it will lead to low performance due to storage method and disk IO. Every account in StorageState is related to one table and stores its data, including `nonce`, `code`, `balance` of the account. AMDB can improve performance by supporting different databases through their storage drives. We have found in RocksDB test that StorageState is twice as much as MPTState in performance.

