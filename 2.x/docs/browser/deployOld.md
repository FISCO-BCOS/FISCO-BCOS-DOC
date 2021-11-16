# 一键部署说明

## 1、前提条件

| 环境   | 版本                   |
| ------ | ---------------------- |
| Java   | JDK8或以上版本 |
| MySQL | MySQL-5.6或以上版本 |
| Python | Python3.4+ |
| PyMySQL | 使用python3时需安装 |

### 检查环境

#### 检查Java

JDK8或以上版本：

```
java -version
```

- Java推荐使用[OpenJDK](#id10) ，建议从[OpenJDK网站](https://jdk.java.net/java-se-ri/11) 自行下载。

#### 检查mysql

MySQL-5.6或以上版本：

```
mysql --version
```

- Mysql安装部署可参考[数据库部署](#id14)

#### 检查Python

Python3.4或以上版本：

```
python --version
```

- Python安装部署可参考[Python部署](#id17)

#### PyMySQL部署（Python3.4+）

**备注** 使用python2.7+时，需安装MySQL-python，推荐参考[Mysql-python安装示例](#mysql-python)的python2指南进行安装；

Python3.4及以上版本，需安装PyMysql依赖包：

- CentOS

  ```
  sudo pip3 install PyMySQL
  ```

  不支持pip命令的话，可以使用以下方式：

  ```
  git clone https://github.com/PyMySQL/PyMySQL
  cd PyMySQL/
  python3 setup.py install
  ```

- Ubuntu

  ```
  sudo apt-get install -y python3-pip
  sudo pip3 install PyMySQL
  ```

## 2、拉取代码

执行命令：

```shell
git clone https://github.com/FISCO-BCOS/fisco-bcos-browser.git

# 若因网络问题导致长时间下载失败，可尝试以下命令
git clone https://gitee.com/FISCO-BCOS/fisco-bcos-browser.git
```

进入目录：

```shell
cd fisco-bcos-browser/deploy
```

## 3、修改配置（没有变化的可以不修改）

① 可以使用以下命令修改，也可以直接修改文件（vi common.properties）

② 数据库需要提前安装（数据库安装请参看 [数据库部署](#id14)）

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

启动所有服务：

```shell
python deploy.py startAll
```

单独启停命令和说明可查看帮助：

```shell
python deploy.py help
```

**备注：** 部署过程出现问题可以查看 [常见问题](#id19)

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

此处给出OpenJDK安装简单步骤，供快速查阅。更详细的步骤，请参考[官网](https://openjdk.java.net/install/index.html)。

#### ① 安装包下载

从[官网](https://jdk.java.net/java-se-ri/11)下载对应版本的java安装包，并解压到服务器相关目录

```shell
mkdir /software
tar -zxvf openjdkXXX.tar.gz /software/
```

#### ② 配置环境变量

- 修改/etc/profile

```
sudo vi /etc/profile
```

- 在/etc/profile末尾添加以下信息

```shell
JAVA_HOME=/software/jdk-11
PATH=$PATH:$JAVA_HOME/bin
CLASSPATH==.:$JAVA_HOME/lib
export JAVA_HOME CLASSPATH PATH
```

- 重载/etc/profile

```
source /etc/profile
```

#### ③ 查看版本

```
java -version
```

### 7.2. 数据库部署

此处以Centos安装*MariaDB*为例。*MariaDB*数据库是 MySQL 的一个分支，主要由开源社区在维护，采用 GPL 授权许可。*MariaDB*完全兼容 MySQL，包括API和命令行。其他安装方式请参考[MySQL官网](https://dev.mysql.com/downloads/mysql/)。

#### ① 安装MariaDB

- 安装命令

```shell
sudo yum install -y mariadb*
```

- 启停

```shell
启动：sudo systemctl start mariadb.service
停止：sudo systemctl stop  mariadb.service
```

- 设置开机启动

```
sudo systemctl enable mariadb.service
```

- 初始化

```shell
执行以下命令：
sudo mysql_secure_installation
以下根据提示输入：
Enter current password for root (enter for none):<–初次运行直接回车
Set root password? [Y/n] <– 是否设置root用户密码，输入y并回车或直接回车
New password: <– 设置root用户的密码
Re-enter new password: <– 再输入一次你设置的密码
Remove anonymous users? [Y/n] <– 是否删除匿名用户，回车
Disallow root login remotely? [Y/n] <–是否禁止root远程登录，回车
Remove test database and access to it? [Y/n] <– 是否删除test数据库，回车
Reload privilege tables now? [Y/n] <– 是否重新加载权限表，回车
```

#### ② 授权访问和添加用户

- 使用root用户登录，密码为初始化设置的密码

```
mysql -uroot -p -h localhost -P 3306
```

- 授权root用户远程访问

```sql
mysql > GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
mysql > flush PRIVILEGES;
```

- 创建test用户并授权本地访问

```sql
mysql > GRANT ALL PRIVILEGES ON *.* TO 'test'@localhost IDENTIFIED BY '123456' WITH GRANT OPTION;
mysql > flush PRIVILEGES;
```

**安全温馨提示：**

- 例子中给出的数据库密码（123456）仅为样例，强烈建议设置成复杂密码
- 例子中root用户的远程授权设置会使数据库在所有网络上都可以访问，请按具体的网络拓扑和权限控制情况，设置网络和权限帐号

#### ③ 测试连接和创建数据库

- 登录数据库

```shell
mysql -utest -p123456 -h localhost -P 3306
```

- 创建数据库

```sql
mysql > create database db_browser;
```

### 7.3. Python部署

- CentOS

  ```
  sudo yum install -y python-requests
  ```

- Ubuntu

  ```
  sudo apt-get install -y python-requests
  ```

### 7.4. 安装MySql python依赖包

#### 查看python版本

```
python --version
```

python3.4+ 安装Mysql依赖包，可参考 [检查环境-PyMysql](#pymysql-python3-4)

#### 4.1 MySQL-python部署（Python2.7）

- CentOS

  ```
  sudo yum install -y MySQL-python
  ```

- Ubuntu

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

### 8.2. 使用Python2时找不到MySQLdb

```
Traceback (most recent call last):
...
ImportError: No module named MySQLdb
```

答：需要安装MySQL-python，安装请参看 [MySQL-python](#mysql-python-python2-7)

### 8.3. 使用Python3时找不到pymysql

```
Traceback (most recent call last):
...
ImportError: No module named 'pymysql'
```

答：需要安装PyMySQL，安装请参看 [pymysql](#pymysql-python3-4)

### 8.4. 安装MySQL-python遇到问题

```
Command "python setup.py egg_info" failed with error code 1
```

答：运行下面两个命令

```
pip install --upgrade setuptools
python -m pip install --upgrade pip
```

### 8.5 部署时编译包下载慢

```
...
Connecting to github-production-release-asset-2e65be.s3.amazonaws.com (github-production-release-asset-2e65be.s3.amazonaws.com)|52.216.112.19|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 22793550 (22M) [application/octet-stream]
Saving to: ‘fisco-bcos-browser.zip’

 0% [                                                                                                                                ] 77,974      37.8KB/s    
```

答：部署过程会下载工程编译包，可能会因为网络原因导致过慢。此时，可以先手动下载 [编译包](https://github.com/FISCO-BCOS/fisco-bcos-browser/releases/download/v2.0.2/fisco-bcos-browser.zip)，再上传至服务器deploy目录，在部署过程中根据提示不再重新下载编译包。

### 8.6 部署时数据库访问报错

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

### 8.7 Server启动失败
答：请检查是否设置了JAVA_HOME

