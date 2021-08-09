# 基于ABI和BIN调用合约

标签：``java-sdk`` ``发送交易`` ``使用接口签名发送交易`` ``组装交易`` ``合约调用``

----
```eval_rst
.. note::
    Java SDK同时支持将 `solidity` 转换为 `java` 文件后，调用相应的 `java` 方法部署和调用合约，也支持构造交易的方式部署和调用合约，这里主要展示交易构造与发送，前者的使用方法请参考 `这里 <./quick_start.html#solidityjava>`_ 
```

## 1. 概念解析：部署和调用
合约的操作可分为合约部署和合约调用两大类。其中合约调用又可以被区分为『交易』和『查询』。

**合约部署**是指新创建和发布一个合约。交易创建传入的数据会被转换为 EVM 字节码并执行，执行的输出将作为合约代码被永久存储。

**合约调用**是指调用已部署的合约的函数。合约调用又可以被区分为『交易』和『查询』。

**“查询”**：被view修饰符修饰的方法一般称为“查询”，“查询”无需被同步和发送给到其他节点全网共识。

**“交易”**：未被修饰的才会称为“交易”。，而“交易”需发送全网进行上链的共识。

以下是“交易”和“查询”更详细的区别。

| 内容 | 查询 | 交易 |
| ---- | ---- | ----|
| 合约表现 | view修饰 | 无view修饰符 |
| ABI表现 | "constant":true | "constant":false |
| 是否需要签名 | 否  | 是 |
| rpc类型  | call | sendTransaction |
| 执行节点 | 所有共识节点 | 执行层面 |
| 是否消耗gas | 否 | 是 |
| 是否变更存储状态 | 否 | 是 |


## 2. 快速上手
在快速上手环节，使用同步方式来发送。

### 2.1 准备abi和binary文件
控制台提供一个专门的编译合约工具，方便开发者将Solidity合约文件编译生成Java文件和abi、binary文件，具体使用方式[参考这里](../../console/console.html#id10)。

通过运行sol2java.sh脚本，生成的abi和binary文件分别位于contracts/sdk/abi、contracts/sdk/bin目录下（其中，国密版本编译产生的文件位于contracts/sdk/abi/sm和contracts/sdk/bin/sm文件夹下）。可将文件复制到项目的目录下，例如src/main/resources/abi和src/main/resources/bin。

为了便于演示，我们使用了以下`HelloWorld`的合约。
```
pragma solidity ^0.4.25;

contract HelloWorld{
    string public name;
    constructor() public{
       name = "Hello, World!";
    }

    function set(string n) public{
        name = n;
    }
}
```

**编译合约，生成abi和binary:**

```bash
# 切换到控制台所在目录
$ cd ~/fisco/console

# 调用sol2java.sh脚本，编译HelloWorld合约
$ bash sol2java.sh org HelloWorld.sol

# 生成的abi位于contracts/sdk/abi/HelloWorld.abi路径
$ ls contracts/sdk/abi/HelloWorld.abi

# 生成的非国密版本的bin位于contracts/sdk/bin/HelloWorld.bin路径
$ ls contracts/sdk/bin/HelloWorld.bin

# 生成的国密版本bin位于contracts/sdk/bin/sm/HelloWorld.bin路径
$ ls contracts/sdk/bin/sm/HelloWorld.bin
```

至此`HelloWorld`合约的abi和binary文件均已生成。


### 2.2 初始化SDK
基于配置文件，初始化SDK，如：
```java
    // 初始化BcosSDK对象
    BcosSDK sdk = new BcosSDK(configFile);
    // 获取Client对象，此处传入的群组ID为1
    Client client = sdk.getClient(Integer.valueOf(1));
    // 构造AssembleTransactionProcessor对象，需要传入client对象，CryptoKeyPair对象和abi、binary文件存放的路径。abi和binary文件需要在上一步复制到定义的文件夹中。
    CryptoKeyPair keyPair = client.getCryptoSuite().createKeyPair();
```


### 2.3 初始化配置对象

#### 2.3.1 部署、交易和查询

Java SDK提供了基于abi和binary文件来直接部署和调用合约的方式。本场景下适用于默认的情况，通过创建和使用`AssembleTransactionProcessor`对象来完成合约相关的部署、调用和查询等操作。

```java
       AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "src/main/resources/abi/", "src/main/resources/bin/");
```

#### 2.3.2 仅交易和查询
假如只交易和查询，而不部署合约，那么就不需要复制binary文件，且在构造时无需传入binary文件的路径，例如构造方法的最后一个参数可传入空字符串。

```java
    AssembleTransactionProcessor transactionProcessor = TransactionProcessorFactory.createAssembleTransactionProcessor(client, keyPair, "src/main/resources/abi/", "");
```

### 2.4 发送操作指令
完成初始化SDK和配置对象后，可以发起合约操作指令。

#### 2.4.1 同步方式部署合约
部署合约调用了deployByContractLoader方法，传入合约名和构造函数的参数，上链部署合约，并获得`TransactionResponse`的结果。

```java
    // 部署HelloWorld合约。第一个参数为合约名称，第二个参数为合约构造函数的列表，是List<Object>类型。
    TransactionResponse response = transactionProcessor.deployByContractLoader("HelloWorld", new ArrayList<>());

```

`TransactionResponse`的数据结构如下：
- returnCode: 返回的响应码。其中0为成功。
- returnMessages: 返回的错误信息。
- TransactionReceipt：上链返回的交易回执。
- ContractAddress: 部署或调用的合约地址。
- values: 如果调用的函数存在返回值，则返回解析后的交易返回值，返回Json格式的字符串。
- events: 如果有触发日志记录，则返回解析后的日志返回值，返回Json格式的字符串。
- receiptMessages: 返回解析后的交易回执信息。

例如，部署`HelloWorld`合约的返回结果：

```json
{
  "returnCode": 0,
  "returnMessage": "Success",
  "transactionReceipt": {
    "transactionHash": "0xcfdfb78be52b232afdee717826f9516af98fb2d67ee743da4b78e8c22172112b",
    "transactionIndex": "0x0",
    "root": "0xf6503b5f1a319dbd2c938d7e371b89441c238271bbaabf3d650112017158a658",
    "blockNumber": "0x14b",
    "blockHash": "0x817e0aaaba5448a6ac62fc2531be793cf6de9fed70c73ed8837082b7fcf74881",
    "from": "0xcaa405b5dd47e7f28e6b862c198c15e923000c0b",
    "to": "0x0000000000000000000000000000000000000000",
    "gasUsed": "0x44683",
    "contractAddress": "0x9dbaf42da05a0148d2ca9905870a91085c23ce71",
    "logs": [],
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "status": "0x0",
    "input": "0x608060405234801561001057600080fd5b506040805190810160405280600d81526020017f48656c6c6f2c20576f726c6421000000000000000000000000000000000000008152506000908051906020019061005c929190610062565b50610107565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a357805160ff19168380011785556100d1565b828001600101855582156100d1579182015b828111156100d05782518255916020019190600101906100b5565b5b5090506100de91906100e2565b5090565b61010491905b808211156101005760008160009055506001016100e8565b5090565b90565b6102d3806101166000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680633590b49f14610051578063b11b6883146100ba575b600080fd5b34801561005d57600080fd5b506100b8600480360381019080803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919291929050505061014a565b005b3480156100c657600080fd5b506100cf610164565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561010f5780820151818401526020810190506100f4565b50505050905090810190601f16801561013c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b8060009080519060200190610160929190610202565b5050565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101fa5780601f106101cf576101008083540402835291602001916101fa565b820191906000526020600020905b8154815290600101906020018083116101dd57829003601f168201915b505050505081565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061024357805160ff1916838001178555610271565b82800160010185558215610271579182015b82811115610270578251825591602001919060010190610255565b5b50905061027e9190610282565b5090565b6102a491905b808211156102a0576000816000905550600101610288565b5090565b905600a165627a7a723058209072782fc019ac745aa91b6fede6c358df1b03584b1bf5193a8c66d68a6880f30029",
    "output": "0x",
    "txProof": null,
    "receiptProof": null,
    "message": null,
    "statusOK": true
  },
  "contractAddress": "0x9dbaf42da05a0148d2ca9905870a91085c23ce71",
  "values": null,
  "events": "{}",
  "receiptMessages": "Success"
}
```

 
#### 2.4.2 同步方式发送交易

调用合约交易使用了`sendTransactionAndGetResponseByContractLoader`来调用合约交易，此处展示了如何调用`HelloWorld`中的`set`函数。

```java
    // 创建调用交易函数的参数，此处为传入一个参数
    List<Object> params = new ArrayList<>();
    params.add("test");
    // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
    TransactionResponse transactionResponse = transactionProcessor.sendTransactionAndGetResponseByContractLoader("HelloWorld", helloWorldAddrss, "set", params);
```

例如，调用`HelloWorld`合约的返回如下：

```json
{
  "returnCode": 0,
  "returnMessage": "Success",
  "transactionReceipt": {
    "transactionHash": "0x46b510dfff02c327432362db76555c75211aab17b3b75221137132123773fe8f",
    "transactionIndex": "0x0",
    "root": "0x0000000000000000000000000000000000000000000000000000000000000000",
    "blockNumber": "0x156",
    "blockHash": "0x2d1ef0246bc0484e0fecd2a0b6671c19b69c86ac9e464b162ff4a932908151a0",
    "from": "0x77a5933b5af032a313fde655d8290c134aeeb0d5",
    "to": "0x975810f096e8d8b2daa9ee399f5ce809c0a12f1b",
    "gasUsed": "0x6032",
    "contractAddress": "0x0000000000000000000000000000000000000000",
    "logs": [],
    "logsBloom": "0x00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
    "status": "0x0",
    "input": "0x4ed3885e000000000000000000000000000000000000000000000000000000000000002000000000000000000000000000000000000000000000000000000000000000047465737400000000000000000000000000000000000000000000000000000000",
    "output": "0x",
    "txProof": null,
    "receiptProof": null,
    "message": null,
    "statusOK": true
  },
  "contractAddress": "0x0000000000000000000000000000000000000000",
  "values": "[]",
  "events": "{}",
  "receiptMessages": "Success"
}
```

#### 2.4.3 调用合约查询接口
查询合约直接通过调用链上的节点查询函数即可返回结果，无需共识；因此所有的查询交易都是同步方式通讯的。查询合约使用了`sendCallByContractLoader`函数来查询合约，此处展示了如何调用`HelloWorld`中的`name`函数来进行查询。

```java
    // 查询HelloWorld合约的『name』函数，合约地址为helloWorldAddress，参数为空
    CallResponse callResponse = transactionProcessor.sendCallByContractLoader("HelloWorld", helloWorldAddrss, "name", new ArrayList<>());
```

查询函数返回如下：

```
{
  "returnCode": 0,
  "returnMessage": "Success",
  "values": "[\"test\"]"
}
```


## 3. 更多操作
### 3.1 拼接签名的方式发送交易
此外，对于特殊的场景，可以通过接口签名的方式DIY拼装交易和发送交易。

例如上述`HelloWorld`智能合约定义的set方法的签名为 `set(string)`

#### 3.1.1 构造接口签名

```java
    ABICodec abiCodec = new ABICodec(client.getCryptoSuite());
    String setMethodSignature = "set(string)";
    String abiEncoded = abiCodec.encodeMethodByInterface(setMethodSignature, new Object[]{new String("Hello World")});
```

#### 3.1.2 构造TransactionProcessor
由于通过构造接口签名的方式无需提供abi，故可以构造一个`TransactionProcessor`来操作。同样可使用`TransactionProcessorFactory`来构造。
```java
    // ……
    TransactionProcessor transactionProcessor = TransactionProcessorFactory.createTransactionProcessor(client, keyPair);
```

#### 3.1.3 发送交易
发送交易到FISCO BCOS节点并接收回执。
```java
    // ……
    TransactionReceipt transactionReceipt = transactionProcessor.sendTransactionAndGetReceipt(contractAddress, abiEncoded, keyPair);
```

### 3.2 采用callback的方式异步操作合约
#### 3.2.1 定义回调类
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

#### 3.2.2 采用callback的方式异步部署合约
首先，创建一个回调类的实例。然后使用`deployByContractLoaderAsync`方法，异步部署合约。

```java
    // 创建回调类的实例
    TransactionCallbackMock callbackMock = new TransactionCallbackMock();
    // 异步部署合约
    transactionProcessor.deployByContractLoaderAsync("HelloWorld", new ArrayList<>(), callbackMock);
    // 异步等待获取回执
    TransactionReceipt transactionReceipt = callbackMock.getResult();
```

#### 3.2.3 采用callback的方式发送交易
参考部署合约交易，可采用异步的方式发送交易。

```java
    // 创建回调类的实例
    TransactionCallbackMock callbackMock = new TransactionCallbackMock();
    // 定义构造参数
    List<Object> params = Lists.newArrayList("test");
    // 异步调用合约交易
    transactionProcessor.sendTransactionAsync(to, abi, "set", params, callbackMock );
    // 异步等待获取回执
    TransactionReceipt transactionReceipt = callbackMock.getResult();
```

### 3.3 采用CompletableFuture的方式异步操作合约

#### 3.3.1 采用CompletableFuture的方式部署合约
SDK还支持使用`CompletableFuture`封装的方式异步部署合约。

```java
    // 异步部署交易，并获得CompletableFuture<TransactionReceipt> 对象
    CompletableFuture<TransactionReceipt> future =
        transactionProcessor.deployAsync(abi, bin, new ArrayList<>());
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