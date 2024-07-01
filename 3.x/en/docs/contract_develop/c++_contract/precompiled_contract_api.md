# Precompiled Contract Interface

Tags: "precompiled contract" "interface"

---

FISCO BCOS 3.x follows the FISCO BCOS 2.0 version of the precompiled contract。In the future, we will also try to abstract the existing typical business scenarios and develop them into pre-compiled contract templates as the basic capability provided by the underlying layer to help users use FISCO BCOS in their business faster and more conveniently.。

## 1. SystemConfigPrecompiled

### Contract Address

- Solidity：0x1000
- WBC-Liquid：/sys/status

### interface declaration

Take Solidity for example.

```solidity
pragma solidity ^0.6.0;

contract SystemConfigPrecompiled
{
    function setValueByKey(string memory key, string memory value) public returns(int32){}
    function getValueByKey(string memory key) public view returns(string memory,int256){}
}
```

### setValueByKey Description

**Intake:**

- key indicates the configuration item name. Currently supported parameters include 'tx _ count _ limit', 'tx _ gas _ lmit', 'consensus _ leader _ period', and 'compatibility _ version'。
- value indicates the value of the corresponding configuration item. The default value of 'tx _ count _ limit' is 1000, and the minimum value is 1. The default value of 'consensus _ leader _ period' is 1, and the minimum value is 1. The default value of tx _ gas _ limit is 300000000, and the minimum value is 100000.。

**Returns:**

- setValueByKey will be returned as an error code

| error code| Description|
|:-------|:-------------------|
| equal to 0| Success|
| -51300 | Incorrect system parameters entered|

### getValueByKey Description

**Intake:**

- key indicates the configuration item name. Currently supported parameters include 'tx _ count _ limit', 'consensus _ leader _ period', 'consensus _ leader _ period', and 'compatibility _ version'。

**Returns:**

- returns the specific value and the block height in effect

### SDK support

- [Java SDK](../sdk/java_sdk/index.md)

## 2. ConsensusPrecompiled

### Contract Address

- Solidity：0x1003
- WBC-Liquid: /sys/consensus

### interface declaration

Take Solidity for example.

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract ConsensusPrecompiled {
    function addSealer(string memory,uint256) public returns (int32){}
    function addObserver(string memory) public returns (int32){}
    function remove(string memory) public returns (int32){}
    function setWeight(string memory,uint256) public returns (int32){}
}
```

### Interface Description

- addSealer adds a consensus node, the parameter is the hexadecimal representation of the new node's public key, and sets the weight, which can only be a positive number。
- addObserver Add an observation node or change the identity of an existing consensus node to an observation node。
- remove a node. If it is the last consensus node, it is not allowed to be deleted.。
- setWeigh is used to set the weight of a consensus node。
- Data stored in _ s _ consensus _ table。

**Interface Return Description:**

- interfaces are returned as error codes

| error code| Description|
| :----- | :--------------------- |
| equal to 0| Success|
| -51100 | Enter the wrong nodeID|
| -51101 | Removing last sealer|
| -51102 | Weight of input error|
| -51103 | Operation on non-existent nodes|

### SDK support

- [Java SDK](../sdk/java_sdk/api.html#consensusservice)

## 3. TableManagerPrecompiled

### Contract Address

- Solidity：0x1002
- WBC-Liquid: /sys/table_manager

### interface declaration

```solidity
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

/ / KeyOrder specifies the collation, lexicographical order, and numeric order of the key. If numeric order is specified, the key can only be numeric
// enum KeyOrder {Lexicographic, Numerical}
struct TableInfo {
    string keyColumn;
    string[] valueColumns;
}

/ / The table management contract is static precompiled and has a fixed contract address.
abstract contract TableManager {
    / / Create a table and pass in TableInfo
    function createTable(string memory path, TableInfo memory tableInfo) public virtual returns (int32);

    / / Create a KV table and enter the key and value field names.
    function createKVTable(string memory tableName, string memory keyField, string memory valueField) public virtual returns (int32);

    / / Use only when calling the Solidity contract
    function openTable(string memory path) public view virtual returns (address);

    / / Change table fields
    / / Only new fields can be added, and fields cannot be deleted. The default value of new fields is blank and cannot be duplicated with the original fields.
    function appendColumns(string memory path, string[] memory newColumns) public virtual returns (int32);

    / / Get table information
    function desc(string memory tableName) public view virtual returns (TableInfo memory);
}
```

### Interface Description

- CreateTable and createKVTable create a table. The parameters are the table name, primary key column name, and other column names separated by commas.。
  - CreateTable table name allows letters, numbers, underscores, table name does not exceed 50 characters
  - KeyField cannot start with an underscore. Allows letters, numbers, and underscores. The total length cannot exceed 64 characters.
  - valueField cannot start with an underscore. Letters, numbers, and underscores are allowed. The single-field name does not exceed 64 characters. The total length of valueFields does not exceed 1024.
  - valueFields and keyField cannot have duplicate fields
- AppendColumns adds table fields. The field requirements are the same as those for creating a table.
- OpenTable obtains the real address of the table, which is dedicated to the Solidity contract.
- desc reads the key and valueFiles of the table.
- **Interface Return Description:**

  - interfaces are returned as error codes

  | error code| Description|
  | :----- | :---------------- |
  | equal to 0| Success|
  | -50007 | Table field duplicate|
  | -50006 | The field value of Table is too long|
  | -50005 | The key value of Table is too long|
  | -50003 | The field name of Table is too long|
  | -50002 | Table name is too long|
  | -50001 | Table already exists|

## 4. CryptoPrecompiled

### Contract Address

- Solidity：0x100a
- WBC-Liquid: /sys/crypto_tools

### interface declaration

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract Crypto
{
    function sm3(bytes memory data) public view returns(bytes32){}
    function keccak256Hash(bytes memory data) public view returns(bytes32){}
    function sm2Verify(bytes32 message, bytes memory publicKey, bytes32 r, bytes32 s) public view returns(bool, address){}
    function curve25519VRFVerify(bytes memory message, bytes memory publicKey, bytes memory proof) public view returns(bool, uint256){}
}
```

### Interface Description

- `sm3`: calculate the hash of the specified data by using the national secret sm3 algorithm;
- `keccak256Hash`: use the keccak256 algorithm to calculate the hash of specified data;
- `sm2Verify`: Signatures are verified using the sm2 algorithm'(publicKey, r, s)Valid. Verify the state secret account derived from the public key by returning 'true'. If the verification fails, return 'false' and all 0 addresses.;
- `curve25519VRFVerify`: Given the VRF input and the VRF public key, use the VRF algorithm based on the ed25519 curve to verify whether the VRF proof is valid, and if the VRF proof is verified successfully, return 'true' and the VRF random number derived from the proof.；Returns' if VRF attestation verification fails(false, 0)`。(Not currently supported)

## 5. BFSPrecompiled

### Contract Address

- Solidity：0x100e
- WBC-Liquid: /sys/bfs

### interface declaration

Take Solidity for example:

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

    struct BfsInfo{
        string file_name;
        string file_type;
        string[] ext;
    }

contract BfsPrecompiled {
    function list(string memory absolutePath) public view returns (int32,BfsInfo[] memory){}
    function mkdir(string memory absolutePath) public returns (int32){}
    function link(string memory name, string memory version, address _address, string memory _abi) public returns (int32){}
    function readlink(string memory absolutePath) public view returns (address) {}
}
```

### Interface Description

- 'list ': The reference must be**absolute path**If it is a directory, the meta information of all files in the directory is returned.；Returns a single BfsInfo if it is a contract。
  - The absolute path cannot have special characters, the total length cannot exceed 56, and the total number of paths cannot exceed 6 levels
  - If the input path is' link ', then the' ext 'field string will return the contract address and ABI corresponding to the soft link, the 0th is the contract address, and the first is the ABI string.

- 'mkdir ': the reference must be**absolute path**Create a directory file in the specified path. Multi-level creation is supported. If the creation fails, an error code will be returned.。
  - The absolute path cannot have special characters, the total length cannot exceed 56, and the total number of paths cannot exceed 6 levels

- 'link ': replace the function of CNS, create a contract alias, the created soft links are in the' / apps / 'directory。
  - The contract name cannot contain special characters, and a directory with the same contract name will be created under '/ apps', which will fail if there is a non-directory resource with the same name.
  - The version number cannot contain special characters, and a link resource of '/ apps / contract name / version number' will be created in the '/ apps / contract name' directory
  - '/ apps / contract name / version number' as an absolute path, the total length cannot exceed 56
  - The contract address must be real and in normal condition.
  - ABI string does not exceed 16MB

- 'readlink ': Obtain the real address of the soft link. The parameter must be**absolute path**
  - The absolute path cannot have special characters, the total length cannot exceed 56, and the total number of paths cannot exceed 6 levels


| error code| Error Message| Error message / workaround|
| ------ | ---------------------------- | ------------------------------------------------------------ |
| 0      | Success                      | Success|
| -53006 | Wrong file type| Appears when calling the BFS touch interface with the wrong file type|
| -53005 | Wrong file path| This error occurs when calling the BFS interface to pass in the absolute path, the total length of the absolute path of the BFS cannot exceed 56, the total number of stages of the path cannot exceed 6, and it cannot contain special characters|
| -53003 | Failed to create folder| An exception occurs when creating a folder when calling the BFS link API. For example, the parent folder corresponding to the contract name already exists.|
| -51202 | Incoming version number or address is wrong| Appears when calling the BFS link interface. The version number cannot contain special characters and the contract address must also exist.|
| -53002 | File already exists| The file name created when calling the BFS writer interface already exists|
| -53001 | File does not exist| The file corresponding to the absolute path does not exist when calling the BFS read interface|
