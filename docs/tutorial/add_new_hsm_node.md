# 扩容一个使用密码机的节点

标签：``密码机`` 

----

## 1. 为节点生成证书
每个节点都需要有一套证书来与链上的其他节点建立连接，扩容一个新节点，首先需要为其签发证书。

### 为新节点生成密钥
如果您的节点需要使用密码机/密码卡内部密钥，请从密码机厂商获得密码机的使用手册，使用密码机的管理程序，生成两对SM2密钥。其中一对是签名密钥（假设密钥索引为31），另一对是加密密钥（假设密钥索引为32）。

确保将符合了GMT0018-2012规范的头文件和库文件安装在了动态库默认的搜索路径中。
1. 确保头文件``gmt0018.h``在目录``/usr/include``中，并保证所有用户都有读权限。
2. 请将库文件``libgmt0018.so``放在默认的库搜索路径下，并保证用户具有读和执行权限。如，放在Ubuntu操作系统的``/usr/lib``目录下，放在CentOS操作系统，`/lib64``目录下。

### 为新节点生成证书
1. 获取证书生成脚本

```bash
curl -#LO https://raw.githubusercontent.com/MaggieNgWu/FISCO-BCOS/newoct/tools/gen_gm_hsm_node_cert.sh
```

2. 生成新节点私钥证书
首先请检查您的Agency的目录, 了解Agency所使用的密钥类型。

**使用密码机内部密钥的情况**

如果运行ls命令得到的输出如图，不存在gmagency.key，说明您的Agency使用的是密码机内部密钥。
```bash
ls nodes/gmcert/agencyA-gm 
gmagency.crt  gmagency.srl  gmca.crt  gmcert.cnf  gmsm2.param
```
您需要知道自己的Agency使用的是密码机内部的哪个密钥，密钥类型在您[搭链](./use_hsm.md)时就指定了, 比如：当您搭链时使用的是以下配置，那么你可以确定这个agencyA使用的是内部密钥，密钥索引是21.
```
[CA]
CA internalKey 1  
[agency]
agencyA internalKey 21  
[node]
127.0.0.1 agencyA 1 internalKey 31 32
127.0.0.1 agencyA 1 externalKey 
127.0.0.1 agencyA 1 internalKey 35 36
127.0.0.1 agencyA 1 externalKey
```
在生成新节点私钥证书的过程中，请指定Agency使用的密钥类型为internalKey,密钥索引为21。

**使用外部密钥的情况**

如果得到的输出如图，存在gmagency.key，说明您使用的是外部密钥。
```bash
ls nodes/gmcert/agencyA-gm 
gmagency.crt  gmagency.key  gmagency.srl  gmca.crt  gmcert.cnf  gmsm2.param
```
在生成新节点私钥证书的过程中，Agency采用默认的密钥类型externalKey。

**生成新节点私钥证书**
```bash
# -g 指定机构证书及私钥所在路径
# -t 指定机构使用的是externalKey （外部密钥）还是 internalKey（密码机内部密钥）， 默认外部密钥
# -a 如果Agency使用的是内部密钥，则指定密钥索引。
# -k 节点使用的密钥类型，默认外部密钥。
# -i 节点的密钥索引 e.g. 31,32 
# -X 证书有效时间，默认36500天
# -H 如果使用密码机，则配置好密码机的ip，端口，密码。 e.g. -H 192.168.10.12,10000,XXXXX
# -o 输出到指定文件夹，其中node4/conf中会存在机构agency新签发的证书和私钥
# -s 产生SDK证书
# 成功会输出 All completed 提示

# 请根据您计划的密钥使用情况选择执行下列命令中的一个。
# 当Agency使用外部密钥，节点使用外部密钥。
bash gen_gm_hsm_node_cert.sh -g nodes/gmcert/agencyA-gm -o node4 

# Agency使用外部密钥，节点使用内部密钥
bash gen_gm_hsm_node_cert.sh  -g nodes/gmcert/agencyA-gm -o node4  -k internalKey -i 31,32 -H 192.168.10.12,10000,XXXXX

# Agency使用内部密钥，节点使用外部密钥
bash gen_gm_hsm_node_cert.sh  -g nodes/gmcert/agencyA-gm -t internalKey -a 21 -o node4 -H 192.168.10.12,10000,XXXXX

# Agency和节点都使用密码机中的内部密钥，假设Agency的密钥索引为21, 节点的密钥索引为31和32
bash gen_gm_hsm_node_cert.sh -g nodes/gmcert/agencyA-gm -t internalKey -a 21 -o node4 -k internalKey -i 31,32 -H 192.168.10.12,10000,XXXXX
```

```eval_rst
.. important::
    - 使用这个脚本同样可以生成SDK的证书。在以上命令后面加上 -s 即可。

    bash gen_gm_hsm_node_cert.sh -g nodes/gmcert/agencyA-gm -o sdk -s

    bash gen_gm_hsm_node_cert.sh  -g nodes/gmcert/agencyA-gm -o sdk  -k internalKey -i 31,32 -H 192.168.10.12,10000,XXXXX -s
    
    bash gen_gm_hsm_node_cert.sh  -g nodes/gmcert/agencyA-gm -t internalKey -a 21 -o sdk -H 192.168.10.12,10000,XXXXX -s
    
    bash gen_gm_hsm_node_cert.sh -g nodes/gmcert/agencyA-gm -t internalKey -a 21 -o sdk -k internalKey -i 31,32 -H 192.168.10.12,10000,XXXXX -s
```


### 准备节点配置文件

1. 修改`node4/config.ini`。对于`[rpc]`模块，修改`channel_listen_port=20204`和`jsonrpc_listen_port=8549`；对于`[p2p]`模块，修改`listen_port=30304`并在`node.`中增加自身节点信息；

```
$ vim node4/config.ini
[rpc]
    ;rpc listen ip
    listen_ip=127.0.0.1
    ;channelserver listen port
    channel_listen_port=20204
    ;jsonrpc listen port
    jsonrpc_listen_port=8549
[p2p]
    ;p2p listen ip
    listen_ip=0.0.0.0
    ;p2p listen port
    listen_port=30304
    ;nodes to connect
    node.0=127.0.0.1:30300
    node.1=127.0.0.1:30301
    node.2=127.0.0.1:30302
    node.3=127.0.0.1:30303
    node.4=127.0.0.1:30304
```

2. 节点4拷贝节点1的`node0/conf/group.1.genesis`（内含**群组节点初始列表**）和`node0/conf/group.1.ini`到`node4/conf`目录下，不需改动；
```
cp node0/conf/group.1.genesis node0/conf/group.1.ini node4/conf/
```

3. 执行`node4/start.sh`启动节点；
```
cd node4
export LD_LIBRARY_PATH="${HOME}"/.fisco/swssl/lib
bash start.sh
```

4. 确认node4与其他节点连接已经建立，加入网络操作完成。

```bash
tail -f node4/log/log*  | grep "connected count"
```

```
# 以下日志表明节点node4与其他4个节点建立了连接
info|2020-12-22 20:44:36.113611|[P2P][Service] heartBeat,connected count=4
info|2020-12-22 20:44:46.117942|[P2P][Service] heartBeat,connected count=4
info|2020-12-22 20:44:56.120799|[P2P][Service] heartBeat,connected count=4
```
## 2. 节点加入群组

### 获取node4的nodeid

```bash
cat node4/conf/gmnode.nodeid
```

得到类似下面的字符串就是nodeid

```bash
94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
```

### 使用控制台将node4加入群组1

1. 使用addObserver将node4作为观察节点加入群组1

```bash
[group:1]> getObserverList
[]

[group:1]> addObserver 94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
{
    "code":1,
    "msg":"Success"
}

[group:1]> getObserverList
[
    94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
]
```

2. 使用addSealer将node4作为共识节点加入群组1

```bash
[group:1]> getSealerList
[
    6c41f7e138051a13a220cb186e934398e37700295ff355b87f113704996b3e03750100e16653cda18b5f954d3b7b08d068ca4a9d65cec5a40db980b697ffb699,
    7404cdf7f34f038aba90059ff25dc5f05f538010c55e98976aea6bc954910f34f15a255869751c8fe564bdb0fa1eee8e2db47eeca0fdd1359beaac6adcd37ede,
    a7b856e5b59072c809ea963fa45ede72f7d37561affff989fbede6cd61a40137e2146db205434788e61b89a57f08c614cd283e5e915c23714c2fa685237e8bdb,
    e5ea1e18717418a57f115bf1cea5168250f86e5b77f74dd15d0c4bf3758ca37002059ba2e54131296d1646a62be5faf85e243dac8d33d452acd63e20428b72ed
]

[group:1]> addSealer 94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
{
    "code":1,
    "msg":"Success"
}

[group:1]> getSealerList
[
    6c41f7e138051a13a220cb186e934398e37700295ff355b87f113704996b3e03750100e16653cda18b5f954d3b7b08d068ca4a9d65cec5a40db980b697ffb699,
    7404cdf7f34f038aba90059ff25dc5f05f538010c55e98976aea6bc954910f34f15a255869751c8fe564bdb0fa1eee8e2db47eeca0fdd1359beaac6adcd37ede,
    a7b856e5b59072c809ea963fa45ede72f7d37561affff989fbede6cd61a40137e2146db205434788e61b89a57f08c614cd283e5e915c23714c2fa685237e8bdb,
    e5ea1e18717418a57f115bf1cea5168250f86e5b77f74dd15d0c4bf3758ca37002059ba2e54131296d1646a62be5faf85e243dac8d33d452acd63e20428b72ed,
    94ae60f93ef9a25a93666e0149b7b4cb0e044a61b7dcd1b00096f2bdb17d1c6853fc81a24e037c9d07803fcaf78f768de2ba56a4f729ef91baeadaa55a8ccd6e
]
```

更多操作请参考[节点管理](../manual/node_management.md)