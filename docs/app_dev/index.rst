##############################################################
区块链应用开发
##############################################################

标签：``应用开发`` ``索引`` ``开发手册`` ``区块链应用``

----

.. admonition:: 外部调用

    - `账户管理 <./account.html>`_
        + 生成账户、用特定账户操作链
    - `SDK <../sdk/index.html>`_
        + 外部应用调用区块链上的智能合约
    - `链上信使协议 <./amop_protocol.html>`_
        + 多个SDK间的消息相互推送

.. admonition:: 合约开发

    - `智能合约开发 <./smart_contract.html>`_
        + 普通智能合约开发、预编译合约开发合约开发
    - `使用预编译合约 <./precompile_contract.html>`_
        + 普通智能合约开发、预编译合约开发合约开发
    - `并行合约 <./transaction_parallel.html>`_
        + 写并行合约，满足高并发场景
    - `隐私保护 <./privacy.html>`_
        + 预编译合约支持同态加密、群/环签名验证

.. admonition:: FISCO BCOS SDK

    - `FISCO BCOS Java SDK <../sdk/java_sdk/index.html>`_
        + (推荐使用)稳定、功能强大、无内置控制台、支持FISCO BCOS 2.0+
    - `FISCO BCOS Web3SDK <../sdk/web3sdk/java_sdk.html>`_
        + 旧版Java SDK、支持FISCO BCOS 2.0+
    - `FISCO BCOS NodeJS SDK <../sdk/nodejs_sdk/index.html>`_
        + 简单轻便、有内置控制台
    - `FISCO BCOS Python SDK <../sdk/python_sdk/index.html>`_
        + 简单轻便、有内置控制台
    - `FISCO BCOS Go SDK <../sdk/go_sdk/index.html>`_
        + 简单轻便、有内置控制台

.. important::

    * 核心特性
        + `并行合约 <./transaction_parallel.html>`_

.. toctree::
   :hidden:

   account.md
   smart_contract.md
   precompile_contract.md
   ../sdk/java_sdk/index.md
   ../sdk/web3sdk/java_sdk.md
   ../sdk/nodejs_sdk/index.rst
   ../sdk/python_sdk/index.rst
   ../sdk/go_sdk/index.rst
   amop_protocol.md
   transaction_parallel.md
   privacy.md
