# tars配置文件

标签：``Pro版区块链网络`` ``tars.conf`` ``tars_proxy.ini`` ``不依赖tars网页管理台``

------------

搭建不依赖tars网页管理台的环境，由于没有tars管理后台的存在，各个服务的tars模块监听和连接信息需要使用配置文件管理。

每个服务会有两个额外的配置文件`tars.conf`和`tars_proxy.ini`

```shell
$ ls -a 127.0.0.1/*/conf/tars.conf
127.0.0.1/gateway_30300/conf/tars.conf          
127.0.0.1/group0_node_40402/conf/tars.conf      
127.0.0.1/rpc_20200/conf/tars.conf
127.0.0.1/gateway_30301/conf/tars.conf          
127.0.0.1/group0_node_40412/conf/tars.conf      
127.0.0.1/rpc_20201/conf/tars.conf
```

```shell
$ ls -a 127.0.0.1/*/conf/tars_proxy.ini
127.0.0.1/gateway_30300/conf/tars_proxy.ini     
127.0.0.1/group0_node_40402/conf/tars_proxy.ini 
127.0.0.1/rpc_20200/conf/tars_proxy.ini
127.0.0.1/gateway_30301/conf/tars_proxy.ini     
127.0.0.1/group0_node_40412/conf/tars_proxy.ini 
127.0.0.1/rpc_20201/conf/tars_proxy.ini
```

## 1. tars.conf

服务内部tars模块的服务端监听信息

### 1.1 RPC服务

```shell
$ cat 127.0.0.1/rpc_20200/conf/tars.conf
<tars>
  <application>
    enableset=n
    setdivision=NULL
    <server>
      app=chain0
      server=agencyABcosRpcService
      localip=127.0.0.1
      basepath=./conf/
      datapath=./.data/
      logpath=./log/
      logsize=100M
      lognum=5
      logLevel=INFO
      deactivating-timeout=3000
      activating-timeout=10000
      opencoroutine=0
      coroutinememsize=1G
      coroutinestack=128K
      closecout=0
      netthread=4
      <chain0.agencyABcosRpcService.RpcServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40400 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyABcosRpcService.RpcServiceObj
        threads=8
      </chain0.agencyABcosRpcService.RpcServiceObjAdapter>
    </server>
    <client>
      sync-invoke-timeout=3000
      async-invoke-timeout=5000
      asyncthread=8
      modulename=chain0.agencyABcosRpcService
    </client>
  </application>
</tars>
```

tars配置详情参考 <https://doc.tarsyun.com/#/base/tars-template.md>

如配置示例:

```shell
<chain0.agencyABcosRpcService.RpcServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40400 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyABcosRpcService.RpcServiceObj
        threads=8
      </chain0.agencyABcosRpcService.RpcServiceObjAdapter> 
```

RPC服务内部有一个tars rpc模块，监听端口`40400`

注意: 服务内部的`tars`监听信息修改，可以在构建时修改`config.toml`的`[agency.rpc] tars_listen_ip`和`tars_listen_port`配置

```shell
[agency.rpc]
    deploy_ip=["127.0.0.1"]
    # rpc listen ip
    listen_ip="0.0.0.0"
    # rpc listen port
    listen_port=20201
    thread_count=4
    # rpc tars server listen ip
    tars_listen_ip="0.0.0.0"   # 修改tars监听的IP
    # rpc tars server listen port
    tars_listen_port=40410     # 修改tars监听的端口
```

### 1.2 Gateway网关服务

```shell
$ cat 127.0.0.1/gateway_30300/conf/tars.conf
<tars>
  <application>
    enableset=n
    setdivision=NULL
    <server>
      app=chain0
      server=agencyABcosGatewayService
      localip=127.0.0.1
      basepath=./conf/
      datapath=./.data/
      logpath=./log/
      logsize=100M
      lognum=10
      logLevel=INFO
      deactivating-timeout=3000
      activating-timeout=10000
      opencoroutine=0
      coroutinememsize=1G
      coroutinestack=128K
      closecout=0
      netthread=4
      <chain0.agencyABcosGatewayService.GatewayServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40401 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyABcosGatewayService.GatewayServiceObj
        threads=8
      </chain0.agencyABcosGatewayService.GatewayServiceObjAdapter>
    </server>
    <client>
      sync-invoke-timeout=3000
      async-invoke-timeout=5000
      asyncthread=8
      modulename=chain0.agencyABcosGatewayService
    </client>
  </application>
</tars>
```

tars配置详情参考 <https://doc.tarsyun.com/#/base/tars-template.md>

如配置示例:

```shell
<chain0.agencyABcosGatewayService.GatewayServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40401 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyABcosGatewayService.GatewayServiceObj
        threads=8
      </chain0.agencyABcosGatewayService.GatewayServiceObjAdapter>
```

Gateway网关服务内部有一个tars gateway模块，监听端口`40401`

注意: 服务内部的`tars`监听信息修改，可以在构建时修改`config.toml`的`[agency.gateway] tars_listen_ip`和`tars_listen_port`配置

```shell
[agency.gateway]
    deploy_ip=["127.0.0.1"]
    # gateway listen ip
    listen_ip="0.0.0.0"
    # gateway listen port
    listen_port=30300
    # gateway connected peers, should be all of the gateway peers info
    peers=["127.0.0.1:30300", "127.0.0.1:30301"]
    # gateway tars server listen ip
    tars_listen_ip="0.0.0.0"        # 修改tars监听IP
    # gateway tars server listen port
    tars_listen_port=40401          # 修改tars监听端口
```

### 1.3 节点服务

```shell
cat 127.0.0.1/group0_node_40402/conf/tars.conf
<tars>
  <application>
    enableset=n
    setdivision=NULL
    <server>
      app=chain0
      server=agencyAgroup0node0BcosNodeService
      localip=127.0.0.1
      basepath=./conf/
      datapath=./.data/
      logpath=./log
      logsize=100M
      lognum=10
      logLevel=INFO
      deactivating-timeout=3000
      activating-timeout=10000
      opencoroutine=0
      coroutinememsize=1G
      coroutinestack=128K
      closecout=0
      netthread=4
      <chain0.agencyAgroup0node0BcosNodeService.TxPoolServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40402 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.TxPoolServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.TxPoolServiceObjAdapter>
      <chain0.agencyAgroup0node0BcosNodeService.SchedulerServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40403 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.SchedulerServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.SchedulerServiceObjAdapter>
      <chain0.agencyAgroup0node0BcosNodeService.PBFTServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40404 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.PBFTServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.PBFTServiceObjAdapter>
      <chain0.agencyAgroup0node0BcosNodeService.LedgerServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40405 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.LedgerServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.LedgerServiceObjAdapter>
      <chain0.agencyAgroup0node0BcosNodeService.FrontServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40406 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.FrontServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.FrontServiceObjAdapter>
    </server>
    <client>
      sync-invoke-timeout=3000
      async-invoke-timeout=5000
      asyncthread=8
      modulename=chain0.agencyAgroup0node0BcosNodeService
    </client>
  </application>
</tars>
```

如配置示例:

节点服务内部包含5个tars模块: TxPool、Scheduler、PBFT、Ledger和Front。

- TxPool

```shell
<chain0.agencyAgroup0node0BcosNodeService.TxPoolServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40402 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.TxPoolServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.TxPoolServiceObjAdapter>
```

交易池模块监听端口: 40402

- Scheduler

```shell
<chain0.agencyAgroup0node0BcosNodeService.SchedulerServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40403 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.SchedulerServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.SchedulerServiceObjAdapter>
```

Scheduler模块监听端口: 40403

- PBFT

```shell
<chain0.agencyAgroup0node0BcosNodeService.PBFTServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40404 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.PBFTServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.PBFTServiceObjAdapter>
```

PBFT模块监听端口: 40404

- Ledger

```shell
 <chain0.agencyAgroup0node0BcosNodeService.LedgerServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40405 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.LedgerServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.LedgerServiceObjAdapter>
```

Ledger模块监听端口: 40405

- Front

```shell
<chain0.agencyAgroup0node0BcosNodeService.FrontServiceObjAdapter>
        allow
        endpoint=tcp -h 127.0.0.1 -p 40406 -t 60000
        maxconns=100000
        protocol=tars
        queuecap=50000
        queuetimeout=20000
        servant=chain0.agencyAgroup0node0BcosNodeService.FrontServiceObj
        threads=8
      </chain0.agencyAgroup0node0BcosNodeService.FrontServiceObjAdapter>
```

Front模块监听端口: 40406

注意:

- 服务内部的`tars`监听端口修改，可以在构建时修改`config.toml`的`[[agency.group]] [[agency.group.node]] tars_listen_ip`和`tars_listen_port`配置
- 节点服务内部需要分配五个连续的端口，范围是[tars_listen_port, tars_listen_port+4]，请注意端口冲突

```shell
[[agency.group]]
        group_id = "group0"
        [[agency.group.node]]
        # node name, Notice: node_name in the same agency and group must be unique
        node_name = "node0"
        deploy_ip = "127.0.0.1"
        # node tars server listen ip
        tars_listen_ip="0.0.0.0"        # 修改tars监听IP
        # node tars server listen port, Notice: the tars server of the node will cost five ports, then the port tars_listen_port ~ tars_listen_port + 4 should be in free
        tars_listen_port=40402          # 修改tars监听端口
        # enable data disk encryption for bcos node or not, default is false
        enable_storage_security = false
        # url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
        # key_center_url =
        # cipher_data_key =
        monitor_listen_port = "3902"
        # monitor log path example:"/home/fisco/tars/framework/app_log/"
        monitor_log_path = ""
```

## tars_proxy.ini

tars客户端连接信息

```shell
$ cat generated/chain0/agencyA_tars_proxy.ini
[rpc]
proxy.0 = 127.0.0.1:40400

[gateway]
proxy.0 = 127.0.0.1:40401

[txpool]
proxy.0 = 127.0.0.1:40402

[scheduler]
proxy.0 = 127.0.0.1:40403

[pbft]
proxy.0 = 127.0.0.1:40404

[ledger]
proxy.0 = 127.0.0.1:40405

[front]
proxy.0 = 127.0.0.1:40406
```

示例:

```shell
[rpc]
proxy.0 = 127.0.0.1:40400
```

上面的配置表示，服务内部模块如果需要与rpc模块通信，使用的endpoint为`127.0.0.1:40400`

**注意:**

- 机构内部各个服务的`tars_proxy.ini`建议保持一致
- 在扩容时扩容的服务会生成新的文件，需要将新生成的`机构名_tars_proxy.ini`合并到已使用的`tars_proxy.ini`文件，并同步到所有的服务，服务需要重启后生效，否则新扩容的服务无法接入到现有环境