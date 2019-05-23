##############################################################
存储模块
##############################################################

FISCO BCOS继承以太坊存储的同时，引入了高扩展性、高吞吐量、高可用、高性能的分布式存储。存储模块主要包括两部分：

**世界状态**: 可进一步划分成 **MPTState** 和 **StorageState**

- **MPTState**: 使用MPT树存储账户的状态，与以太坊一致

- **StorageState**: 使用分布式存储的表结构存储账户状态，不存历史信息，去掉了对MPT树的依赖，性能更高


**分布式存储(Advanced Mass Database，AMDB)**: 通过抽象表结构，实现了SQL和NOSQL的统一，通过实现对应的存储驱动，可以支持各类数据库，目前已经支持LevelDB和MySQL。

.. image:: ../../../images/storage/architecture.png


.. toctree::
   :maxdepth: 1
   
   storage.md
   mpt.md
