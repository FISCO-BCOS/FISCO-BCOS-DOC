# 合约编解码

标签：``java-sdk`` ``abi`` `scale`  ``codec``

----
在Java SDK 3.x版本使用`ABI`和`Scale`两种编解码格式，分别对**Solidity合约**和**WebankBlockchain-Liquid合约（简称WBC-Liquid）** 的函数签名、参数编码、返回结果进行编解码。

**注**：为了更加区别ABI和Scale的区分，3.0.0-rc3以后org.fisco.bcos.sdk.ABICodec改名为org.fisco.bcos.sdk.v3.codec.ContractCodec

在Java SDK中，`org.fisco.bcos.sdk.v3.codec.ContractCodec`类提供了编码交易的输出（`data`的字段）、解析交易返回值及解析合约事件推送内容的功能。

这里以`Add.sol`合约为例，给出`ContractCodec`的使用参考。

```solidity
pragma solidity^0.6.0;

contract Add {

    uint256 private _n;
    event LogAdd(uint256 base, uint256 e);
 
    constructor() public {
        _n = 100;
    }

    function get() public view returns (uint256 n) {
        return _n;
    }

    function add(uint256 e) public returns (uint256 n) {
        emit LogAdd(_n, e);
        _n = _n + e;
        return _n;
    }
}
```

调用`add(uint256)`接口的交易回执内容如下，重点关注`input`、`output`和`logs`字段：

```Java
{
  // 省略 ...
  "input":"0x1003e2d2000000000000000000000000000000000000000000000000000000000000003c",
  "output":"0x00000000000000000000000000000000000000000000000000000000000000a0",
  "logs":[
      {
        // 省略 ... 
        "data":"0x0000000000000000000000000000000000000000000000000000000000000064000000000000000000000000000000000000000000000000000000000000003c",
        // 省略 ...
      }
  ],
  // 省略 ...
}
```

## 1. 初始化ContractCodec

在使用`ContractCodec`的方法之前，需要先对密码学环境、编解码格式进行初始设定。ABICodec构造函数如下：

```java
public ContractCodec(CryptoSuite cryptoSuite, boolean isWasm) {
   // 省略
}
```

CryptoSuite可以从初始化的Client类中进行获取，可参考链接：[初始化SDK](./assemble_transaction.html#sdk)

isWasm是决定ContractCodec编码格式使用的重要参数：

- 如果isWasm为true，那么将使用Scale编码格式对交易输入输出进行编解码，在节点中对应的是使用WBC-Liquid合约；
- 如果isWasm为false，那么将使用ABI编码格式对交易输入输出进行编解码，在节点中对应的是使用Solidity合约。

## 2. 构造部署合约构造函数input

部署交易的input由两个部分组成，合约部署的二进制code以及构造函数所需参数的编码。其中二进制code为合约编译后的binary code。

```java
// bin + Object格式的参数列表, abi用于部署时插入到交易中
byte[] encodeConstructor(String abi, String bin, List<Object> params);
// bin + String格式的参数列表, abi用于部署时插入到交易中
byte[] encodeConstructorFromString(String abi, String bin, List<String> params);
// bin + String格式的参数列表, abi需要额外插入到交易中
byte[] encodeConstructorFromBytes(String bin, byte[] params);
```

## 3. 构造交易input

交易的input由两部分组成，函数选择器及调用该函数所需参数的编码。其中input的前四个字节数据（如"0x1003e2d2"）指定了要调用的函数选择器，函数选择器的计算方式为函数声明（去除空格，即`add(uint256)`）的哈希，取前4个字节。input的剩余部分为输入参数根据ABI编码之后的结果（如"000000000000000000000000000000000000000000000000000000000000003c"为参数"60"编码之后的结果）。

根据函数指定方式及参数输入格式的不同，`ContractCodec`分别提供了以下接口计算交易的`data`。

```Java
// 函数名 + Object格式的参数列表
byte[] encodeMethod(String abi, String methodName, List<Object> params);
// 已有ABI定义 + Object格式的参数列表
byte[] encodeMethodByAbiDefinition(ABIDefinition abiDefinition, List<Object> params);
// 函数声明 + Object格式的参数列表
byte[] encodeMethodByInterface(String methodInterface, List<Object> params)
// 函数签名 + Object格式的参数列表
byte[] encodeMethodById(String abi, byte[] methodId, List<Object> params);
// 函数名 + String格式的参数列表
byte[] encodeMethodFromString(String abi, String methodName, List<String> params);
// 函数声明 + String格式的参数列表
byte[] encodeMethodByInterfaceFromString(String methodInterface, List<String> params);
// 函数签名 + String格式的参数列表
byte[] encodeMethodByIdFromString(String ABI, byte[] methodId, List<String> params);
```

以下以`encodeMethod`为例举例说明使用方法，其他接口的使用方法类似。

```Java
// 初始化SDK
BcosSDK sdk =  BcosSDK.build(configFile);
// 初始化group群组
Client client = sdk.getClient("group0");
// 使用Solidity合约
boolean isWasm = false;
ContractCodec contractCodec = new ContractCodec(client.getCryptoSuite(), isWasm);
String abi = ""; // 合约ABI编码，省略

// 构造参数列表
List<Object> argsObjects = new ArrayList<Object>();
argsObjects.add(new BigInteger("60"));
try {
  String encoded = contractCodec.encodeMethod(abi, "add", argsObjects));
  logger.info("encode method result, " + encoded);
    // encoded = "0x1003e2d2000000000000000000000000000000000000000000000000000000000000003c"
} catch (ContractCodecException e) {
  logger.info("encode method error, " + e.getMessage());
}
```

## 4. 解析交易返回值

根据函数指定方式及返回值类型的不同，`ContractCodec`分别提供了以下接口解析函数返回值。

```Java
// ABI定义 + Object列表 + ABIObject返回列表
Pair<List<Object>, List<ABIObject>> decodeMethodAndGetOutputObject(ABIDefinition abiDefinition, String output)
// ABI定义 + ABIObject返回列表
ABIObject decodeMethodAndGetOutAbiObjectByABIDefinition(ABIDefinition abiDefinition, String output)
// 函数名 + ABIObject返回列表
ABIObject decodeMethodAndGetOutputAbiObject(String abi, String methodName, String output)
// 函数名 + Object格式的返回列表
List<Object> decodeMethod(String abi, String methodName, String output)
// 函数声明 + Object格式的返回列表
List<Object> decodeMethodByInterface(String abi, String methodInterface, byte[] output)
// 函数签名 + Object格式的返回列表
List<Object> decodeMethodById(String abi, byte[] methodId, byte[] output)
// 函数名 + String格式的返回列表
List<String> decodeMethodToString(String abi, String methodName, byte[] output)
// 函数声明 + String格式的返回列表
List<String> decodeMethodByInterfaceToString(String abi, String methodInterface, byte[] output)
// 函数签名 + String格式的返回列表
List<String> decodeMethodByIdToString(String abi, byte[] methodId, byte[] output)
```

上述接口参数中的`output`为交易回执中的`output`字段（"0x00000000000000000000000000000000000000000000000000000000000000a0"）。接口的使用方法可参考构造交易input的接口用法。

## 5. 解析合约事件推送内容

根据事件指定方式及解析结果类型的不同，`ContractCodec`分别提供了以下接口解析事件内容。

```Java
// 事件名 + Object格式的解析结果列表
List<Object> decodeEvent(String abi, String eventName, EventLog log)
// 事件声明 + Object格式的解析结果列表
List<Object> decodeEventByInterface(String abi, String eventSignature, EventLog log)
// 事件签名/Topic + Object格式的解析结果列表
List<Object> decodeEventByTopic(String abi, String eventTopic, EventLog log)
// 事件名 + String格式的解析结果列表
List<String> decodeEventToString(String abi, String eventName, EventLog log)
// 事件声明 + String格式的解析结果列表
List<String> decodeEventByInterfaceToString(String abi, String eventSignature, EventLog output)
// 事件签名/Topic + String格式的解析结果列表
List<String> decodeEventByTopicToString(String abi, String eventTopic, EventLog log)
```

对于事件推送，Java SDK需用户可以通过继承`EventCallback`类，重写`onReceiveLog`接口，实现自己对回调的处理逻辑。以下例子使用`decodeEvent`对推送的事件内容进行解析。其他接口的使用方法类似。

```Java
class SubscribeCallback implements EventSubCallback {
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        if (logs != null) {
            String abi = ""; // 合约ABI编码，省略
            for (EventLog log : logs) {
                // 使用Solidity
                boolean isWasm = false;
                ContractCodec contractCodec = new ContractCodec(client.getCryptoSuite(), isWasm); // client初始化，省略
                try {
                    List<Object> list = contractCodec.decodeEvent(abi, "LogAdd", log);
                    logger.debug("decode event log content, " + list);
                    // list.size() = 2
                } catch (ContractCodecException e) {
                    logger.error("decode event log error, " + e.getMessage());
                }
            }
        }
    }
}
```

## 6. 解析交易输入值

与构造交易input相比，解析交易input是逆向操作，可以按照ABI解析出input参数。交易分为部署合约的交易与调用合约的交易，从上文可知，部署合约的交易input是由二进制code与编码后的构造函数参数拼接而成；调用合约的交易input是由函数选择器与与编码后的函数参数拼接而成。

```java
// ABI定义 + 去掉函数选择器之后的被编码过的参数 + Object，ABIObject返回列表
Pair<List<Object>, List<ABIObject>> decodeMethodAndGetInputObject(ABIDefinition abiDefinition, String params)
// ABI定义 + 交易input + ABIObject返回
ABIObject decodeMethodAndGetInputObjectByABIDefinition(ABIDefinition abiDefinition, String input)
// ABI + 方法名 + 交易input + ABIObject返回
ABIObject decodeMethodAndGetInputABIObject(String abi, String methodName, String input)
// ABI + 二进制code + 交易input + Object返回
List<Object> decodeConstructorInput(String abi, String bin, String input)
// ABI + 二进制code + 交易input + String返回
List<String> decodeConstructorInputToString(String abi, String bin, String input)
// ABI定义 + 去掉函数选择器之后的被编码过的参数 + Object，ABIObject返回列表
List<Object> decodeMethodInput(ABIDefinition abiDefinition, String input)
List<Object> decodeMethodInputById(String abi, byte[] methodId, byte[] input)
List<Object> decodeMethodInputByInterface(String abi, String methodInterface, byte[] input)
List<String> decodeMethodInputToString(String abi, String methodName, byte[] input)
List<String> decodeMethodInputByIdToString(String abi, byte[] methodId, byte[] input)
List<String> decodeMethodInputByInterfaceToString(String abi, String methodInterface, byte[] input)
```

