# 部署区块链
* 本章节会通过一个示例说明如何使用物料包工具, 也会介绍使用物料包构建好的环境中比较重要的一些目录
* 如果你希望快速搭建fisco bcos测试环境，请转至部署区块链sample
##  下载物料包  


```shell
$ git clone https://github.com/FISCO-BCOS/fisco-package-build-tool.git
```
目录结构以及主要配置文件作用： 
```
fisco-package-build-tool
├── Changelog.md                       更新记录       
├── config.ini                         配置文件
├── doc                                附加文档
├── ext                                拓展目录
├── generate_installation_packages.sh  启动shell文件
├── installation_dependencies          依赖目录
├── LICENSE                            license文件
├── README.md                          使用手册
└── release_note.txt                   版本号
```

##  配置

```shell
$ cd fisco-package-build-tool
$ vim config.ini
```

配置文件config.ini
```
[common]
* 物料包拉取FISCO-BCOS源码的github地址, 用户一般不需要修改.
github_url=https://github.com/FISCO-BCOS/FISCO-BCOS.git
* 物料包拉取FISCO-BCOS源码之后, 会将源码保存在本地的目录, 保存的目录名称为FISCO-BCOS, 默认拉取的代码会放入物料包同级的目录.
fisco_bcos_src_local=../
* 需要使用FISCO-BCOS的版本号, 使用物料包时需要将该值改为需要使用FISCO-BCOS的版本号.
* 版本号可以是FISCO-BCOS已经发布的版本之一, 链接： https://github.com/FISCO-BCOS/FISCO-BCOS/releases
fisco_bcos_version=v1.3.2

[docker]
* docker开关, 打开时构建的FISCO BCOS链节点为docker节点 . 0:关闭  1:打开
docker_toggle=0
* docker仓库地址.
docker_repository=fiscoorg/fisco-octo
* docker镜像版本号, 使用时修改为需要的版本号.
docker_version=v1.3.1

* 生成web3sdk证书时使用的keystore与clientcert的密码.
* 也是生成的web3sdk配置文件applicationContext.xml中keystorePassWord与clientCertPassWord的值, 强烈建议用户修改这两个值.
[web3sdk]
keystore_pwd=123456
clientcert_pwd=123456

[other]
* CA拓展模式, 目前不使用
ca_ext=0

* 扩容使用的一些参数
[expand]
* 扩容依赖的文件的目录,具体使用参考扩容流程
genesis_follow_dir=/follow/

* 端口配置, 一般不用做修改, 使用默认值即可, 但是要注意不要端口冲突.
* 每个节点需要占用三个端口:p2p port、rpc port、channel port, 对于单台服务器上的节点端口使用规则, 默认配置的端口开始, 依次增长。
[ports]
* p2p端口
p2p_port=30303
* rpc端口
rpc_port=8545
* channel端口
channel_port=8821

* 节点信息
[nodes]
* 格式为 : nodeIDX=p2p_ip listen_ip num agent
* IDX为索引, 从0开始增加.
* p2p_ip     => 服务器上用于p2p通信的网段的ip.
* listen_ip  => 服务器上的监听端口, 用来接收rpc、channel的链接请求, 建议默认值为"0.0.0.0".
* num        => 在服务器上需要启动的节点的数目.
* agent      => 机构名称, 若是不关心机构信息, 值可以随意, 但是不可以为空.
node0=127.0.0.1  0.0.0.0  4  agent
``` 

###  <a name="configuration" id="configuration">配置详解</a>    

* [common] section
```
[common]
* 物料包拉取FISCO-BCOS源码的github地址.
github_url=https://github.com/FISCO-BCOS/FISCO-BCOS.git
* 物料包拉取FISCO-BCOS源码之后, 会将源码保存在本地的目录, 保存的目录名称为FISCO-BCOS.
fisco_bcos_src_local=../
* 需要使用FISCO-BCOS的版本号, 使用物料包时需要将该值改为需要使用的版本号.
* 版本号可以是FISCO-BCOS已经发布的版本之一, 链接： https://github.com/FISCO-BCOS/FISCO-BCOS/releases
fisco_bcos_version=v1.3.2
```

- 物料包在构建安装包过程中(非扩容流程), 会启动一个默认的临时temp节点用来部署进行系统合约的部署, 将所有的节点注册到节点管理合约, 然后导出导出系统合约信息生成genesis.json文件。
- 在扩容流程中, 需要手动将扩容节点注册到节点管理合约, 参考后面的扩容流程。
- 每个节点启动时需要占用三个端口: p2p、rpc、channel. 对于启动的临时节点temp节点, 使用就是配置的nodes section中的p2p_port、rpc_port、channel_port配置端口, 要确认端口没有被占用。
- 对于FISCO BCOS节点, 每台服务器上第一个节点使用nodes section配置的p2p_port、rpc_port、channel_port端口, 后续的节点端口依次进行端口递增。
- 工具构建安装包过程中会涉及到从github上面拉取FISCO BCOS、编译FISCO BCOS流程, 规则： 
 
a、检查/usr/local/bin是否已经存在已经编译的fisco-bcos。 

b、若存在则查看fisco-bcos的版本号与配置fisco_bcos_version字段的版本号是否一致, 一致则说明fisco-bcos是需要的版本, 该流程退出。

c、判断配置文件中fisco_bcos_src_local配置的路径是否存在名为FISCO-BCOS的文件夹, 存在说明FISCO-BCOS源码已经存在, 否则开始从github上面拉取FISCO BCOS源码, 接下来进入FISCO-BCOS目录切换tag到自己需要的分支, 然后进行编译、安装流程, 成功则fisco-bcos会安装在/usr/local/bin目录下。 

* [docker] section

与docker节点搭链相关的配置, 参考附录。

* [web3sdk] section

与私钥跟证书文件的加解密相关。生成web3sdk证书时使用的keystore与clientcert的密码, 也是生成的web3sdk配置文件applicationContext.xml中keystorePassWord与clientCertPassWord的值, **建议用户修改这两个值**。

*  [expand] section
  
扩容操作, 使用参考 ( [扩容流程](#expand_blockchain) )


*  [nodes] section
```
需要部署FISCO BCOS服务器上的节点配置信息。
[nodes]
* 格式为 : nodeIDX=p2p_ip listen_ip num agent
* IDX为索引, 从0开始增加
* p2p_ip     => 服务器上用于p2p通信的网段的ip
* listen_ip  => 服务器上的监听端口, 用来接收rpc、channel的链接请求, 建议默认值为"0.0.0.0"
* num        => 在服务器上需要启动的节点的数目
* agent      => 机构名称, 若是不关心机构信息, 值可以随意, 但是不可以为空
```

*  [ports] section  
```
[ports]
* p2p端口
p2p_port=30303
* rpc端口
rpc_port=8545
* channel端口
channel_port=8821
```
fisco-bcos的每个节点需要使用3个端口,p2pport、rpcport、channelport,  [ports]配置的端口是服务器上面的第一个节点使用的端口,其他节点依次递增
```
node0=127.0.0.1 0.0.0.0 4 agent
````
上面的配置说明要启动四个节点, 按照默认的配置：
- 第1个节点的端口：p2p 30303、rpc 8545、channel 8821   
- 第2个节点的端口：p2p 30304、rpc 8546、channel 8822  
- 第3个节点的端口：p2p 30305、rpc 8547、channel 8823  
- 第4个节点的端口：p2p 30306、rpc 8548、channel 8824 


下面以在三台服务器上部署区块链为例构建一条新链： 
```
服务器ip  ： 172.20.245.42 172.20.245.43 172.20.245.44  
机构分别为： agent_0   agent_1    agent_2  
节点数目  ： 每台服务器搭建两个节点
```

修改[nodes] section字段为：
```
[nodes]
node0=172.20.245.42  0.0.0.0  2  agent_0
node1=172.20.245.43  0.0.0.0  2  agent_1
node2=172.20.245.44  0.0.0.0  2  agent_2
```

##  创建安装包

```
$ ./generate_installation_packages.sh build
```
执行成功之后会生成build目录, 目录下有生成的对应服务器的安装包：
```
build/
├── 172.20.245.42_agent_0_genesis  //172.20.245.42服务器的安装包
├── 172.20.245.43_agent_1          //172.20.245.43服务器的安装包
├── 172.20.245.44_agent_2          //172.20.245.44服务器的安装包
├── stderr.log      //标准错误的重定向文件
└── temp            //temp节点安装目录, 用户不用关心不用关心
```

* 其中带有**genesis**字样的为创世节点所在服务器的安装包。 

## 上传  
* 将安装包上传到对应的服务器, 注意上传的安装包必须与服务器相对应, 否则部署过程会出错。
* 一定要确认各个服务器之间的网络可连通, p2p网段的端口网络策略已经放开。

## 安装  
进入安装目录, 执行
```sh
$ ./install_node.sh 
```
正确执行在当前目录会多一个build目录, 目录结构如下：
```

build
├── check.sh          #检查脚本, 可以检查节点是否启动
├── fisco-bcos        #fisco-bcos二进制程序
├── node0             #节点0的目录
├── node1             #节点1的目录
├── nodejs            #nodejs相关安装目录
├── node_manager.sh   #节点管理脚本
├── node.sh           #nodejs相关环境变量
├── register.sh       #注册节点入网脚本, 扩容使用
├── start.sh          #启动脚本
├── stop.sh           #停止脚本
├── systemcontract    #nodejs系统合约工具
├── tool              #nodejs工具
├── unregister.sh     #从节点管理合约删除某个节点
├── web3lib           #nodejs基础库
└── web3sdk           #web3sdk环境
```

说明:
- nodeIDX
- 
节点IDX的目录, 示例中每台服务器启动两个节点, 所以有node0, node1两个目录

nodeIDX的目录结构如下：
```
build/node0/
├── check.sh     #检查当前节点是否启动
├── config.json  #配置文件
├── data         #数据目录
├── genesis.json #创世块文件
├── keystore
├── log          #log日志目录
├── log.conf     #log配置文件
├── start.sh     #启动当前节点
└── stop.sh      #停止当前节点
```

- node.sh

记录nodejs相关依赖的环境变量  

- start.sh

节点启动脚本, 使用方式：
```
start.sh       启动所有的节点
或者
start.sh IDX   启动指定的节点, IDX为节点的索引, 从0开始, 比如: start.sh 0表示启动第0个节点
```
- stop.sh
节点停止脚本, 使用方式：
```
stop.sh       停止所有的节点
或者
stop.sh IDX   停止指定的节点, IDX为节点的索引, 从0开始, 比如: stop.sh 0表示停止第0个节点
```
- register.sh
注册指定节点信息到节点管理合约, 扩容时使用
```
register.sh IDX
```
- unregister.sh
将指定节点从节点管理合约中删除
```
unregister.sh IDX
```
- node_manager.sh
查看当前节点管理合约中的节点信息
```
./node_manager.sh all
```
[[web3sdk使用说明链接]](https://github.com/FISCO-BCOS/web3sdk)  

[[web3lib、systemcontract、tool目录作用参考用户手册]](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/doc/manual)

## 启动节点

在build目录执行start.sh脚本.  
**注意:要先启动创世块节点所在的服务器上的节点!!!**

```sh
$ ./start.sh 
start all node ... 
start node0 ...
start node1 ...
check all node status ... 
node is running.
node is running.
```

## 验证

- **一定要所有服务器上的节点正常启动之后.**

### 日志

```shell
tail -f node0/log/log_*.log  | egrep "Generating seal"
INFO|2018-08-03 14:16:42:588|+++++++++++++++++++++++++++ Generating seal on8e5add00c337398ac5e9058432037aa646c20fb0d1d0fb7ddb4c6092c9d654fe#1tx:0,maxtx:1000,tq.num=0time:1522736202588
INFO|2018-08-03 14:16:43:595|+++++++++++++++++++++++++++ Generating seal ona98781aaa737b483c0eb24e845d7f352a943b9a5de77491c0cb6fd212c2fa7a4#1tx:0,maxtx:1000,tq.num=0time:1522736203595
```
可看到周期性的出现上面的日志，表示节点间在周期性的进行共识，整个链正常。

### 部署合约

每个服务器执行install_node时, 都会在安装目录下安装nodejs、babel-node、ethconsole, 其环境变量会写入当前安装用户的.bashrc文件，使用这些工具之前需要使环境变量生效，有两种使环境变量生效的方式，选择其中一种即可：

* 方式1：退出当前登录, 重新登录一次
* 方式2：执行node.sh脚本中的内容, 首先cat node.sh, 将显示的内容执行一遍
```
$ cat node.sh 
export NODE_HOME=/root/octo/fisco-bcos/build/nodejs* export PATH=$PATH:$NODE_HOME/bin* export NODE_PATH=$NODE_HOME/lib/node_modules:$NODE_HOME/lib/node_modules/ethereum-console/node_modules;
$ export NODE_HOME=/root/octo/fisco-bcos/build/nodejs* export PATH=$PATH:$NODE_HOME/bin* export NODE_PATH=$NODE_HOME/lib/node_modules:$NODE_HOME/lib/node_modules/ethereum-console/node_modules;
```

部署合约验证, 进入tool目录： 
```
$ cd tool
$ babel-node deploy.js HelloWorld
RPC=http://127.0.0.1:8546
Ouputpath=./output/
deploy.js  ........................Start........................
Soc File :HelloWorld
HelloWorldcomplie success！
send transaction success: 0xfb6237b0dab940e697e0d3a4d25dcbfd68a8e164e0897651fe4da6a83d180ccd
HelloWorldcontract address 0x61dba250334e0fd5804c71e7cbe79eabecef8abe
HelloWorld deploy success!
cns add operation => cns_name = HelloWorld
         cns_name =>HelloWorld
         contract =>HelloWorld
         version  =>
         address  =>0x61dba250334e0fd5804c71e7cbe79eabecef8abe
         abi      =>[{"constant":false,"inputs":[{"name":"n","type":"string"}],"name":"set","outputs":[],"payable":false,"type":"function"},{"constant":true,"inputs":[],"name":"get","outputs":[{"name":"","type":"string"}],"payable":false,"type":"function"},{"inputs":[],"payable":false,"type":"constructor"}]
send transaction success: 0x769e4ea7742b451e33cbb0d2a7d3126af8f277a52137624b3d4ae41681d58687
```
合约部署成功。

## 问题排查
参考附录FAQ。
