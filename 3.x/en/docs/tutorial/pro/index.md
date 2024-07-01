# 2. Multi-group blockchain (Pro version)


Tags: "Pro FISCO BCOS" "" Expansion "" Configuration "" Deployment Tools ""

------------

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. note::
   It consists of RPC, Gateway access layer services, and multiple blockchain node node services. One node service represents a group, and the storage uses local RocksDB. All nodes share access layer services. Access layer services can be extended in parallel. It is suitable for production environments with controllable capacity (within T level) and can support multi-group dynamic expansion.。
```

```eval_rst
.. toctree::
   :maxdepth: 1
   
   deploy_pro_by_buildchain.md
   installation.md
   installation_without_tars.md
   config.md
   pro_builder.md
   expand_service.md
   expand_node.md
   expand_pro_withoutTars.md
   expand_group.md
```
