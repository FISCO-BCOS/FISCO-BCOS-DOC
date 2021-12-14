# 使用KV存储预编译合约开发应用

标签：``预编译合约`` ``CRUD`` ``区块链应用``

----------

本文将介绍 FISCO BCOS 3.0的KV存储功能，帮助开发者更高效便捷地开发区块链应用。

（CRUD功能将在FISCO BCOS 3.0.0下个版本支持。）

**特别注意：使用KV存储预编译合约的Solidity合约必须高于0.6.0版本，并且开启使用ABIEncoderV2**

## KV存储使用方法

目前可以用两种方式使用KV存储功能，分别是KVTable合约和 Java SDK KVTable Service接口。

### 1. KVTable合约

- Solidity合约只需要引入FISCO BCOS官方提供的KVTable.sol抽象接口合约文件即可。
- webankblockchain-liquid（以下简称WBC-Liquid）合约在实现合约之前对KVTable的接口进行声明使用即可。

KVTable包含分布式存储专用的智能合约接口，其接口实现在区块链节点，可以创建表，并对表进行get/set操作。

KVTable抽象接口合约文件包括以下抽象合约接口，下面分别进行介绍。

#### Entry结构体

从FISCO BCOS 3.0版本起，Entry的使用都是基于结构体进行使用。已Solidity接口为例，Entry带有一个KVFeild结构体数组字段，在将Entry作为参数传入时，需要先初始化该字段。

```solidity
struct KVField {
  string key;
  string value;
}

struct Entry {
  KVField[] fields;
}
```

#### KVTable合约

用于创建、访问表数据，其固定合约地址为0x1009，接口如下：

| 接口                                | 功能   | 参数                                                         | 返回值                                                   |
| ----------------------------------- | ------ | ------------------------------------------------------------ | -------------------------------------------------------- |
| createTable(string ,string, string) | 创建表 | 表名，主键名（目前只支持单个主键），表的其他字段名（字段之间以英文逗号分隔） | 返回错误码（int256），错误码详见下表                     |
| get(string,string)                  | 获取值 | 表名，主键名                                                 | 返回bool值、Entry，如果查询失败，第一个返回值将会是false |
| set(string,string,Entry)            | 设置值 | 表名，主键名，Entry结构体                                    | 返回错误码（int256），成功时返回1，其余错误码详见下表    |

接口返回错误码：

| 错误码 | 说明                         |
| :----- | :--------------------------- |
| 0      | 创建成功                     |
| -50001 | 创建表名已存在               |
| -50002 | 表名超过48字符               |
| -50003 | valueField字段名长度超过64字符     |
| -50004 | valueField总字段名长度超过1024字符 |
| -50005 | keyField字段值长度超过64字符       |
| -50006 | valueField字段值长度超过64字符       |
| -50007 | 存在重复字段                 |
| -50008 | 字段存在非法字符             |
| 其他   | 创建时遇到的其他错误         |

有了以上对KVTable抽象接口合约的了解，现在可以正式进行KVTable合约的开发。

### 2. Solidity合约使用KVTable

#### 2.1 引入KVTable.sol

将KVTable.sol合约放入KVTableTest.sol同级目录，并在KVTableTest.sol合约文件中引入KVTable.sol，其代码如下：

```solidity
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

import "./KVTable.sol";
```

#### 2.2 创建表

在KVTableTest.sol合约文件中，创建表的核心代码如下：

```solidity
KVTable kv_table;
constructor () public{
  // 创建KVTable对象，其在区块链上的固定地址是0x1001
  kv_table = KVTable(0x1009);
  // 创建t_kv_test表，表的主键名为id，其他字段名为item_price和item_name
  int count = kv_table.createTable("t_kv_test", "id", "item_price,item_name");
  // 检查是否创建成功
  if(count >= 0)
  {
    // success
  }
}
```

**注：**

- createTable执行原理：createTable执行成功之后，将会在区块链系统表_s_tables_(区块链启动会自动创建该表，专门记录区块链中所有表的信息)中插入t_kv_test的表信息，即表名，主键名和其他字段名，但并没有正式创建该表。当对t_kv_test表进行增删改查操作时，会首先判断t_kv_test表是否存在，若不存在，则会查询_s_tables_表获取t_kv_test表的信息，如果查询有t_kv_test表信息，则创建该表，否则执行失败。t_kv_test表存在，则继续执行读写操作。
- 这一步是可选操作：比如新合约只是读写旧合约创建的表，则不需创建表这步操作。

#### 2.3 针对表进行KV读写操作

在KVTableTest.sol合约文件中，写入记录核心代码如下：

``` solidity
function set(string memory id, string memory item_price, string memory item_name)
public
returns (int256)
{
 // 创建KVField结构体对象，用于插入entry
 KVField memory kv1 = KVField("item_price",item_price);
 KVField memory kv2 = KVField("item_name",item_name);
 KVField[] memory KVFields = new KVField[](2);
 KVFields[0] = kv1;
 KVFields[1] = kv2;
 // 创建entry，并将创建的KVField对象数组作为参数设置
 Entry memory entry = Entry(KVFields);
 int256 count = kv_table.set("t_kv_test", id, entry);
 return count;
}
```

读数据记录核心代码如下：

```solidity
function get(string memory id) public view returns (bool, string memory, string memory) {
  bool ok = false;
  Entry memory entry;
  // kv_table在构造函数就已指定地址0x1009
  (ok, entry) = kv_table.get("t_kv_test",id);
  string memory item_price;
  string memory item_name;
  if (ok) {
    // 从返回的entry中取值
    item_price = entry.fields[0].value;
    item_name = entry.fields[1].value;
  }
  return (ok, item_price, item_name);
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

// Entry结构体的声明
#[derive(InOut)]
pub struct KVField {
    key: String,
    value: String,
}
#[derive(InOut)]
pub struct Entry {
    fileds: Vec<KVField>,
}

// KVTable的接口声明
#[liquid::interface(name = auto)]
mod kv_table {
    use super::*;
    extern "liquid" {
        fn createTable(
            &mut self,
            table_name: String,
            key: String,
            value_fields: String,
        ) -> i256;
        fn get(&self, table_name: String, key: String) -> (bool, Entry);
        fn set(&mut self, table_name: String, key: String, entry: Entry) -> i256;
    }
}
```

#### 3.2 WBC-Liquid创建表

可在WBC-Liquid的构造函数中实现创建表的逻辑，此处引入的KVTable的地址为BFS路径 `/sys/kv_storage` ，注意WBC-Liquid和Solidity的区别。

创建表的原理与Solidity的类似，再次不再赘述。

```rust
pub fn new(&mut self) {
   // kv table指定系统合约路径为"/sys/kv_storage"
  self.table
    .initialize(KvTable::at("/sys/kv_storage".parse().unwrap()));
  
   // 调用接口创建表
  self.table.createTable(
    String::from("t_kv_test"),
    String::from("id"),
    [String::from("item_price"), String::from("item_name")].join(","),
  );
}
```

#### 3.3 针对表进行KV读写操作

写的主要逻辑如下：

```rust
pub fn set(&mut self, id: String, item_price: String, item_name: String) -> i256 {
   // 创建KVField结构体，用于设置进Entry
  let kv1 = KVField {
    key: String::from("item_price"),
    value: item_price,
  };
  let kv2 = KVField {
    key: String::from("item_name"),
    value: item_name,
  };
  let mut kv_fields = Vec::new();
  kv_fields.push(kv1);
  kv_fields.push(kv2);
   // 创建Entry结构体，将KVField结构体数组作为参数设置
  let entry = Entry { fileds: kv_fields };
   // 调用KVTable的set接口
  let count = (*self.table)
    .set(String::from("t_kv_test"), id, entry)
    .unwrap();
  // 调用合约事件
  self.env().emit(SetResult {
    count: count.clone(),
  });
  count
}
```

读的主要逻辑如下：

```rust
pub fn get(&self, id: String) -> (bool, String, String) {
   // 调用KVTable的get接口
  if let Some((ok, entry)) = (*self.table).get(String::from("t_kv_test"), id) {
    return (
      ok,
      entry.fileds[0].value.clone(),
      entry.fileds[1].value.clone(),
    );
  }
  return (false, Default::default(), Default::default());
}
```

### 4. SDK KVTable Service接口

FISCO BCOS 3.0 SDK提供KVTable Service数据上链接口，这些接口实现的原理是调用区块链内置的一个预编译的KVTable合约，专门负责对用户表进行读写操作。Java SDK KVTable Service实现在org.fisco.bcos.sdk.contract.precompiled.crud.KVTableService 类，其接口如下：

| 接口                                        | 功能         | 参数                    | 返回值                   |
| ------------------------------------------- | ------------ | ----------------------- | ------------------------ |
| createTable(String, String, List\<String\>) | 创建表       | 表名、主键名、字段名    | 错误码                   |
| set(String, String, Entry)                  | 写数据       | 表名、主键名、Entry对象 | 错误码                   |
| get(String, String)                         | 读数据       | 表名、主键名            | Map\<String,String\>     |
| desc(String tableName)                      | 查询表的信息 | 表名                    | 表的keyField和valueField |

其中调用写接口会产生与调用KV合约接口等效的交易，需要共识节点共识一致后才会落盘存储。
