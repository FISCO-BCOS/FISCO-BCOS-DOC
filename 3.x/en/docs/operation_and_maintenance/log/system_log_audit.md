# Critical System Log Audit Methods

Tags: "system transaction" "log audit"

---

This article describes the log content of the key steps in the FISCO BCOS blockchain node. It is intended that users can quickly understand the status of the blockchain node and the basis of transaction execution errors.。

## 1. Block Status Log

### 1.1 Block Packing Successfully

The consensus node receives the transaction and broadcasts the actively packaged consensus proposal to other nodes for publicity within the minimum packaging time. The proposal contains all the transactions that have been sorted for execution.。

- Log Keywords:++++++++++++++++ Generate proposal
- Log level: INFO
- Log example:

```log
info|2022-11-24 10:32:35.034810|[CONSENSUS][SEALER]++++++++++++++++ Generate proposal,index=32,curNum=31,hash=9c3c41de...,sysTxs=false,txsSize=1,version=50462720
```

- Log interpretation:
  - index: Block height of the current packed block
  - curNum: currently dropped block height
  - hash: hash of proposal
  - sysTxs: Whether system transactions are included
  - txsSize: Number of transactions packaged
  - version: Version number of the packed block

### 1.2 Block Start Execution

When consensus receives enough Pre on a block proposal-Commit proposal, which will call the Scheduler module to request the execution of the block。

When the synchronization module synchronizes blocks from other nodes to the local, it also calls the Scheduler module to request the execution of blocks to verify the validity of the blocks.。

- Log Keywords: ExecuteBlock request
- Log level: INFO
- Log example:

```log
info|2022-11-24 10:32:35.046634|[SCHEDULER][METRIC][blk-32]ExecuteBlock request,gasLimit=3000000000,verify=false,signatureSize=0,tx count=0,meta tx count=1,version=3.2.0,waitT=1
```

- Log interpretation:
  - blk-32: The block height executed is 32
  - gasLimit: gas limit of the current execution block
  - verify: Whether to verify. If the current node is the leader node, no verification is required.
  - signatureSize: Number of signatures. If the current node is the leader node, the signature does not need to be verified.
  - tx count: The number of intra-block transactions, which is greater than 0 if the block is synchronized from another node
  - meta tx count: The number of transaction meta-information in the block, which is greater than 0 if the request was initiated by the consensus module.
  - version: Version number of the currently executed block

### 1.3 Block Execution Success

This log is output when the consensus module or synchronization module requests the Scheduler module to execute the block.。

- Log Keywords: asyncExecuteBlock success
- Log level: INFO
- Log example:

```log
info|2022-11-24 10:32:35.048912|[CONSENSUS][Core][METRIC]asyncExecuteBlock success,sysBlock=false,number=32,result=989d3d4c...,txsSize=1,txsRoot=02e7ed51...,receiptsRoot=cf7d8528...,stateRoot=018b8af3...,timeCost=0,execPerTx=0
```

- Log interpretation:
  - sysBlock: Whether there are system transactions in the block
  - number: Block high for successful execution
  - result: Block Hash
  - txsSize: Number of transactions
  - txsRoot: Tree root of transaction hash
  - receiptsRoot: Root of the tree for the receipt hash
  - stateRoot: Tree root that stores the hash of the changed data
  - timeCost: Execution Block Time(ms)
  - execPerTx: Average execution time per transaction(ms)

### 1.4 Block Drop

After the consensus or synchronization module passes the verification after executing the block, it will initiate a call to the Scheduler to take the initiative to drop the disk.。

- Log Keywords: ^ ^ ^ ^ ^ ^ ^ ^ Report
- Log level: INFO
- Log example:

```log
info|2022-11-24 10:32:35.058065|[CONSENSUS][PBFT][METRIC]^^^^^^^^Report,sealer=1,txs=1,committedIndex=32,consNum=33,committedHash=989d3d4c...,view=3,toView=3,changeCycle=0,expectedCheckPoint=33,Idx=1,unsealedTxs=0,sealUntil=0,waitResealUntil=0,nodeId=ffa9aa23...
```

- Log interpretation:
  - sealer: Packing Node
  - txs: Number of transactions
  - committedIndex: consensus block high
  - consNum: Current packing block high
  - committedHash: Hash of block on disk
  - view: Current View
  - toView: The next view, when a view switch occurs, will+1
  - expectedCheckPoint: Next check point
  - idx: Current node index
  - unsealedTxs: Not yet packaged deal
  - nodeId: Current node id

## 2. System Configuration Change Log

### 2.1 System Configuration Change Log

When a system configuration modification is initiated, it will be output in the log。

- Log Keywords: SystemConfigPrecompiled
- Log level: INFO
- Log example:

```log
info|2022-11-24 12:07:04.180708|[EXECUTOR][PRECOMPILED][blk-6283][SystemConfigPrecompiled]setValueByKey,configKey=tx_gas_limit,configValue=3000000001
```

- Log interpretation:
  - blk: Current Execution Block High
  - configKey: Modified system configuration name, currently supported:
    - compatibility_version: Compatibility version number
    - consensus_leader_period: Consensus Select Primary Interval
    - tx_count_limit: Maximum number of transactions in a block
    - tx_gas_limit: gas usage limit for a single transaction
  - configValue: Modified System Configuration Values

### 2.2 Consensus node operation log

When the initiating node joins the consensus node, joins the observation node, modifies the weight of the consensus node, or deletes the node, logs are output:

- Log Keywords: SensusPrecompiled
- Log level: INFO
- Log example:

```log
info|2022-11-24 12:27:04.210708|[EXECUTOR][PRECOMPILED][ConsensusPrecompiled]addSealer,nodeID=97af395f31cd52868162c790c2248e23f65c85a64cd0581d323515f6afffc0138279292a55f7bd706f8f1602f142b12a3407a45334eb0cf7daeb064dcec69369
```

- Log Interpretation
  - addSealer: The example above is a request to add to a consensus node, in addition to the following types:
    - addObserver: Add to Watch Node
    - setWeight: Set consensus node weights
    - remove: Delete Node
  - nodeID: Modified node id

## 3. Contract Permission Log

### 3.1 Deployment contract permission writing

The governance committee has approved the write proposal for setting the deployment contract type and deployment contract permissions, which will be output in the node log.。

- Log Keywords: AuthManagerPrecompiled
- Log level: INFO
- Log Example 1:

```log
info|2022-11-24 12:47:04.345608|[EXECUTOR][PRECOMPILED][AuthManagerPrecompiled]setDeployType,type=1
```

- Log Interpretation 1
  - Set deployment permission type
  - type: Type of deployment contract permission, 0: No Permissions Required；1: Whitelist；2: Blacklist
- Log Example 2:

```log
info|2022-11-24 12:47:39.784532|[EXECUTOR][PRECOMPILED][AuthManagerPrecompiled]setDeployAuth,account=0x320b129f4e8dbd956eeb4c3ff3b5627c2945ff1a,isClose=false
```

- Log Interpretation 2
  - Set the deployment permissions of an account
  - account: Account address
  - isClose: Whether to turn off deployment permissions

### 3.2 Contract permission writing

The contract administrator sets the ACL type of a contract interface and sets an account to write the ACL of the contract interface, which will be output in the node log.。

- Log Keywords: ContractAuthMgrPrecompiled
- Log level: INFO
- Log Example 1:

```log
info|2022-11-24 12:47:04.345608|[EXECUTOR][PRECOMPILED][blk-31909][ContractAuthMgrPrecompiled]setMethodAuthType,path=0xa42a320743c2390eb8d8d5928a57a09ebbc6d2ec,func=0x7a23b101,type=1
```

- Log Interpretation 1
  - Set the permission type of the contract interface func
  - blk: Block height
  - path: Contract Address
  - func: Contract Interface Selector
  - type: Type of contract interface permission, 0: No Permissions Required；1: Whitelist；2: Blacklist
- Log Example 2:

```log
info|2022-11-24 12:59:01.321412|[EXECUTOR][PRECOMPILED][blk-31912][ContractAuthMgrPrecompiled]setAuth,path=0xa42a320743c2390eb8d8d5928a57a09ebbc6d2ec,func=0x7a23b101,account=0x320b129f4e8dbd956eeb4c3ff3b5627c2945ff1a,
isClose=false
```

- Log Interpretation 2
  - set the func permission of an account on a contract.
  - blk: Block height
  - path: Contract Address
  - func: Contract Interface Selector
  - account: Account address
  - isClose: Whether to turn off access

### 3.3 Contract Status Write

The contract administrator sets the status type of the contract to which it belongs, which will be output in the node log.。

- Log Keywords: ContractAuthMgrPrecompiled
- Log level: INFO
- Log Example 1:

```log
info|2022-11-24 13:27:11.123121|[EXECUTOR][PRECOMPILED][blk-31123][ContractAuthMgrPrecompiled]setContractStatus,address=0xa42a320743c2390eb8d8d5928a57a09ebbc6d2ec,status=1
```

- Log Interpretation 1
  - blk: Block height
  - address: Contract Address
  - status: Status type of contract, 0: Normal；1: Freeze；2: Abolition

## 4. Account Permission Log

The status type of an account to which the governance committee set belongs will be output in the node log。

- Log Keywords: ContractAuthMgrPrecompiled
- Log level: INFO
- Log Example 1:

```log
info|2022-11-24 13:38:51.783912|[EXECUTOR][PRECOMPILED][blk-31654][AccountManagerPrecompiled]setAccountStatus,account=0x320b129f4e8dbd956eeb4c3ff3b5627c2945ff1a,status=1
```

- Log Interpretation 1
  - blk: Block height
  - account: Account Address
  - status: Status type of contract, 0: Normal；1: Freeze；2: Abolition
