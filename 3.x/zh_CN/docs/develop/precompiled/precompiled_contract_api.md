# 预编译合约接口

标签：``预编译合约`` ``接口``

---

FISCO BCOS 3.0 沿用了FISCO BCOS 2.0版本的预编译合约。未来，我们还会尝试将现有的典型业务场景抽象，开发成预编译合约模板，作为底层提供的基础能力，帮助用户更快的更方便的在业务中使用FISCO BCOS。

## FISCO BCOS当前系统合约及地址

该表中的地址只用于Solidity合约。

在FISCO BCOS 3.0暂未开放Table系统合约接口的使用，敬请期待。

| 地址             | 合约                      | 说明                                   |
| :--------------- | :------------------------ | :------------------------------------- |
| 0x1000           | SystemConfigPrecompiled   | 实现对群组系统参数配置管理             |
| 0x1001（不支持） | TablePrecompiled          | Solidity中使用的Table                  |
| 0x1003           | ConsensusPrecompiled      | 群组节点及节点身份管理                 |
| 0x1004           | CNSPrecompiled            | 保存更新CNS(contract name service)信息 |
| 0x1005           | ContractAuthPrecompiled   | 基于合约的权限控制                     |
| 0x1006           | ParallelConfigPrecompiled | Solidity中合约并行接口配置             |
| 0x1009           | KVTableFactoryPrecompiled | Solidity中使用KVTable                  |
| 0x100a           | CryptoPrecompiled         | 提供密码学接口                         |
| 0x100c           | DAGTransferPrecompiled    | 提供DAG转账测试合约                    |
| 0x100e           | BFSPrecompiled            | BFS系统合约接口                        |

下表的BFS路径只用于 webankblockchain-liquid（以下简称WBC-Liquid）合约。

| BFS路径              | 合约                      | 说明                                   |
| :------------------- | :------------------------ | :------------------------------------- |
| /sys/status          | SystemConfigPrecompiled   | 实现对群组系统参数配置管理             |
| /sys/table_storage   | TablePrecompiled          | Solidity中使用的Table                  |
| /sys/consensus       | ConsensusPrecompiled      | 群组节点及节点身份管理                 |
| /sys/cns             | CNSPrecompiled            | 保存更新CNS(contract name service)信息 |
| /sys/auth            | ContractAuthPrecompiled   | 基于合约的权限控制                     |
| /sys/parallel_config | ParallelConfigPrecompiled | Solidity中合约并行接口配置             |
| /sys/kv_storage      | KVTableFactoryPrecompiled | Solidity中使用KVTable                  |
| /sys/crypto_tools    | CryptoPrecompiled         | 提供密码学接口                         |
| /sys/dag_test        | DAGTransferPrecompiled    | 提供DAG转账测试合约                    |
| /sys/bfs             | BFSPrecompiled            | BFS系统合约接口                        |

## 预编译合约接口描述与SDK支持

### 1. SystemConfigPrecompiled

#### 合约地址

- Solidity：0x1000
- WBC-Liquid：/sys/status

#### 接口声明

以Solidity为例

```solidity
pragma solidity ^0.6.0;

contract SystemConfigPrecompiled
{
  function setValueByKey(string memory key, string memory value) public returns(int256){}
  function getValueByKey(string memory key) public view returns(string memory,int256){}
}
```

#### setValueByKey说明

**入参：**

- key表示配置项名称，当前支持的参数有`tx_count_limit`, `consensus_leader_period`。
- value表示对应配置项的值，其中`tx_count_limit`默认值为1000，不可设置为负数，`consensus_leader_period`默认值为1，不可设置为负数。

**返回：**

- setValueByKey将以错误码的形式返回

| 错误码        | 说明               |
| :------------ | :----------------- |
| 错误码大等于0 | 修改所影响的行数   |
| -51300        | 输入的系统参数有误 |

#### getValueByKey说明

**入参：**

- key表示配置项名称，当前支持的参数有`tx_count_limit`, `consensus_leader_period`。

**返回：**

- 返回具体的值和生效的区块高度

#### SDK支持

- [Java SDK](../sdk/java_sdk/index.md)

### ConsensusPrecompiled

#### 合约地址

- Solidity：0x1003
- WBC-Liquid: /sys/consensus

#### 接口声明

以Solidity为例

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract ConsensusPrecompiled {
    function addSealer(string memory,uint256) public returns (int256){}
    function addObserver(string memory) public returns (int256){}
    function remove(string memory) public returns (int256){}
    function setWeight(string memory,uint256) public returns (int256){}
}
```

#### 接口说明

- addSealer添加一个共识节点，参数是新节点公钥的16进制表示，并设置权重，权重只能是正数。
- addObserver添加一个观察节点或将已经存在的共识节点身份改为观察节点。
- remove删除某个节点，如果是最后一个共识节点则不允许删除。
- setWeigh用于设置某一个共识节点的权重。
- 数据存放在_s_consensus_表中。

**接口返回说明：**

- 接口均以错误码形式返回

| 错误码        | 说明                   |
| :------------ | :--------------------- |
| 错误码大等于0 | 修改所影响的行数       |
| -51100        | 输入错误的nodeID       |
| -51101        | 正在删除最后一个sealer |
| -51102        | 输入错误的权重         |
| -51103        | 操作不存在的节点       |

#### SDK支持

- [Java SDK](../sdk/java_sdk/api.html#consensusservice)

### CNSPrecompiled

#### 合约地址

- Solidity：0x1004
- WBC-Liquid: /sys/cns

#### 接口声明

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract CNSPrecompiled
{
    function insert(string memory name, string memory version, address addr, string memory abiStr) public returns(uint256){}
    function selectByName(string memory name) public view returns(string memory){}
    function selectByNameAndVersion(string memory name, string memory version) public view returns(address,string memory){}
    function getContractAddress(string memory name, string memory version) public view returns(address){}
}
```

#### 接口说明

- Insert插入了合约名、合约版本、地址和abi
- selectByName返回该合约所有版本的版本、地址、abi的json
- selectByNameAndVersion根据合约名和版本号返回对应地址、abi的json
- getContractAddress根据合约名和版本号返回合约地址
- version不超128字符，address不超256字符，abi不超16MB

**接口返回说明：**

- 接口均以错误码形式返回

| 错误码        | 说明                     |
|:--------------|:-------------------------|
| 错误码大等于0 | 修改所影响的行数         |
| -50000        | 用户没有权限修改         |
| -51200        | 合约地址与版本号已经存在 |
| -51201        | 合约版本号超过128字符    |
| -51202        | 地址名或版本号不合法    |

#### SDK支持

- [Java SDK](../sdk/java_sdk/api.html#cnsservice)

### ParallelConfigPrecompiled

#### 合约地址

- Solidity：0x1006
- WBC-Liquid: /sys/parallel_config

#### 接口声明

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract ParallelConfigPrecompiled {
    function registerParallelFunctionInternal(address, string memory, uint256)
    public
    returns (int256){}
    function unregisterParallelFunctionInternal(address, string memory)
    public
    returns (int256){}
}
```

#### 接口说明

- registerParallelFunctionInternal注册合约的并行接口信息，参数为合约地址，并行函数签名、互斥参数个数。并行函数的互斥参数必须放在不互斥参数之前
- unregisterParallelFunctionInternal删除某个函数的并行设置

### KVTableFactoryPrecompiled

#### 合约地址

- Solidity：0x1009
- WBC-Liquid: /sys/kv_storage

#### 接口声明

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

    struct KVField {
        string key;
        string value;
    }

    struct Entry {
        KVField[] fields;
    }

contract KVTable {
    function createTable(
        string memory tableName,
        string memory key,
        string memory valueFields
    ) public returns (int256) {}

    function get(string memory tableName, string memory key)
    public
    view
    returns (bool, Entry memory entry)
    {}

    function set(
        string memory tableName,
        string memory key,
        Entry memory entry
    ) public returns (int256) {}
    function desc(string memory tableName) public returns(string memory,string memory){}
}
```

#### 接口说明

- createTable 创建表，参数分别是表名、主键列名、以逗号分隔的其他列名。
  - createTable 表名允许字母、数字、下划线，表名不超48字符
  - keyField不能以下划线开始，允许字母、数字、下划线，总长度不能超过64字符
  - valueField不能以下划线开始，允许字母、数字、下划线，单字段名不超过64字符， valueFields总长度不超过1024
  - valueFields与keyField不能存在重复字段
- set 写数据，参数分别是表名、主键名、Entry结构体。
- get 读数据，参数分别是表名、主键名。
- desc 读取表的key 和 valueFileds的值

### CryptoPrecompiled

#### 合约地址

- Solidity：0x100a
- WBC-Liquid: /sys/crypto_tools

#### 接口声明

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract Crypto
{
    function sm3(bytes memory data) public view returns(bytes32){}
    function keccak256Hash(bytes memory data) public view returns(bytes32){}
    function sm2Verify(bytes memory message, bytes memory sign) public view returns(bool, address){}
    // not support now
    function curve25519VRFVerify(string memory input, string memory vrfPublicKey, string memory vrfProof) public view returns(bool,uint256){}
}
```

#### 接口说明

- `sm3`: 使用国密sm3算法计算指定数据的哈希;
- `keccak256Hash`: 使用keccak256算法计算指定数据的哈希;
- `sm2Verify`: 使用sm2算法验证签名`(publicKey, r, s)`是否有效，验证通过返回`true`以及通过公钥推导出的国密账户，验证失败返回`false`和地址全0的账户;
- `curve25519VRFVerify`: 给定VRF输入和VRF公钥，使用基于ed25519曲线的VRF算法验证VRF证明是否有效，若VRF证明验证成功，返回`true`以及根据证明推导出来的VRF随机数；若VRF证明验证失败，则返回`(false, 0)`。（目前还不支持）

### BFSPrecompiled

#### 合约地址

- Solidity：0x100e
- WBC-Liquid: /sys/bfs

#### 接口声明

以Solidity为例：

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract BFSPrecompiled
{
    function list(string memory path) public view returns(string){}
    function mkdir(string memory path) public returns(int256){}
}
```

#### 接口说明

- `list`：入参一定是**绝对路径**，如果是目录则返回目录下所有文件元信息；如果是合约，则返回合约的元信息。返回字符串是JSON编码后的。
- `mkdir`：入参一定是**绝对路径**，在指定路径下创建目录文件，支持多级创建，创建失败将会返回错误码：

| 错误码      | 说明         |
| :---------- | :----------- |
| 错误码等于0 | 创建成功     |
| -53001      | 文件不存在   |
| -53002      | 文件已存在   |
| -53003      | 创建目录失败 |
| -53005      | 错误的路径名 |