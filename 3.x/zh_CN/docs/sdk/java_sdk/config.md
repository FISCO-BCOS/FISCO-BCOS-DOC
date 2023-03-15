# 配置说明

标签：``java-sdk`` ``配置``

----

Java SDK主要包括五个配置选项，分别是

* 证书配置（必须）
* 网络连接配置 （必须）
* AMOP配置（非必须）
* 账户配置（非必须，不配置则使用默认配置值）
* 线程池配置（非必须，不配置则使用默认配置值）
* Cpp SDK日志配置（必须）

支持的配置格式，包括

* toml(默认)
* properties
* yml
* xml

其中`properties`、`yml`和`xml`格式的配置文件示例及使用方法详见[4. 其它格式的配置](./config.html#id10)

## 1. 快速配置

### 1.1 配置步骤

1. 在您的应用的主目录下新建一个`conf`目录。

2. 从节点`nodes/${ip}/sdk/` 目录下的证书拷贝到新建的`conf`目录。

3. 将配置文件config-example.toml, 存放在应用的主目录下。

   * config-example.toml可以在java-sdk [GitHub链接](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/config-example.toml)或[Gitee链接](https://gitee.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/config-example.toml)的源文件以下位置找到：`src/test/resources/config-example.toml`
   * 本文的“3. 配置示例” 部分也可以看到``config-example.toml``的内容。

4. 修改config-example.toml中节点的IP和端口，与您要连接的节点所匹配。

   ```toml
   [network]
   peers=["127.0.0.1:20200", "127.0.0.1:20201"] 
   ```

5. 在您的应用中使用该配置文件初始化Java SDK，您就可以开发区块链应用了。

   ```java
   String configFile = "config-example.toml";
   BcosSDK sdk =  BcosSDK.build(configFile);
   ```

关于如何在业务项目中引入以及配置Java SDK的进一步详细说明，可参考[例子](./quick_start.html#java-sdk)。对一项目，完成Java SDK引入、配置文件及证书放置、编译后，项目的dist目录结构如下：

```shell
├── lib
│   ├── fisco-bcos-java-sdk-3.x.x.jar
│   └── XXXXX.jar
├── conf
│   ├── applicationContext.xml
│   ├── clog.ini
│   ├── config.toml
|   ├── node.crt
|   ├── ca.crt
|   ├── sdk.publickey
|   ├── sdk.key
|   ├── node.key
|   ├── sdk.crt
│   └── log4j.properties
├── apps
│   └── XXXX.jar
└── other folders
```

## 2. 配置解读

Java sdk主要包括五个配置选项，分别是

* 证书配置（必须）
* 网络连接配置 （必须）
* AMOP配置（非必须）
* 账户配置（非必须，不配置则使用默认配置值）
* 线程池配置（非必须，不配置则使用默认配置值）

### 证书配置

基于安全考虑，Java SDK与节点间采用SSL加密通信，目前同时支持非国密SSL连接以及国密SSL连接，`[cryptoMaterial]`配置SSL连接的证书信息，具体包括如下配置项：

* `certPath`: 证书存放路径，默认是`conf`目录；

* `caCert`: CA证书路径，默认注释该配置项，该配置项注释时，当SDK与节点间采用非国密SSL连接时，默认的CA证书路径为`${certPath}/ca.crt`，当SDK与节点采用国密SSL连接时，默认的CA证书路径为`${certPath}/sm_ca.crt`；开启该配置项时，从配置指定的路径加载CA证书；

* `sslCert`: SDK证书路径，默认注释该配置项，该配置项注释时，当SDK与节点间采用非国密SSL连接时，从`${certPath}/sdk.crt`加载SDK证书，当SDK与节点间采用国密SSL连接时，从`${certPath}/sm_sdk.crt`加载SDK证书；开启该配置选项时，从配置指定的路径加载SDK证书；

* `sslKey`: SDK私钥路径，默认注释该配置项，当该配置项注释时，当SDK与节点间采用非国密SSL连接时，从`${certPath}/sdk.key`加载SDK私钥，SDK与节点采用国密SSL连接时，从`${certPaht}/sm_sdk.key`加载SDK私钥；开启该配置项时，直接从配置项指定的路径加载SDK私钥；

* `enSslCert`: 国密SSL加密证书路径，仅当SDK与节点间采用国密SSL连接时，需要配置该配置项，默认从`${certPath}/sm_ensdk.crt`加载国密SSL加密证书；当开启该配置项时，从配置项指定的路径加载国密SSL加密证书；

* `enSslKey`: 国密SSL加密私钥路径，仅当SDK与节点间采用国密SSL连接时，需配置该配置项，默认从`${certPath}/sm_ensdk.key`加载国密SSL加密私钥；当卡其该配置项时，从配置项指定的路径加载国密SSL加密私钥。

```eval_rst
.. note::
    - 大部分场景仅需要配置 `certPath` 配置项即可，其他配置项不需额外配置；
    - SDK证书获取：参考 `SDK连接证书配置 <../cert_config.html>`
    - SDK与节点RPC间SSL连接方式，可通过节点配置项 `sm_crypto` 判断，该配置项详细说明请参考 `FISCO BCOS配置文件与配置项说明 <../../../tutorial/air/config.html#rpc>`_ .
```

SDK证书配置示例如下：

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path
useSMCrypto = "false"                       # RPC SM crypto type

# The following configurations take the certPath by default if commented
# caCert = "conf/ca.crt"                    # CA cert file path
# sslCert = "conf/sdk.crt"                  # SSL cert file path
# sslKey = "conf/sdk.key"                   # SSL key file path

# The following configurations take the sm certPath by default if commented
# caCert = "conf/sm_ca.crt"                    # SM CA cert file path
# sslCert = "conf/sm_sdk.crt"                  # SM SSL cert file path
# sslKey = "conf/sm_sdk.key"                    # SM SSL key file path
# enSslCert = "conf/sm_ensdk.crt"               # SM encryption cert file path
# enSslKey = "conf/sm_ensdk.key"                # SM ssl cert file path
```

### 网络连接配置

SDK与FISCO BCOS节点通信，必须配置SDK连接的节点的`IP`和`Port`，`[network]`配置了Java SDK连接的节点信息，具体包括如下配置项：

* peers：SDK连接的节点的`IP:Port`信息，可配置多个连接。
* defaultGroup：SDK默认发送请求的群组

```eval_rst
.. note::
    节点与网络之间的连接信息
    SDK与节点间通过 `RPC` 进行通信，SDK需要连接 `RPC` 的监听端口，该端口可通过节点 `config.ini` 的 `rpc.listen_port` 获取，具体请参考 `这里 <../../../tutorial/air/config.html#rpc>`_
```

SDK与节点间的网络配置示例如下：

```toml
[network]
peers=["127.0.0.1:20200", "127.0.0.1:20201"]    # The peer list to connect
defaultGroup = "group0"
```

### 账户配置

账户配置主要用于设置SDK向节点发交易的账户信息，SDK初始化[client](./rpc_api.md)时，默认读取`[account]`配置项加载账户信息，具体如下： 

* `keyStoreDir`: 加载/保存账户文件的路径，默认为`account`；

* `accountFileFormat`: 账户文件格式，默认为`pem`，目前仅支持`pem`和`p12`，`pem`格式的账户文件不需要口令加载，加载`p12`格式的账户文件时需要口令；

* `accountAddress`: 加载的账户地址，默认为空

* `accountFilePath`: 加载的账户文件路径，默认注释该配置项，注释该配置项时，当SDK连接非国密区块链时，默认从`${keyStoreDir}/ecdsa/${accountAddress}.${accountFileFormat}`路径加载账户文件，当SDK连接国密区块链时，默认从`${keyStoreDir}/gm/${accountAddress}.${accountFileFormat}`路径加载账户；当开启该配置项时，从该配置项指定的目录加载账户；

* `password`: 加载`p12`类型账户文件的口令。

```eval_rst
.. note::
    当没有配置 `accountAddress` 和 `accountFilePath` 时，SDK会生成随机的账户向节点交易，生成的账户信息均保存在 `keyStoreDir` 配置项指定的目录下：当SDK连接节点是非国密节点时，生成的临时账户以 `pem` 的格式保存在 `${keyStoreDir}/ecdsa/` 目录下；当SDK连接的节点时国密节点时，生成的临时账户以 `p12` 的格式保存在 `${keyStoreDir}/gm` 目录下
```

账户配置文件示例如下：

```toml
[account]
keyStoreDir = "account"         # The directory to load/store the account file, default is "account"
# accountFilePath = ""          # The account file path (default load from the path specified by the keyStoreDir)
accountFileFormat = "pem"       # The storage format of account file (Default is "pem", "p12" as an option)

# accountAddress = ""           # The transactions sending account address
                                # Default is a randomly generated account
                                # The randomly generated account is stored in the path specified by the keyStoreDir

# password = ""                 # The password used to load the account file
```

### 线程池配置

为了方便业务根据机器实际负载调整SDK的处理线程，Java SDK将其线程配置项暴露在配置中，`[threadPool]`是线程池相关配置，具体包括：

* `threadPoolSize`: 接收交易的线程数目，默认注释该配置项，注释该配置项时，默认值为机器的CPU数目；开启该配置项时，根据配置的值创建接收交易的线程数目；

```eval_rst
.. note::
    大多数场景下，不需要手工配置线程池配置；压测场景下，可将 `maxBlockingQueueSize` 配置大一些。
```

线程池配置示例如下：

```toml
[threadPool]
# threadPoolSize = "16"         # The size of the thread pool to process message callback
                                # Default is the number of cpu cores
```

### Cpp SDK日志配置

因为Java SDK是使用JNI封装的Cpp SDK的接口，对节点进行的操作，因此在启动Java SDK时也会输出Cpp SDK的日志。Cpp SDK的日志以独立的文件形式存在于配置文件中，文件名为`clog.ini`，JNI在启动时将在 `classpath`下的根目录或者conf目录下找这个文件。一般而言，该文件不需要额外配置，按照默认即可。

日志文件示例如下：

```ini
[log]
    enable=true
    log_path=./log
    ; info debug trace
    level=DEBUG
    ; MB
    max_log_file_size=200
```

## 3. 配置示例

config-example.toml

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path  
useSMCrypto = "false"                       # RPC SM crypto type

# The following configurations take the certPath by default if commented
# caCert = "conf/ca.crt"                    # CA cert file path
# sslCert = "conf/sdk.crt"                  # SSL cert file path
# sslKey = "conf/sdk.key"                   # SSL key file path

# The following configurations take the sm certPath by default if commented
# caCert = "conf/sm_ca.crt"                    # SM CA cert file path
# sslCert = "conf/sm_sdk.crt"                  # SM SSL cert file path
# sslKey = "conf/sm_sdk.key"                    # SM SSL key file path
# enSslCert = "conf/sm_ensdk.crt"               # SM encryption cert file path
# enSslKey = "conf/sm_ensdk.key"                # SM ssl cert file path

[network]
messageTimeout = "10000"
defaultGroup="group0"                            # Console default group to connect
peers=["127.0.0.1:20200", "127.0.0.1:20201"]    # The peer list to connect

[account]
authCheck = "false"
keyStoreDir = "account"         # The directory to load/store the account file, default is "account"
# accountFilePath = ""          # The account file path (default load from the path specified by the keyStoreDir)
accountFileFormat = "pem"       # The storage format of account file (Default is "pem", "p12" as an option)

# accountAddress = ""           # The transactions sending account address
                                # Default is a randomly generated account
                                # The randomly generated account is stored in the path specified by the keyStoreDir

# password = ""                 # The password used to load the account file

[threadPool]
# threadPoolSize = "16"         # The size of the thread pool to process message callback
                                # Default is the number of cpu cores
```

## 4. 其它格式的配置

Java SDK还支持`properties`、`yml`以及`xml`格式的配置文件。

### properties格式

#### 配置示例

各字段的含义以及默认值与`toml`配置文件一致。

在项目的主目录创建文件`fisco-config.properties`，复制以下配置内容，并根据实际情况修改各配置项。

```properties
cryptoMaterial.certPath=conf                       # The certification path
cryptoMaterial.useSMCrypto=false

# The following configurations take the certPath by default if commented
# cryptoMaterial.caCert=conf/ca.crt 
# cryptoMaterial.sslCert=conf/sdk.crt
# cryptoMaterial.sslKey=conf/sdk.key
# cryptoMaterial.enSslCert=conf/sm_ensdk.crt
# cryptoMaterial.enSslKey=conf/sm_ensdk.key


# The peer list to connect
network.defaultGroup=group0 
network.peers[0]=127.0.0.1:20200
network.peers[0]=127.0.0.1:21200


# AMOP configuration

# Configure a private topic as a topic message sender.
# amop[0].publicKeys[0]=conf/amop/consumer_public_key_1.pem
# amop[0].topicName=PrivateTopic1

# Configure a private topic as a topic subscriber.
# amop[1].password=123456 
# amop[1].privateKey=conf/amop/consumer_private_key.p12
# amop[1].topicName=PrivateTopic2


account.keyStoreDir=account
# account.accountFilePath=conf
account.accountFileFormat=pem
# account.accountAddress=0x
# account.password=123456

# threadPool.threadPoolSize=16
```

#### 代码示例

使用SpringBoot的配置装载方法，创建配置类:

```java
@Data
@Configuration
@ConfigurationProperties
public class BcosConfig {
    private Map<String, Object> cryptoMaterial;
    public Map<String, List<String>> network;
    public List<AmopTopic> amop;
    public Map<String, Object> account;
    public Map<String, Object> threadPool;
}

```

基于配置类初始化`BcosSDK`:

```java
@Slf4j
@Data
@Component
public class FiscoBcos {

    @Autowired
    BcosConfig bcosConfig;

    BcosSDK bcosSDK;

    public void init() {
        ConfigProperty configProperty = loadProperty();
        ConfigOption configOption;
        try {
            configOption = new ConfigOption(configProperty, CryptoType.ECDSA_TYPE);
        } catch (ConfigException e) {
            log.error("init error:" + e.toString());
            return ;
        }
        bcosSDK = new BcosSDK(configOption);
    }

    public ConfigProperty loadProperty() {
        ConfigProperty configProperty = new ConfigProperty();
        configProperty.setCryptoMaterial(bcosConfig.getCryptoMaterial());
        configProperty.setAccount(bcosConfig.getAccount());
        configProperty.setNetwork(new HashMap<String, Object>(){{
            put("peers", bcosConfig.getNetwork().get("peers"));
        }} );
        configProperty.setAmop(bcosConfig.getAmop());
        configProperty.setThreadPool(bcosConfig.getThreadPool());
        return configProperty;
    }
}
```

### yml格式

#### yml配置示例

各字段的含义以及默认值与`toml`配置文件一致。

在项目的主目录创建文件`fisco-config.yml`，复制以下配置内容，并根据实际情况修改各配置项。

```yml
cryptoMaterial:
  useSMCrypto: false
  certPath: "conf"                   
#  caCert: "conf/ca.crt"               
#  sslCert: "conf/sdk.crt"             
#  sslKey: "conf/sdk.key"
#  enSslCert: "conf/sm_ensdk.crt"
#  enSslKey: "conf/sm_ensdk.key"

network:
  defaultGroup: "group0"
  peers:
    - "127.0.0.1:20201"
    - "127.0.0.1:20200"

amop:
#  - publicKeys: [ "conf/amop/consumer_public_key_1.pem" ]
#    topicName: "PrivateTopic1"
#  - password: "123456"
#    privateKey: "conf/amop/consumer_private_key.p12"
#    topicName: "PrivateTopic2"

account:
  keyStoreDir: "account"
#  accountFilePath: "conf"
  accountFileFormat: "pem"
#  accountAddress: "0x"
#  password: ""


threadPool:
#  threadPoolSize: "16"
```

#### yml配置代码示例

引入`yml`文件解析工具：

```gradle
compile ("org.yaml:snakeyaml:1.27")
```

初始化`BcosSDK`:

```java
@Data
@Component
@Slf4j
public class FiscoBcos {

    BcosSDK bcosSDK;

    public void init() {
        ConfigProperty configProperty = loadProperty();
        ConfigOption configOption ;
        try {
            configOption = new ConfigOption(configProperty, CryptoType.ECDSA_TYPE);
        } catch (ConfigException e) {
            log.error("init error:" + e.toString());
            return ;
        }
        bcosSDK = new BcosSDK(configOption);
    }

    public ConfigProperty loadProperty() {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml(representer);
        String configFile = "/fisco-config.yml";
        try (InputStream inputStream = this.getClass().getResourceAsStream(configFile)) {
            return yaml.loadAs(inputStream, ConfigProperty.class);
        } catch (Exception e) {
            log.error("load property: ", e);
        }
    }
}
```

### xml格式

#### xml配置示例

各property的含义与`toml`配置文件一致。

在项目的主目录创建文件`fisco-config.xml`，复制以下配置内容，并根据实际情况修改各配置项。

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-4.0.xsd">
    <bean id="defaultConfigProperty" class="org.fisco.bcos.sdk.v3.config.model.ConfigProperty">
        <property name="cryptoMaterial">
            <map>
                <entry key="certPath" value="conf" />
                <entry key="useSMCrypto" value="false"/>
            </map>
        </property>
        <property name="network">
            <map>
                <entry key="peers">
                    <list>
                        <value>127.0.0.1:20200</value>
                        <value>127.0.0.1:20201</value>
                    </list>
                </entry>
                <entry key="defaultGroup" value="group0" />
            </map>
        </property>
        <property name="account">
            <map>
                <entry key="keyStoreDir" value="account" />
                <entry key="accountAddress" value="" />
                <entry key="accountFileFormat" value="pem" />
                <entry key="password" value="" />
                <entry key="accountFilePath" value="" />
            </map>
        </property>
        <property name="threadPool">
            <map>
                <entry key="threadPoolSize" value="16" />
            </map>
        </property>
    </bean>

    <bean id="defaultConfigOption" class="org.fisco.bcos.sdk.v3.config.ConfigOption">
        <constructor-arg name="configProperty">
            <ref bean="defaultConfigProperty"/>
        </constructor-arg>
    </bean>

    <bean id="bcosSDK" class="org.fisco.bcos.sdk.v3.BcosSDK">
        <constructor-arg name="configOption">
            <ref bean="defaultConfigOption"/>
        </constructor-arg>
    </bean>
</beans>
```

#### xml配置代码示例

初始化`BcosSDK`:

```java
    @SuppressWarnings("resource")
    ApplicationContext context =
            new ClassPathXmlApplicationContext("classpath:fisco-config.xml");
    BcosSDK bcosSDK = context.getBean(BcosSDK.class);
```
