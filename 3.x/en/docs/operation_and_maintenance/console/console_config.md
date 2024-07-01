# Console Configuration

Tags: "console" "Console Configuration" "Command Line Interactive Tools"

---------

```eval_rst
.. important::
    - "Console" only supports FISCO BCOS version 3.x, based on 'Java SDK <.. / sdk / java _ sdk / index.html >' _ implementation。
    - You can use the command. "/ start.sh--version "View the current console version
```

[CONSOLE](https://github.com/FISCO-BCOS/console)is an important interactive client tool for FISCO BCOS 3.x, which is available through the [Java SDK](../../sdk/java_sdk/index.md)Establish a connection with a blockchain node to implement read and write access requests for blockchain node data。The console has a wealth of commands, including querying blockchain status, managing blockchain nodes, deploying and invoking contracts, and more.。In addition, the console provides a contract compilation tool that allows users to quickly and easily integrate Solidity and webankblockchain-liquid contract file(Hereinafter referred to as WBC-liquid) the compiled WASM file is converted to a Java contract file.。

wbc-Please refer to [wbc] for building the liquid compilation environment.-environment configuration of liquid](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/quickstart/prerequisite.html)。

## Console Configuration and Operation

```eval_rst
.. important::
    Precondition: To build the FISCO BCOS blockchain, see 'Building the first blockchain network <.. /.. / quick _ start / air _ installation.html >' _
    Chain Building Tool Reference:
     - 'Air version FISCO BCOS build chain script build _ chain <.. /.. / tutorial / air / build _ chain.html > '_
     - 'Pro version FISCO BCOS chain building tool BcosBuilder <.. /.. / tutorial / pro / pro _ builder.html > '_
```

### 1. Get the console

```shell
cd ~ && mkdir -p fisco && cd fisco
# Get Console Download Script
curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh 
# Execute download script
bash download_console.sh
```

```eval_rst
.. note::
    - If you cannot download for a long time due to network problems, try 'curl-#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh`
```

#### 1.1 Getting consoles for other Solidity versions

Since the default Solc compiler version in the console is 0.8.11, you can use the following command to download the Solidity contract.(Currently supports 0.4.25, 0.5.2, 0.6.10, 0.8.11)：

```shell
# Download version 0.4.25 of the console
bash download_console.sh -v 0.4 # And so on, support specified version(0.4,0.5,0.6,0.8)
```

#### 1.2 The console has been configured, and the Solidity version is manually switched

After you have configured the console, you need to switch to another Solidity version. You can use the 'download _ console.sh' script to download the corresponding SolcJ jar package and manually replace the Solidity version. You can use the following command to download (currently 0.4.25, 0.5.2, 0.6.10). The default Solidity version is 0.8.11:

```shell
# Download solcJ jar package version 0.6.10
bash download_console.sh -s 0.6 # And so on, support specified version(0.4,0.5,0.6)
# In the directory where the script is executed, two new files are generated, similar to the other versions
ls solcJ*
solcJ-0.6.10.1.jar solcJ-0.6.tar.gz
# will solcJ-0.6.10.1.jar Manually replace the solcJ file in the lib directory of the folder where the console is located.。For example, the console directory is in this directory:
mv ./console/lib/solcJ-0.8.11.1.jar . && cp solcJ-0.6.10.1.jar ./console/lib/
# At this point, the default Solidity version of the console is switched to version 0.6.10
```

The configured console directory structure is as follows:

```shell
│── apps # console jar package directory
│   └── console.jar
├── lib # dependent jar package directory
├── conf
│   ├── clog.ini    # c sdk log configuration file
│   ├── config-example.toml # Profile
│   └── log4j.properties # Log Configuration File
├── contracts # Contract Directory
│   ├── console # Contract abi, bin, java file directory compiled during contract deployment in the console
│   ├── sdk     # contract abi, bin, java file directory compiled by sol2java.sh script
│   ├── liquid  # WBC-Liquid contract storage directory
│   └── solidity    # Solidity contract storage directory
│       └── HelloWorld.sol # Common contract: HelloWorld contract, deployable and callable
│       └── KVTableTest.sol # Contracts using the KV storage interface: KVTableTest contract, which can be deployed and invoked
│       └── KVTable.sol # Interface contract providing KV storage operation
│-- start.sh # Console Startup Script
│-- get_account.sh # Account Generation Script
│-- get_gm_account.sh # Account Generation Script, State Secret Edition
│-- contract2java.sh # Solidity/WBC-Liquid contract files are compiled into development tool scripts for java contract files.
```

### 2. Configure Console

- Configuration of blockchain nodes and certificates:
  - Copy all files in the node sdk directory to the 'conf' directory。
  - Put the 'config' in the 'conf' directory-example.toml 'file renamed to' config.toml 'file。Configure the 'config.toml' file, where the content of the added comment is modified according to the blockchain node configuration。

The sample configuration file is as follows:

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path
disableSsl = "false"                        # Communication with nodes without SSL
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

Configuration item detailed description [refer here](../sdk/java_sdk/config.md)。

```eval_rst
.. important::

    Console Description
    
    - When the console configuration file configures multiple node connections in a group, some nodes in the group may exit the group during operation, so the information returned by the console polling node query may be inconsistent, which is a normal phenomenon.。We recommend that you use the console to configure a node or ensure that the configured node is always in the group, so that the information in the group queried during the synchronization time is consistent.。
```

### 3. Start the console

With the node running, start the console:

```shell
$ ./start.sh
# Output the following message to indicate successful startup
=============================================================================================
Welcome to FISCO BCOS console(3.0.0)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=============================================================================================
```

### 4. Startup Script Description

#### 4.1 Viewing the Current Console Version

```shell
./start.sh --version
console version: 3.0.0
```

#### 4.2 Account Usage

##### 4.2.1 Console Load Private Key

The console provides the account generation script get _ account.sh(Please refer to [Account Management Document] for script usage.(../../develop/account.md)The generated account file is in the accounts directory, and the account file loaded by the console must be placed in that directory。
There are several ways to start the console:

```shell
./start.sh
./start.sh group0
./start.sh group0 -pem pemName
./start.sh group0 -p12 p12Name
```

##### 4.2.2 Default startup

Starts with the default group number specified by the console profile。

```shell
./start.sh
```

**注意**: When the console starts without specifying a private key account, it will try to load an available private key account from the 'account' directory for sending transactions. If the load fails, a new 'PEM' account file will be created and saved in the 'account' directory.。

##### 4.2.3 Start by specifying the group name

Start with the group name specified on the command line。

```shell
./start.sh group0
```

##### 4.2.4 Start using PEM format private key file

- Start with the account of the specified pem file, enter the parameters: group number,-pem, pem file path

```shell
./start.sh group0 -pem account/ecdsa/0x2dbb332a844e0e076f97c90ff5078ea7dd2de910.pem
```

##### 4.2.5 Start using PKCS12 format private key file

- Use the specified p12 file account, you need to enter a password, enter parameters: group number,-p12, p12 file path

```shell
./start.sh group0 -p12 account/ecdsa/0x2dbb332a844e0e076f97c90ff5078ea7dd2de910.pem
Enter Export Password:
```

**Note:**
When the console starts, the following error occurs when the p12 file is loaded:

```shell
exception unwrapping private key - java.security.InvalidKeyException: Illegal key size
```

It may be the Java version. Refer to the solution: [https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size](https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size)

## Java Contract Generation Tool

The console provides a specialized tool for generating Java contracts, making it easy for developers to integrate Solidity and WBC.-The liquid contract file is compiled into a Java contract file.。

The current contract generation tool supports automatic compilation of Solidity and generation of Java files, support for specifying wbc-Liquid compiles the WASM file and the ABI file to generate the Java file.。

**Note:** The Solidity contract generation tool is directly related to the Solc version number. For the corresponding Solidity contract, use the console with the corresponding Solc。Please refer to 1.1 above for consoles of other Solidity versions.。

### Solidity Contract Use

```shell
$ bash contract2java.sh solidity -h 
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -h,--help
 -l,--libraries <arg>   [Optional] Set library address information built
                        into the solidity contract
                        eg:
                        --libraries lib1:lib1_address lib2:lib2_address
 -o,--output <arg>      [Optional] The file path of the generated java
                        code, default is contracts/sdk/java/
 -p,--package <arg>     [Optional] The package name of the generated java
                        code, default is com
 -s,--sol <arg>         [Optional] The solidity file path or the solidity
                        directory path, default is contracts/solidity/
```

Detailed parameters:

- `package`: Generate the package name of the 'Java' file。
- `sol`: (Optional)The path of the 'solidity' file. Two methods are supported: file path and directory path. When the parameter is a directory, all the 'solidity' files in the directory are compiled and converted.。The default directory is' contracts / solidity'。
- `output`: (Optional)The directory where the 'Java' file is generated. By default, it is generated in the 'contracts / sdk / java' directory.。 

### wbc-The liquid contract uses

```shell
$ bash contract2java.sh liquid -h
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -a,--abi <arg>       [Required] The ABI file path of WBC-Liquid contract.
 -b,--bin <arg>       [Required] The binary file path of WBC-Liquid contract.
 -h,--help
 -o,--output <arg>    [Optional] The file path of the generated java code,
                      default is contracts/sdk/java/
 -p,--package <arg>   [Optional] The package name of the generated java
                      code, default is com
 -s,--sm-bin <arg>    [Required] The SM binary file path of WBC-Liquid
                      contract.
```

Detailed parameters:

- 'abi ': (Required) WBC-Path to the 'ABI' file of the Liquid contract, which is generated in the target folder after using the 'cargo liquid build' command。
- 'bin ': (Required) WBC-Path to the 'wasm bin' file of the Liquid contract, which is generated in the target folder after using the 'cargo liquid build' command。
- 'package ': (Optional) Generate the package name of the' Java 'file, which is' org 'by default.。
- `sm-bin ': (Required) WBC-The path to the 'wasm sm bin' file of the Liquid contract.-Generated in the target folder after the g 'command。

#### 使用

```shell
$ cd ~/fisco/console

# Java code for generating Solidity contracts
$ bash contract2java.sh solidity -p org.com.fisco

# Generate WBC-Java code for the Liquid contract
$ bash contract2java.sh liquid -p org.com.fisco -b ./contracts/liquid/asset/asset.wasm -a ./contracts/liquid/asset/asset.abi -s ./contracts/liquid/asset/asset_gm.wasm 
```

After running successfully, the java, abi, and bin directories will be generated in the 'console / contracts / sdk' directory, as shown below。

```shell
|-- abi # The abi directory generated by compilation, which stores the abi file compiled by the solidity contract
|   |-- HelloWorld.abi
|   |-- KVTable.abi
|   |-- KVTableTest.abi
|-- bin # The bin directory generated by compilation, which stores the bin file compiled by the Solidity contract
|   |-- HelloWorld.bin
|   |-- KVTable.bin
|   |-- KVTableTest.bin
|-- java  # Store the compiled package path and Java contract file
|   |-- org
|       |-- com
|           |-- fisco
|               |-- HelloWorld.java # Solidity Compiled HelloWorld Java File
|               |-- KVTable.java    # Solidity Compiled KV Storage Interface Contract Java File
|               |-- KVTableTest.java  # Solidity compiled KVTableTest Java file
|               |-- Asset.java  # wbc-The asset file generated by liquid
```

The 'org / com / fisco /' package path directory is generated in the Java directory。The Java contract files' HelloWorld.java ',' KVTableTest.java ',' KVTable.java 'and' Asset.java 'will be generated in the package path directory.。where 'HelloWorld.java', 'KVTableTest.java' and 'Asset.java' are the Java contract files required by the Java application。
