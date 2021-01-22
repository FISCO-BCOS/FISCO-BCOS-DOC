# 交易执行失败

标签：``交易执行`` ``问题排查``

----

FISCO BCOS完整的错误码描述请参考[这里](../api.html#id1).
## 1. out of gas

**问题描述:**

交易回执状态值为`0xc`，错误描述`out-of-gas during EVM execution`。
这个错误可能的原因:
1. 合约逻辑比较复杂。
2. 合约逻辑问题，数组结构体没有初始化，或者出现死循环。

**解决方法:**

1. 使用控制台加大tx_gas_limit的值，操作流程参考: [控制台设置tx_gas_limit](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/console.html#setsystemconfigbykey)

2. 检查合约的逻辑，修复合约逻辑的漏洞

## 2. revert instruction

**问题描述:**

交易回滚，交易回执状态值为`0x16`，错误描述`revert instruction`，这个错误是因为合约的逻辑问题，包括:
   * 访问调用未初始化的合约
   * 访问初始化为0x0的合约
   * 数组越界访问
   * 除零错误
   * 调用`assert`、`revert`
   * 其他错误

**解决方法:**

检查合约逻辑，修复漏洞。

