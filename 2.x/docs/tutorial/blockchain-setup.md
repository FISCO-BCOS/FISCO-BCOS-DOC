# 搭建区块链

标签：`搭链`   `FISCO-BCOS`

------

本章介绍在ubuntu操作系统下搭建区块链，帮助开发者在单机环境快速搭建完整的FISCO BCOS区块链管理平台，适用于本地开发测试、快速体验和教学演示场景。

部署内容
通过本方案，您将自动获得以下组件：
区块链节点（FISCO BCOS 2.9.1，支持国密/非国密双模式）
WeBASE管理平台（可视化节点监控与合约管理）

### 实验环境

| 环境 |操作系统 |
|---|---|
| 版本 |Ubuntu 20.04.6 LTS |



#### 第一步.下载压缩包，安装依赖

[搭建区块链的压缩包](https://blog.csdn.net/2401_86836547/article/details/148213520?spm=1001.2014.3001.5502)下载压缩包到ubuntu


```bash
# 解压
unzip blcokchain.zip

# 创建操作目录，把解压的文件都放放到目录下
mv fisco-bcos fisco/
......
```


```bash
#  安装 openssl 和 curl依赖
sudo apt-get install openssl curl -y
```


#### 第二步.搭建单群组4节点联盟链 



给文件最大权限，修改文件的所有者和组
> blockchain:blockchain 这里的blockchain是你ubuntu的用户名
```bash
sudo chmod 777 fisco/build_chain.sh
sudo chmod 777 fisco/fisco-bcos
chmod 777 console.tar.gz
sudo chown blockchain:blockchain *
```


```bash
# 创建bin目录
mkdir bin/

# 在fisco目录下把fisco-bcos文件移动到bin目录
mv fisco-bcos bin/
```


```bash
# 编写node_list文件
gedit node_list
```
编写ip，节点数，机构名，组名，端口

```bash
# 编写node_list文件的内容
127.0.0.1:4 agency1 1,2 30300,20200,8545
```

 **生成fisco启动脚本** 

```bash
bash build_chain.sh -f node_list -e bin/fisco-bcos -o blockchain
```
[^搭建国密链请执行：bash build_chain.sh -f node_list -e bin/fisco-bcos -o guominodes -g -G]: 

命令执行成功会输出`All completed`信息如下，如果执行出错，请检查`nodes/build.log`文件中的错误信息

```bash
Checking fisco-bcos binary...
Binary check passed.
==============================================================
Generating CA key...
==============================================================
Generating keys and certificates ...
Processing IP=127.0.0.1 Total=4 Agency=agency1 Groups=1
==============================================================
Generating configuration files ...
Processing IP=127.0.0.1 Total=4 Agency=agency1 Groups=1
==============================================================
Group:1 has 4 nodes
==============================================================
[INFO] FISCO-BCOS Path : bin/fisco-bcos
[INFO] IP List File    : node_list
[INFO] Start Port      : 30300 20200 8545
[INFO] Server IP       : 127.0.0.1:4
[INFO] Output Dir      : /home/ashley/Desktop/fisco/blockchain
[INFO] CA Path         : /home/ashley/Desktop/fisco/blockchain/cert/
[INFO] RSA channel     : true
==============================================================
[INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/ashley/Desktop/fisco/blockchain/127.0.0.1/download_console.sh -f
==============================================================
[INFO] All completed. Files in /home/ashley/Desktop/fisco/blockchain
```



#### 第三步. 启动FISCO BCOS链



启动所有节点

```bash
cd blockchain/127.0.0.1
bash start_all.sh
```
[^如果启动失败请检查端口是否冲突]: 

启动成功会输出类似下面内容的响应。否则请使用`netstat -an | grep tcp`检查机器的`30300~30303，20200~20203，8545~8548`端口是否被占用。

```bash
try to start node0
try to start node1
try to start node2
try to start node3
 node0 start successfully
 node1 start successfully
 node3 start successfully
 node2 start successfully
```

####  

#### **第四步.检查进程** 

进程是否启动

```bash
ps -ef | grep -v grep | grep fisco-bcos
```
正常情况会有类似下面的输出； 如果进程数不为4，则进程没有启动（一般是端口被占用导致的）

```bash
ashley      7603    3755  1 07:45 pts/0    00:00:03 /home/ashley/Desktop/fisco/blockchain/127.0.0.1/node0/../fisco-bcos -c config.ini
ashley      7605    3755  1 07:45 pts/0    00:00:03 /home/ashley/Desktop/fisco/blockchain/127.0.0.1/node1/../fisco-bcos -c config.ini
ashley      7608    3755  1 07:45 pts/0    00:00:03 /home/ashley/Desktop/fisco/blockchain/127.0.0.1/node3/../fisco-bcos -c config.ini
ashley      7610    3755  1 07:45 pts/0    00:00:03 /home/ashley/Desktop/fisco/blockchain/127.0.0.1/node2/../fisco-bcos -c config.ini
```



####  

#### 第五步.检查节点连接情况

```bash
cd node0/log
ls     
cat log_2025052507.45.log | grep connected    #查看日志文件
```

正常情况会输出连接信息，从输出可以看出`node0`与另外3个节点有连接。

```bash
info|2025-05-25 07:45:27.281092|[P2P][Service] heartBeat,connected count=0
info|2025-05-25 07:45:37.282166|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:45:47.282639|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:45:57.282917|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:46:07.283622|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:46:17.284357|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:46:27.284626|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:46:37.285329|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:46:47.286066|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:46:57.286511|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:47:07.287220|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:47:17.287743|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:47:27.288219|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:47:37.288967|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:47:47.289396|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:47:57.290706|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:48:07.291119|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:48:17.291929|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:48:27.292493|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:48:37.293002|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:48:47.293662|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:48:57.293924|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:49:07.294329|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:49:17.294729|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:49:27.295490|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:49:37.296007|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:49:47.296354|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:49:57.297012|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:50:07.297283|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:50:17.297543|[P2P][Service] heartBeat,connected count=3
info|2025-05-25 07:50:27.297982|[P2P][Service] heartBeat,connected count=3
```



#### 配置及使用控制台

安装java

```bash
sudo apt-get install default-jdk default-jdk -y
```

回到`fisco`目录解压文件

```bash
tar -zxvf console.tar.gz
```

拷贝控制台配置文件

```bash
cp -n console/conf/config-example.toml console/conf/config.toml
```

配置控制台证书

```bash
cp -r blockchain/127.0.0.1/sdk/* console/conf/
```

启动并使用控制台

```bash
cd console && bash start.sh
```

输出下述信息表明启动成功 否则请检查`conf/config.toml`中节点端口配置是否正确

```bash
=============================================================================================
Welcome to FISCO BCOS console(2.9.1)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______  
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \ 
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \ 
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=============================================================================================
```



