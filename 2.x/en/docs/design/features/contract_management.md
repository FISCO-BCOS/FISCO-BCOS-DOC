# Contract Management

This document describes the design of the freezing/unfreezing operations(referred to as contract lift cycle status management operation on below) and their operation permissions in contract life cycle management.

```eval_rst
.. important::
   The contract life cycle management operation on the contract supports storagestate storage mode, but not mptstate storage mode.The contracts mentioned here are only Solidity contracts and do not include pre-compiled contracts currently.
```

## noun explanation

Contract management related operations include `freezeContract`, `unfreezeContract`, `grantContractStatusManager`, `getContractStatus`, `listContractStatusManager`.

- [freezeContract](../../console/console.html#freezecontract) : Reversible operation, the interfaces of a frozen contract can not be called
- [unfreezeContract](../../console/console.html#unfreezecontract) : Undo the `freezeContract` operation, the interfaces of an unfrozen contract can be called
- [getContractStatus](../../console/console.html#getcontractstatus) : Query the status of a contract to return the status of available/frozen
- [grantContractStatusManager](../../console/console.html#grantcontractstatusmanager) : Grant the account's permission of contract status managememt
- [listContractStatusManager](../../console/console.html#listcontractstatusmanager) : Query a list of authorized accounts that can manage a specified contract

```eval_rst
.. important::
   The operation of freezing a contract will not modify the original contract content, including logic and data, and will only be recorded through a field.
```

The state transition moments are shown below:

|          | available | frozen  |
| -------- | --------- | ------- |
| freeze   | Success   | Fail    |
| unfreeze | Fail      | Success |

## Implementation

### Record of Contract status

- A new field `frozen` is used to record whether the contract has been frozen. The default of this field is false, indicating that it is available. When frozen, the value is true.
- A new field `authority` is used to record accounts that can manage contract status. Each account with permission corresponds to one line of `authority` records.

**Note:**

1. False will be returned when querying the field for the contract table with no field `frozen`;
2. When a contract is deployed, the tx.origin will be written to field `authority` by default.
3. When an interface of contract A was called to create contract B, the tx.origin and the authorization of contract A is written to field `authority` of contract B by default.

### Judgment of contract status

In the Executive module, the values of frozen fields are obtained according to the address of a contract, and the transaction will be executed smoothly, or an exception is thrown to indicate that the contract has been frozen after judgment.

### Judgment of authority

- The authority to update contract status needs to be determined. Only the accounts in authority list can set the contract status;
- The authority to grant authorization needs to be determined. Only the account in the authority list can grant other accounts the authorization to manage the contract;
- Any account can query contract status and authorization list.

### Interfaces of contract life cycle management

A contract life cycle management precompiled named ContractLifeCyclePrecompiled is added with 0x1007 address, which is used to set and query contract status.

```text
contract ContractLifeCyclePrecompiled {
    function freeze(address addr) public returns(int);
    function unfreeze(address addr) public returns(int);
    function grantManager(address contractAddr, address userAddr) public returns(int);
    function getStatus(address addr) public constant returns(uint,string);
    function listManager(address addr) public constant returns(uint,address[]);
}
```

### Description of return code

| code   | message                                                    |
| ------ | ---------------------------------------------------------- |
| 0      | success                                                    |
| -51900 | the contract has been frozen                               |
| -51901 | the contract is available                                  |
| -51902 | the contract has been granted authorization with same user |
| -51903 | the contract address is invalid                            |
| -51904 | the address is not exist                                   |
| -51905 | this operation has no permissions                          |

```eval_rst
.. important::
   Contract management related operations can only be performed on 2.3 and above.
```
