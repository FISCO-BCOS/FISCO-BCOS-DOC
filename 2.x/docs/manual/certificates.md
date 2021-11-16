# 获取和续期证书

标签：``获取证书`` ``开发手册`` ``x509证书`` ``CFCA证书`` ``CA`` ``SDK证书`` ``证书检测脚本``

----

FISCO BCOS网络采用面向CA的准入机制，支持任意多级的证书结构，保障信息保密性、认证性、完整性、不可抵赖性。

FISCO BCOS使用[x509协议的证书格式](https://en.wikipedia.org/wiki/X.509)，根据现有业务场景，默认采用三级的证书结构，自上而下分别为链证书、机构证书、节点证书。

在多群组架构中，一条链拥有一个链证书及对应的链私钥，链私钥由联盟链委员会共同管理。联盟链委员会可以使用机构的证书请求文件`agency.csr`，签发机构证书`agency.crt`。

机构私钥由机构管理员持有，可以对机构下属节点签发节点证书。

节点证书是节点身份的凭证，用于与其他持有合法证书的节点间建立SSL连接，并进行加密通讯。

sdk证书是sdk与节点通信的凭证，机构生成sdk证书，允许sdk与节点进行通信。

FISCO BCOS节点运行时的文件后缀介绍如下：

| 后缀 | 说明 |
| :-: | :-: |
| .key | 私钥文件|
| .crt | 证书文件 |
| .csr  | 证书请求文件 |

使用第三方证书部署节点的说明，可以参考[使用CFCA证书部署节点](./cfca.md)

## 角色定义

FISCO BCOS的证书结构中，共有四种角色，分别是联盟链委员会管理员、机构、节点和SDK。

### 联盟链委员会

* 联盟链委员会管理链的私钥，并根据机构的证书请求文件`agency.csr`为机构颁发机构证书。

```bash
ca.crt 链证书
ca.key 链私钥
```

FISCO BCOS进行SSL加密通信时，拥有相同链证书`ca.crt`的节点才可建立连接。

### 机构

* 机构管理员管理机构私钥，可以颁发节点证书和sdk证书。

```bash
ca.crt 链证书
agency.crt 机构证书
agency.csr 机构证书请求文件
agency.key 机构私钥
```

### 节点/SDK

* FISCO BCOS节点包括节点证书和私钥，用于建立节点间SSL加密连接；
* SDK包括SDK证书和私钥，用于与区块链节点建立SSL加密连接。


```bash
ca.crt #链证书
node.crt #节点证书
node.key #节点私钥
sdk.crt #SDK证书
sdk.key #SDK私钥
```

节点证书`node.crt`包括节点证书和机构证书信息，节点与其他节点/SDK通信验证时会用自己的私钥`node.key`对消息进行签名，并发送自己的`node.crt`至对方进行验证

## 证书生成流程

FISCO BCOS的证书生成流程如下，用户也可以使用[企业部署工具](../enterprise_tools/operation.md)生成相应证书

### 生成链证书

* 联盟链委员会使用openssl命令请求链私钥`ca.key`，根据ca.key生成链证书`ca.crt`

### 生成机构证书

* 机构使用openssl命令生成机构私钥`agency.key`
* 机构使用机构私钥`agency.key`得到机构证书请求文件`agency.csr`，发送`agency.csr`给联盟链委员会
* 联盟链委员会使用链私钥`ca.key`，根据得到机构证书请求文件`agency.csr`生成机构证书`agency.crt`，并将机构证书`agency.crt`发送给对应机构

### 生成节点/SDK证书

* 节点生成私钥`node.key`和证书请求文件`node.csr`，机构管理员使用私钥`agency.key`和证书请求文件`node.csr`为节点颁发证书。同理，可用相同的方式为SDK生成证书

## 节点证书续期操作

完成证书续期前推荐使用[证书检测脚本](../enterprise_tools/operation.html#handshake-failed)对证书进行检测。

当证书过期时，需要用户使用对当前节点私钥重新签发证书，操作如下：

假设用户证书过期的节点目录为`~/mynode`，节点目录如下：

```bash
mynode
├── conf
│   ├── ca.crt
│   ├── group.1.genesis
│   ├── group.1.ini
│   ├── node.crt #节点证书过期，需要替换
│   ├── node.key #节点私钥，证书续期需要使用
│   └── node.nodeid
├── config.ini
├── scripts
│   ├── load_new_groups.sh
│   └── reload_whitelist.sh
├── start.sh
└── stop.sh
```

设用户机构证书目录为`~/myagency`，目录如下：

```bash
agency
├── agency.crt #机构证书，证书续期需要使用
├── agency.key #机构私钥，证书续期需要使用
├── agency.srl
├── ca.crt
└── cert.cnf
```

续期操作如下：

- 使用节点私钥生成证书请求文件，请将`~/mynode/node/conf/node.key`修改为你自己的节点私钥，将`~/myagency/cert.cnf`替换为自己的证书配置文件

```bash
openssl req -new -sha256 -subj "/CN=RenewalNode/O=fisco-bcos/OU=node" -key ~/mynode/node/conf/node.key -config ~/myagency/cert.cnf -out node.csr
```

操作完成后会在当前目录下生成证书请求文件`node.csr`。

- 查看证书请求文件

```bash
cat node.csr
```

操作完成后显示如下：

```bash
-----BEGIN CERTIFICATE REQUEST-----
MIIBGzCBwgIBADA6MRQwEgYDVQQDDAtSZW5ld2FsTm9kZTETMBEGA1UECgwKZmlz
Y28tYmNvczENMAsGA1UECwwEbm9kZTBWMBAGByqGSM49AgEGBSuBBAAKA0IABICU
KLP9GFRF6bBz+pfHCl1ifqzqrPiVoSPtwubXx+NRAI502EENMpnLqaXWm+OyadKz
PqUneVDQ6U+CvgY2IPygKTAnBgkqhkiG9w0BCQ4xGjAYMAkGA1UdEwQCMAAwCwYD
VR0PBAQDAgXgMAoGCCqGSM49BAMCA0gAMEUCIQDa8PzS1sCdk+rWgEsaOdvBnY+z
NDw6LU44WHCtrW6iNQIgY7Ne4EpAvPGmMOXalJsvYm2Xy6Bm9MlL7NEIP9Y0ai0=
-----END CERTIFICATE REQUEST-----
```

- 使用机构私钥和机构证书对证书请求文件node.csr签发新证书，请将`~/myagency/agency.key`修改为你自己的机构私钥，请将`~/myagency/agency.crt`修改为你自己的机构证书

```bash
openssl x509 -req -days 3650 -sha256 -in node.csr -CAkey ~/myagency/agency.key -CA ~/myagency/agency.crt -out node.crt -CAcreateserial -extensions v3_req -extfile ~/myagency/cert.cnf
```

成功会有如下显示

```bash
Signature ok
subject=/CN=RenewalNode/O=fisco-bcos/OU=node
Getting CA Private Key
```

操作完成后会在当前目录下生成续期后的证书`node.crt`。

- 查看节点新证书

```bash
cat ./node.crt
```

操作完成后显示如下：

```bash
-----BEGIN CERTIFICATE-----
MIICQDCCASigAwIBAgIJALm++fKF6UmXMA0GCSqGSIb3DQEBCwUAMDcxDzANBgNV
BAMMBmFnZW5jeTETMBEGA1UECgwKZmlzY28tYmNvczEPMA0GA1UECwwGYWdlbmN5
MB4XDTE5MDkyNjEwMjEyNVoXDTI5MDkyMzEwMjEyNVowOjEUMBIGA1UEAwwLUmVu
ZXdhbE5vZGUxEzARBgNVBAoMCmZpc2NvLWJjb3MxDTALBgNVBAsMBG5vZGUwVjAQ
BgcqhkjOPQIBBgUrgQQACgNCAASAlCiz/RhURemwc/qXxwpdYn6s6qz4laEj7cLm
18fjUQCOdNhBDTKZy6ml1pvjsmnSsz6lJ3lQ0OlPgr4GNiD8oxowGDAJBgNVHRME
AjAAMAsGA1UdDwQEAwIF4DANBgkqhkiG9w0BAQsFAAOCAQEAVvLUYeOJBfr1bbwp
E2H2QTb4phgcFGvrW5tqfvDvKaVGrSjJowZPKX+ruWFRQAZJBCc3/4M0Q1PYlWpB
R5a9Tpc7ebmUVltY7/GqASlDExdt2nqSvLxOKWgE++FveCdJzOEGuuttTZxjWFhQ
Yr9rPlKhzhEo2jM0lFIxdoCrG/WkcKmzJEyHdVwxLr2FOF9q9e9O9xyUkt2QRBGD
T4dIOeLRK6V1pnNkbBNRYG+tGMq2nBUPCAKJbV1LnhaNNRRbE5z7I4JkRnLHea6P
1VIiwnmbv9a3aM7lsnisPAz8PY5Ddmflo87UiL02J2UnQmq+gtAB9C9DUROGbSH5
Q6CXDA==
-----END CERTIFICATE-----
```

- 将机构证书添加到节点证书末尾

由于fisco-bcos使用三级证书结构，需要将机构证书和节点证书合并

```bash
cat ~/myagency/agency.crt >> ./node.crt
```

- 查看合并后的节点新证书

```bash
cat ./node.crt
```

操作完成后显示如下：

```bash
-----BEGIN CERTIFICATE-----
MIICQDCCASigAwIBAgIJALm++fKF6UmXMA0GCSqGSIb3DQEBCwUAMDcxDzANBgNV
BAMMBmFnZW5jeTETMBEGA1UECgwKZmlzY28tYmNvczEPMA0GA1UECwwGYWdlbmN5
MB4XDTE5MDkyNjEwMjEyNVoXDTI5MDkyMzEwMjEyNVowOjEUMBIGA1UEAwwLUmVu
ZXdhbE5vZGUxEzARBgNVBAoMCmZpc2NvLWJjb3MxDTALBgNVBAsMBG5vZGUwVjAQ
BgcqhkjOPQIBBgUrgQQACgNCAASAlCiz/RhURemwc/qXxwpdYn6s6qz4laEj7cLm
18fjUQCOdNhBDTKZy6ml1pvjsmnSsz6lJ3lQ0OlPgr4GNiD8oxowGDAJBgNVHRME
AjAAMAsGA1UdDwQEAwIF4DANBgkqhkiG9w0BAQsFAAOCAQEAVvLUYeOJBfr1bbwp
E2H2QTb4phgcFGvrW5tqfvDvKaVGrSjJowZPKX+ruWFRQAZJBCc3/4M0Q1PYlWpB
R5a9Tpc7ebmUVltY7/GqASlDExdt2nqSvLxOKWgE++FveCdJzOEGuuttTZxjWFhQ
Yr9rPlKhzhEo2jM0lFIxdoCrG/WkcKmzJEyHdVwxLr2FOF9q9e9O9xyUkt2QRBGD
T4dIOeLRK6V1pnNkbBNRYG+tGMq2nBUPCAKJbV1LnhaNNRRbE5z7I4JkRnLHea6P
1VIiwnmbv9a3aM7lsnisPAz8PY5Ddmflo87UiL02J2UnQmq+gtAB9C9DUROGbSH5
Q6CXDA==
-----END CERTIFICATE-----
-----BEGIN CERTIFICATE-----
MIIC/zCCAeegAwIBAgIJAKK0/dNnUmlqMA0GCSqGSIb3DQEBCwUAMDUxDjAMBgNV
BAMMBWNoYWluMRMwEQYDVQQKDApmaXNjby1iY29zMQ4wDAYDVQQLDAVjaGFpbjAe
Fw0xOTA5MjYwOTU4NDFaFw0yOTA5MjMwOTU4NDFaMDcxDzANBgNVBAMMBmFnZW5j
eTETMBEGA1UECgwKZmlzY28tYmNvczEPMA0GA1UECwwGYWdlbmN5MIIBIjANBgkq
hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuPqz154aXw4t+dcRl+aOz3X7yy0PUymm
DqMq3O7OeWXWYa8MWss5GBGWa2SL6puX/uryZJUUYcmSDwAo7Rsrf8zmbiHqouEC
liy01IqM+9jE7/IywRpRZO7W/QNrv9vRXxDJsr120vs760aMRKWD6UCd7bOQ/m/H
N8VC66r3cvcqey1q49idwOnhh5g80921MFlvu30Rire8kzckzUDr/SV3yt036tZs
D+9l/jHRc/tWo38nkiPy3DIm2oOlrNeJ4+IHnXOfxQxOwsiAeFluxtCq/ZFh4pTL
5lJZTo7bzRcORLOdz40svwDxJKyrMflhue0kGDC0WMExzzvx2oT14wIDAQABoxAw
DjAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQBlXrFIPQPlKosm2q/O
KktQA04Qh/y6w94Z4bHve0AqzTZn3/tf5q0e9C4f8F/Da+D+nV0GETLtEqRSHT+r
CCAAm78qN9oXmfkt3LvK/YXLNCVB6SSXw8fQx+bfDbIVRB5ivkG1+pmmnh3po1zU
zbrnfdSQi0ZV9MjIPsArjWwkE1i0GkXeiXov305iEX6J5pgu3AMe2RRMwyJiJ6ud
PRPCsF5BN6QrtMubwEnyvyrrX0/drBMtHLMCgecLd/nYMyJ4P15L6UnxC8taQSjM
rAtP3RZrBvBTwXKED0ge/hGIzrO9I1vjfCEuxV3DLlKfGVewuuboW2tYFWGfmrEX
MB7w
-----END CERTIFICATE-----
```

- 将生成的节点证书node.crt替换至节点的conf文件夹下

```bash
cp -f ./node.crt ~/mynode/node/conf
```

- 启动节点

```bash
bash ~/mynode/node/start.sh
```

- 查看节点共识

```bash
tail -f ~/mynode/log/log*  | grep +++
```

正常情况会不停输出`++++Generating seal`，表示共识正常。

通过上述操作，完成了证书续期的操作。

## 四类证书续期简易流程

当整条链的证书均已过期时，需要重新对整条链的证书进行续期操作，续期证书的OpenSSL命令与节点续期操作基本相同，或查阅`build_chain.sh`脚本签发证书的操作，简要步骤如下：

- 使用链私钥`ca.key`重新签发链证书`ca.crt`
- 使用机构私钥`agency.key`生成证书请求文件`agency.csr`
- 使用链私钥`ca.key`和链证书`ca.crt`对证书请求文件`agency.csr`签发得到机构证书`agency.crt`
- 使用节点私钥`node.key`生成证书请求文件`node.csr`
- 使用机构私钥`agency.key`和机构证书`agency.crt`对证书请求文件`node.csr`签发得到节点证书`node.crt`
- 将节点证书和机构证书拼接得到`node.crt`，拼接操作可以参考`节点证书续期操作`
- SDK证书`sdk.crt`签发步骤同节点证书签发
- 使用新生成的链证书`ca.crt`，节点证书`node.crt`替换所有节点conf目录下的证书
