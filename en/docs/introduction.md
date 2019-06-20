# FISCO BCOS introduction

FISCO BCOS platform is a open source platform to be applicable in financial industry collaboratively built by the FISCO open source working group. Members in the working group include: WeBank, Shenzhen Securities Communication, Tencent, Huawei, Digital China, Forms Syntron, Beyondsoft, Yuexiu Financial Holdings (FinTech), YIBI Technology.

## Sublimation of alliance chain: Collaborative Business and Open Consortium Chain

Business is a competitive and free economic activity. It is naturally easy to result in survival of the fittest, monopoly, and even rent seeking. Especially after the global financial crisis in 2008, the appeared malady of “Too Big to Fail” led to a series of technological  and business revolution, and started an era of “centralized” to “distributed”.

In this context, blockchain technology was sprouted in 2008 and gradually became matured. Through the consensus mechanism, distributed ledger, encryption algorithm, smart contract, peer-to-peer communication, distributed computing architecture, distributed storage, privacy protection algorithm, cross-chain protocol and other technical modules in blockchain solution, blockchain technology can help the participating parties in business model to achieve the cooperation of reciprocity and mutual trust so that to promote the progress from "information Internet" to "trusted Internet", and also to make business model moving toward "Collaborative" possible.

"Collaborative Business" model defined by WeBank, is a new type of production relationship established by a number of peer-to-peer commercial interests communities and a new economic activity to implement organization and administration, functional division, value exchange, joint provision of products and services, and benefit share through pre-set transparent rules. The main features of Collaborative Business are multi-participation, resources share, intelligent collaboration, value integration, mode transparentizing, cross boundaries, and so on. A mature scenario of Collaborative Business has requirements for production materials to be held by multiple parties, product and service capabilities to be built by multiple parties, mutual relationship in business, and rules of proudct and profit-sharing to be transparent.

The biggest difference between Collaborative Business and the business models of chain and franchise store and shared business, which are prevailing previously, is not the person, product, or information platform but the objective technology to be the bridge to connent each other. It is true that if not to encourage open source technology, it may evolve into another kind of monopoly. Therefore, for developing Collaborative Business, we must always maintain the attitude of open source technology. All the participants cooperating with each other through open source community is able to eliminate monopoly. This will help small and mirco enterprises to achieve business value, thereby stimulating ecnomic growth, employment and innovation, and realizing anti-monopoly.

To develop open source blockchain technology is significant, but the choice of technology route is also crucial. The primitive blockchain technology originates from virtual currency and public chain projects; however, the public chain projects always make for financing and their users targeted on the profit made by trade, so everyone pays more attention to the coin price but not the blockchain application ability.

Since the tokens of public chain are essentially "class currency" and "class security", they have been severely suspended by regulatory. After they are washed out, alliance chain technology has shouldered the responsibility of pushing the blockchain technology to move forward.

In 2018, "Open Consortium Chain" was announced in the industry. The alliance chain was appealed to actively encourage open source to move alliance from close to public, so that people can really feel the benefits of good experience, increased efficiency, cost reduction, trust enhancement, data interchange, and traceability of responsibility brought by blockchain. And the vision of Collaborative Business is realized.

A new generation of Open Consortium Chain proposes some new requirements on blockchain underlying technology. Except the standard blockchain characteristics, there are still several aspects that need to be strengthened:

First, because of Open Consortium Chain is not a single chain, it needs the technology to support multi-chain parallel and cross-chain communication, and at the same time, it needs the ability to support the massive transaction request from the Internet.

Second, it needs the ability to form alliances and build chains quickly and at low cost, so that each demand party can efficiently establish an alliance chain network to make the cooperation of chain building among enterprises becomes simple as building a "chat group".

Finally, it needs to be opened to achieve full trust among alliance members.

Open Consortium Chain is not only conducive to reducing the cost on trial and error for enterprise, but also promoting the development of the commercial society in the direction of credibility and transparency, and comprehensively reduces the risks of operation, moral hazard, credit, information protection, etc during cooperation.

Adhere to our above goals and vision, we officially release the FISCO BCOS 2.0 version based on the “Open Consortium Chain” technology route.

## FISCO BCOS 2.0
FISCO BCOS 2.0 version has been upgraded and optimized on the basis of the original version, and has made major breakthroughs in scalability, performance, and ease for use, including:

- Implement **group architecture**. In a global network of multiple nodes, there may be much sub-networks which are made up by multiple subsets of nodes to maintain a separate ledger. The consensus and storage among these ledgers are independent of each other and have good scalability and security. In group architecture, parallel expansion can be better achieved to meet the needs of high frequency transaction scenario for financial industry. Meanwhile, group architecture can quickly support the requirements of chain building. It greatly reduces the difficulty of operation and maintenance, and truly enables the chain building among enterprises is as easy as building a "chat group".

- Support **Distributed Storage** to enable storage to break through the limitation of single machine and support lateral spreading. Separating between calculation and storage can improve system's robustness to prevent data from being affected even if the node executive server is failure. Distributed storage defines a standard data accessing CRUD interface which can adapt to multiple storage systems and support both SQL and NoSQL data management methods to support multiple business scenarios.

- Implement **pre-compiled contract framework** to break the EVM performance bottleneck. It supports transaction concurrent processing to greatly increase the transaction processing throughput. Precompiled contract is implemented in C++ and built into the underlying system. Blockchain can automatically recognize the exclusive transaction information of calling contract, construct the DAG dependency, and plan an efficient parallel transaction execution path. In the best case, the performance is increased by N times (N = CPU cores).

- In addition, FISCO BCOS 2.0 version is continue to optimize network transmission models, computing storage processes, and so on for greatly improving performance. For the architecture, it is continuous to upgrade the high availability and ease for use in terms of storage, network, and computing. The core modules are continuous to remodel and upgrade for ensuring the system's robustness.

More 2.0 version features will be introduced in depth in the following sections. Please see [2.0 New Release] (./ what_is_new.md).

## FISCO BCOS 1.0
Looking back at the evolution of FISCO BCOS, we have been working on the balance of performance, security, availability and compliance.

- In terms of performance, FISCO BCOS has made a lot of optimizations on overall architecture and transaction processing such as the use of efficient consensus algorithms, paralelizing computing, reducing repetitive computation, upgrading critical computing units, and so on. Furthermore, the core breakthrough of its performance is not only in single-chain, but also in optimizing architecture design based on single-chain performance, and achieving the flexible, efficient, reliable, and secure parallel computing and parallel scalability capability. It can help developers to achieve the performance they need for their business scenarios by simply adding machines. In general, the FISCO BCOS platform optimizes the network communication model, adopts the Byzantine Fault Tolerance(BFT) consensus mechanism, and combines with the multi-chain architecture and cross-chain interaction scheme to solve the performance problems of concurrent access and hotspot accounts so that to meet the need of high frequency transaction scenario for financial industry.

- In terms of security, the FISCO BCOS platform achieves comprehensive security in the application, storage, network, and host layers through node admission control, reliable key management, and flexible access control. In the design of privacy protection, FISCO BCOS platform supports permission management, physical isolation, and national cryptography algorithm (standard algorithm certified by national cryptographic bureau). Meanwhile, it implements mutiple privacy protection algorithms including homomorphic encryption, zero knowledge proof, group signature, ring signature, etc. as open projects to public.

- In terms of usability, FISCO BCOS is designed to run at 7 * 24 hours to achieve a high availability needed for financial grade. In terms of regulatory, it supports regulatory and auditing agencies can join as observation nodes to obtain real-time data for regulating and auditing. In addition, it provides mutiple development interfaces to make it easier for developers to compile and call smart contract.

## Summary

In practice, FISCO BCOS has grown into a stable, efficient and secure blockchain underlying platform through testing in many external agencys, multiple applications, and longstanding running in production environments.

The subsequent content of this document will detail the tutorial of construction, installation, smart contract deployment and calling of FISCO BCOS 2.0 version, as well as an in-depth introduction to the overall architecture of FISCO BCOS 2.0 and the design of each module.

