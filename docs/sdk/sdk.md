# Web3SDK

[Web3SDK](https://github.com/FISCO-BCOS/web3sdk)可以支持访问节点、查询节点状态、修改系统设置和发送交易等功能。该版本（2.0）的技术文档只适用Web3SDK 2.0及以上版本(与FISCO BCOS 2.0及以上版本适配)，1.2.x版本的技术文档请查看[Web3SDK 1.2.x版本技术文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/docs/web3sdk/config_web3sdk.html)。

2.0版本主要特性包括：

- 提供调用FISCO BCOS JSON-RPC的Java API
- 支持预编译（Precompiled）合约管理区块链
- 支持[链上信使协议](../manual/amop_protocol.md)为联盟链提供安全高效的消息信道
- 支持使用国密算法发送交易

## 环境要求

```eval_rst
.. important::

    - java版本
     要求 `JDK8或以上 <https://openjdk.java.net/>`_。由于CentOS的yum仓库的OpenJDK缺少JCE(Java Cryptography Extension)，导致Web3SDK无法正常连接区块链节点，因此在使用CentOS操作系统时，推荐从OpenJDK网站自行下载。`下载地址 <https://jdk.java.net/java-se-ri/8>`_  `安装指南 <https://openjdk.java.net/install/index.html>`_ 
    - FISCO BCOS区块链环境搭建
     参考 `FISCO BCOS安装教程 <../installation.html>`_
    - 网络连通性
     检查Web3SDK连接的FISCO BCOS节点channel_listen_port是否能telnet通，若telnet不通，需要检查网络连通性和安全策略。

```

## Java应用引入SDK

   通过gradle或maven引入SDK到java应用

   gradle:
```bash
compile ('org.fisco-bcos:web3sdk:2.0.4')
```
   maven:
``` xml
<dependency>
    <groupId>org.fisco-bcos</groupId>
    <artifactId>web3sdk</artifactId>
    <version>2.0.4</version>
</dependency>
```
由于引入了以太坊的solidity编译器相关jar包，需要在Java应用的gradle配置文件build.gradle中添加以太坊的远程仓库。

```bash
repositories {
        mavenCentral()
        maven { url "https://dl.bintray.com/ethereum/maven/" }
    }
```
**注：** 如果下载Web3SDK的依赖`solcJ-all-0.4.25.jar`速度过慢，可以[参考这里](../manual/console.html#jar)进行下载。

## 配置SDK

### FISCO BCOS节点证书配置
FISCO BCOS作为联盟链，其SDK连接区块链节点需要通过证书(ca.crt、node.crt)和私钥(node.key)进行双向认证。因此需要将节点所在目录`nodes/${ip}/sdk`下的`ca.crt`、`node.crt`和`node.key`文件拷贝到项目的资源目录，供SDK与节点建立连接时使用。

### 配置文件设置
Java应用的配置文件需要做相关配置。值得关注的是，FISCO BCOS 2.0版本支持[多群组功能](../design/architecture/group.md)，SDK需要配置群组的节点信息。将以Spring项目和Spring Boot项目为例，提供配置指引。

### Spring项目配置
提供Spring项目中关于`applicationContext.xml`的配置下所示。
```xml
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
           xmlns:tx="http://www.springframework.org/schema/tx" xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
         http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
         http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">


        <bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
                <constructor-arg value="0"/> <!-- 0:standard 1:guomi -->
        </bean>

        <bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
                <property name="allChannelConnections">
                        <list>  <!-- 每个群组需要配置一个bean，每个群组可以配置多个节点 -->
                                <bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                                        <property name="groupId" value="1" /> <!-- 群组的groupID -->
                                        <property name="connectionsStr">
                                                <list>
                                                        <value>127.0.0.1:20200</value>  <!-- IP:channel_port -->
                                                        <value>127.0.0.1:20201</value>
                                                </list>
                                        </property>
                                </bean>
                                <bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                                        <property name="groupId" value="2" /> <!-- 群组的groupID -->
                                        <property name="connectionsStr">
                                                <list>
                                                        <value>127.0.0.1:20202</value> 
                                                        <value>127.0.0.1:20203</value> 
                                                </list>
                                        </property>
                                </bean>
                        </list>
                </property>
        </bean>

        <bean id="channelService" class="org.fisco.bcos.channel.client.Service" depends-on="groupChannelConnectionsConfig">
                <property name="groupId" value="1" /> <!-- 配置连接群组1 -->
                <property name="agencyName" value="fisco" /> <!-- 配置机构名 -->
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>

</beans>
```
`applicationContext.xml`配置项详细说明:
- encryptType: 国密算法开关(默认为0)                              
  - 0: 不使用国密算法发交易                              
  - 1: 使用国密算法发交易(开启国密功能，需要连接的区块链节点是国密节点，搭建国密版FISCO BCOS区块链[参考这里](../manual/guomi_crypto.md))
- groupChannelConnectionsConfig: 
  - 配置待连接的群组，可以配置一个或多个群组，每个群组需要配置群组ID 
  - 每个群组可以配置一个或多个节点，设置群组节点的配置文件**config.ini**中`[rpc]`部分的`listen_ip`和`channel_listen_port`。
- channelService: 通过指定群组ID配置SDK实际连接的群组，指定的群组ID是groupChannelConnectionsConfig配置中的群组ID。SDK会与群组中配置的节点均建立连接，然后随机选择一个节点发送请求。

### Spring Boot项目配置
提供Spring Boot项目中关于`application.yml`的配置如下所示。
```yml
encrypt-type: # 0：普通， 1：国密
 encrypt-type: 0 
 
group-channel-connections-config:
  all-channel-connections:
  - group-id: 1  # 群组ID
    connections-str:
                    - 127.0.0.1:20200  # 节点，listen_ip:channel_listen_port
                    - 127.0.0.1:20201
  - group-id: 2  
    connections-str:
                    - 127.0.0.1:20202  # 节点，listen_ip:channel_listen_port
                    - 127.0.0.1:20203
 
channel-service:
  group-id: 1 # sdk实际连接的群组
  agency-name: fisco # 机构名称
```
`application.yml`配置项与`applicationContext.xml`配置项相对应，详细介绍参考`applicationContext.xml`配置说明。

## 使用SDK 

### Spring项目开发指引
#### 调用SDK的API(参考[Web3SDK API列表](#web3sdk-api)设置或查询相关的区块链数据。
##### 调用SDK Web3j的API
加载配置文件，SDK与区块链节点建立连接，获取web3j对象，根据Web3j对象调用相关API。示例代码如下：
```java
    //读取配置文件，SDK与区块链节点建立连接
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    Service service = context.getBean(Service.class);
    service.run(); 
    ChannelEthereumService channelEthereumService = new ChannelEthereumService();
    channelEthereumService.setChannelService(service);

    //获取Web3j对象
    Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
    //通过Web3j对象调用API接口getBlockNumber
    BigInteger blockNumber = web3j.getBlockNumber().send().getBlockNumber();
    System.out.println(blockNumber);
```
**注：** SDK处理交易超时时间默认为60秒，即60秒内没有收到交易响应，判断为超时。该值可以通过`ChannelEthereumService`进行设置，示例如下：
```java
// 设置交易超时时间为100000毫秒，即100秒
channelEthereumService.setTimeout(100000);
```
##### 调用SDK Precompiled的API
加载配置文件，SDK与区块链节点建立连接。获取SDK Precompiled Service对象，调用相关的API。示例代码如下：
```java
    //读取配置文件，SDK与区块链节点建立连接，获取Web3j对象
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    Service service = context.getBean(Service.class);
    service.run(); 
    ChannelEthereumService channelEthereumService = new ChannelEthereumService();
    channelEthereumService.setChannelService(service);
    Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
    String privateKey = "b83261efa42895c38c6c2364ca878f43e77f3cddbc922bf57d0d48070f79feb6"; 
    //指定外部账户私钥，用于交易签名
    Credentials credentials = GenCredential.create(privateKey); 
    //获取SystemConfigService对象
    SystemConfigService systemConfigService = new SystemConfigService(web3j, credentials);
    //通过SystemConfigService对象调用API接口setValueByKey
    String result = systemConfigService.setValueByKey("tx_count_limit", "2000");
    //通过Web3j对象调用API接口getSystemConfigByKey
    String value = web3j.getSystemConfigByKey("tx_count_limit").send().getSystemConfigByKey();
    System.out.println(value);
```
##### 创建并使用指定外部账户
sdk发送交易需要一个外部账户，下面是随机创建一个外部账户的方法。
```java
//创建普通外部账户
EncryptType.encryptType = 0;
//创建国密外部账户，向国密区块链节点发送交易需要使用国密外部账户
// EncryptType.encryptType = 1; 
Credentials credentials = GenCredential.create();
//账户地址
String address = credentials.getAddress();
//账户私钥 
String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
//账户公钥 
String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
```
使用指定的外部账户
```java
//通过指定外部账户私钥使用指定的外部账户
Credentials credentials = GenCredential.create(privateKey);
```

##### 加载账户私钥文件
如果通过账户生成脚本`get_accounts.sh`生成了PEM或PKCS12格式的账户私钥文件(账户生成脚本的用法参考[账户管理文档](../tutorial/account.md))，则可以通过加载PEM或PKCS12账户私钥文件使用账户。加载私钥有两个类：P12Manager和PEMManager，其中，P12Manager用于加载PKCS12格式的私钥文件，PEMManager用于加载PEM格式的私钥文件。

* P12Manager用法举例：
在applicationContext.xml中配置PKCS12账户的私钥文件路径和密码
```xml
<bean id="p12" class="org.fisco.bcos.channel.client.P12Manager" init-method="load" >
	<property name="password" value="123456" />
	<property name="p12File" value="classpath:0x0fc3c4bb89bd90299db4c62be0174c4966286c00.p12" />
</bean>
```
开发代码
```java
//加载Bean
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
P12Manager p12 = context.getBean(P12Manager.class);
//提供密码获取ECKeyPair，密码在生产p12账户文件时指定
ECKeyPair p12KeyPair = p12.getECKeyPair(p12.getPassword());
			
//以十六进制串输出私钥和公钥
System.out.println("p12 privateKey: " + p12KeyPair.getPrivateKey().toString(16));
System.out.println("p12 publicKey: " + p12KeyPair.getPublicKey().toString(16));

//生成web3sdk使用的Credentials
Credentials credentials = GenCredential.create(p12KeyPair.getPrivateKey().toString(16));
System.out.println("p12 Address: " + credentials.getAddress());
```

* PEMManager使用举例

在applicationContext.xml中配置PEM账户的私钥文件路径
```xml
<bean id="pem" class="org.fisco.bcos.channel.client.PEMManager" init-method="load" >
	<property name="pemFile" value="classpath:0x0fc3c4bb89bd90299db4c62be0174c4966286c00.pem" />
</bean>
```
使用代码加载
```java
//加载Bean
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-keystore-sample.xml");
PEMManager pem = context.getBean(PEMManager.class);
ECKeyPair pemKeyPair = pem.getECKeyPair();

//以十六进制串输出私钥和公钥
System.out.println("PEM privateKey: " + pemKeyPair.getPrivateKey().toString(16));
System.out.println("PEM publicKey: " + pemKeyPair.getPublicKey().toString(16));

//生成web3sdk使用的Credentials
Credentials credentialsPEM = GenCredential.create(pemKeyPair.getPrivateKey().toString(16));
System.out.println("PEM Address: " + credentialsPEM.getAddress());
```

#### 通过SDK部署并调用合约
##### 准备Java合约文件
控制台提供一个专门的编译合约工具，方便开发者将Solidity合约文件编译为Java合约文件，具体使用方式[参考这里](../manual/console.html#id10)。

##### 部署并调用合约
SDK的核心功能是部署/加载合约，然后调用合约相关接口，实现相关业务功能。部署合约调用Java合约类的deploy方法，获取合约对象。通过合约对象可以调用getContractAddress方法获取部署合约的地址以及调用该合约的其他方法实现业务功能。如果合约已部署，则通过部署的合约地址可以调用load方法加载合约对象，然后调用该合约的相关方法。
```java
    //读取配置文件，sdk与区块链节点建立连接，获取web3j对象
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    Service service = context.getBean(Service.class);
    service.run(); 
    ChannelEthereumService channelEthereumService = new ChannelEthereumService();
    channelEthereumService.setChannelService(service);
    channelEthereumService.setTimeout(10000);
    Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
    //准备部署和调用合约的参数
    BigInteger gasPrice = new BigInteger("300000000");
    BigInteger gasLimit = new BigInteger("300000000");
    String privateKey = "b83261efa42895c38c6c2364ca878f43e77f3cddbc922bf57d0d48070f79feb6"; 
    //指定外部账户私钥，用于交易签名
    Credentials credentials = GenCredential.create(privateKey); 
    //部署合约 
    YourSmartContract contract = YourSmartContract.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();
    //根据合约地址加载合约
    //YourSmartContract contract = YourSmartContract.load(address, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)); 
    //调用合约方法发送交易
    TransactionReceipt transactionReceipt = contract.someMethod(<param1>, ...).send(); 
    //查询合约方法查询该合约的数据状态
    Type result = contract.someMethod(<param1>, ...).send(); 
```
### Spring Boot项目开发指引
提供[spring-boot-starter](https://github.com/FISCO-BCOS/spring-boot-starter)示例项目供参考。Spring Boot项目开发与Spring项目开发类似，其主要区别在于配置文件方式的差异。该示例项目提供相关的测试案例，具体描述参考示例项目的README文档。

### SDK国密功能使用
- 前置条件：FISCO BCOS区块链采用国密算法，搭建国密版的FISCO BCOS区块链请参考[国密使用手册](../manual/guomi_crypto.md)。
- 启用国密功能：application.xml/application.yml配置文件中将encryptType属性设置为1。
- 加载私钥使用GenCredential类(适用于国密和非国密)，Credential类只适用于加载非国密私钥。

国密版SDK调用API的方式与普通版SDK调用API的方式相同，其差异在于国密版SDK需要生成国密版的Java合约文件。编译国密版的Java合约文件[参考这里](../manual/console.html#id10)。

## Web3SDK API

Web3SDK API主要分为Web3j API和Precompiled Service API。其中Web3j API可以查询区块链相关的状态，发送和查询交易信息；Precompiled Service API可以管理区块链相关配置以及实现特定功能。

### Web3j API
Web3j API是由web3j对象调用的FISCO BCOS的RPC API，其API名称与RPC API相同，参考[RPC API文档](../api.md)。

### Precompiled Service API
预编译合约是FISCO BCOS底层通过C++实现的一种高效智能合约。SDK已提供预编译合约对应的Java接口，控制台通过调用这些Java接口实现了相关的操作命令，体验控制台，参考[控制台手册](../manual/console.md)。SDK提供Precompiled对应的Service类，分别是分布式控制权限相关的PermissionService，[CNS](../design/features/cns_contract_name_service.md)相关的CnsService，系统属性配置相关的SystemConfigService和节点类型配置相关ConsensusService。相关错误码请参考：[Precompiled Service API 错误码](../api.html#precompiled-service-api)

#### PermissionService
SDK提供对[分布式控制权限](../manual/permission_control.md)的支持，PermissionService可以配置权限信息，其API如下：
- **public String grantUserTableManager(String tableName, String address)：** 根据用户表名和外部账户地址设置权限信息。
- **public String revokeUserTableManager(String tableName, String address)：** 根据用户表名和外部账户地址去除权限信息。
- **public List\<PermissionInfo\> listUserTableManager(String tableName)：** 根据用户表名查询设置的权限记录列表(每条记录包含外部账户地址和生效块高)。
- **public String grantDeployAndCreateManager(String address)：** 增加外部账户地址的部署合约和创建用户表权限。
- **public String revokeDeployAndCreateManager(String address)：** 移除外部账户地址的部署合约和创建用户表权限。
- **public List\<PermissionInfo\> listDeployAndCreateManager()：** 查询拥有部署合约和创建用户表权限的权限记录列表。
- **public String grantPermissionManager(String address)：** 增加外部账户地址的管理权限的权限。
- **public String revokePermissionManager(String address)：** 移除外部账户地址的管理权限的权限。
- **public List\<PermissionInfo\> listPermissionManager()：** 查询拥有管理权限的权限记录列表。
- **public String grantNodeManager(String address)：** 增加外部账户地址的节点管理权限。
- **public String revokeNodeManager(String address)：** 移除外部账户地址的节点管理权限。
- **public List\<PermissionInfo\> listNodeManager()：** 查询拥有节点管理的权限记录列表。
- **public String grantCNSManager(String address)：** 增加外部账户地址的使用CNS权限。
- **public String revokeCNSManager(String address)：** 移除外部账户地址的使用CNS权限。
- **public List\<PermissionInfo\> listCNSManager()：** 查询拥有使用CNS的权限记录列表。
- **public String grantSysConfigManager(String address)：** 增加外部账户地址的系统参数管理权限。
- **public String revokeSysConfigManager(String address)：** 移除外部账户地址的系统参数管理权限。
- **public List\<PermissionInfo\> listSysConfigManager()：** 查询拥有系统参数管理的权限记录列表。

#### CnsService
SDK提供对[CNS](../design/features/cns_contract_name_service.md)的支持。CnsService可以配置CNS信息，其API如下：
- **String registerCns(String name, String version, String address, String abi)：** 根据合约名、合约版本号、合约地址和合约abi注册CNS信息。
- **String getAddressByContractNameAndVersion(String contractNameAndVersion)：** 根据合约名和合约版本号(合约名和合约版本号用英文冒号连接)查询合约地址。若缺失合约版本号，默认使用合约最新版本。
- **List\<CnsInfo\> queryCnsByName(String name)：** 根据合约名查询CNS信息。
- **List\<CnsInfo\> queryCnsByNameAndVersion(String name, String version)：** 根据合约名和合约版本号查询CNS信息。

#### SystemConfigService
SDK提供对系统配置的支持。SystemConfigService可以配置系统属性值（目前支持tx_count_limit和tx_gas_limit属性的设置），其API如下：
- **String setValueByKey(String key, String value)：** 根据键设置对应的值（查询键对应的值，参考Web3j API中的getSystemConfigByKey接口）。

#### ConsensusService 
SDK提供对[节点类型](../design/security_control/node_management.html#id6)配置的支持。ConsensusService可以设置节点类型，其API如下：
- **String addSealer(String nodeId)：** 根据节点NodeID设置对应节点为共识节点。
- **String addObserver(String nodeId)：** 根据节点NodeID设置对应节点为观察节点。
- **String removeNode(String nodeId)：** 根据节点NodeID设置对应节点为游离节点。

#### CRUDService 
SDK提供对CRUD(增删改查)操作的支持。CRUDService可以创建表，对表进行增删改查操作，其API如下：
- **int createTable(Table table)：** 创建表，提供表对象。表对象需要设置其表名，主键字段名和其他字段名。其中，其他字段名是以英文逗号分隔拼接的字符串。返回创建表的状态值，返回为0则代表创建成功。
- **int insert(Table table, Entry entry)：** 插入记录，提供表对象和Entry对象。表对象需要设置表名和主键字段名；Entry是map对象，提供插入的字段名和字段值，注意必须设置主键字段。返回插入的记录数。
- **int update(Table table, Entry entry, Condition condition)：** 更新记录，提供表对象，Entry对象和Condtion对象。表对象需要设置表名和主键字段名；Entry是map对象，提供更新的字段名和字段值；Condition对象是条件对象，可以设置更新的匹配条件。返回更新的记录数。
- **List\<Map\<String, String\>\> select(Table table, Condition condition)：** 查询记录，提供表对象和Condtion对象。表对象需要设置表名和主键字段名；Condition对象是条件对象，可以设置查询的匹配条件。返回查询的记录。
- **int remove(Table table, Condition condition)：** 移除记录，提供表对象和Condtion对象。表对象需要设置表名和主键字段名；Condition对象是条件对象，可以设置移除的匹配条件。返回移除的记录数。
- **Table desc(String tableName)：** 根据表名查询表的信息，主要包含表的主键和其他属性字段。返回表类型，主要包含表的主键字段名和其他属性字段名。

## 交易解析
FISCO BCOS的交易是一段发往区块链系统的请求数据，用于部署合约，调用合约接口，维护合约的生命周期以及管理资产，进行价值交换等。当交易确认后会产生交易回执，[交易回执](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionreceipt)和[交易](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionbyhash)均保存在区块里，用于记录交易执行过程生成的信息，如结果码、日志、消耗的gas量等。用户可以使用交易哈希查询交易回执，判定交易是否完成。  

交易回执包含三个关键字段，分别是input（目前dev分支编译的fisco bcos包含该字段，后续合入2.0.0版本）, output , logs:

| 字段   | 类型      | 描述                               |
| :----- | :-------- | :--------------------------------- |
| input  | String    | 交易输入的ABI编码十六进制字符串    |
| output | String    | 交易返回的ABI编码十六进制字符串    |
| logs   | List<Log> | event log列表，保存交易的event信息 |

交易解析功能帮助用户解析这三个字段为json数据和java对象。

### 引入jar包
解析工具类在web3sdk中，应用需要首先在build.gradle配置文件中增加如下配置，引入web3sdk jar包。
```xml
repositories {
    maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
    maven { url "https://dl.bintray.com/ethereum/maven/" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    mavenCentral()
}
compile group: "org.fisco-bcos", name: "web3sdk", version: "2.0.34-SNAPSHOT"
```

### 接口说明
代码包路径``` org.fisco.bcos.web3j.tx.txdecode ```，使用`TransactionDecoderFactory`工厂类建立交易解析对象`TransactionDecoder`，有两种方式：

1. ``` TransactionDecoder buildTransactionDecoder(String abi, String bin); ```  
   
   abi：合约的ABI   
   
   bin：合约bin，暂无使用，可以直接传入空字符串""  

2. ``` TransactionDecoder buildTransactionDecoder(String contractName); ```  
   
   contractName：合约名称，在应用的根目录下创建`solidity`目录，将交易相关的合约放在`solidity`目录，通过指定合约名获取交易解析对象

交易解析对象`TransactionDecoder`接口列表：
1. `String decodeInputReturnJson(String input)`  
   
   解析input，将结果封装为json字符串，json格式
   ```json
   {"data":[{"name":"","type":"","data":} ... ],"function":"","methodID":""}
   ```
   function : 函数签名字符串  

   methodID : [函数选择器](https://solidity.readthedocs.io/en/develop/abi-spec.html#function-selector)

2. `InputAndOutputResult decodeInputReturnObject(String input)`  
   
   解析input，返回Object对象，InputAndOutputResult结构:
   ```java
   public class InputAndOutputResult {
      private String function; // 函数签名
      private String methodID; // methodID
      private List<ResultEntity> result; // 返回列表
    }

   public class ResultEntity {
      private String name;  // 字段名称, 解析output返回时，值为空字符串
      private String type;  // 字段类型
      private Object data;  // 字段值
    }
   ```

3. `String decodeOutputReturnJson(String input, String output)`  
   
   解析output，将结果封装为json字符串，格式同```decodeInputReturnJson```

4. `InputAndOutputResult decodeOutputReturnObject(String input, String output)`  
   
   解析output，返回java Object对象

5. `String decodeEventReturnJson(List<Log> logList)`  
   
   解析event列表，将结果封装为json字符串，json格式
   ```json
   {"event1签名":[[{"name":"","type":"","data":}...]...],"event2签名":[[{"name":"","type":"","data":}...]...]...}
   ```

6. `Map<String, List<List<ResultEntity>>> decodeEventReturnObject(List<Log> logList)`  
   
   解析event列表，返回java Map对象，key为[event签名](https://solidity.readthedocs.io/en/develop/abi-spec.html#events)字符串，`List<ResultEntity>`为交易中单个event参数列表，`List<List<ResultEntity>>`表示单个交易可以包含多个event

`TransactionDecoder`对input，output和event logs均分别提供返回json字符串和java对象的方法。json字符串方便客户端处理数据，java对象方便服务端处理数据。

### 示例
以`TxDecodeSample`合约为例说明接口的使用：
```solidity
pragma solidity ^0.4.24;
contract TxDecodeSample
{
    event Event1(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs);
    event Event2(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes  _bs);
    
    function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) public constant returns (uint256,int256,bool,address,bytes32,string,bytes)
    {
      Event1(_u, _i, _b, _addr, _bs32, _s, _bs);
      return (_u, _i, _b, _addr, _bs32, _s, _bs);
    }
    
    function do_event(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) public 
    {
      Event1(_u, _i, _b, _addr, _bs32, _s, _bs);
      Event2(_u, _i, _b, _addr, _bs32, _s, _bs);
    }
}
```

使用`buildTransactionDecoder` 创建`TxDecodeSample`合约的解析对象：
```java
// TxDecodeSample合约ABI
String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"_u\",\"type\":\"uint256\"},{\"name\":\"_i\",\"type\":\"int256\"},{\"name\":\"_b\",\"type\":\"bool\"},{\"name\":\"_addr\",\"type\":\"address\"},{\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"name\":\"_s\",\"type\":\"string\"},{\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"do_event\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"Event1\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"Event2\",\"type\":\"event\"},{\"constant\":true,\"inputs\":[{\"name\":\"_u\",\"type\":\"uint256\"},{\"name\":\"_i\",\"type\":\"int256\"},{\"name\":\"_b\",\"type\":\"bool\"},{\"name\":\"_addr\",\"type\":\"address\"},{\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"name\":\"_s\",\"type\":\"string\"},{\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"echo\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"bool\"},{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]";
String bin = "";
TransactionDecoder txDecodeSampleDecoder = TransactionDecoderFactory.buildTransactionDecoder(abi, bin);
```

#### 解析input
调用``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```
```java
// function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) 
String input = "0x406d373b000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000";
String jsonResult = txDecodeSampleDecoder.decodeInputReturnJson(input);
InputAndOutputResult objectResult = txDecodeSampleDecoder.decodeInputReturnObject(input);
System.out.println("json => \n" + jsonResult);
System.out.println("object => \n" + objectResult);
```

输出：
```java
json => 
{
  "function": "echo(uint256,int256,bool,address,bytes32,string,bytes)",
  "methodID": "0x406d373b",
  "result": [
    {
      "name": "_u",
      "type": "uint256",
      "data": 111111
    },
    {
      "name": "_i",
      "type": "int256",
      "data": -1111111
    },
    {
      "name": "_b",
      "type": "bool",
      "data": false
    },
    {
      "name": "_addr",
      "type": "address",
      "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a"
    },
    {
      "name": "_bs32",
      "type": "bytes32",
      "data": "abcdefghiabcdefghiabcdefghiabhji"
    },
    {
      "name": "_s",
      "type": "string",
      "data": "章鱼小丸子ljjkl;adjsfkljlkjl"
    },
    {
      "name": "_bs",
      "type": "bytes",
      "data": "sadfljkjkljkl"
    }
  ]
}

object => 
InputAndOutputResult[
  function=echo(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes),
  methodID=0x406d373b,
  result=[
    ResultEntity[
      name=_u,
      type=uint256,
      data=111111
    ],
    ResultEntity[
      name=_i,
      type=int256,
      data=-1111111
    ],
    ResultEntity[
      name=_b,
      type=bool,
      data=false
    ],
    ResultEntity[
      name=_addr,
      type=address,
      data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
    ],
    ResultEntity[
      name=_bs32,
      type=bytes32,
      data=abcdefghiabcdefghiabcdefghiabhji
    ],
    ResultEntity[
      name=_s,
      type=string,
      data=章鱼小丸子ljjkl;adjsfkljlkjl
    ],
    ResultEntity[
      name=_bs,
      type=bytes,
      data=sadfljkjkljkl
    ]
  ]
]
```
#### 解析output
调用``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```，echo接口直接将输入返回，因此返回与输入相同

```java
//  function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  public constant returns (uint256,int256,bool,address,bytes32,string,bytes)
String input = “0x406d373b000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000”;

String output = "“0x000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000";

String jsonResult = txDecodeSampleDecoder.decodeOutputReturnJson(input, output);
InputAndOutputResult objectResult = txDecodeSampleDecoder.decodeOutputReturnObject(input, output);
System.out.println("json => \n" + jsonResult);
System.out.println("object => \n" + objectResult);
```

结果：
```java
json => 
{
  "function": "echo(uint256,int256,bool,address,bytes32,string,bytes)",
  "methodID": "0x406d373b",
  "result": [
    {
      "name": "",
      "type": "uint256",
      "data": 111111
    },
    {
      "name": "",
      "type": "int256",
      "data": -1111111
    },
    {
      "name": "",
      "type": "bool",
      "data": false
    },
    {
      "name": "",
      "type": "address",
      "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a"
    },
    {
      "name": "",
      "type": "bytes32",
      "data": "abcdefghiabcdefghiabcdefghiabhji"
    },
    {
      "name": "",
      "type": "string",
      "data": "章鱼小丸子ljjkl;adjsfkljlkjl"
    },
    {
      "name": "",
      "type": "bytes",
      "data": "sadfljkjkljkl"
    }
  ]
}

object => 
InputAndOutputResult[
  function=echo(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes),
  methodID=0x406d373b,
  result=[
    ResultEntity[
      name=,
      type=uint256,
      data=111111
    ],
    ResultEntity[
      name=,
      type=int256,
      data=-1111111
    ],
    ResultEntity[
      name=,
      type=bool,
      data=false
    ],
    ResultEntity[
      name=,
      type=address,
      data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
    ],
    ResultEntity[
      name=,
      type=bytes32,
      data=abcdefghiabcdefghiabcdefghiabhji
    ],
    ResultEntity[
      name=,
      type=string,
      data=章鱼小丸子ljjkl;adjsfkljlkjl
    ],
    ResultEntity[
      name=,
      type=bytes,
      data=sadfljkjkljkl
    ]
  ]
]
```

#### 解析event logs

调用``` function do_event(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```，解析交易中的logs

```java
// transactionReceipt为调用do_event接口的交易回执
String jsonResult = txDecodeSampleDecoder.decodeEventReturnJson(transactionReceipt.getLogs());
String mapResult = txDecodeSampleDecoder.decodeEventReturnJson(transactionReceipt.getLogs());

System.out.println("json => \n" + jsonResult);
System.out.println("map => \n" + mapResult);
```

结果：
```java
json => 
{
  "Event1(uint256,int256,bool,address,bytes32,string,bytes)": [
    [
      {
        "name": "_u",
        "type": "uint256",
        "data": 111111,
        "indexed": false
      },
      {
        "name": "_i",
        "type": "int256",
        "data": -1111111,
        "indexed": false
      },
      {
        "name": "_b",
        "type": "bool",
        "data": false,
        "indexed": false
      },
      {
        "name": "_addr",
        "type": "address",
        "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a",
        "indexed": false
      },
      {
        "name": "_bs32",
        "type": "bytes32",
        "data": "abcdefghiabcdefghiabcdefghiabhji",
        "indexed": false
      },
      {
        "name": "_s",
        "type": "string",
        "data": "章鱼小丸子ljjkl;adjsfkljlkjl",
        "indexed": false
      },
      {
        "name": "_bs",
        "type": "bytes",
        "data": "sadfljkjkljkl",
        "indexed": false
      }
    ]
  ],
  "Event2(uint256,int256,bool,address,bytes32,string,bytes)": [
    [
      {
        "name": "_u",
        "type": "uint256",
        "data": 111111,
        "indexed": false
      },
      {
        "name": "_i",
        "type": "int256",
        "data": -1111111,
        "indexed": false
      },
      {
        "name": "_b",
        "type": "bool",
        "data": false,
        "indexed": false
      },
      {
        "name": "_addr",
        "type": "address",
        "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a",
        "indexed": false
      },
      {
        "name": "_bs32",
        "type": "bytes32",
        "data": "abcdefghiabcdefghiabcdefghiabhji",
        "indexed": false
      },
      {
        "name": "_s",
        "type": "string",
        "data": "章鱼小丸子ljjkl;adjsfkljlkjl",
        "indexed": false
      },
      {
        "name": "_bs",
        "type": "bytes",
        "data": "sadfljkjkljkl",
        "indexed": false
      }
    ]
  ]
}

map => 
{
  Event1(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes)=[
    [
      ResultEntity[
        name=_u,
        type=uint256,
        data=111111
      ],
      ResultEntity[
        name=_i,
        type=int256,
        data=-1111111
      ],
      ResultEntity[
        name=_b,
        type=bool,
        data=false
      ],
      ResultEntity[
        name=_addr,
        type=address,
        data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
      ],
      ResultEntity[
        name=_bs32,
        type=bytes32,
        data=abcdefghiabcdefghiabcdefghiabhji
      ],
      ResultEntity[
        name=_s,
        type=string,
        data=章鱼小丸子ljjkl;adjsfkljlkjl
      ],
      ResultEntity[
        name=_bs,
        type=bytes,
        data=sadfljkjkljkl
      ]
    ]
  ],
  Event2(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes)=[
    [
      ResultEntity[
        name=_u,
        type=uint256,
        data=111111
      ],
      ResultEntity[
        name=_i,
        type=int256,
        data=-1111111
      ],
      ResultEntity[
        name=_b,
        type=bool,
        data=false
      ],
      ResultEntity[
        name=_addr,
        type=address,
        data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
      ],
      ResultEntity[
        name=_bs32,
        type=bytes32,
        data=abcdefghiabcdefghiabcdefghiabhji
      ],
      ResultEntity[
        name=_s,
        type=string,
        data=章鱼小丸子ljjkl;adjsfkljlkjl
      ],
      ResultEntity[
        name=_bs,
        type=bytes,
        data=sadfljkjkljkl
      ]
    ]
  ]
}
```
