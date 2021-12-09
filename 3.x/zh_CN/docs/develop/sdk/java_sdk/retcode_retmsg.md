# TransactionResponse 返回码与信息汇总

标签：``TransactionResponse`` ``响应码`` ``错误信息`` ``returnCode`` ``returnMessages``

----

**响应码**与**错误信息**对应表

| returnCode | returnMessages |
| ------------------ | ------------------ |
| 0  | Success |
| -50000  | Permission denied |
| -53005  | Invalid path |
| -53004  | Deploy WASM contract failed |
| -53003  | Make directory failed |
| -53002  | File already existed |
| -52012  | The current value is expected |
| -52010  | The account is already available |
| -52009  | Invalid account address |
| -52008  | Account not exist, you can create a blockchain account by using this account to deploy contracts on the chain |
| -52007  | The operator not exist |
| -52006  | The operator already exist |
| -52005  | The committee member cannot be operator |
| -52004  | The operator cannot be committee member |
| -52003  | Invalid threshold, threshold should from 0 to 99 |
| -52002  | Invalid request for permission deny |
| -52001  | The committee member not exist |
| -52000  | The committee member already exist |
| -51907 | The permission of the last contract status manager can't be revoked |
| -51906  | The contract status manager doesn't exist |
| -51905  | Have no permission to access the contract table |
| -51904  | The queried contract address doesn't exist |
| -51903  | The contract address is invalid |
| -51902  | The contract has been granted authorization with same user |
| -51901  | The contract is available |
| -51900  | The contract has been frozen |
| -51800  | Verify ring signature failed |
| -51700  | Verify group signature failed |
| -51600  | Execute PaillierAdd failed |
| -51507  | Key not exist in table, use insert method |
| -51506  | Don't insert the key already existed |
| -51505  | Add specific table key EQ syntax in condition |
| -51504  | Add specific table key in entry |
| -51503  | Don't update the table key |
| -51502  | Undefined function of Condition Precompiled |
| -51501  | Parse the input of Condition Precompiled failed |
| -51500  | Parse the inpput of the Entriy Precompiled failed |
| -51300  | Invalid configuration value |
| -51201  | The version string length exceeds the maximum limit |
| -51200  | The contract name and version already exist |
| -51103  | The node is not exist |
| -51102  | The weight is invalid |
| -51101  | The last sealer cannot be removed |
| -51100  | Invalid node ID |
| -51004  | Auth map decode error |
| -51003  | Error auth type input |
| -51002  | The contract method auth type not set, please set method auth type first |
| -51001  | The contract method auth not exist |
| -51000  | The contract admin not exist |
| -50105  | Open table error |
| -50104  | Create table error |
| -50103  | Table set row error |
| -50102  | Invalid address format |
| -50101  | Undefined function |
| -50100  | Open table failed, please check the existence of the table |
| -50001  | The table already exist |
| -50002  | The table name length exceeds the limit 48 |
| -50003  | The table field name exceeds the limit 64 |
| -50004  | The length of all the fields name exceeds the limit 1024 |
| -50005  | The value exceeds the limit, key max length is 255, field value max length is 1024 |
| -50006  | The field value exceeds the limit 1024 |
| -50007  | The table contains duplicated field |
| -50008  | Invalid table name or field name |
