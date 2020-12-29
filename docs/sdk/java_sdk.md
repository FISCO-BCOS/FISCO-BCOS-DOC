#  Web3SDK

标签：``Web3SDK``

----

[Web3SDK](https://github.com/FISCO-BCOS/web3sdk)可以支持访问节点、查询节点状态、修改系统设置和发送交易等功能。该版本（2.0）的技术文档只适用Web3SDK 2.0及以上版本(与FISCO BCOS 2.0及以上版本适配)，1.2.x版本的技术文档请查看[Web3SDK 1.2.x版本技术文档](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-1.3/docs/web3sdk/config_web3sdk.html)。

2.0+版本主要特性包括：

- 提供调用FISCO BCOS 2.0 JSON-RPC的Java API
- 支持预编译（Precompiled）合约管理区块链
- 支持[链上信使协议](../manual/amop_protocol.md)为联盟链提供安全高效的消息信道
- 支持使用国密算法发送交易
- 支持通过国密SSL与节点通信

## 环境要求

```eval_rst
.. important::

    - Java版本
     JDK1.8 或者以上版本，推荐使用OracleJDK。  
     
     **注意**：CentOS的yum仓库的OpenJDK缺少JCE(Java Cryptography Extension)，会导致JavaSDK无法正常连接区块链节点。
    - Java安装
     参考 `Java环境配置 <../console/console.html#java>`_
    - FISCO BCOS区块链环境搭建
     参考 `FISCO BCOS安装教程 <../installation.html>`_
    - 网络连通性
     检查Web3SDK连接的FISCO BCOS节点`channel_listen_port`是否能telnet通，若telnet不通，需要检查网络连通性和安全策略。

```

## Java应用引入SDK

   通过gradle或maven引入SDK到java应用

   gradle:
```bash
compile ('org.fisco-bcos:web3sdk:2.6.1')
```
   maven:
``` xml
<dependency>
    <groupId>org.fisco-bcos</groupId>
    <artifactId>web3sdk</artifactId>
    <version>2.6.1</version>
</dependency>
```
由于引入了以太坊的solidity编译器相关jar包，需要在Java应用的gradle配置文件build.gradle中添加以太坊的远程仓库。

```bash
repositories {
        mavenCentral()
        maven { url "https://dl.bintray.com/ethereum/maven/" }
    }
```
**注：** 如果下载Web3SDK的依赖`solcJ-all-0.4.25.jar`速度过慢，可以[参考这里](../console/console.html#jar)进行下载。

## 配置SDK

### 证书配置
FISCO BCOS作为联盟链，SDK连接区块链节点时通过SSL进行双向认证。JavaSDK支持SSL与国密SSL两种认证方式。

#### SSL连接配置
国密区块链和非国密区块链环境下，节点与SDK之间均可以建立SSL的连接，将节点所在目录`nodes/${ip}/sdk/`目录下的`ca.crt`、`sdk.crt`和`sdk.key`文件拷贝到项目的资源目录。（低于2.1版本的FISCO BCOS节点目录下只有`node.crt`和`node.key`，需将其重命名为`sdk.crt`和`sdk.key`以兼容最新的SDK）

#### 国密SSL连接配置
FISCO-BCOS 2.5及之后的版本，在国密区块链环境下支持节点与SDK建立国密SSL连接，将节点所在目录`nodes/${ip}/sdk/gm/`目录下的`gmca.crt`、`gmensdk.crt`、`gmensdk.key`、`gmsdk.crt`、`gmsdk.key`文件拷贝到项目的资源目录。

```eval_rst
.. important::

    - 国密SSL连接只有在国密区块链环境下才可以使用。

    - 是否选择国密SSL连接，SDK与区块链节点的配置要保持一致，节点配置参考 `配置链属性 <../manual/configuration.html#id10>`_

    - FISCO-BCOS 2.5及之后的版本，添加了SDK只能连本机构节点的限制，操作时需确认拷贝证书的路径，否则建联报错。

```

### 配置文件设置

Java应用的配置文件需要做相关配置。值得关注的是，FISCO BCOS 2.0+版本支持[多群组功能](../design/architecture/group.md)，SDK需要配置群组的节点信息。将以Spring项目和Spring Boot项目为例，提供配置指引。

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
                <!-- SSL certificate configuration -->
                <property name="caCert" value="ca.crt" />
                <property name="sslCert" value="sdk.crt" />
                <property name="sslKey" value="sdk.key" />
                <!-- GM SSL certificate configuration -->
                <property name="gmCaCert" value="gmca.crt" />
                <property name="gmEnSslCert" value="gmensdk.crt" />
                <property name="gmEnSslKey" value="gmensdk.key" />
                <property name="gmSslCert" value="gmsdk.crt" />
                <property name="gmSslKey" value="gmsdk.key" />
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
- encryptType: 国密开关(默认为0，关闭)                              
  - 0: 不使用国密                              
  - 1: 使用国密
    - 开启国密功能，需要连接的区块链节点是国密节点，搭建国密版FISCO BCOS区块链[参考这里](../manual/guomi_crypto.md))
    - 使用国密SSL，在国密区块链环境基础上，SDK需要打开encryptType开关，然后配置国密SSL证书
- groupChannelConnectionsConfig: 
  - 配置待连接的群组，可以配置一个或多个群组，每个群组需要配置群组ID 
  - 每个群组可以配置一个或多个节点，设置群组节点的配置文件**config.ini**中`[rpc]`部分的`channel_listen_ip`(若节点小于v2.3.0版本，查看配置项listen_ip)和`channel_listen_port`。
  - SSL配置项: SDK与节点SSL连接时使用
    - `caCert`SL连接根证书路径
    - `sslCert`SDK证书路径
    - `sslKey`SDK证书私钥路径
  - 国密SSL配置项: SDK与节点国密SSL连接时使用
    - `gmCaCert`国密SSL连接根证书路径
    - `gmEnSslCert`国密SSL连接加密证书路径
    - `gmEnSslKey`国密SSL连接加密证书私钥路径
    - `gmSslCert`SSL连接签名证书路径
    - `gmSslKey`SSL连接签名证书私钥路径
  
- channelService: 通过指定群组ID配置SDK实际连接的群组，指定的群组ID是groupChannelConnectionsConfig配置中的群组ID。SDK会与群组中配置的节点均建立连接，然后随机选择一个节点发送请求。

备注：刚下载项目时，有些插件可能没有安装，代码会报错。当你第一次在IDEA上使用lombok这个工具包时，请按以下步骤操作：
- 进入setting->Plugins->Marketplace->选择安装Lombok plugin 　　　　　　
- 进入设置Setting-> Compiler -> Annotation Processors -> 勾选Enable annotation processing。

### Spring Boot项目配置
提供Spring Boot项目中关于`application.yml`的配置如下所示。

```yaml
encrypt-type: # 0：普通， 1：国密
  encrypt-type: 0

group-channel-connections-config:
  caCert: ca.crt
  sslCert: sdk.crt
  sslKey: sdk.key
  all-channel-connections:
    - group-id: 1 #group ID
      connections-str:
        # 若节点小于v2.3.0版本，查看配置项listen_ip:channel_listen_port
        - 127.0.0.1:20200 # node channel_listen_ip:channel_listen_port
        - 127.0.0.1:20201
    - group-id: 2
      connections-str:
        # 若节点小于v2.3.0版本，查看配置项listen_ip:channel_listen_port
        - 127.0.0.1:20202 # node channel_listen_ip:channel_listen_port
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
SDK发送交易需要一个外部账户，下面是随机创建一个外部账户的方法。
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
如果通过账户生成脚本`get_accounts.sh`生成了PEM或PKCS12格式的账户私钥文件(账户生成脚本的用法参考[账户管理文档](../manual/account.md))，则可以通过加载PEM或PKCS12账户私钥文件使用账户。加载私钥有两个类：P12Manager和PEMManager，其中，P12Manager用于加载PKCS12格式的私钥文件，PEMManager用于加载PEM格式的私钥文件。

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

//生成Web3SDK使用的Credentials
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

//生成Web3SDK使用的Credentials
Credentials credentialsPEM = GenCredential.create(pemKeyPair.getPrivateKey().toString(16));
System.out.println("PEM Address: " + credentialsPEM.getAddress());
```

#### 通过SDK部署并调用合约
##### 准备Java合约文件
控制台提供一个专门的编译合约工具，方便开发者将Solidity合约文件编译为Java合约文件，具体使用方式[参考这里](../console/console.html#id10)。

##### 部署并调用合约
SDK的核心功能是部署/加载合约，然后调用合约相关接口，实现相关业务功能。部署合约调用Java合约类的deploy方法，获取合约对象。通过合约对象可以调用getContractAddress方法获取部署合约的地址以及调用该合约的其他方法实现业务功能。如果合约已部署，则通过部署的合约地址可以调用load方法加载合约对象，然后调用该合约的相关方法。
```java
    //读取配置文件，SDK与区块链节点建立连接，获取web3j对象
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
- 启用国密功能：`applicationContext.xml`/`application.yml`配置文件中将`encryptType`属性设置为1。
- 加载私钥使用`GenCredential`类(适用于国密和非国密)，`Credential`类只适用于加载非国密私钥。

国密版SDK调用API的方式与普通版SDK调用API的方式相同，其差异在于国密版SDK需要生成国密版的Java合约文件。编译国密版的Java合约文件[参考这里](../console/console.html#id10)。

## Web3SDK API

Web3SDK API主要分为Web3j API和Precompiled Service API。其中Web3j API可以查询区块链相关的状态，发送和查询交易信息；Precompiled Service API可以管理区块链相关配置以及实现特定功能。

### Web3j API
Web3j API是由web3j对象调用的FISCO BCOS的RPC API，其API名称与RPC API相同，参考[RPC API文档](../api.md)。

### Precompiled Service API
预编译合约是FISCO BCOS底层通过C++实现的一种高效智能合约。SDK已提供预编译合约对应的Java接口，控制台通过调用这些Java接口实现了相关的操作命令，体验控制台，参考[控制台手册](2.6版本控制台[参考这里](../../console/console_of_java_sdk.md)，1.x版本控制台[参考这里](../../console/console.md))。SDK提供Precompiled对应的Service类，分别是分布式控制权限相关的PermissionService，[CNS](../design/features/cns_contract_name_service.md)相关的CnsService，系统属性配置相关的SystemConfigService和节点类型配置相关ConsensusService。相关错误码请参考：[Precompiled Service API 错误码](../api.html#precompiled-service-api)

#### PermissionService
SDK提供对[分布式控制权限](../manual/distributed_storage.md)的支持，PermissionService可以配置权限信息，其API如下：
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
- **int insert(Table table, Entry entry)：** 插入记录，提供表对象和Entry对象。表对象需要设置表名和主键值；Entry是map对象，提供插入的字段名和字段值。返回插入的记录数。
- **int update(Table table, Entry entry, Condition condition)：** 更新记录，提供表对象，Entry对象和Condtion对象。表对象需要设置表名和主键值；Entry是map对象，提供更新的字段名和字段值；Condition对象是条件对象，可以设置更新的匹配条件。返回更新的记录数。
- **List\<Map\<String, String\>\> select(Table table, Condition condition)：** 查询记录，提供表对象和Condtion对象。表对象需要设置表名和主键值；Condition对象是条件对象，可以设置查询的匹配条件。返回查询的记录。
- **int remove(Table table, Condition condition)：** 移除记录，提供表对象和Condtion对象。表对象需要设置表名和主键值；Condition对象是条件对象，可以设置移除的匹配条件。返回移除的记录数。
- **Table desc(String tableName)：** 根据表名查询表的信息，主要包含表的主键和其他属性字段。返回表类型，主要包含表的主键字段名和其他属性字段名。

## 交易解析
FISCO BCOS的交易是一段发往区块链系统的请求数据，用于部署合约，调用合约接口，维护合约的生命周期以及管理资产，进行价值交换等。当交易确认后会产生交易回执，[交易回执](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionreceipt)和[交易](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionbyhash)均保存在区块里，用于记录交易执行过程生成的信息，如结果码、日志、消耗的gas量等。用户可以使用交易哈希查询交易回执，判定交易是否完成。  

交易回执包含三个关键字段，分别是input, output , logs:

| 字段   | 类型      | 描述                               |
| :----- | :-------- | :--------------------------------- |
| input  | String    | 交易输入的ABI编码十六进制字符串    |
| output | String    | 交易返回的ABI编码十六进制字符串    |
| logs   | List<Log> | event log列表，保存交易的event信息 |

交易解析功能帮助用户解析这三个字段为json数据和java对象。

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
调用``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji FISCO-BCOS nice ]```
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
      "data": "FISCO-BCOS"
    },
    {
      "name": "_bs",
      "type": "bytes",
      "data": "nice"
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
      data=FISCO-BCOS
    ],
    ResultEntity[
      name=_bs,
      type=bytes,
      data=nice
    ]
  ]
]
```
#### 解析output
调用``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji FISCO-BCOS nice ]```，echo接口直接将输入返回，因此返回与输入相同

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
      "data": "FISCO-BCOS"
    },
    {
      "name": "",
      "type": "bytes",
      "data": "nice"
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
      data=FISCO-BCOS
    ],
    ResultEntity[
      name=,
      type=bytes,
      data=nice
    ]
  ]
]
```

#### 解析event logs

调用``` function do_event(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji FISCO-BCOS nice ]```，解析交易中的logs

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
        "data": "Fisco Bcos",
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
        "data": "FISCO-BCOS",
        "indexed": false
      },
      {
        "name": "_bs",
        "type": "bytes",
        "data": "nice",
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
        data=FISCO-BCOS
      ],
      ResultEntity[
        name=_bs,
        type=bytes,
        data=nice
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
        data=FISCO-BCOS
      ],
      ResultEntity[
        name=_bs,
        type=bytes,
        data=nices
      ]
    ]
  ]
}
```

## 合约事件推送
### 功能简介
合约事件推送功能提供了合约事件的异步推送机制，客户端向节点发送注册请求，在请求中携带客户端关注的合约事件的参数，节点根据请求参数对请求区块范围的`Event Log`进行过滤，将结果分次推送给客户端。

### 交互协议

客户端与节点的交互基于[`Channel`](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/protocol_description.html#id4)协议。交互分为三个阶段：注册请求，节点回复，Event Log数据推送。

#### 注册请求
客户端向节点发送Event推送的注册请求:

```Json
// request sample:
{
  "fromBlock": "latest",
  "toBlock": "latest",
  "addresses": [
    "0xca5ed56862869c25da0bdf186e634aac6c6361ee"
  ],
  "topics": [
    "0x91c95f04198617c60eaf2180fbca88fc192db379657df0e412a9f7dd4ebbe95d"
  ],
  "groupID": "1",
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967"
}
```

- filerID：字符串类型，每次请求唯一，标记一次注册任务  
- groupID：字符串类型，群组ID  
- fromBlock：整形字符串，初始区块。“latest” 当前块高  
- toBlock：整形字符串，最终区块。“latest” 处理至当前块高时，继续等待新区块  
- addresses：字符串或者字符串数组：字符串表示单个合约地址，数组为多个合约地址，数组可以为空
- topics：字符串类型或者数组类型：字符串表示单个topic，数组为多个topic，数组可以为空

#### 节点回复
节点接受客户端注册请求时，会对请求参数进行校验，将是否成功接受该注册请求结果回复给客户端。

```Json
// response sample:
{
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967",
  "result": 0
}
```

- filterID：字符串类型，每次请求唯一，标记一次注册任务  
- result：整形，返回结果。0成功，其余为失败状态码

#### Event Log数据推送

节点验证客户端注册请求成功之后，根据客户端请求参数条件，向客户端推送`Event`的`Log`数据。

```Json
// event log push sample:
{
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967",
  "result": 0,
  "logs": [
    
  ]
}
```

- filterID：字符串类型，每次请求唯一，标记一次注册任务  
- result：整形 0：`Event Log`数据推送 1：推送完成。客户端一次注册请求对应节点的数据推送会有多次（请求区块范围比较大或者等待新的区块），`result`字段为1时说明节点推送已经结束  
- logs：Log对象数组，result为0时有效  

### Java SDK教程

#### 注册接口  
Java SDK中`org.fisco.bcos.channel.client.Service`类提供合约事件的注册接口，用户可以调用`registerEventLogFilter`向节点发送注册请求，并设置回调函数。
```Java
  public void registerEventLogFilter(EventLogUserParams params, EventLogPushCallback callback);
```

##### `params`注册参数  
事件回调请求注册的参数：  

```Java
public class EventLogUserParams {
    private String fromBlock;   
    private String toBlock;
    private List<String> addresses;
    private List<Object> topics;
}
```

##### `callback`回调对象
```Java
public abstract class EventLogPushCallback {
    public void onPushEventLog(int status, List<LogResult> logs);
}
```

- `status` 回调返回状态：  
```Java
    0       : 正常推送，此时logs为节点推送的Event日志
    1       : 推送完成，执行区间的区块都已经处理
    -41000  : 参数无效，客户端验证参数错误返回
    -41001  : 参数错误，节点验证参数错误返回
    -41002  : 群组不存在
    -41003  : 请求错误的区块区间
    -41004  : 节点推送数据格式错误
    -41005  : 请求发送超时
    -41006  : 其他错误
```

- `logs`表示回调的`Event Log`对象列表，status为0有效  
```Java
  public class LogResult {
    private List<EventResultEntity> logParams;
    private Log log;
  }

  // Log对象
  public class Log {
    private String logIndex;
    private String transactionIndex;
    private String transactionHash;
    private String blockHash;
    private String blockNumber;
    private String address;
    private String data;
    private String type;
    private List<String> topics;
  }
```

`Log log`：Log对象  

`List<EventResultEntity> logParams`：默认值`null`，可以在子类中解析Log的`data`字段，将结果保存入`logParams`   [[参考交易解析]](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk.html#id11) 

- 实现回调对象  
  

Java SDK默认实现的回调类`ServiceEventLogPushCallback`，将`status`与`logs`在日志中打印，用户可以通过继承`ServiceEventLogPushCallback`类，重写`onPushEventLog`接口，实现自己的回调逻辑处理。 
```Java
class MyEventLogPushCallBack extends ServiceEventLogPushCallback {
    @Override
    public void onPushEventLog(int status, List<LogResult> logs) {
        // ADD CODE
    }
}
```
**注意：`onPushEventLog`接口多次回调的`logs`有重复的可能性，可以根据`Log`对象中的`blockNumber，transactionIndex，logIndex`进行去重**  

##### topic工具  
`org.fisco.bcos.channel.event.filter.TopicTools`提供将各种类型参数转换为对应topic的工具，用户设置`EventLogUserParams`的`topics`参数可以使用。

```Java
 class TopicTools {
    // int1/uint1~uint1/uint256 
    public static String integerToTopic(BigInteger i)
    // bool
    public static String boolToTopic(boolean b)
    // address
    public static String addressToTopic(String s)
    // string
    public static String stringToTopic(String s)
    // bytes
    public static String bytesToTopic(byte[] b)
    // byte1~byte32
    public static String byteNToTopic(byte[] b)
}
```

#### Solidity To Java
为了简化使用，`solidity`合约生成对应的`Java`合约代码时，为每个`Event`生成两个重载的同名接口，接口命名规则: `register` + Event名称 + `EventLogFilter`。  

这里以[`Asset`](https://github.com/FISCO-BCOS/LargeFiles/blob/master/tools/asset-app.tar.gz)合约的`TransferEvent`为例说明  

```solidity
contract Asset {
    event TransferEvent(int256 ret, string indexed from_account, string indexed to_account, uint256 indexed amount)

    function transfer(string from_account, string to_account, uint256 amount) public returns(int256) {
        // 结果
        int result = 0;

        // 其他逻辑，省略

        // TransferEvent 保存结果以及接口参数
        TransferEvent(result, from_account, to_account, amount);
    }
}
```

将`Asset.sol`生成对应`Java`合约文件[[将solidity合约生成对应的Java调用文件](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/console.html#id10)]
```Java
class Asset {
    // 其他生成代码 省略

    public void registerTransferEventEventLogFilter(EventLogPushWithDecodeCallback callback);
    public void registerTransferEventEventLogFilter(String fromBlock, String toBlock, List<String> otherTopics, EventLogPushWithDecodeCallback callback);
}
```

##### registerTransferEventEventLogFilter  
  这两个接口对`org.fisco.bcos.channel.client.Service.registerEventLogFilter`进行了封装，调用等价于将`registerEventLogFilter`的`params`参数设置为：
```Java
    EventLogUserParams params = new EventLogUserParams();
    // fromBlock, 无参数设置为“latest”
    params.setFromBlock(fromBlock); // params.setFromBlock("latest");
    // toBlock, 无参数设置为“latest”
    params.setToBlock(toBlock); // params.setToBlock("latest");

    // addresses，设置为Java合约对象的地址
    // 当前java合约对象为：Asset asset 
    ArrayList<String> addresses = new ArrayList<String>();
    addresses.add(asset.getContractedAddress());
    params.setAddresses(addresses);

    // topics, topic0设置为Event接口对应的topic
    ArrayList<Object> topics = new ArrayList<>();
    topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
    // 其他topic设置， 没有则忽略
    topics.addAll(otherTopics);
```
可以看出，在关注指定地址特定合约的某个`Event`，使用生成的Java合约对象中的接口，更加简单方便。

##### EventLogPushWithDecodeCallback
`EventLogPushWithDecodeCallback`与`ServiceEventLogPushCallback`相同，是`EventLogPushCallback`的子类，区别在于：
- `ServiceEventLogPushCallback`回调接口`onPushEventLog(int status, List<LogResult> logs)` `LogResult`成员`logParams`为空，用户需要使用`Log`数据时需要解析数据
- `EventLogPushWithDecodeCallback`作为`Asset`对象的成员，可以根据其保存的`ABI`成员构造对应`Event`的解析工具，解析返回的`Log`数据，解析结果保存在`logParams`中。 

### 示例
这里以`Asset`合约为例，给出合约事件推送的一些场景供用户参考。  

- 场景1：将链上所有/最新的Event回调至客户端
```Java
        // 其他初始化逻辑，省略
        
        // 参数设置
        EventLogUserParams params = new EventLogUserParams();

        // 全部Event fromBlock设置为"1" 
        params.setFromBlock("1");

        // 最新Event fromBlock设置为"latest"
        // params.setFromBlock("latest");

        // toBlock设置为"latest"，处理至最新区块继续等待新的区块
        params.setToBlock("latest");

        // addresses设置为空数组，匹配所有的合约地址
        params.setAddresses(new ArrayList<String>());

        // topics设置为空数组，匹配所有的Event
        params.setTopics(new ArrayList<Object>());

        // 回调，用户可以替换为自己实现的类的回调对象
        ServiceEventLogPushCallback callback = new ServiceEventLogPushCallback();
        service.registerEventLogFilter(params, callback);
```

- 场景2: 将`Asset`合约最新的`TransferEvent`事件回调至客户端
```Java
        // 其他初始化逻辑，省略
        
        // 设置参数
        EventLogUserParams params = new EventLogUserParams();

        // 从最新区块开始，fromBlock设置为"latest"
        params.setFromBlock("latest");
        // toBlock设置为"latest"，处理至最新区块继续等待新的区块
        params.setToBlock("latest");

        // addresses设置为空数组，匹配所有的合约地址
        params.setAddresses(new ArrayList<String>());
        
        // topic0，TransferEvent(int256,string,string,uint256)
        ArrayList<Object> topics = new ArrayList<>();
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        params.setTopics(topics);

        // 回调，用户可以替换为自己实现的类的回调对象
        ServiceEventLogPushCallback callback = new ServiceEventLogPushCallback();
        service.registerEventLogFilter(params, callback);
```


- 场景3: 将指定地址的`Asset`合约最新的`TransferEvent`事件回调至客户端 

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324";`

方案1.  
```Java
        // 其他初始化逻辑，省略

        String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
        
        // 设置参数
        EventLogUserParams params = new EventLogUserParams();

        // 从最新区块开始，fromBlock设置为"latest"
        params.setFromBlock("latest");
        // toBlock设置为"latest"，处理至最新块并继续等待共识出块
        params.setToBlock("latest");

        // 合约地址
        ArrayList<String> addresses = new ArrayList<String>();
        addresses.add(addr);
        params.setAddresses(addresses);
        
        // topic0，匹配 TransferEvent(int256,string,string,uint256) 事件
        ArrayList<Object> topics = new ArrayList<>();
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,uint256)"));
        params.setTopics(topics);

        ServiceEventLogPushCallback callback = new ServiceEventLogPushCallback();
        service.registerEventLogFilter(params, callback);
```

方案2.
```Java
        // 其他初始化逻辑，省略

        String addr = "0x06922a844c542df030a2a2be8f835892db99f324";

        // 构造Asset合约对象
        Asset asset = Asset.load(addr, ... );

        EventLogPushWithDecodeCallback callback = new EventLogPushWithDecodeCallback();
        asset.registerTransferEventEventLogFilter(callback);
```

- 场景4: 将指定地址的`Asset`合约所有`TransferEvent`事件回调至客户端 

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324";`

方案1：
```Java
        // 其他初始化逻辑，省略
        
        // 设置参数
        EventLogUserParams params = new EventLogUserParams();

        // 从最初区块开始，fromBlock设置为"1"
        params.setFromBlock("1");
        // toBlock设置为"latest"，处理至最新块并继续等待共识出块
        params.setToBlock("latest");

        // 设置合约地址
        ArrayList<String> addresses = new ArrayList<String>();
        addresses.add(addr);
        params.setAddresses(addresses);
        
        // TransferEvent(int256,string,string,uint256) 转换为topic
        ArrayList<Object> topics = new ArrayList<>();
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        params.setTopics(topics);

        ServiceEventLogPushCallback callback = new ServiceEventLogPushCallback();
        service.registerEventLogFilter(params, callback);
```

方案2.
```Java
        // 其他初始化逻辑，省略
        
        Asset asset = Asset.load(addr, ... );
        
        // 设置区块范围
        String fromBlock = "1";
        String toBlock = "latest";

        // 参数topic为空
        ArrayList<Object> otherTopics = new ArrayList<>();

        EventLogPushWithDecodeCallback callback = new EventLogPushWithDecodeCallback();

        asset.registerTransferEventEventLogFilter(fromBlock,toBlock,otherTopics,callback);
```

- 场景5: 将`Asset`指定合约指定账户转账的所有事件回调至客户端 

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324"`  

转账账户: `String fromAccount = "account"`

方案1：
```Java
        // 其他初始化逻辑，省略

        String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
        String fromAccount = "account";
        
        // 参数
        EventLogUserParams params = new EventLogUserParams();

        // 从最初区块开始，fromBlock设置为"1"
        params.setFromBlock("1");
        // toBlock设置为"latest"
        params.setToBlock("latest");

        // 设置合约地址
        ArrayList<String> addresses = new ArrayList<String>();
        addresses.add(addr);
        params.setAddresses(addresses);
        
        // 设置topic
        ArrayList<Object> topics = new ArrayList<>();
        // TransferEvent(int256,string,string,uint256) 转换为topic
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        // 转账账户 fromAccount转换为topic
        topics.add(TopicTools.stringToTopic(fromAccount));
        params.setTopics(topics);

        ServiceEventLogPushCallback callback = new ServiceEventLogPushCallback();
        service.registerEventLogFilter(params, callback);
```

方案2.
```Java
        // 其他初始化逻辑，省略

        String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
        String fromAccount = "account";
        
        // 加载合约地址，生成Java合约对象
        Asset asset = Asset.load(addr, ... );

        // 回调函数
        EventLogPushWithDecodeCallback callback = new EventLogPushWithDecodeCallback();
        
        // 设置区块范围
        String fromBlock = "1";
        String toBlock = "latest";
        // 参数topic
        ArrayList<Object> otherTopics = new ArrayList<>();
        // 转账账户 fromAccount转换为topic
        otherTopics.add(TopicTools.stringToTopic(fromAccount));

        asset.registerRegisterEventEventLogFilter(fromBlock,toBlock,otherTopics,callback);
```

## 附录：JavaSDK启动异常场景  

- Failed to connect to the node. Please check the node status and the console configuration.<br>    
  比较旧的SDK版本的提示，建议将JavaSDK版本升级至**2.2.2**或者以上(修改gradle.build或者maven配置文件中web3sdk的版本号)，可以获取更准确友好的提示，然后参考下面的错误提示解决问题。<br> <br>    

- Failed to initialize the SSLContext: class path resource [ca.crt] cannot be opened because it does not exist. <br>   
  无法加载到证书文件，证书文件没有正确拷贝至conf目录，可以参考控制台安装流程，拷贝证书文件至conf目录下。<br><br>  

- Failed to initialize the SSLContext: Input stream not contain valid certificates. <br> 
  加载证书文件失败，CentOS系统使用OpenJDK的错误，参考[CentOS环境安装JDK](../console/console.html#java)章节重新安装OracleJDK。<br><br> 

- Failed to connect to nodes: [connection timed out: /127.0.0.1:20200]<br>  
  连接超时，节点的网络不可达，请检查提示的IP是否配置错误，或者，当前JavaSDK运行环境与节点的环境网络确实不通，可以咨询运维人员解决网络不通的问题。<br><br> 

- Failed to connect to nodes: [Connection refused: /127.0.0.1:20200]<br>  
  拒绝连接，无法连接对端的端口，可以使用telnet命令检查端口是否连通，可能原因：
  1. 节点未启动，端口处于未监听状态，启动节点即可。
  2. 节点监听`127.0.0.1`的网段，监听`127.0.0.1`网络只能本机的客户端才可以连接，控制台位于不同服务器时无法连接节点，将节点配置文件`config.ini`中的`channel_listen_ip`修改为控制台连接节点使用的网段IP，或者将其修改为`0.0.0.0`。
  3. 错误的端口配置，配置的端口并不是节点监听的channel端口，修改连接端口为节点`config.ini`配置的`channel_listen_port`的值。<br>  
   **注意：控制台（或者JavaSDK）连接节点时使用Channel端口，并不是RPC端口，Channel端口在节点配置文件中通过channel_listen_ip字段配置，RPC端口通过jsonrpc_listen_port字段配置，注意区分，RPC默认从8545开始分配, Channel端口默认从20200开始分配。**  

- Failed to connect to nodes: [ ssl handshake failed:/127.0.0.1:20233]
  与节点ssl握手失败，可能原因：<br>  
  1. 拷贝了错误的证书，检查拷贝的证书是否正确。
  2. 端口配置错误，连接其他服务正在监听的端口，检查连接端口是否为节点`channel_listen_port`端口。
  3. JDK版本问题，推荐使用1.8以及以上的OracleJDK，参考[CentOS环境安装JDK](../console/console.html#java)章节安装OracleJDK。<br><br> 
  
- Failed to connect to [127.0.0.1:20233, 127.0.0.1:20234, 127.0.0.1:20235] ,groupId: 1 ,caCert: classpath:ca.crt ,sslKey: classpath:sdk.key ,sslCrt: classpath:sdk.crt ,java version: 1.8.0_231.<br>
  其他未知的错误，需要查看日志文件分析具体错误。