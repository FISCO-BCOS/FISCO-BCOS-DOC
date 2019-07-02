# One-click deployment

## 1. Premises

| Environment   | Version                   |
| ------ | ---------------------- |
| Java   | jdk1.8.0_121 or above version |
| python | 2.7                    |
| database | mysql-5.6 or above version    |

## 2. Pull code

Execute command:

```shell
git clone https://github.com/FISCO-BCOS/fisco-bcos-browser.git
```
Enter one-click deployment directory

```shell
cd fisco-bcos-browser/deploy
```

## 3. Modify config (except the unchanged ones):

**Note:** 

​    a. Database and data should be prepared in advance.

​    b. The web service port shouldn't be smaller than 1024.

```shell
# vi common.properties
Database IP: sed -i "s/127.0.0.1/${your_db_ip}/g" common.properties
Database prot: sed -i "s/3306/${your_db_port}/g" common.properties
Datebase user name: sed -i "s/dbUsername/${your_db_account}/g" common.properties
Database password: sed -i "s/dbPassword/${your_db_password}/g" common.properties
Database name: sed -i "s/db_browser/${your_db_name}/g" common.properties

Server service port: sed -i "s/8088/${your_server_port}/g" common.properties
Web service port: sed -i "s/8081/${your_web_port}/g" common.properties

Example (Change the database IP from 10.0.0.1 to 0.0.0.0): sed -i "s/10.0.0.1/0.0.0.0/g" application.yml
```

## 4. Deploy

set up all the service:

```shell
python deploy.py startAll
```

stop all the service:

```shell
python deploy.py stopAll
```

other infomation can be found through the help parameter:

```
python deploy.py help
```

## 5. Access the explorer

Typing the server IP and port had set before in the explorer:

```shell 
http://127.0.0.1:8081/#/
```

## 6. Log directory
the default log directory is set as follow:

```
Deployment log：log/
Server log：server/log/
Web log：web/log/
```