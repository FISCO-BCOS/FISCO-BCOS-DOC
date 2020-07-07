# Log description

All group logs of FISCO BCOS are outputted in the log directory to the file `log_% YYYY% mm% dd% HH.% MM`, and the log format is customized to facilitate users to view the status of each group through the log. Please refer to the log configuration instructions [Log configuration instructions](./configuration.html#id6)

## Log format

Each log record format is as follows:

```bash
# Log format:
log_level|time|[g:group_id][module_name] content

# Example log:
info|2019-06-26 16:37:08.253147|[g:3][CONSENSUS][PBFT]^^^^^^^^Report,num=0,sealerIdx=0,hash=a4e10062...,next=1,tx=0,nodeIdx=2
```

The meaning of each field is as follows:

- `log_level`: Log level, currently mainly including`trace`, `debug`, `info`, `warning`, `error` and `fatal`, Where `fatal` is output when an extremely serious error occurs

- `time`: Log output time, accurate to nanoseconds

- `group_id`: Output log group ID

- `module_name`: Module keyword, for example, SYNC module keyword is SYNC and consensus module keyword is CONSENSUS

- `content`: Logging content


## Common log description

**Consensus packaging block log**

```eval_rst
.. note::

    - Only the consensus node will periodically output the consensus packaging log (you can use the command ``tail -f log/* | grep "${group_id}.*++"`` under the node directory to view the specified group consensus packaging log)

    - The packaging log can check whether the consensus node of the specified group is abnormal, **abnormal consensus nodes will not output packed logs**
```

The following is an example of a consensus packaging log:

```bash
info|2019-06-26 18:00:02.551399|[g:2][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=1f9c2b14...
```

The meaning of each field in the log is as follows:
- `blkNum`: Height of packed blocks
- `tx`: The number of transactions contained in the packed block
- `nodeIdx`: Index of the current consensus node
- `hash`: Hash of packed block


**Consensus exception log**

Network jitter, network disconnection, or configuration errors (like the inconsistency of a group's genesis block files) may all cause abnormal node consensus, The PBFT consensus node will output the `ViewChangeWarning` log, the example is as follows:

```bash
warning|2019-06-26 18:00:06.154102|[g:1][CONSENSUS][PBFT]ViewChangeWarning: not caused by omit empty block ,v=5,toV=6,curNum=715,hash=ed6e856d...,nodeIdx=3,myNode=e39000ea...
```
The meaning of each field of the log is as follows:

- v: PBFT consensus view of current node
- toV: The view the current node is trying to switch to
- curNum: The highest block height of the node
- hash: Node highest block hash
- nodeIdx: Current consensus node request
- myNode: Node ID of the current node


**Block Commit Log**

If the block consensus is successful or the node is synchronizing blocks from other nodes, the log will be output.

```eval_rst
.. note::

    Send a transaction to the node, if the transaction is processed, the non-free node will output the log (n the node directory, you can use the command ``tail -f log/* | grep "${group_id}.*Report"`` to check the status of the node block), If the log is not output, it indicates that the node is in an abnormal state. Please check whether the network connection is normal and the node certificate is valid.

```

The following is the block commit log:
```bash
info|2019-06-26 18:00:07.802027|[g:1][CONSENSUS][PBFT]^^^^^^^^Report,num=716,sealerIdx=2,hash=dfd75e06...,next=717,tx=8,nodeIdx=3
```

The description of each field in the log is as follows:

- `num`: Committed block height
- `sealerIdx`: The consensus node index that packages the block
- `hash`: Committed block hash
- `next`: Next block height
- `tx`: Number of transactions included in the block
- `nodeIdx`: Current consensus node index


**Network connection log**

```eval_rst
.. note::

    n the node directory, you can check the network status by using the command ``tail -f log/* | grep "connected count"``, If the number of network connections output by the log does not meet expectations, please check the node connection through the ``netstat -anp | grep fisco-bcos`` command
```

Examples of logs are as follows: 

```bash
info|2019-06-26 18:00:01.343480|[P2P][Service] heartBeat,connected count=3
```

The meaning of each field in the log is as follows:
- `connected count`: Number of nodes that establish a P2P network connection with the current node


## Log module keywords

The core module keywords in the FISCO BCOS log are as follows:

| Module | Module keywords |
| :--- | :---- |
| Blockchain initialization module | INITIALIZER |
| Network basic module | NETWORK |
| P2P network module | P2P |
| ChannelRPC module |  CHANNEL |
| RPC module| RPC |
| Ledger module |LEDGER|
| Consensus block packaging module |CONSENSUS, SEALER|
| PBFT consensus processing module | CONSENSUS, PBFT|
| RAFT consensus processing module | CONSENSUS, RAFTENGINE|
| Block/transaction sync module |SYNC|
| Transaction pool |TXPOOL|
| Blockchain module | BLOCKCHAIN |
| Block verify module    | BLOCKVERIFIER | 
| DAG module |DAG |
| executive context| EXECUTIVECONTEXT|
| Precompile contract |PRECOMPILED|
| Storage middleware module |STORAGE|
| External Storage engine |SQLConnectionPool|
| MySQL Storage engine  |ZdbStorage|

