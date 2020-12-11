##############################################################
Manual (Revision in progress)
##############################################################

This chapter provides an operation tutorial of FISCO BCOS platform to introduce its functions and operation methods.

.. toctree::
   :hidden:
   :maxdepth: 1

   get_executable.md
   hardware_requirements.md
   build_chain.md
   certificates.md
   configuration.md
   group_use_cases.md
   console.md
   account.md
   cfca.md
   smart_contract.md
   transaction_parallel.md
   distributed_storage.md
   node_management.md
   permission_control.md
   certificate_list.md
   amop_protocol.md
   storage_security.md
   guomi_crypto.md
   log_description.md
   privacy.md
   cdn.md
   sdk_allowlist.md
   consensus_recover.md


.. admonition:: Setup and deploy blockchain

    - `Getting Executables <./get_executable.html>`_
        + Download binary, use docker images and complie source code.
    - `Chain building script <./build_chain.html>`_
        + Options and node directory
    - `Certificate description <./certificates.html>`_
        + Certificate format, role and generating process.
    - `Configuration files and configuration items <./configuration.html>`_
        + All configure files' options
    - `Deploy Multi-Group Blockchain System <./group_use_cases.html>`_
        + The guide of deploying Multi-Group Blockchain
    - `Distributed storage <./distributed_storage.html>`_
        + The guide of using distributed storage feature


.. admonition:: Use Blockchain

    - `Console <../console/console.html>`_
        + Configuration and options
    - `Manage blockchain accounts <./account.html>`_
        + Account generation and using guide.
    - `SDK <../sdk/index.html>`_
        + The SDK to call smart from outside
    - `AMOP <../app_dev/amop_protocol.html>`_
        + Send messages between SDKs


.. admonition:: Write samrt contracts

    - `Smart contract development <./smart_contract.html>`_
        + Solidity smart contract and precompiled contract
    - `Parallel contract <./transaction_parallel.html>`_
        + The guide of writing parallel contract


.. admonition:: Management and Security

    - `Group members management <./node_management.html>`_
        + Add/Remove members(nodes) of group
    - `Permission control <./permission_control.html>`_
        + Access control among accounts.
    - `CA blacklist <./certificate_list.html>`_
        + Deny connection from certain node.
    - `Storage security <./storage_security.html>`_
        + Encrypt data during writing into disk
    - `Privacy protection <./privacy.html>`_
        + Integrate homomorphic encryption and group/ring signature algorithms in precompiled contracts



.. admonition:: Others

    - `OSCCA-approved cryptography <./guomi_crypto.html>`_
        + OSCCA-approved cryptography node and SDK

.. important::

    * Important features
        + `Deploy Multi-Group Blockchain System <./group_use_cases.html>`_
        + `Parallel contract <./transaction_parallel.html>`_
        + `Distributed storage <./distributed_storage.html>`_
