# CA blacklist and whitelist

This documents tells you how to use CA blacklist and whitelist. Read [the design of CA blacklist and whitelist ](../design/security_control/certificate_list.md) for more.

## CA blacklist

Use blacklist to reject connection coming from certain NodeIDs.

**Configure blacklist**

Modify `config.ini`

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    ;crl.0=
```

Restart the node.

```shell
$ bash stop.sh && bash start.sh
```

Check connections.

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

## CA whitelist

Use whitelist to reject all connections which NodeID is not belong to whitelist.

**Configure whitelist**

Modify`config.ini`，**If whitelist is empty, the whitelist is disable and accept all connections.**

```ini
[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

If the node is not started, use `start.sh`. The whitelist configuration is loaded during starting up. If the node has started, use `reload_whitelist.sh` . The whitelist configureation is refresh and reject disconnect the node not belongs to whitelist.

```shell
# If the node is not started.
$ bash start.sh
# If the node is started.
$ cd scripts
$ bash reload_whitelist.sh
node_127.0.0.1_30300 is not running, use start.sh to start and enable whitelist directlly.
```

Check connections.

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

## Usage: Public CA

If we build chain using the certificate coming from CFCA. This chain's CA is CFCA. This CA is public and can be used to build by other blockchain. Cause different chain can connect each other by using a same CA. In this case, we need to use whitelist to reject the connection coming from other blockchain.

**Build chain an enable whitelist**

1. Build chain.
2. Get every nodes' NodeID.
3. Add every nodes' NodeID into **every** Node's whitelist.
4. Start the node or use `reload_whitelist.sh` to reconfigure whitelist.

**Add node in blockchain and update whitelist**

1. Build a node based on a blockchain.
2. Get NodeID of new node.
3. Append new node's NodeID into **every** Node's whitelist.
4. Copy old node's whitelist to new node.
5. Reconfigure whitelist using `reload_whitelist.sh`.
6. Start new node.
7. Add new node into group (addSealer or addObserver)

## Examples

### Build blockchain

Build a blockchain contains 4 nodes.

```shell
$ bash build_chain.sh -l 127.0.0.1:4
```

Get NodeIDs of these nodes.

```shell
$ cat node*/conf/node.nodeid
219b319ba7b2b3a1ecfa7130ea314410a52c537e6e7dda9da46dec492102aa5a43bad81679b6af0cd5b9feb7cfdc0b395cfb50016f56806a2afc7ee81bbb09bf
7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
38158ef34eb2d58ce1d31c8f3ef9f1fa829d0eb8ed1657f4b2a3ebd3265d44b243c69ffee0519c143dd67e91572ea8cb4e409144a1865f3e980c22d33d443296
```

The NodeIDs are ：

- **node0**: 219b319b....
- **node1**: 7718df20....
- **node2**: f306eb10....
- **node3**: 38158ef3....

Start all nodes.

```shell
$ cd node/127.0.0.1/
$ bash start_all.sh
```

Check connections. Use node0 as example. (8545 is the rpc port of node0).

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

We can see, node0 has connected with 3 nodes.

```json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:62774",
      "Node": "node3",
      "NodeID": "38158ef34eb2d58ce1d31c8f3ef9f1fa829d0eb8ed1657f4b2a3ebd3265d44b243c69ffee0519c143dd67e91572ea8cb4e409144a1865f3e980c22d33d443296",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:62766",
      "Node": "node1",
      "NodeID": "7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    }
  ]
}
```



### Use blacklist: Reject connection from node1 to node0

Add node1's NodeID into node0's `config.ini`.

```shell
vim node0/config.ini
```

Like (Let whitelist empty):

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    crl.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    ; cal.0=

```

Restart to enable.

```shell
$ cd node0
$ bash stop.sh && bash start.sh
```

Check connections.

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

We can see node0 has connected with 2 nodes. Haven't connected with node1.

```json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30303",
      "Node": "node3",
      "NodeID": "38158ef34eb2d58ce1d31c8f3ef9f1fa829d0eb8ed1657f4b2a3ebd3265d44b243c69ffee0519c143dd67e91572ea8cb4e409144a1865f3e980c22d33d443296",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    }
  ]
}
```

### Use whitelist: Reject all connection except node1 and node 2

Add NodeIDs of node1 and node2 into node0's `config.ini`.

```shell
$ vim node0/config.ini
```

Let blacklist empty and add NodeIDs of node1 and node2 into whitelist

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    ;crl.0=

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

Restart the node.

```shell
$ bash stop.sh && bash start.sh
```

Check connections.

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

We can see, the node0 has connected with 2 nodes, not connected with node1.

```json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30301",
      "Node": "node1",
      "NodeID": "7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb",
      "Topic": []
    }
  ]
}
```



### Use blacklist and whitelist: The priority of blacklist is higher than whitelist

Modify `config.ini` of node0.

```shell
$ vim node0/config.ini
```

Add node1 into blacklist and add node1, node2 into whitelist. Both blacklist and whitelist has configured node1.

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    crl.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

Restart the node.

```shell
$ bash stop.sh && bash start.sh
```

Check connections.

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

We can see. Although node1 has been added into whitelist, node0 hasn't connected with node1. Because node1 has also added into blacklist. The priority of blacklist is higher than whitelist.

```json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    }
  ]
}
```