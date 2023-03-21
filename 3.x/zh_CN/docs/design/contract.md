##############################################################
智能合约
##############################################################

标签： ``智能合约`` ``虚拟机``

----

交易的执行是区块链节点上的一个重要的功能。交易的执行，是把交易中的智能合约二进制代码取出来，用执行器（Executor_）执行。共识模块（Consensus_）把交易从交易池(TxPool_)中取出，打包成区块，并调用执行器去执行区块中的交易。在交易的执行过程中，会对区块链的状态（State）进行修改，形成新区块的状态储存下来（Storage）。执行器在这个过程中，类似于一个黑盒，输入是智能合约代码，输出是状态的改变。

随着技术的发展，人们开始关注执行器的性能和易用性。一方面，人们希望智能合约在区块链上能有更快的执行速度，满足大规模交易的需求。另一方面，人们希望能用更熟悉更好用的语言进行开发。进而出现了一些替代传统的执行器（EVM）的方案，如：JIT_、 WASM_甚至JVM。

EVMC (Ethereum Client-VM Connector API)，是以太坊抽象出来的执行器的接口，旨在能够对接各种类型的执行器。

![虚拟机](../../images/evm/evmc_frame.png)

在节点上，共识模块会调用EVMC，将打包好的交易交由执行器执行。执行器执行时，对状态进行的读写，会通过EVMC的回调反过来操作节点上的状态数据。

经过EVMC的抽象，FISCO BCOS能够对接今后出现的更高效、易用性更强的执行器。目前，FISCO BCOS支持evm和wasm两种合约引擎，其中evmone作为evm的解释器，支持以Solidity为主的能编译为evm字节码的智能合约语言。wasm引擎使用wasmtime实现，支持能够编译为wasm的语言编写的合约，例如[liquid](https://liquid-doc.readthedocs.io/zh_CN/latest/index.html)，其他语言的工具链正在规划中。

.. toctree::
:maxdepth: 1

virtual_machine/evm.md
virtual_machine/precompiled.md
virtual_machine/gas.md

.. _Executor: ./evm.html

.. _Consensus: ../consensus

.. _TxPool: ../architecture/transaction_stream.html

.. _WASM: https://webassembly.org/
