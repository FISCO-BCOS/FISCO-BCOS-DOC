# Node Configuration Introduction

Tags: "Air Blockchain Network" "Configuration" "config.ini" "config.genesis" "Port Configuration" "Log Level" "Configuration"

----

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

Air version FISCO BCOS mainly includes creation block configuration file 'config.genesis' and node configuration file 'config.ini':

- 'config.ini ': node configuration file, mainly configuring RPC, P2P, SSL certificate, ledger data path, disk encryption and other information;
- 'config.genesis': Genesis block configuration file,**The Genesis block configuration must be consistent for all nodes in the group.**, **Genesis block configuration file cannot be changed after chain initialization**After the chain is initialized, even if the creation block configuration is changed, the new configuration will not take effect, and the system will still use the genesis configuration when the chain was initialized.。

## 1. Genesis block configuration

The node genesis block configuration is in the configuration file 'config.genesis'。

```eval_rst
.. note::
    - **The Genesis block configuration must be consistent for all nodes in the group.**
    - **Genesis block configuration file cannot be changed after chain initialization**
    - After the chain is initialized, even if the creation block configuration is changed, the new configuration will not take effect, and the system still uses the genesis configuration when the chain is initialized
```

### 1.1 Configuration chain information

'[chain]' Configure the chain information of the node,**The field information under this configuration should not be changed once it is determined**：

- `[chain].sm_crypto`: Whether the node uses the national secret ledger. The default value is' false '.;
- '[chain] .group _ id': group ID, default is' group0';
- '[chain] .chain _ id': the chain ID, which is' chain0 'by default.

```ini
[chain]
    ; use SM crypto or not, should nerver be changed
    sm_crypto=false
    ; the group id, should nerver be changed
    group_id=group0
    ; the chain id, should nerver be changed
    chain_id=chain0
```

### 1.2 Consensus Configuration

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

### 1.3 Gas Configuration

FISCO BCOS is compatible with EVM and WASM virtual machines. In order to prevent DOS attacks against EVM / WASM, EVM introduces the concept of gas when executing transactions, which is used to measure the computing and storage resources consumed during the execution of smart contracts, including the maximum gas limit for transactions, if the gas consumed by transaction or block execution exceeds the limit(gas limit), the transaction is discarded, and the maximum gas limit of the transaction can be configured in the '[tx] .gas _ limit' of the creation block, which defaults to 300000000. After the chain is initialized, you can use the [console command](../../operation_and_maintenance/console/console_commands.html#setsystemconfigbykey)Dynamically adjust gas limits。

- `[tx].gas_limit`: Gas limit on trade execution, default setting is 300000000

The configuration example of '[tx] .gas _ limit' is as follows:

```ini
[tx]
gas_limit = 3000000000
```

### 1.4 Data Compatibility Configuration

FISCO BCOS v3.0.0 designs and implements a compatibility framework that supports dynamic upgrade of data versions. This configuration item is located under '[version]':

- `[version].compatibility_version`: Data compatibility version number, the default is' v3.0.0 '. When upgrading a new version, after replacing all binaries, you can run the [console command setSystemConfigByKey](../../operation_and_maintenance/console/console_commands.html#setsystemconfigbykey)Dynamic upgrade data version。

### 1.5 Execute module configuration

'[executor]' configuration items involve the execution of related genesis block configurations, mainly including:

- `[executor].is_wasm`: Used to configure the virtual machine type, 'true' indicates the use of WASM virtual machine, 'false' indicates the use of EVM virtual machine, the configuration option is not dynamically adjustable, the default is' false ';
- `[executor].is_auth_check`: The configuration switch for permission control. 'true' indicates that permission control is enabled, and 'false' indicates that permission control is disabled. This configuration option cannot be dynamically adjusted. The permission control function is disabled by default.;
- `[executor].is_serial_execute`: Transaction execution serial and parallel mode configuration switch, 'true' indicates to enter serial execution mode, 'false' indicates to enter DMC parallel execution mode, this configuration option cannot be dynamically adjusted, the default is' false ';
- `[executor].auth_admin_account`: Permission administrator account address, only used in permission control scenarios。

## 2. Node Profile

'config.ini 'adopts the format of' ini ', mainly including**p2p, rpc, cert, chain, security, consensus, storage, txpool, and log** Configuration Item。

```eval_rst
.. important::
    - The public IP addresses of cloud hosts are all virtual IP addresses. If listen _ ip is filled in, the binding fails.
    - RPC / P2P listening port must be at 1024-65535 range, and cannot conflict with other application listening ports on the machine
    - To facilitate development and experience, the listen _ ip reference configuration is' 0.0.0.0 '. For security reasons, please modify it to a secure listening address according to the actual business network conditions, such as the intranet IP or a specific extranet IP
```

### 2.1 Configuring P2P

P2P related configurations include:

- '[p2p] .listen _ ip': the P2P listening IP address. The default setting is' 0.0.0.0';
- '[p2p] .listen _ port': Node P2P listening port;
- `[p2p].sm_ssl`: Whether the SSL connection between nodes uses the state-secret SSL protocol, 'true' indicates that the state-secret SSL connection is enabled; 'false 'indicates that a non-state-secret SSL connection is used. The default value is' false '.;
- '[p2p] .nodes _ path': the directory where the node connection information file 'nodes.json' is located. The default value is the current folder.;
- '[p2p] .nodes _ file': Path to the 'P2P' connection information file 'nodes.json'。

An example P2P configuration is as follows:

```ini
[p2p]
    listen_ip=0.0.0.0
    listen_port=30300
    ; ssl or sm ssl
    sm_ssl=false
    nodes_path=./
    nodes_file=nodes.json
```

'p2p 'connection configuration file' nodes _ file 'format:

```shell
{"nodes":[Connection List]}
```

Example:

```shell
{"nodes":["127.0.0.1:30300","127.0.0.1:30301","127.0.0.1:30302","127.0.0.1:30303"]}
```

'P2P 'supports configurable network connections and dynamic addition / deletion of connection nodes during service operation. The process is as follows:

- Modify the connection information in the '[p2p] .nodes _ file' configuration
- Send signal to service process' USR1':

```shell
kill -USR1 Gateway Node pid
```

Service reloads' P2P 'connection information。

### 2.2 Configure RPC

The RPC configuration options are located at '[rpc]' and mainly include:

- `[rpc].listen_ip`: RPC listens on the IP address, which is set to '0.0.0.0' by default to facilitate cross-machine deployment of nodes and SDKs;
- `[rpc].listen_port`: RPC listening port, default setting is' 20200';
- `[rpc].thread_count`: Number of RPC service threads, 4 by default;
- `[rpc].sm_ssl`: Whether the connection between the SDK and the node uses the state-secret SSL connection. True indicates that the state-secret SSL connection is enabled.; 'false 'indicates that a non-state secret SSL connection is used. The default value is' false '.

An example RPC configuration is as follows:

```ini
[rpc]
    listen_ip=0.0.0.0
    listen_port=20200
    thread_count=4
    ; ssl or sm ssl
    sm_ssl=false
    ; ssl connection switch, if disable the ssl connection, default: false
    ;disable_ssl=true
```

### 2.3 Configuring Certificate Information

For security reasons, SSL is used to encrypt communication between FISCO BCOS nodes. Configure the certificate information of the SSL connection.

- `[cert].ca_path`: Certificate path, default is' conf';
- `[cert].ca_cert`: ca certificate name, default is' ca.crt';
- `[cert].node_key`: The private key of the node SSL connection. The default value is' ssl.key';
- `[cert].node_cert`: The SSL connection certificate of the node. The default value is' ssl.cert '.

```ini
[cert]
    ; directory the certificates located in
    ca_path=./conf
    ; the ca certificate file
    ca_cert=ca.crt
    ; the node private key file
    node_key=ssl.key
    ; the node certificate file
    node_cert=ssl.crt
```

'[security]' Configure the private key path, which is mainly used for message signing of the consensus module, as follows:

- '[security] .private _ key _ path': path to the private key file. The default value is' conf / node.pem'。

```ini
[security]
    private_key_path=conf/node.pem
```

### 2.4 Configure consensus information

Considering that too fast packaging of PBFT modules will result in packaging only 1 to 2 very few transactions in some blocks, which wastes storage space, FISCO BCOS introduces the 'min _ seal _ time' configuration item under the '[consensus]' of the variable configuration 'config.ini' to control the minimum time for PBFT consensus packaging, that is, the consensus node packaging time exceeds' min _ seal _ time 'and the number of packaged transactions is greater than 0 to start the。

```eval_rst
.. important::
   - "min _ seal _ time" defaults to 500ms
   - "min _ seal _ time" cannot exceed 1000ms. If the value exceeds 1000ms, the default min _ seal _ time is 500ms.

```

```ini
[consensus]
    ; min block generation time(ms)
    min_seal_time=500
```

### 2.5 Configure storage information

The storage configuration is located at '[storage]' and includes:

- `[storage].data_path`: The data storage path of the blockchain node. The default value is data.;
- `[storage].enable_cache`: Whether to enable caching. The default value is true.;
- `[storage].key_page_size`: In the KeyPage storage scheme, the storage page size, in bytes, is required to be no less than '4096'(4KB)default is' 10240'(10KB);

```ini
[storage]
    data_path=data
    enable_cache=true
    key_page_size=10240
```

### 2.6 Configuring Disk Drop Encryption

The drop disk encryption configuration option is located at '[storage _ security]':

- `[storage_security].enable`: Whether to enable disk encryption. Disk encryption is disabled by default.;
- `[storage_security].key_manager_url`: [Key Manager] is configured for 'key _ center _ url' when encryption is enabled(../../design/storage_security.md)url to get the data encryption and decryption key;
- `[storage_security].cipher_data_key`: Private key for data drop encryption。

```ini
[storage_security]
    ; enable data disk encryption or not, default is false
    enable=false
    ; url of the key center, in format of ip:port
    key_center_url=
    cipher_data_key=
```

### 2.7 Configuring Trading Pool Information

The trading pool configuration option is located at '[txpool]':

- `[txpool].limit`: Capacity limit of trading pool, default is' 15000';
- `[txpool].notify_worker_num`: Number of transaction notification threads, 2 by default;
- `[txpool].verify_worker_num`: Number of transaction verification threads. The default value is the number of machine CPU cores.;
- `[txpool].txs_expiration_time`: The transaction expiration time, in seconds. The default value is 10 minutes. That is, transactions that have not been packaged by the consensus module for more than 10 minutes will be discarded directly.。

```ini
[txpool]
; size of the txpool, default is 15000
limit = 15000
; txs notification threads num, default is 2
notify_worker_num = 2
; txs verification threads num, default is the number of cpu cores
;verify_worker_num = 2
; txs expiration time, in seconds, default is 10 minutes
txs_expiration_time = 600
```

### 2.8 Configuring Log Information

FISCO BCOS supports powerful [boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)The log configuration is mainly located in the '[log]' configuration item of 'config.ini'。

- `[log].enable`: Enables / disables logging, set to 'true' to enable logging；Set to 'false' to disable logging,**The default setting is true, and performance tests can set this option to 'false' to reduce the impact of printing logs on test results**
- `[log].log_path`:Log File Path。
- `[log].level`: Log level. Currently, there are five log levels: 'trace', 'debug', 'info', 'warning', and 'error'. After a log level is set, logs greater than or equal to the log level are entered in the log file.。
- '[log] .max _ log _ file _ size': the maximum size of each log file.**The unit of measurement is MB, the default is 200MB**。

The log configuration example is as follows:

```ini
[log]
    enable=true
    log_path=./log
    ; info debug trace
    level=DEBUG
    ; MB
    max_log_file_size=200
```

#### v3.6.0 New Configuration Item

- 'log.format ': Configure the format of each log. The keywords are wrapped in%. Supported keywords include' LineID, TimeStamp, ProcessID, ThreadName, ThreadID, and Message '.
- 'log.enable _ rotate _ by _ hour ': The default value is true. When' false 'is set to' log.log _ name _ pattern, 'log.rotate _ name _ pattern,' log.archive _ path, 'log.compress _ archive _ file,' log.max _ archive _ files, 'log.max _ archive _ size,' or 'log.max _ archive _ size', 'log.min _
- 'log.log _ name _ pattern ': The file name mode of the log file. You can configure a string and support formatting characters. The% prefix, Y, m, d, H, M, and S represent the year, month, day, minute, and second. N represents a monotonically increasing number. You can use a fixed-length number for% 5N.
- 'log.rotate _ name _ pattern ': the file name of the log file generated after scrolling. The supported format characters are the same as log.log _ name _ pattern
- 'log.archive _ path ': Archive folder for history log files
- 'log.compress _ archive _ file ': Whether to compress archived log files
- 'log.max _ archive _ files': the maximum number of files in the archive folder, 0 is unlimited
- 'log.max _ archive _ size ': the maximum hard disk space limit of the archive folder, in MB, 0 is unlimited
- 'log.min _ free _ space ': Minimum archive folder space, 0 by default

### 2.9 Gateway module current limiting

The gateway module supports configuring the function of traffic rate limiting in config.ini. When the traffic exceeds the limit, it can achieve current limiting by discarding data packets。

Configure the following according to your needs to achieve

- Outgoing Bandwidth and Incoming Bandwidth Limiting
- Restriction of specific IP and group
- Excluding Current Limiting for Specific Modules

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
