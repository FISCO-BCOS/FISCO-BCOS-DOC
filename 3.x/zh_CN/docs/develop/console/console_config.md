# 控制台获取与配置

标签：``console`` ``控制台配置`` ``命令行交互工具``

---------

```eval_rst
.. important::
    - ``控制台`` 只支持FISCO BCOS 3.0+版本，基于 `Java SDK <../sdk/java_sdk/index.html>`_ 实现。
    - 可通过命令 ``./start.sh --version`` 查看当前控制台版本
```

[控制台](https://github.com/FISCO-BCOS/console)是FISCO BCOS 3.0重要的交互式客户端工具，它通过[Java SDK](./sdk/java_sdk/index.md)与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity和webankblockchain-liquid合约文件(以下简称wbc-liquid）编译后的WASM文件转换为Java合约文件。

wbc-liquid编译环境搭建请参考：[wbc-liquid的环境配置](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/quickstart/prerequisite.html)。

## 1. 控制台配置与运行

```eval_rst
.. important::
    前置条件：搭建FISCO BCOS区块链，请参考 `搭建第一个区块链网络 <../installation.html>`_
    建链工具参考：单群组区块链（Air版本）` <../../tutorial/air/build_chain.html>`_ 或 `多群组区块链（Pro版本） <../../tutorial/pro/build_chain.html>`_。
```

### 1.1 获取控制台

```shell
cd ~ && mkdir -p fisco && cd fisco
# 获取控制台
curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.0.0-rc1/download_console.sh && bash download_console.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh`
```

目录结构如下：

```shell
│── apps # 控制台jar包目录
│   └── console.jar
├── lib # 相关依赖的jar包目录
├── conf
│   ├── clog.ini    # c sdk日志配置文件
│   ├── config-example.toml # 配置文件
│   └── log4j.properties # 日志配置文件
├── contracts # 合约所在目录
│   ├── console # 控制台部署合约时编译的合约abi, bin，java文件目录
│   ├── sdk     # sol2java.sh脚本编译的合约abi, bin，java文件目录
│   ├── liquid  # wbc-liquid 合约存放目录
│   └── solidity    # solidity合约存放目录
│       └── HelloWorld.sol # 普通合约：HelloWorld合约，可部署和调用
│       └── KVTableTest.sol # 使用KV存储接口的合约：KVTableTest合约，可部署和调用
│       └── KVTable.sol # 提供KV存储操作的接口合约    
│-- start.sh # 控制台启动脚本
│-- get_account.sh # 账户生成脚本
│-- get_gm_account.sh # 账户生成脚本，国密版
│-- contract2java.sh # Solidity/wbc-liquid合约文件编译为java合约文件的开发工具脚本
```

### 1.2 配置控制台

- 区块链节点和证书的配置：
  - 将节点sdk目录下的`ca.crt`、`sdk.crt`和`sdk.key`文件拷贝到`conf`目录下。
  - 将`conf`目录下的`config-example.toml`文件重命名为`config.toml`文件。配置`config.toml`文件，其中添加注释的内容根据区块链节点配置做相应修改。

配置示例文件如下：

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path  
useSMCrypto = "false"                       # RPC SM crypto type

# The following configurations take the certPath by default if commented
# caCert = "conf/ca.crt"                    # CA cert file path
# sslCert = "conf/sdk.crt"                  # SSL cert file path
# sslKey = "conf/sdk.key"                   # SSL key file path

# The following configurations take the sm certPath by default if commented
# caCert = "conf/sm_ca.crt"                    # SM CA cert file path
# sslCert = "conf/sm_sdk.crt"                  # SM SSL cert file path
# sslKey = "conf/sm_sdk.key"                    # SM SSL key file path
# enSslCert = "conf/sm_ensdk.crt"               # SM encryption cert file path
# enSslKey = "conf/sm_ensdk.key"                # SM ssl cert file path

[network]
messageTimeout = "10000"
defaultGroup="group"                            # Console default group to connect
peers=["127.0.0.1:20200", "127.0.0.1:20201"]    # The peer list to connect

[account]
authCheck = "false"
keyStoreDir = "account"         # The directory to load/store the account file, default is "account"
# accountFilePath = ""          # The account file path (default load from the path specified by the keyStoreDir)
accountFileFormat = "pem"       # The storage format of account file (Default is "pem", "p12" as an option)

# accountAddress = ""           # The transactions sending account address
                                # Default is a randomly generated account
                                # The randomly generated account is stored in the path specified by the keyStoreDir

# password = ""                 # The password used to load the account file

[threadPool]
# threadPoolSize = "16"         # The size of the thread pool to process message callback
                                            # Default is the number of cpu cores
```

配置项详细说明[参考这里](../sdk/java_sdk/config.md)。

```eval_rst
.. important::

    控制台说明
    
    - 当控制台配置文件在一个群组内配置多个节点连接时，由于群组内的某些节点在操作过程中可能退出群组，因此控制台轮询节点查询时，其返回信息可能不一致，属于正常现象。建议使用控制台时，配置一个节点或者保证配置的节点始终在群组中，这样在同步时间内查询的群组内信息保持一致。
```

#### 1.2.1 Java合约生成工具

控制台提供一个专门的生成Java合约工具，方便开发者将Solidity和wbc-liquid合约文件编译为Java合约文件。

当前合约生成工具支持Solidity的自动编译并生成Java文件、支持指定wbc-liquid编译后的WASM文件以及ABI文件生成Java文件。

**Solidity合约使用**

```shell
$ bash contract2java.sh solidity -h 
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -h,--help
 -l,--libraries <arg>   [Optional] Set library address information built
                        into the solidity contract
                        eg:
                        --libraries lib1:lib1_address lib2:lib2_address
 -o,--output <arg>      [Optional] The file path of the generated java
                        code, default is contracts/sdk/java/
 -p,--package <arg>     [Optional] The package name of the generated java
                        code, default is com
 -s,--sol <arg>         [Optional] The solidity file path or the solidity
                        directory path, default is contracts/solidity/
```

参数详细：

- `package`: 生成`Java`文件的包名。
- `sol`: (可选)`solidity`文件的路径，支持文件路径和目录路径两种方式，参数为目录时将目录下所有的`solidity`文件进行编译转换。默认目录为`contracts/solidity`。
- `output`: (可选)生成`Java`文件的目录，默认生成在`contracts/sdk/java`目录。 

**wbc-liquid合约使用**

```shell
$ bash contract2java.sh liquid -h
usage: contract2java.sh <solidity|liquid> [OPTIONS...]
 -a,--abi <arg>       [Required] The ABI file path of wbc-liquid contract.
 -b,--bin <arg>       [Required] The binary file path of wbc-liquid contract.
 -h,--help
 -o,--output <arg>    [Optional] The file path of the generated java code,
                      default is contracts/sdk/java/
 -p,--package <arg>   [Optional] The package name of the generated java
                      code, default is com
 -s,--sm-bin <arg>    [Required] The SM binary file path of wbc-liquid
                      contract.
```

参数详细：

- `abi `：（必选）wbc-liquid合约`ABI`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `bin`：（必选）wbc-liquid合约`wasm bin`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `package`：（可选）生成`Java`文件的包名，默认为`org`。
- `sm-bin`：（必选）wbc-liquid合约`wasm sm bin`文件的路径，在使用`cargo liquid build -g`命令之后生成在target文件夹中。

**使用**

```shell
$ cd ~/fisco/console

# 生成Solidity合约的Java代码
$ bash contract2java.sh solidity -p org.com.fisco

# 生成wbc-liquid合约的Java代码
$ bash contract2java.sh liquid -p org.com.fisco -b ./contracts/liquid/asset_test/asset_test.wasm -a ./contracts/liquid/asset_test/asset_test.abi -s ./contracts/liquid/asset_test/asset_test_sm.wasm 
```

运行成功之后，将会在`console/contracts/sdk`目录生成java、abi和bin目录，如下所示。

```shell
|-- abi # 编译生成的abi目录，存放solidity合约编译的abi文件
|   |-- HelloWorld.abi
|   |-- KVTable.abi
|   |-- KVTableTest.abi
|-- bin # 编译生成的bin目录，存放solidity合约编译的bin文件
|   |-- HelloWorld.bin
|   |-- KVTable.bin
|   |-- KVTableTest.bin
|-- java  # 存放编译的包路径及Java合约文件
|   |-- org
|       |-- com
|           |-- fisco
|               |-- HelloWorld.java # Solidity编译的HelloWorld Java文件
|               |-- KVTable.java    # Solidity编译的KV存储接口合约 Java文件
|               |-- KVTableTest.java  # Solidity编译的KVTableTest Java文件
|               |-- AssetTest.java  # wbc-liquid生成的AssetTest文件
```

Java目录下生成了`org/com/fisco/`包路径目录。包路径目录下将会生成Java合约文件`HelloWorld.java`、`KVTableTest.java`、`KVTable.java`和`AssetTest.java`。其中`HelloWorld.java`、`KVTableTest.java`和`AssetTest.java`是Java应用所需要的Java合约文件。

### 1.3. 启动控制台

在节点正在运行的情况下，启动控制台：

```shell
$ ./start.sh
# 输出下述信息表明启动成功
=====================================================================================
Welcome to FISCO BCOS console(3.0.0-rc1)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$

=====================================================================================
```

### 1.4 启动脚本说明

#### 1.4.1 查看当前控制台版本：

```shell
./start.sh --version
console version: 3.0.0-rc1
```

#### 1.4.2 账户使用方式

##### 1.4.2.1 控制台加载私钥

控制台提供账户生成脚本get_account.sh(脚本用法请参考[账户管理文档](../account.md)，生成的的账户文件在accounts目录下，控制台加载的账户文件必须放置在该目录下。
控制台启动方式有如下几种：

```shell
./start.sh
./start.sh group
./start.sh group -pem pemName
./start.sh group -p12 p12Name
```

##### 1.4.2.2 默认启动

使用控制台配置文件指定的默认群组号启动。

```shell
./start.sh
```

**注意**: 控制台启动未指定私钥账户时，会尝试从`account`目录下加载一个可用的私钥账户用于发送交易，加载失败则会创建一个新的`PEM`格式的账户文件，将其保存在`account`目录下。

##### 1.4.2.3 指定群组名启动

使用命令行指定的群组名启动。

```shell
./start.sh group
```

##### 1.4.2.4 使用PEM格式私钥文件启动

- 使用指定的pem文件的账户启动，输入参数：群组号、-pem、pem文件路径

```shell
./start.sh group -pem accounts/0xebb824a1122e587b17701ed2e512d8638dfb9c88.pem
```

##### 1.4.2.5 使用PKCS12格式私钥文件启动

- 使用指定的p12文件的账户，需要输入密码，输入参数：群组号、-p12、p12文件路径

```shell
./start.sh group -p12 accounts/0x5ef4df1b156bc9f077ee992a283c2dbb0bf045c0.p12
Enter Export Password:
```

**注意：**
控制台启动时加载p12文件出现下面报错：

```shell
exception unwrapping private key - java.security.InvalidKeyException: Illegal key size
```

可能是Java版本的原因，参考解决方案：[https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size](https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size)

### 1.5 控制台命令结构

控制台命令由两部分组成，即指令和指令相关的参数：

- **指令**: 指令是执行的操作命令，包括查询区块链相关信息，部署合约和调用合约的指令等，其中部分指令调用JSON-RPC接口，因此与JSON-RPC接口同名。
  **使用提示： 指令可以使用tab键补全，并且支持按上下键显示历史输入指令。**

- **指令相关的参数**: 指令调用接口需要的参数，指令与参数以及参数与参数之间均用空格分隔，与JSON-RPC接口同名命令的输入参数和获取信息字段的详细解释参考[JSON-RPC API](./api.md)。

### 1.6 控制台常用命令

#### 合约相关命令

- 利用**CNS**部署和调用合约(**推荐**)
  - 部署合约: [deployByCNS](./console.html#deploybycns)
  - 调用合约: [callByCNS](./console.html#callbycns)
  - 查询CNS部署合约信息: [queryCNS](./console.html#querycns)
- 普通部署和调用合约
  - 部署合约: [deploy](./console.html#deploy)
  - 调用合约: [call](./console.html#call)

#### 其他命令

- 查询区块高度：[getBlockNumber](./console.html#getblocknumber)
- 查询共识节点列表：[getSealerList](./console.html#getsealerlist)
- 查询交易回执信息: [getTransactionReceipt](./console.html#gettransactionreceipt)
- 切换群组: [switch](./console.html#switch)

### 快捷键

- `Ctrl+A`：光标移动到行首
- `Ctrl+D`：退出控制台
- `Ctrl+E`：光标移动到行尾
- `Ctrl+R`：搜索输入的历史命令
- &uarr;：向前浏览历史命令
- &darr;：向后浏览历史命令

### 控制台响应

当发起一个控制台命令时，控制台会获取命令执行的结果，并且在终端展示执行结果，执行结果分为2类：

- **正确结果:** 命令返回正确的执行结果，以字符串或是json的形式返回。
- **错误结果:** 命令返回错误的执行结果，以字符串或是json的形式返回。
  - 控制台的命令调用JSON-RPC接口时，错误码[参考这里](../api.html#rpc)。
  - 控制台的命令调用Precompiled Service接口时，错误码[参考这里](../api.html#precompiled-service-api)。【FIXME: 链接有误】

