# Storage security

The data of the alliance chain is only visible to members of the alliance. Disk encryption ensures the security of the data running on the alliance chain on the hard disk. Once the hard drive is taken out from the intranet environment of alliance chain, the data will not be decrypted.

Disk encryption encrypts the content stored on the hard disk by the node. The encrypted content includes: the data of the contract and the private key of the node.

For specific disk encryption introduction, please refer to: [Introduction of Disk Encryption](../design/features/storage_security.md)


## Key Manager deployment

Each agency has a Key Manager. For specific deployment steps, please refer to [Key Manager README](https://github.com/FISCO-BCOS/key-manager) or [Key Manager Gitee README](https://gitee.com/FISCO-BCOS/key-manager)

## Node building

Use the script [```build_chain.sh```] (../installation.md) to build a node with normal operations.

``` shell
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/`curl -s https://api.github.com/repos/FISCO-BCOS/FISCO-BCOS/releases | grep "\"v2\.[0-9]\.[0-9]\"" | sort -u | tail -n 1 | cut -d \" -f 4`/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - If the script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/build_chain.sh`
```

``` shell
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545
```

```eval_rst
.. important::
    The node cannot be launched until the dataKey is configured. Before the node runs for the first time, it must be configured to use the disk encryption or not. Once the node starts running, it cannot be switched its state.
```

## Key Manager launch

To launch `key-manager` directly. If `key-manager` is not deployed, refer to [Key Manager README Introduction](https://github.com/FISCO-BCOS/key-manager).


```shell
# parameter: port，superkey
./key-manager 8150 123xyz
```
launch successfully and print the log.

```log
[1546501342949][TRACE][Load]key-manager started,port=8150
```

## DataKey configuration

```eval_rst
.. important::
    The node configured by the dataKey must be newly generated node and has not been launched.
```

To execute the script, define `dataKey`, and get `cipherDataKey`

```shell
cd key-manager/scripts
bash gen_data_secure_key.sh 127.0.0.1 8150 123456

CiherDataKey generated: ed157f4588b86d61a2e1745efe71e6ea
Append these into config.ini to enable disk encryption:
[storage_security]
enable=true
key_manager_ip=127.0.0.1
key_manager_port=8150
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

The script for getting `cipherDataKey` automatically prints out the ini configuration required for the disk encryption (see below). Now, the cipherDataKey is:``` cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea ```

To write the ini configuration that has been disk encryption to the node configuration file ([config.ini](configuration.md)).

```shell
vim nodes/127.0.0.1/node0/config.ini
```

To put it at last like this.

```ini
[storage_security]
enable=true
key_manager_ip=127.0.0.1
key_manager_port=8150
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

## Encrypted node private key

To execute script and encrypt node private key

```shell
cd key-manager/scripts
# parameter:ip port node private key file cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 8150 ../../nodes/127.0.0.1/node0/conf/node.key ed157f4588b86d61a2e1745efe71e6ea
```

The node private key is automatically encrypted after execution, and the files before encryption is backed up to the file ``` node.key.bak.xxxxxx ```. **Please take care of the backup private key and delete the backup private key generated on the node**

```log
[INFO] File backup to "nodes/127.0.0.1/node0/conf/node.key.bak.1546502474"
[INFO] "nodes/127.0.0.1/node0/conf/node.key" encrypted!
```

If you check the `node.key`, you can see that it has been encrypted as ciphertext.

``` shell
8b2eba71821a5eb15b0cbe710e96f23191419784f644389c58e823477cf33bd73a51b6f14af368d4d3ed647d9de6818938ded7b821394446279490b537d04e7a7e87308b66fc82ab3987fb9f3c7079c2477ed4edaf7060ae151f237b466e4f3f8a19be268af65d7b4ae8be37d81810c30a0f00ec7146a1125812989c2205e1e37375bc5e4654e569c21f0f59b3895b137f3ede01714e2312b74918e2501ac6568ffa3c10ae06f7ce1cbb38595b74783af5fea7a8a735309db3e30c383e4ed1abd8ca37e2aa375c913e3d049cb439f01962dd2f24b9e787008c811abd9a92cfb7b6c336ed78d604a3abe3ad859932d84f11506565f733d244f75c9687ef9334b8fabe139a82e9640db9e956b540f8b61675d04c3fb070620c7c135f3f4f6319aae8b6df2b091949a2c9938e5c1e5bb13c0f530764b7c2a884704637be953ce887
```

```eval_rst
.. important::
    All files that need to be encrypted are listed below. If they are not encrypted, the node cannot be launched.

    - standard version: conf/node.key
    - national cryptography version: conf/gmnode.key和conf/origin_cert/node.key

```



## Node running

to launch node directly

```shell
cd nodes/127.0.0.1/node0/
./start.sh
```

## Correct judgment

(1) The node runs and generates block normally, and the block information is continuously output.

``` shell
tail -f nodes/127.0.0.1/node0/log/* | grep +++
```

(2) `key-manager` will print a log each time the node launches. For example, when a node launches, the log directly output by Key Manager is as follows.

``` log
[1546504272699][TRACE][Dec]Respond
{
   "dataKey" : "313233343536",
   "error" : 0,
   "info" : "success"
}
```
