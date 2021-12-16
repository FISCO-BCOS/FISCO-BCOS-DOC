# 接口列表

标签：``c-sdk`` ``API``

----------

## 基本操作

### `bcos_sdk_create`

- 原型: `void* bcos_sdk_create(struct bcos_sdk_c_config* config)`
- 功能: 创建`sdk`对象指针
- 参数:
  - config: 配置对象, 参考[配置对象](./config.html#id2)
- 返回值:
  - 返回`sdk`指针, 失败返回`NULL`,可以使用`bcos_sdk_get_last_error`获取错误信息, 参考`bcos_sdk_get_last_error`接口介绍

### `bcos_sdk_create_by_config_file`

- 原型: `void* bcos_sdk_create_by_config_file(const char *config_file)`
- 功能: 创建`sdk`对象指针
- 参数:
  - config_file: 配置文件, 参考[配置文件](./config.html#id3)
- 返回值:
  - 返回`sdk`指针, 失败返回`NULL`,可以使用`bcos_sdk_get_last_error`获取错误信息, 参考`bcos_sdk_get_last_error`接口介绍

### `bcos_sdk_start`

- 函数原型: `void bcos_sdk_start(void* sdk)`
- 功能: 启动`sdk`
- 参数:
  - sdk: `sdk`指针
- 返回:
  - 无, 可以使用`bcos_sdk_get_last_error`确认是否启动成功, 参考`bcos_sdk_get_last_error`介绍

### `bcos_sdk_stop`

- 函数原型: `void bcos_sdk_stop(void* sdk)`
- 功能: 停止`sdk`
  - sdk: `sdk`指针
- 返回:
  - 无

### `bcos_sdk_destroy`

- 函数原型: `void bcos_sdk_destroy(void* sdk)`
- 功能: 停止并且释放`sdk`对象资源
- 参数:
  - sdk: `sdk`指针
- 返回值: 无

### `bcos_sdk_get_last_error`

- 函数原型: `int bcos_sdk_get_last_error()`
- 功能: 获取上一次操作的执行状态, 只对部分同步接口有效, 执行失败时可以使用`bcos_sdk_get_last_error_msg`获取错误描述信息
- 参数:
  - sdk: `sdk`指针
- 返回值:
  - 0: 成功, 其他表示错误码, 可以使用`bcos_sdk_get_last_error_msg`获取错误描述信息

### `bcos_sdk_get_last_error_msg`

- 函数原型: `const char* bcos_sdk_get_last_error_msg()`
- 功能: 获取上一次操作的错误信息描述, 只对部分同步接口有效, 与`bcos_sdk_get_last_error`配合使用
- 参数:
  - sdk: `sdk`指针
- 返回值: 错误描述信息

## RPC接口

### `bcos_rpc_call`

- 函数原型:
  - `void bcos_rpc_call(void* sdk, const char* group, const char* node, const char* to, const char* data,
  bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 调用合约，查询操作，无需共识
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 请求发往的节点名称(节点名称可以通过`getGroupInfo`获取), 值为`NULL`或者空字符串时, 在群组中按最大块高的原则, 随机选择一个节点
  - `to`: 合约地址
  - `data`: 参数编码
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
- 返回值: 空

### `bcos_rpc_send_transaction`

- 函数原型: `void bcos_rpc_send_transaction(void* sdk, const char* group, const char* node, const char* data,
    int proof, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 发送交易，需要区块链共识
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `data`: 交易编码
  - `proof`: 是否返回交易回执证明, 0：不返回，1：返回
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回值: 空

### `bcos_rpc_get_transaction`

- 函数原型: `void bcos_rpc_get_transaction(void* sdk, const char* group, const char* node, const char* tx_hash,int proof, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 根据交易哈希获取交易
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `tx_hash`: 交易哈希
  - `proof`: 是否返回交易证明, 0：不返回, 1：返回
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回值: 空

### `bcos_rpc_get_transaction_receipt`

- 函数原型: `void bcos_rpc_get_transaction_receipt(void* sdk, const char* group, const char* node, const char* tx_hash, int proof, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 根据交易哈希获取交易回执
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `tx_hash`: 交易哈希
  - `proof`: 是否返回交易回执证明, 0: 不返回,1: 返回
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回值: 空

### `bcos_rpc_get_block_by_hash`

- 函数原型: `void bcos_rpc_get_block_by_hash(void* sdk, const char* group, const char* node,const char* block_hash, int only_header, int only_tx_hash, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 根据区块哈希获取区块
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `block_hash`: 区块哈希
  - `only_header`: 是否只获取区块头, 1: 是, 0: 否
  - `only_tx_hash`: 是否只获取区块的交易哈希, 1: 是, 0: 否
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回值: 空

### `bcos_rpc_get_block_by_number`

- 函数原型: `void bcos_rpc_get_block_by_number(void* sdk, const char* group, const char* node, int64_t block_number, int only_header, int only_tx_hash, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 根据块高获取区块
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `block_number`: 区块高度
  - `only_header`: 是否只获取区块头, 1: 是, 0: 否
  - `only_tx_hash`: 是否只获取区块的交易哈希, 1: 是, 0: 否
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回值: 空

### `bcos_rpc_get_block_hash_by_number`

- 函数原型: `void bcos_rpc_get_block_hash_by_number(void* sdk, const char* group, const char* node, int64_t block_number, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 根据块高获取区块哈希
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `block_number`: 区块高度
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空
  
### `bcos_rpc_get_block_limit`

- 函数原型: `int64_t bcos_rpc_get_block_limit(void* sdk, const char* group)`
- 功能: 获取`block limit`
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
- 返回: `>0`时返回`block limit`值, `<=0`表示获取失败, 此时可以通过`bcos_rpc_get_block_number`获取当前块高,然后加固定值`500`就是`block limit`的值

### `bcos_rpc_get_block_number`

- 函数原型: `void bcos_rpc_get_block_number(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组块高
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_code`

- 函数原型: `void bcos_rpc_get_code(void* sdk, const char* group, const char* node, const char* address,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 根据合约地址,查询合约代码
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `address`: 合约地址
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_sealer_list`

- 函数原型: `void bcos_rpc_get_sealer_list(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组共识节点列表
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空
  
### `bcos_rpc_get_observer_list`

- 函数原型: `void bcos_rpc_get_observer_list(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组观察节点列表
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_pbft_view`

- 函数原型: `void bcos_rpc_get_pbft_view(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组的`PBFT`共识的视图
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_pending_tx_size`

- 函数原型: `void bcos_rpc_get_pending_tx_size(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取交易池待打包的交易数量
- 参数；
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_sync_status`

- 函数原型: `void bcos_rpc_get_sync_status(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组的区块同步状态
- 参数；
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明

### `bcos_rpc_get_consensus_status`

- 函数原型: `void bcos_rpc_get_consensus_status(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取节点的共识状态
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明

### `bcos_rpc_get_system_config_by_key`

- 函数原型:`void bcos_rpc_get_system_config_by_key(void* sdk, const char* group, const char* node,const char* key,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:
  - 获取系统配置
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `key`: 配置`key`
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_total_transaction_count`

- 函数原型:`void bcos_rpc_get_total_transaction_count(void* sdk, const char* group, const char* node, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取当前区块高度下的交易总量
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `node`: 节点名称, 参考`bcos_rpc_call`接口对`node`的说明
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空
  
### `bcos_rpc_get_group_peers`

- 函数原型:`void bcos_rpc_get_group_peers(void* sdk, const char* group, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组的网络连接信息
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_peers`

- 函数原型:`void bcos_rpc_get_peers(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取网关的`p2p`网络连接信息
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_group_list`

- 函数原型: `void bcos_rpc_get_group_list(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组列表
- 参数:
  - `sdk`: `sdk`指针
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_group_info`

- 函数原型:`void bcos_rpc_get_group_info(void* sdk, const char* group, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组信息
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 群组ID
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_group_info_list`

- 函数原型:`void bcos_rpc_get_group_info_list(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 获取群组列表详情
- 参数:
  - sdk: `sdk`指针
  - `callback`: 参考`bcos_rpc_call`接口对`context`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

### `bcos_rpc_get_group_node_info`

- 函数原型:`void bcos_rpc_get_group_node_info(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能:获取群组的节点信息
- 参数:
  - sdk: `sdk`指针
  - group: 群组ID
  - node: 节点名称
  - `callback`: 参考`bcos_rpc_call`接口对`callback`的说明
  - `context`: 参考`bcos_rpc_call`接口对`context`的说明
- 返回: 空

## AMOP接口

### `bcos_amop_subscribe_topic`

- 函数原型: `void bcos_amop_subscribe_topic(void* sdk, char** topics, size_t count)`
- 功能: 订阅topic
- 参数:
  - `sdk`: `sdk`指针
  - `topics`: topics内容
  - `count`: topics长度

### `bcos_amop_subscribe_topic_with_cb`

- 函数原型: `void bcos_amop_subscribe_topic_with_cb(void* sdk, const char* topic, bcos_sdk_c_amop_subscribe_cb cb, void* context)`
- 功能: 订阅topic，并设置接收`topic`消息的回调函数
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

- 函数原型: `void bcos_amop_set_subscribe_topic_cb(void* sdk, bcos_sdk_c_amop_subscribe_cb cb, void* context)`
- 功能: 设置默认回调函数,接收的`topic`消息没有单独设置回调函数时,默认回调函数会被调用
- 参数:
  - `sdk`: `sdk`指针
  - `cb`: `topic`回调函数, 参考`bcos_amop_subscribe_topic_with_cb`接口对`cb`的说明
  - `context`: 回调上下文

### `bcos_amop_unsubscribe_topic`

- 函数原型: `void bcos_amop_unsubscribe_topic(void* sdk, char** topics, size_t count)`
- 功能: 取消订阅
- 参数:
  - `sdk`: `sdk`指针
  - `topics`: topics内容
  - `count`: topics长度
  
### `bcos_amop_publish`

- 函数原型: `void bcos_amop_publish(void* sdk, const char* topic, void* data, size_t size, uint32_t timeout,bcos_sdk_c_amop_publish_cb cb, void* context)`
- 功能: 发送topic消息
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

- 函数原型: `void bcos_amop_broadcast(void* sdk, const char* topic, void* data, size_t size)`
- 功能: 发送topic广播消息
- 参数:
  - `sdk`: `sdk`指针
  - `topic`: topic
  - `data`: 消息内容
  - `size`: 消息长度

### `bcos_amop_send_response`

- 函数原型: `void bcos_amop_send_response(void* sdk, const char* peer, const char* seq, void* data, size_t size)`
- 功能: 发送回复消息
- 参数:
  - `sdk`: `sdk`指针
  - `peer`: 接收消息的网络连接标记, 参考`bcos_amop_subscribe_topic_with_cb`接口回调函数`cb`的字段`endpoint`说明
  - `seq`: 消息标记, 参考`bcos_amop_subscribe_topic_with_cb`接口回调函数`cb`的字段`seq`说明
  - `data`: 消息内容
  - `size`: 消息长度

## EventSub接口

### `bcos_event_sub_subscribe_event`

- 函数原型:`const char* bcos_event_sub_subscribe_event(void* sdk, const char* group, const char* params,bcos_sdk_c_struct_response_cb callback, void* context)`
- 功能: 合约事件订阅
- 参数:
  - `sdk`: `sdk`指针
  - `group`: 请求群组ID
  - `params`: 请求参数
  - `callback`: 回调函数
  - `context`: 回调上下文
- 返回值:
  - 合约事件订阅的任务id

### `bcos_event_sub_unsubscribe_event`

- 函数原型: `void bcos_event_sub_unsubscribe_event(void* sdk, const char* id)`
- 功能: 去掉合约事件订阅
- 参数:
  - `sdk`: `sdk`指针
  - `id`: 合约事件订阅的任务id
