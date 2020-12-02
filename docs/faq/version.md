# 版本相关

## FISCO BCOS 2.0版本与之前版本有哪些变化

请 [参考这里](../what_is_new.md)。

## 开发者如何与FISCO BCOS平台交互

FISCO BCOS提供多种开发者与平台交互的方式，参考如下：

- FISCO BCOS 2.0版本提供JSON-RPC接口，具体请 [参考这里](../api.md)。
- FISCO BCOS 2.0版本提供Web3SDK帮助开发者快速实现应用，具体请 [参考这里](../sdk/java_sdk.md)。
- FISCO BCOS 2.0版本提供控制台帮助用户快速了解使用FISCO BCOS，[2.6及其以上版本控制台使用文档参考这里](../manual/console_of_java_sdk.md)，[1.x版本控制台使用文档参考这里](../manual/console.md)。

## FISCO BCOS 2.0版本如何搭建

FISCO BCOS支持多种搭建方式，常用方式有：

- 开发部署工具 build_chain.sh：适合开发者体验、测试FISCO BCOS联盟链，具体请 [参考这里](../manual/build_chain.md)。
- 运维部署工具 generator：适用于企业用户部署、维护FISCO BCOS联盟链，具体请 [参考这里](../enterprise_tools/index.md)。

## FISCO BCOS 2.0版本的智能合约与之前版本合约有什么不同，兼容性如何

FISCO BCOS 2.0版本支持最新的Solidity合约，同时增加了precompile合约，具体请 [参考这里](../manual/smart_contract.md)。

## 国密和普通版本的区别有哪些

国密版FISCO BCOS将交易签名验签、p2p网络连接、节点连接、数据落盘加密等底层模块的密码学算法均替换为国密算法。同时在编译版本，证书，落盘加密，solidity编译java，Web3SDK使用国密版本和普通版本都有区别，具体请 [参考这里](../manual/guomi_crypto.md)。

## 是否支持从1.3或1.5升级到2.0版本

不支持。
