## 框架

存储模块架构图如下所示：
![架构图](../../../images/storage/architecture.png)

### 世界状态

1. MPTSTate
使用MPT树存储账户的状态，与以太坊一致。

1. StorageState
使用分布式存储的表结构存储账户状态，不存历史信息，去掉了对MPT树的依赖，具有更好的性能表现。

### 分布式存储（Advanced Mass Database，AMDB）

通过抽象表结构，实现了SQL和NOSQL的统一，通过实现对应的存储驱动，可以支持各类数据库，目前已经实现支持LevelDB。
