# Parallel contract

FISCO BCOS provides development structure for parallel contract. Contract developed under the structure regulation can be parallelly executed by nodes of FISCO BCOS. The advantages of parallel contract include:

- high TPS: multiple independent transaction being executed at the same time can utilize the CPU resources to the most extent and reach high TPS

- scalable: improve the performance of transaction execution with better configuration of machine to support expansion of applications

The following context will introduce how to compile, deploy and execute FISCO BCOS parallel contract.

## Basic knowledge

### Parallel exclusion

Whether two transactions can be executed in parallel depends on whether they are mutually **exclusive**. By exclusive, it means the two transactions have intersection in their contract storage variables collection.

Taking payment transfer as an example, it involves transactions of payment transfer between users. Use transfer(X, Y) to represent the access of user X to user Y. The exclusion is as below.

| transaction                       | exclusive object  | intersection | exclusive or not                          |
| --------------------------------- | ----------------- | ------------ | ----------------------------------------- |
| transfer(A, B) and transfer(A, C) | [A, B] and [A, C] | [A]          | exclusive, cannot be executed parallelly  |
| transfer(A, B) and transfer(B, C) | [A, B] and [B, C] | [B]          | exclusive, cannot be executed parallelly  |
| transfer(A, C) and transfer(B, C) | [A, C] and [B, C] | [C]          | exclusive, cannot be executed parallelly  |
| transfer(A, B) and transfer(A, B) | [A, B] and [A, B] | [A, B]       | exclusive, cannot be executed parallelly  |
| transfer(A, B) and transfer(C, D) | [A, B] and [C, D] | no           | non-exclusive, can be executed parallelly |

Here are detailed definitions:

- **exclusive parameter**：parameter that is related to "read/write" of contract storage variable in contract **interface**. Such as the interface of payment transfer transfer(X, Y), in which X and Y are exclusive parameters.

- **exclusive object**：the exclusive content extracted from exclusive parameters. Such as the payment transfer interface transfer(X, Y). In a transaction that calls the interface, the parameter is transfer(A, B), then the exclusive object is [A, B]; for another transaction that calls parameter transfer(A, C), the exclusive object is [A, C].

**To judge whether 2 transactions at the same moment can be executed in parallel depends on whether there is intersection between their exclusive objects. Transaction without intersection can be executed in parallel.**

## Compile parallel contract

FISCO BCOS provides **parallel contract development structure**. Developers only need to adhere to its regulation and define the exclusive parameter of each contract interface so as to realize parallelly executed contract. When contract is deployed, FISCO BCOS will auto-analyze exclusive object before the transaction is excuted to make non-dependent transaction execute in parallel as much as possible.

So far, FISCO BCOS offers two types of parallel contract development structure: [solidity](./smart_contract.html#id1) and [Precompiled contract](./smart_contract.html#id2).

### Solidity development structure

Parallel solidity contract shares the same development process with [regular solidity contract](./smart_contract.html#id1): make [``` ParallelContract ```](https://github.com/FISCO-BCOS/java-sdk-demo/blob/main/src/main/java/org/fisco/bcos/sdk/demo/contract/sol/ParallelContract.sol) as the base class of the parallel contract and call ``` registerParallelFunction() ``` to register the interface. （ParallelContract.sol can be found at [here](https://github.com/FISCO-BCOS/java-sdk-demo/blob/main/src/main/java/org/fisco/bcos/sdk/demo/contract/sol/ParallelContract.sol)）

Here is a complete example of how ParallelOk contract realize parallel payment transfer

```javascript
pragma solidity ^0.4.25;

import "./ParallelContract.sol";  // import ParallelContract.sol

contract ParallelOk is ParallelContract // make ParallelContract as the base class
{
    // contract realization
    mapping (string => uint256) _balance;

    function transfer(string from, string to, uint256 num) public
    {
        // here is a simple example, please use SafeMath instead of "+/-" in real production
        _balance[from] -= num;
        _balance[to] += num;
    }

    function set(string name, uint256 num) public
    {
        _balance[name] = num;
    }

    function balanceOf(string name) public view returns (uint256)
    {
        return _balance[name];
    }

    // register parallel contract interface
    function enableParallel() public
    {
        // function defined character string (no blank space behind ","), the former part of parameter constitutes exclusive parameter (which should be put ahead when designing function)
        registerParallelFunction("transfer(string,string,uint256)", 2); // critical: string string
        registerParallelFunction("set(string,uint256)", 1); // critical: string
    }

    // revoke parallel contract interface
    function disableParallel() public
    {
        unregisterParallelFunction("transfer(string,string,uint256)");
        unregisterParallelFunction("set(string,uint256)");
    }
}
```

The detail steps are:

**（1）make ``` ParallelContract ``` as the base class of contract**

```javascript
pragma solidity ^0.4.25;

import "./ParallelContract.sol"; // import ParallelContract.sol

contract ParallelOk is ParallelContract // make ParallelContract as the base class
{
   // contract realization

   // register parallel contract interface
   function enableParallel() public;

   // revoke parallel contract interface
   function disableParallel() public;
}
```

**（2）Compile parallel contract interface**

Public function in contract is the interface of contract. To compile a parallel contract interface is to realize the public function of a contract according to certain rules.

**Confirm whether the interface is parallelable**

A parallelable contract interface has to meet following conditions:

- no call of external contract

- no call of other function interface

**Confirm exclusive parameter**

Before compiling interface, please confirm the exclusive parameter of interface. The exclusion of interface is the exclusion of global variables. The confirmation of exclusive parameter has following rules:

- the interface accessed global mapping, the key of mapping is the exclusive parameter
- the interface accessed global arrays, the subscript of a array is the exclusive parameter
- the interface accessed simple type of global variables, all the simple type global variables share one exclusive parameter and use different variable names as the exclusive objects.

> For example: If `setA(int x)`writes `globalA`, we need to declare it as `setA(string aflag, int x)` and call it like `setA("globalA", 10)` by using `globalA` to declare the exclusive object.

**Confirm parameter type and sequence**

After the exclusive parameter is confirmed, confirm parameter type and sequence according to following rules:

- interface parameter is limited to: **string、address、uint256、int256** (more types coming in the future)

- exclusive parameter should all be contained in interface parameter

- all exclusive should be put in the beginning of the interface parameter

```javascript
mapping (string => uint256) _balance; // global mapping

// exclusive variable from, to are put at the beginning of transfer()
function transfer(string from, string to, uint256 num) public
{
    _balance[from] -= num;  // from is the key of global mapping, the exclusive parameter
    _balance[to] += num; // to is the key of global mapping, the exclusive parameter
}

// the exclusive variable name is put at the beginning of the parameter of set()
function set(string name, uint256 num) public
{
    _balance[name] = num;
}
```

**（3）Register parallelable contract interface**

Implement enableParallel() function in contract, call registerParallelFunction() to register parallelable contract interface, and implement disableParallel() function to endow the contract with ability to revoke parallel execution.

```javascript
// register parallelable contract interface
function enableParallel() public
{
    // function defined character string (no blank space behind ","), the parameter starts with exclusive parameters
    registerParallelFunction("transfer(string,string,uint256)", 2); // transfer interface, the former 2 is exclusive parameter
    registerParallelFunction("set(string,uint256)", 1); // set interface, the first 1 is exclusive parameter
}  

// revoke parallel contract interface
function disableParallel() public
{
    unregisterParallelFunction("transfer(string,string,uint256)");
    unregisterParallelFunction("set(string,uint256)");
}
```

**（4）Deploy/execute parallel contract**

Please refer to [here](./console_of_java_sdk.md) for the console manual for version 2.6 and above, and [here](./console.md) for the console manual for version 1.x. Here we use console as an example.

deploy contract

```shell
[group:1]> deploy ParallelOk.sol
```

call ``` enableParallel() ``` interface to make ParallelOk executed parallelly

```shell
[group:1]> call ParallelOk.sol 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 enableParallel
```

send parallel transaction ``` set() ```

```shell
[group:1]> call ParallelOk.sol 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 set "jimmyshi" 100000
```

send parallel transaction ``` transfer() ```

```shell
[group:1]> call ParallelOk.sol 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 transfer "jimmyshi" "jinny" 80000
```

check transaction execution result ``` balanceOf() ```

```shell
[group:1]> call ParallelOk.sol 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 balanceOf "jinny"
80000
```

The following context contains an example to send massive transaction through SDK.

### Precompile parallel contract structure

Parallel precompiled contract has the same compilation and development process with [regular precompiled contract](./smart_contract.html#id2). Regular precompiled contract uses Precompile as the base class to implement contract logical. Based on this, Precompile base class offers 2 virtual functions for parallel to enable implementation of parallel precompiled contract.

**（1）Define the contract as parallel contract**

```c++
bool isParallelPrecompiled() override { return true; }
```

**（2）Define parallel interface and exclusive parameter**

It needs attention that once contract is defined parallelable, all interfaces need to be defined. If an interface is returned with null, it has no exclusive object. Exclusive parameter is related to the implementation of precompiled contract, which needs understanding of FISCO BCOS storage. You can read the codes or consult experienced programmer for implementation details.

```c++
// take out exclusive object from parallel interface parameter, return exclusive object
std::vector<std::string> getParallelTag(bytesConstRef param) override
{
    // get the func and data to be called
    uint32_t func = getParamFunc(param);
    bytesConstRef data = getParamData(param);

    std::vector<std::string> results;
    if (func == name2Selector[DAG_TRANSFER_METHOD_TRS_STR2_UINT]) // function is parallel interface
    {  
        // interfaces：userTransfer(string,string,uint256)
        // take out exclusive object from data
        std::string fromUser, toUser;
        dev::u256 amount;
        abi.abiOut(data, fromUser, toUser, amount);

        if (!invalidUserName(fromUser) && !invalidUserName(toUser) && (amount > 0))
        {
            // write results to exclusive object
            results.push_back(fromUser);
            results.push_back(toUser);
        }
    }
    else if ... // all interfaces needs to offer exclusive object, returning null means no exclusive object

 	return results;  //return exclusion
}
```

**（3）Compile, restart node**

To manually compile nodes please check [here](../manual/get_executable.md)

After compilation, close node and replace with the original node binaries, and restart node.

## Example: parallel payment transfer

Here gives 2 parallel examples of solidity contract and precompiled contract.

**Config environment**

The execution environment in this case:

- java-sdk-demo client end
- a FISCO BCOS chain

java-sdk-demo is to send parallel transaction, FISCO BCOS chain is to execute parallel transaction. The related configuration are:

- [java-sdk-demo configuration](../sdk/java_sdk/configuration.md)
- [Chain building](../manual/build_chain.md)

For pressure test on maximum performance, it at least needs:

- 3 java-sdk-demo to generate enough transactions
- 4 nodes, all java-sdk-demo are configured with all information of nodes on chain to send transaction evenly to each node so that the chain can receive enough transaction

### Parallel Solidity contract: ParallelOk

Payment transfer based on account model is a typical operation. ParallelOk contract is an example of account model and is capable of parallel transfer. The ParallelOk contract is given in former context.

FISCO BCOS has built-in ParallelOk contract in java-sdk-demo. Here is the operation method to send massive parallel transactions through java-sdk-demo.

**（1）Deploy contract, create new user, activate parallel contract through SDK**

```shell
# java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [add] [count] [tps] [file]
java -cp conf/:lib/*:apps/* org.fisco.bcos.sdk.demo.perf.ParallelOkPerf precompiled 1 add 10000 2500 user
# 在group1上创建了 10000个用户，创建操作以2500TPS发送的，生成的用户信息保存在user中
```

After executed, ParallelOk contract will be deployed to blockchain, the created user information is stored in user file, and the parallel ability of ParallelOk contract is activated.

**（2）Send parallel transfer transactions in batch**

**Note: before send transactions in batch, please adjust the SDK log level to  ``ERROR`` to ensure capacity.**

```shell
# java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [transfer] [count] [tps] [file]
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf precompiled 1 transfer 100000 4000 user
# 100000 transactions have been sent to group1, the TPS limit is 4000, users are the same in the user file created formerly, 20% of exclusion exists between transactions.
```

**（3）Verify parallel correctness**

After parallel transaction is executed, java-sdk-demo will print execution result. ```TPS``` is the TPS executed on node in the transaction sent by SDK. ```validation``` is the verification of transfer transaction result.

```log
Total transactions:  100000
Total time: 34412ms
TPS: 2905.9630361501804
Avg time cost: 4027ms
Error rate: 0%
Return Error rate: 0%
Time area:
0    < time <  50ms   : 0  : 0.0%
50   < time <  100ms  : 44  : 0.044000000000000004%
100  < time <  200ms  : 2617  : 2.617%
200  < time <  400ms  : 6214  : 6.214%
400  < time <  1000ms : 14190  : 14.19%
1000 < time <  2000ms : 9224  : 9.224%
2000 < time           : 67711  : 67.711%
validation:
 	user count is 10000
 	verify_success count is 10000
 	verify_failed count is 0
```

We can see that the TPS of this transaction is 2905. No error (``` verify_failed count is 0 ```) after execution result is verified.

**（4）Count total TPS**

Single java-sdk-demo cannot send enough transactions to reach the parallel execution limit of nodes. It needs multiple java-sdk-demos to send transactions at the same time. TPS by simply summing together won't be correct enough when multiple java-sdk-demos sending transactions, so it should be acquired directly from node.

count TPS from log file using script

```shell
cd tools
sh get_tps.sh log/log_2019031821.00.log 21:26:24 21:26:59 # parameters：<log file> <count start time> <count end time>
```

get TPS（2 SDK, 4 nodes, 8 cores, 16G memory）

```shell
statistic_end = 21:26:58.631195
statistic_start = 21:26:24.051715
total transactions = 193332, execute_time = 34580ms, tps = 5590 (tx/s)
```

### Parallel precompiled contract: DagTransferPrecompiled

Same with the function of ParallelOk contract, FISCO BCOS has built-in example of parallel precompiled contract ([DagTransferPrecompiled](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/libprecompiled/extension/DagTransferPrecompiled.cpp)) and realizes transfer function based on account model. The contract can manage deposits from multiple users and provides a parallel transfer interface for parallel transactions of payment transfer between users.

**Note: DagTransferPrecompiled is the example of parallel transaction with simple functions, please don't use it for online transactions.**

**（1）Create user**

Use java-sdk-demo to send transaction to create user, the user information will be stored in user file. Command parameter is the same with parallelOk, the only difference is that the object called by the command is precompile.

```shell
# java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [add] [count] [tps] [file]
java -cp conf/:lib/*:apps/* org.fisco.bcos.sdk.demo.perf.ParallelOkPerf precompiled 1 add 10000 2500 user
# 在group1上创建了 10000个用户，创建操作以2500TPS发送的，生成的用户信息保存在user中
```

**（2）Send parallel transfer transactions in batch**

Send parallel transfer transactions through java-sdk-demo

**Note: before sending transactions in batch, please adjust SDK log level to ``ERROR`` for enough capability to send transactions.**

```shell
# java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [transfer] [count] [tps] [file]
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf precompiled 1 transfer 100000 4000 user

# 100000 transactions has been sent to group1, the TPS limit is 4000, users are the same ones in the user file created formerly, 20% exclusion exists between transactions.
```

**（3）Verify parallel correctness**

After parallel transactions are executed, java-sdk-demo will print execution result. ```TPS``` is the TPS of the transaction sent by SDK on the node. ```validation``` is the verification of transfer execution result.

```log
Total transactions:  80000
Total time: 25451ms
TPS: 3143.2949589407094
Avg time cost: 5203ms
Error rate: 0%
Return Error rate: 0%
Time area:
0    < time <  50ms   : 0  : 0.0%
50   < time <  100ms  : 0  : 0.0%
100  < time <  200ms  : 0  : 0.0%
200  < time <  400ms  : 0  : 0.0%
400  < time <  1000ms : 403  : 0.50375%
1000 < time <  2000ms : 5274  : 6.592499999999999%
2000 < time           : 74323  : 92.90375%
validation:
 	user count is 10000
 	verify_success count is 10000
 	verify_failed count is 0
```

We can see that in this transaction, the TPS is 3143. No error (``` verify_failed count is 0 ```) after execution result verification.

**（4）Count total TPS**

Single java-sdk-demo can send enough transactions to meet the parallel execution limit of node. It needs multiple java-sdk-demo to send tranactions. And by simply summing the TPS of each transaction won't be correct, so the TPS should be acquired from node directly.

Count TPS from log file using script

```shell
cd tools
sh get_tps.sh log/log_2019031311.17.log 11:25 11:30 # parameter：<log file> <count start time> <count end time>
```

get TPS (3 SDK, 4 nodes, 8 cores, 16G memory)

```shell
statistic_end = 11:29:59.587145
statistic_start = 11:25:00.642866
total transactions = 3340000, execute_time = 298945ms, tps = 11172 (tx/s)
```

### Result description

The performance result in the example of this chapter is tested in 3SDK, 4 nodes, 8 cores, 16G memory, 1G network. Each SDK and node are deployed in different VPS with cloud disk. The real TPS depends on the condition of your hardware configuration, operation system and bandwidth.
