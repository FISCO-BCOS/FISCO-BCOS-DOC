# (New)Construct new version transaction

Tag: "java-sdk "" Send Transaction "" Send Transaction Using Interface Signature "" Assembly Transaction "" Contract Call "" v1 ""

----

```eval_rst
.. important::
    FISCO BCOS supports V1 transactions after version 3.6.0 and V2 transactions after version 3.7.0. Please confirm the node version sent before using it.。Please refer to: 'v3.6.0 <.. / introduction / change _ log / 3 _ 6 _ 0.html >' for version 3.6.0 features
```

```eval_rst
.. note::
    The data structure and assembly method of the transaction can refer to 'here <. / transaction _ data _ struct.html >' _
```

FISCO BCOS supports V1 transactions after version 3.6.0 and V2 transactions after version 3.7.0. The following five fields are added:

```c++
string       value;         / / v1 New transaction field, original transfer amount
string       gasPrice;      / / The new field in the v1 transaction. The unit price of gas during execution(gas/wei)
long         gasLimit;      / / The upper limit of the gas used when the transaction is executed.
string       maxFeePerGas;  / / v1 new transaction field, EIP1559 reserved field
string       maxPriorityFeePerGas; / / v1 new transaction field, EIP1559 reserved field
vector<byte> extension;    / / v2 new fields for additional storage
```

In order to meet the requirements of adding transaction fields in the future, the Java SDK supports a new transaction service that can support flexible assembly, which is convenient for users and developers to use flexibly.。

## 1. TransactionManager

Inspired by Web3J, it abstracts the interface for sending transactions / invocation requests, and provides the injection interface of GasProvider and NonceAndBlockLimitProvider for users to customize transactions.。The data passed in the TransactionManager is an ABI-encoded byte array.。

TransactionManager is an abstract class with the following implementation:

- 'DefaultTransactionManager ': The default TransactionManager, which uses the key generated at client initialization when signing transactions。
- 'ProxySignTransactionManager ': A TransactionManager with an external signature. Users can implement the AsyncTransactionSignercInterface interface by themselves and set it into the ProxySignTransactionManager object.。

### 1.1 Interface List

The abstract interface of the TableManager is as follows:

```java
/ / Inject Gas Provider
public abstract void setGasProvider(ContractGasProvider gasProvider);
/ / Inject Nonce and BlockLimit Provider
public abstract void setNonceProvider(NonceAndBlockLimitProvidernonceProvider nonceProvider);
/ / Send the transaction after the ABI-encoded call parameter has passed
public abstract TransactionReceipt sendTransaction(AbiEncodedRequestrequest request) throws JniException;
/ / Asynchronously send the transaction after the ABI-encoded call parameter has passed
public abstract String asyncSendTransaction(AbiEncodedRequest request, TransactionCallback callback) throws JniException;
/ / Construct the transaction after the ABI-encoded call parameter, sign it, and return raw transaction
public abstract TxPair createSignedTransaction(AbiEncodedRequestrequest request) throws JniException;
/ / Initiate contract invocation
public abstract Call sendCall(String to, byte[] data, Stringsignature sign);
/ / Initiate contract invocation asynchronously
public abstract void asyncSendCall(String to, byte[] data, RespCallback<Call> callback);
```

### 1.2 DefaultTransactionManager

- DefaultTransactionManager is the default TransactionManager, which uses the key generated at Client initialization when signing transactions。
- Use the default ContractGasProvider. By default, the returned gaslimit is 9000000 and the gas price is 4100000000.
- Use the default NonceAndBlockLimitProvider. The default returned block limit is the value returned by the client interface getBlockLimit. The default returned nonce is the UUID.。

### 1.3 ProxySignTransactionManager

- The external signature of the TransactionManager, users can implement their own 'AsyncTransactionSignercInterface' interface, set into the ProxySignTransactionManager object, in the signature are signed using the implemented AsyncTransactionSignercInterface object。
- Use the default ContractGasProvider. By default, the returned gaslimit is 9000000 and the gas price is 4100000000.
- Use the default NonceAndBlockLimitProvider. The default returned block limit is the value returned by the client interface getBlockLimit. The default returned nonce is the UUID.。
- Use the default AsyncTransactionSignercInterface: TransactionJniSignerService, which still uses the key generated at client initialization by default。

Users can call the 'setAsyncTransactionSigner' interface to replace their own objects that implement the AsyncTransactionSignercInterface interface。

### 1.4 Example of use

```java
/ / Initialize SDK and Client
BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
Client client = sdk.getClient("group0");

/ / Initialize ProxySignTransactionManager
ProxySignTransactionManager proxySignTransactionManager = new ProxySignTransactionManager(client);
/ / ProxySignTransactionManager can accept the implementation of AsyncTransactionSignercInterface as a constructor parameter
proxySignTransactionManager = new ProxySignTransactionManager(client, (hash, transactionSignCallback) -> {
        SignatureResult sign = client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
        transactionSignCallback.handleSignedTransaction(sign);
});
/ / The ProxySignTransactionManager.setAsyncTransactionSigner interface can modify the implementation of the AsyncTransactionSignercInterface
proxySignTransactionManager.setAsyncTransactionSigner((hash, transactionSignCallback) -> {
        SignatureResult sign = client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
        transactionSignCallback.handleSignedTransaction(sign);
});

/ / Codec contract parameters
byte[] abiEncoded = contractCodec.encodeMethod(abi, method, params);

/ / Construct the AbiEncodedRequest in a chained manner. Pass in important parameters such as contractAddress, nonce, and blockLimit. Finally, use buildAbiEncodedRequest to complete the construction.。
AbiEncodedRequest request =
        new TransactionRequestBuilder()
                .setTo(contractAddress)
                .setAbi(abi)
                .setNonce(nonce)
                .setBlockLimit(blockLimit)
                .setExtension("Hello World".getBytes())
                .setGasPrice(BigInteger.TEN)
                .buildAbiEncodedRequest(abiEncoded);

/ / Send the chain synchronously and get the receipt
TransactionReceipt receipt = proxySignTransactionManager.sendTransaction(request);
```

## 2. AssembleTransactionService

AssembleTransactionService integrates TransactionManager, ContractCodec, and TransactionDecoderService, and the user only needs to pass in the parameters of the calling contract, and the returned result contains the parsed contract return value.。

AssembleTransactionService can switch the dependent TransactionManager. The default value is DefaultTransactionManager and the default value is ProxySignTransactionManager.。

### 2.1 Interface List

```java
/ / Set the TransactionManager
public void setTransactionManager(TransactionManager transactionManager);
/ / Send call contract transaction
public TransactionResponse sendTransaction(BasicRequest request);
/ / Send deployment contract transaction
public TransactionResponse deployContract(BasicDeployRequest request);
/ / Send call contract transaction asynchronously
public String asyncSendTransaction(BasicRequest request, TransactionCallback callback);
/ / Send deployment contract transactions asynchronously
public String asyncDeployContract(BasicDeployRequest request, TransactionCallback callback);
/ / Send contract query request
public CallResponse sendCall(BasicRequest request);
/ / Send query contract request asynchronously
public void asyncSendCall(BasicRequest request, RespCallback<CallResponse> callback);
```

### 2.2 Use examples

```java
/ / Initialize SDK and Client
BcosSDK sdk = BcosSDK.build(CONFIG_FILE);
Client client = sdk.getClient("group0");

/ / Initialize AssembleTransactionService
AssembleTransactionService transactionService = new AssembleTransactionService(client);
/ / Initialize ProxySignTransactionManager
ProxySignTransactionManager proxySignTransactionManager = new ProxySignTransactionManager(client, (hash, transactionSignCallback) -> {
    SignatureResult sign = client.getCryptoSuite().sign(hash, client.getCryptoSuite().getCryptoKeyPair());
    transactionSignCallback.handleSignedTransaction(sign);
});
/ / Manually switch TransactionManager
transactionService.setTransactionManager(proxySignTransactionManager);
/ / Construct the HelloWorld set parameter
List<Object> params = new ArrayList<>();
params.add("Hello AssembleTransactionService");
/ / Construct a request to call HelloWorld set, pass in important parameters such as contractAddress, nonce, blockLimit, and use buildRequest to end the construction。
TransactionRequest request =
        new TransactionRequestBuilder(abi, "set", contractAddress)
                .setNonce(nonce)
                .setBlockLimit(blockLimit)
                .setExtension("HelloWorld".getBytes())
                .setGasPrice(BigInteger.TEN)
                .buildRequest(params);

/ / Synchronous send on the chain, get the return
TransactionResponse transactionResponse = transactionService.sendTransaction(request);

/ / Parameters of type String can also be constructed
List<String> params = new ArrayList<>();
params.add("[[\"0xabcd\"],[\"0x1234\"]]");
/ / Construct a request to call the setBytesArrayArray API. The parameter is a two-dimensional array of bytes. Pass in important parameters such as contractAddress, nonce, and blockLimit. Finally, use buildStringParamsRequest to end the construction.。
TransactionRequestWithStringParams requestWithStringParams = 
        new TransactionRequestBuilder(abi, "setBytesArrayArray", contractAddress)
                .setNonce(nonce)
                .setBlockLimit(blockLimit)
                .setExtension("HelloWorld".getBytes())
                .setGasPrice(BigInteger.TEN)
                .buildStringParamsRequest(params);
/ / Synchronous send on the chain, get the return
TransactionResponse transactionResponse = transactionService.sendTransaction(requestWithStringParams);
```

## 3. Solidity generates Java files using the new interface.

The detailed documentation of the Java interface file for generating smart contracts can be seen: [link](./contracts_to_java.html)

In the console after 3.6.0, the contract2java.sh script adds'-t 'option, when the value is 1, the Java file using the new interface is generated, using the same posture as before。For example:

```shell
 bash contract2java.sh solidity  -t 1 -n -s ./contracts/solidity/Incremental.sol
```

Existing Java file transformation methods:

In Java-sdk-Take package org.fisco.bcos.sdk.demo.perf.PerformanceOk in demo as an example:
After the contract object is constructed by deploy, the TransactionManager is set up, and then the request is sent according to the new transaction interface.。

```java
// build the client
Client client = sdk.getClient(groupId);
// deploy the HelloWorld
System.out.println("====== Deploy Ok ====== ");
Ok ok = Ok.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
/ / Set the TransactionManager
ok.setTransactionManager(new DefaultTransactionManager(client));
```
