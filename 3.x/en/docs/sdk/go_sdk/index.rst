##############################################################
4. Go SDK
##############################################################

Tags: "go-sdk" "Go SDK"

----

`Go SDK <https://github.com/FISCO-BCOS/go-sdk>'_ provides access to' FISCO BCOS<https://github.com/FISCO-BCOS/FISCO-BCOS>'_ The Go API of the node, which supports node status query, deployment, and contract invocation. Based on the Go SDK, you can quickly develop blockchain applications. Currently, FISCO BCOS v2.2.0 is supported+with v3.3.0+

.. admonition:: **Main characteristics**

    - Provides calls to FISCO BCOS 'JSON-RPC<../../develop/api.html>Go API for '_
    -Provide contract compilation, compile Solidity contract files into abi and bin files, and then convert them into Go contract files
    - Provide GO API for deploying and calling go contract files
    - Provides Go APIs for calling precompiled contracts, which are fully supported in v2 and partially supported in v3
    - Support to establish TLS and Guodi TLS connection with nodes
    - Provide CLI (Command-Line Interface) tool for users to interact with the blockchain conveniently and quickly from the command line

Install and configure the environment. For application development using the Go SDK, see 'gitHub<https://github.com/FISCO-BCOS/go-sdk>'_ LINK

.. toctree::
   :hidden:
   :maxdepth: 3

   env_conf.md
   api.md
   console.md
   contractExamples.md
   amopExamples.md
   event_sub.md