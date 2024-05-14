# 读写集并行

## 介绍

FISCO BCOS的最新并行架构为流水线执行架构。

流水线架构的优势：
- 流水线架构的执行结果与串行执行结果一致，便于发现问题。
- 流水线架构提升性能的同时无需特殊前置条件，其他并行方案有前置条件：DAG需要冲突字段信息、DMC执行结果与串行不一致。
- 流水线架构的理论在计算机系统理论研究已经深入透彻，有诸多久经考验的优化措施，可直接应用到区块链。

FISCO BCOS支持两种流水线执行架构：标量流水线和超标量流水线，适合不同场景。
- 标量流水线将交易拆分为三个步骤并行执行，无论何种场景，相比普通串行至少能提升20%的性能。
- 超标量流水线会试图并行执行更多交易，性能提升幅度视交易读写数据的冲突量而定，冲突数量少时，相比普通串行能提升300%以上的性能，冲突数量多时，相比普通串行的性能更低。

## 使用条件

流水线执行器需要手工打开，加入流水线架构后，目前有5种执行模式：
- 标量流水线（config.ini，baseline_scheduler=true）
- 超标量流水线（config.ini，baseline_scheduler_parallel=true）
- 普通串行（config.genesis，is_serial=true）
- sharding模式（启用feature_sharding）
- 纯DMC模式（config.genesis，is_serial=false）

其中模式1、2、3互相兼容，可以混跑，4、5与其它模式均不兼容。

流水线执行器的启用条件
- 启用所有bugfix
- 节点架构为air
- 使用串行（config.genesis，is_serial=true）模式，不打开feature_sharding
- 使用evm虚拟机，非wasm

满足这些条件，可启用流水线执行器，且可以与普通串行混合运行，如果不满足条件，节点将无法启动，并在stdout输出错误原因。

## 开启方法

### 启用标量流水线

节点配置文件config.ini，增加选项executor.baseline_scheduler

```
[executor]
    baseline_scheduler=true
```

### 启用超标量流水线

节点配置文件config.ini，增加选项executor.baseline_scheduler_parallel

开启此选项之前，请先确认设置了baseline_scheduler=true，否则选项无效

```
[executor]
    baseline_scheduler=true
    baseline_scheduler_parallel=true
```

