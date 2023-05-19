##############################################################
FISCO BCOS 技术文档
##############################################################

.. image:: _static/images/FISCO_BCOS_Logo.svg

FISCO BCOS（读作/ˈfɪskl  bi:ˈkɒz/） 是一个稳定、高效、安全的区块链底层平台，其可用性经广泛应用实践检验。至今已超过4000家企业及机构、300+产业数字化标杆应用，覆盖文化版权、司法服务、政务服务、物联网、金融、智慧社区、房产建筑、社区治理、乡村振兴等领域。

.. image:: images/introduction/applications.png
   :align: center
   :alt: 产业应用


FISCO BCOS开源社区致力打造开放多元的开源联盟链生态，至今为止，开源社区汇聚超过90000+成员共建共治，发展成为最大最活跃的国产开源联盟链生态圈，其中涌现出诸多对社区建设、代码贡献的优秀社区成员。2022年，开源社区共认定20位MVP，这些优秀的贡献者或是将FISCO BCOS技术落地到各领域应用中，助力产业数字化，或是在多渠道布道，将开源社区精神传播到更远的地方。

.. image:: _static/images/img_1.png
   :align: center
   :width: 7in
   :height: 5in
   :alt: FISCO BCOS 2022年度MVP

.. note::
   本技术文档适用于FISCO BCOS 3.x版本, FISCO BCOS 2.x稳定版技术文档请参考 `FISCO BCOS 2.x技术文档(stable) <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/>`_

   FISCO BCOS 3.x版本源码位于 `master` 分支，请参考 `这里 <https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master>`_
   FISCO BCOS 2.x版本源码位于 `master-2.0` 分支，请参考 `这里 <https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0>`_

.. container:: row

   .. container:: card-holder

      .. container:: card rocket

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;快速开始</h>
            <br><br>

         - `了解FISCO BCOS区块链 <./docs/introduction/introduction.html>`_
         - `FISCO BCOS 3.X新特性 <./docs/introduction/change_log/index.html>`_
         - `搭建第一个区块链网络 <./docs/quick_start/air_installation.html>`_
         - `开发第一个Solidity区块链应用 <./docs/quick_start/solidity_application.html>`_
         - `开发第一个webankblockchain-liquid区块链应用 <./docs/quick_start/wbc_liquid_application.html>`_

   .. container:: card-holder

      .. container:: card manuals

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;开发教程</h>
            <br><br>

         - `Air版本区块链网络搭建 <./docs/tutorial/air/index.html>`_
         - `Pro版本区块链网络搭建 <./docs/tutorial/pro/index.html>`_
         - `轻节点搭建 <./docs/tutorial/lightnode.html>`_
         - `FISCO BCOS Java SDK <./docs/sdk/java_sdk/index.html>`_

   .. container:: card-holder-bigger

      .. container:: card-bigger rocket

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用工具</h>
            <br><br>

         .. container:: tools

            .. raw:: html

               <img src="installation/build_chain.png" class="card-holder" >
               <div class="tools-holder">
                  <br>
                  <h style="font-size: 18px;"><b><a href="./docs/tutorial/air/build_chain.html">开发部署工具：区块链网络快速部署工具</a></b></h>
                  <br><br>
                  <p>开发部署工具是提供给开发者快速搭建FISCO BCOS区块链网络的脚本工具。</p>
               </div>
               <div style="clear:both"></div>

         .. raw:: html

            <hr>

         .. container:: tools

            .. raw:: html

               <div class="tools-holder">
                  <br>
                  <h style="font-size: 18px;"><b><a href="./docs/develop/console/index.html">命令行交互控制台：节点查询与管理工具</a></b></h>
                  <br><br>
                  <p>命令行交互控制台是提供给开发者使用的节点查询与管理的工具。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity合约文件编译为Java合约文件。</p>
               </div>
               <img src="installation/console.png" class="card-holder" >
               <div style="clear:both"></div>

         .. raw:: html

            <hr>

         .. container:: tools

            .. raw:: html

               <img src="webase/webase-web.png" class="card-holder">
               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/components/webase.html">图形化的区块链管理工具</a></b></h>
                  <br><br>
                  <p>WeBankBlockchain WeBASE(WeBank Blockchain Application Software Extension, 简称WBC-WeBASE) 是一套管理FISCO-BCOS联盟链的工具集。WBC-WeBASE提供了图形化的管理界面，屏蔽了区块链底层的复杂度，降低区块链使用的门槛，大幅提高区块链应用的开发效率，包含节点前置、节点管理、交易链路，数据导出，Web管理平台等子系统。</p>
               </div>
               <div style="clear:both"></div>
               <br><br>

         .. raw:: html

            <hr>

         .. container:: tools

            .. raw:: html

               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/components/data_index.html">数据治理通用组件：释放数据价值 </a></b></h>
                  <br><br>
                  <p>数据治理通用组件的全名是“WeBankBlockchain-Data数据治理通用组件”，它是一套稳定、高效、安全的区块链数据治理组件解决方案，可无缝适配FISCO BCOS区块链底层平台。它由数据导出组件(Data-Export)、数据仓库组件(Data-Stash)、数据对账组件(Data-Reconcile)这三款相互独立、可插拔、可灵活组装的组件所组成，开箱即用，灵活便捷，易于二次开发。</p>
               </div>
               <img src="governance/data/data-gov.png" class="card-holder">
               <div style="clear:both"></div>

            .. raw:: html

               <hr>

         .. container:: tools

            .. raw:: html

               <img src="governance/MCGF/MCGF_overview.png" class="card-holder">
               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/components/governance_index.html">区块链多方协作治理组件：开启治理实践新起点</a></b></h>
                  <br><br>
                  <p>WeBankBlockchain-Governance区块链多方协作治理组件是一套轻量解耦、简洁易用、通用场景和一站式的区块链治理组件解决方案。 首批开源的有账户治理组件(Governance-Account)、权限治理组件(Governance-Auth)、 私钥管理组件(Governance-Key)和证书管理组件（Governance-Cert）。上述组件都提供了可部署的智能合约代码、易于使用的SDK和可参考的落地实践Demo等交付物。</p>
               </div>
               <div style="clear:both"></div>

            .. raw:: html

               <hr>

         .. container:: tools

            .. raw:: html

               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/components/smartdev_index.html">区块链应用开发组件：助力低代码开发</a></b></h>
                  <br><br>
                  <p>WeBankBlockchain-SmartDev应用开发组件包含了一套开放、轻量的开发组件集，覆盖智能合约的开发、调试、应用开发等环节，包括智能合约库（SmartDev-Contract）、智能合约编译插件（SmartDev-SCGP）和应用开发脚手架（SmartDev-Scaffold）。开发者可根据自己的情况自由选择相应的开发工具，提升开发效率。</p>
               </div>
               <img src="governance/SmartDev/smartdev_overview.png" class="card-holder">
               <div style="clear:both"></div>


   .. container:: card-holder

      .. container:: card ref

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统设计</h>
            <br><br>

         - `系统架构 <./docs/design/architecture.html>`_
         - `两阶段并行拜占庭共识 <./docs/design/consensus/consensus.html>`_
         - `合约文件系统BFS <./docs/design/contract_directory.html>`_
         - `更多设计文档 <./docs/design/index.html>`_

   .. container:: card-holder

      .. container:: card rocket

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;社区资源</h>
            <br><br>

         - `Github主页 <https://github.com/FISCO-BCOS/FISCO-BCOS>`_
         - `贡献代码 <https://mp.weixin.qq.com/s/_w_auH8X4SQQWO3lhfNrbQ>`_
         - `反馈问题 <https://github.com/FISCO-BCOS/FISCO-BCOS/issues>`_
         - `应用案例集 <https://mp.weixin.qq.com/s/cUjuWf1eGMbG3AFq60CBUA>`_
         - `微信群 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/WeChatQR%2Ejpg>`_ 、`公众号 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/OfficialAccountsQR%2Ejpg>`_


   .. container:: card-holder-bigger

      .. container:: card-bigger rocket

         .. raw:: html

               <br>
               <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;更多开源工具</h>
               <br><br>

               <img src="_static/images/products.jpeg" class="card-holder-full">


         .. container:: tools

            .. raw:: html

            - **FISCO BCOS企业级金融联盟链底层平台**: `[GitHub] <https://github.com/FISCO-BCOS/FISCO-BCOS>`_ `[Gitee] <https://gitee.com/FISCO-BCOS>`_ `[文档] <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/index.html>`_
            - **区块链中间件平台**：`[GitHub] <https://github.com/WeBankFinTech/WeBASE>`_ `[Gitee] <https://gitee.com/WeBank/WeBASE>`_  `[文档] <https://webasedoc.readthedocs.io/>`_
            - **WeIdentity 基于区块链的实体身份标识及可信数据交换解决方案**: `[GitHub] <https://github.com/WeBankFinTech/WeIdentity>`_ `[Gitee] <https://gitee.com/WeBank/WeIdentity>`_ `[文档] <https://weidentity.readthedocs.io/>`_
            - **WeDPR 即时可用，场景式隐私保护高效解决方案套件和服务**：`[GitHub] <https://github.com/WeBankBlockchain/WeDPR-Lab-Core>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/WeDPR-Lab-Crypto>`_ `[文档] <https://wedpr-lab.readthedocs.io/>`_
            - **WeCross 区块链跨链协作平台**: `[GitHub] <https://github.com/WeBankBlockchain/WeCross>`_ `[Gitee] <https://gitee.com/WeBank/WeCross>`_ `[文档] <https://wecross.readthedocs.io/>`_
            - **Truora 可信预言机服务**：`[GitHub] <https://github.com/WeBankBlockchain/Truora>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Truora>`_  `[文档] <https://truora.readthedocs.io/>`_
            - **webankblockchain-liquid（简称WBC-Liquid）智能合约编程语言软件**：`[GitHub] <https://github.com/WeBankBlockchain/liquid>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/liquid>`_  `[文档] <https://liquid-doc.readthedocs.io/>`_
            - **WeBankBlockchain-Data 数据治理通用组件**：
               - Data-Stash 数据仓库组件： `[GitHub] <https://github.com/WeBankBlockchain/Data-Stash>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Data-Stash>`_  `[文档] <https://data-doc.readthedocs.io/zh_CN/stable/docs/WeBankBlockchain-Data-Stash/index.html>`_
               - Data-Export 数据导出组件： `[GitHub] <https://github.com/WeBankBlockchain/Data-Export>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Data-Export>`_  `[文档] <https://data-doc.readthedocs.io/zh_CN/stable/docs/WeBankBlockchain-Data-Export/index.html>`_
               - Data-Reconcile 数据对账组件：  `[GitHub] <https://github.com/WeBankBlockchain/Data-Reconcile>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Data-Reconcile>`_  `[文档] <https://data-doc.readthedocs.io/zh_CN/stable/docs/WeBankBlockchain-Data-Reconcile/index.html>`_
            - **WeBankBlockchain-Governance 多方治理协作组件**：
               - Governance-Account 账户治理组件： `[GitHub] <https://github.com/WeBankBlockchain/Governance-Account>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Governance-Account>`_  `[文档] <https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Acct/index.html>`_
               - Governance-Authority 权限治理组件：`[GitHub] <https://github.com/WeBankBlockchain/Governance-Authority>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Governance-Authority>`_  `[文档] <https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Auth/index.html>`_
               - Governance-Key 私钥管理组件： `[GitHub] <https://github.com/WeBankBlockchain/Governance-Key>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Governance-Key>`_  `[文档] <https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Key/index.html>`_
               - Governance-Cert 证书管理组件：`[GitHub] <https://github.com/WeBankBlockchain/Governance-Cert>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/Governance-Cert>`_  `[文档] <https://governance-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-Governance-Cert/index.html>`_
            - **WeEvent 基于区块链的分布式事件驱动架构**：`[GitHub] <https://github.com/WeBankFinTech/WeEvent>`_ `[Gitee] <https://gitee.com/WeBank/WeEvent>`_  `[文档] <https://weevent.readthedocs.io/>`_
            - **WeBankBlockchain-SmartDev 区块链应用开发工具**：
               - SmartDev-Contract 智能合约库组件：`[GitHub] <https://github.com/WeBankBlockchain/SmartDev-Contract>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/SmartDev-Contract>`_  `[文档] <https://smartdev-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Contract/index.html>`_
               - SmartDev-SCGP 合约编译插件：`[GitHub] <https://github.com/WeBankBlockchain/SmartDev-SCGP>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/SmartDev-SCGP>`_  `[文档] <https://smartdev-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-SCGP/index.html>`_
               - SmartDev-Scaffold 应用开发脚手架：`[GitHub] <https://github.com/WeBankBlockchain/SmartDev-Scaffold>`_ `[Gitee] <https://gitee.com/WeBankBlockchain/SmartDev-Scaffold>`_  `[文档] <https://smartdev-doc.readthedocs.io/zh_CN/latest/docs/WeBankBlockchain-SmartDev-Scaffold/index.html>`_


.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 平台介绍

   docs/introduction/introduction.md
   docs/introduction/key_feature.md
   docs/introduction/function_overview.md
   docs/introduction/change_log/index.rst

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 快速开始

   docs/quick_start/hardware_requirements.md
   docs/quick_start/air_installation.md
   docs/quick_start/solidity_application.md
   docs/quick_start/wbc_liquid_application.md

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 合约开发

   docs/contract_develop/solidity_develop.md
   docs/contract_develop/c++_contract/index.md
   docs/contract_develop/Liquid_develop.md
   docs/contract_develop/contracts_IDE_develop.md


.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: SDK教程

   docs/sdk/index.md
   docs/sdk/java_sdk/index.md
   docs/sdk/c_sdk/index.md
   docs/sdk/go_sdk/index.rst
   docs/sdk/python_sdk/index.rst
   docs/sdk/nodejs_sdk/index.rst
   docs/sdk/rust_sdk/index.md
   docs/sdk/cpp_sdk/index.md
   docs/sdk/csharp_sdk/index.md
   docs/sdk/cert_config.md

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 搭链教程

   docs/tutorial/air/index.md
   docs/tutorial/pro/index.md
   docs/tutorial/max/index.md
   docs/tutorial/lightnode.md
   docs/tutorial/compile_binary.md
   docs/tutorial/support_os.md
   docs/tutorial/docker.md

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 应用开发

   docs/develop/index.md
   docs/develop/api.md
   docs/develop/account.md
   docs/develop/contract_life_cycle.md
   docs/develop/console_deploy_contract.md
   docs/develop/console/index.md
   docs/develop/smartdev_index.md
   docs/develop/amop.md
   docs/develop/privacy.md

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 区块链运维

   docs/operation_and_maintenance/build_chain.md
   docs/operation_and_maintenance/light_monitor.md
   docs/operation_and_maintenance/console/index.md
   docs/operation_and_maintenance/storage_tool.md
   docs/operation_and_maintenance/data_archive_tool.md
   docs/operation_and_maintenance/webase.md
   docs/operation_and_maintenance/committee_usage.md
   docs/operation_and_maintenance/add_new_node.md
   docs/operation_and_maintenance/stress_testing.md
   docs/operation_and_maintenance/upgrade.md
   docs/operation_and_maintenance/data_index.md
   docs/operation_and_maintenance/governance_index.md
   docs/operation_and_maintenance/log/index.md
   docs/operation_and_maintenance/docs/operation_and_maintenance.md


.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 高阶功能使用

   docs/advanced_function/safety.md
   docs/advanced_function/wecross.md
   docs/advanced_function/distributed_identity.md
   docs/advanced_function/distributed_event.md
   docs/advanced_function/trusted_oracle.md
   docs/advanced_function/privacy_protect.md

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: FISCO BCOS设计原理

   docs/design/architecture.md
   docs/design/tx_procedure.md
   docs/design/protocol_description.md
   docs/design/consensus/index.rst
   docs/design/sync.md
   docs/design/storage/storage.md
   docs/design/parallel/index.md
   docs/design/virtual_machine/index.rst
   docs/design/committee_design.md
   docs/design/storage/storage_security.md
   docs/design/storage/archive.md
   docs/design/guomi.md
   docs/design/network_compress.md
   docs/design/security_control/index.rst
   docs/design/hsm.md
   docs/design/compatibility.md
   docs/design/cns_contract_name_service.md
   docs/design/contract_directory.md
   docs/design/boostssl.md
   docs/design/amop_protocol.md



.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 社区资源

   docs/community/pr.md
   docs/community/contributor_list_new.md
   docs/community/MVP_list_new.md
   dcos/community/partner_list_new.md
   docs/articles/index.md
   docs/community/join_fiscobcos.md
   docs/community/app_example.md
