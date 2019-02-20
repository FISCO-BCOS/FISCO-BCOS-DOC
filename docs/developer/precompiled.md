[# Precompile合约开发

## 一. 简介
[预编译合约(precompiled contract)](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/6b15a14b346f5369a262c74bda5bc2b0fd2012f9/docs/design/virtualMachine/precompiled.md)是一项以太坊原生支持的功能, FISCO-BCOS在此基础上发展了一套功能强大并且容易拓展的precompiled合约框架。  
precompiled合约是内置在底层使用c++实现的合约代码, 一方面从用户使用角度来看, precompiled合约地址固定, solidity合约地址在部署时生成, 除此外两者有相同的使用方式, 因此precompiled合约并不会给用户添加额外的使用成本; 从开发者角度,precompiled是一种实现简单并且极具拓展性的合约执行引擎,用户可以在不了解EVM实现细节情况下通过precompiled拓展改进交易执行流程(eg：引入DAG,实现交易并行)。  
本文作为一篇入门指导, 旨在指引用户如何在FISCO-BCOS实现自己的precompiled合约, 并且能够与EVM或者客户端进行交互。

## 二. FISCO-BCOS内置precompiled合约

FISCO-BCOS已经实现的precompied合约包括：
配置管理合约
节点管理合约
[CRUD合约](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/developer/crud.md)
[CNS合约](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/design/features/CNS_contract_name_service.md)
[权限管理合约](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/design/security_control/node_access_management.md)

源码位于：libprecompiled目录
```
// FISCO-BCOS/libprecompiled

SystemConfigPrecompiled.h       // 配置管理
SystemConfigPrecompiled.cpp
ConsensusPrecompiled.h      // 节点管理
ConsensusPrecompiled.cpp
CRUDPrecompiled.h       //CRUD
CRUDPrecompiled.cpp
CNSPrecompiled.h        //CNS
CNSPrecompiled.cpp
AuthorityPrecompiled.h      //权限管理
AuthorityPrecompiled.cpp
```

### 三. 实现precompiled合约
#### 3.1 基本步骤
实现一个新的precopiled合约的流程包括：

- 定义合约接口  
设计一个有相同ABI接口的solidity合约, 只定义接口。

- 定义存储表  
包括表名与表结构,参考[存储](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/design/storage/storage.md)。
**注意**:不涉及存储可以省略该步骤。  

- 分配地址  
调用合约类型(solidity或者precompiled)根据地址来区分, 地址划分如下：

<<<<<<< HEAD:docs/developer/precompile.md
以太坊precompiled及预留 | 系统配置及预留 | 用户 | precompiled临时地址 | solidity合约地址
---------|----------|--------------|------|---------
0x0000 - 0x0fff | 0x1000 - 0x5000 | 0x5001 - 0xffff | 0x10000+ | 其他
=======
| 保留地址        | 系统配置以及保留地址 | 用户使用地址    | 临时precompiled合约地址 | solidity合约地址 |
| --------------- | -------------------- | --------------- | ----------------------- | ---------------- |
| 0x0000 - 0x0fff | 0x1000 - 0x5000      | 0x5001 - 0xffff | 0x10000+                | 其他             |
>>>>>>> 69051e97cb3e40dc823b191d12b44de808cc39f9:docs/developer/precompiled.md

 用户地址空间为0x5001 - 0xffff, 用户可以在这个范围内为新增的precompiled合约分配一个地址, precompiled合约地址必须唯一, 不可冲突。 
- 实现调用逻辑  
新增的precompiled合约类需要继承Precompiled, 在重载的call函数中实现precompiled合约各个接口调用的行为。
```
// FISCO-BCOS/libblockverifier/Precompiled.h
class Precompiled
{
    ... begin 省略 begin ....
    
    virtual bytes call(std::shared_ptr<ExecutiveContext> context, bytesConstRef param,
        Address const& origin = Address()) = 0;
    
    ... end 省略 end ...
};
```

- 注册合约
将合约注册到执行上下文, 目前已经注册的precompiled合约：
```
// FISCO-BCOS/libblockverifier/ExecutiveContextFactory.cpp
// ExecutiveContextFactory::initExecutiveContext

auto tableFactoryPrecompiled = std::make_shared<dev::blockverifier::TableFactoryPrecompiled>(); 
tableFactoryPrecompiled->setMemoryTableFactory(memoryTableFactory); // table precompiled
context->setAddress2Precompiled(Address(0x1000), std::make_shared<dev::precompiled::SystemConfigPrecompiled>()); // 系统配置
context->setAddress2Precompiled(Address(0x1001),tableFactoryPrecompiled);
context->setAddress2Precompiled(Address(0x1002),  std::make_shared<dev::precompiled::CRUDPrecompiled>()); //CRUD
context->setAddress2Precompiled(Address(0x1003),  std::make_shared<dev::precompiled::ConsensusPrecompiled>()); //共识节点管理
context->setAddress2Precompiled(Address(0x1004),  std::make_shared<dev::precompiled::CNSPrecompiled>()); //cns数据管理
context->setAddress2Precompiled(Address(0x1005), std::make_shared<dev::precompiled::AuthorityPrecompiled>()); //权限管理
```

#### 3.2 step by step sample  
本章节通过precompiled方式实现一个HelloWorld.sol的功能, 通过step by step带大家对precompiled合约编写有个直观的了解。  
该示例的c++源码位于：FISCO-BCOS/examples/HelloWorldPrecompiled.h HelloWorldPrecompiled.cpp
```
//HelloWorld.sol
pragma solidity ^0.4.2;

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

##### 3.2.1 合约接口  
需要实现HelloWorld.sol的功能, 接口与HelloWorld.sol接口相同
```
contract HelloWorld {
    function get() public constant returns(string);
    function set(string _m);
}
```

##### 3.2.2 表结构  
HelloWorld中需要存储单个字符串, 因此需要设计表结构. 

表名： \_\_test_hello_world\_\_  
表结构：
| key       | value       |
| --------- | ----------- |
| hello_key | hello_value |

该表只存储一对键值对, key字段为hello_key, value字段为hello_value 存储对应的字符串值, 可以通过set(string)接口修改, 通过get()接口获取.

##### 3.2.3 入口地址
```
0x5001
```

##### 3.2.4 实现调用逻辑  
HelloWorldPrecompiled类, 重载call函数, 实现所有接口的调用行为.
```
//file HelloWorldPrecompiled.h
class HelloWorldPrecompiled : public dev::blockverifier::Precompiled
{
public:
    ... begin 省略  begin ...
    
    bytes call(dev::blockverifier::ExecutiveContext::Ptr context, bytesConstRef param,
        Address const& origin = Address()) override;
        
    ... end 省略  end ...
};

//file HelloWorldPrecompiled.cpp
bytes HelloWorldPrecompiled::call(
    dev::blockverifier::ExecutiveContext::Ptr context, bytesConstRef param, Address const& origin)
{
    // 函数名解析
    uint32_t func = getParamFunc(param);
    // 参数解析
    bytesConstRef data = getParamData(param);
    // 返回值
    bytes out;
    
    if (func == name2Selector[HELLO_WORLD_METHOD_GET])
    {  // get() function call operation
    }
    else if (func == name2Selector[DAG_TRANSFER_METHOD_SAV_STR_UINT])
    {  // set(string) function call operation
    }
    else
    {  // unkown function call
        
    }
    return out;
}

```

##### 3.2.5 注册合约
```
// FISCO-BCOS/libblockverifier/ExecutiveContextFactory.cpp
// ExecutiveContextFactory::initExecutiveContext

context->setAddress2Precompiled(Address(0x5001), std::make_shared<dev::precompiled::HelloWorldPrecompiled>()); //HelloWorld precompiled 注册
```

##### 3.2.6 其他流程  
编译, 搭链, 测试

### 四. 调用 
#### 4.1 web3sdk调用
#### 4.2 solidity调用

## 参考链接
[precompiled设计介绍](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/6b15a14b346f5369a262c74bda5bc2b0fd2012f9/docs/design/virtual_machine/precompiled.md)  
[FISCO-BCOS搭链](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/manual/build_chain.md)  
[控制台](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/manual/console.md)
](# Precompiled合约开发

## 一. 简介
预编译合约(precompiled contract)是一项以太坊原生支持的功能, FISCO-BCOS在此基础上发展了一套功能强大并且容易拓展的precompiled合约框架。  
precompiled合约在底层使用c++实现,用户需要实现precomipled合约,需要有一定的c++技能,也需要用对底层运行原理有一定的了解。本文作为一篇入门指导, 旨在指引用户如何在FISCO-BCOS实现自己的precompiled合约, 并且能够与EVM或者客户端进行交互。

## 二. 实现precompiled合约  
### 2.1 基本步骤
实现一个新的precopiled合约的流程包括：
- 定义合约接口  
precompiled合约的接口, 与solidity的接口定义格式完全相同, 因此定义接口时可以完成对应的solidity合约设计, 将接口的函数体置空即可.

- 定义表结构  
不涉及存储操作可以省略该步骤.   

- 分配入口地址  
调用合约类型(solidity或者precompiled)根据地址来区分, 目前的地址分配如下：

预留地址 | 系统配置及预留 | 用户 | precompiled临时地址 | solidity合约地址
---------|----------|--------------|------|---------
0x0000 - 0x0fff | 0x1000 - 0x5000 | 0x5001 - 0xffff | 0x10000+ | 其他

 用户地址空间为0x5001 - 0xffff, 用户可以在这个范围内为新增的precompiled合约分配一个地址, precompiled合约地址不冲突即可。

- 实现调用逻辑  
新增的precompiled合约需要继承Precompiled合约, 在重载的call函数中实现precompiled合约各个接口调用的行为。
```
// FISCO-BCOS/libblockverifier/Precompiled.h
class Precompiled
{
    ... begin 省略 begin ....
    
    virtual bytes call(std::shared_ptr<ExecutiveContext> context, bytesConstRef param,
        Address const& origin = Address()) = 0;
    
    ... end 省略 end ...
};
```

- 注册合约

将合约注册到执行上下文, 目前已经注册的precompiled合约：
```
// FISCO-BCOS/libblockverifier/ExecutiveContextFactory.cpp
// ExecutiveContextFactory::initExecutiveContext

auto tableFactoryPrecompiled = std::make_shared<dev::blockverifier::TableFactoryPrecompiled>(); 
tableFactoryPrecompiled->setMemoryTableFactory(memoryTableFactory); // table precompiled
context->setAddress2Precompiled(Address(0x1000), std::make_shared<dev::precompiled::SystemConfigPrecompiled>()); // 系统配置
context->setAddress2Precompiled(Address(0x1001),tableFactoryPrecompiled);
context->setAddress2Precompiled(Address(0x1002),  std::make_shared<dev::precompiled::CRUDPrecompiled>()); //CRUD
context->setAddress2Precompiled(Address(0x1003),  std::make_shared<dev::precompiled::ConsensusPrecompiled>()); //共识节点管理
context->setAddress2Precompiled(Address(0x1004),  std::make_shared<dev::precompiled::CNSPrecompiled>()); //cns数据管理
context->setAddress2Precompiled(Address(0x1005), std::make_shared<dev::precompiled::AuthorityPrecompiled>()); //权限管理
```

### 2.2 step by step sample  
本章节通过precompiled方式实现一个HelloWorld.sol的功能, 通过step by step带大家对precompiled合约编写有个直观的了解。  
该示例的c++源码位于：FISCO-BCOS/examples/HelloWorldPrecompiled.h HelloWorldPrecompiled.cpp
```
//HelloWorld.sol
pragma solidity ^0.4.25;
contract HelloWorld {
    string m;
    function HelloWorld() {
        m = "Hello World!";
    }
    
    function get() public constant returns(string) {
        return m;
    }
    
    function set(string _m) public {
        m = _m;
    }
}
```

#### 2.2.1 合约接口  
需要实现HelloWorld.sol的功能, 接口与HelloWorld.sol接口相同
```
contract HelloWorld {
    function get() public constant returns(string);
    function set(string _m);
}
```

#### 2.2.2 表结构  
HelloWorld中需要存储单个字符串, 因此需要设计表结构. 

表名： \_\_test_hello_world\_\_  
表结构：
key | value
----|-----
hello_key | hello_value

该表只存储一对键值对, key字段为hello_key, value字段为hello_value 存储对应的字符串值, 可以通过set(string)接口修改, 通过get()接口获取.

#### 2.2.3 入口地址
```
0x5001
```

#### 2.2.4 实现调用逻辑  
HelloWorldPrecompiled类, 重载call函数, 实现所有接口的调用行为.
```
//file HelloWorldPrecompiled.h
class HelloWorldPrecompiled : public dev::blockverifier::Precompiled
{
public:
    ... begin 省略  begin ...
    
    bytes call(dev::blockverifier::ExecutiveContext::Ptr context, bytesConstRef param,
        Address const& origin = Address()) override;
        
    ... end 省略  end ...
};

//file HelloWorldPrecompiled.cpp
bytes HelloWorldPrecompiled::call(
    dev::blockverifier::ExecutiveContext::Ptr context, bytesConstRef param, Address const& origin)
{
    // 函数名解析
    uint32_t func = getParamFunc(param);
    // 参数解析
    bytesConstRef data = getParamData(param);
    // 返回值
    bytes out;
    
    if (func == name2Selector[HELLO_WORLD_METHOD_GET])
    {  // get() function call operation
    }
    else if (func == name2Selector[DAG_TRANSFER_METHOD_SAV_STR_UINT])
    {  // set(string) function call operation
    }
    else
    {  // unkown function call
        
    }
    return out;
}

```

#### 2.2.5 注册合约
```
// FISCO-BCOS/libblockverifier/ExecutiveContextFactory.cpp
// ExecutiveContextFactory::initExecutiveContext

context->setAddress2Precompiled(Address(0x5001), std::make_shared<dev::precompiled::HelloWorldPrecompiled>()); //HelloWorld precompiled 注册
```

#### 2.2.6 其他流程  
编译, 搭链, 测试

## 三. 调用 
### 3.1 web3sdk调用
### 3.2 solidity调用

## 附录： 参考链接
[precompiled设计](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/6b15a14b346f5369a262c74bda5bc2b0fd2012f9/docs/design/virtualMachine/precompiled.md)  
[环境搭建](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/manual/buildchain.md)    
[控制台](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/manual/console.md)
)