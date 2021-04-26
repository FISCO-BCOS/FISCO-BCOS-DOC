# 密码学模块

标签：``java-sdk`` ``Crypto``

----

Java SDK提供了可访问所有密码学相关接口的`CryptoSuite`，`CryptoSuite`会根据传入的`cryptoType`(目前支持`CryptoType.ECDSA_TYPE`和`CryptoType.SM_TYPE`，前者用在非国密链中，后者用于国密链中)初始化密码学相关的套件。

Java SDK目前支持以下功能:

- **计算哈希**: 支持`sm3`和`keccak256`两种哈希算法，一般国密采用前者，非国密采用后者；

- **签名/验签** : 支持`sm2`和`secp256k1`两种签名和验签方法，一半国密采用前者，非国密采用后者。

## 创建CryptoSuite

Java SDK目前支持创建非国密、国密类型的`CryptoSuite`。

**创建非国密类型的CryptoSuite的示例如下:**

```java
    public CryptoSuite createECDSACryptoSuite()
    {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }
```

**创建国密类型的CryptoSuite的示例如下:**

```java
  public CryptoSuite createSMCryptoSuite()
    {
        return new CryptoSuite(CryptoType.SM_TYPE);
    }
```

## 哈希接口

初始化密码学套件`CryptoSuite`后，用户可直接使用创建的`CryptoSuite`调用哈希接口，也可自定义创建指定的哈希类，并调用哈希算法。

### 使用CryptoSuite调用哈希接口

```java
    /// 调用keccak256哈希算法
    public String calculateHashWithkeccak256(String data)
    {
        // 创建非国密的CryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 使用cryptoSuite调用hash算法，返回十六进制哈希字符串
        return cryptoSuite.hash(data);
    }

    public byte[] calculateHashWithkeccak256(byte[] data)
    {
         // 创建非国密的CryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 使用cryptoSuite调用hash算法
        // 返回二进制数组，可通过Hex.toHexString(result)将其转换成十六进制字符串
        byte[] result = cryptoSuite.hash(data);
        return result;
    }

    /// 调用sm3哈希算法
    public String calculateHashWithSM3(String data)
    {
        // 创建国密的CryptoSuite
        CryptoSuite SMcryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // 使用SMcryptoSuite调用hash算法，返回十六进制哈希字符串
        return SMcryptoSuite.hash(data);
    }

    public byte[] calculateHashWithSM3(byte[] data)
    {
         // 创建非国密的CryptoSuite
        CryptoSuite SMcryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // 使用SMcryptoSuite调用hash算法
        // 返回二进制数组，可通过Hex.toHexString(result)将其转换成十六进制字符串
        byte[] result = SMcryptoSuite.hash(data);
        return result;
    }
```

### 创建指定方法的哈希对象调用哈希接口

```java
    /// 调用keccak256哈希算法
    public String calculateHashWithkeccak256(String data)
    {
        // 创建keccak256对应的对象
        Keccak256 hasher = new Keccak256();
        // 返回十六进制哈希字符串
        return hasher.hash(data);
    }

    public byte[] calculateHashWithkeccak256(byte[] data)
    {
        // 创建keccak256对应的对象
        Keccak256 hasher = new Keccak256();
        // 返回二进制数组，可通过Hex.toHexString将其转换为十六进制字符串
        return hasher.hash(data);
    }

    /// 调用sm3哈希算法
    public String calculateHashWithSM3(String data)
    {
        // 创建sm3对应的对象
        SM3Hash hasher = new SM3Hash();
        // 返回十六进制哈希字符串
        return hasher.hash(data);
    }

    public byte[] calculateHashWithSM3(byte[] data)
    {
        // 创建sm3对应的对象
        SM3Hash hasher = new SM3Hash();
        // 返回二进制数组，可通过Hex.toHexString将其转换为十六进制字符串
        return hasher.hash(data);
    }
```


## 签名/验签接口


初始化密码学套件`CryptoSuite`后，用户可直接使用创建的`CryptoSuite`调用签名和验签接口，也可自定义创建指定的签名验签对象，调用签名和验签接口。

```eval_rst
.. note::
    签名/验签接口传入的明文数据必须是哈希，生成指定明文的签名前，须计算其哈希，并将哈希结果作为签名原文传入接口生成签名
```

### 使用CryptoSuite调用哈希接口

**非国密签名/验签接口调用示例如下:**

```java
    /// 生成secp256k1签名
    public ECDSASignatureResult  generateSigantureWithSecp256k1(String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 生成CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        // 计算传入数据的哈希(keccak256哈希算法)
        String hashData = cryptoSuite.hash(data);
        // 生成签名
        return (ECDSASignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    // 当入参为byte[]
    public ECDSASignatureResult generateSigantureWithSecp256k1(byte[] data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 生成CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        // 计算传入数据的哈希(keccak256哈希算法)
        byte[] hashData = cryptoSuite.hash(data);
        // 生成签名
        return (ECDSASignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    /// 验证签名
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 计算data的哈希(keccak256k1哈希算法)
        String hashData = cryptoSuite.hash(data);
        // 验证签名
        return cryptoSuite.verify(keyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    // 入参为byte[]
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, byte[]ß data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        // 计算data的哈希(keccak256k1哈希算法)
        byte[] hashData = cryptoSuite.hash(data);
        // 验证签名
        return cryptoSuite.verify(keyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
```

**类似地，国密签名/验签接口调用示例如下:**

```java
    /// 生成sm2签名
    public SM2SignatureResult  generateSigantureWithSM2(String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // 生成CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        // 计算传入数据的哈希(sm3哈希算法)
        String hashData = cryptoSuite.hash(data);
        // 生成签名
        return (SM2SignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    // 当入参为byte[]
    public SM2SignatureResult generateSigantureWithSM2(byte[] data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // 生成CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        // 计算传入数据的哈希(sm3哈希算法)
        byte[] hashData = cryptoSuite.hash(data);
        // 生成签名
        return (SM2SignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    /// 验证签名
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // 计算data的哈希(sm3哈希算法)
        String hashData = cryptoSuite.hash(data);
        // 验证签名
        return cryptoSuite.verify(keyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    // 入参为byte[]
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, byte[]ß data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        // 计算data的哈希(sm3哈希算法)
        byte[] hashData = cryptoSuite.hash(data);
        // 验证签名
        return cryptoSuite.verify(keyPair.getHexPublicKey(),  Hex.toHexString(hashData), signatureResult.convertToString());
    }
```

### 创建指定方法的签名验签对象调用签名验签接口

**非国密签名/验签接口调用示例如下(签名密钥对的生成可参考[这里](./key_tool.html#id3)):**

```java
    // 生成secp256k1签名
    public ECDSASignatureResult generateSignatureWithSecp256k1(CryptoKeyPair ecdsaKeyPair, String data)
    {
        // 生成secp256k1签名对象
        ECDSASignature signer = new ECDSASignature();
        // 计算data的哈希(keccak256)
        Keccak256 hasher = new Keccak256();
        String hashData = hasher.hash(data);
        return (ECDSASignatureResult)signer.sign(hashData, ecdsaKeyPair);
    }

    // data类型为byte[]
    public ECDSASignatureResult generateSignatureWithSecp256k1(CryptoKeyPair ecdsaKeyPair, byte[] data)
    {
        // 生成secp256k1签名对象
        ECDSASignature signer = new ECDSASignature();
        // 计算data的哈希(keccak256)
        Keccak256 hasher = new Keccak256();
        byte[] hashData = hasher.hash(data);
        return (ECDSASignatureResult)signer.sign(hashData, ecdsaKeyPair);
    }

    /// 验证secp256k1签名
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair ecdsaKeyPair, String data)
    {
        // 生成secp256k1验签对象
        ECDSASignature verifier = new ECDSASignature();
        // 计算data的哈希(keccak256)
        Keccak256 hasher = new Keccak256();
        String hashData = hasher.hash(data);
        // 验证签名
        return verifier.verify(ecdsaKeyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    // 入参为byte[]
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair ecdsaKeyPair, byte[] data)
    {
        // 生成secp256k1验签对象
        ECDSASignature verifier = new ECDSASignature();
        // 计算data的哈希
        Keccak256 hasher = new Keccak256();
        byte[] hashData = hasher.hash(data);
        // 验证签名
        return verifier.verify(ecdsaKeyPair.getHexPublicKey(), Hex.toHexString(hashData), signatureResult.convertToString());
    }

```

**类似地，国密签名/验签接口调用示例如下:**

```java
    // 生成sm2签名
    public SM2SignatureResult generateSignatureWithSM2(CryptoKeyPair sm2KeyPair, String data)
    {
        // 生成sm2签名对象
        SM2Signature signer = new SM2Signature();
        // 计算data的哈希(sm3)
        SM3Hash hasher = new SM3Hash();
        String hashData = hasher.hash(data);
        return (SM2SignatureResult)signer.sign(hashData, sm2KeyPair);
    }

    // data类型为byte[]
    public SM2SignatureResult generateSignatureWithSecp256k1(CryptoKeyPair sm2KeyPair, byte[] data)
    {
        // 生成sm2签名对象
        SM2Signature signer = new SM2Signature();
        // 计算data的哈希(sm3)
        SM3Hash hasher = new SM3Hash();
        byte[] hashData = hasher.hash(data);
        return (SM2SignatureResult)signer.sign(hashData, sm2KeyPair);
    }

    /// 验证sm2签名
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair sm2KeyPair, String data)
    {
        // 生成sm2验签对象
        SM2Signature verifier = new SM2Signature();
        // 计算data的哈希
        SM3Hash hasher = new SM3Hash();
        String hashData = hasher.hash(data);
        // 验证签名
        return verifier.verify(sm2KeyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    // 入参为byte[]
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair sm2KeyPair, byte[] data)
    {
        // 生成sm2验签对象
        SM2Signature verifier = new SM2Signature();
        // 计算data的哈希
        SM3Hash hasher = new SM3Hash();
        byte[] hashData = hasher.hash(data);
        // 验证签名
        return verifier.verify(sm2KeyPair.getHexPublicKey(), Hex.toHexString(hashData), signatureResult.convertToString());
    }
```

### 签名结果类型转换

Java SDK提供了将签名结果`SignatureResult`转换为字符串，以及从字符串中构造签名对象`SignatureResult`的功能，示例如下:

```java
    // 将签名结果转换为字符串
    public String covertSignatureResultToString(SignatureResult signatureResult)
    {
        return signatureResult.convertToString();
    }

    // 从字符串中构造非国密签名对象
    public ECDSASignatureResult covertStringToECDSASignatureResult(String signatureString)
    {
        return new ECDSASignatureResult(signatureString);
    }

    // 从签名字符串中构造国密签名对象
    public SM2SignatureResult covertStringToECDSASignatureResult(CryptoKeyPair smKeyPair, String signatureString)
    {
        return new SM2SignatureResult(smKeyPair.getHexPublicKey(), signatureString);
    }
```
