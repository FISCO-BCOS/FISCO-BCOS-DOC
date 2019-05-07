# 版本及兼容

[FISCO BCOS Release 列表](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)

## FISCO BCOS 1 系列

请参考： [FISCO BCOS 1.0 Release](<https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v1.0.0>)

## [FISCO BCOS 2.0 rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v2.0.0-rc1)

### 变更描述

**架构**

- Add

1. 新增群组架构，各群组独立共识和存储，在较低运维成本基础上实现系统吞吐能力横向扩展。
2. 新增分布式数据存储，支持节点将数据存储在远端分布式系统中，实现计算与数据隔离、高速扩容、数据安全等级提升等目标。
3. 新增对预编译合约的支持，底层基于C++实现预编译合约框架，兼容solidity调用方式，提升智能合约执行性能。
4. 引入evmc扩展框架，支持扩展不同虚拟机引擎。

- Update

1. 升级重塑P2P、共识、同步、交易执行、交易池、区块管理模块。

**协议**

- Add

1. 实现一套CRUD基本数据访问接口规范合约，基于CRUD接口编写业务合约，实现传统面向SQL方式的业务开发流程。
2. 支持交易上链异步通知、区块上链异步通知以及自定义的AMOP消息通知等机制。

- Update

1. 升级以太坊虚拟机版本，支持Solidity 0.5.2版本。
2. 升级RPC模块。

**安全**

- Update

1. 升级落盘加密，提供密钥管理服务。开启落盘加密功能时，依赖KeyManager服务进行密钥管理。
2. 升级准入机制，通过引入网络准入机制和群组准入机制，在不同维度对链和数据访问进行安全控制。
3. 升级权限控制体系，基于表进行访问权限的设计。

**其他**

- Add

1. 提供入门级的搭链工具。
2. 提供模块化的单元测试和端对端集成测试，支持自动化持续集成和持续部署。

### 兼容性说明

|           | 兼容版本                                            | 说明                                                         |
| --------- | --------------------------------------------------- | ------------------------------------------------------------ |
| 节点      | 向下兼容 FISCO BCOS 2.0.0 rc1，不兼容FISCO BCOS 1.x | 2.0版本与1.x版本完全不兼容。2.0版本不能直接跑在1.0的数据上，也不能和1.0的节点相互通信。 |
| 控制台    | 1.0.0+                                              |                                                              |
| Web3SDK   | 2.0.0-rc1+                                          |                                                              |
| generator | 1.0.0-rc1                                           |                                                              |
| 浏览器    | 2.0.0-rc1+                                          |                                                              |
| Solidity  | 最高支持 solidity 0.5.2                             |                                                              |

### 升级说明

* 从1.0升级：1.0无法直接将数据迁移到2.0上来，只能依靠外部的方式，将历史的交易重放到2.0的新链上。搭建2.0的新链，请参考[安装](./installation.md)部分。

## [FISCO BCOS 2.0 rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v2.0.0-rc2)

### 变更描述

**New Feature**

* 并行计算模型：可并行合约开发框架、交易并行执行引擎（PTE）
* 分布式存储：AMDB、SQLStorage

**Update**

* 优化了区块打包交易数的逻辑，根据执行时间动态的调整区块打包交易数
* 优化了区块同步的流程，让区块同步更快
* 并行优化了将交易的编解码、交易的验签和落盘的编码
* 优化了交易执行返回码的逻辑，让返回码更准确
* 升级了存储模块，支持并发读写

**Add**

* 加入[网络数据包压缩](<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/features/network_compress.html>)
* 加入[兼容性配置](<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/configuration.html#id7>)
* 交易编码中加入chainID和groupID
* 交易中加入二进制缓存
* 创世块中加入timestamp信息
* 增加了一些precompile的demo
* 支持用Docker搭链

**Fix**

* RPC中处理参数时asInt异常造成程序退出的bug
* 交易执行Out of gas时交易一直在交易池中不被处理的bug
* 不同组间可以用相同的交易二进制重放的bug
* insert操作造成的性能衰减问题
* 一些稳定性修复

**Detele**

* 删除不必要的日志
* 删除不必要的重复操作

### 兼容性说明

|           | 兼容版本                                            | 说明                                                         |
| --------- | --------------------------------------------------- | ------------------------------------------------------------ |
| 节点      | 向下兼容 FISCO BCOS 2.0.0 rc1，不兼容FISCO BCOS 1.x | 此版本可直接跑在v2.0.0-rc1的配置和数据上，但不会有此版本的新特性，仅仅相当于稳定性提升。若需要用此版本的新特性，需重新搭链。 |
| 控制台    | 1.0.2+                                              |                                                              |
| Web3SDK   | 2.0.0-rc2+                                          |                                                              |
| generator | 1.0.0-rc2                                           |                                                              |
| 浏览器    | 2.0.0-rc2+                                          |                                                              |
| Solidity  | 最高支持 solidity 0.5.2                             |                                                              |
| AMDB      | 2.0.0+                                              |                                                              |

### 升级说明

**从1.0升级**

1.0无法直接将数据迁移到2.0上来，只能依靠外部的方式，将历史的交易重放到2.0的新链上。搭建2.0的新链，请参考[安装](./installation.md)部分。

**从2.0 rc1升级**

* 兼容模式的升级：rc2提供了兼容模式。可直接将rc1的节点二进制替换成rc2的节点二进制完成升级。升级后能修复rc1中的bug，但不会启用rc2的新特性（并行计算模型，分布式存储）。升级后，不可再将rc2的二进制替换回rc1。
* 使用新特性的升级：先参考[安装](./installation.md)部分搭建新链，再用外部的方式将交易重放到2.0 rc2的新链上。

## 相关操作

**查看节点版本**

``` shell
cd nodes/127.0.0.1/
./fisco-bcos --version
```

可看到版本号

``` shell
FISCO-BCOS Version : 2.0.0-rc2
Build Time         : 20190425 14:34:50
Build Type         : Linux
Git Branch         : master
Git Commit Hash    : 4ddc1f61605c3b1450d0d348ec094f5668288eb5
```

**查看节点数据的版本**

通过查看节点的配置文件，可看到节点生成的文件版本

``` shell
grep "supported_version" nodes/127.0.0.1/node0/config.ini
```

可看到节点数据的版本（若为空，表示默认为2.0.0-rc1版本）

``` ini
supported_version=2.0.0-rc2
```

