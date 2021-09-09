# 交易的组装与发送详解

标签：``java-sdk`` ``组装交易`` 

Java SDK提供了灵活的交易构造接口，针对不同的签名方式，提供了包括[在SDK中对交易进行签名的AssembleTransactionProcessor](./assemble_transaction.html)和[用单独的签名服务对交易进行签名的AssembleTransactionWithRemoteSignProcessor](./remote_sign_assemble_transaction.html)。

本章节进一步详解交易的组装与发送的原理和细节。主要包括：

- `RawTransaction`构造：构造不带有签名的交易
- `RawTransaction`编码：对不带有签名的交易进行编码，并计算其哈希
- `SignedTransaction`构造与编码：将不带有签名的交易与交易签名进行组装编码为带有签名的交易
- `SignedTransaction`发送：将带有签名的交易发送到区块链上

本文以`HelloWorld`合约为例，为大家详细介绍如何使用Java SDK生成`RawTransaction`、如何使用已有的签名服务对`RawTransaction`进行签名、如何将签名服务返回的签名与`RawTransaction`组装成`SignedTransaction`以及如何发送已经签名的交易到链上。

## 1. 准备abi和binary文件
控制台提供一个专门的编译合约工具，方便开发者将Solidity合约文件编译生成Java文件和abi、binary文件，具体使用方式[参考这里](../../console/console.html#id10)。

**下载控制台:**

```bash
# 下载控制台
$ mkdir -p ~/fisco && cd ~/fisco
$ curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.7.1/download_console.sh

# 若因为网络问题导致长时间无法执行以上命令，请尝试以下命令：
$ curl -#LO https://gitee.com/FISCO-BCOS/console/releases/download/v2.7.1/download_console.sh

$ bash download_console.sh
```

**准备合约，这里以HelloWorld为例：**
```bash
$ cd ~/fisco/console
$ cat >> HelloWorld.sol << EOF
pragma solidity>=0.4.24 <0.6.11;
contract HelloWorld {
    string name;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public {
        name = n;
    }
}
EOF
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


## 2. 构造RawTransaction

FISCO BCOS的交易结构可参考[这里](../../design/protocol_description.html#rlp)，构造RawTransaction之前，首先要构造交易内容，基于abi和binary以及参数对交易进行编码。
### 2.1 构造交易内容

交易内容定义了交易要调用的指令以及参数，FISCO BCOS中的交易采用ABI编码，可根据abi和binary以及参数，构造交易内容，主要包括两步：

**步骤一: 创建ABICodec对象**

```java
/**
 * 创建ABICodec对象
 * @param client client对象
 * @return: 创建的ABICodec对象
 */
 public ABICodec createABICodec(Client client)
{
    return new ABICodec(client.getCryptoSuite());
}
```

**步骤二：调用创建的ABICodec对象对交易内容进行编码**

```java
/**
 * 对合约部署类型的交易进行编码
 * @param abiCodec 用于编码交易内容的ABICodeC对象
 * @param abiContent 合约的abi字符串(需要读取第1节生成的abi文件获取)
 * @param binContent binary字符串(需要读取第1节生成的bin文件获取)
 * @param params 部署合约的初始化参数列表
 * @return 编码后的交易内容
 */
public String encodeConstructor(ABICodec abiCodec, String abiContent, String binContent, List<Object> params)
{
    return abiCodec.encodeConstructor(abiContent, binContent, params);
}

/**
 * 对合约调用类型的交易进行编码
 * @param abiCodec 用于编码交易内容的ABICodeC对象
 * @param abiContent 合约的abi字符串(需要读取第1节生成的abi文件获取)
 * @param methodName 需要调用的合约方法名
 * @param param 调用合约的参数列表
 * @return 编码后的交易内容
 */
public String encodeMethod(ABICodec abiCodec, String abiContent, String methodName, List<Object> params)
{
    return abiCodec.encodeMethod(abiContent, methodName, params);
}
```

### 2.2 构造RawTransaction

```java
/**
 * 根据交易内容txData构造RawTransaction，并指定交易发送的地址为to(部署合约时，to为全0的合约地址)
 * @param transactionBuilder 交易构造对象
 * @param client Client对象，用于获取BlockLimit
 * @param to 交易发送的目标地址(部署合约时，to为全0的合约地址)
 * @param txData 编码后的交易
 * @return 创建的RawTransaction
 */
public RawTransaction createRawTransaction(TransactionBuilderInterface transactionBuilder, Client client, String to, String txData)
{
    // 获取chainId和groupId
    Pair<String, Integer> chainIdAndGroupId = TransactionProcessorFactory.getChainIdAndGroupId(client);
    return transactionBuilder.createTransaction(
                        DefaultGasProvider.GAS_PRICE,
                        DefaultGasProvider.GAS_LIMIT,
                        to,
                        txData,
                        BigInteger.ZERO,
                        new BigInteger(chainIdAndGroupId.getLeft()),
                        BigInteger.valueOf(chainIdAndGroupId.getRight()),
                        "");
}
```

## 3. 对RawTransaction进行编码

对第2节产生的RawTransaction进行编码，并计算编码后内容的哈希如下：

```java
/**
 * 对RawTransaction进行RLP编码，返回编码内容的哈希
 *
 * @param transactionEncoder 交易编码器
 * @param client client对象，用于确定使用的哈希算法类型
 * @param rawTransaction 需要编码的交易
 * @return 编码后交易的哈希
 */
public byte[] encodeRawTransactionAndGetHash(
        TransactionEncoderInterface transactionEncoder,
        Client client,
        RawTransaction rawTransaction) {
    byte[] encodedTransaction = transactionEncoder.encode(rawTransaction, null);
    return client.getCryptoSuite().hash(encodedTransaction);
}
```

## 4. 签名服务对交易进行签名

第3节获取RawTransaction的哈希后，可以调用硬件加密机或远程签名服务对该哈希进行签名，该流程因业务系统的不同而有所差异。外部签名服务需实现RemoteSignProviderInterface接口，具体的接口定义如下：

```java
public interface RemoteSignProviderInterface {
    /**
     * request for signature provider service, and return the signature.
     *
     * @param dataToSign data to be signed
     * @param cryptoType: ECDSA=0,SM=1, or self defined
     * @return signature result
     */
    public String requestForSign(byte[] dataToSign, int cryptoType);

    /**
     * request for signature provider service asynchronously
     *
     * @param dataToSign data to be signed
     * @param cryptoType: ECDSA=0,SM=1, or self defined
     * @param callback transaction sign callback
     */
    public void requestForSignAsync(
            byte[] dataToSign, int cryptoType, RemoteSignCallbackInterface callback);
}
```

示例如下，其中`dataToSign`是第3节获取到的交易哈希。Java SDK收到签名服务返回的签名后(这里设为`txSignature`)，需要对齐反序列化为`SignatureResult`对象，接口示例如下：

```java
public SignatureResult decodeSign(String txSignature);

// 若是非国密类型应用，从txSignature可反序列化出 v, r, s, 构造SignatureResult的方法如下:
public SignatureResult decodeECDSASignature(byte v, byte[] r, byte[] s)
{
    return new ECDSASignatureResult(v, r, s);
}

// 若是国密类型应用，从txSignature可反序列化出 pub, r, s, 构造SignatureResult的方法如下:
public SignatureResult decodeECDSASignature(byte[] pub, byte[] r, byte[] s)
{
    return new SM2SignatureResult(pub, r, s);
}
```

## 5. 拼接未签名交易与签名

Java SDK通过第4节获取到签名后，需将未签名交易与签名拼接起来，产生带有签名的交易：

```java
/**
 * 根据RawTransaction和签名结果产生带有签名的交易
 *
 * @param transactionEncoder 交易编码器
 * @param transaction 不带有签名的交易
 * @param signatureResult 签名服务器返回的反序列化的签名结果
 * @return 带有签名的交易编码
 */
public byte[] createSignedTransaction(
        TransactionEncoderInterface transactionEncoder,
        RawTransaction transaction,
        SignatureResult signatureResult) {
    return transactionEncoder.encode(transaction, signatureResult);
}
```

## 6. 发送带有签名的交易

Java SDK通过第5节获取带有签名的交易后，可将其发送到链上：

```java
/**
 * 发送带有签名的交易
 *
 * @param txPusher 交易发送器
 * @param signedTransaction 带有签名的交易
 * @return 交易回执
 */
TransactionReceipt sendTransaction(
        TransactionPusherInterface txPusher, byte[] signedTransaction) {
    return txPusher.push(Hex.toHexString(signedTransaction));
}
```

## 7. 交易构造整体流程


```eval_rst
.. note::
    - 真实业务场景中，程序逻辑中同步等待签名服务器返回签名结果可能影响系统性能，需要采用异步加缓存的方法提升系统性能。
    - 该示例基于 ``toml`` 文件初始化 ``BcosSDK`` , 基于 ``xml`` 或 ``ConfigOption`` 初始化 ``BcosSDK`` 的示例可参考 `使用xml配置BcosSDK <./quick_start.html#xml>`_  以及 `使用ConfigOption初始化BcosSDK <./quick_start.html#configoptionbcossdk>`_ 
```
经过以上6个步骤即可完成交易构造，下面以根据合约abi、合约方法、合约地址发送交易并使用签名服务器对交易进行签名为例，介绍了交易构造的主要流程`makeAndSendSignedTransaction`:

```java
public class TransactionMaker
{
    // 获取配置文件路径
    public final String configFile = TransactionMaker.class.getClassLoader().getResource("config-example.toml").getPath();

    // 根据合约abi、合约方法、合约地址发送交易，其中交易签名通过签名服务产生
    public TransactionReceipt makeAndSendSignedTransaction(String abiContent, String methodName, String to, List<Object> params)
    {
         // 初始化BcosSDK
        BcosSDK sdk =  BcosSDK.build(configFile);
        // 为群组1初始化client
        Client client = sdk.getClient(Integer.valueOf(1));
         // 创建transactionEncoder
        TransactionEncoderInterface transactionEncoder = new TransactionEncoderService(client.getCryptoSuite());

        // 创建RawTransaction
        RawTransaction rawTransaction = makeTransaction(client, abiContent, methodName, to, params);

        // 请求签名服务签名
        SignatureResult signtature = requestForTransactionSignature(transactionEncoder, rawTransaction, client);

        // 产生带有签名的交易
        byte[] signedTransaction = createSignedTransaction(transactionEncoder, rawTransaction, signature);

        // 发送签名交易
        TransactionPusherInterface txPusher = new TransactionPusherService(client);
        return sendTransaction(txPusher, signedTransaction);
    }

    // 构造交易
    public RawTransaction makeTransaction(Client client, String abiContent, String methodName, String to, List<Object> params)
    {
        //1.创建ABICodeC对象
        ABICodec abiCodec = createABICodec(client);
        
        //2.编码交易内容
        String txData =  encodeMethod(abiCodec, abiContent, methodName, params);

        //3. 创建TransactionBuilder，构造RawTransaction
        TransactionBuilderInterface transactionBuilder = new TransactionBuilderService(client);
        return createRawTransaction(transactionBuilder, client, to, txData);       
    }

    // 请求签名服务
    public SignatureResult requestForTransactionSignature(TransactionEncoderInterface transactionEncoder, RawTransaction rawTransaction, Client client)
    {
        // 获取RawTransaction的哈希
        byte[] rawTxHash = encodeRawTransactionAndGetHash(transactionEncoder, client, rawTransaction);
        // 请求签名服务，获取交易签名
        String signature = requestForSign(rawTxHash， encryptType);
        // 对签名结果进行反序列化
        return decodeSign(signature);
    }
}
```