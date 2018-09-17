# 编译安装

## 拉取源码

```eval_rst

.. admonition:: 安装依赖软件
   
   centos系统安装如下依赖软件: 
  
   .. code-block:: bash

      $ sudo yum -y install git dos2unix lsof
    
   ubuntu系统安装如下软件:

   .. code-block:: bash
      
      $ sudo apt install git lsof tofrodos
      $ ln -s /usr/bin/todos /usr/bin/unxi2dos && ln -s /usr/bin/fromdos /usr/bin/dos2unix

.. admonition:: 拉取源码
   
   .. code-block:: bash

      # 进入源码存放目录(设位于~/mydata)
      $ cd ~/mydata
      
      # 从git拉取源码
      $ git clone https://github.com/FISCO-BCOS/FISCO-BCOS

```

## 编译源码

```eval_rst

.. admonition:: 编译国密版FISCO BCOS

   安装依赖包(执行scripts/install_deps.sh脚本)：
     .. code-block:: bash

        # 进入FISCO BCOS源码目录(设FISCO-BCOS源码位于/mydata目录)
        $ cd /mydata/FISCO-BCOS
        
        # 为了防止windows脚本上传到linux环境下引起的不兼容问题，使用dos2unix格式化所有脚本
        $ dos2unix `find . -name "*.sh"`
        
   
   编译国密版FISCO-BCOS(-DENCRYPTTYPE=ON)：
     .. code-block:: bash
        
        # 进入源码目录(设位于~/mydata目录)
        $ cd ~/mydata/FISCO-BCOS

        # 调用build.sh脚本编译国密版fisco-bcos
        # 注: (当前用户需要有sudo权限，期间可能会多次输入密码)
        # -g: 编译国密版FISCO-BCOS(国密链必须设置该选项)
        $ bash build.sh -g

        # 确认fisco-bcos是国密版本
        $ fisco-bcos --version
        FISCO-BCOS version 1.3.2-gm  # 有-gm，表明是国密版FISCO BCOS
        FISCO-BCOS network protocol version: 63
        Client database version: 12041
        Build: ETH_BUILD_PLATFORM/ETH_BUILD_TYPE

        # 注: 若上次编译失败，本次继续编译时可能会报错，此时需要删掉源码目录下deps/src/目录中缓存包后重新使用build.sh编译，一般包括如下命令:
        # rm -rf deps/src/*-build
        # rm -rf deps/src/*-stamp

```
