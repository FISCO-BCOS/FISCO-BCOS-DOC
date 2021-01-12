# 扩容一个新节点

标签：``节点管理`` ``扩容群组`` ``游离节点`` ``新增节点`` ``共识节点``

----

FISCO BCOS引入了[游离节点、观察者节点和共识节点](../design/security_control/node_management.html#id6)，这三种节点类型可通过控制台相互转换。

- 共识节点：参与共识的节点，拥有群组的所有数据（搭链时默认都生成共识节点）。
- 观察者节点：不参与共识，但能实时同步链上数据的节点。
- 游离节点：已启动，待等待加入群组的节点。处在一种暂时的节点状态，不能获取链上的数据。

将指定节点分别转换成共识节点、观察者节点、游离节点，相关操作命令如下：

- [addSealer：根据节点NodeID设置对应节点为共识节点](../console/console_of_java_sdk.html#addsealer)
- [addObserver：根据节点NodeID设置对应节点为观察节点](../console/console_of_java_sdk.html#addobserver)
- [removeNode：根据节点NodeID设置对应节点为游离节点](../console/console_of_java_sdk.html#removenode)
- [getSealerList：查看群组中共识节点列表](../console/console_of_java_sdk.html#getsealerlist)
- [getObserverList：查看群组中观察节点列表](../console/console_of_java_sdk.html#getobserverlist)
- [getNodeIDList：查看节点已连接的所有其他节点的NodeID](../console/console_of_java_sdk.html#getnodeidlist)


下面结合具体操作案例详细阐述群组如何扩容一个新节点。扩容操作分两个阶段， 分别为**为节点生成证书并启动**、**将节点加入群组**。

本节假设用户已经参照[搭建第一个区块链网络](../installation.md)搭建了一条4节点的联盟链，接下来的操作将生成一个新的节点，然后将节点加入群组1。

如果是使用运维部署工具，请参考[这里进行扩容操作](../enterprise_tools/tutorial_one_click.html#id11)。

## 1. 为节点生成证书并启动

每个节点都需要有一套证书来与链上的其他节点建立连接，扩容一个新节点，首先需要为其签发证书。

### 为新节点生成私钥证书

接下来的操作都在`nodes/127.0.0.1`目录下进行

1. 获取证书生成脚本

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_node_cert.sh`
```

2. 生成新节点私钥证书

```bash
# -c指定机构证书及私钥所在路径
# -o输出到指定文件夹，其中node4/conf中会存在机构agency新签发的证书和私钥
# 成功会输出 All completed 提示
bash gen_node_cert.sh -c ../cert/agency -o node4
```

国密版本请执行下面的指令生成证书。
```bash
bash gen_node_cert.sh -c ../cert/agency -o node4 -g ../gmcert/agency/
```

### 准备节点配置文件

1. 拷贝`node0/config.ini`、`node0/start.sh`和`node0/stop.sh`到node4目录;

```
cp node0/config.ini node0/start.sh node0/stop.sh node4/
```

2. 修改`node4/config.ini`。对于`[rpc]`模块，修改`channel_listen_port=20204`和`jsonrpc_listen_port=8549`；对于`[p2p]`模块，修改`listen_port=30304`并在`node.`中增加自身节点信息；

```
$ vim node4/config.ini
[rpc]
    ;rpc listen ip
    listen_ip=127.0.0.1
    ;channelserver listen port
    channel_listen_port=20204
    ;jsonrpc listen port
    jsonrpc_listen_port=8549
[p2p]
    ;p2p listen ip
    listen_ip=0.0.0.0
    ;p2p listen port
    listen_port=30304
    ;nodes to connect
    node.0=127.0.0.1:30300
    node.1=127.0.0.1:30301
    node.2=127.0.0.1:30302
    node.3=127.0.0.1:30303
    node.4=127.0.0.1:30304
```

3. 节点3拷贝节点1的`node0/conf/group.1.genesis`（内含**群组节点初始列表**）和`node0/conf/group.1.ini`到`node4/conf`目录下，不需改动；
```
cp node0/conf/group.1.genesis node0/conf/group.1.ini node4/conf/
```

4. 执行`node4/start.sh`启动节点；
```
bash node4/start.sh
```

5. 确认node4与其他节点连接已经建立，加入网络操作完成。

```bash
tail -f node4/log/log*  | grep "connected count"
```

```
# 以下日志表明节点node4与其他4个节点建立了连接
info|2020-12-22 20:44:36.113611|[P2P][Service] heartBeat,connected count=4
info|2020-12-22 20:44:46.117942|[P2P][Service] heartBeat,connected count=4
info|2020-12-22 20:44:56.120799|[P2P][Service] heartBeat,connected count=4
```

## 2. 节点加入群组

### 获取node4的nodeid

```bash
cat node4/conf/node.nodeid
```

得到类似下面的字符串就是nodeid，nodeid是节点公钥的16进制表示，国密请执行`cat node4/conf/gmnode.nodeid`

```bash
94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
```

### 使用控制台将node4加入群组1

1. 使用addObserver将node4作为观察节点加入群组1

```bash
[group:1]> getObserverList
[]

[group:1]> addObserver 94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
{
    "code":1,
    "msg":"Success"
}

[group:1]> getObserverList
[
    94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
]
```

2. 使用addSealer将node4作为共识节点加入群组1

```bash
[group:1]> getSealerList
[
    6c41f7e138051a13a220cb186e934398e37700295ff355b87f113704996b3e03750100e16653cda18b5f954d3b7b08d068ca4a9d65cec5a40db980b697ffb699,
    7404cdf7f34f038aba90059ff25dc5f05f538010c55e98976aea6bc954910f34f15a255869751c8fe564bdb0fa1eee8e2db47eeca0fdd1359beaac6adcd37ede,
    a7b856e5b59072c809ea963fa45ede72f7d37561affff989fbede6cd61a40137e2146db205434788e61b89a57f08c614cd283e5e915c23714c2fa685237e8bdb,
    e5ea1e18717418a57f115bf1cea5168250f86e5b77f74dd15d0c4bf3758ca37002059ba2e54131296d1646a62be5faf85e243dac8d33d452acd63e20428b72ed
]

[group:1]> addSealer 94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
{
    "code":1,
    "msg":"Success"
}

[group:1]> getSealerList
[
    6c41f7e138051a13a220cb186e934398e37700295ff355b87f113704996b3e03750100e16653cda18b5f954d3b7b08d068ca4a9d65cec5a40db980b697ffb699,
    7404cdf7f34f038aba90059ff25dc5f05f538010c55e98976aea6bc954910f34f15a255869751c8fe564bdb0fa1eee8e2db47eeca0fdd1359beaac6adcd37ede,
    a7b856e5b59072c809ea963fa45ede72f7d37561affff989fbede6cd61a40137e2146db205434788e61b89a57f08c614cd283e5e915c23714c2fa685237e8bdb,
    e5ea1e18717418a57f115bf1cea5168250f86e5b77f74dd15d0c4bf3758ca37002059ba2e54131296d1646a62be5faf85e243dac8d33d452acd63e20428b72ed,
    94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
]
```

更多操作请参考[节点管理](../manual/node_management.md)
