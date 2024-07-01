# Precompiled Contract Service Interface

Tags: "Precompiled Contracts" "Interface" "Precompiled" "Service"

---------

The Java SDK provides Java API interfaces for blockchain application developers. By function, Java APIs can be divided into the following categories:

- Client: Provides access to FISCO BCOS 3.x node JSON-RPC interface support, providing support for deployment and invocation contracts；
- Precompiled: Provides calls to FISCO BCOS 3.x Precompiled contract(Precompiled Contracts)interfaces, including 'SensusService', 'SystemConfigService', 'BFSService', 'KVTableService', 'TableCRUDService', and 'AuthManager'。

## 5. BFSService

### 5.1 mkdir

Creates a directory at the specified absolute path。

**Parameters**

- path: absolute path

**Return value**

- RetCode: Create Directory Results。

### 5.2 list

View the information of the specified absolute path. If it is a directory file, the meta information of all sub-resources in the directory is returned. If it is another file, the meta information of the file is returned.。(After the node version 3.1, the interface only returns up to 500)

**Parameters**

- absolute Path: absolute path

**Return value**

- List < BfsInfo >: Returns a list of meta information for a file。

### 5.3 list

Note: This interface can only be used when the node version is greater than 3.1

View the information of the specified absolute path. If it is a directory file, the meta information of all sub-resources in the directory is returned. If it is another file, the meta information of the file is returned.。If there are too many directory files to traverse (greater than 500), you can traverse them using offsets and limits。

**Parameters**

- absolute Path: absolute path
- offset: offset
- limit: limit value

**Return value**

- Tuple2 < BigInteger, List < BfsInfo > >: if the first value of tuple is negative, it means that the execution error occurred; if it is positive, it means how many files are left to be returned (when traversing the directory file)；The second value of tuple is a list of meta information of the returned file。

### 5.4 isExist

Note: This interface can only be used when the node version is greater than 3.1

Determine whether the file resource exists。

**Parameters**

- absolute Path: absolute path

**Return value**

- BFSInfo: returns specific file meta information if it exists, or null if it does not exist。

### 5.5 link

Create soft links to contracts under / apps / to facilitate contract management and version control。This method provides the same interface as before in order to adapt to the CNS function of the old node version.。

After successful execution, a link file is created under / apps /. For example, if the contract name is hello and the version number is v1, the absolute path of the link file is / apps / hello / v1

**Parameters**

- name: contract name
- version: contract version number
- contractAddress: contract address
- abi: Contract ABI

**Return value**

- RetCode: execution result

### 5.6 link

Note: This interface can only be used when the node version is greater than 3.1

Create soft links to contracts under / apps / to facilitate contract management and version control。This interface allows users to create soft links at any path in the / apps directory.

**Parameters**

- absolute Path: absolute path
- contractAddress: contract address
- abi: Contract ABI

**Return value**

- RetCode: execution result

### 5.7 readlink

Obtain the address corresponding to the link file。This method provides the same interface as before in order to adapt to the CNS function of the old node version.。

**Parameters**

- absolute Path: absolute path

**Return value**

- address: the address corresponding to the link file.

## 6. ConsensusService

### 6.1 addSealer

 Add the specified node as a consensus node。

**Parameters**

- nodeId: The ID of the node added as the consensus node.
- weight: add the weight of the consensus node

**Return value**

- RetCode: Consensus Node Add Result。

```eval_rst
.. note::
    In order to ensure that the new node does not affect the consensus, the node to be added as a consensus node must establish a P2P network connection with other nodes in the group, and the node block height must not be lower than the current highest block.-10, otherwise it cannot be added as a consensus node。
```

### 6.2 addObserver

Add the specified node as an observation node。

**Parameters**

- nodeId: The ID of the node added as an observation node.

**Return value**

- RetCode: Watch Node Add Results。

### 6.3 removeNode

Move the specified node out of the group。

**Parameters**

- nodeId: The node ID of the node removed from the group.

**Return value**

- RetCode: Execution result of node removed from group。

### 6.4 setWeight

Set the weight of a consensus node。

**Parameters**

- nodeId: The node ID of the consensus node.
- weight: weight, not less than 1

**Return value**

- RetCode: Execution Results。

## 7. SystemConfigService

### 7.1 setValueByKey

Sets the value of the specified system configuration item。

**Parameters**

- key: Configuration item. Currently, 'tx _ count _ limit' and 'consensus _ leader _ period' are supported.；

- value: The value to which the system configuration item is set。

**Return value**

- RetCode: System Configuration Item Setting Results。

## 8. KVTableService

### 8.1 createTable

Create User Table。

**Parameters**

- tableName: Name of the created user table;
- keyFieldName: Primary key name of the user table;
- valueFields: The fields of the user table.

**Return value**

- RetCode: User Table Creation Results。

### 8.2 set

Write a record to the specified user table。

**Parameters**

- tableName: Name of the table in which the record needs to be inserted;
- key: The value to which the primary key is set;
- fieldNameToValue: Mapping of each field to its corresponding value。

**Return value**

- RetCode: Whether the record is inserted successfully。

### 8.3 get

Query specified records in the user table。

**Parameters**

- tableName: Queried user table name。
- key: the primary key value to be queried.。

**Return value**

- String: Query Results。     

### 8.4 desc

Obtain the description information of the specified user table。

**Parameters**

- tableName: Queried user table name。

**Return value**

- Map<String, String>: Description of the user table. The mapping between 'PrecompiledConstant.KEY _ NAME' and the mapping between 'PrecompiledConstant.FIELD _ NAME' and all fields. The fields are separated by commas.。


### 8.5 asyncSet

'set 'asynchronous interface, writes the specified record to the specified table, and calls the specified callback function after receiving the receipt of the node。

**Parameters**

- tableName: Name of the table in which the record needs to be inserted;
- key: The value to which the primary key is set;
- fieldNameToValue: Mapping of each field to its corresponding value;
- callback: callback function。

**Return value**

- None

## 9. CNSService

**Note:** from 3.0.0-rc3 version started, CNS is no longer supported。For the corresponding contract alias function, please refer to the BFS link function.。

**Migration Instructions:** Due to the abandonment of the CNS interface, BFS contains the functions of the CNS and also provides the corresponding adaptation interface.。You can change the original CNS service interface to the BFS interface. The interface corresponds to the following table:

| Method Name| CNSService                                                   | BFSService                                                   |
| ------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Create a mapping between a contract name and a contract address| insert(string name, string version, string address,string abi); | link(string name, string version, string address,string abi); |
| Get the address of the corresponding name and version| selectByNameAndVersion(string name,string version);          | readlink(string path);                                       |
| Traverse all versions of contract name| selectByName(string name);                                   | list(string path);                                           |
| Contract Address| 0x1004                                                       | 0x100e                                                       |

## 10. AuthManager Rights Management Interface

Rights management interfaces include the following three interfaces:

- Query interface without permission；
- Governance Committee-specific interface: An interface that has the private key of the governance committee to initiate transactions in order to execute correctly.；
- Administrator-specific interface: An interface where transactions initiated by an administrator's private key with administrative privileges on the corresponding contract can be executed correctly.。

### 10.1 Query interface without permission

#### getCommitteeInfo

At initialization, a governance committee is deployed whose address information is automatically generated or specified at build _ chain.sh.。Initialize only one member, and the weight of the member is 1。

**Parameters**

- None

**Return value**

- CommitteeInfo: Details of the Governance Committee

#### getProposalInfo

Get information about a specific proposal。

**Parameters**

- proposalID: the ID number of the proposal

**Return value**

- ProposalInfo: details of the proposal

#### getDeployAuthType

Get the permissions policy for the current global deployment

**Parameters**

- None

**Return value**

- BigInteger: policy type: 0 is no policy, 1 is whitelist mode, 2 is blacklist mode

#### checkDeployAuth

Check whether an account has deployment permissions

**Parameters**

- account: account address

**Return value**

- Boolean: Permission

#### checkMethodAuth

Check whether an account has the permission to call an interface of a contract.

**Parameters**

- contractAddr: contract address
- func: function selector for the interface, 4 bytes
- account: account address

**Return value**

- Boolean: Permission

#### getAdmin

Get the administrator address for a specific contract

**Parameters**

- contractAddr: contract address

**Return value**

- account: account address

### 10.2 Special Interface for Account Number of Governance Committee

There must be an account in the Governance Committee's Governors to call, and if there is only one Governance Committee member and the proposal was initiated by that member, then the proposal will be successful。

#### updateGovernor

In the case of a new governance committee, add an address and weight.。If you are deleting a governance member, you can set the weight of a governance member to 0。

**Parameters**

- account: account address
- weight: account weight

**Return value**

- proposalId: returns the ID number of the proposal

#### setRate

Set proposal threshold, which is divided into participation threshold and weight threshold。

**Parameters**

- participatesRate: participation threshold, in percentage units
- winRate: by weight threshold, percentage unit

**Return value**

- proposalId: returns the ID number of the proposal

#### setDeployAuthType

Set the ACL policy for deployment. Only white _ list and black _ list policies are supported.

**Parameters**

- deployAuthType: When type is 1, it is set to a whitelist. When type is 2, it is set to a blacklist.。

**Return value**

- proposalId: returns the ID number of the proposal

#### modifyDeployAuth

Modify a deployment permission proposal for an administrator account

**Parameters**

- account: account address
- openFlag: whether to enable or disable permissions

**Return value**

- proposalId: returns the ID number of the proposal

#### resetAdmin

Resetting an administrator account proposal for a contract

**Parameters**

- newAdmin: Account address
- contractAddr: contract address

**Return value**

- proposalId: returns the ID number of the proposal

#### revokeProposal

Undo the initiation of a proposal, an operation that only the governance committee that initiated the proposal can operate

**Parameters**

- proposalId: ID number of the proposal

**Return value**

- TransactionReceipt: execute receipt

#### voteProposal

vote on a proposal

**Parameters**

- proposalId: ID number of the proposal
- agree: Do you agree to this proposal?

**Return value**

- TransactionReceipt: execute receipt

### 10.3 Special interface for contract administrator account

Each contract has an independent administrator. Only the administrator account of a contract can set the interface permissions of the contract.。

#### setMethodAuthType

Set the API call ACL policy of a contract. Only white _ list and black _ list policies are supported.

**Parameters**

- contractAddr: contract address
- func: function selector for the contract interface, four bytes in length。
- authType: When type is 1, it is set to a whitelist. When type is 2, it is set to a blacklist.。

**Return value**

- result: If it is 0, the setting is successful。

#### setMethodAuth

Modify the interface call ACL policy of a contract。

**Parameters**

- contractAddr: contract address
- func: function selector for the contract interface, four bytes in length。
- account: account address
- isOpen: whether to enable or disable permissions

**Return value**

- result: If it is 0, the setting is successful。
