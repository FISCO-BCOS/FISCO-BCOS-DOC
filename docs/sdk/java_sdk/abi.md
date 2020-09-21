# ABI解析

ABI（Application Binary Interface）定义为调用智能合约函数以及合约之间函数调用的消息编码格式。它作为智能合约函数调用的接口说明，定义了合约的函数签名，参数编码，返回结果编码等。

在Java SDK中，`org.fisco.bcos.sdk.abi.ABICodec`类提供了构造交易`data`的字段、解析交易返回值及事件回调中及解析事件回调EventLog对象`data`字段的功能。

## 构造交易data

交易的`data`由两部分组成，函数选择器及该函数的参数编码。编码后的ABI字节码，可用于交易发送或方法调用。其中`data`的前四个字节数据指定了要调用的函数选择器（也成为函数签名），函数选择器的计算方式为函数声明（去除空格）的哈希，取前4个字节。`data`的剩余部分为输入参数根据ABI编码之后的结果。

根据函数指定方式及参数输入格式的不同，`ABICodec`分别提供了以下接口计算交易的`data`。

- 提供函数名以及Object列表格式描述的函数参数：String encodeMethod(String ABI, String methodName, List<Object> params)
- 提供函数完整声明及Object列表格式描述的函数参数：String encodeMethodByInterface(String ABI, String methodInterface, List<Object> params)
- 提供函数签名及Object列表格式描述的函数参数：String encodeMethodById(String ABI, String methodId, List<Object> params)
- 提供函数名以及String列表格式描述的函数参数：String encodeMethodFromString(String ABI, String methodName, List<String> params)
- 提供函数完整声明及String列表格式描述的函数参数：String encodeMethodByInterfaceFromString(String ABI, String methodInterface, List<String> params)
- 提供函数签名及String列表格式描述的函数参数：String encodeMethodByIdFromString(String ABI, String methodId, List<String> params)

以下以`encodeMethod`为例举例说明使用方法，其他接口的使用方法类似。

```Java
  // 构造参数列表
  // [{"name": "Hello world!", "count": 100, "items": [{"a": 1, "b": 2, "c": 3}]}, {"name":"Hello world2", "count": 200, "items": [{"a": 1, "b": 2, "c": 3}]}]
  List<Object> argsObjects = new ArrayList<Object>();
  argsObjects.add(new BigInteger("100"));
  List<Info> listParams = new ArrayList<Info>();
  Item item1 = new Item(new BigInteger("1"), new BigInteger("2"), new BigInteger("3"));
  Item[] listItem1 = {item1};
  Info info1 = new Info("Hello world!", new BigInteger("100"), listItem1);
  listParams.add(info1);
  Item item2 = new Item(new BigInteger("5"), new BigInteger("6"), new BigInteger("7"));
  Item[] listItem2 = {item2};
  Info info2 = new Info("Hello world2", new BigInteger("200"), listItem2);
  listParams.add(info2);
  argsObjects.add(listParams);
  argsObjects.add("Hello world!");

  BcosSDK sdk =  BcosSDK.build(configFile);
  Client client = sdk.getClient(Integer.valueOf(1));
  ABICodec abiCodec = new ABICodec(client.getCryptoInterface());
  String abi = ""; // 合约ABI编码，省略
  try {
    String encoded = abiCodec.encodeMethod(abi, "test", argsObjects)); // test为合约中函数
    logger.info("encode method result, " + encoded);   
  } catch (ABICodecException e) {
    logger.info("encode method error, " + e.getMessage());
  }
```

## 解析交易返回值

根据函数指定方式及要求返回格式的不同，`ABICodec`分别提供了以下接口解析函数返回值。

- 提供函数名及要求Object列表格式的函数返回值：List<Object> decodeMethod(String ABI, String methodName, String output)
- 提供函数完整声明及要求Object列表格式的函数返回值：List<Object> decodeMethodByInterface(String ABI, String methodInterface, String output)
- 提供函数签名及要求Object列表格式的函数返回值：List<Object> decodeMethodById(String ABI, String methodId, String output)
- 提供函数名及要求String列表格式的函数返回值：List<String> decodeMethodToString(String ABI, String methodName, String output)
- 提供函数完整声明及要求String列表格式的函数返回值：List<String> decodeMethodByInterfaceToString(String ABI, String methodInterface, String output)
- 提供函数签名及要求String列表格式的函数返回值：List<String> decodeMethodByIdToString(String ABI, String methodId, String output)

上述接口参数中的`output`为交易执行返回的`TransactionReceipt`中的`output`字段。接口的使用方法可参考构造交易data的接口用法。

## 解析EventLog

根据Event指定方式及要求返回格式的不同，`ABICodec`分别提供了以下接口解析EventLog。

- 提供Event名及要求Object列表格式的函数返回值：List<Object> decodeEvent(String ABI, String eventName, String output)
- 提供Event完整声明及要求Object列表格式的函数返回值：List<Object> decodeEventByInterface(String ABI, String eventSignature, String output)
- 提供Topic及要求Object列表格式的函数返回值：List<Object> decodeEventByTopic(String ABI, String eventTopic, String output)
- 提供Event名及要求String列表格式的函数返回值：List<String> decodeEventToString(String ABI, String eventName, String output)
- 提供Event完整声明及要求String列表格式的函数返回值：List<String> decodeEventByInterfaceToString(String ABI, String eventSignature, String output)
- 提供Topic签名及要求String列表格式的函数返回值：List<String> decodeEventByTopicToString(String ABI, String eventTopic, String output)

对于事件推送，Java SDK需用户可以通过继承`EventCallback`类，重写`onReceiveLog`接口，实现自己的回调逻辑处理。以下例子的处理过程中使用`decodeEvent`对EventLog进行解析。其他接口的使用方法类似。

```Java
class SubscribeCallback implements EventCallback {
  public void onReceiveLog(int status, List<EventLog> logs) {
  if (logs != null) {
    String abi = ""; // 合约ABI编码，省略
    for (EventLog log : logs) {
      ABICodec abiCodec = new ABICodec(client.getCryptoInterface()); // client初始化，省略
      try {
        List<Object> list = abiCodec.decodeEvent(abi, "LogSetValues", log.getData());
        logger.debug("decode event log content, " + list);
      } catch (ABICodecException e) {
        logger.error("decode event log error, " + e.getMessage());
      }
    }
  }
}
```