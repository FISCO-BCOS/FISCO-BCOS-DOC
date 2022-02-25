# 合约事件推送

标签：``java-sdk`` ``事件订阅`` ``Event``

----

## 1. 功能简介

合约事件推送提供了合约事件的异步推送机制，客户端向节点发送注册请求，在请求中携带客户端关注的合约事件的参数，节点根据请求参数获取`Event Log`，将结果推送给客户端。

## 2. 交互协议

客户端与节点的交互基于WebSocket协议。交互分为三个阶段：

- 注册请求
- 节点回复
- 合约事件推送

### 2.1 注册请求

客户端向节点发送事件推送的请求:

```Json
// 请求示例
{
  "fromBlock": -1,  
  "toBlock": -1,
  "addresses": [
    "0xca5ed56862869c25da0bdf186e634aac6c6361ee"
  ],
  "topics": [
    "0x91c95f04198617c60eaf2180fbca88fc192db379657df0e412a9f7dd4ebbe95d"
  ]
}
```

### 2.2 节点返回

节点接受客户端注册请求时，对请求参数进行校验，将是否成功接受请求结果回复给客户端。

```Json
// 返回示例
{
  "id": "bb31e4ec086c48e18f21cb994e2e5967",
  "status": 0 // 0: 表示返回成功
}
```

### 2.3 合约事件推送

节点验证客户端请求成功之后，根据请求参数，向客户端推送`Event Log`数据。

```Json
// Event Logs推送示例
{
  "id": "bb31e4ec086c48e18f21cb994e2e5967",
  "status": 0,
  "logs": [
    
  ]
}
```

## 3. Java SDK 合约事件教程

### 3.1 接口列表

Java SDK中`org.fisco.bcos.sdk.eventsub.EventSubscribe`提供合约事件的功能，主要接口:

#### subscribeEvent

- 接口功能
注册合约事件通知

- 接口原型

```Java
  String subscribeEvent(EventSubParams params, EventSubCallback callback);
```

- 参数列表
  - params: `EventSubParams`类型，请求参数

    ```Java
    public class EventSubParams {
        BigInteger fromBlock = BigInteger.valueOf(-1);
        BigInteger toBlock = BigInteger.valueOf(-1);
        List<String> addresses = new ArrayList<>();
        List<List<String>> topics =
            new ArrayList<List<String>>() {
                {
                    add(null);
                    add(null);
                    add(null);
                    add(null);
                }
            };

        public boolean addAddress(String addr) // 添加合约地址
        public boolean addTopic(int index, String topic) // 添加topic
    ```

    - fromBlock：整型，起始块高。<=0表示从当前块高开始
    - toBlock：整型，最终块高。<=0表示至当前块高时，等待新区块继续处理
    - addresses：字符串数组，合约列表，为空时表示订阅所有的合约
    - topics：数组类型，订阅的topic信息

  - callback: 回调，`EventSubCallback`类型
  
    ```Java
    public interface EventSubCallback {
        void onReceiveLog(String id, int status, List<EventLog> logs);
    }
    ```

    - id: 字符串类型，表示本次推送的任务id
    - status: 推送状态，0：正常推送，1：推送结束，其他值表示错误码

    ```Java
        0       : 正常推送
        1       : 推送完成
        -41000  : 参数无效，客户端验证参数错误返回
        -41001  : 参数错误，节点验证参数错误返回
        -41002  : 群组不存在
        -41003  : 请求错误的区块区间
        -41004  : 节点推送数据格式错误
        -41005  : 请求发送超时
        -41006  : 客户端无订阅权限
        -41007  : 事件尚未注册，取消订阅失败
        -42000  : 其他错误 
    ```

    - logs: 推送的合约事件列表，`status`为0时有效。EventLog结构:

    ```Java
        // EventLog
        public class EventLog {
            private String logIndex;
            private String transactionIndex;
            private String transactionHash;
            private String blockNumber;
            private String address;
            private String data;
            private List<String> topics;
        }
    ```

- 返回值
  - String: 标记推送的id，可以用于取消推送

#### unsubscribeEvent

- 接口原型

```Java
  void unsubscribeEvent(String id);
```

- 接口功能
取消合约事件通知

- 参数列表
  - id: `String`类型，推送标记id，由`subscribeEvent`返回

### 3.2 回调实现

Java SDK对回调类`EventSubCallback`无默认实现，用户可以通过继承`EventSubCallback`类，重写`onReceiveLog`接口，实现自己的回调逻辑处理。

```Java
class MySubscribeCallback implements EventSubCallback {
    @Override
    public void onReceiveLog(String id, int status, List<EventLog> logs) {
        if(status == 0) {
            // 正常推送，处理logs
        } else if(status == 1) {
            // 推送完成
        } else {
            // 处理错误
        }
    }
}
```

**注意：`onReceiveLog`接口多次回调的`logs`有重复的可能性，可以根据`EventLog`对象中的`blockNumber，transactionIndex，logIndex`进行去重**  

### 3.3 `Topic`工具

`org.fisco.bcos.sdk.codec.abi.tools.TopicTools`提供将基本类型参数转换为对应topic的工具，用户设置`EventLogParams`的`topics`参数可以使用。

```Java
 class TopicTools {
    // int1/uint1~uint1/uint256 
    public static String integerToTopic(BigInteger i)
    // bool
    public static String boolToTopic(boolean b)
    // address
    public static String addressToTopic(String s)
    // string
    public static String stringToTopic(String s)
    // bytes
    public static String bytesToTopic(byte[] b)
    // byte1~byte32
    public static String byteNToTopic(byte[] b)
}
```

## 4. 示例

这里以[`Asset`](https://github.com/FISCO-BCOS/LargeFiles/blob/master/tools/asset-app-3.0-solidity.tar.gz)合约的`TransferEvent`为例说明，给出合约事件推送的一些场景供用户参考。

```solidity
contract Asset {
    event TransferEvent(int256 ret, string indexed from_account, string indexed to_account, uint256 indexed amount);
    event TransferAccountEvent(string,string);

    function transfer(string from_account, string to_account, uint256 amount) public returns(int256) {
        // 结果
        int result = 0;

        // 其他逻辑，省略

        // TransferEvent 
        TransferEvent(result, from_account, to_account, amount);

        // TransferAccountEvent
        TransferAccountEvent(from_account, to_account);
    }
}
```

- 场景1：将链上所有或者最新的事件回调至客户端

```Java
String group = "group0";
ConfigOption configOption = null;

// 其他初始化逻辑，省略

EventSubscribe eventSubscribe = EventSubscribe.build(group, configOption);
eventSubscribe.start();

// 参数设置
EventSubParams params = new EventSubParams();

// 最新Event fromBlock设置为 -1
params.setFromBlock(BigInteger.valueOf(-1));

// 所有Event fromBlock设置为 1
// params.setFromBlock(BigInteger.ONE);

// toBlock设置为-1
params.setToBlock(BigInteger.valueOf(-1));

// 注册事件
EventSubCallback callback = new EventSubCallback() {
    @Override
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        if(status == 0) {
            // 正常推送，处理logs
        } else if(status == 1) {
            // 推送完成
        } else {
            // 处理错误
        }
    }
};
String id = eventSubscribe.subscribeEvent(params, callback);
```

- 场景2: 将`Asset`合约最新的`TransferEvent`事件回调至客户端

```Java
String group = "group0";
ConfigOption configOption = null;

// 其他初始化逻辑，省略

EventSubscribe eventSubscribe = EventSubscribe.build(group, configOption);
eventSubscribe.start();

// 假设非国密环境
CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
TopicTools topicTools = new TopicTools(cryptoSuite);

// 参数设置
EventSubParams params = new EventSubParams();

// 最新Event
// fromBlock设置为-1
params.setFromBlock(BigInteger.valueOf(-1));

// toBlock设置为-1, 全部区块处理完成之后，等待新区块
params.setToBlock(BigInteger.valueOf(-1));

// topic0，TransferEvent(int256,string,string,uint256)
params.addTopic(0, topicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));

// 注册事件
EventSubCallback callback = new EventSubCallback() {
    @Override
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        if(status == 0) {
            // 正常推送，处理logs
        } else if(status == 1) {
            // 推送完成
        } else {
            // 处理错误
        }
    }
};
String id = eventSubscribe.subscribeEvent(params, callback);
```

- 场景3: 将指定地址的`Asset`合约最新或者所有的`TransferEvent`事件回调至客户端

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324";`

```Java
String group = "group0";
ConfigOption configOption = null;

// 其他初始化逻辑，省略

EventSubscribe eventSubscribe = EventSubscribe.build(group, configOption);
eventSubscribe.start();

// 假设非国密环境
CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
TopicTools topicTools = new TopicTools(cryptoSuite);

// 参数设置
EventSubParams params = new EventSubParams();

// 最新Event
// fromBlock设置为-1
params.setFromBlock(BigInteger.valueOf(-1));

// 所有Event
// fromBlock设置为1
// params.setFromBlock(BigInteger.valueOf(1));

// toBlock设置为-1, 全部区块处理完成之后继续等待区块处理
params.setToBlock(BigInteger.valueOf(-1));

String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
// 添加地址
params.addAddress(addr);

// topic0，TransferEvent(int256,string,string,uint256)
params.addTopic(0, topicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));

// 注册事件
EventSubCallback callback = new EventSubCallback() {
    @Override
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        if(status == 0) {
            // 正常推送，处理logs
        } else if(status == 1) {
            // 推送完成
        } else {
            // 处理错误
        }
    }
};
String id = eventSubscribe.subscribeEvent(params, callback);
```

- 场景4: 将`Asset`指定合约指定账户转账的所有事件回调至客户端

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324"`  

转账账户: `String fromAccount = "account"`

```Java
String group = "group0";
ConfigOption configOption = null;

// 其他初始化逻辑，省略

EventSubscribe eventSubscribe = EventSubscribe.build("", configOption);
eventSubscribe.start();

// 假设非国密环境
CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
TopicTools topicTools = new TopicTools(cryptoSuite);

// 参数设置
EventSubParams params = new EventSubParams();

// 所有Event
// fromBlock设置为1
params.setFromBlock(BigInteger.valueOf(1));

// toBlock设置为-1, 全部区块处理完成之后继续等待区块处理
params.setToBlock(BigInteger.valueOf(-1));

String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
// 添加地址
params.addAddress(addr);

// topic0，TransferEvent(int256,string,string,uint256)
params.addTopic(0, topicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));

String fromAccount = "account";
// topic1, fromAccount
params.addTopic(1, topicTools.stringToTopic(fromAccount));

// 注册事件
EventSubCallback callback = new EventSubCallback() {
    @Override
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        if(status == 0) {
            // 正常推送，处理logs
        } else if(status == 1) {
            // 推送完成
        } else {
            // 处理错误
        }
    }
};
String id = eventSubscribe.subscribeEvent(params, callback);
```

## 4. 解析`Event Log`

以`Asset`合约为例，描述合约部署、调用、注册事件及解析节点推送事件内容的实现。

注意：对增加了indexed属性的事件参数，均不进行解码，在相应位置上直接记录，其余非indexed属性的事件参数将进行解码。

```Java
class EventSubCallbackSample implements EventSubCallback {

    @Override
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        if(status == 1) {
            // 推送完成
            return;
        } else if(status != 0){
            // 错误处理
            return;
        }

        // status = 0
        for (EventLog log : logs) {
            logger.debug(
                    " blockNumber:"
                            + log.getBlockNumber()
                            + ",txIndex:"
                            + log.getTransactionIndex()
                            + " data:"
                            + log.getData());
            ABICodec abiCodec = new ABICodec(client.getCryptoSuite());
            try {
                List<Object> list = abiCodec.decodeEvent(abi, "TransferEvent", log);
                logger.debug("decode event log content, " + list);
                // list = [0, 0x81376b9868b292a46a1c486d344e427a3088657fda629b5f4a647822d329cd6a, 0x28cac318a86c8a0a6a9156c2dba2c8c2363677ba0514ef616592d81557e679b6, 0x0000000000000000000000000000000000000000000000000000000000000064]
                // 后三个事件参数均为indexed属性
                Assert.assertEquals(4, list.size());
            } catch (ABICodecException e) {
                logger.error("decode event log error, " + e.getMessage());
            }
            try {
                List<Object> list = abiCodec.decodeEvent(abi, "TransferAccountEvent", log);
                logger.debug("decode event log content, " + list);
                // list = [Alice, Bob]
                Assert.assertEquals(2, list.size());
            } catch (ABICodecException e) {
                logger.error("decode event log error, " + e.getMessage());
            }
        }
    }
}
```
