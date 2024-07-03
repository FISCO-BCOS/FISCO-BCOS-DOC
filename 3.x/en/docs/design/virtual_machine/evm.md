# EVM Engine

Tags: "EVM" "Smart Contract" "Virtual Machine" "

----

On the blockchain, users complete actions that require consensus by running contracts deployed on the blockchain。Ethereum virtual machine, the executor of smart contract code。

When the smart contract is compiled into a binary file, it is deployed on the blockchain。The user triggers the execution of the smart contract by calling the interface of the smart contract。The EVM executes the code of the smart contract, modifying the data (state) on the current blockchain。The modified data will be agreed upon to ensure consistency。

## EVMC – Ethereum Client-VM Connector API

The new version of Ethereum strips the EVM from the node code to form a separate module。The interaction between EVM and node abstracts the EVMC interface standard。Through EVMC, nodes can interface with multiple virtual machines。

The original EVM virtual machine is called interpreter in Ethereum. The following mainly explains the implementation of interpreter。

### EVMC Interface

EVMC defines two main types of invocation interfaces:

- Instance interface: the interface through which the node invokes the EVM
- Callback interface: interface of EVM callback node

The EVM itself does not save state data. The node operates the EVM through the instance interface. The EVM, in turn, adjusts the Callback interface to operate the state of the node。

![](../../../images/evm/evmc.png)

**Instance interface**

Defines the operation of the node to the virtual machine, including creation, destruction, setup, etc。

The interface is defined in evmc _ instance (evmc.h)

* abi_version
* name
* version
* destroy
* execute
* set_tracer
* set_option

**Callback Interface**

Defines the operations of EVM on nodes, mainly for state reading and writing, block information reading and writing, etc。

The interface is defined in evmc _ context _ fn _ table (evmc.h)。

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

## EVM Execution

### EVM command

Solidity is the execution language of the contract, which is compiled by solc and becomes an assembly-like EVM instruction。Interpreter defines a complete set of instructions。After the solidity is compiled, the binary file is generated, the binary file is the collection of EVM instructions, the transaction is sent to the node in binary form, the node receives, through the EVMC call EVM to execute these instructions。In EVM, the logic of these instructions is implemented in code emulation。

Solidity is a stack-based language, and EVM is called as a stack when executing binary。

**Examples of Arithmetic Instruction**

An ADD instruction, the code in the EVM is implemented as follows。SP is the pointer of the stack. Take out the data from the first and second positions on the top of the stack ("'SP [0]"', "'SP [1]"'), add and write the data to the top of the result stack SPP "'SPP [0]"'。

``` cpp
CASE(ADD)
{
    ON_OP();
    updateIOGas();

    // pops two items and pushes their sum mod 2^256.
    m_SPP[0] = m_SP[0] + m_SP[1];
}
```

**Jump Instruction Example**

JUMP instruction, which implements the jump between binary code。First, from the top of the stack "'SP [0]"' take out the address to be jumped, verify whether it is out of bounds, into the program counter PC, the next instruction, will be executed from the position pointed to by the PC。

``` cpp
CASE(JUMP)
{
    ON_OP();
    updateIOGas();
    m_PC = verifyJumpDest(m_SP[0]);
}
```

**Example of Status Read Instruction**

SLOAD can query status data。The general procedure is to "SP [0]" from the top of the stack, take the key to be accessed as an argument, and then call the evmc callback function "get _ storage()"', query the value corresponding to the corresponding key。Then write the read value to the top of the result stack SPP "'SPP [0]"'。

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

**State Write Instruction Example**

The SSTORE instruction can write data to the state of the node. The general procedure is to take out key and value from the first and second positions on the top of the stack ("'SP [0]"', "'SP [1]"'), take key and value as parameters, and call evmc's callback function "'set _ storage()"', write the status of the node。

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

**Examples of contract call instructions**

The CALL instruction can call another contract based on the address。First, the EVM determines that it is a CALL instruction and calls "caseCall."()"', in caseCall()"', use"' caseCallSetup()"'Take the data from the stack, package it into msg, and call evmc's callback function as an argument。Eth is called back "()"'After that, start a new EVM, process the call, and then pass the execution result of the new EVM to" call()"'parameter is returned to the current EVM, the current EVM writes the result to the result stack SSP, the call ends。The logic for contract creation is similar to this logic。

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

## SUMMARY

EVM is a state execution machine, the input is the binary instructions compiled by solidity and the state data of the node, and the output is the change of the node state。Ethereum achieves compatibility of multiple virtual machines through EVMC。
