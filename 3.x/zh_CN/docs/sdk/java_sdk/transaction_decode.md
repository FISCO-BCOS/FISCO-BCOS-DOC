# 交易回执解析

标签：``java-sdk`` ``回执解析`` ``事件解析``

----
FISCO BCOS的交易是一段发往区块链系统的请求数据，用于部署合约，调用合约接口，维护合约的生命周期以及管理资产，进行价值交换等。当交易确认后会产生交易回执，[交易回执](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionreceipt)和[交易](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionbyhash)均保存在区块里，用于记录交易执行过程生成的信息，如结果码、事件、消耗的gas量等。用户可以使用交易哈希查询交易回执，判定交易是否完成。  

交易回执包含三个关键字段，分别是input, output , logs:

| 字段   | 类型        | 描述                               |
|:-------|:------------|:-----------------------------------|
| input  | String      | 交易输入的ABI编码十六进制字符串    |
| output | String      | 交易返回的ABI编码十六进制字符串    |
| logs   | List\<Log\> | event log列表，保存交易的event信息 |

交易解析功能帮助用户把交易回执解析为json数据。

## 1. 构造TransactionDecoderInterface

创建一个TransactionDecoderService对象。

```Java
// 初始化SDK
BcosSDK sdk =  BcosSDK.build(configFile);
// 发送群组group
Client client = sdk.getClient("group0");
// 获取当前群组对应的密码学接口
CryptoSuite cryptoSuite = client.getCryptoSuite();
// 构造TransactionDecoderService实例，传入是否密钥类型参数。并且传入是否使用scale解码
TransactionDecoderInterface decoder = new TransactionDecoderService(cryptoSuite, client.isWASM());
```

TransactionDecoderInterface 主要包括以下功能：
abi在合约生成的java客户端文件夹下，以HelloWorld.sol为例，为HelloWorld.abi中的json字符串。

- **public TransactionResponse decodeReceiptWithValues(String abi, String functionName, TransactionReceipt receipt)：**  解析带函数返回值的交易回执。
- **public TransactionResponse decodeReceiptWithoutValues(String abi, TransactionReceipt transactionReceipt)：** 解析不带函数返回值的交易回执。
- **public Map\<String, List\<List\<Object\>\>\>\> decodeEvents(String abi, List\<Logs\> logs)：** 解析交易事件。
- **public TransactionResponse decodeReceiptStatus(TransactionReceipt receipt)：** 解析回执的状态和报错信息等。
- **public String decodeRevertMessage(String output)**：如果回执错误码是回滚，解析output中的revert信息

### 解析合约函数示例

我们以一个简单的递增函数为例，来演示如何解析交易。递增函数对应的solidity代码如下：

```solidity
function incrementUint256(uint256 v) public returns(uint256){
    _uint256V = v + 1 ;
    emit LogIncrement(msg.sender, v);
    return _uint256V;
}
```

在上面这段代码中，首先将传入的参数加1，然后记录了递增事件（event），最后返回结果。

## 2. 解析带返回值的交易

传入合约的abi文件，调用函数的名称，以及交易回执，解析交易结果。

```Java
TransactionResponse transactionResponse = decoder.decodeReceiptWithValues(abi, "incrementUint256", transactionReceipt);
```

### 解析结果示例

以上函数定义中，有函数返回值，也触发了event调用。我们的传入值v为1. 解析交易执行返回的TransactionReceipt以后，对应的结果如下：

```json
{
        "id" : 2,
        "jsonrpc" : "2.0",
        "result" :
        {
                "blockNumber" : 10,
                "checksumContractAddress" : "",
                "contractAddress" : "",
                "extraData" : "",
                "from" : "0xc3e90bebf6dd43ef62e96ba622e324266f7dde1e",
                "gasUsed" : "8880",
                "hash" : "0x0d387a50debfe9332ba91080a0c3c7bc4ac023d08b583939048d819c33a5162e",
                "logEntries" :
                [
                        {
                                "address" : "0102e8b6fc8cdf9626fddc1c3ea8c1e79b3fce94",
                                "data" : "0x000000000000000000000000c3e90bebf6dd43ef62e96ba622e324266f7dde1e0000000000000000000000000000000000000000000000000000000000000001",
                                "topics" :
                                [
                                        "0xaca9a02cfe513f3f88c54a860469369849c8fa0a2119a8d1f3f75c67ac0c9547"
                                ]
                        }
                ],
                "message" : "",
                "output" : "0x0000000000000000000000000000000000000000000000000000000000000002",
                "status" : 0,
                "to" : "0x0102e8b6fc8cdf9626fddc1c3ea8c1e79b3fce94",
                "transactionHash" : "0xaa333f8679673d14ff641b388b00cc7a54d80e3dd6051198518a6294075b310a",
                "version" : 0
        }
}
```

上述解析的报文中，包含了区块链回执的数据结构的详细字段值。此外，还解析了函数的事件（event）以及返回值。

解析后的函数事件(event)以及返回值，可查看`events`或`eventResultMap`以及`values`或`valuesList`字段。

```json
{
   ……
  "logEntries": {
    "LogIncrement": [
      [
        "0xc3e90bebf6dd43ef62e96ba622e324266f7dde1e",
        1
      ]
    ]
  }
}
```

## 3. 解析无返回值的交易

在某些场景下，我们不关心交易的返回值，只需解析函数中触发的事件(event)以及交易回执的详细数据结构。

传入合约的abi文件和交易回执，解析交易结果。

```Java
TransactionResponse transactionResponseWithoutValues = decoder.decodeReceiptWithoutValues(abi, transactionReceipt);
```

### 解析结果示例

还是以上节调用incrementUint256函数为例，我们还是解析此交易回执，但不解析函数返回值，返回的结果如下：

```json
{
        "id" : 2,
        "jsonrpc" : "2.0",
        "result" :
        {
                "blockNumber" : 10,
                "checksumContractAddress" : "",
                "contractAddress" : "",
                "extraData" : "",
                "from" : "0xc3e90bebf6dd43ef62e96ba622e324266f7dde1e",
                "gasUsed" : "8880",
                "hash" : "0x0d387a50debfe9332ba91080a0c3c7bc4ac023d08b583939048d819c33a5162e",
                "logEntries" :
                [
                        {
                                "address" : "0102e8b6fc8cdf9626fddc1c3ea8c1e79b3fce94",
                                "data" : "0x000000000000000000000000c3e90bebf6dd43ef62e96ba622e324266f7dde1e0000000000000000000000000000000000000000000000000000000000000001",
                                "topics" :
                                [
                                        "0xaca9a02cfe513f3f88c54a860469369849c8fa0a2119a8d1f3f75c67ac0c9547"
                                ]
                        }
                ],
                "message" : "",
                "output" : "0x0000000000000000000000000000000000000000000000000000000000000002",
                "status" : 0,
                "to" : "0x0102e8b6fc8cdf9626fddc1c3ea8c1e79b3fce94",
                "transactionHash" : "0xaa333f8679673d14ff641b388b00cc7a54d80e3dd6051198518a6294075b310a",
                "version" : 0
        }
}
```

上述解析的报文结果中，包含了区块链回执的数据结构的详细字段值和解析后的函数事件(event)。

解析后的函数事件(event)，可查看`events`或`eventResultMap`字段。

```json
{
   ……
  "logEntries": {
    "LogIncrement": [
      [
        "0xc3e90bebf6dd43ef62e96ba622e324266f7dde1e",
        1
      ]
    ]
  }
}
```

## 4. 解析事件（event）

只解析调用函数过程中触发的事件。传入合约的abi文件和交易回执的logs，解析交易结果；返回事件名和事件List的Map。

```Java
Map<String, List<List<Object>>>> events = decoder.decodeEvents(abi, transactionReceipt.getLogs());
```

### 解析结果示例

还是以上节调用incrementUint256函数为例，现在演示只解析事件（event），返回的结果如下：

```json
{
  "LogIncrement": [
    [
      "0x7c8000530ae01adb3f8f77e7096b335eef83172f",
      1
    ]
  ]
}
```

## 5. 解析回执的错误信息

传入交易回执，解析返回数据，并解析为TransactionResponse对象。

```Java
TransactionResponse transactionResponse = decoder.decodeReceiptStatus(transactionReceipt);
```

### 解析结果示例

对应的solidity代码：

```solidity
function setBytesMapping(bytes[] bytesArray) public returns (bool) {
    require(bytesArray.length>1, "Bytes array is less than 2");
    _bytesMapping[bytesArray[0]] = bytesArray;
    return true;
}
```

以下函数执行中，交易执行失败，在执行require语句后报错。解析交易执行返回的TransactionReceipt以后，对应的结果如下：

```json
{
        "id" : 16,
        "jsonrpc" : "2.0",
        "result" :
        {
                "blockNumber" : 12,
                "checksumContractAddress" : "",
                "contractAddress" : "",
                "extraData" : "",
                "from" : "0xa38e104bb4a92a52452b48342c935f68df20c2ce",
                "gasUsed" : "1132",
                "hash" : "0x",
                "logEntries" : [],
                "message" : "",
                "output" : "0x08c379a00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000001a4279746573206172726179206973206c657373207468616e2032000000000000",
                "status" : 16,
                "to" : "0x19a6434154de51c7a7406edf312f01527441b561",
                "transactionHash" : "0x8075a4f016dec5147eb60df219529c3d504c5fad2c16691b39cff457692afeb1",
                "version" : 0
        }
}
```
