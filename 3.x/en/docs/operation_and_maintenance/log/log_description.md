# View Log

Tags: "Log Format" "Log Keywords" "Troubleshooting" "View Log"

----

All group logs of FISCO BCOS are output to the file 'log _% YYYY% mm% dd% HH.% MM' in the log directory, and the log format is customized, so that users can view the running status of the chain through logs。

## Log Format

Each log record is formatted as follows:

```bash
# Log format:
log_level|time|[module_name] content

# Log example:

info|2022-11-21 20:00:35.479505|[SCHEDULER][blk-1]BlockExecutive prepare: fillBlock end,txNum=1,cost=0,fetchNum=1
```

The fields have the following meanings:

- `log_level`: Log level. Currently, log levels include 'trace', 'debug', 'info', 'warning', 'error', and 'fatal'

- `time`: Log output time, accurate to nanoseconds

- 'module _ name': module keyword. For example, the synchronization module keyword is' SYNC 'and the consensus module keyword is' CONSENSUS'

- 'content': logging content


## Common Log Description

**Consensus Packaging Log**

```eval_rst
.. note::

    - only the consensus node periodically outputs consensus packed logs(The command "tail -f log /* | grep "${group_id}.*++""View consensus packaging logs for a specified group)

    - Package logs to check whether the consensus node of the specified group is abnormal**Abnormal consensus node does not output packed logs**
```

The following is an example of consensus packed logs:
```bash
info|2022-11-21 20:00:45.530293|[CONSENSUS][PBFT]addCheckPointMsg,reqHash=c2e031c8...,reqIndex=2,reqV=9,fromIdx=3,Idx=1,weight=4,minRequiredWeight=3
```
- 'reqHash': the hash of the PBFT request
- 'reqIndex': the block height corresponding to the PBFT request
- `reqV`:  View corresponding to PBFT request
- `fromIdx`: The node index number that generated the PBFT request
- `Idx`: Current Node Index Number
- `weight`: Total consensus weight of the proposal corresponding to the request
- `minRequiredWeight`: The minimum voting weight required to reach consensus on the proposal corresponding to the request


**Exception Log**

Network jitter, network disconnect, or configuration error(Genesis block file as a group is inconsistent)The PBFT consensus node will output the 'ViewChangeWarning' log, as shown in the following example:

```bash
warning|2022-11-17 00:58:03.621465|[CONSENSUS][PBFT]onCheckPointTimeout: resend the checkpoint message package,index=176432,hash=d411d77d...,committedIndex=176431,consNum=176432,committedHash=ecac3705...,view=1713,toView=1713,changeCycle=0,expectedCheckPoint=176433,Idx=0,unsealedTxs=168,sealUntil=176432,waitResealUntil=176431,nodeId=0318568d...
```
- 'index': consensus index number
- 'hash': consensus block hash
- `committedIndex`: Falling block block height
- `consNum`:  Next consensus block high
- `committedHash`: Drop Block Hash
- `view`: Current View
- `toview`:  Next View
- `changeCycle`: Current Timeout Clock Cycle
- `expectedCheckPoint`: The next block to be executed is high
- `Idx`: The index number of the current node
- `sealUntil`:  The height of the block that can be packaged to generate the next block. In a system block scenario, the block can be packaged to generate the next block if and only if the disk height exceeds sealUntil
- `waitResealUntil`: Same as above, the block height of the next block can be packaged to produce the next block, when there is a view switch+ In the system block scenario, the next block can only be packaged if and only if the drop height exceeds waitResealUntil
- `unsealedTxs`: Number of unpackaged transactions in the trading pool
- `nodeId`: current consensus node id


**Block Drop Log**

If the block consensus is successful or the node is synchronizing blocks from other nodes, the disk drop log will be output。

```eval_rst
.. note::

    Send transactions to nodes, if the transaction is processed, non-free nodes will output drop logs(The command "tail -f log /* | grep "Report""View node out-of-block status)If the log is not output, the node is in an abnormal state. Please check whether the network connection is normal and whether the node certificate is valid

```

The following is the block drop log:
```bash
info|2022-11-21 20:00:45.531121|[CONSENSUS][PBFT][METRIC]^^^^^^^^Report,sealer=3,txs=1,committedIndex=2,consNum=3,committedHash=c2e031c8...,view=9,toView=9,changeCycle=0,expectedCheckPoint=3,Idx=1,unsealedTxs=0,sealUntil=0,waitResealUntil=0,nodeId=8f69046f...
```

The fields in the log are described as follows:
- 'sealer': the index number of the consensus node that generates the proposal
- 'txs': Number of transactions contained within the block
- `committedIndex`: Falling block block height
- `consNum`:  Next consensus block high
- `committedHash`: Drop Block Hash
- `view`: Current View
- `toview`:  Next View
- `changeCycle`: Current Timeout Clock Cycle
- `expectedCheckPoint`: The next block to be executed is high
- `Idx`: The index number of the current node
- `sealUntil`:  The height of the block that can be packaged to generate the next block. In a system block scenario, the block can be packaged to generate the next block if and only if the disk height exceeds sealUntil
- `waitResealUntil`: Same as above, the block height of the next block can be packaged to produce the next block, when there is a view switch+ In the system block scenario, the next block can only be packaged if and only if the drop height exceeds waitResealUntil
- `unsealedTxs`: Number of unpackaged transactions in the trading pool
- `nodeId`: current consensus node id


**network connection log**

```eval_rst
.. note::

    The command "tail -f log /* | grep "connected count""Check the network status. If the number of network connections in the log output does not meet the expectation, use the" netstat -anp| grep fisco-bcos "command to check node connectivity
```

An example of a log is as follows:
```bash
info|2022-08-15 19:38:59.270112|[P2PService][Service][METRIC]heartBeat,connected count=3
```

The fields in the log have the following meanings:
- `connected count`: Number of nodes establishing P2P network connection with current node


## Log Module Keywords

The core module keywords in the FISCO BCOS log are as follows:

| Module| Module Keywords|
| :--- | :---- |
| Blockchain Initialization Module| INITIALIZER |
| Network Foundation Module| NETWORK |
| P2P network module| P2PService |
| Ledger module|LEDGER|
| Consensus block packaging module|CONSENSUS, SEALER|
| PBFT consensus processing module| CONSENSUS, PBFT|
| Block / transaction synchronization module|SYNC|
| Trading pool|TXPOOL|
| Amop Module| AMOP |
| scheduler| SCHEDULER |
| Actuator| EXECUTOR |
| light node|LIGHTNODE |
| Gateway|Gateway |
| Storage Middleware Module|STORAGE|
| Chain Tool|TOOL|