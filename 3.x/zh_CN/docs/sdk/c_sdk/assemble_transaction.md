# 交易构造与发送

标签：``c-sdk`` ``组装交易``

----

```eval_rst
.. important::
    FISCO BCOS 在3.6.0版本以后开始支持V1版本的交易，在3.7.0版本以后开始支持V2交易，在使用之前请确认发送的节点版本。3.6.0版本特性请参考：`v3.6.0 <../introduction/change_log/3_6_0.html>`_ 
```

```eval_rst
.. note::
    交易的数据结构可以参考 `这里 <./transaction_data_struct.html>`_ 
```

FISCO BCOS 在3.6.0版本以后开始支持V1版本的交易，在3.7.0版本以后开始支持V2交易，新增以下五个字段：

```c++
string       value;         // v1交易新增字段，原生转账金额
string       gasPrice;      // v1交易新增字段，执行时gas的单价(gas/wei)
long         gasLimit;      // v1交易新增字段，交易执行时gas使用的上限
string       maxFeePerGas;  // v1交易新增字段，EIP1559预留字段
string       maxPriorityFeePerGas; // v1交易新增字段，EIP1559预留字段
vector<byte> extension;    // v2交易新增字段，用于额外存储
```

为了解决未来可能的增加交易字段的需求，C SDK支持全新的能够支持灵活拼装的交易服务，方便用户开发者灵活使用。

## 1. 交易结构体定义

在3.6.0以后增加了使用交易结构体的支持，即返回值、入参都支持使用交易结构体。结构体如下：

```c
// 基础bytes类型
struct bcos_sdk_c_bytes
{
    uint8_t* buffer;
    uint32_t length;
};
// v0交易data结构体
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
// v1交易data结构体
struct bcos_sdk_c_transaction_data_v1
{
    struct bcos_sdk_c_transaction_data base;
    char* value;
    char* gas_price;
    int64_t gas_limit;
    char* max_fee_per_gas;
    char* max_priority_fee_per_gas;
};
// v2交易data结构体
struct bcos_sdk_c_transaction_data_v2
{
    struct bcos_sdk_c_transaction_data_v1 base_v1;
    struct bcos_sdk_c_bytes* extension;
};
// v0交易结构体
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
// v1交易结构体
struct bcos_sdk_c_transaction_v1
{
    struct bcos_sdk_c_transaction_data_v1* transaction_data;
    struct bcos_sdk_c_bytes* data_hash;
    struct bcos_sdk_c_bytes* signature;
    struct bcos_sdk_c_bytes* sender;
    int64_t import_time;
    int32_t attribute;
    char* extra_data;
};
// v2交易结构体
struct bcos_sdk_c_transaction_v2
{
    struct bcos_sdk_c_transaction_data_v2* transaction_data;
    struct bcos_sdk_c_bytes* data_hash;
    struct bcos_sdk_c_bytes* signature;
    struct bcos_sdk_c_bytes* sender;
    int64_t import_time;
    int32_t attribute;
    char* extra_data;
};
```

## 2. 交易的组装过程

SDK需要先组装好 `TransactionData`，再组装交易数据结构为 `Transaction`，最后对交易数据结构进行编码，发到区块链节点。具体步骤如下：

- 交易调用合约的实际参数，使用ABI/Scale编码后作为 `input` 字段；
- 传入`blockLimit`字段，一般为当前块高+600；
- 传入`nonce`字段，一般为随机16进制字符串；
- 传入其他参数，构造出 `TransactionData` 结构体对象；
- 对`TransactionData`的对象进行哈希计算；
- 使用密钥对上一步骤计算出的哈希值（字节数组）进行签名计算，得出签名；
- 传入其他参数，构造出 `Transaction` 结构体对象；
- 使用`Tars`编码对 `Transaction` 结构体对象进行编码；
- 得到最终的交易Raw data，发送到链上。

## 3. 交易结构体计算接口

下面以 `v2` 版本交易为例，以交易组装的过程为时间线，介绍交易结构体的计算接口。

### 3.1 构造TransactionData结构体

接口`bcos_sdk_create_transaction_data_struct_v2`

- 功能:
  - 创建`bcos_sdk_c_transaction_data_v2`交易结构体，该对象结构体是未签名的交易对象
- 参数：
  - `version`: 交易版本，根据使用的交易字段情况传入对应的交易版本，默认为2
  - `group_id`: 群组ID
  - `chain_id`: 链ID，可以调用`bcos_sdk_get_group_chain_id`接口获取群组的链ID
  - `to`: 调用的合约地址，部署合约时设置为空字符串""
  - `input`: ABI编码后的参数，bytes数组，需要传入bytes指针以及长度
  - `abi`: 合约的ABI，JSON字符串，可选参数，部署合约时可以将合约的ABI传入，默认传入空字符串""
  - `block_limit`: 区块限制，可以调用`bcos_rpc_get_block_limit`接口获取
  - `value`: 交易transfer balance的值
  - `gas_price`: 交易给定的gas price
  - `gas_limit`: 交易最大使用gas 数量
  - `max_fee_per_gas`: 交易给定的EIP-1559字段
  - `max_priority_fee_per_gas`: 交易给定的EIP-1559字段
  - `extension`: 交易可额外存储的bytes类型，需要传入bytes指针以及长度
- 返回:
  - `bcos_sdk_c_transaction_data_v2`交易结构体指针
  - 失败返回`NULL`，使用`bcos_sdk_get_last_error`、 `bcos_sdk_get_last_error_msg`获取错误码和错误描述信息
- 注意:
  - `bcos_sdk_c_transaction_data_v2`交易结构体，需要调用`bcos_sdk_destroy_transaction_data_struct_v2`接口释放，以免造成内存泄露

### 3.2 计算TransactionData结构体Hash

接口 `bcos_sdk_calc_transaction_data_struct_hash_v2`

- 功能：计算交易Data对象的哈希
- 参数：
  - `crypto_type`: 哈希类型，0 为keccak256，1 为SM3
  - `bcos_sdk_c_transaction_data_v2`: transactionData指针
- 返回：交易哈希，Hex String形式
- 注意：计算哈希时会根据交易的版本号进行不同判断，哈希计算错误会导致交易上链检查失败，因此请正确设置交易版本号。

### 3.3 使用签名、交易哈希构造交易Transaction结构体

在使用3.2的接口计算出交易哈希之后，可以使用C-SDK的接口，或者外部签名服务计算出对交易哈希的签名。

**注意：FISCO BCOS的签名的构造方式为：**

- 如果是ECDSA签名，签名的字节构造方式为 R||S||V，其中V为标准ECDSA的值，取值范围是[0,1]
- 如果是SM2签名，签名的字节构造方式为 R||S||PK，其中PK为私钥的公钥

接口 `bcos_sdk_create_encoded_transaction_v2`

- 功能：使用签名、交易哈希构造出编码后的交易，可直接发送到链上
- 参数：
  - `bcos_sdk_c_transaction_data_v2` : 交易TransactionData对象指针
  - `signature` ：对交易哈希的签名，Hex String格式
  - `transaction_data_hash` : 交易哈希，Hex String格式
  - `attribute`: 交易属性，待拓展，默认填0即可
  - `extra_data`: 交易额外数据，可存额外交易值，填""空字符串即可
- 返回：编码后的交易数据结构，Hex String格式，可直接用于上链

接口 `bcos_sdk_encode_transaction_struct_to_hex_v2`

- 功能：可另外构建Transaction结构体，对其进行编码，可直接发送到链上
- 参数：
  - `bcos_sdk_c_transaction_v2` : 交易Transaction对象指针
- 返回：编码后的交易数据结构，Hex String格式，可直接用于上链

### 3.4 解析已编码的Transaction

接口 `bcos_sdk_decode_transaction_struct_from_hex_v2`：

- 功能：可解析已`Tars`编码过后的交易Hex String，构造出Transaction结构体
- 参数：`transaction_hex_str` Tars编码后的交易结构体，Hex String
- 返回：`bcos_sdk_c_transaction_v2` ：transaction对象指针

### 3.5 释放TransactionData结构体

由于`C-SDK`在构造交易结构体的时候都是使用指针进行操作。因此按照C的规范做法，每次在使用完交易结构体后应该主动调用释放结构体接口，以免内存泄漏。

接口 `bcos_sdk_destroy_transaction_data_struct_v2`

- 功能：释放构造好的transactionData对象。
- 参数：`bcos_sdk_c_transaction_data_v2` transactionData指针
- 注意：调用完该接口后不应该再次使用该指针，也不应该多次使用同一个指针调用该接口。

### 3.6 释放Transaction结构体

接口 `bcos_sdk_destroy_transaction_struct_v2`

- 功能：释放构造好的transaction对象。
- 参数：`bcos_sdk_c_transaction_v2` transactionData指针
- 注意：调用完该接口后不应该再次使用该指针，也不应该多次使用同一个指针调用该接口。
