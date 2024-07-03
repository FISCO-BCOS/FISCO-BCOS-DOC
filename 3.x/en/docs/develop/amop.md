# 7. Using the AMOP function

tags: "java-sdk" "AMOP" "on-chain messenger protocol"

----
The Java SDK supports the Advanced Messages Onchain Protocol (AMOP). Users can use the AMOP protocol to exchange messages with other organizations。

## 1. Interface description

AMOP enables any subscriber who subscribes to a topic to receive push messages related to that topic

For the interface classes of the AMOP module, see the "src / main / java / org / fisco / bcos / sdk / v3 / amop / Amop.java" file in the java-sdk file, which mainly contains the following interfaces:

### 1.1 subscribeTopic

Subscribe to a topic

**Parameters:**

* topic: Subscribe to Topic Name。Type: "String"。
* callback: The function that processes the topic message, which is called when a message related to the topic is received。Type: "AmopRequestCallback"。

**Example:**

```java
/ / Initialize the Java SDK to obtain the Amop object
BcosSDK sdk = BcosSDK.build("config-example.toml");
Amop amop = sdk.getAmop();
/ / start amop
amop.start();

/ / Define a Callback, override the receiveAmopMsg method, and define the processing flow after receiving the message。
AmopRequestCallback cb = new AmopRequestCallback() {
    @Override
    public void onRequest(String endpoint, String seq, byte[] data) {
        / / You can write the processing logic after receiving the message here。
        System.out.println("Received msg, content:" + new String(data));
    }
};

/ / Subscribe to topics
amop.subscribeTopic("MyTopic", cb);
```

### 1.2 sendAmopMsg

Send AMOP messages as unicast

**Parameters:**

* topic: The topic on which the message was sent。Type: ``String``
* content: Message content。Type: "byte []"
* timeout: Timeout。Type: "int"
* callback: callback function。Type: "AmopResponseCallback"

**注意:**

For a unicast AMOP message, if there are multiple clients subscribing to the topic, a random one can receive the unicast message。

**Example:**

```java
/ / Initialize the Java SDK to obtain the Amop object
BcosSDK bcosSDK = BcosSDK.build("config-example.toml");
Amop amop = bcosSDK.getAmop();
amop.start();

AmopResponseCallback cb = new AmopResponseCallback() {
    @Override
    public void onResponse(Response response) {
        / / You can write the processing logic of the received reply here。
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
/ / Send message
amop.sendAmopMsg("MyTopic", msg.getBytes(), 0, cb);
```

### 1.3 broadcastAmopMsg

Send AMOP messages as broadcast

**Parameters:**

* topic: Broadcast topic。Type: "String"
* content: Message content。Type: "byte []"

**Example:**

```java
/ / Initialize the Java SDK to obtain the Amop object
BcosSDK bcosSDK = BcosSDK.build("config-example.toml");
Amop amop = bcosSDK.getAmop();
amop.start();

/ / Send message
String content = "Send Message";

/ / Send message
amop.broadcastAmopMsg("MyTopic", content.getBytes());
```

### 1.4 unsubscribeTopic

Unsubscribe from a topic。

**Parameters:**

* topic: Unsubscribed Topics。Type: "String"
  
**Example:**

```java
/ / Send message
amop.unsubscribeTopic("MyTopic");
```

### 1.5 sendResponse

Reply Message。

**Parameters:**

* endpoint: The peer endpoint that receives the message. It is returned in the 'AmopRequestCallback' callback。Type: "String"
* seq: Message seq, returned in the 'AmopRequestCallback' callback。Type: "String"
* content: Reply message content。Type: "byte []"

**Example:**

```java
/ / Initialize the Java SDK to obtain the Amop object
BcosSDK sdk = BcosSDK.build("config-example.toml");
Amop amop = sdk.getAmop();
/ / start amop
amop.start();

/ / Define a Callback, override the receiveAmopMsg method, and define the processing flow after receiving the message。
AmopRequestCallback cb = new AmopRequestCallback() {
    @Override
    public void onRequest(String endpoint, String seq, byte[] data) {
        / / Return the message to the sender here
        amop.sendResponse(endpoint, seq, data);
    }
};

/ / Subscribe to topics
amop.subscribeTopic("MyTopic", cb);
```

### 1.6 setCallback

Set the default callback. When the callback specified by the subscription topic is empty, the default callback API is called when a message is received

**Parameters:**

* cb: callback。Type: ``AmopRequestCallback``

## 2. Example

More examples please see [java-sdk-demo](https://github.com/FISCO-BCOS/java-sdk-demo)Code demonstration under "java-sdk-demo / src / main / java / org / fisco / bcos / sdk / demo / amop /" project source code, link: [java-sdk-demo GitHub link](https://github.com/FISCO-BCOS/java-sdk-demo)[java-sdk-demo Gitee link](https://gitee.com/FISCO-BCOS/java-sdk-demo)。

* Example:

  * Subscribe
    * Function: Subscribe to the topic and return the received message directly to the peer
    * 使用:

    ```shell
    Usage:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Subscribe topic
    Example:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Subscribe topic
    ```

  * Publish
    * Function: Send AMOP unicast message
    * 使用:

    ```shell
    Usage:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Publish topic msg
    Example:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Publish topic HelloWorld
    ```

  * Broadcast
    * Function: Broadcast AMOP message
    * 使用:

    ```shell
    Usage:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Broadcast topic msg
    Example:
        java -cp "conf/:lib/*:apps/*"  org.fisco.bcos.sdk.demo.amop.Broadcast topic HelloWorld
    ```
## 2. Quick trial AMOP

### Step 1: Build the FISCO BCOS blockchain network

Reference [Building the First Blockchain Network](../quick_start/air_installation.md)Building the FISCO BCOS Blockchain Network。

### Step 2: Download the project

```shell
mkdir -p ~/fisco && cd ~/fisco
# get java-sdk code
git clone https://github.com/FISCO-BCOS/java-sdk-demo

# If the pull fails for a long time due to network problems, try the following command:
git clone https://gitee.com/FISCO-BCOS/java-sdk-demo

cd java-sdk-demo
# Build Project
bash gradlew build
```

### Step 3: Configure

* Copy the certificate: set up your FISCO BCOS network node "nodes / ${ip}/ sdk / "Copy the certificate from the directory to the" java-sdk-demo / dist / conf "directory。
```shell
# Enter dist directory
cd dist
cp ~/fisco/nodes/127.0.0.1/sdk/* conf/
```

* Modify Configuration
```shell
cp conf/config-example.toml conf/config.toml
```

### Step 4: Run Demo

**Run Subscribers:**

```shell
# In the java-sdk-demo / dist directory
# We subscribe to a topic called "testTopic"
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Subscribe testTopic
```

Subscriber's terminal output

```shell
====== AMOP subscribe, topic: testTopic
```

Then, run the Sender Demo

**unicast message**：

```shell
# Calling Publish to send an AMOP message
# Topic: testTopic, content: Tell you something
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Publish testTopic "Tell you something"
```

Terminal output:

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

At the same time, return to the topic subscriber's terminal and find the terminal output:

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

**broadcast message**：

```shell
# Call Broadcast to send AMOP message
# Topic: testTopic, content: Tell you something =
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Broadcast testTopic "Tell you something"
```

Output of terminal

```shell
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.amop.Broadcast testTopic "Tell you something"
 ====== AMOP broadcast, topic: testTopic ,msg: Tell you something
```

At the same time, return to the topic subscriber's terminal and find the terminal output:

```java
 ==> receive message from client
 	==> endpoint: 127.0.0.1:20200
 	==> seq: 3d5e5bec03cf4693939912ed0236b611
 ==> receive message from client
 	==> data: Tell you something
```

Note:

1. The broadcast message is not returned。

2. The receiver may receive multiple repeated broadcast messages。