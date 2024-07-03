# Operation and Maintenance Manual

Tags: "Operation and Maintenance"

## Deploy

Alliance chain is a distributed network and distributed system composed of multiple nodes, the node geographic location rate belongs to a certain partition, and the attribution rate belongs to an organization。The deployment of alliance chain needs to consider many factors such as organization, partition, node, etc。Here are some basic principles of deployment:

||目的|Content
|:--|:--|:--
|1|Consensus has fault-tolerant space|The number of nodes satisfies N = 3F+1. The chain needs at least 4 nodes
|2|partition fault tolerance|Number of consensus nodes per partition should not exceed F
|3|Avoiding single points of failure within the mechanism|At least 2 nodes per institution
|4|Save resources and increase efficiency|Some nodes in the mechanism are observation nodes
|5|Institutional Weight Adjustment|Adjust the number of nodes in the organization and the weight of the consensus node according to the weight agreed by all parties

## Log Description

FISCO BCOS provides a standardized log output format, which can be used to analyze the running status of the system, locate problems, monitor statistics, etc。

```bash
# Log format:
log_level|time|[module_name] content

# Log example:

info|2022-11-21 20:00:35.479505|[SCHEDULER][blk-1]BlockExecutive prepare: fillBlock end,txNum=1,cost=0,fetchNum=1
```

where log _ level is the log level, from small to large, including trace, debug, info, warning, error, and fatal, time indicates the log printing time, [module _ name] indicates the module name, including consensus, synchronization, transaction pool, storage, etc., and content is the specific log content。General log analysis and problem location, you can view [Log Description](./log_description.md)。

The log output level is configured in the config.ini file. In the test environment, it is recommended to set it to the trace or debug level, which can output logs of all levels for easy analysis and positioning。In a production environment, we recommend that you set it to the info level to reduce the amount of log output (the amount of trace and debug logs is large) and avoid excessive log disk usage。

## monitoring alarm

The monitoring of FISCO BCOS includes two parts: blockchain monitoring and system monitoring。

[Blockchain monitoring] FISCO BCOS provides its own system monitoring tool monitor.sh, which can monitor node survival, consensus status, and ledger status。The monitor.sh tool can connect the output content to the organization's own operation and maintenance monitoring system, so that blockchain monitoring can be connected to the organization's operation and maintenance monitoring platform。

[System Monitoring] In addition to monitoring the FISCO BCOS node itself, it is also necessary to monitor relevant indicators from the perspective of the system environment。It is recommended that the operation and maintenance should monitor the CPU, memory, bandwidth consumption and disk consumption of the node to find out the abnormal system environment in time。FISCO BCOS3.0 can monitor whether the blockchain is working properly, including monitoring consensus, abnormal synchronization, and disk space. It also provides a simple way to access the user alarm system. You can view the [light _ monitor.sh monitoring tool](../operation_and_maintenance/light_monitor.md)。

## Data Backup and Recovery

FISCO BCOS supports two data backup methods, you can choose the appropriate method according to your needs。
[Method 1]: Stop the node, package and compress the data directory of the node as a whole and back it up to another location, decompress the backup data when needed, and restore the node。This method is equivalent to a snapshot of the data in a ledger state for subsequent recovery from this state. For details, see [Node Monitoring Configuration](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/tutorial/air/build_chain.html?highlight=%E7%9B%91%E6%8E%A7#id4)。

[Method 2]: According to the data archiving service tool, the data on the chain can be archived and stored。When you need to restore or add new nodes, you can restore the archived data to realize data backup and recovery. For specific data archiving operations, please refer to [Data Archiving Usage](../operation_and_maintenance/data_archive_tool.md)

The advantage of method 1 is that there is no need to deploy new services and operations, and the operation and maintenance is simple. The disadvantage is that a historical state is backed up, and the data recovered from this state is not the latest data. After recovery, the updated data of the ledger needs to be synchronized from other nodes。Method 2 is the opposite of Method 1, which requires the deployment of services, which is more expensive to operate and maintain, but can be restored to the latest ledger state at any time。

## Expansion method

FISCO BCOS expansion mainly considers two aspects: the expansion of the number of nodes and the expansion of the number of disks。

[node number expansion]: FISCO BCOS supports dynamic addition and removal of nodes, and can change the identity status of nodes (consensus, observation, free)。Reject and change status can be done directly through console commands。To add a node, you need to perform the following steps:

1. Prepare certificates for new nodes and issue node certificates with agency certificates；
2. Prepare the machine of the new node, allocate the RPC and P2P ports, ensure that the ports can be connected, and ensure that the P2P ports can communicate with other nodes；
3. Generate the configuration of the new node, mainly the network configuration in config.ini。During configuration, we recommend that you copy a copy from another node and modify the network-related configuration items on this basis；
4. Publish the new node to the machine, start the node, verify whether the network connection between the new node and other nodes is established, and eliminate exceptions such as certificate problems and network policy problems；
5. Send a command from the console to add the new node as an observation node；
6. At this time, the node does not participate in the consensus, it will synchronize the ledger and wait for the block height to reach an agreement with other nodes；
7. Send a command from the console to change the new node status to the consensus node。
   FISCO BCOS supports node expansion regardless of air, pro or max. The above steps are the same. For details, please refer to [Air node expansion](../tutorial/air/expand_node.md)[Pro node expansion](../tutorial/pro/expand_node.md)[Max Node Expansion](../tutorial/max/max_builder.md).

[Data disk expansion]: Data disk expansion includes Air chain data disk expansion and Max chain data disk expansion。

Air chain data disk expansion: FISCO BCOS uses the rocksdb storage engine by default (performance is higher than the Mysql engine), rocksdb data is stored on the local disk。If you need to expand the local disk, use the following steps:

1. Remove the node from the console so that the node does not participate in consensus or synchronization；
2. Stop Node；
3. Mount the new disk；
4. Migrate node to new disk；
5. Restart the node；
6. Send a command from the console to add the node to the consensus。
   Some cloud platforms provide functions such as one-click upgrade and hard disk expansion. The above 3-4 steps can replace this function。

Max Chain Data Disk Expansion:We recommend that you use the TIKV cluster version for Max nodes in the production environment. The TiKV cluster version can be used as the backend of the nodes to easily and simply scale out。For specific expansion and contraction, please refer to [TIKV Expansion](../tutorial/max/max_builder.md)。

## Upgrade Method

FISCO BCOS supports node-friendly, contract-compatible upgrades。

[Node upgrade]: FISCO BCOS uses compatibility _ version to control the compatibility version of the block chain. Compatibility _ version must be determined in the construction chain. This configuration cannot be changed during subsequent node upgrades。For example, the compatibility _ version is 3.1.0 when the chain is established, and the compatibility _ version configuration must remain at 3.1.0 after subsequent node upgrades to 3.2.0 and 3.3.0。The node upgrade steps are as follows:

1. Stop Node；
2. Back up the disco-bcos binary executable of the old version node and replace it with the new version；
3. Restart the node；
4. Check the consensus and synchronization to ensure the normal operation of the node。
   Contract upgrade can refer to the document [upgrade of smart contract](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/contract_life_cycle.html#id5)。

## Key Management

FISCO BCOS involves the management of private keys and certificates of chains, institutions, nodes, and SDKs. If a self-signed certificate is used (FISCO BCOS is provided by default), O & M needs to manage all these private keys and certificates and make backups。The specific management method can be the organization's own management system, or the key escrow service provided by FISCO BCOS (the service needs to be deployed and maintained)。The certificates and private keys involved include:

1. The private key and certificate of the chain
2. Private key and certificate of the institution
3. The node's private key and certificate
4. SDK private key and certificate

All keys and certificates support the national secret. The generated national secret certificate and private key have their own sm prefix. For example, the normal key and certificate are ca.key and ca.crt, and the national secret private key and certificate are sm _ ca.key and sm _ ca.crt。

## TLS Communication Certificate Maintenance

In order to ensure the security of system communication operation and maintenance, FISCO BCOS regularly updates the TLS communication key of nodes to prevent attackers from analyzing the key by intercepting a large number of ciphertexts over a long period of time。
Key update is divided into two methods: updating all certificates and keys of the node and updating only the TLS communication certificate of the node. The steps for updating the root certificate are as follows:

1. Back up the original CA certificate and key；
2. Generate a new CA certificate and key based on the configuration of the original CA certificate and the certificate signing request；
3. Back up the new CA certificate and key；
4. The node reboots, enabling the new CA certificate to communicate with the key。

To update only the node TLS communication certificate, follow these steps:

1. Back up the TLS communication certificate and key of the original node
2. Generate a new node TLS communication certificate based on the configuration of the CA certificate and the certificate signing request；
3. Backup new node TLS communication certificate and key
4. The node communication restarts, enabling the new CA certificate to communicate with the key。

If the certificate is compromised, the longer the certificate is used, the greater the loss。 Therefore, the use of the certificate, should set the validity period, when the certificate exceeds the validity period or stop using, the certificate is destroyed, the specific destruction process is as follows:

1. Check the validity period of the node communication certificate. If the certificate expires, the certificate will be archived and destroyed. If the key is stopped, the user can also take the initiative to destroy the certificate after the certificate is archived；
2. Update the node TLS communication key to generate a new communication certificate；
3. Back up the new certificate, restart the node, and enable the new certificate。

## exception recovery

When the system system failure, network abnormalities, such as power failure, node loss, network failure, etc., just restart the node operation, the system can restore the normal operation of the system。
