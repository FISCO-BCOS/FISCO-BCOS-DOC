# 落盘加密

## 背景介绍

在联盟链的架构中，机构和机构之间搭建一条区块链，数据在联盟链的各个机构内是可见的。

在某些数据安全性要求较高的场景下，联盟内部的成员并不希望联盟之外的机构能够获取联盟链上的数据。此时，就需要对联盟链上的数据进行访问控制。

联盟链数据的访问控制，主要分为两个方面

* 链上通信数据的访问控制
* 节点存储数据的访问控制

对于链上通信数据的访问控制，FISCO BCOS是通过节点证书和SSL来完成。此处主要介绍的是节点存储数据的访问控制，即落盘加密。



![](../../../images/features/data_secure_background.png)



## 主要思想

落盘加密是在机构内部进行的。在机构的内网环境中，每个机构独立地对节点的硬盘数据进行加密。当节点所在机器的硬盘被带离机构，并让节点在机构内网之外的网络启动，硬盘数据将无法解密，节点无法启动。进而无法盗取联盟链上的数据。

## 方案架构



![](../../../images/features/diskencryption_framework.png)



落盘加密是在机构内部进行的，每个机构独立管理自己硬盘数据的安全。内网中，每个节点的硬盘数据是被加密的。所有加密数据的访问权限，通过Key Manager来管理。Key Manager是部署在机构内网内，专门管理节点硬盘数据访问秘钥的服务，外网无法访问。当内网的节点启动时，从Key Manager处获取加密数据的访问秘钥，来对自身的加密数据进行访问。

加密保护的对象包括：

* 节点本地存储的数据库：leveldb
* 节点私钥：node.key，gmnode.key（国密）

## 具体实现

具体的实现过程，是通过节点自身持有的秘钥（dataKey）和Key Manager管理的全局秘钥（superKey）来完成的。

**节点**

* 节点用自己的dataKey，对自身加密的数据（Encrypted Space）进行加解密。
* 节点本身不会在本地磁盘中存储dataKey，而是存储dataKey被加密后的cipherDataKey。
* 节点启动时，拿cipherDataKey向Key Manager请求，获取dataKey。
* dataKey只在节点的内存中，当节点关闭后，dataKey自动丢弃。

**Key Manager**

持有全局的superKey，负责对所有节点启动时的授权请求进行响应，授权。

- Key Manager必须实时在线，响应节点的启动请求。
- 当节点启动时，发来cipherDataKey，Key Manager用superKey对cipherDataKey进行解密，若解密成功，就将节点的dataK返回给节点。
- Key Manager只能在内网访问，机构内的外网无法访问Key Manager.





![](../../../images/features/diskencryption.png)

## 方案流程

方案流程分为节点初始配置和节点安全运行。

### 节点初始配置

节点启动前，需要为节点配置dataKey

```eval_rst
.. important::
    节点在生成后，启动前，必须决定好是否采用落盘加密，一旦节点配置成功，并正常启动，将无法切换状态。
```

（1）管理员定义好节点的的dataKey，并将dataKey发送给Key Manager，从Key Manager处获取cipherDataKey。

（2）将cipherDataKey配置到节点的配置文件中

（3）启动节点

### 节点安全运行

节点启动时，会通过Key Manager，获取本地数据访问的秘钥dataKey。

（1）节点启动，从配置文件中读取cipherDataKey，并发送给Key Manager。

（2）Key Manager收到cipherDataKey，用superKey解密cipherDataKey，若解密成功，则将解密后的dataKey返回给节点。

（3）节点拿到dataKey，用dataKey对本地的数据（Encrypted Space）进行交互。从Encrypted Space读取的数据，用dataKey解密获取真实数据。要写入Encrypted Space的数据，先用dataKey加密，再写入。

### 为什么可以保护数据？

当某节点的硬盘被意外的带到内网环境之外，数据是不会泄露的。

（1）当节点在内网之外启动时，无法连接Key Manager，虽然有cipherDataKey，也无法获取dataKey。

（2）不启动节点，直接对节点本地的数据进行操作，由于拿不到dataKey，无法解密Encrypted Space，拿不到敏感数据。



具体落盘加密的使用，可参考：[落盘加密操作](../../manual/storage_security.md)
