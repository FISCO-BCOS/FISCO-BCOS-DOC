# Configuration Description

Tags: "Python SDK" "Certificate Configuration"

----

'client _ config.py 'is the configuration file of the Python SDK.**SDK Algorithm Type Configuration**，**Common Configuration**，**Account Configuration**，**Group Configuration**，**Communication Configuration**和**Certificate Configuration**。

```eval_rst
.. note::

    - Ensure that the connection port is open: We recommend that you use "telnet ip port" to check whether the client is connected to the node network.
    - Use the RPC communication protocol without setting up a certificate
    - For more information about log configuration, see "client / clientlogger.py". By default, logs are generated in the "bin / logs" directory. The default level is DEBUG.
```

## SDK Algorithm Type Configuration

- **crypto_type**: SDK interface type. Currently, it supports the national secret interface.(`GM`)and non-state secret interface(`ECDSA`)

## Common Configuration

- **contract_info_file**: The file that saves the deployed contract information. The default value is' bin / contract.ini'
- **account_keyfile_path**: The directory where keystore files are stored. The default value is' bin / accounts'.
- **logdir**Default log output directory. The default value is bin / logs.

## Account Configuration

**The configuration of the non-State secret account is as follows.**

- **account_keyfile**: The path of the keystore file that stores non-state secret account information. The default value is' pyaccount.keystore '.
- **account_password**: The storage password of the non-state secret keystore file, which is' 123456 'by default

**The configuration of the State Secret account is as follows.**
- **gm_account_keyfile**: The path of the encrypted file that stores the information of the national secret account. The default value is' gm _ account.json'
- **gm_account_password**: The storage password of the State Secret account file, which defaults to '123456'


## Group Configuration

Group configuration mainly includes chain ID and group ID:

- **fiscoChainId**Chain ID, which must be the same as that of the communication node. The default value is 1
- **groupid**The ID of the group. The ID must be the same as that of the communication node. The default value is 1.

## Communication Configuration

- **client_protocol**Python SDK and node communication protocol, including 'rpc' and 'channel' options, the former using JSON-The RPC interface accesses the node by using the channel. You need to configure a certificate. The default value is' channel'
- **remote_rpcurl**： 采用**rpc**The rpc IP address and port of the node. The default value is' http '.://127.0.0.1:8545`，**If the channel protocol is used, it can be left blank.**
- **channel_host**When the channel protocol is used, the channel IP address of the node is' 127.0.0.1 'by default.**If you use the rpc protocol to communicate, you can leave it blank.**
- **channel_port**The channel port of the node. The default value is 20200.**If you use the rpc protocol to communicate, you can leave it blank.**

## Certificate Configuration

- **channel_ca**: Chain CA Certificate,**Setting when using the channel protocol**the default is' bin / ca.crt ',
- **channel_node_cert**node certificate,**Setting when using the channel protocol**the default value is' bin / sdk.crt '.**If you use the rpc protocol to communicate, you can leave it blank.**
- **channel_node_key**The private key for communication between the Python SDK and the node. It must be set when the channel protocol is used. The default value is' bin / sdk.key '.**If you use the rpc protocol to communicate, you can leave it blank here.**

## solc compiler configuration

The Python SDK allows you to automatically compile contracts using the configured solc and solcjs compilers. When you configure both solc and solcjs, you can select a high-performance solc compiler. The compilation options are as follows:

- **solc_path**: non-state secret solc compiler path

- **gm_solc_path**: State Secret Compiler Path

- **solcjs_path**Solcjs compilation script path, which is'. / solc.js'


## Configuration Item Example

**Examples of configuration items are as follows**：

```python
    """
    Class member constants and variables for easy .call and namespace differentiation
    """
    # keyword used to represent the RPC Protocol
    PROTOCOL_RPC = "rpc"
    # keyword used to represent the Channel Protocol
    PROTOCOL_CHANNEL = "channel"

    # ---------crypto_type config--------------
    # crypto_type : Case insensitive:"GM" for national secrets,"ECDSA" or others are the default implementations of elliptic curves。
    crypto_type = "ECDSA"
    crypto_type = crypto_type.upper()
    set_crypto_type(crypto_type)  # Make the password algorithm mode take effect globally, do not delete this line

    # --------------------------------------
    # configure below
    # ---------client communication config--------------
    client_protocol = "channel"  # or PROTOCOL_CHANNEL to use channel prototol
    # client_protocol = PROTOCOL_CHANNEL
    remote_rpcurl = "http://127.0.0.1:8545"  # When using rpc communication, the rpc port of the node and the node to communicate with*Must*Consistent, such as the use of channel protocol communication, here can be left blank
    channel_host = "127.0.0.1"  # When using channel communication, the channel ip address of the node, such as using rpc protocol communication, can be left blank here.
    channel_port = 20200  # The channel port of the node. If the RPC protocol is used for communication, leave it blank.
    channel_ca = "bin/ca.crt"  # When using the channel protocol, you need to set the chain certificate, such as the use of rpc protocol communication, this can be left blank
    channel_node_cert = "bin/sdk.crt"  # When using the channel protocol, you need to set the sdk certificate. If you use the rpc protocol for communication, you can leave it blank.
    channel_node_key = "bin/sdk.key"  # When using the channel protocol, you need to set the sdk private key, such as using the rpc protocol communication, this can be left blank
    fiscoChainId = 1  # chain ID, and the node to communicate with*Must*Consistent
    groupid = 1  # Group ID, and the node to communicate with*Must*Consistent, such as communicating with other groups, modifying this item, or setting the corresponding member variable in bcosclient.py

    # ---------account &keyfile config--------------
    # Pay attention to the account part, the national secret and ECDSA use different configurations
    contract_info_file = "bin/contract.ini"  # File to save deployed contract information
    account_keyfile_path = "bin/accounts"  # The path where the keystore file is saved, where the keystore file is named after [name] .keystore
    account_keyfile = "pyaccount.keystore"
    account_password = "123456"  # It is recommended to change to a complex password in actual use.
    gm_account_keyfile = "gm_account.json"  # The storage file of the national secret account, which can be encrypted and stored. If it is left blank, gm _ account _ password ="123456"
    gm_account_password = "123456"
    # ---------console mode, support user input--------------
    background = True

    # ---------runtime related--------------
    # Non-secret compiler path
    solc_path = "./bin/solc/v0.4.25/solc"
    # State Secret Compiler Path
    gm_solc_path = "./bin/solc/v0.4.25/solc-gm" 
    solcjs_path = "./solcjs"

    logdir = "bin/logs"  # The default log output directory, which must first be created
```
