# 控制台

标签：``console`` ``控制台`` ``命令行交互工具``

---------

```eval_rst
.. important::
    - ``控制台`` 只支持FISCO BCOS 3.0+版本，基于 `Java SDK <../sdk/java_sdk/index.html>`_ 实现。
    - 可通过命令 ``./start.sh --version`` 查看当前控制台版本
```

[控制台](https://github.com/FISCO-BCOS/console)是FISCO BCOS 3.0重要的交互式客户端工具，它通过[Java SDK](../sdk/java_sdk/index.md)与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity和webankblockchain-liquid合约文件编译后的WASM文件转换为Java合约文件。

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

命令行交互控制台是提供给开发者使用的节点查询与管理的工具。

控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。

```eval_rst
.. important::
    访问FISCO BCOS 3.x区块链须使用 ``>= v3.x`` 版本的控制台，不能使用 ``2.x`` 或 ``1.x`` 版本的控制台。
```

使用手册：

```eval_rst
.. toctree::
   :maxdepth: 2

   console_config.md
   console_commands.md
   console_error.md
```
