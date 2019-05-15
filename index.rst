##############################################################
FISCO BCOS 技术文档
##############################################################

.. image:: _static/images/FISCO_BCOS_Logo.svg

.. admonition:: 关于FISCO BCOS

   FISCO BCOS 是一个稳定、高效、安全的区块链底层平台，经过了外部多家机构、多个应用，长时间在生产环境运行的实际检验。
   
    - `FISCO BCOS GitHub <https://github.com/FISCO-BCOS/FISCO-BCOS>`_

    - `微信群 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2.0/images/community/WeChatQR%2Ejpg>`_

    - `公众号 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2.0/images/community/OfficialAccountsQR%2Ejpg>`_

    - `Twitter <https://twitter.com/FiscoBcos>`_

    - `e-mail <mailto:service@fisco.com.cn>`_

.. important::
   
   - 本技术文档只适用FISCO BCOS 2.0及以上版本，FISCO BCOS 1.3.x版本的技术文档请查看 `1.3系列技术文档 <http://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/>`_
   - FISCO BCOS 2.0新特性请参考 `这里 <./docs/what_is_new.html>`_
   - FISCO BCOS 2.0版本及兼容性说明 `这里 <./docs/change_log/index.html>`_

.. admonition:: 关键特性

    - `多群组 <./docs/design/architecture/group.html>`_: `教程 <./docs/tutorial/group_use_cases.html>`_ ，`使用手册 <./docs/manual/configuration.html>`_
    - `并行计算 <./docs/design/parallel/dag.html>`_：`使用手册 <./docs/manual/transaction_parallel.html>`_
    - `分布式存储 <./docs/design/storage/index.html>`_: `使用手册 <./docs/manual/amdbconfig.html>`_

.. admonition:: 工具集
    
    - `企业级部署工具(Generator) <./docs/enterprise_tools/index.html>`_：支持建链、扩容等操作，**推荐构建企业级区块链时使用**，快速使用方法可参考 `教程 <./docs/tutorial/enterprise_quick_start.html>`_
    - `建链脚本(build_chain) <./docs/manual/build_chain.html>`_：简单快速的建链脚本，不支持扩容，**推荐区块链底层开发者使用**
    - `控制台 <./docs/manual/console.html>`_：**交互式客户端**，可访问区块链节点，控制台配置安装方法参考 `安装 <./docs/installation.html#id7>`_
    - `SDK <./docs/sdk/sdk.html>`_：提供访问节点、查询节点状态、修改区块链系统配置以及向节点发送交易等接口


.. admonition:: Overview

    - 了解FISCO BCOS版本、兼容性信息，请参考 `版本及兼容性 <./docs/change_log/index.html>`_
    - 基于FISCO BCOS 2.0快速构建区块链系统，请参考 `安装 <./docs/installation.html>`_
    - 基于FISCO BCOS 2.0部署多群组区块链、构建第一个区块链应用，请参考 `教程 <./docs/tutorial/index.html>`_
    - 使用企业级部署工具Generator，请参考 `教程 <./docs/tutorial/enterprise_quick_start.html>`_ 和 `企业级部署工具 <./docs/enterprise_tools/index.html>`_
    - 深入了解FISCO BCOS 2.0的 `源码编译 <./docs/manual/get_executable.html#id2>`_、`建链脚本 <./docs/manual/build_chain.html>`_、`配置文件和配置项 <./docs/manual/configuration.html>`_、`控制台 <./docs/manual/console.html>`_、`节点准入 <./docs/manual/node_management.html>`_、`并行交易 <./docs/manual/transaction_parallel.html>`_、`分布式存储 <./docs/manual/amdbconfig.html>`_、`国密 <./docs/manual/guomi_crypto.html>`_ 等请参考 `使用手册 <./docs/manual/index.html>`_
    - SDK的详细介绍请参考 `Web3SDK <./docs/sdk/sdk.html>`_
    - RPC接口可参考 `JSON-RPC API <./docs/api.html>`_
    - 系统设计文档请参考 `系统设计 <./docs/design/index.html>`_

.. toctree::
   :maxdepth: 1
   
   docs/introduction.md
   docs/what_is_new.md
   docs/change_log/index.rst
   docs/installation.md
   docs/tutorial/index.rst
   docs/manual/index.rst
   docs/enterprise_tools/index.md
   docs/design/index.rst
   docs/sdk/sdk.md
   docs/api.md
   docs/faq.md
   docs/community.md
