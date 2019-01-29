## Precompile合约开发

### 一. 简介
预编译合约(precompiled contract)是一项以太坊原生支持的功能, FISCO-BCOS在此基础上发展了一套功能强大且易拓展的precompiled框架。 
precompiled合约的设计思路可以参考[precompiled设计](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/design/virtualMachine/precompiled.md)。   
本文作为一篇入门指导, 旨在指引用户如何在FISCO-BCOS实现自己的precompiled合约, 并且能够与EVM或者客户端进行交互。

### 二. 实现precompiled合约
#### 2.1 基本步骤
- 定义合约接口  
precompiled合约使用的接口, 与solidity的接口定义格式完全相同,  因此定义接口时可以先写个对应的solidity合约文件, 将接口的函数体都置空即可.

- 定义表的结构  
不涉及存储操作可以省略该步骤.   
FISCO-BCOS底层存储结构参见链接.

- 选择入口地址  
solidity或者precompiled调用根据地址来区分, 目前的地址分配如下：

保留地址 | 系统配置以及保留地址 | 用户使用地址 | 临时precompiled合约地址 | solidity合约地址
---------|----------|--------------|------|--------- 
0x0000 - 0x0fff | 0x1000 - 0x5000 | 0x5001 - 0xffff | 0x10000+ | 其他

 用户分配地址空间为0x5001 - 0xffff, 用户可以为此范围内为新增的precompiled合约选一个地址, 不同precompiled的地址不要冲突即可.

- 实现调用逻辑  
新增的precompiled合约需要继承Precompiled合约, 在重载的call函数中实现precompiled合约各个接口调用的行为.
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

- 注册合约到执行上下文

目前已经注册的precompiled合约：
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

#### 2.2 step by step sample  
本章节通过precompiled方式实现一个HelloWorld.sol(合约如下)的功能, 通过step by step带大家对precompiled合约编写有个直观的了解。  
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

##### 2.2.1 合约接口  
需要实现HelloWorld.sol的功能, 接口与HelloWorld.sol接口相同
```
contract HelloWorld {
    function get() public constant returns(string);
    function set(string _m);
}
```

##### 2.2.2 表结构  
HelloWorld中需要存储单个字符串, 因此需要设计表结构. 

表名： \_\_test_hello_world\_\_  
表结构：
key | value
----|-----
hello_key | hello_value

该表只存储一对键值对, key字段为hello_key, value字段为hello_value 存储对应的字符串值, 可以通过set(string)接口修改, 通过get()接口获取.

##### 2.2.3 入口地址
```
0x5001
```

##### 2.2.4 实现调用逻辑  
实现HelloWorldPrecompiled类
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

##### 2.2.5 注册合约
```
// FISCO-BCOS/libblockverifier/ExecutiveContextFactory.cpp
// ExecutiveContextFactory::initExecutiveContext

context->setAddress2Precompiled(Address(0x5001), std::make_shared<dev::precompiled::HelloWorldPrecompiled>()); //HelloWorld precompiled 注册
```

##### 2.2.6 其他流程  
编译, 搭链, 测试

### 三. 调用 
#### 3.1 web3sdk调用
#### 3.2 solidity调用
#### 3.3 precompiled调用

### 附录： 参考链接
[precompiled设计](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/6b15a14b346f5369a262c74bda5bc2b0fd2012f9/docs/design/virtualMachine/precompiled.md)  
[环境搭建](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/manual/buildchain.md)    
[控制台](https://github.com/FISCO-BCOS/FISCO-BCOS-DOC/blob/feature-2.0.0/docs/manual/console.md)
