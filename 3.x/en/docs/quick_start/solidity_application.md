# 3. Develop the first Solidity blockchain application

Tags: "Develop First Application" "Solidity" "Contract Development" "Blockchain Application" "EVM" "

---

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

This chapter will introduce the whole process of developing a business application scenario based on FISCO BCOS blockchain, from business scenario analysis to contract design and implementation, then contract compilation and how to deploy to the blockchain, and finally the implementation of an application module, through the [Java SDK] we provide.(../develop/sdk/java_sdk/index.md)Enables call access to contracts on the blockchain。

This tutorial requires users to be familiar with the Linux operating environment, have basic skills in Java development, be able to use the Gradle tool, and be familiar with [Solidity syntax](https://solidity.readthedocs.io/en/latest/)。

If you have not built a blockchain network or downloaded the console, please complete the tutorial [Building the First Blockchain Network](./air_installation.md)and back to this tutorial。

## 1. Understand application requirements

Blockchain naturally has tamper-proof, traceability and other characteristics, these characteristics determine that it is more likely to be favored by the financial sector.。In this example, a simple development example of asset management will be provided, and the following features will eventually be implemented:

- Ability to register assets on the blockchain
- Ability to transfer different accounts
- Can query the asset amount of the account

## Design and Development of Smart Contracts

When developing applications on the blockchain, combined with business requirements, it is first necessary to design the corresponding smart contract, determine the data that the contract needs to store, determine the interface provided by the smart contract on this basis, and finally give the specific implementation of each interface.。

### Step 1: Designing Smart Contracts

#### Storage Design

FISCO BCOS provides [contract KV storage interface](../develop/precompiled/use_kv_precompiled.md)Development mode, you can create a table through a contract and add, delete, modify, and query the created table。For this application, you need to design a table 't _ asset' that stores asset management. The table fields are as follows:

- account: Primary Key, Asset Account(string type)
- asset_value: Amount of assets(uint256 type)

where account is the primary key, which is the field that needs to be passed in when operating the 't _ asset' table, and the blockchain queries the matching records in the table based on the primary key field.。't _ asset 'means for example the following:

| account | asset_value |
|---------|-------------|
| Alice   | 10000       |
| Bob     | 20000       |

#### Interface Design

 According to the design objectives of the business, asset registration, transfer, and query functions need to be implemented. The interfaces for the corresponding functions are as follows:

```js
/ / Query the asset amount
function select(string memory account) public view returns(int256, uint256);
/ / Asset Registration
function register(string memory account, uint256 asset_value) public returns(int256);
/ / Asset transfer
function transfer(string memory from_account, string memory to_account, uint256 amount) public returns(int256);
```

### Step 2. Develop source code

According to our first step of storage and interface design, create an Asset smart contract that implements registration, transfer, and query functions, and introduce a system contract called KVTable, which provides a KV storage interface。

```shell
# Create working directory ~ / fisco
mkdir -p ~/fisco

# Download Console
cd ~/fisco && curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh && bash download_console.sh

# Switch to fisco / console / directory
cd ~/fisco/console/

# Enter the console / contracts directory
cd ~/fisco/console/contracts/solidity
# Create the Asset.sol contract file
vi Asset.sol

# Write the contents of the Asset.sol contract to。
# and type wq save exit。
```

The content of Asset.sol is as follows:

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

import "./Table.sol";

contract Asset {
    // event
    event RegisterEvent(
        int256 ret,
        string indexed account,
        uint256 indexed asset_value
    );
    event TransferEvent(
        int256 ret,
        string indexed from_account,
        string indexed to_account,
        uint256 indexed amount
    );

    KVTable kvTable;
    TableManager tm;
    string constant tableName = "t_asset";

    constructor() public {
        / / Create the t _ asset table in the constructor
        tm = TableManager(address(0x1002));

        / / asset management table, key: account, field : asset_value
        // |  Asset Account(primary key)      |     Amount of assets|
        // |-------------------- |-------------------|
        // |        account      |    asset_value    |
        // |---------------------|-------------------|
        //

        // create table
        tm.createKVTable(tableName, "account", "asset_value");

        // get table address
        address t_address = tm.openTable(tableName);
        kvTable = KVTable(t_address);
    }

    /*
    描述: Query asset amount based on asset account
    Parameters:
            account : Asset Account

    Return value:
            Parameter 1: successful return 0, account does not exist return-1
            Parameter 2: Valid when the first parameter is 0, asset amount
    */
    function select(string memory account) public view returns (bool, uint256) {
        / / Query
        bool result;
        string memory value;
        (result, value) = kvTable.get(account);
        uint256 asset_value = 0;

        asset_value = safeParseInt(value);
        return (result, asset_value);
    }

    /*
    描述: Asset Registration
    Parameters:
            account : Asset Account
            amount  : Amount of assets
    Return value:
            0 Asset registration successful
            -1 Asset account already exists
            -2 Other errors
    */
    function register(string memory account, uint256 asset_value)
    public
    returns (int256)
    {
        int256 ret_code = 0;
        bool ret = true;
        uint256 temp_asset_value = 0;
        / / query whether the account exists
        (ret, temp_asset_value) = select(account);
        if (ret != true) {
            / / does not exist, create
            string memory asset_value_str = uint2str(asset_value);

            / / Insert
            int32 count = kvTable.set(account, asset_value_str);
            if (count == 1) {
                / / Success
                ret_code = 0;
            } else {
                / / Failed? No permissions or other errors
                ret_code = - 2;
            }
        } else {
            / / Account already exists
            ret_code = - 1;
        }

        emit RegisterEvent(ret_code, account, asset_value);

        return ret_code;
    }

    /*
    描述: Asset Transfer
    Parameters:
            from_account : Transfer Asset Account
            to _ account: Receiving Asset Account
            amount: transfer amount
    Return value:
            0 Asset transfer successful
            -1 Transfer asset account does not exist
            -2 Receiving asset account does not exist
            -3 Insufficient amount
            -4 Amount overflow
            -5 Other errors
    */
    function transfer(
        string memory from_account,
        string memory to_account,
        uint256 amount
    ) public returns (int16) {
        / / Query the transferred asset account information
        bool ret = true;
        uint256 from_asset_value = 0;
        uint256 to_asset_value = 0;

        / / Whether the transfer account exists?
        (ret, from_asset_value) = select(from_account);
        if (ret != true) {
            / / Transfer account does not exist
            emit TransferEvent(- 1, from_account, to_account, amount);
            return - 1;
        }

        / / Whether the accepting account exists?
        (ret, to_asset_value) = select(to_account);
        if (ret != true) {
            / / The account receiving the asset does not exist
            emit TransferEvent(- 2, from_account, to_account, amount);
            return - 2;
        }

        if (from_asset_value < amount) {
            / / Insufficient account amount to transfer assets
            emit TransferEvent(- 3, from_account, to_account, amount);
            return - 3;
        }

        if (to_asset_value + amount < to_asset_value) {
            / / Receiving account amount overflow
            emit TransferEvent(- 4, from_account, to_account, amount);
            return - 4;
        }

        string memory f_new_value_str = uint2str(from_asset_value - amount);

        / / Update transfer account
        int32 count = kvTable.set(from_account, f_new_value_str);
        if (count != 1) {
            / / Failed? No permissions or other errors?
            emit TransferEvent(- 5, from_account, to_account, amount);
            return - 5;
        }

        string memory to_new_value_str = uint2str(to_asset_value + amount);

        / / Update receiving account
        kvTable.set(to_account, to_new_value_str);

        emit TransferEvent(0, from_account, to_account, amount);

        return 0;
    }

    function uint2str(uint256 _i)
    internal
    pure
    returns (string memory _uintAsString)
    {
        if (_i == 0) {
            return "0";
        }
        uint j = _i;
        uint len;
        while (j != 0) {
            len++;
            j /= 10;
        }
        bytes memory bstr = new bytes(len);
        uint k = len;
        while (_i != 0) {
            k = k-1;
            uint8 temp = (48 + uint8(_i - _i / 10 * 10));
            bytes1 b1 = bytes1(temp);
            bstr[k] = b1;
            _i /= 10;
        }
        return string(bstr);
    }

    function safeParseInt(string memory _a)
    internal
    pure
    returns (uint256 _parsedInt)
    {
        return safeParseInt(_a, 0);
    }

    function safeParseInt(string memory _a, uint256 _b)
    internal
    pure
    returns (uint256 _parsedInt)
    {
        bytes memory bresult = bytes(_a);
        uint256 mint = 0;
        bool decimals = false;
        for (uint256 i = 0; i < bresult.length; i++) {
            if (
                (uint256(uint8(bresult[i])) >= 48) &&
                (uint256(uint8(bresult[i])) <= 57)
            ) {
                if (decimals) {
                    if (_b == 0) break;
                    else _b--;
                }
                mint *= 10;
                mint += uint256(uint8(bresult[i])) - 48;
            } else if (uint256(uint8(bresult[i])) == 46) {
                require(
                    !decimals,
                    "More than one decimal encountered in string!"
                );
                decimals = true;
            } else {
                revert("Non-numeral character encountered in string!");
            }
        }
        if (_b > 0) {
            mint *= 10 ** _b;
        }
        return mint;
    }
}
```

The Table.sol referenced by Asset.sol is already in the "~ / fisco / console / contracts / consolidation" directory。The interface in the system contract file is implemented by the FISCO BCOS underlying layer.。When a business contract needs to operate a KV storage interface, the interface contract file needs to be introduced.。Table.sol contract detailed interface reference [here](../develop/precompiled/precompiled_contract_api.md)。

Run the "ls" command and make sure that "Asset.sol" and "Table.sol" are in the directory "~ / fisco / console / contracts / consolidation"。

## 3. Compile Smart Contracts

Smart contracts for '.sol' need to be compiled into ABI and BIN files to be deployed on the blockchain network。With these two files, you can deploy and invoke contracts with the Java SDK.。However, this call is relatively cumbersome and requires the user to pass parameters and parse the results based on the contract ABI.。To this end, the console provides a compilation tool that not only compiles ABI and BIN files, but also automatically generates a contract Java class with the same name as the compiled smart contract.。This Java class is generated based on the ABI to help users parse the parameters and provide methods with the same name.。When an application needs to deploy and invoke a contract, you can call the corresponding method of the contract class and pass in the specified parameters.。Using this contract Java class to develop applications can greatly simplify the user's code。

```shell
# Assuming that you have completed the download operation of the console, if not, please check the development source code steps in Section 2 of this article.
# Switch to fisco / console / directory
cd ~/fisco/console/

# Available via bash contract2java.sh solidity-H command to view the script solidity usage,-s specify sol file
bash contract2java.sh solidity -s contracts/solidity/Asset.sol -p org.fisco.bcos.asset.contract
```

After running successfully, the java, abi, and bin directories will be generated in the 'console / contracts / sdk' directory, as shown below。

```shell
# Omission of other irrelevant documents
|-- abi # The generated abi directory, which stores the abi files generated by the compilation of the solidity contract.
|   |-- Asset.abi
|-- bin # The generated bin directory, which stores the bin file generated by compiling the Solidity contract.
|   |-- Asset.bin
|-- java  # Store the compiled package path and Java contract file
|   |-- org
|        |--fisco
|             |--bcos
|                  |--asset
|                       |--contract
|                             |--Asset.java  # Java files generated by the Asset.sol contract
```

The 'org / fisco / bcos / asset / contract /' package path directory is generated in the java directory, which contains the 'Asset.java' file, which is the file required by the Java application to call the 'Asset.sol' contract.。

'Asset.java 'main interface:

```java
package org.fisco.bcos.asset.contract;

public class Asset extends Contract {
    / / Asset.sol contract transfer interface generation
    public TransactionReceipt transfer(String from_account, String to_account, BigInteger amount);
    / / Asset.sol contract register interface generation
    public TransactionReceipt register(String account, BigInteger asset_value);
    / / Asset.sol contract select interface generation
    public Tuple2<Boolean, BigInteger> select(String account) throws ContractException;

    / / Load the Asset contract address and generate an Asset object
    public static Asset load(String contractAddress, Client client, CryptoKeyPair credential);

    / / Deploy the Asset.sol contract and generate an Asset object
    public static Asset deploy(Client client, CryptoKeyPair credential) throws ContractException;
}
```

The load and deploy functions are used to construct the Asset object, and the other interfaces are used to call the corresponding solidity contract interfaces.。

## 4. Create a blockchain application project

### Step 1. Install the environment

First, we need to install the JDK and the integrated development environment

- Java: JDK 11 recommended (JDK1.8 to JDK 14 are supported)
  First, download and install JDK on the official website, and modify the JAVA _ HOME environment variable by yourself

- IDE：IntelliJ IDE

  Enter [IntelliJ IDE official website](https://www.jetbrains.com/idea/download/)to download and install the Community Edition IntelliJ IDE

### Step 2. Create a Java project

Create a gradle project in the IntelliJ IDE, select Gradle and Java, and enter the project name "asset-app-3.0``。

Note: (This step is not a required step) The source code for this project can be obtained and referenced in the following way.。

```bash
$ cd ~/fisco

$ curl -o asset-app-3.0-solidity.zip -#LO https://github.com/FISCO-BCOS/asset-app-demo/archive/refs/heads/main.zip
# Decompress to get Java project asset-app-3.0
$ unzip asset-app-3.0-solidity.zip && mv asset-app-demo-main asset-app-3.0
```

```eval_rst
.. note::
    - If you cannot download for a long time due to network problems, please try to append '185.199.108.133 raw.githubusercontent.com' to '/ etc / hosts', or try 'curl-o asset-app-3.0-solidity.zip -#LO https://gitee.com/FISCO-BCOS/asset-app-demo/repository/archive/main.zip`
```

### Step 3. Introducing the FISCO BCOS Java SDK

Modify the "build.gradle" file, "repositories" to set the maven source, introduce the Spring framework, and add a reference to the FISCO BCOS Java SDK under "dependencies" (note java-sdk version number)。

```groovy
repositories {
    mavenCentral()
    maven {
        url "http://maven.aliyun.com/nexus/content/groups/public/"
    }
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots" 
    }
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

in the "asset-app-3.0 / src / test / resources "Create the configuration file" applicationContext.xml "in the directory and write the configuration content。

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

**Note:** If rpc listen _ ip is set to 127.0.0.1 or 0.0.0.0 and listen _ port is set to 20200, the 'applicationContext.xml' configuration does not need to be modified.。If the blockchain node configuration is changed, you must also modify the 'peers' configuration option under the 'network' attribute of the configuration 'applicationContext.xml' to configure the 'listen _ ip' of the '[rpc]' configuration of the connected node.:listen_port`。

In the above configuration file, we specified the value of "certPath" for the bit where the certificate is stored as "conf"。Next, we need to put the certificate used by the SDK to connect to the node into the specified "conf" directory.。

```shell
# Suppose we take the asset-app-3.0 Put it in the ~ / fisco directory to enter the ~ / fisco directory
$ cd ~/fisco
# Create a folder to place the certificate(The default unzipped project exists)
$ mkdir -p asset-app-3.0/src/test/resources
# Create conf directory
$ mkdir asset-app-3.0/src/test/resources/conf
# Copy the node certificate to the project resource directory
$ cp -r nodes/127.0.0.1/sdk/* asset-app-3.0/src/test/resources/conf
# If you run the IDE directly, copy the certificate to the resources path.
$ mkdir -p asset-app-3.0/src/main/resources asset-app-3.0/src/main/resources/conf
$ cp -r nodes/127.0.0.1/sdk/* asset-app-3.0/src/main/resources/conf
```

## 5. Business Logic Development

We've covered how to introduce and configure the Java SDK in our own projects, and this section describes how to invoke contracts through Java programs, also with example asset management instructions.。

### Step 1. Introduce 3 compiled Java contracts into the project.

```shell
cd ~/fisco  
# Introducing compiled contract Java classes into your project。
cp console/contracts/sdk/java/org/fisco/bcos/asset/contract/Asset.java asset-app-3.0/src/main/java/org/fisco/bcos/asset/contract/Asset.java
```

### Step 2. Develop business logic

in path 'asset-app-3.0 / src / main / java / org / fisco / bcos / asset / client 'directory, create the' AssetClient.java 'class, and deploy and invoke the contract by calling' Asset.java '

The 'AssetClient.java' code is as follows:

```java
package org.fisco.bcos.asset.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.fisco.bcos.asset.contract.Asset;
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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
        logger.debug("create client for group, account address is {}", cryptoKeyPair.getAddress());
    }

    public void deployAssetAndRecordAddr() {

        try {
            Asset asset = Asset.deploy(client, cryptoKeyPair);
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
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
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
        logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
        return contractAddress;
    }

    public void queryAssetAmount(String assetAccount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            Tuple2<Boolean, BigInteger> result = asset.select(assetAccount);
            if (result.getValue1()) {
                System.out.printf(" asset account %s, value %s %n", assetAccount, result.getValue2());
            } else {
                System.out.printf(" %s asset account is not exist %n", assetAccount);
            }
        } catch (Exception e) {
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
                if (registerEventEvents.get(0).ret.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " register asset account success => asset: %s, value: %s %n", assetAccount, amount);
                } else {
                    System.out.printf(
                            " register asset account failed, ret code is %s %n", registerEventEvents.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, receipt status is: " + receipt.getStatus());
            }
        } catch (Exception e) {
            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s%n", e.getMessage());
        }
    }

    public void transferAsset(String fromAssetAccount, String toAssetAccount, BigInteger amount) {
        try {
            String contractAddress = loadAssetAddr();
            Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
            List<Asset.TransferEventEventResponse> transferEventEvents = asset.getTransferEventEvents(receipt);
            if (!transferEventEvents.isEmpty()) {
                if (transferEventEvents.get(0).ret.compareTo(BigInteger.ZERO) == 0) {
                    System.out.printf(
                            " transfer success => from_asset: %s, to_asset: %s, amount: %s %n",
                            fromAssetAccount, toAssetAccount, amount);
                } else {
                    System.out.printf(
                            " transfer asset account failed, ret code is %s %n", transferEventEvents.get(0).ret.toString());
                }
            } else {
                System.out.println(" event log not found, maybe transaction not exec, status is: " + receipt.getStatus());
            }
        } catch (Exception e) {

            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s%n", e.getMessage());
        }
    }

    public static void Usage() {
        System.out.println(" Usage:");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient deploy");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient query account");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient register account value");
        System.out.println(
                "\t java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.asset.client.AssetClient transfer from_account to_account amount");
        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            Usage();
        }

        AssetClient client = new AssetClient();
        client.initialize();

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
Asset asset = Asset.deploy(client, cryptoKeyPair);
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

in the "asset-app-3.0 / tool "Add a script to call AssetClient" asset _ run.sh "。

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

    java -Djdk.tls.namedGroups="secp256k1" -cp 'apps/*:conf/:lib/*' org.fisco.bcos.asset.client.AssetClient $@
```

Next, configure the log。in the "asset-app-3.0 / src / test / resources "directory to create" log4j.properties "

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

Next, specify the replication and compilation tasks by configuring the Jar command in gradle。And introduce the log library, in the "asset-app-3.0 / src / test / resources "directory, create an empty" contract.properties "file for the application to store the contract address at runtime。

So far, we have completed the development of this application。Finally, we get the asset-app-The directory structure for 3.0 is as follows:

```shell
|-- build.gradle / / gradle Configuration File
|-- gradle
|   |-- wrapper
|       |-- gradle-Wrapper.jar / / is used to download the relevant code implementation of Gradle.
|       |-- gradle-The configuration information used by wrapper.properties / / wrapper, such as the version of gradle.
|-- gradlew / / Shell script for executing the wrapper command under Linux or Unix
|-- gradlew.bat / / Batch script for executing the wrapper command under Windows
├── LICENSE
├── README.md
|-- src
|   |-- main
|   |   |-- java
|   |   |     |-- org
|   |   |          |-- fisco
|   |   |                |-- bcos
|   |   |                      |-- asset
|   |   |                            |-- client / / Place the client call class
|   |   |                                   |-- AssetClient.java
|   |   |                            |-- contract / / Place Java contract classes
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
|   |        |-- contract / / Store the solidity contract file
|   |                |-- Asset.sol
|   |                |-- Table.sol
|   |-- test
|   |    |-- resources / / stores the code resource file
|   |       |-- conf
|   |               |-- ca.crt
|   |               |-- cert.cnf
|   |               |-- sdk.crt
|   |               |-- sdk.key
|   |               |-- sdk.nodeid
|   |       |-- applicationContext.xml / / project configuration file
|   |       |-- contract.properties / / File that stores the deployment contract address
|   |       |-- log4j.properties / / log configuration file
|   |       |-- contract / / Store the solidity contract file
|   |               |-- Asset.sol
|   |               |-- KVTable.sol
|
|-- tool
    |-- asset _ run.sh / / project run script
```

## 6. Run the application

So far, we have introduced all the processes and functions of developing asset management applications using blockchain, and then we can run the project to test whether the functions are normal.。

- Compile

```shell
# Switch to project directory
$ cd ~/fisco/asset-app-3.0
# Compile Project
$ ./gradlew build
```

After successful compilation, the 'dist' directory will be generated in the project root directory。There is an 'asset _ run.sh' script in the dist directory to simplify project running。Now start to verify the requirements set at the beginning of this article.。

- Deploying the 'Asset.sol' Contract

```shell
# Enter dist directory
$ cd dist
$ bash asset_run.sh deploy
deploy Asset success, contract address is 0xc8ead4b26b2c6ac14c9fd90d9684c9bc2cc40085
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
 transfer success => from_asset: Alice, to_asset: Bob, amount: 50000
$ bash asset_run.sh query Alice
account Alice, value 50000
$ bash asset_run.sh query Bob
asset account Bob, value 150000
```

**To summarize:** So far, we have built a solid application based on FISCO BCOS consortium blockchain through contract development, contract compilation, SDK configuration and business development.。
