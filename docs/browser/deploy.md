# 一键部署说明

## 1、前提条件

| 环境         | 版本                   |
| ------------ | ---------------------- |
| Java         | jdk1.8.0_121或以上版本 |
| python       | 2.7                    |
| MySQL-python | 1.2.5                  |
| 数据库       | mysql-5.6或以上版本    |

**备注：** 安装说明请参看 [附录7](./deploy.html#id8)

## 2、拉取代码

执行命令：

```shell
git clone https://github.com/FISCO-BCOS/fisco-bcos-browser.git
```

进入目录：

```shell
cd fisco-bcos-browser/deploy
```

## 3、修改配置（没有变化的可以不修改）

① 可以使用以下命令修改，也可以直接修改文件（vi common.properties）

② 数据库需要提前安装（数据库安装请参看 [附录7.3](./deploy.html#id9)）

③ 服务端口不能小于1024

```shell
数据库IP：sed -i "s/127.0.0.1/${your_db_ip}/g" common.properties
数据库端口：sed -i "s/3306/${your_db_port}/g" common.properties
数据库用户名：sed -i "s/dbUsername/${your_db_account}/g" common.properties
数据库密码：sed -i "s/dbPassword/${your_db_password}/g" common.properties
数据库名称：sed -i "s/db_browser/${your_db_name}/g" common.properties

前端服务端口：sed -i "s/5100/${your_web_port}/g" common.properties
后端服务端口：sed -i "s/5101/${your_server_port}/g" common.properties

例子（将数据库IP由127.0.0.1改为0.0.0.0）：sed -i "s/127.0.0.1/0.0.0.0/g" application.yml
```

## 4、部署

部署所有服务：

```shell
python deploy.py installAll
```

停止所有服务：

```shell
python deploy.py stopAll
```

单独启停命令和说明可查看帮助：

```shell
python deploy.py help
```

**备注：** 部署过程出现问题可以查看 [常见问题8](./deploy.html#id10)

## 5、访问

例如：在浏览器输入以下访问地址，IP为部署服务器IP，端口为前端服务端口

```
http://127.0.0.1:5100/
```

## 6、日志路径
```
部署日志：log/
后端日志：server/log/
前端日志：web/log/
```

## 7、附录

### 7.1 Java环境部署

此处给出简单步骤，供快速查阅。更详细的步骤，请参考[官网](http://www.oracle.com/technetwork/java/javase/downloads/index.html)。

（1）从[官网](http://www.oracle.com/technetwork/java/javase/downloads/index.html)下载对应版本的java安装包，并解压到相应目录

```shell
mkdir /software
tar -zxvf jdkXXX.tar.gz /software/
```

（2）配置环境变量

```shell
export JAVA_HOME=/software/jdk1.8.0_121
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

### 7.2 Python request库安装

```shell
pip install requests 或 sudo yum install -y requests
```

### 7.3 数据库部署

此处以Centos/Fedora为例。

（1）切换到root

```shell
sudo -s
```

（2）安装mysql

```shell
yum install mysql*
#某些版本的linux，需要安装mariadb，mariadb是mysql的一个分支
yum install mariadb*
```

（3）启动mysql

```shell
service mysqld start
#若安装了mariadb，则使用下面的命令启动
systemctl start mariadb.service
```

（4）初始化数据库用户

初次登录

```shell
mysql -u root
```

给root设置密码和授权远程访问

```sql
mysql > SET PASSWORD FOR 'root'@'localhost' = PASSWORD('123456');
mysql > GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
```

**安全温馨提示：**

1. 例子中给出的数据库密码（123456）仅为样例，强烈建议设置成复杂密码
2. 例子中的远程授权设置会使数据库在所有网络上都可以访问，请按具体的网络拓扑和权限控制情况，设置网络和权限帐号

授权test用户本地访问数据库

```sql
mysql > create user 'test'@'localhost' identified by '123456';
```

（5）测试连接

另开一个ssh测试本地用户test是否可以登录数据库

```shell
mysql -utest -p123456 -h 127.0.0.1 -P 3306
```

登陆成功后，执行以下sql语句，若出现错误，则用户授权不成功

```sql
mysql > show databases;
```

（6）创建数据库

登录数据库

```shell
mysql -utest -p123456 -h 127.0.0.1 -P 3306
```

创建数据库

```sql
mysql > create database db_browser;
```
### 7.4 MySQL-python部署

* CentOS

  ```
  sudo yum install -y MySQL-python
  ```

* Ubuntu

  ```
  sudo apt-get install -y python-pip
  sudo pip install MySQL-python
  ```

## 8、常见问题

### 8.1 数据库安装后登录报错

腾讯云centos mysql安装完成后，登录报错：Access denied for user 'root'@'localhost'

① 编辑 /etc/my.cnf ，在[mysqld] 部分最后添加一行

```
skip-grant-tables
```

② 保存后重启mysql

```shell
service mysqld restart
```

③ 输入以下命令，回车后输入密码再回车登录mysql

```
mysql -uroot -p mysql
```

### 8.2 找不到MySQLdb

```
Traceback (most recent call last):
  File "deploy.py", line 4, in <module>
    import comm.check as commCheck
  File "/data/temp/browser/fisco-bcos-browser/deploy/comm/check.py", line 7, in <module>
    from mysql import *
  File "/data/temp/browser/fisco-bcos-browser/deploy/comm/mysql.py", line 6, in <module>
    import MySQLdb as mdb
ImportError: No module named MySQLdb
```

答：MySQL-python安装请参看[附录7.4](./deploy.html#mysql-python)

### 8.3 部署时编译包下载慢

```
...
Connecting to github-production-release-asset-2e65be.s3.amazonaws.com (github-production-release-asset-2e65be.s3.amazonaws.com)|52.216.112.19|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 22793550 (22M) [application/octet-stream]
Saving to: ‘fisco-bcos-browser.zip’

 0% [                                                                                                                                ] 77,974      37.8KB/s    
```

答：部署过程会下载工程编译包，可能会因为网络原因导致过慢。此时，可以先手动下载 [编译包](https://github.com/FISCO-BCOS/fisco-bcos-browser/releases/download/v2.0.2/fisco-bcos-browser.zip)，再上传至服务器deploy目录，在部署过程中根据提示不再重新下载编译包。

### 8.4 部署时数据库访问报错

```
...
checking database connection
Traceback (most recent call last):
  File "/data/temp/browser/fisco-bcos-browser/deploy/comm/mysql.py", line 21, in dbConnect
    conn = mdb.connect(host=mysql_ip, port=mysql_port, user=mysql_user, passwd=mysql_password, charset='utf8')
  File "/usr/lib64/python2.7/site-packages/MySQLdb/__init__.py", line 81, in Connect
    return Connection(*args, **kwargs)
  File "/usr/lib64/python2.7/site-packages/MySQLdb/connections.py", line 193, in __init__
    super(Connection, self).__init__(*args, **kwargs2)
OperationalError: (1045, "Access denied for user 'root'@'localhost' (using password: YES)")
```

答：确认数据库用户名和密码

### 8.5 Server启动失败
答：请检查是否设置了JAVA_HOME

