# Using Precompiled Contracts

Tags: "Precompiled Contracts" "BFS" "CRUD"

---

FISCO BCOS 3.0 follows the FISCO BCOS 2.0 version of the precompiled contract。In the future, we will also try to abstract the existing typical business scenarios and develop them into pre-compiled contract templates as the basic capability provided by the underlying layer to help users use FISCO BCOS in their business faster and more conveniently.。

Principles of Precompiled Contracts and FISCO BCOS 2.0+Similar to the version, users can refer to the link when studying its principles: [FISCO BCOS Precompiled Contract Architecture](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/precompiled_contract.html?highlight=%E9%A2%84%E7%BC%96%E8%AF%91#fisco-bcos)。

## Advantages of Precompiled Contracts

**Access to distributed storage interfaces**Based on this framework, users can access the local DB storage state and implement any logic they need.。

**Better performance**Since the implementation is C++The code will be compiled in the underlying layer without entering the EVM for execution, which can have better performance。

**Get started without learning Solidity language**Based on FISCO BCOS**Precompiled**contract framework, developers can use C++Develop your own pre-compiled contracts to quickly implement the required business logic without learning the Solidity language。

## FISCO BCOS 3.x Precompiled Contracts and Addresses

Currently, the Solidity contract only supports the address type of 20 bytes as the calling object, while Liquid supports the address of the string to call the contract, so the address of the precompiled contract is divided into two versions: Solidity and Liquid.。

Addresses in this table are for Solidity contracts only。

| Address| 合同| Description|
| :------ | :---------------------- | :------------------------- |
| 0x1000  | SystemConfigPrecompiled | Realize configuration management of group system parameters|
| 0x1002  | TableManagerPrecompiled | Table Contract Management|
| 0x1003  | ConsensusPrecompiled    | Group node and node identity management|
| 0x1005  | AuthManagerPrecompiled  | Contract-based permission control|
| 0x100a  | CryptoPrecompiled       | Provide cryptographic interface|
| 0x100c  | DAGTransferPrecompiled  | Provide DAG Transfer Test Contract|
| 0x100e  | BFSPrecompiled          | BFS System Contract Interface|
| 0x10001 | AuthManagerPrecompiled  | Authority Governance Committee Contract|
| 0x5004  | GroupSignPrecompiled    | Group Signature System Contract|
| 0x5005  | RingSignPrecompield     | Ring Signature System Contract|
| 0x5100  | ZKPPrecompiled          | ZKP System Contract|

The BFS path of the following table is only used for webankblockchain-liquid (hereinafter referred to as WBC-Liquid) contract。

| BFS Path| 合同| Description|
| :----------------- | :---------------------- | :------------------------- |
| /sys/status        | SystemConfigPrecompiled | Realize configuration management of group system parameters|
| /sys/table_Manager | TableManagerPrecompiled | Table Contract Management|
| /sys/consensus     | ConsensusPrecompiled    | Group node and node identity management|
| /sys/auth          | AuthManagerPrecompiled  | Contract-based permission control|
| /sys/crypto_tools  | CryptoPrecompiled       | Provide cryptographic interface|
| /sys/dag_test      | DAGTransferPrecompiled  | Provide DAG Transfer Test Contract|
| /sys/bfs           | BFSPrecompiled          | BFS System Contract Interface|
| /sys/group_sig     | GroupSignPrecompiled    | Group Signature System Contract|
| /sys/ring_sig      | RingSignPrecompield     | Ring Signature System Contract|
| /sys/discrete_zkp  | ZKPPrecompiled          | ZKP System Contract|

## How to use the FISCO BCOS precompiled contract interface

The steps for a smart contract to invoke a precompiled contract are similar to those for invoking a normal contract, as follows.

- Introducing an interface declaration: Introducing a contract file for a precompiled contract, or declaring an interface in the same smart contract file；
- Specify the contract address: According to the address table of the precompiled contract, the corresponding address can be used when initializing the object.；
- Call the object interface: After initializing the object, use the object to call the method interface.；

The following uses the Table contract as an example to call the Table interface:

1. Introduction of interface declarations

   Place the Table.sol contract in the TableTest.sol peer directory and introduce Table.sol in the TableTest.sol contract file. The code is as follows:

   ```solidity
   pragma solidity >=0.6.10 <0.8.20;
   pragma experimental ABIEncoderV2;
   import "./Table.sol";
   ```

2. Specify Contract Address

   Initializes the 'TableManager' object by specifying the address of the 'TableManager'

   ```solidity
   TableManager constant tm =  TableManager(address(0x1002));
   ```

3. Call the object interface.

   Call the initialized 'TableManager' object interface to create a 'Table' contract object

   ```solidity
   Table table;
   string constant TABLE_NAME = "t_test";
   constructor () public{
       / / Create the t _ test table. The primary key of the table is id, and the other fields are name and age.
       string[] memory columnNames = new string[](2);
       columnNames[0] = "name";
       columnNames[1] = "age";
       TableInfo memory tf = TableInfo("id", columnNames);
       tm.createTable(TABLE_NAME, tf);
       
       / / Get the real address, which is stored in the contract
       address t_address = tm.openTable(TABLE_NAME);
       require(t_address!=address(0x0),"");
       table = Table(t_address);
   }
   ```

   
