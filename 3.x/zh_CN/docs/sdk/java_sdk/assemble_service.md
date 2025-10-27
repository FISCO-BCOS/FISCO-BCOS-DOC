# (New)构造新版本交易

标签：``java-sdk`` ``发送交易`` ``使用接口签名发送交易`` ``组装交易`` ``合约调用`` ``v1``

----

```eval_rst
.. important::
    FISCO BCOS 在3.6.0版本以后开始支持V1版本的交易，在3.7.0版本以后开始支持V2交易，在使用之前请确认发送的节点版本。3.6.0版本特性请参考：`v3.6.0 <../introduction/change_log/3_6_0.html>`_ 
```

```eval_rst
.. note::
    交易的数据结构以及拼装方式可以参考 `这里 <./transaction_data_struct.html>`_ 
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

为了解决未来可能的增加交易字段的需求，Java SDK支持全新的能够支持灵活拼装的交易服务，方便用户开发者灵活使用。

## 1. TransactionManager

灵感来自Web3J，抽象了发送交易/调用请求的接口，提供GasProvider以及NonceAndBlockLimitProvider的注入接口，供用户自定义交易。在TransactionManager中传入的data均为ABI编码过后的字节数组。

TransactionManager是一个抽象类，有以下实现：

- `DefaultTransactionManager` ：默认的TransactionManager，在签名交易时使用Client初始化时生成的密钥。
- `ProxySignTransactionManager` ：将签名外置的TransactionManager，用户可以自行实现`AsyncTransactionSignercInterface`的接口，设置进ProxySignTransactionManager对象，在签名时均使用所实现的AsyncTransactionSignercInterface对象进行签名。

### 1.1 接口列表

TableManager的抽象接口如下所示：

```java
// 注入Gas Provider
public abstract void setGasProvider(ContractGasProvider gasProvider);
// 注入Nonce 和 BlockLimit Provider
public abstract void setNonceProvider(NonceAndBlockLimitProvidernonceProvider nonceProvider);
// 发送已经ABI编码调用参数过后的交易
public abstract TransactionReceipt sendTransaction(AbiEncodedRequestrequest request) throws JniException;
// 异步发送已经ABI编码调用参数过后的交易
public abstract String asyncSendTransaction(AbiEncodedRequest request, TransactionCallback callback) throws JniException;
// 构造已经ABI编码调用参数过后的交易，并进行签名，返回raw transaction
public abstract TxPair createSignedTransaction(AbiEncodedRequestrequest request) throws JniException;
// 发起合约调用
public abstract Call sendCall(String to, byte[] data, Stringsignature sign);
// 异步发起合约调用
public abstract void asyncSendCall(String to, byte[] data, RespCallback<Call> callback);
```

### 1.2 DefaultTransactionManager

- DefaultTransactionManager是默认的TransactionManager，在签名交易时使用Client初始化时生成的密钥。
- 使用默认的ContractGasProvider，默认返回的gaslimit为9000000，gas price为4100000000
- 使用默认的NonceAndBlockLimitProvider，默认返回的block limit为Client接口getBlockLimit返回的值，默认返回的nonce为UUID。

### 1.3 ProxySignTransactionManager

- 将签名外置的TransactionManager，用户可以自行实现`AsyncTransactionSignercInterface`的接口，设置进ProxySignTransactionManager对象，在签名时均使用所实现的AsyncTransactionSignercInterface对象进行签名。
- 使用默认的ContractGasProvider，默认返回的gaslimit为9000000，gas price为4100000000
- 使用默认的NonceAndBlockLimitProvider，默认返回的block limit为Client接口getBlockLimit返回的值，默认返回的nonce为UUID。
- 使用默认的AsyncTransactionSignercInterface：TransactionJniSignerService，默认仍然还是使用Client初始化时生成的密钥。

用户可以调用`setAsyncTransactionSigner`接口替换自己实现AsyncTransactionSignercInterface接口的对象。

### 1.4 使用示例

```java
// 初始化SDK和Client
BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
Client client = sdk.getClient("group0");

// 初始化ProxySignTransactionManager
ProxySignTransactionManager proxySignTransactionManager = new ProxySignTransactionManager(client);
// ProxySignTransactionManager可以接受AsyncTransactionSignercInterface的实现作为构造函数参数
proxySignTransactionManager = new ProxySignTransactionManager(client, (hash, transactionSignCallback) -> {
        SignatureResult sign = client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
        transactionSignCallback.handleSignedTransaction(sign);
});
// ProxySignTransactionManager.setAsyncTransactionSigner接口可以修改AsyncTransactionSignercInterface的实现
proxySignTransactionManager.setAsyncTransactionSigner((hash, transactionSignCallback) -> {
        SignatureResult sign = client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
        transactionSignCallback.handleSignedTransaction(sign);
});

// 对合约参数进行编解码
byte[] abiEncoded = contractCodec.encodeMethod(abi, method, params);

// 采用链式构造AbiEncodedRequest，传入contractAddress、nonce、blockLimit等重要参数，最后使用buildAbiEncodedRequest结束构造。
AbiEncodedRequest request =
        new TransactionRequestBuilder()
                .setTo(contractAddress)
                .setAbi(abi)
                .setNonce(nonce)
                .setBlockLimit(blockLimit)
                .setExtension("Hello World".getBytes())
                .setGasPrice(BigInteger.TEN)
                .buildAbiEncodedRequest(abiEncoded);

// 同步发送上链，获得回执
TransactionReceipt receipt = proxySignTransactionManager.sendTransaction(request);
```

## 2. AssembleTransactionService

AssembleTransactionService集成了TransactionManager、ContractCodec以及TransactionDecoderService，用户只需要传入调用合约的参数，返回的结果就包含已经解析完毕的合约返回值。

AssembleTransactionService可以切换依赖的TransactionManager，默认为DefaultTransactionManager，可切换为ProxySignTransactionManager。

### 2.1 接口列表

```java
// 设置TransactionManager
public void setTransactionManager(TransactionManager transactionManager);
// 发送调用合约交易
public TransactionResponse sendTransaction(BasicRequest request);
// 发送部署合约交易
public TransactionResponse deployContract(BasicDeployRequest request);
// 异步发送调用合约交易
public String asyncSendTransaction(BasicRequest request, TransactionCallback callback);
// 异步发送部署合约交易
public String asyncDeployContract(BasicDeployRequest request, TransactionCallback callback);
// 发送查询合约请求
public CallResponse sendCall(BasicRequest request);
// 异步发送查询合约请求
public void asyncSendCall(BasicRequest request, RespCallback<CallResponse> callback);
```

### 2.2 使用示例

```java
// 初始化SDK和Client
BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
Client client = sdk.getClient("group0");

// 初始化AssembleTransactionService
AssembleTransactionService transactionService = new AssembleTransactionService(client);
// 初始化ProxySignTransactionManager
ProxySignTransactionManager proxySignTransactionManager = new ProxySignTransactionManager(client, (hash, transactionSignCallback) -> {
    SignatureResult sign = client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
    transactionSignCallback.handleSignedTransaction(sign);
});
// 手动切换TransactionManager
transactionService.setTransactionManager(proxySignTransactionManager);
// 构造HelloWorld set参数
List<Object> params = new ArrayList<>();
params.add("Hello AssembleTransactionService");
// 构造调用HelloWorld set的请求，传入contractAddress、nonce、blockLimit等重要参数，最后使用buildRequest结束构造。
TransactionRequest request =
        new TransactionRequestBuilder(abi, "set", contractAddress)
                .setNonce(nonce)
                .setBlockLimit(blockLimit)
                .setExtension("HelloWorld".getBytes())
                .setGasPrice(BigInteger.TEN)
                .buildRequest(params);

// 同步发送上链，获得返回
TransactionResponse transactionResponse = transactionService.sendTransaction(request);

// 也可以构造使用String类型的参数
List<String> params = new ArrayList<>();
params.add("[[\"0xabcd\"],[\"0x1234\"]]");
// 构造调用合约setBytesArrayArray接口的请求，参数为bytes二维数组，传入contractAddress、nonce、blockLimit等重要参数，最后使用buildStringParamsRequest结束构造。
TransactionRequestWithStringParams requestWithStringParams = 
        new TransactionRequestBuilder(abi, "setBytesArrayArray", contractAddress)
                .setNonce(nonce)
                .setBlockLimit(blockLimit)
                .setExtension("HelloWorld".getBytes())
                .setGasPrice(BigInteger.TEN)
                .buildStringParamsRequest(params);
// 同步发送上链，获得返回
TransactionResponse transactionResponse = transactionService.sendTransaction(requestWithStringParams);
```

## 3. Solidity生成Java文件使用新接口

生成智能合约的Java接口文件详细文档可见：[链接](./contracts_to_java.md)

在3.6.0以后版本的控制台中，contract2java.sh脚本新增 `-t` 选项，当值为1就生成使用新接口的Java文件，使用姿势与之前无异。例如：

```shell
 bash contract2java.sh solidity  -t 1 -n -s ./contracts/solidity/Incremental.sol
```

已有的Java文件改造方法：

以Java-sdk-demo中 package org.fisco.bcos.sdk.demo.perf.PerformanceOk 为例：
在deploy构造好的合约对象后，设置TransactionManager，之后发送的请求就会按照新的交易接口进行。

```java
// build the client
Client client = sdk.getClient(groupId);
// deploy the HelloWorld
System.out.println("====== Deploy Ok ====== ");
Ok ok = Ok.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
// 设置TransactionManager
ok.setTransactionManager(new DefaultTransactionManager(client));
```

### 3.1 通过修改TransactionManager的Provider更改交易字段

### 3.2 使用V2版本交易的接口

