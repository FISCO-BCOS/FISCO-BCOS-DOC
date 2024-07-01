# 5. EVM OPCODE Compatibility Description

This document describes the differences in execution behavior between FISCO BCOS and Ethereum from the perspective of OPCODE:

1. The basic operating instructions are the same as Ethereum.
2. The password calculation OPCODE of the state secret version is the state secret algorithm.
3. Alliance chain design
   * Balance related OPCODE needs to be opened with a switch
   * POW, POS related OPCODE default return 0

FISCO BCOS belongs to [Type 3](https://vitalik.eth.limo/general/2022/08/04/zkevm.html)Compatible (see Appendix for description of Compatible Type)

## OPCODE Compatibility Classification

* No difference
  * Default support: calculation class, instruction jump class, memory operation class
  * New version support: code class, call class
* Semi-differences (see next section for a detailed description)
  * Features similar to: TIMESTAMP, GASLIMIT, SELFDESTRUCT
  * State secret version is different: KECCAK256, CREATE2
  * Open via switch: BALANCE, CALLVALUE, GASPRICE, SELFBALANCE
* Total difference
  * Default returns 0: COINBASE, PREVRANDAO, CHAINID, BASEFEE

## OPCODE Compatibility Description List

（*OPCODE not given in the table is supported by default, no difference-, half difference ○, full difference ●)

| Stack | Name         | Differences| 描述| Supported Versions|
| :---: | :----------- | ---- | ------------------------------------------------------------ | ------------------ |
|  20   | KECCAK256    | ○    | The type of national secret is SM3, and the non-national secret is unchanged.|                    |
|  31   | BALANCE      | ○    | Effective after feature _ balance is enabled| 3.6.0+             |
|  34   | CALLVALUE    | ○    | Effective after feature _ balance is enabled| 3.6.0+             |
|  38   | CODESIZE     | ○    | FISCO BCOS's precompiled contract returns 1 < br > Ethereum's original precompiled contract returns 0| 3.1.0+             |
|  39   | CODECOPY     | -    |                                                              | 3.2.4+<br> 3.6.0+  |
|  3A   | GASPRICE     | ○    | It takes effect after feature _ balance _ policy1 is enabled, and is configured in the tx _ gas _ price system configuration item.| 3.6.0+             |
|  3B   | EXTCODESIZE  | -    |                                                              | 3.1.0+             |
|  3C   | EXTCODECOPY  | -    |                                                              | 3.2.4+<br/> 3.6.0+ |
|  3F   | EXTCODEHASH  | -    |                                                              | 3.1.0+             |
|  40   | BLOCKHASH    | -    |                                                              | 3.1.0+             |
|  41   | COINBASE     | ●    | Return to 0|                    |
|  42   | TIMESTAMP    | ○    | Returns the block timestamp in milliseconds < br > (in seconds in Ethereum).|                    |
|  44   | PREVRANDAO   | ●    | Return to 0|                    |
|  45   | GASLIMIT     | ○    | Returns the upper limit of gas for a single transaction through system contract configuration < br > (in Ethereum, the upper limit of gas for a block)|                    |
|  46   | CHAINID      | ●    | Return to 0|                    |
|  47   | SELFBALANCE  | ○    | Effective after feature _ balance is enabled| 3.6.0+             |
|  48   | BASEFEE      | ●    | Return to 0|                    |
|  F2   | CALLCODE     | -    |                                                              | 3.1.0+             |
|  F4   | DELEGATECALL | -    |                                                              | 3.1.0+             |
|  F5   | CREATE2      | ○    | The calculation of the state secret calculates the address according to the SM3 algorithm.| 3.2.4+<br/> 3.6.0+ |
|  FA   | STATICCALL   | -    | The historical version is the same as CALL, 3.2.4 and 3.6.0 start to support| 3.2.4+<br/> 3.6.0+ |
|  FF   | SELFDESTRUCT | ○    | 3.1.0+Support Contract Destruction < br > 3.6.0+Support to destroy contract and recycle balance after opening featrue _ balance| 3.6.0+             |

## Appendix: Compatibility Classification

Article [The different types of ZK-EVMs》](https://vitalik.eth.limo/general/2022/08/04/zkevm.html)Ethereum compatibility is ranked in

* Type 1：fully Ethereum-equivalent
  * Fully compatible with Ethereum, including RPC interfaces, EVM, and execution environments outside of EVM
* Type 2：fully EVM-equivalent)
  * Compatible with EVM, same execution environment outside EVM
* Type 2.5：EVM-equivalent, except for gas costs
  * EVM compatible, but gas is different
* Type 3：almost EVM-equivalent
  * EVM is used, but there are differences in the execution environment outside the EVM, with custom features
* Type 4：high-level-language equivalent
  * Adoption of other smart contract execution engines

![img](https://vitalik.eth.limo/images/zkevm/chart.png)

