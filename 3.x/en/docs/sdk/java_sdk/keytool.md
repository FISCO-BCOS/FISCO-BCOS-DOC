# Account Key Management Tool

Tag: "java-sdk "" 'Set up account "

----

The Java SDK provides an account management interface and supports the following functions:

- **Account loading**: Loads an account from a specified path, supports loading account files in both 'pem' and 'p12' formats, and also supports loading hexadecimal private key strings.

- **Account Generation**: Randomly generate account public-private key pairs

- **Save Account**: Save account information

- **Provides an interface for loading 'p12' and parsing 'pem' files**

```eval_rst
.. note::
    The Java SDK provides interface-level account generation methods. For more information about tool-level account generation scripts, see 'get _ account.sh script <.. /.. / develop / account.html >'.
```

## 1. Account loading

Java SDK 'org.fisco.bcos.sdk.v3.crypto.CryptoSuite' provides the account loading function. By default, the transaction sending account is loaded from the '[account]' configuration item in the configuration file. For details, see [here](./config.html#id6).

### 1.1 Load account from hex private key string

An example of loading an account from a hexadecimal private key string is as follows:

```java
/* Example 1: Create a cryptosuite and load a private key string through the cryptosuite****/
// cryptoType: Used to specify the type of private key to be loaded
// hexPrivateKey: Hexadecimal private key string
public CryptoKeyPair loadAccountFromHexPrivateKey(int cryptoType, String hexPrivateKey)
{
    / / Create a cryptoSuite based on the cryptoType. Currently, cryptoType supports:
    // 1. CryptoType.ECDSA_TYPE: CryptoSuite for creating non-state secret types
    // 2. CryptoType.SM_TYPE:    CryptoSuite for creating a country secret type
    CryptoSuite cryptoSuite = new CryptoSuite(cryptoType);
    / / Load the private key object from the hexadecimal private key string hexPrivateKey
    return cryptoSuite.getKeyPairFactory().createKeyPair(hexPrivateKey);
}

/* Example 2: Directly load a non-state secret private key****/
public CryptoKeyPair loadECDSAAccountFromHexPrivateKey(String hexPrivateKey)
{
    / / Create a KeyFactory of the national secret type
    ECDSAKeyPair keyFacotry = new ECDSAKeyPair();
    / / load hexPrivateKey from hex string
    return keyFacotry.createKeyPair(hexPrivateKey);
}

/* Example 3: Directly Load the State Secret Private Key****/
public CryptoKeyPair loadGMAccountFromHexPrivateKey(String hexPrivateKey)
{
    / / Create a KeyFactory of the national secret type
    SM2KeyPair keyFacotry = new SM2KeyPair();
    / / load hexPrivateKey from hex string
    return keyFacotry.createKeyPair(hexPrivateKey);
}

```

### 1.2 Load account from large integer private key

**An example of loading an account from a large integer private key is as follows:**

```java
/* Example 1: Create a cryptosuite and load a private key string through the cryptosuite****/
// cryptoType: Used to specify the type of private key to be loaded
// privateKey: Private key
public CryptoKeyPair loadAccountFromHexPrivateKey(int cryptoType, BigInteger privateKey)
{
    / / Create a cryptoSuite based on the cryptoType. Currently, cryptoType supports:
    // 1. CryptoType.ECDSA_TYPE: CryptoSuite for creating non-state secret types
    // 2. CryptoType.SM_TYPE:    CryptoSuite for creating a country secret type
    CryptoSuite cryptoSuite = new CryptoSuite(cryptoType);
    / / Load the private key object from the hexadecimal private key string hexPrivateKey
    return cryptoSuite.getKeyPairFactory().createKeyPair(privateKey);
}

/* Example 2: Directly load a non-state secret private key****/
public CryptoKeyPair loadECDSAAccountFromHexPrivateKey(BigInteger privateKey)
{
    / / Create a KeyFactory of the national secret type
    ECDSAKeyPair keyFacotry = new ECDSAKeyPair();
    / / load hexPrivateKey from hex string
    return keyFacotry.createKeyPair(privateKey);
}

/* Example 3: Directly Load the State Secret Private Key****/
public CryptoKeyPair loadGMAccountFromHexPrivateKey(BigInteger privateKey)
{
    / / Create a KeyFactory of the national secret type
    SM2KeyPair keyFacotry = new SM2KeyPair();
    / / load hexPrivateKey from hex string
    return keyFacotry.createKeyPair(privateKey);
}
```

### 1.3 Load account from pem file

An example of loading a transaction sending account from a specified 'pem' account file is as follows(Please refer to [Quick Start] for client initialization method.(./quick_start.html#id4))：

```java
/ / Load the pem account file from the path specified by pemAccountFilePath and set it as the transaction sending account
public void loadPemAccount(Client client, String pemAccountFilePath)
{
    / / Get the CryptoSuite object from the client
    CryptoSuite cryptoSuite = client.getCryptoSuite();
    / / load pem account file
    cryptoSuite.loadAccount("pem", pemAccountFilePath, null);
}
```

### 1.4 Load account from p12 file

An example of loading a transaction sending account from a specified 'p12' account file is as follows:

```java
/ / Load the p12 account file from the path specified by p12AccountFilePath and set it as the transaction sending account
public void loadP12Account(Client client, String p12AccountFilePath, String password)
{
    / / Get the CryptoSuite object from the client
    CryptoSuite cryptoSuite = client.getCryptoSuite();
    / / load pem account file
    cryptoSuite.loadAccount("p12", p12AccountFilePath, password);
}
```

## 2. Account Generation

Java SDK 'org.fisco.bcos.sdk.v3.crypto.CryptoSuite' provides account generation functionality。

Examples of randomly generated non-State secret accounts are as follows.

```java
/ / Create a non-state secret type of CryptoSuite
CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
/ / Randomly generate non-state secret public-private key pairs
CryptoKeyPair cryptoKeyPair = cryptoSuite.generateRandomKeyPair();
/ / Get account address
String accountAddress = cryptoKeyPair.getAddress();
```

An example of a randomly generated State Secret account is as follows.

```java
/ / Create a country secret type of CryptoSuite
CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
/ / Randomly generate public-private key pairs
CryptoKeyPair cryptoKeyPair = cryptoSuite.generateRandomKeyPair();
/ / Get account address
String accountAddress = cryptoKeyPair.getAddress();
```

## 3. Account Preservation

When the account is not custom loaded and the account information is not configured through the profile(Please refer to [here] for account configuration.(./config.html#id6))Java SDK randomly generates an account to send transactions. Java SDK 'org.fisco.bcos.sdk.v3.crypto.CryptoSuite' provides the account save function, which can save the randomly generated account in the specified path。

An example of saving an account file to a specified path in the format 'pem' is as follows:

```java
/ / Save the randomly generated account information in the path specified by pemFilePath
public void saveAccountWithPem(CryptoKeyPair cryptoKeyPair, String pemFilePath)
{
    / / Save the account file in pem format to the pemFilePath path
    cryptoKeyPair.storeKeyPairWithPem(pemFilePath);
}

/ / save the randomly generated account information in the account configuration ${keyStoreDir}under the specified directory
public void saveAccountWithPemToKeyStoreDir(CryptoKeyPair cryptoKeyPair)
{
    / / The account file name is ${accountAddress}.pem
    cryptoKeyPair.storeKeyPairWithPemFormat();
}
```

An example of saving an account file to a specified path in the format 'p12' is as follows:

```java
public void saveAccountWithP12(CryptoKeyPair cryptoKeyPair, String p12FilePath, String password)
{
    / / Save the account file to the p12FilePath path in p12 format
    cryptoKeyPair.storeKeyPairWithP12(p12FilePath, password);
}

/ / save the randomly generated account information in the account configuration ${keyStoreDir}under the specified directory
public void saveAccountWithP12ToKeyStoreDir(CryptoKeyPair cryptoKeyPair, String password)
{
    / / The account file name is ${accountAddress}.p12
    cryptoKeyPair.storeKeyPairWithP12Format(password);
}
```

The account information randomly generated by the Java SDK can be obtained as follows:

```java
public CryptoKeyPair getCreatedCryptoKeyPair(Client client)
{
    return client.getCryptoSuite().getCryptoKeyPair();
}
```

## 4. 'p12' and 'pem' file parsing interfaces

Java SDK 'org.fisco.bcos.sdk.v3.crypto.keystore.KeyTool' provides' p12 'and' pem 'file parsing interfaces。

### 4.1 'pem' account file parsing interface

An example of loading a 'pem' file from a specified file is as follows:

```java
public KeyTool loadPem(String pemFilePath)
{
    return new PEMKeyStore(pemFilePath);
}
```

### 4.2 'p12' account file parsing interface

An example of loading a 'pem' file from a specified file is as follows:

```java
public KeyTool loadP12(String p12FilePath, String password)
{
    return new P12KeyStore(p12FilePath, password);
}
```

### 4.3 Get the 'KeyPair' object through 'KeyTool'

The 'KeyTool' object generated after parsing 'pem' and 'p12' provides the following interfaces for accessing public and private key information:

```java
/ / Obtain public-private key pair information
public KeyPair getKeyPair();
```

In addition, 'org.fisco.bcos.sdk.v3.crypto.CryptoSuite' also provides the function of converting public-private key information of the 'java.security.KeyPair' type into 'CryptoKeyPair', as shown in the following example:

```java
/ / The public and private key information maintained in KeyTool
public CryptoKeyPair loadKeyStore(KeyTool keyTool)
{
    KeyPair keyPair = keyTool.getKeyPair();
    CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.ECDSA_TYPE);
    return cryptoSuite.createKeyPair(keyPair);
}

/ / The public and private key information of the national secret is maintained in KeyTool
public CryptoKeyPair loadGMKeyStore(KeyTool gmKeyTool)
{
    KeyPair keyPair = gmKeyTool.getKeyPair();
    CryptoSuite cryptoSuite = new CryptoSuite(CryptoType.SM_TYPE);
    return cryptoSuite.createKeyPair(keyPair);
}
```
