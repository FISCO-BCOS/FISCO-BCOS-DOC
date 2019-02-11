# 国密使用手册
为了充分支持国产密码学算法，金链盟基于国产密码学标准，实现了国密加解密、签名、验签、哈希算法、国密SSL通信协议，并将其集成到FISCO BCOS平台中，实现了对国家密码局认定的商用密码的完全支持。

见[国密版fisco bcos设计手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-2.0.0/docs/design/features/guomi.html)
## 源码编译
```
$ git clone https://github.com/FISCO-BCOS/FISCO-bcos
$ git checkout release-2.0.1
$ mkdir build && cd build
$ cmake3 .. -DBUILD_GM=ON
$ make
```
执行成功后会在./bin/目录下生成国密版fisco-bcos可执行文件
```
$ ./fisco-bcos --version
会显示
$ FISCO-BCOS gm version 2.0
```
## 一键搭链脚本
拉取build_chain脚本，并进行安装
```
$ curl -LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/build_chain.sh
$ bash ./build_chain.sh -l '127.0.0.1:4' -e ./bin/fisco-bcos -g
```
* -e 为编译的国密版fisco-bcos的路径，需要在脚本后指定
* -g 国密编译选项，使用成功后会生成国密版的节点

当国密联盟链部署完成之后，其余操作与[快速入门](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-2.0.0/docs/tutorials/tutorials.html)的操作相同

## 国密配置信息

2.0国密版本FISCO BCOS节点之间采用SSL安全通道发送和接收消息，证书主要配置项集中在
```
[secure] section：

data_path：证书文件所在路径
key：节点私钥相对于data_path的路径
cert: 证书gmnode.crt相对于data_path的路径
ca_cert: gmca证书路径
```
```
;certificate configuration
[secure]
    ;directory the certificates located in
    data_path=conf/
    ;the node private key file
    key=gmnode.key
    ;the node certificate file
    cert=gmnode.crt
    ;the ca certificate file
    ca_cert=gmca.crt
```
conf目录下的original_cert文件夹为节点与sdk进行通信所需要的证书

## 国密版sdk配置
国密版FISCO BCOS需要配置相应的国密SDK。

### 依赖环境

   Java 8 和 gradle（4.10.1以上）

    如果本地gradle版本过低，又不便于升级，请使用./gradlew替代gradle命令即可

### 打开国密开关：

  	使用国密功能，需要在在application.xml添加国密配置，如下；
  	<!-- 国密开关 -->
​	<bean id="encryptType" class="org.fisco.bcos.web3j.crypto.EncryptType">
​		<constructor-arg value="1"/>
​	</bean>
​    国密编译：国密与非国密的差异只在于sol生成的bin不一样，而abi文件是一样的，你可以引入我们的solidity国密jar包，参考文档https://github.com/FISCO-BCOS/solidity， 我们已经把国密solidity的jar包放入了项目的lib目录下，使用国密你只需要修改build.gradle文件，注释掉非国密编译器jar包，引入国密编译器jar包<br>
​    	compile files('lib/solcJ-all-0.4.25-gm.jar')<br>
​	  //compile	'org.ethereum:solcJ-all:0.4.25'<br>
​    再执行1.4的步骤生成国密的java类即可使用国密功能。<br>
​     sdk提供了测试案例，在application.xml配置连接国密节点，然后执行下面命令即可：<br>
​     国密Erc合约测试<br>
​     gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.guomi.GMErc20Transaction<br>
​     国密OK合约测试：<br>
​     gradle run -Dexec.mainClass=org.fisco.bcos.channel.test.guomi.GMOkTransaction<br>


### 编译使用

  本项目提供了丰富的测试案例和功能，您也可以直接下载代码，使用基本功能。流程如下：
```
       #==== 拉取git代码 ====
       $ git clone https://github.com/FISCO-BCOS/web3sdk
       $ cd web3sdk
       $ git checkout release-2.0.1
       #===证书和节点配置 ===
      1. 在src/test/resources目录下，配置web3sdk的applicationContext.xml，
      如上图所示设置sdk要连接节点channel_listen_ip和channel_listen_port。并设置节点所在群组groupId;
      2. 由于是联盟链,拷贝节点证书文件ca.crt和keystore.p12到web3sdk/src/test/resources目录下；
      3. 成功后执行gradle build -x test；编译相应web3sdk jar包。
      4. 你也可以直接执行特定测试：
 	  *查询块高*    gradle clean test --tests  org.fisco.bcos.channel.test.block.BlockTest
​      *部署OK合约*  gradle clean test --tests  org.fisco.bcos.channel.test.contract.OkTest
```
### sol合约转换Java类
   	为了方便使用，您无需安装solc工具，直接使用sdk帮您把智能合约sol文件转成相应java类以及abi和bin文件。目前我们默认支持的最新版本是0.4.25.如果想编译0.5以上版本合约请参考https://github.com/tbocek/solcJ这个项目，引入此jar包到sdk即可。
```
   	1 把编写的sol文件拷贝到src/test/resources/contract下,确保合约名和文件名保持一致。
​    2 在项目目录下执行
​     gradle test --tests org.fisco.bcos.web3j.solidity.SolidityFunctionWrapperGeneratorTest.compileSolFilesToJavaTest ;
​    3 生成的类在src/test/java/org/fisco/bcos/temp文件夹下,并且生成的abi和bin在目录  src/test/resources/solidity目录下。（如果要使用编译后的java，注意修改生成java类的包名。）
```

详细操作见[sdk使用](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-2.0.0/docs/api/sdk.html

## 国密控制台使用

配置国密sdk之后，可以使用控制台功能。与标准版控制台使用方式相同，见[控制台操作手册](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-2.0.0/docs/manual/console.html)

## 国密落盘加密配置
**国密版的Key Center**

国密版的Key Center需重新编译Key Center，不同点在于cmake时带上``` -DBUILD_GM=On ```选项。

``` shell
cmake3 .. -DBUILD_GM=On
```

其它步骤与标准版Key Center相同，请参考：[keycenter repository](https://github.com/FISCO-BCOS/keycenter)

**国密版节点配置**

国密版的证书有所调整，落盘加密需要加密的证书，变为两个：conf/gmnode.key 和 conf/origin_cert/node.key。其它与[标准版落盘加密操作](./disk_encryption.md)相同。

``` shell
cd keycenter/scripts
#加密 conf/gmnode.key 参数：ip port 节点私钥文件 cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node_127.0.0.1_0/conf/gmnode.key ed157f4588b86d61a2e1745efe71e6ea 
#加密 conf/origin_cert/node.key 参数：ip port 节点私钥文件 cipherDataKey
bash encrypt_node_key.sh 127.0.0.1 31443 nodes/127.0.0.1/node_127.0.0.1_0/conf/origin_cert/node.key ed157f4588b86d61a2e1745efe71e6ea 
```