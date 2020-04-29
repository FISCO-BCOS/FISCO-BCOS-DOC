##############################################################
使用手册
##############################################################

本章提供了FISCO BCOS平台的使用手册，使用手册介绍FISCO BCOS平台各种功能使用方式。

.. toctree::
   :hidden:
   :maxdepth: 1

   get_executable.md
   hardware_requirements.md
   build_chain.md
   certificates.md
   configuration.md
   group_use_cases.md
   distributed_storage.md
   console.md
   account.md
   amop_protocol.md
   smart_contract.md
   transaction_parallel.md
   node_management.md
   permission_control.md
   certificate_list.md
   storage_security.md
   guomi_crypto.md
   log_description.md
   caliper.md
   privacy.md


.. admonition:: 区块链部署

    - `获取可执行程序 <./get_executable.html>`_
        + 下载二进制、docker镜像或手动编译
    - `开发部署工具 <./build_chain.html>`_
        + 脚本选项、生成的节点目录结构
    - `证书说明 <./certificates.html>`_
        + 证书格式、证书对应角色、证书生成流程
    - `配置文件与配置项 <./configuration.html>`_
        + 节点所有的配置文件的详细说明
    - `多群组部署 <./group_use_cases.html>`_
        + 多群组架构的配置指导
    - `分布式存储 <./distributed_storage.html>`_
        + 分布式存储的配置指导


.. admonition:: 外部调用

    - `控制台 <./console.html>`_
        + 详细的控制台配置和使用说明
    - `账户管理 <./account.html>`_
        + 生成账户、用特定账户操作链
    - `SDK <../sdk/index.html>`_
        + 外部应用调用区块链上的智能合约
    - `链上信使协议 <./amop_protocol.html>`_
        + 多个SDK间的消息相互推送


.. admonition:: 合约开发

    - `智能合约开发 <./smart_contract.html>`_
        + 普通智能合约开发、预编译合约开发合约开发
    - `并行合约 <./transaction_parallel.html>`_
        + 写并行合约，满足高并发场景


.. admonition:: 管理与安全

    - `组员管理 <./node_management.html>`_
        + 节点加入、退出群组
    - `权限控制 <./permission_control.html>`_
        + 限制用户对区块链的操作
    - `CA黑白名单 <./certificate_list.html>`_
        + 通过配置拒绝与不安全的节点建立连接
    - `存储安全 <./storage_security.html>`_
        + 落盘加密，对节点存储的数据进行加密
    - `隐私保护 <./privacy.html>`_
        + 预编译合约支持同态加密、群/环签名验证

.. admonition:: 其它

    - `国密支持 <./guomi_crypto.html>`_
        + 国密版本的节点、SDK
    - `日志说明 <./log_description.html>`_
        + 节点日志格式和说明
    - `Caliper压力测试指南 <./caliper.html>`_
        + Caliper压力测试指南

.. important::

    * 核心特性
        + `多群组部署 <./group_use_cases.html>`_
        + `并行合约 <./transaction_parallel.html>`_
        + `分布式存储 <./distributed_storage.html>`_
