# Installation

This chapter will introduce the required installations and configurations of FISCO BCOS. For better understanding, we will illustrate an example of deploying a 4-node consortium chain in a local machine using FISCO BCOS. Please use the supported **hardware and platform** operations [according to here](./manual/hardware_requirements.md).

## To build a single-group consortium chain

This section takes the construction of single group FISCO BCOS chain as an example to operate. We use the `build_chain.sh` script to build a 4-node FISCO BCOS chain locally in `Ubuntu 16.04 64bit` system.

```eval_rst
.. note::
    - To update an existing chain, please refer to `compatibility <change_log/index.html>`_ chapter.
    - It is similar to build a multi-group chain, interested can be referred to `here <manual/group_use_cases.html>`_ .
    - This section uses pre-compiled static `fisco-bcos` binaries which tested on CentOS 7 and Ubuntu 16.04 64bit.
```

### Prepare environment

- Install dependence

`build_chain.sh` script depends on `openssl, curl` and is installed by using the following instructions. For CentOS system, to replaces `apt` with `yum` in the following command. For macOS system, to executes `brew install openssl curl`.

```bash
sudo apt install -y openssl curl
```

- Create operation directory

```bash
cd ~ && mkdir -p fisco && cd fisco
```

- Download `build_chain.sh` script

```bash
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.2.0/build_chain.sh && chmod u+x build_chain.sh
```

### Build a single-group 4-node consortium chain

Execute the following command in the fisco directory to generate a single group 4-node FISCO chain. It is necessary to ensure that the `30300~30303, 20200~20203, 8545~8548` ports of the machine are not occupied.


```bash
bash build_chain.sh -l "127.0.0.1:4" -p 30300,20200,8545
```

```eval_rst
.. note::
    - The -p option specifies the starting ports, which are p2p_port, channel_port, jsonrpc_port. For security reasons, jsonrpc/channel listens to 127.0.0.1 by defaults. **If you require to access external network, please add -i parameter**.

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
[INFO] Execute the download_console.sh script to get FISCO-BCOS console, download_console.sh is in directory named by IP.
 bash download_console.sh
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /home/ubuntu16/fisco/nodes
[INFO] CA Key Path       : /home/ubuntu16/fisco/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /home/ubuntu16/fisco/nodes
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
fisco       5453     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
fisco       5459     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
fisco       5464     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
fisco       5476     1  1 17:11 pts/0    00:00:02 /home/fisco/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

### Check log output

- Execute the following command to view the number of nodes that node0 links to

```bash
tail -f nodes/127.0.0.1/node0/log/log*  | grep connected
```

In normal situation, the connecting messages will be output continuously. From the output messages, we can see that node0 has links with the other three nodes.

```bash
info|2019-01-21 17:30:58.316769| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:08.316922| [P2P][Service] heartBeat connected count,size=3
info|2019-01-21 17:31:18.317105| [P2P][Service] heartBeat connected count,size=3
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

Console links nodes of FISCO BCOS through Web3SDK so as to realize functions like blockchain status query, call and deploy contracts. The instructions of console are introduced [here](manual/console.md).

### Prepare environment

- Install Java

In macOS, execute `brew cask install java` to install java

```bash
sudo apt install -y default-jdk
```

- Get console

```bash
cd ~/fisco && bash nodes/127.0.0.1/download_console.sh
```

- Copy the console configuration file. If the node does not use the default port, please replace 20200 in the file with another port.

```bash
cp -n console/conf/applicationContext-sample.xml console/conf/applicationContext.xml
```

- Configure the console certificate

```bash
cp nodes/127.0.0.1/sdk/* console/conf/
```

```eval_rst
.. important::

  - if the console has been configured correctly, but it reports the following exception when starting console in CentOS system:

    Failed to connect to the node. Please check the node status and the console configuration.

   this is caused by the in-built JDK version of CentOS system(who will lead to verification failure of console and nodes). Please download and install Java 8 or above version from `OpenJDK official website <https://jdk.java.net/java-se-ri/8>`_ or `Oracle official website <https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html>`_ (for detailed installation steps please check `Additional <manual/console.html#java>`_ ), and start console after finishing installation.

```

### Start console

- Start console
```bash
cd ~/fisco/console && bash start.sh
```

If it outputs following information, then the console has been started successfully, otherwise please check if the node ports in conf/applicationContext.xml are configured correctly.

```bash
=============================================================================================
Welcome to FISCO BCOS console(1.0.3)！
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
{
    "Build Time":"20190121 06:21:05",
    "Build Type":"Linux/clang/Debug",
    "FISCO-BCOS Version":"2.0.0",
    "Git Branch":"master",
    "Git Commit Hash":"c213e033328631b1b8c2ee936059d7126fd98d1a"
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
contract address:0xb3c223fc0bf6646959f254ac4e4a7e355b50a344
```

### Call HelloWorld contract

```bash
# check the current block number
[group:1]> getBlockNumber
1

# call get interface to acquire name variety, the contract address here is the returned address of deploy instruction
[group:1]> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, World!

# check the current block number, it remains the same, because get interface will not change the ledger status
[group:1]> getBlockNumber
1

# call set to set name
[group:1]> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 set "Hello, FISCO BCOS"
0x21dca087cb3e44f44f9b882071ec6ecfcb500361cad36a52d39900ea359d0895

# check the current block number again, if it increased, then it has generated block and the ledger status is changed
[group:1]> getBlockNumber
2

# call get interface to acquire name variety, check if the setting is valid
[group:1]> call HelloWorld 0xb3c223fc0bf6646959f254ac4e4a7e355b50a344 get
Hello, FISCO BCOS

# log out console
[group:1]> quit
```

**Note:** To deploy contract can also specify the contract version number of the deployment through the `deployByCNS` command, using method [reference here](manual/console.html#deploybycns). To call contract via the `callByCNS` command, using the method [reference here](manual/console.html#callbycns).

[build_chain_code]:https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/build_chain.sh
