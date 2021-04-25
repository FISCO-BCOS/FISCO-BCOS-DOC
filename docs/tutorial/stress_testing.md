# 压力测试指南

标签：``压力测试`` ``Java SDK Demo`` ``Caliper``

----

## 1. 通过Java SDK进行压力测试

Java SDK Demo是基于Java SDK的基准测试集合，能够对FISCO BCOS节点进行压力测试。Java SDK Demo提供有合约编译功能，能够将Solidity合约文件转换成Java合约文件，此外还提供了针对转账合约、CRUD合约以及AMOP功能的压力测试示例程序。

### 第一步. 安装JDK

  Java SDK Demo中的测试程序能够在部署有JDK 1.8 ~ JDK 14的环境中运行，执行测试程序前请先确保已安装所需版本的JDK。以在Ubuntu系统中安装OpenJDK 11为例：

  ```bash
  # 安装open JDK 11
  $ sudo apt install openjdk-11-jdk
  # 验证Java版本
  $ java --version
  # 输出以下内容：
  # openjdk 11.0.10 2021-01-19
  # OpenJDK Runtime Environment (build 11.0.10+9-Ubuntu-0ubuntu1.20.04)
  # OpenJDK 64-Bit Server VM (build 11.0.10+9-Ubuntu-0ubuntu1.20.04, mixed mode, sharing)
  ```

### 第二步. 编译源码

  ```bash
  # 下载源码
  $ git clone https://github.com/FISCO-BCOS/java-sdk-demo
  $ cd java-sdk-demo
  # 编译源码
  $ ./gradlew build 
  ```

  ```eval_rst
  .. note::

      当网络无法访问GitHub时，请从https://gitee.com/FISCO-BCOS/java-sdk-demo处下载源码。
  ```

### 第三步. 配置Demo

  使用Java SDK Demo之前，需要首先要Java SDK，包括证书拷贝以及端口配置，详细请参考[这里](../sdk/java_sdk/quick_start.html)

  ```bash
  # 拷贝证书(假设SDK证书位于~/fisco/nodes/127.0.0.1/sdk目录，请根据实际情况更改路径)
  $ cp -r ~/fisco/nodes/127.0.0.1/sdk/* conf

  # 拷贝配置文件
  # 注:
  #   默认搭建的FISCO BCOS区块链系统Channel端口是20200，若修改了该端口，请同步修改config.toml中的[network.peers]配置选项
  $ cp conf/config-example.toml conf/config.toml
  ```

### 第四步. 执行示例压力测试程序

  Java SDK Demo提供了一系列压测程序，包括串行转账合约压测、并行转账合约压测、AMOP压测等，具体使用方法如下：

  ```bash
  # 进入dist目录
  $ cd dist

  # 将需要转换为java代码的sol文件拷贝到dist/contracts/solidity路径下
  # 转换sol, 其中${packageName}是生成的java代码包路径
  # 生成的java代码位于 /dist/contracts/sdk/java目录下
  $ java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.codegen.DemoSolcToJava ${packageName}

  # 压测串行转账合约:
  # count: 压测的交易总量
  # tps: 压测QPS
  # groupId: 压测的群组ID
  java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceOk [count] [tps] [groupId]

  # 压测并行转账合约
  # --------------------------
  # 基于Solidity并行合约parallelok添加账户:
  # groupID: 压测的群组ID
  # count: 压测的交易总量
  # tps: 压测QPS
  # file: 保存生成账户的文件名
  $ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [parallelok] [groupID] [add] [count] [tps] [file]
  # 基于Precompiled并行合约precompiled添加账户
  # (参数含义同上)
  java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [add] [count] [tps] [file]
  # --------------------------
  # 基于Solidity并行合约parallelok发起转账交易压测
  # groupID: 压测的群组ID
  # count: 压测的交易总量
  # tps: 压测的QPS
  # file: 转账用户文件
  $ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [parallelok] [groupID] [transfer] [count] [tps] [file]
  # 基于Precompiled并行合约Precompiled发起转账压测
  $ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [transfer] [count] [tps] [file]


  # CRUD合约压测
  # 压测CRUD insert
  # count: 压测的交易总量
  # tps: 压测QPS
  # groupId: 压测群组
  $ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceTable [insert] [count] [tps] [groupId]
  # 压测CRUD update
  # (参数解释同上)
  $ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceTable [update] [count] [tps] [groupId]
  # 压测CRUD remove
  # (参数解释同上)
  $ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceTable [remove] [count] [tps] [groupId]
  # 压测CRUD query
  # (参数解释同上)
  $ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceTable [query] [count] [tps] [groupId]
  ```

## 2. 通过Caliper进行压力测试程序

```eval_rst
.. note::

    目前FISCO BCOS适配的Caliper版本为0.2.0，请在部署Caliper运行环境时确保Caliper的版本为0.2.0，如在部署或使用过程中遇到任何问题，请优先参考 `https://github.com/FISCO-BCOS/FISCO-BCOS/issues/1248 <https://github.com/FISCO-BCOS/FISCO-BCOS/issues/1248>`_ 中的解决方案进行排查。
    
```

## 2.1. 环境要求

### 第一步. 配置基本环境

- 部署Caliper的计算机需要有外网权限；
- 操作系统版本需要满足以下要求：Ubuntu >= 16.04、CentOS >= 7或MacOS >= 10.14；
- 部署Caliper的计算机需要安装有以下软件：python 2.7、make、g++、gcc及git。

### 第二步. 安装NodeJS

- 版本要求：

    NodeJS 8 (LTS), 9, 或 10 (LTS)，Caliper尚未在更高的NodeJS版本中进行过验证。

- 安装指南：

    建议使用nvm(Node Version Manager)安装，nvm的安装方式如下：

    ```bash
    # 安装nvm
    curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.2/install.sh | bash

    # 若出现因网络问题导致长时间下载失败，可尝试以下命令
    curl -o- https://gitee.com/mirrors/nvm/raw/v0.33.2/install.sh | bash

    # 加载nvm配置
    source ~/.$(basename $SHELL)rc
    # 安装Node.js 8
    nvm install 8
    # 使用Node.js 8
    nvm use 8
    ```

### 第三步. 部署Docker

- 版本要求：>= 18.06.01

- 安装指南：

    CentOS：

    ```bash
    # 添加源
    sudo yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
    # 更新缓存
    sudo yum makecache fast
    # 安装社区版Docker
    sudo yum -y install docker-ce
    # 将当前用户加入docker用户组（重要）
    sudo groupadd docker
    sudo gpasswd -a ${USER} docker
    # 重启Docker服务
    sudo service docker restart
    newgrp - docker
    # 验证Docker是否已经启动
    sudo systemctl status docker
    ```

    Ubuntu

    ```bash

    # 更新包索引
    sudo apt-get update
    # 安装基础依赖库
    sudo apt-get install \
        apt-transport-https \
        ca-certificates \
        curl \
        gnupg-agent \
        software-properties-common
    # 添加Docker官方GPG key
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    # 添加docker仓库
    sudo add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) \
    stable"
    # 更新包索引
    sudo apt-get update
    # 安装Docker
    sudo apt-get install docker-ce docker-ce-cli containerd.io
    ```

    MacOs下

    请安装[Docker Desktop](https://www.docker.com/products/docker-desktop)。

- 加入Docker用户组

    CentOS

    ```bash
    sudo groupadd docker
    sudo gpasswd -a ${USER} docker

    # 重启Docker服务
    sudo service docker restart
    # 验证Docker是否已经启动
    sudo systemctl status docker
    ```

    Ubuntu

    ```bash
    sudo groupadd docker
    sudo usermod -aG docker $USER
    ```

### 第四步. 安装Docker Compose

- 版本要求：>= 1.22.0
- 安装指南：

    ```bash
    sudo curl -L "https://github.com/docker/compose/releases/download/1.24.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    ```

## 2.2. Caliper部署

### 第一步. 部署

Caliper提供了方便易用的命令行界面工具`caliper-cli`，推荐在本地进行局部安装：

1. 建立一个工作目录

```bash
mkdir benchmarks && cd benchmarks
```

2. 对NPM项目进行初始化

```bash
npm init
```

这一步主要是为在工作目录下创建package.json文件以方便后续依赖项的安装，如果不需要填写项目信息的话可以直接执行`npm init -y`。

3. 安装`caliper-cli`

```bash
npm install --only=prod @hyperledger/caliper-cli@0.2.0
```

由于Caliper所有依赖项的安装较为耗时，因此使用`--only=prod`选项用于指定NPM只安装Caliper的核心组件，而不安装其他的依赖项（如各个区块链平台针对Caliper的适配器）。在部署完成后，可以通过`caliper-cli`显式绑定需要测试的区块链平台及相应的适配器。

4. 验证`caliper-cli`安装成功

```bash
npx caliper --version
```

若安装成功，则会打印相应的版本信息，如：

```bash
user@ubuntu:~/benchmarks$ npx caliper --version
v0.2.0
```

### 第二步. 绑定

由于Caliper采用了轻量级的部署方式，因此需要显式的绑定步骤指定要测试的平台及适配器版本，`caliper-cli`会自动进行相应依赖项的安装。使用`npx caliper bind`命令进行绑定，命令所需的各项参数可以通过如下命令查看：

```bash
user@ubuntu:~/benchmarks$ npx caliper bind --help
Usage:
  caliper bind --caliper-bind-sut fabric --caliper-bind-sdk 1.4.1 --caliper-bind-cwd ./ --caliper-bind-args="-g"

Options:
  --help               Show help  [boolean]
  -v, --version        Show version number  [boolean]
  --caliper-bind-sut   The name of the platform to bind to  [string]
  --caliper-bind-sdk   Version of the platform SDK to bind to  [string]
  --caliper-bind-cwd   The working directory for performing the SDK install  [string]
  --caliper-bind-args  Additional arguments to pass to "npm install". Use the "=" notation when setting this parameter  [string]
```

其中，

**--caliper-bind-sut** ：用于指定需要测试的区块链平台，即受测系统（***S***ystem ***u***nder ***T***est）；
**--caliper-bind-sdk**：用于指定适配器版本；
**--caliper-bind-cwd**：用于绑定`caliper-cli`的工作目录，`caliper-cli`在加载配置文件等场合时均是使用相对于工作目录的相对路径；
**caliper-bind-args**：用于指定`caliper-cli`在安装依赖项时传递给`npm`的参数，如用于全局安装的`-g`。

对于FISCO BCOS，可以采用如下方式进行绑定：

```bash
npx caliper bind --caliper-bind-sut fisco-bcos --caliper-bind-sdk latest
```

由于FISCO BCOS对于caliper 0.2.0版本的适配存在部分不兼容情况，需要手动按照(https://github.com/FISCO-BCOS/FISCO-BCOS/issues/1248)中的步骤修改代码后方可正常运行。

### 第三步. 快速体验FISCO BCOS基准测试

为方便测试人员快速上手，FISCO BCOS已经为Caliper提供了一组预定义的测试样例，测试对象涵盖HelloWorld合约、Solidity版转账合约及预编译版转账合约。同时在测试样例中，Caliper测试脚本会使用docker在本地自动部署及运行4个互连的节点组成的链，因此测试人员无需手工搭链及编写测试用例便可直接运行这些测试样例。

**在工作目录下下载预定义测试用例**

```bash
git clone https://github.com/vita-dounai/caliper-benchmarks.git
```

**注意** 若出现网络问题导致的长时间拉取代码失败，则尝试以下方式:

```bash
# 拉取gitee代码
git clone https://gitee.com/mirrors_hyperledger/caliper-benchmarks.git
```

**执行HelloWorld合约测试**

```bash
npx caliper benchmark run --caliper-workspace caliper-benchmarks --caliper-benchconfig benchmarks/samples/fisco-bcos/helloworld/config.yaml  --caliper-networkconfig networks/fisco-bcos/4nodes1group/fisco-bcos.json
```

**执行Solidity版转账合约测试**

```bash
npx caliper benchmark run --caliper-workspace caliper-benchmarks --caliper-benchconfig benchmarks/samples/fisco-bcos/transfer/solidity/config.yaml  --caliper-networkconfig networks/fisco-bcos/4nodes1group/fisco-bcos.json
```

**执行预编译版转账合约测试**

```bash
npx caliper benchmark run --caliper-workspace caliper-benchmarks --caliper-benchconfig benchmarks/samples/fisco-bcos/transfer/precompiled/config.yaml  --caliper-networkconfig networks/fisco-bcos/4nodes1group/fisco-bcos.json
```

测试完成后，会在命令行界面中展示测试结果（TPS、延迟等）及资源消耗情况，同时会在`caliper-benchmarks`目录下生成一份包含上述内容的可视化HTML报告。

`caliper benchmark run`所需的各项参数可以通过如下命令查看：

```bash
user@ubuntu:~/benchmarks$ npx caliper benchmark run --help
caliper benchmark run --caliper-workspace ~/myCaliperProject --caliper-benchconfig my-app-test-config.yaml --caliper-networkconfig my-sut-config.yaml

Options:
  --help                   Show help  [boolean]
  -v, --version            Show version number  [boolean]
  --caliper-benchconfig    Path to the benchmark workload file that describes the test client(s), test rounds and monitor.  [string]
  --caliper-networkconfig  Path to the blockchain configuration file that contains information required to interact with the SUT  [string]
  --caliper-workspace      Workspace directory that contains all configuration information  [string]
```

其中，

**--caliper-workspace**：用于指定`caliper-cli`的工作目录，如果没有绑定工作目录，可以通过该选项动态指定工作目录；
**--caliper-benchconfig**：用于指定测试配置文件，测试配置文件中包含测试的具体参数，如交易的发送方式、发送速率控制器类型、性能监视器类型等；
**--caliper-networkconfig**：用于指定网络配置文件，网络配置文件中会指定Caliper与受测系统的连接方式及要部署测试的合约等。

## 2.3. 自定义测试用例

本节将会以测试HelloWorld合约为例，介绍如何使用Caliper测试自定义的测试用例。

Caliper前后端分离的设计原则使得只要后端的区块链系统开放了相关网络端口，Caliper便可以对该系统进行测试。结合Docker提供的性能数据统计服务或本地的`ps`命令工具，Caliper能够在测试的同时收集节点所在机器上的各种性能数据，包括CPU、内存、网络及磁盘的使用等。尽管Caliper能工作在不使用Docker模式而是使用原生二进制ficos-bcos可执行程序搭建出的链上，但是那样Caliper将无法获知节点所在机器上的资源消耗。因此，在目前的Caliper版本下（v0.2.0），我们推荐使用Docker模式搭链。

### 配置Docker Daemon及部署FISCO BCOS网络

如果只想基于已经搭建好的链进行测试，可以跳过本小节。

#### 配置Docker Daemon

为方便Caliper统一管理节点容器及监控性能数据，在运行节点的服务器上首先需要开启Docker Daemon服务。

开始之前，先停止docker进程：

```bash
sudo service docker stop
```

创建`/etc/docker/daemon.json`文件（如果已经存在则修改），加入以下内容：

```JSON
{
  "hosts" : ["unix:///var/run/docker.sock", "tcp://0.0.0.0:2375"]
}
```

**"unix:///var/run/docker.sock"**：UNIX套接字，本地客户端将通过这个来连接Docker Daemon；
**tcp://0.0.0.0:2375**，TCP套接字，表示允许任何远程客户端通过2375端口连接Docker Daemon.

使用`sudo systemctl edit docker`新建或修改`/etc/systemd/system/docker.service.d/override.conf`，其内容如下：

```ini
##Add this to the file for the docker daemon to use different ExecStart parameters (more things can be added here)
[Service]
ExecStart=
ExecStart=/usr/bin/dockerd
```

默认情况下使用`systemd`时，`docker.service`的设置为：`ExecStart=/usr/bin/dockerd -H fd://`，这将覆写`daemon.json`中的任何hosts。通过`override.conf`文件将ExecStart定义为：`ExecStart=/usr/bin/dockerd`，就能使`daemon.json`中设置的hosts生效。`override.conf`中的第一行`ExecStart=`必须要有，这一行将用于清除默认的ExecStart参数。

重新加载daemon并重启docker服务：

```bash
sudo systemctl daemon-reload
sudo systemctl restart docker.service
```

检查端口监听：

```bash
sudo netstat -anp | grep 2375
```

如果出现以下字样则表明配置成功：

```bash
tcp6       0      0 :::2375                 :::*                    LISTEN      79018/dockerd
```

此时能够在另一台机器上通过远程连接访问本机的Docker Daemon服务，例如：

```bash
# 假设开启Docker Daemon服务的机器IP地址为192.168.1.1
docker -H 192.168.1.1:2375 images
```

#### 建链

使用[开发部署工具 build_chain.sh](../manual/build_chain.html)脚本快速建链。本节以4个节点、全连接的形式搭链，但本节所述的测试方法能够推广任意数量节点及任意网络拓扑形式的链。

创建生成节点的配置文件（如一个名为`ipconf`的文件），文件内容如下：

```bash
192.168.1.1:1 agency1 1
192.168.1.2:1 agency1 1
192.168.1.3:1 agency1 1
192.168.1.4:1 agency1 1
```

生成链中节点的配置文件：

```bash
bash build_chain.sh -f ipconf -i -p 30914,20914,8914
```

将产生的节点配置文件夹分别拷贝至对应的服务器上：

```bash
scp -r 192.168.1.1/node0/ app@192.168.1.1:/data/test
scp -r 192.168.1.2/node0/ app@192.168.1.2:/data/test
scp -r 192.168.1.3/node0/ app@192.168.1.3:/data/test
scp -r 192.168.1.4/node0/ app@192.168.1.3:/data/test
```

#### 配置FISCO BCOS适配器

在另外一台机器上部署Caliper，部署教程见第二节。

#### 网络配置

新建一个名为`4nodes1group`的目录，本阶示例中的FISCO BCOS适配器的网络配置文件均会放置于此。新建一个名为`fisco-bcos.json`的配置文件，文件内容如下：

```JSON
{
    "caliper": {
        "blockchain": "fisco-bcos",
        "command": {
            "start": "sh network/fisco-bcos/4nodes1group/start.sh",
            "end": "sh network/fisco-bcos/4nodes1group/end.sh"
        }
    },
    "fisco-bcos": {
        "config": {
            "privateKey": "bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd",
            "account": "0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3"
        },
        "network": {
            "nodes": [
                {
                    "ip": "192.168.1.1",
                    "rpcPort": "8914",
                    "channelPort": "20914"
                },
                {
                    "ip": "192.168.1.2",
                    "rpcPort": "8914",
                    "channelPort": "20914"
                },
                {
                    "ip": "192.168.1.3",
                    "rpcPort": "8914",
                    "channelPort": "20914"
                },
           ],
            "authentication": {
                "key": "packages/caliper-samples/network/fisco-bcos/4nodes1group/sdk/node.key",
                "cert": "packages/caliper-samples/network/fisco-bcos/4nodes1group/sdk/node.crt",
                "ca": "packages/caliper-samples/network/fisco-bcos/4nodes1group/sdk/ca.crt"
            },
            "groupID": 1,
            "timeout": 600000
        },
        "smartContracts": [
            {
                "id": "helloworld",
                "path": "src/contract/fisco-bcos/helloworld/HelloWorld.sol",
                "language": "solidity",
                "version": "v0"
            }
        ]
    },
    "info": {
        "Version": "2.0.0",
        "Size": "4 Nodes",
        "Distribution": "Remote Host"
    }
}
```

配置文件中每一项的具体含义如下：

- **caliper.command.start**

启动Caliper时会首先执行start配置中指定的命令，主要用于初始化SUT。本文示例中使用Docker模式启动，启动Caliper时首先执行当前目录下的`start.sh`文件，其具体内容是：
```
docker -H 192.168.1.1:2375 run -d --rm --name node0 -v /data/test/node0/:/data -p 8914:8914 -p 20914:20914 -p 30914:30914 -w=/data fiscoorg/fiscobcos:latest -c config.ini 1> /dev/null
docker -H 192.168.1.2:2375 run -d --rm --name node1 -v /data/test/node0/:/data -p 8914:8914 -p 20914:20914 -p 30914:30914 -w=/data fiscoorg/fiscobcos:latest -c config.ini 1> /dev/null
docker -H 192.168.1.3:2375 run -d --rm --name node2 -v /data/test/node0/:/data -p 8914:8914 -p 20914:20914 -p 30914:30914 -w=/data fiscoorg/fiscobcos:latest -c config.ini 1> /dev/null
docker -H 192.168.1.4:2375 run -d --rm --name node3 -v /data/test/node0/:/data -p 8914:8914 -p 20914:20914 -p 30914:30914 -w=/data fiscoorg/fiscobcos:latest -c config.ini 1> /dev/null
```

即启动远程的Docker容器。如果不需要在Caliper启动时执行命令，需要将该配置项置空。

- **caliper.command.end**

Caliper在退出流程的最后会执行end配置指定的命令，主要用于清理环境。本例中在测试结束时会执行当前目录下的`end.sh`文件，其具体内容是：

```bash
docker -H 192.168.1.1:2375 stop $(docker -H 192.168.1.1:2375 ps -a | grep node0 | cut -d " " -f 1) 1> /dev/null && echo -e "\033[32mremote container node0 stopped\033[0m"
docker -H 192.168.1.2:2375 stop $(docker -H 192.168.1.2:2375 ps -a | grep node1 | cut -d " " -f 1) 1> /dev/null && echo -e "\033[32mremote container node1 stopped\033[0m"
docker -H 192.168.1.3:2375 stop $(docker -H 192.168.1.3:2375 ps -a | grep node2 | cut -d " " -f 1) 1> /dev/null && echo -e "\033[32mremote container node2 stopped\033[0m"
docker -H 192.168.1.4:2375 stop $(docker -H 192.168.1.3:2375 ps -a | grep node3 | cut -d " " -f 1) 1> /dev/null && echo -e "\033[32mremote container node3 stopped\033[0m"
```

即停止并删除有所的远程容器。如果不需要在Caliper退出时执行命令，需要将该配置项置空。

- **network.nodes**

一个包含了所有要连接节点的列表，列表中每一项需要指明被连接节点的IP地址、RPC端口及Channel端口号，所有端口号需要和节点的配置文件保持一致。

- **network.authentication**

适配器向节点的Channel端口发起请求时需要使用CA根证书等文件，这些文件已在3.1.2节中调用`build_chain.sh`脚本时已经生成好，使用任一节点配置下的sdk文件夹中的相应文件即可，需要在该配置中写上所有文件的路径（使用相对路径时需要以`caliper-cli`工作目录为起始目录）。

- **network.smartContracts**

指定要测试的合约，Caliper会在启动时想后端区块链系统部署合约。目前FISCO BCOS适配器支持通过`language`字段指定两种类型的合约——Solidity合约和预编译合约，当测试合约为Solidity合约时，`language`字段需要指定为`solidity`，当测试合约为预编译合约时，`language`字段需要指定为`precompiled`。当测试合约为预编译合约时，需要在`address`字段中指定预编译合约的地址，否则需要在`path`字段中指定Solidity合约的路径。

#### 测试配置

测试配置用于指定测试的具体运行方式。测试配置是一个YAML文件，HelloWorld合约的测试配置文件内容如下所示：

```YAML
---
test:
  name: Hello World
  description: This is a helloworld benchmark of FISCO BCOS for caliper
  clients:
    type: local
    number: 1
  rounds:
  - label: get
    description: Test performance of getting name
    txNumber:
    - 10000
    rateControl:
    - type: fixed-rate
      opts:
        tps: 1000
    callback: benchmarks/samples/fisco-bcos/helloworld/get.js
  - label: set
    description: Test performance of setting name
    txNumber:
    - 10000
    rateControl:
    - type: fixed-rate
      opts:
        tps: 1000
    callback: benchmarks/samples/fisco-bcos/helloworld/set.js
monitor:
  type:
  - docker
  docker:
    name:
    - http://192.168.1.1:2375/node0
    - http://192.168.1.2:2375/node1
    - http://192.168.1.3:2375/node2
    - http://192.168.1.4:2375/node3
  interval: 0.1
```

测试文件中主要包括两部分：

- 测试内容配置

`test`项负责对测试内容进行配置。配置主要集中在`round`字段中指定如何对区块链系统进行测试。每一个测试可以包含多轮，每一轮可以向区块链发起不同的测试请求。具体要HelloWorld合约测试，测试中包含两轮，分别对合约的`get`接口和`set`接口进行测试。在每一轮测试中，可以通过`txNumber`或`txDuration`字段指定测试的交易发送数量或执行时间，并通过`rateControl`字段指定交易发送时的速率控制器，在本节的示例中，使用了QPS为1000的匀速控制器，更多速率控制器的介绍可以参考[官方文档](https://hyperledger.github.io/caliper/v0.2/getting-started/)。

- 性能监视器配置

`monitor`项负责对测试所使用的性能监视器的进行配置。每项配置项的解释如下：

1. monitor.type，需要指定为`docker`，指对docker容器进行监控；
2. monitor.docker.name，一个包含所有要监视的节点的docker容器名称列表，名字必须以`http://`开头，其后跟随"{节点的IP}:{节点docker daemon端口}/{docker容器的名称}"；
3. monitor.interval，监视器的采样间隔，单位为秒。

如果是在**本地**搭好的链，则可以添加本地性能监视器，相应地监视器的配置更改如下：

```YAML
monitor:
  type:
  - process
  process:
    - command: node0
      multiOutput: avg
    - command: node1
      multiOutput: avg
    - command: node2
      multiOutput: avg
    - command: node3
      multiOutput: avg
  interval: 0.1
```

其中每项配置项的解释如下：
1. monitor.type，需要指定为`process`，只对进程进行监控；
2. monitor.process，一个包含所有要监视的进称列表，其中每个进程的command属性为一个正则表达式，表示进程名称；每个进程还可以有一个arguments属性（未在上述示例中使用到），表示进程的参数。Caliper会先使用ps命令搜索commad + arguments,然后匹配以得到目标的进程的进程ID及系统资源的使用情况。每个进程的multiOutput属性用于指定结果的输出方式，目前支持平均值（avg）及总和（sum）两种方式；
3. monitor.interval，监视器的采样间隔，单位为秒。

需要注意的是，进程监控目前暂不支持监控进程对网络和磁盘的使用情况。
