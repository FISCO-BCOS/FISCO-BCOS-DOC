# FISCO BCOS 2.0使用教程： 分布式存储体验

作者：莫楠｜FISCO BCOS 高级架构师

《分布式存储架构设计》一文发布后，社区成员对技术内核及使用非常关注。团队和社区热心小伙伴、业内专家针对分布式存储，进行了一箩筐地讨论。在此，跟大家分享交流心得，或有助你更好地理解和使用分布式存储：

- FISCO BCOS 2.0的分布式存储采用库表风格，CRUD操作符合业务习惯。
- 不用合约存储变量模式，解构了合约和数据的内嵌式耦合，合约升级更容易。
- 存储访问引擎逻辑和数据结构更直观，容易适配各种存储引擎，扩展空间大。
- 数据本身行列式存储，没有MPT树那般盘根错节的关系，更容易打快照和切割迁移。
- 表加主键的结构索引数据，存取效率高，并发访问更容易。
- 存储开销更少，容量模型和交易数、状态数线性相关，更容易预测业务容量，对海量服务非常有意义。
- 细节方面，弱化状态MPT，但保留了交易和回执MPT，依旧可支持轻客户端，采用过程证明和存在证明，而不依赖易变的状态，不影响实现跨链。
- 状态由增量HASH检验，每块交易产生的状态集会全网严格检验以保证一致性。
- 一开始是面向SQL类型构建的，可支持MySQL和Oracle等引擎，然后适配到NoSQL类型，如LevelDB等。后续还会适配更多高速和海量存储引擎，在【单次io延迟/并发效率/容量扩展】这个三角关系中，探索出最优解。

分布式存储虽说是个大工程（团队几个快枪手撸了小一年才敢拿出来见人），但使用非常简单，本文就讲讲分布式存储的体验流程。初步接触用户，建议先从上篇入手（点标题可直接跳转）→ [分布式存储架构设计](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485336&idx=1&sn=ea3a7119634c1c27daa4ec2b9a9f278b&chksm=9f2ef584a8597c9288f8c5000c7def47c3c5b9dc64f25221985cd9e3743b9364a93933e51833&token=705851025&lang=zh_CN#rd)

## 配置分布式存储

分布式存储支持多种存储引擎，根据业务需求和部署环境灵活选择，可以配置为不同的存储引擎。

区块链的区块、交易等基础数据采用库表结构保存，状态数据的存储方式可配为库表结构或MPT，满足不同场景的需求。

分布式存储的配置项位于群组的配置文件中，各个群组可以使用单独的存储策略，群组配置文件位于区块链节点中名为conf/group.[群组号].genesis的路径下，如group.1.genesis，一旦群组启动，该群组的分布式存储的相关配置不能再改变。

**分布式存储配置项示例如下：**

[storage]

type=LevelDB：分布式存储的DB引擎类型，支持”LevelDB”和“External”（rc2版本）

[state]

type=storage：state类型，目前支持storage state和MPT state，默认为storage state

**推荐使用 storage state**， **除非必须使用MPT来追溯全局历史状态**，**不建议使用MPT State**。

## 使用CRUD智能合约开发

分布式存储提供了专用的CRUD接口，支持合约直接访问底层的存储表。

访问CRUD需要引用分布式存储专用的智能合约Table.sol接口，该接口是数据库合约，可以创建表，并对表进行增删改查操作。

**引用Table.sol**

```
import "./Table.sol";
```

**Table.sol的接口包括**：

- createTable //创建表
- select(string, Condition) //查询数据
- insert(string, Entry) //插入数据
- update(string, Entry, Condition) //更新数据
- remove(string, Condition) //删除数据

**每个接口的用法如下**：

**创建表**

```
// TableFactory的地址固定为0x1001
TableFactory tf = TableFactory(0x1001); 

// 创建t_test表，表的key_field为name，value_field为item_id,item_name 
// key_field表示分布式存储主key value_field表示表中的列，可以有多列，以逗号分隔
int count = tf.createTable("t_test", "name", "item_id,item_name");
```

**查询数据**

```
TableFactory tf = TableFactory(0x1001);
Table table = tf.openTable("t_test");

// 条件为空表示不筛选 也可以根据需要使用条件筛选
Condition condition = table.newCondition();

Entries entries = table.select(name, condition);
```

**插入数据**

```
TableFactory tf = TableFactory(0x1001);
Table table = tf.openTable("t_test"); 

Entry entry = table.newEntry();
entry.set("name", name);
entry.set("item_id", item_id);
entry.set("item_name", item_name);

int count = table.insert(name, entry);
```

**更新数据**

```
TableFactory tf = TableFactory(0x1001);
Table table = tf.openTable("t_test");

Entry entry = table.newEntry();
entry.set("item_name", item_name);

Condition condition = table.newCondition();
condition.EQ("name", name);
condition.EQ("item_id", item_id);

int count = table.update(name, entry, condition);
```

**删除数据**

```
TableFactory tf = TableFactory(0x1001);
Table table = tf.openTable("t_test");

Condition condition = table.newCondition();
condition.EQ("name", name);
condition.EQ("item_id", item_id);

int count = table.remove(name, condition);
```

#### PS

存储架构的优化是个基础工程，也是个大工程。实现的转变，其实是架构世界观的一种进化，影响会比看到的功能点更深远。此二文所讲述的，仅是分布式存储的冰山一角。更多原理和使用案例，请参考：https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/smart_contract.html

### 系列精选

[FISCO BCOS 2.0发布](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485340&idx=1&sn=310a442382c879aecaa0cb37c1bce289&chksm=9f2ef580a8597c96cada2526ad2df2e65c00c62a50bcebbd41d84c36aadfac2526f6a12aa2ec&token=705851025&lang=zh_CN#rd)：（附新特性解读）

#### 原理解析

[群组架构的设计](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485338&idx=1&sn=9ce03340c699a8527960a0d0b26d4923&chksm=9f2ef586a8597c9003192718c1f60ed486570f6a334c9713cc7e99ede91c6f3ddcd7f438821f&token=705851025&lang=zh_CN#rd)：使企业间建立多方协作的商业关系像拉群聊天一样简便。

#### 使用教程

[群组架构实操演练](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485337&idx=1&sn=622e88b631ae1bfe5789b2fe21576779&chksm=9f2ef585a8597c9311c972eb67174b3638f7b69d87d6eea243fc327bf515159fb53f216a5fec&token=705851025&lang=zh_CN#rd)：以搭建仲裁链为例，并演示如何向该链发送交易。