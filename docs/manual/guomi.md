# 国密使用手册

为了充分支持国产密码学算法，金链盟基于国产密码学标准，实现了国密加解密、签名、验签、哈希算法、国密SSL通信协议，并将其集成到FISCO BCOS平台中，实现了对国家密码局认定的商用密码的完全支持。

见[国密版fisco bcos设计手册](../design/features/guomi.md)

## 国密FISCO-BCOS安装

```bash
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh) -b release-2.0.1 -g
# 执行成功后会在./bin/目录下生成国密版fisco-bcos可执行文件
$ ./bin/fisco-bcos -v
FISCO-BCOS gm version 2.0
```

## 一键搭链脚本

拉取build_chain脚本，并进行安装

* 以下安装默认在当前目录下执行

```bash
$ curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh
$ bash ./build_chain.sh -l '127.0.0.1:4' -e ./bin/fisco-bcos -g
```

* -e 为编译的国密版fisco-bcos的路径，需要在脚本后指定
* -g 国密编译选项，使用成功后会生成国密版的节点

当国密联盟链部署完成之后，其余操作与[快速入门](./hello_world.md)的操作相同

## 国密配置信息

2.0国密版本FISCO BCOS节点之间采用SSL安全通道发送和接收消息，证书主要配置项集中在

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

conf目录下的original_cert文件夹为节点与sdk进行通信所需要的证书

## 国密版SDK使用

详细操作参考[sdk文档](../sdk/index.html)。

## 国密控制台使用

国密版控制台功能与标准版控制台使用方式相同，见[控制台操作手册](../manual/console.md)。

## 国密落盘加密配置

### 国密版Key Center

国密版的Key Center需重新编译Key Center，不同点在于cmake时带上``` -DBUILD_GM=ON ```选项。

``` shell
cmake3 .. -DBUILD_GM=ON
```

其它步骤与标准版Key Center相同，请参考：[keycenter repository](https://github.com/FISCO-BCOS/keycenter)

### 国密版节点配置

国密版的证书有所调整，落盘加密需要加密的证书，变为两个：conf/gmnode.key 和 conf/origin_cert/node.key。其它与[标准版落盘加密操作](./disk_encryption.md)相同。

``` shell
cd keycenter/scripts
#加密 conf/gmnode.key 参数：ip port 节点私钥文件 cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node_127.0.0.1_0/conf/gmnode.key ed157f4588b86d61a2e1745efe71e6ea 
#加密 conf/origin_cert/node.key 参数：ip port 节点私钥文件 cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node_127.0.0.1_0/conf/origin_cert/node.key ed157f4588b86d61a2e1745efe71e6ea 
```