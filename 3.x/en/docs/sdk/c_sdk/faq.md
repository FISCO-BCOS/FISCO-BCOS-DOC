# FAQ

Tag: "c-sdk" "" FAQ ""

----------

This summary lists some common problems in using 'c-sdk':

## 1. Send transaction return exception: `transaction hash mismatching`

```shell
ErrorMessage: /home/ci/actions-runner/_work/FISCO-BCOS/FISCO-BCOS/bcos-tars-protocol/bcos-tars-protocol/protocol/TransactionFactoryImpl.h(77): Throw in function virtual bcos::protocol::Transaction::Ptr bcostars::protocol::TransactionFactoryImpl::createTransaction(bcos::bytesConstRef, bool, bool)
Dynamic exception type: boost::wrapexcept<std::invalid_argument>
std::exception::what: transaction hash mismatching
```

The underlying blockchain environment is upgraded from the original version to 'v3.2.0+', or use' v3.2.0 directly+'Environment, when the 'sdk' sends a transaction, an exception of 'transaction hash mismatching' may occur

- Reason

Blockchain Node 'v3.2.0+'Added mandatory verification of transaction 'hash'

The node detects that the transaction 'hash' field carried in the 'sdk' sending transaction structure does not match the transaction 'hash' calculated by the node itself, and considers that there is a problem with the transaction and rejects the transaction。

- Scene

This problem does not occur in general scenarios. It may only occur when users use the 'sdk' tool to assemble (call the interface to create, calculate hash, and sign) transactions

- Resolved

Assemble the transaction with the correct interface:

1. bcos_sdk_create_transaction_data
2. bcos_sdk_calc_transaction_data_hash # **注意**Be sure to use this interface to calculate the hash
3. bcos_sdk_sign_transaction_data_hash
4. bcos _ sdk _ create _ signed _ transaction _ with _ signed _ data or bcos _ sdk _ create _ signed _ transaction _ with _ signed _ data _ ver _ extra _ data

Example:
[c-sdk example](https://github.com/FISCO-BCOS/bcos-c-sdk/blob/v3.2.0/sample/tx/hello_sample.c#L308)

The 'sdk' of each language encapsulates the above interfaces:
    - ['Java SDK' link](https://github.com/FISCO-BCOS/bcos-sdk-jni/blob/v3.2.0/src/main/java/org/fisco/bcos/sdk/jni/utilities/tx/TransactionBuilderJniObj.java#L21)
    - Other language SDK, please refer to the specific documentation, or source code
