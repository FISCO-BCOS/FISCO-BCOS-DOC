# 使用CFCA证书部署节点

标签：``CFCA证书`` ``获取证书`` ``开发手册``

----

使用前建议阅读[证书说明](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/certificates.html)

## 购买前注意事项

- 普通版FISCO BCOS节点使用的节点证书算法为EC secp256k1曲线**(注：由于CFCA目前官网仅支持rsa和sm2证书，建议用户使用普通版FISCO BCOS节点时，使用其他可以签发EC secp256k1曲线的第三方证书机构)**

- 国密版FISCO BCOS节点使用的节点证书算法为SM2，具体操作可以参考[使用CFCA证书部署国密FISCO BCOS](../faq/gm_cfca.md)

- 用户向CFCA购买前请确认签发算法是否正确

- 购买前请确认证书用途、节点信息已经填写正确

- 使用前请确认已经安装openssl 1.0.2k以上版本

- cfca申请的`ca.crt--node.crt`二级证书结构与fisco bcos自带工具生成的三级证书结构互相兼容，用户可直接使用cfca签发的二级证书作为节点或sdk的证书使用

建议用户结合[白名单机制](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/certificate_list.html#id2)一起使用

## 本地生成证书请求文件

### 普通证书操作流程

根据CFCA的要求，生成节点私钥`node.key`和节点证书`node.crt`

生成的私钥会存储在`node.key`中，生成后私钥示例如下：

```bash
-----BEGIN PRIVATE KEY-----
MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgZQE8JAJfs97BAj3mJbme
jSyNG+5kClhuAmXpZ1aI5VyhRANCAASLs7td5X1aDPLynH9HjruPLlovJYx1nIWu
E9mB8iTehZ++qd4b6YWXZoAizCgjvXIRIPEXOSkNaVSzJG7whmgb
-----END PRIVATE KEY-----
```

**注意，节点私钥需要私密存储，部署区块链网络时需要用到私钥，每个节点都需要独立的私钥**

根据CFCA的要求，使用节点私钥`node.key`填写信息模板，采用如下方式生成证书请求文件

```bash
openssl req -new -key node.key -out node.csr
```

**将此步生成的证书请求文件`node.csr`发送给CFCA，得到PEM格式的证书node.crt**，同时，CFCA会给你返回一个验证的根证书`ca.crt`

最终得到的节点证书`node.crt`示例如下：

```bash
-----BEGIN CERTIFICATE-----
MIIC/zCCAeegAwIBAgIJALk+dh2WPTueMA0GCSqGSIb3DQEBCwUAMDUxDjAMBgNV
BAMMBWNoYWluMRMwEQYDVQQKDApmaXNjby1iY29zMQ4wDAYDVQQLDAVjaGFpbjAe
Fw0xOTExMDEwODM0MDZaFw0yOTEwMjkwODM0MDZaMDcxDzANBgNVBAMMBmFnZW5j
eTETMBEGA1UECgwKZmlzY28tYmNvczEPMA0GA1UECwwGYWdlbmN5MIIBIjANBgkq
hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp4AkR1wfCodSiUraqb0zR/QLMGjpzuz5
r+s78Ao9G9oLq4q8NiGNlPOC+vrQZPtFlTzf8Ax+0DaC6L33JHIDTH6GCE/qDS0W
b1sBCZtB02opqHcKjzgRDlL7ITwS5o7wtEMm3bp1ade7rZbYavlBQzSQ4aBgwDt7
8YX8XkqDun4KYDL6EGPq7xDTKpkE81hCU1L4huXdo4HXO16JeabP8J7EEsIfdtUZ
prl/e+QmvgKs7HThjA61OT2spSAFFNh+q48ZbGuEUF2iRQA47wnk/H9zI5phMSt5
DqQNP4D1w5DM7poDsOhc8GpM7gDOmnhC5gmiomBSYiZRmB3jSUQWHwIDAQABoxAw
DjAMBgNVHRMEBTADAQH/MA0GCSqGSIb3DQEBCwUAA4IBAQA9GSvkYPHljWtKSpzV
RqszxyqfpxpH+X1z2LXHrNhE1mVSmcfU74rmu1dOlCOEJcOFTe/XTVUMgB14PWuh
Bpigh/pDMEP3eMLNlKuJhH2SxPmTJTKtAkAg2uu8Q6nr7UJA58ja9PA8clT4YNDO
XnI4r/cEF8qoNBw2Gna8+ENuqlD8IbLMt4JbX0zlmjIL6sJnfRZ8SuOqPANSvJiz
/6j8yys1QX7b66MkGHoUt6mnMzncmO0BCjXOu0DUC4kx57EKLX5jaB3Wm6jJogDE
hOGRKcNXMkct51ISRTJ7Yrn+mP0ELiACDOpM/dbNsqfyxwxQMcmZe2gNHgZcpI1L
C7/4
-----END CERTIFICATE-----
```

## 开始部署FISCO BCOS节点

部署前需要先获取所有节点的证书，并使用[FISCO BCOS企业级部署工具](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/enterprise_tools/tutorial_detail_operation.html)，完成下载安装的操作，操作过程与文档类似。

本节中，我们将部署如下图所示的节点网络：

### 机器环境

每个节点的IP，端口号为如下：

| 机构  | 节点  | 所属群组 | P2P地址         | RPC/channel监听地址   |
| ----- | ----- | -------- | --------------- | --------------------- |
| 机构A | 节点0 | 群组1、2 | 127.0.0.1:30300 | 127.0.0.1:8545/:20200 |
|       | 节点1 | 群组1、2 | 127.0.0.1:30301 | 127.0.0.1:8546/:20201 |
|       | 节点2 | 群组1    | 127.0.0.1:30302 | 127.0.0.1:8547/:20202 |
|       | 节点3 | 群组1    | 127.0.0.1:30303 | 127.0.0.1:8548/:20203 |

```eval_rst
.. important::

    针对云服务器中的vps服务器，RPC监听地址需要写网卡中的真实地址(如内网地址或127.0.0.1)，可能与用户登录的ssh服务器不一致。
```


- 获取generator

```bash
cd ~/
git clone https://github.com/FISCO-BCOS/generator.git

# 若因为网络问题导致长时间无法下载，请尝试以下命令：
git clone https://gitee.com/FISCO-BCOS/generator.git
```

- 安装依赖

```bash
cd ~/generator && bash ./scripts/install.sh
```

### 普通版部署教程

我们假设用户上述过程中生成了4个节点证书，分别放置为~/cert/node_127.0.0.1_30300，~/cert/node_127.0.0.1_30301，~/cert/node_127.0.0.1_30302，~/cert/node_127.0.0.1_30303路径下，每个目录包含以下文件

```bash
node_127.0.0.1_30300 # 节点证书存放文件夹，名称必须为node_ip_port
├── cert_127.0.0.1_30300.crt # 节点证书，名称必须为cert_ip_port.crt
├── node.key # 节点私钥，名称必须为node.key
```

- 拷贝节点证书路径

```bash
cp -r ~/cert/node_127.0.0.1_30300 ./meta
```

```bash
cp -r ~/cert/node_127.0.0.1_30301 ./meta
```

```bash
cp -r ~/cert/node_127.0.0.1_30302 ./meta
```

```bash
cp -r ~/cert/node_127.0.0.1_30303 ./meta
```

- 生成群组创世区块

将上一步所有节点的节点证书`node.crt`拷贝至meta文件夹下

```bash
cp ./meta/node_127.0.0.1_30300/cert_127.0.0.1_30300.crt ./meta
```

```bash
cp ./meta/node_127.0.0.1_30301/cert_127.0.0.1_30301.crt ./meta
```

```bash
cp ./meta/node_127.0.0.1_30302/cert_127.0.0.1_30302.crt ./meta
```

```bash
cp ./meta/node_127.0.0.1_30303/cert_127.0.0.1_30303.crt ./meta
```

- 填写group_genesis.ini，教程中采用默认ip
- 生成群组创世区块

```bash
./generator --create_group_genesis ./group
```

生成的group.1.genesis即为群组创世区块

- 修改节点配置文件

```bash
​```bash
cat > ./conf/node_deployment.ini << EOF
[group]
group_id=2

[node0]
; host ip for the communication among peers.
; Please use your ssh login ip.
p2p_ip=127.0.0.1
; listen ip for the communication between sdk clients.
; This ip is the same as p2p_ip for physical host.
; But for virtual host e.g. vps servers, it is usually different from p2p_ip.
; You can check accessible addresses of your network card.
; Please see https://tecadmin.net/check-ip-address-ubuntu-18-04-desktop/
; for more instructions.
rpc_ip=127.0.0.1
p2p_listen_port=30300
channel_listen_port=20200
jsonrpc_listen_port=8545

[node1]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30301
channel_listen_port=20201
jsonrpc_listen_port=8546

[node2]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30302
channel_listen_port=20202
jsonrpc_listen_port=8547

[node3]
p2p_ip=127.0.0.1
rpc_ip=127.0.0.1
p2p_listen_port=30303
channel_listen_port=20203
jsonrpc_listen_port=8548
EOF
​```
```

- 生成节点

```bash
echo "" >> ./meta/peers.txt
```

```bash
./generator --build_install_package ./meta/peers.txt ./nodeA
```

查看生成节点配置文件夹：

```bash
ls ./nodeA
```

```bash
# 命令解释 此处采用tree风格显示
# 生成的文件夹nodeA信息如下所示，
├── monitor # monitor脚本
├── node_127.0.0.1_30300 # 127.0.0.1服务器 端口号30300的节点配置文件夹
├── node_127.0.0.1_30301
├── node_127.0.0.1_30302 # 127.0.0.1服务器 端口号30300的节点配置文件夹
├── node_127.0.0.1_30303
├── scripts # 节点的相关工具脚本
├── start_all.sh # 节点批量启动脚本
└── stop_all.sh # 节点批量停止脚本
```

机构A启动节点：

```bash
bash ./nodeA/start_all.sh
```

查看节点进程:

```bash
ps -ef | grep fisco
```

后续操作与[使用企业级部署工具](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/enterprise_tools/tutorial_detail_operation.html)相同

## 如何使用国密CFCA证书

国密CFCA证书申请和使用流程与上述过程类似，需额外向CFCA申请证书生成小工具，或使用FISCO BCOS的tassl小工具生成对应的证书请求文件，可以参考开发部署工具，国密节点生成过程使用tassl小工具[build_chain.sh](../manual/build_chain.md)
