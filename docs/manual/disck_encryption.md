## 落盘加密

在进行操作前，可先阅读：[落盘加密的介绍](../design/features/disk_encryption.md)

**注意：节点在第一次运行前，必须配置好是否采用落盘加密。一旦节点开始运行，无法切换状态。**

### 启动Key Center

配置KeyCenter，编辑配置文件

```shell
vim kcconfig.ini
```

配置port和superKey

```ini
[keycenter]
port=31443
superkey=01234567012345670123456701234564
```

配置后，启动 Key Center

```shell
./keycenter kcconfig.ini
```

启动成功，打印日志

```log
[1545471609499] [TRACE] keycenter stared. Port: 31443
```

### 配置dataKey

**注意：配置dataKey的节点，必须是新生成，未启动过的节点**

执行脚本，定义dataKey，获取cipherDataKey

```shell
cd lab-bcos/tools
sh gen_diskencryption_key.sh http://127.0.0.1:31443 123456 # 参数：keycenterURL dataKey
```

得到cipherDataKey，脚本自动打印出落盘加密需要的ini配置。此时得到的节点的cipherDataKey为：``` cipherDataKey=927975ed471862711c833ec6e798eec6 ```

```ini
[diskencryption]
enable=true
keyCenterUrl=http://127.0.0.1:31443
cipherDataKey=927975ed471862711c833ec6e798eec6
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
    listen_port=20301
    ;rpc listen port
    http_listen_port=20302
[p2p]
    ;p2p listen ip
    listen_ip=0.0.0.0
    ;p2p listen port
    listen_port=20300
    ;nodes to connect
    node.0=127.0.0.1:20300
    node.1=127.0.0.1:20303


;group configurations
;if need add a new group, eg. group2, can add the following configuration:
;group_config.2=conf/group.2.ini
;group.2.ini can be populated from group.1.ini
;WARNING: group 0 is forbided
[group]
    group_data_path=data/
    group_config.1=conf/group.1.ini

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
    
[diskencryption]
enable=true
keyCenterUrl=http://127.0.0.1:31443
cipherDataKey=927975ed471862711c833ec6e798eec6
```

### 加密节点私钥

执行脚本，加密节点私钥

```shell
cd lab-bcos/tools
sh encrypt_node_key.sh http://127.0.0.1:31443 nodes/node_127.0.0.1_0/conf/node.key 927975ed471862711c833ec6e798eec6 #参数：keycenterURL 节点私钥文件 cipherDataKey
```

执行后，节点私钥自动被加密，加密前的文件备份到了文件``` node.key.bak.xxxxxx ```中

```log
[INFO] File backup to "xxxxx/nodes/node_127.0.0.1_0/conf/node.key.bak.1545473699"
[INFO] "xxxxx/nodes/node_127.0.0.1_0/conf/node.key" encrypted!
```

### 节点运行

直接启动节点即可。

```shell
cd xxxxx/nodes/node_127.0.0.1_0/
./start.sh
```