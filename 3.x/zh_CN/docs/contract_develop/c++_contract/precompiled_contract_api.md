# 预编译合约接口

标签：``预编译合约`` ``接口``

---

FISCO BCOS 3.x 沿用了FISCO BCOS 2.0版本的预编译合约。未来，我们还会尝试将现有的典型业务场景抽象，开发成预编译合约模板，作为底层提供的基础能力，帮助用户更快的更方便的在业务中使用FISCO BCOS。

## 1. SystemConfigPrecompiled

### 合约地址

- Solidity：0x1000
- WBC-Liquid：/sys/status

### 接口声明

以Solidity为例

```solidity
pragma solidity ^0.6.0;

contract SystemConfigPrecompiled
{
    function setValueByKey(string memory key, string memory value) public returns(int32){}
    function getValueByKey(string memory key) public view returns(string memory,int256){}
}
```

### setValueByKey说明

**入参：**

- key表示配置项名称，当前支持的参数有`tx_count_limit`, `tx_gas_lmit`，`consensus_leader_period`,`compatibility_version`。
- value表示对应配置项的值，其中`tx_count_limit`默认值为1000，最小值为1，`consensus_leader_period`默认值为1，最小值为1，tx_gas_limit 默认值为3000000000，最小值为100000。

**返回：**

- setValueByKey将以错误码的形式返回

| 错误码 | 说明               |
|:-------|:-------------------|
| 等于0  | 成功               |
| -51300 | 输入的系统参数有误 |

### getValueByKey说明

**入参：**

- key表示配置项名称，当前支持的参数有`tx_count_limit`, `consensus_leader_period`，`consensus_leader_period`,`compatibility_version`。

**返回：**

- 返回具体的值和生效的区块高度

### SDK支持

- [Java SDK](../sdk/java_sdk/index.md)

## 2. ConsensusPrecompiled

### 合约地址

- Solidity：0x1003
- WBC-Liquid: /sys/consensus

### 接口声明

以Solidity为例

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract ConsensusPrecompiled {
    function addSealer(string memory,uint256) public returns (int32){}
    function addObserver(string memory) public returns (int32){}
    function remove(string memory) public returns (int32){}
    function setWeight(string memory,uint256) public returns (int32){}
}
```

### 接口说明

- addSealer添加一个共识节点，参数是新节点公钥的16进制表示，并设置权重，权重只能是正数。
- addObserver添加一个观察节点或将已经存在的共识节点身份改为观察节点。
- remove删除某个节点，如果是最后一个共识节点则不允许删除。
- setWeigh用于设置某一个共识节点的权重。
- 数据存放在_s_consensus_表中。

**接口返回说明：**

- 接口均以错误码形式返回

| 错误码 | 说明                   |
| :----- | :--------------------- |
| 等于0  | 成功                   |
| -51100 | 输入错误的nodeID       |
| -51101 | 正在删除最后一个sealer |
| -51102 | 输入错误的权重         |
| -51103 | 操作不存在的节点       |

### SDK支持

- [Java SDK](../sdk/java_sdk/api.html#consensusservice)

## 3. TableManagerPrecompiled

### 合约地址

- Solidity：0x1002
- WBC-Liquid: /sys/table_manager

### 接口声明

```solidity
pragma solidity >=0.6.10 <0.8.20;
pragma experimental ABIEncoderV2;

// KeyOrder指定Key的排序规则，字典序和数字序，如果指定为数字序，key只能为数字
// enum KeyOrder {Lexicographic, Numerical}
struct TableInfo {
    string keyColumn;
    string[] valueColumns;
}

// 表管理合约，是静态Precompiled，有固定的合约地址
abstract contract TableManager {
    // 创建表，传入TableInfo
    function createTable(string memory path, TableInfo memory tableInfo) public virtual returns (int32);

    // 创建KV表，传入key和value字段名
    function createKVTable(string memory tableName, string memory keyField, string memory valueField) public virtual returns (int32);

    // 只提供给Solidity合约调用时使用
    function openTable(string memory path) public view virtual returns (address);

    // 变更表字段
    // 只能新增字段，不能删除字段，新增的字段默认值为空，不能与原有字段重复
    function appendColumns(string memory path, string[] memory newColumns) public virtual returns (int32);

    // 获取表信息
    function desc(string memory tableName) public view virtual returns (TableInfo memory);
}
```

### 接口说明

- createTable, createKVTable创建表，参数分别是表名、主键列名、以逗号分隔的其他列名。
  - createTable 表名允许字母、数字、下划线，表名不超50字符
  - keyField不能以下划线开始，允许字母、数字、下划线，总长度不能超过64字符
  - valueField不能以下划线开始，允许字母、数字、下划线，单字段名不超过64字符， valueFields总长度不超过1024
  - valueFields与keyField不能存在重复字段
- appendColumns新增表字段，字段要求与创建表一致
- openTable 获取表真实地址，Solidity合约专用
- desc 读取表的key 和 valueFileds的值
- **接口返回说明：**

  - 接口均以错误码形式返回

  | 错误码 | 说明              |
  | :----- | :---------------- |
  | 等于0  | 成功              |
  | -50007 | Table存在字段重复 |
  | -50006 | Table的字段值超长 |
  | -50005 | Table的键值超长   |
  | -50003 | Table的字段名超长 |
  | -50002 | Table的表名超长   |
  | -50001 | 表已经存在        |

## 4. CryptoPrecompiled

### 合约地址

- Solidity：0x100a
- WBC-Liquid: /sys/crypto_tools

### 接口声明

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;

contract Crypto
{
    function sm3(bytes memory data) public view returns(bytes32){}
    function keccak256Hash(bytes memory data) public view returns(bytes32){}
    function sm2Verify(bytes32 message, bytes memory publicKey, bytes32 r, bytes32 s) public view returns(bool, address){}
    function curve25519VRFVerify(bytes memory message, bytes memory publicKey, bytes memory proof) public view returns(bool, uint256){}
}
```

### 接口说明

- `sm3`: 使用国密sm3算法计算指定数据的哈希;
- `keccak256Hash`: 使用keccak256算法计算指定数据的哈希;
- `sm2Verify`: 使用sm2算法验证签名`(publicKey, r, s)`是否有效，验证通过返回`true`以及通过公钥推导出的国密账户，验证失败返回`false`和地址全0的账户;
- `curve25519VRFVerify`: 给定VRF输入和VRF公钥，使用基于ed25519曲线的VRF算法验证VRF证明是否有效，若VRF证明验证成功，返回`true`以及根据证明推导出来的VRF随机数；若VRF证明验证失败，则返回`(false, 0)`。（目前还不支持）

## 5. BFSPrecompiled

### 合约地址

- Solidity：0x100e
- WBC-Liquid: /sys/bfs

### 接口声明

以Solidity为例：

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.6.0;
pragma experimental ABIEncoderV2;

    struct BfsInfo{
        string file_name;
        string file_type;
        string[] ext;
    }

contract BfsPrecompiled {
    function list(string memory absolutePath) public view returns (int32,BfsInfo[] memory){}
    function mkdir(string memory absolutePath) public returns (int32){}
    function link(string memory name, string memory version, address _address, string memory _abi) public returns (int32){}
    function readlink(string memory absolutePath) public view returns (address) {}
}
```

### 接口说明

- `list`：入参一定是**绝对路径**，如果是目录则返回目录下所有文件元信息；如果是合约，则返回单个BfsInfo。
  - 绝对路径不能带有特殊字符，总长度不能超过56，路径的总级数不能超过6级
  - 如果入参路径下是`link` ，那么 `ext` 字段字符串将会返回软链接对应的合约地址以及ABI，第0个是合约地址，第1个是ABI字符串

- `mkdir`：入参一定是**绝对路径**，在指定路径下创建目录文件，支持多级创建，创建失败将会返回错误码。
  - 绝对路径不能带有特殊字符，总长度不能超过56，路径的总级数不能超过6级

- `link`：替代CNS的功能，创建合约的别名，所创建的软链接均在`/apps/`目录下。
  - 合约名不能带有特殊字符，且将会在 `/apps`下创建合约名同名的目录，如果存在同名的非目录资源则会失败
  - 版本号不能带有特殊字符，且将会在 `/apps/合约名` 目录下创建 `/apps/合约名/版本号` 的链接资源
  - `/apps/合约名/版本号` 作为绝对路径，总长度不能超过56
  - 合约地址一定要真实存在，且状态正常
  - ABI字符串不超过16MB

- `readlink`：获取软链接的真实地址，入参一定是**绝对路径**
  - 绝对路径不能带有特殊字符，总长度不能超过56，路径的总级数不能超过6级


| 错误码 | 错误信息                     | 错误信息/应对措施                                            |
| ------ | ---------------------------- | ------------------------------------------------------------ |
| 0      | Success                      | 成功                                                         |
| -53006 | 错误的文件类型               | 在调用BFS touch接口时才会出现，使用了错误的文件类型          |
| -53005 | 错误的文件路径               | 在调用BFS接口传入绝对路径时会出现这样的错误，BFS的绝对路径总长度不能超过56，路径的总级数不能超过6级，且不能带有特殊字符 |
| -53003 | 创建文件夹失败               | 在调用BFS link接口时创建文件夹出现异常，比如合约名对应的父文件夹已经存在 |
| -51202 | 传入的版本号或者地址是错误的 | 在调用BFS link接口时会出现，版本号不能带有特殊字符，合约地址也必须存在 |
| -53002 | 文件已经存在                 | 在调用BFS 写接口时，创建的文件名已经存在                     |
| -53001 | 文件不存在                   | 在调用BFS读接口时，绝对路径对应的文件不存在                  |
