# 控制台

[Python SDK](https://github.com/FISCO-BCOS/python-sdk)通过`console.py`实现了一个简单的控制台，支持合约操作、账户管理操作等。

```eval_rst
.. note::

    - Python SDK提供的控制台目前正处于个人开发者体验阶段，提供的功能较少，若有完善的控制台体验需求，请使用 `Java版本控制台 <../../manual/console.html>`_ 
    - 安装Java版本控制台可参考 `这里 <../../installation.html>`_
```


## 常用命令链接

### 合约相关命令

- **部署合约**：[deploy](./console.html#deploy)
- **调用合约**：[call](./console.html#call)
- **发送交易**：[sendtx](./console.html#sendtx)
- **交易解析**: [txinput](./console.html#txinput)

### 账户管理相关命令

- **新建账户**：[newaccount](./console.html#newaccount)
- **查看账户公私钥信息**：[showaccount](./console.html#showaccount)

### 其他命令

- **显示控制台用法**：[usage](./console.html#usage)
- **获取控制台API用法**: [list](./console.html#list)



## 控制台命令

### deploy

部署合约，参数：

- contract_binary_file：保存合约`.bin`文件路径
- save：若设置了save参数，表明会将合约地址写入历史记录文件

```eval_rst
.. note::

    参考 `教程的合约编译 <../../tutorial/sdk_application.html#id7>`_ 将 ``.sol`` 后缀的合约代码转换为 ``.bin`` 文件

```

```bash
# 合约bin文件路径：contracts/HelloWorld.bin
$ ./console.py deploy contracts/HelloWorld.bin save 

>> user input : ['deploy', 'contracts/HelloWorld.bin', 'save']

deploy result  for [contracts/HelloWorld.bin] is:
 {
    "blockHash": "0xa9238a4138b5cac925d2d7b338c44acca5c1ae4d83df2243159cef4ff89c8c66",
    "blockNumber": "0x8",
    "contractAddress": "0x42883e01ac97a3a5ef8a70c290abe0f67913964e",
    "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
    "gasUsed": "0x44ab3",
    "input": "0x60806040523...省略若干...",
    "logs": [],
    "logsBloom": "0x0000000000...省略若干...",
    "output": "0x",
    "status": "0x0",
    "to": "0x0000000000000000000000000000000000000000",
    "transactionHash": "0x3e9b8dc3a3e07da5a971923cd74fd264fc76f80be34eac05f362c754426fefd2",
    "transactionIndex": "0x0"
}
on block : 8,address: 0x42883e01ac97a3a5ef8a70c290abe0f67913964e 
address save to file:  bin/contract.ini
```

### call

调用合约接口，并解析返回结果，参数：

- contractname：合约名
- address：调用的合约地址
- func：调用的合约接口
- args：调用参数

```bash
# 合约地址：0x42883e01ac97a3a5ef8a70c290abe0f67913964e
# 调用接口：get
$./console.py  call HelloWorld 0x42883e01ac97a3a5ef8a70c290abe0f67913964e get

>> user input : ['call', 'HelloWorld', '0x42883e01ac97a3a5ef8a70c290abe0f67913964e', 'get']

param formatted by abi: []
call HelloWorld , address: 0x42883e01ac97a3a5ef8a70c290abe0f67913964e, func: get, args:[]
call result:  ('Hello, World!',)
```

### sendtx

发送交易调用指定合约的接口，交易如成功，结果会写入区块和状态，参数：
- contractname：合约名
- address：合约地址
- func：函数接口
- args：参数列表

```bash
# 合约名：HelloWorld
# 合约地址：0x42883e01ac97a3a5ef8a70c290abe0f67913964e
# 调用接口：set
# 参数："Hello, FISCO"
$ ./console.py sendtx HelloWorld 0x42883e01ac97a3a5ef8a70c290abe0f67913964e "set" "Hello, FISCO"

>> user input : ['sendtx', 'HelloWorld', '0x42883e01ac97a3a5ef8a70c290abe0f67913964e', 'set', 'Hello, FISCO']

param formatted by abi: ['Hello, FISCO']
sendtx HelloWorld , address: 0x42883e01ac97a3a5ef8a70c290abe0f67913964e, func: set, args:['Hello, FISCO']

sendtx receipt:  {
    "blockHash": "0x44d00ec42fe8abe12f324ceea786e065c095ecef1116fdc3b7ce4b38618de5d6",
    "blockNumber": "0x9",
    "contractAddress": "0x0000000000000000000000000000000000000000",
    "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
    "gasUsed": "0x82ec",
    "input": "0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000c48656c6c6f2c20464953434f0000000000000000000000000000000000000000",
    "logs": [],
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "output": "0x",
    "status": "0x0",
    "to": "0x42883e01ac97a3a5ef8a70c290abe0f67913964e",
    "transactionHash": "0x7e7f56c656a743b6b052fff8d61901a9ea752f758084fc3ef2fdc9a854f597d4",
    "transactionIndex": "0x0"
}

>>  receipt logs : 
>> transaction hash :  0x7e7f56c656a743b6b052fff8d61901a9ea752f758084fc3ef2fdc9a854f597d4
tx input data detail:
 {'name': 'set', 'args': ('Hello, FISCO',), 'signature': 'set(string)'}
receipt output : ()
```

### txinput

复制一段来自transaction的inputdata(十六进制字符串)，指定合约名，可自动解析（合约的abi文件应存在指定目录下），参数：

- contractname：合约名
- inputdata：合约输入参数

```bash
$ ./console.py txinput HelloWorld "0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000c48656c6c6f2c20464953434f0000000000000000000000000000000000000000"
>> user input : ['txinput', 'HelloWorld', '0x4ed3885e0000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000c48656c6c6f2c20464953434f0000000000000000000000000000000000000000']

abifile :  contracts/HelloWorld.abi
parse result: {'name': 'set', 'args': ('Hello, FISCO',), 'signature': 'set(string)'}
```

### newaccount

创建新账户，并将结果以加密的形式把保存与`bin/accounts/${accoutname}.keystore`文件中，**如同目录下已经有同名帐户文件，旧文件会复制一个备份**，参数：

- accountname：账户名
- password：加密keystore文件的口令

```eval_rst
.. note::
    
    采用创建帐号的命令创建帐号后，若需作为默认帐号使用，注意修改client_config.py的 ``account_keyfile`` 和 ``account_password`` 配置项
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

根据账户名和账户`keystore`文件口令，输出账户公私钥信息，参数包括：

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

>> user input : ['usage']
usage
    使用说明,输入./console.py [指令 参数列表]
    Usage of console (FISCO BCOS 2.0 lite client @python):
    ./console.py [cmd args]
    
1): showaccount [name] [password]
    指定帐户名字(不带后缀)和密码，打开配置文件里默认账户文件路径下的[name].keystore文件，打印公私钥和地址
    
    ...省略若干行 ...

10): checkaddr [address]
    将普通地址转为自校验地址,自校验地址使用时不容易出错
    change address to checksum address according EIP55:
    to_checksum_address: 0xf2c07c98a6829ae61f3cb40c69f6b2f035dd63fc -> 0xF2c07c98a6829aE61F3cB40c69f6b2f035dD63FC
```
### list

输出Python SDK支持的所有接口：

```bash
$ ./console.py list
>> user input : ['list']

query commands:
1 ): getNodeVersion     无参数(no args)
----------------------------------------------------------------------------------------
2 ): getBlockNumber     无参数(no args)

...省略若干行 ...

22 ): getTotalTransactionCount  无参数(no args)
----------------------------------------------------------------------------------------
23 ): getSystemConfigByKey      name : 配置参数名(system param name),eg:tx_count_limit
----------------------------------------------------------------------------------------
```