# 权限控制
## 1 权限控制介绍

与可自由加入退出、自由交易、自由检索的公有链相比，联盟链有准入许可、交易多样化、基于商业上隐私及安全考虑、高稳定性等要求。因此，联盟链在实践过程中需强调“权限”及“控制”的理念。

为体现“权限”及“控制”理念，FISCO BCOS平台基于分布式存储，提出分布式存储权限控制的机制，可以灵活，细粒度的方式进行有效的权限控制，为联盟链的治理提供重要的技术手段。分布式权限控制基于外部账户(tx.origin)的访问机制，对包括合约部署，表的创建，表的写操作（插入、更新和删除）进行权限控制，表的读操作不受权限控制。 在实际操作中，每个账号使用独立且唯一的公私钥对，发起交易时使用其私钥进行签名，接收方可通过公钥验签知道交易具体是由哪个账号发出，实现交易的可控及后续监管的追溯。     

## 2 权限控制规则
权限控制规则如下：    
1. 权限控制的最小粒度为表，基于外部账号进行控制。     
2. 使用白名单机制，未配置权限的表，默认完全放开，即所有外部账户均有读写权限。    
3. 权限设置利用权限表（\_sys_table_access_）。权限表中设置表名和外部账户地址，则表明该账户对该表有读写权限，设置之外的账户对该表仅有读权限。

## 3 权限控制分类

分布式存储权限控制分为对用户表和系统表的权限控制。用户表指用户合约所创建的表，用户表均可以设置权限。系统表指FISCO BCOS区块链网络内置的表，系统表的设计详见[存储文档](../storage/index.rst)。系统表的权限控制如下所示：   

|表名|权限控制是否生效|表存储数据说明|权限控制意义|
|:---|:------|:---------|:----|
|**\_sys_tables_**|**是**|存储所有表的结构|控制部署合约和创建表|
|**\_sys_table_access_**|**是**|存储权限控制信息|控制权限功能设置|
|**\_sys_miners_**|**是**|存储共识节点和观察节点的列表|控制节点类型设置|
|**\_sys_cns_**|**是**|存储cns列表|控制使用CNS|
|**\_sys_config_**|**是**|存储系统配置的列表|控制系统配置设置|
|`_sys_current_state_`|否|存储最新的状态| |
|`_sys_tx_hash_2_block_`|否|存储交易hash到区块号的映射| |
|`_sys_number_2_hash_`|否|存储区块号到区块头hash的映射| |
|`_sys_hash_2_block_`|否|存储头hash到序列化区块的映射| |

针对操作的系统表，其逻辑性错误定义如下：   
其中code是返回码（code大于等于0表示操作成功，其code值为成功操作的记录数），msg是返回码的描述信息。   

|table|code|msg|
|:---|:------|:---------|
|**\_sys_table_access_**| -30| table name and address exist|
|**\_sys_table_access_**| -31| table name and address does not exist|
|**\_sys_miners_**| -40 |invalid nodeID |
|**\_sys_miners_**| -41 |last miner cannot be removed |
|**\_sys_miners_**| -42 |nodeID is not in network |
|**\_sys_miners_**| -43 |nodeID is not in group peers |
|**\_sys_miners_**| -44 |nodeID is already in miner list |
|**\_sys_miners_**| -45 |nodeID is already in observer list |
|**\_sys_cns_**| -50 |address and version exist|
|**\_sys_config_**| -60 | invalid configuration values|

无权限属于系统性错误，code定义-1，msg定义为“non-authorized”。

## 3 数据定义
1. 权限信息以系统表的方式进行存储，权限表表名为_sys_table_access_，其字段信息定义如下：


| 字段   | 类型    | 是否为空   | 主键 | 描述    |
| ------- | ------- | ------ | --- | ----------|
| table_name  | string  | No     | PRI | 表名称   |
| address | string  | No     |     | 外部账户地址  |
| enable_num | string  | No    |     | 权限设置生效区块高度    |
| \_status\_| string  | No     |     | 分布式存储通用字段，“0”表示可用，“1”表示移除  |

**注**：对权限表控制的信息也保存在权限表中。   

2. 权限信息结构体定义：
```
struct AccessOptions : public std::enable_shared_from_this<AccessOptions>
{
    typedef std::shared_ptr<AccessOptions> Ptr;
    AccessOptions() = default;
    AccessOptions(Address _origin) { origin = _origin; }
    Address origin;
}
```

## 4 接口描述
合约接口
```
pragma solidity ^0.4.10;

contract Authority {
    function insert(string tableName, string addr) public returns(int);
    function remove(string tableName, string addr) public returns(int);
    function queryByName(string table_name) public constant returns(string);
}
```
- insert接口：参数为表名和外部账户地址，返回插入的记录数。提供权限控制信息上链功能。insert接口不会插入重复记录，对权限表的记录只增不改。
- remove接口：参数为表名和外部账户地址，返回移除的记录数。移除表示将
\_status\_字段设置为1。
- queryByName接口：参数为表名，返回查询的记录。

## 5 流程图

#### 5.1 用户表权限控制流程
外部账户查询表不进行权限控制。当需要更新，增加或移除记录时，将通过查询权限表进行权限控制。流程如下图所示。
![ac1.png](../../../images/authority/ac1.png)

#### 5.2 系统表权限控制流程
对于sdk层，用户合约不可以直接操作权限表，通过sdk的AuthorityService接口（详见[sdk文档](../api/sdk.md)）和控制台（详见[控制台文档](../../manual/console.md)）可以操作系统表。对于C++底层，当需要操作权限表时，通过AuthorityPreCompiled进行权限表的操作。其中查询权限表不需要检查权限，新增和移除权限表的记录需要检查权限。整个系统内权限相关的增删查将通过AuthorityPreCompiled进行维护。所有权限内容记录在区块链上。交易请求发起后，系统将访问_sys_table_access_表查询该交易发起方是否有对应的权限。如果具有权限，执行交易；如果不具备，则返回无权限操作提示。
![ac2.png](../../../images/authority/ac2.png)

**注：** _sys_miners_表（ConsensusPrecompiled），_sys_cns_表（CNSPrecompiled），_sys_config_表（SystemConfigPrecompiled）控制流程与对权限表的控制流程类似。

## 6 权限控制工具
### 控制台权限控制命令
FISCO BCOS的分布式存储权限控制通过权限表来管理。通过提供控制台命令对权限表进行读写操作（针对开发者，可以调用sdk的AuthorityService接口操作权限表），其中有三个命令涉及权限表，如下所示。

|AuthorityService API|命令全称（缩写）|命令参数|含义|
|:--- |:---|:------|:---------|
|String add(String tableName, String addr) |addAuthority(aa)| table_name address | 增加控制的表名和外部账户地址
|String remove(String tableName, String addr) |removeAuthority(ra)| table_name address | 移除控制的表名和外部账户地址
|List<AuthorityInfo> query(String tableName) |queryAuthority(qa)| table_name |根据表名查询权限设置记录|
	
**注：**
 表名可以是用户表和系统表(\_sys_tables_, \_sys_table_access_, \_sys_miners_, \_sys_cns_和_sys_config_)的表名。

## 7 代码修改范围

#### 7.1 涉及Executive修改
**① call方法修改：**
Executive::call(CallParameters const& _p, u256 const& _gasPrice, Address const& _origin)方法在调用m_envInfo.precompiledEngine()->call(_origin, _p.codeAddress, _p.data)时将外部账户地址_origin传入，以供MemoryTable的update, insert, remove接口获取并判断权限。

**② executeCreate方法修改：**
Executive::executeCreate(Address const& _sender, u256 const& _endowment, u256 const& _gasPrice,u256 const& _gas, bytesConstRef _init, Address const& _origin)是创建账户，部署合约的入口。在方法执行之前加入权限判断。判断代码如下：
```
    auto memeryTableFactory = m_envInfo.precompiledEngine()->getMemoryTableFactory();
    auto table = memeryTableFactory->openTable(SYS_TABLES);
    if (!table->checkAuthority(_origin))
    {
        LOG(WARNING) << "deploy contract checkAuthority of " << _origin.hex() << " failed!";
        m_gas = 0;
        m_excepted = TransactionException::PermissionDenied;
        revert();
        m_ext = {};
        return !m_ext;
    }
```

#### 7.2 涉及StorageState修改

**① StateFace增加判断权限的接口:**  
virtual void checAuthority(Address _origin, Address _contract) const = 0;   
ExtVM的setStore方法利用StateFace的checAuthority接口进行权限判断，判断无权限则抛异常。新增PermissionDenied异常, 在Executive的go方法中捕获，并回滚交易。    
**② StateFace的子类修改：**  
StorageState实现checAuthority接口，用于权限判断。          
MPTState空实现checAuthority接口，MPTState不涉及权限判断。

#### 7.3 涉及Precompile权限修改

**①1. 修改ExecutiveContext和Precompiled的call方法，增加外部账户参数**   
将call(Address address, bytesConstRef param)改为call(Address const& origin, Address address, bytesConstRef param)  
**② 修改MemoryTableFactory和MemoryTable**   
MemoryTableFactory增加setAuthorizedAddress接口，用于将查询的权限地址设置到MemoryTable。 MemoryTable中的update, insert, remove接口均增加AccessOptions参数。MemoryTable中增加checAuthority方法，用于访问update, insert, remove接口时进行权限判断。判断无权限则返回-1。

#### 7.4 涉及权限表的操作
新增AuthorityPrecompiled，提供插入、移除和查询权限表的方法。

## 8 权限控制示例

参考[权限控制使用文档](../../manual/priority_control.md)
