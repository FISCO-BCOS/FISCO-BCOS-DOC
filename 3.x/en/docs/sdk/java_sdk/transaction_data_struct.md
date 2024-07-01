# Transaction and Receipt Data Structure and Assembly Process

Tag: "java-sdk "" 'Assembly Transaction ""' Data Structure "" 'Transaction "' Transaction Receipt" '

---

## 1. Transaction data structure interpretation

The transaction of 3.0 is defined in FISCO-BCOS warehouse in 'bcos-tars-protocol/bcos-tars-defined in protocol / tars / Transaction.tars', visible link: [Transaction.tars](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/bcos-tars-protocol/bcos-tars-protocol/tars/Transaction.tars)。The data structure is as follows:

```c++
module bcostars {
    struct TransactionData {
        1  optional int          version;       / / Transaction version number. Currently, there are three types of transactions: v0, v1, and v2.
        2  optional string       chainID;       / / Chain name
        3  optional string       groupID;       / / group name
        4  optional long         blockLimit;    / / Block height of transaction limit execution
        5  optional string       nonce;         / / Transaction uniqueness identification
        6  optional string       to;            / / The contract address of the transaction call.
        7  optional vector<byte> input;         / / Parameters of the transaction call contract, encoded by ABI / Scale
        8  optional string       abi;           / / The JSON string of the ABI. We recommend that you add the ABI when deploying a contract.
        9  optional string       value;         / / v1 New transaction field, original transfer amount
        10 optional string       gasPrice;      / / The new field in the v1 transaction. The unit price of gas during execution(gas/wei)
        11 optional long         gasLimit;      / / The upper limit of the gas used when the transaction is executed.
        12 optional string       maxFeePerGas;  / / v1 new transaction field, EIP1559 reserved field
        13 optional string       maxPriorityFeePerGas; / / v1 new transaction field, EIP1559 reserved field
        14 optional vector<byte> extension;    / / v2 new fields for additional storage
    };

    struct Transaction {
        1 optional TransactionData data;    / / Transaction Base Field
        2 optional vector<byte> dataHash;   / / Hash value of the transaction base field data
        3 optional vector<byte> signature;  / / Signature of the hash value byte of the transaction base field data
        4 optional long importTime;         / / Time when the transaction arrives in the trading pool
        5 optional int attribute;           / / Transaction attribute. EVM transaction is 1. Default value: 0；WASM deal for 2；WASM deployment deal is 2|| 8;
        // 6 optional string source;
        7 optional vector<byte> sender;     / / The EOA address from which the transaction originated
        8 optional string extraData;        / / Transaction extra field, which does not calculate the hash
    };
};
```

## 2. Transaction receipt data structure interpretation.

Transaction receipts for 3.0 are defined in FISCO-BCOS warehouse in 'bcos-tars-protocol/bcos-tars-defined in protocol / tars / TransactionReceipt.tars', visible link: [TransactionReceipt.tars](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/bcos-tars-protocol/bcos-tars-protocol/tars/TransactionReceipt.tars)。The data structure is as follows:

```c++
module bcostars {
    struct LogEntry {                           / / Event structure
        1 optional string address;              / / Event Contract Address
        2 optional vector<vector<byte>> topic;  / / The topic of the event's indexed field, up to 4
        3 optional vector<byte> data;           / / Value other than the indexed field of the event, ABI encoding
    };

    struct TransactionReceiptData {             / / Transaction receipt basis type
        1 require  int              version;    / / The version of the transaction receipt. Currently, there are v0, v1, and v2
        2 optional string           gasUsed;    / / Gas used by the transaction
        3 optional string           contractAddress;    / / The contract address of the transaction call. If the transaction is a deployment contract, the new contract address
        4 optional int              status;     / / Transaction execution status, 0 is successful；Non-0 is unsuccessful, and an error message will be written in output (Error(string) value after ABI encoding)
        5 optional vector<byte>     output;     / / Transaction execution return value
        6 optional vector<LogEntry> logEntries; / / Event list
        7 optional long             blockNumber;/ / Block height where the transaction is executed
        8 optional string           effectiveGasPrice; / / The gas unit price (gas / wei) that takes effect when the transaction is executed.
    };

    struct TransactionReceipt {                 / / Transaction receipt type
        1 optional TransactionReceiptData data; / / Transaction receipt basis type
        2 optional vector<byte> dataHash;       / / Hash value of the transaction receipt base type data
        3 optional string message;              / / Transaction receipt return information
    };
};
```

## 3. The assembly process of the transaction

As shown above, the SDK needs to assemble the 'TransactionData' first, then assemble the transaction data structure as' Transaction ', and finally send it to the blockchain node.。Specific steps are as follows:

- The actual parameters of the transaction call contract, encoded using ABI / Scale as the 'input' field；
- Enter the 'blockLimit' field, which is usually the height of the current block+600；
- The 'nonce' field, which is a random hexadecimal string.；
- Pass in other parameters to construct the 'TransactionData' structure object；
- Hash the object of 'TransactionData', the hash calculation algorithm can be found in Section 4；
- Use the key to perform the signature calculation on the hash value (byte array) calculated in the previous step to obtain the signature；
- Pass in other parameters to construct the 'Transaction' structure object；
- Encode the 'Transaction' structure object using the 'Tars' encoding；
- Get the final transaction raw data, send to the chain。

## 4. TransactionData hash calculation algorithm and example.

TransactionData performs a hash calculation by assembling the bytes of all the fields in the object and finally performing a hash calculation on the byte array。C++An example of an implementation is as follows:

```c++
int32_t version = boost::endian::native_to_big((int32_t)hashFields.version);
hasher.update(version);
hasher.update(hashFields.chainID);
hasher.update(hashFields.groupID);
int64_t blockLimit = boost::endian::native_to_big((int64_t)hashFields.blockLimit);
hasher.update(blockLimit);
hasher.update(hashFields.nonce);
hasher.update(hashFields.to);
hasher.update(hashFields.input);
hasher.update(hashFields.abi);
// if version == 1, update value, gasPrice, gasLimit, maxFeePerGas, maxPriorityFeePerGas to
// hashBuffer calculate hash
if ((uint32_t)hashFields.version >= (uint32_t)bcos::protocol::TransactionVersion::V1_VERSION)
{
    hasher.update(hashFields.value);
    hasher.update(hashFields.gasPrice);
    int64_t bigEndGasLimit = boost::endian::native_to_big((int64_t)hashFields.gasLimit);
    hasher.update(bigEndGasLimit);
    hasher.update(hashFields.maxFeePerGas);
    hasher.update(hashFields.maxPriorityFeePerGas);
}
if ((uint32_t)hashFields.version >= (uint32_t)bcos::protocol::TransactionVersion::V2_VERSION)
{
    hasher.update(hashFields.extension);
}
hasher.final(out);
```

An example of a Java implementation is as follows:

```java
ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
// version
byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getVersion()), 4));
// chainId
byteArrayOutputStream.write(getChainID().getBytes());
// groupId
byteArrayOutputStream.write(getGroupID().getBytes());
// blockLimit
byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getBlockLimit()), 8));
// nonce
byteArrayOutputStream.write(Hex.decode(getNonce()));
// to
byteArrayOutputStream.write(getTo().getBytes());
// input
byteArrayOutputStream.write(Hex.decode(getInput()));
// abi
byteArrayOutputStream.write(getAbi().getBytes());
if (getVersion() == TransactionVersion.V1.getValue()) {
    byteArrayOutputStream.write(getValue().getBytes());
    byteArrayOutputStream.write(getGasPrice().getBytes());
    byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getGasLimit()), 8));
    byteArrayOutputStream.write(getMaxFeePerGas().getBytes());
    byteArrayOutputStream.write(getMaxPriorityFeePerGas().getBytes());
}
if (getVersion() == TransactionVersion.V2.getValue()) {
    byteArrayOutputStream.write(getExtension());
}
return byteArrayOutputStream.toByteArray();
```

## 5. TransactionReceiptData hash calculation algorithm and example.

As described in Section 4, TransactionReceiptData's hash is also calculated by assembling the bytes of all the fields within the object and finally hashing the byte array.。C++An example of an implementation is as follows:

```c++
int32_t version = boost::endian::native_to_big((int32_t)hashFields.version);
hasher.update(version);
hasher.update(hashFields.gasUsed);
hasher.update(hashFields.contractAddress);
int32_t status = boost::endian::native_to_big((int32_t)hashFields.status);
hasher.update(status);
hasher.update(hashFields.output);
if(hashFields.version >= int32_t(bcos::protocol::TransactionVersion::V1_VERSION))
{
    hasher.update(hashFields.effectiveGasPrice);
}
for (auto const& log : hashFields.logEntries)
{
    hasher.update(log.address);
    for (auto const& topicItem : log.topic)
    {
        hasher.update(topicItem);
    }
    hasher.update(log.data);
}
int64_t blockNumber = boost::endian::native_to_big((int64_t)hashFields.blockNumber);
hasher.update(blockNumber);
hasher.final(out);
```

An example of a Java implementation is as follows:

```java
ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getVersion()), 4));
byteArrayOutputStream.write(getGasUsed().getBytes());
byteArrayOutputStream.write(getContractAddress().getBytes());
byteArrayOutputStream.write(toBytesPadded(BigInteger.valueOf(getStatus()), 4));
byteArrayOutputStream.write(getOutput().getBytes());
if (getVersion() == TransactionVersion.V1.getValue()) {
    byteArrayOutputStream.write(getEffectiveGasPrice().getBytes());
}
for (Logs logEntry : getLogEntries()) {
    byteArrayOutputStream.write(logEntry.getAddress().getBytes());
    for (String topic : logEntry.getTopics()) {
        byteArrayOutputStream.write(topic.getBytes());
    }
    byteArrayOutputStream.write(logEntry.getData().getBytes());
}
byteArrayOutputStream.write(toBytesPadded(getBlockNumber(), 8));
return byteArrayOutputStream.toByteArray();
```
