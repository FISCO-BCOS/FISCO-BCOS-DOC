# RPC

RPC(Remote Procedure Call) is a set of protocols and interfaces that the client interacts with blockchain system. The user can query the blockchain related information (such as block number, blocks, node connection, etc.) and send the transaction request through RPC interface.

## 1 Concepts
- [JSON](http://json.org/)(JavaScript Object Notation): A lightweight data exchange format. It can represent numbers, strings, ordered sequences, and key-value pairs.

- [JSON-RPC](https://www.jsonrpc.org/specification): A stateless, lightweight remote procedure call protocol. The specification primarily defines several data structures and their processing rules. It is allowed to run in the same process based on much different messaging environments such as socket, http, etc. It uses JSON ([RFC 4627](http://www.ietf.org/rfc/rfc4627.txt)) as the data format. FISCO BCOS adopts the JSON-RPC 2.0 protocol.


## 2 Module architecture
 ![](../../images/rpc/rpc.png)

The RPC module is responsible for providing the external interface of FISCO BCOS. The client sends the request through RPC, and RPC obtains the relevant response by calling [book management module](architecture/group.md) and [p2p module](p2p/p2p.md), and returns the response to the client. The ledger management module manages the relevant modules at the underlying of blockchain through a multi-book mechanism, including [consensus module](consensus/index.html), [synchronization module](sync/sync.md), block management module, transaction pool module, and block verification module.

## 3 Data definition
### 3.1 Client request

The client request sent to blockchain node will trigger an RPC calling. The client request includes the following data members:
- jsonrpc: A string specifying the JSON-RPC protocol version, which must be accurately written as "2.0".
- method: The name of the method to call.
- params: The parameters required to call the method. The method parameters are optional. Since FISCO BCOS 2.0 enables a multiple ledger mechanism, this specification requires that the first parameter passed in must be the group ID.
- id: The established unique ID of client. The ID must be a string, a numeric value or a NULL value. If the ID does not include one of these, it is considered as a notification.


The example format of RPC request package:
```
{"jsonrpc": "2.0", "method": "getBlockNumber", "params": [1], "id": 1}
```
**Note:**       

- NULL is not recommended as the id value in the request object because the specification will use a null value to identify the request as an unknown id.
- Decimal is not recommended as the id value in request objects because of uncertainty.


### 3.2 Server response

When starting an RPC calling, all blockchain nodes must respond others except the notification. The response represents as a JSON object, using the following members:
- jsonrpc: A string specifying the JSON-RPC protocol version which must be accurately written as "2.0".
- result: The correct result field. This member must be included when the response is processed successfully, and must not be included when the calling method causes an error.
- error: Error result field. The member must be included in the failure, and must not be included when no error is caused. Its parameter value must be an object defined in the [3.3](#error-object) section.
- id: The member must be contained. Its value must match the id value in the corresponding client request. If the id of the request object shows errors (such as a parameter error or an invalid request), the value must be null.

The example format of RPC response package:
```
{"jsonrpc": "2.0", "result": "0x1", "id": 1}
```
**Note:**
The server response must contain a member result or error, but not both of them.

### 3.3 Error object
When an RPC calling encounters error, the returned response object must contain an error result field. For relative description and error codes, please check [RPC error codes](../api.html#rpc)

## 4 RPC interface design

FISCO BCOS provides the rich RPC interfaces for client calling. They are divided into 3 categories:
- The query interface named beginning with get: For example, `[getBlockNumber]` interface, is to query the latest block number.
- `[sendRawTransaction]` interface: to execute a signed transaction and return response after blockchain achieves consensus.
- `[call]` interface: Executing a request will not create a transaction, so no blockchain consensus is required, but a response is returned immediately.


## 5 RPC interface list
Refer to [RPC API Documentation](../api.md)
