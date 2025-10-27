# EVM – Ethereum Virtual Machine

The process of consensus is operated through contract deployment on blockchain. Ethereum Virtual Machine is a code executor of smart contract.

When smart contract is compiled to binary file and deployed on blockchain, users will start the execution of smart contract by calling its API. EVM executes smart contract and modifies blockchain data (status). The modified data will be in consensus to ensure consistency.



## EVMC – Ethereum Client-VM Connector API

The latest version of Ethereum extracts EVM from node codes and makes it an independent model. The interaction between EVM and nodes has been abstracted to be the standard of EVMC API. Through EVMC, nodes can connect with different virtual machines, not limited to traditional virtual machine based on solidity.

Traditional solidity virtual machine is called interpreter in Ethereum. We will introduce the implementation of interpreter in the following context.

### EVMC API

EVMC has defined 2 types of API:

- Instance API：API for nodes to call EVM
- Callback API：API for EVM to call back node

EVM itself doesn't store status data. Node operates EVM through instance API, and EVM, in return, call Callback API to operate nodes' status.

![](../../../images/evm/evmc.png)

**Instance API**

The operations of nodes ton virtual machine include create, destroy, set, etc..

API is defined in evmc_instance (evmc.h)

* abi_version  
* name  
* version  
* destroy  
* execute  
* set_tracer  
* set_option

**Callback API**

The operations of EVM on nodes mainly include read/write status and block information.

API defined in evmc_context_fn_table（evmc.h）.


* evmc_account_exists_fn account_exists
* evmc_get_storage_fn get_storage
* evmc_set_storage_fn set_storage
* evmc_get_balance_fn get_balance
* evmc_get_code_size_fn get_code_size
* evmc_get_code_hash_fn get_code_hash
* evmc_copy_code_fn copy_code
* evmc_selfdestruct_fn selfdestruct
* evmc_call_fn call
* evmc_get_tx_context_fn get_tx_context
* evmc_get_block_hash_fn get_block_hash
* evmc_emit_log_fn emit_log


## EVM execution

### EVM instruction

Solidity is the execution language of contract. After compiled by solc, solidity will become EVM instruction like an assembly programming language. Interpreter has defined a complete collection of instructions. After compiled, solidity will generate binary file, which is the collection of EVM instructions. Transactions are sent in binary form to nodes, who will call EVM through EVMC to execute the instructions once receiving it. In EVM, the logic of the instructions are simulated in codes.

Solidity is based on stack. EVM calls by stack when executing binary file.

**Arithmetic instruction**

An ADD instruction is realized in following EVM codes. SP is the pointer of stack, which takes data from top 1 and 2 ```SP[0]```, ```SP[1]```, sums and writes to the top of SPP ```SPP[0]```.

``` cpp
CASE(ADD)
{
    ON_OP();
    updateIOGas();

    // pops two items and pushes their sum mod 2^256.
    m_SPP[0] = m_SP[0] + m_SP[1];
}
```

**Jump instruction**

JUMP instruction realize the jumping between binary codes. First, take out the address to jump to from stack top ```SP[0]```, verify if it has crossed the limit, put into program counter (PC). The next instruction will start execution from where PC appoints.

``` cpp
CASE(JUMP)
{
    ON_OP();
    updateIOGas();
    m_PC = verifyJumpDest(m_SP[0]);
}
```

**Read status instruction**

SLOAD can inquire status data with these steps: take out the key to visit from stack top ```SP[0]```, call callback function ```get_storage()``` with key as the parameter to check the value of key. Then, write the value to SPP top ```SPP[0]```.

``` cpp
CASE(SLOAD)
{
    m_runGas = m_rev >= EVMC_TANGERINE_WHISTLE ? 200 : 50;
    ON_OP();
    updateIOGas();

    evmc_uint256be key = toEvmC(m_SP[0]);
    evmc_uint256be value;
    m_context->fn_table->get_storage(&value, m_context, &m_message->destination, &key);
    m_SPP[0] = fromEvmC(value);
}
```

**Write status instruction**

SSTORE instruction can write data to node status with these steps: take out key and value from stack top 1 and 2 ```SP[0]```、```SP[1]```, call callback function ```set_storage()``` with key and value as the parameters, write it to node status.

``` cpp
CASE(SSTORE)
{
    ON_OP();
    if (m_message->flags & EVMC_STATIC)
        throwDisallowedStateChange();

    static_assert(
        VMSchedule::sstoreResetGas <= VMSchedule::sstoreSetGas, "Wrong SSTORE gas costs");
    m_runGas = VMSchedule::sstoreResetGas;  // Charge the modification cost up front.
    updateIOGas();

    evmc_uint256be key = toEvmC(m_SP[0]);
    evmc_uint256be value = toEvmC(m_SP[1]);
    auto status =
        m_context->fn_table->set_storage(m_context, &m_message->destination, &key, &value);

    if (status == EVMC_STORAGE_ADDED)
    {
        // Charge additional amount for added storage item.
        m_runGas = VMSchedule::sstoreSetGas - VMSchedule::sstoreResetGas;
        updateIOGas();
    }
}
```

**Call contract instruction**

CALL instruction is used to call another contract according to the address. First, EVM judges if it's CALL instruction, calls ```caseCall()``` to take out data from stack using ```caseCallSetup()```, encapsulates into msg, calls callback function of EVMC with msg as the parameter. When the ```call()``` is called back, Eth will start a new EVM to handle calls and return the execution result of the new EVM to current EVM through parameter of ```call()```. The current EVM writes the result to SPP and ends call. It shares the same logic with the creation of contract.

``` cpp
CASE(CALL)
CASE(CALLCODE)
{
    ON_OP();
    if (m_OP == Instruction::DELEGATECALL && m_rev < EVMC_HOMESTEAD)
        throwBadInstruction();
    if (m_OP == Instruction::STATICCALL && m_rev < EVMC_BYZANTIUM)
        throwBadInstruction();
    if (m_OP == Instruction::CALL && m_message->flags & EVMC_STATIC && m_SP[2] != 0)
        throwDisallowedStateChange();
    m_bounce = &VM::caseCall;
}
BREAK

void VM::caseCall()
{
    m_bounce = &VM::interpretCases;

    evmc_message msg = {};

    // Clear the return data buffer. This will not free the memory.
    m_returnData.clear();

    bytesRef output;
    if (caseCallSetup(msg, output))
    {
        evmc_result result;
        m_context->fn_table->call(&result, m_context, &msg);

        m_returnData.assign(result.output_data, result.output_data + result.output_size);
        bytesConstRef{&m_returnData}.copyTo(output);

        m_SPP[0] = result.status_code == EVMC_SUCCESS ? 1 : 0;
        m_io_gas += result.gas_left;

        if (result.release)
            result.release(&result);
    }
    else
    {
        m_SPP[0] = 0;
        m_io_gas += msg.gas;
    }
    ++m_PC;
}
```



## Summary

EVM is a machine for status execution, which input as binary instruction of compiled solidity and status data of nodes and output as the modification of node status. Ethereum's adaptability for multiple virtual machines is realized through EVMC. But till now, there has not been a production available virtual machine except interpreter. It is probably too difficult to run the same codes on different virtual machines but get the same result. BCOS will keep following up its development.
