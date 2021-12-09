# 区块链组件

标签：``区块链中间件平台`` ``图形化的区块链管理工具`` ``区块链多方协作治理组件`` ``区块链应用开发组件``

FISCO BCOS社区接入了丰富的区块链开源组件：

- **图形化的区块链管理工具**: WeBankBlockchain WeBASE(WeBank Blockchain Application Software Extension, 简称WBC-WeBASE) 是一套管理FISCO-BCOS联盟链的工具集。WBC-WeBASE提供了图形化的管理界面，屏蔽了区块链底层的复杂度，降低区块链使用的门槛，大幅提高区块链应用的开发效率，包含节点前置、节点管理、交易链路，数据导出，Web管理平台等子系统。

***
------

- **数据治理通用组件**: 全名是“WeBankBlockchain-Data数据治理通用组件”，它是一套稳定、高效、安全的区块链数据治理组件解决方案，可无缝适配FISCO BCOS区块链底层平台。它由数据导出组件(Data-Export)、数据仓库组件(Data-Stash)、数据对账组件(Data-Reconcile)这三款相互独立、可插拔、可灵活组装的组件所组成，开箱即用，灵活便捷，易于二次开发。

***
------


- **区块链多方协作治理组件**: WeBankBlockchain-Governance区块链多方协作治理组件是一套轻量解耦、简洁易用、通用场景和一站式的区块链治理组件解决方案。 首批开源的有账户治理组件(Governance-Account)、权限治理组件(Governance-Auth)、 私钥管理组件(Governance-Key)和证书管理组件（Governance-Cert）。上述组件都提供了可部署的智能合约代码、易于使用的SDK和可参考的落地实践Demo等交付物。

***
------


- **区块链应用开发组件**: WeBankBlockchain-SmartDev应用开发组件包含了一套开放、轻量的开发组件集，覆盖智能合约的开发、调试、应用开发等环节，包括智能合约库（SmartDev-Contract）、智能合约编译插件（SmartDev-SCGP）和应用开发脚手架（SmartDev-Scaffold）。开发者可根据自己的情况自由选择相应的开发工具，提升开发效率。

----------

```eval_rst
.. toctree::
   :maxdepth: 1
   

   webase.md
   data_index.md
   governance_index.md
   smartdev_index.md
```