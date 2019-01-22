<center> <h1>节点准入管理操作文档</h1> </center>

本文档描述节点准入管理的实践操作，建议阅读本操作文档前请先行了解[《节点准入管理介绍》](../design/security_control/node_access_management.md)。

## 目录
<!-- TOC -->

- [1 操作项目](#1-操作项目)
- [2 操作方式](#2-操作方式)
- [3 操作示例](#3-操作示例)
    - [3.1 A节点加入网络](#31-A节点加入网络)
    - [3.2 A节点退出网络](#32-A节点退出网络)
    - [3.3 A节点加入群组](#33-A节点加入群组)
    - [3.4 A节点退出群组](#34-A节点退出群组)
    - [3.5 A节点将B节点列入CA黑名单](#35-A节点将B节点列入CA黑名单)
    - [3.6 A节点将B节点移除CA黑名单](#36-A节点将B节点移除CA黑名单)
- [4 操作工具](#4-操作工具)
    - [4.1 控制台](#41-控制台)
    - [4.2 RPC](#42-RPC)
        
<!-- /TOC -->

## 1 操作项目

本文档对以下六个操作项目的顺序分别进行说明：

1. 一节点加入/退出网络
2. 一节点加入/退出群组（节点类型的修改、群组节点的查询）
3. 一节点将他节点列入/移除CA黑名单

## 2 操作方式

- 修改节点配置：节点修改自身配置后重启生效，涉及的操作项目包括**网络的加入/退出、CA黑名单的列入/移除**。配置项的修改例子参考[配置文件示例](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-introduction#bkmrk-5.2-%E9%85%8D%E7%BD%AE%E6%96%87%E4%BB%B6%E7%A4%BA%E4)。
- 交易共识上链：节点发送上链交易修改需群组共识的配置项，涉及的操作项目包括**节点类型的修改**。目前提供的发送交易途径为控制台，控制台操作参考[4.1 控制台](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-4.1-%E6%8E%A7%E5%88%B6%E5%8F%B0)。
- RPC查询：使用curl命令查询链上信息，涉及的操作项目包括**群组节点的查询**。RPC操作参考[4.2 RPC](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-4.2-rpc)。

## 3 操作示例

本节将以下图为例对上述六种操作进行描述。虚线表示节点间能进行网络通信，实线表示节点间在可通信的基础上具备群组关系，不同颜色区分不同的群组关系。图中有一个网络，包含三个群组，其中群组Group3有三个节点。Group3是否与其他群组存在交集节点，不影响以下操作过程的通用性。

![群组例子.png](../../../images/node_access_management/multi_ledger_example.png)

### 3.1 A节点加入网络

场景描述：

节点3原先不在网络中，现在加入网络。

操作顺序：

1. 对于节点3，可选择任意群组（例如Group3）的已有节点（例如节点1和节点2）及自身节点的IP和Port加入到自身节点的<font color=#FF0000>P2P节点连接列表</font>；
2. 生成可信第三方颁发的证书，在<font color=#FF0000>节点证书</font>中指定证书路径；
3. 重启节点3；
4. 使用netstat命令确认节点3与节点1和节点2的连接已经建立，加入网络操作完成。

补充说明：

- 节点1和2不需修改自身的P2P节点连接列表；
- 步骤1中所选择的群组建议为节点3后续需加入的群组；
- 节点3的<font color=#FF0000>**网络可改配置文件**</font>的其他配置项可与节点1和2的一致。

### 3.2 A节点退出网络

场景描述：

节点3已在网络中，与节点1和节点2通信，现在退出网络。

操作顺序：

1. 对于节点3，将自身的<font color=#FF0000>P2P节点连接列表</font>内容清空，重启节点3；
2. 对于节点1和2，将节点3从自身的<font color=#FF0000>P2P节点连接列表</font>中移除（如有），重启节点1和2；
3. 使用netstat命令确认节点1与节点1（和2）的原有连接已经断开，退出网络操作完成。

补充说明：

- <font color=#FF0000>节点3需先退出群组再退出网络，**退出顺序由用户保证，系统不再作校验**</font>；
- 网络连接由节点主动发起，如缺省第2步，节点3仍可感知节点1和节点2发起的P2P连接请求，并建立连接；
- 如果节点3想拒绝节点1和节点2的连接请求，可参考[3.5 A节点将B节点列入CA黑名单](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-3.5-a%E8%8A%82%E7%82%B9%E5%B0%86b%E8%8A%82%E7%82)进行操作。

### 3.3 A节点加入群组

场景描述：

群组Group3原有节点1和节点2，两节点轮流出块，现在将节点3加入群组。

操作顺序：

1. 节点3拷贝节点1（或2）的<font color=#FF0000>P2P节点链接列表</font>，然后将自身IP和P2P Port加入列表中；
2. 节点3拷贝节点1（或2）的<font color=#FF0000>群组节点初始列表</font>，不需改动；
3. 重启节点3；
4. 将节点3作为**观察节点**类型，调用[4.1 控制台](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-4.1-%E6%8E%A7%E5%88%B6%E5%8F%B0)的<font color=#FF0000>addObserver</font>接口发送加入群组交易；
5. 区块同步结束后，调用[4.1 控制台](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-4.1-%E6%8E%A7%E5%88%B6%E5%8F%B0)的<font color=#FF0000>addMiner</font>接口发送交易修改节点3类型为**记账节点**；
6. 查询日志确认节点3参与出块，或通过[4.2 RPC](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-4.2-rpc)的<font color=#FF0000>getMinerList</font>命令查询返回的json中是否包含节点3的nodeID，加入群组操作完成。

补充说明：

- 节点3首次启动会将配置的群组节点初始列表内容写入群组节点系统表，区块同步结束后，<font color=#FF0000>**群组各节点的群组节点系统表均一致**</font>；
- <font color=#FF0000>节点3需先完成网络节点准入后，再执行加入群组的操作，**操作顺序由用户保证，系统不再作校验**</font>；
- <font color=#FF0000>**节点3的群组固定配置文件需与节点1和2的一致**</font>。

### 3.4 A节点退出群组

场景描述：

群组Group3原有节点1、节点2和节点3，三节点轮流出块，现在将节点3退出群组。

操作顺序：

1. 调用[4.1 控制台](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-4.1-%E6%8E%A7%E5%88%B6%E5%8F%B0)的<font color=#FF0000>remove</font>接口发送退出群组交易；
2. 查询日志确认节点3不参与出块，或通过[4.2 RPC](http://wiki.weoa.com/books/fisco-bcos/page/node-access-management-manual#bkmrk-4.2-rpc)的<font color=#FF0000>getMinerList</font>和<font color=#FF0000>getObserverList</font>命令查询返回的json中均不包含节点3的nodeID，退出群组操作完成。

补充说明：

- 节点3可以记账节点或观察节点的身份执行退出操作。

### 3.5 A节点将B节点列入CA黑名单

场景描述：

节点1和节点2在群组Group3中，三节点轮流出块，现在节点1将节点2加入自身黑名单。

操作顺序：

1. 对于节点1，将节点2的公钥NodeID加入自身的<font color=#FF0000>CA黑名单</font>；
2. 重启节点1；
3. 使用netstat命令确认节点1与节点2的原有连接已经断开，加入黑名单操作完成。

补充说明：

- 节点1添加节点2到自身CA黑名单的操作，将断开与节点2的网络连接及AMOP通信；
- <font color=#FF0000>节点1添加节点2到自身CA黑名单的操作，将对节点1所在群组Group3的**共识及同步消息/数据转发**</font>。

### 3.6 A节点将B节点移除CA黑名单

场景描述：

节点1的CA黑名单中有节点2的nodeID，节点2的CA黑名单中没有节点1的nodeID，现在节点1将节点2移除自身的CA黑名单。

操作顺序：

1. 对于节点1，将节点2的公钥NodeID从自身的<font color=#FF0000>CA黑名单</font>移除；
2. 重启节点1；
3. 使用netstat命令确认节点1与节点2重新建立连接，移除黑名单操作完成。

## 4 操作工具

### 4.1 控制台

控制台提供的命令包括：

- [addMiner](http://wiki.weoa.com/books/fisco-bcos/page/sdk%E6%8E%A7%E5%88%B6%E5%8F%B0#bkmrk-addminer)：修改/增加一节点为记账节点
- [addObserver](http://wiki.weoa.com/books/fisco-bcos/page/sdk%E6%8E%A7%E5%88%B6%E5%8F%B0#bkmrk-addobserver)：修改/增加一节点为观察节点
- [removeNode](http://wiki.weoa.com/books/fisco-bcos/page/sdk%E6%8E%A7%E5%88%B6%E5%8F%B0#bkmrk-removenode)：移除节点（节点被移除后既不是记账节点，也不是观察节点）
- [getMinerList](http://wiki.weoa.com/books/fisco-bcos/page/sdk%E6%8E%A7%E5%88%B6%E5%8F%B0#bkmrk-getminerlist)：查看群组中记账节点列表
- [getObserverList](http://wiki.weoa.com/books/fisco-bcos/page/sdk%E6%8E%A7%E5%88%B6%E5%8F%B0#bkmrk-getobserverlist)：查看群组中观察节点列表

控制台详细使用方法请参考[《SDK控制台》](http://wiki.weoa.com/books/fisco-bcos/page/sdk%E6%8E%A7%E5%88%B6%E5%8F%B0)。

### 4.2 RPC

**群组节点的查询**的RPC接口如下，RPC详细使用方法请参考[《FISCO BCOS2.0 JSON-RPC 接口》](http://wiki.weoa.com/books/fisco-bcos/page/fisco-bcos20-json-rpc-%E6%8E%A5%E5%8F%A3)。

```
// 查询特定群组的记账节点
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getMinerList","params":[1],"id":1}' http://127.0.0.1:30405 |jq
// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    "9c03693698f72244af165817579996c4dbc9e90a78322bea37497fbcddba23ee1fcc99888cc06d039e468d7ca827d3a11fb95a994a57061efc488b4f8d33bd47",
    "4ba790ee3d17077103b3a9ca1b9b84cd05df1de89e393604e726d259ffcbcd2de4c5d01e5d7a8be3599f3b80a40e94e41818b0b6ef6b569c9a1203f13516844b",
    "d077a7a4829e9cc98c11cd1cb0d4b775edbb35c340c7cfa46a39c59d49e6d8b1b0507313119103c9d5665a9814972c4418f368dab9ff76b380249c33f2baf2cb",
    "6e71a2cff0f38f44f6f80cd7c96a432c38ef908451ce72b44843ebe5bd27655c71a0d603f94e32addba042b99b29a8c85575d181d8a42487db5a533d48dca142"
  ]
}

// 查询特定群组的观察节点
// Request
curl -X POST --data '{"jsonrpc":"2.0","method":"getObserverList","params":[1],"id":1}' http://127.0.0.1:30405 |jq
// Result
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    "8fcd6706a82e279a517104ab2676605c9cec1223c7dbb6f53f4bfc326325c43d75e1cbe520ea53cc36c4f8438c3812d9a016830e3e158d6a69266857da35c4ca"
  ]
}
```
