## 框架

FISCO BCOS同时支持**PBFT(Practical Byzantine Fault Tolerance)** 和 **RAFT(Replication and Fault Tolerant)** 共识算法，共识模块框架如下图：

![共识模块框架](../../../images/consensus/architecture.png)



**Sealer模块**

包括PBFTSealer和RaftSealer，负责从交易池取交易，并基于节点最高块打包交易，产生新区块，交给Engine模块处理。


**Engine模块**

包括PBFTEngine和RaftEngine，负责从Sealer模块或者网络模块接收新区块，并根据从网络模块接收的共识消息包完成共识流程，最终将达成共识的新区块写入区块链(BlockChain)，区块上链后，从交易池中删除已经上链的交易。