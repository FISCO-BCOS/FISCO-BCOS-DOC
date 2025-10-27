# Config file

The config files of FISCO BCOS generator are placed under ./conf folder, including group's genesis block config file `group_genesis.ini`, node config file `node_deployment.ini`.

The user configures node config file folder by operations on files under the conf folder.

## Metadata folder meta

The meta folder of FISCO BCOS generator is metadata folder to store `fisco-bcos` binaries, chain certificate `ca.crt`, agency certificate `agency.crt`, agency private key and node certificate, group genesis block file, and so on.

The format of stored certificates should be cert_p2pip_port.crt. For example: cert_127.0.0.1_30300.crt.

FISCO BCOS generator will generate nodes config file according to the certificates under the meta folder and the config files under the conf folder.

## group_genesis.ini

Through modifying the configuration of `group_genesis.ini`, the user generates a configuration of new group genesis block under the specific directory and meta folder. Such as `group.1.genesis`.

```ini
[group]
group_id=1

[nodes]
;node p2p address of group genesis block
node0=127.0.0.1:30300
node1=127.0.0.1:30301
node2=127.0.0.1:30302
node3=127.0.0.1:30303
```

```eval_rst
.. important::
    Node certificate is needed during generating genesis block. In the above case, the config file needs 4 nodes' certificates, which are: cert_127.0.0.1_30301.crt, cert_127.0.0.1_30302.crt, cert_127.0.0.1_30303.crt and cert_127.0.0.1_30304.crt.
```

## node_deployment.ini

Through modifying `node_deployment.ini` configuration, user can use --build_install_package command to generate node config file containing no private key under a specific folder. Each `section[node]` configured by the user is the needed node config file folder. `section[peers]` is the p2p information for connection with other nodes.

For example:

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
channel_ip=0.0.0.0
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
channel_ip=0.0.0.0
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
```

Read the node config command. To generate node certificate and node config file folder will need to read the config file.

## Template folder tpl

The template folder of the generator is as below:

```bash
├── config.toml # sdk config file
├── config.ini # node config file template
├── config.ini.gm # OSCCA node config file template
├── group.i.genesis # group genesis block template
├── group.i.ini # group block configuration template
├── start.sh  # start node script template
├── start_all.sh # start nodes in batch script template
├── stop.sh # stop node script template
└── stop_all.sh # stop nodes in batch template
```

To modify the consensus algorithm of node and the configured default DB, the user only needs to alter the configuration of `config.ini`, re-execute the commands to set relative node information.

For details of FISCO BCOS configuration please check [FISCO BCOS config file](../manual/configuration.md)

## P2p node connection file peers.txt

P2P node connection file `peers.txt` is the node connection information of the **other agencies** specified when generating node config file folder. When executing `build_install_package` command, it's needed to determine the p2p node connection file `peers.txt`, according to which node config file folder will start communication with other nodes.

User that executes `generate_all_certificates` command generates `peers.txt` according to the `node_deployment.ini` filled under `conf` directory. The user that adopts other ways to generate certificate needs to generate p2p node connection file manually and send to peers. The format of the p2p node connection file is:

```bash
127.0.0.1:30300
127.0.0.1:30301
```

Format like this: node ip:p2p_listen_port

-   for multi-agency node communication, the files need to be combined
