# What can't be trusted in the blockchain world?？

Author: Zhang Kaixiang ｜ Chief Architect, FISCO BCOS

Previous post shared "[What Exactly Are You Trusting When You Trust a Blockchain?？](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485345&idx=1&sn=eab5bbcf45ec46bd7f69cb48de1db4b3&chksm=9f2ef5bda8597cab2f0c938251cb876d3920915f8faef1f0c60857ed44f4c8865fc355f00709&token=1692289815&lang=zh_CN#rd)"(Children's shoes that haven't been seen yet, click on the title to go directly), this time change the angle, stroll through the dark side of the moon, talk about in**Blockchain Systems and Business Design**When you don't trust anything。

Let's start with the conclusion: you can't believe almost anything！Creating Don'The concept of t Trust, Just Verify, is the right attitude to the blockchain world。- By what I said casually

## Do not trust other nodes

Blockchain nodes and other nodes will establish P2P communication, together form a network, transfer blocks, transactions, consensus signaling and other information。Other nodes may be held by different institutions, different people, and the person holding the node may be bona fide or malicious。Even in good faith assumptions, the health of the node's operation and survival will be affected by the level of operation and maintenance and resources, such as being in an unstable network, will occasionally hang up, will send random messages, or hard disk full and other reasons leading to data storage failure, and other possible failures。When making malicious assumptions, it is necessary to assume that other nodes may deceive or harm themselves, such as passing the wrong protocol packet, or using weird instructions to find vulnerabilities to attack, or launching high-frequency spam requests, frequent connections and then disconnections, or massive connections taking up resources。

Therefore, the node should regard itself as a hunter who survives alone in the dark jungle, and must have an attitude of "independence" and "self-sufficiency," and take the posture of "not believing in any other node" to protect itself。Certificate technology is required to authenticate node identity during node admission；On connection control, reject connections with exceptions；Use frequency control to limit the number of connections, requests, etc；Verify protocol packet format and instruction correctness。The information sent by oneself should not expose its own private information, nor should it be expected that other nodes will necessarily give an immediate and correct response, and must be designed for asynchronous processing and verification fault tolerance。

## Node and client do not trust each other

The client, which refers to the module that initiates a request to the blockchain outside the blockchain network, such as the java sdk and wallet client used by the business。Clients and nodes communicate through network ports。If the client is in the hands of an uncontrolled person, it is possible to make a large number of requests to the node, or send a bunch of spam messages, making the node tired to deal with, or even cleverly construct vulnerability attack information, trying to overreach, steal information or make the node wrong。

At the same time, from the client's point of view, the node may not respond or respond slowly, or return the wrong data, including format errors, status errors, said received but actually not processed, etc., or even ulterior motives will set up a "fake" node and client communication, deceiving the client。Nodes react in a way that is not in line with expectations, potentially causing the client to run incorrectly and become functionally impaired。

In order to enhance the mutual trust between nodes and clients, digital certificates can be assigned to both parties. A two-way handshake must be carried out through the certificate. The client can initiate transaction requests to the node only after the private key is signed. The node should control the client's permissions, reject high-risk interface calls, and do not easily open the node management interface and system configuration interface。Both parties strictly check the data format and data validity of each communication。The two sides should also carry out frequency control and asynchronous processing when interacting, and verify the results of each interaction, which cannot be preset for the other party to handle correctly, and must obtain transaction receipts and processing results for confirmation。

When it is considered that communicating with only one node does not guarantee security, the client can use "f+1 query "idea, as much as possible and a few nodes communication。If the consensus security model of the current chain is "3f+1, "then, if from f+The information read by 1 node is consistent, and the result can be confirmed。



## Untrusted block height

Block height is a very critical piece of information that represents the current state of the entire chain。Operations such as sending transactions to the blockchain, consensus between nodes, and verification of blocks and states all depend on block height。

When a node is disconnected or the processing speed is slow, its block height may lag behind the entire chain, or when a node maliciously falsifies data, its height may exceed the entire chain。In the event of a fork in the chain, if the block height on one fork is surpassed by another fork, the backward fork becomes meaningless。Even under normal circumstances, it is still possible for a node to be intermittently one to a few blocks behind the entire chain, and then it is possible to catch up with the latest height within a certain period of time。

For example, in the PBFT consensus model, when more than 2 / 3 of the total number of nodes are at the same height, the full chain has the opportunity to reach a consensus and continue to issue blocks。The remaining 1 / 3 of the nodes may be at a different height from the nodes participating in the consensus, which means that the data read from this node is not the latest data on the network and can only represent a snapshot of the chain at that height。

The business logic can use the block height as a reference value, do some decision logic based on the height, and use f on the chain of deterministic consensus (e.g., PBFT)+1 Query and other methods to confirm the latest height of the chain, in the chain of possible forks, you need to refer to the "six block confirmation" logic, carefully select the trusted block height。

## Do not trust transaction data

A transaction (Transaction) represents a transaction request initiated by one party to another, which may result in the transfer of assets, change the account status or system configuration, and the blockchain system confirms the transaction through consensus to make the transaction take effect。Transactions must be accompanied by the sender's digital signature, all data fields in the transaction must be included in the signature, unsigned fields have the potential to be forged and will not be accepted。When the transaction data is broadcast on the network, it can be read by others, and if the transaction data contains private data, the sender must desensitize or encrypt the data。Transactions may be reissued for network reasons, or saved by others and deliberately sent again, resulting in "replay" of transactions, so the blockchain system must guard against heavy transactions and avoid "double flowers."。

## Do not trust state data

The state (State) data of the blockchain is generated after the smart contract is run, and ideally, the contract engine of each node is consistent, the inputs are consistent, and the rules are consistent, so the output state should be consistent。However, different nodes may have different software versions installed, or the sandbox mechanism of the contract engine is not tight enough to introduce uncertainties, or even be hacked, tampered with, or there are other inexplicable bugs, which may lead to inconsistent output from the contract, so consistency and transactionality cannot be guaranteed。

State verification is a very expensive thing, the typical verification method is to use MPT (Merkle Patricia Tree) tree, all states are crammed into the tree management。The MPT tree can attribute all states to a Merkleroot Hash, a state tree Merkleroot generated after the transaction is confirmed between nodes in the consensus process to ensure consistent states。

This tree has a complex structure, a large amount of data, and consumes a lot of computing and storage resources, which can easily become a performance bottleneck。Therefore, the verification of the state needs to have a faster, simpler, and more secure solution, such as the combination of version verification, incremental hash verification and other algorithms, supplemented by data caching, can reduce the number of repeated calculations and optimize IO, can ensure consistency, correctness at the same time, effectively improve the efficiency of verification。

## Do not trust private key holder

Using private keys to sign transactions and other key operations, and then using public keys to verify them, is the most basic verification logic on the blockchain。This logic is secure as long as the private key is used correctly。

But the private key is only a piece of data, only rely on the private key, the user is anonymous。In the scenario faced by the alliance chain, you need to use a permissioned identity, first confirm the identity through real-world authentication methods such as KYC, due diligence, and authoritative authentication, and then bind the identity and the public key and publish it, or issue a public-private key in combination with the digital certificate of the PKI system, so that the identity corresponding to the private key is known, trusted, and controllable。

Private keys may be stolen by others due to loss, leakage, or loss of assets due to forgetting。Therefore, in the preservation of the private key, we need to consider the use of comprehensive protection schemes, such as encrypted storage, TEE environment, password card, USBkey, soft and hard encryption machine and so on。In the management of the private key, you need to consider how to safely reset and retrieve the key after it is lost。

There are several ways to use the enhanced version of the private key, such as the use of multi-signature, threshold signature, etc., each transaction must be signed with multiple private keys, private keys can be kept in different places, high security, but the technical solution and use experience is complex。

Another is the separation of the transaction private key and the management private key。The transaction private key is used to manage assets, the management private key is used to manage personal data, the transaction private key can be reset by the management private key, and the management private key itself is stored separately for reset or retrieval through algorithms such as thresholds and sharding。

## Do not trust other chains

In the cross-chain scenario, each chain has its own assets, consensus, the security model between the chains becomes very complex, such as a chain of bookkeepers colluding to fake, or the chain has a fork, block height rollback, then if the other modules outside the chain and the chain have not been rigorous enough interaction, will cause data inconsistency or asset loss。If different chains still use different platform architectures, it will be more complex in engineering。

Cross-chain, side-chain is still the industry in the research and gradual realization of the subject, the main purpose is to solve the communication between the chain and the chain, asset lock-in and asset exchange, to ensure the overall consistency of the whole process, transaction transactions, and anti-fraud。To transfer an asset from chain A to chain B, it is necessary to ensure that the assets on chain A are locked or destroyed, and that a corresponding asset is added to chain B, and that there is a mechanism to ensure the safety of assets in both directions in the time window when there may be forks and rollbacks on both sides, respectively。

In the existing cross-chain scheme, there are trunking, inter-chain HUB and other ways, the design of these systems itself to achieve a high degree of credibility and reliability of the standard, the security level should not be lower than or even higher than the docking chain, the same should also adopt a multi-center, group consensus system design, the overall complexity can be regarded as the N-th power of the chain。

## Do not trust the network layer

Blockchain nodes need to communicate with other nodes, so they must expose their communication ports on the network。The node must protect itself at the network layer, including setting up IP black and white lists on the gateway, setting port policies, DDOS traffic protection, and monitoring network traffic and network status。Non-essential ports should not be open to the public network. For example, RPC ports used for management and monitoring can only be open to the organization。

## Untrusted code

"Code is law" is indeed a loud slogan, but before the programmer's hair falls out, the code he writes may have bugs, just to see if it's fast to write bugs or fix bugs。

Whether it is the underlying code or smart contract code, there may be technical or logical pits, but all the data and instruction behavior generated by the code, it needs to be strictly verified by another piece of code, the code itself also needs to be static and dynamic scanning, including the use of formal proof and other technologies to conduct a comprehensive audit verification to detect possible logical errors, security vulnerabilities or whether there is information disclosure。Some time ago, there was a code of a hotel system published on github, which actually included the username and password of mysql connection, and the database port was actually open to the public network, this kind of pit is simply unimaginable。

Open out the open source code, of course, can be reviewed, feedback to improve security, may also be rummaging for loopholes, random modification, and even malicious mine。But in general, open source still has more advantages than disadvantages。In the open source community, developers will submit PR (Pull Request) to the project。PR audit is very critical and very heavy work, it is worth to arrange experts and allocate a lot of time to do audit。An old driver of an open source project revealed that the PR of the core module of the project took years to review, otherwise "adding a feature to introduce two bugs" would have been a real loss, not to mention if it had been planted with a loophole。

## Do not trust the bookkeeper

The process of consensus can be roughly abstracted as, select the bookkeeper, the bookkeeper publishes the block, and the other nodes check and confirm。Bookkeeping in the public chain can be done in a "mining" manner (e.g. Bitcoin), where the miner endorses his own integrity at the expense of a large amount of computing power, or obtains the right to book with a large amount of asset equity collateral (consensus such as Pos and DPos)。In algorithms such as PBFT / Raft, which are commonly used in affiliate chains, the list of bookkeepers can be generated randomly or in rotation, with the bookkeeper giving a proposal and other voters submitting it in multiple steps to collect votes。According to the principle that the minority is subordinate to the majority, consensus can only be reached if more than 2 / 3 of the consensus nodes agree。

From a system availability perspective, the bookkeeper has the potential to make errors, crash, or run slowly, affecting the entire chain out of the block。Or the bookkeeper can only include transactions with high fees and discard some transactions, resulting in some transactions always failing to be concluded。Some bookkeepers can also rely on computing power or black-box operation, "pre-digging" or "block attack," destroying the game relationship.....

If the bookkeeper fails or commits an evil act that exceeds the consensus safety threshold, it will directly harm the value base of the entire chain。According to different bookkeeping models, bookkeepers need to design different fault tolerance, verification, anti-fraud algorithms, implement incentive and punishment mechanisms, regularly check the health of bookkeepers during operation, and for bookkeeping nodes that are unable to keep accounts or do evil, the whole network will not accept their bookkeeping results and punish them, or even kick them out of the network。

------

There are many more to list, including contracts, certificates, synchronization, etc., each module has its own function and risk points, it is difficult to write。In short, the blockchain as a distributed multi-party collaboration system, access to a variety of participants, the whole system is by no means a single developer or operator can control, "good faith speculation" in this field has not been fully applicable, the whole world step by step, cold arrows everywhere, only through careful algorithms and complicated processes to maintain consensus and security, in short, without verified information, a byte can not be trusted。

Compared with the software design in a single environment, the design ideas in the blockchain field are indeed subversive, and developers have to jump out of the thinking mode of "doing functions, only fault-tolerant, not cheat-proof," and design with the attitude of "doubting everything."。

When facing the blockchain field, developers should not only think about how to implement a function, but also think about whether there will be errors in the whole process, whether data will be tampered with, vulnerabilities will be discovered, systems will be attacked, and other participants will be defrauded。To empathize with the functions you achieve, how they will be used by others, how they will behave in different environments, and what consequences they may have。Any received information, any process input, output, must be strictly verified to be accepted, developers can do this, is to open the door to the new world of blockchain, in order to live to at least the second episode of the series。

Distributed algorithms, symmetric asymmetric encryption, HASH, certificates, security and privacy technologies are popular in the blockchain field, all in order to protect the information at the same time, to add layers of proof and verifiable factors to the information, which makes the whole system complex and cumbersome, but it is worth it, because it can be verified together to build "security" and "trust."。

Above, for programmers who are ready to jump into a pit, or are already in a pit。Mutual encouragement。