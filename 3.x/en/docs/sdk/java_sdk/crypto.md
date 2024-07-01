# Signature and Verification

Tag: "java-sdk`` ``Crypto``

----

The Java SDK provides a 'CryptoSuite' that can access all cryptography-related interfaces, depending on the incoming 'cryptoType'(Currently supports' CryptoType.ECDSA _ TYPE 'and' CryptoType.SM _ TYPE ', the former is used in non-state secret chain, the latter is used in state secret chain)Initializing Cryptography-Related Kits。

The Java SDK currently supports the following features:

- **Calculate Hash**: Support 'sm3' and 'keccak256' two hash algorithms, the general state secret using the former, non-state secret using the latter；

- **Signature / Verification** : Support 'sm2' and 'secp256k1' two signature and verification methods, half of the state secret using the former, non-state secret using the latter。

## Creating a CryptoSuite

Java SDK currently supports the creation of non-national secret, national secret type 'CryptoSuite'。

**An example of creating a CryptoSuite of non-state secret type is as follows:**

```java
    public CryptoSuite createECDSACryptoSuite()
    {
        return new CryptoSuite(CryptoType.ECDSA_TYPE);
    }
```

**An example of creating a CryptoSuite of the country secret type is as follows:**

```java
  public CryptoSuite createSMCryptoSuite()
    {
        return new CryptoSuite(CryptoType.SM_TYPE);
    }
```

## Hash Interface

After initializing the cryptography suite 'CryptoSuite', users can directly use the created 'CryptoSuite' to call the hash interface, or customize the creation of a specified hash class and call the hash algorithm。

### Calling the hash interface using CryptoSuite

```java
    / / / Call the keccak256 hash algorithm
    public String calculateHashWithkeccak256(String data)
    {
        / / Create a non-secret CryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        / / Use cryptoSuite to call the hash algorithm and return the hexadecimal hash string
        return cryptoSuite.hash(data);
    }

    public byte[] calculateHashWithkeccak256(byte[] data)
    {
         / / Create a non-secret CryptoSuite
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        / / Invoke the hash algorithm using cryptoSuite
        / / Returns a binary array. You can use the Hex.toHexString(result)Convert it to a hexadecimal string
        byte[] result = cryptoSuite.hash(data);
        return result;
    }

    / / / Call the sm3 hash algorithm
    public String calculateHashWithSM3(String data)
    {
        / / Create State Secret CryptoSuite
        CryptoSuite SMcryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        / / Use SMcryptoSuite to call the hash algorithm and return the hexadecimal hash string
        return SMcryptoSuite.hash(data);
    }

    public byte[] calculateHashWithSM3(byte[] data)
    {
         / / Create a non-secret CryptoSuite
        CryptoSuite SMcryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        / / Invoke the hash algorithm using SMcryptoSuite
        / / Returns a binary array. You can use the Hex.toHexString(result)Convert it to a hexadecimal string
        byte[] result = SMcryptoSuite.hash(data);
        return result;
    }
```

### Create a hash object for the specified method and call the hash interface

```java
    / / / Call the keccak256 hash algorithm
    public String calculateHashWithkeccak256(String data)
    {
        / / Create an object corresponding to keccak256
        Keccak256 hasher = new Keccak256();
        / / returns the hex hash string
        return hasher.hash(data);
    }

    public byte[] calculateHashWithkeccak256(byte[] data)
    {
        / / Create an object corresponding to keccak256
        Keccak256 hasher = new Keccak256();
        / / Returns a binary array, which can be converted to a hexadecimal string by Hex.toHexString
        return hasher.hash(data);
    }

    / / / Call the sm3 hash algorithm
    public String calculateHashWithSM3(String data)
    {
        / / Create an object corresponding to sm3
        SM3Hash hasher = new SM3Hash();
        / / returns the hex hash string
        return hasher.hash(data);
    }

    public byte[] calculateHashWithSM3(byte[] data)
    {
        / / Create an object corresponding to sm3
        SM3Hash hasher = new SM3Hash();
        / / Returns a binary array, which can be converted to a hexadecimal string by Hex.toHexString
        return hasher.hash(data);
    }
```

## Signature / Validation Interface

After initializing the cryptography suite 'CryptoSuite', you can directly use the created 'CryptoSuite' to call the signature and signature verification interfaces. You can also create a specified signature verification object and call the signature and signature verification interfaces.。

```eval_rst
.. note::
    The plaintext data passed in by the signature / signature verification interface must be a hash. Before generating the signature of the specified plaintext, the hash must be calculated and the hash result must be passed into the interface as the original signature to generate the signature.
```

### Invoking the signing / checking interface using CryptoSuite

**The following is an example of calling the non-state secret signature / verification interface:**

```java
    / / / Generate secp256k1 signature
    public ECDSASignatureResult  generateSigantureWithSecp256k1(String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        / / Generate CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        / / Calculate the hash of the incoming data(keccak256 hash algorithm)
        String hashData = cryptoSuite.hash(data);
        / / Generate signature
        return (ECDSASignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    / / When the input parameter is byte []
    public ECDSASignatureResult generateSigantureWithSecp256k1(byte[] data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        / / Generate CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        / / Calculate the hash of the incoming data(keccak256 hash algorithm)
        byte[] hashData = cryptoSuite.hash(data);
        / / Generate signature
        return (ECDSASignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    / / / Verify signature
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        / / Calculate the hash of data(keccak256k1 hash algorithm)
        String hashData = cryptoSuite.hash(data);
        / / Verify signature
        return cryptoSuite.verify(keyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    / / Incoming parameter is byte []
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, byte[] data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
        / / Calculate the hash of data(keccak256k1 hash algorithm)
        byte[] hashData = cryptoSuite.hash(data);
        / / Verify signature
        return cryptoSuite.verify(keyPair.getHexPublicKey(), hashData, signatureResult.getSignatureBytes());
    }
```

**Similarly, an example of a call to the signature / verification interface is as follows:**

```java
    / / / Generate sm2 signature
    public SM2SignatureResult  generateSigantureWithSM2(String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        / / Generate CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        / / Calculate the hash of the incoming data(sm3 hash algorithm)
        String hashData = cryptoSuite.hash(data);
        / / Generate signature
        return (SM2SignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    / / When the input parameter is byte []
    public SM2SignatureResult generateSigantureWithSM2(byte[] data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        / / Generate CryptoKeyPair
        CryptoKeyPair cryptoKeyPair = cryptoSuite.createKeyPair();
        / / Calculate the hash of the incoming data(sm3 hash algorithm)
        byte[] hashData = cryptoSuite.hash(data);
        / / Generate signature
        return (SM2SignatureResult)(cryptoSuite.sign(hashData, cryptoKeyPair));
    }

    / / / Verify signature
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, String data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        / / Calculate the hash of data(sm3 hash algorithm)
        String hashData = cryptoSuite.hash(data);
        / / Verify signature
        return cryptoSuite.verify(keyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    / / Incoming parameter is byte []
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair keyPair, byte[] data)
    {
        CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
        / / Calculate the hash of data(sm3 hash algorithm)
        byte[] hashData = cryptoSuite.hash(data);
        / / Verify signature
        return cryptoSuite.verify(keyPair.getHexPublicKey(),  Hex.toHexString(hashData), signatureResult.convertToString());
    }
```

### Create a signature verification object of a specified method and call the signature verification interface

**The following is an example of calling the non-state secret signature / verification interface(The generation of the signature key pair can refer to [here](./keytool.html#id3)):**

```java
    / / Generate secp256k1 signature
    public ECDSASignatureResult generateSignatureWithSecp256k1(CryptoKeyPair ecdsaKeyPair, String data)
    {
        / / Generate the secp256k1 signature object
        ECDSASignature signer = new ECDSASignature();
        / / Calculate the hash of data(keccak256)
        Keccak256 hasher = new Keccak256();
        String hashData = hasher.hash(data);
        return (ECDSASignatureResult)signer.sign(hashData, ecdsaKeyPair);
    }

    / / data type is byte []
    public ECDSASignatureResult generateSignatureWithSecp256k1(CryptoKeyPair ecdsaKeyPair, byte[] data)
    {
        / / Generate the secp256k1 signature object
        ECDSASignature signer = new ECDSASignature();
        / / Calculate the hash of data(keccak256)
        Keccak256 hasher = new Keccak256();
        byte[] hashData = hasher.hash(data);
        return (ECDSASignatureResult)signer.sign(hashData, ecdsaKeyPair);
    }

    / / / Verify secp256k1 signature
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair ecdsaKeyPair, String data)
    {
        / / Generate the secp256k1 check object
        ECDSASignature verifier = new ECDSASignature();
        / / Calculate the hash of data(keccak256)
        Keccak256 hasher = new Keccak256();
        String hashData = hasher.hash(data);
        / / Verify signature
        return verifier.verify(ecdsaKeyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    / / Incoming parameter is byte []
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair ecdsaKeyPair, byte[] data)
    {
        / / Generate the secp256k1 check object
        ECDSASignature verifier = new ECDSASignature();
        / / Calculate the hash of data
        Keccak256 hasher = new Keccak256();
        byte[] hashData = hasher.hash(data);
        / / Verify signature
        return verifier.verify(ecdsaKeyPair.getHexPublicKey(), Hex.toHexString(hashData), signatureResult.convertToString());
    }

```

**Similarly, an example of a call to the signature / verification interface is as follows:**

```java
    / / generate sm2 signature
    public SM2SignatureResult generateSignatureWithSM2(CryptoKeyPair sm2KeyPair, String data)
    {
        / / Generate the sm2 signature object
        SM2Signature signer = new SM2Signature();
        / / Calculate the hash of data(sm3)
        SM3Hash hasher = new SM3Hash();
        String hashData = hasher.hash(data);
        return (SM2SignatureResult)signer.sign(hashData, sm2KeyPair);
    }

    / / data type is byte []
    public SM2SignatureResult generateSignatureWithSecp256k1(CryptoKeyPair sm2KeyPair, byte[] data)
    {
        / / Generate the sm2 signature object
        SM2Signature signer = new SM2Signature();
        / / Calculate the hash of data(sm3)
        SM3Hash hasher = new SM3Hash();
        byte[] hashData = hasher.hash(data);
        return (SM2SignatureResult)signer.sign(hashData, sm2KeyPair);
    }

    / / / verify sm2 signature
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair sm2KeyPair, String data)
    {
        / / Generate sm2 check object
        SM2Signature verifier = new SM2Signature();
        / / Calculate the hash of data
        SM3Hash hasher = new SM3Hash();
        String hashData = hasher.hash(data);
        / / Verify signature
        return verifier.verify(sm2KeyPair.getHexPublicKey(), hashData, signatureResult.convertToString());
    }
    / / Incoming parameter is byte []
    public boolean verifySignature(SignatureResult signatureResult, CryptoKeyPair sm2KeyPair, byte[] data)
    {
        / / Generate sm2 check object
        SM2Signature verifier = new SM2Signature();
        / / Calculate the hash of data
        SM3Hash hasher = new SM3Hash();
        byte[] hashData = hasher.hash(data);
        / / Verify signature
        return verifier.verify(sm2KeyPair.getHexPublicKey(), Hex.toHexString(hashData), signatureResult.convertToString());
    }
```

### Signature Result Type Conversion

The Java SDK provides the function of converting the signature result 'SignatureResult' into a string and constructing the signature object 'SignatureResult' from the string. The example is as follows:

```java
    / / Convert the signature result to a string
    public String covertSignatureResultToString(SignatureResult signatureResult)
    {
        return signatureResult.convertToString();
    }

    / / Construct a non-secret signature object from a string
    public ECDSASignatureResult covertStringToECDSASignatureResult(String signatureString)
    {
        return new ECDSASignatureResult(signatureString);
    }

    / / Construct the state secret signature object from the signature string
    public SM2SignatureResult covertStringToECDSASignatureResult(CryptoKeyPair smKeyPair, String signatureString)
    {
        return new SM2SignatureResult(smKeyPair.getHexPublicKey(), signatureString);
    }
```
