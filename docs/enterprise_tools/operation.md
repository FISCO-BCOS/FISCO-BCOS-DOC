# 操作手册

fisco generator 提供多种节点生成、扩容、群组划分、证书相关操作，简略介绍如下：

| 命令名称 | 命令参数 | 基本功能 | 适用场景 |
| :-: | :-: | :-: | :-:|
| demo | 无 | 快速部署一条测试链 | 体验、测试FISCO BCOS |
| build | 指定文件夹 | 在指定文件夹下生成mchain.ini中配置的节点安装包（需要在meta下存放证书） | 用户需要生成节点安装包和群组配置文件。需要交换证书 |
| expand | 1.存放原有群组节点的文件夹  2.扩容节点安装包文件夹 | 根据mexpand.ini，以及现有群组和节点配置文件，生成扩容节点安装包 | 用户需要为现有群组生成节点安装包，需要交换配置文件，不需要交换证书 |
| create | 指定文件夹 | 在指定文件夹下根据mcreate.ini和meta下的证书生成群组配置文件 | 用户已有节点，但需要生成新群组 。需要交换证书 |
| *ca | 证书目录 | 生成相关证书 | 用户需要生成自签相关根证书、机构证书、节点证书、sdk证书时 |
| cert* | 证书存放目录| 根据配置文件生成相关证书 | 用户需要批量根据配置文件生成节点证书时 |
| combine | 需要合并的两个节点配置配置文件 | 将两个节点配置文件中的P2P部分合并 | 扩容节点后需要更新节点P2P配置项时 |
| deploykey | 私钥存放目录 安装包存放目录| 将私钥批量导入生成的安装包中 | 用户已经拥有私钥和生成的安装包，需要将私钥批量导入安装包中时 |
| version | 无 | 打印当前版本号 | 提示性命令 |
| h/help | 无 | 帮助命令 | 提示性命令 |

## 快速体验命令 --demo

```s
$ git clone https://github.com/FISCO-BCOS/generator.git
$ cd generator
$ ./generator --demo
```

此命令会执行的操作为：

1. 按照./conf/mchain.ini中的配置在./meta下生成证书
2. 按照./conf/mchain.ini的配置在./data下生成安装包
3. 拷贝./meta下的私钥至./data的安装包
4. 按照./conf/expand.ini中的配置在./meta下生成证书
5. 按照./conf/expand.ini的配置在./expand下生成扩容安装包
6. 按照./conf/mgroup.ini的配置在./data下生成group2的相关配置

操作完成后，用户可以:

1. 在./data下运行start_all.sh启动所有节点，并查看节点配置
2. 在./expand下查看扩容生成的节点
3. 在./data下查看group2的相关信息

```eval_rst
.. important::
    此操作仅供体验，实际生产部署中请勿使用本命令
```

## 部署新节点及群组 --build

-- build命令为部署新节点及群组的操作

指定参数为安装包输出路径，用户需配置mchain.ini，并在meta下配置相关节点的证书 ----可用于区块链生成后，创建新群组和新节点的场景

操作完成后，给定节点证书，和节点信息（端口号，ip），生成安装包，并加入一个群里，将打好的包生成到指定目录

操作示例

```s
$ cp node0/node.crt ./meta/cert_127.0.0.1_30300.crt
...
$ cp noden/node.crt ./meta/cert_127.0.0.1_3030n.crt
$ vim ./conf/mchain.ini
$ ./generator --build ~/mydata
```

程序执行完成后，会在~/mydata文件夹下生成多个名为node_hostip_port的文件夹，推送到对应服务器解压后，拷贝私钥到节点conf下即可启动节点

## 扩容新节点 --expand

--expand命令为扩容新节点加入现有群组

nargs=2, 指定参数1.存放有原有群组信息的路径，2.生成安装包的路径  用户需配置mexpand.ini

给定原有group中节点的配置，和新节点的证书，生成安装包

```s
$ vim ./conf/mexpand.ini
$ cp node0/node.crt ./meta/cert_127.0.0.1_30307.crt
$ cp /tmp/config.ini /tmp/group.1.genesis /tmp/group.1.ini ./expand
$ ./generator --expand ./expand ~/mydata
```

程序执行完成后，会在~/mydata文件夹下生成名为node_127.0.0.1_30307的文件夹，推送到对应服务器解压后，拷贝私钥到conf下即可启动节点

节点正常启动后，使用sdk将节点加入群组，完成扩容操作

## 划分新群组 --create

--create命令为已有节点新建群组的操作

指定参数为生成的group.i.genesis和group.i.ini的存放路径

此命令会根据用户在meta下配置的证书，在指定目录生成新群组的配置

操作范例

```s
$ cp node0/node.crt ./meta/cert_127.0.0.1_3030n.crt
...
$ vim ./conf/group.ini
$ ./generator --create ~/mydata
```

程序执行完成后，会在~/mydata文件夹下生成mgroup.ini中配置的group.i.genesis和group.i.ini

用户将生成的group.i.genesis和group.i.ini拷贝至对应节点的conf文件夹下，即可完成新群组划分操作

## 证书生成相关命令

fisco generator提供自签证书操作。示例如下：

### 生成根证书 --chainca命令

用户可以指定目录，生成根证书

```s
$ ./genrator --chainca ./dir_chain_ca(SET)
```

执行完成后用户可以在指定文件夹下看到根证书ca.crt 和私钥ca.key。

### 生成机构证书 --agencyca命令

用户可以指定机构证书目录，链证书存放目录和机构名称，生成机构证书

```s
$ ./genrator --agencyca ./dir_agency_ca(SET) ./chain_ca_dir The_Agency_Name
```

执行完成后可以在./dir_agency_ca(SET)路径下生成名为The_Agency_Name的文件夹，包含相应的机构证书

### 生成sdk证书 --sdkca

用户可以指定sdk存放目录，机构证书存放目录，生成sdk证书

```s
$ ./genrator --sdkca ./dir_sdk_ca(SET) ./dir_agency_ca
```

执行完成后可以在./dir_sdk_ca(SET)路径下生成名为sdk的文件夹，包含相应的sdk证书

### 生成节点证书 --nodeca

用户可以指定机构证书目录，节点存放目录和节点名称，生成节点证书

```s
$ ./genrator --nodeca ./agency_dir node_dir(SET) node_name
```

执行完成后可以在node_dir(SET) 路径下生成节点证书

## 根据配置文件生成相应证书

fisco generator支持用户根据配置文件夹生成相应证书与相应私钥

### --certbuild

用户使用此命令，可以在指定目录下根据mchain.ini的相关配置生成相应节点证书与私钥，并放置生成的节点证书与./meta文件夹下，用户生成安装包后可以结合[--deploykey]命令导入私钥到安装包启动节点

```
$ ./genrator --certbuild ./cert
```

```eval_rst
.. important::
    
    此命令会根据meta目录下存放的ca.crt和ca.key生成相应的节点证书，如果没有根证书会自动生成
```

执行完成后会在./cert文件夹下生成节点的相关证书与私钥，并拷贝节点证书放置于./meta下

### --certexpand

用户使用此命令，可以在指定目录下根据mexpand.ini的相关配置生成相应节点证书与私钥，并放置生成的节点证书与./meta文件夹下，用户生成安装包后可以结合[--deploykey]命令导入私钥到安装包启动节点

```
$ ./genrator --certexpand ./cert
```

执行完成后会在./cert文件夹下生成节点的相关证书与私钥，并拷贝节点证书放置于./meta下

```eval_rst
.. important::
  由于扩容节点与链本身节点的根证书必须相同，此命令会根据meta目录下存放的(ca.crt和ca.key)或(agency.crt和agency.key)生成相应的节点证书，如果没有根证书抛出异常  
```

## 其他命令

### -h/--help

用户可以使用-h或--help命令查看帮助菜单

使用示例：

```s
$ ./generator -h
```

使用后会显示相关提示

```s
$ ./generator -h
usage: generator [-h] [--version] [--build data_dir]
                 [--expand conf_dir, data_dir conf_dir, data_dir]
                 [--create data_dir] [--chainca chain_dir]
                 [--agencyca agency_dir chain_dir agency_name]
                 [--nodeca node_dir agency_dir node_name] [--gm] [--demo]
                 [--certbuild cert_dir] [--certexpand cert_dir]
                 [--deploykey cert_dir, pkg_dir cert_dir, pkg_dir]
                 [--combine config.ini, config.ini config.ini, config.ini
```

### --combine

使用--combine命令可以合并两个config.ini中的p2p section

如 A目录下的config.ini文件的p2p section为

```s
[p2p]
listen_ip = 127.0.0.1
listen_port = 30300
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 127.0.0.1:30302
node.3 = 127.0.0.1:30303
```

B目录下的config.ini文件的p2p section为

```s
[p2p]
listen_ip = 127.0.0.1
listen_port = 30303
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30303
node.2 = 192.167.1.1:30300
node.3 = 192.167.1.1:30301
```
使用此命令后会成为：

```s
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

```s
$ ./generator --combine ~/mydata/node_A/config.ini  ~/mydata/node_B/config.ini
```

使用成功后会将node_A和node_B的config.ini中p2p section合并与 ~/mydata/node_B/config.ini的文件中

### --deploykey

使用--deploykey可以将路径下名称相同的节点私钥导入到生成好的安装包中。

使用示例:

```s
$./generator --deploykey ./cert ./data
```

如./cert下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的文件夹，文件夹中有节点私钥文件node.key

./data下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的安装包

执行完成后可以将./cert下的对应的节点私钥导入./data的安装包中

## --version

使用--version命令查看当前部署工具的版本号

```s
$ ./generator --version
```
