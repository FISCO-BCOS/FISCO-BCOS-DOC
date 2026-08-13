# 3. 可扩展区块链（Max版本）


标签：``Pro FISCO BCOS`` ``扩容`` ``配置`` ``部署工具``

------------

```eval_rst
.. warning::
    自 v3.17.0 起，Max 版本部署形态（``BcosMaxNodeService``、``BcosExecutorService`` 等微服务部署形态）计划在 v3.18.0 中不再维护；Air / Pro 版本保持不变，新部署建议使用 Air 版本。详见 `版本功能变更说明 <../../introduction/change_log/3_17_0.html>`_ 。
```

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. note::
   Max版本FISCO BCOS旨在提供海量存储服务、高性能可扩展的执行模块、高可用的故障恢复机制。
   Max版FISCO BCOS节点采用分布式存储TiKV，执行模块独立成服务，存储和执行均可横向扩展，且支持自动化主备恢复。
```

```eval_rst
.. toctree::
   :maxdepth: 1

   deploy_max_by_buildchain.md
   installation.md
   max_builder.md
   expand_max_withoutTars.md
```
