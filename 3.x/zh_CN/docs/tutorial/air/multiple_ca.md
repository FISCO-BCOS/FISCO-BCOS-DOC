# 修改普通区块链为支持多CA证书区块链

标签： ``配置`` ``config.ini`` ``端口配置`` ``配置`` `` ca证书`` 

----

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

本节详细介绍如何将基于[部署工具(build_chain.sh)](./build_chain.md)搭建的4节点区块链系统改造为支持多CA证书的区块链系统。

## 1.改造所需文件

普通区块链改造为支持多CA证书区块链时，需要提前准备所需要的额外证书，用于生成扩容节点配置，需要准备的文件包括：

- **CA证书**: 此为用户想使用的其他证书和私钥，用于在指定节点使用此证书


## 2.改造节点

**步骤1：改造节点配置**

准备好配置文件后，使用建链脚本`build_chain.sh`改造区块链节点：

这里将以[搭建第一个区块链网络](../quick_start.md)为基础的四节点区块链改造为支持多CA证书的区块链，节点`node0`、`node1`、`node2`使用区块链生成的`ca.crt`作为ca证书，节点`node3`使用额外的`extraCa.crt`作为ca证书：

```shell
# 进入操作目录(Note: 进行本操作之前，请参考【搭建第一个区块链网络节点】部署一条Air版FISCO BCOS区块链)
$ cd ~/fisco

# 创建额外证书存放目录
$ mkdir ca

#将额外证书与私钥拷贝至刚刚创建的ca目录
$ cp -r ~/ca ca

# 调用build_chain.sh改造节点，新节点扩容到nodes/127.0.0.1/node4目录
# -C 指定为改造节点功能
# -N 指定将要改造的节点
# -u 指定除该节点自身证书外区块链所使用到的其他证书路径，本例中node0~node2使用区块链生成的节点证书，此选项指定为extraCa.crt的路径
$ bash build_chain.sh -C modify -N ./nodes/127.0.0.1/node0 -u ./ca/extraCa.crt
$ bash build_chain.sh -C modify -N ./nodes/127.0.0.1/node1 -u ./ca/extraCa.crt
$ bash build_chain.sh -C modify -N ./nodes/127.0.0.1/node2 -u ./ca/extraCa.crt

# node3此选项指定node0~node2证书路径
$ bash build_chain.sh -C modify -N ./nodes/127.0.0.1/node3 -u ./nodes/127.0.0.1/node0/conf/ca.crt
```

当节点输出下列信息说明生成扩容配置成功，输出的日志如下：

`[INFO] Modify node ca setting success`

改造节点的配置目录如下：

~~~
# tree nodes/127.0.0.1/node3/conf
nodes/127.0.0.1/node3/conf
├── ca.crt
├── cert.cnf
├── multiCaPath
│   └── 5c299a7d.0
├── node.nodeid
├── node.pem
├── ssl.crt
├── ssl.key
└── ssl.nodeid
~~~

其中`multiCaPath`目录为存放其他不同CA证书的路径，存放的CA证书以`subject_hash.0`的方式命名，`subject_hash`为证书的机构名称hash值。

**步骤2：启动改造完成后的节点**

~~~
bash nodes/127.0.0.1/start_all.sh
~~~

至此，通过`build_chain.sh`脚本完成了Air版本FISCO BCOS的改造多CA证书操作，`build_chain.sh`脚本使用方法请参考[这里](./build_chain.md)。Pro、Max版本FISCO BCOS的改造与Air版本一致。

国密版本改造方法在调用`build_chain.sh`脚本时需要添加国密选项，如下

~~~
bash build_chain.sh -C modify -N ./nodes/127.0.0.1/node0 -u ./ca/extraCa.crt -s
~~~

其他步骤均与此例一致。