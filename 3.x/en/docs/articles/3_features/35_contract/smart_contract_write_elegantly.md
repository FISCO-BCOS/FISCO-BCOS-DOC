# How to write smart contracts elegantly

Author ： ZHANG Long ｜ FISCO BCOS Core Developer

## Write at the beginning

As we all know, the emergence of smart contracts allows blockchain to handle not only simple transfer functions, but also complex business logic processing, the core of which lies in the account model.。

Most of the current blockchain platforms integrate the Ethereum Virtual Machine and use Solidity as the development language for smart contracts.。The Solidity language not only supports basic / complex data type operations and logical operations, but also provides features related to high-level languages, such as inheritance and overloading.。

In addition, the Solidity language also has many common methods built in, such as a complete set of encryption algorithm interfaces, making data encryption and decryption very simple.；Provides event events to track the execution status of transactions and facilitate business logic processing, monitoring, and O & M。

However, when we write smart contract code, we still encounter various problems, including code bugs, scalability, maintainability, and business interoperability friendliness.。At the same time, the Solidity language is not perfect, needs to be executed on the EVM, the language itself and the execution environment will also bring us some holes.。

Based on this, we combine the previous projects and experience to sort out, hoping to summarize the problems encountered before, and provide reference for the follow-up development.。

**⊙ Note**: Smart contract security is not discussed in this article, the smart contract code is written in version 0.4.。

## Solidity FAQ

### EVM Stack Overflow

The stack depth of EVM is 1024, but the maximum access depth of EVM instruction set is 16, which brings many restrictions to the writing of smart contracts. Common errors are: stack overflows。

This error occurs during the smart contract compilation phase.。We know that the EVM stack is used to store temporary or local variables, such as function parameters or variables inside functions.。Optimization is generally from these two aspects.。

The following code snippet may have a stack overflow problem:

```

/ / If there are more than 14 courses, then the parameters exceed 16, then overflow
function addStudentScores(
  bytes32 studentId,
  bytes32 studentName,
  uint8 chineseScore;
  uint8 englishScore;
  ...
  uint8 mathScore
)
  public
    returns (bool)
{
    //TODO
}
```

Function parameters and local variables can not exceed 16, the general recommendation is not more than 10。Problems with too many parameters:

1. Easy stack overflow；
2. Writing code is difficult and error-prone.；
3. Not conducive to business understanding and maintenance；
4. Not easy to expand。

The general practice is to minimize the function parameters, encounter really can not reduce the situation, it is recommended to use the array。Local variables are similar to function parameters, too many definitions can also lead to stack overflow, you can splice arrays to reduce the number of variables, usually combined with array input parameters, the above code snippet optimization is as follows:

```
function addStudentScores(
  bytes32[] studentInfo,
    uint8[] scores
)
  public
    returns (bool)
{
    //TODO
}
```

### BINARY FIELD EXTRA LONG

The smart contract is compiled by the JAVA compiler to generate the corresponding JAVA contract, in which there is an important constant field BINARY, which is the code of the smart contract, that is, the contract code.。The contract code is used to sign when the contract is deployed, and the BINARY corresponding to each contract change will be different.。

When writing smart contracts, if a single smart contract code is long, the compiled BINARY field will be large。In the JAVA contract, the BINARY field is stored in the String type, and the maximum length of the String type is 65534. If the smart contract code is too much, the length of the BINARY field will exceed the maximum length of the String type, causing the String type to overflow and reporting an error.。

The solution is also very simple:

1. Reuse the code as much as possible, for example, some judgments appear multiple times in different methods and can be extracted, which is also convenient for subsequent maintenance.；
2. Contract split, the split of a contract into multiple contracts, the general occurrence of String out of bounds, basically can show that the contract design is unreasonable.。

### Use string type with caution

The string type is a special dynamic byte array that cannot be directly converted to a fixed-length array, and its parsing and array conversion are also very complex.。

In addition, the string type wastes space, is very expensive (consumes a lot of gas), and cannot be passed between contracts (except for the new experimental ABI compiler), so it is recommended to use bytes instead, except for special scenarios, such as unknown length byte arrays or reserved fields.。

⊙ **Remarks**The string type can be passed between contracts by adding a new experimental ABI compiler (code below) to the contract.。

```
pragma experimental ABIEncoderV2;
```

## Smart contract writing

### Layered design

Most examples of smart contracts on the Internet, such as the famous ERC20, are usually written in a smart contract file, which is not a problem in itself, but inevitably in the face of complex business.

1. Code all written in a file, this file is very large, not easy to view and understand, modify error-prone；
2. It is not easy for multiple people to collaborate and maintain, especially when business changes or code vulnerabilities occur, the deployment contract needs to be re-upgraded, resulting in the invalidation of the previous contract and the loss of relevant business data or assets.。

So, is there a way to upgrade a smart contract without affecting the original account (address)？

First answer: No.！(Except for CRUD based on the underlying distributed storage, currently FISCO BCOS 2.0 supports distributed storage, which can be upgraded directly through the CRUD operation database.。）

But！No does not mean that can not be upgraded, smart contract upgrade after the biggest problem is the data, so as long as the data is complete。

For example: we need to link the student information, the general writing is as follows:

```
contract Students {
  struct StudentInfo {
        uint32 _studentId;
        bytes32 _studentName;
    }
    mapping (uint32 => StudentInfo) private _studentMapping;
    function addStudent(uint32 studentId, bytes32 studentName) public returns(bool){
      //TODO：
    }
}
```

In this way, the code is all in a smart contract, if the existing smart contract can no longer meet the business requirements, such as the type of uint32 field needs to be upgraded to uint64, or a new field is added to the contract, such as sex, then the smart contract is useless and needs to be redeployed.。However, due to the redeployment, the contract address has changed and the previous data cannot be accessed.。

One approach is to layer contracts, separating business logic and data, as follows:

```
contract StudentController {
  mapping (uint32 => address) private _studentMapping;
    function addStudent(uint32 studentId, bytes32 studentName) public returns(bool){
      //TODO：
    }
}
contract Student {
  uint32 _studentId;
  bytes32 _studentName;
    //uint8 sex；
}
```

This writing method makes the logic and data separate. When you need to add a sex field, you can write two StudentController contracts for the original data. By version distinction, the new student adopts the new logic, which requires compatibility processing at the business level. The biggest problem is that the interactive operation of the original data needs to be completed across contracts, which is very inconvenient, such as querying all student information.。

We layered again, with an extra map layer dedicated to contract data management, even if there are problems with both the business logic layer and the data layer, it doesn't matter, just rewrite the business logic layer and the data layer, and perform special processing on the original data to be compatible.。However, this approach requires version control (version) in the data contract in advance, using different logic for different data.。

The biggest advantage of this approach is that all data is stored in StudentMap, changes to data contracts and logical contracts will not affect the data, and in subsequent upgrades, a controller contract can be used to achieve compatibility with old and new data, as shown below.

```
contract StudentController {
  mapping (uint32 => address) private _studentMapping;
  constructor(address studentMapping) public {
      _studentMapping = studentMapping;
    }
    function addStudent(uint version, uint32 studentId, bytes32 studentName, uint8 sex) public returns(bool){
      if(version == 1){
            //TODO
        }else if(version == 2){
            //TODO
        }
    }
}
contract StudentMap {
  mapping (uint32 => address) private _studentMapping;
    function getStudentMap() public constant returns(address){
      return _studentMapping;
    }
}
contract Student {
  uint8 version;
  uint32 _studentId;
  bytes32 _studentName;
    //uint8 sex；
}
```

### Unified Interface

Smart contracts have many high-level language features, but they still have many limitations.。For accurate business processing, you need to use Event events for tracking. For different contracts and methods, you can write different Event events, as follows:

PS: You can also use the require method to process, but the require method does not support dynamic variables, each require after processing needs to fill in a specific error content, in the SDK level coupling is too heavy, and is not easy to expand.。

```
contract StudentController {
  //other code
    event addStudentSuccessEvent(...); / / Omitted parameter, same below
    event addStudentFailEvent(...);
    
    function addStudent(bytes32 studentId, bytes32 studentName) public returns(bool){
      if(add success){
          addStudentSuccessEvent(...);
            return true;
        }else {
          addStudentFailEvent(...);
            return false;
        }
    }
}
```

There is no problem with this approach, but we need to write a large number of Event events, adding to the complexity of smart contracts.。Every time we add a new method or processing logic, we need to write a special event to track, the code is too intrusive and error-prone。

In addition, SDK development based on smart contracts requires writing a lot of non-reusable code to parse the Event event for each transaction (method) due to the different Event events.。This way of writing, the understanding and maintenance of the code is very poor.。To solve this problem, we just need to write a base contract CommonLib as follows:

```
contract CommonLib {
  //tx code
  bytes32 constant public ADD_STUDENT = "1";
    bytes32 constant public MODIFY_STUDENT_NAME = "2";
    
    //return code
    bytes32 constant public STUDENT_EXIST = "1001";
    bytes32 constant public STUDENT_NOT_EXIST = "1002";
    bytes32 constant public TX_SUCCESS = "0000";
    
  event commonEvent(bytes id, bytes32 txCode, bytes32 rtnCode);
}

contract StudentController is CommonLib {
     function addStudent(bytes32 studentId, bytes32 studenntName) public returns(bool) {
      //process add student
        if(add success){
          commonEvent(studentId, ADD_STUDENT, TX_SUCCESS);
            return true;
        }else {
          commonEvent(studentId, ADD_STUDENT, STUDENT_EXIST);
            return false;
        }
    }
    function modifyStudentName(bytes32 studentId, bytes32 studentName) public returns(bool){
      //TODO:
    }
}
```

When adding a modifyStudentName method or other contract, the original method is to define multiple Event events according to the possible situation of the method, and then write the parsing method for different events in the SDK, which is a lot of work.。Now you only need to define a pair of constants in CommonLib, and the SDK code can be completely reused with almost no new work。

⊙ **Note**: In the above example, commonEvent contains three parameters, where txCode is the transaction type, which is the transaction method called, and rtnCode is the return code, which indicates what happens when the transaction method represented by txCode is executed.。There is also an Id field in commonEvent, which is used to associate the business field studentId. In a specific project, the associated business field can be defined and adjusted by itself.。

### Code Details

Code details can experience a coder's ability and professional ethics.。When the business is in a hurry, code details are often overlooked, and code details (style) vary from person to person.。For a multi-person collaborative project, a unified code style and code specification can greatly improve R & D efficiency, reduce R & D and maintenance costs, and reduce code error rates.。

#### Naming Specification

There is no standard for naming smart contracts, but the team can follow an industry consensus specification.。After actual combat, recommend the following style (not mandatory), the following code block。

1. Contract naming: the use of hump naming, capital initials, and can express the corresponding business meaning.；
2. Method naming: the use of hump naming, the first letter is lowercase, and can express the corresponding business meaning.；
3. Event naming: Hump naming, initial lowercase, and can express the corresponding business meaning, ending with Event；
4. Contract variables: named after the hump, starting with _, with lowercase initials, and expressing the corresponding business meaning.；
5. Method entry: the use of hump naming, the first letter is lowercase, and can express the corresponding business meaning.；
6. Method parameters: It is recommended to write only the parameter type, without naming, except in special cases.；
7. Event parameters: the same method into the reference.；
8. Local variables: the same method into the reference.。

```
contract Student {
  bytes32 _studentId;
    bytes32 _studentName;
  event setStudentNameEvent(bytes32 studentId, bytes32 studentName);
  function setStudentName(bytes32 studentName) public returns(bool){}
    //other code
}
```

#### conditional judgment

In smart contracts, conditions can be judged through logical control, such as if statements, or built-in methods provided by the solidity language, such as require.。

There are some differences between the two in execution, in general, there is no problem using require, but require does not support parameter transfer, if the business needs to give a clear exception in the case of exception, it is recommended to use the if statement combined with the event, as follows。

```
event commonEvent(bytes id, bytes32 txCode, bytes32 rtnCode);
//require(!_studentMapping.studentExist(studentId),"student does not exist");
if(_studentMapping.studentExist(studentId)){
  commonEvent(studentId, ADD_STUDENT, STUDENT_EXIST);
  return false;
}
```

#### Constants and Notes

In smart contracts, constants, like other programming languages, need to be named in uppercase and underscore, and the naming needs to have business implications, and the need to use the constant keyword modification, it is recommended to place at the beginning of the contract.。

Constants also need to be distinguished, and external interface constants are decorated with public and placed in the base contract.。Business-related constants are decorated with private and placed in specific business logic contracts.。As follows:

```
contract CommonLib {
    //tx code
  bytes32 constant public ADD_STUDENT = "1";
    bytes32 constant public MODIFY_STUDENT_NAME = "2";
    ...
}

contract StudentController is CommonLib {
  /** student status */
    bytes32 constant private STUDENT_REGISTERED = "A";
    bytes32 constant private STUDENT_CANCELED = "C";
    
    //other code
}
```

Smart contract annotation with most programming languages, there is no very strict requirements。For some special fields, constants, each variable in the array, and specific logic, the method and Event can use /** comments */, specific fields and logical descriptions can be used / /。As follows:

```
/**
* stundent controller
*/
contract StudentController {
  /** add student */
    function addStudent(
      //[0]-seqNo;[1]-studentId;[2]-studentName;
      bytes32[3] studentInfos
)
      public returns(bool)
{
      //TODO:
    }
}
```

#### Pouting scheme

In the smart contract design process, no one can guarantee that their code will meet the business requirements, because business changes are absolute.。At the same time, no one can guarantee that the business and operators will not make mistakes, for example, the business does not check certain fields resulting in illegal data on the chain, or because the business operators hand errors, malicious operations, resulting in wrong data on the chain.。

Unlike other traditional systems, blockchain systems can modify data by manually modifying libraries or files, and blockchains must modify data through transactions.。

For business changes, you can add some reserved fields when writing smart contracts for possible subsequent business changes.。Generally defined as a generic data type is more appropriate, such as string, on the one hand, string type storage capacity is large, on the other hand, almost anything can be stored.。

We can store the extended data into the string field through data processing at the SDK level, and provide the corresponding data processing reverse operation to parse the data when using it, for example, in the Student contract, add the reserved field, as shown below.。At this stage, reserved has no effect and is empty in smart contracts.。

```
contract Student {
  //other code
    string _reserved;
    
    function getReserved() constant public returns(string){
    return _reserved;
  }

  function setReserved(string reserved) onlyOwner public returns(bool){
    _reserved = reserved;
    return true;
  }
}
```

For data errors caused by manual errors or illegal operations, be sure to reserve the relevant interfaces so that in an emergency, you can not modify the contract, but update the SDK to fix the data on the chain (SDK can not be implemented first)。For example, for the owner field in the Student contract, add the set operation.。

```
contract Student {
  //other code;
    address _owner;
  function setOwner(address owner) onlyOwner public returns(bool){
    _owner = owner;
    return true;
  }
}
```

Special attention should be paid to the fact that for reserved fields and reserved methods, their operation rights must be ensured to prevent the introduction of more problems。At the same time, reserved fields and reserved methods are an abnormal design, with advance consciousness, but must avoid over-design, which will lead to a waste of storage space of smart contracts, and improper use of reserved methods will bring hidden dangers to the security of the business.。

## Write at the end

The development of blockchain applications involves many aspects, smart contracts are the core, this article gives some suggestions and optimization methods in the process of developing smart contracts, but it is not complete and perfect, and essentially can not eliminate the emergence of bugs, but through optimization methods, you can make the code more robust and easy to maintain, from this point of view, has the basic conscience requirements of the industry.。