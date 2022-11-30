# 使用预编译合约

标签：``预编译合约`` ``BFS`` ``CRUD`` 

---

FISCO BCOS 3.0 沿用了FISCO BCOS 2.0版本的预编译合约。未来，我们还会尝试将现有的典型业务场景抽象，开发成预编译合约模板，作为底层提供的基础能力，帮助用户更快的更方便的在业务中使用FISCO BCOS。

预编译合约的原理与FISCO BCOS 2.0+版本的类似，用户在研究其原理时可以参考链接：[FISCO BCOS预编译合约架构](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/precompiled_contract.html?highlight=%E9%A2%84%E7%BC%96%E8%AF%91#fisco-bcos)。

## 预编译合约的优势

**可访问分布式存储接口**：基于这套框架，用户可以访问本地DB存储状态，实现自己需要的任何逻辑。

**更好的性能表现**：由于实现是C++代码，会编译在底层中，不需要进入EVM执行，可以有更好的性能。

**无需学习Solidity语言即可上手**：基于FISCO BCOS**预编译**合约框架，开发者可以使用C++开发自己的预编译合约，快速实现需要的业务逻辑，而不需要学习Solidity语言。

## FISCO BCOS 3.x 预编译合约及地址

由于Solidity合约目前只支持20字节的Address类型作为调用对象，而Liquid支持字符串的地址进行合约调用合约，因此预编译合约的地址分为Solidity和Liquid两种版本。

该表中的地址只用于Solidity合约。

| 地址    | 合约                    | 说明                       |
| :------ | :---------------------- | :------------------------- |
| 0x1000  | SystemConfigPrecompiled | 实现对群组系统参数配置管理 |
| 0x1002  | TableManagerPrecompiled | Table合约管理              |
| 0x1003  | ConsensusPrecompiled    | 群组节点及节点身份管理     |
| 0x1005  | AuthManagerPrecompiled  | 基于合约的权限控制         |
| 0x100a  | CryptoPrecompiled       | 提供密码学接口             |
| 0x100c  | DAGTransferPrecompiled  | 提供DAG转账测试合约        |
| 0x100e  | BFSPrecompiled          | BFS系统合约接口            |
| 0x10001 | AuthManagerPrecompiled  | 权限治理委员会合约         |
| 0x5004  | GroupSignPrecompiled    | 群签名系统合约             |
| 0x5005  | RingSignPrecompield     | 环签名系统合约             |
| 0x5100  | ZKPPrecompiled          | ZKP系统合约                |

下表的BFS路径只用于 webankblockchain-liquid（以下简称WBC-Liquid）合约。

| BFS路径            | 合约                    | 说明                       |
| :----------------- | :---------------------- | :------------------------- |
| /sys/status        | SystemConfigPrecompiled | 实现对群组系统参数配置管理 |
| /sys/table_Manager | TableManagerPrecompiled | Table合约管理              |
| /sys/consensus     | ConsensusPrecompiled    | 群组节点及节点身份管理     |
| /sys/auth          | AuthManagerPrecompiled  | 基于合约的权限控制         |
| /sys/crypto_tools  | CryptoPrecompiled       | 提供密码学接口             |
| /sys/dag_test      | DAGTransferPrecompiled  | 提供DAG转账测试合约        |
| /sys/bfs           | BFSPrecompiled          | BFS系统合约接口            |
| /sys/group_sig     | GroupSignPrecompiled    | 群签名系统合约             |
| /sys/ring_sig      | RingSignPrecompield     | 环签名系统合约             |
| /sys/discrete_zkp  | ZKPPrecompiled          | ZKP系统合约                |

## 如何使用FISCO BCOS预编译合约接口

智能合约在调用预编译合约的步骤，与调用普通合约类似，步骤如下：

- 引入接口声明：引入预编译合约的合约文件，或者在同个智能合约文件中声明接口；
- 指定合约地址：根据预编译合约的地址表，在初始化对象时使用对应地址即可；
- 调用对象接口：初始化对象结束后，使用该对象调用方法接口即可；

下文以Table合约为例，实现对Table接口的调用：

1. 引入接口声明

   将Table.sol合约放入TableTest.sol同级目录，并在TableTest.sol合约文件中引入Table.sol，其代码如下：

   ```solidity
   pragma solidity >=0.6.10 <0.8.20;
   pragma experimental ABIEncoderV2;
   import "./Table.sol";
   ```

2. 指定合约地址

   指定 `TableManager` 的地址，初始化 `TableManager` 对象

   ```solidity
   TableManager constant tm =  TableManager(address(0x1002));
   ```

3. 调用对象接口

   调用已经初始化的  `TableManager` 对象接口，创建 `Table`合约对象

   ```solidity
   Table table;
   string constant TABLE_NAME = "t_test";
   constructor () public{
       // 创建t_test表，表的主键名为id，其他字段名为name和age
       string[] memory columnNames = new string[](2);
       columnNames[0] = "name";
       columnNames[1] = "age";
       TableInfo memory tf = TableInfo("id", columnNames);
       tm.createTable(TABLE_NAME, tf);
       
       // 获取真实的地址，存在合约中
       address t_address = tm.openTable(TABLE_NAME);
       require(t_address!=address(0x0),"");
       table = Table(t_address);
   }
   ```

   
