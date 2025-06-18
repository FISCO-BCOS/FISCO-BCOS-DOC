# 预编译合约相关限制

## TableFactoryPrecompiled

### createTable(string tableName, string keyField, string valueFields)

- tableName：表名可以使用字母、数字、下划线，不可以超过48字符。
- keyField：可以使用字母、数字、下划线，不可以超过64字符
- valueFields：可以使用字母、数字、下划线，字段之间使用英文逗号分割，单个字段不能超过64字符，valueFields总长度不能超过1024字符。字段名不允许以下划线开始。

## SystemConfigPrecompiled

### setValueByKey

- tx_count_limit：默认值为1000，禁止设置为负数
- tx_gas_limit：默认3亿，禁止设为负数

## ConsensusPrecompiled

### addSealer

- 参数是nodeID，nodeID的长度应该为128字符

### addObserver

- 参数是nodeID，nodeID的长度应该为128字符
- 如果节点是最后一个共识节点，那么不允许修改最后一个共识节点为观察节点

### removeNode

- 参数是nodeID，nodeID的长度应该为128字符
- 如果节点是最后一个共识节点，那么不允许删除最后一个共识节点

## CNSPrecompiled

### insert(string name, string version, string addr, string abi)

- version：不允许超过128字符
- address：必须是有效的地址
- abi：不超过16MB

## PermissionPrecompiled

### insert(string tableName, string addr)

- tableName：表名不能超过48字符
- addr：必须是有效的地址

