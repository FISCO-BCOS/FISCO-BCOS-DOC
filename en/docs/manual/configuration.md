# Configuration files and configuration items

FISCO BCOS supports multiple ledger. Each chain includes multiple unique ledgers, whose data among them are isolated from each other. And the transaction processing among groups are also isolated. Each node includes a main configuration `config.ini` and multiple ledger configurations `group.group_id.genesis`, `group.group_id.ini`.

- `config.ini`: The main configuration file, mainly configures with RPC, P2P, SSL certificate, ledger configuration file path, compatibility and other information.

- `group.group_id.genesis`：group configurations file. All nodes in the group are consistent. After node launches, you cannot manually change the configuration including items like group consensus algorithm, storage type, and maximum gas limit, etc.
- `group.group_id.ini`：group variable configuration file, including the transaction pool size, etc.. All configuration changes are effective after node restarts.

## Main configuration file config.ini

`config.ini` uses `ini` format. It mainly includes the configuration items like ** rpc, p2p, group, secure and log **.


```eval_rst
.. important::
    - The public IP addresses of the cloud host are virtual IP addresses. If listen_ip/jsonrpc_listen_ip/channel_listen_ip is filled in external network IP address, the binding fails. You must fill in 0.0.0.0.

    - RPC/P2P/Channel listening port must be in the range of 1024-65535 and cannot conflict with other application listening ports on the machine.

    - In order to facilitate development and experience, the reference configuration of listen_ip/channel_listen_ip is `0.0.0.0`. For security reasons, please modify it to a safe listening address according to the actual business network situation, such as the internal IP or a specific external IP
```

### Configure RPC

- `channel_listen_ip`: Channel listening IP, to facilitate node and SDK cross-machine deployment, the default setting is `0.0.0.0`;

- `jsonrpc_listen_ip`：RPC listening IP, security considerations, the default setting is 127.0.0.1, if there is an external network access requirement, please monitor **node external network IP** or `0.0.0.0`;

- `channel_listen_port`: Channel port, is corresponding to `channel_listen_port` in [Web3SDK](../sdk/sdk.html#id2) configuration;

- `jsonrpc_listen_port`: JSON-RPC port.

```eval_rst
.. note::
    For security and ease of use consideration, the latest configuration of v2.3.0 version splits listen_ip into jsonrpc_listen_ip and channel_listen_ip, but still retains the parsing function of listen_ip:

     - Include only listen_ip in the configuration: The listening IPs of both RPC and Channel are configured listen_ip
     - The configuration also contains listen_ip, channel_listen_ip, or jsonrpc_listen_ip: Priority is given to channel_listen_ip and jsonrpc_listen_ip. Configuration items that are not configured are replaced with the value of listen_ip
```


RPC configuration example is as follows:

```ini
[rpc]
    channel_listen_ip=0.0.0.0
    jsonrpc_listen_ip=127.0.0.1
    channel_listen_port=30301
    jsonrpc_listen_port=30302
```

### Configure P2P

```eval_rst
.. note::
    In order to facilitate development and experience, the reference configuration of listen_ip is `0.0.0.0`. For security reasons, please modify it to a safe listening address according to the actual business network situation, such as the internal IP or a specific external IP.
```

The current version of FISCO BCOS must be configured with `IP` and `Port` of the connection node in the `config.ini` configuration. The P2P related configurations include:

- `listen_ip`: P2P listens for IP, to set `0.0.0.0` by default. 
- `listen_port`: Node P2P listening port.  
- `node.*`: All nodes' `IP:Port` or `DomainName:Port` which need to be connected to node. This option supports domain names, but suggests users who need to use it [manually compile source code](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html#id2).  
- `enable_compress`: Enable network compression configuration option. Configuring to true, indicates that network compression is enabled. Configuring to false, indicates that network compression is disabled. For details on network compression, please refer to [here](../design/features/network_compress .md).

P2P configuration example is as follows:

```ini
[p2p]
    listen_ip=0.0.0.0
    listen_port=30300
    node.0=127.0.0.1:30300
    node.1=127.0.0.1:30304
    node.2=127.0.0.1:30308
    node.3=127.0.0.1:30312
```

### Configure ledger file path

`[group]`To configure all group configuration paths which this node belongs:

- `group_data_path`: Group data storage path.
- `group_config_path`: Group configuration file path.

> Node launches group according to all `.genesis` suffix files in the `group_config_path` path.

```ini
[group]
    ; All group data is placed in the node's data subdirectory
    group_data_path=data/
    ; Program automatically loads all .genesis files in the path
    group_config_path=conf/
```

### Configure certificate information

For security reasons, communication among FISCO BCOS nodes uses SSL encrypted communication.`[network_security]` configure to SSL connection certificate information:

- `data_path`：Directory where the certificate and private key file are located.
- `key`: The `data_path` path that node private key relative to.
- `cert`: The `data_path` path that certificate `node.crt` relative to.
- `ca_cert`: ca certificate file path.
- `ca_path`: ca certificate folder, required for multiple ca.

```ini
[network_security]
    data_path=conf/
    key=node.key
    cert=node.crt
    ca_cert=ca.crt
    ;ca_path=
```

### Configure blacklist

For preventing vice, FISCO BCOS allows nodes to configure untrusted node blacklist to reject establishing connections with these blacklist nodes. To configure blacklist through `[crl]`:

> `crl.idx`: Blacklist node's Node ID, can get from `node.nodeid` file; `idx` is index of the blacklist node.

For details of the blacklist, refer to [CA Blacklist].(./certificate_list.md)

Blacklist configuration example is as follows:

```ini
; certificate blacklist
[crl]
    crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e
3787c338a4
```

### Configure log information

FISCO BCOS supports [boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html). Configurations:

- `enable`: Enable/disable log. Set to `true` to enable log; set to `false` to disable log. **set to true by default. For performance test, to set this option to `false` to reduce the impact of print log on test results**
- `log_path`:log file patch.
- `level`: log level, currently includes 5 levels which are `trace`、`debug`、`info`、`warning`、`error`.After setting a certain log level, the log file will be entered with a log equal to or larger than this level.The log level is sorted from large to small by `error > warning > info > debug > trace`.
- `max_log_file_size`：Maximum size per log file, ** unit of measure is bytes, default is 200MB**
- `flush`：boostlog enables log auto-refresh by default. To improves system performance, it is recommended to set this value to false.

boostlog configuration example is as follows:

```ini
[log]
    ; whether to enable log, set to true by default
    enable=true
    log_path=./log
    level=info
    ; Maximum size per log file, default is 200MB
    max_log_file_size=200
    flush=true
```

### Configure node compatibility

All versions of FISCO BCOS 2.0+ are forward compatible. You can configure the compatibility of node through `[compatibility]` in `config.ini`. The tool will be automatically generated when changing the configuration item to build chain, so users do not need to change it.


- `supported_version`：The version of the current node running

```eval_rst
.. important::
    -  view the latest version of FISCO BCOS currently supports through the command  ``./fisco-bcos --version | grep "Version" ``

    - In the blockchain node configuration generated by build_chain.sh, supported_version is configured to the current latest version of FISCO BCOS
    - When upgrading an old node to a new node, directly replace the old FISCO BCOS binary with the latest FISCO BCOS binary.

```

`FISCO BCOS 2.2.0` node's `[compatibility]` configuration is as follows:

```ini

[compatibility]
    supported_version=2.2.0
```

### Optional configuration: Disk encryption
In order to protect node data, FISCO BCOS introduces [Disk Encryption](../design/features/storage_security.md) to ensure confidentiality. **Disk Encryption** Operation Manual [Reference](./storage_security.md).

`storage_security` in `config.ini` is used to configure disk encryption. It mainly includes (for the operation of the disk encryption, please refer to [Operation Manual](./storage_security.md)):


- `enable`：whether to launch disk encryption, not to launch by default;

- `key_manager_ip`：[Key Manager](https://github.com/FISCO-BCOS/key-manager)service's deployment IP;

- `key_manager_port`：[Key Manager](https://github.com/FISCO-BCOS/key-manager)service's listening port；

- `cipher_data_key`: ciphertext of node data encryption key. For `cipher_data_key` generation, refer to [disk encryption operation manual](./storage_security.md).


disk encryption configuration example is as follows:

```ini
[storage_security]
enable=true
key_manager_ip=127.0.0.1
key_manager_port=31443
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

## Group system configuration instruction

Each group has unique separate configuration file, which can be divided into **group system configuration** and **group variable configuration** according to whether it can be changed after launch.
group system configuration is generally located in the `.genesis` suffix configuration file in node's `conf` directory.

For example:`group1` system configuration generally names as `group.1.genesis`. Group system configuration mainly includes the related configuration of **group ID、consensus, storage and gas**.

```eval_rst
.. important::
    When configuring the system configuration, you need to pay attention to:

    - **configuration group must be consistent**: group system configuration is used to generate the genesis block (block 0), so the configurations of all nodes in the group must be consistent.

    - **node cannot be modified after launching** ：system configuration has been written to the system table as genesis block, so it cannot be modified after chain initializes.

    - After chain is initialized, even if genesis configuration is modified, new configuration will not take effect, and system still uses the genesis configuration when initializing the chain.

    - Since genesis configuration requires all nodes in the group to be consistent, it is recommended to use `build_chain <build_chain.html>`_ to generate the configuration.
```

### Group configuration

`[group]`configures **group ID**. Node initializes the group according to the group ID.

group2's configuration example is as follows:

```ini
[group]
id=2
```

### Consensus configuration

`[consensus]` involves consensus-related configuration, including:

- `consensus_type`：consensus algorithm type, currently supports [PBFT](../design/consensus/pbft.md), [Raft](../design/consensus/raft.md) and RPBFT. To use PBFT by default;

- `max_trans_num`：a maximum number of transactions that can be packed in a block. The default is 1000. After the chain is initialized, the parameter can be dynamically adjusted through [Console](./console.html#setsystemconfigbykey);

- `node.idx`：consensus node list, has configured with the [Node ID] of the participating consensus nodes. The Node ID can be obtained by the `${data_path}/node.nodeid` file (where `${data_path}` can be obtained by the configuration item `[secure].data_path` of the main configuration `config.ini`)


FISCO BCOS v2.3.0引入了RPBFT共识算法，具体可参考[这里](../design/consensus/rpbft.md)，RPBFT相关配置如下：

FISCO BCOS v2.3.0 introduced the RPBFT consensus algorithm, The RPBFT related configuration is as follows:

- `epoch_sealer_num`：The number of nodes participating in the consensus is selected in a consensus period. The default is the total number of all consensus nodes. After the chain is initialized, this parameter can be dynamically adjusted through [Console] (./console.html#setsystemconfigbykey)
- `epoch_block_num`：The number of blocks generated in a consensus period, the default is 1000, which can be dynamically adjusted through [Console] (./console.html#setsystemconfigbykey)

```eval_rst
.. note::

    RPBFT configuration does not take effect on other consensus algorithms
```

```ini
; Consensus protocol configuration
[consensus]
    ;consensus algorithm, currently supports PBFT(consensus_type=pbft) and Raft(consensus_type=raft)
    consensus_type=pbft
    ; maximum number of transactions in a block
    max_trans_num=1000
    epoch_sealer_num=4
    epoch_block_num=1000
    ; leader node's ID lists
    node.0=123d24a998b54b31f7602972b83d899b5176add03369395e53a5f60c303acb719ec0718ef1ed51feb7e9cf4836f266553df44a1cae5651bc6ddf50
e01789233a
    node.1=70ee8e4bf85eccda9529a8daf5689410ff771ec72fc4322c431d67689efbd6fbd474cb7dc7435f63fa592b98f22b13b2ad3fb416d136878369eb41
3494db8776
    node.2=7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2
922aa0ef50
    node.3=fd6e0bfe509078e273c0b3e23639374f0552b512c2bea1b2d3743012b7fed8a9dec7b47c57090fa6dcc5341922c32b89611eb9d967dba5f5d07be7
4a5aed2b4a
```

### State mode configuration

[state](../design/storage/mpt.html) is used to store blockchain status information. It locates in the genesis file `[state]`:

- `type`: state type, currently supports [storage state](../design/storage/storage.html#id6) and [MPT state](../design/storage/mpt.html), **defaults to Storage state**. storage state storing the transaction execution result in the system table, which is more efficient. MPT state storing the transaction execution result in the MPT tree, which is inefficient but contains complete historical information.


```eval_rst
.. important::
    The **storage state** is recommended.
```

```ini
[state]
    type=storage
```

### Gas configuration

FISCO BCOS is compatible with Ethereum virtual machine ([EVM](../design/virtual_machine/evm.md)). In order to prevent DOS from attacking [EVM](../design/virtual_machine/evm.md), EVM introduces the concept of gas when executing transactions, which is used to measure the computing and storage resources consumed during the execution of smart contracts. The measure includes the maximum gas limit of transaction and block. If the gas consumed by the transaction or block execution exceeds the gas limit, the transaction or block is discarded.


FISCO BCOS is alliance chain that simplifies gas design. **It retains only maximum gas limit of transaction, and maximum gas of block is constrained together by [consensus configuration max_trans_num](./configs.html#id8) and transaction maximum gas limit.**

FISCO BCOS configures maximum gas limit of the transaction through genesis `[tx].gas_limit`. The default value is 300000000. After chain is initialized, the gas limit can be dynamically adjusted through the [console command](./console.html#setsystemconfigbykey).


```ini
[tx]
    gas_limit=300000000
```


## Ledger variable configuration instruction

Variable configuration of the ledger is located in the file of the `.ini` suffix in the node `conf` directory.

For example: `group1` variable configuration is generally named `group.1.ini`. Variable configuration mainly includes transaction pool size, PBFT consensus message forwarding TTL, PBFT consensus packing time setting, PBFT transaction packaging dynamic adjustment setting, parallel transaction settings, etc..

### Configure storage

Storage currently supports three modes: RocksDB, MySQL, and External. Users can choose the DB to use according to their needs. RocksDB has the highest performance. MySQL supports users to use MySQL database for viewing data. External accesses mysql through data proxy, and users need to start and configure the data proxy. The design documentation can be referenced [AMDB Storage Design](../design/storage/storage.html). Since the RC3 version, we have used RocksDB instead of LevelDB for better performance, but still supports LevelDB.

#### Public configuration item

```eval_rst
.. important::
   If you want to use MySQL, please set type to MySQL.
```

- `type`: The stored DB type, which supports `RocksDB`, `MySQL` and `External`. When the DB type is RocksDB, all the data of blockchain system is stored in the RocksDB local database; when the type is `MySQL` or `External`, the node accesses mysql database according to the configuration. All data of blockchain system is stored in mysql database. For accessing mysql database, to configure the amdb-proxy. Please refer to [here](./distributed_storage.html#amdb) for the amdb-proxy configuration.
- `max_capacity`: configures the space size of the node that is allowed to use for memory caching.
- `max_forward_block`: configures the space size of the node that allowed to use for memory block. When the blocks exceeds this value, the node stops the consensus and waits for the blocks to be written to database.
- `binary_log`: default is false. when set to `true`, enable binary log, and then disable the wal of rocksdb.
- `cached_storage`: controls whether to use the cache. The default is `true`.

#### Database related configuration item

- `topic`: When the type is `External`, you need to configure this field to indicate the amdb-proxy topic that blockchain system is interested in. For details, please refer to [here](./distributed_storage.html#id3).
- `max_retry`: When the type is `External`, you need to configure this field to indicate the number of retries when writing fails. For details, please refer to [here](./distributed_storage.html#id3).
- `scroll_threshold_multiple`： when the type is `scalable`, this configuration item is used to configure the handover threshold of the block database. The default value is 2, so Block data is stored in different rocksdb instances every 2000 blocks.
- `db_ip`: When the type is `MySQL`, you need to configure this field to indicate the IP address of MySQL.
- `db_port`: When the type is `MySQL`, you need to configure this field to indicate the port number of MySQL.
- `db_username`: When the type is `MySQL`, you need to configure this field to indicate the MySQL username.
- `db_passwd`: When the type is `MySQL`, you need to configure this field to indicate the password corresponding to the MySQL user.
- `db_name`: When the type is `MySQL`, you need to configure this field to indicate the database name used in MySQL.
- `init_connections`: When the type is `MySQL`, this field can be optionally configured to indicate the initial number of connections established with MySQL. The default value is 15, and it is fine to use it.
- `max_connections`: When the type is `MySQL`, this field can be optionally configured to indicate the maximum number of connections established with MySQL. The default value is 20, and it is fine to use it.


#### The following is an example of the configuration of [storage]:

```ini
[storage]
    ; storage db type, rocksdb / mysql / external, rocksdb is recommended
    type=RocksDB
    max_capacity=256
    max_forward_block=10
    ; only for external
    max_retry=100
    topic=DB
    ; only for mysql
    db_ip=127.0.0.1
    db_port=3306
    db_username=
    db_passwd=
    db_name=
```

### Transaction pool configuration

FISCO BCOS opens the transaction pool capacity configuration to users. Users can dynamically adjust the transaction pool size according to their business size requirements, stability requirements, and node hardware configuration.

Transaction pool configuration example is as follows:

```ini
[tx_pool]
    limit=150000
```

### PBFT consensus configurations
In order to improve the performance, availability, and network efficiency of the PBFT algorithm, FISCO BCOS has made a series of optimizations for block packaging algorithms and networks, including PBFT block packaging dynamic adjustment strategies, PBFT message forwarding optimization, and PBFT Prepare packet structure optimization.

```eval_rst
.. note::

    Due to protocol and algorithm consistency requirements, it is recommended to ensure that the PBFT consensus configuration of all nodes is consistent.

```

#### PBFT consensus message broadcast configuration

In order to ensure the maximum network fault tolerance of the consensus process, each consensus node broadcasts the message to other nodes after receiving a valid consensus message. In smooth network environment, the consensus message forwarding mechanism will waste additional network bandwidth, so the `ttl` is introduced in the group variable configuration item to control the maximum number of message forwarding. The maximum number of message forwarding is `ttl-1`, and **the configuration item is valid only for PBFT**.


Setting consensus message to be forwarded at most once configuration example is as follows:

```ini
; the ttl for broadcasting pbft message
[consensus]
ttl=2
```

#### PBFT consensus packing time configuration

The PBFT module packing too fast causes only 1 to 2 transactions to be pack in some blocks. For avoiding wasting storage space, FISCO BCOS v2.0.0-rc2 introduces `min_block_generation_time` configuration item in the group variable configuration `group.group_id.ini`'s `[consensus]` to manager the minimum time for PBFT consensus packing. That is, when the consensus node packing time exceeds `min_block_generation_time` and the number of packaged transactions is greater than 0, the consensus process will start and handle the new block generated by the package.


```eval_rst
.. important::
   - ``min_block_generation_time`` is 500ms by default
   - The longest packing time of consensus node is 1000ms. If the time is exceeded 1000ms and the number of transactions packed in the new block is still 0, the consensus module will enter the logic of empty block generation, and the empty block will not be written to disk;
   - ``min_block_generation_time`` cannot exceed the time of empty block generation which is 1000ms. If the set value exceeds 1000ms, the system defaults min_block_generation_time to be 500ms.

```

```ini
[consensus]
;min block generation time(ms), the max block generation time is 1000 ms
min_block_generation_time=500
```

#### PBFT transaction package dynamic adjustment


For the impact causing by CPU loading and network latency on system processing power, PBFT provides an algorithm that dynamically adjusts the maximum number of transactions that can be packed in a block. The algorithm dynamically can adjust the maximum number of transactions according to the state of historical transaction processing. The algorithm is turned on by default, and it can be turned off by changing the `[consensus].enable_dynamic_block_size` configuration item of the variable configuration `group.group_id.ini` to `false`. At this time, the maximum number of transactions in the block is the `[consensus].max_trans_num` of `group.group_id.genesis`.

The configuration of closing the dynamic adjustment algorithm for the block package transaction number is as follows:

```ini
[consensus]
    enable_dynamic_block_size=false
```

#### PBFT message forwarding configuration

FISCO BCOS v2.2.0 optimizes the PBFT message forwarding mechanism to ensure that PBFT message packets can reach each consensus node as much as possible in the network disconnection scenario, while reducing redundant PBFT message packets in the network. For PBFT message forwarding optimization strategies. You can use the `[consensus].enable_ttl_optimization` configuration item of `group.group_id.ini` to enable or disable the PBFT message forwarding optimization strategy.


- `[consensus].enable_ttl_optimization` is configured as `true`: Enable PBFT message forwarding optimization strategy
- `[consensus].enable_ttl_optimization` is configured as `false`: Disable PBFT message forwarding optimization strategy
- When `supported_version` is not less than v2.2.0, the PBFT message forwarding strategy is enabled by default; when` supported_version` is less than v2.2.0, the PBFT message forwarding optimization strategy is disabled by default

Disable PBFT message forwarding optimization strategy configuration as follows：

```ini
[consensus]
    enable_ttl_optimization=false
```

#### PBFT Prepare package structure optimization

Considering that in the PBFT algorithm, transactions in blocks in the Prepare packet broadcast by the Leader have a high probability of hitting in the transaction pools of other consensus nodes. In order to save network bandwidth, FISCO BCOS v2.2.0 has optimized the Prepare packet structure: The block only contains a list of transaction hashes. After other consensus nodes receive the Prepare packet, they will first obtain the hit transaction from the local transaction pool and request the missing transaction from Leader. This policy can be enabled or disabled through the `[consensus].enable_prepare_with_txsHash` configuration item of `group.group_id.ini`.

- `[consensus].enable_prepare_with_txsHash` is configured as `true`：Enable the structure optimization of the Prepare package. The blocks in the Prepare message package only contain the transaction hash list.
- `[consensus].enable_prepare_with_txsHash` is configured as `false`：Disable the structure optimization of the Prepare packet, the block in the Prepare message packet contains the full amount of transactions
- When `supported_version` is not less than v2.2.0,` [consensus].enable_prepare_with_txsHash` defaults to `true`; when `supported_version` is less than v2.2.0, `[consensus].enable_prepare_with_txsHash` defaults to `false`

```eval_rst
.. note::
    Due to protocol consistency requirements, all nodes must ensure `enable_prepare_with_txsHash` configuration is consistent

```

Disable the PBFT Prepare package structure optimization configuration as follows:

```ini
[consensus]
    enable_prepare_with_txsHash=false
```

### RPBFT consensus configurations

FISCO BCOS v2.3.0 introduces the RPBFT consensus algorithm. In order to ensure the load balance of the network traffic of the RPBFT algorithm, the tree broadcast policy of the Prepare packet is introduced, Corresponding fault tolerance scheme.

- `[consensus].broadcast_prepare_by_tree`: Enable/disable switch for Prepare tree broadcast policy. Set to `true` to enable the tree broadcast policy for Prepare packets. Set to` false` to disable the tree broadcast policy for Prepare packets. Default is `true`.

The following is the fault-tolerant configuration after the Prepare packet tree broadcast policy is enabled:

- `[consensus].prepare_status_broadcast_percent`: The percentage of the randomly selected nodes that receive the prepare status, The value ranges from 25 to 100, and the default is 33.

- `[consensus].max_request_prepare_waitTime`：When the node's Prepare cache is missing, the longest delay for waiting for the parent node to send a Prepare packet is 100ms by default. After this delay, the node will request from other nodes that own the Prepare packet.

The following is the configuration of load balancing after enabling Prepare package structure optimization in RPBFT mode:

- `[consensus].max_request_missedTxs_waitTime`: After the transaction in the node's Prepare packet is missing, the longest delay for waiting for the parent node or other non-leader node to synchronize the Prepare packet status is 100ms by default, if the packet status is synchronized to the parent node or non-leader node within the waiting delay window, a random node will be selected to request the missing transaction, otherwise, directly request the missing transaction from the leader.

RPBFT default configuration is as follows:

```ini
; Tree broadcast policy for Prepare packets is enabled by default
broadcast_prepare_by_tree=true
; Only effective when the prepare package tree broadcast is enabled
; Each node randomly selects 33% consensus nodes to synchronize the prepare packet status
prepare_status_broadcast_percent=33
; prepare包树状广播策略下，缺失prepare包的节点超过100ms没等到父节点转发的prepare包，会向其他节点请求缺失的prepare包
; Under the prepare package tree broadcast strategy, 
; the node missing the prepare package takes more than 100ms and 
; does not wait for the prepare package forwarded by the parent node 
; to request the missing prepare package from other nodes.
max_request_prepare_waitTime=100
; The maximum delay for a node to wait for a parent node 
;or other non-leader node to synchronize a prepare packet is 100ms
max_request_missedTxs_waitTime=100
```

### Sync configurations

The synchronization module is a "big network consumer", including block synchronization and transaction synchronization. FISCO BCOS optimizes the efficiency of the consensus module network using the principle of load balancing.

```eval_rst
.. note::

    Due to protocol consistency requirements, it is recommended to ensure that the PBFT consensus configuration of all nodes is consistent.
```

#### Block synchronization optimization configuration

In order to enhance the scalability of the blockchain system under the condition of limited network bandwidth, FISCO BCOS v2.2.0 has optimized block synchronization. For detailed optimization strategies. You can use the `[sync].sync_block_by_tree` of `group.group_id.ini` to enable or disable the block synchronization optimization strategy.

- `[sync].sync_block_by_tree` is configured as `true`:Enable block synchronization optimization strategy
- `[sync].sync_block_by_tree` is configured as `false`: Turn off block synchronization optimization strategy
- When `supported_version` is not less than v2.2.0, `[sync].sync_block_by_tree` defaults to `true`; when `supported_version` is less than v2.2.0,` [sync].sync_block_by_tree` defaults to `false`

In addition, in order to ensure the robustness of tree topology block synchronization, FISCO BCOS v2.2.0 also introduced the gossip protocol to periodically synchronize the block status. The related configuration items of the gossip protocol are located in `[sync]` of `group.group_id.ini` The details are as follows:

- `gossip_interval_ms`：gossip protocol synchronization block status period, default is 1000ms
- `gossip_peers_number`：Each time a node synchronizes the block status, the number of randomly selected neighbor nodes, the default is 3

```eval_rst
.. note::

    1. gossip protocol configuration item, only effective when block tree broadcast optimization is enabled
    2. Must ensure that all nodes `sync_block_by_tree` configuration is consistent
```

The optimized configuration of enabling block tree broadcasting is as follows：

```ini
[sync]
    ; Block tree synchronization strategy is enabled by default
    sync_block_by_tree=true
    ; Every node synchronizes the latest block status every 1000ms
    gossip_interval_ms=1000
    ; Each node randomly selects 3 neighbor nodes at a time to synchronize the latest block status
    gossip_peers_number=3
```

#### Optimal configuration of transaction tree broadcast

In order to reduce the peak outbound bandwidth of SDK directly connected nodes and improve the scalability of the blockchain system, FISCO BCOS v2.2.0 introduced a transaction tree broadcast optimization strategy. You can use the `[sync].send_txs_by_tree` of `group.group_id.ini` to enable or disable the transaction tree broadcast strategy. The detailed configuration is as follows:

- `[sync].sync_block_by_tree`：Set to `true` to enable transaction tree broadcast strategy; set to` false` to disable transaction tree broadcast strategy

The configuration of the disabled transaction tree broadcast policy is as follows：

```ini
[sync]
    ; Transaction tree broadcast strategy is enabled by default
    send_txs_by_tree=false
```
    
```eval_rst
.. note::
    - Due to protocol consistency requirements, all nodes must ensure that the tree broadcast switch `send_txs_by_tree` is configured consistently
    - When `supported_version` is not less than v2.2.0, the transaction tree broadcast optimization strategy is turned on by default; when `supported_version` is less than v2.2.0, the transaction tree broadcast strategy is turned off by default
```

#### Optimized transaction forwarding configuration

In order to reduce the traffic overhead caused by transaction forwarding, FISCO BCOS v2.2.0 introduced a state packet-based transaction forwarding strategy. You can configure the maximum number of forwarding nodes for the transaction status through `[sync].txs_max_gossip_peers_num` of `group.group_id.ini`.

```eval_rst
.. note::
    To ensure that transactions reach each node and minimize the traffic overhead introduced by transaction status forwarding, it is not recommended to set `txs_max_gossip_peers_num` too small or too large, just use the default configuration
```

The maximum number of nodes for transaction status forwarding is configured as follows:

```ini
[sync]
    ; Each node randomly selects up to 5 neighbor nodes to synchronize the latest transaction status.
    txs_max_gossip_peers_num=5
```


### Parallel transaction configuration

FISCO BCOS supports execution of transactions in parallel. Turning on the transaction parallel execution switch to enable for improving throughput. **Execution of the transaction in parallel is only effective in the storage state mode**.


``` ini
[tx_execute]
    enable_parallel=true
```


## Dynamically configure system parameters

FISCO BCOS system currently includes the following system parameters (other system parameters will be extended in the future):

|  System parameters    | Defaults  | meaning |
|  ----  | ----  | ---- |
| tx_count_limit  | 1000 | Maximum number of transactions that can be packed in a block |
| tx_gas_limit  | 300000000 | Maximum gas limit for one transaction |
| rpbft_epoch_sealer_num | Total number of chain consensus nodes | RPBFT system configuration, the number of nodes participating in consensus is selected in a consensus period, and the number of nodes participating in consensus is dynamically switched in each consensus period of RPBFT |
| rpbft_epoch_block_num | 1000 | RPBFT system configuration, the number of blocks produced in a consensus period| 

Console provides **[setSystemConfigByKey](./console.html#setsystemconfigbykey)** command to modify these system parameters.
**[getSystemConfigByKey](./console.html#getsystemconfigbykey)** command can view the current value of the system parameter:


```eval_rst
.. important::

    It is not recommended to modify tx_count_limit and tx_gas_limit arbitrarily. These parameters can be modified as follows:

    - Hardware performance such as machine network or CPU is limited: to reduce tx_count_limit for reducing business pressure;

    - gas is insufficient when executing transactions for comlicated business logic: increase tx_gas_limit.

    `rpbft_epoch_sealer_num` and `rpbft_epoch_block_num` are only valid for the RPBFT consensus algorithm. In order to ensure the performance of the consensus, it is not recommended to frequently switch the consensus list dynamically, that is, it is not recommended that the `rpbft_epoch_block_num` configuration value is too small

```


```bash
# To set the maximum number of transactions of a packaged block to 500

> setSystemConfigByKey tx_count_limit 500
# inquiry tx_count_limit
> getSystemConfigByKey tx_count_limit
[500]

# To set transaction gas limit as  400000000
> getSystemConfigByKey tx_gas_limit 400000000
> getSystemConfigByKey
[400000000]

# Under the RPBFT consensus algorithm, set a consensus period to select the number of nodes participating in the consensus to 4
[group:1]> setSystemConfigByKey rpbft_epoch_sealer_num 4
Note: rpbft_epoch_sealer_num only takes effect when RPBFT is used
{
    "code":0,
    "msg":"success"
}
# query rpbft_epoch_sealer_num
[group:1]> getSystemConfigByKey rpbft_epoch_sealer_num 
Note: rpbft_epoch_sealer_num only takes effect when RPBFT is used
4

# Under the RPBFT consensus algorithm, set a consensus period to produce 10,000 blocks
[group:1]> setSystemConfigByKey rpbft_epoch_block_num 10000
Note: rpbft_epoch_block_num only takes effect when RPBFT is used
{
    "code":0,
    "msg":"success"
}
# query rpbft_epoch_block_num
[group:1]> getSystemConfigByKey rpbft_epoch_block_num
Note: rpbft_epoch_block_num only takes effect when RPBFT is used
10000
```
