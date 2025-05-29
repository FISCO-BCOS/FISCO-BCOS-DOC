# 5. EVM OPCODE 兼容性描述

该文档从OPCODE的角度描述FISCO BCOS与以太坊在执行行为上的差异：

1. 基础操作指令与以太坊相同
2. 国密版的密码计算OPCODE为国密算法
3. 联盟链设计
   * Balance相关OPCODE需用开关打开
   * POW、POS相关OPCODE默认返回0

FISCO BCOS 属于[Type 3](https://vitalik.eth.limo/general/2022/08/04/zkevm.html)兼容（兼容Type描述见附录）

## OPCODE 兼容性分类

* 无差异 
  * 默认支持：计算类、指令跳转类、内存操作类
  * 新版本支持：代码类，调用类
* 半差异（详细描述见下一节）
  * 功能类似：TIMESTAMP、GASLIMIT、SELFDESTRUCT
  * 国密版不同：KECCAK256、CREATE2
  * 通过开关开启：BALANCE、CALLVALUE、GASPRICE、SELFBALANCE
* 全差异
  * 默认返回0：COINBASE、PREVRANDAO、CHAINID、BASEFEE

## OPCODE 兼容性描述列表

（*表格中未给出的OPCODE为默认支持，无差异 -，半差异 ○，全差异 ●）

| Stack | Name         | 差异 | 描述                                                         | 支持版本           |
| :---: | :----------- | ---- | ------------------------------------------------------------ | ------------------ |
|  20   | KECCAK256    | ○    | 国密类型的为SM3，非国密的不变                                |                    |
|  31   | BALANCE      | ○    | 开启feature_balance后生效                                    | 3.6.0+             |
|  34   | CALLVALUE    | ○    | 开启feature_balance后生效                                    | 3.6.0+             |
|  38   | CODESIZE     | ○    | FISCO BCOS 的预编译合约返回 1<br>以太坊原有的预编译合约返回 0 | 3.1.0+             |
|  39   | CODECOPY     | -    |                                                              | 3.2.4+<br> 3.6.0+  |
|  3A   | GASPRICE     | ○    | 开启feature_balance_policy1后生效，在tx_gas_price系统配置项中配置 | 3.6.0+             |
|  3B   | EXTCODESIZE  | -    |                                                              | 3.1.0+             |
|  3C   | EXTCODECOPY  | -    |                                                              | 3.2.4+<br/> 3.6.0+ |
|  3F   | EXTCODEHASH  | -    |                                                              | 3.1.0+             |
|  40   | BLOCKHASH    | -    |                                                              | 3.1.0+             |
|  41   | COINBASE     | ●    | 返回0                                                        |                    |
|  42   | TIMESTAMP    | ○    | 返回区块时间戳，单位为“毫秒”<br>（以太坊中单位为“秒”）       |                    |
|  44   | PREVRANDAO   | ●    | 返回0                                                        |                    |
|  45   | GASLIMIT     | ○    | 通过系统合约配置，返回“单笔交易”的gas上限<br>（以太坊中是区块的gas上限） |                    |
|  46   | CHAINID      | ●    | 返回0                                                        |                    |
|  47   | SELFBALANCE  | ○    | 开启feature_balance后生效                                    | 3.6.0+             |
|  48   | BASEFEE      | ●    | 返回0                                                        |                    |
|  F2   | CALLCODE     | -    |                                                              | 3.1.0+             |
|  F4   | DELEGATECALL | -    |                                                              | 3.1.0+             |
|  F5   | CREATE2      | ○    | 国密的计算根据SM3算法计算地址                                | 3.2.4+<br/> 3.6.0+ |
|  FA   | STATICCALL   | -    | 历史版本与CALL相同，3.2.4和3.6.0开始支持                     | 3.2.4+<br/> 3.6.0+ |
|  FF   | SELFDESTRUCT | ○    | 3.1.0+支持合约销毁<br>3.6.0+开启featrue_balance后支持销毁合约并回收balance | 3.6.0+             |

## 附录：兼容等级划分

文章[《The different types of ZK-EVMs》](https://vitalik.eth.limo/general/2022/08/04/zkevm.html)中对以太坊的兼容性进行了等级划分

* Type 1：fully Ethereum-equivalent
  * 完全兼容以太坊，包括RPC接口、EVM和EVM外的执行环境
* Type 2：fully EVM-equivalent)
  * 兼容EVM，EVM外的执行环境相同
* Type 2.5：EVM-equivalent, except for gas costs
  * 兼容EVM，但gas不同
* Type 3：almost EVM-equivalent
  * 采用EVM，但EVM外的执行环境存在差异，有自定义功能
* Type 4：high-level-language equivalent
  * 采用其他智能合约执行引擎

![img](https://vitalik.eth.limo/images/zkevm/chart.png)

