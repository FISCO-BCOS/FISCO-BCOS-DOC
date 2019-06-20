# Deploy a multi-group structure

In this chapter we adopts star networking and parallel networking topology as an example to tutor on following contents:

- creating multi-group blockchain installer package by `build_chain.sh`;
- organization of the multi-group blockchain installation package directory created by `build_chain.sh`;
- activating the node and checking the consensus status of each group through log;
- sending transactions to each group and view block generation status of groups through log;
- nodes management in groups, including in-and-out of nodes;
- creating new group

```eval_rst
.. important::
    - build_chain.sh adapts to fast networking of chains for developers and users, not for capacity expansion
    - build business level transaction chain, we provide `Deployment Tool <../enterprise_tools/index.html>`_ for easier networking    
```

## Star topology and parallel multi-group networking

As what shows below, star networking topology and parallel multi-group networking topology are broadly used networking methods in blockchain application.

- **Star topology**: the center node belongs to multiple groups and runs applications from agencies of different groups;
- **Parallel multi-group networking**: each node belongs to multiple groups so that participants can extend or grow their business.

![](../../images/group/group.png)

We will take examples of 7-nodes star topology and 4-nodes parallel networking to explain the operation of multi-group structure in blockchain.

## Installation dependency

Before deploying nodes on FISCO BCOS, we need to install dependent software first, like `openssl，curl`, etc. The commands are as followed:

```bash
# CentOS
$ sudo yum install -y openssl curl

# Ubuntu
$ sudo apt install -y openssl curl

# Mac OS
$ brew install -y openssl curl
```

## Star topology

In this chapter, we will introduce the operation method of multi-group structure according to the above-showed star networking topology of **1 agency, 4 agencies, 3 groups and 7 nodes.

Star networking of blockchain:

- `agencyA`: belongs to `group1、group2、group3`, including 2 nodes with the same IP address `127.0.0.1`;
- `agencyB`: belongs to `group1`, including 2 nodes with the same IP address `127.0.0.1`;
- `agencyC`: belongs to `group2`, including 2 nodes with the same IP address `127.0.0.1`;
- `agencyD`: belongs to `group3`, including 2 nodes with the same IP address `127.0.0.1`.

```eval_rst
.. important::
   - In actual application cases, it is **not recommended to deploy multiple nodes on one machine**. We suggest users to determine node quantity for deployment depending on, **machine loading**. For reference please check `hardware configuration <../manual/configuration.html>`_
   - **Star networking topology** requires the center node (or agency A here) to be owned by all groups with heavy loading, so we recommend to **deploy it on a machine with sound performance.**
   - **To operate with different machine, users need to copy the folder that contains IP to the other machine. Operation for blockchain building would only be done once!**
```

### To create folder for node configuration on blockchain

Any kind of multi-group networking can be achieved by [build_chain.sh](../manual/build_chain.md). And the node configuration folder for star topology can be created using this script:

**Prepare for dependency**

- create an operation directory

```bash
mkdir -p ~/fisco && cd ~/fisco
```

- get the build_chain.sh script
```bash
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/`curl -s https://api.github.com/repos/FISCO-BCOS/FISCO-BCOS/releases | grep "\"v2\." | sort -u | tail -n 1 | cut -d \" -f 4`/build_chain.sh && chmod u+x build_chain.sh
```

**Generate configuration file for star networking blockchain system**

```bash
# generate configuration file ip_list
$ cat > ipconf << EOF
127.0.0.1:2 agencyA 1,2,3
127.0.0.1:2 agencyB 1
127.0.0.1:2 agencyC 2
127.0.0.1:2 agencyD 3
EOF

# view the content of configuration file ip_list
$ cat ipconf
# meaning of the space-separated parameters:
# ip:num: IP of physical machine and number of nodes
# agency_name: agency name
# group_list: the list of groups that nodes belong to, groups are separated by comma
127.0.0.1:2 agencyA 1,2,3
127.0.0.1:2 agencyB 1
127.0.0.1:2 agencyC 2
127.0.0.1:2 agencyD 3
```

**Create node configuration folder for star networking using build_chain script**

The operation method of `build_chain` is introduced [here](../manual/build_chain.md).

```bash
# generate blockchain of star networking and make sure ports 30300~30301, 20200~20201, 8545~8546 of machine are not occupied
$ bash build_chain.sh -f ipconf -p 30300,20200,8545
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:2 Agency:agencyA Groups:1,2,3
Processing IP:127.0.0.1 Total:2 Agency:agencyB Groups:1
Processing IP:127.0.0.1 Total:2 Agency:agencyC Groups:2
Processing IP:127.0.0.1 Total:2 Agency:agencyD Groups:3
==============================================================
......Here to omit other outputs......
==============================================================
[INFO] FISCO-BCOS Path   : ./bin/fisco-bcos
[INFO] IP List File      : ipconf
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:2 127.0.0.1:2 127.0.0.1:2 127.0.0.1:2
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /home/ubuntu16/fisco/nodes
[INFO] CA Key Path       : /home/ubuntu16/fisco/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /home/ubuntu16/fisco/nodes

# the generated node file is as follows:
nodes
|-- 127.0.0.1
|   |-- fisco-bcos
|   |-- node0
|   |   |-- conf  #node configuration folder
|   |   |   |-- ca.crt
|   |   |   |-- group.1.genesis
|   |   |   |-- group.1.ini
|   |   |   |-- group.2.genesis
|   |   |   |-- group.2.ini
|   |   |   |-- group.3.genesis
|   |   |   |-- group.3.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid # records of Node ID
|   |   |-- config.ini #node configuration file
|   |   |-- start.sh  #start script of node
|   |   `-- stop.sh   #stop script of node
|   |-- node1
|   |   |-- conf
......Here to omit other outputs......
```

```eval_rst
.. note::
   if the generated node belongs to another physical machine, users should copy the node to the physical machine it belongs.
```

**Start node**

Nodes are equipped with `start_all.sh` and `stop_all.sh` script to start and stop nodes.

```bash
# enter node folder
$ cd ~/fisco/nodes/127.0.0.1

# start node
$ bash start_all.sh

# check node progress
$ ps aux | grep fisco-bcos
ubuntu16         301  0.8  0.0 986644  7452 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node5/../fisco-bcos -c config.ini
ubuntu16         306  0.9  0.0 986644  6928 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node6/../fisco-bcos -c config.ini
ubuntu16         311  0.9  0.0 986644  7184 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node7/../fisco-bcos -c config.ini
ubuntu16      131048  2.1  0.0 1429036 7452 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
ubuntu16      131053  2.1  0.0 1429032 7180 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
ubuntu16      131058  0.8  0.0 986644  7928 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
ubuntu16      131063  0.8  0.0 986644  7452 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
ubuntu16      131068  0.8  0.0 986644  7672 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node4/../fisco-bcos -c config.ini
```

**Check consensus status of groups**

When there is no transaction to send, nodes with normal consensus status will output `+++` log. In this example, `node0` and `node1` all belong to `group1`, `group2` and `group3`; `node2` and `node3` belong to `group1`; `node4` and `node5` belong to `group2`; `node6` and `node7` belong to `group3`. We can check the status of node through `tail -f node*/log/* | grep "++"`.

```eval_rst
.. important::

    nodes with normal consensus status print ``+++`` log, fields in ``+++`` log are defined as:
     - ``g:``: group ID;
     - ``blkNum``: the newest block number generated by Leader node;
     - ``tx``: transaction volume contained in the new block;
     - ``nodeIdx``: index of the node;
     - ``hash``: hash of the newest block generated by consensus nodes.
```

```bash
# check if node0 group1 is in normal consensus status
$ tail -f node0/log/* | grep "g:1.*++"
info|2019-02-11 15:33:09.914042| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=72254a42....

# check if node0 group2 is in normal consensus status
$ tail -f node0/log/* | grep "g:2.*++"
info|2019-02-11 15:33:31.021697| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=ef59cf17...

# ... You can check if all groups of node1 and node2 are in normal status using the above method...

# check if node3 group1 is in normal consensus status
$ tail -f node3/log/*| grep "g:1.*++"  
info|2019-02-11 15:39:43.927167| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=5e94bf63...

# check if node5 group2 is in normal consensus status
$ tail -f node5/log/* | grep "g:2.*++"
info|2019-02-11 15:39:42.922510| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=b80a724d...

```

### Configuration console

The console connects nodes on FISCO BCOS through Web3SDK to get quick information with function of blockchain status inquiry and contract deployment. For reference of the console instruction please check [here](../manual/console.md).

```eval_rst
.. important::
   The console depends on system of Java 8 version and above. Ubuntu 16.04 system needs to install openjdk 8. For CentOS please install Oracle Java 8 version and above.
```

```bash
# back to fisco folder
$ cd ~/fisco

# acquire console
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/download_console.sh)

# enter operation folder of console
$ cd console

# copy node certificate of group 2 to the configuration folder of console
$ cp ~/fisco/nodes/127.0.0.1/sdk/* conf/

# acquire channel_listen_port of Node 0
$ grep "channel_listen_port" ~/fisco/nodes/127.0.0.1/node*/config.ini
/home/ubuntu16/fisco/nodes/127.0.0.1/node0/config.ini:    channel_listen_port=20200
/home/ubuntu16/fisco/nodes/127.0.0.1/node1/config.ini:    channel_listen_port=20201
/home/ubuntu16/fisco/nodes/127.0.0.1/node2/config.ini:    channel_listen_port=20202
/home/ubuntu16/fisco/nodes/127.0.0.1/node3/config.ini:    channel_listen_port=20203
/home/ubuntu16/fisco/nodes/127.0.0.1/node4/config.ini:    channel_listen_port=20204
/home/ubuntu16/fisco/nodes/127.0.0.1/node5/config.ini:    channel_listen_port=20205
/home/ubuntu16/fisco/nodes/127.0.0.1/node6/config.ini:    channel_listen_port=20206
/home/ubuntu16/fisco/nodes/127.0.0.1/node7/config.ini:    channel_listen_port=20207

```

```eval_rst
.. important::
    When connecting node with the console, we should make sure that the connected nodes are inside the group configured by the console
```

**Create console configuration file `conf/applicationContext.xml`'s configuration is as follows**. Console is connected to three groups from node0 (`127.0.0.1:20200`), for console configuration method please refer to [here](../manual/console.html#id7).

```xml
cat > ./conf/applicationContext.xml << EOF
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
           xmlns:tx="http://www.springframework.org/schema/tx" xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
         http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
         http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">


        <bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
                <constructor-arg value="0"/> <!-- 0:standard 1:guomi -->
        </bean>

      <bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
        <property name="allChannelConnections">
        <list>
            <bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="1" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20200</value>
                    </list>
                    </property>
            </bean>
            <bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="2" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20200</value>
                    </list>
                    </property>
            </bean>
            <bean id="group3"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="3" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20200</value>
                    </list>
                    </property>
            </bean>
        </list>
        </property>
        </bean>

        <bean id="channelService" class="org.fisco.bcos.channel.client.Service" depends-on="groupChannelConnectionsConfig">
                <property name="groupId" value="1" />
                <property name="orgID" value="fisco" />
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>
</beans>
EOF
```

**Start the console**

```bash
$ bash start.sh
# the following outputted information means it has been successfully started, otherwise please check if the port configuration of the nodes in conf/applicationContext.xml is correct.
=====================================================================================
Welcome to FISCO BCOS console(1.0.3)!
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

=====================================================================================
[group:1]>
```

### Send transaction to groups

In the above section, we learned how to configure console, this section will introduce the method to send transactions to groups through the console.

```eval_rst
.. important::

   In multi-group structure, the ledgers are independent in each group. And sending transaction to one group will only lead in the increment of block number in this group but not others
```

**Send transaction through console**

```bash
# ... send transaction to group 1...
$ [group:1]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# check the current block number of group 1, if it's 1 then the block generation is in normal status, otherwise please check if the consensus of group 1 is normal or not
$ [group:1]> getBlockNumber
1

# ... send transaction to group 2...
# switch to group 2
$ [group:1]> switch 2
Switched to group 2.
# send transaction to group 2, if it is returned with the hash of this transaction, then it has been deployed successfully, otherwise please check if group2 is in normal consensus status
$ [group:2]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# check the current block number of group 2, if it is 1, then the block generation is in normal status, otherwise please check if group 2 is in normal consensus status
$ [group:2]> getBlockNumber
1

# ... send transaction to group 3...
# switch to group 3
$ [group:2]> switch 3
Switched to group 3.
# send transaction to group 3, if it is returned with the hash of this transaction, then it has been deployed successfully
$ [group:3]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# check the current block number of group 3, if it is 1 then the block generation is in normal status, otherwise please check if group 3 is in normal consensus status
$ [group:3]> getBlockNumber
1

# ... switch to group 4 which doesn't exist, the console will hint that group 4 doesn't exist and output the current group list ...
$ [group:3]> switch 4
Group 4 does not exist. The group list is [1, 2, 3].

# exit console
$ [group:3]> exit
```

**Check the log**

After the block is generated, the node will output `Report` log, which contains fields with following definitions:

```eval_rst
.. important:

    once a new block is generated, node will print a Report log which contains fields with following definitions:
     - ``g:``: group ID
     - ``num``: block number
     - ``sealerIdx``: index of the consensus node;
     - ``hash``: the hash of the block;
     - ``next``: the number of next block;
     - ``tx``: transaction volume in a block;
     - ``nodeIdx``: index of the current node.
```

```bash
# enter the node folder
$ cd ~/fisco/nodes/127.0.0.1

# check the block generation status of group 1: new block generated
$ cat node0/log/* |grep "g:1.*Report"
info|2019-02-11 16:08:45.077484| [g:1][p:264][CONSENSUS][PBFT]^^^^^^^^Report,num=1,sealerIdx=1,hash=9b5487a6...,next=2,tx=1,nodeIdx=2

# check the block generation status of group 2: new block generated
$ cat node0/log/* |grep "g:2.*Report"
info|2019-02-11 16:11:55.354881| [g:2][p:520][CONSENSUS][PBFT]^^^^^^^^Report,num=1,sealerIdx=0,hash=434b6e07...,next=2,tx=1,nodeIdx=0

# check the block generation status of group 3: new block generated
$ cat node0/log/* |grep "g:3.*Report"
info|2019-02-11 16:14:33.930978| [g:3][p:776][CONSENSUS][PBFT]^^^^^^^^Report,num=1,sealerIdx=1,hash=3a42fcd1...,next=2,tx=1,nodeIdx=2

```

### Add nodes to group

Groups on FISCO BCOS can add or remove nodes through consoles. For details please check [Node Access Management](../manual/node_management.md), for console configuration please check [Console Operations](../manual/console.html#id7)。

This chapter will take node 2 and group 2 as an example to explain how to add new node to an existed group.

```eval_rst
.. important:

    Before adding new node to group, please make sure:

    - The new NodeID exists.
    - All nodes in the group are in normal consensus status: normal consensus node will output +++ log
````

**Copy group 2 configuration to node 2**

```bash
# enter node folder
$ cd ~/fisco/nodes/127.0.0.1

# ... copy group 2 configuration from node 0 to node 2...
$ cp node0/conf/group.2.* node2/conf

# ...restart node 2(them make sure the node is in normal consensus status)...
$ cd node2 && bash stop.sh && bash start.sh
```

**Acquire the ID of node 2**

```bash
# please remember the ID of node 2, which is needed when joining group 2
$ cat conf/node.nodeid
6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4
```

**Send command to group 2 through console and add node 2**

```bash

# ...back to console folder and start console...
$ cd ~/fisco/console && bash start.sh 2

# ...add node 2 as the consensus node through console...
# 1. check the current consensus nodes list
$ [group:2]> getSealerList
[
    9217e87c6b76184cf70a5a77930ad5886ea68aefbcce1909bdb799e45b520baa53d5bb9a5edddeab94751df179d54d41e6e5b83c338af0a19c0611200b830442,
    227c600c2e52d8ec37aa9f8de8db016ddc1c8a30bb77ec7608b99ee2233480d4c06337d2461e24c26617b6fd53acfa6124ca23a8aa98cb090a675f9b40a9b106,
    7a50b646fcd9ac7dd0b87299f79ccaa2a4b3af875bd0947221ba6dec1c1ba4add7f7f690c95cf3e796296cf4adc989f4c7ae7c8a37f4505229922fb6df13bb9e,
    8b2c4204982d2a2937261e648c20fe80d256dfb47bda27b420e76697897b0b0ebb42c140b4e8bf0f27dfee64c946039739467b073cf60d923a12c4f96d1c7da6
]
# 2. add node 2 to consensus node
# parameter behind addSealer is the node ID acquired in the last step
$ [group:2]> addSealer 6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4
{
    "code":0,
    "msg":"success"
}
# 3. check consensus nodes list
$ [group:2]> getSealerList
[
    9217e87c6b76184cf70a5a77930ad5886ea68aefbcce1909bdb799e45b520baa53d5bb9a5edddeab94751df179d54d41e6e5b83c338af0a19c0611200b830442,
    227c600c2e52d8ec37aa9f8de8db016ddc1c8a30bb77ec7608b99ee2233480d4c06337d2461e24c26617b6fd53acfa6124ca23a8aa98cb090a675f9b40a9b106,
    7a50b646fcd9ac7dd0b87299f79ccaa2a4b3af875bd0947221ba6dec1c1ba4add7f7f690c95cf3e796296cf4adc989f4c7ae7c8a37f4505229922fb6df13bb9e,
    8b2c4204982d2a2937261e648c20fe80d256dfb47bda27b420e76697897b0b0ebb42c140b4e8bf0f27dfee64c946039739467b073cf60d923a12c4f96d1c7da6,
    6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4 # new node
]
# acquire the current block number of group 2
$ [group:2]> getBlockNumber
2

#... send transaction to group 2
# deploy HelloWorld contract and output contract address. If failed, please check the consensus status of group 2
$ [group:2] deploy HelloWorld
contract address:0xdfdd3ada340d7346c40254600ae4bb7a6cd8e660

# acquire the current block number of group 2 if it increases to 3. If not, please check the consensus status of group 2
$ [group:2]> getBlockNumber
3

# exit console
$ [group:2]> exit
```

**Check the block generation status of the new node through log**

```bash
# enter the folder where the node is located
cd ~/fisco/nodes/127.0.0.1
# check the consensus status of node
$ tail -f node2/log/* | grep "g:2.*++"
info|2019-02-11 18:41:31.625599| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=4,tx=0,nodeIdx=1,hash=c8a1ed9c...
......Other outputs are omitted here......

# check the block generation status of node 2 and group 2: new block generated
$ cat node2/log/* | grep "g:2.*Report"
info|2019-02-11 18:53:20.708366| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=3,idx=3,hash=80c98d31...,next=10,tx=1,nodeIdx=1
# node2 also reports a block with block number 3. It indicates that node2 has joined group2.

```

#### Stop the node

```bash
# back to node folder && stop the node
$ cd ~/fisco/nodes/127.0.0.1 && bash stop_all.sh
```


## Parallel multi-group networking

Parallel multi-group networking is similar with star topology networking. Let's create a multi-group blockchain system with 4 nodes and 2 groups as an example:

- group 1: including 4 nodes with the same IP `127.0.0.1`;
- group 2: including 4 nodes with the same IP `127.0.0.1`.

```eval_rst
.. important::
   - In real life application, **it is not recommended to deploy multiple nodes on one machine** , please select the number of deployed nodes according to **the load of machine**
   - To demonstrate the process of parallel multi-group expansion, we will only create group 1 here.
   - In parallel multi-group cases, the add and remove of nodes is the same with star topology networking
```

### Build blockchain with a single group and 4 nodes

> **use build_chain script to generate node configuration folder of the 1 group 4 nodes blockchain**

```bash
$ mkdir -p ~/fisco && cd ~/fisco
# acquire build_chain.sh script
$ curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/`curl -s https://api.github.com/repos/FISCO-BCOS/FISCO-BCOS/releases | grep "\"v2\." | sort -u | tail -n 1 | cut -d \" -f 4`/build_chain.sh && chmod u+x build_chain.sh
# build blockchain of 1 group and 4 nodes(in production environment nodes are recommend to be deployed on different physical machines)
$ bash build_chain.sh -l "127.0.0.1:4" -o multi_nodes -p 20000,20100,7545
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 20000 20100 7545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /home/ubuntu16/fisco/multi_nodes
[INFO] CA Key Path       : /home/ubuntu16/fisco/multi_nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /home/ubuntu16/fisco/multi_nodes

```

> **Start all nodes**

```bash
# enter node folder
$ cd ~/fisco/multi_nodes/127.0.0.1
$ bash start_all.sh

# check progress
$ ps aux | grep fisco-bcos
ubuntu16       55028  0.9  0.0 986384  6624 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
ubuntu16       55034  0.8  0.0 986104  6872 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
ubuntu16       55041  0.8  0.0 986384  6584 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
ubuntu16       55047  0.8  0.0 986396  6656 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

> **Check consensus status of nodes**

```bash
# check consensus status of node 0
$ tail -f node0/log/* | grep "g:1.*++"
info|2019-02-11 20:59:52.065958| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=da72649e...

# check consensus status of node 1
$ tail -f node1/log/* | grep "g:1.*++"
info|2019-02-11 20:59:54.070297| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=0,hash=11c9354d...

# check consensus status of node 2
$ tail -f node2/log/* | grep "g:1.*++"
info|2019-02-11 20:59:55.073124| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=b65cbac8...

# check consensus status of node 3
$ tail -f node3/log/* | grep "g:1.*++"
info|2019-02-11 20:59:53.067702| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=0467e5c4...

```


### Add group2 to the blockchain

The `genesis` configuration files in each group of parallel multi-group networked blockchain are almost the same, except group ID [group].id.

```bash
# enter node folder
$ cd ~/fisco/multi_nodes/127.0.0.1

# copy the configuration of group 1
$ cp node0/conf/group.1.genesis group.2.genesis

# modify group ID
$ sed -i "s/id=1/id=2/g" group.2.genesis
$ cat group.2.genesis | grep "id"
# have modified to    id=2

# copy the configuration to each node
$ cp node0/conf/group.2.genesis node1/conf/group.2.genesis
$ cp node0/conf/group.2.genesis node2/conf/group.2.genesis
$ cp node0/conf/group.2.genesis node3/conf/group.2.genesis

# restart node
$ bash stop_all.sh
$ bash start_all.sh
```

### Check consensus status of groups

```bash
# check the consensus status of node 0 group 2
$ tail -f node0/log/* | grep "g:2.*++"
info|2019-02-11 21:13:28.541596| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=f3562664...

# check the consensus status of node 1 group 2
$ tail -f node1/log/* | grep "g:2.*++"
info|2019-02-11 21:13:30.546011| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=0,hash=4b17e74f...

# check the consensus status of node 2 group 2
$ tail -f node2/log/* | grep "g:2.*++"
info|2019-02-11 21:13:59.653615| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=90cbd225...

# check the consensus status of node 3 group 2
$ tail -f node3/log/* | grep "g:2.*++"
info|2019-02-11 21:14:01.657428| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=d7dcb462...

```

### Send transaction to groups

**Acquire console**

```bash
# If it's the first time to download console, please follow the below operations, otherwise copy the console to ~/fisco folder:
$ cd ~/fisco
# acquire console
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/download_console.sh)
```

**Configure the console**

```bash
# acquire channel_port
$ grep "channel_listen_port" multi_nodes/127.0.0.1/node0/config.ini
multi_nodes/127.0.0.1/node0/config.ini:    channel_listen_port=20100

# enter console folder
$ cd console
# configure certificate
$ cp ~/fisco/multi_nodes/127.0.0.1/sdk/* conf

```

**create configuration file `conf/applicationContext.xml` is created as follows. Two groups (group1 and group2) are configured on node0 (127.0.0.1:20100):**


```xml
cat > ./conf/applicationContext.xml << EOF
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
           xmlns:tx="http://www.springframework.org/schema/tx" xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
         http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
         http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">


        <bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
                <constructor-arg value="0"/> <!-- 0:standard 1:guomi -->
        </bean>

      <bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
        <property name="allChannelConnections">
        <list>
            <bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="1" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20100</value>
                    </list>
                    </property>
            </bean>
            <bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="2" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20100</value>
                    </list>
                    </property>
            </bean>
        </list>
        </property>
        </bean>

        <bean id="channelService" class="org.fisco.bcos.channel.client.Service" depends-on="groupChannelConnectionsConfig">
                <property name="groupId" value="1" />
                <property name="orgID" value="fisco" />
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>
</beans>
EOF
```


**Send transaction to groups through console**

```bash
# ... start console ...
$ bash start.sh
# The following output means the console has been started, otherwise please check if the certificate is configured or channel listen port is configured correctly.
=====================================================================================
Welcome to FISCO BCOS console(1.0.3)!
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

=====================================================================================
# ... send transaction to group 1...
# acquire current block number
$ [group:1]> getBlockNumber
0
# deploy HelloWorld contract to group 1. If failed, please check if group 1 is in normal consensus status
$ [group:1]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# acquire current block number. If it didn't increase, please check if group 1 is in normal consensus status
$ [group:1]> getBlockNumber
1

# ... send transaction to group 2...
# switch to group2
$ [group:1]> switch 2
Switched to group 2.
# acquire current block number
$ [group:2]> getBlockNumber
0
# deploy HelloWorld contract to group2
$ [group:2]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# acquire current block number. If it didn't increase, please check if group 2 is in normal consensus status
$ [group:2]> getBlockNumber
1
# exit console
$[group:2]> exit
```

**Check block generation status of nodes through log**

```bash
# switch to node folder
$ cd ~/fisco/multi_nodes/127.0.0.1/

# check block generation status of group 1, and to see that the block with block number of 1 belonging to group1 is reported.

$ cat node0/log/* | grep "g:1.*Report"
info|2019-02-11 21:14:57.216548| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=1,sealerIdx=3,hash=be961c98...,next=2,tx=1,nodeIdx=2

# check block generation status of group 2, and to see that the block with block number of 1 belonging to group2 is reported.
$ cat node0/log/* | grep "g:2.*Report"
info|2019-02-11 21:15:25.310565| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=1,sealerIdx=3,hash=5d006230...,next=2,tx=1,nodeIdx=2
```

#### Stop nodes

```bash
# back to nodes folder && stop the node
$ cd ~/fisco/multi_nodes/127.0.0.1 && bash stop_all.sh
```
