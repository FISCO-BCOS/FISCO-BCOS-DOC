# 合约开发样例

标签：``go-sdk`` ``合约开发``

----

## 非国密样例

本开发样例使用标准单群组四节点区块链网络结构，搭建请参考：[安装](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/quick_start/air_installation.html)。

在利用SDK进行项目开发时，对智能合约进行操作需要利用go-sdk的`abigen`工具将Solidity智能合约转换为`Go`文件代码，会自动生成合约中事件监听的接口。整体上主要包含六个流程：

- 准备需要编译的智能合约
- 配置好相应版本的solc编译器
- 构建go-sdk的合约编译工具abigen
- 编译生成go文件
- 准备建立ssl连接需要的证书
- 使用生成的go文件进行合约部署、调用

### HelloWorld样例

#### 准备HelloWorld.sol合约文件

```bash
# 该指令在go-sdk目录中执行
mkdir helloworld && cd helloworld
```

在 go-sdk 主目录中新建 helloworld 文件夹，在该文件夹中创建 HelloWorld.sol 合约。该合约提供两个接口，分别是get()和set()，用于获取/设置合约变量name。合约内容如下

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity >=0.6.10 <0.8.20;

contract HelloWorld {
    string value;
    event setValue(string v, address indexed from, address indexed to, int256 value);
    int public version;

    constructor(string memory initValue) {
        value = initValue;
        version = 0;
    }

    function get() public view returns (string memory) {
        return value;
    }

    function set(string calldata v) public returns (string memory) {
        string memory old = value;
        value = v;
        version = version + 1;
        emit setValue(v, tx.origin, msg.sender, version);
        return old;
    }
}

```

#### 安装solc编译器

该编译器用于将 sol 合约文件编译成 abi 和 bin 文件，目前FISCO BCOS提供的`solc`编译器有0.8.11/0.6.10，每个版本有国密和非国密两种。

```bash
# 该指令在helloworld文件夹中执行
bash ../tools/download_solc.sh -v 0.8.11
```

#### 构建go-sdk的代码生成工具abigen

该工具用于将 abi 和 bin 文件转换为 go 文件

```bash
# 该指令在helloworld文件夹中执行，编译生成abigen工具
go build ../cmd/abigen
```

#### 编译生成go文件

先利用solc编译合约文件HelloWorld.sol，生成abi和bin文件

```bash
# 该指令在helloworld文件夹中执行
./solc-0.8.11 --bin --abi -o ./ ./HelloWorld.sol
```

helloworld目录下会生成HelloWorld.bin和HelloWorld.abi。此时利用abigen工具将HelloWorld.bin和HelloWorld.abi转换成HelloWorld.go：

```bash
# 该指令在helloworld文件夹中执行
./abigen --bin ./HelloWorld.bin --abi ./HelloWorld.abi --pkg helloworld --type HelloWorld --out ./HelloWorld.go
```

最后helloworld文件夹下面存在以下6个文件：

```bash
HelloWorld.abi、HelloWorld.bin、HelloWorld.go、HelloWorld.sol、solc-0.8.11、abigen
```

#### 准备建立ssl连接需要的证书

使用build_chain.sh脚本搭建区块链时会在./nodes/127.0.0.1/sdk文件夹中生成sdk证书、私钥以及ca证书，需要将这三个文件拷贝到参数指定的位置，例如下文中和可执行文件同一目录下。

#### 部署合约

在helloworld文件夹中创建cmd文件夹，在cmd文件夹中创建main.go文件，main.go的内容如下，在该文件中调用HelloWorld.go部署智能合约

```go
package main

import (
    "context"
    "encoding/hex"
    "fmt"
    "log"

    "github.com/FISCO-BCOS/go-sdk/client"
    "github.com/FISCO-BCOS/go-sdk/core/types"
    "github.com/FISCO-BCOS/go-sdk/hello"
)

func main() {
    privateKey, _ := hex.DecodeString("145e247e170ba3afd6ae97e88f00dbc976c2345d511b0f6713355d19d8b80b58")
    config := &client.Config{IsSMCrypto: false, GroupID: "group0",
        PrivateKey: privateKey, Host: "127.0.0.1", Port: 20200, TLSCaFile: "./ca.crt", TLSKeyFile: "./sdk.key", TLSCertFile: "./sdk.crt"}
    client, err := client.DialContext(context.Background(), config)
    if err != nil {
        log.Fatal(err)
    }
    input := "HelloWorld deployment 1.0"
    fmt.Println("=================DeployHelloWorld===============")
    address, receipt, instance, err := helloworld.DeployHelloWorld(client.GetTransactOpts(), client, input)
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println("contract address: ", address.Hex()) // the address should be saved, will use in next example
    fmt.Println("transaction hash: ", receipt.TransactionHash)

    // load the contract
    // contractAddress := common.HexToAddress("contract address in hex String")
    // instance, err := helloworld.NewStore(contractAddress, client)
    // if err != nil {
    //     log.Fatal(err)
    // }

    fmt.Println("================================")
    helloSession := &helloworld.HelloWorldSession{Contract: instance, CallOpts: *client.GetCallOpts(), TransactOpts: *client.GetTransactOpts()}

    version, err := helloSession.Version()
    if err != nil {
        log.Fatal(err)
    }

    fmt.Println("version :", version) // "HelloWorld deployment 1.0"

    ret, err := helloSession.Get() //调用合约Get方法
    if err != nil {
        fmt.Printf("helloworld.Get() failed: %v", err)
        return
    }
    done := make(chan bool)
    // 监听合约set事件
    _, err = helloSession.WatchAllSetValue(nil, func(ret int, logs []types.Log) {
        fmt.Printf("WatchAllSetValue receive statud: %d, logs: %v\n", ret, logs)
        setValue, err := helloSession.ParseSetValue(logs[0])
        if err != nil {
            fmt.Printf("helloworld.WatchAllSetValue() failed: %v", err)
            panic("WatchAllSetValue helloworld.WatchAllSetValue() failed")
        }
        fmt.Printf("receive setValue: %+v\n", *setValue)
        done <- true
    })
    if err != nil {
        fmt.Printf("helloworld.WatchAllSetValue() failed: %v", err)
        return
    }
    fmt.Printf("Get: %s\n", ret)
    fmt.Println("================================")

    oldValue, _, receipt, err := helloSession.Set("hello fisco")
    fmt.Println("old value is: ", oldValue)
    if err != nil {
        log.Fatal(err)
    }

    fmt.Printf("transaction hash of receipt: %s\n", receipt.GetTransactionHash())

    ret, err = helloSession.Get()
    if err != nil {
        fmt.Printf("helloworld.Get() failed: %v", err)
        return
    }
    fmt.Printf("Get: %s\n", ret)
    <-done
}

```

构建并执行。

```bash
# 该指令在go-sdk目录中执行
go run helloworld/cmd/main.go
```

```eval_rst
.. note::

    - 合约地址需要手动保存，调用合约接口时使用
    - 如果c-sdk的动态库放在自定义目录，需要`go run -ldflags="-r 自定义目录的路径"`

```

### 异步部署、调用HelloWorld合约

```golang
package main

import (
    "fmt"
    "log"

    "github.com/FISCO-BCOS/go-sdk/client"
    "github.com/FISCO-BCOS/go-sdk/conf"
    "github.com/FISCO-BCOS/go-sdk/helloworld"
    "github.com/ethereum/go-ethereum/common"
    "github.com/FISCO-BCOS/go-sdk/core/types"
)

func main() {
    configs, err := conf.ParseConfigFile("config.toml")
    if err != nil {
        log.Fatalf("ParseConfigFile failed, err: %v", err)
    }
    client, err := client.Dial(&configs[0])
    if err != nil {
        fmt.Printf("Dial Client failed, err:%v", err)
        return
    }
    var contractAddress common.Address
    var channel = make(chan int, 0)
    tx, err := helloworld.AsyncDeployHelloWorld(client.GetTransactOpts(), func(receipt *types.Receipt, err error) {
        if err != nil {
            fmt.Printf("%v\n", err)
            return
        }
        fmt.Println("contract address: ", receipt.ContractAddress.Hex()) // the address should be saved
        contractAddress = receipt.ContractAddress
        channel <- 0
    }, client)
    fmt.Println("transaction hash: ", tx.Hash().Hex())
    <-channel
    instance, err := helloworld.NewHelloWorld(contractAddress, client)
    if err != nil {
        log.Fatal(err)
    }
    if err != nil {
        fmt.Printf("Deploy failed, err:%v", err)
        return
    }
    hello := &helloworld.HelloWorldSession{Contract: instance, CallOpts: *client.GetCallOpts(), TransactOpts: *client.GetTransactOpts()}
    ret, err := helloworld.Get()
    if err != nil {
        fmt.Printf("helloworld.Get() failed: %v", err)
        return
    }
    fmt.Printf("Get: %s\n", ret)
    tx, err = helloworld.AsyncSet(func(receipt *types.Receipt, err error) {
        if err != nil {
            fmt.Printf("helloworld.AsyncSet failed: %v\n", err)
            return
        }
        if receipt.Status != 0 {
            fmt.Printf("helloworld.AsyncSet failed: %v\n", receipt.GetErrorMessage())
        }
        channel <- 0
    }, "fisco")
    <-channel
    ret, err = helloworld.Get()
    if err != nil {
        fmt.Printf("helloworld.Get() failed: %v", err)
        return
    }
    fmt.Printf("Get: %s\n", ret)
}

```

## 国密样例

使用国密特性的开发流程和非国密大致相同，不同点在于以下几部分：

- 搭建的 FISCO BCOS 区块链网络需要开启国密特性
- 需要将非国密私钥替换为国密私钥
- 需要准备国密的TLS证书和私钥
- 安装 solc 编译器时需要添加 **-g** 选项，替换为国密版本
- 使用 abigen 工具将 bin 和 abi 转换为 go 文件时，需要添加参数 **--smcrypto=true**

### HelloWorld样例

#### 准备HelloWorld.sol合约文件

在 go-sdk 主目录中新建 helloworld 文件夹，在该文件夹中创建 HelloWorld.sol 合约。该合约提供两个接口，分别是get()和set()，用于获取/设置合约变量name。合约内容如下

```solidity
pragma solidity >=0.6.10 <0.8.20;

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

#### 安装国密solc编译器

该编译器用于将 sol 合约文件编译成 abi 和 bin 文件

```bash
# 该指令在helloworld文件夹中执行
bash ../tools/download_solc.sh -v 0.8.11 -g
```

#### 构建go-sdk的代码生成工具abigen

该工具用于将 abi 和 bin 文件转换为 go 文件

```bash
# 该指令在helloworld文件夹中执行，编译生成abigen工具
go build ../cmd/abigen
```

#### 编译生成go文件

先利用solc编译合约文件HelloWorld.sol，生成abi和bin文件

```bash
# 该指令在helloworld文件夹中执行
./solc-0.8.11-gm --bin --abi -o ./ ./HelloWorld.sol
```

helloworld目录下会生成HelloWorld.bin和HelloWorld.abi。此时利用abigen工具将HelloWorld.bin和HelloWorld.abi转换成HelloWorld.go：

```bash
# 该指令在helloworld文件夹中执行
./abigen --bin ./HelloWorld.bin --abi ./HelloWorld.abi --pkg helloworld --type HelloWorld --out ./HelloWorld.go --smcrypto=true
```

- 接下来的步骤同非国密，不占用多余篇幅
