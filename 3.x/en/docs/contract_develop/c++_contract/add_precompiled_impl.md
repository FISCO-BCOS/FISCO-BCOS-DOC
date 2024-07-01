# Precompiled Contract Development Guide

Tags: "Precompiled Contracts" "Development Guide" "" Blockchain Application Development ""

----------
This article takes the HelloWorld contract as an example to show you how to use the pre-compiled contract version of HelloWorld.。

## Development premise

The precompiled contract is to use C.++To implement a smart contract, the developer must have a C++Basic development ability, familiar with CMake operation。

The following rules must be followed before developing a precompiled contract:

1. Pre-compiled contracts are built into nodes and have a larger operational range than ordinary contracts, so the implementation must meet security audit requirements and must output critical storage write information in the log for information audit.。
2. Before submitting the node code of the new precompiled contract, you must go through the code review of professional peers. For details, please refer to the FISCO BCOS code submission process.。
3. The precompiled contract needs to agree on the write operation of the storage, so the execution result of the precompiled contract must be strongly consistent, and random numbers are not allowed to be used or indirectly referenced.。
4. Multiple precompiled contracts should not share the same storage table, otherwise there may be inconsistent execution in multiple calls.。
5. When pre-compiled contracts across versions have data compatibility issues, compatibility must be done.。

## step1 Defining the HelloWorld Interface

Let's first look at the Solidity version of the HelloWorld contract that we want to implement.。Solidity version of HelloWorld, there is a member name for storing data, two interfaces get(),set(string)for reading and setting the member variable respectively。

```solidity
pragma solidity>=0.6.10 <0.8.20;

contract HelloWorld {
    string name;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public {
        name = n;
    }
}
```

Solidity's interface calls are encapsulated as a transaction, where transactions that call read-only interfaces are not packaged into blocks, while write-interface transactions are packaged into blocks.。Since the underlying layer needs to determine the called interface and parse the parameters based on the ABI code in the transaction data, the interface needs to be defined first。The ABI interface rules for precompiled contracts are exactly the same as Solidity. When defining a precompiled contract interface, you usually need to define a Solidity contract with the same interface.**Interface Contract**。The interface contract needs to be used when calling the precompiled contract.。

```solidity
pragma solidity >=0.6.10 <0.8.20;

contract HelloWorldPrecompiled{
    function get() public view returns (string memory);
    function set(string memory n) public;
}
```

## step2 Design storage structure

When precompiled contracts involve storage operations, you need to determine the stored table information.(Table name and table structure. The stored data is abstracted into a table structure in FISCO BCOS.)。If variable storage is not involved in the contract, you can ignore this step。

For HelloWorld, we design the following table。The table only stores a pair of key-value pairs. The key field is hello _ key, and the value field is hello _ value to store the corresponding string value.(string)Interface modification, through get()interface acquisition。

| key       | value          |
|-----------|----------------|
| hello_key | "Hello World!" |

## step3 Implementing contract logic

To implement the invocation logic for the new contract, a new C++class, which needs to inherit the Precompiled class, overload the call function, and implement the calling behavior of each interface in the call function。

```c++
std::shared_ptr<PrecompiledExecResult> call(
        std::shared_ptr<executor::TransactionExecutive> executive,
        PrecompiledExecResult::Ptr callParameters) override;
```

The call function has two parameters, executive saves the context of the transaction execution, 'callParameters' is the parameter information of the calling contract, this call corresponds to the contract interface and the parameters of the interface can be obtained from the 'callParameters' parsing。 Next, we have the source code**bcos-executor/src/precompiled/extension**directory implements the HelloWorldPrecompiled class, overloads the call function, and implements get()/set(string)Two interfaces。

### interface registration

```c++
/ / Define all interfaces in the class
const char* const HELLO_WORLD_METHOD_GET = "get()";
const char* const HELLO_WORLD_METHOD_SET = "set(string)";

/ / interface registration in constructor
HelloWorldPrecompiled::HelloWorldPrecompiled(crypto::Hash::Ptr _hashImpl) : Precompiled(_hashImpl)
{/ / name2Selector is a member of the Precompiled class, which saves the mapping relationship of interface calls
    name2Selector[HELLO_WORLD_METHOD_GET] = getFuncSelector(HELLO_WORLD_METHOD_GET);
    name2Selector[HELLO_WORLD_METHOD_SET] = getFuncSelector(HELLO_WORLD_METHOD_SET);
}
```

### Create Table

```c++
/ / Define table name
const std::string HELLO_WORLD_TABLE_NAME = "_ext_hello_world_";
/ / Primary key field
const std::string HELLOWORLD_KEY_FIELD = "key";
/ / Other fields, multiple fields are separated by commas, such as"field0,field1,field2"
const std::string HELLOWORLD_VALUE_FIELD = "value";
```

### Add logic to open the table in the call function

```c++
/ / Get the storage object
auto storage = _executive->storage();
/ / In the call function, open when the table exists, otherwise create the table first
auto table = storage.openTable(precompiled::getTableName(HELLO_WORLD_TABLE_NAME));
if (!table)
{
    / / table does not exist, first create
    table = _executive->storage().createTable(
        precompiled::getTableName(HELLO_WORLD_TABLE_NAME), HELLO_WORLD_VALUE_FIELD);
    if (!table)
    {
       / / Failed to create table, error code returned
    }
}
```

### Distinguish calling interfaces

```c++
uint32_t func = getParamFunc(_param);
if (func == name2Selector[HELLO_WORLD_METHOD_GET])
{// get() Interface Call Logic
}
else if (func == name2Selector[HELLO_WORLD_METHOD_SET])
{// set(string) Interface Call Logic
}
else
{/ / Unknown interface, call error, error code returned
}
```

### Parsing and returning parameters

The parameters when calling the contract are included in the _ param parameter of the call function. If it is a Solidity call, the Solidity ABI encoding is used.-Liquid (WBC)-Liquid) uses Scale encoding。

PrecompiledCodec encapsulates the interface of two encoding formats. You can use PrecompiledCodec。

### HelloWorldPrecompiled implementation

```c++
std::shared_ptr<PrecompiledExecResult> HelloWorldPrecompiled::call(
    std::shared_ptr<executor::TransactionExecutive> _executive, PrecompiledExecResult::Ptr _callParameters)
{
    / / parse function interface
    uint32_t func = getParamFunc(_param);
    bytesConstRef data = getParamData(_param);
    auto blockContext = _executive->blockContext().lock();
    / / Create a CodecWrapper codec object
    auto codec = CodecWrapper(blockContext->hashHandler(), blockContext->isWasm());
     / / Open _ ext _ hello _ world _ table, omit
    ........
```

get()interface implementation

```c++
/ / Distinguish calling interfaces. The specific calling logic of each interface
    if (func == name2Selector[HELLO_WORLD_METHOD_GET])
    {  
        // get() interface invocation
        / / Default return value
        std::string retValue = "Hello World!";

        auto entry = table->getRow(HELLO_WORLD_KEY_FIELD_NAME);
        if (!entry)
        {
            retValue = entry->getField(HELLO_WORLD_VALUE_FIELD);
        }
        callResult->setExecResult(codec->encode(retValue));
    }
```

set interface implementation

```c++
    else if (func == name2Selector[HELLO_WORLD_METHOD_SET])
    {  // set(string) function call

        std::string strValue;
        codec->decode(data, strValue);
        auto entry = table->getRow(HELLO_WORLD_KEY_FIELD_NAME);
        gasPricer->updateMemUsed(entry->capacityOfHashField());
        gasPricer->appendOperation(InterfaceOpcode::Select, 1);
        entry->setField(HELLO_WORLD_VALUE_FIELD, strValue);

        table->setRow(HELLO_WORLD_KEY_FIELD_NAME, *entry);
        gasPricer->appendOperation(InterfaceOpcode::Update, 1);
        gasPricer->updateMemUsed(entry->capacityOfHashField());
        getErrorCodeOut(callResult->mutableExecResult(), 1, *codec);
    }
    else
    {  / / Parameter error, unknown interface call
        callResult->setExecResult(codec->encode(u256((int)CODE_UNKNOW_FUNCTION_CALL)));
    }
    return callResult;
}
```

## step4 Assign and register a contract address

When FSICO BCOS 3.0 executes a transaction, the contract address is used to distinguish whether it is a pre-compiled contract, so after the pre-compiled contract is developed, it needs to be registered as the pre-compiled contract registration address at the bottom.。

The user-allocated address space is 0x5001-0xffff, the user needs to assign an unused address to the newly added precompiled contract.**Precompiled contract addresses must be unique and non-conflicting**。

Developers need to modify 'bcos-Executor / src / executor / TransactionExecutor.cpp 'file, insert the contract address and contract object instance into the' m _ constantPrecompiled 'Map in the initPrecompiled function, and register the HelloWorldPrecompiled contract as follows:

```c++
auto helloPrecompiled = std::make_shared<HelloWorldPrecompiled>(m_hashImpl);
m_constantPrecompiled->insert({"0000000000000000000000000000000000005001", std::move(helloPrecompiled)});
```

## Step5 compiled source code

Refer to FISCO BCOS 3.x manual-> Get Executable Program-> [source code compilation](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html)。Note that the implementations of HelloWorldPrecompile.cpp and HelloWorldPrecompile.h need to be placed in the FISCO-BCOS / libprecompiled / extension directory。

## HelloWorld precompiled contract call

### Invoking the HelloWorld precompiled contract using the console

Create a HelloWorldPrecompiled.sol file in the solidity / contracts console. The file content is the interface declaration of the HelloWorld precompiled contract, as follows

```solidity
pragma solidity >=0.6.10 <0.8.20;
contract HelloWorldPrecompiled{
    function get() public constant returns(string memory);
    function set(string memory n);
}
```

Use the compiled binary to build the node, and then execute the following statement to call the

```shell
[group] />: call HelloWorldPrecompiled 0x5001 get
Hello World!
```

### Invoking the HelloWorld precompiled contract in Solidity

We try to create a precompiled contract object in the Solidity contract and call its interface。Create a HelloWorldHelper.sol file in the Solidity / contracts console. The file content is as follows

```solidity
pragma solidity >=0.6.10 <0.8.20;
import "./HelloWorldPrecompiled.sol";

contract HelloWorldHelper {
    HelloWorldPrecompiled hello;
    function HelloWorldHelper() {
        / / Call the HelloWorld precompiled contract
        hello = HelloWorldPrecompiled(0x5001); 
    }
    function get() public constant returns(string memory) {
        return hello.get();
    }
    function set(string memory m) {
        hello.set(m);
    }
}
```

Deploy the HelloWorldHelper contract, and then call the HelloWorldHelper contract interface. The result is as follows:

```shell
[group] />: deploy HelloWorldHelper
transaction hash: 0x0fc5d6ad61d756a28235dde3041e7ea9acef7cb64babb9e5815532dbd9846681
contract address: 0x33E56a083e135936C1144960a708c43A661706C0
currentAccount: 0x3977d248ce98f3affa78a800c4f234434355aa77

[group] />: call HelloWorldHelper 0x33E56a083e135936C1144960a708c43A661706C0 get
Hello World!
```

Here, you can congratulate you on the smooth completion of the development of the HelloWorld precompiled contract, the development process of other precompiled contracts is the same.。
