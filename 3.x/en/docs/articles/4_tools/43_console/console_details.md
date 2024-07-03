# FISCO BCOS console detailed explanation, flying general blockchain experience

Author ： Liao Feiqiang ｜ FISCO BCOS Core Developer

We are already familiar with the command line terminal in the Linux system, and we can use Linux smoothly through the shell terminal。Similarly, in the FISCO BCOS consortium chain, there are such command-line terminals, called consoles。The console is a booster for developers to explore the blockchain world, providing a variety of features that help cross the mountains between blockchain entry and mastery, bringing an "out-of-the-box" smooth experience。

## Why: Why a console?？

### Experience environmental impact users from "getting started to give up," or "getting started to proficient"。

When learning a new technology or product, in addition to reading documents, it is also important to try to get started and get a first-hand experience。If the experience environment is complex to configure and cumbersome to operate, it is likely to cause users to move from "getting started to giving up."；The experience environment is simple, easy to configure, and feature-rich, which will quickly open the door to a new world for users, accelerating users from entry to mastery。

What kind of form do you choose to carry this fast and friendly experience?？If we have a console, just enter a single command or code, and then press Enter to return the result. The result is displayed in front of the user。This "out of the box" effect is exactly the way we expect to experience。

### Can the console for FISCO BCOS achieve speed experience?？

FISCO BCOS version 1.3 actually has the function of fast experience, which consists of two parts, namely ethconsole and Node.js tool。Among them, ethconsole can query information on the chain, including node, block, and transaction information；The Node.js tool provides template js files for deploying and invoking contracts to assist users in deploying and invoking contracts。

But this experience is not friendly enough, that version of ethconsole can only query very limited information on the chain, and can not send transactions and manage the blockchain, the function is relatively single；Through the Node.js tool, you need to manually write the template js file for deploying and calling the contract, the operation is more cumbersome, the experience is separated from the ethconsole, not so smooth。

Therefore, FISCO BCOS 2.0 version planning, focusing on the design of the FISCO BCOS 2.0 console, the goal is to make an easy-to-use, friendly, and powerful new console, to provide FISCO BCOS with a speed experience。

## What: What functions are implemented in the console？

The realization of each function of the console comes from a simple operation, all according to the actual needs, the user's valuable functions, one by one。

### Requirement 1: What is Blockchain and Where Is It?？Can you see it?？

Realize a series of commands related to querying the blockchain, making the blockchain visible and tangible！For example, query block height, blocks, transactions, nodes, etc., and according to different parameters, provide different query methods to meet the query requirements under different conditions。

It is worth noting that for transaction and transaction receipt information query commands (getTransactionByHash, getTransactionReceipt, etc.), the function of parsing detailed data by ABI definition is provided, so that the input, output and event log information of the transaction is presented in a decoded way instead of full-screen hexadecimal astronomical numbers。

### Requirement 2: Deploying and invoking contracts is the core requirement for using blockchain. Can the console directly deploy and invoke contracts?？

must be able to。Before the console was launched, there were two options for deploying and invoking contracts: one was to use the Node.js tool, that is, to write Node.js client deployment and invocation contracts；One is to use the Java SDK to write Java client deployment and call contracts。Both of these are powerful, but they are not designed for the speed experience, and users need to write deployment and calling code outside of the contract。

Therefore, the effect of the console implementation is that the user writes the contract, puts it in the specified path, enters a command (deploy) in the console to complete the deployment, and then uses the call command to call the contract interface without any additional work (such as converting the solidity contract into java code, writing the client code for deploying and calling the contract, etc.)。
After the deploy command deploys the contract, a contract address will be displayed. Considering that the contract address will be used in subsequent contract calls, the console will record the deployed contract address locally and provide the getDeployLog command to view the list of deployed contract addresses。

In addition, the FISCO BCOS blockchain provides the CNS function, the contract command service function。The contract name, version number and corresponding contract deployment address of the deployment can be recorded on the chain；When deploying a contract, specify the contract name and version number；When calling a contract, specify the contract name and version number (if not, use the most recently deployed contract version number)。

This is a more advanced way to deploy and invoke contracts, and is the recommended way to deploy and invoke contracts。So the console implements the deployment contract command deployByCNS with CNS and the call contract command callByCNS with CNS。
It is worth noting that for developers to view processing information and debug contracts, the console automatically parses contract output and event log information。

### Requirement 3: FISCO BCOS 2.0 supports multiple groups. Can the console switch groups online?？

After console login, the current group number is displayed in front of its command prompt。Provide the switch command to switch multiple groups online, that is, you can switch seamlessly without exiting the console。After switching, the group number in front of the command prompt is automatically updated, and then commands can be sent in the switched group。

### Requirement 4: Can the console manage the blockchain?？

FISCO BCOS 2.0 provides node management, system parameter management, and permission management. The console provides corresponding commands for operation, making it easy for users to manage the blockchain through simple commands。
where the command for node management is addSealer(Add consensus node)、addObserver(Add Observation Node)、removeNode(Remove a node from a group)；The command for system parameter management is setSystemConfigByKey(Setting System Parameters)；Permission management has a series of commands to manage the operation permissions of related functions of the blockchain system, including the grant command beginning with grant, the revoke command beginning with revoke, and the query permission command beginning with list。

[Specific use reference here](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/console.html#grantpermissionmanager)

### Requirement 5: Can I operate the user table in the blockchain without writing a CRUD contract?？

FISCO BCOS 2.0 provides distributed storage, the core of which lies in the table storage design。CRUD contract is a kind of contract writing method for table storage, the contract data is stored in the user table, the contract interface for table addition, deletion, modification and query operations。
In order to allow users to experience distributed storage without writing CRUD contracts, the console provides a form similar to the mysql statement, providing the create table (create), view table (desc) and table add and delete (insert, delete, update, select) commands。
Both table creation and addition / deletion commands send a transaction that requires the consensus of blockchain nodes, which is equivalent to writing a CRUD contract operation table。

### Requirement 6: Does the console support the State Secret method for transactions?？

The console provides the State Secret switch to modify the configuration file and download the State Secret contract compiler and replacement, which becomes the State Secret console。Therefore, when the blockchain node is in the state secret version, the console can connect to the state secret node and support the deployment and invocation of the state secret contract to send the state secret transaction。

### Requirement 7:... wait for you to mention？

Community users are welcome to actively provide comments, suggestions and requirements (in the form of issue or WeChat community). At the same time, you can directly pull requests to the official console repository to modify and add the functions you need, and you can even fork the source code and then customize the individual or organization's console separately. The co-construction and sharing of open source communities rely on the extensive and active participation of community users。

## Where: Where is the value of the console

Now, as long as users start the console, they can query rich information on the chain, quickly deploy and invoke contracts, and easily manage the blockchain. What value do these features bring to users?？

- **For beginner**No need to build a complex development environment, as long as the simple node ip and port configuration, very lightweight, you can quickly experience the blockchain function。When the console is launched and a cool FISCO BCOS logo is presented, you will immediately feel that you have successfully made the first connection with FISCO BCOS, thus continuing to explore the blockchain with confidence。

  [Configure and start the console please refer here](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/console.html#id8)

- **For developers**: Developers can use the console to deploy and debug contracts. After writing a contract, they can deploy the contract in the console and use the call command to call the verification contract logic to observe the contract running process and results。If the business side uses Java to develop applications, you can use the contract compilation tool of the console to compile the Solidity contract into a Java client code file for the client's Java project call。

- **For test and operation personnel**After building a blockchain environment, you can use the console to view the status of the blockchain, operate the blockchain configuration, and test or check related blockchain functions。

**In short, the console has become a window of FISCO BCOS's extreme experience, a powerful weapon that continues to bring real value to users**。

------

####  **Note**：

1. The console is already a standard feature of the FISCO BCOS SDK. Currently, the Java version of the console (a separate console repository has been established), the Python version of the console and the Node.js console are available。The above is mainly for the Java version of the console, other console-related functions are roughly the same。

**2. Console Document List**：

[Java version of the console to use documentation](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/console/console.html )  

[Python version of the console to use the document](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/python_sdk/console.html)

[Node.js console use document](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/nodejs_sdk/install.html#node-js-cli**)