# 构建使用硬件密码模块的国密链

标签：``硬件加密`` ``HSM`` ``密码机``

---

国密版的FISCO BCOS 3.3.0硬件加密模块（Hardware secure module, HSM）增加了以下功能：
1. build_chain.sh加载密码机内置密钥的node.pem文件，搭建使用密码机的区块链。
2. java-sdk增加密码机配置项，使用密码机对交易签名验签；(具体参考[java-sdk配置](../../sdk/java_sdk/config.md))
本教程主主要介绍FISCO BCOS 3.3.0版本在节点端如何配置使用密码机。

## 1. 节点版本

- 当您的节点需要使用硬件加密模块时，需要在节点配置项设置，来启动密码机加密功能，其中节点的签名验签用密钥机中的密钥。所有密钥对存储在密码机中，内存中无密钥对存留，提高了密钥存储的安全性。

## 2. 安装密码卡/密码机
构建使用硬件密码模块的国密链，你需要在节点所在的服务器安装上密码卡或密码机。FISCO BCOS支持了《GMT0018-2012 密码设备应用接口规范》的密码卡/密码机。

### 第一步. 请根据您密码卡/密码机的安装指引安装好密码机.
安装符合了GMT0018-2012规范的动态库文件，比如：
1. 将动态库文件``libgmt0018.so``放在默认的库搜索路径下（windows操作系统为.dll格式），并保证用户具有读和执行权限。可以在节点的配置文件`config.ini`的配置项`security`中配置该动态库的路径。例如，可以放在Ubuntu操作系统的``/usr/local/lib``目录下，放在CentOS操作系统，``/lib64``或``/usr/lib64``目录下。

### 第二步. 请初始化密码卡/密码机，运行其测试程序确保功能正常.
请根据密码卡/密码机厂商的指引初始化设备，并创建你所需要的内部密钥。然后运行测试程序，确保功能正常，确保能通过libgmt0018.so的动态库正确调用密码机所提供GMT0018-2012的接口方法。

## 3. 创建使用密码机的FISCO BCOS区块链节点
### 第一步. 节点的动态二进制
加载密码卡的动态库文件需采用FISCO BCOS的动态二进制。用户可下载FISCO BCOS提供的动态二进制，或者用户自己在相应环境下手动编译节点动态二进制。使用源码编译二进制，参考[源码编译](../../tutorial/compile_binary.md)。
注意：**编译**环节，需要指定编译动态的二进制，即不指定`-DBUILD_STATIC=ON`
```shell
# 创建编译目录
mkdir -p build && cd build
cmake .. || cat *.log
```

### 第二步. 生成国密节点

如密码机密钥生成，有两种方式：
1. 通过工具生成节点密钥后，将节点密钥导入密码机，记录索引位置。如将node0、node1的密钥证书导入到密码机的43和44号密钥索引位置；
2. 通过密码机管理程序，生成密码机内置密钥，记录索引位置；

密码机的**公私钥**用于**签名验签**；

搭建区块链，具体可参考[搭建第一个区块链网络](../../quick_start/air_installation.md)
```bash
cd ~/fisco

curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.5.0/build_chain.sh && chmod u+x build_chain.sh
```
在build_chain同级目录下，创建文件夹（例如nodeKeyDir），用于存放密码机密钥的node.pem文件(证书数目和搭建节点数一致)。
```bash
./build_chain.sh -e ~/fisco/FISCO-BCOS/build/fisco-bcos-air/fisco-bcos -p 30300,20200 -l 127.0.0.1:4 -s -H -n nodeKeyDir/
```
- `-H`: 开启密码机；
- `-n`: 加载证书目录里的证书文件生成nodeid；
具体参考[部署工具(build_chain.sh)](./build_chain.md)；

### 第三步. 配置密钥类型和密钥索引
在节点配置文件`config.ini`中添加配置项`enable_hsm`、`hsm_lib_path`、`key_index`、`password`，设置节点签名验签是否使用密码机中的密钥。
例如，配置节点node0使用密码机内部密钥,签名验签密钥索引为43；
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
其他节点同理。

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