# fisco bcos国密版部署
** 作者：饶应典
## fisco bcos国密版部署总体思路：
1.  ca节点初始化链证书和链私钥
2.  ca节点生成各机构证书 (推荐机构自行创建私钥并向ca节点申请机构证书：机构向ca节点申请证书方式在文末）
3.  将链证书、机构证书发送给对应机构
4.  机构修改节点配置文件
5.  生成节点信息(证书，连接信息)
6.  机构间互换节点连接信息
7.  选出一个机构，将所有的节点证书发送给该机构，让其创建群组的创世区块，并分发给其他组员
8.  生成节点(已拿到链证书、机构证书、节点证书、，其他组员的连接信息、创世区块)
9.  启动节点
10.  配置控制台 

ca初始化 → 各机构自己生成机构私钥并向ca申请机构证书 → 各自生成节点信息 → 节点间互换连接信息 → 选出创世矿工进行创世区块的挖矿(需要所有节点证书) → 生成节点

节点间彼此拥有连接信息，创世矿工需要所有节点的节点证书

---------------------------------------------
 - 单机部署略简单，请直接参考 [fisco官方文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)
----------------------------------------------

## 准备工作：
**下载：**
 - ```cd ~/ && git clone https://github.com/FISCO-BCOS/generator.git```

**安装：**
 - ```cd ~/generator && bash ./scripts/install.sh```

**获取国密二进制文件：**
 - ```./generator --download_fisco ./meta -g```

**初始化机构：(仅限本机，多机部署需自行初始化)**
 - ```cp -r ~/generator ~/generator-A```
 - ```cp -r ~/generator ~/generator-B```

---------------------------------------------

## 部署流程：

**1.  ca节点：初始化链证书和链私钥**
 - ```cd ~/generator```
 - ```./generator --generate_chain_certificate ./dir_chain_ca -g```	---国密证书
 - ```./generator --generate_chain_certificate ./dir_chain_ca_normal```		---普通证书

**查看链证书和链私钥**
 - ```ls ./dir_chain_ca```
 - ```ls ./dir_chain_ca_normal```

---------------------------------------------

**2. ca节点： 生成机构的证书和机构私钥(正常多机部署需要机构自行创建机构私钥并向ca节点申请机构证书)**
 - A机构
 - ```./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyA -g 	```
 - ```./generator --generate_agency_certificate ./dir_agency_ca  ./dir_chain_ca_normal agencyA_normal```

 - B机构
 - ```./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyB -g 	```
 - ```./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca_normal agencyB_normal```

**查看机构证书和私钥**
 - ```ls dir_agency_ca/agencyA/```
 - ```ls dir_agency_ca/agencyA_normal/```
 - ```ls dir_agency_ca/agencyB/```
 - ```ls dir_agency_ca/agencyB_normal/```

---------------------------------------------

**3. ca节点：将链证书，机构证书(机构私钥)发送给机构**
 - A机构
 - ```cp ./dir_agency_ca/agencyA/* ~/generator-A/meta/```
 - ```cp ./dir_agency_ca/agencyA_normal/* ~/generator-A/meta/```

 - B机构
 - ```cp ./dir_agency_ca/agencyB/* ~/generator-B/meta/```
 - ```cp ./dir_agency_ca/agencyB_normal/* ~/generator-B/meta/```

---------------------------------------------

**4. 所有机构：修改机构的节点配置文件(#node_deployment.ini为节点配置文件，运维部署工具会根据node_deployment.ini下的配置生成相关节点证书，及生成节点配置文件夹等)**
 - A机构
 - ```cd ~/generator-A```
 - ```cat > ./conf/node_deployment.ini << EOF```	---详情见尾部node_deployment.ini

 - B机构
 - ```cd ~/generator-B```
 - ```cat > ./conf/node_deployment.ini << EOF```

---------------------------------------------

**5. 所有机构:生成节点信息(节点证书，连接信息)**
 - A机构
 - ```cd ~/generator-A```
 - ```./generator --generate_all_certificates ./agencyA_node_info -g```

 - B机构
 - ```cd ~/generator-B```
 - ```./generator --generate_all_certificates ./agencyB_node_info -g```

 - 查看节点信息
 - ```cd ~/generator-A```
 - ```ls ./agencyA_node_info```

 - ```cd ~/generator-B```
 - ```ls ./agencyB_node_info```

---------------------------------------------

**6. 所有机构：发送节点信息给其他机构(p2p节点连接地址)**
 - A机构
 - ```cd ~/generator-A```
 - ```cp ./agencyA_node_info/peers.txt ~/generator-B/meta/peersA.txt```

 - B机构
 - ```cd ~/generator-B```
 - ```cp ./agencyB_node_info/peers.txt ~/generator-A/meta/peersB.txt```

---------------------------------------------

**7. 单个机构(该机构由各成员商讨选出,例：A机构)：生成创世块(生成创世块的机构需要拿到其他机构的节点证书)**

1. 收集其他机构节点证书(各节点将节点证书发送给要生成创世区块的机构：例：B机构将节点证书发送给A机构)
 - ```cp ./agencyB_node_info/gmcert*.crt ~/generator-A/meta/```

2. 修改group_genesis.ini 文件
 - ```cd ~/generator-A```
 - ```cat > ./conf/group_genesis.ini << EOF```		---详情见尾部group_genesis.ini


3. 生成群组创世区块
 - ```./generator --create_group_genesis ./group -g```

4. 将创世区块分发给其他组员
 - ```cp ./group/group.1.genesis ~/generator-B/meta```

---------------------------------------------

**8. 所有机构：生成节点(peer.txt文件可自定，如果有多名成员，需要将其合并)**
 - A机构
 - ```cd ~/generator-A```
 - ```./generator --build_install_package ./meta/peersB.txt ./nodeA -g```

 - B机构
 - ```cd ~/generator-B```
 - ```./generator --build_install_package ./meta/peersA.txt ./nodeB -g```

---------------------------------------------

**9. 所有机构：启动节点**
 - A机构
 - ```bash ./nodeA/start_all.sh```

 - B机构
 - ```bash ./nodeB/start_all.sh```

**10. 配置控制台**
1. 下载
 - ```./generator --download_console ./ --cdn```
2. 修改配置文件
 - ```vi ./console/conf/applicationContext.xml```

 - 将encryptType改为1：
```
<bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
    <!-- encryptType值设置为1，打开国密开关 -->
    <constructor-arg value="1"/> <!-- 0:standard 1:guomi -->
</bean>
```

3. 替换为国密版jar包
```
cd console && curl -LO https://www.fisco.com.cn/cdn/deps/tools/solcj/solcJ-all-0.4.25-gm.jar && bash replace_solc_jar.sh solcJ-all-0.4.25-gm.jar
```

4. 打开控制台
 - ```cd ~/generator-A/console && bash ./start.sh```
控制台命令请参考官方文档[控制台](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/console.html)

------------------------------------------------------------

**node_deployment.ini：**

```
[group]
group_id=1

[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
channel_ip=0.0.0.0
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
channel_ip=0.0.0.0
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
EOF
```

------------------------------------

**group_genesis.ini：**

```
[group]
group_id=1
	
[nodes]
node0=127.0.0.1:30300
node1=127.0.0.1:30301
node2=127.0.0.1:30302
node3=127.0.0.1:30303
EOF
```

---------------------------------------------

## 机构向ca节点请求机构证书：
1.机构首先在本地使用机构私钥agency.key生成证书请求文件agency.csr；

```
openssl req -new -sha256 -subj "/CN=$name/O=fisco-bcos/OU=agency" -key ./agency.key -config ./cert.cnf -out ./agency.csr
```

2.联盟链委员会根据证书请求文件生成机构证书agency.crt；

```
openssl x509 -req -days 3650 -sha256 -CA ./ca.crt -CAkey ./ca.key -CAcreateserial -in ./agency.csr -out ./agency.crt  -extensions v4_req -extfile ./cert.cnf
```

----------------------------------------------

## 新增组员：
1. 该机构先向ca节点取得链证书和机构证书
2. 向目标群组的某一成员请求群组创世文件
3. 将自己的连接信息发送给其他组员
4. 启动节点
5. 配置/generator/console/conf/applicationContext.xml文件，加上新组信息

--------------------------------

## 使用mysql存储：

mysql配置：
1. 查看配置文件my.cnf
 - ```mysql --help | grep 'Default options' -A 1```
 -  执行之后看到如下数据
 - ```Default options are read from the following files in the given order:/etc/mysql/my.cnf /etc/my.cnf ~/.my.cnf```

2. mysql依次从/etc/mysql/my.cnf，/etc/my.cnf，~/.my.cnf中加载配置。依次查找这几个文件，找到第一个存在的文件，在[mysqld]段中新增如下内容（如果存在则修改值）。
 - ```max_allowed_packet = 1024M```
 - ```sql_mode =STRICT_TRANS_TABLES```

3. 重启mysql
 - ```service mysql restart```


配置好mysql后，修改节点中/conf文件夹下的group.1.ini文件，将[storage]下的type改为mysql，并设置db_ip、db_port、db_username、db_passwd、db_name，重启节点。
db_name需要分区节点和群组，例如：A节点的群组1的db_name=db_Group1_A，群组2的db_name=db_Group2_A。B节点的群组1的db_name=db_Group1_B，B节点的群组2的db_name=db_Group2_B

**如何将已有链转为mysql存储：**
先将其中一个节点改为mysql模式，并重启节点，等到该节点mysql中数据同步完成后，将其他节点也改为mysql模式，并重启即可，这样可以在保留之前的区块信息的前提下转为mysql。

---
## 证书间的关系：
ca→机构→节点

1. ca节点生成链证书
2. 机构向ca节点请求机构证书
3. 节点向机构请求节点证书


---
详情请以官方文档为准 [fisco bcos国密版部署](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/enterprise_tools/tutorial_detail_operation_gm.html)
