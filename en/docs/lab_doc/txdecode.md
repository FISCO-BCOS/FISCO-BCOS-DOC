# Transaction analysis
The transaction in FISCO BCOS is the request data toward blockchain system to deploy contract, call contract API, maintain contract lifecycle and manage assets, value exchange, etc. There will be transaction receipt after it is confirmed: [Transaction receipt](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionreceipt) and [Transaction](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/api.html#gettransactionbyhash) are all stored in block to record the information generated during transaction execution, like result code, log, consumed gas amount, etc. User can use transaction hash to retrieve transaction receipt and know whether the transaction is accomplished.

Transaction receipt contains three key fields: input (currently fisco bcos compiled by dev branch includes this field), output, logs.

| field   | type      | description                               |
| :----- | :-------- | :--------------------------------- |
| input  | String    | ABI coded hexadecimal string input by transaction    |
| output | String    | ABI coded hexadecimal string returned by transaction    |
| logs   | List<Log> | event log list storing event information of transaction |

Transaction analysis can help user to analyze the 3 fields as json data and java object.

```eval_rst
.. important:: code
    - fisco bcos
     branch：https://github.com/FISCO-BCOS/FISCO-BCOS/tree/dev
    - web3sdk
     branch：https://github.com/FISCO-BCOS/web3sdk/tree/dev
     
     maven version：2.0.34-SNAPSHOT
```

## Import jar packet
Analysis tool class is in web3sdk. First, add the following configurations in build.gradle config file and import web3sdk jar packet.
```xml
repositories {
    maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
    maven { url "https://dl.bintray.com/ethereum/maven/" }
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
    mavenCentral()
}
compile group: "org.fisco-bcos", name: "web3sdk", version: "2.0.34-SNAPSHOT"
```

## API description
Code packet route ``` org.fisco.bcos.web3j.tx.txdecode ```, use `TransactionDecoderFactory` factory class to build transaction decoder `TransactionDecoder`. There are 2 ways:

1. ``` TransactionDecoder buildTransactionDecoder(String abi, String bin); ```  
   
   abi：contract ABI   
   
   bin：contract bin, can import empty string "" when not available  

2. ``` TransactionDecoder buildTransactionDecoder(String contractName); ```  
   
   contractName：contract name, create `solidity` directory under root directory of the application, put the contract under `solidity` directory and get transaction decoder through contract name.

Transaction decoder `TransactionDecoder` API list:
1. `String decodeInputReturnJson(String input)`  
   
   Analyze input, seal the result as json character string in json format
   ```json
   {"data":[{"name":"","type":"","data":} ... ],"function":"","methodID":""}
   ```
   function : function signature string
   methodID : [function selector](https://solidity.readthedocs.io/en/develop/abi-spec.html#function-selector)

2. `List<ResultEntity> decodeInputReturnObject(String input)`  
   
   Analyze input, return java List object, ResultEntity structure
   ```java
   public class ResultEntity {
      private String name;  // field name, analyze output and return with empty string
      private String type;  // field type
      private Object data;  // field value
    }
   ```

3. `String decodeOutputReturnJson(String input, String output)`  
   
   Analyze output, seal the result as json string, the same format with ```decodeInputReturnJson```

4. `List<ResultEntity> decodeOutputReturnObject(String input, String output)`  
   
   Analyze output, return java List object

5. `String decodeEventReturnJson(List<Log> logList)`  
   
   Analyze event list, seal the result as json string with json format
   ```json
   {"event1 signature":[[{"name":"_u","type":"","data":}...]...],"event2 signature":[[{"name":"_u","type":"","data":}...]...]...}
   ```

6. `Map<String, List<List<ResultEntity>>> decodeEventReturnObject(List<Log> logList)`  
   
   Analyze event list, return java Map object, key is [event signature](https://solidity.readthedocs.io/en/develop/abi-spec.html#events) string, List<List<ResultEntity>> is all the event parameter information in the transaction.

`TransactionDecoder` provides methods of returning json string and java object respectively to input, output and event logs. Json string helps client end to easily deal data. Java object helps to easily deal data for server.

## Example
Here is an example of `TxDecodeSample` contract to describe the use of API:
```solidity
pragma solidity ^0.4.24;
contract TxDecodeSample
{
    event Event1(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs);
    event Event2(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs);
    
    
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

Use `buildTransactionDecoder` to build the transaction decoder of `TxDecodeSample` contract.
```
// TxDecodeSample合约ABI
String abi = "[{\"constant\":false,\"inputs\":[{\"name\":\"_u\",\"type\":\"uint256\"},{\"name\":\"_i\",\"type\":\"int256\"},{\"name\":\"_b\",\"type\":\"bool\"},{\"name\":\"_addr\",\"type\":\"address\"},{\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"name\":\"_s\",\"type\":\"string\"},{\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"do_event\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"Event1\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"_u\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"_i\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"_b\",\"type\":\"bool\"},{\"indexed\":false,\"name\":\"_addr\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"indexed\":false,\"name\":\"_s\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"Event2\",\"type\":\"event\"},{\"constant\":true,\"inputs\":[{\"name\":\"_u\",\"type\":\"uint256\"},{\"name\":\"_i\",\"type\":\"int256\"},{\"name\":\"_b\",\"type\":\"bool\"},{\"name\":\"_addr\",\"type\":\"address\"},{\"name\":\"_bs32\",\"type\":\"bytes32\"},{\"name\":\"_s\",\"type\":\"string\"},{\"name\":\"_bs\",\"type\":\"bytes\"}],\"name\":\"echo\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"},{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"bool\"},{\"name\":\"\",\"type\":\"address\"},{\"name\":\"\",\"type\":\"bytes32\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"bytes\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"}]";
String bin = "";
TransactionDecoder txDecodeSampleDecoder = TransactionDecoder.buildTransactionDecoder(abi, bin);
```

### Analyze input
Call ``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` API, input parameter ```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```
```java
// function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs) 
String input = “0x406d373b000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000”;
String jsonResult = txDecodeSampleDecoder.decodeInputReturnJson(input);
List<ResultEntity> listResult = txDecodeSampleDecoder.decodeInputReturnObject(input);
System.out.println("json => \n" + jsonResult);
System.out.println("list => \n" + listResult);
```

Output:
```
json => 
{
  "data": [
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
  ],
  "function": "echo(uint256,int256,bool,address,bytes32,string,bytes)",
  "methodID": "0x406d373b"
}

list => 
[ResultEntity [name=_u, type=uint256, data=111111], ResultEntity [name=_i, type=int256, data=-1111111], ResultEntity [name=_b, type=bool, data=false], ResultEntity [name=_addr, type=address, data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a], ResultEntity [name=_bs32, type=bytes32, data=abcdefghiabcdefghiabcdefghiabhji], ResultEntity [name=_s, type=string, data=章鱼小丸子ljjkl;adjsfkljlkjl], ResultEntity [name=_bs, type=bytes, data=sadfljkjkljkl]]

```
### Analyze output
Call ``` function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` API, input parameter ```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```, echo API returns what is input.

```java
//  function echo(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  public constant returns (uint256,int256,bool,address,bytes32,string,bytes)
String input = “0x406d373b000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000”;

String output = "000000000000000000000000000000000000000000000000000000000001b207ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffef0bb90000000000000000000000000000000000000000000000000000000000000000000000000000000000000000692a70d2e424a56d2c6c27aa97d1a86395877b3a6162636465666768696162636465666768696162636465666768696162686a6900000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000000000000001400000000000000000000000000000000000000000000000000000000000000021e7aba0e9b1bce5b08fe4b8b8e5ad906c6a6a6b6c3b61646a73666b6c6a6c6b6a6c00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d736164666c6a6b6a6b6c6a6b6c00000000000000000000000000000000000000";

String jsonResult = txDecodeSampleDecoder.decodeOutputReturnJson(input, output);
List<ResultEntity> listResult = txDecodeSampleDecoder.decodeOutputReturnObject(input, output);
System.out.println("json => \n" + jsonResult);
System.out.println("list => \n" + listResult);
```

Result:
```
json => 
[
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

list => 
[ResultEntity [name=, type=uint256, data=111111], ResultEntity [name=, type=int256, data=-1111111], ResultEntity [name=, type=bool, data=false], ResultEntity [name=, type=address, data=0x692a70d2e424a56d2c6c27aa97d1a86395877b3a], ResultEntity [name=, type=bytes32, data=abcdefghiabcdefghiabcdefghiabhji], ResultEntity [name=, type=string, data=章鱼小丸子ljjkl;adjsfkljlkjl], ResultEntity [name=, type=bytes, data=sadfljkjkljkl]]

```

### Analyze event logs

Call ``` function do_event(uint256 _u,int256 _i,bool _b,address _addr,bytes32 _bs32, string _s,bytes _bs)  ``` API, input parameter ```[ 111111 -1111111 false 0x692a70d2e424a56d2c6c27aa97d1a86395877b3a abcdefghiabcdefghiabcdefghiabhji 章鱼小丸子ljjkl;adjsfkljlkjl sadfljkjkljkl ]```, analyze transaction logs

```java
// transactionReceipt is the transaction receipt of calling do_event API
String jsonResult = txDecodeSampleDecoder.decodeEventReturnJson(transactionReceipt.getLogs());
String mapResult = txDecodeSampleDecoder.decodeEventReturnJson(transactionReceipt.getLogs());

System.out.println("json => \n" + jsonResult);
System.out.println("map => \n" + mapResult);
```

Result:
```java
jsonResult => 
{
  "Event1(uint256,int256,bool,address,bytes32,string,bytes)": [
    [
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
  ],
  "Event2(uint256,int256,bool,address,bytes32,string,bytes)": [
    [
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
  ]
}

map => 
{"Event1(uint256,int256,bool,address,bytes32,string,bytes)":[[{"name":"_u","type":"uint256","data":111111},{"name":"_i","type":"int256","data":-1111111},{"name":"_b","type":"bool","data":false},{"name":"_addr","type":"address","data":"0x692a70d2e424a56d2c6c27aa97d1a86395877b3a"},{"name":"_bs32","type":"bytes32","data":"abcdefghiabcdefghiabcdefghiabhji"},{"name":"_s","type":"string","data":"章鱼小丸子ljjkl;adjsfkljlkjl"},{"name":"_bs","type":"bytes","data":"sadfljkjkljkl"}]],"Event2(uint256,int256,bool,address,bytes32,string,bytes)":[[{"name":"_u","type":"uint256","data":111111},{"name":"_i","type":"int256","data":-1111111},{"name":"_b","type":"bool","data":false},{"name":"_addr","type":"address","data":"0x692a70d2e424a56d2c6c27aa97d1a86395877b3a"},{"name":"_bs32","type":"bytes32","data":"abcdefghiabcdefghiabcdefghiabhji"},{"name":"_s","type":"string","data":"章鱼小丸子ljjkl;adjsfkljlkjl"},{"name":"_bs","type":"bytes","data":"sadfljkjkljkl"}]]}
```
