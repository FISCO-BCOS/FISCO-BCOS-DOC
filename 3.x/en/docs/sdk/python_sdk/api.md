# Python API

Tags: "Python API" "Client" "

----

The Python SDK provides Python API interfaces for blockchain application developers, including:

- Python API: Encapsulates access to FISCO BCOS 2.0+Node [JSON-RPC](../../api.md)The Python API
- Transaction structure definition: FISCO BCOS 2.0 is defined+The transaction data structure of
- Transaction input and output parsing: provides ABI, Event Log, transaction input and output parsing functions
- ChannelHandler: FISCO BCOS channel protocol implementation class, supports SSL encrypted communication between nodes


## Python API：BcosClient
Implemented in 'client / bcosclient.py', encapsulating access to FISCO BCOS 2.0+Node [JSON-RPC](../../api.md)Python API, the main interfaces include:

| Interface Name| 描述| Parameters|
| :--| :--| :-- | 
| getNodeVersion | Obtain blockchain node version information| None|
| getBlockNumber  | Get Latest Block High| None| 
| getPbftView  | Get PBFT View| None| 
| getSealerList | Get Consensus Node List| None| 
| getObserverList | Get Observer Node List| None| 
| getConsensusStatus | Get blockchain node consensus status| None| 
| getSyncStatus |  Obtain the synchronization status of a blockchain node| None| 
| getPeers |  Obtain the connection information of a blockchain node| None| 
| getGroupPeers | Gets the consensus node of the specified group<br>and watch node list| None|
| getNodeIDList |  Get a list of nodes and their connected nodes| None| 
| getGroupList |  Obtain the group ID list of the group to which the node belongs|  None|
| getBlockByHash |  Obtain block information based on block hash| Block Hash|
| getBlockByNumber |  Obtain block information according to block height| Block height|
| getBlockHashByNumber | Obtain block hash based on block height| Block height|
| getTransactionByHash | Get transaction information based on transaction hash| Transaction Hash|
| getTransactionByBlockHashAndIndex |According to the block hash of the transaction,<br>Transaction index Get transaction information| Transaction-owned block hash<br>Transaction Index|
| getTransactionByBlockNumberAndIndex | According to the exchange belongs to the block height,<br>Transaction index Get transaction information| Exchange-owned block height<br>Transaction Index|
| getTransactionReceipt | Get transaction receipt based on transaction hash| Transaction Hash|
| getPendingTransactions | Get all unchained transactions in the transaction pool| None|
| getPendingTxSize |  Get the number of unchained transactions in the transaction pool| None|
| getCode |  Contract data queried by contract address| Contract Address| 
| getTotalTransactionCount | Obtains the number of transactions on the chain of a specified group| None|
| getSystemConfigByKey |  Get System Configuration| System Configuration Keywords<br>如：<br> - tx_count_limit <br> - tx_gas_limit|
| deploy |  Deployment contract| Contract binary code|
| call | Call Contract| Contract Address<br>Contract abi<br>Call Interface Name<br>Parameter List|
| sendRawTransaction | Send transaction| Contract Address<br>Contract abi<br>Interface Name<br>Parameter List<br>Contract binary code|
| sendRawTransactionGetReceipt |  Send transaction<br>and get the results of the trade execution| Contract Address<br>contract abi interface name<br>Parameter List<br>Contract binary code|


## Precompile Service

### CNS

**Class Name**
```
client.precompile.cns.cns_service.CnsService
```

**Function Interface**
-register _ cns: Register the contract name to(Contract Address, Contract Version)maps to the CNS system table
-query _ cns _ by _ name: Query CNS information based on contract name
-query _ cns _ by _ nameAndVersion: Query CNS information based on contract name and contract name

### Consensus

**Class Name**
```
client.precompile.consensus.consensus_precompile.ConsensusPrecompile
```

**Function Interface**

-addSealer: Add consensus node
-addObserver: add observer node
-removeNode: remove node from group

### Permission Control

**Class Name**
```
client.precompile.permission.permission_service.PermissionService
```
**Function Interface**

- grant: Authorize permissions for the specified table to the user
-revoke: reclaims the write permission of the specified user on the specified table
- list_permission: Displays account information that has write permission to the specified table

### CRUD

**Class Name**
```
client.precompile.crud.crud_service.Entry
```
**Function Interface**
-create _ table: Create a user table
-insert: Inserts a record into the user table
-update: Update user table records
-remove: Deletes the specified record in the user table
-select: Query the specified record in the user table
- desc: Querying User Table Information

### System Configuration

**Class Name**
```
client.precompile.config.config_precompile.ConfigPrecompile
```

**Function Interface**
- setValueByKey: Set the value of the system configuration item

## Transaction Structure Definition: BcosTransaction
Implemented in 'client / bcostransaction.py', which defines FISCO BCOS 2.0+The transaction data structure:

| Field| 描述|
| :--   | :--   |
| randomid | Random number, used for transaction weight protection|
| gasPrice | The default is 30000000|
| gasLimit | The upper limit of gas consumed by transactions, which is 30000000 by default|
| blockLimit | Transaction weight limit, default is 500|
| to | Usually the contract address|
| value | Default is 0|
| data | Transaction Data|
| fiscoChainId | Chain ID, which is loaded by configuring 'client _ config.py'|
| groupId | The group ID, which is loaded by configuring 'client _ config.py'|
| extraData | Additional data, empty string by default|


## Transaction input and output parsing: DatatypeParser

Provides ABI, Event Log, and transaction input and output parsing functions, implemented in 'client / datatype _ parser.py':

| Interface| Parameters| 描述| 
| :--   | :--  |  :-- |
| load_abi_file | abi file path| Load and parse ABI file from specified path<br>Create function name, selector to function abi mapping list|
| parse_event_logs | event log| Parsing event log|  
| parse_transaction_input | Transaction input| Parsing Transaction Input<br>Returns the interface name and transaction parameters of the transaction call|
| parse_receipt_output | Interface name of the transaction call<br>Transaction output| Parsing Transaction Output| 


## ChannelHandler

FISCO BCOS channel protocol implementation class, supports SSL encrypted communication between nodes, and can receive messages pushed by nodes, mainly implemented in 'client / channelhandler.py', channel protocol codec reference [here](../../design/protocol_description.html#channelmessage)


## Contract History Query


- **client/contratnote.py：** Use the ini configuration file format to save the latest and historical addresses of the contract for loading (as can be used in the console command(Contract name last)Refers to the address of the latest deployment of a contract)

## Log Module

- **client/clientlogger.py：** Logger definition, which currently includes client logs and statistics logs
- **client/stattool.py** A simple tool class for collecting statistics and printing logs
