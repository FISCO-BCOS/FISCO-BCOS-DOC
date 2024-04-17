# 7. 全方位并行

标签：``执行`` ``并行调度`` ``DMC`` ``DAG``  

----------

FISCO BCOS为提升交易处理性能，进行了全方位的并行处理设计。按照**并行粒度**从细到粗的划分，其并行机制可分为：

* **交易**的并行：DAG交易并行、读写集并行

* **合约**的并行：DMC、块内分片

* **区块**的并行：区块DAG（设计中）

* **链**的并行：群组架构

* **链服务**的并行：多链跨链

----------



```eval_rst
.. toctree::
   :maxdepth: 1
   
   dag.md
   rwset.md
   DMC.md
   sharding.md
   group.md
   multichain.md
```