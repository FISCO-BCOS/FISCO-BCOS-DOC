# 连接失败

标签：``连接失败`` ``问题排查`` ``Failed to connect to nodes`` ``握手失败``

----

## Failed to connect to nodes

若SDK连接节点失败，抛出错误`Failed to connect to nodes: [Connection refused:`，可从以下思路排查:

**第一步.检查节点进程**

通过`ps aux | grep fisco-bcos`命令检查节点进程是否启动

**第二步.检查节点监听IP**

若SDK与节点处于不同机器，检查节点的channel服务的监听端口`channel_listen_ip`(位于SDK直连节点的config.ini配置文件中)是否为`0.0.0.0`或外网IP(**注:** 此外网IP非云服务器的虚拟IP，必须是机器的网卡IP)

**第三步.检查SDK的连接配置**

检查SDK配置的节点连接是否正确，Web3SDK的节点连接配置参考[这里](../sdk/java_sdk.html#spring)；Java SDK的节点连接配置参考[这里](../sdk/java_sdk/configuration.html#id6)

**第四步.检查SDK与节点之间连通性**

下载telnet工具，使用命令`telnet ${nodeIP}:${channel_listen_port}`检查SDK与节点之间是否可连通，其中`${nodeIP}`为节点IP，`${channel_listen_ip}`为节点Channel服务的监听端口，可通过节点目录下的`config.ini`配置文件中`rpc.channel_listen_port`配置选项获取，若SDK与节点之间不连通，请检查是否开启了防火墙/安全组策略，需要在防火墙/安全组中开放FISCO BCOS节点所使用的channel端口。
<hr>

## Java SDK握手失败

若Java SDK连接节点时，显示握手失败，请按以下步骤排查: 

**第一步.检查是否拷贝证书**

若没有配置证书，请参考[Java SDK快速入门](../sdk/java_sdk/quick_start.html#sdk)配置SDK证书

**第二步.检查SDK与直连节点是否处于相同机构**

FISCO BCOS v2.5.0版本起，限制SDK只能连接本机构的节点，请确保SDK证书与直连节点证书属于相同机构

**第三步.检查JDK版本**

请确认JDK版本不小于JDK 1.8且不大于JDK 14。

**第四步.检查证书配置是否正确**

检查证书是否拷贝到默认的配置路径，请参考[这里](./certificate.html#id1).

**第五步.检查是否Jar包冲突**

Jar包冲突会报如下几个错误。
- SSL_MODE_ENABLE_FALSE_START
    **问题描述**<br>
    基于**Java SDK 2.6.1-rc1**开发应用时，已经按照[Java SDK快速入门](../sdk/java_sdk/quick_start.md)配置了证书，启动报错如下：
    ```
    java.lang.NoSuchFieldError: SSL_MODE_ENABLE_FALSE_START
        at io.netty.handler.ssl.ReferenceCountedOpenSslEngine.(ReferenceCountedOpenSslEngine.java:355) ~[netty-all-4.1.52.Final.jar:4.1.52.Final]
        at io.netty.handler.ssl.OpenSslEngine.(OpenSslEngine.java:32) ~[netty-all-4.1.52.Final.jar:4.1.52.Final]
    ```
    **问题分析:**<br>
    应用中引入了4.1.52版本的netty，使用了较高版本的`tcnative`，与Java SDK 2.6.1-rc1中的tcnative版本冲突，导致Java SDK初始化失败
    **解决方法：**<br>
    升级Java SDK 2.6.1-rc1到Java SDK 2.6.1或者最新，请参考[引入Java SDK](../sdk/java_sdk/quick_start.html#java-sdk).
- PrivateKey type not supported PKCS#8
    **问题描述**<br>
    应用中同时Java SDK 2.6.1和Web3SDK 2.6.0(或Web3SDK 2.6.1)，已经按照[Java SDK快速入门](../sdk/java_sdk/quick_start.md)配置了证书，SDK启动报错如下：
    ```bash
    create BcosSDK failed, error info: init channel network error: SSL context init failed, please make sure your cert and key files are properly configured. error info: PrivateKey type not supported PKCS#8
    ```
    **问题分析:**<br>
    Java SDK 2.6.1使用了较高版本的netty和tcnative，Web3SDK 2.6.0与Web3SDK 2.6.1使用的netty和tcnative版本较低，两者同时使用时jar包冲突，导致SDK启动报错。<br>
    **解决方法**<br>
    由于Java SDK和Web3SDK包含相同的功能，但Java SDK功能更全且会持续迭代，因此不建议同时使用Web3SDK与Java SDK，**建议使用Java SDK替代Web3SDK**.
    - **方法一:** 将Web3SDK升级到`2.6.2`版本，使其与Java SDK基于同样版本的netty与tcnative
    - **方法二:** 去除对Web3SDK的依赖，仅依赖Java SDK
<hr>

## web3SDK握手失败

若Web3SDK连接节点时，显示握手失败，请按以下步骤排查: 

**第一步.检查证书配置**

若没有配置证书，请参考[Web3SDK证书配置](../sdk/java_sdk.html#id2)配置SDK证书

**第二步.检查SDK与直连节点是否处于相同机构**

FISCO BCOS v2.5.0版本起，限制SDK只能连接本机构的节点，请确保SDK证书与直连节点证书属于相同机构

**第三步.检查JDK版本**

Web3SDK要求JDK版本大于等于1.8，推荐使用OracleJDK。
注：CentOS的yum仓库的OpenJDK缺少JCE(Java Cryptography Extension)，会导致JavaSDK无法正常连接区块链节点，Java环境安装请参考[Java环境配置](../console/console.html#java)

**第四步.针对Web3SDK常见的异常启动问题进行排查**
- Failed to connect to the node. Please check the node status and the console configuration.<br>    
  比较旧的SDK版本的提示，建议将JavaSDK版本升级至**2.2.2**或者以上(修改gradle.build或者maven配置文件中web3sdk的版本号)，可以获取更准确友好的提示，然后参考下面的错误提示解决问题。<br> <br>    

- Failed to initialize the SSLContext: class path resource [ca.crt] cannot be opened because it does not exist. <br>   
  无法加载到证书文件，证书文件没有正确拷贝至conf目录，可以参考控制台安装流程，拷贝证书文件至conf目录下。<br><br>  

- Failed to initialize the SSLContext: Input stream not contain valid certificates. <br> 
  加载证书文件失败，CentOS系统使用OpenJDK的错误，参考[CentOS环境安装JDK](../console/console.html#java)章节重新安装OracleJDK。<br><br> 

- Failed to connect to nodes: [connection timed out: /127.0.0.1:20200]<br>  
  连接超时，节点的网络不可达，请检查提示的IP是否配置错误，或者，当前JavaSDK运行环境与节点的环境网络确实不通，可以咨询运维人员解决网络不通的问题。<br><br> 

- Failed to connect to nodes: [Connection refused: /127.0.0.1:20200]<br>  
  拒绝连接，无法连接对端的端口，可以使用telnet命令检查端口是否连通，可能原因：
  1. 节点未启动，端口处于未监听状态，启动节点即可。
  2. 节点监听`127.0.0.1`的网段，监听`127.0.0.1`网络只能本机的客户端才可以连接，控制台位于不同服务器时无法连接节点，将节点配置文件`config.ini`中的`channel_listen_ip`修改为控制台连接节点使用的网段IP，或者将其修改为`0.0.0.0`。
  3. 错误的端口配置，配置的端口并不是节点监听的channel端口，修改连接端口为节点`config.ini`配置的`channel_listen_port`的值。<br>  
   **注意：控制台（或者JavaSDK）连接节点时使用Channel端口，并不是RPC端口，Channel端口在节点配置文件中通过channel_listen_ip字段配置，RPC端口通过jsonrpc_listen_port字段配置，注意区分，RPC默认从8545开始分配, Channel端口默认从20200开始分配。**  

- Failed to connect to nodes: [ ssl handshake failed:/127.0.0.1:20233]

  与节点ssl握手失败，可能原因：<br>  
  1. 拷贝了错误的证书，检查拷贝的证书是否正确。
  2. 端口配置错误，连接其他服务正在监听的端口，检查连接端口是否为节点`channel_listen_port`端口。
  3. JDK版本问题，推荐使用1.8以及以上的OracleJDK，参考[CentOS环境安装JDK](../console/console.html#java)章节安装OracleJDK。<br><br> 
  
- Failed to connect to [127.0.0.1:20233, 127.0.0.1:20234, 127.0.0.1:20235] ,groupId: 1 ,caCert: classpath:ca.crt ,sslKey: classpath:sdk.key ,sslCrt: classpath:sdk.crt ,java version: 1.8.0_231.
  
  其他未知的错误，需要查看日志文件分析具体错误。<br>

- Failed to connect to nodes: [ ssl handshake failed:/127.0.0.1:20200], PKIX path validation failed: java.security.cert.CertPathValidatorException:Algorithm constraints check failed on disabled algorithm: secp256k1<br>
  SDK依赖的Java禁用了`secp256k1`曲线，解决方法包括:
  - 使用1.8以上的OracleJDK，启动SDK时，加上`-Djdk.tls.namedGroups="secp256k1"`参数启用`secp256k1`曲线
  - 升级到最新的`Web3SDK 2.6.2`版本<br>

- java.lang.NoSuchFieldError: SSL_MODE_ENABLE_FALSE_START
  
  请参考[web3sdk issue #711](https://github.com/FISCO-BCOS/web3sdk/issues/711)<br>
