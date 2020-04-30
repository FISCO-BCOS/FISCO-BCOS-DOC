# Introduction

FISCO BCOS is the first safe and controllable enterprise-level financial consortium blockchain platform open source by domestic enterprises. It is jointly created by the FISCO open source working group and officially launched in December 2017.

The community links multiple parties with open source. At present, more than 500 enterprises and institutions and more than 10,000 community members have joined to build and co-governance, and developed into the largest and most active domestic consortium blockchain platform ecosystem. The underlying platform is highly available and easy to use after extensive application and practice. Hundreds of application projects are developed based on the FISCO BCOS underlying platform, and over 60 have been steadily operating in the production environment, covering cultural copyright, judicial services, government services, Internet of Things, finance, smart communities and other fields.


```eval_rst
.. note::
    FISCO BCOS takes the actual needs of the consortium blockchain as a starting point, taking into account performance, security, maintainability, ease of use, and scalability, and supports multiple SDK, and provides visual middleware tools, greatly reducing the time to build chains, develop and deploy applications. In addition, FISCO BCOS passed the two evaluations of the Trusted Blockchain evaluation function and performance of the Information Communication Institute, and the single-chain TPS can reach 20,000.
```

## Architecture

FISCO BCOS 2.0 adopts an integrated two-wing multi-engine architecture to achieve the horizontal expansion of system throughput, supports the rapid formation of consortium blockchain, and allows enterprises to build blockchains as conveniently as the WeChat group. Performance, security, operability, ease of use, and scalability have great advantages.

![](../images/architecture/plane.jpg)


## Core module

FISCO BCOS adopts high-throughput scalable [multi-group architecture](./design/architecture/group.md), which can dynamically manage multiple chains and groups to meet the expansion and isolation requirements of multiple business scenarios. Modules include:

- <font color=blue>**[Consensus mechanism](./design/consensus/index.md)**</font>: Pluggable consensus mechanism, supporting PBFT, Raft and RPBFT consensus algorithms, low transaction confirmation delay, high throughput, and ultimate consistency. Among them, PBFT and RPBFT can solve Byzantine problems and have higher security.

- <font color=blue>**[Storage](./design/storage/index.md)**</font>: The storage of the world state is changed from the original MPT storage structure to [distributed storage](./design/storage/storage.md), avoids the problem of performance degradation caused by the rapid expansion of the world state. Introduces a pluggable storage engine, supports LevelDB, RocksDB, MySQL and other back-end storage, supports data expansion quickly and easily, and isolates calculation from data, reducing the impact of node failure on node data.

- <font color=blue>**[Network](./design/p2p/p2p.md)**</font>: Support network compression, and implement a good distributed network distribution mechanism based on the idea of load balancing to minimize bandwidth overhead.

## Performance

In order to improve system performance, FISCO BCOS optimizes transaction execution in terms of improving transaction execution efficiency and concurrency, so that transaction processing performance can reach more than 10,000 levels.

- <font color=blue>**[Precompiled contract based on C++](./design/virtual_machine/precompiled.md)**</font>: The Precompiled contract written in C++ language is built into the blockchain platform, and the execution efficiency is higher.

- <font color=blue>**[Transaction execution in parallel](./design/parallel/dag.md)**</font>: Based on the DAG algorithm to build a transaction execution flow within a block based on the mutually exclusive relationship between transactions, maximizing parallel execution of transactions within a block.


## Safety

Considering the high security requirements of the consortium blockchain platform, in addition to the TLS security protocol used for communication between nodes and between nodes and clients, FISCO BCOS also implements a complete set of security solutions:

- <font color=blue>**[Network access mechanism](./design/security_control/node_management.md)**</font>: Restrict nodes from joining and exiting the alliance chain, and delete the malicious nodes of the specified group from the group, ensuring system security

- <font color=blue>**[Black and white list mechanism](./design/security_control/certificate_list.md)**</font>: Each group can only receive messages from the corresponding group to ensure the isolation of network communication between the groups; the CA blacklist mechanism can disconnect the network connection from the malicious node in time, ensuring the security of the system

- <font color=blue>**[Authority management mechanism](./design/security_control/permission_control.md)**</font>: Based on distributed storage permission control mechanism, flexible and fine-grained control of permissions for external account deployment contracts and creation, insertion, deletion and update of user tables

- <font color=blue>**[Support OSCCA-approved algorithm](./manual/guomi_crypto.md)**</font>: Support OSCCA-approved encryption, signature algorithm and OSCCA-approved SSL communication protocol

- <font color=blue>**[Disk encryption algorithm](./design/features/storage_security.md)**</font>: Support the disk encryption algorithm to ensure the confidentiality of the data on the chain

- <font color=blue>**[Key management scheme](./design/features/storage_security.md)**</font>: Based on the disk encryption algorithm, the KeyManager service is used to manage the node key, which is more secure

- <font color=blue>**[Homomorphic encryption](./manual/privacy.md)、[Group/Ring signature](./manual/privacy.md)**</font>: Homomorphic encryption and group ring signature interfaces are provided on the chain to meet more business needs


## Operability

In the consortium blockchain platform, the operation and maintenance of the blockchain is crucial. FISCO BCOS provides a complete set of operation and maintenance deployment tools, and introduces **contract naming service**, **data archiving and migration**, **contract lifecycle management** to improve Operation and Management efficiency.

- <font color=blue>**[Operation and Management deployment tool](./enterprise_tools/index.md)**</font>: Convenient tool for deploying, managing and monitoring multi-institution multi-group consortium blockchain, supporting multiple operations such as expanding nodes and expanding new groups

- <font color=blue>**[Contract naming service](./design/features/cns_contract_name_service.md)**</font>: Establish a mapping relationship between the contract address to the contract name and the contract version, so that the caller can easily call the contract on the chain by remembering the simple contract name

- **Data archiving, migration and export functions**: Provide data export components, support on-chain data archiving, migration and export, increase the maintainability of on-chain data, and reduce the complexity of operation

- <font color=blue>**[Contract lifecycle management](./design/features/contract_management.md)**</font>: Provide contract life cycle management function on the chain, which is convenient for the chain administrator to manage the contract on the chain


## Ease of use

FISO BCOS introduces tools such as development and deployment tools, interactive console, blockchain browsers, etc. to improve the ease of use of the system and greatly reduce the time to build chains and deploy applications。

- <font color=blue>**[Development and deployment tools](./manual/build_chain.md)**</font>
- <font color=blue>**[Interactive command line tool console](./manual/console.md)**</font>
- <font color=blue>**[Blockchain browser](./browser/browser.md)**</font>
- <font color=blue>**[Visualized blockchain management platform WeBASE](https://webase-web.readthedocs.io/en/latest/)**</font>

In order to facilitate the rapid development of applications for developers of different languages, FISCO BCOS also supports [Java SDK](./sdk/java_sdk.md)、[Node.js SDK](./sdk/nodejs_sdk/index.md)、[Python SDK](./sdk/python_sdk/index.md) and [Go SDK](https://github.com/FISCO-BCOS/go-sdk)


## Scalability

In order to improve the scalability of FISCO BCOS, the FISCO BCOS team proposed the cross-chain collaboration solution **WeCross** and privacy protection solution **WeDPR**.

- <font color=blue>**[Cross-chain collaboration solution WeCross](https://fintech.webank.com/wecross/)**</font>: Support cross-chain transaction transactions, meet the atomicity of cross-chain transactions, manage cross-chain transactions, support multi-party collaborative management, and avoid single-point risks.

- <font color=blue>**[Scene-based privacy protection solution WeDPR](https://fintech.webank.com/wedpr/)**</font>: Provide anonymous payment, anonymous voting, anonymous auction and selective disclosure.