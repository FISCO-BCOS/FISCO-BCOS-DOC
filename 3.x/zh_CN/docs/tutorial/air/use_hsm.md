# 构建使用硬件密码模块的国密链

标签：``硬件加密`` ``HSM`` ``密码机``

---

国密版的FISCO BCOS 3.2.0使用硬件加密模块（Hardware secure module, HSM）进行密码计算的功能，这使得FISCO BCOS拥有更快的密码计算速度，更安全的密钥保护。本教程主主要介绍FISCO BCOS 3.2.0版本的使用方法。

## 1. 节点版本

- 当您的节点需要使用硬件加密模块时，需要在节点配置项设置，来启动密码机加密功能，其中节点的签名验签用密钥机中的密钥。所有密钥对存储在密码机中，内存中无密钥对存留，提高了密钥存储的安全性。


## 2. 安装密码卡/密码机
构建使用硬件密码模块的国密链，你需要在节点所在的服务器安装上密码卡或密码机。FISCO BCOS支持了《GMT0018-2012 密码设备应用接口规范》的密码卡/密码机。

### 第一步. 请根据您密码卡/密码机的安装指引安装好密码机.
安装符合了GMT0018-2012规范的动态库文件，比如：
1. 将库文件``libgmt0018.so``放在默认的库搜索路径下，并保证用户具有读和执行权限。可以在节点的配置文件`config.ini`的配置项`security`中配置该so动态库的路径。例如，可以放在Ubuntu操作系统的``/usr/local/lib``目录下，放在CentOS操作系统，``/lib64``或``/usr/lib64``目录下。

### 第二步. 请初始化密码卡/密码机，运行其测试程序确保功能正常.
请根据密码卡/密码机厂商的指引初始化设备，并创建你所需要的内部密钥。然后运行测试程序，确保功能正常，确保能通过libgmt0018.so的库正确调用密码机所提供GMT0018-2012的接口方法。

## 3. 创建使用密码机的FISCO BCOS区块链节点
### 第一步. 编译节点的动态二进制
加载密码卡的动态库文件需采用FISCO BCOS的动态二进制，用户需在相应环境下手动编译节点动态二进制。使用源码编译二进制，参考[源码编译](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/tutorial/compile_binary.html)。
注意：**编译**环节，需要指定编译动态的二进制，即不指定`-DBUILD_STATIC=ON`
```shell
# 创建编译目录
mkdir -p build && cd build
cmake .. || cat *.log
```

### 第二步. 生成国密节点
搭建区块链，具体可参考[搭建第一个区块链网络](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/quick_start/air_installation.html#airfisco-bcos)
```bash
cd ~/fisco

curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.2.0/build_chain.sh && chmod u+x build_chain.sh

./build_chain.sh -e ~/fisco/FISCO-BCOS/build/fisco-bcos-air/fisco-bcos -p 30300,20200 -l 127.0.0.1:4 -s
```
### 第三步. 配置密钥类型和密钥索引
例如，配置节点node0使用密码机内部密钥。

首先，请将node0的节点密钥导入密码机。比如，将``~/fisco/nodes/127.0.0.1/node0/conf/gmnode.key``和``~/fisco/nodes/127.0.0.1/node0/conf/gmennode.key``，分别导入到密码机的43和44号密钥索引位置。

然后，修改node0的配置文件，``~/fisco/nodes/127.0.0.1/node0/config.ini``。设置enable_hsm、hsm_lib_path、key_index、password四个配置项，指定节点使用硬件加密模块，以及硬件加密模块内的密钥索引。

其他节点同理。
```
[security]
    ; true present that use hsm
    enable_hsm=true
    ; the path of lib file for HSM
    hsm_lib_path = /usr/local/lib/libgmt0018.so
    ; key index the key inside HSM
    key_id=43
    ; password to use HSM
    password = 12345678
```

### 第四步. 启动节点
```shell
./nodes/127.0.0.1/start_all.sh 
```
启动成功
```shell
try to start node0
try to start node1
try to start node2
try to start node3
 node0 start successfully
 node1 start successfully
 node2 start successfully
 node3 start successfully
 ```

若是使用已经导入在密码机中的密钥，由于目前build_chain.sh还未适配，所以需要手动修改下`config.genesis`中的`nodeid`公钥。操作简介如下：
1. 在已经搭建的链中，将config.ini的enable_hsm、hsm_lib_path、key_index、password四个配置项设置好，注意`enable_hsm = true`；
2. 启动节点后，在节点日志中搜索`loadKeyPair success`、`publicKey`,获得密码机的公钥
```shell
info|2023-01-11 19:33:10.887669|[INITIALIZER][METRIC]loadKeyPair success,publicKey=8275656394e35c42b4845b00000c4705787bb270a913200b1d5c58e89e7795b7fe9d25f19b9adfd5ae0c571343357450052b9a756f984f03f23ceccfb7a18993
```
3. 将日志中的公钥信息 `8275656394e35c42b4845b00000c4705787bb270a913200b1d5c58e89e7795b7fe9d25f19b9adfd5ae0c571343357450052b9a756f984f03f23ceccfb7a18993` 复制到`config.genesis`[consensus]的node.0，然后删除节点`/data`目录数据，重启节点即可。其他节点同理。
```shell
[consensus]
    node.0=8275656394e35c42b4845b00000c4705787bb270a913200b1d5c58e89e7795b7fe9d25f19b9adfd5ae0c571343357450052b9a756f984f03f23ceccfb7a18993:1
    node.1=fcbaa37097a569cc9e217496b97c07bd2f78598d81ee44ebc4fc40ea8d120630f7cf9d4d9f87fb61716ed49a5c2603e2d7d27bfb0587797752668455d2ebbf26:1

```

 ### 第五步. 确认节点运行正常
 检查进程是否启动

```shell
ps -ef | grep -v grep | grep fisco-bcos
```

正常情况会有类似下面的输出；
如果进程数不为4（以实际启动节点数为准），则进程没有启动（一般是端口被占用导致的）

```shell
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

如下，查看节点node0链接的节点数

```shell
tail -f nodes/127.0.0.1/node0/log/* |grep -i "heartBeat,connected count"
```

正常情况会不停地输出连接信息，从输出可以看出node0与另外3个节点有连接。
```bash
info|2022-08-15 19:38:59.270112|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:09.270210|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:19.270335|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:29.270427|[P2PService][Service][METRIC]heartBeat,connected count=3
```