# Develop applications using KV storage precompiled contracts

Tags: "Precompiled Contracts" "CRUD" "" Blockchain Applications ""

----------

This article will introduce the KV storage function of FISCO BCOS 3.x to help developers develop block chain applications more efficiently and easily。

**Special note: Solidity contracts that use KV to store precompiled contracts must be higher than version 0.6.0 and use ABIEncoderV2**

## KV storage usage

Currently, you can use the KV storage function in two ways: the KVTable contract and the Java SDK KVTable Service interface。

### 1. KVTable contract

-The Solidity contract only needs to introduce the Table.sol abstract interface contract file provided by FISCO BCOS。
-webankblockchain-liquid (hereinafter referred to as WBC-Liquid) contract can be used by declaring the interface of KVTable before implementing the contract。

Table contains a dedicated smart contract interface for distributed storage. The interface is implemented on a blockchain node. TableManager can create a dedicated KV table, and KVTable can be used as a table for get / set operations。The following are introduced separately。

#### 1.1 TableManager contract interface

Used to create a KV table and open the KV table. The fixed contract addresses are '0x1002 (Solidity)' and '/ sys / table _ manager (Liquid)'. The interfaces are as follows (only the interfaces related to KVTable are shown):

| Interface| Function| Parameters| Return value|
|-------------------------------|------------|--------------------------------------------|---------------------------------------------|
| createKVTable(string ,string) | Create Table| Table name, primary key name (currently only a single primary key is supported), field name| The error code (int32) is returned. For details, see the following table|
| openTable(string)             | Get Table Address| Table name. This interface is only used for Solidity| Returns the real address of the table. If it does not exist, 0x0 is returned|

#### 1.2 The KVTable Contract

Used to access table data. The interface is as follows:

| Interface| Function| Parameters| Return value|
|--------------------|--------|--------------|-----------------------------------------------------------|
| get(string)        | Get Value| primary key| Return bool value and string. If the query fails, the first return value will be false|
| set(string,string) | Set value| Primary key, field value| The error code (int32) is returned. If the error code is successful, 1 is returned. See the following table for other error codes|

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
| 其他| Other errors encountered while creating|

With the above understanding of the KVTable abstract interface contract, the development of the KVTable contract can now be formally carried out。

### 2. Solidity contract uses KVTable

#### 2.1 Introducing Table.sol

Place the Table.sol contract in the KVTableTest.sol peer directory and introduce Table.sol in the KVTableTest.sol contract file. The code is as follows:

```solidity
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

import "./Table.sol";
```

#### 2.2 Create Table

In the KVTableTest.sol contract file, the core code for creating a table is as follows:

```solidity
TableManager tm;
KVTable table;
string constant tableName = "t_kv_test";
event SetEvent(int256 count);
constructor () public{
  	/ / Create a TableManager object whose fixed address on the blockchain is 0x1002
    tm = TableManager(address(0x1002));

    / / Create the t _ kv _ test table. The primary key of the table is id, and other fields are item _ name
    tm.createKVTable(tableName, "id", "item_name");

    / / Get the real address, which is stored in the contract
    address t_address = tm.openTable(tableName);
    table = KVTable(t_address);
}
```

**Note:** This step is optional: for example, if the new contract only reads and writes the table created by the old contract, you do not need to create the table。

#### 2.3 KV read and write operation for the table

In the KVTableTest.sol contract file, the core code for writing records is as follows:

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

The core code of the read data record is as follows:

```solidity
function get(string memory id) public view returns (bool, string memory) {
    bool ok = false;
    string memory value;
    (ok, value) = table.get(id);
    return (ok, value);
}
```

### 3. The WBC-Liquid contract uses the KVTable interface

#### 3.1 Declaring the KVTable interface

Declare the KVTable interface before using it in the WBC-Liquid contract。

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

/ / Interface declaration of TableManager
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
/ / KVTable interface declaration
#[liquid::interface(name = auto)]
mod kv_table {

    extern "liquid" {
        fn get(&self, key: String) -> (bool, String);
        fn set(&mut self, key: String, value: String) -> i32;
    }
}
```

#### 3.2 WBC-Liquid Create Table

The logic for creating a table can be implemented in the constructor of WBC-Liquid. The address of the TableManager introduced here is the BFS path '/ sys / table _ manager'. Note the difference between WBC-Liquid and Solidity。

The principle of creating a table is similar to that of Solidity, so I won't repeat it again。

```rust
pub fn new(&mut self) {
    self.table_name.initialize(String::from("t_kv_test"));
  	/ / The fixed address of the TableManager is / sys / table _ manager
    self.tm
        .initialize(TableManager::at("/sys/table_manager".parse().unwrap()));
    self.tm.createKVTable(
        self.table_name.clone(),
        String::from("id"),
        String::from("item_name"),
    );
  	/ / After successful creation, / tables /+The table name created is the correct address for the contract
    self.table
        .initialize(KvTable::at("/tables/t_kv_test".parse().unwrap()));
}
```

#### 3.3 KV read and write operation for the table

The main logic of writing is as follows:

```rust
pub fn set(&mut self, id: String, item_name: String) -> i32 {
    let count = (*self.table).set(id, item_name).unwrap();

    self.env().emit(SetEvent {
        count: count.clone(),
    });
    count
}
```

The main logic of reading is as follows:

```rust
pub fn get(&self, id: String) -> (bool, String) {
    if let Some((ok, value)) = (*self.table).get(id) {
        return (ok, value);
    }
    return (false, Default::default());
}
```

### 4. SDK KVTable Service interface

FISCO BCOS 3.x SDK provides KVTable Service data connection ports. These interfaces are implemented by calling a precompiled KVTable contract built into the blockchain to read and write user tables。The Java SDK KVTable Service is implemented in the org.fisco.bcos.sdk.v3.contract.precompiled.crud.KVTableService class. Its interfaces are as follows:

| Interface| Function| Parameters| Return value|
|-------------------------------------|--------------|----------------------|--------------------------|
| createTable(String, String, String) | Create Table| Table name, primary key name, field name| error code|
| set(String, String, String)         | Write data| Table name, primary key name, field value| error code|
| get(String, String)                 | Read Data| Table name, primary key name| String                   |
| desc(String)                        | Query table information| Table Name| KeyField and valueField for tables|

The call to the write interface will generate the equivalent transaction to the call to the KV contract interface, which will not be stored until the consensus node consensus is consistent。
