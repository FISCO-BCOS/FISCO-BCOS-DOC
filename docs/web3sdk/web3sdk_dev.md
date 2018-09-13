# 应用开发指南

## 应用开发步骤 

```eval_rst

.. admonition:: 主要开发步骤

   使用web3sdk开发区块链java应用主要包括如下过程:
    (1) 数据结构和接口设计，编写合约，并将合约代码转换成java代码
    (2) 编写应用程序：调用合约接口完成合约部署和调用逻辑
    (3) 配置java应用
    (4) 运行并测试java应用
   
```

## 编写合约


```eval_rst
.. admonition:: 合约功能设计：实现简单计数器

   实现一个简单的计数器，主要功能包括： 
    - 设置和读取计数器名字、增加计数、读取当前计数功能。
    - 通过receipt log的方式，把修改记录log到区块中，供客户端查询。
    
    (注: receipt log用处很大，是区块链和业务端同步交易处理过程和结果信息的有效渠道)

.. admonition:: 智能合约代码Counter.sol
    
    根据合约功能要求可实现智能合约 `Counter.sol <https://github.com/cyjseagull/fisco-doc-test/blob/master/docs/web3sdk/codes/Counter.sol>`_ ，合约代码如下：

     .. literalinclude:: codes/Counter.sol
        :language: cpp
        :linenos:


.. admonition:: 将合约代码Counter.sol转换为java代码Counter.java

    web3sdk提供了 ``counter_compile.sh`` 脚本将Counter.sol转换成Counter.java:
     .. code-block:: bash
        
        # 进入合约编译脚本所在目录 (设web3sdk位于~/mydata目录)
        $ cd ~/mydata/web3sdk/dist/bin
        # 执行合约编译脚本
        # (com是java代码所属的包，转换后可手动修改)
        $ bash counter_compile.sh org.bcosliteclient

    查看生成的java代码
      .. code-block:: bash

         $ cd ~/mydata/web3sdk/dist/output
         $ tree
         # ...此处省略若干输出...
         ├── Counter.abi  # Counter.sol编译生成的abi文件
         ├── Counter.bin  # Counter.sol编译生成的bin文件
         └── org
            └── bcosliteclient
                ├── Counter.java # Counter.sol转换成的java代码
                ├── Evidence.java
                ├── EvidenceSignersData.java
                └── Ok.java

    output目录生成了合约的.abi, .bin等文件，以及org/bcosliteclient/Counter.java文件。
    
    这个java文件可以复制到客户端开发环境里，后续建立的java工程的对应的包路径下。
    
    若转换成java代码时报错，请参考 `faq【合约转换成java代码出错】 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/faq.html#id2>`_ .
    
    Counter.sol对应的Counter.java代码如下：

     `Counter.java <https://github.com/cyjseagull/fisco-doc-test/blob/master/docs/web3sdk/codes/Counter.java>`_ 

```


## 搭建并配置java应用


```eval_rst
.. admonition:: 下载java应用bcosliteclient

   - FISCO-BCOS提供了示例应用bcosliteclient，该应用在 `CounterClient.java <https://github.com/cyjseagull/fisco-doc-test/blob/master/docs/web3sdk/codes/CounterClient.java>`_ 中提供Counter.sol合约部署和调用功能。应用下载链接如下:

     `bcosliteclient.zip <https://github.com/cyjseagull/fisco-doc-test/raw/master/docs/web3sdk/codes/bcosliteclient.zip>`_ 

.. admonition:: 编译bcosliteclient应用

   .. code-block:: bash
      
      # 解压应用程序(设下载的压缩包bcosliteclient.zip位于~/mydata目录下)
      $ cd ~/mydata && unzip bcosliteclient.zip
      
      # 编译bcosliteclient应用
      $ cd bcosliteclient && gradle build
    
   此时bcosliteclient应用目录如下:
    .. code-block:: bash

       $ tree -L 2
       ├── bcosliteclient # 编译生成目录
       │   ├── bin        # 包含部署和调用Counter.sol合约的可执行脚本
       │   ├── conf       # 配置文件，包含客户端配置文件applicationContext.xml，客户端证书
       │   └── lib        # jar包目录
       ├── build          # 编译生成的临时目录
       │   ├── ...省略若干行...
       ├── build.gradle   
       ├── lib
       │   ├── fastjson-1.2.29.jar
       │   └── web3sdk.jar    # 应用引用的web3sdk jar包
       └── src
          ├── bin             # 包含可执行程序bcosclient
          ├── contract        
          |── org             # 源码目录
          └── resources       # 配置文件目录

.. admonition:: 配置java应用

   参考 `web3sdk配置 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/config_web3sdk.html>`_ 配置java应用，主要配置选项包括：

     .. image:: imgs/javaconfig.png
        :align: center

```

## 部署和调用合约

```eval_rst

.. admonition:: 部署Counter.sol合约

   按照上节操作配置好java应用工程后，可调用相关接口部署和调用Counter.sol合约。
    .. code-block:: bash
       
       # 设bcosliteclient应用位于~/mydata目录
       $ cd ~/mydata/bcosliteclient/bcosliteclient/bin
       # 部署合约
       $ chmod a+x bcosclient && ./bcosclient deploy
       -----> start test !
       init AOMP ChannelEthereumService
       -->Got ethBlockNumber: 42
       Deploy contract :null,address :0x8bc176465048ec377a824b7cf36f3cd7452cd093
       <--start blockNumber = 42,finish blocknmber=43

    由输出结果看出，合约部署成功，合约地址为 ``0x8bc176465048ec377a824b7cf36f3cd7452cd093`` ，且部署成功后，区块链系统区块高度由42增加为43.


.. admonition:: 调用Counter.sol合约
   
   使用 ``bcosliteclient/bcosliteclient/bin`` 目录下的 ``bcosclient`` 脚本调用Counter.sol合约：
    .. code-block:: bash
          
       # 设bcosliteclient应用位于~/mydata目录
       $ cd ~/mydata/bcosliteclient/bcosliteclient/bin
       # 调用合约(合约地址是0x8bc176465048ec377a824b7cf36f3cd7452cd093)
       $ chmod a+x bcosclient && ./bcosclient call_contract 0x8bc176465048ec377a824b7cf36f3cd7452cd093
       -----> start test !
       init AOMP ChannelEthereumService
       -->Got ethBlockNumber: 43
       counter value before transaction:0
       setname-->oldname:[MyCounter from:0,inc:100],newname=[MyCounter from:0,inc:100]
       Current Counter:100
       addcount-->inc:100,before:0,after:100,memo=when tx done,counter inc 100
       <--start blockNumber = 43,finish blocknmber=44

    由输出结果可看出，计数器合约Counter.sol调用成功后，计数器值增加100，区块链系统块高由43增加为44.

```

## gradle文件配置说明

```eval_rst

.. admonition:: gradle配置文件说明
   
   应用SDK的【build.gradle】要通过【compile】和【runtime】添加web3sdk.jar依赖和应用外部依赖库：
    .. image:: imgs/build-gradle.png
       :align: center
    
   一个完整的build.gradle示例如下：

.. literalinclude:: codes/build.gradle
   :language: cpp
   :linenos:

```

## 总结


```eval_rst

.. admonition:: SDK应用开发步骤总结

   根据以上描述，使用web3sdk开发区块链应用主要包括如下过程：
    1. 根据应用功能设计合约数据结构和接口;
    2. 编写智能合约，可先用Nodejs简单验证合约代码逻辑是否正确，验证通过后，将合约代码转换成java代码
    3. 编写java应用，调用合约java接口完成合约部署和调用功能
    4. 配置并编译java应用
    5. 应用功能测试

.. admonition:: SDK应用部署/调用合约主要流程

   参考 `CounterClient.java <https://github.com/cyjseagull/fisco-doc-test/blob/master/docs/web3sdk/codes/CounterClient.java>`_：
    1. 初始化AMOP的ChannelEthereumService
    2. 使用AMOP初始化Web3j
    3. 初始化交易签名密钥对
    4. 初始化交易参数
    5. 调用合约接口部署或调用合约

.. admonition:: 其他说明

   - 从零开发SDK应用时，可使用eclipse新建java工程，编译配置文件build.gradle可参考bcosliteclient.zip中的编译配置; 
   - java应用跟目录的lib目录下要存放FISCO BCOS的web3sdk.jar，web3sdk升级时，直接替换java应用的web3sdk.jar到最新即可。


.. admonition:: 参考资料

    - 智能合约参考文档：http://solidity.readthedocs.io/en/v0.4.24/
    - AMOP: https://fisco-bcos-test.readthedocs.io/zh/latest/docs/features/amop/index.html
    - web3j JSON-RPC: https://github.com/ethereum/wiki/wiki/JSON-RPC
    - FISCO dev团队提供的示例应用:
       (1) 存证Demo： https://github.com/FISCO-BCOS/evidenceSample
       (2) 群/环签名客户端Demo: https://github.com/FISCO-BCOS/sig-service-client
       (3) depotSample服务Demo: https://github.com/FISCO-BCOS/depotSample

```






