# Solidity Programming Strategy for Smart Contract Writing

Author ： MAO Jiayu ｜ FISCO BCOS Core Developer

## **Preface** 

As a veteran code farmer who has been moving bricks for many years, I felt helpless when I first came into contact with Solidity: expensive computing and storage resources, rudimentary syntax features, maddening debug experience, almost barren class library support, inserting assembly statements if I didn't agree...... makes one wonder if it's been 9012 years and there's even this anti-human language.？For code farmers who are accustomed to using all kinds of increasingly "silly" class libraries and automated advanced frameworks, the process of learning Solidity is an endless journey of persuasion.。But as we learn more about the underlying technology of blockchain, we will gradually understand the design principles that must be strictly followed and the price that must be paid after weighing the Solidity language running on "The World Machine."。As the famous slogan in the Matrix: "Welcome to the dessert of the real," in the face of harsh and difficult circumstances, the most important thing is to learn how to adapt to the environment, preserve yourself and evolve quickly.。This article summarizes some of the Solidity programming strategy, looking forward to the readers will not hesitate to share the exchange, to achieve the effect of throwing bricks and mortar。

## The principle of the chain

**"Do not add entities unless necessary"。**

Based on the current development of blockchain technology and smart contracts, the following principles should be followed in the data chain:

- Important data that requires distributed collaboration is chained, and non-essential data is not chained.；
- Sensitive data is desensitized or encrypted and then linked (depending on the degree of data confidentiality, select an encryption algorithm that meets the requirements of the privacy protection security level)； 
- On-chain authentication, off-chain authorization。

When using blockchain, developers don't need to put all their business and data on the chain。Instead, "good steel is on the cutting edge," and smart contracts are more suitable for use in distributed collaboration business scenarios.。

## thin function variable

If complex logic is defined in a smart contract, especially if complex function parameters, variables, and return values are defined in the contract, you will encounter the following errors at compile time.

```
Compiler error: Stack too deep, try removing local variables.
```

This is also one of the high-frequency technical issues in the community.。The reason for this problem is that EVM is designed for a maximum stack depth of 16。All calculations are performed within a stack, and access to the stack is limited to the top of the stack in such a way as to allow one of the top 16 elements to be copied to the top of the stack, or to swap the top of the stack with one of the 16 elements below.。All other operations can only take the top few elements, and after the operation, the result is pushed to the top of the stack.。Of course, you can put the elements on the stack into storage or memory.。However, you cannot access only the element on the stack at the specified depth unless you first remove the other elements from the top of the stack。If the size of the input parameters, return values, and internal variables in a contract exceeds 16, it clearly exceeds the maximum depth of the stack.。Therefore, we can use structs or arrays to encapsulate input or return values to reduce the use of elements at the top of the stack, thereby avoiding this error。For example, the following code encapsulates the original 16 bytes variables by using the bytes array.。

```
function doBiz(bytes[] paras) public {
        require(paras.length >= 16);
        // do something
}
```

## Guaranteed parameters and behavior as expected

With the lofty ideal of "Code is law," geeks design and create smart contracts for blockchain。In the alliance chain, different participants can use smart contracts to define and write the logic of a part of a business or interaction to complete a part of a social or commercial activity.。

Compared to traditional software development, smart contracts have more stringent security requirements for function parameters and behavior。Mechanisms such as identity real names and CA certificates are provided in the federation chain to effectively locate and regulate all participants。However, smart contracts lack prior intervention mechanisms for vulnerabilities and attacks.。As the so-called word Breguet, if you do not rigorously check the smart contract input parameters or behavior, it may trigger some unexpected bugs.。

Therefore, when writing smart contracts, it is important to pay attention to the examination of contract parameters and behavior, especially those contract functions that are open to the outside world.。Solidity provides keywords such as require, revert, and assert to detect and handle exceptions.。Once the error is detected and found, the entire function call is rolled back and all state modifications are rolled back as if the function had never been called。The following uses three keywords to achieve the same semantics.。

```
require(_data == data, "require data is valid");

if(_data != data) { revert("require data is valid"); }

assert(_data == data);
```

However, these three keywords generally apply to different usage scenarios:

- require: The most commonly used detection keyword to verify whether the input parameters and the result of calling the function are legitimate.。
- revert: Applicable to a branch judgment scenario。
- assert: Check whether the result is correct and legal, generally used at the end of the function。

In a function of a contract, you can use the function decorator to abstract part of the parameter and condition checking。Within the function body, you can use if for the running state-Else and other judgment statements to check, the abnormal branch using revert fallback。You can use assert to check the execution result or intermediate state before the function runs。In practice, it is recommended to use the require keyword and move the condition check to the function decorator.；This allows the function to have more single responsibilities and focus more on the business logic.。At the same time, condition codes such as function modifiers are easier to reuse, and contracts are more secure and hierarchical.。

In this paper, we use a fruit store inventory management system as an example to design a fruit supermarket contract.。This contract only contains the management of all fruit categories and inventory quantities in the store, and the setFruitStock function provides a function corresponding to the fruit inventory settings.。In this contract, we need to check the incoming parameters, i.e. the fruit name cannot be empty。

```
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

As mentioned above, we added a function decorator for parameter checking before function execution。Similarly, by using function decorators that check before and after function execution, you can ensure that smart contracts are safer and clearer.。The writing of smart contracts requires strict pre-and post-function checks to ensure their security.。

## Strictly control the execution permission of functions

If the parameters and behavior detection of smart contracts provide static contract security measures, then the mode of contract permission control provides control of dynamic access behavior.。Since smart contracts are published on the blockchain, all data and functions are open and transparent to all participants, and any node participant can initiate a transaction, which does not guarantee the privacy of the contract.。Therefore, the contract publisher must design a strict access restriction mechanism for the function。Solidity provides syntax such as function visibility modifiers and modifiers, which can be used flexibly to help build a smart contract system with legal authorization and controlled calls.。Or take the fruit contract just now as an example.。Now getStock provides a function to query the inventory quantity of specific fruits.。

```
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

The fruit store owner posted the contract on the chain.。However, after publication, the setFruitStock function can be called by any other affiliate chain participant。Although the participants in the alliance chain are real-name authenticated and can be held accountable afterwards.；However, once a malicious attacker attacks the fruit store, calling the setFruitStock function can modify the fruit inventory at will, or even clear all the fruit inventory, which will have serious consequences for the normal operation and management of the fruit store.。Therefore, it is necessary to set up some prevention and authorization measures: for the function setFruitStock that modifies the inventory, the caller can be authenticated before the function executes.。Similarly, these checks may be reused by multiple functions that modify the data, using an onlyOwner decorator to abstract this check。The owner field represents the owner of the contract and is initialized in the contract constructor.。Using public to modify the getter query function, you can pass _ owner()function to query the owner of a contract。

```
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
    / / authentication function decorator
    modifier onlyOwner() { 
        require(msg.sender == _owner, "Auth: only owner is authorized.");
        _; 
    }
    function getStock(bytes fruit) external view returns(uint) {
        return _fruitStock[fruit];
    }
    / / added the onlyOwner decorator
    function setFruitStock(bytes fruitName, uint stock) 
        onlyOwner validFruitName(fruitName) external {
        _fruitStock[fruitName] = stock;
    }
}
```

In this way, we can encapsulate the corresponding function call permission check into the decorator, the smart contract will automatically initiate the caller authentication check, and only allow the contract deployer to call the setFruitStock function, thus ensuring that the contract function is open to the specified caller.。

## abstract generic business logic

Analyzing the above FruitStore contract, we found that there seems to be something strange mixed in with the contract.。Referring to the programming principle of single responsibility, the fruit store inventory management contract has more logic than the above function function check, so that the contract can not focus all the code in its own business logic.。In this regard, we can abstract reusable functions and use Solidity's inheritance mechanism to inherit the final abstract contract.。Based on the above FruitStore contract, a BasicAuth contract can be abstracted, which contains the previous onlyOwner's decorator and related functional interfaces.。

```
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

FruitStore can reuse this decorator and converge the contract code into its own business logic。

```
import "./BasicAuth.sol";

contract FruitStore is BasicAuth {
    mapping(bytes => uint) _fruitStock;

    function setFruitStock(bytes fruitName, uint stock) 
        onlyOwner validFruitName(fruitName) external {
        _fruitStock[fruitName] = stock;
    }
}
```

In this way, the logic of FruitStore is greatly simplified, and the contract code is more streamlined, focused and clear.。

## Prevent loss of private keys

There are two ways to call contract functions in the blockchain: internal calls and external calls.。For privacy protection and permission control, a business contract defines a contract owner。Suppose user A deploys the FruitStore contract, then the above contract owner is the external account address of deployer A.。This address is generated by the private key calculation of the external account.。However, in the real world, the phenomenon of private key leakage, loss abound。A commercial blockchain DAPP needs to seriously consider issues such as private key replacement and reset.。The simplest and most intuitive solution to this problem is to add an alternate private key。This alternate private key supports the operation of the permission contract modification owner. The code is as follows:

```
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

In this way, when we find that the private key is lost or leaked, we can use the standby external account to call setOwner to reset the account to restore and ensure the normal operation of the business.。

## interface-oriented programming

The above-mentioned private key backup concept is worthy of praise, but its specific implementation has certain limitations, in many business scenarios, it is too simple and crude.。For actual business scenarios, the dimensions and factors that need to be considered for the backup and preservation of private keys are much more complex, and the corresponding key backup strategies are more diversified.。Take fruit stores as an example, some chain fruit stores may want to manage private keys through brand headquarters, some may reset their accounts through social relationships, and some may bind a social platform management account...... Interface-oriented programming, without relying on specific implementation details, can effectively circumvent this problem。For example, we use the interface function to first define an abstract interface for judging permissions:

```
contract Authority {
    function canCall(
        address src, address dst, bytes4 sig
    ) public view returns (bool);
}
```

This canCall function covers the function caller address, the address of the target call contract, and the function signature, and the function returns the result of a bool。This contains all the necessary parameters for contract authentication。We can further modify the previous rights management contract and rely on the Authority interface in the contract, and when authenticated, the decorator calls the abstract methods in the interface.

```
contract BasicAuth {
    Authority  public  _authority;

    function setAuthority(Authority authority)
        public
        auth
    {
        _authority = authority;
    }

    modifier isAuthorized() { 
        require(auth(msg.sender, msg.sig), "BasicAuth: only owner or back owner is authorized.");
        _; 
    }
    
    function auth(address src, bytes4 sig) public view returns (bool) {
        if (src == address(this)) {
            return true;
        } else if (src == _owner) {
            return true;
        } else if (_authority == Authority(0)) {
            return false;
        } else {
            return _authority.canCall(src, this, sig);
        }
    }
}
```

In this way, we only need to flexibly define the contract that implements the canCall interface and define the specific judgment logic in the canCall method of the contract.。Business contracts, such as FruitStore, which inherit the BasicAuth contract, can be created with different judgment logic as long as the specific implementation contract is passed in.。

## Reasonable Reservation Event

So far, we have implemented a strong and flexible permission management mechanism, and only pre-authorized external accounts can modify the contract owner attribute.。However, with the above contract code alone, we cannot record and query the history and details of modifications and calls to functions.。And such needs abound in real business scenarios.。For example, FruitStore needs to check the historical inventory modification records to calculate the best-selling and slow-selling fruits in different seasons.。

One way is to rely on the chain to maintain an independent ledger mechanism.。However, there are many problems with this approach: the cost overhead of keeping the off-chain ledger and on-chain records consistent is very high.；At the same time, smart contracts are open to all participants in the chain, and once other participants call the contract function, there is a risk that the relevant transaction information will not be synchronized.。For such scenarios, Solidity provides the event syntax。Event not only has the mechanism for SDK listening callback, but also can record and save event parameters and other information to the block with low gas cost.。FISCO BCOS community, there is also WEBASE-Collect-A tool like Bee that enables the complete export of block history event information after the fact.。

[WEBASE-Collect-Bee Tool Reference](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Collect-Bee/index.html)

Based on the above permission management contract, we can define the corresponding permission modification event, other events and so on.。

```
event LogSetAuthority (Authority indexed authority, address indexed from);
}
```

Next, you can call the corresponding event:

```
function setAuthority(Authority authority)
        public
        auth
{
        _authority = authority;
        emit LogSetAuthority(authority, msg.sender);
    }
```

When the setAuthority function is called, LogSetAuthority is triggered at the same time, and the Authority contract address and caller address defined in the event are recorded in the blockchain transaction receipt.。When the setAuthority method is called from the console, the corresponding event LogSetAuthority is also printed。Based on WEBASE-Collect-Bee, we can export all the historical information of the function to the database.。Also available based on WEBASE-Collect-Bee for secondary development, to achieve complex data query, big data analysis and data visualization functions。

## Follow security programming specifications

Each language has its own coding specifications, and we need to follow Solidity's official programming style guidelines as strictly as possible to make the code easier to read, understand, and maintain, effectively reducing the number of contract bugs.。[Solidity Official Programming Style Guide Reference](https://solidity.readthedocs.io/en/latest/style-guide.html)。In addition to programming specifications, the industry has also summarized many secure programming guidelines, such as re-entry vulnerabilities, data structure overflows, random number errors, runaway constructors, storage pointers for initialization, and so on.。To address and prevent such risks, it is critical to adopt industry-recommended security programming specifications, such as the [Solidity Official Security Programming Guide](https://solidity.readthedocs.io/en/latest/security-considerations.html)。At the same time, after the contract is released and launched, you also need to pay attention to and subscribe to all kinds of security vulnerabilities and attack methods released by security organizations or institutions in the Solidity community, and make up for problems in a timely manner.。

For important smart contracts, it is necessary to introduce auditing。Existing audits include manual audits, machine audits and other methods to ensure contract security through code analysis, rule validation, semantic validation and formal validation.。Although emphasized throughout this article, modularity and reuse of smart contracts that are highly reviewed and widely validated are best practice strategies。But in the actual development process, this assumption is too idealistic, each project will more or less introduce new code, or even from scratch。However, we can still grade audits based on how much code is reused, explicitly label referenced code, and focus audits and inspections on new code to save on audit costs。

Finally, we need to constantly summarize and learn the best practices of our predecessors, dynamically and sustainably improve coding engineering, and continue to apply them to specific practices.。

### Accumulate and reuse mature code

The previous ideas in interface-oriented programming reduce code coupling, making contracts easier to extend and easier to maintain.。In addition to following this rule, there is another piece of advice: reuse existing code bases as much as possible.。Smart contracts are difficult to modify or withdraw after they are released, and when they are released on an open and transparent blockchain environment, they mean that bugs can cause more damage and risk than traditional software.。So, reusing some better and safer wheels is far better than rebuilding them.。In the open source community, there are already a large number of business contracts and libraries available, such as excellent libraries such as OpenZeppelin。If you can't find suitable reusable code in the open source world and past team code repositories, it is recommended to test and refine the code design as much as possible when writing new code。In addition, historical contract codes are regularly analyzed and reviewed to be templated for easy scaling and reuse。

For example, for the above BasicAuth, refer to Firewall Classic ACL(Access Control List)design, we can further inherit and extend BasicAuth to abstract the implementation of ACL contract control.。

```
contract AclGuard is BasicAuth {
    bytes4 constant public ANY_SIG = bytes4(uint(-1));
    address constant public ANY_ADDRESS = address(bytes20(uint(-1)));
    mapping (address => mapping (address => mapping (bytes4 => bool))) _acl;

    function canCall(
        address src, address dst, bytes4 sig
) public view returns (bool) {
        return _acl[src][dst][sig]
            || _acl[src][dst][ANY_SIG]
            || _acl[src][ANY_ADDRESS][sig]
            || _acl[src][ANY_ADDRESS][ANY_SIG]
            || _acl[ANY_ADDRESS][dst][sig]
            || _acl[ANY_ADDRESS][dst][ANY_SIG]
            || _acl[ANY_ADDRESS][ANY_ADDRESS][sig]
            || _acl[ANY_ADDRESS][ANY_ADDRESS][ANY_SIG];
    }

    function permit(address src, address dst, bytes4 sig) public onlyAuthorized {
        _acl[src][dst][sig] = true;
        emit LogPermit(src, dst, sig);
    }

    function forbid(address src, address dst, bytes4 sig) public onlyAuthorized {
        _acl[src][dst][sig] = false;
        emit LogForbid(src, dst, sig);
    }
    
    function permit(address src, address dst, string sig) external {
        permit(src, dst, bytes4(keccak256(sig)));
    }
    
    function forbid(address src, address dst, string sig) external {
        forbid(src, dst, bytes4(keccak256(sig)));
    }

    function permitAny(address src, address dst) external {
        permit(src, dst, ANY_SIG);
    }
    
    function forbidAny(address src, address dst) external {
        forbid(src, dst, ANY_SIG);
    }
}
```

In this contract, there are three main parameters: the caller address, the called contract address, and the function signature.。By configuring ACL access policies, you can precisely define and control function access behavior and permissions。The contract has built-in ANY constants that match arbitrary functions, making access granularity control easier.。This template contract is powerful and flexible enough to meet the needs of all similar permission control scenarios.。

## Improve storage and compute efficiency

So far, in the above deduction process, more is to do the addition of smart contract programming。But the storage and computing resources on smart contracts are more valuable than traditional software environments。Therefore, how to subtract contracts is also one of the required courses to make good use of Solidity。

### Select the appropriate variable type

Explicit problems can be detected and reported by the EVM compiler.；But a large number of performance issues can be hidden in the details of the code.。Solidity provides very precise base types, which is very different from traditional programming languages。Here are a few tips on the basic types of Solidity。In C, you can use short\ int\ long to define integer types on demand, and in Solidity, not only distinguish between int and uint, but even define the length of uint, such as uint8 is one byte, uint256 is 32 bytes。This design warns us that what can be done with uint8 should never be done with uint16.！Almost all basic types of Solidity, whose size can be specified at declaration time。Developers must make effective use of this syntax feature, writing code as small as possible to meet the needs of the variable type。The data type bytes32 can hold 32 (raw) bytes, but unless the data is a fixed-length data type such as bytes32 or bytes16, it is more recommended to use bytes that can vary in length.。Bytes is similar to byte [], but it will be automatically compressed and packaged in external functions, which is more space-saving。If the variable content is in English, you do not need to use UTF-8 encoding, here, recommend bytes instead of string。string defaults to UTF-8 encoding, so the storage cost of the same string will be much higher。

### compact state variable packing

In addition to using as small data types as possible to define variables, sometimes the order in which variables are arranged is also very important and may affect program execution and storage efficiency。The root cause is still EVM, whether it is an EVM storage slot (Storage Slot) or a stack, each element is a word in length (256 bits, 32 bytes).。When allocating storage, all variables (except for non-static types such as maps and dynamic arrays) are written down in order of declaration, starting at position 0。When processing state variables and structure member variables, EVM packs multiple elements into a storage slot, thereby merging multiple reads or writes into a single operation on storage。It is worth noting that when using elements smaller than 32 bytes, the gas usage of the contract may be higher than when using 32-byte elements.。This is because EVM will operate on 32 bytes at a time, so if the element is smaller than 32 bytes, more operations must be used to reduce its size to the required。This also explains why the most common data types in Solidity, such as int, uint, and byte32, all occupy just 32 bytes.。Therefore, when a contract or structure declares multiple state variables, it is important to be able to reasonably arrange multiple storage state variables and structure member variables so that they take up less storage space.。For example, in the following two contracts, the Test1 contract consumes less storage and computing resources than the Test2 contract.。

```
contract Test1 {
    / / occupy 2 slots,"gasUsed":188873
    struct S {
        bytes1 b1;
        bytes31 b31;
        bytes32 b32;
    }
    S s;
    function f() public {
        S memory tmp = S("a","b","c");
        s = tmp;
    }
}

contract Test2 {
    / / occupy 3 slots,"gasUsed":188937
    struct S {
        bytes31 b31;
        bytes32 b32;
        bytes1 b1;
    }
    // ……
}
```

### Optimize Query Interface

There are many optimization points of the query interface, for example, you must add the view modifier to the function declaration that is only responsible for the query, otherwise the query function will be packaged as a transaction and sent to the consensus queue, executed by the whole network and recorded in the block.；This will greatly increase the burden on the blockchain and take up valuable on-chain resources。For example, don't add complex query logic to a smart contract, because any complex query code will make the entire contract longer and more complex.。Readers can use the WeBASE data export component mentioned above to export on-chain data to a database for off-chain query and analysis。

### Reduced contract binary length

The Solidity code written by the developer is compiled into binary code, and the process of deploying the smart contract is actually storing the binary code on the chain through a transaction and obtaining the address specific to the contract.。Reducing the length of binary code can save the overhead of network transmission and consensus packed data storage.。For example, in a typical deposit business scenario, a new deposit contract is created each time a customer deposits a certificate, so the length of the binary code should be reduced as much as possible。The common idea is to cut unnecessary logic and remove redundant code.。Especially when reusing code, some non-rigid code may be introduced。In the above example, ACL contracts support permissions to control the granularity of contract functions.。

```
function canCall(
        address src, address dst, bytes4 sig
    ) public view returns (bool) {
        return _acl[src][dst][sig]
            || _acl[src][dst][ANY_SIG]
            || _acl[src][ANY_ADDRESS][sig]
            || _acl[src][ANY_ADDRESS][ANY_SIG]
            || _acl[ANY_ADDRESS][dst][sig]
            || _acl[ANY_ADDRESS][dst][ANY_SIG]
            || _acl[ANY_ADDRESS][ANY_ADDRESS][sig]
            || _acl[ANY_ADDRESS][ANY_ADDRESS][ANY_SIG];
    }
```

However, in specific business scenarios, you only need to control the contract visitors, and further simplify the usage logic by deleting the corresponding code.。In this way, the length of the binary code of the corresponding contract will be greatly reduced.。

```
function canCall(
        address src, address dst
) public view returns (bool) {
        return _acl[src][dst]
            || _acl[src][ANY_ADDRESS]
            || _acl[ANY_ADDRESS][dst];
    }
```

Another way to reduce binary code is to use a more compact writing method.。It has been measured that the binary length of the judgment statement using the short-circuit principle as above will be longer than if-Shorter Else Syntax。Similarly, if-Else's structure will also be better than if-if-If the structure generates shorter binary code。Finally, in scenarios where the length of the binary code is extreme, you should avoid creating new contracts in the contract as much as possible, which will significantly increase the length of the binary.。For example, a contract has the following constructor:

```
constructor() public {
        / / Create a new object in the constructor
        _a = new A();
}
```

We can circumvent this problem by constructing the A object off-chain and based on address transmission and fixed validation.。

```
constructor(address a) public {
        A _a = A(a);
        require(_a._owner == address(this));
}
```

Of course, this can also complicate the way contracts interact.。However, it provides a shortcut to effectively shorten the length of binary code, which requires trade-offs in specific business scenarios.。

## Guaranteed contracts can be upgraded

### classic three-layer structure

Through the above, we do our best to maintain the flexibility of the contract design.；The wheels were reused when turning over boxes and cabinets.；Also conduct all-round, dead-end-free testing of release contracts。In addition, as business needs change, we will also face a problem: how to ensure a smooth and smooth upgrade of the contract.？As a high-level programming language, Solidity supports running some complex control and calculation logic, and also supports storing the state and business data after the smart contract is run.。Different from the application of WEB development and other scenarios-Database hierarchical architecture, Solidity language does not even abstract a layer of independent data storage structure, data are saved to the contract.。However, this model becomes a bottleneck once the contract needs to be upgraded。

In Solidity, once a contract is deployed and released, its code cannot be modified and can only be modified by releasing a new contract.。If the data is stored in the old contract, there will be a so-called "orphan data" problem, the new contract will lose the historical business data previously run.。In this case, developers can consider migrating the old contract data to the new contract, but this operation has at least two problems:

Migrating data will increase the burden on the blockchain, resulting in waste and consumption of resources, and even introduce security issues；
2. Pull the whole body, will introduce additional migration data logic, increase contract complexity.。

A more reasonable approach is to abstract a separate contract storage layer.。This storage layer only provides the most basic way to read and write contracts, and does not contain any business logic.。In this model, there are three contract roles:

- Data contract: Save data in a contract and provide an interface for data manipulation。
- Manage contracts: Set control permissions to ensure that only control contracts have permission to modify data contracts.。
- Control contracts: Contracts that really need to initiate operations on data。

Specific code examples are as follows:

##### Data contracts:

```
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

##### Management contract:

```
contract Admin is BasicAuth {
    function upgradeContract(FruitStore fruitStore, address newController) isAuthorized external {
        fruitStore.upgradeVersion(newController);
    }
}
```

##### Control Contracts:

```
contract FruitStoreController is BasicAuth {
    function upgradeStock(bytes fruit, uint stock) isAuthorized external {
        fruitStore.setFruitStock(fruit, stock);
    }
}
```

Once the control logic of the function needs to be changed, the developer simply modifies the FruitStoreController control contract logic, deploys a new contract, and then uses the management contract Admin to modify the new contract address parameters to easily complete the contract upgrade.。This approach eliminates data migration hazards due to changes in business control logic in contract upgrades。But there is no such thing as a free lunch, and this kind of operation requires a basic trade-off between scalability and complexity.。First, the separation of data and logic reduces operational performance。Second, further encapsulation increases program complexity。Finally, more complex contracts increase the potential attack surface, and simple contracts are safer than complex contracts.。

### general data structure

So far, there is a question of what to do if the data structure itself in the data contract needs to be upgraded？

For example, in FruitStore, originally only inventory information was kept, but now, as the fruit store business has grown, a total of ten branches have been opened, and each branch, each fruit's inventory and sales information needs to be recorded.。In this case, one solution is to use external association management: create a new ChainStore contract, create a mapping in this contract, and establish the relationship between the branch name and FruitStore.。

In addition, different stores need to create a FruitStore contract。In order to record new sales information and other data, we also need to create a new contract to manage。If you can preset different types of reserved fields in FruitStore, you can avoid the overhead of creating new sales information contracts and still reuse FruitStore contracts.。But this approach will increase the storage overhead at the beginning.。A better idea is to abstract a more underlying and generic storage structure。The code is as follows:

```
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

Similarly, we can add all data type variables to help commonDB cope with and meet different data type storage needs.。The corresponding control contract may be modified as follows:

```
contract FruitStoreControllerV2 is BasicAuth {
    function upgradeStock(bytes32 storeName, bytes32 fruit, uint stock) 
        isAuthorized external {
        commonDB.setUint(sha256(storeName, fruit), stock);
        uint result = commonDB.getUint(sha256(storeName, fruit));
    }
}
```

Using the above storage design patterns can significantly improve the flexibility of contract data storage and ensure that contracts can be upgraded.。As we all know, Solidity neither supports databases, uses code as a storage entity, nor provides the flexibility to change schemas。However, with this KV design, the storage itself can be made highly scalable。Anyway,**No strategy is perfect, and good architects are good at weighing**。Smart contract designers need to fully understand the pros and cons of various solutions and choose the right design based on the actual situation。

## SUMMARY

As for this, I hope to arouse the reader's interest in survival and evolution in the Solidity world.。"If there is perfection, there must be lies," the world of software development has no silver bullet。The process of writing this article is the process of gradual improvement and evolution from the simplest contract.。In the Solidity programming world, survival and evolution are inseparable from three key words: security, reusability, and efficiency.。Life goes on, evolution goes on。It is difficult to exhaust all the skills of survival and evolution in a short essay. I hope these three key words can help you soar in the world of Solidity and keep writing brilliant stories and legends:)
