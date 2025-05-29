# 附录

标签：``c-sdk`` ``ABI编解码`` ``签名交易构造``

----------

本小结介绍`c-sdk`的一些工具类的使用细节:

- ABI编解码
- 签名交易构造

## ABI编解码

待添加

## 构造签名交易

构造签名交易有两种方式，根据交易构造与签名是否一起区分:

- 直接构造签名交易: 可以加载签名私钥，直接构造签名交易
- 交易构造与签名分离: 这种情况下私钥因安全等因素由其他服务托管，交易构造由本地完成，交易签名需要由签名服务完成

### 直接构造签名交易

- 构造签名对象

参考[KeyPair](./api.html#keypair)小节，创建`KeyPir`对象

```shell
bcos_sdk_create_keypair: 创建KeyPair对象
bcos_sdk_create_keypair_by_private_key: 加载私钥创建KeyPair对象
bcos_sdk_create_keypair_by_hex_private_key: 加载十六进制字符串格式私钥创建KeyPair对象
```

- 构造签名交易

参考[交易构造](./api.html#id5)小节，构造签名交易

```shell
bcos_sdk_create_signed_transaction: 构造签名交易
```

参考[示例](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/tx/hello_sample.c#L287)

### 交易构造&签名分离

```shell
# 创建TransactionData
bcos_sdk_create_transaction_data
# 计算交易哈希
bcos_sdk_calc_transaction_data_hash
# 交易哈希签名，由签名服务实施
bcos_sdk_sign_transaction_data_hash
# 构造签名交易
bcos_sdk_create_signed_transaction_with_signed_data
```

参考[示例](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.0.1/sample/tx/hello_sample.c#L305)
