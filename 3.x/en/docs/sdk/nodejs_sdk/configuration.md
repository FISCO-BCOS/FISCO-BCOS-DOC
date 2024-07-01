# Configuration Description

Tags: "Node.JS SDK" "Configuration"

----
The configuration file of the Node.js SDK is a JSON file, including**Common Configuration**，**Group Configuration**，**Communication Configuration**和**Certificate Configuration**。

## Common Configuration

- `privateKey`: 'object ', required。The private key of the external account, which can be a random integer of 256 bits, or a private key file in pem or p12 format, the latter two need to be combined [get _ account.sh](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html)The generated private key file uses the。'privateKey 'contains two required fields and one optional field:
  - `type`: 'string ', required。Used to indicate the private key type。The value of 'type' must be one of the following three values:
    - 'ecrandom ': random integer
    - 'pem ': file in pem format
    - 'p12 ': file in p12 format
  - 'value ':' string ', required。The specific value used to indicate the private key:
    - If 'type' is' random ', then' value 'is a random integer of length 256 bits between 1 and 0xFFFF FFFF FFFF FFFF FFFF FFFE BAAE DCE6 AF48 A03B BFD2 5E8C D036 4141。
    - If 'type' is' pem ',' value 'is the path of the pem file. If it is a relative path, the directory where the configuration file is located must be the starting location of the relative path.。
    - If 'type' is' p12 ',' value 'is the path of the p12 file. If it is a relative path, the directory where the configuration file is located must be the starting location of the relative path.。
  - 'password ':' string ', optional。This field is required to decrypt the private key if 'type' is' p12 ', otherwise it is ignored。
- `timeout`: `number`。Nodes connected to Node.js SDK may fall into a state of stopping responding。To avoid an infinite wait, every API call of the Node.js SDK is forced to return an error object if no result is obtained after 'timeout'.。'timeout 'in milliseconds。
- `solc`: 'string ', optional。The Node.js SDK already comes with the 0.4.26 and 0.5.10 versions of the Solidity compiler. If you have special compiler requirements, you can set this configuration item to the execution path or global command of your compiler.

## Group Configuration

- `groupID`: `number`。The group ID of the chain accessed by the Node.js SDK.

## Communication Configuration

- `nodes`: 'list ', required。The list of FISCO BCOS nodes. When the Node.js SDK accesses a node, it randomly selects a node from the list for communication. The number of nodes must be greater than or equal to 1.。In FISCO BCOS, a transaction on the chain does not mean that all nodes in the network have been synchronized to the latest state, if the Node.js SDK connects multiple nodes, it may not be able to read the latest state, so in the case of higher requirements for state synchronization, please be careful to connect multiple nodes。Each node contains two fields:
  - `ip`: 'string ', required。IP address of the FISCO BCOS node
  - `port`: 'string ', required, Channel port of FISCO BCOS node

## Certificate Configuration
- `authentication`：`object`。Required, including the authentication information required to establish Channel communication, which is typically generated automatically during the construction of the chain.。'authentication 'contains three required fields:
  - `key`: 'string ', required。The path of the private key file. If the path is relative, the directory where the configuration file is located must be the starting location of the relative path.。
  - `cert`: 'string ', required。The path of the certificate file. If the path is relative, the directory where the configuration file is located must be the starting location of the relative path.。
  - `ca`: 'string ', required。The path of the CA root certificate file. If the path is relative, the directory where the configuration file is located must be the starting location of the relative path.。
