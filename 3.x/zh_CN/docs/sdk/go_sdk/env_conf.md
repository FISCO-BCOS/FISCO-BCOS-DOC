# 环境和配置文件

标签：``go-sdk`` ``环境配置``

----

## 开发环境

- Go开发环境

  - Golang >= 1.17
  - 项目采用 go module 进行包管理，可参考 [Using Go Modules](https://blog.golang.org/using-go-modules)
  - 如果您没有部署过Go环境，可参考 [官方文档](https://golang.org/doc/)

- 基本开发组件

  - Git（Windows、Linux及MacOS需要）
  - Git bash（仅Windows需要）

## bcos-c-sdk动态库准备

go-sdk v3依赖bcos-c-sdk动态库，需要先下载bcos-c-sdk动态库，然后将动态库放到指定目录下。

### 下载bcos-c-sdk动态库

从[这里](https://github.com/FISCO-BCOS/bcos-c-sdk/releases/tag/v3.3.0)下载对应平台的动态库。我们提供了一个脚本，默认下载到`/usr/local/lib`目录下，如果需要下载到其他目录，可以使用脚本的`-o`选项

```bash
./tools/download_csdk_lib.sh -o ./lib
```

如果将动态库放在`/usr/local/lib`目录下，后续无特殊操作，如果放在自定义的目录下，例如`./lib`，则`go build`需要添加`ldflags`参数，例如`go build -v -ldflags="-r ${PWD}/lib" main.go`。在其他机器使用时也需要通过`export LD_LIBRARY_PATH=${PWD}/lib`设置动态库的搜索路径。

## 配置

Go SDK v3通过调用bcos-c-sdk的动态库实现，提供两种初始化方式，一种bcos-c-sdk的配置文件，另一种通过参数传入配置信息。

### 方式一：传入参数

```go
type Config struct {
    TLSCaFile       string // TLS 根证书文件路径
    TLSKeyFile      string // TLS 私钥文件路径
    TLSCertFile     string // TLS SDK证书文件路径
    TLSSmEnKeyFile  string // 国密加密私钥文件路径
    TLSSmEnCertFile string // 国密加密证书文件路径
    IsSMCrypto      bool   // 链是否为国密
    PrivateKey      []byte // 签名交易的私钥
    GroupID         string // groupID
    Host            string // 节点IP或域名
    Port            int    // 节点port
}

func DialContext(ctx context.Context, config *Config) (*Client, error)
```

### 方式二：传入配置文件路径

```go
// configFile 制定配置文件路径，groupID 指定群组ID，privateKey 指定私钥
func Dial(configFile, groupID string, privateKey []byte) (*Client, error)
```

bcos-c-sdk配置文件示例

```ini
[common]
    ; if ssl connection is disabled, default: false
    ; disable_ssl = true
    ; thread pool size for network message sending recving handing
    thread_pool_size = 8
    ; send message timeout(ms)
    message_timeout_ms = 10000
    ;
    send_rpc_request_to_highest_block_node = true

; ssl cert config items,
[cert]
    ; ssl_type: ssl or sm_ssl, default: ssl
    ssl_type = ssl
    ; directory the certificates located in, defaul: ./conf
    ca_path=./conf
    ; the ca certificate file
    ca_cert=ca.crt
    ; the node private key file
    sdk_key=sdk.key
    ; the node certificate file
    sdk_cert=sdk.crt

[peers]
# supported ipv4 and ipv6
    node.0=127.0.0.1:20200
    node.1=127.0.0.1:20201
```

### bcos-c-sdk日志配置

bcos-c-sdk需要一个日志的配置文件，示例如下：

```ini
[log]
    enable=false
    log_path=./log
    ; network statistics interval, unit is second, default is 60s
    stat_flush_interval=60
    ; info debug trace
    level=info
    ; MB
    max_log_file_size=512
```

### 私钥配置

`privateKey`是32字节的私钥，可以通过`crypto`或`smcrypto`包的`GenerateKey()`方法生成，也可以从pem文件或hex字符串中解析。从pem文件解析可以使用`client.LoadECPrivateKeyFromPEM`。

国密和非国密账户脚本可从[get_account.sh](https://github.com/FISCO-BCOS/console/blob/master-2.0/tools/get_account.sh)和[get_gm_account.sh](https://github.com/FISCO-BCOS/console/blob/master-2.0/tools/get_gm_account.sh)下载（若因为网络原因导致长时间无法下载`get_account.sh`脚本和`get_gm_account.sh`脚本，可尝试这两个链接：[get_account.sh](https://gitee.com/FISCO-BCOS/console/blob/master-2.0/tools/get_account.sh)和[get_gm_account.sh](https://gitee.com/FISCO-BCOS/console/blob/master-2.0/tools/get_gm_account.sh)），使用方式可参考[账户管理](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html)。
