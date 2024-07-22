# feature and bugfix list

本文档归档了FISCO BCOS 3.0+ 版本所有的feature功能开关和bugfix列表，随版本更新持续更新。

## 1. feature功能开关

|                    | Feature 名                 | 默认状态 | 说明                                                           |
|--------------------|----------------------------|----------|----------------------------------------------------------------|
| 资产管理           | feature_balance            | 关：0    | 默认关闭                                                       |
| 资产操作预编译合约 | feature_balance_precompile | 关：0    | 默认关闭                                                       |
| 计费模式           | feature_policy1            | 关：0    | 默认关闭                                                       |
| 块内分片           | feature_sharding           | 关：0    | 默认关闭，仅在从3.3、3.4升级至当前版本时，feature_sharding打开 |
| 同态加密           | feature_paillier           | 关：0    | 默认关闭                                                       |
| rpbft共识          | feature_rpbft              | 关：0    | 默认关闭                                                       |
| dmc切换至串行      | feature_dmc2serial         | 关：0    | 默认关闭                                                       |

## 2. bugfix列表

|                                                                                 | bugfix 名                                           | 默认状态 | 说明                         |
|---------------------------------------------------------------------------------|-----------------------------------------------------|----------|------------------------------|
| 修复串行模式下回滚时不回滚子合约的bug                                           | bugfix_revert                                       | 开启：1  | 3.2.3、3.5.0 默认开启        |
| 修复stateStorage_hash计算错误的问题                                             | bugfix_statestorage_hash                            | 开启：1  | 3.2.4、3.5.0、3.6.0 默认开启 |
| 适配以太坊的调用行为                                                            | bugfix_evm_create2_delegatecall_staticcall_codecopy | 开启：1  | 3.2.4、3.6.0 默认开启        |
| 修复抛出事件的顺序问题                                                          | bugfix_event_log_order                              | 开启：1  | 3.2.7、 3.6.0 默认开启       |
| 修复call没有地址返回的问题                                                      | bugfix_call_noaddr_return                           | 开启：1  | 3.2.7、3.6.0 默认开启        |
| 修复预编译合约算哈希与以太坊不同的问题                                          | bugfix_precompiled_codehash                         | 开启：1  | 3.2.7、3.6.0 默认开启        |
| 修复dmc模式下回滚时不回滚子合约的bug                                            | bugfix_dmc_revert                                   | 开启：1  | 3.2.7、3.6.0 默认开启        |
| 修复keyPage哈希不一致的兼容问                                                   | bugfix_keypage_system_entry_hash                    | 开启：1  | 3.6.1 默认开启               |
| InternalCreate复用现有部署合约逻辑                                              | bugfix_internal_create_redundant_storage            | 开启：1  | 3.6.1 默认开启               |
| 修复开启合约部署权限后资产转移受限问题                                          | bugfix_internal_create_permission_denied            | 开启：1  | 3.7.0 默认开启               |
| 修复块内分片合约调用合约的问题                                                  | bugfix_sharding_call_in_child_executive             | 开启：1  | 3.7.0 默认开启               |
| 修复已部署空abi，部署相同的合约无abi的问题                                      | bugfix_empty_abi_reset                              | 开启：1  | 3.7.0 默认开启               |
| 修复无法通过eip55类型的合约地址调用合约的问题                                   | bugfix_eip55_addr                                   | 开启：1  | 3.7.0 默认开启               |
| 解决对EOA账户getCode的返回值问题                                                | bugfix_eoa_as_contract                              | 开启：1  | 3.8.0默认开启                |
| 解决DMC模式下部署合约时gas消耗与串行模式不同的问题                              | bugfix_dmc_deploy_gas_used                          | 开启：1  | 3.8.0默认开启                |
| 解决EVM执行status_code非0和revert时未扣除gas的问题                              | bugfix_evm_exception_gas_used                       | 开启：1  | 3.8.0默认开启                |
| 解决StateStorage和KeyPageStorage的setRow接口写入未修改Entry时不计算DBHash的问题 | bugfix_set_row_with_dirty_flag                      | 开启：1  | 3.8.0默认开启                |
| 修复Solidity合约使用staticcall的opcode时当出现错误时没有正确返回的问题          | bugfix_staticcall_noaddr_return                     | 开启：1  | 3.9.0默认开启                |
| 修复在Solidity合约中receive函数没有正确处理的问题                               | bugfix_support_transfer_receive_fallback            | 开启：1  | 3.9.0默认开启                |
| 修复在合约中获取EOA code时可能出现错误结果的问题                                | bugfix_eoa_match_failed                             | 开启：1  | 3.9.0默认开启                |
