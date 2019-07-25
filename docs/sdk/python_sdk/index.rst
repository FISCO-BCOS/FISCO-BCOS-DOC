##############################################################
Python SDK
##############################################################

`Python SDK <https://github.com/FISCO-BCOS/python-sdk>`_ 提供了访问 `FISCO BCOS <https://github.com/FISCO-BCOS/FISCO-BCOS>`_ 节点的Python API，支持节点状态查询、部署和调用合约等功能，基于Python SDK可快速开发区块链应用，目前仅支持 `FISCO BCOS 2.0  <../../../>`_ 


.. admonition:: **注意**
    :class: red

    **Python SDK目前仅处于个人开发者体验阶段，开发企业级应用请使用** `Web3SDK <../java_sdk.html>`_



.. admonition:: **主要特性**

    - 提供调用FISCO BCOS `JSON-RPC <../../api.html>`_ 的Python API
    - 支持使用 `Channel协议 <../../design/protocol_description.html#channelmessage>`_ 与FISCO BCOS节点通信，保证节点与SDK安全加密通信的同时，可接收节点推送的消息。
    - 支持交易解析功能：包括交易输入、交易输出、Event Log等ABI数据的拼装和解析
    - 支持合约编译，将 ``sol`` 合约编译成 ``abi`` 和 ``bin`` 文件
    - 支持基于keystore的账户管理
    - 支持合约历史查询


.. toctree::
   :hidden:

   install.md
   configuration.md
   api.md
   console.md
   demo.md
