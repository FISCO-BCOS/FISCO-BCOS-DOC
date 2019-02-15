FAQ
==========

版本相关
""""""""""""""""

:问:
  FISCO BCOS v2.0与之前版本有哪些变化？
:答:
  请 :doc:`参考这里 <whats_new>`。

:问:
  FISCO BCOS v2.0都有哪些API？开发者如何与FISCO BCOS平台交互？
:答:
  1. FISCO BCOS v2.0提供jsonrpc接口，具体请 :doc:`参考这里 <api>`。
  2. FISCO BCOS v2.0提供Java SDK帮助开发者快速实现应用，具体请 :doc:`参考这里 <sdk/index>`。
  3. FISCO BCOS v2.0提供控制台帮助用户快速了解使用FISCO BCOS，具体请 :doc:`参考这里 <manual/console>`。

:问:
  FISCO BCOS v2.0如何搭建？
:答:
  - 建链脚本：使用简单，适合第一次接触FISCO BCOS的同学，具体请 :doc:`参考这里 <manual/build_chain>`。
  - FISCO-Generator：更灵活，适合熟悉FISCO BCOS的用户和企业用户，具体请 :doc:`参考这里 <enterprise/index>`。

:问:
  FISCO BCOS v2.0的智能合约与之前版本合约有什么不同，兼容性如何？
:答:
  FISCO BCOS v2.0支持最新的Solidity合约，同时增加了precompare合约，具体请 :doc:`参考这里 <developer/index>`。

:问:
  国密和普通版本的区别有哪些？
:答:
  编译版本，证书，落盘加密，solidity编译java，web3sdk使用，具体请 :doc:`参考这里 <manual/guomi>`。

:问:
  是否支持从1.3或1.5升级到2.0版本？  
:答:
  不支持。

控制台
""""""""""""""""
:问:
  控制台指令区分大小写吗？
:答:
  只支持小写。

:问:
  加入共识列表或观察者列表报错，nodeID is not in network，为什么？
:答:
  节点加入共识列表和观察者列表的节点必须是连接peer的nodeID列表里面的成员。

:问:
  删除节点操作报错，nodeID is not in group peers，为什么？
:答:
  节点删除操作中的节点必须是getGroupPeers里面展示的group的peers。

:问:
  游离节点（非群组节点）是否可以同步group数据？
:答:
  游离节点不参与group内的共识、同步和出块，游离节点可以通过am/ao命令可以将退出的节点添加为共识/观察节点。

:问:
  统一节点属于不同的group，是否可以支持查询多group的信息。
:答:
   可以，在进入控制台时，输入要查看的groupID:  ./web3sdk -c [groupID]

FISCO BCOS使用
""""""""""""""""

:问:
  系统配置、群组配置、节点配置分别指什么？
:答:
  系统配置是指节点配置中一些影响账本功能，并需账本节点共识的配置项。群组配置指节点所属的群组的相关配置，节点的每个群组都有独立的配置。节点配置指所有可配置项。

:问:
  群组配置都是可改的吗？
:答: 从配置项是否可改的维度，分为

  - 节点首次启动生成创世块后不能再修改。这类配置放置于group.x.genesis文件，其中x表示组编号，全链唯一。
  - 通过发交易修改配置项实现账本内一致。
  - 修改自身配置文件后，节点重启生效。这类配置放置于group.x.ini文件。群组配置改后重启可改项就是本地配置，nodeX/conf下的group.*.ini文件，更改重启生效。涉及配置项为[tx_pool].limit（交易池容量），[consensus].ttl(节点转发数)。  

:问:
  群组配置用户可以改的涉及哪些配置？
:答: 群组可修改配置分为共识可改配置和手工可改配置

  - 共识可改配置：全组所有节点相同，共识后生效。[consensus].max_trans_num,[consensus].node.X,[tx].gas_limit。
  - 手工可改配置：group.x.ini文件中，修改后重启生效，只影响节点。配置项有[tx_pool].limit。

:问:
  群组共识可改配置如何更改、查询？
:答: 共识可改配置可以通过控制台修改。共识可改配置项查询除了控制台外，还可以通过RPC接口查询，具体请 :doc:`参考这里 <api/rpc>`。

  + [consensus].max_trans_num，[tx].gas_limit使用接口setSystemConfigByKey(ssc)更改，对于的配置项为tx_count_limit，tx_gas_limit。具体参见ssc -h 。
  + [consensus].node.X的更改涉及到节点管理，控制台接口涉及到addMiner(am)，addObserver(ao)，removeNode(rn)，具体参考《节点管理》。

:问:
  群组观察节点和共识节点有什么区别？
:答:
  观察节点能同步群组数据，但不能参与共识。共识节点除了具有观察者权限，还参与共识。

:问:
  如何将合约纳入CNS管理？
:答:
  在部署合约时，调用CNS合约接口，将合约name、version、address信息写入CNS表中

:问:
  如何查询合约CNS表？
:答:
  通过web3sdk控制台指令查询，查询指令根据合约name查询。

Java SDK
""""""""""""""""

:问:
  Java SDK对Java版本有要求吗？
:答:
  推荐使用oracle jdk 1.8（open-jdk 1.8在某些操作系统上会缺少椭圆曲线的包。） 

:问:
  Java SDK配置完成，发送交易失败的原因是什么？  
:答:
  applicationContext.xml中的ip、端口、群组号填错或者是缺少节点的ca.crt、node.crt和node.key文件。
