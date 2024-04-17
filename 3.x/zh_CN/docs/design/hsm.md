# 15. 硬件密码机

标签：``硬件加密`` ``HSM`` ``密码机``

-----

## 一、密码机与GMT0018简介

### 密码机HSM

硬件安全模块（Hardware security module，HSM）是一种用于保障和管理强认证系统所使用的数字密钥，并同时提供相关密码学操作的计算机硬件设备。硬件安全模块一般通过扩展卡或外部设备的形式直接连接到电脑或网络服务器。

### GMT0018

《GMT0018-2012 密码设备应用接口规范》是由国家密码管理局发布的，符合中国密码行业标准的一个密码设备应用接口规范。它为公钥密码基础设施应用体系框架下的服务类密码设备制定统一的应用接口标准，通过该接口调用密码设备，向上层提供基础密码服务。为该类密码设备的开发、使用及检测提供标准依据和指导，有利于提高该类密码设备的产品化、标准化和系列化水平。

FISCO BCOS 2.8.0和FISCO BCOS 3.3.0版本引入了密码机功能。用户可以将密码放入密码机，通过密码机进行**共识签名**、**交易验签**。FISCO BCOS支持《GMT0018-2012 密码设备应用接口规范》的密码卡/密码机，支持SDF标准，这使得FISCO BCOS拥有更快的密码计算速度，更安全的密钥保护。

## 二、调用密码机的模块

FISCO BCOS的共识和交易模块调用了密码机。
共识、交易模块在签名时，调用`bcos-crypto`模块，`bcos-crypto`再调用`hsm-crypto`模块，最终调用到密码机API接口完成签名。其中涉及到的参数还有通过配置文件传入的密码机内置密钥索引keyIndex，最终调用密码机签名接口`SDF_InternalSign_ECC`。
交易模块在验签时，同理调用`bcos-crypto`模块，`bcos-crypto`再调用`hsm-crypto`模块，最终调用到密码机API接口完成签名。最终调用密码机验签接口`SDF_ExternalVerify_ECC`。

### hsm-crypto模块

hsm-crypto是个封装密码机API接口，使用C++实现的硬件加密模块（Hardware secure module），它能协助应用调用符合《GMT0018-2012密码设备通用接口规范》的PCI密码卡或者密码机进行国密算法SM2、SM3、SM4运算。FISCO BCOS节点，以及java-sdk都通过调用该模块，调用密码机API接口。[Github项目地址](https://github.com/WeBankBlockchain/hsm-crypto)

至此，硬件密码机HSM的设计文档到此结束，关于FISCO BCOS和java-sdk如何使用密码机，请参考[构建使用硬件密码模块的国密链](../tutorial/air/use_hsm.md)
