# Wasm Engine

## FISCO BCOS Environment Interface Specification

The FISCO BCOS Environment Interface (FBEI) specification includes the blockchain underlying platform [FISCO BCOS](https://gitee.com/FISCO-BCOS/FISCO-BCOS)Application Programming Interface (API) exposed to the Wasm virtual machine。All APIs in the FBEI specification are implemented by FISCO BCOS, and programs running in the Wasm virtual machine can directly access these APIs to obtain the environment and state of the blockchain.。

### Data Type

In the FBEI specification, the data types of API parameters and return values are marked with 'i32', 'i32ptr', and 'i64', which are defined as follows:

```eval_rst
.. list-table::
   :header-rows: 1

   * - Type Mark
     - 定义
   * - i32
     - 32-bit integer, consistent with the definition of the i32 type in Wasm
   * - i32ptr
     - 32-bit integer, which is stored in the same way as the i32 type in Wasm, but is used to represent the memory offset in the virtual machine
   * - i64
     - 64-bit integer, consistent with the definition of the i64 type in Wasm
```

### API List

#### setStorage

**_ Description _**

Write key-value pair data to blockchain underlying storage for persistent storage。The byte sequence representing the key and value needs to be stored in the virtual machine memory first.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - keyOffset
     - i32ptr
     - Start address of the storage location of the key in the virtual machine memory
   * - keyLength
     - i32
     - Length of key
   * - valueOffset
     - i32ptr
     - The starting address of the location where the value is stored in the virtual machine memory
   * - valueLength
     - i32
     - Length of value
```

**_ Return Value _**

None。

```eval_rst
.. note::

   When setStorage is called, if the provided valueLength parameter is 0, the data corresponding to the key is deleted from the underlying storage of the blockchain.。In this case, the API implementation will directly skip the reading of the value, so the valueOffset parameter does not need to be given a valid value, and is generally set directly to 0.。
```

#### getStorage

**_ Description _**

According to the key provided, the corresponding value in the underlying storage of the blockchain is read into the memory of the virtual machine.。The byte sequence representing the key needs to be stored in the virtual machine memory and the memory area where the value is stored needs to be allocated in advance.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - keyOffset
     - i32ptr
     - Start address of the storage location of the key in the virtual machine memory
   * - keyLength
     - i32
     - Length of key
   * - valueOffset
     - i32ptr
     - Virtual machine memory start address for storing values
```

**_ Return Value _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Type
     - 描述
   * - i32
     - Length of value
```

#### getCallData

**_ Description _**

Copy the input data of the current transaction to the virtual machine memory, which needs to be allocated in advance to store the transaction input data.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - resultOffset
     - i32ptr
     - Start address of the VM memory used to store the current transaction input data
```

**_ Return Value _**

None。

#### getCallDataSize

**_ Description _**

Get the length of the current transaction input data。

**_ Parameters _**

None。

**_ Return Value _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Type
     - 描述
   * - i32
     - Length of the current transaction input data
```

#### getCaller

**_ Description _**

Obtain the address of the caller who initiated the contract call, and allocate the memory area to store the caller's address in advance.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - resultOffset
     - i32ptr
     - Virtual machine memory start address used to store the caller's address
```

**_ Return Value _**

None。

#### finish

**_ Description _**

Pass a sequence of bytes representing the return value to the host environment and end the execution process, which the host environment returns to the caller as part of the transaction receipt。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - dataOffset
     - i32ptr
     - The starting address of the virtual machine memory used to store the return value.
   * - dataLength
     - i32
     - Length of return value
```

**_ Return Value _**

None。

#### revert

**_ Description _**

Throws a sequence of bytes representing exception information to the host environment, which returns it to the caller as part of the transaction receipt。After calling this interface, the status in the transaction receipt will be marked as "rolled back."。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - dataOffset
     - i32ptr
     - The start address of the storage location of the exception information in the virtual machine memory.
   * - dataLength
     - i32
     - Length of exception information
```

**_ Return Value _**

None。

```eval_rst
.. note::

   The exception information needs to be a human-readable string to facilitate quick locating of the cause of the exception。
```

#### log

**_ Description _**

Create a transaction log。Up to 4 log indexes can be created for this log。The byte sequence representing the log data and its index needs to be stored in the virtual machine memory first.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - dataOffset
     - i32ptr
     - Start address of the storage location of log data in the virtual machine memory
   * - dataLength
     - i32
     - Length of log data
   * - topic1
     - i32ptr
     - Virtual machine memory start address for 1st log index, no time 0
   * - topic2
     - i32ptr
     - Virtual machine memory start address for 2nd log index, no time 0
   * - topic3
     - i32ptr
     - Virtual machine memory start address for 3rd log index, no time 0
   * - topic4
     - i32ptr
     - Virtual machine memory start address for 4th log index, no time 0
```

**_ Return Value _**

None。

```eval_rst
.. note::

   The length of the log index needs to be exactly 32 bytes。
```

#### getTxOrigin

**_ Description _**

Obtain the address of the caller who initiates the contract call at the beginning of the call chain, and allocate the memory area for storing the caller's address in advance.。Unlike the 'getCaller' interface, the caller address obtained by this interface must be an external account address.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - resultOffset
     - i32ptr
     - Virtual machine memory start address used to store the caller's address
```

**_ Return Value _**

None。

#### getBlockNumber

**_ Description _**

Get current block height。

**_ Parameters _**

None。

**_ Return Value _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Type
     - 描述
   * - i64
     - Current block height
```

#### getBlockTimestamp

**_ Description _**

Get the timestamp of the current block。

**_ Parameters _**

None。

**_ Return Value _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Type
     - 描述
   * - i64
     - Timestamp of the current block
```

#### call

**_ Description _**

To initiate an external contract call, the byte sequence representing the call parameters needs to be stored in the virtual machine memory.。After calling this interface, the execution process is blocked until the external contract call ends or an exception occurs.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - addressOffset
     - i32ptr
     - The starting address of the storage location of the called contract address in the virtual machine memory.
   * - dataOffset
     - i32ptr
     - Start address of the storage location of the call parameter in the virtual machine memory
   * - dataLength
     - i32
     - Length of call parameter
```

**_ Return Value _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Type
     - 描述
   * - i32
     - Call status, 0 indicates success, otherwise it indicates failure
```

#### getReturnDataSize

**_ Description _**

Gets the length of the return value of the external contract call, which can only be called after the external contract call is successful.。

**_ Parameters _**

None。

```eval_rst
.. list-table::
   :header-rows: 1

   * - Type
     - 描述
   * - i32
     - Return value length of external contract call
```

#### getReturnData

Obtain the return value of an external contract call. When using the call, allocate the memory area for storing the return value in advance according to the return result of getReturnDataSize.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - resultOffset
     - i32ptr
     - The starting address of the virtual machine memory used to store the return value.
```

**_ Return Value _**

None。

## FISCO BCOS Wasm Constraints

FISCO BCOS Wasm Contract Interface (FBWCI) specification contains conventions on contract file format and content。Compliance with the FBWCI specification requires contract files to run in the Wasm virtual machine built into FISCO BCOS。

### transmission format

All contracts must be encoded in [WebAssembly binary](https://webassembly.github.io/spec/core/binary/index.html)Format saving and transmission。

### Symbol Import

The contract file can only import the interfaces specified in the FBEI. All interfaces need to be imported from the namespace named 'bcos', and the signature must be consistent with the interface signature declared in the BCOS environment interface specification.。In addition to the 'bcos' command space, there is a special namespace called 'debug'。The function declared in the 'debug' namespace is mainly used in the debugging mode of the virtual machine. This namespace will not be enabled in the formal production environment. For more information, see Debugging mode.。

### Symbol export

The contract file must export exactly the following 3 symbols:

| Symbol Name| 描述|
| ------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| memory | Shared linear memory for exchanging data with the host environment|
| deploy | Initialization entry, no parameters and no return value, used to complete the work of state initialization。When the contract is first deployed to the chain, the host environment actively calls the function|
| main   | Execution entry, no parameters and no return value, used to execute specific contract logic。When there is a transaction sent to the contract, the host environment actively calls the function。The function exits normally when the trade is executed successfully；Otherwise throw an exception cause to the host environment and roll back the transaction|

### Debug Mode

Debug mode is a special mode for debugging virtual machines, which provides an additional set of debugging interfaces for contracts through the 'debug' namespace。However, in a formal production environment, if the contract bytecode attempts to import symbols from the 'debug' namespace, the deployment is rejected。The interfaces available in the 'debug' namespace are as follows, and all interfaces have no return value:

#### print32

**_ Description _**

Output a 32-bit integer value in the log at the bottom of the blockchain。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - value
     - i32
     - 32-bit integer value
```

#### print64

**_ Description _**

Output a 64-bit integer value in the log at the bottom of the blockchain。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - value
     - i64
     - 64-bit integer value
```

#### printMem

**_ Description _**

Output a piece of virtual machine memory in the form of printable characters in the log at the bottom of the blockchain。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - offset
     - i32
     - Start address of memory region
   * - len
     - i32
     - Length of memory area
```

#### printMemHex

Output a piece of virtual machine memory in the log at the bottom of the blockchain as a hexadecimal string.。

**_ Parameters _**

```eval_rst
.. list-table::
   :header-rows: 1

   * - Parameter Name
     - Type
     - 描述
   * - offset
     - i32
     - Start address of memory region
   * - len
     - i32
     - Length of memory area
```

### Start function

[Start function](https://webassembly.github.io/spec/core/syntax/modules.html#start-function) It is automatically executed when the virtual machine loads the contract bytecode, when the host environment does not yet have access to the shared memory provided by the virtual machine, which may result in a runtime exception being thrown, so the FBWCI specification states that start functions are not allowed in the contract file。
