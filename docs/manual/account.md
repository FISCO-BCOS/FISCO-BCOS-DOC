# 账户管理
FISCO BCOS使用账户来标识和区分每一个独立的用户。在采用公私钥体系的区块链系统里，每一个账户对应着一对公钥和私钥。其中，由公钥经哈希等安全的单向性算法计算后得到地址字符串被用作该账户的账户名，即**账户地址**，为了与智能合约的地址相区别和一些其他的历史原因，账户地址也常被称之**外部账户地址**。而仅有用户知晓的私钥则对应着传统认证模型中的密码。用户需要通过安全的密码学协议证明其知道对应账户的私钥，来声明其对于该账户的所有权，以及进行敏感的账户操作。

```eval_rst
.. important::

    在之前的其他教程中，为了简化操作，使用了工具提供的默认的账户进行操作。但在实际应用部署中，用户需要创建自己的账户，并妥善保存账户私钥，避免账户私钥泄露等严重的安全问题。
```

本文将具体介绍账户的创建、存储和使用方式，要求阅读者有一定的Linux操作基础。

FISCO BCOS提供了脚本和Web3SDK用以创建账户，同时也提供了Web3SDK和控制台来存储账户私钥。用户可以根据需求选择将账户私钥存储为PEM或者PKCS12格式的文件。其中，PEM格式使用明文存储私钥，而PKCS12使用用户提供的口令加密存储私钥。

## 账户的创建

### 使用脚本创建账户
#### 1. 获取脚本
```bash
curl -LO https://media.githubusercontent.com/media/FISCO-BCOS/LargeFiles/master/tools/get_account.sh && chmod u+x get_account.sh && bash get_account.sh -h
```
执行上面的指令，看到如下输出则下载到了正确的脚本，否则请重试。
```bash
Usage: ./get_account.sh
    default       generate account and store private key in PEM format file
    -p            generate account and store private key in PKCS12 format file
    -k [FILE]     calculate address of PEM format [FILE]
    -P [FILE]     calculate address of PKCS12 format [FILE]
    -h Help
```

#### 2. 使用脚本生成PEM格式私钥
- 生成私钥与地址
```bash
bash get_account.sh
```
执行上面的命令，可以得到类似下面的输出，包括账户地址和以账户地址为文件名的私钥PEM文件。
```bash
[INFO] Account Address   : 0xee5fffba2da55a763198e361c7dd627795906ead
[INFO] Private Key (pem) : accounts/0xee5fffba2da55a763198e361c7dd627795906ead.pem
```
- 指定PEM私钥文件计算账户地址
```bash
bash get_account.sh -k accounts/0xee5fffba2da55a763198e361c7dd627795906ead.pem
```
执行上面的命令，结果如下
```bash
[INFO] Account Address   : 0xee5fffba2da55a763198e361c7dd627795906ead
```
#### 3. 使用脚本生成PKCS12格式私钥
- 生成私钥与地址
```bash
bash get_account.sh -p
```
执行上面的命令，可以得到类似下面的输出，按照提示输入密码，生成对应的p12文件。
```bash
Enter Export Password:
Verifying - Enter Export Password:
[INFO] Account Address   : 0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1
[INFO] Private Key (p12) : accounts/0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1.p12
```
- 指定p12私钥文件计算账户地址，**按提示输入p12文件密码**
```bash
bash get_account.sh -P accounts/0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1.p12
```
执行上面的命令，结果如下
```bash
Enter Import Password:
MAC verified OK
[INFO] Account Address   : 0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1
```

### 调用Web3SDK创建账户
```bash
//创建普通账户
EncryptType.encryptType = 0;
//创建国密账户，向国密区块链节点发送交易需要使用国密账户
// EncryptType.encryptType = 1; 
Credentials credentials = GenCredential.create();
//账户地址
String address = credentials.getAddress();
//账户私钥 
String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
//账户公钥 
String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
```

更多操作详情，请参见[创建并使用指定外部账号](../sdk/java_sdk.html#id5)。

## 账户的存储
- web3SDK支持通过私钥字符串或者文件加载，所以账户的私钥可以存储在数据库中或者本地文件。
- 本地文件支持两种存储格式，其中PKCS12加密存储，而PEM格式明文存储。
- 开发业务时可以根据实际业务场景选择私钥的存储管理方式。

## 账户的使用

### 控制台加载私钥文件
控制台提供账户生成脚本get_account.sh，生成的的账户文件在accounts目录下，控制台加载的账户文件必须放置在该目录下。
控制台启动方式有如下几种：
```
./start.sh
./start.sh groupID
./start.sh groupID -pem pemName
./start.sh groupID -p12 p12Name
```
##### 默认启动
控制台随机生成一个账户，使用控制台配置文件指定的群组号启动。
```bash
./start.sh
```
##### 指定群组号启动
控制台随机生成一个账户，使用命令行指定的群组号启动。
```bash
./start.sh 2
```
- 注意：指定的群组在控制台配置文件中需要配置bean。

##### 使用PEM格式私钥文件启动
- 使用指定的pem文件的账户启动，输入参数：群组号、-pem、pem文件路径
```bash
./start.sh 1 -pem accounts/0xebb824a1122e587b17701ed2e512d8638dfb9c88.pem
```
##### 使用PKCS12格式私钥文件启动
- 使用指定的p12文件的账户，需要输入密码，输入参数：群组号、-p12、p12文件路径
```bash
./start.sh 1 -p12 accounts/0x5ef4df1b156bc9f077ee992a283c2dbb0bf045c0.p12
Enter Export Password:
```

### Web3SDK加载私钥文件

如果通过账户生成脚本get_accounts.sh生成了PEM或PKCS12格式的账户私钥文件，则可以通过加载PEM或PKCS12账户私钥文件使用账户。加载私钥有两个类：P12Manager和PEMManager，其中，P12Manager用于加载PKCS12格式的私钥文件，PEMManager用于加载PEM格式的私钥文件。

* P12Manager用法举例：
在applicationContext.xml中配置PKCS12账户的私钥文件路径和密码
```xml
<bean id="p12" class="org.fisco.bcos.channel.client.P12Manager" init-method="load" >
	<property name="password" value="123456" />
	<property name="p12File" value="classpath:0x0fc3c4bb89bd90299db4c62be0174c4966286c00.p12" />
</bean>
```
开发代码
```java
//加载Bean
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
P12Manager p12 = context.getBean(P12Manager.class);
//提供密码获取ECKeyPair，密码在生产p12账户文件时指定
ECKeyPair p12KeyPair = p12.getECKeyPair(p12.getPassword());
			
//以十六进制串输出私钥和公钥
System.out.println("p12 privateKey: " + p12KeyPair.getPrivateKey().toString(16));
System.out.println("p12 publicKey: " + p12KeyPair.getPublicKey().toString(16));

//生成web3sdk使用的Credentials
Credentials credentials =  GenCredential.create(p12KeyPair.getPrivateKey().toString(16));
System.out.println("p12 Address: " + credentials.getAddress());
```

* PEMManager使用举例

在applicationContext.xml中配置PEM账户的私钥文件路径
```xml
<bean id="pem" class="org.fisco.bcos.channel.client.PEMManager" init-method="load" >
	<property name="pemFile" value="classpath:0x0fc3c4bb89bd90299db4c62be0174c4966286c00.pem" />
</bean>
```
使用代码加载
```java
//加载Bean
ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext-keystore-sample.xml");
PEMManager pem = context.getBean(PEMManager.class);
ECKeyPair pemKeyPair = pem.getECKeyPair();

//以十六进制串输出私钥和公钥
System.out.println("PEM privateKey: " + pemKeyPair.getPrivateKey().toString(16));
System.out.println("PEM publicKey: " + pemKeyPair.getPublicKey().toString(16));

//生成web3sdk使用的Credentials
Credentials credentialsPEM = GenCredential.create(pemKeyPair.getPrivateKey().toString(16));
System.out.println("PEM Address: " + credentialsPEM.getAddress());
```

## 账户地址的计算
FISCO BCOS的账户地址由ECDSA公钥计算得来，对ECDSA公钥的16进制表示计算keccak-256sum哈希，取计算结果的后20字节的16进制表示作为账户地址，每个字节需要两个16进制数表示，所以账户地址长度为40。FISCO BCOS的账户地址与以太坊兼容。
注意keccak-256sum与`SHA3`**不相同**，详情参考[这里](https://ethereum.stackexchange.com/questions/550/which-cryptographic-hash-function-does-ethereum-use)。

[以太坊地址生成](https://kobl.one/blog/create-full-ethereum-keypair-and-address/)
### 1. 生成ECDSA私钥
首先，我们使用OpenSSL生成椭圆曲线私钥，椭圆曲线的参数使用secp256k1。执行下面的命令，生成PEM格式的私钥并保存在ecprivkey.pem文件中。
```bash
openssl ecparam -name secp256k1 -genkey -noout -out ecprivkey.pem
```
执行下面的指令，查看文件内容。
```bash
cat ecprivkey.pem
```
可以看到类似下面的输出。
```bash
-----BEGIN EC PRIVATE KEY-----
MHQCAQEEINHaCmLhw9S9+vD0IOSUd9IhHO9bBVJXTbbBeTyFNvesoAcGBSuBBAAK
oUQDQgAEjSUbQAZn4tzHnsbeahQ2J0AeMu0iNOxpdpyPo3j9Diq3qdljrv07wvjx
zOzLpUNRcJCC5hnU500MD+4+Zxc8zQ==
-----END EC PRIVATE KEY-----
```
接下来根据私钥计算公钥，执行下面的指令
```bash
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}'
```
可以得到类似下面的输出
```bash
8d251b400667e2dcc79ec6de6a143627401e32ed2234ec69769c8fa378fd0e2ab7a9d963aefd3bc2f8f1cceccba54351709082e619d4e74d0c0fee3e67173ccd
```

### 2. 根据公钥计算地址
本节我们根据公钥计算对应的账户地址。我们需要获取keccak-256sum工具，可以从[这里下载](https://github.com/vkobel/ethereum-generate-wallet/tree/master/lib)。
```bash
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}' | ./keccak-256sum -x -l | tr -d ' -' | tail -c 41
```
得到类似下面的输出，就是计算得出的账户地址。
```bash
dcc703c0e500b653ca82273b7bfad8045d85a470
```
