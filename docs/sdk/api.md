# Java SDK API

Java SDK API主要分为Web3j API和Precompiled Service API。其中Web3j API可以查询区块链相关的状态，发送和查询交易信息；Precompiled Service API可以管理区块链相关配置以及实现特定功能。

## 1.1 Web3j API
Web3j API是由web3j对象调用的FISCO BCOS的RPC API，其API名称与RPC API相同，参考[RPC API文档](../api.md)。调用Web3j API示例参考[Java SDK配置与使用](./config.md)。

## 1.2 Precompiled Service API
Precompiled合约是FISCO BCOS底层通过C++实现的一种高效智能合约。sdk已提供Precompiled合约对应的Java接口，控制台通过调用这些Java接口实现了相关的操作命令，体验控制台，参考[控制台手册](../manual/console.md)。sdk提供Precompiled对应的Service类，分别是分布式控制权限相关的AuthorityService，[CNS](../design/features/CNS_contract_name_service.md)相关的CnsService，系统属性配置相关的SystemConfigService和节点类型配置相关ConsensusService。调用Precompiled Service API示例参考[Java SDK配置与使用](./config.md)。

### 1.2.1 AuthorityService
sdk提供对[分布式控制权限](../manual/priority_control.md)的支持，AuthorityService可以配置权限信息，其API如下：
- String add(String tableName, String addr)：根据表名和外部账号地址设置权限信息。
- String remove(String tableName, String addr)：根据表名和外部账号地址去除权限信息。
- List<AuthorityInfo> query(String tableName)：根据表名查询设置的权限信息。

### 1.2.2 CnsService
sdk提供对[CNS](../design/features/CNS_contract_name_service.md)的支持。CnsService可以配置CNS信息，其API如下：
- String registerCns(String name, String version, String addr, String abi)：根据合约名、合约版本号、合约地址和合约abi注册CNS信息。
- String getAddressByContractNameAndVersion(String contractNameAndVersion)：根据合约名和合约版本号(合约名和合约版本号用英文冒号连接)查询合约地址。
- List<CnsInfo> queryCnsByName(String name)：根据合约名查询CNS信息。
- List<CnsInfo> queryCnsByNameAndVersion(String name, String version)：根据合约名和合约版本号查询CNS信息。

### 1.2.3 SystemConfigSerivce
sdk提供对系统配置的支持。SystemConfigSerivce可以配置系统属性值（目前支持tx_count_limit和tx_gas_limit属性的设置），其API如下：
- String setValueByKey(String key, String value)：根据键设置对应的值（查询键对应的值，参考Web3j API中的getSystemConfigByKey接口）。

### 1.2.4 ConsensusService 
sdk提供对[节点类型](../design/security_control/node_access_management.md)配置的支持。ConsensusService可以设置节点类型，其API如下：
- String addMiner(String nodeId)：根据节点NodeID设置对应节点为共识节点。
- String addObserver(String nodeId)：根据节点NodeID设置对应节点为观察节点。
- String removeNode(String nodeId)：根据节点NodeID设置对应节点为游离节点。