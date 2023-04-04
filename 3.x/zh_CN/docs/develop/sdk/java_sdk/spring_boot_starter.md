# Gradle SpringBoot 应用示例

标签：``spring-boot-starter`` ``开发区块链应用``

---------

本示例项目基于Java SDK + Gradle + SpringBoot方式来调用智能合约。

若您想通过Java SDK + Maven + SpringBoot方式访问智能合约，请参考[Maven示例](./spring_boot_crud.md)

## 前置条件

搭建FISCO BCOS 单群组区块链（Air版本），具体步骤[参考这里](../../../tutorial/air/build_chain.md)。

## 下载spring-boot-starter、证书拷贝

```eval_rst
.. note::
   若访问github速度较慢，可尝试从gitee克隆代码，gitee链接为https://gitee.com/FISCO-BCOS/spring-boot-starter.git
```

```shell
git clone https://github.com/FISCO-BCOS/spring-boot-starter.git
```

进入spring-boot-starter项目

```shell
cd spring-boot-starter
```

请将证书拷贝到src/main/resources/conf目录下。

## 配置连接节点

请修改application.properties，该文件包含如下信息：

```yml
### Java sdk configuration
cryptoMaterial.certPath=conf
network.peers[0]=127.0.0.1:20200
#network.peers[1]=127.0.0.1:20201

### System configuration
system.groupId=group0
system.hexPrivateKey=

### Springboot configuration
server.port=8080
```

其中：

- Java SDK configuration配置部分与 [Java SDK](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/config.html)一致。就本例而言，用户需要：
  - 请将network.peers更换成实际的链节点监听地址。
  - cryptoMaterial.certPath设为conf

- System configuration配置部分，需要配置：
  - system.hexPrivateKey是16进制的私钥明文，可运行Demos.java中的`keyGeneration`生成（文件路径：src/test/java/org/example/demo/Demos.java）。该配置允许为空，此时系统会随机生成一个私钥。
  - system.groupId设为目标群组，默认为group0

Demos.java 代码如下：（**以项目最新文件为准**）

```java
package org.example.demo;

import java.util.Arrays;
import org.example.demo.constants.ContractConstants;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.ECDSAKeyPair;
import org.fisco.bcos.sdk.v3.crypto.keypair.SM2KeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Demos {

    @Autowired private Client client;

    @Test
    public void keyGeneration() throws Exception {
        // ECDSA key generation
        CryptoKeyPair ecdsaKeyPair = new ECDSAKeyPair().generateKeyPair();
        System.out.println("ecdsa private key :" + ecdsaKeyPair.getHexPrivateKey());
        System.out.println("ecdsa public key :" + ecdsaKeyPair.getHexPublicKey());
        System.out.println("ecdsa address :" + ecdsaKeyPair.getAddress());
        // SM2 key generation
        CryptoKeyPair sm2KeyPair = new SM2KeyPair().generateKeyPair();
        System.out.println("sm2 private key :" + sm2KeyPair.getHexPrivateKey());
        System.out.println("sm2 public key :" + sm2KeyPair.getHexPublicKey());
        System.out.println("sm2 address :" + sm2KeyPair.getAddress());
    }

    @Test
    public void deploy() throws Exception {
        AssembleTransactionProcessor txProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor(
                        client, client.getCryptoSuite().getCryptoKeyPair());
        String abi = ContractConstants.HelloWorldAbi;
        String bin = ContractConstants.HelloWorldBinary;
        TransactionReceipt receipt =
                txProcessor.deployAndGetResponse(abi, bin, Arrays.asList()).getTransactionReceipt();
        if (receipt.isStatusOK()) {
            System.out.println("Contract Address:" + receipt.getContractAddress());
        } else {
            System.out.println("Status code:" + receipt.getStatus() + "-" + receipt.getMessage());
        }
    }
}
```

## 编译和运行

您可以在idea内直接运行，也可以编译成可执行jar包后运行。以编译jar包方式为例：

```shell
cd spring-boot-starter
bash gradlew bootJar
cd dist
```

会在dist目录生成spring-boot-starter-exec.jar，可执行此jar包：

```shell
java -jar spring-boot-starter-exec.jar
```

随后，即可访问相关接口。

set示例：

```shell
curl http://127.0.0.1:8080/hello/set?n=hello
```

返回示例（表示交易哈希）：

```shell
0x1c8b283daef12b38632e8a6b8fe4d798e053feb5128d9eaf2be77c324645763b
```

get示例：

```shell
curl http://127.0.0.1:8080/hello/get
```

返回示例：

```json
["hello"]
```

## 加入我们的社区

**FISCO BCOS开源社区**是国内活跃的开源社区，社区长期为机构和个人开发者提供各类支持与帮助。已有来自各行业的数千名技术爱好者在研究和使用FISCO BCOS。如您对FISCO BCOS开源技术及应用感兴趣，欢迎加入社区获得更多支持与帮助。

![](https://raw.githubusercontent.com/FISCO-BCOS/LargeFiles/master/images/QR_image.png)

## 相关链接

- FISCO BCOS： [FISCO BCOS文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/introduction.html)。
- Java Sdk： [JavaSdk文档](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/develop/sdk/java_sdk/index.html)。
- SpringBoot文档： [Spring Boot](https://spring.io/guides/gs/spring-boot/)。
- Maven工程示例：[maven示例](https://github.com/FISCO-BCOS/spring-boot-crud)。
