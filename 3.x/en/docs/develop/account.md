# 3. Account Use and Account Management

Tags: "Create Account" "State Secret Account" "Key File" "

----

FISCO BCOS uses accounts to identify and differentiate each individual user。In a blockchain system that uses a public-private key system, each account corresponds to a pair of public and private keys.。where the address string calculated by the public key using a secure one-way algorithm such as a hash is used as the account name for the account, i.e.**Account Address**In order to distinguish it from the address of a smart contract and for some other historical reasons, the account address is also often referred to.**External Account Address**。The private key known only to the user corresponds to the password in the traditional authentication model.。Users need to prove that they know the private key of the corresponding account through a secure cryptographic protocol to claim their ownership of the account and perform sensitive account operations。

```eval_rst
.. important::

    In other previous tutorials, to simplify the operation, the default account provided by the tool was used.。However, in the actual application deployment, users need to create their own accounts and properly save the account private key to avoid serious security issues such as account private key disclosure.。
```

This article will specifically describe how accounts are created, stored, and used, requiring readers to have a certain Linux operating base。

FISCO BCOS provides scripts and Java SDK to create accounts, as well as Java SDK and console to store account private keys。Users can choose to store the account private key as a file in PEM or PKCS12 format according to their needs.。where the PEM format stores the private key in clear text, while PKCS12 stores the private key encrypted with a user-supplied password。

## Creation of account

### Create an account using a script

The 'get _ gm _ account.sh' script for generating an account is consistent with the 'get _ account.sh' script for generating an account.。

#### 1. Get the script

```shell
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_account.sh && chmod u+x get_account.sh && bash get_account.sh -h
```

```eval_rst
.. note::
    - If you cannot download for a long time due to network problems, try 'curl-#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh && chmod u+x get_account.sh && bash get_account.sh -h`
```

State secret version please use the following instruction to get the script

```shell
curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/console/master/tools/get_gm_account.sh && chmod u+x get_gm_account.sh && bash get_gm_account.sh -h
```

```eval_rst
.. note::
    - If you cannot download for a long time due to network problems, try 'curl-#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_gm_account.sh && chmod u+x get_gm_account.sh && bash get_gm_account.sh -h`
```

Execute the above instructions and see the following output to download the correct script, otherwise please try again。

```shell
Usage: get_account.sh
    default       generate account and store private key in PEM format file
    -p            generate account and store private key in PKCS12 format file
    -k [FILE]     calculate address of PEM format [FILE]
    -P [FILE]     calculate address of PKCS12 format [FILE]
    -a            force script to consider the platform is aarch64
    -h Help
```

#### 2. Use the script to generate the PEM format private key.

- Generate private key and address

```shell
bash get_account.sh
```

Execute the above command to get output similar to the following, including the account address and the private key PEM file with the account address as the file name and the public key PUB file。

```shell
[INFO] Account privateHex: 0x3c73c7ee2d549c7a39fddd3e0449677f3c201f0f4c6c540c93ab38f8aab03388
[INFO] Account publicHex : 0x5309fa17ae97f81f80a1da3d6b116377ace351dffdcbfd0e91fbb3bcf0312d363c78b8aaf929b3661c1f02e8b2c318358843de6a2dcc66cc0d5260a0d6874a6e
[INFO] Account Address   : 0x3d1771fb60963fbaf5906c6b2a574c77dae7c136
[INFO] Private Key (pem) : accounts/0x3d1771fb60963fbaf5906c6b2a574c77dae7c136.pem
[INFO] Public  Key (pem) : accounts/0x3d1771fb60963fbaf5906c6b2a574c77dae7c136.pem.pub
```

- Specify the PEM private key file to calculate the account address

```shell
bash get_account.sh -k accounts/0xa04beef19c812628a2aa1f0fc73e0963f84ec75e.pem
```

Execute the above command, the result is as follows

```shell
[INFO] Account Address   : 0x3d1771fb60963fbaf5906c6b2a574c77dae7c136
[INFO] Account privateHex: 0x3c73c7ee2d549c7a39fddd3e0449677f3c201f0f4c6c540c93ab38f8aab03388
[INFO] Account publicHex : 0x5309fa17ae97f81f80a1da3d6b116377ace351dffdcbfd0e91fbb3bcf0312d363c78b8aaf929b3661c1f02e8b2c318358843de6a2dcc66cc0d5260a0d6874a6e
```

#### 3. Use the script to generate the PKCS12 format private key.

- Generate private key and address

```shell
bash get_account.sh -p
```

Execute the above command, you can get output similar to the following, follow the prompts to enter the password, generate the corresponding p12 file and pub file。

```shell
[INFO] Account privateHex: 0x7fe388c7617b7b2241f0a67a789f38f29dd731ae6ce3510302ab26fd429d65ce
[INFO] Account publicHex : 0x8d90d7e3e5b88b62941b23db10f35d13d1a06265e14993d43dff8562d23fded7466c006b74fa04b71c86c20a27ca20915e2f5e01dc466af9e080ff71229957e8
[INFO] Note: the entered password cannot contain Chinese characters!
Warning: -chain option ignored with -nocerts
Enter Export Password:
Verifying - Enter Export Password:
[INFO] Account Address   : 0x6a707388d949ca429c1473e6c5ce334ad9c455be
[INFO] Private Key (p12) : accounts/0x6a707388d949ca429c1473e6c5ce334ad9c455be.p12
[INFO] Public  Key (pem) : accounts/0x6a707388d949ca429c1473e6c5ce334ad9c455be.pem.pub
```

- Specify the p12 private key file to calculate the account address,**Enter p12 file password as prompted**

```shell
bash get_account.sh -P accounts/0x6a707388d949ca429c1473e6c5ce334ad9c455be.p12
```

Execute the above command, the result is as follows

```shell
Enter Import Password:
[INFO] Account Address   : 0x6a707388d949ca429c1473e6c5ce334ad9c455be
[INFO] Account privateHex: 0x7fe388c7617b7b2241f0a67a789f38f29dd731ae6ce3510302ab26fd429d65ce
[INFO] Account publicHex : 0x8d90d7e3e5b88b62941b23db10f35d13d1a06265e14993d43dff8562d23fded7466c006b74fa04b71c86c20a27ca20915e2f5e01dc466af9e080ff71229957e8
```

## Storage of accounts

- Java SDK supports private key string or file loading, so the private key of the account can be stored in the database or local file。
- Local files support two storage formats, with PKCS12 encrypted storage and PEM plaintext storage。
- When developing a business, you can choose the storage and management method of the private key according to the actual business scenario.。

## Use of account

### Console Load Private Key File

The account generation script get _ account.sh is provided in the console. The generated account private key file is in the accounts directory. You need to specify the private key file when loading the private key in the console.。
There are several ways to start the console:

```shell
bash start.sh
bash start.sh group0
bash start.sh group0 -pem pemName
bash start.sh group0 -p12 p12Name
```

#### Default startup

The console randomly generates an account and starts it with the group number specified in the console configuration file.。

```shell
bash start.sh
```

#### Specify group number to start

The console randomly generates an account and starts it with the group name specified on the command line。

```shell
bash start.sh group0
```

- Note: The specified group requires configuration beans in the console configuration file。

#### Start using a private key file in PEM format

- Start with the account of the specified pem file, enter the parameters: group number,-pem, pem file path

```shell
bash start.sh group0 -pem accounts/0xebb824a1122e587b17701ed2e512d8638dfb9c88.pem
```

#### Start using PKCS12 format private key file

- Use the specified p12 file account, you need to enter a password, enter parameters: group name,-p12, p12 file path

```shell
bash start.sh group0 -p12 accounts/0x5ef4df1b156bc9f077ee992a283c2dbb0bf045c0.p12
Enter Export Password:
```

### Java SDK loading private key file

If the account generation script get _ accounts.sh generates an account private key file in PEM or PKCS12 format, you can use the account by loading the PEM or PKCS12 account private key file。There are two classes for loading private keys: P12Manager and PEMManager, where P12Manager is used to load private key files in PKCS12 format and PEMManager is used to load private key files in PEM format。

- P12Manager Usage Example:

Load private key using code。

```java
/ / Initialize the BcosSDK
BcosSDK sdk =  BcosSDK.build(configFile);
/ / Initialize client for group group0
Client client = sdk.getClient("group0");
/ / Get the CryptoSuite object from the client
CryptoSuite cryptoSuite = client.getCryptoSuite();
/ / load pem account file
cryptoSuite.loadAccount("p12", p12AccountFilePath, password);
```

- PEMManager Usage Example

Load private key using code。

```java
/ / Initialize the BcosSDK
BcosSDK sdk =  BcosSDK.build(configFile);
/ / Initialize client for group group0
Client client = sdk.getClient("group0");
/ / Get the CryptoSuite object from the client
CryptoSuite cryptoSuite = client.getCryptoSuite();
/ / load pem account file
cryptoSuite.loadAccount("pem", pemAccountFilePath, null);
```

## Calculation of account address

The FISCO BCOS account address is calculated from the ECDSA public key and keccak is calculated for the hexadecimal representation of the ECDSA public key.-256sum hash, taking the hexadecimal representation of the last 20 bytes of the calculation result as the account address, each byte requires two hexadecimal representations, so the account address length is 40。FISCO BCOS account address compatible with Ethereum。
Note keccak-256sum with 'SHA3'**Not the same**For more information, see [here](https://ethereum.stackexchange.com/questions/550/which-cryptographic-hash-function-does-ethereum-use)。

[Ethereum Address Generation](https://kobl.one/blog/create-full-ethereum-keypair-and-address/)

### 1. Generate ECDSA private key

First, we use OpenSSL to generate the elliptic curve private key, and the parameters of the elliptic curve use secp256k1。Run the following command to generate the private key in PEM format and save it in the ecprivkey.pem file。

```shell
openssl ecparam -name secp256k1 -genkey -noout -out ecprivkey.pem
```

Execute the following instructions to view the contents of the file。

```shell
cat ecprivkey.pem
```

You can see output similar to the following。

```shell
-----BEGIN EC PRIVATE KEY-----
MHQCAQEEINHaCmLhw9S9+vD0IOSUd9IhHO9bBVJXTbbBeTyFNvesoAcGBSuBBAAK
oUQDQgAEjSUbQAZn4tzHnsbeahQ2J0AeMu0iNOxpdpyPo3j9Diq3qdljrv07wvjx
zOzLpUNRcJCC5hnU500MD+4+Zxc8zQ==
-----END EC PRIVATE KEY-----
```

Next, calculate the public key based on the private key and execute the following instructions

```shell
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}'
```

You can get output similar to the following

```shell
8d251b400667e2dcc79ec6de6a143627401e32ed2234ec69769c8fa378fd0e2ab7a9d963aefd3bc2f8f1cceccba54351709082e619d4e74d0c0fee3e67173ccd
```

### 2. Calculate the address based on the public key.

In this section, we calculate the corresponding account address based on the public key.。We need to get keccak-256sum tool, can be downloaded from [here](https://github.com/vkobel/ethereum-generate-wallet/tree/master/lib)。

```shell
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}' | ./keccak-256sum -x -l | tr -d ' -' | tail -c 41
```

You get output similar to the following, which is the calculated account address。

```shell
dcc703c0e500b653ca82273b7bfad8045d85a470
```

## Account Management

```eval_rst
.. important::
   To freeze, unfreeze, or revoke an account, you need to enable the blockchain permission mode. For more information, see the Permission Management User Guide < https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/committee_usage.html>`_
```

After the blockchain permission mode is enabled, each time a contract call is initiated, the account status is checked (tx.origin). The account status is recorded in the BFS '/ usr /' directory in the form of a storage table named '/ usr /'.+ The account address. If the account status is not found, the account is normal by default.。The account status under the BFS '/ usr /' directory is only created when the account status is actively set。**Only the governance committee member can operate the interface of account status management.。**

The governance committee can operate on the account through the AccountManagerPrecompiled interface, fixed address 0x10003。

```solidity
enum AccountStatus{
    normal,
    freeze,
    abolish
}

abstract contract AccountManager {
    / / Set account status, only governance members can call, 0- normal, others - Abnormal, if the account does not exist, it will be created first
    function setAccountStatus(address addr, AccountStatus status) public virtual returns (int32);
    / / Any user can call
    function getAccountStatus(address addr) public view virtual returns (AccountStatus);
}
```

### Freezing, unfreezing and abolishing of accounts

The governance committee can initiate transactions on pre-compiled contracts with a fixed address of 0x10003 and read and write the status of the account.。At the time of execution, it will be determined whether the transaction sponsor msg.sender is a governance member in the governance committee record, and if not, it will be rejected。It is worth noting that the address of the account of the governance committee does not allow the status to be modified。

Governance members can also freeze, unfreeze, and abolish accounts through the console. For details, see: [Freeze / Unfreeze Account Order](../operation_and_maintenance/console/console_commands.html#freezeaccount-unfreezeaccount), [Order to Abolish Account](../operation_and_maintenance/console/console_commands.html#abolishaccount)
