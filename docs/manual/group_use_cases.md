# 多群组部署

本章主要以星形组网和并行多组组网拓扑为例，指导您了解如下内容：

- 了解如何使用`build_chain.sh`创建多群组区块链安装包；
- 了解`build_chain.sh`创建的多群组区块链安装包目录组织形式；
- 学习如何启动该区块链节点，并通过日志查看各群组共识状态；
- 学习如何向各群组发送交易，并通过日志查看群组出块状态；
- 了解群组内节点管理，包括节点入网、退网等；
- 了解如何新建群组。

```eval_rst
.. important::
    - build_chain.sh适用于开发者和体验者快速搭链使用
    - 搭建企业级业务链，推荐使用 `企业搭链工具 <../enterprise_tools/index.html>`_
```

## 星形拓扑和并行多组

如下图，星形组网拓扑和并行多组组网拓扑是区块链应用中使用较广泛的两种组网方式。

- **星形拓扑**：中心机构节点同时属于多个群组，运行多家机构应用，其他每家机构属于不同群组，运行各自应用；
- **并行多组**：区块链中每个节点均属于多个群组，可用于多方不同业务的横向扩展，或者同一业务的纵向扩展。

![](../../images/group/group.png)

下面以构建八节点星形拓扑和四节点并行多组区块链为例，详细介绍多群组操作方法。

## 安装依赖

部署FISCO BCOS区块链节点前，需安装`openssl, curl`等依赖软件，具体命令如下：

```bash
# CentOS
$ sudo yum install -y openssl curl

# Ubuntu
$ sudo apt install -y openssl curl

# Mac OS
$ brew install openssl curl
```

## 星形拓扑

本章以构建上图所示的**单机、四机构、三群组、八节点的星形组网拓扑**为例，介绍多群组使用方法。

星形区块链组网如下：

- `agencyA`：在`127.0.0.1`上有2个节点，同时属于`group1、group2、group3`；
- `agencyB`：在`127.0.0.1`上有2个节点，属于`group1`；
- `agencyC`：在`127.0.0.1`上有2个节点，属于`group2`；
- `agencyD`：在`127.0.0.1`上有2个节点，属于`group3`。

```eval_rst
.. important::
   - 实际应用场景中，**不建议将多个节点部署在同一台机器**，建议根据 **机器负载** 选择部署节点数目，请参考 `硬件配置 <../manual/configuration.html>`_
   - **星形网络拓扑** 中，核心节点(本例中agencyA节点)属于所有群组，负载较高，**建议单独部署于性能较好的机器**
   - **在不同机器操作时，请将生成的对应IP的文件夹拷贝到对应机器启动，建链操作只需要执行一次！**
```

### 构建星形区块链节点配置文件夹

[build_chain.sh](../manual/build_chain.md)支持任意拓扑多群组区块链构建，可使用该脚本构建星形拓扑区块链节点配置文件夹：

**准备依赖**

- 创建操作目录

```bash
mkdir -p ~/fisco && cd ~/fisco
```

- 获取build_chain.sh脚本
```bash
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.6.0/build_chain.sh && chmod u+x build_chain.sh
```

**生成星形区块链系统配置文件**

```bash
# 生成区块链配置文件ip_list
$ cat > ipconf << EOF
127.0.0.1:2 agencyA 1,2,3
127.0.0.1:2 agencyB 1
127.0.0.1:2 agencyC 2
127.0.0.1:2 agencyD 3
EOF

# 查看配置文件ip_list内容
$ cat ipconf
# 空格分隔的参数分别表示如下含义：
# ip:num: 物理机IP以及物理机上的节点数目
# agency_name: 机构名称
# group_list: 节点所属的群组列表，不同群组以逗号分隔
127.0.0.1:2 agencyA 1,2,3
127.0.0.1:2 agencyB 1
127.0.0.1:2 agencyC 2
127.0.0.1:2 agencyD 3
```

**使用build_chain脚本构建星形区块链节点配置文件夹**

`build_chain`更多参数说明请参考[这里](../manual/build_chain.md)。

```bash
# 根据配置生成星形区块链 需要保证机器的30300~30301，20200~20201，8545~8546端口没有被占用
$ bash build_chain.sh -f ipconf -p 30300,20200,8545
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:2 Agency:agencyA Groups:1,2,3
Processing IP:127.0.0.1 Total:2 Agency:agencyB Groups:1
Processing IP:127.0.0.1 Total:2 Agency:agencyC Groups:2
Processing IP:127.0.0.1 Total:2 Agency:agencyD Groups:3
==============================================================
......此处省略其他输出......
==============================================================
[INFO] FISCO-BCOS Path   : ./bin/fisco-bcos
[INFO] IP List File      : ipconf
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:2 127.0.0.1:2 127.0.0.1:2 127.0.0.1:2
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /home/ubuntu16/fisco/nodes
[INFO] CA Key Path       : /home/ubuntu16/fisco/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /home/ubuntu16/fisco/nodes

# 生成的节点文件如下
nodes
|-- 127.0.0.1
|   |-- fisco-bcos
|   |-- node0
|   |   |-- conf  #节点配置目录
|   |   |   |-- ca.crt
|   |   |   |-- group.1.genesis
|   |   |   |-- group.1.ini
|   |   |   |-- group.2.genesis
|   |   |   |-- group.2.ini
|   |   |   |-- group.3.genesis
|   |   |   |-- group.3.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid # 记录节点Node ID信息
|   |   |-- config.ini #节点配置文件
|   |   |-- start.sh  #节点启动脚本
|   |   `-- stop.sh   #节点停止脚本
|   |-- node1
|   |   |-- conf
......此处省略其他输出......
```

```eval_rst
.. note::
   若生成的区块链节点属于不同物理机，需要将区块链节点拷贝到相应的物理机
```

**启动节点**

节点提供`start_all.sh`和`stop_all.sh`脚本启动和停止节点。

```bash
# 进入节点目录
$ cd ~/fisco/nodes/127.0.0.1

# 启动节点
$ bash start_all.sh

# 查看节点进程
$ ps aux | grep fisco-bcos
ubuntu16         301  0.8  0.0 986644  7452 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node5/../fisco-bcos -c config.ini
ubuntu16         306  0.9  0.0 986644  6928 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node6/../fisco-bcos -c config.ini
ubuntu16         311  0.9  0.0 986644  7184 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node7/../fisco-bcos -c config.ini
ubuntu16      131048  2.1  0.0 1429036 7452 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
ubuntu16      131053  2.1  0.0 1429032 7180 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
ubuntu16      131058  0.8  0.0 986644  7928 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
ubuntu16      131063  0.8  0.0 986644  7452 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
ubuntu16      131068  0.8  0.0 986644  7672 pts/0    Sl   15:21   0:00 /home/ubuntu16/fisco/nodes/127.0.0.1/node4/../fisco-bcos -c config.ini
```

**查看群组共识状态**

不发交易时，共识正常的节点会输出`+++`日志，本例中，`node0`、`node1`同时属于`group1`、`group2`和`group3`；`node2`、`node3`属于`group1`；`node4`、`node5`属于`group2`；`node6`、`node7`属于`group3`，可通过`tail -f node*/log/* | grep "++"`查看各节点是否正常。

```eval_rst
.. important::

    节点正常共识打印 ``+++`` 日志， ``+++`` 日志字段含义：
     - ``g:``：群组ID
     - ``blkNum``：Leader节点产生的新区块高度；
     - ``tx``: 新区块中包含的交易数目；
     - ``nodeIdx``: 本节点索引；
     - ``hash``: 共识节点产生的最新区块哈希。
```

```bash
# 查看node0 group1是否正常共识（Ctrl+c退回命令行）
$ tail -f node0/log/* | grep "g:1.*++"
info|2019-02-11 15:33:09.914042| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=72254a42....

# 查看node0 group2是否正常共识
$ tail -f node0/log/* | grep "g:2.*++"
info|2019-02-11 15:33:31.021697| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=ef59cf17...

# ... 查看node1, node2节点每个群组是否正常可参考以上操作方法...

# 查看node3 group1是否正常共识
$ tail -f node3/log/*| grep "g:1.*++"
info|2019-02-11 15:39:43.927167| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=5e94bf63...

# 查看node5 group2是否正常共识
$ tail -f node5/log/* | grep "g:2.*++"
info|2019-02-11 15:39:42.922510| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=b80a724d...

```

### 配置控制台

控制台通过Web3SDK链接FISCO BCOS节点，实现查询区块链状态、部署调用合约等功能，能够快速获取到所需要的信息。2.6及其以上版本控制台使用手册请参考[这里](../manual/console_of_java_sdk.md), 1.x版本控制台使用手册请参考[这里](../manual/console.md)。

```eval_rst
.. important::
   控制台依赖于Java 8以上版本，Ubuntu 16.04系统安装openjdk 8即可。CentOS请安装Oracle Java 8以上版本。
   如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh`
```

```bash
#回到fisco目录
$ cd ~/fisco

# 获取控制台
$ curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v1.1.0/download_console.sh && bash download_console.sh

# 进入控制台操作目录
$ cd console

# 拷贝group2节点证书到控制台配置目录
$ cp ~/fisco/nodes/127.0.0.1/sdk/* conf/

# 获取node0的channel_listen_port
$ grep "channel_listen_port" ~/fisco/nodes/127.0.0.1/node*/config.ini
/home/ubuntu16/fisco/nodes/127.0.0.1/node0/config.ini:    channel_listen_port=20200
/home/ubuntu16/fisco/nodes/127.0.0.1/node1/config.ini:    channel_listen_port=20201
/home/ubuntu16/fisco/nodes/127.0.0.1/node2/config.ini:    channel_listen_port=20202
/home/ubuntu16/fisco/nodes/127.0.0.1/node3/config.ini:    channel_listen_port=20203
/home/ubuntu16/fisco/nodes/127.0.0.1/node4/config.ini:    channel_listen_port=20204
/home/ubuntu16/fisco/nodes/127.0.0.1/node5/config.ini:    channel_listen_port=20205
/home/ubuntu16/fisco/nodes/127.0.0.1/node6/config.ini:    channel_listen_port=20206
/home/ubuntu16/fisco/nodes/127.0.0.1/node7/config.ini:    channel_listen_port=20207

```

```eval_rst
.. important::
    使用控制台连接节点时，控制台连接的节点必须在控制台配置的组中
```

**创建控制台配置文件`conf/applicationContext.xml`的配置如下**，控制台从node0(`127.0.0.1:20200`)分别接入三个group中，2.6版本控制台指令详细介绍[参考这里](manual/console_of_java_sdk.md)，1.x版本控制台指令详细介绍[参考这里](manual/console.md)。

```xml
cat > ./conf/applicationContext.xml << EOF
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
           xmlns:tx="http://www.springframework.org/schema/tx" xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
         http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
         http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">


        <bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
                <constructor-arg value="0"/> <!-- 0:standard 1:guomi -->
        </bean>

      <bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
        <property name="allChannelConnections">
        <list>
            <bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="1" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20200</value>
                    </list>
                    </property>
            </bean>
            <bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="2" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20200</value>
                    </list>
                    </property>
            </bean>
            <bean id="group3"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="3" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20200</value>
                    </list>
                    </property>
            </bean>
        </list>
        </property>
        </bean>

        <bean id="channelService" class="org.fisco.bcos.channel.client.Service" depends-on="groupChannelConnectionsConfig">
                <property name="groupId" value="1" />
                <property name="orgID" value="fisco" />
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>
</beans>
EOF
```

**启动控制台**

```bash
$ bash start.sh
# 输出下述信息表明启动成功 否则请检查conf/applicationContext.xml中节点端口配置是否正确
=====================================================================================
Welcome to FISCO BCOS console(1.0.3)!
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

=====================================================================================
[group:1]>
```

### 向群组发交易

上节配置了控制台，本节通过控制台向各群组发交易。

```eval_rst
.. important::

   多群组架构中，群组间账本相互独立，向某个群组发交易仅会导致本群组区块高度增加，不会增加其他群组区块高度
```

**控制台发送交易**

```bash
# ... 向group1发交易...
$ [group:1]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# 查看group1当前块高，块高增加为1表明出块正常，否则请检查group1是否共识正常
$ [group:1]> getBlockNumber
1

# ... 向group2发交易...
# 切换到group2
$ [group:1]> switch 2
Switched to group 2.
# 向group2发交易，返回交易哈希表明交易部署成功，否则请检查group2是否共识正常
$ [group:2]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# 查看group2当前块高，块高增加为1表明出块正常，否则请检查group2是否共识正常
$ [group:2]> getBlockNumber
1

# ... 向group3发交易...
# 切换到group3
$ [group:2]> switch 3
Switched to group 3.
# 向group3发交易，返回交易哈希表明交易部署成功
$ [group:3]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# 查看group3当前块高，块高为1表明出块正常，否则请检查group3是否共识正常
$ [group:3]> getBlockNumber
1

# ... 切换到不存在的组4，控制台提示group4不存在，并输出当前的group列表 ...
$ [group:3]> switch 4
Group 4 does not exist. The group list is [1, 2, 3].

# 退出控制台
$ [group:3]> exit
```

**查看日志**

节点出块后，会输出`Report`日志，日志各个字段含义如下：

```eval_rst
.. important::

    节点每出一个新块，会打印一条Report日志，Report日志中各字段含义如下：
     - ``g:``：群组ID
     - ``num``：出块高度；
     - ``sealerIdx``：共识节点索引；
     - ``hash``：区块哈希；
     - ``next``：下一个区块高度；
     - ``tx``：区块包含的交易数；
     - ``nodeIdx``：当前节点索引。
```

```bash
# 进入节点目录
$ cd ~/fisco/nodes/127.0.0.1

# 查看group1出块情况：有新区块产生
$ cat node0/log/* |grep "g:1.*Report"
info|2019-02-11 16:08:45.077484| [g:1][p:264][CONSENSUS][PBFT]^^^^^^^^Report,num=1,sealerIdx=1,hash=9b5487a6...,next=2,tx=1,nodeIdx=2

# 查看group2出块情况：有新区块产生
$ cat node0/log/* |grep "g:2.*Report"
info|2019-02-11 16:11:55.354881| [g:2][p:520][CONSENSUS][PBFT]^^^^^^^^Report,num=1,sealerIdx=0,hash=434b6e07...,next=2,tx=1,nodeIdx=0

# 查看group3出块情况：有新区块产生
$ cat node0/log/* |grep "g:3.*Report"
info|2019-02-11 16:14:33.930978| [g:3][p:776][CONSENSUS][PBFT]^^^^^^^^Report,num=1,sealerIdx=1,hash=3a42fcd1...,next=2,tx=1,nodeIdx=2

```

### 节点加入群组

通过控制台，FISCO BCOS可将指定节点加入到指定群组，也可将节点从指定群组删除，详细介绍请参考[节点准入管理手册](../manual/node_management.md)，控制台配置参考[控制台操作手册](../manual/console.html#id7)。

本章以将node2加入group2为例，介绍如何在已有的群组中，加入新节点。

```eval_rst
.. important::

    新节点加入群组前，请确保：

    - 新加入NodeID存在
    - 群组内节点正常共识：正常共识的节点会输出+++日志
````

**拷贝group2群组配置到node2**

```bash
# 进入节点目录
$ cd ~/fisco/nodes/127.0.0.1

# ... 从node0拷贝group2的配置到node2...
$ cp node0/conf/group.2.* node2/conf

# ...重启node2(重启后请确定节点正常共识)...
$ cd node2 && bash stop.sh && bash start.sh
```

**获取node2的节点ID**

```bash
# 请记住node2的node ID，将node2加入到group2需用到该node ID
$ cat conf/node.nodeid
6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4
```

**通过控制台向group2发送命令，将node2加入到group2**

```bash

# ...回到控制台目录，并启动控制台（直接启动到group2）...
$ cd ~/fisco/console && bash start.sh 2

# ...通过控制台将node2加入为共识节点...
# 1. 查看当前共识节点列表
$ [group:2]> getSealerList
[
    9217e87c6b76184cf70a5a77930ad5886ea68aefbcce1909bdb799e45b520baa53d5bb9a5edddeab94751df179d54d41e6e5b83c338af0a19c0611200b830442,
    227c600c2e52d8ec37aa9f8de8db016ddc1c8a30bb77ec7608b99ee2233480d4c06337d2461e24c26617b6fd53acfa6124ca23a8aa98cb090a675f9b40a9b106,
    7a50b646fcd9ac7dd0b87299f79ccaa2a4b3af875bd0947221ba6dec1c1ba4add7f7f690c95cf3e796296cf4adc989f4c7ae7c8a37f4505229922fb6df13bb9e,
    8b2c4204982d2a2937261e648c20fe80d256dfb47bda27b420e76697897b0b0ebb42c140b4e8bf0f27dfee64c946039739467b073cf60d923a12c4f96d1c7da6
]
# 2. 将node2加入到共识节点
# addSealer后面的参数是上步获取的node ID
$ [group:2]> addSealer 6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4
{
    "code":0,
    "msg":"success"
}
# 3. 查看共识节点列表
$ [group:2]> getSealerList
[
    9217e87c6b76184cf70a5a77930ad5886ea68aefbcce1909bdb799e45b520baa53d5bb9a5edddeab94751df179d54d41e6e5b83c338af0a19c0611200b830442,
    227c600c2e52d8ec37aa9f8de8db016ddc1c8a30bb77ec7608b99ee2233480d4c06337d2461e24c26617b6fd53acfa6124ca23a8aa98cb090a675f9b40a9b106,
    7a50b646fcd9ac7dd0b87299f79ccaa2a4b3af875bd0947221ba6dec1c1ba4add7f7f690c95cf3e796296cf4adc989f4c7ae7c8a37f4505229922fb6df13bb9e,
    8b2c4204982d2a2937261e648c20fe80d256dfb47bda27b420e76697897b0b0ebb42c140b4e8bf0f27dfee64c946039739467b073cf60d923a12c4f96d1c7da6,
    6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4 # 新加入节点
]
# 获取group2当前块高
$ [group:2]> getBlockNumber
2

#... 向group2发交易
# 部署HelloWorld合约，输出合约地址，若合约部署失败，请检查group2共识情况
$ [group:2] deploy HelloWorld
contract address:0xdfdd3ada340d7346c40254600ae4bb7a6cd8e660

# 获取group2当前块高，块高增加为3，若块高不变，请检查group2共识情况
$ [group:2]> getBlockNumber
3

# 退出控制台
$ [group:2]> exit
```

**通过日志查看新加入节点出块情况**

```bash
# 进入节点所在目录
cd ~/fisco/nodes/127.0.0.1
# 查看节点共识情况（Ctrl+c退回命令行）
$ tail -f node2/log/* | grep "g:2.*++"
info|2019-02-11 18:41:31.625599| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=4,tx=0,nodeIdx=1,hash=c8a1ed9c...
......此处省略其他输出......

# 查看node2 group2出块情况：有新区块产生
$ cat node2/log/* | grep "g:2.*Report"
info|2019-02-11 18:53:20.708366| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=3,idx=3,hash=80c98d31...,next=10,tx=1,nodeIdx=1
# node2也Report了块高为3的区块，说明node2已经加入group2
```

#### 停止节点

```bash
# 回到节点目录 && 停止节点
$ cd ~/fisco/nodes/127.0.0.1 && bash stop_all.sh
```


## 并行多组

并行多组区块链搭建方法与星形拓扑区块链搭建方法类似，以搭建四节点两群组并行多链系统为例：

- 群组1：包括四个节点，节点IP均为`127.0.0.1`；
- 群组2：包括四个节点，节点IP均为`127.0.0.1`。

```eval_rst
.. important::
   - 真实应用场景中，**不建议将多个节点部署在同一台机器** ，建议根据 **机器负载** 选择部署节点数目
   - 为演示并行多组扩容流程，这里仅先创建group1
   - 并行多组场景中，节点加入和退出群组操作与星形组网拓扑类似
```

### 构建单群组四节点区块链

> **用build_chain.sh脚本生成单群组四节点区块链节点配置文件夹**

```bash
$ mkdir -p ~/fisco && cd ~/fisco
# 获取build_chain.sh脚本
$ curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.6.0/build_chain.sh && chmod u+x build_chain.sh
# 构建本机单群组四节点区块链(生产环境中，建议每个节点部署在不同物理机上)
$ bash build_chain.sh -l 127.0.0.1:4 -o multi_nodes -p 20000,20100,7545
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 20000 20100 7545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /home/ubuntu16/fisco/multi_nodes
[INFO] CA Key Path       : /home/ubuntu16/fisco/multi_nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /home/ubuntu16/fisco/multi_nodes

```

> **启动所有节点**

```bash
# 进入节点目录
$ cd ~/fisco/multi_nodes/127.0.0.1
$ bash start_all.sh

# 查看进程情况
$ ps aux | grep fisco-bcos
ubuntu16       55028  0.9  0.0 986384  6624 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
ubuntu16       55034  0.8  0.0 986104  6872 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
ubuntu16       55041  0.8  0.0 986384  6584 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
ubuntu16       55047  0.8  0.0 986396  6656 pts/2    Sl   20:59   0:00 /home/ubuntu16/fisco/multi_nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
```

> **查看节点共识情况**

```bash
# 查看node0共识情况（Ctrl+c退回命令行）
$ tail -f node0/log/* | grep "g:1.*++"
info|2019-02-11 20:59:52.065958| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=da72649e...

# 查看node1共识情况
$ tail -f node1/log/* | grep "g:1.*++"
info|2019-02-11 20:59:54.070297| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=0,hash=11c9354d...

# 查看node2共识情况
$ tail -f node2/log/* | grep "g:1.*++"
info|2019-02-11 20:59:55.073124| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=b65cbac8...

# 查看node3共识情况
$ tail -f node3/log/* | grep "g:1.*++"
info|2019-02-11 20:59:53.067702| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=0467e5c4...

```


### 将group2加入区块链

并行多组区块链每个群组的`genesis`配置文件几乎相同，但[group].id不同，为群组号。

```bash
# 进入节点目录
$ cd ~/fisco/multi_nodes/127.0.0.1

# 拷贝group1的配置
$ cp node0/conf/group.1.genesis node0/conf/group.2.genesis
$ cp node0/conf/group.1.ini node0/conf/group.2.ini

# 修改群组ID
$ sed -i "s/id=1/id=2/g"  node0/conf/group.2.genesis
$ cat node0/conf/group.2.genesis | grep "id"
# 已修改到    id=2

# 更新group.2.genesis文件中的共识节点列表，剔除已废弃的共识节点。

# 将配置拷贝到各个节点
$ cp node0/conf/group.2.genesis node1/conf/group.2.genesis
$ cp node0/conf/group.2.genesis node2/conf/group.2.genesis
$ cp node0/conf/group.2.genesis node3/conf/group.2.genesis
$ cp node0/conf/group.2.ini node1/conf/group.2.ini
$ cp node0/conf/group.2.ini node2/conf/group.2.ini
$ cp node0/conf/group.2.ini node3/conf/group.2.ini

# 重启各个节点
$ bash stop_all.sh
$ bash start_all.sh
```

### 查看群组共识情况

```bash
# 查看node0 group2共识情况（Ctrl+c退回命令行）
$ tail -f node0/log/* | grep "g:2.*++"
info|2019-02-11 21:13:28.541596| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=2,hash=f3562664...

# 查看node1 group2共识情况
$ tail -f node1/log/* | grep "g:2.*++"
info|2019-02-11 21:13:30.546011| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=0,hash=4b17e74f...

# 查看node2 group2共识情况
$ tail -f node2/log/* | grep "g:2.*++"
info|2019-02-11 21:13:59.653615| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=1,hash=90cbd225...

# 查看node3 group2共识情况
$ tail -f node3/log/* | grep "g:2.*++"
info|2019-02-11 21:14:01.657428| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,nodeIdx=3,hash=d7dcb462...

```

### 向群组发交易

**获取控制台**

```bash
# 若从未下载控制台，请进行下面操作下载控制台，否则将控制台拷贝到~/fisco目录：
$ cd ~/fisco
# 获取控制台
$ curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v1.1.0/download_console.sh && bash download_console.sh
```

**配置控制台**

```bash
# 获取channel_port
$ grep "channel_listen_port" multi_nodes/127.0.0.1/node0/config.ini
multi_nodes/127.0.0.1/node0/config.ini:    channel_listen_port=20100

# 进入控制台目录
$ cd console
# 拷贝节点证书
$ cp ~/fisco/multi_nodes/127.0.0.1/sdk/* conf

```

**创建控制台配置文件`conf/applicationContext.xml`的配置如下，在node0（`127.0.0.1:20100`）上配置了两个group（group1和group2）：**

```xml
cat > ./conf/applicationContext.xml << EOF
<?xml version="1.0" encoding="UTF-8" ?>

<beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
           xmlns:tx="http://www.springframework.org/schema/tx" xmlns:aop="http://www.springframework.org/schema/aop"
           xmlns:context="http://www.springframework.org/schema/context"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
         http://www.springframework.org/schema/tx
    http://www.springframework.org/schema/tx/spring-tx-2.5.xsd
         http://www.springframework.org/schema/aop
    http://www.springframework.org/schema/aop/spring-aop-2.5.xsd">


        <bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
                <constructor-arg value="0"/> <!-- 0:standard 1:guomi -->
        </bean>

      <bean id="groupChannelConnectionsConfig" class="org.fisco.bcos.channel.handler.GroupChannelConnectionsConfig">
        <property name="allChannelConnections">
        <list>
            <bean id="group1"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="1" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20100</value>
                    </list>
                    </property>
            </bean>
            <bean id="group2"  class="org.fisco.bcos.channel.handler.ChannelConnections">
                <property name="groupId" value="2" />
                    <property name="connectionsStr">
                    <list>
                    <value>127.0.0.1:20100</value>
                    </list>
                    </property>
            </bean>
        </list>
        </property>
        </bean>

        <bean id="channelService" class="org.fisco.bcos.channel.client.Service" depends-on="groupChannelConnectionsConfig">
                <property name="groupId" value="1" />
                <property name="orgID" value="fisco" />
                <property name="allChannelConnections" ref="groupChannelConnectionsConfig"></property>
        </bean>
</beans>
EOF
```


**通过控制台向群组发交易**

```bash
# ... 启动控制台 ...
$ bash start.sh
# 输出如下信息表明控制台启动成功，若启动失败，请检查是否配置证书、channel listen port配置是否正确
=====================================================================================
Welcome to FISCO BCOS console(1.0.3)!
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

=====================================================================================
# ... 向group1发交易...
# 获取当前块高
$ [group:1]> getBlockNumber
0
# 向group1部署HelloWorld合约，若部署失败，请检查group1共识是否正常
$ [group:1]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# 获取当前块高，若块高没有增加，请检查group1共识是否正常
$ [group:1]> getBlockNumber
1

# ... 向group2发交易...
# 切换到group2
$ [group:1]> switch 2
Switched to group 2.
# 获取当前块高
$ [group:2]> getBlockNumber
0
# 向group2部署HelloWorld合约
$ [group:2]> deploy HelloWorld
contract address:0x8c17cf316c1063ab6c89df875e96c9f0f5b2f744
# 获取当前块高，若块高没有增加，请检查group2共识是否正常
$ [group:2]> getBlockNumber
1
# 退出控制台
$[group:2]> exit
```

**通过日志查看节点出块状态**

```bash
# 切换到节点目录
$ cd ~/fisco/multi_nodes/127.0.0.1/

# 查看group1出块情况，看到Report了属于group1的块高为1的块
$ cat node0/log/* | grep "g:1.*Report"
info|2019-02-11 21:14:57.216548| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=1,sealerIdx=3,hash=be961c98...,next=2,tx=1,nodeIdx=2

# 查看group2出块情况，看到Report了属于group2的块高为1的块
$ cat node0/log/* | grep "g:2.*Report"
info|2019-02-11 21:15:25.310565| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=1,sealerIdx=3,hash=5d006230...,next=2,tx=1,nodeIdx=2
```

#### 停止节点

```bash
# 回到节点目录 && 停止节点
$ cd ~/fisco/multi_nodes/127.0.0.1 && bash stop_all.sh
```
