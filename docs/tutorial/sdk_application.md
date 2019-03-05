# 极简Java应用开发

本文将介绍开发一个基于FISCO BCOS区块链的Java示例应用。开发者通过该示例，可以熟悉开发环境，应用配置，合约开发与编译。可以实现合约部署与调用功能，后续可以扩展其他功能。


```eval_rst
.. important::
    请参考 `安装文档 <../installation.html>`_ 完成FISCO BCOS区块链的搭建和控制台的下载工作。
```

## 示例应用需求

该示例应用将建立一个学生成绩管理系统，其基本业务需求如下：
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
curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/student-score-app.tar.gz
# 解压得到Java工程项目student-score-app目录
tar -zxf student-score-app.tar.gz
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
$ cd ~/fisco
# 拷贝节点证书到项目的资源目录
$ cp nodes/127.0.0.1/sdk/* student-score-app/src/test/resources/
```
- **`applicationContext.xml`配置**：**已提供默认配置，不需要更改。** 若搭建区块链的节点参数有改动，配置`applicationContext.xml`请参考[SDK使用文档](../sdk/api_configuration.html#spring)。

**小结：** 我们为应用配置好了SDK，下一步将进入实际业务开发。

## 业务开发
这一部分有三项工作，每一项工作增加一个Java类。**项目相关路径下已有开发完成的三个Java类，可以直接使用**。现在分别介绍这个三个Java类的设计与实现。
- `StudentScore.java`： 此类由`StudentScore.sol`通过控制台编译工具编译生成，提供了solidity合约接口对应的Java接口，放置在包路径目录`/src/main/java/org/bcos/student/contract`。
- `StudentScoreService.java`：此类负责应用的核心业务逻辑处理，通过调用`StudentScore.java`实现对合约的部署与调用。放置在包路径目录`/src/main/java/org/bcos/student/service`，其核心设计代码如下：
```java
// 部署合约
public String deployStudentScoreContract() throws Exception {

  StudentScore studentScore = StudentScore.deploy(web3j, credentials, new StaticGasProvider(gasPrice, gasLimit)).send();

  logger.info("Deploy  StudentScore contract successfully, address is {}", studentScore.getContractAddress());

  return studentScore.getContractAddress();
}

// 插入学生成绩
public void insertStudentScore(String name, String subject, BigInteger score) throws Exception {

  TransactionReceipt receipt = studentScore.insert(name, subject, score).send();
  List<InsertResultEventResponse> response = studentScore.getInsertResultEvents(receipt);
  
  if (response.isEmpty()) {
    throw new Exception("Insert failed, event log not found, may be transaction not exec.");
  }
  
  if ((response.get(0).count.compareTo(new BigInteger("1")) != 0)) {
    throw new Exception("Insert failed, ret code = " + response.get(0).count.toString());
  }
  logger.info("Insert  StudentScore contract successfully, ,name is {}, subject is {}, score is {} ", name, subject,
      score);
}

// 更新学生成绩
public void updateStudentScore(String name, String subject, BigInteger score) throws Exception {

  TransactionReceipt receipt = studentScore.update(name, subject, score).send();
  List<UpdateResultEventResponse> response = studentScore.getUpdateResultEvents(receipt);
  
  if (response.isEmpty()) {
    throw new Exception("Update failed, event log not found, may be transaction not exec.");
  }
  
  logger.info("Update  StudentScore contract successfully, ,name is {}, subject is {}, score is {} ", name, subject,
      score);
}

// 移除学生成绩
public void removeStudentScore(String name) throws Exception {

  TransactionReceipt receipt = studentScore.remove(name).send();
  List<RemoveResultEventResponse> response = studentScore.getRemoveResultEvents(receipt);
  
  if (response.isEmpty()) {
    throw new Exception("Remove failed, event log not found, may be transaction not exec.");
  }
  
  logger.info("Remove StudentScore contract successfully, name is {} ", name);

}

// 查询学生成绩 
public Tuple3<List<byte[]>, List<byte[]>, List<BigInteger>> selectStudentScore(String name)
    throws Exception {

  Tuple3<List<byte[]>, List<byte[]>, List<BigInteger>> result = studentScore.select(name).send();

  logger.info("Select StudentScore contract successfully, name is {}, result is {} ", name, result);

  return result;
}

```
- `StudentScoreClient.java`：此类是应用的入口，通过调用`StudenScoreService.java`实现业务功能。放置在包路径目录`/src/main/java/org/bcos/student/client`，其核心设计代码如下：
```java
// 应用的main函数入口
public static void main(String[] args) throws Exception {

  if (args.length < 1) {
    Usage();
  }

  StudentScoreClient client = new StudentScoreClient();
  client.initialize(args[0]);

  switch (args[0]) {
  case "deploy":
    client.deployStudentScoreAndRecordAddr();
    break;
  case "select":
    if (args.length < 2) {
      Usage();
    }
    client.selectStudentScore(args[1]);
    break;
  case "update":
    if (args.length < 4) {
      Usage();
    }
    client.updateStudentScore(args[1], args[2], new BigInteger(args[3]));
    break;
  case "remove":
    if (args.length < 2) {
      Usage();
    }
    client.removeStudentScore(args[1]);
    break;
  case "insert":
    if (args.length < 4) {
      Usage();
    }
    client.insertStudentScore(args[1], args[2], new BigInteger(args[3]));
    break;

  default: {
    Usage();
  }
  }

  System.exit(0);
}

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
  
  StudentScoreService studentScoreService = new StudentScoreService();
  studentScoreService.setCredentials(credentials);
  studentScoreService.setWeb3j(web3j);

  setStudentScoreService(studentScoreService);

  // 日志记录发送交易的账号地址和私钥
  logger.debug("address is " + credentials.getAddress() + ", privateKey is " + credentials.getEcKeyPair().getPrivateKey().toString(16));
  
  // 加载合约地址
  if (!cmd.equals("deploy")) {
    
    Properties prop = new Properties();
    final Resource contractResource = new ClassPathResource("contract.properties");
    prop.load(contractResource.getInputStream());
    String contractAddress = prop.getProperty("address");
    
    if (contractAddress == null || contractAddress.trim().equals("")) {
      throw new Exception("Load student score contract address failed, deploy it first. ");
    }
    
    StudentScore studentScore = StudentScore.load(contractAddress, web3j, credentials, new StaticGasProvider(new BigInteger("300000000"), new BigInteger("300000000")));
    studentScoreService.setStudentScore(studentScore);
  }
}

// 部署合约
public void deployStudentScoreAndRecordAddr() {

  try {
    String address = studentScoreService.deployStudentScoreContract();
    System.out.println("Deploy StudentScore contract successfully, contract address is " + address);

    Properties prop = new Properties();
    prop.setProperty("address", address);
    final Resource contractResource = new ClassPathResource("contract.properties");
    FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
    prop.store(fileOutputStream, "contract address");

  } catch (Exception e) {
    System.out.println("Deploy StudentScore contract failed, error message is  " + e.getMessage());
  }
}
// 插入学生成绩，参数：学生姓名，学科，分数
public void insertStudentScore(String name, String subject, BigInteger score) {
  try {

    studentScoreService.insertStudentScore(name, subject, score);

    System.out.println("Insert student score successfully. ");
  } catch (Exception e) {
    System.out.println("Insert student score failed, error message is " + e.getMessage());
  }
}
// 更新学生成绩，参数：学生姓名，学科，分数
public void updateStudentScore(String name, String subject, BigInteger score) {

  try {

    studentScoreService.updateStudentScore(name, subject, score);

    System.out.println("Update student score successfully. ");
  } catch (Exception e) {
    System.out.println("Update student score failed, error message is " + e.getMessage());
  }
}
// 移除学生成绩，参数：学生姓名
public void removeStudentScore(String name) {
  try {

    studentScoreService.removeStudentScore(name);

    System.out.println("Remove student score successfully. ");
  } catch (Exception e) {
    System.out.println("Remove student score failed, error message is " + e.getMessage());
  }
}
// 查询学生成绩，参数：学生姓名
public void selectStudentScore(String name) {
  try {

    Tuple3<List<byte[]>, List<byte[]>, List<BigInteger>> result = studentScoreService
        .selectStudentScore(name);

    List<byte[]> value1 = result.getValue1();
    List<byte[]> value2 = result.getValue2();
    List<BigInteger> value3 = result.getValue3();

    System.out.println(name + "'s score count = " + value1.size());

    for (int i = 0; i < value1.size(); i++) {
      System.out.printf("Subject => %s, score => %s\n", new String(value2.get(i)), value3.get(i).toString());
    }
  } catch (Exception e) {
    System.out.println("Select student score failed, error message is " + e.getMessage());
  }
}
```
**小结：** 通过Java合约文件，设计了一个业务Service类和调用入口类，已完成学生成绩管理系统的业务功能。接下来可以运行项目，测试功能是否正常。

## 运行
编译项目。
```bash
# 切换到项目目录
$ cd ~/fisco/student-score-app
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
