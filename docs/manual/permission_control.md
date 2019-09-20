# 权限控制

本文档描述权限控制的实践操作，有关权限控制的详细设计请参考[权限控制设计文档](../design/security_control/permission_control.md)。

```eval_rst
.. important::
    推荐管理员机制：由于系统默认无权限设置记录，因此任何账户均可以使用权限设置功能。例如当账户1设置账户1有权限部署合约，但是账户2也可以设置账户2有权限部署合约。那么账户1的设置将失去控制的意义，因为其他账户可以自由添加权限。因此，搭建联盟链之前，推荐确定权限使用规则。可以使用grantPermissionManager指令设置链管理员账户，即指定特定账户可以使用权限分配功能，非链管理员账户无权限分配功能。
```

## 操作内容

本文档分别对以下功能进行权限控制的操作介绍：
- [授权账户为链管理员](./permission_control.html#id6)
- [授权账户为系统管理员](./permission_control.html#id7)
- [授权部署合约和创建用户表](./permission_control.html#id8)
- [授权利用CNS部署合约](./permission_control.html#cns)
- [授权管理节点](./permission_control.html#id9)
- [授权修改系统参数](./permission_control.html#id10)
- [授权账户写用户表](./permission_control.html#id11)

## 环境配置  
配置并启动FISCO BCOS 2.0区块链节点和控制台，请参考[安装文档](../installation.md)。   

## 权限控制工具
FISCO BCOS提供控制台命令使用权限功能（针对开发者，可以调用[SDK API](../sdk/java_sdk.html#permissionservice)的PermissionService接口使用权限功能），其中涉及的权限控制命令如下:

|命令名称|命令参数|功能|
|:----|:-----|:----|
|grantPermissionManager                |address               |授权账户的链管理员权限              |
|revokePermissionManager             |address               |撤销账户的链管理员权限              |
|listPermissionManager              |                      |查询拥有链管理员权限的账户列表                |
|grantDeployAndCreateManager          |address               |授权账户的部署合约和创建用户表权限    |
|revokeDeployAndCreateManager       |address               |撤销账户的部署合约和创建用户表权限    |
|listDeployAndCreateManager        |                      |查询拥有部署合约和创建用户表权限的账户列表|
|grantNodeManager                     |address               |授权账户的节点管理权限                |
|revokeNodeManager                  |address               |撤销账户的节点管理权限                |
|listNodeManager                   |                      |查询拥有节点管理的账户列表                |
|grantCNSManager                      |address              |授权账户的使用CNS权限                 |
|revokeCNSManager                   |address               |撤销账户的使用CNS权限                 |
|listCNSManager                    |                      |查询拥有使用CNS的账户列表                 |
|grantSysConfigManager                |address             |授权账户的修改系统参数权限            |
|revokeSysConfigManager             |address               |撤销账户的修改系统参数权限            |
|listSysConfigManager              |                       |查询拥有修改系统参数的账户列表            |
|grantUserTableManager                |table_name address  |授权账户对用户表的写权限          |
|revokeUserTableManager             |table_name address    |撤销账户对用户表的写权限        |
|listUserTableManager              |table_name            |查询拥有对用户表写权限的账号列表            |

## 权限控制示例账户
控制台提供账户生成脚本`get_account.sh`，生成的账户文件在`accounts`目录下。控制台可以指定账户启动，具体用法参考[控制台手册](./console.html#id11)。因此，通过控制台可以指定账户，体验权限控制功能。为了账户安全起见，我们可以在控制台根目录下通过`get_account.sh`脚本生成三个PKCS12格式的账户文件，生成过程中输入的密码需要牢记。生成的三个PKCS12格式的账户文件如下：
```bash
# 账户1 
0x2c7f31d22974d5b1b2d6d5c359e81e91ee656252.p12
# 账户2 
0x7fc8335fec9da5f84e60236029bb4a64a469a021.p12
# 账户3
0xd86572ad4c92d4598852e2f34720a865dd4fc3dd.p12    
```
现在可以打开三个连接Linux的终端，分别以三个账户登录控制台。

指定账户1登录控制台：
```bash
$ ./start.sh 1 -p12 accounts/0x2c7f31d22974d5b1b2d6d5c359e81e91ee656252.p12
```
指定账户2登录控制台：
```bash
$ ./start.sh 1 -p12 accounts/0x7fc8335fec9da5f84e60236029bb4a64a469a021.p12
```
指定账户3登录控制台：
```bash
$ ./start.sh 1 -p12 accounts/0xd86572ad4c92d4598852e2f34720a865dd4fc3dd.p12
```

## 授权账户为链管理员
提供的三个账户设为三种角色，设定账户1为链管理员账户，账户2为系统管理员账户，账户3为普通账户。链管理员账户拥有权限管理的权限，即能分配权限。系统管理员账户可以管理系统相关功能的权限，每一种系统功能权限都需要单独分配，具体包括部署合约和创建用户表的权限、管理节点的权限、利用CNS部署合约的权限以及修改系统参数的权限。链管理员账户可以授权其他账户为链管理员账户或系统管理员账户，也可以授权指定账号可以写指定的用户表，即普通账户。
   
链初始状态，没有任何权限账户记录。现在，可以进入账户1的控制台，设置账户1成为链管理员账户，则其他账户为非链管理员账户。
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
设置账户1为链管理员成功。

## 授权账户为系统管理员  

### 授权部署合约和创建用户表
通过账户1授权账户2为系统管理员账户，首先授权账户2可以部署合约和创建用户表。
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
登录账户2的控制台，部署控制台提供的TableTest合约。TableTest.sol合约代码[参考这里](smart_contract.html#solidity)。其提供创建用户表t_test和相关增删改查的方法。
```
[group:1]> deploy TableTest.sol 
contract address:0xfe649f510e0ca41f716e7935caee74db993e9de8
```
调用TableTest的create接口创建用户表t_test。
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
用户表t_test创建成功。

登录账户3的控制台，部署TableTest合约。
```
[group:1]> deploy TableTest.sol 
{
    "code":-50000,
    "msg":"permission denied"
}
```
账户3没有部署合约的权限，部署合约失败。

- **注意：** 其中部署合约和创建用户表是“二合一”的控制项，在使用CRUD合约时，我们建议部署合约的时候一起把合约里用到的表创建了（在合约的构造函数中创建表），否则接下来读写表的交易可能会遇到“缺表”错误。如果业务流程需要动态创建表，动态建表的权限也应该只分配给少数账户，否则链上可能会出现各种废表。

### 授权利用CNS部署合约
控制台提供3个涉及[CNS](../design/features/CNS_contract_name_service.md)的命令，如下所示：

|命令名称|命令参数|功能|
|:----|:-----|:----|
|deployByCNS         |contractName contractVersion                 |利用CNS部署合约           |
|callByCNS           |contractName contractVersion funcName params |利用CNS调用合约           |
|queryCNS            |contractName [contractVersion]               |查询CNS信息               |

**注意：** 其中deployByCNS命令受权限可以控制，**且同时需要部署合约和使用CNS的权限**，callByCNS和queryCNS命令不受权限控制。

登录账户1的控制台，授权账户2拥有利用CNS部署合约的权限。
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
登录账户2的控制台，利用CNS部署合约。
```
[group:1]> deployByCNS TableTest.sol 1.0
contract address:0x24f902ff362a01335db94b693edc769ba6226ff7

[group:1]> queryCNS TableTest.sol 
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x24f902ff362a01335db94b693edc769ba6226ff7  |
---------------------------------------------------------------------------------------------
```
登录账户3的控制台，利用CNS部署合约。
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
部署失败，账户3无权限利用CNS部署合约。

### 授权管理节点

控制台提供5个有关节点类型操作的命令，如下表所示：

|命令名称|命令参数|功能|
|:----|:-----|:----|
|addSealer             |nodeID                 |设置节点为共识节点         |
|addObserver           |nodeID                 |设置节点为观察节点         |
|removeNode            |nodeID                 |设置节点为游离节点         |
|getSealerList         |                       |查询共识节点列表           |
|getObserverList       |                       |查询观察节点列表           |

- **注意：** 其中addSealer、addObserver和removeNode命令受权限控制，getSealerList和getObserverList命令不受权限控制。

登录账户1的控制台，授权账户2拥有管理节点的权限。
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
登录账户2的控制台，查看共识节点列表。
```
[group:1]> getSealerList 
[
    01cd46feef2bb385bf03d1743c1d1a52753129cf092392acb9e941d1a4e0f499fdf6559dfcd4dbf2b3ca418caa09d953620c2aa3c5bbe93ad5f6b378c678489e,
    279c4adfd1e51e15e7fbd3fca37407db84bd60a6dd36813708479f31646b7480d776b84df5fea2f3157da6df9cad078c28810db88e8044741152eb037a19bc17,
    320b8f3c485c42d2bfd88bb6bb62504a9433c13d377d69e9901242f76abe2eae3c1ca053d35026160d86db1a563ab2add127f1bbe1ae96e7d15977538d6c0fb4,
    c26dc878c4ff109f81915accaa056ba206893145a7125d17dc534c0ec41c6a10f33790ff38855df008aeca3a27ae7d96cdcb2f61eb8748fefe88de6412bae1b5
]
```
查看观察节点列表：
```
[group:1]> getObserverList
[]
```
将第一个nodeID对应的节点设置为观察节点：
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
登录账户3的控制台，将观察节点加入共识节点列表。
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
添加共识节点失败，账户3没有权限管理节点。现在只有账户2有权限将观察节点加入共识节点列表。

### 授权修改系统参数
控制台提供2个关于修改系统参数的命令，如下表所示：

|命令名称|命令参数|功能|
|:----|:-----|:----|
|setSystemConfigByKey             |key value           |设置键为key，值为value的系统参数  |
|getSystemConfigByKey             |key                 |根据key查询value                |

- **注意：** 目前支持键为tx_count_limit和tx_gas_limit的系统参数设置。其中setSystemConfigByKey命令受权限控制，getSystemConfigByKey命令不受权限控制。

登录账户1的控制台，授权账户2拥有修改系统参数的权限。
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
登录账户2的控制台，修改系统参数tx_count_limit的值为2000。
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
登录账户3的控制台，修改系统参数tx_count_limit的值为3000。
```
[group:1]> setSystemConfigByKey tx_count_limit 3000
{
    "code":-50000,
    "msg":"permission denied"
}

[group:1]> getSystemConfigByKey tx_count_limit 
2000
```
设置失败，账户3没有修改系统参数的权限。

## 授权账户写用户表
通过账户1授权账户3可以写用户表t_test的权限。
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
登录账户3的控制台，在用户表t_test插入一条记录，然后查询该表的记录。
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
登录账户2的控制台，更新账户3插入的记录，并查询该表的记录。
```
[group:1]> call TableTest.sol 0xfe649f510e0ca41f716e7935caee74db993e9de8 update "fruit" 1 "orange"
{
    "code":-50000,
    "msg":"permission denied"
}
[group:1]> call TableTest.sol 0xfe649f510e0ca41f716e7935caee74db993e9de8 select "fruit"
[[fruit], [1], [apple]]
```
更新失败，账户2没有权限更新用户表t_test。

- 通过账户1撤销账户3写用户表t_test的权限。
```
[group:1]> revokeUserTableManager t_test 0xd86572ad4c92d4598852e2f34720a865dd4fc3dd
{
    "code":0,
    "msg":"success"
}

[group:1]> listUserTableManager t_test
Empty set.
```
撤销成功。

- **注意：** 此时没有账户拥有对用户表t_test的写权限，因此对该表的写权限恢复了初始状态，即所有账户均拥有对该表的写权限。如果让账户1没有对该表的写权限，则可以通过账号1授权另外一个账号，比如账号2拥有该表的写权限实现。


