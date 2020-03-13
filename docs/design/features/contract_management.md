# 合约管理

本文档描述合约生命周期管理中冻结/解冻/销毁操作及其操作权限的设计方案。

```eval_rst
.. important::
   合约的冻结/解冻/销毁操作支持storagestate的存储模式，不支持mptstate的存储模式。这里提及的合约目前只限于智能合约，不包括预编译合约、CRUD合约。
```

## 名词解释

合约管理的相关操作包括冻结、解冻、销毁、查询状态、授权、查询授权。

- 冻结合约：可逆操作，一合约冻结后读写接口都不能被调用
- 解冻合约：撤销冻结的操作，一合约解冻后读写接口都可调用
- 销毁合约：不可逆操作，一合约冻结后合约代码删除，读写接口都不能被调用，且无法恢复可调用状态
- 查询合约状态：查询合约状态，返回该合约可用/已冻结/已销毁的状态
- 授权：已有权限的账号可以给其他账号授予合约管理权限
- 查询授权：查询合约的管理权限列表

合约状态（可用、已冻结、已销毁）转换矩阵如下：

|                  | available（可用）          | frozen（已冻结） | destroy（已销毁） |
| ---------------- | -------------------------- | ---------------- | ----------------- |
| freeze（冻结）   | 冻结成功                   | 失败，提示已冻结 | 失败，提示已销毁  |
| unfreeze（解冻） | 失败，提示合约可用无需解冻 | 解冻成功         | 失败，提示已销毁  |
| destroy（销毁）  | 销毁成功                   | 销毁成功         | 失败，提示已销毁  |

## 具体实现

### 合约状态存储

- 复用合约表中已有的alive字段，用于记录该合约是否已销毁，该字段默认为true，销毁时该值为false；
- 新增一字段frozen，用于记录该合约是否已冻结，该字段默认为false，表示可用，冻结时该值为true；
- 新增一字段authority，用于记录能冻结/解冻/销毁该合约的账号，每个账号对应一行authority记录。

**注意：**

1. 对不存在字段frozen的合约表，查询该字段时将返回false；
2. 部署合约时，默认将tx.origin及创建该合约的父合约（如有）的权限信息写入authority。

### 合约状态判断

Executive中根据合约地址获取alive和frozen字段值，进行判断后交易顺利执行，或者抛出异常，提示该合约已冻结或已销毁。判断规则为：合约在alive为true且frozen为false时，才可调用；两字段中，alive判断的优先级高于frozen，只有非销毁状态时才需判断是否已冻结。

### 管理权限判断

- 设置（冻结/解冻/销毁）合约状态的操作需进行权限判断，只有authority列表中的账号才能设置该合约的状态；
- 授予权限的操作也需进行权限判断，只有authority列表中的账号才能授予其他账号管理该合约的权限；
- 查询合约状态及权限列表不需进行权限判断。

### 合约状态管理接口

新增一个合约状态管理的预编译合约ContractStatusPrecompiled，地址为0x1007，用于给指定合约设置指定状态，并提供查询功能。

```text
contract ContractStatusPrecompiled {
    function destroy(address addr) public returns(int);
    function freeze(address addr) public returns(int);
    function unfreeze(address addr) public returns(int);
    function grantManager(address contractAddr, address userAddr) public returns(int);
    function getStatus(address addr) public constant returns(uint,string);
    function listManager(address addr) public constant returns(uint,address[]);
}
```

```eval_rst
.. important::
   兼容性说明：合约管理相关操作只能在2.3及以上版本上进行。
```
