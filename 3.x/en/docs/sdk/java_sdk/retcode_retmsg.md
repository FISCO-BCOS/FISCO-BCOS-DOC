# Return Code and Error Message Summary

Tags: "TransactionResponse" "Response Code" "Error Message" "returnCode" "returnMessages" "Transaction Receipt"

----

## 1. The node returns the data structure.

The data returned by the node through the RPC interface, including the following. The detailed reasons are explained below:

- Node is not successfully linked, RPC request error
- The chain is successful on the node, including: execution failure, transaction rollback, precompiled contract execution error.

Examples of nodes that are not successfully linked are as follows:

```json
{
        "error" :
        {
                "code" : 10002,
                "message" : "TxPoolIsFull"
        },
        "id" : 102,
        "jsonrpc" : "2.0"
}
```

An example of a successful link on a node is as follows:

```json
{
        "id" : 16,
        "jsonrpc" : "2.0",
        "result" :
        {
                "blockNumber" : 12,
                "checksumContractAddress" : "",
                "contractAddress" : "",
                "extraData" : "",
                "from" : "0xa38e104bb4a92a52452b48342c935f68df20c2ce",
                "gasUsed" : "1132",
                "hash" : "0x",
                "logEntries" : [],
                "message" : "",
                "output" : "0x08c379a00000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000001a4279746573206172726179206973206c657373207468616e2032000000000000",
                "status" : 16,
                "to" : "0x19a6434154de51c7a7406edf312f01527441b561",
                "transactionHash" : "0x8075a4f016dec5147eb60df219529c3d504c5fad2c16691b39cff457692afeb1",
                "version" : 0
        }
}
```

After a user initiates a transaction or RPC request, the following steps are required to analyze the JSON result returned by the node from the RPC interface:

- Determine whether there is an error in the outermost layer of the JSON. If there is an error, it means that the transaction has failed before entering the consensus, and the RPC request has failed.；The user performs specific operations according to the message in the error。
- If there is a result structure, it means that the transaction is successfully linked and the RPC request returns success.
  - If it is a transaction, the status in the result is parsed. If it is 0, the execution is successful, and the others are execution errors.
- If you call a precompiled contract, you need to decode the output based on the ABI.

## 2. The error code that the node has not been successfully linked

| error code| Error reason|
|--------|----------------------------------------------------|
| -32700 | Node-side JSON decoding fails, usually in RPC requests|
| -32600 | Bad request, missing fields in JSON, etc.|
| -32601 | The requested RPC method does not exist|
| -32603 | Internal error, usually the node has an error|
| -32000 | The node is not yet started, it usually appears in Pro and Max nodes|
| -32004 | Service has not been initialized yet, generally appears on Pro and Max nodes|
| -32005 | Request group does not exist|
| 10000  | The Nonce check fails, and the transaction request is usually sent repeatedly.|
| 10001  | The block limit check fails. Generally, the high state of the SDK block is too far behind the node.|
| 10002  | The trading pool is full.|
| 10003  | Unknown error|
| 10004  | The transaction already exists in the transaction pool|
| 10005  | The deal is already on the chain|
| 10006  | Bad Chain ID|
| 10007  | Bad Group ID|
| 10008  | Wrong transaction signature|
| 10009  | Transaction request sent to wrong group|
| 10010  | Transactions in the trading pool have not been processed for more than 10 minutes.|

## 3. RPC request error codes other than transactions.

| error code| Error reason|
|--------|--------------------------------------|
| -1     | Request Error|
| 0      | Request successful|
| 3003   | Ledger request parameter error, usually an intra-node error|
| 3006   | Failed to request Block data|
| 3008   | Ledger request for storage failed|
| 3010   | Unknown error in Ledger layer|

## 4. Link receipt error code on node

| error code| Error code| Error reason|
|--------|----------------------------|------------------------------------------------------------------------------|
| 0      | None                       | Success|
| 1      | Unknown                    | Unknown error|
| 10     | BadInstruction             | Error in constructor, generally occurs when executing code using unknown opcode|
| 11     | BadJumpDestination         | Stack error, which usually occurs when jumping to the wrong stack during execution|
| 12     | OutOfGas                   | Exceeding the gas usage during execution. When deploying a contract, an error may be reported. Exceptional Failed Code Deposit|
| 13     | OutOfStack                 | Stack limit exceeded when executing|
| 14     | StackUnderflow             | Lack of stack, generally occurs in the execution of the lack of parameters, lack of input|
| 15     | PrecompiledError           | Pre-compiled contract execution failed|
| 16     | RevertInstruction          | Transaction Rollback|
| 17     | ContractAddressAlreadyUsed | The deployed contract address already exists|
| 18     | PermissionDenied           | Insufficient permissions to invoke contracts and deploy contracts|
| 19     | CallAddressError           | Request contract address does not exist|
| 21     | ContractFrozen             | Contracts have been frozen.|
| 22     | AccountFrozen              | Account has been frozen|
| 23     | AccountAbolished           | Account has been abolished|
| 24     | ContractAbolished          | The contract has been annulled|
| 32     | WASMValidationFailure      | WASM binary validation failure, usually occurs when the wrong cryptographic method is used, WASM uses floating point numbers|
| 33     | WASMArgumentOutOfRange     | Too many WASM parameters|
| 34     | WASMUnreachableInstruction | Error during WASM execution|
| 35     | WASMTrap                   | WASM execution failed|

## 5. Precompiled contract error code.

**Response code**与**Error Message**corresponding table

| error code| Error code| Error Message|
|--------------|--------------------------------------------------------------------------|----------------------------------------------------------|
| 0            | Success                                                                  | Success|
| -53007       | File Count Error                                                         | An error occurred when BFS called the count interface while executing the list interface, usually a storage error|
| -53006       | Invalid file type                                                        | File type of BFS error|
| -53005       | Invalid path                                                             | Path to BFS Error|
| -53003       | Make directory failed                                                    | BFS create path directory failed|
| -53002       | File already existed                                                     | BFS file already exists|
| -53001       | File not exist                                                           | BFS requested file does not exist|
| -51900       | Account already exist                                                    | The new account already exists|
| -51800       | verify ring sig failed                                                   | Ring signature verification failed|
| -51700       | verify group sig failed                                                  | Failed to verify group signature|
| -51508       | remove key not exist                                                     | CRUD Deletes a nonexistent key|
| -51507       | Key not exist in table, use insert method                                | CRUD update key that does not exist|
| -51506       | Don't insert the key already existed                                     | CRUD inserts an existing key|
| -51202       | The version string or address is error                                   | BFS Inserts Contract Address and Contract Version with Error|
| -51104       | Add sealer should exist in observer                                      | You must join the Sealer node to the Watch node before adding the node|
| -51103       | The node is not exist                                                    | The node for the operation does not exist|
| -51102       | The weight is invalid                                                    | Set the weight of node errors|
| -51101       | The last sealer cannot be removed                                        | The last sealer node should not be deleted|
| -51100       | Invalid node ID                                                          | Wrong node ID|
| -51004       | Auth map decode error                                                    | Permissions map parsing error, usually a storage error|
| -51003       | Error auth type input                                                    | Wrong permission ACL type used|
| -51002       | The contract method auth type not set, please set method auth type first | Permission ACL not set|
| -51001       | The contract method auth not exist                                       | Permission table not set|
| -50105       | Open table error                                                         | Error reading storage table|
| -50104       | Create table error                                                       | Error creating storage table|
| -50103       | Table set row error                                                      | Error setting storage table rows|
| -50102       | Invalid address format                                                   | Wrong address format|
| -50101       | Undefined function                                                       | Called a precompiled contract method that does not exist|
| -50008       | Invalid table name or field name                                         | Bad table or field name|
| -50007       | The table contains duplicated field                                      | Duplicate field in table|
| -50006(or 15) | The field value exceeds the limit                                        | Field value exceeds 16MB length limit|
| -50005(or 15) | The value exceeds the limit, key max length is 255                       | Primary key value exceeds 255 length limit|
| -50004       | The length of all the fields name exceeds the limit 1024                 | Total field length of table exceeds 1024 limit|
| -50003(or 15) | The table field name exceeds the limit 64                                | Single field length exceeds 64 limit|
| -50002(or 15) | The table name length exceeds the limit 50                               | Table name exceeds 50 length limit|
| -50001       | The table already exist                                                  | Table name already exists|
| -50000       | No authrized                                                             | Request does not have permission|
