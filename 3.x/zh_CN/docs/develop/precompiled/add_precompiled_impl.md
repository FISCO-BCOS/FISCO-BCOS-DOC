# 新增HelloWorld预编译合约

标签：``预编译合约`` ``HelloWorld`` ``区块链应用开发``


----------


先来看一下我们想要实现的HelloWorld合约的Solidity版本。Solidity版本的HelloWorld，有一个成员name用于存储数据，两个接口get(),set(string)分别用于读取和设置该成员变量。

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

### step1 定义HelloWorld接口

Solidity的接口调用都会被封装为一笔交易，其中，调用只读接口的交易不会被打包进区块，而写接口交易会被打包进区块中。由于底层需要根据交易数据中的ABI编码来判断调用的接口并解析参数，所以需要先把接口定义出来。预编译合约的ABI接口规则与Solidity完全相同，定义预编译合约接口时，通常需要定义一个有相同接口的Solidity合约，这个合约称为预编译合约的**接口合约**。接口合约在调用预编译合约时需要使用。

```solidity
pragma solidity ^0.6.0;

contract HelloWorldPrecompiled{
    function get() public view returns (string memory);
    function set(string memory n) public;
}
```

### step2 设计存储结构

预编译合约涉及存储操作时，需要确定存储的表信息(表名与表结构，存储数据在FISCO BCOS中会统一抽象为表结构)。如果合约中不涉及变量存储，可以忽略该步骤。

对于HelloWorld，我们设计如下的表。该表只存储一对键值对，key字段为hello_key，value字段为hello_value 存储对应的字符串值，可以通过set(string)接口修改，通过get()接口获取。

| key       | value          |
| --------- | -------------- |
| hello_key | "Hello World!" |


### step3 实现合约逻辑

实现新增合约的调用逻辑，需要新实现一个C++类，该类需要继承Precompiled类， 重载call函数， 在call函数中实现各个接口的调用行为。

```c++
std::shared_ptr<PrecompiledExecResult> call(
        std::shared_ptr<executor::TransactionExecutive> _executive, bytesConstRef _param,
        const std::string& _origin, const std::string& _sender) override;
```

call函数有三个参数，\_executive保存交易执行的上下文，_ param是调用合约的参数信息，本次调用对应合约接口以及接口的参数可以从_ param解析获取，_origin是交易发送者，用于权限控制。 接下来，我们在源码**bcos-executor/src/precompiled/extension**目录下实现HelloWorldPrecompiled类，重载call函数，实现get()/set(string)两个接口。 

##### 接口注册：

```c++
// 定义类中所有的接口
const char* const HELLO_WORLD_METHOD_GET = "get()";
const char* const HELLO_WORLD_METHOD_SET = "set(string)";

// 在构造函数进行接口注册
HelloWorldPrecompiled::HelloWorldPrecompiled(crypto::Hash::Ptr _hashImpl) : Precompiled(_hashImpl)
{// name2Selector是基类Precompiled类中成员，保存接口调用的映射关系
    name2Selector[HELLO_WORLD_METHOD_GET] = getFuncSelector(HELLO_WORLD_METHOD_GET);
    name2Selector[HELLO_WORLD_METHOD_SET] = getFuncSelector(HELLO_WORLD_METHOD_SET);
}
```

##### 创建表：

```c++
// 定义表名
const std::string HELLO_WORLD_TABLE_NAME = "_ext_hello_world_";
// 主键字段
const std::string HELLOWORLD_KEY_FIELD = "key";
// 其他字段字段，多个字段使用逗号分割，比如 "field0,field1,field2"
const std::string HELLOWORLD_VALUE_FIELD = "value";
```

##### 在call函数中添加打开表的逻辑：

```c++
// 获取存储对象
auto storage = _executive->storage();
// call函数中，表存在时打开，否则首先创建表
auto table = storage.openTable(precompiled::getTableName(HELLO_WORLD_TABLE_NAME));
    if (!table)
    {
	      // 表不存在，首先创建
        table = _executive->storage().createTable(
            precompiled::getTableName(HELLO_WORLD_TABLE_NAME), HELLO_WORLD_VALUE_FIELD);
        if (!table)
        {
           // 创建表失败，返回错误码
        }
    }
```

##### 区分调用接口：

```c++
uint32_t func = getParamFunc(_param);
if (func == name2Selector[HELLO_WORLD_METHOD_GET])
{// get() 接口调用逻辑 
}
else if (func == name2Selector[HELLO_WORLD_METHOD_SET])
{// set(string) 接口调用逻辑 
}
else
{// 未知接口，调用错误，返回错误码
}
```

##### 参数解析与返回：

调用合约时的参数包含在call函数的_param参数中，如果是Solidity调用，则使用Solidity ABI编码，如果是webankblockchain-liquid（简称WBC-Liquid）则使用Scale编码。

PrecompiledCodec封装了两种编码格式的接口，使用PrecompiledCodec即可。

##### HelloWorldPrecompiled实现：

```c++
std::shared_ptr<PrecompiledExecResult> HelloWorldPrecompiled::call(
    std::shared_ptr<executor::TransactionExecutive> _executive, bytesConstRef _param,
    const std::string&, const std::string&)
{
    // 解析函数接口
    uint32_t func = getParamFunc(_param);
    bytesConstRef data = getParamData(_param);
    auto blockContext = _executive->blockContext().lock();
  	// 创建PrecompiledCodec编解码对象
    auto codec =
        make_shared<PrecompiledCodec>(blockContext->hashHandler(), blockContext->isWasm());
	  // 打开_ext_hello_world_表，省略
........
```

get()接口实现

```c++
// 区分调用接口，各个接口的具体调用逻辑
    if (func == name2Selector[HELLO_WORLD_METHOD_GET])
    {  
      	// get() 接口调用
        // 默认返回值
        std::string retValue = "Hello World!";

        auto entry = table->getRow(HELLO_WORLD_KEY_FIELD_NAME);
        if (!entry)
        {
            retValue = entry->getField(HELLO_WORLD_VALUE_FIELD);
        }
        callResult->setExecResult(codec->encode(retValue));
    }
```

set接口实现

```c++
		else if (func == name2Selector[HELLO_WORLD_METHOD_SET])
    {  // set(string) function call

        std::string strValue;
        codec->decode(data, strValue);
        auto entry = table->getRow(HELLO_WORLD_KEY_FIELD_NAME);
        gasPricer->updateMemUsed(entry->capacityOfHashField());
        gasPricer->appendOperation(InterfaceOpcode::Select, 1);
        entry->setField(HELLO_WORLD_VALUE_FIELD, strValue);

        table->setRow(HELLO_WORLD_KEY_FIELD_NAME, *entry);
        gasPricer->appendOperation(InterfaceOpcode::Update, 1);
        gasPricer->updateMemUsed(entry->capacityOfHashField());
        getErrorCodeOut(callResult->mutableExecResult(), 1, *codec);
    }
    else
    {  // 参数错误，未知的接口调用
				callResult->setExecResult(codec->encode(u256((int)CODE_UNKNOW_FUNCTION_CALL)));
    }
    return callResult;
}
```

### step4 分配并注册合约地址

FSICO BCOS 3.0 执行交易时，根据合约地址区分是不是预编译合约，所以开发完预编译合约后，需要在底层注册为预编译合约注册地址。

用户分配地址空间为0x5001-0xffff,用户需要为新添加的预编译合约分配一个未使用的地址，**预编译合约地址必须唯一， 不可冲突**。

开发者需要修改`bcos-executor/src/executor/TransactionExecutor.cpp`文件，在initPrecompiled函数中的`m_constantPrecompiled` Map中插入合约地址和合约对象实例，如下注册HelloWorldPrecompiled合约：

```
auto helloPrecompiled = std::make_shared<HelloWorldPrecompiled>(m_hashImpl);
m_constantPrecompiled.insert({"0000000000000000000000000000000000005001", helloPrecompiled});
```

### step5 编译源码

参考FISCO BCOS 3.0使用手册->获取可执行程序->[源码编译](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html)。需要注意的是，实现的HelloWorldPrecompiled.cpp和HelloWorldPrecompiled.h需要放置于FISCO-BCOS/libprecompiled/extension目录下。

## HelloWorld预编译合约调用

### 使用控制台调用HelloWorld预编译合约

在控制台solidity/contracts创建HelloWorldPrecompiled.sol文件，文件内容是HelloWorld预编译合约的接口声明，如下

```solidity
pragma solidity ^0.6.0;
contract HelloWorldPrecompiled{
    function get() public constant returns(string memory);
    function set(string memory n);
}
```

使用编译出的二进制搭建节点后，然后执行下面语句即可调用

```shell
[group] />: call HelloWorldPrecompiled 0x5001 get
Hello World!
```

### 在Solidity中调用HelloWorld预编译合约

我们尝试在Solidity合约中创建预编译合约对象并调用其接口。在控制台solidity/contracts创建HelloWorldHelper.sol文件，文件内容如下

```solidity
pragma solidity ^0.6.0;
import "./HelloWorldPrecompiled.sol";

contract HelloWorldHelper {
    HelloWorldPrecompiled hello;
    function HelloWorldHelper() {
        // 调用HelloWorld预编译合约
        hello = HelloWorldPrecompiled(0x5001); 
    }
    function get() public constant returns(string memory) {
        return hello.get();
    }
    function set(string memory m) {
        hello.set(m);
    }
}
```

部署HelloWorldHelper合约，然后调用HelloWorldHelper合约的接口，结果如下:

```shell
[group] />: deploy HelloWorldHelper
transaction hash: 0x0fc5d6ad61d756a28235dde3041e7ea9acef7cb64babb9e5815532dbd9846681
contract address: 0x33E56a083e135936C1144960a708c43A661706C0
currentAccount: 0x3977d248ce98f3affa78a800c4f234434355aa77

[group] />: call HelloWorldHelper 0x33E56a083e135936C1144960a708c43A661706C0 get
Hello World!
```

到这里，就可以恭喜你顺滑地完成了HelloWorld预编译合约的开发，其他预编译合约的开发流程道理相通。
