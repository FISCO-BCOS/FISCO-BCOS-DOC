# 交易并行

某些业务场景需要极高的交易执行tps。

并行执行的交易，能够最大限度的利用机器的CPU资源，实现：

* 高吞吐量：多笔交易同时被执行。
* 平行拓展：随着业务的发展，tps要求越来越高，可以通过提高机器的配置来提升交易执行的tps。

FISCO BCOS提供了并行交易的编程框架。按照并行框架编写的合约，能够被FISCO BCOS节点并行的执行。本文将指导完成FSICO BCOS并行合约的编写和执行。

## 基础

### 并行冲突

两笔交易是否能被并行的执行，依赖于这两笔交易是否存在**冲突**。冲突，是指两笔交易对合约存储变量的“读/写”操作，存在交集。

例如在转账场景中，交易是用户间的转账操作。用transfer(X, Y) 表示从X用户转到Y用户的转账接口。则冲突情况如下。

| 交易                             | 冲突对象         | 交集   | 是否冲突           |
| -------------------------------- | ---------------- | ------ | ------------------ |
| transfer(A, B) 和 transfer(A, C) | [A, B] 和 [A, C] | [A]    | 冲突，不可并行执行 |
| transfer(A, B) 和 transfer(B, C) | [A, B] 和 [B, C] | [B]    | 冲突，不可并行执行 |
| transfer(A, C) 和 transfer(B, C) | [A, C] 和 [B, C] | [C]    | 冲突，不可并行执行 |
| transfer(A, B) 和 transfer(A, B) | [A, B] 和 [A, B] | [A, B] | 冲突，不可并行执行 |
| transfer(A, B) 和 transfer(C, D) | [A, B] 和 [C, D] | 无     | 无冲突，可并行执行 |

此处给出更具体的定义：

- **冲突参数**：合约**接口**中，与合约存储变量的“读/写”操作相关的参数。例如转账的接口transfer(X, Y)，X和Y都是冲突参数。

- **冲突对象**：一笔**交易**中，根据冲突参数提取出来的，具体的冲突内容。例如转账的接口transfer(X, Y), 一笔调用此接口的交易中，具体的参数是transfer(A, B)，则对于这笔操作，冲突对象是[A, B]；另外一笔交易，调用的参数是transfer(A, C)，则这笔操作的冲突对象是[A, C]。

**判断交易间是否有冲突，就是判断冲突对象是否有交集。相互之间交集为空的交易可并行执行。**

### 并行合约框架

FISCO BCOS提供了智能合约的并行编程的框架，开发者只需按照框架进行合约开发，定义好每个合约接口的冲突参数，即可实现能被并行执行的合约。当合约被部署后，FISCO BCOS即可在执行交易前，自动提取冲突对象，让无冲突的交易并行执行。

目前，FISCO BCOS提供了solidity与precompile两种合约的并行框架。

#### solidity合约并行框架

用solidity语言写并行合约。开发者要做的是根据提供的框架，用solidity进行合约开发。按照框架的方式，将合约中的接口定义成并行的接口，之后对此接口的调用，将支持并行的处理。

基于并行solidity合约框架开发的合约，可维护，可升级，无需重启节点，比precompile合约更容易维护。

#### precompile合约并行框架

[precompile合约](../design/virtual_machine/precompile.md)是内置在FISCO BCOS节点中的合约，用C++语言实现。由于precompile合约的执行不依赖于EVM，因此precompile合约的性能比solidity合约的性能高很多（10倍左右）。因此，若需要追求极致的性能，可考虑用precompile实现合约逻辑。

FISCO BCOS提供了precompile合约并行框架，开发者只需要按照框架进行编程，即可实现并行的precompile合约。与普通的precompile合约相同，并行的precompile合约内置在节点代码中，必须重启所有节点，才能完成代码逻辑的升级。

#### 并行框架的选择

两种并行框架各有优劣，开发者根据具体场景选择合适的框架进行开发。

| 并行框架               | 开发语言 | 开发难度 | 可维护性                           | 性能 |
| ---------------------- | -------- | -------- | ---------------------------------- | ---- |
| solidity合约并行s框架  | solidity | 低       | 高，无需重启节点即可部署           | 高   |
| precompile合约并行框架 | C++      | 高       | 低，内置于节点中，升级时需重启节点 | 极高 |

## 编写并行合约

### solidity合约并行框架

（开发中）

### precompile合约并行框架

编写并行的precompile合约，开发流程与开发[普通的precompile合约的流程](./smart_contract.html#id2)相同。普通的precompile合约以Precompile为基类，再其上实现合约逻辑。在此基础上，Precompile的基类还为并行提供了两个虚函数，继续实现这两个函数，即可实现并行的precompile合约。

（1）将合约定义成支持并行

``` c++
bool isParallelPrecompiled() override { return true; }
```

（2）定义并行接口和冲突参数

​	注意，一旦定义成支持并行，所有的接口都需要进行定义。若返回空，表示此接口无任何冲突对象。

``` c++
// 根据并行接口，从参数中取出冲突对象，返回冲突对象
std::vector<std::string> getParallelTag(bytesConstRef param) override
{
    // 获取被调用的函数名（func）和参数（data）
    uint32_t func = getParamFunc(param);
    bytesConstRef data = getParamData(param);

    std::vector<std::string> results;
    if (func == name2Selector[DAG_TRANSFER_METHOD_TRS_STR2_UINT]) // 函数是并行接口
    {  
        // 接口为：userTransfer(string,string,uint256)
        // 从data中取出冲突对象
        std::string fromUser, toUser;
        dev::u256 amount;
        abi.abiOut(data, fromUser, toUser, amount);
        
        if (!invalidUserName(fromUser) && !invalidUserName(toUser) && (amount > 0))
        {
            // 将冲突对象写到results中
            results.push_back(fromUser);
            results.push_back(toUser);
        }
    }
    else if ... // 所有的接口都需要给出冲突对象，返回空表示无任何冲突对象
        
 	return results;  //返回冲突
}
```

## 举例：并行转账

基于账户模型的转账，是一种典型的业务操作。FISCO BCOS内置了一个并行precompile合约的例子（[DagTransferPrecompiled](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/feature-parallel/libprecompiled/DagTransferPrecompiled.cpp)），实现了简单的基于账户模型的转账功能。该合约能够管理多个用户的存款，并提供一个支持并行的transfer接口，实现对用户间转账操作的并行处理。

**注意：DagTransferPrecompiled为并行交易的举例，功能较为简单，请勿用于线上业务。**

### 配置环境

该举例需要以下执行环境：

* Web3SDK客户端
* 一条FISCO BCOS链

Web3SDK用来发送并行交易，FISCO BCOS链用来执行并行交易。相关配置，可参考：

* [web3sdk的配置](../sdk/sdk.md)
* [搭链](./build_chain.md)

若需要压测最大的性能，至少需要：

* 3个Web3SDK，才能产生足够多的交易
* 4个节点，且所有Web3SDK都配置了链上所有的节点信息，让交易均匀的发送到每个节点上，才能让链能接收足够多的交易

### 生成用户

用Web3SDK发送创建用户的操作，创建的用户信息保存在user文件中

``` shell
# 参数： add <创建的用户数量> <此创建操作请求的TPS> <生成的用户信息文件名>
java -cp conf/:lib/*:apps/* org.fisco.bcos.channel.test.dag.PerfomanceDT add 10000 2500 user
# 创建了 10000个用户，创建操作以2500TPS发送的，生成的用户信息保存在user1中
```

### 发送并行转账交易

用Web3SDK发送并行转账交易

``` shell
# 参数： transfer <总交易数量> <此转账操作请求的TPS上限> <需要的用户信息文件> <交易冲突百分比：0~10>
java -cp conf/:lib/*:apps/* org.fisco.bcos.channel.test.dag.PerfomanceDT transfer 80000 4000 user 2
# 发送了 80000比交易，发送的TPS上限是4000，用的之前创建的user文件里的用户，发送的交易间有20%的冲突。
```

### 验证并行正确性

并行交易执行完成后，Web3SDK会打印出执行结果。```TPS``` 是此SDK发送的交易在节点上执行的TPS。```validation``` 是转账交易执行结果的检查。

``` log
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

### 计算总TPS

单个Web3SDK无法发送足够多的交易以达到节点并行执行能力的上限。需要多个Web3SDK同时发送交易。在多个Web3SDK同时发送交易后，单纯的将结果中的TPS加和得到的TPS不够准确，需要直接从节点处获取TPS。

用脚本从日志文件中计算TPS

``` shell
sh get_tps.sh log/log_2019031311.17.log 11:25 11:30 # 参数：<日志文件> <计算开始时间> <计算结束时间>
```

得到TPS

``` shell
statistic_end = 11:29:59.587145
statistic_start = 11:25:00.642866
total transactions = 3340000, execute_time = 298945ms, tps = 11172 (tx/s)
```





