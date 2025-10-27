# 版本问题

标签： ``问题排查`` ``版本问题`` ``版本不正确`` ``Java版本`` ``版本兼容性``

----

## FISCO BCOS版本疑问
**问题:FISCO BCOS 2.0版本与之前版本有哪些变化？**<br>
**答案**:请[参考这里](./what_is_new.md)。

**问题：国密和普通版本的区别有哪些？**<br>
**答案：**
国密版FISCO BCOS将交易签名验签、p2p网络连接、节点连接、数据落盘加密等底层模块的密码学算法均替换为国密算法。同时在编译版本，证书，落盘加密，solidity编译java，Web3SDK使用国密版本和普通版本都有区别，具体请[参考这里](../manual/guomi_crypto.md)。

**问题：FISCO BCOS 2.0版本的智能合约与之前版本合约有什么不同，兼容性如何？**<br>
**答案：**
FISCO BCOS 2.0版本支持最新的Solidity合约，同时增加了precompile合约，具体请 [参考这里](../manual/smart_contract.md)。

**问题：是否支持从1.3或1.5升级到2.0版本？**<br>
**答案：** 不支持。

<hr>

## Java版本不正确（Java SDK）

Java SDK要求JDK版本大于等于1.8，推荐使用OpenJDK/OracleJDK 15以下版本的JDK，不同系统的Java安装请参考[这里](../installation.html#id9)