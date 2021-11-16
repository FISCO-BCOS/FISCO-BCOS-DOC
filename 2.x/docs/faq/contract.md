# 合约编译错误

标签：``合约编译`` ``问题排查``

----

## 1. CompilerError: Stack too deep, try removing local variables

**问题描述:**

编译合约时报错`Stack too deep, try removing local variables`。
这个错误的原因是合约接口定义的局部变量过多，solidity接口最多支持16个局部变量（包括接口参数列表）。

**解决方法:**

- 减少接口内部定义的局部变量个数
- 可以使用数组或者struct类型作为参数减少参数列表的个数


## 2. sol转换java编译报错: Unsupported type encountered: tuple

目前Java SDK还不支持`struct`类型的`sol`到`Java`的转换，后续版本会支持，请关注[Java SDK Issue #248](https://github.com/FISCO-BCOS/java-sdk/issues/248)