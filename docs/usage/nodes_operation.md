# 区块链节点

节点是区块链上的执行单元。多个节点彼此连接，构成一个P2P网络，承载了区块链上的通信，计算和存储。节点入网后（加入联盟），成为区块链上的一个共识单位。多个节点参与共识，确保了区块链上交易的一致。

## 节点文件目录、分类

FISCO-BCOS的节点包含了下列必要的文件。此处不列举节点在运行时生成的文件。

``` log
node0
|-- genesis.json   #创世块文件（创世块信息，god账号，创世节点）
|-- config.json    #节点总配置文件（IP，端口，共识算法）
|-- log.conf       #节点日志配置文件（日志格式，优先级）
|-- start.sh       #节点启动脚本
|-- stop.sh        #节点停止脚本
|-- data
|   |-- bootstrapnodes.json    #节点启动时需访问的peers列表
|   |-- ca.crt                 #链根证书私钥
|   |-- agency.crt             #机构证书私钥
|   |-- node.crt               #节点证书私钥
|   |-- node.ca          
|   |-- node.csr
|   |-- node.json
|   |-- node.key
|   |-- node.nodeid
|   |-- node.param
|   |-- node.private
|   |-- node.pubkey
|   `-- node.serial            #节点证书序列号
|-- keystore
|-- fisco-bcos.log   #节点启动日志
`-- log  #节点运行日志目录
```

其中，按类型归类：

* 配置文件：genesis.json、config.json、log.conf、bootstrapnodes.json
* 证书文件：ca.crt、agency.crt、node.crt、node.csr、node.key、node.private、node.pubkey
* 功能文件：node.json
* 信息文件：node.nodeid、node.serial、node.ca 
* 日志文件：fisco-bcos.log、log文件夹
* 操作脚本：start.sh、stop.sh

## 配置文件

### genesis.json

genesis.json中配置创世块的信息，是节点启动必备的信息。

``` json
{
     "nonce": "0x0",
     "difficulty": "0x0",
     "mixhash": "0x0",
     "coinbase": "0x0",
     "timestamp": "0x0",
     "parentHash": "0x0",
     "extraData": "0x0",
     "gasLimit": "0x13880000000000",
     "god":"0xf78451eb46e20bc5336e279c52bda3a3e92c09b6",
     "alloc": {}, 	
     "initMinerNodes":["d23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd"]
}
```

字段说明

| 配置项            | 说明                                       |
| -------------- | ---------------------------------------- |
| timestamp      | 创世块时间戳(毫秒)                               |
| god            | 内置链管理员账号地址（填入<u>2.2 生成god账号</u> 小节中生成的地址） |
| alloc          | 内置合约数据                                   |
| initMinerNodes | 创世块节点NodeId（填入<u>2.3 生成节点身份NodeId</u>小节中生成的NodeId） |

### config.json

节点的总配置文件，配置节点的IP，端口，共识算法，data目录等等

``` json
{
        "sealEngine": "PBFT",
	"systemproxyaddress":"0x919868496524eedc26dbb81915fa1547a20f8998",
        "listenip":"127.0.0.1",
        "cryptomod":"0",
        "rpcport": "8545",
        "p2pport": "30303",
        "channelPort": "8891",
        "wallet":"./data/keys.info",
        "keystoredir":"./data/keystore/",
        "datadir":"./data/",
        "vm":"interpreter",
        "networkid":"12345",
        "logverbosity":"4",
        "coverlog":"OFF",
        "eventlog":"ON",
        "statlog":"OFF",
        "logconf":"./log.conf"
}

```

字段说明

**注意：rpcport 和 channelPort 仅限于被机构内的监控、运维、sdk等模块访问，切勿对外网开放**

| 配置项                | 说明                                       |
| ------------------ | ---------------------------------------- |
| sealEngine         | 共识算法（可选PBFT、RAFT、SinglePoint）            |
| systemproxyaddress | 系统路由合约地址（生成方法可参看部署系统合约）                  |
| listenip           | 节点监听IP                                   |
| cryptomod          | 加密模式默认为0                                 |
| rpcport            | RPC监听端口）（若在同台机器上部署多个节点时，端口不能重复）          |
| p2pport            | P2P网络监听端口（若在同台机器上部署多个节点时，端口不能重复）         |
| channelPort        | 链上链下监听端口（若在同台机器上部署多个节点时，端口不能重复）          |
| wallet             | 钱包文件路径                                   |
| keystoredir        | 账号文件目录路径                                 |
| datadir            | 节点数据目录路径                                 |
| vm                 | vm引擎 （默认 interpreter ）                   |
| networkid          | 网络ID                                     |
| logverbosity       | 日志级别（级别越高日志越详细，>8 TRACE日志，4<=x<8 DEBU G日志，<4 INFO日志） |
| coverlog           | 覆盖率插件开关（ON或OFF）                          |
| eventlog           | 合约日志开关（ON或OFF）                           |
| statlog            | 统计日志开关（ON或OFF）                           |
| logconf            | 日志配置文件路径（日志配置文件可参看日志配置文件说明）              |
| dfsNode            | 分布式文件服务节点ID ，与节点身份NodeID一致 （可选功能配置参数）    |
| dfsGroup           | 分布式文件服务组ID （10 - 32个字符）（可选功能配置参数）        |
| dfsStorage         | 指定分布式文件系统所使用文件存储目录（可选功能配置参数）             |

### log.conf

log.conf中配置节点日志生成的格式、路径和优先级。

``` conf
* GLOBAL:  
    ENABLED                 =   true  
    TO_FILE                 =   true  
    TO_STANDARD_OUTPUT      =   false  
    FORMAT                  =   "%level|%datetime{%Y-%M-%d %H:%m:%s:%g}|%msg"   
    FILENAME                =   "./log/log_%datetime{%Y%M%d%H}.log"  
    MILLISECONDS_WIDTH      =   3  
    PERFORMANCE_TRACKING    =   false  
    MAX_LOG_FILE_SIZE       =   209715200 ## 200MB - Comment starts with two hashes (##)
    LOG_FLUSH_THRESHOLD     =   100  ## Flush after every 100 logs
      
* TRACE:  
    ENABLED                 =   true
    FILENAME                =   "./log/trace_log_%datetime{%Y%M%d%H}.log"  
      
* DEBUG:  
    ENABLED                 =   true
    FILENAME                =   "./log/debug_log_%datetime{%Y%M%d%H}.log"  

* FATAL:  
    ENABLED                 =   true  
    FILENAME                =   "./log/fatal_log_%datetime{%Y%M%d%H}.log"
      
* ERROR:  
    ENABLED                 =   true
    FILENAME                =   "./log/error_log_%datetime{%Y%M%d%H}.log"  
      
* WARNING: 
     ENABLED                 =   true
     FILENAME                =   "./log/warn_log_%datetime{%Y%M%d%H}.log"
 
* INFO: 
    ENABLED                 =   true
    FILENAME                =   "./log/info_log_%datetime{%Y%M%d%H}.log"  
      
* VERBOSE:  
    ENABLED                 =   true
    FILENAME                =   "./log/verbose_log_%datetime{%Y%M%d%H}.log"

```

字段说明

| 配置项                 | 说明                                       |
| ------------------- | ---------------------------------------- |
| FORMAT              | 日志格式，典型如%level                           |
| FILENAME            | 例如/mydata/nodedata-1/log/log_%datetime{%Y%M%d%H}.log |
| MAX_LOG_FILE_SIZE   | 最大日志文件大小                                 |
| LOG_FLUSH_THRESHOLD | 超过多少条日志即可落盘                              |

### bootstrapnodes.json

配置节点启动时主动去连接的节点。在连接成功后，节点会自动同步彼此的peers，进而连接更多的节点。

``` json
{"nodes":[
        {"host":"127.0.0.1","p2pport":"30303"},
        {"host":"127.0.0.1","p2pport":"30304"}
]}
```

## 证书文件

参考[证书说明](https://fisco-bcos-test.readthedocs.io/zh/latest/docs/usage/cert_permission.html)

## 功能文件

### node.json

节点注册入网需要提供的文件，在生成节点证书时自动生成。

``` json
 {
     "id":"d23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd",
     "name":"node0",
     "agency":"test_agency",
     "caHash":"A809F269BEE93DA4"
}
```

## 信息文件

### node.nodeid

保存节点nodeid信息，在生成节点证书时自动生成。

``` log
d23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd
```

### node.serial

保存节点证书的序列号，在生成节点证书时自动生成。

``` log
A809F269BEE93DA4
```

## 日志文件

### fisco-bcos.log

节点的启动日志。若节点无法启动时，查看此日志。正常启动时日志如下。

``` log
XXXXXX
```

### log文件夹

节点运行时打印出的日志，按照日志优先级，存放于此。

``` log
log/
|-- debug_log_2018081319.log
|-- error_log_2018081319.log
|-- fatal_log_2018081319.log
|-- info_log_2018081319.log
|-- log_2018081319.log         #全部的日志
|-- stat_log_2018081319.log    #统计日志
|-- trace_log_2018081319.log
|-- verbose_log_2018081319.log
`-- warn_log_2018081319.log
```

## 操作脚本

### start.sh

必须cd到脚本所在目录下才能正确运行此脚本。执行后，节点在后台被启动。

``` shell
cd /mydata/node0
sh start.sh
```

### stop.sh

与start.sh配合，必须cd到脚本所在目录下才能正确使用此脚本停掉节点。

``` shell
cd /mydata/node0
sh stop.sh
```

