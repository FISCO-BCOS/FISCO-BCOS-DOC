# Remember the process of improving JavaSDK performance from 8000 to 30000

Author ： LI Hui-zhong ｜ Senior Architect, FISCO BCOS

## Origin

FISCO BCOS Reaches 20,000 in China ICT Institute Trusted Blockchain Evaluation+ TPS's transaction processing capabilities, leading in similar products。The test target is the underlying platform, the purpose is to stress test the performance of the underlying platform, the main evaluation target is the underlying platform's transaction processing capacity.。

The transaction construction is done by the client (integrated with the SDK), which can usually be easily extended in parallel.。SDK to complete the process of transaction construction, to achieve the transaction group package, encoding, signing, sending and a series of operations, these processes are stateless, the client can be extended through multi-threaded way, a client performance bottleneck, you can add more clients to expand。

Although the parallel expansion of the "heap machine" can solve the performance problem of the sender, the machine itself is a precious resource, further optimization of algorithm efficiency, improve resource utilization, will be of great benefit.。So, we plan to test the current performance of the JavaSDK first, mainly to evaluate the performance of the generated transaction。Generating transactions includes the process of transaction group packages, parameter codes, transaction codes, transaction signatures, etc., of which transaction signatures are the most important part, with the following test data.

- 8 nuclear machines to test the time-consuming nature of locally generated 50W transactions
- Fully parallel: Generates per second**8498 pens**transactions, taking an average of 0.12 ms per transaction
- Fully serial: 1504 transactions per second, taking an average of 0.66 milliseconds per transaction

Compared to C++Implementation of transaction signature, this performance is not high。Judging from past experience, there is a lot of room for optimization.！So the team began a road of performance optimization of JavaSDK.。

## PROCESS

About performance optimization, the community has done many times to share, you can read the following articles:

- [FISCO BCOS Consensus Optimization Path](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485295&idx=2&sn=46cff7fcdf2e807325532941fcbc98fe&chksm=9f2ef573a8597c65d159c17298ecec02097aafeedfd0192154d9d530c9a0d0a79a22c33894a0&scene=21#wechat_redirect)
- [Synchronization of blockchain and its performance optimization method](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485283&idx=1&sn=c2028923dc7ec7d8bfa808febc57e596&chksm=9f2ef57fa8597c6911f629b324e466f7058e4ae5da06aab8484c1d7db3203496ffebd9562ecb&scene=21#wechat_redirect)
- [FISCO BCOS Trading Pool and Its Optimization Strategy](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485255&idx=1&sn=3947f289f75813c13a2f58fb00d2018e&chksm=9f2ef55ba8597c4de0a3e427f03af7b7b327a54cf440a36d38b62f520591f81463e6aca772fd&scene=21#wechat_redirect)
- [FISCO BCOS Performance Optimization - Tools](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485323&idx=1&sn=d63421fa2353d0e9a1506f01516f3416&chksm=9f2ef597a8597c8134f17053236863c501fb6e4f7480cca1a37926e7cfc309f8faacadf1786a&scene=21#wechat_redirect)
- [FISCO BCOS FAST AND PASSION:](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485329&idx=1&sn=4bb6c31ff10ae1ae03cd0dbeaf023a4c&chksm=9f2ef58da8597c9bcda115382624012c240ce56a5f84450d9808281b8736bd2e9b8833eab5f2&scene=21#wechat_redirect)[Performance Optimization Scheme Most Full Decryption](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485329&idx=1&sn=4bb6c31ff10ae1ae03cd0dbeaf023a4c&chksm=9f2ef58da8597c9bcda115382624012c240ce56a5f84450d9808281b8736bd2e9b8833eab5f2&scene=21#wechat_redirect)
- [Let the barrel have no short board, FISCO BCOS comprehensive promotion of parallel transformation](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485324&idx=1&sn=94cdd4e7944f1058ee01eadbb7b3ec98&chksm=9f2ef590a8597c86af366b6d3d69407d3be0d3d7e50455d2b229c1d69b1fdc6748999601cd05&scene=21#wechat_redirect)

In the process of these performance optimizations, there are two sentences that are most deeply touched: "Premature optimization is the root of all evil" and "Optimization without any evidence is the root of all evil."

Optimization depends on data, and obtaining data requires effective analysis tools, so the first task of this JavaSDK performance optimization is to determine what performance analysis tools to use.。

### Tools: found that java's own analysis tools are quite useful

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5722.PNG)


Using HPROF ran once and got the following data report:

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5723.JPG)


The data shows that the hot spot is at the very bottom of the library function.。This conclusion is not in line with expectations, indicating that the SDK itself code is not a hot spot, performance optimization is more difficult to start.。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5724.JPG)

Fortunately, the good news came soon: using jvisualvm, another tool that comes with java, to better visualize the output of performance analysis data, but also intuitively show that there are hot spots in the JavaSDK itself.。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5725.JPG)


### Analysis: Find a "pseudo hot spot"

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5726.JPG)

Through the analysis of the jvisualvm tool, the biggest hot spot is in the generation of random numbers, which surprised me a bit.。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5727.JPG)

The above analysis is the national secret version, for non-national secret how?？After running it again, the hot spot is still visible in createTransaction, so try uuid。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5728.JPG)

However, the face, come is that called a fast, UUID implementation is also based on SecureRandom。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5729.JPG)

Group chats have been silent for a long time... then study SecureRandom to see why it is so slow, and learn some background knowledge about Java random numbers。Finally, the following knowledge points let us get excited again。（[https://zhuanlan.zhihu.com/p/72697237](https://zhuanlan.zhihu.com/p/72697237)）

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5730.JPG)


My phrase "replace SecureRandom with ThreadLocalRandom to calculate Nonce" hasn't been typed yet, and a new round of face-bashing has begun.。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5731.PNG)

The discussion in this direction has opened another door for thinking about this performance optimization journey ~

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5732.JPG)


The discussion makes sense, and then a closer look at the data confirms the idea at the data level.。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5733.JPG)


Experimental tests prove that this is the right direction, the number of threads down, the hot spot disappeared。There are hot spots in random numbers because the number of concurrent threads in stress testing is too large, and too many threads preempt resources, resulting in slow random number acquisition.。

### Re-analysis: Finding shocking hot spots

After the first round of analysis, a "pseudo hot spot" was found, but the performance improvement was still ineffective.。The revolution has not yet succeeded, comrades still need to work hard！Reduce the number of threads and run the performance analysis again. The performance data is as follows:

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5734.JPG)

At first glance, the distribution of hot spots is a very low-level basic library operation, and anxiety pervades my heart... With the silence of the early morning, the group chat fell into silent silence again... Until, I raised another question。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5735.JPG)

Octopus (Wang Zhang) seconds back, the team's passion awakened ~ for him this reply speed, but also for this reply results！

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5736.JPG)

The implementation of the signature algorithm is actually to sign first and then do the verification, and the verification is to get a value called recoveryID (the recovery principle of ECDSA will be expanded in detail in another article)。Here's why we're excited。The purpose of the recoveryID setting is to allow future users to quickly recover the public key from the signature. Without this recoveryID, four possibilities are required to recover the public key.。This approach does not reduce the actual cost at all, but only uses the "dry kun move" to transfer the cost to the signature link.。In fact, recoveryIDs are available in a faster way, as seen in the next section。[This part of the code is inherited from web3j, before no in-depth study of its implementation, the current web3j is still this implementation]。Out of the instinct of the old code farmer, he wanted to find out the boundary of the problem in the first place when he found the symptoms, so there was such an analysis and attempt.

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5737.JPG)

The same old driver's brother octopus quickly gives a gratifying conclusion:

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5738.JPG)

Here, the bottom of the heart！At least killing recoverFromSignature can double the performance, as for how to kill, uh...。Out of curiosity, I want to see what the performance data will be like at this time (thanks to curiosity)。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5739.JPG)

Re-adjust the pose (replace it with a version that uses ThreadLocalRandom to generate nonce and reduces the number of concurrent threads to 10), and get another round to get the surprise data again.。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5740.JPG)


In this case, the performance distribution is more uniform, and there are no obvious hot spots.。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5741.JPG)

At one o'clock in the morning, I finally got a satisfactory result, and the mood was great... After a second, the brain circuit popped up with a question, "What will happen to the national secret?"？Let's talk about it tomorrow (the optimization of national secrets will be discussed in more detail in another article)。

![](../../../../images/articles/javasdk_performance_improvement_8000-30000/IMG_5742.JPG)


### Pit filling: calculation method of recoveryID

Due to the Java Cryptographic Algorithm Library (bc-Java) is not familiar enough, here also encountered a lot of pits, and finally through the inheritance of cryptographic algorithm library, more required parameters will be exposed back to the upper layer, finally realized the Java version of the recoveryID calculation.。

```
// Now we have to work backwards to figure out the recId needed to recover the signature.
        ECPoint ecPoint = sig.p;
        BigInteger affineXCoordValue = ecPoint.normalize().getAffineXCoord().toBigInteger();
        BigInteger affineYCoordValue = ecPoint.normalize().getAffineYCoord().toBigInteger();

        int recId = affineYCoordValue.and(BigInteger.ONE).intValue();
        recId |= (affineXCoordValue.compareTo(sig.r) != 0 ? 2 : 0);
        if (sig.s.compareTo(halfCurveN) > 0) {
            sig.s = Sign.CURVE.getN().subtract(sig.s);
            recId = recId ^ 1;
        }
```

The recovery mechanism and recoveryID generation principle of the ECDSA algorithm will be expanded in detail in the subsequent push.。

## Afterword

Every time to do performance optimization, is a very cool experience, not to stay up late, but never lack of passion。The code is stripped of its thread, going through the process of repeatedly discovering bottlenecks, mercilessly beating the face, regaining confidence, and finally reaching the willow.。 End with those two sentences again: premature optimization is the root of all evil, and optimization without any data support is the root of all evil.。Mutual encouragement！