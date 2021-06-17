# FISCO BCOS - 底层特性

## 1. FISCO BCOS 2.0版本与之前版本有哪些变化

请 [参考这里](../what_is_new.md)。

## 2. FISCO BCOS 2.0版本交易结构包括哪些字段

请参考[交易结构及其编码描述](../design/protocol_description.md#rlp)

## 3. FISCO BCOS 2.0版本的智能合约与之前版本合约有什么不同，兼容性如何

FISCO BCOS 2.0版本支持最新的Solidity合约，同时增加了precompile合约，具体请 [参考这里](../manual/smart_contract.md)。

## 4. 国密和普通版本的区别有哪些

国密版FISCO BCOS将交易签名验签、p2p网络连接、节点连接、数据落盘加密等底层模块的密码学算法均替换为国密算法。同时在编译版本，证书，落盘加密，solidity编译java，Web3SDK使用国密版本和普通版本都有区别，具体请 [参考这里](../manual/guomi_crypto.md)。

## 5. FISCO BCOS 2.0版本证书介绍

请参考[证书说明文档](manual/certificates.md)

## 6. 群组观察节点和共识节点有什么区别

观察节点能同步群组数据，但不能参与共识。共识节点除了具有观察者权限，还参与共识。

## 7. 是否支持从1.3或1.5升级到2.0版本

不支持。

----------

# FISCO BCOS - 开发工具

## 开发者如何与FISCO BCOS平台交互

FISCO BCOS提供多种开发者与平台交互的方式，参考如下：

- FISCO BCOS 2.0版本提供JSON-RPC接口，具体请 [参考这里](../api.md)。
- FISCO BCOS 2.0版本提供Java SDK帮助开发者快速实现应用，具体请参考[这里](../sdk/java_sdk/index.md)
- FISCO BCOS 2.0版本提供Web3SDK帮助开发者快速实现应用，具体请 [参考这里](../sdk/java_sdk.md)。
- FISCO BCOS 2.0版本提供控制台帮助用户快速了解使用FISCO BCOS，[2.6及其以上版本控制台使用文档参考这里](../console/console_of_java_sdk.md)，[1.x版本控制台使用文档参考这里](../console/console.md)。

----------

# FISCO BCOS - 区块链搭建

## 1. FISCO BCOS 2.0版本如何搭建

FISCO BCOS支持多种搭建方式，常用方式有：

- 开发部署工具 build_chain.sh：适合开发者体验、测试FISCO BCOS联盟链，具体请 [参考这里](../manual/build_chain.md)。
- 运维部署工具 generator：适用于企业用户部署、维护FISCO BCOS联盟链，具体请 [参考这里](../enterprise_tools/index.md)。

## 2. 源码编译慢

- **case1: 先前没有编译过源码**

修改`/etc/hosts`文件，添加如下内容可加速依赖包的下载：

```
140.82.113.4    github.com
185.199.108.153 assets-cdn.github.com
185.199.109.153 assets-cdn.github.com
185.199.110.153 assets-cdn.github.com
185.199.111.153 assets-cdn.github.com
199.232.69.194 github.global.ssl.fastly.net
151.101.108.133 github.map.fastly.net
151.101.108.133 raw.githubusercontent.com

```

- **case2: 以前编译过源码**

若先前有一套编译完成的环境，可从原先环境的`deps/src`目录下拷贝已经下载好的依赖包到当前正在编译项目的`deps/src`目录下。

----------


# FISCO BCOS - 群组配置项说明

## 1. 配置项分类

FISCO BCOS配置项包括两大类，即：

- **节点配置**: 位于节点目录的`config.ini`文件中，主要包括节点的P2P、RPC和Channel的连接配置，黑白名单配置，证书配置，落盘加密配置以及日志配置，详细可参考[这里](../manual/configuration.html#config-ini)。

- **群组配置**: 包括**群组不可变配置项**和**群组可变配置项**，均位于节点目录的`conf`子文件夹下

## 2. 群组配置说明

- **群组不可变配置项**: 位于节点目录的`conf/group.${groupId}.genesis`(其中`${groupId}`表示群组ID)文件中，主要包括系统初始共识节点列表、区块内可打包的最大交易数、Gas上限、群组ID等配置，**该配置文件不可修改**，但可使用控制台发交易修改相关配置项，具体可参考[addSealer](../manual/console_of_java_sdk.html#addsealer), [addObserver](../manual/console_of_java_sdk.html#addobserver), [removeNode](../manual/console_of_java_sdk.html#removenode)以及[setSystemConfigByKey](../manual/console_of_java_sdk.html#setsystemconfigbykey).

- **群组可变配置项**: 位于节点目录的`conf/group.${groupId}.ini`(其中`${groupId}`表示群组ID)文件中，主要包括交易池、存储类型等相关配置，**修改该配置文件后重启生效**。


## 3. 群组配置查询

可通过控制台查询群组配置，具体包括：

- **获取共识节点列表**: 控制台执行`getSealerList`命令可获取共识节点列表，具体参考[这里](../manual/console_of_java_sdk.html#getsealerlist).
- **获取观察节点列表**: 控制台执行`getObserverList`命令可获取共识节点列表，具体参考[这里](../manual/console_of_java_sdk.html#getobserverlist).
- **获取系统配置**:  控制台执行`getSystemConfigByKey`命令可获取共识节点列表，具体参考[这里](../console_of_java_sdk.html#getsystemconfigbykey).


----------

# FISCO BCOS - 动态群组

## 节点启动失败

- **问题描述**: 动态群组场景下，节点启动失败，出现`invalid group status`的错误提示
- **解决方案**: 由于文件系统错误等原因导致节点在本地记录的群组状态不合法，可检查群组数据目录(群组数据目录通常为`data/group${groupId}/`，其中`groupId`是群组编号)下的`.group_status`文件，将其状态修改为`STOPPED`或`DELETED`或`RUNNING`.


----------

# FISCO BCOS - CNS

## 1. 合约信息注册到CNS

CNS的详细设计可参考[这里](../design/features/cns_contract_name_service.md)，可通过控制台注册合约信息到CNS系统表，也可使用`Java SDK`的`CnsService`相关接口注册CNS，具体如下：

- **通过控制台注册合约信息**: 调用控制台的[deployByCNS命令](../manual/console_of_java_sdk.html#deploybycns)，部署合约时将合约名和合约版本号到合约信息的映射注册到CNS系统表中
- **通过Java SDK接口注册合约信息**: 参考Java SDK CnsService的[registerCNS](../../javadoc/org/fisco/bcos/sdk/contract/precompiled/cns/CnsService.html)接口，注册合约信息到CNS系统表中

## 2. 通过CNS查询合约信息

控制台和Java SDK均提供了通过CNS查询合约信息的方式，具体如下：

- **通过控制台查询合约信息**：调用控制台的[queryCNS](../manual/console_of_java_sdk.html#querycns)接口，通过合约名称和合约版本号查询合约信息
- **通过Java SDK接口查询合约信息**： 调用[Java SDK CnsService](../../javadoc/org/fisco/bcos/sdk/contract/precompiled/cns/CnsService.html)的`selectByName`、`selectByNameAndVersion`和`getContractAddress`可从CNS系统表中查询注册的合约信息
