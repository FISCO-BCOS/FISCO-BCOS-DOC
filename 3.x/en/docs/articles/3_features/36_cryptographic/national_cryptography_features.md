# The State Secret Characteristics of FISCO BCOS

Author ： LI Hao Xuan ｜ FISCO BCOS Core Developer


> Security is the guarantee of trust, algorithm is the basis of security, the establishment of node communication channel, signature generation, data encryption, etc., all need to use the corresponding algorithm of cryptography。FISCO BCOS uses the national secret algorithm to achieve a secure and controllable blockchain architecture.。

This article explains the concepts related to the state secret algorithm and the application of the state secret algorithm in the blockchain.。

## Cryptography in Blockchain

In the blockchain, the general use of cryptography in the following scenarios:

### 1. Data hashing algorithm

Hash function is a kind of one-way function, the role is to convert any length of the message into a fixed length of the output value, with one-way, collision-free, deterministic, irreversible properties.。
In the blockchain, the hash function is used to compress the message into a fixed-length output, as well as to ensure the authenticity of the data, to ensure that the data has not been modified。

### 2. Data encryption and decryption algorithm

Encryption and decryption algorithms are mainly divided into two types: symmetric encryption and asymmetric encryption.
- Symmetric encryption has the characteristics of fast speed, high efficiency, high encryption strength, the use of the need to negotiate the key in advance, mainly for large-scale data encryption, such as FISCO BCOS node data when the encryption.。
- Asymmetric encryption has the characteristics of no need to negotiate the key, compared to symmetric encryption calculation efficiency is lower, there are defects such as man-in-the-middle attacks, mainly used in the process of key agreement.。

For different needs, the two can be used in combination with each other.。

### 3. Generation and verification of message signatures.

In the blockchain, messages need to be signed for message tamper resistance and authentication。For example, in the process of node consensus, the identity of other nodes needs to be verified, and nodes need to verify the transaction data on the chain.。

### 4. Handshake establishment process

We talked earlier [**Handshake Process of Node TLS**](https://mp.weixin.qq.com/s?__biz=MzU5NTg0MjA4MA==&mid=2247484845&idx=1&sn=c943c8a3f55a060991ffafec8bbdb27b&scene=21#wechat_redirect)(Click Direct), which requires the use of cryptographic components and digital certificates, both need to use the corresponding cryptographic algorithms。

## FISCO BCOS's State Secret Algorithm

The national secret algorithm is issued by the National Cryptographic Bureau, including SM1\ SM2\ SM3\ SM4\ and so on, for China's independent research and development of cryptographic algorithm standards.。
In order to fully support domestic cryptography algorithms, based on domestic cryptography standards, Jinchainmeng has implemented the national secret encryption and decryption, signature, signature verification, hash algorithm, national secret SSL communication protocol, and integrated it into the FISCO BCOS platform to achieve full support for commercial passwords recognized by the National Cryptographic Bureau.。
The state secret version of FISCO BCOS replaces the cryptographic algorithms of the underlying modules such as transaction signature verification, p2p network connection, node connection, data drop encryption, etc. with the state secret algorithm.。

1. The state-secret SSL algorithm is used in the node TLS handshake.；
2. Transaction signature generation, verification process using the state secret SM2 algorithm；
3. The national secret SM4 algorithm is used in the data encryption process.；
4. The data summary algorithm uses the national secret SM3 algorithm.。

The ECDHE _ SM4 _ SM3 cipher suite of State Secret SSL 1.1 is used to establish SSL links for authentication between FISCO BCOS nodes. The differences are shown in the following table:

|              | **OpenSSL**                              | **State Secret SSL**                              |
| ------------ | ---------------------------------------- | ---------------------------------------- |
| Encryption Suite| Using ECDH, RSA, SHA-256, AES256 and other cryptographic algorithms| Adopting the State Secret Algorithm|
| PRF algorithm| SHA-256                                  | SM3                                      |
| Key exchange mode| Transmission elliptic curve parameters and the signature of the current message| The signature and encryption certificate of the current message.|
| Certificate Mode| OpenSSL certificate mode| The dual certificate model of the State Secret, which is an encryption certificate and a signature certificate, respectively.|

The data structure differences between the State Secret Edition and the Standard Edition FISCO BCOS are as follows:

| **Algorithm Type** | **Standard Edition**FISCO BCOS                           | **State Secret Edition**FISCO BCOS                               |
| ------------ | ---------------------------------------------- | -------------------------------------------------- |
| Signature| ECDSA (Public key length: 512 bits, private key length: 256 bits) | SM2 (Length of public key: 512 bits, length of private key: 256 bits)       |
| Hash| SHA3 (Hash string length: 256 bits)                    | SM3 (Hash string length: 256 bits)                         |
| symmetric encryption and decryption| AES (Encryption Key Length: 256 bits)                   | SM4 (Symmetric key length: 128 bits)                       |
| Transaction length| 520bits(The identifier is 8bits and the signature length is 512bits.)       | 1024bits(128 bytes, including public key 512bits, signature length 512bits) |

## Turn on the national secret feature

Need to use the state secret version of FISCO BCOS features, there are the following points need to pay attention to:

### 1. Build Jianguo Secret Edition FISCO BCOS Block Chain

There are two main ways to build the Jianguo Secret FISCO BCOS blockchain:

##### (1). Use the build _ chain.sh script to build

of the buildchain.sh script-g is the state secret compilation option. After successful use, the state secret version of the node will be generated.。By default, download the latest stable executable from GitHub.

```bash
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.9.1/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    - If the build _ chain.sh script cannot be downloaded for a long time due to network problems, try 'curl-#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master-2.0/tools/build_chain.sh && chmod u+x build_chain.sh`
```

Run the following command to build a four-node state secret FISCO BCOS alliance chain

```bash
./build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545 -g
```

After successful execution, a local four-node state-secret FISCO BCOS alliance chain will be built。

##### (2)Use enterprise deployment tools to build

Enterprise Deployment Tools for Generator-The g command is related to the national secret operation, the user needs to generate the relevant certificate, download the binary and other operations in the process of accompanying.-g option, operation mode:

**Reference Tutorial**：https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tutorial/enterprise_quick_start.html

```
./generator --download_fisco ./meta -g
./generator --generate_chain_certificate ./dir_chain_ca -g
./generator --generate_agency_certificate ./dir_agency_ca ./dir_chain_ca agencyB -g
...
```

The following operations are similar to those in the tutorial

```
./generator --build_install_package ./meta/peersB.txt ./nodeA -g
```

### 2. Use the state secret sdk / console


The operation commands of the state secret SDK and console are the same as those of the normal version. The Web3SDK and 1.x console need to do the following when enabling the state secret feature:

- (1). Set the encryptType in applicationContext.xml to 1；
- (2). When loading the private key, the state secret version of the private key needs to be loaded.；
- (3). The console also needs to obtain the state secret version jar package。

```eval_rst
.. note::
    Java SDK and 2.6.0+No additional configuration is required when the version of the console enables the national secret algorithm.
```

### 3. Turn on the encryption function of the state secret version

In the state-secret version, you need to encrypt the nodes conf / gmnode.key and conf / origin _ cert / node.key at the same time. Other operations are the same as those in the normal version.。

## SUMMARY

The main algorithmic features in FISCO BCOS are compared as follows.

|              | **Standard Edition****FISCO BCOS** | **State Secret Edition****FISCO BCOS** |
| ------------ | ------------------------ | ------------------------ |
| SSL Link| OpenSSL TLSv1.2 Protocol| State Secret TLSv1.1 Agreement|
| Signature Verification| ECDSA Signature Algorithm| SM2 Signature Algorithm|
| message digest algorithm| SHA-256 SHA-3            | SM3 Message Digest Algorithm|
| falling disk encryption algorithm| AES-256 encryption algorithm| SM4 Encryption Algorithm|
| Certificate Mode| OpenSSL certificate mode| State Secret Dual Certificate Mode|
| contract compiler| Ethereum Solidity Compiler| State Secret Solidity Compiler|

------

### User Questions

**Q**：**Wang Gang+Yunfei Micro-networking+Zhuhai**: Does the solidity compiler also need to use cryptographic algorithms?？

**A**：**Li Haoxuan**: The abi in solidity will use the hash, which requires both the underlying and contract compilers to use the same SM3 algorithm.。

**Q**：**Chen Xiaojun-Jiangnan Keyou-Guangzhou**: Can you tell me whether the National Secret TLS protocol suite is implemented by itself or is it open source?？

**A**：**Li Haoxuan**: State Secret TLS We use the TASSL open source library。

**Q**：**Tenglong(He Zhiqun)**If you use a state-secret node, because the signature algorithm is changed, will the RPC SDK be different?？

**A**：**Li Haoxuan**Yes, the SDK also needs to enable the national secret feature.。

**Q**：**KHJ**: How the private key of the current drop encryption is handled.？

**A**：**meng**: To save it yourself, please refer to [instructions] here.(https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/storage_security.html)