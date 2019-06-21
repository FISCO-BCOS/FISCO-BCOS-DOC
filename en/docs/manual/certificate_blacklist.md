# CA blacklist

This document describes the practical operation of CA blacklist. It is recommended that you read this document before you realize [CA Blacklist Introduction].(../design/security_control/certificate_blacklist.md).

The operations of the CA blacklist include **a node list to/remove from the CA blacklist**, and are implemented through modifying the configuration file and restarting.

## Revised scope

The node configuration `config.ini` has `[certificate_blacklist]` path (optional). `[certificate_blacklist]` is the list of NodeID. node.X is the other node NodeID that  the own node refuses to connect.

## Revised example

There are three nodes which are interconnected in the network. The related information of the nodes is:

The directory of node 1 is node0, IP port is 127.0.0.1:30400, and the first four bytes of nodeID are b231b309...

The directory of node 2 is node1, IP port is 127.0.0.1:30401, and the first four bytes of nodeID are aab37e73...

The directory of node 3 is node2, IP port is 127.0.0.1:30402, and the first four bytes of nodeID are d6b01a96...

### Node A puts Node B in the CA blacklist

**scenario description：**

Node 1 and node 2 are in the same group. Other nodes in the group and them are taking turn to generate block. Now node 1 puts Node 2 to its own blacklist.

**operation steps:**

1. For node0, to put the public key nodeID of node1 to its own **CA blacklist**;

```
$ cat node1/conf/node.nodeid 
aab37e73489bbd277aa848a99229ab70b6d6d4e1b81a715a22608a62f0f5d4270d7dd887394e78bd02d9f31b8d366ce4903481f50b1f44f0e4fda67149208943
$ vim node0/config.ini
;certificate blacklist
[certificate_blacklist]
    ;crl.0 should be nodeid, nodeid's length is 128 
    crl.0=aab37e73489bbd277aa848a99229ab70b6d6d4e1b81a715a22608a62f0f5d4270d7dd887394e78bd02d9f31b8d366ce4903481f50b1f44f0e4fda67149208943
```

2. Restart node 1;

```
# Execute in the node1 directory
$ ./stop.sh
$ ./start.sh
nohup: appending output to ‘nohup.out’
```

3. Confirm that node 1 and node 2 are no longer connected by the log. The operation of putting to blacklist is completed.

```
# Under the premise of opening the DEBUG level log, to check the number of nodes connected to the own node (node2) and the connected node information (nodeID).

# The following log indicates that the node2 is connected with two nodes (the first 4 bytes of nodeID are b231b309 and aab37e73).
$ tail -f node2/log/log*  | grep P2P
debug|2019-02-21 10:30:18.694258| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30400,nodeID=b231b309...
debug|2019-02-21 10:30:18.694277| [P2P][Service] heartBeat ignore connected,endpoint=127.0.0.1:30401,nodeID=aab37e73...
info|2019-02-21 10:30:18.694294| [P2P][Service] heartBeat connected count,size=2
```

Additional instructions:

- Node 0 adds node 1 to its CA blacklist, node 0 will disconnect the connection and AMOP communication with node 1;

### Node A revokes Node B from CA blacklist 

scenario description：

Node 1 has the nodeID of node 2 in its own CA blacklist, and node 2 has not the nodeID of node 1 in its own CA blacklist. Now node 1 revoke node 2 from its own CA blacklist.

operation steps:

1. For node 1, to revoke the public key nodeID of node 2 to its own **CA blacklist**;

2. Restart node 1;

3. Confirm that node 1 and node 2 re-establish the connection by the log. The operation of revoking from blacklist is completed.
