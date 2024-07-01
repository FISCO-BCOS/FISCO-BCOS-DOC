# Develop applications using CRUD precompiled contracts

Tags: "Precompiled Contracts" "CRUD" "" Blockchain Applications ""


----------

This article will introduce the CRUD storage capabilities of FISCO BCOS 3.x to help developers develop blockchain applications more efficiently and easily.。

**Special note: Solidity contracts that use stored precompiled contracts must be older than version 0.6.0 and use ABIEncoderV2**

## CRUD Storage Usage

There are currently two ways to use the CRUD storage feature: the Table contract and the Java SDK TableCRUDService interface。

### 1. Table Contracts

- The Solidity contract only needs to introduce the KVTable.sol abstract interface contract file officially provided by FISCO BCOS.。
- webankblockchain-liquid (hereinafter referred to as WBC-Liquid) The contract declares the use of the Table interface before implementing the contract.。

Table contains a smart contract interface dedicated to distributed storage, which is implemented on blockchain nodes.。TableManager can create tables and add table fields. Table can be used as a table CRUD operation.。The following are introduced separately。

#### 1.1 TableManager contract interface

Used to create a table and open the table. The fixed contract addresses are '0x1002 (Solidity)' and '/ sys / table _ manager (Liquid)'. The interfaces are as follows (only the table-related interfaces are shown):

| Interface| Function| Parameters| Return value|
|---------------------------------|------------------|----------------------------|---------------------------------------------|
| createTable(string ,TableInfo)  | Create Table| Table name, TableInfo structure| The error code (int32) is returned. For details, see the following table.|
| appendColumns(string, string[]) | Add Table Field| table name, array of field names| The error code (int32) is returned. For details, see the following table.|
| openTable(string)               | Get Table Address| Table name. This interface is only used for Solidity| Returns the real address of the table. If it does not exist, 0x0 is returned.|
| desc(string)                    | Get Table Information Structure| Table Name| Returns the TableInfo structure|

#### 1.2 Table Contracts

Used to access table data. The interface is as follows:

| Interface| Function| Parameters| Return value|
|-------------------------------------------|--------------|----------------------------------------|-------------------------------------------------------------|
| select(string)                            | Get Single Row Value| Primary Key Value| Returns an Entry structure containing all field values in a single row|
| select(Condition[], Limit)                | Get multi-row values| Primary key filter criteria, limit on the number of rows returned| Returns an array of Entry structures containing all field values for multiple rows|
| count(Condition[])                        | Get number of matching rows| Primary Key Filter Criteria| Returns the number of all rows that meet the criteria|
| insert(Entry)                             | Set Single Line| Entry structure, containing all values of the current row| The error code (int32) is returned. If the error code is successful, 1 is returned. See the following table for other error codes.|
| update(string, UpdateFiled[])             | Update single line| primary key, updating field values| The error code (int32) is returned. If the error code is successful, 1 is returned. See the following table for other error codes.|
| update(Condition[], Limit, UpdateFiled[]) | Update multiple rows| Primary key filter, return row limit, update field value| The error code (int32) is returned. The number of updated rows is returned when the error code is successful. See the following table for details.|
| remove(string)                            | Delete Single Row Value| Primary Key Value| The error code (int32) is returned. If the error code is successful, 1 is returned. See the following table for other error codes.|
| remove(Condition[], Limit)                | Delete multi-row values| Primary key filter criteria, limit on the number of rows returned| The error code (int32) is returned. If the error code succeeds, the number of deleted rows is returned. See the following table for other error codes.|

The interface returns an error code:

| error code| Description|
|:-------|:-----------------------------------|
| 0      | Creation successful|
| -50001 | Create table name already exists|
| -50002 | Table name exceeds 48 characters|
| -50003 | valueField field name length exceeds 64 characters|
| -50004 | valueField Total field name length exceeds 1024 characters|
| -50005 | keyField field value exceeds 64 characters in length|
| -50006 | valueField field value length exceeds 16 megabytes|
| -50007 | Duplicate field exists|
| -50008 | Illegal character in field|
| -51508 | Primary key does not exist on delete|
| -51507 | Primary key does not exist on update|
| -51506 | Primary key does not exist at insert time|
| 其他| Other errors encountered while creating|

With the above understanding of the KVTable abstract interface contract, the development of the KVTable contract can now be formally carried out.。

### 2. Solidity contract uses Table

#### 2.1 Introducing Table.sol

Place the Table.sol contract in the TableTest.sol peer directory and introduce Table.sol in the TableTest.sol contract file. The code is as follows:

```solidity
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

import "./Table.sol";
```

#### 2.2 Create Table

In the TableTest.sol contract file, the core code for creating a table is as follows:

```solidity
/ / Create a TableManager object whose fixed address on the blockchain is 0x1002
TableManager constant tm =  TableManager(address(0x1002));
Table table;
string constant TABLE_NAME = "t_test";
constructor () public{
    / / Create the t _ test table. The primary key of the table is id, and the other fields are name and age.
    string[] memory columnNames = new string[](2);
    columnNames[0] = "name";
    columnNames[1] = "age";
    TableInfo memory tf = TableInfo("id", columnNames);
    tm.createTable(TABLE_NAME, tf);
    
    / / Get the real address, which is stored in the contract
    address t_address = tm.openTable(TABLE_NAME);
    require(t_address!=address(0x0),"");
    table = Table(t_address);
}
```

**Note:** This step is optional: for example, if the new contract only reads and writes the table created by the old contract, you do not need to create the table.。

#### 2.3 CRUD read and write operations for tables

In the TableTest.sol contract file, the insert record core code is as follows:

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

In the TableTest.sol contract file, the update record core code is as follows:

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

In the TableTest.sol contract file, remove records the core code as follows:

```solidity
function remove(string memory id) public returns(int32){
    int32 result = table.remove(id);
    emit RemoveResult(result);
    return result;
}
```

The core code of the read data record is as follows:

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

#### 2.4 Use Condition to read and write multiple rows of data

Users can read and write multiple rows of data by using the interface provided by Table with the Condition parameter.。

**Note:** Currently, Condition only supports range filtering for primary key fields。

The core code for reading multiple rows of data is as follows, similar to writing multiple rows of data.

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

### 3. WBC-The Liquid contract uses the Table interface

#### 3.1 Declaring the Table interface

at WBC-Declare the interface of the KVTable before using the interface in the Liquid contract。

```rust
#![cfg_attr(not(feature = "std"), no_std)]

use liquid::storage;
use liquid_lang as liquid;
use liquid_lang::InOut;
use liquid_prelude::{string::String, vec::Vec};

/ / TableInfo structure
#[derive(InOut)]
pub struct TableInfo {
    key_column: String,
    value_columns: Vec<String>,
}

/ / Entry structure
#[derive(InOut)]
pub struct Entry {
    key: String,
    fields: Vec<String>,
}

/ / UpdateField structure
#[derive(InOut)]
pub struct UpdateField {
    field_name: String,
    value: String,
}

/ / Condition condition
#[derive(InOut)]
pub enum ConditionOP {
    GT(u8),
    GE(u8),
    LT(u8),
    LE(u8),
}

/ / Condition structure
#[derive(InOut)]
pub struct Condition {
    op: ConditionOP,
    value: String,
}

/ / Limit structure
#[derive(InOut)]
pub struct Limit {
    offset: u32,
    count: u32,
}

/ / TableManager interface declaration
#[liquid::interface(name = auto)]
mod table_manager {
    use super::*;

    extern "liquid" {
        fn createTable(&mut self, path: String, table_info: TableInfo) -> i32;
    }
}
/ / Table interface declaration
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

#### 3.2 WBC-Liquid Create Table

Available at WBC-The logic for creating a table is implemented in the constructor of Liquid. The address of the TableManager introduced here is the BFS path '/ sys / table _ manager'. Note that WBC-Difference between Liquid and Solidity。

The principle of creating a table is similar to that of Solidity, so I won't repeat it again.。

```rust
pub fn new(&mut self) {
    self.table_name.initialize(String::from("t_test"));
    / / The fixed address of the TableManager is / sys / table _ manager
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
    / / After successful creation, / tables /+The table name created is the correct address for the contract
    self.table
        .initialize(Table::at("/tables/t_test".parse().unwrap()));
}
```

#### 3.3 CRUD read and write operations for tables

The main logic of writing is as follows:

```rust
/ / Insert interface
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

/ / Update interface
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

/ / Delete interface
pub fn remove(&mut self, id: String) -> i32 {
    let result = self.table.remove(id).unwrap();
    self.env().emit(RemoveResult {
        count: result.clone(),
    });
    return result;
}
```

The main logic of reading is as follows:

```rust
pub fn select(&self, id: String) -> (String, String) {
    let entry = self.table.select(id).unwrap();

    if entry.fields.len() < 1 {
        return (Default::default(), Default::default());
    }

    return (entry.fields[0].clone(), entry.fields[1].clone());
}
```

### 4. SDK TableCRUDService interface

FISCO BCOS 3.x SDK provides TableCRUDService data connection ports. These interfaces are implemented by calling a precompiled KVTable contract built into the blockchain to read and write user tables.。The Java SDK TableCRUDService is implemented in the org.fisco.bcos.sdk.v3.contract.precompiled.crud.TableCRUDService class。The call to the write interface will generate the same transaction as the call to the Table contract interface, which will not be stored until the consensus node consensus is consistent.。
