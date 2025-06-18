# FAQ

标签：``c-sdk`` ``FAQ``

----------

本小结罗列一些使用`c-sdk`常见的一些问题:

## 1. 发送交易返回异常: `transaction hash mismatching`

```shell
ErrorMessage: /home/ci/actions-runner/_work/FISCO-BCOS/FISCO-BCOS/bcos-tars-protocol/bcos-tars-protocol/protocol/TransactionFactoryImpl.h(77): Throw in function virtual bcos::protocol::Transaction::Ptr bcostars::protocol::TransactionFactoryImpl::createTransaction(bcos::bytesConstRef, bool, bool)
Dynamic exception type: boost::wrapexcept<std::invalid_argument>
std::exception::what: transaction hash mismatching
```

底层区块链环境由原来的版本升级至`v3.2.0+`，或者直接使用`v3.2.0+`环境，`sdk`发送交易时，可能会出现`transaction hash mismatching`的异常

- 原因

区块链节点`v3.2.0+`新增对交易`hash`强制校验

节点检测到`sdk`发送交易结构里面携带的交易`hash`字段与节点本身计算出来的交易`hash`不匹配，认为该交易存在问题拒绝该交易。

- 场景

一般场景下不会出现该问题，仅在用户使用`sdk`工具自行组装（分别调用接口创建、计算hash、签名）交易时，可能会出现该问题

- 解决

使用正确的接口组装交易：

1. bcos_sdk_create_transaction_data
2. bcos_sdk_calc_transaction_data_hash # **注意**：一定要使用该接口计算hash
3. bcos_sdk_sign_transaction_data_hash
4. bcos_sdk_create_signed_transaction_with_signed_data或者bcos_sdk_create_signed_transaction_with_signed_data_ver_extra_data

示例:
[c-sdk示例](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.2.0/sample/tx/hello_sample.c#L308)

各个语言的`sdk`对上述接口均有对应的封装:
    - [`Java SDK`链接](https://github.com/FISCO-BCOS/bcos-sdk-jni/blob/v3.2.0/src/main/java/org/fisco/bcos/sdk/jni/utilities/tx/TransactionBuilderJniObj.java#L21)
    - 其他语言sdk，请参考具体文档，或者源码
