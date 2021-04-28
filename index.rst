##############################################################
FISCO BCOS 技术文档
##############################################################

.. image:: _static/images/FISCO_BCOS_Logo.svg

FISCO BCOS 是一个稳定、高效、安全的区块链底层平台，经过多家机构、多个应用，长时间在生产环境运行的实际检验。

.. container:: row 
   
   .. container:: card-holder
      
      .. container:: card rocket

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;快速开始</h>
            <br><br>
         
         - `了解FISCO BCOS区块链 <./docs/introduction.html>`_
         - `FISCO BCOS 2.X新特性 <./docs/change_log/index.html#fisco-bcos-2-x>`_
         - `搭建第一个区块链网络 <./docs/installation.html>`_
         - `开发第一个区块链应用 <./docs/tutorial/sdk_application.html>`_

   .. container:: card-holder
      
      .. container:: card manuals

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;开发手册</h>
            <br><br>
         
         - `关键概念 <./docs/tutorial/key_concepts.html>`_
         - `区块链网络搭建 <./docs/blockchain_dev/index.html>`_
         - `区块链应用开发 <./docs/app_dev/index.html>`_
         - `FISCO BCOS Java SDK <./docs/sdk/java_sdk/index.html>`_
         - `问题排查 <./docs/faq/index.html>`_

   .. container:: card-holder-bigger
      
      .. container:: card-bigger rocket

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;使用工具</h>
            <br><br>

         .. container:: tools 
         
            .. raw:: html
         
               <img src="_images/build_chain.png" class="card-holder" >
               <div class="tools-holder">
                  <br>
                  <h style="font-size: 18px;"><b><a href="./docs/manual/build_chain.html">开发部署工具：区块链网络快速部署工具</a></b></h>
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
                  <h style="font-size: 18px;"><b><a href="./docs/console/index.html">命令行交互控制台：节点查询与管理工具</a></b></h>
                  <br><br>
                  <p>命令行交互控制台是提供给开发者使用的节点查询与管理的工具。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity合约文件编译为Java合约文件。</p>
               </div>
               <img src="_images/console.png" class="card-holder" >
               <div style="clear:both"></div>

         .. raw:: html

            <hr>

         .. container:: tools 
         
            .. raw:: html
         
               <img src="_images/webase-web.png" class="card-holder">
               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/webase/webase.html">WeBASE：图形化的区块链管理工具</a></b></h>
                  <br><br>
                  <p>WeBASE(WeBank Blockchain Application Software Extension) 是一套管理FISCO-BCOS联盟链的工具集。WeBASE提供了图形化的管理界面，屏蔽了区块链底层的复杂度，降低区块链使用的门槛，大幅提高区块链应用的开发效率，包含节点前置、节点管理、交易链路，数据导出，Web管理平台等子系统。</p>
               </div>
               <div style="clear:both"></div>
               <br><br>

         .. raw:: html

            <hr>

         .. container:: tools 

            .. raw:: html
         
               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/browser/index.html">区块链浏览器：区块链数据浏览工具</a></b></h>
                  <br><br>
                  <p>区块链浏览器将区块链中的数据可视化，并进行实时展示。方便用户以Web页面的方式，浏览当前区块链中的信息。本浏览器版本适配FISCO BCOS 2.0+，关于2.0+版本的特性可以参考此链接。在使用本浏览器之前需要先理解2.0+版本的群组特性，详情可以参考此链接。</p>
               </div>
               <img src="_images/overview.png" class="card-holder" >
               <div style="clear:both"></div>
               <br><br>

         .. raw:: html
            
            <hr>

         .. container:: tools 

            .. raw:: html

               <img src="_images/toolshow.png" class="card-holder">
               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/enterprise_tools/index.html">运维部署工具：企业级用户的区块链管理工具</a></b></h>
                  <br><br>
                  <p>运维部署工具是为企业用户提供的部署、管理和监控多机构多群组联盟链的便捷工具。它面向于真实的多机构生产环境，为了保证机构的密钥安全，运维部署工具提供了一种机构间相互合作部署联盟链方式。</p>
               </div>
               <div style="clear:both"></div>

         .. raw:: html
            
            <hr>

         .. container:: tools 

            .. raw:: html

               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/webank_blockchain/data_index.html">数据治理通用组件：释放数据价值 </a></b></h>
                  <br><br>
                  <p>数据治理通用组件的全名是“WeBankBlockchain-Data数据治理通用组件”，它是一套稳定、高效、安全的区块链数据治理组件解决方案，可无缝适配FISCO BCOS区块链底层平台。它由数据导出组件(Data-Export)、数据仓库组件(Data-Stash)、数据对账组件(Data-Reconcile)这三款相互独立、可插拔、可灵活组装的组件所组成，开箱即用，灵活便捷，易于二次开发。</p>
               </div>
               <img src="_images/data-gov.png" class="card-holder">
               <div style="clear:both"></div>

            .. raw:: html
            
               <hr>

         .. container:: tools 

            .. raw:: html

               <img src="_images/MCGF_overview.png" class="card-holder">
               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/webank_blockchain/governance_index.html">区块链多方协作治理组件：开启治理实践新起点</a></b></h>
                  <br><br>
                  <p>WeBankBlockchain-Governance区块链多方协作治理组件是一套轻量解耦、简洁易用、通用场景和一站式的区块链治理组件解决方案。 首批开源的有账户治理组件(Governance-Account)、权限治理组件(Governance-Auth)、 私钥管理组件(Governance-Key)和证书管理组件（Governance-Cert）。上述组件都提供了可部署的智能合约代码、易于使用的SDK和可参考的落地实践Demo等交付物。</p>
               </div>
               <div style="clear:both"></div>

            .. raw:: html
            
               <hr>

         .. container:: tools 

            .. raw:: html

               <div class="tools-holder">
                  <h style="font-size: 18px;"><b><a href="./docs/webank_blockchain/smartdev_index.html">区块链应用开发组件：助力低代码开发</a></b></h>
                  <br><br>
                  <p>WeBankBlockchain-SmartDev应用开发组件包含了一套开放、轻量的开发组件集，覆盖智能合约的开发、调试、应用开发等环节，包括智能合约库（SmartDev-Contract）、智能合约编译插件（SmartDev-SCGP）和应用开发脚手架（SmartDev-Scaffold）。开发者可根据自己的情况自由选择相应的开发工具，提升开发效率。</p>
               </div>
               <img src="_images/smartdev_overview.png" class="card-holder">
               <div style="clear:both"></div>
               

   .. container:: card-holder
      
      .. container:: card ref

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统设计</h>
            <br><br>
         
         - `整体架构 <./docs/design/architecture/index.html>`_
         - `共识算法 <./docs/design/consensus/index.html>`_
         - `交易并行 <./docs/design/parallel/dag.html>`_
         - `分布式存储 <./docs/design/storage/index.html>`_
         - `预编译合约 <./docs/design/virtual_machine/precompiled.html>`_
         - `更多设计文档 <./docs/design/index.html>`_

   .. container:: card-holder
      
      .. container:: card rocket

         .. raw:: html

            <br>
            <h style="font-size: 22px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;更多资源</h>
            <br><br>
         
         - `Github主页 <https://github.com/FISCO-BCOS/FISCO-BCOS>`_
         - `深度解析系列文章 <http://mp.weixin.qq.com/mp/homepage?__biz=MzA3MTI5Njg4Mw==&hid=2&sn=4f6d7251fbc4a73ed600e1d6fd61efc1&scene=18#wechat_redirect>`_
         - `贡献代码 <https://mp.weixin.qq.com/s/_w_auH8X4SQQWO3lhfNrbQ>`_
         - `反馈问题 <https://github.com/FISCO-BCOS/FISCO-BCOS/issues>`_
         - `应用案例集 <https://mp.weixin.qq.com/s/cUjuWf1eGMbG3AFq60CBUA>`_
         - `微信群 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/WeChatQR%2Ejpg>`_ 、`公众号 <https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/release-2/images/community/OfficialAccountsQR%2Ejpg>`_



.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 平台介绍

   docs/introduction.md
   docs/change_log/index.rst

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 快速开始

   docs/installation.md
   docs/tutorial/sdk_application.md

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 构建和管理区块链网络

   docs/tutorial/key_concepts.md
   docs/blockchain_dev/env.rst
   docs/blockchain_dev/config.rst
   docs/blockchain_dev/maintain.rst

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 开发区块链应用

   docs/manual/account.md
   docs/app_dev/index.rst
   docs/sdk/index.rst
   docs/api.md
   docs/manual/amop_protocol.md
   docs/faq/index.rst

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 使用工具

   docs/manual/build_chain.md
   docs/console/index.rst
   docs/webase/webase.md
   docs/browser/index.rst
   docs/enterprise_tools/index.md
   docs/webank_blockchain/data_index.md
   docs/webank_blockchain/governance_index.md
   docs/webank_blockchain/smartdev_index.md

.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 系统设计
   
   docs/design/architecture/index.rst
   docs/design/tx_procedure.md
   docs/design/protocol_description.md
   docs/design/index.rst


.. toctree::
   :hidden:
   :maxdepth: 1
   :caption: 更多参考资料

   docs/articles/index.rst
   docs/community.md