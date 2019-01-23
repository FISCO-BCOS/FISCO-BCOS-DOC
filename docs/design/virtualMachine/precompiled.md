## Precompiled

Precompiled合约提供一种使用C++编写合约的方法，合约逻辑与数据分离，相比于solidity合约具有更好的性能，且可以通过修改底层代码实现合约升级。Precompiled合约支持交易并发处理，大幅提升交易处理吞吐量。预编译合约采用C++实现，内置于底层系统中，区块链自动识别调用合约的交易互斥信息，构建DAG依赖，规划出一个高效的并行交易执行路径。最佳情况下，性能提升可达到CPU核心数的倍数。

### 1. Precompiled合约与Solidity合约对比

||Precompiled合约|Solidity合约|
|:---|:---|:---|
|地址|固定地址，代码中定义|部署时确定|
|合约代码|数据与合约分离，可升级|不可更改|
|执行|C++底层执行，性能更高，可实现并行|EVM虚拟机|

### 2. 模块架构

Precompiled的架构如下图所示：
- 区块验证器在执行交易的时候会根据被调用合约的地址来判断类型。地址1-4表示以太坊预编译合约，地址0x1000-0x10000是C++预编译合约，其他地址是EVM合约。

![模块架构](../../../images/precompiled/architecture.png)

### 3. 关键流程

- 执行Precompiled合约时首先需要根据合约地址获取到预编译合约的对象。
- 每个预编译合约对象都会实现`call`接口，预编译合约的具体逻辑在该接口中实现。
- `call`根据交易的abi编码，获取到`Function Selector`和参数，然后执行对应的逻辑。

![](../../../images/precompiled/process.png)

### 3. 接口定义

每个Precompiled合约都必须实现自己的`call`接口，接口接受三个参数，分别是`ExecutiveContext`执行上下文、`bytesConstRef`参数的abi编码和外部账户地址，其中外部账户地址用于判断是否具有写权限。
```cpp
class ExecutiveContext;
class Precompiled : public std::enable_shared_from_this<Precompiled>
{
public:
    typedef std::shared_ptr<Precompiled> Ptr;

    virtual ~Precompiled(){};

    virtual std::string toString(std::shared_ptr<ExecutiveContext>) { return ""; }

    virtual bytes call(std::shared_ptr<ExecutiveContext> context, bytesConstRef param,
        Address const& origin = Address()) = 0;

    virtual uint32_t getParamFunc(bytesConstRef param);

    virtual uint32_t getFuncSelector(std::string const& _functionName);
    virtual bytesConstRef getParamData(bytesConstRef param);
protected:
    std::map<std::string, uint32_t> name2Selector;
    std::shared_ptr<dev::storage::Table> openTable(
        std::shared_ptr<dev::blockverifier::ExecutiveContext> context,
        const std::string& tableName);
};
```
