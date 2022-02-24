# AMOP 功能

标签：``java-sdk`` ``AMOP`` ``链上信使协议``

----
Java SDK支持链上信使协议AMOP（Advanced Messages Onchain Protocol），用户可以通过AMOP协议与其它机构互传消息。

## 1. 接口说明

AMOP支持任何一个订阅了某话题的订阅者都能收到该话题相关的推送消息

AMOP模块的接口类可参考文件java-sdk中的``sdk-amop/src/main/org/fisco/bcos/sdk/amop/Amop.java``文件，其中主要包含以下几个接口：

### 1.1 subscribeTopic

订阅一个话题

**参数：**

* topic: 订阅话题名称。类型：``String``。
* callback: 处理该话题消息的函数，当收到该话题相关消息时，会被调用。类型：``AmopRequestCallback``。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = BcosSDK.build("config-example.toml");
Amop amop = sdk.getAmop();
// 启动amop
amop.start();

// 定义一个Callback，重写receiveAmopMsg方法，定义收到消息后的处理流程。
AmopRequestCallback cb = new AmopRequestCallback() {
    @Override
    public void onRequest(String endpoint, String seq, byte[] data) {
        // 你可以在这里写收到消息后的处理逻辑。
        System.out.println("Received msg, content:" + new String(data));
    }
};

// 订阅话题
amop.subscribeTopic("MyTopic", cb);
```

### 1.2 sendAmopMsg

以单播的方式发送AMOP消息

**参数：**

* topic: 发送消息的topic。类型: ``String``
* content: 消息内容。类型：``byte[]``
* timeout: 超时时间。类型：``int``
* callback: 回调函数。类型：``AmopResponseCallback``

**注意:**

对于发送的单播AMOP消息，存在多个订阅topic的客户端时，其中随机的一个可以接收到单播消息。

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK bcosSDK = BcosSDK.build("config-example.toml");
Amop amop = bcosSDK.getAmop();
amop.start();

AmopResponseCallback cb = new AmopResponseCallback() {
    @Override
    public void onResponse(Response response) {
        // 你可以在这里写收到回复的处理逻辑。
        System.out.println(
        "Get response, { errorCode:"
        + response.getErrorCode()
        + " error:"
        + response.getErrorMessage()
        + " content:"
        + new String(response.getData())
        + " }");
    }
};

String msg = "Send Message";
// 发送消息
amop.sendAmopMsg("MyTopic", msg.getBytes(), 0, cb);
```

### 1.3 broadcastAmopMsg

以广播的方式发送AMOP消息

**参数：**

* topic: 广播topic。类型：``String``
* content: 消息内容。类型：``byte[]``

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK bcosSDK = BcosSDK.build("config-example.toml");
Amop amop = bcosSDK.getAmop();
amop.start();

// 发送消息
String content = "Send Message";

// 发送消息
amop.broadcastAmopMsg("MyTopic", content.getBytes());
```

### 1.4 unsubscribeTopic

取消订阅话题。

**参数：**

* topic: 取消订阅的话题。类型：``String``
  
**例子：**

```java
// 发送消息
amop.unsubscribeTopic("MyTopic");
```

### 1.5 sendResponse

回复消息。

**参数：**

* endpoint: 接收消息的对端endpoint，`AmopRequestCallback`回调中返回。类型：``String``
* seq: 消息seq，`AmopRequestCallback`回调中返回。类型：``String``
* content: 回复消息内容。类型：``byte[]``

**例子：**

```java
// 初始化java SDK, 获得Amop对象
BcosSDK sdk = BcosSDK.build("config-example.toml");
Amop amop = sdk.getAmop();
// 启动amop
amop.start();

// 定义一个Callback，重写receiveAmopMsg方法，定义收到消息后的处理流程。
AmopRequestCallback cb = new AmopRequestCallback() {
    @Override
    public void onRequest(String endpoint, String seq, byte[] data) {
        // 这里将消息返回给发送端
        amop.sendResponse(endpoint, seq, data);
    }
};

// 订阅话题
amop.subscribeTopic("MyTopic", cb);
```

### 1.6 setCallback

设置默认回调，`subscribeTopic`订阅topic指定的回调为空时，接收到消息时会调用设置的默认回调接口

**参数:**

* cb: 回调。类型: ``AmopRequestCallback``

## 2. 示例

更多的示例请看[java-sdk-demo](https://github.com/FISCO-BCOS/java-sdk-demo)项目源码``java-sdk-demo/src/main/java/org/fisco/bcos/sdk/demo/amop/``下的代码示范，链接：[java-sdk-demo GitHub链接](https://github.com/FISCO-BCOS/java-sdk-demo)，[java-sdk-demo Gitee链接](https://gitee.com/FISCO-BCOS/java-sdk-demo)。

* 示例：

  * Subscribe
    * 功能: 订阅topic，并且将接收的消息直接返回给对端
    * 使用:

    ```shell
    Usage:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Subscribe topic
    Example:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Subscribe topic
    ```

  * Publish
    * 功能: 发送AMOP单播消息
    * 使用:

    ```shell
    Usage:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Publish topic msg
    Example:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Publish topic HelloWorld
    ```

  * Broadcast
    * 功能: 广播AMOP消息
    * 使用:

    ```shell
    Usage:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Broadcast topic msg
    Example:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Broadcast topic HelloWorld
    ```
## 2. 快速试用AMOP

### 第一步：下载项目

```shell
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

根据[指引](../../../quick_start/air_installation.md)搭建FISCO BCOS区块链网络。

### 第三步：配置

* 复制证书：将你搭建FISCO BCOS网络节点``nodes/${ip}/sdk/`` 目录下的证书复制到``java-sdk-demo/dist/conf``目录下。

* 修改配置：`cp config-example.toml config.toml`

### 第四步：运行Demo

#### 公有话题Demo

新打开一个终端，下载java-sdk-demo的代码，并build。

```shell
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

```shell
# 进入java-sdk-demo/dist目录
cd dist 
# 我们订阅名为”testTopic“的话题
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Subscribe testTopic
```

订阅方的终端输出

```shell
====== AMOP subscribe, topic: testTopic
```

然后，运行发送者Demo

**单播消息**：

```shell
# 调用Publishe发送AMOP消息
# 话题名：testTopic，内容：Tell you something
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Publishe testTopic "Tell you something"
```

终端输出：

```shell
$ java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Publish testTopic "Tell you something"

 ====== AMOP publish, topic: testTopic ,msg: Tell you something
 ====== AMOP publish send message
 ==> receive response message from server
 	 responseData: Tell you something
 ====== AMOP publish send message
 ==> receive response message from server
 	 responseData: Tell you something
```

同时，返回到话题订阅者的终端，发现终端输出：

```shell
 ==> receive message from client
 	==> endpoint: 127.0.0.1:20201
 	==> seq: 64f3f40832d6499f8a154c6205001081
 	==> data: Tell you something
 ==> receive message from client
 	==> endpoint: 127.0.0.1:20200
 	==> seq: c121fa99204346c0a87478117607a50b
 	==> data: Tell you something
```

**广播消息**：

```shell
# 调用Broadcast发送AMOP消息
# 话题名：testTopic，内容：Tell you something=
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Broadcast testTopic "Tell you something"
```

终端的输出

```shell
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Broadcast testTopic "Tell you something"
 ====== AMOP broadcast, topic: testTopic ,msg: Tell you something
```

同时，返回到话题订阅者的终端，发现终端输出：

```java
 ==> receive message from client
 	==> endpoint: 127.0.0.1:20200
 	==> seq: 3d5e5bec03cf4693939912ed0236b611
 ==> receive message from client
 	==> data: Tell you something
```

注意：

1. 广播消息没有回包。

2. 接收方可能收到多条重复的广播信息。