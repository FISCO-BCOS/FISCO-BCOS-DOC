# Configuration Introduction

Tag: "c-sdk`` ``config``

----------

`bcos-c-sdk 'supports the initialization of configuration objects and configuration files.:

- Configuration Object Initialization:
  - `void* bcos_sdk_create(struct bcos_sdk_c_config* config)`
- Configuration file initialization:
  - `void* bcos_sdk_create_by_config_file(const char* config_file)`

This section describes the configuration object 'struct bcos _ sdk _ c _ config' and the configuration file 'config _ file'。

## Configuration Item

Source code:

- [github link](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/bcos-c-sdk/bcos_sdk_c_common.h#L70)
- [gitee link](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/bcos-c-sdk/bcos_sdk_c_common.h#L70)

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

**Field Meaning:**

### `bcos_sdk_c_endpoint`

- Function
  - connection 'ip:port`

- Field
  - `host`: node 'rpc' connection, supports' ipv4 'and' ipv6 'formats**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `port`: node 'rpc' port

### `bcos_sdk_c_cert_config`

- Function:
  - 'ssl 'connection certificate configuration, valid when' ssl _ type 'is' ssl'

- Field:
  - `ca_cert`: The root certificate can be configured in two ways: file path and file content. For more information, see the 'is _ cert _ path' field.**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `node_cert`: The 'sdk' certificate supports both file path and file content. For more information, see the 'is _ cert _ path' field.**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `node_key`: The 'sdk' private key, which supports both file path and file content. For details, see the 'is _ cert _ path' field.**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**

### `bcos_sdk_c_sm_cert_config`

- Function:
  - Configuration of 'ssl' connection certificate, valid when 'ssl _ type' is' sm _ ssl'

- Field:
  - `ca_cert`: The national secret root certificate supports two methods: file path and file content.**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `node_cert`: 'sdk 'national secret signature certificate, supports two methods of file path and file content, refer to the' is _ cert _ path 'field**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `node_key`: 'sdk 'state-secret signature private key, supports two methods of file path and file content, see' is _ cert _ path 'field**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `en_node_key`: The 'sdk' encryption certificate supports both file path and file content. For more information, see the 'is _ cert _ path' field.**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `en_node_crt`: The 'sdk' encryption private key supports both file path and file content. For more information, see the 'is _ cert _ path' field.**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**

### `bcos_sdk_c_config`

- Field:
  - `thread_pool_size`: Thread pool size, which is used to process network messages.
  - `message_timeout_ms`: Message timeout
  - `peers`: connection list,**注意: Use 'malloc' initialization to ensure that you can use 'free' release**
  - `peers_count`: Connection list size
  - `disable_ssl`: Mask 'ssl', 0: No, 1: Yes
  - `ssl_type`: 'ssl 'type, supports' ssl 'and' sm _ ssl '**注意: Use 'strdup' or 'malloc' initialization to ensure that you can use 'free' release**
  - `is_cert_path`: The configuration item of the 'ssl' connection certificate is the file path or file content, 0:Content, 1:Path
  - `bcos_sdk_c_cert_config`: Certificate configuration for 'ssl' connection, valid when 'ssl _ type' is' ssl'**注意: Use 'malloc' initialization to ensure that you can use 'free' release**
  - `bcos_sdk_c_sm_cert_config`: The certificate configuration of the 'ssl' connection. Valid when 'ssl _ type' is' sm _ ssl'**注意: Use 'malloc' initialization to ensure that you can use 'free' release**

## Profile

The fields in the configuration file correspond to the fields in the configuration object.

- 'ssl 'connection profile

Sample Configuration: [github](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/config/config_sample.ini) [gitee](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/config/config_sample.ini)

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

- State Secret 'ssl' connection configuration file

Sample Configuration: [github](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/config/sm_config_sample.ini) [gitee](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/config/sm_config_sample.ini)

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

## Initialization example

- How to configure objects: `bcos-c-sdk/sample/rpc/rpc.c`
  - [github link](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/rpc/rpc.c#L66)
  - [gitee link](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/rpc/rpc.c#L66)

- Profile: `bcos-c-sdk/sample/eventsub/eventsub.c`
  - [github link](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/eventsub/eventsub.c#L83)
  - [gitee link](https://gitee.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/eventsub/eventsub.c#L83)
