# 压力测试指南

标签：``压力测试`` ``Java SDK Demo``

----

## 通过Java SDK进行压力测试

Java SDK Demo是基于[Java SDK](./sdk/java_sdk/index.md)的基准测试集合，能够对FISCO BCOS节点进行压力测试。Java SDK Demo提供有合约编译功能，能够将Solidity合约文件转换成Java合约文件，此外还提供了针对转账合约、CRUD合约以及AMOP功能的压力测试示例程序。

### 第一步. 安装JDK

Java SDK Demo中的测试程序能够在部署有JDK 1.8 ~ JDK 14的环境中运行，执行测试程序前请先确保已安装所需版本的JDK。以在Ubuntu系统中安装OpenJDK 11为例：

  ```shell
# 安装open JDK 11
$ sudo apt install openjdk-11-jdk
# 验证Java版本
$ java --version
# 输出以下内容：
# openjdk 11.0.10 2021-01-19
# OpenJDK Runtime Environment (build 11.0.10+9-Ubuntu-0ubuntu1.20.04)
# OpenJDK 64-Bit Server VM (build 11.0.10+9-Ubuntu-0ubuntu1.20.04, mixed mode, sharing)
  ```

### 第二步. 编译源码

  ```shell
# 下载源码
$ git clone https://github.com/FISCO-BCOS/java-sdk-demo
$ cd java-sdk-demo
# 编译源码
$ bash gradlew build 
  ```

  ```eval_rst
  .. note::

      当网络无法访问GitHub时，请从https://gitee.com/FISCO-BCOS/java-sdk-demo处下载源码。
  ```

### 第三步. 配置Demo

使用Java SDK Demo之前，需要首先配置Java SDK，包括证书拷贝以及端口配置，详细请参考[这里](./sdk/java_sdk/quick_start.md)

  ```shell
  # 拷贝证书(假设SDK证书位于~/fisco/nodes/127.0.0.1/sdk目录，请根据实际情况更改路径)
  $ cp -r ~/fisco/nodes/127.0.0.1/sdk/* conf

  # 拷贝配置文件
  # 注:
  #   默认搭建的FISCO BCOS区块链系统Channel端口是20200，若修改了该端口，请同步修改config.toml中的[network.peers]配置选项
  $ cp conf/config-example.toml conf/config.toml
  ```

### 第四步. 执行示例压力测试程序

Java SDK Demo提供了一系列压测程序，包括串行转账合约压测、并行转账合约压测等，具体使用方法如下：

**注意：下面的压力测试程序均为EVM的节点执行环境，节点配置详情请参考：[节点配置](../tutorial/air/config.md)**

  ```shell
# 进入dist目录
$ cd dist

# 多合约-合约内并行转账合约:
# groupId: 压测的群组ID
# userCount: 创建账户的个数，建议（4～32个）
# count: 压测的交易总量
# qps: 压测QPS
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceDMC [groupId] [userCount] [count] [qps]

# 多合约-跨合约并行转账
# groupId: 压测的群组ID
# userCount: 创建账户的个数，建议（4～32个）
# count: 压测的交易总量
# qps: 压测QPS
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceTransferDMC [groupId] [userCount] [count] [qps]

# 压测串行转账合约:
# count: 压测的交易总量
# tps: 压测QPS
# groupId: 压测的群组ID
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceOk [count] [tps] [groupId]

# 压测并行转账合约
# --------------------------
# 基于Solidity并行合约parallelok添加账户:
# groupID: 压测的群组ID
# count: 压测的交易总量
# tps: 压测QPS
# file: 保存生成账户的文件名
$ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [parallelok] [groupID] [add] [count] [tps] [file]
# 基于Precompiled并行合约precompiled添加账户
# (参数含义同上)
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [add] [count] [tps] [file]
# --------------------------
# 基于Solidity并行合约parallelok发起转账交易压测
# groupID: 压测的群组ID
# count: 压测的交易总量
# tps: 压测的QPS
# file: 转账用户文件
$ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [parallelok] [groupID] [transfer] [count] [tps] [file]
# 基于Precompiled并行合约Precompiled发起转账压测
$ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelOkPerf [precompiled] [groupID] [transfer] [count] [tps] [file]


# KVTable合约压测
# 压测KV set
# count: 压测的交易总量
# tps: 压测QPS
# groupId: 压测群组
$ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceKVTable [set] [count] [tps] [groupId]
# 压测KV get
# (参数解释同上)
$ java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.PerformanceKVTable [get] [count] [tps] [groupId]
  ```

**以下是WASM环境的压力测试**

```shell
# 压测并行转账合约
# --------------------------
# 基于Liquid并行合约parallelok添加账户:
# groupID: 压测的群组ID
# count: 压测的交易总量
# tps: 压测QPS
# file: 保存生成账户的文件名
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelLiquidPerf [groupId] [add] [count] [tps] [file]

# 基于Liquid并行合约parallelok发起转账交易压测
# groupID: 压测的群组ID
# count: 压测的交易总量
# tps: 压测的QPS
# file: 转账用户文件
java -cp 'conf/:lib/*:apps/*' org.fisco.bcos.sdk.demo.perf.ParallelLiquidPerf [groupId] [transfer] [count] [tps] [file]
```

### 压力测试示例



```shell
```

