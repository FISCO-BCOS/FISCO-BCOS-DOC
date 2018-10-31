
## Genesis Node

### Generate Genesis Node
Generate Node's directory, configuration file, startup script, identity file, and certificate. And automatically deploy system contracts.

```shell
cd /mydata/FISCO-BCOS/tools/scripts/

#sh generate_genesis_node -o directory -n node name -l node listening IP -r node RPC port -p node P2P port -c node Channel Port -d agency certificate directory -a agency certificate name
#Genesis Node
bash generate_genesis_node.sh  -o /mydata -n node0 -l 127.0.0.1 -r 8545 -p 30303 -c 8891 -d /mydata/test_agency/ -a test_agency
```
If success, you will get Genesis node information like below:

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

Record Genesis node's RPC address, which will be used later.
``` shell
RPC address:  127.0.0.1:8545
```

### Start Genesis node

``` shell
cd /mydata/node0
bash start.sh
#stop command   sh stop.sh
```

### Register Genesis node to Consortium chain
Let the Genesis node become the first member to participate in the consensus

``` shell
cd /mydata/FISCO-BCOS/tools/scripts/

```

Set the RPC port (only one Genesis node in the chain now), enter y to confirm.

``` shell
#bash set_proxy_address.sh -o 节点的RPC address
bash set_proxy_address.sh -o 127.0.0.1:8545
```

Register Genesis node to consortium chain and participate in the consensus.

``` shell
#bash register_node.sh -d node di
bash register_node.sh -d /mydata/node0/
```



### Verify Genesis node's start

#### Verify process


``` shell
ps -ef |grep fisco-bcos
```
If genesis node's process is available means genesis node start successful.

``` log
app  67232      1  2 14:51 ?        00:00:03 fisco-bcos --genesis /mydata/node0/genesis.json --config /mydata/node0/config.json
```

#### Verify consensus ability

Check log to see the package information

``` shell
tail -f /mydata/node0/log/info* |grep +++
```

With waiting a while, if you see the log like below shows periodicity means node is participate in the consensus and work successful.

``` log
INFO|2018-08-10 14:53:33:083|+++++++++++++++++++++++++++ Generating seal on9272a2f7d8fba7e68b1927912f97797447cf94d92fa78222bb8a2892ee814ba8#31tx:0,maxtx:0,tq.num=0time:1533884013083
INFO|2018-08-10 14:53:34:094|+++++++++++++++++++++++++++ Generating seal onf007c276c3f2b136fe84e40a84a54eda1887a47192ec7e2a4feff6407293665d#31tx:0,maxtx:0,tq.num=0time:1533884014094
```
