# WeBASE节点前置组件与后端应用交互

作者：毛宇｜FISCO BCOS 核心开发者

​	WeBASE-Front(下文称节点前置)集成了Web3SDK，并对Web3SDK接口进行了封装，开发者可通过HTTP请求，调用WeBASE-Front接口和区块链节点进行交互，本篇主要介绍如何通过节点前置接口与java后端进行交互，以及如何参照官方文档编写接口。

## 一、准备链和节点前置服务

​    首先准备好自己的区块链节点，这里采用单机四节点的默认配置搭建节点，实际可任意搭链方式（节点和节点前置搭建方式可参见文末连接指引）。

### 1.启动区块链节点	

![](../../../../images/articles/webase_node_preposition_application\1.PNG)

### 2.启动节点前置服务

![img](../../../../images/articles/webase_node_preposition_application\2.PNG)

![img](../../../../images/articles/webase_node_preposition_application\3.PNG)

## 二、准备测试合约

这里以最简单的HelloWorld合约作为举例。

合约源码：

```solidity
pragma solidity ^0.6.0;

contract HelloWorld {
  string private message;

  function sayHello() public pure returns (string memory) {
    return "Hello, World!";
  }

  function setMessage(string memory newMessage) public {
    message = newMessage;
  }

  function getMessage() public view returns (string memory) {
    return message;
  }
}
```

### 1.编译部署合约

在合约管理 > 合约IDE中新建合约HelloWorld，选择合适的编译版本进行编译。

![img](../../../../images/articles/webase_node_preposition_application\4.PNG)

### 2.添加测试账户

在合约管理 > 测试用户中新建测试用户test。

![img](../../../../images/articles/webase_node_preposition_application\5.PNG)

### 3.部署合约

在合约管理 > 合约IDE中部署合约HelloWorld，选择test账户部署，将得到的合约地址和abi信息进行保存。

![img](../../../../images/articles/webase_node_preposition_application\6.PNG)

## 三、引入后端项目

### 1.新建项目

我们直接新建一个springboot项目，然后创建一个controller层，我们直接在这里测试节点前置的API接口。

![img](../../../../images/articles/webase_node_preposition_application\7.PNG)

### 2.引入依赖

同时也要加几个必要的依赖包，fastjson用于json转换，hutool用来解析区块交互信息。

```XML
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.79</version>
</dependency>
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.11</version>
</dependency>
```

## 四、参照API官方文档编写接口

WeBASE的子系统节点前置提供了许多与区块链进行交互的接口，详细查询可参见此官方文档：

> 官方文档地址：[接口说明 — WeBASE v1.5.5 文档 (webasedoc.readthedocs.io)](https://webasedoc.readthedocs.io/zh-cn/latest/docs/WeBASE-Front/interface.html)

![img](../../../../images/articles/webase_node_preposition_application\8.PNG)

### 1.合约交易接口调用

我们调用API最重要的目的是通过FISCO BCOS与合约交互，可以使用交易处理接口接口与合约进行交互。

![img](../../../../images/articles/webase_node_preposition_application\9.PNG)

官方文档中已经对合约的一些参数做了规范与定义 ，看起来很麻烦，需要很多参数，但其实多数参数是固定的，like this，注意替换你自己的合约地址等信息！

```java
// 合约地址
private static final String contractAddress = "0xb75096b9b37fcfc007cee1f00d4dc256879f9bef";
// 合约ABI
private static final String ABI = "这里是合约ABI信息，原文过长，影响阅读";
// 合约名称
private static final String contractName = "HelloWorld";
// 节点前置接口地址
private static final String url = "http://192.168.200.135:5002/WeBASE-Front/trans/handle";
```

接着我们定义一个commonReq的方法调用接口，接收3个值：用户地址、合约方法名、合约方法需要传入的参数，用户地址可以使用我们在节点前置添加的用户，要注意data内数据的准确性。

```java
public static String commonReq(String userAddress,
                               String funcName,
                               List funcParam) {
    JSONObject data = new JSONObject();
    JSONArray abiJSON = JSON.parseArray(ABI);
    data.put("user", userAddress);
    data.put("contractName", contractName);
    data.put("contractAddress", contractAddress);
    data.put("funcName", funcName);
    data.put("contractAbi", abiJSON);
    data.put("groupId", 1);
    data.put("funcParam", funcParam);
    data.put("useCns", false);
    data.put("useCns", false);
    data.put("cnsName", "");
    String dataString = data.toString();

    String response = HttpRequest.post(url)
            .header(Header.CONTENT_TYPE, "application/json")
            .body(dataString)
            .execute()
            .body();
    return response;
}
```

通过我们定义的commonReq方法，我们即可对合约进行交互，例如对合约的get方法和set方法进行调用。

### 2.接口测试

之后我们启动后端springboot项目，就可以在postman中进行接口测试，交易返回的result即为在节点前置中调用合约方法的返回结果。

![img](../../../../images/articles/webase_node_preposition_application\10.PNG)

![img](../../../../images/articles/webase_node_preposition_application\11.PNG)

测试结果正常，此时可以去节点前置验证。

### 3.交易查验

可以看到，区块高度和交易数量都增加了，且合约内信息与刚才测试调用的信息相同，真正实现了通过java与webase节点前置的交互。

![img](../../../../images/articles/webase_node_preposition_application\12.PNG)

![img](../../../../images/articles/webase_node_preposition_application\13.PNG)

------

#### 链接指引

- [搭建第一个区块链网络](https://fisco-bcos-documentation.readthedocs.io/zh-cn/latest/docs/installation.html)
- [WeBASE-Front部署](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Install/developer.html#)
- [节点前置接口说明]([接口说明 — WeBASE v1.5.5 文档 (webasedoc.readthedocs.io)](https://webasedoc.readthedocs.io/zh-cn/latest/docs/WeBASE-Front/interface.html#id399))

