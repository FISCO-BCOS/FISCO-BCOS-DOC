# Go API

标签：``go-sdk`` ``AMOP``

----

Go SDK为区块链应用开发者提供了Go API接口，以服务的形式供外部调用。

- **client**：提供访问FISCO BCOS节点[JSON-RPC](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/api.html)接口支持、提供部署及调用合约的支持；

## Client

**位置**：go-sdk/client/go_client.go

| 接口名                              | 描述                                                     | 参数                                                         |
| ----------------------------------- | -------------------------------------------------------- | ------------------------------------------------------------ |
| AsyncSendTransaction                | 异步发送一个经过签名的交易，该交易随后会被链上节点执行并共识 |已签名交易以及回调                                         |
| Call                                | 调用只读合约                                             | 合约地址<br/>调用接口*<br/>参数列表                          |
| GetBlockNumber                      | 获取最新块高                                             | 无                                                           |
| GetBlockByHash                      | 根据区块哈希获取区块信息                                 | 区块哈希 & bool                                              |
| GetBlockByNumber                    | 根据区块高度获取区块信息                                 | 区块高度 & bool                                              |
| GetBlockHashByNumber                | 根据区块高度获取区块哈希                                 | 区块高度                                                     |
| GetCode                             | 根据合约地址查询合约数据                                 | 合约地址                                                     |
| GetConsensusStatus                  | 获取区块链节点共识状态                                   | 无                                                           |
| GetContractAddress                  | 根据部署合约时产生的交易地址获取合约地址                 | 交易哈希                                                     |
| GetGroupPeers                       | 获取指定群组的共识节点和观察节点列表                     | 无                                                           |
| GetGroupList                        | 获取节点所属群组的群组ID列表                             | 无                                                           |
| GetNodeIDList                       | 获取节点及其连接节点的列表                               | 无                                                           |
| GetObserverList                     | 获取观察者节点列表                                       | 无                                                           |
| GetPbftView                         | 获取PBFT视图                                             | 无                                                           |
| GetPeers                            | 获取区块链节点的连接信息                                 | 无                                                           |
| GetSealerList                       | 获取共识节点列表                                         | 无                                                           |
| GetSystemConfigByKey                | 根据关键字获取区块链系统配置                             | 系统配置关键字，目前支持：<br/>\- tx_count_limit<br/>\- tx_gas_limit<br/>\- rpbft_epoch_sealer_num<br/>\- rpbft_epoch_block_num |
| GetSyncStatus                       | 获取区块链节点同步状态                                   | 无                                                           |
| GetTransactionByHash                | 根据交易哈希获取交易信息                                 | 交易哈希                                                     |
| GetTransactionReceipt               | 根据交易哈希获取交易回执                                 | 交易哈希                                                     |
| GetPendingTxSize                    | 获取交易池内未上链的交易数目                             | 无                                                           |
| GetTotalTransactionCount            | 获取指定群组的上链交易数目                               | 无                                                           |
| SendRawTransaction                  | 发送一个经过签名的交易，该交易随后会被链上节点执行并共识 |已签名交易                                          |
| SubscribeEventLogs                  | 监听合约事件eventlog | event参数与收到后处理的回调函数                                        |
| SubscribeTopic                  | 监听链上信使协议AMOP的topic |topic与收到后处理的回调函数                                        |
| SendAMOPMsg                  | 发送链上信使协议AMOP的消息，随机发送到某个监听此topic的SDK |topic与消息                                        |
| BroadcastAMOPMsg                  | 广播发送链上信使协议AMOP的消息，发送到所有监听此topic的SDK |topic与消息                                        |
| UnsubscribeTopic                  | 取消监听链上信使协议AMOP的topic |topic                                     |
| SubscribeBlockNumberNotify                  | 取消块高通知 |收到块高通知的回调函数                              |
| UnsubscribeBlockNumberNotify                  | 取消监听块高通知  |                           |
