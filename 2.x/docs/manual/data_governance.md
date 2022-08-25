# 海量数据治理

区块链在运行的过程中，随着上链业务数的增加，会出现链数据量成比例增加和链性能有一定程度衰减的情况。其中性能衰减对业务体验影响较大，并且无法通过增加硬盘容量等硬件方式解决。通过分析，我们可采用**scalable存储模式**，通过对写入本地的链上数据按照一定维度进行划分，分别存于不同存储实例，从而控制节点所访问的单个存储实例大小，稳定链处理能力；同时，对本地的存储实例，在实现其备份归档后可以有选择地进行实例删除，控制本地存储的整体数据量。

```eval_rst
.. important::

    - FISCO BCOS从2.7.1版本开始，通过配置“数据仓库”，实现对海量数据治理的支持；

    - FISCO BCOS为实现海量数据治理的功能，需配合WeBankBlockchain-Data-Stash，相关操作见  `[文档] <https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Stash/install.html>`_ 
    
    - 更多“数据仓库”的使用请参考 `[文档] <https://data-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Data-Stash/index.html>`_ 。

```
