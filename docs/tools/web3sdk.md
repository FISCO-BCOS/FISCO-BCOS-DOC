# 物料包web3sdk配置

请注意，物料包内置了配置好的web3sdk以及相关的环境，用户可以直接使用web3sdk。请注意，由于物料包已经生成好了链证书和机构证书，因此物料包中的web3sdk的证书配置与源码编译略有不同。

假设用户拉取web3sdk的操作git clone https://github.com/FISCO-BCOS/web3sdk 是在/mydata下进行的，则用户执行：

则用户在物料包build/web3sdk目录下，拷贝相应证书至需要自行配置的web3sdk文件夹下。 

```
$ cd conf
$ cp sdk.* ca.crt client.keystore /mydata/web3sdk/dist/conf/
```

## 物料包应用开发指南

物料包内置了配置好的web3sdk，用户可以直接进入配置好的服务器下的build目录，进入web3sdk目录进行应用开发，[应用开发指南](https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/web3sdk_dev.html)

## 物料包web3sdk配置

如果用户想要在物料包下配置web3sdk，请参考[web3sdk配置部分](https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/index.html)

请注意，由于物料包已经生成好了链证书和机构证书，物料包中配置文件-配置java客户端过程和文档中略有不同。

用户可以直接从编译源码部分开始配置。   

修改web3sdk/dist/conf目录的applicationContext.xml文件所需要节点的config.json在物料包目录下的build/node* 下 *为希望选择的节点数
config.json中包含systemproxyaddress 系统合约地址 rpcport, p2pport,channelPort。


用户根据build/node*/config.json 配置java客户端相关数据：需要更改的有数据有：系统合约地址systemproxyaddress，node port相关信息等

```
$ vim applicationContext.xml
```


具体操作参考web3sdk-[配置文件](https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/config_web3sdk.html)

```
如 </value>node0@0.0.0.0:8841</value>
```

修改完成后，运行

```
$ java -cp 'conf/:apps/*:lib/*' org.bcos.channel.test.TestOk
```

测试是否配置成功


