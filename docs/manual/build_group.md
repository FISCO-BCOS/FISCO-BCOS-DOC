# 多群组操作指南

## 创建多群组区块链

FISCO BCOS提供了一键安装脚本[build_chain.sh](./build_chain.md)用于快速生成链配置文件。该脚本使用方法如下(详细可参考[快速建链指南](./build_chain.md))：

```bash
$ bash build_chain.sh -h
Usage:
    -l <IP list>                        [Required] "ip1:nodeNum1,ip2:nodeNum2" e.g:"192.168.0.1:2,192.168.0.2:3"
    -f <IP list file>                   [Optional] split by line, every line should be "ip:nodeNum agencyName groupList". eg "127.0.0.1:4 agency1 1,2"
    -e <FISCO-BCOS binary path>         Default download from GitHub
    -o <Output Dir>                     Default ./nodes/
    -p <Start Port>                     Default 30300
    -i <Host ip>                        Default 127.0.0.1. If set -i, listen 0.0.0.0
    -c <Consensus Algorithm>            Default PBFT. If set -c, use raft
    -s <State type>                     Default mpt. if set -s, use storage 
    -g <Generate guomi nodes>           Default no
    -z <Generate tar packet>            Default no
    -t <Cert config file>               Default auto generate
    -T <Enable debug log>               Default off. If set -T, enable debug log
    -P <PKCS12 passwd>                  Default generate PKCS12 file without passwd, use -P to set custom passwd
    -h Help
e.g 
    build_chain.sh -l "127.0.0.1:4"
```

其中l和f两个选项必须指定一个，脚本默认生成SDK所需的PKCS12文件，RPC默认监听127.0.0.1。

- **l选项**: 指定要生成的链的IP列表以及每个IP下的节点数，以逗号分隔。脚本根据输入的参数生成对应的节点配置文件，其中每个节点的端口号默认从30300开始递增，默认所有节点属于同一个机构和账本。


- **f选项**: 根据配置文件生成节点，相比于l选项支持更多的定制。
下面是一个配置文件的例子，每行表示一台机器节点分布情况和所属群组情况，格式为 **IP:NUM AgencyName GroupList** ，即 ：**机器Ip:节点数目 机构名称 群组列表** ，每个配置项以空格分隔，其中GroupList表示该服务器所属于的账本，以逗号分隔, **AgencyName(机构名称)不能有空格** 。 

例：192.168.0.1:2 agency1 1,2 表示ip为192.168.0.1的节点上有两个节点，属于机构agency1，属于群组1和群组2。

```bash
# 创建配置文件ipList
echo "192.168.0.1:2 agency1 1,2
192.168.0.2:3 agency2 1
192.168.0.3:5 agency3 2,3
192.168.0.4:2 agency2 3" > ipList

# 获取二进制文件，并调用build_chain.sh脚本建链
# 若有源码编译FISCO BCOS需求，请参考[FISCO BCOS源码安装]()
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh)
$ bash build_chain.sh -f ipList -e ../build/bin/fisco-bcos
```


## 配置并启动控制台

为了方便用户操作，FISCO BCOS在web3sdk的基础上封装了控制台，用户可方便地通过控制台执行增删共识节点以控制节点权限，修改区块可打包最大交易数、最大gas限制等系统参数，并可调用FISCO BCOS底层提供的所有RPC接口，详细说明请参考[控制台使用指南](./console.md)。

控制台是对web3sdk的封装，配置控制台前，首先需要部署web3sdk，详细配置可参考[配置控制台](./console.html#id6)

控制台配置完毕后，通过`./web3sdk -c ${group_id}`启动组${group_id}的控制台:

```bash
# 进入web3sdk dist/bin目录(设web3sdk位于/data目录)
$ cd /data/web3sdk/dist

# 设启动组1的控制台
$ bash ./start 1

```


## 节点入网操作


为了保证区块链安全性，FISCO BCOS引入了网络节点、观察者节点和共识节点三类角色，并可通过控制台动态将节点转换成者三类角色，具体可参考[节点入网](./node_access_management.html)

- [游离节点](../design/security_control/node_access_management.md)：仅可与其他节点进行网络通信，无法处理任何请求(包括交易上链、RPC查询请求等)
- [观察者节点](../design/security_control/node_access_management.md)：可从同组节点同步最新区块，可转发交易、处理RPC请求，但不能参加共识流程
- [共识节点](../design/security_control/node_access_management.md)：可以从同组节点同步最新区块、转发交易、处理客户端的RPC请求，参与共识出块

控制台提供了 **AddSealer(am)** 、**AddObserver(ao)** 和 **RemoveNode(rn)** 三类命令将指定节点转换为共识节点、观察者节点和网络节点，并可通过getSealerList(gml)、getObserverList(gol)和getNodeIDList(gnl)查看当前组的共识节点列表、观察者节点列表和网络节点列表。

例：
将指定节点分别转换成组1的共识节点、观察者节点、网络节点，具体操作和验证步骤如下：

```eval_rst
.. important::
    
    转换节点角色后，请确保节点node id存在，节点node id可在节点目录下执行 cat conf/node.node_id获取
```

```bash
# 获取节点node id（设节点目录为~/127.0.0.1/node0）
$ cat ~/127.0.0.1/node0/conf/node.node_id
7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50

# 连接组1的控制台(设web3sdk位于~目录)
$ cd ~/web3sdk/dist

$ bash ./start 1

# 将指定节点转换为共识节点
> addSealer 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
# 查询共识节点列表
> getSealerList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]

# 将指定节点转换为观察者节点
> addObserver 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
# 查询观察者节点列表
> getObserverList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]

# 将指定节点转换为网络节点
> removeNode 7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
# 查询节点列表
> getNodeIDList
[
	7a056eb611a43bae685efd86d4841bc65aefafbf20d8c8f6028031d67af27c36c5767c9c79cff201769ed80ff220b96953da63f92ae83554962dc2922aa0ef50
]
> getSealerList
[]
> getObserverList
[]

```

## 修改系统参数

FISCO BCOS系统目前主要包括如下系统参数(未来会继续扩展其他系统参数)：


```eval_rst
+-----------------+-----------+---------------------------------+
| 系统参数        | 默认值    |             含义                |
+=================+===========+=================================+
| tx_count_limit  | 1000      | 一个区块中可打包的最大交易数目  |
+-----------------+-----------+---------------------------------+
| tx_gas_limit    | 300000000 | 一个区块最大gas限制             |
+-----------------+-----------+---------------------------------+

```

控制台提供setSystemConfigByKey(ssc)命令来修改这些系统参数，getSystemConfigByKey(gsc)命令可查看系统参数的当前值：


```eval_rst
.. important::

    不建议随意修改tx_count_limit和tx_gas_limit，如下情况可修改这些参数：

    - 机器网络或CPU等硬件性能有限：调小tx_count_limit，或降低业务压力
    - 业务逻辑太复杂，执行区块时gas不足：调大tx_gas_limit
```

```bash
# 设置一个区块可打包最大交易数为500
> ssc tx_count_limit 500
# 查询tx_count_limit
> gsc tx_count_limit
[500]

# 设置区块gas限制为400000000
> ssc tx_gas_limit 400000000
> gsc
[400000000]
```

## RPC接口调用

通过控制台可调用所有RPC接口，详细可参考[控制台操作手册](./console.md)
