##############################################################
Build Blockchain Network
##############################################################

.. admonition:: Setup and deploy blockchain

    - `Getting Executables <./get_executable.html>`_
        + Download binary, use docker images and complie source code.
    - `Chain building script <../tools/build_chain.html>`_
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
    - `Manage blockchain accounts <../app_dev/account.html>`_
        + Account generation and using guide.
    - `SDK <../sdk/index.html>`_
        + The SDK to call smart from outside
    - `AMOP <../app_dev/amop_protocol.html>`_
        + Send messages between SDKs


.. admonition:: Write samrt contracts

    - `Smart contract development <../app_dev/smart_contract.html>`_
        + Solidity smart contract and precompiled contract
    - `Parallel contract <../app_dev/transaction_parallel.html>`_
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
    - `Privacy protection <../app_dev/privacy.html>`_
        + Integrate homomorphic encryption and group/ring signature algorithms in precompiled contracts



.. admonition:: Others

    - `OSCCA-approved cryptography <./guomi_crypto.html>`_
        + OSCCA-approved cryptography node and SDK

.. important::

    * Important features
        + `Deploy Multi-Group Blockchain System <./group_use_cases.html>`_
        + `Parallel contract <../app_dev/transaction_parallel.html>`_
        + `Distributed storage <./distributed_storage.html>`_
        

.. toctree::
   :hidden:

   
   hardware_requirements.md
   get_executable.md
   certificates.md
   cfca.md
   certificate_list.md
   configuration.md
   guomi_crypto.md
   group_use_cases.md
   distributed_storage.md
   sdk_allowlist.md
   storage_security.md
   node_management.md
   permission_control.md
   consensus_recover.md
   cdn.md
   caliper.md
   log_description.md
   
   

   
