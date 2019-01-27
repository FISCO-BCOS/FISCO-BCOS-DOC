# 落盘加密

在进行操作前，可先阅读：[落盘加密的介绍](../design/features/disk_encryption.md)

## 生成节点

用```build_chain.sh```脚本，生成节点。

``` shell
cd FISCO-BCOS/tools
bash build_chain.sh -l "127.0.0.1:4" -p 12300 -e ../build/bin/fisco-bcos
```

**注意：节点在第一次运行前，必须配置好是否采用落盘加密。一旦节点开始运行，无法切换状态。**

## 启动Key Center

直接启动keycenter

```shell
./keycenter 31443 123xyz #参数：端口，superkey
```

启动成功，打印日志

```log
[1546501342949][TRACE][Load]keycenter stared,port=31443
```

## 配置dataKey

**注意：配置dataKey的节点，必须是新生成，未启动过的节点**

执行脚本，定义dataKey，获取cipherDataKey

```shell
cd keycenter/scripts
bash gen_data_secure_key.sh 127.0.0.1 31443 123456
```

得到cipherDataKey，脚本自动打印出落盘加密需要的ini配置。此时得到节点的cipherDataKey：``` cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea ```

```ini
[data_secure]
enable=true
keycenter_ip=127.0.0.1
keycenter_port=31443
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

将落盘加密的ini配置，写入节点配置文件中

```shell
vim nodes/127.0.0.1/node_127.0.0.1_0/config.ini
```

放在最后即可，如下

```ini
[rpc]
    ;rpc listen ip
    listen_ip=127.0.0.1
    ;channelserver listen port
    channel_listen_port=12301
    ;jsonrpc listen port
    jsonrpc_listen_port=12302
[p2p]
    ;p2p listen ip
    listen_ip=0.0.0.0
    ;p2p listen port
    listen_port=12300
    ;nodes to connect
    node.0=127.0.0.1:12300
    node.1=127.0.0.1:12303
    node.2=127.0.0.1:12306
    node.3=127.0.0.1:12309
    
;certificate rejected list		
[crl]		
    ;crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e3787c338a4

;group configurations
;if need add a new group, eg. group2, can add the following configuration:
;group_config.2=conf/group.2.genesis
;group.2.genesis can be populated from group.1.genesis
;WARNING: group 0 is forbided
[group]
    group_data_path=data/
    group_config.1=conf/group.1.genesis

;certificate configuration
[secure]
    ;directory the certificates located in
    data_path=conf/
    ;the node private key file
    key=node.key
    ;the node certificate file
    cert=node.crt
    ;the ca certificate file
    ca_cert=ca.crt

;log configurations
[log]
    ;the directory of the log
    LOG_PATH=./log
    GLOBAL-ENABLED=true
    GLOBAL-FORMAT=%level|%datetime{%Y-%M-%d %H:%m:%s:%g}|%msg
    GLOBAL-MILLISECONDS_WIDTH=3
    GLOBAL-PERFORMANCE_TRACKING=false
    GLOBAL-MAX_LOG_FILE_SIZE=209715200
    GLOBAL-LOG_FLUSH_THRESHOLD=100

    ;log level configuration, enable(true)/disable(false) corresponding level log
    FATAL-ENABLED=true
    ERROR-ENABLED=true
    WARNING-ENABLED=true
    INFO-ENABLED=true
    DEBUG-ENABLED=false
    TRACE-ENABLED=false
    VERBOSE-ENABLED=false
[data_secure]
enable=true
keycenter_ip=127.0.0.1
keycenter_port=31443
cipher_data_key=ed157f4588b86d61a2e1745efe71e6ea
```

## 加密节点私钥

执行脚本，加密节点私钥

```shell
cd keycenter/scripts
bash encrypt_node_key.sh 127.0.0.1 31443 xxxx/nodes/127.0.0.1/node_127.0.0.1_0/conf/node.key ed157f4588b86d61a2e1745efe71e6ea #参数：ip port 节点私钥文件 cipherDataKey
```

执行后，节点私钥自动被加密，加密前的文件备份到了文件``` node.key.bak.xxxxxx ```中，**请将备份私钥妥善保管，并删除节点上生成的备份私钥**

```log
[INFO] File backup to "xxxx/nodes/127.0.0.1/node_127.0.0.1_0/conf/node.key.bak.1546502474"
[INFO] "xxxx/nodes/127.0.0.1/node_127.0.0.1_0/conf/node.key" encrypted!
```

若查看node.key，可看到，已经被加密为密文

``` shell
8b2eba71821a5eb15b0cbe710e96f23191419784f644389c58e823477cf33bd73a51b6f14af368d4d3ed647d9de6818938ded7b821394446279490b537d04e7a7e87308b66fc82ab3987fb9f3c7079c2477ed4edaf7060ae151f237b466e4f3f8a19be268af65d7b4ae8be37d81810c30a0f00ec7146a1125812989c2205e1e37375bc5e4654e569c21f0f59b3895b137f3ede01714e2312b74918e2501ac6568ffa3c10ae06f7ce1cbb38595b74783af5fea7a8a735309db3e30c383e4ed1abd8ca37e2aa375c913e3d049cb439f01962dd2f24b9e787008c811abd9a92cfb7b6c336ed78d604a3abe3ad859932d84f11506565f733d244f75c9687ef9334b8fabe139a82e9640db9e956b540f8b61675d04c3fb070620c7c135f3f4f6319aae8b6df2b091949a2c9938e5c1e5bb13c0f530764b7c2a884704637be953ce887
```

**注意，所有需要加密的文件列举如下，若未加密，节点无法启动**

非国密版：conf/node.key

国密版：conf/gmnode.key 和 conf/origin_cert/node.key

## 节点运行

直接启动节点即可

```shell
cd xxxxx/nodes/node_127.0.0.1_0/
./start.sh
```

## 正确性判断

（1）节点正常运行，正常出块

（2）keycenter在节点每次启动时，都会打印一条日志：

``` log
[1546504272699][TRACE][Dec]Respond
{
   "dataKey" : "313233343536",
   "error" : 0,
   "info" : "success"
}
```

