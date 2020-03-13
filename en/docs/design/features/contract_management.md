# Contract Management

This document describes the design of the freezing/unfreezing/destroying operations and their operation permissions in contract life cycle management.

```eval_rst
.. important::
   The freezing/unfreezing/destroying operation on the contract supports storagestate storage mode, but not mptstate storage mode.The contracts mentioned here are only smart contracts and do not include pre-compiled contracts or CRUD contracts currently.
```

## noun explanation

Contract management related operations include `freezeContract`, `unfreezeContract`, `destroyContract`, `grantContractStatusManager`, `getContractStatus`, `listContractStatusManager`.

- freezeContract : Reversible operation, the interfaces of a frozen contract can not be called
- unfreezeContract : Undo the `freezeContract` operation, the interfaces of an unfrozen contract can be called
- destroyContract : Irreversible operation，the interfaces of a frozen contract can not be called and cannot be restored
- getContractStatus : Query the status of a contract to return the status of available/frozen/destroyed
- grantContractStatusManager : Grant the account's permission of contract status managememt
- listContractStatusManager : Query a list of authorized accounts that can manage a specified contract

The state transition moments are shown below:

|          | available | frozen  | destroyed |
| -------- | --------- | ------- | --------- |
| freeze   | Success   | Fail    | Fail      |
| unfreeze | Fail      | Success | Fail      |
| destroy  | Success   | Success | Fail      |

## Implementation

### Record of Contract status

- Reuse the existing `alive` field in the contract table to record whether the contract has been destroyed. This field defaults to true, and the value is false when killed.
- A new field `frozen` is used to record whether the contract has been frozen. The default of this field is false, indicating that it is available. When frozen, the value is true.
- A new field `authority` is used to record accounts that can freeze/unfreeze/destroy the contract. Each account with permission corresponds to one line of `authority` records.

**Note:**

1. False will be returned when querying the field for the contract table with no field `frozen`;
2. When a contract is deployed, the authorization for tx.origin and the authorization of parent contract, if any, is written to field `authority` by default.

### Judgment of contract status

In the Executive module, the values of alive and frozen fields are obtained according to the address of a contract, and the transaction will be executed smoothly, or an exception is thrown to indicate that the contract has been frozen or destroyed after judgment.

### Judgment of authority

- The authority to freeze/unfreeze/destroy contract needs to be determined. Only the accounts in authority list can set the contract status;
- The authority to grant authorization needs to be determined. Only the account in the authority list can grant other accounts the authorization to manage the contract;
- Any account can query contract status.

### Interfaces of contract management

A contract management precompiled named ContractStatusPrecompiled is added with 0x1007 address, which is used to set and query contract status.

```text
contract ContractStatusPrecompiled {
    function destroy(address addr) public returns(int);
    function freeze(address addr) public returns(int);
    function unfreeze(address addr) public returns(int);
    function grantManager(address contractAddr, address userAddr) public returns(int);
    function getStatus(address addr) public constant returns(uint,string);
    function listManager(address addr) public constant returns(uint,address[]);
}
```

```eval_rst
.. important::
   Contract management related operations can only be performed on 2.3 and above.
```
