# Response返回码与错误信息汇总

标签：``TransactionResponse`` ``响应码`` ``错误信息`` ``returnCode`` ``returnMessages``

----

**响应码**与**错误信息**对应表

| returnCode | returnMessages                                                                     |
|------------|------------------------------------------------------------------------------------|
| 0          | Success                                                                            |
| -53006     | Invalid file type                                                                  |
| -53005     | Invalid path                                                                       |
| -53003     | Make directory failed                                                              |
| -53002     | File already existed                                                               |
| -53001     | File not exist                                                                     |
| -51507     | Key not exist in table, use insert method                                          |
| -51506     | Don't insert the key already existed                                               |
| -51505     | Add specific table key EQ syntax in condition                                      |
| -51504     | Add specific table key in entry                                                    |
| -51503     | Don't update the table key                                                         |
| -51502     | Undefined function of Condition Precompiled                                        |
| -51501     | Parse the input of Condition Precompiled failed                                    |
| -51500     | Parse the inpput of the Entriy Precompiled failed                                  |
| -51300     | Invalid configuration value                                                        |
| -51202     | The version string or address is error                                             |
| -51103     | The node is not exist                                                              |
| -51102     | The weight is invalid                                                              |
| -51101     | The last sealer cannot be removed                                                  |
| -51100     | Invalid node ID                                                                    |
| -51004     | Auth map decode error                                                              |
| -51003     | Error auth type input                                                              |
| -51002     | The contract method auth type not set, please set method auth type first           |
| -51001     | The contract method auth not exist                                                 |
| -51000     | The contract agent not exist                                                       |
| -50105     | Open table error                                                                   |
| -50104     | Create table error                                                                 |
| -50103     | Table set row error                                                                |
| -50102     | Invalid address format                                                             |
| -50101     | Undefined function                                                                 |
| -50100     | Open table failed, please check the existence of the table                         |
| -50008     | Invalid table name or field name                                                   |
| -50007     | The table contains duplicated field                                                |
| -50006     | The field value exceeds the limit 1024                                             |
| -50005     | The value exceeds the limit, key max length is 255, field value max length is 1024 |
| -50004     | The length of all the fields name exceeds the limit 1024                           |
| -50003     | The table field name exceeds the limit 64                                          |
| -50002     | The table name length exceeds the limit 50                                         |
| -50001     | The table already exist                                                            |
