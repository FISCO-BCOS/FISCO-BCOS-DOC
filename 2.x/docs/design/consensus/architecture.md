## 框架

标签：``共识算法`` ``设计方案``

----

FISCO BCOS实现了一套可扩展的共识框架，可插件化扩展不同的共识算法，目前支持 **PBFT(Practical Byzantine Fault Tolerance)** 和 **Raft(Replication and Fault Tolerant)** 共识算法，共识模块框架如下图：

![](../../../images/consensus/architecture.png)



**Sealer线程**

交易打包线程，负责从交易池取交易，并基于节点最高块打包交易，产生新区块，产生的新区块交给Engine线程处理，PBFT和Raft的交易打包线程分别为PBFTSealer和RaftSealer。



**Engine线程**

共识线程，负责从本地或通过网络接收新区块，并根据接收的共识消息包完成共识流程，最终将达成共识的新区块写入区块链(BlockChain)，区块上链后，从交易池中删除已经上链的交易，PBFT和Raft的共识线程分别为PBFTEngine和RaftEngine。
