# 扩容RPC/Gateway服务（不依赖tars管理台）

标签：``Pro版区块链网络`` ``扩容RPC服务`` ``扩容网关Gateway服务`` ``不依赖tars网页管理台``

------------

本章通过单机扩容Pro版本FISCO BCOS联盟链的RPC/Gateway服务为例，帮助用户掌握不依赖tars管理台，进行Pro版本FISCO BCOS区块链的服务扩容。

```eval_rst
.. note::
   进行RPC扩容操作前，请先参考 `搭建不依赖tars网页管理台版的Pro版区块链网络 <./installation_without_tars.html>`_ 部署Pro版本区块链。
```

## 1. 修改扩容配置

区块链节点服务的扩容配置可参考`BcosBuilder`的扩容模板`conf/config-node-rpc-example.toml`，具体配置步骤如下：

```shell
# 进入操作目录
cd ~/fisco/BcosBuilder
# 扩容配置
cp conf/config-build-expand-rpc.toml config-expand-rpc.toml
```

```shell
cat config-expand-rpc.toml
[tars]
tars_pkg_dir = "binary/"

[chain]
chain_id="chain0"
# the rpc-service enable sm-ssl or not, default disable sm-ssl
rpc_sm_ssl=false
# the gateway-service enable sm-ssl or not, default disable sm-ssm
gateway_sm_ssl=false
# the existed rpc service ca path, will generate new ca if not configured
rpc_ca_cert_path=""
# the existed gateway service ca path, will generate new ca if not configured
#gateway_ca_cert_path="

[[agency]]
name = "agencyA"
# enable data disk encryption for rpc/gateway or not, default is false
enable_storage_security = false
# url of the key center, in format of ip:port, please refer to https://github.com/FISCO-BCOS/key-manager for details
# key_center_url =
# cipher_data_key =

    [agency.rpc]
    deploy_ip=["127.0.0.1"]
    # rpc listen ip
    listen_ip="0.0.0.0"
    # rpc listen port
    listen_port=20200
    thread_count=4
    # rpc tars server listen ip
    tars_listen_ip="0.0.0.0"
    # rpc tars server listen port
    tars_listen_port=40400
```

根据需要修改配置文件:

- RPC根证书路径

```shell
rpc_ca_cert_path="generated/rpc/chain0/ca/"
```

- 部署服务器修改

  ```shell
  deploy_ip = "127.0.0.1"
  ```

- 修改rpc监听信息

  ```shell
    listen_ip="0.0.0.0"
    listen_port=20202
  ```

- tars 监听信息修改

  ```shell
  tars_listen_ip="0.0.0.0"
  tars_listen_port=40420
  ```

## 2. 生成安装包

配置修改完成之后，使用如下命令生成安装包

```shell
python3 build_chain.py build -c config-expand-rpc.toml -t rpc -O ./expand/rpc
```

执行上述命令后，输出`* build tars install package output dir : ./expand/rpc`并且无其他报错时，安装包构建成功

```shell
$ python3 build_chain.py build -c config-expand-rpc.toml -t rpc -O ./expand/rpc
=========================================================
* output dir: ./expand/rpc
* Don't load tars token and url
* args type: rpc
* generate config for the rpc service, build opr: True
----------- * generate config for the rpc service agencyABcosRpcService -----------
* generate config.ini for the rpc service agencyABcosRpcService
* store ./expand/rpc/127.0.0.1/rpc_20200/conf/config.ini
* generate config.ini for the rpc service agencyABcosRpcService success
* generate cert for the rpc service agencyABcosRpcService
* generate cert, ip: 127.0.0.1, output path: ./expand/rpc/127.0.0.1/rpc_20200/conf
* generate sdk cert, output path: ./expand/rpc/127.0.0.1/rpc_20200/conf
* generate cert for the rpc service agencyABcosRpcService success
----------- * generate config for the rpc service successagencyABcosRpcService -----------
* generate tars install package for BcosRpcService:agencyABcosRpcService:agencyA:chain0:rpc:binary/
* generate config for the rpc service success
* copy tars_proxy.ini: ./expand/rpc/chain0/agencyA_tars_proxy.ini ,dir: ./expand/rpc/127.0.0.1/rpc_20200/conf
=========================================================
* build tars install package output dir : ./expand/rpc
```

生成的安装包`./expand/rpc/`

```shell
expand/rpc
├── 127.0.0.1 
│   ├── rpc_20202         # RPC服务目录
│   │   ├── BcosRpcService      # 可执行程序
│   │   ├── conf                # 配置目录
│   │   │   ├── ca.crt          # RPC根证书
│   │   │   ├── cert.cnf        # 证书配置文件
│   │   │   ├── config.ini      # 配置文件
│   │   │   ├── sdk             # SDK证书目录，rpc与sdk之间的ssl连接使用，sdk连接rpc服务时需要拷贝这些文件
│   │   │   │   ├── ca.crt
│   │   │   │   ├── cert.cnf
│   │   │   │   ├── sdk.crt
│   │   │   │   ├── sdk.key
│   │   │   │   └── sdk.nodeid
│   │   │   ├── ssl.crt         # ssl证书，用于rpc与sdk的网络连接
│   │   │   ├── ssl.key         # ssl证书私钥
│   │   │   ├── ssl.nodeid
│   │   │   ├── tars.conf       # tars服务端配置，参考tars.conf配置说明
│   │   │   └── tars_proxy.ini  # tars客户端连接配置，参考tars_proxy.ini配置说明
│   │   ├── start.sh    # 启动脚本
│   │   └── stop.sh     # 停止脚本
│   ├── start_all.sh
│   └── stop_all.sh
└── chain0
    └── agencyA_tars_proxy.ini # 新生成tars客户端连接配置
```

## 3. 合并tars_proxy.ini文件

使用`merge-config`命令 合并tars_proxy文件

```shell
python3 build_chain.py merge-config --help
usage: build_chain.py merge-config [-h] -t TYPE -c CONFIG [CONFIG ...] -O OUTPUT

e.g:
python3 build_chain.py merge-config -t tars -c tars0.conf tars1.conf -O output_dir

options:
  -h, --help            show this help message and exit
  -t TYPE, --type TYPE  [Required] specify the type:
                        * type list: tars
  -c CONFIG [CONFIG ...], --config CONFIG [CONFIG ...]
                        [Required] the config files to be
  -O OUTPUT, --output OUTPUT
                        [Required] specify the output dir
```

-t/--type   : 合并的配置文件类型，目前只支持`tars`类型
-c/--config : 配置列表，需要合并的配置文件列表
-O/--output : 输出目录

```shell
python3 build_chain.py merge-config -t tars -c generated/chain0/agencyA_tars_proxy.ini expand/rpc/chain0/agencyA_tars_proxy.ini -O agencyA_tars_proxy
```

```shell
cat agencyA_tars_proxy/tars_proxy.ini
[gateway]
proxy.0 = 127.0.0.1:40401

[rpc]
proxy.0 = 127.0.0.1:40400
proxy.1 = 127.0.0.1:40420

[txpool]
proxy.0 = 127.0.0.1:40402
proxy.1 = 127.0.0.1:40422

[scheduler]
proxy.0 = 127.0.0.1:40403
proxy.1 = 127.0.0.1:40423

[pbft]
proxy.0 = 127.0.0.1:40404
proxy.1 = 127.0.0.1:40424

[ledger]
proxy.0 = 127.0.0.1:40405
proxy.1 = 127.0.0.1:40425

[front]
proxy.0 = 127.0.0.1:40406
proxy.1 = 127.0.0.1:40426
```

操作成功之后，所有的tars连接信息会合并入同一个文件

## 4. 更新tars_proxy并重启服务

更新机构A所有的服务的tars_proxy.ini文件

包括已有的服务以及扩容的服务。

```shell
cp -f agencyA_tars_proxy/tars_proxy.ini generated/127.0.0.1/gateway_30300/conf/
cp -f agencyA_tars_proxy/tars_proxy.ini generated/127.0.0.1/rpc_20200/conf
cp -f agencyA_tars_proxy/tars_proxy.ini generated/127.0.0.1/group0_node_40402/conf
cp -f agencyA_tars_proxy/tars_proxy.ini expand/rpc/127.0.0.1/rpc_20201/conf # 扩容的服务
```

重启服务

```shell
$ cd ~/fisco/BcosBuilder/pro/
$ bash generated/127.0.0.1/stop_all.sh
try to stop gateway_30300
 stop BcosGatewayService success.
try to stop gateway_30301
 stop BcosGatewayService success.
try to stop group0_node_40402
 stop BcosNodeService success.
try to stop group0_node_40412
 stop BcosNodeService success.
try to stop rpc_20200
 stop BcosRpcService success.
try to stop rpc_20201
 stop BcosRpcService success.
```

```shell
$ cd ~/fisco/BcosBuilder/pro/
$ bash generated/127.0.0.1/start_all.sh
try to start gateway_30300
try to start gateway_30301
try to start group0_node_40402
try to start group0_node_40412
try to start rpc_20200
try to start rpc_20201
 gateway_30300 start successfully pid=75226
 group0_node_40402 start successfully pid=75235
 rpc_20200 start successfully pid=75238
 group0_node_40412 start successfully pid=75237
 gateway_30301 start successfully pid=75230
 rpc_20201 start successfully pid=75239
```

启动扩容节点

```shell
$ cd ~/fisco/BcosBuilder/pro/
bash expand/rpc/127.0.0.1/start_all.sh
try to start rpc_20202
 rpc_20202 start successfully pid=75529
```

## 5. 通过控制台连接扩容的RPC服务

### 5.1 安装依赖

```eval_rst
.. note::
   - 控制台的配置方法和命令请参考 `这里 <../../operation_and_maintenance/console/console_config.html>`_
```

使用控制台之前，需先安装java环境：

```shell
# ubuntu系统安装java
sudo apt install -y default-jdk

#centos系统安装java
sudo yum install -y java java-devel
```

### 5.2 下载、配置并使用控制台

**步骤1：下载控制台**

```shell
cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh && bash download_console.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `cd ~/fisco && curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh`
```

**步骤2：配置控制台**

- 拷贝控制台配置文件

将端口替换为扩容的RPC服务的端口

```shell
# 最新版本控制台使用如下命令拷贝配置文件
cp -n console/conf/config-example.toml console/conf/config.toml
```

```shell
peers=["127.0.0.1:20202"]
```

- 配置控制台证书

```shell
# 可通过命令 find . -name sdk找到所有SDK证书路径
cp ~/fisco/BcosBuilder/pro/expand/rpc/127.0.0.1/rpc_20202/conf/sdk/* console/conf
```

**步骤3：启动并使用控制台**

```shell
cd ~/fisco/console && bash start.sh
```

**步骤4: 调用控制台**

```shell
[group0]: /apps> deploy HelloWorld # 部署合约
transaction hash: 0x98bd489a77f9531bc4ccade0a72c6cff6aa0ca1205d6e5fe391b2cc150443277
contract address: 0x33e56a083e135936c1144960a708c43a661706c0
currentAccount: 0x3e00116eaf82e440ef93da1ecc510471cb3c97de
[group0]: /apps> call HelloWorld 0x33e56a083e135936c1144960a708c43a661706c0 set "Hello,Fisco" #调用合约
transaction hash: 0x30ac4d27c35fd3d5ca0f009ad195ec4c98e90bf72503879e7860e6a508acd614
---------------------------------------------------------------------------------------------
transaction status: 0
description: transaction executed successfully
---------------------------------------------------------------------------------------------
Receipt message: Success
Return message: Success
Return value size:0
Return types: ()
Return values:()
---------------------------------------------------------------------------------------------
```