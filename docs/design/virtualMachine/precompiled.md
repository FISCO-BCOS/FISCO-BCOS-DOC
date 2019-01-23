## Precompiled

Precompiled合约提供一种使用C++编写合约的方法，使用这种写法的只能合约逻辑使用C++实现，编译时确定，相比于solidity合约具有更好的性能。

### 1. Precompiled合约与Solidity合约对比

||Precompiled合约|Solidity合约|
|:---|:---|:---|
|地址|固定，C++代码中定义|部署时确定|
|合约代码|C++实现接口逻辑，可升级|部署时确定，不可更改|
|执行|C++底层执行，性能更高|EVM虚拟机|

### 2. 模块架构

![](../../../images/Precompiled_arch.png)

### 3. 关键流程



### 3. 接口定义

