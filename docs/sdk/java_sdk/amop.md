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



## 3. 示例

更多的示例请看源码``sdk-demo/src/main/java/org/fisco/bcos/sdk/demo/amop/tool``下的代码示范。

* 普通话题代码示例：

  * AmopPublisher
    输入： 话题名（TopicName），是否多播（isBroadcast, true 为多播，false为单拨），内容（Content），发送的数量（Count）
    功能：发送普通话题消息、广播普通话题消息
  * AmopPublisherFile
    输入： 话题名（TopicName），是否多播（isBroadcast），文件名（FileName），发送的数量（Count）
    功能：发送普通话题文件、广播普通话题文件
  * AmopSubscriber
    输入：话题名（TopicName）
    默认：订阅了一个名为“Test”的普通话题
    功能：订阅一个普通话题

* 私有话题代码示例：

  * AmopSubscribePrivateTopic

    输入：话题名（TopicName），私钥文件名（Filename），密码（password）
    默认：订阅了一个名为“PrivTest”的私有话题
    功能：订阅一个私有话题

  * AmopPublisherPrivate

    输入： 话题名（TopicName），公钥文件名1（Filename），公钥文件名2（Filename），是否多播（isBroadcast），内容（Content），发送的数量（Count）
    功能：配置私有话题发送，发送私有话题消息，广播私有话题消息

  * AmopPublisherPrivateFile

    输入：话题名（TopicName），公钥文件名1（Filename），公钥文件名2（Filename，如无则输入null），是否多播（isBroadcast），文件名（FileName），发送的数量（Count）
    测试功能：发送私有话题文件、广播普通话题文件

## 4. 快速试用AMOP

**第一步：下载项目**

```bash
mkdir ~/fisco && cd ~/fisco
# 获取java-sdk代码
git clone https://github.com/FISCO-BCOS/java-sdk
cd java-sdk
```

**第二步：搭建FISCO BCOS区块链网络**

方法一：如果您使用的操作系统为Linux或Darwin，可以用将以下脚本保存到java-sdk的目录下，命名为initEnv.sh，并运行该脚本。

initEnv.sh

```bash
#!/bin/bash
download_tassl()
{
  mkdir -p ~/.fisco/
  if [ "$(uname)" == "Darwin" ];then
    curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/tassl_mac.tar.gz
    mv tassl_mac.tar.gz ~/.fisco/tassl.tar.gz
  else
    curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/tassl.tar.gz
    mv tassl.tar.gz ~/.fisco/tassl.tar.gz
  fi
  tar -xvf ~/.fisco/tassl.tar.gz
}

build_node()
{
  local node_type="${1}"
  if [ "${node_type}" == "sm" ];then
      ./build_chain.sh -l 127.0.0.1:4 -g
      sed_cmd=$(get_sed_cmd)
      $sed_cmd 's/sm_crypto_channel=false/sm_crypto_channel=true/g' nodes/127.0.0.1/node*/config.ini
  else
      ./build_chain.sh -l 127.0.0.1:4
  fi
  ./nodes/127.0.0.1/fisco-bcos -v
  ./nodes/127.0.0.1/start_all.sh
}
download_build_chain()
{
  tag=$(curl -sS "https://gitee.com/api/v5/repos/FISCO-BCOS/FISCO-BCOS/tags" | grep -oe "\"name\":\"v[2-9]*\.[0-9]*\.[0-9]*\"" | cut -d \" -f 4 | sort -V | tail -n 1)
  LOG_INFO "--- current tag: $tag"
  curl -LO "https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/${tag}/build_chain.sh" && chmod u+x build_chain.sh
}
prepare_environment()
{
  ## prepare resources for amop demo
  bash gradlew build -x test
  cp -r nodes/127.0.0.1/sdk/* sdk-demo/dist/conf
}

download_tassl
./gradlew build -x test
download_build_chain
build_node
prepare_environment
```

运行该文件：

```bash
# 更改文件权限
chmod 777 intEnv.sh     
# 运行initEnv.sh文件
./initEnv.sh
```

运行完成后demo环境就已经准备好了。

方法二：如果您使用Windows，请根据[指引](../../installation.html#fisco-bcos)搭建FISCO BCOS区块链网络。然后进行以下操作

```cmd
# 当前目录为java-sdk,构建项目
gradlew.bat build -x test
```

将你搭建FISCO BCOS网络节点``nodes/${ip}/sdk/`` 目录下的证书复制到``java-sdk/sdk-demo/dist/config``目录下。

**第三步：运行订阅者Demo**

```bash
# 进入sdk-demo/dist目录
cd sdk-demo/dist 
# 使用第三节中所描述的工具
# 我们订阅名为”testTopic“的话题
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.tool.AmopSubscriber testTopic
```

订阅方的控制台输出

```bash
Start test

```



第四步：运行消息发布者Demo

新打开一个控制台

```bash
# 进入sdk-demo/dist目录$ 
cd sdk-demo/dist 
# 调用AmopPublisher发送AMOP消息
# 话题名：testTopic，是否广播：false(即使用单播)，内容：Tell you something， 发送次数：2次
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.tool.AmopPublisher testTopic false "Tell you something" 2
```

得到控制台的回复

```bash
3s ...
2s ...
1s ...
start test
===================================================================
Step 1: Send out msg, topic:testTopic content:Tell you something
Step 3:Get response, { errorCode:0 error:null seq:b6ca68689b304d5abf0093269a9936cf content:
testTopicYes, I received! }
Step 1: Send out msg, topic:testTopic content:Tell you something
Step 3:Get response, { errorCode:0 error:null seq:aa9a461456d24902a391299b91d0a707 content:
testTopicYes, I received! }
```

回到订阅者的控制台，看到订阅者的控制台有新的输出

```bash
Start test
Step 2:Receive msg, topic:testTopic content:Tell you something
|---response:Yes, I received!
Step 2:Receive msg, topic:testTopic content:Tell you something
|---response:Yes, I received!
```

一个AMOP消息的发送Demo就完成了。

