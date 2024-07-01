# A quick start FISCO BCOS Node.js SDK

Author: Li Chen Xi | FISCO BCOS Core Developer

SDK generally refers to a collection of APIs, development aids and documents provided to facilitate developers to develop applications for the system.。In the face of different developer groups, FISCO BCOS has successively launched a Java SDK for enterprise application developers (rich and stable), and a Python SDK for individual developers (quick and lightweight).。In the actual promotion process, we found that some developers are accustomed to using JavaScript to build the front end of the application, if there is an SDK that supports the use of JavaScript for back-end development, then the language barrier between the front and back end can be further broken - developers only need to understand JavaScript, you can complete the entire FISCO BCOS application front and back end development.。In this context, the FISCO BCOS Node.js SDK was born。This article will introduce the design and use of Node.js SDK。

## I. Design of SDK

The Node.js SDK follows the hierarchical design principle, with clear boundaries and clear functions between layers. The following figure shows the architecture of the Node.js SDK:

![](../../../../images/articles/node.js_sdk_quick_start/IMG_5709.JPG)

Bottom-up in the figure:

- **Base Layer**: Provide basic functions such as network communication, transaction construction and signature, contract compilation, etc.；
- **API layer**: Based on the functions provided by the base layer, the common functions of FISCO BCOS are further encapsulated, exposing the API used to call the upper application layer.。These APIs cover basic JSON RPC functionality (query chain status, deployment contracts, etc.) and precompiled contract functionality (rights management, CNS services, etc.)；
- **application layer**Applications that use the Node.js SDK for secondary development belong to this layer, such as the CLI (Command-Line Interface) chain management tools。

## Second, the installation of SDK

### 2.1 Environmental preparation

The Node.js SDK depends on the following software:

- Node.js version 8.10.0 or above, NPM version 5.6.0 or above；
- Python2、g++and make。Solidity compiler solc needs to be compiled before it can be used, and the basic software required for compilation needs to be provided by the user, where Python2 is used to run the build tool node-gyp，g++and make is used to compile solc。for no g++and make windows users, can install windows-build-Tools to build again。

###  2.2 Installing the SDK

- From https://github.com/FISCO-BCOS/nodejs-sdk download sdk；

- Enter nodejs-sdk directory；

- Execute npm i。The Node.js SDK uses lerna to manage dependencies. This step is used to install lerna.；

- Execute npm repoclean。You do not need to execute this command during the initial installation, but if the installation is interrupted halfway, it is recommended to execute this command first to clear all dependencies；

- Run npm run bootstrap, which installs all dependencies of the SDK。

After all the above commands are executed, the directory structure of the API layer is shown in the following figure:

![](../../../../images/articles/node.js_sdk_quick_start/IMG_5710.PNG)


For application developers, you can focus on nodejs-sdk / packages / api / web3j and nodejs-sdk / packages / api / precompiled directory. All APIs provided by the SDK are located in the modules in these two directories. You can import the corresponding modules through the require statement during application development.。

## Third, use SDK for development

### 3.1 Initialization

Before using all the features of the Node.js SDK, you must configure the SDK. The configuration is provided as a .json configuration file.。It mainly includes the following configuration items:

- **privateKey**FISCO BCOS is based on a public-private key system. Each account (public key) has a corresponding private key. The SDK needs to use this private key to sign transactions.；
- **nodes**: There can be multiple nodes connected to the SDK. When the number of nodes is greater than 1, each request of the SDK will randomly call up a node from nodes and send it；
- **authentication**: The SDK uses the Channel protocol to communicate with the node, and the Channel uses the SSL secure transmission protocol to transmit data. Before the two parties establish communication, necessary authentication is required。Therefore, this configuration item needs to indicate the path of the SDK private key file, certificate file, and CA root certificate file, which are usually automatically generated at the stage of the generation chain.；
- **groupID**FISCO BCOS uses a multi-group architecture. The same node can belong to multiple groups. Therefore, you need to specify the group ID of the chain that the SDK needs to connect to.；
- **timeout**Due to the network environment, the SDK request may time out, which may cause the program calling the SDK interface to fall into endless waiting. Therefore, you need to specify a timeout period.。

Developers need to load the configuration file into the Configuration object during the initialization phase. The Configuration object is globally unique and shared by all modules.。

### 3.2 Call example

Take a simple program to get the current block height as an example, the call steps are as follows:

1. Initialize the Configuration object and pass the configuration file path to the setConfig function of the Configuration object:

```
const Configuration = require('./nodejs-sdk/packages/api/common/configuration').Configuration;
Configuration.setConfig(path.join(__dirname, args.config));
```

2. The API for obtaining the height of the current block is located in Web3jService, and the object is constructed:

```
const Web3jService = require('./nodejs-sdk/packages/api').Web3jService;
let web3jService = new Web3jService();
```

3. Call the getBlockNumber interface of the Web3jService to obtain the return value and output it in the console:

```
web3jService.getBlockNumber().then(blockNumber => {
        console.log(blockNumber)
    });
```

Note that the above code uses the Promise.protoype.then method。Promise, as its name suggests, encapsulates an asynchronous operation, and "promises" that after the operation is over, the callback function specified by the user in then or catch will be called.。

Because Node.js naturally supports asynchronous features, the concept of Promise exists everywhere in the Node.js SDK (careful readers may have noticed that the module responsible for channel communication in the Node.js SDK base layer is called channelPromise)。The API calling convention of the Node.js SDK is that all API calls return the Promise object, and developers need to use the await or then... catch... method to get the call result.。Therefore, developers need to be careful when calling the API. If the return value of the API is directly used, it will easily lead to bugs.。

## IV. Using CLI Tools

In addition to the API, the Node.js SDK also provides a small CLI tool for users to operate the chain directly from the command line. The CLI tool is also an example that shows how to use the Node.js SDK for secondary development.。The CLI tool is located in the packages / cli directory. If you need to use the CLI tool, you need to enter this directory and execute the. / cli.js script. You also need to configure the CLI tool before using it. The configuration file is located in the packages / cli / conf / config.json file.。Several examples of use are given below:

1. View the version of the connected node:

![](../../../../images/articles/node.js_sdk_quick_start/IMG_5711.PNG)

2. Get the current block height:

![](../../../../images/articles/node.js_sdk_quick_start/IMG_5712.PNG)

3. Deploy the contract. The contract must be placed in the packages / cli / contracts directory before deployment.。

![](../../../../images/articles/node.js_sdk_quick_start/IMG_5713.PNG)

4. Call the get method of the HelloWorld contract:

![](../../../../images/articles/node.js_sdk_quick_start/IMG_5714.PNG)

## The future of Node.js SDK needs you

Currently, the Node.js SDK is still growing, and in some places it still needs to be further polished, such as the need for CLI tools to parse SQL statements, or the need to optimize the performance of the SDK...... Adhering to the spirit of open source, we believe that the energy of the community can make the Node.js SDK more convenient and easy to use.！