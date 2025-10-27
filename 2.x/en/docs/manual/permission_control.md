# Permission control

## TODO: add Role Based Access control

### Roles and Permissions

## Permission conrtol based on Table permission

This section will introduce the operations concerning permission control, for details please check [Design of Permission Control](../design/security_control/permission_control.md).

```eval_rst
.. important::
For the system is defaulted with no permission setting record, any account can perform permission setting. For example, account 1 gives permission of contract deployment to itself, but account 2 also sets itself with permission of contract deployment. So the setting of account 1 becomes meaningless for every other node can add permissions freely. Therefore, before building consortium chain, confirming permission setting rules is needed. We can use `grantPermissionManager` instruction to set manager account, that is to give some account access to permission setting, which other accounts don't have.

```

### Operations

The operations concerning permission control of following functions are introduced in this section:
- [Permission of chain manager](./permission_control.html#grant-permission-of-chain-manager)
- [Permission of system manager](./permission_control.html#grant-permission-of-system-manager)
- [Permission of contract deployment and user table creation](./permission_control.html#id8)
- [Permission of Contract deployment using CNS](./permission_control.html#cns)
- [Permission of node management](./permission_control.html#id9)
- [Permission to modify system parameter](./permission_control.html#id10)
- [Permission to write user table](./permission_control.html#id11)

### Environment configuration
Configure and start the nodes and console of FISCO BCOS 2.0+. For reference please check [Installation](../installation.md).

### Tools for permission control
FISCO BCOS offers permission control of console commands (developers can call PermissionService API of [SDK API](../sdk/sdk.html#permissionservice) for permission control). The involved permission control commands are:

|Command|Parameter|Function|
|:----|:-----|:----|
|grantPermissionManager                |address               |Grant permission to be chain manager              |
|revokePermissionManager             |address               |Revoke permission of chain manager              |
|listPermissionManager              |                      |Inquire list of accounts with chain manager permission                |
|grantDeployAndCreateManager          |address               |Grant permission to deploy contract and create user table    |
|revokeDeployAndCreateManager       |address               |Revoke permission to deploy contract and create user table    |
|listDeployAndCreateManager        |                      |Inquire list of accounts with permission to deploy contract and create user table|
|grantNodeManager                     |address               |Grant permission to manage node                |
|revokeNodeManager                  |address               |Revoke permission to manage node                |
|listNodeManager                   |                      |Inquire list of accounts with permission to manage node                |
|grantCNSManager                      |address              |Grant CNS permission                |
|revokeCNSManager                   |address               |Revoke CNS permission             |
|listCNSManager                    |                      |Inquire list of accounts with CNS permission                |
|grantSysConfigManager                |address             |Grant permission to modify system parameter            |
|revokeSysConfigManager             |address               |Revoke permission to modify system parameter           |
|listSysConfigManager              |                       |Inquire list of accounts with permission to modify system parameter            |
|grantUserTableManager                |table_name address  |Grant permission to write user table          |
|revokeUserTableManager             |table_name address    |Revoke permission to write user table        |
|listUserTableManager              |table_name            |Inquire list of accounts with permission to write user table            |

### Permission control example
Console provides script `get_account.sh` to generate accounts. The account files will be stored in `accounts` folder. Console can set active accounts. The operation method is introduced in [Console tutorial](../console/console.html#id11). Therefore, through console we can set account to experience permission control. For account safety, we will generate 3 PKCS12 account files under the root folder of console by `get_account.sh` script. Please remember the password during generation. The 3 PKCS12 account files are:
```bash
# account 1
0x2c7f31d22974d5b1b2d6d5c359e81e91ee656252.p12
# account 2
0x7fc8335fec9da5f84e60236029bb4a64a469a021.p12
# account 3
0xd86572ad4c92d4598852e2f34720a865dd4fc3dd.p12
```
Now we can open 3 Linux terminal and log in console with the 3 accounts separately.

Log in with account 1:
```bash
$ ./start.sh 1 -p12 accounts/0x2c7f31d22974d5b1b2d6d5c359e81e91ee656252.p12
```
Log in with account 2:
```bash
$ ./start.sh 1 -p12 accounts/0x7fc8335fec9da5f84e60236029bb4a64a469a021.p12
```
Log in with account 3：
```bash
$ ./start.sh 1 -p12 accounts/0xd86572ad4c92d4598852e2f34720a865dd4fc3dd.p12
```

### Grant permission of chain manager

The 3 accounts play 3 kinds of roles. Account 1 performs chain manager, account 2 performs system manager and account 3 the regular account. Chain manager has permission for access control, namely granting permissions. System manager can manager permissions related to system functions, each of which should be granted independently, including deploying contract, creating user table, managing nodes and deploying contract with CNS and modifying system parameter. Chain manager can grant other accounts to be chain manager or system manager, or grant regular accounts to write table list.

Initial status of chain contains no permission records. Now, we can enter the console of account 1 and set itself as the chain manager, so other accounts are regular accounts.
```
[group:1]> grantPermissionManager 0x2c7f31d22974d5b1b2d6d5c359e81e91ee656252
{
    "code":0,
    "msg":"success"
}

[group:1]> listPermissionManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x2c7f31d22974d5b1b2d6d5c359e81e91ee656252  |                      1                      |
---------------------------------------------------------------------------------------------
```
Account 1 is set as the chain manager.

### Grant permission of system manager

#### Grant permission to deploy contract and create user table
Account 1 grants permission of system manager to account 2. At first, grant account 2 with permission to deploy contract and create user table.
```
[group:1]> grantDeployAndCreateManager 0x7fc8335fec9da5f84e60236029bb4a64a469a021
{
    "code":0,
    "msg":"success"
}

[group:1]> listDeployAndCreateManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x7fc8335fec9da5f84e60236029bb4a64a469a021  |                      2                      |
---------------------------------------------------------------------------------------------
```
Log in console with account 2 and deploy TableTest contract offered by console. Code of TableTest.sol contract is [here](../manual/smart_contract.html#solidity). The CRUD operations of user table t_test are also provided.
```
[group:1]> deploy TableTest.sol
contract address:0xfe649f510e0ca41f716e7935caee74db993e9de8
```
Call create API of TableTest to create user table t_test.
```
[group:1]> call TableTest.sol 0xfe649f510e0ca41f716e7935caee74db993e9de8 create
transaction hash:0x67ef80cf04d24c488d5f25cc3dc7681035defc82d07ad983fbac820d7db31b5b
---------------------------------------------------------------------------------------------
Event logs
---------------------------------------------------------------------------------------------
createResult index: 0
count = 0
---------------------------------------------------------------------------------------------
```
User table t_test is created successfully.

Log in console with account 3 and deploy TableTest contract.
```
[group:1]> deploy TableTest.sol
{
    "code":-50000,
    "msg":"permission denied"
}
```
Account 3 fails to deploy contract as it has no permission.

- **Note:** deploying contract and creating user table are "2-in-1" control items. When using CRUD interface contracts, we suggest to create the needed tables (creating tables in building function of contract) when deploying contract, otherwise "table-missing" error may occur when reading or writing table. If it is needed to dynamically create table, the permission should be granted to minority accounts, otherwise there will be many invalid tables on blockchain.

#### Grant permission to deploy contract using CNS
Console provides 3 commands involving [CNS](../design/features/CNS_contract_name_service.md):

|command name|parameter|function|
|:----|:-----|:----|
|deployByCNS         |contractName contractVersion                 |deploy contract using CNS           |
|callByCNS           |contractName contractVersion funcName params |call contract using CNS           |
|queryCNS            |contractName [contractVersion]               |inquire CNS information              |

**Note:** permission of deployByCNS command is controllable **and needs permission to deploy contract and use CNS at the same time**, permissions of callByCNS and queryCNS commands are not controllable.

Log in console with account 1, grant account 2 with permission to deploy contract using CNS.
```
[group:1]> grantCNSManager 0x7fc8335fec9da5f84e60236029bb4a64a469a021
{
    "code":0,
    "msg":"success"
}

[group:1]> listCNSManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x7fc8335fec9da5f84e60236029bb4a64a469a021  |                     13                      |
---------------------------------------------------------------------------------------------
```
Log in console with account 2, deploy contract using CNS
```
[group:1]> deployByCNS TableTest.sol 1.0
contract address:0x24f902ff362a01335db94b693edc769ba6226ff7

[group:1]> queryCNS TableTest.sol
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x24f902ff362a01335db94b693edc769ba6226ff7  |
---------------------------------------------------------------------------------------------
```
Log in console with account 3, deploy contract using CNS
```
[group:1]> deployByCNS TableTest.sol 2.0
{
    "code":-50000,
    "msg":"permission denied"
}

[group:1]> queryCNS TableTest.sol
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x24f902ff362a01335db94b693edc769ba6226ff7  |
---------------------------------------------------------------------------------------------
```
Account 3 fails to deploy contract by CNS due to lack of permission

#### Grant permission to manage nodes

Console provides 5 commands related to node type management:

|command name|parameter|function|
|:----|:-----|:----|
|addSealer             |nodeID                 |set node as consensus node        |
|addObserver           |nodeID                 |set node as observer node         |
|removeNode            |nodeID                 |set node as free node         |
|getSealerList         |                       |inquire consensus node list           |
|getObserverList       |                       |inquire observer node list           |

- **Note:** permissions of addSealer, addObserver and removeNode commands are controllable, while permissions of getSealerList and getObserverList commands are not.

Log in console with account 1, grant account 2 with permission to manage nodes.
```
[group:1]> grantNodeManager 0x7fc8335fec9da5f84e60236029bb4a64a469a021
{
    "code":0,
    "msg":"success"
}

[group:1]> listNodeManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x7fc8335fec9da5f84e60236029bb4a64a469a021  |                     20                      |
---------------------------------------------------------------------------------------------
```
Log in console with account 2, view consensus node list.
```
[group:1]> getSealerList
[
    01cd46feef2bb385bf03d1743c1d1a52753129cf092392acb9e941d1a4e0f499fdf6559dfcd4dbf2b3ca418caa09d953620c2aa3c5bbe93ad5f6b378c678489e,
    279c4adfd1e51e15e7fbd3fca37407db84bd60a6dd36813708479f31646b7480d776b84df5fea2f3157da6df9cad078c28810db88e8044741152eb037a19bc17,
    320b8f3c485c42d2bfd88bb6bb62504a9433c13d377d69e9901242f76abe2eae3c1ca053d35026160d86db1a563ab2add127f1bbe1ae96e7d15977538d6c0fb4,
    c26dc878c4ff109f81915accaa056ba206893145a7125d17dc534c0ec41c6a10f33790ff38855df008aeca3a27ae7d96cdcb2f61eb8748fefe88de6412bae1b5
]
```
View observer node list:
```
[group:1]> getObserverList
[]
```
Set the first node ID as the observer node:
```
[group:1]> addObserver 01cd46feef2bb385bf03d1743c1d1a52753129cf092392acb9e941d1a4e0f499fdf6559dfcd4dbf2b3ca418caa09d953620c2aa3c5bbe93ad5f6b378c678489e
{
    "code":0,
    "msg":"success"
}

[group:1]> getObserverList
[
    01cd46feef2bb385bf03d1743c1d1a52753129cf092392acb9e941d1a4e0f499fdf6559dfcd4dbf2b3ca418caa09d953620c2aa3c5bbe93ad5f6b378c678489e
]

[group:1]> getSealerList
[
    279c4adfd1e51e15e7fbd3fca37407db84bd60a6dd36813708479f31646b7480d776b84df5fea2f3157da6df9cad078c28810db88e8044741152eb037a19bc17,
    320b8f3c485c42d2bfd88bb6bb62504a9433c13d377d69e9901242f76abe2eae3c1ca053d35026160d86db1a563ab2add127f1bbe1ae96e7d15977538d6c0fb4,
    c26dc878c4ff109f81915accaa056ba206893145a7125d17dc534c0ec41c6a10f33790ff38855df008aeca3a27ae7d96cdcb2f61eb8748fefe88de6412bae1b5
]
```
Log in console with account 3, add observer node to consensus node list.
```
[group:1]> addSealer 01cd46feef2bb385bf03d1743c1d1a52753129cf092392acb9e941d1a4e0f499fdf6559dfcd4dbf2b3ca418caa09d953620c2aa3c5bbe93ad5f6b378c678489e
{
    "code":-50000,
    "msg":"permission denied"
}

[group:1]> getSealerList
[
    279c4adfd1e51e15e7fbd3fca37407db84bd60a6dd36813708479f31646b7480d776b84df5fea2f3157da6df9cad078c28810db88e8044741152eb037a19bc17,
    320b8f3c485c42d2bfd88bb6bb62504a9433c13d377d69e9901242f76abe2eae3c1ca053d35026160d86db1a563ab2add127f1bbe1ae96e7d15977538d6c0fb4,
    c26dc878c4ff109f81915accaa056ba206893145a7125d17dc534c0ec41c6a10f33790ff38855df008aeca3a27ae7d96cdcb2f61eb8748fefe88de6412bae1b5
]

[group:1]> getObserverList
[
    01cd46feef2bb385bf03d1743c1d1a52753129cf092392acb9e941d1a4e0f499fdf6559dfcd4dbf2b3ca418caa09d953620c2aa3c5bbe93ad5f6b378c678489e
]
```
Account 3 fails to add consensus node for lack of permission to manage nodes. Now only account 2 has permission to add observer node to consensus node list.

#### Grant permission to modify system parameter
Console provides 2 commands about system parameter modification:

|Command name|parameter|function|
|:----|:-----|:----|
|setSystemConfigByKey             |key value           |set system parameter with key and value  |
|getSystemConfigByKey             |key                 |inquire value by key               |

- **Note:** currently we support system parameter setting with key tx_count_limit or tx_gas_limit. Permission of setSystemConfigByKey command is controllable, while permission of getSystemConfigByKey command is not.

Log in console with account 1, grant account 2 with permission to modify system parameter.
```
[group:1]> grantSysConfigManager 0x7fc8335fec9da5f84e60236029bb4a64a469a021
{
    "code":0,
    "msg":"success"
}

[group:1]> listSysConfigManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x7fc8335fec9da5f84e60236029bb4a64a469a021  |                     23                      |
---------------------------------------------------------------------------------------------
```
Log in console with account 2, modify the value of system parameter tx_count_limit to 2000.
```
[group:1]> getSystemConfigByKey tx_count_limit
1000

[group:1]> setSystemConfigByKey tx_count_limit 2000
{
    "code":0,
    "msg":"success"
}

[group:1]> getSystemConfigByKey tx_count_limit
2000
```
Log in console with account 3, modify value of parameter tx_count_limit to 3000.
```
[group:1]> setSystemConfigByKey tx_count_limit 3000
{
    "code":-50000,
    "msg":"permission denied"
}

[group:1]> getSystemConfigByKey tx_count_limit
2000
```
Account 3 fails to set parameter due to no permission.

### Grant permission to write user table
Account 1 can grant account 3 with permission to write user table t_test.
```
[group:1]> grantUserTableManager t_test 0xd86572ad4c92d4598852e2f34720a865dd4fc3dd
{
    "code":0,
    "msg":"success"
}
[group:1]> listUserTableManager t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xd86572ad4c92d4598852e2f34720a865dd4fc3dd  |                      6                      |
---------------------------------------------------------------------------------------------
```
Log in console with account 3, insert a record in user table t_test and inquire the records.
```
[group:1]> call TableTest.sol 0xfe649f510e0ca41f716e7935caee74db993e9de8 insert "fruit" 1 "apple"

transaction hash:0xc4d261026851c3338f1a64ecd4712e5fc2a028c108363181725f07448b986f7e
---------------------------------------------------------------------------------------------
Event logs
---------------------------------------------------------------------------------------------
InsertResult index: 0
count = 1
---------------------------------------------------------------------------------------------

[group:1]> call TableTest.sol 0xfe649f510e0ca41f716e7935caee74db993e9de8 select "fruit"
[[fruit], [1], [apple]]
```
Log in console with account 2, update the record inserted by account 3 and inquire the records.
```
[group:1]> call TableTest.sol 0xfe649f510e0ca41f716e7935caee74db993e9de8 update "fruit" 1 "orange"
{
    "code":-50000,
    "msg":"permission denied"
}
[group:1]> call TableTest.sol 0xfe649f510e0ca41f716e7935caee74db993e9de8 select "fruit"
[[fruit], [1], [apple]]
```
Account 2 fails to update information for it has no permission to write user table t_test.

- Account 1 revoke permission of account 3 to write user table t_test.
```
[group:1]> revokeUserTableManager t_test 0xd86572ad4c92d4598852e2f34720a865dd4fc3dd
{
    "code":0,
    "msg":"success"
}

[group:1]> listUserTableManager t_test
Empty set.
```
Revoked successfully.

- **Note:** now there is no account with permission to write user table t_test, so it is back to initial status, that is, all accounts have permission to write table. Therefore, account 1 can grant another account, like account 2, with permission to write this table.

