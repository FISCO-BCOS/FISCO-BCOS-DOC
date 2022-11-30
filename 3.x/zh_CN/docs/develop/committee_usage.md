# 权限治理使用指南

标签：``合约权限`` ``部署权限`` ``权限控制`` ``权限使用``

----

FISCO BCOS 3.x 引入了合约粒度的权限治理体系。治理委员会可通过投票的方式管理合约的部署、合约的接口调用权限。

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

使用`-A`选项开启权限模式，可以看到`Auth Mode`已经开启，`Auth init account`初始账户为`0x976fe0c250181c7ef68a17d3bc34916978da103a`。

```shell
## 如果使用-A选项，则开启权限设置，并且随机生成一个账户地址，作为初始化治理委员的唯一admin账户
bash build_chain.sh -l 127.0.0.1:4 -o nodes -A

[INFO] Downloading fisco-bcos binary from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.0.0/fisco-bcos-linux-x86_64.tar.gz ...
######################################################################## 100.0%
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate nodes/127.0.0.1/sdk cert successful!
[INFO] Generate nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node3/conf cert successful!
[INFO] Downloading get_account.sh from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh...
######################################################################## 100.0%
[INFO] Generate uuid success: 4cf39d8a-9f9f-43d0-baa4-3b89d6c9e013
[INFO] Generate uuid success: 16d14ff1-b3f0-4104-8b3e-1bb5faf0ee12
[INFO] Generate uuid success: cc90fc61-bd2f-4029-adaa-135543ec887e
[INFO] Generate uuid success: 12799030-e698-4edd-8719-b5aaa6f8f4c7
==============================================================
[INFO] GroupID              : group0
[INFO] ChainID              : chain0
[INFO] fisco-bcos path      : bin/fisco-bcos
[INFO] Auth mode            : true
[INFO] Auth account         : 0x976fe0c250181c7ef68a17d3bc34916978da103a
[INFO] Start port           : 30300 20200 3901
[INFO] Server IP            : 127.0.0.1:4
[INFO] SM model             : false
[INFO] Output dir           : nodes
[INFO] All completed. Files in nodes

# 随机生成的账户将放置在 {节点名}/ca/accounts 目录下
ls nodes/ca/accounts
0x976fe0c250181c7ef68a17d3bc34916978da103a.pem        0x976fe0c250181c7ef68a17d3bc34916978da103a.public.pem
```

使用`-a`选项开启权限模式，指定账户地址为初始化的治理委员，可以看到`Auth Mode`已经开启，`Auth init account`初始账户为`0x976fe0c250181c7ef68a17d3bc34916978da103a`

```shell

## 如果使用-a选项，则开启权限设置，并指定账户地址作为初始化治理委员的唯一admin账户
bash build_chain.sh -l 127.0.0.1:4 -o nodes -a 0x976fe0c250181c7ef68a17d3bc34916978da103a

[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate nodes/127.0.0.1/sdk cert successful!
[INFO] Generate nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node3/conf cert successful!
[INFO] Generate uuid success: 1b1a3ea8-cacf-498d-9609-6185500b724f
[INFO] Generate uuid success: e0099fb5-0a54-4073-9f67-077772187df6
[INFO] Generate uuid success: 5a8c6f09-1984-4bbc-9c9b-627d6a125985
[INFO] Generate uuid success: 688d7047-644b-48e3-b61b-6b4360b5ac2c
==============================================================
[INFO] GroupID               : group0
[INFO] ChainID               : chain0
[INFO] fisco-bcos path       : bin/fisco-bcos
[INFO] Auth mode             : true
[INFO] Auth account          : 0x976fe0c250181c7ef68a17d3bc34916978da103a
[INFO] Start port            : 30300 20200 3901
[INFO] Server IP             : 127.0.0.1:4
[INFO] SM model              : false
[INFO] Output dir            : nodes
[INFO] All completed. Files in nodes
```

#### 查看节点权限配置

无论是使用`-A`还是`-a`选项开启权限治理，均会体现在每个节点的配置中。节点在启动初始化的时候将会读取配置，对权限合约进行初始化。

我们以 `nodes/127.0.0.1/node0/config.genesis` 为例：

```ini
...
[executor]
    ; use the wasm virtual machine or not
    is_wasm=false
    is_auth_check=true
    auth_admin_account=0x976fe0c250181c7ef68a17d3bc34916978da103a
    is_serial_execute=false
...
```

### FISCO BCOS Pro/Max 版开启权限治理

FISCO BCOS Pro版的建链部署工具详情请参考：[搭建Pro版区块链网络](../tutorial/pro/installation.md)。在这里以BcosBuilder为例，开启权限治理设置。

在开启Pro/Max版区块链网络权限模式之前，请保证已经完成[部署Pro版本区块链节点](../tutorial/pro/installation.html#id4)之前的所有步骤。

在复制配置文件时，需要手动配置权限初始化配置。复制配置文件的步骤参考：[部署RPC服务](../tutorial/pro/installation.html#rpc)

```shell
# 进入config.toml
vim config.toml
```

在这里我们选择开启`auth_check`配置项，并使用`0x976fe0c250181c7ef68a17d3bc34916978da103a`账户地址作为初始化治理委员，请以实际情况为准。

如何创建、使用链上账户，请参考链接：[创建和使用账户](./account.md)。

配置详情请参考：[tars服务配置](../tutorial/pro/pro_builder.html#tars)

```toml
...

[group]
group_id="group0"
vm_type="evm"
sm_crypto=false
auth_check=true
init_auth_address="0x976fe0c250181c7ef68a17d3bc34916978da103a"

...
```

完成配置项之后，就可以继续部署RPC服务、GateWay服务和节点服务即可。继续流程参考：[部署RPC服务](../tutorial/pro/installation.html#rpc)

## 控制台使用

控制台有提供权限治理专属的命令，以及切换控制台账户的命令。用户可以通过控制台操作权限的治理，详情请参考：[权限操作命令](./console/console_commands.html#id14)。权限治理的命令只有控制台连入了开启权限治理的节点才会出现。

控制台操作命令包含以下三种类型，详情请参考[权限操作命令](./console/console_commands.html#id14)：

- 查询状态命令，该命令没有权限控制，所有账户均可访问。
- 治理委员专用命令，这些命令只能持有治理委员的账户才可以使用。
- 合约管理员专用命令，这些命令只有对某一个合约具有管理权限的管理员账户才可以访问。

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



### 4. 升级权限投票权重计算逻辑

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
