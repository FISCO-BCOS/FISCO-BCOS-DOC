# Distributed storage

## Install MySQL

The currently supported distributed database is MySQL. Before using distributed storage, you need to set up the MySQL service. The configuration on Ubuntu and CentOS servers is as follows:

Ubuntu: Execute the following three commands to configure the root account password during the installation process.

```bash
sudo apt install -y mysql-server mysql-client libmysqlclient-dev
```
Start the MySQL service and log in:
root account password.
```bash
sudo service mysql start
mysql -uroot -p
```
CentOS: Perform the following two commands to install.
```bash
yum install mysql*
# some versions of linux need to install mariadb which is a branch of mysql
yum install mariadb*
```

Start the MySQL service. Log in and set a password for the root user.

```bash
service mysqld start
#If mariadb is installed, to use the following command to start
service mariadb start
mysql -uroot
mysql> set password for root@localhost = password('123456');
```

## Node directly connected to MySQL

FISCO BCOS in version 2.0.0-rc3 supports nodes directly connected to MySQL through connection pool. Compared to the proxy access MySQL mode, this configuration is simple. No need to manually create a database. Please refer to the configuration method:

### Logical architecture diagram

The multi-group architecture means that blockchain node supports launching multiple groups. The transaction processing, data storage, and block consensus among the groups are isolated from each other. Therefore, each node in the group corresponds to a database instance. For example, in blockchain network, there are three nodes A, B, and C, where A and B belong to Group1, and B and C belong to Group2. Nodes A and C correspond to one database instance respectively, and Node B corresponds to two database instances. The logical architecture diagram is as follows.
![](../../images/storage/storage.png)

As shown in the above figure, NodeB belongs to multiple groups. The database instances which are corresponded by the same node in different groups are separate. For distinguishing the same node in different groups, the nodes of A, B, and C are respectively represented with Group1_A (NodeA in Group1, same as below), Group1_B, Group2_B, and Group2_C.

We use the above figure as an example to describe the setup configuration process in following.

### Build node

Before using distributed storage, you need to complete the establishment of the alliance chain and the configuration of multiple groups. For details, refer to the following steps.

#### Prepare dependence
```bash
mkdir -p ~/fisco && cd ~/fisco
# Download build_chain.sh script
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.2.0/build_chain.sh && chmod u+x build_chain.sh
```
#### Generate configuration file
```bash
# generate blockchain configuration file ipconf
cat > ipconf << EOF
127.0.0.1:1 agencyA 1
127.0.0.1:1 agencyB 1,2
127.0.0.1:1 agencyC 2
EOF

# view configuration file
cat ipconf
127.0.0.1:1 agencyA 1
127.0.0.1:1 agencyB 1,2
127.0.0.1:1 agencyC 2
```

#### Build blockchain with build_chain
```bash
### build blockchain（please confirm the ports of 30300~30302，20200~20202，8545~8547 are not occupied）
### The difference right here is that the parameter "-s MySQL" is appended to the command and the port is changed.
bash build_chain.sh -f ipconf -p 30300,20200,8545 -s MySQL
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
### Modify node ini file

In group.[group].ini configuration file, the configuration information of MySQL is related to this feature. Suppose that the MySQL configuration information is as follows:

```bash
|node|db_ip|db_port|db_username|db_passwd|db_name|
|Group1_A|127.0.0.1|3306|root|123456|db_Group1_A|
|Group1_B|127.0.0.1|3306|root|123456|db_Group1_B|
|Group2_B|127.0.0.1|3306|root|123456|db_Group2_B|
|Group2_C|127.0.0.1|3306|root|123456|db_Group2_C|
```

### Modify the group.1.ini configuration in node0

Modify the content in the section ~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini[storage] and add the following content. Db_passwd is the corresponding password.

```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group1_A
    	db_passwd=
```

### Modify the group.1.ini configuration in node1

Modify the content in the section ~/fisco/nodes/127.0.0.1/node1/conf/group.1.ini[storage] and add the following content. Db_passwd is the corresponding password.

```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group1_B
    	db_passwd=
```

### Modify the group.2.ini configuration in node1

Modify the content in the section ~/fisco/nodes/127.0.0.1/node1/conf/group.2.ini[storage] and add the following content. Db_passwd is the corresponding password.

```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group2_B
    	db_passwd=
```
### Modify the group.2.ini configuration in node2

Modify the content in the section ~/fisco/nodes/127.0.0.1/node2/conf/group.2.ini[storage] and add the following content. Db_passwd is the corresponding password.

```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group2_C
    	db_passwd=
```

### Start node
```bash
cd ~/fisco/nodes/127.0.0.1;sh start_all.sh
```
### Check process
```bash
ps -ef|grep fisco-bcos|grep -v grep
fisco   111061      1  0 16:22 pts/0    00:00:04 /data/home/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco   111065      1  0 16:22 pts/0    00:00:04 /data/home/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco   122910      1  1 16:22 pts/0    00:00:02 /data/home/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
```
If it starts successfully, you can see there are 3 fisco-bcos processes. If it fails, please refer to the log to confirm whether the configuration is correct.

### Check output of log
Execute the following command to view the number of nodes connected to node0 (similar to other nodes)

```
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```
Normally, you will see an output similar to the following, and you can see that node0 is connecting to the other two nodes from it.

```
info|2019-05-28 16:28:57.267770|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:07.267935|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:17.268163|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:27.268284|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:37.268467|[P2P][Service] heartBeat,connected count=2
```
Execute the following command to check if it is in consensus

```
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```

Normally, the output will continue to output ++++Generating seal to indicate that the consensus is normal.

```
info|2019-05-28 16:26:32.454059|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=28,tx=0,nodeIdx=3,hash=c9c859d5...
info|2019-05-28 16:26:36.473543|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=28,tx=0,nodeIdx=3,hash=6b319fa7...
info|2019-05-28 16:26:40.498838|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=28,tx=0,nodeIdx=3,hash=2164360f...
```

### Send transaction by console

#### Prepare dependence
```bash
cd ~/fisco;
bash <(curl -S https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/download_console.sh)
cp -n console/conf/applicationContext-sample.xml console/conf/applicationContext.xml
cp nodes/127.0.0.1/sdk/* console/conf/
```
#### Modify configuration file
Modify ~/fisco/console/conf/applicationContext.xml to the following configuration (partial information)

```bash
<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
	<property name="allChannelConnections">
		<list>
			<bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
				<property name="groupId" value="1" />
					<property name="connectionsStr">
					<list>
						<value>127.0.0.1:20200</value>
					</list>
				</property>
			</bean>
		</list>
	</property>
</bean>
```
#### Start console
```bash
cd ~/fisco/console
sh start.sh 1
#deploy TableTest contract
[group:1]> deploy TableTest
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
```

view the table in the database

```bash
MySQL -uroot -p123456 -A db_Group1_A
use db_Group1_A;
show tables;
----------------------------------------------------------+
| Tables_in_db_Group1_A                                  |
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

call the create interface in the console

```bash
#create table
call TableTest 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 create
0xab1160f0c8db2742f8bdb41d1d76d7c4e2caf63b6fdcc1bbfc69540a38794429
```

view the table in the database

```bash
show tables;
+----------------------------------------------------------+
| Tables_in_db_Group1_A                                  |
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

Inserting a record to the database
```bash
#insert data into the table
call TableTest 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 insert "fruit" 100 "apple"
0x082ca6a5a292f1f7b20abeb3fb03f45e0c6f48b5a79cc65d1246bfe57be358d1
```

open the MySQL client and query the_user_t_test table data

```bash
#view data in the user table
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


## Access MySQL through proxy

This operation tutorial is only valid for the 2.1.0 or newer node version.If you need to build a distributed storage environment in 2.0.0-rc3 or 2.0.0 through "accessing MySQL through proxy", please refer to the document [Distributed Storage Building Method](
https://fisco-bcos-documentation.readthedocs.io/zh_CN/v2.0.0/docs/manual/distributed_storage.html) If you need to build a distributed storage environment in 2.0.0-rc2 through "accessing MySQL through proxy", please refer to the document [Distributed Storage Building Method](
https://fisco-bcos-documentation.readthedocs.io/zh_CN/v2.0.0-rc2/docs/manual/amdbconfig.html)

```eval_rst
.. important::
   External will be deprecated in v2.3.0, please use MySQL instead.
```

### Logical architecture diagram

Multi-group architecture means that blockchain node supports starting multiple groups, and the transaction processing, data storage, and block consensus among the groups are isolated from each other. Therefore, each node in the group corresponds to an AMDB instance. For example, in blockchain network, there are three nodes A, B, and C, where A and B belong to group1, and B and C belong to group2. NodeA and NodeC correspond to one database instance respectively, and NodeB corresponds to two database instances. The logical architecture diagram is as follows:

![](../../images/storage/logic_archite.png)


As shown in the above figure, NodeB belongs to multiple groups. The AMDB server and MySQL which are corresponded by the same node in different groups are separate. For distinguishing the same node in different groups, the nodes of A, B, and C are respectively represented with Group1_A (NodeA in Group1, same as below), Group1_B, Group2_B, and Group2_C.

We use the above figure as an example to describe the setup configuration process in following.

### Build nodes

Before configuring the AMDB service, you need to complete the establishment of alliance chain and the configuration of multiple groups. For details, refer to the following steps.

#### Prepare dependence

- create folder

```bash
mkdir -p ~/fisco && cd ~/fisco
```

- get `build_chain` script

```bash
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.2.0/build_chain.sh && chmod u+x build_chain.sh
```

#### Generate configuration file

```bash
# generate blockchain configuration file ipconf
cat > ipconf << EOF
127.0.0.1:1 agencyA 1
127.0.0.1:1 agencyB 1,2
127.0.0.1:1 agencyC 2
EOF

# view configuration file
cat ipconf
127.0.0.1:1 agencyA 1
127.0.0.1:1 agencyB 1,2
127.0.0.1:1 agencyC 2
```

#### Build blockchain with build_chain
```bash
### build blockchain（please confirm the ports of 30300~30302，20200~20202，8545~8547 are not occupied）

bash build_chain.sh -f ipconf -p 30300,20200,8545
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


#### Modify node ini file

##### Modify the group.1.ini configuration in node0

Modify the contents of the [storage] section in the ~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini file, and set as following

```bash
[storage]
    	type=external
    	topic=DB_Group1_A
    	max_retry=100
```
##### Modify the group.1.ini configuration in node1

Modify the contents of the [storage] section in the ~/fisco/nodes/127.0.0.1/node1/conf/group.1.ini file, and set as following

```bash
[storage]
    	type=external
    	topic=DB_Group1_B
    	max_retry=100
```

##### Modify the group.2.ini configuration in node1

Modify the contents of the [storage] section in the ~/fisco/nodes/127.0.0.1/node1/conf/group.2.ini file, and set as following

```bash
[storage]
    	type=external
    	topic=DB_Group2_B
    	max_retry=100
```

##### Modify the group.2.ini configuration in node2

Modify the contents of the [storage] section in the ~/fisco/nodes/127.0.0.1/node2/conf/group.2.ini file, and set as following

```bash
[storage]
    	type=external
    	topic=DB_Group2_C
    	max_retry=100
```

### Prepare amdb proxy
#### Get source code
```bash
cd ~/fisco;
git clone https://github.com/FISCO-BCOS/amdb-proxy.git
```

#### Compile source code
```bash
cd AMDB;gradle build
```

After the compilation is completed, a dist directory is generated, and the file structure is as follows:

```bash
├── apps
│   └── AMDB.jar
├── conf
│   ├── applicationContext.xml
│   ├── contracts
│   │   ├── Table.sol
│   │   ├── TableTest.sol
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

#### Configure amdb proxy

In the connection process between AMDB and node, AMDB is client and node is server. During the startup process, AMDB service is active to connect to node, and node only needs to configure topic of AMDB attention. For topic introduction, please refer to [AMOP](./amop_protocol.md). AMDB needs to pass by the certificate access.

##### Certificate configuration
```bash
cp ~/fisco/nodes/127.0.0.1/sdk/* ~/fisco/AMDB/dist/conf/
```

##### amdb instance copy
```bash
cd ~/fisco;
###dist_Group1_A is the amdb instance corresponding to the node Group1_A
cp AMDB/dist/ dist_Group1_A -R
###dist_Group1_B is the amdb instance corresponding to the node Group1_B
cp AMDB/dist/ dist_Group1_B -R
###dist_Group2_B is the amdb instance corresponding to the node Group2_B
cp AMDB/dist/ dist_Group2_B -R
###dist_Group2_C is the amdb instance corresponding to the node Group2_C
cp AMDB/dist/ dist_Group2_C -R
```
After the above steps, you can see the file structure of ~/fisco directory as follows:
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

##### DB creation
```bash
	MySQL -uroot -p123456
	CREATE DATABASE `bcos_Group1_A`;
	CREATE DATABASE `bcos_Group1_B`;
	CREATE DATABASE `bcos_Group2_B`;
	CREATE DATABASE `bcos_Group2_C`;
```

#### File configuration
Here we assume that the MySQL configuration information is as follows:

```bash
|node|db_ip|db_port|db_username|db_passwd|db_name|
|Group1_A|127.0.0.1|3306|root|123456|bcos_Group1_A|
|Group1_B|127.0.0.1|3306|root|123456|bcos_Group1_B|
|Group2_B|127.0.0.1|3306|root|123456|bcos_Group2_B|
|Group2_C|127.0.0.1|3306|root|123456|bcos_Group2_C|
```
We need to modify  **applicationContext.xml** in configuration process,we need to modify  topic configuration **node.topic**, MySQL configuration items **db.ip**、**db.port**、**db. Database**、 **db.user** and **db.password**.

##### Configure amdb proxy for Group1's NodeA

modify ~/fisco/dist_Group1_A/conf/applicationContext.xml to the following configuration (partial information)
```bash
<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
	<property name="allChannelConnections">
		<list>
			<bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
				<property name="groupId" value="1" />
					<property name="connectionsStr">
					<list>
						<value>127.0.0.1:20200</value>
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
				<value>DB_Group1_A</value>
			</list>
		</property>
	<property name="pushCallback" ref="DBHandler"/>
</bean>
<!-- database connection configuration -->
	<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
	<property name="driverClassName" value="com.mysql.jdbc.Driver" />
	<!-- please configure db connection here-->
	<property name="url" value="jdbc:mysql://127.0.0.1:3306/bcos_Group1_A?characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull" />
	<property name="username" value="root" />
	<property name="password" value="123456" />
</bean>
```

##### Configure amdb proxy for Group1's NodeB

modify ~/fisco/dist_Group1_B/conf/applicationContext.xml to the following configuration (partial information)

```bash
<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
	<property name="allChannelConnections">
		<list>
			<bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
				<property name="groupId" value="1" />
					<property name="connectionsStr">
					<list>
						<value>127.0.0.1:20201</value>
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
				<value>DB_Group1_B</value>
			</list>
		</property>
	<property name="pushCallback" ref="DBHandler"/>
</bean>

<!-- database connection configuration -->
	<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
	<property name="driverClassName" value="com.mysql.jdbc.Driver" />
	<!-- please configure db connection here-->
	<property name="url" value="jdbc:mysql://127.0.0.1:3306/bcos_Group1_B?characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull" />
	<property name="username" value="root" />
	<property name="password" value="123456" />
</bean>

```
##### Configure amdb proxy for Group2's NodeB
configure ~/fisco/dist_Group2_B/conf/amdb.properties as following content:
modify ~/fisco/dist_Group2_B/conf/applicationContext.xml to the following configuration (partial information)
```bash
<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
	<property name="allChannelConnections">
		<list>
			<bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
				<property name="groupId" value="2" />
					<property name="connectionsStr">
					<list>
						<value>127.0.0.1:20201</value>
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
				<value>DB_Group2_B</value>
			</list>
		</property>
		<property name="pushCallback" ref="DBHandler"/>
	</bean>
<!-- database connection configuration -->
	<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
	<property name="driverClassName" value="com.mysql.jdbc.Driver" />
	<!-- please configure db connection here-->
	<property name="url" value="jdbc:mysql://127.0.0.1:3306/bcos_Group2_B?characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull" />
	<property name="username" value="root" />
	<property name="password" value="123456" />
</bean>

```

##### Configure amdb proxy for Group2's NodeC

modify ~/fisco/dist_Group2_C/conf/applicationContext.xml to the following configuration (partial information)

```bash
<bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
	<property name="allChannelConnections">
		<list>
			<bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
				<property name="groupId" value="2" />
					<property name="connectionsStr">
					<list>
						<value>127.0.0.1:20202</value>
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
				<value>DB_Group2_C</value>
			</list>
		</property>
	<property name="pushCallback" ref="DBHandler"/>
</bean>

<!-- database connection configuration -->
	<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
	<property name="driverClassName" value="com.mysql.jdbc.Driver" />
	<!-- please configure db connection here-->
	<property name="url" value="jdbc:mysql://jdbc:mysql://127.0.0.1:3306/bcos_Group2_C?characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull" />
	<property name="username" value="root" />
	<property name="password" value="123456" />
</bean>
```


### Start amdb proxy
```bash
cd ~/fisco/dist_Group1_A;sh start.sh
cd ~/fisco/dist_Group1_B;sh start.sh
cd ~/fisco/dist_Group2_B;sh start.sh
cd ~/fisco/dist_Group2_C;sh start.sh
```

### Start nodes
```bash
cd ~/fisco/nodes/127.0.0.1;sh start_all.sh
```
### Check process
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

If it starts successfully, you can see there are 4 jave processes and 3 fisco-bcos processes. If it fails, please refer to the log to confirm whether the configuration is correct.

### Check the output of log

Execute the following command to view the number of nodes connected to node0 (other nodes are similar)
```
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```

Normally, you will see an output similar to the following, and you can see that node0 is connecting to the other two nodes from it.

```
info|2019-05-07 21:47:22.849910| [P2P][Service] heartBeat connected count,size=2
info|2019-05-07 21:47:32.849970| [P2P][Service] heartBeat connected count,size=2
info|2019-05-07 21:47:42.850024| [P2P][Service] heartBeat connected count,size=2
```
Execute the following command to check if it is in consensus
```
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```
Normally, the output will continue to output ++++Generating seal to indicate that the consensus is normal.
```
info|2019-05-07 21:48:54.942111| [g:1][p:65544][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=6,tx=0,nodeIdx=1,hash=355790f7...
info|2019-05-07 21:48:56.946022| [g:1][p:65544][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=6,tx=0,nodeIdx=1,hash=4ef772bb...
info|2019-05-07 21:48:58.950222| [g:1][p:65544][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=6,tx=0,nodeIdx=1,hash=48341ee5...
```

### Send transaction by console

Please refer to the section [Sending Transaction by Console](./distributed_storage.html#id12) in "Node Direct Connection to MySQL".
