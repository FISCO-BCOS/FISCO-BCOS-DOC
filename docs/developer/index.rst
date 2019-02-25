##############################################################
合约开发
##############################################################

本章介绍FISCO BCOS智能合约开发相关知识。FISCO BCOS平台目前支持Precompiled、CRUD、Solidity三种智能合约形式。

预编译（Precompiled）合约使用C++开发，内置于FISCO BCOS平台，相比于Solidity合约具有更好的性能，但由于是编译时确定，所以适用于逻辑固定但需要共识的场景，例如群组配置。

CRUD合约通过在Solidity合约中支持分布式存储预编译合约，可以实现将Solidity合约中数据存储在FISCO BCOS平台AMDB的表结构中，实现合约逻辑与数据的分离。

Solidity与以太坊相同，支持最新版本。

.. toctree::
   :maxdepth: 2

   precompiled.md
   crud_sol.md
