

# Build web of Blockchain explorer

This project is to build fisco-bcos web with `vue-cli` framwork.

It adapts to IE9 or above version, 360 browser of compatible version (IE9 core), 360 browser of speed version, chrome.

## 1. Functions

(1) The main functions include Blockchain overview, block and transaction details, node configuration and group switch.

(2) It supports group switch, which needs to configure groups and nodes.

(3) Upload and compile the contracts that sending transactions, and view the decoded data of inputs and event of transactions.

(4) It provides Blockchain overview, block details, transaction and node configuration pages that execute each round of request om every 10 seconds.

## 2. Deploy

### 2.1 Environment dependency

| Environment     | Version              |
| ------ | --------------- |
| nginx   | nginx1.6 or above version    |

The install of nginx is introduced in Additional.


### 2.2 Pull code


Execute command: (when web and server are deployed on the same machine, it only needs to be pulled once)

```shell
git clone https://github.com/FISCO-BCOS/fisco-bcos-browser.git
```

```shell
cd fisco-bcos-browser
```

And then, place the dist directory in ./web/fisco-bcos-browser-front/ under /data/app/web directory.

**Tip:** the directory can be cutomized, as long as the step 2 of nginx config file is kept the same.

### 2.3 Modify nginx config

There is nginx config file under ./web/fisco-bcos-browser-front/doc, which can replace the installed nginx.conf;

**Note**：If nginx is installed as the way in Additional, the config file route is /usr/local/nginx/conf/nginx.conf.

And modify nginx.conf;

1. Modify IP address and port of web.
2. Modify web file route and point to dist directory that pulled code.
3. Modify IP and port of server. Please note that '/api' need not to be changed.

```Nginx
    server {
            listen       8081 default_server;   #Step 1, web nginx monitoring port
            server_name  192.168.0.1;         #Step 2, web address, can be configured as domain name
            location / {
                    root    /data/app/web/dist;   #Step 2, web file route
                    index  index.html index.htm;
                    try_files $uri $uri/ /index.html =404;
                }

            # Load configuration files for the default server block.
            include /etc/nginx/default.d/*.conf;

            location /api {
                    proxy_pass    http://192.168.0.1:8088/;    #Step 3, IP and port of server (fisco-bcos-browser server)
               	 	proxy_set_header		Host				$host;
                    proxy_set_header		X-Real-IP			$remote_addr;
                    proxy_set_header		X-Forwarded-For		$proxy_add_x_forwarded_for;
            }
            }
```

### 2.4 Start nginx

(1) Start nginx。
Start command:

```shell
/usr/local/nginx/sbin/nginx   
```
**Start exception report and troubleshooting:**

1. The log route is correct of not (error.log and access.log)
2. nginx has been added with access

(2) Open page, page url is the web port and IP configured by nginx
Example: the url of the above config file is http:192.168.0.1:8081

(3) Open page. configure group (group IP same as build chain), configure nodes (within the group), the we can access the data.

## 3. Additional
### 3.1 Install nginx（Please check [Network Tutorial](http://www.runoob.com/linux/nginx-install-setup.html)）
#### 3.1.1 Download nginx dependency
Please confirm if gcc, pcre-devel, zlib-devel, openssl-develnginx are installed in the system. If not, execute the command

	yum -y install gcc pcre-devel zlib-devel openssl openssl-devel
Please note permission problems when executing command. If any please add sudo
#### 3.1.2 Download nginx
nginx download address: https://nginx.org/download/(download the latest stable version)
Or use the following command:

	wget http://nginx.org/download/nginx-1.10.2.tar.gz  (version changable)
Move the downloaded packet to /usr/local/
#### 3.1.3 Install nginx
##### 3.1.3.1 Decompress
	tar -zxvf nginx-1.10.2.tar.gz

##### 3.1.3.2 Enter nginx directory

	cd nginx-1.10.2
##### 3.1.3.3 Configure

	./configure --prefix=/usr/local/nginx

##### 3.1.3.4 make

	make
	make install
##### 3.1.3.5 Test the install
Use command:

	/usr/local/nginx/sbin/nginx –t
	
The output message in normal status:

	nginx: the configuration file /usr/local/nginx/conf/nginx.conf syntax is ok
	nginx: configuration file /usr/local/nginx/conf/nginx.conf test is successful
##### 3.1.3.6 Common commands of nginx
```shell
/usr/local/nginx/sbin/nginx -s reload            # reload config file
/usr/local/nginx/sbin/nginx -s reopen            # restart Nginx
/usr/local/nginx/sbin/nginx -s stop              # stop Nginx
ps -ef | grep nginx                              # view progress of nginx
```
