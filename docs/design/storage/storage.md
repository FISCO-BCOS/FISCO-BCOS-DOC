# AMDB

分布式存储（Advanced Mass Database，AMDB）通过对表结构的设计，既可以对应到关系型数据库的表，又可以拆分使用KV数据库存储。通过实现对应于不同数据库的存储驱动，AMDB理论上可以支持所有关系型和KV的数据库。

- CRUD数据、区块数据和合约代码数据存储默认情况下都保存在AMDB，无需配置，合约局部变量存储可根据需要配置为MPTState或StorageState，无论配置哪种State，合约代码都不需要变动。
- 当使用MPTState时，合约局部变量保存在MPT树中。当使用StorageState时，合约局部变量保存在AMDB表中。
- 尽管MPTState和AMDB最终数据都会写向LevelDB，但二者使用不同的LevelDB实例，没有事务性，因此当配置成使用MPTState时，提交数据时异常可能导致两个LevelDB数据不一致。

## 名词解释

### Table

存储表中的所有数据。Table中存储AMDB主key到对应Entries的映射，可以基于AMDB主key进行增删改查，支持条件筛选。

### Entries

Entries中存放主Key相同的Entry，数组。AMDB的主Key与Mysql中的主key不同，AMDB主key用于标示Entry属于哪个key，相同key的Entry会存放在同一个Entries中。

### Entry

对应于表中的一行，每行以列名作为key，对应的值作为value，构成KV结构。每个Entry拥有自己的AMDB主key，不同Entry允许拥有相同的AMDB主key。

### Condition

Table中的删改查接口支持传入条件，这三种接口会返回根据条件筛选后的结果。如果条件为空，则不做任何筛选。

### 举例

以某公司员工领用物资登记表为例，解释上述名词。

|Name*|item_id|item_name|
|:--|:---|:---|
|Alice|1001001|laptop|
|Alice|1001002|screen|
|Bob|1002001|macbook|
|Chris|1003001|PC|

解释如下：
- 表中**Name**是AMDB主key。
- 表中的每一行为一个Entry。一共有4个Entry，每个Entry以Map存储数据。4个Entry如下：
    + Entry1：`{Name:Alice，item_id:1001001,item_name:laptop}`
    + Entry2：`{Name:Alice，item_id:1001001,item_name:screen}`
    + Entry3：`{Name:Bob，item_id:1001001,item_name:macbook}`
    + Entry4：`{Name:Chris，item_id:1001001,item_name:PC}`
- Table中以**Name**为主key，存有3个Entries对象。第1个Entries中存有Alice的2条记录，第2个Entries中存有Bob的1条记录，第3个Entries中存有Chris的一条记录。
- 调用Table类的查询接口时，查接口需要指定AMDB主key和条件，设置查询的AMDB主key为Alice，条件为`price > 40`，会查询出Entry1。

## AMDB表分类

表中的所有`entry`，都会有`_status_`,`_num_`,`_hash_`内置字段。

### 系统表

系统表默认存在，由存储驱动保证系统表的创建。

|表名                   |  keyField  | valueField            |  存储数据说明                            |  AMDB主key                              |
|:--------|:--------|:--------|:--------|:--------|
|`_sys_tables_`         | table_name |key_field,value_field  | 存储所有表的结构，以表名为主键           |    所有表的表名                         |    
|`_sys_consensus_`      | name       |type,node_id,enable_num| 存储共识节点和观察节点的列表             |    node                                 |  
|`_sys_table_access_`   | table_name |address,enable_num     | 存储每个表的具有写权限的外部账户地址     |     表的表名                            |       
|`_sys_cns_`            | name       |version,address,abi    | 存储CNS映射关系                          | 合约名                                  | 
|`_sys_config_`         | key        |value,enable_num       | 存储需要共识的群组配置项                 |   配置项                                |   
|`_sys_current_state_`  | key        |value                  | 存储最新的状态                           |  current_number/total_transaction_count |
|`_sys_tx_hash_2_block_`| hash       |value,index            | 存储交易hash到区块号的映射               |   交易hash的16进制                      |  
|`_sys_number_2_hash_`  | hash       |value                  | 存储区块号到区块头hash的16进制表示的映射 |     区块高                              |   
|`_sys_hash_2_block_`   | key        |value                  | 存储hash到序列化的区块数据               |   区块头hash的16进制                    |  
|`_sys_block_2_nonces_` | number     |value                  | 存储区块中交易的nonces                   |  区块高                      |

### 用户表

用户调用CRUD接口所创建的表，以`_user_<TableName>`为表名，底层自动添加`_user_`前缀。

### StorageState账户表

`_contract_data_`+`Address`+`_`作为表名。表中存储外部账户相关信息。表结构如下

|key*|value|
|:---|:---|
|balance||
|nonce||
|code||
|codeHash||
|alive||

# StorageState

StorageState是一种使用AMDB实现的存储账户状态的方式。相比于MPTState主要有以下区别：

|      |StorageState|MPTState|
|:-------|:------|:--------|
|账户数据组织方式|AMDB表|MPT树|
|历史状态|不支持，不维护历史状态|支持|

MPTState每个账户使用MPT树存储其数据，当历史数据逐渐增多时，会因为存储方式和磁盘IO导致性能问题。StorageState每个账户对应一个Table存储其相关数据，包括账户的`nonce`,`code`,`balance`等内容，而AMDB可以通过实现对应的存储驱动支持不同的数据库以提高性能，我们使用LevelDB测试发现，StorageState性能大约是MPTState的两倍。


# amdb环境部署

## 逻辑架构图
amdb多群组架构下，群组下的单个节点对应一个amdb实例，例如，区块链网络中，有三个节点A,B,C，其中A,B属于Group1,B,C属于Group2。节点A和C分别对应1个db实例，B节点对应了2个db实例，逻辑架构图如下：

![](../../../images/storage/logic_archite.png)

## 节点配置
区块链底层配置 编辑每个群组的group.<群组编号>.genesis文件，修改[storage]段的内容，设置为如下内容：
```bash
[storage]
    	;storage db type, now support leveldb, external
    	type=external
        ;这里请注意，需要和要连接的amdb中的amdb.properties中的node.topic保持一致。
    	topic=DB
    	maxRetry=100
```
这里需要注意一下：同一条链上的所有节点，group.<群组编号>.genesis中storage和state必须保持一致。
配置内容描述：

|配置项|可选值|描述|
|:--|:--|:--|
|type|leveldb、external|配置类型，当前支持：leveldb：本地数据存储external：外部存储，通过AMOP访问|
|topic|字符串|通过AMOP访问外部存储的topic，需要与AMDB服务配置的topic一致|
|maxRetry|大于0的数字|通过AMOP访问外部存储失败时的最大重试次数，达到最大重试次数后，区块链进程将退出|

## amdb代码编译

### 源码获取
```bash
git clone https://github.com/FISCO-BCOS/AMDB.git
```

### 源码编译
```bash
cd AMDB;gradle build
```
编译完成之后，会生成一个dist目录，文件结构如下：
```bash
├── apps
│   └── AMDB.jar
├── conf
│   ├── amdb.properties
│   ├── applicationContext.xml
│   ├── contracts
│   │   ├── Table.sol
│   │   ├── TableTest.sol
│   ├── db.properties
│   ├── doc
│   │   ├── amop.png
│   │   ├── leveldb.png
│   │   └── README.md
│   ├── log4j2.xml
│   └── mappers
│       └── data_mapper.xml
├── lib
├── log
└── start.sh
```

## 配置文件配置
### 证书配置
将节点sdk目录下所有文件（包括 ca.crt，node.crt，node.key）拷贝到 dist/conf 目录下。

### 配置文件配置
#### amdb.properties配置
amdb.properties配置AMDB数据代理需要连接的节点配置信息
```
#节点ip
node.ip=127.0.0.1
#节点rpc listen port(请参考节点config.ini中的channel_listen_port)
node.listen_port=20600
#节点的topic
node.topic=DB
```

#### db.properties配置
db.properties为amdb实例的配置信息，参考配置如下：
```
#数据库服务器的ip
db.ip=127.0.0.1
#数据库服务器监听端口
db.port=3306
#数据库用户名
db.user=root
#数据库密码
db.password=123456
#dbname
db.database=bcos
```
#### applicationContext.xml配置
将/DBChannelService/groupId的值修改为节点所在的groupid。
在/groupChannelConnectionsConfig/allChannelConnections配置group的connectionsStr。
例如节点B分别属于group1,group2。B节点的访问地址为127.0.0.1:20600。
group1下的B节点配置参考如下（部分信息）
```
	<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
		<property name="allChannelConnections">
			<list>
				<bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
					<property name="groupId" value="1" />
					<property name="connectionsStr">
						<list>
							<value>127.0.0.1:20600</value>
						</list>
					</property>
				</bean>
			</list>
		</property>
	</bean>

<bean id="DBChannelService" class="org.fisco.bcos.channel.client.Service">
		<property name="groupId" value="1" />
		<property name="orgID" value="fisco" />
		<property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
		
		<!-- communication topic configuration of the node -->
		<property name="topics">
			<list>
				<value>${node.topic}</value>
			</list>
		</property>
		<property name="pushCallback" ref="DBHandler"/>
	</bean>
```

group2下的B节点配置参考如下（部分信息）
```
	<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
		<property name="allChannelConnections">
			<list>
				<bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
					<property name="groupId" value="2" />
					<property name="connectionsStr">
						<list>
							<value>127.0.0.1:20600</value>
						</list>
					</property>
				</bean>
			</list>
		</property>
	</bean>

<bean id="DBChannelService" class="org.fisco.bcos.channel.client.Service">
		<property name="groupId" value="2" />
		<property name="orgID" value="fisco" />
		<property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
		
		<!-- communication topic configuration of the node -->
		<property name="topics">
			<list>
				<value>${node.topic}</value>
			</list>
		</property>
		<property name="pushCallback" ref="DBHandler"/>
	</bean>
```

## amdb进程启动
```bash
cd dist;sh start.sh
```
ps查看到java进程并确认日志没有问题，确认启动成功就可以启动节点了。
