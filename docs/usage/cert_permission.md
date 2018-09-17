# 证书说明
FISCO-BCOS网络采用面向CA的准入机制，保障信息保密性、认证性、完整性、不可抵赖性。

一条链拥有一个链证书及对应的链私钥，链私钥由链管理员拥有。并对每个参与该链的机构签发机构证书，机构证书私钥由机构管理员持有，并对机构下属节点签发节点证书。节点证书是节点身份的凭证，并使用该证书与其他节点间建立SSL连接进行加密通讯。sdk证书是sdk与节点通信的凭证，机构生成sdk证书，允许sdk与节点进行通信。

因此，需要生成链证书、机构证书、节点证书，sdk证书。文件后缀介绍如下：

| 后缀 | 说明 |
| :------: | :------: |
|.key | 私钥 |
|.srl | 文件存储序列号 |
|.csr | Cerificate Signing Request, 证书请求文件 |
|.crt | Certificate 证书 |
|.pubkey | 公钥 |
|.private | 私钥.key编码得到 |
|.p12 | PKCS#12格式来储存密钥 |
|.keystore | client.keystore是用做web3sdk的SSL证书 |
|.crl | Certificate Revocation List 证书吊销列表 |

## 角色定义：
* FISCO-BCOS准入机制中，不同角色拥有不同的密钥与证书文件，共同保障信息保密性、认证性、完整性、不可抵赖性。FISCO-BCOS中，共有四种角色，分别是链管理员，机构，节点和SDK。
### 链管理员：
* 链管理员管理链的私钥，可以向机构颁发机构证书。

```shell
ca.crt 链证书			
ca.key 链私钥			
ca.srl 链序列号
```		

### 机构：
* 机构拥有机构私钥，可以颁发节点证书和sdk证书。

```shell
ca.crt 链证书
agency.crt 机构证书
agency.csr 机构证书请求文件
agency.key 机构私钥
agency.srl 机构序列号
ca-agency.crt 机构和链的证书
```	

### 节点：
* 节点管理节点私钥。

```shell
ca.crt 链证书
node.ca节点证书相关信息，应用于系统合约
node.crt 节点证书
node.csr节点证书请求文件
node.key节点私钥
node.private 节点私钥编码得到
node.pubkey 节点公钥
node.serial 节点序列号
```	

### SDK：
* sdk管理sdk私钥。

```shell
ca.crt 链证书
clent.keysotre 用做web3sdk的SSL证书
keystore.p12 用pkcs#12存储的密钥
sdk.crt sdk证书
sdk.csr sdk证书请求文件
sdk.key sdk私钥
sdk.private sdk私钥编码得到
sdk.pubkey sdk公钥
```	


## 证书生成流程：
FISCO-BCOS的证书生成流程如下，详见[证书操作](https://fisco-bcos-test.readthedocs.io/zh/latest/docs/usage/tools.html)部分

### 生成链证书：
```shell
* 运行生成链证书脚本generate_chain_cert.sh
* 请求链私钥ca.key，用ca.key生成链证书ca.crt
```

### 生成机构证书：
```shell
* 运行机构证书生成脚本generate_agency_cert.sh
* 首先请求机构私钥agency.key
* 用机构私钥agency.key 得到公钥证书原始文件agency.csr
* 用链证书ca.crt，链私钥ca.key，公钥证书原始文件agency.csr生成机构证书agency.crt。机构证书生成不需要机构私钥
```

### 生成节点证书：
```shell
* 运行节点证书生成脚本generate_node_cert.sh
* 首先请求secp256k1模块得到node参数，用这些参数得到node.key私钥
* 然后用node.key得到公钥node.pubkey
* 用私钥node.key得到node公钥证书原始文件node.csr
* 用公钥证书原始文件node.csr，机构私钥agency.key，机构证书agency.crt，node公钥node.pubkey得到node证书node.crt。节点证书生成不需要节点私钥
* 将node.key DER编码写入node.private
* 提取node.key里的数据得到nodeID
* 提取node.crt里的数据得到node.serial
```

### 生成sdk证书：
```shell
* 运行sdk证书生成脚本generate_sdk_cert.sh
* 首先请求secp256k1模块得到sdk参数，用这些参数得到sdk.key私钥
* 然后用sdk.key得到公钥sdk.pubkey
* 用私钥sdk.key得到sdk公钥证书原始文件sdk.csr
* 用公钥证书原始文件sdk.csr，机构私钥agency.key，机构证书agency.crt，sdk公钥sdk.pubkey得到sdk证书sdk.crt
* 将sdk.key DER编码写入sdk.private
* 最后把ca.crt和agency.crt写入ca-agency.crt
```
数字证书就是区块链网络中标志通讯各方身份信息的一串数字，提供了一种在网络上验证通信实体身份的方式，数字证书不是数字身份证，而是身份认证机构盖在数字身份证上的一个章或印（或者说加在数字身份证上的一个签名）。FISCO-BCOS网络采用证书机制，对节点的准入进行管理，从而保证整个网络有效，可靠，安全地进行通信。
