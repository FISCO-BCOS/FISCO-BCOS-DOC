# Group members management

FISCO BCOS introduces [free nodes, observer nodes and consensus nodes](../design/security_control/node_management.html#id6) which can be converted to each other by the console.

* Group member
  * Consensus nodes (Sealer)
  * Observer nodes (Observer)
* Non-Group member
  * Free nodes (The node waiting for joining the group)

## Operation command

The console provides three commands of **[AddSealer](../console/console.html#addSealer)**, **[AddObserver](../console/console.html#addObserver)**, and **[RemoveNode](../console/console.html#removenode)** to convert the specified node to Sealer, Oberserver, and RemoveNode, and can use  **[getSealerList](../console/console.html#getSealerlist)**, **[getObserverList](../console/console.html#getObserverlist)**, and **[getNodeIDList](../console/console.html#getnodeidlist)** to view the current list of Sealer, Observer, and other nodes.

- addSealer: to set the corresponding node as the Sealer according to the NodeID;
- addObserver: to set the corresponding node as the Observer according to the NodeID;
- removeNode: to set the corresponding node as the RemoveNode according to the NodeID;
- getSealerList: to view the Sealer in the group;
- getObserverList: to view the Observer in the group;
- getNodeIDList: to view the NodeID in the group;

For example, to convert the specified nodes to Sealer, Observer, and RemoveNode, the main operation commands are as follows:

```eval_rst
.. important::

    Before accessing the node, please ensure that:

     - Node ID exists and can execute cat that is getting from conf/node.nodeid in the node directory

     - All Sealers are normal, and they will output +++ logs.

```

```bash
# to get Node ID (to set the directory as ~/nodes/192.168.0.1/node0/）
$ cat ~/fisco/nodes/192.168.0.1/node0/conf/node.nodeid
7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50

# to connect console (to set the console in the ~/fisco/console directory)
$ cd ~/fisco/console

$ bash start.sh

# to convert the specified node to Sealer
[group:1]> addSealer 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
# to view the list of Sealer
[group:1]> getSealerList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]

# to convert the specified node to Observer
[group:1]> addObserver 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50

# to view the list of Observer
[group:1]> getObserverList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]

# to convert the specified node to removeNode
[group:1]> removeNode 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50

# to view the list of NodeID
[group:1]> getNodeIDList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]
[group:1]> getSealerList
[]
[group:1]> getObserverList
[]

```

## Operation cases

The following describes the operations of group expansion and node exit in detail with specific operation cases. Group expansion is divided into two phases, namely **adding nodes to the network** and **adding nodes to the group**. Also, node exit is divided into two phases, namely **exiting node from the group** and **exiting node from the network**.

### Operation methods

- Node configuration modification: After the node modifies its own configuration, it need to be restarted to takes effect. The involved operations include **network adding/exit and CA blacklist inclusion/removal**.
- Transaction uploading to the chain: To modify the transaction, the configuration of group consensus is needed, and the operation involves **the modification of node type**. The current sending transaction path is the pre-compiled service interface provided by the console and SDK.
- RPC inquiry：To use the command curl to inquire the information on the chain, and the operation involves **the query of the group node**.

### Operation steps


In this section, the following figure is taken as an example to describe the operations of expansion and network exit show above.

The dotted line indicates that network communication can be performed among nodes, and the solid line indicates that nodes have group relationships base on the communication among nodes, and colors' difference distinguish different group relationships.

The below figure below shows a network with three groups of which Group3 has three nodes. Whether Group3 has intersection nodes with other groups does not affect the versatility of the following operation process.

![](../../images/node_management/multi_ledger_example.png)

<center> group example </center>
Here we take the following node information of Group 3 as an example:

The folder name of node 1 is `node0`, IP port 127.0.0.1:30400, the former 4 bytes of nodeID b231b309...

The folder name of node 2 is `node1`, IP port 127.0.0.1:30401, the former 4 bytes of nodeID aab37e73...

The folder name of node 3 is `node2`, IP port 127.0.0.1:30402, the former 4 bytes of nodeID d6b01a96...

#### Node A to join the network

Background:

Node 3 is outside the network and wants to join it now.

Operation steps:

1 . enter nodes folder and execute `gen_node_cert.sh` to generate node folder. Here we name the folder as node2, which contains `conf/` folder;

```
# acquire script
$ curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/gen_node_cert.sh && chmod u+x gen_node_cert.sh
# execute, -c is the ca route given when the node was generated, agency is the agency name, -o is the name of the node folder to be generated ( use -g when the node type is GM )
$ ./gen_node_cert.sh -c nodes/cert/agency -o node2
```

```eval_rst
.. note::
    - If download failed, please try `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/gen_node_cert.sh`
```

2 . copy node 2 under `nodes/127.0.0.1/`, parallel with other node folder (`node0`, `node1`);
```
$ cp -r ./node2/ nodes/127.0.0.1/
```

3 . enter `nodes/127.0.0.1/`, copy `node0/config.ini`, `node0/start.sh` and `node0/stop.sh` to node2 directory;

```
$ cd nodes/127.0.0.1/
$ cp node0/config.ini node0/start.sh node0/stop.sh node2/
```

4 . modify `node2/config.ini`. For `[rpc]` model, modify `channel_listen_port` and `jsonrpc_listen_port`; for `[p2p]` model, modify `listen_port` and add its node information in `node.`.

```eval_rst
.. note::
    For the convenience of development and experience, the recommended configuration of channel_listen_ip/listen_ip is `0.0.0.0`. For security considerations, please modify it to a safe listening address according to the actual business network situation, such as the internal IP or a specific external IP
```

```
$ vim node2/config.ini
[rpc]
    ;rpc listen ip
    channel_listen_ip=0.0.0.0
    jsonrpc_listen_ip=127.0.0.1
    ;channelserver listen port
    channel_listen_port=20302
    ;jsonrpc listen port
    jsonrpc_listen_port=8647
[p2p]
    ;p2p listen ip
    listen_ip=0.0.0.0
    ;p2p listen port
    listen_port=30402
    ;nodes to connect
    node.0=127.0.0.1:30400
    node.1=127.0.0.1:30401
    node.2=127.0.0.1:30402
```

5 . node 3 copies `node1/conf/group.3.genesis`(which contains **initial list of group nodes**) and `node1/conf/group.3.ini` to `node2/conf` folder, without modification;
```
$ cp node1/conf/group.3.genesis node2/conf/
$ cp node1/conf/group.3.ini node2/conf/
```

6 . execute `node2/start.sh` and start node 3;
```
$ ./node2/start.sh
```

7 . confirm that node 3 is connected with node 1, 2, then it has joined the network now.

```
# Open the DEBUG log to check the number and ID of nodes connected with node 2
# The following log information indicates that node 2 is connected with 2 nodes(with the former 4 bytes being b231b309 and aab37e73)
$ tail -f node2/log/log*  | grep P2P
debug|2019-02-21 10:30:18.694258| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30400,nodeID=b231b309...
debug|2019-02-21 10:30:18.694277| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30401,nodeID=aab37e73...
info|2019-02-21 10:30:18.694294| [P2P][Service] heartBeat connected count,size=2
```

```eval_rst
.. note::
	- If CA whitelist is enabled, you should configure every nodeID into every nodes' whitelist configuration and reload the configuration. Read "CA blacklist and whitelist" for more.
    - The other configurations of config.ini copied from node 1 remain the same;
    - Theoretically, node 1, 2 can accomplish the extension of node 3 without changing their p2p connecting nodes list;
    - The group to be chosen in step 5 are recommended to be the group to be joined by node 3;
    - To keep in full connection status, we recommend users to add the information of node 3 to the p2p connecting nodes list in config.ini of node 1, 2, and restart node 1, 2.
```

#### Node A to quit the network

Background:

Node 3 is in the network and connected with node 1, 2. Now node 3 needs to quit the network.

Operation steps:

1 . For node 3, clear up the **P2P connecting nodes list**, and restart node 3;

```
# execute under node 2
$ ./stop.sh
$ ./start.sh
nohup: appending output to ‘nohup.out’
```

2 . For node 1, 2, remove node 3 form their **P2P connecting nodes list**(if has), and restart node 1, 2;

3 . Confirm that node 3 has been disconnected with node 1, 2, and it has quitted the network now.

```eval_rst
.. note::
    - **node 3 has to quit the group before quitting the network, which is guaranteed by users and will not be verified by the system**;
    - the networking process is started by nodes. If missing step 2, node 3 can still get the p2p connecting request from node 1, 2 and start connection. It can be stopped by using CA blacklist.
    - If CA whitelist is enabled, you should delete the node from every nodes' whitelist configuration and reload the configuration. Read "CA blacklist and whitelist" for more.
```

#### Node A to join a group

Background:

Group 3 contains node 1, 2, either generates block in turn. Now node 3 wants to join the group.

Operation steps:

1. Node 3 joins the network;
2. Set node 3 as the **consensus node** using console addSealer according to the node ID;
3. check if the node ID of node 3 is included in the consensus nodes of group 3 through console getSealerList. If is, then it has joined the group successfully.

```eval_rst
.. note::
    - node ID of node 3 can be acquired through `cat nodes/127.0.0.1/node2/conf/node.nodeid`;
    - the first start of node 3 will write the configured group node initial list to the node system list, when the blocks stop synchronizing, **the node system lists of each node are the same**;
    - **node 3 needs to have access to the network before joining the group, which will be verified by the system**;
    - **the group fixed configuration file of node 3 should be the same with node 1, 2**.
```

#### Node A to quit the group

Background:

Group 3 contains node 1, 2, 3, either of which generates block in turn. Now node 3 wants to quit the group.

Operation steps:

1. set node 3 as **free node** according to its ID using console removeNode;
2. check if the node ID of node 3 is included in the consensus nodes of group 3 through console getSealerList. If not, then node 3 has quited the group.

Additional:

```eval_rst
.. note::
    - node 3 can quit the group as a consensus node or observer node.
```
