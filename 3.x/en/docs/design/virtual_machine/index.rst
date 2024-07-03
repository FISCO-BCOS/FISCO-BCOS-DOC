##############################################################
8. Smart Contract Engine
##############################################################

Tags: "smart contract" "virtual machine"

----

The execution of transactions is an important function on a blockchain node。The execution of the transaction is to take out the binary code of the smart contract in the transaction and execute it with the executor (Executor)。The Consensus module (Consensus) takes transactions out of the transaction pool, packages them into blocks, and calls the executor to execute the transactions in the blocks。During the execution of the transaction, the state of the blockchain (State) is modified to form a new block state stored (Storage)。Executor in this process, similar to a black box, the input is the smart contract code, the output is the change of state。

With the development of technology, people began to pay attention to the performance and ease of use of actuators。On the one hand, people hope that smart contracts can be executed faster on the blockchain to meet the needs of large-scale transactions。On the other hand, people want to develop in a more familiar and better language。And then there are some alternatives to the traditional executor (EVM), such as: WASM, the traditional EVM is coupled in the node code。The first thing to do is to abstract the interface of the actuator and be compatible with the implementation of various virtual machines。Therefore, EVMC was designed。

EVMC (Ethereum Client-VM Connector API), Is the interface of the actuator abstracted by Ethereum, designed to be able to interface with various types of actuators。FISCO BCOS currently uses Ethereum's smart contract language, Solidity, and therefore follows Ethereum's abstraction of the executor interface。

.. image:: ../../../images/evm/evmc_frame.png

On the node, the consensus module hands over the packaged blocks to the executor for execution。When the virtual machine is executed, the reading and writing of the state will in turn operate the state data on the node through the EVMC callback。

After a layer of EVMC abstraction, FISCO BCOS can interface with more efficient and easy-to-use actuators that will emerge in the future。Currently, FISCO BCOS uses evone to execute the solidity contract and wasmtime to execute the wasm contract。

.. toctree::
   :maxdepth: 1

   evm.md
   precompiled.md
   wasm.md
   gas.md

.. _Executor: ./evm.html

.. _Consensus: ../consensus

.. _JIT: https://github.com/ethereum/evmjit

.. _WASM: https://webassembly.org/