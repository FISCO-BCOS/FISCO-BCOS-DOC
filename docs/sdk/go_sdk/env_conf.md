# 环境和配置文件

标签：``go-sdk`` ``环境配置``

----
## 环境

- Go开发环境

  - Golang >= 1.13.6
  - 项目采用 go module 进行包管理，可参考 [Using Go Modules](https://blog.golang.org/using-go-modules)
  - 如果您没有部署过Go环境，可参考 [官方文档](https://golang.org/doc/)

- 基本开发组件

  - Git（Windows、Linux及MacOS需要）
  - Git bash（仅Windows需要）


## 配置文件

Go SDK 的配置文件为一个 TOML 文件，主要包括**网络配置**、**账户配置**以及**链配置**。配置文件 config.toml 示例如下：

```toml
[Network]
Type="channel"
CAFile="ca.crt"
Cert="sdk.crt"
Key="sdk.key"
[[Network.Connection]]
NodeURL="127.0.0.1:20200"
GroupID=1
# [[Network.Connection]]
# NodeURL="127.0.0.1:20200"
# GroupID=2

[Account]
KeyFile=".ci/0x83309d045a19c44dc3722d15a6abd472f95866ac.pem"

[Chain]
ChainID=1
SMCrypto=false
```

### 网络配置

网络配置主要用于设置 **网络连接模式**、**证书文件** 和待连接的 **节点信息**，支持设置多个节点。

- `Type`：是Go SDK与区块链节点建立连接的模式，支持channel和rpc两种方式；
  + `channel`：使用ssl协议建立连接，需要提供ca.crt、sdk.crt、sdk.key证书；
  + `rpc`：使用http协议建立连接，不需要提供证书；
- `CAfile`：CA根证书文件路径，用于验证待连接节点的合法性；
- `Cert`：SDK证书文件路径，用于待连接节点验证SDK的合法性；
- `Key`：SDK私钥文件路径，Cert证书对应的私钥，用于加解密和签名；

- `NodeURL`：待连接节点的URL地址，由IP和port两部分组成；
- `GroupID`：待连接节点所属的群组ID。

```eval_rst
.. note::
    - go-sdk暂不支持使用国密SSL与节点建立连接，使用国密时请将节点config.ini中配置设置为`chain.sm_crypto_channel=false`
    - 国密SSL配置项说明请参考https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/configuration.html#id10
```

### 账户配置

- `KeyFile`：外部账户的私钥文件路径，目前只支持pem格式的私钥文件。国密和非国密账户脚本可从[get_account.sh](https://github.com/FISCO-BCOS/console/blob/master/tools/get_account.sh)和[get_gm_account.sh](https://github.com/FISCO-BCOS/console/blob/master/tools/get_gm_account.sh)下载（若因为网络原因导致长时间无法下载`get_account.sh`脚本和`get_gm_account.sh`脚本，可尝试这两个链接：[get_account.sh](https://gitee.com/FISCO-BCOS/console/blob/master/tools/get_account.sh)和[get_gm_account.sh](https://gitee.com/FISCO-BCOS/console/blob/master/tools/get_gm_account.sh)），使用方式可参考[账户管理](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html)。此外，Go SDK代码也支持生成账号，[参考这里](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html)了解更多。

### 链配置

- `ChainID`：待连接节点所属的链ID，可通过查看节点config.ini配置文件中chain.id配置项获得；
- `SMCrypto`：待连接节点所属链使用的签名算法，ture表示使用国密SM2，false表示使用普通ECDSA。