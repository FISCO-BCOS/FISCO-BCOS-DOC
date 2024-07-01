# External account generation for FISCO BCOS

Author ： Bai Xingqiang ｜ FISCO BCOS Core Developer

## What is an account?

FISCO BCOS uses accounts to identify and differentiate each individual user。In a blockchain system that uses a public-private key system, each account corresponds to a pair of public and private keys.。where the address string obtained by the public key calculated by a secure one-way algorithm such as hashing is used as the account name of the account, i.e., the account address。The private key known only to the user corresponds to the password in the traditional authentication model.。Such accounts with private keys are also often referred to as external accounts or accounts.。

The smart contracts deployed to the chain in FISCO BCOS also correspond to an account in the underlying storage, which we call contract accounts.。The difference with an external account is that the address of the contract account is determined at the time of deployment, calculated from the deployer's account address and the information in its account, and the contract account does not have a private key.。

This article will focus on the generation of external accounts and will not discuss contract accounts. For more information about how to use the generated external accounts, please refer to the documentation of each SDK of FISCO BCOS.。

## Account usage scenarios

In FISCO BCOS, the account has the following usage scenarios:

- The SDK must hold an external account private key. Use the external account private key to sign transactions.。In a blockchain system, each call to the contract write interface is a transaction, and each transaction needs to be signed with the account's private key.。
- Permission control requires the address of an external account。FISCO BCOS permission control model, based on the external account address of the sender of the transaction, to determine whether there is permission to write data.。
- Contract account address uniquely identifies the contract on the blockchain。After each contract is deployed, the underlying node generates a contract address for it, which needs to be provided when calling the contract interface.。

## **Generation of external accounts**

For convenience, references to external accounts are referred to below as simply accounts.。FISCO BCOS provides the get _ account.sh script and the Web3SDK interface to create an account, and the console and Web3SDK also support loading the created account private key for transaction signing.。Users can store the account private key as a file in PEM or PKCS12 format。The PEM format uses plaintext to store the private key, while the PKCS12 format uses the password provided by the user to encrypt the private key.(https://zh.wikipedia.org/wiki/PKCS_12)。

### Use the get _ account.sh script to generate an account (action)

- **Get Script**

```
curl -LO https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/tools/get_account.sh && chmod u+x get_account.sh && bash get_account.sh -h
```

Execute the above instructions and see the following output, the correct script is downloaded, otherwise please try again。

```
Usage: ./get_account.sh
    default       generate account and store private key in PEM format file
    -p            generate account and store private key in PKCS12 format file
    -k [FILE]     calculate address of PEM format [FILE]
    -P [FILE]     calculate address of PKCS12 format [FILE]
    -h Help
```

- **Generate account private key stored in PEM format**

```
bash get_account.sh
```

Execute the above command to get output similar to the following, including the account address and the private key PEM file with the account address as the file name。

```
[INFO] Account Address   : 0xee5fffba2da55a763198e361c7dd627795906ead
[INFO] Private Key (pem) : accounts/0xee5fffba2da55a763198e361c7dd627795906ead.pem
```

- **Generate account private key stored in PKCS12 format**

```
bash get_account.sh -p
```

Execute the above command, you can get output similar to the following, follow the prompts to enter the password, generate the corresponding p12 file。

```
Enter Export Password:
Verifying - Enter Export Password:
[INFO] Account Address : 0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1
[INFO] Private Key (p12) : accounts/0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1.p12
```

### Using Java-SDK interface generation account

Sometimes we need to generate a new account in the code, this time we need to use Java-SDK(The project is called web3SDK)Provided Interface。

As shown below, Java-The SDK provides functions such as generating an account, calculating an account address, and obtaining a public key. Compared with the get _ account.sh script, the SDK supports the generation of state secret accounts.。

```
import org.fisco.bcos.web3j.crypto.EncryptType
import org.fisco.bcos.web3j.crypto.Credentials
import org.fisco.bcos.web3j.crypto.gm.GenCredential

/ / Create a regular account
EncryptType.encryptType = 0;
/ / To create a national secret account and send transactions to the national secret blockchain node, you need to use the national secret account.
// EncryptType.encryptType = 1; 
Credentials credentials = GenCredential.create();
/ / Account address
String address = credentials.getAddress();
/ / Account private key
String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
/ / Account public key
String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
```

The above interfaces can be used directly in Java business code, while Java-The SDK also provides the function of loading private keys stored in PEM format or PKCS12 format. For more information, see here.(https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/sdk.html#id5)。

## Account address calculation method

The FISCO BCOS account address is calculated from the ECDSA public key.-256sum hash, taking the hexadecimal representation of the last 20 bytes of the calculation result as the account address, each byte requires two hexadecimal representations, so the account address length is 40。FISCO BCOS account address compatible with Ethereum。

The following is a brief demonstration of the account address calculation steps:

- Use OpenSSL to generate the elliptic curve private key, and the parameters of the elliptic curve use secp256k1。Run the following command to generate the private key in PEM format and save it in the ecprivkey.pem file。

```
openssl ecparam -name secp256k1 -genkey -noout -out ecprivkey.pem
```

- Calculate the public key based on the private key, and then use the public key to calculate the corresponding account address。Need to get keccak-256sum tool, can be downloaded from [here](https://github.com/vkobel/ethereum-generate-wallet/tree/master/lib)。

```
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}' | ./keccak-256sum -x -l | tr -d ' -' | tail -c 41
```

Get output similar to the following, that is, calculate the account address corresponding to ecprivkey.pem。

```
dcc703c0e500b653ca82273b7bfad8045d85a470
```

## **SUMMARY**

This article briefly introduces the definition, generation and calculation method of FISCO BCOS external account.。In the future, we will also open more useful supporting components to help developers manage their accounts more easily and securely.。

