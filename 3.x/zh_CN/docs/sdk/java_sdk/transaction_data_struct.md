# 交易与回执数据结构与组装过程

标签：``java-sdk`` ``组装交易`` ``数据结构`` ``交易`` ``交易回执``

---

## 1. 交易数据结构解释

3.0的交易定义于FISCO-BCOS仓库中 `bcos-tars-protocol/bcos-tars-protocol/tars/Transaction.tars` 中定义，可见链接：[Transaction.tars](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/bcos-tars-protocol/bcos-tars-protocol/tars/Transaction.tars)。数据结构如下所示：

```c++
module bcostars {
    struct TransactionData {
        1  optional int          version;       // 交易版本号，目前已有v0，v1，v2三种交易
        2  optional string       chainID;       // 链名
        3  optional string       groupID;       // 群组名
        4  optional long         blockLimit;    // 交易限定执行的区块高度
        5  optional string       nonce;         // 交易唯一性标识
        6  optional string       to;            // 交易调用的合约地址
        7  optional vector<byte> input;         // 交易调用合约的参数，经过ABI/Scale编码
        8  optional string       abi;           // ABI的JSON字符串，建议在部署合约时带上ABI
        9  optional string       value;         // v1交易新增字段，原生转账金额
        10 optional string       gasPrice;      // v1交易新增字段，执行时gas的单价(gas/wei)
        11 optional long         gasLimit;      // v1交易新增字段，交易执行时gas使用的上限
        12 optional string       maxFeePerGas;  // v1交易新增字段，EIP1559预留字段
        13 optional string       maxPriorityFeePerGas; // v1交易新增字段，EIP1559预留字段
        14 optional vector<byte> extension;    // v2交易新增字段，用于额外存储
    };

    struct Transaction {
        1 optional TransactionData data;    // 交易基础字段
        2 optional vector<byte> dataHash;   // 交易基础字段data的哈希值
        3 optional vector<byte> signature;  // 对交易基础字段data的哈希值字节的签名
        4 optional long importTime;         // 交易到达交易池的时间
        5 optional int attribute;           // 交易属性，EVM交易为1，默认可填写0；WASM交易为2；WASM部署交易为2 || 8;
        // 6 optional string source;
        7 optional vector<byte> sender;     // 交易发起的EOA地址
        8 optional string extraData;        // 交易额外字段，该字段不计算哈希
    };
};
```

## 2. 交易回执数据结构解释

3.0的交易回执定义于FISCO-BCOS仓库中 `bcos-tars-protocol/bcos-tars-protocol/tars/TransactionReceipt.tars` 中定义，可见链接：[TransactionReceipt.tars](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/bcos-tars-protocol/bcos-tars-protocol/tars/TransactionReceipt.tars)。数据结构如下所示：

```c++
module bcostars {
    struct LogEntry {                           // 事件结构体
        1 optional string address;              // 事件合约地址
        2 optional vector<vector<byte>> topic;  // 事件的indexed字段的topic，最多为4
        3 optional vector<byte> data;           // 事件的indexed字段以外的数值，ABI编码
    };

    struct TransactionReceiptData {             // 交易回执基础类型
        1 require  int              version;    // 交易回执的版本，目前已有v0，v1，v2
        2 optional string           gasUsed;    // 交易使用的gas
        3 optional string           contractAddress;    // 交易调用的合约地址，若为部署合约交易则为新的合约地址
        4 optional int              status;     // 交易执行状态，为0则成功；非0为不成功，将在output写入错误信息（Error(string) ABI编码后的值）
        5 optional vector<byte>     output;     // 交易执行返回值
        6 optional vector<LogEntry> logEntries; // 事件列表
        7 optional long             blockNumber;// 交易执行所在的块高
        8 optional string           effectiveGasPrice; // v1版本新增字段，交易执行时生效的gas单价（gas/wei）
    };

    struct TransactionReceipt {                 // 交易回执类型
        1 optional TransactionReceiptData data; // 交易回执基础类型
        2 optional vector<byte> dataHash;       // 交易回执基础类型data的哈希值
        3 optional string message;              // 交易回执返回信息
    };
};
```

## 3. 交易的组装过程

由上所示，SDK需要先组装好 `TransactionData`，再组装交易数据结构为 `Transaction`，最后发到区块链节点。具体步骤如下：

- 交易调用合约的实际参数，使用ABI/Scale编码后作为 `input` 字段；
- 传入`blockLimit`字段，一般为当前块高+600；
- 传入`nonce`字段，一般为随机16进制字符串；
- 传入其他参数，构造出 `TransactionData` 结构体对象；
- 对`TransactionData`的对象进行哈希计算，哈希计算算法可见第4小节；
- 使用密钥对上一步骤计算出的哈希值（字节数组）进行签名计算，得出签名；
- 传入其他参数，构造出 `Transaction` 结构体对象；
- 使用`Tars`编码对 `Transaction` 结构体对象进行编码；
- 得到最终的交易Raw data，发送到链上。

## 4. TransactionData哈希计算算法与示例

TransactionData在进行哈希计算时，是对该对象内的所有字段的字节进行拼装，最后对字节数组进行哈希计算。C++实现的示例如下：

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

Java实现的示例如下：

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

## 5. TransactionReceiptData哈希计算算法与示例

如同第4节所述，TransactionReceiptData的哈希计算方法也是对该对象内的所有字段的字节进行拼装，最后对字节数组进行哈希计算。C++实现的示例如下：

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

Java实现的示例如下：

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
