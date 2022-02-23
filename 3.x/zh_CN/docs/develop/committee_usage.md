# 权限治理使用指南

标签：``合约权限`` ``部署权限`` ``权限控制`` ``权限使用``

----

FISCO BCOS 3.0+ 引入了合约粒度的权限治理体系。治理委员会可通过投票的方式管理合约的部署、合约的接口调用权限。

详细设计请参考链接：[权限治理体系设计](../design/committee_design.md)

## 开启权限治理模式

在区块链初始化启动之前，在配置中必须开启并设置好权限治理的配置，才能正确启动权限治理模式。区块链启动后再配置将不起作用。

开启权限治理模式的主要要点是：将`is_auth_check`选项置为`true`，`auth_admin_account`初始委员会账户地址需要配置正确的地址。

FISCO BCOS不同的节点部署模式，开启权限治理的方式略有不同。本节将分开讨论不同的节点部署模式下开启权限治理的方式。

### FISCO BCOS Air版开启权限治理

FISCO BCOS Air版的建链部署工具详情请参考：[Air版部署工具](../tutorial/air/build_chain.md)。在这里以搭建四节点为例，开启权限治理设置。

建链部署工具有`-A`和`-a`两种模式，用于开启权限模式：

- `-A`: 将开启权限设置，并使用`get_account.sh`与`get_gm_account.sh`脚本随机生成一个账户地址，并将生成账户的公私密钥对放置在链的`ca`目录中，账户的创建和使用请参考：[创建和使用账户](./account.md)
- `-a`：将开启权限设置，并指定一个账户地址作为初始化治理委员的唯一账户，**在指定时，必须确认账户是存在的且账户地址是正确的，否则将会因为没有治理委员权限而导致权限治理不可用的情况**。

#### 开启权限治理举例

使用`-A`选项开启权限模式，可以看到`Auth Mode`已经开启，`Auth init account`初始账户为`0x2690ef01645972e0940400a4bb43f62eb4e2b7f1`。

```shell
## 如果使用-A选项，则开启权限设置，并且随机生成一个账户地址，作为初始化治理委员的唯一admin账户
bash build_chain.sh -l 127.0.0.1:4 -o nodes -A

[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate nodes/127.0.0.1/sdk cert successful!
[INFO] Generate nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node3/conf cert successful!
[INFO] Downloading get_account.sh from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh...
############################################################################################# 100.0%
==============================================================
[INFO] fisco-bcos Path     : bin/fisco-bcos
[INFO] Auth Mode           : true
[INFO] Auth init account   : 0x2690ef01645972e0940400a4bb43f62eb4e2b7f1
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 127.0.0.1:4
[INFO] SM Model            : false
[INFO] output dir          : nodes
[INFO] All completed. Files in nodes

# 随机生成的账户将放置在 {节点名}/ca/accounts 目录下
ls nodes/ca/accounts
0x2690ef01645972e0940400a4bb43f62eb4e2b7f1.pem        0x2690ef01645972e0940400a4bb43f62eb4e2b7f1.public.pem
```

使用`-a`选项开启权限模式，指定账户地址为初始化的治理委员，可以看到`Auth Mode`已经开启，`Auth init account`初始账户为`0x2690ef01645972e0940400a4bb43f62eb4e2b7f1`

```shell

## 如果使用-a选项，则开启权限设置，并指定账户地址作为初始化治理委员的唯一admin账户
bash build_chain.sh -l 127.0.0.1:4 -o nodes -a 0x2690ef01645972e0940400a4bb43f62eb4e2b7f1

[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate nodes/127.0.0.1/sdk cert successful!
[INFO] Generate nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node3/conf cert successful!
==============================================================
[INFO] fisco-bcos Path     : bin/fisco-bcos
[INFO] Auth Mode           : true
[INFO] Auth init account   : 0x2690ef01645972e0940400a4bb43f62eb4e2b7f1
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 127.0.0.1:4
[INFO] SM Model            : false
[INFO] output dir          : nodes
[INFO] All completed. Files in nodes
```

#### 查看节点权限配置

无论是使用`-A`还是`-a`选项开启权限治理，均会体现在每个节点的配置中。节点在启动初始化的时候将会读取配置，对权限合约进行初始化。

我们以 `nodes/127.0.0.1/node0/config.ini` 为例：

```ini
...

[executor]
    ; use the wasm virtual machine or not
    is_wasm=false
    is_auth_check=true
    auth_admin_account=0x2690ef01645972e0940400a4bb43f62eb4e2b7f1

...
```

### FISCO BCOS Pro版开启权限治理

FISCO BCOS Pro版的建链部署工具详情请参考：[搭建Pro版区块链网络](../tutorial/pro/installation.md)。在这里以BcosProBuilder为例，开启权限治理设置。

在开启Pro版区块链网络权限模式之前，请保证已经完成[部署Pro版本区块链节点](../tutorial/pro/installation.html#id4)之前的所有步骤。

在复制配置文件时，需要手动配置权限初始化配置。复制配置文件的步骤参考：[部署RPC服务](../tutorial/pro/installation.html#rpc)

```shell
# 进入config.toml
vim config.toml
```

在这里我们选择开启`auth_check`配置项，并使用`0x2690ef01645972e0940400a4bb43f62eb4e2b7f1`账户地址作为初始化治理委员，请以实际情况为准。

如何创建、使用链上账户，请参考链接：[创建和使用账户](./account.md)。

配置详情请参考：[tars服务配置](../tutorial/pro/pro_builder.html#tars)

```toml
...

[group]
group_id="group0"
vm_type="evm"
sm_crypto=false
auth_check=true
init_auth_address="0x2690ef01645972e0940400a4bb43f62eb4e2b7f1"

...
```

完成配置项之后，就可以继续部署RPC服务、GateWay服务和节点服务即可。继续流程参考：[部署RPC服务](../tutorial/pro/installation.html#rpc)

## 控制台使用

控制台有提供权限治理专属的命令，以及切换控制台账户的命令。用户可以通过控制台操作权限的治理，详情请参考：[权限操作命令](./console/console_commands.html#id14)。权限治理的命令只有控制台连入了开启权限治理的节点才会出现。

控制台操作命令包含以下三种类型：

- 查询状态命令，该命令没有权限控制，所有账户均可访问。
- 治理委员专用命令，这些命令只能持有治理委员的账户才可以使用。
- 合约管理员专用命令，这些命令只有对某一个合约具有管理权限的管理员账户才可以访问。

### 1. 查询状态命令

该命令没有权限控制，所有账户均可访问。

- [getCommitteeInfo](./console/console_commands.html#getcommitteeinfo)：获取治理委员会详细信息；
- [getProposalInfo](./console/console_commands.html#getproposalinfo)：获取某个特定的提案信息；
- [getDeployAuth](./console/console_commands.html#getdeployauth)：获取当前全局的部署权限策略，分为：无策略，白名单策略，黑名单策略；
- [checkDeployAuth](./console/console_commands.html#checkdeployauth)：检查某个账户是否有部署权限；
- [checkMethodAuth](./console/console_commands.html#checkmethodauth)：检查某个账户是否有调用某个合约接口的权限；
- [getLatestProposal](./console/console_commands.html#getlatestproposal)：获取最新提案的ID；
- [getContractAdmin](./console/console_commands.html#getcontractadmin)：获取某个合约的管理员账户地址；

proposalType分为以下几种：

- setWeight：当治理委员发起updateGovernorProposal 提案时会生成
- setRate：setRateProposal 提案会生成
- setDeployAuthType：setDeployAuthTypeProposal 提案会生成
- modifyDeployAuth：openDeployAuthProposal 和closeDeployAuthProposal 提案会生成
- resetAdmin：resetAdminProposal 提案会生成
- unknown：这个类型出现时，有可能是有bug

status分为以下几种：

- notEnoughVotes：提案正常，还未收集到足够的投票
- finish：提案执行完成
- failed：提案失败
- revoke：提案被撤回
- unknown：这个类型出现时，有可能是有bug

### 2. 治理委员专用命令

这些命令只能持有治理委员的账户才可以使用。

- [updateGovernorProposal](./console/console_commands.html#updategovernorproposal)：发起一个更新某个治理委员信息的提案；
- [setRateProposal](./console/console_commands.html#setrateproposal)：发起一个更改治理委员会提案阈值的提案；
- [setDeployAuthTypeProposal](./console/console_commands.html#setdeployauthtypeproposal)：发起一个设置全局部署权限策略的提案；
- [openDeployAuthProposal](./console/console_commands.html#opendeployauthproposal)：发起一个开启某个账户部署权限的提案；
- [closeDeployAuthProposal](./console/console_commands.html#closedeployauthproposal)：发起一个关闭某个账户部署权限的提案；
- [resetAdminProposal](./console/console_commands.html#resetadminproposal)：发起一个重置某个合约管理员的提案；
- [revokeProposal](./console/console_commands.html#revokeproposal)：提案发起人撤回一个提案；
- [voteProposal](./console/console_commands.html#voteproposal)：治理委员向某一个提案进行投票；

### 3. 合约管理员专用命令

这些命令只有对某一个合约具有管理权限的管理员账户才可以访问。

- [setMethodAuth](./console/console_commands.html#setmethodauth)：合约管理员设置某一个方法的策略；
- [openMethodAuth](./console/console_commands.html#openmethodauth)：合约管理员开启某个账户对某个接口的权限；
- [closeMethodAuth](./console/console_commands.html#closemethodauth)：合约管理员关闭某个账户对某个接口的权限；

## 使用举例

首先使用build_chain.sh脚本搭建权限模式的区块链：

这里参考[创建和使用账户](./account.md)链接创建新的账户，指定初始化治理委员的账户地址为 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642

可以使用`-A`选项自动生成一个账户。账户是有国密和非国密区分的，会根据链的类型自动生成。

```shell
 bash build_chain.sh  -l 127.0.0.1:4 -o nodes4 -a 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
```

### 1. 治理委员使用

使用`getCommitteeInfo`命令可以看到，初始化时治理委员只有一个，权重为1.

并且当前控制台所用的账户就是委员

```shell
[group0]: /> getCommitteeInfo
---------------------------------------------------------------------------------------------
Committee address   : 0xcbc22a496c810dde3fa53c72f575ed024789b2cc
ProposalMgr address : 0xa0974646d4462913a36c986ea260567cf471db1f
---------------------------------------------------------------------------------------------
ParticipatesRate: 0% , WinRate: 0%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642     | 1

[group0]: /> getCurrentAccount
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
```

用当前账户发起一个更新治理委员的提案：

可以看到，发起了一个提案（proposal），提案编号为1。

因为当前治理委员会只有一个委员，且参与阈值和权重阈值都为0，因此发起的提案一定能成功。

使用`getCommitteeInfo` 命令看，确实已经更新了治理委员的权重

```shell
[group0]: /> updateGovernorProposal 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642 2
Update governor proposal created, ID is: 1
---------------------------------------------------------------------------------------------
Proposer: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Proposal Type   : setWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
---------------------------------------------------------------------------------------------
Against Voters:

```

也可以使用`updateGovernorProposal`添加新的治理委员：

这里只会做长度和字符校验，不会对正确性校验。可以看到成功添加了一个治理委员，权重为1

```shell
[group0]: /> updateGovernorProposal 0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e 1
Update governor proposal created, ID is: 2
---------------------------------------------------------------------------------------------
Proposer: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Proposal Type   : setWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
---------------------------------------------------------------------------------------------
Against Voters:
```

也可以使用`updateGovernorProposal`删除治理委员：

设置账户权重为0，则删除治理委员

```shell
[group0]: /> updateGovernorProposal 0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e 0
Update governor proposal created, ID is: 3
---------------------------------------------------------------------------------------------
Proposer: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Proposal Type   : setWeight
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /> getCommitteeInfo
---------------------------------------------------------------------------------------------
Committee address   : 0xcbc22a496c810dde3fa53c72f575ed024789b2cc
ProposalMgr address : 0xa0974646d4462913a36c986ea260567cf471db1f
---------------------------------------------------------------------------------------------
ParticipatesRate: 0% , WinRate: 0%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642     | 2
```

使用`setRateProposal`命令更改治理委员的投票阈值

```shell
[group0]: /> setRateProposal 51 51
Set rate proposal created, ID is: 4
---------------------------------------------------------------------------------------------
Proposer: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Proposal Type   : setRate
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /> getCommitteeInfo
---------------------------------------------------------------------------------------------
Committee address   : 0xcbc22a496c810dde3fa53c72f575ed024789b2cc
ProposalMgr address : 0xa0974646d4462913a36c986ea260567cf471db1f
---------------------------------------------------------------------------------------------
ParticipatesRate: 51% , WinRate: 51%
---------------------------------------------------------------------------------------------
Governor Address                                        | Weight
index0 : 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642     | 2
```

此时，委员会的参与率必须大于51，权重阈值也必须大于51，委员会有两名账户

使用当前账户发起 `setDeployAuthTypeProposal` 提案，变更全局部署权限策略，使用白名单模式。

此时，可以看到第6个提案的类型是`setDeployAuthType`，状态是 `notEnoughVotes`，还不可以通过提案，当前的部署权限策略还是处于无策略情况。

```shell
[group0]: /> setDeployAuthTypeProposal white_list
Set deploy auth type proposal created, ID is: 6
---------------------------------------------------------------------------------------------
Proposer: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Proposal Type   : setDeployAuthType
Proposal Status : notEnoughVotes
---------------------------------------------------------------------------------------------
Agree Voters:
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /> getDeployAuth
There is no deploy strategy, everyone can deploy contracts.
```

切换到另外一个委员会账户中，对提案6进行投票，可以看到投票成功，提案状态变更到结束。部署策略也变为白名单模式。

```shell
[group0]: /> loadAccount 0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e
Load account 0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e success!

[group0]: /> voteProposal 6
Vote proposal success.
---------------------------------------------------------------------------------------------
Proposer: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Proposal Type   : setDeployAuthType
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /> getDeployAuth
Deploy strategy is White List Access.
```

### 2. 部署权限

接上文，当前链的部署权限为白名单模式。

治理委员也没有权限部署，但是治理委员可以发起开启某个账户的部署权限。

也可以通过命令`closeDeployAuthProposal` 发起关闭部署权限的提案

```shell
[group0]: /> deploy HelloWorld
deploy contract for HelloWorld failed!
return message: Permission denied
return code:18
Return values:null

[group0]: /> openDeployAuthProposal 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Open deploy auth proposal created, ID is: 7
---------------------------------------------------------------------------------------------
Proposer: 0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e
Proposal Type   : modifyDeployAuth
Proposal Status : notEnoughVotes
---------------------------------------------------------------------------------------------
Agree Voters:
0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /> loadAccount 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Load account 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642 success!

[group0]: /> voteProposal 7
Vote proposal success.
---------------------------------------------------------------------------------------------
Proposer: 0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e
Proposal Type   : modifyDeployAuth
Proposal Status : finished
---------------------------------------------------------------------------------------------
Agree Voters:
0xba0cd3e729cfe3ebdf1f74a10ec237bfd3954e1e
0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
---------------------------------------------------------------------------------------------
Against Voters:

[group0]: /> checkDeployAuth
Deploy : ACCESS
Account: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642

[group0]: /> deploy HelloWorld
transaction hash: 0xe38ca3d69efee66aaf7de5600d3bdada8fd7c658d52bc0401ce65dd7e6437d97
contract address: 0x33E56a083e135936C1144960a708c43A661706C0
currentAccount: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642

[group0]: /> getContractAdmin 0x33E56a083e135936C1144960a708c43A661706C0
Admin for contract 0x33E56a083e135936C1144960a708c43A661706C0 is: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
```

可以看到，通过投票，`0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642` 账户已经有权限部署了。

此时，HelloWorld合约 0x33E56a083e135936C1144960a708c43A661706C0 地址的合约管理员是 `0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642` 账户

### 3. 合约管理员使用

接上文，当前HelloWorld合约 0x33E56a083e135936C1144960a708c43A661706C0 地址的合约管理员是 `0xab835e87a86f94af10c81278bb9a82ea13d82d39` 账户

合约管理员可以设置当前合约的接口策略:

合约管理员对HelloWorld合约的 “set(string)” 合约设置白名单模式，设置成功之后，管理员也没有权限调用set(string)接口

```shell
[group0]: /> getContractAdmin 0x33E56a083e135936C1144960a708c43A661706C0
Admin for contract 0x33E56a083e135936C1144960a708c43A661706C0 is: 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642

[group0]: /> setMethodAuth 0x33E56a083e135936C1144960a708c43A661706C0 "set(string)" white_list
{
    "code":0,
    "msg":"Success"
}

[group0]: /> checkMethodAuth 0x33E56a083e135936C1144960a708c43A661706C0  "set(string)"
Method   : PERMISSION DENIED
Account  : 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
Interface: set(string)
Contract : 0x33E56a083e135936C1144960a708c43A661706C0

[group0]: /> call HelloWorld 0x33E56a083e135936C1144960a708c43A661706C0 set oops
transaction hash: 0xf9fcac9a0d4503e366c582c72fccf4e571b081ba44d46f58ff0b17fb7ab4361b
---------------------------------------------------------------------------------------------
transaction status: 18
---------------------------------------------------------------------------------------------
Receipt message: Permission denied
Return message: Permission denied
---------------------------------------------------------------------------------------------

# 还是可以使用set接口
[group0]: /> call HelloWorld 0x33E56a083e135936C1144960a708c43A661706C0 get
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

管理员可以使用命令开启某个账户对set(string)接口的使用权限

```shell
[group0]: /> openMethodAuth 0x33E56a083e135936C1144960a708c43A661706C0 "set(string)" 0x1cc06388cd8a12dcf7fb8967378c0aea4e6cf642
{
    "code":0,
    "msg":"Success"
}

[group0]: /> call HelloWorld 0x33E56a083e135936C1144960a708c43A661706C0 set "May the flame guide thee."
transaction hash: 0x3986a27e3075d703bc6828984bbfa8115fad612eaecaf7749e835edb495f38d6
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

[group0]: /> call HelloWorld 0x33E56a083e135936C1144960a708c43A661706C0 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(May the flame guide thee.)
---------------------------------------------------------------------------------------------
```
