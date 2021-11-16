##############################################################
命令行交互控制台
##############################################################

标签：``控制台``

----

`命令行交互控制台 <https://github.com/FISCO-BCOS/console>`_ (简称“控制台”) 是FISCO BCOS 2.0重要的交互式客户端工具，它通过 `Java SDK <../sdk/java_sdk/index.md>`_ 与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity合约文件编译为Java合约文件。

.. important::
    - ``控制台2.6+`` 基于 `Java SDK <../sdk/java_sdk/index.html>`_ 实现, ``控制台1.x`` 系列基于 `Web3SDK <../sdk/java_sdk.html>`_ 实现，本教程针对 **2.6及其以上版本控制台**，1.x及其以上版本控制台使用文档请 `参考这里 <./console.html>`_ 
    - 可通过命令 ``./start.sh --version`` 查看当前控制台版本
    - 基于 `Java SDK <../sdk/java_sdk/index.html>`_ 开发应用过程中将 ``solidity`` 代码转换为 ``java`` 代码时，必须使用 ``2.6+`` 版本控制台，具体请参考  `这里 <../console/download_console.html>`_ 

.. important::
    前置条件：搭建FISCO BCOS区块链，请参考 `搭建第一个区块链网络 <../installation.html>`_
    建链工具参考：`开发部署工具 <../manual/build_chain.html>`_ 或 `运维部署工具 <../enterprise_tools/index.html>`_。


.. toctree::
   :maxdepth: 1

   download_console.md
   console_of_java_sdk.md
   console.md