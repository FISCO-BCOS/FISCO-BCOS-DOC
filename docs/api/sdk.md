# SDK
web3sdk提供访问fisco-bcos节点的java API,项目Fork自以太坊的web3j,并根据Fisco-bcos特性做了相应修改。

## 依赖环境

   Java 8 和 gradle（4.10.1以上）

    如果本地gradle版本过低，又不便于升级，请使用./gradlew替代gradle命令即可


## 引入SDK

   可以通过gradle或maven引入到您的java应用中；

   gradle:

	compile ('org.fisco-bcos:web3sdk:2.0.1')
   maven:

	<dependency>
   		<groupId>org.fisco-bcos</groupId>
   		<artifactId>web3sdk</artifactId>
    	<version>2.0.1</version>
	</dependency>
   由于引入了以太坊的solidity编译器相关jar包
   org.ethereum:solcJ-all:0.4.25 ，请在build.gradle远程仓库里加上
   maven { url 'https://dl.bintray.com/ethereum/maven/' } 下载此包。

   因为是联盟链,需要身份认证，使用sdk需要拷贝节点证书文件ca.crt和keystore.p12到项目的资源目录下，然后配置web3sdk的applicationContext.xml，2.0支持多群组功能，所以sdk需要配置每个群组的节点的信息，groupChannelConnectionsConfig类中需要配置group以及group对应的节点信息。需要sdk向哪些group发消息就配置几个相应的ChannelConnections， 配置service类可以默认指定一个group，当然也可以不配groupId信息，而在程序中动态传入groupId。具体配置如下图所示：
  ![app3.png](http://wiki.weoa.com/uploads/images/gallery/2019-01-Jan/scaled-840-0/DsD8ESB91MfP3pEw-app3.png)![appalicaiont2.xml.png](http://wiki.weoa.com/uploads/images/gallery/2019-01-Jan/scaled-840-0/d4kzhIBF9IdzFyHC-appalicaiont2.xml.png)


### 编译使用

  本项目提供了丰富的测试案例和功能，您也可以直接下载代码，使用基本功能。流程如下：

       #==== 拉取git代码 ====
       $ git clone https://github.com/FISCO-BCOS/web3sdk
       $ cd web3sdk
       $ git checkout release-2.0.1
       #===证书和节点配置 ===
	  1. 在src/test/resources目录下，配置web3sdk的applicationContext.xml，
      如上图所示设置sdk要连接节点channel_listen_ip和channel_listen_port。并设置节点所在群组groupId;

      2. 由于是联盟链,拷贝节点证书文件ca.crt和keystore.p12到web3sdk/src/test/resources目录下；

      3. 然后你可以在web3sdk目录下执行，执行gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.TestOkTransaction --args='1 deploy'， 检查一下是否能向群组1成功部署合约。 如果不成功，在log目录下查看demo_debug.log日志文件。
      成功后执行gradle build -x test；这样可以编译相应web3sdk jar包。

      4. 你也可以直接执行特定测试：
 	  *查询块高*    gradle clean test --tests  org.fisco.bcos.channel.test.block.BlockTest
      *部署OK合约*  gradle clean test --tests  org.fisco.bcos.channel.test.contract.OkTest
      *基本测试*    gradle clean test --tests org.fisco.bcos.channel.test.BasicTest
      *cns测试* 	 gradle clean test --tests org.fisco.bcos.channel.test.precompile.CnsServiceTest
      *tps测试*    gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.contract.PerfomanceOk --args='trans 100 100 1'
      *多群组测试部署*  	gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.TestOkTransaction --args='组号 deploy'
     *多群组测试调用合约*   gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.TestOkTransaction --args='租号 transaction 合约地址'

## sol合约转换Java类
   	为了方便使用，您无需安装solc工具，直接使用sdk帮您把智能合约sol文件转成相应java类以及abi和bin文件。目前我们默认支持的最新版本是0.4.25.如果想编译0.5以上版本合约请参考https://github.com/tbocek/solcJ这个项目，引入此jar包到sdk即可。

   	1 把编写的sol文件拷贝到src/test/resources/contract下,确保合约名和文件名保持一致。
    2 在项目目录下执行
     gradle test --tests org.fisco.bcos.web3j.solidity.SolidityFunctionWrapperGeneratorTest.compileSolFilesToJavaTest ;
    3 生成的类在src/test/java/org/fisco/bcos/temp文件夹下,并且生成的abi和bin在目录  src/test/resources/solidity目录下。（如果要使用编译后的java，注意修改生成java类的包名。）

## Precompile合约功能
  precompile合约是FISCO BCOS底层通过C++实现的一种高效智能合约，用于FISCO BCOS底层的系统信息配置与管理。sdk已提供precompile合约对应的Java接口，并且sdk的控制台通过调用这些Java接口实现了相关操作命令，体验控制台，可查阅控制台文档。sdk的precompile合约相关文件位于precompile目录下，目前提供分布式控制权限合约Authority.sol，CNS合约CNS.sol，系统属性配置合约SystemConfig.sol和节点类型配置合约Consensus.sol。其中对应转换的Java合约文件与solidty合约同名，并且同级目录下提供操作对应precompile合约的Service类，该Service类中的公有方法是sdk提供给开发者调用的接口。下面分别对precompile对应的Service类的接口进行介绍。

-AuthorityService ： sdk提供对分布式控制权限的支持。AuthorityService可以配置权限信息，其api，涉及的控制台命令及其含义如下表所示：

```eval_rst

+--------------------------------------------+------------------------+-----------------------------------+      
|API                                         |command                 |描述                               |
+============================================+========================+===================================+ 
|String add(String tableName, String addr)   |addAuthority(aa)        |根据表名和外部账户地址设置权限信息 |
+--------------------------------------------+------------------------+-----------------------------------+ 
|String remove(String tableName, String addr)|removeAuthority(ra)     |根据表名和外部账户地址去除权限信息 |
+--------------------------------------------+------------------------+-----------------------------------+ 
|List<AuthorityInfo> query(String tableName) |queryAuthority(qa)      |根据表名查询设置的权限信息         |
+--------------------------------------------+------------------------+-----------------------------------+ 

```

- CnsService ： sdk提供对CNS的支持。CnsService可以配置CNS信息，其api，涉及的控制台命令及其含义如下表所示：

```eval_rst

+------------------------------------------------------------------------+----------------+-------------------------------------------------------------+
|API                                                                     |command         |描述                                                         |
+========================================================================+================+=============================================================+ 
|String registerCns(String name, String version, String addr, String abi)|deployByCNS(dbc)|根据合约名、合约版本、合约地址和合约abi设置CNS信息          |
+------------------------------------------------------------------------+----------------+-------------------------------------------------------------+
|String getAddressByContractNameAndVersion(String contractNameAndVersion)|callByCNS(cbc)  |根据合约名和合约版本（使用:拼接为一个字符串）查询合约地址    |
+------------------------------------------------------------------------+----------------+-------------------------------------------------------------+
|List<CnsInfo> queryCnsByName(String name)                               |queryCNS(qcs)   |根据合约名查询CNS信息                                        |
+------------------------------------------------------------------------+----------------+-------------------------------------------------------------+
|List<CnsInfo> queryCnsByNameAndVersion(String name, String version)     |queryCNS(qcs)   |根据合约名和合约版本查询CNS信息                              |
+------------------------------------------------------------------------+----------------+-------------------------------------------------------------+

```

- SystemConfigSerivce ： sdk提供对系统配置的支持。SystemConfigSerivce可以配置系统属性值（目前支持tx_count_limit和tx_gas_limit属性的设置），其api，涉及的控制台命令及其含义如下表所示：


```eval_rst

+-----------------------------------------------+--------------------------+--------------------+      
|API                                            |command                   |描述                |
+===============================================+==========================+====================+ 
|String setValueByKey(String key, String value) |setSystemConfigByKey(ssc) |根据键设置对应的值  |
+--------------------------------------------+------------------------+-------------------------+ 

```
- ConsensusService ： sdk提供对节点类型配置的支持。ConsensusService可以设置节点类型，其api，涉及的控制台命令及其含义如下表所示：

```eval_rst

+----------------------------------+---------------+------------------------------------------------------+      
|API                               |command        |描述                                                  |
+==================================+===============+======================================================+ 
|String addMiner(String nodeId)    |addMiner(am)   |根据节点NodeID设置对应节点为记账节点                  |
+----------------------------------+---------------+------------------------------------------------------+  
|String addObserver(String nodeId) |addObserver(ao)|根据节点NodeID设置对应节点为观察节点                  |
+----------------------------------+---------------+------------------------------------------------------+   
|String removeNode(String nodeId)  |removeNode(rn) |根据节点NodeID从记账节点列表或观察节点列表退出对应节点|
+----------------------------------+---------------+------------------------------------------------------+  

```


## 国密功能使用：

  	使用国密功能，需要在在application.xml添加国密配置，如下；
  	<!-- 国密开关 -->
	<bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
		<constructor-arg value="1"/>
	</bean>
    国密编译：国密与非国密的差异只在于sol生成的bin不一样，而abi文件是一样的，你可以引入我们的solidity国密jar包，参考文档https://github.com/FISCO-BCOS/solidity， 我们已经把国密solidity的jar包放入了项目的lib目录下，使用国密你只需要修改build.gradle文件，注释掉非国密编译器jar包，引入国密编译器jar包
    	compile files('lib/solcJ-all-0.4.25-gm.jar')
	  //compile	'org.ethereum:solcJ-all:0.4.25'
    再执行1.4的步骤生成国密的java类即可使用国密功能。
     sdk提供了测试案例，在application.xml配置连接国密节点，然后执行下面命令即可：
     国密Erc合约测试
     gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.guomi.GMErc20Transaction
     国密OK合约测试：
     gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.guomi.GMOkTransaction
