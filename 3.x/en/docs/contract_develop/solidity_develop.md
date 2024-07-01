# 1. Solidity Contract Development
Tags: "Solidity" "Contract Development"

----
FISCO BCOS supports implementing smart contracts in several ways

* [Solidity](https://solidity.readthedocs.io/en/latest/)The contract programming language used in the Ethereum ecosystem, FISCO BCOS expands a series of functions for the alliance chain, and is the most commonly used way to develop smart contracts on FISCO BCOS.。
* [Pre-Compiled Contract](./c++_contract/add_precompiled_impl.md): Built-in customized smart contracts directly inside blockchain nodes, using c++Language implementation, can directly call the node internal various interfaces, for complex scenarios, the use of high barriers to entry。
* [WBC-Liquid](./Liquid_develop.md)The Rust-based smart contract programming language developed by the micro-blockchain, with the help of Rust language features, can achieve more powerful programming functions than the Solidity language.。



Contract Development of Solidity Language on FISCO BCOS

- 基础的
  - The syntax is the same as that of Ethereum. For more information, see [Solidity official document](https://solidity.readthedocs.io/en/latest/)carry on learning。

- Alliance Chain Expansion Oriented
  - Write Solidity code to call the [built-in contract interface](./c++_contract/use_precompiled.html#fisco-bcos-3-x)CRUD contract, KVTable contract, group ring signature, system management, etc.


```eval_rst
.. important::
    Solidity has some built-in cryptographic algorithms (such as keccak256 / sha3, etc.). When the deployed FISCO BCOS block is of the state secret type, the corresponding state secret algorithm (such as sm3, etc.)
```
