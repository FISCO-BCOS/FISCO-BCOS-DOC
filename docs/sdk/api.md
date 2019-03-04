# Java SDK API

Java SDK API主要分为Web3j API和Precompiled Service API。其中Web3j API可以查询区块链相关的状态，发送和查询交易信息；Precompiled Service API可以管理区块链相关配置以及实现特定功能。

## Web3j API
Web3j API是由web3j对象调用的FISCO BCOS的RPC API，其API名称与RPC API相同，参考[RPC API文档](../api.md)。调用Web3j API示例参考[SDK配置与使用](./config.md)。

## Precompiled Service API
预编译合约是FISCO BCOS底层通过C++实现的一种高效智能合约。SDK已提供预编译合约对应的Java接口，控制台通过调用这些Java接口实现了相关的操作命令，体验控制台，参考[控制台手册](../manual/console.md)。SDK提供Precompiled对应的Service类，分别是分布式控制权限相关的AuthorityService，[CNS](../design/features/CNS_contract_name_service.md)相关的CnsService，系统属性配置相关的SystemConfigService和节点类型配置相关ConsensusService。

设置和移除接口返回json字符串，包含code和msg字段，当无权限操作时，其code定义-1，msg定义为“non-authorized”。当成功操作时，其code为1(影响1条记录)，msg为“success”。其他返回码如下：

|错误码|消息内容|
|:----|:---|
|0|success|
|50000|permission denied|
|51000|table name and address exist|
|51001|table name and address does not exist|
|51100|invalid nodeId|
|51101|the last sealer cannot be removed|
|51102|table name and address does not exist|
|51103|the node is not in group peers|
|51104|the node is already in sealer list|
|51105|the node is already in observer list|
|51200|contract name and version exist|
|51201|version exceeds maximum(40) length|
|51300|invalid configuration key|

调用Precompiled Service API示例参考[Java SDK配置与使用](./config.md)。

### AuthorityService
SDK提供对[分布式控制权限](../manual/priority_control.md)的支持，AuthorityService可以配置权限信息，其API如下：
- **public String grantUserTableManager(String tableName, String address)：** 根据用户表名和外部账号地址设置权限信息。
- **public String revokeUserTableManager(String tableName, String address)：** 根据用户表名和外部账号地址去除权限信息。
- **public List\<AuthorityInfo\> listUserTableManager(String tableName)：** 根据用户表名查询设置的权限记录列表(每条记录包含外部账号地址和生效块高)。
- **public String addDeployAndCreateManager(String address)：** 增加外部账号地址的部署合约和创建用户表权限。
- **public String removeDeployAndCreateManager(String address)：** 移除外部账号地址的部署合约和创建用户表权限。
- **public List\<AuthorityInfo\> queryDeployAndCreateManager()：** 查询拥有部署合约和创建用户表权限的权限记录列表。
- **public String grantPermissionManager(String address)：** 增加外部账号地址的管理权限的权限。
- **public String revokePermissionManager(String address)：** 移除外部账号地址的管理权限的权限。
- **public List\<AuthorityInfo\> listPermissionManager()：** 查询拥有管理权限的权限记录列表。
- **public String grantNodeManager(String address)：** 增加外部账号地址的节点管理权限。
- **public String revokeNodeManager(String address)：** 移除外部账号地址的节点管理权限。
- **public List\<AuthorityInfo\> listNodeManager()：** 查询拥有节点管理的权限记录列表。
- **public String grantCNSManager(String address)：** 增加外部账号地址的使用CNS权限。
- **public String revokeCNSManager(String address)：** 移除外部账号地址的使用CNS权限。
- **public List\<AuthorityInfo\> listCNSManager()：** 查询拥有使用CNS的权限记录列表。
- **public String addSysConfig(String address)：** 增加外部账号地址的系统参数管理权限。
- **public String removeSysConfig(String address)：** 移除外部账号地址的系统参数管理权限。
- **public List\<AuthorityInfo\> querySysConfig()：** 查询拥有系统参数管理的权限记录列表。

### CnsService
SDK提供对[CNS](../design/features/CNS_contract_name_service.md)的支持。CnsService可以配置CNS信息，其API如下：
- **String registerCns(String name, String version, String address, String abi)：** 根据合约名、合约版本号、合约地址和合约abi注册CNS信息。
- **String getAddressByContractNameAndVersion(String contractNameAndVersion)：** 根据合约名和合约版本号(合约名和合约版本号用英文冒号连接)查询合约地址。
- **List\<CnsInfo\> queryCnsByName(String name)：** 根据合约名查询CNS信息。
- **List\<CnsInfo\> queryCnsByNameAndVersion(String name, String version)：** 根据合约名和合约版本号查询CNS信息。

### SystemConfigSerivce
SDK提供对系统配置的支持。SystemConfigSerivce可以配置系统属性值（目前支持tx_count_limit和tx_gas_limit属性的设置），其API如下：
- **String setValueByKey(String key, String value)：** 根据键设置对应的值（查询键对应的值，参考Web3j API中的getSystemConfigByKey接口）。

### ConsensusService 
SDK提供对[节点类型](../design/security_control/node_access_management.md)配置的支持。ConsensusService可以设置节点类型，其API如下：
- **String addSealer(String nodeId)：** 根据节点NodeID设置对应节点为共识节点。
- **String addObserver(String nodeId)：** 根据节点NodeID设置对应节点为观察节点。
- **String removeNode(String nodeId)：** 根据节点NodeID设置对应节点为游离节点。
