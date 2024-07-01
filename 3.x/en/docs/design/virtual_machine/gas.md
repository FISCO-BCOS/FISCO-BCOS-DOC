# Gas Billing

Tags: "Gas" "Smart Contract" "Virtual Machine" "

----
EVM virtual machines have a set of Gas mechanisms to measure the CPU, memory and storage resources consumed by the chain on each transaction.。FISCO BCOS introduces Precompiled contract, supports built-in C++In order to improve the security of precompiled contracts, FISCO BCOS v2.4.0 introduces the Gas mechanism in precompiled contracts.。

## Precompiled contracts support Gas calculations

### Module Architecture

In FISCO BCOS v2.4.0, the 'PrecompiledGas' module is added to calculate gas. The gas overhead includes CPU, memory, and storage. The module diagram is as follows:

![](../../../images/evm/precompiled_gas.png)

'PrecompiledGas' mainly records the basic operations called during the execution of the Precompiled contract for each transaction, and the Gas that consumes memory.

- When the virtual machine executes a transaction and calls the 'call' interface of the 'Precompiled' contract, each time a basic operation is called, the corresponding 'OPCode' is added to the**Runtime Instruction Collection**中

- When the virtual machine executes a transaction and calls the 'call' interface of the 'Precompiled' contract, the memory consumed by the runtime of the 'PrecompiledGas' is updated when the memory occupied by the basic operation changes

- After the 'Precompiled' contract is executed, you can call the interface to calculate the Gas consumption of the 'Precompiled' contract based on the set of instructions executed and the memory consumed during the running of the 'Precompiled' contract.。

### Precompiled contract Gas measure

The FISCO BCOS Precompiled contract Gas measurement standard refers to EVM, which mainly includes CPU, memory and storage dimensions.。The following details the specific Gas calculation method for precompiled contracts.。

#### Precompiled Contract Memory Gas Calculation

Precompiled contract memory consumption mainly comes from input, output and additional memory consumption at runtime。When the total memory consumed by a transaction is' txMemUsed ', the corresponding memory gas is calculated as follows。That is, add 'memoryGasUnit' gas every 32 bytes, and the value of 'memoryGasUnit' is 3.

```
    MemoryGas(txMemUsed) = memoryGasUnit * txMemUsed / 32 + (txMemUsed * txMemUsed)/512
```

#### Precompiled Contract CPU, Storage Gas Compute

In order to calculate the Gas consumed by the underlying operation of the Precompiled contract, FISCO BCOS v2.4.0 maps the Precompiled contract to a specific opcode and defines the Gas corresponding to each underlying operation.。

##### The opcode corresponding to the underlying operation of the precompiled contract.

The 'PrecompiledGas' module maps Precompiled contract base operations to opcodes as follows:

Operation| Description|  Opcode
|-------|---------|--------|
EQ | EQ call to ConditionPrecompiled to determine whether two operands are equal| 0x00 |
GE | GE call of ConditionPrecompiled to judge whether the left value is greater than or equal to the right value| 0x01 |
GT | GT call of ConditionPrecompiled to determine whether the left value is greater than the right value| 0x02 |
LE | The LE call of ConditionPrecompiled to determine whether the left value is less than or equal to the right value| 0x03 |
LT | LT call of ConditionPrecompiled to determine whether the left value is less than the right value| 0x04 |
NE | The NE call of ConditionPrecompiled to determine whether the left value is not equal to the right value| 0x05 |
Limit | The Limit call of ConditionPrecompiled, which limits the number of pieces of data queried from the CRUD interface.| 0x06 |
GetInt | The getInt call to EntryPrecompiled converts the string to int256 / uint256 and returns| 0x07 |
GetAddr | GetAddress call of EntryPrecompiled, converting string to Address| 0x08 |
Set | Set call to EntryPrecompiled, setting the value of the specified Key to the specified Value| 0x09 |
GetByte32 | EntryPrecompiled getByte32, convert string to byte 32| 0x0a |
GetByte64 | EntryPrecompiled getByte64, converting a string to byte 64| 0x0b |
GetString | GetString of the EntryPrecompiled parameter to obtain the value corresponding to the input Key| 0x0c |
CreateTable | CreateTable call for TableFactoryPrecompiled, creating a table| 0x0d |
OpenTable | OpenTable call for TableFactoryPrecompiled, open table| 0x0e |
Select | Select call of TablePrecompiled, query table| 0x0f |
Insert | Insert call of TablePrecompiled to insert the specified record into the table| 0x10 |
Update | Update call of TablePrecompiled to update the specified record in the specified table| 0x11 |
Remove | Remove call of TablePrecompiled to delete the specified record in the specified table| 0x12 |
PaillierAdd | homomorphic plus interface| 0x13 |
GroupSigVerify | Group Signature Verification Interface| 0x14 |
RingSigVerify | Ring Signature Verification Interface| 0x15 |

##### Precompiled contract basis operation measure

'PrecompiledGas' defines the Gas consumption corresponding to each underlying operation of the Precompiled contract, as follows:

| Operation| Gas consumption|
|-------|--------|
EQ | 3 |
GE | 3 |
GT | 3 |
LE | 3 |
LT | 3 |
NE | 3 |
Limit | 3 |
GetInt | 3 |
GetAddr | 3 |
Set | 3 |
GetByte32 | 3 |
GetByte64 | 3 |
GetString | 3 |
CreateTable | 16000 |
OpenTable | 200 |
Select | 200 |
Insert | 10000 |
Update | 10000 |
Remove | 2500 |
PaillierAdd | 20000 |
GroupSigVerify | 20000 |
RingSigVerify | 20000 |

### Gas measurement

The following describes the EVM virtual machine and the Precompiled contract Gas metrics, respectively:

**EVM Virtual Machine Gas Measurement**

Gas | Description| Gas consumption|
|-------|--------|-------|
CreateGas | Gas consumption for contract creation| 32000 |
sloadGas| Gas consumed to read 32 bytes of data from storage| 200 |
sstoreSetGas| Gas consumption for adding 32 bytes of data to storage| 20000 |
sstoreResetGas| Gas consumption for updating 32 bytes of stored data|5000 |

**Precompiled contract Gas measure**

Gas | Description| Gas consumption|
-------|--------|-------|
CreateTableGas| Gas consumption for creating a table| 16000 |
StoreGas| Gas consumption for inserting data into a table or updating data in a table| 10000 |
RemoveGas| Gas consumption for deleting data in a table| 2500 |

### Configuration Item

```eval_rst
.. note::
    EVM Gas metrics support plug-in configuration items in the "genesis" file
```
