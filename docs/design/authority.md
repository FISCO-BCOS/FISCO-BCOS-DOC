## 权限控制
## 1 权限控制介绍

与可自由加入退出、自由交易、自由检索的公有链相比，联盟链有准入许可、交易多样化、基于商业上隐私及安全考虑、高稳定性等要求。因此，联盟链在实践过程中需强调“权限”及“控制”的理念。

为体现“权限”及“控制”理念，FISCO BCOS平台基于分布式存储，提出分布式存储权限控制的机制，可以灵活，细粒度的方式进行有效的权限控制，为联盟链的治理提供重要的技术手段。分布式权限控制基于外部账户(tx.origin)的访问机制，对包括合约部署，表的创建，表的写操作（插入、更新和删除）进行权限控制。表的读操作不受权限控制。 在实际操作中，每个账号使用独立且唯一的公私钥对，发起交易时使用其私钥进行签名，接收方可通过公钥验签知道交易具体是由哪个账号发出，实现交易的可控及后续监管的追溯。     

## 2 权限控制规则
权限控制规则如下：    
1. 权限控制的最小粒度为表，基于外部账号进行控制。     
2. 使用白名单机制，未配置权限的表，默认完全放开，即所有外部账户均有读写权限。    
3. 权限设置利用权限表（\_sys_table_access_）。权限表中设置表名和外部账户地址，则表明该账户对该表有读写权限，设置之外的账户对该表仅有读权限。

## 3 权限控制分类

分布式存储权限控制分为对用户表和系统表的权限控制。用户表指用户合约所创建的表，用户表均可以设置权限。系统表指FISCO BCOS区块链网络内置的表，系统表的设计详见[存储文档](./storage.md)。系统表的权限控制如下所示：   

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
![ac1.png](../../images/authority/ac1.png)

#### 5.2 系统表权限控制流程
对于sdk层，用户合约不可以直接操作权限表，通过sdk的AuthorityService接口（详见[sdk文档](../web3sdk/introduction.md)）和sdk的控制台（详见[控制台文档](../manual/console.md)）可以操作系统表。对于C++底层，当需要操作权限表时，通过AuthorityPreCompiled进行权限表的操作。其中查询权限表不需要检查权限，新增和移除权限表的记录需要检查权限。整个系统内权限相关的增删查将通过AuthorityPreCompiled进行维护。所有权限内容记录在区块链上。交易请求发起后，系统将访问_sys_table_access_表查询该交易发起方是否有对应的权限。如果具有权限，执行交易；如果不具备，则返回无权限操作提示。
![ac2.png](../../images/authority/ac2.png)

**注：** _sys_miners_表（ConsensusPrecompiled），_sys_cns_表（CNSPrecompiled），_sys_config_表（SystemConfigPrecompiled）控制流程与对权限表的控制流程类似。

## 6 权限控制工具
### 控制台权限控制命令
FISCO BCOS的分布式存储权限控制通过权限表来管理。通过提供控制台命令对权限表进行读写操作（针对开发者，可以调用sdk的AuthorityService接口操作权限表），其中有三个命令涉及权限表，如下所示。
|AuthorityService API|命令全称（命令缩写）|命令参数|含义|
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

## 8 权限测试案例

### 测试客户端工具
提供权限控制测试客户端工具[bcosclient_authority.tar.gz](https://idisk.weoa.com/l/yo7MYo)，该测试程序可以指定三个外部账户进行部署合约、创建用户表t_test以及对用户表t_test进行增删改查操作。其中三个账户的外部账户分别如下：
```
1： tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
2： tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
3： tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed
```
客户端工具使用参见使用说明文档。

### 8.1 _sys_tables_测试
_sys_tables_表保存所有表的字段信息，用于控制表的创建操作。创建合约需要对_sys_tables_表拥有写权限，因此将权限设置于_sys_tables_表可以控制合约的部署和表的创建。    

#### 8.1.1 默认权限测试
权限表默认没有设置关于_sys_tables_表的权限信息，因此所有外部账户均可部署合约和创建表。   
#### 8.1.1.1 合约部署测试
首先登录sdk控制台，查询_sys_tables_表的权限设置内容。
```
> qa _sys_tables_
Empty set.
```
确认初始状态_sys_tables_表没有设置权限信息，默认所有外部账户均可以部署合约。   
**外部账户1部署合约：** 
```
$ ./run.sh 1 1 deploy
1 1 deploy
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
deploy contract address: 0x9e116ecf100be281ae9587c907cf5b450d51af1b
deploy contract successful!
```
外部账户1部署合约成功。   
**外部账户2部署合约：** 
```
$ ./run.sh 2 1 deploy
2 1 deploy
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
deploy contract address: 0xa5b85cf3728a15b67572af9b180e2ab4e449359a
deploy contract successful!
```
外部账户2部署合约成功。        
**外部账户3部署合约：** 
```
$ ./run.sh 3 1 deploy
3 1 deploy
tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed
deploy contract address: 0xd4ebb24ac68263e92335977c7ea968d5e770eb07
deploy contract successful!
```
外部账户3部署合约成功。  

#### 8.1.1.2 创建用户表测试
**外部账户1创建用户表t_test:**
```
$ ./run.sh 1 1 create
1 1 create
create t_test table completed.
```
外部账户1创建t_test表成功，表明有权限创建用户表。类似的，外部账户2和3也可也创建用户表t_test。   
**注意:** 因为测试客户端中的测试合约是创建t_test表，FISCO BCOS不会创建重复的表，因此重复创建已有的表会失败。测试多账户创建同一张表，可以删除节点数据（恢复链的初始状态）后再测试。

#### 8.1.2 设置权限测试
登录sdk控制台，设置外部账户1可以对_sys_tables_表进行读写操作。
```
> aa _sys_tables_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}
> qa _sys_tables_
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      1                      |
---------------------------------------------------------------------------------------------
```
#### 8.1.2.1 合约部署测试
**外部账户1部署合约：** 
```
$ ./run.sh 1 1 deploy
1 1 deploy
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
deploy contract address: 0x31877d5864125b3aa3a5ae60022274d1a4130d00
deploy contract successful!
```
外部账户1部署合约成，有权限部署合约。   
**外部账户2部署合约：** 
```
$ ./run.sh 2 1 deploy
2 1 deploy
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
non-authorized to deploy contracts!
```
外部账户2部署合约失败，无权限部署合约。    
**外部账户3部署合约：** 
```
$ ./run.sh 3 1 deploy
3 1 deploy
tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed
non-authorized to deploy contracts!
```
外部账户3部署合约失败，无权限部署合约。

#### 8.1.1.2 创建用户表测试
**外部账户2创建用户表t_test:**
```
$ ./run.sh 2 1 create
2 1 create
non-authorized to deploy contracts!
```
外部账户2创建t_test表失败，表明无权限创建用户表。   
**外部账户3创建用户表t_test:**
```
$ ./run.sh 3 1 create
3 1 create
non-authorized to create t_test table.
```
外部账户3创建t_test表失败，表明无权限创建用户表。

**外部账户1创建用户表t_test:**
```
$ ./run.sh 1 1 create
1 1 create
create t_test table completed.
```
外部账户1创建t_test表成功，表明有权限创建用户表。

#### 8.1.3 去除权限测试
登录sdk控制台，去除设置的外部账户1权限，则外部账户1，2和3均可以部署合约和创建表。
```
> ra _sys_tables_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa _sys_tables_
Empty set.
```
重复5.1.1进行测试。

### 8.2 用户表测试
#### 8.2.1 默认权限测试
默认所有表放开，无权限设置信息，所有外部账户均读写表。通过测试客户端分别指定三个外部账户进行t_test表的增删改查测试。首先登录sdk控制台，查询t_test表的权限设置内容。
```
> qa t_test
Empty set.
```
确认初始状态没有设置权限，因此默认所有外部账户均可以进行读写操作。
##### 8.2.1.1 账户1测试
指定外部账户1创建t_test表：
```
$ ./run.sh 1 1 create
1 1 create
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
create t_test table completed.
```
t_test表创建成功。
1）通过指定外部账户1向t_test表插入记录：
```
$ ./run.sh 1 1 insert fruit 1 apple1
1 1 insert fruit 1 apple1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
insertCount = 1
```
t_test表插入记录成功。
2）通过指定外部账户1向t_test表查询记录：
```
$ ./run.sh 1 1 read fruit
1 1 read fruit
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
record numbers = 1
name = fruit
item_id = 1
item_name = apple1
```
3）通过指定外部账户1向t_test表更新记录：
```
$ ./run.sh 1 1 update fruit 1 apple11
1 1 update fruit 1 apple11
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
updateCount = 1
```
t_test表更新记录成功，可以通过查询记录再次验证。
4）通过指定外部账户1向t_test表删除记录：
```
$ ./run.sh 1 1 remove fruit 1
1 1 remove fruit 1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
removeCount = 1
```
t_test表删除记录成功，可以通过查询记录再次验证。     
##### 8.2.1.2 账户2和3测试
外部账户1已创建t_test表，无需再创建。与测试外部账户1类似，可以分别指定外部账户2和3对t_test表进行增删改查验证。

#### 8.2.2 设置权限测试
登录sdk控制台，设置外部账户1可以对t_test表进行读写操作。
```
> aa t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      5                      |
---------------------------------------------------------------------------------------------
```
设置完毕后，则外部账户1有权限对t_test表进行写操作，其他外部账户只可以对t_test表执行读操作。    
**外部账户1有权限操作t_test表测试：**    
1）通过指定外部账户1向t_test表插入记录：
```
$ ./run.sh 1 1 insert fruit 2 apple1
1 1 insert fruit 2 apple1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
insertCount = 1
```
t_test表插入记录成功。
2）通过指定外部账户1向t_test表查询记录：
```
$ ./run.sh 1 1 read fruit
1 1 read fruit
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
record numbers = 1
name = fruit
item_id = 2
item_name = apple1
```
3）通过指定外部账户1向t_test表更新记录：
```
$ ./run.sh 1 1 update fruit 2 apple22
1 1 update fruit 2 apple22
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
updateCount = 1
```
t_test表更新记录成功，可以通过查询记录再次验证。
4）通过指定外部账户1向t_test表删除记录：
```
$ ./run.sh 1 1 remove fruit 2
1 1 remove fruit 2
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
removeCount = 1
```
t_test表删除记录成功，可以通过查询记录再次验证。      

**外部账户2或3无权限操作t_test表测试：**   
1）通过指定外部账户2向t_test表插入记录：
```
$ ./run.sh 2 1 insert fruit 2 apple2
2 1 insert fruit 2 apple2
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
insertCount = -1
```
t_test表插入记录失败，可以通过查询记录再次验证。insertCount返回-1表示无权限插入记录。
2）通过指定外部账户2向t_test表查询记录：
```
$ ./run.sh 2 1 read fruit
2 1 read fruit
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
record numbers = 0
```
3）通过指定外部账户1向t_test表插入记录：
```
$ ./run.sh 1 1 insert fruit 2 apple1
1 1 insert fruit 2 apple1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
insertCount = 1
```
t_test表插入记录成功，可以通过查询记录再次验证。外部账户1有权限插入记录。
4）通过指定外部账户2向t_test表更新记录：
```
$ ./run.sh 2 1 update fruit 2 apple12
2 1 update fruit 2 apple12
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
updateCount = -1
```
t_test表更新记录失败，可以通过查询记录再次验证。updateCount返回-1表示无权限更新记录。
5）通过指定外部账户2向t_test表删除记录：
```
$ ./run.sh 2 1 remove fruit 2
2 1 remove fruit 2
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
removeCount = -1
```
t_test表删除记录失败，可以通过查询记录再次验证。removeCount返回-1表示无权限删除记录。

#### 8.2.3 去除权限测试
登录sdk控制台，去除设置的外部账户1权限，则外部账户1，2和3均可以对t_test表进行读写操作。
```
> ra t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa t_test
Empty set.
```
类似5.2.1进行t_test表的增删改查测试。