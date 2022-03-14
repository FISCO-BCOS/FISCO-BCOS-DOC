# 错误提示

标签：``console`` ``控制台配置`` ``命令行交互工具``

---------

```eval_rst
.. important::
    - ``控制台`` 只支持FISCO BCOS 3.0+版本，基于 `Java SDK <../sdk/java_sdk/index.html>`_ 实现。
    - 可通过命令 ``./start.sh --version`` 查看当前控制台版本
```

控制台启动可能遇到的错误:

## `The connection to the server timed out`

连接节点超时,可能原因:

- **节点版本错误:** `console 3.0`只支持FISCO BCOS 3.0+，请确保部署的区块链环境为3.0+版本
- **网络连接失败:** 节点未启动或者配置节点连接信息错误
- **SSL握手失败:** 证书错误或者`SSL`配置错误

对于**网络连接失败**的问题,排查方法如下:

1. 检查节点是否正常启动

    `ps -ef | egrep fisco`

2. 查看控制台配置的节点连接信息是否错误

   控制台配置的节点连接列表,在控制台配置文件`config.toml`:

    ```shell
    [network]
        peers=["127.0.0.1:20200", "127.0.0.1:20201"]    # The peer list to connect
    ```

    连接端口应该为节点监听的**RPC端口**,节点`RPC`端口在节点配置文件`config.ini:``listen_port`字段:

    ```shell
    [rpc]
        listen_ip=0.0.0.0
        listen_port=20200
    ```

3. 检查网络是否连通

   可以使用`ping`、`telnet`等工具判断，若控制台跟节点所在服务器网络不连通，可以让运维人员帮忙解决

对于**SSL握手失败**问题:

1. 检查`sdk`证书是否正确:

    - `Air`版本安装包的`sdk`证书位置: `nodes/IP/sdk`
    - `Pro`版本安装包的`sdk`证书位置: `generated/rpc/chainID/IP/serviceName/sdk`(备注: chainID:链ID,IP:节点IP,serviceName:服务名称,搭建环境时指定)

    将`sdk/*`目录下的证书拷贝至控制台的配置目录`console/conf`

2. 检查`SSL`配置

   控制台的`SSL`配置:

   ```shell
   [cryptoMaterial]
        useSMCrypto = "false"                       # RPC SM crypto type
   ```

   节点的`SSL`配置:

   ```shell
   [rpc]
        ; ssl or sm ssl
        sm_ssl=false
   ```

   两个配置应该保持一致,国密环境时,设置为`true`,非国密环境时,设置为`false`

## `there has no connection available for the group, maybe all connections disconnected or the group does not exist`

控制台使用的群组id不存在, 控制台的启动方式包括两种:

指定群组: `bash start.sh groupId`
默认启动: `bash start.sh`,此时使用的群组id为`config.toml`文件配置的群组:

```shell
[network]   
    defaultGroup="group0"                            # Console default group to connect
```

节点的群组id,查看节点配置文件`config.ini`:

```shell
// config.ini
[chain]
    ; the group id, should never be changed
    group_id=group0
```

确保控制台使用的群组id与节点的群组id对应。
