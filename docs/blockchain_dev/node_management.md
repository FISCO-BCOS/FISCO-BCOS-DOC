# 组员节点管理

标签：``节点管理`` ``退出群组`` ``游离节点`` ``观察者节点`` ``共识节点``

----
FISCO BCOS引入了[游离节点、观察者节点和共识节点](../design/security_control/node_management.html#id6)，这三种节点类型可通过控制台相互转换。

* 组员
  * 共识节点：参与共识的节点，拥有群组的所有数据（搭链时默认都生成共识节点）。
  * 观察者节点：不参与共识，但能实时同步链上数据的节点。
* 非组员
  * 游离节点：已启动，待等待加入群组的节点。处在一种暂时的节点状态，不能获取链上的数据。

## 操作命令

控制台提供了 **[addSealer](../console/console_of_java_sdk.html#addsealer)** 、**[addObserver](../console/console_of_java_sdk.html#addobserver)** 和 **[removeNode](../console/console_of_java_sdk.html#removenode)** 三类命令将指定节点转换为共识节点、观察者节点和游离节点，并可使用 **[getSealerList](../console/console_of_java_sdk.html#getsealerlist)**、**[getObserverList](../console/console_of_java_sdk.html#getobserverlist)** 和 **[getNodeIDList](../console/console_of_java_sdk.html#getnodeidlist)** 查看当前组的共识节点列表、观察者节点列表和组内所有节点列表。

- addSealer：根据节点NodeID设置对应节点为共识节点；
- addObserver：根据节点NodeID设置对应节点为观察节点；
- removeNode：根据节点NodeID设置对应节点为游离节点；
- getSealerList：查看群组中共识节点列表；
- getObserverList：查看群组中观察节点列表；
- getNodeIDList：查看节点已连接的所有其他节点的NodeID。

例：
将指定节点分别转换成共识节点、观察者节点、游离节点，主要操作命令如下：

```eval_rst
.. important::

    节点准入操作前，请确保：

     - 操作节点Node ID存在，节点Node ID可在节点目录下执行 cat conf/node.nodeid获取
     - 节点加入的区块链所有节点共识正常：正常共识的节点会输出+++日志
```

```bash
# 获取节点Node ID（设节点目录为~/nodes/192.168.0.1/node0/）
$ cat ~/fisco/nodes/192.168.0.1/node0/conf/node.nodeid
7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50

# 连接控制台(设控制台位于~/fisco/console目录)
$ cd ~/fisco/console

$ bash start.sh

# 将指定节点转换为共识节点
[group:1]> addSealer 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
# 查询共识节点列表
[group:1]> getSealerList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]

# 将指定节点转换为观察者节点
[group:1]> addObserver 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50

# 查询观察者节点列表
[group:1]> getObserverList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]

# 将指定节点转换为游离节点
[group:1]> removeNode 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50

# 查询节点列表
[group:1]> getNodeIDList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]
[group:1]> getSealerList
[]
[group:1]> getObserverList
[]

```

## 操作案例

下面结合具体操作案例详细阐述群组扩容操作及节点退网操作。扩容操作分两个阶段， 分别为**将节点加入网络**、**将节点加入群组**。退网操作也分为两个阶段，为**将节点退出群组**、**将节点退出网络**。

### 操作方式

- 修改节点配置：节点修改自身配置后重启生效，涉及的操作项目包括**网络的加入/退出、CA黑名单的列入/移除**。
- 交易共识上链：节点发送上链交易修改需群组共识的配置项，涉及的操作项目包括**节点类型的修改**。目前提供的发送交易途径为控制台、SDK提供的precompiled service接口。
- RPC查询：使用curl命令查询链上信息，涉及的操作项目包括**群组节点的查询**。


### 操作步骤

本节将以下图为例对上述扩容操作及退网操作进行描述。虚线表示节点间能进行网络通信，实线表示节点间在可通信的基础上具备群组关系，不同颜色区分不同的群组关系。下图有一个网络，包含三个群组，其中群组Group3有三个节点。Group3是否与其他群组存在交集节点，不影响以下操作过程的通用性。

![](../../images/node_management/multi_ledger_example.png)

<center>群组例子</center>
Group3的相关节点信息举例为：

节点1的目录名为`node0`，IP端口为127.0.0.1:30400，nodeID前四个字节为b231b309...

节点2的目录名为`node1`，IP端口为127.0.0.1:30401，nodeID前四个字节为aab37e73...

节点3的目录名为`node2`，IP端口为127.0.0.1:30402，nodeID前四个字节为d6b01a96...

#### A节点加入网络

场景描述：

节点3原先不在网络中，现在加入网络。

操作顺序：

1 . 进入nodes同级目录，在该目录下拉取并执行`gen_node_cert.sh`生成节点目录，目录名以node2为例，node2内有`conf/`目录；

```
# 获取脚本
$ curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh && chmod u+x gen_node_cert.sh
# 执行，-c为生成节点所提供的ca路径，agency为机构名，-o为将生成的节点目录名（如果是国密节点，使用 -g 参数）
$ ./gen_node_cert.sh -c nodes/cert/agency -o node2
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_node_cert.sh`
```

2 . 拷贝node2到`nodes/127.0.0.1/`下，与其他节点目录（`node0`、`node1`）同级；
```
$ cp -r ./node2/ nodes/127.0.0.1/
```

3 . 进入`nodes/127.0.0.1/`，拷贝`node0/config.ini`、`node0/start.sh`和`node0/stop.sh`到node2目录;

```
$ cd nodes/127.0.0.1/
$ cp node0/config.ini node0/start.sh node0/stop.sh node2/
```

4 . 修改`node2/config.ini`。对于`[rpc]`模块，修改`channel_listen_port`和`jsonrpc_listen_port`；对于`[p2p]`模块，修改`listen_port`并在`node.`中增加自身节点信息；

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

5 . 节点3拷贝节点1的`node1/conf/group.3.genesis`（内含**群组节点初始列表**）和`node1/conf/group.3.ini`到`node2/conf`目录下，不需改动；
```
$ cp node1/conf/group.3.genesis node2/conf/
$ cp node1/conf/group.3.ini node2/conf/
```

6 . 执行`node2/start.sh`启动节点3；
```
$ ./node2/start.sh
```

7 . 确认节点3与节点1和节点2的连接已经建立，加入网络操作完成。

```
# 在打开DEBUG级别日志前提下，查看自身节点（node2）连接的节点数及所连接的节点信息（nodeID）
# 以下日志表明节点node2与两个节点（节点的nodeID前4个字节为b231b309、aab37e73）建立了连接
$ tail -f node2/log/log*  | grep P2P
debug|2019-02-21 10:30:18.694258| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30400,nodeID=b231b309...
debug|2019-02-21 10:30:18.694277| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30401,nodeID=aab37e73...
info|2019-02-21 10:30:18.694294| [P2P][Service] heartBeat connected count,size=2
```

```eval_rst
.. note::
    - 若启用了白名单，需确保所有节点的config.ini中的白名单都已配置了所有的节点，并正确的将白名单配置刷新入节点中。参考《CA黑白名单》；
    - 从节点1拷贝过来的config.ini的其余配置可保持不变；
    - 理论上，节点1和2不需修改自身的P2P节点连接列表，即可完成扩容节点3的操作；
    - 步骤5中所选择的群组建议为节点3后续需加入的群组；
    - 建议用户在节点1和2的config.ini的P2P节点连接列表中加入节点3的信息并重启节点1和2，保持全网节点的全互联状态。
```

#### A节点退出网络

场景描述：

节点3已在网络中，与节点1和节点2通信，现在退出网络。

操作顺序：

1 . 对于节点3，将自身的**P2P节点连接列表**内容清空，重启节点3；

```
# 在node2目录下执行
$ ./stop.sh
$ ./start.sh
nohup: appending output to ‘nohup.out’
```

2 . 对于节点1和2，将节点3从自身的**P2P节点连接列表**中移除（如有），重启节点1和2；

3 . 确认节点3与节点1（和2）的原有连接已经断开，退出网络操作完成。

```eval_rst
.. note::
    - **节点3需先退出群组再退出网络，退出顺序由用户保证，系统不再作校验**；
    - 网络连接由节点主动发起，如缺少第2步，节点3仍可感知节点1和节点2发起的P2P连接请求，并建立连接，可使用CA黑名单避免这种情况。
    - 若启用了白名单，需将退出节点的从所有节点的config.ini的白名单配置中删除，并正确的将新的白名单配置刷入节点中。参考《CA黑白名单》。
```

#### A节点加入群组

场景描述：

群组Group3原有节点1和节点2，两节点轮流出块，现在将节点3加入群组。

操作顺序：

1. 节点3加入网络；
2. 使用控制台addSealer根据节点3的nodeID设置节点3为**共识节点**；
3. 使用控制台getSealerList查询group3的共识节点中是否包含节点3的nodeID，如存在，加入群组操作完成。

```eval_rst
.. note::
    - 节点3的NodeID可以使用`cat nodes/127.0.0.1/node2/conf/node.nodeid`获取；
    - 节点3首次启动会将配置的群组节点初始列表内容写入群组节点系统表，区块同步结束后，**群组各节点的群组节点系统表均一致**；
    - **节点3需先完成网络准入后，再执行加入群组的操作，系统将校验操作顺序**；
    - **节点3的群组固定配置文件需与节点1和2的一致**。
```

#### A节点退出群组

场景描述：

群组Group3原有节点1、节点2和节点3，三节点轮流出块，现在将节点3退出群组。

操作顺序：

1. 使用控制台removeNode根据节点3的NodeID设置节点3为**游离节点**；
2. 使用控制台getSealerList查询group3的共识节点中是否包含节点3的nodeID，如已消失，退出群组操作完成。

补充说明：

```eval_rst
.. note::
    - 节点3可以共识节点或观察节点的身份执行退出操作。
```
