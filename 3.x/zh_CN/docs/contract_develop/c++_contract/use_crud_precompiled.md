# 使用CRUD预编译合约开发应用

标签：``预编译合约`` ``CRUD`` ``区块链应用``


----------

本文将介绍 FISCO BCOS 3.x的CRUD存储功能，帮助开发者更高效便捷地开发区块链应用。

**特别注意：使用存储预编译合约的Solidity合约必须高于0.6.0版本，并且开启使用ABIEncoderV2**

## CRUD存储使用方法

目前可以用两种方式使用CRUD存储功能，分别是Table合约和 Java SDK TableCRUDService接口。

### 1. Table合约

- Solidity合约只需要引入FISCO BCOS官方提供的KVTable.sol抽象接口合约文件即可。
- webankblockchain-liquid（以下简称WBC-Liquid）合约在实现合约之前对Table的接口进行声明使用即可。

Table包含分布式存储专用的智能合约接口，其接口实现在区块链节点。其中，TableManager可以创建表、新增表字段，Table可以用作表CRUD操作。下面分别进行介绍。

#### 1.1 TableManager合约接口

用于创建表，打开表，其固定合约地址为`0x1002（Solidity）`和`/sys/table_manager（Liquid）`，接口如下（只展示与Table相关接口）：

| 接口                            | 功能             | 参数                       | 返回值                                      |
|---------------------------------|------------------|----------------------------|---------------------------------------------|
| createTable(string ,TableInfo)  | 创建表           | 表名，TableInfo结构体      | 返回错误码（int32），错误码详见下表         |
| appendColumns(string, string[]) | 增加表字段       | 表名，字段名数组           | 返回错误码（int32），错误码详见下表         |
| openTable(string)               | 获取表地址       | 表名，该接口只用于Solidity | 返回表对应的真实地址，如果不存在，则返回0x0 |
| desc(string)                    | 获取表信息结构体 | 表名                       | 返回TableInfo结构体                         |

#### 1.2 Table合约

用于访问表数据，接口如下：

| 接口                                      | 功能         | 参数                                   | 返回值                                                      |
|-------------------------------------------|--------------|----------------------------------------|-------------------------------------------------------------|
| select(string)                            | 获取单行值   | 主键值                                 | 返回Entry结构体，包含单行所有字段值                         |
| select(Condition[], Limit)                | 获取多行值   | 主键筛选条件，返回行数限制             | 返回Entry结构体数组，包含多行的所有字段值                   |
| count(Condition[])                        | 获取匹配行数 | 主键筛选条件                           | 返回符合条件的所有行数                                      |
| insert(Entry)                             | 设置单行     | Entry结构体，包含当行所有值            | 返回错误码（int32），成功时返回1，其余错误码详见下表        |
| update(string, UpdateFiled[])             | 更新单行     | 主键，更新字段值                       | 返回错误码（int32），成功时返回1，其余错误码详见下表        |
| update(Condition[], Limit, UpdateFiled[]) | 更新多行     | 主键筛选条件，返回行数限制，更新字段值 | 返回错误码（int32），成功时返回更新行数，其余错误码详见下表 |
| remove(string)                            | 删除单行值   | 主键值                                 | 返回错误码（int32），成功时返回1，其余错误码详见下表        |
| remove(Condition[], Limit)                | 删除多行值   | 主键筛选条件，返回行数限制             | 返回错误码（int32），成功时返回删除行数，其余错误码详见下表 |

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
| -51508 | 删除时主键不存在                   |
| -51507 | 更新时主键不存在                   |
| -51506 | 插入时主键不存在                   |
| 其他   | 创建时遇到的其他错误               |

有了以上对KVTable抽象接口合约的了解，现在可以正式进行KVTable合约的开发。

### 2. Solidity合约使用Table

#### 2.1 引入Table.sol

将Table.sol合约放入TableTest.sol同级目录，并在TableTest.sol合约文件中引入Table.sol，其代码如下：

```solidity
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

import "./Table.sol";
```

#### 2.2 创建表

在TableTest.sol合约文件中，创建表的核心代码如下：

```solidity
// 创建TableManager对象，其在区块链上的固定地址是0x1002
TableManager constant tm =  TableManager(address(0x1002));
Table table;
string constant TABLE_NAME = "t_test";
constructor () public{
    // 创建t_test表，表的主键名为id，其他字段名为name和age
    string[] memory columnNames = new string[](2);
    columnNames[0] = "name";
    columnNames[1] = "age";
    TableInfo memory tf = TableInfo("id", columnNames);
    tm.createTable(TABLE_NAME, tf);
    
    // 获取真实的地址，存在合约中
    address t_address = tm.openTable(TABLE_NAME);
    require(t_address!=address(0x0),"");
    table = Table(t_address);
}
```

**注：** 这一步是可选操作：比如新合约只是读写旧合约创建的表，则不需创建表这步操作。

#### 2.3 针对表进行CRUD读写操作

在TableTest.sol合约文件中，insert记录核心代码如下：

``` solidity
function insert(string memory id,string memory name,string memory age) public returns (int32){
    string[] memory columns = new string[](2);
    columns[0] = name;
    columns[1] = age;
    Entry memory entry = Entry(id, columns);
    int32 result = table.insert(entry);
    emit InsertResult(result);
    return result;
}
```

在TableTest.sol合约文件中，update记录核心代码如下：

```solidity
function update(string memory id, string memory name, string memory age) public returns (int32){
    UpdateField[] memory updateFields = new UpdateField[](2);
    updateFields[0] = UpdateField("name", name);
    updateFields[1] = UpdateField("age", age);

    int32 result = table.update(id, updateFields);
    emit UpdateResult(result);
    return result;
}
```

在TableTest.sol合约文件中，remove记录核心代码如下：

```solidity
function remove(string memory id) public returns(int32){
    int32 result = table.remove(id);
    emit RemoveResult(result);
    return result;
}
```

读数据记录核心代码如下：

```solidity
function select(string memory id) public view returns (string memory,string memory)
{
    Entry memory entry = table.select(id);

    string memory name;
    string memory age;
    if(entry.fields.length==2){
        name = entry.fields[0];
        age = entry.fields[1];
    }
    return (name,age);
}
```

#### 2.4 使用Condition读写多行数据

用户可以使用Table提供的具有Condition参数的接口，进行多行数据读写。

**注意：** 目前的Condition只支持主键字段的范围筛选。

读多行数据的核心代码如下，写多行数据的类似：

```solidity
function selectMore(string memory gt_id)
    public
    view
    returns (Entry[] memory entries)
{
    Condition[] memory conds = new Condition[](1);
    Condition memory gt = Condition({op: ConditionOP.GT, value: gt_id});
    conds[0] = gt;
    Limit memory limit = Limit({offset: 0, count: 100});
    entries = table.select(conds, limit);
    return entries;
}
```

### 3. WBC-Liquid合约使用Table接口

#### 3.1 声明Table接口

在WBC-Liquid合约中使用接口之前先对KVTable的接口进行声明。

```rust
#![cfg_attr(not(feature = "std"), no_std)]

use liquid::storage;
use liquid_lang as liquid;
use liquid_lang::InOut;
use liquid_prelude::{string::String, vec::Vec};

// TableInfo结构体
#[derive(InOut)]
pub struct TableInfo {
    key_column: String,
    value_columns: Vec<String>,
}

// Entry结构体
#[derive(InOut)]
pub struct Entry {
    key: String,
    fields: Vec<String>,
}

// UpdateField结构体
#[derive(InOut)]
pub struct UpdateField {
    field_name: String,
    value: String,
}

// Condition条件
#[derive(InOut)]
pub enum ConditionOP {
    GT(u8),
    GE(u8),
    LT(u8),
    LE(u8),
}

// Condition结构体
#[derive(InOut)]
pub struct Condition {
    op: ConditionOP,
    value: String,
}

// Limit结构体
#[derive(InOut)]
pub struct Limit {
    offset: u32,
    count: u32,
}

// TableManager接口声明
#[liquid::interface(name = auto)]
mod table_manager {
    use super::*;

    extern "liquid" {
        fn createTable(&mut self, path: String, table_info: TableInfo) -> i32;
    }
}
// Table接口声明
#[liquid::interface(name = auto)]
mod table {
    use super::*;

    extern "liquid" {
        fn select(&self, key: String) -> Entry;
        fn insert(&mut self, entry: Entry) -> i32;
        fn update(&mut self, key: String, update_fields: Vec<UpdateField>) -> i32;
        fn remove(&mut self, key: String) -> i32;
    }
}
```

#### 3.2 WBC-Liquid创建表

可在WBC-Liquid的构造函数中实现创建表的逻辑，此处引入的TableManager的地址为BFS路径 `/sys/table_manager` ，注意WBC-Liquid和Solidity的区别。

创建表的原理与Solidity的类似，再次不再赘述。

```rust
pub fn new(&mut self) {
    self.table_name.initialize(String::from("t_test"));
    // TableManager的固定地址为/sys/table_manager
    self.tm
        .initialize(TableManager::at("/sys/table_manager".parse().unwrap()));

    let mut column_names = Vec::new();
    column_names.push(String::from("name"));
    column_names.push(String::from("age"));
    let ti = TableInfo {
        key_column: String::from("id"),
        value_columns: column_names,
    };

    self.tm.createTable(self.table_name.clone(), ti);
    // 创建成功后，/tables/+创建的表名就是合约的正确地址
    self.table
        .initialize(Table::at("/tables/t_test".parse().unwrap()));
}
```

#### 3.3 针对表进行CRUD读写操作

写的主要逻辑如下：

```rust
// 插入接口
pub fn insert(&mut self, id: String, name: String, age: String) -> i32 {
    let mut values = Vec::new();
    values.push(name);
    values.push(age);

    let entry = Entry {
        key: id,
        fields: values,
    };
    let result = self.table.insert(entry).unwrap();
    self.env().emit(InsertResult {
        count: result.clone(),
    });
    return result;
}

// 更新接口
pub fn update(&mut self, id: String, name: String, age: String) -> i32 {
    let mut update_fields = Vec::new();
    update_fields.push(UpdateField {
        field_name: String::from("name"),
        value: name,
    });

    update_fields.push(UpdateField {
        field_name: String::from("age"),
        value: age,
    });

    let result = self.table.update(id, update_fields).unwrap();
    self.env().emit(UpdateResult {
        count: result.clone(),
    });
    return result;
}

// 删除接口
pub fn remove(&mut self, id: String) -> i32 {
    let result = self.table.remove(id).unwrap();
    self.env().emit(RemoveResult {
        count: result.clone(),
    });
    return result;
}
```

读的主要逻辑如下：

```rust
pub fn select(&self, id: String) -> (String, String) {
    let entry = self.table.select(id).unwrap();

    if entry.fields.len() < 1 {
        return (Default::default(), Default::default());
    }

    return (entry.fields[0].clone(), entry.fields[1].clone());
}
```

### 4. SDK TableCRUDService接口

FISCO BCOS 3.x SDK提供TableCRUDService数据上链接口，这些接口实现的原理是调用区块链内置的一个预编译的KVTable合约，专门负责对用户表进行读写操作。Java SDK TableCRUDService实现在org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService 类。其中调用写接口会产生与调用Table合约接口等效的交易，需要共识节点共识一致后才会落盘存储。
