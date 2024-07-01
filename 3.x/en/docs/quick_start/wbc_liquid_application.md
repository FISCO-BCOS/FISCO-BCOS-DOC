# Development of the first WBC-Liquid Blockchain Applications

Tags: "Develop first app" "WBC-Liquid "" Contract Development "" Blockchain Application "" WASM ""

---

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

This chapter will introduce the whole process of developing a business application scenario based on FISCO BCOS blockchain, from business scenario analysis to contract design and implementation, then contract compilation and how to deploy to the blockchain, and finally the implementation of an application module, through the [Java SDK] we provide.(../develop/sdk/java_sdk/index.md)Enables call access to contracts on the blockchain。

This tutorial requires users to be familiar with the Linux operating environment, have basic skills in Java development, be able to use Gradle tools, and be familiar with webankblockchain.-liquid syntax (hereinafter referred to as WBC-Liquid), and a [WBC-Liquid's environment configuration](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/quickstart/prerequisite.html)。

Developing the WBC-Liquid application, need to build**WASM**Configure the blockchain network。The steps are as follows:
1. If you have not built a blockchain network or downloaded the console, please complete the tutorial [Building the First Blockchain Network](./air_installation.md)and back to this tutorial。(Ignore this step if built)；
2. Enable the node wasm configuration item: modify the '[executor]' of the node creation block configuration file 'config.genesis' to 'is _ wasm = true'；
3. Delete data and restart the node；
```shell
# Directory to node
cd ~/fisco/nodes/127.0.0.1
# Stop Node
bash stop_all.sh
# Delete old node data
rm -rf node*/data
# Restart node
bash start_all.sh
```
At this point, the blockchain network has opened the WASM configuration。

## 1. Understand application requirements

Blockchain naturally has tamper-proof, traceability and other characteristics, these characteristics determine that it is more likely to be favored by the financial sector.。In this example, a simple development example of asset management will be provided, and the following features will eventually be implemented:

- Ability to register assets on the blockchain
- Ability to transfer different accounts
- Can query the asset amount of the account

## Design and Development of Smart Contracts

When developing applications on the blockchain, combined with business requirements, it is first necessary to design the corresponding smart contract, determine the data that the contract needs to store, determine the interface provided by the smart contract on this basis, and finally give the specific implementation of each interface.。

### Step 1: Designing Smart Contracts

#### Storage Design

For this application, you need to design a table for storing asset management. The table fields are as follows:

- account: Primary Key, Asset Account(string type)
- asset_value: Amount of assets(uint256 type)

where account is the primary key, which is the field that needs to be passed in when operating the table, and the blockchain queries the matching records in the table based on the primary key field.。Storage representation for example below:

| account | asset_value |
|---------|-------------|
| Alice   | 10000       |
| Bob     | 20000       |

In this example, the mapping that comes with rust is used as a storage field for recording。

#### Interface Design

 According to the design objectives of the business, asset registration, transfer, and query functions need to be implemented. The interfaces for the corresponding functions are as follows:

```solidity
/ / Query the asset amount
pub fn select(&mut self, account: String) -> (bool, u128)
/ / Asset Registration
pub fn register(&mut self, account: String, asset_value: u128) -> i16
/ / Asset transfer
pub fn transfer(&mut self, from: String, to: String, value: u128) -> i16
```

### Step 2. Develop source code

#### Create

Create an Asset smart contract project based on our first step of storage and interface design。
Execute the following command in the terminal to create the WBC-liquid smart contract project

**Special attention:** For the convenience of users, the console has prepared the 'asset' example under the 'console / contracts / liquid' path. The following process is to create a new WBC-Liquid Contract Process。

```shell
# Create working directory ~ / fisco
mkdir -p ~/fisco

# Download Console
cd ~/fisco && curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh && bash download_console.sh

# Switch to fisco / console / directory
cd ~/fisco/console/

# Enter the console / contracts directory
cd ~/fisco/console/contracts/liquid

# Create a new contract(The console has prepared the asset directory. If you want to download the console, skip this step.)
cargo liquid new contract asset
```

The file structure in the asset directory is as follows:

```shell
asset/
├── .gitignore
├── .liquid
│   └── abi_gen
│       ├── Cargo.toml
│       └── main.rs
├── Cargo.toml
└── src
│   └──lib.rs
```

The function of each file is as follows:

- '.gitignore ': hidden file to tell version management software [Git](https://git-scm.com/)Which files or directories do not need to be added to version management。WBC-By default, Liquid excludes some unimportant issues (such as temporary files generated during compilation) from version management. If you do not need to use Git management to manage project versions, you can ignore this file.；

- '.liquid / ': hidden directory, used to implement WBC-The 'abi _ gen' subdirectory contains the implementation of the ABI generator. The compilation configuration and code logic in this directory are fixed.；

- 'Cargo.toml ': project configuration list, mainly including project information, external library dependencies, compilation configuration, etc. Generally, there is no need to modify the file, unless there are special requirements (such as referencing additional third-party libraries, adjusting optimization levels, etc.)；

- `src/lib.rs`：WBC-Liquid smart contract project root file, where the contract code is stored。After the smart contract project is created, the 'lib.rs' file is automatically populated with some template code, which we can use for further development.。

Once we have copied the code from Asset liquid into the 'lib.rs' file, we can proceed to the next steps。

The Asset liquid content is as follows:

```rust
#![cfg_attr(not(feature = "std"), no_std)]

use liquid::storage;
use liquid_lang as liquid;
use liquid_prelude::string::ToString;

#[liquid::interface(name = auto)]
mod table_manager {

    extern "liquid" {
        fn createKVTable(
            &mut self,
            table_name: String,
            key: String,
            value_fields: String,
        ) -> i32;
    }
}
#[liquid::interface(name = auto)]
mod kv_table {

    extern "liquid" {
        fn get(&self, key: String) -> (bool, String);
        fn set(&mut self, key: String, value: String) -> i32;
    }
}

#[liquid::contract]
mod asset {
    use super::{kv_table::*, table_manager::*, *};

    #[liquid(event)]
    struct RegisterEvent {
        ret_code: i16,
        #[liquid(indexed)]
        account: String,
        #[liquid(indexed)]
        asset_value: u128,
    }

    #[liquid(event)]
    struct TransferEvent {
        ret_code: i16,
        #[liquid(indexed)]
        from: String,
        #[liquid(indexed)]
        to: String,
        value: u128,
    }

    #[liquid(storage)]
    struct Asset {
        table: storage::Value<KvTable>,
        tm: storage::Value<TableManager>,
        table_name: storage::Value<String>,
    }

    #[liquid(methods)]
    impl Asset {
        pub fn new(&mut self) {
            self.table_name.initialize(String::from("t_asset"));
            self.tm
                .initialize(TableManager::at("/sys/table_manager".parse().unwrap()));
            self.tm.createKVTable(
                self.table_name.clone(),
                String::from("account"),
                String::from("asset_value"),
            );
            self.table
                .initialize(KvTable::at("/tables/t_asset".parse().unwrap()));
        }

        pub fn select(&self, account: String) -> (bool, u128) {
            if let Some((result, value)) = (*self.table).get(account) {
                if value.len() == 0 {
                    return (false, Default::default());
                }
                return (result, u128::from_str_radix(&value, 10).ok().unwrap());
            }
            return (false, Default::default());
        }

        pub fn register(&mut self, account: String, asset_value: u128) -> i16 {
            let ret_code: i16;
            let (ok, _) = self.select(account.clone());
            if ok == false {
                let result = (*self.table)
                    .set(account.clone(), asset_value.to_string())
                    .unwrap();

                if result == 1.into() {
                    ret_code = 0;
                } else {
                    ret_code = -2;
                }
            } else {
                ret_code = -1;
            }
            let ret = ret_code.clone();
            self.env().emit(RegisterEvent {
                ret_code,
                account,
                asset_value,
            });
            return ret;
        }

        pub fn transfer(&mut self, from: String, to: String, value: u128) -> i16 {
            let mut ret_code: i16 = 0;
            let (ok, from_value) = self.select(from.clone());
            if ok != true.into() {
                ret_code = -1;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            let (ret, to_value) = self.select(to.clone());
            if ret != true {
                ret_code = -2;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            if from_value < value.clone() {
                ret_code = -3;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            if to_value.clone() + value.clone() < to_value.clone() {
                ret_code = -4;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            let from_u = self.update(from.clone(), from_value - value.clone());
            if from_u != 1 {
                ret_code = -5;
                self.env().emit(TransferEvent {
                    ret_code,
                    from,
                    to,
                    value,
                });
                return ret_code;
            }

            let to_u = self.update(to.clone(), to_value.clone() + value.clone());
            if to_u != 1 {
                ret_code = -6;
            }
            self.env().emit(TransferEvent {
                ret_code,
                from,
                to,
                value,
            });
            return ret_code;
        }

        pub fn update(&mut self, account: String, value: u128) -> i16 {
            let r = (*self.table).set(account, value.to_string()).unwrap();
            if r == 1.into() {
                return 1;
            }
            return -1;
        }
    }
}
```

#### Build

Run the following command in the asset project root directory to start the build:

```shell
# Compile the secret version of the wasm binary file.
cargo liquid build -g
```

This command directs the Rust language compiler to 'wasm32-unknown-'Unknown 'compiles the smart contract code for the target, and finally generates the Wasm format bytecode and ABI.。`-g 'Build smart contracts that can run on the underlying platform of the State Secret FISCO BCOS blockchain。After the command is executed, the following content is displayed:

```shell
[1/4] 🔍  Collecting crate metadata
[2/4] 🚚  Building cargo project
[3/4] 🔗  Optimizing Wasm bytecode
[4/4] 📃  Generating ABI file

✨ Done in 30 seconds, your project is ready now:
Binary: ~/fisco/console/contracts/liquid/asset/target/asset_gm.wasm
   ABI: ~/fisco/console/contracts/liquid/asset/target/asset.abi
```

Among them, "Binary:"followed by the absolute path of the generated bytecode file," ABI:after the absolute path for the generated ABI file。To simplify the adaptation of FISCO BCOS SDKs in various languages, WBC-Liquid uses an ABI format compatible with the Solidity ABI specification.

Then generate the non-secret Binary, ABI file:

```shell
cargo liquid build
```

Note: Without '-g`。
After executing the command, the generated information is the same as the above, enter 'target', see the new Binary, ABI, and just 'asset _ gm.wasm'

## 3. Compile Smart Contracts

The "Liquid" smart contract needs to be compiled into ABI and WASM files before it can be deployed to the blockchain network. With these two files, you can deploy and call the contract with the Java SDK.。For details about how to build and compile the Liquid project environment, see: [Deploying the Liquid Compilation Environment](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/quickstart/prerequisite.html) [Liquid Development Guide](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/dev_testing/development.html)。

The Java generation tool provided by the console can compile the ABI and WASM files from the 'cargo liquid build' and automatically generate a contract Java class with the same name as the compiled smart contract.。This Java class is generated based on the ABI to help users parse the parameters and provide methods with the same name.。When an application needs to deploy and invoke a contract, you can call the corresponding method of the contract class and pass in the specified parameters.。Using this contract Java class to develop applications can greatly simplify the user's code。We use the console console script 'contract2java.sh' to generate the Java file。

```shell
# Assuming that you have completed the download operation of the console, if not, please check the development source code steps in Section 2 of this article.

# Switch to fisco / console / directory
cd ~/fisco/console/

# Compile Contract(Specify the path of the BINARY and abi files. You can specify the path based on the actual project path.)As follows:
bash contract2java.sh liquid -a ~/fisco/console/contracts/liquid/asset/target/asset.abi -b ~/fisco/console/contracts/liquid/asset/target/asset.wasm -s ~/fisco/console/contracts/liquid/asset/target/asset_gm.wasm -p org.fisco.bcos.asset.liquid.contract

# Script Usage:
$ bash contract2java.sh liquid -h
Missing required options: b, a, s
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -a,--abi <arg>       [Required] The ABI file path of WBC-Liquid contract.
 -b,--bin <arg>       [Required] The binary file path of WBC-Liquid contract.
 -h,--help
 -o,--output <arg>    [Optional] The file path of the generated java code,
                      default is contracts/sdk/java/
 -p,--package <arg>   [Optional] The package name of the generated java
                      code, default is com
 -s,--sm-bin <arg>   [Required] The SM binary file path of WBC-Liquid
                      contract.
```

After successful running, the Asset.java file will be generated in the 'console / contracts / sdk / java / com' directory。

'Asset.java 'main interface:

```java
package org.fisco.bcos.asset.contract;

public class Asset extends Contract {
    / / Asset contract transfer interface generation
    public TransactionReceipt transfer(String from, String to, BigInteger value);
    / / Asset contract register interface generation
    public TransactionReceipt register(String account, BigInteger asset_value);
    / / Asset contract select interface generation
    public Tuple2<Boolean, BigInteger> select(String account) throws ContractException;

    / / Load the Asset contract address and generate an Asset object
    public static Asset load(String contractAddress, Client client, CryptoKeyPair credential);

    / / Deploy the Asset contract and generate the Asset object
    public static Asset deploy(Client client, CryptoKeyPair credential, String contractPath) throws ContractException;
}
```

The load and deploy functions are used to construct the Asset object, and the other interfaces are used to call the corresponding contract interfaces, respectively.。

## 4. Create a blockchain application project

### Step 1. Install the environment

First, we need to install the JDK and the integrated development environment

- Java: JDK 11 (supported from JDK 1.8 to JDK 14)

  First, download and install JDK11 on the official website, and modify the JAVA _ HOME environment variable by yourself

- IDE：IntelliJ IDE.

  Enter [IntelliJ IDE official website](https://www.jetbrains.com/idea/download/)to download and install the Community Edition IntelliJ IDE。

### Step 2. Create a Java project

Create a gradle project in the IntelliJ IDE, select Gradle and Java, and enter the project name "asset-app-liquid``。

Note: (This step is not a required step) The source code for this project can be obtained and referenced in the following way.。

```shell
$ cd ~/fisco

$ curl -o asset-app-3.0-liquid.zip -#LO https://github.com/FISCO-BCOS/asset-app-demo/archive/refs/heads/main-liquid.zip

# Decompress to get Java project asset-app-liquid
$ unzip asset-app-3.0-liquid.zip && mv asset-app-demo-main-liquid  asset-app-liquid
```

```eval_rst
.. note::
- If you cannot download for a long time due to network problems, please try to append '185.199.108.133 raw.githubusercontent.com' to '/ etc / hosts', or try 'curl-o asset-app-3.0-liquid.zip -#LO https://gitee.com/FISCO-BCOS/asset-app-demo/repository/archive/main-liquid.zip`
```

### Step 3. Introducing the FISCO BCOS Java SDK

Modify the "build.gradle" file, introduce the Spring framework, and add a reference to the FISCO BCOS Java SDK under "dependencies" (note java-sdk version number)。

```groovy
repositories {
    mavenCentral()
    maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
    maven { url "https://oss.sonatype.org/service/local/staging/deploy/maven2" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}

def spring_version = "5.3.25"
List spring = [
        "org.springframework:spring-core:$spring_version",
        "org.springframework:spring-beans:$spring_version",
        "org.springframework:spring-context:$spring_version",
        "org.springframework:spring-tx:$spring_version",
]

dependencies {
    compile logger
    runtime logger
    compile ("org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:3.3.0")
    compile spring
}
```

### Step 4. Configure the SDK certificate
in the "asset-app-create a configuration file "applicationContext.xml" in the liquid / src / test / resources directory and write the configuration content。

The contents of applicationContext.xml are as follows:

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
        <!-- SSL certificate configuration -->
        <!-- entry key="caCert" value="conf/ca.crt" /-->
        <!-- entry key="sslCert" value="conf/sdk.crt" /-->
        <!-- entry key="sslKey" value="conf/sdk.key" /-->
        <!-- GM SSL certificate configuration -->
        <!-- entry key="caCert" value="conf/gm/gmca.crt" /-->
        <!-- entry key="sslCert" value="conf/gm/gmsdk.crt" /-->
        <!-- entry key="sslKey" value="conf/gm/gmsdk.key" /-->
        <!--entry key="enSslCert" value="conf/gm/gmensdk.crt" /-->
        <!--entry key="enSslKey" value="conf/gm/gmensdk.key" /-->
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
    <!--
    <property name="amop">
      <list>
        <bean id="amopTopic1" class="org.fisco.bcos.sdk.v3.config.model.AmopTopic">
          <property name="topicName" value="PrivateTopic1" />
          <property name="password" value="" />
          <property name="privateKey" value="" />
          <property name="publicKeys">
            <list>
              <value>conf/amop/consumer_public_key_1.pem</value>
            </list>
          </property>
        </bean>
      </list>
    </property>
    -->
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

**Note:** The liquid contract. The node needs to be enabled.**wasm**Options。If rpc listen _ ip is set to 127.0.0.1 or 0.0.0.0 and listen _ port is set to 20200, the 'applicationContext.xml' configuration does not need to be modified.。If the blockchain node configuration is changed, you must also modify the 'peers' configuration option under the 'network' attribute of the configuration 'applicationContext.xml' to configure the 'listen _ ip' of the '[rpc]' configuration of the connected node.:listen_port`。

In the above configuration file, we specified the value of "certPath" for the bit where the certificate is stored as "conf"。Next, we need to put the certificate used by the SDK to connect to the node into the specified "conf" directory.。

```shell
# Suppose we take the asset-app-put liquid in the ~ / fisco directory to enter the ~ / fisco directory
$ cd ~/fisco
# Create a folder to place the certificate
$ mkdir -p asset-app-liquid/src/test/resources/conf
# Copy the node certificate to the project resource directory
$ cp -r nodes/127.0.0.1/sdk/* asset-app-liquid/src/test/resources/conf
# If you run the IDE directly, copy the certificate to the resources path.
$ mkdir -p asset-app-liquid/src/main/resources/conf
$ cp -r nodes/127.0.0.1/sdk/* asset-app-liquid/src/main/resources/conf
```

## 5. Business Logic Development

We've covered how to introduce and configure the Java SDK in our own projects, and this section describes how to invoke contracts through Java programs, also with example asset management instructions.。

### The first step is to introduce 3 compiled Java contracts into the project.

```shell
cd ~/fisco
# Introducing compiled contract Java classes into your project。
cp console/contracts/sdk/java/org/fisco/bcos/asset/liquid/contract/Asset.java asset-app-liquid/src/main/java/org/fisco/bcos/asset/liquid/contract/Asset.java
```

### The second step is to develop business logic.

Create the 'AssetClient.java' class in the '/ src / main / java / org / fisco / bcos / asset / liquid / client' directory to deploy and invoke the contract by calling 'Asset.java'

The 'AssetClient.java' code is as follows:

```java
package org.fisco.bcos.asset.liquid.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

import org.fisco.bcos.asset.liquid.contract.Asset;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple1;
import org.fisco.bcos.sdk.v3.codec.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class AssetClient {

    static Logger logger = LoggerFactory.getLogger(AssetClient.class);

    private BcosSDK bcosSDK;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    public void initialize() {
        @SuppressWarnings("resource")
        ApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        bcosSDK = context.getBean(BcosSDK.class);
        client = bcosSDK.getClient();
        cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        logger.debug("create client for group1, account address is {}", cryptoKeyPair.getAddress());
    }

    public void deployAssetAndRecordAddr() {

        try {
            String assetPath = "/asset" + new Random().nextInt(10000);
            Asset asset = Asset.deploy(client, cryptoKeyPair, assetPath);
            System.out.println(
                    " deploy Asset success, contract address is " + asset.getContractAddress());

            recordAssetAddr(asset.getContractAddress());
        } catch (Exception e) {
            System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
        }
    }

    public void recordAssetAddr(String address) throws IOException {
        Properties prop = new Properties();
        prop.setProperty("address", address);
        final Resource contractResource = new ClassPathResource("contract.properties");
        try (FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile())) {
            prop.store(fileOutputStream, "contract address");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String loadAssetAddr() throws Exception {
        // load Asset contact address from contract.properties
        Properties prop = new Properties();
        final Resource contractResource = new ClassPathResource("contract.properties");
        prop.load(contractResource.getInputStream());

        String contractAddress = prop.getProperty("address");
        if (contractAddress == null || contractAddress.trim().equals("")) {
            throw new Exception(" load Asset contract address failed, please deploy it first. ");
        }
        if (!contractAddress.startsWith("/")) {
            contractAddress = "/" + contractAddress;
        }
        logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    public void queryAssetAmount(String assetAccount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            Tuple2<Boolean, BigInteger> result = asset.getSelectOutput(asset.select(assetAccount));
            if (result.getValue1()) {
                System.out.printf(" asset account %s, value %s %n", assetAccount, result.getValue2());
            } else {
                System.out.printf(" %s asset account is not exist %n", assetAccount);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query asset account failed, error message is %s%n", e.getMessage());
        }
    }

    public void registerAssetAccount(String assetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();

            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.register(assetAccount, amount);
            List<Asset.RegisterEventEventResponse> registerEventEvents = asset.getRegisterEventEvents(receipt);
            if (!registerEventEvents.isEmpty()) {
                if (registerEventEvents.get(0).ret_code.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " register asset account success => asset: %s, value: %s %n", assetAccount, amount);
                } else {
                    System.out.printf(
                            " register asset account failed, ret code is %s %n", registerEventEvents.get(0).ret_code.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, receipt status is: " + receipt.getStatus());
            }
        } catch (Exception e) {
            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.println(" register asset account failed, error message is " + e.getMessage());
        }
    }

    public void transferAsset(String fromAssetAccount, String toAssetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
            List<Asset.TransferEventEventResponse> transferEventEvents = asset.getTransferEventEvents(receipt);
            if (!transferEventEvents.isEmpty()) {
                if (transferEventEvents.get(0).ret_code.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " transfer success => from_asset: %s, to_asset: %s, amount: %s %n",
                            fromAssetAccount, toAssetAccount, amount);
                } else {
                    System.out.printf(
                            " transfer asset account failed, ret code is %s %n", transferEventEvents.get(0).ret_code.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, status is: " + receipt.getStatus());
            }
        } catch (Exception e) {

            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.println(" register asset account failed, error message is " + e.getMessage());
        }
    }

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient deploy");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient query account");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient register account value");
        System.out.println(
                "\t java -cp conf/:lib/*:apps/* org.fisco.bcos.asset.liquid.client.AssetClient transfer from_account to_account amount");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            Usage();
        }

        AssetClient client = new AssetClient();
        client.initialize();
        try {

            switch (args[0]) {
                case "deploy":
                    client.deployAssetAndRecordAddr();
                    break;
                case "query":
                    if (args.length < 2) {
                        Usage();
                    }
                    client.queryAssetAmount(args[1]);
                    break;
                case "register":
                    if (args.length < 3) {
                        Usage();
                    }
                    client.registerAssetAccount(args[1], new BigInteger(args[2]));
                    break;
                case "transfer":
                    if (args.length < 4) {
                        Usage();
                    }
                    client.transferAsset(args[1], args[2], new BigInteger(args[3]));
                    break;
                default: {
                    Usage();
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        System.exit(0);
    }
}
```

Let's look at the call to the FISCO BCOS Java SDK using the AssetClient example:

- Initialization

The main function of the initialization code is to construct the Client and CryptoKeyPair objects, which are created in the corresponding contract class object.(Call the deploy or load function of the contract class)need to use。

```java
/ / Initialize in the initialize function
/ / Initialize the BcosSDK
@SuppressWarnings("resource")
ApplicationContext context =
    new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
bcosSDK = context.getBean(BcosSDK.class);
client = bcosSDK.getClient();
cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
logger.debug("create client for group, account address is " + cryptoKeyPair.getAddress());
```

- Constructing a Contract Class Object

The contract object can be initialized using the deploy or load function, which is used differently, the former for the initial deployment of the contract and the latter when the contract has been deployed and the contract address is known.。

```java
/ / Deployment contract
Asset asset = Asset.deploy(client, cryptoKeyPair, assetPath);
/ / Load contract address
Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
```

- interface invocation

Use the contract object to call the corresponding interface and process the returned result。

```java
/ / select interface call
Tuple2<Boolean, BigInteger> result = asset.select(assetAccount);
/ / call the register interface
TransactionReceipt receipt = asset.register(assetAccount, amount);
/ / transfer interface
TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
```

in the "asset-app-liquid / tool "directory to add a script that calls AssetClient" asset _ run.sh "。

```shell
#!/bin/bash

function usage()
{
    echo " Usage : "
    echo "   bash asset_run.sh deploy"
    echo "   bash asset_run.sh query    asset_account "
    echo "   bash asset_run.sh register asset_account asset_amount "
    echo "   bash asset_run.sh transfer from_asset_account to_asset_account amount "
    echo " "
    echo " "
    echo "examples : "
    echo "   bash asset_run.sh deploy "
    echo "   bash asset_run.sh register  Asset0  10000000 "
    echo "   bash asset_run.sh register  Asset1  10000000 "
    echo "   bash asset_run.sh transfer  Asset0  Asset1 11111 "
    echo "   bash asset_run.sh query Asset0"
    echo "   bash asset_run.sh query Asset1"
    exit 0
}

    case $1 in
    deploy)
            [ $# -lt 1 ] && { usage; }
            ;;
    register)
            [ $# -lt 3 ] && { usage; }
            ;;
    transfer)
            [ $# -lt 4 ] && { usage; }
            ;;
    query)
            [ $# -lt 2 ] && { usage; }
            ;;
    *)
        usage
            ;;
    esac

    java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:conf/:lib/*' org.fisco.bcos.asset.liquid.client.AssetClient $@
```

Next, configure the log。in the "asset-app-liquid / src / test / resources "Create" log4j.properties "

```properties
### set log levels ###
log4j.rootLogger=DEBUG, file

### output the log information to the file ###
log4j.appender.file=org.apache.log4j.DailyRollingFileAppender
log4j.appender.file.DatePattern='_'yyyyMMddHH'.log'
log4j.appender.file.File=./log/sdk.log
log4j.appender.file.Append=true
log4j.appender.file.filter.traceFilter=org.apache.log4j.varia.LevelRangeFilter
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%p] [%-d{yyyy-MM-dd HH:mm:ss}] %C{1}.%M(%L) | %m%n

###output the log information to the console ###
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=[%p] [%-d{yyyy-MM-dd HH:mm:ss}] %C{1}.%M(%L) | %m%n
```

Next, specify the replication and compilation tasks by configuring the Jar command in gradle。And introduce the log library, in the "asset-app-create an empty "contract.properties" file in the "liquid / src / test / resources" directory to store the contract address at runtime.。

So far, we have completed the development of this application。Finally, we get the asset-app-The directory structure of liquid is as follows:

```shell
|-- build.gradle / / gradle Configuration File
|-- gradle
|   |-- wrapper
|       |-- gradle-Wrapper.jar / / is used to download the relevant code implementation of Gradle.
|       |-- gradle-The configuration information used by wrapper.properties / / wrapper, such as the version of gradle.
|-- gradlew / / Shell script for executing the wrapper command under Linux or Unix
|-- gradlew.bat / / Batch script for executing the wrapper command under Windows
|-- src
|   |-- main
|   |   |-- java
|   |   |     |-- org
|   |   |          |-- fisco
|   |   |                |-- bcos
|   |   |                      |-- asset
|   |   |                           |-- liquid
|   |   |                               |-- client / / Place the client call class
|   |   |                                   |-- AssetClient.java
|   |   |                               |-- contract / / Place Java contract classes
|   |   |                                   |-- Asset.java
|   |   |-- resources
|   |        |-- conf
|   |               |-- ca.crt
|   |               |-- cert.cnf
|   |               |-- sdk.crt
|   |               |-- sdk.key
|   |               |-- sdk.nodeid
|   |        |-- applicationContext.xml / / project configuration file
|   |        |-- contract.properties / / File that stores the deployment contract address
|   |        |-- log4j.properties / / log configuration file
|   |        |-- contract / / Store WBC-Liquid Contract Files
|   |               |-- asset
|   |                   |-- src
|   |                       |-- lib.rs WBC-Liquid File
|   |-- test
|       |-- resources / / stores the code resource file
|           |-- conf
|                   |-- ca.crt
|                   |-- cert.cnf
|                   |-- sdk.crt
|                   |-- sdk.key
|                   |-- sdk.nodeid
|           |-- applicationContext.xml / / project configuration file
|           |-- contract.properties / / File that stores the deployment contract address
|           |-- log4j.properties / / log configuration file
|           |-- contract / / Store WBC-Liquid Contract Files
|                   |-- asset
|                       |-- src
|                           |-- lib.rs WBC-Liquid File
|
|-- tool
    |-- asset _ run.sh / / project run script
```

## 6. Run the application

So far, we have introduced all the processes and functions of developing asset management applications using blockchain, and then we can run the project to test whether the functions are normal.。

- Compile

```shell
# Switch to project directory
$ cd ~/fisco/asset-app-liquid
# Compile Project
$ ./gradlew build
```

After successful compilation, the 'dist' directory will be generated in the project root directory。There is an 'asset _ run.sh' script in the dist directory to simplify project running。Now start to verify the requirements set at the beginning of this article.。

- Deploy the 'Asset.liquid' contract

```shell
# Enter dist directory
$ cd dist
$ bash asset_run.sh deploy
deploy Asset success, contract address is /asset/liquid180
```

- Registered Assets

```shell
$ bash asset_run.sh register Alice 100000
register asset account success => asset: Alice, value: 100000
$ bash asset_run.sh register Bob 100000
register asset account success => asset: Bob, value: 100000
```

- Query Assets

```shell
$ bash asset_run.sh query Alice
asset account Alice, value 100000
$ bash asset_run.sh query Bob
asset account Bob, value 100000
```

- Asset Transfer

```shell
$ bash asset_run.sh transfer Alice Bob  50000
Transfer success => from_account: Alice, to_account: Bob, amount: 50000
$ bash asset_run.sh query Alice
account Alice, value 50000
$ bash asset_run.sh query Bob
asset account Bob,, value 150000
```

**To summarize:** At this point, we passed the WBC-Liquid contract development, contract compilation, SDK configuration and business development build a WBC based on FISCO BCOS consortium blockchain.-Liquid Applications。
