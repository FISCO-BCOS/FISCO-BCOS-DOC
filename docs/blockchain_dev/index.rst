##############################################################
区块链网络搭建
##############################################################

标签：``搭建区块链网络`` ``索引`` ``开发手册`` ``区块链搭建``

----

本章提供了FISCO BCOS平台的使用手册，使用手册介绍FISCO BCOS平台各种功能使用方式。

.. admonition:: 区块链部署

    - `获取可执行程序 <../manual/get_executable.html>`_
        + 下载二进制、docker镜像或手动编译
    - `开发部署工具 <../manual/build_chain.html>`_
        + 脚本选项、生成的节点目录结构
    - `证书说明 <../manual/certificates.html>`_
        + 证书格式、证书对应角色、证书生成流程
    - `配置文件与配置项 <../manual/configuration.html>`_
        + 节点所有的配置文件的详细说明
    - `多群组部署 <../manual/group_use_cases.html>`_
        + 多群组架构的配置指导
    - `分布式存储 <../manual/distributed_storage.html>`_
        + 分布式存储的配置指导


.. admonition:: 管理与安全

    - `组员管理 <../manual/node_management.html>`_
        + 节点加入、退出群组
    - `权限控制 <../manual/permission_control.html>`_
        + 限制用户对区块链的操作
    - `CA黑白名单 <../manual/certificate_list.html>`_
        + 通过配置拒绝与不安全的节点建立连接
    - `存储安全 <../manual/storage_security.html>`_
        + 落盘加密，对节点存储的数据进行加密


.. admonition:: 其它

    - `国密支持 <../manual/guomi_crypto.html>`_
        + 国密版本的节点、SDK
    - `日志说明 <../manual/log_description.html>`_
        + 节点日志格式和说明
    - `Caliper压力测试指南 <../manual/caliper.html>`_
        + Caliper压力测试指南
    - `极端异常下的共识恢复应急方案 <../manual/consensus_recover.html>`_
        + 极端异常下的共识恢复应急方案
    - `海量数据治理 <../manual/data_governance.html>`_
        + 海量数据治理使用说明

.. important::

    * 核心特性
        + `多群组部署 <../manual/group_use_cases.html>`_
        + `分布式存储 <../manual/distributed_storage.html>`_


.. toctree::
   :hidden:

   ../manual/hardware_requirements.md
   ../manual/get_executable.md
   ../manual/certificates.md
   ../manual/cfca.md
   ../manual/certificate_list.md
   ../manual/configuration.md
   ../manual/guomi_crypto.md
   ../manual/group_use_cases.md
   ../manual/distributed_storage.md
   ../manual/data_governance.md
   ../manual/sdk_allowlist.md
   ../manual/storage_security.md
   ../manual/node_management.md
   ../manual/permission_control.md
   ../manual/consensus_recover.md
   ../tutorial/cdn.md
   ../manual/caliper.md
   ../manual/log_description.md




