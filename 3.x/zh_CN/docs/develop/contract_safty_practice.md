# 9. 智能合约安全实践

智能合约安全是指在设计、编码、部署、运行和维护智能合约的全生命周期中，采取措施确保合约的安全性和可靠性，防止恶意攻击、漏洞利用或错误操作导致的资产损失或系统崩溃。

本文从设计模式出发详细讲述智能合约在各个阶段应该使用的策略、推荐实践方式以及安全保障措施。

## 1. 智能合约设计模式

[智能合约编写之 Solidity的设计模式](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/articles/3_features/35_contract/solidity_design_patterns.html)

作者：储雨知｜FISCO BCOS 核心开发者

随着区块链技术发展，越来越多的企业与个人开始将区块链与自身业务相结合。区块链所具有的独特优势，例如，数据公开透明、不可篡改，可以为业务带来便利。但与此同时，也存在一些隐患。数据的公开透明，意味着任何人都可以读取；不可篡改，意味着信息一旦上链就无法删除，甚至合约代码都无法被更改。除此之外，合约的公开性、回调机制，每一个特点都可被利用，作为攻击手法，稍有不慎，轻则合约形同虚设，重则要面临企业机密泄露的风险。所以，在业务合约上链前，需要预先对合约的安全性、可维护性等方面作充分考虑。幸运的是，通过近些年Solidity语言的大量实践，开发者们不断提炼和总结，已经形成了一些"设计模式"，来指导应对日常开发常见的问题。

2019年，IEEE收录了维也纳大学一篇题为《Design Patterns For Smart Contracts In the Ethereum Ecosystem》的论文。这篇论文分析了那些火热的Solidity开源项目，结合以往的研究成果，整理出了18种设计模式。这些设计模式涵盖了安全性、可维护性、生命周期管理、鉴权等多个方面。

| 类型               | 模式                                                                                                                      |
|--------------------|---------------------------------------------------------------------------------------------------------------------------|
| Security           | 1. Checks-Effects-Interaction<br/>2. Emergency Stop<br/>3. Speed Bump<br/>4. Rate Limit<br/>5. Mutex<br/>6. Balance Limit |
| Maintenance        | 7. Data Segregation<br/>8. Satellite<br/>9. Contract Register<br/>10. Contract Relay                                      |
| Lifecycle          | 11. Mortal<br/>12. Automatic Deprecation                                                                                  |
| Authorization      | 13. Ownership<br/>14. Access Restriction                                                                                  |
| Action And Control | 15. Pull Payment<br/>16. Commit And Reveal<br/>17. State Machine<br/>18. Oracle                                           |

接下来，本文将从这18种设计模式中选择最为通用常见的进行介绍，这些设计模式在实际开发经历中得到了大量检验。

### 1.1 Checks-Effects-Interaction - 保证状态完整，再做外部调用

该模式是编码风格约束，可有效避免重放攻击。通常情况下，一个函数可能包含三个部分：

- Checks：参数验证
- Effects：修改合约状态
- Interaction：外部交互

这个模式要求合约按照Checks-Effects-Interaction的顺序来组织代码。它的好处在于进行外部调用之前，Checks-Effects已完成合约自身状态所有相关工作，使得状态完整、逻辑自洽，这样外部调用就无法利用不完整的状态进行攻击了。回顾前文的AddService合约，并没有遵循这个规则，在自身状态没有更新完的情况下去调用了外部代码，外部代码自然可以横插一刀，让_adders[msg.sender]=true永久不被调用，从而使require语句失效。我们以checks-effects-interaction的角度审阅原来的代码：

```solidity
    //Checks
    require(_adders[msg.sender] == false, "You have added already");
    //Effects    
    _count++;
    //Interaction    
    AdderInterface adder = AdderInterface(msg.sender);
    adder.notify();
    //Effects
    _adders[msg.sender] = true;
```

只要稍微调整顺序，满足Checks-Effects-Interaction模式，悲剧就得以避免：

```solidity
    //Checks
    require(_adders[msg.sender] == false, "You have added already");
    //Effects    
    _count++;
    _adders[msg.sender] = true;
    //Interaction    
    AdderInterface adder = AdderInterface(msg.sender);
    adder.notify();
```

由于_adders映射已经修改完毕，当恶意攻击者想递归地调用addByOne，require这道防线就会起到作用，将恶意调用拦截在外。虽然该模式并非解决重入攻击的唯一方式，但依然推荐开发者遵循。

### 1.2 Mutex - 禁止递归

Mutex模式也是解决重入攻击的有效方式。它通过提供一个简单的修饰符来防止函数被递归调用：

```solidity
contract Mutex {
    bool locked;
    modifier noReentrancy() {
        //防止递归
        require(!locked, "Reentrancy detected");
        locked = true;
        _;
        locked = false;
    }

    //调用该函数将会抛出Reentrancy detected错误
    function some() public noReentrancy{
        some();
    }
}
```

在这个例子中，调用some函数前会先运行noReentrancy修饰符，将locked变量赋值为true。如果此时又递归地调用了some，修饰符的逻辑会再次激活，由于此时的locked属性已为true，修饰符的第一行代码会抛出错误。

### 1.3 Data segregation - 数据与逻辑相分离

了解该设计模式之前，先看看下面这个合约代码：

```solidity
contract Computer{

    uint private _data;

    function setData(uint data) public {
        _data = data;
    }

    function compute() public view returns(uint){
        return _data * 10;
    }
}
```

此合约包含两个能力，一个是存储数据(setData函数)，另一个是运用数据进行计算(compute函数）。如果合约部署一段时间后，发现compute写错了，比如不应是乘以10，而要乘以20，就会引出前文如何升级合约的问题。这时，可以部署一个新合约，并尝试将已有数据迁移到新的合约上，但这是一个很重的操作，一方面要编写迁移工具的代码，另一方面原先的数据完全作废，空占着宝贵的节点存储资源。

所以，预先在编程时进行模块化十分必要。如果我们将"数据"看成不变的事物，将"逻辑"看成可能改变的事物，就可以完美避开上述问题。Data Segregation（意为数据分离）模式很好地实现了这一想法。该模式要求一个业务合约和一个数据合约：数据合约只管数据存取，这部分是稳定的；而业务合约则通过数据合约来完成逻辑操作。

结合前面的例子，我们将数据读写操作专门转移到一个合约DataRepository中：

```solidity
contract DataRepository{

    uint private _data;

    function setData(uint data) public {
        _data = data;
    }

    function getData() public view returns(uint){
        return _data;
    }
}
```

计算功能被单独放入一个业务合约中：

```solidity
contract Computer{
    DataRepository private _dataRepository;
    constructor(address addr){
        _dataRepository =DataRepository(addr);
    }

    //业务代码
    function compute() public view returns(uint){
        return _dataRepository.getData() * 10;
    }    
}
```

这样，只要数据合约是稳定的，业务合约的升级就很轻量化了。比如，当我要把Computer换成ComputerV2时，原先的数据依然可以被复用。

### 1.4 Satellite - 分解合约功能

一个复杂的合约通常由许多功能构成，如果这些功能全部耦合在一个合约中，当某一个功能需要更新时，就不得不去部署整个合约，正常的功能都会受到波及。Satellite模式运用单一职责原则解决上述问题，提倡将合约子功能放到子合约里，每个子合约（也称为卫星合约）只对应一个功能。当某个子功能需要修改，只要创建新的子合约，并将其地址更新到主合约里即可，其余功能不受影响。

举个简单的例子，下面这个合约的setVariable功能是将输入数据进行计算（compute函数），并将计算结果存入合约状态_variable：

```solidity
contract Base {
    uint public _variable;

    function setVariable(uint data) public {
        _variable = compute(data);
    }

    //计算
    function compute(uint a) internal returns(uint){
        return a * 10;        
    }
}
```

如果部署后，发现compute函数写错，希望乘以的系数是20，就要重新部署整个合约。但如果一开始按照Satellite模式操作，则只需部署相应的子合约。

首先，我们先将compute函数剥离到一个单独的卫星合约中去：

```solidity
contract Satellite {
    function compute(uint a) public returns(uint){
        return a * 10;        
    }
}
```

然后，主合约依赖该子合约完成setVariable：

```solidity
contract Base {
    uint public _variable;

    function setVariable(uint data) public {
        _variable = _satellite.compute(data);
    }

     Satellite _satellite;
    //更新子合约（卫星合约）
    function updateSatellite(address addr) public {
        _satellite = Satellite(addr);
    }
}
```

这样，当我们需要修改compute函数时，只需部署这样一个新合约，并将它的地址传入到Base.updateSatellite即可：

```solidity
contract Satellite2{
    function compute(uint a) public returns(uint){
        return a * 20;        
    }    
}
```

### 1.5 Contract Registry - 跟踪最新合约

在Satellite模式中，如果一个主合约依赖子合约，在子合约升级时，主合约需要更新对子合约的地址引用，这通过updateXXX来完成，例如前文的updateSatellite函数。这类接口属于维护性接口，与实际业务无关，过多暴露此类接口会影响主合约美观，让调用者的体验大打折扣。Contract Registry设计模式优雅地解决了这个问题。在该设计模式下，会有一个专门的合约Registry跟踪子合约的每次升级情况，主合约可通过查询此Registyr合约取得最新的子合约地址。卫星合约重新部署后，新地址通过Registry.update函数来更新。

```solidity
contract Registry{

    address _current;
    address[] _previous;

    //子合约升级了，就通过update函数更新地址
    function update(address newAddress) public{
        if(newAddress != _current){
            _previous.push(_current);
            _current = newAddress;
        }
    } 

    function getCurrent() public view returns(address){
        return _current;
    }
}
```

主合约依赖于Registry获取最新的卫星合约地址。

```solidity
contract Base {
    uint public _variable;

    function setVariable(uint data) public {
        Satellite satellite = Satellite(_registry.getCurrent());
        _variable = satellite.compute(data);
    }

    Registry private _registry = //...;
}
```

### 1.6 Contract Relay - 代理调用最新合约

该设计模式所解决问题与Contract Registry一样，即主合约无需暴露维护性接口就可调用最新子合约。该模式下，存在一个代理合约，和子合约享有相同接口，负责将主合约的调用请求传递给真正的子合约。卫星合约重新部署后，新地址通过SatelliteProxy.update函数来更新。

```solidity
contract SatelliteProxy{
    address _current;
    function compute(uint a) public returns(uint){
        Satellite satellite = Satellite(_current);   
        return satellite.compute(a);
    } 
    
    //子合约升级了，就通过update函数更新地址
    function update(address newAddress) public{
        if(newAddress != _current){
            _current = newAddress;
        }
    }   
}


contract Satellite {
    function compute(uint a) public returns(uint){
        return a * 10;        
    }
}
```

主合约依赖于SatelliteProxy：

```solidity
contract Base {
    uint public _variable;

    function setVariable(uint data) public {
        _variable = _proxy.compute(data);
    }
    SatelliteProxy private _proxy = //...;
}
```

### 1.7 Mortal - 允许合约自毁

字节码中有一个selfdestruct指令，用于销毁合约。所以只需要暴露出自毁接口即可：

```solidity
contract Mortal{

    //自毁
    function destroy() public{
        selfdestruct(msg.sender);
    } 
}
```

### 1.8 Automatic Deprecation - 允许合约自动停止服务

如果你希望一个合约在指定期限后停止服务，而不需要人工介入，可以使用Automatic Deprecation模式。

``` solidity
contract AutoDeprecated{

    uint private _deadline;

    function setDeadline(uint time) public {
        _deadline = time;
    }

    modifier notExpired(){
        require(now <= _deadline);
        _;
    }

    function service() public notExpired { 
        //some code    
    } 
}
```

当用户调用service，notExpired修饰符会先进行日期检测，这样，一旦过了特定时间，调用就会因过期而被拦截在notExpired层。

### 1.9 Ownership检查

前文中有许多管理性接口，这些接口如果任何人都可调用，会造成严重后果，例如上文中的自毁函数，假设任何人都能访问，其严重性不言而喻。所以，一套保证只有特定账户能够访问的权限控制设计模式显得尤为重要。

对于权限的管控，可以采用Ownership模式。该模式保证了只有合约的拥有者才能调用某些函数。首先需要有一个Owned合约：

```solidity
contract Owned{

    address public _owner;

    constructor() {
        _owner = msg.sender;
    }    

    modifier onlyOwner(){
        require(_owner == msg.sender);
        _;
    }
}
```

如果一个业务合约，希望某个函数只由拥有者调用，该怎么办呢？如下：

```solidity
contract Biz is Owned{
    function manage() public onlyOwner{
    }
}
```

这样，当调用manage函数时，onlyOwner修饰符就会先运行并检测调用者是否与合约拥有者一致，从而将无授权的调用拦截在外。

### 1.10 延迟秘密泄露

这类模式一般针对具体场景使用，这节将主要介绍基于隐私的编码模式和与链外数据交互的设计模式。

链上数据都是公开透明的，一旦某些隐私数据上链，任何人都可看到，并且再也无法撤回。Commit And Reveal模式允许用户将要保护的数据转换为不可识别数据，比如一串哈希值，直到某个时刻再揭示哈希值的含义，展露真正的原值。以投票场景举例，假设需要在所有参与者都完成投票后再揭示投票内容，以防这期间参与者受票数影响。我们可以看看，在这个场景下所用到的具体代码：

```solidity
contract CommitReveal {

    struct Commit {
        string choice; 
        string secret; 
        uint status;
    }

    mapping(address => mapping(bytes32 => Commit)) public userCommits;
    event LogCommit(bytes32, address);
    event LogReveal(bytes32, address, string, string);

    function commit(bytes32 commit) public {
        Commit storage userCommit = userCommits[msg.sender][commit];
        require(userCommit.status == 0);
        userCommit.status = 1; // committed
        emit LogCommit(commit, msg.sender);
    }

    function reveal(string choice, string secret, bytes32 commit) public {
        Commit storage userCommit = userCommits[msg.sender][commit];
        require(userCommit.status == 1);
        require(commit == keccak256(choice, secret));
        userCommit.choice = choice;
        userCommit.secret = secret;
        userCommit.status = 2;
        emit LogReveal(commit, msg.sender, choice, secret);
    }
}
```

## 2. 智能合约编程攻略

[智能合约编写之Solidity的编程攻略](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/articles/3_features/35_contract/solidity_design_programming_strategy.html)

作者：毛嘉宇｜FISCO BCOS 核心开发者

**“如无必要，勿增实体”。**

- 需要分布式协作的重要数据才上链，非必需数据不上链；
- 敏感数据脱敏或加密后上链（视数据保密程度选择符合隐私保护安全等级要求的加密算法）；
- 链上验证，链下授权。

在使用区块链时，开发者不需要将所有业务和数据都放到链上。相反，“好钢用在刀刃上”，智能合约更适合被用在分布式协作的业务场景中。

### 2.1 精简函数变量

如果在智能合约中定义了复杂的逻辑，特别是合约内定义了复杂的函数入参、变量和返回值，就会在编译的时候碰到以下错误：

```shell
Compiler error: Stack too deep, try removing local variables.
```

这也是社区中的高频技术问题之一。造成这个问题的原因就是EVM所设计用于最大的栈深度为16。所有的计算都在一个栈内执行，对栈的访问只限于其顶端，限制方式为：允许拷贝最顶端16个元素中的一个到栈顶，或者将栈顶元素和下面16个元素中的一个交换。所有其他操作都只能取最顶的几个元素，运算后，把结果压入栈顶。当然可以把栈上的元素放到存储或内存中。但无法只访问栈上指定深度的那个元素，除非先从栈顶移除其他元素。如果一个合约中，入参、返回值、内部变量的大小超过了16个，显然就超出了栈的最大深度。因此，我们可以使用结构体或数组来封装入参或返回值，达到减少栈顶元素使用的目的，从而避免此错误。例如以下代码，通过使用bytes数组来封装了原本16个bytes变量。

```solidity
function doBiz(bytes[] paras) public {
        require(paras.length >= 16);
        // do something
}
```

### 2.2 保证参数和行为符合预期

在编写智能合约时，一定要注意对合约参数和行为的检查，尤其是那些对外部开放的合约函数。Solidity提供了require、revert、assert等关键字来进行异常的检测和处理。一旦检测并发现错误，整个函数调用会被回滚，所有状态修改都会被回退，就像从未调用过函数一样。以下分别使用了三个关键字，实现了相同的语义。

```solidity
require(_data == data, "require data is valid");

if(_data != data) { revert("require data is valid"); }

assert(_data == data);
```

不过，这三个关键字一般适用于不同的使用场景：

- require：最常用的检测关键字，用来验证输入参数和调用函数结果是否合法。
- revert：适用在某个分支判断的场景下。
- assert: 检查结果是否正确、合法，一般用于函数结尾。

在一个合约的函数中，可以使用函数修饰器来抽象部分参数和条件的检查。在函数体内，可以对运行状态使用if-else等判断语句进行检查，对异常的分支使用revert回退。在函数运行结束前，可以使用assert对执行结果或中间状态进行断言检查。在实践中，推荐使用require关键字，并将条件检查移到函数修饰器中去；这样可以让函数的职责更为单一，更专注到业务逻辑中。同时，函数修饰器等条件代码也更容易被复用，合约也会更加安全、层次化。

以一个水果店库存管理系统为例，设计一个水果超市的合约。这个合约只包含了对店内所有水果品类和库存数量的管理，setFruitStock函数提供了对应水果库存设置的函数。在这个合约中，我们需要检查传入的参数，即水果名称不能为空。

```solidity
pragma solidity ^0.4.25;

contract FruitStore {
    mapping(bytes => uint) _fruitStock;
    modifier validFruitName(bytes fruitName) {
        require(fruitName.length > 0, "fruite name is invalid!");
        _;
    }
    function setFruitStock(bytes fruitName, uint stock) validFruitName(fruitName) external {
        _fruitStock[fruitName] = stock;
    }
}
```

如上所述，我们添加了函数执行前的参数检查的函数修饰器。同理，通过使用函数执行前和函数执行后检查的函数修饰器，可以保证智能合约更加安全、清晰。智能合约的编写需要设置严格的前置和后置函数检查，来保证其安全性。

### 2.3 严控函数的执行权限

如果说智能合约的参数和行为检测提供了静态的合约安全措施，那么合约权限控制的模式则提供了动态访问行为的控制。由于智能合约是发布到区块链上，所有数据和函数对所有参与者都是公开透明的，任一节点参与者都可发起交易，无法保证合约的隐私。因此，合约发布者必须对函数设计严格的访问限制机制。Solidity提供了函数可见性修饰符、修饰器等语法，灵活地使用这些语法，可帮助构建起合法授权、受控调用的智能合约系统。还是以刚才的水果合约为例。现在getStock提供了查询具体水果库存数量的函数。

```solidity
pragma solidity ^0.4.25;

contract FruitStore {
    mapping(bytes => uint) _fruitStock;
    modifier validFruitName(bytes fruitName) {
        require(fruitName.length > 0, "fruite name is invalid!");
        _;
    }
    function getStock(bytes fruit) external view returns(uint) {
        return _fruitStock[fruit];
    }
    function setFruitStock(bytes fruitName, uint stock) validFruitName(fruitName) external {
        _fruitStock[fruitName] = stock;
    }
}
```

水果店老板将这个合约发布到了链上。但是，发布之后，setFruitStock函数可被任何其他联盟链的参与者调用。虽然联盟链的参与者是实名认证且可事后追责；但一旦有恶意攻击者对水果店发起攻击，调用setFruitStock函数就能任意修改水果库存，甚至将所有水果库存清零，这将对水果店正常经营管理产生严重后果。因此，设置某些预防和授权的措施很必要：对于修改库存的函数setFruitStock，可在函数执行前对调用者进行鉴权。类似的，这些检查可能会被多个修改数据的函数复用，使用一个onlyOwner的修饰器就可以抽象此检查。owner字段代表了合约的所有者，会在合约构造函数中被初始化。使用public修饰getter查询函数，就可以通过_owner()函数查询合约的所有者。

```solidity
contract FruitStore {
    address public  _owner;
    mapping(bytes => uint) _fruitStock;
  
    constructor() public {
        _owner = msg.sender;
    } 
  
    modifier validFruitName(bytes fruitName) {
        require(fruitName.length > 0, "fruite name is invalid!");
        _;
    }
    // 鉴权函数修饰器
    modifier onlyOwner() { 
        require(msg.sender == _owner, "Auth: only owner is authorized.");
        _; 
    }
    function getStock(bytes fruit) external view returns(uint) {
        return _fruitStock[fruit];
    }
    // 添加了onlyOwner修饰器
    function setFruitStock(bytes fruitName, uint stock) 
        onlyOwner validFruitName(fruitName) external {
        _fruitStock[fruitName] = stock;
    }
}
```

这样一来，我们可以将相应的函数调用权限检查封装到修饰器中，智能合约会自动发起对调用者身份验证检查，并且只允许合约部署者来调用setFruitStock函数，以此保证合约函数向指定调用者开放。

### 2.4 抽象通用的业务逻辑

分析上述FruitStore合约，我们发现合约里似乎混入了奇怪的东西。参考单一职责的编程原则，水果店库存管理合约多了上述函数功能检查的逻辑，使合约无法将所有代码专注在自身业务逻辑中。对此，我们可以抽象出可复用的功能，利用Solidity的继承机制继承最终抽象的合约。基于上述FruitStore合约，可抽象出一个BasicAuth合约，此合约包含之前onlyOwner的修饰器和相关功能接口。

```solidity
contract BasicAuth {
    address public _owner;

    constructor() public {
        _owner = msg.sender;
    }

    function setOwner(address owner)
        public
        onlyOwner
{
        _owner = owner;
    }

    modifier onlyOwner() { 
        require(msg.sender == _owner, "BasicAuth: only owner is authorized.");
        _; 
    }
}
```

FruitStore可以复用这个修饰器，并将合约代码收敛到自身业务逻辑中。

```solidity
import "./BasicAuth.sol";

contract FruitStore is BasicAuth {
    mapping(bytes => uint) _fruitStock;

    function setFruitStock(bytes fruitName, uint stock) 
        onlyOwner validFruitName(fruitName) external {
        _fruitStock[fruitName] = stock;
    }
}
```

这样一来，FruitStore的逻辑被大大简化，合约代码更精简、聚焦和清晰。

### 2.5 预防私钥的丢失

在区块链中调用合约函数的方式有两种：内部调用和外部调用。出于隐私保护和权限控制，业务合约会定义一个合约所有者。假设用户A部署了FruitStore合约，那上述合约owner就是部署者A的外部账户地址。这个地址由外部账户的私钥计算生成。但是，在现实世界中，私钥泄露、丢失的现象比比皆是。一个商用区块链DAPP需要严肃考虑私钥的替换和重置等问题。这个问题最为简单直观的解决方法是添加一个备用私钥。这个备用私钥可支持权限合约修改owner的操作，代码如下：

```solidity
ontract BasicAuth {
    address public  _owner;
    address public _bakOwner;
    
    constructor(address bakOwner) public {
        _owner = msg.sender;
        _bakOwner = bakOwner;
    }

    function setOwner(address owner)
        public
        canSetOwner
{
        _owner = owner;
    }

    function setBakOwner(address owner)
        public
        canSetOwner
{
        _bakOwner = owner;
    }

    // ...
    
    modifier isAuthorized() { 
        require(msg.sender == _owner || msg.sender == _bakOwner, "BasicAuth: only owner or back owner is authorized.");
        _; 
    }
}
```

这样，当发现私钥丢失或泄露时，我们可以使用备用外部账户调用setOwner重置账号，恢复、保障业务正常运行。

### 2.6 合理预留事件

迄今为止，我们已实现强大灵活的权限管理机制，只有预先授权的外部账户才能修改合约owner属性。不过，仅通过上述合约代码，我们无法记录和查询修改、调用函数的历史记录和明细信息。而这样的需求在实际业务场景中比比皆是。比如，FruitStore水果店需要通过查询历史库存修改记录，计算出不同季节的畅销与滞销水果。

一种方法是依托链下维护独立的台账机制。不过，这种方法存在很多问题：保持链下台账和链上记录一致的成本开销非常高；同时，智能合约面向链上所有参与者开放，一旦其他参与者调用了合约函数，相关交易信息就存在不能同步的风险。针对此类场景，Solidity提供了event语法。event不仅具备可供SDK监听回调的机制，还能用较低的gas成本将事件参数等信息完整记录、保存到区块中。FISCO BCOS社区中，也有WEBASE-Collect-Bee这样的工具，在事后实现区块历史事件信息的完整导出。

[WEBASE-Collect-Bee工具参考](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Collect-Bee/index.html)

基于上述权限管理合约，我们可以定义相应的修改权限事件，其他事件以此类推。

```solidity
event LogSetAuthority (Authority indexed authority, address indexed from);
}
```

接下来，可以调用相应的事件：

```solidity
function setAuthority(Authority authority)
        public
        auth
{
        _authority = authority;
        emit LogSetAuthority(authority, msg.sender);
    }
```

当setAuthority函数被调用时，会同时触发LogSetAuthority，将事件中定义的Authority合约地址以及调用者地址记录到区块链交易回执中。当通过控制台调用setAuthority方法时，对应事件LogSetAuthority也会被打印出来。基于WEBASE-Collect-Bee，我们可以导出所有该函数的历史信息到数据库中。也可基于WEBASE-Collect-Bee进行二次开发，实现复杂的数据查询、大数据分析和数据可视化等功能。

### 2.7 遵循安全编程规范

每一门语言都有其相应的编码规范，我们需要尽可能严格地遵循Solidity官方编程风格指南，使代码更利于阅读、理解和维护，有效地减少合约的bug数量。[Solidity官方编程风格指南参考](https://solidity.readthedocs.io/en/latest/style-guide.html)。除了编程规范，业界也总结了很多安全编程指南，例如重入漏洞、数据结构溢出、随机数误区、构造函数失控、为初始化的存储指针等等。重视和防范此类风险，采用业界推荐的安全编程规范至关重要，例如[Solidity官方安全编程指南](https://solidity.readthedocs.io/en/latest/security-considerations.html)。同时，在合约发布上线后，还需要注意关注、订阅Solidity社区内安全组织或机构发布的各类安全漏洞、攻击手法，一旦出现问题，及时做到亡羊补牢。

对于重要的智能合约，有必要引入审计。现有的审计包括了人工审计、机器审计等方法，通过代码分析、规则验证、语义验证和形式化验证等方法保证合约安全性。虽然本文通篇都在强调，模块化和重用被严格审查并广泛验证的智能合约是最佳的实践策略。但在实际开发过程，这种假设过于理想化，每个项目或多或少都会引入新的代码，甚至从零开始。不过，我们仍然可以视代码的复用程度进行审计分级，显式地标注出引用的代码，将审计和检查的重点放在新代码上，以节省审计成本。

### 2.8 使用SmartDev应用插件

SmartDev包含了一套开放、轻量的开发组件集，覆盖智能合约的开发、调试、应用开发等环节，包括智能合约库（SmartDev-Contract）、智能合约编译插件（SmartDev-SCGP）和应用开发脚手架（SmartDev-Scaffold）。开发者可根据自己的情况自由选择相应的开发工具，提升开发效率。

详情可以看：[SmartDev应用开发组件](./smartdev_index.md)

## 3. 智能合约部署权限控制

部署合约的权限控制将由治理委员会统一控制，治理委员会将以投票表决的形式控制部署权限。治理委员会对某个部署权限的提案通过后，将会主动调用固定地址0x1005预编译合约的部署权限写接口，这些写接口也限定只能治理委员会合约调用。

部署权限记录在BFS目录/apps下，这代表着允许在/apps目录下的写权限。

治理委员可以通过控制台进行部署合约权限控制等操作，详情请看 [设置部署权限类型提案](../operation_and_maintenance/console/console_commands.html#setdeployauthtypeproposal) 、[开启部署权限提案](../operation_and_maintenance/console/console_commands.html#opendeployauthproposal) 、[关闭部署权限提案](../operation_and_maintenance/console/console_commands.html#closedeployauthproposal)

在检查部署权限时将会对交易发起地址tx.origin进行校验，若没有权限则会返回错误码 -5000。即，会对用户部署合约、用户通过合约部署合约都进行校验。

## 4. 智能合约执行权限控制

合约管理员可以对固定地址0x1005的预编译合约发起交易，对合约接口的访问ACL进行读写。

在执行合约接口的访问ACL的写操作时，将会确定交易发起人msg.sender是否为合约权限表记录的合约管理员，若不是则会拒绝。

合约管理员可以通过控制台对合约接口访问ACL的写操作等操作，详情请看：[合约管理员专用命令](../operation_and_maintenance/console/console_commands.html#setmethodauth)

在检查合约调用权限时将会对交易发起地址tx.origin和消息发送者msg.sender进行校验，若没有权限则会返回错误码 -5000。即，会对用户调用合约、用户通过合约调用合约、合约调用合约都进行校验。

## 5. 智能合约运维

智能合约在运维时主要关注智能合约的数据状态、智能合约升级、智能合约冻结、智能合约销毁。

### 5.1 智能合约升级

在Solidity中，一旦合约部署发布后，其代码就无法被修改，只能通过发布新合约去改动代码。假如数据存储在老合约，就会出现所谓的“孤儿数据”问题，新合约将丢失之前运行的历史业务数据。这种情况，开发者可以考虑将老合约数据迁移到新合约中，但此操作至少存在两个问题：

1. 迁移数据会加重区块链的负担，产生资源浪费和消耗，甚至引入安全问题；
2. 牵一发而动全身，会引入额外的迁移数据逻辑，增加合约复杂度。

一种更合理的方式是抽象一层独立的合约存储层。这个存储层只提供合约读写的最基本方法，而不包含任何业务逻辑。在这种模式中，存在三种合约角色：

- 数据合约：在合约中保存数据，并提供数据的操作接口。
- 管理合约：设置控制权限，保证只有控制合约才有权限修改数据合约。
- 控制合约：真正需要对数据发起操作的合约。

具体的代码示例如下：

**数据合约**

```solidity
contract FruitStore is BasicAuth {
    address _latestVersion; 
    mapping(bytes => uint) _fruitStock;
    
    modifier onlyLatestVersion() {
       require(msg.sender == _latestVersion);
        _;
    }

    function upgradeVersion(address newVersion) public {
        require(msg.sender == _owner);
        _latestVersion = newVersion;
    }
    
    function setFruitStock(bytes fruit, uint stock) onlyLatestVersion external {
        _fruitStock[fruit] = stock;
    }
}
```

**管理合约**

```solidity
contract Admin is BasicAuth {
    function upgradeContract(FruitStore fruitStore, address newController) isAuthorized external {
        fruitStore.upgradeVersion(newController);
    }
}
```

**控制合约**

```solidity
contract FruitStoreController is BasicAuth {
    function upgradeStock(bytes fruit, uint stock) isAuthorized external {
        fruitStore.setFruitStock(fruit, stock);
    }
}
```

一旦函数的控制逻辑需要变更，开发者只需修改FruitStoreController控制合约逻辑，部署一个新合约，然后使用管理合约Admin修改新的合约地址参数就可轻松完成合约升级。这种方法可消除合约升级中因业务控制逻辑改变而导致的数据迁移隐患。但天下没有免费的午餐，这种操作需要在可扩展性和复杂性之间需要做基本的权衡。首先，数据和逻辑的分离降低了运行性能。其次，进一步封装增加了程序复杂度。最后，越是复杂的合约越会增加潜在攻击面，简单的合约比复杂的合约更安全。

**通用数据结构——数据升级**

到目前为止，还存在一个问题，假如数据合约中的数据结构本身需要升级怎么办？

例如，在FruitStore中，原本只保存了库存信息，现在由于水果销售店生意发展壮大，一共开了十家分店，需要记录每家分店、每种水果的库存和售出信息。在这种情况下，一种解决方案是采用外部关联管理方式：创建一个新的ChainStore合约，在这个合约中创建一个mapping，建立分店名和FruitStore的关系。

此外，不同分店需要创建一个FruitStore的合约。为了记录新增的售出信息等数据，我们还需要新建一个合约来管理。假如在FruitStore中可预设一些不同类型的reserved字段，可帮助规避新建售出信息合约的开销，仍然复用FruitStore合约。但这种方式在最开始会增加存储开销。一种更好的思路是抽象一层更为底层和通用的存储结构。代码如下：

```solidity
contract commonDB  is BasicAuth {
    mapping(bytes => uint) _uintMapping;
    
    function getUint(bytes key) external view returns(uint) {
        return _uintMapping[key];
    }

    function setUint(bytes key, uint value) isAuthorized onlyLatestVersion external {
        _uintMapping[key] = value;
    }

}
```

类似的，我们可加入所有数据类型变量，帮助commonDB应对和满足不同的数据类型存储需求。相应的控制合约可修改如下：

```solidity
contract FruitStoreControllerV2 is BasicAuth {
    function upgradeStock(bytes32 storeName, bytes32 fruit, uint stock) 
        isAuthorized external {
        commonDB.setUint(sha256(storeName, fruit), stock);
        uint result = commonDB.getUint(sha256(storeName, fruit));
    }
}
```

使用以上存储的设计模式，可显著提升合约数据存储灵活性，保证合约可升级。众所周知，Solidity既不支持数据库，使用代码作为存储entity，也无法提供更改schema的灵活性。但是，通过这种KV设计，可以使存储本身获得强大的可扩展性。总之，**没有一个策略是完美的，优秀的架构师善于权衡**。智能合约设计者需要充分了解各种方案的利弊，并基于实际情况选择合适的设计方案。

**使用CRUD或者KV存储合约数据**

需要存储的数据都使用CRUD数据接口进行存储，CRUD的数据是通过节点共识并持久存储在链上的。详情请参考[使用CRUD预编译合约开发应用](../contract_develop/c++_contract/use_crud_precompiled.md)，[使用KV存储预编译合约开发应用](../contract_develop/c++_contract/use_kv_precompiled.md)

### 5.2 智能合约冻结、解冻

在合约出现数据异常或者出现大量访问异常时，合约管理员可以对该智能合约进行冻结操作，防止其他用户继续访问该合约。

合约管理员可以对固定地址0x1005的预编译合约发起交易，对合约的状态进行读写。

在执行合约状态的写操作时，将会确定交易发起人msg.sender是否为合约权限表记录的合约管理员，若不是则会拒绝。

```eval_rst
.. important::
   兼容性说明：合约生命周期管理废止操作只能在节点版本3.2以上进行。
```

合约管理员也可以通过控制台对合约进行冻结等操作，详情请看：[冻结合约命令](../operation_and_maintenance/console/console_commands.html#freezecontract)、[解冻合约命令](../operation_and_maintenance/console/console_commands.html#unfreezecontract)

### 5.3 智能合约废止

当合约不再使用，且数据不再供访问，用户可以使用预留的selfdestruct进行合约销毁，合约管理员也可以使用合约废止功能主动将合约状态设定为废止。

**selfdestruct**

字节码中有一个selfdestruct指令，用于销毁合约。所以只需要暴露出自毁接口即可。**注意：** 该过程不可逆，请酌情考虑后果。

```solidity
contract Mortal{

    //自毁
    function destroy() public{
        selfdestruct(msg.sender);
    } 
}
```

**合约废止**

```eval_rst
.. important::
   兼容性说明：合约生命周期管理废止操作只能在节点版本3.2以上进行。
```

**注意：** 该过程不可逆，请酌情考虑后果。

在执行合约状态的写操作时，将会确定交易发起人msg.sender是否为合约权限表记录的合约管理员，若不是则会拒绝。

合约管理员也可以通过控制台对合约进行冻结等操作，详情请看：[冻结合约命令](../operation_and_maintenance/console/console_commands.html#freezecontract)