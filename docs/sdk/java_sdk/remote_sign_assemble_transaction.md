# 集成外部签名服务调用合约

标签：``java-sdk`` ``发送交易`` ``外部签名`` ``组装交易`` ``合约调用``

----

[AssembleTransactionProcessor](./assemble_transaction.html)已经支持和覆盖了常见的合约操作接口。但是在真实的业务场景中，对于某些特定的业务场景，需要调用硬件加密机或远程签名服务对该哈希进行签名。为此，我们在AssembleTransactionProcessor的基础上进一步提供了AssembleTransactionWithRemoteSignProcessor，来便于用户集成自定义签名服务。

## 1. 概念解析：部署和调用
部署、调用（交易和查询）的相关概念可参考[概念解析：部署和调用](./assemble_transaction.html#id1)


## 2. 快速上手
SDK支持同步和异步方式来调用合约。在快速上手环节，首先展示使用同步方式来调用合约。

### 2.1 准备abi和binary文件
控制台提供一个专门的编译合约工具，方便开发者将Solidity合约文件编译生成Java文件和abi、binary文件，具体使用方式[参考这里](../../console/console.html#id10)。

通过运行sol2java.sh脚本，生成的abi和binary文件分别位于contracts/sdk/abi、contracts/sdk/bin目录下（其中，国密版本编译产生的文件位于contracts/sdk/abi/sm和contracts/sdk/bin/sm文件夹下）。可将文件复制到项目的目录下，例如src/main/resources/abi和src/main/resources/bin。

为了便于演示，我们使用了以下HelloWorld的合约。
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

至此`HelloWorld`合约的abi和binary文件均已生成

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

#### 2.3.1 自定义外部签名服务
外部签名的服务需要实现`RemoteSignProviderInterface`接口。
``` java
public interface RemoteSignProviderInterface {
    // 同步签名接口
    public SignatureResult requestForSign(byte[] dataToSign, int cryptoType);

    // 异步签名接口
    public void requestForSignAsync(
            byte[] dataToSign, int cryptoType, RemoteSignCallbackInterface callback);
}
```

用户可按需实现`requestForSign和requestForSignAsync`接口，实现调用外部签名服务，同步或异步返回结果的逻辑。具体的业务逻辑视业务场景自主封装，可以是调用硬件加签机服务，也可以是调用外部托管的签名服务。当异步签名接口结果返回后，会自动回调`RemoteSignCallbackInterface`中定义的`handleSignedTransaction`接口。该接口定义如下：
```java
public interface RemoteSignCallbackInterface {
    /**
     * receive the signature,and execute the callback function later.
     *
     * @param signature
     * @return result code
     */
    public int handleSignedTransaction(SignatureResult signature);
}
```

为了便于演示，我们创建一个外部签名服务的Mock类(代码位置`src/integration-test/java/org/fisco/bcos/sdk/transaction/mock/RemoteSignProviderMock` )，该类模拟实现了`RemoteSignProviderInterface`的同步签名接口`requestForSign`和异步签名接口`requestForSignAsync`。

#### 2.3.2 部署、交易和查询

Java SDK提供了基于abi和binary文件来直接部署和调用合约的方式。本场景下适用于默认的情况，通过创建和使用`AssembleTransactionWithRemoteSignProcessor`对象来完成合约相关的部署、调用和查询等操作。此处， 假设我们创建了一个外部签名的Mock类 `RemoteSignProviderMock`。

```java
      // remoteSignProviderMock 对象需实现RemoteSignCallbackInterface 接口
      AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor =
          TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
              client, cryptoKeyPair, "src/main/resources/abi/", "src/main/resources/bin/", remoteSignProviderMock);
```

#### 2.3.3 仅交易和查询
假如只交易和查询，而不部署合约，那么就不需要复制binary文件，且在构造时无需传入binary文件的路径，例如binary路径参数可传入空字符串。

```java
      // remoteSignProviderMock 对象需实现RemoteSignCallbackInterface 接口
      AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor =
          TransactionProcessorFactory.createAssembleTransactionWithRemoteSignProcessor(
                client, cryptoKeyPair, "src/main/resources/abi/", "", remoteSignProviderMock);
```

### 2.4 发送操作指令
完成初始化SDK和配置对象后，可以发起合约操作指令。

#### 2.4.1 同步方式部署合约
部署合约调用了`deployByContractLoader`方法，传入合约名和构造函数的参数，上链部署合约，并获得`TransactionResponse`的结果。

```java
    // 部署HelloWorld合约。第一个参数为合约名称，第二个参数为合约构造函数的列表，是List<Object>类型。
    TransactionResponse response =
                assembleTransactionWithRemoteSignProcessor.deployByContractLoader(
                        "HelloWorld", new ArrayList<>());
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
    List<Object> params = Lists.newArrayList("test");
    // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
    TransactionResponse transactionResponse =
        assembleTransactionWithRemoteSignProcessor.sendTransactionAndGetResponse(
            helloWorldAddrss, abi, "set", params);
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
查询合约直接通过调用链上的节点查询函数即可返回结果，无需共识；因此所有的查询交易都是同步的。查询合约使用了`sendCallByContractLoader`函数来查询合约，此处展示了如何调用`HelloWorld`中的`name`函数来进行查询。

```java
    // 查询HelloWorld合约的『name』函数，合约地址为helloWorldAddress，参数为空
    CallResponse callResponse1 =
        assembleTransactionWithRemoteSignProcessor.sendCallByContractLoader(
            "HelloWorld", helloWorldAddrss, "name", new ArrayList<>());
```

查询函数返回如下：

```json
{
  "returnCode": 0,
  "returnMessage": "Success",
  "values": "[\"test\"]"
}
```


## 3. 更多操作接口
在调用外部签名服务的时候，可通过同步或异步的方式。异步调用可采用callback或CompletableFuture等方式。

### 3.1 采用callback的方式异步操作合约

#### 3.1.1 定义回调类
异步调用外部签名服务的时候，可以自定义回调类，实现和重写回调处理函数。

自定义的回调类需要继承抽象类`RemoteSignCallbackInterface`, 实现`handleSignedTransaction`方法。

例如，我们定义一个简单的回调类。该回调类实现了异步回调发送交易到节点的效果。
```java
public class RemoteSignCallbackMock implements RemoteSignCallbackInterface {

    AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor;
    RawTransaction rawTransaction;

    public RemoteSignCallbackMock(
            AssembleTransactionWithRemoteSignProcessor assembleTransactionWithRemoteSignProcessor,
            RawTransaction rawTransaction) {
        this.assembleTransactionWithRemoteSignProcessor =
                assembleTransactionWithRemoteSignProcessor;
        this.rawTransaction = rawTransaction;
    }

    /**
     * 签名结果回调的实现
     *
     * @param signatureStr 签名服务回调返回的签名结果串
     * @return *
     */
    @Override
    public int handleSignedTransaction(String signatureStr) {
        System.out.println(System.currentTimeMillis() + " SignatureResult: " + signatureStr);
        // 完成了交易签名后，将其发送出去
        TransactionReceipt tr =
                assembleTransactionWithRemoteSignProcessor.signAndPush(
                        rawTransaction, signatureStr);
        return 0;
    }
}

```

#### 3.1.2 采用callback的方式异步部署合约
首先，创建一个回调类的实例。然后使用`deployAsync`方法异步部署合约。

```java
    // 创建 RawTransaction
    RawTransaction rawTransaction =
                assembleTransactionWithRemoteSignProcessor.getDeployedRawTransaction(
                        abi, bin, new ArrayList<>());
    // 创建回调实例
    RemoteSignCallbackMock callbackMock =
                new RemoteSignCallbackMock(
                        assembleTransactionWithRemoteSignProcessor, rawTransaction);
    // 异步部署合约
    assembleTransactionWithRemoteSignProcessor.deployAsync(rawTransaction, callbackMock);
```

#### 3.1.3 采用callback的方式调用交易
参考部署合约交易，可采用异步的方式发送交易。

```java
    // 创建 RawTransaction
    RawTransaction sendTxRawTransaction =
                assembleTransactionWithRemoteSignProcessor.getRawTransaction(
                        helloWorldAddrss, abi, "set", params);
    // 创建回调实例
    RemoteSignCallbackMock callbackMock2 =
                new RemoteSignCallbackMock(
                        assembleTransactionWithRemoteSignProcessor, sendTxRawTransaction);
    // 发送异步回调交易
    assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                helloWorldAddrss, abi, "set", params, callbackMock2);
```

### 3.2 采用CompletableFuture的方式异步操作合约

#### 3.2.1 采用CompletableFuture的方式部署合约
SDK还支持使用CompletableFuture封装的方式异步部署合约。

```java
    // 异步部署交易，并获得CompletableFuture<TransactionReceipt> 对象
    CompletableFuture<TransactionReceipt> future =
                assembleTransactionWithRemoteSignProcessor.deployAsync(abi, bin, new ArrayList<>());
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

#### 3.2.2 采用CompletableFuture的方式发送交易
同部署合约。

```java
    // 异步部署交易，并获得CompletableFuture<TransactionReceipt> 对象
    CompletableFuture<TransactionReceipt> future2 =
                assembleTransactionWithRemoteSignProcessor.sendTransactionAsync(
                        helloWorldAddrss, abi, "set", params);
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

`AssembleTransactionWithRemoteSignProcessor`支持自定义参数发送交易，支持异步的方式来发送交易，也支持返回多种封装方式的结果。

`AssembleTransactionWithRemoteSignProcessor`继承了 `AssembleTransactionProcessor`类，实现了`AssembleTransactionWithRemoteSignProviderInterface`接口。

继承的接口参考[AssembleTransactionWithRemoteSignProcessor](./assemble_transaction.html#api)

详细的API功能如下。

- **void deployAsync(RawTransaction rawTransaction, RemoteSignCallbackInterface remoteSignCallbackInterface)：** 传入部署合约的RawTransaction报文和签名服务的callback来部署合约，自动执行回调函数。
- **void deployAsync(String abi, String bin, List\<Object\> params, RemoteSignCallbackInterface remoteSignCallbackInterface) ：** 传入合约abi、bin和构造函数参数和签名服务的callback来部署合约，自动执行回调函数。
- **void deployByContractLoaderAsync(String contractName, List\<Object\> params, RemoteSignCallbackInterface remoteSignCallbackInterface):** 传入合约名和构造参数以及callback，来异步部署合约
- **void sendTransactionAndGetReceiptByContractLoaderAsync(String contractName, String to, String functionName, List\<Object\> params,RemoteSignCallbackInterface remoteSignCallbackInterface)：** 传入调用合约名、合约地址、函数名、函数参数、签名服务的callback，异步发送交易。
- **CompletableFuture\<TransactionReceipt\> sendTransactionAsync(String to,String abi,String functionName,List\<Object\> params,RemoteSignCallbackInterface remoteSignCallbackInterface)：** 传入调用合约地址、abi、函数名、函数参数、签名服务的callback，同步返回调用，异步获取CompletableFuture处理回执结果。
- **CompletableFuture\<TransactionReceipt\> sendTransactionAsync(String to, String abi, String functionName, List\<Object\> params)：** 传入调用合约地址、abi、函数名、函数参数，同步签名，并同步返回调用，异步获取CompletableFuture处理回执结果。
- **TransactionReceipt signAndPush(RawTransaction rawTransaction, String signatureStr)：** 传入RawTransaction和签名结果，推送给节点，同步接收交易回执。
- **CompletableFuture\<TransactionReceipt\> signAndPush(RawTransaction rawTransaction, byte[] rawTxHash) ：** 传入RawTransaction和签名结果，同步调用签名服务，并异步接收交易回执结果。