# 落盘加密

## 总体方案

在内网环境下，保证节点本地存储的数据是加密的。当节点所在硬盘被带出外网，数据不能被解密。通过此方法，保证联盟链内部数据隐私。非联盟链的成员不能通过盗取硬盘，而获取联盟链的私密信息。

## 方案架构

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