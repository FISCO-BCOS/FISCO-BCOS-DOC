# 交易解析
FISCO BCOS的交易是一段发往区块链系统的请求数据，用于部署合约，调用合约接口，维护合约的生命周期以及管理资产，进行价值交换等。当交易确认后会产生交易回执，[交易回执](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionreceipt)和[交易](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionbyhash)均保存在区块里，用于记录交易执行过程生成的信息，如结果码、日志、消耗的gas量等。用户可以使用交易哈希查询交易回执，判定交易是否完成。  

交易回执包含三个关键字段，分别是input（目前dev分支编译的fisco bcos包含该字段，后续合入2.0.0版本）, output , logs:

| 字段   | 类型      | 描述                               |
| :----- | :-------- | :--------------------------------- |
| input  | String    | 交易输入的ABI编码十六进制字符串    |
| output | String    | 交易返回的ABI编码十六进制字符串    |
| logs   | List<Log> | event log列表，保存交易的event信息 |

交易解析功能帮助用户解析这三个字段为json数据和java对象。

```eval_rst
.. important:: 代码
    - fisco bcos
     分支：https://github.com/FISCO-BCOS/FISCO-BCOS/tree/dev
    - web3sdk
     分支：https://github.com/FISCO-BCOS/web3sdk/tree/dev
     
     maven版本：2.0.34-SNAPSHOT
```

## 引入jar包
解析工具类在web3sdk中，应用需要首先在build.gradle配置文件中增加如下配置，引入web3sdk jar包。
```xml
repositories {
    maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
    maven { url "https://dl.bintray.com/ethereum/maven/" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    mavenCentral()
}
compile group: "org.fisco-bcos", name: "web3sdk", version: "2.0.34-SNAPSHOT"
```

## 接口说明
代码包路径``` org.fisco.bcos.web3j.tx.txdecode ```，使用`TransactionDecoderFactory`工厂类建立交易解析对象`TransactionDecoder`，有两种方式：

1. ``` TransactionDecoder buildTransactionDecoder(String abi, String bin); ```  
   
   abi：合约的ABI   
   
   bin：合约bin，暂无使用，可以直接传入空字符串""  

2. ``` TransactionDecoder buildTransactionDecoder(String contractName); ```  
   
   contractName：合约名称，在应用的根目录下创建`solidity`目录，将交易相关的合约放在`solidity`目录，通过指定合约名获取交易解析对象

交易解析对象`TransactionDecoder`接口列表：
1. `String decodeInputReturnJson(String input)`  
   
   解析input，将结果封装为json字符串，json格式
   ```json
   {"data":[{"name":"","type":"","data":} ... ],"function":"","methodID":""}
   ```
   function : 函数签名字符串  

   methodID : [函数选择器](https://solidity.readthedocs.io/en/develop/abi-spec.html#function-selector)

2. `InputAndOutputResult decodeInputReturnObject(String input)`  
   
   解析input，返回Object对象，InputAndOutputResult结构:
   ```java
   public class InputAndOutputResult {
      private String function; // 函数签名
      private String methodID; // methodID
      private List<ResultEntity> result; // 返回列表
    }

   public class ResultEntity {
      private String name;  // 字段名称, 解析output返回时，值为空字符串
      private String type;  // 字段类型
      private Object data;  // 字段值
    }
   ```

3. `String decodeOutputReturnJson(String input, String output)`  
   
   解析output，将结果封装为json字符串，格式同```decodeInputReturnJson```

4. `InputAndOutputResult decodeOutputReturnObject(String input, String output)`  
   
   解析output，返回java Object对象

5. `String decodeEventReturnJson(List<Log> logList)`  
   
   解析event列表，将结果封装为json字符串，json格式
   ```json
   {"event1签名":[[{"name":"","type":"","data":}...]...],"event2签名":[[{"name":"","type":"","data":}...]...]...}
   ```

6. `Map<String, List<List<ResultEntity>>> decodeEventReturnObject(List<Log> logList)`  
   
   解析event列表，返回java Map对象，key为[event签名](https://solidity.readthedocs.io/en/develop/abi-spec.html#events)字符串，`List<ResultEntity>`为交易中单个event参数列表，`List<List<ResultEntity>>`表示单个交易可以包含多个event

`TransactionDecoder`对input，output和event logs均分别提供返回json字符串和java对象的方法。json字符串方便客户端处理数据，java对象方便服务端处理数据。

## 示例
以`TxDecodeSample`合约为例说明接口的使用：
```solidity
pragma solidity ^0.4.24;
contract TxDecodeSample
{
    event Event1(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs);
    event Event2(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes  _bs);
    
    function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) public constant returns (uint256,int256,bool,address,bytes32,string,bytes)
    {
      Event1(_u, _i, _b, _addr, _bs32, _s, _bs);
      return (_u, _i, _b, _addr, _bs32, _s, _bs);
    }
    
    function do_event(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) public 
    {
      Event1(_u, _i, _b, _addr, _bs32, _s, _bs);
      Event2(_u, _i, _b, _addr, _bs32, _s, _bs);
    }
}
```

使用`buildTransactionDecoder` 创建`TxDecodeSample`合约的解析对象：
```java
// TxDecodeSample合约ABI
String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"_u\",\"type\":\"uint256\"},{\"name\":\"_i\",\"type\":\"int256\"},{\"name\":\"_b\",\"type\":\"bool\"},{\"name\":\"_addr\",\"type\":\"address\"},{\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"name\":\"_s\",\"type\":\"string\"},{\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"do_event\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"Event1\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"Event2\",\"type\":\"event\"},{\"constant\":true,\"inputs\":[{\"name\":\"_u\",\"type\":\"uint256\"},{\"name\":\"_i\",\"type\":\"int256\"},{\"name\":\"_b\",\"type\":\"bool\"},{\"name\":\"_addr\",\"type\":\"address\"},{\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"name\":\"_s\",\"type\":\"string\"},{\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"echo\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"bool\"},{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]";
String bin = "";
TransactionDecoder txDecodeSampleDecoder = TransactionDecoderFactory.buildTransactionDecoder(abi, bin);
```

### 解析input
调用``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```
```java
// function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) 
String input = "0x406d373b000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000";
String jsonResult = txDecodeSampleDecoder.decodeInputReturnJson(input);
InputAndOutputResult objectResult = txDecodeSampleDecoder.decodeInputReturnObject(input);
System.out.println("json => \n" + jsonResult);
System.out.println("object => \n" + objectResult);
```

输出：
```java
json => 
{
  "function": "echo(uint256,int256,bool,address,bytes32,string,bytes)",
  "methodID": "0x406d373b",
  "result": [
    {
      "name": "_u",
      "type": "uint256",
      "data": 111111
    },
    {
      "name": "_i",
      "type": "int256",
      "data": -1111111
    },
    {
      "name": "_b",
      "type": "bool",
      "data": false
    },
    {
      "name": "_addr",
      "type": "address",
      "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a"
    },
    {
      "name": "_bs32",
      "type": "bytes32",
      "data": "abcdefghiabcdefghiabcdefghiabhji"
    },
    {
      "name": "_s",
      "type": "string",
      "data": "章鱼小丸子ljjkl;adjsfkljlkjl"
    },
    {
      "name": "_bs",
      "type": "bytes",
      "data": "sadfljkjkljkl"
    }
  ]
}

object => 
InputAndOutputResult[
  function=echo(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes),
  methodID=0x406d373b,
  result=[
    ResultEntity[
      name=_u,
      type=uint256,
      data=111111
    ],
    ResultEntity[
      name=_i,
      type=int256,
      data=-1111111
    ],
    ResultEntity[
      name=_b,
      type=bool,
      data=false
    ],
    ResultEntity[
      name=_addr,
      type=address,
      data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
    ],
    ResultEntity[
      name=_bs32,
      type=bytes32,
      data=abcdefghiabcdefghiabcdefghiabhji
    ],
    ResultEntity[
      name=_s,
      type=string,
      data=章鱼小丸子ljjkl;adjsfkljlkjl
    ],
    ResultEntity[
      name=_bs,
      type=bytes,
      data=sadfljkjkljkl
    ]
  ]
]

```
### 解析output
调用``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```，echo接口直接将输入返回，因此返回与输入相同

```java
//  function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  public constant returns (uint256,int256,bool,address,bytes32,string,bytes)
String input = “0x406d373b000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000”;

String output = "“0x000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000";

String jsonResult = txDecodeSampleDecoder.decodeOutputReturnJson(input, output);
InputAndOutputResult objectResult = txDecodeSampleDecoder.decodeOutputReturnObject(input, output);
System.out.println("json => \n" + jsonResult);
System.out.println("object => \n" + objectResult);
```

结果：
```java
json => 
{
  "function": "echo(uint256,int256,bool,address,bytes32,string,bytes)",
  "methodID": "0x406d373b",
  "result": [
    {
      "name": "",
      "type": "uint256",
      "data": 111111
    },
    {
      "name": "",
      "type": "int256",
      "data": -1111111
    },
    {
      "name": "",
      "type": "bool",
      "data": false
    },
    {
      "name": "",
      "type": "address",
      "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a"
    },
    {
      "name": "",
      "type": "bytes32",
      "data": "abcdefghiabcdefghiabcdefghiabhji"
    },
    {
      "name": "",
      "type": "string",
      "data": "章鱼小丸子ljjkl;adjsfkljlkjl"
    },
    {
      "name": "",
      "type": "bytes",
      "data": "sadfljkjkljkl"
    }
  ]
}

object => 
InputAndOutputResult[
  function=echo(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes),
  methodID=0x406d373b,
  result=[
    ResultEntity[
      name=,
      type=uint256,
      data=111111
    ],
    ResultEntity[
      name=,
      type=int256,
      data=-1111111
    ],
    ResultEntity[
      name=,
      type=bool,
      data=false
    ],
    ResultEntity[
      name=,
      type=address,
      data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
    ],
    ResultEntity[
      name=,
      type=bytes32,
      data=abcdefghiabcdefghiabcdefghiabhji
    ],
    ResultEntity[
      name=,
      type=string,
      data=章鱼小丸子ljjkl;adjsfkljlkjl
    ],
    ResultEntity[
      name=,
      type=bytes,
      data=sadfljkjkljkl
    ]
  ]
]
```

### 解析event logs

调用``` function do_event(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` 接口，输入参数为```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```，解析交易中的logs

```java
// transactionReceipt为调用do_event接口的交易回执
String jsonResult = txDecodeSampleDecoder.decodeEventReturnJson(transactionReceipt.getLogs());
String mapResult = txDecodeSampleDecoder.decodeEventReturnJson(transactionReceipt.getLogs());

System.out.println("json => \n" + jsonResult);
System.out.println("map => \n" + mapResult);
```

结果：
```java
json => 
{
  "Event1(uint256,int256,bool,address,bytes32,string,bytes)": [
    [
      {
        "name": "_u",
        "type": "uint256",
        "data": 111111,
        "indexed": false
      },
      {
        "name": "_i",
        "type": "int256",
        "data": -1111111,
        "indexed": false
      },
      {
        "name": "_b",
        "type": "bool",
        "data": false,
        "indexed": false
      },
      {
        "name": "_addr",
        "type": "address",
        "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a",
        "indexed": false
      },
      {
        "name": "_bs32",
        "type": "bytes32",
        "data": "abcdefghiabcdefghiabcdefghiabhji",
        "indexed": false
      },
      {
        "name": "_s",
        "type": "string",
        "data": "章鱼小丸子ljjkl;adjsfkljlkjl",
        "indexed": false
      },
      {
        "name": "_bs",
        "type": "bytes",
        "data": "sadfljkjkljkl",
        "indexed": false
      }
    ]
  ],
  "Event2(uint256,int256,bool,address,bytes32,string,bytes)": [
    [
      {
        "name": "_u",
        "type": "uint256",
        "data": 111111,
        "indexed": false
      },
      {
        "name": "_i",
        "type": "int256",
        "data": -1111111,
        "indexed": false
      },
      {
        "name": "_b",
        "type": "bool",
        "data": false,
        "indexed": false
      },
      {
        "name": "_addr",
        "type": "address",
        "data": "0x692a70d2e424a56d2c6c27aa97d1a86395877b3a",
        "indexed": false
      },
      {
        "name": "_bs32",
        "type": "bytes32",
        "data": "abcdefghiabcdefghiabcdefghiabhji",
        "indexed": false
      },
      {
        "name": "_s",
        "type": "string",
        "data": "章鱼小丸子ljjkl;adjsfkljlkjl",
        "indexed": false
      },
      {
        "name": "_bs",
        "type": "bytes",
        "data": "sadfljkjkljkl",
        "indexed": false
      }
    ]
  ]
}

map => 
{
  Event1(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes)=[
    [
      ResultEntity[
        name=_u,
        type=uint256,
        data=111111
      ],
      ResultEntity[
        name=_i,
        type=int256,
        data=-1111111
      ],
      ResultEntity[
        name=_b,
        type=bool,
        data=false
      ],
      ResultEntity[
        name=_addr,
        type=address,
        data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
      ],
      ResultEntity[
        name=_bs32,
        type=bytes32,
        data=abcdefghiabcdefghiabcdefghiabhji
      ],
      ResultEntity[
        name=_s,
        type=string,
        data=章鱼小丸子ljjkl;adjsfkljlkjl
      ],
      ResultEntity[
        name=_bs,
        type=bytes,
        data=sadfljkjkljkl
      ]
    ]
  ],
  Event2(uint256,
  int256,
  bool,
  address,
  bytes32,
  string,
  bytes)=[
    [
      ResultEntity[
        name=_u,
        type=uint256,
        data=111111
      ],
      ResultEntity[
        name=_i,
        type=int256,
        data=-1111111
      ],
      ResultEntity[
        name=_b,
        type=bool,
        data=false
      ],
      ResultEntity[
        name=_addr,
        type=address,
        data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a
      ],
      ResultEntity[
        name=_bs32,
        type=bytes32,
        data=abcdefghiabcdefghiabcdefghiabhji
      ],
      ResultEntity[
        name=_s,
        type=string,
        data=章鱼小丸子ljjkl;adjsfkljlkjl
      ],
      ResultEntity[
        name=_bs,
        type=bytes,
        data=sadfljkjkljkl
      ]
    ]
  ]
}
```
