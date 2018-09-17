# 系统合约部署


```eval_rst
.. admonition:: 文档目标
  
   web3sdk提供了系统合约部署工具，本文主要介绍如何使用web3sdk部署系统合约 

.. important::

   - 部署系统合约前，请参考 `web3sdk入门 <https://fisco-bcos-test.readthedocs.io/zh/latest/docs/web3sdk/quick-start/index.html>`_ 搭建并运行web3sdk
   - 部署系统合约前，请保证web3sdk连接的FISCO-BCOS节点运行正常(可以正常出块)
   - 若FISCO-BCOS节点已部署系统合约，没有必要再用web3sdk再次部署系统合约 

.. admonition:: web3sdk部署系统合约详细步骤
   
   使用 ``./web3sdk InitSystemContract`` 命令部署系统合约：

   .. code-block:: bash
      
      #-----进入dist目录(设web3sdk存放于/mydata/目录)
      $ cd /mydata/web3sdk/dist/bin
      
      #-----执行部署工具InitSystemContract部署系统合约:
      $ ./web3sdk InitSystemContract
      ===================================================================
      Start deployment...
      ===================================================================
      systemProxy getContractAddress 0xc9ed60a2ebdf22936fdc920133af2a77dd553e13
      caAction getContractAddress 0x014bf33e022f78f7c4bb8dbfe1d22df5168fc9bc
      nodeAction getContractAddress 0x51b25952b01a42e6f84666ed091571c7836eda34
      consensusControlMgr getContractAddress 0xffda2977b8bd529dd187d226ea6600ff3c8fb716
      configAction getContractAddress 0xf6677fa9594c823abf39ac67f1f34866e2843399
      fileInfoManager getContractAddress 0x0f49a17d17f82da2a7d92ecf19268274150eaf5e
      fileServerManager getContractAddress 0xfbe0184fe09a3554103c5a541ba052f7fa45283b
      contractAbiMgr getContractAddress 0x66ec295357750ce442227a6419ada7fdf9207be2
      authorityFilter getContractAddress 0x2fa1ec76f3e31d2c42d21b62960625f326a044e6
      group getContractAddress 0xa172b92c85a98167d96b9fde10792eb2fd4d584c
      transactionFilterChain getContractAddress 0x0b78d9be55f047fb32d6fbc2c79013c0eca5d09d
      Contract Deployment Completed System Agency
      Contract:0xc9ed60a2ebdf22936fdc920133af2a77dd553e13
      -----------------System routing table----------------------
      0)TransactionFilterChain=>0x0b78d9be55f047fb32d6fbc2c79013c0eca5d09d,false,35
            AuthorityFilter=>1.0,0x2fa1ec76f3e31d2c42d21b62960625f326a044e6
      1)ConfigAction=>0xf6677fa9594c823abf39ac67f1f34866e2843399,false,36
      2)NodeAction=>0x51b25952b01a42e6f84666ed091571c7836eda34,false,37
      3)ConsensusControlMgr=>0xffda2977b8bd529dd187d226ea6600ff3c8fb716,false,38
      4)CAAction=>0x014bf33e022f78f7c4bb8dbfe1d22df5168fc9bc,false,39
      5)ContractAbiMgr=>0x66ec295357750ce442227a6419ada7fdf9207be2,false,40
      6)FileInfoManager=>0x0f49a17d17f82da2a7d92ecf19268274150eaf5e,false,41
      7)FileServerManager=>0xfbe0184fe09a3554103c5a541ba052f7fa45283b,false,42

   部署完毕的系统合约地址是 ``0xc9ed60a2ebdf22936fdc920133af2a77dd553e13`` 

.. admonition:: 系统合约配置

   部署完系统合约后，若要使用该系统合约，需要如下操作:

   - 将 ``dist/conf/applicationContext.xml`` 的 ``systemProxyAddress`` 字段更新为输出的系统合约地址
   - 将输出的系统合约地址更新到所有FISCO-BCOS节点config.json的 ``systemProxyAddress`` 字段，并重启节点
```



