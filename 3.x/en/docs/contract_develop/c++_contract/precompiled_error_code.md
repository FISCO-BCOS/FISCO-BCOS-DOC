# Precompiled Contract Error Codes and Countermeasures

Tags: "precompiled contract" "precompiled error code" "error message" "RetCode"

---

There are two main ways to pass errors in precompiled contracts, one is to return a specific numeric value on the interface, which is usually a negative number less than 0 at the time of the error.；The other is to throw an exception, when the status code of the receipt is 15, the user can take the initiative to parse the 'message' field in the receipt for further error analysis.。

The following table mainly shows the error codes returned by the interface and the measures to be taken when the corresponding error codes are encountered.。

| error code| Error Message| Error message / workaround|
|--------|------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| 0      | Success                      | Success|
| -53006 | Wrong file type| Appears when calling the BFS touch interface with the wrong file type|
| -53005 | Wrong file path| This error occurs when calling the BFS interface to pass in the absolute path, the total length of the absolute path of the BFS cannot exceed 56, the total number of stages of the path cannot exceed 6, and it cannot contain special characters|
| -53003 | Failed to create folder| An exception occurs when creating a folder when calling the BFS link API. For example, the parent folder corresponding to the contract name already exists.|
| -51202 | Incoming version number or address is wrong| Appears when the BFS link interface is called, the version number cannot have'/'The contract address must also exist.|
| -53002 | File already exists| The file name created when calling the BFS writer interface already exists|
| -53001 | File does not exist| The file corresponding to the absolute path does not exist when calling the BFS read interface|
| -51800 | Ring signature verification failed| Appears when calling the verification interface of ring precompiled fails to check whether the input parameters are correct|
| -51700 | Group signature verification failed| Appears when calling the validation interface of group precompiled fails to check whether the passed-in parameters are correct|
| -51508 | The key of Remove does not exist.| When the remove interface of the table precompiled contract is called, the remove key does not exist.|
| -51507 | Update key does not exist.| Appears when the update interface of the table precompiled contract is called. The update key does not exist.|
| -51506 | The insert key already exists.| Appears when the insert interface of the table precompiled contract is called, and the insert key already exists|
| -51103 | Node ID does not exist| Appears when calling the Consensus precompiled contract. The passed-in node id parameter does not exist.|
| -51102 | Wrong node weight value| Appears when calling the setWeight and addSealer interfaces of the Consensus precompiled contract. The set weight cannot be less than or equal to 0|
| -51101 | Cannot delete last consensus node| Appears when the removeNode and addObserver interfaces of the Consensus precompiled contract are called, and the last consensus node in the chain cannot be deleted|
| -51100 | Wrong node ID| Node ID must be a 128-length hexadecimal string|
| -51004 | ACL map decoding error for contract method| The ACL map decoding error of the permission method occurs when calling the contract permission precompilation contract, and it is necessary to consider whether the storage is written out.|
| -51003 | Wrong permission type| The permission type of the precompiled contract is displayed when the contract permission is called. Currently, only whitelist and blacklist types are supported.|
| -51002 | ACL type for contract method does not exist| The read interface of the precompiled contract will appear when calling the contract permission, the type does not exist generally because there is no setting, the default as all users can call|
| -51001 | ACL for contract method does not exist| The read interface of the precompiled contract will appear when calling the contract permission. The ACL does not exist because it is not set. By default, all users can call the|
| -50105 | Open table error             | Internal error, failed to open storage table|
| -50104 | Create table error           | Internal error, failed to create storage table|
| -50103 | Table set row error          | Internal error, write to storage table failed|
| -50102 | Invalid address format       | Internal error, malformed address|
| -50101 | Undefined function           | Internal error, called precompiled contract method does not exist|
| -50100 | Table not exist              | Internal error, accessed table does not exist|
| -50007 | Table field duplicate| Appears when the createTable and appendColumn interfaces of the TableManager precompiled contract are called, with duplicate fields|
| -50006 | The field value of Table is too long| Appears when the write interface of the Table precompiled contract is called, and the written field value is too long, exceeding 16MB|
| -50005 | The key value of Table is too long| Appears when the write interface of the Table precompiled contract is called, and the key value written is too long, exceeding 255|
| -50003 | The field name of Table is too long| Appears when you call the createTable and appendColumn interfaces of a TableManager precompiled contract with a field name that exceeds 64|
| -50002 | Table name is too long| Appears when the createTable interface of the TableManager precompiled contract is called and the table name exceeds 50|
| -50001 | Table already exists| Appears when the createTable interface of the TableManager precompiled contract is called, the table name already exists|
| -50000 | No access| When the permission mode is enabled, the precompiled contract for direct access to System, Consensus, and AuthManager appears without direct access permission.|

