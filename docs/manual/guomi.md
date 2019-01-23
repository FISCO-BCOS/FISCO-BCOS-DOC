# 国密使用手册

## 注意事项
国密的操作方式与非国密版的[测试操作手册](http://wiki.weoa.com/books/fisco-bcos/page/%E6%B5%8B%E8%AF%95%E6%8C%87%E5%AF%BC%E6%89%8B%E5%86%8C)大致相同，但在进行**源码编译**，**一键搭链脚本**，和**配置SDK**略有不同。

请注意，国密版的fisco bcos编译合约时请使用国密版的[solidity编译器](https://github.com/FISCO-BCOS/solidity)

与非国密版的FISCO BCOS区别如下：
## 源码编译
```
$ git clone https://github.com/FISCO-BCOS/FISCO-bcos
$ git checkout release-2.0.1
$ mkdir build && cd build
$ cmake3 .. -DBUILD_GM=ON
$ make
```
执行成功后会在./bin/目录下生成国密版fisco-bcos可执行文件
```
$ ./fisco-bcos --version
会显示
$ FISCO-BCOS gm version 2.0
```
## 一键搭链脚本
拉取build_chain脚本，并进行安装
```
$ curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh
$ bash ./build_chain.sh -l '127.0.0.1:4' -e ./bin/fisco-bcos -g
```
* -e 为编译的国密版fisco-bcos的路径，需要在脚本后指定
* -g 国密编译选项，使用成功后会生成国密版的节点

## 国密版sdk配置
国密版FISCO BCOS需要配置相应的国密SDK。

需要将sdk的applicationContext.xml中encryptType字段设置为1，并且使用国密版节点生成的证书

拉取web3sdk源码后
```
$ cd web3sdk
$ vim conf/applicationContext.xml
将
encryptType=0 
设置为
encryptType=1
```
国密版的sdk需要的证书在节点的data文件夹下的sdk文件夹中

详细操作见[sdk使用]()

## 国密落盘加密配置
使用国密版落盘加密时，需要同时对conf/gmnode.key 和 conf/origin_cert/node.key

详细操作见[落盘加密]()


## 国密配置信息

2.0国密版本FISCO BCOS节点之间采用SSL安全通道发送和接收消息，证书主要配置项集中在
```
[secure] section：

data_path：证书文件所在路径
key：节点私钥相对于data_path的路径
cert: 证书gmnode.crt相对于data_path的路径
ca_cert: gmca证书路径
```
```
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
