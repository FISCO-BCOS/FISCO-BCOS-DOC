# Console

Tags: "Python SDK" "PythonSDK Console"

----

[Python SDK](https://github.com/FISCO-BCOS/python-sdk)A simple console is implemented through 'console.py' to support contract operations, account management operations, etc.。

```eval_rst
.. note::

    - **Python SDK is currently a candidate version, available for development and testing, available for enterprise applications** `Java SDK <../java_sdk/index.html>`_
    - To install the Java version console, refer to 'here <.. /.. / installation.html >' _
    - To run console.py in windows, use '.\ console.py' or 'python console.py'.

```


## Common Commands

### deploy

Deploy the contract:
```
./console.py deploy [contract_name] [save]
```
Parameters include:
- contract_name: The contract name, which needs to be placed in the 'contracts' directory first
- save: If the save parameter is set, the contract address will be written to the history file.

```bash
$ ./console.py deploy HelloWorld save

INFO >> user input : ['deploy', 'HelloWorld', 'save']

backup [contracts/HelloWorld.abi] to [contracts/HelloWorld.abi.20190807102912]
backup [contracts/HelloWorld.bin] to [contracts/HelloWorld.bin.20190807102912]
INFO >> compile with solc compiler
deploy result  for [HelloWorld] is:
 {
    "blockHash": "0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d",
    "blockNumber": "0x1",
    "contractAddress": "0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce",
    "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
    "gasUsed": "0x44ab3",
    "input": "0x6080604052... several lines omitted... c6f2c20576f726c6421000000000000000000000000000000",
    "logs": [],
    "logsBloom": "0x000... several lines omitted... 0000",
    "output": "0x",
    "status": "0x0",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionHash": "0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91",
    "transactionIndex": "0x0"
}
on block : 1,address: 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
address save to file:  bin/contract.ini
```

### call

Call the contract interface and parse the returned result:
```
./console.py  call [contract_name] [contract_address] [function] [args]
```

Parameters include:
- contract _ name: contract name
- contract _ address: the address of the contract called
- function: the contract interface called
- args: call parameter

```bash
# Contract address: 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
# Call interface: get
$./console.py  call HelloWorld 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce get

INFO >> user input : ['call', 'HelloWorld', '0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce', 'get']
INFO >> call HelloWorld , address: 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce, func: get, args:[]
INFO >> call result: ('Hello, World!',)
```

### sendtx

Sending a transaction invokes the interface of the specified contract, and the transaction results are written to the block and status:
```
./console.py sendtx [contract_name] [contract_address] [function] [args]
```

Parameters include:
- contract _ name: contract name
- contract _ address: contract address
- function: function interface
- args: parameter list

```bash
# Contract Name: HelloWorld
# Contract address: 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
# Call interface: set
# Parameters:"Hello, FISCO"
$ ./console.py sendtx HelloWorld 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce set "Hello, FISCO"

INFO >> user input : ['sendtx', 'HelloWorld', '0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce', 'set', 'Hello, FISCO']

INFO >> sendtx HelloWorld , address: 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce, func: set, args:['Hello, FISCO']

INFO >>  receipt logs :
INFO >> transaction hash :  0xc20cbc6b0f28ad8fe1c560c8ce28c0e7eb7719a4a618a81604ac87ac46cc60f0
tx input data detail:
 {'name': 'set', 'args': ('Hello, FISCO',), 'signature': 'set(string)'}
receipt output : ()
```

### newaccount

Create a new account and save the results in encrypted form with 'bin / accounts / ${accoutname}.keystore 'file,**If there is already an account file with the same name in the directory, a backup of the old file is copied**：

```
./console.py newaccount [account_name] [account_password]
```

Parameters include:

- account _ name: Account name
- account _ password: Password to encrypt the keystore file

```eval_rst
.. note::

    - After creating an account using the account creation command, if you want to use it as the default account, modify the "account _ keyfile" and "account _ password" configurations of client _ config.py.
    - Account name cannot exceed 240 characters
    - If "account _ password" contains special characters, add single quotation marks around "account _ password," otherwise it cannot be parsed
```

```bash
$ ./console.py newaccount test_account "123456"

>> user input : ['newaccount', 'test_account', '123456']

starting : test_account 123456
new address :    0x247e7AE892a94c9e089D61A7DB08af23CEDBec16
new privkey :    0xe2cf070a7c1da05577841b54b4f8ca7d9f7eb52e688bb7e61a2c6ada8a4c5c77
new pubkey :     0x71317d52a7f8b5bb3fa882b9936d7d31a04e6a122e6fdf790d39aeee8ed2883d3c0b90f644cab0b30153d700d93da4c4ea4aef07a7eca2a5e62c8d0f058b3533
encrypt use time : 1.453 s
save to file : [bin/accounts/test_account.keystore]
>>-------------------------------------------------------
>> read [bin/accounts/test_account.keystore] again after new account,address & keys in file:
decrypt use time : 1.447 s
address:         0x247e7AE892a94c9e089D61A7DB08af23CEDBec16
privkey:         0xe2cf070a7c1da05577841b54b4f8ca7d9f7eb52e688bb7e61a2c6ada8a4c5c77
pubkey :         0x71317d52a7f8b5bb3fa882b9936d7d31a04e6a122e6fdf790d39aeee8ed2883d3c0b90f644cab0b30153d700d93da4c4ea4aef07a7eca2a5e62c8d0f058b3533

account store in file: [bin/accounts/test_account.keystore]

**** please remember your password !!! *****
```

### showaccount

Based on the account name and the password of the account 'keystore' file, output the account public and private key information:

```
./console.py showaccount [account_name] [account_password]
```

Parameters include:

- name: Account name
- password: Account 'keystore' file password

```bash
$ ./console.py showaccount test_account "123456"

>> user input : ['showaccount', 'test_account', '123456']

show account : test_account, keyfile:bin/accounts/test_account.keystore ,password 123456
decrypt use time : 1.467 s
address:         0x247e7AE892a94c9e089D61A7DB08af23CEDBec16
privkey:         0xe2cf070a7c1da05577841b54b4f8ca7d9f7eb52e688bb7e61a2c6ada8a4c5c77
pubkey :         0x71317d52a7f8b5bb3fa882b9936d7d31a04e6a122e6fdf790d39aeee8ed2883d3c0b90f644cab0b30153d700d93da4c4ea4aef07a7eca2a5e62c8d0f058b3533

account store in file: [bin/accounts/test_account.keystore]

**** please remember your password !!! *****
```

### usage

output console use method

```bash
 $ ./console.py usage

INFO >> user input : ['usage']

FISCO BCOS 2.0 @python-SDK Usage:
newaccount [name] [password] [save]
        Create a new account with the account name as the parameter(such as alice, bob)and password
        The resulting encryption is saved in the account directory specified by the profile*If there is already an account file with the same name in the directory, a backup of the old file is copied
        If entered"save"parameter at the end, do not ask for direct backup and write
        create a new account ,save to :[bin/accounts] (default) ,
        the path in client_config.py:[account_keyfile_path]
        if account file has exist ,then old file will save to a backup
        if "save" arg follows,then backup file and write new without ask
        the account len should be limitted to 240

    ... omit lines...
    [getTransactionByBlockHashAndIndex] [blockHash] [transactionIndex]
    [getTransactionByBlockNumberAndIndex] [blockNumber] [transactionIndex]
    [getSystemConfigByKey] [tx_count_limit/tx_gas_limit]
```
### list

Output all interfaces supported by the Python SDK:

```bash
$ ./console.py list

INFO >> user input : ['list']

 >> RPC commands
    [getNodeVersion]
    [getBlockNumber]
    ... omit lines...
    [getTransactionByBlockHashAndIndex] [blockHash] [transactionIndex]
    [getTransactionByBlockNumberAndIndex] [blockNumber] [transactionIndex]
    [getSystemConfigByKey] [tx_count_limit/tx_gas_limit]
```

## CNS

The Python SDK console provides CNS commands, including registering CNS and querying CNS information. For details about how to use CNS, see [here](../../design/features/cns_contract_name_service.md)。

### registerCNS

Will(Contract Address, Contract Version)The mapping to the contract name is registered in the CNS system table:
```bash
./console.py registerCNS [contract_name] [contract_address] [contract_version]
```
Parameters include:
- contract_name: Contract Name
- contract_address: Contract Address
- contract_version: Contract Version

```bash
# Map contract address 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce and contract version v _ 1.0 to contract name HelloWorld
$ ./console.py registerCNS HelloWorld 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce v_1.0

INFO >> user input : ['registerCNS', 'HelloWorld', '0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce', 'v_1.0']

INFO >> CNS version (strip space): v_1.0
INFO >> registerCNS
     >> status: 0x0
     >> transactionHash: 0x14720764a67c669811c02e9d9b4c7faa5ea94328e1e33fb7e35e885a27843a4e
     >> gasUsed: 0x6a98
     >> registerCNS succ, output: 1
```

### queryCNSByName

Query CNS information based on contract name:
```
./console.py queryCNSByName [contract_name]
```
Parameters include:
- contract _ name: contract name

```bash
Query the CNS information corresponding to the HelloWorld contract name
$ ./console.py queryCNSByName HelloWorld

INFO >> user input : ['queryCNSByName', 'HelloWorld']

     >> ('[{"abi":"\\"\\"","address":"0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce","name":"HelloWorld","version":"v_1.0"}]\n',)
CNS ITEM 0 >>
        ContractName: HelloWorld
        ContractVersion: v_1.0
        ContractAddress: 0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce

```

### queryCNSByNameAndVersion

Query CNS information based on contract name and contract version:
```
./console.py queryCNSByNameAndVersion [contract_name] [contract_version]
```
Parameters include:
- contract_name: Contract Name
- contract_version: Contract Version

```bash
# Query CNS information for contract name HelloWorld, version v _ 1.0
$ ./console.py queryCNSByNameAndVersion HelloWorld v_1.0

INFO >> user input : ['queryCNSByNameAndVersion', 'HelloWorld', 'v_1.0']

INFO >> CNS version (strip space): v_1.0
     >> ('[{"abi":"\\"\\"","address":"0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce","name":"HelloWorld","version":"v_1.0"}]\n',)
CNS ITEM 0 >>
        ContractName: HelloWorld
        ContractVersion: v_1.0
        ContractAddress: 0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce
```

## Node Management

The Python SDK provides node management commands, including adding consensus nodes, adding observer nodes, and deleting nodes from groups. For details about node management, see [here](../../manual/node_management.md)。

### removeNode

Removes the specified node from the group:
```
./console.py removeNode [nodeId]
```
Parameters include:
- nodeId: nodeID of the deleted node

```bash
# Set the node to be located in the ~ / fisco / nodes directory and query the nodeID of node1
$ cat ~/fisco/nodes/127.0.0.1/node1/conf/node.nodeid
12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4
# Remove node 1 from group node1
./console.py removeNode 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

INFO >> user input : ['removeNode', '12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4']

INFO >> removeNode
     >> status: 0x0
     >> transactionHash: 0x68cde78d76f490b35431905d2336d9811966a370da8b4041db092feb09981f28
     >> gasUsed: 0x7698
     >> removeNode succ, output: 1
```

### addSealer

Add the specified node to the consensus node list:
```
./console.py addSealer [nodeId]
```
Parameters include:
- nodeId: The nodeID of the added consensus node. To obtain the nodeID of the node, see [here](../../manual/configuration.html#id11)

```bash
# Set the node to be located in the ~ / fisco / nodes directory and query the nodeID of node1
$ cat ~/fisco/nodes/127.0.0.1/node1/conf/node.nodeid
12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

# Add node node1 as a consensus node
$./console.py addSealer 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

INFO >> user input : ['addSealer', '12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4']

INFO >> addSealer
     >> status: 0x0
     >> transactionHash: 0xfddfa618419880e37f82c8cd385994fcb1ee1d4c5b4b506ae0d67f223c8b723d
     >> gasUsed: 0x7698
     >> addSealer succ, output: 1
```


### addObserver
Add the specified node as an observer node:
```
./console.py addObserver [nodeId]
```
Parameters include:
- nodeId: The node nodeID of the added observer node. For details about how to obtain the node nodeID, see [here](../../manual/configuration.html#id11)

```bash
# Set the node to be located in the ~ / fisco / nodes directory and query the nodeID of node1
$ cat ~/fisco/nodes/127.0.0.1/node1/conf/node.nodeid
12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

# Add node node1 as an observation node
$ ./console.py addObserver 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

INFO >> user input : ['addObserver', '12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4']

INFO >> addObserver
     >> status: 0x0
     >> transactionHash: 0xb126900787205a5f913e6643058359a07ace1cc550190a5a9478ae4f49cfc1eb
     >> gasUsed: 0x7658
     >> addObserver succ, output: 1
```

### System Configuration
Python SDK provides system configuration modification commands, FISCO BCOS currently supports the system configuration reference [here](../../manual/configuration.html#id21)。
```
./console.py setSystemConfigByKey [key(tx_count_limit/tx_gas_limit)] [value]
```
Parameters include:
- key: configuration keyword, which mainly includes' tx _ count _ limit 'and' tx _ gas _ limit'
- value: Configure the value of the keyword

```bash
# Adjust the maximum number of transactions in the block to 500
$ ./console.py setSystemConfigByKey tx_count_limit 500

INFO >> user input : ['setSystemConfigByKey', 'tx_count_limit', '500']

INFO >> setSystemConfigByKey
     >> status: 0x0
     >> transactionHash: 0xded8abc0858f8a7be5961ae38958928c98f75ee78dbe8197a47c382cb2549de1
     >> gasUsed: 0x5b58
     >> setSystemConfigByKey succ, output: 1

# Adjust trading gas limit to 400000000
$ ./console.py setSystemConfigByKey tx_gas_limit 400000000

INFO >> user input : ['setSystemConfigByKey', 'tx_gas_limit', '400000000']

INFO >> setSystemConfigByKey
     >> status: 0x0
     >> transactionHash: 0x4b78868ec183c432e07971f578f5ab8222a9effda39dfa8e87643410cb2cea05
     >> gasUsed: 0x5c58
     >> setSystemConfigByKey succ, output: 1
```

## Permission Management

The Python SDK provides permission management functions, including authorization, permission revocation, and permission list. For details about permission control, see [here](../../manual/distributed_storage.md)。

### grantPermissionManager
Authorize the functions that control permissions to the specified account:
```
./console.py grantPermissionManager [account_adddress]
```
Parameters include:
- account _ address: the address of the account to which the permission is granted. The account can be generated by using the 'newaccount' command.

```bash
# Get Default Account Address
./console.py showaccount pyaccount "123456"
INFO >> user input : ['showaccount', 'pyaccount', '123456']
show account : pyaccount, keyfile:bin/accounts/pyaccount.keystore ,password 123456
decrypt use time : 1.450 s
address:         0x95198B93705e394a916579e048c8A32DdFB900f7
privkey:         0x48140af2cf0879631d558833aa48b7bb4b37091dbfe902a573886538041b69c0
pubkey :         0x142d340c0f4df64bf56bbc0a3931e5228c7836add09cf8ff3cefeb3d7e610deb458ec871a9da86bae1ffc029f5aba41e725786ecb7f93ad2670303bf2db27b8a
account store in file: [bin/accounts/pyaccount.keystore]
**** please remember your password !!! *****

# Add permission management permission for account 0x95198B93705e394a916579e048c8A32DdFB900f7
$ ./console.py grantPermissionManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantPermissionManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantPermissionManager
     >> status: 0x0
     >> transactionHash: 0xdac11796dcfb663842a13333976626d844527605edb5bf9daadcfa28236bb5c8
     >> gasUsed: 0x6698
     >> grantPermissionManager succ, output: 1

```
### listPermissionManager

List the account information with the permission management function:

```bash
# List all rights management account information
$ ./console.py listPermissionManager
INFO >> user input : ['listPermissionManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 9
```

### grantUserTableManager
Grant the given user table permissions to the specified user:
```
./console.py grantUserTableManager [tableName] [account_adddress]
```

```eval_rst
.. note::
    Before granting user table permissions to a user, ensure that the user table exists. You can use the "createTable" command to create the user table.
```
Parameters include:
- tableName: User table name
- account _ address: Authorized user account address

```bash
# Create user table t _ test
$./console.py createTable t_test "key" "value1, value2, value3"
INFO >> user input : ['createTable', 't_test', 'key', 'value1, value2, value3']
INFO >> createTable
     >> status: 0x0
     >> transactionHash: 0xfbc10c0d9e4652f59655903e5ba772bb7f127e8e9de12be250d487f0ff9c5268
     >> gasUsed: 0x6098
     >> createTable succ, output: 0

# Management function of user table t _ test for account 0x95198B93705e394a916579e048c8A32DdFB900f7
$ ./console.py grantUserTableManager t_test 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantUserTableManager', 't_test', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> table t_test
     >> key_field: key
     >> value_field: value1,value2,value3
INFO >> grantUserTableManager
     >> status: 0x0
     >> transactionHash: 0x2b9640f02db7afa839b5bdf158cca33a96a9718dc2e80f2c7b8af6100f6f8e92
     >> gasUsed: 0x6398
     >> grantUserTableManager succ, output: 1
```

### listUserTableManager
Lists the account information that has administrative permissions on the specified user table:
```
./console.py listUserTableManager [tableName]
```
Parameters include:
- tableName: User Table

```bash
# View the management information of the user table t _ test
$./console.py listUserTableManager t_test
INFO >> user input : ['listUserTableManager', 't_test']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 11
```

### grantNodeManager
Grant node management permissions to the specified account:
```
./console.py grantNodeManager [account_adddress]
```
Parameters include:
- account _ address: Authorized user account address

```bash
# Add node management for account 0x95198B93705e394a916579e048c8A32DdFB900f7
$ ./console.py grantNodeManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantNodeManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantNodeManager
     >> status: 0x0
     >> transactionHash: 0x3a8839bfdfefcd3fff2678f91f231d44d8d442e40fc7f3af726daec624ba80c8
     >> gasUsed: 0x65d8
     >> grantNodeManager succ, output: 1

```

### listNodeManager

List the account information with the node management function:

```bash
$ ./console.py listNodeManager
INFO >> user input : ['listNodeManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 12
```

### grantCNSManager
Grant CNS administrative privileges to the specified account:
```
./console.py grantCNSManager [account_adddress]
```
Parameters include:
- account _ address: Authorized user account address

```bash
# Add CNS administrative rights for account 0x95198B93705e394a916579e048c8A32DdFB900f7
$ ./console.py grantCNSManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantCNSManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantCNSManager
     >> status: 0x0
     >> transactionHash: 0x4a112be9f582fb1ae98ae9d6a84706930f4ab3523b45722cc4bf08341397dd1e
     >> gasUsed: 0x6458
     >> grantCNSManager succ, output: 1
```

### listCNSManager

List account information with CNS administrative privileges

```bash
$ ./console.py listCNSManager

INFO >> user input : ['listCNSManager']

----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 13
```

### grantSysConfigManager

Grant the system configuration modification permission to the specified account:
```
./console.py grantSysConfigManager [account_adddress]
```
Parameters include:
- account _ address: Authorized user account address

```bash
# Add system configuration permissions for account 0x95198B93705e394a916579e048c8A32DdFB900f7
$ ./console.py grantSysConfigManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantSysConfigManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantSysConfigManager
     >> status: 0x0
     >> transactionHash: 0xf6ec040686496256a8c01233d1339ee147551f6a2dfcbd7bd6d7647f240f1411
     >> gasUsed: 0x6518
     >> grantSysConfigManager succ, output: 1
```

### listSysConfigManager

List the account information that has the system configuration modification permission:

```bash
$ ./console.py listSysConfigManager
INFO >> user input : ['listSysConfigManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 14
```

### grantDeployAndCreateManager

Grant permissions to deploy and create tables to the specified account:
```
./console.py grantDeployAndCreateManager [account_adddress]
```
Parameters include:
- account _ address: Authorized user account address

```bash
# Add create table and deploy contract permissions for account 0x95198B93705e394a916579e048c8A32DdFB900f7
$./console.py grantDeployAndCreateManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantDeployAndCreateManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantDeployAndCreateManager
     >> status: 0x0
     >> transactionHash: 0xf60452a12d5346fa641bca6bee662c261fa0c67ef90aca3944cdb29a5803c625
     >> gasUsed: 0x6518
     >> grantDeployAndCreateManager succ, output: 1
```

### listDeployAndCreateManager

List the account information for which the contract and user tables were created:
```bash
$ ./console.py listDeployAndCreateManager
INFO >> user input : ['listDeployAndCreateManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 15
```

### revokeUserTableManager

Revoke the write permission of the specified user on the specified user table:

```
./console.py revokeUserTableManager [tableName] [account_adddress]
```
Parameters include:
- tableName: the name of the table that the specified user is prohibited from writing to
- account _ address: Address of the account whose permission has been revoked

```bash
# Revoke the control permission of account 0x95198B93705e394a916579e048c8A32DdFB900f7 on user table t _ test
$ ./console.py revokeUserTableManager t_test 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeUserTableManager', 't_test', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeUserTableManager
     >> status: 0x0
     >> transactionHash: 0xc7ffbd0f79bfe06f43c603afde5997f9127a9fe499338362e64c653a593ded36
     >> gasUsed: 0x6398
     >> revokeUserTableManager succ, output: 1
```

### revokeDeployAndCreateManager
Revoke the permission of the specified account to create tables and deploy contracts:
```
./console.py revokeDeployAndCreateManager [account_adddress]
```
Parameters include:
- account _ address: Address of the account whose permission has been revoked

```bash
# Revoke account 0x95198B93705e394a916579e048c8A32DdFB900f7 Deploy and create table permissions
$ ./console.py revokeDeployAndCreateManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeDeployAndCreateManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeDeployAndCreateManager
     >> status: 0x0
     >> transactionHash: 0xeac82f3464093f0659eb6412c39599d51b64082401ac43df9d7670cf17882f78
     >> gasUsed: 0x6518
     >> revokeDeployAndCreateManager succ, output: 1
```

### revokeNodeManager
Revoke the node management permission of the specified account:
```
./console.py revokeNodeManager [account_adddress]
```
Parameters include:
- account _ address: Address of the account whose permission has been revoked

```bash
# Revoke the account 0x95198B93705e394a916579e048c8A32DdFB900f7 node management permission
$ ./console.py revokeNodeManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeNodeManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeNodeManager
     >> status: 0x0
     >> transactionHash: 0xc9f3799dc81a146f562fe10b493d14920676a8e49a6de94e7b4b998844198342
     >> gasUsed: 0x65d8
     >> revokeNodeManager succ, output: 1
```

### revokeCNSManager
Revoke the CNS management authority of the specified account:
```
./console.py revokeCNSManager [account_adddress]
```
Parameters include:
- account _ address: Address of the account whose permission has been revoked

```bash
# Revoke account 0x95198B93705e394a916579e048c8A32DdFB900f7 CNS administrative privileges
$ ./console.py revokeCNSManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeCNSManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeCNSManager
     >> status: 0x0
     >> transactionHash: 0xa5aa6d115875156512af8c9974e353336e00bc3b9c2f2c2e21749d728e45abb4
     >> gasUsed: 0x6458
     >> revokeCNSManager succ, output: 1
```

### revokeSysConfigManager
Revoke the permission of the specified account to modify the system configuration:
```
./console.py revokeSysConfigManager [account_adddress]
```
Parameters include:
- account _ address: Address of the account whose permission has been revoked

```bash
# Revoke account 0x95198B93705e394a916579e048c8A32DdFB900f7 system table management permissions
$ ./console.py revokeSysConfigManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeSysConfigManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeSysConfigManager
     >> status: 0x0
     >> transactionHash: 0xfaffc25a4b111cfdaddca323d8b125c553c5e8f83b85fae1de21a6bc3bef792a
     >> gasUsed: 0x6518
     >> revokeSysConfigManager succ, output: 1
```

### revokePermissionManager
Revoke the permission of the specified account management permission:
```
./console.py revokePermissionManager [account_adddress]
```
Parameters include:
- account _ address: Address of the account whose permission has been revoked

```bash
# Revoke account 0x95198B93705e394a916579e048c8A32DdFB900f7 permission management permission
$ ./console.py revokePermissionManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokePermissionManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokePermissionManager
     >> status: 0x0
     >> transactionHash: 0xa9398d4de7a3e86238a48bdbf5e053c61bc57ccd1aa57ebaa3c070bc47ea0f98
     >> gasUsed: 0x6698
     >> revokePermissionManager succ, output: 1
```

## RPC

You can use the Python SDK to query node information. Currently, the Python SDK supports the following query commands:
### getNodeVersion

To obtain the node version information:

```bash
$ ./console.py getNodeVersion

INFO >> user input : ['getNodeVersion']

INFO >> getNodeVersion
     >> {
    "Build Time":"20200619 06:32:10",
    "Build Type":"Linux/clang/Release",
    "Chain Id":"1",
    "FISCO-BCOS Version":"2.5.0",
    "Git Branch":"HEAD",
    "Git Commit Hash":"72c6d770e5cf0f4197162d0e26005ec03d30fcfe",
    "Supported Version":"2.5.0"
}
```

### getBlockNumber

Get the latest block height of the node:

```bash
$ ./console.py getBlockNumber
INFO >> user input : ['getBlockNumber']
INFO >> getBlockNumber
     >> 21
```

### getPbftView

To get the node consensus view:
```bash
$ ./console.py getPbftView

INFO >> user input : ['getPbftView']

INFO >> getPbftView
     >> 0x34e
```
### getSealerList

Get the current consensus node list:
```bash
$ ./console.py getSealerList

INFO >> user input : ['getSealerList']

INFO >> getSealerList
     >> 3ad90ae5a10b8d88c9936492a37f564884e82b176e91f5e2e2f75a347be87212aac148ee7fa2060be8a790eaa3d44a299f94ba3d97adfa45526346902d64e0af
     >> 6bd07f2f8180ac9d56b40ff9977ba528a4f65e83d4ca95a0537e12f6638f78848e0765cbee0cb2b5f581d7eb5027d189f8691bfa92186bbf51deefd467339b6f
     >> b8783cfe3c073a532e9cbc47978d45a187da179d2fef4a85990c3b286d69d1afcd061de1b8cba07a59819d94f021db1e7707304908024f80e5830298e3829b82
```
### getObserverList

Get the list of observer nodes:
```bash
$ ./console.py getObserverList
INFO >> user input : ['getObserverList']
INFO >> getObserverList
     >> 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4
```
### getConsensusStatus

To obtain node consensus status information:
```bash
$ ./console.py getConsensusStatus

INFO >> user input : ['getConsensusStatus']

INFO >> getConsensusStatus
     >> {
    "accountType": 1,
    "allowFutureBlocks": true,
    "cfgErr": false,
    "connectedNodes": 3,
    "consensusedBlockNumber": 22,
    "currentView": 904,
    "groupId": 1,
    "highestblockHash": "0x2aa73c33c054eb168dd1cb5d62cd211c780731c3fe40333be0f32069568d0083",
    "highestblockNumber": 21,
    "leaderFailed": false,
    "max_faulty_leader": 0,
    "nodeId": "b8783cfe3c073a532e9cbc47978d45a187da179d2fef4a85990c3b286d69d1afcd061de1b8cba07a59819d94f021db1e7707304908024f80e5830298e3829b82",
    "nodeNum": 3,
    "node_index": 2,
    "omitEmptyBlock": true,
    "protocolId": 65544,
    ... omit lines...
}
```
### getSyncStatus

To obtain node synchronization status information:
```bash
$ ./console.py getSyncStatus

INFO >> user input : ['getSyncStatus']

INFO >> getSyncStatus
     >> {
    "blockNumber": 21,
    "genesisHash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
    "isSyncing": false,
    "knownHighestNumber": 21,
    "knownLatestHash": "2aa73c33c054eb168dd1cb5d62cd211c780731c3fe40333be0f32069568d0083",
    "latestHash": "0x2aa73c33c054eb168dd1cb5d62cd211c780731c3fe40333be0f32069568d0083",
    "nodeId": "b8783cfe3c073a532e9cbc47978d45a187da179d2fef4a85990c3b286d69d1afcd061de1b8cba07a59819d94f021db1e7707304908024f80e5830298e3829b82",
    "peers": [
        {
            "blockNumber": 21,
            "genesisHash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
            "latestHash": "0x2aa73c33c054eb168dd1cb5d62cd211c780731c3fe40333be0f32069568d0083",
            "nodeId": "12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4"
        },
        {
            "blockNumber": 21,
            "genesisHash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
            "latestHash": "0x2aa73c33c054eb168dd1cb5d62cd211c780731c3fe40333be0f32069568d0083",
            "nodeId": "3ad90ae5a10b8d88c9936492a37f564884e82b176e91f5e2e2f75a347be87212aac148ee7fa2060be8a790eaa3d44a299f94ba3d97adfa45526346902d64e0af"
        },
        {
            "blockNumber": 21,
            "genesisHash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
            "latestHash": "0x2aa73c33c054eb168dd1cb5d62cd211c780731c3fe40333be0f32069568d0083",
            "nodeId": "6bd07f2f8180ac9d56b40ff9977ba528a4f65e83d4ca95a0537e12f6638f78848e0765cbee0cb2b5f581d7eb5027d189f8691bfa92186bbf51deefd467339b6f"
        }
    ],
    "protocolId": 65545,
    "txPoolSize": "0"
}
```
### getPeers

Get a list of node connections:
```bash
$ ./console.py getPeers
INFO >> user input : ['getPeers']
INFO >> getPeers
     >> {
    "Agency": "agency",
    "IPAndPort": "127.0.0.1:30301",
    "Node": "node1",
    "NodeID": "12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4",
    "Topic": []
}
     >> {
    "Agency": "agency",
    "IPAndPort": "127.0.0.1:30302",
    "Node": "node2",
    "NodeID": "6bd07f2f8180ac9d56b40ff9977ba528a4f65e83d4ca95a0537e12f6638f78848e0765cbee0cb2b5f581d7eb5027d189f8691bfa92186bbf51deefd467339b6f",
    "Topic": []
}
     >> {
    "Agency": "agency",
    "IPAndPort": "127.0.0.1:30303",
    "Node": "node3",
    "NodeID": "3ad90ae5a10b8d88c9936492a37f564884e82b176e91f5e2e2f75a347be87212aac148ee7fa2060be8a790eaa3d44a299f94ba3d97adfa45526346902d64e0af",
    "Topic": []
}
```

### getGroupPeers

To obtain the connection information of nodes in a group:
```bash
$ ./console.py getGroupPeers
INFO >> user input : ['getGroupPeers']
INFO >> getGroupPeers
     >> 3ad90ae5a10b8d88c9936492a37f564884e82b176e91f5e2e2f75a347be87212aac148ee7fa2060be8a790eaa3d44a299f94ba3d97adfa45526346902d64e0af
     >> 6bd07f2f8180ac9d56b40ff9977ba528a4f65e83d4ca95a0537e12f6638f78848e0765cbee0cb2b5f581d7eb5027d189f8691bfa92186bbf51deefd467339b6f
     >> b8783cfe3c073a532e9cbc47978d45a187da179d2fef4a85990c3b286d69d1afcd061de1b8cba07a59819d94f021db1e7707304908024f80e5830298e3829b82
     >> 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4
```

### getNodeIDList

Obtain the list of all networking nodes in the blockchain:
```bash
$ ./console.py getNodeIDList
INFO >> user input : ['getNodeIDList']
INFO >> getNodeIDList
     >> b8783cfe3c073a532e9cbc47978d45a187da179d2fef4a85990c3b286d69d1afcd061de1b8cba07a59819d94f021db1e7707304908024f80e5830298e3829b82
     >> 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4
     >> 6bd07f2f8180ac9d56b40ff9977ba528a4f65e83d4ca95a0537e12f6638f78848e0765cbee0cb2b5f581d7eb5027d189f8691bfa92186bbf51deefd467339b6f
     >> 3ad90ae5a10b8d88c9936492a37f564884e82b176e91f5e2e2f75a347be87212aac148ee7fa2060be8a790eaa3d44a299f94ba3d97adfa45526346902d64e0af
```

### getGroupList

To get a list of groups:
```bash
$ ./console.py getGroupList
INFO >> user input : ['getGroupList']
INFO >> getGroupList
     >> 1
```

### getPendingTransactions

To obtain information about transactions in the transaction pool that have not yet been linked:
```bash
$ ./console.py getPendingTransactions
INFO >> user input : ['getPendingTransactions']
INFO >> getPendingTransactions
     >> Empty Set
```

### getPendingTxSize

Get the number of transactions in the transaction pool that have not yet been chained:
```bash
$ ./console.py getPendingTxSize
INFO >> user input : ['getPendingTxSize']
INFO >> getPendingTxSize
     >> 0x0
```

### getTotalTransactionCount

Get the number of transactions that have been chained:
```bash
$ ./console.py getTotalTransactionCount
INFO >> user input : ['getTotalTransactionCount']
INFO >> getTotalTransactionCount
     >> {
    "blockNumber": "0x16",
    "failedTxSum": "0x0",
    "txSum": "0x16"
     }
```

### getBlockByNumber

Query blocks based on block height:
```bash
$ ./console.py getBlockByNumber [block_number] [True/False]
```
Parameters include:
- block _ number: block height
- True/False: Optional. True indicates that the returned block information contains specific transaction information.；False indicates that the returned block contains only the transaction hash


```bash
$ ./console.py getBlockByNumber 0

INFO >> user input : ['getBlockByNumber', '0']

INFO >> getBlockByNumber
     >> {
    "dbHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "extraData": [
        "0x312d62383738336366653363303733613533326539636263343739373864
        ... omit lines...
        7652d313030302d333030303030303030"
    ],
    "gasLimit": "0x0",
    "gasUsed": "0x0",
    "hash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
    "logsBloom": "0x00000000... several lines omitted... 0000000000000000000000",
    "number": "0x0",
    "parentHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "receiptsRoot": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "sealer": "0x0",
    "sealerList": [],
    "stateRoot": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "timestamp": "0x16c61113388",
    "transactions": [],
    "transactionsRoot": "0x0000000000000000000000000000000000000000000000000000000000000000"
}
```

### getBlockHashByNumber

Query block hash based on block height:

```bash
$ ./console.py getBlockHashByNumber 0
INFO >> user input : ['getBlockHashByNumber', '0']
INFO >> getBlockHashByNumber
     >> 0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04
```
### getBlockByHash

Obtain block information based on the block hash:

```
$ ./console.py getBlockByHash [block_hash] [True/False]
```
Parameters include:
- block _ hash: block hash
- True/False: Optional. True indicates that the returned block contains transaction specific information.；False indicates that the block returned contains only the transaction hash

```bash
$ ./console.py getBlockByHash 0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04
INFO >> user input : ['getBlockByHash', '0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04']
INFO >> getBlockByHash
     >> {
    "extraData": [
        "0x312d623... some omitted... 3030303030"
    ],
    "gasLimit": "0x0",
    "gasUsed": "0x0",
    "hash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
    "logsBloom": "0x0000... Omit some... 000000",
    "number": "0x0",
    "parentHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "sealer": "0x0",
    "sealerList": [],
    "stateRoot": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "timestamp": "0x16c61113388",
    "transactions": [],
    "transactionsRoot": "0x0000000000000000000000000000000000000000000000000000000000000000"
}
```

### getCode

Get the binary encoding of the specified contract:

```bash
$ ./console.py getCode 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
INFO >> user input : ['getCode', '0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce']
INFO >> getCode
     > > 0x60806040526... some omitted... a40029
```
### getTransactionByHash

Get transaction information based on transaction hash:
```
./console.py getTransactionByHash [hash] [contract_name]
```

Parameters include:
- hash: Transaction Hash
- contract _ name: optional. The name of the contract related to the transaction. If this parameter is entered, the specific content of the transaction will be parsed and returned.


```bash
$ ./console.py getTransactionByHash 0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91
INFO >> user input : ['getTransactionByHash', '0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91']
INFO >> getTransactionByHash
     >> {
    "blockHash": "0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d",
    "blockNumber": "0x1",
    "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
    "gas": "0x1c9c380",
    "gasPrice": "0x1c9c380",
    "hash": "0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91",
    "input": "0x60806... some omitted... ddd81c4a40029",
    "nonce": "0x2b2350c8",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionIndex": "0x0",
    "value": "0x0"
}
```
### getTransactionReceipt

Get transaction receipt information based on transaction hash:
```bash
./console.py getTransactionReceipt [hash] [contract_name]
```
Parameters include:
- hash: transaction hash
- contract _ name: optional. The contract name related to the transaction. If this parameter is entered, the specific content of the transaction and receipt will be parsed.

```bash
$ ./console.py getTransactionReceipt 0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91
INFO >> user input : ['getTransactionReceipt', '0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91']
INFO >> getTransactionReceipt
     >> {
    "blockHash": "0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d",
    "blockNumber": "0x1",
    "contractAddress": "0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce",
    "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
    "gasUsed": "0x44ab3",
    "input": "0x608060405234... some omitted... d9acf16e2fc2d570d491ddd81c4a40029",
    "logs": [],
    "logsBloom": "0x00000... Omit some... 00000000000",
    "output": "0x",
    "status": "0x0",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionHash": "0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91",
    "transactionIndex": "0x0"
}
```
### getTransactionByBlockHashAndIndex

Query transaction information based on block hash and transaction index:

```bash
./console.py getTransactionByBlockHashAndIndex [blockHash] [transactionIndex] [contract_name]
```
Parameters include:
- blockHash: Block hash of the transaction in
- transactionIndex: transaction index
- contract _ name: optional. The name of the contract related to the transaction. If this parameter is entered, the specific content of the transaction will be parsed and returned.

```bash
$  ./console.py getTransactionByBlockHashAndIndex 0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d 0

INFO >> user input : ['getTransactionByBlockHashAndIndex', '0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d', '0']

INFO >> getTransactionByBlockHashAndIndex
     >> {
    "blockHash": "0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d",
    "blockNumber": "0x1",
    "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
    "gas": "0x1c9c380",
    "gasPrice": "0x1c9c380",
    "hash": "0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91",
    "input": "0x6080... some omitted... 4a40029",
    "nonce": "0x2b2350c8",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionIndex": "0x0",
    "value": "0x0"
}
```

### getTransactionByBlockNumberAndIndex

Query transaction information based on block height and transaction index:
```
$ ./console.py getTransactionByBlockNumberAndIndex [blockNumber] [transactionIndex] [contract_name]
```
Parameters include:
- blockNumber: Exchange in block high
- transactionIndex: transaction index
- contract _ name: optional. The name of the contract related to the transaction. If this parameter is entered, the specific content of the transaction will be parsed and returned.

 ```bash
$ ./console.py getTransactionByBlockNumberAndIndex 1 0
INFO >> user input : ['getTransactionByBlockNumberAndIndex', '1', '0']
INFO >> getTransactionByBlockNumberAndIndex
     >> {
    "blockHash": "0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d",
    "blockNumber": "0x1",
    "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
    "gas": "0x1c9c380",
    "gasPrice": "0x1c9c380",
    "hash": "0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91",
    "input": "0x608060... some omitted... a40029",
    "nonce": "0x2b2350c8",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionIndex": "0x0",
    "value": "0x0"
}
```
### getSystemConfigByKey

To obtain system configuration information:

```bash
# Get the maximum number of transactions that can be packaged in a block
$ ./console.py getSystemConfigByKey tx_count_limit
INFO >> user input : ['getSystemConfigByKey', 'tx_count_limit']
INFO >> getSystemConfigByKey tx_count_limit
     >> 500
# Get system gas limits
$ ./console.py getSystemConfigByKey  tx_gas_limit
INFO >> user input : ['getSystemConfigByKey', 'tx_gas_limit']
INFO >> getSystemConfigByKey tx_gas_limit
     >> 400000000
```
