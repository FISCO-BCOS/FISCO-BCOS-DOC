##############################################################
Consensus algorithm
##############################################################


Blockchain system adopts consensus algorithm to ensure consistency.
Theoretically, consensus is the process of commonly agreeing on a certain proposal. In distributed system, proposal is defined in a broad sense, which includes the sequence of event, who will be the leader and so on. In blockchain system, consensus is the process for consensus node to agree on transaction results.

**Types of consensus algorithm**

According to its tolerance on `Byzantine Fault <https://zh.wikipedia.org/wiki/%E6%8B%9C%E5%8D%A0%E5%BA%AD%E5%B0%86%E5%86%9B%E9%97%AE%E9%A2%98>`_ , consensus algorithms can be divided into Crash Fault Tolerance, CFT and Byzantine Fault Tolerance, BFT:

-  **CFT algorithms** ：regular fault-tolerant algorithms, when it occurs to system malfunctions in network, disk or server crash down, they can still reach agreement on a proposal. Classic CFT algorithms include Paxos and Raft which has better performance and efficiency and tolerate less than a half of malfunction nodes;
-  **BFT algorithms** ：Byzantine fault-tolerant algorithms, besides regular malfunctions happen during consensus, it can tolerate Byzantine fault like node cheating (faking execution result of transaction, etc.). Classic BFT algorithm includes PBFT, which has lower performance and tolerates less than one third of malfunction nodes.


**FISCO BCOS consensus algorithm**

FISCO BCOS realized pluggable consensus algorithm based on multi-group structure. With different group running different consensus algorithms, consensus processes are independent in each group. Currently, FISCO BCOS supports PBFT (Practical Byzantine Fault Tolerance) and Raft (Replication and Fault Tolerant) consensus algorithms:

- **PBFT algorithm**: BFT algorithm, tolerate less than one third of malfunction nodes and malicious nodes, able to reach final consistency;
- **Raft algorithm**: CFT algorithm, tolerate half of multifunction nodes except malicious nodes, able to reach consistency.



.. toctree::
   :maxdepth: 1
   

   architecture.md
   pbft.md
   raft.md
