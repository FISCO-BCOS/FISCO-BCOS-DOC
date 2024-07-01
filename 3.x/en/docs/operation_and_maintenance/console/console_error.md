# Error prompt

Tags: "console" "Console Configuration" "Command Line Interactive Tools"

---------

```eval_rst
.. important::
    - "Console" only supports FISCO BCOS 3.x version, based on 'Java SDK <.. /.. / sdk / java _ sdk / index.html >' _ implementation。
    - You can use the command. "/ start.sh--version "View the current console version
```

Possible errors in console startup:

## `The connection to the server timed out`

Connection node timeout, possible cause:

- **Node version error:** 'console 3.0 'only supports FISCO BCOS 3.x, please ensure that the deployed blockchain environment is 3.0+Version
- **Network connection failure:** The node is not started or the configuration node connection information is incorrect
- **SSL handshake failed:** Certificate error or 'SSL' configuration error

对于**Network connection failure**The problem, troubleshooting methods are as follows:

1. Check whether the node starts normally

    `ps -ef | egrep fisco`

2. Check whether the node connection information configured in the console is wrong

   The list of node connections configured in the console, in the console configuration file 'config.toml':

    ```shell
    [network]
        peers=["127.0.0.1:20200", "127.0.0.1:20201"]    # The peer list to connect
    ```

    The connection port should be the**RPC Port**, node 'RPC' port in node configuration file 'config.ini:"listen _ port" field:

    ```shell
    [rpc]
        listen_ip=0.0.0.0
        listen_port=20200
    ```

3. Check whether the network is connected

   You can use tools such as' ping 'and' telnet 'to determine if the console is not connected to the server network where the node is located.

对于**SSL handshake failed**问题:

1. Check whether the 'sdk' certificate is correct:

    - The 'sdk' certificate location of the 'Air' installation package: `nodes/IP/sdk`
    - The 'sdk' certificate location of the 'Pro' version installation package: `generated/rpc/chainID/IP/serviceName/sdk`(Remarks: chainID:Chain ID, IP:Node IP, serviceName:Service name, specified when setting up the environment)

    will 'sdk /*'Copy the certificate in the directory to the console configuration directory 'console / conf'

2. Check the 'SSL' configuration

   Console 'SSL' Configuration:

   ```shell
   [cryptoMaterial]
        useSMCrypto = "false"                       # RPC SM crypto type
   ```

   'SSL 'configuration of the node:

   ```shell
   [rpc]
        ; ssl or sm ssl
        sm_ssl=false
   ```

   The two configurations should be consistent, set to 'true' in the national secret environment and 'false' in the non-national secret environment.

## `there has no connection available for the group, maybe all connections disconnected or the group does not exist`

The group id used by the console does not exist. There are two ways to start the console.:

Specify Group: `bash start.sh groupId`
Default startup: 'bash start.sh ', the group id used at this time is the group configured in the' config.toml 'file:

```shell
[network]   
    defaultGroup="group0"                            # Console default group to connect
```

The group ID of the node. Check the node configuration file 'config.genesis'.:

```shell
// config.genesis
[chain]
    ; the group id, should never be changed
    group_id=group0
```

ensure that the group id used by the console corresponds to the group id of the node。
