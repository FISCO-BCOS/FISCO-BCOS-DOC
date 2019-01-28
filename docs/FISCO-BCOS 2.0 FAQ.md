#   FAQ：

#### 【通用问题】

1. fisco 1.3和2.0有什么大的差异？
  2.0增加了多群组、分布式存储等重要特性，在性能等方面进行了较大的优化。

2. fisco2.0支持哪些版本
   普通版本，国密版本，暂不支持群环签名版本

3. 有哪些接口可以调用 fisco-bcos 2.0
  1） rpc接口 ：使用简便，搭链完成后可以直接通过curl命令发送请求。

  2）控制台：功能比rpc接口强大，需要下载配置web3sdk。

  3）web3sdk：功能最强大，可以调用合约，开发区块链应用必选。

4. 有哪几种褡裢方式，各有什么特点：
  一键褡裢：使用更简单，适合入门小白和第一次用2.0的同学
  物料包：用法更灵活，适合熟悉fisco-bcos的用户和企业搭链用户。


#### 【系统配置】

1. 什么是系统配置？

   狭义理解，系统配置是指节点配置中一些影响账本功能，并需账本节点共识的配置项。

2. 系统配置有哪些维度？

   基于影响范围的维度分网络配置、群组配置。

3. 群组配置都是可改的吗？
   从配置项是否可改的维度，分为：
   1）节点首次启动生成创世块后不能再修改
   2）通过发交易修改配置项实现账本内一致
   3）修改自身配置文件后，节点重启生效

4. 群组配置用户可以改的涉及哪些配置？
   涉及2块：
   1）交易共识动态可改：[consensus].max_trans_num，[consensus].node.X，[tx].gas_limit。
   2）配置改后重启可改：[sync].idle_wait_ms，[tx_pool].limit

5. 群组配置更改途径是什么？
   交易共识动态可改可以通过控制修改。控制台进入路径：~/web3sdk/dist/bin/web3sdk -c <groupID>, [consensus].max_trans_num，[tx].gas_limit使用接口setSystemConfigByKey(ssc)更改，对于的配置项为tx_count_limit，tx_gas_limit。具体参见ssc -h 。[consensus].node.X的更改涉及到节点管理，控制台接口涉及到addMiner(am)，addObserver(ao)，removeNode(rn)，具体参考《节点管理》。

6. 群组配置查询途径是什么？
    交易共识动态可改群组配置项查询除了控制台外，还可以通过RPC接口查询。控制台详细使用方法请参考《SDK控制台》，RPC详细使用方法请参考《FISCO BCOS2.0 JSON-RPC 接口》。

7. 群组配置的本地配置是什么？
   群组配置改后重启可改项就是本地配置，nodeX/conf下的group.*.ini文件，更改重启生效。涉及配置项为[sync].idle_wait_ms（设置同步线程启动同步区块状态发送的时间间隔），[tx_pool].limit（交易池容量），[consensus].ttl(节点转发数)。  

#### 【控制台】

1. 什么是控制台？
   SDK控制台是FISCO BCOS2.0重要的交互式客户端工具，通过复用Java SDK的证书连接区块链节点。SDK控制台拥有丰富的命令，可以查询区块链状态，管理区块链节点，部署并调用合约等功能。

2. 控制台帮助支持大小写吗？
   不支持，只支持小写。

3. 加入记账列表或观察者列表报错：nodeID is not in network。
   节点加入记账列表和观察者列表的节点必须是连接peer的nodeID列表里面的成员。

4. 删除节点操作报错：nodeID is not in group peers。
   节点删除操作中的节点必须是getGroupPeers里面展示的group的peers。

5. 被删除的节点，是否可以同步group数据？
   被删除的节点将不会再参与group内的共识、同步和出块，被删除的节点可以通过am/ao命令可以将退出的节点添加为共识/观察节点。

6. 控制台返回超时处理需注意事项。
	切记不要立即做重复的操作，请认真确认交易是否操作成功，以免出现多笔交易。

7. 设置和查询系统配置注意事项。
   max_trans_num和gas_limit两个系统配置是写在tx_count_limit/tx_gas_limit表里的，手动修改group.X.genesis文件系统是无法动态获取的，只能通过控制台setSystemConfigByKey设置。

8. sdk配置文件在一个群组内配置多个节点连接时，为啥查询控制台返回的信息不一致？
   当sdk配置文件在一个群组内配置多个节点连接时，由于群组内的某些节点在操作过程中可能退出群组，因此sdk轮询节点查询时，其返回信息可能不一致，属于正常现象。建议使用控制台时，配置一个节点或者保证配置的节点始终在群组中，这样在同步时间内查询的群组内信息保持一致。

9. 统一节点属于不同的group，是否可以支持查询多group的信息。
   可以，在进入控制台时，输入要查看的groupID:  ./web3sdk -c [groupID]

#### 【Raft算法】

1. 什么是Raft算法
 RAFT算法采用2F+1<=N算法来计算冗余；其中F表示加入和合约组网中节点进程挂起或者僵尸数，N代表group内记账列表内节点总数，满足对应算法后，系统能正常的共识、同步和出快；

2. 使用build_chain.sh脚本搭建Raft算法环境需要注意
 在使用build_chain.sh脚本搭建group后，需要在节点进程启动前修改group.X.genesis文件的consensus_type为raft。

3. 在Raft算法的环境中只有leader才会出块，当group下节点较多时，如何快速找到leader节点。
  进入控制台执行getConsensusStatus(gcs) 命令，可以通过leaderId和leaderIdx快速的查出当前group下那个节点是leader。  

#### 【节点准入管理】

1. 2.0节点准入机制是什么
 2.0引入group即账本概念，节点分为group节点和网络节点两种。只有成为group节点才能参与共识和出块，要成为group节点的前提是成为链所在网中的网络节点。
2. group下观察节点和记账节点的区别
 观察节点作为group账本下的观察者，能被动同步出块节点的数据，但是没有权限作为出块节点。记账节点除了具有观察者权限，还具有作为出块者的权限。
3. 新节点扩容加入链网络启动失败报错：conf/group.2.genesis:can not open file
 对于新节点，报类似无法打开文件的错误，节点启动首先会初始化config.ini配置，这种错误一般是读取到config.ini有配置conf/group.2.genesis，但是实际上目录下没有这个文件导致。
 一般节点要加入哪个组，config.ini就配置哪个组，上述报错是因为额外配置了group.2.genesis。直接在config.ini删除该行配置。
4. 节点黑名单设置导致链无法正常出块
 2.0节点黑名单管理实现指定节点之间连接开放与限制，两个节点设置黑名单后，此两节点间无法通信和共识，可能导致链无法正常运行。
5. 4个节点，A B C 三个节点依次设置黑名单，导致链出块异常  
  原因是节点间转发机制异常，需要在conf下设置group.1.ini配置增加ttl参数。

#### 【CNS 合约服务】

1. 2.0 CNS合约服务特性
 通过web3sdk实现合约接口调用，实现合约部署和调用，使用CNS表管理合约

2. 合约如何编译
  当前使用solidity编写好合约后，需要使用web3sdk编译成*.java格式的java类再执行

3. 合约编译后使用控制台无法部署合约的问题
  合约编译后，会产生一个新的temp目录，需要将web3sdk再次执行gradle build操作打包一次

4. 如何查询合约cns表
 通过web3sdk控制台指令查询，查询指令根据合约name查询。

5. 如何将合约纳入CNS管理
  在部署合约时，调用CNS合约接口，将合约name、version、address信息写入CNS表中

#### 【国密】

1. 国密有什么作用

  国密即[国家密码局](https://www.baidu.com/s?wd=%E5%9B%BD%E5%AE%B6%E5%AF%86%E7%A0%81%E5%B1%80&tn=24004469_oem_dg&rsv_dl=gh_pl_sl_csd)认定的国产密码算法，符合中国密码安全要求。

2. 国密和非国密版本的区别有哪几方面：
  编译版本，证书，落盘加密，solidity编译java，web3sdk使用.