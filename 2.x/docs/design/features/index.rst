##############################################################
其他特性
##############################################################

标签：``设计方案`` ``特性`` ``区块结构`` ``消息包`` ``交易结构``

----

为了提供更好的智能合约调用体验、支持更高的安全性，FISCO BCOS引入了合约命名服务(Contract Name Service, CNS)、国密算法和落盘加密特性。

- **合约命名服务(Contract Name Service, CNS)**

以太坊基于智能合约地址调用合约，存在如下问题：

- 合约abi为较长的JSON字符串，调用方无法直接感知

- 合约地址为20字节的魔数，不方便记忆，若丢失后将导致合约不可访问

- 约重新部署后，一个或多个调用方都需更新合约地址

- 不便于进行版本管理以及合约灰度升级

FISCO BCOS引入的合约命名服务CNS通过提供链上合约名称与合约地址映射关系的记录及相应的查询功能，方便调用者通过记忆简单的合约名来实现对链上合约的调用。


- **国密算法**


为了充分支持国产密码学算法，FISCO BCOS基于 `国产密码学标准 <http://www.gmbz.org.cn/main/bzlb.html>`_ ，实现了国密加解密、签名、验签、哈希算法、国密SSL通信协议，并将其集成到FISCO BCOS平台中，实现对 **国家密码局认定的商用密码** 的完全支持。


- **落盘加密特性**

考虑到联盟链的架构中，数据在联盟链的各个机构内是可见的，FISCO BCOS引入了落盘加密特性，对存储到节点数据库中的数据进行加密，并引入Key Manager保存加密密钥，保障了节点数据的机密性。


.. toctree::
   :maxdepth: 1

   cns_contract_name_service.md
   guomi.md
   storage_security.md
   network_compress.md
   contract_management.md
   account_management.md
   stat.md
   flow_control.md
