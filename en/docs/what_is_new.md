# New features in 2.0 version

## Group architecture

Group architecture is the main one of many new features in FISCO BCOS 2.0, which is inspired by the group chat mode that everyone is familiar with. The group building is very flexible, so a few persons can quickly build a topic group to communicate. The same person can participate in multiple groups, and send and receive messages in parallel. The existing groups can continue to add members.

In a network with group architecture, depending on different service scenario, there may be multiple different ledgers exist. The blockchain node may select a group to join according to the business relationship, and participate in the process of data sharing and consensus of the corresponding ledgers. The characteristics of the architecture are:

- Each group independently implements the consensus process, and the participants in the group decide how to conduct consensus. The consensus within one group is not affected by other groups. Each group has an independent ledger to maintain its own transaction and data, so that groups can be decoupling each other to operate independently, and achieve a better privacy isolation;

- The agency's nodes need to be deployed only once, and can participate in different multi-party collaboration services through group settings, or divide one service into groups according to user, time and other dimensions. The group architecture can be rapidly expanded in parallel. While expanding the scale of business, it greatly simplifies the  complexity of operation and reduces the management cost.

For more group introductions, please refer to [Group Architecture Design Document](./design/architecture/group.md) and [Group Usage Tutorial](./manual/group_use_cases.md)

## Distributed storage

FISCO BCOS 2.0 has added a support for distributed data storage, which allows nodes to store data in remote distributed systems for overcoming the limitations of localized data storage. This program has the following advantages:

- Support multiple storage engines, select high-availability distributed storage systems, and support data expansion easily and quickly;
- Isolating calculations and data, so node failures will not cause data anomalies;
- Data can be stored in a more secure quarantine area like remote end, which makes sense in many scenarios;
- Distributed storage not only supports the Key-Value form, but also supports the SQL method for making business development easier;
- The storage of world state has changed from the original MPT storage structure to distributed storage for avoiding the problem of the performance degradation caused by the rapid expansion of world state;
- Optimize the structure of the data storage for saving storage space.

Meanwhile, FISCO BCOS 2.0 is still compatible with the local storage mode in 1.0 version. For more information on storage, please refer to [Distributed Storage Operations Manual](./manual/distributed_storage.md).

## Parallel computing model

In 2.0 version, a parallel processing mechanism for contract transactions has been added to further increase the concurrent throughput of contracts.

In 1.0 version and most of traditional blockchain platforms, transaction is packaged into a block, and executed serially in the transaction sequence in a block.
In 2.0 version, based on pre-compiled contracts, a set of parallel transaction processing models has been implemented. Based on this model, transaction mutex variables can be customized.
During the block execution process, based on the transaction mutex variable, the system will automatically build a transaction dependency graph--DAG, what to execute the transaction in parallel based on, can the performance increase several times (depending on the number of CPU cores).

For the introduction to more parallel computing models, please refer to the parallel transaction's [Design Document](./Design/Parallel/dag.md) and [User Manual](./manual/transaction_parallel.md).

## Precompiled contract

FISCO BCOS 2.0 provides a pre-compiled contract framework that supports compiling contracts in C++. The advantages are faster contract calling, faster running, less resources consuming, and easier computing in parallel. These greatly improve the efficiency of entire system. FISCO BCOS has built-in multiple system-level contracts that provide access control, permission management, system configuration, and CRUD-style data access etc.. These features are naturally integrated into the underlying platform without manual deployment.

FISCO BCOS provides standardized interfaces and examples to help users with secondary development for making it easy for users to compile high-performance business contracts, and to easily deploy them to FISCO BCOS. The pre-compiled contract framework is compatible with the EVM engine to form a "dual-engine" architecture. The users who are familiar with the EVM engine can select to combine Solidity contracts with pre-compiled contracts to achieve significant efficiency while meeting business logic.

In addition, CRUD and the similar operations are also implemented by precompiled contracts. For more introductions to precompiled contracts, please refer to [Precompiled Design Documentation](./design/virtual_machine/precompiled.md) and [Precompiled Contract Development Documentation](./manual/smart_contract.html#id2).

## CRUD contract

FISCO BCOS 2.0 has added a contractual interface specification that conforms to the CRUD interface for simplifying the cost of migrating mainstream SQL-oriented business applications to blockchain. The benefits are:

- Similar to the traditional business development model, so it reduces the cost of contract development learning;
- Contracts only need to care about the core logic. Storage and calculation are separated to facilitate contract upgrading;
- The underlying logic of CRUD is implemented based on pre-compiled contracts. Data storage adopts distributed storage. These make more efficient;

Meanwhile, 2.0 version is still compatible with the contract in 1.0 version. For more introduction to CRUD interface, please refer to [CRUD interface Manual](./manual/smart_contract.html#crud).

## Console

FISCO BCOS 2.0 has added console as an interactive client tool in FISCO BCOS 2.0.

The console installation is simple and convenient. After simple configuration, console can communicate with the chain node. Console has rich commands and good interactive experience. Users can query the blockchain status, read and modify configuration, manage blockchain nodes, deploy and call contract through console. Console helps users to manage, develop, operate and maintain blockchain, and to reduce the complicated operation and the threshold of use.

Compare to traditional scripting tools such as nodejs, console is simple to install and has better experience. For details, please refer to [Console Manual](./manual/console.md).

## Virtual machine

In 2.0 version, the latest Ethereum virtual machine version has been introduced for supporting Solidity 0.5 version. At the same time, the EVMC extension framework is introduced for supporting the expansion of different virtual machine engines.

The underlying internal integration supports interpreter virtual machines. It can be extended to support the virtual machines such as WASM/JIT in the future.

For more information on virtual machines, please refer to [Virtual Machine Design Document] (./design/virtual_machine/index.html)


## Key management service

In 2.0 version, disk encryption has been remodeled and upgraded. When the disk encryption function is enabled, relying on KeyManager service for key management causes more secure.

KeyManager is released on Github open source. The interaction protocol between the node and KeyManager is open. It supports agency to design the KeyManager service that conforms to its own key management specification, such as hardware encryption technology. 

For more details, please refer to [Manual Document](./manual/storage_security.md) and [Design Document](./design/features/storage_security.md).

## Admission control

In 2.0 version, we have remodeled and upgraded the admission mechanism, including the admission of network access and group access to securely control chain and data access in different dimensions.

We adopt new permission control system, design access permission based on list, and support CA blacklist mechanism, which can shield the evil/failed nodes.

For details, please see [Admission Mechanism Design Document](./design/security_control/index.html)

## Asynchronous event

In 2.0 version, the mechanisms such as transaction on chain asynchronous notifications, blocks on chain asynchronous notifications, and custom AMOP message notifications are supported together.

## Module remodeling

In 2.0 version, we have remodeled and upgraded the core modules to perform modular unit testing and end-to-end integration testing. The automated continuous integration and continuous deployment is supported.

