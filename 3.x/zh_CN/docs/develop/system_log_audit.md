# 关键系统日志审计方法

标签：``系统交易`` ``日志审计``

---

本文将描述FISCO BCOS区块链节点内关键步骤的日志内容，意在用户可以快速了解区块链节点的状态、交易执行错误的依据，可使用其他审计工具通过抓取日志从而获取区块链的实际状态。

## 1. 区块状态日志

### 1.1 区块打包成功

共识节点收到交易，在最小打包时间内将会主动打包共识Proposal广播到其他节点进行公示，Proposal内包含已排序待执行的所有交易。

- 日志关键词：++++++++++++++++ Generate proposal
- 日志级别：INFO
- 日志举例：

```log
info|2022-11-24 10:32:35.034810|[CONSENSUS][SEALER]++++++++++++++++ Generate proposal,index=32,curNum=31,hash=9c3c41de...,sysTxs=false,txsSize=1,version=50462720
```

- 日志解读：
  - index: 当前打包区块的块高
  - curNum: 当前已落盘的块高
  - hash: proposal 的哈希
  - sysTxs: 是否包含系统交易
  - txsSize: 打包的交易数量
  - version: 打包区块的版本号

### 1.2 区块开始执行

当共识在某个区块proposal上收到足够多Pre-Commit proposal，将会调用Scheduler模块请求执行区块。

当同步模块从其他节点同步区块到本地，也会调用Scheduler模块请求执行区块，以验证区块合法性。

- 日志关键词：ExecuteBlock request
- 日志级别：INFO
- 日志举例：

```log
info|2022-11-24 10:32:35.046634|[SCHEDULER][METRIC][blk-32]ExecuteBlock request,gasLimit=3000000000,verify=false,signatureSize=0,tx count=0,meta tx count=1,version=3.2.0,waitT=1
```

- 日志解读：
  - blk-32: 执行的块高为32
  - gasLimit: 当前执行区块的gas限制
  - verify: 是否进行验证，如果当前节点就是leader节点，那么不需要验证
  - signatureSize: 签名数量，如果当前节点就是leader节点，那么不需要验证签名
  - tx count: 区块内交易数量，如果是从其他节点同步的区块，那么这一项大于0
  - meta tx count: 区块内交易元信息数量，如果是共识模块发起的请求，那么这一项大于0
  - version: 当前执行的区块版本号

### 1.3 区块执行成功

当共识模块或同步模块请求Scheduler模块执行区块完成后，将会输出该日志。

- 日志关键词：asyncExecuteBlock success
- 日志级别：INFO
- 日志举例：

```log
info|2022-11-24 10:32:35.048912|[CONSENSUS][Core][METRIC]asyncExecuteBlock success,sysBlock=false,number=32,result=989d3d4c...,txsSize=1,txsRoot=02e7ed51...,receiptsRoot=cf7d8528...,stateRoot=018b8af3...,timeCost=0,execPerTx=0
```

- 日志解读：
  - sysBlock: 区块内是否带有系统交易
  - number: 执行成功的块高
  - result: 区块哈希
  - txsSize: 交易数量
  - txsRoot: 交易哈希的树根
  - receiptsRoot: 回执哈希的树根
  - stateRoot: 存储变化数据哈希的树根
  - timeCost: 执行区块时间(ms)
  - execPerTx: 每个交易平均执行时间(ms)

### 1.4 区块落盘

共识或同步模块在执行完区块后验证通过，将会发起调用Scheduler主动进行落盘操作。

- 日志关键词：^^^^^^^^Report
- 日志级别：INFO
- 日志举例：

```log
info|2022-11-24 10:32:35.058065|[CONSENSUS][PBFT][METRIC]^^^^^^^^Report,sealer=1,txs=1,committedIndex=32,consNum=33,committedHash=989d3d4c...,view=3,toView=3,changeCycle=0,expectedCheckPoint=33,Idx=1,unsealedTxs=0,sealUntil=0,waitResealUntil=0,nodeId=ffa9aa23...
```

- 日志解读：
  - sealer: 打包节点
  - txs: 交易数量
  - committedIndex: 共识块高
  - consNum: 当前打包块高
  - committedHash: 落盘的区块哈希
  - view: 当前视图
  - toView: 下个视图，当出现视图切换时将会+1
  - expectedCheckPoint: 下一个check point
  - idx: 当前节点index
  - unsealedTxs: 尚未打包交易
  - nodeId: 当前node id

## 2. 系统配置更改日志

### 2.1 系统配置更改日志

当发起系统配置修改时，将会在日志输出。

- 日志关键词：SystemConfigPrecompiled
- 日志级别：INFO
- 日志举例：

```log
info|2022-11-24 12:07:04.180708|[EXECUTOR][PRECOMPILED][blk-6283][SystemConfigPrecompiled]setValueByKey,configKey=tx_gas_limit,configValue=3000000001
```

- 日志解读：
  - blk: 当前执行块高
  - configKey: 修改的系统配置名，目前支持：
    - compatibility_version: 兼容性版本号
    - consensus_leader_period: 共识选主间隔
    - tx_count_limit: 区块内交易数量最大值
    - tx_gas_limit: 单笔交易的gas使用上限
  - configValue: 修改的系统配置值

### 2.2 共识节点操作日志

当发起节点加入共识节点、加入观察节点、修改共识节点权重、删除节点等操作，都将输出日志：

- 日志关键词：ConsensusPrecompiled
- 日志级别：INFO
- 日志举例：

```log
info|2022-11-24 12:27:04.210708|[EXECUTOR][PRECOMPILED][ConsensusPrecompiled]addSealer,nodeID=97af395f31cd52868162c790c2248e23f65c85a64cd0581d323515f6afffc0138279292a55f7bd706f8f1602f142b12a3407a45334eb0cf7daeb064dcec69369
```

- 日志解读
  - addSealer: 上面举例的是添加到共识节点的请求, 此外还有以下类型：
    - addObserver: 添加到观察节点
    - setWeight: 设置共识节点权重
    - remove: 删除节点
  - nodeID: 修改的node id

## 3. 合约权限日志

### 3.1 部署合约权限写入

治理委员会通过了设置部署合约类型、部署合约权限的写入提案，将会在节点日志中输出。

- 日志关键词：AuthManagerPrecompiled
- 日志级别：INFO
- 日志举例1：

```log
info|2022-11-24 12:47:04.345608|[EXECUTOR][PRECOMPILED][AuthManagerPrecompiled]setDeployType,type=1
```

- 日志解读1
  - 设置部署权限类型
  - type: 部署合约权限的类型，0: 不需要权限；1: 白名单；2: 黑名单
- 日志举例2:

```log
info|2022-11-24 12:47:39.784532|[EXECUTOR][PRECOMPILED][AuthManagerPrecompiled]setDeployAuth,account=0x320b129f4e8dbd956eeb4c3ff3b5627c2945ff1a,isClose=false
```

- 日志解读2
  - 设置某个账号的部署权限
  - account: 账号地址
  - isClose: 是否关闭部署权限

### 3.2 合约权限写入

合约管理员设置某个合约接口的ACL类型、设置某个账号对该合约接口ACL写入，将会在节点日志中输出。

- 日志关键词：ContractAuthMgrPrecompiled
- 日志级别：INFO
- 日志举例1：

```log
info|2022-11-24 12:47:04.345608|[EXECUTOR][PRECOMPILED][blk-31909][ContractAuthMgrPrecompiled]setMethodAuthType,path=0xa42a320743c2390eb8d8d5928a57a09ebbc6d2ec,func=0x7a23b101,type=1
```

- 日志解读1
  - 设置合约接口func的权限类型
  - blk: 块高
  - path: 合约地址
  - func: 合约接口Selector
  - type: 合约接口权限的类型，0: 不需要权限；1: 白名单；2: 黑名单
- 日志举例2:

```log
info|2022-11-24 12:59:01.321412|[EXECUTOR][PRECOMPILED][blk-31912][ContractAuthMgrPrecompiled]setAuth,path=0xa42a320743c2390eb8d8d5928a57a09ebbc6d2ec,func=0x7a23b101,account=0x320b129f4e8dbd956eeb4c3ff3b5627c2945ff1a,
isClose=false
```

- 日志解读2
  - 设置某个账号的对某个合约的func接口权限
  - blk: 块高
  - path: 合约地址
  - func: 合约接口Selector
  - account: 账号地址
  - isClose: 是否关闭访问权限

### 3.3 合约状态写入

合约管理员设置所属合约的状态类型，将会在节点日志中输出。

- 日志关键词：ContractAuthMgrPrecompiled
- 日志级别：INFO
- 日志举例1：

```log
info|2022-11-24 13:27:11.123121|[EXECUTOR][PRECOMPILED][blk-31123][ContractAuthMgrPrecompiled]setContractStatus,address=0xa42a320743c2390eb8d8d5928a57a09ebbc6d2ec,status=1
```

- 日志解读1
  - blk: 块高
  - address: 合约地址
  - status: 合约的状态类型，0: 正常；1: 冻结；2: 废止

## 4. 账户权限日志

治理委员设置所属某个账户的状态类型，将会在节点日志中输出。

- 日志关键词：ContractAuthMgrPrecompiled
- 日志级别：INFO
- 日志举例1：

```log
info|2022-11-24 13:38:51.783912|[EXECUTOR][PRECOMPILED][blk-31654][AccountManagerPrecompiled]setAccountStatus,account=0x320b129f4e8dbd956eeb4c3ff3b5627c2945ff1a,status=1
```

- 日志解读1
  - blk: 块高
  - account: 账户地址
  - status: 合约的状态类型，0: 正常；1: 冻结；2: 废止
