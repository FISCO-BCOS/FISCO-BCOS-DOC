# 国密支持方案

标签：``国密算法`` ``SSL`` ``SM``

----
## 设计目标

  为了充分支持国产密码学算法，金链盟基于[国产密码学标准](http://www.gmbz.org.cn/main/bzlb.html)，实现了国密加解密、签名、验签、哈希算法、国密SSL通信协议，并将其集成到FISCO BCOS平台中，实现了对**国家密码局认定的商用密码**的完全支持。

**国密版FISCO BCOS将交易签名验签、p2p网络连接、节点连接、数据落盘加密等底层模块的密码学算法均替换为国密算法**，国密版FISCO BCOS与标准版主要特性对比如下：

 | | 标准版FISCO BCOS | 国密版FISCO BCOS |
 | :-: | :-: | :-: |
 | SSL链接 | Openssl TLSv1.2协议 | 国密TLSv1.1协议|
 | 签名验证 | ECDSA签名算法 | SM2签名算法 |
 | 消息摘要算法 | SHA-256 SHA-3 | SM3消息摘要算法 |
 | 落盘加密算法 | AES-256加密算法 | SM4加密算法 |
 | 证书模式 | OpenSSL证书模式 | 国密双证书模式 |
 | 合约编译器 | 以太坊solidity编译器 | 国密solidity编译器 |

(注：国密算法SM2, SM3, SM4均基于[国产密码学标准](http://www.gmbz.org.cn/main/bzlb.html)开发)

## 系统框架

系统整体框架如下图所示：

![](../../../images/guomi/guomishakehand.png)

## 国密SSL 1.1 握手建立流程

国密版FISCO BCOS节点之间的认证选用国密SSL 1.1的ECDHE_SM4_SM3密码套件进行SSL链接的建立，差异如下表所示：

 | | OpenSSL | 国密SSL |
 | :-: | :-: | :-: |
 | 加密套件 | 采用ECDH、RSA、SHA-256、AES256等密码算法 | 采用国密算法 |
 | PRF算法 | SHA-256 | SM3 |
 | 密钥交换方式 | 传输椭圆曲线参数以及当前报文的签名 | 当前报文的签名和加密证书 |
 | 证书模式 | OpenSSL证书模式 | 国密双证书模式，分别为加密证书和签名证书 |


## 数据结构差异

国密版与标准版FISCO BCOS在数据结构上的差异如下：

 | 算法类型 | 标准版FISCO BCOS | 国密版FISCO BCOS |
 | :-: | :-: | :-: |
 | 签名 | ECDSA (公钥长度: 512 bits, 私钥长度: 256 bits)| SM2 (公钥长度：512 bits, 私钥长度：256 bits) |
 | 哈希 | SHA3 (哈希串长度: 256 bits) | SM3 (哈希串长度: 256 bits) |
 | 对称加解密 | AES (加密秘钥长度: 256 bits) | SM4 (对称密钥长度: 128 bits) |
 | 交易长度 | 520bits(其中标识符8bits,签名长度512bits) | 1024bits(128字节，其中公钥512bits,签名长度512bits) |