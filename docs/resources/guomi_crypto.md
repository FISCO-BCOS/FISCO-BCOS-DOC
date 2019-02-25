# 启用国密算法

为了充分支持国产密码学算法，金链盟基于国产密码学标准，在FISCO BCOS平台中集成了国密加解密、签名、验签、哈希算法、国密SSL通信协议，实现了对国家密码局认定的商用密码的完全支持。设计文档见[国密版FISCO BCOS设计手册](../design/features/guomi.md)。

## 初次部署国密版FISCO BCOS

本节使用[`build_chain`](build_chain.md)脚本在本地搭建一条4节点的FISCO BCOS链，以`Ubuntu 16.04`系统为例操作。本节使用预编译的静态`fisco-bcos`二进制文件，在CentOS 7和Ubuntu 16.04上经过测试。

```bash
# Ubuntu16安装依赖
$ sudo apt install -y openssl curl
# 准备环境
$ cd ~ && mkdir fisco && cd fisco
# 下载build_chain.sh脚本
$ curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh && chmod u+x build_chain.sh
# 准备fisco-bcos二进制文件
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh) -g
# 检查二进制是否可执行 执行下述命令，看是否输出版本信息
# 执行成功后会在./bin/目录下生成国密版fisco-bcos可执行文件
$ ./bin/fisco-bcos -v
FISCO-BCOS gm version 2.0
```

执行完上述步骤后，fisco目录下结构如下：

```bash
fisco
├── bin
│   └── fisco-bcos
└── build_chain.sh
```


- 搭建4节点FISCO BCOS链

```bash
# 生成一条4节点的FISCO链 4个节点都属于group1 下面指令在fisco目录下执行
# -e 指定fisco-bcos路径 -p指定起始端口，分别是p2p_port,channel_port,jsonrpc_port
# 根据下面的指令，需要保证机器的30300~30303，20200~20203，8545~8548端口没有被占用
$ ./build_chain.sh -e bin/fisco-bcos -l "127.0.0.1:4" -p 30300,20200,8545 -g
```

* -e 为编译的国密版fisco-bcos的路径，需要在脚本后指定。
* -g 国密编译选项，使用成功后会生成国密版的节点。

关于`build_chain.sh`脚本选项，请[参考这里](build_chain.md)。命令正常执行会输出`All completed`。（如果没有输出，则参考`nodes/build.log`检查）。

```bash
[INFO] Downloading tassl binary ...
Generating CA key...
Generating Guomi CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /mnt/c/Users/asherli/Desktop/keycenter/build/nodes
[INFO] CA Key Path       : /mnt/c/Users/asherli/Desktop/keycenter/build/nodes/gmcert/ca.key
[INFO] Guomi mode        : yes
==============================================================
[INFO] All completed. Files in /mnt/c/Users/asherli/Desktop/keycenter/build/nodes
```

当国密联盟链部署完成之后，其余操作与[hello world](./hello_world.md)的操作相同。

## 国密配置信息

国密版本FISCO BCOS节点之间采用SSL安全通道发送和接收消息，证书主要配置项集中在如下配置项中：

```shell
[secure] section：

data_path：证书文件所在路径
key：节点私钥相对于data_path的路径
cert: 证书gmnode.crt相对于data_path的路径
ca_cert: gmca证书路径

;certificate configuration
[secure]
    ;directory the certificates located in
    data_path=conf/
    ;the node private key file
    key=gmnode.key
    ;the node certificate file
    cert=gmnode.crt
    ;the ca certificate file
    ca_cert=gmca.crt
```

## 国密版SDK使用

详细操作参考[sdk文档](../../docs/sdk/config.html#id8)。

## 国密控制台使用

国密版控制台功能与标准版控制台使用方式相同，见[控制台操作手册](../manual/console.md)。

## 国密落盘加密配置

### 国密版Key Center

国密版的Key Center需重新编译Key Center，不同点在于cmake时带上``` -DBUILD_GM=ON ```选项。

``` shell
# centos下
cmake3 .. -DBUILD_GM=ON
# ubuntu下
cmake .. -DBUILD_GM=ON
```

其它步骤与标准版Key Center相同，请参考：[keycenter repository](https://github.com/FISCO-BCOS/keycenter)。

### 国密版节点配置

FISCO BCOS国密版采用双证书模式，因此落盘加密需要加密的两套证书，分别为：conf/gmnode.key 和 conf/origin_cert/node.key。其它与[标准版落盘加密操作](./disk_encryption.md)相同。

``` shell
cd keycenter/scripts
#加密 conf/gmnode.key 参数：ip port 节点私钥文件 cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node_127.0.0.1_0/conf/gmnode.key ed157f4588b86d61a2e1745efe71e6ea 
#加密 conf/origin_cert/node.key 参数：ip port 节点私钥文件 cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node_127.0.0.1_0/conf/origin_cert/node.key ed157f4588b86d61a2e1745efe71e6ea 
```