# System Design

Tags: "System Design" "Consensus" "Distributed Storage" "Contract Catalog"

FISCO BCOS 3.x version adopted**Microservices Modularization**Design architecture, the overall system includes access layer, scheduling layer, computing layer, storage layer and management layer:

- **access layer**: Responsible for blockchain**The ability to connect** , including the "external gateway service" that provides P2P capabilities and the "internal gateway service" that provides SDK access。
***
- **scheduling layer**The "brain center" system for the operation and scheduling of the blockchain kernel, responsible for the entire blockchain system**operation scheduling**, including network distribution scheduling, transaction pool management, consensus mechanism, calculation scheduling and other modules。
***
- **calculation layer**: Responsible**Transaction Validation**The core of the blockchain is to decode the transaction and execute it in the contract virtual machine to get the result of the transaction execution。
***
- **Storage Tier**: Responsible**Drop Disk Storage** Data such as transaction, block and ledger status。
***
- **Management**: Implemented for each module of the entire blockchain system**visual management** platform, including management functions such as deployment, configuration, logging, and network routing。FISCO BCOS 3.x system architecture based on open source microservices framework Tars。

***
------
___

Support**Flexible split combination** Microservice modules, which can build different forms of service patterns, currently include:
***
- **Lightweight Air Edition**All-in-one encapsulation mode is used to compile all modules into a binary (process), a process is a blockchain node, including all functional modules such as network, consensus, access, etc., using local RocksDB storage, suitable for beginners, function verification, POC products, etc。
***
- **Pro Edition**It consists of RPC, Gateway service, and multiple blockchain node services. Multiple node services can form a group. All nodes share access layer services. Access layer services can be extended in parallel. It is suitable for production environments with controllable capacity (within T level)。

- **Large Capacity Max Edition**: Consists of all services at each layer, each service can be expanded independently, the storage adopts distributed storage TiKV, and the management adopts Tars-Framwork service。It is suitable for scenarios where massive transactions are linked and a large amount of data needs to be stored on disk。
----------

```eval_rst
.. toctree::
   :maxdepth: 1

   architecture.md
   tx_procedure.md
   protocol_description.md
   consensus/index.rst
   sync.md
   storage/storage.md
   DMC.md
   virtual_machine/index.rst
   committee_design.md
   storage/storage_security.md
   storage/archive.md
   guomi.md
   rip.md
   network_compress.md
   security_control/index.rst
   hsm.md
   cns_contract_name_service.md
   contract_directory.md
   boostssl.md
   amop_protocol.md
   p2p.md
   compatibility.md
```
