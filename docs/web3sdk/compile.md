# SDK编译

```eval_rst
.. admonition:: 安装依赖软件
   
   部署web3sdk之前需要安装git, dos2unix依赖软件:

   -  **git**：用于拉取最新代码
   -  **dos2unix**: 用于处理windows文件上传到linux服务器时，文件格式无法被linux正确解析的问题；
   
   **centos**:  
    .. code-block:: bash

       $ sudo yum -y install git dos2unix 
    
   **ubuntu**: 
    .. code-block:: bash

       $ sudo apt install git tofrodos
       $ ln -s /usr/bin/todos /usr/bin/unxi2dos && ln -s /usr/bin/fromdos /usr/

.. admonition:: 编译源码

   执行如下命令拉取并编译源码：

    .. code-block:: bash

       #=== 创建并进入web3sdk源码放置目录（假设为~/mydata/）=====
       $ mkdir -p ~/mydata
       $ cd ~/mydata
       
       #==== 拉取git代码 ====
       $ git clone https://github.com/FISCO-BCOS/web3sdk
       
       #===编译we3bsdk源码，生成dist目录 ===
       $ cd web3sdk
       $ dos2unix *.sh
       $ . ./compile.sh
       
       #===编译成功后，web3sdk目录下生成dist文件夹，目录结构如下==========
       $ tree -L 2
       .
       ├── build
       │   ├── classes
       │   ├── ... 省略若干行...
       ├── build.gradle
       ├── dist
       │   ├── apps  #存放web3sdk.jar
       │   ├── bin   #存放可执行脚本compile.sh和web3sdk
       │   ├── contracts  #合约存储目录
       │   └── lib   #所有jar包存放目录
       ├── README.md
       ├── src
       │   ├── ...省略若干行...
       └── tools
       |   ├── bin
       |   └── contracts
```



```eval_rst

web3sdk编译成功后，会生成dist目录，dist目录主要内容如下：

+---------------+-------------------------------------------------------------------------------------+
|目录           | 说明                                                                                |
+===============+=====================================================================================+
|dist/apps      | 存放web3sdk编译生成的jar包web3sdk.jar                                               |
+---------------+-------------------------------------------------------------------------------------+
|               |  - web3sdk: 调用web3sdk.jar执行web3sdk内方法(如部署系统合约、调用合约工具方法等)    |
|dist/bin       |  - compile.sh: 将dist/contracts目录下的合约代码转换成java代码，供开发者使用         |
+---------------+-------------------------------------------------------------------------------------+
|dist/conf      | 配置目录, 用于配置节点信息、证书信息、日志目录等                                    |
+---------------+-------------------------------------------------------------------------------------+
|dist/contracts | 合约存放目录，compile.sh脚本可将存放于该目录下的合约代码转换成java代码              |
+---------------+-------------------------------------------------------------------------------------+
|dist/lib       | 存放web3sdk依赖库的jar包                                                            |
+---------------+-------------------------------------------------------------------------------------+

