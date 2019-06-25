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

## 3. Enter one-click deployment directory
```shell
cd fisco-bcos-browser/deploy
```

## 4. Modify config (except the unchanged ones):

**Note:**1. Database and data should be prepared in advance. 2. The web service port shouldn't be smaller than 1024.

```shell
Database IP：sed -i "s/10.0.0.1/${your_db_ip}/g" common.properties
Datebase user name：sed -i "s/root/${your_db_account}/g" common.properties
Database password：sed -i "s/123456/${your_db_password}/g" common.properties
Database name：sed -i "s/testDB/${your_db_name}/g" common.properties

Deployment server IP：sed -i "s/127.0.0.1/${your_server_ip}/g" common.properties
Web service port：sed -i "s/8088/${your_server_port}/g" common.properties
Server service port：sed -i "s/8081/${your_web_port}/g" common.properties

Example (Change the database IP from 10.0.0.1 to 0.0.0.0)：sed -i "s/10.0.0.1/0.0.0.0/g" application.yml
```



## 4. Deploy
```shell
python deploy.py run
```

## 5. Access
Example: input the following url in the explorer with deployment server IP and web service port.

```
http://127.0.0.1:8081/#/
```

## 6. Log route
```
Deployment log：log/
Server log：server/log/
Web log：web/log/
```


