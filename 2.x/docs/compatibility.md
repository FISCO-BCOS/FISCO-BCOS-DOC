# FISCO-BCOS 版本说明

标签：``FISCO BCOS v2.x`` ``FISCO BCOS v3.x`` ``兼容性``

---

本节内容对FISCO-BCOS常见的一些版本问题进行说明，包括: FISCO-BCOS v2.x v3.x对比、JDK版本、项目兼容性。

## 1. `FISCO-BCOS v2.x` VS `v3.x`

`FISCO-BCOS`目前主要存在`2.x`[[文档链接](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)]和`3.x`[[文档链接](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/)]两个大版本，用户可以根据不同的场景选择:

- v2.x和v3.x版本之间不兼容，后续会同时保持维护、迭代、更新。<br>
- v2.x是目前的稳定版本，已经经过多个机构、多个应用，长时间在生产环境中的实践检验，具备金融级的高性能、高可用性及高安全性。该版本会持续进行维护。**用户当前有生产级的使用需求，可以直接使用v2.x即可**。<br>
- v3.x为当前FISCO-BCOS最新的版本，采用微服务的架构，已发布正式版，欢迎体验。

## 2. JDK版本说明

FISCO-BCOS支持Java版本的SDK，并且大量的示例、周边项目都是基于JavaSDK，使用这些工程之前，推荐用户使用官方已经验证的`JDK`版本:

|       JDK      |  推荐版本   | 下载链接 |
| ---------------| -----------|-------- |
| OracleJDK     | 1.8.0_141、1.8.0_202、11.0.2、14.0.2、15.0.2  | [Oracle官网下载](https://www.oracle.com/java/technologies/downloads/archive/)  、[国内镜像下载](http://www.codebaoku.com/jdk/jdk-oracle.html)
| OpenJDK       | 11.0.2、14.0.2、15.0.2   |  [国内镜像下载](http://www.codebaoku.com/jdk/jdk-openjdk.html)

## 3. 项目版本兼容性

FISCO-BCOS v2.x和v3.x之间不兼容，不同的底层区块链，需要使用不同版本的周边配套项目。

用户可以使用`./fisco-bcos --version`检查当前部署的区块链版本:

```shell
$ ./fisco-bcos --version
FISCO BCOS Version : 3.0.0 # 版本号
Build Time         : 20220830 05:44:17
Build Type         : Linux/g++/Release
Git Branch         : HEAD
Git Commit         : 07f3ca4fa727300290113c8ba339db27b6516864
```

### 3.1. FISCO-BCOS 2.x

|       项目      | 功能简介|  最新版本   | 文档 |    github | gitee |  备注   |
| ---------------| -----------|-------- |----------|--------|---------|---------
| FISCO-BCOS     | 区块链底层平台  |   v2.9.1   | [文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest)   |    [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0)      |   [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0)     |
| Solidity       | Solidity智能合约 |   v0.6.10   |  - [solidity文档](https://docs.soliditylang.org/en/v0.6.10) </br> - [智能合约开发](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/app_dev/index.html) |          |        | **最高支持solidity v0.6.10**
| JavaSDK |   Java语言SDK |   v2.9.1   | [文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk/index.html)    |    [github](https://github.com/FISCO-BCOS/java-sdk/tree/master-2.0)      |   [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/master-2.0)     |
| GoSDK | Go语言SDK |   v1.0.0   | [文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/go_sdk/index.html)   |   [github](https://github.com/FISCO-BCOS/go-sdk)       |    [gitee](https://gitee.com/FISCO-BCOS/go-sdk)    |
| PythonSDK | Python语言SDK |   v0.9.2    | [文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/python_sdk/index.html)   |     [github](https://github.com/FISCO-BCOS/python-sdk)     |    [gitee](https://gitee.com/FISCO-BCOS/python-sdk)    |
| 控制台(console) | 交互式命令行工具 |   v2.9.1   | [文档](<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/index.html>)   |      [github](https://github.com/FISCO-BCOS/console/tree/master-2.0)    |   [gitee](https://gitee.com/FISCO-BCOS/console/tree/master-2.0)     |
| java-sdk-demo | Java压测工具 |   v2.9.0   | [文档](<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tutorial/stress_testing.html>)   |      [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/main-2.0)    |   [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/main-2.0)     |
| spring-boot-starter | Gradle SpringBoot 应用示例 |   v2.9.0 | [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/master-2.0)  |  [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/master-2.0)  |     [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/master-2.0)     |       |
| spring-boot-crud |  Maven SpringBoot 应用示例 | v2.9.0  | [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/master-2.0) |  [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/master-2.0) |    [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/master-2.0)  |
| generator | 企业区块链部署工具 |   v1.9.0   | [文档](<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/enterprise_tools/index.html>)   |      [github](https://github.com/FISCO-BCOS/generator)    |   [gitee](https://gitee.com/FISCO-BCOS/generator)     |
| WeBASE | 区块链中间件平台 |   v1.5.4   | [文档](https://webasedoc.readthedocs.io/zh_CN/latest)   |   [github](https://github.com/WeBankBlockchain/WeBASE)         |  [gitee](https://gitee.com/WeBank/WeBASE)    |
| WeCross | 跨链协作平台 |   v1.2.1  | [文档](https://wecross.readthedocs.io/zh_CN/latest)   |    [github](https://github.com/WeBankBlockchain/WeCross)      |    [gitee](https://gitee.com/WeBank/WeCross)    |
| WeIdentity | 分布式身份解决方案 |   v1.8.5-rc1   | [文档](https://weidentity.readthedocs.io/zh_CN/latest/)   |   [github](https://github.com/WeBankBlockchain/WeIdentity)         |  [gitee](https://gitee.com/WeBank/WeIdentity)    |
| WeBankBlockchain-Data-Export |  数据导出组件 |   v1.7.7   |  [文档](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Export/index.html)  |    [github](https://github.com/WeBankBlockchain/Data-Export)      |    [gitee](https://gitee.com/WeBankBlockchain/Data-Export)  |
| WeBankBlockchain-Data-Stash |  数据仓库组件 |   v1.2.3   |  [文档](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Stash/index.html)  |    [github](https://github.com/WeBankBlockchain/Data-Stash)      |    [gitee](https://gitee.com/WeBankBlockchain/Data-Stash)  |
| WeBankBlockchain-Data-Reconcile |  数据对账组件 |   v1.0.0   |  [文档](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Reconcile/index.html)  |    [github](https://github.com/WeBankBlockchain/Data-Reconcile)      |    [gitee](https://gitee.com/WeBankBlockchain/Data-Reconcile)  |
| WeBankBlockchain-SmartDev-Scaffold |  应用开发脚手架 |   v1.1.1   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Scaffold/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-Scaffold)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Scaffold)  |
| WeBankBlockchain-SmartDev-SCGP |  智能合约编译插件 |   v1.0.1   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-SCGP/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-SCGP)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-SCGP)  |
| WeBankBlockchain-SmartDev-Contract |  智能合约库 |   v1.0.0   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-Contract)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Contract)  |
| WeBankBlockchain-Governance-Cert |  证书管理组件 |   v1.0.1   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Cert)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Cert)  |
| WeBankBlockchain-Governance-Key |  私钥管理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Key)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Key)  |
| WeBankBlockchain-Governance-Authority |  权限治理组件 |   v1.0.0   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Auth/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Authority)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Authority)  |
| WeBankBlockchain-Governance-Account |  账户治理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Acct/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Account)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Account)  |

### 3.2. FISCO-BCOS 3.x

FISCO-BCOS 3.0.0 及之后的版本之间相互兼容。FISCO-BCOS 3.0.0-rc的各个版本之间不保证兼容性，用户根据不同的版本，需要使用不同版本的周边配套项目。

目前FISCO-BCOS 3.x包含以下RC版本：

- [v3.0.0-rc1](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html#fisco-bcos-v3-0-0-rc1)
- [v3.0.0-rc2](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html#fisco-bcos-v3-0-0-rc2)
- [v3.0.0-rc3](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html#fisco-bcos-v3-0-0-rc3)
- [v3.0.0-rc4](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html#fisco-bcos-v3-0-0-rc4)
- [v3.0.0](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html#fisco-bcos-v3-0-0)
- [v3.1.0](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html#fisco-bcos-v3-1-0)

#### 3.2.1. FISCO-BCOS v3.0.0-rc1

|       项目      | 功能简介| 版本   | 文档 |    github | gitee |  备注   |
| ---------------| -----------|-------- |----------|--------|---------|-------
| FISCO-BCOS     |  区块链底层平台 | v3.0.0-rc1   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc1/)   |    [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc1)      |   [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc1)     |
| Solidity       |   Solidity智能合约 | v0.8.11   |  [文档](https://docs.soliditylang.org/en/v0.8.11)   |          |        | **最高支持solidity v0.8.11**
| JavaSDK |   Java语言SDK | v3.0.0-rc1   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc1/docs/develop/sdk/java_sdk/index.html)    |    [github](https://github.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc1)      |   [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc1)    |
| GoSDK |   Go语言SDK| 暂不支持</br>后续版本规划  |   |       |   |
| PythonSDK |   Python语言SDK | 暂不支持</br>后续版本规划   |    |         |       |
| 控制台(console) | 交互式命令行工具 |   v3.0.0-rc1   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc1/docs/develop/console/index.html)   |      [github](https://github.com/FISCO-BCOS/console/tree/v3.0.0-rc1)    |   [gitee](https://gitee.com/FISCO-BCOS/console/tree/v3.0.0-rc1)   |
| java-sdk-demo |  Java压测工具 | v3.0.0-rc1   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc1/docs/develop/stress_testing.html)  |    [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0-rc1)      |     [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/v3.0.0-rc1) |
| spring-boot-starter | Gradle SpringBoot 应用示例 |   v3.0.0-rc1  |  [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc1#spring-boot-starter)  |     [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc1)     |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc1)    |
| spring-boot-crud |  Maven SpringBoot 应用示例 | v3.0.0-rc1   |  [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc1#spring-boot-crud) |  [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc1)    |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc1)     |
| generator |  部署运维工具 | 不支持  |   |    |    |
| WeBASE | 区块链中间件平台 | lab-rc1  |    [文档](https://webasedoc.readthedocs.io/zh_CN/lab-rc1/)     |  [github](https://github.com/WeBankBlockchain/WeBASE/tree/lab-rc1)    |  [gitee](https://gitee.com/WeBank/WeBASE/tree/lab-rc1)  |
| WeCross|  跨链协作平台 | 暂不支持</br>后续版本规划  |    |         |     |
| WeIdentity | 分布式身份解决方案 |  不支持  |    |        |     |
| WeBankBlockchain-Data-Export| 数据导出组件 | 不支持   |   |          |        |
| WeBankBlockchain-Data-Stash |  数据仓库组件 |   不支持    |   |          |        |
| WeBankBlockchain-Data-Reconcile |  数据对账组件 |   不支持    |   |          |        |
| WeBankBlockchain-SmartDev-Scaffold |  应用开发脚手架 | 不支持    |   |          |        |
| WeBankBlockchain-SmartDev-SCGP |  智能合约编译插件 | **适配中**    |   |          |        |
| WeBankBlockchain-SmartDev-Contract |  智能合约库 |   v1.0.0   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-Contract)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Contract)  |
| WeBankBlockchain-Governance-Cert |  证书管理组件 |   v1.0.1   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Cert)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Cert)  |
| WeBankBlockchain-Governance-Key |  私钥管理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Key)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Key)  |
| WeBankBlockchain-Governance-Authority |  权限治理组件 |   v1.0.0   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Auth/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Authority)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Authority)  | 合约部分支持，sdk部分不支持
| WeBankBlockchain-Governance-Account |  账户治理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Acct/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Account)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Account)  | 合约部分支持，sdk部分不支持

#### 3.2.2. FISCO-BCOS v3.0.0-rc2

|       项目      | 功能简介| 版本   | 文档 |    github | gitee |  备注   |
| ---------------| -----------|-------- |----------|--------|---------|-------
| FISCO-BCOS     |  区块链底层平台 | v3.0.0-rc2   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc2/)   |    [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc2)      |   [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc2)     |
| Solidity       |   Solidity智能合约 | v0.8.11   |  [文档](https://docs.soliditylang.org/en/v0.8.11)   |          |        | **最高支持solidity v0.8.11**
| JavaSDK |   Java语言SDK | v3.0.0-rc2   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc2/docs/develop/sdk/java_sdk/index.html)    |    [github](https://github.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc2)      |   [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc2)    |
| GoSDK |   Go语言SDK| 暂不支持</br>后续版本规划  |   |       |   |
| PythonSDK |   Python语言SDK | 暂不支持</br>后续版本规划   |    |         |       |
| 控制台(console) | 交互式命令行工具 |   v3.0.0-rc2   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc2/docs/develop/console/index.html)   |      [github](https://github.com/FISCO-BCOS/console/tree/v3.0.0-rc2)    |   [gitee](https://gitee.com/FISCO-BCOS/console/tree/v3.0.0-rc2)   |
| java-sdk-demo |  Java压测工具 | v3.0.0-rc2   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc2/docs/develop/stress_testing.html)  |    [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0-rc2)      |     [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/v3.0.0-rc2) |
| spring-boot-starter | Gradle SpringBoot 应用示例 |   v3.0.0-rc2   |  [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc2#spring-boot-starter)  |     [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc2)     |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc2)    |
| spring-boot-crud |  Maven SpringBoot 应用示例 | v3.0.0-rc2   |  [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc2#spring-boot-crud) |  [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc2)    |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc2)     |
| generator |  企业区块链部署工具 | 不支持  |   |    |    |
| WeBASE | 区块链中间件平台 | lab-rc2  |    [文档](https://webasedoc.readthedocs.io/zh_CN/lab/)     |  [github](https://github.com/WeBankBlockchain/WeBASE/tree/lab-rc2)    |  [gitee](https://gitee.com/WeBank/WeBASE/tree/lab-rc2)  |
| WeCross|  跨链协作平台 | 暂不支持</br>后续版本规划  |    |         |     |
| WeIdentity | 分布式身份解决方案 |  不支持  |    |        |     |
| WeBankBlockchain-Data-Export| 数据导出组件 | 不支持   |   |          |        |
| WeBankBlockchain-Data-Stash |  数据仓库组件 |   不支持    |   |          |        |
| WeBankBlockchain-Data-Reconcile |  数据对账组件 |   不支持    |   |          |        |
| WeBankBlockchain-SmartDev-Scaffold |  应用开发脚手架 | 不支持    |   |          |        |
| WeBankBlockchain-SmartDev-SCGP |  智能合约编译插件 | **适配中**    |   |          |        |
| WeBankBlockchain-SmartDev-Contract |  智能合约库 |   v1.0.0   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-Contract)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Contract)  |
| WeBankBlockchain-Governance-Cert |  证书管理组件 |   v1.0.1   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Cert)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Cert)  |
| WeBankBlockchain-Governance-Key |  私钥管理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Key)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Key)  |
| WeBankBlockchain-Governance-Authority |  权限治理组件 |   v1.0.0   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Auth/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Authority)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Authority)  | 合约部分支持，sdk部分不支持
| WeBankBlockchain-Governance-Account |  账户治理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Acct/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Account)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Account)  | 合约部分支持，sdk部分不支持

#### 3.2.3. FISCO-BCOS v3.0.0-rc3

|       项目      | 功能简介| 版本   | 文档 |    github | gitee |  备注   |
| ---------------| -----------|-------- |----------|--------|---------|-------
| FISCO-BCOS     |  区块链底层平台 | v3.0.0-rc3   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc3/)   |    [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc3)      |   [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc3)     |
| Solidity       |   solidity智能合约 | v0.8.11   |  [文档](https://docs.soliditylang.org/en/v0.8.11)   |          |        | **最高支持solidity v0.8.11**
| JavaSDK |   Java语言SDK | v3.0.0-rc3   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc3/docs/develop/sdk/java_sdk/index.html)    |    [github](https://github.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc3)      |   [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc3)    |
| GoSDK |   Go语言SDK| 暂不支持</br>后续版本规划  |   |       |   |
| PythonSDK |   Python语言SDK | 暂不支持</br>后续版本规划   |    |         |       |
| 控制台(console) | 交互式命令行工具 |   v3.0.0-rc3   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc3/docs/develop/console/index.html)   |      [github](https://github.com/FISCO-BCOS/console/tree/v3.0.0-rc3)    |   [gitee](https://gitee.com/FISCO-BCOS/console/tree/v3.0.0-rc3)   |
| java-sdk-demo |  Java压测工具 | v3.0.0-rc3   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/v3.0.0-rc3/docs/develop/stress_testing.html)  |    [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0-rc3)      |     [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0-rc3) |
| spring-boot-starter | Gradle SpringBoot 应用示例 |   v3.0.0-rc3   |  [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc3#spring-boot-starter)  |     [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc3)     |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc3)    |
| spring-boot-crud |  Maven SpringBoot 应用示例 | v3.0.0-rc3   |  [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc3#spring-boot-crud) |  [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc3)    |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc3)     |
| generator |  企业区块链部署工具 | 不支持  |   |    |    |
| WeBASE | 区块链中间件平台 | 不支持  |         |      |    |
| WeCross|  跨链协作平台 | 暂不支持</br>后续版本规划  |    |         |     |
| WeIdentity | 分布式身份解决方案 |  不支持  |    |        |     |
| WeBankBlockchain-Data-Export| 数据导出组件 |  v3.0.0  | [文档](https://data-doc.readthedocs.io/zh_CN/data_export_3.0/docs/WeBankBlockchain-Data-Export/index.html)  |    [github](https://github.com/WeBankBlockchain/Data-Export/tree/3.0.0)       |   [gitee](https://gitee.com/WeBankBlockchain/Data-Export/tree/2.0.0/)     |
| WeBankBlockchain-Data-Stash |  数据仓库组件 |   不支持    |   |          |        |
| WeBankBlockchain-Data-Reconcile |  数据对账组件 |   暂不支持</br>后续版本规划    |   |          |        |
| WeBankBlockchain-SmartDev-Scaffold |  应用开发脚手架 | 暂不支持</br>后续版本规划    |   |          |        |
| WeBankBlockchain-SmartDev-SCGP |  智能合约编译插件 | **适配中**    |   |          |        |
| WeBankBlockchain-SmartDev-Contract |  智能合约库 |   v1.0.0   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-Contract)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Contract)  |
| WeBankBlockchain-Governance-Cert |  证书管理组件 |   v1.0.1   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Cert)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Cert)  |
| WeBankBlockchain-Governance-Key |  私钥管理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Key)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Key)  |
| WeBankBlockchain-Governance-Authority |  权限治理组件 |   v1.0.0   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Auth/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Authority)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Authority)  | 合约部分支持，sdk部分不支持
| WeBankBlockchain-Governance-Account |  账户治理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Acct/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Account)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Account)  | 合约部分支持，sdk部分不支持

#### 3.2.4. FISCO-BCOS v3.0.0-rc4

|       项目      | 功能简介| 版本   | 文档 |    github | gitee |  备注   |
| ---------------| -----------|-------- |----------|--------|---------|-------
| FISCO-BCOS     |  区块链底层平台 | v3.0.0-rc4   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/)   |    [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc4)      |   [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0-rc4)     |
| Solidity       |   solidity智能合约 | v0.8.11   |  [文档](https://docs.soliditylang.org/en/v0.8.11)   |          |        | **最高支持solidity v0.8.11**
| JavaSDK |   Java语言SDK | v3.0.0-rc4   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)    |    [github](https://github.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc4)      |   [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/v3.0.0-rc4)    |
| GoSDK |   Go语言SDK| 暂不支持</br>后续版本规划  |   |       |   |
| PythonSDK |   Python语言SDK | 暂不支持</br>后续版本规划   |    |         |       |
| 控制台(console) | 交互式命令行工具 |   v3.0.0-rc4   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/console/index.html)   |      [github](https://github.com/FISCO-BCOS/console/tree/v3.0.0-rc4)    |   [gitee](https://gitee.com/FISCO-BCOS/console/tree/v3.0.0-rc4)   |
| java-sdk-demo |  Java压测工具 | v3.0.0-rc4   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/stress_testing.html)  |    [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0-rc4)      |     [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0-rc4) |
| spring-boot-starter | Gradle SpringBoot 应用示例 |   v3.0.0-rc4   |  [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc4#spring-boot-starter)  |     [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc4)     |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc4)    |
| spring-boot-crud |  Maven SpringBoot 应用示例 | v3.0.0-rc4   |  [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc4#spring-boot-crud) |  [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc4)    |   [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc4)     |
| generator |  企业区块链部署工具 | 不支持  |   |    |    |
| WeBASE | 区块链中间件平台 | 暂不支持  |         |      |    |
| WeCross|  跨链协作平台 | 暂不支持</br>后续版本规划  |    |         |     |
| WeIdentity | 分布式身份解决方案 |  v1.8.5-rc1  |    |        |     |
| WeBankBlockchain-Data-Export| 数据导出组件 |  暂不支持</br>后续版本规划  |   |        |     |
| WeBankBlockchain-Data-Stash |  数据仓库组件 |   不支持    |   |          |        |
| WeBankBlockchain-Data-Reconcile |  数据对账组件 |   暂不支持</br>后续版本规划    |   |          |        |
| WeBankBlockchain-SmartDev-Scaffold |  应用开发脚手架 | 暂不支持</br>后续版本规划    |   |          |        |
| WeBankBlockchain-SmartDev-SCGP |  智能合约编译插件 | **适配中**    |   |          |        |
| WeBankBlockchain-SmartDev-Contract |  智能合约库 |   暂不支持</br>后续版本规划   |    |    |   |
| WeBankBlockchain-Governance-Cert |  证书管理组件 |   暂不支持</br>后续版本规划   |    |    |     |
| WeBankBlockchain-Governance-Key |  私钥管理组件 |   暂不支持</br>后续版本规划   |   |      |     |
| WeBankBlockchain-Governance-Authority |  权限治理组件 |   暂不支持</br>后续版本规划   |   |       |   |
| WeBankBlockchain-Governance-Account |  账户治理组件 |   v3.0.0-rc4   |  [文档](https://governance-doc.readthedocs.io/zh_CN/v3.0.0/docs/WeBankBlockchain-Governance-Acct/index.html)  | [github](https://github.com/WeBankBlockchain/Governance-Account/tree/V3.0.0-rc4)   |  [gitee](https://gitee.com/WeBankBlockchain/Governance-Account/tree/V3.0.0-rc4)    |

#### 3.2.5. FISCO-BCOS v3.0.x

| 项目                                  | 功能简介                   | 版本                      | 文档                                                         | github                                                       | gitee                                                        | 备注                         |
| ------------------------------------- | -------------------------- | ------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ---------------------------- |
| FISCO-BCOS                            | 区块链底层平台             | v3.0.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/)  | [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/v3.0.0) |                              |
| Solidity                              | solidity智能合约           | v0.8.11                   | [文档](https://docs.soliditylang.org/en/v0.8.11)             |                                                              |                                                              | **最高支持solidity v0.8.11** |
| JavaSDK                               | Java语言SDK                | v3.0.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html) | [github](https://github.com/FISCO-BCOS/java-sdk/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/v3.0.0)   |                              |
| GoSDK                                 | Go语言SDK                  | 暂不支持</br>后续版本规划 |                                                              |                                                              |                                                              |                              |
| PythonSDK                             | Python语言SDK              | 暂不支持</br>后续版本规划 |                                                              |                                                              |                                                              |                              |
| 控制台(console)                       | 交互式命令行工具           | v3.0.x                   | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/console/index.html) | [github](https://github.com/FISCO-BCOS/console/tree/v3.0.0)  | [gitee](https://gitee.com/FISCO-BCOS/console/tree/v3.0.0)    |                              |
| java-sdk-demo                         | Java压测工具               | v3.0.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/stress_testing.html) | [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/3.0.0) |                              |
| spring-boot-starter                   | Gradle SpringBoot 应用示例 | v3.0.0                    | [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc4#spring-boot-starter) | [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0) |                              |
| spring-boot-crud                      | Maven SpringBoot 应用示例  | v3.0.0                    | [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc4#spring-boot-crud) | [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0) |                              |
| generator                             | 企业区块链部署工具         | 不支持                    |                                                              |                                                              |                                                              |                              |
| WeBASE                                | 区块链中间件平台           | v3.0.0        |    [文档](https://webasedoc.readthedocs.io/zh_CN/lab/)            | [github](https://github.com/WeBankBlockchain/WeBASE/tree/master-3.0)          |    [gitee](https://gitee.com/WeBank/WeBASE/tree/master-3.0)                    |                              |
| WeCross                               | 跨链协作平台               | 暂不支持</br>后续版本规划 |                                                              |                                                              |                                                              |                              |
| WeIdentity | 分布式身份解决方案 |   v1.8.5-rc1   | [文档](https://weidentity.readthedocs.io/zh_CN/latest/)   |   [github](https://github.com/WeBankBlockchain/WeIdentity)         |  [gitee](https://gitee.com/WeBank/WeIdentity)    |
| WeBankBlockchain-Data-Export          | 数据导出组件               | V3 |  [文档](https://data-doc.readthedocs.io/zh_CN/data_export_3.0/docs/WeBankBlockchain-Data-Export/index.html)  |  [github](https://github.com/WeBankBlockchain/Data-Export/tree/V3)  | [gitee](https://gitee.com/WeBankBlockchain/Data-Export/tree/V3/)  |                             |
| WeBankBlockchain-Data-Stash           | 数据仓库组件               | 不支持                    |                                                              |                                                              |                                                              |                              |
| WeBankBlockchain-Data-Reconcile |  数据对账组件 |   v1.0.0   |  [文档](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Reconcile/index.html)  |    [github](https://github.com/WeBankBlockchain/Data-Reconcile)      |    [gitee](https://gitee.com/WeBankBlockchain/Data-Reconcile)  |
| WeBankBlockchain-SmartDev-Scaffold    | 应用开发脚手架             | V3 |     [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Scaffold/index.html)  |  [github](https://github.com/WeBankBlockchain/SmartDev-Scaffold/tree/V3)  |  [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Scaffold/tree/V3/) |                     |
| WeBankBlockchain-SmartDev-SCGP |  智能合约编译插件 |   v1.0.1   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-SCGP/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-SCGP)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-SCGP)  |
| WeBankBlockchain-SmartDev-Contract |  智能合约库 |   v1.0.0   |  [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html)  |    [github](https://github.com/WeBankBlockchain/SmartDev-Contract)      |    [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Contract)  |
| WeBankBlockchain-Governance-Cert |  证书管理组件 |   v1.0.1   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Cert)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Cert)  |
| WeBankBlockchain-Governance-Key |  私钥管理组件 |   v1.0.2   |  [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html)  |    [github](https://github.com/WeBankBlockchain/Governance-Key)      |    [gitee](https://gitee.com/WeBankBlockchain/Governance-Key)  |
| WeBankBlockchain-Governance-Authority | 权限治理组件 | V3 |  [文档](https://governance-doc.readthedocs.io/zh_CN/v3.0.0/docs/WeBankBlockchain-Governance-Acct/index.html)    |  [github](https://github.com/WeBankBlockchain/Governance-Account/tree/V3)  |  [gitee](https://gitee.com/WeBankBlockchain/Governance-Account/tree/V3/)  |                              |
| WeBankBlockchain-Governance-Account   | 账户治理组件   | V3 |    [文档](https://governance-doc.readthedocs.io/zh_CN/v3.0.0/docs/WeBankBlockchain-Governance-Auth/index.html)   |  [github](https://github.com/WeBankBlockchain/Governance-Authority/tree/V3)   | [gitee](https://gitee.com/WeBankBlockchain/Governance-Authority/tree/V3/)       |                              |

#### 3.2.6. FISCO-BCOS v3.1.x

| 项目                                  | 功能简介                   | 版本                      | 文档                                                         | github                                                       | gitee                                                        | 备注                         |
| ------------------------------------- | -------------------------- | ------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ---------------------------- |
| FISCO-BCOS                            | 区块链底层平台             | v3.1.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/)  | [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/v3.1.0) | [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/v3.1.0) |                              |
| Solidity                              | solidity智能合约           | v0.8.11                   | [文档](https://docs.soliditylang.org/en/v0.8.11)             |                                                              |                                                              | **最高支持solidity v0.8.11** |
| JavaSDK                               | Java语言SDK                | v3.1.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html) | [github](https://github.com/FISCO-BCOS/java-sdk/tree/v3.1.1) | [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/v3.1.1)   |                              |
| GoSDK                                 | Go语言SDK                  | 暂不支持</br>后续版本规划 |                                                              |                                                              |                                                              |                              |
| PythonSDK                             | Python语言SDK              | 暂不支持</br>后续版本规划 |                                                              |                                                              |                                                              |                              |
| 控制台(console)                       | 交互式命令行工具           | v3.1.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/console/index.html) | [github](https://github.com/FISCO-BCOS/console/tree/v3.1.0)  | [gitee](https://gitee.com/FISCO-BCOS/console/tree/v3.1.0)    |                              |
| java-sdk-demo                         | Java压测工具               | v3.1.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/stress_testing.html) | [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/3.1.0) | [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/3.1.0) |                              |
| spring-boot-starter                   | Gradle SpringBoot 应用示例 | v3.0.0                    | [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc4#spring-boot-starter) | [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0) |                              |
| spring-boot-crud                      | Maven SpringBoot 应用示例  | v3.0.0                    | [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc4#spring-boot-crud) | [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0) |                              |
| generator                             | 企业区块链部署工具         | 不支持                    |                                                              |                                                              |                                                              |                              |
| WeBASE                                | 区块链中间件平台           | v3.0.0                    | [文档](https://webasedoc.readthedocs.io/zh_CN/lab/)          | [github](https://github.com/WeBankBlockchain/WeBASE/tree/master-3.0) | [gitee](https://gitee.com/WeBank/WeBASE/tree/master-3.0)     |                              |
| WeCross                               | 跨链协作平台               | v1.3.0（测试中）          |                                                              |                                                              |                                                              |                              |
| WeIdentity                            | 分布式身份解决方案         | v1.8.5-rc1                | [文档](https://weidentity.readthedocs.io/zh_CN/latest/)      | [github](https://github.com/WeBankBlockchain/WeIdentity)     | [gitee](https://gitee.com/WeBank/WeIdentity)                 |                              |
| WeBankBlockchain-Data-Export          | 数据导出组件               | V3                        | [文档](https://data-doc.readthedocs.io/zh_CN/data_export_3.0/docs/WeBankBlockchain-Data-Export/index.html) | [github](https://github.com/WeBankBlockchain/Data-Export/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/Data-Export/tree/V3/) |                              |
| WeBankBlockchain-Data-Stash           | 数据仓库组件               | 不支持                    |                                                              |                                                              |                                                              |                              |
| WeBankBlockchain-Data-Reconcile       | 数据对账组件               | v1.0.0                    | [文档](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Reconcile/index.html) | [github](https://github.com/WeBankBlockchain/Data-Reconcile) | [gitee](https://gitee.com/WeBankBlockchain/Data-Reconcile)   |                              |
| WeBankBlockchain-SmartDev-Scaffold    | 应用开发脚手架             | V3                        | [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Scaffold/index.html) | [github](https://github.com/WeBankBlockchain/SmartDev-Scaffold/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Scaffold/tree/V3/) |                              |
| WeBankBlockchain-SmartDev-SCGP        | 智能合约编译插件           | v1.0.1                    | [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-SCGP/index.html) | [github](https://github.com/WeBankBlockchain/SmartDev-SCGP)  | [gitee](https://gitee.com/WeBankBlockchain/SmartDev-SCGP)    |                              |
| WeBankBlockchain-SmartDev-Contract    | 智能合约库                 | v1.0.0                    | [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html) | [github](https://github.com/WeBankBlockchain/SmartDev-Contract) | [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Contract) |                              |
| WeBankBlockchain-Governance-Cert      | 证书管理组件               | v1.0.1                    | [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Cert) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Cert)  |                              |
| WeBankBlockchain-Governance-Key       | 私钥管理组件               | v1.0.2                    | [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Key) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Key)   |                              |
| WeBankBlockchain-Governance-Authority | 权限治理组件               | V3                        | [文档](https://governance-doc.readthedocs.io/zh_CN/v3.0.0/docs/WeBankBlockchain-Governance-Acct/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Account/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Account/tree/V3/) |                              |
| WeBankBlockchain-Governance-Account   | 账户治理组件               | V3                        | [文档](https://governance-doc.readthedocs.io/zh_CN/v3.0.0/docs/WeBankBlockchain-Governance-Auth/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Authority/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Authority/tree/V3/) |                              |

#### 3.2.6. FISCO-BCOS v3.2.x

| 项目                                  | 功能简介                   | 版本                      | 文档                                                         | github                                                       | gitee                                                        | 备注                         |
| ------------------------------------- | -------------------------- | ------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ---------------------------- |
| FISCO-BCOS                            | 区块链底层平台             | v3.2.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/)  | [github](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/v3.2.0) | [gitee](https://gitee.com/FISCO-BCOS/FISCO-BCOS/tree/v3.2.0) |                              |
| Solidity                              | solidity智能合约           | v0.8.11                   | [文档](https://docs.soliditylang.org/en/v0.8.11)             |                                                              |                                                              | **最高支持solidity v0.8.11** |
| JavaSDK                               | Java语言SDK                | v3.2.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html) | [github](https://github.com/FISCO-BCOS/java-sdk/tree/v3.2.0) | [gitee](https://gitee.com/FISCO-BCOS/java-sdk/tree/v3.2.0)   |                              |
| GoSDK                                 | Go语言SDK                  | 暂不支持</br>后续版本规划 |                                                              |                                                              |                                                              |                              |
| PythonSDK                             | Python语言SDK              | 暂不支持</br>后续版本规划 |                                                              |                                                              |                                                              |                              |
| 控制台(console)                       | 交互式命令行工具           | v3.2.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/console/index.html) | [github](https://github.com/FISCO-BCOS/console/tree/v3.2.0)  | [gitee](https://gitee.com/FISCO-BCOS/console/tree/v3.2.0)    |                              |
| java-sdk-demo                         | Java压测工具               | v3.2.x                    | [文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/stress_testing.html) | [github](https://github.com/FISCO-BCOS/java-sdk-demo/tree/3.2.0) | [gitee](https://gitee.com/FISCO-BCOS/java-sdk-demo/tree/3.2.0) |                              |
| spring-boot-starter                   | Gradle SpringBoot 应用示例 | v3.0.0                    | [文档](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0-rc4#spring-boot-starter) | [github](https://github.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/spring-boot-starter/tree/v3.0.0) |                              |
| spring-boot-crud                      | Maven SpringBoot 应用示例  | v3.0.0                    | [文档](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0-rc4#spring-boot-crud) | [github](https://github.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0) | [gitee](https://gitee.com/FISCO-BCOS/spring-boot-crud/tree/v3.0.0) |                              |
| generator                             | 企业区块链部署工具         | 不支持                    |                                                              |                                                              |                                                              |                              |
| WeBASE                                | 区块链中间件平台           | v3.0.0                    | [文档](https://webasedoc.readthedocs.io/zh_CN/lab/)          | [github](https://github.com/WeBankBlockchain/WeBASE/tree/master-3.0) | [gitee](https://gitee.com/WeBank/WeBASE/tree/master-3.0)     |                              |
| WeCross                               | 跨链协作平台               | v1.3.0（测试中）          |                                                              |                                                              |                                                              |                              |
| WeIdentity                            | 分布式身份解决方案         | v1.8.5-rc1                | [文档](https://weidentity.readthedocs.io/zh_CN/latest/)      | [github](https://github.com/WeBankBlockchain/WeIdentity)     | [gitee](https://gitee.com/WeBank/WeIdentity)                 |                              |
| WeBankBlockchain-Data-Export          | 数据导出组件               | V3                        | [文档](https://data-doc.readthedocs.io/zh_CN/data_export_3.0/docs/WeBankBlockchain-Data-Export/index.html) | [github](https://github.com/WeBankBlockchain/Data-Export/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/Data-Export/tree/V3/) |                              |
| WeBankBlockchain-Data-Stash           | 数据仓库组件               | 不支持                    |                                                              |                                                              |                                                              |                              |
| WeBankBlockchain-Data-Reconcile       | 数据对账组件               | v1.0.0                    | [文档](https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Reconcile/index.html) | [github](https://github.com/WeBankBlockchain/Data-Reconcile) | [gitee](https://gitee.com/WeBankBlockchain/Data-Reconcile)   |                              |
| WeBankBlockchain-SmartDev-Scaffold    | 应用开发脚手架             | V3                        | [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Scaffold/index.html) | [github](https://github.com/WeBankBlockchain/SmartDev-Scaffold/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Scaffold/tree/V3/) |                              |
| WeBankBlockchain-SmartDev-SCGP        | 智能合约编译插件           | v1.0.1                    | [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-SCGP/index.html) | [github](https://github.com/WeBankBlockchain/SmartDev-SCGP)  | [gitee](https://gitee.com/WeBankBlockchain/SmartDev-SCGP)    |                              |
| WeBankBlockchain-SmartDev-Contract    | 智能合约库                 | v1.0.0                    | [文档](https://toolkit-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html) | [github](https://github.com/WeBankBlockchain/SmartDev-Contract) | [gitee](https://gitee.com/WeBankBlockchain/SmartDev-Contract) |                              |
| WeBankBlockchain-Governance-Cert      | 证书管理组件               | v1.0.1                    | [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Cert) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Cert)  |                              |
| WeBankBlockchain-Governance-Key       | 私钥管理组件               | v1.0.2                    | [文档](https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Key) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Key)   |                              |
| WeBankBlockchain-Governance-Authority | 权限治理组件               | V3                        | [文档](https://governance-doc.readthedocs.io/zh_CN/v3.0.0/docs/WeBankBlockchain-Governance-Acct/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Account/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Account/tree/V3/) |                              |
| WeBankBlockchain-Governance-Account   | 账户治理组件               | V3                        | [文档](https://governance-doc.readthedocs.io/zh_CN/v3.0.0/docs/WeBankBlockchain-Governance-Auth/index.html) | [github](https://github.com/WeBankBlockchain/Governance-Authority/tree/V3) | [gitee](https://gitee.com/WeBankBlockchain/Governance-Authority/tree/V3/) |                              |

