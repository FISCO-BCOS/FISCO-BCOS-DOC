# Console Command List

Tags: "console" "console commands" "command line interactive tools"

---------

```eval_rst
.. important::
    - "Console" only supports FISCO BCOS version 3.x, based on 'Java SDK <.. / sdk / java _ sdk / index.html >' _ implementation。
    - You can use the command. "/ start.sh--version "View the current console version
```

## Console Command Structure

Console commands consist of two parts, directives and directive-related parameters:

- **Directive**: Instructions are operational commands that are executed, including instructions to query blockchain-related information, deploy contracts, and invoke contracts, some of which call JSON-RPC interface, so with JSON-RPC interface with same name。
  **Tips: commands can be completed using the tab key, and support the up and down keys to display historical input commands。**

- **Instruction-related parameters**: The parameters required by the instruction call interface, instructions and parameters, and parameters and parameters are separated by spaces, and JSON.-For detailed explanation of input parameters and obtaining information fields of RPC interface commands with the same name, refer to [JSON-RPC API](../../develop/api.md)。

### Console Common Commands

### Contract Related Orders

- Common deployment and invocation contracts
  - Deployment contract: [deploy](./console_commands.html#deploy)
  - Call Contract: [call](./console_commands.html#call)

### Other Commands

- Query the block height: [getBlockNumber](./console_commands.html#getblocknumber)
- Query the consensus node list: [getSealerList](./console_commands.html#getsealerlist)
- Query transaction receipt information: [getTransactionReceipt](./console_commands.html#gettransactionreceipt)
- Toggle Group: [switch](./console_commands.html#switch)

### Shortcut Keys

- `Ctrl+A ': Move the cursor to the beginning of the line
- `Ctrl+D ': Exit the console
- `Ctrl+E ': cursor moves to end of line
- `Ctrl+R ': Search for input history commands
- &uarr;Browse Forward History command
- &darr;: browse back history command

### Console Response

When a console command is launched, the console obtains the command execution result and displays the execution result on the terminal. The execution result is divided into two categories:

- **correct result:** The command returns the correct execution result, which is returned as a string or json.。
- **Error result:** The command returns the execution result of the error, which is returned as a string or json.。
  - Console command calls JSON-Error code when RPC interface [refer here](../../develop/api.html#rpc)。
  - Error code when the command of the console calls the Precompiled Service interface [refer to here](../../develop/api.html#id5)。

## Console Basic Commands

### 1. help

Run help or h to view all commands in the console.。

```shell
[group0]: /apps> help
* help([-h, -help, --h, --H, --help, -H, h])  Provide help information
---------------------------Basic Command----------------------------
* clearNodeName                             Clear default node name to empty.
* quit([quit, q, exit])                     Quit console
* getNodeName                               Get default node name in this client.
* switch([s])                               Switch to a specific group by name
* setNodeName                               Set default node name to send request.
---------------------------Contract Operation----------------------------
* call                                      Call a contract by a function and parameters
* deploy                                    Deploy a contract on blockchain
* getCode                                   Query code at a given address
* getDeployLog                              Query the log of deployed contracts
* listAbi                                   List functions and events info of the contract.
* listDeployContractAddress                 List the contractAddress for the specified contract
---------------------------Blockchain Status Query----------------------------
* getBlockByHash                            Query information about a block by hash
* getBlockByNumber                          Query information about a block by number
* getBlockHashByNumber                      Query block hash by block number.
* getBlockHeaderByHash                      Query information about a block header by hash
* getBlockHeaderByNumber                    Query information about a block header by block number
* getBlockNumber                            Query the number of most recent block
* getPeers                                  Query peers currently connected to the client
* getPendingTxSize                          Query pending transactions size
* getSyncStatus                             Query sync status
* getSystemConfigByKey                      Query a system config value by key
* getTotalTransactionCount                  Query total transaction count
* getTransactionByHash                      Query information about a transaction requested by transaction hash
* getTransactionByHashWithProof             Query the transaction and transaction proof by transaction hash
* getTransactionReceipt                     Query the receipt of a transaction by transaction hash
* getTransactionReceiptByHashWithProof      Query the receipt and transaction receipt proof by transaction hash
* setSystemConfigByKey                      Set a system config value by key
---------------------------Consensus Operation----------------------------
* addObserver                               Add an observer node
* addSealer                                 Add a sealer node
* getConsensusStatus                        Query consensus status
* getObserverList                           Query nodeId list for observer nodes.
* getPbftView                               Query the pbft view of node
* getSealerList                             Query nodeId list for sealer nodes
* removeNode                                Remove a node
* setConsensusWeight                        Set consensus weight for the specified node
---------------------------BFS Operation----------------------------
* cd                                        Change dir to given path.
* ln                                        Create a link to access contract.
* ls                                        List resources in given path.
* mkdir                                     Create dir in given path.
* pwd                                       Show absolute path of working directory name
* tree                                      List contents of directories in a tree-like format.
---------------------------CRUD Contract Operation----------------------------
* alter                                     Alter table columns by sql
* create                                    Create table by sql
* delete                                    Remove records by sql
* desc                                      Description table information
* insert                                    Insert records by sql
* select                                    Select records by sql
* update                                    Update records by sql
---------------------------Group Info Query----------------------------
* getGroupInfo                              Query the current group information.
* getGroupInfoList                          Get all groups info
* getGroupList                              List all group list
* getGroupNodeInfo                          Get group node info
* getGroupPeers                             List all group peers
---------------------------Account Operation----------------------------
* getCurrentAccount                         Get the current account info
* listAccount                               List the current saved account list
* loadAccount                               Load account for the transaction signature
* newAccount                                Create account
---------------------------Sharding Operation----------------------------
* getContractShard                          Get a contract's belonging shard.
* linkShard                                 Add a contract to a shard.
* makeShard                                 Make a shard.
---------------------------Balance Precompiled Operation----------------------------
* addBalance                                Add balance to account. Only balanceGovernor can use it.
* getBalance                                Get balance of the account
* listBalanceGovernor                       List all registered balanceGovernor
* registerBalanceGovernor                   Register an account as balanceGovernor. Only Governor accounts can use it.
* subBalance                                Sub balance from account. Only balanceGovernor can use it
* transferBalance                           Transfer balance from one account to another. Only balanceGovernor can use it
* unregisterBalanceGovernor                 Unregister an account from balanceGovernor. Only governor account can use it
---------------------------------------------------------------------------------------------
```

**Note:**

- Help shows the meaning of each command is: command command function description
- View the instructions for using a specific command and enter the command-h or\--Help View。For example:

```shell
[group0]: /apps> getBlockByNumber -h
Query information about a block by block number.
Usage: 
getBlockByNumber blockNumber [boolean]
* blockNumber -- Integer of a block number, from 0 to 2147483647.
* boolean -- (optional) If true it returns only the hashes of the transactions, if false then return the full transaction objects.
```

### 2. switch

Run switch or s to switch to the specified group。The group number appears in front of the command prompt。

```shell
[group0]: /apps>  switch group2
Switched to group2.

[group2]>
```

### 3. quit

Run quit, q, or exit to exit the console。

```shell
quit
```

## Contract Operation Order

### 1. deploy

Deployment contract。(HelloWorld contract and KVTableTest are provided by default for sample use)

**New Liquid Deployment Method**: When the "is _ wasm = true" option is enabled in the configuration of the connected node, the console can be automatically started for use. For specific configuration items, please refer to: [Node Configuration](../../tutorial/air/config.md)

**Solidity deployment parameters:**

- Contract path: the path of the contract file, which supports relative path, absolute path, and default path.。When the user enters a file name, the file is obtained from the default directory, which is: 'contracts / solidity ', for example: HelloWorld。
- Enable static analysis: optional, the default is off。If enabled, static analysis of parallel field conflict domains is enabled to accelerate parallel contract execution。Static analysis takes a long time. Please be patient。

```shell
# Deploy HelloWorld contract, default path
[group0]: /apps> deploy HelloWorld
contract address:0xc0ce097a5757e2b6e189aa70c7d55770ace47767

# Deploy HelloWorld contract, relative path
[group0]: /apps> deploy contracts/solidity/HelloWorld.sol
contract address:0x4721D1A77e0E76851D460073E64Ea06d9C104194

# Deploy HelloWorld contract, absolute path
[group0]: /apps> deploy ~/fisco/console/contracts/solidity/HelloWorld.sol
contract address:0x85517d3070309a89357c829e4b9e2d23ee01d881

# Turn on static analysis options
[group0]: /apps> deploy HelloWorld -p
contract address: 0x0102e8B6fC8cdF9626fDdC1C3Ea8C1E79b3FCE94
```

**Note:**

- To deploy a contract written by a user, you can place the Solidity contract file in the 'contracts / solidity /' directory of the console root directory, and then deploy it.。Press tab to search for contract names in the 'contracts / consolidation /' directory。
- If the contract to be deployed references another contract or library, the reference format is' import '."./XXX.sol";`。The relevant introduced contracts and library libraries are placed in the 'contracts / consolidation /' directory。
- If the contract references a library, the name of the library file must start with the string 'Lib' to distinguish between a normal contract and a library file.。library library files cannot be deployed and called separately。

**Liquid deployment parameters:**

- Binary file folder path: cargo-Both the wasm file and the ABI file compiled by liquid must be placed in the same path. Absolute paths and relative paths are supported.
- Deploy BFS path: Path name in BFS file system
- Deployment construction parameters: Construction parameters required for deployment

```shell
# Deploy HelloWorld contract, relative path
[group0]: /apps> deploy asset asset_test
transaction hash: 0x7498487dbf79378b5b50c4e36c09ea90842a343de307ee66d560d005d3c40ccb
contract address: /asset_test
currentAccount: 0x52d8001791a646d7e0d63e164731b8b7509c8bda
```

**deploy with BFS:**

Supports creating an alias in BFS when deploying a contract, using the parameter'-l 'Link the deployed address of HelloWorld to the / apps / hello / v1 directory:

```shell
[group0]: /apps> deploy -l ./hello/v1 HelloWorld
deploy contract with link, link path: /apps/hello/v1
transaction hash: 0x1122b7933e3468d007eea4f49c7e5182083f59b739dc06e40ee621129fed04e8
contract address: 0xcceef68c9b4811b32c75df284a1396c7c5509561
currentAccount: 0x7c6bb210ac67412f38ff850330b2dcd294437498
link path: /apps/hello/v1

[group0]: /apps> ls ./hello/v1
v1 -> cceef68c9b4811b32c75df284a1396c7c5509561
```

### 2. call

Run call, call contract。

**New Liquid Deployment Method**: When the "is _ wasm = true" option is enabled in the configuration of the connected node, the console can be automatically started for use. For specific configuration items, please refer to: [Node Configuration](../../tutorial/air/config.md)

**Solidity call parameters:**

- Contract path: the path of the contract file, which supports relative path, absolute path, and default path.。When the user enters a file name, the file is obtained from the default directory, which is: `contracts/solidity`。
- Contract Address: Address obtained from the deployment contract。
- Contract Interface Name: The name of the contract interface to call。
- Parameters: Determined by contract interface parameters。**Parameters are separated by spaces；Array parameters need to be bracketed, such as [1,2,3], the array is a string or byte type, double quotation marks, such as ["alice," "bob"], note that the array parameters do not have spaces；Boolean type is true or false。**

```shell
# Call the get interface of HelloWorld to get the name string
[group0]: /apps> call HelloWorld 0x4721D1A77e0E76851D460073E64Ea06d9C104194 get 
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, World!)
---------------------------------------------------------------------------------------------

# Call the set interface of HelloWorld to set the name string
[group0]: /apps> call HelloWorld 0x4721D1A77e0E76851D460073E64Ea06d9C104194 set "Hello, FISCO BCOS 3.0" 
transaction hash: 0x622808fb47e765576e444175793e74230ac4cd056b18d578d23442eb5a0102a0
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
---------------------------------------------------------------------------------------------
Event logs
Event: {}

# Call the get interface of HelloWorld to obtain the name string and check whether the setting takes effect.
[group0]: /apps> call HelloWorld 0x4721D1A77e0E76851D460073E64Ea06d9C104194 get 
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, FISCO BCOS 3.0)
---------------------------------------------------------------------------------------------

```

**Liquid parameter:**

- Contract Path: The pathname in the BFS file system that was populated when the contract was deployed。
- Contract Interface Name: The name of the contract interface to call。
- Parameters: Determined by contract interface parameters。**Parameters are separated by spaces；Array parameters need to be bracketed, such as [1,2,3], the array is a string or byte type, double quotation marks, such as ["alice," "bob"], note that the array parameters do not have spaces；Boolean type is true or false。**

```shell
# Call the contract in the / apps / asset _ test path to register a new asset asset1
[group0]: /apps> call ./apps/asset_test register asset1 10000
transaction hash: 0xf9289064053eed8a71b4af43fd793dc1cd703d75f74b19e302bc4456e523fcf2
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:1
Return types: (int16)
Return values:(0)
---------------------------------------------------------------------------------------------
Event logs
Event: {}

# Call the contract in the / apps / asset _ test path to query asset asset1
[group0]: /apps> call ./apps/asset_test select asset1
transaction hash: 0x68867d2e55a1c16c96ec31f96a9c913c5995b4cb9ff8985b016a18b80e9c02cb
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:2
Return types: (bool, uint128)
Return values:(true, 10000)
---------------------------------------------------------------------------------------------
Event logs
Event: {}

```

**Call with BFS:**

You can call a link file created in the BFS directory. The call gesture is similar to calling a normal contract.。

```shell
[group0]: /apps> call ./hello/v1 set "Hello, BFS."
transaction hash: 0x0b39db7b23aebe78b50a5f45da0e94381a4f4495c7286ab3039f9a761a3cc655
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
---------------------------------------------------------------------------------------------
Event logs
Event: {}
```

### 3. getCode

Run getCode to query the contract binary code based on the contract address.。
Parameters:

- Contract address: 0x contract address(Deploy the contract to get the contract address)。

```shell
[group0]: /apps>  getCode 0x97b8c19598fd781aaeb53a1957ef9c8acc59f705
0x60606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100465780636d4ce63c14610066575bfe5b341561004e57fe5b610064600480803590602001909190505061008c565b005b341561006e57fe5b61007661028c565b6040518082815260200191505060405180910390f35b8060006001015410806100aa57506002600101548160026001015401105b156100b457610289565b806000600101540360006001018190555080600260010160008282540192505081905550600480548060010182816100ec919061029a565b916000526020600020906004020160005b608060405190810160405280604060405190810160405280600881526020017f32303137303431330000000000000000000000000000000000000000000000008152508152602001600060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815250909190915060008201518160000190805190602001906101ec9291906102cc565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b600060026001015490505b90565b8154818355818115116102c7576004028160040283600052602060002091820191016102c6919061034c565b5b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061030d57805160ff191683800117855561033b565b8280016001018555821561033b579182015b8281111561033a57825182559160200191906001019061031f565b5b50905061034891906103d2565b5090565b6103cf91905b808211156103cb57600060008201600061036c91906103f7565b6001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556002820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600382016000905550600401610352565b5090565b90565b6103f491905b808211156103f05760008160009055506001016103d8565b5090565b90565b50805460018160011615610100020316600290046000825580601f1061041d575061043c565b601f01602090049060005260206000209081019061043b91906103d2565b5b505600a165627a7a723058203c1f95b4a803493db0120df571d9f5155734152548a532412f2f9fa2dbe1ac730029
```

### 4. listAbi

Show contract interface and Event list
Parameters:

- Contract path: the path of the contract file, which supports relative path, absolute path, and default path.。When the user enters a file name, the file is obtained from the default directory, which is: 'contracts / solidity ', for example: TableTest。
- Contract Name:(Optional)Contract name, which uses the contract file name as the contract name parameter by default
- Contract Address:(Optional)After the contract address is deployed, listAbi initiates a getAbi request to the node

```shell
[group0]: /apps> listAbi TableTest
Method list:
 name                |    constant  |    methodId    |    signature
  --------------------------------------------------------------
 createTable         |    false     |    6a5bae4e    |    createTable(string,string,string[])
 remove              |    false     |    80599e4b    |    remove(string)
 select              |    true      |    fcd7e3c1    |    select(string)
 update              |    false     |    31c3e456    |    update(string,string,string)
 insert              |    false     |    2fe99bdc    |    insert(string,string,string)
 desc                |    true      |    55f150f1    |    desc()

Event list:
 name                |   topic                                                                   signature
  --------------------------------------------------------------
 InsertResult        |   0xc57b01fa77f41df77eaab79a0e2623fab2e7ae3e9530d9b1cab225ad65f2b7ce  |   InsertResult(int256)
 CreateResult        |   0xb5636cd912a73dcdb5b570dbe331dfa3e6435c93e029e642def2c8e40dacf210  |   CreateResult(int256)
 RemoveResult        |   0x4b930e280fe29620bdff00c88155d46d6d82a39f45dd5c3ea114dc3157358112  |   RemoveResult(int256)
 UpdateResult        |   0x8e5890af40fc24a059396aca2f83d6ce41fcef086876548fa4fb8ec27e9d292a  |   UpdateResult(int256)

# Use address parameters
[group0]: /apps> listAbi cceef68c9b4811b32c75df284a1396c7c5509561
Method list:
 name                |    constant  |    methodId    |    signature
  --------------------------------------------------------------
 set                 |    false     |    4ed3885e    |    set(string)
 get                 |    true      |    6d4ce63c    |    get()

```

### 5. getDeployLog

Run getDeployLog to query the group**by the current console**Log information of the deployment contract。Log information includes when the contract was deployed, group ID, contract name, and contract address。Parameters:

- Number of log lines. Optional. The latest log information is returned based on the expected value. When the actual number of log lines is less than the expected value, the actual number of log lines is returned.。When the expected value is not given, the latest log information is returned as 20 by default.。

```shell
[group0]: /apps>  getDeployLog 2

2019-05-26 08:37:03  [group]  HelloWorld            0xc0ce097a5757e2b6e189aa70c7d55770ace47767
2019-05-26 08:37:45  [group]  KVTableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576

[group0]: /apps>  getDeployLog 1

2019-05-26 08:37:45  [group]  KVTableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576
```

**Note:** To view all deployment contract log information, view the 'deploylog.txt' file in the 'console' directory。This file stores only log records for the last 10,000 deployment contracts。

### 6. listDeployContractAddress

Lists all contract addresses deployed with the specified contract name.
Lists the list of contract addresses generated by deploying a specified contract.

- contractNameOrPath: Contract name or contract absolute path, which specifies the contract;
- recordNumber: The length of the displayed list of contract addresses, which defaults to 20

```shell
# Get the list of contract addresses that result from deploying HelloWorld contracts
[group0]: /apps>  listDeployContractAddress HelloWorld
0xe157185434183b276b9e5af7d0315a9829171281  2020-10-13 15:35:29
0x136b042e1fc480b03e1e5b075cbdfa52f5851a23  2020-10-13 15:35:22
0xd7d0b221bc984a20aa6b0fc098dad89888378e3a  2020-10-13 15:34:14
0x0fe221339e50c39aaefddfc3a9a26b4aeff23c63  2020-10-13 15:16:20
0x5f248ad7e917cddc5a4d408cf18169d19c0990e5  2020-10-13 12:28:23
0xf027fd91a51bd4844f17600c01e943058fc27482  2020-10-12 18:28:50
0x682b51f3c7f9a605fa2026b09fd2635a6fa562d9  2020-10-11 23:22:28
0xf68a1aabfad336847e109c33ca471b192c93c0a9  2020-10-11 22:53:07
0xf485b9ccfffa668f4d7fac37c81c0cd63408188c  2020-10-11 22:52:26
0x175b16a1299c7af3e2e49b97e68a44734257a35e  2020-10-11 22:49:25
0x7c6dc94e4e146cb13eb03dc98d2b96ac79ef5e67  2020-10-11 22:46:35


# Limit the list of contract addresses displayed to 2
[group0]: /apps>  listDeployContractAddress HelloWorld 2
0xe157185434183b276b9e5af7d0315a9829171281  2020-10-13 15:35:29
0x136b042e1fc480b03e1e5b075cbdfa52f5851a23  2020-10-13 15:35:22
```

## Blockchain status query command

### 1. getBlockNumber

Run getBlockNumber to view the block height。

```shell
[group0]: /apps>  getBlockNumber
90
```

### 2. getSyncStatus

Run getSyncStatus to view the synchronization status。

```shell
[group0]: /apps> getSyncStatus 
SyncStatusInfo{
    isSyncing='false',
    protocolId='null',
    genesisHash='e1cedcd9d09d47a6ffac4e621b7d852e84363c20151d0ee2df63837ed23318d9',
    nodeId='44c3c0d914d7a3818923f9f45927724bddeeb25df92b93f1242c32b63f726935d6742b51cd40d2c828b52ed6cde94f4d6fb4b3bfdc0689cfcddf7425eafdae85',
    blockNumber='6',
    latestHash='ac0e663c2e99dd733553afe66c41ebf03b1ab2a48a40d72d700705ea860c8290',
    knownHighestNumber='6',
    txPoolSize='null',
    peers=[
        PeersInfo{
            nodeId='bb21228b0762433ea6e4cb185e1c54aeb83cd964ec0e831f8732cb2522795bb569d58215dfbeb7d3fc474fdce33dc9a793d4f0e86ce69834eddc707b48915824',
            genesisHash='e1cedcd9d09d47a6ffac4e621b7d852e84363c20151d0ee2df63837ed23318d9',
            blockNumber='6',
            latestHash='ac0e663c2e99dd733553afe66c41ebf03b1ab2a48a40d72d700705ea860c8290'
        },
        PeersInfo{
            nodeId='c1de42fc9e6798142fdbeddc05018b548b848155a8527f0ffc75eb93d0ae51ebd8074c86b6bdc0f4161dcad7cab9455a4eebf146ac5b08cb23c33c8eef756b7c',
            genesisHash='e1cedcd9d09d47a6ffac4e621b7d852e84363c20151d0ee2df63837ed23318d9',
            blockNumber='6',
            latestHash='ac0e663c2e99dd733553afe66c41ebf03b1ab2a48a40d72d700705ea860c8290'
        },
        PeersInfo{
            nodeId='f39b21b4832976591085b73a8550442e76dc2ae657adb799ff123001a553be60293b1059e97c472e49bb02b71384f05501f149905015707a2fe08979742c1366',
            genesisHash='e1cedcd9d09d47a6ffac4e621b7d852e84363c20151d0ee2df63837ed23318d9',
            blockNumber='6',
            latestHash='ac0e663c2e99dd733553afe66c41ebf03b1ab2a48a40d72d700705ea860c8290'
        }
    ],
    knownLatestHash='ac0e663c2e99dd733553afe66c41ebf03b1ab2a48a40d72d700705ea860c8290'
}
```

### 3. getPeers

Run getPeers to view the peers of the node。

```shell
[group0]: /apps> getPeers 
PeersInfo{
    p2pNodeID='3082010a0282010100ced891dad17f04738a2da965264e1387c707b351a0a4e360341066bb2893635fd4e2717ba203aed68d2733a633ecc097bd70fd0c34f31cf82c1772c60a2b252f5498d41a6b7c7d26bad930e38dacee2889fef0e7311f76d4c098c80c8096dc8842d95135e27523aa3cfb1910eb2021bfe8c82f73508e419427f79508ca0d5693e1a913a3749d0496c6fcfc43b69eca7d3bd14a9d6e079737af7cf9ad0e78a40ddb6e3081ef88c9928b3864141933d360dada8a197b45cfebb475efba05387cbf54bfb8357681bdc27c1b48b7ab460dc01b1cb8d5ff8256ad36f1e8c1e4dce63d8758bb08a9f9829e366ef5233997b5ff46ec064e46136a8770addc927bbbc81f0203010001',
    endPoint='0.0.0.0:30300',
    groupNodeIDInfo=[
        NodeIDInfo{
            group='group0',
            nodeIDList=[
                44c3c0d914d7a3818923f9f45927724bddeeb25df92b93f1242c32b63f726935d6742b51cd40d2c828b52ed6cde94f4d6fb4b3bfdc0689cfcddf7425eafdae85
            ]
        }
    ],
    peers=[
        PeerInfo{
            p2pNodeID='3082010a0282010100d1f4618de0e7a6867e04e28749f688c02c763e570bebe9d9b4f1dd02446eb970f7eeb2926ff5dce55c3ef8d2736b7edee54e1a171a44b555f129ed9f9658b442c98b5837f2651717e2420720ac98d74177abfbe772c6a478600b29aa370423934111e0d03b2ccadf59a5f7978383b3f672ad5ef122a4df600d40ebed1ae226ac1dc727619baf5658d7866876e06b482857fa109b99e159688ba2a53d3e57c055f55e35613fbfe0e1058e8ce161ed740ee4ea03c7d933a171ef4da8238027c4bca966c85d43d04dfd186bae563c1d6790335c0f96daa9d33272598ab1e2a60cfb22e7cf70bc10ed0a0757d0ef0add741cb8604d8707a8fd80d21f2786f9cdb3670203010001',
            endPoint='127.0.0.1:55686',
            groupNodeIDInfo=[
                NodeIDInfo{
                    group='group0',
                    nodeIDList=[
                        f39b21b4832976591085b73a8550442e76dc2ae657adb799ff123001a553be60293b1059e97c472e49bb02b71384f05501f149905015707a2fe08979742c1366
                    ]
                }
            ]
        },
        PeerInfo{
            p2pNodeID='3082010a0282010100d686574a794a3e27aac75eab7f84b3b46cc2bd19afc088bf6eb5702d2faf09116b7c374646a06793c71aa62ea43de6dda7f1d2711da81b1e289132d390b75c501d37aeee5a55d081d4c32b6c53e103547876d19f2f8fe8ca336bbd4a74ee6101861832380e65f6638fc7c52451993fe74084c36692c82c6ad6f958dbbe9d6a9e4a43753c6c5678830c2fc293fb1b2f1ac58484f9088e1ac70d8074ca64a6a6d68b09846f7437453ff0a9e27f4ba39442186d1a5cef1391f627e8f2bdc963c72ad31ee3a1a970db553fdd0a9ed6e0de365479deff5a086417e64ff4b9da29fd1debcdddcd0abb6928bc5c64ac40f1c83690696f1304104718aa4b31fcd06bf2150203010001',
            endPoint='127.0.0.1:55690',
            groupNodeIDInfo=[
                NodeIDInfo{
                    group='group0',
                    nodeIDList=[
                        c1de42fc9e6798142fdbeddc05018b548b848155a8527f0ffc75eb93d0ae51ebd8074c86b6bdc0f4161dcad7cab9455a4eebf146ac5b08cb23c33c8eef756b7c
                    ]
                }
            ]
        },
        PeerInfo{
            p2pNodeID='3082010a0282010100c0944e4235c287adb423816074286057a6d07fd03cc17e106bbffda1061d9872a2446378ff802e67eae602db415a2fca82569a554150c7ed70db0438076e545856f7d48356d2ab9cb2c9e3ec1cf3150fef31568056c19f92e568648346226c27b7f5c211a8557ef62ffe17ee55462d88d0f634a61490f4b86f7a4cd6568107bc8a08d2536abbee07c490f477b1c2f680db616663d386f5cdca93edbc895ef899b52518b7cf599001e5d9c7466e289a1a457a6514c479bb9287317a14cf171fc53874b4e368a737f920c1e4d26b835cc6503bb197ab1894db0ecc0383f2230e308a528aa1dae39cc00a2768f636c0573a91112f64550df823fd26e86d953701030203010001',
            endPoint='127.0.0.1:55694',
            groupNodeIDInfo=[
                NodeIDInfo{
                    group='group0',
                    nodeIDList=[
                        bb21228b0762433ea6e4cb185e1c54aeb83cd964ec0e831f8732cb2522795bb569d58215dfbeb7d3fc474fdce33dc9a793d4f0e86ce69834eddc707b48915824
                    ]
                }
            ]
        }
    ]
}
```

### 4. getBlockByHash

Run getBlockByHash to query the block information based on the block hash。
Parameters:

- Block hash: the hash value of the block starting with 0x。
- Transaction flag: false by default. Only transaction hash is displayed for transactions in the block. Set to true to display transaction details.。

```shell
[group0]: /apps> getBlockByHash 0x2cc22006edec686f116ac6b41859f7b23fa9b39f8a2baef33f17da46bfd13d42
{
    transactions=[
        {
            version='0',
            from='0x33909383b925fd20b3c12fb5fcfd7d39b4b015cc',
            hash='0x214bde834b119a3d8e834debefdf6708edabeca404dc268f794552ae6c5d338d',
            input='0x2317489345789578231243122904823904583905',
            nonce='1322329174548920161282698967016643957683885216413423752035236304235782814228',
            to='',
            blockLimit='500',
            chainId='chain0',
            groupID='group0',
            transactionProof='null',
            signature=0x5aefca1279c60d06da60f53c4d6c80203944ddd8bde5c2286de2ca3d640675fd7f855bd33c7f1a69835273bc411e9effc628b0a28491df1a4c0527fb4dfad16901
        }
    ],
    number='1',
    hash='0x2cc22006edec686f116ac6b41859f7b23fa9b39f8a2baef33f17da46bfd13d42',
    logsBloom='null',
    transactionsRoot='0x42734ef2f6a30539ed5c597ed4b6d904b0a6138980a251c21b18d5f28f2bcd83',
    receiptRoot='0x624065350d722316acf34efea6ea73c9d03902d0c8eb927796030502ce0728d0',
    stateRoot='0x028a4a625185ccc909c4820246d495aa6b04c70b10dda04bcc2875df6c172021',
    sealer='0',
    sealerList=[
        0x017115d6ae70f6331c7e2240dbba983fcc9e83cbd9b6bc12117b2b54513161c1dc6b78ace10d17a1b045b37c0345970cb57a2d56e6174508b52c56aa6283fecb,
        0x24b31c75b8448afd3495a2fd1941773e8e565473da35bf125868752ce2c81554623de2109a38a219c37b345cd43499d6fe092ed6723021ee21aa787b239da0b1
    ],
    extraData=0x,
    gasUsed='36488',
    timestamp='1639032194201',
    signatureList=[
        {
            index='0',
            signature='0xf4b77e92ddd74fb0bb33d28344260ae4935c0646092399d40b37cf3b364c324174d34c2b5a5495f2b403b34b342eff7cac00dcf8dfa0e2cb4458360d0bb0f04800'
        },
        {
            index='1',
            signature='0x41a43f6f0053496e68d258f4790c5a7497f6e80617cffb558db6fd6d746d212c6d721aaa68ed4d6ccfc709356296a4b5393680f20dd27120d94f2bc008a5c39800'
        }
    ]
}

[group0]: /apps> getBlockByHash 0x2cc22006edec686f116ac6b41859f7b23fa9b39f8a2baef33f17da46bfd13d42 true
{
    transactions=[
        TransactionHash{
            value='0x214bde834b119a3d8e834debefdf6708edabeca404dc268f794552ae6c5d338d'
        }
    ],
    number='1',
    hash='0x2cc22006edec686f116ac6b41859f7b23fa9b39f8a2baef33f17da46bfd13d42',
    logsBloom='null',
    transactionsRoot='0x42734ef2f6a30539ed5c597ed4b6d904b0a6138980a251c21b18d5f28f2bcd83',
    receiptRoot='0x624065350d722316acf34efea6ea73c9d03902d0c8eb927796030502ce0728d0',
    stateRoot='0x028a4a625185ccc909c4820246d495aa6b04c70b10dda04bcc2875df6c172021',
    sealer='0',
    sealerList=[
        0x017115d6ae70f6331c7e2240dbba983fcc9e83cbd9b6bc12117b2b54513161c1dc6b78ace10d17a1b045b37c0345970cb57a2d56e6174508b52c56aa6283fecb,
        0x24b31c75b8448afd3495a2fd1941773e8e565473da35bf125868752ce2c81554623de2109a38a219c37b345cd43499d6fe092ed6723021ee21aa787b239da0b1
    ],
    extraData=0x,
    gasUsed='36488',
    timestamp='1639032194201',
    signatureList=[
        {
            index='0',
            signature='0xf4b77e92ddd74fb0bb33d28344260ae4935c0646092399d40b37cf3b364c324174d34c2b5a5495f2b403b34b342eff7cac00dcf8dfa0e2cb4458360d0bb0f04800'
        },
        {
            index='1',
            signature='0x41a43f6f0053496e68d258f4790c5a7497f6e80617cffb558db6fd6d746d212c6d721aaa68ed4d6ccfc709356296a4b5393680f20dd27120d94f2bc008a5c39800'
        }
    ]
}
```

### 5. getBlockByNumber

Run getBlockByNumber to query the block information based on the block height.。
Parameters:

- Block height: decimal integer。
- Transaction flag: false by default. Only transaction hash is displayed for transactions in the block. Set to true to display transaction details.。

```shell
[group0]: /apps> getBlockByNumber 1
{
    transactions=[
        {
            version='0',
            from='0x33909383b925fd20b3c12fb5fcfd7d39b4b015cc',
            hash='0x214bde834b119a3d8e834debefdf6708edabeca404dc268f794552ae6c5d338d',
            input='0x4678294769234805890812340281049328540945470',
            nonce='1322329174548920161282698967016643957683885216413423752035236304235782814228',
            to='',
            blockLimit='500',
            chainId='chain0',
            groupID='group0',
            transactionProof='null',
            signature=0x5aefca1279c60d06da60f53c4d6c80203944ddd8bde5c2286de2ca3d640675fd7f855bd33c7f1a69835273bc411e9effc628b0a28491df1a4c0527fb4dfad16901
        }
    ],
    number='1',
    hash='0x2cc22006edec686f116ac6b41859f7b23fa9b39f8a2baef33f17da46bfd13d42',
    logsBloom='null',
    transactionsRoot='0x42734ef2f6a30539ed5c597ed4b6d904b0a6138980a251c21b18d5f28f2bcd83',
    receiptRoot='0x624065350d722316acf34efea6ea73c9d03902d0c8eb927796030502ce0728d0',
    stateRoot='0x028a4a625185ccc909c4820246d495aa6b04c70b10dda04bcc2875df6c172021',
    sealer='0',
    sealerList=[
        0x017115d6ae70f6331c7e2240dbba983fcc9e83cbd9b6bc12117b2b54513161c1dc6b78ace10d17a1b045b37c0345970cb57a2d56e6174508b52c56aa6283fecb,
        0x24b31c75b8448afd3495a2fd1941773e8e565473da35bf125868752ce2c81554623de2109a38a219c37b345cd43499d6fe092ed6723021ee21aa787b239da0b1
    ],
    extraData=0x,
    gasUsed='36488',
    timestamp='1639032194201',
    signatureList=[
        {
            index='0',
            signature='0xf4b77e92ddd74fb0bb33d28344260ae4935c0646092399d40b37cf3b364c324174d34c2b5a5495f2b403b34b342eff7cac00dcf8dfa0e2cb4458360d0bb0f04800'
        },
        {
            index='1',
            signature='0x41a43f6f0053496e68d258f4790c5a7497f6e80617cffb558db6fd6d746d212c6d721aaa68ed4d6ccfc709356296a4b5393680f20dd27120d94f2bc008a5c39800'
        }
    ]
}

[group0]: /apps> getBlockByNumber 1 true
{
    transactions=[
        TransactionHash{
            value='0x214bde834b119a3d8e834debefdf6708edabeca404dc268f794552ae6c5d338d'
        }
    ],
    number='1',
    hash='0x2cc22006edec686f116ac6b41859f7b23fa9b39f8a2baef33f17da46bfd13d42',
    logsBloom='null',
    transactionsRoot='0x42734ef2f6a30539ed5c597ed4b6d904b0a6138980a251c21b18d5f28f2bcd83',
    receiptRoot='0x624065350d722316acf34efea6ea73c9d03902d0c8eb927796030502ce0728d0',
    stateRoot='0x028a4a625185ccc909c4820246d495aa6b04c70b10dda04bcc2875df6c172021',
    sealer='0',
    sealerList=[
        0x017115d6ae70f6331c7e2240dbba983fcc9e83cbd9b6bc12117b2b54513161c1dc6b78ace10d17a1b045b37c0345970cb57a2d56e6174508b52c56aa6283fecb,
        0x24b31c75b8448afd3495a2fd1941773e8e565473da35bf125868752ce2c81554623de2109a38a219c37b345cd43499d6fe092ed6723021ee21aa787b239da0b1
    ],
    extraData=0x,
    gasUsed='36488',
    timestamp='1639032194201',
    signatureList=[
        {
            index='0',
            signature='0xf4b77e92ddd74fb0bb33d28344260ae4935c0646092399d40b37cf3b364c324174d34c2b5a5495f2b403b34b342eff7cac00dcf8dfa0e2cb4458360d0bb0f04800'
        },
        {
            index='1',
            signature='0x41a43f6f0053496e68d258f4790c5a7497f6e80617cffb558db6fd6d746d212c6d721aaa68ed4d6ccfc709356296a4b5393680f20dd27120d94f2bc008a5c39800'
        }
    ]
}
```

### 6. getBlockHashByNumber

Run getBlockHashByNumber to get the block hash by the block height。
Parameters:

- Block height: decimal integer。

```shell
[group0]: /apps>  getBlockHashByNumber 1
0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855
```

### 7. getTransactionByHash

Run getTransactionByHash to query transaction information through transaction hash。
Parameters:

- Transaction hash: transaction hash value starting with 0x。

```shell
[group0]: /apps> getTransactionByHash 0x459e0bbe907bc1fb34367a150a3485921e5ce3d49c6b044e8ebb7171f8081241
{
    version='0',
    from='0x3977d248ce98f3affa78a800c4f234434355aa77',
    hash='0x459e0bbe907bc1fb34367a150a3485921e5ce3d49c6b044e8ebb7171f8081241',
    input='0x2800efc0000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000806262323132323862303736323433336561366534636231383565316335346165623833636439363465633065383331663837333263623235323237393562623536396435383231356466626562376433666334373466646365333364633961373933643466306538366365363938333465646463373037623438393135383234',
    nonce='1244654254262255984079741307028596561526481167594459835423352512079777563980',
    to='0x0000000000000000000000000000000000001003',
    blockLimit='500',
    chainId='chain0',
    groupID='group0',
    transactionProof='null',
    signature=0x10617472b24161960284e1812f2becefbac5f8a4f3973ad5f600aff10a8935e345e9a8a99406f9b51bcaa2537055c76d24b0864a4fdd64b6c0c9dbfc5056680a00
}
```

### 8. getTransactionReceipt

Run getTransactionReceipt to query the transaction receipt through the transaction hash。
Parameters:

- Transaction hash: transaction hash value starting with 0x。

```shell
[group0]: /apps>  getTransactionReceipt 0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5
{
    "blockHash":"0xe4e1293837013f547ad7f443a8ff20a4e32a060b9cac56c41462255603548b7b",
    "blockNumber":"0x8",
    "contractAddress":"0x0000000000000000000000000000000000000000",
    "from":"0xf0d2115e52b0533e367447f700bfbf2ed35ff6fc",
    "gasUsed":"0x94f5",
    "input":"0xebf3b24f0000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a00000000000000000000000000000000000000000000000000000000000000005667275697400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000056170706c65000000000000000000000000000000000000000000000000000000",
    "logs":[
        {
            "address":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
            "data":"0x0000000000000000000000000000000000000000000000000000000000000001",
            "topics":[
                "0xc57b01fa77f41df77eaab79a0e2623fab2e7ae3e9530d9b1cab225ad65f2b7ce"
            ]
        }
    ],
    "logsBloom":"0x00000000000000800000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000800000000000000000000000000000000002000000000000000002000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "output":"0x0000000000000000000000000000000000000000000000000000000000000001",
    "status":"0x0",
    "to":"0x42fc572759fd568bd590f46011784be2a2d53f0c",
    "transactionHash":"0x1dfc67c51f5cc93b033fc80e5e9feb049c575a58b863483aa4d04f530a2c87d5",
    "transactionIndex":"0x0"
}
```

### 9. getPendingTxSize

Run getPendingTxSize to query the number of transactions waiting to be processed (the number of transactions in the transaction pool)。

```shell
[group0]: /apps>  getPendingTxSize
0
```

### 10. getTotalTransactionCount

Run getTotalTransactionCount to query the current block height and the cumulative number of transactions (starting from the block height of 0)。

```shell
[group0]: /apps>  getTotalTransactionCount
{
    "blockNumber":1,
    "txSum":1,
    "failedTxSum":0
}
```

### 11. setSystemConfigByKey

Run setSystemConfigByKey to set system parameters as key-value pairs。Currently set system parameters support 'tx _ count _ limit', 'consensus _ leader _ period'。The key names of these system parameters can be completed by the tab key:

**Note:** When the permission governance mode is enabled, this command can only be used by the governance committee and cannot be directly called by the user. For details, see the command 'setSysConfigProposal'

- `tx_count_limit`: Maximum number of packaged transactions in a block
- `tx_gas_price`: Transaction gas price. The default unit is wei. Kwei, mwei, gwei, szabo, finney, ether, Kether, Mether, and Gether are supported.
- `tx_gas_limit`: Gas limits for trade execution
- `consensus_leader_period`: Consensus Select Primary Interval
- `compatibility_version`: Data-compatible version number. After all binaries in the blockchain are upgraded to the latest version, you can upgrade the data-compatible version number to the latest version by using setSystemConfigByKey.
- `auth_check_status`: (Effective after 3.3.0) Permission check status, if it is 0, all permission checks are turned off, and if it is not 0, all checks are turned on

Parameters:

- key
- value

```shell
[group0]: /apps>  setSystemConfigByKey tx_count_limit 100
{
    "code":0,
    "msg":"success"
}

[group0]: /apps>  setSystemConfigByKey tx_gas_price 1 kwei
{
    "code":0,
    "msg":"success"
}
```

### 12. getSystemConfigByKey

Run getSystemConfigByKey to query the value of the system parameter based on the key。
Parameters:

- key

```shell
[group0]: /apps>  getSystemConfigByKey tx_count_limit
100

[group0]: /apps> getSystemConfigByKey tx_gas_price 
1000
```

## consensus operation command

### 1. getSealerList

Run getSealerList to view the list of consensus nodes。

```shell
[group0]: /apps>  getSealerList
[
    Sealer{
        nodeID='44c3c0d914d7a3818923f9f45927724bddeeb25df92b93f1242c32b63f726935d6742b51cd40d2c828b52ed6cde94f4d6fb4b3bfdc0689cfcddf7425eafdae85',
        weight=1
    },
    Sealer{
        nodeID='f39b21b4832976591085b73a8550442e76dc2ae657adb799ff123001a553be60293b1059e97c472e49bb02b71384f05501f149905015707a2fe08979742c1366',
        weight=1
    },
    Sealer{
        nodeID='c1de42fc9e6798142fdbeddc05018b548b848155a8527f0ffc75eb93d0ae51ebd8074c86b6bdc0f4161dcad7cab9455a4eebf146ac5b08cb23c33c8eef756b7c',
        weight=1
    },
    Sealer{
        nodeID='bb21228b0762433ea6e4cb185e1c54aeb83cd964ec0e831f8732cb2522795bb569d58215dfbeb7d3fc474fdce33dc9a793d4f0e86ce69834eddc707b48915824',
        weight=1
    }
]
```

### 2. getObserverList

Run getObserverList to view the list of observation nodes。

```shell
[group0]: /apps>  getObserverList
[  
    bb21228b0762433ea6e4cb185e1c54aeb83cd964ec0e831f8732cb2522795bb569d58215dfbeb7d3fc474fdce33dc9a793d4f0e86ce69834eddc707b48915824
]
```

### 3. getPbftView

Run getPbftView to view the pbft view。

```shell
[group0]: /apps>  getPbftView
2730
```

### 4. getConsensusStatus

Run getSensusStatus to view the consensus status。

```shell
[group0]: /apps> getConsensusStatus 
ConsensusStatusInfo{
    nodeID='44c3c0d914d7a3818923f9f45927724bddeeb25df92b93f1242c32b63f726935d6742b51cd40d2c828b52ed6cde94f4d6fb4b3bfdc0689cfcddf7425eafdae85',
    index=0,
    leaderIndex=0,
    consensusNodesNum=3,
    maxFaultyQuorum=0,
    minRequiredQuorum=3,
    isConsensusNode=true,
    blockNumber=1,
    hash='fde78118e4b387b5c7ba74c19ddbd59992c5956145c5cc86265ee43814c2a320',
    timeout=false,
    changeCycle=0,
    view=1,
    connectedNodeList=4,
    consensusNodeInfos=[
        ConsensusNodeInfo{
            nodeID='44c3c0d914d7a3818923f9f45927724bddeeb25df92b93f1242c32b63f726935d6742b51cd40d2c828b52ed6cde94f4d6fb4b3bfdc0689cfcddf7425eafdae85',
            weight='1',
            index='0'
        },
        ConsensusNodeInfo{
            nodeID='c1de42fc9e6798142fdbeddc05018b548b848155a8527f0ffc75eb93d0ae51ebd8074c86b6bdc0f4161dcad7cab9455a4eebf146ac5b08cb23c33c8eef756b7c',
            weight='1',
            index='1'
        },
        ConsensusNodeInfo{
            nodeID='f39b21b4832976591085b73a8550442e76dc2ae657adb799ff123001a553be60293b1059e97c472e49bb02b71384f05501f149905015707a2fe08979742c1366',
            weight='1',
            index='2'
        }
    ]
}

```

### 5. addSealer

Run addSealer to add the node as a consensus node。

**Note:** When the permission governance mode is enabled, this command can only be used by the governance committee and cannot be directly called by the user. For details, see the command 'addSealerProposal'

Parameters:

- Node nodeId
- node weight

```shell
[group0]: /apps> addSealer bb21228b0762433ea6e4cb185e1c54aeb83cd964ec0e831f8732cb2522795bb569d58215dfbeb7d3fc474fdce33dc9a793d4f0e86ce69834eddc707b48915824 2
{
    "code":0,
    "msg":"Success"
}
```

### 6. addObserver

Run addObserver to add the node as an observation node。

**Note:** When the permission governance mode is enabled, this command can only be used by the governance committee and cannot be directly called by the user. For details, see the command 'addObserverProposal'

Parameters:

- Node nodeId

```shell
[group0]: /apps>  addObserver ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
    "code":0,
    "msg":"success"
}
```

### 7. removeNode

Run removeNode and the node exits。You can use the addSealer command to add the exiting node as a consensus node and the addObserver command to add the node as an observation node.。

**Note:** When the permission governance mode is enabled, this command can only be used by the governance committee and cannot be directly called by the user. For details, see the command 'removeNodeProposal'.

Parameters:

- Node nodeId

```shell
[group0]: /apps>  removeNode ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
    "code":0,
    "msg":"success"
}
```

### 8. setConsensusWeight

Run setSensusWeight to set the consensus weight of a specific node。

**Note:** When the permission governance mode is enabled, this command can only be used by the governance committee and cannot be directly called by the user. For details, see the command 'setConsensusNodeWeightProposal'

```shell
[group0]: /apps> setConsensusWeight 44c3c0d914d7a3818923f9f45927724bddeeb25df92b93f1242c32b63f726935d6742b51cd40d2c828b52ed6cde94f4d6fb4b3bfdc0689cfcddf7425eafdae85 2
{
    "code":0,
    "msg":"Success"
}
```

## Contract Table Operation Command

### 1. [create sql]

Run the create sql statement to create a user table, using the mysql statement。

```bash
# Create a user table t _ demo with the primary key name and other fields item _ id and item _ name
[group0]: /apps> create table t_demo(name varchar, item_id varchar, item_name varchar, primary key(name))
Create 't_demo' Ok.

# The created t _ demo table can be observed under the absolute path / tables /
[group0]: /apps> ls ../tables/
t_demo
```

**Note:**

- The length of the prefix on the created table name cannot exceed 50. For example, the length of / tables / t _ demo cannot exceed 50.。
- The field type of the created table is a string type, even if other field types of the database are provided, the string type is set。
- Primary key field must be specified。For example, create a t _ demo table. The primary key field is name.。
- The primary key of a table is not the same as the primary key in a relational database. The value of the primary key is not unique here.。
- You can specify a field as the primary key, but the modified keywords such as field self-increment, non-null, and index do not work。

### 2. [alter sql]

Run the alter sql statement to modify the user table, using the mysql statement。

```bash
# Modify the user table t _ demo and add the comment field
[group0]: /apps> alter table t_demo add comment varchar
Alter 't_demo' Ok.

[group0]: /apps> desc t_demo
{
    "key_field":[
        "name"
    ],
    "value_field":[
        "item_id",
        "item_name",
        "comment"
    ]
}
```

**Note:**

- The modified table must exist and currently**Only new fields are supported**
- The field type of the created table is a string type, even if other field types of the database are provided, they are also set according to the string type and cannot be repeated.

### 3. desc

Run the desc statement to query the field information of the table, using the mysql statement。

```shell
# Queries the field information of the t _ demo table. You can view the primary key name and other field names of the table.
[group0]: /apps> desc t_demo
{
    "key_field":[
        "name"
    ],
    "value_field":[
        "item_id",
        "item_name"
    ]
}
```

### 4. [insert sql]

Run the insert sql statement to insert records, using the mysql statement form。

```text
[group0]: /apps> insert into t_demo (name, item_id, item_name) values (fruit, 1, apple1)
Insert OK:
1 row(s) affected.
```

**Note:**

- insert record sql statement must be inserted into the primary key field value of the table。
- The entered value is a string containing letters with punctuation, spaces, or numbers that start with a number. Double quotation marks are required. Double quotation marks are not allowed in double quotation marks。

### 5. [select sql]

Run the select sql statement to query records, using the mysql statement。

Unlike regular SQL, conditional statements that traverse interfaces currently only support the condition of the key field.。

```text
# Query records with all fields
[group0]: /apps> select * from t_demo where name = fruit
{name=fruit, item_id=1, item_name=apple1}
1 row(s) in set.

# Query records that contain the specified field
[group0]: /apps> select name, item_id, item_name from t_demo where name = fruit
{name=fruit, item_id=1, item_name=apple1}
1 row(s) in set.

# Insert a new record
[group0]: /apps> insert into t_demo values (fruit2, 2, apple2)
Insert OK, 1 row affected.

# Use the and keyword to join multiple query criteria
[group0]: /apps> select * from t_demo where name >= fruit
{name=fruit, item_id=1, item_name=apple1}
{name=fruit2, item_id=2, item_name=apple2}
2 row(s) in set.

# Use the limit field to query the first row record, no offset is provided and the default is 0
[group0]: /apps> select * from t_demo where name = fruit limit 1
{item_id=1, item_name=apple1, name=fruit}
1 row in set.

# Use the limit field to query row 2 records at offset 1
[group0]: /apps> select * from t_demo where name = fruit limit 1,1
{item_id=1, item_name=apple1, name=fruit}
1 rows in set.
```

**Note:**

- query record sql statement must provide the primary key field value of the table in the where clause。
- The limit field in a relational database can be used, providing two parameters, respectively, offset(Offset)and number of records(count)。
- The WHERE clause only supports the AND keyword. Other OR, IN, LIKE, INNER, JOIN, UNION, subqueries, and multi-table union queries are not supported.。
- The entered value is a string containing letters with punctuation, spaces, or numbers that start with a number. Double quotation marks are required. Double quotation marks are not allowed in double quotation marks。

### 6. [update sql]

Run the update sql statement to update the record, using the mysql statement。

```text
[group:1]> update t_demo set item_name = orange where name = fruit
Update OK, 1 row affected.
```

**Note:**

- The where clause of the update record sql statement currently only supports the primary key field value condition of the table.。
- The entered value is a string containing letters with punctuation, spaces, or numbers that start with a number. Double quotation marks are required. Double quotation marks are not allowed in double quotation marks。

### 7. [delete sql]

Run the delete sql statement to delete a record, using the mysql statement。

```text
[group:1]> delete from t_demo where name = fruit
Remove OK, 1 row affected.
```

**Note:**

- The where clause of the delete record sql statement currently only supports the primary key field value condition of the table.。
- The entered value is a string containing letters with punctuation, spaces, or numbers that start with a number. Double quotation marks are required. Double quotation marks are not allowed in double quotation marks。

## BFS Operation Commands

### 1. cd

Similar to the Linux cd command, you can switch the current path and support absolute and relative paths.。

```shell
[group0]: /apps> cd ../tables

[group0]: /tables>

[group0]: /apps> cd ../tables/test

[group0]: /tables/test>

[group0]: /tables/test> cd ../../

[group0]: />
```

### 2. ls

Similar to the Linux ls command, you can view the resources in the current path. If it is a directory, all resources in the directory are displayed.；In the case of a contract, display the contract's meta information。

When the ls parameter is 0, the current folder is displayed. When the ls parameter is 1, absolute and relative paths are supported.。

```shell
[group0]: /> ls
apps usr sys tables

[group0]: /> ls apps
Hello

[group0]: /> ls ./apps/Hello
name: Hello, type: contract
```

### 3. mkdir

Similar to the mkdir command in Linux, a new directory is created under a folder, and absolute and relative paths are supported.。

```shell
[group0]: /> mkdir /apps/test

[group0]: /> cd /apps

[group0]: /apps> ls
test

[group0]: /> mkdir ./test/test

[group0]: /> ls ./test
test
```

### 4. ln

Similar to the Linux ln command, you can create a link to a contract resource and initiate a call to the actual contract by calling the link directly.。

Similar to version 2.0 of the CNS service, relying on the BFS multi-level directory, you can establish a mapping relationship between contract name and contract address, contract version number。

For example, if the contract name is Hello and the contract version number is latest, you can create a soft connection of '/ apps / Hello / latest' in the '/ apps' directory.。Similarly, users can create multiple versions under '/ apps / Hello', for example: '/ apps / Hello / newOne', '/ apps / Hello / layerTwo', etc.。

```bash
# Create a contract soft link with the contract name Hello and the contract version latest
[group0]: /apps> ln Hello/latest 0x19a6434154de51c7a7406edF312F01527441B561
{
    "code":0,
    "msg":"Success"
}

# The link file is created in the / apps / directory.
[group0]: /apps> ls ./Hello/latest 
latest -> 19a6434154de51c7a7406edf312f01527441b561      

# Links can be called directly
[group0]: /apps> call ./Hello/latest get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, World!)
---------------------------------------------------------------------------------------------

[group0]: /apps> deploy HelloWorld
contract address: 0x2b5DCbaE97f9d9178E8b051b08c9Fb4089BAE71b

# Version number can be overwritten
[group0]: /apps> ln Hello/latest  0x2b5DCbaE97f9d9178E8b051b08c9Fb4089BAE71b
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> ls ./Hello/latest 
latest -> 2b5dcbae97f9d9178e8b051b08c9fb4089bae71b  
```

### 5. tree

Similar to the tree command in Linux, the resources under the specified BFS path are displayed in a tree structure.。The default depth is 3, you can use the parameter to set the depth to no more than 5。

```bash
[group0]: /apps> tree ..
/
├─apps
├─sys
│ ├─account_manager
│ ├─auth
│ ├─bfs
│ ├─cast_tools
│ ├─consensus
│ ├─crypto_tools
│ ├─dag_test
│ ├─discrete_zkp
│ ├─group_sig
│ ├─kv_storage
│ ├─ring_sig
│ ├─status
│ ├─table_manager
│ └─table_storage
├─tables
└─usr

4 directory, 14 contracts.

# Use depth of 1
[group0]: /apps> tree .. 1
/
├─apps
├─sys
├─tables
└─usr
```

### 6. pwd

Similar to the Linux pwd command, no parameters, showing the current path。

```shell
[group0]: /apps/Hello/BFS>  pwd
/apps/Hello/BFS
```

## group query command

### 1. getGroupPeers

Run getGroupPeers to view the list of consensus nodes and observation nodes in the group where the node is located.。

```shell
[group0]: /apps> getGroupPeers 
peer0: 07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a
peer1: 0ba87f0f2a218c70d207d5df01f74c9b5799bc0853af27f599422a5a0e8224d0ffe12d6aa0765b90487bfcfb9562e01dd98af6693ab54976d305b372b40e460c
peer2: 1fa15731ae79ac7a9c6affa5511b50b1d549e28906f1968db8ddce69e18601707803c032afc42a230b74d4579a1bcca04b2e848019e5b1215b7cab96d59fab5a
peer3: bad149badf702520cdf3d0a72a4790c2cd68bc23f9e2dd9b796b46ac9da78c98b089720981d987a546d3b3355406d0b428767a18916459c2cbade04432d80627
peer4: d177c9ce82f3ae1aa02a37544d52496afa8c102cf08c2b8b3b9822874c15188863d052cad4c3f78abff0510a6c1bc04892129c183ec53baffeff8c5a000f069a
```

### 2. getGroupInfo

Run getGroupInfo to view the details of the group where the node resides。

```shell
[group0]: /apps> getGroupInfo
{
    "chainID":"chain0",
    "groupID":"group0",
    "genesisConfig":{
        "consensusType":"pbft",
        "blockTxCountLimit":1000,
        "txGasLimit":3000000000,
        "consensusLeaderPeriod":1,
        "sealerList":[
            {
                "nodeID":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
                "weight":1
            },
            {
                "nodeID":"0ba87f0f2a218c70d207d5df01f74c9b5799bc0853af27f599422a5a0e8224d0ffe12d6aa0765b90487bfcfb9562e01dd98af6693ab54976d305b372b40e460c",
                "weight":1
            },
            {
                "nodeID":"bad149badf702520cdf3d0a72a4790c2cd68bc23f9e2dd9b796b46ac9da78c98b089720981d987a546d3b3355406d0b428767a18916459c2cbade04432d80627",
                "weight":1
            },
            {
                "nodeID":"d177c9ce82f3ae1aa02a37544d52496afa8c102cf08c2b8b3b9822874c15188863d052cad4c3f78abff0510a6c1bc04892129c183ec53baffeff8c5a000f069a",
                "weight":1
            }
        ]
    },
    "nodeList":[
        {
            "type":0,
            "iniConfig":{
                "binaryInfo":{
                    "version":"3.0.0",
                    "gitCommitHash":"77bcb55ed21e75b68f04e1443db27425e0e5f142",
                    "platform":"Linux/g++",
                    "buildTime":"20220815 17:44:59"
                },
                "chainID":"chain0",
                "groupID":"group0",
                "smCryptoType":false,
                "isSerialExecute":false,
                "nodeID":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
                "nodeName":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
                "rpcServiceName":"",
                "gatewayServiceName":"",
                "authCheck":false,
                "isWasm":false,
                "isAuthCheck":false
            },
            "name":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
            "serviceInfoList":null,
            "protocol":{
                "compatibilityVersion":50331648,
                "minSupportedVersion":0,
                "maxSupportedVersion":1
            }
        },
        {
            "type":0,
            "iniConfig":{
                "binaryInfo":{
                    "version":"3.0.0",
                    "gitCommitHash":"77bcb55ed21e75b68f04e1443db27425e0e5f142",
                    "platform":"Linux/g++",
                    "buildTime":"20220815 17:44:59"
                },
                "chainID":"chain0",
                "groupID":"group0",
                "smCryptoType":false,
                "isSerialExecute":false,
                "nodeID":"0ba87f0f2a218c70d207d5df01f74c9b5799bc0853af27f599422a5a0e8224d0ffe12d6aa0765b90487bfcfb9562e01dd98af6693ab54976d305b372b40e460c",
                "nodeName":"0ba87f0f2a218c70d207d5df01f74c9b5799bc0853af27f599422a5a0e8224d0ffe12d6aa0765b90487bfcfb9562e01dd98af6693ab54976d305b372b40e460c",
                "rpcServiceName":"",
                "gatewayServiceName":"",
                "authCheck":false,
                "isWasm":false,
                "isAuthCheck":false
            },
            "name":"0ba87f0f2a218c70d207d5df01f74c9b5799bc0853af27f599422a5a0e8224d0ffe12d6aa0765b90487bfcfb9562e01dd98af6693ab54976d305b372b40e460c",
            "serviceInfoList":null,
            "protocol":{
                "compatibilityVersion":50331648,
                "minSupportedVersion":0,
                "maxSupportedVersion":1
            }
        }
    ]
}
```

### 3. getGroupList

Run getGroupList to view the list of groups:

```shell
[group0]: /apps> getGroupList
[
    group0
]
```

### 4. getGroupInfoList

Run getGroupList to view a list of all group information:

```shell
[group0]: /apps> getGroupInfoList 
[
    {
        "chainID":"chain0",
        "groupID":"group0",
        "genesisConfig":{
            "consensusType":"pbft",
            "blockTxCountLimit":1000,
            "txGasLimit":3000000000,
            "consensusLeaderPeriod":1,
            "sealerList":[
                {
                    "nodeID":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
                    "weight":1
                },
                {
                    "nodeID":"0ba87f0f2a218c70d207d5df01f74c9b5799bc0853af27f599422a5a0e8224d0ffe12d6aa0765b90487bfcfb9562e01dd98af6693ab54976d305b372b40e460c",
                    "weight":1
                },
                {
                    "nodeID":"bad149badf702520cdf3d0a72a4790c2cd68bc23f9e2dd9b796b46ac9da78c98b089720981d987a546d3b3355406d0b428767a18916459c2cbade04432d80627",
                    "weight":1
                },
                {
                    "nodeID":"d177c9ce82f3ae1aa02a37544d52496afa8c102cf08c2b8b3b9822874c15188863d052cad4c3f78abff0510a6c1bc04892129c183ec53baffeff8c5a000f069a",
                    "weight":1
                }
            ]
        },
        "nodeList":[
            {
                "type":0,
                "iniConfig":{
                    "binaryInfo":{
                        "version":"3.0.0",
                        "gitCommitHash":"77bcb55ed21e75b68f04e1443db27425e0e5f142",
                        "platform":"Linux/g++",
                        "buildTime":"20220815 17:44:59"
                    },
                    "chainID":"chain0",
                    "groupID":"group0",
                    "smCryptoType":false,
                    "isSerialExecute":false,
                    "nodeID":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
                    "nodeName":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
                    "rpcServiceName":"",
                    "gatewayServiceName":"",
                    "authCheck":false,
                    "isWasm":false,
                    "isAuthCheck":false
                },
                "name":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
                "serviceInfoList":null,
                "protocol":{
                    "compatibilityVersion":50331648,
                    "minSupportedVersion":0,
                    "maxSupportedVersion":1
                }
            }
        ]
    }
]
```

### 5. getGroupNodeInfo

Run the getGroupNodeInfo command to get information about a node in the current group:

```shell
[group0]: /apps> getGroupNodeInfo 07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a
{
    "type":0,
    "iniConfig":{
        "binaryInfo":{
            "version":"3.0.0",
            "gitCommitHash":"77bcb55ed21e75b68f04e1443db27425e0e5f142",
            "platform":"Linux/g++",
            "buildTime":"20220815 17:44:59"
        },
        "chainID":"chain0",
        "groupID":"group0",
        "smCryptoType":false,
        "isSerialExecute":false,
        "nodeID":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
        "nodeName":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
        "rpcServiceName":"",
        "gatewayServiceName":"",
        "authCheck":false,
        "isWasm":false,
        "isAuthCheck":false
    },
    "name":"07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a",
    "serviceInfoList":null,
    "protocol":{
        "compatibilityVersion":50331648,
        "minSupportedVersion":0,
        "maxSupportedVersion":1
    }
}
```

## permission operation command

Permission governance operation commands are divided into: query permission governance status command, governance committee special command, contract administrator special command.。

### 1. Query permission governance commands

This type of command has no permission control and is accessible to all accounts。

#### 1.1. getCommitteeInfo

At initialization, a governance committee is deployed whose address information is automatically generated or specified at build _ chain.sh.。Initialize only one member, and the weight of the member is 1

```shell
[group0]: /apps> getCommitteeInfo 
---------------------------------------------------------------------------------------------
Committee address   : 0xcbc22a496c810dde3fa53c72f575ed024789b2cc
ProposalMgr address : 0xa0974646d4462913a36c986ea260567cf471db1f
---------------------------------------------------------------------------------------------
ParticipatesRate: 0% , WinRate: 0%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6     | 1
```

#### 1.2. getProposalInfo

Obtain proposal information in a specific range in batches. If only a single ID is entered, the proposal information of a single ID is returned.。

'proposalType 'and' status' can see the type and status of the proposal

ProposalType is divided into the following categories:

- setWeight: Generated when the governance committee initiates the updateGovernorProposal
- setRate: The setRateProposal is generated
- setDeployAuthType: The setDeployAuthTypeProposal proposal generates
- modifyDeployAuth: openDeployAuthProposal and closeDeployAuthProposal generate
- resetAdmin: resetAdminProposal is generated
- setConfig: setSysConfigProposal is generated
- setNodeWeight: addObserverProposal, addSealerProposal, setSensusNodeWeightProposal Proposal Generation
- removeNode: removeNodeProposal generation
- unknown: When this type appears, there may be a bug

status is divided into the following categories:

- notEnoughVotes: proposal normal, not enough votes collected yet
- finish: Proposal execution complete
- Failed: Proposal failed
- Revoke: Proposal withdrawn
- outdated: Proposal exceeds voting deadline
- unknown: When this type appears, there may be a bug

```shell
[group0]: /apps> getProposalInfo 1
Show proposal, ID is: 1
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : setWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

# Batch Get
[group0]: /apps> getProposalInfo 1 2
Proposal ID: 1
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : setWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

```

#### 1.3. getLatestProposal

In order to avoid the timeout of initiating a proposal and forgetting the proposal ID when exiting the console, the getLatestProposal command can obtain the latest proposal information of the current committee.。

```shell
[group0]: /apps> getLatestProposal 
Latest proposal ID: 9
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : resetAdmin
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:
```

#### 1.4. getDeployAuth

Permission policies are divided into:

- No permissions, everyone can deploy
- Blacklist. Users on the blacklist cannot be deployed
- Whitelist, only whitelisted users can be deployed

```shell
[group0]: /apps> getDeployAuth
There is no deploy strategy, everyone can deploy contracts.
```

Governance Committee-specific commands, which must have an account in the Governance Committee's Governors before they can be called.

If there is only one governance committee member and the proposal is initiated by that committee member, then the proposal is bound to succeed

#### 1.5. checkDeployAuth

Check if the account has deployment permissions

```shell
# the current deployment permission is in whitelist mode.
[group0]: /apps> getDeployAuth 
Deploy strategy is White List Access.

# If you do not select the parameter, check whether the current account has the deployment permission.
[group0]: /apps> checkDeployAuth 
Deploy : PERMISSION DENIED
Account: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

# Check if other accounts have permissions
[group0]: /apps> checkDeployAuth 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
Deploy : ACCESS
Account: 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
```

#### 1.6. getContractAdmin

Use the command to obtain the administrator of a contract. Only the administrator can control the permissions of the contract.。

```shell
# The admin account number of the contract address 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
[group0]: /apps> getContractAdmin 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
Admin for contract 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
```

#### 1.7. checkMethodAuth

Check whether the account has permission to call a contract interface.

```shell
# Set the set of the contract with address 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b(string) for whitelist mode
[group0]: /apps> setMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b set(string) white_list
{
    "code":0,
    "msg":"Success"
}

# If no parameter is selected, check whether the current account has the calling permission.
[group0]: /apps> checkMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b set(string)
Method   : PERMISSION DENIED
Account  : 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
Interface: set(string)
Contract : 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b

# Check the permissions of other accounts
[group0]: /apps> checkMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b set(string) 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Method   : ACCESS
Account  : 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Interface: set(string)
Contract : 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b
```

#### 1.8. getMethodAuth

Get all access lists for a contract interface

```shell
# Open account access to contract 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 set interface
[group0]: /apps> openMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b "set(string)" 0x5298906acaa14e9b1a3c1462c6938e044bd41967
{
    "code":0,
    "msg":"Success"
}
# Get the list of permissions for the 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 set interface of the contract
[group0]: /apps> getMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b "set(string)"
---------------------------------------------------------------------------------------------
Contract address: 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b
Contract method : set(string)
Method auth type: WHITE_LIST
---------------------------------------------------------------------------------------------
Access address:
5298906acaa14e9b1a3c1462c6938e044bd41967
---------------------------------------------------------------------------------------------
Block address :
```

#### 1.9. getContractStatus

Obtain the status of a contract. Currently, there are only two statuses: frozen and normal access.

```shell
[group0]: /apps> getContractStatus 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01
Available

[group0]: /apps> freezeContract 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> getContractStatus 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01
Unavailable
```

### 2. Special Order of Governance Committee

These orders can only be used by holding the account of the governance committee.。

#### 2.1. updateGovernorProposal

In the case of a new governance committee, add an address and weight.。

If you are deleting a governance member, you can set the weight of a governance member to 0

```shell
[group0]: /apps> updateGovernorProposal 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6 2
Update governor proposal created, ID is: 1
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : setWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

```

#### 2.2. setRateProposal

Set proposal threshold, which is divided into participation threshold and weight threshold。

Thresholds are used here in percentages。

```shell
[group0]: /apps> setRateProposal 51 51
Set rate proposal created, ID is: 2
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : setRate
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

```

#### 2.3. setDeployAuthTypeProposal

Set the ACL policy for deployment. Only white _ list and black _ list policies are supported.

```shell
[group0]: /apps> setDeployAuthTypeProposal white_list
Set deploy auth type proposal created, ID is: 4
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : setDeployAuthType
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /apps> getDeployAuth 
Deploy strategy is White List Access.
```

#### 2.4. openDeployAuthProposal

Open a proposal for an administrator deployment permission

```shell
# In whitelist mode, only users in the whitelist can use the
[group0]: /apps> deploy HelloWorld 
deploy contract for HelloWorld failed!
return message: Permission denied
return code:18
Return values:null

# The governance committee can open its own (or other accounts) deployment permissions
[group0]: /apps> openDeployAuthProposal 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Open deploy auth proposal created, ID is: 5
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : modifyDeployAuth
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

# Deploy again, can succeed
[group0]: /apps> deploy HelloWorld 
transaction hash: 0x732e29baa263fc81e17a93a4102a0aa1cc2ec33ffbc1408c6b206d124b7f7ae0
contract address: 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
currentAccount: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

```

#### 2.5. closeDeployAuthProposal

 Proposal to turn off an administrator deployment permission

```shell
# Account can deploy contract
[group0]: /apps> deploy HelloWorld 
transaction hash: 0x732e29baa263fc81e17a93a4102a0aa1cc2ec33ffbc1408c6b206d124b7f7ae0
contract address: 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
currentAccount: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

# Disable the deployment permission of an account
[group0]: /apps> closeDeployAuthProposal 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6 
Close deploy auth proposal created, ID is: 6
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : modifyDeployAuth
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

# This account can no longer deploy contracts
[group0]: /apps> deploy HelloWorld 
deploy contract for HelloWorld failed!
return message: Permission denied
return code:18
Return values:null

```

#### 2.6. resetAdminProposal

Proposal to reset the administrator of a contract

```shell
# The admin account number of the contract address 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
[group0]: /apps> getContractAdmin 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
Admin for contract 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

# The admin account number of reset 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is 0xea9b0d13812f235e4f7ea5b6131794c9c755e9a
[group0]: /apps> resetAdminProposal 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a  0xCcEeF68C9b4811b32c75df284a1396C7C5509561
Reset contract admin proposal created, ID is: 8
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : resetAdmin
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /apps> getContractAdmin 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
Admin for contract 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is: 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a

```

#### 2.7. addSealerProposal

Initiate a proposal to add a new consensus node Sealer

```shell
# New consensus node Sealer with weight 1
[group0]: /apps> addSealerProposal 6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8 1
Add consensus sealer proposal created, ID is: 3
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : setNodeWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

# Confirm Increase Success
[group0]: /apps> getSealerList
[
    Sealer{
        nodeID='63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0',
        weight=1
    },
    Sealer{
        nodeID='6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8',
        weight=1
    }
]
```

#### 2.8. addObserverProposal

Initiate a proposal to add a new observation node Observer

```shell
[group0]: /apps> addObserverProposal 6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8
Add observer proposal created, ID is: 2
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : setNodeWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

# Confirm that the observation node has been added
[group0]: /apps> getObserverList
[
 6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8
]
```

#### 2.9. setConsensusNodeWeightProposal

Initiate a proposal to modify consensus node weights

```shell
[group0]: /apps> setConsensusNodeWeightProposal 6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8 2
Set consensus weight proposal created, ID is: 6
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : setNodeWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

# Confirm updated to weight 2
[group0]: /apps> getSealerList
[
    Sealer{
        nodeID='63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0',
        weight=1
    },
    Sealer{
        nodeID='6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8',
        weight=2
    }
]
```

#### 2.10. removeNodeProposal

Initiate a proposal to delete a node

```shell
[group0]: /apps> removeNodeProposal 6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8
Remove node proposal created, ID is: 7
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : removeNode
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /apps> getSealerList
[
    Sealer{
        nodeID='63a2e45b2d84f83b32342f0741ffc51069c74fb7c82b8eb0247b12230d50169b86545ecf84420adeec86c57dbc48db1342f4afebc6a127b481eeaaa23722fff0',
        weight=1
    }
]
```

#### 2.11. setSysConfigProposal

Initiate a proposal to change the system configuration

```shell
# Change tx _ count _ limit to 2000
[group0]: /apps> setSysConfigProposal tx_count_limit 2000
Set system config proposal created, ID is: 9
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : setConfig
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

# View has changed successfully
[group0]: /apps> getSystemConfigByKey tx_count_limit
2000
```

#### 2.12. upgradeVoteProposal

Initiate a proposal to upgrade the logic of voting calculations。The upgrade proposal vote calculation logic is divided into the following steps:

1. Write contracts based on interfaces.；
2. Deploy the written contract on the chain and get the address of the contract；
3. Initiate a proposal to upgrade the voting calculation logic, enter the address of the contract as a parameter, and vote on it in the governance committee；
4. After the vote is passed (the voting calculation logic is still the original logic at this time), the voting calculation logic is upgraded.；Otherwise do not upgrade。

The voting calculation logic contract can only be used according to a certain interface implementation.。For contract implementation, see the following interface contract 'VoteComputerTemplate.sol':

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;

import "./Committee.sol";
import "./BasicAuth.sol";

abstract contract VoteComputerTemplate is BasicAuth {
    // Governors and threshold
    Committee public _committee;

    constructor(address committeeMgrAddress, address committeeAddress) {
        setOwner(committeeMgrAddress);
        _committee = Committee(committeeAddress);
        // first, test committee exist; second, test committee is helthy
        require(
            _committee.getWeights() >= 1,
            "committee is error, please check address!"
        );
    }
    / / This is the only entry to the voting rights recalculation logic. You must implement this interface and specify:
    / / Not enough votes, return 1；Vote passed, return 2；Vote not passed, return 3；
    function determineVoteResult(
        address[] memory agreeVoters,
        address[] memory againstVoters
    ) public view virtual returns (uint8);
    
    / / This is a verification interface for computational logic for other governance members to verify the validity of the contract.
    function voteResultCalc(
        uint32 agreeVotes,
        uint32 doneVotes,
        uint32 allVotes,
        uint8 participatesRate,
        uint8 winRate
    ) public pure virtual returns (uint8);
}
```

The existing contracts based on the above 'VoteComputerTemplate.sol' interface implementation are as follows:

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;

import "./Committee.sol";
import "./VoteComputerTemplate.sol";

contract VoteComputer is VoteComputerTemplate {
    constructor(address committeeMgrAddress, address committeeAddress)
        public
        VoteComputerTemplate(committeeMgrAddress, committeeAddress)
    {}
    / / Voting Rights Recalculation Logic Implementation
    function determineVoteResult(
        address[] memory agreeVoters,
        address[] memory againstVoters
    ) public view override returns (uint8) {
        uint32 agreeVotes = _committee.getWeights(agreeVoters);
        uint32 doneVotes = agreeVotes + _committee.getWeights(againstVoters);
        uint32 allVotes = _committee.getWeights();
        return
            voteResultCalc(
                agreeVotes,
                doneVotes,
                allVotes,
                _committee._participatesRate(),
                _committee._winRate()
            );
    }
    / / Implementation of verification interface for calculation logic
    function voteResultCalc(
        uint32 agreeVotes,
        uint32 doneVotes,
        uint32 allVotes,
        uint8 participatesRate,
        uint8 winRate
    ) public pure override returns (uint8) {
        //1. Checks enough voters: totalVotes/totalVotesPower >= p_rate/100
        if (doneVotes * 100 < allVotes * participatesRate) {
            //not enough voters, need more votes
            return 1;
        }
        //2. Checks whether for votes wins: agreeVotes/totalVotes >= win_rate/100
        if (agreeVotes * 100 >= winRate * doneVotes) {
            return 2;
        } else {
            return 3;
        }
    }
}
```

Once the contract is written, it can be deployed on-chain and updated to the governance committee:

```shell
# First, confirm that the address of the Committee contract is 0xa0974646d4462913a36c986ea260567cf471db1f through the getCommitteeInfo command
[group0]: /apps> getCommitteeInfo
---------------------------------------------------------------------------------------------
Committee address   : 0xa0974646d4462913a36c986ea260567cf471db1f
ProposalMgr address : 0x2568bd207f50455f1b933220d0aef11be8d096b2
---------------------------------------------------------------------------------------------
ParticipatesRate: 0% , WinRate: 0%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x4a37eba43c66df4b8394abdf8b239e3381ea4221     | 2

# Deploy the VoteComputer contract. The first parameter 0x10001 is a fixed address, and the second parameter is the address of the current governance committee member Committee.
[group0]: /apps> deploy VoteComputer 0x10001 0xa0974646d4462913a36c986ea260567cf471db1f
transaction hash: 0x429a7ceccefb3a4a1649599f18b60cac1af040cd86bb8283b9aab68f0ab35ae4
contract address: 0x6EA6907F036Ff456d2F0f0A858Afa9807Ff4b788
currentAccount: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221

# After successful deployment, update via upgradeVoteProposal
[group0]: /apps> upgradeVoteProposal 0x6EA6907F036Ff456d2F0f0A858Afa9807Ff4b788
Upgrade vote computer proposal created, ID is: 10
---------------------------------------------------------------------------------------------
Proposer: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221
Proposal Type   : upgradeVoteCalc
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x4a37eba43c66df4b8394abdf8b239e3381ea4221
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /apps>
```

#### 2.13. voteProposal

Governance members may vote on a proposal with the option of agreeing or disagreeing。

```shell
# The current account initiates a proposal to set the threshold change. The proposal ID is 12
[group0]: /apps> setRateProposal 66 66
Set rate proposal created, ID is: 12
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : setRate
Proposal Status : notEnoughVotes
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

# At this time, another governance committee account is simulated to log in.
[group0]: /apps> loadAccount 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a 
Load account 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a success!

# The governance committee voted 12 to agree (the default second parameter is true, and the second parameter is false to reject the proposal)
[group0]: /apps> voteProposal 12
Vote proposal success.
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : setRate
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
---------------------------------------------------------------------------------------------
Against Voters:

# At this point the governance board information has changed
[group0]: /apps> getCommitteeInfo 
---------------------------------------------------------------------------------------------
Committee address   : 0xcbc22a496c810dde3fa53c72f575ed024789b2cc
ProposalMgr address : 0xa0974646d4462913a36c986ea260567cf471db1f
---------------------------------------------------------------------------------------------
ParticipatesRate: 66% , WinRate: 66%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6     | 2
index1 : 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a     | 2
```

#### 2.14. revokeProposal

Withdrawal of unsuccessful proposals, which can only be initiated by the governance committee that initiated the proposal。

```shell
# You can see that the number of people on the current committee is 2, and the participation pass threshold and success threshold are both 51%
[group0]: /apps> getCommitteeInfo 
---------------------------------------------------------------------------------------------
Committee address   : 0xcbc22a496c810dde3fa53c72f575ed024789b2cc
ProposalMgr address : 0xa0974646d4462913a36c986ea260567cf471db1f
---------------------------------------------------------------------------------------------
ParticipatesRate: 51% , WinRate: 51%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6     | 2
index1 : 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a     | 2

# Initiate a proposal that requires the consent of another committee to pass, with the status notEnoughVotes
[group0]: /apps> setRateProposal 66 66
Set rate proposal created, ID is: 11
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : setRate
Proposal Status : notEnoughVotes
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

# The current account actively cancels the proposal and the status of the proposal changes to revoke
[group0]: /apps> revokeProposal 11
Revoke proposal success.
---------------------------------------------------------------------------------------------
Proposer: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Proposal Type   : setRate
Proposal Status : revoke
---------------------------------------------------------------------------------------------
Agree Voters:
0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
---------------------------------------------------------------------------------------------
Against Voters:

```

#### 2.15. freezeAccount/unfreezeAccount

Freezing or unfreezing accounts, frozen accounts may not initiate any**Write**Transactions that can be unfrozen via unfreezeAccount。
This command can only be invoked by the governance committee。

```shell
# Freeze account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> freezeAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
{
    "code":0,
    "msg":"Success"
}

# Load into the frozen account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> loadAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22 success!

# Deployment calls are rejected
[group0]: /apps> deploy HelloWorld
deploy contract for HelloWorld failed!
return message: Account is frozen.
return code:22
Return values:null

[group0]: /apps> call HelloWorld 0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085 set test
transaction hash: 0x8844e61177f25cfafa9974525d6b8cb71f9ff2ec86cb40244018097bce8999bd
---------------------------------------------------------------------------------------------
transaction status: 22
---------------------------------------------------------------------------------------------
Receipt message: Account is frozen.
Return message: Account is frozen.
---------------------------------------------------------------------------------------------

# Unfreeze account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> unfreezeAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
{
    "code":0,
    "msg":"Success"
}

# Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> loadAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22 success!

[group0]: /apps> deploy HelloWorld
transaction hash: 0xd978585392114e2379be8c94250f5abceaf84538567d737db7dfbafcc0b7399c
contract address: 0xd24180cc0fef2f3e545de4f9aafc09345cd08903
currentAccount: 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
```

#### 2.16. abolishAccount

Abolished accounts, which may not initiate any**Write**transactions and are not recoverable。
This command can only be invoked by the governance committee。

```shell
# Abolished account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> abolishAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
{
    "code":0,
    "msg":"Success"
}

# Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> loadAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22 success!

# Deploy and call rejected
[group0]: /apps> deploy HelloWorld
deploy contract for HelloWorld failed!
return message: Account is abolished.
return code:23
Return values:null

[group0]: /apps> call HelloWorld 0xd24180cc0fef2f3e545de4f9aafc09345cd08903 set test
transaction hash: 0xccfaffed1a329fdaaee8f23906be15bc64eadc9b1f47ba1fd25faf7e3fb572c4
---------------------------------------------------------------------------------------------
transaction status: 23
---------------------------------------------------------------------------------------------
Receipt message: Account is abolished.
Return message: Account is abolished.
---------------------------------------------------------------------------------------------
```

### 3. Special order for contract administrator

These commands are only accessible to an administrator account that has administrative privileges on a contract。By default, the contract deployment initiation account is the contract administrator。

#### 3.1. setMethodAuth

 Permission policy for administrator setting method

**Special attention: the interface permission control of the contract can only control the write method at present.。**

```shell
# Set the set of the HelloWorld contract with the contract address 0xCcEeF68C9b4811b32c75df284a1396C7C5509561(string) Interface is in whitelist mode
[group0]: /apps> setMethodAuth 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set(string) white_list
{
    "code":0,
    "msg":"Success"
}

# This interface is currently in whitelist mode. Only accounts in whitelist mode can call the set interface.
[group0]: /apps> call HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set 123
transaction hash: 0x51e43a93b8e6621e45357ba542112117c3dd3e089b5067e06084e36243458074
---------------------------------------------------------------------------------------------
transaction status: 18
---------------------------------------------------------------------------------------------
Receipt message: Permission denied
Return message: Permission denied
---------------------------------------------------------------------------------------------

# Does not affect the account to call the get interface
[group0]: /apps> call HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 get 
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, World!)
---------------------------------------------------------------------------------------------

```

#### 3.2. openMethodAuth

Administrators enable users to access a method of the contract

```shell
# Open the set of 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 address of account number 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6(string)interface invocation
[group0]: /apps> openMethodAuth 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set(string) 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
{
    "code":0,
    "msg":"Success"
}

# This account can use the set interface of HelloWorld
[group0]: /apps> call HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set "Hello, FISCO BCOS!"
transaction hash: 0x0b94b775312a771197e5093fe9e24fffc386d0350f1330fe4ded6f3cfb01a0b6
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
---------------------------------------------------------------------------------------------
Event logs
Event: {}

[group0]: /apps> call HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, FISCO BCOS!)
---------------------------------------------------------------------------------------------
```

#### 3.3. closeMethodAuth

Administrators turn off a user's permission to access a method of a contract

```shell
# Close the set interface of HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
[group0]: /apps> closeMethodAuth 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set(string) 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
{
    "code":0,
    "msg":"Success"
}

# The account can no longer access the set interface of the contract
[group0]: /apps> call HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set "oops, permission deny!"
transaction hash: 0x4db693c3c1fbb498ad1595dac111b5b23efe68a44bd4b622d421e52b4890fdd0
---------------------------------------------------------------------------------------------
transaction status: 18
---------------------------------------------------------------------------------------------
Receipt message: Permission denied
Return message: Permission denied
---------------------------------------------------------------------------------------------
```

#### 3.4. freezeContract

Run freezeContract to freeze the specified contract。Parameters:

- Contract address: The contract address can be obtained for the deployment contract, where the 0x prefix is not required.。

```shell
[group0]: /apps> deploy HelloWorld
transaction hash: 0x847f89e44d79a7b7037c3d78a103c0c4d7b5fc458ac18b8aa75fd810094deade
contract address: 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E
currentAccount: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221

[group0]: /apps> freezeContract 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> call HelloWorld 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E get
call for HelloWorld failed, contractAddress: 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E
{
    "code":21,
    "msg":"ContractFrozen"
}
description: ContractFrozen, please refer to https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#id73

[group0]: /apps> call HelloWorld 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E set 123
transaction hash: 0x722176a922c85fe282bf6c434edc008b51ca72f4198ff0d6ed5f7359061404fb
---------------------------------------------------------------------------------------------
transaction status: 21
---------------------------------------------------------------------------------------------
Receipt message: ContractFrozen
Return message: ContractFrozen
---------------------------------------------------------------------------------------------
```

#### 3.5. unfreezeContract

Run unfreezeContract to unfreeze the specified contract。Parameters:

- Contract address: The contract address can be obtained for the deployment contract, where the 0x prefix is not required.。

```shell
[group0]: /apps> call HelloWorld 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E get
call for HelloWorld failed, contractAddress: 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E
{
    "code":21,
    "msg":"ContractFrozen"
}
description: ContractFrozen, please refer to https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#id73

[group0]: /apps> unfreezeContract 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> call HelloWorld 0xA28AC30A792A59C3CD114A87a75193C6B8278D7E get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, World!)
---------------------------------------------------------------------------------------------
```

## Account Operation Order

### 1. newAccount

Create a new account for sending transactions. By default, the account is saved in the 'account' directory in the 'PEM' format.。

```shell
# The account file is automatically saved in the 'account / ecdsa' directory when the console is connected to the non-national secret blockchain.
# The account file is automatically saved in the 'accout / gm' directory when the console is connected to the State Secret blockchain.
[group0]: /apps>  newAccount
AccountPath: account/ecdsa/0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642.pem
Note: This operation does not create an account in the blockchain, but only creates a local account, and deploying a contract through this account will create an account in the blockchain
newAccount: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
AccountType: ecdsa

$ ls -al account/ecdsa/0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642.pem
$ -rw-r--r--  1 octopus  staff  258  9 30 16:34 account/ecdsa/0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642.pem
```

### 2. loadAccount

Load a private key file in the format of 'PEM' or 'P12'. The loaded private key can be used to send transaction signatures.。However, if the console uses the public and private keys of the cipher machine, the command cannot be used because the public and private keys are placed inside the cipher machine.。
Parameters:

- Private key file path: Supports relative path, absolute path and default path。By default, the account is loaded from the account configuration option 'keyStoreDir' of 'config.toml'. For details about the configuration item 'keyStoreDir', see [here](./sdk/java_sdk/config.html#id9)。

- Account Format: Optional. The file type of the loaded account. The file type is' pem 'and' p12 '. The default value is' pem '.。

```shell
[group0]: /apps>  loadAccount 0x6fad87071f790c3234108f41b76bb99874a6d813
Load account 0x6fad87071f790c3234108f41b76bb99874a6d813 success!
```

### 3. listAccount

View all currently loaded account information

```shell
[group0]: /apps>  listAccount
0x6fad87071f790c3234108f41b76bb99874a6d813(current account) <=
0x726d9f31cf44debf80b08a7e759fa98b360b0736
```

**Note: The private key account marked with the '< =' suffix is the private key account currently used to send the transaction, which can be switched using 'loadAccount'.**

### 4. getCurrentAccount

Get current account address。If the console uses the public and private key of the cipher machine, the account address converted according to the internal public key of the cipher machine is displayed.。

```shell
[group0]: /apps>  getCurrentAccount
0x6fad87071f790c3234108f41b76bb99874a6d813
```



## Intra-block fragmentation management commands

This operation is the management operation of "intra-block fragmentation," please refer to [here](../../design/parallel/sharding.md)Learn more technical details。

### 1. makeShard

Create a shard

Parameters:

* Split name: The username to be created. No duplicates are allowed.

```
[group0]: /apps> makeShard hello_shard
make shard hello_shard Ok. You can use 'ls' to check
```

### 2. linkShard

Bind a contract to a shard

Parameters

* Contract address: The contract address to be bound
* Split name: The name of the slice to be bound to. If it does not exist, create a

注意

* already bound contracts are not allowed to bind again
* Unbound contracts are assigned by default in a shard named "" (empty)
* Contracts deployed within a shard belong to the same shard

```
[group0]: /apps> linkShard 0xd24180cc0fef2f3e545de4f9aafc09345cd08903 hello_shard
Add 0xd24180cc0fef2f3e545de4f9aafc09345cd08903 to hello_shard Ok. You can use 'ls' to check
```

### 3. getContractShard

Get which shard it belongs to based on the contract address

Parameters

* Contract Address: The contract address to be queried

```
[group0]: /apps> getContractShard d24180cc0fef2f3e545de4f9aafc09345cd08903
/shards/hello_shard
```

## Asset Management Precompiled Commands

This operation is the management operation of "Asset Management," please refer to [here]()Learn more technical details。

### 1. listBalanceGovernor

View accounts with asset operation permissions, any account can view。

```shell
[group0]: /apps>  listBalanceGovernor
listBalanceGovernor: [0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e]
```

注意

* After you turn on the feature _ balance _ precompiled switch, the chain management account is added to the asset management account by default.
  interface to add another account。
* Up to 500 accounts can be displayed, more than 500 accounts will not be able to register again。

### 2. registerBalanceGovernor

Registered account asset management permission, only the chain administrator account has this permission。

Parameters

* Account address / contract address: the account address or contract address to be registered

```shell
[group0]: /apps> registerBalanceGovernor 0x7ef1de472584a76dc5ff06f21ca899695ce5e730
transaction hash:0xa200a7dc58e100d41c544b583cc74a0e6aa3b1aa82fb7b7e04c117b75b5df898
register balanceGovernor 0x7ef1de472584a76dc5ff06f21ca899695ce5e730 success.

[group0]: /apps> listBalanceGovernor 
listBalanceGovernor: [0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e, 0x7ef1de472584a76dc5ff06f21ca899695ce5e730]
```

注意

* Only the chain administrator account has permission to call this interface
* Up to 500 accounts registered as balanceGovernor, more than 500 accounts will not be registered

### 3. unregisterBalanceGovernor

Cancels the asset management permission of the registered account, and only the chain administrator account has this permission.。

Parameters

* Account address / contract address: the account address or contract address to be cancelled

```shell
[group0]: /apps> unregisterBalanceGovernor 0x7ef1de472584a76dc5ff06f21ca899695ce5e730
transaction hash:0x95bcb68caa65451785c23a0a370ec9e7cb6010c81dadf151461b4e270603dffb
unregister balanceGovernor 0x7ef1de472584a76dc5ff06f21ca899695ce5e730 success.

[group0]: /apps> listBalanceGovernor 
listBalanceGovernor: [0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e]
```

注意

* When balanceGovernor has only one account, it can no longer be cancelled。

### 4. getBalance

Query the asset balance of an account, any account can view。

Parameters

* Account address: the account address to be queried

```shell
[group0]: /apps> getBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e
balance: 0 wei
```

### 5. addBalance

Increase the asset balance of the account, only the asset management permission account has the permission to call the interface.。

Parameters

* Account address: the address of the account where the asset needs to be added
* Number of assets added: the number of assets to be added, the default unit is wei
* Unit of asset quantity: optional. The unit of asset quantity. The default value is wei. Wei, kwei, mwei, gwei, szabo, finney, ether, Kether, Mether, and Gether are supported.

```shell
[group0]: /apps> addBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e 100 wei
transaction hash:0x42265ad297666f16b020d9619180716548a639d5018ed853125f7792b77d9d62
add balance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e success. You can use 'getBalance' to check

[group0]: /apps> getBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e
balance: 100 wei

[group0]: /apps> addBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e 100 kwei
transaction hash:0x7b9491466dddabedaac8aed942a1e4eb819ea5d75d1a8986a889d59bd601dd19
add balance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e success. You can use 'getBalance' to check

[group0]: /apps> getBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e
balance: 100100 wei
```

### 6. subBalance

Reduce the asset balance of the specified account, only the asset management permission account has the permission to call the interface.。

Parameters

* Account address: the address of the account where the assets need to be reduced
* Number of assets reduced: the number of assets that need to be reduced, the default unit is wei
* Unit of asset quantity: optional. The unit of asset quantity. The default value is wei. Wei, kwei, mwei, gwei, szabo, finney, ether, Kether, Mether, and Gether are supported.

```shell
[group0]: /apps> subBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e 100
transaction hash:0x4b723c968e4a1d058e1fbcb7f2babe7717d11f53c63b91c395c74a4d13cabdf5
sub balance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e success. You can use 'getBalance' to check

[group0]: /apps> getBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e
balance: 100000 wei

[group0]: /apps> subBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e 1 kwei
transaction hash:0x2fc4fe40fdefee492b363f6f42d4f814194384e72c27fd46b9ffc644d63b2492
sub balance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e success. You can use 'getBalance' to check

[group0]: /apps> getBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e
balance: 99000 wei
```

### 7. transferBalance

Transfer, transfer assets from one account to another, only the asset management permission account has the permission to call the interface.。

Parameters

* Transfer-out account address: the address of the account where the assets need to be transferred out
* Transfer to account address: the address of the account to which the asset needs to be transferred.
* Number of transferred assets: the number of assets to be transferred, the default unit is wei
* Unit of asset quantity: optional. The unit of asset quantity. The default value is wei. Wei, kwei, mwei, gwei, szabo, finney, ether, Kether, Mether, and Gether are supported.

```shell
[group0]: /apps> getBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e
balance: 99000 wei

[group0]: /apps> getBalance 0x7ef1de472584a76dc5ff06f21ca899695ce5e730
balance: 0 wei

[group0]: /apps> transferBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e 0x7ef1de472584a76dc5ff06f21ca899695ce5e730 9 kwei
transaction hash:0xff7c7818a573da072b673c3ba2f4f50694948c12ae439371c7b13cc50f917bc1
transfer 9kwei from 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e to 0x7ef1de472584a76dc5ff06f21ca899695ce5e730 success. You can use 'getBalance' to check

[group0]: /apps> getBalance 0x77ed4ea0a43fb76a88ec81a466695a4a704bb30e
balance: 90000 wei

[group0]: /apps> getBalance 0x7ef1de472584a76dc5ff06f21ca899695ce5e730
balance: 9000 wei
```