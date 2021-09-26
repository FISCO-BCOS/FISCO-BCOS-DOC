# Java SDK

标签：``java-sdk`` ``区块链应用``

----
Java SDK 提供了访问 FISCO BCOS 节点的Java API，支持节点状态查询、部署和调用合约等功能，基于Java SDK可开发区块链应用，目前支持[FISCO BCOS 2.0+](../../change_log/index.md)。


```eval_rst

   .. admonition:: **主要特性**

      - 提供 `合约编译 <./quick_start.html#solidityjava>`_ ，将Solidity合约文件转换成Java合约文件
      - 提供 `Java SDK API <./api.html>`_,提供访问FISCO BCOS `JSON-RPC <../../api.html>`_ 的功能，并支持预编译（Precompiled）合约调用
      - 提供 `自定义构造和发送交易功能 <./assemble_transaction.html>`_
      - 提供 `AMOP <./amop.html>`_ 功能
      - 支持 `事件推送 <./event_sub.html>`_
      - 支持 `ABI解析 <./abi.html>`_
      - 提供 `账户管理 <./key_tool.html>`_ 接口


.. toctree::
   :maxdepth: 1

   quick_start.md
   configuration.md
   assemble_transaction.md
   remote_sign_assemble_transaction
   make_transaction.md
   transaction_decode.md
   amop.md
   event_sub.md
   abi.md
   api.md
   key_tool.md
   crypto.md
   Java SDK JavaDoc <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/javadoc/index.html>
```
