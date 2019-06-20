# CA blacklist

This section contains the brief introduction of blacklist, for the implementation please check [CA blacklist operation tutorial](../../manual/certificate_blacklist.md).

## Terms definition

## Terms definition

CA blacklist is also known as **Certificate Blacklist**, or CBL. CA blacklist verifies and denies connection requests from each node based on node ID in `[certificate_blacklist]` of `config.ini` configuration file.


Config types of CA blacklist:

- according to **effective area** (network config/ledger config), it belongs to **network config** and affects the node connection in the overall network;
- according to **modifiability** (modifiable config/fixed config), it belongs to **modifiable config**, modification valid after restart;
- according to **storage location** (local storage/store on chain), it belongs to **local storage**, content recorded in local instead of on chain.

## Model structure

The below diagram shows the model and relations of CA blacklist. A->B informs that model B depends on the data of model A, and it is later in initialization than model A.

![](../../../images/node_management/architecture.png)

<center>Model structure</center>

## Core process

SSL two-way certificate is implemented in the bottom level of FISCO BCOS. During handshake, nodes acquire each other's ID from its certificate and verify if it is among the CA blacklist. If is, close the connect and resume the later process.

## Effective area

- CA blacklist has evident influence in P2P node connection in network level and AMOP function by **invalidation**;
- CA blacklist has potential influence in consens and syncing in ledger level by **interfering message/data transfer**.

## Config format

`config.ini` node config adds `[certificate_blacklist]` route (`[certificate_blacklist]` is optional). CA blacklist contains node ID list. node.X indicates node ID of rejected nodes. Config format of CA blacklist is as below:

```
[certificate_blacklist]
    crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e3787c338a4
    crl.1=af57c506be9ae60df8a4a16823fa948a68550a9b6a5624df44afcd3f75ce3afc6bb1416bcb7018e1a22c5ecbd016a80ffa57b4a73adc1aeaff4508666c9b633a
```

## Expected functions

- Modification of CA blacklist becomes valid after restarting node. In the future, dynamic loading and real-time validation are expected to be realized;
- CA blacklist has realized black list based on node. In the future, we will consider blacklist based on agency.
