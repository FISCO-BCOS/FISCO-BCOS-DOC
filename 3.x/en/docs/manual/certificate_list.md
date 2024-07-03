# Configure CA Blacklist

Tags: "CA black and white list" "development manual" "refused to connect"

----

This document describes the practical operation of CA black and white lists. It is recommended that you understand [Introduction to CA Black and White Lists] before reading this operation document(../design/security_control/certificate_list.md)。

## Blacklist

By configuring the blacklist, you can refuse to connect to the specified node。

**Configuration Method**

Edit 'config.ini'

``` ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    ;crl.0=
```

Restart the node to take effect

``` shell
$ bash stop.sh && bash start.sh
```

View Node Connections

``` shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

## Whitelist

By configuring the whitelist, you can connect to only the specified nodes and deny connections to nodes outside the whitelist。

**Configuration Method**

Edit 'config.ini',**No configuration means that the whitelist is closed and a connection can be established with any node。**

```ini
[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

If the node is not started, start the node directly. If the node is started, use the script 'reload _ whitelist.sh' to refresh the whitelist configuration。

```shell
# If the node is not started
$ bash start.sh
# If the node is started
$ cd scripts
$ bash reload_whitelist.sh
node_127.0.0.1_30300 is not running, use start.sh to start and enable whitelist directlly.
```

View Node Connections

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

## Usage scenario: Public CA

All chains that use CFCA to issue certificates, the CA of the chain is CFCA。This CA is shared。Whitelist feature must be enabled。If you use a common CA, the two chains share the same CA. As a result, the nodes of the two unrelated chains can be connected to each other。At this point, you need to configure a whitelist to deny connections to nodes in unrelated chains。

**Chain operation steps**

1. Use tools to link
2. Query the NodeID of all nodes
3. Configure all NodeIDs in**Each**node in the whitelist
4. Start the node or refresh the node whitelist configuration with the script 'reload _ whitelist.sh'

**Expansion operation steps**

1. Use tools to expand a node
2. Query the NodeID of this expansion node
3. Put this NodeID**Append**Go to the whitelist configuration of all inbound nodes
4. Copy the whitelist configuration of other nodes to the newly expanded node
5. Use the script 'reload _ whitelist.sh' to refresh the whitelist configuration of all started nodes
6. Start the expansion node
7. Add the expansion node to the group member (addSealer or addObserver)

## Examples of black and white list operations

### Prepare

Build a chain of four nodes

``` bash
bash build_chain.sh -l 127.0.0.1:4
```

View the NodeIDs of four nodes

``` shell
$ cat node*/conf/node.nodeid
219b319ba7b2b3a1ecfa7130ea314410a52c537e6e7dda9da46dec492102aa5a43bad81679b6af0cd5b9feb7cfdc0b395cfb50016f56806a2afc7ee81bbb09bf
7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
38158ef34eb2d58ce1d31c8f3ef9f1fa829d0eb8ed1657f4b2a3ebd3265d44b243c69ffee0519c143dd67e91572ea8cb4e409144a1865f3e980c22d33d443296
```

The NodeIDs of the four nodes are available:

* **node0**: 219b319b....
* **node1**: 7718df20....
* **node2**: f306eb10....
* **node3**: 38158ef3....

Start all nodes

``` shell
$ cd node/127.0.0.1/
$ bash start_all.sh
```

View the connection, for example, node0。(8545 is the rpc port of node0)

``` shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

You can see the connection information, node0 is connected to the other three nodes in addition to itself。

``` json
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



### Configure blacklist: node0 rejects node1 connection

Write node1's NodeID to node0's configuration

``` shell
vim node0/config.ini
```

The required configuration is as follows. The whitelist is empty (closed by default)

``` ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    crl.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    ; cal.0=

```

Restart the node to take effect

``` shell
$ cd node0
$ bash stop.sh && bash start.sh
```

View Node Connections

``` shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

You can see that only two nodes are connected, not node1

``` json
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



### Configure the whitelist: node0 refuses to connect to nodes other than node1 and node2

Write the NodeIDs of node1 and node2 to node0's configuration

```shell
$ vim node0/config.ini
```

The required configuration is as follows, blacklist is empty, whitelist is configured on node1, node2

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    ;crl.0=

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

Restart the node to take effect

```shell
$ bash stop.sh && bash start.sh
```

View Node Connections

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

You can see that only two nodes are connected, not node3

``` json
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



### Mixed configuration of blacklist and whitelist: The priority of the blacklist is higher than that of the whitelist, and the connection to node1 is refused based on the whitelist configuration

Edit the configuration of node0

```shell
$ vim node0/config.ini
```

The required configuration is as follows: node1 on the blacklist configuration, node1 and node2 on the whitelist configuration

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    crl.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

Restart the node to take effect

```shell
$ bash stop.sh && bash start.sh
```

View Node Connections

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

Although node1 is configured on the whitelist, node0 cannot establish a connection with node1 because node1 is also configured in the blacklist

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
