# 系统表

## 表结构
表中的所有`entry`，都会有`_status_`,`_num_`,`_hash_`字段。

### 用户表
用户合约所创建的表，以`_user_<TableName>`为表名，底层自动添加`_user_`前缀。

### 系统表

系统表默认存在，由存储驱动保证系统表的创建。
1. 底层平台所使用的表

|表名|keyField|valueField|key的取值|权限控制是否生效|表存储数据说明|
|:---|:------|:---------|:----|:---|:----|
|`_sys_tables_`|table_name|key_field value_field|所有表的表名|是|存储所有表的结构，以表名为主键|
|`_sys_miners_`|name|type,node_id enable_num|node|是|存储共识节点和观察节点的列表|
|`_sys_current_state_`|key|value|current_number total_current_transaction_count|否|存储最新的状态|
|`_sys_tx_hash_2_block_`|hash|value,index|交易hash的16进制|否|存储交易hash到区块号的映射|
|`_sys_number_2_hash_`|hash|value|区块高|否|存储区块号到区块头hash的16进制表示的映射|
|`_sys_hash_2_block_`|key|value|区块头hash的16进制|否|存储hash到序列化的区块数据|

2. 权限控制表

3. storagestate模式下的合约表
`_contract_data_`+`Address`作为表名。

|key|value|说明|
|:---|:---|:---|
|balance|value||
|codeHash|value|合约代码的hash|
|code|value|合约代码|
|nonce|value|账户nonce|
|alive|value||