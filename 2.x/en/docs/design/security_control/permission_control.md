# Permission control

## Introduction

Comparing with public chain with open access and free transaction or query, consortium chain are required to have access control, diverse transactions, privacy protection in business level and high stability. Therefore, "permission" and "control" are strongly emphasized in the implementation of consortium chain.

In this case, FISCO BCOS has proposed a flexible and delicate permission control mechanism based on distributed storage, which performs fundamental technological supports for its governance. The permission control system manages permissions on contract deployment and table create/insert/update/delete operations (read is not included), based on the access mechanism of exterior accounts (tx.origin). In actual operations, each account owns one and only public and private key pair. Private key is for signature when sending transactions. The receiver will verify to know which account the transaction is from through the public key, helping the management and tracing back of transaction and regulation.     

## Rules
The rules of permission control include:    
1. the minimum granularity of permission control is table, based on exterior accounts.     
2. whitelist mechanism is adopted: all exterior accounts are default to have write/read permission.
3. permission table (\_sys_table_access_) is used in permission control, if table name and account address are in the permission table, the account has write/read permission to the table; if not, account has only read permission.

## Types

Permission control based on distributed storage contains permissions of user table and sys table. User table is created by user contract. All user tables can set permissions. System table is built in FISCO BCOS network. The design of system table is introduced in [Storage Documentation](../storage/storage.md). Permission control of system table is as below:   

|table name             |data storage description       |purpose of permission control           |
|:---------------|:-------------|:-----------|
|`_sys_tables_`      |store all table structure               |control contract deployment and table creation   |
|`_sys_table_access_`|store permission control information               |control permission setting     |
|`_sys_consensus_`   |store sealer and observer list   |control node type setting       |
|`_sys_cns_`         |store cns list                    |control CNS using            |
|`_sys_config_`      |store list of system config            |control system config setting      |


Targeting user table and each system table, SDK conducts permission management of three APIs:
- User table:
  - **public String grantUserTableManager(String tableName, String address)：** set permission information according to user table name and exterior account address.
  - **public String revokeUserTableManager(String tableName, String address)：** remove permission information according to user table name and exterior account address.
  - **public List\<PermissionInfo\> listUserTableManager(String tableName)：** inquire permission information according to the user table name (each record contains exterior account address and valid block number).
- _sys_tables_表：
  - **public String grantDeployAndCreateManager(String address)：** grant permission of contract deployment and user table creation to exterior account.
  - **public String revokeDeployAndCreateManager(String address)：** remove permission of contract deployment and user table creation of exterior account.
  - **public List\<PermissionInfo\> listDeployAndCreateManager()：** inquire permission records of contract deployment and user table creation.
- _sys_table_access_表：
  - **public String grantPermissionManager(String address)：** grant permission of management to exterior account.
  - **public String revokePermissionManager(String address)：** remove permission of management of exterior account.
  - **public List\<PermissionInfo\> listPermissionManager()：** inquire permission records of permission management.
- _sys_consensus_表：
  - **public String grantNodeManager(String address)：** grant node management permission to exterior account.
  - **public String revokeNodeManager(String address)：** remove node management permission of exterior account.
  - **public List\<PermissionInfo\> listNodeManager()：** inquire permission records of node management.
- _sys_cns_表：
  - **public String grantCNSManager(String address)：** grant CNS permission of exterior account.
  - **public String revokeCNSManager(String address)：** remove CNS permission of exterior account.
  - **public List\<PermissionInfo\> listCNSManager()：** inquire CNS permission records.
- _sys_config_表：
  - **public String grantSysConfigManager(String address)：** grant system parameter management permission to exterior account.
  - **public String revokeSysConfigManager(String address)：** remove system parameter management permission of exterior account.
  - **public List\<PermissionInfo\> listSysConfigManager()：** inquire system parameter management permission records.

Set and remove permission API and return json string, containing code and msg fields. For operations with no permission, code is set to 50000 and msg defined as “permission denied”; for those granted with permission, code is 0 and msg “success”.

## Data definition
Permission information is stored in the form of system table, which is named _sys_table_access_ with following fields:

```eval_rst

+-----------+-------+--------+-----+---------------------------------------------+
|field      |type   |empty or not|primary key |description                                         |
+===========+=======+========+=====+=============================================+
|table_name |string |No      |PRI  | table name                                    |
+-----------+-------+--------+-----+---------------------------------------------+
|address    |string |No      |     | exterior account address                                |
+-----------+-------+--------+-----+---------------------------------------------+
|enable_num |string |No      |     | valid block number after permission setting                        |
+-----------+-------+--------+-----+---------------------------------------------+
|_status_   |string |No      |     | general fields in distributed storage, “0” means available, “1” means removed|
+-----------+-------+--------+-----+---------------------------------------------+

```
The insertion or update to the permission table is validated in the next block instead of current block. When the state field is 0, it means that the permission record is in normal valid status; if 1, the permission record is removed and invalid.  

## Design

#### Function design of permission control
Transaction information contains exterior account, pending table and operation methods. Pending table is user table or system table. As what shows below, system table manages system functions of blockchain; user table manages transactional function of blockchain. Exterior accounts can get permission information through permission table, and then manage system and transactional functions by operating user table and permission table.

```eval_rst
.. mermaid::

    sequenceDiagram
        participant exterior account
        participant permission table
        participant system table
        participant user table

        exterior account->>permission table: query
        permission table->>system table: control
        permission table->>user table: control
        system table->>system function of blockchain: control
        user table->>transactional function of blockchain: control

```

#### Process design
The process of permission control: first, client end sends transaction request, node acquires transaction data to confirm exterior account, pending table and operation type; if it's write operation, check the permission (from permission list), if it has permission, execute write operation; if not, reject write operation and return no permission; if it's read operation, skip permission information check, execute read operation and return data. The process is shown below.

```eval_rst
.. mermaid::

   graph TB
        classDef blue fill:#4C84FF,stroke:#4C84FF,stroke-width:4px, font:#1D263F, text-align:center;

        classDef yellow fill:#FFEEB8,stroke:#FFEEB8,stroke-width:4px, font:#1D263F, text-align:center;

        classDef light fill:#EBF5FF,stroke:#1D263F,stroke-width:2px,  font:#1D263F, text-align:center;

        subgraph process of permission control
        A((start))-->B
        B(client end send transaction)-->C
        C(confirm pending table and operation type)-->D
        D(operation type is write or not)-->|no|E
        E(get result)
        D-->|yes|F
        F(permission records cached or not)-->|no|G
        F-->H
        G(inquire permission table)-->H
        H(with permission or not)-->|no|I
        H(with permission or not)-->|yes|J
        I(reject write operation)
        J(execute write operation)

        class A,B,C,D,E,F,G,H,I,J light
        end
```

## Tool

The operation methods of permission control in FISCO BCOS include:
- non-developers can user permission function through console command, the detailed operation is introduced in [Operation tutorial for permission control](../../manual/distributed_storage.md).
- For developers, SDK has implemented 3 APIs (grant/remove/inquire) in light of user table and system table, so they can call PermissionService of [SDK API](../../sdk/sdk.html) to realize permission management.
