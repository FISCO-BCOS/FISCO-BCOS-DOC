# Function Analysis of Pre-Component of WeBASE Node

Author ： He Shuoyan ｜ FISCO BCOS Core Developer

The FISCO BCOS tethering script has given developers the ultimate tethering experience. How can you quickly build a blockchain visual interface to interact with the blockchain? WeBASE-Front is the component that can meet this expectation the fastest。WeBASE-Front provides developers with a subset of the minimum functions of blockchain interaction, which is lightweight and easy to install without installing any third-party components.。Build WeBASE after completing the node-Front, you can open the interface in the browser, quickly open the blockchain experience journey。

WeBASE-Front also offers a lot of friendly and useful features:

- in WeBASE-On the front page, developers can view the block information, transaction information, group information, number of nodes, and node status of the blockchain.**The core information of the blockchain network is clear at a glance**。
- WeBASE-Front provides a contract development IDE for developers to write and debug smart contracts.**Quickly develop your own blockchain applications**。
- WeBASE-Front integrates the Web3SDK and encapsulates the Web3SDK interface. Developers can call WeBASE through HTTP requests.-Front interface interacts with blockchain nodes。This approach shields the limitations of the SDK language.**Developers of any language can call WeBASE-The interface of Front interacts with the blockchain**。

Of course, WeBASE-The function of Front is not limited to this。As a member of the WeBASE family, this component cooperates with WeBASE-Node-Manager and WeBASE-Web is used together as a node front to monitor the blockchain network in all directions and realize the enterprise-level blockchain monitoring function.。

## Function Introduction

WeBASE-Front has the following five main functions:

### I. Data Overview

Display common data that developers care about:**Block height, number of nodes, total number of transactions, number of pending transactions,**And support block information and transaction information query。

![](../../../../images/articles/webase_node_preposition/IMG_5628.PNG)

### II. Node Management

Displays the**Number of nodes, node ID, block height, pbftview, and node running status**and supports dynamic switching of blockchain clusters。

### III. Contract Management

This is WeBASE-The core functionality of Front, on which developers can**Write, compile, debug contracts**， 以及**JAVA class for generating contracts with one click**The deployed contracts will be stored in the H2 embedded database, and historical contracts can be queried in the contract list.。

![](../../../../images/articles/webase_node_preposition/IMG_5629.PNG)

### IV. System Monitoring

System monitoring is divided into two aspects:

1. Performance metrics monitoring of node servers, including**CPU, hard disk, memory, upstream bandwidth, downstream bandwidth**；
2. Performance metrics monitoring of blockchain nodes, including**block height, pbftview, and number of transactions to be packaged**。

After the node is running for a long time, you can view the performance of the server through this function。

### V. Private key management

Generate elliptic curve public-private key pairs, supporting**Import Export Private Key**and supports aliasing the address to facilitate address memory。You need to create a public-private key pair before deploying a contract。

![](../../../../images/articles/webase_node_preposition/IMG_5630.PNG)


## technical analysis

WeBASE-Front is based on FISCO BCOS**spring-boot-starter**(Please refer to the link at the end of the article) A development example of the project。**Web3SDK interface encapsulation, dynamic group switching, deployment call contract (without generating JAVA classes), public-private key pair generation,**Refer to WeBASE for these common features-Front code, developers can learn from and write their own springboot applications。 

For ease of installation and use, WeBASE-Front uses a lightweight**H2 Embedded Database**The backend uses the SSH framework and uses JPA to access the database.；Front End Adopt**VUE**Framework development, front-end resources are built into the back-end springboot service, no need to install and configure nginx and mysql these steps, directly start the Java service to access the interface。

The generated public and private keys and deployed contracts are stored in the H2 database for easy query history。The performance monitoring function uses the**sigar**Data Collection Components。The collected data will also be stored in the H2 database, but only the most recent week's monitoring data will be saved。

## Deployment method

As node front, WeBASE-Front needs to be deployed on the same machine as the node。When deploying multiple nodes on one machine, we recommend that you deploy only one WeBASE-Front Services。

WeBASE-There are three ways to deploy Front:

1. Separate deployment is used as an independent console, and is equipped with an interface, deployment is simple and quick, just download WeBASE-Front application, replace node certificate to start。We recommend that beginners and developers use this deployment method to query information about the blockchain and develop and debug smart contracts.。(Please refer to the link at the end of the article for installation)

2. In Mode 1, WeBASE-Front is used as a visual console. The private key is encrypted and stored in the H2 database by default. If you need a more secure private key protection scheme, you can combine WeBASE-The private key is stored in WeBASE for deployment with the Sign service.-Sign, WeBASE-Sign service is responsible for signing transaction data, providing a more secure private key protection scheme。

   This method deploys WeBASE on top of Method 1-Sign service. If you have high requirements for private key security, use this deployment method.。（WeBASE-Sign service please refer to the link at the end of the article)

3. Combined with WeBASE-Node-Manager and WeBASE-Web services are deployed together using WeBASE here-Front only as a node front, multiple node front unified by WeBASE-Node-Manager Management, WeBASE-Node-Manager has an authentication login system implemented by spring security, and pulls the block information and transaction information on the chain and stores it in the Mysql database.。It is recommended to use this method in the production environment. The architecture diagram is as follows。(Please refer to the link at the end of the article for WeBASE installation and deployment)

![](../../../../images/articles/webase_node_preposition/IMG_5631.PNG)

## SUMMARY

WeBASE-As a convenient and powerful blockchain component, Front can be used independently as a visual console for developers to interact with the blockchain, and can also cooperate with WeBASE.-Node-Manager and WeBASE-Web is used together to realize the blockchain monitoring function of production environment。

WeBASE-Front is still in continuous optimization and development, and will add more and more features in the future, such as the system management function of adding and deleting nodes in the alliance chain, and the transaction resolution function.。Of course, continuous iterative upgrades will maintain its ease of use and convenience.。Welcome community friends to mention PR and ISSUE, participate in optimization together。

------

#### Link Guide

- [Spring-boot-starter](https://github.com/FISCO-BCOS/spring-boot-starter/tree/master-2.0)

- [Sigar Data Collection Component](https://www.jianshu.com/p/c3d88dd617bf)

- [WeBASE-Front Separate Installation Deployment Instructions](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Install/developer.html#)

- [WeBASE-Sign Service](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Sign/index.html)

- [WeBASE Installation Deployment](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Install/enterprise.html)

