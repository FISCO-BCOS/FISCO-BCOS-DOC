# 使用MySQL存储引擎

标签：``分布式存储`` ``MySQL`` ``CRUD``

----

本章介绍使用FISCO BCOS搭建基于MySQL存储引擎的分布式存储区块链网络，通过在单机上部署一条基于MySQL分布式存储的4节点FISCO BCOS联盟链，帮助用户掌握FISCO BCOS分布式存储区块链网络的部署流程，请参考[这里](../manual/hardware_requirements.md)获取**系统和硬件要求**信息。


## 1. 安装和配置MySQL

由于FISCO BCOS目前支持的分布式数据库是MySQL，因此在搭建分布式存储区块链节点之前，须先安装和配置MySQL服务。

### 1.1 安装MySQL

本节介绍如何在ubuntu和centos系统中安装MySQL.
#### ubuntu系统

**安装MySQL**

执行下面命令在ubuntu系统中安装MySQL:

```bash
sudo apt install -y mysql-server mysql-client libmysqlclient-dev
```

**启动MySQL**

MySQL安装成功后，执行下述命令启动MySQL服务，并设置root账户密码：

```bash
# 启动MySQL服务
sudo service mysql start

# 设置root账户密码
mysql -uroot -p
```

#### centos系统

**安装MySQL**
执行下面命令在centos系统中安装MySQL:

```bash
sudo yum install mariadb*
```

**启动MySQL**

MySQL安装成功后，执行下面命令启动服务，并设置root账户密码：

```bash
# 启动MySQL服务
service mariadb start

# 设置root账户密码(这里将密码设置为123456)
mysql -uroot -p
mysql> set password for root@localhost = password('123456');
```

### 1.2 配置MySQL

**修改MySQL配置文件**

在`/etc/mysql/my.cnf`配置文件的`[mysqld]`部分添加如下配置：

```bash
max_allowed_packet = 1024M
sql_mode =STRICT_TRANS_TABLES
ssl=0
default_authentication_plugin = mysql_native_password
```
**重启MySQL服务**

```bash
# ubuntu系统
sudo service mysql restart

# centos系统
sudo service mariadb restart
```

**登录MySQL客户端，验证参数是否生效**

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

# 查看default_authentication_plugin的值
MariaDB [(none)]> show variables like 'default_authentication_plugin';
+-------------------------------+-----------------------+
| Variable_name                 | Value                 |
+-------------------------------+-----------------------+
| default_authentication_plugin | mysql_native_password |
+-------------------------------+-----------------------+
1 row in set (0.01 sec)
```


## 2. 搭建基于MySQL存储的区块链节点

### 2.1 安装依赖

**ubuntu系统**

```bash
sudo apt install -y openssl curl
```

**centos系统**

```bash
sudo yum install -y openssl openssl-devel curl
```

### 2.2 创建操作目录, 下载安装脚本

```bash
## 创建操作目录
mkdir -p ~/fisco && cd ~/fisco

## 下载脚本
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.7.2/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/build_chain.sh && chmod u+x build_chain.sh`
```

### 2.3 搭建单机4节点分布式存储区块链网络

```eval_rst
.. note::
    请确保机器的 ``30300~30303``, ``20200~20203``, ``8545~8548`` 端口没有被占用。
```

**生成单机4节点区块链节点配置**

```bash
# 进入操作目录
cd ~/fisco

# 搭建单机4节点区块链系统
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545
```

命令执行成功会输出`All completed`信息如下，如果执行出错，请检查`nodes/build.log`文件中的错误信息

```bash
==============================================================
Generating CA key...
==============================================================
Generating keys and certificates ...
Processing IP=127.0.0.1 Total=4 Agency=agency Groups=1
==============================================================
Generating configuration files ...
Processing IP=127.0.0.1 Total=4 Agency=agency Groups=1
==============================================================
[INFO] Start Port      : 30300 20200 8545
[INFO] Server IP       : 127.0.0.1:4
[INFO] Output Dir      : /home/ubuntu/fisco/nodes
[INFO] CA Path         : /home/ubuntu/fisco/nodes/cert/
==============================================================
[INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/ubuntu/fisco/nodes/127.0.0.1/download_console.sh -f
==============================================================
[INFO] All completed. Files in /home/ubuntu/fisco/nodes
```

**修改存储配置为MySQL**

通过群组配置文件`group.[group_id].ini`的`storage`配置项可配置MySQL，详细可参考[这里](../manual/configuration.html#id21).
本教程中，所有节点均以root用户名连接同一个本机MySQL数据库，真实业务场景中，可按需修改数据库相关配置(包括MySQL的IP和端口，连接MySQL的用户名和密码等)。

```bash
# 修改存储类型为mysql
sed -i 's/type=rocksdb/type=mysql/g' ~/fisco/nodes/127.0.0.1/node*/conf/group.1.ini

# 配置数据库用户名和密码(本教程中，所有节点均以root的用户名连接同一个数据库，root用户密码为123456)
sed -i 's/db_username=/db_username=root/g' ~/fisco/nodes/127.0.0.1/node*/conf/group.1.ini
sed -i 's/db_passwd=/db_passwd=123456/g' ~/fisco/nodes/127.0.0.1/node*/conf/group.1.ini

# -----配置每个区块链节点在MySQL中创建的库名-----
# 配置node0的数据库名称为db_node0
sed -i 's/db_name=/db_name=db_node0/g' ~/fisco/nodes/127.0.0.1/node0/conf/group.1.ini

# 配置node1的数据库名称为db_node1
sed -i 's/db_name=/db_name=db_node1/g' ~/fisco/nodes/127.0.0.1/node1/conf/group.1.ini

# 配置node2的数据库名称为db_node2
sed -i 's/db_name=/db_name=db_node2/g' ~/fisco/nodes/127.0.0.1/node2/conf/group.1.ini

# 配置node3的数据库名称为db_node3
sed -i 's/db_name=/db_name=db_node3/g' ~/fisco/nodes/127.0.0.1/node3/conf/group.1.ini
```

**启动区块链节点**

```bash
# 进入操作目录
cd ~/fisco

# 启动所有节点
bash nodes/127.0.0.1/start_all.sh
```

启动成功后会输出如下日志：

```bash
try to start node0
try to start node1
try to start node2
try to start node3
 node2 start successfully
 node1 start successfully
 node0 start successfully
 node3 start successfully
```

### 2.4 检查区块链网络

**检查进程是否启动**

```bash
ps -ef | grep -v grep | grep fisco-bcos
```

正常情况会有类似下面的输出；
如果进程数不为4，则进程没有启动（一般是端口被占用导致的）

```bash
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

**检查网络连接是否正常**

如下，查看节点node0链接的节点数

```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```

正常情况会不停地输出连接信息，从输出可以看出node0与另外3个节点有连接。
```bash
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat,connected count=3
```

**检查区块链共识是否正常**

```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```

正常情况会不停输出`++++Generating seal`，表示共识正常。
```bash
info|2020-12-22 17:24:43.729402|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=2e133146...
info|2020-12-22 17:24:47.740603|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=eb199760...
```

至此，已经搭建好一个基于MySQL的分布式存储区块链网络。


## 3. 使用控制台发起CRUD操作

为了直观显示区块链状态数据在MySQL中的存储形式，本章通过控制台发起CRUD操作，并通过MySQL客户端查询状态数据信息。
正式开始本操作前，请先参考**搭建第一个区块链网络**中的[配置和使用控制台](../installation.html#id8)安装控制台。

**创建表t_demo**

```bash
# 创建表t_demo，包含(name, item_id, item_name)三个字段，其中name是主key
[group:1]> create table t_demo(name varchar, item_id varchar, item_name varchar, primary key(name))
Create 't_demo' Ok.

# 查看t_demo表的信息
[group:1]> desc t_demo
[
    {
        "key_field":"name",
        "value_field":"item_id,item_name"
    }
]
```

**向表中插入数据**

```bash
# 向t_demo表中插入一条数据记录，主key是fruit，item_id为1，item_name为apple1
[group:1]> insert into t_demo (name, item_id, item_name) values (fruit, 1, apple1)
Insert OK:
1 row affected.
```

**查询表中数据**

```bash
# 查询主key为fruit的所有数据记录
[group:1]> select * from t_demo where name = fruit
{name=fruit, item_id=1, item_name=apple1}
1 row in set.
```

**通过MySQL客户端查询t_demo在数据库中的情况**

```bash
# 连接db_node0数据库
mysql -uroot -p123456 -A db_node0

# 查看数据库中的表
show tables;
MariaDB> show tables;
+-----------------------+
| Tables_in_db_node0    |
+-----------------------+
| _sys_block_2_nonces_  |
| _sys_cns_             |
| _sys_config_          |
| _sys_consensus_       |
| _sys_current_state_   |
| _sys_hash_2_block_    |
| _sys_hash_2_header_   |
| _sys_number_2_hash_   |
| _sys_table_access_    |
| _sys_tables_          |
| _sys_tx_hash_2_block_ |
| u_t_demo              |
+-----------------------+

# 查询用户表t_demo的存储信息
MariaDB> select * from u_t_demo;
+--------+-------+----------+-------+---------+-----------+
| _id_   | _num_ | _status_ | name  | item_id | item_name |
+--------+-------+----------+-------+---------+-----------+
| 100025 |     2 |        0 | fruit | 1       | apple1    |
+--------+-------+----------+-------+---------+-----------+
1 row in set (0.00 sec)
```