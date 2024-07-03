# Past lives of the Python SDK

Author ： Chen Yujie ｜ FISCO BCOS Core Developer

In June this year, on the basis of the existing Java SDK, FISCO BCOS has launched the Python SDK and Go SDK, many developers said, these two components greatly reduce the difficulty of developing blockchain applications, so that more developers can participate in the open source construction of FISCO BCOS。Today, this article will take a look at the past lives of the Python SDK to see how this simple and easy-to-use component that plays a great role is implemented。

## Why: Why the Python SDK?

As we all know, Python language is simple and clear, almost close to natural language, you can quickly get started；Although the Java language is powerful, the syntax and components are relatively complex and are generally used in enterprise applications。In order to combine the advantages of the two, FISCO BCOS Chief Architect Zhang Kaixiang achieved the first version of the Python SDK after two weeks of intensive development。This version played a huge role in the community's hackathon competition in July this year, helping participants better focus on the development project itself。In the high-intensity hackathon competition, participants have only 36 hours to complete blockchain-related projects。In the exchange with the contestants, we found that most geek developers started from public chains such as Bitcoin and Ethereum. They are not very familiar with the Java language, but are proficient in Python, Go, and Node.JS。At that time, the team mainly promoted Java SDK, console, WeBASE and other Java-based blockchain development tools, but in just 36 hours, it was really difficult for geek developers who were not familiar with Java to complete development based on these tools。In this case, the first Python SDK has become a life-saving medicine for developers。In the end, many of the entries were based on the Python SDK。

## What: What is the Python SDK?

The Python SDK has been evolved and optimized in different versions to gradually realize rich functions。

### Basic Python SDK

The first version of the Python SDK is mainly developed on the Windows platform and implements an interface to access the RPC service of the FISCO BCOS chain. Through the Python SDK interface, users can obtain basic information about the blockchain(Such as block height, block, transaction, transaction receipt, etc)You can also deploy and invoke contracts。However, this version of the SDK does not support the channel protocol, so the communication security between the SDK and the node cannot be guaranteed；In addition, the Python SDK cannot receive the receipt and block height information pushed by the FISCO BCOS blockchain node. Therefore, after the transaction is sent, the node must be polled to obtain the latest information。

### Python SDK supporting the Channel protocol

To improve the security of communication between the SDK and nodes, the Python SDK implements the Channel protocol in the basic version。After the SDK that supports the Channel protocol sends the transaction, it does not need to poll the node to obtain the transaction execution result, but can directly receive the push of the execution result from the node, so that after the SDK sends the transaction, it will not block the polling in the state, realizing the function of sending the transaction asynchronously。

### Support for Precompile contract calls

In order to break through the performance bottleneck of blockchain virtual machines and greatly improve the transaction processing throughput, FISCO BCOS implements the precompiled contract framework. The existing precompiled contracts include naming service contracts, consensus contracts, system configuration contracts, permission control contracts, CRUDs, etc. To facilitate users to use these precompiled contracts, the Python SDK provides an interface to access the precompile contract。

### Python SDK Console

After the above functions are ready, the Python SDK can already implement all interactions with the FISCO BCOS chain, but users still cannot intuitively experience the FISCO BCOS chain through the Python SDK. Therefore, on the basis of implementing all the above interfaces, the Python SDK integrates the console。

### Python SDK multi-platform support

As we all know, Python is a cross-platform language that supports various development platforms such as Windows, MacOS, CentOS, and Ubuntu。The Python SDK is based on the basic version of Windows system development, so there are still some problems when deployed in MacOS and CentOS systems, such as inconsistent dependency package requirements for each platform, different installation methods for Python that meet the Python SDK version, and different installation methods for contract compilers。

The above problems will cause some developers to run out of energy on installing the basic environment. To solve this problem, the Python SDK provides a deployment script 'init _ env.sh', which can be used by users to initialize the Python SDK environment with one click。

The main features of the script 'init _ env.sh' include:

- If the Python version of the system running the Python SDK is less than 3.6.0, install version 3.7.3 of the Python virtual environment python-sdk；
- Install v0.4.25 version of the solidity compiler。

## How: How to implement Python SDK

The previous section introduced the main features of the Python SDK, and this section talks about the implementation of the Python SDK。

Rome was not built in a day, and the Python SDK referred to [Ethereum Client] during development(https://github.com/ethereum/web3.py)Some modules, including:

- ABI Codec Module
- RLP codec module
- Account and key generation module

### Implementing the Channel Protocol

On the basis of the above modules, the Python SDK implements the RPC interface and the sending transaction interface that support the FISCO BCOS Channel protocol。The Channel message package type definition is implemented in the module 'client.channelpack.ChannelPack'. The main message package types include:

- 0x12: RPC type messages
- 0x13: Node Heartbeat Packet
- 0x30: AMOP Request Package
- 0x31: Envelope body of failed AMOP message
- 0x32: SDK topics
- 0x35: AMOP message packet body
- 0x1000: Transaction Chain Notification(Include transaction receipt)
- 0x1001: Block height push package

The implementation of the channel protocol is located in the 'client.channel.handler.ChannelHandler' module. In order to support asynchronous sending transactions, the Python SDK introduces the 'pymitter' and 'promise' components to manage asynchronous events. The main workflow of the module to send transactions and obtain transaction receipts is as follows:

- 1. The Python SDK calls the 'sendRawTransactionAndGetReceipt' interface to encode and sign the transaction, and uses the encoded data as the parameters of the 'ChannelHandler' module to send the socket request packet；
-2. After ChannelHandler receives the data of 1, register the uuid of the data packet to the asynchronous event queue, put the message packet into the send buffer and send it；
-3. After the node processes the transaction, it sends a transaction chain notification of type 0x1001 to the SDK；
-4. After receiving the return packet with message type 0x1001, the SDK-side ChannelHandler takes out the uuid of the message packet, triggers the asynchronous event corresponding to the uuid, and returns the transaction execution result to the upper-level application。

### Implement Console

The Python SDK console is implemented in the 'console' module, which mainly calls the module interface to interact with the blockchain:

- 'client.bcosclient': Python SDK basic interface class, which provides the interface for accessing FISCO BCOS RPC services, deploying and invoking contracts；
- 'console _ utils.precompile': Provides access to Precompile precompiled contracts；
- `console_utils.rpc_console`: Encapsulates' client.bcosclient 'to call RPC services from the command line。

## Python SDK usage demonstration

Learn about the origin, features, and implementation of the Python SDK. Here's a look at the Python SDK from the console perspective:

### Get Node Version

Enter '. / console.py getNodeVersion' in the command line terminal to obtain the node version:

![](../../../../images/articles/python-sdk_origin_function_and_realization/IMG_5715.PNG)


### Deploying HelloWorld Contracts

The Python SDK has a built-in HelloWorld contract. Enter '. / console.py deploy HelloWorld' in the command line terminal to deploy the 'HelloWorld' contract. If you need to deploy a custom contract, you need to place the contract in the 'contracts' subdirectory and use the command '. / console.py deploy [contract name]' to deploy the contract。The following figure is the HelloWorld contract deployment output, from which you can see that the contract address is: `0xbbe16a7054c0f1d3b71f4efdb51b9e40974ad651`

![](../../../../images/articles/python-sdk_origin_function_and_realization/IMG_5716.JPG)


### Invoking the HelloWorld contract

The Python SDK console uses the 'sendtx' subcommand to send transactions and the call subcommand to call the contract constant interface。HelloWorld contract code is as follows, mainly including 'set' and get two interfaces, the former is used to set the contract local variable 'name', the latter is a constant interface, get the current value of the local variable name。

```
pragma solidity^0.4.24;
contract HelloWorld{
  string name;
  constructor() public{
      name ="Hello, World!";
  }
# get interface
   function get() constant public returns(string){
      return name;
  }
# set interface
   function set(string n) public{
      name = n;
  }
}
```

### Calling the HelloWorld set interface

Because the 'set' interface changes the contract status, it is called using the 'sendtx' subcommand, and the 'sendtx' usage is as follows:

```
./console.py sendtx [contract_name][contract_address] [function][args]
```

Parameters include:

-contract _ name: contract name
-contract _ address: contract address
-function: function interface
-args: parameter list

Use '. / console.py sendtx HelloWorld 0xbbe16a7054c0f1d3b71f4efdb51b9e40974ad651 set"Hello,Fisco"command sets the 'name' member variable of the 'HelloWorld' contract to"Hello, Fisco"and enter as follows:

![](../../../../images/articles/python-sdk_origin_function_and_realization/IMG_5717.PNG)

### Calling the HelloWorld get interface

The get of 'HelloWorld' is a constant interface, so it is called using the 'call' subcommand. The call usage is as follows:

```
./console.py call [contract_name] [contract_address] [function] [args]
```

Parameters include:

-contract _ name: contract name
-contract _ address: the address of the contract called
-function: the contract interface called
-args: call parameter

Use. / console.py call HelloWorld 0xbbe16a7054c0f1d3b71f4efdb51b9e40974ad651 get to get the latest value of the HelloWorld contract name member variable:

![](../../../../images/articles/python-sdk_origin_function_and_realization/IMG_5718.PNG)


## Summary

This article describes the past and present life of the Python SDK, including the origin, function and implementation of the Python SDK(https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/python_sdk/index.html)。Of course, the Python SDK is still in the process of continuous improvement, welcome community enthusiasts to pay attention to [related issue](https://github.com/FISCO-BCOS/python-sdk/issues)and contribute valuable PR to the optimization of Python SDK。

