# 存储加密

标签：``存储安全`` ``存储加密`` ``落盘加密`` 

----

联盟链的数据，只对联盟内部成员可见。落盘加密，保证了运行联盟链的数据，在硬盘上的安全性。一旦硬盘被带出联盟链自己的内网环境，数据将无法被解密。

落盘加密是对节点存储在硬盘上的内容进行加密，加密的内容包括：合约的数据、节点的私钥。

具体的落盘加密介绍，可参考：[落盘加密的介绍](../design/features/storage_security.md)

## 部署Key Manager

每个机构一个Key Manager，具体的部署步骤，可参考[Key Manager Github README](https://github.com/FISCO-BCOS/key-manager)或[Key Manager Gitee README](https://gitee.com/FISCO-BCOS/key-manager)

```eval_rst
.. important::
    若节点为国密版，Key Manager也需是国密版。
```

## 生成节点

用[```build_chain.sh```](../installation.md)脚本，用普通的操作方法，先生成节点。

下载`build_chain.sh`脚本
``` shell
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/`curl -s https://api.github.com/repos/FISCO-BCOS/FISCO-BCOS/releases | grep "\"v2\.[0-9]\.[0-9]\"" | sort -u | tail -n 1 | cut -d \" -f 4`/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载build_chain.sh脚本，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/build_chain.sh && chmod u+x build_chain.sh`
```

部署四个节点：

```bash
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545
```

```eval_rst
.. important::
    节点生成后，不能启动，待dataKey配置后，再启动。节点在第一次运行前，必须配置好是否采用落盘加密。一旦节点开始运行，无法切换状态。
```

## 启动Key Manager

直接启动`key-manager`。若未部署`key-manager`，可参考[Key Manager README](https://github.com/FISCO-BCOS/key-manager)

```shell
# 参数：端口，superkey
./key-manager 8150 123xyz
```

启动成功，打印日志

```log
[1546501342949][TRACE][Load]key-manager started,port=8150
```

## 配置dataKey

```eval_rst
.. important::
    配置dataKey的节点，必须是新生成，未启动过的节点。
```

执行脚本，定义`dataKey`，获取`cipherDataKey`

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

得到`cipherDataKey`，脚本自动打印出落盘加密需要的ini配置(如下)。此时得到节点的cipherDataKey：``` cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea ```
将得到的落盘加密的ini配置，写入节点配置文件（[config.ini](configuration.md)）中。

```shell
vim nodes/127.0.0.1/node0/config.ini
```

修改`[storage_security]`中的字段如下。

```ini
[storage_security]
enable=true
key_manager_ip=127.0.0.1
key_manager_port=8150
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

## 加密节点私钥

执行脚本，加密节点私钥

```shell
cd key-manager/scripts
# 参数：ip port 节点私钥文件 cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 8150 ../../nodes/127.0.0.1/node0/conf/node.key ed157f4588b86d61a2e1745efe71e6ea
```

执行后，节点私钥自动被加密，加密前的文件备份到了文件``` node.key.bak.xxxxxx ```中，**请将备份私钥妥善保管，并删除节点上生成的备份私钥**

```log
[INFO] File backup to "nodes/127.0.0.1/node0/conf/node.key.bak.1546502474"
[INFO] "nodes/127.0.0.1/node0/conf/node.key" encrypted!
```

若查看`node.key`，可看到，已经被加密为密文

``` shell
8b2eba71821a5eb15b0cbe710e96f23191419784f644389c58e823477cf33bd73a51b6f14af368d4d3ed647d9de6818938ded7b821394446279490b537d04e7a7e87308b66fc82ab3987fb9f3c7079c2477ed4edaf7060ae151f237b466e4f3f8a19be268af65d7b4ae8be37d81810c30a0f00ec7146a1125812989c2205e1e37375bc5e4654e569c21f0f59b3895b137f3ede01714e2312b74918e2501ac6568ffa3c10ae06f7ce1cbb38595b74783af5fea7a8a735309db3e30c383e4ed1abd8ca37e2aa375c913e3d049cb439f01962dd2f24b9e787008c811abd9a92cfb7b6c336ed78d604a3abe3ad859932d84f11506565f733d244f75c9687ef9334b8fabe139a82e9640db9e956b540f8b61675d04c3fb070620c7c135f3f4f6319aae8b6df2b091949a2c9938e5c1e5bb13c0f530764b7c2a884704637be953ce887
```

```eval_rst
.. important::
    所有需要加密的文件列举如下，若未加密，节点无法启动。

    - 非国密版：conf/node.key
    - 国密版：conf/gmnode.key和conf/origin_cert/node.key

```



## 节点运行

直接启动节点即可

```shell
cd nodes/127.0.0.1/node0/
./start.sh
```

## 正确性判断

（1）节点正常运行，正常共识，不断输出共识打包信息。

``` shell
tail -f nodes/127.0.0.1/node0/log/* | grep +++
```

（2）`key-manager`在节点每次启动时，都会打印一条日志。例如，节点在一次启动时，Key Manager直接输出的日志如下。

``` log
[1546504272699][TRACE][Dec]Respond
{
   "dataKey" : "313233343536",
   "error" : 0,
   "info" : "success"
}
```
