# 5. Console deployment calls the contract.

-----

This document describes how to configure the console and describes how the console deploys contracts and invokes contracts.

## 1. Download the configuration console
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
cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v3.5.0/download_console.sh && bash download_console.sh
```

```eval_rst
.. note::
   - If you cannot download for a long time due to network problems, please try cd ~ / fisco & & curl-#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh
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
   - For console configuration methods and commands, please refer to 'here <.. / operation _ and _ maintenance / console / index.html >' _ implementation。
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


## 2. Deploy and invoke contracts

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

At this point, we have completed the configuration and use of the console, and introduced how to deploy and invoke the contract through the console, for more console tutorials, please refer to [here](../operation_and_maintenance/console/index.md)。

