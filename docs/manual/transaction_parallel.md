# 并行交易

某些业务场景需要极高的交易执行tps。让交易并行执行，能够最大限度的利用机器的CPU资源，实现：

- 高吞吐量：多笔交易同时被执行。
- 平行拓展：随着业务的发展，tps要求越来越高，可以通过提高机器的配置来提升交易执行的tps。

FISCO BCOS提供了并行交易的编程框架。按照并行框架编写的合约，能够被FISCO BCOS节点并行的执行。本文将指导完成如何编写FISCO BCOS并行合约，以及如何部署和执行。

## 预备知识

### 并行互斥

两笔交易是否能被并行的执行，依赖于这两笔交易是否存在**互斥**。互斥，是指两笔交易对合约存储变量的“读/写”操作，存在交集。

例如在转账场景中，交易是用户间的转账操作。用transfer(X, Y) 表示从X用户转到Y用户的转账接口。则互斥情况如下。

| 交易                             | 互斥对象         | 交集   | 是否互斥           |
| -------------------------------- | ---------------- | ------ | ------------------ |
| transfer(A, B) 和 transfer(A, C) | [A, B] 和 [A, C] | [A]    | 互斥，不可并行执行 |
| transfer(A, B) 和 transfer(B, C) | [A, B] 和 [B, C] | [B]    | 互斥，不可并行执行 |
| transfer(A, C) 和 transfer(B, C) | [A, C] 和 [B, C] | [C]    | 互斥，不可并行执行 |
| transfer(A, B) 和 transfer(A, B) | [A, B] 和 [A, B] | [A, B] | 互斥，不可并行执行 |
| transfer(A, B) 和 transfer(C, D) | [A, B] 和 [C, D] | 无     | 无互斥，可并行执行 |

此处给出更具体的定义：

- **互斥参数**：合约**接口**中，与合约存储变量的“读/写”操作相关的参数。例如转账的接口transfer(X, Y)，X和Y都是互斥参数。
- **互斥对象**：一笔**交易**中，根据互斥参数提取出来的，具体的互斥内容。例如转账的接口transfer(X, Y), 一笔调用此接口的交易中，具体的参数是transfer(A, B)，则对于这笔操作，互斥对象是[A, B]；另外一笔交易，调用的参数是transfer(A, C)，则这笔操作的互斥对象是[A, C]。

**判断交易间是否有互斥，就是判断互斥对象是否有交集。相互之间交集为空的交易可并行执行。**

### 并行合约框架

FISCO BCOS提供了智能合约的并行编程的框架，开发者只需按照框架进行合约开发，定义好每个合约接口的互斥参数，即可实现能被并行执行的合约。当合约被部署后，FISCO BCOS即可在执行交易前，自动提取互斥对象，让无互斥的交易并行执行。

目前，FISCO BCOS提供了solidity与预编译合约两种合约的并行框架。

#### solidity并行合约框架

用solidity语言写并行合约。开发者要做的是根据提供的框架，用solidity进行合约开发。按照框架的格式，将合约中的接口定义成并行的接口，之后对此接口的调用，将支持并行的处理。

基于并行solidity合约框架开发的合约，可维护，可升级，无需重启节点，比预编译合约更容易维护。

#### 预编译并行合约框架

[预编译合约](../design/virtual_machine/precompiled.md)是内置在FISCO BCOS节点中的合约，用C++语言实现。由于预编译合约的执行不依赖于EVM，因此预编译合约的性能比solidity合约的性能高很多（10倍左右）。因此，若需要追求极致的性能，可考虑用预编译合约实现合约逻辑。

FISCO BCOS提供了预编译合约并行框架，开发者只需要按照框架进行编程，即可实现并行的预编译合约。与普通的预编译合约相同，并行的预编译合约内置在节点代码中，必须重启所有节点，才能完成代码逻辑的升级。

#### 并行框架的选择

两种并行框架各有优劣，开发者根据具体场景选择合适的框架进行开发。

| 并行框架               | 开发语言 | 开发难度 | 可维护性                           | 性能 |
| ---------------------- | -------- | -------- | ---------------------------------- | ---- |
| solidity并行合约框架   | solidity | 低       | 高，无需重启节点即可部署           | 高   |
| 预编译并行合约框架 | C++      | 高       | 低，内置于节点中，升级时需重启节点 | 极高 |

## 编写并行合约

### solidity合约并行框架

编写并行的solidity合约，开发流程与开发[普通的solidity合约的流程相同](./smart_contract.html#id1)。在基础上，只需要将``` ParallelContract ``` 作为需要并行的合约的基类，并调用``` registerParallelFunction() ```，注册可以并行的接口即可。

先给出完整的举例，例子中的ParallelOk合约实现了并行转账的功能

```javascript
pragma solidity ^0.4.25;

import "./ParallelContract.sol";  // 引入ParallelContract.sol

// A parallel contract example
contract ParallelOk is ParallelContract // 将ParallelContract 作为基类
{
    // 合约实现
    mapping (string => uint256) _balance;
    
    function transfer(string from, string to, uint256 num) public
    {
        // Just an example, overflow is ok, use 'SafeMath' if needed
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
    
    // 注册可以并行的合约接口
    function enableParallel() public
    {
        // 函数定义字符串（注意","后不能有空格）,参数的前几个是互斥参数（设计函数时互斥参数必须放在前面
        registerParallelFunction("transfer(string,string,uint256)", 2); // critical: string string
        registerParallelFunction("set(string,uint256)", 1); // critical: string
    } 

    // 注销并行合约接口
    function disableParallel() public
    {
        unregisterParallelFunction("transfer(string,string,uint256)");
        unregisterParallelFunction("set(string,uint256)"); 
    } 
}
```

具体步骤如下：

**（1）将``` ParallelContract ```作为合约的基类**

```javascript
pragma solidity ^0.4.25;

import "./ParallelContract.sol"; // 引入ParallelContract.sol

// A parallel contract example
contract ParallelOk is ParallelContract // 将ParallelContract 作为基类
{
   // 合约实现
   
   // 注册可以并行的合约接口
   function enableParallel() public;
   
   // 注销并行合约接口
   function disableParallel() public;
}
```

**（2）注册/注销可以并行的合约接口**

在合约中实现 ``` enableParallel() ``` 函数，调用``` registerParallelFunction() ```注册可以并行的合约的接口。同时把``` disableParallel() ```也实现了，使合约具备取消并行执行的能力。

**注意：在写可以并行的合约接口时，必须让接口满足：**

- 接口的参数完全标识了此接口的所有互斥
- 不能调用外部合约
- 接口参数仅限：**string、address、uint256、int256**（后续支持更多类型）

```javascript
// 注册可以并行的合约接口
function enableParallel() public
{
    // 函数定义字符串（注意","后不能有空格）,参数的前几个是互斥参数（设计函数时互斥参数必须放在前面）
    registerParallelFunction("transfer(string,string,uint256)", 2); // 互斥: string string
    registerParallelFunction("set(string,uint256)", 1); // 互斥: string
}  

// 注销并行合约接口
function disableParallel() public
{
    unregisterParallelFunction("transfer(string,string,uint256)");
    unregisterParallelFunction("set(string,uint256)"); 
} 
```

**（3）部署/执行并行合约**

用[控制台](console.md)或[Web3SDK](../sdk/sdk.md)编译和部署合约，此处以控制台为例。

部署合约

```shell
[group:1]> deploy ParallelOk.sol
0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
```

调用 ``` enableParallel() ```接口，让ParallelOk能并行执行

```shell
[group:1]> call ParallelOk.sol 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 enableParallel
```

发送并行交易 ``` set() ```

```shell
0x4833dd374db41211eb88e2b618e967f8bdce76711792c394d693f7a98e399b4f
```

发送并行交易 ``` transfer() ```

```shell 
[group:1]> call ParallelOk.sol 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 transfer "jimmyshi" "jinny" 80000
```

查看交易执行结果 ``` balanceOf() ```

```shell
[group:1]> call ParallelOk.sol 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 balanceOf "jinny"
80000
```

用SDK发送大量交易的例子，将在下文的举例中给出。

### 预编译并行合约框架

编写并行的预编译合约，开发流程与开发[普通预编译合约的流程](./smart_contract.html#id2)相同。普通的预编译合约以Precompile为基类，再其上实现合约逻辑。在此基础上，Precompile的基类还为并行提供了两个虚函数，继续实现这两个函数，即可实现并行的预编译合约。

**（1）将合约定义成支持并行**

```c++
bool isParallelPrecompiled() override { return true; }
```

**（2）定义并行接口和互斥参数**

```
注意，一旦定义成支持并行，所有的接口都需要进行定义。若返回空，表示此接口无任何互斥对象。
```

```c++
// 根据并行接口，从参数中取出互斥对象，返回互斥对象
std::vector<std::string> getParallelTag(bytesConstRef param) override
{
    // 获取被调用的函数名（func）和参数（data）
    uint32_t func = getParamFunc(param);
    bytesConstRef data = getParamData(param);

    std::vector<std::string> results;
    if (func == name2Selector[DAG_TRANSFER_METHOD_TRS_STR2_UINT]) // 函数是并行接口
    {  
        // 接口为：userTransfer(string,string,uint256)
        // 从data中取出互斥对象
        std::string fromUser, toUser;
        dev::u256 amount;
        abi.abiOut(data, fromUser, toUser, amount);
        
        if (!invalidUserName(fromUser) && !invalidUserName(toUser) && (amount > 0))
        {
            // 将互斥对象写到results中
            results.push_back(fromUser);
            results.push_back(toUser);
        }
    }
    else if ... // 所有的接口都需要给出互斥对象，返回空表示无任何互斥对象
        
 	return results;  //返回互斥
}
```

**（3）编译，重启节点**

手动编译节点的方法，参考：[这里](./get_executable.md)

编译之后，关闭节点，替换掉原来的节点二进制文件，再重启节点即可。

## 举例：并行转账

此处分别给出solidity合约和预编译合约的并行举例。

**配置环境**

该举例需要以下执行环境：

- Web3SDK客户端
- 一条FISCO BCOS链

Web3SDK用来发送并行交易，FISCO BCOS链用来执行并行交易。相关配置，可参考：

- [web3sdk的配置](../sdk/sdk.md)
- [搭链](./build_chain.md)

若需要压测最大的性能，至少需要：

- 3个Web3SDK，才能产生足够多的交易
- 4个节点，且所有Web3SDK都配置了链上所有的节点信息，让交易均匀的发送到每个节点上，才能让链能接收足够多的交易

### 并行Solidity合约：ParallelOk

基于账户模型的转账，是一种典型的业务操作。ParallelOk合约，是账户模型的一个举例，能实现并行的转账功能。ParallelOk合约的实现如下。

```javascript
pragma solidity ^0.4.25;

import "./ParallelContract.sol";

// A parallel contract example
contract ParallelOk is ParallelContract
{
    mapping (string => uint256) _balance;
    
    function transfer(string from, string to, uint256 num) public
    {
        // Just an example, overflow is ok, use 'SafeMath' if needed
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
    
    // Register parallel function
    function enableParallel() public
    {
        // critical number is to define how many critical params from start
        registerParallelFunction("transfer(string,string,uint256)", 2); // critical: string string
        registerParallelFunction("set(string,uint256)", 1); // critical: string
    } 

    // Un-Register parallel function
    function disableParallel() public
    {
        unregisterParallelFunction("transfer(string,string,uint256)"); 
        unregisterParallelFunction("set(string,uint256)");
    } 
}
```

FISCO BCOS在Web3SDK中内置了ParallelOk合约，此处给出用Web3SDK来发送大量并行交易的操作方法。

**（1）用SDK部署合约、新建用户、开启合约的并行能力**

```shell
# 参数：<groupID> add <创建的用户数量> <此创建操作请求的TPS> <生成的用户信息文件名>
java -cp conf/:lib/*:apps/* org.fisco.bcos.channel.test.parallel.parallelok.PerformanceDT 1 add 10000 2500 user
# 在group1上创建了 10000个用户，创建操作以2500TPS发送的，生成的用户信息保存在user中
```

执行成功后，ParallelOk被部署到区块链上，创建的用户信息保存在user文件中，同时开启了ParallelOk的并行能力。

**（2）批量发送并行转账交易**

**注意：在批量发送前，请将SDK的日志等级请调整为``ERROR``，才能有足够的发送能力。**

```shell
# 参数：<groupID> transfer <总交易数量> <此转账操作请求的TPS上限> <需要的用户信息文件> <交易互斥百分比：0~10>
java -cp conf/:lib/*:apps/* org.fisco.bcos.channel.test.parallel.parallelok.PerformanceDT 1 transfer 100000 4000 user 2

# 向group1发送了 100000比交易，发送的TPS上限是4000，用的之前创建的user文件里的用户，发送的交易间有20%的互斥。
```

**（3）验证并行正确性**

并行交易执行完成后，Web3SDK会打印出执行结果。```TPS``` 是此SDK发送的交易在节点上执行的TPS。```validation``` 是转账交易执行结果的检查。

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

从图中可看出，本次交易执行的TPS是2905。执行结果校验后，无任何错误(``` verify_failed count is 0 ```)。

**（4）计算总TPS**

单个Web3SDK无法发送足够多的交易以达到节点并行执行能力的上限。需要多个Web3SDK同时发送交易。在多个Web3SDK同时发送交易后，单纯的将结果中的TPS加和得到的TPS不够准确，需要直接从节点处获取TPS。

用脚本从日志文件中计算TPS

```shell
cd tools
sh get_tps.sh log/log_2019031821.00.log 21:26:24 21:26:59 # 参数：<日志文件> <计算开始时间> <计算结束时间>
```

得到TPS（2 SDK、4节点，8核，16G内存）

```shell
statistic_end = 21:26:58.631195
statistic_start = 21:26:24.051715
total transactions = 193332, execute_time = 34580ms, tps = 5590 (tx/s)
```

### 并行预编译合约：DagTransferPrecompiled

与ParallelOk合约的功能一样，FISCO BCOS内置了一个并行预编译合约的例子（[DagTransferPrecompiled](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/libprecompiled/extension/DagTransferPrecompiled.cpp)），实现了简单的基于账户模型的转账功能。该合约能够管理多个用户的存款，并提供一个支持并行的transfer接口，实现对用户间转账操作的并行处理。

**注意：DagTransferPrecompiled为并行交易的举例，功能较为简单，请勿用于线上业务。**

**（1）生成用户**

用Web3SDK发送创建用户的操作，创建的用户信息保存在user文件中

```shell
# 参数：<groupID> add <创建的用户数量> <此创建操作请求的TPS> <生成的用户信息文件名>
java -cp conf/:lib/*:apps/* org.fisco.bcos.channel.test.parallel.precompile.PerformanceDT 1 add 10000 2500 user
# 在group1上创建了 10000个用户，创建操作以2500TPS发送的，生成的用户信息保存在user中
```

**（2）批量发送并行转账交易**

用Web3SDK发送并行转账交易

**注意：在批量发送前，请将SDK的日志等级请调整为``ERROR``，才能有足够的发送能力。**

```shell
# 参数：<groupID> transfer <总交易数量> <此转账操作请求的TPS上限> <需要的用户信息文件> <交易互斥百分比：0~10>
java -cp conf/:lib/*:apps/* org.fisco.bcos.channel.test.parallel.precompile.PerformanceDT 1 transfer 100000 4000 user 2
# 向group1发送了 100000比交易，发送的TPS上限是4000，用的之前创建的user文件里的用户，发送的交易间有20%的互斥。
```

**（3）验证并行正确性**

并行交易执行完成后，Web3SDK会打印出执行结果。```TPS``` 是此SDK发送的交易在节点上执行的TPS。```validation``` 是转账交易执行结果的检查。

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

从图中可看出，本次交易执行的TPS是3143。执行结果校验后，无任何错误(``` verify_failed count is 0 ```)。

**（4）计算总TPS**

单个Web3SDK无法发送足够多的交易以达到节点并行执行能力的上限。需要多个Web3SDK同时发送交易。在多个Web3SDK同时发送交易后，单纯的将结果中的TPS加和得到的TPS不够准确，需要直接从节点处获取TPS。

用脚本从日志文件中计算TPS

```shell
cd tools
sh get_tps.sh log/log_2019031311.17.log 11:25 11:30 # 参数：<日志文件> <计算开始时间> <计算结束时间>
```

得到TPS（3 SDK、4节点，8核，16G内存）

```shell
statistic_end = 11:29:59.587145
statistic_start = 11:25:00.642866
total transactions = 3340000, execute_time = 298945ms, tps = 11172 (tx/s)
```
