# 交易解析
FISCO BCOS的交易是一段发往区块链系统的请求数据，用于部署合约，调用合约接口，维护合约的生命周期以及管理资产，进行价值交换等。当交易确认后会产生交易回执，交易回执和交易均保存在区块里，用于记录交易执行过程生成的信息，如结果码、日志、消耗的gas量等。用户可以使用交易哈希查询交易回执，判定交易是否完成。其中，交易回执包含三个关键字段，分别是input（目前dev分支编译的fisco bcos包含该字段，后续合入2.0.0版本）, output和event logs字段，如下表所示。交易解析功能帮助用户解析这三个字段为json数据和java对象。

| 字段       | 类型         | 描述                          |
| :--------- | :----------- | :----------------------------------- |
| input     | String     | 交易输入（包括调用的合约方法名，参数类型，参数值等）的十六进制字符串 |
| output    | String       | 交易输出（调用合约方法的返回值）的十六进制字符串          |
| event logs   | List<Log>      | event log列表，保存了合约的日志信息                |

```eval_rst
.. important:: 代码位置

    - fisco bcos
     分支：https://github.com/FISCO-BCOS/FISCO-BCOS/tree/dev
    - web3sdk
     maven快照版本：web3sdk-2.0.33-SNAPSHOT.jar
```

## 引入web3sdk jar包
解析工具类在web3sdk中，应用需要首先在build.gradle配置文件中增加如下配置，引入web3sdk jar包。
```xml
repositories {
    maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
    maven { url "https://dl.bintray.com/ethereum/maven/" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    mavenCentral()
}
compile group: "org.fisco-bcos", name: "web3sdk", version: "2.0.33-SNAPSHOT"
```
## 建立交易解析对象
利用`TransactionDecoderFactory`工厂类建立交易解析对象`TransactionDecoder`。该工厂类有两个工厂方法。

传入合约的abi和bin，获取交易解析对象:
```java
TransactionDecoder buildTransactionDecoder(String abi, String bin);
```
在应用的根目录下创建`solidity`目录，将发送交易相关合约放在`solidity`目录，通过指定合约名获取交易解析对象:
```java
TransactionDecoder buildTransactionDecoder(String contractName);
```
**示例：**
```java
String abi = "[{"constant":false,...,"type":"event"}]"
String bin = "0x608..."
TransactionDecoder transactionDecoder = TransactionDecoder buildTransactionDecoder(abi, bin);
```
```java
String contractName = "TableTest";
TransactionDecoder transactionDecoder = TransactionDecoderFactory.buildTransactionDecoder(contractName);
```
## 交易解析对象解析相关字段
`TransactionDecoder`对input, output和event logs均分别提供返回json字符串和java对象的方法。json字符串方便客户端处理数据，java对象方便服务端处理数据。

### 解析input
输入input字符串，返回解析的json字符串（包含参数名，参数类型和参数值）
```java
String decodeInputReturnJson(String input);
```
输入input字符串，返回解析的List\<ResultEntity\>列表（ResultEntity对象包含参数名，参数类型和参数值）
```java
List<ResultEntity> decodeInputReturnObject(String input);
```

**示例：**
```java
String input = “0xefc81a8c...”
String result1 = transactionDecoder.decodeInputReturnJson(input);
List<ResultEntity> result2 = transactionDecoder.decodeInputReturnObject(input);
```
### 解析output
输入output字符串，返回解析的json字符串（包含返回变量的名称，变量类型和值）
```java
String decodeOutputReturnJson(String input, String output);
```
输入output字符串，返回解析的List\<ResultEntity\>列表（ResultEntity对象包含返回变量的名称，变量类型和值）
```java
List<ResultEntity> decodeOutputReturnObject(String input, String output);
```
**示例：**
```java
String input = “0xefc81a8c...”
String output = “"0x00e...”
String result1 = transactionDecoder.decodeOutputReturnJson(input, output);
List<ResultEntity> result2 = transactionDecoder.decodeOutputReturnObject(input, output);
```
### 解析event logs
输入event logs的json字符串，返回解析的json字符串（包含event的名称，event变量名，变量类型和变量值）
```java
String decodeEventReturnJson(String logs);
```
输入event logs列表，返回解析的Map\<String, List\<ResultEntity\>\>对象（map的键是event的名称，ResultEntity对象包含event变量名称，变量类型和值）
```java
Map<String, List<ResultEntity>> decodeEventReturnObject(List<log> logList);
```
**示例：**
```java
String logsStr = "[ {
  "blockHash" : null,
  "blockNumber" : null,
  "address" : "0x8aface7417b85c544f07aa318ee2d9fc607263aa",
  ...
}, ...]"

List<log> logsList = [Log@5668c66f,...]
String result1 = transactionDecoder.decodeEventReturnJson(logsStr);
Map<String, List<ResultEntity>> result2 = transactionDecoder.decodeEventReturnObject(logsList);
```

## 解析结果示例
### input解析结果
返回方法签名，methodID和参数构成的json字符串:
```json
{
  "data" : [ {
    "name" : "name",
    "type" : "string",
    "data" : "fruit"
  }, {
    "name" : "item_id",
    "type" : "int256",
    "data" : 1
  }, {
    "name" : "item_name",
    "type" : "string",
    "data" : "kk"
  } ],
  "function" : "insert(string,int256,string)",
  "methodID" : "0xebf3b24f"
}
```
返回解析的List\<ResultEntity\>列表:
```java
[ResultEntity(name=name, type=string, data=fruit), ResultEntity(name=item_id, type=int256, data=1), ResultEntity(name=item_name, type=string, data=kk)]
```
### output解析结果
返回值构成的json字符串：
```json
[ {
  "name" : "result",
  "type" : "int256",
  "data" : 1
}, {
  "name" : "result_name",
  "type" : "string",
  "data" : "hello"
}, {
  "name" : "",
  "type" : "bytes32",
  "data" : "world"
}, {
  "name" : "addr",
  "type" : "address",
  "data" : "0x0000000000000000000000000000000000000012"
}, {
  "name" : "",
  "type" : "bytes32[]",
  "data" : [ "fisco", "bcos" ]
} ]
```
返回解析的List\<ResultEntity\>列表:
```java
[ResultEntity(name=result, type=int256, data=1), ResultEntity(name=result_name, type=string, data=hello), ResultEntity(name=, type=bytes32, data=world), ResultEntity(name=addr, type=address, data=0x0000000000000000000000000000000000000012), ResultEntity(name=, type=bytes32[], data=[fisco, bcos])]
```
### event logs解析结果
返回event名，变量名，变量类型和变量值构成的json字符串：
```json
{
  "InsertResult" : [ {
    "name" : "count",
    "type" : "int256",
    "data" : 1
  }, {
    "name" : "name",
    "type" : "string",
    "data" : "fruit"
  }, {
    "name" : "item_name",
    "type" : "bytes8",
    "data" : "apple"
  }, {
    "name" : "item_id",
    "type" : "bytes32[]",
    "data" : [ "fisco", "bcos" ]
  } ],
  "UpdateResult" : [ {
    "name" : "count",
    "type" : "int256",
    "data" : 1
  }, {
    "name" : "name",
    "type" : "string",
    "data" : "fruit"
  } ]
}
```
返回解析的Map\<String, List\<ResultEntity\>\>对象：
```java
{InsertResult=[ResultEntity(name=count, type=int256, data=1), ResultEntity(name=name, type=string, data=fruit), ResultEntity(name=item_name, type=bytes8, data=apple), ResultEntity(name=item_id, type=bytes32[], data=[fisco, bcos])], UpdateResult=[ResultEntity(name=count, type=int256, data=1), ResultEntity(name=name, type=string, data=fruit)]}
```