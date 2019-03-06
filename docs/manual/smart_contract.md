# 智能合约开发

FISCO BCOS平台目前支持Solidity、CRUD、Precompiled三种智能合约形式。

- Solidity合约与以太坊相同，支持最新版本。

- CRUD合约通过在Solidity合约中支持分布式存储预编译合约，可以实现将Solidity合约中数据存储在FISCO BCOS平台AMDB的表结构中，实现合约逻辑与数据的分离。

- 预编译（Precompiled）合约使用C++开发，内置于FISCO BCOS平台，相比于Solidity合约具有更好的性能，其合约接口需要在编译时预先确定，适用于逻辑固定但需要共识的场景，例如群组配置。关于预编译合约的开发将在下一节进行介绍。


### [Solidity合约开发](https://solidity.readthedocs.io/en/latest/)

- [Solidity官方文档](https://solidity.readthedocs.io/en/latest/)
- [Remix在线IDE](https://remix.ethereum.org/)

### CRUD合约开发

访问 AMDB 需要使用 AMDB 专用的智能合约`Table.sol`接口，该接口是数据库合约，可以创建表，并对表进行增删改查操作。

`Table.sol`文件代码如下:

```js

contract TableFactory {
    function openTable(string) public constant returns (Table);  // 打开表
    function createTable(string,string,string) public constant returns(Table);  // 创建表
}

// 查询条件
contract Condition {
    function EQ(string, int) public;
    function EQ(string, string) public;
    
    function NE(string, int) public;
    function NE(string, string) public;

    function GT(string, int) public;
    function GE(string, int) public;
    
    function LT(string, int) public;
    function LE(string, int) public;
    
    function limit(int) public;
    function limit(int, int) public;
}

// 单条数据记录
contract Entry {
    function getInt(string) public constant returns(int);
    function getAddress(string) public constant returns(address);
    function getBytes64(string) public constant returns(byte[64]);
    function getBytes32(string) public constant returns(bytes32);
    
    function set(string, int) public;
    function set(string, string) public;
}

// 数据记录集
contract Entries {
    function get(int) public constant returns(Entry);
    function size() public constant returns(int);
}

// Table主类
contract Table {
    // 查询接口
    function select(string, Condition) public constant returns(Entries);
    // 插入接口
    function insert(string, Entry) public returns(int);
    // 更新接口
    function update(string, Entry, Condition) public returns(int);
    // 删除接口
    function remove(string, Condition) public returns(int);
    
    function newEntry() public constant returns(Entry);
    function newCondition() public constant returns(Condition);
}
```

提供一个合约案例`TableTest.sol`，代码如下：

```js
import "./Table.sol";

contract TableTest {
    event selectResult(bytes32 name, int item_id, bytes32 item_name);
    event insertResult(int count);
    event updateResult(int count);
    event removeResult(int count);
    
    // 创建表
    function create() public {
        TableFactory tf = TableFactory(0x1001);  // TableFactory的地址固定为0x1001
        // 创建t_test表，表的key_field为name，value_field为item_id,item_name 
        // key_field表示AMDB主key value_field表示表中的列，可以有多列，以逗号分隔
        tf.createTable("t_test", "name", "item_id,item_name");
    }

    // 查询数据
    function select(string name) public constant returns(bytes32[], int[], bytes32[]){
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_test");
        
        // 条件为空表示不筛选 也可以根据需要使用条件筛选
        Condition condition = table.newCondition();
        
        Entries entries = table.select(name, condition);
        bytes32[] memory user_name_bytes_list = new bytes32[](uint256(entries.size()));
        int[] memory item_id_list = new int[](uint256(entries.size()));
        bytes32[] memory item_name_bytes_list = new bytes32[](uint256(entries.size()));
        
        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);
            
            user_name_bytes_list[uint256(i)] = entry.getBytes32("name");
            item_id_list[uint256(i)] = entry.getInt("item_id");
            item_name_bytes_list[uint256(i)] = entry.getBytes32("item_name");
            selectResult(user_name_bytes_list[uint256(i)], item_id_list[uint256(i)], item_name_bytes_list[uint256(i)]);
        }
 
        return (user_name_bytes_list, item_id_list, item_name_bytes_list);
    }
    // 插入数据
    function insert(string name, int item_id, string item_name) public returns(int) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_test");
        
        Entry entry = table.newEntry();
        entry.set("name", name);
        entry.set("item_id", item_id);
        entry.set("item_name", item_name);
        
        int count = table.insert(name, entry);
        insertResult(count);
        
        return count;
    }
    // 更新数据
    function update(string name, int item_id, string item_name) public returns(int) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_test");
        
        Entry entry = table.newEntry();
        entry.set("item_name", item_name);
        
        Condition condition = table.newCondition();
        condition.EQ("name", name);
        condition.EQ("item_id", item_id);
        
        int count = table.update(name, entry, condition);
        updateResult(count);
        
        return count;
    }
    // 删除数据
    function remove(string name, int item_id) public returns(int){
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_test");
        
        Condition condition = table.newCondition();
        condition.EQ("name", name);
        condition.EQ("item_id", item_id);
        
        int count = table.remove(name, condition);
        removeResult(count);
        
        return count;
    }
}
```

`TableTest.sol`调用了 AMDB 专用的智能合约`Table.sol`，实现的是创建用户表`t_test`，并对`t_test`表进行增删改查的功能。`t_test`表结构如下，该表记录某公司员工领用物资和编号。

|name*|item_name|item_id|
|:----|:----|:------|
|Bob|Laptop|100010001001|

```eval_rst
.. important::
    客户端需要调用转换为 Java 文件的合约代码，需要将TableTest.sol和Table.sol放入 web3sdk 的src/test/resources/contract目录下，通过 Web3SDK 的编译脚本生成TableTest.java。
```


## 预编译合约开发

### 一. 简介

预编译（precompiled）合约是一项以太坊原生支持的功能：在底层使用c++代码实现特定功能的合约，提供给EVM模块调用。FISCO BCOS继承并且拓展了这种特性，在此基础上发展了一套功能强大并易于拓展的框架[precompiled设计原理](../design/virtual_machine/precompiled.md)。   
本文作为一篇入门指导，旨在指引用户如何实现自己的precompiled合约,并实现precompiled合约的调用。

### 二. 实现预编译合约  

#### 2.1 流程
实现预编译合约的流程：
![流程](../../images/precompiled/create_process.png)

- **分配合约地址**  

调用solidity合约或者预编译合约需要根据合约地址来区分，地址空间划分：

| 地址用途 | 地址范围 |
| --------- | --------- |
| 以太坊precompiled | 0x0001-0x0004 |
| 保留 | 0x0005-0x0fff |
| FISCO BCOS precompied | 0x1000-0x1006 |
| FISCO BCOS预留 | 0x1007-0x5000 |
| 用户分配区间 | 0x5001 - 0xffff |
| CRUD临时合约 | 0x10000+ |
| solidity | 其他 |

 用户分配地址空间为`0x5001`-`0xffff`,用户需要为新添加的预编译合约分配一个未使用的地址，**预编译合约地址必须唯一， 不可冲突**。

FISCO BCOS中实现的precompild合约列表以及地址分配：

| 地址   | 功能   | 源码([libprecompiled目录](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/release-2.0.1/libprecompiled)) |
|--------|--------|---------|
| 0x1000 | 系统参数管理 | SystemConfigPrecompiled.cpp |
| 0x1001 | 表工厂合约 | TableFactoryPrecompiled.cpp |
| 0x1002 | CRUD合约 | CRUDPrecompiled.cpp |
| 0x1003 | 共识节点管理 | ConsensusPrecompiled.cpp |
| 0x1004 | CNS功能  | CNSPrecompiled.cpp |
| 0x1005 | 存储表权限管理 | AuthorityPrecompiled.cpp |

- **定义合约接口**  

同solidity合约，设计合约时需要首先确定合约的ABI接口， precomipiled合约的ABI接口规则与solidity完全相同，[solidity ABI链接](https://solidity.readthedocs.io/en/develop/abi-spec.html)。  
 
> 定义预编译合约接口时，通常需要定义一个有相同接口的solidity合约，并且将所有的接口的函数体置空，这个合约我们称为预编译合约的**辅助合约**，辅助合约在调用预编译合约时需要使用。 

```js
    pragma solidity ^0.4.25;
    contract Contract_Name {
        function interface0(parameters ... ) {}
        ....
        function interfaceN(parameters ... ) {}
    }
```  

- **设计存储结构**  

预编译合约涉及存储操作时，需要确定存储的表信息(表名与表结构,存储数据在FISCO BCOS中会统一抽象为表结构)， [存储结构](../design/storage/storage.md)。  

```eval_rst
.. note::
    不涉及存储操作可以省略该流程。
```

- **实现调用逻辑**  

实现新增合约的调用逻辑，需要新实现一个c++类，该类需要继承[Precompiled](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/libblockverifier/Precompiled.h#L37), 重载call函数， 在call函数中实现各个接口的调用行为。

```cpp
    // libblockverifier/Precompiled.h
    class Precompiled
    {
        virtual bytes call(std::shared_ptr<ExecutiveContext> context, bytesConstRef param,
            Address const& origin = Address()) = 0;
    };
```

- **注册合约**  

最后需要将合约的地址与对应的类注册到合约的执行上下文，这样通过地址调用precompiled合约时合约的执行逻辑才能被正确识别执行， 查看注册的[预编译合约列表](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/libblockverifier/ExecutiveContextFactory.cpp#L36)。   
注册路径：

```
    file        libblockverifier/ExecutiveContextFactory.cpp
    function    initExecutiveContext  
```

#### 2.2 示例合约开发

```js
// HelloWorld.sol
pragma solidity ^0.4.25;

contract HelloWorld{
    string name;

    function HelloWorld(){
       name = "Hello, World!";
    }

    function get()constant returns(string){
        return name;
    }

    function set(string n){
    	name = n;
    }
}
```

上述源码为solidity编写的HelloWorld合约， 本章节会实现一个相同功能的预编译合约，通过step by step使用户对预编译合约编写有直观的认识。
示例的c++[源码路径](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/libprecompiled/extension/HelloWorldPrecompiled.cpp)：

```cpp
    libprecompiled/extension/HelloWorldPrecompiled.h
    libprecompiled/extension/HelloWorldPrecompiled.cpp
```

##### 2.2.1 分配合约地址  

参照地址分配空间，HelloWorld预编译合约的地址分配为：
```
0x5001
```

##### 2.2.2 定义合约接口  

需要实现HelloWorld合约的功能，接口与HelloWorld接口相同， HelloWorldPrecompiled的辅助合约：
```js
pragma solidity ^0.4.25;

contract HelloWorld {
    function get() public constant returns(string) {}
    function set(string _m) {}
}
```

##### 2.2.3 设计存储结构  

HelloWorldPrecompiled需要存储set的字符串值，所以涉及到存储操作，需要 设计存储的表结构。

表名： ```_ext_hello_world_```

表结构：

|key       | value
----------|------------
|hello_key | hello_value



该表只存储一对键值对，key字段为hello_key，value字段为hello_value 存储对应的字符串值，可以通过set(string)接口修改，通过get()接口获取。

##### 2.2.4 实现调用逻辑  
添加HelloWorldPrecompiled类，重载call函数，实现所有接口的调用行为，[call函数源码](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/libprecompiled/extension/HelloWorldPrecompiled.cpp#L66)。

```cpp
// file HelloWorldPrecompiled.h
// file HelloWorldPrecompiled.cpp
bytes HelloWorldPrecompiled::call(dev::blockverifier::ExecutiveContext::Ptr _context,
    bytesConstRef _param, Address const& _origin)
{
    PRECOMPILED_LOG(TRACE) << LOG_BADGE("HelloWorldPrecompiled") << LOG_DESC("call")
                           << LOG_KV("param", toHex(_param));

    // parse function name
    uint32_t func = getParamFunc(_param);
    bytesConstRef data = getParamData(_param);
    bytes out;
    dev::eth::ContractABI abi;

    Table::Ptr table = openTable(_context, HELLO_WORLD_TABLE_NAME);
    if (!table)
    {
        // table is not exist, create it.
        table = createTable(_context, HELLO_WORLD_TABLE_NAME, HELLOWORLD_KEY_FIELD,
            HELLOWORLD_VALUE_FIELD, _origin);
        if (!table)
        {
            PRECOMPILED_LOG(ERROR) << LOG_BADGE("HelloWorldPrecompiled") << LOG_DESC("set")
                                   << LOG_DESC("open table failed.");
            out = abi.abiIn("", CODE_NO_AUTHORIZED);
            return out;
        }
    }
    if (func == name2Selector[HELLO_WORLD_METHOD_GET])
    {   // get() function call
        // default retMsg
        std::string retValue = "Hello World!";

        auto entries = table->select(HELLOWORLD_KEY_FIELD_NAME, table->newCondition());
        if (0u != entries->size())
        {
            auto entry = entries->get(0);
            retValue = entry->getField(HELLOWORLD_VALUE_FIELD);
            PRECOMPILED_LOG(ERROR) << LOG_BADGE("HelloWorldPrecompiled") << LOG_DESC("get")
                                   << LOG_KV("value", retValue);
        }
        out = abi.abiIn("", retValue);
    }
    else if (func == name2Selector[HELLO_WORLD_METHOD_SET])
    {   // set(string) function call

        std::string strValue;
        abi.abiOut(data, strValue);
        auto entries = table->select(HELLOWORLD_KEY_FIELD_NAME, table->newCondition());
        auto entry = table->newEntry();
        entry->setField(HELLOWORLD_KEY_FIELD, HELLOWORLD_KEY_FIELD_NAME);
        entry->setField(HELLOWORLD_VALUE_FIELD, strValue);

        int count = 0;
        if (0u != entries->size())
        {  // update
            count = table->update(HELLOWORLD_KEY_FIELD_NAME, entry, table->newCondition(),
                std::make_shared<AccessOptions>(_origin));
        }
        else
        {  // insert
            count = table->insert(
                HELLOWORLD_KEY_FIELD_NAME, entry, std::make_shared<AccessOptions>(_origin));
        }

        if (count == CODE_NO_AUTHORIZED)
        {  //  permission denied
            PRECOMPILED_LOG(ERROR) << LOG_BADGE("HelloWorldPrecompiled") << LOG_DESC("set")
                                   << LOG_DESC("non-authorized");
        }
        out = abi.abiIn("", count);
    }
    else
    {  // unkown function call
        PRECOMPILED_LOG(ERROR) << LOG_BADGE("HelloWorldPrecompiled") << LOG_DESC(" unkown func ")
                               << LOG_KV("func", func);
    }

    return out;
}
```

##### 2.2.5 注册合约

```cpp
// file         libblockverifier/ExecutiveContextFactory.cpp
// function     initExecutiveContext

context->setAddress2Precompiled(Address(0x5001), std::make_shared<dev::precompiled::HelloWorldPrecompiled>());  // HelloWorld precompiled 注册
```

### 三 调用

从用户角度，预编译合约与solidity合约的调用方式基本相同，唯一的区别是solidity合约在部署之后才能获取到调用的合约地址，预编译合约的地址为预分配，不用部署，可以直接使用。

#### 3.1 web3sdk调用  
web3sdk调用合约时，需要先将合约转换为java代码，对于预编译合约，需要使用辅助合约生成java代码，并且合约不需要部署，使用其分配地址，调用各个接口。[web3sdk应用构建案例参考](../tutorial/sdk_application.md)

#### 3.2 solidity调用  
solidity调用预编译合约时，以上文的HelloWorld预编译合约为例，使用HelloWorldHelper合约对其进行调用：

```js
pragma solidity ^0.4.25;
contract HelloWorld {
    function get() public constant returns(string) {}
    function set(string _m) {}
}
```

```js
pragma solidity ^0.4.25;
import "./HelloWorld.sol";

contract HelloWorldHelper {
    HelloWorld hello;
    function HelloWorldHelper() {
        hello = HelloWorld(0x5001); // 调用HelloWorld预编译合约
    }
    function get() public constant returns(string) {
        return hello.get();
    }
    function set(string m) {
        hello.set(m);
    }
}
```
