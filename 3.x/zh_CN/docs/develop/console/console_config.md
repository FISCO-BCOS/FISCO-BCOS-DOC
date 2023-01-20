# 控制台配置

标签：``console`` ``控制台配置`` ``命令行交互工具``

---------

```eval_rst
.. important::
    - ``控制台`` 只支持FISCO BCOS 3.x版本，基于 `Java SDK <../sdk/java_sdk/index.html>`_ 实现。
    - 可通过命令 ``./start.sh --version`` 查看当前控制台版本
```

[控制台](https://github.com/FISCO-BCOS/console)是FISCO BCOS 3.x重要的交互式客户端工具，它通过[Java SDK](../sdk/java_sdk/index.md)与区块链节点建立连接，实现对区块链节点数据的读写访问请求。控制台拥有丰富的命令，包括查询区块链状态、管理区块链节点、部署并调用合约等。此外，控制台提供一个合约编译工具，用户可以方便快捷的将Solidity和webankblockchain-liquid合约文件(以下简称wbc-liquid）编译后的WASM文件转换为Java合约文件。

wbc-liquid编译环境搭建请参考：[wbc-liquid的环境配置](https://liquid-doc.readthedocs.io/zh_CN/latest/docs/quickstart/prerequisite.html)。

## 控制台配置与运行

```eval_rst
.. important::
    前置条件：搭建FISCO BCOS区块链，请参考 `搭建第一个区块链网络 <../../quick_start/air_installation.html>`_
    建链工具参考:
     - `Air版本FISCO BCOS建链脚本build_chain <../../tutorial/air/build_chain.html>`_
     - `Pro版本FISCO BCOS建链工具BcosBuilder <../../tutorial/pro/pro_builder.html>`_
```

### 1. 获取控制台

```shell
cd ~ && mkdir -p fisco && cd fisco
# 获取控制台下载脚本
curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v3.2.0/download_console.sh 
# 执行下载脚本
bash download_console.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh`
```

#### 1.1 获取其他Solidity版本的控制台

由于目前控制台默认的Solc编译器版本是0.8.11，为了更加方便地使用其他版本的Solidity合约，可以通过以下命令下载(目前支持0.4.25，0.5.2，0.6.10，0.8.11)：

```shell
# 下载0.4.25版本的控制台
bash download_console.sh -v 0.4 # 以此类推，支持指定版本(0.4,0.5,0.6,0.8)
```

#### 1.2 已经配置好控制台，手动切换Solidity版本

当用户已经配置好控制台之后，有需求切换到其他版本的Solidity版本，可以使用 `download_console.sh` 脚本下载对应版本的SolcJ jar包，手动替换Solidity版本，可以通过以下命令下载（目前支持0.4.25，0.5.2，0.6.10），默认Solidity版本为0.8.11：

```shell
# 下载0.6.10版本的solcJ jar包
bash download_console.sh -s 0.6 # 以此类推，支持指定版本(0.4,0.5,0.6)
# 在执行脚本的所在目录下，会生成两个新文件，其他版本类似
ls solcJ*
solcJ-0.6.10.1.jar solcJ-0.6.tar.gz
# 将 solcJ-0.6.10.1.jar 手动替换控制台所在文件夹的lib目录下的solcJ文件。这里举例控制台目录就在本目录下：
mv ./console/lib/solcJ-0.8.11.1.jar . && cp solcJ-0.6.10.1.jar ./console/lib/
# 至此，控制台的默认Solidity版本就切换到0.6.10版本
```

配置完毕的控制台目录结构如下：

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
│   ├── liquid  # WBC-Liquid 合约存放目录
│   └── solidity    # solidity合约存放目录
│       └── HelloWorld.sol # 普通合约：HelloWorld合约，可部署和调用
│       └── KVTableTest.sol # 使用KV存储接口的合约：KVTableTest合约，可部署和调用
│       └── KVTable.sol # 提供KV存储操作的接口合约    
│-- start.sh # 控制台启动脚本
│-- get_account.sh # 账户生成脚本
│-- get_gm_account.sh # 账户生成脚本，国密版
│-- contract2java.sh # Solidity/WBC-Liquid合约文件编译为java合约文件的开发工具脚本
```

### 2. 配置控制台

- 区块链节点和证书的配置：
  - 将节点sdk目录下的所有文件拷贝到`conf`目录下。
  - 将`conf`目录下的`config-example.toml`文件重命名为`config.toml`文件。配置`config.toml`文件，其中添加注释的内容根据区块链节点配置做相应修改。

配置示例文件如下：

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path
disableSsl = "false"                        # Communication with nodes without SSL
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
defaultGroup="group0"                            # Console default group to connect
peers=["127.0.0.1:20200", "127.0.0.1:20201"]    # The peer list to connect

[account]
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

### 3. 启动控制台

在节点正在运行的情况下，启动控制台：

```shell
$ ./start.sh
# 输出下述信息表明启动成功
=============================================================================================
Welcome to FISCO BCOS console(3.0.0)!
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

=============================================================================================
```

### 4. 启动脚本说明

#### 4.1 查看当前控制台版本

```shell
./start.sh --version
console version: 3.0.0
```

#### 4.2 账户使用方式

##### 4.2.1 控制台加载私钥

控制台提供账户生成脚本get_account.sh(脚本用法请参考[账户管理文档](../account.md)，生成的的账户文件在accounts目录下，控制台加载的账户文件必须放置在该目录下。
控制台启动方式有如下几种：

```shell
./start.sh
./start.sh group0
./start.sh group0 -pem pemName
./start.sh group0 -p12 p12Name
```

##### 4.2.2 默认启动

使用控制台配置文件指定的默认群组号启动。

```shell
./start.sh
```

**注意**: 控制台启动未指定私钥账户时，会尝试从`account`目录下加载一个可用的私钥账户用于发送交易，加载失败则会创建一个新的`PEM`格式的账户文件，将其保存在`account`目录下。

##### 4.2.3 指定群组名启动

使用命令行指定的群组名启动。

```shell
./start.sh group0
```

##### 4.2.4 使用PEM格式私钥文件启动

- 使用指定的pem文件的账户启动，输入参数：群组号、-pem、pem文件路径

```shell
./start.sh group0 -pem account/ecdsa/0x2dbb332a844e0e076f97c90ff5078ea7dd2de910.pem
```

##### 4.2.5 使用PKCS12格式私钥文件启动

- 使用指定的p12文件的账户，需要输入密码，输入参数：群组号、-p12、p12文件路径

```shell
./start.sh group0 -p12 account/ecdsa/0x2dbb332a844e0e076f97c90ff5078ea7dd2de910.pem
Enter Export Password:
```

**注意：**
控制台启动时加载p12文件出现下面报错：

```shell
exception unwrapping private key - java.security.InvalidKeyException: Illegal key size
```

可能是Java版本的原因，参考解决方案：[https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size](https://stackoverflow.com/questions/3862800/invalidkeyexception-illegal-key-size)

## Java合约生成工具

控制台提供一个专门的生成Java合约工具，方便开发者将Solidity和wbc-liquid合约文件编译为Java合约文件。

当前合约生成工具支持Solidity的自动编译并生成Java文件、支持指定wbc-liquid编译后的WASM文件以及ABI文件生成Java文件。

**注意：** Solidity合约生成工具与Solc版本号直接相关，对应的Solidity版本的合约请使用具有对应Solc的控制台。请参考上文中 1.1获取其他Solidity版本的控制台。

### Solidity合约使用

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

### wbc-liquid合约使用

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

- `abi `：（必选）WBC-Liquid合约`ABI`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `bin`：（必选）WBC-Liquid合约`wasm bin`文件的路径，在使用`cargo liquid build`命令之后生成在target文件夹中。
- `package`：（可选）生成`Java`文件的包名，默认为`org`。
- `sm-bin`：（必选）WBC-Liquid合约`wasm sm bin`文件的路径，在使用`cargo liquid build -g`命令之后生成在target文件夹中。

#### 使用

```shell
$ cd ~/fisco/console

# 生成Solidity合约的Java代码
$ bash contract2java.sh solidity -p org.com.fisco

# 生成WBC-Liquid合约的Java代码
$ bash contract2java.sh liquid -p org.com.fisco -b ./contracts/liquid/asset/asset.wasm -a ./contracts/liquid/asset/asset.abi -s ./contracts/liquid/asset/asset_gm.wasm 
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
|               |-- Asset.java  # wbc-liquid生成的Asset文件
```

Java目录下生成了`org/com/fisco/`包路径目录。包路径目录下将会生成Java合约文件`HelloWorld.java`、`KVTableTest.java`、`KVTable.java`和`Asset.java`。其中`HelloWorld.java`、`KVTableTest.java`和`Asset.java`是Java应用所需要的Java合约文件。
