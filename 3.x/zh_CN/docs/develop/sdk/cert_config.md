# SDK连接证书配置

标签： ``SDK``、``证书配置``

----

在使用SDK开发应用时，与节点进行交互需要使用节点的证书文件。FISCO BCOS 3.x版本提供了三种节点的部署模式，每种部署模式下节点的SDK证书文件略有不同，本文以 [Java SDK](./java_sdk/index.md) 为例，分别介绍在三种节点模式下，SDK应用证书配置的正确方式。

## 单群组区块链（Air版本）部署模式

[单群组区块链（Air版本）](../../tutorial/air/index.md) 采用all-in-one的封装模式，将所有模块编译成一个二进制（进程），一个进程即为一个区块链节点。

Air版本的安装部署请参考：[链接](../../tutorial/air/build_chain.md) 。

`build_chain.sh`生成的节点配置中与SDK连接相关的配置主要为：

- **SDK连接证书**: 由`build_chain.sh`生成，客户端可拷贝该证书与节点建立SSL连接。

单机四节点Air版本**非国密区块链**的配置文件组织示例如下（已忽略与本文介绍无关的文件）：

```shell
nodes/
├── 127.0.0.1
│   │.....
│   ├── sdk # SDK证书
│   │   ├── ca.crt 		# SSL连接根证书
│   │   ├── cert.cnf 	# SSL证书配置
│   │   ├── sdk.crt 	# SSL连接证书
│   │   └── sdk.key 	# SSL证书私钥
│   ├── ......
```

单机四节点Air版本**国密区块链**的配置文件组织示例如下（已忽略与本文介绍无关的文件）：

```shell
nodes/
├── 127.0.0.1
│   │.....
│   ├── sdk # SDK证书
│   │   ├── sm_ca.crt 		# 国密SSL连接根证书
│   │   ├── sm_ensdk.crt 	# 国密SSL加密证书
│   │   ├── sm_ensdk.key	#	国密SSL加密证书私钥
│   │   ├── sm_sdk.crt		# 国密SSL连接签名证书
│   │   ├── sm_sdk.key		# 国密SSL连接签名证书私钥
│   │   └── sm_sdk.nodeid	# 国密节点ID
│   ├── ......
```

在使用Java SDK时，在项目已编译的`dist`目录下将节点SSL证书复制到`conf`目录下：

**注意：为了演示方便，这里的SDK应用路径默认为`~/fisco`中，在使用时请以实际路径为准。**

```shell
# 为了演示方便，在～/fisco目录下有Java SDK应用，并使用build_chain.sh建链脚本搭建了区块链节点
tree -L 1 ~/fisco
~/fisco
├── java-sdk-demo		# Java SDK应用
├── build_chain.sh 	# 建链脚本
└── nodes						# 节点目录

# 进入SDK应用目录
# SDK应用路径请以实际为准
cd ~/fisco/java-sdk-demo/

# 编译SDK应用
bash gradlew build

# 编译完成后，将会在项目根目录下生成dist文件夹
cd dist

# 将节点的证书全部拷贝进SDK配置文件夹中
cp -r ~/fisco/nodes/127.0.0.1/sdk/* ~/fisco/java-sdk-demo/dist/conf

# Done, 接下来配置SDK的使用配置文件即可，Have fun :)
```

## 多群组区块链（Pro版本）部署模式

[多群组区块链（Pro版本）](../../tutorial/pro/index.md) 包括RPC、Gateway接入层的服务和多个区块链节点Node服务组成，其中一个Node服务代表一个群组，存储采用本地RocksDB，所有Node共用接入层服务。

Pro版本的安装部署请参考：[链接](../../tutorial/pro/installation.md) 。

在完成[部署RPC服务](../../tutorial/pro/installation.html#rpc)之后，在`generated/rpc/chain`下将会生成所有需要用到的配置文件。其中，与SDK SSL连接的配置如下（已忽略与本文介绍无关的文件）：

```shell
tree generated/rpc/chain
generated/rpc/chain
├── 172.25.0.3 # 请以实际IP为准
│   ├── agencyABcosRpcService # 机构A的RPC服务目录
│   │   ├── sdk               # SDK证书目录，SDK客户端可从本目录拷贝证书连接RPC服务
│   │   │   ├── ca.crt				# SSL连接根证书
│   │   │   ├── cert.cnf			# SSL证书配置
│   │   │   ├── sdk.crt				# SSL连接证书
│   │   │   └── sdk.key				# SSL证书私钥
│   │   └── ssl               # RPC服务证书目录
│   └── agencyBBcosRpcService # 机构B的RPC服务配置目录
│       ├── config.ini.tmp    # 机构B的RPC服务的配置文件
│       ├── sdk               # SDK证书目录，SDK客户端从本目录拷贝证书连接RPC服务
│       │   ├── ca.crt
│       │   ├── cert.cnf
│       │   ├── sdk.crt
│       │   └── sdk.key
│       └── ssl              # RPC服务证书目录
└── ca                       # CA证书目录
```

在使用Java SDK时，在项目已编译的`dist`目录下将节点SSL证书复制到`conf`目录下：

**注意：为了演示方便，这里的SDK应用路径默认为`~/fisco`中，在使用时请以实际路径为准。**

```shell
# 为了演示方便，在～/fisco目录下有Java SDK应用，并使用build_chain.sh建链脚本搭建了区块链节点
tree -L 2 ~/fisco
~/fisco
├── java-sdk-demo		# Java SDK应用
└── BcosProBuilder
    ├── build_chain.py
    ├── conf
    ├── docker
    ├── generated		# 生成节点目录
    ├── requirements.txt
    └── src

# 进入SDK应用目录
# SDK应用路径请以实际为准
cd ~/fisco/java-sdk-demo/

# 编译SDK应用
bash gradlew build

# 编译完成后，将会在项目根目录下生成dist文件夹
cd dist

# 将节点的证书全部拷贝进SDK配置文件夹中
cp -r ~/fisco/BcosProBuilder/generated/rpc/chain/172.25.0.3/agencyABcosRpcService/sdk/* ~/fisco/java-sdk-demo/dist/conf

# Done, 接下来配置SDK的使用配置文件即可，Have fun :)
```

## 附录：确认区块链的密码学环境类型（非国密/国密）

在Air版本模式和Pro版本模式中，搭建完区块链节点之后均会生成节点的配置文件`config.ini`。从文件`config.ini`中即可判断当前区块链的密码箱环境类型是国密还是非国密。

由于SDK是直接与区块链节点的RPC模块直接连接的，因此在这里我们只需要关注RPC的配置即可：

```ini
[rpc]
    listen_ip=0.0.0.0
    listen_port=20200
    thread_count=4
    ; ssl是否使用国密模式连接
    sm_ssl=false
```

Air版本的RPC配置详情请参考链接：[配置RPC](../../tutorial/air/config.html#rpc)

Pro版本的RPC配置详情请参考链接：[rpc服务](../..//tutorial/pro/config.html#id9)

