# 配置方法

标签：``运维部署工具`` ``配置``

----

FISCO BCOS generator的配置文件在./conf文件夹下，配置文件为：群组创世区块配置文件`group_genesis.ini`和生成节点配置文件`node_deployment.ini`。

用户通过对conf文件夹下文件的操作，配置生成节点配置文件夹的具体信息。

## 元数据文件夹meta

FISCO BCOS generator的meta文件夹为元数据文件夹，需要存放`fisco bcos`二进制文件、链证书`ca.crt`、本机构机构证书`agency.crt`、机构私钥节点证书、群组创世区块文件等。

证书的存放格式需要为cert_p2pip_port.crt的格式，如cert_127.0.0.1_30300.crt。

FISCO BCOS generator会根据用户在元数据文件夹下放置的相关证书、conf下的配置文件，生成用户下配置的节点配置文件夹。

## group_genesis.ini

通过修改`group_genesis.ini`的配置，用户在指定目录及meta文件夹下生成新群组创世区块的相关配置，如`group.1.genesis`。

```ini
[group]
group_id=1

[nodes]
;群组创世区块的节点p2p地址
node0=127.0.0.1:30300
node1=127.0.0.1:30301
node2=127.0.0.1:30302
node3=127.0.0.1:30303
```

```eval_rst
.. important::
    生成群组创世区块时需要节点的证书，如上述配置文件中需要4个节点的证书。分别为：cert_127.0.0.1_30301.crt，cert_127.0.0.1_30302.crt，cert_127.0.0.1_30303.crt和cert_127.0.0.1_30304.crt。
```

## node_deployment.ini

通过修改`node_deployment.ini`的配置，用户可以使用--build_install_package命令在指定文件夹下生成节点不含私钥的节点配置文件夹。用户配置的每个`section[node]`即为用户需要生成的节点配置文件夹.`section[peers]`为需要连接的其他节点p2p信息。

配置文件示例如下：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
channel_ip=0.0.0.0
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
channel_ip=0.0.0.0
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
```

读取节点配置的命令，如生成节点证书和节点配置文件夹等会读取该配置文件。

## 模板文件夹tpl

generator的模板文件夹如下图所示：

```bash
├── config.toml # sdk配置文件模板
├── config.ini # 节点配置文件模板
├── config.ini.gm # 国密节点配置文件模板
├── group.i.genesis # 群组创世区块模板
├── group.i.ini # 群组区块配置模板
├── start.sh  # 节点启动脚本模板
├── start_all.sh # 节点批量启动脚本模板
├── stop.sh # 节点停止脚本模板
└── stop_all.sh # 节点批量停止脚本模板
```

generator在进行如生成节点或群组配置的相关操作时，会根据模板文件夹下的配置文件生成相应的节点配置文件夹/群组配置，用户可以修改模板文件夹下的相关文件，再运行部署相关命令，即可生成自定义节点。

FISCO BCOS配置的相关解释可以参考[FISCO BCOS配置文件](../manual/configuration.md)

## 节点p2p连接文件peers.txt

节点p2p连接文件`peers.txt`为生成节点配置文件夹时指定的**其他机构**的节点连接信息，在使用`build_install_package`命令时，需要指定与本机构节点进行连接的节点p2p连接文件`peers.txt`，生成的本机构节点配置文件夹会根据该文件与其他节点进行通信。

采用`generate_all_certificates`命令的用户会根据在`conf`目录下填写的`node_deployment.ini`生成相应的`peers.txt`，采用其他方式生成证书的用户需要手动生成本机构节点的p2p连接文件并发送给对方，节点p2p连接文件的格式如下所示：

```bash
127.0.0.1:30300
127.0.0.1:30301
```

格式为 对应节点ip:p2p_listen_port

- 当需要与多机构节点通信时，需要将该文件合并