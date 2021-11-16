# New features in 2.0 version

## Group architecture

Group architecture is the main feature in FISCO BCOS 2.0, which is inspired by the group chat mode with that everyone is familiar. The group building is very flexible, so a few persons can quickly build a topic group to communicate. The same person can participate in multiple groups, send and receive messages in parallel. The existing groups can continue to add members.

In a network with group architecture, depending on different service scenario, there may be multiple different ledgers exist. The blockchain node may select a group to join according to the business relationship, and participate in the process of data sharing and consensus of the corresponding ledgers. The characteristics of the architecture are:

- Each group implements the consensus process independently, and the participants in the group decide how to conduct consensus. The consensus within one group is not affected by other groups. Each group has an independent ledger to maintain its transaction and data so that groups can be decoupling each other to operate independently, and achieve better privacy isolation;

- The agency's nodes need to be deployed only once and can participate in different multi-party collaboration services through group settings. They also can divide one service into groups according to the user, time, and other dimensions. The group architecture can expand rapidly in parallel. While expanding the scale of business, it dramatically simplifies the complexity of the operation and reduces the management cost.

For more group introductions, please refer to [Group Architecture Design Document](../design/architecture/group.md) and [Group Usage Tutorial](./manual/group_use_cases.md)

## Distributed storage

FISCO BCOS 2.0 has added support for distributed data storage, which allows nodes to store data in remote distributed systems for overcoming the limitations of localized data storage. This program has the following advantages:

- Support multiple storage engines, select high-availability distributed storage systems, and support data expansion easily and quickly;
- Isolating calculations and storage, so node failures can not cause data anomalies;
- Store data in a more secure quarantine area like a remote end, which makes sense in many scenarios;
- Distributed storage not only supports the Key-Value form but also supports the SQL method for making business development easier;
- The storage of world state has changed from the original MPT storage structure to distributed storage for avoiding the problem of the performance degradation caused by the rapid expansion of world state;
- Optimize the structure of the data storage for saving storage space.

Meanwhile, FISCO BCOS 2.0 is still compatible with the local storage mode in the 1.0 version. For more information on storage, please refer to [Distributed Storage Operations Manual](../manual/distributed_storage.md).

## Parallel computing model

In the 2.0 version, a parallel processing mechanism for contract transactions has been added to increase the concurrent throughput of contracts further.

In the 1.0 version and most of the traditional blockchain platforms, the transaction is packaged into a block and executed serially in the transaction sequence in a block.
In the 2.0 version, it implements a set of parallel transaction processing model. Based on this model, transaction mutex variables can be customized.
During the block execution process, based on the transaction mutex variable, the system builds a transaction dependency graph--DAG automatically, what to execute the transaction in parallel based on, increase the performance several times (depending on the number of CPU cores).

For the introduction to more parallel computing models, please refer to the parallel transaction's [Design Document](../design/parallel/dag.md) and [User Manual](../manual/transaction_parallel.md).

## Precompiled contract

FISCO BCOS 2.0 provides a pre-compiled contract framework that supports compiling contracts in C++. It makes contract calling faster,  running faster, resources consuming fewer, and more accessible computing in parallel. These significantly improve the efficiency of the entire system. FISCO BCOS has several built-in system-level contracts that provide access control, such as permission management, system configuration, CRUD-style data access. These functions are naturally integrated into the underlying platform without manual deployment.

FISCO BCOS provides standardized interfaces and examples to help users with secondary development for making it easy to compile high-performance business contracts, and to quickly deploy them to FISCO BCOS. The pre-compiled contract framework is compatible with the EVM engine to form a "dual-engine" architecture. The users who are familiar with the EVM engine can select to combine Solidity contracts with pre-compiled contracts to achieve significant efficiency while meeting business logic.

In addition, CRUD and the similar operations are also implemented by precompiled contracts. For more introductions to precompiled contracts, please refer to [Precompiled Design Documentation](../design/virtual_machine/precompiled.md) and [Precompiled Contract Development Documentation](../manual/smart_contract.html#id2).

## CRUD interface

FISCO BCOS 2.0 has added a contractual interface specification that conforms to the CRUD interface for simplifying the cost of migrating mainstream SQL-oriented business applications to the blockchain. The benefits are:

- Similar to the traditional business development model, so it reduces the cost of contract development learning;
- Contracts only need to care about the core logic. Storage and calculation are separated to facilitate contract upgrading;
- The underlying logic of CRUD is implemented based on pre-compiled contracts. Data storage adopts distributed storage. These make it more efficient;

Meanwhile, the 2.0 version is still compatible with the contract in the 1.0 version. For more introduction to CRUD interface, please refer to [CRUD interface Manual](../manual/smart_contract.html#crud).

## Console

FISCO BCOS 2.0 has added console as an interactive client tool in FISCO BCOS 2.0.

The console installation is convenient and straightforward. After simple configuration, the console can communicate with the chain node. The console has rich commands and good interactive experience. Users can query the blockchain status, read and modify the configuration, manage blockchain nodes, deploy and call contract through the console. The console helps users to manage, develop, operate, and maintain the blockchain, and to reduce the complicated operation and the threshold of use.

Compare to traditional scripting tools such as node-js, and the console is simple to install and has a better experience. For details, please refer to [Java SDK-based console manual](../console/console_of_java_sdk.md) and [Web3SDK-based console manual](../console/console.md).

## Virtual machine

The 2.0 version supports the latest Ethereum virtual machine version Solidity 0.5 version. At the same time, it introduces the EVMC extension framework for supporting the expansion of different virtual machine engines.

The underlying internal integration supports interpreter virtual machines. It can be extended to support virtual machines such as WASM/JIT in the future.

For more information on virtual machines, please refer to [Virtual Machine Design Document] (../design/virtual_machine/index.html)


## Key management service

In the 2.0 version, we upgrade disk encryption and release KeyManager service for secure keys management.

The interaction protocol between the node and the KeyManager is open. It supports agency to design the KeyManager service that conforms to its keys management specification, such as hardware encryption technology. 

For more details, please refer to [Manual Document](../manual/storage_security.md) and [Design Document](../design/features/storage_security.md).

## Admission control

In 2.0 version, we have remodeled and upgraded the admission mechanism, including the admission of network access and group access to securely control chain and data access in different dimensions.

We adopt new permission control system, design access permission based on a TABLE, and support CA blacklist mechanism, which can shield the evil/failed nodes.

For details, please see [Admission Mechanism Design Document](../design/security_control/index.html)

## Asynchronous event

In 2.0 version, the mechanisms such as transaction on-chain asynchronous notifications, block on-chain asynchronous notifications, and custom AMOP message notifications are supported together.

## Module remodeling

In the 2.0 version, we have remodeled and upgraded the core modules to perform modular unit testing and end-to-end integration testing. The continuous automated integration and continuous deployment are supported.

