# 交易回执解析

标签：``java-sdk`` ``回执解析`` ``事件解析``

----

FISCO BCOS的交易是一段发往区块链系统的请求数据，用于部署合约，调用合约接口，维护合约的生命周期以及管理资产，进行价值交换等。当交易确认后会产生交易回执，[交易回执](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionreceipt)和[交易](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionbyhash)均保存在区块里，用于记录交易执行过程生成的信息，如结果码、事件、消耗的gas量等。用户可以使用交易哈希查询交易回执，判定交易是否完成。  

交易回执包含三个关键字段，分别是input, output , logs:

| 字段   | 类型      | 描述                               |
| :----- | :-------- | :--------------------------------- |
| input  | String    | 交易输入的ABI编码十六进制字符串    |
| output | String    | 交易返回的ABI编码十六进制字符串    |
| logs   | List<Log> | event log列表，保存交易的event信息 |

交易解析功能帮助用户把交易回执解析为json数据。

## 1. 构造TransactionDecoderInterface

创建一个TransactionDecoderService对象。

```
// 初始化SDK
BcosSDK sdk =  BcosSDK.build(configFile);
// 发送群组ID1
Client client = sdk.getClient(Integer.valueOf(1));
// 获取当前群组对应的密码学接口
CryptoSuite cryptoSuite = client.getCryptoSuite();
// 构造TransactionDecoderService实例，传入是否密钥类型参数。
TransactionDecoderInterface decoder = new TransactionDecoderService(cryptoSuite);
```

TransactionDecoderInterface 主要包括以下功能：
// abi在合约生成的java客户端文件夹下,以HelloWorld.sol为例,为HelloWorld.abi中的json字符串。
- **public TransactionResponse decodeReceiptWithValues(String abi, String functionName, TransactionReceipt receipt)：**  解析带函数返回值的交易回执。
- **public TransactionResponse decodeReceiptWithoutValues(String abi, TransactionReceipt transactionReceipt)：** 解析不带函数返回值的交易回执。
- **public Map\<String, List\<List\<Object\>\>\>\> decodeEvents(String abi, List\<Logs\> logs)：** 解析交易事件。
- **public TransactionResponse decodeReceiptStatus(TransactionReceipt receipt)：** 解析回执的状态和报错信息等。

### 解析合约函数示例
我们以一个简单的递增函数为例，来演示如何解析交易。递增函数对应的solidity代码如下：
```
function incrementUint256(uint256 v) public returns(uint256){
    _uint256V = v + 1 ;
    emit LogIncrement(msg.sender, v);
    return _uint256V;
}
```
在上面这段代码中，首先将传入的参数加1，然后记录了递增事件（event），最后返回结果。

## 2. 解析带返回值的交易


传入合约的abi文件，调用函数的名称，以及交易回执，解析交易结果。

```
TransactionResponse transactionResponse = decoder.decodeReceiptWithValues(abi, "incrementUint256", transactionReceipt);

```

### 解析结果示例：


以上函数定义中，有函数返回值，也触发了event调用。我们的传入值v为1. 解析交易执行返回的TransactionReceipt以后，对应的结果如下：

```
{
  "returnCode": 2,
  "returnMessage": "Success",
  "transactionReceipt": {
    "transactionHash": "0x433c41e0bdd5420f07186eb33d47aac9cf4bbfff040d27213f17ad739096f19b",
    "transactionIndex": "0x0",
    "root": "0x3094859967ad37882b450ffa97dc15ad9d1d69cab8d3ff8212f6c200185f7bae",
    "blockNumber": "0x20a",
    "blockHash": "0x02143583560dd1bc0c226f0d2a7bd947170c322ef5be203da3308fdd36fde87e",
    "from": "0x7c8000530ae01adb3f8f77e7096b335eef83172f",
    "to": "0xa90bec2f9957eed99c6856172f0c58a5cb2a46fd",
    "gasUsed": "0xab6a",
    "contractAddress": "0x0000000000000000000000000000000000000000",
    "logs": [
      {
        "address": "0xa90bec2f9957eed99c6856172f0c58a5cb2a46fd",
        "topics": [
          "0xaca9a02cfe513f3f88c54a860469369849c8fa0a2119a8d1f3f75c67ac0c9547"
        ],
        "data": "0x0000000000000000000000007c8000530ae01adb3f8f77e7096b335eef83172f0000000000000000000000000000000000000000000000000000000000000001",
        "blockNumber": null
      }
    ],
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000008000000000080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004000000000040000000000000000000000000000000000000000000000000000000000000000000000000000400000",
    "status": "0x0",
    "input": "0x7c1bf3c50000000000000000000000000000000000000000000000000000000000000001",
    "output": "0x0000000000000000000000000000000000000000000000000000000000000002",
    "txProof": null,
    "receiptProof": null,
    "message": "Success",
    "statusOK": true
  },
  "contractAddress": "0x0000000000000000000000000000000000000000",
  "values": "[2]",
  "events": "{\"LogIncrement\":[\"0x7c8000530ae01adb3f8f77e7096b335eef83172f\",1]}",
  "receiptMessages": "Success",
  "valuesList": [
    2
  ],
  "eventResultMap": {
    "LogIncrement": [
      [
        "0x7c8000530ae01adb3f8f77e7096b335eef83172f",
        1
      ]
    ]
  }
}
```
上述解析的报文中，包含了区块链回执的数据结构的详细字段值。此外，还解析了函数的事件（event）以及返回值。

解析后的函数事件(event)以及返回值，可查看`events`或`eventResultMap`以及`values`或`valuesList`字段。
```
{
   ……
  "values": "[2]",
  "events": "{\"LogIncrement\":[\"0x7c8000530ae01adb3f8f77e7096b335eef83172f\",1]}",
  "valuesList": [
    2
  ],
  "eventResultMap": {
    "LogIncrement": [
      [
        "0x7c8000530ae01adb3f8f77e7096b335eef83172f",
        1
      ]
    ]
  }
}
```


## 3. 解析无返回值的交易
在某些场景下，我们不关心交易的返回值，只需解析函数中触发的事件(event)以及交易回执的详细数据结构。

传入合约的abi文件和交易回执，解析交易结果。

```
TransactionResponse transactionResponseWithoutValues = decoder.decodeReceiptWithoutValues(abi, transactionReceipt);
```

### 解析结果示例：

还是以上节调用incrementUint256函数为例，我们还是解析此交易回执，但不解析函数返回值，返回的结果如下：

```
{
  "returnCode": 2,
  "returnMessage": "Success",
  "transactionReceipt": {
    "transactionHash": "0x433c41e0bdd5420f07186eb33d47aac9cf4bbfff040d27213f17ad739096f19b",
    "transactionIndex": "0x0",
    "root": "0x3094859967ad37882b450ffa97dc15ad9d1d69cab8d3ff8212f6c200185f7bae",
    "blockNumber": "0x20a",
    "blockHash": "0x02143583560dd1bc0c226f0d2a7bd947170c322ef5be203da3308fdd36fde87e",
    "from": "0x7c8000530ae01adb3f8f77e7096b335eef83172f",
    "to": "0xa90bec2f9957eed99c6856172f0c58a5cb2a46fd",
    "gasUsed": "0xab6a",
    "contractAddress": "0x0000000000000000000000000000000000000000",
    "logs": [
      {
        "address": "0xa90bec2f9957eed99c6856172f0c58a5cb2a46fd",
        "topics": [
          "0xaca9a02cfe513f3f88c54a860469369849c8fa0a2119a8d1f3f75c67ac0c9547"
        ],
        "data": "0x0000000000000000000000007c8000530ae01adb3f8f77e7096b335eef83172f0000000000000000000000000000000000000000000000000000000000000001",
        "blockNumber": null
      }
    ],
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000008000000000080000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000004000000000040000000000000000000000000000000000000000000000000000000000000000000000000000400000",
    "status": "0x0",
    "input": "0x7c1bf3c50000000000000000000000000000000000000000000000000000000000000001",
    "output": "0x0000000000000000000000000000000000000000000000000000000000000002",
    "txProof": null,
    "receiptProof": null,
    "message": "Success",
    "statusOK": true
  },
  "contractAddress": "0x0000000000000000000000000000000000000000",
  "values": null,
  "events": "{\"LogIncrement\":[\"0x7c8000530ae01adb3f8f77e7096b335eef83172f\",1]}",
  "receiptMessages": "Success",
  "valuesList": null,
  "eventResultMap": {
    "LogIncrement": [
      [
        "0x7c8000530ae01adb3f8f77e7096b335eef83172f",
        1
      ]
    ]
  }
}
```

上述解析的报文结果中，包含了区块链回执的数据结构的详细字段值和解析后的函数事件(event)。

解析后的函数事件(event)，可查看`events`或`eventResultMap`字段。
```
{
  ……
  "events": "{\"LogIncrement\":[\"0x7c8000530ae01adb3f8f77e7096b335eef83172f\",1]}",
  "eventResultMap": {
    "LogIncrement": [
      [
        "0x7c8000530ae01adb3f8f77e7096b335eef83172f",
        1
      ]
    ]
  }
}
```


## 4. 解析事件（event）

只解析调用函数过程中触发的事件。传入合约的abi文件和交易回执的logs，解析交易结果；返回事件名和事件List的Map。

```
Map<String, List<List<Object>>>> events = decoder.decodeEvents(abi, transactionReceipt.getLogs());
```

### 解析结果示例：

还是以上节调用incrementUint256函数为例，现在演示只解析事件（event），返回的结果如下：
```
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

```
TransactionResponse transactionResponse = decoder.decodeReceiptStatus(transactionReceipt);
```

### 解析结果示例：

对应的solidity代码：
```
function setBytesMapping(bytes[] bytesArray) public returns (bool) {
    require(bytesArray.length>1, "Bytes array is less than 2");
    _bytesMapping[bytesArray[0]] = bytesArray;
    return true;
}
```


以下函数执行中，交易执行失败，在执行require语句后报错。解析交易执行返回的TransactionReceipt以后，对应的结果如下：
```
{
  "returnCode": 22,
  "returnMessage": "Bytes array is less than 2",
  "transactionReceipt": null,
  "contractAddress": null,
  "values": null,
  "events": null,
  "receiptMessages": "Bytes array is less than 2",
  "valuesList": null,
  "eventResultMap": null
}
```
