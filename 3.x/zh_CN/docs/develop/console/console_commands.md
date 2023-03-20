# 控制台命令列表

标签：``console`` ``控制台命令`` ``命令行交互工具``

---------

```eval_rst
.. important::
    - ``控制台`` 只支持FISCO BCOS 3.x版本，基于 `Java SDK <../sdk/java_sdk/index.html>`_ 实现。
    - 可通过命令 ``./start.sh --version`` 查看当前控制台版本
```

## 控制台命令结构

控制台命令由两部分组成，即指令和指令相关的参数：

- **指令**: 指令是执行的操作命令，包括查询区块链相关信息，部署合约和调用合约的指令等，其中部分指令调用JSON-RPC接口，因此与JSON-RPC接口同名。
  **使用提示： 指令可以使用tab键补全，并且支持按上下键显示历史输入指令。**

- **指令相关的参数**: 指令调用接口需要的参数，指令与参数以及参数与参数之间均用空格分隔，与JSON-RPC接口同名命令的输入参数和获取信息字段的详细解释参考[JSON-RPC API](../api.md)。

### 控制台常用命令

### 合约相关命令

- 普通部署和调用合约
  - 部署合约: [deploy](./console_commands.html#deploy)
  - 调用合约: [call](./console_commands.html#call)

### 其他命令

- 查询区块高度：[getBlockNumber](./console_commands.html#getblocknumber)
- 查询共识节点列表：[getSealerList](./console_commands.html#getsealerlist)
- 查询交易回执信息: [getTransactionReceipt](./console_commands.html#gettransactionreceipt)
- 切换群组: [switch](./console_commands.html#switch)

### 快捷键

- `Ctrl+A`：光标移动到行首
- `Ctrl+D`：退出控制台
- `Ctrl+E`：光标移动到行尾
- `Ctrl+R`：搜索输入的历史命令
- &uarr;：向前浏览历史命令
- &darr;：向后浏览历史命令

### 控制台响应

当发起一个控制台命令时，控制台会获取命令执行的结果，并且在终端展示执行结果，执行结果分为2类：

- **正确结果:** 命令返回正确的执行结果，以字符串或是json的形式返回。
- **错误结果:** 命令返回错误的执行结果，以字符串或是json的形式返回。
  - 控制台的命令调用JSON-RPC接口时，错误码[参考这里](../api.html#rpc)。
  - 控制台的命令调用Precompiled Service接口时，错误码[参考这里](../api.html#id5)。

## 控制台基础命令

### 1. help

输入help或者h，查看控制台所有的命令。

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
---------------------------------------------------------------------------------------------
```

**注：**

- help显示每条命令的含义是：命令 命令功能描述
- 查看具体命令的使用介绍说明，输入命令 -h或\--help查看。例如：

```shell
[group0]: /apps> getBlockByNumber -h
Query information about a block by block number.
Usage: 
getBlockByNumber blockNumber [boolean]
* blockNumber -- Integer of a block number, from 0 to 2147483647.
* boolean -- (optional) If true it returns only the hashes of the transactions, if false then return the full transaction objects.
```

### 2. switch

运行switch或者s，切换到指定群组。群组号显示在命令提示符前面。

```shell
[group0]: /apps>  switch group2
Switched to group2.

[group2]>
```

### 3. quit

运行quit、q或exit，退出控制台。

```shell
quit
```

## 合约操作命令

### 1. deploy

部署合约。(默认提供HelloWorld合约和KVTableTest进行示例使用)

**新增Liquid部署方式**：当连接节点在配置开启 “is_wasm=true”选项时，可自动启动控制台使用，具体配置项请参考：[节点配置](../../tutorial/air/config.md)

**Solidity部署参数：**

- 合约路径：合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`，比如：HelloWorld。
- 开启静态分析：可选项，默认为关闭。若开启，则会开启静态分析并行字段冲突域，加速合约并行执行速度。静态分析需要耗时较久，请耐心等待一段时间。

```shell
# 部署HelloWorld合约，默认路径
[group0]: /apps> deploy HelloWorld
contract address:0xc0ce097a5757e2b6e189aa70c7d55770ace47767

# 部署HelloWorld合约，相对路径
[group0]: /apps> deploy contracts/solidity/HelloWorld.sol
contract address:0x4721D1A77e0E76851D460073E64Ea06d9C104194

# 部署HelloWorld合约，绝对路径
[group0]: /apps> deploy ~/fisco/console/contracts/solidity/HelloWorld.sol
contract address:0x85517d3070309a89357c829e4b9e2d23ee01d881

# 开启静态分析选项
[group0]: /apps> deploy HelloWorld -p
contract address: 0x0102e8B6fC8cdF9626fDdC1C3Ea8C1E79b3FCE94
```

**注：**

- 部署用户编写的合约，可以将solidity合约文件放到控制台根目录的`contracts/solidity/`目录下，然后进行部署即可。按tab键可以搜索`contracts/solidity/`目录下的合约名称。
- 若需要部署的合约引用了其他合约或library库，引用格式为`import "./XXX.sol";`。其相关引入的合约和library库均放在`contracts/solidity/`目录。
- 如果合约引用了library库，library库文件的名称必须以`Lib`字符串开始，以便于区分是普通合约与library库文件。library库文件不能单独部署和调用。

**Liquid部署参数：**

- 二进制文件所在文件夹路径：cargo-liquid编译过后的wasm文件，以及ABI文件，均需要放在同一个路径下，支持绝对路径、相对路径
- 部署BFS路径：BFS文件系统中的路径名
- 部署构造参数：部署时所需要的构造参数

```shell
# 部署HelloWorld合约，相对路径
[group0]: /apps> deploy asset asset_test
transaction hash: 0x7498487dbf79378b5b50c4e36c09ea90842a343de307ee66d560d005d3c40ccb
contract address: /asset_test
currentAccount: 0x52d8001791a646d7e0d63e164731b8b7509c8bda
```

**deploy with BFS:**

支持在部署合约时在BFS创建别名, 使用参数 `-l` 将HelloWorld部署后的地址link到/apps/hello/v1目录：

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

运行call，调用合约。

**新增Liquid部署方式**：当连接节点在配置开启 “is_wasm=true”选项时，可自动启动控制台使用，具体配置项请参考：[节点配置](../../tutorial/air/config.md)

**Solidity调用参数：**

- 合约路径：合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`。
- 合约地址: 部署合约获取的地址。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定。**参数由空格分隔；数组参数需要加上中括号，比如[1,2,3]，数组中是字符串或字节类型，加双引号，例如[“alice”,”bob”]，注意数组参数中不要有空格；布尔类型为true或者false。**

```shell
# 调用HelloWorld的get接口获取name字符串
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

# 调用HelloWorld的set接口设置name字符串
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

# 调用HelloWorld的get接口获取name字符串，检查设置是否生效
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

**Liquid参数:**

- 合约路径: 部署合约时填入的BFS文件系统中的路径名。
- 合约接口名：调用的合约接口名。
- 参数：由合约接口参数决定。**参数由空格分隔；数组参数需要加上中括号，比如[1,2,3]，数组中是字符串或字节类型，加双引号，例如[“alice”,”bob”]，注意数组参数中不要有空格；布尔类型为true或者false。**

```shell
# 调用 /apps/asset_test 路径下的合约，注册一个新的资产 asset1
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

# 调用 /apps/asset_test 路径下的合约，查询资产 asset1
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

可以调用在BFS目录中创建的link文件，调用姿势和调用普通合约类似。

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

运行getCode，根据合约地址查询合约二进制代码。
参数：

- 合约地址：0x的合约地址(部署合约可以获得合约地址)。

```shell
[group0]: /apps>  getCode 0x97b8c19598fd781aaeb53a1957ef9c8acc59f705
0x60606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100465780636d4ce63c14610066575bfe5b341561004e57fe5b610064600480803590602001909190505061008c565b005b341561006e57fe5b61007661028c565b6040518082815260200191505060405180910390f35b8060006001015410806100aa57506002600101548160026001015401105b156100b457610289565b806000600101540360006001018190555080600260010160008282540192505081905550600480548060010182816100ec919061029a565b916000526020600020906004020160005b608060405190810160405280604060405190810160405280600881526020017f32303137303431330000000000000000000000000000000000000000000000008152508152602001600060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815250909190915060008201518160000190805190602001906101ec9291906102cc565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b600060026001015490505b90565b8154818355818115116102c7576004028160040283600052602060002091820191016102c6919061034c565b5b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061030d57805160ff191683800117855561033b565b8280016001018555821561033b579182015b8281111561033a57825182559160200191906001019061031f565b5b50905061034891906103d2565b5090565b6103cf91905b808211156103cb57600060008201600061036c91906103f7565b6001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556002820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600382016000905550600401610352565b5090565b90565b6103f491905b808211156103f05760008160009055506001016103d8565b5090565b90565b50805460018160011615610100020316600290046000825580601f1061041d575061043c565b601f01602090049060005260206000209081019061043b91906103d2565b5b505600a165627a7a723058203c1f95b4a803493db0120df571d9f5155734152548a532412f2f9fa2dbe1ac730029
```

### 4. listAbi

显示合约接口和Event列表
参数：

- 合约路径：合约文件的路径，支持相对路径、绝对路径和默认路径三种方式。用户输入为文件名时，从默认目录获取文件，默认目录为: `contracts/solidity`，比如：TableTest。
- 合约名：(可选)合约名称，默认情况下使用合约文件名作为合约名参数
- 合约地址：(可选)在部署后的合约地址，listAbi会向节点发起getAbi的请求

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

# 使用地址参数
[group0]: /apps> listAbi cceef68c9b4811b32c75df284a1396c7c5509561
Method list:
 name                |    constant  |    methodId    |    signature
  --------------------------------------------------------------
 set                 |    false     |    4ed3885e    |    set(string)
 get                 |    true      |    6d4ce63c    |    get()

```

### 5. getDeployLog

运行getDeployLog，查询群组内**由当前控制台**部署合约的日志信息。日志信息包括部署合约的时间，群组ID，合约名称和合约地址。参数：

- 日志行数，可选，根据输入的期望值返回最新的日志信息，当实际条数少于期望值时，按照实际数量返回。当期望值未给出时，默认按照20条返回最新的日志信息。

```shell
[group0]: /apps>  getDeployLog 2

2019-05-26 08:37:03  [group]  HelloWorld            0xc0ce097a5757e2b6e189aa70c7d55770ace47767
2019-05-26 08:37:45  [group]  KVTableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576

[group0]: /apps>  getDeployLog 1

2019-05-26 08:37:45  [group]  KVTableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576
```

**注：** 如果要查看所有的部署合约日志信息，请查看`console`目录下的`deploylog.txt`文件。该文件只存储最近10000条部署合约的日志记录。

### 6. listDeployContractAddress

列出指定合约名部署的所有合约地址，
列出部署指定合约产生的合约地址列表，参数：

- contractNameOrPath: 合约名或合约绝对路径，用于指定合约;
- recordNumber: 显示的合约地址列表长度，默认为20

```shell
# 获取部署HelloWorld合约产生的合约地址列表
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


# 限制显示的合约地址列表长度为2
[group0]: /apps>  listDeployContractAddress HelloWorld 2
0xe157185434183b276b9e5af7d0315a9829171281  2020-10-13 15:35:29
0x136b042e1fc480b03e1e5b075cbdfa52f5851a23  2020-10-13 15:35:22
```

## 区块链状态查询命令

### 1. getBlockNumber

运行getBlockNumber，查看区块高度。

```shell
[group0]: /apps>  getBlockNumber
90
```

### 2. getSyncStatus

运行getSyncStatus，查看同步状态。

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

运行getPeers，查看节点的peers。

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

运行getBlockByHash，根据区块哈希查询区块信息。
参数：

- 区块哈希：0x开头的区块哈希值。
- 交易标志：默认false，区块中的交易只显示交易哈希，设置为true，显示交易具体信息。

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

运行getBlockByNumber，根据区块高度查询区块信息。
参数：

- 区块高度：十进制整数。
- 交易标志：默认false，区块中的交易只显示交易哈希，设置为true，显示交易具体信息。

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

运行getBlockHashByNumber，通过区块高度获得区块哈希。
参数：

- 区块高度：十进制整数。

```shell
[group0]: /apps>  getBlockHashByNumber 1
0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855
```

### 7. getTransactionByHash

运行getTransactionByHash，通过交易哈希查询交易信息。
参数：

- 交易哈希：0x开头的交易哈希值。

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

运行getTransactionReceipt，通过交易哈希查询交易回执。
参数：

- 交易哈希：0x开头的交易哈希值。

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

运行getPendingTxSize，查询等待处理的交易数量（交易池中的交易数量）。

```shell
[group0]: /apps>  getPendingTxSize
0
```

### 10. getTotalTransactionCount

运行getTotalTransactionCount，查询当前块高和累计交易数（从块高为0开始）。

```shell
[group0]: /apps>  getTotalTransactionCount
{
    "blockNumber":1,
    "txSum":1,
    "failedTxSum":0
}
```

### 11. setSystemConfigByKey

运行setSystemConfigByKey，以键值对方式设置系统参数。目前设置的系统参数支持`tx_count_limit`,`consensus_leader_period`。这些系统参数的键名可以通过tab键补全：

- `tx_count_limit`: 区块最大打包交易数
- `consensus_leader_period`: 共识选主间隔
- `gas_limit`: 交易执行的gas限制
- `compatibility_version`: 数据兼容版本号，当区块链所有二进制均升级到最新版本后，可通过`setSystemConfigByKey`升级数据兼容版本号到最新

参数：

- key
- value

```shell
[group0]: /apps>  setSystemConfigByKey tx_count_limit 100
{
    "code":0,
    "msg":"success"
}
```

### 12. getSystemConfigByKey

运行getSystemConfigByKey，根据键查询系统参数的值。
参数：

- key

```shell
[group0]: /apps>  getSystemConfigByKey tx_count_limit
100
```

## 共识操作命令

### 1. getSealerList

运行getSealerList，查看共识节点列表。

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

运行getObserverList，查看观察节点列表。

```shell
[group0]: /apps>  getObserverList
[  
    bb21228b0762433ea6e4cb185e1c54aeb83cd964ec0e831f8732cb2522795bb569d58215dfbeb7d3fc474fdce33dc9a793d4f0e86ce69834eddc707b48915824
]
```

### 3. getPbftView

运行getPbftView，查看pbft视图。

```shell
[group0]: /apps>  getPbftView
2730
```

### 4. getConsensusStatus

运行getConsensusStatus，查看共识状态。

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

运行addSealer，将节点添加为共识节点。
参数：

- 节点nodeId
- 节点权重

```shell
[group0]: /apps> addSealer bb21228b0762433ea6e4cb185e1c54aeb83cd964ec0e831f8732cb2522795bb569d58215dfbeb7d3fc474fdce33dc9a793d4f0e86ce69834eddc707b48915824 2
{
    "code":0,
    "msg":"Success"
}
```

### 6. addObserver

运行addObserver，将节点添加为观察节点。
参数：

- 节点nodeId

```shell
[group0]: /apps>  addObserver ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
    "code":0,
    "msg":"success"
}
```

### 7. removeNode

运行removeNode，节点退出。通过addSealer命令可以将退出的节点添加为共识节点，通过addObserver命令将节点添加为观察节点。
参数：

- 节点nodeId

```shell
[group0]: /apps>  removeNode ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
    "code":0,
    "msg":"success"
}
```

### 8. setConsensusWeight

运行setConsensusWeight，设置某一个特定节点的共识权重。

```shell
[group0]: /apps> setConsensusWeight 44c3c0d914d7a3818923f9f45927724bddeeb25df92b93f1242c32b63f726935d6742b51cd40d2c828b52ed6cde94f4d6fb4b3bfdc0689cfcddf7425eafdae85 2
{
    "code":0,
    "msg":"Success"
}
```

## 合约表操作命令

### 1. [create sql]

运行create sql语句创建用户表，使用mysql语句形式。

```bash
# 创建用户表t_demo，其主键为name，其他字段为item_id和item_name
[group0]: /apps> create table t_demo(name varchar, item_id varchar, item_name varchar, primary key(name))
Create 't_demo' Ok.

# 创建好的t_demo表会在绝对路径 /tables/ 底下可以观察到
[group0]: /apps> ls ../tables/
t_demo
```

**注意：**

- 创建的表名带上前缀的长度不能超过50，例如： /tables/t_demo 的长度不能超过50。
- 创建表的字段类型均为字符串类型，即使提供数据库其他字段类型，也按照字符串类型设置。
- 必须指定主键字段。例如创建t_demo表，主键字段为name。
- 表的主键与关系型数据库中的主键不是相同概念，这里主键取值不唯一，区块链底层处理记录时需要传入主键值。
- 可以指定字段为主键，但设置的字段自增，非空，索引等修饰关键字不起作用。

### 2. [alter sql]

运行alter sql语句修改用户表，使用mysql语句形式。

```bash
# 修改用户表t_demo，新增字段comment
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

**注意：**

- 修改的表必须存在，且目前**只支持新增字段**
- 创建表的字段类型均为字符串类型，即使提供数据库其他字段类型，也按照字符串类型设置，且不能重复

### 3. desc

运行desc语句查询表的字段信息，使用mysql语句形式。

```shell
# 查询t_demo表的字段信息，可以查看表的主键名和其他字段名
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

运行insert sql语句插入记录，使用mysql语句形式。

```text
[group0]: /apps> insert into t_demo (name, item_id, item_name) values (fruit, 1, apple1)
Insert OK:
1 row(s) affected.
```

**注意：**

- 插入记录sql语句必须插入表的主键字段值。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

### 5. [select sql]

运行select sql语句查询记录，使用mysql语句形式。

与一般的SQL不同的是，遍历接口的条件语句目前只支持key字段的条件。

```text
# 查询包含所有字段的记录
[group0]: /apps> select * from t_demo where name = fruit
{name=fruit, item_id=1, item_name=apple1}
1 row(s) in set.

# 查询包含指定字段的记录
[group0]: /apps> select name, item_id, item_name from t_demo where name = fruit
{name=fruit, item_id=1, item_name=apple1}
1 row(s) in set.

# 插入一条新记录
[group0]: /apps> insert into t_demo values (fruit2, 2, apple2)
Insert OK, 1 row affected.

# 使用and关键字连接多个查询条件
[group0]: /apps> select * from t_demo where name >= fruit
{name=fruit, item_id=1, item_name=apple1}
{name=fruit2, item_id=2, item_name=apple2}
2 row(s) in set.

# 使用limit字段，查询第1行记录，没有提供偏移量默认为0
[group0]: /apps> select * from t_demo where name = fruit limit 1
{item_id=1, item_name=apple1, name=fruit}
1 row in set.

# 使用limit字段，查询第2行记录，偏移量为1
[group0]: /apps> select * from t_demo where name = fruit limit 1,1
{item_id=1, item_name=apple1, name=fruit}
1 rows in set.
```

**注意：**

- 查询记录sql语句必须在where子句中提供表的主键字段值。
- 关系型数据库中的limit字段可以使用，提供两个参数，分别offset(偏移量)和记录数(count)。
- where条件子句只支持and关键字，其他or、in、like、inner、join，union以及子查询、多表联合查询等均不支持。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

### 6. [update sql]

运行update sql语句更新记录，使用mysql语句形式。

```text
[group:1]> update t_demo set item_name = orange where name = fruit
Update OK, 1 row affected.
```

**注意：**

- 更新记录sql语句的where子句目前只支持表的主键字段值条件。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

### 7. [delete sql]

运行delete sql语句删除记录，使用mysql语句形式。

```text
[group:1]> delete from t_demo where name = fruit
Remove OK, 1 row affected.
```

**注意：**

- 删除记录sql语句的where子句目前只支持表的主键字段值条件。
- 输入的值带标点符号、空格或者以数字开头的包含字母的字符串，需要加上双引号，双引号中不允许再用双引号。

## BFS操作命令

### 1. cd

与Linux的cd命令类似，可以切换当前所在的路径，支持绝对路径、相对路径。

```shell
[group0]: /apps> cd ../tables

[group0]: /tables>

[group0]: /apps> cd ../tables/test

[group0]: /tables/test>

[group0]: /tables/test> cd ../../

[group0]: />
```

### 2. ls

与Linux的ls命令相似，可以查看当前路径下的资源，如果是目录，则展示出该目录下所有资源；如果是合约，则展示该合约的元信息。

ls参数为0时，展示当前文件夹，ls 参数为1时，支持绝对路径和相对路径。

```shell
[group0]: /> ls
apps usr sys tables

[group0]: /> ls apps
Hello

[group0]: /> ls ./apps/Hello
name: Hello, type: contract
```

### 3. mkdir

与Linux的mkdir命令相似，在某个文件夹下创建新的目录，支持绝对路径和相对路径。

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

与Linux的ln命令相似，创建某个合约资源的链接，可以通过直接调用链接发起对实际合约的调用。

与2.0版本的CNS服务类似，依靠BFS多层级目录，可以建立合约名与合约地址、合约版本号的映射关系。

例如，合约名为Hello，合约版本号为latest，可以在`/apps`目录下建立`/apps/Hello/latest` 的软连接，软连接对应的合约地址可覆盖写。同理，用户可以在 `/apps/Hello` 下建立多个版本，例如：`/apps/Hello/newOne`、`/apps/Hello/layerTwo`等等。

```bash
# 创建合约软链接，合约名为Hello，合约版本为latest
[group0]: /apps> ln Hello/latest 0x19a6434154de51c7a7406edF312F01527441B561
{
    "code":0,
    "msg":"Success"
}

# 创建的软链会在/apps/目录下创建链接文件
[group0]: /apps> ls ./Hello/latest 
latest -> 19a6434154de51c7a7406edf312f01527441b561      

# 可以直接调用链接
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

# 可以覆盖版本号
[group0]: /apps> ln Hello/latest  0x2b5DCbaE97f9d9178E8b051b08c9Fb4089BAE71b
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> ls ./Hello/latest 
latest -> 2b5dcbae97f9d9178e8b051b08c9fb4089bae71b  
```

### 5. tree

与Linux的tree命令相似，将指定BFS路径下的资源以树形结构的形式展示出来。默认深度为3，可使用参数设置深度，不超过5。

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

# 使用深度为1
[group0]: /apps> tree .. 1
/
├─apps
├─sys
├─tables
└─usr
```

### 6. pwd

与Linux的pwd命令相似，没有参数，展示当前路径。

```shell
[group0]: /apps/Hello/BFS>  pwd
/apps/Hello/BFS
```

## 群组查询命令

### 1. getGroupPeers

运行getGroupPeers，查看节点所在group的共识节点和观察节点列表。

```shell
[group0]: /apps> getGroupPeers 
peer0: 07844e249ca404fd54ac9f430cbc0dde9c23ca28e872f1d1bafd974aae6149bc3d0442a4b278873830c0f0642cbde3fda4884cec508b1bc64c56ad23f4256d0a
peer1: 0ba87f0f2a218c70d207d5df01f74c9b5799bc0853af27f599422a5a0e8224d0ffe12d6aa0765b90487bfcfb9562e01dd98af6693ab54976d305b372b40e460c
peer2: 1fa15731ae79ac7a9c6affa5511b50b1d549e28906f1968db8ddce69e18601707803c032afc42a230b74d4579a1bcca04b2e848019e5b1215b7cab96d59fab5a
peer3: bad149badf702520cdf3d0a72a4790c2cd68bc23f9e2dd9b796b46ac9da78c98b089720981d987a546d3b3355406d0b428767a18916459c2cbade04432d80627
peer4: d177c9ce82f3ae1aa02a37544d52496afa8c102cf08c2b8b3b9822874c15188863d052cad4c3f78abff0510a6c1bc04892129c183ec53baffeff8c5a000f069a
```

### 2. getGroupInfo

运行getGroupInfo，查看节点所在group的详细信息。

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

运行getGroupList，查看群组列表：

```shell
[group0]: /apps> getGroupList
[
    group0
]
```

### 4. getGroupInfoList

运行getGroupList，查看所有的群组信息列表：

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

运行getGroupNodeInfo命令，获取当前群组内某一个节点的信息：

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

## 权限操作命令

权限治理操作命令分为：查询权限治理状态命令、治理委员专用命令、合约管理员专用命令。

### 1. 查询权限治理命令

该种类的命令没有权限控制，所有账户均可访问。

#### 1.1. getCommitteeInfo

在初始化时，将会部署一个治理委员，该治理委员的地址信息在 build_chain.sh时自动生成或者指定。初始化只有一个委员，并且委员的权重为1

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

批量获取某个特定范围内的提案信息，若只输入单个ID则返回单个ID的提案信息。

`proposalType` 和  `status` 可以看到提案的类型和状态

proposalType分为以下几种：

- setWeight：当治理委员发起updateGovernorProposal 提案时会生成
- setRate：setRateProposal 提案会生成
- setDeployAuthType：setDeployAuthTypeProposal 提案会生成
- modifyDeployAuth：openDeployAuthProposal 和closeDeployAuthProposal 提案会生成
- resetAdmin：resetAdminProposal 提案会生成
- setConfig: setSysConfigProposal 提案会生成
- setNodeWeight: addObserverProposal、addSealerProposal、setConsensusNodeWeightProposal 提案生成
- removeNode：removeNodeProposal 提案生成
- unknown：这个类型出现时，有可能是有bug

status分为以下几种：

- notEnoughVotes：提案正常，还未收集到足够的投票
- finish：提案执行完成
- failed：提案失败
- revoke：提案被撤回
- outdated：提案超过投票期限
- unknown：这个类型出现时，有可能是有bug

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

# 批量获取
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

为了避免发起提案超时、退出控制台遗忘提案ID，getLatestProposal的命令可以获取当前委员会的最新的提案信息。

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

权限策略分为：

- 无权限，所有人都可以部署
- 黑名单，在黑名单上的用户不可以部署
- 白名单，只有在白名单的用户可以部署

```shell
[group0]: /apps> getDeployAuth
There is no deploy strategy, everyone can deploy contracts.
```

治理委员会专用命令，必须要有治理委员会的Governors中的账户才可以调用

如果治理委员只有一个，且提案是该委员发起的，那么这个提案一定能成功

#### 1.5. checkDeployAuth

检查账户是否有部署权限

```shell
# 当前的部署权限为白名单模式
[group0]: /apps> getDeployAuth 
Deploy strategy is White List Access.

# 如果不选择参数，则检查当前的账号是否有部署权限
[group0]: /apps> checkDeployAuth 
Deploy : PERMISSION DENIED
Account: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

# 检查其他账户是否有权限
[group0]: /apps> checkDeployAuth 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
Deploy : ACCESS
Account: 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
```

#### 1.6. getContractAdmin

使用命令可获取某个合约的管理员，只有管理员才可以对该合约进行权限控制操作。

```shell
# 合约地址 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 的admin账号是 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
[group0]: /apps> getContractAdmin 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
Admin for contract 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
```

#### 1.7. checkMethodAuth

检查账户是否有调用某个合约接口的权限

```shell
# 设置0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b 地址的合约的 set(string) 为白名单模式
[group0]: /apps> setMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b set(string) white_list
{
    "code":0,
    "msg":"Success"
}

# 如果不选择参数，则检查当前账号是否有调用权限
[group0]: /apps> checkMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b set(string)
Method   : PERMISSION DENIED
Account  : 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
Interface: set(string)
Contract : 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b

# 检查其他账号的权限
[group0]: /apps> checkMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b set(string) 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Method   : ACCESS
Account  : 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
Interface: set(string)
Contract : 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b
```

#### 1.8. getMethodAuth

获取某个合约接口的所有访问列表

```shell
# 开启账户对合约0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 set接口访问权限
[group0]: /apps> openMethodAuth 0x600E41F494CbEEd1936D5e0a293AEe0ab1746c7b "set(string)" 0x5298906acaa14e9b1a3c1462c6938e044bd41967
{
    "code":0,
    "msg":"Success"
}
# 获取合约0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 set接口的权限列表
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

获取某个合约的状态，目前只有（冻结、正常访问）两种状态

```shell
[group0]: /apps> getContractStatus 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01
Available

[group0]: /apps> freezeContract 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01
{
    "code":0,
    "msg":"Success"
}

[group0]: /apps> getContractStatus 0x31eD5233b81c79D5adDDeeF991f531A9BBc2aD01
Freeze
```

### 2. 治理委员专用命令

这些命令只能持有治理委员的账户才可以使用。

#### 2.1. updateGovernorProposal

如果是新加治理委员，新增地址和权重即可。

如果是删除治理委员，将一个治理委员的权重设置为0 即可

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

设置提案阈值，提案阈值分为参与阈值和权重阈值。

阈值在这里是以百分比为单位进行使用的。

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

设置部署的ACL策略，只支持 white_list 和 black_list 两种策略

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

开启某个管理员部署权限的提案

```shell
# 在白名单模式下，只有白名单内的用户可以使用
[group0]: /apps> deploy HelloWorld 
deploy contract for HelloWorld failed!
return message: Permission denied
return code:18
Return values:null

# 治理委员开启自己（也可以其他账号）的部署权限
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

# 再次部署，可以成功
[group0]: /apps> deploy HelloWorld 
transaction hash: 0x732e29baa263fc81e17a93a4102a0aa1cc2ec33ffbc1408c6b206d124b7f7ae0
contract address: 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
currentAccount: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

```

#### 2.5. closeDeployAuthProposal

 关闭某个管理员部署权限的提案

```shell
# 账号可以部署合约
[group0]: /apps> deploy HelloWorld 
transaction hash: 0x732e29baa263fc81e17a93a4102a0aa1cc2ec33ffbc1408c6b206d124b7f7ae0
contract address: 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
currentAccount: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

# 关闭账号的部署权限
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

# 该账号不再可以部署合约
[group0]: /apps> deploy HelloWorld 
deploy contract for HelloWorld failed!
return message: Permission denied
return code:18
Return values:null

```

#### 2.6. resetAdminProposal

重置某个合约的管理员的提案

```shell
# 合约地址 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 的admin账号是 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
[group0]: /apps> getContractAdmin 0xCcEeF68C9b4811b32c75df284a1396C7C5509561
Admin for contract 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 is: 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6

# reset 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 的admin账号为 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a
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

发起新增共识节点Sealer的提案

```shell
# 新增共识节点Sealer，权重为1
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

# 确认增加成功
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

发起新增观察节点Observer的提案

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

# 确认观察节点已加入
[group0]: /apps> getObserverList
[
 6471685bb764ddd625c8855809ae23ae803fcf2890977def7c3d4353e22633cdea92471ba0859fc0538679c31b89577e1dd13b292d6538acff42ac4c599d5ce8
]
```

#### 2.9. setConsensusNodeWeightProposal

发起修改共识节点权重的提案

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

# 确认已经更新到权重2
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

发起删除节点的提案

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

发起更改系统配置的提案

```shell
# 更改tx_count_limit为2000
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

# 查看已经更改成功
[group0]: /apps> getSystemConfigByKey tx_count_limit
2000
```

#### 2.12. upgradeVoteProposal

发起升级投票计算逻辑的提案。升级提案投票计算逻辑分为以下几步：

1. 基于接口编写合约；
2. 将写好的合约部署在链上，并得到合约的地址；
3. 发起升级投票计算逻辑的提案，将合约的地址作为参数输入，并在治理委员会中进行投票表决；
4. 投票通过后（此时投票计算逻辑还是原有逻辑），则升级投票计算逻辑；否则就不升级。

投票计算逻辑合约是按照一定的接口实现方可使用。合约实现可以参考下面的接口合约`VoteComputerTemplate.sol`进行实现：

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
    // 此为投票权重计算逻辑唯一入口，必须实现该接口，且规定：
    // 投票数不够，返回 1；投票通过，返回 2；投票不通过，返回 3；
    function determineVoteResult(
        address[] memory agreeVoters,
        address[] memory againstVoters
    ) public view virtual returns (uint8);
    
    // 此为计算逻辑的检验接口，用于其他治理委员验证该合约有效性
    function voteResultCalc(
        uint32 agreeVotes,
        uint32 doneVotes,
        uint32 allVotes,
        uint8 participatesRate,
        uint8 winRate
    ) public pure virtual returns (uint8);
}
```

现已有基于上面的`VoteComputerTemplate.sol`接口实现的合约如下：

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
    // 投票权重计算逻辑实现
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
    // 计算逻辑的检验接口实现
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

合约编写完成之后就可以将合约在链上进行部署，并更新到治理委员会中：

```shell
# 首先通过getCommitteeInfo命令 确认Committee合约的地址为0xa0974646d4462913a36c986ea260567cf471db1f
[group0]: /apps> getCommitteeInfo
---------------------------------------------------------------------------------------------
Committee address   : 0xa0974646d4462913a36c986ea260567cf471db1f
ProposalMgr address : 0x2568bd207f50455f1b933220d0aef11be8d096b2
---------------------------------------------------------------------------------------------
ParticipatesRate: 0% , WinRate: 0%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x4a37eba43c66df4b8394abdf8b239e3381ea4221     | 2

# 部署VoteComputer合约，第一个参数0x10001为固定地址，第二个参数为当前治理委员Committee的地址
[group0]: /apps> deploy VoteComputer 0x10001 0xa0974646d4462913a36c986ea260567cf471db1f
transaction hash: 0x429a7ceccefb3a4a1649599f18b60cac1af040cd86bb8283b9aab68f0ab35ae4
contract address: 0x6EA6907F036Ff456d2F0f0A858Afa9807Ff4b788
currentAccount: 0x4a37eba43c66df4b8394abdf8b239e3381ea4221

# 部署成功后，即可通过upgradeVoteProposal更新
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

治理委员可向某个提案进行投票，投票时可选择同意和不同意。

```shell
# 当前账号发起设置阈值变化的提案，提案ID为12
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

# 此时模拟另外一个治理委员账户登陆
[group0]: /apps> loadAccount 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a 
Load account 0xea9b0d13812f235e4f7eaa5b6131794c9c755e9a success!

# 该治理委员投票12为同意（默认第二个参数为true，第二个参数为false时表示拒绝该提案）
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

# 此时治理委员会的信息已经变更
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

撤销还未成功的提案，撤销提案的动作只能由发起提案的治理委员发起。

```shell
# 可以看到当前委员会的人数为2人，参与通过阈值和成功阈值均为51%
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

# 发起一个提案，此时需要另外一个委员会同意才能通过，状态为notEnoughVotes
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

# 当前账户主动取消提案，提案的状态变为revoke
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

冻结、解冻账户，冻结的账户不可以发起任何**写**交易，可通过unfreezeAccount解冻。
该命令只有治理委员才可以调用。

```shell
# 冻结账户0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> freezeAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
{
    "code":0,
    "msg":"Success"
}

# 加载到已经冻结的账户 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> loadAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22 success!

# 部署调用都被拒绝
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

# 解冻账户0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> unfreezeAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
{
    "code":0,
    "msg":"Success"
}

# 加载账户0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> loadAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22 success!

[group0]: /apps> deploy HelloWorld
transaction hash: 0xd978585392114e2379be8c94250f5abceaf84538567d737db7dfbafcc0b7399c
contract address: 0xd24180cc0fef2f3e545de4f9aafc09345cd08903
currentAccount: 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
```

#### 2.16. abolishAccount

废止账户，废止的账户不可以发起任何**写**交易，且不可恢复。
该命令只有治理委员才可以调用。

```shell
# 废止账户0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> abolishAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
{
    "code":0,
    "msg":"Success"
}

# 加载账户0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
[group0]: /apps> loadAccount 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22
Load account 0x3d20a4e26f41b57c2061e520c825fbfa5f321f22 success!

# 部署和调用都被拒绝
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

### 3. 合约管理员专用命令

这些命令只有对某一个合约具有管理权限的管理员账户才可以访问。在默认时，合约的部署发起账号即为合约的管理员。

#### 3.1. setMethodAuth

 管理员设置方法的权限策略

**特别注意：合约的接口权限控制目前只能对写方法进行控制。**

```shell
# 设置合约地址为 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 的HelloWorld合约的 set(string) 接口为白名单模式
[group0]: /apps> setMethodAuth 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set(string) white_list
{
    "code":0,
    "msg":"Success"
}

# 这个接口目前为白名单模式，只有白名单模式的账号可以调用 set 接口
[group0]: /apps> call HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set 123
transaction hash: 0x51e43a93b8e6621e45357ba542112117c3dd3e089b5067e06084e36243458074
---------------------------------------------------------------------------------------------
transaction status: 18
---------------------------------------------------------------------------------------------
Receipt message: Permission denied
Return message: Permission denied
---------------------------------------------------------------------------------------------

# 不影响该账号调用 get 接口
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

管理员开启用户可以访问合约的某个方法的权限

```shell
# 开启账号0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6 的 0xCcEeF68C9b4811b32c75df284a1396C7C5509561地址的 set(string)接口调用
[group0]: /apps> openMethodAuth 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set(string) 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
{
    "code":0,
    "msg":"Success"
}

# 该账号可以使用HelloWorld的set接口
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

管理员关闭用户可以访问合约的某个方法的权限

```shell
# 关闭HelloWorld 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 的set接口
[group0]: /apps> closeMethodAuth 0xCcEeF68C9b4811b32c75df284a1396C7C5509561 set(string) 0x7fb008862ff69353a02ddabbc6cb7dc31683d0f6
{
    "code":0,
    "msg":"Success"
}

# 该账户不再可以访问合约的set接口
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

运行freezeContract，对指定合约进行冻结操作。参数：

- 合约地址：部署合约可以获得合约地址，其中0x前缀非必须。

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

运行unfreezeContract，对指定合约进行解冻操作。参数：

- 合约地址：部署合约可以获得合约地址，其中0x前缀非必须。

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

## 账户操作命令

### 1. newAccount

创建新的发送交易的账户，默认会以`PEM`格式将账户保存在`account`目录下。

```shell
# 控制台连接非国密区块链时，账户文件自动保存在`account/ecdsa`目录
# 控制台连接国密区块链时，账户文件自动保存在`accout/gm`目录下
[group0]: /apps>  newAccount
AccountPath: account/ecdsa/0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642.pem
Note: This operation does not create an account in the blockchain, but only creates a local account, and deploying a contract through this account will create an account in the blockchain
newAccount: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
AccountType: ecdsa

$ ls -al account/ecdsa/0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642.pem
$ -rw-r--r--  1 octopus  staff  258  9 30 16:34 account/ecdsa/0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642.pem
```

### 2. loadAccount

加载`PEM`或者`P12`格式的私钥文件，加载的私钥可以用于发送交易签名。
参数：

- 私钥文件路径: 支持相对路径、绝对路径和默认路径三种方式。用户账户地址时，默认从`config.toml`的账户配置选项`keyStoreDir`加载账户，`keyStoreDir`配置项请参考[这里](./sdk/java_sdk/config.html#id9)。

- 账户格式: 可选，加载的账户文件类型，支持`pem`与`p12`，默认为`pem`。

```shell
[group0]: /apps>  loadAccount 0x6fad87071f790c3234108f41b76bb99874a6d813
Load account 0x6fad87071f790c3234108f41b76bb99874a6d813 success!
```

### 3. listAccount

查看当前加载的所有账户信息

```shell
[group0]: /apps>  listAccount
0x6fad87071f790c3234108f41b76bb99874a6d813(current account) <=
0x726d9f31cf44debf80b08a7e759fa98b360b0736
```

**注意：带有`<=`后缀标记的为当前用于发送交易的私钥账户，可以使用`loadAccount`进行切换**

### 4. getCurrentAccount

获取当前账户地址。

```shell
[group0]: /apps>  getCurrentAccount
0x6fad87071f790c3234108f41b76bb99874a6d813
```
