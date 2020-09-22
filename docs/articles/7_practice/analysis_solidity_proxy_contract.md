# 解析Solidity代理合约的实现方式

作者：朱立派｜深耕区块链的开发者、探索者

## 一、什么是代理合约？

这里提供一个代理合约的Demo，Proxy.sol代码如下：

```
pragma solidity >=0.4.24 <0.7.0;
contract Proxy {
  address direction;

  function update(address a) external {
    direction= a;
  }
  
function() external {
    address destination = direction;
    assembly {
         calldatacopy(0, 0, calldatasize())
         let result := call(gas,destination,0, 0, calldatasize(), 0, 0)
         returndatacopy(0, 0, returndatasize())
         switch result
         case 0 {revert(0, returndatasize())}
         default {return (0, returndatasize())}
     }
 }
}
```

这个合约的功能包含两个：

- 调用合约“update”方法，更新“direction”地址变量；
- 其他任意调用将会被代理到“direction”地址上，事务性操作。

注：本合约测试基于FISCO BCOS平台，不考虑“payable”关键字；且实际部署时，需要增加调用权限判断！

## 二、代理合约的实现原理

代理合约主要基于“Fallback函数”和“内联汇编”实现。

### Fallback函数

Fallback函数的定义如下：

合约可以有一个未命名的函数。这个函数不能有参数也不能有返回值。 如果在一个到合约的调用中，没有其他函数与给定的函数标识符匹配（或没有提供调用数据），那么这个函数（fallback 函数）会被执行。

因此当外部交易发送到“Proxy.sol”合约地址上时，如果不是调用“update”方法，那么都会调用到“fallback”方法上。
我们在“fallback”方法中，使用“内联汇编”的方式实现代理逻辑。

### 内联汇编

内联汇编程序由 *assembly { ... }* 来标记，在这些大括号内可以使用操作码直接对栈虚拟机进行操作。

简单介绍一下“fallback”方法中操作码的作用：

- **calldatasize**：获取调用数据的字节数大小；
- **calldatacopy**：拷贝指定字节数到指定内存位置；
- **call**：调用操作，以指定内存处的数据作为输入调用destination地址，返回调用结果；
- **returndatasize**：获取返回数据的字节数
- **returndatacopy**：拷贝返回数据到指定内存位置；
- **switch**：判断调用是否成功，发生错误时返回 0，正确结束返回 1；
- **revert**：调用失败则回滚；
- **return**：调用成功返回结果数据

## 三、代理合约的调用方式

我们需要首选部署被代理合约，然后在Proxy.sol中以被代理合约部署后的地址作为入参调用“update”方法，这样就可以实现代理调用了。

这里提供一个简单的合约作为被代理合约，Test.sol代码如下：

```
pragma solidity >=0.4.24 <0.7.0;
contract Test {
  string s;

  function get() public view returns(string memory i) {
    i = s;
  }
  
  function set(string memory i) public {
    s = i;
  }
}
```



#### 3.1 部署合约

以下步骤假设已经安装了FISCO节点以及console控制台；控制台“console/contracts/solidity/”目录下包含上述Proxy.sol、Test.sol合约文件。

###### 3.1.1 启动控制台
```$bash console/start.sh```

###### 3.1.2 在控制台下部署被调用合约

记录合约地址；

```[group:1]> deploy Test ```

>contract address: 0x30e8f90c33f22a4cf928988ee715417e51c52030


###### 3.1.3 在控制台下部署代理合约

记录合约地址；

```[group:1]> deploy Proxy ```

>contract address: 0xc84f5c9fde0821fe4c3f8b878a78c086efe21a2f



###### 3.1.4 调用代理合约，更新代理地址

请根据实际部署的合约地址更新参数；

```[group:1]> call Proxy 0xc84f5c9fde0821fe4c3f8b878a78c086efe21a2f update "0x30e8f90c33f22a4cf928988ee715417e51c52030"```

>transaction hash: 0xde69dd546313d0a9361ad0b960417857f3b037b5910a5fcc53328ae346184668


#### 3.2 调用合约

到这一步，我们只需要向Proxy.sol合约发送交易就可以实现代理调用了，但是如何正确访问Proxy.sol合约的Fallback方法呢？

回顾Fallback方法内联汇编中有一行关键代码：

```let result := call(gas,destination,0, 0, calldatasize(), 0, 0)```

这是实现代理调用的关键逻辑。

我们想要实现的目的是请求“Proxy.sol”合约地址，但是请求的数据会被代理到“Test.sol”合约地址上。
如何实现我们的目的呢？
如果控制台成功部署合约，那么在路径“console/contracts/console/java/temp/”下会生成“Proxy.java”、“Test.java”两个java文件。
检查这两个java文件，我们发现“Proxy.java”包含“update”的相关方法，但是没有和“fallback”有关的方法，这意味着我们无法直接通过“Proxy.java”访问“Proxy.sol”合约的“fallback”方法。

实际上，我们是通过“Test.java”提供的接口构造合约请求数据（包括私钥签名、RLP编码等），然后访问“Proxy.sol”合约地址；再通过“Proxy.sol”合约的“fallback”函数将请求数据代理到“Test.sol”合约地址上，实现调用。


**以下介绍代理调用中的关键流程：**


###### 3.2.1 在JavaSDK端加载合约
加载Test.java合约文件，以“Proxy.sol”合约地址作为地址参数；

```
String address = “0xc84f5c9fde0821fe4c3f8b878a78c086efe21a2f”//Proxy.sol合约部署后的地址
Test contract = Test.load(address , web3j, credentials, new StaticGasProvider(gasPrice, gasLimit));
```

###### 3.2.2 在JavaSDK端发起调用请求
调用请求会发送到“Proxy.sol”合约，但输入数据是按照“Test.sol”的ABI进行编码的；

```TransactionReceipt receipt = contract.set(“invoke success”).send();```

###### 3.2.3 “Proxy.sol”合约接受调用请求
如果不是访问“update”方法，合约中的“fallback”方法会接受调用请求；

```let result := call(gas,destination,0, 0, calldatasize(), 0, 0)```

这一步，将输入数据代理到“destination”地址上，即“Test.sol”的合约地址；
注意：输入数据是按照“Test.java”文件编码的，将符合“Test.sol”的函数签名。

###### 3.2.4 “Test.sol”合约接受调用请求
```
  function set(string memory i) public {
    s = i;
  }
```
执行合约逻辑，如果成功，依次返回成功响应到“Proxy.sol”合约、JavaSDK端。



## 四、小结

我们为什么要用代理合约的调用方式？

代理合约和反射类似；通过代理合约，我们可以免去大量方法的重复编写，可以平滑升级业务逻辑合约而始终对外保持接口一致。

大家可以一起去探索，如何使用代理合约的方法提升我们合约开发的简洁性。