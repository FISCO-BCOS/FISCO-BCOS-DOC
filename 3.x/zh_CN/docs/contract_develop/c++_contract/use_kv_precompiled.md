# 使用KV存储预编译合约开发应用

标签：``预编译合约`` ``CRUD`` ``区块链应用``

----------

本文将介绍 FISCO BCOS 3.x的KV存储功能，帮助开发者更高效便捷地开发区块链应用。

**特别注意：使用KV存储预编译合约的Solidity合约必须高于0.6.0版本，并且开启使用ABIEncoderV2**

## KV存储使用方法

目前可以用两种方式使用KV存储功能，分别是KVTable合约和 Java SDK KVTable Service接口。

### 1. KVTable合约

- Solidity合约只需要引入FISCO BCOS官方提供的Table.sol抽象接口合约文件即可。
- webankblockchain-liquid（以下简称WBC-Liquid）合约在实现合约之前对KVTable的接口进行声明使用即可。

Table包含分布式存储专用的智能合约接口，其接口实现在区块链节点，其中，TableManager可以创建专属KV表，KVTable可以用作表进行get/set操作。下面分别进行介绍。

#### 1.1 TableManager合约接口

用于创建KV表，打开KV表，其固定合约地址为`0x1002（Solidity）`和`/sys/table_manager（Liquid）`，接口如下（只展示与KVTable相关接口）：

| 接口                          | 功能       | 参数                                       | 返回值                                      |
|-------------------------------|------------|--------------------------------------------|---------------------------------------------|
| createKVTable(string ,string) | 创建表     | 表名，主键名（目前只支持单个主键），字段名 | 返回错误码（int32），错误码详见下表         |
| openTable(string)             | 获取表地址 | 表名，该接口只用于Solidity                 | 返回表对应的真实地址，如果不存在，则返回0x0 |

#### 1.2 KVTable合约

用于访问表数据，接口如下：

| 接口               | 功能   | 参数         | 返回值                                                    |
|--------------------|--------|--------------|-----------------------------------------------------------|
| get(string)        | 获取值 | 主键         | 返回bool值、string，如果查询失败，第一个返回值将会是false |
| set(string,string) | 设置值 | 主键，字段值 | 返回错误码（int32），成功时返回1，其余错误码详见下表      |

接口返回错误码：

| 错误码 | 说明                               |
|:-------|:-----------------------------------|
| 0      | 创建成功                           |
| -50001 | 创建表名已存在                     |
| -50002 | 表名超过48字符                     |
| -50003 | valueField字段名长度超过64字符     |
| -50004 | valueField总字段名长度超过1024字符 |
| -50005 | keyField字段值长度超过64字符       |
| -50006 | valueField字段值长度超过16兆       |
| -50007 | 存在重复字段                       |
| -50008 | 字段存在非法字符                   |
| 其他   | 创建时遇到的其他错误               |

有了以上对KVTable抽象接口合约的了解，现在可以正式进行KVTable合约的开发。

### 2. Solidity合约使用KVTable

#### 2.1 引入Table.sol

将Table.sol合约放入KVTableTest.sol同级目录，并在KVTableTest.sol合约文件中引入Table.sol，其代码如下：

```solidity
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

import "./Table.sol";
```

#### 2.2 创建表

在KVTableTest.sol合约文件中，创建表的核心代码如下：

```solidity
TableManager tm;
KVTable table;
string constant tableName = "t_kv_test";
event SetEvent(int256 count);
constructor () public{
  	// 创建TableManager对象，其在区块链上的固定地址是0x1002
    tm = TableManager(address(0x1002));

    // 创建t_kv_test表，表的主键名为id，其他字段名为item_name
    tm.createKVTable(tableName, "id", "item_name");

    // 获取真实的地址，存在合约中
    address t_address = tm.openTable(tableName);
    table = KVTable(t_address);
}
```

**注：** 这一步是可选操作：比如新合约只是读写旧合约创建的表，则不需创建表这步操作。

#### 2.3 针对表进行KV读写操作

在KVTableTest.sol合约文件中，写入记录核心代码如下：

``` solidity
function set(string memory id, string memory item_name)
public
returns (int32)
{
    int32 result = table.set(id,item_name);
    emit SetEvent(result);
    return result;
}
```

读数据记录核心代码如下：

```solidity
function get(string memory id) public view returns (bool, string memory) {
    bool ok = false;
    string memory value;
    (ok, value) = table.get(id);
    return (ok, value);
}
```

### 3. WBC-Liquid合约使用KVTable接口

#### 3.1 声明KVTable接口

在WBC-Liquid合约中使用接口之前先对KVTable的接口进行声明。

```rust
#![cfg_attr(not(feature = "std"), no_std)]

use liquid::storage;
use liquid_lang as liquid;
use liquid_lang::InOut;
use liquid_prelude::{string::String, vec::Vec};

#[derive(InOut)]
pub struct TableInfo {
    key_column: String,
    value_columns: Vec<String>,
}

// TableManager的接口声明
#[liquid::interface(name = auto)]
mod table_manager {
    use super::*;

    extern "liquid" {
        fn createKVTable(
            &mut self,
            table_name: String,
            key: String,
            value_fields: String,
        ) -> i32;
        fn desc(&self, table_name: String) -> TableInfo;
    }
}
// KVTable的接口声明
#[liquid::interface(name = auto)]
mod kv_table {

    extern "liquid" {
        fn get(&self, key: String) -> (bool, String);
        fn set(&mut self, key: String, value: String) -> i32;
    }
}
```

#### 3.2 WBC-Liquid创建表

可在WBC-Liquid的构造函数中实现创建表的逻辑，此处引入的TableManager的地址为BFS路径 `/sys/table_manager` ，注意WBC-Liquid和Solidity的区别。

创建表的原理与Solidity的类似，再次不再赘述。

```rust
pub fn new(&mut self) {
    self.table_name.initialize(String::from("t_kv_test"));
  	// TableManager的固定地址为/sys/table_manager
    self.tm
        .initialize(TableManager::at("/sys/table_manager".parse().unwrap()));
    self.tm.createKVTable(
        self.table_name.clone(),
        String::from("id"),
        String::from("item_name"),
    );
  	// 创建成功后，/tables/+创建的表名就是合约的正确地址
    self.table
        .initialize(KvTable::at("/tables/t_kv_test".parse().unwrap()));
}
```

#### 3.3 针对表进行KV读写操作

写的主要逻辑如下：

```rust
pub fn set(&mut self, id: String, item_name: String) -> i32 {
    let count = (*self.table).set(id, item_name).unwrap();

    self.env().emit(SetEvent {
        count: count.clone(),
    });
    count
}
```

读的主要逻辑如下：

```rust
pub fn get(&self, id: String) -> (bool, String) {
    if let Some((ok, value)) = (*self.table).get(id) {
        return (ok, value);
    }
    return (false, Default::default());
}
```

### 4. SDK KVTable Service接口

FISCO BCOS 3.x SDK提供KVTable Service数据上链接口，这些接口实现的原理是调用区块链内置的一个预编译的KVTable合约，专门负责对用户表进行读写操作。Java SDK KVTable Service实现在org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTableService 类，其接口如下：

| 接口                                | 功能         | 参数                 | 返回值                   |
|-------------------------------------|--------------|----------------------|--------------------------|
| createTable(String, String, String) | 创建表       | 表名、主键名、字段名 | 错误码                   |
| set(String, String, String)         | 写数据       | 表名、主键名、字段值 | 错误码                   |
| get(String, String)                 | 读数据       | 表名、主键名         | String                   |
| desc(String)                        | 查询表的信息 | 表名                 | 表的keyField和valueField |

其中调用写接口会产生与调用KV合约接口等效的交易，需要共识节点共识一致后才会落盘存储。
