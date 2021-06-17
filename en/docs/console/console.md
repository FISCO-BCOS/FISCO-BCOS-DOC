# Console

```eval_rst
.. important::
    - ``Console 1.x`` series is based on `Web3SDK <../sdk/java_sdk.html>`_ implementation, after ``Console 2.6`` is based on `Java SDK <../sdk/java_sdk/index.html >`_ implementation, this tutorial is aimed at **1.x version console**, for 2.6 and above version console usage documentation please refer to `here <./console_of_java_sdk.md>`_ 
    - You can view the current console version through the command ``./start.sh --version``
```

[Console](https://github.com/FISCO-BCOS/console) is an important interactive client tool of FISCO BCOS 2.0. It establishes a connection with blockchain node through [Java SDK](../sdk/java_sdk.md) to request read and write access for blockchain node data. Console has a wealth of commands, including blockchain status inquiry, blockchain nodes management, contracts deployment and calling. In addition, console provides a contract compilation tool that allows users to easily and quickly compile Solidity contract files into Java contract files.

### Console command

Console command consists of two parts, the instructions and the parameters related to the instruction:
- **Instruction**: instruction is an executed operation command, including blockchain status inquiry and contracts deployment and calling. And some of the instructions call the JSON-RPC interface, so they have same name as the JSON-RPC interface.
**Use suggestions: instructions can be completed using the tab key, and support for displaying historical input commands by pressing the up and down keys.**

- **Parameters related to the instruction**: parameters required by instruction call interface. Instructions to parameters and parameters to parameters are separated by spaces. The parameters name same as JSON-RPC interface and the explanation of getting information field can be referred to [JSON-RPC API](../api.md).

### Common command link:
#### Contract related commands
  - use[CNS](../design/features/cns_contract_name_service.md) to deploy and call contract (**recommend**)
    - deploy contract: [deployByCNS](./console.html#deploybycns)
    - call contract: [callByCNS](./console.html#callbycns)
    - query CNS deployment contract information: [queryCNS](./console.html#querycns)
  - deploy and call contract normally
    - deploy contract: [deploy](./console.html#deploy)
    - call contract: [call](./console.html#call)
#### Other commands
- query block number:[getBlockNumber](./console.html#getblocknumber)
- query Sealer list:[getSealerList](./console.html#getsealerlist)
- query the information of transaction receipt: [getTransactionReceipt](./console.html#gettransactionreceipt)
- switch group: [switch](./console.html#switch)

### Shortcut key
- `Ctrl+A`: move cursor to the beginning of line
- `Ctrl+D`: exit console
- `Ctrl+E`: move cursor to the end of line
- `Ctrl+R`: search for the history commands have been entered
- &uarr;: browse history commands forward
- &darr;: browse history commands backward


### Console response

When a console command is launched, the console will obtain the result of the command execution and displays the result at the terminal. The execution result is divided into two categories:
- **True:** The command returns to the true execution result as a string or json.
- **False:** The command returns to the false execution result as a string or json.
  - When console command call the JSON-RPC interface, error code [reference here](../design/rpc.html#id6).
  - When console command call the Precompiled Service interface, error code  [reference here](../sdk/sdk.html#precompiled-service-api).

## Console configuration and operation

```eval_rst
.. important::
    Precondition：to build FISCO BCOS blockchain, please refer to `Building Chain Script <../manual/build_chain.html>`_ or `Enterprise Tools <../enterprise_tools/index.html>`_.


```
### Get console

```bash
$ cd ~ && mkdir fisco && cd fisco
# get console
$ curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.7.2/download_console.sh && bash download_console.sh -c 1.2.0
```

```eval_rst
.. note::
    - If the script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh -c 1.2.0`
```

The directory structure is as follows:
```bash
|-- apps # console jar package directory
|   -- console.jar
|-- lib # related dependent jar package directory
|-- conf
|   |-- applicationContext-sample.xml # configuration file
|   |-- log4j.properties  # log configuration file
|-- contracts # directory where contract locates
|   -- solidity  # directory where solidity contract locates
|       -- HelloWorld.sol # normal contract: HelloWorld contract, is deployable and callable
|       -- TableTest.sol # the contracts by using CRUD interface: TableTest contract, is deployable and callable
|       -- Table.sol # CRUD interfac contract
|   -- console  # The file directory of contract abi, bin, java compiled when console deploys the contract
|   -- sdk      # The file directory of contract abi, bin, java compiled by sol2java.sh script
|-- start.sh # console start script
|-- get_account.sh # account generate script
|-- sol2java.sh # development tool script for compiling solidity contract file as java contract file
|-- replace_solc_jar.sh # a script for replacing the compiling jar package
```

### Configure console
- Blockchain node and certificate configuration:
   - To copy the `ca.crt`, `sdk.crt`, and `sdk.key` files in the sdk node directory to the `conf` directory.
   - To rename the `applicationContext-sample.xml` file in the `conf` directory to the `applicationContext.xml` file. To configure the `applicationContext.xml` file, where the remark content is modified according to the blockchain node configuration. **Hint: If the channel_listen_ip(If the node version is earlier than v2.3.0, check the configuration item listen_ip) set through chain building is 127.0.0.1 or 0.0.0.0 and the channel_port is 20200, the `applicationContext.xml` configuration is not modified. **

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
                        <list>  <!-- each group need to configure a bean -->
                                <bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                                        <property name="groupId" value="1" /> <!-- groupID -->
                                        <property name="connectionsStr">
                                                <list>
                                                        <value>127.0.0.1:20200</value>  <!-- IP:channel_port -->
                                                </list>
                                        </property>
                                </bean>
                        </list>
                </property>
        </bean>

        <bean id="channelService" class="org.fisco.bcos.channel.client.Service" depends-on="groupChannelConnectionsConfig">
                <property name="groupId" value="1" /> <!-- to connect to the group with ID 1 -->
                <property name="orgID" value="fisco" />
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>

</beans>
```
  Configuration detail [reference here](../sdk/sdk.html#spring).

```eval_rst
.. important::

    Console configuration instructions

    - If the console is configured correctly, but when it is launched on Cent0S system, the following error occurs:

      Failed to connect to the node. Please check the node status and the console configuration.

      It is because the JDK version that comes with the CentOS system is used (it will cause the console and blockchain node's authentication to fail). Please download Java 8 version or above from `OpenJDK official website <https://jdk.java.net/java-se-ri/8>`_ or `Oracle official website <https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`_ and install (specific installation steps `refer to Appendix <./console.html#java>`_). To launch the console after installation.

    - When the console configuration file configures multiple node connections in a group, some nodes in the group may leave the group during operation. Therefore, it shows a norm which is when the console is polling, the return information may be inconsistent. It is recommended to configure a node or ensure that the configured nodes are always in the group when using the console, so that the inquired information in the group will keep consistent during the synchronization time.

```

### Configure OSCCA-approved cryptography console
- Blockchain node and certificate configuration:
   - To copy the `ca.crt`, `sdk.crt`, and `sdk.key` files in the sdk node directory to the `conf` directory.
   - To rename the `applicationContext-sample.xml` file in the `conf` directory to the `applicationContext.xml` file. To configure the `applicationContext.xml` file, where the remark content is modified according to the blockchain node configuration. **Hint: If the channel_listen_ip(If the node version is earlier than v2.3.0, check the configuration item listen_ip) set through chain building is 127.0.0.1 or 0.0.0.0 and the channel_port is 20200, the `applicationContext.xml` configuration is not modified. **

#### Contract compilation tool

**Console provides a special compilation contract tool that allows developers to compile Solidity contract files into Java contract files.** Two steps for using the tool:
  - To place the Solidity contract file in the `contracts/solidity` directory.
  - Complete the task of compiling contract by running the `sol2java.sh` script (**requires specifying a java package name**). For example, there are `HelloWorld.sol`, `TableTest.sol`, and `Table.sol` contracts in the `contracts/solidity` directory, and we specify the package name as `org.com.fisco`. The command is as follows:

    ```bash
    $ cd ~/fisco/console
    $ ./sol2java.sh org.com.fisco
    ```
 After running successfully, the directories of Java, ABI and bin will be generated in the `console/contracts/sdk` directory as shown below.

    ```bash
    |-- abi # to compile the generated abi directory and to store the abi file compiled by solidity contract
    |   |-- HelloWorld.abi
    |   |-- Table.abi
    |   |-- TableTest.abi
    |-- bin # to compile the generated bin directory and to store the bin file compiled by solidity contract
    |   |-- HelloWorld.bin
    |   |-- Table.bin
    |   |-- TableTest.bin
    |-- java  # to store compiled package path and Java contract file
    |   |-- org
    |       |-- com
    |           |-- fisco
    |               |-- HelloWorld.java # the target Java file which is compiled successfully
    |               |-- Table.java  # the CRUD interface Java file which is compiled successfully
    |               |-- TableTest.java  # the TableTest Java file which is compiled successfully
    ```

In the java directory, `org/com/fisco/` package path directory is generated. In the package path directory, the java contract files `HelloWorld.java`, `TableTest.java` and `Table.java` will be generated. `HelloWorld.java` and `TableTest.java` are the java contract files required by the java application.

### Launch console

Start the console while the node is running:

```text
$ ./start.sh
# To output the following information to indicate successful launch
=====================================================================================
Welcome to FISCO BCOS console(1.0.3)!
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

=====================================================================================
```

### Launch script description
#### To view the current console version:
```bash
./start.sh --version
console version: 1.0.3
```
#### Account using method

##### Console loads private key

The console provides the account generation script get_account.sh (for the script tutorial, please refer to [Account Management Document](../manual/account.md). The generated account file is in the accounts directory, and the account file loaded by console must be placed in the directory.


The console startup methods are as follows:
```
./start.sh
./start.sh groupID
./start.sh groupID -pem pemName
./start.sh groupID -p12 p12Name
```
##### Default start
The console randomly generates an account that is started with the group number specified in the console configuration file.
```bash
./start.sh
```
##### Specify group number to start
The console randomly generates an account that is started with the group number specified on the command line.
```bash
./start.sh 2
```
- Note: The specified group needs to configure 'bean' in the console configuration file.

##### Start with PEM format private key file
- Start with the account of the specified pem file, enter the parameters: group number, -pem, and pem file path
```bash
./start.sh 1 -pem accounts/0xebb824a1122e587b17701ed2e512d8638dfb9c88.pem
```
##### Start with PKCS12 format private key file
- Start with the account of the specified p12 file, enter the parameters: group number, -p12, and p12 file path
```bash
./start.sh 1 -p12 accounts/0x5ef4df1b156bc9f077ee992a283c2dbb0bf045c0.p12
Enter Export Password:
```

## Console command
### **help**
Enter help or h to see all the commands on the console.

```text
[group:1]> help
-------------------------------------------------------------------------------------
addObserver                              Add an observer node.
addSealer                                Add a sealer node.
call                                     Call a contract by a function and Parameters.
callByCNS                                Call a contract by a function and Parameters by CNS.
deploy                                   Deploy a contract on blockchain.
deployByCNS                              Deploy a contract on blockchain by CNS.
desc                                     Description table information.
exit                                     Quit console.
getBlockHeaderByHash                     Query information about a block header by hash.
getBlockHeaderByNumber                   Query information about a block header by block number.
getBlockByHash                           Query information about a block by hash.
getBlockByNumber                         Query information about a block by block number.
getBlockHashByNumber                     Query block hash by block number.
getBlockNumber                           Query the number of most recent block.
getCode                                  Query code at a given address.
getConsensusStatus                       Query consensus status.
getDeployLog                             Query the log of deployed contracts.
getGroupList                             Query group list.
getGroupPeers                            Query nodeId list for sealer and observer nodes.
getNodeIDList                            Query nodeId list for all connected nodes.
getNodeVersion                           Query the current node version.
getNodeInfo                              Query the specified node information.
getObserverList                          Query nodeId list for observer nodes.
getPbftView                              Query the pbft view of node.
getPeers                                 Query peers currently connected to the client.
getPendingTransactions                   Query pending transactions.
getPendingTxSize                         Query pending transactions size.
getSealerList                            Query nodeId list for sealer nodes.
getSyncStatus                            Query sync status.
getSystemConfigByKey                     Query a system config value by key.
setSystemConfigByKey                     Set a system config value by key.
getTotalTransactionCount                 Query total transaction count.
getTransactionByBlockHashAndIndex        Query information about a transaction by block hash and transaction index position.
getTransactionByBlockNumberAndIndex      Query information about a transaction by block number and transaction index position.
getTransactionByHash                     Query information about a transaction requested by transaction hash.
getTransactionReceipt                    Query the receipt of a transaction by transaction hash.
getTransactionByHashWithProof            Query the transaction and transaction proof by transaction hash.
getTransactionReceiptByHashWithProof     Query the receipt and transaction receipt proof by transaction hash.
grantCNSManager                          Grant permission for CNS by address.
grantDeployAndCreateManager              Grant permission for deploy contract and create user table by address.
grantNodeManager                         Grant permission for node configuration by address.
grantSysConfigManager                    Grant permission for system configuration by address.
grantUserTableManager                    Grant permission for user table by table name and address.
help(h)                                  Provide help information.
listCNSManager                           Query permission information for CNS.
listDeployAndCreateManager               Query permission information for deploy contract and create user table.
listNodeManager                          Query permission information for node configuration.
listSysConfigManager                     Query permission information for system configuration.
listUserTableManager                     Query permission for user table information.
queryCNS                                 Query CNS information by contract name and contract version.
quit(q)                                  Quit console.
removeNode                               Remove a node.
revokeCNSManager                         Revoke permission for CNS by address.
revokeDeployAndCreateManager             Revoke permission for deploy contract and create user table by address.
revokeNodeManager                        Revoke permission for node configuration by address.
revokeSysConfigManager                   Revoke permission for system configuration by address.
revokeUserTableManager                   Revoke permission for user table by table name and address.
listContractWritePermission              Query the account list which have write permission of the contract.
grantContractWritePermission             Grant the account the contract write permission.
revokeContractWritePermission            Revoke the account the contract write permission.
grantContractStatusManager               Grant contract authorization to the user.
getContractStatus                        Get the status of the contract.
listContractStatusManager                List the authorization of the contract.
grantCommitteeMember                     Grant the account committee member
revokeCommitteeMember                    Revoke the account from committee member
listCommitteeMembers                     List all committee members
grantOperator                            Grant the account operator
revokeOperator                           Revoke the operator
listOperators                            List all operators
updateThreshold                          Update the threshold
queryThreshold                           Query the threshold
updateCommitteeMemberWeight              Update the committee member weight
queryCommitteeMemberWeight               Query the committee member weight
freezeAccount                            Freeze the account.
unfreezeAccount                          Unfreeze the account.
getAccountStatus                         GetAccountStatus of the account.
freezeContract                           Freeze the contract.
unfreezeContract                         Unfreeze the contract.
switch(s)                                Switch to a specific group by group ID.
[create sql]                             Create table by sql.
[delete sql]                             Remove records by sql.
[insert sql]                             Insert records by sql.
[select sql]                             Select records by sql.
[update sql]                             Update records by sql.
-------------------------------------------------------------------------------------
```
**Note: **
- help shows the meaning of each command: command and command description
- for instructions on how to use specific commands, enter the command -h or \--help to view them. E.g:

```text
[group:1]> getBlockByNumber -h
Query information about a block by block number.
Usage: getBlockByNumber blockNumber [boolean]
blockNumber -- Integer of a block number, from 0 to 2147483647.
boolean -- (optional) If true it returns the full transaction objects, if false only the hashes of the transactions.
```
### **switch**
To run switch or s to switch to the specified group. The group number is displayed in front of the command prompt.

```text
[group:1]> switch 2
Switched to group 2.

[group:2]>
```
**Note: ** For the group that needs to be switched, make sure that the information of the group is configured in `applicationContext.xml` (the initial state of this configuration file only provides the group 1 configuration) in the `console/conf` directory, the configured node ID and port in the group are correct, and the node is running normally.


### **getBlockNumber**
To run getBlockNumber to view block number.

```text
[group:1]> getBlockNumber
90
```
### **getSealerList**
To run getSealerList to view the list of consensus nodes.

```text
[group:1]> getSealerList
[
    0c0bbd25152d40969d3d3cee3431fa28287e07cff2330df3258782d3008b876d146ddab97eab42796495bfbb281591febc2a0069dcc7dfe88c8831801c5b5801,
    10b3a2d4b775ec7f3c2c9e8dc97fa52beb8caab9c34d026db9b95a72ac1d1c1ad551c67c2b7fdc34177857eada75836e69016d1f356c676a6e8b15c45fc9bc34,
    622af37b2bd29c60ae8f15d467b67c0a7fe5eb3e5c63fdc27a0ee8066707a25afa3aa0eb5a3b802d3a8e5e26de9d5af33806664554241a3de9385d3b448bcd73
]
```

### **getObserverList**
To run getSealerList to view the list of observer nodes.

```text
[group:1]> getObserverList
[
    037c255c06161711b6234b8c0960a6979ef039374ccc8b723afea2107cba3432dbbc837a714b7da20111f74d5a24e91925c773a72158fa066f586055379a1772
]
```
### **getNodeIDList**
To run getNodeIDList to view the nodes and the list of nodeIds connected to p2p nodes.

```text
[group:1]> getNodeIDList
[
    41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
    87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1,
    29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
    d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8
]
```

### **getPbftView**
To run getPbftView to view the pbft viewgraph.

```text
[group:1]> getPbftView
2730
```
### **getConsensusStatus**
To run getConsensusStatus to view the consensus status.

```text
[group:1]> getConsensusStatus
[
    {
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "accountType": 1,
      "allowFutureBlocks": true,
      "cfgErr": false,
      "connectedNodes": 3,
      "consensusedBlockNumber": 38207,
      "currentView": 54477,
      "groupId": 1,
      "highestblockHash": "0x19a16e8833e671aa11431de589c866a6442ca6c8548ba40a44f50889cd785069",
      "highestblockNumber": 38206,
      "leaderFailed": false,
      "max_faulty_leader": 1,
      "nodeId": "f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13",
      "nodeNum": 4,
      "node_index": 3,
      "omitEmptyBlock": true,
      "protocolId": 65544,
      "sealer.0": "6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d",
      "sealer.1": "8a453f1328c80b908b2d02ba25adca6341b16b16846d84f903c4f4912728c6aae1050ce4f24cd9c13e010ce922d3393b846f6f5c42f6af59c65a814de733afe4",
      "sealer.2": "ed483837e73ee1b56073b178f5ac0896fa328fc0ed418ae3e268d9e9109721421ec48d68f28d6525642868b40dd26555c9148dbb8f4334ca071115925132889c",
      "sealer.3": "f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13",
      "toView": 54477
    },
    [
      {
        "nodeId": "6a99f357ecf8a001e03b68aba66f68398ee08f3ce0f0147e777ec77995369aac470b8c9f0f85f91ebb58a98475764b7ca1be8e37637dd6cb80b3355749636a3d",
        "view": 54474
      },
      {
        "nodeId": "8a453f1328c80b908b2d02ba25adca6341b16b16846d84f903c4f4912728c6aae1050ce4f24cd9c13e010ce922d3393b846f6f5c42f6af59c65a814de733afe4",
        "view": 54475
      },
      {
        "nodeId": "ed483837e73ee1b56073b178f5ac0896fa328fc0ed418ae3e268d9e9109721421ec48d68f28d6525642868b40dd26555c9148dbb8f4334ca071115925132889c",
        "view": 54476
      },
      {
        "nodeId": "f72648fe165da17a889bece08ca0e57862cb979c4e3661d6a77bcc2de85cb766af5d299fec8a4337eedd142dca026abc2def632f6e456f80230902f93e2bea13",
        "view": 54477
      }
    ]
  ]
}
]
```

### **getSyncStatus**
To run getSyncStatus to view the synchronization status.

```text
[group:1]> getSyncStatus
{
    "blockNumber":5,
    "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "isSyncing":false,
    "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
    "nodeId":"cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
    "peers":[
        {
            "blockNumber":5,
            "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
            "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
            "nodeId":"0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278"
        },
        {
            "blockNumber":5,
            "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
            "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
            "nodeId":"2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108"
        },
        {
            "blockNumber":5,
            "genesisHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
            "latestHash":"0xb99703130e24702d3b580111b0cf4e39ff60ac530561dd9eb0678d03d7acce1d",
            "nodeId":"ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
        }
    ],
    "protocolId":265,
    "txPoolSize":"0"
}
```

### **getNodeVersion**
To run getNodeVersion to view the node version.

```text
[group:1]> getNodeVersion
{
    "Build Time":"20200619 06:32:10",
    "Build Type":"Linux/clang/Release",
    "Chain Id":"1",
    "FISCO-BCOS Version":"2.5.0",
    "Git Branch":"HEAD",
    "Git Commit Hash":"72c6d770e5cf0f4197162d0e26005ec03d30fcfe",
    "Supported Version":"2.5.0"
}
```
### **getPeers**
To run getPeers to view the peers of node.

```text
[group:1]> getPeers
[
	{
		"IPAndPort":"127.0.0.1:50723",
		"nodeId":"8718579e9a6fee647b3d7404d59d66749862aeddef22e6b5abaafe1af6fc128fc33ed5a9a105abddab51e12004c6bfe9083727a1c3a22b067ddbaac3fa349f7f",
		"Topic":[

		]
	},
	{
		"IPAndPort":"127.0.0.1:50719",
		"nodeId":"697e81e512cffc55fc9c506104fb888a9ecf4e29eabfef6bb334b0ebb6fc4ef8fab60eb614a0f2be178d0b5993464c7387e2b284235402887cdf640f15cb2b4a",
		"Topic":[

		]
	},
	{
		"IPAndPort":"127.0.0.1:30304",
		"nodeId":"8fc9661baa057034f10efacfd8be3b7984e2f2e902f83c5c4e0e8a60804341426ace51492ffae087d96c0b968bd5e92fa53ea094ace8d1ba72de6e4515249011",
		"Topic":[

		]
	}
]
```
### **getGroupPeers**
To run getGroupPeers to view the list of consensus and observer node of the group where the node is located.

```text
[group:1]> getGroupPeers
[
    cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd,
    ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec,
    0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278,
    2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108
]
```
### **getGroupList**
To run getGroupList to view the list of group:
```text
[group:1]> getGroupList
[1]
```

### **getBlockHeaderByHash**
Run getBlockHeaderByHash to query the block header information based on the block hash.

parameter:
- Block hash: the hash value of the block starting with 0x
- Signature list flag: The default is false, that is, the block signature list information is not displayed in the block header information, and if set to true, the block signature list is displayed.

```text
[group:1]> getBlockHeaderByHash 0x99576e7567d258bd6426ddaf953ec0c953778b2f09a078423103c6555aa4362d
{
    "dbHash":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "extraData":[

    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0x99576e7567d258bd6426ddaf953ec0c953778b2f09a078423103c6555aa4362d",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":1,
    "parentHash":"0x4f6394763c33c1709e5a72b202ad4d7a3b8152de3dc698cef6f675ecdaf20a3b",
    "receiptsRoot":"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2",
    "sealer":"0x2",
    "sealerList":[
        "11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f",
        "78a313b426c3de3267d72b53c044fa9fe70c2a27a00af7fea4a549a7d65210ed90512fc92b6194c14766366d434235c794289d66deff0796f15228e0e14a9191",
        "95b7ff064f91de76598f90bc059bec1834f0d9eeb0d05e1086d49af1f9c2f321062d011ee8b0df7644bd54c4f9ca3d8515a3129bbb9d0df8287c9fa69552887e",
        "b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36"
    ],
    "stateRoot":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "timestamp":"0x173ad8703d6",
    "transactionsRoot":"0xb563f70188512a085b5607cac0c35480336a566de736c83410a062c9acc785ad"
}
```

### **getBlockHeaderByNumber**
Run getBlockHeaderByNumber to query the block header information according to the block height.
parameter:
- Block height
- Signature list flag: The default is false, that is, the block signature list information is not displayed in the block header information, and if set to true, the block signature list is displayed.

```text
[group:1]> getBlockHeaderByNumber 1 true
{
    "dbHash":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "extraData":[

    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0x99576e7567d258bd6426ddaf953ec0c953778b2f09a078423103c6555aa4362d",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":1,
    "parentHash":"0x4f6394763c33c1709e5a72b202ad4d7a3b8152de3dc698cef6f675ecdaf20a3b",
    "receiptsRoot":"0x69a04fa6073e4fc0947bac7ee6990e788d1e2c5ec0fe6c2436d0892e7f3c09d2",
    "sealer":"0x2",
    "sealerList":[
        "11e1be251ca08bb44f36fdeedfaeca40894ff80dfd80084607a75509edeaf2a9c6fee914f1e9efda571611cf4575a1577957edfd2baa9386bd63eb034868625f",
        "78a313b426c3de3267d72b53c044fa9fe70c2a27a00af7fea4a549a7d65210ed90512fc92b6194c14766366d434235c794289d66deff0796f15228e0e14a9191",
        "95b7ff064f91de76598f90bc059bec1834f0d9eeb0d05e1086d49af1f9c2f321062d011ee8b0df7644bd54c4f9ca3d8515a3129bbb9d0df8287c9fa69552887e",
        "b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36"
    ],
    "signatureList":[
        {
            "index":"0x3",
            "signature":"0xb5b41e49c0b2bf758322ecb5c86dc3a3a0f9b98891b5bbf50c8613a241f05f595ce40d0bb212b6faa32e98546754835b057b9be0b29b9d0c8ae8b38f7487b8d001"
        },
        {
            "index":"0x0",
            "signature":"0x411cb93f816549eba82c3bf8c03fa637036dcdee65667b541d0da06a6eaea80d16e6ca52bf1b08f77b59a834bffbc124c492ea7a1601d0c4fb257d97dc97cea600"
        },
        {
            "index":"0x1",
            "signature":"0xea3c27c2a1486c7942c41c4dc8f15fbf9a668aff2ca40f00701d73fa659a14317d45d74372d69d43ced8e81f789e48140e7fa0c61997fa7cde514c654ef9f26d00"
        }
    ],
    "stateRoot":"0x0000000000000000000000000000000000000000000000000000000000000000",
    "timestamp":"0x173ad8703d6",
    "transactionsRoot":"0xb563f70188512a085b5607cac0c35480336a566de736c83410a062c9acc785ad"
}
```

### **getBlockByHash**
To run getBlockByHash to view block information according to the block hash.
Parameter:
- Block hash: The hash starting with 0x.
- Transaction sign: to set it false by default, the transaction in the block only displays the hash. To set it true, it displays the transaction specific information.

```text
[group:1]> getBlockByHash 0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855
{
    "extraData":[

    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":"0x1",
    "parentHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "sealer":"0x0",
    "sealerList":[
        "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
        "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
        "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
        "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],
    "stateRoot":"0x9711819153f7397ec66a78b02624f70a343b49c60bc2f21a77b977b0ed91cef9",
    "timestamp":"0x1692f119c84",
    "transactions":[
        "0xa14638d47cc679cf6eeb7f36a6d2a30ea56cb8dcf0938719ff45023a7a8edb5d"
    ],
    "transactionsRoot":"0x516787f85980a86fd04b0e9ce82a1a75950db866e8cdf543c2cae3e4a51d91b7"
}
[group:1]> getBlockByHash 0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855 true
{
    "extraData":[

    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":"0x1",
    "parentHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "sealer":"0x0",
    "sealerList":[
        "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
        "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
        "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
        "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],
    "stateRoot":"0x9711819153f7397ec66a78b02624f70a343b49c60bc2f21a77b977b0ed91cef9",
    "timestamp":"0x1692f119c84",
    "transactions":[
        {
            "blockHash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
            "blockNumber":"0x1",
            "from":"0x7234c32327795e4e612164e3442cfae0d445b9ad",
            "gas":"0x1c9c380",
            "gasPrice":"0x1",
            "hash":"0xa14638d47cc679cf6eeb7f36a6d2a30ea56cb8dcf0938719ff45023a7a8edb5d",
            "input":"0x608060405234801561001057600080fd5b506040805190810160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6102d7806101166000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634ed3885e146100515780636d4ce63c146100ba575b600080fd5b34801561005d57600080fd5b506100b8600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061014a565b005b3480156100c657600080fd5b506100cf610164565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561010f5780820151818401526020810190506100f4565b50505050905090810190601f16801561013c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b8060009080519060200190610160929190610206565b5050565b606060008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101fc5780601f106101d1576101008083540402835291602001916101fc565b820191906000526020600020905b8154815290600101906020018083116101df57829003601f168201915b5050505050905090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024757805160ff1916838001178555610275565b82800160010185558215610275579182015b82811115610274578251825591602001919060010190610259565b5b5090506102829190610286565b5090565b6102a891905b808211156102a457600081600090555060010161028c565b5090565b905600a165627a7a72305820fd74886bedbe51a7f3d834162de4d9af7f276c70133e04fd6776b5fbdd3653000029",
            "nonce":"0x3443a1391c9c29f751e8350304efb310850b8afbaa7738f5e89ddfce79b1d6",
            "to":null,
            "transactionIndex":"0x0",
            "value":"0x0"
        }
    ],
    "transactionsRoot":"0x516787f85980a86fd04b0e9ce82a1a75950db866e8cdf543c2cae3e4a51d91b7"
}
```
### **getBlockByNumber**
To run getBlockByNumber to view block information according to the block number.
Parameter:
- Block number: decimal integer.
- Transaction sign: to set it false by default, the transaction in the block only displays the hash. To set it true, it displays the transaction specific information.

```text
[group:1]> getBlockByNumber 1
{
    "extraData":[

    ],
    "gasLimit":"0x0",
    "gasUsed":"0x0",
    "hash":"0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855",
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "number":"0x1",
    "parentHash":"0xeccad5274949b9d25996f7a96b89c0ac5c099eb9b72cc00d65bc6ef09f7bd10b",
    "sealer":"0x0",
    "sealerList":[
        "0471101bcf033cd9e0cbd6eef76c144e6eff90a7a0b1847b5976f8ba32b2516c0528338060a4599fc5e3bafee188bca8ccc529fbd92a760ef57ec9a14e9e4278",
        "2b08375e6f876241b2a1d495cd560bd8e43265f57dc9ed07254616ea88e371dfa6d40d9a702eadfd5e025180f9d966a67f861da214dd36237b58d72aaec2e108",
        "cf93054cf524f51c9fe4e9a76a50218aaa7a2ca6e58f6f5634f9c2884d2e972486c7fe1d244d4b49c6148c1cb524bcc1c99ee838bb9dd77eb42f557687310ebd",
        "ed1c85b815164b31e895d3f4fc0b6e3f0a0622561ec58a10cc8f3757a73621292d88072bf853ac52f0a9a9bbb10a54bdeef03c3a8a42885fe2467b9d13da9dec"
    ],
    "stateRoot":"0x9711819153f7397ec66a78b02624f70a343b49c60bc2f21a77b977b0ed91cef9",
    "timestamp":"0x1692f119c84",
    "transactions":[
        "0xa14638d47cc679cf6eeb7f36a6d2a30ea56cb8dcf0938719ff45023a7a8edb5d"
    ],
    "transactionsRoot":"0x516787f85980a86fd04b0e9ce82a1a75950db866e8cdf543c2cae3e4a51d91b7"
}
```
### **getBlockHashByNumber**
To run getBlockHashByNumber to get hash through block number
Parameter:
- Block number: decimal integer.

```text
[group:1]> getBlockHashByNumber 1
0xf6afbcc3ec9eb4ac2c2829c2607e95ea0fa1be914ca1157436b2d3c5f1842855
```
### **getTransactionByHash**
To run getTransactionByHash to check the transaction information through transaction hash.
Parameter:
- Transaction hash: the transaction hash starting with 0x.

```text
[group:1]> getTransactionByHash 0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac
{
	"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"blockNumber":"0x5a",
	"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
	"gas":"0x1c9c380",
	"gasPrice":"0x1",
	"hash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
	"input":"0x10009562616c6963650000000000000000000000000000000000000000000000000000006a6f726500000000000000000000000000000000000000000000000000000000",
	"nonce":"0x18711fff2ea68dc8b8dce4a3d3845c62a0630766a448bd9453a9127efe6f9e5",
	"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
	"transactionIndex":"0x0",
	"value":"0x0"
}
```
### **getTransactionByBlockHashAndIndex**
To run getTransactionByBlockHashAndIndex to inquire transaction information through block hash and transaction index.
Parameter:
- Block hash: the transaction hash starting with 0x.
- Transaction index: decimal integer.

```text
[group:1]> getTransactionByBlockHashAndIndex 0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02 0
{
	"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"blockNumber":"0x5a",
	"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
	"gas":"0x1c9c380",
	"gasPrice":"0x1",
	"hash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
	"input":"0x10009562616c6963650000000000000000000000000000000000000000000000000000006a6f726500000000000000000000000000000000000000000000000000000000",
	"nonce":"0x18711fff2ea68dc8b8dce4a3d3845c62a0630766a448bd9453a9127efe6f9e5",
	"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
	"transactionIndex":"0x0",
	"value":"0x0"
}

```
### **getTransactionByBlockNumberAndIndex**
To run getTransactionByBlockNumberAndIndex to inquire transaction information through block number and transaction index.
Parameter:
- Block number: decimal integer.
- Transaction index: decimal integer.

```text
[group:1]> getTransactionByBlockNumberAndIndex 2 0
{
	"blockHash":"0x77e5b6d799edabaeae654ac5cea9baacd6f8e7ace33531d40c7ed65192de1f02",
	"blockNumber":"0x5a",
	"from":"0x7a5b31b49c6e944e9e1768785b1bc9a96cea0c17",
	"gas":"0x1c9c380",
	"gasPrice":"0x1",
	"hash":"0xed82e2cda98db8614677aba1fa8a795820bd7f68a5919a2f85018ba8c10952ac",
	"input":"0x10009562616c6963650000000000000000000000000000000000000000000000000000006a6f726500000000000000000000000000000000000000000000000000000000",
	"nonce":"0x18711fff2ea68dc8b8dce4a3d3845c62a0630766a448bd9453a9127efe6f9e5",
	"to":"0x738eedd873bb9722173194ab990c5b9a6c0beb25",
	"transactionIndex":"0x0",
	"value":"0x0"
}
```
### **getTransactionReceipt**
To run getTransactionReceipt to inquire transaction receipt through transaction hash.
Parameter:
- Transaction hash: the transaction hash starting with 0x.
- contract name: Optional. The contract name generated by transaction receipt. To use this parameter can parse and output the event log in the transaction receipt.
- event name: optional. Event Name. To specify this parameter to output the specified event log information.
- event index number: optional. Event index. To specify this parameter to output the event log information of the specified event index location.

```text
[group:1]> getTransactionReceipt 0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1
{
    "blockHash":"0x68a1f47ca465acc89edbc24115d1b435cb39fa0def53e8d0ad8090cf1827cafd",
    "blockNumber":"0x5",
    "contractAddress":"0x0000000000000000000000000000000000000000",
    "from":"0xc44e7a8a4ae20d6afaa43221c6120b5e1e9f9a72",
    "gasUsed":"0x8be5",
    "logs":[
        {
            "address":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
            "data":"0x0000000000000000000000000000000000000000000000000000000000000001",
            "topics":[
                "0x66f7705280112a4d1145399e0414adc43a2d6974b487710f417edcf7d4a39d71"
            ]
        }
    ],
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000400000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000",
    "output":"0x0000000000000000000000000000000000000000000000000000000000000001",
    "status":"0x0",
    "to":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
    "transactionHash":"0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1",
    "transactionIndex":"0x0"
}

[group:1]> getTransactionReceipt 0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1 TableTest
{
    "blockHash":"0x68a1f47ca465acc89edbc24115d1b435cb39fa0def53e8d0ad8090cf1827cafd",
    "blockNumber":"0x5",
    "contractAddress":"0x0000000000000000000000000000000000000000",
    "from":"0xc44e7a8a4ae20d6afaa43221c6120b5e1e9f9a72",
    "gasUsed":"0x8be5",
    "logs":[
        {
            "address":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
            "data":"0x0000000000000000000000000000000000000000000000000000000000000001",
            "topics":[
                "0x66f7705280112a4d1145399e0414adc43a2d6974b487710f417edcf7d4a39d71"
            ]
        }
    ],
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000400000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000",
    "output":"0x0000000000000000000000000000000000000000000000000000000000000001",
    "status":"0x0",
    "to":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
    "transactionHash":"0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1",
    "transactionIndex":"0x0"
}
---------------------------------------------------------------------------------------------
Event logs
---------------------------------------------------------------------------------------------
InsertResult index: 0
count = 1
---------------------------------------------------------------------------------------------

[group:1]> getTransactionReceipt 0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1 TableTest InsertResult
{
    "blockHash":"0x68a1f47ca465acc89edbc24115d1b435cb39fa0def53e8d0ad8090cf1827cafd",
    "blockNumber":"0x5",
    "contractAddress":"0x0000000000000000000000000000000000000000",
    "from":"0xc44e7a8a4ae20d6afaa43221c6120b5e1e9f9a72",
    "gasUsed":"0x8be5",
    "logs":[
        {
            "address":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
            "data":"0x0000000000000000000000000000000000000000000000000000000000000001",
            "topics":[
                "0x66f7705280112a4d1145399e0414adc43a2d6974b487710f417edcf7d4a39d71"
            ]
        }
    ],
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000400000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000",
    "output":"0x0000000000000000000000000000000000000000000000000000000000000001",
    "status":"0x0",
    "to":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
    "transactionHash":"0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1",
    "transactionIndex":"0x0"
}
---------------------------------------------------------------------------------------------
Event logs
---------------------------------------------------------------------------------------------
InsertResult index: 0
count = 1
---------------------------------------------------------------------------------------------

[group:1]> getTransactionReceipt 0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1 TableTest InsertResult 0
{
    "blockHash":"0x68a1f47ca465acc89edbc24115d1b435cb39fa0def53e8d0ad8090cf1827cafd",
    "blockNumber":"0x5",
    "contractAddress":"0x0000000000000000000000000000000000000000",
    "from":"0xc44e7a8a4ae20d6afaa43221c6120b5e1e9f9a72",
    "gasUsed":"0x8be5",
    "logs":[
        {
            "address":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
            "data":"0x0000000000000000000000000000000000000000000000000000000000000001",
            "topics":[
                "0x66f7705280112a4d1145399e0414adc43a2d6974b487710f417edcf7d4a39d71"
            ]
        }
    ],
    "logsBloom":"0x00000000000000000000000000000000000000000000000000000000000000000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000400000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000080000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000040000000000000000000000000000000000000000000000000000000000000000000",
    "output":"0x0000000000000000000000000000000000000000000000000000000000000001",
    "status":"0x0",
    "to":"0xd653139b9abffc3fe07573e7bacdfd35210b5576",
    "transactionHash":"0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1",
    "transactionIndex":"0x0"
}
---------------------------------------------------------------------------------------------
Event logs
---------------------------------------------------------------------------------------------
InsertResult index: 0
count = 1
---------------------------------------------------------------------------------------------
```
### **getPendingTransactions**
To run getPendingTransactions to inquire the transactions waiting to be processed.

```text
[group:1]> getPendingTransactions
[]
```

### **getPendingTxSize**
To run getPendingTxSize to inquire the number of transactions waiting to be processed.

```text
[group:1]> getPendingTxSize
0
```

### **getCode**
To run getCode to inquire contract code according contract address.
Parameter:
- Contract address: The contract address starting with 0x(To deploy contract can get contract address).

```text
[group:1]> getCode 0x97b8c19598fd781aaeb53a1957ef9c8acc59f705
0x60606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806366c99139146100465780636d4ce63c14610066575bfe5b341561004e57fe5b610064600480803590602001909190505061008c565b005b341561006e57fe5b61007661028c565b6040518082815260200191505060405180910390f35b8060006001015410806100aa57506002600101548160026001015401105b156100b457610289565b806000600101540360006001018190555080600260010160008282540192505081905550600480548060010182816100ec919061029a565b916000526020600020906004020160005b608060405190810160405280604060405190810160405280600881526020017f32303137303431330000000000000000000000000000000000000000000000008152508152602001600060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600260000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200185815250909190915060008201518160000190805190602001906101ec9291906102cc565b5060208201518160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550606082015181600301555050505b50565b600060026001015490505b90565b8154818355818115116102c7576004028160040283600052602060002091820191016102c6919061034c565b5b505050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061030d57805160ff191683800117855561033b565b8280016001018555821561033b579182015b8281111561033a57825182559160200191906001019061031f565b5b50905061034891906103d2565b5090565b6103cf91905b808211156103cb57600060008201600061036c91906103f7565b6001820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff02191690556002820160006101000a81549073ffffffffffffffffffffffffffffffffffffffff0219169055600382016000905550600401610352565b5090565b90565b6103f491905b808211156103f05760008160009055506001016103d8565b5090565b90565b50805460018160011615610100020316600290046000825580601f1061041d575061043c565b601f01602090049060005260206000209081019061043b91906103d2565b5b505600a165627a7a723058203c1f95b4a803493db0120df571d9f5155734152548a532412f2f9fa2dbe1ac730029
```

### **getTotalTransactionCount**
To run getTotalTransactionCount to inquire the current block number and the total number of transaction.

```text
[group:1]> getTotalTransactionCount
{
	"blockNumber":1,
	"txSum":1,
	"failedTxSum":0
}
```
### **deploy**
To run deploy to deploy contract. (By default, HelloWorld contract and TableTest.sol are provided for example)
Parameter:
- Contract name: deployment contract name (can be suffixed with .sol). It can name as either HelloWorld or HelloWorld.sol.

```text
# To deploy HelloWorld contract
[group:1]> deploy HelloWorld.sol
contract address:0xb3c223fc0bf6646959f254ac4e4a7e355b50a344

# To deploy TableTest contract
[group:1]> deploy TableTest.sol
contract address:0x3554a56ea2905f366c345bd44fa374757fb4696a
```

**Note:**
- For deploying a user-written contract, we just need to place the solidity contract file in the `contracts/solidity/` directory of the console root, and then deploy it. Press the tab key to search for the contract name in the `contracts/solidity` directory.
- If the contract need to be deployed refers to other contracts or libraries, the reference format is `import "./XXX.sol";`. The related contracts and libraries are placed in the `contracts/solidity/` directory.
- If contract references the library library, the name of library file must start with `Lib` string to distinguish between the normal contract and the library file. Library files cannot be deployed and called separately.
- **Because FISCO BCOS has removed the transfer payment logic of Ethereum, the solidity contract does not support using `payable` keyword. This keyword will cause the Java contract file converted by solidity contract to fail at compilation. **


### **getDeployLog**

Run getDeployLog to query the log information of the contract deployed by **current console** in the group. The log information includes the time of deployment contract, the group ID, the contract name, and the contract address. parameter:

- Log number: optional. To return the latest log information according to the expected value entered. When the actual number is less than the expected value, it returns by the actual number. When the expected value is not given, it returns by the latest 20 log information by default.

```text
[group:1]> getDeployLog 2

2019-05-26 08:37:03  [group:1]  HelloWorld            0xc0ce097a5757e2b6e189aa70c7d55770ace47767
2019-05-26 08:37:45  [group:1]  TableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576

[group:1]> getDeployLog 1

2019-05-26 08:37:45  [group:1]  TableTest             0xd653139b9abffc3fe07573e7bacdfd35210b5576
```

**Note:** If you want to see all the deployment contract log information, please check the `deploylog.txt` file in the `console` directory. The file only stores the log records of the last 10,000 deployment contracts.

### **call**
To run call to call contract.
Parameter:
- Contract name: the contract name of the deployment (can be suffixed with .sol).
- Contract address: the address obtained by the deployment contract. The contract address can omit the prefix 0. For example, 0x000ac78 can be abbreviated as 0xac78.
- Contract interface name: the called interface name.
- Parameter: determined by contract interface parameters.

**Parameters are separated by spaces. The string and byte type parameters need to be enclosed in double quotes; array parameters need to be enclosed in brackets, such as [1,2,3]; array is a string or byte type and needs to be enclosed in double quotation marks, such as ["alice", "bob"]. Note that there are no spaces in the array parameters; boolean types are true or false. **


```text
​```text
# To call the get interface of HelloWorld to get the name string
[group:1]> call HelloWorld.sol 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 get
Hello, World!

# To call the set interface of HelloWorld to set the name string
[group:1]> call HelloWorld.sol 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 set "Hello, FISCO BCOS"
transaction hash:0xa7c7d5ef8d9205ce1b228be1fe90f8ad70eeb6a5d93d3f526f30d8f431cb1e70

# To call the get interface of HelloWorld to get the name string for checking whether the settings take effect
[group:1]> call HelloWorld.sol 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 get
Hello, FISCO BCOS

# To call the insert interface of TableTest to insert the record, the fields are name, item_id, item_name
[group:1]> call TableTest.sol 0xd653139b9abffc3fe07573e7bacdfd35210b5576 insert "fruit" 1 "apple"
transaction hash:0x6393c74681f14ca3972575188c2d2c60d7f3fb08623315dbf6820fc9dcc119c1
---------------------------------------------------------------------------------------------
Event logs
---------------------------------------------------------------------------------------------
InsertResult index: 0
count = 1
---------------------------------------------------------------------------------------------

# To call TableTest's select interface to inquiry records
[group:1]> call TableTest.sol 0xd653139b9abffc3fe07573e7bacdfd35210b5576 select "fruit"
[[fruit], [1], [apple]]
```
**Note:** TableTest.sol contract code[Reference here](../manual/smart_contract.html#solidity)。

### **deployByCNS**

Run deployByCNS and deploy the contract with [CNS](../design/features/cns_contract_name_service.md). Contracts deployed with CNS can be called directly with the contract name.

Parameter:

- Contract name: deployable contract name.
- Contract version number: deployable contract version number(the length cannot exceed 40).
```text
# To deploy HellowWorld contract 1.0 version
[group:1]> deployByCNS HelloWorld.sol 1.0
contract address:0x3554a56ea2905f366c345bd44fa374757fb4696a

# To deploy HellowWorld contract 2.0 version
[group:1]> deployByCNS HelloWorld.sol 2.0
contract address:0x07625453fb4a6459cbf14f5aa4d574cae0f17d92

# To deploy TableTest contract
[group:1]> deployByCNS TableTest.sol 1.0
contract address:0x0b33d383e8e93c7c8083963a4ac4a58b214684a8
```

**Note:**
- For deploying the contracts compiled by users only needs to place the solidity contract file in the `contracts/solidity/` directory of the console root and to deploy it. Press tab key to search for the contract name in the `contracts/solidity/` directory.
- If the contract to be deployed references other contracts or libraries, the reference format is `import "./XXX.sol";`. The related contract and library are placed in the `contracts/solidity/` directory.
- **Because FISCO BCOS has removed the transfer payment logic of Ethereum, the solidity contract does not support using `payable` keyword. This keyword will cause the Java contract file converted by solidity contract to fail at compilation. **

### **queryCNS**

Run queryCNS and query the CNS table record information (the mapping of contract name and contract address) according to the contract name and contract version number (optional parameter).

Parameter:
- Contract name: deployable contract name.
- Contract version number: (optional) deployable contract version number.
```text
[group:1]> queryCNS HelloWorld.sol
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x3554a56ea2905f366c345bd44fa374757fb4696a  |
---------------------------------------------------------------------------------------------

[group:1]> queryCNS HelloWorld 1.0
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x3554a56ea2905f366c345bd44fa374757fb4696a  |
---------------------------------------------------------------------------------------------
```
### **callByCNS**
To run deployByCNS and deploy the contract with CNS.
Parameter:
- Contract name and contract version number: The contract name and contract version number are separated by colon, such as `HelloWorld:1.0` or `HelloWorld.sol:1.0`. When the contract version number is omitted like `HelloWorld` or `HelloWorld.sol`, the latest version of the contract is called.
- Contract interface name: The called contract interface name.
- Parameter: is determined by the parameter of contract interface. **The parameters are separated by spaces, where the string and byte type parameters need to be enclosed in double quotation marks; the array parameters need to be enclosed in brackets, such as [1, 2, 3]. The array is a string or byte type with double quotation marks such as ["alice", "bob"]; the boolean type is true or false.**

```text
# To call the HelloWorld contract 1.0 version to set the name string by the set interface
[group:1]> callByCNS HelloWorld:1.0 set "Hello,CNS"
transaction hash:0x80bb37cc8de2e25f6a1cdcb6b4a01ab5b5628082f8da4c48ef1bbc1fb1d28b2d

# To call the HelloWorld contract 2.0 version to set the name string by the set interface
[group:1]> callByCNS HelloWorld:2.0 set "Hello,CNS2"
transaction hash:0x43000d14040f0c67ac080d0179b9499b6885d4a1495d3cfd1a79ffb5f2945f64

# To call the HelloWorld contract 1.0 version to get the name string by the get interface
[group:1]> callByCNS HelloWorld:1.0 get
Hello,CNS

# To call the HelloWorld contract 2.0 version to get the name string by the get interface
[group:1]> callByCNS HelloWorld get
Hello,CNS2
```

### **addSealer**
To run addSealer to add the node as a consensus node.
Parameter:
- node's nodeId
```text
[group:1]> addSealer ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":0,
	"msg":"success"
}
```

### **addObserver**
To run addObserver to add the node as an observed node.
Parameter:
- node's nodeId
```text
[group:1]> addObserver ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":0,
	"msg":"success"
}
```

### **removeNode**
To run removeNode to exit the node. The exit node can be added as a consensus node by the addSealer command or can be added as an observation node by the addObserver command.
Parameter:
- node's nodeId
```text
[group:1]> removeNode ea2ca519148cafc3e92c8d9a8572b41ea2f62d0d19e99273ee18cccd34ab50079b4ec82fe5f4ae51bd95dd788811c97153ece8c05eac7a5ae34c96454c4d3123
{
	"code":0,
	"msg":"success"
}
```
### **setSystemConfigByKey**
To run setSystemConfigByKey to set the system configuration in key-value pairs. The currently system configuration supports `tx_count_limit`, `tx_gas_limit`, `rpbft_epoch_sealer_num` and `rpbft_epoch_block_num`. The key name of these two configuration can be complemented by the tab key:

* tx_count_limit: block maximum number of packaged transactions
* tx_gas_limit: The maximum number of gas allowed to be consumed
* rpbft_epoch_sealer_num: rPBFT system configuration, the number of consensus nodes selected in a consensus epoch
* rpbft_epoch_block_num: rPBFT system configuration, number of blocks generated in one consensus epoch
* consensus_timeout: During the PBFT consensus process, the timeout period for each block execution, the default is 3s, the unit is seconds

Parameters:

- key
- value
```text
[group:1]> setSystemConfigByKey tx_count_limit 100
{
	"code":0,
	"msg":"success"
}
```
### **getSystemConfigByKey**

To run getSystemConfigByKe to inquire the value of the system configuration according to the key.
Parameter:

- key
```text
[group:1]> getSystemConfigByKey tx_count_limit
100
```
### **grantPermissionManager**

Run grantPermissionManager to grant the account's chain administrator privileges. parameter:

- account address
```text
[group:1]> grantPermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```

**Note: For an example of the using permission control related commands, refer to [Permission Control Manual Document](./permission_control.md). **

### **listPermissionManager**
To run listPermissionManager to inquire the list of permission records with administrative privileges.
```text
[group:1]> listPermissionManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokePermissionManager**
To run revokePermissionManager to revoke the permission management of the external account address.
parameter:
- account address
```text
[group:1]> revokePermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantUserTableManager**

Run grantUserTableManager to grant the account to write to the user table.

parameter:

- table name
- account address
```text
[group:1]> grantUserTableManager t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listUserTableManager**

Run listUserTableManager to query the account's table that has writing permission to the user table.

parameter:
- table name
```text
[group:1]> listUserTableManager t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeUserTableManager**

Run revokeUserTableManager to revoke the account's writing permission from the user table.

parameter:

- table name
- account address

```text
[group:1]> revokeUserTableManager t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantDeployAndCreateManager**
Run grantDeployAndCreateManager to grant the account's permission of deployment contract and user table creation.

parameter:

- account address
```text
[group:1]> grantDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listDeployAndCreateManager**

Run listDeployAndCreateManager to query the account's permission of deployment contract and user table creation.

```text
[group:1]> listDeployAndCreateManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeDeployAndCreateManager**

Run revokeDeployAndCreateManager to revoke the account's permission of deployment contract and user table creation.

parameter:

- account address
```text
[group:1]> revokeDeployAndCreateManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantNodeManager**

Run grantNodeManager to grant the account's node management permission.

parameter:

- account address
```text
[group:1]> grantNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listNodeManager**

Run the listNodeManager to query the list of accounts that have node management.

```text
[group:1]> listNodeManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeNodeManager**

Run revokeNodeManager to revoke the account's node management permission.

parameter:
- account address
```text
[group:1]> revokeNodeManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantCNSManager**

Run grantCNSManager to grant the account's permission of using CNS.
parameter:

- account address
```text
[group:1]> grantCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listCNSManager**

Run listCNSManager to query the list of accounts that have CNS.

```text
[group:1]> listCNSManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeCNSManager**

Run revokeCNSManager to revoke the account's permission of using CNS.
parameter:
- account address
```text
[group:1]> revokeCNSManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **grantSysConfigManager**

Run grantSysConfigManager to grant the account's permission of modifying system parameter.
parameter:

- account address
```text
[group:1]> grantSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```
### **listSysConfigManager**

Run listSysConfigManager to query the list of accounts that have modified system parameters.

```text
[group:1]> listSysConfigManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                      2                      |
---------------------------------------------------------------------------------------------
```
### **revokeSysConfigManager**

Run revokeSysConfigManager to revoke the account's permission of modifying system parameter. parameter:

- account address
```text
[group:1]> revokeSysConfigManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```

### **grantContractWritePermission**

Run grantContractWritePermissio to grant the account the contract write permission. parameters:

- contract address
- account address

```bash
[group:1]> grantContractWritePermission 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```

### **listContractWritePermission**

Run listContractWritePermission to query the account list which have write permission of the contract. parameters:

- contract address

```bash
[group:1]> listContractWritePermission 0xc0ce097a5757e2b6e189aa70c7d55770ace47767
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d  |                     11                      |
---------------------------------------------------------------------------------------------
```

### **revokeContractWritePermission**

Run revokeContractWritePermission to Revoke the account the contract write permission. parameters:

- 合约地址
- account address

```bash
[group:1]> revokeContractWritePermission 0xc0ce097a5757e2b6e189aa70c7d55770ace47767 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":0,
	"msg":"success"
}
```


### **quit**
To run quit, q or exit to exit the console.
```text
quit
```

### **[create sql]**

Run create sql statement to create a user table in mysql statement form.

```text
# Create user table t_demo whose primary key is name and other fields are item_id and item_name
[group:1]> create table t_demo(name varchar, item_id varchar, item_name varchar, primary key(name))
Create 't_demo' Ok.
```

**Note:**
- The field types for creating table are all string types. Even if other field types of the database are provided, the field types have to be set according to the string type.
- The primary key field must be specified. For example, to create a t_demo table with the primary key field as name.
- The primary key of the table has different concept from the primary key in the relational database. Here, the value of the primary key is not unique, and the primary key value needs to be passed when the blockchain underlying platform is handling records.
- You can specify the field as the primary key, but the setting fields such as self-incrementing, non-empty, indexing, etc do not work.


### **desc**

Run desc statement to query the field information of the table in mysql statement form.

```text
# query the field information of the t_demo table. you can view the primary key name and other field names of the table.

[group:1]> desc t_demo
{
    "key":"name",
    "valueFields":"item_id,item_name"
}
```
### **[insert sql]**

Run insert sql statement to insert the record in the mysql statement form.

```text
[group:1]> insert into t_demo (name, item_id, item_name) values (fruit, 1, apple1)
Insert OK, 1 row affected.
```
**Note:**
- must insert a record sql statement with the primary key field value of the table.
- The enter values with punctuation, spaces, or strings containing letters starting with a number requires double quotation marks, and no more double quotation marks are allowed inside.

### **[select sql]**

Run select sql statement to query the record in mysql statement form.

```text
# query the records contain all fields
select * from t_demo where name = fruit
{item_id=1, item_name=apple1, name=fruit}
1 row in set.

# query the records contain the specified fields
[group:1]> select name, item_id, item_name from t_demo where name = fruit
{name=fruit, item_id=1, item_name=apple1}
1 row in set.

# insert a new record
[group:1]> insert into t_demo values (fruit, 2, apple2)
Insert OK, 1 row affected.

# use the keyword 'and' to connect multiple query condition
[group:1]> select * from t_demo where name = fruit and item_name = apple2
{item_id=2, item_name=apple2, name=fruit}
1 row in set.

# use limit field to query the first line of records. If the offset is not provided, it is 0 by default.
[group:1]> select * from t_demo where name = fruit limit 1
{item_id=1, item_name=apple1, name=fruit}
1 row in set.

# use limit field to query the second line record. The offset is 1
[group:1]> select * from t_demo where name = fruit limit 1,1
{item_id=2, item_name=apple2, name=fruit}
1 rows in set.
```
**Note:**
- For querying the statement recording sql, the primary key field value of the table in the where clause must be provided.
- The limit field in the relational database can be used. Providing two parameters which are offset and count.
- The where clause only supports the keyword 'and'. Other keywords like 'or', 'in', 'like', 'inner', 'join', 'union', subquery, multi-table joint query, and etc. are not supported.
- The enter values with punctuation, spaces, or strings containing letters starting with a number requires double quotation marks, and no more double quotation marks are allowed inside.


### **[update sql]**

Run update sql statement to update the record in mysql statement form.

```text
[group:1]> update t_demo set item_name = orange where name = fruit and item_id = 1
Update OK, 1 row affected.
```
**Note:**
- For updating the where clause of recording sql statement, the primary key field value of the table in the where clause must be provided.
- The enter values with punctuation, spaces, or strings containing letters starting with a number requires double quotation marks, and no more double quotation marks are allowed inside.

### **[delete sql]**

Run delete sql statement to delete the record in mysql statement form.

```text
[group:1]> delete from t_demo where name = fruit and item_id = 1
Remove OK, 1 row affected.
```
**Note:**
- For deleting the where clause of recording sql statement, the primary key field value of the table in the where clause must be provided.
- The enter values with punctuation, spaces, or strings containing letters starting with a number requires double quotation marks, and no more double quotation marks are allowed inside.

```eval_rst
.. important::
   The executing  of the `freezeContract`/`unfreezeContract`/`grantContractStatusManager` commands for contract management should specify the private key to start the console for permission.This private key is also the account private key used to deploy the specified contract. So a private key should be specified to launch the console when deploying the contract.
```

### **freezeContract**
Run freezeContract to freeze contract according contract address.
Parameter:
- Contract address: To deploy contract can get contract address. The prefix of 0x is not necessary.

```text
[group:1]> freezeContract 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **unfreezeContract**
Run unfreezeContract to unfreeze contract according contract address.
Parameter:
- Contract address: To deploy contract can get contract address. The prefix of 0x is not necessary.

```text
[group:1]> unfreezeContract 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **grantContractStatusManager**
Run grantCNSManager to grant the account's permission of contract status managememt.
Parameter:
- Contract address: To deploy contract can get contract address. The prefix of 0x is not necessary.
- Account address: tx.origin. The prefix of 0x is not necessary.

```text
[group:1]> grantContractStatusManager 0x30d2a17b6819f0d77f26dd3a9711ae75c291f7f1 0x965ebffc38b309fa706b809017f360d4f6de909a
{
    "code":0,
    "msg":"success"
}
```

### **getContractStatus**
To run getContractStatus to query contract status according contract address.
Parameter:
- Contract address: To deploy contract can get contract address. The prefix of 0x is not necessary.

```text
[group:1]> getContractStatus 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
The contract is available.
```

### **listContractStatusManager**
To run listContractStatusManager to query a list of authorized accounts that can manage a specified contract.
Parameter:
- Contract address: To deploy contract can get contract address. The prefix of 0x is not necessary.

```text
[group:1]> listContractStatusManager 0x30d2a17b6819f0d77f26dd3a9711ae75c291f7f1
[
    "0x0cc9b73b960323816ac5f52806257184c08b5ac0",
    "0x965ebffc38b309fa706b809017f360d4f6de909a"
]
```

### grantCommitteeMember

grant account with Committee Member permission. Parameters:

- account address

```bash
[group:1]> grantCommitteeMember 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a
{
    "code":0,
    "msg":"success"
}
```

### revokeCommitteeMember

revoke account's Committee Member permission, parameters:

- account address

```bash
[group:1]> revokeCommitteeMember 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a
{
    "code":0,
    "msg":"success"
}
```

### listCommitteeMembers

```bash
[group:1]> listCommitteeMembers
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a  |                      1                      |
| 0x85961172229aec21694d742a5bd577bedffcfec3  |                      2                      |
---------------------------------------------------------------------------------------------
```

### updateThreshold

vote to modify the votes threshold, Parameters:

- threshold:[0,99]

```bash
[group:1]> updateThreshold 75
{
    "code":0,
    "msg":"success"
}
```

### queryThreshold

query votes threshold

```bash
[group:1]> queryThreshold
Effective threshold : 50%
```

### queryCommitteeMemberWeight

```bash
[group:1]> queryCommitteeMemberWeight 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a
Account: 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a Weight: 1
```

### updateCommitteeMemberWeight

update Committee Member's votes. Parameters：

- account address
- votes

```bash
[group:1]> updateCommitteeMemberWeight 0x61d88abf7ce4a7f8479cff9cc1422bef2dac9b9a 2
{
    "code":0,
    "msg":"success"
}
```


### grantOperator

grantOperator, committee member's permission, parameters:

- account address

```bash
[group:1]> grantOperator 0x283f5b859e34f7fd2cf136c07579dcc72423b1b2
{
    "code":0,
    "msg":"success"
}
```

### revokeOperator

revokeOperator, committee member's permission, parameters:

- account address

```bash
[group:1]> revokeOperator 0x283f5b859e34f7fd2cf136c07579dcc72423b1b2
{
    "code":0,
    "msg":"success"
}
```

### listOperators

list address who has operator permission。

```bash
[group:1]> listOperators
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0x283f5b859e34f7fd2cf136c07579dcc72423b1b2  |                      1                      |
---------------------------------------------------------------------------------------------
```

### **freezeAccount**
Run freezeAccount to freeze account according account address.
Parameter:
- account address: tx.origin. The prefix of 0x is necessary.

```text
[group:1]> freezeAccount 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **unfreezeAccount**
Run unfreezeAccount to unfreeze account according account address.
Parameter:
- account address: tx.origin. The prefix of 0x is necessary.

```text
[group:1]> unfreezeAccount 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
{
    "code":0,
    "msg":"success"
}
```

### **getAccountStatus**
Run getAccountStatus to get status of the account according account address.
Parameter:
- account address: tx.origin. The prefix of 0x is necessary.

```text
[group:1]> getAccountStatus 0xcc5fc5abe347b7f81d9833f4d84a356e34488845
The account is available.
```

## Appendix: Java environment configuration

### Install Java in ubuntu environment
```bash
# Install the default Java version (Java 8 version or above)
sudo apt install -y default-jdk
# query Java version
java -version
```

### Install Java in CentOS environment
**Note: the OpenJDK under CentOS does not work properly and needs to be replaced with the OracleJDK.**
```bash
# To create new folder to install Java 8 version or above. To put the downloaded jdk in the software directory
# Download Java 8 version or above from Oracle official website (https://www.oracle.com/technetwork/java/javase/downloads/index.html). For example, to download jdk-8u201-linux-x64.tar.gz
$ mkdir /software
# To unzip jdk
$ tar -zxvf jdk-8u201-linux-x64.tar.gz
# To configure the Java environment and edit the /etc/profile file.
$ vim /etc/profile
# After opening the file, to enter the following three sentences into the file and exit
export JAVA_HOME=/software/jdk-8u201-linux-x64.tar.gz
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
# profile takes effect
$ source /etc/profile
# To inquire the Java version. If the result shows the version you just downloaded, the installation is successful.
java -version
```

