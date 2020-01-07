# Configuration files and configuration items

FISCO BCOS supports multiple ledger. Each chain includes multiple unique ledgers, whose data among them are isolated from each other. And the transaction processing among groups are also isolated. Each node includes a main configuration `config.ini` and multiple ledger configurations `group.group_id.genesis`, `group.group_id.ini`.

- `config.ini`: The main configuration file, mainly configures with RPC, P2P, SSL certificate, ledger configuration file path, compatibility and other information.

- `group.group_id.genesis`：group configurations file. All nodes in the group are consistent. After node launches, you cannot manually change the configuration including items like group consensus algorithm, storage type, and maximum gas limit, etc.
- `group.group_id.ini`：group variable configuration file, including the transaction pool size, etc.. All configuration changes are effective after node restarts.

## Main configuration file config.ini

`config.ini` uses `ini` format. It mainly includes the configuration items like ** rpc, p2p, group, secure and log **.


```eval_rst
.. important::
    - The public IP addresses of the cloud host are virtual IP addresses. If listen_ip is filled in external network IP address, the binding fails. You must fill in 0.0.0.0.

    - RPC/P2P/Channel listening port must be in the range of 1024-65535 and cannot conflict with other application listening ports on the machine.

```

### Configure RPC

- `listen_ip`: For security reasons, the chain building script will listen to 127.0.0.1 by default. If you need to access the RPC or use SDK through external network, please listen to **external network IP address of node** or `0.0.0.0`;
- `channel_listen_port`: Channel port, is corresponding to `channel_listen_port` in [Web3SDK](../sdk/sdk.html#id2) configuration;
- `jsonrpc_listen_port`: JSON-RPC port.

RPC configuration example is as follows:

```ini
[rpc]
    listen_ip=127.0.0.1
    channel_listen_port=30301
    jsonrpc_listen_port=30302
```

### Configure P2P

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

- `consensus_type`：consensus algorithm type, currently supports [PBFT](../design/consensus/pbft.md) and [Raft](../design/consensus/raft.md). To use PBFT by default;

- `max_trans_num`：a maximum number of transactions that can be packed in a block. The default is 1000. After the chain is initialized, the parameter can be dynamically adjusted through [Console](./console.html#setsystemconfigbykey);

- `node.idx`：consensus node list, has configured with the [Node ID] of the participating consensus nodes. The Node ID can be obtained by the `${data_path}/node.nodeid` file (where `${data_path}` can be obtained by the configuration item `[secure].data_path` of the main configuration `config.ini`)


```ini
; Consensus protocol configuration
[consensus]
    ;consensus algorithm, currently supports PBFT(consensus_type=pbft) and Raft(consensus_type=raft)
    consensus_type=pbft
    ; maximum number of transactions in a block
    max_trans_num=1000
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
    MPT State will be deprecated in v2.3.0, **storage state** is recommended.
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
   External will be deprecated in v2.3.0, if you want to use MySQL, please set type to MySQL.
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

### PBFT consensus message broadcast configuration

In order to ensure the maximum network fault tolerance of the consensus process, each consensus node broadcasts the message to other nodes after receiving a valid consensus message. In smooth network environment, the consensus message forwarding mechanism will waste additional network bandwidth, so the `ttl` is introduced in the group variable configuration item to control the maximum number of message forwarding. The maximum number of message forwarding is `ttl-1`, and **the configuration item is valid only for PBFT**.


Setting consensus message to be forwarded at most once configuration example is as follows:

```ini
; the ttl for broadcasting pbft message
[consensus]
ttl=2
```

### PBFT consensus packing time configuration

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

### PBFT transaction package dynamic adjustment


For the impact causing by CPU loading and network latency on system processing power, PBFT provides an algorithm that dynamically adjusts the maximum number of transactions that can be packed in a block. The algorithm dynamically can adjust the maximum number of transactions according to the state of historical transaction processing. The algorithm is turned on by default, and it can be turned off by changing the `[consensus].enable_dynamic_block_size` configuration item of the variable configuration `group.group_id.ini` to `false`. At this time, the maximum number of transactions in the block is the `[consensus].max_trans_num` of `group.group_id.genesis`.

The configuration of closing the dynamic adjustment algorithm for the block package transaction number is as follows:

```ini
[consensus]
    enable_dynamic_block_size=false
```


### Parallel transaction configuration

FISCO BCOS supports execution of transactions in parallel. Turning on the transaction parallel execution switch to enable for improving throughput. **Execution of the transaction in parallel is only effective in the storage state mode**.


``` ini
[tx_execute]
    enable_parallel=true
```


## Dynamically configure system parameters

FISCO BCOS system currently includes the following system parameters (other system parameters will be extended in the future):


```eval_rst
+-----------------+-----------+---------------------------------+
| system parameters        | default value    |             definition                |
+=================+===========+=================================+
| tx_count_limit  | 1000      | maximum number of transactions that can be packaged in one block
  |
+-----------------+-----------+---------------------------------+
| tx_gas_limit    | 300000000 | Maximum gas limit for a transaction            |
+-----------------+-----------+---------------------------------+

```

Console provides **[setSystemConfigByKey](./console.html#setsystemconfigbykey)** command to modify these system parameters.
**[getSystemConfigByKey](./console.html#getsystemconfigbykey)** command can view the current value of the system parameter:


```eval_rst
.. important::

    It is not recommended to modify tx_count_limit and tx_gas_limit arbitrarily. These parameters can be modified as follows:

    - Hardware performance such as machine network or CPU is limited: to reduce tx_count_limit for reducing business pressure;

    - gas is insufficient when executing transactions for comlicated business logic: increase tx_gas_limit.

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
```
