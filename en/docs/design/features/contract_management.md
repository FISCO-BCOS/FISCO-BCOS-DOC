# Contract Management

This document describes the design of the freezing/unfreezing/killing operation in contract life cycle management.

```eval_rst
.. important::
   The freezing/unfreezing/killing operation on the contract supports storagestate storage mode, but not mptstate storage mode.The contracts mentioned here are only smart contracts and do not include pre-compiled contracts or CRUD contracts.
```

## noun explanation

Contract management related operations include freezing, unfreezing, killing, query.

- freeze : Reversible operation, the interfaces of a frozen contract can not be called
- unfreeze : Undo the frozen operation, the interfaces of an unfrozen contract can be called
- kill : Irreversible operation，the interfaces of a frozen contract can not be called and cannot be restored
- query : Query the status of a contract to return the status of available/frozen/killed

The state transition moments are shown below:

|          | available | frozen  | killed |
| -------- | --------- | ------- | ------ |
| freeze   | Success   | Fail    | Fail   |
| unfreeze | Fail      | Success | Fail   |
| kill     | Success   | Success | Fail   |

## Implementation

### Record of Contract status

- Reuse the existing `alive` field in the contract table to record whether the contract has been killed. This field defaults to true, and the value is false when killed.
- A new field `frozen` is used to record whether the contract has been frozen. The default of this field is false, indicating that it is available. When frozen, the value is true.
- A new field `authority` is used to record accounts that can freeze/unfreeze/kill the contract, split by ",".

**Note:**

1. False will be returned when querying the field for the contract table with no field `frozen`;
2. Authority information will be written into the deployment contract.

### Judgment of contract status

In the Executive module, the values of alive and frozen fields are obtained according to the address of a contract, and the transaction will be executed smoothly, or an exception is thrown to indicate that the contract has been frozen or killed after judgment.

### Judgment of authority

- The authority to freeze/unfreeze/kill contract needs to be determined. Only the accounts in authority list can set the contract status;
- Any account can query contract status.

### Interfaces of contract management

A contract management precompiled named ContractStatusPrecompiled is added with 0x1007 address, which is used to set and query contract status.

```text
contract Frozen {
    function kill(address addr) public returns(int);
    function freeze(address addr) public returns(int);
    function unfreeze(address addr) public returns(int);
    function queryStatus(address addr) public constant returns(uint,string);
}
```

```eval_rst
.. important::
   Compatibility description: the operation of freezing/unfreezing/killing/querying contracts can be carried out on version 2.2. However, the authority to freeze/unfreeze/kill contracts can only be checked on version 2.3.
```
