# 一键部署

标签：``区块链浏览器`` ``部署`` 

----

本文档试用于v2.1.0及之后版本一键部署。v2.1.0之前版本一键部署可参考[文档](./deployOld.md)。

## 1.前提条件

| 环境   | 版本                   |
| ------ | ---------------------- |
| Java   | JDK8或以上版本 |
| MySQL | MySQL-5.6或以上版本 |
| Python | Python3.5+ |
| PyMySQL | 使用python3时需安装 |

### 检查环境

#### 平台要求

推荐使用CentOS 7.2+, Ubuntu 16.04及以上版本, 一键部署脚本将自动安装`openssl, curl, wget, git, nginx, dos2unix`相关依赖项。

其余系统可能导致安装依赖失败，可自行安装`openssl, curl, wget, git, nginx, dos2unix`依赖项后重试

#### 检查Java

推荐JDK8-JDK13版本，使用OracleJDK[安装指引](#jdk)：

```
java -version
```

*注意：不要用`sudo`执行安装脚本*

#### 检查mysql

MySQL-5.6或以上版本：

```
mysql --version
```

- Mysql安装部署可参考[数据库部署](#mysql)

#### 检查Python

<span id="checkpy"></span>

使用Python3.6或以上版本：

```
python3 --version
```

- Python3安装部署可参考[Python部署](#python3)

#### PyMySQL部署（Python3.6+）

<span id="pymysql"></span>

Python3.6及以上版本，需安装PyMysql依赖包：

- CentOS

  ```
  sudo yum -y install python36-pip
  sudo pip3 install PyMySQL
  ```

- Ubuntu

  ```
  sudo apt-get install -y python3-pip
  sudo pip3 install PyMySQL
  ```

 CentOS或Ubuntu不支持pip命令的话，可以使用以下方式：

```
  git clone https://github.com/PyMySQL/PyMySQL
  cd PyMySQL/
  python3 setup.py install
```

## 2.拉取安装脚本

获取部署安装包：
```shell
wget https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/fisco-bcos-browser/releases/download/v2.2.3/browser-deploy.zip
```
解压安装包：
```shell
unzip browser-deploy.zip
```
进入目录：
```shell
cd browser-deploy
```

## 3.修改配置（没有变化的可以不修改）

① 可以使用以下命令修改，也可以直接修改文件（vi common.properties）

② 数据库需要提前安装（数据库安装请参看 [数据库部署](#mysql)）

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

## 4.部署

部署所有服务：

```shell
python3 deploy.py installAll
```

停止所有服务：

```shell
python3 deploy.py stopAll
```

启动所有服务：

```shell
python3 deploy.py startAll
```

单独启停命令和说明可查看帮助：

```shell
python3 deploy.py help
```

**备注：** 部署过程出现问题可以查看 [常见问题](#q&a)

## 5.状态检查

成功部署后，可以根据以下步骤**确认各子服务是否启动成功**

### 检查子系统进程

通过`ps`命令，检查各子系统的进程是否存在

- 包含：后端server进程和前端的`nginx`进程

检查方法如下，若无输出，则代表进程未启动，需要到该子系统的日志中[检查日志错误信息](#checklog)，并根据错误提示或本文档的[常见问题](#q&a)进行排查

- 检查后端server进程

```
$ ps -ef | grep org.bcos.browser
```

输出如下

```
root     91851      1  0 Mar31 ?        00:04:08 /usr/local/jdk1.8.0_181/bin/java -cp conf/:apps/*:lib/* org.bcos.browser.Application
```

- 检查前端的nginx进程

```
$ ps -ef | grep browser |grep nginx       
```

输出如下

```
root      112543      1  0 Mar23 ?        00:00:00 nginx: master process /usr/sbin/nginx -c /data/home/webase/webase/browser-deploy/comm/nginx.conf
```

### 检查进程端口

通过`netstat`命令，检查各子系统进程的端口监听情况。

检查方法如下，若无输出，则代表进程端口监听异常，需要到该子系统的日志中[检查日志错误信息](#checklog)，并根据错误提示或本文档的[常见问题](#q&a)进行排查

- 检查后端server端口(默认为5101)是否已监听

```
$ netstat -anlp | grep 5101
```

输出如下

```
tcp        0      0 0.0.0.0:5101            0.0.0.0:*               LISTEN      91851/java
```

- 检查前端端口(默认为5100)在nginx是否已监听

```
$ netstat -anlp | grep 5100
```

输出如下

```
tcp        0      0 0.0.0.0:5100            0.0.0.0:*               LISTEN      112543/nginx: maste 
```

<span id="checklog"></span>

### 检查服务日志 

<span id="logpath"></span>

#### 日志路径如下：

```
部署日志：log/
后端日志：server/log/
前端日志：web/log/
```

#### 检查服务日志有无错误信息

- 如果各个子服务的进程**已启用**且端口**已监听**，可直接访问下一章节[访问](#access)

- 如果上述检查步骤出现异常，如检查不到进程或端口监听，则需要按[日志路径](#logpath)进入**异常子服务**的日志目录，检查该服务的日志


启动失败或无法使用时，欢迎到`区块链浏览器`[提交Issue](https://github.com/FISCO-BCOS/fisco-bcos-browser/issues)或到技术社区共同探讨。

- 提交Issue或讨论问题时，可以在issue中配上自己的**环境配置，操作步骤，错误现象，错误日志**等信息，方便社区用户快速定位问题

<span id="access"></span>

## 6.访问

例如：在浏览器输入以下访问地址，IP为部署服务器IP，端口为前端服务端口

```
http://127.0.0.1:5100/
```

**备注：** 

- 部署服务器IP和前端服务端口需对应修改，网络策略需开通
- 区块链浏览器使用请查看[使用介绍](../browser.html#id1)

## 7.附录

<span id="jdk"></span>

### 7.1 Java环境部署

<span id="centosjava"></span>

#### CentOS环境安装Java

**注意：CentOS下OpenJDK无法正常工作，需要安装OracleJDK[下载链接](https://www.oracle.com/technetwork/java/javase/downloads/index.html)。**

```
# 创建新的文件夹，安装Java 8或以上的版本，推荐JDK8-JDK13版本，将下载的jdk放在software目录
# 从Oracle官网(https://www.oracle.com/technetwork/java/javase/downloads/index.html)选择Java 8或以上的版本下载，例如下载jdk-8u201-linux-x64.tar.gz
$ mkdir /software

# 解压jdk
$ tar -zxvf jdk-8u201-linux-x64.tar.gz

# 配置Java环境，编辑/etc/profile文件
$ vim /etc/profile

# 打开以后将下面三句输入到文件里面并保存退出
export JAVA_HOME=/software/jdk-8u201  #这是一个文件目录，非文件
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

# 生效profile
$ source /etc/profile

# 查询Java版本，出现的版本是自己下载的版本，则安装成功。
java -version
```

<span id="ubuntujava"></span>

#### Ubuntu环境安装Java

```
  # 安装默认Java版本(Java 8或以上)
  sudo apt install -y default-jdk
  # 查询Java版本
  java -version
```

<span id="mysql"></span>

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

<span id="python3"></span>

### 7.3. Python部署

python版本要求使用python3.x, 推荐使用python3.6及以上版本

- CentOS

  ```
  sudo yum install -y python36
  sudo yum install -y python36-pip
  ```

- Ubuntu

  ```
  // 添加仓库，回车继续
  sudo add-apt-repository ppa:deadsnakes/ppa
  // 安装python 3.6
  sudo apt-get install -y python3.6
  sudo apt-get install -y python3-pip
  ```

<span id="q&a"></span>

## 8.常见问题

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

### 8.2. Python命令出错

- SyntaxError报错

```
  File "deploy.py", line 62
    print helpMsg
                ^
SyntaxError: Missing parentheses in call to "print". Did you mean print(helpMsg)?
```

- 找不到fallback关键字

```
File "/home/ubuntu/webase-deploy/comm/utils.py", line 127, in getCommProperties
    value = cf.get('common', paramsKey,fallback=None)
TypeError: get() got an unexpected keyword argument 'fallback'
```

答：检查[Python版本](#checkpy)，推荐使用python3.6及以上版本

### 8.3 使用Python3时找不到pymysql

```
Traceback (most recent call last):
...
ImportError: No module named 'pymysql'
```

答：需要安装PyMySQL，安装请参看 [pymysql](#pymysql)

### 8.4 部署时数据库访问报错

```
...
checking database connection
Traceback (most recent call last):
  File "/data/temp/browser/fisco-bcos-browser/deploy/comm/mysql.py", line 21, in dbConnect
    conn = mdb.connect(host=mysql_ip, port=mysql_port, user=mysql_user, passwd=mysql_password, charset='utf8')
OperationalError: (1045, "Access denied for user 'root'@'localhost' (using password: YES)")
```

答：确认数据库用户名和密码

### 8.5 Server启动失败
答：1: 请检查是否设置了JAVA_HOME。 2: 检查gradle版本是否在5.0以上。 3: 检查jdk版本，如果使用的是jdk9或者更高版本，需要在项目启动时加入 –add-modules java.xml.bind 后缀，但是这种方法只能在JDK9或者10去使用。修改位置：server/start.sh文件，start()函数，nohub $JAVA_HOME/bin/java 语句之后加上 –add-modules java.xml.bind

### 8.6 server启动成功，但提示启动失败

答：将openjdk换成oracle jdk，因为openjdk中缺少一些组件。

