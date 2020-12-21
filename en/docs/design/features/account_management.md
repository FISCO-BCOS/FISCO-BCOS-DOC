# Account Management

This document describes the design of the freezing/unfreezing account operations and their operation permissions.

```eval_rst
.. important::
   The account life cycle management operation on the account supports storagestate storage mode, but not mptstate storage mode.
```

## noun explanation

Account management related operations include `freezeAccount`, `unfreezeAccount`, `getAccountStatus` and authority management related operations.

- [freezeAccount](../../console/console.html#freezeaccount) : Reversible operation, the interfaces of a frozen account can not deploy or execute transaction
- [unfreezeAccount](../../console/console.html#unfreezeaccount) : Undo the `freezeAccount` operation, the interfaces of an unfrozen account can deploy or execute transaction
- [getAccountStatus](../../console/console.html#getaccountstatus) : Query the status of a account to return the status of available/frozen

The authority management related operations please refer to [ChainGovernance](../security_control/chain_governance.md).

```eval_rst
.. important::
   The operation of freezing a account will not modify the original account content, and will only be recorded through a field.
```

The state transition moments are shown below:

|          | available | frozen  |
| -------- | --------- | ------- |
| freeze   | Success   | Fail    |
| unfreeze | Fail      | Success |

## Implementation

### Record of Account status

- A existing field `frozen` is used to record whether the account has been frozen. The default of this field is false, indicating that it is available. When frozen, the value is true.

**Note:**

1. False will be returned when querying the field for the account table with no field `frozen`;

### Judgment of account status

In the Executive module, the values of frozen fields are obtained according to the address of an account, then the account can deploy and execute transaction smoothly, or an exception is thrown to indicate that the account has been frozen after judgment.

```eval_rst
.. important::
   Account management related operations can only be performed on 2.5 and above.
```
