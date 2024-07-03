# Blockchain Permission Control

Tags: "Security Control" "Access Control" "Permission Control" "

----
## Introduction to Permission Control

Compared with the public chain, which is free to join and exit, free to trade and free to search, the alliance chain has the requirements of access permission, transaction diversification, commercial privacy and security considerations, high stability and so on。Therefore, the alliance chain needs to emphasize the concept of "authority" and "control" in practice。

In order to reflect the concept of "permission" and "control," FISCO BCOS platform is based on distributed storage, and proposes a distributed storage permission control mechanism, which can be flexible and fine-grained for effective permission control, providing an important technical means for the governance of the alliance chain。Distributed permission control based on external accounts(tx.origin)access mechanism, including contract deployment, table creation, table write operations (insert, update and delete) for permission control, table read operations are not subject to permission control。 In practice, each account uses an independent and unique public-private key pair, and its private key is used to sign the transaction when it is initiated, so that the recipient can know which account the transaction was issued from through public key verification, thus realizing the control of the transaction and the traceability of subsequent supervision。     

## Permission Control Rules
The permission control rules are as follows:
1. The minimum granularity of permission control is table, which is controlled based on external accounts。     
2. Using the whitelist mechanism, tables with no permissions configured are fully released by default, that is, all external accounts have read and write permissions。    
3. Permission settings use the permission table (\ _ sys _ table _ access _)。If the table name and external account address are set in the permission table, the account has read and write permissions on the table。

## Permission Control Classification

Distributed storage permission control is divided into permission control for user tables and system tables。A user table is a table created by a user contract. You can set permissions on user tables。System tables refer to the tables built into the FISCO BCOS blockchain network. For details about the design of system tables, see [Storage Document](../storage/storage.md)。The permission control of system tables is as follows:

|Table Name|Table Storage Data Description|Meaning of permission control|
|:---------------|:-------------|:-----------|
|`_sys_tables_`      |Structure to store all tables|Control deployment contracts and create tables|
|`_sys_table_access_`|Store permission control information|Control permission function settings|
|`_sys_consensus_`   |Stores a list of consensus and observation nodes|Control Node Type Settings|
|`_sys_cns_`         |store cns list|Controlled use of CNS|
|`_sys_config_`      |List of storage system configurations|Control System Configuration Settings|


For the user table and each system table, the SDK implements three APIs for permission-related operations:
- User table:
  - **public String grantUserTableManager(String tableName, String address)：** Set permission information based on user table name and external account address。
  - **public String revokeUserTableManager(String tableName, String address)：** Remove permission information based on user table name and external account address。
  - **public List\<PermissionInfo\> listUserTableManager(String tableName)：** Query the set permission record list according to the user table name(Each record contains the external account address and the active block height)。
- _ sys _ tables _ tables:
  - **public String grantDeployAndCreateManager(String address)：** Add permissions to deploy contracts and create user tables for external account addresses。
  - **public String revokeDeployAndCreateManager(String address)：** Remove deployment contract and create user table permissions for external account addresses。
  - **public List\<PermissionInfo\> listDeployAndCreateManager()：** Querying the list of permission records that have permission to deploy contracts and create user tables。
- _ sys _ table _ access _ table:
  - **public String grantPermissionManager(String address)：** Add permissions for managing external account addresses。
  - **public String revokePermissionManager(String address)：** Permission to remove administrative permissions for an external account address。
  - **public List\<PermissionInfo\> listPermissionManager()：** Query the list of permission records that have administrative permissions。
- _ sys _ consensus _ table:
  - **public String grantNodeManager(String address)：** Add node management permissions for external account addresses。
  - **public String revokeNodeManager(String address)：** Remove the node management permission of the external account address。
  - **public List\<PermissionInfo\> listNodeManager()：** Query the list of permission records that have node management。
- _ sys _ cns _ Table:
  - **public String grantCNSManager(String address)：** Increase Use CNS permissions for external account addresses。
  - **public String revokeCNSManager(String address)：** Remove Use CNS permission for an external account address。
  - **public List\<PermissionInfo\> listCNSManager()：** Querying the list of records that have permission to use the CNS。
- _ sys _ config _ table:
  - **public String grantSysConfigManager(String address)：** Increase the system parameter management permission of the external account address。
  - **public String revokeSysConfigManager(String address)：** Remove the system parameter management permission of the external account address。
  - **public List\<PermissionInfo\> listSysConfigManager()：** Query the list of records that have permission to manage system parameters。

The API for setting and removing permissions returns a JSON string, which contains code and msg fields. When an operation is performed without permissions, the code definition is -50000, and msg is defined as "permission denied."。When the permission is set successfully, its code is 0 and msg is "success"。

## Data Definition
Permission information is stored as a system table. The permission table name is _ sys _ table _ access _, and its field information is defined as follows:

```eval_rst

+-----------+-------+--------+-----+---------------------------------------------+
|Field|Type|Is empty|primary key|描述|
+===========+=======+========+=====+=============================================+
|table_name |string |No      |PRI  | Table Name|
+-----------+-------+--------+-----+---------------------------------------------+
|address    |string |No      |     | External Account Address|
+-----------+-------+--------+-----+---------------------------------------------+
|enable_num |string |No      |     | Permission Setting Effective Block Height|
+-----------+-------+--------+-----+---------------------------------------------+
|_status_   |string |No      |     | Distributed storage generic field, '0' for available,' 1' for remove|
+-----------+-------+--------+-----+---------------------------------------------+

```
For the insertion or update of the permission table, the current block does not take effect, but takes effect in the next block of the current block。When the status field is "0," the permission record is in the normal effective state. When the status field is "1," the permission record has been deleted. That is, the permission record is in the invalid state。  

## Permission Control Design

#### Permission control function design
Determine external accounts, tables to be operated and how to operate based on transaction information。The table to be operated on is a user table or a system table。The system table is used to control the system functions of the block chain, and the user table is used to control the business functions of the block chain, as shown in the following figure。External accounts can control related system and business functions by querying the permission table to obtain permission-related information, determining the permissions and then operating the relevant user tables and permission tables。

```eval_rst
.. mermaid::

    sequenceDiagram
        participant external account
        participant permission table
        participant system table
        participant user table

        External accounts ->>Permission Table: Query
        Permissions Table ->>System tables: Control
        Permissions Table ->>User Table: Control
        System Tables ->>System functions of the blockchain: Control
        User Table ->>Business Functions of Blockchain: Control

```

#### Permission control process design
The process of permission control is as follows: first, the client initiates a transaction request, and the node obtains the transaction data to determine the external account and the table to be operated and the way the table is operated。If the operation mode is determined to be a write operation, check the permission information of the external account for the operation table (the permission information is obtained from the permission table)。If the check has permission, the write operation is performed and the transaction is executed normally；If no permission is checked, the write operation is rejected and no permission information is returned。If the operation mode is determined to be a read operation, the permission information is not checked, the read operation is performed normally, and the query data is returned。The flow chart is as follows。

```eval_rst
.. mermaid::

   graph TB
        classDef blue fill:#4C84FF,stroke:#4C84FF,stroke-width:4px, font:#1D263F, text-align:center;

        classDef yellow fill:#FFEEB8,stroke:#FFEEB8,stroke-width:4px, font:#1D263F, text-align:center;

        classDef light fill:#EBF5FF,stroke:#1D263F,stroke-width:2px,  font:#1D263F, text-align:center;

        subgraph permission control process
        A((Commencement))-->B
        B(Client initiates transaction request)-->C
        C(Determine the table to be operated and the operation mode)-->D
        D(Whether the operation mode is write operation)-->|否|E
        E(Get Query Results)
        D-->|Yes|F
        F(Permission to record cache)-->|否|G
        F-->H
        G(Query permission table)-->H
        H(Permission)-->|否|I
        H(Permission)-->|Yes|J
        I(Reject Write)
        J(Perform write operation)

        class A,B,C,D,E,F,G,H,I,J light
        end
```

## Permission Control Tool

The distributed storage permission control of FISCO BCOS can be used in the following ways:
-For ordinary users, use the permission function through console commands. For details, please refer to [Permission Management User Guide](../../develop/committee_usage.md)。
-For developers, the SDK implements three interfaces according to the user table controlled by permissions and each system table, namely authorization, revocation and query permission interfaces。
