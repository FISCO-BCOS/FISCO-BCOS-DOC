# Go API

标签：``go-sdk`` ``AMOP``

----

Go SDK为区块链应用开发者提供了Go API接口，以服务的形式供外部调用。按照功能，Go API可以分为如下几类：

- **client**：提供访问FISCO BCOS 2.0+节点[JSON-RPC](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html)接口支持、提供部署及调用合约的支持；
- **PrecompiledService**：Precompiled合约（预编译合约）是一种FISCO BCOS底层内嵌的、通过C++实现的高效智能合约，提供包括 [基于表的权限控制](https://fisco-bcos-doc-qiubing.readthedocs.io/en/latest/docs/manual/permission_control.html#id15)、[CNS](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/features/cns_contract_name_service.html)、系统属性配置、节点类型配置、用户表 CRUD、[基于角色的权限控制](https://fisco-bcos-doc-qiubing.readthedocs.io/en/latest/docs/manual/permission_control.html#id2)、合约生命周期权限控制等功能。PrecompiledService是调用这类功能的API的统称，分为：
  - **PermissionService**：提供对分布式权限控制的支持；
  - **CNSService**：提供对CNS的支持；
  - **SystemConfigService**：提供对系统配置的支持；
  - **ConsensusService**：提供对节点类型配置的支持；
  - **CRUDService**：提供对CRUD(增删改查)操作的支持，可以创建表或对表进行增删改查操作；
  - **ChainGovernanceService**：提供基于角色的权限控制支持；
  - **ContractLifeCycleService**：提供合约生命周期权限控制支持。

## Client

**位置**：go-sdk/client/go_client.go

**功能**：访问FISCO BCOS 2.0+节点[JSON-RPC](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html)

| 接口名                              | 描述                                                     | 参数                                                         |
| ----------------------------------- | -------------------------------------------------------- | ------------------------------------------------------------ |
| GetClientVersion                    | 获取区块链节点版本信息                                   | 无                                                           |
| GetBlockNumber                      | 获取最新块高                                             | 无                                                           |
| GetPbftView                         | 获取PBFT视图                                             | 无                                                           |
| GetBlockLimit                       | 获取最新区块高度限制                                     | 无                                                           |
| GetSealerList                       | 获取共识节点列表                                         | 无                                                           |
| GetObserverList                     | 获取观察者节点列表                                       | 无                                                           |
| GetConsensusStatus                  | 获取区块链节点共识状态                                   | 无                                                           |
| GetSyncStatus                       | 获取区块链节点同步状态                                   | 无                                                           |
| GetPeers                            | 获取区块链节点的连接信息                                 | 无                                                           |
| GetGroupPeers                       | 获取指定群组的共识节点和观察节点列表                     | 无                                                           |
| GetNodeIDList                       | 获取节点及其连接节点的列表                               | 无                                                           |
| GetGroupList                        | 获取节点所属群组的群组ID列表                             | 无                                                           |
| GetBlockByHash                      | 根据区块哈希获取区块信息                                 | 区块哈希 & bool                                              |
| GetBlockByNumber                    | 根据区块高度获取区块信息                                 | 区块高度 & bool                                              |
| GetBlockHashByNumber                | 根据区块高度获取区块哈希                                 | 区块高度                                                     |
| GetTransactionByHash                | 根据交易哈希获取交易信息                                 | 交易哈希                                                     |
| GetTransactionByBlockHashAndIndex   | 根据交易所属的区块哈希、 交易索引获取交易信息            | 交易所属的区块哈希 & 交易索引                                |
| GetTransactionByBlockNumberAndIndex | 根据交易所属的区块高度、 交易索引获取交易信息            | 交易所属的区块高度 & 交易索引                                |
| GetTransactionReceipt               | 根据交易哈希获取交易回执                                 | 交易哈希                                                     |
| GetContractAddress                  | 根据部署合约时产生的交易地址获取合约地址                 | 交易哈希                                                     |
| GetPendingTransactions              | 获取交易池内所有未上链的交易                             | 无                                                           |
| GetPendingTxSize                    | 获取交易池内未上链的交易数目                             | 无                                                           |
| GetCode                             | 根据合约地址查询合约数据                                 | 合约地址                                                     |
| GetTotalTransactionCount            | 获取指定群组的上链交易数目                               | 无                                                           |
| GetSystemConfigByKey                | 根据关键字获取区块链系统配置                             | 系统配置关键字，目前支持：<br/>\- tx_count_limit<br/>\- tx_gas_limit<br/>\- rpbft_epoch_sealer_num<br/>\- rpbft_epoch_block_num |
| Call                                | 调用只读合约                                             | 合约地址<br/>调用接口*<br/>参数列表                          |
| SendRawTransaction                  | 发送一个经过签名的交易，该交易随后会被链上节点执行并共识 | 群组ID & 已签名交易                                          |

```eval_rst
.. note::

    - 如果用户试图尝试使用一个 sdk 连接多个群组，可以利用 APIHandler 中暴露的接口，详细内容可阅读源码 `go-sdk <https://github.com/FISCO-BCOS/go-sdk>`_

```

## PermissionService

**位置**：go-sdk/precompiled/permission

**功能**：提供对基于表的权限控制支持

| 接口名                       | 描述                                                         | 参数                |
| ---------------------------- | ------------------------------------------------------------ | ------------------- |
| GrantUserTableManager        | 根据用户表名和外部账户地址设置权限信息                       | 表名 & 外部账户地址 |
| RevokeUserTableManager       | 根据用户表名和外部账户地址去除权限信息                       | 表名 & 外部账户地址 |
| ListUserTableManager         | 根据用户表名查询设置的权限记录列表(每条记录包含外部账户地址和生效块高) | 表名                |
| GrantContractWritePermission        | 根据合约地址和外部账户地址设置合约写权限信息                       | 合约地址 & 外部账户地址 |
| RevokeContractWritePermission       | 根据合约地址和外部账户地址去除合约写权限信息                       | 合约地址 & 外部账户地址 |
| ListContractWritePermission         | 根据合约地址查询拥有合约写权限的记录列表| 合约地址                |
| GrantDeployAndCreateManager  | 增加外部账户地址部署合约和创建用户表的权限                   | 外部账户地址        |
| RevokeDeployAndCreateManager | 移除外部账户地址部署合约和创建用户表的权限                   | 外部账户地址        |
| ListDeployAndCreateManager   | 查询拥有部署合约和创建用户表权限的记录列表               | 无                  |
| GrantPermissionManager       | 授予外部账户地址链管理员权限，链管理员可以使用权限分配功能                             | 外部账户地址        |
| RevokePermissionManager      | 撤销链外部账户地址链管理员权限                             | 外部账户地址        |
| ListPermissionManager        | 查询拥有链管理权限的记录列表                               | 无                  |
| GrantNodeManager             | 增加外部账户地址的节点管理权限                               | 外部账户地址        |
| RevokeNodeManager            | 移除外部账户地址的节点管理权限                               | 外部账户地址        |
| ListNodeManager              | 查询拥有节点管理的权限记录列表                               | 无                  |
| GrantCNSManager              | 增加外部账户地址使用CNS的权限                                | 外部账户地址        |
| RevokeCNSManager             | 移除外部账户地址使用CNS的权限                                | 外部账户地址        |
| ListCNSManager               | 查询拥有使用CNS权限的记录列表                                | 无                  |
| GrantSysConfigManager        | 增加外部账户地址的系统参数管理权限                           | 外部账户地址        |
| RevokeSysConfigManager       | 移除外部账户地址的系统参数管理权限                           | 外部账户地址        |
| ListSysConfigManager         | 查询拥有系统参数管理的权限记录列表                           | 无                  |

## CNSService

**位置**：go-sdk/precompiled/cns

**功能**：提供对CNS的支持

| 接口名                             | 描述                                                         | 参数                                      |
| ---------------------------------- | ------------------------------------------------------------ | ----------------------------------------- |
| RegisterCns                        | 根据合约名、合约版本号、合约地址和合约abi注册CNS信息         | 合约名 & 合约版本号 & 合约地址  & 合约abi |
| GetAddressByContractNameAndVersion | 根据合约名和合约版本号(合约名和合约版本号用英文冒号连接)查询合约地址。若缺失合约版本号，默认使用合约最新版本 | 合约名 + ':' + 版本号                     |
| QueryCnsByName                     | 根据合约名查询CNS信息                                        | 合约名                                    |
| QueryCnsByNameAndVersion           | 根据合约名和版本号查询CNS信息                                | 合约名 & 版本号                           |

## SystemConfigService

**位置**：go-sdk/precompiled/config

**功能**：提供修改系统配置项的功能，目前支持的配置项有 tx_count_limit、tx_gas_limit、rpbft_epoch_sealer_num 和 rpbft_epoch_block_num

| 接口名        | 描述                                                         | 参数      |
| ------------- | ------------------------------------------------------------ | --------- |
| SetValueByKey | 根据键设置对应的值（查询键对应的值，参考**ApiHandler**中的 getSystemConfigByKey 接口） | 键名 & 值 |

## ConsensusService

**位置**：go-sdk/precompiled/consensus

**功能**：提供对节点类型配置的支持

| 接口名      | 描述                                   | 参数   |
| ----------- | -------------------------------------- | ------ |
| AddSealer   | 根据节点NodeID设置对应节点为共识节点   | 节点ID |
| AddObserver | 根据节点NodeID设置对应节点为观察者节点 | 节点ID |
| RemoveNode  | 根据节点NodeID设置对应节点为游离节点   | 节点ID |

## CRUDService

**位置**：go-sdk/precompiled/crud

**功能**：提供对CRUD(增删改查)操作的支持

| 接口名      | 描述                 | 参数                                                         |
| ----------- | -------------------- | ------------------------------------------------------------ |
| CreateTable | 创建表               | 表名 & 主键字段名 & 其它字段名 |
| AsyncCreateTable | 异步创建表               | 表名 & 主键字段名 & 其它字段名<br/>handler：处理交易回执和 error 的函数 |
| Insert      | 插入记录             | 表名 & 主键字段名 <br/>Entry对象：Entry是map对象，提供插入的字段名和字段值 |
| AsyncInsert      | 异步插入记录             | 表名 & 主键字段名 <br/>Entry对象：Entry是map对象，提供插入的字段名和字段值<br/>handler：处理交易回执和 error 的函数 |
| Select      | 查询记录             | 表名 & 主键字段名<br/>Condtion对象：Condition对象是条件对象，可以设置查询的匹配条件 |
| Update      | 更新记录             | 表名 & 主键字段名<br/>Entry对象：Entry是map对象，提供待更新的字段名和字段值<br/>Condtion对象：Condition对象是条件对象，可以设置查询的匹配条件 |
| AsyncUpdate      | 异步更新记录             | 表名 & 主键字段名<br/>Entry对象：Entry是map对象，提供待更新的字段名和字段值<br/>Condtion对象：Condition对象是条件对象，可以设置查询的匹配条件<br/>handler：处理交易回执和 error 的函数 |
| Remove      | 移除记录             | 表名 & 主键字段名<br/>Condtion对象：Condition对象是条件对象，可以设置查询的匹配条件  |
| AsyncRemove      | 异步移除记录             | 表名 & 主键字段名<br/>Condtion对象：Condition对象是条件对象，可以设置查询的匹配条件<br/>handler：处理交易回执和 error 的函数  |
| Desc        | 根据表名查询表的信息 | 表名                                                         |

## ChainGovernanceService

**位置**：go-sdk/precompiled/chaingovernance

**功能**：提供对基于角色的权限控制支持

| 接口名      | 描述                 | 参数                                                         |
| ----------- | -------------------- | ------------------------------------------------------------ |
| GrantCommitteeMember | 	根据外部账户地址新增委员会成员               | 外部账户地址 |
| RevokeCommitteeMember      | 根据外部账户地址撤销委员会成员             | 外部账户地址 |
| ListCommitteeMembers      | 查询委员会成员列表             | 无 |
| QueryCommitteeMemberWeight      | 根据外部账户地址查询委员会成员投票权值             | 外部账户地址 |
| UpdateCommitteeMemberWeight      | 根据外部账户地址更新委员会成员投票权值             | 外部账户地址 & 权值（大于 0 的整数） |
| UpdateThreshold | 更新委员会全体委员投票时票数占比生效的阈值 | 阈值 [0, 99)           |
| QueryThreshold        | 查询委员会全体委员投票时票数占比生效的阈值 | 无       |
| GrantOperator | 	根据外部账户地址新增运维权限               | 外部账户地址 |
| RevokeOperator      | 根据外部账户地址撤销运维权限             | 外部账户地址 |
| ListOperators      | 查询全体运维成员列表             | 无 |
| FreezeAccount      | 根据外部账户地址冻结账户，该外部账号需要是部署过合约的账号             | 外部账户地址 |
| UnfreezeAccount      | 根据外部账户地址解冻合账户            | 外部账户地址 |
| GetAccountStatus | 根据外部账户地址查询账户状态 | 外部账户地址   |

## ContractLifeCycleService

**位置**：go-sdk/precompiled/contractlifecycle

**功能**：提供对合约生命周期操作的支持

| 接口名      | 描述                 | 参数                                                         |
| ----------- | -------------------- | ------------------------------------------------------------ |
| Freeze | 根据合约地址冻结合约              | 合约地址 |
| Unfreeze      | 根据合约地址解冻合约             | 合约地址 |
| GrantManager      | 根据合约地址和外部账户地址授予账户合约管理权限             | 合约地址 & 外部账户地址 |
| GetStatus      | 根据合约地址查询合约状态             | 合约地址 |
| ListManager      | 查询拥有合约地址管理权限的外部账号             | 合约地址  |