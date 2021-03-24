# Build the first blockchain application


This chapter will introduce a whole process of business application scenario development based on FISCO BCOS blockchain. The introduce includes business scenario analysis, contract design implementation, contract compilation, and blockchain development. Finally, we introduce an application module implementation which is to implement calling access to the contract on blockchain through the Java SDK we provide.

This tutorial requires user to be familiar with the Linux operating environment, has the basic skills of Java development, is able to use the Gradle tool, and is familiar with [Solidity syntax](https://solidity.readthedocs.io/en/latest/).

**Through the tutorial, you will learn the following:**

1. How to express the logic of a business scenario in the form of a contract
2. How to convert Solidity contract into Java class
3. How to configure Java SDK
4. How to build an application and integrate Java SDK into application engineering
5. How to call the contract interface through Java SDK, and to understand its principle

The full project source code for the sample is provided in the tutorial and users can quickly develop their own applications based on it.

```eval_rst
.. important::
    Please refer to `Installation documentation <../installation.html>`_ to complete the construction of the FISCO BCOS blockchain and the download of the console. The operation in this tutorial is assumed to be carried out in the environment of the documentation building.

```

## Sample application requirements

Blockchain is naturally tamper-proof and traceable. These characteristics make it more attractive to the financial sector. This article will provide an easy example of asset management development and ultimately achieve the following functions:

- Ability to register assets on blockchain
- Ability to transfer funds from different accounts
- Ability to check the amount of assets in the account

## Contract design and implementation

When developing an application on blockchain, for combining with business requirements, it is first necessary to design the corresponding smart contract to determine the storage data that contract needs, and on this basis, to determine the interface provided by the smart contract. Finally, to specifically implement each interface.

### Storage design

FISCO BCOS provides a [contract CRUD interface](../manual/smart_contract.html#crud) development model, which can create table through contracts, and add, delete, and modify the created table. For this application, we need to design a table `t_asset` for storage asset management. The table's fields are as follows:

-   account: primary key, asset account (string type)
-   asset_value: asset amount (uint256 type)

account is the primary key, which is the field that needs to be passed when the `t_asset` table is operated. The blockchain queries the matching records in the table according to the primary key field. The example of `t_asset` table is as follow:

| account | asset_value |
| ------- | ----------- |
| Alice   | 10000       |
| Bob     | 20000       |

### Interface design

According to the design goals of the business, it is necessary to implement asset registration, transfer, and query functions. The interfaces of the corresponding functions are as follows:

```js
// query the amount of assets
function select(string account) public constant returns(int256, uint256)
// asset registration
function register(string account, uint256 amount) public returns(int256)
// asset transfer
function transfer(string from_asset_account, string to_asset_account, uint256 amount) public returns(int256)
```

### Full source

```js
pragma solidity ^0.4.24;

import "./Table.sol";

contract Asset {
    // event
    event RegisterEvent(int256 ret, string account, uint256 asset_value);
    event TransferEvent(int256 ret, string from_account, string to_account, uint256 amount);

    constructor() public {
        // create a t_asset table in the constructor
        createTable();
    }

    function createTable() private {
        TableFactory tf = TableFactory(0x1001);
        // asset management table, key : account, field : asset_value
        // |  account(primary key)   |  amount       |
        // |-------------------- |-------------------|
        // |        account      |    asset_value    |
        // |---------------------|-------------------|
        //
        // create table
        tf.createTable("t_asset", "account", "asset_value");
    }

    function openTable() private returns(Table) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_asset");
        return table;
    }

    /*
    description: query asset amount according to asset account

    parameter:
            account: asset account

    return value：
            parameter1： successfully returns 0, the account does not exist and returns -1
            parameter2： valid when the first parameter is 0, the amount of assets
    */
    function select(string account) public constant returns(int256, uint256) {
        // open table
        Table table = openTable();
        // query
        Entries entries = table.select(account, table.newCondition());
        uint256 asset_value = 0;
        if (0 == uint256(entries.size())) {
            return (-1, asset_value);
        } else {
            Entry entry = entries.get(0);
            return (0, uint256(entry.getInt("asset_value")));
        }
    }

    /*
    description : asset registration
    parameter ：
            account : asset account
            amount  : asset amount
    return value：
            0  regist successfully
            -1 asset account already exists
            -2 other error
    */
    function register(string account, uint256 asset_value) public returns(int256){
        int256 ret_code = 0;
        int256 ret= 0;
        uint256 temp_asset_value = 0;
        // to query whather the account exists
        (ret, temp_asset_value) = select(account);
        if(ret != 0) {
            Table table = openTable();

            Entry entry = table.newEntry();
            entry.set("account", account);
            entry.set("asset_value", int256(asset_value));
            // insert
            int count = table.insert(account, entry);
            if (count == 1) {
                // true
                ret_code = 0;
            } else {
                // false. no permission or other error
                ret_code = -2;
            }
        } else {
            // account already exists
            ret_code = -1;
        }

        emit RegisterEvent(ret_code, account, asset_value);

        return ret_code;
    }

    /*
    description : asset transfer
    parameter ：
            from_account : transferred asset account
            to_account ：received asset account
            amount ： transferred amount
    return value：
            0  transfer asset successfully
            -1 transfe asset account does not exist
            -2 receive asset account does not exist
            -3 amount is insufficient
            -4 amount is excessive
            -5 other error
    */
    function transfer(string from_account, string to_account, uint256 amount) public returns(int256) {
        // query transferred asset account information
        int ret_code = 0;
        int256 ret = 0;
        uint256 from_asset_value = 0;
        uint256 to_asset_value = 0;

        // whather transferred asset account exists?
        (ret, from_asset_value) = select(from_account);
        if(ret != 0) {
            ret_code = -1;
            // not exist
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;

        }

        // whather received asset account exists?
        (ret, to_asset_value) = select(to_account);
        if(ret != 0) {
            ret_code = -2;
            // not exist
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        if(from_asset_value < amount) {
            ret_code = -3;
            // amount of transferred asset account is insufficient
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        if (to_asset_value + amount < to_asset_value) {
            ret_code = -4;
            // amount of received asset account is excessive
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        Table table = openTable();

        Entry entry0 = table.newEntry();
        entry0.set("account", from_account);
        entry0.set("asset_value", int256(from_asset_value - amount));
        // update transferred account
        int count = table.update(from_account, entry0, table.newCondition());
        if(count != 1) {
            ret_code = -5;
            // false? no permission or other error?
            emit TransferEvent(ret_code, from_account, to_account, amount);
            return ret_code;
        }

        Entry entry1 = table.newEntry();
        entry1.set("account", to_account);
        entry1.set("asset_value", int256(to_asset_value + amount));
        // update received account
        table.update(to_account, entry1, table.newCondition());

        emit TransferEvent(ret_code, from_account, to_account, amount);

        return ret_code;
    }
}
```


**Note:**  The implementation of the `Asset.sol` contract requires to introduce a system contract interface file `Table.sol` provided by FISCO BCOS. The system contract file’s interface is implemented by the underlying FISCO BCOS. When a business contract needs to operate CRUD interface, it is necessary to introduce the interface contract file. `Table.sol` contract detailed interface [reference here](../manual/smart_contract.html#crud).


## Contract compiling

In the previous section, we designed the storage and interface of the contract `Asset.sol` according to business requirements, and implemented them completely. However, Java program cannot directly call Solidity contract. The Solidity contract file needs to be compiled into a Java file first.

The console provides a compilation tool that stores the `Asset.sol` contract file in the `console/contract/solidity` directory. Compile with the `sol2java.sh` script provided in the console directory, as follows:
```bash
$ mkdir -p ~/fisco
# download console
$ cd ~/fisco && curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.7.2/download_console.sh && bash download_console.sh
# switch to the fisco/console/ directory
$ cd ~/fisco/console/
# compile the contract, specify a Java package name parameter later, you can specify the package name according to the actual project path.
$ ./sol2java.sh org.fisco.bcos.asset.contract
```

After successful operation, the java, abi, and bin directories will be generated in the `console/contracts/sdk directory` as shown below.

```bash
|-- abi # The generated abi directory, which stores the abi file generated by Solidity contract compilation.
|   |-- Asset.abi
|   |-- Table.abi
|-- bin # The generated bin directory, which stores the bin file generated by Solidity contract compilation.
|   |-- Asset.bin
|   |-- Table.bin
|-- contracts # The source code file that stores Solidity contract. Copy the contract that needs to be compiled to this directory.
|   |-- Asset.sol # A copied Asset.sol contract, depends on Table.sol
|   |-- Table.sol # The contract interface file that implements the CRUD operation
|-- java  # Storing compiled package path and Java contract file
|   |-- org
|        |--fisco
|             |--bcos
|                  |--asset
|                       |--contract
|                             |--Asset.java  # Java file generated by the Asset.sol contract
|                             |--Table.java  # Java file generated by the Table.sol contract
|-- sol2java.sh
```

The `org/fisco/bcos/asset/contract/` package path directory is generated in the java directory. The directory contains two files `Asset.java` and `Table.java`, where `Asset.java` is the file required by the Java application to call the Asset.sol contract.

`Asset.java`'s main interface:

```java
package org.fisco.bcos.asset.contract;

public class Asset extends Contract {
    // Asset.sol contract  transfer interface generation
    public TransactionReceipt transfer(String from_account, String to_account, BigInteger amount);
    // Asset.sol contract  register interface generation
    public TransactionReceipt register(String account, BigInteger asset_value);
    // Asset.sol contract  select interface generation
     public Tuple2<BigInteger, BigInteger> select(String account) throws ContractException;

    // Load the Asset contract address, to generate Asset object
    public static Asset load(String contractAddress, Client client, CryptoKeyPair credential);

    // Deploy Assert.sol contract, to generate Asset object
    public static Asset deploy(Client client, CryptoKeyPair credential) throws ContractException;
}
```

The load and deploy functions are used to construct the Asset object, and the other interfaces are used to call the interface of the corresponding solidity contract. The detailed use will be introduced below.


## SDK configuration

We provide a Java engineering project for development. First, get the Java engineering project:

```bash
$ mkdir -p ~/fisco
# get the Java project project archive
$ cd ~/fisco
$ curl -#LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/asset-app.tar.gz
# extract the Java project project asset-app directory
$ tar -zxf asset-app.tar.gz
```

```eval_rst
.. note::
    - If the asset-app.tar.gz cannot be downloaded for a long time due to network problems, try `curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/asset-app.tar.gz`
```

The directory structure of the asset-app project is as follows:

```bash
|-- build.gradle // gradle configuration file
|-- gradle
|   |-- wrapper
|       |-- gradle-wrapper.jar //  related code implementation for downloading Gradle
|       |-- gradle-wrapper.properties //  Configuration information used by the wrapper, such as the version of gradle
|-- gradlew // shell script for executing wrapper commands under Linux or Unix
|-- gradlew.bat // batch script for executing wrapper commands under Windows
|-- src
|   |-- main
|   |   |-- java
|   |         |-- org
|   |             |-- fisco
|   |                   |-- bcos
|   |                         |-- asset
|   |                               |-- client // the client calling class
|   |                                      |-- AssetClient.java
|   |                               |-- contract // the Java contract class
|   |                                      |-- Asset.java
|   |-- test
|       |-- resources // resource files
|           |-- applicationContext.xml // project configuration file
|           |-- contract.properties // file that stores the deployment contract address
|           |-- log4j.properties // log configuration file
|           |-- contract // Solidity contract files
|                   |-- Asset.sol
|                   |-- Table.sol
|
|-- tool
    |-- asset_run.sh // project running script
```

### Project introduced Java SDK

**The project's `build.gradle` file has been introduced to Java SDK and no need to be modified**. The introduction method is as follows:

- You need to add maven remote repository to the `build.gradle` file:

```java
repositories {
    mavenCentral()
    maven {
        url "http://maven.aliyun.com/nexus/content/groups/public/"
    }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
}
```

-   introduce the Java SDK jar package

```java
compile ('org.fisco-bcos.java-sdk:fisco-bcos-java-sdk:2.7.2')
```

### Certificate and configuration file

-   Blockchain node certificate configuration

Copy the SDK certificate corresponding to the blockchain node

```bash
# go to the ~ directory
# copy the node certificate to the project's resource directory
$ cd ~/fisco
$ cp -r nodes/127.0.0.1/sdk/* asset-app/src/test/resources/conf
# if you want to run this app in IDE, copy the certificate to the main resource directory
$ mkdir -p asset-app/src/main/resources/conf
$ cp -r nodes/127.0.0.1/sdk/* asset-app/src/main/resources/conf
```

-   applicationContext.xml

**Note:**

If the channel_listen_ip (If the node version is less than v2.3.0, check listen_ip) set in the chain is 127.0.0.1 or 0.0.0.0 and the channel_listen_port is 20200, the `applicationContext.xml` configuration does not need to be modified. If the configuration of blockchain node is changed, you need to modify `applicationContext.xml`.

## Business development

We've covered how to introduce and configure the Java SDK in your own project. This section describes how to invoke a contract through a Java program, as well as an example asset management note. The asset-app project already contains the full source code of the sample, which users can use directly. Now introduces the design and implementation of the core class `AssetClient`.

`AssetClient.java`: The deployment and invocation of the contract is implemented by calling `Asset.java`, The path `/src/main/java/org/fisco/bcos/asset/client`, the initialization and the calling process are all in this class.


-   initialization

The main function of the initialization code is to construct the Web3j and Credentials' objects, which are needed to be used when creating the corresponding contract class object (calling the contract class's deploy or load function).

```java
@SuppressWarnings("resource")
ApplicationContext context =
        new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
bcosSDK = context.getBean(BcosSDK.class);
// init the client that can send requests to the group one
client = bcosSDK.getClient(1);
// create the keyPair
cryptoKeyPair = client.getCryptoSuite().createKeyPair();
client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
logger.debug("create client for group1, account address is " + cryptoKeyPair.getAddress());
```

-   construct contract class object

Contract objects can be initialized using the deploy or load functions, which are used in different scenarios. The former applies to the initial deployment contract, and the latter is used when the contract has been deployed and the contract address is known.

```java
// deploy contract
Asset asset = Asset.deploy(client, cryptoKeyPair);
// load contract address
Asset asset = Asset.load(contractAddress, client, cryptoKeyPair);
```

-   interface calling

Use the contract object to call the corresponding interface and handle the returned result.

```java
// select interface calling
 Tuple2<BigInteger, BigInteger> result = asset.select(assetAccount);
// register interface calling
TransactionReceipt receipt = asset.register(assetAccount, amount);
// transfer interface
TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount);
```

## Running

So far we have introduced all the processes of the asset management application using the blockchain and implemented the functions. Then we can run the project and test whether the function is normal.

-   compilation

```bash
# switch to project directory
$ cd ~/asset-app
# compile project
$ ./gradlew build
```

After the compilation is successful, the `dist` directory will be generated under the project root directory. There is an `asset_run.sh` script in the dist directory to simplify project operation. Now let's start by verifying the requirements set out in this article.


-   deploy the `Asset.sol` contract

```bash
# enter dist directory
$ cd dist
$ bash asset_run.sh deploy
Deploy Asset successfully, contract address is 0xd09ad04220e40bb8666e885730c8c460091a4775
```

-   register asset

```bash
$ bash asset_run.sh register Alice 100000
Register account successfully => account: Alice, value: 100000
$ bash asset_run.sh register Bob 100000
Register account successfully => account: Bob, value: 100000
```

-   query asset

```bash
$ bash asset_run.sh query Alice
account Alice, value 100000
$ bash asset_run.sh query Bob
account Bob, value 100000
```

-   transfer asset

```bash
$ bash asset_run.sh transfer Alice Bob  50000
Transfer successfully => from_account: Alice, to_account: Bob, amount: 50000
$ bash asset_run.sh query Alice
account Alice, value 50000
$ bash asset_run.sh query Bob
account Bob, value 150000
```

**Summary:** So far, we have built an application based on the FISCO BCOS Alliance blockchain through contract development, contract compilation, SDK configuration and business development.
