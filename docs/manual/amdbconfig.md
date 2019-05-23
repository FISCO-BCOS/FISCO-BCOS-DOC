# 分布式存储

## 逻辑架构图
amdb多群组架构下，群组下的单个节点对应一个amdb实例，例如，区块链网络中，有三个节点A,B,C，其中A,B属于Group1,B,C属于Group2。节点A和C分别对应1个db实例，B节点对应了2个db实例，逻辑架构图如下：

![](../../images/storage/logic_archite.png)

以上图为例，描述搭建配置过程。
A,B,C三个节点，分别用Group1_A（Group1下的A节点，下同），Group1_B，Group2_B，Group2_C表示。

## 节点搭建
使用amdb之前，需要先搭链，详细搭链过程请参考[搭链](../installation.md)，也可参考如下步骤。

### 准备依赖
```bash
mkdir -p ~/fisco && cd ~/fisco
curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/build_chain.sh && chmod u+x build_chain.sh
```
### 生成配置文件
```bash
# 生成区块链配置文件ipconf
cat > ipconf << EOF
127.0.0.1:1 agencyA 1
127.0.0.1:1 agencyB 1,2
127.0.0.1:1 agencyC 2
EOF

# 查看配置文件
cat ipconf 
127.0.0.1:1 agencyA 1
127.0.0.1:1 agencyB 1,2
127.0.0.1:1 agencyC 2
```

### 使用build_chain搭建区块链
```bash
### 搭建区块链（请先确认30600~30602，20800~20802，8565~8567端口没有被占用）
bash build_chain.sh -f ipconf -p 30600,20800,8565
==============================================================
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:1 Agency:agencyA Groups:1
Processing IP:127.0.0.1 Total:1 Agency:agencyB Groups:1,2
Processing IP:127.0.0.1 Total:1 Agency:agencyC Groups:2
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:1 Agency:agencyA Groups:1
Processing IP:127.0.0.1 Total:1 Agency:agencyB Groups:1,2
Processing IP:127.0.0.1 Total:1 Agency:agencyC Groups:2
==============================================================
Group:1 has 2 nodes
Group:2 has 2 nodes
```
生成的节点文件如下
```
.
├── 127.0.0.1
│   ├── fisco-bcos
│   ├── node0
│   │   ├── conf
│   │   │   ├── ca.crt
│   │   │   ├── group.1.genesis
│   │   │   ├── group.1.ini
│   │   │   ├── node.crt
│   │   │   ├── node.key
│   │   │   └── node.nodeid
│   │   ├── config.ini
│   │   ├── start.sh
│   │   └── stop.sh
│   ├── node1
│   │   ├── conf
│   │   │   ├── ca.crt
│   │   │   ├── group.1.genesis
│   │   │   ├── group.1.ini
│   │   │   ├── group.2.genesis
│   │   │   ├── group.2.ini
│   │   │   ├── node.crt
│   │   │   ├── node.key
│   │   │   └── node.nodeid
│   │   ├── config.ini
│   │   ├── start.sh
│   │   └── stop.sh
│   ├── node2
│   │   ├── conf
│   │   │   ├── ca.crt
│   │   │   ├── group.2.genesis
│   │   │   ├── group.2.ini
│   │   │   ├── node.crt
│   │   │   ├── node.key
│   │   │   └── node.nodeid
│   │   ├── config.ini
│   │   ├── start.sh
│   │   └── stop.sh
│   ├── sdk
│   │   ├── ca.crt
│   │   ├── node.crt
│   │   └── node.key
│   ├── start_all.sh
│   └── stop_all.sh
......此处省略其他输出......

```

### 修改节点genesis文件
#### 修改node0下的group.1.genesis配置
```
cd ~/fisco/nodes/127.0.0.1/node0/conf;
```
修改group.1.genesis[storage]段的内容，设置为如下内容
```bash
[storage]
    	;storage db type, now support leveldb, external
    	type=external
    	topic=DB_Group1_A
    	maxRetry=100
```
#### 修改node1下的group.1.genesis配置
```bash
cd ~/fisco/nodes/127.0.0.1/node1/conf;
```
修改group.1.genesis[storage]段的内容，设置为如下内容
```bash
[storage]
    	;storage db type, now support leveldb, external
    	type=external
    	topic=DB_Group1_B
    	maxRetry=100
```

#### 修改node1下的group.2.genesis配置
```bash
cd ~/fisco/nodes/127.0.0.1/node1/conf;
```
修改group.2.genesis[storage]段的内容，设置为如下内容
```bash
[storage]
    	;storage db type, now support leveldb, external
    	type=external
    	topic=DB_Group2_B
    	maxRetry=100
```

#### 修改node2下的group.2.genesis配置
```bash
cd ~/fisco/nodes/127.0.0.1/node2/conf;
```
修改group.2.genesis[storage]段的内容，设置为如下内容
```bash
[storage]
    	;storage db type, now support leveldb, external
    	type=external
    	topic=DB_Group2_C
    	maxRetry=100
```

## 准备amdb代理
### 源码获取
```bash
cd ~/fisco; 
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

## 配置AMDB代理
amdb与节点连接过程，amdb是client,节点是server，启动过程是amdb主动连接节点，节点只需要配置AMDB关注的topic即可，关于topic的介绍请参考[AMOP](./amop_protocol.md)，amdb需要通过证书准入。
### 证书配置
```bash
cp ~/fisco/nodes/127.0.0.1/sdk/* ~/fisco/AMDB/dist/conf/
```

### amdb实例拷贝
```bash
cd ~/fisco;
###dist_Group1_A是节点Group1_A对应的amdb实例
cp AMDB/dist/ dist_Group1_A -Rf
###dist_Group1_B是节点Group1_B对应的amdb实例
cp AMDB/dist/ dist_Group1_B -Rf
###dist_Group2_B是节点Group2_B对应的amdb实例
cp AMDB/dist/ dist_Group2_B -Rf
###dist_Group2_C是节点Group2_C对应的amdb实例
cp AMDB/dist/ dist_Group2_C -Rf
```
经过上述步骤，可以看到~/fisco目录的文件结构如下：
```
drwxrwxr-x 8 fisco fisco  4096 May  7 15:53 AMDB
-rwxrw-r-- 1 fisco fisco 37539 May  7 14:58 build_chain.sh
drwxrwxr-x 5 fisco fisco  4096 May  7 15:58 dist_Group1_A
drwxrwxr-x 5 fisco fisco  4096 May  7 15:58 dist_Group1_B
drwxrwxr-x 5 fisco fisco  4096 May  7 15:59 dist_Group2_B
drwxrwxr-x 5 fisco fisco  4096 May  7 15:59 dist_Group2_C
-rw-rw-r-- 1 fisco fisco    68 May  7 14:59 ipconf
drwxrwxr-x 4 fisco fisco  4096 May  7 15:08 nodes
```

### DB创建
```bash
	mysql -uroot -p123456
	CREATE DATABASE `bcos_Group1_A`;
	CREATE DATABASE `bcos_Group1_B`;
	CREATE DATABASE `bcos_Group2_B`;
	CREATE DATABASE `bcos_Group2_C`;
```

### 配置文件配置
#### 为Group1的A节点配置AMDB代理
```bash
cd ~/fisco/dist_Group1_A/conf
```
将amdb.properties配置为如下内容:
```bash
node.ip=127.0.0.1
node.listen_port=20800
node.topic=DB_Group1_A
```
将db.properties配置为如下内容:
```bash
db.ip=127.0.0.1
db.port=3306
db.user=root
db.password=123456
db.database=bcos_Group1_A
```

将applicationContext.xml修改为如下配置(部分信息)
```bash
	<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
		<property name="allChannelConnections">
			<list>
				<bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
					<property name="groupId" value="1" />
					<property name="connectionsStr">
						<list>
						<value>127.0.0.1:20800</value>
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
		<property name="topics">
			<list>
				<value>${node.topic}</value>
			</list>
		</property>
		<property name="pushCallback" ref="DBHandler"/>
	</bean>
```

#### 为Group1的B节点配置AMDB代理
```bash
cd ~/fisco/dist_Group1_B/conf
```
将amdb.properties配置为如下内容:
```bash
node.ip=127.0.0.1
node.listen_port=20801
node.topic=DB_Group1_B
```
将db.properties配置为如下内容:
```bash
db.ip=127.0.0.1
db.port=3306
db.user=root
db.password=123456
db.database=bcos_Group1_B
```

将applicationContext.xml修改为如下配置(部分信息)
```bash
	<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
		<property name="allChannelConnections">
			<list>
				<bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
					<property name="groupId" value="1" />
					<property name="connectionsStr">
						<list>
						<value>127.0.0.1:20801</value>
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
		<property name="topics">
			<list>
				<value>${node.topic}</value>
			</list>
		</property>
		<property name="pushCallback" ref="DBHandler"/>
	</bean>
```
#### 为Group2的B节点配置AMDB代理
```bash
cd ~/fisco/dist_Group2_B/conf
```
将amdb.properties配置为如下内容:
```bash
node.ip=127.0.0.1
node.listen_port=20801
node.topic=DB_Group2_B
```
将db.properties配置为如下内容:
```bash
db.ip=127.0.0.1
db.port=3306
db.user=root
db.password=123456
db.database=bcos_Group2_B
```
将applicationContext.xml修改为如下配置(部分信息)
```bash
	<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
		<property name="allChannelConnections">
			<list>
				<bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
					<property name="groupId" value="2" />
					<property name="connectionsStr">
						<list>
							<value>127.0.0.1:20801</value>
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

#### 为Group2的C节点配置AMDB代理
```bash
cd ~/fisco/dist_Group2_C/conf
```
将amdb.properties配置为如下内容:
```bash
node.ip=127.0.0.1
node.listen_port=20802
node.topic=DB_Group2_C
```
将db.properties配置为如下内容:
```bash
db.ip=127.0.0.1
db.port=3306
db.user=root
db.password=123456
db.database=bcos_Group2_C
```

将applicationContext.xml修改为如下配置(部分信息)
```bash
	<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
		<property name="allChannelConnections">
			<list>
				<bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
					<property name="groupId" value="2" />
					<property name="connectionsStr">
						<list>
							<value>127.0.0.1:20802</value>
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


## 启动AMDB代理
```bash
cd ~/fisco/dist_Group1_A;sh start.sh
cd ~/fisco/dist_Group1_B;sh start.sh
cd ~/fisco/dist_Group2_B;sh start.sh
cd ~/fisco/dist_Group2_C;sh start.sh
```

## 启动节点
```bash
cd ~/fisco/nodes/127.0.0.1;sh start_all.sh
```
## 检查进程
```bash
ps -ef|grep org.bcos.amdb.server.Main|grep -v grep
fisco   110734      1  1 17:25 ?        00:00:10 java -cp conf/:apps/*:lib/* org.bcos.amdb.server.Main
fisco   110778      1  1 17:25 ?        00:00:11 java -cp conf/:apps/*:lib/* org.bcos.amdb.server.Main
fisco   110803      1  1 17:25 ?        00:00:10 java -cp conf/:apps/*:lib/* org.bcos.amdb.server.Main
fisco   122676      1 16 17:38 ?        00:00:08 java -cp conf/:apps/*:lib/* org.bcos.amdb.server.Main

ps -ef|grep fisco-bcos|grep -v grep
fisco   111061      1  0 17:25 pts/0    00:00:04 /data/home/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco   111065      1  0 17:25 pts/0    00:00:04 /data/home/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco   122910      1  1 17:38 pts/0    00:00:02 /data/home/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
```
启动成功，会看到有4个java进程，3个fisco-bcos进程。不成功的话请参考日志确认配置是否正确。

## 检查日志输出
执行下面指令，查看节点node0链接的节点数（其他节点类似）
```
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```
正常情况会看到类似下面的输出，从输出可以看出node0与另外2个节点有连接。
```
info|2019-05-07 21:47:22.849910| [P2P][Service] heartBeat connected count,size=2
info|2019-05-07 21:47:32.849970| [P2P][Service] heartBeat connected count,size=2
info|2019-05-07 21:47:42.850024| [P2P][Service] heartBeat connected count,size=2
```
执行下面指令，检查是否在共识
```
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```
正常情况会不停输出++++Generating seal表示共识正常。
```
info|2019-05-07 21:48:54.942111| [g:1][p:65544][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=6,tx=0,nodeIdx=1,hash=355790f7...
info|2019-05-07 21:48:56.946022| [g:1][p:65544][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=6,tx=0,nodeIdx=1,hash=4ef772bb...
info|2019-05-07 21:48:58.950222| [g:1][p:65544][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=6,tx=0,nodeIdx=1,hash=48341ee5...
```

## 使用控制台发送交易

### 准备依赖
```bash
cd ~/fisco;
bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/download_console.sh)
$ cp -n console/conf/applicationContext-sample.xml console/conf/applicationContext.xml
cp nodes/127.0.0.1/sdk/* console/conf/
```
### 修改配置文件
```bash
cd ~/fisco/console/conf
```
applicationContext.xml修改为如下配置(部分信息)
```bash
<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
		<property name="allChannelConnections">
			<list>
				<bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
					<property name="groupId" value="1" />
					<property name="connectionsStr">
						<list>
							<value>127.0.0.1:20800</value>
						</list>
					</property>
				</bean>
			</list>
		</property>
	</bean>
```
### 启用控制台
```bash
cd ~/fisco/console
sh start.sh 1
#部署TableTest合约
[group:1]> deploy TableTest
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
```

查看数据库中的表情况
```bash
mysql -uroot -p123456 -A bcos_Group1_A
use bcos_Group1_A;
show tables;
----------------------------------------------------------+
| Tables_in_bcos_Group1_A                                  |
+----------------------------------------------------------+
| _contract_data_8c17cf316c1063ab6c89df875e96c9f0f5b2f744_ |
| _contract_data_f69a2fa2eca49820218062164837c6eecc909abd_ |
| _sys_block_2_nonces_                                     |
| _sys_cns_                                                |
| _sys_config_                                             |
| _sys_consensus_                                          |
| _sys_current_state_                                      |
| _sys_hash_2_block_                                       |
| _sys_number_2_hash_                                      |
| _sys_table_access_                                       |
| _sys_tables_                                             |
| _sys_tx_hash_2_block_                                    |
+----------------------------------------------------------+
12 rows in set (0.02 sec)
```

在控制台中调用create接口。
```bash
#创建表
call TableTest 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 create
0xab1160f0c8db2742f8bdb41d1d76d7c4e2caf63b6fdcc1bbfc69540a38794429
```

查看数据库中的表情况
```bash
show tables;
+----------------------------------------------------------+
| Tables_in_bcos_Group1_A                                  |
+----------------------------------------------------------+
| _contract_data_8c17cf316c1063ab6c89df875e96c9f0f5b2f744_ |
| _contract_data_f69a2fa2eca49820218062164837c6eecc909abd_ |
| _sys_block_2_nonces_                                     |
| _sys_cns_                                                |
| _sys_config_                                             |
| _sys_consensus_                                          |
| _sys_current_state_                                      |
| _sys_hash_2_block_                                       |
| _sys_number_2_hash_                                      |
| _sys_table_access_                                       |
| _sys_tables_                                             |
| _sys_tx_hash_2_block_                                    |
| _user_t_test                                             |
+----------------------------------------------------------+
```

在控制台中调用create接口
```bash
#往表里插入数据
call TableTest 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 insert "fruit" 100 "apple"
0x082ca6a5a292f1f7b20abeb3fb03f45e0c6f48b5a79cc65d1246bfe57be358d1
```

打开mysql客户端，查询_user_t_test表数据
```bash
#查看用户表中的数据
select * from _user_t_test\G;
*************************** 1. row ***************************
     _id_: 31
   _hash_: 0a0ed3b2b0a227a6276114863ef3e8aa34f44e31567a5909d1da0aece31e575e
    _num_: 3
 _status_: 0
     name: fruit
  item_id: 100
item_name: apple
1 row in set (0.00 sec)
```