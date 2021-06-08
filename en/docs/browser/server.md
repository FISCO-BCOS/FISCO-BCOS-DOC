# Build server of Blockchain explorer 

# Content
> * [Functions](#chapter-1)
> * [Prerequisites](#chapter-2)
> * [Deployment](#chapter-3)
> * [Troubleshooting](#chapter-4)
> * [Appendix](#chapter-5)

# 1. <a id="chapter-1"></a>Functions

This project aims to build the back-end server of the Blockchain explorer. Its workflow includes  extracting the node's local blockchain data into a database, providinfg database access to the front-end webpage.

# 2. <a id="chapter-2"></a>Prerequisites

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

# If you have network issue for exec the command above, please try:
git clone https://gitee.com/FISCO-BCOS/fisco-bcos-browser.git
```

```shell
cd fisco-bcos-browser
```

## 3.2 Compile code

Enter the code directory:

```shell
cd server/fisco-bcos-browser
```

Then, build code:

```shell
gradle build
```

It will create a target directory `dist` after the building.

## 3.3 Modify config

The directory `dist` provides a config template on `dist/conf_template`(**only for reference**)：

```shell
# create a config file on dist/conf  and set the parameters according to the template, or copy and rename the template if it's the initial installation.
 Example：cp dist/conf_template dist/conf -r
```

Enter directory:

```shell
cd dist/conf
```

Modify service config (except for the unchanged parts):

Database server  should be prepared in advance. The build method can be referred  in Appendix.

```shell
modify current server port: sed -i "s/5101/${your_server_port}/g" application.yml
modify database IP: sed -i "s/127.0.0.1/${your_db_ip}/g" application.yml
modify database user name: sed -i "s/dbUsername/${your_db_account}/g" application.yml
modify database password: sed -i "s/dbPassword/${your_db_password}/g" application.yml
modify database name: sed -i "s/db_browser/${your_db_name}/g" application.yml

Example (change the database IP from 127.0.0.1 to 0.0.0.0): sed -i "s/127.0.0.1/0.0.0.0/g" application.yml
```

**Note:**

In real production, we suggest to place the compiled install package (i.e. the  directory `dist`) to the service deployment directory. For example: /data/app/fisco-bcos-browser

## 3.4 Service start/stop

Go to the compiled target directory:

```shell
cd dist
```

```shell
start：sh start.sh
stop：sh stop.sh
review：sh status.sh
```

## 3.5 View log

Enter the compiled target directory:
```shell
cd dist
```

Execute the command:

```shell
tail -f log/fisco-bcos-browser.log
```

# 4. <a id="chapter-4"></a>Troubleshooting

## 4.1 Start/Stop fail

If this problem happens in the above bash script, please try:

```shell
chmod +x *.sh
```

## 4.2 gradle build fail

If the following exception occures during the building process. **Please check the gradle version and make sure it is at v 5.0 or above.**
```
Could not find method annotationProcessor() for arguments [org.projectlombok:lombok:1.18.2] on object of type org.gradle.api.internal.artifacts.dsl.dependencies.DefaultDependencyHandler.
```

# 5. <a id="chapter-5"></a>Appendix

## 5.1 Java environment deployment
Here are simple steps for quick start. For detailed description, please consult [the official website](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Download Java installation package from [the official website](http://www.oracle.com/technetwork/java/javase/downloads/index.html) corresponding to the specific version introduced before, and decompress to the relevant directory:

```shell
mkdir /software
tar -zxvf jdkXXX.tar.gz /software/
```

Configure environment variable:

```shell
export JAVA_HOME=/software/jdk1.8.0_121
export PATH=$JAVA_HOME/bin:$PATH 
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
```

## 5.2 gradle enrironment deployment

Here are simple steps for quick start. For detailed description, please consult [the official website](http://www.gradle.org/downloads).

Download the 5.0 or above version of gradle installation package from [the website](http://www.gradle.org/downloads) and decompress to the relative directory.

```shell
mkdir /software/
unzip -d /software/ gradle-XXX.zip
```

Configure environment variable

```shell
export GRADLE_HOME=/software/gradle-XXX
export PATH=$GRADLE_HOME/bin:$PATH
```

## 5.3 Set-up MySQL

Here we take Centos/Fedora as an example.

a. Transfer to the  root user

```shell
sudo -s
```

b. Install MySQL

```shell
yum install mysql*
# some versions of Linux needs to install mariadb which is a branch of mysql
yum install mariadb*
```

c. Start MySQL service

```shell
service mysqld start
#if mariadb is installed, start it with the following command
systemctl start mariadb.service
```

d. Initialize database user

Access the database with the root user:

```shell
mysql -u root
```

Set password for the root user and grant the access privilege to remote login

```sql
mysql > SET PASSWORD FOR 'root'@'localhost' = PASSWORD('123456');
mysql > GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
```

**Notes:**

1. The database password (123456) provided here is only for example. We strongly recommend you to set more complexer password.
2. The setting of remote access in the example will make the database accessible for the outside network . Please limit the network accessibility according to the specific network topology and permissioned accounts.

Create test user and a database `test`, grant the database privilege to the test user:

```sql
mysql > CREATE user 'test'@'localhost' identified by '123456';
mysql > CREATE DATABASE test;
mysql > GRANT ALL PRIVILEGES ON test.* TO 'test'@'localhost' IDENTIFIED BY '123456' WITH GRANT OPTION;
```

e. Test the connection

Open another terminal and check the database connection with the test user :

```shell
mysql -utest -p123456 -h 127.0.0.1 -P 3306
```

If login successfully, execute the following SQL commands to check the privilege granting.

```sql
mysql > SHOW DATABASES;
mysql > USE test;
```

f. Create database

Login database with root user

```shell
mysql -uroot -p
```

Create database and grant the privilege to test user

```sql
mysql > CREATE DATABASE db_browser;
mysql > GRANT ALL PRIVILEGES ON db_browser.* TO 'test'@'localhost' IDENTIFIED BY '123456' WITH GRANT OPTION;
```

### 5.3.1 Common issues 

**After installing MySQL on centos of Tencent Cloud , it reports the exception: Access denied for user 'root'@'localhost'**

1. Edit /etc/my.cnf, add the follow  in the bottom of [mysqld]

```
   skip-grant-tables 
```

2. Save and restart MySQL

```shell
   service mysqld restart 
```

3. Input the following command, press enter, input password and press enter again to login MySQL

```shell
   mysql -uroot -p mysql  
```
