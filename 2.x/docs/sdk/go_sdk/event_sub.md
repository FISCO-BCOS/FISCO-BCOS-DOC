# 合约事件推送

标签：``go-sdk`` ``事件订阅`` ``Event``

----
## 1. 功能简介

合约事件推送功能提供了合约事件的异步推送机制，客户端向节点发送注册请求，在请求中携带客户端关注的合约事件的参数，节点根据请求参数对请求区块范围的`Event Log`进行过滤，将结果分次推送给客户端。

## 2. 交互协议

客户端与节点的交互基于[`Channel`](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/protocol_description.html#id4)协议。交互分为三个阶段：注册请求，节点回复，`Event Log`数据推送。

### 注册请求

客户端向节点发送事件推送的注册请求:

```Json
// request sample:
{
  "fromBlock": "latest",
  "toBlock": "latest",
  "addresses": [
    "0x1f3119043aaee8e5080967e6f80eb3135086c138"
  ],
  "topics": [
    "0x91c95f04198617c60eaf2180fbca88fc192db379657df0e412a9f7dd4ebbe95d"
  ],
  "groupID": "1",
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967"
}
```

- filerID：字符串类型，每次请求唯一，标记一次注册任务  
- groupID：字符串类型，群组ID  
- fromBlock：整形字符串，初始区块。“latest” 当前块高  
- toBlock：整形字符串，最终区块。“latest” 处理至当前块高时，继续等待新区块  
- addresses：字符串或者字符串数组：字符串表示单个合约地址，数组为多个合约地址，数组可以为空
- topics：字符串类型或者数组类型：字符串表示单个topic，数组为多个topic，数组可以为空

### 节点回复

节点接受客户端注册请求时，会对请求参数进行校验，将是否成功接受该注册请求结果回复给客户端。

```Json
// response sample:
{
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967",
  "result": 0
}
```

- filterID：字符串类型，每次请求唯一，标记一次注册任务  
- result：整形，返回结果。0成功，其余为失败状态码

### Event Log数据推送

节点验证客户端注册请求成功之后，根据客户端请求参数条件，向客户端推送`Event Log`数据。

```Json
// event log push sample:
{
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967",
  "result": 0,
  "logs": [
    
  ]
}
```

- filterID：字符串类型，每次请求唯一，标记一次注册任务  
- result：整形 0：`Event Log`数据推送 1：推送完成。客户端一次注册请求对应节点的数据推送会有多次（请求区块范围比较大或者等待新的区块），`result`字段为1时说明节点推送已经结束  
- logs：Log对象数组，result为0时有效  

## 3. Go SDK教程

### 注册接口  

在FISCO-BCOS Go SDK中`client.Client`类提供合约事件的注册接口，用户可以调用`SubscribeEventLogs`向节点发送注册请求，并设置回调函数。

```go
func (c *Client) SubscribeEventLogs(eventLogParams types.EventLogParams, handler func(int, []types.Log)) error {
	return c.apiHandler.SubscribeEventLogs(eventLogParams, handler)
}
```

#### `types.EventLogParams`注册参数  

事件回调请求注册的参数：  

```go
type EventLogParams struct {
	FromBlock string   `json:"fromBlock"`   // 订阅起始区块 latest表示最新区块
	ToBlock   string   `json:"toBlock"`     // 订阅结束区块 latest表示最新区块
	Addresses []string `json:"addresses"`   // 订阅的合约地址
	Topics    []string `json:"topics"`      // 订阅的事件
	GroupID   string   `json:"groupID"`     
	FilterID  string   `json:"filterID"`
}
```


#### topic计算

Go语言可以使用`github.com/ethereum/go-ethereum/common`包根据合约中定义的事件进行Topic的计算：

```go
topic = common.BytesToHash(crypto.Keccak256([]byte("testEventLog(address,string,uint256)"))).Hex()
```

*需要注意的是uint及int为uint256及int256的别名，即使合约中使用的类型为uint及int，在topic的计算中仍需使用uint256与int256*

## 4. 示例

此处以一个进行数值更改并计算更改次数的`eventDemo.sol`合约进行事件订阅举例：

```solidity
pragma solidity >=0.4.24;

contract eventDemo {
    
    uint256 public count = 0;
    uint256 public num = 0;

    event setNum(address caller,uint256 cnt,uint256 value);   // Declare A Event

    function setNumber(uint256 v) public { 
        count += 1;
        num = v;
        emit setNum(msg.sender,count,num); 
    }
    
    function getNumber()public returns (uint){
        return num;
    }
}
```

该合约提供了`setNumber`及`getNumber`方法，其中调用前者将获得一个抛出事件。

在Fisco Go SDK中有两种事件订阅方式，分别为使用`client`直接进行事件订阅与调用Abigen工具生成的代码中函数进行订阅两种，其中后者须建立`session`。



### 4.1 使用client进行事件订阅

**事件推送订阅**

在Go SDK中给出了[subscriber.go](https://github.com/FISCO-BCOS/go-sdk/blob/master/examples/eventLog/sub/subscriber.go)合约事件订阅样例

*需要注意的是Topic及Address不可置空，置空将无法接收到相应事件。*

```go
func main() {
	...... // 省略
    endpoint := "127.0.0.1:20200" // 部署的Fisco链的ip:channel_port
	privateKey, _ := hex.DecodeString("145e247e170ba3afd6ae97e88f00dbc976c2345d511b0f6713355d19d8b80b58") //用户私钥
	config := &conf.Config{IsHTTP: false, ChainID: 1, CAFile: "ca.crt", Key: "sdk.key", Cert: "sdk.crt", 
		IsSMCrypto: false, GroupID: 1, PrivateKey: privateKey, NodeURL: endpoint}
	var c *client.Client
	var err error
	...... // 省略
	var eventLogParams types.EventLogParams
	eventLogParams.FromBlock = "1"
	eventLogParams.ToBlock = "latest" // 当前配置订阅了从创世区块至最新区块的链上所有事件
	eventLogParams.GroupID = "1"
	var topics = make([]string, 1)
	topics[0] = common.BytesToHash(crypto.Keccak256([]byte("setNum(address,uint256,uint256)"))).Hex() // 事件的Topic计算，注意使用uint256及int256替代uint及int
	eventLogParams.Topics = topics
	var addresses = make([]string, 1)
	addresses[0] = "0xd2cf82e18f3d2c5cae0de87d29994be622f3fdd3" // 所订阅事件对应的合约地址

	eventLogParams.Addresses = addresses
	timeout := 30 * time.Second
	queryTicker := time.NewTicker(timeout)
	defer queryTicker.Stop()
	done := make(chan bool)
	err = c.SubscribeEventLogs(eventLogParams, func(status int, logs []types.Log) { // 调用SubscribeEventLogs函数订阅事件
		...... // 省略
	})
    ......	// 省略
}
```

**Log数据解析**

在使用sdk提供的客户端订阅事件的方式中，获取到的数据需要`abi`进行解析，

首先需要根据事件包含的变量类型构建一个用于解析的结构体：

```go
type setNum struct {
	Caller common.Address
	Cnt    *big.Int
	Value  *big.Int
}
```

此后可采用`abi`工具进行解析：

```go
	err = c.SubscribeEventLogs(eventLogParams, func(status int, logs []types.Log) {
		fmt.Println(logs[0].Data)
		for k, v := range logs {
			var tempABI abi.ABI
			var temp setNum
			tempABI, err := abi.JSON(strings.NewReader(EventDemoABI))
			if err != nil {
				logrus.Println(err.Error())
			}
			err = tempABI.Unpack(&temp, "setNum", v.Data)
			if err != nil {
				logrus.Println(err.Error())
			}
			fmt.Println("log number:", k, temp)
		}
	})

```
此处`abi.Json`所使用到的数据需要使用`abigen`工具生成对应的go文件后获得:

```go
// EventDemoABI is the input ABI used to generate the binding from.
const EventDemoABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"count\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"setNumber\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"num\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getNumber\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"caller\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"cnt\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"setNum\",\"type\":\"event\"}]"
```



### 4.2 使用Abigen工具生成后的函数进行事件订阅

该方式进行事件订阅需先构建与合约的`session`连接，后直接调用`abigen`生成的go文件中函数即可进行事件的订阅与解析：

```go
// 根据配置文件构建eventDemoSession
err := eventDemoSession.WatchAllSetNum(from, func(status int, logs []types.Log) {
    fmt.Println("Status Code:",status)	
	for k, v := range logs {
			log, errs := eventDemoSession.ParseSetNum(v) // 解析数据
			if errs != nil {
				fmt.Println(errs)
			}
        fmt.Println("Log id:", k, "Log Content:",log)
		}
	})
```

关于Session的构建请参考[合约开发样例](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/go_sdk/contractExamples.html)部分中例程。

