# 7. Omni-directional parallel

Tags: "Execute" "Parallel Scheduling" "DMC" "DAG"

----------

FISCO BCOS has a comprehensive parallel processing design to improve transaction processing performance。According to**parallel granularity**From fine to coarse division, its parallel mechanism can be divided into:

* **Transaction**The parallel: DAG transaction parallel, pipeline parallel

* **合同**Parallelism: DMC, intra-block sharding

* **Block**Parallel: Block DAG (in design)

* **Chain**Parallel: Group Architecture

* **chain service**The parallel: multi-chain cross-chain

----------



```eval_rst
.. toctree::
   :maxdepth: 1
   
   dag.md
   pipeline.md
   DMC.md
   sharding.md
   group.md
   multichain.md
```