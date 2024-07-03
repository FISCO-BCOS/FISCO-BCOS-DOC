# Interface List

Tags: "c-sdk" "API"

----------

This section describes the API list and module list of 'c-sdk':

- [Basic Operation](../c_sdk/api.html#id2)
- [Error Handling](../c_sdk/api.html#id3)
- [RPC](../c_sdk/api.html#rpc)
- [AMOP](../c_sdk/api.html#amop)
- [EventSub](../c_sdk/api.html#eventsub)
- [Tool Class](../c_sdk/api.html#id4)
  - [KeyPair](../c_sdk/api.html#keypair)
  - [ABI Codec](../c_sdk/api.html#abi)
  - [Transaction construction (without type)](../c_sdk/api.html#id5)
  - [Transaction construction (with type)](../c_sdk/api.html#id6)

## 1. Basic operation

This section describes the basic operations of 'c-sdk', including the creation, start, stop, and release of 'sdk' objects。

### `bcos_sdk_version`

- Prototype:
  - `const char* bcos_sdk_version()`
- Function:
  - obtain the version and build information of the c-sdk
- Parameters:
  - None
- Return:
  -String type, including the version and build information of c-sdk, example:

  ```shell
  FISCO BCOS C SDK Version : 3.1.0
  Build Time         : 20220915 11:11:11
  Build Type         : Darwin/appleclang/Release
  Git Branch         : main
  Git Commit         : dbc82415510a0e59339faebcd72e540fe408d2d0
  ```

- Attention:
  - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

### `bcos_sdk_create`

- Prototype:
  - `void* bcos_sdk_create(struct bcos_sdk_c_config* config)`
- Function:
  - create 'sdk' object pointer
- Parameters:
  - config: configuration object, refer to [configuration object](./config.html#id2)
- Return:
  - returns the 'sdk' pointer
  - 'NULL' is returned. You can call 'bcos _ sdk _ get _ last _ error' to obtain the error information. For more information, see 'bcos _ sdk _ get _ last _ error'
- Attention:
  - The created 'sdk' object needs to be released by the 'bcos _ sdk _ destroy' interface to avoid memory leakage

### `bcos_sdk_create_by_config_file`

- Prototype:
  - `void* bcos_sdk_create_by_config_file(const char *config_file)`
- Function:
  - create 'sdk' object pointer
- Parameters:
  - config_file: configuration files, refer to [Configuration Files](./config.html#id3)
- Return:
  - returns the 'sdk' pointer
  - 'NULL' is returned. You can call 'bcos _ sdk _ get _ last _ error' to obtain the error information. For more information, see 'bcos _ sdk _ get _ last _ error'
- Attention:
  - The created 'sdk' object needs to be released by the 'bcos _ sdk _ destroy' interface to avoid memory leakage

### `bcos_sdk_start`

- Prototype:
  - `void bcos_sdk_start(void* sdk)`
- Function:
  - launch 'sdk'
- Parameters:
  - sdk: 'sdk 'pointer
- Return:
  - None. You can use 'bcos _ sdk _ get _ last _ error' to check whether the startup is successful. For details, see 'bcos _ sdk _ get _ last _ error'

### `bcos_sdk_stop`

- Prototype:
  - `void bcos_sdk_stop(void* sdk)`
- Function:
  - Stop 'sdk'
- Parameters:
  - sdk: 'sdk 'pointer
- Return:
  - None

### `bcos_sdk_destroy`

- Prototype:
  - `void bcos_sdk_destroy(void* sdk)`
- Function:
  - Stop and release the 'sdk'
- Parameters:
  - sdk: 'sdk 'pointer
- Return: None

### `bcos_sdk_c_free`

- Prototype:
  - `void bcos_sdk_c_free(void* p)`
- Function:
  - Free up memory
- Parameters:
  - p: Pointer
- Return: None

## 2. Error handling

This section describes the error handling interface of 'c-sdk'。

**注意: These interfaces are only valid for synchronous calling interfaces, and the error message for the asynchronous interface is returned in the callback**。

### `bcos_sdk_is_last_opr_success`

- Prototype:
  - `int bcos_sdk_is_last_opr_success()`
- Function:
  - Whether the last operation was successful, which is equivalent to 'bcos _ sdk _ get _ last _ error'. The returned result is not 0。
- Parameters:
  - None
- Return:
  - 0: Operation failed
  - 1: Operation successful

### `bcos_sdk_get_last_error`

- Prototype:
  - `int bcos_sdk_get_last_error()`
- Function:
  - Obtain the return status of the previous operation. If the operation fails, you can call 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error description
- Parameters:
  - None
- Return:
  - 0: Success, other indicates the error code, you can use 'bcos _ sdk _ get _ last _ error _ msg' to get the error description

### `bcos_sdk_get_last_error_msg`

- Prototype:
  - `const char* bcos_sdk_get_last_error_msg()`
- Function:
  - Obtain the error description of the last operation, and use it with 'bcos _ sdk _ get _ last _ error'
- Parameters:
  - None
- Return: Error Description Information

## 3. RPC interface

This section describes how to call the 'rpc' interface of 'FISCO-BCOS 3.0' in 'c-sdk' to interact with nodes。

### `bcos_rpc_call`

- Prototype:
  - `void bcos_rpc_call(void* sdk, const char* group, const char* node, const char* to, const char* data, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Call contracts, query operations, no consensus required
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: node name, the name of the node to which the request is sent(The node name can be obtained by 'getGroupInfo')When the value is NULL or an empty string, a node is randomly selected according to the principle of the highest block height in the group
  - `to`: Contract Address
  - `data`: Encoded parameters
    - 'ABI' encoding when calling the 'solidity' contract
    - Encode 'liquid' when calling 'liquid' contracts
  - `callback`: callback function, function prototype:

    ```shell
    typedef void (*bcos_sdk_c_struct_response_cb)(struct bcos_sdk_c_struct_response* resp)
    ```

    'bcos _ sdk _ c _ struct _ response 'Definition and field meaning:

      ```c
      struct bcos_sdk_c_struct_response
      {
          int error;   / / Return status, 0 succeeded, others failed
          char* desc;  / / description error message on failure

          void* data;   / / return data, valid when error = 0
          size_t size;  / / Return data size, valid when error = 0

          void* context;  / / The callback context. The 'context' parameter passed in when the interface is called
      };
      ```

      **!!!注意: The callback data 'data' is only valid in the callback thread. In multi-thread scenarios, users need to copy the data themselves to ensure thread safety**
  - `context`: Callback context, returned in the 'context' field of callback 'bcos _ sdk _ c _ struct _ response'
- Return:
  - None

### `bcos_rpc_send_transaction`

- Prototype:
  - `void bcos_rpc_send_transaction(void* sdk, const char* group, const char* node, const char* data, int proof, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Send transactions that require blockchain consensus
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `data`: signed transaction, hex c style string
  - `proof`: Whether to return the transaction receipt proof, 0: do not return, 1: return
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_transaction`

- Function prototype: `void bcos_rpc_get_transaction(void* sdk, const char* group, const char* node, const char* tx_hash,int proof, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get transactions based on transaction hash
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `tx_hash`: Transaction Hash
  - `proof`: Return Proof of Transaction, 0: No Return, 1: Return
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_transaction_receipt`

- Prototype:

  ```shell
  void bcos_rpc_get_transaction_receipt(void* sdk, const char* group, const char* node, const char* tx_hash, int proof, bcos_sdk_c_struct_response_cb callback, void* context)
  ```

- Function:
  - Get transaction receipt based on transaction hash
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `tx_hash`: Transaction Hash
  - `proof`: Return transaction receipt proof, 0: No return, 1: Return
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_block_by_hash`

- Prototype:

  ```shell
  void bcos_rpc_get_block_by_hash(void* sdk, const char* group, const char* node,const char* block_hash, int only_header, int only_tx_hash, bcos_sdk_c_struct_response_cb callback, void* context)
  ```

- Function:
  - Get block based on block hash
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `block_hash`: Block Hash
  - `only_header`: Whether to get only the block header, 1: Yes, 0: 否
  - `only_tx_hash`: Whether to get only the transaction hash of the block, 1: Yes, 0: 否
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_block_by_number`

- Prototype:

  ```shell
  void bcos_rpc_get_block_by_number(void* sdk, const char* group, const char* node, int64_t block_number, int only_header, int only_tx_hash, bcos_sdk_c_struct_response_cb callback, void* context)
  ```

- Function:
  - Get blocks based on block height
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `block_number`: Block height
  - `only_header`: Whether to get only the block header, 1: Yes, 0: 否
  - `only_tx_hash`: Whether to get only the transaction hash of the block, 1: Yes, 0: 否
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_block_hash_by_number`

- Prototype:
  - `void bcos_rpc_get_block_hash_by_number(void* sdk, const char* group, const char* node, int64_t block_number, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get block hash based on block height
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `block_number`: Block height
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return: None
  
### `bcos_rpc_get_block_limit`

- Prototype:
  - `int64_t bcos_rpc_get_block_limit(void* sdk, const char* group)`
- Function:
  - Get block high limit, required when creating signature transactions
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
- Return:
  - `>0 'returns the' block limit 'value
  - `<= 0 'indicates that the group failed to be queried

### `bcos_rpc_get_block_number`

- Prototype:
  - `void bcos_rpc_get_block_number(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get cluster block high
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_code`

- Prototype:
  - `void bcos_rpc_get_code(void* sdk, const char* group, const char* node, const char* address,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - According to the contract address, check the contract code
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `address`: Contract Address
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return: None

### `bcos_rpc_get_sealer_list`

- Prototype:
  - `void bcos_rpc_get_sealer_list(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get group consensus node list
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None
  
### `bcos_rpc_get_observer_list`

- Prototype:
  - `void bcos_rpc_get_observer_list(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get group watch node list
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_pending_tx_size`

- Prototype:
  - `void bcos_rpc_get_pending_tx_size(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get the number of transactions to be packaged in the transaction pool
- Parameters；
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - Empty

### `bcos_rpc_get_sync_status`

- Prototype:
  - `void bcos_rpc_get_sync_status(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get the block synchronization status of the group
- Parameters；
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_consensus_status`

- Prototype:
  - `void bcos_rpc_get_consensus_status(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get the consensus status of the node
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_system_config_by_key`

- Prototype:
  - `void bcos_rpc_get_system_config_by_key(void* sdk, const char* group, const char* node,const char* key,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get system configuration
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `key`: Configure 'key'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - Empty

### `bcos_rpc_get_total_transaction_count`

- Prototype:
  - `void bcos_rpc_get_total_transaction_count(void* sdk, const char* group, const char* node, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get the total amount of transactions at the current block height
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `node`: The node name. For more information, see the description of the 'bcos _ rpc _ call' interface for the 'node'
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None
  
### `bcos_rpc_get_group_peers`

- Prototype:
  - `void bcos_rpc_get_group_peers(void* sdk, const char* group, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get the network connection information of the group
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_peers`

- Prototype:
  - `void bcos_rpc_get_peers(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get the 'p2p' network connection information for the gateway
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_group_list`

- Prototype:
  - `void bcos_rpc_get_group_list(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get group list
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_group_info`

- Prototype:
  - `void bcos_rpc_get_group_info(void* sdk, const char* group, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get group information
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Group ID
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_group_info_list`

- Prototype:
  - `void bcos_rpc_get_group_info_list(void* sdk, bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get group list details
- Parameters:
  - sdk: 'sdk 'pointer
  - `callback`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

### `bcos_rpc_get_group_node_info`

- Prototype:
  - `void bcos_rpc_get_group_node_info(void* sdk, const char* group, const char* node,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Get the node information of the group
- Parameters:
  - sdk: 'sdk 'pointer
  - group: Group ID
  - node: Node Name
  - `callback`: Refer to the description of 'callback' for the 'bcos _ rpc _ call' interface
  - `context`: Refer to the description of 'context' for the 'bcos _ rpc _ call' interface
- Return:
  - None

## 4. AMOP interface

This section describes the interface for using the FISCO-BCOS 3.0 'AMOP' function in the 'c-sdk'。

### `bcos_amop_subscribe_topic`

- Prototype:
  - `void bcos_amop_subscribe_topic(void* sdk, char** topics, size_t count)`
- Function:
  - Subscribe to topic
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `topics`: topics Content
  - `count`: length of topics
- Return:
  - None

### `bcos_amop_subscribe_topic_with_cb`

- Prototype:
  - `void bcos_amop_subscribe_topic_with_cb(void* sdk, const char* topic, bcos_sdk_c_amop_subscribe_cb cb, void* context)`
- Function:
  - Subscribe to 'topic' and set the callback function for receiving 'topic' messages
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `topic`: topic
  - `cb`: callback function, the function prototype is as follows:

    ```shell
    typedef void (*bcos_sdk_c_amop_subscribe_cb)(
    const char* endpoint, const char* seq, struct bcos_sdk_c_struct_response* resp);
    ```

    Field Meaning:
    - endpoint: The network connection tag of the received message. It is required when the reply message calls' bcos _ amop _ send _ response '
    - seq: Message tag, required when 'bcos _ amop _ send _ response' is called in reply message
    - resp: Refer to the description of 'bcos _ sdk _ c _ struct _ response' for the 'callback' interface of 'bcos _ rpc _ call'

  - `context`: callback context. For more information, see the description of 'context' in the 'bcos _ rpc _ call' interface
  
### `bcos_amop_set_subscribe_topic_cb`

- Prototype:
  - `void bcos_amop_set_subscribe_topic_cb(void* sdk, bcos_sdk_c_amop_subscribe_cb cb, void* context)`
- Function:
  - Set a callback function for 'topic'. If no callback function is set for the received 'topic' message, the default callback function is called
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `cb`: 'topic 'callback function, refer to the description of' bcos _ amop _ subscribe _ topic _ with _ cb 'interface for' cb'
  - `context`: Callback Context

### `bcos_amop_unsubscribe_topic`

- Prototype:
  - `void bcos_amop_unsubscribe_topic(void* sdk, char** topics, size_t count)`
- Function:
  - Unsubscribe
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `topics`: topics Content
  - `count`: length of topics
  
### `bcos_amop_publish`

- Prototype:
  - `void bcos_amop_publish(void* sdk, const char* topic, void* data, size_t size, uint32_t timeout,bcos_sdk_c_amop_publish_cb cb, void* context)`
- Function:
  - Send 'topic' message
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `topic`: topic
  - `data`: Message content
  - `size`: Message length
  - `timeout`: timeout, in ms
  - `cb`: callback function, the function prototype is as follows:

    ```shell
    typedef void (*bcos_sdk_c_amop_publish_cb)(struct bcos_sdk_c_struct_response* resp)
    ```

  - `context`: Callback Context

### `bcos_amop_broadcast`

- Prototype:
  - `void bcos_amop_broadcast(void* sdk, const char* topic, void* data, size_t size)`
- Function:
  - Send 'topic' broadcast message
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `topic`: topic
  - `data`: Message content
  - `size`: Message length

### `bcos_amop_send_response`

- Prototype:
  - `void bcos_amop_send_response(void* sdk, const char* peer, const char* seq, void* data, size_t size)`
- Function:
  - Send reply message
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `peer`: The network connection tag of the received message. For more information, see the field 'endpoint' of the 'bcos _ amop _ subscribe _ topic _ with _ cb' callback function 'cb'
  - `seq`: Message tag. For details, see the field 'seq' of the 'bcos _ amop _ subscribe _ topic _ with _ cb' callback function 'cb'
  - `data`: Message content
  - `size`: Message length

## 5. EventSub interface

This section describes the interface for using the 'c-sdk' FISCO-BCOS 3.0 'EventSub' event subscription function。

### `bcos_event_sub_subscribe_event`

- Prototype:
  - `const char* bcos_event_sub_subscribe_event(void* sdk, const char* group, const char* params,bcos_sdk_c_struct_response_cb callback, void* context)`
- Function:
  - Contract event subscription
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `group`: Request Group ID
  - `params`: request parameter, c-style JSON string
    - addresses: String array, a list of contract addresses to subscribe to the Event, indicating all contracts when empty
    - fromBlock: Shaping, initial block, -1 means starting from the current highest block
    - toBlock: Shaping, end block, -1 indicates that the block height is not limited, and it continues to wait for new blocks when it is already the highest block
    - topics: String array, a list of subscribed topics. When empty, all topics are represented

    Example:

    ```shell
    {
    "addresses": ["6849F21D1E455e9f0712b1e99Fa4FCD23758E8F1"],
    "fromBlock": -1,
    "toBlock": -1,
    "topics": []
    }
    ```

  - `context`: Callback Context
- Return:
  - task id of contract event subscription, c-style string

### `bcos_event_sub_unsubscribe_event`

- Prototype:
  - `void bcos_event_sub_unsubscribe_event(void* sdk, const char* id)`
- Function:
  - Cancel contract event subscription
- Parameters:
  - `sdk`: 'sdk 'pointer
  - `id`: The ID of the task to which the contract event is subscribed, and the return value of 'bcos _ event _ sub _ subscribe _ event'

## 6. Tool class

This summary describes the use of the basic tools of 'c-sdk', including 'KeyPair' signature objects, 'ABI' encoding and decoding, and constructing signature transactions。

### 6.1 'KeyPair' Signature Object

- `bcos_sdk_create_keypair`
  - Prototype:
    - `void* bcos_sdk_create_keypair(int crypto_type)`
  - Function:
    - Creates a 'KeyPair' object
  - Parameters:
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - 'KeyPair' object pointer
    - 'NULL' is returned. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - When the 'KeyPair' object is no longer in use, you need to call the 'bcos _ sdk _ destroy _ keypair' interface to release it to avoid memory leakage
- `bcos_sdk_create_keypair_by_private_key`
  - Prototype:
    - `void* bcos_sdk_create_keypair_by_private_key(int crypto_type, void* private_key, unsigned length)`
  - Function:
    - Load the private key to create the 'KeyPair' object
  - Parameters:
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
    - private_key: Private key, byte array format
    - length: Array length
  - Return:
    - 'KeyPair' object pointer
    - Failed to return 'NULL' Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - When the 'KeyPair' object is no longer in use, you need to call the 'bcos _ sdk _ destroy _ keypair' interface to release it to avoid memory leakage
- `bcos_sdk_create_keypair_by_hex_private_key`
  - Prototype:
    - `void* bcos_sdk_create_keypair_by_hex_private_key(int crypto_type, const char* private_key)`
  - Function:
    - Load the private key to create the 'KeyPair' object
  - Parameters:
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
    - private_key: Private key, hexadecimal c-style string format
  - Return:
    - 'KeyPair' object pointer
    - 'NULL' is returned. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - When the 'KeyPair' object is no longer in use, you need to call the 'bcos _ sdk _ destroy _ keypair' interface to release it to avoid memory leakage
- `bcos_sdk_get_keypair_type`
  - Prototype:
    - `int bcos_sdk_get_keypair_type(void* key_pair)`
  - Function:
    - Get 'KeyPair' object type
  - Parameters:
    - key_pair: 'KeyPair 'object pointer
  - Return:
    - Type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
- `bcos_sdk_get_keypair_address`
  - Prototype:
    - `const char* bcos_sdk_get_keypair_address(void* key_pair)`
  - Function:
    - Get the account address corresponding to the 'KeyPair' object
  - Parameters:
    - key_pair: 'KeyPair 'object pointer
  - Return:
    - account address, hex c style string
  - Attention:
    -When the returned string is not used, use 'bcos _ sdk _ c _ free' to release it to avoid memory leakage
- `bcos_sdk_get_keypair_public_key`
  - Prototype:
    - `const char* bcos_sdk_get_keypair_public_key(void* key_pair)`
  - Function:
    - Get the public key string of the 'KeyPair' object
  - Parameters:
    - key_pair: 'KeyPair 'object pointer
  - Return:
    - Public key, hex c style string
  - Attention:
    -When the returned string is not used, use 'bcos _ sdk _ c _ free' to release it to avoid memory leakage
- `bcos_sdk_get_keypair_private_key`
  - Prototype:
    - `const char* bcos_sdk_get_keypair_private_key(void* key_pair)`
  - Function:
    - Get the private key string of the 'KeyPair' object
  - Parameters:
    - key_pair: 'KeyPair 'object pointer
  - Return:
    - Private key, hex c style string
  - Attention:
    -When the returned string is not used, use 'bcos _ sdk _ c _ free' to release it to avoid memory leakage
- `bcos_sdk_destroy_keypair`
  - Prototype:
    - `void bcos_sdk_destroy_keypair(void* key_pair)`
  - Function:
    - Release the 'KeyPair' object
  - Parameters:
    - key_pair: 'KeyPair 'object pointer
  - Return:
    - None

### 6.2 'ABI' Codec

- `bcos_sdk_abi_encode_constructor`
  - Prototype:
    - `const char* bcos_sdk_abi_encode_constructor(const char* abi, const char* bin, const char* params, int crypto_type)`
  - Function:
    - Encode constructor parameters
  - Parameters:
    - abi: Contract ABI, JSON string
    - bin: Contract BIN, hex c-style string
    - params: Constructor parameters, JSON string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - Encoded parameter, hex c style string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_abi_encode_method`
  - Prototype:
    - `const char* bcos_sdk_abi_encode_method(const char* abi, const char* method_name, const char* params, int crypto_type)`
  - Function:
    - coded interface parameters
  - Parameters:
    - abi: Contract ABI, JSON string
    - method_name: Interface Name
    - params: constructor parameter, JSON string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - Encoded parameter, hex c style string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_encode_method_by_method_id`
  - Prototype:
    - `const char* bcos_sdk_abi_encode_method_by_method_id(const char* abi, const char* method_id, const char* params, int crypto_type)`
  - Function:
    - Encode parameters according to methodID
  - Parameters:
    - abi: Contract ABI, JSON string
    - method_id: methodID
    - params: constructor parameter, JSON string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - Encoded parameter, hex c style string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_encode_method_by_method_sig`
  - Prototype:
    - `const char* bcos_sdk_abi_encode_method_by_method_sig(const char* method_sig, const char* params, int crypto_type)`
  - Function:
    - encode parameters according to the signature of the interface
  - Parameters:
    - method_sig: interface signature
    - params: constructor parameter, JSON string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - Encoded parameter, hex c style string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_decode_method_input`
  - Prototype:
    - `const char* bcos_sdk_abi_decode_method_input(const char* abi, const char* method_name, const char* data, int crypto_type)`
  - Function:
    - Parse input parameters based on interface name
  - Parameters:
    - abi: Contract ABI, JSON string
    - method_name: Interface Name
    - data: Encoded parameters, hexadecimal c-style strings
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - parsed parameter, hex c style json string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_decode_method_input_by_method_id`
  - Prototype:
    - `const char* bcos_sdk_abi_decode_method_input_by_method_id(const char* abi, const char* method_id, const char* data, int crypto_type)`
  - Function:
    - Parse input parameters based on methodID
  - Parameters:
    - abi: Contract ABI
    - method_id: methodID
    - data: ABI-encoded parameters, hexadecimal c-style strings
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - parsed parameter, hex c style json string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_decode_method_input_by_method_sig`
  - Prototype:
    - `const char* bcos_sdk_abi_decode_method_input_by_method_sig(const char* method_sig, const char* data, int crypto_type)`
  - Function:
    - parses input parameters according to the interface signature
  - Parameters:
    - method_sig: interface signature
    - data: Encoded parameters, hexadecimal c-style strings
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - parsed parameter, hex c style json string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_decode_method_output`
  - Prototype:
    - `const char* bcos_sdk_abi_decode_method_output(const char* abi, const char* method_name, const char* data, int crypto_type)`
  - Function:
    - return parameters based on interface name resolution
  - Parameters:
    - abi: Contract ABI
    - method_name: Interface Name
    - data: Encoded return, hex c-style string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - Parsed return, hex c style JSON string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_decode_method_output_by_method_id`
  - Prototype:
    - `const char* bcos_sdk_abi_decode_method_output_by_method_id(const char* abi, const char* method_id, const char* data, int crypto_type)`
  - Function:
    - Parse return parameters based on methodID
  - Parameters:
    - abi: Contract ABI
    - method_id: methodID
    - data: Encoded return, hex c-style string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - Parsed return, hex c style JSON string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_decode_event`
  - Prototype:
    - `const char* bcos_sdk_abi_decode_event(const char* abi, const char* event_name, const char* data, int crypto_type)`
  - Function:
    - parse the event parameter based on the event name
  - Parameters:
    - abi: Contract ABI
    - event_name: event name
    - data: Encoded return, hex c-style string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - parsed event parameter, hex c-style JSON string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
- `bcos_sdk_abi_decode_event_by_topic`
  - Prototype:
    - `const char* bcos_sdk_abi_decode_event_by_topic(const char* abi, const char* topic, const char* data, int crypto_type)`
  - Function:
    - parse the event parameter based on the topic
  - Parameters:
    - abi: Contract ABI
    - topic: event topic
    - data: Encoded return, hex c-style string
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
  - Return:
    - parsed event parameter, hex c-style JSON string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

### 6.3 Transaction construction (without type)
- `bcos_sdk_get_group_wasm_and_crypto`
  - Prototype:
    - `void bcos_sdk_get_group_wasm_and_crypto(void* sdk, const char* group_id, int* wasm, int* crypto_type)`
  - Function:
    - Get some basic information about the group 1. Is the group running a 'wasm' contract or a 'solidity' contract 2. Is the group a state secret or a non-state secret environment
  - Parameters:
    - `sdk`: sdk object, 'bcos _ sdk _ create' or 'bcos _ sdk _ create _ by _ config _ file'
    - `group_id`: Group ID
    - `wasm`: Return value, whether the group runs the 'wasm' contract
      - 0: No, the group runs the 'solidity' contract,
      - 1: Yes, the group runs the 'wasm' contract
    - `crypto_type`: Return value: whether the group is of the state secret type, 0: No, 1: Yes
  - Return:
    - None

- `bcos_sdk_get_group_chain_id`
  - Prototype:
    - `const char* bcos_sdk_get_group_chain_id(void* sdk, const char* group_id)`
  - Function:
    - Get the chain ID of the group, which is used when constructing the transaction
  - Parameters:
    - `sdk`: sdk object, 'bcos _ sdk _ create' or 'bcos _ sdk _ create _ by _ config _ file'
    - `group_id`: Group ID
  - Return:
    - Chain ID of the group
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_create_transaction_data`
  - Prototype:
    ```cpp
    void* bcos_sdk_create_transaction_data(const char* group_id, const char* chain_id, const char* to, const char* data, const char* abi, int64_t block_limit)
    ```
  - Function:
    - Creates a 'TransactionData' object, which is an unsigned transaction object
  - Parameters:
    - `group_id`: Group ID
    - `chain_id`: The chain ID. You can call the 'bcos _ sdk _ get _ group _ chain _ id' operation to obtain the chain ID of the group
    - `to`: Called contract address, set to empty string when contract is deployed""
    - `data`: ABI encoded parameters, hexadecimal c-style string, refer to [ABI codec](../c_sdk/api.html#abi)
    - `abi`: The ABI of the contract, which is a JSON string with optional parameters. You can enter the ABI of the contract when deploying the contract. By default, an empty string is entered""
    - `block_limit`: The block limit. You can call the 'bcos _ rpc _ get _ block _ limit' interface to obtain the
  - Return:
    - 'TransactionData' object pointer
    - 'NULL' is returned. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - The 'TransactionData' object needs to be released by calling the 'bcos _ sdk _ destroy _ transaction _ data' interface to avoid memory leakage

- `bcos_sdk_calc_transaction_data_hash`
  - Prototype:
    - `const char* bcos_sdk_calc_transaction_data_hash(int crypto_type, void* transaction_data)`
  - Function:
    - Calculates the 'TransactionData' object hash
  - Parameters:
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
    - `transaction_data`: 'TransactionData 'object pointer
  - Return:
    - 'TransactionData' object hash
    - 'NULL' is returned. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - **The hash of the 'TransactionData' object, which is also the hash of the transaction**
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_sign_transaction_data_hash`
  - Prototype:
    - `const char* bcos_sdk_sign_transaction_data_hash(void* keypair, const char* transcation_hash)`
  - Function:
    - Transaction hash signature
  - Parameters:
    - keypair:'KeyPair 'object, reference [' KeyPair 'signature object](../c_sdk/api.html#keypair)
    - transcation_hash: Transaction hash, generated by the 'bcos _ sdk _ calc _ transaction _ data _ hash' interface
  - Return:
    - Transaction signature, string type
    - 'NULL' is returned. Call 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_create_signed_transaction_with_signed_data`
  - Prototype:

  ```cpp
  const char* bcos_sdk_create_signed_transaction_with_signed_data(void* transaction_data, const char* signed_transaction_data, const char* transaction_data_hash, int32_t attribute)
  ```

  - Function:
    - Create signed transactions
  - Parameters:
    - transaction_data: 'TransactionData 'object
    - signed_transaction_data: Signature of transaction hash, hexadecimal C-style string, generated by the 'bcos _ sdk _ sign _ transaction _ data _ hash' interface
    - transaction_data_hash: Transaction hash, hexadecimal C-style string, generated by the 'bcos _ sdk _ calc _ transaction _ data _ hash' interface
    - attribute: Additional transaction attributes, to be expanded, default to 0
  - Return:
    - Signed transactions, hex c style strings
    - 'NULL' is returned. Call 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_create_signed_transaction`
  - Prototype:

  ```cpp
  void bcos_sdk_create_signed_transaction(void* key_pair, const char* group_id, const char* chain_id, const char* to, const char* data, const char* abi, int64_t block_limit, int32_t attribute, char** tx_hash, char** signed_tx)
  ```

  - Function:
    - Create signed transactions
  - Parameters:
    - key_pair: 'KeyPair 'object, reference [' KeyPair 'signature object](../c_sdk/api.html#keypair)
    - group_id: Group ID
    - chain_id: The chain ID. You can call the 'bcos _ sdk _ get _ group _ chain _ id' operation to obtain the chain ID of the group
    - to: Called contract address, set to empty string when contract is deployed""
    - data: ABI encoded parameters, refer to [ABI codec](../c_sdk/api.html#abi)
    - abi: The ABI of the contract. This parameter is optional. You can enter the ABI of the contract when you deploy the contract. The default value is an empty string""
    - block_limit: The block limit. You can call the 'bcos _ rpc _ get _ block _ limit' interface to obtain the
    - attribute: Additional transaction attributes, to be expanded, default to 0
    - tx_hash: return value, transaction hash, hex c-style string
    - signed_tx: return value, signed transaction, hex c-style string
  - Return:
    - Call the 'bcos _ sdk _ get _ last _ error' interface to determine whether it is successful. 0 indicates success, and other values indicate error codes
  - Attention:
    - The returned 'tx _ hash' and 'signed _ tx' must be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
  - **Description**:
    - 'bcos _ sdk _ create _ signed _ transaction' is equivalent to a combination of the following interfaces. When the transaction creation, transaction hash, and transaction signature processes need to be processed separately, use the following interfaces:
      - `bcos_sdk_create_transaction_data`: Create 'TransactionData'
      - `bcos_sdk_calc_transaction_data_hash`: Calculate Transaction Hash
      - `bcos_sdk_sign_transaction_data_hash`: Transaction Hash Signature
      - `bcos_sdk_create_signed_transaction_with_signed_data`: Create a signed transaction

- `bcos_sdk_destroy_transaction_data`
  - Prototype:
    - `void bcos_sdk_destroy_transaction_data(void* transaction_data)`
  - Function:
    - Release the 'TransactionData' object
  - Parameters:
    - `transaction_data`: 'TransactionData 'object pointer
  - Return:
    - None

- `bcos_sdk_create_transaction_builder_service`
  - Prototype:
    - `void* bcos_sdk_create_transaction_builder_service(void* sdk, const char* group_id)`
  - Function:
    - Create a 'TransactionBuilderService' object to simplify the construction of signature transactions. You can compare the differences between 'bcos _ sdk _ create _ transaction _ data _ with _ tx _ builder _ service' and 'bcos _ sdk _ create _ transaction _ data'
  - Parameters:
    - sdk: sdk object pointer
    - group_id: Group ID
  - Return:
    - 'TransactionBuilderService' object pointer
    - 'NULL' is returned. Call 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - 'TransactionBuilderService' object needs to be destroyed using 'bcos _ sdk _ destroy _ transaction _ builder _ service' to avoid memory leakage

- `bcos_sdk_destroy_transaction_builder_service`
  - Prototype:
    - `bcos_sdk_destroy_transaction_builder_service(void* service)`
  - Function:
    - Destroy the 'TransactionBuilderService' object
  - Parameters:
    - 'TransactionBuilderService' object pointer
  - Return:
    - None
- `bcos_sdk_create_transaction_data_with_tx_builder_service`
  - Prototype:
    - `void* bcos_sdk_create_transaction_data_with_tx_builder_service(void* tx_builder_service, const char* to, const char* data, const char* abi)`
  - Function:
    - Creates a 'TransactionData' object
  - Parameters:
    - tx_builder_service: 'TransactionBuilderService 'object pointer
    - to: Called contract address, set to empty string when contract is deployed""
    - data: ABI encoded parameters, refer to [ABI codec](../c_sdk/api.html#abi)
    - abi: The ABI of the contract. This parameter is optional. You can enter the ABI of the contract when you deploy the contract. The default value is an empty string""
  - Return:
    - 'TransactionData' object pointer
    - Failed to return 'NULL' Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - The created 'TransactionData' object needs to be released by the 'bcos _ sdk _ destroy _ transaction _ data' interface to avoid memory leakage

- `bcos_sdk_create_signed_transaction_with_tx_builder_service`
  - Prototype:

  ```cpp
  void bcos_sdk_create_signed_transaction_with_tx_builder_service(void*tx_builder_service, void* key_pair, const char*to, const char* data, const char* abi, int32_t attribute, char** tx_hash, char** signed_tx)
  ```

  - Function:
    - Create signed transactions
  - Parameters:
    - tx_builder_service: 'TransactionBuilderService 'object pointer
    - key_pair: 'KeyPair 'object, reference [' KeyPair 'signature object](../c_sdk/api.html#keypair)
    - to: Called contract address, set to empty string when contract is deployed""
    - data: ABI encoded parameters, refer to [ABI codec](../c_sdk/api.html#abi)
    - abi: The ABI of the contract. This parameter is optional. You can enter the ABI of the contract when you deploy the contract. The default value is an empty string""
    - attribute: Additional transaction attributes, to be expanded, default to 0
    - tx_hash: return value, transaction hash, hex c-style string
    - signed_tx: return value, signed transaction, hex c-style string
  - Attention:
    - The returned 'tx _ hash' and 'signed _ tx' must be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

### 6.4 Transaction Structure (Band Type)
- **c-sdk '3.3.0-tx-struct' feature branch, added support for transaction structures**。
That is, the return value and input parameters support the transaction structure, which is as follows
```c
// transaction bytes
struct bcos_sdk_c_bytes
{
    uint8_t* buffer;
    uint32_t length;
};

// transaction data
struct bcos_sdk_c_transaction_data
{
    int32_t version;
    int64_t block_limit;
    char* chain_id;
    char* group_id;
    char* nonce;
    char* to;
    char* abi;
    struct bcos_sdk_c_bytes* input;
};

// transaction
struct bcos_sdk_c_transaction
{
    struct bcos_sdk_c_transaction_data* transaction_data;
    struct bcos_sdk_c_bytes* data_hash;
    struct bcos_sdk_c_bytes* signature;
    struct bcos_sdk_c_bytes* sender;
    int64_t import_time;
    int32_t attribute;
    char* extra_data;
};
```

- `bcos_sdk_create_transaction_data_struct_with_hex_input`
  - Prototype:
    ```cpp
    struct bcos_sdk_c_transaction_data* bcos_sdk_create_transaction_data_struct_with_hex_input(const char* group_id, const char* chain_id, const char* to, const char* input, const char* abi, int64_t block_limit)
    ```
  - Function:
    - Create a 'bcos _ sdk _ c _ transaction _ data' transaction structure, which is an unsigned transaction object
  - Parameters:
    - `group_id`: Group ID
    - `chain_id`: The chain ID. You can call the 'bcos _ sdk _ get _ group _ chain _ id' operation to obtain the chain ID of the group
    - `to`: Called contract address, set to empty string when contract is deployed""
    - `input`: ABI-encoded parameter, hexadecimal C-style string, hex string
    - `abi`: The ABI of the contract, which is a JSON string with optional parameters. You can enter the ABI of the contract when deploying the contract. By default, an empty string is entered""
    - `block_limit`: The block limit. You can call the 'bcos _ rpc _ get _ block _ limit' interface to obtain the
  - Return:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
    - 'NULL' is returned. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure. You need to call the 'bcos _ sdk _ destroy _ transaction _ data _ struct' interface to release the transaction structure to avoid memory leakage

- `bcos_sdk_create_transaction_data_struct_with_bytes`
  - Prototype:
    ```cpp
    struct bcos_sdk_c_transaction_data* bcos_sdk_create_transaction_data_struct_with_bytes(const char* group_id, const char* chain_id, const char* to, const unsigned char* bytes_input, uint32_t bytes_input_length, const char* abi, int64_t block_limit)
    ```
  - Function:
    - Create a 'bcos _ sdk _ c _ transaction _ data' transaction structure, which is an unsigned transaction object
  - Parameters:
    - `group_id`: Group ID
    - `chain_id`: The chain ID. You can call the 'bcos _ sdk _ get _ group _ chain _ id' operation to obtain the chain ID of the group
    - `to`: Called contract address, set to empty string when contract is deployed""
    - `bytes_input`: ABI-encoded parameter, byte array of bytes
    - `bytes_input_length`: length of byte array
    - `abi`: The ABI of the contract, which is a JSON string with optional parameters. You can enter the ABI of the contract when deploying the contract. By default, an empty string is entered""
    - `block_limit`: The block limit. You can call the 'bcos _ rpc _ get _ block _ limit' interface to obtain the
  - Return:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
    - 'NULL' is returned. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure. You need to call the 'bcos _ sdk _ destroy _ transaction _ data _ struct' interface to release the transaction structure to avoid memory leakage

- `bcos_sdk_calc_transaction_data_struct_hash`
  - Prototype:
    - `const char* bcos_sdk_calc_transaction_data_struct_hash(int crypto_type, struct bcos_sdk_c_transaction_data* transaction_data)`
  - Function:
    - Calculate the hash of the 'bcos _ sdk _ c _ transaction _ data' transaction structure
  - Parameters:
    - crypto_type: type, ECDSA: BCOS_C_SDK_ECDSA_TYPE(0), SM: BCOS_C_SDK_SM_TYPE(1)
    - `transaction_data`: 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
  - Return:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure hash
    - 'NULL' is returned. Use 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - **The hash of the 'bcos _ sdk _ c _ transaction _ data' transaction structure, which is also the hash of the transaction**
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_create_transaction_struct`
  - Prototype:

  ```cpp
  struct bcos_sdk_c_transaction* bcos_sdk_create_transaction_struct(struct bcos_sdk_c_transaction_data* transaction_data, const char* signature, const char* transaction_data_hash, int32_t attribute, const char* extra_data)
  ```

  - Function:
    - Create a signed transaction structure
  - Parameters:
    - transaction_data: 'bcos _ sdk _ c _ transaction _ data 'transaction structure
    - signature: Signature of the transaction structure hash, a hexadecimal C-style string, generated by the 'bcos _ sdk _ sign _ transaction _ data _ hash' interface
    - transaction_data_hash: Transaction structure hash, hexadecimal C-style string, generated by the 'bcos _ sdk _ calc _ transaction _ data _ struct _ hash' interface
    - attribute: Additional transaction attributes, to be expanded, default to 0
    - extra_data: Transaction additional data, fill in""Empty string is enough
  - Return:
    - 'bcos _ sdk _ c _ transaction' signed transaction structure pointer
    - 'NULL' is returned. Call 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - The transaction structure signed by 'bcos _ sdk _ c _ transaction', which needs to be released by calling the 'bcos _ sdk _ destroy _ transaction _ struct' interface to avoid memory leakage

- `bcos_sdk_create_encoded_transaction`
  - Prototype:

  ```cpp
  const char* bcos_sdk_create_encoded_transaction(
      struct bcos_sdk_c_transaction_data* transaction_data, const char* signature,
      const char* transaction_data_hash, int32_t attribute, const char* extra_data)
  ```

  - Function:
    - Create a signed transaction string
  - Parameters:
    - transaction_data: 'bcos _ sdk _ c _ transaction _ data 'transaction structure
    - signature: Signature of the transaction structure hash, a hexadecimal C-style string, generated by the 'bcos _ sdk _ sign _ transaction _ data _ hash' interface
    - transaction_data_hash: Transaction structure hash, hexadecimal C-style string, generated by the 'bcos _ sdk _ calc _ transaction _ data _ struct _ hash' interface
    - attribute: Additional transaction attributes, to be expanded, default to 0
    - extra_data: Transaction additional data, fill in""Empty string is enough
  - Return:
    - Signature of transaction string
    - 'NULL' is returned. Call 'bcos _ sdk _ get _ last _ error' and 'bcos _ sdk _ get _ last _ error _ msg' to obtain the error code and error description
  - Attention:
    - The returned signed transaction string, which needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_encode_transaction_data_struct`
  - Prototype:
    - `const char* bcos_sdk_encode_transaction_data_struct(struct bcos_sdk_c_transaction_data* transaction_data)`
  - Function:
    - encode the 'bcos _ sdk _ c _ transaction _ data' transaction structure as a hex string
  - Parameters:
    - `transaction_data`: 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
  - Return:
    - 'transaction _ data' transaction structure encoded hex string
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_encode_transaction_data_struct_to_json`
  - Prototype:
    - `const char* bcos_sdk_encode_transaction_data_struct_to_json(struct bcos_sdk_c_transaction_data* transaction_data)`
  - Function:
    - encode the 'bcos _ sdk _ c _ transaction _ data' transaction structure as a json string
  - Parameters:
    - `transaction_data`: 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
  - Return:
    - json string after 'transaction _ data' transaction structure encoding
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
  
- `bcos_sdk_decode_transaction_data_struct`
  - Prototype:
    - `struct bcos_sdk_c_transaction_data* bcos_sdk_decode_transaction_data_struct(const char* transaction_data_hex_str)`
  - Function:
    - Decode the encoded hex string into the 'bcos _ sdk _ c _ transaction _ data' transaction structure
  - Parameters:
    - `transaction_data_hex_str`: encoded hex string
  - Return:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
  - Attention:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure. You need to call the 'bcos _ sdk _ destroy _ transaction _ data _ struct' interface to release the transaction structure to avoid memory leakage

- `bcos_sdk_decode_transaction_data_struct_with_json`
  - Prototype:
    - `struct bcos_sdk_c_transaction_data* bcos_sdk_decode_transaction_data_struct_with_json(const char* transaction_data_json_str)`
  - Function:
    - Decode the encoded json string into the 'bcos _ sdk _ c _ transaction _ data' transaction structure
  - Parameters:
    - `transaction_data_json_str`: encoded json string
  - Return:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
  - Attention:
    - 'bcos _ sdk _ c _ transaction _ data' transaction structure. You need to call the 'bcos _ sdk _ destroy _ transaction _ data _ struct' interface to release the transaction structure to avoid memory leakage

- `bcos_sdk_destroy_transaction_data_struct`
  - Prototype:
    - `void bcos_sdk_destroy_transaction_data_struct(struct bcos_sdk_c_transaction_data* transaction_data)`
  - Function:
    - Release the 'bcos _ sdk _ c _ transaction _ data' transaction structure
  - Parameters:
    - `transaction_data`: 'bcos _ sdk _ c _ transaction _ data' transaction structure pointer
  - Return:
    - None

- `bcos_sdk_encode_transaction_struct`
  - Prototype:
    - `const char* bcos_sdk_encode_transaction_struct(struct bcos_sdk_c_transaction* transaction)`
  - Function:
    - encode the transaction structure of the 'bcos _ sdk _ c _ transaction' signature as a hex string
  - Parameters:
    - `transaction`: 'bcos _ sdk _ c _ transaction 'signed transaction structure pointer
  - Return:
    - hex string after the transaction structure encoding of the 'transaction' signature
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage

- `bcos_sdk_encode_transaction_struct_to_json`
  - Prototype:
    - `const char* bcos_sdk_encode_transaction_struct_to_json(struct bcos_sdk_c_transaction* transaction)`
  - Function:
    - encode the 'bcos _ sdk _ c _ transaction' signed transaction structure as a json string
  - Parameters:
    - `transaction`: 'bcos _ sdk _ c _ transaction 'signed transaction structure pointer
  - Return:
    - json string after the transaction structure of the 'transaction' signature is encoded
  - Attention:
    - The returned string needs to be released by calling 'bcos _ sdk _ c _ free' to avoid memory leakage
  
- `bcos_sdk_decode_transaction_struct`
  - Prototype:
    - `struct bcos_sdk_c_transaction* bcos_sdk_decode_transaction_struct(const char* transaction_hex_str)`
  - Function:
    - Decode the encoded hex string into a 'bcos _ sdk _ c _ transaction' signed transaction structure
  - Parameters:
    - `transaction_hex_str`: encoded hex string
  - Return:
    - 'bcos _ sdk _ c _ transaction' signed transaction structure pointer
  - Attention:
    - The transaction structure signed by 'bcos _ sdk _ c _ transaction', which needs to be released by calling the 'bcos _ sdk _ destroy _ transaction _ struct' interface to avoid memory leakage

- `bcos_sdk_decode_transaction_struct_with_json`
  - Prototype:
    - `struct bcos_sdk_c_transaction* bcos_sdk_decode_transaction_struct_with_json(const char* transaction_json_str)`
  - Function:
    - Decode the encoded json string into a 'bcos _ sdk _ c _ transaction' signed transaction structure
  - Parameters:
    - `transaction_json_str`: encoded json string
  - Return:
    - 'bcos _ sdk _ c _ transaction' signed transaction structure pointer
  - Attention:
    - The transaction structure signed by 'bcos _ sdk _ c _ transaction', which needs to be released by calling the 'bcos _ sdk _ destroy _ transaction _ struct' interface to avoid memory leakage

- `bcos_sdk_destroy_transaction_struct`
  - Prototype:
    - `void bcos_sdk_destroy_transaction_struct(struct bcos_sdk_c_transaction* transaction)`
  - Function:
    - Release the transaction structure signed by 'bcos _ sdk _ c _ transaction'
  - Parameters:
    - `transaction_data`: 'bcos _ sdk _ c _ transaction 'signed transaction structure pointer
  - Return:
    - None