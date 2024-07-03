# Advanced features of Solidity for writing smart contracts

Author ： MAO Jiayu ｜ FISCO BCOS Core Developer

## **Foreword**

FISCO BCOS uses Solidity language for smart contract development。Solidity is a Turing-complete programming language designed for blockchain platforms, supporting features of multiple high-level languages such as function calls, modifiers, overloads, events, inheritance, and libraries。The first two articles in this series introduced the concept of smart contracts and the basic features of Solidity。This article will introduce some advanced features of Solidity to help readers get started quickly and write high-quality, reusable Solidity code。

## **Rational control of types of functions and variables**

Based on the classic object-oriented programming principles of the Least Knowledge Principle, an object should have minimal knowledge of other objects。Good Solidity programming practices should also be consistent with this principle: each contract clearly and reasonably defines the visibility of functions, exposes minimal information to the outside, and manages the visibility of internal functions。At the same time, correctly modifying the types of functions and variables can provide different levels of protection for data within the contract to prevent unintended operations in the program from causing data errors；It also improves code readability and quality, reducing misunderstandings and bugs；It is more conducive to optimizing the cost of contract execution and improving the efficiency of the use of resources on the chain。

### Hold the door to function operations: Function visibility

Solidity has two function calls:

- Internal call: also known as "message call"。Common calls to contract internal functions, parent contract functions, and library functions。(For example, suppose there is an f function in contract A, then inside contract A, other functions call the f function as f()。）

- External calls: Also known as "EVM calls"。Generally cross-contract function calls。Within the same contract, external calls can also be made。(For example, assuming that there is an f-function in contract A, you can use A.f in contract B()Call。Inside contract A, you can use this.f()to call。）。

Functions can be modified by specifying external, public, internal, or private identifiers。

| Identifier| Role|
| -------- | ---------------------------------------- |
| external | Non-internal calls, more efficient when receiving large amounts of data。 |
| public   | Supports both internal and external calls。                 |
| internal | Only internal calls are supported。                         |
| private  | Used only in the current contract and cannot be inherited。         |

Based on the above table, we can derive the visibility of the function public> external > internal > private。Also, if the function does not use the above type identifier, the function type is public by default。

To sum up, we can summarize the different usage scenarios of the above identifiers:

-public, public function, system default。Usually used to embellish**A function that can be exposed to the outside world, and the function may be called internally at the same time。**

-external, external function, recommended**Exposed to the outside only**The function uses the。When a parameter of a function is very large, if you explicitly mark the function as external, you can force the function storage location to be set to calldata, which saves the storage or computing resources required for function execution。

-internal, internal function, recommended for all contracts**Not exposed outside the contract**function to avoid the risk of being attacked due to permission exposure。

-private, private functions, strictly protected contract functions in very few**Not open to outside of contract and not inheritable**used in the scene。

However, it should be noted that no matter what identifier is used, even private, the entire function execution process and data are visible to all nodes, and other nodes can verify and replay arbitrary historical functions。In fact, all the data of the entire smart contract is transparent to the participating nodes of the blockchain。

Users who are new to the blockchain often misunderstand that the privacy of the data on the blockchain can be controlled and protected through permission control operations。This is a wrong view。In fact, under the premise that the blockchain business data is not specially encrypted, all the data in the same ledger of the blockchain is agreed to fall on all nodes, and the data on the chain is globally public and the same, and smart contracts can only control and protect the execution rights of contract data。How to correctly select function modifiers is a "required course" in contract programming practice, only to master the true meaning of this section can freely control the contract function access rights, improve contract security。

### Exposing the least necessary information to the outside world: Visibility of variables

As with functions, for state variables, you need to be aware of the visibility modifier。The modifier of a state variable is internal by default and cannot be set to external。In addition, when a state variable is modified to public, the compiler generates a function with the same name as the state variable。Specific can refer to the following example:

```
pragma solidity ^0.4.0;
​
contract TestContract {
    uint public year = 2020;
}
​
contract Caller {
    TestContract c = new TestContract();
    function f() public {
        uint local = c.year();
        //expected to be 2020
    }
}
```

This mechanism is a bit like the @ Getter annotation provided by the lombok library in the Java language, which generates a get function for a POJO class variable by default, greatly simplifying the writing of some contract code。Similarly, the visibility of variables needs to be reasonably modified, and variables that should not be exposed should be decisively modified with private to make contract code more in line with the "least known" design principle。

### Precise classification of functions: types of functions

Functions can be declared as pure and view, both of which can be seen in the figure below。

| Function Type| Role|
| -------- | ---------------------- |
| pure     | Promise not to read or modify status。 |
| view     | Guaranteed not to modify status。       |

So, what is reading or modifying state?？In simple terms, the two states are reading or modifying the data related to the ledger。

In FISCO BCOS, the read status might be:

1. Read the state variable。
2. Access any member in block, tx, msg (except msg.sig and msg.data)。
3. Call any function that is not marked as pure。
4. Use inline assembly that contains some opcodes。

And the modification status might be:

1. Modify the state variable。
2. Generate events。
3. Create Other Contracts。
4. Using selfdestruct。
5. Call any function that is not marked as view or pure。
6. Use the underlying call。
7. Use an inline assembly that contains a specific opcode。

Note that in some versions of the compiler, there are no mandatory syntax checks for these two keywords。It is recommended to use pure and view to declare functions as much as possible, for example, to declare library functions that do not read or modify any state as pure, which not only improves code readability, but also makes it more pleasing to the eye, why not？

### Value determined at compile time: state constant

The so-called state constant refers to the state variable declared as constant。Once a state variable is declared constant, the value of the variable can only be determined at compile time and cannot be modified。The compiler will generally calculate the actual value of this variable in the compiled state and will not reserve storage space for the variable。Therefore, constant only supports decorated value types and strings。State constants are generally used to define well-defined business constant values。

## Slice-Oriented Programming: Function Modifier

Solidity provides a powerful syntax for changing the behavior of functions: function modifiers。Once a function is decorated, the code defined within the decorator can be executed as a decoration of the function, similar to the concept of decorators in other high-level languages。This is very abstract, let's look at a concrete example:

```
pragma solidity ^0.4.11;
​
contract owned {
    function owned() public { owner = msg.sender; }
    address owner;
​
    / / The function body decorated by the decorator will be inserted into the special symbol _; Location of。
    modifier onlyOwner {
        require(msg.sender == owner);
        _;
    }
​
    / / Modified by the onlyOwner decorator. Before executing the changeOwner function, you must first execute the onlyOwner"_;"statement before。
    function changeOwner(address _owner) public onlyOwner {
        owner = _owner;
    }
}
```

As shown above, after defining the onlyOwner decorator, within the decorator, the require statement requires that msg.sender must be equal to owner。Behind the"_;"Represents the code in the decorated function。

So, the actual execution order of the code becomes:

1. Execute the statement of the onlyOwner decorator, first execute the require statement。(execute line 9)
2. Execute the statement of the changeOwner function。(Execute line 15)

Because the changeOwner function is modified by the onlyOwner function, this function can only be called successfully if msg.sender is the owner, otherwise an error will be reported and rolled back。At the same time, the decorator can also pass in parameters, for example, the above decorator can also be written as:

```
modifier onlyOwner(address sender) {
    require(sender == owner);
    _;
}
​
function changeOwner(address _owner) public onlyOwner(msg.sender) {
        owner = _owner;
}
```

The same function can have multiple modifiers, with spaces in between, and the modifiers check for execution in turn。In addition, decorators can be inherited and overridden。Because of the power it provides, decorators are also often used for permission control, input checking, logging, etc。For example, we can define a modifier for the execution of a trace function:

```
event LogStartMethod();
event LogEndMethod();
​
modifier logMethod {
    emit LogStartMethod();
    _;
    emit LogEndMethod();
}
```

In this way, any function decorated with the logMethod decorator can log its function before and after execution to achieve the log wrap effect。If you are used to AOP using the Spring framework, you can also try to implement a simple AOP function with modifier。

The most common way to open a modifier is through a validator that provides a function。In practice, some of the check statements of the contract code are often abstracted and defined as a modifier, such as the onlyOwner in the above example is a classic permission checker。In this way, even the logic of the check can be quickly reused, and users no longer have to worry about the smart contract being full of parameter checks or other validation code。

## Logs that can be debugged: Events in the contract

After introducing functions and variables, let's talk about one of Solidity's more unique advanced features - the event mechanism。

Events allow us to easily use EVM's logging infrastructure, while Solidity's events have the following effects:

1. Record the parameters defined by the event and store them in the log of the blockchain transaction, providing cheap storage。
2. Provide a callback mechanism, after the event is successfully executed, the node sends a callback notification to the SDK registered to listen, triggering the callback function to be executed。
3. Provide a filter to support parameter retrieval and filtering。

The use of events is very simple, two steps to play。

- The first step is to define an event using the keyword "event"。It is recommended that the naming of the event start with a specific prefix or end with a specific suffix, which is easier to distinguish from the function, in this article we will unify the "Log" prefix to name the event。Below, we use "event" to define an event that is tracked by a function call

```
event LogCallTrace(address indexed from, address indexed to, bool result);

```

Events can be inherited in a contract。When they are called, the parameters are stored in the transaction's log。These logs are saved to the blockchain, associated with the address。In the above example, the parameters are searched with the indexed tag, otherwise, these parameters are stored in the log data and cannot be searched。

-The second step is to trigger the definition event in the corresponding function。When calling an event, add the "emit" keyword before the event name:

```
function f() public {
    emit LogCallTrace(msg.sender, this, true);
}
```

In this way, when the function body is executed, it will trigger the execution of LogCallTrace。

Finally, in the Java SDK of FISCO BCOS, the contract event push function provides an asynchronous push mechanism for contract events. The client sends a registration request to the node, which carries the contract event parameters that the client is concerned about。For more details, please refer to the contract event push function document。In the SDK, you can search by a specific value based on the indexed property of the event。[Contract Event Push Function Document](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/java_sdk.html#id14)：

However, logs and events cannot be accessed directly, not even in contracts created。But the good news is that the definition and declaration of the log is very useful for tracing and exporting "after the fact."。For example, we can define and bury enough events in the writing of contracts, and through WeBASE's data export subsystem we can export all logs to databases such as MySQL。This is particularly applicable to scenarios such as generating reconciliation files, generating reports, and OLTP queries for complex businesses。In addition, WeBASE provides a dedicated code generation subsystem to help analyze specific business contracts and automatically generate the appropriate code。

- [WeBASE's Data Export Subsystem](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Collect-Bee/index.html)

- [Code Generation Subsystem](https://webasedoc.readthedocs.io/zh_CN/latest/docs/WeBASE-Codegen-Monkey/index.html)

In Solidity, events are a very useful mechanism. If the biggest difficulty in developing smart contracts is debug, then making good use of the event mechanism allows you to quickly subdue Solidity development。

## Object-Oriented Overloading

Overloading refers to a function with the same name whose contract has multiple different parameters。For the caller, you can use the same function name to call multiple functions with the same function but different parameters。In some scenarios, this operation can make the code clearer and easier to understand, and I believe readers with some programming experience will have a deep understanding of this。Here's a typical overload syntax:

```
pragma solidity ^0.4.25;
​
contract Test {
    function f(uint _in) public pure returns (uint out) {
        out = 1;
    }
​
    function f(uint _in, bytes32 _key) public pure returns (uint out) {
        out = 2;
    }
}
```

Note that there is only one constructor per contract, which means that contract constructors are not overloaded。We can imagine a world without overloading, programmers must be racking their brains and trying to name functions, and everyone may have to lose a few more hairs!。

## Object-Oriented Inheritance

Solidity uses "is" as the inheritance key。Thus, the following code indicates that contract B inherits contract A

```
pragma solidity ^0.4.25;
​
contract A {
}
​
contract B is A {
}
```

Inherited contract B has access to all non-private functions and state variables of inherited contract A。In Solidity, the underlying implementation principle of inheritance is that when a contract inherits from multiple contracts, only one contract is created on the blockchain, and the code of all base contracts is copied into the created contract。Compared to C++Or the inheritance mechanism of languages such as Java, Solidity's inheritance mechanism is somewhat similar to Python, supporting multiple inheritance mechanisms。Therefore, one contract can be used in Solidity to inherit multiple contracts。In some high-level languages, such as Java, only single inheritance is supported for security and reliability reasons, and multiple inheritance is implemented by using the interface mechanism。For most scenarios, a single inheritance mechanism is sufficient。Multiple inheritance will bring a lot of complex technical problems, such as the so-called "diamond inheritance" and so on, it is recommended to avoid complex multiple inheritance as much as possible in practice。Inheritance simplifies the understanding and description of abstract contract models, clearly reflects the hierarchical relationships between related contracts, and provides software reuse capabilities。This avoids code and data redundancy and increases program reusability。

## **Object-Oriented Abstract Classes and Interfaces**

According to the dependency inversion principle, smart contracts should be as interface-oriented as possible, independent of implementation details。Solidity supports mechanisms for abstract contracts and interfaces。If a contract has unimplemented methods, then it is an abstract contract。For example:

```
pragma solidity ^0.4.25;
​
contract Vehicle {
    / / abstract method
    function brand() public returns (bytes32);
}
```

Abstract contracts cannot be compiled successfully, but can be inherited。interface uses the keyword interface, the above abstraction can also be defined as an interface。

```
pragma solidity ^0.4.25;
​
interface Vehicle {
    / / abstract method
    function brand() public returns (bytes32);
}
```

Interfaces are similar to abstract contracts, but cannot implement any functions, with further limitations

1. Cannot inherit other contracts or interfaces。
2. The constructor cannot be defined。
3. Unable to define variable。
4. Unable to define structure
5. Enumeration cannot be defined。

Appropriate use of interfaces or abstract contracts helps enhance scalability of contract designs。However, due to the limitations of computing and storage resources on the blockchain EVM, it is important not to overdesign, which is also the sinkhole that old drivers who move from the high-level language technology stack to Solidity development often fall into。

## **Avoid remaking wheels: library(Library)**

In software development, many classic principles can improve the quality of software, the most classic of which is to reuse tried and tested, repeatedly polished, rigorously tested high-quality code as much as possible。In addition, reusing mature library code can improve code readability, maintainability, and even scalability。

Like all major languages, Solidity provides a library mechanism。Solidity's library has the following basic features:

1. Users can use the keyword library to create contracts as they do with contracts。
Libraries cannot be inherited or inherited。
3. The internal function of the library is visible to the caller。
4. The library is stateless and state variables cannot be defined, but state variables explicitly provided by the calling contract can be accessed and modified。

Next, let's look at a simple example, the following is a LibSafeMath code base in the FISCO BCOS community。We've streamlined this, retaining only the functionality of addition:

```
pragma solidity ^0.4.25;
​
library LibSafeMath {
  /**
  * @dev Adds two numbers, throws on overflow.
  */
  function add(uint256 a, uint256 b) internal returns (uint256 c) {
    c = a + b;
    assert(c >= a);
    return c;
  }
}
```

We just import the library file in the contract and use L.f()way to call the function, (e.g. LibSafeMath.add(a,b)）。Next, we write a test contract that calls this library, which reads as follows

```
pragma solidity ^0.4.25;
​
import "./LibSafeMath.sol";
​
contract TestAdd {
​
  function testAdd(uint256 a, uint256 b) external returns (uint256 c) {
    c = LibSafeMath.add(a,b);
  }
}
```

In the FISCO BCOS console, we can test the results of the contract (the introduction article of the console is detailed in [FISCO BCOS console details, flying general blockchain experience](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485275&idx=1&sn=95e1cb1a961041d5800b76b4a407d24e&chksm=9f2ef547a8597c51a8940548dd1e30f22eb883dd1864371e832bc50188c153989050244f31e5&scene=21#wechat_redirect)), the running results are as follows:

```
=============================================================================================
Welcome to FISCO BCOS console(1.0.8)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$
​
=============================================================================================
[group:1]> deploy TestAdd
contract address: 0xe2af1fd7ecd91eb7e0b16b5c754515b775b25fd2
​
[group:1]> call TestAdd 0xe2af1fd7ecd91eb7e0b16b5c754515b775b25fd2 testAdd 2000 20
transaction hash: 0x136ce66603aa6e7fd9e4750fcf25302b13171abba8c6b2109e6dd28111777d54
---------------------------------------------------------------------------------------------
Output
function: testAdd(uint256,uint256)
return type: (uint256)
return value: (2020)
---------------------------------------------------------------------------------------------
​
[group:1]>
```

With the above example, we can clearly understand how the library should be used in Solidity。Like Python, in some scenarios, the directive "using A for B";"Can be used to attach library functions (from library A) to any type (B)。These functions will receive the object that called them as the first argument (like Python's self variable)。This feature makes the use of the library easier and more intuitive。

For example, we make the following simple changes to the code:

```
pragma solidity ^0.4.25;
​
import "./LibSafeMath.sol";
​
contract TestAdd {
  / / Add a using... for... statement, the functions in the library LibSafeMath are attached to the type of uint256
  using LibSafeMath for uint256;
​
  function testAdd(uint256 a, uint256 b) external returns (uint256 c) {
        //c = LibSafeMath.add(a,b);
        c = a.add(b);
        / / Object a is directly passed in as the first parameter of the add method。
  }
}
```

Verify that the results are still correct。

```
=============================================================================================
Welcome to FISCO BCOS console(1.0.8)!
Type 'help' or 'h' for help. Type 'quit' or 'q' to quit console.
 ________ ______  ______   ______   ______       _______   ______   ______   ______
|        |      \/      \ /      \ /      \     |       \ /      \ /      \ /      \
| $$$$$$$$\$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\    | $$$$$$$|  $$$$$$|  $$$$$$|  $$$$$$\
| $$__     | $$ | $$___\$| $$   \$| $$  | $$    | $$__/ $| $$   \$| $$  | $| $$___\$$
| $$  \    | $$  \$$    \| $$     | $$  | $$    | $$    $| $$     | $$  | $$\$$    \
| $$$$$    | $$  _\$$$$$$| $$   __| $$  | $$    | $$$$$$$| $$   __| $$  | $$_\$$$$$$\
| $$      _| $$_|  \__| $| $$__/  | $$__/ $$    | $$__/ $| $$__/  | $$__/ $|  \__| $$
| $$     |   $$ \\$$    $$\$$    $$\$$    $$    | $$    $$\$$    $$\$$    $$\$$    $$
 \$$      \$$$$$$ \$$$$$$  \$$$$$$  \$$$$$$      \$$$$$$$  \$$$$$$  \$$$$$$  \$$$$$$
​
=============================================================================================
[group:1]> deploy TestAdd
contract address: 0xf82c19709a9057d8e32c19c23e891b29b708c01a
​
[group:1]> call TestAdd 0xf82c19709a9057d8e32c19c23e891b29b708c01a testAdd 2000 20
transaction hash: 0xcc44a80784404831d8522dde2a8855606924696957503491eb47174c9dbf5793
---------------------------------------------------------------------------------------------
Output
function: testAdd(uint256,uint256)
return type: (uint256)
return value: (2020)
---------------------------------------------------------------------------------------------
​
[group:1]>
```

Better use of Solidity library helps developers reuse code better。In addition to the large number of open source, high-quality code libraries provided by the Solidity community, the FISCO BCOS community also plans to launch a new Solidity code library, open to community users, so stay tuned。Of course, you can also do it yourself, write reusable code library components, and share them with the community。

## SUMMARY

This article introduces several high-level syntax features of Solidity contract writing, aiming to help readers quickly immerse themselves in the Solidity programming world。The trick to writing high-quality, reusable Solidity code is to look at the community's best code, practice coding, summarize and evolve。Looking forward to more friends in the community to share Solidity's valuable experience and wonderful stories, have fun:)

