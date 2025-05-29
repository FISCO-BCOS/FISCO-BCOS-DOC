# Core concept (Revision in progress)

Blockchain is intricated with multiple technical approaches. This chapter will illustrate the basic concept of blockchain and knowledges about some relative theories. You can skip this chapter if you are familiar with these techniques.

## What is blockchain

Blockchain is a concept proposed after bitcoin. In Satoshi Nakamoto’s [Paper](https://bitcoin.org/bitcoin.pdf) about bitcoin, he didn’t mention about blockchain but described the data structure as “chain of block”.

Chain of block is an organization type of data, a chain-like structure connected by hashes of blocks. Blockchain refers to a comprehensive technology intricated with many techniques to maintain and manage the chain of block and form an immutable distributed ledger.

Blockchain technology is adapted to build an unforgeable, immutable and traceable block-chain data structure in an equal networking environment through transparent and trustable rules, a way to realize and manage trusted data generation, in & out and usage. As of the technical structure, blockchain is an all-rounded solution formed by multiple information technologies including distributed structure and storage, block-chain data structure, p2p networking, consensus algorithm, cryptography algorithm, game theory and smart contract.

Blockchain technology and eco-system is originated from bitcoin. Today, serious look has been taken on this technology by broad industries like finance, justice, supply chain, entertainment, social administration, IoT, etc. They want to put its great technical value into extensive distributed cooperation. Meanwhile, progress has also been seen in the blockchain technology and product model. FISCO BCOS blockchain platform concentrates on capabilities of improving security, performance, usability, friendly operation, privacy protection, compliance and regulation based on blockchain technology. It grows together with the community eco-system to better present functions like multi-participation, smart cooperation, professional division of labor, and value sharing.

### Ledger

Ledger, as its name suggests, is used for managing data of accounts or transaction records and supports functions like distributed ledger, reconciliation and settlement, etc.. In multi-lateral cooperation, partners want to co-maintain or share a real-time, correct and safe distributed ledger to eliminate inequality of message and improve efficiency and ensure funds and business security. And blockchain are often regarded as a core technology for building up distributed sharing ledger, contributed by joint efforts of technologies like block-chain data structure, multi-party consensus mechanism, smart contract, global state storage, which can realize a consistent, trustable, safe and immutable traceable sharing ledger. Ledger contains contents like block number, transactions, accounts and global state.


#### Block

Block is a data structure built in chronological order. The first block of blockchain is called Genesis Block, and the latter blocks are identified with block number. Each block number increases one by one. New block will take the hash of the former block and generate a unique data print by hash algorithm and local block data, so that a coherent block-chain structure, namely blockchain, is formulated. This delicate data structure design makes it possible that data on chain saved in order and is traceable and verifiable. If any of the data is changed, it will cost extremely high because of being stuck in verification of all chain.

The basic data structure of a block is block header and block body. Block header contains block height, hash, generator’s signature, state root. Block body contains the returning message of a transaction data list. The size of block will be different according to the size of the transaction list, and it won’t be too large considering network transmission, up to between 1M to 10M byte.

#### Transaction

Transaction can be seen as a request data targeting blockchain system to deploy contract, call contract interface, maintain life cycle of contract and manage assets, conduct value exchange, etc.. The basic data structure of transaction includes sender, receiver and transaction data. User can create a transaction and sign it with its private key, then send on chain (through interface like sendRawTransaction). And it will be consensus by several nodes, executes smart contract code, generates the status data assigned with the transaction and packs it into block and saves with status data. Now the transaction is confirmed and gets its duty and consistency.

As the transaction is confirmed, a receipt will be created and saved in the block correspondently for storage of execution information like result code, log and gas consumption. User can use hash of the transaction to check its receipt to know whether it is finished. Equivalent with “write” transaction, there is a “read-only” method of invocation for reading data on chain.

It shares the similar request method with transaction but invokes functions by call() (not sendRawTransaction() ). When the node receives “read-only” invocation request, it will return with the requested accessed parameter status without inviting the request into consensus process to avoid modification of data on chain.

#### Account

In the blockchain system designed in account model, account represents the uniqueness of user and smart contract.

In the blockchain system adapting private-public key, user creates a public and private key pair and calculates a unique address string by hash or other algorithms to be the account of this user. User uses private key to manage assets in this account. Sometimes there might not be storage space for user account, so smart contract will manage user’s data instead, this account is called “exterior account”.

As of smart contract, when one smart contract is deployed, it has an only address on chain, which is also called contract account that pointing at the index for status bit, binary code, related status data, etc.. During operation of smart contract, it will load binary code through this address and visit data in global state storage by the index of status data. Then, according to the operation result, it will write the data into global state storage and update status data index in the contract account. To deactivate smart contract, user only has to change its status bit into invalid, and the real data of the contract account won’t be cleared out usually.

#### Global state

FISCO BCOS uses account model design, which contains another storage space for smart contract operation result besides space for blocks and transactions. The status data generated during smart contract execution is confirmed by consensus mechanism and saved distributedly on each node to ensure global consistency, verifiability and immutability. And therefore, it’s called “global” status.

Because of status storage space, blockchain is able to save various kinds of data, including user account information like balance, etc., smart contract binary code and operation result or other related data. During execution, smart contract will acquire some data from status storage for calculation, laying the foundation for complex contract logic realization.

On the other hand, status data maintenance costs high in storage, as the chain keeps operating, status data will keep inflating, like adapting the complex data structure Patricia Tree results in expansion of capacity. Therefore, status data can be cut or optimized in some cases, or use storage solution like distributed data warehouse for more extensive data capacity.

### Consensus mechanism

Consensus mechanism is the core concept of blockchain, as there will not be blockchain if without consensus. Blockchain is a distributed system where nodes cooperate to calculate and witness the execution of transactions and confirm the final result. It brings these loosely-coupled untrusted participants together to be trusted partners, and keeps the cooperation being consistent and lasting, which can be abstracted as the process of “consensus”, and the related algorithms and strategies are called consensus mechanism.

#### Node

A computer installed with software and hardware concerning blockchain system and join the blockchain network can be called a “node”. Nodes take part in network communication, logic calculation, data verification and storage of block, transaction and status, etc., and provide client ends interfaces for transaction process and data inquiry. The identification of node adapts public-private key mechanism, which generates a string of unique Node ID to ensure its uniqueness on blockchain.

According to the involvement of calculation and storage of data, nodes can be categorized into consensus node and observation node. Consensus node fully participates in consensus process, packing block as accountant and verifying block as verifier. Observation node doesn’t join consensus process but synchronize data for verification and storage as a data service provider. 

#### Consensus algorithm

Consensus algorithm needs to handle several core problems:

1. pick out a role as with ledger right within the system, and start ledgering as a leader.
2. Participator adapts undeniable and immutable algorithm, verifies in multi-levels and takes the ledger from leader.
3. Data synchronization and distributed cooperation can make sure that all participants receive the same and correct result.

Common algorithm of blockchain includes Proof of Work, Proof of Stake and Delegated Proof of Stake that are often used in public chain, and Practical Byzantine Fault Tolerance (PBFT), RAFT that are often used in consortium chain. Besides, some advanced consensus algorithms often organically combine the above mentioned algorithms with random number generator to improve security, energy consumption and performance, size or other issues.

FISCO BCOS consensus module is designed pluggable and supports many consensus algorithms, including PBFT and RAFT currently, to realize broader and faster consensus algorithms.

### Smart contract

Smart contract, first proposed by Nick Szabo in 1995, is a contract defined in numeric term and can execute clauses automatically. Numeric term means that contract has to be realized by computer codes, for as long as the agreement is reached among participants, the right and responsibility established by smart contract will be executed automatically and its result is undeniable.

FISCO BCOS applies smart contract in not only asset management, rules definition and value exchange, but also overall configuration, maintenance, governance and authority setting, etc.

#### Life cycle of smart contract

The life cycle of smart contract contains steps of design, development, test, deployment, operation, upgrade and deactivation.

Developers edit, compile and unit-test the codes of smart contract, the development language can be solidity, C++, java, go, javascript, rust, etc.. The choice of language depends on the type of virtual machine. After passing test, the contract will be published on chain using deployment command and confirmed by consensus algorithm.

The contract will be called by transaction afterwards after validated. When contract needs upgrade, user has to repeat the above mentioned steps from development to deployment so as to release new contract version, which will own a new address and independent storage space instead of covering the old one. The new contract can access the data in the old contract through the interface, or migrate the old contract data into its own storage. The best practice is of the “behavior contract” for designing execution process and “data contract” for storing data. It decouples data and contract, so when there is change in process but not data, the new behavior contract can access the existed data contract. 

Revoking an old contract doesn’t mean clearing all of its data, but only setting its status to “invalid” so that this contract can’t be called any more.

#### Smart contract virtual machine

To run digital smart contract, blockchain system needs compiler and actuator that are capable of compilation, analysis and code execution, which is called virtual machine system. After the contract is edited and complied by the compiler, user sends deployment transaction to deploy it on blockchain system. Once the transaction has passed consensus process, the system will allocate a binary code with a unique address to reserve contract. When a contract is called by another transaction, virtual machine actuator loads code from contract storage and executes to output execution result.

In a blockchain system which emphasizes security, transactional routines and consistency, the virtual machine should possess sandbox features to block uncertain factors, like random number, system time, exterior file system and network, as well as invasion of malicious code to make sure consistent execution result and safe process of one transaction and one contract on different nodes.

Currently popular virtual machines includes EVM, controlled Docker, WebAssembly, etc.. The virtual machine model of FISCO BCOS adapts modularization design and supports broadly-used EVM. More kinds of adaptable virtual machines can be expected in the future.

#### Turing complete

Turing machine or Turing complete is a classical concept in computing field. It is an abstract computing model proposed by mathematician Alan Mathison Turing (1912-1954) and is stretched into blockchain field, referring to a model where contract supports logical computing like judgement, jump, cycle and recursion, process ability of multiple data types like integer, byte string and structure, and even has object-oriented features like inheritance, derivation and interface. This will make it possible for complex transactional logics and complete contract execution, which is distinct from simple script that only supports operand stack.

Most blockchain systems appearing after 2014 support Turing complete smart contract to make them highly compilable. On top of some basic features of blockchain (like multi-party consensus, immutability, traceability and security), they can realize contract with transactional logics, such as The Ricardian Contract, and smart contract is also adaptable.

The execution of contract needs to process the Halting problem, namely to judge whether the program can solve the input problem within limited time and terminate execution to release resource. Now imagine that a contract is deployed in the whole network, when being called it will also be executed on every node. And if the contract is an infinite cycle, then there is possibility to use up all the system resources. So, the addressing of Halting problem is an important issue of Turing complete computing system in blockchain.

## Consortium blockchain

Usually blockchain is divided into 3 types: public blockchain, consortium blockchain and private blockchain. Public blockchain can be joined by anybody at any time, even anonymously. Private blockchain is owned by an entity (an agency or a nature person) and is managed and used in private way. Consortium blockchain is usually formed by multiple entities who made an agreement on certain protocols, or built a business alliance. Whoever wants to join consortium blockchain needs verification, often with knowable identity. For there is access control, consortium blockchain is also called “permissioned blockchain”.

Consortium blockchain has access control and identity management in most sections from creation, member joining to operation and transaction. Operations on chain can be monitored by permission setting. Consensus mechanism like PBFT based on multi-party multi-round verification voting is adopted here, instead of energy-consuming POW mining mechanism. Therefore, its network scale is relatively controllable and can be massively optimized in aspects like transaction delay, transactional consistency and certainty, concurrency and capacity.

Consortium blockchain has inherited the advantages of blockchain and is more adaptable for some difficult business cases which requires high performance capacity, regulation and compliance, like finance, justice or others that related to entity economy. The way consortium blockchain possesses is suitable both for business compliance and stability and for innovation, which is also been encouraged by the country and the industry.

### Performance

#### Performance indicator

The most popular performance indicator for software system is TPS (Transaction Per Second), the transaction volume that the system could process and confirm per second. The higher the TPS, the better the performance. Besides TPS, the performance indicators for blockchain also include Delay ACK and the network scale, etc..

Delay ACK is the total time used from transaction arriving on blockchain to final confirmation after a series of process including verification, calculation and consensus. For example, each block on bitcoin network costs 10 minutes. And transaction will be processed mostly by 6 blocks, that is 1 hour. In PBFT algorithm, transaction can be confirmed in seconds with final certainty, which caters to the need of finance transactions.

The network scale refers to the number of co-working consensus nodes the system supports on the premise of assured TPS and Delay ACK. It’s believed usually by insiders that the node scale is around hundred level if using PBFT consensus algorithm in a system, and increment of the node scale will result in decrease of TPS and increase of Delay ACK. The consensus mechanism where the accounting group is chosen by random number algorithm can fix the problem.

#### Performance optimization

The is two options of performance optimization: scale up and scale out. scale up is to optimize the configurations of software and hardware on the basis of limited resources to lift up processing ability, like using more efficient algorithm or hardware acceleration, etc.. Scale out means good extendibility of system structure. It uses Sharding and Partition to carry out various users and transaction flows. As long as adding software and hardware resources appropriately, it can load more requests.

Performance indicators, software structure, hardware configuration like CPU, internal memories, storage scale and internet bandwidth, are all closely related. And following the increment of TPS, there will be more pressure for storage capacity, which needs to be considered comprehensively.

### Security

Security is a big topic, especially for blockchain system that built on distributed network with multi-party engagement. In system level, problems like internet attack, system penetration, data corruption or leakage should be concerned. In transaction level, we should consider about unauthorized operation, logic errors, asset impairment caused by system stability and privacy invasion.

To ensure security we need to focus on “the shortest board of buckets” and prepare with comprehensive security strategy providing all-rounded protection that meets high security standard. With best practices in security and equal security for all participants, this will make sure the security within overall network.

#### Access mechanism

Access mechanism refers to the processes for either agencies or persons who want to build or join the blockchain, like multi-party verification of the subject to make sure it has knowable identity, trustable quality and reliable technology before starting the creation work of consortium blockchain, so the verified node will be added into blockchain and allocated with public and private keys that can send transactions. After the access process is done, information of the agency, node or staff will be registered on blockchain or reliable information services, where every operation can be traced down to each agency and person.

#### Permission control

Permission control on consortium blockchain means controls on data read-and-write in various sensitivity levels by different staff. It contains permissions in contract deployment, data access in contract, block data syncing, system parameters access and change, node start-stop, etc.. There can be more permission controls according to different transactional needs.

Permissions are allocated to roles if using Role-Based Access Control model design. One example for reference is to divide roles into operation manager, transaction operator, application developer, O&M manager, administrator. And each role can be further subdivided to meet other needs. The complete model could be very huge and complicated, so it should be designed properly to adapt to transactional needs as well as security concerns.

#### Privacy protection

Business cooperation based on blockchain structure requires all parties to output and share data for calculation and verification. In complicated business context, agencies want better control on their data. And there is also a growing need for personal data privacy protection. Therefore, how to protect the private part of shared data and prevent from privacy leakage during operation becomes an important problem.

Administration is the first area to address privacy protection. When the system starts running, it should keep the principle of “minimum authorized and express consent”, complete life-cycle management of data collection, storage, application, disclosure, deletion and recovery, and establish daily management and emergency management system. For those business transactions with high sensitivity, there should be a regulation role for checking and auditing from a third party so that all sections can be supervised.

Technically, data masking, transaction separation or system physical isolation or other ways can control the scope of data distribution. Meanwhile, cryptographic methods like Zero-knowledge Proof, Secure Multi-Party Computation, Ring Signature, Group Signature and Blind Signature can protect data through strong encryption.

#### Physical isolation

Physical isolation is a radical method to avoid privacy data leakage. In this way, only the participants who share data can communicate in the network layer, others will not be able to communicate or exchange even one byte of data.

There is another method called logic isolation in which participants can receive other data but with access limits or encryption, so unauthorized participants or those with no keys have no right of access and change. However, with the technological development, data that are limited in access or encrypted may be decoded in the future.

For data with extremely high sensitivity, it’s good to use physical isolation strategy to eradicate possibility of being cracked. But meanwhile, it will cost more in detailed screening of data’s sensitivity level and need thorough planning and enough hardware resources to load different data.

### Governance and regulation

#### Governance of consortium blockchain

Governance of consortium involves coordination, incentive mechanism, safe operation and regulation audit of multiple participants. The core is to sort out the responsibility and right, work flow of each party to build up a smooth development and O&M system, guarantee compliance and create precaution and emergency management for security issues. Rules need to be stipulated to make sure every participant reaches agreement and conducts thorough execution to accomplish governance.

A typical reference model for consortium blockchain is that all participants co-found a consortium blockchain committee for joint discussion and decision making, design roles and tasks according to transaction needs, for instance, some agencies work on development and some join operation management, and every agency takes part in transactions and O&M with smart contract managing rules and maintaining system data. The committee and regulator can be given with right of permission control, verifying and setting permissions for transactions, agencies and staff. When it comes to emergency, they can carry out emergency operations like resetting accounts and adjusting transactions through agreed rules in smart contract. When the system needs to be upgraded, the committee will take the responsibility of coordinating each party.

On a consortium blockchain with complete governance mechanism, there will be peer-to-peer cooperation of all participants, including asset transactions and data exchanging, which improves operation efficiency greatly and business innovation as well as guarantees the compliance and security.

#### Fast deployment

The general steps to build a blockchain system include: acquire hardware resources including server, internet, memories, hard disk, etc.; configure the environment by choosing an operation system, opening a network port and making strategies, bandwidth planning and storage space allocation, etc.; acquire binary executable software or compile it from the source code; configure the blockchain system, including genesis block configuration, parameter configuration and log configuration; configure multi-party interconnection, including node access configuration, port discovery, consensus participants list, etc.; configure client ends and developer tools, including the console and SDK, etc.. There are so many complicated and repetitive steps, like the management of certificates and public and private keys, which form a high entry barrier.

Therefore, to simplify and quicken the process of building blockchain with low error rate and cost, these need to be considered: First, standardize the target deployment platform and prepare in advance the operation system, reliable software list, network bandwidth, network strategy and other key software and hardware, match the version with parameters to make the platform available and ready for use. Currently there are cloud services or docker that can help building standardized platform.

Then, take the user experience into consideration by optimizing the formation, configuration and networking of blockchain software and offering toolkit for fast and automatic networking, so users will not be tangled with miscellaneous details but can start operating blockchain with few steps.

FISCO BCOS emphasizes the deployment experience for users and offers command-line for one-click deployment to help developers expedite development and debugging environment building. It provides networking tool of business level for flexible parameters configuration of the host and network, manages relative certificates for easier cooperation among companies when they co-network on blockchain. By optimized deployment method, it shortens the time of building blockchain into a few minutes or within half an hour.

#### Data governance

Blockchain requires data to be verified in each layer and leaves traceable records. Usually, the solution is to save all the data on all nodes (except lightweight nodes), resulting in data inflation and capacity intensity. It is especially obvious with cases that bear massive services. After some time, regular storage solution has limited data capacity, and it costs high to adopt mass storage. Besides, security should also be concerned. The permanent storage of all data may face risk of data leakage. Therefore, it is important to better the design of data governance.

Some strategies of data governance are concluded here: cutting and transfer, parallel expansion and distributed storage. It depends on specific cases to determine which one is suitable.

For data with strong temporal features, like in a case that account clearing happens one time a week, then the data before the week will not be calculated or verified again. The old data can be transferred from node to big data storage to meet the need of data traceability and verifiability and long storage life for transactions. When there is lower data pressure for nodes and history data is kept off-line, more attention can be put on security strategies.

When it comes to snowballing transaction cases, like when users and contract copies increase tremendously, it can allocate each of them to different logic partitions, each of which owns independent storage space and bears certain quantity of data. When data is near capacity limit, it will arrange more resources to store new data. Partition design makes it easier to do resources allocation and cost management.

Combining the strategies of data cutting and transfer with parallel expansion, the cost of data capacity and security of data can get better controlled, and it also benefits the execution of massive transactions.

#### O&M monitoring

Blockchain system presents high consistency in its foundation and operation logic. Different nodes often share the same software and hardware system. Its standardized features bring convenience for operation and maintenance staff. They can use the commonly-used tools, O&M strategy and workflow or others to build, deploy, configure blockchain system and handle faults to realize low O&M cost and high efficiency.

O&M staff has limited authority to operate in consortium blockchain. They have permission to modify system configuration, process start-stop, check operation log and detect troubles, but they are not involved in transactions and cannot check user data or transaction data that rates high in privacy security.

During the operation of system, they can monitor all the operational indicators and evaluate the health of system status through monitoring system. It will send warning messages when there appear faults, so the staff can response and handle them immediately.

The monitoring system covers status of fundamental environment, like CPU occupation rate, system memories rate and incremental, IO status of disk, internet connection quantity and traffic, etc..

The monitoring of blockchain system includes block number, transaction volume and virtual machine computation, and voting and block generation of consensus nodes, etc..

The monitoring of interface includes counting, time consumed, and success rate of API callings.

The monitoring data can be output from log or network interface for agencies to connect with the existing monitoring systems so the monitoring ability and O&M workflows can be multiplexed. When the O&M staff receive the warning message, they can use the O&M tool offered by consortium blockchain to view system information, modify configuration, start-stop process and handle faults, etc..

#### Regulation audit

With the development of blockchain technology and business exploration, the blockchain platform needs a function to support regulation to prevent it from being against regulation rules and laws, or becoming the carrier for money washing, illegal financing and criminal transactions.

The audit function is mainly designed to meet the needs of audit and internal control, responsibility confirmation and event tracing of blockchain system. It should be combined with effective techniques to do accurate audit management according to the specific industrial standards.

Regulators can join the blockchain system as nodes, or interact with blockchain system through interfaces. They can synchronize all the data for audit analysis and trace overall transaction flows. And if they detect exceptions, they can send instruction with regulation authority. Also, they can monitor transactions, participants and accounts to realize “penetrative regulation”.

FISCO BCOS supports regulation audit in aspects like roles and access control design, function interface and audit tool.
