# Expansion of new nodes

Tags: "Air version of the blockchain network" "" Expansion ""

------

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

'build _ chain.sh 'provides the function of scaling new nodes. In this chapter, [Build the first blockchain network](../../quick_start/air_installation.md)A new blockchain node is expanded on the basis of FISCO BCOS to help users master the expansion steps of the Air version FISCO BCOS blockchain node。

```eval_rst
.. note::
   Before performing node scaling, refer to 'Building the first blockchain network'<../../quick_start/air_installation.html>'_ Deploy Air version blockchain。
```

## 1. Prepare documents required for expansion

When scaling the Air version of the blockchain, you need to prepare a certificate and configuration file in advance to generate the scaling node configuration. The files to be prepared include:

- **CA Certificates and CA Private Keys**: Used to issue a node certificate for a newly expanded node
- **Node configuration file 'config.ini'**: Can be copied from an existing node directory。
- **Node Genesis block configuration file 'config.genesis'**: Can be copied from an existing node directory。
- **Node connection configuration 'nodes.json'**: Configure the IP and port information of all node connections, which can be copied from the existing node directory and added with the IP and port of the new node。

```eval_rst
.. note::
   The root certificate of the Air version blockchain node is located in the directory generated during the connection, and you can enter the folder generated when the node is built(For example: 'Building the first blockchain network<../../quick_start/air_installation.html>'_ The generated node configuration folder is' nodes')Find the root certificate of the chain through "find. -name ca"
```

Here to [build the first blockchain network](../quick_start.md)For example, scale out a new node 'node4' based on 'node0':

```shell
# Enter the operation directory(Note: Before performing this operation, please refer to [Building the First Blockchain Network Node] to deploy an Air version FISCO BCOS blockchain)
$ cd ~/fisco

# Create a directory to store the expansion configuration
$ mkdir config

# Copy the root certificate and root certificate private key
$ cp -r nodes/ca config

# Copy the node configuration file config.ini, the creation block configuration file config.genesis, and the node connection configuration file nodes.json from the expanded node node0
$ cp nodes/127.0.0.1/node0/config.ini config/
$ cp nodes/127.0.0.1/node0/config.genesis config/
$ cp nodes/127.0.0.1/node0/nodes.json config/nodes.json.tmp

# Setting P2P and RPC Listening Ports for New Nodes
# macOS system (set P2P listening port to 30304, RPC listening port to 20204)
$ sed -i .bkp 's/listen_port=30300/listen_port=30304/g' config/config.ini
$ sed -i .bkp 's/listen_port=20200/listen_port=20204/g' config/config.ini
# Linux system (set P2P listening port to 30304, RPC listening port to 20204)
$ sed -i 's/listen_port=30300/listen_port=30304/g' config/config.ini
$ sed -i 's/listen_port=20200/listen_port=20204/g' config/config.ini

# Add a new node connection to nodes.json
$ sed -e 's/"nodes":\[/"nodes":\["127.0.0.1:30304",/' config/nodes.json.tmp > config/nodes.json
# Confirm new node connection information: 127.0.0.1:30304 Joined Successfully
$ cat config/nodes.json
{"nodes":["127.0.0.1:30304","127.0.0.1:30300","127.0.0.1:30301","127.0.0.1:30302","127.0.0.1:30303"]}
```

## 2. Expand new nodes

```eval_rst
.. note::
   -Please make sure that the "30304" and "20204" ports of the machine are not occupied
   - Please refer to 'Building the First Blockchain Network<../../quick_start/air_installation.html>"_ Download the build _ chain.sh script," "build _ chain" can be used here<./build_chain.html>`_
```

**Step 1: Generate the scaling node configuration**

After the configuration file is prepared, use the link creation script 'build _ chain.sh' to scale out the new node node4:

```shell
# Enter the operation directory
cd ~/fisco

# Call build _ chain.sh to expand the node. The new node is expanded to the nodes / 127.0.0.1 / node4 directory
# -c: Specify the paths of config.ini, config.genesis, and nodes.json
# -d: Specify the path to the CA certificate and private key
# -o: Specify the directory where the expansion node configuration is located
bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node4
```

When the node outputs' All completed.Files in nodes / 127.0.0.1 / node4 ', the scaling configuration is generated successfully. The output logs are as follows:

```shell
[INFO] Use binary bin/fisco-bcos
[INFO] generate_node_scripts ...
[INFO] generate_node_scripts success...
[INFO] generate_node_cert ...
[INFO] Generate nodes/127.0.0.1/node4/conf cert successful!
[INFO] generate_node_cert success...
[INFO] generate_node_account ...
[INFO] generate_node_account success...
[INFO] copy configurations ...
[INFO] copy configurations success...
==============================================================
[INFO] fisco-bcos Path       : .bin/fisco-bcos
[INFO] sdk dir         : nodes/127.0.0.1/sdk
[INFO] SM Model         : false
[INFO] output dir         : nodes/127.0.0.1/node4
[INFO] All completed. Files in nodes/127.0.0.1/node4
```

The node4 directory of the scaling node is as follows:

```shell
$ tree nodes/127.0.0.1/node4
nodes/127.0.0.1/node4
├── conf
│   ├── ca.crt
│   ├── cert.cnf
│   ├── node.nodeid
│   ├── node.pem
│   ├── ssl.crt
│   └── ssl.key
├── config.genesis
├── config.ini
├── nodes.json
├── start.sh
└── stop.sh
```

**Step 2: Start the expansion node**

```shell
bash nodes/127.0.0.1/node4/start.sh
```

## 3. Add the expansion node as an observation node

```eval_rst
.. note::
   -Start all nodes including the expansion node before performing this step
   - Please refer to the [Configuration and Use Console] of 'Building the First Blockchain Network'<../../quick_start/air_installation.html#id7>'_ Download Console
```

**Step 1: Check if all nodes are started**

```shell
ps aux |grep -v grep |grep fisco-bcos

fisco        79637   4.5  0.1  4979692  19072 s005  S     6:22 p.m. 0:11.49 /home/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini -g config.genesis
fisco        79695   4.4  0.1  4979692  19080 s005  S     6:22 p.m. 0:11.56 /home/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini -g config.genesis
fisco        79671   4.3  0.1  5241836  19192 s005  S     6:22 p.m. 0:11.59 /home/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini -g config.genesis
fisco        79717   4.3  0.1  4979692  19016 s005  S     6:22 p.m. 0:11.38 /home/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini -g config.genesis
fisco        78968   3.6  0.1  5110764  19116 s005  S     6:16 p.m. 0:21.27 /home/fisco/nodes//127.0.0.1/node4/../fisco-bcos -c config.ini -g config.genesis
```

**Step 2: Determine the Node Node ID**

```shell
# Enter the operation directory
$ cd ~/fisco

# Get the nodeID of a node
$ cat nodes/127.0.0.1/node4/conf/node.nodeid
51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105
```

**Step 3: Add the node as an observation node via the console**

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.0.0)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=============================================================================================
# Add an expansion node as an observation node
[group0]: /> addObserver 51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105
{
    "code":0,
    "msg":"Success"
}
# Confirm that the node is successfully added to the observation node
[group0]: /> getObserverList
[
    51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105
]
```

## 4. Add the expansion node as a consensus node

```eval_rst
.. note::
   In order to ensure that the new node does not affect the consensus, you must first add the expansion node as an observation node, and then add it to the consensus node when the expansion node is synchronized to the latest block。
```

```shell
# Add the scaling node as a consensus node
[group0]: /> addSealer 51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105 1
{
    "code":0,
    "msg":"Success"
}
# Confirm that the new node is successfully added as a consensus node
[group0]: /> getSealerList
[
    Sealer{
        nodeID='e77e78303f1dd1613285560da7b4bcd261d47bdd66762e10791cac529cc372f1f3f61a71cb3cdb14e9fec4a5de2f8defd3c28a2cfd7a9d158d51fdda7c65ca4e',
        weight=1
    },
    Sealer{
        nodeID='78fe081d9b14c60ef3eb49f1c7dcb20689bf548e476009454b1559a44b19affc32fe109510dd5f3e9be59b6b6636e12ca169654820637bb6797d1dc8bce89da1',
        weight=1
    },
    Sealer{
        nodeID='4248f278cef93b5ad83b0fc2eadbca5060c4afe291ea677c4c79fdb51ba85e79af7cf7e6fdc82aba351081431213235f400d572ff5355595b74624cd34d6d11d',
        weight=1
    },
    Sealer{
        nodeID='625f16370e88776b621c686f207c22fa694884af6e54d4586dbcf08dfc97e4d4d929115a99d1849c52ca1ae7be6adf1109f7614856b63466b7ec5879e834f26c',
        weight=1
    },
    Sealer{
        nodeID='51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105',
        weight=1
    }
]

# Deploying the HelloWorld Test Contract
[group0]: /> deploy HelloWorld
transaction hash: 0xbf9f7981cddce678fddde6eb1d5f9bdac5dc4c9161b2d8d56a8bb0ecd55aea62
contract address: 0xC8eAd4B26b2c6Ac14c9fD90d9684c9Bc2cC40085
currentAccount: 0x537149148696c7e6c3449331d77ddfaabc3c7a75

# Confirm that all nodes have reached the latest block
[group0]: /> getSyncStatus
SyncStatusInfo{
    isSyncing='false',
    protocolId='null',
    genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
    nodeId='78fe081d9b14c60ef3eb49f1c7dcb20689bf548e476009454b1559a44b19affc32fe109510dd5f3e9be59b6b6636e12ca169654820637bb6797d1dc8bce89da1',
    blockNumber='3',
    latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb',
    knownHighestNumber='3',
    txPoolSize='null',
    peers=[
        PeersInfo{
            nodeId='4248f278cef93b5ad83b0fc2eadbca5060c4afe291ea677c4c79fdb51ba85e79af7cf7e6fdc82aba351081431213235f400d572ff5355595b74624cd34d6d11d',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        },
        PeersInfo{
            nodeId='51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        },
        PeersInfo{
            nodeId='625f16370e88776b621c686f207c22fa694884af6e54d4586dbcf08dfc97e4d4d929115a99d1849c52ca1ae7be6adf1109f7614856b63466b7ec5879e834f26c',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        },
        PeersInfo{
            nodeId='e77e78303f1dd1613285560da7b4bcd261d47bdd66762e10791cac529cc372f1f3f61a71cb3cdb14e9fec4a5de2f8defd3c28a2cfd7a9d158d51fdda7c65ca4e',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        }
    ],
    knownLatestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
}
```

So far, the 'build _ chain.sh' script has been used to complete the expansion of the Air version FISCO BCOS. For more information about how to use the 'build _ chain.sh' script, see [here](./build_chain.md)。For the expansion of Pro version FISCO BCOS, please refer to [Pro version FISCO BCOS node expansion](../../tutorial/pro/expand_node.md)。
