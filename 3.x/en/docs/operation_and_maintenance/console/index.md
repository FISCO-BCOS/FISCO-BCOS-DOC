# 3. Console Tools

Tags: "console" "console" "command line interactive tools" "

---------

```eval_rst
.. important::
    - "Console" only supports FISCO BCOS 3.0+Version, based on 'Java SDK <.. / sdk / java _ sdk / index.html >' _。
    - You can use the command. "/ start.sh--version "View the current console version
```

[CONSOLE](https://github.com/FISCO-BCOS/console)is an important interactive client tool for FISCO BCOS 3.0, which is available through the [Java SDK](../../sdk/java_sdk/index.md)Establish a connection with a blockchain node to implement read and write access requests for blockchain node data。The console has a wealth of commands, including querying blockchain status, managing blockchain nodes, deploying and invoking contracts, and more.。In addition, the console provides a contract compilation tool that allows users to quickly and easily integrate Solidity and webankblockchain-The compiled WASM file of the liquid contract file is converted into a Java contract file.。

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check < https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

The command line interactive console is a tool for developers to query and manage nodes.。

The console has a wealth of commands, including querying blockchain status, managing blockchain nodes, deploying and invoking contracts, and more.。

```eval_rst
.. important::
    "> = v3.x" console must be used to access FISCO BCOS 3.x blockchain. You cannot use "2.x" or "1.x" console。
```

Use manual:

```eval_rst
.. toctree::
   :maxdepth: 2

   console_config.md
   console_commands.md
   console_error.md
```
