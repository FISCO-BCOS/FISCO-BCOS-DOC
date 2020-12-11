##############################################################
智能合约
##############################################################

标签： ``智能合约`` ``虚拟机`` 

----

交易的执行是区块链节点上的一个重要的功能。交易的执行，是把交易中的智能合约二进制代码取出来，用执行器（Executor_）执行。共识模块（Consensus_）把交易从交易池(TxPool_)中取出，打包成区块，并调用执行器去执行区块中的交易。在交易的执行过程中，会对区块链的状态（State）进行修改，形成新区块的状态储存下来（Storage）。执行器在这个过程中，类似于一个黑盒，输入是智能合约代码，输出是状态的改变。

随着技术的发展，人们开始关注执行器的性能和易用性。一方面，人们希望智能合约在区块链上能有更快的执行速度，满足大规模交易的需求。另一方面，人们希望能用更熟悉更好用的语言进行开发。进而出现了一些替代传统的执行器（EVM）的方案，如：JIT_、 WASM_甚至JVM。然而，传统的EVM是耦合在节点代码中的。首先要做的，是将执行器的接口抽象出来，兼容各种虚拟机的实现。因此，EVMC被设计出来。


EVMC (Ethereum Client-VM Connector API)，是以太坊抽象出来的执行器的接口，旨在能够对接各种类型的执行器。FISCO BCOS目前采用了以太坊的智能合约语言Solidity，因此也沿用了以太坊对执行器接口的抽象。

.. image:: ../../../images/evm/evmc_frame.png

在节点上，共识模块会调用EVMC，将打包好的交易交由执行器执行。执行器执行时，对状态进行的读写，会通过EVMC的回调反过来操作节点上的状态数据。

经过EVMC一层的抽象，FISCO BCOS能够对接今后出现的更高效、易用性更强的执行器。目前，FISCO BCOS采用的是传统的EVM根据EVMC抽象出来的执行器---Interpreter。因此能够支持基于Solidity语言的智能合约。目前其他类型的执行器发展尚未成熟，后续将持续跟进。


.. toctree::
   :maxdepth: 1
   
   evm.md
   precompiled.md
   gas.md

.. _Executor: ./evm.html

.. _Consensus: ../consensus

.. _TxPool: ../architecture/transaction_stream.html

.. _JIT: https://github.com/ethereum/evmjit

.. _WASM: https://webassembly.org/