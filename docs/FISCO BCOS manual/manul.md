## Deploy FISCO BCOS

### Get the code

Clone the code to the directory, eg */mydata*:

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

In *FISCO-BCOS* root directory:

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

## Add nodes

### Preparation

Check genesis node information

``` shell
cd /mydata/FISCO-BCOS/tools/scripts/

#bash node_info.sh -d node directory -o node information
bash node_info.sh -d /mydata/node0/ -o node0.info
```

Get the key information of the genesis node. The key information of the genesis node has been stored in the file 'node0.info'

``` log
Node ID:		d23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd
P2P address:		127.0.0.1:30303
SystemProxy address:	0x919868496524eedc26dbb81915fa1547a20f8998
God address:		0xf78451eb46e20bc5336e279c52bda3a3e92c09b6
```

### Generate node

Generate the node's directory, configuration file, startup script, identity file, and certificate.


**Note: Do not use duplicate ports with other nodes**

``` shell
#bash generate_node -o generation directory -n node name -l node listening IP -r node RPC port -p node P2P port -c node Channel Port -e existing P2P ports list, saperate by ',' -d agency certificate directory -a agency certificate -f Genesis node information file
bash generate_node.sh -o /mydata -n node1 -l 127.0.0.1 -r 8546 -p 30304 -c 8892 -e 127.0.0.1:30303,127.0.0.1:30304 -d /mydata/test_agency -a test_agency -f node0.info
```
If success you will get node information like below:

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

### Start node

``` shell
cd /mydata/node1
bash start.sh
#stop command  sh stop.sh
```

### Register node to Consortium chain

Let the node participate in the consensus


```shell
cd /mydata/FISCO-BCOS/tools/scripts/
```

Set the RPC port (if it has been set before, there is no need to set again)

```shell
#bash set_proxy_address.sh -o node RPC address
bash set_proxy_address.sh -o 127.0.0.1:8545
```

Register node to the consortium chain and participate in the consensus.

```shell
#bash register_node.sh -d node directory
bash register_node.sh -d /mydata/node1/
```

### Verify node start

#### Verify process

Check node process

```shell
ps -ef |grep fisco-bcos
```

If node's process is available means node start successful.


```log
app  70450      1  0 14:58 ?        00:00:26 fisco-bcos --genesis /mydata/node1/genesis.json --config /mydata/node1/config.json
```

#### Verify connection

Check log

``` shell
cat /mydata/node1/log/* | grep "topics Send to"
```

If the topics send log available means the connection between nodes is works.
``` log
DEBUG|2018-08-10 15:42:05:621|topics Send to:1 nodes
DEBUG|2018-08-10 15:42:06:621|topics Send tod23058c33577f850832e47994df495c674ba66273df2fcb1e6ee7d7e1dbd7be78be2f7b302c9d15842110b3db6239da2aa98ddf68e512b452df748d3d3e4c1cd@127.0.0.1:30303
```

#### Verify consensus ability

Check log to see the package information

```shell
tail -f /mydata/node1/log/* |grep +++
```

With waiting a while, if you see the log like below shows periodicity means node is participate in the consensus and work successful.

```log
INFO|2018-08-10 15:48:52:108|+++++++++++++++++++++++++++ Generating seal on57b9e818999467bff75f58b08b3ca79e7475ebfefbb4caea6d628de9f4456a1d#32tx:0,maxtx:1000,tq.num=0time:1533887332108
INFO|2018-08-10 15:48:54:119|+++++++++++++++++++++++++++ Generating seal on273870caa50741a4841c3b54b7130ab66f08227601b01272f62d31e48d38e956#32tx:0,maxtx:1000,tq.num=0time:1533887334119
```
