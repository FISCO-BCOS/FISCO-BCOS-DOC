##############################################################
Build Blockchain Network
##############################################################

.. admonition:: Setup and deploy blockchain

    - `Getting Executables <../manual/get_executable.html>`_
        + Download binary, use docker images and complie source code.
    - `Chain building script <../manual/build_chain.html>`_
        + Options and node directory
    - `Certificate description <../manual/certificates.html>`_
        + Certificate format, role and generating process.
    - `Configuration files and configuration items <../manual/configuration.html>`_
        + All configure files' options
    - `Deploy Multi-Group Blockchain System <../manual/group_use_cases.html>`_
        + The guide of deploying Multi-Group Blockchain
    - `Distributed storage <../manual/distributed_storage.html>`_
        + The guide of using distributed storage feature


.. admonition:: Use Blockchain

    - `Console <../console/console.html>`_
        + Configuration and options
    - `Manage blockchain accounts <../manual/account.html>`_
        + Account generation and using guide.
    - `SDK <../sdk/index.html>`_
        + The SDK to call smart from outside
    - `AMOP <../manual/amop_protocol.html>`_
        + Send messages between SDKs


.. admonition:: Write samrt contracts

    - `Smart contract development <../manual/smart_contract.html>`_
        + Solidity smart contract and precompiled contract
    - `Parallel contract <../manual/transaction_parallel.html>`_
        + The guide of writing parallel contract


.. admonition:: Management and Security

    - `Group members management <../manual/node_management.html>`_
        + Add/Remove members(nodes) of group
    - `Permission control <../manual/permission_control.html>`_
        + Access control among accounts.
    - `CA blacklist <../manual/certificate_list.html>`_
        + Deny connection from certain node.
    - `Storage security <../manual/storage_security.html>`_
        + Encrypt data during writing into disk
    - `Privacy protection <../manual/privacy.html>`_
        + Integrate homomorphic encryption and group/ring signature algorithms in precompiled contracts



.. admonition:: Others

    - `OSCCA-approved cryptography <../manual/guomi_crypto.html>`_
        + OSCCA-approved cryptography node and SDK

.. important::

    * Important features
        + `Deploy Multi-Group Blockchain System <../manual/group_use_cases.html>`_
        + `Parallel contract <../manual/transaction_parallel.html>`_
        + `Distributed storage <../manual/distributed_storage.html>`_


.. toctree::
   :hidden:


   ../manual/hardware_requirements.md
   ../manual/get_executable.md
   ../manual/certificates.md
   ../manual/cfca.md
   ../manual/certificate_list.md
   ../manual/configuration.md
   ../manual/guomi_crypto.md
   ../manual/group_use_cases.md
   ../manual/distributed_storage.md
   ../manual/sdk_allowlist.md
   ../manual/storage_security.md
   ../manual/node_management.md
   ../manual/permission_control.md
   ../manual/consensus_recover.md
   ../manual/cdn.md
   ../tutorial/stress_testing.md
   ../manual/log_description.md