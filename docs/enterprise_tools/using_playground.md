# 案例分析

```eval_rst
.. important::
    使用前建议优先阅读 `教程-使用企业级部署工具 <../tutorial/enterprise_quick_start.html>`_ 
```

## 组网示例

本节中我们将给出一个常见场景的例子。在本场景中，共有4家机构，分别为A、B、C、D。部署节点如下表所示

| 节点序号 |   P2P地址     |   RPC/channel地址     |   所属机构     |   所在群组     |
| :-----------: | :-------------: | :-------------: | :-------------: | :-------------: |
|   节点0     | 127.0.0.1:30300| 127.0.0.1:8545/:20200 | 机构A | 群组1、2、3 |
|   节点1     | 127.0.0.1:30301| 127.0.0.1:8546/:20201 | 机构B | 群组1 |
|   节点2     | 127.0.0.1:30302| 127.0.0.1:8547/:20202 | 机构C | 群组2 |
|   节点3     | 127.0.0.1:30303| 127.0.0.1:8548/:20203 | 机构D | 群组3 |

![](../../images/enterprise/star.png)

机构A、B、C、D分别维护一个节点，机构A的节点0同时处于三个群组中，接下来我们将讲解如何对等地部署上述模式的多群组联盟链。

### 证书协商

#### 机构获取证书资格

1. 机构A、B、C、D在本机生成使用`agency.key`。

2. 机构A、B、C、D向第三方权威机构申请联盟链组网资格，由第三方权威机构生成根证书`ca.crt`，得到机构证书`agency.crt`

#### 协商节点证书

1. 机构A、B、C、D分别用自己的机构私钥`agency.key`和机构证书`agency.crt`生成自己节点的证书cert_127.0.0.1_30300.crt，cert_127.0.0.1_30301.crt，cert_127.0.0.1_30302.crt，cert_127.0.0.1_30303.crt。

2. 机构A分别与B、C、D协商节点证书，放置与meta文件夹下

```eval_rst
.. note::
    
    在这种场景中，我们假设A来生成所有群组的创世区块文件。
```

### 机构A、B生成group1

组网流程如下：

![](../../images/enterprise/sample_star_1.png)

1. 机构A将fisco-bcos可执行程序放置于meta文件夹下，修改`group_genesis.ini`，生成group1的创世区块，并将群组创世区块传给机构B

2. 机构A、B分别修改`node_deployment.ini`中的信息，配置为节点0和节点1的信息，此步中`node`为自己机构的节点，`peers`为对应机构的节点连接信息

3. 机构A、B分别使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹

至此，完成机构A、B生成group1的操作

### 机构A、C生成group2

组网流程如下：

![](../../images/enterprise/sample_star_2.png)

1. 机构A修改`group_genesis.ini`，生成group2的创世区块，并将群组创世区块传给机构C

2. 机构A将group.2.genesis和group.2.ini拷贝到节点0配置文件夹的conf目录下，从启节点

3. 机构C将fisco-bcos可执行程序放置于meta文件夹下，修改`node_deployment.ini`中的信息，配置为节点0和节点2的信息

4. 机构C使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹，启动节点

至此，完成机构A、C生成group3的操作

### 机构A、D生成group3

组网流程如下：

![](../../images/enterprise/star.png)

1. 机构A修改`group_genesis.ini`，生成group3的创世区块，并将群组创世区块传给机构D

2. 机构A将group.3.genesis和group.3.ini拷贝到节点0配置文件夹的conf目录下，从启节点

3. 机构D将fisco-bcos可执行程序放置于meta文件夹下，修改`node_deployment.ini`中的信息，配置为节点0和节点3的信息

4. 机构D使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹，启动节点

至此，完成机构A、D生成group3的操作

### 配置SDK

完成上述初始组网操作后，机构业务需要完成SDK及控制台的配置，配置文档连接如下：

[配置Web3sdk](../sdk/sdk.md)

[配置控制台](../manual/console.md)

### 机构E新加节点进入group1

在这种场景中，机构E需要扩容一个节点进入group1中，如下表所示

| 节点序号 |   P2P地址     |   RPC/channel地址     |   所属机构     |   所在群组     |
| :-----------: | :-------------: | :-------------: | :-------------: | :-------------: |
|   节点0     | 127.0.0.1:30300| 127.0.0.1:8545/:20200 | 机构A | 群组1、2、3 |
|   节点1     | 127.0.0.1:30301| 127.0.0.1:8546/:20201 | 机构B | 群组1 |
|   节点2     | 127.0.0.1:30302| 127.0.0.1:8547/:20202 | 机构C | 群组2 |
|   节点3     | 127.0.0.1:30303| 127.0.0.1:8548/:20203 | 机构D | 群组3 |
|   节点4     | 127.0.0.1:30304| 127.0.0.1:8549/:20204 | 机构E | 群组1 |

![](../../images/enterprise/sample_star_3.png)

过程如下：

#### 机构E获取证书资格

1. 机构E在本机生成使用`agency.key`

2. 机构E向第三方权威机构申请联盟链组网资格，由第三方权威机构根据group1的根证书`ca.crt`，签发`agency.crt`

#### 机构E生成节点证书

机构E用自己的机构私钥`agency.key`和机构证书`agency.crt`生成自己节点的证书cert_127.0.0.1_30304.crt。

### 机构E生成group1扩容节点配置文件

此时，group1已经存在，组网流程如下：

1. 机构E向机构A或B请求group1配置文件，获取group1中已经运行群组创世区块`group.1.genesis`

2. 机构E将节点4证书，fisco-bcos可执行程序，group1群组创世区块`group.1.genesis`放置于meta文件夹下

3. 机构E修改`node_deployment.ini`中的信息，配置为节点4的信息

4. 机构E使用[build_install_package](](./operation.html#build-install-package-b)命令生成节点4配置文件夹，启动节点

5. 机构E向机构A或B发送节点4入网请求，等待机构A或B使用控制台或SDK发送节点入网交易，注册节点4入网。

至此，完成机构E扩容节点加入group1的操作。

```eval_rst
.. note::

    按照一般业务分析，可以将场景分为以下三种，分别是以下三种场景。
```

## 起始组网

A、B、C三个对等机构需要沟通搭建一条链，开始时只需要一个账本，需要生成节点配置文件，启动节点，初始化一个业务。操作步骤如下：

1. A、B、C三个机构采用链下安全的方式共享自己的节点证书与节点信息（此步可选择由某一机构统一收集，或是所有机构都收集）
2. 假设A收集所有资料后，将证书按照指定格式放在meta目录下，并配置`group_genesis.ini`中的node信息，指定配置文件中的groupid，生成群组创世区块，发送给B、C
3. A、B、C机构修改`node_deployment.ini`的`node`信息，执行[build_install_package](./operation.html#build-install-package-b)命令，指定生成节点配置文件的目录，会在指定目录下生成多个节点配置文件夹
4. 各个机构并将自己的节点配置文件推送至对应服务器，启动节点

## 新节点加入现有group

在本场景中，节点需要与所有原有节点进行网络连接，并且需要加入现有群组，以上述`起始组网`为例，操作步骤如下：
假设D机构需要新加入节点到A、B、C的组网的group1中

1. D机构收集A、B、C组网生成的群组创世区块`group.1.genesis`，放置于meta文件夹下
2. D机构配置`node_deployment.ini`中的`node`信息，执行信息，将扩容节点的证书按照指定格式放在meta目录下
3. D机构使用[build_install_package](](./operation.html#build-install-package-b)命令指定扩容节点配置文件节点输出路径，在输出路径生成扩容节点节点配置文件夹
4. D机构将节点配置文件夹推送至指定服务器，启动节点
5. D机构请求A、B、C中的某一个机构将自己的节点注册入group1中，完成新节点入网操作

## 节点新建群组

在本场景中，链中的已有节点需要建立新群组。

假设节点均已建立为网络链接，需要建立新群组，如上述场景中A、B、C、D要新建立group3，则需要进行的操作如下：

1. A、B、C、D四个机构采用链下安全的方式共享自己的节点证书与节点信息（此步可选择由某一机构统一收集，或是所有机构都收集）
2. A配置`group_genesis.ini`中的信息，将1中协商的证书放置与meta文件夹下
3. A执行[create_group_genesis](./operation.html#create-group-config-c)命令，生成`group.3.genesis`和`group.3.ini`，将生成的配置信息发送给B、C、D
4. A、B、C、D将生成`group.3.genesis`和`group.3.ini`拷贝到已存在的节点文件夹下，从启节点完成新群组的建立

## 组网模式分析

针对上述场景，FISCO BCOS generator将其总结为以下四种组网模式。

设4个节点的地址为，由机构A生成群组创世区块：

| 节点序号 |   P2P地址     |   RPC/channel地址     | 所属机构 |
| :-----------: | :-------------: | :-------------: | :-------------: |
|   节点0     | 127.0.0.1:30300| 127.0.0.1:8545/:20200 | 机构A |
|   节点1     | 127.0.0.1:30301| 127.0.0.1:8546/:20201 | 机构A |
|   节点2     | 127.0.0.1:30302| 127.0.0.1:8547/:20202 | 机构B |
|   节点3     | 127.0.0.1:30303| 127.0.0.1:8548/:20203 | 机构C |

### 简单模式组网

![](../../images/enterprise/simple.png)

简单模式组网可以适用于大多数单一业务的使用场景。整条链的所有节点都在唯一的一个群组中。

当业务的数据需要由区块链所有节点共识时，一般采用此模式组网

使用示例：

用户操作如下：

1.用户生成证书，机构A收集所有证书

2.机构A修改`group_genesis.ini`如下，生成群组创世区块`group.1.genesis`，发送给机构B、C

```ini
[group]
group_id=1

[nodes]
node0=127.0.0.1:30300
node1=127.0.0.1:30301
node2=127.0.0.1:30302
node3=127.0.0.1:30303
```

3.各个机构修改`node_deployment.ini`中的配置

机构A修改如下：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
```

机构B修改如下：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547
```

机构C修改如下：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548
```

3.使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件

4.启动节点

### 网状模式组网

![](../../images/enterprise/net.png)

网状模式组网适合有多个业务的场景，如一条链的不同节点需要有多个需要共识的业务账本。当业务没有交叉时，彼此之间的账本是独立的，可以采用此模式组网。

使用示例：

用户操作如下：

1.用户生成证书，机构A收集所有证书

2.机构A修改`group_genesis.ini`如下，生成群组创世区块`group.1.genesis`，发送给机构B

3.机构A修改`group_genesis.ini`如下，生成群组创世区块`group.2.genesis`，发送给机构B、C

4.各机构修改`node_deployment.ini`中的配置

机构A修改如下：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
```

机构B修改如下：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547
```

机构C修改如下：

```ini
[group]
group_id=2

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548
```

5.各机构使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹，机构A将group.2.genesis导入节点0的conf文件夹，机构B将group.2.genesis导入节点2的conf文件夹

6.启动节点

### 星型模式组网

![](../../images/enterprise/star.png)

星型模式组网适合某一机构与其他机构有较多的交叉，但是其他机构之间通信较少的业务模式。

在这种模式中，节点0需要与其他所有节点交互，而节点1、2、3只需关心与节点0的交互即可

使用示例：

用户操作如下：

1.所有机构生成证书、机构A收集证书

2.机构A修改`group_genesis.ini`如下所示：

```ini
[group]
group_id=1

[nodes]
node0=127.0.0.1:30300
node1=127.0.0.1:30301
```

```ini
[group]
group_id=2

[nodes]
node0=127.0.0.1:30300
node1=127.0.0.1:30302
```

```ini
[group]
group_id=3

[nodes]
node0=127.0.0.1:30300
node1=127.0.0.1:30303
```

生成`group.1.genesis`用于本机构组网，`group.2.genesis`发送给机构B，`group.3.genesis`发送给机构C。

3.各机构分别修改`node_deployment.ini`中的配置如下所示

机构A：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
```

机构B：

```ini
[group]
group_id=2

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547
```

机构C：

```ini
[group]
group_id=2

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548
```

各机构通过上述配置文件，使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹

4.机构A还需将`group.2.genesis`，`group.2.ini`，`group.3.genesis`,`group.2.ini`放置于节点0的conf目录下，启动节点

### 隔离模式组网

![](../../images/enterprise/ring.png)

隔离模式组网适合多个机构之间经常有业务账本隔离需求的场景。

使用示例：

用户操作如下：

1.各机构生成证书，机构A收集证书

2.机构A分别修改`group_genesis.ini`如下所示：

```ini
#群组1配置文件
[group]
group_id=1

[nodes]
node0=127.0.0.1:30300
node2=127.0.0.1:30302
# 群组2配置文件
[group]
group_id=2

[nodes]
node0=127.0.0.1:30301
node2=127.0.0.1:30303
```

3.各机构修改`node_deployment.ini`中的配置如下：

机构A：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546
```

机构B：

```ini
[group]
group_id=1

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547
```

机构C：

```ini
[group]
group_id=2

# Owned nodes
[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548
```

4.机构使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹

5.机构A将节点1中节点创世区块替换为`group.2.genesis`，`group.2.ini`，启动节点

