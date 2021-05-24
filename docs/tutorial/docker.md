# 使用docker部署区块链

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
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.7.2/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/build_chain.sh && chmod u+x build_chain.sh`
```

## 3. 搭建单群组4节点区块链
在fisco目录下执行下面的指令，生成一条单群组4节点的区块链。
请确保机器的`30300~30303，20200~20203，8545~8548`端口没有被占用，也可以通过`-p`参数指定其他端口。

```bash
bash build_chain.sh -d -l 127.0.0.1:4 -p 30300,20200,8545
```

```eval_rst
.. note::
    - build_chain.sh 各个参数的使用，参考 `这里 <../manual/build_chain.html>`_
```

命令执行成功会输出`All completed`。如果执行出错，请检查`nodes/build.log`文件中的错误信息。

```bash
==============================================================
Generating CA key...
==============================================================
Generating keys and certificates ...
Processing IP=127.0.0.1 Total=4 Agency=agency Groups=1
==============================================================
Generating configuration files ...
Processing IP=127.0.0.1 Total=4 Agency=agency Groups=1
==============================================================
[INFO] Docker tag      : latest
[INFO] Start Port      : 30300 20200 8545
[INFO] Server IP       : 127.0.0.1:4
[INFO] Output Dir      : /home/ubuntu/fisco/nodes
[INFO] CA Path         : /home/ubuntu/fisco/nodes/cert/
==============================================================
[INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/ubuntu/fisco/nodes/127.0.0.1/download_console.sh -f
==============================================================
[INFO] All completed. Files in /home/ubuntu/fisco/nodes
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
Unable to find image 'fiscoorg/fiscobcos:v2.7.2' locally    # 本地镜像不存在
Unable to find image 'fiscoorg/fiscobcos:v2.7.2' locally
Unable to find image 'fiscoorg/fiscobcos:v2.7.2' locally
Unable to find image 'fiscoorg/fiscobcos:v2.7.2' locally
v2.7.2: Pulling from fiscoorg/fiscobcos                     # 下载镜像
d519e2592276: Pulling fs layer
d22d2dfcfa9c: Pulling fs layer
b3afe92c540b: Pulling fs layer
d0e4ee611225: Waiting
af59874f6897: Waiting
v2.7.2: Pulling from fiscoorg/fiscobcos
d519e2592276: Pulling fs layer
d22d2dfcfa9c: Pulling fs layer
b3afe92c540b: Pulling fs layer
d0e4ee611225: Waiting
af59874f6897: Waiting
v2.7.2: Pulling from fiscoorg/fiscobcos
d519e2592276: Pulling fs layer
Pull complete d519e2592276: Pull complete
              Extracting     162B/162B
Pull complete d22d2dfcfa9c: Pull complete
af59874f6897: Extracting  24.48MB/26.71MB
Pull complete b3afe92c540b: Pull complete
d519e2592276: Pull complete
Pull complete d0e4ee611225: Pull complete
b3afe92c540b: Pull complete
Pull complete af59874f6897: Pull complete
af59874f6897: Pull complete
Digest: sha256:7a4e4f60bafe8362b402e0f86245c9a840544845fc57007259d2e4b023da6149
Status: Downloaded newer image for fiscoorg/fiscobcos:v2.7.2
Digest: sha256:7a4e4f60bafe8362b402e0f86245c9a840544845fc57007259d2e4b023da6149
Status: Image is up to date for fiscoorg/fiscobcos:v2.7.2
Digest: sha256:7a4e4f60bafe8362b402e0f86245c9a840544845fc57007259d2e4b023da6149
Status: Image is up to date for fiscoorg/fiscobcos:v2.7.2
Digest: sha256:7a4e4f60bafe8362b402e0f86245c9a840544845fc57007259d2e4b023da6149
Status: Image is up to date for fiscoorg/fiscobcos:v2.7.2
8df0d79bf764cd5693e00bbf5c91e2ed7ec69901049e79049082d7ab0da707c8   # 启动节点的 CONTAINER ID
c27dcaa0adfbf45820506f659913e6dacb8a148425801ea80ab078100a8d57b7   # 启动节点的 CONTAINER ID
e66fe7c68d3a8db5d8c18fe5749eba25fd41350ebbaa7accbc8374e03bfba690   # 启动节点的 CONTAINER ID
2bb7b89e2a46b9a4f92fbf9b48b39023540fccdae14de52e091fa17ea219905e   # 启动节点的 CONTAINER ID
 node0 start successfully                                           
 node2 start successfully                                           
 node3 start successfully                                           
 node1 start successfully                                           
```

## 5. 检查容器

检查容器状态是否正常，命令如下:
```shell
$ docker ps -a | egrep fiscobcos

8df0d79bf764   fiscoorg/fiscobcos:v2.7.2   "/usr/local/bin/fisc…"   5 minutes ago   Up 5 minutes             rootfisconodes127.0.0.1node2
2bb7b89e2a46   fiscoorg/fiscobcos:v2.7.2   "/usr/local/bin/fisc…"   5 minutes ago   Up 5 minutes             rootfisconodes127.0.0.1node1
e66fe7c68d3a   fiscoorg/fiscobcos:v2.7.2   "/usr/local/bin/fisc…"   5 minutes ago   Up 5 minutes             rootfisconodes127.0.0.1node0
c27dcaa0adfb   fiscoorg/fiscobcos:v2.7.2   "/usr/local/bin/fisc…"   5 minutes ago   Up 5 minutes             rootfisconodes127.0.0.1node3
```
容器状态为`UP`时，说明节点正常启动。

docker的相关内容，可以参考docker文档进行了解: [https://docs.docker.com/](https://docs.docker.com/)

## 6. 查看节点

可以通过检查日志来确认节点的p2p连接数目、共识是否正常。

- 查看节点node0连接的节点数

```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```

正常情况会不停地输出连接信息，从输出可以看出node0与另外3个节点有连接。
```bash
info|2021-03-10 16:20:18.316769| [P2P][Service] heartBeat,connected count=3
info|2021-03-10 16:20:28.316922| [P2P][Service] heartBeat,connected count=3
info|2021-03-10 16:20:38.317105| [P2P][Service] heartBeat,connected count=3
```

- 检查共识


```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```

正常情况会不停输出`++++Generating seal`，表示共识正常。
```bash
info|2021-03-10 17:17:16.989389|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=9af45b7d...
info|2021-03-10 17:17:21.003890|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=427aae1b...
info|2021-03-10 17:17:25.023183|[g:1][CONSENSUS][SEALER]++++++++++++++++ Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=a6c1a5a9...
```

到此docker环境已经部署完成。