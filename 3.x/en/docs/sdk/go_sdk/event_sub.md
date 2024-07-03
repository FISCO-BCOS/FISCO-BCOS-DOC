# Contract Event Push

Tags: "go-sdk" "event subscription" "Event"

----

## 1. Function Introduction

The contract event push function provides an asynchronous push mechanism for contract events. The client sends a registration request to the node, which carries the parameters of the contract events that the client is concerned about. The node filters the 'Event Log' of the request block range according to the request parameters and pushes the results to the client in stages。We recommend that you use the generated golang wapper to subscribe to contract events. The subscription interface and resolution are automatically generated。

## 2. Interactive Protocol

The interaction between the client and the node is divided into three stages: registration request, node reply, and 'Event Log' data push。

### Registration Request

The client sends a registration request for event push to the node:

```go
type EventLogParams struct {
    FromBlock int64    `json:"fromBlock"`
    ToBlock   int64    `json:"toBlock"`
    Addresses []string `json:"addresses"`
    Topics    []string `json:"topics"`
}

/ / SubscribeEventLogs subscribes to the contract event. The parameters of the contract event to be subscribed to and the function that handles the received event are passed in. The ID of the subscription task is returned successfully, and the error message is returned when it fails。Subscription id can be used to unsubscribe
func (c *Connection) SubscribeEventLogs(eventLogParams types.EventLogParams, handler func(int, []types.Log)) (string, error)
/ / Unsubscribe to the contract event. The ID of the subscription task
func (c *Connection) UnsubscribeEventLogs(taskID string)
```

- FromBlock: Start block (greater than 0)
-ToBlock: -1 indicates the latest block
-Addresses: string array, the array is multiple contract addresses, the array can be empty
-Topics: the array type. The array contains multiple topics. The array can be empty

### Event Log Data Push

After the node verifies that the client registration request is successful, it pushes the 'Event Log' data to the client according to the client request parameters and returns' [] types.Log'。

```go
type Log struct {
    // Consensus fields:
    // address of the contract that generated the event
    Address common.Address `json:"address" gencodec:"required"`
    // list of topics provided by the contract.
    Topics []common.Hash `json:"topics" gencodec:"required"`
    // supplied by the contract, usually ABI-encoded
    Data []byte `json:"data" gencodec:"required"`

    // Derived fields. These fields are filled in by the node
    // but not secured by consensus.
    // block in which the transaction was included
    BlockNumber uint64 `json:"blockNumber"`
    // hash of the transaction
    TxHash common.Hash `json:"transactionHash" gencodec:"required"`
    // index of the transaction in the block
    TxIndex uint `json:"transactionIndex" gencodec:"required"`
    // index of the log in the block
    Index uint `json:"logIndex" gencodec:"required"`
}
```

## 3. Go SDK Tutorial

### Registration Interface

In the FISCO-BCOS Go SDK, the 'client.Client' class provides an interface for registering contract events. You can call 'SubscribeEventLogs' to send a registration request to a node and set a callback function。

```go
func (c *Client) SubscribeEventLogs(eventLogParams types.EventLogParams, handler func(int, []types.Log)) error {
    return c.apiHandler.SubscribeEventLogs(eventLogParams, handler)
}
```

#### 'types.EventLogParams' Register Parameters

Parameters for event callback request registration:

```go
type EventLogParams struct {
    FromBlock string   `json:"fromBlock"'/ / subscription start block latest indicates the latest block
    ToBlock   string   `json:"toBlock"'/ / End of subscription block latest indicates the latest block
    Addresses []string `json:"addresses"'/ / the contract address of the subscription
    Topics    []string `json:"topics"'/ / subscribed events
    GroupID   string   `json:"groupID"`
    FilterID  string   `json:"filterID"`
}
```

#### topic calculation

You can use the 'github.com / ethereum / go-ethereum / common' package to calculate topics based on events defined in contracts
Note that uint and int are aliases of uint256 and int256. Even if the types used in the contract are uint and int, you still need to use uint256 and int256 in the calculation of the topic

```go
topic = common.BytesToHash(crypto.Keccak256([]byte("testEventLog(address,string,uint256)"))).Hex()
```

## 4. Example

Here is an example of subscribing to an event with an 'eventDemo.sol' contract that changes values and counts the number of changes:

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

The contract provides' setNumber 'and' getNumber 'methods, where calling the former will get a thrown event。

There are two ways to subscribe to events in the Fisco Go SDK: directly using 'client' to subscribe to events and calling functions in the code generated by the Abigen tool to subscribe。

### 4.1 Event subscription using client

#### Event Push Subscription

[subscriber.go] is given in the Go SDK(https://github.com/FISCO-BCOS/go-sdk/blob/master/examples/eventLog/sub/subscriber.go)Contract Event Subscription Sample

*Note that the topic and address cannot be emptied, and the corresponding event cannot be received if emptied。*

```go
func main() {
    ...... / / Omitted
    endpoint := "127.0.0.1:20200" / / the ip address of the deployed fisco chain:channel_port
    privateKey, _ := hex.DecodeString("145e247e170ba3afd6ae97e88f00dbc976c2345d511b0f6713355d19d8b80b58") / / User private key
    config := &conf.Config{IsHTTP: false, ChainID: 1, CAFile: "ca.crt", Key: "sdk.key", Cert: "sdk.crt",
        IsSMCrypto: false, GroupID: 1, PrivateKey: privateKey, NodeURL: endpoint}
    var c *client.Client
    var err error
    ...... / / Omitted
    var eventLogParams types.EventLogParams
    eventLogParams.FromBlock = 1
    eventLogParams.ToBlock = -1
    var topics = make([]string, 1)
    topics[0] = common.BytesToHash(crypto.Keccak256([]byte("setNum(address,uint256,uint256)"))).Hex() / / The topic of the event. Use uint256 and int256 instead of uint and int
    eventLogParams.Topics = topics
    var addresses = make([]string, 1)
    addresses[0] = "0xd2cf82e18f3d2c5cae0de87d29994be622f3fdd3" / / The contract address corresponding to the subscribed event

    eventLogParams.Addresses = addresses
    timeout := 30 * time.Second
    queryTicker := time.NewTicker(timeout)
    defer queryTicker.Stop()
    done := make(chan bool)
    taskId, err = c.SubscribeEventLogs(eventLogParams, func(status int, logs []types.Log) { / / Call the SubscribeEventLogs function to subscribe to events
        ...... / / Omitted
    })
    ...... / / Omitted
}
```

#### Log data parsing

In the client subscription method provided by the SDK, the obtained data needs to be parsed by 'abi'

First, you need to build a structure for parsing based on the type of variables included in the event

```go
type setNum struct {
    Caller common.Address
    Cnt    *big.Int
    Value  *big.Int
}
```

This can then be parsed using the 'abi' tool:

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

Here, the data used by 'abi.Json' needs to be obtained after using the 'abigen' tool to generate the corresponding go file:

```go
// EventDemoABI is the input ABI used to generate the binding from.
const EventDemoABI = "[{\"constant\":true,\"inputs\":[],\"name\":\"count\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"v\",\"type\":\"uint256\"}],\"name\":\"setNumber\",\"outputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"num\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"getNumber\",\"outputs\":[{\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"caller\",\"type\":\"address\"},{\"indexed\":false,\"name\":\"cnt\",\"type\":\"uint256\"},{\"indexed\":false,\"name\":\"value\",\"type\":\"uint256\"}],\"name\":\"setNum\",\"type\":\"event\"}]"
```

### 4.2 Event subscription using functions generated by the Abigen tool

To subscribe to events in this way, you need to first build a 'session' connection to the contract, and then directly call the function in the go file generated by 'abigen' to subscribe to and parse events:

```go
/ / Build eventDemoSession based on the configuration file
err := eventDemoSession.WatchAllSetNum(from, func(status int, logs []types.Log) {
    fmt.Println("Status Code:",status)
    for k, v := range logs {
            log, errs := eventDemoSession.ParseSetNum(v) / / Parse data
            if errs != nil {
                fmt.Println(errs)
            }
        fmt.Println("Log id:", k, "Log Content:",log)
        }
    })
```

For the construction of Session, please refer to [Contract Development Sample](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/go_sdk/contractExamples.html)routine in section。
