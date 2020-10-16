# 常见问题解答

## 版本相关

问:
  FISCO BCOS 2.0版本与之前版本有哪些变化？<br>
答:
  请 [参考这里](./what_is_new.md)。

问:
  开发者如何与FISCO BCOS平台交互？<br>
答:
  FISCO BCOS提供多种开发者与平台交互的方式，参考如下：
  - FISCO BCOS 2.0版本提供JSON-RPC接口，具体请 [参考这里](./api.md)。
  - FISCO BCOS 2.0版本提供Web3SDK帮助开发者快速实现应用，具体请 [参考这里](./sdk/java_sdk.md)。
  - FISCO BCOS 2.0版本提供控制台帮助用户快速了解使用FISCO BCOS，[2.6及其以上版本控制台使用文档参考这里](./manual/console_of_java_sdk.md)，[1.x版本控制台使用文档参考这里](./manual/console.md)。

问:
  FISCO BCOS 2.0版本如何搭建？<br>
答:
  FISCO BCOS支持多种搭建方式，常用方式有：
  - 开发部署工具 build_chain.sh：适合开发者体验、测试FISCO BCOS联盟链，具体请 [参考这里](./manual/build_chain.md)。
  - 运维部署工具 generator：适用于企业用户部署、维护FISCO BCOS联盟链，具体请 [参考这里](./enterprise_tools/index.md)。

问:
  FISCO BCOS 2.0版本的智能合约与之前版本合约有什么不同，兼容性如何？<br>
答:
  FISCO BCOS 2.0版本支持最新的Solidity合约，同时增加了precompile合约，具体请 [参考这里](./manual/smart_contract.md)。

问:
  国密和普通版本的区别有哪些？<br>
答:
  国密版FISCO BCOS将交易签名验签、p2p网络连接、节点连接、数据落盘加密等底层模块的密码学算法均替换为国密算法。同时在编译版本，证书，落盘加密，solidity编译java，Web3SDK使用国密版本和普通版本都有区别，具体请 [参考这里](./manual/guomi_crypto.md)。

问:
  是否支持从1.3或1.5升级到2.0版本?<br>
答:
  不支持。

## 控制台

问:
  控制台指令区分大小写吗？<br>
答:
  区分大小写，命令是完全匹配，但是可以采用`tab`补全命令。

问:
  加入共识列表或观察者列表报错，nodeID is not in network，为什么？<br>
答:
  节点加入共识列表和观察者列表的节点必须是连接peer的nodeID列表里面的成员。

问:
  删除节点操作报错，nodeID is not in group peers，为什么？<br>
答:
  节点删除操作中的节点必须是getGroupPeers里面展示的group的peers。

问:
  游离节点（非群组节点）是否可以同步group数据？<br>
答:
  游离节点不参与group内的共识、同步和出块，游离节点可以通过控制台`addSealer/addObserver`命令可以将退出的节点添加为共识/观察节点。

问:
  某节点属于不同的group，是否可以支持查询多group的信息。<br>
答:
   可以，在进入控制台时，输入要查看的groupID:  ./start [groupID]

## FISCO BCOS使用


问:
  2.0版本证书在哪里使用?<br>
答:
  请参考[证书说明文档](manual/certificates.md)

问:
  2.0版本交易结构包括哪些字段?<br>
答:
  请参考[这里](design/protocol_description.html#rlp)

问:
  系统配置、群组配置、节点配置分别指什么？<br>
答:
  系统配置是指节点配置中一些影响账本功能，并需账本节点共识的配置项。群组配置指节点所属的群组的相关配置，节点的每个群组都有独立的配置。节点配置指所有可配置项。

问:
  群组配置都是可改的吗？<br>
答:
  从配置项是否可改的维度，分为

  - 节点首次启动生成创世块后不能再修改。这类配置放置于group.x.genesis文件，其中x表示组编号，全链唯一。
  - 通过发交易修改配置项实现账本内一致。
  - 修改自身配置文件后，节点重启生效。这类配置放置于`group.x.ini`文件。群组配置改后重启可改项就是本地配置，nodeX/conf下的`group.*.ini`文件，更改重启生效。涉及配置项为[tx_pool].limit（交易池容量），[consensus].ttl(节点转发数)。


问:
  群组配置用户可以改的涉及哪些配置？<br>
答:
  群组可修改配置分为共识可改配置和手工可改配置

  - 共识可改配置：全组所有节点相同，共识后生效。[consensus].max_trans_num,[consensus].node.X,[tx].gas_limit。
  - 手工可改配置：`group.x.ini`文件中，修改后重启生效，只影响节点。配置项有[tx_pool].limit。

问:
  群组共识可改配置如何更改、查询？<br>
答:
  共识可改配置可以通过控制台修改。共识可改配置项查询除了控制台外，还可以通过RPC接口查询，具体请 [参考这里](./design/rpc.md)。

  - [consensus].max_trans_num，[tx].gas_limit使用接口setSystemConfigByKey更改，对于的配置项为tx_count_limit，tx_gas_limit。具体参见setSystemConfigByKey -h 。
  - [consensus].node.X的更改涉及到节点管理，控制台接口涉及到addSealer，addObserver，removeNode，具体参考《节点管理》。


问:
  群组观察节点和共识节点有什么区别？<br>
答:
  观察节点能同步群组数据，但不能参与共识。共识节点除了具有观察者权限，还参与共识。


问:
  如何将合约纳入CNS管理？<br>
答:
  在部署合约时，调用CNS合约接口，将合约name、version、address信息写入CNS表中。


问:
  如何查询合约CNS表？<br>
答:
  通过Web3SDK控制台指令查询，查询指令根据合约name查询。

问:
  为什么本地SDK无法连接云服务器上的FISCO BCOS节点？<br>
答:
  1. 检查云服务器上的节点配置，channel是否监听外网IP，而不是`127.0.0.1`。端口介绍[参考这里](https://mp.weixin.qq.com/s/XZ0pXEELaj8kXHo32UFprg)
  2. 检查通过云服务器提厂商提供的控制台，检查是否配置了安全组，需要在安全组中开放FISCO BCOS节点所使用的channel端口。
  3. 检查生成的证书是否正确，[参考这里](./enterprise_tools/operation.md#节点配置错误检查)

问:
  节点启动后，为什么无法连接其他节点且节点日志中出现『错误的文件描述符』等网络异常信息？<br>
答:
  1. 请检查节点证书配置是否正确
  2. 请检查节点类型（国密、非国密）是否与链中其他节点一致

问：
  日志中为何出现形如"invalid group status"的错误提示？

答：可能由于文件系统错误等原因导致节点在本地记录的群组状态不合法，可以检查群组数据目录下的`.group_status`文件，将其内容改为下列值之一：

- STOPPED
- DELETED
- RUNNING


## Web3SDK

问:
  Web3SDK对Java版本有要求吗？<br>
答:
  参考[Java环境要求](sdk/java_sdk.html#id1)<br>

问:
  Web3SDK配置完成，启动失败的原因是什么？<br>
答:
  参考[JavaSDK异常场景](sdk/java_sdk.html#id22)<br>


## 运维部署工具
问：
  运维部署工具使用时出现找不到pip

答：
  运维部署工具依赖python pip，使用以下命令安装：
```
$ bash ./scripts/install.sh
```

问:
  运维部署工具使用时出现
```
Traceback (most recent call last):
   File "./generator", line 19, in <module>
    from pys.build import config
   File "/data/asherli/generator/pys/build/config.py", line 25, in <module>
     import configparse
```
答:
  系统缺少python configparser模块，请按照以下命令安装：

```bash
  $ pip install configparser
```

问:
  节点或SDK使用的OpenSSL证书过期了，如何续期？

答:
  证书续期操作可以参考[证书续期操作](./manual/certificates.md#id9)

问：
  使用下载命令提示certificate verify failed
答：
  在 ./pys/tool/utils.py 这个文件的开头中加入如下两行

```python
import ssl
ssl._create_default_https_context = ssl._create_unverified_context
```