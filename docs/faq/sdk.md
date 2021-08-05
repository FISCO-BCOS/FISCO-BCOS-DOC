# Web3SDK - Java版本要求

Web3SDK要求JDK版本大于等于1.8，推荐使用OracleJDK。  
     
**注**：CentOS的yum仓库的OpenJDK缺少JCE(Java Cryptography Extension)，会导致JavaSDK无法正常连接区块链节点，Java环境安装请参考[Java环境配置](../manual/console.html#java)

--------

# Web3SDK - 常见的异常启动问题

- Failed to connect to the node. Please check the node status and the console configuration.<br>    
  比较旧的SDK版本的提示，建议将JavaSDK版本升级至**2.2.2**或者以上(修改gradle.build或者maven配置文件中web3sdk的版本号)，可以获取更准确友好的提示，然后参考下面的错误提示解决问题。<br> <br>    

- Failed to initialize the SSLContext: class path resource [ca.crt] cannot be opened because it does not exist. <br>   
  无法加载到证书文件，证书文件没有正确拷贝至conf目录，可以参考控制台安装流程，拷贝证书文件至conf目录下。<br><br>  

- Failed to initialize the SSLContext: Input stream not contain valid certificates. <br> 
  加载证书文件失败，CentOS系统使用OpenJDK的错误，参考[CentOS环境安装JDK](../console/console.html?#centosjava)章节重新安装OracleJDK。<br><br> 

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
  3. JDK版本问题，推荐使用1.8以及以上的OracleJDK，参考[CentOS环境安装JDK](../console/console.html?#centosjava)章节安装OracleJDK。<br><br> 
  
- Failed to connect to [127.0.0.1:20233, 127.0.0.1:20234, 127.0.0.1:20235] ,groupId: 1 ,caCert: classpath:ca.crt ,sslKey: classpath:sdk.key ,sslCrt: classpath:sdk.crt ,java version: 1.8.0_231.
  其他未知的错误，需要查看日志文件分析具体错误。<br>

- Failed to connect to nodes: [ ssl handshake failed:/127.0.0.1:20200], PKIX path validation failed: java.security.cert.CertPathValidatorException:Algorithm constraints check failed on disabled algorithm: secp256k1<br>
  SDK依赖的Java禁用了`secp256k1`曲线，解决方法包括:
  - 使用1.8以上的OracleJDK，启动SDK时，加上`-Djdk.tls.namedGroups="secp256k1"`参数启用`secp256k1`曲线
  - 升级到最新的`Web3SDK 2.6.2`版本<br>

- java.lang.NoSuchFieldError: SSL_MODE_ENABLE_FALSE_START
  请参考[web3sdk issue #711](https://github.com/FISCO-BCOS/web3sdk/issues/711)<br>

