# 控制台

标签：``Python SDK`` ``PythonSDK控制台``

----

[Python SDK](https://github.com/FISCO-BCOS/python-sdk)通过`console.py`实现了一个简单的控制台，支持合约操作、账户管理操作等。

```eval_rst
.. note::

    - **Python SDK当前为候选版本，可供开发测试使用，企业级应用可用** `Java SDK <../java_sdk/index.html>`_
    - 安装Java版本控制台可参考 `这里 <../../installation.html>`_
    - windows环境下执行console.py请使用 ``.\console.py`` 或者 ``python console.py``

```


## 常用命令

### deploy

部署合约：
```
./console.py deploy [contract_name] [save]
```
参数包括：
- contract_name: 合约名，需要先放到`contracts`目录
- save：若设置了save参数，表明会将合约地址写入历史记录文件

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
    "input": "0x6080604052...省略若干行...c6f2c20576f726c642100000000000000000000000000",
    "logs": [],
    "logsBloom": "0x000...省略若干行...0000",
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

调用合约接口，并解析返回结果：
```
./console.py  call [contract_name] [contract_address] [function] [args]
```

参数包括：
- contract_name：合约名
- contract_address：调用的合约地址
- function：调用的合约接口
- args：调用参数

```bash
# 合约地址：0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
# 调用接口：get
$./console.py  call HelloWorld 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce get

INFO >> user input : ['call', 'HelloWorld', '0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce', 'get']
INFO >> call HelloWorld , address: 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce, func: get, args:[]
INFO >> call result: ('Hello, World!',)
```

### sendtx

发送交易调用指定合约的接口，交易结果会写入区块和状态：
```
./console.py sendtx [contract_name] [contract_address] [function] [args]
```

参数包括：
- contract_name：合约名
- contract_address：合约地址
- function：函数接口
- args：参数列表

```bash
# 合约名：HelloWorld
# 合约地址：0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
# 调用接口：set
# 参数："Hello, FISCO"
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

创建新账户，并将结果以加密的形式把保存与`bin/accounts/${accoutname}.keystore`文件中，**如同目录下已经有同名帐户文件，旧文件会复制一个备份**：

```
./console.py newaccount [account_name] [account_password]
```

参数包括：

- account_name：账户名
- account_password：加密keystore文件的口令

```eval_rst
.. note::

    - 采用创建帐号的命令创建帐号后，若需作为默认帐号使用，注意修改client_config.py的 ``account_keyfile`` 和 ``account_password`` 配置项
    - 账户名不可超过240个字符
    - 若 ``account_password`` 中包含特殊字符，请在 ``account_password`` 周围加上单引号，否则无法解析
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

根据账户名和账户`keystore`文件口令，输出账户公私钥信息：

```
./console.py showaccount [account_name] [account_password]
```

参数包括：

- name：账户名称
- password: 账户`keystore`文件口令

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

输出控制台使用方法：

```bash
 $ ./console.py usage

INFO >> user input : ['usage']

FISCO BCOS 2.0 @python-SDK Usage:
newaccount [name] [password] [save]
        创建一个新帐户，参数为帐户名(如alice,bob)和密码
        结果加密保存在配置文件指定的帐户目录 *如同目录下已经有同名帐户文件，旧文件会复制一个备份
        如输入了"save"参数在最后，则不做询问直接备份和写入
        create a new account ,save to :[bin/accounts] (default) ,
        the path in client_config.py:[account_keyfile_path]
        if account file has exist ,then old file will save to a backup
        if "save" arg follows,then backup file and write new without ask
        the account len should be limitted to 240

    ... 省略若干行...
    [getTransactionByBlockHashAndIndex] [blockHash] [transactionIndex]
    [getTransactionByBlockNumberAndIndex] [blockNumber] [transactionIndex]
    [getSystemConfigByKey] [tx_count_limit/tx_gas_limit]
```
### list

输出Python SDK支持的所有接口：

```bash
$ ./console.py list

INFO >> user input : ['list']

 >> RPC commands
    [getNodeVersion]
    [getBlockNumber]
    ... 省略若干行...
    [getTransactionByBlockHashAndIndex] [blockHash] [transactionIndex]
    [getTransactionByBlockNumberAndIndex] [blockNumber] [transactionIndex]
    [getSystemConfigByKey] [tx_count_limit/tx_gas_limit]
```

## CNS

Python SDK控制台提供了CNS命令，主要包括注册CNS、查询CNS信息，CNS设计使用方法请参考[这里](../../design/features/cns_contract_name_service.md)。

### registerCNS

将(合约地址, 合约版本)到合约名的映射注册到CNS系统表中：
```bash
./console.py registerCNS [contract_name] [contract_address] [contract_version]
```
参数包括:
- contract_name: 合约名
- contract_address: 合约地址
- contract_version: 合约版本

```bash
# 将合约地址0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce和合约版本v_1.0映射到合约名HelloWorld
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

根据合约名查询CNS信息：
```
./console.py queryCNSByName [contract_name]
```
参数包括：
- contract_name：合约名

```bash
查询HelloWorld合约名对应的CNS信息
$ ./console.py queryCNSByName HelloWorld

INFO >> user input : ['queryCNSByName', 'HelloWorld']

     >> ('[{"abi":"\\"\\"","address":"0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce","name":"HelloWorld","version":"v_1.0"}]\n',)
CNS ITEM 0 >>
        ContractName: HelloWorld
        ContractVersion: v_1.0
        ContractAddress: 0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce

```

### queryCNSByNameAndVersion

根据合约名和合约版本查询CNS信息：
```
./console.py queryCNSByNameAndVersion [contract_name] [contract_version]
```
参数包括：
- contract_name: 合约名
- contract_version: 合约版本

```bash
# 查询合约名为HelloWorld，版本为v_1.0的CNS信息
$ ./console.py queryCNSByNameAndVersion HelloWorld v_1.0

INFO >> user input : ['queryCNSByNameAndVersion', 'HelloWorld', 'v_1.0']

INFO >> CNS version (strip space): v_1.0
     >> ('[{"abi":"\\"\\"","address":"0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce","name":"HelloWorld","version":"v_1.0"}]\n',)
CNS ITEM 0 >>
        ContractName: HelloWorld
        ContractVersion: v_1.0
        ContractAddress: 0x2d1c577e41809453C50e7E5C3F57D06f3CDD90Ce
```

## 节点管理

Python SDK提供了节点管理命令，包括添加共识节点、添加观察者节点、将节点从群组中删除，节点管理的详细资料可参考[这里](../../manual/node_management.md)。

### removeNode

将指定该节点从群组中删除：
```
./console.py removeNode [nodeId]
```
参数包括：
- nodeId：被删除节点的nodeID

```bash
# 设节点位于~/fisco/nodes目录，查询node1的nodeID
$ cat ~/fisco/nodes/127.0.0.1/node1/conf/node.nodeid
12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4
# 将节点1从群组中删除node1
./console.py removeNode 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

INFO >> user input : ['removeNode', '12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4']

INFO >> removeNode
     >> status: 0x0
     >> transactionHash: 0x68cde78d76f490b35431905d2336d9811966a370da8b4041db092feb09981f28
     >> gasUsed: 0x7698
     >> removeNode succ, output: 1
```

### addSealer

将指定节点加入共识节点列表：
```
./console.py addSealer [nodeId]
```
参数包括：
- nodeId: 加入的共识节点nodeID，获取节点nodeID可参考[这里](../../manual/configuration.html#id11)

```bash
# 设节点位于~/fisco/nodes目录，查询node1的nodeID
$ cat ~/fisco/nodes/127.0.0.1/node1/conf/node.nodeid
12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

# 将节点node1加入为共识节点
$./console.py addSealer 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

INFO >> user input : ['addSealer', '12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4']

INFO >> addSealer
     >> status: 0x0
     >> transactionHash: 0xfddfa618419880e37f82c8cd385994fcb1ee1d4c5b4b506ae0d67f223c8b723d
     >> gasUsed: 0x7698
     >> addSealer succ, output: 1
```


### addObserver
将指定节点加入为观察者节点：
```
./console.py addObserver [nodeId]
```
参数包括：
- nodeId: 加入的观察者节点nodeID，获取节点nodeID可参考[这里](../../manual/configuration.html#id11)

```bash
# 设节点位于~/fisco/nodes目录，查询node1的nodeID
$ cat ~/fisco/nodes/127.0.0.1/node1/conf/node.nodeid
12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

# 将节点node1加入为观察节点
$ ./console.py addObserver 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4

INFO >> user input : ['addObserver', '12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4']

INFO >> addObserver
     >> status: 0x0
     >> transactionHash: 0xb126900787205a5f913e6643058359a07ace1cc550190a5a9478ae4f49cfc1eb
     >> gasUsed: 0x7658
     >> addObserver succ, output: 1
```

### 系统配置
Python SDK提供了系统配置修改命令，FISCO BCOS目前支持的系统配置参考[这里](../../manual/configuration.html#id21)。
```
./console.py setSystemConfigByKey [key(tx_count_limit/tx_gas_limit)] [value]
```
参数包括：
- key：配置关键字，目前主要包括`tx_count_limit`和`tx_gas_limit`
- value: 配置关键字的值

```bash
# 将区块内最大交易数目调整为500
$ ./console.py setSystemConfigByKey tx_count_limit 500

INFO >> user input : ['setSystemConfigByKey', 'tx_count_limit', '500']

INFO >> setSystemConfigByKey
     >> status: 0x0
     >> transactionHash: 0xded8abc0858f8a7be5961ae38958928c98f75ee78dbe8197a47c382cb2549de1
     >> gasUsed: 0x5b58
     >> setSystemConfigByKey succ, output: 1

# 将交易gas限制调整为400000000
$ ./console.py setSystemConfigByKey tx_gas_limit 400000000

INFO >> user input : ['setSystemConfigByKey', 'tx_gas_limit', '400000000']

INFO >> setSystemConfigByKey
     >> status: 0x0
     >> transactionHash: 0x4b78868ec183c432e07971f578f5ab8222a9effda39dfa8e87643410cb2cea05
     >> gasUsed: 0x5c58
     >> setSystemConfigByKey succ, output: 1
```

## 权限管理

Python SDK提供了权限管理功能，包括授权、撤销权限和列出权限列表等，权限控制的详细内容可参考[这里](../../manual/distributed_storage.md)。

### grantPermissionManager
将控制权限的功能授权给指定账户：
```
./console.py grantPermissionManager [account_adddress]
```
参数包括：
- account_adddress：被授予权限的账户地址，账户可通过`newaccount`命令生成

```bash
# 获取默认账户地址
./console.py showaccount pyaccount "123456"
INFO >> user input : ['showaccount', 'pyaccount', '123456']
show account : pyaccount, keyfile:bin/accounts/pyaccount.keystore ,password 123456
decrypt use time : 1.450 s
address:         0x95198B93705e394a916579e048c8A32DdFB900f7
privkey:         0x48140af2cf0879631d558833aa48b7bb4b37091dbfe902a573886538041b69c0
pubkey :         0x142d340c0f4df64bf56bbc0a3931e5228c7836add09cf8ff3cefeb3d7e610deb458ec871a9da86bae1ffc029f5aba41e725786ecb7f93ad2670303bf2db27b8a
account store in file: [bin/accounts/pyaccount.keystore]
**** please remember your password !!! *****

# 为账户0x95198B93705e394a916579e048c8A32DdFB900f7添加权限管理权限
$ ./console.py grantPermissionManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantPermissionManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantPermissionManager
     >> status: 0x0
     >> transactionHash: 0xdac11796dcfb663842a13333976626d844527605edb5bf9daadcfa28236bb5c8
     >> gasUsed: 0x6698
     >> grantPermissionManager succ, output: 1

```
### listPermissionManager

列出有权限管理功能的账户信息：

```bash
# 列出所有权限管理账户信息
$ ./console.py listPermissionManager
INFO >> user input : ['listPermissionManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 9
```

### grantUserTableManager
将给定用户表权限授予指定用户：
```
./console.py grantUserTableManager [tableName] [account_adddress]
```

```eval_rst
.. note::
    给用户授权用户表权限前，请确保用户表存在，可用 ``createTable`` 命令创建用户表
```
参数包括：
- tableName： 用户表名
- account_adddress：被授权用户账户地址

```bash
# 创建用户表t_test
$./console.py createTable t_test "key" "value1, value2, value3"
INFO >> user input : ['createTable', 't_test', 'key', 'value1, value2, value3']
INFO >> createTable
     >> status: 0x0
     >> transactionHash: 0xfbc10c0d9e4652f59655903e5ba772bb7f127e8e9de12be250d487f0ff9c5268
     >> gasUsed: 0x6098
     >> createTable succ, output: 0

# 为账户0x95198B93705e394a916579e048c8A32DdFB900f7对用户表t_test的管理功能
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
列出对指定用户表有管理权限的账户信息：
```
./console.py listUserTableManager [tableName]
```
参数包括：
- tableName: 用户表

```bash
# 查看用户表t_test的管理信息
$./console.py listUserTableManager t_test
INFO >> user input : ['listUserTableManager', 't_test']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 11
```

### grantNodeManager
将节点管理权限授予指定账户：
```
./console.py grantNodeManager [account_adddress]
```
参数包括：
- account_adddress：被授权用户账户地址

```bash
# 为账户0x95198B93705e394a916579e048c8A32DdFB900f7添加节点管理功能
$ ./console.py grantNodeManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantNodeManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantNodeManager
     >> status: 0x0
     >> transactionHash: 0x3a8839bfdfefcd3fff2678f91f231d44d8d442e40fc7f3af726daec624ba80c8
     >> gasUsed: 0x65d8
     >> grantNodeManager succ, output: 1

```

### listNodeManager

列出有节点管理功能的账户信息：

```bash
$ ./console.py listNodeManager
INFO >> user input : ['listNodeManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 12
```

### grantCNSManager
将CNS管理权限授予指定账户：
```
./console.py grantCNSManager [account_adddress]
```
参数包括：
- account_adddress：被授权用户账户地址

```bash
# 为账户0x95198B93705e394a916579e048c8A32DdFB900f7添加CNS管理权限
$ ./console.py grantCNSManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantCNSManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantCNSManager
     >> status: 0x0
     >> transactionHash: 0x4a112be9f582fb1ae98ae9d6a84706930f4ab3523b45722cc4bf08341397dd1e
     >> gasUsed: 0x6458
     >> grantCNSManager succ, output: 1
```

### listCNSManager

列出有CNS管理权限的账户信息

```bash
$ ./console.py listCNSManager

INFO >> user input : ['listCNSManager']

----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 13
```

### grantSysConfigManager

将系统配置修改权限授予指定账户：
```
./console.py grantSysConfigManager [account_adddress]
```
参数包括：
- account_adddress：被授权用户账户地址

```bash
# 为账户0x95198B93705e394a916579e048c8A32DdFB900f7添加系统配置权限
$ ./console.py grantSysConfigManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantSysConfigManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantSysConfigManager
     >> status: 0x0
     >> transactionHash: 0xf6ec040686496256a8c01233d1339ee147551f6a2dfcbd7bd6d7647f240f1411
     >> gasUsed: 0x6518
     >> grantSysConfigManager succ, output: 1
```

### listSysConfigManager

列出有系统配置修改权限的账户信息：

```bash
$ ./console.py listSysConfigManager
INFO >> user input : ['listSysConfigManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 14
```

### grantDeployAndCreateManager

将部署和创建表的权限授予指定账户：
```
./console.py grantDeployAndCreateManager [account_adddress]
```
参数包括：
- account_adddress：被授权用户账户地址

```bash
# 为账户0x95198B93705e394a916579e048c8A32DdFB900f7添加创建表和部署合约权限
$./console.py grantDeployAndCreateManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['grantDeployAndCreateManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> grantDeployAndCreateManager
     >> status: 0x0
     >> transactionHash: 0xf60452a12d5346fa641bca6bee662c261fa0c67ef90aca3944cdb29a5803c625
     >> gasUsed: 0x6518
     >> grantDeployAndCreateManager succ, output: 1
```

### listDeployAndCreateManager

列出有创建合约和用户表的账户信息：
```bash
$ ./console.py listDeployAndCreateManager
INFO >> user input : ['listDeployAndCreateManager']
----->> ITEM 0
     = address: 0x95198B93705e394a916579e048c8A32DdFB900f7
     = enable_num: 15
```

### revokeUserTableManager

撤销指定用户对指定用户表的写入权限：

```
./console.py revokeUserTableManager [tableName] [account_adddress]
```
参数包括：
- tableName：禁止指定用户写入的表名
- account_adddress：被撤销权限的账户地址

```bash
# 撤销账户0x95198B93705e394a916579e048c8A32DdFB900f7对用户表t_test的控制权限
$ ./console.py revokeUserTableManager t_test 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeUserTableManager', 't_test', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeUserTableManager
     >> status: 0x0
     >> transactionHash: 0xc7ffbd0f79bfe06f43c603afde5997f9127a9fe499338362e64c653a593ded36
     >> gasUsed: 0x6398
     >> revokeUserTableManager succ, output: 1
```

### revokeDeployAndCreateManager
撤销指定账户创建表、部署合约的权限：
```
./console.py revokeDeployAndCreateManager [account_adddress]
```
参数包括：
- account_adddress：被撤销权限的账户地址

```bash
# 撤销账户0x95198B93705e394a916579e048c8A32DdFB900f7部署和创建表权限
$ ./console.py revokeDeployAndCreateManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeDeployAndCreateManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeDeployAndCreateManager
     >> status: 0x0
     >> transactionHash: 0xeac82f3464093f0659eb6412c39599d51b64082401ac43df9d7670cf17882f78
     >> gasUsed: 0x6518
     >> revokeDeployAndCreateManager succ, output: 1
```

### revokeNodeManager
撤销指定账户的节点管理权限：
```
./console.py revokeNodeManager [account_adddress]
```
参数包括：
- account_adddress：被撤销权限的账户地址

```bash
# 撤销账户0x95198B93705e394a916579e048c8A32DdFB900f7节点管理权限
$ ./console.py revokeNodeManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeNodeManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeNodeManager
     >> status: 0x0
     >> transactionHash: 0xc9f3799dc81a146f562fe10b493d14920676a8e49a6de94e7b4b998844198342
     >> gasUsed: 0x65d8
     >> revokeNodeManager succ, output: 1
```

### revokeCNSManager
撤销指定账户CNS管理权限：
```
./console.py revokeCNSManager [account_adddress]
```
参数包括：
- account_adddress：被撤销权限的账户地址

```bash
# 撤销账户0x95198B93705e394a916579e048c8A32DdFB900f7 CNS管理权限
$ ./console.py revokeCNSManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeCNSManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeCNSManager
     >> status: 0x0
     >> transactionHash: 0xa5aa6d115875156512af8c9974e353336e00bc3b9c2f2c2e21749d728e45abb4
     >> gasUsed: 0x6458
     >> revokeCNSManager succ, output: 1
```

### revokeSysConfigManager
撤销指定账户修改系统配置权限：
```
./console.py revokeSysConfigManager [account_adddress]
```
参数包括：
- account_adddress：被撤销权限的账户地址

```bash
# 撤销账户0x95198B93705e394a916579e048c8A32DdFB900f7系统表管理权限
$ ./console.py revokeSysConfigManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokeSysConfigManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokeSysConfigManager
     >> status: 0x0
     >> transactionHash: 0xfaffc25a4b111cfdaddca323d8b125c553c5e8f83b85fae1de21a6bc3bef792a
     >> gasUsed: 0x6518
     >> revokeSysConfigManager succ, output: 1
```

### revokePermissionManager
撤销指定账户管理权限的权限：
```
./console.py revokePermissionManager [account_adddress]
```
参数包括：
- account_adddress：被撤销权限的账户地址

```bash
# 撤销账户0x95198B93705e394a916579e048c8A32DdFB900f7权限管理权限
$ ./console.py revokePermissionManager 0x95198B93705e394a916579e048c8A32DdFB900f7
INFO >> user input : ['revokePermissionManager', '0x95198B93705e394a916579e048c8A32DdFB900f7']
INFO >> revokePermissionManager
     >> status: 0x0
     >> transactionHash: 0xa9398d4de7a3e86238a48bdbf5e053c61bc57ccd1aa57ebaa3c070bc47ea0f98
     >> gasUsed: 0x6698
     >> revokePermissionManager succ, output: 1
```

## RPC

可以通过Python SDK查询节点信息，目前Python SDK支持的查询命令如下：
### getNodeVersion

获取节点版本信息：

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

获取节点最新块高：

```bash
$ ./console.py getBlockNumber
INFO >> user input : ['getBlockNumber']
INFO >> getBlockNumber
     >> 21
```

### getPbftView

获取节点共识视图：
```bash
$ ./console.py getPbftView

INFO >> user input : ['getPbftView']

INFO >> getPbftView
     >> 0x34e
```
### getSealerList

获取当前共识节点列表：
```bash
$ ./console.py getSealerList

INFO >> user input : ['getSealerList']

INFO >> getSealerList
     >> 3ad90ae5a10b8d88c9936492a37f564884e82b176e91f5e2e2f75a347be87212aac148ee7fa2060be8a790eaa3d44a299f94ba3d97adfa45526346902d64e0af
     >> 6bd07f2f8180ac9d56b40ff9977ba528a4f65e83d4ca95a0537e12f6638f78848e0765cbee0cb2b5f581d7eb5027d189f8691bfa92186bbf51deefd467339b6f
     >> b8783cfe3c073a532e9cbc47978d45a187da179d2fef4a85990c3b286d69d1afcd061de1b8cba07a59819d94f021db1e7707304908024f80e5830298e3829b82
```
### getObserverList

获取观察者节点列表：
```bash
$ ./console.py getObserverList
INFO >> user input : ['getObserverList']
INFO >> getObserverList
     >> 12ce3fc76bc3253ba9be25dc3adb8b75df392583b8f2813f4c623cff258980c8c2c73f384ce6f37dca7261ea0a9fb24ff59fa3c58ee8f278be009827114500e4
```
### getConsensusStatus

获取节点共识状态信息：
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
    ... 省略若干行 ...
}
```
### getSyncStatus

获取节点同步状态信息：
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

获取节点连接列表：
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

获取群组内节点连接信息：
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

获取区块链所有组网节点列表：
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

获取群组列表：
```bash
$ ./console.py getGroupList
INFO >> user input : ['getGroupList']
INFO >> getGroupList
     >> 1
```

### getPendingTransactions

获取交易池内还未上链的交易信息：
```bash
$ ./console.py getPendingTransactions
INFO >> user input : ['getPendingTransactions']
INFO >> getPendingTransactions
     >> Empty Set
```

### getPendingTxSize

获取交易池内还未上链的交易数目：
```bash
$ ./console.py getPendingTxSize
INFO >> user input : ['getPendingTxSize']
INFO >> getPendingTxSize
     >> 0x0
```

### getTotalTransactionCount

获取已经上链的交易数目：
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

根据块高查询区块：
```bash
$ ./console.py getBlockByNumber [block_number] [True/False]
```
参数包括：
- block_number：区块高度
- True/False: 可选，True表明返回的区块信息内包含具体的交易信息；False表明返回的区块内仅包含交易哈希


```bash
$ ./console.py getBlockByNumber 0

INFO >> user input : ['getBlockByNumber', '0']

INFO >> getBlockByNumber
     >> {
    "dbHash": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "extraData": [
        "0x312d62383738336366653363303733613533326539636263343739373864
        ... 省略若干行...
        7652d313030302d333030303030303030"
    ],
    "gasLimit": "0x0",
    "gasUsed": "0x0",
    "hash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
    "logsBloom": "0x00000000... 省略若干行...0000000000000000000000",
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

根据块高查询区块哈希：

```bash
$ ./console.py getBlockHashByNumber 0
INFO >> user input : ['getBlockHashByNumber', '0']
INFO >> getBlockHashByNumber
     >> 0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04
```
### getBlockByHash

根据区块哈希获取区块信息：

```
$ ./console.py getBlockByHash [block_hash] [True/False]
```
参数包括：
- block_hash：区块哈希
- True/False: 可选，True表明返回的区块内包含交易具体信息；False表明返回的区块仅包含交易哈希

```bash
$ ./console.py getBlockByHash 0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04
INFO >> user input : ['getBlockByHash', '0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04']
INFO >> getBlockByHash
     >> {
    "extraData": [
        "0x312d623...省略若干...3030303030"
    ],
    "gasLimit": "0x0",
    "gasUsed": "0x0",
    "hash": "0xff1404962c6c063a98cc9e6a20b408e6a612052dc4267836bb1dc378acc6ce04",
    "logsBloom": "0x0000...省略若干...000000",
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

获取指定合约的二进制编码：

```bash
$ ./console.py getCode 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
INFO >> user input : ['getCode', '0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce']
INFO >> getCode
     >> 0x60806040526...省略若干...a40029
```
### getTransactionByHash

根据交易哈希获取交易信息：
```
./console.py getTransactionByHash [hash] [contract_name]
```

参数包括:
- hash: 交易哈希
- contract_name：可选，该交易相关的合约名，若输入该参数，会解析返回交易的具体内容


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
    "input": "0x60806...省略若干...ddd81c4a40029",
    "nonce": "0x2b2350c8",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionIndex": "0x0",
    "value": "0x0"
}
```
### getTransactionReceipt

根据交易哈希获取交易回执信息：
```bash
./console.py getTransactionReceipt [hash] [contract_name]
```
参数包括：
- hash：交易哈希
- contract_name：可选，该交易相关的合约名，若输入该参数，会解析交易和回执的具体内容

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
    "input": "0x608060405234...省略若干...d9acf16e2fc2d570d491ddd81c4a40029",
    "logs": [],
    "logsBloom": "0x00000...省略若干...00000000000",
    "output": "0x",
    "status": "0x0",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionHash": "0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91",
    "transactionIndex": "0x0"
}
```
### getTransactionByBlockHashAndIndex

根据区块哈希和交易索引查询交易信息：

```bash
./console.py getTransactionByBlockHashAndIndex [blockHash] [transactionIndex] [contract_name]
```
参数包括：
- blockHash: 交易所在的区块哈希
- transactionIndex：交易索引
- contract_name：可选，该交易相关的合约名，若输入该参数，会解析返回交易的具体内容

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
    "input": "0x6080...省略若干...4a40029",
    "nonce": "0x2b2350c8",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionIndex": "0x0",
    "value": "0x0"
}
```

### getTransactionByBlockNumberAndIndex

根据块高和交易索引查询交易信息：
```
$ ./console.py getTransactionByBlockNumberAndIndex [blockNumber] [transactionIndex] [contract_name]
```
参数包括：
- blockNumber：交易所在的区块块高
- transactionIndex：交易索引
- contract_name：可选，该交易相关的合约名，若输入该参数，会解析返回交易的具体内容

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
    "input": "0x608060...省略若干...a40029",
    "nonce": "0x2b2350c8",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionIndex": "0x0",
    "value": "0x0"
}
```
### getSystemConfigByKey

获取系统配置信息：

```bash
# 获取区块可打包最大交易数目
$ ./console.py getSystemConfigByKey tx_count_limit
INFO >> user input : ['getSystemConfigByKey', 'tx_count_limit']
INFO >> getSystemConfigByKey tx_count_limit
     >> 500
# 获取系统gas限制
$ ./console.py getSystemConfigByKey  tx_gas_limit
INFO >> user input : ['getSystemConfigByKey', 'tx_gas_limit']
INFO >> getSystemConfigByKey tx_gas_limit
     >> 400000000
```
