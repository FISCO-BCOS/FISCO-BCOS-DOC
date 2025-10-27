##############################################################
网络维护
##############################################################

- `压力测试指南 <../tutorial/stress_testing.html>`_ 介绍了如何使用Caliper对FISCO BCOS区块链网络进行压力测试。
- `查看日志 <../manual/log_description.html>`_ 介绍了日志格式和各关键步骤的日志样式，帮助您了解节点运行情况及排查问题。
- `极端异常下的共识恢复应急方案 <../manual/consensus_recover.html>`_ 介绍由于网络脑裂、节点网络中断、节点硬件崩溃等极端情况，导致PBFT网络共识异常的情况下，恢复区块链网络的方法。
- `海量数据治理 <../manual/data_governance.html>`_ 介绍了一种本地+远端数据仓库的联合区块链数据存储方式。当节点本地存储资源不足或运行过慢的情况下可开启此海量数据治理功能。开启后，可裁剪本地数据从而加快存储，当需要访问被裁剪的数据时，即可访问保存有全量数据的远端数据仓库。

.. toctree::
   :hidden:
    
   ../tutorial/stress_testing.md
   ../manual/log_description.md
   ../manual/consensus_recover.md
   ../manual/data_governance.md