##############################################################
Security control
##############################################################

To ensure safe communication and access of data among nodes, FISCO BCOS adopts mechanisms of node access, CA blacklist and permission control for security control in network and storage level.


**Network security control**

- **SSL connection** of nodes, ensuring secrecy of communication

- **Network access mechanism**, ensuring system security by removing malicious nodes from consensus node list or group

- **Group whitelist mechanism**, ensuring independency of communication data among groups by making each group receives the messages of the counter group only

- **CA blacklist mechanism**, disconnecting malicious nodes in time

- **Distributed storage permission control mechanism** controlling the permissions of exterior accounts for contract deployment and CRUD operations on user table


**Storage security control**

The permission control mechanism based on distributed storage controls access in a flexible and delicate way by implementing the restriction on the storage access for exterior accounts (tx.origin), which includes contract deployment, table creation and writing.


.. toctree::
   :maxdepth: 1

   node_management.md
   certificate_list.md
   chain_governance.md
   permission_control.md
