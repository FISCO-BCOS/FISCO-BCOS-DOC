# Java SDK 接口

标签：``RPC`` ``Interface``

---------

Java SDK为区块链应用开发者提供了Java API接口，按照功能，Java API可分为如下几类：

- Client: 提供访问FISCO BCOS 3.x节点JSON-RPC接口支持、提供部署及调用合约的支持；
- Precompiled: 提供调用FISCO BCOS 3.x Precompiled合约(预编译合约)的接口，主要包括`ConsensusService`、`SystemConfigService`、`BFSService`、`KVTableService`。
- AuthManager: 提供FISCO BCOS 3.x 权限控制预部署合约的调用。

```eval_rst
.. note::
    - Client接口声明位于 `Client.java` 文件中
    - Client是群组维度的对象，可参考 `快速入门 <quick_start.html>`_ 初始化Client，初始化Client时，必须传入群组名
```

**特别注意：Client接口均有两种，一种是带有node的接口，另一种是不带node的接口。带有node的接口可以让节点RPC发送请求到指定已连接的节点。如果不指定，节点RPC则会随机发送请求到节点。**

## 1. 合约操作接口

### sendTransaction

发送交易到区块链RPC。

**参数**

- node：可让RPC发送请求到指定节点
- signedTransactionData：签名后的交易
- withProof：返回是否带上默克尔树证明
  
**返回值**

- BcosTransactionReceipt: 节点收到交易后，回复给SDK的回包，包括交易哈希信息。

### sendTransactionAsync

交易发布异步接口, 收到节点的响应之后，调用指定的callback。

**参数**

- node：可让RPC发送请求到指定节点
- signedTransactionData: 签名后的交易字符串;
- withProof：返回是否带上默克尔树证明
- callback: SDK收到节点的回包后，调用的回调函数，回调函数时将会带上交易回执。

**返回值**

- 无

### call

向节点发送请求，调用合约常量接口。

**参数**

- node：可让RPC发送请求到指定节点
- transaction: 合约调用信息，包含合约地址、合约调用者以及调用的合约接口和参数的abi编码

**返回值**

- Call: 合约常量接口的返回结果，包括当前块高、接口执行状态信息以及接口执行结果

### callAsync

合约常量接口异步调用，接收到节点返回的合约接口执行结果后，执行指定的回调函数

**参数**

- node：可让RPC发送请求到指定节点
- transaction: 合约调用信息，包含合约地址、合约调用者以及调用的接口和参数信息；
- callback: 回调函数。

**返回值**

- 无

### getCode

查询指定合约地址对应的合约代码信息。

**参数**

- node：可让RPC发送请求到指定节点
- address: 合约地址。

**返回值**

- Code: 合约地址对应的合约代码。

## 2. 区块链查询接口

### getBlockNumber

获取Client对象对应的群组最新块高。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- BlockNumber: Client对象对应的群组最新区块高度。

### getBlockNumberAsync

异步获取Client对象对应的群组最新块高。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取块高后的回调

**返回值**

- 无

### getTotalTransactionCount

获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- TotalTransactionCount: 交易统计信息，包括：
  - txSum: 上链的交易总量
  - blockNumber: 群组的当前区块高度
  - failedTxSum: 上链执行异常的交易总量

### getTotalTransactionCountAsync

异步获取Client对应群组的交易统计信息，包括上链的交易数、上链失败的交易数目。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取交易信息后的回调

**返回值**

- 无

### getBlockByNumber

根据区块高度获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点；
- blockNumber: 区块高度；
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash：true/false，表明获取的区块信息中是否包含完整的交易信息；
  - false: 节点返回的区块中包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希。

**返回值**

- BcosBlock: 查询获取的区块信息

### getBlockByNumberAsync

根据区块高度异步获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点
- blockNumber: 区块高度；
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；
  - false: 节点返回的区块中包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希；
- callback：获取区块完成后的回调。

**返回值**

- 无

### getBlockByHash

根据区块哈希获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点
- blockHash: 区块哈希
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希;
  - false: 节点返回的区块中包含完整的交易信息。

**返回值**

- BcosBlock: 查询获取的区块信息。

### getBlockByHashAsync

根据区块哈希异步获取区块信息。

**参数**

- node：可让RPC发送请求到指定节点
- blockHash: 区块哈希
- onlyHeader：true/false，表明获取的区块信息中是否只获取区块头数据；
- onlyTxHash: true/false，表明获取的区块信息中是否包含完整的交易信息；
  - true: 节点返回的区块中仅包含交易哈希;
  - false: 节点返回的区块中包含完整的交易信息；
- callback：获取区块完成后的回调。

**返回值**

- 无

### getBlockHashByNumber

根据区块高度获取区块哈希

**参数**

- node：可让RPC发送请求到指定节点
- blockNumber: 区块高度

**返回值**

- BlockHash: 指定区块高度对应的区块哈希

### getBlockHashByNumberAsync

根据区块高度异步获取区块哈希

**参数**

- node：可让RPC发送请求到指定节点
- blockNumber: 区块高度
- callback：获取之后的回调

**返回值**

- 无

### getTransactionByHash

根据交易哈希获取交易信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：是否带上默克尔树证明

**返回值**

- BcosTransaction: 指定哈希对应的交易信息。

### getTransactionByHashAsync

根据交易哈异步希获取交易信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：是否带上默克尔树证明
- callback：获取到交易时的回调

**返回值**

- 无

### getTransactionReceipt

根据交易哈希获取交易回执信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：返回是否带上默克尔树证明

**返回值**

- BcosTransactionReceipt: 交易哈希对应的回执信息。

### getTransactionReceiptAync

根据交易哈希获取交易回执信息。

**参数**

- node：可让RPC发送请求到指定节点
- transactionHash: 交易哈希
- withProof：返回是否带上默克尔树证明
- callback：获取交易回执时的回调

**返回值**

- 无

### getPendingTxSize

获取交易池内未处理的交易数目。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- PendingTxSize: 交易池内未处理的交易数目。

### getPendingTxSizeAsync

获取交易池内未处理的交易数目。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取交易回执时的回调

**返回值**

- 无

### getBlockLimit

获取Client对应群组的BlockLimit，BlockLimit主要用于交易防重。

**参数**

- 无

**返回值**

- BigInteger: 群组的BlockLimit。

### getPeers

获取指定节点的网络连接信息。

**参数**

- endpoint: 被查询的节点的`IP:Port`。

**返回值**

- Peers: 指定节点的网络连接信息。

### getPeersAsync

异步获取指定节点的网络连接信息。

**参数**

- endpoint：被查询的节点的`IP:Port`。
- callback：获取之后的回调

**返回值**

- 无

### getSyncStatus

获取节点同步状态。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- SyncStatus: 区块链节点同步状态。

### getSyncStatusAsync

异步获取节点同步状态。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取同步信息之后的回调

**返回值**

- 无

### getSystemConfigByKey

根据指定配置关键字获取系统配置项的值。

**参数**

- node：可让RPC发送请求到指定节点
- key: 系统配置项，目前包括`tx_count_limit`, `consensus_leader_period`.

**返回值**

- SystemConfig: 系统配置项的值。

### getSystemConfigByKeyAsync

根据指定配置关键字异步获取系统配置项的值。

**参数**

- node：可让RPC发送请求到指定节点
- key: 系统配置项，目前包括`tx_count_limit`, `consensus_leader_period`.
- callback：获取配置项之后的回调

**返回值**

- 无

## 3. 共识查询接口

### getObserverList

获取Client对应群组的观察节点列表。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- ObserverList: 观察节点列表。

### getObserverListAsync

异步获取Client对应群组的观察节点列表。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取节点列表之后的回调

**返回值**

- 无

### getSealerList

获取Client对应群组的共识节点列表。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- SealerList: 共识节点列表。

### getSealerListAsycn

异步获取Client对应群组的共识节点列表。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取节点列表之后的回调

**返回值**

- 无

### getPbftView

节点使用PBFT共识算法时，获取PBFT视图信息。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- PbftView: PBFT视图信息。

### getPbftViewAsync

节点使用PBFT共识算法时，异步获取PBFT视图信息。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- PbftView: PBFT视图信息。

### getConsensusStatus

获取节点共识状态。

**参数**

- node：可让RPC发送请求到指定节点

**返回值**

- ConsensusStatus: 节点共识状态。

### getConsensusStatusAsync

异步获取节点共识状态。

**参数**

- node：可让RPC发送请求到指定节点
- callback：获取状态后的回调

**返回值**

- 无

## 4. 群组查询接口

### getGroupInfo

查询当前群组的状态信息。

**参数**

- 无

**返回值**

- BcosGroupInfo: 被查询的群组状态信息。

### getGroupInfoAsync

异步查询当前群组的状态信息。

**参数**

- callback：查询到状态信息之后的回调

**返回值**

- 无

### getGroupList

获取当前节点的群组列表。

**参数**

- 无
  
**返回值**

- BcosGroupList: 当前节点的群组列表。

### getGroupListAsync

异步获取当前节点的群组列表。

**参数**

- callback：获取群组列表之后的回调

**返回值**

- 无

### getGroupPeers

获取当前节点指定群组连接的节点列表。

**参数**

- 无

**返回值**

- GroupPeers: 指定群组连接的节点列表。

### getGroupPeersAsync

异步获取当前节点指定群组连接的节点列表。

**参数**

- callback：获取节点列表之后的回调

**返回值**

- 无

### getGroupInfoList

获取当前节点群组信息列表。

**参数**

- 无

**返回值**

- BcosGroupInfoList: 当前节点群组信息列表。

### getGroupInfoListAsync

异步获取当前节点群组信息列表。

**参数**

- callback：获取群组信息之后的回调

**返回值**

- 无

### getGroupNodeInfo

获取群组内指定节点的信息。

**参数**

- node: 指定节点名

**返回值**

- BcosGroupNodeInfo: 查询获取的节点信息。

### getGroupNodeInfoAsync

异步获取群组内指定节点的信息。

**参数**

- node: 指定节点名
- callback：获取信息后的回调

**返回值**

- 无

## 5. 预编译合约服务接口

### 5.1 BFSService

#### mkdir

在指定的绝对路径下创建目录。

**参数**

- path：绝对路径

**返回值**

- RetCode：创建目录结果。

#### list

查看指定的绝对路径的信息，如果是目录文件，则返回目录下所有子资源元信息，如果是合约文件，则返回合约文件的元信息。

**参数**

- path：绝对路径

**返回值**

- List<FileInfo>：返回文件的元信息列表。

### 5.2 ConsensusService

#### addSealer

 将指定节点添加为共识节点。

**参数**

- nodeId：被添加为共识节点的node ID.
- weight：添加共识节点的权重

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

### 5.3 SystemConfigService

#### setValueByKey

设置指定系统配置项的值。

**参数**

- key: 配置项，目前支持`tx_count_limit`, `consensus_leader_period`；

- value: 系统配置项被设置的值。

**返回值**

- RetCode: 系统配置项设置结果。

### 5.4 KVTableService

#### createTable

创建用户表。

**参数**

- tableName: 创建的用户表名;
- keyFieldName: 用户表的主key名;
- valueFields: 用户表的fields.

**返回值**

- RetCode: 用户表创建结果。

#### set

向指定用户表中写入一条记录。

**参数**

- tableName: 需要插入记录的表名;
- key: 主key被设置的值;
- fieldNameToValue: 每个field到其对应值的映射。

**返回值**

- RetCode: 记录是否插入成功。

#### get

查询用户表指定记录。

**参数**

- tableName: 被查询的用户表名;
- key: 被查询的主key值;

**返回值**

- Map<String, String>: 查询结果。     

#### desc

获取指定用户表的描述信息。

**参数**

- tableName: 被查询的用户表名。

**返回值**

- Map<String, String>: 用户表描述信息，记录了`PrecompiledConstant.KEY_NAME`到主key的映射，以及`PrecompiledConstant.FIELD_NAME`到所有field的映射，field之间用逗号分隔开。


#### asyncSet

`set`的异步接口，向指定表写入指定记录，并在接收到节点的回执后，调用指定回调函数。

**参数**

- tableName: 需要插入记录的表名;
- key: 主key被设置的值;
- fieldNameToValue: 每个field到其对应值的映射;
- callback: 回调函数。

**返回值**

- 无

### 5.5 CNSService

**注意：** 从3.0.0-rc3版本开始，不再支持CNS。相应的合约别名功能请参考BFS link功能。

## 6. AuthManager权限管理接口

权限管理接口包含以下三种接口：

- 无需权限的查询接口；
- 治理委员会专用接口：拥有治理委员会的私钥发起的交易才能正确执行的接口；
- 管理员专用接口：拥有相应合约的管理权限的管理员私钥发起的交易才能被正确执行的接口。

### 6.1 无需权限的查询接口

#### getCommitteeInfo

在初始化时，将会部署一个治理委员，该治理委员的地址信息在 build_chain.sh时自动生成或者指定。初始化只有一个委员，并且委员的权重为1。

**参数**

- 无

**返回值**

- CommitteeInfo：治理委员会的详细信息

#### getProposalInfo

获取某个特定的提案信息。

**参数**

- proposalID：提案的ID号

**返回值**

- ProposalInfo：提案的详细信息

#### getDeployAuthType

获取当前全局部署的权限策略

**参数**

- 无

**返回值**

- BigInteger：策略类型：0则无策略，1则为白名单模式，2则为黑名单模式

#### checkDeployAuth

检查某个账号是否有部署权限

**参数**

- account：账号地址

**返回值**

- Boolean：是否有权限

#### checkMethodAuth

检查某个账号是否有某个合约的一个接口的调用权限

**参数**

- contractAddr：合约地址
- func：接口的函数选择器，为4个字节
- account：账号地址

**返回值**

- Boolean：是否有权限

#### getAdmin

获取特定合约的管理员地址

**参数**

- contractAddr：合约地址

**返回值**

- account：账号地址

### 6.2 治理委员账号专用接口

必须要有治理委员会的Governors中的账户才可以调用，如果治理委员只有一个，且提案是该委员发起的，那么这个提案一定能成功。

#### updateGovernor

如果是新加治理委员，新增地址和权重即可。如果是删除治理委员，将一个治理委员的权重设置为0 即可。

**参数**

- account：账号地址
- weight：账号权重

**返回值**

- proposalId：返回提案的ID号

#### setRate

设置提案阈值，提案阈值分为参与阈值和权重阈值。

**参数**

- participatesRate：参与阈值，百分比单位
- winRate：通过权重阈值，百分比单位

**返回值**

- proposalId：返回提案的ID号

#### setDeployAuthType

设置部署的ACL策略，只支持 white_list 和 black_list 两种策略

**参数**

- deployAuthType：type为1时，设置为白名单，type为2时，设置为黑名单。

**返回值**

- proposalId：返回提案的ID号

#### modifyDeployAuth

修改某个管理员账户的部署权限提案

**参数**

- account：账号地址
- openFlag：是开启权限还是关闭权限

**返回值**

- proposalId：返回提案的ID号

#### resetAdmin

重置某个合约的管理员账号提案

**参数**

- newAdmin：账号地址
- contractAddr：合约地址

**返回值**

- proposalId：返回提案的ID号

#### revokeProposal

撤销提案的发起，该操作只有发起提案的治理委员才能操作

**参数**

- proposalId：提案的ID号

**返回值**

- TransactionReceipt：执行回执

#### voteProposal

向某个提案进行投票

**参数**

- proposalId：提案的ID号
- agree：是否同意这个提案

**返回值**

- TransactionReceipt：执行回执

### 6.3 合约管理员账号专用接口

每个合约都有独立的管理员，持有某个合约的管理员账号才能对合约的接口权限进行设置。

#### setMethodAuthType

设置某个合约的接口调用ACL策略，只支持 white_list 和 black_list 两种策略

**参数**

- contractAddr：合约地址
- func：合约接口的函数选择器，长度是四个字节。
- authType：type为1时，设置为白名单，type为2时，设置为黑名单。

**返回值**

- result：如果是0则设置成功。

#### setMethodAuth

修改某个合约的接口调用ACL策略。

**参数**

- contractAddr：合约地址
- func：合约接口的函数选择器，长度是四个字节。
- account：账号地址
- isOpen：是开启权限，还是关闭权限

**返回值**

- result：如果是0则设置成功。