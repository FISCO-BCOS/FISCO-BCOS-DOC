##############################################################
Node.js SDK
##############################################################

标签：``Node.JS SDK`` 

----
`Node.js SDK <https://github.com/FISCO-BCOS/nodejs-sdk>`_ 提供了访问 `FISCO BCOS <https://github.com/FISCO-BCOS/FISCO-BCOS>`_ 节点的Node.js API，支持节点状态查询、部署和调用合约等功能，基于Node.js SDK可快速开发区块链应用，目前支持 `FISCO BCOS 2.0+  <../../../>`_ 


.. admonition:: **注意**
    :class: red

    **Node.js SDK目前仅处于个人开发者体验阶段，开发企业级应用请使用** `Java SDK <../java_sdk/index.html>`_
    Node.js SDK目前不支持国密SSL通信协议



.. admonition:: **主要特性**

    - 提供调用FISCO BCOS `JSON-RPC <../../api.html>`_ 的Node.js API
    - 提供部署及调用Solidity合约（支持Solidity 0.4.x 及Solidity 0.5.x）的Node.js API
    - 提供调用预编译（Precompiled）合约的Node.js API
    - 使用 `Channel协议 <../../design/protocol_description.html#channelmessage>`_ 与FISCO BCOS节点通信，双向认证更安全
    - 提供CLI（Command-Line Interface）工具供用户在命令行中方便快捷地与区块链交互

.. toctree::
   :hidden:

   install.md
   configuration.md
   api.md