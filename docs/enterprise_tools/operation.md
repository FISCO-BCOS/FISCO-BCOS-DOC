# 操作手册

## 配置文件夹conf

FISCO BCOS generator的配置文件在./conf文件夹下，共有三个配置文件：`mchain.ini`、`mexpand.ini`和`mgroup.ini`。分别对应新建节点及群组、扩容新节点加入现有群组、节点划分新群组三种操作的配置。

用户通过对conf文件夹下文件的操作，配置生成节点配置文件夹的具体信息。

### 元数据文件夹meta

FISCO BCOS generator的meta文件夹为元数据文件夹，需要存放`fisco bcos`二进制文件、链证书`ca.crt`、节点证书、群组创世区块文件等。

证书的存放格式需要为cert_p2pip_port.crt的格式，如cert_127.0.0.1_30300.crt。节点证书需要已经拼装好`agency.crt`。

FISCO BCOS generator会根据用户在元数据文件夹下放置的相关证书、conf下的配置文件，生成用户下配置的节点配置文件夹。

### mchain.ini

通过修改`mchain.ini`的配置，用户可以使用build命令在指定文件夹下生成节点不含私钥的配置文件夹。用户配置的每个`section[node]`即为生成好的链的节点配置文件夹.

配置文件中字段的含义解释如下：

```ini
[node0]
p2p_ip=127.0.0.1 # 节点之间p2p通信ip
rpc_ip=127.0.0.1 # 节点与SDK通信ip 
p2p_listen_port=30300 # 节点之间p2p通信端口
channel_listen_port=20200 # SDK与节点通信端口
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

上述配置在执行build命令后会在指定目录下生成名为node_127.0.0.1_30300、node_127.0.0.1_30301、node_127.0.0.1_30302、node_127.0.0.1_30303的不含节点私钥的配置文件夹。生成的节点处于群组group1中。

### mexpand.ini

与`mchain.ini`配置相似，通过修改`mexpand.ini`的配置，用户可以使用--expand命令在指定文件夹下生成节点不含私钥的配置文件夹。用户配置的每个section[node]即为生成好的链的节点配置文件夹。

```ini
[group]
group_id=1

[node0]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30304
channel_listen_port=20205
jsonrpc_listen_port=8549

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30305
channel_listen_port=20205
jsonrpc_listen_port=8550

[members]
member0=127.0.0.1:30300 # 已在group1中的节点p2p地址信息
member1=127.0.0.1:30301
member2=127.0.0.1:30302
member3=127.0.0.1:30303
```

上述配置在执行expand命令后会在指定目录下生成名为node_127.0.0.1_30304、node_127.0.0.1_30305的不含节点私钥的配置文件夹。节点包含group1中配置文件，经过group1中的节点发交易后作为新节点加入group1的共识/观察列表中。

### mgroup.ini

通过修改`mgourp.ini`的配置，用户可以在指定目录下生成新群组的相关配置，如`group.2.ini`和`group.2.genesis`。

```ini
[group]
; group : group id
group_id=2 #群组序号
member0=127.0.0.1:30300 # 新群组成员
member1=127.0.0.1:30302 # 新群组成员
member2=127.0.0.1:30303 # 新群组成员
```

## 命令详解

FISCO BCOS generator 提供多种节点生成、扩容、群组划分、证书相关操作，简略介绍如下：

| 命令名称 | 基本功能 |
| :-: | :-: |
| demo | 快速部署一条测试链 |
| build_install_package | 在指定文件夹下生成mchain.ini中配置的节点配置文件夹（需要在meta下存放证书） |
| build_expand_package | 根据mexpand.ini，以及现有群组和节点配置文件，生成扩容节点配置文件夹 |
| create_group_config | 指定文件夹 | 在指定文件夹下根据mcreate.ini和meta下的证书生成群组配置文件 |
| generate_*_certificate | 生成相关证书 |
| *_all_certificates | 根据配置文件生成相关证书 |
| merge_config | 将两个节点配置文件中的P2P部分合并 |
| deploy_private_key | 将私钥批量导入生成的节点配置文件夹中 |
| version | 打印当前版本号 |
| h/help | 帮助命令 |

### 快速体验命令 demo

```bash
$ git clone https://github.com/FISCO-BCOS/generator.git
$ cd generator
$ ./generator --demo
```

此命令会执行的操作为：

1. 按照./conf/mchain.ini中的配置在./meta下生成证书
2. 按照./conf/mchain.ini的配置在./data下生成节点配置文件夹
3. 拷贝./meta下的私钥至./data的配置文件夹
4. 按照./conf/mexpand.ini中的配置在./meta下生成证书
5. 按照./conf/mexpand.ini的配置在./expand下生成扩容配置文件夹
6. 按照./conf/mgroup.ini的配置在./data下生成group2的相关配置

操作完成后，用户可以:

1. 在./data下运行start_all.sh启动所有节点，并查看节点配置
2. 在./expand下查看扩容生成的节点
3. 在./data下查看group2的相关信息

```eval_rst
.. important::
    此操作仅供体验，实际生产部署中请勿使用本命令
```

### build_install_package (-b)

|  |  |
| :-: | :-: |
| 命令解释 | 部署新节点及新群组 |
| 使用前提 | 用户需配置`mchain.ini`，并在meta下配置相关节点的证书  |
| 参数设置 | 指定文件夹作为配置文件夹存放路径 |
| 实现功能 | 通过给定节点证书和节点信息，生成配置文件夹，并加入一个群组中 |
| 适用场景 | 可用于区块链初始化，区块链生成后，创建新群组和新节点的场景 |

操作示例

```bash
$ cp node0/node.crt ./meta/cert_127.0.0.1_30300.crt
...
$ cp noden/node.crt ./meta/cert_127.0.0.1_3030n.crt
$ vim ./conf/mchain.ini
$ ./generator --build_install_package ~/mydata
```

程序执行完成后，会在~/mydata文件夹下生成多个名为node_hostip_port的文件夹，推送到对应服务器后，拷贝私钥到节点conf下即可启动节点

### build_expand_package (-e)

|  |  |
| :-: | :-: |
| 命令解释 | 扩容新节点加入现有群组 |
| 使用前提 | 用户需配置`mexpand.ini`，并在meta下配置相关节点的证书，并收集扩容群组已运行群组的创世区块信息  |
| 参数设置 | 指文件夹作为扩容包存放路径 |
| 实现功能 | 通过给定节点证书和现有群组节点信息，生成配置文件夹，并加入现有群组中 |
| 适用场景 | 可用于区块链群组已存在，需要扩容节点配置文件夹 |

操作示例

```bash
$ vim ./conf/mexpand.ini
$ cp node0/node.crt ./meta/cert_127.0.0.1_30307.crt
$ cp /tmp/group.1.genesis .meta
$ ./generator --build_expand_package ./expand
```

程序执行完成后，会在./expand文件夹下生成名为node_127.0.0.1_30307的文件夹，推送到对应服务器解压后，拷贝私钥到conf下即可启动节点

节点正常启动后，使用SDK将节点加入群组，完成扩容操作

### create_group_config (-c)

|  |  |
| :-: | :-: |
| 命令解释 | 为已有节点新建群组 |
| 使用前提 | 用户需配置`mgroup.ini`，并在meta下配置相关节点的证书  |
| 参数设置 | 指定生成群组节点配置文件夹 |
| 实现功能 | 根据用户在meta下配置的证书，在指定目录生成新群组的配置 |
| 适用场景 | 联盟链中已有节点从新划分群组时 |

操作示例

```bash
$ cp node0/node.crt ./meta/cert_127.0.0.1_3030n.crt
...
$ vim ./conf/mgroup.ini
$ ./generator --create_group_config ~/mydata
```

程序执行完成后，会在~/mydata文件夹下生成mgroup.ini中配置的`group.i.genesis`和`group.i.ini`

用户将生成的`group.i.genesis`和`group.i.ini`拷贝至对应节点的conf文件夹下，即可完成新群组划分操作

### generate_chain_certificate

|  |  |
| :-: | :-: |
| 命令解释 | 生成根证书 |
| 使用前提 | 无 |
| 参数设置 | 指定根证书存放文件夹 |
| 实现功能 | 在指定目录生成根证书和私钥 |
| 适用场景 | 用户需要生成自签相关根证书 |

```bash
$ ./generator --generate_chain_certificate ./dir_chain_ca(SET)
```

执行完成后用户可以在指定文件夹下看到根证书`ca.crt` 和私钥`ca.key`。

### generate_agency_certificate

|  |  |
| :-: | :-: |
| 命令解释 | 生成机构证书 |
| 使用前提 | 存在根证书和私钥 |
| 参数设置 | 指定机构证书目录，链证书及私钥存放目录和机构名称 |
| 实现功能 | 在指定目录生成机构证书和私钥 |
| 适用场景 | 用户需要生成自签相关机构证书 |

```bash
$ ./generator --generate_agency_certificate ./dir_agency_ca(SET) ./chain_ca_dir The_Agency_Name
```

执行完成后可以在./dir_agency_ca(SET)路径下生成名为The_Agency_Name的文件夹，包含相应的机构证书

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

执行完成后可以在node_dir(SET) 路径下生成节点证书

### generate_sdk_certificate

|  |  |
| :-: | :-: |
| 命令解释 | 生成SDK证书 |
| 使用前提 | 存在机构证书和私钥 |
| 参数设置 | 指定节点证书目录，机构证书及私钥存放目录和节点名称 |
| 实现功能 | 在指定目录生成SDK证书和私钥 |
| 适用场景 | 用户需要生成自签相关SDK证书 |

```bash
$ ./generator --generate_sdk_certificate ./dir_sdk_ca(SET) ./dir_agency_ca
```

执行完成后可以在./dir_sdk_ca(SET)路径下生成名为SDK的文件夹，包含相应的SDK证书

### generate_all_certificates

|  |  |
| :-: | :-: |
| 命令解释 | 根据mchain.ini生成相关证书及私钥 |
| 使用前提 | 无 |
| 参数设置 | 指定节点证书目录 |
| 实现功能 | 在指定目录生成mchain.ini中需要的节点证书和私钥 |
| 适用场景 | 用户生成本机构节点时 |

```
$ ./generator --generate_all_certificates ./cert
```

```eval_rst
.. note::
    上述命令会根据meta目录下存放的ca.crt和ca.key生成相应的其他证书。

    - 如果没有根证书ca.crt会自动生成相应的根证书ca.crt，私钥ca.key，机构证书agency.crt，机构私钥agency.key及其他必备的证书。
    - 如果用户已经拥有根证书ca.crt，但是缺少私钥ca.key，则无法生成任何证书，程序会抛出异常。
    - 如果用户拥有根证书ca.crt，机构证书agency.crt，但是缺少机构私钥agency.key，则无法生成节点证书，程序会抛出异常。

```

执行完成后会在./cert文件夹下生成节点的相关证书与私钥，并拷贝节点证书放置于./meta下

### expand_all_certificates

|  |  |
| :-: | :-: |
| 命令解释 | 根据mchain.ini生成相关证书及私钥 |
| 使用前提 | 存在机构证书及私钥 |
| 参数设置 | 指定节点证书目录 |
| 实现功能 | 在指定目录生成mexpand.ini中需要的节点证书和私钥 |
| 适用场景 | 用户扩容本机构节点时 |

```
$ ./generator --expand_all_certificates ./cert
```

执行完成后会在./cert文件夹下生成节点的相关证书与私钥，并拷贝节点证书放置于./meta下

```eval_rst
.. important::
  由于扩容节点与链本身节点的根证书必须相同，此命令会根据meta目录下存放的(ca.crt和ca.key)或(agency.crt和agency.key)生成相应的节点证书，如果没有根证书抛出异常  
```

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
node.2 = 127.0.0.1:30300
node.3 = 127.0.0.1:30301
```
使用此命令后会成为：

```ini
[p2p]
listen_ip = 127.0.0.1
listen_port = 30304
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 127.0.0.1:30302
node.3 = 127.0.0.1:30303
node.4 = 127.0.0.1:30300
node.5 = 127.0.0.1:30301
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

### version (-v)

使用--version命令查看当前部署工具的版本号

```bash
$ ./generator --version
```

### help (-h)

用户可以使用-h或--help命令查看帮助菜单

使用示例：

```bash
$ ./generator -h
```

使用后会显示相关提示

```bash
$ ./generator -h
usage: generator [-h] [--version] [--build_install_package data_dir]
                 [--build_expand_package data_dir]
                 [--create_group_config data_dir]
                 [--generate_chain_certificate chain_dir]
                 [--generate_agency_certificate agency_dir chain_dir agency_name]
                 [--generate_node_certificate node_dir agency_dir node_name]
                 [--generate_sdk_certificate sdk_dir agency_dir] [--use_guomi]
                 [--demo] [--generate_all_certificates cert_dir]
                 [--expand_all_certificates cert_dir]
                 [--deploy_private_key cert_dir pkg_dir]
                 [--merge_config config.ini config.ini]
```

## 监控设计

FISCO BCOS generator 生成的节点配置文件夹中提供了内置的监控脚本，用户可以通过对其进行配置，将节点的告警信息发送至指定地址。FISCO BCOS generator会将monitor脚本放置于生成节点配置文件的指定目录下，假设用户指定生成的文件夹名为data，则monitor脚本会在data目录下的monitor文件夹下

使用方式如下：

```
$ cd ./data/monitor
```

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

- 使用脚本监控指定路径下节点，发送给接收者Alice

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