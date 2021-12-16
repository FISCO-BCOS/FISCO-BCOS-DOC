# 配置介绍

标签：``c-sdk`` ``config``

----------

`bcos-c-sdk`支持配置对象和配置文件两种方式的初始化, 接口分别为:

- `void* bcos_sdk_create(struct bcos_sdk_c_config* config)`
- `void* bcos_sdk_create_by_config_file(const char* config_file)`

本节内容介绍`struct bcos_sdk_c_config`对象以及`config_file`配置文件的格式

## 配置对象

源码定义位置: [github链接](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/main/bcos-c-sdk/bcos_sdk_c_common.h#L70)、[gitee链接](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/main/bcos-c-sdk/bcos_sdk_c_common.h#L70)

```shell
struct bcos_sdk_c_config
{
    // common config
    int thread_pool_size;
    int message_timeout_ms;
    int reconnect_period_ms;
    int heartbeat_period_ms;

    // connected peers
    struct bcos_sdk_c_endpoint* peers;
    size_t peers_count;

    // the switch for disable ssl connection
    int disable_ssl;

    // ssl or sm_ssl
    char* ssl_type;
    // cert config items is the content of the cert or the path of the cert file
    int is_cert_path;
    // ssl connection cert, effective with ssl_type is 'ssl'
    struct bcos_sdk_c_cert_config* cert_config;
    // sm ssl connection cert, effective with ssl_type is 'sm_ssl'
    struct bcos_sdk_c_sm_cert_config* sm_cert_config;
};

struct bcos_sdk_c_endpoint
{
    char* host;  // c-style string，end with '\0', ipv4、ipv6 support
    uint16_t port;
};

struct bcos_sdk_c_cert_config
{
    char* ca_cert;  // cert and key should be in pem format
    char* node_key;
    char* node_cert;
};

struct bcos_sdk_c_sm_cert_config
{
    char* ca_cert;  // cert and key should be in pem format
    char* node_key;
    char* node_cert;
    char* en_node_key;
    char* en_node_cert;
};
```

**字段含义:**

### `bcos_sdk_c_endpoint`

- 功能
  - 连接`ip:port`

- 字段
  - `host`: 节点`rpc`连接,支持`ipv4`、`ipv6`格式 **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `port`: 节点`rpc`端口

### `bcos_sdk_c_cert_config`

- 功能:
  - `ssl`连接证书配置, `ssl_type`为`ssl`时有效

- 字段:
  - `ca_cert`: 根证书配置, 支持文件路径和文件内容两种方式, 参考`is_cert_path`字段, **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `node_cert`: `sdk`证书, 支持文件路径和文件内容两种方式,参考`is_cert_path`字段, **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `node_key`: `sdk`私钥, 支持文件路径和文件内容两种方式,参考`is_cert_path`字段, **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**

### `bcos_sdk_c_sm_cert_config`

- 功能:
  - 国密`ssl`连接证书配置, `ssl_type`为`sm_ssl`时有效

- 字段:
  - `ca_cert`: 国密根证, 支持文件路径和文件内容两种方式 **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `node_cert`: `sdk`国密签名证书, 支持文件路径和文件内容两种方式,参考`is_cert_path`字段 **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `node_key`: `sdk`国密签名私钥, 支持文件路径和文件内容两种方式,参考`is_cert_path`字段 **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `en_node_key`: `sdk`国密加密证书, 支持文件路径和文件内容两种方式,参考`is_cert_path`字段 **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `en_node_crt`: `sdk`国密加密私钥, 支持文件路径和文件内容两种方式,参考`is_cert_path`字段 **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**

### `bcos_sdk_c_config`

- 字段:
  - `thread_pool_size`: 线程池大小,该线程池用于处理网络消息
  - `message_timeout_ms`: 消息超时时间
  - `peers`: 连接列表, **注意: 使用`malloc`初始化,确保可以使用`free`释放**
  - `peers_count`: 连接列表大小
  - `disable_ssl`: 是否屏蔽`ssl`, 0: 否, 1: 是
  - `ssl_type`: `ssl`类型, 支持`ssl`和`sm_ssl`两种 **注意: 使用`strdup`或者`malloc`初始化,确保可以使用`free`释放**
  - `is_cert_path`: `ssl`连接证书的配置项为文件路径或者文件内容, 0:内容, 1:路径
  - `bcos_sdk_c_cert_config`: `ssl`连接的证书配置, `ssl_type`为`ssl`时有效 **注意: 使用`malloc`初始化,确保可以使用`free`释放**
  - `bcos_sdk_c_sm_cert_config`: 国密`ssl`连接的证书配置, `ssl_type`为`sm_ssl`时有效 **注意: 使用`malloc`初始化,确保可以使用`free`释放**

## 配置文件

配置文件中的字段与配置对象中的字段对应

[示例配置文件](https://github.com/FISCO-BCOS/bcos-c-sdk/tree/main/sample/config)

- `ssl`连接配置文件

```shell
[common]
    ; if ssl connection is disabled, default: false
    ; disable_ssl = true
    ; thread pool size for network message sending receiving handing
    thread_pool_size = 8
    ; send message timeout(ms)
    message_timeout_ms = 10000

; ssl cert config items,  
[cert]
    ; ssl_type: ssl or sm_ssl, default: ssl
    ssl_type = ssl
    ; directory the certificates located in, default: ./conf
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

- 国密`ssl`连接配置文件

```shell
[common]
    ; if ssl connection is disabled, default: false
    ; disable_ssl = true
    ; thread pool size for network message sending receiving handing
    thread_pool_size = 8
    ; send message timeout(ms)
    message_timeout_ms = 10000

[cert]
    ; ssl_type: ssl or sm_ssl, default: ssl
    ssl_type = sm_ssl
    ; directory the certificates located in, default: ./conf
    ca_path=./conf
    ; the ca certificate file
    sm_ca_cert=sm_ca.crt
    ; the node private key file
    sm_sdk_key=sm_sdk.key
    ; the node certificate file
    sm_sdk_cert=sm_sdk.crt
    ; the node private key file
    sm_ensdk_key=sm_ensdk.key
    ; the node certificate file
    sm_ensdk_cert=sm_ensdk.crt

[peers]
# supported ipv4 and ipv6 
    node.0=127.0.0.1:20200
    node.1=127.0.0.1:20201
```

## 使用示例

`bcos-c-sdk/sample`目录提供了sdk使用的一些示例:

```shell
bcos-c-sdk/sample/
├── CMakeLists.txt
├── amop        # amop示例
├── eventsub    # 合约事件订阅示例
└── rpc         # rpc示例
├── config      # 配置文件示例
```

`bcos-c-sdk/sample/rpc/rpc.c` 通过配置对象方式初始化sdk. [github链接](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/main/sample/rpc/rpc.c#L66)、[gitee链接](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/main/sample/rpc/rpc.c#L66)

`bcos-c-sdk/sample/eventsub/eventsub.c` 通过配置文件初始化sdk. [github链接](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/main/sample/eventsub/eventsub.c#L83)、[gitee链接](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/main/sample/eventsub/eventsub.c#L83)
