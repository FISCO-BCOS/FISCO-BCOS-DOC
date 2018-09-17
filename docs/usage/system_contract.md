# 系统合约

系统合约是FISCO BCOS区块链内置的智能合约。一条链对应唯一的系统合约。系统合约实现了对链的控制和管理。如节点注册，机构准入等等。

系统合约是一组合约的集合，包括：

* 系统代理合约
* 节点管理合约
* 注销证书合约
* 权限管理合约
* 全网配置合约

**节点相关**

系统合约在创世节点生成时，脚本已自动将其部署到链上，并设置了节点config.json文件中的systemproxyaddress来指向系统合约的地址。重新更新systemproxyaddress的节点需重新启动才能生效。

**操作相关**

配置：在操作前，需用脚本``` set_proxy_address.sh```配置需要操作的链。

操作目录：``` FISCO-BCOS/tools/systemcontract ```

## 系统代理合约

系统代理合约是系统合约的统一入口。

它提供了路由名称到合约地址的映射关系。

源码路径：systemcontract/SystemProxy.sol

**接口说明**

| 接口名             | 输入              | 输出              | 备注            |
| --------------- | --------------- | --------------- | ------------- |
| 获取路由信息 getRoute | 路由名称            | 路由地址、缓存标志位、生效块号 | 无             |
| 注册路由信息setRoute  | 路由名称、路由地址、缓存标志位 | 无               | 若该路由名称已存在，则覆盖 |

web3调用示例如下（可参看systemcontract/deploy.js）：

```js
 console.log("register NodeAction.....");
 func = "setRoute(string,address,bool)";
 params = ["NodeAction", NodeAction.address, false];
 receipt = await web3sync.sendRawTransaction(config.account, config.privKey, SystemProxy.address, func, params);
```

**工具使用方法**

查看所有系统合约信息：

```shell
babel-node tool.js SystemProxy
```

示例输出如下：

```log
{ HttpProvider: 'http://127.0.0.1:8701',
  Ouputpath: './output/',
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
Soc File :SystemProxy
Func :undefined
SystemProxy address 0x210a7d467c3c43307f11eda35f387be456334fed
-----------------SystemProxy route----------------------
0 )TransactionFilterChain=>0xea8425697a093606309eb85e4447d6f333cff2fe,false,395
1 )ConfigAction=>0x09e4f1b4fa1713339f5aa17b40fa6f9920c7b278,false,396
2 )NodeAction=>0xcc46c245e6cca918d43bf939bbb10a8c0988548f,false,397
3 )CAAction=>0x8ab1175c6e7edb40dd0ed2a52ceaa94afb135a64,false,398
4 )ContractAbiMgr=>0x707024221d2433067b768c4be3a005c5ece8df40,false,399
5 )ConsensusControlMgr=>0x007f2c2751bbcd6c9a630945a87a3bc2af38788c,false,400
6 )FileInfoManager=>0xe0caa8103ea05b5ce585c05d8112051a0b213acf,false,401
7 )FileServerManager=>0xe585cc5b8ca7fb174a0560bf79eea7398efaf014,false,402
```

输出中即是当前系统路由表的所有路由信息。

## 节点管理合约

节点管理合约主要功能是维护网络中节点列表。
网络中节点加入或退出都需要与节点管理合约进行交互。

源码路径：systemcontract/NodeAction.sol

**接口说明**

| 接口名               | 输入                    | 输出   | 备注            |
| ----------------- | --------------------- | ---- | ------------- |
| 节点入网 registerNode | 节点ID、、节点名称、机构、节点证书序列号 | 布尔结果 | 若该节点ID已存在，则忽略 |
| 节点出网 cancelNode   | 节点ID                  | 布尔结果 | 若该路由名称不存在，则忽略 |

web3调用示例如下（可参看systemcontract/tool.js）：

```js
var instance=getAction("NodeAction");
var func = "registerNode(string,string,string,string)";
var params = [node.id,node.name,node.agency,node.caHash];
var receipt = web3sync.sendRawTransaction(config.account, config.privKey, instance.address, func, params);
```

**工具使用方法**

请参看 注册记账节点、退出记账节点。

## 注销证书合约

注销证书合约主要功能是维护注销证书信息列表。

源码路径：systemcontract/CAAction.sol

**（1）接口说明**

| 接口名        | 输入              | 输出               | 备注            |
| ---------- | --------------- | ---------------- | ------------- |
| 登记 add     | 证书序列号、证书公钥、节点名称 | 布尔结果             | 若该证书信息不存在，则新建 |
| 移除 remove  | 证书序列号           | 布尔结果             | 若该证书不存在，则忽略   |
| 查询证书信息 get | 证书序列号           | 证书序列号、公钥、节点名称、块号 | 无             |

web3调用示例如下（可参看systemcontract/tool.js）：

```js
var instance=getAction("CAAction");
var func = "add(string,string,string)";
var params = [ca.serial,ca.pubkey,ca.name]; 
var receipt = web3sync.sendRawTransaction(config.account, config.privKey, instance.address, func, params);ig.account, config.privKey, instance.address, func, params);
```

**工具使用方法**

查看注销证书列表

```shell
babel-node tool.js CAAction all
```

登记注销证书

```shell
babel-node tool.js CAAction add
```

移除注销证书

```shell
babel-node tool.js CAAction remove
```

## 权限管理合约

权限管理合约是对区块链权限模型的实现。

一个外部账户只属于一个角色，一个角色拥有一个权限项列表。

一个权限项由合约地址加上合约接口来唯一标识。

源码路径：systemcontract/AuthorityFilter.sol	交易权限Filter

 systemcontract/Group.sol

**接口说明**

| 合约         | 接口名                       | 输入                             | 输出   | 备注   |
| ---------- | ------------------------- | ------------------------------ | ---- | ---- |
| 角色         | 设置用户权限组 权限项 setPermission | 合约地址、合约接口、权限标记                 | 无    | 无    |
|            | 获取权限标记 getPermission      | 合约地址、合约接口                      | 权限标记 | 无    |
| 交易权限Filter | 设置用户所属角色 setUserGroup     | 用户外部账户、用户所属角色合约                | 无    | 无    |
|            | 交易权限检查 process            | 用户外部账户、交易发起账户、合约地址、合约接口、交易输入数据 |      | 无    |

web3调用示例如下（可参看systemcontract/deploy.js）：

```js
var GroupReicpt= await web3sync.rawDeploy(config.account, config.privKey, "Group");
var Group=web3.eth.contract(getAbi("Group")).at(GroupReicpt.contractAddress);
#......省略若干行......
abi = getAbi0("Group");
params  = ["Group","Group","",abi,GroupReicpt.contractAddress];
receipt = await web3sync.sendRawTransaction(config.account, config.privKey, ContractAbiMgrReicpt.contractAddre
ss, func, params);
```

**工具使用方法**

检查用户外部账户权限

```shell
babel-node tool.js AuthorityFilter 用户外部账户、合约地址、合约接口
```

**自主定制**

继承TransactionFilterBase实现新的交易Filter合约。并通过addFilter接口将新Filter注册入TransactionFilterChain即可。

## 全网配置合约

全网配置合约维护了区块链中部分全网运行配置信息。

目标是为了通过交易的全网共识来达成全网配置的一致更新。

源码路径：systemcontract/ConfigAction.sol

**全网配置项说明**

| 配置项                  | 说明                     | 默认值           | 推荐值           |
| -------------------- | ---------------------- | ------------- | ------------- |
| maxBlockHeadGas      | 块最大GAS                 | 2,000,000,000 | 2,000,000,000 |
| intervalBlockTime    | 块间隔(ms)                | 1000          | 1000          |
| maxBlockTranscations | 块最大交易数                 | 1000          | 1000          |
| maxNonceCheckBlock   | 交易nonce检查最大块范围         | 1000          | 1000          |
| maxBlockLimit        | blockLimit超过当前块号的偏移最大值 | 1000          | 1000          |
| maxTranscationGas    | 交易的最大gas               | 30,000,000    | 30,000,000    |
| CAVerify             | CA验证开关                 | false         | false         |

**接口说明**

| 接口名       | 输入      | 输出     | 备注           |
| --------- | ------- | ------ | ------------ |
| 设置配置项 set | 配置项、配置值 | 无      | 若配置表中已存在，则覆盖 |
| 查询配置值 get | 配置项     | 配置值、块号 | 无            |

web3调用示例如下（可参看systemcontract/tool.js）：

```js
var func = "set(string,string)";
var params = [key,value];
var receipt = web3sync.sendRawTransaction(config.account, config.privKey, instance.address, func, params);
console.log("config :"+key+","+value);
```

**使用方法**

查询配置项

```shell
babel-node tool.js ConfigAction get 配置项
```

设置配置项

```shell
babel-node tool.js ConfigAction set 配置项 配置值
```