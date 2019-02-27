# 配置文件

fisco generator的配置文件在./conf文件夹下，共有三个配置文件，`mchain.ini`、`mexpand.ini`和`mgroup.ini`。分别对应新建节点及群组、扩容新节点加入现有群组、节点划分新群组三种操作。

## meta文件夹

meta文件夹下需要存放生成节点的证书，包括`fisco bcos`二进制文件(或者用户指定版本，待讨论)、链证书ca.crt、节点证书。

证书的存放格式需要为cert_p2pip_port.crt的格式，如cert_127.0.0.1_30300.crt。节点证书需要已经拼装好agency.crt。

## mchain.ini

通过修改`mchain.ini`的配置，用户可以使用--build命令在指定文件夹下生成节点不含私钥的安装包。用户配置的每个section[node]即为生成好的链的安装包.

配置文件中字段的含义解释如下：
| | |
| :-: | :-: |
| p2p_ip | 节点之间p2p通信ip |
| rpc_ip | 节点与sdk通信ip |
| p2p_listen_port | 节点之间p2p通信端口 |
| channel_listen_port | sdk与节点通信端口 |
| jsonrpc_listen_port | 节点rpc端口 |

```bash
[node0]
p2p_ip=127.0.0.1 # 节点p2p通信ip
rpc_ip=127.0.0.1 # 节点与sdk通信ip
p2p_listen_port=30300 # 节点通信端口
channel_listen_port=20200 # sdk与节点通信端口
jsonrpc_listen_port=8545 # 节点rpc端口

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546

[node2]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547

[node3]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548

[group]
group_id=1
```

上述配置在执行--build命令后会在指定目录下生成名为node_127.0.0.1_30300、node_127.0.0.1_30301、node_127.0.0.1_30302、node_127.0.0.1_30303的不含节点私钥的安装包。节点处于group1中。

## mexpand.ini

```bash
与`mchain.ini`配置相似，通过修改`mexpand.ini`的配置，用户可以使用--expand命令在指定文件夹下生成节点不含私钥的安装包。用户配置的每个section[node]即为生成好的链的安装包。


[node0]
p2p_ip=127.0.0.1 # 节点p2p通信ip
rpc_ip=127.0.0.1 # 节点与sdk通信ip
p2p_listen_port=30304 # 节点通信端口
channel_listen_port=20205 # sdk与节点通信端口
jsonrpc_listen_port=8549 # 节点rpc端口

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30305
channel_listen_port=20205
jsonrpc_listen_port=8550

[group]
group_id=1
```

上述配置在执行--build命令后会在指定目录下生成名为node_127.0.0.1_30304、node_127.0.0.1_30305的不含节点私钥的安装包。节点包含group1中配置文件，但是不在群组内，需要group1中的节点发交易允许新节点入网。

## mgroup.ini

通过修改`mgourp.ini`的配置，用户可以在指定目录下生成新群组的相关配置，如group.2.ini和group.2.genesis。

```bash
[group]
; group : group index
group_id=2 #群组序号
member0=127.0.0.1:30300 # 新群组成员
member1=127.0.0.1:30302 # 新群组成员
member2=127.0.0.1:30303 # 新群组成员
```
