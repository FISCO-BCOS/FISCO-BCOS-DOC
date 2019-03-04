# 构建第一个区块链应用

本章将会介绍一个基于FISCO BCOS区块链的业务应用场景开发全过程，从业务场景分析，到合约的设计实现，然后介绍合约编译以及如何部署到区块链，最后介绍一个应用模块的实现，通过我们提供的Java SDK实现对区块链上合约的调用访问。  

本章教程要求用户具备Java开发的基本技能，能够使用Gradle工具，有一定的Solidity使用经验，如果没有也无妨，能大致看懂合约逻辑即可。
通过学习本章教程，你将会掌握以下几个重要知识点（如果走完流程之后仍然未掌握，可以到各个知识点的章节详细了解对应的教程）  
1，知道如何将一个业务场景的逻辑用合约的形式表达  
2，知道如何将Solidity合约转化成Java类  
3，知道如何配置Web3SDK，掌握Web3SDK配置所需相关文件  
4，知道如何构建一个应用，并将SDK集成进来  
5，知道如何调用SDK的最基本接口，完成合约调用访问  

最后，教程中会提供示例的完整项目源码，用户可以在此基础上快速的修改或者集成自己的功能模块。

```eval_rst
.. important::
    请参考 `安装文档 <../installation.html>`_ 完成FISCO BCOS区块链的搭建和控制台的下载工作。
```

## 示例应用需求

区块链天然具有防串改的特性，因此将学生成绩，本文提供一个学生成绩管理的示例，其基本业务需求如下：
- 老师可以录入学生的课程分数，保存到区块链上。
- 学生可以从区块链上查询课程分数。
- 当课程分数需要更新时，老师可以更新区块链上的课程分数。学生可以从区块链上查询到更新后的课程分数。

## 合约开发

**业务表设计：** 实现学生成绩管理，首先需要设计一个存储学生成绩的表`t_student_score`，该表字段如下：
- name：主键，学生姓名(字符串类型)
- subject：课程名称(字符串类型)
- score：分数(整型)

其中name是主键，即操作`t_student_score`表时需要传入的字段，区块链根据该主键字段查询表中匹配的记录。与传统关系型数据库中的主键不同，该主键字段的值可以重复。`t_student_score`表示例如下：


```eval_rst

+------+----------+-------+
|name  |subject   |score  |
+======+==========+=======+
|Alice |Math      |98     |
+------+----------+-------+
|Alice |Chinese   |90     |
+------+----------+-------+
|Bob   |English   |95     |
+------+----------+-------+

```

**业务合约开发：**  针对学生成绩表，可以设计一个学生成绩的智能合约对学生成绩表进行操作。FISCO BCOS提供[CRUD合约](../manual/smart_contract.html#crud)开发模式，可以通过合约创建表，并对创建的表进行增删改查操作。因此，我们采用CURD合约开发模式设计`StudentScore.sol`合约，通过该合约操作学生成绩表`t_student_score`。
- `StudentScore.sol`合约设计如下：
```solidity
pragma solidity ^0.4.25;

// Table.sol与StudentScore.sol放在同级目录时，需要在合约名前加"./"符号。
import "./Table.sol"; 

contract StudentScore {
    
    // 插入成绩记录event事件
    event insertResult(int count);
    // 更新成绩记录event事件
    event updateResult(int count);
    // 移除成绩记录event事件
    event removeResult(int count);

    // 合约构造函数，构造函数调用将会创建学生成绩表
    function StudentScore() public {
        createTable();
    }

    // 创建学生成绩表
    function createTable() public {
        TableFactory tf = TableFactory(0x1001); 
        // 参数：表名，主键字段，普通字段(使用逗号分隔)
        tf.createTable("t_student_score", "name", "subject,score");
    }

    // 打开学生成绩表
    function openTable() public returns(Table) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_student_score");
        return table;
    }

    // 查询成绩记录
    function select(string name) public constant returns(bytes32[], bytes32[], int[]) {

        Table table = openTable();
        Condition condition = table.newCondition();
        
        Entries entries = table.select(name, condition);
        bytes32[] memory name_list = new bytes32[](uint256(entries.size()));
        bytes32[] memory subject_list = new bytes32[](uint256(entries.size()));
        int[] memory score_list = new int[](uint256(entries.size()));
        
        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);
            
            name_list[uint256(i)] = entry.getBytes32("name");
            subject_list[uint256(i)] = entry.getBytes32("subject");
            score_list[uint256(i)] = entry.getInt("score");
        }
 
        return (name_list, subject_list, score_list);
    }

    // 插入成绩记录
    function insert(string name, string subject, int score) public returns(int) {

        Table table = openTable();
        
        Entry entry = table.newEntry();
        entry.set("name", name);
        entry.set("subject", subject);
        entry.set("score", score);
        
        int count = table.insert(name, entry);
        insertResult(count);
        
        return count;
    }

    // 更新成绩记录
    function update(string name, string subject, int score) public returns(int) {

        Table table = openTable();
       
        Entry entry = table.newEntry();
        entry.set("name", name);
        entry.set("subject", subject);
        entry.set("score", score);
        
        Condition condition = table.newCondition();
        condition.EQ("name", name);
        condition.EQ("subject", subject);
        
        int count = table.update(name, entry, condition);
        updateResult(count);
        
        return count;
    }

    // 移除成绩记录
    function remove(string name) public returns(int) {

        Table table = openTable();
       
        Condition condition = table.newCondition();
        condition.EQ("name", name);
        
        int count = table.remove(name, condition);
        removeResult(count);
        
        return count;
    }
}
```

 **注：** `StudentScore.sol`合约的实现需要引入FISCO BCOS提供的一个系统合约接口文件 `Table.sol` ，该系统合约文件中的接口由FISCO BCOS底层实现。当业务合约需要操作CRUD接口时，均需要引入该接口合约文件。`Table.sol` 合约详细接口[参考这里](../manual/smart_contract.html#crud)。

**小结：** 我们根据业务需求设计了一个业务表`t_student_score`。根据设计的业务表，利用CRUD合约开发模式开发了一个业务合约`StudentScore.sol`。由于Java应用不能直接调用solidity合约文件，下一步将开发的`StudentScore.sol`合约编译为Java合约文件。

## 合约编译
控制台提供了合约编译工具。将`StudentScore.sol`存放在`console/tools/contracts`目录，利用console/tools目录下提供的`sol2java.sh`脚本执行合约编译，命令如下：
```bash
# 切换到fisco/console/tools目录
$ cd ~/fisco/console/tools/
# 编译合约，后面指定一个Java的包名参数，可以根据实际项目路径指定包名
$ ./sol2java.sh org.bcos.student.contract
```
运行成功之后，将会在console/tools目录生成java、abi和bin目录，如下所示。
```bash
|-- abi // 编译生成的abi目录，存放solidity合约编译的abi文件
|   |-- StudentScore.abi
|   |-- Table.abi
|-- bin // 编译生成的bin目录，存放solidity合约编译的bin文件
|   |-- StudentScore.bin
|   |-- Table.bin
|-- contracts // 存放solidity合约源码文件，将需要编译的合约拷贝到该目录下
|   |-- StudentScore.sol // 拷贝进来的StudentScore.sol合约，依赖Table.sol
|   |-- Table.sol // 默认提供的系统CRUD合约接口文件
|-- java  // 存放编译的包路径及Java合约文件
|   |-- org
|       |-- bcos
|           |-- student
|               |-- contract
|                   |-- StudentScore.java // 编译成功的目标Java文件
|                   |-- Table.java  // 编译成功的系统CRUD合约接口Java文件
|-- sol2java.sh
```
我们关注的是，java目录下生成了`org/bcos/student/student/contract`包路径目录。包路径目录下将会生成Java合约文件`StudentScore.java`和`Table.java`。其中`StudentScore.java`是Java应用所需要的Java合约文件。

**小结：** 我们通过控制台合约编译工具将设计的`StudentScore.sol`合约编译为了`StudentScore.java`，下一步将进入SDK的配置与业务的开发。

## SDK配置

我们提供了一个Java工程项目供开发使用，首先获取Java工程项目：
```
# 获取Java工程项目压缩包
$ cd ~
$ curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/student-score-app.tar.gz
# 解压得到Java工程项目student-score-app目录
$ tar -zxf student-score-app.tar.gz
```
student-score-app项目的目录结构如下：
```bash
|-- build.gradle // gradle配置文件
|-- gradle
|   |-- wrapper
|       |-- gradle-wrapper.jar // 用于下载Gradle的相关代码实现
|       |-- gradle-wrapper.properties // wrapper所使用的配置信息，比如gradle的版本等信息
|-- gradlew // Linux或者Unix下用于执行wrapper命令的Shell脚本
|-- gradlew.bat // Windows下用于执行wrapper命令的批处理脚本
|-- settings.gradle
|-- src
|   |-- main
|   |   |-- java
|   |       |-- org
|   |           |-- bcos
|   |               |-- student
|   |                   |-- client // 放置客户端调用类
|   |                   |   |-- StudentScoreClient.java
|   |                   |-- contract // 放置Java合约类
|   |                   |   |-- StudentScore.java
|   |                   |-- service // 放置业务实现类，部署和调用合约
|   |                       |-- StudentScoreService.java
|   |-- test 
|       |-- java 
|       |-- resources // 存放代码资源文件
|           |-- applicationContext.xml // 项目配置文件
|           |-- ca.crt // 区块链ca证书
|           |-- node.crt // 区块链ca证书
|           |-- node.key // 节点证书
|           |-- contract.properties // 存储部署合约地址的文件
|           |-- log4j.properties // 日志配置文件
|           |-- contract //存放solidity约文件
|           |   |-- StudentScore.sol
|           |   |-- Table.sol

|-- tool
    |-- run.sh // 项目运行脚本
```

### 项目引入SDK
**项目的`build.gradle`文件已引入SDK，不需修改**。其引入方法介绍如下：
- SDK引入了以太坊的solidity编译器相关jar包，因此在`build.gradle`文件需要添加以太坊的远程仓库：
```java
repositories {
    maven {
        url "http：//maven.aliyun.com/nexus/content/groups/public/"
    }
    maven { url "https：//dl.bintray.com/ethereum/maven/" }
    mavenCentral()
}
```
- 引入SDK jar包，增加如下依赖：
```java
compile ('org.fisco-bcos：web3sdk：2.0.2')
```

### 节点证书与项目配置文件设置
- **区块链节点证书配置：**  
```bash
# 进入~/fisco目录
# 拷贝节点证书到项目的资源目录
$ cd ~
$ cp fisco/nodes/127.0.0.1/sdk/* student-score-app/src/test/resources/
```
- `student-score-app/src/test/resources/applicationContext.xml`是从fisco/nodes/127.0.0.1/sdk/复制而来，已默认配置好，不需要做额外修改。若搭建区块链节点的channel_ip与channel_port有所改动，需要配置`applicationContext.xml`，具体请参考[SDK使用文档](../sdk/api_configuration.html#spring)。

**小结：** 我们为应用配置好了SDK，下一步将进入实际业务开发。

## 业务开发
这一部分有三项工作，每一项工作增加一个Java类。**项目相关路径下已有开发完成的三个Java类，可以直接使用**。现在分别介绍这个三个Java类的设计与实现。
- `StudentScore.java`： 此类由`StudentScore.sol`通过控制台编译工具编译生成，提供了solidity合约接口对应的Java接口，放置在包路径目录`/src/main/java/org/bcos/student/contract`。
- `StudentScoreService.java`：此类负责应用的核心业务逻辑处理，通过调用`StudentScore.java`实现对合约的部署与调用。放置在包路径目录`/src/main/java/org/bcos/student/service`。
- `StudentScoreClient.java`：此类是应用的入口，通过调用`StudenScoreService.java`实现业务功能。放置在包路径目录`/src/main/java/org/bcos/student/client`，其核心设计代码如下：
```java
// 应用的main函数入口
public void initialize(String cmd) throws Exception {

  // 初始化Service
  ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml"); 
  Service service = context.getBean(Service.class);
  service.run();

  ChannelEthereumService channelEthereumService = new ChannelEthereumService();
  channelEthereumService.setChannelService(service);
  // 创建Web3j对象，第二个参数为群组ID
  Web3j web3j = Web3j.build(channelEthereumService, 1);

  // 构建随机的交易账号
  // 可以通过指定特定私钥的方式指定特定账号发生交易，示例：
  // Credentials credentials = Credentials.create("3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4");
  Credentials credentials = Credentials.create(Keys.createEcKeyPair());

  // others
}
```
**小结：** 通过Java合约文件，设计了一个业务Service类和调用入口类，已完成学生成绩管理系统的业务功能。接下来可以运行项目，测试功能是否正常。

## 运行
编译项目。
```bash
# 切换到项目目录
$ cd ~/student-score-app
# 编译项目
$ ./gradlew build
```
编译成功之后，将在项目根目录下生成`dist`目录。dist目录下有一个`run.sh`脚本，简化项目运行。现在开始一一验证本文开始定下的需求。

- 部署`StudentScore.sol`合约
```bash
# 进入dist目录
$ cd dist
$ ./run.sh deploy
Deploy StudentScore contract successfully, contract address is 0xd996558d2fceeca464b454a745a0aa5832fac715
```
- 老师录入学生课程分数
```bash
$ ./run.sh insert Alice Math 75
Insert student score successfully. 
```
- 学生查询课程分数
```bash
$ ./run.sh select Alice              
Alice's score count = 1
Subject => Math, score => 75
```
- 老师修改学生课程分数
```bash
$ ./run.sh update Alice Math 98
Update student score successfully.
```
- 学生查询课程分数 
```bash
$ ./run.sh select Alice
Alice's score count = 1
Subject => Math, score => 98
```
- 老师移除学习课程分数
```bash
$ ./run.sh remove Alice 
Remove student score successfully.
```
- 学生查询课程分数 
```bash
$ ./run.sh select Alice
Alice's score count = 0
```
**总结：** 至此，我们通过合约开发，合约编译，SDK配置与业务开发构建了一个基于FISCO BCOS联盟区块链的应用。
