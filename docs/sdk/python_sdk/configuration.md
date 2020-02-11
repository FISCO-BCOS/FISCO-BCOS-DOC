# 配置说明

`client_config.py`是Python SDK的配置文件，主要包括**通用配置**，**群组配置**，**通信配置**和**证书配置**。

```eval_rst
.. note::

     - 确保连接端口开放：推荐使用 ``telnet ip port`` 确认客户端与节点网络是否连通
	 - 使用RPC通信协议，不需设置证书
	 - 日志配置参见 ``client/clientlogger.py`` ，默认在 ``bin/logs`` 目录下生成日志，默认级别为DEBUG
```

## 通用配置

- **contract_info_file**: 保存已部署合约信息的文件，默认为`bin/contract.ini`
- **account_keyfile_path**: 存放keystore文件的目录，默认为`bin/accounts`
- **account_keyfile**: keystore文件路径，默认为`pyaccount.keystore`
- **account_password**：keystore文件存储口令，默认为`123456`
- **logdir**：默认日志输出目录，默认为`bin/logs`

## 群组配置

群组配置主要包括链ID和群组ID：

- **fiscoChainId**：链ID，必须与通信节点的一致，默认为1
- **groupid**：群组ID，必须与通信的节点一致，获取节点群组ID请参考[这里](../../manual/configuration.html#id10)，默认为1

## 通信配置

- **client_protocol**：Python SDK与节点通信协议，包括`rpc`和`channel`选项，前者使用JSON-RPC接口访问节点，后者使用Channel访问节点，需要配置证书，默认为`channel`
- **remote_rpcurl**：采用**rpc**通信协议时，节点的rpc IP和端口，参考[这里](../../manual/configuration.html#rpc)获取节点RPC信息，默认为`http://127.0.0.1:8545`，**如采用channel协议，可以留空**
- **channel_host**：采用channel协议时，节点的channel IP地址，参考[这里](../../manual/configuration.html#rpc)获取节点Channel信息，默认为`127.0.0.1`，**如采用rpc协议通信，可以留空**
- **channel_port**：节点的channel 端口，默认为`20200`，**如采用rpc协议通信，可以留空**

## 证书配置

- **channel_ca**：链CA证书，**使用channel协议时设置**，默认为`bin/ca.crt`，
- **channel_node_cert**：节点证书，**使用channel协议时设置**，默认为`bin/sdk.crt`，**如采用rpc协议通信，可以留空**
- **channel_node_key**：Python SDK与节点通信私钥，采用channel协议时须设置，默认为`bin/sdk.key`，**如采用rpc协议通信，这里可以留空**

## solc编译器配置

Python SDK支持使用配置的solc和solcjs编译器自动编译合约，同时配置solc和solcjs时，选择性能较高的solc编译器，编译选项如下：

- **solc_path**：solc编译器路径
- **solcjs_path**：solcjs编译脚本路径，为`./solc.js`

## 配置项示例

**配置项示例如下**：

```python
class client_config:
#类成员变量，便于用.调用和区分命名空间
    PROTOCOL_RPC="rpc" #const,dont change this
    PROTOCOL_CHANNEL="channel" #const,dont change this
    #--------------------------------------
    # configure below
    contract_info_file="bin/contract.ini" #保存已部署合约信息的文件
    account_keyfile_path="bin/accounts" #保存keystore文件的路径，在此路径下,keystore文件以 [name].keystore命名
    account_keyfile ="pyaccount.keystore"
    account_password ="123456" #实际使用时建议改为复杂密码
    fiscoChainId=1 #链ID，和要通信的节点*必须*一致
    groupid = 1  #群组ID，和要通信的节点*必须*一致，如和其他群组通信，修改这一项，或者设置bcosclient.py里对应的成员变量
    logdir="bin/logs" #默认日志输出目录，该目录必须先建立
    #---------client communication config--------------
    client_protocol = "channel"  # or PROTOCOL_CHANNEL to use channel prototol
    #client_protocol = PROTOCOL_CHANNEL
    remote_rpcurl = "http://127.0.0.1:8545"  # 采用rpc通信时，节点的rpc端口,和要通信的节点*必须*一致,**如采用channel协议通信，这里可以留空**
    channel_host="127.0.0.1" #采用channel通信时，节点的channel ip地址,**如采用rpc协议通信，这里可以留空**
    channel_port=20200  ##节点的channel 端口,**如采用rpc协议通信，这里可以留空**
    channel_ca="bin/ca.crt"  #采用channel协议时，需要设置链证书,**如采用rpc协议通信，这里可以留空**
    channel_node_cert="bin/sdk.crt"  # 采用channel协议时，需要设置sdk证书,如采用rpc协议通信，这里可以留空
    channel_node_key="bin/sdk.key"   # 采用channel协议时，需要设置sdk私钥,如采用rpc协议通信，这里可以留空
    # ---------console mode, support user input--------------
    background = True
    # ---------compiler related--------------
    # path of solc compiler
    solc_path = os.environ["HOME"] + "/.py-solc/solc-v0.4.25/bin/solc"
    solcjs_path = "./solcjs"
```
