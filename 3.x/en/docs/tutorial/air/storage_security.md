# Deploy Disk Encryption Node

Tags: "Storage Security" "Storage Encryption" "Drop Disk Encryption"

----

Alliance chain data, visible only to members within the alliance。Drop disk encryption ensures the security of data running the alliance chain on the hard disk。Once the hard drive is taken out of the Alliance chain's own intranet environment, the data cannot be decrypted。

Disk encryption is the encryption of the contents of the node stored on the hard disk, including: contract data, the node's private key.。

For a specific introduction to falling disk encryption, please refer to: [Introduction to falling disk encryption](../../design/storage/storage_security.md)

## 1. Deploy Key Manager

Each organization has a Key Manager. For more information, see [Key Manager Github README](https://github.com/FISCO-BCOS/key-manager)or [Key Manager Gitee README](https://gitee.com/FISCO-BCOS/key-manager)

```eval_rst
.. important::
    If the node is in the state secret version, the key manager must be started in the state secret mode. Here, the non-state secret version is used as an example.。
```

## 2. Generate blockchain nodes

Refer to [Building the First Blockchain Network](../../quick_start/air_installation.md)to generate four air version nodes using the build _ chain.sh script。

Download the 'build _ chain.sh' script
``` shell
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v3.6.0/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - If the build _ chain.sh script cannot be downloaded for a long time due to network problems, try 'curl-#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v3.6.0/build_chain.sh && chmod u+x build_chain.sh`
```

Deploy four nodes:

```bash
bash build_chain.sh -l 127.0.0.1:4
```

```eval_rst
.. important::
    After the node is generated, it cannot be started. After the dataKey is configured, write the obtained cipher _ data _ key to the node's configuration file before starting。Unable to toggle Enable / Disable Drop Disk Encryption mode once node starts running。
```

## 3. Start Key Manager

Start 'key by referring to the following command-manager`。If 'key' has not been deployed-manager '. For details, see Deploy Key Manager in the previous section of this document.

```shell
# Parameters: port, superkey
./key-manager 8150 123xyz

# If it is a state-secret blockchain node, use the following command to start Key Manager
./key-manager 8150 123xyz -g
```

Start successfully, print log

```log
[1546501342949][TRACE][Load]key-manager started,port=8150
```

## 4. Configure dataKey

```eval_rst
.. important::
    The node on which the dataKey is configured must be a newly generated node that has not been started.。If the node that has been started to disable the disk encryption mode modifies the configuration file to enable disk encryption, the node will not start normally, please be cautious。
```

Execute the script, define 'dataKey', and obtain 'cipherDataKey'

```shell
cd key-manager/scripts
bash gen_data_secure_key.sh 127.0.0.1 8150 123456

CiherDataKey generated: ed157f4588b86d61a2e1745efe71e6ea
Append these into config.ini to enable disk encryption:
[storage_security]
enable=true
key_manager_url=127.0.0.1:8150
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

The script automatically prints the ini configuration required for disk encryption.。
The cipherDataKey of the node is obtained: "'cipher _ data _ key = ed157f4588b86d61a2e1745efe71e6ea"'
Write the resulting encrypted ini configuration to the node configuration file ([config.ini](../tutorial/air/config.md)) in。

```shell
vim nodes/127.0.0.1/node0/config.ini
```

Modify the fields in '[storage _ security]' as follows。

```ini
[storage_security]
enable=true
key_manager_url=127.0.0.1:8150
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

## 5. Encrypt the node private key.

Execute the script to encrypt the private keys of all nodes. The node 'node0' is used as an example.。

```bash
$ cd key-manager/scripts
# Parameter: ip port node private key file cipherDataKey
# Encrypt SSL connection private key
$ bash encrypt_node_key.sh 127.0.0.1 8150 ../../nodes/127.0.0.1/node0/conf/ssl.key ed157f4588b86d61a2e1745efe71e6ea
[INFO] File backup to "nodes/127.0.0.1/node0/conf/ssl.key.bak.1546502474"
[INFO] "nodes/127.0.0.1/node0/conf/ssl.key" encrypted!

# Encryption Node Signature Private Key
$ bash encrypt_node_key.sh 127.0.0.1 8150 ../../nodes/127.0.0.1/node0/conf/node.pem ed157f4588b86d61a2e1745efe71e6ea
[INFO] File backup to "nodes/127.0.0.1/node0/conf/node.pem.bak.1546502474"
[INFO] "nodes/127.0.0.1/node0/conf/node.pem" encrypted!
```

After execution, the node private key is automatically encrypted. The files before encryption are backed up to the files' ssl.key.bak.xxxxxx 'and' node.pem.bak.xxxxxx '.**Keep the backup private key safe and delete the backup private key generated on the node**


If you view 'ssl.key', you can see that it has been encrypted as ciphertext

``` shell
8b2eba71821a5eb15b0cbe710e96f23191419784f644389c58e823477cf33bd73a51b6f14af368d4d3ed647d9de6818938ded7b821394446279490b537d04e7a7e87308b66fc82ab3987fb9f3c7079c2477ed4edaf7060ae151f237b466e4f3f8a19be268af65d7b4ae8be37d81810c30a0f00ec7146a1125812989c2205e1e37375bc5e4654e569c21f0f59b3895b137f3ede01714e2312b74918e2501ac6568ffa3c10ae06f7ce1cbb38595b74783af5fea7a8a735309db3e30c383e4ed1abd8ca37e2aa375c913e3d049cb439f01962dd2f24b9e787008c811abd9a92cfb7b6c336ed78d604a3abe3ad859932d84f11506565f733d244f75c9687ef9334b8fabe139a82e9640db9e956b540f8b61675d04c3fb070620c7c135f3f4f6319aae8b6df2b091949a2c9938e5c1e5bb13c0f530764b7c2a884704637be953ce887
```

**Note: All files that need to be encrypted are listed below。Node cannot start without encryption。**

   - non-state secret edition
        - conf/ssl.key
        - conf/node.pem
   - State Secret Edition
        - conf/sm_ssl.key 
        - conf/sm_enssl.key 
        - conf/node.pem

## 6. Node running

Start Node:

```shell
cd nodes/127.0.0.1/node0/
bash start.sh
```