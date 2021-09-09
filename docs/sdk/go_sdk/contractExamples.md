# 合约开发样例

标签：``go-sdk`` ``合约开发``

----
## 非国密样例

本开发样例使用标准单群组四节点区块链网络结构，搭建请参考：[安装](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)。

在利用SDK进行项目开发时，对智能合约进行操作需要利用go-sdk的`abigen`工具将Solidity智能合约转换为`Go`文件代码。整体上主要包含六个流程：

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
pragma solidity>=0.4.24 <0.6.11;

contract HelloWorld {
    string value;

    constructor() public {
        value = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return value;
    }

    function set(string v) public {
        value = v;
    }
}
```

#### 安装solc编译器

该编译器用于将 sol 合约文件编译成 abi 和 bin 文件，目前FISCO BCOS提供的`solc`编译器有0.4.25/0.5.2/0.6.10，每个版本有国密和非国密两种。

```bash
# 该指令在helloworld文件夹中执行
bash ../tools/download_solc.sh -v 0.4.25
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
./solc-0.4.25 --bin --abi -o ./ ./HelloWorld.sol
```

helloworld目录下会生成HelloWorld.bin和HelloWorld.abi。此时利用abigen工具将HelloWorld.bin和HelloWorld.abi转换成HelloWorld.go：

```bash
# 该指令在helloworld文件夹中执行
./abigen --bin ./HelloWorld.bin --abi ./HelloWorld.abi --pkg helloworld --type HelloWorld --out ./HelloWorld.go
```

最后helloworld文件夹下面存在以下6个文件：

```bash
HelloWorld.abi、HelloWorld.bin、HelloWorld.go、HelloWorld.sol、solc-0.4.25、abigen
```

#### 准备建立ssl连接需要的证书

使用build_chain.sh脚本搭建区块链时会在./nodes/127.0.0.1/sdk文件夹中生成sdk证书、私钥以及ca证书，需要将这三个文件拷贝至`config.toml`中配置的位置。

#### 部署合约

在helloworld文件夹中创建cmd文件夹，在cmd文件夹中创建main.go文件，main.go的内容如下，在该文件中调用HelloWorld.go部署智能合约

```go
package main

import (
    "fmt"
    "log"

    "github.com/FISCO-BCOS/go-sdk/client"
    "github.com/FISCO-BCOS/go-sdk/conf"
    "github.com/FISCO-BCOS/go-sdk/helloworld" // import helloworld
)

func main(){
    configs, err := conf.ParseConfigFile("config.toml")
    if err != nil {
        log.Fatal(err)
    }
    config := &configs[0]

    client, err := client.Dial(config)
    if err != nil {
        log.Fatal(err)
    }
    address, tx, instance, err := helloworld.DeployHelloWorld(client.GetTransactOpts(), client) // deploy contract
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println("contract address: ", address.Hex())  // the address should be saved
    fmt.Println("transaction hash: ", tx.Hash().Hex())
    _ = instance
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

```

#### 调用合约get/set接口

在contract文件夹中创建helloworld_get.go文件，调用合约get接口，获取智能合约中name变量存储的值

```go
package main

import (
    "fmt"
    "log"

    "github.com/FISCO-BCOS/go-sdk/client"
    "github.com/FISCO-BCOS/go-sdk/conf"
    "github.com/FISCO-BCOS/go-sdk/helloworld"
    "github.com/ethereum/go-ethereum/common"
)

func main() {
    configs, err := conf.ParseConfigFile("config.toml")
    if err != nil {
        log.Fatal(err)
    }
    config := &configs[0]
    client, err := client.Dial(config)
    if err != nil {
        log.Fatal(err)
    }

    // load the contract
    contractAddress := common.HexToAddress("contract address in hex") // 0x481D3A1dcD72cD618Ea768b3FbF69D78B46995b0
    instance, err := helloworld.NewHelloWorld(contractAddress, client)
    if err != nil {
        log.Fatal(err)
    }

    helloworldSession := &helloworld.HelloWorldSession{Contract: instance, CallOpts: *client.GetCallOpts(), TransactOpts: *client.GetTransactOpts()}

    value, err := helloworldSession.Get()    // call Get API
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println("value :", value)

    value = "Hello, FISCO BCOS"
    tx, receipt, err := helloworldSession.Set(value)  // call set API
    if err != nil {
        log.Fatal(err)
    }

    fmt.Printf("tx sent: %s\n", tx.Hash().Hex())
    fmt.Printf("transaction hash of receipt: %s\n", receipt.GetTransactionHash())
}
```

### KVTableTest样例

#### 准备Table.sol合约文件

在 go-sdk 主目录中新建 kvtabletest 文件夹，拷贝 Table.sol 合约。

```bash
# 创建 kvtabletest 文件夹，该指令在go-sdk目录中执行
mkdir kvtabletest && cd kvtabletest

# 拷贝 Table.sol KVTableTest 合约
cp ../.ci/Table/Table.sol ../.ci/Table/KVTableTest.sol ./
```

#### 准备KVTableTest.sol合约文件

该合约调用 Table 合约，实现创建用户表 t_kvtest，并对 t_kvtest 表进行读写。

```solidity
pragma solidity>=0.4.24 <0.6.11;

import "./Table.sol";

contract KVTableTest {
    event SetResult(int256 count);

    KVTableFactory tableFactory;
    string constant TABLE_NAME = "t_kvtest";

    constructor() public {
        //The fixed address is 0x1010 for KVTableFactory
        tableFactory = KVTableFactory(0x1010);
        // the parameters of createTable are tableName,keyField,"vlaueFiled1,vlaueFiled2,vlaueFiled3,..."
        tableFactory.createTable(TABLE_NAME, "id", "item_price,item_name");
    }

    //get record
    function get(string memory id) public view returns (bool, int256, string memory) {
        KVTable table = tableFactory.openTable(TABLE_NAME);
        bool ok = false;
        Entry entry;
        (ok, entry) = table.get(id);
        int256 item_price;
        string memory item_name;
        if (ok) {
            item_price = entry.getInt("item_price");
            item_name = entry.getString("item_name");
        }
        return (ok, item_price, item_name);
    }

    //set record
    function set(string memory id, int256 item_price, string memory item_name)
    public
    returns (int256)
    {
        KVTable table = tableFactory.openTable(TABLE_NAME);
        Entry entry = table.newEntry();
        // the length of entry's field value should < 16MB
        entry.set("id", id);
        entry.set("item_price", item_price);
        entry.set("item_name", item_name);
        // the first parameter length of set should <= 255B
        int256 count = table.set(id, entry);
        emit SetResult(count);
        return count;
    }
}
```

#### 准备环境与合约编译

下面的操作都在 kvtabletest 文件夹中执行

```bash
bash ../tools/download_solc.sh -v 0.5.2

./solc-0.5.2 --bin --abi -o ./ ./KVTableTest.sol
```

#### 编译生成 go 文件

先利用 solc 编译合约文件 KVTableTest.sol，生成 abi 和 bin 文件

```bash
go run ../cmd/abigen --bin ./KVTableTest.bin --abi ./KVTableTest.abi --pkg kvtabletest --type KVTableTest --out ./KVTableTest.go
```

最后 kvtabletest 文件夹下面存在以下5个文件和其它若干文件：

```bash
KVTableTest.abi、KVTableTest.bin、KVTableTest.go、KVTableTest.sol、solc-0.5.2
```

#### 部署合约

在 kvtabletest 文件夹中创建 cmd 文件夹，在 cmd 文件夹中创建 kvtabletest_main.go 文件，调用 KVTableTest.go 部署智能合约。合约将创建 t_kvtest 表，该表用于记录某公司仓库中物资，以唯一的物资编号作为主key，保存物资的名称和价格。使用build_chain.sh脚本搭建区块链时会在./nodes/127.0.0.1/sdk文件夹中生成sdk证书、私钥以及ca证书，需要将这三个文件拷贝至`config.toml`中配置的位置。

```go
package main

import (
    "fmt"
    "log"

    "github.com/FISCO-BCOS/go-sdk/client"
    "github.com/FISCO-BCOS/go-sdk/conf"
    kvtable "github.com/FISCO-BCOS/go-sdk/kvtabletest" // import kvtabletest
)

func main(){
    configs, err := conf.ParseConfigFile("config.toml")
    if err != nil {
        log.Fatal(err)
    }
    config := &configs[0]

    client, err := client.Dial(config)
    if err != nil {
        log.Fatal(err)
    }
    address, tx, instance, err := kvtable.DeployKVTableTest(client.GetTransactOpts(), client)
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println("contract address: ", address.Hex())  // the address should be saved
    fmt.Println("transaction hash: ", tx.Hash().Hex())
    _ = instance
}
```

```bash
# 该指令在go-sdk目录中执行
go run kvtabletest/cmd/kvtabletest_main.go
```

```eval_rst
.. note::

    - 合约地址需要手动保存，调用合约接口时使用

```

#### 调用合约set/get接口

在 contract 文件夹中新建 kvtabletest_set.go 文件，该文件调用合约 set 接口，向 t_kvtest 表中插入一条数据：id="100010001001"、item_name="Laptop"、item_price=6000。然后调用get接口查询数据。

```go
package main

import (
	"fmt"
	"log"
	"math/big"
	"strings"

	"github.com/FISCO-BCOS/go-sdk/abi"
	"github.com/FISCO-BCOS/go-sdk/client"
	"github.com/FISCO-BCOS/go-sdk/conf"
	kvtable "github.com/FISCO-BCOS/go-sdk/kvtabletest"
	"github.com/ethereum/go-ethereum/common"
)

func main() {
	configs, err := conf.ParseConfigFile("config.toml")
	if err != nil {
		log.Fatal(err)
	}
	config := &configs[0]

	client, err := client.Dial(config)
	if err != nil {
		log.Fatal(err)
	}

	// load the contract
	contractAddress := common.HexToAddress(contract address in hex string) // deploy contract to get address
	instance, err := kvtable.NewKVTableTest(contractAddress, client)
	if err != nil {
		log.Fatal(err)
	}

	kvtabletestSession := &kvtable.KVTableTestSession{Contract: instance, CallOpts: *client.GetCallOpts(), TransactOpts: *client.GetTransactOpts()}

	id := "100010001001"
	item_name := "Laptop"
	item_price := big.NewInt(6000)
	tx, receipt, err := kvtabletestSession.Set(id, item_price, item_name) // call set API
	if err != nil {
		log.Fatal(err)
	}
	fmt.Printf("tx sent: %s\n", tx.Hash().Hex())
	// 解析abi
	kvtableTestABI, err := abi.JSON(strings.NewReader(kvtable.KVTableTestABI))
	if err != nil {
		fmt.Printf("parse abi failed, err: %v\n", err)
		return
	}
	// kvtableTestABI 解析返回值
	ret := big.NewInt(0)
	err = kvtableTestABI.Unpack(&ret, "set", common.FromHex(receipt.Output))
	if err != nil {
		fmt.Printf("parse return value failed, err: %v\n", err)
		return
	}

    fmt.Printf("seted lines: %v\n", ret.String())

	success, item_price, item_name, err := kvtabletestSession.Get(id) // call get API
	if err != nil {
		log.Fatal(err)
	}
	if !success {
		log.Fatalf("id：%v is not found \n", id)
	}
	fmt.Printf("id: %v, item_price: %v, item_name: %v \n", id, item_price, item_name)
}

```

## 异步接口使用样例

异步合约开发指的是调用编译生成的 go 文件中提供的异步接口部署合约、修改数据，可以极大提高交易并发量。以 KVTableTest 为例，`编译生成 go 文件` 的步骤 [同上](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/go_sdk/contractExamples.html#kvtabletest)

### 异步部署、调用KVTableTest合约

利用生成的异步接口部署和调用合约。通过注入 handler 函数，处理交易回执，获取交易回执中的 output。

```go
package main

import (
    "encoding/hex"
    "fmt"
    "log"
    "math/big"
    "strings"

    "github.com/ethereum/go-ethereum/common"

    "github.com/FISCO-BCOS/go-sdk/abi"
    "github.com/FISCO-BCOS/go-sdk/client"
    "github.com/FISCO-BCOS/go-sdk/conf"
    "github.com/FISCO-BCOS/go-sdk/core/types"
    kvtable "github.com/FISCO-BCOS/go-sdk/examples" // import kvtabletest
)

var (
    channel         = make(chan int, 0)
    contractAddress common.Address
)

func deployContractHandler(receipt *types.Receipt, err error) {
    if err != nil {
        fmt.Printf("%v\n", err)
        return
    }
    fmt.Println("contract address: ", receipt.ContractAddress.Hex()) // the address should be saved
    contractAddress = receipt.ContractAddress
    channel <- 0
}

func invokeSetHandler(receipt *types.Receipt, err error) {
    if err != nil {
        fmt.Printf("%v\n", err)
        return
    }
    setedLines, err := parseOutput(kvtable.KVTableTestABI, "set", receipt)
    if err != nil {
        log.Fatalf("error when transfer string to int: %v\n", err)
    }
    fmt.Printf("seted lines: %v\n", setedLines.Int64())
    channel <- 0
}

func main() {
    configs, err := conf.ParseConfigFile("config.toml")
    if err != nil {
        log.Fatal(err)
    }
    config := &configs[0]

    // AsyncDeploy
    fmt.Println("-------------------starting deploy contract-----------------------")
    client, err := client.Dial(config)
    if err != nil {
        log.Fatal(err)
    }
    tx, err := kvtable.AsyncDeployKVTableTest(client.GetTransactOpts(), deployContractHandler, client)
    if err != nil {
        log.Fatal(err)
    }
    fmt.Println("transaction hash: ", tx.Hash().Hex())
    <-channel

    // invoke AsyncSet to insert info
    fmt.Println("\n-------------------starting invoke Set to insert info-----------------------")
    instance, err := kvtable.NewKVTableTest(contractAddress, client)
    if err != nil {
        log.Fatal(err)
    }
    kvtabletestSession := &kvtable.KVTableTestSession{Contract: instance, CallOpts: *client.GetCallOpts(), TransactOpts: *client.GetTransactOpts()}
    id := "100010001001"
    item_name := "Laptop"
    item_price := big.NewInt(6000)
    tx, err = kvtabletestSession.AsyncSet(invokeSetHandler, id, item_price, item_name) // call set API
    if err != nil {
        log.Fatal(err)
    }
    fmt.Printf("tx sent: %s\n", tx.Hash().Hex())
    <-channel

    // invoke Get to query info
    fmt.Println("\n-------------------starting invoke Get to query info-----------------------")
    bool, item_price, item_name, err := kvtabletestSession.Get(id) // call get API
    if err != nil {
        log.Fatal(err)
    }
    if !bool {
        log.Fatalf("id：%v is not found \n", id)
    }
    fmt.Printf("id: %v, item_price: %v, item_name: %v \n", id, item_price, item_name)
}

func parseOutput(abiStr, name string, receipt *types.Receipt) (*big.Int, error) {
    parsed, err := abi.JSON(strings.NewReader(abiStr))
    if err != nil {
        fmt.Printf("parse ABI failed, err: %v", err)
    }
    var ret *big.Int
    b, err := hex.DecodeString(receipt.Output[2:])
    if err != nil {
        return nil, fmt.Errorf("decode receipt.Output[2:] failed, err: %v", err)
    }
    err = parsed.Unpack(&ret, name, b)
    if err != nil {
        return nil, fmt.Errorf("unpack %v failed, err: %v", name, err)
    }
    return ret, nil
}
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
	ret, err := hello.Get()
	if err != nil {
		fmt.Printf("hello.Get() failed: %v", err)
		return
	}
	fmt.Printf("Get: %s\n", ret)
	tx, err = hello.AsyncSet(func(receipt *types.Receipt, err error) {
		if err != nil {
			fmt.Printf("hello.AsyncSet failed: %v\n", err)
			return
		}
		if receipt.Status != 0 {
			fmt.Printf("hello.AsyncSet failed: %v\n", receipt.GetErrorMessage())
		}
		channel <- 0
	}, "fisco")
	<-channel
	ret, err = hello.Get()
	if err != nil {
		fmt.Printf("hello.Get() failed: %v", err)
		return
	}
	fmt.Printf("Get: %s\n", ret)
}

```

## 国密样例

使用国密特性的开发流程和非国密大致相同，不同点在于以下几部分：

- 搭建的 FISCO BCOS 区块链网络需要开启国密特性，可参考：[国密支持](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/guomi_crypto.html)
- go-sdk 的 config.toml 配置文件中 KeyFile 配置项，需要将非国密私钥替换为国密私钥
- go-sdk 的 config.toml 配置文件中 SMCrypto 配置项，需要修改为 true
- 安装 solc 编译器时需要添加 **-g** 选项，替换为国密版本
- 使用 abigen 工具将 bin 和 abi 转换为 go 文件时，需要添加参数 **--smcrypto=true**

### HelloWorld样例

#### 准备HelloWorld.sol合约文件

在 go-sdk 主目录中新建 helloworld 文件夹，在该文件夹中创建 HelloWorld.sol 合约。该合约提供两个接口，分别是get()和set()，用于获取/设置合约变量name。合约内容如下

```solidity
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

#### 安装国密solc编译器

该编译器用于将 sol 合约文件编译成 abi 和 bin 文件

```bash
# 该指令在helloworld文件夹中执行
bash ../tools/download_solc.sh -v 0.4.25 -g
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
./solc-0.4.25-gm --bin --abi -o ./ ./HelloWorld.sol
```

helloworld目录下会生成HelloWorld.bin和HelloWorld.abi。此时利用abigen工具将HelloWorld.bin和HelloWorld.abi转换成HelloWorld.go：

```bash
# 该指令在helloworld文件夹中执行
./abigen --bin ./HelloWorld.bin --abi ./HelloWorld.abi --pkg helloworld --type HelloWorld --out ./HelloWorld.go --smcrypto=true
```

- 接下来的步骤同非国密，不占用多余篇幅

