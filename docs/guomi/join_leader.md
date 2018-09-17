# 节点入网

```eval_rst
.. admonition:: 注意事项

   节点入网时，请确保首先注册创世节点
```

## 创世节点入网

FISCO BCOS提供`register_node.sh`工具用于节点入网，创世节点入网过程如下：

```bash
# 进入脚本所在目录(设FISCO-BCOS位于~/mydata目录)
$ cd ~/mydata/FISCO-BCOS/tools/scripts

# -d: 创世节点所在目录
# -g: 创世节点类型是国密版FISCO-BCOS
$ bash ./register_node.sh -d ~/mydata/node0 -g
RUN: babel-node tool.js NodeAction register ~/mydata/node0/data/gmnode.json 
{ HttpProvider: 'http://127.0.0.1:8546',
  Ouputpath: './output/',
  EncryptType: 1,
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
Soc File :NodeAction
Func :register
SystemProxy address 0xee80d7c98cb9a840b9c4df742f61336770951875
node.json=~/mydata/node0/data/gmnode.json
NodeAction address 0x22af893607e84456eb5aea0b277e4dffe260fdcd
send transaction success: 0xbfc83175af76dd7e466b75ecd76cd6fd328a4b700233943a81187ea72b0c6bf7
SUCCESS execution of command: babel-node tool.js NodeAction register ~/mydata/node0/data/gmnode.json 
~/mydata/FISCO-BCOS/tools/scripts
Register Node Success!

# 创世节点配置~/mydata/node0/data/gmnode.json如下：
$ cat ~/mydata/node0/data/gmnode.json
{
 "id":"730195b08dda7b027c9ba5bec8ec19420aa996c7ce72fa0954711d46c1c66ae8c2eeaa5f84d1f7766f21ba3dc822bc6d764fbee14034b19a0cf1c69c7f75e537",
 "name":"",
 "agency":"",
 "caHash":"AF33DEB4033C0D47"
}

```

## 普通节点入网

普通节点入网过程如下：

```bash
# 进入脚本所在目录(设FISCO-BCOS位于~/mydata目录)
$ cd ~/mydata/FISCO-BCOS/tools/scripts

# -d: 普通节点目录，这里是~/mydata/node1
# -g：普通节点类型是国密版FISCO-BCOS，必须设置
$ bash ./register_node.sh -d ~/mydata/node1 -g       
RUN: babel-node tool.js NodeAction register /mydata/node1/data/gmnode.json 
{ HttpProvider: 'http://127.0.0.1:8545',
  Ouputpath: './output/',
  EncryptType: 1,
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
Soc File :NodeAction
Func :register
SystemProxy address 0xee80d7c98cb9a840b9c4df742f61336770951875
node.json=~/mydata/node1/data/gmnode.json
NodeAction address 0x22af893607e84456eb5aea0b277e4dffe260fdcd
send transaction success: 0xc67d4e08a03a7094244e3de100979e1f0e50b7f9d83be5691d3833e3ddfcb97b
SUCCESS execution of command: babel-node tool.js NodeAction register ~/mydata/node1/data/gmnode.json { HttpProvider: 'http://127.0.0.1:8545',
  Ouputpath: './output/',
  EncryptType: 1,
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
Soc File :NodeAction
Func :register
SystemProxy address 0xee80d7c98cb9a840b9c4df742f61336770951875
node.json=~/mydata/node1/data/gmnode.json

~/mydata/FISCO-BCOS/tools/scriptsNodeAction address 0x22af893607e84456eb5aea0b277e4dffe260fdcd
# 查看记账节点信息
RUN: babel-node tool.js NodeAction all
{ HttpProvider: 'http://127.0.0.1:8545',
  Ouputpath: './output/',
  EncryptType: 1,
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
send transaction success: 0x1f923378d2640acad78378ee2f21002213cb9f81dfcb7b0f2e42ea5a960a08e6
SUCCESS execution of command: babel-node tool.js NodeAction register ~/mydata/node1/data/gmnode.json
~/mydata/FISCO-BCOS/tools/scripts
RUN: babel-node tool.js NodeAction all

Soc File :NodeAction
Func :all
SystemProxy address 0xee80d7c98cb9a840b9c4df742f61336770951875
NodeAction address 0x22af893607e84456eb5aea0b277e4dffe260fdcd
NodeIdsLength= 2
----------node 0---------
id=3d4fe4c876cac411d4c7180b5794198fb3b4f3e0814156410ae4184e0a51097a01bf63e431293f30af0c01a57f24477ad1704d8f676bc7e345526ba1735db6a7
name=
agency=
caHash=D14983471F0AC975
Idx=0
blocknumber=30
----------node 1---------
id=9af16c4543919589982932b57bb97b162f8eba522037a95e7b013780911c2b0ffdef775b5387b2a4f4867b1271a06357d15055cde76a0b49e4714b691fdd368a
name=
agency=
caHash=95F1A5C35D8CFFA7
Idx=1
blocknumber=31
SUCCESS execution of command: babel-node tool.js NodeAction all{ HttpProvider: 'http://127.0.0.1:8545',
  Ouputpath: './output/',
  EncryptType: 1,
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
Soc File :NodeAction
Func :all
```

## 查看节点入网情况

FISCO-BCOS提供了`node_all.sh`命令查看记账节点信息：

```bash
$ bash ./node_all.sh 
RUN: babel-node tool.js NodeAction all
{ HttpProvider: 'http://127.0.0.1:8545',
  Ouputpath: './output/',
  EncryptType: 1,
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
Soc File :NodeAction
Func :all
SystemProxy address 0xee80d7c98cb9a840b9c4df742f61336770951875
NodeAction address 0x22af893607e84456eb5aea0b277e4dffe260fdcd
NodeIdsLength= 2
----------node 0---------
id=3d4fe4c876cac411d4c7180b5794198fb3b4f3e0814156410ae4184e0a51097a01bf63e431293f30af0c01a57f24477ad1704d8f676bc7e345526ba1735db6a7
name=
agency=
caHash=D14983471F0AC975
Idx=0
blocknumber=30
----------node 1---------
id=9af16c4543919589982932b57bb97b162f8eba522037a95e7b013780911c2b0ffdef775b5387b2a4f4867b1271a06357d15055cde76a0b49e4714b691fdd368a
name=
agency=
caHash=95F1A5C35D8CFFA7
Idx=1
blocknumber=31
SUCCESS execution of command: babel-node tool.js NodeAction all
```

从输出信息可看出，创世节点和普通节点均成功入网。

## check节点入网情况

使用如下命令检查创世节点入网情况，若输出`+++`等打包信息，表明创世节点入网成功：

```bash
$ tail -f ~/mydata/node0/log/log_2018081220.log | grep +++    
INFO|2018-08-12 20:33:13:431|+++++++++++++++++++++++++++ Generating seal on31e1a94c1feb79a4145272a9c5175636d7c24cf4ed90b0b2f5471e4323e5e89e#34tx:0,maxtx:0,tq.num=0time:1534077193431
INFO|2018-08-12 20:33:15:457|+++++++++++++++++++++++++++ Generating seal ondbfa0c0cac0e39f0d22c0c6fa3c21e77e15a3c31d8c81dac580dfbf95b2f96cb#34tx:0,maxtx:0,tq.num=0time:1534077195457
```


同样地，使用如下命令检查普通节点入网情况，若输出`+++`等打包信息，表明普通节点入网成功：

```bash
$ tail -f ~/mydata/node1/log/log_2018081220.log | grep +++
INFO|2018-08-12 20:33:36:696|+++++++++++++++++++++++++++ Generating seal on17d28b77047be017be9ec7ebd048b3b9b711cf75dcbdc1eabfe9cd57d8d6e7f7#34tx:0,maxtx:0,tq.num=0time:1534077216696
INFO|2018-08-12 20:33:38:718|+++++++++++++++++++++++++++ Generating seal onf082c29bcadab361a1bd88853964f8daac643265e4b1a786d669d58a99ce3833#34tx:0,maxtx:0,tq.num=0time:1534077218718
```


```eval_rst

.. admonition:: congratulations :)

   至此，您已经成功搭建一条可用的国密版FISCO-BCOS链 
    - 更高级的使用方法请参考 `FISCO-BCOS系统合约 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/usage/system_contract.html#>`_
    - 国密版web3sdk配置和使用方法请参考 `国密版web3sdk <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/guomi/config_guomi.html#>`_
```