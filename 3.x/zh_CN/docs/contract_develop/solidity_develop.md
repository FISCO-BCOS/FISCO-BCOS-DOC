# 1. Solidity合约开发
标签： ``Solidity`` ``合约开发`` 

----
FISCO BCOS 支持用以下几种方式实现智能合约

* [Solidity](https://solidity.readthedocs.io/en/latest/)：以太坊生态中采用的合约编程语言，FISCO BCOS为其拓展了面向联盟链的一系列功能，是FISCO BCOS上智能合约开发最常用的方式。
* [预编译合约](./c++_contract/add_precompiled_impl.md)：在区块链节点内部直接内置定制化的智能合约，用c++语言实现，能直接调用节点内部各种接口，面向复杂场景，使用门槛较高。
* [WBC-Liquid](./Liquid_develop.md)：微众区块链开发的基于Rust的智能合约编程语言，借助Rust语言特性，能够实现比Solidity语言更强大的编程功能。



FISCO BCOS上的Solidity语言的合约开发

- 基础的
  - 语法与以太坊相同，可参考[Solidity官方文档](https://solidity.readthedocs.io/en/latest/)进行学习。

- 面向联盟链拓展的
  - 编写Solidity代码，调用已[内置的合约接口](./c++_contract/use_precompiled.html#fisco-bcos-3-x)：CRUD合约、KVTable合约、群环签名、系统管理等


```eval_rst
.. important::
    Solidity中有一些内置的密码学算法（如：keccak256/sha3等），当部署的FISCO BCOS区块为国密类型时，内置使用的是相应的国密算法（如：sm3等）
```
