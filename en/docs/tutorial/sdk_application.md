# Build the first blockchain application


In this chapter we will introduce a whole process of business application scenario development based on FISCO BCOS blockchain. The introduce includes business scenario analysis, contract design implementation, contract compilation, and blockchain development. Finally, we introduce an application module implementation which is to implement calling access to the contract on blockchain through the Web3SDK we provide.

This tutorial requires user to be familiar with the Linux operating environment, has the basic skills of Java development, is able to use the Gradle tool, and is familiar with [Solidity syntax](https://solidity.readthedocs.io/en/latest/). Through the tutorial, you will learn the following:

1. How to express the logic of a business scenario in the form of a contract
2. How to convert Solidity contract into Java class
3. How to configure Web3SDK
4. How to build an application and integrate Web3SDK into application engineering
5. How to call the contract interface through Web3SDK, and to understand its principle

The full project source code for the sample is provided in the tutorial. Users can quickly develop their own applications based on it.

```eval_rst
.. important::
    Please refer to `Installation documentation <../installation.html>`_ to complete the construction of the FISCO BCOS blockchain and the console download. The operation in this tutorial is assumed to be carried out in the environment of the documentation building.

```

## Sample application requirements

Blockchain is naturally tamper-proof and traceable. Its characteristics make it more attractive to the financial industry. In this article, we will provide an easy example of asset management development and ultimately achieve the following functions:

- Ability to register assets on blockchain
- Ability to transfer funds between different accounts
- Ability to check the amount of assets in account

## Contract design and implementation

When developing an application on blockchain, for combining with business requirements, it is first necessary to design the corresponding smart contract to determine the storage data that contract needs, and on this basis, to determine the interface provided by the smart contract. Finally, to specifically implement each interface.

### Storage design

FISCO BCOS provides a [contract CRUD interface](../manual/smart_contract.html#crud) development model, which can create table through contracts, and add, delete, and modify the created table. For this application, we need to design a table `t_asset` for storage asset management. The table's fields are as follows:

-   account: primary key, asset account (string type)
-   asset_value: asset amount (uint256 type)

account is the primary key, which is the field that needs to be passed when the `t_asset` table is operated. Blockchain queries the matching records in the table according to the primary key field. The example of `t_asset` table is as follow:

| account | asset_value |
| ------- | ----------- |
| Alice   | 10000       |
| Bob     | 20000       |

### Interface design

According to the design goals of the business, it is necessary to implement asset registration, transfer, and query functions. The interfaces of the corresponding functions are as follows:

```js
// asset amount query
function select(string account) public constant returns(int256, uint256)
// asset registration
function register(string account, uint256 amount) public returns(int256)
// asset transfer
function transfer(string from_asset_account, string to_asset_account, uint256 amount) public returns(int256)
```

### Full source code

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
        // |  asset account(primary key)      |     asset amount       |
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
            parameter1： true to return to 0 or no account to return to -1
            parameter2： it is valid when the first parameter is 0, the amount of assets
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
            -1 transferred asset account does not exist
            -2 received asset account does not exist
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

In the previous section, we designed the storage and interface of the contract `Asset.sol` according to business requirements, and implemented them completely. However, Java program cannot directly call Solidity contract. Solidity contract file needs to be compiled into a Java file first.

Console provides a compilation tool that can store the `Asset.sol` contract file in the `console/contract/solidity` directory, and uses the `sol2java.sh` script provided in the console directory to compile. The operation is as follows:
```bash
# switch to fisco/console/ directory
$ cd ~/fisco/console/
# compile contract, to specify a Java's package name parameter behind, you can specify the package name according to the actual project path.
$ ./sol2java.sh org.fisco.bcos.asset.contract
```

After running is successful, the java, abi, and bin directories will be generated in the `console/contracts/sdk` directory as shown below.

```bash
|-- abi # The generated abi directory, which stores the abi file generated by Solidity contract compilation.
|   |-- Asset.abi
|   |-- Table.abi
|-- bin # The generated bin directory, which stores the bin file generated by Solidity contract compilation.
|   |-- Asset.bin
|   |-- Table.bin
|-- contracts # The source code file that stores Solidity contract. Copy the contract that needs to be compiled to this directory.
|   |-- Asset.sol # A copied Asset.sol contract, depends on Table.sol
|   |-- Table.sol # System CRUD contract interface file provided by default
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

The `org/fisco/bcos/asset/contract/` package path directory is generated in the java directory. The directory contains two files `Asset.java` and `Table.java`, where `Asset.java` is the necessary file that Java application to call `Asset.sol` contract.

`Asset.java`'s main interface:

```java
package org.fisco.bcos.asset.contract;

public class Asset extends Contract {
    // Asset.sol contract  transfer interface generation
    public RemoteCall<TransactionReceipt> transfer(String from_account, String to_account, BigInteger amount);
    // Asset.sol contract  register interface generation
    public RemoteCall<TransactionReceipt> register(String account, BigInteger asset_value);
    // Asset.sol contract  select interface generation
    public RemoteCall<Tuple2<BigInteger, BigInteger>> select(String account);

    // Load the Asset contract address, to generate Asset object
    public static Asset load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider);

    // Deploy Assert.sol contract, to generate Asset object
    public static RemoteCall<Asset> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider);
}
```

The load and deploy functions are used to construct the Asset object, and the other interfaces are used to call the interface of the corresponding solidity contract. The detailed use will be introduced below.


## SDK configuration

We provide a Java engineering project for development. First, to get the Java engineering project:

```bash
    # get the compressed package of Java engineering
    $ cd ~
    $ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/asset-app.tar.gz
    # extract to get the Java engineering project asset-app directory
    $ tar -zxf asset-app.tar.gz
```

The directory structure of the asset-app project is as follows:

```bash
|-- build.gradle // gradle configuration file
|-- gradle
|   |-- wrapper
|       |-- gradle-wrapper.jar //  the related code implementation for downloading Gradle
|       |-- gradle-wrapper.properties // configuration information used by the wrapper, such as the gradle's version etc..
|-- gradlew // shell script for executing wrapper commands under Linux or Unix
|-- gradlew.bat // batch script for executing wrapper commands under Windows
|-- src
|   |-- main
|   |   |-- java
|   |         |-- org
|   |             |-- fisco
|   |                   |-- bcos
|   |                         |-- asset
|   |                               |-- client // place the client calling class
|   |                                      |-- AssetClient.java
|   |                               |-- contract // place the Java contract class
|   |                                      |-- Asset.java
|   |-- test
|       |-- resources // store code resource files
|           |-- applicationContext.xml // project configuration file
|           |-- ca.crt // blockchain ca certificate
|           |-- node.crt // blockchain ca certificate
|           |-- node.key // node certificate
|           |-- contract.properties // file that stores the deployment contract address
|           |-- log4j.properties // log configuration file
|           |-- contract //store Solidity contract files
|                   |-- Asset.sol
|                   |-- Table.sol
|
|-- tool
    |-- asset_run.sh // project running script
```

### Project introduced Web3SDK

**The project's `build.gradle` file has been introduced to Web3SDK and no need to be modified**. The introduction method is as follows:

- Web3SDK introduces the related jar package of Ethereum's solidity compiler, so you need to add Ethereum's remote repository to the `build.gradle` file:

```java
repositories {
    maven {
        url "http：//maven.aliyun.com/nexus/content/groups/public/"
    }
    maven { url "https：//dl.bintray.com/ethereum/maven/" }
    mavenCentral()
}
```

-   introduce Web3SDK jar package

```java
compile ('org.fisco-bcos：web3sdk：2.0.0-rc2')
```

### Certificate and configuration file

-   Blockchain node certificate configuration

Copy the SDK certificate corresponding to the blockchain node

```bash
# come into~directory
# copy the node certificate to the project's resource directory
$ cd ~
$ cp fisco/nodes/127.0.0.1/sdk/* asset-app/src/test/resources/
```

-   applicationContext.xml  

**Note:**

If the rpc_listen_ip set in the chain is 127.0.0.1 or 0.0.0.0 and the channel_port is 20200, the `applicationContext.xml` configuration does not need to be modified. If the configuration of blockchain node is changed, you need to modify `applicationContext.xml`. For details, please refer to [SDK Usage Document](../sdk/sdk.html#spring).

## Business development

We have introduced how to introduce and configure Web3SDK in your own project. In this section, we will introduce how to call a contract through Java program, as well as use an example asset management to explain. The asset-app project already contains the full source code of the sample, which users can use directly. Now we introduces the design and implementation of the core class `AssetClient`.

`AssetClient.java`: to implement contract deployment and calling by calling `Asset.java`. The path is `/src/main/java/org/fisco/bcos/asset/client`. The initialization and the calling process are carrying out in this class.


-   initialization  

The main function of the initialization code is to construct the Web3j and Credentials' objects, which are needed to be used when creating the corresponding contract class object (calling the contract class's deploy or load function).

```java
// initialize in the function 'initialize'
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
Service service = context.getBean(Service.class);
service.run();

ChannelEthereumService channelEthereumService = new ChannelEthereumService();
channelEthereumService.setChannelService(service);
// initialize the Web3j object
Web3j web3j = Web3j.build(channelEthereumService, 1);
// initialize the Credentials object
Credentials credentials = Credentials.create(Keys.createEcKeyPair());
```

-   construct contract class object

You can use deploy or load functions to initialize the contract object. They are used in different scenarios. Deploy applies to the initial deployment contract, and load is used when the contract has been deployed and the contract address is known.

```java
// deploy contract
Asset asset = Asset.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();
// load contract address
Asset asset = Asset.load(contractAddress, web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
```

-   interface calling

Use the contract object to call the corresponding interface and handle the returned result.

```java
// select interface calling
Tuple2<BigInteger, BigInteger> result = asset.select(assetAccount).send();
// register interface calling
TransactionReceipt receipt = asset.register(assetAccount, amount).send();
// transfer interface
TransactionReceipt receipt = asset.transfer(fromAssetAccount, toAssetAccount, amount).send();
```

## Running

So far we have introduced all the processes of asset management application by using blockchain and how to implement the functions. Then we can run project and test whether the function is normal.

-   compilation

```bash
# switch to project directory
$ cd ~/asset-app
# compile project
$ ./gradlew build
```

After the compilation is successful, `dist` directory will be generated under the project root directory. There is an `asset_run.sh` script in the dist directory to simplify project operation. Now let's start verifying the requirements set out in this article.


-   deploy `Asset.sol` contract

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

**Summary:** By now, we have built an application based on FISCO BCOS alliance chain through contract development, contract compilation, SDK configuration and business development.
