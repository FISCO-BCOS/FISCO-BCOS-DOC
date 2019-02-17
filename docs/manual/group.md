# 多群组操作实战

星形组网和物理多链是区块链应用中使用最广泛的两种组网方式。星型组网拓扑中，中心机构节点同时运行多家机构的应用，每家机构的节点属于不同的群组；物理多链拓扑中，每个节点均属于多个群组，每个群组运行独立的应用。

![](../../images/group/group.png)

下面以构建七节点星形拓扑和四节点物理多链拓扑为例，详细介绍多群组操作方法。

## 安装依赖

部署FISCO BCOS区块链系统前，需要安装openssl，leveldb等依赖软件，具体命令如下：

```bash
### CentOS
sudo yum -y install openssl openssl-devel leveldb leveldb-devel

### Ubuntu
sudo apt-get install openssl libssl-dev libleveldb-dev
```

## CASE1: 星形组网

本章以介绍构建本机七节点三群组四机构的星形组网拓扑为例，介绍多群组使用方法。星型组网区块链系统详细组网情况如下：

- 机构A：同时属于group1, group2和group3，包括两个节点，节点IP均为127.0.0.1;
- 机构B：属于group1,包括两个节点，节点IP均为127.0.0.1;
- 机构C：属于group2，包括两个节点，节点IP均为127.0.0.1;
- 机构D：属于group3，包括一个节点，节点IP均为127.0.0.1。

**注：真实应用场景中，建议根据机器负载选择部署节点数目，星形网络拓扑中，核心节点(本例中机构A节点)同属于所有群组，负载较大，建议单独部署在性能较好的机器**


### 构建星形区块链系统

`build_chain.sh`简单方便地支持了任意拓扑多群组区块链系统构建，可以使用该脚本构建星形区块链系统：

**获取二进制代码**

```bash
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh)
```

**获取build_chain.sh脚本**

```bash
 curl https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh -o build_chain.sh
```

**生成星形拓扑配置文件**

```bash
 echo -e "127.0.0.1:2 agencyA 1,2,3\n127.0.0.1:2 agencyB 1\n127.0.0.1:2 agencyC 2\n127.0.0.1:2 agencyD 3" > ip_list   
```

**使用build_chain脚本构建星形区块链系统**

```bash
# 根据配置生成星形区块链系统
$ bash build_chain.sh -f ip_list -e ./bin/fisco-bcos -o nodes
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:2 Agency:agencyA Groups:1,2,3
Processing IP:127.0.0.1 Total:2 Agency:agencyB Groups:1
Processing IP:127.0.0.1 Total:2 Agency:agencyC Groups:2
Processing IP:127.0.0.1 Total:2 Agency:agencyD Groups:3
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:2 Agency:agencyA Groups:1,2,3
Processing IP:127.0.0.1 Total:2 Agency:agencyB Groups:1
Processing IP:127.0.0.1 Total:2 Agency:agencyC Groups:2
Processing IP:127.0.0.1 Total:2 Agency:agencyD Groups:3
==============================================================
Group:1 has 4 nodes
Group:2 has 4 nodes
Group:3 has 4 nodes
==============================================================
[INFO] FISCO-BCOS Path   : ./bin/fisco-bcos
[INFO] IP List File      : ip_list
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:2 127.0.0.1:2 127.0.0.1:2 127.0.0.1:2
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /data/nodes
[INFO] CA Key Path       : /data/nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /data/nodes

# 生成的节点文件如下
$ tree
.
|-- 127.0.0.1
|   |-- fisco-bcos
|   |-- node0
|   |   |-- conf  #节点配置目录
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.1.genesis
|   |   |   |-- group.1.ini
|   |   |   |-- group.2.genesis
|   |   |   |-- group.2.ini
|   |   |   |-- group.3.genesis
|   |   |   |-- group.3.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- node1
|   |   |-- conf
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.1.genesis
|   |   |   |-- group.1.ini
|   |   |   |-- group.2.genesis
|   |   |   |-- group.2.ini
|   |   |   |-- group.3.genesis
|   |   |   |-- group.3.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- node2
|   |   |-- conf
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.1.genesis
|   |   |   |-- group.1.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- node3
|   |   |-- conf
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.1.genesis
|   |   |   |-- group.1.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- node4
|   |   |-- conf
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.2.genesis
|   |   |   |-- group.2.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- node5
|   |   |-- conf
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.2.genesis
|   |   |   |-- group.2.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- node6
|   |   |-- conf
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.3.genesis
|   |   |   |-- group.3.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- node7
|   |   |-- conf
|   |   |   |-- agency.crt
|   |   |   |-- ca.crt
|   |   |   |-- group.3.genesis
|   |   |   |-- group.3.ini
|   |   |   |-- node.crt
|   |   |   |-- node.key
|   |   |   `-- node.nodeid
|   |   |-- config.ini
|   |   |-- start.sh
|   |   `-- stop.sh
|   |-- sdk  #SDK证书文件
|   |   |-- ca.crt
|   |   |-- ca.crt
|   |   |-- node.crt
|   |   |-- node.key
|   |-- start_all.sh  # 节点启动脚本
|   |-- stop_all.sh   # 节点停止脚本
|   `-- transTest.sh  # 发交易脚本
|-- cert  # 证书文件目录
|   |-- agencyA
|   |   |-- agency.crt
|   |   |-- agency.key
|   |   |-- agency.srl
|   |   |-- ca-agency.crt
|   |   |-- ca.crt
|   |   `-- cert.cnf
|   |-- agencyB
|   |   |-- agency.crt
|   |   |-- agency.key
|   |   |-- agency.srl
|   |   |-- ca-agency.crt
|   |   |-- ca.crt
|   |   `-- cert.cnf
|   |-- agencyC
|   |   |-- agency.crt
|   |   |-- agency.key
|   |   |-- agency.srl
|   |   |-- ca-agency.crt
|   |   |-- ca.crt
|   |   `-- cert.cnf
|   |-- agencyD
|   |   |-- agency.crt
|   |   |-- agency.key
|   |   |-- agency.srl
|   |   |-- ca-agency.crt
|   |   |-- ca.crt
|   |   `-- cert.cnf
|   |-- ca.crt
|   |-- ca.key
|   |-- ca.srl
|   `-- cert.cnf
```

**启动节点**

build_chain在节点目录下提供`start_all.sh`和`stop_all.sh`脚本来启动和停止节点。

```bash
# 进入节点目录
$ cd nodes/127.0.0.1

# 启动节点
bash start_all.sh

# 查看节点进程
$ ps aux |grep fisco-bcos
app         301  0.8  0.0 986644  7452 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node5/../fisco-bcos -c config.ini
app         306  0.9  0.0 986644  6928 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node6/../fisco-bcos -c config.ini
app         311  0.9  0.0 986644  7184 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node7/../fisco-bcos -c config.ini
app      131048  2.1  0.0 1429036 7452 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
app      131053  2.1  0.0 1429032 7180 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
app      131058  0.8  0.0 986644  7928 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
app      131063  0.8  0.0 986644  7452 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node3/../fisco-bcos -c config.ini
app      131068  0.8  0.0 986644  7672 pts/0    Sl   15:21   0:00 /data/nodes/127.0.0.1/node4/../fisco-bcos -c config.ini

```

**查看群组出块状态**

不发交易时，节点会一直出空块，本例中，node0、node1同时属于group1、group2和group3；node2、node3属于group1；node4、node5属于group2；node6、node7属于group3，可通过`tail -f xxx.log | grep "g:${group_id}.*++"`查看各节点是否正常：

```bash
# 查看node0的group1是否出块正常
tail -f node0/log/log_2019021115.21.log | grep "g:1.*++" 
info|2019-02-11 15:33:09.914042| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=72254a42...
info|2019-02-11 15:33:13.923497| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=d606db88...

# 查看node0的group2是否出块正常
tail -f node0/log/log_2019021115.21.log | grep "g:2.*++" 
info|2019-02-11 15:33:31.021697| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=ef59cf17...
info|2019-02-11 15:33:35.032044| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=ea175441...

# 查看node0的group3是否出块正常
 tail -f node0/log/log_2019021115.21.log | grep "g:3.*++"
info|2019-02-11 15:33:51.022444| [g:3][p:776][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=2c455288...
info|2019-02-11 15:33:55.032891| [g:3][p:776][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=783e7fb6...

# ... 查看node1的所有群组是否正常可参考node0操作方法...
# 查看node3的group1是否出块正常
 tail -f node3/log/log_2019021115.21.log | grep "g:1.*++"  
info|2019-02-11 15:39:43.927167| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=5e94bf63...
info|2019-02-11 15:39:47.936228| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=09bbb2c0...

# 查看node5的group2是否出块正常
tail -f node5/log/log_2019021115.21.log | grep "g:2.*++" 
info|2019-02-11 15:39:42.922510| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=b80a724d...
info|2019-02-11 15:39:46.931831| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=1b5a50ca...

# 查看node6的group3是否出块正常
tail -f node6/log/log_2019021115.21.log | grep "g:3.*++" 
info|2019-02-11 15:39:58.994218| [g:3][p:776][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=eb5801bf...
info|2019-02-11 15:40:03.004553| [g:3][p:776][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=00f43d52...

```

### 向群组发交易

`build_chain`提供了`transTest.sh`脚本，可向指定群组发交易，该脚本使用方法：

```bash
bash transTest.sh ${交易数目} ${群组ID}

```

```bash
# ... 向group1发交易...
$ bash transTest.sh 10 1
Send transaction:  1
{"id":83,"jsonrpc":"2.0","result":"0x226e54480ce325a5858240a10864d7fc1127f2adc17e3e02dd314f91baab074b"}
Send transaction:  2
{"id":83,"jsonrpc":"2.0","result":"0x9f69fa21081ef18be04536de6583d8c633bf0387b15eb8b8aa9d7f6bbbd9654e"}
Send transaction:  3
{"id":83,"jsonrpc":"2.0","result":"0xf66328490d5aa36b43f35c3310999ca23a37124a43aa16c6e3d8f73b475c3084"}
Send transaction:  4
{"id":83,"jsonrpc":"2.0","result":"0xe4762632333b16b3fe204e57ba0b79cac49fb0502448e604a5a2148e1e7ac8a8"}
Send transaction:  5
{"id":83,"jsonrpc":"2.0","result":"0xc59575e9e8638b6f0702e68313bef2e84fb25cef8f8908b5efb2eeefae0f0065"}
Send transaction:  6
{"id":83,"jsonrpc":"2.0","result":"0x2162ceb09a8588c65ea70cd9d93a63f7055f94878bc93c5486f7796b4d95966d"}
Send transaction:  7
{"id":83,"jsonrpc":"2.0","result":"0x60af22ed7b91cbbf1d84b4ae4cc776fe2c2c28f96802b3d4b67174a0ce17ad40"}
Send transaction:  8
{"id":83,"jsonrpc":"2.0","result":"0xcc29712db23cf95993600722687ae42e45dcf668c23466d2ecd1e51ff6dea628"}
Send transaction:  9
{"id":83,"jsonrpc":"2.0","result":"0x26bd6deaa210c698e48de9e6c3639ffaa68cd39443b29b70147facbc68dfe3bf"}
Send transaction:  10
{"id":83,"jsonrpc":"2.0","result":"0xb3221da2cfcb2c74905c977d8ba07c505e31964ad874d8d05634bf5b348ecb61"}

# 查看共识情况
$ tail -f node0/log/* |grep "g:1.*Report"
info|2019-02-11 16:16:12.996150| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=4,idx=1,hash=197fa34f...,next=5,tx=1,myIdx=2
info|2019-02-11 16:16:13.040461| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=5,idx=1,hash=32c12f3d...,next=6,tx=1,myIdx=2
info|2019-02-11 16:16:13.054608| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=6,idx=1,hash=2bdd3de2...,next=7,tx=3,myIdx=2
info|2019-02-11 16:16:13.063410| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=7,idx=2,hash=0b60770a...,next=8,tx=1,myIdx=2
info|2019-02-11 16:16:13.068799| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=8,idx=3,hash=16e7343e...,next=9,tx=1,myIdx=2
info|2019-02-11 16:16:13.156594| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=9,idx=1,hash=ba6d1e49...,next=10,tx=3,myIdx=2

# ... 向group2发交易...
$  bash transTest.sh 10 2
Send transaction:  1
{"id":83,"jsonrpc":"2.0","result":"0x3ff1ff1f1eee9c7c376e68de88a549a994913312f288f458735c323aff4b83bd"}
Send transaction:  2
{"id":83,"jsonrpc":"2.0","result":"0xad904ab216aadb907b2d8f3c4b47d05d60e7a9af5119ce9e51b323481fc4a25c"}
Send transaction:  3
{"id":83,"jsonrpc":"2.0","result":"0xf4cd11cbd43b18e0c0df4e788e7e9ccb9826c2927b1303cc27e54abf4b6a6c0f"}
Send transaction:  4
{"id":83,"jsonrpc":"2.0","result":"0xe55337d3704864581012297681d8ed3b55fb82c094459cf3cbb0d1a13628c542"}
Send transaction:  5
{"id":83,"jsonrpc":"2.0","result":"0x4bf89a59344a60d7290c21e5368c6765c72a8d7675cee5c33df10a2b423ce8d9"}
Send transaction:  6
{"id":83,"jsonrpc":"2.0","result":"0x2ab3b298093c3e779e7cb38c4ac6e2fe397e682d9a89d6c03d563ff21a9c20b7"}
Send transaction:  7
{"id":83,"jsonrpc":"2.0","result":"0xa78954a78c1d54aabb9498e8cd10927477f73228089c032db45b12fee75077cb"}
Send transaction:  8
{"id":83,"jsonrpc":"2.0","result":"0xdeed6680dadc7071db8ba59700a59cbf2f2a6e229fc38ac7779a3416f49e694f"}
Send transaction:  9
{"id":83,"jsonrpc":"2.0","result":"0xf5c5a151f0d1e744abd46d30a6104041f9fbc4f2eb091e826236e73af125ccd7"}
Send transaction:  10
{"id":83,"jsonrpc":"2.0","result":"0x57c02b4ce0dc3aa38347bc63ff271c650ce73a129d352a3b03090f4e2175ef8e"}

# 查看区块共识情况
$ tail -f node0/log/* |grep "g:2.*Report"
info|2019-02-11 16:07:35.947676| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=1,idx=3,hash=ee92f247...,next=2,tx=1,myIdx=3
info|2019-02-11 16:16:46.909703| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=2,idx=3,hash=1a26fd47...,next=3,tx=1,myIdx=3
info|2019-02-11 16:16:46.959578| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=3,idx=3,hash=7d8d8305...,next=4,tx=1,myIdx=3
info|2019-02-11 16:16:47.005533| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=4,idx=3,hash=bd918b98...,next=5,tx=4,myIdx=3
info|2019-02-11 16:16:47.013241| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=5,idx=0,hash=319df20f...,next=6,tx=3,myIdx=3

# ...向group3发交易...
$ bash transTest.sh 10 3
Send transaction:  1
{"id":83,"jsonrpc":"2.0","result":"0x5f7a7c0a035a32a6fa17f0b797cc98eca45285f3e6347c6cd927efb7cd2a1a0b"}
Send transaction:  2
{"id":83,"jsonrpc":"2.0","result":"0x6f2279b37b98b79960e2e7291afbd89fceb9116c8d40859bf3d8374e2711b2dd"}
Send transaction:  3
{"id":83,"jsonrpc":"2.0","result":"0xda34bfa52fc8d5e8670b178b1f1fa059e772c9708b50d92c5e6c11fcfe4f7c33"}
Send transaction:  4
{"id":83,"jsonrpc":"2.0","result":"0x285fc704a0999cb154840489c3f7ce8735006b41d063fda879b3e1f69204592d"}
Send transaction:  5
{"id":83,"jsonrpc":"2.0","result":"0xa7034d4a172ea2d99737d5af42f4a2814e55a5231b85092eb23422527fb530d0"}
Send transaction:  6
{"id":83,"jsonrpc":"2.0","result":"0x038f6b3df1de9297f4fc3eedba24dacc5cdbd2654d35f4449c9fb3c6135e4465"}
Send transaction:  7
{"id":83,"jsonrpc":"2.0","result":"0x7abbd77921fde86a3969b2d8fbf117d4a476c3adeb94d877f46b498a62c494c4"}
Send transaction:  8
{"id":83,"jsonrpc":"2.0","result":"0x14ddc20bc892f1fcd2f8559ccd96860baa6f8e767ad691bdfc2cf606c05b7f69"}
Send transaction:  9
{"id":83,"jsonrpc":"2.0","result":"0x4eeee09aa9a37fd8f271b9be961e9f9dd356eb6696d0d2ff6737cccddd61ee0c"}
Send transaction:  10
{"id":83,"jsonrpc":"2.0","result":"0xfe229f5a2ad6e43839ba38c0fe863e8400006b9d2d88f2b262803ed6ece0cd21"}

#查看区块共识情况
tail -f node0/log/* |grep "g:3.*Report"
info|2019-02-11 16:17:17.147941| [g:3][p:776][CONSENSUS][PBFT]^^^^^Report:,num=1,idx=3,hash=843f6498...,next=2,tx=1,myIdx=3
info|2019-02-11 16:17:17.191567| [g:3][p:776][CONSENSUS][PBFT]^^^^^Report:,num=2,idx=2,hash=5480d8ff...,next=3,tx=3,myIdx=3
info|2019-02-11 16:17:17.197502| [g:3][p:776][CONSENSUS][PBFT]^^^^^Report:,num=3,idx=2,hash=9f966878...,next=4,tx=1,myIdx=3
info|2019-02-11 16:17:17.206428| [g:3][p:776][CONSENSUS][PBFT]^^^^^Report:,num=4,idx=0,hash=fd3f78da...,next=5,tx=1,myIdx=3
info|2019-02-11 16:17:17.254257| [g:3][p:776][CONSENSUS][PBFT]^^^^^Report:,num=5,idx=0,hash=aaddf235...,next=6,tx=2,myIdx=3
info|2019-02-11 16:17:17.297761| [g:3][p:776][CONSENSUS][PBFT]^^^^^Report:,num=6,idx=1,hash=1fd21637...,next=7,tx=2,myIdx=3

```

### 节点加入/退出新群组

通过[控制台](console.md)，FISCO BCOS可将指定节点加入到群组中，也可将其从群组中删除，详细介绍请参考[节点准入管理手册](node_access_management.md)。控制台依赖SDK，使用控制台之前，请参考[SDK配置](../sdk/config.md)配置好SDK。

以将node2加入group2为共识节点为例，具体操作方法如下：

**启动控制台**

```bash
# 进入web3sdk目录(设位于/data/web3sdk)
$ cd /data/web3sdk/dist/bin

# 启动web3sdk，连接group2所有节点
$ bash web3sdk -c 2

```

**将node2加入group2为共识节点**

```bash
# ...获取node2的node id...
$ cat node2/conf/node.nodeid 
6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4

# ... 从node0拷贝group2的配置到node2...
$ cp node0/conf/group.2.* node2/conf

# ...重启node2...
cd node2 && bash stop.sh && bash start.sh

# ...通过控制台将node2加入为共识节点
# 1. 查看当前共识节点列表
> getMinerList
[
    9217e87c6b76184cf70a5a77930ad5886ea68aefbcce1909bdb799e45b520baa53d5bb9a5edddeab94751df179d54d41e6e5b83c338af0a19c0611200b830442,
    227c600c2e52d8ec37aa9f8de8db016ddc1c8a30bb77ec7608b99ee2233480d4c06337d2461e24c26617b6fd53acfa6124ca23a8aa98cb090a675f9b40a9b106,
    7a50b646fcd9ac7dd0b87299f79ccaa2a4b3af875bd0947221ba6dec1c1ba4add7f7f690c95cf3e796296cf4adc989f4c7ae7c8a37f4505229922fb6df13bb9e,
    8b2c4204982d2a2937261e648c20fe80d256dfb47bda27b420e76697897b0b0ebb42c140b4e8bf0f27dfee64c946039739467b073cf60d923a12c4f96d1c7da6
]
# 2. 将node2加入到共识节点
> addMiner 6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4
{
    "code":1,
    "msg":"success"
}
# 3. 查看共识节点列表
> getMinerList
[
    9217e87c6b76184cf70a5a77930ad5886ea68aefbcce1909bdb799e45b520baa53d5bb9a5edddeab94751df179d54d41e6e5b83c338af0a19c0611200b830442,
    227c600c2e52d8ec37aa9f8de8db016ddc1c8a30bb77ec7608b99ee2233480d4c06337d2461e24c26617b6fd53acfa6124ca23a8aa98cb090a675f9b40a9b106,
    7a50b646fcd9ac7dd0b87299f79ccaa2a4b3af875bd0947221ba6dec1c1ba4add7f7f690c95cf3e796296cf4adc989f4c7ae7c8a37f4505229922fb6df13bb9e,
    8b2c4204982d2a2937261e648c20fe80d256dfb47bda27b420e76697897b0b0ebb42c140b4e8bf0f27dfee64c946039739467b073cf60d923a12c4f96d1c7da6,
    6dc585319e4cf7d73ede73819c6966ea4bed74aadbbcba1bbb777132f63d355965c3502bed7a04425d99cdcfb7694a1c133079e6d9b0ab080e3b874882b95ff4 # 新加入节点
]

```

**查看新加入节点出块情况**

通过以上操作可看出，node2已成功加入group2，可通过`tail -f node2/log/* | grep "g:2.*++"`查看node2的group2是否出块正常：

```bash
$ tail -f node2/log/log_2019021118.* | grep "g:2.*++"
info|2019-02-11 18:41:31.625599| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=9,tx=0,myIdx=1,hash=c8a1ed9c...
info|2019-02-11 18:41:36.642582| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=9,tx=0,myIdx=1,hash=0fdee324...

```

此时可向group2发交易，查看node2的group2共识情况：

```bash
$ bash transTest.sh 10 2
Send transaction:  1
{"id":83,"jsonrpc":"2.0","result":"0xd834f909861599c475fa0a04b4afa4759b20fdaea683dd9692bbf79541f16b01"}
Send transaction:  2
{"id":83,"jsonrpc":"2.0","result":"0x037e6136f6df56545093f8abe6b3718d03fbc85c626058e15cb64ee829ea9c42"}
Send transaction:  3
{"id":83,"jsonrpc":"2.0","result":"0x62580d15b3f2b4066fd4bebe5bc9916c9c32a45c853e56f6b0c9c32f7d8dbe3d"}
Send transaction:  4
{"id":83,"jsonrpc":"2.0","result":"0x2f78a6698ca04744eeb716ac65785e3f0193d540a60a40a9ffe708fe679bf1bd"}
Send transaction:  5
{"id":83,"jsonrpc":"2.0","result":"0x48806e767fad50b7601afa3338909c469476792bbbadecb3a52b7d0f66c41f8b"}
Send transaction:  6
{"id":83,"jsonrpc":"2.0","result":"0xb356e13b925edf797f15c6e04599514e447936f53e292c5b3afda020d93ce58b"}
Send transaction:  7
{"id":83,"jsonrpc":"2.0","result":"0x3d39c17f521b9dcc2e06c54b368840b9f13cfe409996faf7233a2d013158d317"}
Send transaction:  8
{"id":83,"jsonrpc":"2.0","result":"0xea04ce0dfd5b1937e9622cc77c2eb5891f2b88153d108a85b09a942f7459f7b9"}
Send transaction:  9
{"id":83,"jsonrpc":"2.0","result":"0x200a352da5b2d8e686f6d7e0697fa96ab1000917de4ac403033991b9ce7de5fb"}
Send transaction:  10
{"id":83,"jsonrpc":"2.0","result":"0xc7805160fcea2398ed718252f8bd93358addb913ccd8cde7acb72031be86c8fb"}

$ 查看node2节点group2的共识情况
tail -f node2/log/* | grep "g:2.*Report"
info|2019-02-11 18:53:20.708366| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=9,idx=3,hash=80c98d31...,next=10,tx=1,myIdx=1
info|2019-02-11 18:53:20.767243| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=10,idx=0,hash=43f377d9...,next=11,tx=4,myIdx=1
info|2019-02-11 18:53:20.814001| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=11,idx=0,hash=86e29c96...,next=12,tx=1,myIdx=1
info|2019-02-11 18:53:20.863162| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=12,idx=1,hash=801d29d4...,next=13,tx=3,myIdx=1
info|2019-02-11 18:53:20.875290| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=13,idx=2,hash=fdb2a0a7...,next=14,tx=1,myIdx=1

```


## CASE2：物理多链

物理多链搭建方法与星形拓扑区块链网络搭建方法类似，以搭建四节点二群组物理多链为例：

- 群组1(group1)：包括四个节点，节点IP均为127.0.0.1
- 群组2(group2): 包括四个节点，节点IP均为127.0.0.1

为了演示物理多链扩容流程，这里先仅创建group1。
物理多链场景中，节点加入和退出群组操作与星形组网拓扑类似，这里不再赘述。

### 构建单群组四节点区块链系统

**用build_chain脚本生成单群组四节点区块链系统：**

```bash
# 获取fisco-bcos二进制文件
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh)

# 获取build_chain.sh脚本
curl https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh -o build_chain.sh

# 构建单节点物理多链
bash build_chain.sh -l "127.0.0.1:4" -e bin/fisco-bcos -o multi_nodes
Generating CA key...
==============================================================
Generating keys ...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
Generating configurations...
Processing IP:127.0.0.1 Total:4 Agency:agency Groups:1
==============================================================
[INFO] FISCO-BCOS Path   : bin/fisco-bcos
[INFO] Start Port        : 30300 20200 8545
[INFO] Server IP         : 127.0.0.1:4
[INFO] State Type        : storage
[INFO] RPC listen IP     : 127.0.0.1
[INFO] Output Dir        : /data/multi_nodes
[INFO] CA Key Path       : /data/multi_nodes/cert/ca.key
==============================================================
[INFO] All completed. Files in /data/multi_nodes

```

**启动所有节点**

```bash
# 进入节点目录
$ cd /data/multi_nodes/127.0.0.1
$ bash start_all.sh

# 查看进程情况
$ ps aux | grep fisco-bcos
app       55028  0.9  0.0 986384  6624 pts/2    Sl   20:59   0:00 /data/multi_nodes/127.0.0.1/node0/../fisco-bcos -c config.ini
app       55034  0.8  0.0 986104  6872 pts/2    Sl   20:59   0:00 /data/multi_nodes/127.0.0.1/node1/../fisco-bcos -c config.ini
app       55041  0.8  0.0 986384  6584 pts/2    Sl   20:59   0:00 /data/multi_nodes/127.0.0.1/node2/../fisco-bcos -c config.ini
app       55047  0.8  0.0 986396  6656 pts/2    Sl   20:59   0:00 /data/multi_nodes/127.0.0.1/node3/../fisco-bcos -c config.ini

```

**查看节点出块情况**

```bash
# 查看node0出块情况
$ tail -f node0/log/* | grep "g:1.*++"
info|2019-02-11 20:59:52.065958| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=da72649e...
info|2019-02-11 20:59:56.076414| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=042ac123...

# 查看node1出块情况
$ tail -f node1/log/* | grep "g:1.*++" 
info|2019-02-11 20:59:54.070297| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=0,hash=11c9354d...
info|2019-02-11 20:59:58.081463| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=0,hash=87e60f9a...

# 查看node2出块情况
$ tail -f node2/log/* | grep "g:1.*++" 
info|2019-02-11 20:59:55.073124| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=1,hash=b65cbac8...

# 查看node3出块情况
$ tail -f node3/log/* | grep "g:1.*++" 
info|2019-02-11 20:59:53.067702| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=0467e5c4...
info|2019-02-11 20:59:57.078769| [g:1][p:264][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=6a410d79...

```


### 将group2加入区块链

物理多链场景中，多个group的genesis配置文件几乎相同，但[group].index不同，为群组号。

**生成新群组配置：**

```bash
# 拷贝group1的配置
$ cp node0/conf/group.1.genesis group.2.genesis

# 修改群组ID
$ vim group.2.genesis
[group]
    index=2

# 将配置拷贝到各个节点
$ cp group.2.genesis node0/conf
$ cp group.2.genesis node1/conf
$ cp group.2.genesis node2/conf
$ cp group.2.genesis node3/conf

# 重启区块链系统
$ bash stop.sh
$ bash start.sh

```

### 查看群组出块情况

```bash
# 查看node0 group2出块情况
$ tail -f node0/log/* | grep "g:2.*++" 
info|2019-02-11 21:13:28.541596| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=2,hash=f3562664...

# 查看node1 group2出块情况
$ tail -f node1/log/* | grep "g:2.*++"
info|2019-02-11 21:13:30.546011| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=0,hash=4b17e74f...

# 查看node2 group2出块情况
$ tail -f node2/log/* | grep "g:2.*++" 
info|2019-02-11 21:13:59.653615| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=1,hash=90cbd225...

# 查看node3 group2出块情况
$ tail -f node3/log/* | grep "g:2.*++" 
info|2019-02-11 21:14:01.657428| [g:2][p:520][CONSENSUS][SEALER]++++++++Generating seal on,blkNum=1,tx=0,myIdx=3,hash=d7dcb462...

```

### 向群组发交易

使用`transTest.sh`脚本向group1和group2发交易，验证区块链网络是否正常：

```bash
# ...向group1发交易，并查看节点共识情况...
bash transTest.sh 10 1
Send transaction:  1
{"id":83,"jsonrpc":"2.0","result":"0x24827ef7b0bed013123d9981ff61ba0a1761747a475e187467e70d9ff20c0714"}
Send transaction:  2
{"id":83,"jsonrpc":"2.0","result":"0x42058b83924248adbedf2e8f65e60b0986d378ee5255b89fd72cd76f5b0d07bf"}
Send transaction:  3
{"id":83,"jsonrpc":"2.0","result":"0xc2374f64c4297ad048af4296081386f1afaf9bd35bb27d60955cf78af050546e"}
Send transaction:  4
{"id":83,"jsonrpc":"2.0","result":"0xb038b6053f492468a7c1861c0c5b73f54c45b9a83a265ce298a38425ac9fba81"}
Send transaction:  5
{"id":83,"jsonrpc":"2.0","result":"0x69edf64d594ca06f2fc0403c376c3afe768dd555e3e40d3e15d50719a9feb258"}
Send transaction:  6
{"id":83,"jsonrpc":"2.0","result":"0xe03bd47a7dac8da7a23893bd2c3cabd74e5861c9861aafce6acd2576c558b30f"}
Send transaction:  7
{"id":83,"jsonrpc":"2.0","result":"0x54e1b6622097f5d0f68c06081e39a826dee04a55c7f9b5b44ca7199bea9ae4fd"}
Send transaction:  8
{"id":83,"jsonrpc":"2.0","result":"0xcb63cd6741e60954df1942137b0088a4bf65c0278c65ccecabbd618c1df99bc8"}
Send transaction:  9
{"id":83,"jsonrpc":"2.0","result":"0x6243dd68f094129ab5e5f16521dcdd3a96bb6cb8928ca007a6c6515b9da95eec"}
Send transaction:  10
{"id":83,"jsonrpc":"2.0","result":"0xabde478f55efd1af85510294f3e8022452764f4610bd6835b514b62f43826e2a"}

# 查看节点共识情况
$ tail -f node0/log/log_2019021121.12.log | grep "g:1.*Report"
info|2019-02-11 21:14:57.216548| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=1,idx=3,hash=be961c98...,next=2,tx=1,myIdx=2
info|2019-02-11 21:14:57.272258| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=2,idx=2,hash=883be222...,next=3,tx=2,myIdx=2
info|2019-02-11 21:14:57.279292| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=3,idx=2,hash=fa21924a...,next=4,tx=3,myIdx=2
info|2019-02-11 21:14:57.296429| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=4,idx=0,hash=8687f8fc...,next=5,tx=1,myIdx=2
info|2019-02-11 21:14:57.341029| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=5,idx=0,hash=24915a96...,next=6,tx=1,myIdx=2
info|2019-02-11 21:14:57.345303| [g:1][p:264][CONSENSUS][PBFT]^^^^^Report:,num=6,idx=1,hash=a538e99e...,next=7,tx=2,myIdx=2

# ...向group2发交易，并查看节点共识情况...
 bash transTest.sh 10 2
Send transaction:  1
{"id":83,"jsonrpc":"2.0","result":"0xf8278a38d9243bfc6c7c146cb222fe13a7f8436b7df169bd1a90f7e17954c6a6"}
Send transaction:  2
{"id":83,"jsonrpc":"2.0","result":"0x43dca46e44c7658bb86db15d555081869e0157bd8ff7bc901fb3f648ead13d8e"}
Send transaction:  3
{"id":83,"jsonrpc":"2.0","result":"0xc592570bd3cc3cbf1a5f6ee53e611a2a8bfc3936721e12b9a0c4ab1a94e22bc6"}
Send transaction:  4
{"id":83,"jsonrpc":"2.0","result":"0x78d3789f3319b9eaddaa75bc5295a360c5efd20ab1582adddbb80a19a5e2c360"}
Send transaction:  5
{"id":83,"jsonrpc":"2.0","result":"0x697aa967ad1ff7d74d23a45443a92eebd9bae9b44bf860c86f33a361d4e6d8ca"}
Send transaction:  6
{"id":83,"jsonrpc":"2.0","result":"0x8152d6031438026eb5fea2edacb65aa1619adec9f2c1784c2481ad5026c35cd9"}
Send transaction:  7
{"id":83,"jsonrpc":"2.0","result":"0x278f13d6f0a85a0663d9fc2aba586f700a80f44671f33e69f4aa7fab42769849"}
Send transaction:  8
{"id":83,"jsonrpc":"2.0","result":"0x49c225438daca7bcc361808db196ec9c90d93a5ba8024743cceb1a2f2a33ddda"}
Send transaction:  9
{"id":83,"jsonrpc":"2.0","result":"0x3f000f57fbfa90a0f12d644add1930a9789d1e3f810e23c1bb3b35c179513055"}
Send transaction:  10
{"id":83,"jsonrpc":"2.0","result":"0xc22ccf6b0a42046a495e5eabac8bea57fdbecd2fade2d8c97531e127b6170212"}

# 查看节点共识情况
$ tail -f node0/log/log_2019021121.12.log | grep "g:2.*Report"
info|2019-02-11 21:15:25.310565| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=1,idx=3,hash=5d006230...,next=2,tx=1,myIdx=2
info|2019-02-11 21:15:25.357435| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=2,idx=2,hash=2420d572...,next=3,tx=1,myIdx=2
info|2019-02-11 21:15:25.404987| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=3,idx=2,hash=44545d5e...,next=4,tx=3,myIdx=2
info|2019-02-11 21:15:25.447702| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=4,idx=3,hash=6c384cec...,next=5,tx=1,myIdx=2
info|2019-02-11 21:15:25.632791| [g:2][p:520][CONSENSUS][PBFT]^^^^^Report:,num=5,idx=1,hash=f234bf5e...,next=6,tx=4,myIdx=2

```
