
# 预编译合约开发

## 一. 简介

预编译（precompiled）合约(预编译合约)是一项以太坊原生支持的功能：在底层使用c++代码实现特定功能的合约，提供给EVM模块调用。FISCO-BCOS继承并且拓展了这种特性，在此基础上发展了一套功能强大并易于拓展的框架[[precompiled设计原理]](../design/virtual_machine/precompiled.md)。   
本文作为一篇入门指导，旨在指引用户如何实现自己的precompiled合约,并实现precompiled合约的调用。

## 二. 实现预编译合约  

### 2.1 流程
实现预编译合约的流程：
![流程](../../images/precompiled/create_process.png)

- **分配合约地址**  

调用solidity合约或者预编译合约需要根据合约地址来区分，地址空间划分：

| 以太坊precompiled | 保留          | FISCO-BCOS precompied | FISCO-BCOS预留 | 用户分配区间    | CRUD临时合约 | solidity |
|-------------------|---------------|-----------------------|----------------|-----------------|--------------|----------|
| 0x0001-0x0004     | 0x0005-0x0fff | 0x1000-0x1006         | 0x1007-0x5000  | 0x5001 - 0xffff | 0x10000+     | 其他     |

 用户分配地址空间为```0x5001-0xffff```,用户需要为新添加的预编译合约分配一个未使用的地址，**预编译合约地址必须唯一， 不可冲突**。 
 
FISCO-BCOS中实现的precompild合约列表以及地址分配:


| 地址   | 功能   | 文档链接      | 源码([libprecompiled目录](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/release-2.0.1/libprecompiled)) |
|--------|--------|----------|---------|
| 0x1000 | 系统参数管理         | [系统参数](../manual/build_group.html) | SystemConfigPrecompiled.cpp |
| 0x1001 | CRUD合约操作存储接口 | [CRUD](../developer/crud_sol.html) | TableFactoryPrecompiled.cpp |
| 0x1002 | CRUD合约            | [CRUD](../developer/crud_sol.html) | CRUDPrecompiled.cpp |
| 0x1003 | 共识节点管理         | [共识节点管理](../manual/build_group.html) | ConsensusPrecompiled.cpp |
| 0x1004 | CNS功能  | [CNS文档](../design/features/CNS_contract_name_service.html) | CNSPrecompiled.cpp |
| 0x1005 | 存储表权限管理 | [权限管理文档](../design/security_control/node_management.html) | AuthorityPrecompiled.cpp |

- **定义合约接口**  

同solidity合约，设计合约时需要首先确定合约的ABI接口， precomipiled合约的ABI接口规则与solidity完全相同，[solidity ABI 链接](https://solidity.readthedocs.io/en/develop/abi-spec.html)。  
 
> 定义预编译合约接口时，通常需要定义一个有相同接口的solidity合约，并且将所有的接口的函数体置空，这个合约我们称为预编译合约的**辅助合约**，辅助合约在调用预编译合约时需要使用。 

```
    pragma solidity ^0.4.25;
    contract Contract_Name {
        function interface0(parameters ... ) {}
        ....
        function interfaceN(parameters ... ) {}
    }
```  

- **设计存储结构**  

预编译合约涉及存储操作时，需要确定存储的表信息(表名与表结构,存储数据在FISCO-BCOS中会统一抽象为表结构)， [存储结构](../design/storage/storage.md)。  
**注意：不涉及存储操作可以省略该流程**  
 
- **实现调用逻辑**  

实现新增合约的调用逻辑，需要新实现一个c++类，该类需要继承[Precompiled](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/libblockverifier/Precompiled.h#L37), 重载call函数， 在call函数中实现各个接口的调用行为。  
```
    //libblockverifier/Precompiled.h
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

### 2.2 step by step sample  
```
//HelloWorld.sol
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
示例的c++[源码路径](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/extension/HelloWorldPrecompiled.cpp)：
```
    extension/HelloWorldPrecompiled.h 
    extension/HelloWorldPrecompiled.cpp
```

#### 2.2.1 分配合约地址  

参照地址分配空间，HelloWorld预编译合约的地址分配为：
```
0x5001
```

#### 2.2.2 定义合约接口  

需要实现HelloWorld合约的功能，接口与HelloWorld接口相同， HelloWorldPrecompiled的辅助合约：
```
pragma solidity ^0.4.25;

contract HelloWorld {
    function get() public constant returns(string) {}
    function set(string _m) {}
}
```

#### 2.2.3 设计存储结构  

HelloWorldPrecompiled需要存储set的字符串值，所以涉及到存储操作，需要 设计存储的表结构。

表名： ```_ext_hello_world_```    

表结构：

|key       | value
----------|------------
|hello_key | hello_value



该表只存储一对键值对，key字段为hello_key，value字段为hello_value 存储对应的字符串值，可以通过set(string)接口修改，通过get()接口获取。

#### 2.2.4 实现调用逻辑  
添加HelloWorldPrecompiled类，重载call函数，实现所有接口的调用行为，[call函数源码](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/release-2.0.1/extension/HelloWorldPrecompiled.cpp#L85)。
```
//file HelloWorldPrecompiled.h
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
// file         libblockverifier/ExecutiveContextFactory.cpp
// function     initExecutiveContext

context->setAddress2Precompiled(Address(0x5001), std::make_shared<dev::precompiled::HelloWorldPrecompiled>()); //HelloWorld precompiled 注册
```

#### 2.2.6 其他流程  
[源码编译](../manual/install.md)

 [环境搭链](../manual/build_chain.md)

## 三 调用 

从用户角度，预编译合约与solidity合约的调用方式基本相同，唯一的区别是solidity合约在部署之后才能获取到调用的合约地址，预编译合约的地址为预分配，不用部署，可以直接使用。

### 3.1 web3sdk调用  
web3sdk调用合约时，需要先将合约转换为java代码，对于预编译合约，需要使用辅助合约生成java代码，并且合约不需要部署，使用其分配地址，调用各个接口。

### 3.2 solidity调用  
solidity调用预编译合约时，以上文的HelloWorld预编译合约为例，使用HelloWorldHelper合约对其进行调用：
```
pragma solidity ^0.4.25;
contract HelloWorld {
    function get() public constant returns(string) {}
    function set(string _m) {}
}
```

```
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