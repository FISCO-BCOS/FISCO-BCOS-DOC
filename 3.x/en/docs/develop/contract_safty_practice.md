# 9. Smart Contract Security Practices

Smart contract security refers to the design, coding, deployment, operation and maintenance of smart contracts throughout the life cycle, take measures to ensure the security and reliability of the contract, to prevent malicious attacks, exploits or incorrect operations caused by the loss of assets or system crash。

This article details the strategies, recommended practices, and security measures that smart contracts should use at all stages, starting from design patterns。

## 1. Smart Contract Design Patterns

[Solidity design pattern for writing smart contracts](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/articles/3_features/35_contract/solidity_design_patterns.html)

Author: Chu Yuzhi ｜ FISCO BCOS Core Developer

With the development of blockchain technology, more and more enterprises and individuals begin to combine blockchain with their own business。The unique advantages of blockchain, for example, data is open, transparent and immutable, which can facilitate business。But at the same time, there are some hidden dangers。The transparency of the data means that anyone can read it；Cannot be tampered with, meaning that information cannot be deleted once it is on the chain, and even the contract code cannot be changed。In addition, the openness of the contract, the callback mechanism, each of the characteristics can be used as an attack technique, a little careless, light contract is useless, heavy to face the risk of disclosure of corporate secrets。Therefore, before the business contract is put on the chain, the security and maintainability of the contract need to be fully considered in advance。Fortunately, through a lot of practice of Solidity language in recent years, developers continue to refine and summarize, has formed some"Design Pattern"To guide the daily development of common problems。

In 2019, the IEEE included a paper from the University of Vienna entitled "Design Patterns For Smart Contracts In the Ethereum Ecosystem."。This paper analyzes the hot Solidity open source projects, combined with previous research results, sorted out 18 design patterns。These design patterns cover security, maintainability, lifecycle management, authentication, and more。

| Type| Mode|
|--------------------|---------------------------------------------------------------------------------------------------------------------------|
| Security           | 1. Checks-Effects-Interaction<br/>2. Emergency Stop<br/>3. Speed Bump<br/>4. Rate Limit<br/>5. Mutex<br/>6. Balance Limit |
| Maintenance        | 7. Data Segregation<br/>8. Satellite<br/>9. Contract Register<br/>10. Contract Relay                                      |
| Lifecycle          | 11. Mortal<br/>12. Automatic Deprecation                                                                                  |
| Authorization      | 13. Ownership<br/>14. Access Restriction                                                                                  |
| Action And Control | 15. Pull Payment<br/>16. Commit And Reveal<br/>17. State Machine<br/>18. Oracle                                           |

Next, this article will select the most common and common of these 18 design patterns, which have been extensively tested in actual development experience。

### 1.1 Checks-Effects-Interaction - Ensure the state is complete before making external calls

This pattern is a coding style constraint that effectively avoids replay attacks。Typically, a function might have three parts:

-Checks: Parameter Validation
-Effects: Modify contract status
- Interaction: external interaction

This pattern requires contracts to organize code in the order Checks-Effects-Interaction。The advantage is that Checks-Effects has completed all the work related to the state of the contract itself before making the external call, making the state complete and logically self-consistent, so that the external call cannot be attacked with incomplete state。Review the previous AddService contract, did not follow this rule, in the case of its own state has not been updated to call the external code, the external code can naturally cross a knife, so that _ adders [msg.sender] = true permanently not called, thus invalidating the require statement。Let's review the original code in terms of checks-effects-interaction:

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

With a slight adjustment of the order, satisfying the Checks-Effects-Interaction pattern, the tragedy is avoided:

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

Since the _ adders mapping has been modified, when a malicious attacker wants to recursively call addByOne, the require line of defense will work to intercept the malicious call。Although this pattern is not the only way to resolve reentry attacks, it is still recommended that developers follow。

### 1.2 Mutex - Prohibit Recursion

Mutex pattern is also an effective way to address re-entry attacks。It prevents the function from being called recursively by providing a simple modifier:

```solidity
contract Mutex {
    bool locked;
    modifier noReentrancy() {
        / / Prevent recursion
        require(!locked, "Reentrancy detected");
        locked = true;
        _;
        locked = false;
    }

    / / Calling this function will throw a Reentry detected error
    function some() public noReentrancy{
        some();
    }
}
```

In this example, before calling the some function, the noReancy modifier is run to assign the locked variable to true。If some is called recursively at this point, the logic of the modifier is activated again, and the first line of code for the modifier throws an error because the locked property is already true。

### 1.3 Data segregation - separation of data and logic

Before understanding the design pattern, take a look at the following contract code:

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

This contract contains two capabilities, one is to store data(setData function)The other is the use of data for calculation(Compute function)。If the contract is deployed for a period of time and you find that the compute is incorrectly written, for example, you should not multiply by 10, but multiply by 20, it will lead to the question of how to upgrade the contract as described above。At this point, you can deploy a new contract and try to migrate the existing data to the new contract, but this is a heavy operation, on the one hand, to write the code of the migration tool, on the other hand, the original data is completely obsolete, empty of valuable node storage resources。

Therefore, it is necessary to be modular in advance when programming。If we will"Data"Seen as unchanging things, will"Logic"Seeing as something that can change, you can perfectly avoid the above problems。The Data Segregation (which means data separation) pattern is a good implementation of this idea。The model requires a business contract and a data contract: the data contract is only for data access, which is stable；Business contracts, on the other hand, perform logical operations through data contracts。

In conjunction with the previous example, we transfer data read and write operations specifically to a contract DataRepository

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

The calculation function is placed separately in a business contract:

```solidity
contract Computer{
    DataRepository private _dataRepository;
    constructor(address addr){
        _dataRepository =DataRepository(addr);
    }

    / / Business code
    function compute() public view returns(uint){
        return _dataRepository.getData() * 10;
    }    
}
```

In this way, as long as the data contract is stable, the upgrade of the business contract is very lightweight。For example, when I want to replace Computer with ComputerV2, the original data can still be reused。

### 1.4 Satellite - Decompose Contract Function

A complex contract usually consists of many functions, if these functions are all coupled in a contract, when a function needs to be updated, you have to deploy the entire contract, normal functions will be affected。The Satellite model addresses these issues using the single-duty principle, advocating the placement of contract subfunctions into subcontracts, with each subcontract (also known as a satellite contract) corresponding to only one function。When a sub-function needs to be modified, just create a new sub-contract and update its address to the main contract。

For a simple example, the setVariable function of the following contract is to calculate the input data (compute function) and store the calculation result in the contract state _ variable:

```solidity
contract Base {
    uint public _variable;

    function setVariable(uint data) public {
        _variable = compute(data);
    }

    / / Calculation
    function compute(uint a) internal returns(uint){
        return a * 10;        
    }
}
```

After deployment, if you find that the compute function is incorrectly written and you want to multiply by a factor of 20, you must redeploy the entire contract。However, if you initially operate in Satellite mode, you only need to deploy the corresponding subcontract。

First, let's strip the compute function into a separate satellite contract:

```solidity
contract Satellite {
    function compute(uint a) public returns(uint){
        return a * 10;        
    }
}
```

The main contract then relies on the subcontract to complete setVariable

```solidity
contract Base {
    uint public _variable;

    function setVariable(uint data) public {
        _variable = _satellite.compute(data);
    }

     Satellite _satellite;
    / / Update sub-contract (satellite contract)
    function updateSatellite(address addr) public {
        _satellite = Satellite(addr);
    }
}
```

In this way, when we need to modify the compute function, we only need to deploy such a new contract and pass its address to Base.updateSatellite:

```solidity
contract Satellite2{
    function compute(uint a) public returns(uint){
        return a * 20;        
    }    
}
```

### 1.5 Contract Registry - Track Latest Contracts

In Satellite mode, if a primary contract depends on a subcontract, when the subcontract is upgraded, the primary contract needs to update the address reference to the subcontract, which is done through updateXXX, for example, the updateSatellite function described earlier。This type of interface is a maintainable interface and has nothing to do with the actual business. Too much exposure of this type of interface will affect the aesthetics of the main contract and greatly reduce the caller's experience。The Contract Registry design pattern elegantly solves this problem。In this design mode, there is a special contract Registry to track each upgrade of a subcontract, and the main contract can obtain the latest subcontract address by querying this Registyr contract。After the satellite contract is redeployed, the new address is updated via the Registry.update function。

```solidity
contract Registry{

    address _current;
    address[] _previous;

    / / If the subcontract is upgraded, update the address through the update function
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

The main contract relies on Registry to get the latest satellite contract address。

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

### 1.6 Contract Relay - Agent invokes latest contract

This design pattern solves the same problem as Contract Registry, i.e. the main contract can call the latest subcontract without exposing the maintenance interface。In this mode, there is a proxy contract, and the subcontract shares the same interface, responsible for passing the call request of the main contract to the real subcontract。After the satellite contract is redeployed, the new address is updated via the SatelliteProxy.update function。

```solidity
contract SatelliteProxy{
    address _current;
    function compute(uint a) public returns(uint){
        Satellite satellite = Satellite(_current);   
        return satellite.compute(a);
    } 
    
    / / If the subcontract is upgraded, update the address through the update function
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

The main contract depends on the SatelliteProxy:

```solidity
contract Base {
    uint public _variable;

    function setVariable(uint data) public {
        _variable = _proxy.compute(data);
    }
    SatelliteProxy private _proxy = //...;
}
```

### 1.7 Mortal - Allow contracts to self-destruct

There is a selfdestruct instruction in the bytecode to destroy the contract。So just expose the self-destruct interface:

```solidity
contract Mortal{

    / / Self-destruct
    function destroy() public{
        selfdestruct(msg.sender);
    } 
}
```

### 1.8 Automatic Deprecation - allows contracts to automatically stop service

If you want a contract to be out of service after a specified period without human intervention, you can use the Automatic Deprecation pattern。

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

When the user calls service, the notExpired modifier will first perform date detection, so that once a specific time has passed, the call will be intercepted at the notExpired layer due to expiration。

### 1.9 Ownership check

There are many administrative interfaces in the previous article, which can have serious consequences if they can be called by anyone, such as the self-destruct function above, which assumes that anyone can access it, and its severity is self-evident。Therefore, a set of permission control design patterns that ensure that only specific accounts can access is particularly important。

For permission control, you can use the ownership mode。This pattern guarantees that only the owner of the contract can call certain functions。First you need an Owned contract:

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

What if a business contract wants a function to be called only by the owner?？As follows:

```solidity
contract Biz is Owned{
    function manage() public onlyOwner{
    }
}
```

Thus, when the manage function is called, the onlyOwner modifier runs first and detects whether the caller is consistent with the contract owner, thus intercepting unauthorized calls。

### 1.10 Delay in Secret Disclosure

These patterns are typically used in specific scenarios, and this section will focus on privacy-based coding patterns and design patterns for interacting with off-chain data。

On-chain data is open and transparent, once some private data on the chain, anyone can see, and can never withdraw。Commit And Reveal mode allows users to convert the data to be protected into unrecognizable data, such as a string of hash values, until a certain point to reveal the meaning of the hash value, revealing the true original value。In the voting scenario, for example, suppose that the voting content needs to be revealed after all participants have completed the voting to prevent participants from being affected by the number of votes during this period。We can look at the specific code used in this scenario:

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

## 2. Smart contract programming strategy

[Solidity Programming Strategy for Smart Contract Writing](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/articles/3_features/35_contract/solidity_design_programming_strategy.html)

Author ： MAO Jiayu ｜ FISCO BCOS Core Developer

**"Do not add entities unless necessary"。**

- Important data that requires distributed collaboration is chained, and unnecessary data is not chained；
- Sensitive data is desensitized or encrypted on the chain (depending on the degree of data confidentiality, select the encryption algorithm that meets the requirements of the privacy protection security level)；
- On-chain authentication, off-chain authorization。

When using blockchain, developers don't need to put all their business and data on the chain。Instead, "good steel is on the cutting edge," and smart contracts are more suitable for use in distributed collaboration business scenarios。

### 2.1 Refinement of function variables

If complex logic is defined in a smart contract, especially if complex function parameters, variables, and return values are defined in the contract, you will encounter the following errors at compile time

```shell
Compiler error: Stack too deep, try removing local variables.
```

This is also one of the high-frequency technical issues in the community。The reason for this problem is that EVM is designed for a maximum stack depth of 16。All calculations are performed within a stack, and access to the stack is limited to the top of the stack in such a way as to allow one of the top 16 elements to be copied to the top of the stack, or to swap the top of the stack with one of the 16 elements below。All other operations can only take the top few elements, and after the operation, the result is pushed to the top of the stack。Of course, you can put the elements on the stack into storage or memory。However, you cannot access only the element on the stack at the specified depth unless you first remove the other elements from the top of the stack。If the size of the input parameters, return values, and internal variables in a contract exceeds 16, it clearly exceeds the maximum depth of the stack。Therefore, we can use structs or arrays to encapsulate input or return values to reduce the use of elements at the top of the stack, thereby avoiding this error。For example, the following code encapsulates the original 16 bytes variables by using the bytes array。

```solidity
function doBiz(bytes[] paras) public {
        require(paras.length >= 16);
        // do something
}
```

### 2.2 Guaranteed parameters and behavior as expected

When writing smart contracts, it is important to pay attention to the examination of contract parameters and behavior, especially those contract functions that are open to the outside world。Solidity provides keywords such as require, revert, and assert to detect and handle exceptions。Once the error is detected and found, the entire function call is rolled back and all state modifications are rolled back as if the function had never been called。The following uses three keywords to achieve the same semantics。

```solidity
require(_data == data, "require data is valid");

if(_data != data) { revert("require data is valid"); }

assert(_data == data);
```

However, these three keywords generally apply to different usage scenarios:

-require: The most commonly used detection keyword to verify whether the input parameters and the result of calling the function are legitimate。
-revert: Applicable in a branch judgment scenario。
- assert: Check whether the result is correct and legal, generally used at the end of the function。

In a function of a contract, you can use the function decorator to abstract part of the parameter and condition checking。In the function body, you can check the running state using judgment statements such as if-else, and use revert fallback for abnormal branches。You can use assert to check the execution result or intermediate state before the function runs。In practice, it is recommended to use the require keyword and move the condition check to the function decorator；This allows the function to have more single responsibilities and focus more on the business logic。At the same time, condition codes such as function modifiers are easier to reuse, and contracts are more secure and hierarchical。

Take a fruit store inventory management system as an example, design a fruit supermarket contract。This contract only contains the management of all fruit categories and inventory quantities in the store, and the setFruitStock function provides a function corresponding to the fruit inventory settings。In this contract, we need to check the incoming parameters, i.e. the fruit name cannot be empty。

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

As mentioned above, we added a function decorator for parameter checking before function execution。Similarly, by using function decorators that check before and after function execution, you can ensure that smart contracts are safer and clearer。The writing of smart contracts requires strict pre-and post-function checks to ensure their security。

### 2.3 Strictly control the execution authority of functions

If the parameters and behavior detection of smart contracts provide static contract security measures, then the mode of contract permission control provides control of dynamic access behavior。Since smart contracts are published on the blockchain, all data and functions are open and transparent to all participants, and any node participant can initiate a transaction, which does not guarantee the privacy of the contract。Therefore, the contract publisher must design a strict access restriction mechanism for the function。Solidity provides syntax such as function visibility modifiers and modifiers, which can be used flexibly to help build a smart contract system with legal authorization and controlled calls。Or take the fruit contract just now as an example。Now getStock provides a function to query the inventory quantity of specific fruits。

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

The fruit store owner posted the contract on the chain。However, after publication, the setFruitStock function can be called by any other affiliate chain participant。Although the participants in the alliance chain are real-name authenticated and can be held accountable afterwards；However, once a malicious attacker attacks the fruit store, calling the setFruitStock function can modify the fruit inventory at will, or even clear all the fruit inventory, which will have serious consequences for the normal operation and management of the fruit store。Therefore, it is necessary to set up some prevention and authorization measures: for the function setFruitStock that modifies the inventory, the caller can be authenticated before the function executes。Similarly, these checks may be reused by multiple functions that modify the data, using an onlyOwner decorator to abstract this check。The owner field represents the owner of the contract and is initialized in the contract constructor。Using public to modify the getter query function, you can pass _ owner()function to query the owner of a contract。

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

In this way, we can encapsulate the corresponding function call permission check into the decorator, the smart contract will automatically initiate the caller authentication check, and only allow the contract deployer to call the setFruitStock function, thus ensuring that the contract function is open to the specified caller。

### 2.4 Abstract generic business logic

Analyzing the above FruitStore contract, we found that there seems to be something strange mixed in with the contract。Referring to the programming principle of single responsibility, the fruit store inventory management contract has more logic than the above function function check, so that the contract can not focus all the code in its own business logic。In this regard, we can abstract reusable functions and use Solidity's inheritance mechanism to inherit the final abstract contract。Based on the above FruitStore contract, a BasicAuth contract can be abstracted, which contains the previous onlyOwner's decorator and related functional interfaces。

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

FruitStore can reuse this decorator and converge the contract code into its own business logic。

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

In this way, the logic of FruitStore is greatly simplified, and the contract code is more streamlined, focused and clear。

### 2.5 Prevention of loss of private key

There are two ways to call contract functions in the blockchain: internal calls and external calls。For privacy protection and permission control, a business contract defines a contract owner。Suppose user A deploys the FruitStore contract, then the above contract owner is the external account address of deployer A。This address is generated by the private key calculation of the external account。However, in the real world, the phenomenon of private key leakage, loss abound。A commercial blockchain DAPP needs to seriously consider issues such as private key replacement and reset。The simplest and most intuitive solution to this problem is to add an alternate private key。This alternate private key supports the operation of the permission contract modification owner. The code is as follows:

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

In this way, when we find that the private key is lost or leaked, we can use the standby external account to call setOwner to reset the account to restore and ensure the normal operation of the business。

### 2.6 Reasonable Reservation Events

So far, we have implemented a strong and flexible permission management mechanism, and only pre-authorized external accounts can modify the contract owner attribute。However, with the above contract code alone, we cannot record and query the history and details of modifications and calls to functions。And such needs abound in real business scenarios。For example, FruitStore needs to check the historical inventory modification records to calculate the best-selling and slow-selling fruits in different seasons。

One way is to rely on the chain to maintain an independent ledger mechanism。However, there are many problems with this approach: the cost overhead of keeping the off-chain ledger and on-chain records consistent is very high；At the same time, smart contracts are open to all participants in the chain, and once other participants call the contract function, there is a risk that the relevant transaction information will not be synchronized。For such scenarios, Solidity provides the event syntax。Event not only has the mechanism for SDK listening callback, but also can record and save event parameters and other information to the block with low gas cost。In the FISCO BCOS community, there are also tools like WEBASE-Collect-Bee that enable the complete export of block historical event information after the fact。

[WEBASE-Collect-Bee Tool Reference](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Collect-Bee/index.html)

Based on the above permission management contract, we can define the corresponding permission modification event, other events and so on。

```solidity
event LogSetAuthority (Authority indexed authority, address indexed from);
}
```

Next, you can call the corresponding event:

```solidity
function setAuthority(Authority authority)
        public
        auth
{
        _authority = authority;
        emit LogSetAuthority(authority, msg.sender);
    }
```

When the setAuthority function is called, LogSetAuthority is triggered at the same time, and the Authority contract address and caller address defined in the event are recorded in the blockchain transaction receipt。When the setAuthority method is called from the console, the corresponding event LogSetAuthority is also printed。Based on WEBASE-Collect-Bee, we can export all the history information of this function to the database。It can also be based on WEBASE-Collect-Bee for secondary development, to achieve complex data query, big data analysis and data visualization functions。

### 2.7 Follow Safety Programming Specifications

Each language has its own coding specifications, and we need to follow Solidity's official programming style guidelines as strictly as possible to make the code easier to read, understand, and maintain, effectively reducing the number of contract bugs。[Solidity Official Programming Style Guide Reference](https://solidity.readthedocs.io/en/latest/style-guide.html)。In addition to programming specifications, the industry has also summarized many secure programming guidelines, such as re-entry vulnerabilities, data structure overflows, random number errors, runaway constructors, storage pointers for initialization, and so on。To address and prevent such risks, it is critical to adopt industry-recommended security programming specifications, such as the [Solidity Official Security Programming Guide](https://solidity.readthedocs.io/en/latest/security-considerations.html)。At the same time, after the contract is released and launched, you also need to pay attention to and subscribe to all kinds of security vulnerabilities and attack methods released by security organizations or institutions in the Solidity community, and make up for problems in a timely manner。

For important smart contracts, it is necessary to introduce auditing。Existing audits include manual audits, machine audits and other methods to ensure contract security through code analysis, rule validation, semantic validation and formal validation。Although emphasized throughout this article, modularity and reuse of smart contracts that are highly reviewed and widely validated are best practice strategies。But in the actual development process, this assumption is too idealistic, each project will more or less introduce new code, or even from scratch。However, we can still grade audits based on how much code is reused, explicitly label referenced code, and focus audits and inspections on new code to save on audit costs。

### 2.8 Using the SmartDev App Plug-in

SmartDev includes a set of open and lightweight development components, covering the development, debugging, and application development of smart contracts, including the SmartDev-Contract, SmartDev-SCGP, and SmartDev-Scaffold。Developers can freely choose the corresponding development tools according to their own situation to improve development efficiency。

For more information, see: [SmartDev Application Development Components](./smartdev_index.md)

## 3. Smart contract deployment permission control

Authority control for deployment contracts will be centrally controlled by a governance committee, which will control deployment authority by vote。After the governance committee's proposal for a deployment permission is approved, the deployment permission write interface of the fixed address 0x1005 precompiled contract will be actively called, and these write interfaces are also limited to the governance committee contract call。

The deployment permissions are recorded in the BFS directory / apps, which represents the write permissions allowed in the / apps directory。

The governance committee can perform operations such as permission control of deployment contracts through the console. For more information, see [Proposal for Setting Deployment Permission Types](../operation_and_maintenance/console/console_commands.html#setdeployauthtypeproposal) , [Open Deployment Permission Proposal](../operation_and_maintenance/console/console_commands.html#opendeployauthproposal) , [Close Deployment Permissions Proposal](../operation_and_maintenance/console/console_commands.html#closedeployauthproposal)

When checking the deployment permissions, the transaction initiation address tx.origin is verified. If you do not have the permissions, the error code -5000 is returned。That is, the user deployment contract and the user deployment contract are verified。

## 4. Smart contract execution permission control

The contract administrator can initiate a transaction on a precompiled contract with a fixed address of 0x1005 and read and write the access ACL of the contract interface。

When the write operation of the access ACL of the contract interface is performed, it will be determined whether the transaction originator msg.sender is the contract administrator of the contract permission table record, and if not, it will be rejected。

The contract administrator can access the write operation of the ACL through the console. For more information, see [Contract administrator command](../operation_and_maintenance/console/console_commands.html#setmethodauth)

When checking the contract invocation permission, the transaction initiation address tx.origin and the message sender msg.sender will be verified. If there is no permission, the error code -5000 will be returned。That is, the user invokes the contract, the user invokes the contract through the contract, and the contract invokes the contract。

## 5. Smart contract operation and maintenance

Smart contracts in operation and maintenance mainly focus on the data state of smart contracts, smart contract upgrades, smart contract freezing, smart contract destruction。

### 5.1 Smart Contract Upgrade

In Solidity, once a contract is deployed and released, its code cannot be modified and can only be modified by releasing a new contract。If the data is stored in the old contract, there will be a so-called "orphan data" problem, the new contract will lose the historical business data previously run。In this case, developers can consider migrating the old contract data to the new contract, but this operation has at least two problems:

Migrating data will increase the burden on the blockchain, resulting in waste and consumption of resources, and even introduce security issues；
2. Pull the whole body, will introduce additional migration data logic, increase contract complexity。

A more reasonable approach is to abstract a separate contract storage layer。This storage layer only provides the most basic way to read and write contracts, and does not contain any business logic。In this model, there are three contract roles:

- Data contract: Save data in the contract and provide the operation interface of the data。
- Manage contracts: Set control permissions to ensure that only control contracts have permission to modify data contracts。
- Control contracts: Contracts that really need to initiate operations on data。

Specific code examples are as follows:

**Data contracts**

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

**Management contract**

```solidity
contract Admin is BasicAuth {
    function upgradeContract(FruitStore fruitStore, address newController) isAuthorized external {
        fruitStore.upgradeVersion(newController);
    }
}
```

**control contract**

```solidity
contract FruitStoreController is BasicAuth {
    function upgradeStock(bytes fruit, uint stock) isAuthorized external {
        fruitStore.setFruitStock(fruit, stock);
    }
}
```

Once the control logic of the function needs to be changed, the developer simply modifies the FruitStoreController control contract logic, deploys a new contract, and then uses the management contract Admin to modify the new contract address parameters to easily complete the contract upgrade。This approach eliminates data migration hazards due to changes in business control logic in contract upgrades。But there is no such thing as a free lunch, and this kind of operation requires a basic trade-off between scalability and complexity。First, the separation of data and logic reduces operational performance。Second, further encapsulation increases program complexity。Finally, more complex contracts increase the potential attack surface, and simple contracts are safer than complex contracts。

**Generic Data Structure - Data Upgrades**

So far, there is a question of what to do if the data structure itself in the data contract needs to be upgraded？

For example, in FruitStore, originally only inventory information was kept, but now, as the fruit store business has grown, a total of ten branches have been opened, and each branch, each fruit's inventory and sales information needs to be recorded。In this case, one solution is to use external association management: create a new ChainStore contract, create a mapping in this contract, and establish the relationship between the branch name and FruitStore。

In addition, different stores need to create a FruitStore contract。In order to record new sales information and other data, we also need to create a new contract to manage。If you can preset different types of reserved fields in FruitStore, you can avoid the overhead of creating new sales information contracts and still reuse FruitStore contracts。But this approach will increase the storage overhead at the beginning。A better idea is to abstract a more underlying and generic storage structure。The code is as follows:

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

Similarly, we can add all data type variables to help commonDB cope with and meet different data type storage needs。The corresponding control contract may be modified as follows:

```solidity
contract FruitStoreControllerV2 is BasicAuth {
    function upgradeStock(bytes32 storeName, bytes32 fruit, uint stock) 
        isAuthorized external {
        commonDB.setUint(sha256(storeName, fruit), stock);
        uint result = commonDB.getUint(sha256(storeName, fruit));
    }
}
```

Using the above storage design patterns can significantly improve the flexibility of contract data storage and ensure that contracts can be upgraded。As we all know, Solidity neither supports databases, uses code as a storage entity, nor provides the flexibility to change schemas。However, with this KV design, the storage itself can be made highly scalable。Anyway,**No strategy is perfect, and good architects are good at weighing**。Smart contract designers need to fully understand the pros and cons of various solutions and choose the right design based on the actual situation。

**Use CRUD or KV to store contract data**

The data that needs to be stored is stored using the CRUD data interface, and the CRUD data is persisted on the chain through node consensus。For details, please refer to [Developing Applications Using CRUD Precompiled Contracts](../contract_develop/c++_contract/use_crud_precompiled.md), [Develop applications using KV storage precompiled contracts](../contract_develop/c++_contract/use_kv_precompiled.md)

### 5.2 Freezing and unfreezing of smart contracts

In the event of a contract data exception or a large number of access exceptions, the contract administrator can freeze the smart contract to prevent other users from continuing to access the contract。

The contract administrator can initiate a transaction on a precompiled contract with a fixed address of 0x1005 and read and write the status of the contract。

When the write operation of the contract status is performed, it will be determined whether the transaction originator msg.sender is the contract administrator of the contract permission table record, and if not, it will be rejected。

```eval_rst
.. important::
   Compatibility Note: Contract lifecycle management revocation can only be performed above node version 3.2。
```

The contract administrator can also freeze contracts through the console. For more information, see [Freeze Contract Command](../operation_and_maintenance/console/console_commands.html#freezecontract)[Order to Unfreeze Contracts](../operation_and_maintenance/console/console_commands.html#unfreezecontract)

### 5.3 Smart Contract Abolition

When the contract is no longer in use and the data is no longer accessible, users can use the reserved selfdestruct to destroy the contract, and the contract administrator can also use the contract annulment function to actively set the contract status to annulment。

**selfdestruct**

There is a selfdestruct instruction in the bytecode to destroy the contract。So just expose the self-destruct interface。**Note:** The process is irreversible, please consider the consequences as appropriate。

```solidity
contract Mortal{

    / / Self-destruct
    function destroy() public{
        selfdestruct(msg.sender);
    } 
}
```

**Abolition of Contract**

```eval_rst
.. important::
   Compatibility Note: Contract lifecycle management revocation can only be performed above node version 3.2。
```

**Note:** The process is irreversible, please consider the consequences as appropriate。

When the write operation of the contract status is performed, it will be determined whether the transaction originator msg.sender is the contract administrator of the contract permission table record, and if not, it will be rejected。

The contract administrator can also freeze contracts through the console. For more information, see [Freeze Contract Command](../operation_and_maintenance/console/console_commands.html#freezecontract)