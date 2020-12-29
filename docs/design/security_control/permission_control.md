# 权限控制

标签：``安全控制`` ``访问控制`` ``权限控制`` 

----
## 权限控制介绍

与可自由加入退出、自由交易、自由检索的公有链相比，联盟链有准入许可、交易多样化、基于商业上隐私及安全考虑、高稳定性等要求。因此，联盟链在实践过程中需强调“权限”及“控制”的理念。

为体现“权限”及“控制”理念，FISCO BCOS平台基于分布式存储，提出分布式存储权限控制的机制，可以灵活，细粒度的方式进行有效的权限控制，为联盟链的治理提供重要的技术手段。分布式权限控制基于外部账户(tx.origin)的访问机制，对包括合约部署，表的创建，表的写操作（插入、更新和删除）进行权限控制，表的读操作不受权限控制。 在实际操作中，每个账户使用独立且唯一的公私钥对，发起交易时使用其私钥进行签名，接收方可通过公钥验签知道交易具体是由哪个账户发出，实现交易的可控及后续监管的追溯。     

## 权限控制规则
权限控制规则如下：    
1. 权限控制的最小粒度为表，基于外部账户进行控制。     
2. 使用白名单机制，未配置权限的表，默认完全放开，即所有外部账户均有读写权限。    
3. 权限设置利用权限表（\_sys_table_access_）。权限表中设置表名和外部账户地址，则表明该账户对该表有读写权限，设置之外的账户对该表仅有读权限。

## 权限控制分类

分布式存储权限控制分为对用户表和系统表的权限控制。用户表指用户合约所创建的表，用户表均可以设置权限。系统表指FISCO BCOS区块链网络内置的表，系统表的设计详见[存储文档](../storage/storage.md)。系统表的权限控制如下所示：   

|表名             |表存储数据说明       |权限控制意义           |
|:---------------|:-------------|:-----------|
|`_sys_tables_`      |存储所有表的结构               |控制部署合约和创建表   |
|`_sys_table_access_`|存储权限控制信息               |控制权限功能设置       |
|`_sys_consensus_`   |存储共识节点和观察节点的列表   |控制节点类型设置       |
|`_sys_cns_`         |存储cns列表                    |控制使用CNS            |
|`_sys_config_`      |存储系统配置的列表             |控制系统配置设置       |


针对用户表和每个系统表，SDK分别实现三个接口进行权限相关操作：
- 用户表：
  - **public String grantUserTableManager(String tableName, String address)：** 根据用户表名和外部账户地址设置权限信息。
  - **public String revokeUserTableManager(String tableName, String address)：** 根据用户表名和外部账户地址去除权限信息。
  - **public List\<PermissionInfo\> listUserTableManager(String tableName)：** 根据用户表名查询设置的权限记录列表(每条记录包含外部账户地址和生效块高)。
- _sys_tables_表：
  - **public String grantDeployAndCreateManager(String address)：** 增加外部账户地址的部署合约和创建用户表权限。
  - **public String revokeDeployAndCreateManager(String address)：** 移除外部账户地址的部署合约和创建用户表权限。
  - **public List\<PermissionInfo\> listDeployAndCreateManager()：** 查询拥有部署合约和创建用户表权限的权限记录列表。
- _sys_table_access_表：
  - **public String grantPermissionManager(String address)：** 增加外部账户地址的管理权限的权限。
  - **public String revokePermissionManager(String address)：** 移除外部账户地址的管理权限的权限。
  - **public List\<PermissionInfo\> listPermissionManager()：** 查询拥有管理权限的权限记录列表。
- _sys_consensus_表：
  - **public String grantNodeManager(String address)：** 增加外部账户地址的节点管理权限。
  - **public String revokeNodeManager(String address)：** 移除外部账户地址的节点管理权限。
  - **public List\<PermissionInfo\> listNodeManager()：** 查询拥有节点管理的权限记录列表。
- _sys_cns_表：
  - **public String grantCNSManager(String address)：** 增加外部账户地址的使用CNS权限。
  - **public String revokeCNSManager(String address)：** 移除外部账户地址的使用CNS权限。
  - **public List\<PermissionInfo\> listCNSManager()：** 查询拥有使用CNS的权限记录列表。
- _sys_config_表：
  - **public String grantSysConfigManager(String address)：** 增加外部账户地址的系统参数管理权限。
  - **public String revokeSysConfigManager(String address)：** 移除外部账户地址的系统参数管理权限。
  - **public List\<PermissionInfo\> listSysConfigManager()：** 查询拥有系统参数管理的权限记录列表。

设置和移除权限接口返回json字符串，包含code和msg字段，当无权限操作时，其code定义-50000，msg定义为“permission denied”。当成功设置权限时，其code为0，msg为“success”。

## 数据定义
权限信息以系统表的方式进行存储，权限表表名为_sys_table_access_，其字段信息定义如下：

```eval_rst

+-----------+-------+--------+-----+---------------------------------------------+
|字段       |类型   |是否为空|主键 |描述                                         |
+===========+=======+========+=====+=============================================+
|table_name |string |No      |PRI  | 表名称                                      |
+-----------+-------+--------+-----+---------------------------------------------+
|address    |string |No      |     | 外部账户地址                                |
+-----------+-------+--------+-----+---------------------------------------------+
|enable_num |string |No      |     | 权限设置生效区块高度                        |
+-----------+-------+--------+-----+---------------------------------------------+
|_status_   |string |No      |     | 分布式存储通用字段，“0”表示可用，“1”表示移除|
+-----------+-------+--------+-----+---------------------------------------------+

```
其中，对权限表的插入或更新，当前区块不生效，在当前区块的下一区块生效。状态字段为“0”时，表示权限记录处于正常生效状态，为“1”时表示已删除，即表示权限记录处于失效状态。  

## 权限控制设计

#### 权限控制功能设计
根据交易信息确定外部账户，待操作的表以及操作方式。待操作的表为用户表或系统表。系统表用于控制区块链的系统功能，用户表用于控制区块链的业务功能，如下图所示。外部账户通过查询权限表获取权限相关信息，确定权限后再操作相关的用户表和权限表，从而可以控制相关的系统功能和业务功能。

```eval_rst
.. mermaid::

    sequenceDiagram
        participant 外部账户
        participant 权限表
        participant 系统表
        participant 用户表

        外部账户->>权限表: 查询
        权限表->>系统表: 控制
        权限表->>用户表: 控制
        系统表->>区块链的系统功能: 控制
        用户表->>区块链的业务功能: 控制

```

#### 权限控制流程设计
权限控制的流程如下：首先由客户端发起交易请求，节点获取交易数据，从而确定外部账户和待操作的表以及操作表的方式。如果判断操作方式为写操作，则检查该外部账户针对操作的表的权限信息（权限信息从权限表中查询获取）。若检查有权限，则执行写操作，交易正常执行；若检查无权限，则拒绝写操作，返回无权限信息。如果判断操作方式为读操作，则不检查权限信息，正常执行读操作，返回查询数据。流程图如下。

```eval_rst
.. mermaid::

   graph TB
        classDef blue fill:#4C84FF,stroke:#4C84FF,stroke-width:4px, font:#1D263F, text-align:center;

        classDef yellow fill:#FFEEB8,stroke:#FFEEB8,stroke-width:4px, font:#1D263F, text-align:center;

        classDef light fill:#EBF5FF,stroke:#1D263F,stroke-width:2px,  font:#1D263F, text-align:center;

        subgraph 权限控制流程
        A((开始))-->B
        B(客户端发起交易请求)-->C
        C(确定待操作的表和操作方式)-->D
        D(操作方式是否为写操作)-->|否|E
        E(获取查询结果)
        D-->|是|F
        F(是否有权限记录缓存)-->|否|G
        F-->H
        G(查询权限表)-->H
        H(是否有权限)-->|否|I
        H(是否有权限)-->|是|J
        I(拒绝写操作)
        J(执行写操作)

        class A,B,C,D,E,F,G,H,I,J light
        end
```

## 权限控制工具

FISCO BCOS的分布式存储权限控制有如下使用方式：
- 针对普通用户，通过控制台命令使用权限功能，具体参考[权限控制使用手册](../../manual/distributed_storage.md)。
- 针对开发者，SDK根据权限控制的用户表和每个系统表均实现了三个接口，分别是授权，撤销和查询权限接口。可以调用[SDK API](../../sdk/java_sdk.html#web3sdk-api)的PermissionService接口使用权限功能。
