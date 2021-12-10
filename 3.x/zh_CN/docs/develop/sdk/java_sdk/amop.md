# AMOP 功能

标签：``java-sdk`` ``AMOP`` ``链上信使``

----
Java SDK支持[链上信使协议AMOP（Advanced Messages Onchain Protocol）](../../manual/amop_protocol.html)，用户可以通过AMOP协议与其它机构互传消息。 FIXME: 链接有误

## 1. 接口说明

AMOP支持任何一个订阅了某话题的订阅者都能收到该话题相关的推送消息

AMOP模块的接口类可参考文件java-sdk中的``sdk-amop/src/main/org/fisco/bcos/sdk/amop/Amop.java``文件，其中主要包含以下几个接口：

### 1.1 subscribeTopic

订阅一个话题

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

### 1.2 sendAmopMsg

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

### 1.3 broadcastAmopMsg

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

### 1.4 start

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

### 1.5 stop

停止AMOP功能。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = new BcosSDK("config-example.toml");
Amop amop = sdk.getAmop();

// 停止AMOP
amop.stop()
```

## 2. 示例

更多的示例请看[java-sdk-demo](https://github.com/FISCO-BCOS/java-sdk-demo)项目源码``java-sdk-demo/src/main/java/org/fisco/bcos/sdk/demo/amop``下的代码示范，链接：[java-sdk-demo GitHub链接](https://github.com/FISCO-BCOS/java-sdk-demo)，[java-sdk-demo Gitee链接](https://gitee.com/FISCO-BCOS/java-sdk-demo)。

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


**示例讲解**

* 配置。

  Java SDK支持动态订阅话题。也可以在配置文件中配置固定的私有话题。

  订阅者配置例子：[java-sdk-demo](https://github.com/FISCO-BCOS/java-sdk-demo)项目源码``java-sdk-demo/src/test/resources/amop/config-subscriber-for-test.toml``

  ```toml
  [network]
  # The peer list to connect
  peers=["127.0.0.1:20202", "127.0.0.1:20203"]
  
  # Configure a private topic as a topic message sender.
  [[amop]]
  topicName = "privTopic"
  # Your private key that used to subscriber verification.
  privateKey = "conf/amop/consumer_private_key.p12"
  password = "123456"
  ```

  注意，订阅方通过这种方法配置的话题，需要在程序中设定一个默认的回调函数，否则，接收消息时会因为找不到回调函数而报错。

  发送者配置例子：[java-sdk-demo](https://github.com/FISCO-BCOS/java-sdk-demo)项目源码``java-sdk-demo/src/test/resources/amop/config-publisher-for-test.toml``

  ```toml
  [network]
  # The peer list to connect
  peers=["127.0.0.1:20200", "127.0.0.1:20201"]
  
  # Configure a "need verify AMOP topic" as a topic message sender.
  [[amop]]
  topicName = "privTopic"
  # Public keys of the nodes that you want to send AMOP message of this topic to.
  publicKeys = [ "conf/amop/consumer_public_key_1.pem"]
  ```

  发送者所配置的公钥是从订阅者那里获取的，与订阅者的私钥是成对的。这样发送者可以通过私有话题"privTopic"向订阅者发送消息。

* 公有话题订阅和发送

  订阅者代码例子：``src/main/java/org/fisco/bcos/sdk/demo/amop/Subscribe.java``

  ```java
  package org.fisco.bcos.sdk.demo.amop;
  
  import java.net.URL;
  import org.fisco.bcos.sdk.amop.Amop;
  import org.fisco.bcos.sdk.config.Config;
  import org.fisco.bcos.sdk.config.ConfigOption;
  import org.fisco.bcos.sdk.config.exceptions.ConfigException;
  import org.fisco.bcos.sdk.demo.perf.ParallelOkPerf;
  import org.fisco.bcos.sdk.jni.common.JniException;
  import org.fisco.bcos.sdk.model.ConstantConfig;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  
  public class Subscribe {
  
      private static final Logger logger = LoggerFactory.getLogger(Subscribe.class);
  
      public static void usage() {
          System.out.println("\tUsage: ");
          System.out.println(
                  "\t\tjava -cp \"conf/:lib/*:apps/*\"  org.fisco.bcos.sdk.demo.amop.Subscribe topic");
          System.out.println("\tExample:");
          System.out.println(
                  "\t\tjava -cp \"conf/:lib/*:apps/*\"  org.fisco.bcos.sdk.demo.amop.Subscribe topic");
          System.exit(0);
      }
  
      public static void main(String[] args)
              throws InterruptedException, JniException, ConfigException {
  
          String configFileName = ConstantConfig.CONFIG_FILE_NAME;
          URL configUrl = ParallelOkPerf.class.getClassLoader().getResource(configFileName);
          if (configUrl == null) {
              System.out.println("The configFile " + configFileName + " doesn't exist!");
              return;
          }
  
          if (args.length < 1) {
              usage();
          }
  
          String topic = args[0];
  
          System.out.println(" ====== AMOP subscribe, topic: " + topic);
  
          String configFile = configUrl.getPath();
          ConfigOption configOption = Config.load(configFile);
  
          Amop amop = Amop.build(configOption);
          amop.start();
  
          amop.subscribeTopic(
                  topic,
                  (endpoint, seq, data) -> {
                      System.out.println(" ==> receive message from client");
                      System.out.println(" \t==> endpoint: " + endpoint);
                      System.out.println(" \t==> seq: " + seq);
                      System.out.println(" \t==> data: " + new String(data));
  
                      amop.sendResponse(endpoint, seq, data);
                  });
  
          while (true) {
  
              Thread.sleep(10000);
          }
      }
  }
  ```
  
  回调函数例子: ``src/main/java/org/fisco/bcos/sdk/demo/amop/tool/DemoAmopCallback.java``
  
  ```java
  package org.fisco.bcos.sdk.demo.amop.tool;
  
  import static org.fisco.bcos.sdk.utils.ByteUtils.byteArrayToInt;
  
  import java.io.BufferedOutputStream;
  import java.io.File;
  import java.io.FileOutputStream;
  import java.io.IOException;
  import java.util.Arrays;
  import org.fisco.bcos.sdk.amop.AmopCallback;
  import org.fisco.bcos.sdk.amop.topic.AmopMsgIn;
  import org.fisco.bcos.sdk.model.MsgType;
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  
  public class DemoAmopCallback extends AmopCallback {
      private static Logger logger = LoggerFactory.getLogger(DemoAmopCallback.class);
  
      @Override
      public byte[] receiveAmopMsg(AmopMsgIn msg) {
          if (msg.getContent().length > 8) {
              byte[] content = msg.getContent();
              byte[] byteflag = subbytes(content, 0, 4);
              int flag = byteArrayToInt(byteflag);
              if (flag == -128) {
                  // Received a file.
                  byte[] bytelength = subbytes(content, 4, 4);
                  int length = byteArrayToInt(bytelength);
                  byte[] bytefilename = subbytes(content, 8, length);
                  String filename = new String(bytefilename);
                  System.out.println(
                          "Step 2:Receive file, filename length:"
                                  + length
                                  + " filename binary:"
                                  + Arrays.toString(bytefilename)
                                  + " filename:"
                                  + filename);
  
                  int contentlength = content.length - 8 - filename.length();
                  byte[] fileContent = subbytes(content, 8 + filename.length(), contentlength);
                  getFileFromBytes(fileContent, filename);
                  System.out.println("|---save file:" + filename + " success");
                  byte[] responseData = "Yes, I received!".getBytes();
                  if (msg.getType() == (short) MsgType.AMOP_REQUEST.getType()) {
                      System.out.println("|---response:" + new String(responseData));
                  }
                  return responseData;
              }
          }
          
          
          byte[] responseData = "Yes, I received!".getBytes();
          // Print receive amop message
          System.out.println(
                  "Step 2:Receive msg, topic:"
                          + msg.getTopic()
                          + " content:"
                          + new String(msg.getContent()));
          if (msg.getType() == (short) MsgType.AMOP_REQUEST.getType()) {
              System.out.println("|---response:" + new String(responseData));
          }
          // Response to the message sender
          return responseData;
      }
  
      public static byte[] subbytes(byte[] src, int begin, int count) {
          byte[] bs = new byte[count];
          System.arraycopy(src, begin, bs, 0, count);
          return bs;
      }
  
      public static void getFileFromBytes(byte[] b, String outputFile) {
          File ret = null;
          BufferedOutputStream stream = null;
          FileOutputStream fstream = null;
          try {
              ret = new File(outputFile);
              fstream = new FileOutputStream(ret);
              stream = new BufferedOutputStream(fstream);
              stream.write(b);
          } catch (Exception e) {
              logger.error(" write exception, message: {}", e.getMessage());
          } finally {
              if (stream != null) {
                  try {
                      stream.close();
                  } catch (IOException e) {
                      logger.error(" close exception, message: {}", e.getMessage());
                  }
              }
  
              if (fstream != null) {
                  try {
                      fstream.close();
                  } catch (IOException e) {
                      logger.error(" close exception, message: {}", e.getMessage());
                  }
              }
          }
      }
  }
  ```
  
  发送方使用例子：``src/main/java/org/fisco/bcos/sdk/demo/amop/tool/AmopPublisher.java``
  
  ```java
  package org.fisco.bcos.sdk.demo.amop.tool;
  
  import org.fisco.bcos.sdk.BcosSDK;
  import org.fisco.bcos.sdk.amop.Amop;
  import org.fisco.bcos.sdk.amop.AmopMsgOut;
  import org.fisco.bcos.sdk.amop.topic.TopicType;
  
  public class AmopPublisher {
      private static final int parameterNum = 4;
      private static String publisherFile =
              AmopPublisher.class
                      .getClassLoader()
                      .getResource("amop/config-publisher-for-test.toml")
                      .getPath();
  
      /**
       * @param args topicName, isBroadcast, Content(Content you want to send out), Count(how many msg
       *     you want to send out)
       * @throws Exception AMOP publish exceptioned
       */
      public static void main(String[] args) throws Exception {
          if (args.length < parameterNum) {
              System.out.println("param: target topic total number of request");
              return;
          }
          String topicName = args[0];
          Boolean isBroadcast = Boolean.valueOf(args[1]);
          String content = args[2];
          Integer count = Integer.parseInt(args[3]);
          BcosSDK sdk = BcosSDK.build(publisherFile);
          Amop amop = sdk.getAmop();
  
          System.out.println("3s ...");
          Thread.sleep(1000);
          System.out.println("2s ...");
          Thread.sleep(1000);
          System.out.println("1s ...");
          Thread.sleep(1000);
  
          System.out.println("start test");
          System.out.println("===================================================================");
  
          for (Integer i = 0; i < count; ++i) {
              Thread.sleep(2000);
              AmopMsgOut out = new AmopMsgOut();
              out.setType(TopicType.NORMAL_TOPIC);
              out.setContent(content.getBytes());
              out.setTimeout(6000);
              out.setTopic(topicName);
              DemoAmopResponseCallback cb = new DemoAmopResponseCallback();
              if (isBroadcast) {
                // send out amop message by broad cast
                  amop.broadcastAmopMsg(out);
                  System.out.println(
                          "Step 1: Send out msg by broadcast, topic:"
                                  + out.getTopic()
                                  + " content:"
                                  + new String(out.getContent()));
              } else {
                // send out amop message
                  amop.sendAmopMsg(out, cb);
                  System.out.println(
                          "Step 1: Send out msg, topic:"
                                  + out.getTopic()
                                  + " content:"
                                  + new String(out.getContent()));
              }
          }
      }
  }
  
  ```
  
  发送方接收回包的函数例子：``src/main/java/org/fisco/bcos/sdk/demo/amop/tool/DemoAmopResponseCallback.java``
  
  ```java
  package org.fisco.bcos.sdk.demo.amop.tool;
  
  import org.fisco.bcos.sdk.amop.AmopResponse;
  import org.fisco.bcos.sdk.amop.AmopResponseCallback;
  
  public class DemoAmopResponseCallback extends AmopResponseCallback {
  
      @Override
      public void onResponse(AmopResponse response) {
        // 当出现102超时错误时，打印该错误
          if (response.getErrorCode() == 102) {
              System.out.println(
                      "Step 3: Timeout, maybe your file is too large or your gave a short timeout.");
          } else {
            // 收到正常的回包
              if (response.getAmopMsgIn() != null) {
                  System.out.println(
                          "Step 3:Get response, { errorCode:"
                                  + response.getErrorCode()
                                  + " error:"
                                  + response.getErrorMessage()
                                  + " seq:"
                                  + response.getMessageID()
                                  + " content:"
                                  + new String(response.getAmopMsgIn().getContent())
                                  + " }");
              } else {
                // 收到其它错误
                  System.out.println(
                          "Step 3:Get response, { errorCode:"
                                  + response.getErrorCode()
                                  + " error:"
                                  + response.getErrorMessage()
                                  + " seq:"
                                  + response.getMessageID());
              }
          }
      }
  }
  ```

接下来，可以根据下一节的方法来试用这些AMOP的Demo。

## 3. 快速试用AMOP

### 第一步：下载项目

```bash
mkdir -p ~/fisco && cd ~/fisco
# 获取java-sdk代码
git clone https://github.com/FISCO-BCOS/java-sdk-demo

# 若因为网络问题导致长时间拉取失败，请尝试以下命令：
git clone https://gitee.com/FISCO-BCOS/java-sdk-demo

cd java-sdk-demo
# 构建项目
bash gradlew build
```

### 第二步：搭建FISCO BCOS区块链网络

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
chmod 777 initEnv.sh     
# 运行initEnv.sh文件
./initEnv.sh
```

运行完成后，终端显示4个节点已经启动，demo环境就已经准备好了。

```bash
FISCO-BCOS Version : 3.0.0-rc1
Build Time         : 20200814 09:04:17
Build Type         : Darwin/appleclang/RelWithDebInfo
Git Branch         : HEAD
Git Commit Hash    : e4a5ef2ef64d1943fccc4ebc61467a91779fb1c0
try to start node0
try to start node1
try to start node2
try to start node3
 node1 start successfully
 node3 start successfully
 node0 start successfully
 node2 start successfully
```

方法二：您也可以根据[指引](../../installation.html#fisco-bcos)搭建FISCO BCOS区块链网络。然后进行以下操作  【FIXME: 链接有误】

```cmd
# 当前目录为java-sdk,构建项目
gradlew.bat build -x test
```

### 第三步：配置

* 复制证书：将你搭建FISCO BCOS网络节点``nodes/${ip}/sdk/`` 目录下的证书复制到``java-sdk-demo/dist/conf``目录下。

* 修改配置：如果您采用的是方法一搭建的网络，则无需修改配置。如果您采用方法二搭建区块链，需要修改订阅者配置文件``java-sdk-demo/dist/conf/amop/config-subscriber-for-test.toml``，和发送者配置文件``java-sdk-demo/dist/conf/amop/config-publisher-for-test.toml``，修改配置文件中的节点信息。 注意：订阅者和发送者最好不连相同节点，如果连接了相同节点，则会被认为是同一个组织下的成员，私有话题无需认证即可通讯。

### 第四步：运行Demo

#### 公有话题Demo

新打开一个终端，下载java-sdk-demo的代码，并build。

```bash
cd ~/fisco
# 获取java-sdk-demo代码
git clone https://github.com/FISCO-BCOS/java-sdk-demo

# 若因为网络问题导致长时间拉取失败，请尝试以下命令：
git clone https://gitee.com/FISCO-BCOS/java-sdk-demo

cd java-sdk-demo

# build项目
bash gradlew build
```

**运行订阅者：**

```bash
# 进入java-sdk-demo/dist目录
cd dist 
# 使用第三节中所描述的工具
# 我们订阅名为”testTopic“的话题
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.tool.AmopSubscriber testTopic
```

订阅方的终端输出

```bash
Start test
```

然后，运行发送者Demo

**单播消息**：

```bash
# 调用AmopPublisher发送AMOP消息
# 话题名：testTopic，是否广播：false(即使用单播)，内容：Tell you something， 发送次数：2次
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.tool.AmopPublisher testTopic false "Tell you something" 2
```

终端输出：

```bash
3s ...
2s ...
1s ...
start test
===================================================================
Step 1: Send out msg, topic:testTopic content:Tell you something
Step 3:Get response, { errorCode:0 error:null seq:3fa95b760f7f48ddbdf1216a48f361e0 content:Yes, I received! }
Step 1: Send out msg, topic:testTopic content:Tell you something
Step 3:Get response, { errorCode:0 error:null seq:2bc754b1d8844445a4cc2af226fbaa58 content:Yes, I received! }
```

同时，返回到话题订阅者的终端，发现终端输出：

```bash
Step 2:Receive msg, topic:testTopic content:Tell you something
|---response:Yes, I received!
Step 2:Receive msg, topic:testTopic content:Tell you something
|---response:Yes, I received!
```

**广播消息**：

```bash
# 调用AmopPublisher发送AMOP消息
# 话题名：testTopic，是否广播：false(即使用单播)，内容：Tell you something， 发送次数：1次
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.tool.AmopPublisher testTopic true "Tell you something" 1
```

终端的输出

```bash
3s ...
2s ...
1s ...
start test
===================================================================
Step 1: Send out msg by broadcast, topic:testTopic content:Tell you something
```

同时，返回到话题订阅者的终端，发现终端输出：

```java
Start test
Step 2:Receive msg, topic:testTopic content:Tell you something
Step 2:Receive msg, topic:testTopic content:Tell you something
Step 2:Receive msg, topic:testTopic content:Tell you something
Step 2:Receive msg, topic:testTopic content:Tell you something
```

注意：

1. 广播消息没有回包。

2. 接收方可能收到多条重复的广播信息。比如，上述例子中，网络中总共有4个节点，发送发连接节点1和2，接收方连接3和4。因此，广播的时候存在四条路径[发送方 -> 节点1 -> 节点3 -> 接收方]，[发送方 -> 节点1 -> 节点4 -> 接收方]，[发送方 -> 节点2 -> 节点3 -> 接收方]，[发送方 -> 节点2 -> 节点4 -> 接收方]，则接收方SDK收到了4条信息。

**发送文件**：

```bash
# 调用AmopPublisherFile发送AMOP消息文件
# 话题名：testTopic，是否广播：false(即使用单播)，文件：dist/conf/ca.crt， 发送次数：1次
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.tool.AmopPublisherFile testTopic false ../docs/images/FISCO_BCOS_Logo.svg 1
```

终端输出

```bash
3s ...
2s ...
1s ...
start test
===================================================================
Step 1: Send out msg, topic:testTopic content: file ../docs/images/FISCO_BCOS_Logo.svg
Step 3:Get response, { errorCode:0 error:null seq:6e6a1e23d7ca47a0a1904bcb0a151f51 content:Yes, I received! }
```

订阅者终端输出

```bash
Start test
Step 2:Receive file, filename length:34 filename binary:[46, 46, 47, 100, 111, 99, 115, 47, 105, 109, 97, 103, 101, 115, 47, 70, 73, 83, 67, 79, 95, 66, 67, 79, 83, 95, 76, 111, 103, 111, 46, 115, 118, 103] filename:../docs/images/FISCO_BCOS_Logo.svg
|---save file:../docs/images/FISCO_BCOS_Logo.svg success
|---response:Yes, I received!
```
## 4. 错误码

- 99：发送消息失败，AMOP经由所有链路的尝试后，消息未能发到服务端，建议使用发送时生成的`seq`，检查链路上各个节点的处理情况。
- 100：区块链节点之间经由所有链路的尝试后，消息未能发送到可以接收该消息的节点，和错误码‘99’一样，建议使用发送时生成的‘seq’，检查链路上各个节点的处理情况。
- 101：区块链节点往Sdk推送消息，经由所有链路的尝试后，未能到达Sdk端，和错误码‘99’一样，建议使用发送时生成的‘seq’，检查链路上各个节点以及Sdk的处理情况。
- 102：消息超时，建议检查服务端是否正确处理了消息，带宽是否足够。
- 103：因节点出带宽限制，SDK发到节点的AMOP请求被拒绝。
