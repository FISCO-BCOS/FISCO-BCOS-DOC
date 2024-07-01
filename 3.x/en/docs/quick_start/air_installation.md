# Build the first blockchain network

Tags: "Building a Blockchain Network" "Blockchain Tutorial" "HelloWorld" "Console Call Contract" "

----

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

This chapter describes the necessary installation and configuration required to use the FISCO BCOS underlying blockchain system。This chapter helps users master the FISCO BCOS deployment process by deploying a 4-node FISCO BCOS alliance chain on a single machine. Please refer to [Hardware and System Requirements](./hardware_requirements.md)Operating with supported hardware and platforms。

```eval_rst
.. note::
   - For the system architecture of FISCO BCOS 3.x, please refer to 'here <.. / design / architecture.html >' _
   - FISCO BCOS 3.x Air version to build and use the tutorial, please refer to 'here <.. / tutorial / air / index.html >' _
   - FISCO BCOS 3.x Pro version to build and use the tutorial, please refer to 'here <.. / tutorial / pro / index.html >' _
   - FISCO BCOS 3.x Max version to build and use the tutorial, please refer to 'here <.. / tutorial / max / index.html >' _
```

## 1. Build Air version FISCO BCOS alliance chain

This section takes building a FISCO BCOS chain of a single group as an example, and uses the 'development and deployment tool build _ chain.sh' script to build a 4-node FISCO BCOS chain of the Air version locally, taking the Ubuntu 18.04 64-bit system as an example.。

### Step 1. Install dependencies

**Install macOS dependencies**

```shell
# The latest homebrew is downloaded as openssl @ 3 by default, which requires a specified version of openssl @ 1.1
brew install openssl@1.1 curl wget
```

**Install ubuntu dependencies**

```shell
sudo apt install -y curl openssl wget

```

**Install centos dependencies**

```shell
sudo yum install -y curl openssl openssl-devel wget
```

### Step 2. Create an action directory and download the installation script

```eval_rst
.. note::
   If the build _ chain.sh script cannot be downloaded for a long time due to network problems, try curl-#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.6.0/build_chain.sh && chmod u+x build_chain.sh
```

```shell
# Create action directory
cd ~ && mkdir -p fisco && cd fisco

# Download the chain building script
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.6.0/build_chain.sh && chmod u+x build_chain.sh

# Note: If the speed of accessing git is too slow, try the following command to download the link creation script:
curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.6.0/build_chain.sh && chmod u+x build_chain.sh
```

### Step 3. Build a 4-node non-state secret alliance chain

```eval_rst
.. note::
   Please make sure that the 30300 ~ 30303, 20200 ~ 20203 ports of the machine are not occupied。
```

Run the following command in the FISCO directory to generate a FISCO chain of 4 nodes in a single group:

```shell
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200
```

```eval_rst
.. note::
   - 其中-The p option specifies the starting port, which is the p2p listening port and the rpc listening port respectively
   - Air build script build _ chain.sh introduction document 'refer here <.. / tutorial / air / build _ chain.html >' _
```

After the command succeeds, it will output 'All completed':

```shell
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/sdk cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node0/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node1/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node2/conf cert successful!
writing RSA key
[INFO] Generate ./nodes/127.0.0.1/node3/conf cert successful!
[INFO] Downloading get_account.sh from https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh...
############################################################################################################################################################### 100.0%
[INFO] Admin account: 0x4c7239cfef6d41b7322c1567f082bfc65c69acc5
[INFO] Generate uuid success: 167A2233-5444-4CA4-8792-C8E68130D5FC
[INFO] Generate uuid success: CC117CAF-224C-4940-B548-6DED31D24B18
[INFO] Generate uuid success: 16B5E4BD-51C1-416E-BF44-6D1BB05F7666
[INFO] Generate uuid success: 60DD77F2-F3A5-49F2-8C7F-8151E8823C6D
==============================================================
[INFO] GroupID              : group0
[INFO] ChainID              : chain0
[INFO] fisco-bcos path      : bin/fisco-bcos
[INFO] Auth mode            : false
[INFO] Start port           : 30300 20200
[INFO] Server IP            : 127.0.0.1:4
[INFO] SM model             : false
[INFO] enable HSM           : false
[INFO] Output dir           : ./nodes
[INFO] All completed. Files in ./nodes
```

### Step 4. Start the FISCO BCOS chain

- Start all nodes

```shell
bash nodes/127.0.0.1/start_all.sh
```
Successful startup will output the following information。Otherwise use 'netstat-an |grep tcp 'check machine' 30300 ~ 30303,20200 ~ 20203 'ports are occupied。

```shell
try to start node0
try to start node1
try to start node2
try to start node3
 node3 start successfully pid=36430
 node2 start successfully pid=36427
 node1 start successfully pid=36433
 node0 start successfully pid=36428
```

### Step 5. Check the node process

- Check if the process is started

```shell
ps aux |grep -v grep |grep fisco-bcos
```

Normally there would be output similar to the following； If the number of processes is not 4, the process is not started (usually caused by the port being occupied)

```
fisco        35249   7.1  0.2  5170924  57584 s003  S     2:25 p.m. 0:31.63 /home/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini -g config.genesis
fisco        35218   6.8  0.2  5301996  57708 s003  S     2:25 p.m. 0:31.78 /home/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini -g config.genesis
fisco        35277   6.7  0.2  5301996  57660 s003  S     2:25 p.m. 0:31.85 /home/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini -g config.genesis
fisco        35307   6.6  0.2  5301996  57568 s003  S     2:25 p.m. 0:31.93 /home/fisco/nodes//127.0.0.1/node3/../fisco-bcos -c config.ini -g config.genesis
```

### Step 6. Check the log output

- View the number of network connections per node

Take node0 for example:

```shell
tail -f nodes/127.0.0.1/node0/log/* |grep -i "heartBeat,connected count"
```

Under normal circumstances, the connection information is output every 10 seconds. From the output log, it can be seen that node0 is connected to the other three nodes, and the network connection is normal:

```shell
info|2022-08-15 19:38:59.270112|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:09.270210|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:19.270335|[P2PService][Service][METRIC]heartBeat,connected count=3
info|2022-08-15 19:39:29.270427|[P2PService][Service][METRIC]heartBeat,connected count=3
```

## 2. Configure and use the console

The console provides functions such as deploying contracts to FISCO BCOS nodes, initiating contract calls, and querying chain status.。

### Step 1. Install the console dependencies

Console running depends on Java environment(We recommend Java 14.)and the installation command is as follows:

```shell
# Ubuntu system installation java
sudo apt install -y default-jdk

# Centos system installed java
sudo yum install -y java java-devel
```

### Step 2. Download the console

```shell
cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh && bash download_console.sh
```

```eval_rst
.. note::
   - If you cannot download for a long time due to network problems, please try cd ~ / fisco & & curl-#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh  && bash download_console.sh
```

### Step 3. Configure the console

- Copy console configuration file

```shell
cp -n console/conf/config-example.toml console/conf/config.toml
```

```eval_rst
.. note::
   If the node does not use the default port, replace 20200 in the file with the corresponding rpc port of the node. You can use the "[rpc] .listen _ port" configuration item of the node config.ini to obtain the rpc port of the node.。
```

- Configure Console Certificates

SSL connection is enabled by default between the console and the node. The console needs to configure a certificate to connect to the node.。The SDK certificate is generated at the same time as the node is generated. You can directly copy the generated certificate for the console to use:

```shell
cp -r nodes/127.0.0.1/sdk/* console/conf
```

### Step 4. Start and use the console

```eval_rst
.. note::
   - Please make sure that the 30300 ~ 30303, 20200 ~ 20203 ports of the machine are not occupied。
   - For console configuration methods and commands, please refer to 'here <.. / operation _ and _ maintenance / console / console _ config.html >' _
```

- Start

```shell
cd ~/fisco/console && bash start.sh
```

Output the following information to indicate successful startup. Otherwise, please check whether the node port configuration in conf / config.toml is correct

```shell
=============================================================================================
Welcome to FISCO BCOS console(3.4.0)!
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

- Get information with the console

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
```

## 3. Deploy and invoke contracts

### Step 1. Write the HelloWorld contract

HelloWorld contract provides two interfaces' get()'and' set()', used to get / set the contract variable' name ', the contract content is as follows:

```shell
pragma solidity >=0.6.10 <0.8.20;
contract HelloWorld {
    string name;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public {
        name = n;
    }
}
```

### Step 2. Deploy the HelloWorld contract

To facilitate the user's quick experience, the HelloWorld contract is built into the console and located in the console directory 'contracts / consolidation / HelloWorld.sol'.

```shell
# Enter the following command in the console to return the contract address if the deployment is successful
[group0]: /> deploy HelloWorld
transaction hash: 0x796b573aece250bba891b9251b8fb464d22f41cb36e7cae407b2bd0a870f5b72
contract address: 0x6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1
currentAccount: 0x7b047472a4516e9697446576f8c7fcc064f967fa

# View current block height
[group0]: /> getBlockNumber
1
```

### Step 3. Call the HelloWorld contract

```shell
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

At this point, we have completed the first FISCO-Building the BCOS chain, configuring and using the console, and deploying and invoking the first contract。关于**Pro version FISCO BCOS build, configuration and use please refer to [here](../tutorial/pro/index.md)。**
