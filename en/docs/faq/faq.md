# FAQ (Revision in progress)

## Version

Q:
  What changes have been made to FISCO BCOS version 2.0 compares to previous versions? <br>
A:
  Please [refer to here](./what_is_new.md).

Q:
  How do developers interact with the FISCO BCOS platform? <br>
A:
  FISCO BCOS provides multiple ways for developers to interact with the platform. Please refer as follows:
  - FISCO BCOS 2.0 version provides JSON-RPC interface. For the detail, please refer to [here](./api.md).
  - FISCO BCOS 2.0 version provides Web3SDK to help developers quickly implement applications. For the detail, please refer to [here](./sdk/java_sdk.md).
  - FISCO BCOS version 2.0 provides a console to help users quickly understand how to use FISCO BCOS. For the detail, Please refer to [here](./manual/console_of_java_sdk.md) for the console user manual of version 2.6 and above, and [here](./manual/console.md) for the console user manual of version 1.x

Q:
  How to build FISCO BCOS 2.0 version? <br>
A:
  FISCO BCOS supports multiple building methods. The common methods are:
  - build_chain.sh: It is suitable for developer experience and testing FISCO BCOS alliance chain. For the detail, please [refer to here](./manual/build_chain.md)。
  - FISCO-Generator：For deploying and maintaining the FISCO BCOS Alliance Chain with enterprise users. For the detail, please [refer to here](./enterprise_tools/index.md)。

Q:
  What is the difference on the smart contract between FISCO BCOS 2.0 version and the previous version? And how is the compatibility? <br>
A:
  FISCO BCOS version 2.0 supports the latest Solidity contract and precompile contract. For the detail, please [refer to here] (./manual/smart_contract.md).

Q:
  What is the difference between the national cryptographic version and the normal version? <br>
A:
  The national cryptography version FISCO BCOS replaces the cryptographic algorithms of underlying modules such as transaction signature verification, p2p network connection, node connection, and data disk encryption with the national cryptography algorithm. Meanwhile, in compiling version and certificate, disk encryption, and solidity compiling java, there are some difference on web3sdk
using national cryptography version and normal version, please refer to [refer to here] (./manual/guomi_crypto.md).

Q:
  Does it support to upgrade to 2.0 version from 1.3 or 1.5?<br>
A:
  It does not.

## Console

Q:
  Is the console instruction case sensitive? <br>
A:
  It is case-sensitive. The command will match exactly, and `tab` can be used to complete your command.

Q:
  When adding to the Sealer list or the Observer list, it will report error as nodeID is not in network, why? <br>
A:
  The nodes that adds to the Sealer list and the Observer list must be a member of the nodeID list that connects to the peer.

Q:
  To delete node will report error as nodeID is not in group peers, why? <br>
A:
  The node to be delete must be the peer of the group displayed in getGroupPeers

Q:
  Can the RemoveNodes (non-group nodes) synchronize group data? <br>
A:
  RemoveNodes does not participate in the consensus, synchronization, and block generation within the group. RemoveNodes can add the exit node as Sealer/Observer through the command of console `addSealer/addObserver`.

Q:
  If the node belongs to a different group, can it support querying information of multiple groups? <br>
A:
  Yes, when you enter the console, you can input the groupID you want to view: ./start [groupID]

## FISCO BCOS using


Q:
  Where to get Ver 2.0 certificates?<br>
A:
  Please read [Certificates Decsription](manual/certificates.md)

Q:
  What fields are contained in the transaction structure of Ver 2.0?<br>
A:
  Please read [here](design/protocol_description.html#rlp)
Q:
  What are the system configuration, group configuration, and node configuration? <br>
A:
  System configuration refers to some configuration items that affect the ledger function and require the consensus of the ledger node in the node configuration.
  Group configuration refers to the configuration of the group which the node belongs to. Each group of nodes has an independent configuration.
  Node configuration refers to all configurable items.

Q:
  Can the group configuration be changed? <br>
A:
  Whether the configuration item could be changed can be measured by:

  - The node that is first time to launch and has generated a genesis block can not be modified. This type of configuration is placed in the group.x.genesis file, where x is the group number, and it is unique in the entire chain.

  - To implement consistence in ledger by sending the transaction modification configuration item.

  - After the configuration file is modified, the node can be restarted to takes effect. This type of configuration is placed in the `group.x.ini` file. After the group configuration is changed, the restart can be changed locally, the changeable item becomes the local configuration. The `group.*.ini` file under nodeX/conf is changed and restarted to takes effect. The involved configuration items are [tx_pool].limit (transaction pool capacity) and [consensus].ttl (node forwarding number).

Q:
  Which configurations can the group configuration user change? <br>
A:
  The group can be modified and configured into consensus changeable configuration and manual changeable configuration.

  - consensus changeable configuration: all nodes in the group are the same, and takes effect after consensus. [consensus].max_trans_num,[consensus].node.X,[tx].gas_limit.

  - manual changeable configuration: it is in the `group.x.ini` file and restarted to take effect after modification. It only affects node. The configuration item has [tx_pool].limit.

Q:
  How to change and inquire the consensus changeable configuration? <br>
A:
  Consensus changeable configuration can be changed through console. It can be inquired through console and RPC interface. For detail, please [refer to here] (./design/rpc.md).

  - [consensus].max_trans_num,[tx].gas_limit is changed by using the interface setSystemConfigByKey, and the corresponding configuration items are tx_count_limit, tx_gas_limit. See setSystemConfigByKey -h for details.

  - [consensus].node.X's change refers to node management. The console interface refer to addSealer, addObserver, removeNode. For detail, please refer to Node Management.

Q:
  What is the difference between Observer node and Sealer node in group? <br>
A:
  Observer node can synchronize the group data, but cannot participate in consensus. Consensus nodes have the Observer permission and  participate in consensus.

Q:
  How to incorporate contract into CNS management? <br>
A:
  When contract is deployed, to call the CNS contract interface, and to compile the information of contract name, version, and address information into the CNS list.

Q:
  How to query the contract CNS list?<br>
A:
  To query CNS list through the command of web3sdk console, and the query command is queried according to the contract name.

Q:
  Why can't local SDK connect to FISCO BCOS nodes on cloud servers?<br>
A:
  1. Check the node configuration on the cloud server to see if Channel is listening for IP over the extranet, rather than `127.0.0.1`. Port Description [Refer here](https://mp.weixin.qq.com/s/XZ0pXEELaj8kXHo32UFprg)
  2. Check the console provided by the cloud manufacturer of the cloud server, check whether the security group is configured, and open the channel port used by FISCO BCOS nodes in the security group.
  3. Check that the generated certificate is correct, [refer to here](./enterprise_tools/operation.md#node-configuration-error-checking)

Q:
  Why connection to other peer nodes can not be established after launching the node, and the log report there are network exceptions?
A:
  1. Please check that certificate files of the node are correct
  2. Please check that type (SM_CRYPTO or not) of the node is consistent with other peer nodes

Q：
  Why "invalid group status" error information appears in the log of node?

A：Due to possible fault of file system, group status recorded by node on local disk may be invalid. Users can check `.group_status` file in group's data folder, and change the content to the one of following items:

- STOPPED
- DELETED
- RUNNING

## Web3SDK

Q:
  What does Web3SDK require to Java version? <br>
A:
  It requires [JDK8 version or above](https://openjdk.java.net/)<br>

  The OpenJDK of yum repository of CentOS lacks JCE (Java Cryptography Extension), which causes Web3SDK to fail to connect to blockchain node. When using the CentOS operation system, it is recommended to download it from the OpenJDK website. [Installation Guide] (https://openjdk.java.net/install/index.html)

Q:
  After the Web3SDK configuration is completed, what is the reason for the failed transaction? <br>
A:
  The ip, port, group number in applicationContext.xml are incorrectly filled or the node files of ca.crt, sdk.crt, and sdk.key files are missing.

## Enterprise deployment tool
Q：
  There is pip cannot be found appears when using enterprise deployment tools.
A：
  The enterprise deployment tool relies on python pip. To install it with the following command:
```
$ bash ./scripts/install.sh
```

Q:
  When using enterprise deployment tools, the following information appears:
```
Traceback (most recent call last):
   File "./generator", line 19, in <module>
    from pys.build import config
   File "/data/asherli/generator/pys/build/config.py", line 25, in <module>
     import configparse
```
A:
  The python configparser module is missing from the system. Please follow the command below to install:

```bash
  $ pip install configparser
```

Q：
  downloading occurred certificate verify failed
答：
  vim ./pys/tool/utils.py, add this code in first line.

```python
import ssl
ssl._create_default_https_context = ssl._create_unverified_context
```

<!-- // TODO: -->
