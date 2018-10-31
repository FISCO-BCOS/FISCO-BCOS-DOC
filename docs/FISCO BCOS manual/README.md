# FISCO-BCOS Manual  

[Chinese version](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/doc/manual)


## Hardware Requirements  

| Requirements     | Minimum | Recommended                             |
| ---------------- | ------- | --------------------------------------- |
| CPU              | 1.5GHz  | 2.4GHz                                  |
| Memory           | 1GB     | 4GB                                     |
| Core Number      | 2       | 4                                       |
| Bandwidth        | 1Mb     | 5Mb                                     |
| Operation System |         | CentOS (7.2  64x) or Ubuntu(16.04  64x) |
| Java             |         | Java(TM) 1.8 && JDK 1.8                 |

## Deploy FISCO BCOS

### Get the code

> Clone the code to the directory, eg */mydata*:

```shell
#create mydata dir
sudo mkdir -p /mydata
sudo chmod 777 /mydata
cd /mydata

#git clone
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
```

### Install and compile
Change to FISCO-BCOS root dir

> In *FISCO-BCOS* root directory:

```shell
cd FISCO-BCOS
sh build.sh
```
Check if install successful:
```shell
fisco-bcos --version
#success: FISCO-BCOS version x.x.x
```


## Configure Certificates

### Configure root certificate

```Shell
cd /mydata/FISCO-BCOS/tools/scripts/

#bash generate_chain_cert.sh -o root certificate directory
bash generate_chain_cert.sh -o /mydata
```

### Configure agency certificate

```shell
cd /mydata/FISCO-BCOS/tools/scripts/

#bash generate_agency_cert.sh -c root certificate directory -o agency certificate directory -n agency name
bash generate_agency_cert.sh -c /mydata -o /mydata -n test_agency
```


## Genesis Node

### Generate Genesis Node
生成节点的目录、配置文件、启动脚本、身份文件、证书文件。并自动部署系统合约。

```shell
cd /mydata/FISCO-BCOS/tools/scripts/

#sh generate_genesis_node -o 节点文件夹生成位置 -n 节点名 -l 节点监听的IP -r 节点的RPC端口 -p 节点的P2P端口 -c 节点的Channel Port端口 -d 机构证书存放目录 -a 机构证书名
#创世节点
bash generate_genesis_node.sh  -o /mydata -n node0 -l 127.0.0.1 -r 8545 -p 30303 -c 8891 -d /mydata/test_agency/ -a test_agency
```

若成功，得到创世节点信息

``` shell
Genesis node generate success!
-----------------------------------------------------------------
Name:			node0
Node dir:		/mydata/node0
Agency:			test_agency
CA hash:		A809F269BEE93DA4
Node ID:		d23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd
RPC address:		127.0.0.1:8545
P2P address:		127.0.0.1:30303
Channel address:	127.0.0.1:8891
SystemProxy address:	0x919868496524eedc26dbb81915fa1547a20f8998
God address:		0xf78451eb46e20bc5336e279c52bda3a3e92c09b6
State:			Stop
-----------------------------------------------------------------
```

记录下创世节点的RPC address，之后会用到
``` shell
RPC address:  127.0.0.1:8545
```

### 启动创世节点

直接到创世节点文件目录下启动

``` shell
cd /mydata/node0
bash start.sh
#关闭用 sh stop.sh
```

### 创世节点加入联盟
让创世节点成为参与共识的第一个成员

``` shell
cd /mydata/FISCO-BCOS/tools/scripts/

```
设置需要操作的链的RPC端口（此时链上只有一个创世节点），输入y回车确认。

``` shell
#bash set_proxy_address.sh -o 节点的RPC address
bash set_proxy_address.sh -o 127.0.0.1:8545
```

将创世节点注册入联盟中，参与共识

``` shell
#bash register_node.sh -d 要注册节点的文件目录
bash register_node.sh -d /mydata/node0/
```



### 验证创世节点启动

#### 验证进程

查看创世节点进程

``` shell
ps -ef |grep fisco-bcos
```

若看到创世节点进程，表示创世节点启动成功

``` log
app  67232      1  2 14:51 ?        00:00:03 fisco-bcos --genesis /mydata/node0/genesis.json --config /mydata/node0/config.json
```

#### 验证可共识

查看日志，查看打包信息

``` shell
tail -f /mydata/node0/log/info* |grep +++
```

等待一段时间，可看到周期性的出现如下日志，表示节点间在周期性的进行共识，节点运行正确

``` log
INFO|2018-08-10 14:53:33:083|+++++++++++++++++++++++++++ Generating seal on9272a2f7d8fba7e68b1927912f97797447cf94d92fa78222bb8a2892ee814ba8#31tx:0,maxtx:0,tq.num=0time:1533884013083
INFO|2018-08-10 14:53:34:094|+++++++++++++++++++++++++++ Generating seal onf007c276c3f2b136fe84e40a84a54eda1887a47192ec7e2a4feff6407293665d#31tx:0,maxtx:0,tq.num=0time:1533884014094
```

## 增加节点

### 准备

查看创世节点信息

``` shell
cd /mydata/FISCO-BCOS/tools/scripts/

#bash node_info.sh -d 要查看信息的节点目录 -o 生成节点信息文件
bash node_info.sh -d /mydata/node0/ -o node0.info
```

得到创世节点的关键信息。创世节点的关键信息已经在节点信息文件node0.info中记录下来

``` log
Node ID:		d23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd
P2P address:		127.0.0.1:30303
SystemProxy address:	0x919868496524eedc26dbb81915fa1547a20f8998
God address:		0xf78451eb46e20bc5336e279c52bda3a3e92c09b6
```

### 生成节点

生成节点的目录、配置文件、启动脚本、身份文件、证书文件。

**注意：端口不要和其它节点重复**

``` shell
#bash generate_node -o 节点文件生成位置 -n 节点名 -l 节点监听的IP -r 节点的RPC端口 -p 节点的P2P端口 -c 节点的Channel Port端口 -e 链上现有节点的P2P端口列表，用“,”隔开（如指向创世节点和自己 127.0.0.1:30303,127.0.0.1:30304） -d 机构证书存放目录 -a 机构证书名 -f 创世节点的信息文件
bash generate_node.sh -o /mydata -n node1 -l 127.0.0.1 -r 8546 -p 30304 -c 8892 -e 127.0.0.1:30303,127.0.0.1:30304 -d /mydata/test_agency -a test_agency -f node0.info
```

若成功，得到节点信息

``` log
Node generate success!
-----------------------------------------------------------------
Name:			node1
Node dir:		/mydata/node1
Agency:			test_agency
CA hash:		A809F269BEE93DA5
Node ID:		9042d0cbe004c8441ccc92370ab4c36ac197c7c015aa186b3ae7ff3cd4e649a20e82273b9cb54b6ca18adcdc47e15eee103b4b1163be971752c70fd6977d313f
RPC address:		127.0.0.1:8546
P2P address:		127.0.0.1:30304
Channel address:	127.0.0.1:8892
SystemProxy address:	0x919868496524eedc26dbb81915fa1547a20f8998
God address:		0xf78451eb46e20bc5336e279c52bda3a3e92c09b6
State:			Stop
-----------------------------------------------------------------
```

### 启动节点

直接到节点文件目录下启动

``` shell
cd /mydata/node1
bash start.sh
#关闭用 sh stop.sh
```

### 节点加入联盟

让节点成为参与共识的成员

```shell
cd /mydata/FISCO-BCOS/tools/scripts/
```

设置需要操作的链的RPC端口（若之前已设置，则无需重复设置）

```shell
#bash set_proxy_address.sh -o 节点的RPC address
bash set_proxy_address.sh -o 127.0.0.1:8545
```

将节点注册入联盟中，参与共识

```shell
#bash register_node.sh -d 要注册节点的文件目录
bash register_node.sh -d /mydata/node1/
```

### 验证节点启动

#### 验证进程

查看节点进程

```shell
ps -ef |grep fisco-bcos
```

若看到节点进程，表示创世节点启动成功

```log
app  70450      1  0 14:58 ?        00:00:26 fisco-bcos --genesis /mydata/node1/genesis.json --config /mydata/node1/config.json
```

#### 验证已连接

查看日志

``` shell
cat /mydata/node1/log/* | grep "topics Send to"
```

看到发送topic的日志，表示节点已经连接了相应的另一个节点

``` log
DEBUG|2018-08-10 15:42:05:621|topics Send to:1 nodes
DEBUG|2018-08-10 15:42:06:621|topics Send tod23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd@127.0.0.1:30303
```

#### 验证可共识

查看日志，查看打包信息

```shell
tail -f /mydata/node1/log/* |grep +++
```

可看到周期性的出现如下日志，表示节点间在周期性的进行共识，节点运行正确

```log
INFO|2018-08-10 15:48:52:108|+++++++++++++++++++++++++++ Generating seal on57b9e818999467bff75f58b08b3ca79e7475ebfefbb4caea6d628de9f4456a1d#32tx:0,maxtx:1000,tq.num=0time:1533887332108
INFO|2018-08-10 15:48:54:119|+++++++++++++++++++++++++++ Generating seal on273870caa50741a4841c3b54b7130ab66f08227601b01272f62d31e48d38e956#32tx:0,maxtx:1000,tq.num=0time:1533887334119
```
