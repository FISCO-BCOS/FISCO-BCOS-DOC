# 控制台使用

## 控制台指令区分大小写吗

区分大小写，命令是完全匹配，但是可以采用`tab`补全命令。

## 加入共识列表或观察者列表报错，nodeID is not in network

节点加入共识列表和观察者列表的节点必须是连接peer的nodeID列表里面的成员。

## 删除节点操作报错，nodeID is not in group peers，为什么

节点删除操作中的节点必须是getGroupPeers里面展示的group的peers。

## 游离节点（非群组节点）是否可以同步group数据

游离节点不参与group内的共识、同步和出块，游离节点可以通过控制台`addSealer/addObserver`命令可以将退出的节点添加为共识/观察节点。

## 某节点属于不同的group，是否可以支持查询多group的信息

可以，在进入控制台时，输入要查看的groupID:  ./start [groupID]
