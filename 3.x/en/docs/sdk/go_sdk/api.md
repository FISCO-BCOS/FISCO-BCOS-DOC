# Go API

Tags: "go-sdk" "AMOP"

----

The Go SDK provides a Go API interface for blockchain application developers to call externally as a service。

- **client**Provides access to FISCO BCOS nodes [JSON-RPC](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/api.html)Interface support, providing support for deploying and invoking contracts；

## Client

**Location**：go-sdk/client/go_client.go

| Interface Name| 描述| Parameters|
| ----------------------------------- | -------------------------------------------------------- | ------------------------------------------------------------ |
| AsyncSendTransaction                | Asynchronously sends a signed transaction, which is then executed and agreed upon by nodes on the chain|Signed transactions and callbacks|
| Call                                | Call read-only contract| Contract Address<br/>Call Interface*<br/>Parameter List|
| GetBlockNumber                      | Get Latest Block High| None|
| GetBlockByHash                      | Obtain block information based on block hash| Block Hash & bool|
| GetBlockByNumber                    | Obtain block information according to block height| Block height & bool|
| GetBlockHashByNumber                | Obtain block hash based on block height| Block height|
| GetCode                             | Query contract data based on contract address| Contract Address|
| GetConsensusStatus                  | Get blockchain node consensus status| None|
| GetContractAddress                  | Obtain the contract address based on the transaction address generated when the contract is deployed| Transaction Hash|
| GetGroupPeers                       | Obtain the list of consensus nodes and observation nodes of a specified group| None|
| GetGroupList                        | Obtain the group ID list of the group to which the node belongs| None|
| GetNodeIDList                       | Get a list of nodes and their connected nodes| None|
| GetObserverList                     | Get Observer Node List| None|
| GetPbftView                         | Get PBFT View| None|
| GetPeers                            | Obtain the connection information of a blockchain node| None|
| GetSealerList                       | Get Consensus Node List| None|
| GetSystemConfigByKey                | Obtain blockchain system configuration based on keywords| System configuration keywords, currently supported:<br/>\- tx_count_limit<br/>\- tx_gas_limit<br/>\- rpbft_epoch_sealer_num<br/>\- rpbft_epoch_block_num |
| GetSyncStatus                       | Obtain the synchronization status of a blockchain node| None|
| GetTransactionByHash                | Get transaction information based on transaction hash| Transaction Hash|
| GetTransactionReceipt               | Get transaction receipt based on transaction hash| Transaction Hash|
| GetPendingTxSize                    | Get the number of unchained transactions in the transaction pool| None|
| GetTotalTransactionCount            | Obtains the number of transactions on the chain of a specified group| None|
| SendRawTransaction                  | Send a signed transaction, which is then executed and agreed upon by the nodes on the chain|Signed transactions|
| SubscribeEventLogs                  | Listen for contract events eventlog| The event parameter and the callback function of the received post-processing|
| SubscribeTopic                  | Listen to the topic of the on-chain messenger protocol AMOP|topic and the callback function of the received post-processing|
| SendAMOPMsg                  | Send an AMOP message to an SDK that listens to this topic|topic and message|
| BroadcastAMOPMsg                  | Broadcast and send messages of the on-chain messenger protocol AMOP to all SDKs that listen to this topic|topic and message|
| UnsubscribeTopic                  | Cancel listening to the topic of the on-chain messenger protocol AMOP|topic                                     |
| SubscribeBlockNumberNotify                  | Cancel Block High Notification|Callback function to receive block high notification|
| UnsubscribeBlockNumberNotify                  | Cancel listening block high notification|                           |
