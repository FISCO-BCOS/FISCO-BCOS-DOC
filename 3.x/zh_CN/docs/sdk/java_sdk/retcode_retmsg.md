# 返回码与错误信息汇总

标签：``TransactionResponse`` ``响应码`` ``错误信息`` ``returnCode`` ``returnMessages`` ``交易回执``

----

## 1. 节点返回数据结构

节点通过RPC接口返回的数据，包括以下几种情况，详细原因将在下文解释：

- 节点未上链成功、RPC请求错误
- 节点上链成功，包括：执行失败出现交易回滚的情况、预编译合约执行错误的情况

节点未上链成功示例如下：

```json
{
        "error" :
        {
                "code" : 10002,
                "message" : "TxPoolIsFull"
        },
        "id" : 102,
        "jsonrpc" : "2.0"
}
```

节点上链成功示例如下：

```json
{
        "id" : 16,
        "jsonrpc" : "2.0",
        "result" :
        {
                "blockNumber" : 12,
                "checksumContractAddress" : "",
                "contractAddress" : "",
                "extraData" : "",
                "from" : "0xa38e104bb4a92a52452b48342c935f68df20c2ce",
                "gasUsed" : "1132",
                "hash" : "0x",
                "logEntries" : [],
                "message" : "",
                "output" : "0x08c379a00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000001a4279746573206172726179206973206c657373207468616e2032000000000000",
                "status" : 16,
                "to" : "0x19a6434154de51c7a7406edf312f01527441b561",
                "transactionHash" : "0x8075a4f016dec5147eb60df219529c3d504c5fad2c16691b39cff457692afeb1",
                "version" : 0
        }
}
```

用户在发起交易、RPC请求之后，节点从RPC接口返回的JSON结果分析需要进行以下几步：

- 判断JSON最外层是否存在error，如果存在则代表：交易在进入共识之前就已经失败了、RPC请求失败；用户根据error中的message做具体操作。
- 若有result结构体则代表交易上链成功、RPC请求返回成功
  - 若是交易，则解析result中的status，若为0则执行成功，其他则为执行错误
- 若调用的是预编译合约，则需要根据ABI解码output

## 2. 节点未上链成功错误码

| 错误码 | 错误原因                                           |
|--------|----------------------------------------------------|
| -32700 | 节点侧JSON解码失败，一般出现在RPC请求中            |
| -32600 | 错误请求，JSON缺漏字段等                           |
| -32601 | 请求的RPC方法是不存在的                            |
| -32603 | 内部错误，一般是节点有错误                         |
| -32000 | 节点还未启动，一般出现在Pro和Max节点               |
| -32004 | 服务还未初始化完成，一般出现在Pro和Max节点         |
| -32005 | 请求群组不存在                                     |
| 10000  | Nonce检查失败，一般时有重复发送交易请求            |
| 10001  | Block Limit检查失败，一般是SDK块高状态落后节点太多 |
| 10002  | 交易池已经满了                                     |
| 10003  | 未知错误                                           |
| 10004  | 交易已经存在于交易池中                             |
| 10005  | 交易已经在链上了                                   |
| 10006  | 错误的Chain ID                                     |
| 10007  | 错误的Group ID                                     |
| 10008  | 错误的交易签名                                     |
| 10009  | 交易请求发送到错误的群组中                         |
| 10010  | 交易在交易池中超过时间未被处理，一般是十分钟       |

## 3. 除交易之外的RPC请求错误码

| 错误码 | 错误原因                             |
|--------|--------------------------------------|
| -1     | 请求错误                             |
| 0      | 请求成功                             |
| 3003   | Ledger请求参数错误，一般是节点内错误 |
| 3006   | 请求Block data失败                   |
| 3008   | Ledger请求存储失败                   |
| 3010   | Ledger层出现未知错误                 |

## 4. 节点上链回执错误码

| 错误码 | 错误代号                   | 错误原因                                                                     |
|--------|----------------------------|------------------------------------------------------------------------------|
| 0      | None                       | 成功                                                                         |
| 1      | Unknown                    | 未知错误                                                                     |
| 10     | BadInstruction             | 错误的构造，一般出现在执行code使用了未知的opcode                             |
| 11     | BadJumpDestination         | 栈错误，一般出现在执行时跳到错误的栈                                         |
| 12     | OutOfGas                   | 执行时超过gas使用，在部署合约时，可能将会报错Exceptional Failed Code Deposit |
| 13     | OutOfStack                 | 执行时超过栈限制                                                             |
| 14     | StackUnderflow             | 缺栈，一般出现在执行时缺少参数、缺少输入的情况                               |
| 15     | PrecompiledError           | 预编译合约执行失败                                                           |
| 16     | RevertInstruction          | 交易回滚                                                                     |
| 17     | ContractAddressAlreadyUsed | 部署的合约地址已经存在                                                       |
| 18     | PermissionDenied           | 调用合约、部署合约权限不够                                                   |
| 19     | CallAddressError           | 请求合约地址不存在                                                           |
| 21     | ContractFrozen             | 合约已经被冻结                                                               |
| 22     | AccountFrozen              | 帐号已经被冻结                                                               |
| 23     | AccountAbolished           | 帐号已经被废止                                                               |
| 24     | ContractAbolished          | 合约已经被废止                                                               |
| 32     | WASMValidationFailure      | WASM二进制验证失败，一般出现在使用了错误的密码学方法、WASM使用了浮点数       |
| 33     | WASMArgumentOutOfRange     | WASM参数过多                                                                 |
| 34     | WASMUnreachableInstruction | WASM执行过程中出现错误                                                       |
| 35     | WASMTrap                   | WASM执行失败                                                                 |

## 5. 预编译合约错误码

**响应码**与**错误信息**对应表

| 错误码 | 错误代号                                                                 | 错误信息                                                 |
|--------|--------------------------------------------------------------------------|----------------------------------------------------------|
| 0      | Success                                                                  | 成功                                                     |
| -53007 | File Count Error                                                         | BFS在执行list接口时调用count接口出现错误，一般是存储错误 |
| -53006 | Invalid file type                                                        | BFS错误的文件类型                                        |
| -53005 | Invalid path                                                             | BFS错误的路径                                            |
| -53003 | Make directory failed                                                    | BFS创建路径目录失败                                      |
| -53002 | File already existed                                                     | BFS文件已经存在                                          |
| -53001 | File not exist                                                           | BFS请求的文件不存在                                      |
| -51900 | Account already exist                                                    | 新建的帐号已经存在                                       |
| -51800 | verify ring sig failed                                                   | 环签名验证失败                                           |
| -51700 | verify group sig failed                                                  | 验证群签名失败                                           |
| -51508 | remove key not exist                                                     | CRUD删除不存在的key                                      |
| -51507 | Key not exist in table, use insert method                                | CRUD更新不存在的key                                      |
| -51506 | Don't insert the key already existed                                     | CRUD插入已经存在的key                                    |
| -51202 | The version string or address is error                                   | BFS插入合约地址和合约版本有错误                          |
| -51104 | Add sealer should exist in observer                                      | 在添加Sealer节点之前必须将节点加入观察节点               |
| -51103 | The node is not exist                                                    | 操作的节点不存在                                         |
| -51102 | The weight is invalid                                                    | 设置节点错误的权重                                       |
| -51101 | The last sealer cannot be removed                                        | 最后一个sealer节点不应该被删除                           |
| -51100 | Invalid node ID                                                          | 错误的node ID                                            |
| -51004 | Auth map decode error                                                    | 权限的map解析错误，一般是存储错误                        |
| -51003 | Error auth type input                                                    | 使用错误的权限ACL类型                                    |
| -51002 | The contract method auth type not set, please set method auth type first | 权限ACL未设置                                            |
| -51001 | The contract method auth not exist                                       | 权限表未设置                                             |
| -50105 | Open table error                                                         | 读存储表错误                                             |
| -50104 | Create table error                                                       | 创建存储表错误                                           |
| -50103 | Table set row error                                                      | 设置存储表行数错误                                       |
| -50102 | Invalid address format                                                   | 错误的地址格式                                           |
| -50101 | Undefined function                                                       | 调用了不存在的预编译合约方法                             |
| -50008 | Invalid table name or field name                                         | 表名或者字段名错误                                       |
| -50007 | The table contains duplicated field                                      | 表中存在重复的字段                                       |
| -50006 | The field value exceeds the limit                                        | 字段值超过16MB长度限制                                   |
| -50005 | The value exceeds the limit, key max length is 255                       | 主键值超过255长度限制                                    |
| -50004 | The length of all the fields name exceeds the limit 1024                 | 表的总字段长度超过1024限制                               |
| -50003 | The table field name exceeds the limit 64                                | 单个字段长度超过64限制                                   |
| -50002 | The table name length exceeds the limit 50                               | 表名超过50长度限制                                       |
| -50001 | The table already exist                                                  | 表名不存在                                               |
| -50000 | No authrized                                                             | 请求没有权限                                             |
