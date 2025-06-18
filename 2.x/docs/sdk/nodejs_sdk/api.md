# Node.js API

标签：``java-sdk`` ``Client`` 

----
Node.js SDK为区块链应用开发者提供了Node.js API接口，以服务的形式供外部调用。按照功能，Node.js API可以分为如下几类：

- **Web3jService**：提供访问FISCO BCOS 2.0+节点[JSON-RPC](../../api.md)接口支持；提供部署及调用合约的支持。
- **PrecompiledService**：

    Precompiled合约（预编译合约）是一种FISCO BCOS底层内嵌的、通过C++实现的高效智能合约，提供包括[分布式权限控制](../../design/security_control/permission_control.md)、[CNS](../../design/features/cns_contract_name_service.md)、系统属性配置、节点类型配置等功能。PrecompiledService是调用这类功能的API的统称，分为：

  - **PermissionService**：提供对分布式权限控制的支持
  - **CNSService**：提供对CNS的支持
  - **SystemConfigService**：提供对系统配置的支持
  - **ConsensusService**：提供对节点类型配置的支持
  - **CRUDService**：提供对CRUD(增删改查)操作的支持，可以创建表或对表进行增删改查操作。

## API调用约定

- 使用服务之前，首先需要初始化全局的`Configuration`对象，用以为各个服务提供必要的配置信息。`Configuration`对象位于`nodejs-sdk/packages/api/common/configuration.js`，其初始化参数为一个配置文件的路径或包含配置项的对象。配置文件的配置项说明见[配置说明](./configuration.md)
- 如无特殊说明，Node.js SDK提供的API均为**异步**API。异步API的实际返回值是一个包裹了API返回值的[Promise](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise)对象，开发者可以使用[async/await语法](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/await)或[then...catch...finally方法](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/then)操作该Promise对象以实现自己的程序逻辑
- 当API内部出现错误导致逻辑无法继续执行时（如合约地址不存在），均会直接抛出异常，所有异常均继承自Error类

## Web3jService

**位置**：`nodejs-sdk/packages/api/web3j`

**功能**：访问FISCO BCOS 2.0+节点[JSON-RPC](../../api.md)；部署合约；调用合约

| 接口名        | 描述| 参数 | 返回值 |
| :--| :--| :-- | :-- |
| getBlockNumber  | 获取最新块高  | 无 | Object，结果位于result字段\*\* |
| getPbftView  | 获取PBFT视图  | 无 | 同上 |
| getObserverList | 获取观察者节点列表  | 无 | 同上 |
| getSealerList | 获取共识节点列表  | 无 | 同上 |
| getConsensusStatus | 获取区块链节点共识状态 | 无 | 同上 |
| getSyncStatus |  获取区块链节点同步状态 | 无 | 同上 |
| getClientVersion | 获取区块链节点版本信息 | 无 | 同上 |
| getPeers |  获取区块链节点的连接信息 | 无 | 同上 |
| getNodeIDList |  获取节点及其连接节点的列表 | 无 | 同上 |
| getGroupPeers | 获取指定群组的共识节点<br>和观察节点列表| 同上  |
| getGroupList |  获取节点所属群组的群组ID列表 |  无 | 同上 |
| getBlockByHash |  根据区块哈希获取区块信息 | 区块哈希 | 同上 |
| getBlockByNumber |  根据区块高度获取区块信息 | 区块高度 | 同上 |
| getBlockHashByNumber | 根据区块高度获取区块哈希 | 区块高度 | 同上 |
| getTransactionByHash | 根据交易哈希获取交易信息  | 交易哈希| 同上 |
| getTransactionByBlockHashAndIndex | 根据交易所属的区块哈希、<br>交易索引获取交易信息 | 交易所属的区块哈希<br>交易索引 | 同上 |
| getTransactionByBlockNumberAndIndex | 根据交易所属的区块高度、<br>交易索引获取交易信息 | 交易所属的区块高度<br>交易索引 | 同上 |
| getPendingTransactions | 获取交易池内所有未上链的交易  | 无 | 同上 |
| getPendingTxSize |  获取交易池内未上链的交易数目 | 无 | 同上 |  
| getTotalTransactionCount | 获取指定群组的上链交易数目  | 无 | 同上 |
| getTransactionReceipt | 根据交易哈希获取交易回执  | 交易哈希 | 同上 |
| getCode | 根据合约地址查询的合约数据 | 合约地址 | 同上 |
| getSystemConfigByKey |  获取系统配置 | 系统配置关键字，目前支持<br>：<br> - tx_count_limit <br> - tx_gas_limit| 同上 |
| sendRawTransaction | 发送一个经过签名的交易，该交易随后会被链上节点执行并共识 | 接受数量可变的参数：当参数数量为1时，参数应为交易的RLP编码；当参数数量为3时，参数应为合约地址、方法签名及方法参数  | 同上 |
| deploy |  部署合约  | 合约路径<br>输出路径| 同上 |
| call | 调用只读合约  | 合约地址<br>调用接口\*<br>参数列表 | 同上 |

\*调用接口：函数名(参数类型,...)，例如：func(uint256,uint256)，参数类型之间不能有空格

## PrecompiledService

### PermissionService

**位置**：`nodejs-sdk/packages/api/precompiled/permission`

**功能**：提供对分布式权限控制的支持

| 接口名        | 描述| 参数 | 返回值 |
| :--| :--| :-- | :-- |
| grantUserTableManager | 根据用户表名和外部账户地址设置权限信息 | 表名<br>外部账户地址 | Number，表示成功改写权限表的行数 |
| revokeUserTableManager | 根据用户表名和外部账户地址去除权限信息 | 表名<br>外部账户地址 | Number，表示成功改写权限表的行数 |
| listUserTableManager | 根据用户表名查询设置的权限记录列表(每条记录包含外部账户地址和生效块高) | 表名 | Array，查询到的记录 |
| grantDeployAndCreateManager | 增加外部账户地址的部署合约和创建用户表权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| revokeDeployAndCreateManager | 移除外部账户地址的部署合约和创建用户表权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| listDeployAndCreateManager | 查询拥有部署合约和创建用户表权限的权限记录列表 | 无 | Array，查询到的记录 |
| grantPermissionManager | 增加外部账户地址的管理权限的权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| revokePermissionManager | 移除外部账户地址的管理权限的权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| listPermissionManager | 查询拥有管理权限的权限记录列表 | 无 | Array，查询到的记录 |
| grantNodeManager | 增加外部账户地址的节点管理权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| revokeNodeManager |  移除外部账户地址的节点管理权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| listNodeManager | 查询拥有节点管理的权限记录列表 | 无 | Array，查询到的记录 |
| grantCNSManager | 增加外部账户地址的使用CNS权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| revokeCNSManager | 移除外部账户地址的使用CNS权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| listCNSManager | 查询拥有使用CNS的权限记录列表 | 无 | Array，查询到的记录 |
| grantSysConfigManager | 增加外部账户地址的系统参数管理权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| revokeSysConfigManager | 移除外部账户地址的系统参数管理权限 | 外部账户地址 | Number，表示成功改写权限表的行数 |
| listSysConfigManager | 查询拥有系统参数管理的权限记录列表 | 无 | Array，查询到的记录 |

### CNSService

**位置**：`nodejs-sdk/packages/api/precompiled/cns`

**功能**：提供对节点类型配置的支持

| 接口名        | 描述| 参数 | 返回值 |
| :--| :--| :-- | :-- |
| registerCns | 根据合约名、合约版本号、合约地址和合约abi注册CNS信息 | 合约名<br>合约版本号<br>合约地址<br>合约abi |  Number，表示成功增加的CNS条目记录数 |
| getAddressByContractNameAndVersion | 根据合约名和合约版本号(合约名和合约版本号用英文冒号连接)查询合约地址。若缺失合约版本号，默认使用合约最新版本 | 合约名 + ':' + 版本号 | Object，查询到的CNS信息 |
| queryCnsByName | 根据合约名查询CNS信息 | 合约名 | Array，查询到的CNS信息 |
| queryCnsByNameAndVersion | 根据合约名和合约版本号查询CNS信息 | 合约名<br>版本号 | 同上 |

### SystemConfigService

**位置**：`nodejs-sdk/packages/api/precompiled/systemConfig`

**功能**：提供对系统配置的支持

| 接口名        | 描述| 参数 | 返回值 |
| :--| :--| :-- | :-- |
| setValueByKey | 根据键设置对应的值（查询键对应的值，参考**Web3jService**中的`getSystemConfigByKey`接口） | 键名<br>值 | Number，表示成功修改的系统配置的数目 |

### ConsensusService

**位置**：`nodejs-sdk/packages/api/precompiled/consensus`

**功能**：提供对节点类型配置的支持

| 接口名        | 描述| 参数 | 返回值 |
| :--| :--| :-- | :-- |
| addSealer | 根据节点NodeID设置对应节点为共识节点 | 节点ID | Number，表示成功增加的共识节点数目 |
| addObserver | 根据节点NodeID设置对应节点为观察者节点| 节点ID | Number，表示成功增加的观察数目 |
| removeNode |  根据节点NodeID设置对应节点为游离节点 | 节点ID | Number，表示成功增加的游离数目 |

### CRUDService

**位置**：`nodejs-sdk/packages/api/precompiled/crud`

**功能**：提供对CRUD(增删改查)操作的支持

| 接口名        | 描述| 参数 | 返回值 |
| :--| :--| :-- | :-- |
| createTable |  创建表 | 表对象<br>表对象需要设置其表名，主键字段名和其他字段名。其中，其他字段名是以英文逗号分隔拼接的字符串 | Number，状态码，0代表创建成功 |
| insert |  插入记录 | 表对象 Entry对象<br>表对象需要设置表名和主键字段名；Entry是map对象，提供插入的字段名和字段值，注意必须设置主键字段 | Number，表示插入的记录数
| update | 更新记录 | 表对象 Entry对象 Condtion对象<br>表对象需要设置表名和主键字段名；Condition对象是条件对象，可以设置查询的匹配条件 | Array，查询到的记录
| remove | 移除记录 | 表对象 条件对象<br>| 表对象需要设置表名和主键字段名；Condition对象是条件对象，可以设置移除的匹配条件 | Number，成功移除的记录数 |
| Select      | 查询记录             | 表对象：表对象需要设置表名和主键字段值<br/>Condtion对象：Condition对象是条件对象，可以设置查询的匹配条件 | Number，成功查询的记录数 |
| desc | 根据表名查询表的信息 | 表名 | Object，主要包含表的主键字段名和其他属性字段 |
