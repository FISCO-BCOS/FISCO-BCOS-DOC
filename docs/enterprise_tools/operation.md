# 操作手册

## 配置文件夹conf

FISCO BCOS generator的配置文件在./conf文件夹下，配置文件为：群组创世区块配置文件`group_genesis.ini`和生成节点配置文件`node_deployment.ini`。

用户通过对conf文件夹下文件的操作，配置生成节点配置文件夹的具体信息。

### 元数据文件夹meta

FISCO BCOS generator的meta文件夹为元数据文件夹，需要存放`fisco bcos`二进制文件、链证书`ca.crt`、本机构机构证书`agency.crt`、机构私钥节点证书、群组创世区块文件等。

证书的存放格式需要为cert_p2pip_port.crt的格式，如cert_127.0.0.1_30300.crt。

FISCO BCOS generator会根据用户在元数据文件夹下放置的相关证书、conf下的配置文件，生成用户下配置的节点配置文件夹。

### group_genesis.ini

通过修改`group_genesis.ini`的配置，用户在指定目录及meta文件夹下生成新群组创世区块的相关配置，如`group.1.genesis`。

```ini
[group]
group_id=1

[nodes]
node0=127.0.0.1:30300 # 群组创世区块的节点p2p地址
node1=127.0.0.1:30301
node2=127.0.0.1:30302
node3=127.0.0.1:30303
```

```eval_rst
.. important::
    生成群组创世区块时需要节点的证书，如上述配置文件中需要4个节点的证书。分别为：cert_127.0.0.1_30301.crt，cert_127.0.0.1_30302.crt，cert_127.0.0.1_30303.crt和cert_127.0.0.1_30304.crt。
```

### node_deployment.ini

通过修改`node_deployment.ini`的配置，用户可以使用--build_install_package命令在指定文件夹下生成节点不含私钥的节点配置文件夹。用户配置的每个`section[node]`即为用户需要生成的节点配置文件夹.`section[peers]`为需要连接的其他节点p2p信息。

配置文件示例如下：

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

上述配置在执行build命令后会在指定目录下生成名为node_127.0.0.1_30300、node_127.0.0.1_30301的配置文件夹。生成的节点处于群组group1中。

```eval_rst
.. note::
    生成节点配置文件夹时只需要自己节点的证书，如上述操作中，需要的证书为：cert_127.0.0.1_30300.crt和cert_127.0.0.1_30301.crt
```

## 命令详解

FISCO BCOS generator 提供多种节点生成、扩容、群组划分、证书相关操作，简略介绍如下：

| 命令名称 | 基本功能 |
| :-: | :-: |
| create_group_genesis | 指定文件夹 | 在指定文件夹下根据group_genesis.ini和meta下的证书生成群组创世区块文件 |
| build_install_package | 在指定文件夹下生成node_deployment.ini中配置的<br>节点配置文件夹（需要在meta下存放生成节点的证书） |
| generate_all_certificate | 根据node_deployment.ini生成相关节点证书和私钥 |
| generate_*_certificates | 生成相应链、机构、节点、sdk证书及私钥 |
| merge_config | 将两个节点配置文件中的P2P部分合并 |
| deploy_private_key | 将私钥批量导入生成的节点配置文件夹中 |
| add_peers | 将节点连接文件批量导入节点配置文件夹中 |
| add_group | 将群组创世区块批量导入节点配置文件夹中 |
| version | 打印当前版本号 |
| h/help | 帮助命令 |

### create_group_genesis (-c)

|  |  |
| :-: | :-: |
| 命令解释 | 生成群组创世区块 |
| 使用前提 | 用户需配置`group_genesis.ini`，并在meta下配置相关节点的证书  |
| 参数设置 | 指定生成群组节点配置文件夹 |
| 实现功能 | 根据用户在meta下配置的证书，在指定目录生成新群组的配置 |
| 适用场景 | 联盟链中已有节点从新划分群组时 |

操作示例

```bash
$ cp node0/node.crt ./meta/cert_127.0.0.1_3030n.crt
...
$ vim ./conf/group_genesis.ini
$ ./generator --create_group_genesis ~/mydata
```

程序执行完成后，会在~/mydata文件夹下生成mgroup.ini中配置的`group.i.genesis`

用户生成的`group.i.genesis`即为群组的创世区块，即可完成新群组划分操作。

```eval_rst
.. note::
    FISCO BCOS 2.0中每个群组都会有一个群组创世区块。
```

### build_install_package (-b)

|  |  |
| :-: | :-: |
| 命令解释 | 部署新节点及新群组 |
| 使用前提 | 用户需配置`node_deployment.ini`，并指定节点p2p链接文件  |
| 参数设置 | 指定文件夹作为配置文件夹存放路径 |
| 实现功能 | 通过给定群组创世区块、节点证书和节点信息，生成节点配置文件夹 |
| 适用场景 | 生成节点配置文件夹 |

操作示例

```bash
$ vim ./conf/node_deployment.ini
$ ./generator --build_install_package ./peers.txt ~/mydata
```

程序执行完成后，会在~/mydata文件夹下生成多个名为node_hostip_port的文件夹，推送到对应服务器后，拷贝私钥到节点conf下即可启动节点

### generate_chain_certificate

|  |  |
| :-: | :-: |
| 命令解释 | 生成根证书 |
| 使用前提 | 无 |
| 参数设置 | 指定根证书存放文件夹 |
| 实现功能 | 在指定目录生成根证书和私钥 |
| 适用场景 | 用户需要生成自签相关根证书 |

```bash
$ ./generator --generate_chain_certificate ./dir_chain_ca
```

执行完成后用户可以在./dir_chain_ca文件夹下看到根证书`ca.crt` 和私钥`ca.key`。

### generate_agency_certificate

|  |  |
| :-: | :-: |
| 命令解释 | 生成机构证书 |
| 使用前提 | 存在根证书和私钥 |
| 参数设置 | 指定机构证书目录，链证书及私钥存放目录和机构名称 |
| 实现功能 | 在指定目录生成机构证书和私钥 |
| 适用场景 | 用户需要生成自签相关机构证书 |

```bash
$ ./generator --generate_agency_certificate ./dir_agency_ca ./chain_ca_dir The_Agency_Name
```

执行完成后可以在./dir_agency_ca路径下生成名为The_Agency_Name的文件夹，包含相应的机构证书`agency.crt` 和私钥`agency.key`。

### generate_node_certificate

|  |  |
| :-: | :-: |
| 命令解释 | 生成节点证书 |
| 使用前提 | 存在机构证书和私钥 |
| 参数设置 | 指定节点证书目录，机构证书及私钥存放目录和节点名称 |
| 实现功能 | 在指定目录生成节点证书和私钥 |
| 适用场景 | 用户需要生成自签相关节点证书 |

```bash
$ ./generator --generate_node_certificate node_dir(SET) ./agency_dir  node_p2pip_port
```

执行完成后可以在node_dir 路径下生成节点证书`node.crt` 和私钥`node.key`。

### generate_sdk_certificate

|  |  |
| :-: | :-: |
| 命令解释 | 生成SDK证书 |
| 使用前提 | 存在机构证书和私钥 |
| 参数设置 | 指定节点证书目录，机构证书及私钥存放目录和节点名称 |
| 实现功能 | 在指定目录生成SDK证书和私钥 |
| 适用场景 | 用户需要生成自签相关SDK证书 |

```bash
$ ./generator --generate_sdk_certificate ./dir_sdk_ca ./dir_agency_ca
```

执行完成后可以在./dir_sdk_ca路径下生成名为SDK的文件夹，包含相应的SDK证书`node.crt` 和私钥`node.key`。

### generate_all_certificates

|  |  |
| :-: | :-: |
| 命令解释 | 根据node_deployment.ini生成相关证书及私钥 |
| 使用前提 | 无 |
| 参数设置 | 指定节点证书目录 |
| 实现功能 | 根据node_deployment.ini在meta下生成节点私钥、证书，在指定目录生成中需要交换的节点证书、节点p2p连接文件 |
| 适用场景 | 用户交换节点数据时 |

```
$ ./generator --generate_all_certificates ./cert
```

```eval_rst
.. note::
    上述命令会根据meta目录下存放的ca.crt、机构证书agency.crt和机构私钥agency.key生成相应的节点证书。

    - 如果用户缺少上述三个文件，则无法生成节点证书，程序会抛出异常。

```

执行完成后会在./cert文件夹下生成节点的相关证书与私钥，并将节点证书放置于./meta下

### merge_config (-m)

使用--merge_config命令可以合并两个config.ini中的p2p section

如 A目录下的config.ini文件的p2p section为

```ini
[p2p]
listen_ip = 127.0.0.1
listen_port = 30300
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 127.0.0.1:30302
node.3 = 127.0.0.1:30303
```

B目录下的config.ini文件的p2p section为

```ini
[p2p]
listen_ip = 127.0.0.1
listen_port = 30303
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30303
node.2 = 192.167.1.1:30300
node.3 = 192.167.1.1:30301
```
使用此命令后会成为：

```ini
[p2p]
listen_ip = 127.0.0.1
listen_port = 30304
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 192.167.1.1:30302
node.3 = 192.167.1.1:30303
node.4 = 192.167.1.1:30300
node.5 = 192.167.1.1:30301
```

使用示例

```bash
$ ./generator --merge_config ~/mydata/node_A/config.ini  ~/mydata/node_B/config.ini
```

使用成功后会将node_A和node_B的config.ini中p2p section合并与 ~/mydata/node_B/config.ini的文件中

### deploy_private_key (-d)

使用--deploy_private_key可以将路径下名称相同的节点私钥导入到生成好的配置文件夹中。

使用示例:

```bash
$./generator --deploy_private_key ./cert ./data
```

如./cert下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的文件夹，文件夹中有节点私钥文件node.key

./data下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的配置文件夹

执行完成后可以将./cert下的对应的节点私钥导入./data的配置文件夹中

### add_peers (-p)

使用--add_peers可以指定的peers文件导入到生成好的节点配置文件夹中。

使用示例:

```bash
$./generator --add_peers ./meta/peers.txt ./data
```

./data下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的配置文件夹

执行完成后可以将peers文件中的连接信息导入./data下所有节点的配置文件`config.ini`中

### add_group (-a)

使用--add_group可以指定的peers文件导入到生成好的节点配置文件夹中。

使用示例:

```bash
$./generator --add_group ./meta/group.2.genesis ./data
```

./data下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的配置文件夹

执行完成后可以将群组2的连接信息导入./data下所有节点的`conf`文件夹中

### version (-v)

使用--version命令查看当前部署工具的版本号。

```bash
$ ./generator --version
```

### help (-h)

用户可以使用-h或--help命令查看帮助菜单

使用示例：

```bash
$ ./generator -h
usage: generator [-h] [-v] [-b peer_path data_dir] [-c data_dir]
                 [--generate_chain_certificate chain_dir]
                 [--generate_agency_certificate agency_dir chain_dir agency_name]
                 [--generate_node_certificate node_dir agency_dir node_name]
                 [--generate_sdk_certificate sdk_dir agency_dir] [-g]
                 [--generate_all_certificates cert_dir] [-d cert_dir pkg_dir]
                 [-m config.ini config.ini] [-p peers config.ini]
                 [-a group genesis config.ini]
```

## 监控设计

FISCO BCOS generator 生成的节点配置文件夹中提供了内置的监控脚本，用户可以通过对其进行配置，将节点的告警信息发送至指定地址。FISCO BCOS generator会将monitor脚本放置于生成节点配置文件的指定目录下，假设用户指定生成的文件夹名为data，则monitor脚本会在data目录下的monitor文件夹下

使用方式如下：

```
$ cd ./data/monitor
```

用途如下：

1. 监控节点是否存活, 并且可以重新启动挂掉的节点.
2. 获取节点的块高和view信息, 判断节点共识是否正常.
3. 分析最近一分钟的节点日志打印, 收集日志关键错误打印信息, 准实时判断节点的状态.
4. 指定日志文件或者指定时间段, 分析节点的共识消息处理, 出块, 交易数量等信息, 判断节点的健康度. 

### 配置告警服务

用户使用前，首先需要配置告警信息服务，这里以[server酱](http://sc.ftqq.com/3.version)的微信推送为例，可以参考配置[server酱](http://sc.ftqq.com/3.version)

绑定自己的github账号，以及微信后，可以使用本脚本向微信发送告警信息，使用本脚本的-s命令 可以向指定微信发送告警信息

如果用户希望使用其他服务，可以修改monitor.sh中的alarm() {
    # change http server  
}函数，个性化配置为自己需要的服务

### help命令

使用help命令查看脚本使用方式

```bash
$ ./monitor.sh -h
Usage : bash monitor.sh
   -s : send alert to your address
   -m : monitor, statistics.  default : monitor .
   -f : log file to be analyzed.
   -o : dirpath
   -p : name of the monitored program , default is fisco-bcos
   -g : specified the group list to be analized
   -d : log analyze time range. default : 10(min), it should not bigger than max value : 60(min).
   -r : setting alert receiver
   -h : help.
 example :
   bash  monitor.sh -s YourHttpAddr -o nodes -r your_name
   bash  monitor.sh -s YourHttpAddr -m statistics -o nodes -r your_name
   bash  monitor.sh -s YourHttpAddr -m statistics -f node0/log/log_2019021314.log -g 1 2 -r your_name
```

命令解释如下：

- -s 指定告警配置地址，可以配置为告警上报服务的ip
- -m 设定监控模式，可以配置为statistics和monitor两种模式，默认为monitor模式。
- -f 分析节点log
- -o 指定节点路径
- -p 设定监控上报名称，默认为fisco-bcos
- -g 指定监控群组，默认分析所有群组
- -d log分析时间范围，默认10分钟内的log，最大不超过60分钟
- -r 指定上报接收者名称
- -h 帮助命令

### 使用示例

- 使用脚本监控指定路径下节点，发送给接收者Alice：

```bash
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -o alice/nodes -r Alice
```

- 使用脚本统计指定路径下节点信息，发送给接收者Alice

```bash
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -m statistics -o alice/nodes -r Alice
```

- 使用脚本统计指定路径下节点指定log指定群组1和群组2的信息，发送给接收者Alice

```bash
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -m statistics -f node0/log/log_2019021314.log -g 1 2 -o alice/nodes -r Alice
```