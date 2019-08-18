# 快速安装

## 环境要求
- 基本开发环境
  - Python 2 或 Python 3
  - g++
  - make

- Node.js开发环境
  - Node.js >= 8.10.0
  - npm >= 5.6.0
  
  如果您没有部署过Node.js环境，推荐使用[nvm](https://github.com/nvm-sh/nvm/blob/master/README.md)快速部署，使用nvm同时也能够避免潜在的导致Node.js SDK部署失败的权限问题。以部署Node.js 8为例，部署步骤如下：
  
    ```bash
    # 安装nvm
    curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.2/install.sh | bash
    # 加载nvm配置
    source ~/.$(basename $SHELL)rc
    # 安装Node.js 8
    nvm install 8
    # 使用Node.js 8
    nvm use 8
    ```

- FISCO BCOS节点：请参考[FISCO BCOS安装](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html#fisco-bcos)搭建

## 部署Node.js SDK

### 拉取源代码

```bash
git clone https://github.com/FISCO-BCOS/nodejs-sdk.git
```

### 使用npm安装依赖项

*如果您的网络中使用了代理，请先为npm配置代理：*

```bash
npm config set proxy <your proxy>
npm config set https-proxy <your proxy>
```

*如果您所在的网络不便访问npm官方镜像，请使用其他镜像代替，如淘宝：*

```bash
npm config set registry https://registry.npm.taobao.org
```

```bash
# 部署过程中请确保能够访问外网以能够安装第三方依赖包
cd nodejs-sdk
npm install
npm run repoclean
npm run bootstrap
```

## Node.js CLI

Node.js SDK内嵌CLI工具，供用户在命令行中方便地与区块链进行交互。CLI工具在Node.js SDK提供的API的基础上开发而成，使用方式与结果输出对脚本友好，同时也是一个展示如何调用Node.js API进行二次开发的范例。

**快速建链（可选）**

*若您的系统中已经搭建了FISCO BCOS链，请跳过本节。*

```bash
# 获取建链脚本
curl -LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/`curl -s https://api.github.com/repos/FISCO-BCOS/FISCO-BCOS/releases | grep "\"v2\.[0-9]\.[0-9]\"" | sort -u | tail -n 1 | cut -d \" -f 4`/build_chain.sh && chmod u+x build_chain.sh
# 在本地建一个4节点的FISCO BCOS链
bash build_chain.sh -l "127.0.0.1:4" -p 30300,20200,8545 -i
# 启动FISCO BCOS链
bash nodes/127.0.0.1/start_all.sh
```

**配置证书及Channel端口**

- 配置证书

    CLI工具需要使用证书来与FISCO BCOS节点通讯，证书文件默认放置于`packages/cli/conf/`目录。如果您使用了快速建链，则证书配置命令如下：

    ```bash
    cp nodes/127.0.0.1/sdk/* packages/cli/conf/
    ```

- 配置Channel端口

    Node.js SDK与节点建立通信时需要指定节点的Channel端口。端口配置位于`packages/cli/conf/config.json`文件的`nodes.port`配置项中，您需要根据您要连接FISCO BCOS节点的实际配置修改该配置项。如果您使用了快速搭链，可以跳过此步。

配置完成后，即可开始使用CLI工具，CLI工具位于`packages/cli/cli.js`，所有操作均需要在`packages/cli/`目录下执行，您需要先切换至该目录：

```
cd packages/cli
```

**开启自动补全（仅针对bash及zsh用户，可选）**

为方便用户使用CLI工具，CLI工具支持在bash或zsh中进行自动补全，此功能需要手动启用，执行命令：

```bash
rcfile=~/.$(basename $SHELL)rc && ./cli.js completion >> $rcfile && source $rcfile
```

便可启用自动补全。使用CLI工具时，按下`Tab`键（依据系统配置的不同，可能需要按两下）便可弹出候选命令或参数的列表并自动补全。

**示例**

以下给出几个使用示例：

**查看CLI工具的帮助**

```bash
./cli.js --help
```

**查看CLI工具能够调用的命令及对应的功能**

```bash
./cli.js list
```

*以下示例中的输入、输出及参数仅供举例*

**查看所连的FISCO BCOS节点版本**

```bash
./cli.js getClientVersion
```

输出如下：

```JSON
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "Build Time": "20190705 21:19:13",
    "Build Type": "Linux/g++/RelWithDebInfo",
    "Chain Id": "1",
    "FISCO-BCOS Version": "2.0.0",
    "Git Branch": "master",
    "Git Commit Hash": "d8605a73e30148cfb9b63807fb85fa211d365014",
    "Supported Version": "2.0.0"
  }
}
```

**获取当前的块高**

```bash
./cli.js getBlockNumber
```

输出如下：

```JSON
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": "0xfa"
}
```

**部署SDK自带的HelloWorld合约**

```bash
./cli.js deploy HelloWorld
```

输出如下：

```JSON
{
  "contractAddress": "0x11b6d7495f2f04bdca45e9685ceadea4d4bd1832"
}
```

**调用HelloWorld合约的set接口，请将合约地址改为实际地址**

```bash
./cli.js call HelloWorld 0x11b6d7495f2f04bdca45e9685ceadea4d4bd1832 set vita
```

输出如下：

```JSON
{
  "transactionHash": "0xa71f136107389348d5a092a345aa6bc72770d98805a7dbab0dbf8fe569ff3f37",
  "status": "0x0"
}
```

**调用HelloWorld合约的get接口，请将合约地址改为实际地址**

```bash
./cli.js call HelloWorld 0xab09b29dd07e003776d22566ae5c078f2cb2279e get
```

输出如下：

```JSON
{
  "status": "0x0",
  "output": {
    "0": "vita"
  }
}
```

**CLI帮助**

如果您想知道某一个命令该如何使用，可以使用如下的命令：

```bash
./cli.js <command> ?
```

其中command为一个命令名，使用`?`作为参数便可获取该命令的使用提示，如：

```bash
./cli.js call ?
```

会得到如下的输出：

```bash
cli.js call <contractName> <contractAddress> <function> [parameters...]

Call a contract by a function and parameters

位置：
  contractName     The name of a contract                        [字符串] [必需]
  contractAddress  20 Bytes - The address of a contract          [字符串] [必需]
  function         The function of a contract                    [字符串] [必需]
  parameters       The parameters(splited by a space) of a function
                                                             [数组] [默认值: []]

选项：
  --help     显示帮助信息                                                 [布尔]
  --version  显示版本号                                                   [布尔]
```
