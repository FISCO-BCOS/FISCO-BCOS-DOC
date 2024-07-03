# 3. Console Tools

Tags: "console" "console" "command line interactive tools" "

---------

```eval_rst
.. important::
    - "Console" only supports FISCO BCOS 3.0+Version, based on 'Java SDK<../sdk/java_sdk/index.html>'_ Implementation。
    - You can view the current console version through the command. "/ start.sh --version"
```

[CONSOLE](https://github.com/FISCO-BCOS/console)is an important interactive client tool for FISCO BCOS 3.0, which is available through the [Java SDK](../../sdk/java_sdk/index.md)Establish a connection with a blockchain node to implement read and write access requests for blockchain node data。The console has a wealth of commands, including querying blockchain status, managing blockchain nodes, deploying and invoking contracts, and more。In addition, the console provides a contract compilation tool that allows users to quickly and easily convert WASM files compiled from Solidity and webankblockchain-liquid contract files to Java contract files。

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

The command line interactive console is a tool for developers to query and manage nodes。

The console has a wealth of commands, including querying blockchain status, managing blockchain nodes, deploying and invoking contracts, and more。

```eval_rst
.. important::
    Access to FISCO BCOS 3.x blockchain is required using ">= v3.x "version of the console, you cannot use" 2.x "or" 1.x "version of the console。
```

Use manual:

```eval_rst
.. toctree::
   :maxdepth: 2

   console_config.md
   console_commands.md
   console_error.md
```
