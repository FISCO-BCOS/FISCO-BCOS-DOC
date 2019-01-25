# AMDB

分布式存储（Advanced Mass Database，AMDB）通过对表结构的设计，既可以对应到关系型数据库的表，又可以拆分使用KV数据库存储。通过实现对应于不通数据库的存储驱动，AMDB理论上可以支持所有关系型和KV的数据库。
## 名词解释

- Entry，对应于表中的一行，每行以列名作为key，对应的值作为value，构成KV结构。每个Entry拥有自己的AMDB主key，不同Entry允许拥有相同的AMDB主key。
- Entries，Entries中存放主Key相同的Entry，数组。AMDB的主Key与Mysql中的主key不同，AMDB主key用于标示Entry属于哪个key，相同key的Entry会存放在同一个Entries中。
- Table，存储表中的所有数据，KV结构，kV由AMDB主key和Entries对象构成。

## 一个例子
![](../../../images/storage/example.png)

# StorageState

