# FISCO BCOS 区块链

标签：``FISCO BCOS介绍`` ``关键特性`` ``开发工具``

---

FISCO BCOS是由国内企业主导研发、对外开源、安全可控的企业级金融联盟链底层平台，由金链盟开源工作组协作打造，并于2017年正式对外开源。

社区以开源链接多方，截止2020年5月，汇聚了超1000家企业及机构、逾万名社区成员参与共建共治，发展成为最大最活跃的国产开源联盟链生态圈。底层平台可用性经广泛应用实践检验，数百个应用项目基于FISCO BCOS底层平台研发，超80个已在生产环境中稳定运行，覆盖文化版权、司法服务、政务服务、物联网、金融、智慧社区等领域。

```eval_rst
.. note::
    FISCO BCOS以联盟链的实际需求为出发点，兼顾性能、安全、可运维性、易用性、可扩展性，支持多种SDK，并提供了可视化的中间件工具，大幅缩短建链、开发、部署应用的时间。此外，FISCO BCOS通过信通院可信区块链评测功能、性能两项评测，单链TPS可达两万。
```

## 关键特性

<font color=Blue>**整体架构**</font> | |
| - | - |
| 架构模型 | 灵活自适应的区块链框架，目前包括轻量级的Air版本FISCO BCOS和适用于复杂业务场景的Pro版本FISCO BCOS|
| 群组架构 | 支持链内动态扩展多群组|
| 分布式存储 | 支持海量数据存储|
| 并行计算 | 支持基于DAG(有向无环图)和DMC(确定性合约并行算法)块内交易并行执行 |
| 节点类型 | 共识节点、观察节点 |
| 计算模型 | 排序-执行-验证 |
| <font color=Blue>**系统性能**</font> |
| 峰值TPS | 2万+ TPS（PBFT）|
| 交易确认时延 | 秒级|
| <font color=Blue>**硬件推荐配置**</font> |
| CPU | 2.4GHz * 8核|
| 内存 | 8GB |
| 存储 | 4TB |
| 网络带宽| 10Mb |
| <font color=Blue>**账本模型**</font> |
| 数据结构 | 链式结构，区块通过哈希链相连|
| 是否分叉|不分叉|
| 记账类型 | 账户模型（非UTXO）|
| <font color=Blue>**共识算法**</font>  |
| 共识框架 | 可插拔设计 |
| 共识算法 | PBFT|
| <font color=Blue>**存储引擎**</font>  |
| 存储设计 | 支持KV和SQL |
| 引擎类型 | 支持rocksdb|
| CRUD接口 | 提供CRUD接口访问链上数据 |
| <font color=Blue>**网络协议**</font>  |
| 节点间通信 | P2P协议 |
| 客户端与节点通信 | WebSocket协议 |
| 消息订阅服务 | AMOP协议 |
| <font color=Blue>**智能合约**|
|合约引擎| 支持WASM和EVM|
|合约语言| 支持Solidity, C++和WBC-Liquid|
|引擎特点| 图灵完备，沙盒运行 |
|版本控制| 基于CNS支持多版本合约 |
| <font color=Blue>**密码算法和协议**</font>  |
| 国密算法 | 支持 |
| 国密SSL | 支持 |
| 哈希算法 | Keccak256、SM3 |
| 对称加密算法 | AES、SM4 |
| 非对称加密算法 |ECDSA、SM2|
| 非对称加密椭圆曲线|secp256k1、sm2p256v1|
| <font color=Blue>**安全控制**</font>  |
|通信安全| 支持全流程SSL |
|准入安全| 基于PKI身份认证体系 |
|证书管理| 支持证书颁发、撤销、更新|
|权限控制| 支持细粒度权限控制|
| <font color=Blue>**隐私保护**</font> |
|物理隔离| 群组间数据隔离 |
|场景化隐私保护机制|基于[WeDPR](https://fintech.webank.com/wedpr)支持隐匿支付、匿名投票、匿名竞拍、选择性披露等场景|
| <font color=Blue>**跨链协议**</font> |
|SPV|提供获取SPV证明的接口|
|跨链协议|基于[WeCross](https://github.com/WeBankBlockchain/WeCross)支持同构、异构跨链|
| <font color=Blue>**开发支持**</font> |
|开发建链工具|提供[Air版本区块链部署工具build_chain](./tutorial/air/build_chain.html)和[Pro版本区块链部署工具BcosProBuilder](./tutorial/pro/pro_builder.html)|
|合约部署与测试工具|交互式控制台 [基于Java SDK的控制台](./develop/console/index.html)|
|SDK语言|[Java](./develop/sdk/java_sdk/index.html)（待适配语言：go、nodejs、Rust、Python、iOS、Android）
|快速开发组件|提供[Spring-boot-starter](https://github.com/FISCO-BCOS/spring-boot-starter)|
| <font color=Blue>**运维支持**</font> |
|动态管理节点|支持动态新增、剔除、变更节点|
|动态更改配置|支持动态变更系统配置|
|数据备份与恢复|提供数据导出与恢复服务组件|
|监控统计|输出统计日志，提供监控工具|