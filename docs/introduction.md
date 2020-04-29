# 平台介绍

FISCO BCOS是首个由国内企业主导研发、对外开源、安全可控的企业级金融联盟链底层平台，由金链盟开源工作组协作打造，并于2017年正式对外开源。

社区以开源链接多方，目前，汇聚了超500家企业及机构、逾万名社区成员参与共建共治，发展成为最大最活跃的国产开源联盟链生态圈。底层平台经广泛应用实践，具有高可用、易上手的特点，数百个应用项目基于FISCO BCOS底层平台研发，超60个已在生产环境中稳定运行，覆盖文化版权、司法服务、政务服务、物联网、金融、智慧社区等领域。

```eval_rst
.. note::
    FISCO BCOS以联盟链的实际需求为出发点，兼顾性能、安全、可运维性、易用性、可扩展性，支持多种SDK，并提供了可视化的中间件工具，大幅缩短建链、开发、部署应用的时间。此外，FISCO BCOS通过信通院可信区块链评测功能、性能两项评测，单链TPS可达两万。
```

## 架构

FISCO BCOS 2.0采用一体两翼多引擎架构，实现系统吞吐能力的横向扩展，支持快速组建联盟和建链，让企业建链像建微信群一样便利，性能、安全性、可运维性、易用性、可扩展性均有很大优势。

![](../images/architecture/plane.jpg)


## 核心模块

FISCO BCOS采用高通量可扩展的[多群组架构](./design/architecture/group.md)，可以动态管理多链、多群组，满足满足多业务场景的扩展需求和隔离需求，核心模块包括：

- <font color=blue>**[共识机制](./design/consensus/index.md)**</font>：可插拔的共识机制，支持PBFT、Raft和RPBFT共识算法，交易确认时延低、吞吐量高，并具有最终一致性。其中PBFT和RPBFT可解决拜占庭问题，安全性更高

- <font color=blue>**[存储](./design/storage/index.md)**</font>：世界状态的存储从原来的MPT存储结构转为[分布式存储](./design/storage/storage.html#id6)，避免了世界状态急剧膨胀导致性能下降的问题；引入可插拔的存储引擎，支持LevelDB、RocksDB、MySQL等多种后端存储，支持数据简便快速地扩容的同时，将计算与数据隔离，降低了节点故障对节点数据的影响

- <font color=blue>**[网络](./design/p2p/p2p.md)**</font>：支持网络压缩功能，并基于负载均衡的思想实现了良好的分布式网络分发机制，最大化降低带宽开销。


## 性能

为提升系统性能，FISCO BCOS从提升交易执行效率和并发两个方面优化了交易执行，使得交易处理性能达到万级以上。

- <font color=blue>**[基于C++的Precompiled合约](./design/virtual_machine/precompiled.md)**</font>：区块链底层内置C++语言编写的Precompiled合约，执行效率更高。
- <font color=blue>**[交易并行执行](./design/parallel/dag.md)**</font>：基于DAG算法根据交易间互斥关系构建区块内交易执行流，最大化并行执行区块内的交易。


## 安全性

考虑到联盟链的高安全性需求，除了节点之间、节点与客户端之间通信采用TLS安全协议外，FISCO BCOS还实现了一整套安全解决方案：

- <font color=blue>**[网络准入机制](./design/security_control/node_management.md)**</font>：限制节点加入、退出联盟链，可将指定群组的作恶节点从群组中删除，保障了系统安全性

- <font color=blue>**[黑白名单机制](./design/security_control/certificate_list.md)**</font>：每个群组仅可接收相应群组的消息，保证群组间网络通信的隔离性；CA黑名单机制可及时与作恶节点断开网络连接，保障了系统安全

- <font color=blue>**[权限管理机制](./design/security_control/permission_control.md)**</font>：基于分布式存储权限控制机制，灵活、细粒度地控制外部账户部署合约和创建、插入、删除和更新用户表的权限

- <font color=blue>**[支持国密算法](./manual/guomi_crypto.md)**</font>：支持国密加密、签名算法和国密通信协议

- <font color=blue>**[落盘加密方案](./design/features/storage_security.md)**</font>：支持加密节点落盘数据，保障链上数据的机密性

- <font color=blue>**[密钥管理方案](./design/features/storage_security.md)**</font>：在落盘加密方案的基础上，采用KeyManager服务管理节点密钥，安全性更强

- <font color=blue>**[同态加密](./manual/privacy.html#id2)、[群环签名](./manual/privacy.html#id7)**</font>：链上提供了同态加密、群环签名接口，用于满足更多的业务需求


## 可运维性

联盟链系统中，区块链的运维至关重要，FISCO BCOS提供了一整套运维部署工具，并引入了**合约命名服务**、**数据归档和迁移**、**合约生命周期管理**等工具来提升运维效率。


- <font color=blue>**[运维部署工具](./enterprise_tools/index.md)**</font>: 部署、管理和监控多机构多群组联盟链的便捷工具，支持扩容节点、扩容新群组等多种操作

- <font color=blue>**[合约命名服务](./design/features/cns_contract_name_service.md)**</font>: 建立合约地址到合约名和合约版本的映射关系，方便调用者通过记忆简单的合约名来实现对链上合约的调用

- **数据归档、迁移和导出功能**: 提供数据导出组件，支持链上数据归档、迁移和导出，增加了链上数据的可维护性，降低了运维复杂度

- <font color=blue>**[合约生命周期管理](./design/features/contract_management.md)**</font>: 链上提供合约生命周期管理功能，便于链管理员对链上合约进行管理


## 易用性

FISO BCOS引入开发部署工具、交互式控制台、区块链浏览器等工具来提升系统的易用性，大幅缩短建链、部署应用的时间

- <font color=blue>**[开发部署工具](./manual/build_chain.md)**</font>
- <font color=blue>**[交互式的命令行工具console](./manual/console.md)**</font>
- <font color=blue>**[区块链浏览器](./browser/browser.md)**</font>
- <font color=blue>**[可视化的区块链管理平台WeBASE](https://webase-web.readthedocs.io/en/latest/)**</font>

为了便于不同语言开发者快速开发应用，FISCO BCOS同时支持[Java SDK](./sdk/java_sdk.md)、[Node.js SDK](./nodejs_sdk/index.md)、[Python SDK](./sdk/python_sdk/index.md)和[Go SDK](https://github.com/FISCO-BCOS/go-sdk)


## 可扩展性

为提升FISCO BCOS的可扩展性，FISCO BCOS团队提出跨链协作方案WeCross和隐私保护解决方案WeDPR。

- <font color=blue>**[跨链协作方案WeCross](https://fintech.webank.com/wecross/)**</font>：支持跨链事务交易，满足跨链交易的原子性，对跨链进行治理，可支持多方协作管理，避免单点风险。

- <font color=blue>**[场景化的隐私保护解决方案WeDPR](https://fintech.webank.com/wedpr/)**</font>：提供匿支付、匿名投票、匿名竞拍和选择性披露等方案
