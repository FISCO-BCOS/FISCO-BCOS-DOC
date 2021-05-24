# 合约事件推送

标签：``java-sdk`` ``事件订阅`` ``Event``

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
    "0xca5ed56862869c25da0bdf186e634aac6c6361ee"
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

## 3. Java SDK教程

### 注册接口  

Java SDK中`org.fisco.bcos.sdk.eventsub`类提供合约事件的注册接口，用户可以调用`subscribeEvent`向节点发送注册请求，并设置回调函数。

```Java
  public String subscribeEvent(EventLogParams params, EventCallback callback);
```

#### `params`注册参数  

事件回调请求注册的参数：  

```Java
public class EventLogParams {
    private String fromBlock;   
    private String toBlock;
    private List<String> addresses;
    private List<Object> topics;
}
```

#### `callback`回调对象

```Java
public interface EventCallback {
    void onReceiveLog(int status, List<EventLog> logs);
}
```

- `status` 回调返回状态：  

```Java
    0       : 正常推送，此时logs为节点推送的事件日志
    1       : 推送完成，执行区间的区块都已经处理
    -41000  : 参数无效，客户端验证参数错误返回
    -41001  : 参数错误，节点验证参数错误返回
    -41002  : 群组不存在
    -41003  : 请求错误的区块区间
    -41004  : 节点推送数据格式错误
    -41005  : 请求发送超时
    -41006  : 客户端无订阅权限
    -41007  : 事件尚未注册，取消订阅失败
    42000   : 其他错误 
```

- `logs`表示回调的`Event Log`对象列表，status为0有效。默认值`null`，可以在子类中通过`org.fisco.bcos.sdk.abi.ABICodec`解析以下EventLog对象的`data`字段。

```Java
  // EventLog 对象
  public class EventLog {
    private String logIndex;
    private String transactionIndex;
    private String transactionHash;
    private String blockHash;
    private String blockNumber;
    private String address;
    private String data;
    private String type;
    private List<String> topics;
  }
```

- 实现回调对象  

Java SDK对回调类`EventCallback`无默认实现，用户可以通过继承`EventCallback`类，重写`onReceiveLog`接口，实现自己的回调逻辑处理。

```Java
class SubscribeCallback implements EventCallback {
    public void onReceiveLog(int status, List<EventLog> logs) {
        // ADD CODE
    }
}
```

**注意：`onReceiveLog`接口多次回调的`logs`有重复的可能性，可以根据`EventLog`对象中的`blockNumber，transactionIndex，logIndex`进行去重**  

#### topic工具

`org.fisco.bcos.sdk.abi.TopicTools`提供将各种类型参数转换为对应topic的工具，用户设置`EventLogParams`的`topics`参数可以使用。

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

这里以[`Asset`](https://github.com/FISCO-BCOS/LargeFiles/blob/master/tools/asset-app.tar.gz)合约的`TransferEvent`为例说明，给出合约事件推送的一些场景供用户参考。

```solidity
contract Asset {
    event TransferEvent(int256 ret, string indexed from_account, string indexed to_account, uint256 indexed amount);
    event TransferAccountEvent(string,string);

    function transfer(string from_account, string to_account, uint256 amount) public returns(int256) {
        // 结果
        int result = 0;

        // 其他逻辑，省略

        // TransferEvent 保存结果以及接口参数
        TransferEvent(result, from_account, to_account, amount);

        // TransferAccountEvent 保存账号
        TransferAccountEvent(from_account, to_account);
    }
}
```

- 场景1：将链上所有/最新的事件回调至客户端

```Java
        // 其他初始化逻辑，省略
        
        // 参数设置
        EventLogParams params = new EventLogParams();

        // 全部Event fromBlock设置为"1" 
        params.setFromBlock("1");

        // toBlock设置为"latest"，处理至最新区块继续等待新的区块
        params.setToBlock("latest");

        // addresses设置为空数组，匹配所有的合约地址
        params.setAddresses(new ArrayList<String>());

        // topics设置为空数组，匹配所有的Event
        params.setTopics(new ArrayList<Object>());
   
        // 注册事件
        EventCallback callback = new EventCallback();
        String registerId = eventSubscribe.subscribeEvent(params, callback);
```

- 场景2: 将`Asset`合约最新的`TransferEvent`事件回调至客户端

```Java
        // 其他初始化逻辑，省略
        
        // 设置参数
        EventLogParams params = new EventLogParams();

        // 从最新区块开始，fromBlock设置为"latest"
        params.setFromBlock("latest");
        // toBlock设置为"latest"，处理至最新区块继续等待新的区块
        params.setToBlock("latest");

        // addresses设置为空数组，匹配所有的合约地址
        params.setAddresses(new ArrayList<String>());
        
        // topic0，TransferEvent(int256,string,string,uint256)
        ArrayList<Object> topics = new ArrayList<>();
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        params.setTopics(topics);

        // 注册事件
        EventCallback callback = new EventCallback();
        String registerId = eventSubscribe.subscribeEvent(params, callback);
```

- 场景3: 将指定地址的`Asset`合约最新的`TransferEvent`事件回调至客户端 

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324";`

```Java
        // 其他初始化逻辑，省略

        String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
        
        // 设置参数
        EventLogParams params = new EventLogParams();

        // 从最新区块开始，fromBlock设置为"latest"
        params.setFromBlock("latest");
        // toBlock设置为"latest"，处理至最新块并继续等待共识出块
        params.setToBlock("latest");

        // 合约地址
        ArrayList<String> addresses = new ArrayList<String>();
        addresses.add(addr);
        params.setAddresses(addresses);
        
        // topic0，匹配 TransferEvent(int256,string,string,uint256) 事件
        ArrayList<Object> topics = new ArrayList<>();
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,uint256)"));
        params.setTopics(topics);

        // 注册事件
        EventCallback callback = new EventCallback();
        String registerId = eventSubscribe.subscribeEvent(params, callback);
```

- 场景4: 将指定地址的`Asset`合约所有`TransferEvent`事件回调至客户端 

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324";`

```Java
        // 其他初始化逻辑，省略
        
        // 设置参数
        EventLogParams params = new EventLogParams();

        // 从最初区块开始，fromBlock设置为"1"
        params.setFromBlock("1");
        // toBlock设置为"latest"，处理至最新块并继续等待共识出块
        params.setToBlock("latest");

        // 设置合约地址
        ArrayList<String> addresses = new ArrayList<String>();
        addresses.add(addr);
        params.setAddresses(addresses);
        
        // TransferEvent(int256,string,string,uint256) 转换为topic
        ArrayList<Object> topics = new ArrayList<>();
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        params.setTopics(topics);

        // 注册事件
        EventCallback callback = new EventCallback();
        String registerId = eventSubscribe.subscribeEvent(params, callback);
```

- 场景5: 将`Asset`指定合约指定账户转账的所有事件回调至客户端 

合约地址: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324"`  

转账账户: `String fromAccount = "account"`

```Java
        // 其他初始化逻辑，省略

        String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
        String fromAccount = "account";
        
        // 参数
        EventLogParams params = new EventLogParams();

        // 从最初区块开始，fromBlock设置为"1"
        params.setFromBlock("1");
        // toBlock设置为"latest"
        params.setToBlock("latest");

        // 设置合约地址
        ArrayList<String> addresses = new ArrayList<String>();
        addresses.add(addr);
        params.setAddresses(addresses);
        
        // 设置topic
        ArrayList<Object> topics = new ArrayList<>();
        // TransferEvent(int256,string,string,uint256) 转换为topic
        topics.add(TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        // 转账账户 fromAccount转换为topic
        topics.add(TopicTools.stringToTopic(fromAccount));
        params.setTopics(topics);

        // 注册事件
        EventCallback callback = new EventCallback();
        String registerId = eventSubscribe.subscribeEvent(params, callback);
```

## 4. 解析例子

以`Asset`合约为例，描述合约部署、调用、注册事件及解析节点推送事件内容的实现。注意：对增加了indexed属性的事件参数，均不进行解码，在相应位置上直接记录，其余非indexed属性的事件参数将进行解码。

```Java
        String contractAddress = "";
        try {
            AssembleTransactionProcessor manager =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, client.getCryptoSuite().createKeyPair(), abiFile, binFile);
            // deploy
            TransactionResponse response = manager.deployByContractLoader("Asset", Lists.newArrayList());
            if (!response.getTransactionReceipt().getStatus().equals("0x0")) {
                return;
            }
            contractAddress = response.getContractAddress();
            // call function with event
            List<Object> paramsSetValues = new ArrayList<Object>();
            paramsSetValues.add("Alice");
            paramsSetValues.add("Bob");
            paramsSetValues.add(new BigInteger("100"));
            TransactionResponse transactionResponse =
                    manager.sendTransactionAndGetResponse(
                            contractAddress, abi, "transfer", paramsSetValues);
            logger.info("transaction response : " + JsonUtils.toJson(transactionResponse));
        } catch (Exception e) {
            logger.error("exception:", e);
        }
        
        // subscribe event
        EventLogParams eventLogParams = new EventLogParams();
        eventLogParams.setFromBlock("latest");
        eventLogParams.setToBlock("latest");
        eventLogParams.setAddresses(new ArrayList<>());
        ArrayList<Object> topics = new ArrayList<>();
        ArrayList<Object> topicIdx0 = new ArrayList<>();
        CryptoSuite invalidCryptoSuite =
                new CryptoSuite(client.getCryptoSuite().getCryptoTypeConfig());
        TopicTools topicTools = new TopicTools(invalidCryptoSuite);
        topicIdx0.add(topicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        topicIdx0.add(topicTools.stringToTopic("TransferAccountEvent(string,string)"));
        eventLogParams.setTopics(topics);

        class SubscribeCallback implements EventCallback {
            public transient Semaphore semaphore = new Semaphore(1, true);

            SubscribeCallback() {
                try {
                    semaphore.acquire(1);
                } catch (InterruptedException e) {
                    logger.error("error :", e);
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onReceiveLog(int status, List<EventLog> logs) {
                Assert.assertEquals(status, 0);
                String str = "status in onReceiveLog : " + status;
                logger.debug(str);
                semaphore.release();
                
                // decode event
                if (logs != null) {
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
        }

        SubscribeCallback subscribeEventCallback1 = new SubscribeCallback();
        String registerId =
                eventSubscribe.subscribeEvent(eventLogParams, subscribeEventCallback1);
        try {
            subscribeEventCallback1.semaphore.acquire(1);
            subscribeEventCallback1.semaphore.release();
            logger.info("subscribe successful, registerId is " + registerId);
        } catch (InterruptedException e) {
            logger.error("system error:", e);
            Thread.currentThread().interrupt();
        }
```