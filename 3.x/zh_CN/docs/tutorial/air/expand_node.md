# 扩容新节点

标签：``Air版区块链网络`` ``扩容``

------

`build_chain.sh`提供了扩容新节点功能，本章在[搭建第一个区块链网络](../../quick_start/air_installation.md)的基础上扩容一个新的区块链节点，帮助用户掌握Air版本FISCO BCOS区块链节点的扩容步骤。

```eval_rst
.. note::
   进行节点扩容操作前，请先参考 `搭建第一个区块链网络 <../../quick_start/air_installation.html>`_ 部署Pro版本区块链。
```

## 1. 准备扩容所需文件

Air版本区块链扩容时，需要提前准备证书和配置文件，用于生成扩容节点配置，需要准备的文件包括：

- **CA证书和CA私钥**: 用于为新扩容的节点颁发节点证书
- **节点配置文件`config.ini`**: 可从已有的节点目录中拷贝。
- **节点创世块配置文件`config.genesis`**: 可从已有的节点目录中拷贝。
- **节点连接配置`nodes.json`**: 配置所有节点连接的IP和端口信息，可从已有的节点目录中拷贝，并加上新节点的IP和端口。

```eval_rst
.. note::
   Air版本区块链节点根证书位于搭链时生成的目录下，可进入搭建节点时生成的文件夹(如：`搭建第一个区块链网络 <../../quick_start/air_installation.html>`_ 生成的节点配置文件夹是`nodes`)，通过 ``find . -name ca`` 查找链的根证书
```
这里以[搭建第一个区块链网络](../quick_start.md)为基础，基于`node0`扩容一个新节点`node4`为例：

```shell
# 进入操作目录(Note: 进行本操作之前，请参考【搭建第一个区块链网络节点】部署一条Air版FISCO BCOS区块链)
$ cd ~/fisco/nodes

# 创建扩容配置存放目录
$ mkdir config

# 拷贝根证书、根证书私钥
$ cp -r nodes/ca config

# 从被扩容节点node0拷贝节点配置文件config.ini，创世块配置文件config.genesis以及节点连接配置文件nodes.json
$ cp nodes/127.0.0.1/node0/config.ini config/
$ cp nodes/127.0.0.1/node0/config.genesis config/
$ cp nodes/127.0.0.1/node0/nodes.json config/nodes.json.tmp

# 设置新节点P2P和RPC监听端口
# macOS系统（设置P2P监听端口为30304，RPC监听端口为20204）
$ sed -i .bkp 's/listen_port=30300/listen_port=30304/g' config/config.ini
$ sed -i .bkp 's/listen_port=20200/listen_port=20204/g' config/config.ini
# linux系统（设置P2P监听端口为30304，RPC监听端口为20204）
$ sed -i 's/listen_port=30300/listen_port=30304/g' config/config.ini
$ sed -i 's/listen_port=20200/listen_port=20204/g' config/config.ini

# 将新节点连接加入到nodes.json
$ sed -e 's/"nodes":\[/"nodes":\["127.0.0.1:30304",/' config/nodes.json.tmp > config/nodes.json
# 确认新节点连接信息: 127.0.0.1:30304加入成功
$ cat config/nodes.json
{"nodes":["127.0.0.1:30304","127.0.0.1:30300","127.0.0.1:30301","127.0.0.1:30302","127.0.0.1:30303"]}
```

## 2. 扩容新节点

```eval_rst
.. note::
   - 请确保机器的 ``30304`` 和 ``20204`` 端口没有被占用
   - 请参考 `搭建第一个区块链网络 <../../quick_start/air_installation.html>`_ 下载建链脚本 ``build_chain.sh``, ``build_chain`` 的使用可参考 `这里 <./build_chain.html>`_
```

**步骤1：生成扩容节点配置**

准备好配置文件后，使用建链脚本`build_chain.sh`扩容新节点node4：

```shell
# 进入操作目录
cd ~/fisco

# 调用build_chain.sh扩容节点，新节点扩容到nodes/127.0.0.1/node4目录
# -c: 指定扩容配置config.ini, config.genesis和nodes.json路径
# -d: 指定CA证书和私钥的路径
# -o: 指定扩容节点配置所在目录
bash build_chain.sh -C expand -c config -d config/ca -o nodes/127.0.0.1/node4
```
当节点输出`All completed. Files in nodes/127.0.0.1/node4`说明生成扩容配置成功，输出的日志如下：

```shell
[INFO] Use binary bin/fisco-bcos
[INFO] generate_node_scripts ...
[INFO] generate_node_scripts success...
[INFO] generate_node_cert ...
[INFO] Generate nodes/127.0.0.1/node4/conf cert successful!
[INFO] generate_node_cert success...
[INFO] generate_node_account ...
[INFO] generate_node_account success...
[INFO] copy configurations ...
[INFO] copy configurations success...
==============================================================
[INFO] fisco-bcos Path       : .bin/fisco-bcos
[INFO] sdk dir         : nodes/127.0.0.1/sdk
[INFO] SM Model         : false
[INFO] output dir         : nodes/127.0.0.1/node4
[INFO] All completed. Files in nodes/127.0.0.1/node4
```

扩容节点node4目录如下：

```shell
$ tree nodes/127.0.0.1/node4
nodes/127.0.0.1/node4
├── conf
│   ├── ca.crt
│   ├── cert.cnf
│   ├── node.nodeid
│   ├── node.pem
│   ├── ssl.crt
│   └── ssl.key
├── config.genesis
├── config.ini
├── nodes.json
├── start.sh
└── stop.sh
```

**步骤2：启动扩容节点**

```shell
bash nodes/127.0.0.1/node4/start.sh
```

## 3. 将扩容节点加入为观察节点

```eval_rst
.. note::
   - 执行本步骤前请启动包括扩容节点在内的所有节点 
   - 请参考 `搭建第一个区块链网络的【配置和使用控制台】 <../../quick_start/air_installation.html#id7>`_ 下载控制台
```

**步骤1：检查是否所有节点均启动**

```shell
fisco        79637   4.5  0.1  4979692  19072 s005  S     6:22下午   0:11.49 /home/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini -g config.genesis
fisco        79695   4.4  0.1  4979692  19080 s005  S     6:22下午   0:11.56 /home/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini -g config.genesis
fisco        79671   4.3  0.1  5241836  19192 s005  S     6:22下午   0:11.59 /home/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini -g config.genesis
fisco        79717   4.3  0.1  4979692  19016 s005  S     6:22下午   0:11.38 /home/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini -g config.genesis
fisco        78968   3.6  0.1  5110764  19116 s005  S     6:16下午   0:21.27 /home/fisco/nodes//127.0.0.1/node4/../fisco-bcos -c config.ini -g config.genesis
```

**步骤2：确定节点NodeID**
```shell
# 进入操作目录
$ cd ~/fisco

# 获取节点的nodeID
$ cat nodes/127.0.0.1/node4/conf/node.nodeid
51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105
```

**步骤3：通过控制台将节点加为观察节点**

```shell
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
# 将扩容节点加入为观察节点
[group0]: /> addObserver 51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105
{
    "code":0,
    "msg":"Success"
}
# 确认节点成功加入观察节点
[group0]: /> getObserverList
[
    51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105
]
```

## 4. 将扩容节点加入为共识节点


```eval_rst
.. note::
   为了保证新节点加入不影响共识，须先将扩容节点加入为观察节点，当扩容节点同步到最新区块时，再将其加入到共识节点。
```

```shell
# 将扩容节点加入为共识节点
[group0]: /> addSealer 51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105 1
{
    "code":0,
    "msg":"Success"
}
# 确认新节点成功加入为共识节点
[group0]: /> getSealerList
[
    Sealer{
        nodeID='e77e78303f1dd1613285560da7b4bcd261d47bdd66762e10791cac529cc372f1f3f61a71cb3cdb14e9fec4a5de2f8defd3c28a2cfd7a9d158d51fdda7c65ca4e',
        weight=1
    },
    Sealer{
        nodeID='78fe081d9b14c60ef3eb49f1c7dcb20689bf548e476009454b1559a44b19affc32fe109510dd5f3e9be59b6b6636e12ca169654820637bb6797d1dc8bce89da1',
        weight=1
    },
    Sealer{
        nodeID='4248f278cef93b5ad83b0fc2eadbca5060c4afe291ea677c4c79fdb51ba85e79af7cf7e6fdc82aba351081431213235f400d572ff5355595b74624cd34d6d11d',
        weight=1
    },
    Sealer{
        nodeID='625f16370e88776b621c686f207c22fa694884af6e54d4586dbcf08dfc97e4d4d929115a99d1849c52ca1ae7be6adf1109f7614856b63466b7ec5879e834f26c',
        weight=1
    },
    Sealer{
        nodeID='51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105',
        weight=1
    }
]

# 部署HelloWorld测试合约
[group0]: /> deploy HelloWorld
transaction hash: 0xbf9f7981cddce678fddde6eb1d5f9bdac5dc4c9161b2d8d56a8bb0ecd55aea62
contract address: 0xC8eAd4B26b2c6Ac14c9fD90d9684c9Bc2cC40085
currentAccount: 0x537149148696c7e6c3449331d77ddfaabc3c7a75

# 确认所有节点都达到了最新区块
[group0]: /> getSyncStatus
SyncStatusInfo{
    isSyncing='false',
    protocolId='null',
    genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
    nodeId='78fe081d9b14c60ef3eb49f1c7dcb20689bf548e476009454b1559a44b19affc32fe109510dd5f3e9be59b6b6636e12ca169654820637bb6797d1dc8bce89da1',
    blockNumber='3',
    latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb',
    knownHighestNumber='3',
    txPoolSize='null',
    peers=[
        PeersInfo{
            nodeId='4248f278cef93b5ad83b0fc2eadbca5060c4afe291ea677c4c79fdb51ba85e79af7cf7e6fdc82aba351081431213235f400d572ff5355595b74624cd34d6d11d',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        },
        PeersInfo{
            nodeId='51f25a366613ec7524910e3750407278be33d1dd6583b35503dff63d2219469bda55ee4c869a6583526ef1924a3143b776e2553bd07494dfc24716ced3638105',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        },
        PeersInfo{
            nodeId='625f16370e88776b621c686f207c22fa694884af6e54d4586dbcf08dfc97e4d4d929115a99d1849c52ca1ae7be6adf1109f7614856b63466b7ec5879e834f26c',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        },
        PeersInfo{
            nodeId='e77e78303f1dd1613285560da7b4bcd261d47bdd66762e10791cac529cc372f1f3f61a71cb3cdb14e9fec4a5de2f8defd3c28a2cfd7a9d158d51fdda7c65ca4e',
            genesisHash='abd2e60531870ce432b382b366f48673cdb454793371bbabaa6e447c94481639',
            blockNumber='3',
            latestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
        }
    ],
    knownLatestHash='48799878c7012407b9376bb1456e65a85a6d481969ea0f5c7ac312ec89882acb'
}
```

至此，通过`build_chain.sh`脚本完成了Air版本FISCO BCOS的扩容操作，`build_chain.sh`脚本使用方法请参考[这里](./build_chain.md)。Pro版本FISCO BCOS的扩容请参考[Pro版本FISCO BCOS节点扩容](../../tutorial/pro/expand_node.md)。