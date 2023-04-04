# 接口列表

标签：``c-sdk`` ``API``

----------

本章节介绍`c-sdk`的`API`列表，模块列表:

- [基础操作](../c_sdk/api.html#id2)
- [错误处理](../c_sdk/api.html#id3)
- [RPC](../c_sdk/api.html#rpc)
- [AMOP](../c_sdk/api.html#amop)
- [EventSub](../c_sdk/api.html#eventsub)
- [工具类](../c_sdk/api.html#id4)
  - [KeyPair](../c_sdk/api.html#keypair)
  - [ABI编解码](../c_sdk/api.html#abi)
  - [交易构造](../c_sdk/api.html#id5)

## 1. 基础操作

本小节介绍`c-sdk`的基础操作，包括`sdk`对象的创建、启动、停止、释放。

### `bcos_sdk_version`

- 原型:
  - `const char* bcos_sdk_version()`
- 功能:
  - 获取c-sdk的版本以及构建信息
- 参数:
  - 无
- 返回:
  - 字符串类型，包括c-sdk的版本以及构建信息，示例:

  ```shell
  FISCO BCOS C SDK Version : 3.1.0
  Build Time         : 20220915 11:11:11
  Build Type         : Darwin/appleclang/Release
  Git Branch         : main
  Git Commit         : dbc82415510a0e59339faebcd72e540fe408d2d0
  ```

- 注意:
  - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露

### `bcos_sdk_create`

- 原型:
  - `void* bcos_sdk_create(struct bcos_sdk_c_config* config)`
- 功能:
  - 创建`sdk`对象指针
- 参数:
  - config: 配置对象, 参考[配置对象](./config.html#id2)
- 返回:
  - 返回`sdk`指针
  - 失败返回`NULL`,可以调用`bcos_sdk_get_last_error`获取错误信息, 参考`bcos_sdk_get_last_error`接口介绍
- 注意:
  - 创建的`sdk`对象，需要由`bcos_sdk_destroy`接口释放，以免内存泄露

### `bcos_sdk_create_by_config_file`

- 原型:
  - `void* bcos_sdk_create_by_config_file(const char *config_file)`
- 功能:
  - 创建`sdk`对象指针
- 参数:
  - config_file: 配置文件, 参考[配置文件](./config.html#id3)
- 返回:
  - 返回`sdk`指针
  - 失败返回`NULL`,可以调用`bcos_sdk_get_last_error`获取错误信息, 参考`bcos_sdk_get_last_error`接口介绍
- 注意:
  - 创建的`sdk`对象，需要由`bcos_sdk_destroy`接口释放，以免内存泄露

### `bcos_sdk_start`

- 原型:
  - `void bcos_sdk_start(void* sdk)`
- 功能:
  - 启动`sdk`
- 参数:
  - sdk: `sdk`指针
- 返回:
  - 无, 可以使用`bcos_sdk_get_last_error`确认是否启动成功, 参考`bcos_sdk_get_last_error`介绍

### `bcos_sdk_stop`

- 原型:
  - `void bcos_sdk_stop(void* sdk)`
- 功能:
  - 停止`sdk`
- 参数:
  - sdk: `sdk`指针
- 返回:
  - 无

### `bcos_sdk_destroy`

- 原型:
  - `void bcos_sdk_destroy(void* sdk)`
- 功能:
  - 停止并且释放`sdk`
- 参数:
  - sdk: `sdk`指针
- 返回: 无

### `bcos_sdk_c_free`

- 原型:
  - `void bcos_sdk_c_free(void* p)`
- 功能:
  - 释放内存
- 参数:
  - p: 指针
- 返回: 无

## 2. 错误处理

本小节介绍`c-sdk`的错误处理接口。

**注意: 这些接口仅仅对同步调用接口有效，异步接口的错误信息在回调中返回**。

### `bcos_sdk_is_last_opr_success`

- 原型:
  - `int bcos_sdk_is_last_opr_success()`
- 功能:
  - 最近一次操作是否成功，等价于`bcos_sdk_get_last_error`返回结果不为0。
- 参数:
  - 无
- 返回:
  - 0: 操作失败
  - 1: 操作成功

### `bcos_sdk_get_last_error`

- 原型:
  - `int bcos_sdk_get_last_error()`
- 功能:
  - 获取上一次操作的返回状态, 失败时可以调用`bcos_sdk_get_last_error_msg`获取错误描述信息
- 参数:
  - 无
- 返回:
  - 0: 成功, 其他表示错误码, 可以使用`bcos_sdk_get_last_error_msg`获取错误描述信息

### `bcos_sdk_get_last_error_msg`

- 原型:
  - `const char* bcos_sdk_get_last_error_msg()`
- 功能:
  - 获取上一次操作的错误信息描述, 与`bcos_sdk_get_last_error`配合使用
- 参数:
  - 无
- 返回: 错误描述信息

## 3. RPC接口

本小节介绍如何在`c-sdk`中调用`FISCO-BCOS 3.0`的`rpc`接口，与节点交互。

### `bcos_rpc_call`

- 原型:
  - `void bcos_rpc_call(void* sdk, const char* group, const char* node, const char* to, const char* data, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 调用合约，查询操作，无需共识
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 请求发往的节点名称(节点名称可以通过`getGroupInfo`获取), 值为`NULL`或者空字符串时, 在群组中按最高块高的原则, 随机选择一个节点
  - `to`: 合约地址
  - `data`: 编码后的参数
    - 调用`solidity`合约时为`ABI`编码
    - 调用`liquid`合约时为`liquid`编码
  - `callback`: 回调函数, 函数原型:

    ```shell
    typedef void (*bcos_sdk_c_struct_response_cb)(struct bcos_sdk_c_struct_response* resp)
    ```

    `bcos_sdk_c_struct_response`定义及字段含义:

      ```c
      struct bcos_sdk_c_struct_response
      {
          int error;   // 返回状态, 0成功, 其他失败
          char* desc;  // 失败时描述错误信息

          void* data;   // 返回数据, error=0 时有效
          size_t size;  // 返回数据大小, error=0 时有效

          void* context;  // 回调上下文,调用接口时传入的`context`参数
      };
      ```

      **!!!注意: 回调的数据`data`仅在回调线程有效,多线程场景下,用户需自行拷贝保证数据的线程安全**
  - `context`: 回调上下文, 在回调`bcos_sdk_c_struct_response`中`context`字段返回
- 返回:
  - 无

### `bcos_rpc_send_transaction`

- 原型:
  - `void bcos_rpc_send_transaction(void* sdk, const char* group, const char* node, const char* data, int proof, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 发送交易，需要区块链共识
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `data`: 签名的交易，十六进制c风格字符串
  - `proof`: 是否返回交易回执证明, 0：不返回，1：返回
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_transaction`

- 函数原型: `void bcos_rpc_get_transaction(void* sdk, const char* group, const char* node, const char* tx_hash,int proof, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 根据交易哈希获取交易
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `tx_hash`: 交易哈希
  - `proof`: 是否返回交易证明, 0：不返回, 1：返回
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_transaction_receipt`

- 原型:

  ```shell
  void bcos_rpc_get_transaction_receipt(void* sdk, const char* group, const char* node, const char* tx_hash, int proof, bcos_sdk_c_struct_response_cb callback, void* context)
  ```

- 功能:
  - 根据交易哈希获取交易回执
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `tx_hash`: 交易哈希
  - `proof`: 是否返回交易回执证明, 0: 不返回,1: 返回
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_block_by_hash`

- 原型:

  ```shell
  void bcos_rpc_get_block_by_hash(void* sdk, const char* group, const char* node,const char* block_hash, int only_header, int only_tx_hash, bcos_sdk_c_struct_response_cb callback, void* context)
  ```

- 功能:
  - 根据区块哈希获取区块
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `block_hash`: 区块哈希
  - `only_header`: 是否只获取区块头, 1: 是, 0: 否
  - `only_tx_hash`: 是否只获取区块的交易哈希, 1: 是, 0: 否
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_block_by_number`

- 原型:

  ```shell
  void bcos_rpc_get_block_by_number(void* sdk, const char* group, const char* node, int64_t block_number, int only_header, int only_tx_hash, bcos_sdk_c_struct_response_cb callback, void* context)
  ```

- 功能:
  - 根据块高获取区块
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `block_number`: 区块高度
  - `only_header`: 是否只获取区块头, 1: 是, 0: 否
  - `only_tx_hash`: 是否只获取区块的交易哈希, 1: 是, 0: 否
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_block_hash_by_number`

- 原型:
  - `void bcos_rpc_get_block_hash_by_number(void* sdk, const char* group, const char* node, int64_t block_number, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 根据块高获取区块哈希
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `block_number`: 区块高度
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 无
  
### `bcos_rpc_get_block_limit`

- 原型:
  - `int64_t bcos_rpc_get_block_limit(void* sdk, const char* group)`
- 功能:
  - 获取块高限制，创建签名交易时需要使用
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
- 返回:
  - `>0`时返回`block limit`值
  - `<=0`表示获取失败，此时表示查询不到该群组

### `bcos_rpc_get_block_number`

- 原型:
  - `void bcos_rpc_get_block_number(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组块高
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_code`

- 原型:
  - `void bcos_rpc_get_code(void* sdk, const char* group, const char* node, const char* address,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 根据合约地址,查询合约代码
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `address`: 合约地址
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 无

### `bcos_rpc_get_sealer_list`

- 原型:
  - `void bcos_rpc_get_sealer_list(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组共识节点列表
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无
  
### `bcos_rpc_get_observer_list`

- 原型:
  - `void bcos_rpc_get_observer_list(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组观察节点列表
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_pending_tx_size`

- 原型:
  - `void bcos_rpc_get_pending_tx_size(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取交易池待打包的交易数量
- 参数；
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 空

### `bcos_rpc_get_sync_status`

- 原型:
  - `void bcos_rpc_get_sync_status(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组的区块同步状态
- 参数；
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_consensus_status`

- 原型:
  - `void bcos_rpc_get_consensus_status(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取节点的共识状态
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_system_config_by_key`

- 原型:
  - `void bcos_rpc_get_system_config_by_key(void* sdk, const char* group, const char* node,const char* key,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取系统配置
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `key`: 配置`key`
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 空

### `bcos_rpc_get_total_transaction_count`

- 原型:
  - `void bcos_rpc_get_total_transaction_count(void* sdk, const char* group, const char* node, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取当前区块高度下的交易总量
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无
  
### `bcos_rpc_get_group_peers`

- 原型:
  - `void bcos_rpc_get_group_peers(void* sdk, const char* group, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组的网络连接信息
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_peers`

- 原型:
  - `void bcos_rpc_get_peers(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取网关的`p2p`网络连接信息
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_group_list`

- 原型:
  - `void bcos_rpc_get_group_list(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组列表
- 参数:
  - `sdk`: `sdk`指针
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_group_info`

- 原型:
  - `void bcos_rpc_get_group_info(void* sdk, const char* group, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组信息
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_group_info_list`

- 原型:
  - `void bcos_rpc_get_group_info_list(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组列表详情
- 参数:
  - sdk: `sdk`指针
  - `callback`: 参考`bcos_rpc_call`接口对`context`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

### `bcos_rpc_get_group_node_info`

- 原型:
  - `void bcos_rpc_get_group_node_info(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取群组的节点信息
- 参数:
  - sdk: `sdk`指针
  - group: 群组ID
  - node: 节点名称
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回:
  - 无

## 4. AMOP接口

本小节介绍在`c-sdk`使用FISCO-BCOS 3.0`AMOP`功能的接口。

### `bcos_amop_subscribe_topic`

- 原型:
  - `void bcos_amop_subscribe_topic(void* sdk, char** topics, size_t count)`
- 功能:
  - 订阅topic
- 参数:
  - `sdk`: `sdk`指针
  - `topics`: topics内容
  - `count`: topics长度
- 返回:
  - 无

### `bcos_amop_subscribe_topic_with_cb`

- 原型:
  - `void bcos_amop_subscribe_topic_with_cb(void* sdk, const char* topic, bcos_sdk_c_amop_subscribe_cb cb, void* context)`
- 功能:
  - 订阅`topic`，并设置接收`topic`消息的回调函数
- 参数:
  - `sdk`: `sdk`指针
  - `topic`: topic
  - `cb`: 回调函数, 函数原型如下:

    ```shell
    typedef void (*bcos_sdk_c_amop_subscribe_cb)(
    const char* endpoint, const char* seq, struct bcos_sdk_c_struct_response* resp);
    ```

    字段含义:
    - endpoint: 接收消息的网络连接标记, 回复消息调用`bcos_amop_send_response`时需要使用
    - seq: 消息标记, 回复消息调用`bcos_amop_send_response`时需要使用
    - resp: 参考`bcos_rpc_call`接口`callback`对`bcos_sdk_c_struct_response`的说明

  - `context`: 回调上下文, 参考`bcos_rpc_call`接口对`context`的说明
  
### `bcos_amop_set_subscribe_topic_cb`

- 原型:
  - `void bcos_amop_set_subscribe_topic_cb(void* sdk, bcos_sdk_c_amop_subscribe_cb cb, void* context)`
- 功能:
  - 为`topic`设置回调函数，接收到的`topic`消息没有单独设置回调函数时，默认回调函数会被调用
- 参数:
  - `sdk`: `sdk`指针
  - `cb`: `topic`回调函数, 参考`bcos_amop_subscribe_topic_with_cb`接口对`cb`的说明
  - `context`: 回调上下文

### `bcos_amop_unsubscribe_topic`

- 原型:
  - `void bcos_amop_unsubscribe_topic(void* sdk, char** topics, size_t count)`
- 功能:
  - 取消订阅
- 参数:
  - `sdk`: `sdk`指针
  - `topics`: topics内容
  - `count`: topics长度
  
### `bcos_amop_publish`

- 原型:
  - `void bcos_amop_publish(void* sdk, const char* topic, void* data, size_t size, uint32_t timeout,bcos_sdk_c_amop_publish_cb cb, void* context)`
- 功能:
  - 发送`topic`消息
- 参数:
  - `sdk`: `sdk`指针
  - `topic`: topic
  - `data`: 消息内容
  - `size`: 消息长度
  - `timeout`: 超时时间,单位ms
  - `cb`: 回调函数, 函数原型如下:

    ```shell
    typedef void (*bcos_sdk_c_amop_publish_cb)(struct bcos_sdk_c_struct_response* resp)
    ```

  - `context`: 回调上下文

### `bcos_amop_broadcast`

- 原型:
  - `void bcos_amop_broadcast(void* sdk, const char* topic, void* data, size_t size)`
- 功能:
  - 发送`topic`广播消息
- 参数:
  - `sdk`: `sdk`指针
  - `topic`: topic
  - `data`: 消息内容
  - `size`: 消息长度

### `bcos_amop_send_response`

- 原型:
  - `void bcos_amop_send_response(void* sdk, const char* peer, const char* seq, void* data, size_t size)`
- 功能:
  - 发送回复消息
- 参数:
  - `sdk`: `sdk`指针
  - `peer`: 接收消息的网络连接标记, 参考`bcos_amop_subscribe_topic_with_cb`接口回调函数`cb`的字段`endpoint`说明
  - `seq`: 消息标记, 参考`bcos_amop_subscribe_topic_with_cb`接口回调函数`cb`的字段`seq`说明
  - `data`: 消息内容
  - `size`: 消息长度

## 5. EventSub接口

本小节介绍在`c-sdk`使用FISCO-BCOS 3.0`EventSub`事件订阅功能的接口。

### `bcos_event_sub_subscribe_event`

- 原型:
  - `const char* bcos_event_sub_subscribe_event(void* sdk, const char* group, const char* params,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 合约事件订阅
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 请求群组ID
  - `params`: 请求参数，c风格JSON字符串
    - addresses: 字符串数组，订阅Event的合约地址列表，为空时表示所有的合约
    - fromBlock: 整形，初始区块，-1表示从当前最高块开始
    - toBlock: 整形，结束区块，-1表示不限制区块高度，已经是最高块时也继续等待新的区块
    - topics: 字符串数组，订阅的topic列表，为空时表示所有的topic

    示例:

    ```shell
    {
    "addresses": ["6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1"],
    "fromBlock": -1,
    "toBlock": -1,
    "topics": []
    }
    ```

  - `context`: 回调上下文
- 返回:
  - 合约事件订阅的任务id，c风格字符串

### `bcos_event_sub_unsubscribe_event`

- 原型:
  - `void bcos_event_sub_unsubscribe_event(void* sdk, const char* id)`
- 功能:
  - 取消合约事件订阅
- 参数:
  - `sdk`: `sdk`指针
  - `id`: 合约事件订阅的任务id，`bcos_event_sub_subscribe_event`的返回值

## 6. 工具类

本小结介绍`c-sdk`的基础工具类的使用，包括`KeyPair`签名对象、`ABI`编解码、构造签名交易。

### 6.1 `KeyPair`签名对象

- `bcos_sdk_create_keypair`
  - 原型:
    - `void* bcos_sdk_create_keypair(int crypto_type)`
  - 功能:
    - 创建`KeyPair`对象
  - 参数:
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - `KeyPair`对象指针
    - 失败返回`NULL`，使用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - `KeyPair`对象不再使用时需要调用`bcos_sdk_destroy_keypair`接口释放，以免造成内存泄露
- `bcos_sdk_create_keypair_by_private_key`
  - 原型:
    - `void* bcos_sdk_create_keypair_by_private_key(int crypto_type, void* private_key, unsigned length)`
  - 功能:
    - 加载私钥创建`KeyPair`对象
  - 参数:
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
    - private_key: 私钥，字节数组格式
    - length: 数组长度
  - 返回:
    - `KeyPair`对象指针
    - 失败返回`NULL`使用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - `KeyPair`对象不再使用时需要调用`bcos_sdk_destroy_keypair`接口释放，以免造成内存泄露
- `bcos_sdk_create_keypair_by_hex_private_key`
  - 原型:
    - `void* bcos_sdk_create_keypair_by_hex_private_key(int crypto_type, const char* private_key)`
  - 功能:
    - 加载私钥创建`KeyPair`对象
  - 参数:
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
    - private_key: 私钥，十六进制c风格字符串格式
  - 返回:
    - `KeyPair`对象指针
    - 失败返回`NULL`，使用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - `KeyPair`对象不再使用时需要调用`bcos_sdk_destroy_keypair`接口释放，以免造成内存泄露
- `bcos_sdk_get_keypair_type`
  - 原型:
    - `int bcos_sdk_get_keypair_type(void* key_pair)`
  - 功能:
    - 获取`KeyPair`对象类型
  - 参数:
    - key_pair: `KeyPair`对象指针
  - 返回:
    - 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
- `bcos_sdk_get_keypair_address`
  - 原型:
    - `const char* bcos_sdk_get_keypair_address(void* key_pair)`
  - 功能:
    - 获取`KeyPair`对象对应的账户地址
  - 参数:
    - key_pair: `KeyPair`对象指针
  - 返回:
    - 账户地址，十六进制c风格字符串
  - 注意:
    - 返回的字符串不使用时使用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_get_keypair_public_key`
  - 原型:
    - `const char* bcos_sdk_get_keypair_public_key(void* key_pair)`
  - 功能:
    - 获取`KeyPair`对象的公钥字符串
  - 参数:
    - key_pair: `KeyPair`对象指针
  - 返回:
    - 公钥，十六进制c风格字符串
  - 注意:
    - 返回的字符串不使用时使用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_get_keypair_private_key`
  - 原型:
    - `const char* bcos_sdk_get_keypair_private_key(void* key_pair)`
  - 功能:
    - 获取`KeyPair`对象的私钥字符串
  - 参数:
    - key_pair: `KeyPair`对象指针
  - 返回:
    - 私钥，十六进制c风格字符串
  - 注意:
    - 返回的字符串不使用时使用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_destroy_keypair`
  - 原型:
    - `void bcos_sdk_destroy_keypair(void* key_pair)`
  - 功能:
    - 释放`KeyPair`对象
  - 参数:
    - key_pair: `KeyPair`对象指针
  - 返回:
    - 无

### 6.2 `ABI`编解码

- `bcos_sdk_abi_encode_constructor`
  - 原型:
    - `const char* bcos_sdk_abi_encode_constructor(const char* abi, const char* bin, const char* params, int crypto_type)`
  - 功能:
    - 编码构造函数参数
  - 参数:
    - abi: 合约ABI，JSON字符串
    - bin: 合约BIN，十六进制c风格字符串
    - params: 构造函数参数，JSON字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 编码后的参数，十六进制c风格字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露

- `bcos_sdk_abi_encode_method`
  - 原型:
    - `const char* bcos_sdk_abi_encode_method(const char* abi, const char* method_name, const char* params, int crypto_type)`
  - 功能:
    - 编码接口参数
  - 参数:
    - abi: 合约ABI，JSON字符串
    - method_name: 接口名
    - params: 构造函数的参数，JSON字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 编码后的参数，十六进制c风格字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_encode_method_by_method_id`
  - 原型:
    - `const char* bcos_sdk_abi_encode_method_by_method_id(const char* abi, const char* method_id, const char* params, int crypto_type)`
  - 功能:
    - 根据methodID编码参数
  - 参数:
    - abi: 合约ABI，JSON字符串
    - method_id: methodID
    - params: 构造函数的参数，JSON字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 编码后的参数，十六进制c风格字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_encode_method_by_method_sig`
  - 原型:
    - `const char* bcos_sdk_abi_encode_method_by_method_sig(const char* method_sig, const char* params, int crypto_type)`
  - 功能:
    - 根据接口signature编码参数
  - 参数:
    - method_sig: 接口signature
    - params: 构造函数的参数，JSON字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 编码后的参数，十六进制c风格字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_decode_method_input`
  - 原型:
    - `const char* bcos_sdk_abi_decode_method_input(const char* abi, const char* method_name, const char* data, int crypto_type)`
  - 功能:
    - 根据接口名解析输入参数
  - 参数:
    - abi: 合约ABI，JSON字符串
    - method_name: 接口名
    - data: 编码的参数，十六进制c风格字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 解析后的参数，十六进制c风格JSON字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_decode_method_input_by_method_id`
  - 原型:
    - `const char* bcos_sdk_abi_decode_method_input_by_method_id(const char* abi, const char* method_id, const char* data, int crypto_type)`
  - 功能:
    - 根据methodID解析输入参数
  - 参数:
    - abi: 合约ABI
    - method_id: methodID
    - data: ABI编码的参数，十六进制c风格字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 解析后的参数，十六进制c风格JSON字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_decode_method_input_by_method_sig`
  - 原型:
    - `const char* bcos_sdk_abi_decode_method_input_by_method_sig(const char* method_sig, const char* data, int crypto_type)`
  - 功能:
    - 根据接口signature解析输入参数
  - 参数:
    - method_sig: 接口signature
    - data: 编码的参数，十六进制c风格字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 解析后的参数，十六进制c风格JSON字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_decode_method_output`
  - 原型:
    - `const char* bcos_sdk_abi_decode_method_output(const char* abi, const char* method_name, const char* data, int crypto_type)`
  - 功能:
    - 根据接口名解析返回参数
  - 参数:
    - abi: 合约ABI
    - method_name: 接口名
    - data: 编码的返回，十六进制c风格字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 解析后的返回，十六进制c风格JSON字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_decode_method_output_by_method_id`
  - 原型:
    - `const char* bcos_sdk_abi_decode_method_output_by_method_id(const char* abi, const char* method_id, const char* data, int crypto_type)`
  - 功能:
    - 根据methodID解析返回参数
  - 参数:
    - abi: 合约ABI
    - method_id: methodID
    - data: 编码的返回，十六进制c风格字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 解析后的返回，十六进制c风格JSON字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_decode_event`
  - 原型:
    - `const char* bcos_sdk_abi_decode_event(const char* abi, const char* event_name, const char* data, int crypto_type)`
  - 功能:
    - 根据event名解析event参数
  - 参数:
    - abi: 合约ABI
    - event_name: event名
    - data: 编码的返回，十六进制c风格字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 解析后的event参数，十六进制c风格JSON字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_abi_decode_event_by_topic`
  - 原型:
    - `const char* bcos_sdk_abi_decode_event_by_topic(const char* abi, const char* topic, const char* data, int crypto_type)`
  - 功能:
    - 根据topic解析event参数
  - 参数:
    - abi: 合约ABI
    - topic: event topic
    - data: 编码的返回，十六进制c风格字符串
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - 返回:
    - 解析后的event参数，十六进制c风格JSON字符串
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露

### 6.3 构造签名交易

- `bcos_sdk_get_group_wasm_and_crypto`
  - 原型:
    - `void bcos_sdk_get_group_wasm_and_crypto(void* sdk, const char* group_id, int* wasm, int* crypto_type)`
  - 功能:
    - 获取群组的部分基础信息 1. 群组运行`wasm`合约，还是`solidity`合约 2. 群组为国密还是非国密环境
  - 参数:
    - `sdk`: sdk对象，`bcos_sdk_create`或者`bcos_sdk_create_by_config_file`创建
    - `group_id`: 群组ID
    - `wasm`: 返回值，该群组是否运行`wasm`合约
      - 0: 否，群组运行`solidity`合约，
      - 1: 是，群组运行`wasm`合约
    - `crypto_type`: 返回值，该群组是否为国密类型，0: 否，1: 是
  - 返回:
    - 无

- `bcos_sdk_get_group_chain_id`
  - 原型:
    - `const char* bcos_sdk_get_group_chain_id(void* sdk, const char* group_id)`
  - 功能:
    - 获取群组的链ID，构造交易时需使用该参数
  - 参数:
    - `sdk`: sdk对象，`bcos_sdk_create`或者`bcos_sdk_create_by_config_file`创建
    - `group_id`: 群组ID
  - 返回:
    - 群组的链ID
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露

- `bcos_sdk_create_transaction_data`
  - 原型:
    - `void* bcos_sdk_create_transaction_data(const char* group_id, const char* chain_id, const char* to, const char* data, const char* abi, int64_t block_limit)`
  - 功能:
    - 创建`TransactionData`对象，该对象是未签名的交易对象
  - 参数:
    - `group_id`: 群组ID
    - `chain_id`: 链ID，可以调用`bcos_sdk_get_group_chain_id`接口获取群组的链ID
    - `to`: 调用的合约地址，部署合约时设置为空字符串""
    - `data`: ABI编码后的参数，十六进制c风格字符串，参考[ABI编解码](../c_sdk/api.html#abi)
    - `abi`: 合约的ABI，JSON字符串，可选参数，部署合约时可以将合约的ABI传入，默认传入空字符串""
    - `block_limit`: 区块限制，可以调用`bcos_rpc_get_block_limit`接口获取
  - 返回:
    - `TransactionData`对象指针
    - 失败返回`NULL`，使用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - `TransactionData`对象需要调用`bcos_sdk_destroy_transaction_data`接口释放，以免造成内存泄露

- `bcos_sdk_destroy_transaction_data`
  - 原型:
    - `void bcos_sdk_destroy_transaction_data(void* transaction_data)`
  - 功能:
    - 释放`TransactionData`对象
  - 参数:
    - `transaction_data`: `TransactionData`对象指针
  - 返回:
    - 无
- `bcos_sdk_calc_transaction_data_hash`
  - 原型:
    - `const char* bcos_sdk_calc_transaction_data_hash(int crypto_type, void* transaction_data)`
  - 功能:
    - 计算`TransactionData`对象哈希
  - 参数:
    - crypto_type: 类型, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
    - `transaction_data`: `TransactionData`对象指针
  - 返回:
    - `TransactionData`对象哈希
    - 失败返回`NULL`，使用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - **`TransactionData`对象的哈希，也是交易的哈希**
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
- `bcos_sdk_sign_transaction_data_hash`
  - 原型:
    - `const char* bcos_sdk_sign_transaction_data_hash(void* keypair, const char* transcation_hash)`
  - 功能:
    - 交易哈希签名
  - 参数:
    - keypair:`KeyPair`对象，参考[`KeyPair`签名对象](../c_sdk/api.html#keypair)
    - transcation_hash: 交易哈希，由`bcos_sdk_calc_transaction_data_hash`接口生成
  - 返回:
    - 交易签名，字符串类型
    - 失败返回`NULL`，调用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露

- `bcos_sdk_create_signed_transaction_with_signed_data`
  - 原型:

  ```shell
  const char* bcos_sdk_create_signed_transaction_with_signed_data(void* transaction_data, const char* signed_transaction_data, const char* transaction_data_hash, int32_t attribute)
  ```

  - 功能:
    - 创建签名的交易
  - 参数:
    - transaction_data: `TransactionData`对象
    - signed_transaction_data: 交易哈希的签名，十六进制c风格字符串，`bcos_sdk_sign_transaction_data_hash`接口生成
    - transaction_data_hash: 交易哈希，十六进制c风格字符串，`bcos_sdk_calc_transaction_data_hash`接口生成
    - attribute: 交易额外属性，待拓展，默认填0即可
  - 返回:
    - 签名的交易，十六进制c风格字符串
    - 失败返回`NULL`，调用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - 返回的字符串需要调用`bcos_sdk_c_free`释放，以免造成内存泄露

- `bcos_sdk_create_signed_transaction`
  - 原型:

  ```shell
  void bcos_sdk_create_signed_transaction(void* key_pair, const char* group_id, const char* chain_id, const char* to, const char* data, const char* abi, int64_t block_limit, int32_t attribute, char** tx_hash, char** signed_tx)
  ```

  - 功能:
    - 创建签名的交易
  - 参数:
    - key_pair: `KeyPair`对象，参考[`KeyPair`签名对象](../c_sdk/api.html#keypair)
    - group_id: 群组ID
    - chain_id: 链ID，可以调用`bcos_sdk_get_group_chain_id`接口获取群组的链ID
    - to: 调用的合约地址，部署合约时设置为空字符串""
    - data: ABI编码后的参数，参考[ABI编解码](../c_sdk/api.html#abi)
    - abi: 合约的ABI，可选参数，部署合约时可以将合约的ABI传入，默认空字符串""
    - block_limit: 区块限制，可以调用`bcos_rpc_get_block_limit`接口获取
    - attribute: 交易额外属性，待拓展，默认填0即可
    - tx_hash: 返回值，交易哈希，十六进制c风格字符串
    - signed_tx: 返回值，签名的交易，十六进制c风格字符串
  - 返回:
    - 调用`bcos_sdk_get_last_error`接口判断是否成功，0表示成功，其他值表示错误码
  - 注意:
    - 返回的`tx_hash`、`signed_tx`需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
  - **说明**:
    - `bcos_sdk_create_signed_transaction`相当于下面几个接口功能的组合，创建交易、交易哈希、交易签名流程需要分开处理时，使用下面几个接口:
      - `bcos_sdk_create_transaction_data`: 创建`TransactionData`
      - `bcos_sdk_calc_transaction_data_hash`: 计算交易哈希
      - `bcos_sdk_sign_transaction_data_hash`: 交易哈希签名
      - `bcos_sdk_create_signed_transaction_with_signed_data`: 创建签名的交易

- `bcos_sdk_create_transaction_builder_service`
  - 原型:
    - `void* bcos_sdk_create_transaction_builder_service(void* sdk, const char* group_id)`
  - 功能:
    - 创建`TransactionBuilderService`对象，简化构造签名交易的姿势，可以对比`bcos_sdk_create_transaction_data_with_tx_builder_service`与`bcos_sdk_create_transaction_data`接口的差异
  - 参数:
    - sdk: sdk对象指针
    - group_id: 群组ID
  - 返回:
    - `TransactionBuilderService`对象指针
    - 失败返回`NULL`，调用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - `TransactionBuilderService`对象需要使用`bcos_sdk_destroy_transaction_builder_service`销毁，以免造成内存泄露
- `bcos_sdk_destroy_transaction_builder_service`
  - 原型:
    - `bcos_sdk_destroy_transaction_builder_service(void* service)`
  - 功能:
    - 销毁`TransactionBuilderService`对象
  - 参数:
    - `TransactionBuilderService`对象指针
  - 返回:
    - 无
- `bcos_sdk_create_transaction_data_with_tx_builder_service`
  - 原型:
    - `void* bcos_sdk_create_transaction_data_with_tx_builder_service(void* tx_builder_service, const char* to, const char* data, const char* abi)`
  - 功能:
    - 创建`TransactionData`对象
  - 参数:
    - tx_builder_service: `TransactionBuilderService`对象指针
    - to: 调用的合约地址，部署合约时设置为空字符串""
    - data: ABI编码后的参数，参考[ABI编解码](../c_sdk/api.html#abi)
    - abi: 合约的ABI，可选参数，部署合约时可以将合约的ABI传入，默认空字符串""
  - 返回:
    - `TransactionData`对象指针
    - 失败返回`NULL`使用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
  - 注意:
    - 创建的`TransactionData`对象需要由`bcos_sdk_destroy_transaction_data`接口释放，以免造成内存泄露
- `bcos_sdk_create_signed_transaction_with_tx_builder_service`
  - 原型:

  ```shell
  void bcos_sdk_create_signed_transaction_with_tx_builder_service(void*tx_builder_service, void* key_pair, const char*to, const char* data, const char* abi, int32_t attribute, char** tx_hash, char** signed_tx)
  ```

  - 功能:
    - 创建签名的交易
  - 参数:
    - tx_builder_service: `TransactionBuilderService`对象指针
    - key_pair: `KeyPair`对象，参考[`KeyPair`签名对象](../c_sdk/api.html#keypair)
    - to: 调用的合约地址，部署合约时设置为空字符串""
    - data: ABI编码后的参数，参考[ABI编解码](../c_sdk/api.html#abi)
    - abi: 合约的ABI，可选参数，部署合约时可以将合约的ABI传入，默认空字符串""
    - attribute: 交易额外属性，待拓展，默认填0即可
    - tx_hash: 返回值，交易哈希，十六进制c风格字符串
    - signed_tx: 返回值，签名的交易，十六进制c风格字符串
  - 注意:
    - 返回的`tx_hash`、`signed_tx`需要调用`bcos_sdk_c_free`释放，以免造成内存泄露
