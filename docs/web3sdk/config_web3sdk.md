# 配置文件

```eval_rst

.. important::

   - 配置web3sdk前，请确保参考 `web3sdk编译文档 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/web3sdk/compile.html>`_ 成功编译web3sdk
   - 配置web3sdk前，请先生成客户端证书，并将证书拷贝到web3sdk/dist/conf目录：
    1. 手动搭链：客户端证书生成参考 `FISCO-BCOS快速入门 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/usage/tools.html#sdk>`_ **基础配置中的SDK证书配置** ;
     
    2. 由 `FISCO-BCOS物料包搭建的链 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tools/index.html>`_ 搭建的FISCO-BCOS链：客户端证书生成参考 `SDK证书生成 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tools/web3sdk.html#web3sdk>`_ 

    3. 国密版FISCO-BCOS链SDK证书生成参考 `SDK证书生成 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/guomi/gen_cert.html>`_ 
```

## 配置java客户端相关信息 

```eval_rst
.. admonition:: web3sdk客户端配置

   打开web3sdk/dist/conf目录的applicationContext.xml文件,部分信息可以先用默认的, **先关注这些配置项** ：

   .. image:: imgs/javaconfig.png
      :align: center
   

   
   找到 `web3sdk/dist/conf/applicationContext.xml文件的【区块链节点信息配置】 <https://github.com/FISCO-BCOS/web3sdk/blob/master/src/test/resources/applicationContext.xml#L41>`_ 一节，配置keystore密码
     .. code-block:: xml

         <property name="keystorePassWord" value="【生成client.keystore时对应的keystore密码】" />
         <property name="clientCertPassWord" value="【生成client.keystore时对应的证书密码】" />
    
   **配置节点信息，请务必注意：ip、端口，和连接的FISCO-BCOS节点必须一致, 节点id可以是任意非空字符串** 
     .. code-block:: xml

        <property name="connectionsStr">
            <list>
                <!--节点配置：【节点id，可以是任意字符串】@【IP】:【channel port端口】-->
                <value>node1@127.0.0.1:8891</value>
            </list>
        </property>

   
   其他配置
    调用SystemProxy|AuthorityFilter等系统合约工具时需配置系统合约地址SystemProxyAddress和GOD账户信息；
    GOD账号默认为 ``0x776bd5cf9a88e9437dc783d6414bccc603015cf0`` ,GOD账号私钥默认为 ``bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd`` 

     .. code-block:: xml
        
        <!-- 系统合约地址配置，使用系统合约工具时需配置-->
        <bean id="toolConf" class="org.bcos.contract.tools.ToolConf">
            <!--系统合约地址: 【系统合约代理地址,对应节点config.json里的systemproxyaddress】-->
            <property name="systemProxyAddress" value="0x0" />
            <!--GOD账户的私钥: -->
            <!--非国密版FISCO-BCOS获取GOD账户和账户私钥: 【参考https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/web3sdk/config_web3sdk.html】-->
            <!--国密版FISCO-BCOS获取GOD账户和账户私钥：【参考https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/guomi/config_guomi.html#sdk】-->
            <property name="privKey" value="bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd" />
            <!--GOD账户-->
            <property name="account" value="0x776bd5cf9a88e9437dc783d6414bccc603015cf0" />
            <property name="outPutpath" value="./output/" />
        </bean>
        
     日志级别配置：修改log4j.properties文件，将log4j.rootLogger = INFO , C , D , E 中INFO改为 DEBUG 或者ERROR，就可以修改日志级别。
     C、D、E是配置日志信息输出到哪个地方以及输出样式。

.. important::
   -  **节点信息查询方法** ：

    1. 节点id，可以是任意字符串；
    2. channelPort、系统合约地址systemcontractaddress等信息查询: 若节点服务器上，节点数据目录所在路径为~/mydata/node0/，在~/mydata/node0/config.json里可查到;

   - **god账号信息查询：**
    
    1. 手动搭链：
    
     ① **非国密版FISCO-BCOS** ：设源码位于~/mydata/FISCO-BCOS目录，则god账号信息位于~/mydata/FISCO-BCOS/tools/scripts/godInfo.txt文件中; 若搭链过程中使用系统默认god账号，则god账号位于~/mydata/FISCO-BCOS/tools/scripts/god_info/godInfo.txt文件;
     
     ② **国密版FISCO-BCOS** ：设源码位于~/mydata/FISCO-BCOS目录，则god账号位于~/mydata/FISCO-BCOS/tools/scripts/guomi_godInfo.txt文件中; 若搭链过程中使用系统默认god账号，则god账号位于~/mydata/FISCO-BCOS/tools/scripts/god_info/guomiDefaultGod.txt
    
    2. 使用 `FISCO-BCOS物料包 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tools/index.html>`_ 搭链: 可在物料包创世节点dependencies/cert/godInfo.txt路径下获取god账号信息;
   
   - **这里的端口是对应config.json里的channelPort，而不是rpcport或p2pport** 
   - **list段里可以配置多个value，对应多个节点的信息，实现客户端多活通信** 
```

## 测试是否配置成功

```eval_rst
.. admonition:: 测试web3sdk与节点连接是否正常
   
   在web3sdk/dist目录下调用TestOk，非国密版web3sdk输出 ``=====INIT ECDSA KEYPAIR From private key===`` 等提示，说明web3sdk与节点连接正常，否则请参考 `faq【dist/bin/web3sdk运行出错】 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/web3sdk/faq.html>`_ .
   
   具体测试过程如下：
    .. code-block:: bash

       # 进入web3sdk目录(设源码位于~/mydata/web3sdk/dist中)
       $ cd ~/mydata/web3sdk/dist
       
       # 调用测试合约TestOk
       $ java -cp 'conf/:apps/*:lib/*' org.bcos.channel.test.TestOk
       ===================================================================
       =====INIT ECDSA KEYPAIR From private key===
       ============to balance:4
       ============to balance:8

   (Ok合约详细代码可参考 `Ok.sol  <https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tool/Ok.sol>`_ )
```    

## applicationContext.xml详细介绍

```eval_rst
.. admonition:: applicationContext.xml配置项详细说明

   applicationContext.xml主要包括如下配置选项：

+----------------------+----------------------------------------------------------------------------------------------+
| **encryptType**      |  配置国密算法开启/关闭开关(默认为0)                                                          |
|                      |  - 0: 不使用国密算法发交易                                                                   |
|                      |  - 1: 使用国密算法发交易                                                                     |
+----------------------+----------------------------------------------------------------------------------------------+
|**systemProxyAddress**|  系统代理合约地址，对应节点config.json中的systemproxyaddress值                               |
+----------------------+----------------------------------------------------------------------------------------------+
| **privKey**          |  部署合约的私钥,可通过 ``fisco-bcos --newaccount  accountInfo.txt`` 产生普通账户，           |
|                      |  并用普通账户私钥部署合约; 部署权限控制合约时，必须使用GOD账号私钥                           |
+----------------------+----------------------------------------------------------------------------------------------+
| **account**          |  部署权限控制合约时,必须配置成GOD账号;                                                       |
+----------------------+----------------------------------------------------------------------------------------------+
|**ChannelConnections**|- caCertPath: ca.crt证书路径，默认为classpath:ca.crt                                          |
|                      |- clientKeystorePath: client.keystore证书路径，                                               |
|                      |  默认为classpath:client.keystore                                                             |  
|                      |- keystorePassWord: 生成client.keystore时对应的密码                                           | 
|                      |- clientCertPassWord: 生成client.keystore时对应的密码                                         |
|                      |- nodeid:SDK连接的FISCO BCOS节点ID，从节点data/node.nodeid文件获取                            |
|                      |- ip: SDK连接的FISCO BCOS节点外网ip                                                           |
|                      |- channelPort: SDK连接的FISCO BCOS节点channelPort,                                            |
|                      |  对应config.json的channelPort                                                                | 
+----------------------+----------------------------------------------------------------------------------------------+
```

