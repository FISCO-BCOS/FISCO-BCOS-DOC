##############################################################
14. Security control
##############################################################

Tags: "Security Control" "Network Security" "Storage Security" "Black and White List" "Permission Control"

----

In order to ensure the security of communication between nodes and the security of node data access, FISCO BCOS introduces three mechanisms: node admission mechanism, CA blacklist and permission control, and makes strict security control at the network and storage level。


**Network Level Security Control**

- Node usage**SSL connection** The confidentiality of communication data is guaranteed

- Introduction**network admission mechanism** You can delete the offending node of a specified group from the consensus node list or group to ensure system security

- Pass**group whitelist mechanism** to ensure that each group can only receive messages from the corresponding group, ensuring the isolation of communication data between groups

- Introduction**CA blacklist mechanism** , can disconnect the network connection with the evil node in time

- Presented**authority governance system** Mechanism for flexible, granular control of external account deployment contracts and permissions to create, insert, delete, and update user tables。


**Storage-plane security controls**

Based on distributed storage, a distributed storage permission control mechanism is proposed to perform effective permission control in a flexible and fine-grained manner, and a permission control mechanism is designed and implemented to restrict external accounts(tx.origin)Access to storage. Permissions include contract deployment, table creation, and table write operations。


.. toctree::
   :maxdepth: 1

   node_management.md
   certificate_list.md
   permission_control.md
   committee_design.md
