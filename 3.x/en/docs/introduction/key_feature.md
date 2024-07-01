# 2. Key Features

Tags: "Key Features"

**Air, Pro, Max: Deployable in three architectural forms**

- **Lightweight Air Edition**: Has the same form as v2.0, all functions in one blockchain node (all-in-one）。The architecture is simple and can be quickly deployed in any environment。You can use it for blockchain entry, development, testing, POC verification, etc.。
- **Pro Edition**The architecture allows the blockchain core function module to be extended in a multi-group manner while implementing the partition deployment of the access layer and the core module by using the access layer module of the blockchain node as a process.。The architecture implements partition isolation to cope with possible future business expansion and is suitable for production environments with continuous business expansion.。
- **Large Capacity Max Edition**Based on the Pro version, the architecture provides the ability to switch the core module of the chain between master and standby, and can deploy transaction executors and access distributed storage TiKV through multiple machines to achieve parallel expansion of computing and storage.。A node in this architecture consists of a series of microservices, but it relies on high O & M capabilities and is suitable for scenarios that require massive computing and storage.。

**Pipeline: Block pipeline to generate blocks continuously and compactly**

- The block generation process can be split into four stages: packaging, consensus, execution, and placement.。In previous designs, the system had to wait for the previous block to complete four stages before entering the next block generation.。This version uses a pipeline design, so that the four stages of adjacent blocks overlap before and after, reducing the waiting time between blocks and improving the speed of continuous block output.。For example, block 103 is being packaged, 102 is in consensus, 101 is being executed, and 100 is falling.。[Related Documents: Two-Stage Parallel Byzantine Consensus](../design/consensus/consensus.md)

**DMC realizes multi-machine expansion of transaction processing performance**

- In the traditional design, the transaction execution can only be single machine。v3.0 Stable Edition uses an original deterministic multi-contract parallel solution (Deterministic Multi-Contract, referred to as DMC), can automatically process transaction conflicts when the system is running, and schedule multiple transactions to different machines for parallel execution, users can expand the computing instance to achieve parallel expansion of transaction processing performance.。[Related Documentation: Deterministic Multi-Contract Parallelism](../design/parallel/DMC.md)

**+TiKV: Distributed transactional commit, supporting mass storage**

- V3.0 stable version integrates the TiKV storage engine and is developed on the basis of it, supports distributed transactional commit, combines DMC multi-computing instances, gives full play to storage performance, and supports massive data on the chain.。At the same time, this version introduces the KeyPage mechanism, referring to the cache mechanism of memory pages, the key-The value is organized into pages for access, which solves the problem of using key in the past.-When storing data in the way of value, the storage data is fragmented, which improves the locality of data access and is more suitable for large-scale data access.。[Related Documentation: Transaction-Based Storage Module](../design/storage/storage.md)

**Blockchain File System: WYSIWYG Contract Data Management**

- The blockchain file system can be used to manage the resources on the chain. You can manage the contracts on the chain like a file system and call them through the path of the contract. Commands include pwd, cd, ls, tree, mkdir, and ln.。Users can experience the feature through the console。[Related Document: Blockchain Contract File System](../design/contract_directory.md)

**SDK basic library: more convenient access to the whole platform**

- The v3.0 stable version builds a general-purpose national secret basic component, which encapsulates the national secret algorithm, national secret communication protocol, domestic cipher machine access protocol and FISCO BCOS blockchain basic data structure, based on which SDKs on different platforms, different operating systems and different programming languages can be quickly developed, greatly improving R & D efficiency.。[Related Documentation: Multilingual SDK](../sdk/index.md)

**Transaction Parallel Conflict Analysis Tool: Automatically Generate Transaction Conflict Variables**

- To implement parallel transactions in v2.0, you need to manually specify the transaction conflict variable when writing the contract。This version introduces a transaction parallel conflict analysis tool, no need to manually specify transaction conflict variables when writing contracts, just focus on their own code implementation, contract compilation tool automatically generates transaction conflict variables, the corresponding transactions can be automatically executed in parallel.。

**WBC-Liquid: Write a contract with Rust**

- In addition to supporting the Soldity language, this version also supports writing contracts in Rust.。WBC-Liquid is a Rust-based smart contract programming language developed by Microblockchain. With the help of Rust language features, it can achieve more powerful programming functions than Solidity language.。[Related Documentation: Liquid Online Documentation](https://liquid-doc.readthedocs.io/zh_CN/latest/)

**Permission governance framework: multi-party voting governance blockchain**

- This version has a built-in permission governance framework that provides effective permission control directly from the blockchain implementation layer.。After the permission governance function is enabled, multi-party voting authorization is required to modify the blockchain.。Based on the framework, blockchain participants can customize governance policies on the blockchain and update them iteratively through voting.。[Related Documents: Rights Management System Design](../design/committee_design.md)

**Feature Inheritance and Upgrade**

The stable version of v3.0 also inherits and upgrades many of the important features of v2.0, including:

- PBFT consensus algorithm: immediate consensus algorithm for second-level confirmation of transactions
- Solidity: Support up to version 0.8.11
- CRUD: uses a table structure to store data. This version encapsulates an easier-to-use interface, which is more friendly to business development.
- AMOP: on-chain messenger protocol, with the help of blockchain P2P network to achieve information transmission, to achieve access to the blockchain data communication between applications
- Disk encryption: The private key and data of the blockchain node are encrypted and stored in the physical hard disk, and the physical hardware cannot be decrypted even if it is lost.
- Cryptographic algorithm: built-in group ring signature and other cryptographic algorithms, can support a variety of secure multi-party computing scenarios
- Blockchain monitoring: real-time monitoring and data reporting of blockchain status
