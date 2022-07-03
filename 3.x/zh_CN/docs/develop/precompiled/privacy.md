# 合约隐私保护

标签：``隐私合约`` ``隐私保护`` ``合约开发``  ``环签名``

----
隐私保护是联盟链的一大技术挑战。为了保护链上数据、保障联盟成员隐私，并且保证监管的有效性，FISCO BCOS以[预编译合约](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/precompiled/index.html)的形式集成了群/环签名验证功能，提供了多种隐私保护手段。

文档对群/环签名算法以及相关应用场景进行了简单介绍并对调用方式提供了参考。

## 群/环签名

### 算法简介

**群签名**

群签名(Group Signature)是一种能保护签名者身份的具有相对匿名性的数字签名方案，用户可以代替自己所在的群对消息进行签名，而验证者可以验证该签名是否有效，但是并不知道签名属于哪一个群成员。同时，用户无法滥用这种匿名行为，因为群管理员可以通过群主私钥打开签名，暴露签名的归属信息。群签名的特性包括：

- 匿名性：群成员用群参数产生签名，其他人仅可验证签名的有效性，并通过签名知道签名者所属群组，却无法获取签名者身份信息；
- 不可伪造性：只有群成员才能生成有效可被验证的群签名；
- 不可链接性：给定两个签名，无法判断它们是否来自同一个签名者；
- 可追踪性：在监管介入的场景中，群主可通过签名获取签名者身份。

**环签名**

环签名(Ring Signature)是一种特殊的群签名方案，但具备完全匿名性，即不存在管理员这个角色，所有成员可主动加入环，且签名无法被打开。环签名的特性包括：

- 不可伪造性：环中其他成员不能伪造真实签名者签名；
- 完全匿名性：没有群主，只有环成员，其他人仅可验证环签名的有效性，但没有人可以获取签名者身份信息。

### 功能组件

FISCO BCOS群/环签名模块提供的功能组件包括：

- 群/环[签名库](https://github.com/FISCO-BCOS/group-signature-lib)，提供完整的群/环签名算法c++接口

- 群/环签名预编译合约，供智能合约调用，提供群/环签名验证接口。

### 使用方式

有签名者身份隐匿需求的业务可借助本模块实现相关功能。签名者通过调用群/环签名库完成对数据的签名，然后将签名上链，业务合约通过调用群/环签名预编译合约完成签名的验证，并将验证结果返还回业务层。如果是群签名，那么监管方还能打开指定签名数据，获得签名者身份。具体流程如下图所示：

![](../../../images/privacy/group_sig.jpg)

### 应用场景

群/环签名由于其天然的匿名性，在需要对参与者身份进行隐匿的场景中有广泛的应用前景，例如匿名投票、匿名竞拍、匿名拍卖等等，甚至在区块链UTXO模型中可用于实现匿名转账。同时，由于群签名具备可追踪性，可以用于需要监管介入的场景，监管方作为群主或者委托群主揭露签名者身份。

### 开发示例

FISCO BCOS专门为用户提供了群/环签名开发示例，包括：

- 群/环签名服务端: 提供完整的群/环签名RPC服务。[GitHub源码](https://github.com/FISCO-BCOS/group-signature-server)、[Gitee源码](https://gitee.com/FISCO-BCOS/group-signature-server)

- 群/环签名客户端: 调用RPC服务对数据进行签名，并提供签名上链以及链上验证等功能。[GitHub源码](https://github.com/FISCO-BCOS/sig-service-client)、[Gitee源码](https://gitee.com/FISCO-BCOS/sig-service-client)

示例框架如下图所示，具体操作方法请参阅[客户端指南 Github链接](https://github.com/FISCO-BCOS/sig-service-client)或[客户端指南 Gitee链接](https://gitee.com/FISCO-BCOS/sig-service-client)。

![](../../../images/privacy/demo.jpg)

## 启用方法

FISCO BCOS隐私保护模块是通过预编译合约实现的，默认被启用。使用源码编译请参考[这里](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/tutorial/compile_binary.html).

### 搭建联盟链

假设当前位于`FISCO-BCOS/build`目录下，则使用下面的指令搭建本机4节点的链指令如下，更多选项[参考这里](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/tutorial/air/build_chain.html)。

```bash
bash ../tools/BcosAirBuilder/build_chain.sh -l 127.0.0.1:4 -e bin/fisco-bcos-air/fisco-bcos
```


## 预编译合约接口

隐私模块的代码和用户开发的预编译合约都位于`FISCO-BCOS/bcos-excutor/src/precompiled/extension`目录，因此隐私模块的调用方式和用户开发的预编译合约[调用流程](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/precompiled/add_precompiled_impl.html)相同，不过有两点需要注意：

1.已为隐私模块的预编译合约分配了地址，无需另行注册。隐私模块实现的预编译合约列表以及地址分配如下：

源码可参考链接：[GitHub链接](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/bcos-executor/src/precompiled/extension)、[Gitee链接](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/master/bcos-executor/src/precompiled/extension)

| 地址     | 功能   | 源码                      |
| ------ | ---- | ----------------------- |
| 0x5004 | 群签名  | GroupSigPrecompiled.cpp |
| 0x5005 | 环签名  | RingSigPrecompiled.cpp  |

2.需要通过`solidity`合约方式声明隐私模块预编译合约的接口，合约文件需保存在控制台合约目录`console/contracts/solidity`中，各个隐私功能的合约接口如下，可直接复制使用:

- 群签名

  ```solidity
  // GroupSigPrecompiled.sol
  pragma solidity ^0.4.24;
  contract GroupSigPrecompiled{
  		function groupSigVerify(string signature, string message, string gpkInfo, string paramInfo) public constant returns(bool);
  }
  ```

- 环签名

  ```solidity
  // RingSigPrecompiled.sol
  pragma solidity ^0.4.24;
  contract RingSigPrecompiled{
       function ringSigVerify(string signature, string message, string paramInfo) public constant returns(bool);
  }
  ```


