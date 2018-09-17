# 附录
## 构建docker运行环境
### 说明
使用物料包同样可以构建基于docker节点的区块链的搭建, 使用方式与构建普通节点的区块链比较类似, 区别在于最终启动的docker节点。
### 配置
```
[docker]
* 当前是否构建docker节点的安装包. 0：否    1：是
docker_toggle=1
* docker仓库地址
docker_repository=fiscoorg/fisco-octo
* docker版本号.
docker_version=v1.3.x-latest
```
- docker_toggle   
  
构建docker节点环境时, docker_toggle设置为1。

- docker_repository  
  
docker镜像仓库, 用户一般不需要修改。

- docker_version  
  
docker版本号, 使用默认值即可, 版本更新时, 本地可以自动更新至最新版本。

### 配置节点信息

同样以在三台服务器上部署区块链为例：

服务器ip  ： 172.20.245.42 172.20.245.43 172.20.245.44  
机构分别为： agent_0   agent_1    agent_2  
节点数目  ： 每台服务器搭建两个节点
修改[nodes] section字段为：

```
[nodes]
node0=172.20.245.42  0.0.0.0  2  agent_0
node1=172.20.245.43  0.0.0.0  2  agent_1
node2=172.20.245.44  0.0.0.0  2  agent_2
```

### 构建安装包

执行成功会输出``` Building end!  ``` 并生成build目录
```
build/
├── 172.20.245.42_agent_0_genesis
├── 172.20.245.43_agent_1
├── 172.20.245.44_agent_2
├── stderr.log
└── temp
```

将安装包上传到对应的服务器。

### 安装
进入安装目录, 执行``` ./install_node ```, 成功之后会生成 **docker** 目录
```
docker/
├── node0
├── node1
├── register0.sh
├── register1.sh
├── start0.sh
├── start1.sh
├── start.sh
├── stop0.sh
├── stop1.sh
├── stop.sh
├── unregister0.sh
└── unregister1.sh
```

- nodeIDX : 第IDX个节点的目录, 该目录会被映射到docker的/fisco-bcos/node/目录, 这两个目录中的内容是一致的
- nodeIDX/log : 日志目录
- start.sh 启动所有的docker节点
- stop.sh 停止所有的docker节点
- startIDX.sh 启动第IDX个docker节点
- stopIDX.sh 停止第IDX个节点
- registerIDX.sh 扩容时使用, 将第IDX个节点注册入节点管理合约, 调用的是docker中的node_manager.sh脚本, 扩容时使用
- unregisterIDX.sh 将IDX个节点从节点管理合约删除, 调用的是node_manager.sh脚本

### 启动
在build目录执行start.sh脚本  

**注意:要先启动创世块节点所在的服务器上的节点!!!**
```
 ./start.sh 
start node0 ...
705b6c0e380029019a26e954e72da3748e29cec95a508bc1a8365abcfc36b86c
start node1 ...
be8bd964322a08a70f22be9ba15082dbe50d1729f955291586a0503e32d2225f
```

### 验证

*  docker ps
  
通过docker ps命令查看docker节点是否启动正常
```
docker ps  -f name="fisco*"
CONTAINER ID        IMAGE                               COMMAND                  CREATED             STATUS              PORTS               NAMES
be8bd964322a        fiscoorg/fisco-octo:v1.3.x-latest   "/fisco-bcos/start_n…"   21 minutes ago      Up 21 minutes                           fisco-node1_8546
705b6c0e3800        fiscoorg/fisco-octo:v1.3.x-latest   "/fisco-bcos/start_n…"   22 minutes ago      Up 22 minutes                           fisco-node0_8545
```
启动的两个docker节点的容器id分别为: be8bd964322a、705b6c0e3800, docker节点的STATUS状态为Up, 说明节点正常启动。

*  日志验证
```
tail -f node0/log/log_*.log | egrep "seal"
INFO|2018-08-13 11:52:38:534|PBFTClient.cpp:343|+++++++++++++++++++++++++++ Generating seal onec61bbf7cb6152d523f391dfe65dd1f858ec3daa7b6df697308a0ad5219cf232#1tx:0,maxtx:1000,tq.num=0time:1534161158534
INFO|2018-08-13 11:52:40:550|PBFTClient.cpp:343|+++++++++++++++++++++++++++ Generating seal on127962f94ccb075a448ae741e69718ffc0bee4f97ccddb7bd5e8a0310f4b8980#1tx:0,maxtx:1000,tq.num=0time:1534161160550
INFO|2018-08-13 11:52:42:564|PBFTClient.cpp:343|+++++++++++++++++++++++++++ Generating seal on29ccca512d7e2bac34760e8c17807896dac914b426884a0bc28499a556811467#1tx:0,maxtx:1000,tq.num=0time:1534161162564
```
说明节点周期性共识, 出块。

*  进入docker容器部署合约。
  
docker运行之后, docker镜像内部是一个完整的fisco-bcos的运行环境, 包括js的工具都是可用的, 可以进入docker镜像内部进行合约的部署。

以be8bd964322a为例
```
$ sudo docker exec -it be8bd964322a /bin/bash
```

*  加载环境变量

执行 ``` source /etc/profile ```加载docker内部的一些环境变量。

*  目录
```
$ cd /fisco-bcos
```

目录结构如下：
```
/fisco-bcos/
├── node
│   ├── config.json
│   ├── fisco-data   
│   ├── genesis.json
│   ├── log
│   └── start.sh
├── tool
├── systemcontract
├── web3sdk
├── nodejs
├── web3lib
└── nodejs
```
- /fisco-bcos/node             : 节点目录
- /fisco-bcos/node/fisco-data  : 数据目录
- /fisco-bcos/node/log         : 日志目录
- /fisco-bcos/systemcontract   : nodejs系统合约工具
- /fisc-bcos/tool              : nodejs工具 
- /fisco-bcos/web3lib          : nodejs基础库 
- /fisco-bcos/web3sdk          : web3sdk环境

*  部署合约

进入tool目录, 部署合约
```
$ cd /fisco-bcos/tool
$ babel-node deploy.js HelloWorld     
RPC=http://0.0.0.0:8546
Ouputpath=./output/
deploy.js  ........................Start........................
Soc File :HelloWorld
HelloWorldcomplie success！
send transaction success: 0x726e328e5b53ddb3ce040424304ffd61e9ae277d6441068c45ad590003c7426a
HelloWorldcontract address 0x4437f8c9cd1e6a3e8ec9c3460c4bc209acdca052
HelloWorld deploy success!
cns add operation => cns_name = HelloWorld
         cns_name =>HelloWorld
         contract =>HelloWorld
         version  =>
         address  =>0x4437f8c9cd1e6a3e8ec9c3460c4bc209acdca052
         abi      =>[{"constant":false,"inputs":[{"name":"n","type":"string"}],"name":"set","outputs":[],"payable":false,"type":"function"},{"constant":true,"inputs":[],"name":"get","outputs":[{"name":"","type":"string"}],"payable":false,"type":"function"},{"inputs":[],"payable":false,"type":"constructor"}]
send transaction success: 0xa280d823346e1b7ea332a2b4d7a7277ae380b0cc7372bef396c5205fa74b25ae
```

### 扩容

与普通节点的扩容流程类似, 大体来说, 构建扩容安装包, 启动节点, 将扩容的节点入网(加入节点管理合约)。

*  构建扩容安装包

参考 [扩容流程](#expand_blockchain) 构建扩容机器上的安装包。

*  安装启动

将扩容服务器上传对应的服务器, 进入安装目录, 执行``` ./install_node ```

执行成功之后生成**docker**目录

启动docker节点
```
$ cd docker
$ ./start.sh
```

*  节点入网
  
docker目录下``` registerIDX.sh ```脚本为注册脚本。
```
$ ./register0.sh
===================================================================
=====INIT ECDSA KEYPAIR From private key===
node.json=file:/fisco-bcos/node/fisco-data/node.json
$ ./register1.sh
===================================================================
=====INIT ECDSA KEYPAIR From private key===
node.json=file:/fisco-bcos/node/fisco-data/node.json
```

*  验证

查看 log
```
 tail -f node0/log/log*.log | egrep "seal"
INFO|2018-08-13 12:00:18:710|PBFTClient.cpp:343|+++++++++++++++++++++++++++ Generating seal on26c826ad8d275cd2c3c53a034818acce222ad7dc8ef455de64efbf193748c9ef#1tx:0,maxtx:1000,tq.num=0time:1534161618710
INFO|2018-08-13 13:00:02:040|PBFTClient.cpp:343|+++++++++++++++++++++++++++ Generating seal onb7a72e68cbc43293dac635b3c868e83ecbe24c3f1b72e0d57a809ee72bad9ca5#4tx:0,maxtx:0,tq.num=0time:1534165202040
INFO|2018-08-13 13:54:25:054|PBFTClient.cpp:343|+++++++++++++++++++++++++++ Generating seal one2d93621481bf613b065f254519bbc32689d3b2eb8c5a1680c0f4d57531f7ef5#5tx:0,maxtx:0,tq.num=0time:1534168465054
```

## 私钥证书管理
- 物料包使用FISCO BCOS的工具分配证书, 工具位于下载的FSICO-BCOS目录的cert子目录, 使用方式参考[FISCO-BCOS 证书生成工具](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/doc/manual#第二章-准备链环境)。
- 构建完成各个服务器的安装包之后, 整条链的根证书、机构证书会保存在创世节点所在服务器的dependencies/cert目录 保存的目录结构为： 
```
cert目录  
    ca.crt 
    ca.key
    机构名称子目录
        agency.crt
        agency.key
```

- 以上面示例的配置为例,  创世节点服务器cert目录内容(详见使用指南中证书说明)：
```
ca.crt
ca.key
agent_0\
    agency.crt
    agency.key
agent_1\
    agency.crt
    agency.key
agent_2\
    agency.crt
    agency.key
```
- ca.crt 链证书
- ca.key 链证书私钥
- agent_0\agency.crt agent_0机构证书 
- agent_0\agency.key agent_0机构证书私钥
- agent_1\agency.crt agent_1机构证书 
- agent_1\agency.key agent_1机构证书私钥
- agent_2\agency.crt agent_2机构证书 
- agent_2\agency.key agent_2机构证书私钥