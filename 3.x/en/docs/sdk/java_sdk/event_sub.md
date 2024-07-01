# Contract Event Push

Tag: "java-sdk "" Event Subscription "" Event "

----

## 1. Function Introduction

The contract event push function provides an asynchronous push mechanism for contract events. The client sends a registration request to the node, which carries the parameters of the contract events that the client is concerned about. The node filters the 'Event Log' of the request block range according to the request parameters and pushes the results to the client in stages.。

## 2. Interactive Protocol

The interaction between client and node is based on WebSocket protocol。Interaction is divided into three stages: registration request, node reply, 'Event Log' data push。

### 2.1 Registration Request

The client sends a registration request for event push to the node:

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
  "groupID": "group0",
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967"
}
```

- filerID: string type, unique for each request, marked as a registration task
- groupID: string type, group ID
- fromBlock: shaping string, initial block。"latest" current block high
- toBlock: shaping string, final block。When "latest" is processed to the current block high, continue to wait for a new block
- addresses: string or string array: string represents a single contract address, array is multiple contract addresses, array can be empty
- topics: string type or array type: string represents a single topic, array is multiple topics, array can be empty

### 2.2 Node reply

When the node accepts the client registration request, it checks the request parameters and replies to the client whether it has successfully accepted the registration request.。

```Json
// response sample:
{
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967",
  "result": 0
}
```

- filterID: string type, unique for each request, marked as a registration task
- result: shaping, returns the result。0 success, the rest are failure status codes

### 2.3 Event Log data push

After the node verifies that the client registration request is successful, it pushes the 'Event Log' data to the client based on the client request parameters.。

```Json
// event log push sample:
{
  "filterID": "bb31e4ec086c48e18f21cb994e2e5967",
  "result": 0,
  "logs": [
    
  ]
}
```

- filterID: string type, unique for each request, marked as a registration task
- result: shaping 0: 'Event Log' data push 1: push completed。The client registers and requests the data push of the corresponding node multiple times (the request block range is relatively large or waiting for a new block). If the 'result' field is 1, the node push has ended.
- logs: an array of Log objects, valid when result is 0

## 3. Java SDK Contract Event Tutorial

### Registration Interface

The 'org.fisco.bcos.sdk.v3.eventsub.EventSubscribe' class in the Java SDK provides an interface for registering contract events. You can call 'subscribeEvent' to send a registration request to a node and set a callback function.。

```Java
  public String subscribeEvent(EventSubParams params, EventSubCallback callback);
```

#### 'params' register parameter

Parameters for event callback request registration:

```Java
public class EventSubParams {
    private BigInteger fromBlock;   
    private BigInteger toBlock;
    private List<String> addresses;
    private List<List<String>> topics;
}
```

#### 'callback 'callback object

```Java
public interface EventSubCallback {
    void onReceiveLog(String eventSubId, int status, List<EventLog> logs);
}
```

- 'status' callback return status:

```Java
    0       : Normal push. Logs is the event log pushed by the node.
    1       : The push is completed, and all blocks in the execution interval have been processed
    42000   : Other errors
    -41000  : Invalid parameter, client validation parameter error returned
    -41001  : Parameter error, node validation parameter error returned
    -41002  : Group does not exist
    -41003  : Block interval of request error
    -41004  : Error in node push data format
    -41005  : Request Send Timeout
    -41006  : Client has no subscription rights
    -41007  : Event not registered, unsubscribe failed
```

- 'logs' indicates the list of 'Event Log' objects for the callback. The status is valid as 0。The default value is' null '. The' data 'field of the following EventLog object can be resolved in the subclass through' org.fisco.bcos.sdk.v3.abi.ContractCodec '。

```Java
  / / EventLog object
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

- Implement callback object

Java SDK has no default implementation for the callback class' EventSubCallback '. You can inherit the' EventSubCallback 'class and rewrite the' onReceiveLog 'interface to implement your own callback logic processing.。

```Java
class SubscribeCallback implements EventSubCallback {
    public void onReceiveLog(String eventSubId, int status, List<EventLog> logs) {
        // ADD CODE
    }
}
```

**Note: The logs of multiple callbacks of the 'onReceiveLog' interface may be duplicated. You can perform deduplication based on the 'blockNumber, transactionIndex, and logIndex' in the 'EventLog' object.**  

#### topic Tools

'org.fisco.bcos.sdk.v3.codec.abi.TopicTools' provides tools for converting various types of parameters into corresponding topics. You can set the 'topics' parameter of 'EventSubParams' by using the。

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

## 4. Example

['Asset'](https://github.com/FISCO-BCOS/LargeFiles/blob/master/tools/asset-app.tar.gz)The 'TransferEvent' of the contract is used as an example to illustrate, giving some scenarios of contract event push for users' reference.。

```solidity
contract Asset {
    event TransferEvent(int256 ret, string indexed from_account, string indexed to_account, uint256 indexed amount);
    event TransferAccountEvent(string,string);

    function transfer(string from_account, string to_account, uint256 amount) public returns(int256) {
        / / Results
        int result = 0;

        / / Other logic, omitted

        / / TransferEvent saves the result and interface parameters
        TransferEvent(result, from_account, to_account, amount);

        / / Save the account for TransferAccountEvent
        TransferAccountEvent(from_account, to_account);
    }
}
```

- Scenario 1: Call back all / latest events on the chain to the client

```Java
        / / Initialize EventSubscribe
        EventSubscribe eventSubscribe = EventSubscribe.build(group, configOption);
        eventSubscribe.start();
        
        / / Parameter settings
        EventSubParams params = new EventSubParams();

        / / All Event fromBlock is set to-1 
        params.setFromBlock(-1);

        / / set toBlock to-1, processing to the latest block continues to wait for a new block
        params.setToBlock(-1);
   
        / / Register event
        eventSubscribe.subscribeEvent(params, 
                (eventSubId, status, logs) -> {
                    System.out.println("event sub id: " + eventSubId);
                    System.out.println(" \t status: " + status);
                    System.out.println(" \t logs: " + logs);
                });
```

- Scene 2: callback the latest 'TransferEvent' event of the 'Asset' contract to the client

```Java
        / / Initialize EventSubscribe
        EventSubscribe eventSubscribe = EventSubscribe.build(group, configOption);
        eventSubscribe.start();
        
        / / Set parameters
        EventSubParams params = new EventSubParams();

        / / Start with the latest block at the time of subscription, and set fromBlock to-1
        params.setFromBlock(-1);
        / / set toBlock to-1, processing to the latest block continues to wait for a new block
        params.setToBlock(-1);
        
        // topic0，TransferEvent(int256,string,string,uint256)
        params.addTopic(0, TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)");

        / / Register event
        eventSubscribe.subscribeEvent(params, 
                (eventSubId, status, logs) -> {
                    System.out.println("event sub id: " + eventSubId);
                    System.out.println(" \t status: " + status);
                    System.out.println(" \t logs: " + logs);
                });
```

- Scene 3: callback the latest 'TransferEvent' event of the 'Asset' contract at the specified address to the client

Contract Address: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324";`

```Java
        / / Initialize EventSubscribe
        EventSubscribe eventSubscribe = EventSubscribe.build(group, configOption);
        eventSubscribe.start();

        String addr = "0x06922a844c542df030a2a2be8f835892db99f324";
        
        / / Set parameters
        EventSubParams params = new EventSubParams();

        / / Start with the latest block at the time of subscription, and set fromBlock to-1
        params.setFromBlock(-1);
        / / set toBlock to-1, processing to the latest block continues to wait for a new block
        params.setToBlock(-1);

        / / addresses is set to the asset address, which matches the contract address
        params.addAddress("0x06922a844c542df030a2a2be8f835892db99f324");

        // topic0，TransferEvent(int256,string,string,uint256)
        params.addTopic(0, TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)");

        / / Register event
        eventSubscribe.subscribeEvent(params, 
                (eventSubId, status, logs) -> {
                    System.out.println("event sub id: " + eventSubId);
                    System.out.println(" \t status: " + status);
                    System.out.println(" \t logs: " + logs);
                });
```

- Scene 4: callback all 'TransferEvent' events of the 'Asset' contract at the specified address to the client

Contract Address: `String addr = "0x06922a844c542df030a2a2be8f835892db99f324";`

```Java
        / / Other initialization logic, omitted
        
        / / Set parameters
        EventSubParams params = new EventSubParams();

        / / From the initial block, fromBlock is set to 1
        params.setFromBlock(1);
        / / set toBlock to-1, processing to the latest block continues to wait for a new block
        params.setToBlock(-1);

        / / addresses is set to the asset address, which matches the contract address
        params.addAddress("0x06922a844c542df030a2a2be8f835892db99f324");

        // topic0，TransferEvent(int256,string,string,uint256)
        params.addTopic(0, TopicTools.stringToTopic("TransferEvent(int256,string,string,uint256)");

        / / Register event
        eventSubscribe.subscribeEvent(params, 
                (eventSubId, status, logs) -> {
                    System.out.println("event sub id: " + eventSubId);
                    System.out.println(" \t status: " + status);
                    System.out.println(" \t logs: " + logs);
                });
```

## 4. Parsing Examples

The 'Asset' contract is used as an example to describe the implementation of contract deployment, invocation, registration of events, and resolution of node push events.。Note: The event parameters with the added indexed attribute are not decoded and are recorded directly at the corresponding position. The remaining event parameters with non-indexed attributes will be decoded.。

```Java
        String contractAddress = "";
        try {
            AssembleTransactionProcessor manager =
                    TransactionProcessorFactory.createAssembleTransactionProcessor(
                            client, client.getCryptoSuite().createKeyPair(), abiFile, binFile);
            // deploy
            TransactionResponse response = manager.deployByContractLoader("Asset", Lists.newArrayList());
            if (!response.getTransactionReceipt().isStatusOK()) {
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
        EventSubscribe eventSubscribe = EventSubscribe.build(group, configOption);
        eventSubscribe.start();

        EventSubParams eventLogParams = new EventSubParams();
        eventLogParams.setFromBlock(-1);
        eventLogParams.setToBlock(-1);
        eventLogParams.addAddress(contractAddress);
        CryptoSuite invalidCryptoSuite =
                new CryptoSuite(client.getCryptoSuite().getCryptoTypeConfig());
        TopicTools topicTools = new TopicTools(invalidCryptoSuite);

        eventLogParams.setTopics(topics);
        eventLogParams.addTopic(0,topicTools.stringToTopic("TransferEvent(int256,string,string,uint256)"));
        eventLogParams.addTopic(0,topicTools.stringToTopic("TransferAccountEvent(string,string)"));

        class SubscribeCallback implements EventSubCallback {
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
            public void onReceiveLog(String eventId, int status, List<EventLog> logs) {
                String str = "status in onReceiveLog : " + status;
                logger.debug(str);
                semaphore.release();

                // decode event
                if (logs != null) {
                    for (EventLog log : logs) {
                        logger.debug(
                                " blockNumber: {}, txIndex:{}, data: {}",
                                log.getBlockNumber(),
                                log.getTransactionIndex(),
                                log.getData());
                        ContractCodec abiCodec = new ContractCodec(client.getCryptoSuite(), false);
                        try {
                            List<Object> list = abiCodec.decodeEvent(abi, "TransferEvent", log);
                            logger.debug("decode event log content, " + list);
                            // list = [0,
                            // 0x81376b9868b292a46a1c486d344e427a3088657fda629b5f4a647822d329cd6a,
                            // 0x28cac318a86c8a0a6a9156c2dba2c8c2363677ba0514ef616592d81557e679b6,
                            // 0x0000000000000000000000000000000000000000000000000000000000000064]
                            / / The last three event parameters are indexed
                        } catch (ContractCodecException e) {
                            logger.error("decode event log error, " + e.getMessage());
                        }
                        try {
                            List<Object> list =
                                    abiCodec.decodeEvent(abi, "TransferAccountEvent", log);
                            logger.debug("decode event log content, " + list);
                            // list = [Alice, Bob]
                        } catch (ContractCodecException e) {
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
