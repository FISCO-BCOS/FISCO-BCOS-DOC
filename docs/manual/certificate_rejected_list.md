# CA黑名单操作手册

本文档描述CA黑名单的实践操作，建议阅读本操作文档前请先行了解[《CA黑名单介绍》](../design/security_control/certificate_rejected_list.md)。

CA黑名单的操作包括**一节点将他节点列入/移除CA黑名单**，通过修改配置文件重启实现。

## 修改范围

节点`config.ini`配置有`[crl]`路径（可选）。`[crl]`为节点NodeID列表，node.X为本节点拒绝连接的对方节点NodeID。

## 修改示例

网络中存在三个节点，均互联，节点相关信息为：

节点1的目录名为node0，IP端口为127.0.0.1:30400，nodeID前四个字节为b231b309...

节点2的目录名为node1，IP端口为127.0.0.1:30401，nodeID前四个字节为aab37e73...

节点3的目录名为node2，IP端口为127.0.0.1:30402，nodeID前四个字节为d6b01a96...

### A节点将B节点列入CA黑名单

场景描述：

节点1和节点2在群组同一群组中，与群组其余节点轮流出块，现在节点1将节点2加入自身黑名单。

操作顺序：

1. 对于节点1（node0），将节点2（node1）的公钥nodeID加入自身的**CA黑名单**；
```
$ cat node1/conf/node.nodeid 
aab37e73489bbd277aa848a99229ab70b6d6d4e1b81a715a22608a62f0f5d4270d7dd887394e78bd02d9f31b8d366ce4903481f50b1f44f0e4fda67149208943
$ vim node0/config.ini
;certificate rejected list
[crl]
    ;crl.0 should be nodeid, nodeid's length is 128 
    crl.0=aab37e73489bbd277aa848a99229ab70b6d6d4e1b81a715a22608a62f0f5d4270d7dd887394e78bd02d9f31b8d366ce4903481f50b1f44f0e4fda67149208943
```
2. 重启节点1；
```
# 在node1目录下执行
$ ./stop.sh
$ ./start.sh
nohup: appending output to ‘nohup.out’
```
3. 通过日志确认节点1与节点2的原有连接已经断开，加入黑名单操作完成。
```
# 在打开DEBUG级别日志前提下，查看自身节点（node2）连接的节点数及所连接的节点信息（nodeID）
# 以下日志表明节点node2与两个节点（节点的nodeID前4个字节为b231b309、aab37e73）建立了连接
$ tail -f node2/log/log*  | grep P2P
debug|2019-02-21 10:30:18.694258| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30400,nodeID=b231b309...
debug|2019-02-21 10:30:18.694277| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30401,nodeID=aab37e73...
info|2019-02-21 10:30:18.694294| [P2P][Service] heartBeat connected count,size=2
```

补充说明：

- 节点1添加节点2到自身CA黑名单的操作，将断开与节点2的网络连接及AMOP通信；
- 节点1添加节点2到自身CA黑名单的操作，将对节点1所在群组的**共识及同步消息/数据转发**。

### A节点将B节点移除CA黑名单

场景描述：

节点1的CA黑名单中有节点2的nodeID，节点2的CA黑名单中没有节点1的nodeID，现在节点1将节点2移除自身的CA黑名单。

操作顺序：

1. 对于节点1，将节点2的公钥NodeID从自身的**CA黑名单**移除；
2. 重启节点1；
3. 通过日志确认确认节点1与节点2重新建立连接，移除黑名单操作完成。

## FAQ

1、 节点A添加节点B为CA黑名单后，节点A所在的部分账本无法共识？

节点A添加节点B为CA黑名单并重启后，节点A和节点B将不建立连接，无法进行P2P通信，继而无法发送/接收共识消息。当节点A和B的共同群组中有效节点数不满足3f+1容错条件时，群组将无法共识。