# CA Black and White List

Tags: "security control" "network security" "black and white list"

----

This document provides an introductory description of black and white lists. For practical methods, please refer to [CA Black and White List Operation Manual](../../manual/certificate_list.md)。

## noun explanation

**CA Blacklist**

* alias**Certificate Deny List**Certificate blacklist (CBL)。The CA blacklist is determined based on the NodeID configured in the 'config.ini' file '[certificate _ blacklist]' to reject the connection initiated by this NodeID node。

**CA Whitelist**

* alias**Certificate Acceptance List**(Certificate Whitelist, or CAL)。The CA whitelist is determined based on the NodeID configured in the 'config.ini' file '[certificate _ whitelist]' to reject connections initiated by all nodes except the whitelist。

**Configuration type of CA black and white list**

- 基于**Scope of action**(Network Configuration / Ledger Configuration) dimensions can be divided into**Network Configuration**, which affects the node connection establishment process of the entire network；
- 基于**Whether it can be changed**(reconfigurable / fixed configuration) dimensions can be divided into**Configurable**, content can be changed, effective after restart；
- 基于**Storage position**(local storage / on-chain storage) dimensions can be divided into**Local Storage**The content is recorded locally, not on the chain.。

## Module Architecture

The following figure shows the modules involved in the CA blacklist and their relationships。Legend A-> B indicates that the B module depends on the data of the A module, and the B module is initialized later than the A module。The whitelist has the same architecture as the blacklist。

![](../../../images/node_management/architecture.png)

< center > Module architecture < / center >

## Core Process

Underlying implementation of SSL two-way authentication。During the handshake process, the node obtains the nodeID of the other node through the certificate provided by the other node, and checks whether the nodeID is related to the black and white list of the node configuration.。If the connection is rejected based on the configuration of the black and white lists, continue the subsequent process。

**Rejection logic**

* Blacklist: Deny connections to nodes written in the blacklist
* Whitelist: Deny connections to all nodes that are not configured in the whitelist。The whitelist is empty, indicating that it is not open. Any connection is accepted.。

**Priority**

Blacklist takes precedence over whitelist。For example, if A, B, and C are configured in the whitelist, D's connection will be rejected. If A is also configured in the blacklist, A will also be rejected.。

## Scope of influence

- CA black and white lists have a significant impact on P2P node connectivity and AMOP functionality at the network layer.**invalidate**；
- potential impact on the consensus and synchronization capabilities of the ledger layer,**Affects consensus and synchronization message / data forwarding**。

## Configuration Format

**Blacklist**

Add the '[certificate _ blacklist]' path to the node 'config.ini' configuration ('[certificate _ blacklist]' is optional in the configuration)。The content of the CA blacklist is the Node ID list of the node, and crl.X is the Node ID of the opposite node that this node refuses to connect to.。An example of the configuration format of the CA blacklist is as follows。

```ini
[certificate_blacklist]
    crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e3787c338a4
    crl.1=af57c506be9ae60df8a4a16823fa948a68550a9b6a5624df44afcd3f75ce3afc6bb1416bcb7018e1a22c5ecbd016a80ffa57b4a73adc1aeaff4508666c9b633a
   
```

**Whitelist**

Add the '[certificate _ whitelist]' path to the node 'config.ini' configuration ('[certificate _ whitelist]' is optional in the configuration)。The content of the CA whitelist is the Node ID list of the node. Cal.X is the Node ID of the opposite node to which the node can accept connections.。An example of the configuration format of the CA whitelist is as follows。

``` ini
[certificate_whitelist]
    cal.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e3787c338a4
    cal.1=af57c506be9ae60df8a4a16823fa948a68550a9b6a5624df44afcd3f75ce3afc6bb1416bcb7018e1a22c5ecbd016a80ffa57b4a73adc1aeaff4508666c9b633a
```
