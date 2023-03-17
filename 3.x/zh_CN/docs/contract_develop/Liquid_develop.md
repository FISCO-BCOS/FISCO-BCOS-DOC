# 2. WBC-Liquid合约开发

标签：``开发第一个应用`` ``WBC-Liquid`` ``合约开发`` ``区块链应用`` ``WASM``

---
除支持Soldity语言与C++之外，FISCO BCOS也支持用Rust写合约。WBC-Liquid是微众区块链开发的基于Rust的智能合约编程语言，借助Rust语言特性，能够实现比Solidity语言更强大的编程功能。

- 相关文档：[Liquid 在线文档](https://liquid-doc.readthedocs.io/zh_CN/latest/)
- 相关文档：[Rust语言官方教程](https://doc.rust-lang.org/book/)
- 相关文档：[开发第一个WBC_Liquid区块链应用](../quick_start/wbc_liquid_application.md)

下面以KV存储为例，说明Liquid合约开发使用方式。

## KV存储使用方法

目前可以用两种方式使用KV存储功能，分别是KVTable合约和 Java SDK KVTable Service接口。

### 1. KVTable合约

- Solidity合约只需要引入FISCO BCOS官方提供的Table.sol抽象接口合约文件即可。

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
### 2. WBC-Liquid合约使用KVTable接口

#### 2.1 声明KVTable接口

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

#### 2.2 WBC-Liquid创建表

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

#### 2.3 针对表进行KV读写操作

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

### 3. SDK KVTable Service接口

FISCO BCOS 3.x SDK提供KVTable Service数据上链接口，这些接口实现的原理是调用区块链内置的一个预编译的KVTable合约，专门负责对用户表进行读写操作。Java SDK KVTable Service实现在org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTableService 类，其接口如下：

| 接口                                | 功能         | 参数                 | 返回值                   |
|-------------------------------------|--------------|----------------------|--------------------------|
| createTable(String, String, String) | 创建表       | 表名、主键名、字段名 | 错误码                   |
| set(String, String, String)         | 写数据       | 表名、主键名、字段值 | 错误码                   |
| get(String, String)                 | 读数据       | 表名、主键名         | String                   |
| desc(String)                        | 查询表的信息 | 表名                 | 表的keyField和valueField |

其中调用写接口会产生与调用KV合约接口等效的交易，需要共识节点共识一致后才会落盘存储。

