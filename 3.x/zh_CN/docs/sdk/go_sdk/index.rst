##############################################################
4. Go SDK
##############################################################

标签：``go-sdk`` ``Go SDK``

----

`Go SDK <https://github.com/FISCO-BCOS/go-sdk>`_ 提供了访问 `FISCO BCOS <https://github.com/FISCO-BCOS/FISCO-BCOS>`_ 节点的Go API，支持节点状态查询、部署和调用合约等功能，基于Go SDK可快速开发区块链应用，目前支持 `FISCO BCOS v2.2.0+与v3.2.0+  <../../../>`_

.. admonition:: **主要特性**

    - 提供调用FISCO BCOS `JSON-RPC <../../api.html>`_ 的Go API
    - 提供合约编译，将Solidity合约文件编译成abi和bin文件，然后再转换成Go合约文件
    - 提供部署及调用go合约文件的GO API
    - 提供调用预编译（Precompiled）合约的Go API，其中v2版本已完整支持，v3版本部分支持
    - 支持与节点建立TLS与国密TLS连接
    - 提供CLI（Command-Line Interface）工具，供用户在命令行中方便快捷地与区块链交互

安装配置环境，使用Go SDK进行应用开发请参见 `gitHub <https://github.com/FISCO-BCOS/go-sdk>`_ 链接

.. toctree::
   :hidden:
   :maxdepth: 3

   env_conf.md
   api.md
   console.md
   contractExamples.md
   amopExamples.md
   event_sub.md