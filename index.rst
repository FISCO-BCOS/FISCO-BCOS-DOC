##############################################################
FISCO BCOS 技术文档
##############################################################

.. image:: _static/images/FISCO_BCOS_Logo.svg

FISCO BCOS 是一个稳定、高效、安全的区块链底层平台，经过多家机构、多个应用，长时间在生产环境运行的实际检验。

- `Github主页 <https://github.com/FISCO-BCOS/FISCO-BCOS>`_
- `深度解析系列文章 <http://mp.weixin.qq.com/mp/homepage?__biz=MzA3MTI5Njg4Mw==&hid=2&sn=4f6d7251fbc4a73ed600e1d6fd61efc1&scene=18#wechat_redirect>`_
- `贡献代码 <https://mp.weixin.qq.com/s/_w_auH8X4SQQWO3lhfNrbQ>`_
- `反馈问题 <https://github.com/FISCO-BCOS/FISCO-BCOS/issues>`_
- `应用案例集 <https://mp.weixin.qq.com/s/cUjuWf1eGMbG3AFq60CBUA>`_
- `微信群 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/WeChatQR%2Ejpg>`_
- `公众号 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/OfficialAccountsQR%2Ejpg>`_


.. admonition:: 概览

    - 基于FISCO BCOS 2.0+快速构建区块链系统，请参考 `安装 <./docs/installation.html>`_
    - 基于FISCO BCOS 2.0+部署多群组区块链、构建第一个区块链应用，请参考 `教程 <./docs/tutorial/index.html>`_
    - 深入了解FISCO BCOS 2.0+功能请看 `配置文件和配置项 <./docs/manual/configuration.html>`_、`节点准入 <./docs/manual/node_management.html>`_、`并行交易 <./docs/manual/transaction_parallel.html>`_、`分布式存储 <./docs/manual/distributed_storage.html>`_、`国密 <./docs/manual/guomi_crypto.html>`_ 等请参考 `使用手册 <./docs/manual/index.html>`_
    - `控制台 <./docs/manual/console.html>`_：**交互式命令行工具**，可访问区块链节点，查询区块链状态，部署并调用合约等。
    - `运维部署工具(Generator) <./docs/enterprise_tools/index.html>`_：支持建链、扩容等操作，**推荐构建企业级区块链时使用**，快速使用方法可参考 `教程 <./docs/enterprise_tools/tutorial_one_click.html>`_
    - `SDK <./docs/sdk/index.html>`_：提供访问节点状态、修改区块链系统配置以及节点发送交易等接口。
    - 浏览器详细介绍请参考 `浏览器 <./docs/browser/browser.html>`_
    - JSON-RPC接口可参考 `JSON-RPC API <./docs/api.html>`_
    - 系统设计文档请参考 `系统设计 <./docs/design/index.html>`_

.. admonition:: 关键特性

    - 多群组: `教程 <./docs/manual/group_use_cases.html>`_  `使用手册 <./docs/manual/configuration.html>`_  `设计文档 <./docs/design/architecture/group.html>`_
    - 并行计算: `使用手册 <./docs/manual/transaction_parallel.html>`_  `设计文档 <./docs/design/parallel/dag.html>`_
    - 分布式存储: `使用手册 <./docs/manual/distributed_storage.html>`_  `设计文档 <./docs/design/storage/index.html>`_

.. important::

   - 本技术文档只适用FISCO BCOS 2.0+，FISCO BCOS 1.3.x版本的技术文档请查看 `1.3系列技术文档 <http://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/>`_
   - FISCO BCOS 2.0+版本及兼容性说明 `这里 <./docs/change_log/index.html>`_


.. toctree::
   :maxdepth: 1
   :caption: 平台介绍

   docs/intro/introduction.md
   docs/change_log/index.rst

.. toctree::
   :maxdepth: 1
   :caption: 教程

   docs/tutorial/installation.md
   docs/tutorial/sdk_application.md
   docs/tutorial/index.rst

.. toctree::
   :maxdepth: 1
   :caption: 开发手册

   docs/blockchain_dev/index.rst
   docs/app_dev/index.rst
   docs/faq/index.rst

.. toctree::
   :maxdepth: 1
   :caption: 工具

   docs/tools/build_chain.md
   docs/console/index.rst
   docs/webase/webase.md
   docs/browser/browser.md
   docs/enterprise_tools/index.md

.. toctree::
   :maxdepth: 1
   :caption: 系统设计
   
   docs/design/architecture/index.rst
   docs/design/index.rst

.. toctree::
   :maxdepth: 1
   :caption: 参考

   docs/reference/api.md
   Java SDK JavaDoc <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/javadoc/index.html>
   docs/articles/index.rst
   docs/reference/community.md
