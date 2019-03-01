# FISCO BCOS“极简”Java应用开发入门
FISCO BCOS区块链平台用于搭建多方参与的联盟链。业务开发可以结合智能合约和web3sdk，开发向区块链部署智能合约、发送交易、获得结果的业务模块。

本文将介绍从头开发一个最简的Java业务示例应用，开发者通过该示例，跑通流程，后续可扩展更多的功能。主要内容分为如三部曲：
- 合约开发
- 合约编译
- SDK配置与开发

## 示例应用介绍

该示例应用将建立一个学生成绩管理模块。老师可以录入学生的课程分数，保存到区块链上，学生可以从区块链上查询课程分数。当课程分数需要更新时，老师可以更新区块链上的课程分数，学生可以从区块链上查询到更新后的课程分数。

## 前置条件
```eval_rst
.. important::

    - java版本
     要求 `jdk1.8+ <https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase8-2177648.html>`_，推荐使用jdk8u181。
    - FISCO BCOS区块链环境搭建
     参考 `FISCO BCOS建链教程 <https://fisco-doc.readthedocs.io/en/feature-2.0.0/docs/manual/build_chain.html>`_

```

## 合约开发

**业务表设计：** 实现学生成绩管理，首先需要设计一个存储学生成绩的表`t_student_score`，该表字段如下：
- name：主键，学生姓名(字符串类型)
- subject：课程名称(字符串类型)
- score：分数(整型)

其中name是主键，即操作`t_student_score`表时需要传入的字段，区块链根据该主键字段查询表中匹配的记录。该主键字段的值可以重复，与传统关系型数据库中的主键不同。`t_student_score`表示例如下：

| name |  subject   | score  |
|:---|:------|:------| 
| Alice | Math | 98 | 
| Alice | Chinese | 90 | 
| Bob | English | 95 |

**业务合约开发：**  设计好了学生成绩表`t_student_score`，现在需要设计一个管理学生成绩的智能合约可以操作该成绩表。FISCO BCOS提供[CRUD合约](../manual/crud_sol_contract.md)开发模式，可以通过合约创建表，并对创建的表进行增删改查操作。因此，我们采用CURD合约开发模式设计`StudentScore.sol`合约，通过该合约操作学生成绩表`t_student_score`。
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

```eval_rst
.. important::

    ``StudentScore.sol``合约的实现需要引入FISCO BCOS提供的一个系统合约接口文件``Table.sol``，该系统合约文件中的接口由FISCO BCOS底层提供实现。当业务合约需要操作CRUD接口时，均需要引入该接口合约文件。``Table.sol``合约详细接口可参考`这里 <../manual/crud_sol_contract.html>`_

```
> 第一步，我们根据业务需求设计了一个业务表`t_student_score`，根据设计的业务表，利用CRUD合约开发模式开发了一个业务合约`StudentScore.sol`。由于Java应用不能直接调用solidity合约文件，因此，接下来需要将开发的`StudentScore.sol`合约编译为Java合约文件，供Java应用使用。

## 合约编译
通过上一步，我们已经开发完成了学生成绩合约`StudentScore.sol`。现在将该solidity合约文件编译为对应的Java合约文件，FISCO BCOS的控制台提供了合约编译工具，可以方便使用。控制台的获取方式如下：
```bash
# 获取控制台压缩包
curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/console.tar.gz
# 解压得到控制台console目录
tar -zxf console.tar.gz
```
`console/tools`目录下提供了一个合约编译的可执行脚本`sol2java.sh`，运行该脚本可以将存放在`console/tools/contracts`目录下的solidity合约文件全部编译为Java合约文件。默认`console/tools/contracts`目录下已经存放了系统的CRUD合约接口文件`Table.sol`。因此，我们现在只需将开发的`StudentScore.sol`合约拷贝到该目录下即可。拷贝完毕后，可以运行合约编译脚本`sol2java.sh`，该脚本运行需要指定一个Java的包名参数，可以根据实际项目路径指定包名。执行命令如下：
```
./sol2java.sh org.bcos.student.score
```
运行成功之后，将会在console/tools目录生成java、abi、bin目录，如下所示。
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
|               |-- score
|                   |-- StudentScore.java // 编译成功的目标Java文件
|                   |-- Table.java  // 编译成功的系统CRUD合约接口Java文件，不需要导入到Java应用
|-- sol2java.sh
```
我们关注的是，java目录下生成了`org/bcos/student/score`包路径目录，包路径目录下将会生成Java合约文件`StudentScore.java`和`Table.java`。其中`StudentScore.java`Java合约文件正是Java应用所需要的Java文件。

> 第二步，我们通过FISCO BCOS提供的控制台合约编译工具将设计的`StudentScore.sol`合约编译为了`StudentScore.java`，下一步将进入SDK的配置与Java应用的开发。

## SDK配置与开发

### SDK配置
我们提供了一个Java工程项目供开发使用，首先获取Java工程项目：
```
# 获取Java工程项目压缩包
curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/student-score-app.tar.gz
# 解压得到Java工程项目bcos-client目录
tar -zxf student-score-app.tar.gz
```
**温馨提示:** 如果熟悉IDEA或者Eclipse集成开发工具，可以以Gradle项目的方式导入集成开发环境进行后续开发。

student-score-app项目的目录结构如下：
```bash
|-- build.gradle // gradle配置文件
|-- src
|   |-- main
|   |   |-- java
|   |       |-- org
|   |           |-- bcos
|   |               |-- student
|   |                   |-- client // 放置客户端调用类
|   |                   |   |-- BcosClient.java
|   |                   |-- contract // 放置Java合约类
|   |                   |   |-- StudentScore.java
|   |                   |-- service // 放置业务实现类，调用部署和调用合约
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
|-- tool
    |-- student_score.sh // 运行脚本
```

#### 项目引入SDK
引入了以太坊的solidity编译器相关jar包，需要在gradle配置文件build.gradle中添加以太坊的远程仓库：
```java
repositories {
    maven {
        url "http://maven.aliyun.com/nexus/content/groups/public/"
    }
    maven { url "https://dl.bintray.com/ethereum/maven/" }
    mavenCentral()
}
```
在build.gradle文件，引入SDK jar包到项目，增加如下依赖：
```java
compile ('org.fisco-bcos:web3sdk:2.0.2')
```

#### 节点证书与项目配置文件设置
首先拷贝区块链节点目录nodes/${ip}/sdk下的ca.crt 、node.crt和node.key到src/test/resource/目录下。然后配置applicationContext.xml。其配置如下图所示，红框标记的内容根据区块链节点配置做相应修改。

![](../../images/sdk/sdk_xml.png)

applicationContext.xml配置项详细说明:
- encryptType: 国密算法开关(默认为0)                              
  - 0: 不使用国密算法发交易                              
  - 1: 使用国密算法发交易(开启国密功能，需要连接的区块链节点是国密节点，搭建国密版FISCO BCOS区块链[参考这里](../manual/guomi_crypto.md))
- groupChannelConnectionsConfig: 
  - 配置待连接的群组，可以配置一个或多个群组，每个群组需要配置群组ID 
  - 每个群组可以配置一个或多个节点，设置群组节点的配置文件`config.ini`中`[rpc]`部分的`listen_ip`和`channel_listen_port`。
- channelService: 通过指定群组ID配置SDK实际连接的群组，指定的群组ID是groupChannelConnectionsConfig配置中的群组ID。SDK会与群组中配置的节点均建立连接，然后随机选择一个节点发送请求。

> 这一节，我们为应用配置好了SDK，下一步将进入实际业务开发。

### 业务开发:
将第二步编译好的StudentScore.java文件拷贝到org/bcos/student/score包目录下。并在该目录下创建BcosClient.java文件，用于部署和调用合约操作，实现我们的业务功能。BcosClient代码如下:

开发完毕BcosClient，我们一切准备就绪。下面开始一一验证开头定下的需求。
部署合约

老师记录学生成绩

学生查询成绩

老师修改学生成绩

学生再查询成绩 

老师移除学习成绩

学生查询成绩 
