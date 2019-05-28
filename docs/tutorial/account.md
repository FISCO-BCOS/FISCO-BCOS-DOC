# FISCO BCOS账号
FISCO BCOS采用账号模型，账号这个术语是代表着用户、智能合约的唯一性存在。

在采用公私钥体系的区块链系统里，用户创建一个公私钥对，经过hash等算法换算即得到一个唯一性的地址串，代表这个用户的账号，用户用该私钥管理这个账号里的资产。用户账号在链上不一定有对应的存储空间，而是由智能合约管理用户在链上的数据，因此这种用户账号也会被称为“外部账号”。

对智能合约来说，一个智能合约被部署后，在链上就有了一个唯一的地址，也称为合约账号，指向这个合约的状态位、二进制代码、相关状态数据的索引等。智能合约运行过程中，会通过这个地址加载二进制代码，根据状态数据索引去访问世界状态存储里对应的数据，根据运行结果将数据写入世界状态存储，更新合约账号里的状态数据索引。智能合约被注销时，主要是更新合约账号里的合约状态位，将其置为无效，一般不会直接清除该合约账号的实际数据。

FISCO BCOS提供的Web3SDK和控制台支持PEM和PKCS12两种格式的文件用于存储私钥，其中PEM格式使用明文存储私钥，而PKCS12使用用户提供的口令加密存储私钥。

本文将介绍FISCO BCOS账号的生成、存储和使用方式，要求阅读者有一定的Linux基础。

## FISCO BCOS账号生成

### get_account.sh脚本
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
执行上面的命令，可以得到类似下面的输出，包括账号地址和以账号地址为文件名的私钥PEM文件。
```bash
[INFO] Account Address   : 0xee5fffba2da55a763198e361c7dd627795906ead
[INFO] Private Key (pem) : accounts/0xee5fffba2da55a763198e361c7dd627795906ead.pem
```
- 指定PEM私钥文件计算账号地址
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
- 指定p12私钥文件计算账号地址，**按提示输入p12文件密码**
```bash
bash get_account.sh -P accounts/0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1.p12
```
执行上面的命令，结果如下
```bash
Enter Import Password:
MAC verified OK
[INFO] Account Address   : 0x02f1b23310ac8e28cb6084763d16b25a2cc7f5e1
```

### [调用Web3SDK接口生成](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/sdk.html#id5)
```bash
//创建普通外部账号
EncryptType.encryptType = 0;
//创建国密外部账号，向国密区块链节点发送交易需要使用国密外部账号
// EncryptType.encryptType = 1; 
Credentials credentials = GenCredential.create();
//账号地址
String address = credentials.getAddress();
//账号私钥 
String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
//账号公钥 
String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
```

## 账号存储方式
- web3SDK支持通过私钥字符串或者文件加载，所以账号的私钥可以存储在数据库中或者本地文件。
- 本地文件支持两种存储格式，其中PKCS12加密存储，而PEM格式明文存储。
- 开发业务时可以根据实际业务场景选择私钥的存储管理方式。

## 账号使用方式

### 控制台加载私钥文件
控制台提供账号生成脚本get_account.sh，生成的的账号文件在accounts目录下，控制台加载的账号文件必须放置在该目录下。
控制台启动方式有如下几种：
```
./start.sh
./start.sh groupID
./start.sh groupID -pem pemName
./start.sh groupID -p12 p12Name
```
##### 默认启动
控制台随机生成一个账号，使用控制台配置文件指定的群组号启动。
```bash
./start.sh
```
##### 指定群组号启动
控制台随机生成一个账号，使用命令行指定的群组号启动。
```bash
./start.sh 2
```
- 注意：指定的群组在控制台配置文件中需要配置bean。

##### 使用PEM格式私钥文件启动
- 使用指定的pem文件的账号启动，输入参数：群组号、-pem、pem文件名或路径
```bash
./start.sh 1 -pem accounts/0xebb824a1122e587b17701ed2e512d8638dfb9c88.pem
```
##### 使用PKCS12格式私钥文件启动
- 使用指定的p12文件的账号，需要输入密码，输入参数：群组号、-p12、p12文件名或路径
```bash
./start.sh 1 -p12 accounts/0x5ef4df1b156bc9f077ee992a283c2dbb0bf045c0.p12
Enter Export Password:
```

### Web3SDK加载私钥文件

如果通过账号生成脚本get_accounts.sh生成了PEM或PKCS12格式的账号私钥文件，则可以通过加载PEM或PKCS12账号私钥文件使用账号。加载私钥有两个类：P12Manager和PEMManager，其中，P12Manager用于加载PKCS12格式的私钥文件，PEMManager用于加载PEM格式的私钥文件。

* P12Manager用法举例：
在applicationContext.xml中配置PKCS12账号的私钥文件路径和密码
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
//提供密码获取ECKeyPair，密码在生产p12账号文件时指定
ECKeyPair p12KeyPair = p12.getECKeyPair(p12.getPassword());
			
//以十六进制串输出私钥和公钥
System.out.println("p12 privateKey: " + p12KeyPair.getPrivateKey().toString(16));
System.out.println("p12 publicKey: " + p12KeyPair.getPublicKey().toString(16));

//生成web3sdk使用的Credentials
Credentials credentials = Credentials.create(p12KeyPair);
System.out.println("p12 Address: " + credentials.getAddress());
```

* PEMManager使用举例

在applicationContext.xml中配置PEM账号的私钥文件路径
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
Credentials credentialsPEM = Credentials.create(pemKeyPair);
System.out.println("PEM Address: " + credentialsPEM.getAddress());
```

## 账号计算方法
FISCO BCOS账号由ECDSA公钥计算得来，对ECDSA公钥的16进制表示计算keccak-256sum哈希，取计算结果的后20字节的16进制表示作为账号地址，每个字节需要两个16进制数表示，所以账号地址长度为40。FISCO BCOS账号与以太坊兼容。
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
本节我们根据公钥计算对应的账号地址。我们需要获取keccak-256sum工具，可以从[这里下载](https://github.com/vkobel/ethereum-generate-wallet/tree/master/lib)。
```bash
openssl ec -in ecprivkey.pem -text -noout 2>/dev/null| sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}' | ./keccak-256sum -x -l | tr -d ' -' | tail -c 41
```
得到类似下面的输出，就是计算得出的账号地址。
```bash
dcc703c0e500b653ca82273b7bfad8045d85a470
```
