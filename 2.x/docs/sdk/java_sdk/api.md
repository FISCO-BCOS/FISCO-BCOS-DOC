# 远程调用接口

标签：``java-sdk`` ``Client`` 

----
Java SDK为区块链应用开发者提供了Java API接口，按照功能，Java API可分为如下几类：

- Client: 提供访问FISCO BCOS 2.0+节点JSON-RPC接口支持、提供部署及调用合约的支持；
- Precompiled: 提供调用FISCO BCOS 2.0+ Precompiled合约(预编译合约)的接口，主要包括`ConsensusService`、`SystemConfigService`、`PermissionService`、`ChainGovernanceService`、`TableCRUDService`、`ContractLifeCycleService`、`CNSService`。

```eval_rst
.. note::
    - Client接口声明位于 `Client.java` 文件中
    - Client是群组维度的对象，可参考 `快速入门 <quick_start.html>`_ 初始化Client，初始化Client时，必须传入群组ID
```

### sendRawTransaction
发送交易到区块链节点。
#### **参数**
- signedTransactionData：签名后的交易
#### **返回值**
- SendTransaction: 节点收到交易后，回复给SDK的回包，包括交易哈希信息。


### sendRawTransactionAsync
交易发布异步接口, 收到节点的响应之后，调用指定的callback。
#### **参数**
- signedTransactionData: 签名后的交易字符串;
- callback: SDK收到节点的回包后，调用的回调函数。
#### **返回值**
- 无

### sendRawTransactionAndGetProof

向区块链节点发送交易，且声明在交易推送时交易回执中包含交易Merkle证明和交易回执Merkle证明。

#### **参数**

- signedTransactionData: 签名后的交易。

#### **返回值**

- SendTransaction: 节点收到交易后，回复给SDK的回包，包括交易哈希信息。

### sendRawTransactionAndGetProofAsync

`sendRawTransactionAndGetProof`异步接口，向节点发送交易，并在收到节点回报时，调用回调函数。

#### **参数**

- signedTransactionData: SDK发送到节点的签名后交易；
- callback: SDK收到节点回包后，调用的回调函数。

#### **返回值**

- 无

### sendRawTransactionAndGetReceipt

交易发送同步接口，发送交易并获取交易回执。

#### **参数**

- signedTransactionData: 组装好的交易字符串，包含签名信息。

#### **返回值**

- TransactionReceipt: 交易回执。

### sendRawTransactionAndGetReceiptAsync

交易发送异步接口，发送交易，并在收到交易回执时调用指定的回调函数。

#### **参数**

- signedTransactionData: 组装好的交易字符串，包含签名信息；
- callback: 收到交易回执后调用的回调函数。

#### **返回值**

- 无。


### sendRawTransactionAndGetReceiptWithProof

交易发送同步接口，发送交易，并获取带有交易Merkle证明和交易回执Merkle证明的交易回执。

#### **参数**

- signedTransactionData: 组装好的交易字符串，包含签名信息；

#### **返回值**

- TransactionReceipt: 带有交易Merkle证明和交易回执Merkle证明的交易回执。

### sendRawTransactionAndGetReceiptWithProofAsync

交易发送异步接口，发送交易，并在收到交易回执时，调用指定的回调函数(交易回执中包含交易Merkle证明和交易回执的Merkle证明)。

#### **参数**

- signedTransactionData: 组装好的交易字符串，包含签名信息；
- callback: 收到交易回执后调用的回调函数。

#### **返回值**

- 无。

### call
向节点发送请求，调用合约常量接口。
#### **参数**
- transaction: 合约调用信息，包含合约地址、合约调用者以及调用的合约接口和参数的abi编码
#### **返回值**
- Call: 合约常量接口的返回结果，包括当前块高、接口执行状态信息以及接口执行结果

### callAsync
合约常量接口异步调用，接收到节点返回的合约接口执行结果后，执行指定的回调函数
#### **参数**
- transaction: 合约调用信息，包含合约地址、合约调用者以及调用的接口和参数信息；
- callback: 回调函数。

#### **返回值**
- 无

### getBlockNumber
获取Client对象对应的群组最新块高。

#### **参数**
- 无

#### **返回值**
- BlockNumber: Client对象对应的群组最新区块高度。


### getCode
查询指定合约地址对应的合约代码信息。

#### **参数**
- address: 合约地址。

#### **返回值**
- Code: 合约地址对应的合约代码。

### getTotalTransactionCount
获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目。

#### **参数**
- 无

#### **返回值**
- TotalTransactionCount: 交易统计信息，包括：
    - txSum: 上链的交易总量
    - blockNumber: 群组的当前区块高度
    - failedTxSum: 上链执行异常的交易总量

### getBlockByHash
根据区块哈希获取区块信息。

#### **参数**
- blockHash: 区块哈希；
- returnFullTransactionObjects: true/false，表明获取的区块信息中是否包含完整的交易信息；
    - true: 节点返回的区块中包含完整的交易信息；
    - false: 节点返回的区块中仅包含交易哈希。

#### **返回值**
- BcosBlock: 查询获取的区块信息。

### getBlockByNumber
根据区块高度获取区块信息。

#### **参数**
- blockNumber: 区块高度；
- returnFullTransactionObjects: true/false，表明获取的区块信息中是否包含完整的交易信息；
    - true: 节点返回的区块中包含完整的交易信息；
    - false: 节点返回的区块中仅包含交易哈希。

#### **返回值**
- BcosBlock: 查询获取的区块信息

### getBlockHashByNumber
根据区块高度获取区块哈希。
#### **参数**
- blockNumber: 区块高度。
#### **返回值**
- BlockHash: 指定区块高度对应的区块哈希

### getBlockHeaderByHash
根据区块哈希获取区块头信息。
#### **参数**
- blockHash: 区块哈希；
- returnSignatureList: true/false，表明返回的区块头中是否附带签名列表信息
    - true: 返回的区块头中带有区块签名列表信息；
    - false: 返回的区块头中不带区块签名列表信息。
#### **返回值**
- BcosBlockHeader: 指定区块哈希对应的区块头。


### getBlockHeaderByNumber
根据区块高度获取区块头信息。
#### **参数**
- blockHash: 区块高度；
- returnSignatureList: true/false，表明返回的区块头中是否附带签名列表信息
    - true: 返回的区块头中带有区块签名列表信息；
    - false: 返回的区块头中不带区块签名列表信息。
#### **返回值**
- BcosBlockHeader: 指定区块高度对应的区块头。


### getTransactionByHash
根据交易哈希获取交易信息。
#### **参数**
- transactionHash: 交易哈希
#### **返回值**
- BcosTransaction: 指定哈希对应的交易信息。

### getTransactionByHashWithProof
根据交易哈希获取交易信息，交易信息中带有交易Merkle证明。
#### **参数**
- transactionHash: 交易哈希
#### **返回值**
- BcosTransaction: 指定哈希对应的交易信息。

### getTransactionByBlockNumberAndIndex
根据区块高度和交易索引获取交易信息。
#### **参数**
- blockNumber: 交易所在的区块高度；
- transactionIndex: 交易索引。

#### **返回值**
- BcosTransaction: 指定区块高度和交易索引对应的交易信息。

### getTransactionByBlockHashAndIndex
根据区块哈希和交易索引获取交易信息。
#### **参数**
- blockHash: 交易所在的区块哈希；
- transactionIndex:交易索引。
#### **返回值**
- BcosTransaction: 指定区块哈希和交易索引对应的交易信息。


### getTransactionReceipt
根据交易哈希获取交易回执信息。

#### **参数**
- transactionHash: 交易哈希

#### **返回值**
- BcosTransactionReceipt: 交易哈希对应的回执信息。

### getTransactionReceiptByHashWithProof
根据交易哈希获取交易回执信息，回执中带有Merkle证明。

#### **参数**
- transactionHash: 交易哈希。
#### **返回值**
- TransactionReceiptWithProof: 带有Merkle证明的交易回执信息。

### getPendingTransaction
获取交易池内待处理的交易列表。

#### **参数**
- 无

#### **返回值**
- PendingTransactions: 交易池内未处理的交易列表。

### getPendingTxSize
获取交易池内未处理的交易数目。
#### **参数**
- 无
#### **返回值**
- PendingTxSize: 交易池内未处理的交易数目。

### getBlockLimit
获取Client对应群组的BlockLimit，BlockLimit主要用于交易防重。

#### **参数**
- 无

#### **返回值**
- BigInteger: 群组的BlockLimit。
    /**
     * Get cached block height
     *
     * @return block number
     */
    BigInteger getBlockLimit();


### generateGroup
为指定节点动态创建一个新群组。
#### **参数**
- groupId: 创建的群组ID；
- timestamp: 新创建的群组创世块时间戳；
- enableFreeStorage: 新创建群组是否使用`enable_free_storage`的gas计算模式，推荐设置为`false`;
- nodeList: 新创建群组的共识节点NodeID列表；
- peerIpPort: 创建该群组的节点`IP:Port`信息。

#### **返回值**
- GenerateGroup: 群组创建结果。
  
### startGroup
启动指定节点的指定群组。

#### **参数**
- groupId: 需要启动的群组ID;
- peerIpPort: 启动指定群组的节点`IP:Port`信息。
#### **返回值**
- StartGroup: 群组启动状态。

### stopGroup
停止指定节点的指定群组。
#### **参数**
- groupId: 需要停止的群组ID;
- peerIpPort: 群组所在的节点`IP:Port`信息。
#### **返回值**
- StopGroup: 被停止的群组状态。

### removeGroup
删除指定节点的指定群组。
#### **参数**
- groupId: 被删除的群组信息；
- peerIpPort: 群组所在的节点`IP:Port`信息。

#### **返回值**
- RemoveGroup: 被删除的群组状态。

### recoverGroup
恢复指定节点被删除的群组。

#### **参数**
- groupId: 需要恢复的群组ID；
- peerIpPort: 群组所在的节点`IP:Port`信息。
#### **返回值**
- RecoverGroup: 被恢复的群组状态。

### queryGroupStatus
查询指定群组的状态。
#### **参数**
- groupId: 被查询的群组ID;
- peerIpPort: 群组状态查询发送到的目标节点信息，包括`IP:Port`信息。

#### **返回值**
- QueryGroupStatus: 被查询的群组状态。

### getGroupList
获取指定节点的群组列表。
#### **参数**
- peerIpPort: 被查询的节点的`IP:Port`。
#### **返回值**
- GroupList: 指定节点的群组列表。


### getGroupPeers
获取指定节点指定群组连接的节点列表。

#### **参数**
- peerIpPort: 被查询的节点的`IP:Port`。
#### **返回值**
- GroupPeers: 指定群组连接的节点列表。
  
### getPeers
获取指定节点的网络连接信息。
#### **参数**
- endpoint: 被查询的节点的`IP:Port`。
#### **返回值**
- Peers: 指定节点的网络连接信息。

### getNodeIDList
获取指定节点连接的节点列表。
#### **参数**
- endpoint: 被查询的节点的`IP:Port`。
#### **返回值**
- NodeIDList: 指定节点连接的节点列表。

### getObserverList
获取Client对应群组的观察节点列表。
#### **参数**
- 无
#### **返回值**
- ObserverList: 观察节点列表。

### getSealerList
获取Client对应群组的共识节点列表。
#### **参数**
- 无
#### **返回值**
- SealerList: 共识节点列表。

### getPbftView
节点使用PBFT共识算法时，获取PBFT视图信息。
#### **参数**
- 无
#### **返回值**
- PbftView: PBFT视图信息。

### getNodeVersion
获取节点版本信息。
#### **参数**
- ipAndPort: 请求发送的目标节点，包括`IP:Port`信息。
#### **返回值**
- NodeVersion: 查询获取的节点版本信息。

### getNodeInfo

获取节点的NodeID，Topic等信息。

#### **参数**

- ipAndPort: 请求发送的目标节点，包括`IP:Port`信息。

#### **返回值**

- NodeInfo: 查询获取的节点版本信息。


### getConsensusStatus
获取节点共识状态。
#### **参数**
- 无
#### **返回值**
- ConsensusStatus: 节点共识状态。

### getSystemConfigByKey
根据指定配置关键字获取系统配置项的值。
#### **参数**
- key: 系统配置项，目前包括`tx_count_limit`, `tx_gas_limit`, `rpbft_epoch_sealer_num`, `rpbft_epoch_block_num`和`consensus_timeout`.

#### **返回值**
- SystemConfig: 系统配置项的值。
  

### getSyncStatus
获取节点同步状态。
#### **参数**
- 无
#### **返回值**
- SyncStatus: 区块链节点同步状态。


### ConsensusService

#### addSealer
 将指定节点添加为共识节点。

**参数**
- nodeId: 被添加为共识节点的node ID.

**返回值**
- RetCode: 共识节点添加结果。

```eval_rst
.. note::
    为了保证新节点加入不影响共识，即将被添加为共识节点的节点必须与群组内其他节点建立P2P网络连接，否则其无法被添加为共识节点。
```

#### addObserver
将指定节点添加为观察节点。

**参数**
- nodeId: 被添加为观察节点的node ID.

**返回值**
- RetCode: 观察节点添加结果。

#### removeNode
将指定节点移出群组。

**参数**
- nodeId: 被移出群组的节点的node ID.

**返回值**
- RetCode: 节点被移出群组的执行结果。

### SystemConfigService

#### setValueByKey
设置指定系统配置项的值。

**参数**
- key: 配置项，目前支持`tx_count_limit`, `tx_gas_limit`, `rpbft_epoch_block_num`, `rpbft_epoch_sealer_num`和`consensus_time`；

- value: 系统配置项被设置的值。

**返回值**
- RetCode: 系统配置项设置结果。


### PermissionService

#### grantPermission
为指定用户添加对指定表的写权限。

**参数**
- tableName: 授权访问的表名;
- userAddress: 被授权对指定表写操作权限的账户地址。

**返回值**
- RetCode: 授权结果。

#### revokePermission
撤销指定用户对指定表的写权限。

**参数**
- tableName: 撤销写访问权限的表名；
- userAddress: 被撤销对指定表写访问权限的账户地址。

**返回值**
- RetCode: 权限撤销结果。

#### queryPermission
查询对指定合约有权限的账户信息。

**参数**
- contractAddress: 被查询的合约地址。

**返回值**

- List<PermissionInfo>: 拥有对指定合约写权限的账户信息列表。

#### grantWrite
授权指定用户对指定合约的写权限。

**参数**
- contractAddress: 授权写访问的合约地址;
- userAddress: 被授权对指定合约写访问权限的账户地址。

**返回值**
- RetCode: 授权结果。
  
#### revokeWrite
撤销指定用户对指定合约的写访问权限。

**参数**
- contractAddress: 撤销写访问权限的合约地址;
- userAddress: 被撤销对指定合约写访问权限的账户地址。

**返回值**
- RetCode: 合约写访问权限撤销结果。
  
#### queryPermissionByTableName
查询对指定表有写权限的账户地址信息。

**参数**
- tableName: 查询权限信息的表名。

**返回值**
- List<PermissionInfo>: 拥有对指定表写访问权限的账户信息列表。
  
#### grantDeployAndCreateManager
授权指定用户部署和调用合约的权限。

**参数**
- userAddress: 被授权部署和调用合约权限的账户地址。

**返回值**
- RetCode: 授权结果。
  
#### revokeDeployAndCreateManager
撤销指定账户部署和调用合约的权限。

**参数**
- userAddress: 被撤销部署和调用合约权限的账户地址。

**返回值**
- RetCode: 指定用户合约部署和调用权限的撤销结果。
  
#### listDeployAndCreateManager
列出拥有部署和调用合约权限的账户信息。

**参数**
- 无

**返回值**
- 拥有部署和调用合约权限的账户信息列表。

#### grantPermissionManager
授予指定用户权限控制的权限，被授权的账户可配置普通账户的访问权限。

**参数**
- userAddress: 被授予权限控制权限的账户地址。

**返回值**
- RetCode: 授权结果
  
#### revokePermissionManager
撤销指定用户权限控制的权限，被撤销的账户不可配置普通账户的对合约、表的访问权限。

**参数**
- userAddress: 被撤销权限的账户地址。

**返回值**
- RetCode: 权限撤销结果。

#### listPermissionManager
列出拥有权限配置权力的账户信息。

**参数**
- 无

**返回值**
- List<PermissionInfo>: 拥有权限控制权限的账户信息列表。

#### grantNodeManager
授权指定用户操作系统共识表的权限，被授权用户可增加共识节点、增加观察者节点、删除节点。

**参数**
- userAddress: 被授权的账户地址。

**返回值**
- RetCode: 授权结果。
  
#### revokeNodeManager
撤销指定用户对系统共识表的写访问权限，撤销权限的账户无法进行增加共识节点、增加观察者节点、删除节点等操作。

**参数**
- userAddress: 被撤销的账户地址。

**返回值**
- RetCode: 系统够共识表写访问权限撤销结果。
  
#### listNodeManager
列出拥有增加共识节点、增加观察者节点、删除节点等操作权限的账户信息。

**参数**
- 无

**返回值**
- List<PermissionInfo>: 有增加共识节点、增加观察者节点、删除节点等操作权限的账户信息。

#### grantCNSManager
授权指定账户操作CNS表的权限。

**参数**
- userAddress: 被授权账户地址。

**返回值**
- RetCode: 授权结果。

#### revokeCNSManager
撤销指定账户对CNS表的写权限。

**参数**
- userAddress: 被撤销的账户地址。

**返回值**
- RetCode: 撤销结果。
  
#### listCNSManager
列出对CNS表有写权限的账户信息。

**参数**
- 无

**返回值**
- List<PermissionInfo>: 对CNS表有写权限的账户列表。

#### grantSysConfigManager
授权指定用户设置系统配置项的权限。

**参数**
- userAddress: 被授权账户地址。

**返回值**
- RetCode: 授权结果。
  
#### revokeSysConfigManager
撤销指定用户设置系统配置项的权限。

**参数**
- userAddress: 被撤销账户地址。

**返回值**
- RetCode: 权限撤销结果。
  
#### listSysConfigManager
列出有修改系统配置项权限的账户信息。

**参数**
- 无

**返回值**
- List<PermissionInfo>: 有修改系统配置项权限的账户信息。

### ChainGovernanceService

#### grantCommitteeMember
授权指定用户为链管理员

**参数**
- userAddress: 被授权为链管理员的账户地址。

**返回值**
- RetCode: 授权结果。

    
#### revokeCommitteeMember
撤销指定链管理员为普通账户。

**参数**
- userAddress: 被撤销的链管理员账户地址。

**返回值**
- RetCode: 撤销结果。
  
#### listCommitteeMembers
列出当前链管理员信息。

**参数**
- 无

**返回值**
- List<PermissionInfo>: 所有链管理员的信息。

#### updateCommitteeMemberWeight
修改指定链管理员的投票权重。

**参数**
- userAddress: 更新投票权重的链管理员账户地址;
- weight: 更新后的投票权重。

**返回值**
- RetCode: 连管理员投票权重更新结果。
  
#### updateThreshold
修改投票生效阈值。

**参数**
- threshold: 更新后的投票生效阈值。

**返回值**
- RetCode: 投票生效阈值更新结果。
  
#### queryThreshold
查询当前的投票生效阈值。

**参数**
- 无

**返回值**
- BigInteger: 当前投票生效阈值。
  

#### queryCommitteeMemberWeight
查询指定链管理员的投票权重。

**参数**
- userAddress: 被查询投票权重的链管理员账户地址。

**返回值**
- BigInteger: 查询到的指定链管理员投票权重。
  
#### grantOperator
将指定账户授权为运维管理员。

**参数**
- userAddress: 被授权为运维管理员的账户地址。

**返回值**
- RetCode: 授权结果。
  
#### revokeOperator
撤销指定运维管理员的运维权限。

**参数**
- userAddress: 被撤销的运维管理员账户地址。

**返回值**
- RetCode: 运维权限撤销结果。
  
#### listOperators
列出当前群组的运维管理员信息。

**参数**
- 无

**返回值**
- List<PermissionInfo>: 运维管理员列表。


#### queryVotesOfMember

查询指定账户被选举为委员账户的投票情况

**参数**
- `account`: 被查询的账户地址

**返回值**
- `String`: 返回该账户被选举为委员账户的投票情况；若没有任何委员选举该账户，则返回null字符串

#### queryVotesOfThreshold

查询updateThreshold的投票情况

**参数**
- 无

**返回值**
- String: updateThreshold的投票情况，当没有任何委员更新threshold时，返回null字符串。
  
#### freezeAccount
冻结指定账户，被冻结的账户不能部署和调用合约。

**参数**
- userAddress: 被冻结的账户地址。

**返回值**
- RetCode: 账户冻结结果。
  
#### unfreezeAccount
解冻被冻结的账户，解冻后的账户可以部署和调用合约。

**参数**
- userAddress: 解冻的账户地址。

**返回值**
- RetCode: 账户解冻结果。
  
#### getAccountStatus
获取指定账户的状态。

**参数**
- userAddress: 账户地址。

**返回值**
- String: 指定账户的状态。
  

### TableCRUDService

#### createTable
创建用户表。

**参数**
- tableName: 创建的用户表名;
- keyFieldName: 用户表的主key名;
- valueFields: 用户表的fields.

**返回值**
- RetCode: 用户表创建结果。

#### insert
向指定用户表中插入一条记录。

**参数**
- tableName: 需要插入记录的表名;
- key: 主key被设置的值;
- fieldNameToValue: 每个field到其对应值的映射。

**返回值**
- RetCode: 记录是否插入成功。

#### update

更新指定用户表中，将指定主key对应的记录更新为传入的记录。

**参数**
- tableName: 用户表名;
- key: 需要更新的记录对应的主key值;
- fieldNameToValue: 更新后的记录;
- condition: 记录更新条件。

**返回值**
- RetCode: 记录是否更新成功。
  

#### remove
删除用户表指定记录。

**参数**
- tableName: 用户表名;
- key: 被删除的记录主key值;
- condition: 被删除记录的匹配条件。

**返回值**
- RetCode: 记录删除结果。

#### select
查询用户表指定记录。

**参数**
- tableName: 被查询的用户表名;
- key: 被查询的主key值;
- condition: 查询条件。

**返回值**
- List<Map<String, String>>: 查询结果。     

#### desc
获取指定用户表的描述信息。

**参数**
- tableName: 被查询的用户表名。

**返回值**
- List<Map<String, String>>: 用户表描述信息，记录了`PrecompiledConstant.KEY_NAME`到主key的映射，以及`PrecompiledConstant.FIELD_NAME`到所有field的映射，field之间用逗号分隔开。


#### asyncInsert
`insert`的异步接口，向指定表插入指定记录，并在接收到节点的回执后，调用指定回调函数。

**参数**

- tableName: 需要插入记录的表名;
- key: 主key被设置的值;
- fieldNameToValue: 每个field到其对应值的映射;
- callback: 回调函数。

**返回值**
- 无
          

#### asyncUpdate
`update`的异步接口，更新指定记录，并在接收到节点的回执后，调用指定回调函数。

**参数**

- tableName: 用户表名;
- key: 需要更新的记录对应的主key值;
- fieldNameToValue: 更新后的记录;
- condition: 记录更新条件;
- callback: 回调函数。

**返回值**
- 无    

#### asyncRemove
`remove`的异步接口，删除指定记录，并在接收到节点的回执后，调用指定回调函数。

**参数**

- tableName: 用户表名;
- key: 被删除的记录主key值;
- condition: 被删除记录的匹配条件;
- callback: 回调函数。

**返回值**
- 无
        

### ContractLifeCycleService

#### freeze
冻结指定合约。

**参数**
- contractAddress: 被冻结的合约地址。

**返回值**
- RetCode: 合约冻结结果。
  
#### unfreeze
解冻指定合约。

**参数**
- contractAddress: 被解冻的合约地址。

**返回值**
- RetCode: 合约解冻结果。
      
#### grantManager
给指定账户授权冻结/解冻指定合约的权限。

**参数**
- contractAddress: 指定的合约地址;
- userAddress: 被授权对指定合约冻结/解冻权限的账户地址。

**返回值**
- RetCode: 合约生命周期管理权限授权结果。

#### getContractStatus
获取指定合约的状态。

**参数**
- contractAddress: 合约地址。

**返回值**
- String: 合约状态。

   
#### listManager
获取对某合约有管理权限的账户地址。

**参数**
- contractAddress: 合约地址。

**返回值**
- List<String>: 对指定合约有冻结/解冻权限的账户信息。


### CNSService

#### registerCNS
为指定合约注册CNS。

**参数**
- contractName: 合约名;
- contractVersion: 注册的合约版本;
- contractAddress: 合约地址;
- abiData: 注册合约的abi;

**返回值**
- RetCode: CNS注册结果。
  
#### selectByName
根据合约名查询合约CNS信息。

**参数**
- contractName: 需要查询CNS信息的合约名;

**返回值**
- List<CnsInfo>: 查询到的CNS信息列表。
  
#### selectByNameAndVersion
根据合约名和合约版本查询CNS信息。

**参数**
- contractName: 合约名;
- contractVersion: 合约版本。

**返回值**
- List<CnsInfo>: 查询到的CNS信息。
  
#### getContractAddress
根据合约名和合约版本获取合约地址。

**参数**
- contractName: 合约名;
- contractVersion: 合约版本。

**返回值**
- String: 查询到的合约地址。


### getBatchReceiptsByBlockNumberAndRange

根据区块高度和交易范围，批量返回群组内的交易回执信息。

#### **参数**

- `blockNumber`: 请求获取的回执信息所在的区块高度;
- `from`: 需要获取的回执起始索引;
- `count`: 需要批量获取的回执数目，当设置为-1时，返回区块内所有回执信息;

#### **返回值**

- `BcosTransactionReceiptsDecoder`: 包含压缩编码信息的交易回执数据，调用`decodeTransactionReceiptsInfo`方法可将压缩的交易回执转换为`TransactionReceiptsInfo`对象，并可通过该对象获取交易回执具体信息

**示例:**

```java
    // 获取最新区块高度的所有交易回执信息(cient初始化过程省略，详细可以参考快速入门)
   BcosTransactionReceiptsDecoder bcosTransactionReceiptsDecoder =
                    client.getBatchReceiptsByBlockNumberAndRange(
                            client.getBlockNumber().getBlockNumber(), "0", "-1");
    // 解码交易回执信息
    BcosTransactionReceiptsInfo.TransactionReceiptsInfo receiptsInfo = bcosTransactionReceiptsDecoder.decodeTransactionReceiptsInfo();
    // 获取回执所在的区块信息
    BcosTransactionReceiptsInfo.BlockInfo blockInfo = receiptsInfo.getBlockInfo();
    // 获取交易回执列表
    List<TransactionReceipt> receiptList = receiptsInfo.getTransactionReceipts();

```

### getBatchReceiptsByBlockHashAndRange

根据区块哈希和交易范围，批量返回群组内的交易回执信息。

#### **参数**

- `blockHash`: 请求获取的回执信息所在的区块哈希;
- `from`: 需要获取的回执起始索引;
- `count`: 需要批量获取的回执数目，当设置为-1时，返回区块内所有回执信息;

#### **返回值**

- `BcosTransactionReceiptsDecoder`: 包含压缩编码信息的交易回执数据，调用`decodeTransactionReceiptsInfo`方法可将压缩的交易回执转换为`TransactionReceiptsInfo`对象，并可通过该对象获取交易回执具体信息

**示例:**

```java
    // 获取最新区块的所有交易回执信息(cient初始化过程省略，详细可以参考快速入门)
    BcosTransactionReceiptsDecoder bcosTransactionReceiptsDecoder =
                    client.getBatchReceiptsByBlockHashAndRange(
                            client.getBlockHashByNumber(client.getBlockNumber().getBlockNumber())
                                    .getBlockHashByNumber(),
                            "0",
                            "-1");
    // 解码交易回执信息
    BcosTransactionReceiptsInfo.TransactionReceiptsInfo receiptsInfo = bcosTransactionReceiptsDecoder.decodeTransactionReceiptsInfo();
    // 获取回执所在的区块信息
    BcosTransactionReceiptsInfo.BlockInfo blockInfo = receiptsInfo.getBlockInfo();
    // 获取交易回执列表
    List<TransactionReceipt> receiptList = receiptsInfo.getTransactionReceipts();

```
