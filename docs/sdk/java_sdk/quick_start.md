# 快速入门

## 环境要求

- Java开发环境：JDK1.8 或者以上版本
- FISCO BCOS节点：请参考[FISCO BCOS安装](../../installation.html#fisco-bcos)搭建

## Java应用引入SDK

### 使用gradle引入SDK

```bash
compile ('org.fisco-bcos:java-sdk:1.0.0')
```

### 使用maven引入SDK

``` xml
<dependency>
    <groupId>org.fisco-bcos</groupId>
    <artifactId>java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## SDK证书配置

参考[java sdk证书配置](./configuration.html#id2)。

```eval_rst
.. note::
    - 大部分场景仅需要配置 `certPath` 配置项即可，其他配置项不需额外配置；
    - SDK证书获取：若参考 `安装 <../../installation.html>`_ 搭建区块链，则参考 `这里 <../../installation.html#id7>`_ 将 `nodes/${ip}/sdk/` 目录下的证书拷贝到 `certPath` 指定的路径；若区块链节点参考 `运维部署工具 <../../installation.md>`_ 搭建，则参考 `这里 <../../../enterprise_tools/tutorial_one_click.html#id15>`_ 将 `generator/meta` 文件夹下的SDK证书拷贝到 `certPath` 指定路径；
    - SDK与节点间SSL连接方式，可通过节点配置项 `sm_crypto_channel` 判断，该配置项详细说明请参考 `FISCO BCOS配置文件与配置项说明 <../../manual/configuration.html#id10>`_ .
```

## SDK使用示例

```eval_rst
.. note::
    java sdk同时支持将 `solidity` 转换为 `java` 文件后，调用相应的 `java` 方法部署和调用合约，也支持构造交易的方式部署和调用合约，这里主要展示前者的调用方法，后者详细的使用方法请参考 `这里 <./assemble_transaction.html>`_ 
```

### `solidity`代码转`java`代码

java sdk和控制台`console`均提供了将`solidity`代码转换为`java`代码的工具。

**使用java-sdk提供的工具**
```bash
$ mkdir ~/fisco && cd ~/fisco
# 获取java-sdk代码
$ git clone https://github.com/FISCO-BCOS/java-sdk
# 编译
$ ./gradlew clean build -x test
# 进入dist目录，创建合约存放目录
$ cd dist && mkdir -p contracts/solidity
# 将需要转换为java代码的sol文件拷贝到~/fisco/java-sdk/dist/contracts/solidity路径下
# 转换sol, 其中${packageName}是生成的java代码包路径
# 生成的java代码位于 ~/fisco/java-sdk/dist.contracts/sdk/java目录下
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.codegen.DemoSolcToJava ${packageName}
```

**使用控制台提供的工具**

控制台提供了`sol2java.sh`脚本可将`solidity`转换为`java`代码。

```bash
$ mkdir ~/fisco && cd ~/fisco
# 获取控制台
$ cd ~/fisco && curl -LO https://github.com/FISCO-BCOS/console/releases/download/v1.3.0/download_console.sh && bash download_console.sh
# 将需要转换为java代码的sol文件拷贝到~/fisco/console/contracts/solidity路径下
# 使用脚本sol2java.sh转换solidity代码, 其中${packageName}是生成的java代码包路径
# 生成的java代码位于 ~/fisco/java-sdk/dist.contracts/sdk/java目录下
bash sol2java.sh ${packageName}
```

使用java-sdk或控制台将`solidity`代码转换为`java`代码后，即可将生成的`java`代码拷贝到规划的包路径，通过调用该`java`代码部署和调用合约。


### `BcosSDK`初始化

`BcosSDK`是使用java sdk开发应用时必须初始化的对象，目前同时支持通过`toml`和`xml`配置文件初始化`BcosSDK`对象。

#### 通过`toml`配置文件初始化`BcosSDK`

java sdk `toml`配置文件说明请参考[这里](./configuration.md)，配置示例请参考java sdk源码的`src/test/resources/config-example.toml`, 通过`toml`配置文件初始化`BcosSDK`的代码示例如下：

```java
public class BcosSDKTest
{
    public BcosSDK initBcosSDK()
    {
        String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.toml").getPath();
        return BcosSDK.build(configFile);
    }
}
```

#### 通过`xml`配置文件初始化`BcosSDK`

为了适配更多场景，java sdk支持使用`xml`初始化`BcosSDK`, `xml`配置示例请参考java sdk源码的`src/test/resources/applicationContext-sample.xml`, 配置项的含义参考[配置说明](./configuration.md).

通过`xml`配置文件初始化`BcosSDK`之前，需要先引入`spring`。

**通过gradle引入`spring`如下**：

```bash
def spring_version = "4.3.27.RELEASE"
List spring = [
        "org.springframework:spring-core:$spring_version",
        "org.springframework:spring-beans:$spring_version",
        "org.springframework:spring-context:$spring_version",
        "org.springframework:spring-tx:$spring_version",
]
compile spring
```

**通过maven引入`spring`如下**：

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>4.3.27.RELEASE</version>

    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>4.3.27.RELEASE</version>

    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>4.3.27.RELEASE</version>

    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>4.3.27.RELEASE</version>
</dependency>
```

**使用`applicationContext-sample`初始化`BcosSDK`如下**：

```java
ApplicationContext context =
    new ClassPathXmlApplicationContext("classpath:applicationContext-sample.xml");
BcosSDK sdk = context.getBean(BcosSDK.class);
```

### 部署和调用合约

以使用java sdk调用群组1的`getBlockNumber`接口获取群组1最新块高，并向群组1部署和调用`HelloWorld`合约为例，对应的示例代码如下：

```java
public class BcosSDKTest
{
    // 获取配置文件路径
    public final String configFile = BcosSDKTest.class.getClassLoader().getResource("config-example.toml").getPath();
     public void testClient() throws ConfigException {
         // 初始化BcosSDK
        BcosSDK sdk =  BcosSDK.build(configFile);
        // 为群组1初始化client
        Client client = sdk.getClient(Integer.valueOf(1));
    
        // 获取群组1的块高
        BlockNumber blockNumber = client.getBlockNumber();

        // 向群组1部署HelloWorld合约
        HelloWorld helloWorld = HelloWorld.deploy(client, client.getCryptoInterface());

        // 调用HelloWorld合约的get接口
        String getValue = helloWorld.get();
        
        // 调用HelloWorld合约的set接口
        TransactionReceipt receipt = helloWorld.set("Hello, fisco");
     }
}
```