##############################################################
网络搭建
##############################################################

.. important::

   如果您希望快速搭建一个区块链网络，推荐您参考“快速开始” > " `搭建第一个区块链网络 <../installation.html>`_ "。建链工具可参考 `开发部署工具 <../manual/build_chain.html>`_ 或 `运维部署工具 <../enterprise_tools/index.html>`_。

本章提供了丰富的FISCO BCOS区块链网络搭建教程。

- 首先，请确保您用于部署区块链网络的主机满足FISCO BCOS区块链搭建的 `硬件和系统要求 <../manual/hardware_requirements.html>`_
- 如果您的操作系统为Ubuntu 16.04和CentOS 7.2及以上版本，则可以直接 `下载预编译文件 <../manual/get_executable.html>`_ , 并运行节点 。否则请从 `源码编译 <../tutorial/compile.html>`_ FISCO BCOS可执行文件。
- 如果您在国内访问GitHub较慢，请参照 `国内镜像和CDN加速 <../tutorial/cdn.html>`_ 提升您的下载速度。
- 在多台机器上运行区块链节点，构成一个区块链网络，请参照 `搭建多机区块链网络 <../tutorial/multihost.html>`_ 教程。
- `使用Docker容器搭建区块链网络 <../tutorial/docker.html>`_ 。
- `使用企业级运维部署工具建链 <../enterprise_tools/tutorial_one_click.html>`_ 。
- `扩容一个新节点 <../tutorial/add_new_node.html>`_ 。
- `多群组部署 <../manual/group_use_cases.html>`_ 。
- FISCO BOCS网络采用面向CA的准入机制，`获取和续期证书 <../manual/certificates.html>`_ 。
- 我们支持采用第三方证书部署节点，如有需要请参考 `使用CFCA证书部署节点 <../tutorial/cfca.html>`_ 。
- `使用MySQL存储引擎 <../tutorial/mysql_node.html>`_ 搭建区块链网络。
- 搭建 `使用国密算法 <../manual/guomi_crypto.html>`_ 的区块链网络。
    

.. toctree::
   :hidden:

   ../manual/hardware_requirements.md
   ../manual/get_executable.md
   ../tutorial/compile.md
   ../tutorial/cdn.md
   ../tutorial/multihost.md
   ../tutorial/docker.md   
   使用企业级运维部署工具建链 <../enterprise_tools/tutorial_one_click.md>
   ../tutorial/add_new_node.md
   ../manual/group_use_cases.md
   ../manual/certificates.md
   ../tutorial/cfca.md
   ../tutorial/mysql_node.md
   ../manual/guomi_crypto.md
  