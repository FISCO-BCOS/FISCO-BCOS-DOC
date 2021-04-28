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
| 架构模型 | 一体两翼多引擎|
| 群组架构 | 支持链内动态扩展多群组|
| 分布式存储 | 支持海量数据存储|
| 并行计算 | 支持块内交易并行执行 |
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
| 共识算法 | PBFT、Raft、rPBFT|
| <font color=Blue>**存储引擎**</font>  |
| 存储设计 | 支持KV和SQL |
| 引擎类型 | 支持leveldb、rocksdb、mysql|
| CRUD接口 | 提供CRUD接口访问链上数据 |
| <font color=Blue>**网络协议**</font>  |
| 节点间通信 | P2P协议 |
| 客户端与节点通信 | JsonRPC，Channel协议 |
| 消息订阅服务 | AMOP协议 |
| <font color=Blue>**智能合约**|
|合约引擎| 支持Solidity和预编译合约 |
|引擎特点| 图灵完备，沙盒运行 |
|版本控制| 基于CNS支持多版本合约 |
| 灰度升级 | 支持多版本合约共存、灰度升级|
| 生命周期管理 |支持合约和账户的冻结、解冻|
| <font color=Blue>**密码算法和协议**</font>  |
| 国密算法 | 支持 |
| 国密SSL | 支持 |
| 哈希算法 | Keccak256、SM3 |
| 对称加密算法 | AES、SM4 |
| 非对称加密算法 |ECDSA、SM2|
| 非对称加密椭圆曲线|secp256k1、sm2p256v1|
| <font color=Blue>**安全控制**</font>  |
|存储安全| 支持落盘数据加密存储 |
|通信安全| 支持全流程SSL |
|准入安全| 基于PKI身份认证体系 |
|证书管理| 支持证书颁发、撤销、更新|
|权限控制| 支持细粒度权限控制|
| <font color=Blue>**隐私保护**</font> |
|物理隔离| 群组间数据隔离 |
|隐私保护协议| 支持群签名、环签名、同态加密 |
|场景化隐私保护机制|基于[WeDPR](https://fintech.webank.com/wedpr)支持隐匿支付、匿名投票、匿名竞拍、选择性披露等场景|
| <font color=Blue>**跨链协议**</font> |
|SPV|提供获取SPV证明的接口|
|跨链协议|基于[WeCross](https://github.com/WeBankBlockchain/WeCross)支持同构、异构跨链|
| <font color=Blue>**开发支持**</font> |
|合约开发工具|[WeBASE-IDE](https://github.com/WeBankFinTech/WeBASE)，[ChainIDE](https://fiscoide.com/)|
|开发建链工具|提供[一键搭链脚本工具](manual/build_chain.html)|
|合约部署与测试工具|交互式控制台 [基于Java SDK的控制台](console/console_of_java_sdk.html)|
|SDK语言|[Java](sdk/java_sdk/index.html)、[nodejs](sdk/nodejs_sdk/index.html)、[go](https://github.com/FISCO-BCOS/go-sdk)、[python](sdk/python_sdk/index.html)|
|快速开发组件|提供[Spring-boot-starter](https://github.com/FISCO-BCOS/spring-boot-starter)|
|压测工具|SDK内嵌压测工具，支持Caliper|
| <font color=Blue>**运维支持**</font> |
|运维建链工具|提供[企业级联盟链部署工具](enterprise_tools/index.html)|
|可视化数据展现|[区块链浏览器](browser/browser.html)|
|可视化节点管理|基于[WeBASE](https://github.com/WeBankFinTech/WeBASE)，提供节点管理器|
|动态管理节点|支持动态新增、剔除、变更节点|
|动态更改配置|支持动态变更系统配置|
|数据备份与恢复|提供数据导出与恢复服务组件|
|监控统计|输出统计日志，提供监控工具|
|监管审计|基于[WeBASE](https://github.com/WeBankFinTech/WeBASE)，提供监管审计入口|


## 架构

FISCO BCOS 在2.0中，创新性提出“一体两翼多引擎”架构，实现系统吞吐能力的横向扩展，大幅提升性能，在安全性、可运维性、易用性、可扩展性上，均具备行业领先优势。

![](../images/architecture/plane.jpg)

一体指代群组架构，支持快速组建联盟和建链，让企业建链像建聊天群一样便利。根据业务场景和业务关系，企业可选择不同群组，形成多个不同账本的数据共享和共识，从而快速丰富业务场景、扩大业务规模，且大幅简化链的部署和运维成本。

两翼指的是支持并行计算模型和分布式存储，二者为群组架构带来更好的扩展性。前者改变了区块中按交易顺序串行执行的做法，基于DAG（有向无环图）并行执行交易，大幅提升性能；后者支持企业（节点）将数据存储在远端分布式系统中，克服了本地化数据存储的诸多限制。

多引擎是一系列功能特性的总括，比如预编译合约能够突破EVM的性能瓶颈，实现高性能合约；控制台可以让用户快速掌握区块链使用技巧等。

上述功能特性均聚焦解决技术和体验的痛点，为开发、运维、治理和监管提供更多的工具支持，让系统处理更快、容量更高，使应用运行环境更安全、更稳定。


## 核心模块

FISCO BCOS采用高通量可扩展的[多群组架构](design/architecture/group.md)，可以动态管理多链、多群组，满足多业务场景的扩展需求和隔离需求，核心模块包括：

- <font color=blue>**[共识机制](design/consensus/index.md)**</font>：可插拔的共识机制，支持PBFT、Raft和rPBFT共识算法，交易确认时延低、吞吐量高，并具有最终一致性。其中PBFT和rPBFT可解决拜占庭问题，安全性更高。

- <font color=blue>**[存储](design/storage/index.md)**</font>：世界状态的存储从原来的MPT存储结构转为[分布式存储](design/storage/storage.html#id6)，避免了世界状态急剧膨胀导致性能下降的问题；引入可插拔的存储引擎，支持LevelDB、RocksDB、MySQL等多种后端存储，支持数据简便快速扩容的同时，将计算与数据隔离，降低了节点故障对节点数据的影响。

- <font color=blue>**[网络](design/p2p/p2p.md)**</font>：支持网络压缩功能，并基于负载均衡的思想实现了良好的分布式网络分发机制，最大化降低带宽开销。


## 性能

为提升系统性能，FISCO BCOS从提升交易执行效率和并发两个方面优化了交易执行，使得交易处理性能达到万级以上。

- <font color=blue>**[基于C++的Precompiled合约](design/virtual_machine/precompiled.md)**</font>：区块链底层内置C++语言编写的Precompiled合约，执行效率更高。
- <font color=blue>**[交易并行执行](design/parallel/dag.md)**</font>：基于DAG算法根据交易间互斥关系构建区块内交易执行流，最大化并行执行区块内的交易。
- 交易生命周期的异步并行处理：共识、同步、落盘等各个环节的异步化以及并行处理。

## 安全性

考虑到联盟链的高安全性需求，除了节点之间、节点与客户端之间通信采用TLS安全协议外，FISCO BCOS还实现了一整套安全解决方案：

- <font color=blue>**[网络准入机制](design/security_control/node_management.md)**</font>：限制节点加入、退出联盟链，可将指定群组的作恶节点从群组中删除，保障了系统安全性。

- <font color=blue>**[黑白名单机制](design/security_control/certificate_list.md)**</font>：每个群组仅可接收相应群组的消息，保证群组间网络通信的隔离性；CA黑名单机制可及时与作恶节点断开网络连接，保障了系统安全。

- <font color=blue>**[权限管理机制](design/security_control/permission_control.md)**</font>：基于分布式存储权限控制机制，灵活、细粒度地控制外部账户部署合约和创建、插入、删除和更新用户表的权限。

- <font color=blue>**[支持国密算法](manual/guomi_crypto.md)**</font>：支持国密加密、签名算法和国密通信协议。

- <font color=blue>**[落盘加密方案](design/features/storage_security.md)**</font>：支持加密节点落盘数据，保障链上数据的机密性。

- <font color=blue>**[密钥管理方案](design/features/storage_security.md)**</font>：在落盘加密方案的基础上，采用KeyManager服务管理节点密钥，安全性更强。

- <font color=blue>**[同态加密](manual/privacy.html#id2)、[群环签名](manual/privacy.html#id7)**</font>：链上提供了同态加密、群环签名接口，用于满足更多的业务需求。


## 可运维性

联盟链系统中，区块链的运维至关重要，FISCO BCOS提供了一整套运维部署工具，并引入了**合约命名服务**、**数据归档和迁移**、**合约生命周期管理**等工具来提升运维效率。


- <font color=blue>**[运维部署工具](enterprise_tools/index.md)**</font>: 部署、管理和监控多机构多群组联盟链的便捷工具，支持扩容节点、扩容新群组等多种操作。

- <font color=blue>**[合约命名服务](design/features/cns_contract_name_service.md)**</font>: 建立合约地址到合约名和合约版本的映射关系，方便调用者通过记忆简单的合约名来实现对链上合约的调用。

- 数据归档、迁移和导出功能: 提供数据导出组件，支持链上数据归档、迁移和导出，增加了链上数据的可维护性，降低了运维复杂度。

- <font color=blue>**[合约生命周期管理](design/features/contract_management.md)**</font>: 链上提供合约生命周期管理功能，便于链管理员对链上合约进行管理。


## 易用性

FISCO BCOS引入开发部署工具、交互式控制台、区块链浏览器等工具来提升系统的易用性，大幅缩短建链、部署应用的时间。

- <font color=blue>**[开发部署工具](manual/build_chain.md)**</font>
- <font color=blue>**[基于Java SDK的交互式的命令行工具console](console/console_of_java_sdk.md))**</font>
- <font color=blue>**[区块链浏览器](browser/browser.md)**</font>

为了便于不同语言开发者快速开发应用，FISCO BCOS同时支持[Java SDK](sdk/java_sdk/index.html)、[Node.js SDK](sdk/nodejs_sdk/index.md)、[Python SDK](sdk/python_sdk/index.md)和[Go SDK](https://github.com/FISCO-BCOS/go-sdk)


## 社区开发工具

依托庞大的开源生态，社区内众伙伴秉承“来自开发者，用于开发者”的共建理念，在FISCO BCOS底层平台之上，自主研发多个趁手开发工具并回馈给社区，从不同业务层面需求上降低区块链应用开发难度和成本。以下作部分列举，欢迎更多机构或开发者向社区反馈更多好用的工具。

- <font color=blue>**[区块链中间件平台WeBASE](https://github.com/WeBankFinTech/WeBASE)**</font>：面向多种对象，如开发者、运营者，并根据不同的场景，包括开发、调试、部署、审计等，打造丰富的功能组件和实用工具，提供友好的、可视化的操作环境。

- <font color=blue>**[分布式身份解决方案WeIdentity](https://github.com/webankfintech/weidentity)**</font>：基于区块链的分布式多中心的技术解决方案，提供分布式实体身份标识及管理、可信数据交换协议等一系列的基础层与应用接口，可实现实体对象（人或物）数据的安全授权与交换。

- <font color=blue>**[分布式事件驱动架构WeEvent](https://github.com/webankfintech/weevent)**</font>：实现了可信、可靠、高效的跨机构、跨平台事件通知机制。在不改变已有商业系统的开发语言、接入协议的情况下，实现跨机构、跨平台的事件通知与处理。

- <font color=blue>**[跨链协作方案WeCross](https://github.com/WeBankFinTech/WeCross)**</font>：支持跨链事务交易，满足跨链交易的原子性，对跨链进行治理，可支持多方协作管理，避免单点风险。

- <font color=blue>**[场景式隐私保护解决方案WeDPR](https://fintech.webank.com/wedpr)**</font>：针对隐匿支付、匿名投票、匿名竞拍和选择性披露等应用方案，提供即时可用场景式隐私保护高效解决方案，助力各行业合法合规地探索数据相关业务。
  
- <font color=blue>**[区块链数据治理组件解决方案](https://data-doc.readthedocs.io/zh_CN/latest/)**</font>：稳定、高效、安全的区块链数据治理组件解决方案，分别从底层数据存储层、智能合约数据解析层和应用层三个方面，提供了区块链数据挖掘、裁剪、扩容、可信存储、抽取、分析、审计、对账、监管等数据治理方面的关键能力。

- <font color=blue>**[区块链多方协作治理组件解决方案](https://governance-doc.readthedocs.io/zh_CN/latest/)**</font>：可无缝适配FISCO BCOS的区块链治理组件解决方案。首批开源的四个组件分别从私钥丢失重置、合约权限细粒度管控、私钥和证书的全生命周期管控等方面着手，提供了可部署的智能合约代码、易于使用的SDK和可参考的落地实践Demo等交付物。
  
- <font color=blue>**[区块链应用开发组件解决方案](https://smartdev-doc.readthedocs.io/zh_CN/latest/)**</font>：一套开放、轻量的开发组件集，覆盖智能合约的开发、调试、应用开发等环节，包含了Solidity智能合约开发工具库、 智能合约Gradle编译插件、应用开发脚手架。

- <font color=blue>**[ChainIDE](https://fiscoide.com/)**</font>：提供智能合约云端开发工具，帮助开发者节约边际成本，加速推送区块链应用落地。

- <font color=blue>**[FISCO BCOS区块链工具箱](https://github.com/SUIBE-Blockchain/FISCO_BCOS_Toolbox/)**</font>：与WeBase/Remix/VSCode/ChainIDE等IDE协同工作，提升开发体验与开发效率。

- <font color=blue>**[Ansible for FISCO BCOS 自动化生成企业级部署文件](https://github.com/newtouch-cloud/ansible-for-fisco-bcos)**</font>：本项目由为区块链开源项目 FISCO-BCOS 提供了自动化生成企业级配置文件的 ansible playbook。2 群组 3 机构 6 节点的环境，可以在 30 秒内（除下载时间）生成配置，极大简化了部署难度，避免了手工配置容易发生的错误。
