##############################################################
FISCO BCOS 技术文档
##############################################################

.. image:: _static/images/FISCO_BCOS_Logo.svg

FISCO BCOS 是一个稳定、高效、安全的区块链底层平台，经过多家机构、多个应用，长时间在生产环境运行的实际检验。

- `Github主页 <https://github.com/FISCO-BCOS/FISCO-BCOS>`_   
- `深度解析系列文章 <http://mp.weixin.qq.com/mp/homepage?__biz=MzU5NTg0MjA4MA==&hid=9&sn=7edf9a62a2f45494671c91f0608db903&scene=18#wechat_redirect>`_  
- `贡献代码 <https://mp.weixin.qq.com/s/hEn2rxqnqp0dF6OKH6Ua-A>`_ 
- `反馈问题 <https://github.com/FISCO-BCOS/FISCO-BCOS/issues>`_ 
- `应用案例集 <https://mp.weixin.qq.com/s/vUSq80LkhF8yCfUF7AILgQ>`_ 

- `微信群 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2.0/images/community/WeChatQR%2Ejpg>`_
- `公众号 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2.0/images/community/OfficialAccountsQR%2Ejpg>`_

.. important::
   
   - 本技术文档只适用FISCO BCOS 2.0及以上版本，FISCO BCOS 1.3.x版本的技术文档请查看 `1.3系列技术文档 <http://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/>`_
   - FISCO BCOS 2.0新特性请参考 `这里 <./docs/what_is_new.html>`_
   - FISCO BCOS 2.0版本及兼容性说明 `这里 <./docs/change_log/index.html>`_

.. admonition:: 概览

    - 基于FISCO BCOS 2.0快速构建区块链系统，请参考 `安装 <./docs/installation.html>`_
    - 基于FISCO BCOS 2.0部署多群组区块链、构建第一个区块链应用，请参考 `教程 <./docs/tutorial/index.html>`_
    - 深入了解FISCO BCOS 2.0功能请看 `配置文件和配置项 <./docs/manual/configuration.html>`_、`节点准入 <./docs/manual/node_management.html>`_、`并行交易 <./docs/manual/transaction_parallel.html>`_、`分布式存储 <./docs/manual/amdbconfig.html>`_、`国密 <./docs/manual/guomi_crypto.html>`_ 等请参考 `使用手册 <./docs/manual/index.html>`_
    - `控制台 <./docs/manual/console.html>`_：**交互式命令行工具**，可访问区块链节点，查询区块链状态，部署并调用合约等。
    - `企业级部署工具(Generator) <./docs/enterprise_tools/index.html>`_：支持建链、扩容等操作，**推荐构建企业级区块链时使用**，快速使用方法可参考 `教程 <./docs/tutorial/enterprise_quick_start.html>`_
    - `Web3SDK <./docs/sdk/sdk.html>`_：提供访问节点状态、修改区块链系统配置以及节点发送交易等接口。
    - 浏览器详细介绍请参考 `浏览器 <./docs/browser/browser.html>`_
    - JSON-RPC接口可参考 `JSON-RPC API <./docs/api.html>`_ 
    - 系统设计文档请参考 `系统设计 <./docs/design/index.html>`_ 

.. admonition:: 关键特性

    - 多群组: `教程 <./docs/tutorial/group_use_cases.html>`_ ，`使用手册 <./docs/manual/configuration.html>`_ `多群组设计文档 <./docs/design/architecture/group.html>`_
    - 并行计算: `使用手册 <./docs/manual/transaction_parallel.html>`_ `并行计算设计文档 <./docs/design/parallel/dag.html>`_
    - 分布式存储: `使用手册 <./docs/manual/amdbconfig.html>`_ `分布式存储设计文档 <./docs/design/storage/index.html>`_

.. toctree::
   :hidden:
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
   docs/browser/browser.md
   docs/api.md
   docs/faq.md
   docs/community.md