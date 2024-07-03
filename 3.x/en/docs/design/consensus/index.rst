##############################################################
4. Consensus Algorithm
##############################################################

Tags: "consensus algorithm" "design scheme" "extensible consensus framework"

----

Blockchain system guarantees system consistency through consensus algorithm。
In theory, consensus is a proposal(proposal)The process of reaching an agreement, the meaning of a proposal in a distributed system is very broad, including the order in which events occur, who is the leader, etc。In a blockchain system, consensus is the process by which consensus nodes agree on the results of transaction execution。


**consensus algorithm classification**

according to whether or not to tolerate 'Byzantine errors<https://zh.wikipedia.org/wiki/%E6%8B%9C%E5%8D%A0%E5%BA%AD%E5%B0%86%E5%86%9B%E9%97%AE%E9%A2%98>'_, consensus algorithms can be classified as fault-tolerant(Crash Fault Tolerance, CFT)Class Algorithms and Byzantine Fault Tolerance(Byzantine Fault Tolerance, BFT)Class algorithm:

-  **CFT class algorithm** Common fault-tolerant algorithms, when the system network, disk failure, server downtime and other common failures, can still reach a consensus on a proposal, the classic algorithm includes Paxos, Raft, etc., this kind of algorithm has better performance, faster processing speed, can tolerate no more than half of the failed nodes；
-  **BFT class algorithm** : Byzantine fault-tolerant algorithms, in addition to tolerating ordinary failures in the system consensus process, but also tolerating deliberate deception by some nodes(Such as falsifying transaction execution results)Such as Byzantine errors, classical algorithms include PBFT, etc., which have poor performance and can tolerate no more than one-third of faulty nodes。


**FISCO BCOS consensus algorithm**

FISCO BCOS implements a plug-in consensus algorithm based on a multi-group architecture, different groups can run different consensus algorithms, and the consensus process between groups does not affect each other, FISCO BCOS currently supports PBFT(Practical Byzantine Fault Tolerance)and Raft(Replication and Fault Tolerant)Two consensus algorithms:

- **PBFT consensus algorithm**: BFT class algorithm, can tolerate no more than one-third of the failure node and the bad node, can achieve the final consistency；



.. toctree::
   :maxdepth: 1

   pbft.md
   raft.md
   rpbft.md
   consensus.md
