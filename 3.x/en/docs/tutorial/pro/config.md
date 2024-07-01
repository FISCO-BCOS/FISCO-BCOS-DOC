# Configuration Introduction

Tags: "Pro Blockchain Network" "Scaling Groups" "Multi-Group Microservice Blockchain" "" tars "" RpcService "" GatewayService "" NodeService ""

------------

FISCO BCOS uses tars to build a multi-group microservice blockchain. To build a FISCO BCOS Pro version of the blockchain, please refer to [here](./installation.md)

## 1. Organization structure of multi-group microservice files

Use the blockchain build script 'BcosBuilder / pro / build _ chain.py' to build**RpcService** RPC services,**GatewayService** gateway services, and**NodeService** Node Services。After the build, the 'generated' directory is generated. The service configuration information is in this directory。

```shell
generated/
├── chain0 # Chain
│   ├── group0 # GROUP
│   │   ├── 172.25.0.3 # This machine's eth0 ip address
│   │   │   ├── agencyAgroup0node0BcosNodeService # Blockchain node configuration directory
│   │   │   │   ├── config.genesis # Genesis Block Profile
│   │   │   │   ├── config.ini # Profile
│   │   │   │   ├── node.nodeid # node id
│   │   │   │   ├── node.pem # Node private key for consensus module signature
│   │   │   ├── agencyBgroup0node0BcosNodeService # Blockchain node configuration directory
│   │   │   │.....
├── gateway # Gateway
│   ├── chain # Chain
│   │   ├── 172.25.0.3 # This machine's eth0 ip address
│   │   │   ├── agencyABcosGatewayService # Gateway Configuration Directory
│   │   │   │   ├── ssl
│   │   │   │   │   ├── ca.crt # CA Certificate
│   │   │   │   │   ├── cert.cnf # Certificate Configuration
│   │   │   │   │   ├── ssl.crt # ssl certificate
│   │   │   │   │   ├── ssl.key # ssl private key
│   │   │   │   ├── config.ini # Profile
│   │   │   │   ├── nodes.json # node connection information,{"nodes": ["172.25.0.30:30300", "172.25.0.3:30301"]}
│   │   │   ├── agencyBBcosGatewayService # Gateway Configuration Directory
│   │   │   │.....
│   │   ├── ca # CA Certificate
│   │   │   ├── ca.crt # CA Certificate
│   │   │   ├── ca.key # CA Private Key
│   │   │   ├── cert.cnf # Certificate Private Key
├── rpc
│   ├── chain
│   │   ├── 172.25.0.30 # This machine's eth0 ip address
│   │   │   ├── agencyABcosRpcService # RPC Service Configuration Path
│   │   │   │   ├── ssl       
│   │   │   │   │   ├── ca.crt # CA Certificate
│   │   │   │   │   ├── cert.cnf # Certificate Configuration
│   │   │   │   │   ├── ssl.crt # ssl certificate
│   │   │   │   │   ├── ssl.key # ssl private key
│   │   │   │   ├── sdk        # SDK Certificate Directory
│   │   │   │   │   ├── ca.crt # CA Certificate
│   │   │   │   │   ├── cert.cnf # Certificate Configuration
│   │   │   │   │   ├── sdk.crt # sdk certificate
│   │   │   │   │   ├── sdk.key # sdk private key
│   │   │   │   ├── config.ini.tmp # Profile
│   │   │   ├── agencyBBcosRpcService # RpcService B
│   │   │   │.....
│   │   ├── ca # CA Certificate
│   │   │   ├── ca.crt # CA Certificate
│   │   │   ├── ca.key # CA Private Key
│   │   │   ├── cert.cnf # Certificate Private Key
├── agencyABcosGatewayService.tgz # The generated tgz packet of gateway service A
├── agencyABcosRpcService.tgz # The generated tgz package of RPC service A
├── agencyBBcosGatewayService.tgz # The generated tgz packet of gateway service B
├── agencyBBcosRpcService.tgz # The generated tgz package of RPC service B
├── node.nodeid # node id
├── node.pem # node pem format private key
```

## 2. Blockchain node service(BcosNodeService)Configuration

Block chain node service construction please refer to [here](./installation.html#id6)which mainly includes the genesis block configuration 'config.genesis' and the node configuration 'config.ini'。

### 2.1 Genesis block configuration

```eval_rst
.. note::
    - **The Genesis block configuration must be consistent for all nodes in the group.**
    - **Genesis block configuration file cannot be changed after chain initialization**
    - After the chain is initialized, even if the creation block configuration is changed, the new configuration will not take effect, and the system still uses the genesis configuration when the chain is initialized
```

#### 2.1.1 Chain Configuration Options

Chain configuration options are located in '[chain]' and include:

- `[chain].sm_crypto`: Used to configure the cryptology type of the ledger, 'true' indicates that the ledger uses the state secret algorithm, 'false' indicates that the ledger uses the non-state secret algorithm, the default is' false ';
- `[chain].group_id`: group id;
- `[chain].chain_id`: chain id.

An example of node chain configuration options is as follows:

```ini
[chain]
sm_crypto=false
group_id=group0
chain_id=chain0
```

#### 2.1.2 Consensus Configuration

'[consensus]' involves consensus-related configurations, including:

- `[consensus].consensus_type`: Consensus type. The default setting is' pbft '. Currently, FISCO BCOS v3.x only supports the PBFT consensus algorithm.;
- `[consensus].block_tx_count_limit`: The maximum number of transactions that can be included in each block. The default setting is 1000;
- `[consensus].leader_period`: The number of consecutive blocks packed by each leader in the consensus process. The default value is 5;
- '[consensus] .node.idx': list of consensus nodes, configured with the NodeIDs of the participating consensus nodes。


The configuration example of '[consensus]' is as follows:
```ini
[consensus]
consensus_type = pbft
block_tx_count_limit = 1000
leader_period = 5
node.0 = 94172c95917fbf47b4b98aba0cc68f83f61a06b0bc373695590f343464b52c9b40d5f4dd98384c037d4cad938b329c6af826f695a7123007b7e06f24c6a48f20:1
node.1 = 74034fb43f75c63bb2259a63f71d9d1c658945409889d3028d257914be1612d1f2e80c4a777cb3e7929a0f0d671eac2fb9a99fa45d39f5451b6357b00c389a84:1
```

#### 2.1.3 Gas Configuration

FISCO BCOS is compatible with EVM and WASM virtual machines. In order to prevent DOS attacks against EVM / WASM, EVM introduces the concept of gas when executing transactions, which is used to measure the computing and storage resources consumed during the execution of smart contracts, including the maximum gas limit for transactions, if the gas consumed by transaction or block execution exceeds the limit(gas limit), the transaction is discarded, and the maximum gas limit of the transaction can be configured in the '[tx] .gas _ limit' of the creation block, which defaults to 300000000. After the chain is initialized, you can use the [console command](../../operation_and_maintenance/console/console_commands.html#setsystemconfigbykey)Dynamically adjust gas limits。

- `[tx].gas_limit`: Gas limit on trade execution, default setting is 300000000

The configuration example of '[tx] .gas _ limit' is as follows:

```ini
[tx]
gas_limit=3000000000
```

#### 2.1.4 Data Compatibility Configuration

FISCO BCOS v3.0.0 designs and implements a compatibility framework that supports dynamic upgrade of data versions. This configuration item is located under '[version]':

- `[version].compatibility_version`: Data compatibility version number, the default is' v3.0.0 '. When upgrading a new version, after replacing all binaries, you can run the [console command setSystemConfigByKey](../../operation_and_maintenance/console/console_commands.html#setsystemconfigbykey)Dynamic upgrade data version。

#### 2.1.5 Performing Module Configuration

'[executor]' configuration items involve the execution of related genesis block configurations, mainly including:

- `[executor].is_wasm`: Used to configure the virtual machine type, 'true' indicates the use of WASM virtual machine, 'false' indicates the use of EVM virtual machine, the configuration option is not dynamically adjustable, the default is' false ';
- `[executor].is_auth_check`: The configuration switch for permission control. 'true' indicates that permission control is enabled, and 'false' indicates that permission control is disabled. This configuration option cannot be dynamically adjusted. The permission control function is disabled by default.;
- `[executor].is_serial_execute`: Transaction execution serial and parallel mode configuration switch, 'true' indicates to enter the serial execution mode, 'false' indicates to enter the DMC parallel execution mode, the configuration option is expected not to be dynamically adjusted, the default is' false ';
- `[executor].auth_admin_account`: Permission administrator account address, only used in permission control scenarios(When the chain version number is greater than 3.3 or the permission is enabled, this configuration must be added)。

### 2.2 Node Configuration

Node configuration 'config.ini' is mainly used to configure the node's chain ID, group ID, and ledger type(State Secret / Non-State Secret)and so on, including service configuration options, consensus configuration options, storage configuration options, transaction pool configuration options, log configuration options, and so on.。

#### 2.2.1 Service Configuration Options

The service configuration option is located in '[service]' and is mainly used to configure the RPC / Gateway service name corresponding to the node, including: 

- `[service].rpc`: RPC Service Name;
- `[service].gateway`: Gateway Service Name;

```ini
[service]
rpc = chain0.agencyABcosRpcService
gateway = chain0.agencyABcosGatewayService
```

#### 2.2.2 Drop Disk Encryption Configuration Options

Disk encryption configuration options are located at '[storage _ security]' and mainly include:

- `[storage_security].enable`: Falling encryption switch, 'true' indicates that falling encryption is enabled, 'false' indicates that falling encryption is disabled, the default is' false';
- `[storage_security].key_center_url`: [Key Manager] is configured for 'key _ center _ url' when encryption is enabled(../../design/storage_security.md)url to get the data encryption and decryption key;
- `[storage_security].cipher_data_key`: Private key for data drop encryption。

#### 2.2.3 Consensus Configuration Options

Considering that too fast packaging of PBFT modules will result in packaging only 1 to 2 very few transactions in some blocks, which wastes storage space, FISCO BCOS introduces the 'min _ seal _ time' configuration item under the '[consensus]' configuration of 'config.ini' to control the minimum time for PBFT consensus packaging, that is, the consensus node packaging time exceeds' min _ seal _ time 'and the number of packaged transactions is greater than 0 to start the consensus process。

- `[consensus].min_seal_time`: Minimum block generation time, 500ms by default。

```ini
[consensus]
min_seal_time=500
```

#### 2.2.4 Storage Configuration Options

The storage configuration option is located in '[storage]' and is primarily used to configure on-chain data paths:

- `[storage].data_path`: Ledger Data Storage Path;
- `[storage].enable_cache`: Whether to enable caching. The default value is true.;
- `[storage].type`: The underlying storage database type, which is RocksDB by default;
- `pd_addrs`: Pro empty, max version field；
- `key_page_size`: The size of each page in the key _ page storage. The default value is 10240k.。

```ini
[storage]
    data_path=data
    enable_cache=true
    type=RocksDB
    pd_addrs=
    key_page_size=10240
```

#### 2.2.5 Trading Pool Configuration Options

The trading pool configuration option is located at '[txpool]':

- `[txpool].limit`: Capacity limit of trading pool, default is' 15000';
- `[txpool].notify_worker_num`: Number of transaction notification threads, 2 by default;
- `[txpool].verify_worker_num`: Number of transaction verification threads. The default value is the number of machine CPU cores.;
- `[txpool].txs_expiration_time`: The transaction expiration time, in seconds. The default value is 10 minutes. That is, transactions that have not been packaged by the consensus module for more than 10 minutes will be discarded directly.。

```ini
[txpool]
    ; size of the txpool, default is 15000
    limit=15000
    ; txs notification threads num, default is 2
    notify_worker_num=2
    ; txs verification threads num, default is the number of CPU cores
    verify_worker_num=2
    ; txs expiration time, in seconds, default is 10 minutes
    txs_expiration_time = 600
```

#### 2.2.6 Log Configuration Options

Log configuration options are located in '[log]' and include:

- `[log].enable`: Enables / disables logging, set to 'true' to enable logging；Set to 'false' to disable logging,**The default setting is true, and performance tests can set this option to 'false' to reduce the impact of printing logs on test results**
- `[log].log_path`:Log File Path。
- `[log].level`: Log level. Currently, there are five log levels: 'trace', 'debug', 'info', 'warning', and 'error'. After a log level is set, logs greater than or equal to the log level are entered in the log file.。
- '[log] .max _ log _ file _ size': the maximum size of each log file.**The unit of measurement is MB, the default is 200MB**。

```ini
[log]
enable = true
log_path = ./log
; info debug trace
level = DEBUG
; MB
max_log_file_size = 200
```

## 3. RPC / Gateway Service Configuration

RPC / Gateway service setup please refer to [here](./installation.html)which mainly includes the node configuration 'config.ini'。

### 3.1 Network Connection Configuration

The network connection configuration is located at '[p2p]' and mainly includes:

- `[p2p].listen_ip`: RPC / Gateway listens to the IP address. To ensure normal communication between nodes deployed across machines, the default listening IP address is' 0.0.0.0';
- `[p2p].listen_port`:  RPC / Gateway listening port, default setting is' 30300';
- `[p2p].sm_ssl`: Whether to use state-secret SSL connections between nodes or between SDKs and RPC services. The default value is false.;
- `[p2p].nodes_path`: The directory where the gateway connection file 'nodes.json' is located. The default value is the current directory.;
- `[p2p].nodes_file`: The name of the gateway connection information file 'nodes.json'. The default value is' nodes.json';
- `[p2p].thread_count`: Number of RPC / Gateway network processing threads. The default value is 4.

```ini
[p2p]
listen_ip = 0.0.0.0
listen_port = 30301
; ssl or sm ssl
sm_ssl = false
nodes_path = ./
nodes_file = nodes.json
thread_count = 4
```

### 3.2 RPC / Gateway Service Configuration

The RPC / Gateway service configuration is in the 'service' configuration and consists mainly of:

- `[service].rpc`: RPC service name corresponding to the gateway;
- `[service].gateway`: Name of the gateway service corresponding to RPC;

```ini
[service]
gateway = chain.agencyABcosGatewayService
rpc = chain.agencyABcosRpcService
```

### 3.3 Chain configuration

The chain configuration is under the '[chain]' configuration option and consists mainly of:

- `[chain].chain_id`: chain id;
- `[chain].sm_crypto`: When disk encryption is enabled, the certificate encryption type。

```ini
[chain]
; use sm crypto or not, should nerver be changed
sm_crypto = false
chain_id = chain0
```

### 3.4 Disk drop encryption configuration

FISCO BCOS v3.0.0 supports disk encryption. It can encrypt the SSL connection private key of RPC / Gateway to ensure the confidentiality of the SSL connection private key.:

- `[storage_security].enable`: Whether to enable the disk drop encryption function, which is turned off by default;
- `[storage_security].key_center_url`: [Key Manager] is configured for 'key _ center _ url' when encryption is enabled(../../design/storage_security.md)url to get the data encryption and decryption key;
- `[storage_security].cipher_data_key`: Private key for data drop encryption。

The configuration example of disk encryption is as follows:

```ini
[storage_security]
; enable data disk encryption or not, default is false
enable = false
; url of the key center, in format of ip = port
;key_center_url =
;cipher_data_key =
```

### 3.5 Log Configuration Options

The log configuration is in the '[log]' option:

- `[log].enable`: Enables / disables logging, set to 'true' to enable logging；Set to 'false' to disable logging,**The default setting is true, and performance tests can set this option to 'false' to reduce the impact of printing logs on test results**
- `[log].log_path`:Log File Path。
- `[log].level`: Log level. Currently, there are five log levels: 'trace', 'debug', 'info', 'warning', and 'error'. After a log level is set, logs greater than or equal to the log level are entered in the log file.。
- '[log] .max _ log _ file _ size': the maximum size of each log file.**The unit of measurement is MB, the default is 200MB**。

```ini
[log]
enable = true
log_path = ./log
; info debug trace
level = DEBUG
; mb
max_log_file_size = 200
```

### 3.6 Gateway module current limiting

The gateway module supports configuring the function of traffic rate limiting in config.ini. When the traffic exceeds the limit, it can achieve current limiting by discarding data packets。

Configure the following according to your needs to achieve

* Outgoing Bandwidth and Incoming Bandwidth Limiting
* Restriction of specific IP and group
* Excluding Current Limiting for Specific Modules

The configuration in the process-dependent config.ini is as follows (please uncomment some items as required)

``` ini
[flow_control]
    ; rate limiter stat reporter interval, unit: ms
    ; stat_reporter_interval=60000

    ; time window for rate limiter, default: 3s
    ; time_window_sec=3

    ; enable distributed rate limiter, redis required, default: false
    ; enable_distributed_ratelimit=false
    ; enable local cache for distributed rate limiter, work with enable_distributed_ratelimit, default: true
    ; enable_distributed_ratelimit_cache=true
    ; distributed rate limiter local cache percent, work with enable_distributed_ratelimit_cache, default: 20
    ; distributed_ratelimit_cache_percent=20

    ; the module that does not limit bandwidth
    ; list of all modules: raft,pbft,amop,block_sync,txs_sync,light_node,cons_txs_sync
    ;
    ; modules_without_bw_limit=raft,pbft

    ; allow the msg exceed max permit pass
    ; outgoing_allow_exceed_max_permit=false

    ; restrict the outgoing bandwidth of the node
    ; both integer and decimal is support, unit: Mb
    ;
    ; total_outgoing_bw_limit=10

    ; restrict the outgoing bandwidth of the the connection
    ; both integer and decimal is support, unit: Mb
    ;
    ; conn_outgoing_bw_limit=2
    ;
    ; specify IP to limit bandwidth, format: conn_outgoing_bw_limit_x.x.x.x=n
    ;   conn_outgoing_bw_limit_192.108.0.1=3
    ;   conn_outgoing_bw_limit_192.108.0.2=3
    ;   conn_outgoing_bw_limit_192.108.0.3=3
    ;
    ; default bandwidth limit for the group
    ; group_outgoing_bw_limit=2
    ;
    ; specify group to limit bandwidth, group_outgoing_bw_limit_groupName=n
    ;   group_outgoing_bw_limit_group0=2
    ;   group_outgoing_bw_limit_group1=2
    ;   group_outgoing_bw_limit_group2=2

    ; should not change incoming_p2p_basic_msg_type_list if you known what you would to do
    ; incoming_p2p_basic_msg_type_list=
    ; the qps limit for p2p basic msg type, the msg type has been config by incoming_p2p_basic_msg_type_list, default: -1
    ; incoming_p2p_basic_msg_type_qps_limit=-1
    ; default qps limit for all module message, default: -1
    ; incoming_module_msg_type_qps_limit=-1
    ; specify module to limit qps, incoming_module_qps_limit_moduleID=n
    ;       incoming_module_qps_limit_xxxx=1000
    ;       incoming_module_qps_limit_xxxx=2000
    ;       incoming_module_qps_limit_xxxx=3000

```



