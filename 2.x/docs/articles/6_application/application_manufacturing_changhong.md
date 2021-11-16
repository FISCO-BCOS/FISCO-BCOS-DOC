# 长虹启思实验室：制造业生产协同及质量溯源方案

作者：启思实验室

## 为什么选择生产协同及质量溯源场景？

我们（下文“我们”均指启思实验室团队）的作品是在生产制造数据信息化的基础上，通过区块链技术，在企业内部建立可信的、合约化信息流，再用多级链打通制造企业供应链和产品下游的数据信息管理，建立多个节点，实现制造企业规则合约执行与数据可信管理，从而达到降低成本、提升效益的效果。

说到这，很多朋友可能会好奇，我们为什么选择这个场景切入？首先，启思实验室（由四川长虹电器股份有限公司信息安全实验室的区块链研究小组的成员组成）本身就专注于区块链技术应用研究，且与主流区块链基础技术提供方合作，提供基于区块链的智慧家庭和工业互联网解决方案。同时，在整个工业互联网和5G发展驱动下，传统制造能力面临一次较大程度的迭代升级，团队注意力一直集中在生产制造，力争从中找到突破点，利用区块链技术助力这一波生产制造升级。其次，在传统制造业中，当产品出现质量问题时，往往会面临物料零部件质量无法溯源、难以追责的难点。具体痛点如下图所示：图中A公司为产品公司和销售公司；B公司为A公司产品的委托生产方；供应商C、供应商D和供应商E为A公司的指定物料供应商。

![](../../../images/articles/application_manufacturing_changhong/IMG_5273.PNG)

经过分析和研究，我们梳理了传统制造业中的几大需求：

**需求1：生产计划和物料匹配自动化**

B公司根据A公司的订单与现有成品库产品库存，分析出差额后制定生产计划。根据产品的生产数量计算出所需物料。生产物料除去物料库的库存后直接向供应商反馈出所需物料的数量，并且预付款项。

**需求2：物料供应商及时响应物料标识上链**

供应商C、供应商D、供应商E收到物料需求订单，准备发货入库。同时供应商C、供应商D、供应商E的物料匹配唯一标识，信息上链。

**需求3：财务可信清算**

在订单下发、物料采购、物流运送、订单交货等环节建立财务自动按约定清算的机制。

**需求4：售后质量溯源**

售后基于物料唯一标识的可信责任确权和质量溯源，做到及时响应。

## 底层选型的几点考量

了解需求后，下一步就是技术实现，在底层选型上，团队架构师康红娟主要从以下几点进行考量：

- 良好的实际可操作性。区块链应用层与实际业务贴合紧密，尤其是在合约层，重新开发部署业务型合约必然会带来反复调试。因此，底层的完备支持，对于项目成功非常重要。
- 完善的服务层功能组件。用户层与链层的交互，必须经过中间服务层的“嫁接”，而这些“嫁接模块”具备通用性，除了基本的链层功能外，服务层通用组件的完备也至关重要。
- 友好的开源氛围。对于技术极客而言，最大的快乐就是开源。

通过了解评估，团队最终选择了FISCO BCOS作为底层，主要是两方面的原因：

- FISCO BCOS是安全可控的国产开源联盟链，能很好满足、贴合国内企业的使用需求；
- 社区活跃度高，应用场景丰富，对于开发者的技术支持响应及时。

除此之外，FISCO BCOS还具备版本迭代及时、性能强劲、服务中间件丰富等优势。

## 智能合约解决方案

综上所述，我们从实际业务需求出发，结合区块链技术优势，构建“基础层、核心层、服务层、用户层”等四个关键层级，覆盖核心数据库、业务型合约、数据解析、消息解析、用户管理、业务管理等功能，构建工业互联网领域的订单式生产协同垂直解决方案。

本文我们重点分享其中的智能合约方案，方案如下图。这个方案里包括了产品合约、结算合约、生产合约、备货合约和授权合约，下面将对这几个合约逐一展开介绍。

![](../../../images/articles/application_manufacturing_changhong/IMG_5274.JPG)

### 产品合约

本合约用于完成产品生产过程中产品注册及产品所有权变更、产品溯源。

步骤如下:

- 完成对相关合约的关系设置；
- 合约的admin向某些address授权成为product生产商；
- product生产商调用updateProductPrice对产品价格进行设定；
- customer需要向生产商下产品生产订单前会先getProductPrice来比对价格的高低，然后通过payment合约向生产商下产品生产订单；
- 生产商通过payment合约获取到订单后，进行产品生产；
- 在生产产品前，生产商会通过Material合约来检查自己的产品原料储备是否充足，如果充足就消耗原料进行产品的生产注册registerProduct上链；
- 产品生产商进行交付，customer确认收到货品，确认订单，完成资金及产品所有权的变更。

```
// 厂商msg.sender更新自己productType产品的价格
function updateProductPrice(uint256 productType, uint256 newPrice) public
​
// 获取厂商 _to的productType的价格
function getProductPrice(address _to, uint256 productType) public view returns(uint256 price)
​
// 设置供应商的原材料合约
function setMaterialContract(address _materialContract) public onlyOwner
​
// 设置支付合约
function setPaymentContract(address _paymentContract) public onlyOwner
​
// 产品生产商设置自己产品的批次号，ID，所用材料批次等信息
function registerProduct(uint256 productType, uint256 id, uint256 batchNumber, uint256[] memory materialBatches) public
​
// 更换产品所有权，交付产品
function transferProducts(address from, address to, uint256 productType, uint256 count) public
​
// 获取产品详情
function details(uint256 id) public view returns(Product memory)
​
// 获取msg.sender所拥有的产品数组
function getMyProducts(uint256 productType) public view returns(uint256[] memory myProductIDs)
​
// 获取某种产品的原材料用于哪些产品了
function trace(uint256 materialBatchNum) public view returns(uint256[] memory ids)
```

### 结算合约

负责用户资产，具体步骤如下：

- 余额与预付款队列；
- 产品所有权变化(手动确认)过程中的自动结算；
- 资金如果不足，不能进行诸如备货，入库等的操作；
- 充值/直接消费/预付款, 余额查询等相关功能。

### 生产合约

拥有库存生产个数和已拥有产品队列, 和原材料批次队列：

- 获取待生产订单，判断原料库存是否满足需求，不满足则通过结算合约向备货合约预付款下生产订单；
- 通过计算原材料队列，外部唯一标识生成，将原材料的批次信息写入产品进行生产(外部)、入库(调用产品合约的生成入口)。对库存生产个数和已拥有产品队列进行维护；
- 出库(外部调用产品合约的产品所有权变更入口)，对本合约的生产个数和已拥有产品队列库存合约的已拥有产品队列进行维护，通过结算合约进行结算。

### 备货合约

负责对上游厂商进行备货：

- 通过调用生产备货数对厂家B进行备货，计算已有零件数，外部调用零件入库(填入批次，数量等信息)；
- 出库(外部调用)，将零件信息的批次信息写入到厂家B, 并维护自身数据队列，完成后自动调用结算合约进行结算。

补充：因为零件不像产品一物一码，多个零件只对应到某个批次就可以，不需要单独的零件合约对零件进行维护。

### 授权合约

通过不同的权限等级，对各个合约中的函数进行访问限制，各种合约间角色的约定及调用权限。比如普通用户没有权限去向供货商下订单，而供货商也不可能向普通用户提供生产完成的商品。

```
// address是否有role权限
function hasRole(bytes32 role, address account) public view returns (bool)
​
// 获取role权限的成员数
function getRoleMemberCount(bytes32 role) public view returns (uint256)
​
// 根据role和index获取成员
function getRoleMember(bytes32 role, uint256 index) public view returns (address)
​
// 获取role角色的管理者
function getRoleAdmin(bytes32 role) public view returns (bytes32)
​
// 向某个account授予role权限
function grantRole(bytes32 role, address account) public virtual
​
// 移除account的role权限
function revokeRole(bytes32 role, address account) public virtual
​
// account放弃持有的role权限
function revokeRole(bytes32 role, address account) public virtual
```

## 智能合约解决方案

整个智能合约开发过程中，最主要其实是对整个生产流程的梳理。首先我们先进行了需求梳理，然后针对solidity语言开发出一个可用的版本，在此基础上对相关调用建立一个简单的restful服务器，完成对各个接口的测试，有一套完整的可演示流程，最后进行相关开发完成对合约的复现。