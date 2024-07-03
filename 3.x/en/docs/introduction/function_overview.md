# 3. Functional overview
Tags: "Features Overview"

-----

In order to support the demand for massive services, FISCO BCOS v3.0 Stable Edition has designed the system architecture, processing flow, execution, and storage accordingly, and launched three different forms to meet the differentiated needs of different blockchain deployment scenarios。The functional overview is as follows:

|<font color=Blue>**Overall architecture**</font> | |
| - | - |
| architectural model| A flexible and adaptive blockchain framework that currently includes a lightweight Air version, a Pro version for complex business scenarios, and a scalable Max version|
| Group architecture| Support in-chain dynamic expansion of multiple groups|
| distributed storage| Support massive data storage|
| parallel computing| DAG-based support(directed acyclic graph)、DMC(parallel deterministic contract algorithm)and intra-block sharding techniques|
| Node Type| Consensus node, observation node, light node|
| calculation model| Sort-Execute-Validate|
| <font color=Blue>**System performance**</font> ||
| Peak TPS| 100,000+ TPS（PBFT）|
| Transaction confirmation delay| second level|
| <font color=Blue>**Recommended hardware configuration**</font> ||
| CPU | 2.4GHz * 8 Nuclear|
| Memory| 8GB |
| Storage| 4TB |
| network bandwidth| 10Mb |
| <font color=Blue>**ledger model**</font> ||
| Data Structure| Chain structure, blocks connected by hash chain|
| Whether to fork|not bifurcated|
| Bookkeeping Type| Account model (non-UTXO)|
| <font color=Blue>**consensus algorithm**</font>  ||
| Consensus framework| Pluggable design|
| consensus algorithm| PBFT |
| <font color=Blue>**Storage Engine**</font>  ||
| Storage Design| Support KV and SQL|
| Engine type| Support for rocksdb and TikvDB|
| CRUD interface| Provide CRUD interface to access data on the chain|
| <font color=Blue>**network protocol**</font>  ||
| inter-node communication| P2P Protocol|
| Client-node communication| WebSocket Protocol|
| Message Subscription Service| AMOP Protocol|
| <font color=Blue>**Smart Contracts**</font> ||
|Contract Engine| Support for WASM and EVM|
|Contract Language| Support for Solidity, C++and WBC-Liquid|
|Engine features| Turing Complete, Sandbox Run|
|Version Control| BFS-based support for multi-version contracts|
| <font color=Blue>**Cryptographic Algorithms and Protocols**</font>  ||
| State Secret Algorithm| Support|
| State Secret SSL| Support|
| hash algorithm| Keccak256、SM3 |
| symmetric encryption algorithm| AES、SM4 |
| asymmetric encryption algorithm|ECDSA、SM2|
| asymmetric encryption elliptic curve|secp256k1、sm2p256v1|
| <font color=Blue>**Safety control**</font>  ||
|communication security| Supports full-process SSL|
|Access Security| PKI-based identity authentication system|
|Certificate Management| Support certificate issuance, revocation, update|
|Permission Control| Supports fine-grained permission control|
| <font color=Blue>**privacy protection**</font> ||
|Physical isolation| Data isolation between groups|
|Scenario-based privacy protection mechanism|Based on [WeDPR](https://github.com/WeBankBlockchain/WeDPR-Lab-Core)Support hidden payment, anonymous voting, anonymous bidding, selective disclosure and other scenarios|
| <font color=Blue>**cross-chain protocol**</font> ||
|SPV|Provides an interface for obtaining SPV attestations|
|cross-chain protocol|Based on [WeCross](https://github.com/WeBankBlockchain/WeCross)Support isomorphic, heterogeneous cross-chain|
| <font color=Blue>**Development Support**</font> ||
|Development of chain building tools|Provide [Air version blockchain deployment tool](../tutorial/air/build_chain.html), [Pro Version Block Chain Deployment Tool](../tutorial/pro/pro_builder.html)and [Maxb version blockchain deployment tool](../tutorial/max/max_builder.html)|
|Contract deployment and testing tools|Interactive Console [Java SDK-based Console](../operation_and_maintenance/console/index.html)|
|SDK language|[Java(Recommended Use)](../sdk/java_sdk/index.html)、[Go](../sdk/go_sdk/index.html)、[Python](../sdk/python_sdk/index.html)、[C++](../sdk/cpp_sdk/index.html)、[C](../sdk/c_sdk/index.html)、[Rust](../sdk/rust_sdk/index.html)(Language to be adapted: Node.js, C#、iOS、Android）|
|Rapid component development|Provide [Spring-boot-starter](https://github.com/FISCO-BCOS/spring-boot-starter)、[Spring-boot-crud](https://github.com/FISCO-BCOS/spring-boot-crud)|
| <font color=Blue>**Operation and Maintenance Support**</font> ||
|Dynamic management node|Support dynamically adding, removing and changing nodes|
|Dynamic configuration change|Support dynamic change of system configuration|
|Data Backup and Recovery|Provides data export and recovery service components|
|Monitoring Statistics|Output statistical logs and provide monitoring tools|