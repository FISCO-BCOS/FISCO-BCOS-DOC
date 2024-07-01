# feature and bugfix list

This document documents FISCO BCOS 3.0+ List of feature feature switches and bugfixes of all versions, continuously updated with version updates。

## 1.feature function switch

|           | Feature Name| Default State| Description|
|-----------|----------------------------|------|--------------------------------------------|
| Asset Management| feature_balance            | Off: 0| Default Off|
| Pre-compiled contracts for asset operations| feature_balance_precompile | Off: 0| Default Off|
| Billing Mode| feature_policy1            | Off: 0| Default Off|
| intra-block fragmentation| feature_sharding           | Off: 0| By default, feature _ sharding is turned on only when upgrading from 3.3 or 3.4 to the current version|
| homomorphic encryption| feature_paillier           | Off: 0| Default Off|
| rpbft consensus| feature_rpbft              | Off: 0| Default Off|
| dmc switch to serial| feature_dmc2serial         | Off: 0| Default Off|

## 2.bugfix list

|                            | bugfix Name| Default State| Description|
|----------------------------|-----------------------------------------------------|------|------------------------|
| Fix the bug that the roller contract does not roll back when rolling back in serial mode.| bugfix_revert                                       | Open: 1| 3.2.3 and 3.5.0 are turned on by default|
| Fix the problem of incorrect calculation of stateStorage _ hash| bugfix_statestorage_hash                            | Open: 1| 3.2.4, 3.5.0, 3.6.0 On by Default|
| Adapt the call behavior of Ethereum| bugfix_evm_create2_delegatecall_staticcall_codecopy | Open: 1| 3.2.4 and 3.6.0 are turned on by default|
| Fix an issue with the order of thrown events| bugfix_event_log_order                              | Open: 1| 3.2.7 and 3.6.0 are turned on by default|
| Fix the problem that call does not return an address.| bugfix_call_noaddr_return                           | Open: 1| 3.2.7 and 3.6.0 are turned on by default|
| Fix the problem that pre-compiled contract hash is different from Ethereum| bugfix_precompiled_codehash                         | Open: 1| 3.2.7 and 3.6.0 are turned on by default|
| Fix the bug that the roller contract does not return when rolling back in dmc mode.| bugfix_dmc_revert                                   | Open: 1| 3.2.7 and 3.6.0 are turned on by default|
| Fix the compatibility question of inconsistent keyPage hash.| bugfix_keypage_system_entry_hash                    | Open: 1| 3.6.1 On by default|
| InternalCreate Reuse Existing Deployment Contract Logic| bugfix_internal_create_redundant_storage            | Open: 1| 3.6.1 On by default|
| Fix the problem of restricted asset transfer after opening contract deployment permission| bugfix_internal_create_permission_denied            | Open: 1| 3.7.0 On by Default|
| Fix the problem of the shard contract calling contract within the block.| bugfix_sharding_call_in_child_executive             | Open: 1| 3.7.0 On by Default|
| Fix the problem of deploying empty abi and deploying the same contract without abi.| bugfix_empty_abi_reset                              | Open: 1| 3.7.0 On by Default|
| Fix the problem that the contract cannot be called through the contract address of eip55 type.| bugfix_eip55_addr                                   | Open: 1| 3.7.0 On by Default|
| Solve the return value problem to the EOA account getCode| bugfix_eoa_as_contract | Open: 1| 3.8.0 On by Default|
| Solve the problem that gas consumption is different from serial mode when deploying contracts in DMC mode| bugfix_dmc_deploy_gas_used | Open: 1| 3.8.0 On by Default|
| Solve the problem that gas is not deducted when EVM executes status _ code other than 0 and revert| bugfix_evm_exception_gas_used | Open: 1| 3.8.0 On by Default|
| Solve the problem that the setRow interface of StateStorage and KeyPageStorage does not calculate DBHash when writing unmodified Entry| bugfix_set_row_with_dirty_flag | Open: 1| 3.8.0 On by Default|