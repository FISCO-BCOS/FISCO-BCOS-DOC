# 配置介绍

标签：``Air版区块链网络`` ``配置`` ``config.ini`` ``端口配置`` ``日志级别`` ``配置``

----


FISCO BCOS支持多账本，每条链包括多个独立账本，账本间数据相互隔离，群组间交易处理相互隔离，每个节点包括一个主配置`config.ini`。 FIXME: 去掉group.group_id.genesis、 group.group_id.ini

- `config.ini`：主配置文件，主要配置RPC、P2P、SSL证书、账本配置文件路径、兼容性等信息。

## 主配置文件config.ini

`config.ini`采用`ini`格式，主要包括 **p2p、rpc、cert、chain、security、consensus、executor、storage、txpool和log** 配置项。

```eval_rst
.. important::
    FIXME: jsonrpc_listen_ip/channel_listen_ip字段已经不存在？
    - 云主机的公网IP均为虚拟IP，若listen_ip填写外网IP，会绑定失败，须填写0.0.0.0
    FIXME: Channel字段去掉了？
    - RPC/P2P监听端口必须位于1024-65535范围内，且不能与机器上其他应用监听端口冲突
    - 为便于开发和体验，listen_ip参考配置是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如：内网IP或特定的外网IP
```

### 配置RPC

- FIXME: 只有一个listen_ip？其他字段说明
- `listen_ip`: 监听IP，为方便节点和SDK跨机器部署，默认设置为`0.0.0.0`；

- `listen_port`: 监听端口，默认设置为`20200`;
- `thread_count`: 线程数，默认为 4；
- `sm_ssl`: 使用 ssl 还是 sm ssl，默认为 false；

```eval_rst
.. note::
    FIEME: 注解需要更新
    出于安全性和易用性考虑，v2.3.0版本最新配置将listen_ip拆分成jsonrpc_listen_ip和channel_listen_ip，但仍保留对listen_ip的解析功能：

     - 配置中仅包含listen_ip：RPC和Channel的监听IP均为配置的listen_ip
     - 配置中同时包含listen_ip、channel_listen_ip或jsonrpc_listen_ip：优先解析channel_listen_ip和jsonrpc_listen_ip，没有配置的配置项用listen_ip的值替代
     - v2.6.0 版本开始，RPC 支持 ipv4 和 ipv6
```

RPC配置示例如下：

```ini
FIXME: 是否已经不需要区分ipv4 ipv6
[rpc]
    listen_ip=0.0.0.0
    listen_port=20200
    thread_count=4
    ; ssl or sm ssl
    sm_ssl=false
    ; ssl connection switch, if disable the ssl connection, default: false
    ;disable_ssl=true
```

### 配置P2P

FIXME: 介绍是否要更新
当前版本FISCO BCOS必须在`config.ini`配置中配置连接节点的`IP`和`Port`，P2P相关配置包括：

```eval_rst
.. note::
    - 为便于开发和体验，listen_ip参考配置是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如：内网IP或特定的外网IP
    - v2.6.0 版本开始，P2P 支持 ipv4 和 ipv6 FIXME: 是否区分ipv4 6
```

- `listen_ip`：P2P监听IP，默认设置为`0.0.0.0`；
- `listen_port`：节点P2P监听端口；
- `sm_ssl`: 使用 ssl 还是 sm ssl，默认为 false；
- `nodes_path`：生成节点的文件路径，默认为 `./` 当前文件夹
- `nodes_file`：节点信息json文件的文件名，默认为 nodes.json FIXME: 待确定
v2.6.0 版本开始，P2P 支持 ipv4 和 ipv6 FIXME: 待确定

P2P配置示例如下：

```ini
FIXME: 是否已经不需要区分ipv4 ipv6
[p2p]
    listen_ip=0.0.0.0
    listen_port=30300
    ; ssl or sm ssl
    sm_ssl=false
    nodes_path=./
    nodes_file=nodes.json
```

### 配置证书信息

基于安全考虑，FISCO BCOS节点间采用SSL加密通信，`[cert]`配置SSL连接的证书信息：
- `ca_path`: ca证书文件夹。
- `ca_cert`: ca证书文件路径。
- `node_key`：节点私钥文件，设置SDK是否只能连本机构节点，默认为开启（check_cert_issuer=true）。 FIXME: 描述是否要修改
- `node_cert`：节点证书文件，设置SDK是否只能连本机构节点，默认为开启（check_cert_issuer=true）。 FIXME: 描述是否要修改

```ini
[cert]
    ; directory the certificates located in
    ca_path=./conf
    ; the ca certificate file
    ca_cert=ca.crt
    ; the node private key file
    node_key=ssl.key
    ; the node certificate file
    node_cert=ssl.crt
```

`[security]` 配置私钥文件路径：
- `private_key_path`：私钥文件路径：

```ini
[security]
    private_key_path=conf/node.pem
```

### 配置chain链信息

`[chain]`配置节点的链信息，该配置下的字段信息，一旦确定就不应该再更改：
- `sm_crypto`: 是否使用SM国密加密，默认为false。
- `group_id`：群组ID。
- `chain_id`：链ID。

```ini
[chain]
    ; use SM crypto or not, should nerver be changed
    sm_crypto=false
    ; the group id, should nerver be changed
    group_id=group
    ; the chain id, should nerver be changed
    chain_id=chain
```

### 配置共识信息

`[consensus]`配置节点的链信息，该配置下的字段信息，一旦确定就不应该再更改：
- `min_seal_time`: 最小的区块生成时间，默认为500ms。 FIXME: 描述

```ini
[consensus]
    ; min block generation time(ms)
    min_seal_time=500
```

### 配置executor信息

`[executor]`配置执行器信息：
- `is_wasm`: 是否使用wasm虚拟机，默认为false。 FIXME: 描述
- `is_auth_check`: 是否开启权限检查，默认为false。
- `auth_admin_account`: FIXME: 描述

```ini
[executor]
    ; use the wasm virtual machine or not
    is_wasm=false
    is_auth_check=false
    auth_admin_account=
```

### 配置storage信息

`[storage]`配置存储信息：
- `data_path`: 存储数据的文件路径，默认为data。 FIXME: 描述

```ini
[storage]
    data_path=data
```

### 配置交易池信息

`[txpool]`配置交易池信息：为防止过多交易堆积在交易池内占用太多内存，FISCO BCOS提供了`[tx_pool].limit`来限制交易池容量
- `limit`: 限制交易池内可以容纳的最大交易数目，默认为`150000`，超过该限制后，客户端发到节点的交易会被拒绝。 FIXME: 描述
- `notify_worker_num`: 通知线程数量，默认为2。 FIXME: 描述
- `verify_worker_num`: 确认线程数量，默认为2。 FIXME: 描述

```ini
[txpool]
    limit=15000
    notify_worker_num=2
    verify_worker_num=2
```

### 配置日志信息

FISCO BCOS支持功能强大的[boostlog](https://www.boost.org/doc/libs/1_63_0/libs/log/doc/html/index.html)，日志配置主要位于`config.ini`的`[log]`配置项中。

#### 日志通用配置

FISCO BCOS通用日志配置项如下：

- `enable`: 启用/禁用日志，设置为`true`表示启用日志；设置为`false`表示禁用日志，**默认设置为true，性能测试可将该选项设置为`false`，降低打印日志对测试结果的影响**
- `log_path`:日志文件路径。
- `stat_flush_interval`: 由于网络统计日志周期性输出，引入了`log.stat_flush_interval`来控制统计间隔和日志输出频率，单位是秒，默认为60s
- `level`: 日志级别，当前主要包括`trace`、`debug`、`info`、`warning`、`error`五种日志级别，设置某种日志级别后，日志文件中会输大于等于该级别的日志，日志级别从大到小排序`error > warning > info > debug > trace`。
- `max_log_file_size`：每个日志文件最大容量，**计量单位为MB，默认为200MB**。

boostlog示例配置如下：

```ini
[log]
    enable=true
    log_path=./log
    ; network statistics interval, unit is second, default is 60s
    stat_flush_interval=60
    ; info debug trace
    level=DEBUG
    ; MB
    max_log_file_size=200
```