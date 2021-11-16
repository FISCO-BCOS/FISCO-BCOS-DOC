# 浏览器后端服务

标签：``后端服务`` ``区块链浏览器`` 

----

## 1.功能说明

本工程是区块链浏览器的后端服务，功能是解析节点数据储存数据库，向前端提供数据接口，页面展示。

## 2.前提条件

| 环境     | 版本              |
| ------ | --------------- |
| Java   | JDK8或以上版本 |
| gradle | gradle-5.0或以上版本 |
| 数据库    | mysql-5.6或以上版本  |

备注：安装说明请参看 [附录](#appendix)。

## 3.部署说明

### 3.1 拉取代码

执行命令：

```shell
git clone https://github.com/FISCO-BCOS/fisco-bcos-browser.git

# 若因网络问题导致长时间下载失败，可尝试以下命令
git clone https://gitee.com/FISCO-BCOS/fisco-bcos-browser.git
```

```shell
cd fisco-bcos-browser
```

### 3.2 编译代码

（1）进入目录：
```shell
cd server/fisco-bcos-browser
```

（2）执行构建命令：
```shell
gradle build
```
构建完成后，会在目录中生成已编译的代码目录dist。

### 3.3 数据初始化

（1）新建数据库

```
#登录MySQL:
mysql -u ${your_db_account} -p${your_db_password}  例如：mysql -u root -p123456
#新建数据库：
CREATE DATABASE IF NOT EXISTS {your_db_name} DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
```

（2）修改脚本配置

进入数据库脚本目录

```shell
cd  dist/script
```

修改数据库连接信息：

```shell
修改数据库名称：sed -i "s/db_browser/${your_db_name}/g" browser.sh
修改数据库用户名：sed -i "s/dbUsername/${your_db_account}/g" browser.sh
修改数据库密码：sed -i "s/dbPassword/${your_db_password}/g" browser.sh
```

例如：将数据库用户名修改为root，则执行：

```shell
sed -i "s/dbUsername/root/g" browser.sh
```

（2）运行数据库脚本

执行命令：bash  browser.sh  ${dbIP}  ${dbPort}
如：

```shell
bash browser.sh 127.0.0.1 3306
```

### 3.4 修改配置

（1）dist目录中提供了一份配置模板dist/conf_template，**供拷贝参考**：

```shell
 需要根据配置模板生成一份实际配置dist/conf。初次安装可直接拷贝。
 例如：cp dist/conf_template dist/conf -r
```
（2）进入目录：
```shell
cd dist/conf
```

（3）修改服务配置（没变化可以不修改）：

数据库服务器，和数据库需要提前准备，创建方法可以参照 [数据库部署](#mysql)。
```shell
修改当前服务端口：sed -i "s/5101/${your_server_port}/g" application.yml
修改数据库IP：sed -i "s/127.0.0.1/${your_db_ip}/g" application.yml
修改数据库用户名：sed -i "s/dbUsername/${your_db_account}/g" application.yml
修改数据库密码：sed -i "s/dbPassword/${your_db_password}/g" application.yml
修改数据库名称：sed -i "s/db_browser/${your_db_name}/g" application.yml

例子（将数据库IP由127.0.0.1改为0.0.0.0）：sed -i "s/127.0.0.1/0.0.0.0/g" application.yml
```

**温馨提示：**

1. 实际生产中建议将编译后的安装包（dist目录）放到服务部署目录。例如/data/app/fisco-bcos-browser

### 3.5 服务启停

进入到已编译的代码根目录：
```shell
cd dist
```
```shell
启动：sh start.sh
停止：sh stop.sh
检查：sh status.sh
```

### 3.6 查看日志

进入到已编译的代码根目录：
```shell
cd dist
```

查看
```shell
tail -f log/fisco-bcos-browser.log
```

## 4.问题排查

### 4.1 启停失败
如果脚本执行出现问题，尝试以下操作：
```shell
chmod +x *.sh
```
### 4.2 gradle build失败

```shell
gradle build
```
执行后，出现下面错误。**请检查gradle版本，需要使用5.0以上版本。** 

```
Could not find method annotationProcessor() for arguments [org.projectlombok:lombok:1.18.2] on object of type org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler.
```

### 4.3 腾讯云centos mysql安装完成后，登陆报错：Access denied for user 'root'@'localhost'

1. 编辑 /etc/my.cnf ，在[mysqld] 部分最后添加一行

```
   skip-grant-tables 
```

2. 保存后重启mysql

```shell
   service mysqld restart 
```

3. 输入以下命令，回车后输入密码再回车登录Mysql

```shell
   mysql -uroot -p mysql  
```

<span id="appendix"></span>

## 5.附录

<span id="jdk"></span>

### 5.1 Java环境部署

#### CentOS环境安装Java

<span id="centosjava"></span>

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

#### Ubuntu环境安装Java

<span id="ubuntujava"></span>

```
  # 安装默认Java版本(Java 8或以上)
  sudo apt install -y default-jdk
  # 查询Java版本
  java -version
```

### 5.2 gradle环境部署

此处给出简单步骤，供快速查阅。更详细的步骤，请参考[官网](http://www.gradle.org/downloads)。

（1）从[官网](http://www.gradle.org/downloads)下载对应5.0以上版本的gradle安装包，并解压到相应目录

```shell
mkdir /software/
unzip -d /software/ gradle-XXX.zip
```

（2）配置环境变量

```shell
export GRADLE_HOME=/software/gradle-XXX
export PATH=$GRADLE_HOME/bin:$PATH
```

<span id="mysql"></span>

### 5.3 数据库部署

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
