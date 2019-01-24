# EVM 以太坊虚拟机

以太坊虚拟机，可理解为一个执行器，输入是指令和当前的状态数据，输出是对状态的改变。

## EVMC – Ethereum Client-VM Connector API

新版本的以太坊将EVM从节点代码中剥离出来，形成一个独立的模块。EVM与节点的交互，抽象出EVMC接口标准。通过EVMC，节点可以对接多种虚拟机，而不仅限于传统的基于solidity的虚拟机。

传统的solidity虚拟机，在以太坊中称为interpreter，下文主要解释interpreter的实现。

### EVMC 接口

EVMC主要定义了两种调用的接口：

- Instance接口：节点调用EVM的接口
- Callback接口：EVM回调节点的接口

EVM本身不保存状态数据，节点通过instance接口操作EVM，EVM反过来，调Callback接口，对节点的状态进行操作。

![](../../../images/evm/evmc.png)

**Instance 接口**

定义了节点对虚拟机的操作，包括创建，销毁，设置等。

此处给出相关代码及注释（evmc.h）

``` c
/**
 * The EVM instance.
 *
 *  Defines the base struct of the EVM implementation.
 */
struct evmc_instance
{
    /**
     *  EVMC ABI version implemented by the EVM instance.
     *
     *  Used to detect ABI incompatibilities. The EVMC ABI version
     *  represented by this file is in ::EVMC_ABI_VERSION.
     */
    const int abi_version;

    /**
     * The name of the EVMC VM implementation.
     *
     *  It MUST be a NULL-terminated not empty string.
     */
    const char* name;

    /**
     * The version of the EVMC VM implementation, e.g. "1.2.3b4".
     *
     *  It MUST be a NULL-terminated not empty string.
     */
    const char* version;

    /** Pointer to function destroying the EVM instance. */
    evmc_destroy_fn destroy;

    /** Pointer to function executing a code by the EVM instance. */
    evmc_execute_fn execute;

    /**
     * Optional pointer to function setting the EVM instruction tracer.
     *
     * If the EVM does not support this feature the pointer can be NULL.
     */
    evmc_set_tracer_fn set_tracer;

    /**
     * Optional pointer to function modifying VM's options.
     *
     *  If the VM does not support this feature the pointer can be NULL.
     */
    evmc_set_option_fn set_option;
};
```

**Callback接口**

定义了EVM对节点的操作，主要是对state读写、区块信息的读写等。

此处给出相关代码及注释（evmc.h）

``` c
struct evmc_context_fn_table
{
    /** Check account existence callback function. */
    evmc_account_exists_fn account_exists;

    /** Get storage callback function. */
    evmc_get_storage_fn get_storage;

    /** Set storage callback function. */
    evmc_set_storage_fn set_storage;

    /** Get balance callback function. */
    evmc_get_balance_fn get_balance;

    /** Get code size callback function. */
    evmc_get_code_size_fn get_code_size;

    /** Get code hash callback function. */
    evmc_get_code_hash_fn get_code_hash;

    /** Copy code callback function. */
    evmc_copy_code_fn copy_code;

    /** Selfdestruct callback function. */
    evmc_selfdestruct_fn selfdestruct;

    /** Call callback function. */
    evmc_call_fn call;

    /** Get transaction context callback function. */
    evmc_get_tx_context_fn get_tx_context;

    /** Get block hash callback function. */
    evmc_get_block_hash_fn get_block_hash;

    /** Emit log callback function. */
    evmc_emit_log_fn emit_log;
};
```



## EVM 执行

### EVM 指令

solidity是合约的执行语言，solidity被solc编译后，变成类似于汇编的EVM指令。Interpreter定义了一套完整的指令集。solidity被编译后，生成二进制文件，二进制文件就是EVM指令的集合，交易以二进制的形式发往节点，节点收到后，通过EVMC调用EVM执行这些指令。在EVM中，用代码实现了这些指令的逻辑。

``` cpp
enum class Instruction : uint8_t
{
    STOP = 0x00,  ///< halts execution
    ADD,          ///< addition operation
    MUL,          ///< mulitplication operation
    SUB,          ///< subtraction operation
    DIV,          ///< integer division operation
    SDIV,         ///< signed integer division operation
    MOD,          ///< modulo remainder operation
    SMOD,         ///< signed modulo remainder operation
    ADDMOD,       ///< unsigned modular addition
    MULMOD,       ///< unsigned modular multiplication
    EXP,          ///< exponential operation
    SIGNEXTEND,   ///< extend length of signed integer

    LT = 0x10,  ///< less-than comparision
    GT,         ///< greater-than comparision
    SLT,        ///< signed less-than comparision
    SGT,        ///< signed greater-than comparision
    EQ,         ///< equality comparision
    ISZERO,     ///< simple not operator
    AND,        ///< bitwise AND operation
    OR,         ///< bitwise OR operation
    XOR,        ///< bitwise XOR operation
    NOT,        ///< bitwise NOT operation
    BYTE,       ///< retrieve single byte from word
    SHL,        ///< logical shift left operation
    SHR,        ///< logical shift right operation
    SAR,        ///< arithmetic shift right operation

    SHA3 = 0x20,  ///< compute SHA3-256 hash

    ADDRESS = 0x30,  ///< get address of currently executing account
    BALANCE,         ///< get balance of the given account
    ORIGIN,          ///< get execution origination address
    CALLER,          ///< get caller address
    CALLVALUE,       ///< get deposited value by the instruction/transaction responsible for this
                     ///< execution
    CALLDATALOAD,    ///< get input data of current environment
    CALLDATASIZE,    ///< get size of input data in current environment
    CALLDATACOPY,    ///< copy input data in current environment to memory
    CODESIZE,        ///< get size of code running in current environment
    CODECOPY,        ///< copy code running in current environment to memory
    GASPRICE,        ///< get price of gas in current environment
    EXTCODESIZE,     ///< get external code size (from another contract)
    EXTCODECOPY,     ///< copy external code (from another contract)
    RETURNDATASIZE = 0x3d,  ///< size of data returned from previous call
    RETURNDATACOPY = 0x3e,  ///< copy data returned from previous call to memory
    EXTCODEHASH = 0x3f,     ///< get external code hash

    BLOCKHASH = 0x40,  ///< get hash of most recent complete block
    COINBASE,          ///< get the block's coinbase address
    TIMESTAMP,         ///< get the block's timestamp
    NUMBER,            ///< get the block's number
    DIFFICULTY,        ///< get the block's difficulty
    GASLIMIT,          ///< get the block's gas limit

    POP = 0x50,  ///< remove item from stack
    MLOAD,       ///< load word from memory
    MSTORE,      ///< save word to memory
    MSTORE8,     ///< save byte to memory
    SLOAD,       ///< load word from storage
    SSTORE,      ///< save word to storage
    JUMP,        ///< alter the program counter to a jumpdest
    JUMPI,       ///< conditionally alter the program counter
    PC,          ///< get the program counter
    MSIZE,       ///< get the size of active memory
    GAS,         ///< get the amount of available gas
    JUMPDEST,    ///< set a potential jump destination

    PUSH1 = 0x60,  ///< place 1 byte item on stack
    PUSH2,         ///< place 2 byte item on stack
    PUSH3,         ///< place 3 byte item on stack
    PUSH4,         ///< place 4 byte item on stack
    PUSH5,         ///< place 5 byte item on stack
    PUSH6,         ///< place 6 byte item on stack
    PUSH7,         ///< place 7 byte item on stack
    PUSH8,         ///< place 8 byte item on stack
    PUSH9,         ///< place 9 byte item on stack
    PUSH10,        ///< place 10 byte item on stack
    PUSH11,        ///< place 11 byte item on stack
    PUSH12,        ///< place 12 byte item on stack
    PUSH13,        ///< place 13 byte item on stack
    PUSH14,        ///< place 14 byte item on stack
    PUSH15,        ///< place 15 byte item on stack
    PUSH16,        ///< place 16 byte item on stack
    PUSH17,        ///< place 17 byte item on stack
    PUSH18,        ///< place 18 byte item on stack
    PUSH19,        ///< place 19 byte item on stack
    PUSH20,        ///< place 20 byte item on stack
    PUSH21,        ///< place 21 byte item on stack
    PUSH22,        ///< place 22 byte item on stack
    PUSH23,        ///< place 23 byte item on stack
    PUSH24,        ///< place 24 byte item on stack
    PUSH25,        ///< place 25 byte item on stack
    PUSH26,        ///< place 26 byte item on stack
    PUSH27,        ///< place 27 byte item on stack
    PUSH28,        ///< place 28 byte item on stack
    PUSH29,        ///< place 29 byte item on stack
    PUSH30,        ///< place 30 byte item on stack
    PUSH31,        ///< place 31 byte item on stack
    PUSH32,        ///< place 32 byte item on stack

    DUP1 = 0x80,  ///< copies the highest item in the stack to the top of the stack
    DUP2,         ///< copies the second highest item in the stack to the top of the stack
    DUP3,         ///< copies the third highest item in the stack to the top of the stack
    DUP4,         ///< copies the 4th highest item in the stack to the top of the stack
    DUP5,         ///< copies the 5th highest item in the stack to the top of the stack
    DUP6,         ///< copies the 6th highest item in the stack to the top of the stack
    DUP7,         ///< copies the 7th highest item in the stack to the top of the stack
    DUP8,         ///< copies the 8th highest item in the stack to the top of the stack
    DUP9,         ///< copies the 9th highest item in the stack to the top of the stack
    DUP10,        ///< copies the 10th highest item in the stack to the top of the stack
    DUP11,        ///< copies the 11th highest item in the stack to the top of the stack
    DUP12,        ///< copies the 12th highest item in the stack to the top of the stack
    DUP13,        ///< copies the 13th highest item in the stack to the top of the stack
    DUP14,        ///< copies the 14th highest item in the stack to the top of the stack
    DUP15,        ///< copies the 15th highest item in the stack to the top of the stack
    DUP16,        ///< copies the 16th highest item in the stack to the top of the stack

    SWAP1 = 0x90,  ///< swaps the highest and second highest value on the stack
    SWAP2,         ///< swaps the highest and third highest value on the stack
    SWAP3,         ///< swaps the highest and 4th highest value on the stack
    SWAP4,         ///< swaps the highest and 5th highest value on the stack
    SWAP5,         ///< swaps the highest and 6th highest value on the stack
    SWAP6,         ///< swaps the highest and 7th highest value on the stack
    SWAP7,         ///< swaps the highest and 8th highest value on the stack
    SWAP8,         ///< swaps the highest and 9th highest value on the stack
    SWAP9,         ///< swaps the highest and 10th highest value on the stack
    SWAP10,        ///< swaps the highest and 11th highest value on the stack
    SWAP11,        ///< swaps the highest and 12th highest value on the stack
    SWAP12,        ///< swaps the highest and 13th highest value on the stack
    SWAP13,        ///< swaps the highest and 14th highest value on the stack
    SWAP14,        ///< swaps the highest and 15th highest value on the stack
    SWAP15,        ///< swaps the highest and 16th highest value on the stack
    SWAP16,        ///< swaps the highest and 17th highest value on the stack

    LOG0 = 0xa0,  ///< Makes a log entry; no topics.
    LOG1,         ///< Makes a log entry; 1 topic.
    LOG2,         ///< Makes a log entry; 2 topics.
    LOG3,         ///< Makes a log entry; 3 topics.
    LOG4,         ///< Makes a log entry; 4 topics.

    // these are generated by the interpreter - should never be in user code
    PUSHC = 0xac,  ///< push value from constant pool
    JUMPC,         ///< alter the program counter - pre-verified
    JUMPCI,        ///< conditionally alter the program counter - pre-verified

    JUMPTO = 0xb0,  ///< alter the program counter to a jumpdest
    JUMPIF,         ///< conditionally alter the program counter
    JUMPSUB,        ///< alter the program counter to a beginsub
    JUMPV,          ///< alter the program counter to a jumpdest
    JUMPSUBV,       ///< alter the program counter to a beginsub
    BEGINSUB,       ///< set a potential jumpsub destination
    BEGINDATA,      ///< begine the data section
    RETURNSUB,      ///< return to subroutine jumped from
    PUTLOCAL,       ///< pop top of stack to local variable
    GETLOCAL,       ///< push local variable to top of stack

    XADD = 0xc1,    ///< addition operation
    XMUL,           ///< mulitplication operation
    XSUB,           ///< subtraction operation
    XDIV,           ///< integer division operation
    XSDIV,          ///< signed integer division operation
    XMOD,           ///< modulo remainder operation
    XSMOD,          ///< signed modulo remainder operation
    XLT = 0xd0,     ///< less-than comparision
    XGT,            ///< greater-than comparision
    XSLT,           ///< signed less-than comparision
    XSGT,           ///< signed greater-than comparision
    XEQ,            ///< equality comparision
    XISZERO,        ///< simple not operator
    XAND,           ///< bitwise AND operation
    XOOR,           ///< bitwise OR operation
    XXOR,           ///< bitwise XOR operation
    XNOT,           ///< bitwise NOT opertation
    XSHL = 0xdb,    ///< shift left opertation
    XSHR,           ///< shift right opertation
    XSAR,           ///< shift arithmetic right opertation
    XROL,           ///< rotate left opertation
    XROR,           ///< rotate right opertation
    XPUSH = 0xe0,   ///< push vector to stack
    XMLOAD,         ///< load vector from memory
    XMSTORE,        ///< save vector to memory
    XSLOAD = 0xe4,  ///< load vector from storage
    XSSTORE,        ///< save vector to storage
    XVTOWIDE,       ///< convert vector to wide integer
    XWIDETOV,       ///< convert wide integer to vector
    XGET,           ///< get data from vector
    XPUT,           ///< put data in vector
    XSWIZZLE,       ///< permute data in vector
    XSHUFFLE,       ///< permute data in two vectors

    CREATE = 0xf0,      ///< create a new account with associated code
    CALL,               ///< message-call into an account
    CALLCODE,           ///< message-call with another account's code only
    RETURN,             ///< halt execution returning output data
    DELEGATECALL,       ///< like CALLCODE but keeps caller's value and sender
    CREATE2 = 0xf5,     ///< create a new account with associated code. sha3((sender + salt + code)
    STATICCALL = 0xfa,  ///< like CALL except state changing operation are not permitted (will
                        ///< throw)
    REVERT = 0xfd,  ///< stop execution and revert state changes, without consuming all provided gas
    INVALID = 0xfe,  ///< dedicated invalid instruction
    SUICIDE = 0xff   ///< halt execution and register account for later deletion
};
```

Solidity是基于堆栈的语言，EVM在执行二进制时，也是以堆栈的方式进行调用。

**算术指令举例**

一条ADD指令，在EVM中的代码实现如下。SP是堆栈的指针，从栈顶第一和第二个位置（```SP[0]```、```SP[1]```）拿出数据，进行加和后，写入结果堆栈SPP的顶端```SPP[0]```。

``` cpp
CASE(ADD)
{
    ON_OP();
    updateIOGas();

    // pops two items and pushes their sum mod 2^256.
    m_SPP[0] = m_SP[0] + m_SP[1];
}
```

**跳转指令举例**

JUMP指令，实现了二进制代码间的跳转。首先从堆栈顶端```SP[0]```取出待跳转的地址，验证一下是否越界，放到程序计数器PC中，下一个指令，将从PC指向的位置开始执行。

``` cpp
CASE(JUMP)
{
    ON_OP();
    updateIOGas();
    m_PC = verifyJumpDest(m_SP[0]);
}
```

**状态读指令举例**

SLOAD可以查询状态数据。大致过程是，从堆栈顶端```SP[0]```取出要访问的key，把key作为参数，然后调evmc的callback函数```get_storage()``` ，查询相应的key对应的value。之后将读到的value写到结果堆栈SPP的顶端```SPP[0]```。

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

**状态写指令举例**

SSTORE指令可以将数据写到节点的状态中，大致过程是，从栈顶第一和第二个位置（```SP[0]```、```SP[1]```）拿出key和value，把key和value作为参数，调用evmc的callback函数```set_storage()``` ，写入节点的状态。

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

**合约调用指令举例**

CALL指令能够根据地址调用另外一个合约。首先，EVM判断是CALL指令，调用```caseCall()```，在caseCall()```中，用```caseCallSetup()```从堆栈中拿出数据，封装成msg，作为参数，调用evmc的callback函数call。Eth在被回调```call()```后，启动一个新的EVM，处理调用，之后将新的EVM的执行结果，通过```call()```的参数返回给当前的EVM，当前的EVM将结果写入结果堆栈SSP中，调用结束。合约创建的逻辑与此逻辑类似。

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



## 总结

EVM是一个状态执行的机器，输入是solidity编译后的二进制指令和节点的状态数据，输出是节点状态的改变。以太坊通过EVMC实现了多种虚拟机的兼容。但截至目前，并未出现除开interpreter之外的，真正生产可用的虚拟机。也许要做到同一份代码在不同的虚拟机上跑出相同的结果，是一件很难的事情。BCOS将持续跟进此部分的发展。







