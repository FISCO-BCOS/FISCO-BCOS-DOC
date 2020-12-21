# The limitation of Precompiled contracts

## TableFactoryPrecompiled

### createTable(string tableName, string keyField, string valueFields)

- tableName：can use letters, numbers, underscores, and no more than 48 characters.
- keyField：can use letters, numbers, underscores, and no more than 64 characters.
- valueFields：letters, numbers and underscores can be used. Fields can be divided easily in English. A single field cannot exceed 64 characters. The total length of valuefields cannot exceed 1024 characters. Underscore cannot be the first character of a field name.

## SystemConfigPrecompiled

### setValueByKey

- tx_count_limit：The default value is 1000, and it is forbidden to set to a negative number
- tx_gas_limit：The default value is 300000000, and it is forbidden to set to a negative number

## ConsensusPrecompiled

### addSealer

- Parameter is nodeID, nodeID's length should be 128 characters

### addObserver

- Parameter is nodeID, nodeID's length should be 128 characters
- If the node is the last consensus node, it is not allowed to modify the last consensus node as an observer

### removeNode

- Parameter is nodeID, nodeID's length should be 128 characters
- If the node is the last consensus node, it is not allowed to delete the last consensus node

## CNSPrecompiled

### insert(string name, string version, string addr, string abi)

- version：No more than 128 characters allowed
- address：Must be a valid address
- abi：No more than 16MB allowed

## PermissionPrecompiled

### insert(string tableName, string addr)

- tableName：No more than 48 characters allowed
- addr：Must be a valid address

