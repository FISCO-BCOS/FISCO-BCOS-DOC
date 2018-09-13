# FAQ

## dist/bin/web3sdk运行出错

```eval_rst
.. admonition:: dist/bin/web3sdk运行出错
   
   permission denied错误
    web3sdk无可执行权限，尝试运行 ``chmod +x  dist/bin/web3sdk`` 

   com.fasterxml.jackson.databind.JsonMappingException: No content to map due to end-of-input
    可能是节点连接异常，使用如下方法排错:
     - 检查dist/conf/applicationContext.xml的节点配置: 必须设置成连接的FISCO-BCOS节点的channelPort
     - 检查FISCO-BCOS节点listenip: 必须设置成服务器IP或者0.0.0.0 
     - 检查网络连通性：telnet连接的FISCO-BCOS节点的ip和channelPort，必须能telnet通，若不通，请检查网络策略
     - 检查ca证书ca.crt: 必须与连接的FISCO-BCOS节点的ca.crt一致 
     - 检查客户端证书：解决方法参考 `FISCO-BCOS中client.keystore 的生成方法 <https://github.com/FISCO-BCOS/web3sdk/issues/20>`_

```

## 合约转换成java代码出错

```eval_rst
.. admonition:: 合约转换成java代码出错
   
   参考 `web3sdk issue1: 【使用工具包生成合约Java Wrap代码时报错】 <https://github.com/FISCO-BCOS/web3sdk/issues/1>`_ ，具体解决方法:
    .. code-block:: bash

       #------进入web3sdk代码目录(设web3sdk是~/mydata/web3sdk-master下)
       $ cd ~/mydata/web3sdk-master
       
       #------删除已经以前的编译文件
       $ rm -rf dist
       
       #------重命名web3sdk-master
       $ cd ..
       $ mv web3sdk-master web3sdk
       $ cd web3sdk
       
       #-------重新编译web3sdk
       $ gradle build

   原因分析
    从git载代码（Download ZIP）解压后目录为 **web3sdk-master** , 编译后生成  ``dist/apps/web3sdk-master.jar`` ，与 ``dist/bin/web3sdk`` 中配置的 ``CLASSPATH`` 中的配置项 ``$APP_HOME/apps/web3sdk.jar`` 名称不一致，导致调用工具包将合约代码转换为java代码出错

```
