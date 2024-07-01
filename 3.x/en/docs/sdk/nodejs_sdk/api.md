# Node.js API

Tag: "java-sdk`` ``Client`` 

----
The Node.js SDK provides a Node.js API interface for blockchain application developers to use as a service for external calls.。According to the function, Node.js API can be divided into the following categories:

- **Web3jService**: Provides access to FISCO BCOS 2.0+Node [JSON-RPC](../../api.md)Interface Support；Provides support for deploying and invoking contracts。
- **PrecompiledService**：

    A precompiled contract is an embedded FISCO BCOS contract that passes C++Implementation of efficient smart contracts, including [distributed permission control](../../design/security_control/permission_control.md)、[CNS](../../design/features/cns_contract_name_service.md)System property configuration, node type configuration and other functions。PrecompiledService is a general term for APIs that call such functions, divided into:

  - **PermissionService**Provides support for distributed permission control
  - **CNSService**: Providing support to the CNS
  - **SystemConfigService**Provides support for system configurations
  - **ConsensusService**Provides support for node type configuration
  - **CRUDService**: Provide for CRUD(Add, delete, change and check)operation, you can create a table or add or delete a table。

## API calling convention

- Before using a service, you first need to initialize the global 'Configuration' object to provide the necessary configuration information for each service。'Configuration 'object in' nodejs-sdk / packages / api / common / configuration.js', whose initialization parameter is the path of a configuration file or an object containing configuration items。For a description of the configuration items of the configuration file, see [Configuration Description](./configuration.md)
- Unless otherwise specified, the APIs provided by the Node.js SDK are**asynchronous**API。The actual return value of the asynchronous API is a [Promise] wrapped around the API return value.(https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise)Object, developers can use [async / await syntax](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/await)or [then... catch... finally method](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/then)Manipulate the Promise object to implement its own program logic
- When an error occurs in the API and the logic cannot be continued (for example, the contract address does not exist), an exception is thrown directly. All exceptions are inherited from the Error class.

## Web3jService

**Location**：`nodejs-sdk/packages/api/web3j`

**Function**Access FISCO BCOS 2.0+Node [JSON-RPC](../../api.md)；Deployment contract；Call Contract

| Interface Name| 描述| Parameters| Return value|
| :--| :--| :-- | :-- |
| getBlockNumber  | Get Latest Block High| None| Object, the result is in the result field\*\* |
| getPbftView  | Get PBFT View| None| Ibid.|
| getObserverList | Get Observer Node List| None| Ibid.|
| getSealerList | Get Consensus Node List| None| Ibid.|
| getConsensusStatus | Get blockchain node consensus status| None| Ibid.|
| getSyncStatus |  Obtain the synchronization status of a blockchain node| None| Ibid.|
| getClientVersion | Obtain blockchain node version information| None| Ibid.|
| getPeers |  Obtain the connection information of a blockchain node| None| Ibid.|
| getNodeIDList |  Get a list of nodes and their connected nodes| None| Ibid.|
| getGroupPeers | Obtain the consensus node < br > and watch node list of the specified group| Ibid.|
| getGroupList |  Obtain the group ID list of the group to which the node belongs|  None| Ibid.|
| getBlockByHash |  Obtain block information based on block hash| Block Hash| Ibid.|
| getBlockByNumber |  Obtain block information according to block height| Block height| Ibid.|
| getBlockHashByNumber | Obtain block hash based on block height| Block height| Ibid.|
| getTransactionByHash | Get transaction information based on transaction hash| Transaction Hash| Ibid.|
| getTransactionByBlockHashAndIndex | Obtain transaction information based on the transaction's block hash and < br > transaction index.| Transaction-owned block hash < br > transaction index| Ibid.|
| getTransactionByBlockNumberAndIndex | Get trading information based on the block height of the exchange, < br > trading index| Exchange-owned block height < br > Trading index| Ibid.|
| getPendingTransactions | Get all unchained transactions in the transaction pool.| None| Ibid.|
| getPendingTxSize |  Get the number of unchained transactions in the transaction pool| None| Ibid.|  
| getTotalTransactionCount | Obtains the number of transactions on the chain of a specified group.| None| Ibid.|
| getTransactionReceipt | Get transaction receipt based on transaction hash| Transaction Hash| Ibid.|
| getCode | Contract data queried by contract address| Contract Address| Ibid.|
| getSystemConfigByKey |  Get System Configuration| System configuration keyword. Currently, < br >: < br >- tx_count_limit <br> - tx_gas_limit| Ibid.|
| sendRawTransaction | Send a signed transaction, which is then executed and agreed upon by the nodes on the chain| Accept variable number of parameters: When the number of parameters is 1, the parameters should be the RLP code of the transaction；When the number of parameters is 3, the parameters should be the contract address, method signature, and method parameters.| Ibid.|
| deploy |  Deployment contract| Contract Path < br > Output Path| Ibid.|
| call | Call read-only contract| Contract Address < br > Call Interface\*< br > Parameter list| Ibid.|

\*Call interface: function name(parameter type,...)For example: func(uint256,uint256)there can be no spaces between parameter types

## PrecompiledService

### PermissionService

**Location**：`nodejs-sdk/packages/api/precompiled/permission`

**Function**Provides support for distributed permission control

| Interface Name| 描述| Parameters| Return value|
| :--| :--| :-- | :-- |
| grantUserTableManager | Set permission information based on user table name and external account address| Table name < br > External account address| Number, representing the number of rows in the permission table that were successfully overwritten|
| revokeUserTableManager | Remove permission information based on user table name and external account address| Table name < br > External account address| Number, representing the number of rows in the permission table that were successfully overwritten|
| listUserTableManager | Query the set permission record list according to the user table name(Each record contains the external account address and the active block height.) | Table Name| Array, the queried records|
| grantDeployAndCreateManager | Add permissions to deploy contracts and create user tables for external account addresses| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| revokeDeployAndCreateManager | Remove deployment contract and create user table permissions for external account addresses| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| listDeployAndCreateManager | Querying the list of permission records that have permission to deploy contracts and create user tables| None| Array, the queried records|
| grantPermissionManager | Add permissions for managing external account addresses| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| revokePermissionManager | Permission to remove administrative permissions for an external account address| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| listPermissionManager | Query the list of permission records that have administrative permissions| None| Array, the queried records|
| grantNodeManager | Add node management permissions for external account addresses| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| revokeNodeManager |  Remove the node management permission of the external account address| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| listNodeManager | Query the list of permission records that have node management| None| Array, the queried records|
| grantCNSManager | Increase Use CNS permissions for external account addresses| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| revokeCNSManager | Remove Use CNS permission for an external account address| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| listCNSManager | Querying the list of records that have permission to use the CNS| None| Array, the queried records|
| grantSysConfigManager | Increase the system parameter management permission of the external account address| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| revokeSysConfigManager | Remove the system parameter management permission of the external account address.| External Account Address| Number, representing the number of rows in the permission table that were successfully overwritten|
| listSysConfigManager | Query the list of records that have permission to manage system parameters| None| Array, the queried records|

### CNSService

**Location**：`nodejs-sdk/packages/api/precompiled/cns`

**Function**Provides support for node type configuration

| Interface Name| 描述| Parameters| Return value|
| :--| :--| :-- | :-- |
| registerCns | Register CNS information based on contract name, contract version number, contract address, and contract abi| contract name < br > contract version number < br > contract address < br > contract abi|  Number, representing the number of CNS entry records that were successfully incremented|
| getAddressByContractNameAndVersion | Based on contract name and contract version number(The contract name and contract version number are concatenated with an English colon)Query Contract Address。If the contract version number is missing, the latest contract version is used by default.| Contract Name+ ':' + Version Number| Object, the CNS information queried|
| queryCnsByName | Query CNS information based on contract name| Contract Name| Array, the CNS information queried|
| queryCnsByNameAndVersion | Query CNS information based on contract name and contract version number| Contract Name < br > Version Number| Ibid.|

### SystemConfigService

**Location**：`nodejs-sdk/packages/api/precompiled/systemConfig`

**Function**Provides support for system configurations

| Interface Name| 描述| Parameters| Return value|
| :--| :--| :-- | :-- |
| setValueByKey | Set the corresponding value according to the key (the value corresponding to the query key, refer to**Web3jService**in the 'getSystemConfigByKey' interface)| Key Name < br > Value| Number, which represents the number of successfully modified system configurations|

### ConsensusService

**Location**：`nodejs-sdk/packages/api/precompiled/consensus`

**Function**Provides support for node type configuration

| Interface Name| 描述| Parameters| Return value|
| :--| :--| :-- | :-- |
| addSealer | Set the corresponding node as the consensus node according to the node NodeID| Node ID| Number, representing the number of consensus nodes that were successfully increased|
| addObserver | Set the corresponding node as the observer node according to the node NodeID| Node ID| Number, representing the number of successfully increased observations|
| removeNode |  Set the corresponding node as a free node according to the node NodeID| Node ID| Number, representing the number of free increases that were successful|

### CRUDService

**Location**：`nodejs-sdk/packages/api/precompiled/crud`

**Function**: Provide for CRUD(Add, delete, change and check)Operation support

| Interface Name| 描述| Parameters| Return value|
| :--| :--| :-- | :-- |
| createTable |  Create Table| Table object < br > The table object needs to set its table name, primary key field name and other field names。where the other field names are strings separated by commas| Number, status code, 0 indicates successful creation|
| insert |  Insert Record| Table object Entry object < br > Table object needs to set table name and primary key field name；Entry is a map object that provides the inserted field name and field value. Note that the primary key field must be set| Number, representing the number of inserted records
| update | Update Record| Table object Entry object Condtion object < br > Table object requires setting table name and primary key field name；The Condition object is a condition object that allows you to set the matching criteria for the query| Array, the queried records
| remove | Remove Record| Table Object Condition Object < br >| The table object needs to set the table name and primary key field name；The Condition object is a condition object, and you can set the match condition for removal.| Number, the number of records successfully removed|
| Select      | Query records| Table object: Table object needs to set the table name and primary key field value < br / > Condtion object: Condition object is a condition object, you can set the matching condition of the query| Number, the number of records successfully queried|
| desc | Query information about a table based on the table name| Table Name| Object, which mainly contains the primary key field name and other attribute fields of the table|
