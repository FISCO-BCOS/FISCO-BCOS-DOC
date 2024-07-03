##############################################################
6. Node.js SDK
##############################################################

Tags: "Node.JS SDK"

----
`Node.js SDK <https://github.com/FISCO-BCOS/nodejs-sdk>'_ provides access to' FISCO BCOS<https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master-2.0>The Node.js API of the _ node, which supports node status query, deployment, and contract invocation. Based on the Node.js SDK, you can quickly develop blockchain applications. Currently, 'FISCO BCOS 2.0' is supported+  <../../../>`_ 


.. admonition:: **注意**
    :class: red

    **The Node.js SDK is currently only in the personal developer experience stage. To develop enterprise applications, use the** `Java SDK <../java_sdk/index.html>`_
    Node.js SDK does not currently support the SSL communication protocol
    Node.js SDK temporarily supports versions 2.0.0 and above, and versions 3.0.0 and above are being adapted



.. admonition:: **Main characteristics**

    - Provides calls to FISCO BCOS 'JSON-RPC<../../develop/api.html>Node.js API for '_
    - Provides Node.js API for deploying and invoking Solidity contracts (Solidity 0.4.x and Solidity 0.5.x support)
    - Provides Node.js API for calling precompiled contracts
    - Use the 'Channel protocol<../../design/protocol_description.html#channelmessage>'_ Communicate with FISCO BCOS nodes, two-way authentication is more secure
    - Provide CLI (Command-Line Interface) tool for users to interact with the blockchain conveniently and quickly from the command line

Install and configure the environment. For application development using Nodejs SDK, see 'gitHub<https://github.com/FISCO-BCOS/nodejs-sdk>`_


.. toctree::
   :hidden:

   install.md
   configuration.md
   api.md