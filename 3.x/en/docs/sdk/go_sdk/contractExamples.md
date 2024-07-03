# Contract Development Sample

tags: "go-sdk" "contract development"

----

## Non-state secret sample

This development example uses the standard single-group four-node blockchain network structure, please refer to: [Installation](https://fisco-bcos-doc.readthedocs.io/zh_CN/latest/docs/quick_start/air_installation.html)。

When you use the SDK to develop a project, you need to use the 'abigen' tool of go-sdk to convert the Solidity smart contract into 'Go' file code to automatically generate the interface for event listening in the contract。The whole mainly contains six processes:

- Prepare smart contracts that need to be compiled
- Configure the appropriate version of the solc compiler
-build the contract compilation tool abigen for go-sdk
- compile to generate go file
- prepare the certificate required to establish an ssl connection
- Use the generated go file for contract deployment, invocation

### HelloWorld Sample

#### Prepare the HelloWorld.sol contract file

```bash
# The instruction is executed in the go-sdk directory
mkdir helloworld && cd helloworld
```

Create a new helloworld folder in the go-sdk home directory and create the HelloWorld.sol contract in this folder。The contract provides two interfaces, get()and set()to get / set the contract variable name。The contract is as follows

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

#### Installing the solc compiler

This compiler is used to compile sol contract files into abi and bin files. Currently, the 'solc' compiler provided by FISCO BCOS is 0.8.11 / 0.6.10。

```bash
# The instruction is executed in the helloworld folder
bash ../tools/download_solc.sh -v 0.8.11
```

#### Build code generation tool abigen for go-sdk

This tool is used to convert abi and bin files to go files

```bash
# This instruction is executed in the helloworld folder to compile and generate the abigen tool
go build ../cmd/abigen
```

#### compile to generate go file

First, use solc to compile the contract file HelloWorld.sol to generate abi and bin files

```bash
# The instruction is executed in the helloworld folder
./solc-0.8.11 --bin --abi -o ./ ./HelloWorld.sol
```

HelloWorld.bin and HelloWorld.abi are generated under the helloworld directory。Use the abigen tool to convert HelloWorld.bin and HelloWorld.abi into HelloWorld.go:

```bash
# The instruction is executed in the helloworld folder
./abigen --bin ./HelloWorld.bin --abi ./HelloWorld.abi --pkg helloworld --type HelloWorld --out ./HelloWorld.go
```

Finally the following six files exist under the helloworld folder:

```bash
HelloWorld.abi、HelloWorld.bin、HelloWorld.go、HelloWorld.sol、solc-0.8.11、abigen
```

#### prepare the certificate required to establish an ssl connection

When you use the build _ chain.sh script to build a blockchain, the sdk certificate, private key, and ca certificate are generated in the. / nodes / 127.0.0.1 / sdk folder. You need to copy these three files to the location specified by the parameter, for example, in the following。

#### Deployment contract

Create the cmd folder in the helloworld folder and create the main.go file in the cmd folder. The content of main.go is as follows

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

    ret, err := helloSession.Get() / / Call the contract Get method
    if err != nil {
        fmt.Printf("helloworld.Get() failed: %v", err)
        return
    }
    done := make(chan bool)
    / / Listen to the contract set event
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

Build and execute。

```bash
# The instruction is executed in the go-sdk directory
go run helloworld/cmd/main.go
```

```eval_rst
.. note::

    - The contract address needs to be saved manually, which is used when calling the contract interface
    -If the dynamic library of c-sdk is placed in a custom directory, you need 'go run-ldflags ="-r Path to custom directory"`

```

### Asynchronous deployment, invoking HelloWorld contract

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

## State Secret Sample

The development process for using the state secret feature is roughly the same as for non-state secrets, with the following differences

- The FISCO BCOS blockchain network needs to turn on the national secret feature
- You need to replace the non-state secret private key with the state secret private key
- TLS certificate and private key need to be prepared
- need to add when installing solc compiler**-g** option, replace with the State Secret version
-When using the abigen tool to convert bin and abi to go files, you need to add parameters**--smcrypto=true**

### HelloWorld Sample

#### Prepare the HelloWorld.sol contract file

Create a new helloworld folder in the go-sdk home directory and create the HelloWorld.sol contract in this folder。The contract provides two interfaces, get()and set()to get / set the contract variable name。The contract is as follows

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

#### install the state secret solc compiler

The compiler is used to compile the sol contract file into abi and bin files

```bash
# The instruction is executed in the helloworld folder
bash ../tools/download_solc.sh -v 0.8.11 -g
```

#### Build code generation tool abigen for go-sdk

This tool is used to convert abi and bin files to go files

```bash
# This instruction is executed in the helloworld folder to compile and generate the abigen tool
go build ../cmd/abigen
```

#### compile to generate go file

First, use solc to compile the contract file HelloWorld.sol to generate abi and bin files

```bash
# The instruction is executed in the helloworld folder
./solc-0.8.11-gm --bin --abi -o ./ ./HelloWorld.sol
```

HelloWorld.bin and HelloWorld.abi are generated under the helloworld directory。Use the abigen tool to convert HelloWorld.bin and HelloWorld.abi into HelloWorld.go:

```bash
# The instruction is executed in the helloworld folder
./abigen --bin ./HelloWorld.bin --abi ./HelloWorld.abi --pkg helloworld --type HelloWorld --out ./HelloWorld.go --smcrypto=true
```

-The next steps are the same as those of non-state secrets and do not take up extra space
