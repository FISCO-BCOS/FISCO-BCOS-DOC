# 创世节点

## 生成创世节点

生成节点的目录、配置文件、启动脚本、身份文件、证书文件。并自动部署系统合约。

``` shell
cd /mydata/FISCO-BCOS/tools/scripts/

#sh generate_genesis_node -o 节点文件夹生成位置 -n 节点名 -l 节点监听的IP -r 节点的RPC端口 -p 节点的P2P端口 -c 节点的Channel Port端口 -d 机构证书存放目录 -a 机构证书名
#创世节点
bash generate_genesis_node.sh  -o /mydata -n node0 -l 127.0.0.1 -r 8545 -p 30303 -c 8891 -d /mydata/test_agency/ -a test_agency
```

若成功，得到创世节点信息

``` log
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

``` log
RPC address:  127.0.0.1:8545
```

## 启动创世节点

直接到创世节点文件目录下启动

``` shell
cd /mydata/node0
bash start.sh
#关闭用 sh stop.sh
```

## 创世节点加入联盟

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



## 验证创世节点启动

### 验证进程

查看创世节点进程

``` shell
ps -ef |grep fisco-bcos
```

若看到创世节点进程，表示创世节点启动成功

``` log
app  67232      1  2 14:51 ?        00:00:03 fisco-bcos --genesis /mydata/node0/genesis.json --config /mydata/node0/config.json
```

### 验证可共识

查看日志，查看打包信息

``` shell
tail -f /mydata/node0/log/info* |grep +++
```

等待一段时间，可看到周期性的出现如下日志，表示节点间在周期性的进行共识，节点运行正确

``` log
INFO|2018-08-10 14:53:33:083|+++++++++++++++++++++++++++ Generating seal on9272a2f7d8fba7e68b1927912f97797447cf94d92fa78222bb8a2892ee814ba8#31tx:0,maxtx:0,tq.num=0time:1533884013083
INFO|2018-08-10 14:53:34:094|+++++++++++++++++++++++++++ Generating seal onf007c276c3f2b136fe84e40a84a54eda1887a47192ec7e2a4feff6407293665d#31tx:0,maxtx:0,tq.num=0time:1533884014094
```
