# 构造交易与发送

标签：``java-sdk`` ``发送交易`` ``使用接口签名发送交易`` ``组装交易`` ``合约调用``

----

```eval_rst
.. note::
    Java SDK同时支持将 `solidity` 转换为 `java` 文件后，调用相应的 `java` 方法部署和调用合约，也支持构造交易的方式部署和调用合约，这里主要展示交易构造与发送，前者的使用方法请参考 `这里 <./contracts_to_java.html>`_ 
```

## 1. 概念解析：合约的部署与调用

合约的操作可分为合约部署和合约调用两大类。其中合约调用又可以被区分为『交易』和『查询』。

**合约部署**是指新创建和发布一个合约。交易创建传入的数据会被转换为 EVM 字节码并执行，执行的输出将作为合约代码被永久存储。

**合约调用**是指调用已部署的合约的函数。合约调用又可以被区分为『交易』和『查询』。

**“查询”**：被view/pure修饰符修饰的方法一般称为“查询”，“查询”无需被同步和发送给到其他节点全网共识。

**“交易”**：未被修饰的才会称为“交易”。，而“交易”需发送全网进行上链的共识。

以下是“交易”和“查询”更详细的区别。

| 内容             | 查询            | 交易             |
|------------------|-----------------|------------------|
| 合约表现         | view修饰        | 无view修饰符     |
| ABI表现          | "constant":true | "constant":false |
| 是否需要签名     | 否              | 是               |
| rpc类型          | call            | sendTransaction  |
| 执行节点         | 所有节点        | 所有共识节点     |
| 是否消耗gas      | 否              | 是               |
| 是否变更存储状态 | 否              | 是               |

## 2. 前提：准备合约的abi和binary文件

控制台提供一个专门的编译合约工具，方便开发者将Solidity/webankblockchain-liquid（以下简称WBC-Liquid）合约文件编译生成Java文件和abi、binary文件，具体使用方式[参考这里](./contracts_to_java.html)。

通过运行contract2java 脚本，生成的abi和binary文件分别位于contracts/sdk/abi、contracts/sdk/bin目录下（其中，国密版本编译产生的文件位于contracts/sdk/abi/sm和contracts/sdk/bin/sm文件夹下）。可将文件复制到项目的目录下，例如src/main/resources/abi和src/main/resources/bin。

为了便于演示，我们使用了以下HelloWorld的Solidity合约。

```solidity
pragma solidity ^0.6.0;

contract HelloWorld{
    string public name;
    constructor() public{
       name = "Hello, World!";
    }

    function set(string memory n) public{
        name = n;
    }
}
```

**编译合约，生成abi和binary:**

```shell
# 切换到控制台所在目录
$ cd ~/fisco/console

# 调用sol2java.sh脚本，编译contracts/solidity目录下的HelloWorld合约：
$ bash contract2java.sh solidity -p org.com.fisco -s contracts/solidity/HelloWorld.sol

$ tree . -L1
|-- abi # 编译生成的abi目录，存放solidity合约编译的abi文件
|   |-- HelloWorld.abi
|-- bin # 编译生成的bin目录，存放solidity合约编译的bin文件
|   |-- HelloWorld.bin
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|       |-- com
|           |-- fisco
|               |-- HelloWorld.java # Solidity编译的HelloWorld Java文件
```

至此`HelloWorld`合约的abi和binary文件均已生成

## 3. 初始化SDK

基于配置文件，初始化SDK，如：

```java
// 初始化BcosSDK对象
BcosSDK sdk = BcosSDK.build(configFile);
// 获取Client对象，此处传入的群组名 group0
Client client = sdk.getClient("group0");
// 构造AssembleTransactionProcessor对象，需要传入client对象，CryptoKeyPair对象和abi、binary文件存放的路径。abi和binary文件需要在上一步复制到定义的文件夹中。
CryptoKeyPair keyPair = client.getCryptoSuite().getCryptoKeyPair();
```

## 4. 初始化AssembleTransactionProcessor对象

Java SDK提供了基于abi和binary文件来直接部署和调用合约的方式。本场景下适用于默认的情况，通过创建和使用`AssembleTransactionProcessor`对象来完成合约相关的部署、调用和查询等操作。

```java
AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "src/main/resources/abi/", "src/main/resources/bin/");
```

**特别的：** 假如只交易和查询，而不部署合约，那么就不需要复制binary文件，且在构造时无需传入binary文件的路径，例如构造方法的最后一个参数可传入空字符串。

```java
AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "src/main/resources/abi/", "");
```

**特别的：** 也可以不传入任何ABI文件目录，后续操作时手动传入abi字符串也可行。

```java
AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "", "");
```

## 5. 同步方式部署合约

部署合约调用了deployByContractLoader方法，传入合约名和构造函数的参数，上链部署合约，并获得`TransactionResponse`的结果。

```java
// 部署HelloWorld合约。第一个参数为合约名称，第二个参数为合约构造函数的列表，是List<Object>类型。
TransactionResponse response = transactionProcessor.deployByContractLoader("HelloWorld", new ArrayList<>());
// 也可以手动传入bin和abi
TransactionResponse response = transactionProcessor.deployAndGetResponse(abi, bin, new ArrayList<>(), null);
```

`TransactionResponse`的数据结构如下：

- returnCode: 返回的响应码。其中0为成功。
- returnMessages: 返回的错误信息。
- TransactionReceipt：上链返回的交易回执。
- ContractAddress: 部署或调用的合约地址。
- events: 如果有触发日志记录，则返回解析后的日志返回值，返回Json格式的字符串。
- returnObject: 返回值Java类型。
- returnABIObject: 返回值ABI类型。
- receiptMessages: 返回解析后的交易回执信息。

`returnCode`与`returnMessages`对应表汇总[参考这里](./retcode_retmsg.md)

## 6. 同步方式发送交易

调用合约交易使用了`sendTransactionAndGetResponseByContractLoader`来调用合约交易，此处展示了如何调用`HelloWorld`中的`set`函数。

```java
    // 创建调用交易函数的参数，此处为传入一个参数
    List<Object> params = new ArrayList<>();
    params.add("test");
    // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
    TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader("HelloWorld", helloWorldAddrss, "set", params);

    // 也可以手动传入ABI文件调用
    TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponse(helloWroldAddress, abi, "set", params);
```

## 7.调用合约查询接口

查询合约直接通过调用链上的节点查询函数即可返回结果，无需共识；因此所有的查询交易都是同步方式通讯的。查询合约使用了`sendCallByContractLoader`函数来查询合约，此处展示了如何调用`HelloWorld`中的`name`函数来进行查询。

```java
// 查询HelloWorld合约的『name』函数，合约地址为helloWorldAddress，参数为空
CallResponse callResponse = transactionProcessor.sendCallByContractLoader("HelloWorld", helloWorldAddrss, "name", new ArrayList<>());

// 也可以手动传入ABI文件调用
CallResponse callResponse = transactionProcessor.sendCall("", helloWorldAddrss, "name", new ArrayList<>());
```

## 8. 拼接合约方法签名的方式发送交易

此外，对于特殊的场景，可以通过接口签名的方式DIY拼装交易和发送交易。

例如上述`HelloWorld`智能合约定义的set方法的签名为 `set(string)`

```java
// 使用WBC-Liquid合约时_isWasm 为true，Solidity合约则为false
ContractCodec contractCodec = new ContractCodec(client.getCryptoSuite(), _isWasm); 
String setMethodSignature = "set(string)";
byte[] txData = contractCodec.encodeMethodByInterface(setMethodSignature, new Object[]{new String("Hello World")});
```

由于通过构造接口签名的方式无需提供abi，故可以构造一个`TransactionProcessor`来操作。同样可使用`TransactionProcessorFactory`来构造。

```java
TransactionProcessor transactionProcessor = TransactionProcessorFactory.createTransactionProcessor(client, keyPair);
```

发送交易到FISCO BCOS节点并接收回执。

```java
// 如果使用WBC-Liquid，第三个参数应该使用 TransactionAttribute.LIQUID_SCALE_CODEC
TransactionReceipt transactionReceipt = transactionProcessor.sendTransactionAndGetReceipt(contractAddress, txData, TransactionAttribute.EVM_ABI_CODEC);
```

在执行成功后需要手动解析交易回执中的结果信息。更详细的使用方式可以参考：[交易回执解析](../transaction_decode.md)

```java
TransactionDecoderService txDecoder = new TransactionDecoderService(client.getCryptoSuite(), client.isWASM());
TransactionResponse transactionResponse = txDecoder.decodeReceiptWithValues(abi,"set",transactionReceipt);
```

## 9. 采用callback的方式异步操作合约

### 9.1 定义回调类

异步发送交易的时候，可以自定义回调类，实现和重写回调处理函数。

自定义的回调类需要继承抽象类`TransactionCallback`, 实现`onResponse`方法。同时，可按需决定是否需要重写`onError`、`onTimeout`等方法。

例如，我们定义一个简单的回调类。该回调类实现了一个基于可重入锁的异步调用效果，可减少线程的同步等待时间。

```java
public class TransactionCallbackMock extends TransactionCallback {
    private TransactionReceipt transactionReceipt;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition condition;

    public TransactionCallbackMock() {
        condition = reentrantLock.newCondition();
    }

    public TransactionReceipt getResult() {
        try {
            reentrantLock.lock();
            while (transactionReceipt == null) {
                condition.awaitUninterruptibly();
            }
            return transactionReceipt;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void onResponse(TransactionReceipt transactionReceipt) {
        try {
            reentrantLock.lock();
            this.transactionReceipt = transactionReceipt;
            condition.signal();
        } finally {
            reentrantLock.unlock();
        }
    }
}
```

### 9.2 采用callback的方式异步部署合约

首先，创建一个回调类的实例。然后使用`deployByContractLoaderAsync`方法，异步部署合约。

```java
// 创建回调类的实例
TransactionCallbackMock callbackMock = new TransactionCallbackMock();
AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "src/main/resources/abi/", "");
// 异步部署合约
transactionProcessor.deployByContractLoaderAsync("HelloWorld", new ArrayList<>(), callbackMock);
// 异步等待获取回执
TransactionReceipt transactionReceipt = callbackMock.getResult();
```

### 9.3 采用callback的方式发送交易

参考部署合约交易，可采用异步的方式发送交易。

```java
// 创建回调类的实例
TransactionCallbackMock callbackMock = new TransactionCallbackMock();
// 定义构造参数
List<Object> params = Lists.newArrayList("test");
// 异步调用合约交易
transactionProcessor.sendTransactionAsync(to, abi, "set", params, callbackMock);
// 异步等待获取回执
TransactionReceipt transactionReceipt = callbackMock.getResult();
```

## 10. 采用CompletableFuture的方式异步操作合约

SDK还支持使用`CompletableFuture`封装的方式异步部署合约。

```java
// 异步部署交易，并获得CompletableFuture<TransactionReceipt> 对象
CompletableFuture<TransactionReceipt> future = transactionProcessor.deployAsync(abi, bin, new ArrayList<>(),"");
// 定义正常返回的业务逻辑
future.thenAccept(
    tr -> {
       doSomething(tr);
    });
// 定义异常返回的业务逻辑
future.exceptionally(
    e -> {
        doSomething(e);
        return null;
    });
```

## 4. 详细API功能介绍

`AssembleTransactionProcessor`支持自定义参数发送交易，支持异步的方式来发送交易，也支持返回多种封装方式的结果。

可参考Java doc：[AssembleTransactionProcessor](./javadoc/javadoc/org/fisco/bcos/sdk/v3/transaction/manager/AssembleTransactionProcessor.html)

详细的API功能如下。

- **public void deployOnly(String abi, String bin, List\<Object\> params)：** 传入合约abi、bin和构造函数参数来部署合约，不接收回执结果。
- **public TransactionResponse deployAndGetResponse(String abi, String bin, List\<Object\> params) ：** 传入合约abi、bin和构造函数参数来部署合约，接收回执结果
- **TransactionResponse deployAndGetResponseWithStringParams(String abi, String bin, List\<String\> params)：** 传入合约abi和String类型的List作为构造函数参数来部署合约，接收TransactionResponse结果。
- **void deployAsync(String abi, String bin, List\<Object\> params, TransactionCallback callback)：** 传入合约abi、构造好的合约构造函数和callback来异步部署合约
- **CompletableFuture\<TransactionReceipt\> deployAsync(String abi, String bin, List\<Object\> params)：** 传入合约abi、bin和构造函数参数来部署合约，接收CompletableFuture封装的回执结果
- **TransactionResponse deployByContractLoader(String contractName, List\<Object\> params)：** 传入合约名和构造好的合约构造函数，接收TransactionResponse结果。
- **void deployByContractLoaderAsync(String contractName, List\<Object\> args, TransactionCallback callback)：** 传入合约名和合约构造函数参数以及callback，来异步部署合约
- **TransactionReceipt sendTransactionAndGetReceiptByContractLoader(String contractName, String contractAddress, String functionName, List\<Object\> params)：** 传入调用合约名称、合约地址、函数名和函数参数，接收交易回执
- **TransactionResponse sendTransactionAndGetResponse(String to, String abi, String functionName, List\<Object\> params)：** 传入调用合约地址、合约abi、函数名和函数参数，接收TransactionResponse结果
- **TransactionResponse sendTransactionWithStringParamsAndGetResponse(String to, String abi, String functionName, List\<String\> params)：** 传入调用合约地址、合约abi、函数名和String类型List的函数参数，接收TransactionResponse结果
- **void sendTransactionAsync(String to, String abi, String functionName, List\<Object\> params, TransactionCallback callback)：** 传入调用合约地址、合约abi、函数名、函数参数、callback，异步发送交易。
- **void sendTransactionAndGetReceiptByContractLoaderAsync(String contractName,String contractAddress, String functionName, List\<Object\> args, TransactionCallback callback)：** 传入调用合约名、合约地址、函数名、函数参数、callback，异步发送交易。
- **TransactionResponse sendTransactionAndGetResponseByContractLoader(String contractName, String contractAddress, String functionName, List\<Object\> funcParams)：** 传入调用合约名、合约地址、函数名、函数参数，并接收TransactionResponse结果。
- **CallResponse sendCallByContractLoader(String contractName, String contractAddress, String functionName, List\<Object\> params)：** 传入调用合约名、合约地址、函数名、函数参数，并接收CallResponse结果。
- **CallResponse sendCall(String from, String to, String abi, String functionName, List\<Object\> args)：** 传入调用者地址、合约地址、合约abi、函数名、函数参数，并接收CallResponse结果。
- **CallResponse sendCall(CallRequest callRequest)：** 传入CallRequest，并接收CallResponse结果。
- **CallResponse sendCallWithStringParams(String from, String to, String abi, String functionName, List\<String\> paramsList):** 传入调用者地址、合约地址、合约abi、函数名、String类型List的函数参数，并接收CallResponse结果。
