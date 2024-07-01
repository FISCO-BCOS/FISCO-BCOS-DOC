# Key Concepts

Blockchain is a technology formed by the cross-combination of multiple disciplines, and this chapter will explain the basic concepts related to blockchain and provide a popular introduction to the basic theories involved.。If you are already familiar with these basic techniques, you can skip this chapter。

## What is Blockchain?

Blockchain (blockchain) is a concept proposed after Bitcoin, in Satoshi Nakamoto's [paper] on Bitcoin(https://bitcoin.org/bitcoin.pdf)The concept of blockchain is not directly introduced in, but a data structure is described in terms of chain of block.。

Chain of block refers to the data organization method in which multiple blocks are concatenated into a chain structure through hash.。Blockchain, on the other hand, uses a cross-combination of multiple technologies to maintain and manage the chain of block data structure to form a comprehensive technical field of non-tamperable distributed ledgers.。

Blockchain technology is a mode of generating, accessing and using trusted data in a peer-to-peer network environment through transparent and trusted rules to build a block-chain data structure that is non-forgeable, difficult to tamper with and traceable.。In terms of technical architecture, blockchain is an overall solution consisting of distributed architecture and distributed storage, block chain data structure, peer-to-peer network, consensus algorithm, cryptography algorithm, game theory, smart contract and other information technologies.。

Blockchain technology and ecology originated from Bitcoin. As more industries such as finance, justice, supply chain, culture and entertainment, social management, and the Internet of Things pay attention to this field of technology, they hope to apply its technical value to a wider range of distributed collaboration.。

### Account Book

As the name implies, the ledger is used to manage data such as accounts and transaction flows, and supports functions such as classified bookkeeping, reconciliation, and settlement.。In multi-party cooperation, multiple participants hope to jointly maintain and share a timely, correct and secure distributed ledger to eliminate information asymmetry, improve operational efficiency, and ensure financial and business security.。The blockchain is usually considered to be a core technology for building a "distributed shared ledger," through the chain of block data structure, multi-party consensus mechanism, smart contracts, world state storage and a series of technologies, can achieve consistent, trusted, transaction security, difficult to tamper with the traceable shared ledger.。

The basic contents contained in the ledger are blocks, transactions, accounts, and world status.。

#### Block

A block is a data structure constructed in chronological order. The first block of a block chain is called a "genesis block." The subsequent blocks are identified by "height." The height of each block increases one by one. The new block will introduce the hash information of the previous block, and then use the hash algorithm and the data of the block to generate a unique data fingerprint, thus forming an interlocking block chain structure, called "Blockchain" or block chain。Sophisticated data structure design, so that the data on the chain according to the time of occurrence, traceable and verifiable, if you modify any of the data in any one block, will lead to the entire block chain validation does not pass, and thus the cost of tampering will be high.。

The basic data structure of a block is a block header and a block block. The block header contains some basic information such as block height, hash, blocker signature, and state tree root. The block block contains a batch of receipt information related to the transaction data list. Depending on the size of the transaction list, the size of the entire block will vary. Considering factors such as network propagation, it is generally not too large, ranging from 1M to several M bytes.。

#### Transaction

A transaction can be thought of as a request data sent to the blockchain system for deploying contracts, calling contract interfaces, maintaining the life cycle of contracts, managing assets, exchanging values, etc. The basic data structure of a transaction includes sender, recipient, transaction data, etc.。Users can build a transaction, sign the transaction with their own private key, send it to the chain (through interfaces such as sendRawTransaction), process it by the consensus mechanism of multiple nodes, execute the relevant smart contract code, generate the status data specified by the transaction, then package the transaction into a block, and store it together with the status data. The transaction is confirmed, and the confirmed transaction is considered to be transactional and consistent.。

With the confirmation of the transaction, a transaction receipt (receipt) is generated, which corresponds to the transaction and is stored in the block, which is used to store some information generated during the transaction execution process, such as result codes, logs, and the amount of gas consumed.。Users can use the transaction hash to check the transaction receipt to determine whether the transaction is complete.。

Corresponding to the "write operation" transaction, there is also a"Read Only"The call method is used to read the data on the chain. After receiving the request, the node will access the status information according to the parameters of the request and return it. It will not add the request to the consensus process and will not cause the data on the chain to be modified.。

#### Account

In a blockchain system designed with an account model, the term account represents the unique existence of the user, the smart contract。

In a blockchain system that uses a public-private key system, a user creates a public-private key pair, which is converted by an algorithm such as hash to obtain a unique address string that represents the user's account, and the user uses the private key to manage the assets in the account.。The user account does not necessarily have the corresponding storage space on the chain, but the smart contract manages the user's data on the chain, so this kind of user account will also be called "external account."。

For smart contracts, when a smart contract is deployed, it has a unique address on the chain, also known as a contract account, pointing to the contract's status bits, binary code, and an index of relevant status data.。During the operation of the smart contract, binary code will be loaded at this address to access the corresponding data in the world state storage according to the state data index, write the data to the world state storage according to the operation results, and update the state data index in the contract account.。When a smart contract is cancelled, it mainly updates the contract status bit in the contract account and invalidates it, generally without directly clearing the actual data of the contract account.。

#### State of the World

FISCO BCOS uses the "account model" design, that is, in addition to the storage space for blocks and transactions, there will be a storage space for the results of the smart contract operation.。The state data generated during the execution of the smart contract is confirmed by the consensus mechanism and stored on each node in a distributed manner.。

The existence of state storage space enables the blockchain to store a variety of rich data, including user account information such as balances, smart contract binary codes, smart contract running results and other related data, smart contract execution process will be from the state storage to obtain some data to participate in the operation, for the realization of complex contract logic provides a basis.。

On the other hand, maintaining state data requires a lot of storage costs. As the chain continues to run, state data will continue to expand. For example, if a complex data structure such as Patricia Tree is used, the capacity of state data will be further expanded.。

### consensus mechanism

Consensus mechanism is the core concept in the field of blockchain, no consensus, no blockchain。As a distributed system, the blockchain can be used by different nodes to participate in the calculation, witness the execution of transactions, and confirm the final calculation results.。The process of collaborating with these loosely coupled, non-trusting participants to reach a trust relationship and ensure consistency, continuous collaboration, can be abstracted as a "consensus" process, and the algorithms and strategies involved are collectively referred to as consensus mechanisms.。

#### Node

A computer that has installed the hardware and software required for the blockchain system and is added to the blockchain network can be called a "node."。Nodes participate in the network communication, logic operation, data verification, verification and storage of blocks, transactions, status and other data of the blockchain system, and provide the client with the interface for transaction processing and data query.。The node identifier uses a public-private key mechanism to generate a string of unique NodeIDs to ensure its uniqueness on the network.。

According to the degree of participation in the calculation and the stock of data, the nodes can be divided into consensus nodes and observation nodes.。Consensus nodes participate in the entire consensus process, as bookkeepers package blocks and as validators validate blocks to complete the consensus process.。The observation node does not participate in consensus, synchronizes data, verifies and saves it, and can be used as a service for data service providers.。

#### consensus algorithm

The core issues that consensus algorithms need to address are:

1. Select the role with bookkeeping rights in the entire system as the leader to initiate a bookkeeping。
2. Participants adopt the bookkeeping given by Leader after multi-level verification using non-repudiation and non-tampering algorithms.。
3. Ensure that the final results received by all participants are consistent and error-free through data synchronization and distributed consistency collaboration。

Common consensus algorithms in the blockchain field include Proof of Work, Proof of Stake, Delegated Proof of Stake, and Practical Byzantine Fault Tolerance (PBFT), Raft, etc., which are commonly used in federated chains. Other cutting-edge consensus algorithms usually combine the organic random number generator with the above-mentioned consensus algorithms to improve their performance and energy consumption.。

The FISCO BCOS consensus module is a plug-in design that supports a variety of consensus algorithms, including PBFT and Raft, and will continue to implement larger and faster consensus algorithms.。

### Smart Contracts

The concept of smart contract was first proposed by Nick Szabo in 1995 and refers to a contract that is defined in digital form to automatically enforce terms, which means that the contract must be implemented in computer code because the rights and obligations established by the smart contract are automatically executed as soon as the parties reach an agreement, and the result cannot be denied.。

FISCO BCOS uses smart contracts not only for asset management, rule definition and value exchange, but also for global configuration, operation and maintenance governance, permission management, etc.。

#### Smart Contract Lifecycle

The life cycle of a smart contract is designed, developed, tested, deployed, run, upgraded, and destroyed.。

Developers write, compile, and unit test smart contract code as needed.。Contract development languages may include solidity, C++, java, go, javascript, rust, etc., the choice of language depends on the platform virtual machine selection。After the contract passes the test, the deployment order is issued to the chain, and after confirmation by the consensus algorithm, the contract takes effect and is called by subsequent transactions.。

When the contract needs to be updated and upgraded, repeat the above steps from development to deployment to release the new contract. The new contract will have a new address and independent storage space, not overwriting the old contract.。The new contract can access the data stored in the old contract through the old contract data interface, or migrate the data of the old contract to the storage of the new contract through data migration.。

Destroying an old contract does not mean erasing all of the contract's data, just setting its status to "invalid" and the contract can no longer be called.。

#### Smart Contract Virtual Machine

In order to run digital smart contracts, blockchain systems must have compilers and executors that can compile, parse, and execute computer code, collectively known as virtual machine architectures.。After the contract is written, it is compiled with the compiler, and the deployment transaction is sent to deploy the contract to the blockchain system. After the consensus of the deployment transaction is passed, the system assigns a unique address to the contract and saves the binary code of the contract. When a contract is called by another transaction, the virtual machine executor loads the code from the contract store and executes it, and outputs the execution result.。

In a blockchain system that emphasizes security, transactionality, and consistency, the virtual machine should have a sandbox feature that shields factors that may cause uncertainty, such as random numbers, system time, external file systems, and networks, and can resist malicious code intrusion, so as to ensure that the results of the execution of the same transaction and the same contract on different nodes are consistent, and the execution process is safe.。

Currently popular virtual machine mechanisms include EVM, controlled Docker, WebAssembly, etc. FISCO BCOS virtual machine modules are modular in design and already support EVM, which is widely welcomed by the community, and will support more virtual machines。

#### Turing Complete

Turing machine and Turing completeness are classic concepts in the field of computer science. An abstract computing model proposed by mathematician Alan Matheson Turing (1912-1954), extended to the field of blockchain, mainly refers to the contract support judgment, jump, loop, recursion and other logical operations, support a variety of data types such as shaping, strings, structure data processing capabilities, and even have certain object-oriented features such as inheritance, derivation, interface, etc。

Most of the blockchains that emerged after 2014 support Turing-complete smart contracts, making the blockchain system more programmable, and based on the basic features of the blockchain (such as multi-party consensus, difficult to tamper with, traceability, security, etc.), you can also implement business contracts with certain business logic, such as The Ricardian Contract (The Ricardian Contract), or you can use smart contracts to implement。

The execution of the contract also needs to deal with the "shutdown problem," that is, to determine whether the program will solve the input problem within a limited time, and end the execution and release resources.。Imagine a contract that is deployed across the network and is executed on every node when it is called, and if the contract is an infinite loop, it means that the entire system may be exhausted.。Therefore, the handling of the shutdown problem is also an important concern of Turing's complete computing system in the blockchain field.。

## Consortium chain concept analysis

The industry usually divides the type of blockchain into public chain, alliance chain, private chain.。A public chain is a chain in which everyone can participate anytime, anywhere, even anonymously.；A private chain refers to a chain owned by a subject (such as an institution or a natural person), managed and used by privatization.；Alliance chain usually refers to multiple subjects to reach a certain agreement, or the establishment of a business alliance, multi-party joint formation of the chain, to join the alliance chain members need to be verified, generally identity-aware。Because of the access mechanism, the alliance chain is also commonly referred to as the "permission chain."。

Because the alliance chain has access and identity management from the formation, accession, operation, transaction and other links, the operation on the chain can be controlled by permissions, consensus generally adopts PBFT and other consensus mechanisms based on multi-party multi-round verification voting, does not adopt the high energy consumption mechanism of POW mining, the network scale is relatively controllable, in the transaction delay, transaction consistency and certainty, concurrency and capacity can be greatly optimized.。

While inheriting the advantages of blockchain technology, the alliance chain is more suitable for sensitive business scenarios with high performance and capacity requirements, emphasizing regulation and compliance, such as finance, justice, and a large number of businesses related to the real economy.。The route of the alliance chain, taking into account business compliance and stability and business innovation, is also the direction of the country and industry to encourage development.。

### Performance

#### performance index

The most common processing performance indicator of a software system is TPS (Transaction Per Second), which is the number of transactions that the system can process and confirm per second.。In addition to TPS, the performance indicators in the blockchain field include confirmation delay, network size, etc.。

The confirmation delay refers to the time taken by the transaction to be confirmed after it is sent to the blockchain network and after a series of processes such as verification, calculation and consensus, for example, a block in the Bitcoin network is 10 minutes, and it takes 6 blocks, or one hour, for the transaction to be confirmed with a high probability.。Using the PBFT algorithm, the transaction can be confirmed in seconds, and once confirmed, it has final certainty, which is more suitable for financial and other business needs.。

Network size refers to how many consensus nodes can be supported to work together under the premise of ensuring a certain TPS and confirmation delay.。The industry generally believes that the use of PBFT consensus algorithm system, the node size of about 100, and then increase will lead to TPS decline, confirm the delay increased。The industry currently has a consensus mechanism for selecting bookkeeping groups through random number algorithms that can improve this problem。

#### Performance optimization

Performance optimization has two directions, scale up and scale out.。Upscaling refers to optimizing hardware and software configuration on the basis of limited resources, greatly improving processing power, such as the use of more efficient algorithms, the use of hardware acceleration, etc.。Parallel expansion refers to the system architecture has good scalability, can be used to carry different users, business flow processing, as long as the appropriate increase in hardware and software resources, can carry more requests.。

Performance metrics and software architecture, hardware configurations such as CPU, memory, storage specifications, and network bandwidth are closely related, and with the increase of TPS, the pressure on storage capacity will increase accordingly, which needs to be considered comprehensively.。

### Security

Security is a big topic, especially for blockchain systems built on distributed networks。At the system level, attention needs to be paid to network attacks, system penetration, data destruction and leakage, and at the business level to ultra vires operations, logical errors, asset losses due to system stability, and privacy violations.。

Security should be concerned"The short board of the barrel "requires a comprehensive protection strategy that provides multi-faceted, comprehensive security protection, meets demanding security standards, and provides security best practices to align the security levels of all participants to ensure the security of the entire network.。

#### access mechanism

Access mechanism refers to the need to meet the identity-aware, credible qualifications, and technically reliable standards before either institutions or individuals form and join the chain, and the formation of the alliance chain will only be initiated after the subject information is reviewed by multiple parties, and then the nodes of the audited subjects will be added to the network to assign public and private keys that can be sent to the audited personnel.。
After the completion of access, institutions, nodes, personnel information will be registered to the chain or reliable information services, all the chain of behavior can be traced back to the institutions and people。

#### Permission Control

Permission control on the alliance chain is the control of different personnel to read and write data at various sensitive levels. The subdivision can list different permissions such as contract deployment, in-contract data access, block data synchronization, system parameter access and modification, node start and stop, etc. According to business needs, more permission control points can be added.。

Permissions are assigned to roles and can be followed by typical role-based permission access control (Role-Based Access Control) design, a reference design is to divide the roles into operation managers, transaction operators, application developers, operation and maintenance managers, supervisors, each role can also be subdivided according to the needs of the level, a complete model may be very large and complex, according to the needs of the scene to carry out appropriate design, can achieve the degree of business security and control.。

#### privacy protection

Business scenarios based on blockchain architecture require all participants to output and share relevant data for joint calculation and verification.。How to protect the privacy-related parts of shared data and how to avoid leaking privacy during operations is a very important issue.。

Privacy protection is first of all a management issue, which requires that when building a system to carry out business, we should grasp the principle of "minimum authorization and express consent," manage the whole life cycle of data collection, storage, application, disclosure, deletion and recovery, establish daily management and emergency management systems, set up regulatory roles in highly sensitive business scenarios, introduce third-party inspection and auditing, and engage in pre-event and post-event control of all aspects.。

Technically, you can use data desensitization, business isolation or system physical isolation to control the scope of data distribution, but also the introduction of cryptographic methods such as zero-knowledge proof, secure multi-party computing, ring signature, group signature, blind signature, etc., the data for high-strength encryption protection.。

#### Physical isolation

This concept is mainly used in the field of privacy protection, "physical isolation" is a complete means to avoid privacy data leakage, physical isolation means that only participants who share data communicate with each other in the network communication layer, participants who do not participate in sharing data can not communicate with each other in the network, do not exchange even a byte of data.。

Relatively speaking, it is logical isolation, where participants can receive data that is not related to them, but the data itself is protected by permission control or encryption, so that participants without authorization or keys cannot access and modify it.。However, with the development of technology, the rights controlled data or encrypted data may still be cracked after several years.。

For highly sensitive data, a "physical isolation" strategy can be used to eliminate the possibility of being cracked at the root.。The corresponding cost is the need to carefully screen the sensitivity level of the data, carefully plan the isolation strategy, and allocate sufficient hardware resources to carry different data。

### Governance and Regulation

#### Alliance chain governance

Alliance chain governance involves a series of issues such as multi-participant coordination, incentive mechanism, safe operation, supervision and audit, etc. The core is to clarify the responsibilities and rights of each participant, workflow, build a smooth development and operation and maintenance system, and ensure the legal compliance of the business, including security issues can be prevented in advance after the emergency treatment.。In order to achieve governance, rules need to be developed and implemented to ensure that all participants agree on and implement them.。

A typical alliance chain governance reference model is that all participants jointly form an alliance chain committee, discuss and decide together, set various roles and assign tasks according to the needs of the scenario, such as some institutions are responsible for development, some institutions are involved in operation management, all institutions are involved in trading and operation, smart contracts are used to realize management rules and maintain system data, committees and regulators can have certain management authority, such as reviewing and setting up contracts for business, institutions, personnel, etc。

In the alliance chain with a sound governance mechanism, the participants carry out peer-to-peer cooperation in accordance with the rules, including asset transactions, data exchange, greatly improve operational efficiency, promote business innovation, while compliance and security are also guaranteed.。

#### Rapid deployment

The general steps of building a blockchain system include: obtaining hardware resources, including servers, networks, memory, hard disk storage, etc., configuring the environment, including selecting a specified operating system, opening network ports and related policies, bandwidth planning, storage space allocation, etc., obtaining blockchain binary runnable software or compiling from the source code, and then configuring the blockchain system, including the creation block configuration, runtime parameter configuration, log configuration, etc., for multiple interconnection configurations, including node。

How to simplify and speed up the above steps to make the construction and group chain process simple, fast, error-free, and low-cost needs to be considered from the following aspects:
First, standardize the target deployment platform, prepare the operating system, dependent software list, network bandwidth and storage capacity, network strategy and other key hardware and software in advance, align versions and parameters, make the platform available, rely on complete.。Popular cloud services, docker and other methods can help build such a standardized platform.。

Then, from the user's point of view, optimize the blockchain software construction, configuration and group chain process, provide rapid construction, automatic group chain tools, so that users do not need to pay attention to many details, a few simple steps to run the chain for development debugging, online operation.。

FISCO BCOS attaches great importance to the user's deployment experience, provides a one-click deployment command line, helps developers quickly build a development and debugging environment, provides enterprise-level tethering tools, and is oriented to multi-agency joint group chain scenarios. It flexibly configures parameters such as hosts and networks, manages relevant certificates, and facilitates collaboration among multiple enterprises.。Optimized for rapid deployment, reducing the time for users to set up the blockchain to less than a few minutes to half an hour。

#### Data governance

Blockchain emphasizes data layer-by-layer verification and history traceability. A common solution is that since the Genesis block, all data will be stored on all participating nodes (except light nodes), resulting in data expansion and capacity constraints. Especially in scenarios that carry massive services, after a certain period of time, general storage solutions can no longer accommodate data, and massive storage costs are high. Another aspect is security. Full data is permanently stored and may face the risk of historical data leakage.。

Data governance consists of several strategies: tailoring migration, parallel scaling, and distributed storage.。How to choose the need to combine scenario analysis。

For data with strong time characteristics, if the clearing and settlement cycle of a business is one week, then the data before one week does not need to participate in online calculation and verification, and the old data can be migrated from the node to the big data storage to meet the requirements of data query and verifiability and the requirements of business storage life.。

For a business that continues to expand in scale, such as a sharp increase in the number of users or contract certificates, you can allocate different logical partitions for different users and contracts, each logical partition has an independent storage space, and only carries a certain amount of data.。The design of the partition makes it easier to control the allocation of resources and cost management.。

Combined with data pruning migration and parallel expansion, the capacity cost and security level of data are well controlled, facilitating the development of large-scale business.。

#### Operation and Maintenance Monitoring

The blockchain system is logically consistent in terms of construction and operation, and the hardware and software systems of different nodes are basically the same.。Its standardized features bring convenience to operation and maintenance personnel, and can use common tools, operation and maintenance policies and operation and maintenance processes to build, deploy, configure, and troubleshoot blockchain systems, thereby reducing operation and maintenance costs and improving efficiency.。

Operations and maintenance personnel on the alliance chain operations will be controlled by the permission system, operations and maintenance personnel have the right to modify the system configuration, start and stop the process, view the operation log, troubleshooting and other permissions, but do not participate in business transactions, can not directly view the user data with a high level of security and privacy, transaction data.。

During the operation of the system, various operating indicators can be monitored through the monitoring system, the health of the system can be evaluated, and an alarm notification can be issued when a failure occurs, which is convenient for operation and maintenance to respond quickly and deal with it.。

The monitoring dimensions include basic environment monitoring, such as CPU proportion, system memory proportion and growth, disk IO situation, network connection number and traffic, etc.。

Blockchain system monitoring includes such things as block height, transaction volume and virtual machine computation, consensus node out-of-block voting, etc.。

Interface monitoring includes, for example, interface call count, interface call time consumption, interface call success rate, etc.。

Monitoring data can be output through logs or network interfaces to facilitate interfacing with the organization's existing monitoring systems, reusing the organization's monitoring capabilities and existing O & M processes.。After receiving the alarm, the operation and maintenance personnel use the operation and maintenance tools provided by the alliance chain to view system information, modify the configuration, start and stop the process, and handle faults.。

#### Regulatory audit

With the development of blockchain technology and business form exploration, it is necessary to provide regulatory support functions on the blockchain technology platform to prevent the blockchain system from being outside the laws, regulations and industry rules and becoming a carrier of money laundering, illegal financing or criminal transactions.。

The audit function is mainly used to meet the audit internal control, responsibility identification and event traceability requirements of the blockchain system, which requires effective technical means to carry out accurate audit management in line with the industry standards to which the business belongs.。

Regulators can be used as nodes to access the blockchain system, or interact with the blockchain system through the interface, regulators can synchronize all the data for audit analysis, tracking the global business process, if found abnormal, can be issued to the blockchain with regulatory authority instructions, business, participants, accounts and other control, to achieve "penetrating supervision"。

FISCO BCOS supports regulatory audits in terms of role and authority design, functional interfaces, audit tools, etc.。
