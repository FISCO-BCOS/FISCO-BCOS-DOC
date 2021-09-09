# Installation

This chapter will introduce the required installations and configurations of FISCO BCOS. For better understanding, we will illustrate an example of deploying a 4-node consortium chain in a local machine using FISCO BCOS. Please use the supported **hardware and platform** operations [according to here](manual/hardware_requirements.md).

## To build a single-group consortium chain

This section takes the construction of single group FISCO BCOS chain as an example to operate. We use the `build_chain.sh` script to build a 4-node FISCO BCOS chain locally in `Ubuntu 16.04 64bit` system.

```eval_rst
.. note::
    - To update an existing chain, please refer to `compatibility <change_log/index.html>`_ chapter.
    - To build OSCCA chain, please refer to ` <../manual/guomi_crypto.html>`_ 。
    - It is similar to build a multi-group chain, interested can be referred to `here <manual/group_use_cases.html>`_ .
    - This section uses pre-compiled static `fisco-bcos` binaries which tested on CentOS 7 and Ubuntu 16.04 64bit.
    - `build_chain use docker <../manual/build_chain.html#d-optional>`_
```

### Prepare environment

- Install dependence

`build_chain.sh` script depends on `openssl, curl` and is installed by using the following instructions. For CentOS system, to replaces `apt` with `yum` in the following command. For macOS system, execute `brew install openssl curl`.

```bash
# ubuntu
sudo apt install -y openssl curl

# centos
sudo yum install -y openssl openssl-devel
```

- Create operation directory

```bash
cd ~ && mkdir -p fisco && cd fisco
```

- Download `build_chain.sh` script

```bash
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.7.2/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - If the build_chain.sh script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/blob/master/manual/build_chain.sh && chmod u+x build_chain.sh`
```

### Build a single-group 4-node consortium chain

Execute the following command in the fisco directory to generate a single group 4-node FISCO chain. It is necessary to ensure that the `30300~30303, 20200~20203, 8545~8548` ports of the machine are not occupied.


```bash
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545
```

```eval_rst
.. note::
    - The -p option specifies the starting port, which are p2p_port, channel_port, and jsonrpc_port.

    - For security and ease of use consideration, the latest configuration of v2.3.0 version splits listen_ip into jsonrpc_listen_ip and channel_listen_ip, but still retains the parsing function of listen_ip. For details, please refer to `here <../manual/configuration.html#configure-rpc>`_

    - In order to facilitate development and experience, the reference configuration of channel_listen_ip is `0.0.0.0`. For security reasons, please modify it to a safe listening address according to the actual business network situation, such as: intranet IP or specific external IP
```

If the command is executed successfully, `All completed` will be output. If the execution fails, please check the error message in the `nodes/build.log` file.

```bash
Checking fisco-bcos binary...
Binary check passed.
==============================================================
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/ubuntu/fisco/nodes/127.0.0.1/download_console.sh
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /home/ubuntu/fisco/nodes
[INFO] CA Key Path       : /home/ubuntu/fisco/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /home/ubuntu/fisco/nodes
```


### Start FISCO BCOS chain

- Execute the following command to start all nodes

```bash
bash nodes/127.0.0.1/start_all.sh
```

Success will output a response similar to the following, otherwise, please use `netstat -an | grep tcp` to check whether the machine's `30300~30303, 20200~20203, 8545~8548` ports are occupied.

```bash
try to start node0
try to start node1
try to start node2
try to start node3
 node1 start successfully
 node2 start successfully
 node0 start successfully
 node3 start successfully
```

### Check process

- Execute the following command to check whether the process is started

```bash
ps -ef | grep -v grep | grep fisco-bcos
```

In normal situation, the output will be similar to the following. If the number of processes is not 4, then the reason why the process does not start is that the port is occupied.

```bash
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/ubuntu/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

### Check log output

- Execute the following command to view the number of nodes that node0 links to

```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```

In normal situation, the connecting messages will be output continuously. From the output messages, we can see that node0 has links with the other three nodes.

```bash
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat,connected count=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat,connected count=3
```

- Execute the following command to check whether it is in consensus

```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep +++
```

In normal situation, the message will be output `++++Generating seal` continuously to indicate that the consensus is normal.

```bash
info|2019-01-21 17:23:32.576197| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=13dcd2da...
info|2019-01-21 17:23:36.592280| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=31d21ab7...
info|2019-01-21 17:23:40.612241| [g:1][p:264][CONSENSUS][SEALER]++++++++++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=49d0e830...
```

## Using console

```eval_rst
.. important::
    - ``console 1.x`` series is based on `Web3SDK <../sdk/java_sdk.html>`_ implementation, ``Console 2.6 after`` is based on `Java SDK <../sdk/java_sdk/index.html >`_ implementation, the latest version of the console is based on the ``Java SDK`` implementation
    - For 2.6 and above version console documentation please refer to `here <../console/console_of_java_sdk.html>`_
    - For 1.x version console documentation, please refer to `here <../console/console.html>`_ 
    - You can view the current console version through the command ``./start.sh --version``
```

Console links nodes of FISCO BCOS so as to realize functions like blockchain status query, call and deploy contracts. 2.0 version console command detailed introduction [refer here](console/console_of_java_sdk.md), 1.x version console command detailed introduction [refer here](console/console.md).

### Prepare environment

- Install Java

In macOS, execute `brew cask install java` to install java

```bash
# ubuntu
sudo apt install -y default-jdk

# centos
sudo yum install -y java java-devel
```

- Get console

```bash
cd ~/fisco &&  curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.7.2/download_console.sh && bash download_console.sh
```

```eval_rst
.. note::
    - If the download_console.sh script cannot be downloaded for a long time due to network problems, try `cd ~/fisco &&  curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh`, and modify encryptType to 1 in applicationContext.xml.
```

- Copy the console configuration file. If the node does not use the default port, please replace 20200 in the file with another port.

```bash
# The latest version of the console uses the following command to copy the configuration file
cp -n console/conf/config-example.toml console/conf/config.toml
```

- Configure the console certificate

```bash
cp nodes/127.0.0.1/sdk/* console/conf/
```

### Start console

- Start console
```bash
cd ~/fisco/console && bash start.sh
```

If it outputs following information, then the console has been started successfully, otherwise please check if the node ports in conf/config.toml are configured correctly.

```bash
=============================================================================================
Welcome to FISCO BCOS console(2.6.0)！
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________  ______   ______    ______    ______         _______    ______    ______    ______
|        \|      \ /      \  /      \  /      \       |       \  /      \  /      \  /      \
| $$$$$$$$ \$$$$$$|  $$$$$$\|  $$$$$$\|  $$$$$$\      | $$$$$$$\|  $$$$$$\|  $$$$$$\|  $$$$$$\
| $$__      | $$  | $$___\$$| $$   \$$| $$  | $$      | $$__/ $$| $$   \$$| $$  | $$| $$___\$$
| $$  \     | $$   \$$    \ | $$      | $$  | $$      | $$    $$| $$      | $$  | $$ \$$    \
| $$$$$     | $$   _\$$$$$$\| $$   __ | $$  | $$      | $$$$$$$\| $$   __ | $$  | $$ _\$$$$$$\
| $$       _| $$_ |  \__| $$| $$__/  \| $$__/ $$      | $$__/ $$| $$__/  \| $$__/ $$|  \__| $$
| $$      |   $$ \ \$$    $$ \$$    $$ \$$    $$      | $$    $$ \$$    $$ \$$    $$ \$$    $$
 \$$       \$$$$$$  \$$$$$$   \$$$$$$   \$$$$$$        \$$$$$$$   \$$$$$$   \$$$$$$   \$$$$$$

=============================================================================================
```

### Query blockchain status

```bash
# acquire client ends version information
[group:1]> getNodeVersion
ClientVersion{
    "Build Time":"20200619 06:32:10",
    "Build Type":"Linux/clang/Release",
    "Chain Id":"1",
    "FISCO-BCOS Version":"2.5.0",
    "Git Branch":"HEAD",
    "Git Commit Hash":"72c6d770e5cf0f4197162d0e26005ec03d30fcfe",
    "Supported Version":"2.5.0"
}
# acquire node connection information
[group:1]> getPeers
[
    {
        "IPAndPort":"127.0.0.1:49948",
        "NodeID":"b5872eff0569903d71330ab7bc85c5a8be03e80b70746ec33cafe27cc4f6f8a71f8c84fd8af9d7912cb5ba068901fe4131ef69b74cc773cdfb318ab11968e41f",
        "Topic":[]
    },
    {
        "IPAndPort":"127.0.0.1:49940",
        "NodeID":"912126291183b673c537153cf19bf5512d5355d8edea7864496c257630d01103d89ae26d17740daebdd20cbc645c9a96d23c9fd4c31d16bccf1037313f74bb1d",
        "Topic":[]
    },
    {
        "IPAndPort":"127.0.0.1:49932",
        "NodeID":"db75ab16ed7afa966447c403ca2587853237b0d9f942ba6fa551dc67ed6822d88da01a1e4da9b51aedafb8c64e9d208d9d3e271f8421f4813dcbc96a07d6a603",
        "Topic":[]
    }
]
```

## To deploy or call HelloWorld contract

### HelloWorld contract

HelloWorld contract offers 2 interfaces which are `get()` and `set()` and are used to acquire/set contract variety `name`. The contract content is as below:

```solidity
pragma solidity ^0.4.24;

contract HelloWorld {
    string name;

    function HelloWorld() {
        name = "Hello, World!";
    }

    function get()constant returns(string) {
        return name;
    }

    function set(string n) {
        name = n;
    }
}
```

### Deploy HelloWorld contract

For quick experience, the console comes with HelloWorld contract and is placed under console folder `contracts/solidity/HelloWorld.sol`. So, users only have to deploy it using the following command.

```bash
# input the following instruction in console, if it is deployed successfully, the contract address will be returned
[group:1]> deploy HelloWorld
transaction hash: 0xd0305411e36d2ca9c1a4df93e761c820f0a464367b8feb9e3fa40b0f68eb23fa
contract address:0xb3c223fc0bf6646959f254ac4e4a7e355b50a344
```

### Call HelloWorld contract

```bash
# check the current block number
[group:1]> getBlockNumber
1

# call get interface to acquire name variety, the contract address here is the returned address of deploy instruction
[group:1]> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return values:
[
    "Hello,World!"
]
---------------------------------------------------------------------------------------------

# check the current block number, it remains the same, because get interface will not change the ledger status
[group:1]> getBlockNumber
1

# call set to set name
[group:1]> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 set "Hello, FISCO BCOS"
transaction hash: 0x7e742c44091e0d6e4e1df666d957d123116622ab90b718699ce50f54ed791f6e
---------------------------------------------------------------------------------------------
transaction status: 0x0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Output
Receipt message: Success
Return message: Success
---------------------------------------------------------------------------------------------
Event logs
Event: {}

# check the current block number again, if it increased, then it has generated block and the ledger status is changed
[group:1]> getBlockNumber
2

# call get interface to acquire name variety, check if the setting is valid
[group:1]> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
---------------------------------------------------------------------------------------------
Return code: 0
description: transaction executed successfully
Return message: Success
---------------------------------------------------------------------------------------------
Return values:
[
    "Hello,FISCO BCOS"
]
---------------------------------------------------------------------------------------------

# log out console
[group:1]> quit
```

[build_chain_code]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/manual/build_chain.sh
