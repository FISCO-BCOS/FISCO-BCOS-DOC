# FISCO BCOS相关

## 2.0版本证书在哪里使用

请参考[证书说明文档](manual/certificates.md)

## 2.0版本交易结构包括哪些字段

请参考[这里](../design/protocol_description.md#rlp)

## 系统配置、群组配置、节点配置分别指什么

系统配置是指节点配置中一些影响账本功能，并需账本节点共识的配置项。群组配置指节点所属的群组的相关配置，节点的每个群组都有独立的配置。节点配置指所有可配置项。

## 群组配置都可以吗

从配置项是否可改的维度，分为：

- 节点首次启动生成创世块后不能再修改。这类配置放置于group.x.genesis文件，其中x表示组编号，全链唯一。
- 通过发交易修改配置项实现账本内一致。
- 修改自身配置文件后，节点重启生效。这类配置放置于`group.x.ini`文件。群组配置改后重启可改项就是本地配置，nodeX/conf下的`group.*.ini`文件，更改重启生效。涉及配置项为[tx_pool].limit（交易池容量），[consensus].ttl(节点转发数)。

## 群组配置用户可以改的涉及哪些配置

群组可修改配置分为共识可改配置和手工可改配置

- 共识可改配置：全组所有节点相同，共识后生效。[consensus].max_trans_num,[consensus].node.X,[tx].gas_limit。
- 手工可改配置：`group.x.ini`文件中，修改后重启生效，只影响节点。配置项有[tx_pool].limit。

## 群组共识可改配置如何更改、查询

共识可改配置可以通过控制台修改。共识可改配置项查询除了控制台外，还可以通过RPC接口查询，具体请 [参考这里](../design/rpc.md)。

- [consensus].max_trans_num，[tx].gas_limit使用接口setSystemConfigByKey更改，对于的配置项为tx_count_limit，tx_gas_limit。具体参见setSystemConfigByKey -h 。
- [consensus].node.X的更改涉及到节点管理，控制台接口涉及到addSealer，addObserver，removeNode，具体参考《节点管理》。

## 群组观察节点和共识节点有什么区别

观察节点能同步群组数据，但不能参与共识。共识节点除了具有观察者权限，还参与共识。

## 如何将合约纳入CNS管理

在部署合约时，调用CNS合约接口，将合约name、version、address信息写入CNS表中。

## 如何查询合约CNS表

通过Web3SDK控制台指令查询，查询指令根据合约name查询。

## 为什么本地SDK无法连接云服务器上的FISCO BCOS节点

1. 检查云服务器上的节点配置，channel是否监听外网IP，而不是`127.0.0.1`。端口介绍[参考这里](https://mp.weixin.qq.com/s/XZ0pXEELaj8kXHo32UFprg)
2. 检查通过云服务器提厂商提供的控制台，检查是否配置了安全组，需要在安全组中开放FISCO BCOS节点所使用的channel端口。
3. 检查生成的证书是否正确，[参考这里](../enterprise_tools/operation.md#节点配置错误检查)

## 节点启动后，为什么无法连接其他节点且节点日志中出现『错误的文件描述符』等网络异常信息

1. 请检查节点证书配置是否正确
2. 请检查节点类型（国密、非国密）是否与链中其他节点一致

## 日志中为何出现形如"invalid group status"的错误提示

可能由于文件系统错误等原因导致节点在本地记录的群组状态不合法，可以检查群组数据目录下的`.group_status`文件，将其内容改为下列值之一：

- STOPPED
- DELETED
- RUNNING
