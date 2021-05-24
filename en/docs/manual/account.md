# Manage blockchain accounts

FISCO BCOS uses accounts to identify each individual user. In a blockchain system each account corresponds to a pair of public and private keys. The account named by the address string calculated by the secure one-way algorithm such as sha256 hash, that is **account address**. For distinguishing from the address of smart contract, the account address is often referred to as the **external account address**. The private key only known by the user corresponds to the password in the traditional authentication model. Users need to prove that they own the private key of the corresponding account through a secure cryptographic protocol for claiming their ownership of the account, and performing some sensitive account operations.

```eval_rst
.. important::

    In the previous tutorials, for simplifying the operation, we operate with the account provided by the tool by default. However, in actual application deployment, users need to create their own accounts and properly save the account private key to avoid serious security problems such as account private key leakage.
```

In this article, we will specifically introduce the creation, storage and use of accounts. Readers are required to have a basic knowledge of Linux.

FISCO BCOS provides the get_account script to create accounts, as well as Java SDK and console to store account private keys. Users can choose to store the account private key as a file in PEM or PKCS12 format according to their needs. The PEM format uses a plaintext storage private key, and the PKCS 12 encrypts and stores the private key using a user-provided password.

## Create your account

### Use script to create account

The usage of `get_gm_account.sh` is the same as `get_account.sh`.

#### 1. get script

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_account.sh && chmod u+x get_account.sh && bash get_account.sh -h
```

```eval_rst
.. note::
    - If the get_account.sh script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/get_account.sh && chmod u+x get_account.sh && bash get_account.sh -h`
    - Please use `curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/tassl-1.0.2/tassl.tar.gz`, and place in `~/.fisco/tassl`
```

If you use guomi version fisco, please execute below command to get `get_gm_account.sh`

```bash
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_gm_account.sh && chmod u+x get_gm_account.sh && bash get_gm_account.sh -h
```

```eval_rst
.. note::
    - If the get_gm_account.sh script cannot be downloaded for a long time due to network problems, try `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/get_gm_account.sh && chmod u+x get_gm_account.sh && bash get_gm_account.sh -h`
```

execute the above command and if you see the following output, you are downloading the correct script, otherwise please try again.

```bash
Usage: ./get_account.sh
    default       generate account and store private key in PEM format file
    -p            generate account and store private key in PKCS12 format file
    -k [FILE]     calculate the address of PEM format [FILE]
    -P [FILE]     calculate the address of PKCS12 format [FILE]
    -h Help
```

#### 2. Generate private key in PEM format

- generate private key and address
```bash
bash get_account.sh
```

Execute the above command to get output similar to the following. It includes the account address and the private key PEM file with the account address as the file name.

```bash
[INFO] Account Address   : 0xee5fffba2da55a763198e361c7dd627795906ead
[INFO] Private Key (pem) : accounts/0xee5fffba2da55a763198e361c7dd627795906ead.pem
```
- Specify the calculation account address of PEM format
```bash
bash get_account.sh -k accounts/0xee5fffba2da55a763198e361c7dd627795906ead.pem
```
Execute the above command. The result is as follows
```bash
[INFO] Account Address   : 0xee5fffba2da55a763198e361c7dd627795906ead
```
#### 3. Use script to generate PKCS12 format private key
- generate private key and address
```bash
bash get_account.sh -p
```

Execute the above command to get output similar to the following. You can follow the prompts to enter the password and generate the corresponding p12 file.

```bash
Enter Export Password:
Verifying - Enter Export Password:
[INFO] Account Address   : 0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1
[INFO] Private Key (p12) : accounts/0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1.p12
```

- Specify the calculation account address of p12 private key. **Enter the p12 file password as prompted**

```bash
bash get_account.sh -P accounts/0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1.p12
```
Execute the above command. The result is as follows
```bash
Enter Import Password:
MAC verified OK
[INFO] Account Address   : 0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1
```

### Calling Java SDK to create an account

```java
//create normal account
EncryptType.encryptType = 0;
//create national cryptography account, which uses for sending transaction to national blockchain node
// EncryptType.encryptType = 1;
Credentials credentials = GenCredential.create();
//account address
String address = credentials.getAddress();
//account private key
String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
//account public key
String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
```

For more details on the operation, to see [Creating and Using a Specified External Account](../sdk/sdk.html#id5).

## Store your account credential

- Java SDK supports loading via private key string or file, so the private key of the account can be stored in the database or in a local file.
- Local files support two storage formats, which are PKCS12 encrypted storage and PEM plaintext storage.
- When developing a service, you can select the storage management of private key according to the actual business scenario.

## Load your account credential

### Console loads private key file

The console provides the account generation script get_account.sh. The generated account file is in the accounts directory, and the account file loaded by console must be placed in this directory.

The console startup methods are as follows:

```
./start.sh
./start.sh groupID
./start.sh groupID -pem pemName
./start.sh groupID -p12 p12Name
```
##### Default startup
Console randomly generates an account, startup with the group number specified in console configuration file.

```bash
./start.sh
```
##### Specify group number to startup
Console randomly generates an account, startup with the group number specified on the command line.

```bash
./start.sh 2
```
- Note: The specified group needs to configure bean in console configuration file.

##### Use PEM private key file to startup

- Startup with the account of the specified pem file. Enter the parameters: group number, -pem, and pem file path

```bash
./start.sh 1 -pem accounts/0xebb824a1122e587b17701ed2e512d8638dfb9c88.pem
```
##### Use PKCS12 private key file to startup
- Startup with the account of the specified p12 file. Enter the parameters: group number, -p12, and p12 file path

```bash
./start.sh 1 -p12 accounts/0x5ef4df1b156bc9f077ee992a283c2dbb0bf045c0.p12
Enter Export Password:
```

### Java SDK loads private file

If the account private key file in PEM or PKCS12 format is generated by the account generation script get_accounts.sh, the account can be used by loading the PEM or PKCS12 account private key file. There are two classes of private keys to be loaded: P12Manager and PEMManager. P12Manager is used to load the private key file in PKCS12 format. PEMManager is used to load the private key file in PEM format.

* P12Manager usage example:
Load p12 key.
```java
// init BcosSDK
BcosSDK sdk =  BcosSDK.build(configFile);
// get Client
Client client = sdk.getClient(Integer.valueOf(1));
// get CryptoSuite
CryptoSuite cryptoSuite = client.getCryptoSuite();
// load p12 key
cryptoSuite.loadAccount("p12", p12AccountFilePath, password);
```

* PEMManager usage example:

Load key.
```java
// init BcosSDK
BcosSDK sdk =  BcosSDK.build(configFile);
// get Client
Client client = sdk.getClient(Integer.valueOf(1));
// get CryptoSuite
CryptoSuite cryptoSuite = client.getCryptoSuite();
// load pem key
cryptoSuite.loadAccount("pem", pemAccountFilePath, null);
```

## Account address calculation

The account address of FISCO BCOS is calculated by the ECDSA public key. The hexadecimal of ECDSA public key represents the calculation of keccak-256sum hash, and the hexadecimal of the last 20 bytes of the calculation result is taken as the account address. Each byte requires two hexadecimal to represent, so the length of account address is 40. FISCO BCOS's account address is compatible with Ethereum.

Note: keccak-256sum is different from `SHA3`. For details to refer to [here](https://ethereum.stackexchange.com/questions/550/which-cryptographic-hash-function-does-ethereum-use).

[Ethernet Address Generation](https://kobl.one/blog/create-full-ethereum-keypair-and-address/)

### 1. generate ECDSA private key

First, we use OpenSSL to generate an elliptic curve private key. The parameters of the elliptic curve are secp256k1. To run the following command to generate a private key in PEM format and save it in the ecprivkey.pem file.

```bash
openssl ecparam -name secp256k1 -genkey -noout -out ecprivkey.pem
```
Execute the following instructions to view the contents of the file.
```bash
cat ecprivkey.pem
```

You can see output similar to the following

```bash
-----BEGIN EC PRIVATE KEY-----
MHQCAQEEINHaCmLhw9S9+vD0IOSUd9IhHO9bBVJXTbbBeTyFNvesoAcGBSuBBAAK
oUQDQgAEjSUbQAZn4tzHnsbeahQ2J0AeMu0iNOxpdpyPo3j9Diq3qdljrv07wvjx
zOzLpUNRcJCC5hnU500MD+4+Zxc8zQ==
-----END EC PRIVATE KEY-----
```

Next, to calculate the public key based on the private key. To execute the following command.

```bash
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}'
```
You can get output similar to the following
```bash
8d251b400667e2dcc79ec6de6a143627401e32ed2234ec69769c8fa378fd0e2ab7a9d963aefd3bc2f8f1cceccba54351709082e619d4e74d0c0fee3e67173ccd
```

### 2. Calculate the address based on the public key

In this section, we calculate the corresponding account address based on the public key. The keccak-256sum tool we need to get is available for download from [here](https://github.com/vkobel/ethereum-generate-wallet/tree/master/lib).

```bash
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}' | ./keccak-256sum -x -l | tr -d ' -' | tail -c 41
```

Get the output similar to the following, which is the calculated account address.
```bash
dcc703c0e500b653ca82273b7bfad8045d85a470
```
