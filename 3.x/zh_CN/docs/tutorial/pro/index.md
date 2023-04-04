# 多群组区块链（Pro版本）


标签：``Pro FISCO BCOS`` ``扩容`` ``配置`` ``部署工具``

------------

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. note::
   包括RPC、Gateway接入层的服务和多个区块链节点Node服务组成，其中一个Node服务代表一个群组，存储采用本地RocksDB，所有Node共用接入层服务，接入层的服务可平行扩展，适用于容量可控（T级以内）的生产环境，能够支持多群组动态扩展。
```

```eval_rst
.. toctree::
   :maxdepth: 1

   installation.md
   config.md
   pro_builder.md
   expand_service.md
   expand_node.md
   expand_group.md
```
