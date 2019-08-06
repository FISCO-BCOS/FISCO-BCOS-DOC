# Introduction

FISCO BCOS platform is an open-source platform compatible with the financial industry collaboratively. The FISCO open source working group builds it. Members in the working group include WeBank, Shenzhen Securities Communication, Tencent, Huawei, Digital China, Forms Syntron, Beyondsoft, Yuexiu Financial Holdings (FinTech), YIBI Technology.

## Sublimation of alliance chain: Collaborative Business and Open Consortium Chain

Business is competitive and free economic activity. It is naturally easy to result in survival of the fittest, monopoly, and even rent-seeking. Especially after the global financial crisis in 2008, the appeared malady of “Too Big to Fail” led to a series of technological and business revolution, and started an era of changing from centralized to distributed.

In this context, blockchain was sprouted in 2008 and gradually became matured. Through the consensus mechanism, distributed ledger, encryption algorithm, smart contract, peer-to-peer communication, distributed computing architecture, distributed storage, privacy protection algorithm, cross-chain protocol and other technology modules, blockchain  can help the participating parties in business model to achieve the cooperation of reciprocity and mutual trust so that to promote the progress from “information Internet” to “trusted Internet”, and also to make business model moving toward “Collaborative” possible.

“Collaborative Business” model defined by WeBank is a new type of production relationship established by several peer-to-peer commercial communities. It is a new economic activity to implement organization management, functional division, value exchange, joint provision of products and services, and benefit share through pre-set transparent rules. The main features of Collaborative Business are multi-participation, resources share, intelligent collaboration, value integration, mode transparentizing, cross boundaries, etc. A mature scenario of Collaborative Business has multiple requirements. Various parties hold production materials,  build the product, and service capabilities. The relationship in business is mutual, and the rules of product and profit-sharing are transparent.

There’re so many differences between Collaborative Business and the business models of chain and franchise store and shared business, which are prevailing previously. The most significant one is not the person, the product, or the information platform but the objective technology to be the bridge to connect each other. It is true that if not to encourage open source technology, it may evolve into another kind of monopoly. Therefore, for developing Collaborative Business, we must always maintain the attitude of open source technology. All the participants cooperating through open source community can eliminate the monopoly. The collaborative business helps small and micro-enterprises to achieve business value, thereby stimulating economic growth, employment and innovation, and realizing anti-monopoly.

To develop open-source blockchain is significant, but the choice of technology is crucial. The first blockchain originates from crypto-currency and permissionless blockchain projects. However, the permissionless blockchain projects always make for financing, their users target on the profit from trading. So everyone pays more attention to the coin price but not the blockchain application ability.

Since the tokens of the public chain are necessary class currency and class security, they have been severely suspended by regulatory. After they are washed out, alliance chain technology has shouldered the responsibility of pushing the blockchain technology to move forward.

In 2018, “Open Consortium Chain” was announced in the industry.  They called on the consortium chain to actively open up open source, move from the relatively closed alliance or the company to the public. The general public can feel the benefits brought by the blockchain and realize the vision of collaborative business in that way.  The benefits include increased efficiency, cost reduction, trust enhancement, data interchange, and traceability of responsibility.

A new generation of Open Consortium Chain proposes some unique requirements on blockchain underlying technology. Except for the standard blockchain characteristics, there is still a need to strengthen several aspects:

First, Open Consortium Chain is not a single chain, and it requires the technology to support multi-chain parallel computing and cross-chain communication.  It needs the ability to support the massive transaction request from the Internet.

Second, it needs the ability to form alliances and build chains quickly and at low cost. So that each demand party can efficiently establish an alliance chain network to make the cooperation of chain building among enterprises becomes simple as building a “chat group”.

Finally, it needs to be opened to achieve full trust among alliance members.

Open Consortium Chain is not only conducive to reduce the cost on trial and error for enterprise, but also promoting the development of the industrial society in the direction of credibility and transparency. Moreover, it comprehensively reduces the risks of the operation, moral hazard, credit, information protection.

Adhere to our above goals and vision, we officially release the FISCO BCOS 2.0 version based on the “Open Consortium Chain” technology route.

## FISCO BCOS 2.0
FISCO BCOS 2.0 version has been upgraded and optimized based on the 1.0, and has made significant breakthroughs in scalability, performance, and ease for use, including:

- Implement **group architecture**. In a global network of multiple nodes, there may be much sub-networks which are made up of numerous subsets of nodes to maintain a separate ledger. The consensus and storage among these ledgers are independent of each other and have good scalability and security. In group architecture, parallel expansion can be better achieved to meet the needs of high-frequency transaction scenario for the financial industry. Meanwhile, group architecture can quickly support the requirements of chain building. It dramatically reduces the difficulty of operation and maintenance, and truly enables the chain building among enterprises is as easy as building a “chat group”.

- Support **Distributed Storage** to enable storage to break through the limitation of a single machine and support lateral spreading. Separating between calculation and storage can improve the system’s robustness to prevent data from being affected even if the node server fails. Distributed storage defines a standard interface named CRUD  which can adapt to both SQL and NoSQL database system.  CRUD support multiple business scenarios.

- Implement **a pre-compiled contract framework** to break the EVM performance bottleneck. It supports concurrent transaction processing to increase the transaction processing throughput greatly. Precompiled contract is implemented in C++ and built into the underlying system. The framework can automatically recognize a transaction’s conflict information, construct a DAG dependency, and plan an efficient parallel transaction execution path. In the best case, the performance can increase by N times (N = CPU cores).

- Besides, FISCO BCOS 2.0 version continues to optimize network transmission models, computing storage processes, and so on for significantly improving performance. Also, we continue to upgrade the core modules, such as storage, network, and computing model,  ensure the system’s robustness, availability, and performance.

More 2.0 version features will be introduced in depth in the following sections. Please see [2.0 New Release] (./ what_is_new.md).

## FISCO BCOS 1.0
Looking back at the evolution of FISCO BCOS, we have been working on the trade-off of performance, security, availability, and compliance.

- In terms of performance, FISCO BCOS has made many optimizations on overall architecture and transaction processing.  1.0 version uses efficient consensus algorithms, parallelizing computing. Furthermore, the core breakthrough of its performance is not only in single-chain, but also in optimizing architecture design based on single-chain performance and achieving the flexible, efficient, reliable, and secure parallel computing and parallel scalability capability. It can help developers to meet the performance they need for their business scenarios by simply adding machines. In general, the FISCO BCOS platform optimizes the network communication model, adopts the Byzantine Fault Tolerance(BFT) consensus mechanism, and combines with the multi-chain architecture and cross-chain interaction scheme to solve the performance problems of concurrent access and hotspot accounts so that to meet the need of high-frequency transaction scenario for the financial industry.

- In terms of security, the FISCO BCOS platform achieves comprehensive protection in the application, storage, network, and host layers through node admission control, reliable key management, and flexible access control. In the design of privacy protection, FISCO BCOS platform supports permission management, physical isolation, and national cryptography algorithm (standard algorithm certified by national cryptographic bureau). Meanwhile, it implements multiple privacy protection algorithms including homomorphic encryption, zero-knowledge proof, group signature, ring signature as open projects to the public.

- In terms of usability, FISCO BCOS is designed to run at 7 * 24 hours to achieve a high availability needed for the commercial grade. In terms of regulatory, it supports regulatory, and auditing agencies can join as observation nodes to obtain real-time data for regulating and auditing. Also, it provides multiple development interfaces to make it easier for developers to compile and call smart contract.

## Summary

In practice, FISCO BCOS has grown into a stable, efficient, and secure blockchain underlying platform through testing in many external agencies, multiple applications, and longstanding running in production environments.

The following content of this document details the tutorial of construction, installation, smart contract deployment and calling of FISCO BCOS 2.0 version, as well as an in-depth introduction to the overall architecture of FISCO BCOS 2.0 and the design of each module.