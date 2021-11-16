##############################################################
FISCO BCOS Documentation
##############################################################

.. image:: _static/images/FISCO_BCOS_Logo.svg

FISCO BCOS is a reliable, secure, efficient and portable blockchain platform with proven success from many partners and successful financial-grade applications.

- `Github homepage <https://github.com/FISCO-BCOS/FISCO-BCOS>`_
- `Insightful articles <http://mp.weixin.qq.com/mp/homepage?__biz=MzA3MTI5Njg4Mw==&hid=2&sn=4f6d7251fbc4a73ed600e1d6fd61efc1&scene=18#wechat_redirect>`_
- `Code contribution <https://mp.weixin.qq.com/s/_w_auH8X4SQQWO3lhfNrbQ>`_
- `Feedback <https://github.com/FISCO-BCOS/FISCO-BCOS/issues>`_
- `Application cases <https://mp.weixin.qq.com/s/cUjuWf1eGMbG3AFq60CBUA>`_

- `WeChat group <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/WeChatQR%2Ejpg>`_
- `WeChat official account <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/OfficialAccountsQR%2Ejpg>`_


.. admonition:: Overview

    - To fast build a blockchain system based on FISCO BCOS 2.0+, please read `Installation <./docs/installation.html>`_
    - To deploy multi-group blockchain and the first blockchain application based on FISCO BCOS 2.0+, please read `Quick Guide <./docs/tutorial/index.html>`_
    - To know more about functions of FISCO BCOS 2.0+, please read `Config files and items <./docs/manual/configuration.html>`_, `Node access <./docs/manual/node_management.html>`_, `Parallel transactions <./docs/manual/transaction_parallel.html>`_, `Distributed storage <./docs/manual/distributed_storage.html>`_, `OSCCA computing <./docs/manual/guomi_crypto.html>`_ in `Operation Tutorial <./docs/blockchain_dev/index.html>`_
    - `Console <./docs/console/console.html>`_：**Interactive command tool** to visit blockchain nodes and check status, deploy or call contract, etc.
    - `Deployment tool(Generator) <./docs/enterprise_tools/index.html>`_：to support operations like building blockchain, expansion, etc., **recommended for business level applications**. You can learn the operation methods in `Quick Guide <./docs/enterprise_tools/tutorial_one_click.html>`_
    - `SDK <./docs/sdk/index.html>`_：offer APIs for node status, blockchain system configuration modification and nodes to send transactions.
    - The detailed introduction of browser is in `Browser <./docs/browser/browser.html>`_
    - JSON-RPC interface is introduced in `JSON-RPC API <./docs/api.html>`_
    - System design documentation: `System design <./docs/design/index.html>`_

.. admonition:: Key features

    - Multi-group: `Quick Guide <./docs/manual/group_use_cases.html>`_  `Operation Tutorial <./docs/manual/configuration.html>`_  `Design Documentation <./docs/design/architecture/group.html>`_
    - Parallel computing: `Operation Tutorial <./docs/manual/transaction_parallel.html>`_  `Design documentation <./docs/design/parallel/dag.html>`_
    - Distributed storage: `Operation Tutorial <./docs/manual/distributed_storage.html>`_  `Design documentation <./docs/design/storage/index.html>`_

.. important::

   - This technical documentation is only adaptable for FISCO BCOS 2.0+. For FISCO BCOS 1.3.x users, please check `Ver.1.3 Documentation <http://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/>`_
   - FISCO BCOS 2.0+ and its adaptability are illustrated `here <./docs/change_log/index.html>`_

.. toctree::
   :maxdepth: 1
   :caption: Introduction

   docs/introduction.md
   docs/change_log/index.rst

.. toctree::
   :maxdepth: 1
   :caption: Tutorials

   docs/installation.md
   docs/tutorial/sdk_application.md
   docs/tutorial/index.rst

.. toctree::
   :maxdepth: 1
   :caption: Development

   docs/blockchain_dev/index.rst
   docs/app_dev/index.rst
   docs/faq/faq.md

.. toctree::
   :maxdepth: 1
   :caption: Tools

   docs/manual/build_chain.md
   docs/console/index.rst
   docs/webase/webase.md
   docs/browser/browser.md
   docs/enterprise_tools/index.md

.. toctree::
   :maxdepth: 1
   :caption: System Design
   
   ./design/architecture/index.rst
   ./design/index.rst

.. toctree::
   :maxdepth: 1
   :caption: Reference

   docs/api.md
   Java SDK JavaDoc <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/javadoc/index.html>
   docs/articles/index.rst
   docs/community.md