# FISCO BCOS的国密特性

作者：李昊轩｜FISCO BCOS 核心开发者


> 安全是信任的保障，算法则是安全的基础，节点通信信道的建立、签名的生成、数据的加密等，都需要用到密码学的相应算法。FISCO BCOS采用国密算法，实现安全可控的区块链架构。

本期文章讲解国密算法的相关概念，以及国密算法在区块链中的应用。

## 区块链中的密码学

在区块链中，一般使用密码学的场景有以下几种：

### 1. 数据哈希算法

哈希函数是一类单向函数，作用是将任意长度的消息转换为定长的输出值，具有单向性、无碰撞性、确定性、不可逆的性质。
在区块链中，哈希函数用于将消息压缩成定长的输出，以及保证数据真实性，确保数据未被修改。

### 2. 数据加密解密算法

加解密算法主要分为对称加密和非对称加密两种：
- 对称加密具有速度快、效率高、加密强度高等特点，使用时需要提前协商密钥，主要对大规模数据进行加密，如FISCO BCOS的节点数据落盘时进行的加密。
- 非对称加密具有无需协商密钥的特点，相较于对称加密计算效率较低，存在中间人攻击等缺陷，主要用于密钥协商的过程。

针对不同的需求，两者可以互相配合，组合使用。

### 3. 消息签名的生成和验证

在区块链中，需要对消息进行签名，用于消息防篡改和身份验证。如节点共识过程中，需要对其他节点的身份进行验证，节点需要对链上交易数据进行验证等。

### 4. 握手建立流程

前面我们讲过[**节点TLS的握手过程**](https://mp.weixin.qq.com/s?__biz=MzU5NTg0MjA4MA==&mid=2247484845&idx=1&sn=c943c8a3f55a060991ffafec8bbdb27b&scene=21#wechat_redirect)（点击直达），其中需要使用到密码组件和数字证书，都需要使用到相应的密码学算法。

## FISCO BCOS的国密算法

国密算法由国家密码局发布，包含SM1\ SM2\ SM3\ SM4\等，为我国自主研发的密码算法标准。
为了充分支持国产密码学算法，金链盟基于国产密码学标准，实现了国密加解密、签名、验签、哈希算法、国密SSL通信协议，并将其集成到FISCO BCOS平台中，实现了对国家密码局认定的商用密码的完全支持。
国密版FISCO BCOS将交易签名验签、p2p网络连接、节点连接、数据落盘加密等底层模块的密码学算法均替换为国密算法。

1. 节点TLS握手中采用国密SSL算法；
2. 交易签名生成，验证过程采用国密SM2算法；
3.  数据加密过程中采用国密SM4算法；
4. 数据摘要算法采用国密SM3算法。

国密版FISCO BCOS节点之间的认证选用国密SSL 1.1的ECDHE_SM4_SM3密码套件进行SSL链接的建立，差异如下表所示：

|              | **OpenSSL**                              | **国密SSL**                              |
| ------------ | ---------------------------------------- | ---------------------------------------- |
| 加密套件     | 采用ECDH、RSA、SHA-256、AES256等密码算法 | 采用国密算法                             |
| PRF算法      | SHA-256                                  | SM3                                      |
| 密钥交换方式 | 传输椭圆曲线参数以及当前报文的签名       | 当前报文的签名和加密证书                 |
| 证书模式     | OpenSSL证书模式                          | 国密双证书模式，分别为加密证书和签名证书 |

国密版与标准版FISCO BCOS在数据结构上的差异如下：

| **算法类型** | **标准版**FISCO BCOS                           | **国密版**FISCO BCOS                               |
| ------------ | ---------------------------------------------- | -------------------------------------------------- |
| 签名         | ECDSA (公钥长度: 512 bits, 私钥长度: 256 bits) | SM2 (公钥长度：512 bits, 私钥长度：256 bits)       |
| 哈希         | SHA3 (哈希串长度: 256 bits)                    | SM3 (哈希串长度: 256 bits)                         |
| 对称加解密   | AES (加密秘钥长度: 256 bits)                   | SM4 (对称密钥长度: 128 bits)                       |
| 交易长度     | 520bits(其中标识符8bits,签名长度512bits)       | 1024bits(128字节，其中公钥512bits,签名长度512bits) |

## 开启国密特性

需要使用国密版FISCO BCOS特性时，有以下几点需要注意：

### 1. 搭建国密版FISCO BCOS区块链

搭建国密版FISCO BCOS的区块链方式主要有2种：

##### (1). 使用build_chain.sh脚本搭建

buildchain.sh脚本的-g为国密编译选项，使用成功后会生成国密版的节点。默认从GitHub下载最新稳定版本可执行程序，操作方式：

```bash
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/`curl -s https://api.github.com/repos/FISCO-BCOS/FISCO-BCOS/releases | grep "\"v2\.[0-9]\.[0-9]\"" | sort -u | tail -n 1 | cut -d \" -f 4`/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/build_chain.sh && chmod u+x build_chain.sh`
```

运行以下命令，搭建四节点的国密版FISCO BCOS联盟链

```bash
./build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545 -g
```

执行成功后，会搭建本地四节点的国密版FISCO BCOS联盟链。

##### (2). 使用企业级部署工具搭建

企业级部署工具generator的-g命令为国密相关操作，用户需要在生成相关证书，下载二进制等操作的过程中附带-g选项，操作方式：

**参考教程**：https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tutorial/enterprise_quick_start.html

```
./generator --download_fisco ./meta -g
./generator --generate_chain_certificate ./dir_chain_ca -g
./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyB -g
...
```

以下操作与教程中类似

```
./generator --build_install_package ./meta/peersB.txt ./nodeA -g
```

### 2. 使用国密版sdk/控制台


国密版sdk和控制台的操作命令与普通版相同，Web3SDK和1.x版本控制台在启用国密特性时需要做如下操作：

- (1). 将applicationContext.xml中的encryptType设置为1；
- (2). 加载私钥时需要加载国密版的私钥；
- (3). 控制台还需要获取国密版jar包。

```eval_rst
.. note::
    java sdk和2.6.0+版本的控制台启用国密算法时不需要额外配置
```

### 3. 开启国密版落盘加密功能

国密版落盘加密需要同时对节点conf/gmnode.key和conf/origin_cert/node.key进行加密，其余操作与普通版相同。

## 总结

FISCO BCOS中的主要算法特性对比如下：

|              | **标准版****FISCO BCOS** | **国密版****FISCO BCOS** |
| ------------ | ------------------------ | ------------------------ |
| SSL链接      | Openssl TLSv1.2协议      | 国密TLSv1.1协议          |
| 签名验证     | ECDSA签名算法            | SM2签名算法              |
| 消息摘要算法 | SHA-256 SHA-3            | SM3消息摘要算法          |
| 落盘加密算法 | AES-256加密算法          | SM4加密算法              |
| 证书模式     | OpenSSL证书模式          | 国密双证书模式           |
| 合约编译器   | 以太坊solidity编译器     | 国密solidity编译器       |

------

### 「用户提问」

**Q**：**王刚+云飞微联网+珠海**：请问solidity编译器也需要用到密码学算法吗？

**A**：**李昊轩**：solidity中的abi会用到哈希，需要底层和合约编译器都使用相同的SM3算法。

**Q**：**陈小军-江南科友-广州**：请问一下国密TLS协议套件是自己实现的吗，还是用了开源的？

**A**：**李昊轩**：国密TLS我们采用的是TASSL开源库。

**Q**：**腾龙(何直群)**：如果使用国密的节点，因为签名算法都换了，是不是 RPC SDK 都会不一样了？

**A**：**李昊轩**：是的， sdk也需要开启国密特性。

**Q**：**KHJ**：目前落盘加密的私钥是如何处理的？

**A**：**meng**：要自己保存，可以参考这里的[说明](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/storage_security.html)