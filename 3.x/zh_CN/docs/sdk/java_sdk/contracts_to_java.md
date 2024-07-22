# 生成智能合约的Java接口文件

在控制台`console`和``java-sdk-demo``均提供了工具，可以将`solidity`合约生成出调用该合约`java`工具类。本例中使用``console``将Solidity合约生成出做调用该合约`java`工具类为例子。

> 体验 webankblockchain-liquid（以下简称WBC-Liquid），请参考第5小节。
>
> 使用``java-sdk-demo``的例子请看第7小节。

## 1. 下载控制台

```shell
$ mkdir -p ~/fisco && cd ~/fisco
# 获取控制台
$ curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.6.0/download_console.sh

# 若因为网络问题导致长时间无法执行以上命令，请尝试以下命令：
$ curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh

$ bash download_console.sh
$ cd ~/fisco/console
```

## 2. 将合约放置到控制台的合约目录

**然后，将您要用到的Solidity智能合约放入``~/fisco/console/contracts/solidity``的目录**。本次我们用console中的HelloWorld.sol作为例子。保证HelloWorld.sol在指定的目录下。

```shell
# 当前目录~/fisco/console
$ ls contracts/solidity 
```

得到返回

```shell
HelloWorld.sol  KVTableTest.sol ShaTest.sol KVTable.sol ...
```

## 3. 生成调用该智能合约的Java类

```shell
# 当前目录~/fisco/console
$ bash contract2java.sh solidity -p org.com.fisco -s ./contracts/solidity/HelloWorld.sol
# 以上命令中参数“org.com.fisco”是指定产生的java类所属的包名。
# 通过命令./contract2java.sh -h可查看该脚本使用方法
```

得到返回

```shell
*** Compile solidity HelloWorld.sol*** 
INFO: Compile for solidity HelloWorld.sol success.
*** Convert solidity to java  for HelloWorld.sol success ***
```

`contract2java.sh`使用方法将在附录2中详细介绍。

查看编译结果

```shell
$ ls contracts/sdk/java/org/com/fisco 
# 得到返回
# HelloWorld.java
```

运行成功之后，将会在`console/contracts/sdk`目录生成java、abi和bin目录，如下所示。

```shell
|-- abi # 编译生成的abi目录，存放solidity合约编译的abi文件
|   |-- HelloWorld.abi
|-- bin # 编译生成的bin目录，存放solidity合约编译的bin文件
|   |-- HelloWorld.bin
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|       |-- com
|           |-- fisco
|               |-- HelloWorld.java # Solidity编译的HelloWorld Java文件
```

Java目录下生成了`org/com/fisco/`包路径目录。包路径目录下将会生成Java合约文件`HelloWorld.java`。其中`HelloWorld.java`是Java应用所需要的Java合约文件。

## 4. 生成的Java文件代码结构

下面以生成的`HelloWorld.java`的接口列表为例，简要说明代码结构。

```java
public class HelloWorld extends Contract {
    // 构造函数
    protected HelloWorld(String contractAddress, Client client, CryptoKeyPair credential);
    // 根据CryptoSuite获取合约的code，若是国密就返回国密的code
    public static String getBinary(CryptoSuite cryptoSuite);
    // 获取合约的ABI json字符串
    public static String getABI();
    // HelloWorld合约get接口
    public String get() throws ContractException;
    // HelloWorld合约get接口的Function类，记录了输入和返回的类型，可用于ABI解析
    public Function getMethodGetRawFunction() throws ContractException;
    // HelloWorld合约set接口，输入String类型，返回交易回执
    public TransactionReceipt set(String n);
    // HelloWorld合约set接口的Function类，记录了输入和返回的类型，可用于ABI解析
    public Function getMethodSetRawFunction(String n) throws ContractException;
    // 获取已签名的调用set接口的交易，获取后可以直接发到链上
    public String getSignedTransactionForSet(String n);
    // HelloWorld合约set异步接口，输入String类型，返回交易哈希
    public String set(String n, TransactionCallback callback);
    // HelloWorld合约set的input解析
    public Tuple1<String> getSetInput(TransactionReceipt transactionReceipt);
    // 若链上有已知的HelloWorld合约，可以用改接口直接加载出Java 的HelloWorld类。注意：ABI必须相同，否则会出现调用失败
    public static HelloWorld load(String contractAddress, Client client, CryptoKeyPair credential);
    // 向链上发起部署合约操作，返回Java 的HelloWorld类。
    public static HelloWorld deploy(Client client, CryptoKeyPair credential) throws ContractException;
}
```

## 5. 生成WBC-Liquid合约的Java接口文件

与上文Solidity合约类似，如果你想体验 webankblockchain-liquid（以下简称WBC-Liquid）的部署操作，控制台也为你提供了例子。

在使用之前，请先保证cargo liquid的编译环境，使用搭建请参考：https://liquid-doc.readthedocs.io/。

### 5.1 WBC-Liquid合约的编译

可在控制台dist目录下contracts/liquid下查看，下面以hello_world为例子：

```shell
$ ls contracts/liquid
asset_test    hello_world   kv_table_test

$ cd contracts/liquid/hello_world

# 使用cargo liquid 编译
$ cargo liquid build
[1/4] 🔍  Collecting crate metadata
[2/4] 🚚  Building cargo project
[3/4] 🔗  Optimizing Wasm bytecode
[4/4] 📃  Generating ABI file

✨ Done in 1 minute, your project is ready now:
Binary: ~/fisco/contracts/liquid/hello_world/target/hello_world.wasm
   ABI: ~/fisco/console/dist/contracts/liquid/hello_world/target/hello_world.abi
```

生成`hello_world.wasm`和`hello_world.abi`两个文件

### 5.2 WBC-Liquid合约生成Java文件

```shell
# 当前目录~/fisco/console
$ bash contract2java.sh liquid -b ./contracts/liquid/hello_world/hello_world.wasm -a ./contracts/liquid/hello_world/hello_world.abi -s ./contracts/liquid/hello_world/hello_world_sm.wasm -p org.com.fisco
# 通过命令./contract2java.sh -h可查看该脚本使用方法

$ ls contracts/sdk/java/org/com/fisco 
# 得到返回
HelloWorld.java
```

## 6. contract2java.sh 脚本解析

控制台提供一个专门的生成Java合约工具，方便开发者将Solidity和WBC-Liquid合约文件编译为Java合约文件。

当前合约生成工具支持Solidity的自动编译并生成Java文件、支持指定WBC-Liquid编译后的WASM文件以及ABI文件生成Java文件。

### 6.1 Solidity合约使用

```shell
bash contract2java.sh solidity -h
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -e,--enable-async-call           [Optional] Enable generate async
                                  interfaces for constant call, java file
                                  only compilable when java-sdk >= 3.3.0.
 -h,--help
 -l,--libraries <arg>             [Optional] Set library address
                                  information built into the solidity
                                  contract
                                  eg:
                                  --libraries lib1:lib1_address
                                  lib2:lib2_address
 -n,--no-analysis                 [Optional] NOT use evm static
                                  parallel-able analysis. It will not
                                  active DAG analysis, but will speedup
                                  compile speed.
 -o,--output <arg>                [Optional] The file path of the
                                  generated java code, default is
                                  contracts/sdk/java/
 -p,--package <arg>               [Optional] The package name of the
                                  generated java code, default is com
 -s,--sol <arg>                   [Optional] The solidity file path or the
                                  solidity directory path, default is
                                  contracts/solidity/
 -t,--transaction-version <arg>   [Optional] Specify transaction version
                                  interface, default is 0; If you want to
                                  use the latest transaction interface,
                                  please specify 1.
```

参数详细：

- `package`: 生成`Java`文件的包名。
- `sol`: (可选)`solidity`文件的路径，支持文件路径和目录路径两种方式，参数为目录时将目录下所有的`solidity`文件进行编译转换。默认目录为`contracts/solidity`。
- `output`: (可选)生成`Java`文件的目录，默认生成在`contracts/sdk/java`目录。
- `no-analysis`：（可选）跳过solidity编译的静态分析，可以有效缩减编译速度。静态分析可以分析合约的接口并行可行性，并将分析结果放到abi文件中。
- `enable-async-call` ：（可选）可生成具有异步call接口的Java文件，异步接口只有在Java-sdk版本>=3.3.0时使用。
- `transaction-version`：（可选）指定生成Java文件发交易的版本号，默认是0；
  - 当使用交易版本为0时，或者不使用该选项，可以兼容所有版本的节点。
  - 当使用交易版本为1时，可以支持交易带有value、gasLimit、gasPrice、EIP1559等字段，只能发给3.6.0及以上的节点中；
  - 当使用交易版本为2时，可以支持交易带有extension字段，只能发给3.7.0及以上的节点中。


### 6.2 WBC-Liquid合约使用

```shell
$ bash contract2java.sh liquid -h
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -a,--abi <arg>       [Required] The ABI file path of WBC-Liquid contract.
 -b,--bin <arg>       [Required] The binary file path of WBC-Liquid contract.
 -h,--help
 -o,--output <arg>    [Optional] The file path of the generated java code,
                      default is contracts/sdk/java/
 -p,--package <arg>   [Optional] The package name of the generated java
                      code, default is com
 -s,--sm-bin <arg>    [Required] The SM binary file path of WBC-Liquid
                      contract.
```

参数详细：

- `abi`：（必选）WBC-Liquid合约`ABI`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `bin`：（必选）WBC-Liquid合约`wasm bin`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `package`：（可选）生成`Java`文件的包名，默认为`org`。
- `sm-bin`：（必选）WBC-Liquid合约`wasm sm bin`文件的路径，在使用`cargo liquid build -g`命令之后生成在target文件夹中。

## 7. 使用``java-sdk-demo``给智能合约生成调用它的Java工具类

```shell
$ mkdir -p ~/fisco && cd ~/fisco
# 获取java-sdk代码
$ git clone https://github.com/FISCO-BCOS/java-sdk-demo

# 若因为网络问题导致长时间无法执行以上命令，请尝试以下命令：
$ git clone https://gitee.com/FISCO-BCOS/java-sdk-demo

$ cd java-sdk-demo
# 编译
$ ./gradlew clean build -x test
# 进入sdk-demo/dist目录，创建合约存放目录
$ cd dist && mkdir -p contracts/solidity
# 将需要转换为java代码的sol文件拷贝到~/fisco/java-sdk/dist/contracts/solidity路径下
# 转换sol, 其中${packageName}是生成的java代码包路径
# 生成的java代码位于 ~/fisco/java-sdk/dist/contracts/sdk/java目录下
java -cp "apps/*:lib/*:conf/" org.fisco.bcos.sdk.demo.codegen.DemoSolcToJava ${packageName}
```
