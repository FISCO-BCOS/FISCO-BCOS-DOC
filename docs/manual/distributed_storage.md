# 使用分布式存储

标签：``分布式存储`` ``MySQL`` 

----
## 安装MySQL

当前支持的分布式数据库是MySQL,在使用分布式存储之前，需要先搭建MySQL服务，在Ubuntu和CentOS服务器上的配置方式如下：

Ubuntu：执行下面三条命令，安装过程中，配置 root 账户密码。
```bash
sudo apt install -y mysql-server mysql-client libmysqlclient-dev
```
启动 MySQL 服务并登陆:
root 账户密码。
```bash
service msyql start
mysql -uroot -p
```

CentOS: 执行下面两条命令进行安装。
```bash
yum install mysql*
#某些版本的linux，需要安装mariadb，mariadb是mysql的一个分支
yum install mariadb*
```

启动 MySQL 服务，登陆并为 root 用户设置密码。
```bash
service mysqld start
#若安装了mariadb，则使用下面的命令启动
service mariadb start
mysql -uroot -p
mysql> set password for root@localhost = password('123456');
```

## 配置MySQL参数
### 查看配置文件my.cnf
```bash
mysql --help | grep 'Default options' -A 1
```
执行之后可以看到如下如下数据

```
Default options are read from the following files in the given order:
/etc/mysql/my.cnf /etc/my.cnf ~/.my.cnf
```
### 配置my.cnf
mysql依次从/etc/mysql/my.cnf，/etc/my.cnf，~/.my.cnf中加载配置。依次查找这几个文件，找到第一个存在的文件，在[mysqld]段中新增如下内容（如果存在则修改值）。
```
max_allowed_packet = 1024M
sql_mode =STRICT_TRANS_TABLES
ssl=0
default_authentication_plugin = mysql_native_password
```

### 重启mysql-sever，验证参数。

Ubuntu：执行如下命令重启
```bash
service mysql restart
```

CentOS：执行如下命令重启
```bash
service mysqld start
#若安装了mariadb，则使用下面的命令启动
service mariadb start
```

验证参数过程
```bash
mysql -uroot -p
#执行下面命令，查看max_allowed_packet的值
MariaDB [(none)]>  show variables like 'max_allowed_packet%';
+--------------------+------------+
| Variable_name      | Value      |
+--------------------+------------+
| max_allowed_packet | 1073741824 |
+--------------------+------------+
1 row in set (0.00 sec)

#执行下面命令，查看sql_mode的值
MariaDB [(none)]>  show variables like 'sql_mode%';
+---------------+---------------------+
| Variable_name | Value               |
+---------------+---------------------+
| sql_mode      | STRICT_TRANS_TABLES |
+---------------+---------------------+
1 row in set (0.00 sec)
```


## 节点直连MySQL
FISCO BCOS在2.0.0-rc3之后，支持节点通过连接池直连MySQL，相对于代理访问MySQL方式，配置简单，不需要手动创建数据库。配置方法请参考:

### 逻辑架构图
多群组架构是指区块链节点支持启动多个群组，群组间交易处理、数据存储、区块共识相互隔离的。因此群组下的每一个节点对应一个数据库实例，例如，区块链网络中，有三个节点A,B,C，其中A,B属于Group1,B,C属于Group2。节点A和C分别对应1个数据库实例，B节点对应了2个数据库实例，逻辑架构图如下
![](../../images/storage/storage.png)
如上图所示，节点B属于多个群组，不同群组下的同一个节点，对应的数据库实例是分开的，为了区分不同群组下的同一个节点，将A,B,C三个节点，分别用Group1_A（Group1下的A节点，下同），Group1_B，Group2_B，Group2_C表示。

下面以上图为例，描述搭建配置过程。

### 节点搭建
使用分布式存储之前，需要完成联盟链的搭建和多群组的配置，具体参考如下步骤。

#### 准备依赖

```bash
mkdir -p ~/fisco && cd ~/fisco
# 获取build_chain.sh脚本
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.7.2/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/v2.7.2/tools/build_chain.sh && chmod u+x build_chain.sh`
```

#### 生成配置文件
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

#### 使用build_chain搭建区块链
```bash
### 搭建区块链（请先确认30300~30302，20200~20202，8545~8547端口没有被占用）
### 这里区别是在命令后面追加了参数"-s MySQL" 以及换了端口。
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
### 修改节点ini文件
group.[群组].ini配置文件中，和本特性相关的是MySQL的配置信息。假设MySQL的配置信息如下：
```bash
|节点|db_ip|db_port|db_username|db_passwd|db_name|
|Group1_A|127.0.0.1|3306|root|123456|db_Group1_A|
|Group1_B|127.0.0.1|3306|root|123456|db_Group1_B|
|Group2_B|127.0.0.1|3306|root|123456|db_Group2_B|
|Group2_C|127.0.0.1|3306|root|123456|db_Group2_C|
```

### 修改node0下的group.1.ini配置

修改~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini[storage]段的内容，配置如下内容。db_passwd为对应的密码。
```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group1_A
    	db_passwd=
```

### 修改node1下的group.1.ini配置

修改~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini[storage]段的内容，新增如下内容。db_passwd为对应的密码。
```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group1_B
    	db_passwd=
```

### 修改node1下的group.2.ini配置

修改~/fisco/nodes/127.0.0.1/node1/conf/group.2.ini[storage]段的内容，新增如下内容。db_passwd为对应的密码。
```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group2_B
    	db_passwd=
```
### 修改node2下的group.2.ini配置

修改~/fisco/nodes/127.0.0.1/node2/conf/group.2.ini[storage]段的内容，新增如下内容。db_passwd为对应的密码。
```bash
    	db_ip=127.0.0.1
    	db_port=3306
    	db_username=root
    	db_name=db_Group2_C
    	db_passwd=
```
### 启动节点
```bash
cd ~/fisco/nodes/127.0.0.1;sh start_all.sh
```
### 检查进程
```bash
ps -ef|grep fisco-bcos|grep -v grep
fisco   111061      1  0 16:22 pts/0    00:00:04 /data/home/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco   111065      1  0 16:22 pts/0    00:00:04 /data/home/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco   122910      1  1 16:22 pts/0    00:00:02 /data/home/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
```
启动成功，3个fisco-bcos进程。不成功的话请参考日志确认配置是否正确。

### 检查日志输出
执行下面指令，查看节点node0链接的节点数（其他节点类似）
```
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```
正常情况会看到类似下面的输出，从输出可以看出node0与另外2个节点有连接。
```
info|2019-05-28 16:28:57.267770|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:07.267935|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:17.268163|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:27.268284|[P2P][Service] heartBeat,connected count=2
info|2019-05-28 16:29:37.268467|[P2P][Service] heartBeat,connected count=2
```
执行下面指令，检查是否在共识
```
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```
正常情况会不停输出++++Generating seal表示共识正常。
```
info|2019-05-28 16:26:32.454059|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=28,tx=0,nodeIdx=3,hash=c9c859d5...
info|2019-05-28 16:26:36.473543|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=28,tx=0,nodeIdx=3,hash=6b319fa7...
info|2019-05-28 16:26:40.498838|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=28,tx=0,nodeIdx=3,hash=2164360f...
```

### 使用控制台发送交易

#### 准备依赖

```bash

cd ~/fisco
curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.7.1/download_console.sh

# 若因为网络问题导致长时间无法执行上面的命令，请尝试下面的命令：
curl -#LO https://gitee.com/FISCO-BCOS/console/releases/download/v2.7.1/download_console.sh

bash download_console.sh
cp -n console/conf/config-example.toml console/conf/config.toml
cp nodes/127.0.0.1/sdk/* console/conf/
```

#### 修改配置文件
将~/fisco/console/conf/config.toml修改为如下配置(部分信息)
```bash
[network]
peers=["127.0.0.1:20300", "127.0.0.1:20301"]    # The peer list to connect
```
#### 启用控制台
```bash
cd ~/fisco/console
sh start.sh 1
#部署TableTest合约
[group:1]> deploy TableTest
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
```

查看数据库中的表情况
```bash
MySQL -uroot -p123456 -A db_Group1_A
use db_Group1_A;
show tables;
----------------------------------------------------------+
| Tables_in_db_Group1_A                                  |
+----------------------------------------------------------+
| c_8c17cf316c1063ab6c89df875e96c9f0f5b2f744 |
| c_f69a2fa2eca49820218062164837c6eecc909abd |
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

查看数据库中的表情况
```bash
show tables;
+----------------------------------------------------------+
| Tables_in_db_Group1_A                                  |
+----------------------------------------------------------+
| c_8c17cf316c1063ab6c89df875e96c9f0f5b2f744 |
| c_f69a2fa2eca49820218062164837c6eecc909abd |
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
| u_t_test                                             |
+----------------------------------------------------------+
```

往表里面插入一条数据
```bash
#往表里插入数据
call TableTest 0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744 insert "fruit" 100 "apple"
0x082ca6a5a292f1f7b20abeb3fb03f45e0c6f48b5a79cc65d1246bfe57be358d1
```

打开MySQL客户端，查询u_t_test表数据
```bash
#查看用户表中的数据
select * from u_t_test\G;
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
