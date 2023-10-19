# 7. 使用docker部署区块链

标签：``使用docker搭建区块链`` ``区块链教程`` ``docker``

----

[build_chain.sh](../manual/build_chain.md)脚本提供`-d`参数，支持使用docker方式部署区块链。本章将演示如何以docker方式搭建四节点的区块链，通过示例帮助用户熟悉docker搭建区块链的流程。

```eval_rst
.. note::
    - 目前只支持在Linux环境通过docker方式部署区块链环境
```

## 1. 安装依赖

**安装curl、openssl:**

```bash
# ubuntu
sudo apt install -y curl openssl
# centos
sudo yum install -y curl openssl openssl-devel
```

**安装docker:**

参考docker官方文档: [https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/)

## 2. 下载安装脚本
```bash
## 创建操作目录
cd ~ && mkdir -p fisco && cd fisco

## 下载脚本
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.5.0/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 `curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.5.0/build_chain.sh && chmod u+x build_chain.sh`
```

## 3. 搭建单群组4节点区块链
在fisco目录下执行下面的指令，生成一条单群组4节点的区块链。
请确保机器的`30300~30303，20200~20203`端口没有被占用，也可以通过`-p`参数指定其他端口。

```bash
bash build_chain.sh -D -l 127.0.0.1:4 -p 30300,20200
```

```eval_rst
.. note::
    - build_chain.sh 各个参数的使用，参考 `这里 <../operation_and_maintenance/build_chain.html>`_
```

命令执行成功会输出`All completed`。如果执行出错，请检查`nodes/build.log`文件中的错误信息。

```bash
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/sdk cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node0/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node1/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node2/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node3/conf cert successful!
[INFO] Downloading get_account.sh from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh...
######################################################################## 100.0%
[INFO] Admin account: 0x7096372ddefadc3e70057e907f6ab6cf468838ac
[INFO] Generate uuid success: e4640f4f-89ed-47fa-9693-695e96988ec2
[INFO] Generate uuid success: dd2c5bc8-66ab-4f2f-9949-2bb3ca465d18
[INFO] Generate uuid success: f503a549-3dbc-47ee-a9ea-709a9e3f1f03
[INFO] Generate uuid success: 3b46ac36-f944-49bb-ba25-d81b0a3d579b
==============================================================
[INFO] GroupID              : group0
[INFO] ChainID              : chain0
[INFO] docker mode      : true
[INFO] docker tag       : v3.5.0
[INFO] Auth mode            : false
[INFO] Start port           : 30300 20200
[INFO] Server IP            : 127.0.0.1:4
[INFO] SM model             : false
[INFO] enable HSM           : false
[INFO] Output dir           : ./nodes
[INFO] All completed. Files in ./nodes
```

## 4. 启动区块链

执行`nodes/127.0.0.1/start_all.sh`

启动时，会查找本地是否存在FISCO-BCOS对应版本的节点镜像，不存在则从docker hub下载。

```shell
$ bash nodes/127.0.0.1/start_all.sh
try to start node0
try to start node1
try to start node2
try to start node3
Unable to find image 'fiscoorg/fiscobcos:v3.5.0' locally
Unable to find image 'fiscoorg/fiscobcos:v3.5.0' locally
Unable to find image 'fiscoorg/fiscobcos:v3.5.0' locally
Unable to find image 'fiscoorg/fiscobcos:v3.5.0' locally
v3.5.0: Pulling from fiscoorg/fiscobcos
v3.5.0: Pulling from fiscoorg/fiscobcos
v3.5.0: Pulling from fiscoorg/fiscobcos
v3.5.0: Pulling from fiscoorg/fiscobcos
Already exists a1778b69356: Already exists 

dd98998da8ef: Pulling fs layer 
82158e46ca02: Pulling fs layer 

82158e46ca02: Pulling fs layer 
Extracting [==================================================>]   12.3MB/12.3MB 12.3MB/12.3MB
dd98998da8ef: Pulling fs layer 
Extracting [==================================================>]  29.51MB/29.51MB9.51MB/29.51MB
82158e46ca02: 82158e46ca02: Pull complete 
Pull complete 82158e46ca02: Pull complete 
Extracting [==================================================>]  29.51MB/29.51MB27.2MB/29.51MB
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Digest: sha256:ddd677f51458a0ae07458f02bcb4c8cdd876b9323d889dad0c92a5f44244f2f7
Status: Image is up to date for fiscoorg/fiscobcos:v3.5.0
Status: Downloaded newer image for fiscoorg/fiscobcos:v3.5.0
Status: Image is up to date for fiscoorg/fiscobcos:v3.5.0
Status: Downloaded newer image for fiscoorg/fiscobcos:v3.5.0
74342b325faed4cb5913cc80f18dee210ce7757f5a696e26c0a04d665f87b9ce
a846dc34e23b32a5e5d7ee8f465f01e8d231734cf80bd6fd1ca92b2e8d3b9e9c
efae6adb1ebe71b2b81237d51d61d9142736927a28fe91411ecc793a382e6998
de8b704d51a23888d3a129c081bd1e32d61da4af4029415bf7379feef75c0dee
 node0 start successfully pid=74342b325fae
 node1 start successfully pid=a846dc34e23b
 node2 start successfully pid=efae6adb1ebe
 node3 start successfully pid=de8b704d51a2                           
```

## 5. 检查容器

检查容器状态是否正常，命令如下:

```shell
$ docker ps -a | egrep fiscobcos

74342b325fae   fiscoorg/fiscobcos:v3.5.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node0
efae6adb1ebe   fiscoorg/fiscobcos:v3.5.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node2
a846dc34e23b   fiscoorg/fiscobcos:v3.5.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node1
de8b704d51a2   fiscoorg/fiscobcos:v3.5.0    "/usr/local/bin/fisc…"   47 seconds ago   Up 45 seconds                        roottestnodes127.0.0.1node3
```
容器状态为`UP`时，说明节点正常启动。

docker的相关内容，可以参考docker文档进行了解: [https://docs.docker.com/](https://docs.docker.com/)

## 6. 查看节点

可以通过检查日志来确认节点的p2p连接数目、共识是否正常。

- 查看节点node0连接的节点数

```bash
tail -f nodes/127.0.0.1/node0/log/* |grep -i "heartBeat,connected count"
```

正常情况会不停地输出连接信息，从输出可以看出node0与另外3个节点有连接。
```bash
info|2023-06-15 12:28:47.014473|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:28:57.014577|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:29:07.014641|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:29:17.014742|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2023-06-15 12:29:27.014823|[P2PService][Service][METRIC]heartBeat,connected count=3
```

到此docker环境已经部署完成。