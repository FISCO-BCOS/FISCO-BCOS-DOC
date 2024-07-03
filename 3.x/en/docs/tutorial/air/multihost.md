# Building a multi-machine blockchain network

Tags: "Build a multi-machine blockchain network" "Blockchain tutorial" "HelloWorld" "Console call contract" "

----

Building the first blockchain network(../../quick_start/air_installation.md)This chapter describes in detail how to build a stand-alone 4-node blockchain network in the Air version. This chapter takes building a multi-machine 4-node blockchain network as an example to describe in detail how to deploy FISCO BCOS on multiple machines。

## 1. Build a multi-machine 4-node blockchain network

This section details how to deploy tools based on [(build_chain.sh)](./build_chain.md)Building a multi-machine 4-node blockchain system。

In this tutorial, assume that the IP addresses of the four physical machines are '196.168.0.1', '196.168.0.3', '196.168.0.4', and '196.168.0.2'. Each machine is deployed with a blockchain node。

```eval_rst
.. note::
    - Please make sure that the "30300," "20200" ports of each machine are not occupied。
    - Make sure that each machine has network access to ports "30300" and "20200"
    - ensure that the machine that generates the blockchain node configuration can access the external network(Used to download the chain building script)
```

### Step 1. Download the deployment tool and generate the multi-machine node configuration

**Create an operation path, download the fisco-bcos, development and deployment tool build _ chain**

```bash
# Create operation path ~ / fisco
mkdir -p ~/fisco && cd ~/fisco

# download _ bin.sh, download the fisco-bcos binary, v Specifies the FISCO-BCOS version
./download_bin.sh -v 3.4.0

# Download the development and deployment tool build _ chain
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.6.0/build_chain.sh && chmod u+x build_chain.sh
```

**Generate multi-machine node configuration**

```bash
bash build_chain.sh -l "196.168.0.1:1,196.168.0.2:1,196.168.0.3:1,196.168.0.4:1" -o 4nodes
```

`-l`: parameter is the ip address of the node to be generated and the number of nodes generated per machine；
`-o`: Output node file path；
For more information about the parameters, see [Deployment Tools(build_chain.sh)](./build_chain.md)。

Successful command execution will output 'All completed'。If an error occurs, check the error message in the '4nodes / build.log' file。

```bash
[INFO] Use binary ./fisco-bcos
[INFO] Generate ca cert successfully!
Processing IP:196.168.0.1 Total:1
[INFO] Generate 4nodes/196.168.0.1/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.1/node0/conf cert successful!
Processing IP:196.168.0.2 Total:1
[INFO] Generate 4nodes/196.168.0.2/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.2/node0/conf cert successful!
Processing IP:196.168.0.3 Total:1
[INFO] Generate 4nodes/196.168.0.3/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.3/node0/conf cert successful!
Processing IP:196.168.0.4 Total:1
[INFO] Generate 4nodes/196.168.0.4/sdk cert successful!
[INFO] Generate 4nodes/196.168.0.4/node0/conf cert successful!
==============================================================
[INFO] fisco-bcos Path     : ./fisco-bcos
[INFO] Auth Mode           : false
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 196.168.0.1:1 196.168.0.2:1 196.168.0.3:1 196.168.0.4:1
[INFO] SM Model            : false
[INFO] output dir          : 4nodes
[INFO] All completed. Files in 4node
```

At this point, the multi-machine 4-node configuration is successfully generated, and the blockchain node configuration of each machine is located in the '4nodes' folder, as follows:

```bash
$ ls 4nodes/
196.168.0.1  196.168.0.2  196.168.0.3  196.168.0.4  ca
```

### Step 2. Copy the blockchain node configuration

After generating the blockchain node configuration, you need to copy each node configuration to the corresponding machine. You can use the 'scp' command to execute the copy, as follows:

```bash
# Create an operating directory for each machine ~ / fisco
# (Note: Use the FICO user here; In practice, you can use your own account for similar operations, and the IP needs to be replaced with your own machine IP)
ssh fisco@196.168.0.1 "mkdir -p ~/fisco"
ssh fisco@196.168.0.2 "mkdir -p ~/fisco"
ssh fisco@196.168.0.3 "mkdir -p ~/fisco"
ssh fisco@196.168.0.4 "mkdir -p ~/fisco"

# Copy node configuration
# Copy node configuration to 196.168.0.1 ~ / fisco path
scp -r 4nodes/196.168.0.1/ fisco@196.168.0.1:~/fisco/196.168.0.1
# Copy node configuration to 196.168.0.2 ~ / fisco path
scp -r 4nodes/196.168.0.2/ fisco@196.168.0.2:~/fisco/196.168.0.2
# Copy node configuration to 196.168.0.3 ~ / fisco path
scp -r 4nodes/196.168.0.3/ fisco@196.168.0.3:~/fisco/196.168.0.3
# Copy node configuration to 196.168.0.4 ~ / fisco path
scp -r 4nodes/196.168.0.4/ fisco@196.168.0.4:~/fisco/196.168.0.4
```

### Step 3. Start the multi-machine 4-node blockchain system

After the configuration of the blockchain node is copied successfully, you need to start all the nodes. You can start the blockchain node remotely by initiating an 'ssh' operation on a machine, or you can log on to all the physical machines and start the blockchain node on the corresponding physical machine。

**Method one: Start a blockchain node remotely**

The node start command is also initiated from '196.168.0.1', as follows:

```bash
# (Note: Use the FICO user here; In practice, you can use your own account for similar operations, and the IP needs to be replaced with your own machine IP)
# Launch the blockchain node deployed on the 196.168.0.1 machine
$ ssh fisco@196.168.0.1 "bash ~/fisco/196.168.0.1/start_all.sh"
try to start node0
 node0 start successfully

# Launch the blockchain node deployed on the 196.168.0.2 machine
$ ssh fisco@196.168.0.2 "bash ~/fisco/196.168.0.2/start_all.sh"
try to start node0
 node0 start successfully

# Launch the blockchain node deployed on the 196.168.0.3 machine
$ ssh fisco@196.168.0.3 "bash ~/fisco/196.168.0.3/start_all.sh"
try to start node0
 node0 start successfully

# Launch the blockchain node deployed on the 196.168.0.4 machine
$ ssh fisco@196.168.0.4 "bash ~/fisco/196.168.0.4/start_all.sh"
try to start node0
 node0 start successfully
```

**Method two: Log in to the machine directly to start the blockchain node**

```bash
# (Note: Use the FICO user here; In practice, you can use your own account for similar operations, and the IP needs to be replaced with your own machine IP)
# Log in to 196.168.0.1 and launch the blockchain node
$ ssh fisco@196.168.0.1
$ bash ~/fisco/196.168.0.1/start_all.sh

# Log in to 196.168.0.2 and launch the blockchain node
$ ssh fisco@196.168.0.2
$ bash ~/fisco/196.168.0.2/start_all.sh

# Log in to 196.168.0.3 and launch the blockchain node
$ ssh fisco@196.168.0.3
$ bash ~/fisco/196.168.0.3/start_all.sh

# Log in to 196.168.0.4 and launch the blockchain node
$ ssh fisco@196.168.0.4
$ bash ~/fisco/196.168.0.4/start_all.sh

```

At this point, a multi-machine 4-node blockchain system has been built. Next, you need to check whether the blockchain nodes are working properly。

### Step 4. Check the blockchain node

**Check if the process started successfully**

Log on to each machine and execute the following command to determine whether the process is started successfully:

```bash
ps aux | grep fisco | grep -v grep
```

Normally, each machine will have an output similar to the following；

```bash
fisco     29306  0.8  0.1 747008 31488 ?        Sl   17:08   0:05 /home/ubuntu/fisco/196.168.0.1/node0/../fisco-bcos -c config.ini
```

If some machines do not have output similar to the above, please check whether the "30300" and "20200" ports of the machine are occupied。

**Check whether the network connection is normal**

Log on to each machine and run the following command to determine whether the node network connection is normal:

```bash
tail -f ~/fisco/*/node0/log/* |grep -i connected
```

Normally, the connection information will be output continuously. From the output, it can be seen that the node is connected to other machine nodes normally, and transactions can be initiated on the console。

```bash
info|2019-01-21 17:30:58.316769| [P2PService][Service] heartBeat,connected count=3
info|2019-01-21 17:31:08.316922| [P2PService][Service] heartBeat,connected count=3
info|2019-01-21 17:31:18.317105| [P2PService][Service] heartBeat,connected count=3
```

## 2. Configure and use the console

This chapter describes how to configure a console for a multi-machine 4-node blockchain system and use the console to initiate transactions for the multi-machine blockchain system。

### Step 1. Prepare to rely on

- Install Java (Java 14 is recommended)

```bash
# Ubuntu system installation java
sudo apt install -y default-jdk

#Centos system installed java
sudo yum install -y java java-devel
```

### Step 2. Download and configure the console

**Download Console**

```bash
# Create action directory
mkdir -p ~/fisco && cd ~/fisco

curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh && bash download_console.sh

# If you cannot download for a long time due to network problems, try the following command:
curl -#LO  https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh
```

**Configuration Console: Copy SDK certificate**

Copy the SDK certificate from '196.168.0.1', as follows:

```bash
# Copy the certificate from 196.168.0.1 to the conf directory
$ scp 196.168.0.1:~/fisco/196.168.0.1/sdk/* ~/fisco/console/conf
```

**Configuration Console: Modify Console Configuration**

```bash
# Copy Console Configuration
$ cp -n ~/fisco/console/conf/config-example.toml ~/fisco/console/conf/config.toml

# Modify console connection information(During operation, please fill in the IP information of the console connection according to the actual situation)
sed -i 's/peers=\["127.0.0.1:20200", "127.0.0.1:20201"\]/peers=["196.168.0.1:20200", "196.168.0.2:20200", "196.168.0.3:20200", "196.168.0.4:20200"]/g' ~/fisco/console/conf/config.toml
```

### Step 3. Start and use the console

**Start Console**

```bash
bash ~/fisco/console/start.sh
```

After the console is started successfully, the following message is output:

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.1.0)!
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
```

**Send a transaction using the console**

```shell
# Obtaining Node List Information
[group0]: /> getGroupPeers
peer0: 48fde62f1d2dc59a65bed2e3bb9bd199de26de6b9894e2201199726d6e9e98d090bb1d7e22c931b412728a832ffacdd2727b34fc808f5a755af6bc623c44aba6
peer1: 8e510d66644a8a6caa7e031f097f604501bc42a3851b817f65a18eede0c539f2d011349d470da74cb84a3cf88dbd64a4cc18369fa09b17dac4eec9f65975ecc2
peer2: 9d7ff3f0d3abd54054a99d17a6af27c839f8f7702a4335bdb502323c87a0d7978534a2479cfedb614e1548a869efe038fc49da442b5770aa52c0cc793ca13602
peer3: f0ffa45cee35dcc1bcf1e1ef7b7c3d96590c25ba75198a28ef5ceb89dc6bec310619cb6850231018c8d5a5d698eaf1e5669118e17ea79379211bd332896aa56a

# Obtaining Consensus Node List Information
[group0]: /> getSealerList
[
    Sealer{
        nodeID='9d7ff3f0d3abd54054a99d17a6af27c839f8f7702a4335bdb502323c87a0d7978534a2479cfedb614e1548a869efe038fc49da442b5770aa52c0cc793ca13602',
        weight=1
    },
    Sealer{
        nodeID='f0ffa45cee35dcc1bcf1e1ef7b7c3d96590c25ba75198a28ef5ceb89dc6bec310619cb6850231018c8d5a5d698eaf1e5669118e17ea79379211bd332896aa56a',
        weight=1
    },
    Sealer{
        nodeID='8e510d66644a8a6caa7e031f097f604501bc42a3851b817f65a18eede0c539f2d011349d470da74cb84a3cf88dbd64a4cc18369fa09b17dac4eec9f65975ecc2',
        weight=1
    },
    Sealer{
        nodeID='48fde62f1d2dc59a65bed2e3bb9bd199de26de6b9894e2201199726d6e9e98d090bb1d7e22c931b412728a832ffacdd2727b34fc808f5a755af6bc623c44aba6',
        weight=1
    }
]

# Enter the following command in the console to return the contract address if the deployment is successful
[group0]: /> deploy HelloWorld
transaction hash: 0x796b573aece250bba891b9251b8fb464d22f41cb36e7cae407b2bd0a870f5b72
contract address: 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1
currentAccount: 0x7b047472a4516e9697446576f8c7fcc064f967fa

# View current block height
[group0]: /> getBlockNumber
1

# Call the get interface to get the name variable, where the contract address is the address returned by the deploy command
[group0]: /> call HelloWorld 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return value size:1
Return types: (string)
Return values:(Hello, World!)
---------------------------------------------------------------------------------------------

# Check the current block height, the block height remains unchanged, because the get interface does not change the ledger status
[group0]: /> getBlockNumber
1

# Call the set method to set the name
[group0]: /> call HelloWorld 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1 set "Hello, FISCO BCOS"
transaction hash: 0x2f7c85c2c59a76ccaad85d95b09497ad05ca7983c5ec79c8f9d102d1c8dddc30
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
---------------------------------------------------------------------------------------------
Event logs
Event: {}

# View the current block height, because the set interface has modified the ledger status and the block height has increased to 2
[group0]: /> getBlockNumber
2

# Exit Console
[group0]: /> exit
```

At this point, we have completed the construction of the multi-machine blockchain network, the configuration and use of the console。
