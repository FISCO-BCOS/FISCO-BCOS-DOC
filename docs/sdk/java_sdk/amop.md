# AMOP 功能

Java SDK支持[链上信使协议AMOP（Advanced Messages Onchain Protocol）](../../manual/amop_protocol.md)，用户可以通过AMOP协议与其它机构互传消息。



## 1. 配置方法

AMOP有两种话题模式普通话题和私有话题。任何一个订阅了某普通话题的订阅者都能收到该话题相关的推送消息。但在某些情况下，发送者只希望特定的接收者能接收到消息，不希望无关的接收者能任意的监听此话题，这时就需要使用AMOP私有话题了。

AMOP私有话题的特别之处在于，SDK间需要进行了身份认证，认证通过的订阅者才可以收到该话题的消息。身份认证的原理是，首先由发送方生成一个随机数，订阅方用私钥对随机数签名，发送方用所配置的公钥验证这个签名来确定对方是否是自己指定的订阅方。因此，一个成功的私有话题通道的建立需要（1）消息发送者需要设置指定的订阅者的公钥；（2）订阅方也需要设置能证明自己身份的私钥。

当用户需要订阅私有话题，或者作为消息发布者配置一个私有话题时，可用配置文件进行配置。但AMOP的配置不是必须项，私有话题的订阅和设置，还可以通过调用AMOP的接口实现。以下是AMOP的配置示例，是``test/resource/config-example.toml``配置文件中的一部分。

```toml
# Configure a "need verify AMOP topic" as a topic message sender.
[[amop]]
topicName = "PrivateTopic1"
# Public keys of the nodes that you want to send AMOP message of this topic to.
publicKeys = [ "conf/amop/consumer_public_key_1.pem" ]

# Configure a "need verify AMOP topic" as a topic subscriber.
[[amop]]
topicName = "PrivateTopic2"
# Your private key that used to subscriber verification.
privateKey = "conf/amop/consumer_private_key.p12"
password = "123456"
```

配置详解：

1. 配置一个私有话题（作为消息发布者）

   * 需要在配置文件中新建一个``[[amop]]``节点。
   * 并配置话题名称``topicName = "PrivateTopic1"``
   * 消息订阅方的公钥列表``publicKeys = [ "conf/amop/consumer_public_key_1.pem" ]`` ，即您想指定的接收对象的公钥，这个公钥必须与某个订阅方的私钥所匹配。

   ```toml
   [[amop]]
   topicName = "PrivateTopic1"
   publicKeys = [ "conf/amop/consumer_public_key_1.pem" ]
   ```

2. 订阅一个私有话题（作为订阅者）

   * 需要在配置文件中新建一个``[[amop]]``节点。
   * 并配置话题名称``topicName = "PrivateTopic2"``
   * 证明订阅方身份的私钥``privateKey = "conf/amop/consumer_private_key.p12"`` 
   * 该私钥的密码``password = "123456"``

   ```toml
   [[amop]]
   topicName = "PrivateTopic2"
   privateKey = "conf/amop/consumer_private_key.p12"
   password = "123456"
   ```

   

## 2. 接口说明

AMOP模块的接口类可参考文件java-sdk中的AMOP.java文件，其中主要包含以下几个接口：

#### subscribeTopic

订阅一个普通话题

**参数：**

* topicName: 话题名称。类型：``String``。
* callback: 处理该话题消息的函数，当收到该话题相关消息时，会被调用。类型：``AmopCallback``。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 定义一个Callback，重写receiveAmopMsg方法，定义收到消息后的处理流程。
AmopCallback cb = new AmopCallback() {
  @Override
  public byte[] receiveAmopMsg(AmopMsgIn msg) {
    // 你可以在这里写收到消息后的处理逻辑。
    System.out.println("Received msg, content:" + new String(msg.getContent()));
    return "Yes, I received.".getBytes();
  }
};
// 订阅话题
amop.subscribeTopic("MyTopic", cb);
```



#### subscribePrivateTopic

订阅一个私有话题

**参数：**

* topicName: 话题名称。类型：``String``。
* privateKeyManager：证明订阅者身份的私钥信息。类型：``KeyManager``。
* callback: 处理该话题消息的函数，当收到该话题相关消息时，会被调用。类型：``AmopCallback``。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 定义一个Callback，重写receiveAmopMsg方法，定义收到消息后的处理流程。
AmopCallback cb = new AmopCallback() {
  @Override
  public byte[] receiveAmopMsg(AmopMsgIn msg) {
    // 你可以在这里写收到消息后的处理逻辑。
    System.out.println("Received msg, content:" + new String(msg.getContent()));
    return "Yes, I received.".getBytes();
  }
};

// 加载私钥
KeyManager km = new P12Manager("private_key.p12", "12s230");

// 订阅话题
amop.subscribePrivateTopics("MyPrivateTopic", km, cb);
```



#### publishPrivateTopic

作为消息发送者设置一个私有话题。

**参数：**

* topicName: 话题名称。类型：``String``。
* publicKeyManagers：指定订阅者的公钥列表。类型：``List<KeyManager>``。

**例子**：

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 加载指定订阅者的私钥列表
List<KeyManager> publicKeyList = new ArrayList<>();
KeyManager pubKey1 = new PEMManager("target_subscriber1_public_key.pem");
KeyManager pubKey2 = new PEMManager("target_subscriber2_public_key.pem");
publicKeyList.add(pubKey1);
publicKeyList.add(pubKey2);

// 设置一个私有话题
amop.publishPrivateTopic("MyPrivateTopic", publicKeyList);
```



#### unsubscribeTopic

取消一个话题的订阅。

**参数：**

* topicName: 话题名称。类型：``String``。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 订阅“MyTopic”，过程省略

// 取消订阅
amop.unsubscribeTopic("MyTopic");
```



#### sendAmopMsg

以单播的方式发送AMOP消息

**参数：**

* content: 消息内容。类型：``AmopMsgOut``。
* callback: 回调函数。类型：``ResponseCallback``。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 构造内容
AmopMsgOut out = new AmopMsgOut();
out.setTopic("MyTopic");
out.setType(TopicType.NORMAL_TOPIC);
out.setContent("some content here".getBytes());
out.setTimeout(6000);

// 构造Callback
ResponseCallback cb =
  new ResponseCallback() {
  @Override
  public void onResponse(Response response) {
		// 你可以在这里写收到回复的处理逻辑。
    System.out.println(
      "Get response, { errorCode:"
      + response.getErrorCode()
      + " error:"
      + response.getErrorMessage()
      + " seq:"
      + response.getMessageID()
      + " content:"
      + new String(response.getContentBytes())
      + " }");
  }
};

// 发送消息
amop.sendAmopMsg(out, cb);
```



#### broadcastAmopMsg

以广播的方式发送AMOP消息

**参数：**

* content: 消息内容。类型：``AmopMsgOut``。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 构造内容
AmopMsgOut out = new AmopMsgOut();
out.setTopic("MyTopic");
out.setType(TopicType.NORMAL_TOPIC);
out.setContent(content.getBytes());
out.setTimeout(6000);

// 发送消息
amop.broadcastAmopMsg(out);
```



#### getSubTopics

获取SDK当前订阅的话题

**参数：**

无

**返回值：**

* 订阅的话题集合。类型：``Set<String>``。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

Set<String> topics = amop.getSubTopics();
```



#### start

启动amop功能，SDK初始化后默认启动。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

amop.stop()
  
// 在需要的时候
amop.start()
```



#### stop

停止AMOP功能。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 停止AMOP
amop.stop()
```

