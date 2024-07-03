# Environment and Profiles

Tags: "go-sdk" "environment configuration"

----

## Development Environment

- Go development environment

  - Golang >= 1.17
  - The project uses go module for package management, see [Using Go Modules](https://blog.golang.org/using-go-modules)
  -If you have not deployed a Go environment, please refer to [official documentation](https://golang.org/doc/)

- Basic development components

  - Git (required for Windows, Linux and MacOS)
  - Git bash (required for Windows only)

## bcos-c-sdk dynamic library preparation

Go-sdk v3 depends on the bcos-c-sdk dynamic library. You need to download the bcos-c-sdk dynamic library and place the dynamic library in the specified directory。

### Download bcos-c-sdk dynamic library

From [here](https://github.com/FISCO-BCOS/bcos-c-sdk/releases/tag/v3.4.0)Download the dynamic library of the corresponding platform。We provide a script, the default download to the '/ usr / local / lib' directory, if you need to download to other directories, you can use the script '-o' option

```bash
./tools/download_csdk_lib.sh
```

Please place the dynamic library in the '/ usr / local / lib' directory. There is no special operation in the future。If the dynamic library is placed in a custom directory, such as'. / lib ', when used by other machines after native compilation,' go build 'needs to add the' ldflags' parameter, such as' go build -v -ldflags ="-r ${PWD}/lib" main.go`。You can also pass' export LD _ LIBRARY _ PATH = ${PWD}/ lib 'Set the search path for the dynamic library。

## Configuration

Go SDK v3 is implemented by calling the dynamic library of bcos-c-sdk. It provides two initialization methods, one is the configuration file of bcos-c-sdk, and the other is the configuration information passed in through parameters。

### Method 1: Incoming parameters

```go
type Config struct {
    TLSCaFile string / / TLS root certificate file path
    TLSKeyFile string / / TLS private key file path
    TLSCertFile string / / TLS SDK certificate file path
    TLSSmEnKeyFile string / / File path of the encryption private key
    TLSSmEnCertFile string / / State Secret Encryption Certificate File Path
    Is IsSMCrypto bool / / chain a national secret
    PrivateKey [] byte / / private key for signing transactions
    GroupID         string // groupID
    Host string / / Node IP or domain name
    Port int / / node port
    DisableSsl bool / / Whether to disable ssl
}

func DialContext(ctx context.Context, config *Config) (*Client, error)
```

### Method 2: Pass in the configuration file path

```go
/ / configFile specifies the configuration file path, groupID specifies the group ID, and privateKey specifies the private key
func Dial(configFile, groupID string, privateKey []byte) (*Client, error)
```

bcos-c-sdk configuration file example

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

### bcos-c-sdk log configuration

The bcos-c-sdk requires a log configuration file, as shown in the following example:

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

### Private key configuration

'privateKey 'is a 32-byte private key that can be accessed through the' GenerateKey 'of the' crypto 'or' smcrypto 'package()'Method generation, can also be parsed from pem file or hex string。Parsing from a pem file can be done using 'client.LoadECPrivateKeyFromPEM'。

State secret and non-state secret account scripts are available from [get _ account.sh](https://github.com/FISCO-BCOS/console/blob/master-2.0/tools/get_account.sh)and [get _ gm _ account.sh](https://github.com/FISCO-BCOS/console/blob/master-2.0/tools/get_gm_account.sh)Download (If the 'get _ account.sh' script and the 'get _ gm _ account.sh' script cannot be downloaded for a long time due to network reasons, try these two links: [get _ account.sh](https://gitee.com/FISCO-BCOS/console/blob/master-2.0/tools/get_account.sh)and [get _ gm _ account.sh](https://gitee.com/FISCO-BCOS/console/blob/master-2.0/tools/get_gm_account.sh)), the use of the method can refer to [account management](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html)。
