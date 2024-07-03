# The speed dilemma of blockchain: "expensive" in trust, "slow" to its own

Author: Zhang Kaixiang ｜ Chief Architect, FISCO BCOS

### For example

Counting money, such as hundreds of millions (isn't it exciting ~)

1, if a number of people, slow, but good in focus, go all out, in the visible time can be counted。This is called single-threaded intensive computing。

2. If N people are counted together, each person is divided equally, the number is divided at the same time, and the total number is finally summarized, the time used is basically 1 / N of the first case, the more people involved, the less time required, the higher the TPS。It's called parallel computing and MapReduce。

3, if N people count together, but because these N people do not trust each other, have to stare at each other, first draw lots to choose a person, this person picked up a stack of money (such as 10,000 yuan a stack) to count it again, seal, sign and stamp, and then give several other people together at the same time to count again, count the good people are signed and stamped, this stack of money is considered good。Then draw lots for individuals to check out the next stack of numbers, and so on。Because when a person counts money, others just stare at it, and when a person counts out and seals and signs a pile of money, others have to repeat the count and sign to confirm, then it is conceivable that this method must be the slowest。**This is called blockchain。**

But to put it another way, way 1, a number of people may be counted wrong, the person may be sick or on vacation, resulting in no one working, and the worse result is that the person may exchange counterfeit money or hide some of the money and report a wrong total。

Way 2, N individuals will have a certain percentage of the wrong number, or one of them may be on vacation or sabotage, resulting in the final result can not come out, more likely because of the number of people, some people steal money, change fake money, report fake numbers.....

Way 3, very slow, but very safe, because everyone will stare at the whole process of checking, so certainly not wrong。If one of them drops the line, you can pick up a new wad of money and continue counting without interruption。All the counted money has seals and signatures on it, so it won't be tampered with, and if something goes wrong, you can find the person responsible for it。In this case, the security of funds is fully guaranteed unless all participants are complicit。Under this model, the more people involved, the higher the security of funds。

**Therefore, the blockchain solution is committed to the pursuit of, in the lack of mutual trust in the distributed network environment, to achieve transaction security, fairness, to achieve a high degree of data consistency, tamper-proof, anti-evil, traceability, one of the costs is performance。**

The most famous Bitcoin network, on average, can only process 5 to 7 transactions per second, 10 minutes out of 1 block, to reach the final certainty of the transaction takes 6 blocks, that is, 1 hour, and the block process is quite a loss of computing power (POW mining)。Ethereum, known as the "global computer," can process only two-digit transactions per second, with one block in ten seconds。Ethereum is also currently using the consensus mechanism of loss of computing power POW mining, will gradually migrate to the POS consensus mechanism。The two networks can get stuck in a jam when fans explode for deals, with a day or two or more after a large number of deals are sent out before they are packed for confirmation。

**But in the scenario where the security of funds is life, some things are "necessary," so even if it is slow, you will still consider choosing blockchain。**

### Why is the blockchain slow?

There is a well-known theory in distributed systems called CAP theory: in 2000, Professor Eric Brewer proposed a conjecture: consistency, availability, and partition fault tolerance cannot be satisfied simultaneously in a distributed system, and can only satisfy at most two of them。

**General explanation of CAP**

Consistency(Consistency) : The data is updated consistently, and all data changes are synchronized

Availability(Availability): Good response performance

Partition tolerance(partition fault tolerance): Reliability

Although this theory is controversial, in engineering practice, like the speed of light theory, it can approach the extreme infinitely but is difficult to break through。Blockchain systems can achieve the ultimate in consistency and reliability, but the "good response performance" aspect has been a bit criticized。

We are oriented to the field of "alliance chain," because in the access standards, system architecture, the number of participating nodes, consensus mechanism and other aspects are different from the public chain, its performance is much higher than the public chain, but at present several mainstream blockchain platforms, measured on conventional PC-class server hardware, TPS is generally in the thousand-level, transaction delay is generally in the level of 1 second to 10 seconds。(I heard that TPS hundreds of thousands and millions of millions of blockchains have been made？Okay, look forward to)

I have worked in large Internet companies for many years, in the field of mass services, in the face of the C10K problem (concurrent 10000 connection, million-level concurrency) has a familiar solution, for the general e-commerce business or content browsing services, ordinary PC-level server stand-alone up to tens of thousands of TPS, and the average delay of less than 500 milliseconds, flying general experience is normal, after all, the Internet product card may lead to user loss。For fast-growing Internet projects, through parallel expansion, flexible expansion, three-dimensional expansion of the way, almost no bottom line, no limit to the surface of the mountain tsunami of massive traffic。

**In contrast, blockchain performance is slower than Internet services, and it is difficult to expand, because it is still in its "computing for trust" design ideas。**

### Where exactly is it slow？

From the inside of the system of the "classical" blockchain

**1. For security, tamper-proof and leak-proof traceability**, the introduction of encryption algorithms to process transaction data, increasing CPU computing overhead, including HASH, symmetric encryption, elliptic curve or RSA and other algorithms of asymmetric encryption, data signature and verification, CA certificate verification, and even the current slow to outrageous homomorphic encryption, zero-knowledge proof, etc。In terms of data format, the data structure of the blockchain itself contains a variety of signatures, HASH and other transactions outside the verification data, data packaging and unpacking, transmission, verification and other processing is more cumbersome。

Compared with Internet services, there will also be steps for data encryption and protocol packaging and unpacking, but the more streamlined the better, optimized to the extreme, if not necessary, never increase the burden of cumbersome computing。

**2, in order to ensure the transaction transaction**The transactions are performed serially and completely serially, first sorting the transactions and then executing the smart contract with a single thread to avoid transaction confusion, data conflicts, etc. caused by out-of-order execution。Even if a server has a multi-core CPU, the operating system supports multi-threaded multi-process, and there are multiple nodes and multiple servers in the network, all transactions are methodically and strictly single-threaded on each computer。

Internet services, on the other hand, are how many cores of how many servers can be used, using full asynchronous processing, multi-process, multi-threading, coroutine, caching, optimized IOWAIT, etc。

**3, in order to ensure the overall availability of the network**The blockchain uses a P2P network architecture and a Gossip-like transmission model, where all blocks and transaction data are broadcast indiscriminately to the network, and the receiving nodes continue to relay, a model that allows the data to be communicated as much as possible to everyone in the network, even if they are in different regions or subnets。The cost is high transmission redundancy, which takes up more bandwidth, and the arrival time of propagation is uncertain, which may be fast or slow (many transfers)。

Compared to Internet services, unless there is an error retransmission, the network transmission must be the most streamlined, with limited bandwidth to carry massive amounts of data, and the transmission path will strive for the best, point-to-point transmission。

**4. To support smart contract features**, similar to blockchain solutions such as Ethereum, in order to implement sandbox features, ensure the security of the operating environment and shield inconsistencies, its smart contract engine is either an interpretive EVM or a computing unit encapsulated by docker, and the startup speed and instruction execution speed of the smart contract core engine have not reached the highest level, and the memory resources consumed have not reached the optimal level。

In conventional computer languages such as C++, JAVA, go, and rust languages directly implement massive Internet services, often without restrictions in this regard。

**5. In order to achieve the effect of easy verification and anti-tampering**In addition to the first mentioned, the block data structure carries a lot of data, for the transaction input and output, will use similar merkle tree, Patricia (Patricia) tree and other complex tree structure, through layer-by-layer calculation to obtain data proof, for the follow-up process quick verification。The details of the tree are not expanded here, you can learn its mechanism through the information on the network。

Basically, the process of generating and maintaining this kind of tree is very, very, very cumbersome, which takes up both CPU computation and storage. After using the tree, the overall effective data carrying capacity (that is, the comparison between the transaction data initiated by the client and the final data actually stored) drops sharply to a few percent. In extreme cases, after receiving 10m of transaction data, it may actually require hundreds of megabytes of data maintenance overhead on the blockchain disk。

Internet services rarely use this tree proof structure because they basically do not consider the issue of distributed mutual trust。

**6. In order to achieve the consistency and credibility of the whole network**All blocks and transaction data in the blockchain will be driven by the consensus mechanism framework and broadcast on the network, and all nodes will run multi-step complex calculations and voting, and most nodes will recognize the data before landing。

Adding new nodes to the network will not increase the system capacity and improve the processing speed, which completely subverts the conventional Internet system thinking of "insufficient performance hardware compensation," the root of which is that all nodes in the blockchain are doing repeated checking and generating their own data storage, and do not reuse the data of other nodes, and the node computing power is uneven, and even slow down the final confirmation speed。

Adding nodes to a blockchain system will only increase fault tolerance and the credibility of the network, without enhancing performance, making the possibility of parallel scaling largely missing in the same chain。 

Internet services are mostly stateless, data can be cached and reused, the steps between request and return are relatively simple, easy to expand in parallel, can quickly schedule more resources to participate in the service, with unlimited flexibility。

**7, because the block data structure and consensus mechanism characteristics**, resulting in transactions to the blockchain, will be sorted first, and then added to the block, with the block as a unit, a small batch of a small batch of data for consensus confirmation, rather than receiving a transaction immediately consensus confirmation, for example: each block contains 1000 transactions, every 3 seconds consensus confirmation, this time the transaction may take 1 to 3 seconds to be confirmed。

Worse, transactions are queued all the time without being packed into blocks (due to queue congestion), resulting in longer acknowledgement delays。This transaction delay is generally much larger than the Internet service 500ms response standard。So blockchain is actually not suitable for direct use in the pursuit of fast response to real-time trading scenarios, the industry usually says "improve transaction efficiency" is the final settlement time is included, such as the T+1 Up to one or two days of reconciliation or clearing time, reduced to tens of seconds or minutes, making it a "quasi-real-time" experience。

To sum up, the blockchain system is born with several mountains, including the large internal computing overhead and storage of a single machine, the original sin of serial computing, the complex and redundant network structure, the long delay caused by the rhythm of block packaging consensus, and the difficulty of directly adding hardware to parallel expansion in scalability, resulting in obvious bottlenecks in both scale up and scale out。

**Scale Out (equivalent to scale horizontally)**Scale out, such as adding a new set of independent machines to the original system and increasing the service capacity with more machines

**Scale Up (equivalent to Scale vertically)**: Vertical expansion, upward expansion, such as adding CPU and memory to the original machine, increasing processing power inside the machine

Facing the speed dilemma of blockchain, the developers of FISCO BCOS play the spirit of "Foolish Old Man," and strive to optimize。After a period of hard work, we have moved mountains and rivers, built one high-speed channel after another, so that the blockchain has found a way to the era of extreme speed (see the next article for details), which is what we will analyze in depth in our series of articles。