# Build server of Blockchain explorer 

# Content
> * [Function](#chapter-1)
> * [Premise](#chapter-2)
> * [Deployment](#chapter-3)
> * [Troubleshooting](#chapter-4)
> * [Additional](#chapter-5)

# 1. <a id="chapter-1"></a>Functions

This project is to build server of Blockchain explorer. The function is to analyze the node database and provide data access to front-end webpage.

# 2. <a id="chapter-2"></a>Premise

| Environment     | Version              |
| ------ | --------------- |
| Java   | jdk1.8.0_121 or above version    |
| gradle | gradle-5.0 or above version |
| database    | mysql-5.6 or above version  |
Note: the installation details is attached in Additional.

# 3. <a id="chapter-3"></a>Deployment

## 3.1 Pull code
Execute command:
```shell
git clone https://github.com/FISCO-BCOS/fisco-bcos-browser.git
```

```shell
cd fisco-bcos-browser
```

## 3.2 Compile code

（1）Enter directory:
```shell
cd server/fisco-bcos-browser
```

（2）Execute build code:
```shell
gradle build
```
Compiled code directory dist will be generated after execution of build.

## 3.3 Modify config

（1）Directory dist provides a config template dist/conf_template, **for copy or reference**：

```shell
 generate a real config dist/conf according to the template, or copy the template if it's the initial installation.
 Example：cp dist/conf_template dist/conf -r
```
（2）Enter directory:
```shell
cd dist/conf
```

（3）Modify service config (except for the unchanged parts):

Database server and database should be prepared in advance. The build method is introduced in Additional.
```shell
modify current server port：sed -i "s/8088/${your_server_port}/g" application.yml
modify database IP：sed -i "s/127.0.0.1/${your_db_ip}/g" application.yml
modify database user name: sed -i "s/root/${your_db_account}/g" application.yml
modify database password: sed -i "s/123456/${your_db_password}/g" application.yml
modify database name: sed -i "s/testDB/${your_db_name}/g" application.yml

Example (change the database IP from 127.0.0.1 to 0.0.0.0): sed -i "s/127.0.0.1/0.0.0.0/g" application.yml
```

**Important:**

1. In real production, we suggest to place the compiled install package (dist directory) to the server deployment directory. For example: /data/app/fisco-bcos-browser

## 3.4 Service start/stop

Enter the compiled root directory:
```shell
cd dist
```
```shell
start：sh start.sh
stop：sh stop.sh
review：sh status.sh
```

## 3.5 View log

Enter the compiled root directory:
```shell
cd dist
```

View
```shell
tail -f log/fisco-bcos-browser.log
```

# 4. <a id="chapter-4"></a>Troubleshooting

## 4.1 Start/Stop fail
If this problem happens in execution of script, please try:
```shell
chmod +x *.sh
```
## 4.2 gradle build fail

```shell
gradle build
```
If the following exception happens after execution. **Please check gradle version information, which is needed to be 5.0 or above.**
`Could not find method annotationProcessor() for arguments [org.projectlombok:lombok:1.18.2] on object of type org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler.`

# 5. <a id="chapter-5"></a>Additional

## 5.1 Java environment deployment
Here are simple steps for quick start. For detailed description, please consult [the official website](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

（1）Download the right version of java install packet from [the official website](http://www.oracle.com/technetwork/java/javase/downloads/index.html), and decompress to the relevant directory

```shell
mkdir /software
tar -zxvf jdkXXX.tar.gz /software/
```

（2）Configure environment variable

```shell
export JAVA_HOME=/software/jdk1.8.0_121
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

## 5.2 gradle enrironment deployment

Here are simple steps for quick start. For detailed description, please consult [the official website](http://www.gradle.org/downloads).

（1）Download the 5.0 or above version of gradle install packet from [the website](http://www.gradle.org/downloads) and decompress to the relative directory.

```shell
mkdir /software/
unzip -d /software/ gradle-XXX.zip
```

（2）Configure environment variable

```shell
export GRADLE_HOME=/software/gradle-XXX
export PATH=$GRADLE_HOME/bin:$PATH
```

## 5.3 Deploy database

Here we take Centos/Fedora as an example.

（1）Transfer to root

```shell
sudo -s
```

（2）Install mysql

```shell
yum install mysql*
#some versions of linux needs to install mariadb, a branch of mysql
yum install mariadb*
```

（3）Start mysql

```shell
service mysqld start
#if mariadb is installed, start it with the following command
systemctl start mariadb.service
```

（4）Initialize database user

Initial login
```shell
mysql -u root
```

Set password for root and grant remote access
```sql
mysql > SET PASSWORD FOR 'root'@'localhost' = PASSWORD('123456');
mysql > GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
```

**Important:**

1. The database password (123456) provided here is only for example. We strongly recommend you to set complexer password.
2. The setting of remote access in the example will make data accessible to each network. Please set the netwoek and permissions according to the specific network topology and access status.

grant access of database to test user
```sql
mysql > create user 'test'@'localhost' identified by '123456';
```

（5）Test connection

Open another ssh to test if user test can access database

```shell
mysql -utest -p123456 -h 127.0.0.1 -P 3306
```

After login, execute the following sql sentence. If exception comes out, then the user is failed to be granted access.

```sql
mysql > show databases;
mysql > use test;
```

（6）Build database

Login database

```shell
mysql -utest -p123456 -h 127.0.0.1 -P 3306
```

Create database

```sql
mysql > create database testDB;
```

### 5.3.1 Possible exceptions 
#### 5.3.1.1 After installing TenCent Cloud centos mysql, report exception: Access denied for user 'root'@'localhost'

1. Edit /etc/my.cnf, add this in the final of [mysqld]
```
   skip-grant-tables 
```
2. Save and restart mysql
```shell
   service mysqld restart 
```
3. Input the following command, press enter, input password and press enter again to login Mysql
```shell
   mysql -uroot -p mysql  
```

