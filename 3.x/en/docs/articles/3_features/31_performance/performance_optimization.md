# FISCO BCOS's Fast and Furious: Performance Optimized Schemes for the Most Full Decryption


Author ： SHI Xiang ｜ FISCO BCOS Core Developer


The last article said that the speed dilemma of the blockchain is "expensive" in trust, "slow" in the final analysis, the root cause is still in its "computing for trust" design ideas.。The industry generally praised the blockchain as a machine of trust, in order to achieve trust, the blockchain has to do a lot of complex and cumbersome operations, synchronization, verification, execution, consensus, etc., are essential links in the blockchain.。

This is like the "traffic regulations" when driving, always telling us developers, for safety, please drive at the specified speed！However, the community still has a common voice: really too slow！

So, can we upgrade this trusted machine to make it safe and fast?？Through the team's in-depth exploration and practice, we have opened up a number of ways to the era of extreme speed.。Looking back at the whole process, it's like building a car with outstanding performance。

- High power engine**DAG-based parallel execution engine for transactions**
- Fuel delivery unit**distributed storage**
- Front and rear seats**Process optimization for consensus and synchronization**
- Transmission**omni-directional parallel processing**
- Hydrogen fuel**Precompiled Contracts**
- Monitoring instrument**Comprehensive performance analysis tools**
- Exclusive steering wheel**parallelizable contract development framework**

### High-power engine: DAG-based transaction parallel execution engine - extreme effort to make transactions execute in parallel

Traditional transaction execution engines, which execute transactions in a serial manner, can only be executed one by one.。No matter how many transactions there are in a block, they need to be executed one by one.。This is like a low-power engine. Even if it is equipped with a giant fuel tank, it still cannot output powerful power.。One cylinder is not enough, change to 4 cylinders, 8 cylinders, ok？

FISCO BCOS implements a transaction parallel execution engine (PTE) that allows multiple transactions within a block to be executed simultaneously。If the machine has 4 cores, it can support the simultaneous execution of 4 transactions, and if it has 8 cores, it can support the simultaneous execution of 8 transactions.。Of course, under the control of "traffic regulations," the correctness of parallel execution needs to be guaranteed, that is, the results of parallel execution and serial execution need to be consistent.。In order to ensure the consistency of parallel execution, the transaction parallel execution engine (PTE) of FISCO BCOS introduces the data structure DAG (directed acyclic graph).。

Before executing transactions in a block, the execution engine automatically builds dependencies between transactions based on their mutual exclusions.。This dependency is a DAG that allows parallelizable transactions to be executed in parallel when the engine executes.。In this way, the consistency of transaction execution is guaranteed and the throughput of transaction execution is increased by orders of magnitude.。

### Fuel delivery device: distributed storage - enough fuel for the engine

The traditional blockchain storage model is a towering MPT tree。All the data on the blockchain is gathered on this tree。Every write or read of data is a long journey from branch to root (or from root to branch)。As more and more data is on the chain and the tree gets taller, the distance from the branch to the root becomes longer and longer。What is more troublesome is that although there are many branches, there is only one root.。The writing or reading of massive amounts of data on the chain is as tragic as a thousand troops grabbing a single-plank bridge, and the extent of the tragedy can be imagined.。So the traditional blockchain, choose one by one, one data to read, one transaction to execute。Figuratively speaking, it's a pipeline that delivers fuel to the engine。

This will definitely not work！We need multiple pipelines to deliver fuel to the engine.！This time, FISCO BCOS is not crudely connecting multiple pipelines (MPT trees) to the engine because it is too slow to transport oil with pipelines (storing data with MPT)。We'll just ditch the oil pipeline and just soak the engine in the tank.！This analogy may not be appropriate, but understanding the execution engine and storage design of FISCO BCOS, I believe you will have the same feelings as me.。

We abandon the MPT tree and organize the data in a "table" way.。Execute the engine to read and write data, no longer need to MPT tree root to branch traversal, directly read and write on the "table"。In this way, the reading and writing of each piece of data does not depend on a global operation and can be done separately and independently.。This provides the basis for concurrent data reading and writing for the transaction parallel execution engine (PTE). Similar to an engine soaked in a fuel tank, gasoline flows directly into the cylinder, and no one shares whose fuel pipe。

For detailed analysis of distributed storage, please click: [Distributed Storage Architecture Design](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485336&idx=1&sn=ea3a7119634c1c27daa4ec2b9a9f278b&chksm=9f2ef584a8597c9288f8c5000c7def47c3c5b9dc64f25221985cd9e3743b9364a93933e51833&token=942411972&lang=zh_CN#rd)

### Front and back seats: consensus and synchronized process optimization - no egalitarianism, rich first drives rich later

In blockchain nodes, the synchronization module and the consensus module are inseparable twins, sometimes helping each other and sometimes fighting for resources.。In previous designs, there was no prioritization between the synchronization module and the consensus module。It's like riding in a car. There are no rules about who sits in the front row and who sits in the back row. As a result, the twins often waste a lot of time fighting for the order.。

Everything from reality, first rich drive after rich！

The consensus module is responsible for dominating the rhythm of the entire blockchain, and the consensus module should be allowed to go first.。The synchronization module, on the other hand, should play a good role in coordination, assisting the consensus module to come out faster.。Based on this idea, FISCO BCOS optimizes the process of consensus and synchronization:

- First, the transaction verification operation in the synchronization module is stripped from the P2P callback thread, so that the consensus module can receive consensus messages more smoothly for faster consensus.。
- Second, de-duplicate the transaction validation and cache the binary of the transaction。A transaction is verified and decoded only once, freeing up more CPU resources for the execution of blocks in the consensus module.。
- Third, optimize the synchronization process, before the transaction synchronization, as far as possible to make the synchronization module run in front of the consensus module, so that the synchronization module priority to write transactions into the transaction pool, priority decoding and verification, so that the consensus module to get the transaction, eliminating the process of decoding and verification, faster into the block packaging stage.。

In a word, in a word。The purpose of everything is to serve the consensus process, making it faster and smoother to package, execute, agree, and block。

### Transmission device: all-round parallel processing - let the power output efficiently

If you do not match the appropriate transmission device, no matter how high the power of the engine will not be able to effectively output power。Signature verification, encoding and decoding, and data placement are the more time-consuming parts of the blockchain, in addition to opening transactions.。In previous designs, signature verification, codec, and data drop were all performed serially.。Even if transactions are executed in parallel, the performance of this trust machine is subject to the performance of these three links.。

The performance problems of these three links are endless, and the performance will never rise.！Then equip the high-power engine with a high-performance transmission device to release its power。

FISCO BCOS introduces parallel containers, so that data reading and writing naturally supports concurrent access.。On this basis, for the verification and signing of transactions, the verification and signing of transactions are directly executed in parallel, and the verification and signing process between transactions does not affect each other.；For encoding and decoding, the encoding format of RLP has been modified so that the original RLP format, which can only be read and written sequentially, supports parallel encoding and decoding.；For block drop, the state change is encoded in parallel.。

Not only that, FISCO BCOS performs parallel processing where it can be parallelized, allowing the system CPU resources to be maximized.。Transactions are not only executed in parallel when they enter the contract engine, but are also processed in parallel in processes such as signature verification, coding and decoding, and data placement.。Powerful engine, coupled with high-performance transmission, the effect is remarkable！

### Hydrogen Fuels: Precompiled Contracts - An Efficient Lightweight Contract Framework

As we all know, the blockchain runs smart contracts, which are written in the language of solidity.。Solidity contract is deployed to the chain, gas is burned, and the result is obtained。But have you ever thought about switching to a fuel that costs less but makes the car run faster?？

And look at FISCO BCOS self-developed "hydrogen fuel" - pre-compiled contract.！

FISCO BCOS provides organizations with a high-performance, customized, lightweight contract framework。Organizations can build their own pre-compiled contracts into FISCO BCOS nodes according to their business needs.。Precompiled contracts with C.++Written with higher performance than the solidity engine, faster startup, leaner instructions, and less memory usage。Just like "hydrogen fuel," lower cost, higher calorific value, let the car run faster！Of course, extracting "hydrogen fuel" requires a little effort, and the implementation of pre-compiled contracts is relatively complex and has a high threshold.。To learn about precompiled contracts, click: [Precompiled Contract Architecture Design](http://mp.weixin.qq.com/s?__biz=MzU5NTg0MjA4MA==&mid=2247484055&idx=1&sn=2f33d5231784147ed61cb6da85e6d74d&chksm=fe6a87d8c91d0ece832d34c0345d1795c4b88daf9b4af815e94987d4f7abd899a464d0075e09&scene=21#wechat_redirect)

### Monitoring instrument: multi-dimensional performance analysis tool - gives people a sense of stability in the overall situation

FISCO BCOS uses a large number of performance analysis tools in the development process, just like many index clear monitoring instruments are installed on the car。We have adopted mainstream performance analysis tools, such as perf and systemtap, to analyze the hotspots, locks, memory, etc. of the program. We have also developed customized performance analysis tools based on the characteristics of the blockchain program process to better evaluate data in consensus, block verification, storage modules and processes。The tool can analyze the time ratio and time change of each stage in the program.。With reliable quantification tools, developers can be aware of every optimization they do.。

Exclusive steering wheel: parallel contract development framework - to give developers a smooth operating experience

Everything is ready, get in the car！Sitting in the driving position, you will control the exclusive steering wheel provided by FISCO BCOS-the parallel contract development framework！How to operate this machine reasonably depends on this steering wheel。"Hand in hand" parallel contract development framework, in the development of parallel contracts, contract developers do not need to care about the specific underlying logic, but will focus more on their own contract logic.。When the contract is successfully deployed, the parallel contract is automatically recognized by the underlying code and automatically executed in parallel.！

Now, finally got into the car。not enjoyable？It doesn't matter, the next few articles, please pick up, is the real hard core dry goods！We will introduce the parallel transaction executor (PTE) based on the DAG model in FISCO BCOS in the next article.