# Android SDK

标签：``Android SDK`` ``移动端``

----

开发者可在 Android 应用中通过 fisco-bcos-android-sdk（以下简称 android-sdk）实现对 FISCO-BCOS 区块链的操作。目前，android-sdk 支持 [FISCO BCOS 2.0+](../../change_log/index.md)。

- [Github 地址](https://github.com/FISCO-BCOS/fisco-bcos-android-sdk.git)
- [Gitee 地址](https://gitee.com/FISCO-BCOS/fisco-bcos-android-sdk.git)

```eval_rst
.. admonition:: **主要特性**

    - 查询区块链数据
    - 部署及调用合约
    - 解析合约出参和交易回执
```

对于**部署及调用合约**，android-sdk 现有的接口能满足开发者的多种需求：

- 使用开发者传入的私钥/随机私钥发送交易
- 发送国密/非国密交易
- 基于合约 Java 类部署及调用合约
- 基于合约 abi 和 binary 部署及调用合约

| 重要事项      | 说明                                                                                     |
| ------------- | ---------------------------------------------------------------------------------------- |
| 服务依赖      | [节点接入代理服务 bcos-node-proxy](../../manual/bcos_node_proxy.html)                    |
| 包含架构      | armeabi-v7a 和 arm64-v8a                                                                 |
| 支持 API 下限 | 21（平台版本 Android 5.0）                                                               |
| 所需权限      | 读权限（用于获取服务端证书）、网络访问权限（用于访问区块链节点接入代理服务）             |
| 依赖大小      | 整体 4M，其中 android-sdk 大小约 1M，sdk 使用的[依赖](./quick_start.html#sdk)总大小约 3M |

```eval_rst
.. toctree::
   :maxdepth: 1

   quick_start.md
   sdkExamples.md
```