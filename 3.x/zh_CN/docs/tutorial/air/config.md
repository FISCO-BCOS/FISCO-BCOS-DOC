# 配置介绍

标签：``Air版区块链网络`` ``配置`` ``config.ini`` ``端口配置`` ``日志级别`` ``配置``

----


FISCO BCOS支持多账本，每条链包括多个独立账本，账本间数据相互隔离，群组间交易处理相互隔离，每个节点包括一个主配置`config.ini`。

- `config.ini`：主配置文件，主要配置RPC、P2P、SSL证书、账本配置文件路径、兼容性等信息。

## 主配置文件config.ini

`config.ini`采用`ini`格式，主要包括 **p2p、rpc、cert、chain、security、consensus、executor、storage、txpool和log** 配置项。

```eval_rst
.. important::
    - 云主机的公网IP均为虚拟IP，若listen_ip填写外网IP，会绑定失败，须填写0.0.0.0
    - RPC/P2P监听端口必须位于1024-65535范围内，且不能与机器上其他应用监听端口冲突
    - 为便于开发和体验，listen_ip参考配置是 `0.0.0.0` ，出于安全考虑，请根据实际业务网络情况，修改为安全的监听地址，如：内网IP或特定的外网IP
```

### 配置RPC

- `listen_ip`: 监听IP，为方便节点和SDK跨机器部署，默认设置为`0.0.0.0`；
- `listen_port`: 监听端口，默认设置为`20200`;
- `thread_count`: 线程数，默认为 4；
- `sm_ssl`: 使用 ssl 还是 sm ssl，默认为 false；

RPC配置示例如下：

```ini
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

当前版本FISCO BCOS必须在`config.ini`配置中配置连接节点的`IP`和`Port`，P2P相关配置包括：

- `listen_ip`：P2P监听IP，默认设置为`0.0.0.0`；
- `listen_port`：节点P2P监听端口；
- `sm_ssl`: 使用 ssl 还是 sm ssl，默认为 false；
- `nodes_path`：生成节点的文件路径，默认为 `./` 当前文件夹
- `nodes_file`：节点链接信息文件`nodes.json`所在路径。

P2P配置示例如下：

```ini
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

考虑到PBFT模块打包太快会导致某些区块中仅打包1到2个很少的交易，浪费存储空间，FISCO BCOS在可变配置`config.ini`的`[consensus]`下引入`min_seal_time`配置项来控制PBFT共识打包的最短时间，即：共识节点打包时间超过`min_seal_time`且打包的交易数大于0才会开始共识流程，处理打包生成的新区块。

```eval_rst
.. important::
   - ``min_seal_time`` 默认为500ms
   - ``min_seal_time`` 不可超过出空块时间1000ms，若设置值超过1000ms，系统默认min_seal_time为500ms

```

```ini
[consensus]
    ; min block generation time(ms)
    min_seal_time=500
```

### 配置executor信息

`[executor]`配置执行器信息：
- `is_wasm`: 是否使用wasm虚拟机，当设置为false时启动evm虚拟机，默认为false。
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
- `data_path`: 区块链节点数据存储路径，默认为data。

```ini
[storage]
    data_path=data
```

### 配置交易池信息

`[txpool]`配置交易池信息：为防止过多交易堆积在交易池内占用太多内存，FISCO BCOS提供了`[tx_pool].limit`来限制交易池容量
- `limit`: 限制交易池内可以容纳的最大交易数目，默认为`150000`，超过该限制后，客户端发到节点的交易会被拒绝。
- `notify_worker_num`: 交易通知线程数量，默认为2。
- `verify_worker_num`: 交易验证线程数量，默认为2。

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