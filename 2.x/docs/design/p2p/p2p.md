# 对等网络

标签：``对等网络`` ``P2P模块`` ``点对点网络`` ``状态同步``  ``AMOP`` 

----
## 设计目标

FISCO BCOS P2P模块提供高效、通用和安全的网络通信基础功能，支持区块链消息的单播、组播和广播，支持区块链节点状态同步，支持多种协议。

## P2P主要功能

- 区块链节点标识

通过区块链节点标识唯一标识一个区块链节点，在区块链网络上通过区块链节点标识对区块链节点进行寻址

- 管理网络连接

维持区块链网络上区块链节点间的TCP长连接，自动断开异常连接，自动发起重连

- 消息收发

在区块链网络的区块链节点间，进行消息的单播、组播或广播

- 状态同步

在区块链节点间同步状态

## 区块链节点标识

区块链节点标识由ECC算法的公钥生成，每个区块链节点必须有唯一的ECC密钥对，区块链节点标识在区块链网络中唯一标识一个区块链节点

通常情况下，一个节点要加入区块链网络，至少要准备三个文件：

- node.key 节点密钥，ECC格式
- node.crt 节点证书，由CA颁发
- ca.crt CA证书，CA机构提供

区块链节点除了有唯一区块链节点标识，还能关注Topic，供寻址使用

区块链节点寻址：

- 区块链节点标识寻址

通过区块链节点标识，在区块链网络中定位唯一的区块链节点

- Topic寻址

通过Topic，在区块链网络中定位一组关注该Topic的节点

## 管理网络连接

区块链节点间，会自动发起和维持TCP长连接，在系统故障、网络异常时，主动发起重连

区块链节点间建立连接时，会使用CA证书进行认证

### 连接建立流程

```eval_rst
.. mermaid::

    sequenceDiagram
        participant 区块链节点A
        participant 区块链节点B

        区块链节点A->>区块链节点A: 加载密钥和证书
        区块链节点B->>区块链节点B: 加载密钥和证书
        区块链节点A->>区块链节点B: 发起连接
        区块链节点B->>区块链节点A: 连接成功
        区块链节点B->区块链节点A: 发起SSL握手
        区块链节点A->>区块链节点A: 从证书获取公钥，作为节点ID
        区块链节点B->>区块链节点B: 从证书获取公钥，作为节点ID
        区块链节点B->区块链节点A: 握手成功，建立SSL连接

```

## 消息收发

区块链节点间消息支持单播、组播和广播

- 单播，单个区块链节点向单个区块链节点发送消息，通过区块链节点标识寻址
- 组播，单个区块链节点向一组区块链节点发送消息，通过Topic寻址
- 广播，单个区块链节点向所有区块链节点发送消息

### 单播流程

```eval_rst
.. mermaid::

    sequenceDiagram
        participant 区块链节点A
        participant 区块链节点B

        区块链节点A->>区块链节点A: 根据节点ID，筛选在线节点
        区块链节点A->>区块链节点B: 发送消息
        区块链节点B->>区块链节点A: 消息回包

```

### 组播流程

```eval_rst
.. mermaid::

    sequenceDiagram
        participant 区块链节点A
        participant 区块链节点B
        participant 区块链节点C
        participant 区块链节点D

        区块链节点A->>区块链节点A: 根据Topic 1，选择节点B、C
        区块链节点A->>区块链节点B: 发送消息
        区块链节点A->>区块链节点C: 发送消息
        区块链节点B->>区块链节点B: 根据Topic 2，选择节点C、D
        区块链节点B->>区块链节点C: 发送消息
        区块链节点B->>区块链节点D: 发送消息
        区块链节点C->>区块链节点C: 根据Topic 3，选择节点D
        区块链节点C->>区块链节点D: 发送消息

```

### 广播流程

```eval_rst
.. mermaid::

    sequenceDiagram
        participant 区块链节点A
        participant 区块链节点B
        participant 区块链节点C
        participant 区块链节点D

        区块链节点A->>区块链节点A: 遍历所有节点ID
        区块链节点A->>区块链节点B: 发送消息
        区块链节点A->>区块链节点C: 发送消息
        区块链节点A->>区块链节点D: 发送消息
        区块链节点B->>区块链节点B: 遍历所有节点ID
        区块链节点B->>区块链节点C: 发送消息
        区块链节点B->>区块链节点D: 发送消息
        区块链节点C->>区块链节点C: 遍历所有节点ID
        区块链节点C->>区块链节点D: 发送消息

```

## 状态同步

每个节点会维护自身的状态，并将状态的Seq在全网定时广播，与其它节点同步

```eval_rst
.. mermaid::

    sequenceDiagram
        participant 区块链节点A
        participant 区块链节点B

        区块链节点A->区块链节点B: 广播seq
        区块链节点A->>区块链节点A: 判断节点B的seq是否变化
        区块链节点A->>区块链节点B: seq变化，发起状态查询请求
        区块链节点B->>区块链节点A: 返回节点状态
        区块链节点A->>区块链节点A: 更新节点B的状态和seq

```

## AMOP 消息转发流程

### 单播时序图

```eval_rst
.. mermaid::

    sequenceDiagram
        participant sdk [Subscriber]

        participant 节点0
        
        participant 节点1
        
        participant sdk [Publisher]
        
        sdk [Subscriber]->>节点0: 订阅 topic1、类型：0x32
        
        节点0->>节点0: 更新 topic 列表
        
        节点1->>节点0: 请求 topic 列表
        
        节点0-->>节点1: 响应 topic 列表
        
        sdk [Publisher]->>节点1: 往 topic1 单播消息、类型：0x30
        
        节点1->>节点0: 节点转发消息

        节点0->>sdk [Subscriber]: 节点转发消息

        sdk [Subscriber]-->>节点0: 回包、类型：0x31

        节点0-->>节点1: 节点转发消息

        节点1-->>sdk [Publisher]:节点转发消息

```

```eval_rst
.. note::

    - 单播是指如果有多个 Subscriber 订阅同一个 topic，节点则随机选择一个 Subscriber 推送消息
    - 消息发布者和消息订阅者需要选择同一个 topic
    - Subscriber 接收到消息之后的回包是由 sdk 自动发送，不需要用户自己处理，该回包仅仅表示 Subscriber 成功收到消息
    - 如果 Publisher 在推送消息之前，没有对应的订阅者，那么 Publisher 将会收到错误码 *100*，表示网络中没有可用节点

```

### 多播时序图

```eval_rst
.. mermaid::

    sequenceDiagram
        participant sdk [Subscriber0]
        
        participant sdk [Subscriber1]

        participant 节点0
        
        participant 节点1
        
        participant 节点2
        
        participant sdk [Publisher]
        
        sdk [Subscriber0]->>节点0: 订阅 topic1、类型：0x32
        
        节点0->>节点0: 更新 topic 列表
        
        节点1->>节点0: 请求 topic 列表
        
        节点0-->>节点1: 响应 topic 列表
        
        节点2->>节点0: 请求 topic 列表
        
        节点0-->>节点2: 响应 topic 列表
        
        sdk [Subscriber1]->>节点1: 订阅 topic1、类型：0x32
        
        节点1->>节点1: 更新 topic 列表
        
        节点0->>节点1: 请求 topic 列表
        
        节点1-->>节点0: 响应 topic 列表
        
        节点2->>节点1: 请求 topic 列表
        
        节点1-->>节点2: 响应 topic 列表
        
        sdk [Publisher]->>节点2: 往 topic1 多播消息、类型：0x35
        
        节点2->>节点0: 节点转发消息

        节点2->>节点1: 节点转发消息
        
        节点2-->>sdk [Publisher]: 回包、类型：0x31

        节点0->>sdk [Subscriber0]: 节点转发消息

        节点1->>sdk [Subscriber1]: 节点转发消息
        
```

```eval_rst
.. note::

    - 多播是指如果有多个 Subscriber 订阅同一个 topic，节点则向所有的 Subscriber 推送消息
    - 只要网络正常，即使没有 Subsciber 接收消息， Publisher 也可以收到节点消息推送成功的响应包

```

### 带身份验证的单播时序图

```eval_rst
.. mermaid::

    sequenceDiagram
        participant sdk [Publisher]

        participant 节点0
        
        participant 节点1
        
        participant 节点2

        participant sdk [Subscriber0]
        
        participant sdk [Subscriber1]
        
        sdk [Publisher]->>节点0: 订阅 hello、类型：0x32
        
        节点1->>节点0: 请求 topic 列表
        
        节点0-->>节点1: 响应 topic 列表
        
        节点2->>节点0: 请求 topic 列表
        
        节点0-->>节点2: 响应 topic 列表

        sdk [Subscriber0]->>节点1: 订阅 world（非身份验证）、类型：0x32
        
        节点2 ->> 节点1: 请求 topic 列表
        
        节点1-->>节点2: 响应 topic 列表
        
        节点0->>节点1: 请求 topic 列表
        
        节点1-->>节点0: 响应 topic 列表
        
        sdk [Subscriber1]->>节点2: 订阅 hello、类型：0x32
        
        节点1 ->> 节点2: 请求 topic 列表
        
        节点2-->>节点1: 响应 topic 列表
        
        节点0->>节点2: 请求 topic 列表
        
        节点2-->>节点0: 响应 topic 列表

        节点0->>节点0: !$VerifyChannel_!$TopicNeedVerify_hello_{uuid} topic 待验证

        节点0->>sdk [Publisher]: 发送 topic 为 !$TopicNeedVerify_hello 的消息，类型：0x37
        
        sdk [Publisher]-->>节点0: 回包，类型：0x31
        
        sdk [Publisher]->>sdk [Publisher]: 生成随机数
        
        sdk [Publisher]->>节点0: 发送 topic 为<br> !$VerifyChannel_!$TopicNeedVerify_hello_{uuid} <br>的消息，类型：0x30
        
        节点0->>节点2: 节点转发消息
        
        节点2->>sdk [Subscriber1]: 节点转发消息
        
        sdk [Subscriber1]->>sdk [Subscriber1]: 使用私钥 S 对随机数进行签名
        
        sdk [Subscriber1]-->>节点2: 回包，包中 topic 为 !$VerifyChannel_!$TopicNeedVerify_hello、类型：0x31
        
        节点2-->>节点0: 节点转发消息
        
        节点0-->>sdk [Publisher]: 节点转发消息
        
        sdk [Publisher]->>sdk [Publisher]: 使用公钥 P 验证签名
        
        sdk [Publisher]->>节点0: 更新 topic  !$TopicNeedVerify_hello，类型：0x38
        
        节点0->>节点0: 更新状态
        
        sdk [Publisher]->>节点0: 单播、类型：0x30

        节点0->>节点2: 节点转发消息
        
        节点2->>sdk [Subscriber1]: 节点转发消息

        sdk [Subscriber1]-->>节点2: 回包、类型：0x31

        节点2-->>节点0: 节点转发消息

        节点0-->>sdk [Publisher]: 节点转发消息

```

```eval_rst
.. note::

    - 身份验证： Publisher 在推送消息的时候，只给满足身份条件的订阅者推送消息
    - Publisher 拥有公钥 P，同时监听 #!\$TopicNeedVerify_hello 和 #!​\$PushChannel_#!​\$TopicNeedVerify_hello 两个 topic
    - Subscriber1 拥有私钥 S，同时监听 #!\$TopicNeedVerify_hello 和#!\$VerifyChannel_#!\$TopicNeedVerify_hello_{uuid} 两个 topic
    - 时序图中所有的 !$ 前都缺少符号 #， mermaid 不支持该符号转义
    - 节点0 给 Publisher 发送的 0x37 消息包中除了 topic：#!\$TopicNeedVerify_hello，还有 topicForCert：#!\$VerifyChannel_#!\$TopicNeedVerify_hello_{uuid} 以及 NodeID
    - Publisher 收到 0x37 消息后回包，包中使用的 topic 为：#!$VerifyChannel_#!$VerifyChannel_#!$TopicNeedVerify_hello_{uuid} 
    - 身份验证通过之后，推送消息使用的 topic 为 !$TopicNeedVerify_hello 
    - 带身份验证的多播在验证身份的流程上同带身份验证的单播一致。不同点在于身份验证通过之后，单播推送消息是一对一，而多播推送消息是一对多

```

**通信协议使用的数据结构**:
- 0x37 消息类型中 content 数据结构：

    ```json
    {"nodeId":"2bb4562f4f4b69e2c5156510da4beddfca548403eb7cea49bd56daed46de31e4d78a44fdfa051170c64f0233dbc0fd75b5b4e8bc2df50a3c9ade833794128623","topic":"#!$TopicNeedVerify_hello","topicForCert":"#!$VerifyChannel_#!$TopicNeedVerify_hello_92be6ce4dbd311eaae5a983b8fda4e0e"}
    ```

- 随机数单播中的 content 数据结构：

    ```json
    {"randValue":"14131f50ef730219d48e1f9c441db871c","topic":"hello"}
    ```

- 随机数签名回包中 content 数据结构：

    ```json
    {"signature":"vU/Vzqn4MiP0nO1xN+M5TOk/YcyFtY/TLrgU38jFdooN66r3TzNVKEBpkNId8gCuAeNpNPCo8vmTV3dcs/Xj1AE="}
    ```

- 0x38 消息类型中 content 数据结构：

    ```json
    {"checkResult":0,"nodeId":"2bb4562f4f4b69e2c5156510da4beddfca548403eb7cea49bd56daed46de31e4d78a44fdfa051170c64f0233dbc0fd75b5b4e8bc2df50a3c9ade833794128623","topic":"#!$TopicNeedVerify_hello"}
    ```