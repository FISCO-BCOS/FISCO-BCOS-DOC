# Java SDK

Java SDK 提供了访问 FISCO BCOS 节点的Java API，支持节点状态查询、部署和调用合约等功能，基于Java SDK可开发区块链应用，目前支持[FISCO BCOS 2.0+](../../../)。

```eval_rst
   .. admonition:: **主要特性**

    - 提供 `合约编译 <./quick_start.html#solidityjava>`_ ，基于Solidity合约文件生成合约Java客户端调用代码。
    - 提供 `Java SDK API <./api.html>`_,提供访问FISCO BCOS `JSON-RPC <../../api.html>`_ 的功能，并支持预编译（Precompiled）合约调用
    - 提供 `自定义构造和发送交易功能 <./assemble_transaction.html>`_
    - 提供 `AMOP <./amop.html>`_ 功能
    - 支持 `事件推送 <./event_sub.html>`_
    - 支持 `ABI解析 <./abi.html>`_
    - 提供 `账户管理 <./key_tool.html>`_ 接口
```

```eval_rst
.. toctree::
   :hidden:
   :maxdepth: 4

   quick_start.md
   configuration.md
   assemble_transaction.md
   transaction_decode.md
   amop.md
   event_sub.md
   abi.md
   api.md
   key_tool.md
   faq.md
```
