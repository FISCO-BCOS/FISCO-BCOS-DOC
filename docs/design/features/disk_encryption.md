# 落盘加密

## 背景介绍

在联盟链的架构中，机构和机构之间搭建一条区块链，数据在联盟链的各个机构内是可见的。

在某些数据安全性要求较高的场景下，联盟内部的成员并不希望联盟之外的机构能够获取联盟链上的数据。此时，就需要对联盟链上的数据进行访问控制。

联盟链数据的访问控制，主要分为两个方面

* 链上通信数据的访问控制
* 节点存储数据的访问控制

对于链上通信数据的访问控制，FISCO BCOS是通过节点证书和SSL来完成。此处主要介绍的是节点存储数据的访问控制，即落盘加密。



![](../../../images/features/data_secure_background.png)



## 总体方案

落盘加密是在机构内部进行的。在机构的内网环境中，每个机构独立的对节点的硬盘数据进行加密。当节点所在机器的硬盘被带离机构，并让节点在机构内网之外的网络启动，硬盘数据将无法解密，节点无法启动。进而无法盗取联盟链上的数据。

## 方案架构



![](../../../images/features/diskencryption_framework.png)



每个机构独立的维护一个Key Center，在节点启动时，节点与Key Center建立连接，获取解密秘钥。具体的实现方案如下。

**Encrypted Space**：节点本地被加密的区域，包括leveldb和node.key（节点私钥）

**Key Center**：秘钥管理服务

**Node**：区块链节点

主要关系如下：

（1）节点在运行时，用dataKey进行加解密，与Encrypted Space里的数据进行交互。

（2）节点本地不存储dataKey，只存储各自的cipherDataKey，在节点启动时，用cipherDataKey向Key Center获取dataKey。

（3）Key Center持有全局的的superKey，对所有发送过来的请求，用superKey进行加解密操作。

（4）每个机构，在内网中部署一个Key Center，外网无法访问Key Center。



![](../../../images/features/diskencryption.png)

## 方案流程

节点在第一次运行前，必须配置好是否采用落盘加密。一旦节点开始运行，无法切换状态。

#### （1）配置dataKey

节点启动前，在内网中，节点管理者将定义好的dataKey发送给Key Center，从Key Center上获取cipherDataKey，并存储在节点的配置文件中。

#### （2）加密节点私钥

节点管理者把节点私钥和cipherDataKey发送给Key Center，从Key Center上获取加密后的节点私钥，并将此私钥作为节点的私钥。

#### （3）节点运行时加解密

节点每次启动时，把cipherDataKey发送给Key Center，从Key Center上获取dataKey，之后，用dataKey来加解密，与Encrypted Space进行交互。

具体的操作步骤，可参考：[落盘加密操作](../../manual/disk_encryption.md)