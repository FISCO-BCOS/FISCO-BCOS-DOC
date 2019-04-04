# 证书说明

FISCO-BCOS网络采用面向CA的准入机制，支持任意多级的证书结构，保障信息保密性、认证性、完整性、不可抵赖性。

FISCO BCOS使用[x509协议的证书格式](https://en.wikipedia.org/wiki/X.509)，根据现有业务场景，默认采用三级的证书结构，自上而下分别为链证书、机构证书、节点证书。

在多群组架构中，一条链拥有一个链证书及对应的链私钥，链私钥由联盟链委员会共同管理。联盟链委员会可以通过根据机构由私钥生成的证书请求文件`agnecy.csr`，签发机构证书`agency.crt`。

机构私钥由机构管理员持有，可以对机构下属节点签发节点证书。

节点证书是节点身份的凭证，并使用该证书与其他节点间建立SSL连接进行加密通讯。

sdk证书是sdk与节点通信的凭证，机构生成sdk证书，允许sdk与节点进行通信。

FISCO BCOS节点运行时的文件后缀介绍如下：

| 后缀 | 说明 |
| :-: | :-: |
| .key | 私钥文件|
| .crt | 证书文件
| .csr  | 证书请求文件 |
| .crl | 证书吊销列表 |

## 角色定义：

FISCO-BCOS的证书结构中，共有四种角色，分别是联盟链委员会管理员、机构、节点和SDK。

### 联盟链委员会：

* 联盟链委员会管理链的私钥，并根据机构的证书请求文件`agency.csr`机构颁发机构证书。

```bash
ca.crt 链证书
ca.key 链私钥
```

FISCO BCOS的验证逻辑中，会根据本地读取的链证书`ca.crt`从而验证对方节点/SDK发送的证书链。

### 机构：

* 机构拥有机构私钥，可以颁发节点证书和sdk证书。

```bash
ca.crt 链证书
agency.crt 机构证书
agency.csr 机构证书请求文件
agency.key 机构私钥
```

机构生成的节点证书会包含机构证书`agency.crt`，节点/SDK在通信验证时会首先根据`ca.crt`验证`agency.crt`。

### 节点/SDK：

* FISCO BCOS的节点和SDK的证书逻辑相同，管理私钥和证书。

```bash
ca.crt 链证书
node.crt 节点/SDK证书
node.key 节点/SDK私钥
```

节点/SDK的证书`node.crt`中包含拼接好的`agency.crt`，节点与其他节点/SDK通信验证时会用自己的私钥`node.key`对消息进行签名，并发送自己的`node.crt`至对方进行验证，

## 证书生成流程：

FISCO BCOS的证书生成流程如下，用户也可以使用[企业部署工具](../enterprise_tools/operation.md)生成相应证书

### 生成链证书：

* 联盟链委员会使用openssl命令请求链私钥`ca.key`，根据ca.key生成链证书`ca.crt`


### 生成机构证书：

* 机构使用openssl命令生成机构私钥`agency.key`
* 机构使用机构私钥`agency.key`得到机构证书请求文件`agency.csr`，发送`agency.csr`给联盟链委员会
* 联盟链委员会使用链私钥`ca.key`，根据得到机构证书请求文件`agency.csr`生成机构证书`agency.crt`。并将机构证书`agency.crt`发送给对应机构

### 生成节点/SDL证书：

* 机构生成节点私钥`node.key`，根据机构证书`agency.crt`和私钥`agency.key`和节点/SDK私钥`node.key`生成节点/SDK证书`node.crt`