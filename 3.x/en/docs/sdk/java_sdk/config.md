# Configuration Description

Tags: "java-sdk" "Configuration"

----

The Java SDK mainly includes five configuration options, namely

* Certificate Configuration (Required)
* Network Connection Configuration (Required)
* AMOP Configuration (not required)
* Account configuration (not required, use default configuration values if not configured)
* Thread pool configuration (not required, use default configuration values if not configured)
* Cpp SDK log configuration (not required, not configured by default)

Supported configuration formats, including

* toml(Default)
* properties
* yml
* xml

Examples of configuration files in 'properties', 'yml' and 'xml' formats and how to use them are detailed in [4. Configurations in other formats](./config.html#id10)

## 1. Quick Configuration

### 1.1 Configuration steps

1. Create a new 'conf' directory in your application's home directory。

2. From node 'nodes / ${ip}Copy the certificate from the / sdk / 'directory to the new' conf 'directory。

3. Store the configuration file config-example.toml in the application's home directory。

   * config-example.toml can be found in java-sdk [GitHub link](https://github.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/config-example.toml)or [Gitee link](https://gitee.com/FISCO-BCOS/java-sdk/blob/master/src/test/resources/config-example.toml)The source file for 'src / test / resources / config-example.toml'
   * You can also see the contents of "config-example.toml" in the "3. Configuration Example" section of this article。

4. Modify the IP and port of the node in config-example.toml to match the node you want to connect to。

   ```toml
   [network]
   peers=["127.0.0.1:20200", "127.0.0.1:20201"] 
   ```

5. Initialize the Java SDK with the configuration file in your application, and you can develop a blockchain application。

   ```java
   String configFile = "config-example.toml";
   BcosSDK sdk =  BcosSDK.build(configFile);
   ```

For further details on how to introduce and configure the Java SDK in a business project, refer to [Example](./quick_start.html#java-sdk)。For a project, after the Java SDK is introduced, configuration files and certificates are placed, and compiled, the dist directory structure of the project is as follows:

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

## 2. Configuration Interpretation

The Java SDK consists of five configuration options:

* Certificate Configuration (Required)
* Network Connection Configuration (Required)
* AMOP Configuration (not required)
* Account configuration (not required, use default configuration values if not configured)
* Thread pool configuration (not required, use default configuration values if not configured)

### Certificate Configuration

For security reasons, the Java SDK and the node use SSL encryption communication, currently supports both non-state secret SSL connection and state secret SSL connection, java-sdk version 3.3.0 adds support for cipher machine, transaction signature verification can use the key in the cipher machine。'[cryptoMaterial]' Configure the certificate information of the SSL connection, including the following configuration items:

* `certPath`: The certificate storage path. The default value is the 'conf' directory；

* `caCert`: The path of the CA certificate. This configuration item is commented by default. When this configuration item is commented, the default path of the CA certificate is' ${certPath}/ ca.crt '. The default CA certificate path is' $'when the SDK and the node are connected by using state-secret SSL{certPath}/sm_ca.crt`；When this configuration item is turned on, the CA certificate is loaded from the path specified by the configuration；

* `sslCert`: The path of the SDK certificate. This configuration item is annotated by default. When this configuration item is annotated, when a non-state-secret SSL connection is used between the SDK and the node, from '${certPath}/ sdk.crt 'Loads the SDK certificate. When the SDK and the node are connected to each other using the state-secret SSL connection, run the command from' ${certPath}/ sm _ sdk.crt 'Load SDK certificate；When this configuration option is enabled, the SDK certificate is loaded from the path specified by the configuration；

* `sslKey`: The path of the SDK private key. This configuration item is annotated by default. When this configuration item is annotated, when a non-state-secret SSL connection is used between the SDK and the node, from '${certPath}/ sdk.key 'Loads the SDK private key. When the SDK and the node are connected using the state-secret SSL, run the command from' ${certPaht}/ sm _ sdk.key 'Load SDK private key；When the configuration item is enabled, the SDK private key is directly loaded from the path specified by the configuration item；

* `enSslCert`: The path of the state-secret SSL encryption certificate. Only when the SDK and the node use the state-secret SSL connection, you need to configure this configuration item. The default value is from '${certPath}/ sm _ ensdk.crt 'Load the SSL encryption certificate；When this configuration item is enabled, the state-secret SSL encryption certificate is loaded from the path specified by the configuration item；

* `enSslKey`: The path of the private key for state-secret SSL encryption. This configuration item must be configured only when the state-secret SSL connection is used between the SDK and the node. The default value is from '${certPath}/ sm _ ensdk.key 'Load the SSL encryption private key；When the configuration item is blocked, the SSL encryption private key is loaded from the path specified by the configuration item。

* `useSMCrypto`: Whether to use the State Secret SSL connection. True indicates that the State Secret SSL connection is used；

* `enableHsm`: Whether to use a cipher machine, true to use a cipher machine；

* `hsmLibPath`: File path of cipher machine dynamic library；

* `hsmKeyIndex`: Index of cipher machine keys；

* `hsmPassword`: Password for cipher machine；

```eval_rst
.. note::
    - Most scenarios only need to configure the 'certPath' configuration item, other configuration items do not need additional configuration；
    - SDK certificate acquisition: see 'SDK connection certificate configuration<../cert_config.html>`_ .
    - The SSL connection mode between the SDK and the RPC node, which can be determined by the node configuration item 'sm _ crypto'. For more information about this configuration item, see 'FISCO BCOS Configuration File and Configuration Item Description<../../tutorial/air/config.html#rpc>`_ .
```

The SDK certificate configuration example is as follows:

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path
useSMCrypto = "false"                       # RPC SM crypto type

# enableHsm = "false"
# hsmLibPath = "/usr/local/lib/libgmt0018.so"
# hsmKeyIndex = "1"
# hsmPassword = "12345678"

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

### Network Connection Configuration

When the SDK communicates with the FISCO BCOS node, you must configure the 'IP' and 'Port' of the node to which the SDK is connected, and configure the node information to which the Java SDK is connected, including the following configuration items:

* peers: IP address of the node to which the SDK is connected:Port 'information, multiple connections can be configured。
* defaultGroup: The group that the SDK sends requests to by default

```eval_rst
.. note::
    Connection information between nodes and the network
    The SDK communicates with the node through 'RPC'. The SDK needs to connect to the listening port of 'RPC'. This port can be obtained through the 'rpc.listen _ port' of the node 'config.ini'<../../tutorial/air/config.html#rpc>`_
```

The network configuration example between the SDK and the node is as follows:

```toml
[network]
peers=["127.0.0.1:20200", "127.0.0.1:20201"]    # The peer list to connect
defaultGroup = "group0"
```

### Account Configuration

Account configuration is mainly used to set the account information for the SDK to send transactions to the node, SDK initialization [client](./rpc_api.md)By default, the '[account]' configuration item is read to load the account information, as follows:

* `keyStoreDir`: Path to load / save account files, default is' account'；

* `accountFileFormat`: The default file format is' pem '. Currently, only' pem 'and' p12 'are supported. You do not need a password to load an account file in' pem 'format. You need a password to load an account file in' p12 'format；

* `accountAddress`: Loaded account address, empty by default

* `accountFilePath`: The path of the loaded account file. The configuration item is annotated by default. When the configuration item is annotated, when the SDK is connected to the non-state secret blockchain, the default value is from '${keyStoreDir}/ecdsa/${accountAddress}.${accountFileFormat}'Path to load the account file, when the SDK connects to the State Secret blockchain, the default value is from '${keyStoreDir}/gm/${accountAddress}.${accountFileFormat}'Path Load Account；When the configuration item is turned on, the account is loaded from the directory specified by the configuration item；

* `password`: Password to load 'p12' type account file。

```eval_rst
.. note::
    When 'accountAddress' and 'accountFilePath' are not configured, the SDK generates random account-to-node transactions, and the generated account information is stored in the directory specified by the 'keyStoreDir' configuration item: When the SDK connection node is a non-state secret node, the generated temporary account is stored in the '$' format{keyStoreDir}/ ecdsa / 'directory；The generated temporary account is saved in the format of 'p12' in the '${keyStoreDir}/ gm 'directory
```

An example account profile is as follows:

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

### Thread Pool Configuration

In order to facilitate the business to adjust the processing threads of the SDK according to the actual load of the machine, the Java SDK exposes its thread configuration items in the configuration, '[threadPool]' is the thread pool-related configuration, including:

* `threadPoolSize`: The number of threads that receive transactions. This configuration item is commented by default. When this configuration item is commented, the default value is the number of CPUs of the machine；When this configuration item is enabled, the number of threads that receive transactions is created based on the configured value；

```eval_rst
.. note::
    In most scenarios, you do not need to manually configure the thread pool configuration；In the pressure test scenario, you can set 'maxBlockingQueueSize' to a larger size。
```

An example thread pool configuration is as follows:

```toml
[threadPool]
# threadPoolSize = "16"         # The size of the thread pool to process message callback
                                # Default is the number of cpu cores
```

### Cpp SDK Log Configuration

Because the Java SDK uses the interface of the Cpp SDK encapsulated by JNI to perform operations on nodes, the logs of the Cpp SDK are also output when the Java SDK is started。The Cpp SDK log exists as a separate file in the configuration file. The file name is' clog.ini '. JNI will find this file in the root directory or conf directory under' classpath 'when starting。In general, the file does not require additional configuration, according to the default。

An example of a log file is as follows:

```ini
[log]
    enable=true
    log_path=./log
    ; info debug trace
    level=DEBUG
    ; MB
    max_log_file_size=200
```

## 3. Configuration Example

config-example.toml

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path  
useSMCrypto = "false"                       # RPC SM crypto type

# enableHsm = "false"
# hsmLibPath = "/usr/local/lib/libgmt0018.so"
# hsmKeyIndex = "1"
# hsmPassword = "12345678"

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

## 4. Configuration of other formats

The Java SDK also supports configuration files in 'properties', 'yml', and 'xml' formats。

### properties format

#### Configuration Example

The meaning and default values of the fields are consistent with the 'toml' configuration file。

Create a file 'fisco-config.properties' in the home directory of the project, copy the following configuration content, and modify each configuration item according to the actual situation。

```properties
cryptoMaterial.certPath=conf                       # The certification path
cryptoMaterial.useSMCrypto=false

# cryptoMaterial.nableHsm=false
# cryptoMaterial.hsmLibPath=/usr/local/lib/libgmt0018.so
# cryptoMaterial.hsmKeyIndex=1
# cryptoMaterial.hsmPassword=12345678

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

#### Code example

Create a configuration class using the configuration loading method of SpringBoot:

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

Initialize 'BcosSDK' based on configuration class':

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

### yml format

#### YML Configuration Example

The meaning and default values of the fields are consistent with the 'toml' configuration file。

Create a file 'fisco-config.yml' in the home directory of the project, copy the following configuration content, and modify each configuration item according to the actual situation。

```yml
cryptoMaterial:
  useSMCrypto: false
  certPath: "conf"                   
#  caCert: "conf/ca.crt"               
#  sslCert: "conf/sdk.crt"             
#  sslKey: "conf/sdk.key"
#  enSslCert: "conf/sm_ensdk.crt"
#  enSslKey: "conf/sm_ensdk.key"

#  enableHsm: false
#  hsmLibPath: "/usr/local/lib/libgmt0018.so"
#  hsmKeyIndex: "1"
#  hsmPassword: "12345678"

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

#### yml configuration code example

Introduce the 'yml' file parsing tool:

```gradle
compile ("org.yaml:snakeyaml:1.27")
```

Initialize 'BcosSDK':

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

### xml format

#### xml configuration example

The meaning of each property is consistent with the 'toml' configuration file。

Create a file 'fisco-config.xml' in the home directory of the project, copy the following configuration content, and modify each configuration item according to the actual situation。

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

#### xml configuration code example

Initialize 'BcosSDK':

```java
    @SuppressWarnings("resource")
    ApplicationContext context =
            new ClassPathXmlApplicationContext("classpath:fisco-config.xml");
    BcosSDK bcosSDK = context.getBean(BcosSDK.class);
```
