##############################################################
企业工具
##############################################################
fisco-generator_ 是针对多机构组网，适用于多机构部署维护多群组联盟链的企业区块链部署工具。

    - 在使用本工具时，所有机构间只需要共享节点的证书，节点的私钥由各机构自己生成维护，保证节点私钥不出内网，确保机构间节点安全性；
    - 同时提供了多种联盟链多群组间的部署方式，可以降低机构间生成与维护区块链的复杂度；
    - 基于本工具，多机构间可以通过交换数字证书，对等、安全地部署自己的节点。节点生成完成后导入私钥，启动属于本机构的节点。

.. _fisco-generator: https://github.com/FISCO-BCOS/generator

.. image:: ../../images/enterprise/toolshow.png

.. toctree::
   :maxdepth: 1

   introduction.md
   hardware_requirement.md
   installing.md
   quick_start.md
   config.md
   manual/index.rst
   monitor.md
   playgroud/index.rst