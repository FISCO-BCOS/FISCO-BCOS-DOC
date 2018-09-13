# 扩容节点

- **扩容流程与搭链流程最本质的差别是, 初次搭链时会生成一个temp节点, 进行系统合约的部署, 然后会将所有构建的节点信息都注册入节点管理合约, 最后temp节点导出生成genesis.json文件. 所以搭链结束后, 每个节点信息都已经在节点管理合约, 但是在扩容时, 需要自己注册扩容的节点到节点管理合约。(参考FISCO-BCOS系统合约介绍[[节点管理合约]](https://github.com/FISCO-BCOS/Wiki/tree/master/FISCO-BCOS%E7%B3%BB%E7%BB%9F%E5%90%88%E7%BA%A6%E4%BB%8B%E7%BB%8D#%E8%8A%82%E7%82%B9%E7%AE%A1%E7%90%86%E5%90%88%E7%BA%A6))。**

## 使用场景  
对已经在运行的区块链, 可以提供其创世节点的相关文件, 创建出非创世节点, 使其可以连接到这条区块链。
## 获取扩容文件   
- 从创世节点所在的服务器获取dependencies/follow/文件夹。
  
该目录包含构建区块链时分配的链根证书、机构证书、创世块文件、系统合约。

将follow放入/fisco-bcos/目录。

## 配置

扩容的机器为: 172.20.245.45, 172.20.245.46 分别需要启动两个节点, 机构名称分别为agent_3、agent_4, 修改节点信息配置。
```sh
vim config.ini
```

修改扩容参数
```
* 扩容使用的一些参数
[expand]
genesis_follow_dir=/fisco-bcos/follow/
```

修改节点列表为扩容的节点信息。
```
[nodes]
node0=172.20.245.45  0.0.0.0  2  agent_3
node1=172.20.245.46  0.0.0.0  2  agent_4
```

## 扩容 
```
./generate_installation_packages.sh expand
```
成功之后会输出`Expanding end!`并在build目录生成对应安装包
```
build
├── 172.20.245.45_with_0.0.0.0_installation_package
├── 172.20.245.46_with_0.0.0.0_installation_package
└── stderr.log
```

## 安装启动  
将安装包分别上传至对应服务器, 分别在每台服务器上面执行下列命令：  
- 执行安装
```
./install_node.sh
```
- 启动节点
```
cd build
./start.sh
```

## 节点入网  

**在扩容时, 当前运行的链已经有数据, 当前新添加扩容的节点首先要进行数据同步, 建议新添加的节点在数据同步完成之后再将节点入网. 数据是否同步完成可以查看新添加节点的块高是否跟其他节点已经一致。**

以172.20.245.45这台服务器为例进行操作, 172.20.245.46操作类似：

**确保节点先启动。**   

用户在build目录下，使用register.sh脚本进行注册操作

注册
```
$ ./register.sh 0  # 注册第一个节点
$ ./register.sh 1  # 注册第二个节点
```

验证,注册的节点是否正常:
```
$ tail -f node0/log/log_*.log   | egrep "Generating seal"
INFO|2018-07-10 10:49:29:818|+++++++++++++++++++++++++++ Generating seal oncf8e56468bab78ae807b392a6f75e881075e5c5fc034cec207c1d1fe96ce79a1#4tx:0,maxtx:1000,tq.num=0time:1531190969818
INFO|2018-07-10 10:49:35:863|+++++++++++++++++++++++++++ Generating seal one23f1af0174daa4c4353d00266aa31a8fcb58d3e7fbc17915d95748a3a77c540#4tx:0,maxtx:1000,tq.num=0time:1531190975863
INFO|2018-07-10 10:49:41:914|+++++++++++++++++++++++++++ Generating seal on2448f00f295210c07b25090b70f0b610e3b8303fe0a6ec0f8939656c25309b2f#4tx:0,maxtx:1000,tq.num=0time:1531190981914
INFO|2018-07-10 1
```

## 问题排查
参考附录FAQ。