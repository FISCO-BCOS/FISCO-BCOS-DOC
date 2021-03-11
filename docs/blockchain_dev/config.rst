##############################################################
配置管理
##############################################################

- `节点配置 <../manual/configuration.html>`_ 介绍了节点和账本配置方法，包括端口、证书、日志、群组、共识、存储、交易池、同步、流控等配置项。
- `组员配置 <../manual/node_management.html>`_ 介绍了组员节点的加入和推出方法，包括节点加入/退出网络、节点加入/推出群组、转化成共识节点、观察者节点及游离节点等操作。
- `配置CA黑白名单 <../manual/certificate_list.html>`_ 介绍了如何通过配置CA黑白名单，实现拒绝与无关的链的节点或指定节点建立连接，或实现仅允许与白名单中的节点建立链接。
- `存储加密 <../tutorial/enc.html>`_ 介绍了设置落盘加密的流程，保证了运行联盟链的数据在硬盘上的安全性。
- `账户权限控制 <../manual/permission_control.html>`_ 介绍了基于角色的账户权限控制方法。
- `设置SDK白名单 <../manual/permission_control.html>`_ 介绍了设计节点所服务的SDK证书白名单方法。

.. toctree::
   :hidden:

   ../manual/configuration.md
   ../manual/node_management.md
   ../manual/certificate_list.md
   ../tutorial/enc.md
   ../manual/permission_control.md
   ../manual/sdk_allowlist.md
