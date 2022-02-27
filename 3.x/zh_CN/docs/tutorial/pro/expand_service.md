# 扩容RPC/Gateway服务

标签：``Pro版区块链网络`` ``扩容RPC服务``

------------

当RPC/Gateway服务无法支撑业务流量时，需要扩容RPC/Gateway服务，`BcosProBuilder`提供了RPC/Gateway服务扩容功能，本章通过单机扩容Pro版本FISCO BCOS联盟链的RPC/Gateway服务为例，帮助用户掌握Pro版本FISCO BCOS区块链的服务扩容步骤。

```eval_rst
.. note::
   进行RPC扩容操作前，请先参考 `搭建Pro版区块链网络 <./installation.html>`_ 部署Pro版本区块链。
```

## 1. 部署tarsnode

扩容RPC/Gateway服务前，首先需要在部署扩容的RPC/Gateway服务节点的机器上安装tarsnode，为了帮助用户能够在单机上快速体验服务扩容流程，本章直接通过桥接网络虚拟化出IP为`172.25.0.5`的容器作为安装扩容的RPC/Gateway服务节点的物理机器。

```eval_rst
.. note::
   实际生产环境中tarsnode的安装请参考 `tars安装部署 <https://newdoc.tarsyun.com/#/markdown/ TarsCloud/TarsDocs/installation/README.md>`_
```

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# linux系统: 进入到tarsnode docker-compose所在目录(macos系统可跳过)
cd docker/bridge/linux/node

# macos系统: 进入到tarsnode docker-compose所在目录(linux系统可跳过)
cd docker/bridge/mac/node

# 安装并启动tarsnode
docker-compose up -d
```
tarsnode安装成功后，可在tars网页管理平台的【运维管理】->【节点管理】中看到新安装的IP为`172.25.0.5`的tarsnode信息：

![](../../../images/tutorial/tars_node.png)


## 2. 设置RPC/Gateway服务扩容配置

```eval_rst
.. note::
   实际操作过程中，须将tars token替换为从tars网页管理平台【admin】->【用户中心】->【token管理】获取可用的token。
```

RPC/Gateway服务的扩容配置可参考`BcosProBuilder`的扩容模板`conf/config-service-expand-example.toml`，具体配置步骤如下：

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 拷贝模板配置
cp conf/config-service-expand-example.toml config.toml

# 配置tars token: 可通过tars网页管理平台的【admin】->【用户中心】->【token管理】获取可用的token
# 这里的token为：eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls
# linux系统(macOS系统跳过本步骤):
sed -i 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml
# macos系统(linux系统跳过本步骤):
sed -i .bkp 's/tars_token = ""/tars_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"/g' config.toml
```

扩容配置`config.toml`如下：

```ini
[tars]
tars_url = "http://127.0.0.1:3000"
tars_token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOiJhZG1pbiIsImlhdCI6MTYzODQzMTY1NSwiZXhwIjoxNjY3MjAyODU1fQ.430ni50xWPJXgJdckpOTktJB3kAMNwFdl8w_GIP_3Ls"
tars_pkg_dir = ""

[chain]
chain_id="chain0"
rpc_sm_ssl=false
gateway_sm_ssl=false
# RPC服务的根证书、根证书私钥所在目录
rpc_ca_cert_path="generated/rpc/chain0/ca"
# Gateway服务的根证书、根证书私钥所在目录
gateway_ca_cert_path="generated/gateway/chain0/ca"

[[chain.rpc]]
# 扩容的RPC服务名
name="agencyABcosRpcService"
# 扩容的RPC服务节点部署到的IP
deploy_ip=["172.25.0.5"]
# 指定被扩容的RPC服务的IP，扩容时，会从该IP对应的RPC服务拉取配置文件，若RPC服务已经部署到多台机器上，从中随机选取一个IP作为expanded_ip即可
expanded_ip = "172.25.0.3"
# 扩容的RPC服务监听IP
listen_ip="0.0.0.0"
# RPC服务监听端口
listen_port=10200
thread_count=4
# RPC服务对应的Gateway服务名称
gateway_service_name = "agencyABcosGatewayService"

[[chain.gateway]]
# 扩容的Gateway服务名
name="agencyABcosGatewayService"
# 扩容的Gateway服务节点部署到的IP
deploy_ip=["172.25.0.5"]
# 指定被扩容的Gateway服务的IP，扩容时，会从该IP对应的Gateway服务拉取配置文件，若Gateway服务已经部署到多台机器上，从中随机选取一个IP作为expanded_ip即可
expanded_ip = "172.25.0.3"
# Gateway服务监听IP
listen_ip="0.0.0.0"
# Gateway服务监听端口
listen_port=40300
# 所有Gateway服务节点的连接信息
peers=["172.25.0.3:30300", "172.25.0.3:30301", "172.25.0.5:40300"]
# Gateway服务对应的RPC服务名称
rpc_service_name = "agencyABcosRpcService"
```

## 3. 扩容RPC服务

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 扩容并部署RPC服务
python3 build_chain.py chain -o expand -t rpc

# 使用telnet探测RPC服务是否扩容成功
telnet 127.0.0.1 10200
```

执行上述命令后，当脚本输出`expand service success, type: rpc`时，则说明RPC服务扩容成功，详细日志输出如下：

```shell
=========================================================
----------- expand service, type: rpc -----------
=========================================================
----------- generate service config -----------
* generate service config for 172.25.0.5 : agencyABcosRpcService
* generate config for the rpc service
* generate generated/rpc/chain0/172.25.0.5/agencyABcosRpcService/config.ini.tmp
* generate cert, output path: generated/rpc/chain0/172.25.0.5/agencyABcosRpcService
* generate sdk cert, output path: generated/rpc/chain0/172.25.0.5/agencyABcosRpcService
* generate config for the rpc service success
gen configuration for service agencyABcosRpcService success
----------- generate service config success -----------
=========================================================
----------- begin expand service -----------
expand to 172.25.0.5, app: chain0, name: agencyABcosRpcService
expand_server_preview, app: chain0, server_name: agencyABcosRpcService, expanded_node_list: 172.25.0.5
expand server preview response b'{"data":[{"application":"chain0","server_name":"agencyABcosRpcService","set":"..","obj_name":"RpcServiceObj","node_name":"172.25.0.5","bind_ip":"172.25.0.5","port":0,"template_name":"tars.cpp.default","status":"\xe6\x9c\xaa\xe6\x89\xa9\xe5\xae\xb9","auth":0}],"ret_code":200,"err_msg":""}'
expand_server, app: chain0, server_name: agencyABcosRpcService
expand server response b'{"data":{"tars_node_rst":[]},"ret_code":200,"err_msg":""}'
* add config for service agencyABcosRpcService, node: 172.25.0.5, config: ca.crt
* add config for service agencyABcosRpcService, node: 172.25.0.5, config: ssl.key
* add config for service agencyABcosRpcService, node: 172.25.0.5, config: ssl.crt
* add config for service agencyABcosRpcService, node: 172.25.0.5, config: config.ini
upload tar package generated/./agencyABcosRpcService.tgz success, config id: 19
----------- expand service success, type: rpc -----------
=========================================================
```

扩容过程中生成的RPC服务相关的配置位于`generated/rpc/${chainID}/${deploy_ip}`目录，具体如下：

```shell
$ tree generated/rpc/chain0/172.25.0.5
generated/rpc/chain0/172.25.0.5
└── agencyABcosRpcService
    ├── config.ini.tmp
    ├── sdk
    │   ├── ca.crt
    │   ├── cert.cnf
    │   ├── sdk.crt
    │   └── sdk.key
    └── ssl
        ├── ca.crt
        ├── cert.cnf
        ├── ssl.crt
        └── ssl.key
```

RPC服务扩容成功后，可在tars网页管理平台看到服务`agencyABcosRpcService`的【服务列表中】，多了一个位于`172.25.0.5`IP上的RPC服务：
![](../../../images/tutorial/expand_rpc.png)


## 4. 扩容Gateway服务

```shell
# 进入操作目录
cd ~/fisco/BcosProBuilder

# 扩容并部署RPC服务
python3 build_chain.py chain -o expand -t gateway

# 使用telnet探测RPC服务是否扩容成功
telnet 127.0.0.1 40300
```

执行上述命令后，当脚本输出`expand service success, type: gateway`时，则说明Gateway服务扩容成功，详细日志输出如下：

```shell
=========================================================
----------- expand service, type: gateway -----------
=========================================================
----------- generate service config -----------
* generate service config for 172.25.0.5 : agencyABcosGatewayService
* generate config for the gateway service
* generate generated/gateway/chain0/172.25.0.5/agencyABcosGatewayService/config.ini.tmp
* generate cert, output path: generated/gateway/chain0/172.25.0.5/agencyABcosGatewayService
* generate gateway connection file: generated/gateway/chain0/172.25.0.5/agencyABcosGatewayService/nodes.json.tmp
* generate config for the gateway service success
gen configuration for service agencyABcosGatewayService success
----------- generate service config success -----------
=========================================================
----------- begin expand service -----------
expand to 172.25.0.5, app: chain0, name: agencyABcosGatewayService
expand_server_preview, app: chain0, server_name: agencyABcosGatewayService, expanded_node_list: 172.25.0.5
expand server preview response b'{"data":[{"application":"chain0","server_name":"agencyABcosGatewayService","set":"..","obj_name":"GatewayServiceObj","node_name":"172.25.0.5","bind_ip":"172.25.0.5","port":0,"template_name":"tars.cpp.default","status":"\xe6\x9c\xaa\xe6\x89\xa9\xe5\xae\xb9","auth":0}],"ret_code":200,"err_msg":""}'
expand_server, app: chain0, server_name: agencyABcosGatewayService
expand server response b'{"data":{"tars_node_rst":[]},"ret_code":200,"err_msg":""}'
* add config for service agencyABcosGatewayService, node: 172.25.0.5, config: nodes.json
* add config for service agencyABcosGatewayService, node: 172.25.0.5, config: ca.crt
* add config for service agencyABcosGatewayService, node: 172.25.0.5, config: ssl.key
* add config for service agencyABcosGatewayService, node: 172.25.0.5, config: ssl.crt
* add config for service agencyABcosGatewayService, node: 172.25.0.5, config: config.ini
upload tar package generated/./agencyABcosGatewayService.tgz success, config id: 21
----------- expand service success, type: gateway -----------
=========================================================
```

扩容过程中生成的Gateway服务相关的配置位于`generated/gateway/${chainID}/${deploy_ip}`目录，具体如下：

```shell
$ tree generated/gateway/chain0/172.25.0.5
generated/gateway/chain0/172.25.0.5
└── agencyABcosGatewayService
    ├── config.ini.tmp
    ├── nodes.json.tmp
    └── ssl
        ├── ca.crt
        ├── cert.cnf
        ├── ssl.crt
        └── ssl.key
```

Gateway服务扩容成功后，可在tars网页管理平台看到服务`agencyABcosGatewayService`的【服务列表中】，多了一个位于`172.25.0.5`IP上的Gateway服务：
![](../../../images/tutorial/expand_gateway.png)


## 5. 通过控制台获取新扩容的服务信息

```eval_rst
.. note::
   控制台的配置和部署请参考 `配置并使用控制台 <./installation.html#id6>`_
```

启动控制台，输入`getPeers`命令，控制台显示的Gateway服务节点数目由2个增加为3个。

```shell
# 进入操作目录
$ cd ~/fisco/console

# 启动控制台
$ bash start.sh
=============================================================================================
Welcome to FISCO BCOS console(3.0.0-rc2)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=============================================================================================
[group0]: /> getPeers
PeersInfo{
    p2pNodeID='3082010a0282010100c1d64abf0af11ceaa69b237090a5078ccbc122aedbf93486100ae65cb38cbf2a6969b80f2beca1abba7f0c1674876b332380a4b76387d62445ba8da7190b54850ed8c3fb4d6f6bafbd4744249a55805c0d804db9aa0f105c44c3381de20c763469892fc11a2bc8467c523592c9b2738069d6beb4cfb413f90e0be53205eca1cf3618100c625667f0592fd682aabe9cfbca7f7c53d79eeb5961ed9f144681b32c9fa55fc4d80b5ffbf32a9f71e900bc1c9a92ce0a485bb1003a915f9215bd7c42461cd52d1b2add644e8c1c273aa3668d4a707771b1a99d6bfcbfdf28be5b9c619eefb0c182ea7e666c5753c79499b1959df17ad5bd0996b9d7f0d62aa53d2b450203010001',
    endPoint='0.0.0.0:30301',
    groupNodeIDInfo=[
        NodeIDInfo{
            group='group0',
            nodeIDList=[
                4af0433ac2d2afe305b88e7faae8ea4e94b14c63e78ca93c5c836ece6d0fbcb3d2a476a74ae8fb0a11e9662c0ce9861427c314aea7386cb3b619a4cb21ab227a
            ]
        }
    ],
    peers=[
        PeerInfo{
            p2pNodeID='3082010a0282010100e2e86f2be399129723539792ed329a5392868129e2584bca7207c67fee62c3bbffd997a79c8669394dbf2c6ba3298d63b7a22fadaa01dce033578dbdbcb8156d894bee43a74ae7b7e0d9d9937aa6744f83065ed4e3c26673d16cf4c019f40cb590bbc8ae83069039accb0f1b01f456fc2129cdc51c43138b5f4026ac7e0f6a1d2f7d76e953e42d8721dcf08c63a58ea2735467054f96180db6b73146d0d7bd6e3910f72abe362eb87bdeb93415c2bbbb7e8ab96ffeca5fa84625f9372cb66412698315a3ef5a6f808148038f9211df730e55c24d109379df542bcfccbb022ec0e454a2751045881030ace5f584e60aff9607f11548af10f120b0f46113afbde70203010001',
            endPoint='172.25.0.5:35122',
            groupNodeIDInfo=[
                NodeIDInfo{
                    group='group0',
                    nodeIDList=[
                        8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3
                    ]
                }
            ]
        },
        PeerInfo{
            p2pNodeID='3082010a0282010100cd8978651421e2861330242b1736a1814e4f2654476ae177c81494c52aef4e30821689029bff53a6b8d671967e5ca52d40c821775013f663ce003acd40a037a2cf19ed2bb20b92cb519636aa7402499df8e9324a0120cd199de97330f5942cca54ebfd5abb425f5c9128cd7bfa3956281614d492c9858d24ddea336651926869cde4e329550708b78d657019ee19ab9024305b32ffa96ed81d04be9124dc74d8d320b570b446a0b95af051fecbf3e49a3d64daea8b044ae09e891a89e59e87ca3db65bc67c16b6787f804b19a158e957a7b8d734159677837d2cd28f85cc79559d38f64e757c4fa7246604542c01cec2a3805066c4d026b9a90091b0bf67efe30203010001',
            endPoint='172.25.0.3:30300',
            groupNodeIDInfo=[
                NodeIDInfo{
                    group='group0',
                    nodeIDList=[
                        8230e3ad1e7e929044a4ec8a5aca3c16744338a2fdd2865745aab9eef88f5a5c18b0d912a7a047966d112847d5c79eef46b32f7d9a2818adb601049126d289f3
                    ]
                }
            ]
        }
    ]
}
```
