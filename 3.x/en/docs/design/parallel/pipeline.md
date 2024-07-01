# Pipeline Parallel (Experimental Function)

## 介绍

The latest parallel architecture of FISCO BCOS is pipelined parallel architecture, which is an experimental function. Subsequent versions may have modifications that are not forward compatible.。

advantages of pipeline architecture
- The execution results of the pipeline architecture are consistent with the serial execution results, making it easy to find problems.。
- Pipeline architecture improves performance without special preconditions, and other parallel schemes have preconditions.。
- The theory of pipeline architecture has been thoroughly studied in computer system theory, and there are many tried-and-tested optimization measures that can be directly applied to the blockchain.。

FISCO BCOS supports two pipeline execution architectures: scalar pipeline and superscalar pipeline, suitable for different scenarios。
- Scalar pipeline splits transactions into three steps and executes them in parallel, regardless of the scenario, improving performance by at least 20% compared to normal serial。
- The superscalar pipeline will try to execute more transactions in parallel, and the performance improvement depends on the amount of conflicts between the transaction read and write data. When the number of conflicts is small, the performance can be improved by more than 300% compared to ordinary serial, and when the number of conflicts is large, the performance is lower than ordinary serial.。

## Conditions of use

The pipeline actuator needs to be opened manually. After adding the pipeline architecture, there are currently 5 execution modes:
1. Normal serial (config.genesis, is _ serial _ execute = true): Default mode。
1. Scalar pipeline (config.ini, baseline _ scheduler = true): The transaction execution result is consistent with that of ordinary serial, and the performance is 20% higher than that of ordinary serial, which is suitable for all scenarios.。
1. Superscalar pipeline (config.ini, baseline _ scheduler _ parallel = true): The transaction execution result is the same as that of ordinary serial. The performance improvement depends on the amount of conflicts between transaction read and write data. When the amount of conflicts is small, the performance can be improved by more than 300%. When the amount of conflicts is large, the performance is low. It is suitable for scenarios where the amount of transaction read and write data conflicts is small.。
1. sharding mode (feature _ sharding enabled): Users are required to manually allocate transactions to different partitions. Transactions in multiple partitions can be executed in parallel. However, when a cross-partition call occurs, the transaction execution result will be inconsistent with the normal serial, which is suitable for scenarios where transaction partitions can be manually allocated.。
1. DAG parallel (config.ini, enable _ dag = true, and enable feature _ sharding): Smart contracts need to be statically analyzed in advance, suitable for scenarios with simple contract logic。

Modes 1, 2 and 3 are compatible with each other and can be mixed, 4 and 5 are not compatible with other modes。

Enable conditions for pipeline executor
- Enable all bugfixes
- The node architecture is air
- Use serial (config.genesis, is _ serial _ execute = true) mode without opening feature _ sharding
- use evm virtual machine, not wasm

If these conditions are met, the pipeline executor can be enabled and can be mixed with ordinary serial operation. If the conditions are not met, the node will not start and the error reason will be output in stdout。

## Open method

### Enable Scalar Pipeline

In the node configuration file config.ini, add the executor.baseline _ scheduler option.

```
[executor]
    baseline_scheduler=true
```

### Enable superscalar pipeline

In the node configuration file config.ini, add the executor.baseline _ scheduler _ parallel option.

Before turning on this option, make sure that baseline _ scheduler = true is set, otherwise the option is invalid

```
[executor]
    baseline_scheduler=true
    baseline_scheduler_parallel=true
```

