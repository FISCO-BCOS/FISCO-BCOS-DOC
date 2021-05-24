# 合约管理

标签：``合约生命周期`` ``智能合约``

----

本文档描述合约生命周期管理中冻结/解冻操作（以下简称合约生命周期管理操作）及其操作权限的设计方案。

```eval_rst
.. important::
   合约生命周期管理操作支持storagestate的存储模式，不支持mptstate的存储模式。这里提及的合约目前只限于Solidity合约，不包括预编译合约。
```

## 名词解释

合约管理的相关操作包括冻结、解冻、查询状态、授权、查询授权。

- [冻结合约](../../console/console.html#freezecontract)：可逆操作，一合约冻结后读写接口都不能被调用
- [解冻合约](../../console/console.html#unfreezecontract)：撤销冻结的操作，一合约解冻后读写接口都可调用
- [查询合约状态](../../console/console.html#getcontractstatus)：查询合约状态，返回该合约可用/已冻结的状态
- [授权](../../console/console.html#grantcontractstatusmanager)：已有权限的账号可以给其他账号授予合约管理权限
- [查询授权](../../console/console.html#listcontractstatusmanager)：查询合约的管理权限列表

```eval_rst
.. important::
   冻结合约的操作不会对原有合约内容（代码逻辑+数据）进行修改，只会通过标志位进行记录。
```

合约状态（可用、已冻结）转换矩阵如下：

|                  | available（可用）          | frozen（已冻结） |
| ---------------- | -------------------------- | ---------------- |
| freeze（冻结）   | 冻结成功                   | 失败，提示已冻结 |
| unfreeze（解冻） | 失败，提示合约可用无需解冻 | 解冻成功         |

## 具体实现

### 合约状态存储

- 新增一字段frozen，用于记录该合约是否已冻结，该字段默认为false，表示可用，冻结时该值为true；
- 新增一字段authority，用于记录合约管理员账号，每个账号对应一行authority记录。

**注意：**

1. 对不存在字段frozen的合约表，查询该字段时将返回false；
2. 部署合约时，将部署账号tx.origin写入authority;
3. 调用合约A接口过程中创建新合约B时，对于新合约B，默认将tx.origin及合约A的权限信息写入合约B的authority。

### 合约状态判断

Executive中根据合约地址获取frozen字段值，进行判断后交易顺利执行，或者抛出异常，提示该合约已冻结。

### 管理权限判断

- 更新合约状态的操作需进行权限判断，只有authority列表中的账号才能设置该合约的状态；
- 授予权限的操作也需进行权限判断，只有authority列表中的账号才能授予其他账号管理该合约的权限；
- 查询合约状态及权限列表不需进行权限判断。

### 合约生命周期管理接口

新增一个合约生命周期管理的预编译合约ContractLifeCyclePrecompiled，地址为0x1007，用于给指定合约设置指定状态，并提供查询功能。

```text
contract ContractLifeCyclePrecompiled {
    function freeze(address addr) public returns(int);
    function unfreeze(address addr) public returns(int);
    function grantManager(address contractAddr, address userAddr) public returns(int);
    function getStatus(address addr) public constant returns(uint,string);
    function listManager(address addr) public constant returns(uint,address[]);
}
```

### 返回码描述

| code   | message                                                    |
| ------ | ---------------------------------------------------------- |
| 0      | success                                                    |
| -51900 | the contract has been frozen                               |
| -51901 | the contract is available                                  |
| -51902 | the contract has been granted authorization with same user |
| -51903 | the contract address is invalid                            |
| -51904 | the address is not exist                                   |
| -51905 | this operation has no permissions                          |

```eval_rst
.. important::
   兼容性说明：合约管理相关操作只能在2.3及以上版本上进行。
```
