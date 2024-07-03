# Quick installation

Tag: "Install Node.js" "Command Line Tools"

----
## Environmental Requirements

- Node.js development environment
  - Node.js >= 8.10.0
  - npm >= 5.6.0

  If you have not deployed a Node.js environment, you can refer to the following deployment methods:
  - If you are using Linux or MacOS:

    Recommended [nvm](https://github.com/nvm-sh/nvm/blob/master/README.md)Fast deployment, using nvm and avoiding potential permissions issues that could cause Node.js SDK deployments to fail。Take Node.js 8 as an example. The deployment steps are as follows:

      ```bash
      # install nvm
      curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.33.2/install.sh | bash
      # load nvm configuration
      source ~/.$(basename $SHELL)rc
      # Installing Node.js 8
      nvm install 8
      # Using Node.js 8
      nvm use 8
      ```

  - If you use Windows:

    Please go to [Node.js official website](https://nodejs.org/en/download/)Download the installation package under Windows to install it。

- Basic development components
  - Python 2 (required for Windows, Linux and MacOS)
  - g++(Required for Linux and MacOS)
  -make (required for Linux and MacOS)
  - Git (required for Windows, Linux and MacOS)
  - Git bash (required for Windows only)
  - MSBuild build environment (required for Windows only)

- FISCO BCOS Node: Please refer to [FISCO BCOS Installation](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html#fisco-bcos)Build

## Deploying the Node.js SDK

### Pull source code

```bash
git clone https://github.com/FISCO-BCOS/nodejs-sdk.git
```

### Install dependencies using npm

*If a proxy is used in your network, first configure the proxy for npm:*

```bash
npm config set proxy <your proxy>
npm config set https-proxy <your proxy>
```

*If your network does not have access to the npm official image, use another image instead, such as Taobao:*

```bash
npm config set registry https://registry.npm.taobao.org
```

```bash
# During the deployment process, please ensure that you can access the external network to install third-party dependency packages
cd nodejs-sdk
npm install
npm run repoclean
npm run bootstrap
```

## Node.js CLI

Node.js SDK embedded CLI tool for users to easily interact with the blockchain from the command line。The CLI tool is developed on the basis of the API provided by the Node.js SDK, and the usage and result output are script-friendly, and it is also an example of how to call the Node.js API for secondary development。

**Fast chain building (optional)**

*If you already have a FISCO BCOS chain in your system, skip this section。*

```bash
# Get the development and deployment tool build _ chain.sh script
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.11.0/build_chain.sh && chmod u+x build_chain.sh
```

```eval_rst
.. note::
    -If the build _ chain.sh script cannot be downloaded for a long time due to network problems, please try 'curl-#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master-2.0/tools/build_chain.sh && chmod u+x build_chain.sh`
```

```bash
# Build a 4-node FISCO BCOS chain locally
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545 -i
# Start FISCO BCOS Chain
bash nodes/127.0.0.1/start_all.sh
```

**Configure Certificates and Channel Ports**

- Configure certificates

    Modify the configuration file. The certificate configuration is located in the 'authentication' configuration item in the 'packages / cli / conf / config.json' file。You need to modify the 'key', 'cert', and 'ca' configurations of the configuration item based on the path of the actual certificate file you are using, where 'key' is the path of the SDK private key file, 'cert' is the path of the SDK certificate file, and 'ca' is the path of the chain root certificate file(https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/build_chain.html)or [O & M Deployment Tool](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/enterprise_tools/index.html)Automatic generation, please refer to the documentation of the above tools for the specific generation method and file location。

- Configure Channel Port

    Modify the configuration file. The node IP and port configurations are located in the 'nodes' configuration item in the 'packages / cli / conf / config.json' file。You need to modify the 'ip' and 'port' configurations of the configuration item according to the actual configuration of the FISCO BCOS node you want to connect to, where 'ip' is the IP address of the connected node, and 'port' is the value of the 'channel _ listen _ port' configuration item in the config.ini file under the node directory。You can skip this step if you are using a quick hitch。

After the configuration is complete, you can start using the CLI tool. The CLI tool is located in 'packages / cli / cli.js'. All operations need to be performed in the 'packages / cli /' directory

```
cd packages/cli
```

**Enable auto completion (only for bash and zsh users, optional)**

To facilitate users to use the CLI tool, the CLI tool supports automatic completion in bash or zsh. This function needs to be manually enabled and run the command:

```bash
rcfile=~/.$(basename $SHELL)rc && ./cli.js completion >> $rcfile && source $rcfile
```

Autocomplete can be enabled。When using the CLI tool, pressing the 'Tab' key (which may need to be pressed twice depending on the system configuration) will pop up a list of candidate commands or parameters and auto-complete。

**Example**

Here are a few examples of use:

**View help for CLI tools**

```bash
./cli.js --help
```

**View the commands and corresponding functions that the CLI tool can invoke**

```bash
./cli.js list
```

*The inputs, outputs, and parameters in the following examples are for example only*

**View the connected FISCO BCOS node version**

```bash
./cli.js getClientVersion
```

The output is as follows:

```JSON
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "Build Time": "20190705 21:19:13",
    "Build Type": "Linux/g++/RelWithDebInfo",
    "Chain Id": "1",
    "FISCO-BCOS Version": "2.0.0",
    "Git Branch": "master-2.0",
    "Git Commit Hash": "d8605a73e30148cfb9b63807fb85fa211d365014",
    "Supported Version": "2.0.0"
  }
}
```

**Gets the current block height**

```bash
./cli.js getBlockNumber
```

The output is as follows:

```JSON
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": "0xfa"
}
```

**Deploy the HelloWorld contract that comes with the SDK**

```bash
./cli.js deploy HelloWorld
```

The output is as follows:

```JSON
{
  "contractAddress": "0x11b6d7495f2f04bdca45e9685ceadea4d4bd1832"
}
```

**To call the set interface of the HelloWorld contract, change the contract address to the actual address**

```bash
./cli.js call HelloWorld 0x11b6d7495f2f04bdca45e9685ceadea4d4bd1832 set vita
```

The output is as follows:

```JSON
{
  "transactionHash": "0xa71f136107389348d5a092a345aa6bc72770d98805a7dbab0dbf8fe569ff3f37",
  "status": "0x0"
}
```

**To call the get interface of the HelloWorld contract, change the contract address to the actual address**

```bash
./cli.js call HelloWorld 0xab09b29dd07e003776d22566ae5c078f2cb2279e get
```

The output is as follows:

```JSON
{
  "status": "0x0",
  "output": {
    "0": "vita"
  }
}
```

**CLI Help**

If you want to know how to use a command, you can use the following command:

```bash
./cli.js <command> ?
```

where command is a command name, using '?'as a parameter to get the command's usage tips, such as

```bash
./cli.js call ?
```

You will get the following output:

```bash
cli.js call <contractName> <contractAddress> <function> [parameters...]

Call a contract by a function and parameters

Location:
  contractName The name of a contract [string] [required]
  contractAddress 20 Bytes - The address of a contract [string] [required]
  function The function of a contract [string] [required]
  parameters       The parameters(splited by a space) of a function
                                                             [array] [default value]: []]

Options:
  --help Display Help Information [Boolean]
  --version Display Version Number [boolean]
```
