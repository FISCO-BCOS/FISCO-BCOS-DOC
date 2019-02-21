# 节点准入管理手册

本文档描述节点准入管理的实践操作，建议阅读本操作文档前请先行了解[《节点准入管理介绍》](../design/security_control/node_access_management.md)。

## 操作项目

本文档对以下六个操作项目进行说明：

1. 一节点加入/退出网络
2. 一节点加入/退出群组（节点类型的修改、群组节点的查询）
3. 一节点将他节点列入/移除CA黑名单

## 操作方式

- 修改节点配置：节点修改自身配置后重启生效，涉及的操作项目包括**网络的加入/退出、CA黑名单的列入/移除**。
- 交易共识上链：节点发送上链交易修改需群组共识的配置项，涉及的操作项目包括**节点类型的修改**。目前提供的发送交易途径为控制台、SDK提供的precompiled service接口。
- RPC查询：使用curl命令查询链上信息，涉及的操作项目包括**群组节点的查询**。

## 操作示例

本节将以下图为例对上述六种操作进行描述。虚线表示节点间能进行网络通信，实线表示节点间在可通信的基础上具备群组关系，不同颜色区分不同的群组关系。图中有一个网络，包含三个群组，其中群组Group3有三个节点。Group3是否与其他群组存在交集节点，不影响以下操作过程的通用性。

![](../../images/node_access_management/multi_ledger_example.png)

<center>群组例子</center>

Group3的相关节点信息举例为：

节点1的目录名为node0，IP端口为127.0.0.1:30400，nodeID前四个字节为b231b309...

节点2的目录名为node1，IP端口为127.0.0.1:30401，nodeID前四个字节为aab37e73...

节点3的目录名为node2，IP端口为127.0.0.1:30402，nodeID前四个字节为d6b01a96...

### A节点加入网络

场景描述：

节点3原先不在网络中，现在加入网络。

操作顺序：

1 . 执行`tools/gen_node.sh`生成节点目录，目录名以node2为例，node2内有`conf/`目录；

```
# 在目录tools下执行
$ ./gen_node.sh -c nodes/cert/agency -o node2
```

2 . 拷贝node2到`nodes/127.0.0.1/`下，与其他节点目录（node0、node1）同级；

3 . 拷贝`node0/config.ini`到node2目录;

4 . 修改`node2/config.ini`。对于`[rpc]`模块，修改`listen_ip`、`channel_listen_port`和`jsonrpc_listen_port`；对于`[p2p]`模块，修改`listen_port`并在`node.`中增加自身节点信息；

```
$ vim node2/config.ini
[rpc]
    ;rpc listen ip
    listen_ip=127.0.0.1
    ;channelserver listen port
    channel_listen_port=20302
    ;jsonrpc listen port
    jsonrpc_listen_port=8647
[p2p]
    ;p2p listen ip
    listen_ip=0.0.0.0
    ;p2p listen port
    listen_port=30402
    ;nodes to connect
    node.0=127.0.0.1:30400
    node.1=127.0.0.1:30401
    node.2=127.0.0.1:30402
```

5 . 执行`node2/start.sh`启动节点3；

6 . 确认节点3与节点1和节点2的连接已经建立，加入网络操作完成。

```
# 在打开DEBUG级别日志前提下，查看自身节点（node2）连接的节点数及所连接的节点信息（nodeID）
# 以下日志表明节点node2与两个节点（节点的nodeID前4个字节为b231b309、aab37e73）建立了连接
$ tail -f node2/log/log*  | grep P2P
debug|2019-02-21 10:30:18.694258| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30400,nodeID=b231b309...
debug|2019-02-21 10:30:18.694277| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30401,nodeID=aab37e73...
info|2019-02-21 10:30:18.694294| [P2P][Service] heartBeat connected count,size=2
```

补充说明：

- 从节点1拷贝过来的`config.ini`的其余配置可保持不变；
- 节点1和2不需修改自身的P2P节点连接列表；
- 步骤1中所选择的群组建议为节点3后续需加入的群组。

### A节点退出网络

场景描述：

节点3已在网络中，与节点1和节点2通信，现在退出网络。

操作顺序：

1 . 对于节点3，将自身的<font color=#FF0000>P2P节点连接列表</font>内容清空，重启节点3；

```
# 在node2目录下执行
$ ./stop.sh
$ ./start.sh
nohup: appending output to ‘nohup.out’
```

2 . 对于节点1和2，将节点3从自身的<font color=#FF0000>P2P节点连接列表</font>中移除（如有），重启节点1和2；

3 . 确认节点3与节点1（和2）的原有连接已经断开，退出网络操作完成。

补充说明：

- <font color=#FF0000>节点3需先退出群组再退出网络，退出顺序由用户保证，系统不再作校验</font>；
- 网络连接由节点主动发起，如缺少第2步，节点3仍可感知节点1和节点2发起的P2P连接请求，并建立连接；
- 如果节点3想拒绝节点1和节点2的连接请求，可参考**A节点将B节点列入CA黑名单**进行操作。

### A节点加入群组

场景描述：

群组Group3原有节点1和节点2，两节点轮流出块，现在将节点3加入群组。

操作顺序：

1. 节点3加入网络；
2. 节点3拷贝节点1（或2）的`group.3.genesis`（内含<font color=#FF0000>群组节点初始列表</font>）和`group.3.ini`到对应位置，不需改动；
3. 重启节点3；
4. 参考**操作工具**一节，根据节点3的nodeID设置节点3为**共识节点**；
5. 参考**操作工具**一节，查询group3的共识节点中是否包含节点3的nodeID，如存在，加入群组操作完成。

补充说明：

- 节点3首次启动会将配置的群组节点初始列表内容写入群组节点系统表，区块同步结束后，<font color=#FF0000>群组各节点的群组节点系统表均一致</font>；
- <font color=#FF0000>节点3需先完成网络准入后，再执行加入群组的操作，系统将校验操作顺序</font>；
- <font color=#FF0000>节点3的群组固定配置文件需与节点1和2的一致</font>。

### A节点退出群组

场景描述：

群组Group3原有节点1、节点2和节点3，三节点轮流出块，现在将节点3退出群组。

操作顺序：

1. 参考**操作工具**一节，根据节点3的NodeID设置节点3为**游离节点**；
2. 参考**操作工具**一节，查询group3的共识节点中是否包含节点3的nodeID，如已消失，退出群组操作完成。

补充说明：

- 节点3可以共识节点或观察节点的身份执行退出操作。

### A节点将B节点列入CA黑名单

场景描述：

节点1和节点2在群组Group3中，三节点轮流出块，现在节点1将节点2加入自身黑名单。

操作顺序：

1 . 对于节点1（node0），将节点2（node1）的公钥nodeID加入自身的<font color=#FF0000>CA黑名单</font>；

```
$ cat node1/conf/node.nodeid 
aab37e73489bbd277aa848a99229ab70b6d6d4e1b81a715a22608a62f0f5d4270d7dd887394e78bd02d9f31b8d366ce4903481f50b1f44f0e4fda67149208943
$ vim node0/config.ini
;certificate rejected list
[crl]
    ;crl.0 should be nodeid, nodeid's length is 128 
    crl.0=aab37e73489bbd277aa848a99229ab70b6d6d4e1b81a715a22608a62f0f5d4270d7dd887394e78bd02d9f31b8d366ce4903481f50b1f44f0e4fda67149208943
```

2 . 重启节点1；

3 . 确认节点1与节点2的原有连接已经断开，加入黑名单操作完成。

补充说明：

- 节点1添加节点2到自身CA黑名单的操作，将断开与节点2的网络连接及AMOP通信；
- <font color=#FF0000>节点1添加节点2到自身CA黑名单的操作，将对节点1所在群组Group3的共识及同步消息/数据转发</font>。

### A节点将B节点移除CA黑名单

场景描述：

节点1的CA黑名单中有节点2的nodeID，节点2的CA黑名单中没有节点1的nodeID，现在节点1将节点2移除自身的CA黑名单。

操作顺序：

1. 对于节点1，将节点2的公钥nodeID从自身的<font color=#FF0000>CA黑名单</font>移除；
2. 重启节点1；
3. 确认节点1与节点2重新建立连接，移除黑名单操作完成。

## 操作工具

### 控制台

控制台提供的命令包括：

- addSealer：根据节点NodeID设置对应节点为共识节点
- addObserver：根据节点NodeID设置对应节点为观察节点
- removeNode：根据节点NodeID设置对应节点为游离节点
- getSealerList：查看群组中共识节点列表
- getObserverList：查看群组中观察节点列表

控制台详细使用方法请参考[《控制台》](../manual/console.md)。

### SDK
- String addSealer(String nodeId)： 根据节点NodeID设置对应节点为共识节点
- String addObserver(String nodeId)： 根据节点NodeID设置对应节点为观察节点
- String removeNode(String nodeId)： 根据节点NodeID设置对应节点为游离节点

控制台详细使用方法请参考[《SDK》](../sdk/api.md)。

### RPC

查询群组节点的RPC接口包括：

- getSealerList：查看群组中共识节点列表
- getObserverList：查看群组中观察节点列表

RPC详细使用方法请参考[《RPC》](../api.md)。
