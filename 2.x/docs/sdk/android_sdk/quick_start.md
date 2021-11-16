# Android SDK 快速入门

标签：``Android SDK`` ``导入 Android SDK`` ``合约开发``

----

关于如何安装 Android 集成开发环境及如何创建一个 Android 应用，请参考[Android 开发者手册](https://developer.android.google.cn/studio/intro)，此处不再赘述。本章节重点描述如何在一个 Android 应用中通过 android-sdk 与区块链节点进行交互。

### 1. 权限配置

为了保障 android-sdk 的正常使用，开发者需在项目的配置文件`app/src/main/AndroidManifest.xml`增加如下权限。

```java
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.INTERNET"/>
```

### 2. SDK 接入

开发者可通过 Gradle 方式进行 android-sdk 接入，需在项目的`app/build.gradle`中添加以下源和依赖进行集成。

```java
allprojects {
    repositories {
        mavenCentral()
        maven{
            url "https://maven.aliyun.com/repository/google"
        }
        maven{
            url "https://maven.aliyun.com/repository/public"
        }
        maven{
            url "https://maven.aliyun.com/repository/gradle-plugin"
        }
        maven{
            url "https://oss.sonatype.org/content/repositories/snapshots"
        }
        maven {
            url "https://oss.sonatype.org/service/local/staging/deploy/maven2"
        }
    }
}

## 注：对于接入 sdk 的项目已有的依赖，可忽略对应的添加
dependencies {
    implementation 'org.fisco-bcos.android-sdk:fisco-bcos-android-sdk:1.0.0'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.12.1'
    implementation 'com.fasterxml.jackson.core:jackson-annotations:2.12.1'
    implementation 'com.fasterxml.jackson.core:jackson-core:2.12.1'
    implementation 'org.apache.commons:commons-lang3:3.1'
    implementation 'commons-io:commons-io:2.4'
    implementation 'com.squareup.okhttp3:okhttp:3.13.0'
}
```

### 3. 初始化 sdk

初始化 android-sdk 时需提供`ProxyConfig`配置信息，包括以下内容。

| 设置项         | 是否可选 | 说明                                                                                       |
| -------------- | -------- | ------------------------------------------------------------------------------------------ |
| chainId        | 必选     | 链标识，需与 FISCO-BCOS 链配置的一致                                                       |
| crytoType      | 必选     | 是否使用国密交易及账号，需与 FISCO-BCOS 链配置的一致，目前可选值包括 ECDSA_TYPE 和 SM_TYPE |
| hexPrivateKey  | 可选     | 发交易进行签名使用的私钥，开发者可从文件或数据库中读取进行设置，如不设置，sdk 内部随机生成 |
| networkHandler | 可选     | http/https 请求实现，开发者可自行实现并传入，如不传入，采用 sdk 内部实现                   |

对于`networkHandler`，android-sdk 提供了`http`和`https`两种传输协议的内置实现。其中，NetworkHandlerImp 实现 http 请求，直接访问 bcos-node-proxy；NetworkHandlerHttpsImp 实现 https 请求，通过 Nginx 访问 bcos-node-proxy，该方式须在`assets`目录放置 Nginx 的证书。**基于安全考虑，建议使用 NetworkHandlerHttpsImp。**

基于`NetworkHandlerHttpsImp`初始化 android-sdk 例子如下。

```java
    ProxyConfig proxyConfig = new ProxyConfig();
    proxyConfig.setChainId("1");
    proxyConfig.setCryptoType(CryptoType.ECDSA_TYPE);
    proxyConfig.setHexPrivateKey("65c70b77051903d7876c63256d9c165cd372ec7df813d0b45869c56fcf5fd564");
    NetworkHandlerHttpsImp networkHandlerImp = new NetworkHandlerHttpsImp();
    networkHandlerImp.setIpAndPort("https://127.0.0.1:8180/");
    CertInfo certInfo = new CertInfo("nginx.crt");
    networkHandlerImp.setCertInfo(certInfo);
    networkHandlerImp.setContext(getApplicationContext());
    proxyConfig.setNetworkHandler(networkHandlerImp);
    BcosSDK sdk = BcosSDK.build(proxyConfig);
```

### 4. 编译合约

开发者基于智能合约开发应用前，需将 Solidity 合约文件通过 tool 提供的工具，编译生成 **Java 文件**和 **abi 、binary 文件**。

- [tool Github 地址](https://github.com/FISCO-BCOS/fisco-bcos-android-sdk/tree/main/tool)
- [tool Gitee 地址](https://gitee.com/FISCO-BCOS/fisco-bcos-android-sdk/tree/main/tool)

本节提供一个示例合约`HelloWorld.sol`，并描述合约的编译过程。

```text
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
```

- 安装/确认 JDK 版本，要求版本大于等于 1.8，推荐使用 JDK 14；
- 在`tool/`目录执行`bash get_console.sh`下载控制台，下载完成后在当前目录下生成`console/`目录；
- 将需编译的合约`HelloWorld.sol`放置于 tool/console/contracts/solidity 目录；
- 在`tool/console/`目录执行`bash sol2java.sh org.fisco.bcos.sdk`，其中参数 org.fisco.bcos.sdk 指定生成的 Java 包名；
- 编译生成的`HelloWorld.java`位于 tool/console/contracts/sdk/java 目录。
- 编译生成的`HelloWorld.abi`位于 tool/console/contracts/sdk/abi 目录，`HelloWorld.bin`位于 tool/console/contracts/sdk/bin 目录。

### 5. 部署及调用合约

本节基于上一节`合约编译`生成的两类文件，分别描述合约操作的两种方式。

#### 5.1 基于合约 Java 类部署及调用合约

以`HelloWorld`合约为例说明如何部署、加载及调用合约。调用以下代码前需`import HelloWorld.java`到项目中。

```Java
try {
    Client client = sdk.getClient(1);

    HelloWorld sol = HelloWorld.deploy(client, client.getCryptoSuite().getCryptoKeyPair());
    logger.info("deploy contract , contract address: " + JsonUtils.toJson(sol.getContractAddress()));
    // HelloWorld sol = HelloWorld.load("0x2ffa020155c6c7e388c5e5c9ec7e6d403ec2c2d6", client, client.getCryptoSuite().getCryptoKeyPair());
    TransactionReceipt ret1 = sol.set("Hello, FISCO BCOS.");
    logger.info("send, receipt: " + JsonUtils.toJson(ret1));
    String ret2 = sol.get();
    logger.info("call to return string, result: " + ret2);
    BcosTransaction transaction = client.getTransactionByHash(ret1.getTransactionHash());
    logger.info("getTransactionByHash, result: " + JsonUtils.toJson(transaction.getResult()));
    BcosTransactionReceipt receipt = client.getTransactionReceipt(ret1.getTransactionHash());
    logger.info("getTransactionReceipt, result: " + JsonUtils.toJson(receipt.getResult()));

    client.stop();
} catch (NetworkHandlerException e) {
    logger.error("NetworkHandlerException error info: " + e.getMessage());
} catch (Exception e) {
    logger.error("error info: " + e.getMessage());
    e.printStackTrace();
}
```

#### 5.2 基于合约 abi 和 binary 部署及调用合约

以`HelloWorld`合约为例说明如何部署及调用合约。调用以下代码前需先将`HelloWorld.abi`和`HelloWorld.bin`放置在项目的`assets`目录。**通过此方式，只需将合约的 abi 和 bin 文件放置于项目中，不需重新编译项目即可进行该合约的部署及调用。**

```Java
try {
    Client client = sdk.getClient(1);

    String contractName = "HelloWorld";
    String contractAbi = ""; // 读取 HelloWorld.abi 文件内容写入
    String contractBin = ""; // 读取 HelloWorld.bin 文件内容写入
    logger.info("contract abi: " + contractAbi + ", bin: " + contractBin);
    TransactionProcessor manager = TransactionProcessorFactory.createTransactionProcessor(client, client.getCryptoSuite().createKeyPair(), contractName, contractAbi, contractBin);
    TransactionResponse response = manager.deployAndGetResponse(new ArrayList<>());
    String contractAddress = response.getContractAddress();    
    logger.info("deploy contract , contract address: " + contractAddress);
    List<Object> paramsSet = new ArrayList<>();
    paramsSet.add("Hello, FISCO BCOS.");
    TransactionResponse ret1 = manager.sendTransactionAndGetResponse(contractAddress, "set", paramsSet);
    logger.info("send, receipt: " + JsonUtils.toJson(ret1));
    List<Object> paramsGet = new ArrayList<>();
    CallResponse ret2 = manager.sendCall(client.getCryptoSuite().getCryptoKeyPair().getAddress(), contractAddress, "get", paramsGet);
    List<Object> ret3 = JsonUtils.fromJsonList(ret2.getValues(), Object.class);
    logger.info("call to return object list, result: " + ret3);

    client.stop();
} catch (Exception e) {
    logger.error("error info: " + e.getMessage());
    e.printStackTrace();
}
```