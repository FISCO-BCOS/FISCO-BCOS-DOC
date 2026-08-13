# rPBFT使用说明

FISCO BCOS v2.3.0引入了rPBFT共识算法，具体可参考[这里](../design/consensus/rpbft.md)，rPBFT相关配置如下：

FISCO BCOS开启rPBFT有两种方式，为节点部署时指定rPBFT共识，以及通过控制台将节点共识更改为rPBFT

## 节点部署时指定

节点部署时，修改节点`config.genesis`中的`[consensus]`

解释如下：

- `max_trans_num`：一个区块可打包的最大交易数，默认是1000，链初始化后，可通过[控制台](../operation_and_maintenance/console/console_commands.md#setsystemconfigbykey)动态调整该参数；

- `consensus_timeout`：PBFT共识过程中，每个区块执行的超时时间，默认为3s，单位为秒，可通过[控制台](../operation_and_maintenance/console/console_commands.md#setsystemconfigbykey)动态调整该参数；

- `node.idx`：共识节点列表，配置了参与共识节点的[Node ID](../design/consensus/pbft.html#id1)，节点的Node ID可通过`${data_path}/node.nodeid`文件获取(其中`${data_path}`可通过主配置`config.ini`的`[network_security].data_path`配置项获取)

FISCO BCOS v2.3.0引入了rPBFT共识算法，具体可参考[这里](../design/consensus/rpbft.md)，rPBFT相关配置如下：

- `epoch_sealer_num`：一个共识周期内选择参与共识的节点数目，默认是所有共识节点总数，链初始化后可通过[控制台](../operation_and_maintenance/console/console_commands.md#setsystemconfigbykey)动态调整该参数；
- `epoch_block_num`：一个共识周期出块数目，默认为1000，可通过[控制台](../operation_and_maintenance/console/console_commands.md#setsystemconfigbykey)动态调整该参数；


```eval_rst
.. note::

    rPBFT配置对其他共识算法不生效。
```

配置节点开启rPBFT共识算法如下：
```ini
; 共识协议配置
[consensus]
    ; 共识算法，目前支持PBFT(consensus_type=pbft), Raft(consensus_type=raft)和rPBFT(consensus_type=rpbft)
    consensus_type=rpbft
    ; 单个块最大交易数
    max_trans_num=1000
    ; 一个共识周期内选取参与共识的节点数，rPBFT配置项，对其他共识算法不生效
    epoch_sealer_num=4
    ; 一个共识周期出块数，rPBFT配置项，对其他共识算法不生效
    epoch_block_num=1000
    ; leader节点的ID列表
    node.0=123d24a998b54b31f7602972b83d899b5176add03369395e53a5f60c303acb719ec0718ef1ed51feb7e9cf4836f266553df44a1cae5651bc6ddf50e01789233a
    node.1=70ee8e4bf85eccda9529a8daf5689410ff771ec72fc4322c431d67689efbd6fbd474cb7dc7435f63fa592b98f22b13b2ad3fb416d136878369eb413494db8776
    node.2=7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
    node.3=fd6e0bfe509078e273c0b3e23639374f0552b512c2bea1b2d3743012b7fed8a9dec7b47c57090fa6dcc5341922c32b89611eb9d967dba5f5d07be74a5aed2b4a
```

### rPBFT共识配置

FISCO BCOS v2.3.0引入rPBFT共识算法，具体可参考[这里](../design/consensus/rpbft.md)，为保证rPBFT算法网络流量负载均衡，引入了Prepare包树状广播策略以及该策略相对应的容错方案。

- `[consensus].broadcast_prepare_by_tree`：Prepare包树状广播策略开启/关闭开关，设置为`true`，开启Prepare包树状广播策略；设置为`false`，关闭Prepare包树状广播策略，默认为`true`

下面为开启Prepare包树状广播策略后的容错配置：
- `[consensus].prepare_status_broadcast_percent`：Prepare状态包随机广播的节点占共识节点总数的百分比，取值在25到100之间，默认为33
- `[consensus].max_request_prepare_waitTime`：节点Prepare缓存缺失时，等待父节点发送Prepare包的最长时延，默认为100ms，超过这个时延后，节点会向其他拥有该Prepare包的节点请求


下面为rPBFT模式下开启Prepare包结构优化后，负载均衡相关配置：

- `[consensus].max_request_missedTxs_waitTime`：节点Prepare包内交易缺失后，等待父节点或其他非leader节点同步Prepare包状态的最长时延，默认为100ms，若在等待时延窗口内同步到父节点或非leader节点Prepare包状态，则会随机选取一个节点请求缺失交易；若等待超时，直接向leader请求缺失交易。


rPBFT默认配置如下:
```ini
; 默认开启Prepare包树状广播策略
broadcast_prepare_by_tree=true
; 仅在开启prepare包树状广播时生效
; 每个节点随机选取33%共识节点同步prepare包状态
prepare_status_broadcast_percent=33
; prepare包树状广播策略下，缺失prepare包的节点超过100ms没等到父节点转发的prepare包，会向其他节点请求缺失的prepare包
max_request_prepare_waitTime=100
; 节点等待父节点或其他非leader节点同步prepare包最长时延为100ms
max_request_missedTxs_waitTime=100
```

## 使用控制台更换共识算法

FISCO BCOS系统目前主要包括如下系统参数(未来会扩展其他系统参数)：

|  系统参数    | 默认值  | 含义 |
|  ----  | ----  | ---- |
| tx_count_limit  | 1000 | 一个区块中可打包的最大交易数目 |
| tx_gas_limit  | 300000000 | 一个交易最大gas限制 |
| rpbft_epoch_sealer_num | 链共识节点总数 | rPBFT系统配置，一个共识周期内选取参与共识的节点数目，rPBFT每个共识周期都会动态切换参与共识的节点数目 |
| rpbft_epoch_block_num | 1000 | rPBFT系统配置，一个共识周期内出块数目|
| consensus_timeout | 3 | PBFT共识过程中，区块执行的超时时间，最少为3s, supported_version>=v2.6.0时，配置项生效 |

控制台提供 **[setSystemConfigByKey](../operation_and_maintenance/console/console_commands.md#setsystemconfigbykey)** 命令来修改这些系统参数，**[getSystemConfigByKey](../operation_and_maintenance/console/console_commands.md#getsystemconfigbykey)** 命令可查看系统参数的当前值：

```eval_rst
.. important::

    不建议随意修改tx_count_limit和tx_gas_limit，如下情况可修改这些参数：

    - 机器网络或CPU等硬件性能有限：调小tx_count_limit，或降低业务压力；
    - 业务逻辑太复杂，执行交易时gas不足：调大tx_gas_limit。

    `rpbft_epoch_sealer_num` 和 `rpbft_epoch_block_num` 仅对rPBFT共识算法生效，为了保障共识性能，不建议频繁动态切换共识列表，即不建议 `rpbft_epoch_block_num` 配置值太小
```


```bash
# 设置一个区块可打包最大交易数为500
[group:1]> setSystemConfigByKey tx_count_limit 500
# 查询tx_count_limit
[group:1]> getSystemConfigByKey tx_count_limit
[500]

# 设置交易gas限制为400000000
[group:1]> setSystemConfigByKey tx_gas_limit 400000000
[group:1]> getSystemConfigByKey tx_gas_limit
[400000000]

# rPBFT共识算法下，设置一个共识周期选取参与共识的节点数目为4
[group:1]> setSystemConfigByKey rpbft_epoch_sealer_num 4
Note: rpbft_epoch_sealer_num only takes effect when rPBFT is used
{
    "code":0,
    "msg":"success"
}
# 查询rpbft_epoch_sealer_num
[group:1]> getSystemConfigByKey rpbft_epoch_sealer_num
Note: rpbft_epoch_sealer_num only takes effect when rPBFT is used
4

# rPBFT共识算法下，设置一个共识周期出块数目为10000
[group:1]> setSystemConfigByKey rpbft_epoch_block_num 10000
Note: rpbft_epoch_block_num only takes effect when rPBFT is used
{
    "code":0,
    "msg":"success"
}
# 查询rpbft_epoch_block_num
[group:1]> getSystemConfigByKey rpbft_epoch_block_num
Note: rpbft_epoch_block_num only takes effect when rPBFT is used
10000
# 获取区块执行超时时间
[group:1]> getSystemConfigByKey consensus_timeout
3

# 设置区块执行超时时间为5s
[group:1]> setSystemConfigByKey consensus_timeout 5
{
    "code":0,
    "msg":"success"
}

# 设置rPBFT密钥为secp256k1曲线
[group:1]> setSystemConfigByKey feature_rpbft_vrf_type_secp256k1 1
{
    "code":0,
    "msg":"success"
}
```
