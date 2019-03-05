# 环境要求

```eval_rst
.. important::

    - java版本
     要求 `JDK8或以上 <https://openjdk.java.net/>`_，推荐使用OpenJDK11

     > CentOS的yum仓库的OpenJDK由于缺少JCE(Java Cryptography Extension)，导致Java SDK无法正常连接区块链节点，在使用CentOS操作系统时，推荐从OpenJDK网站自行下载JDK [OpenJDK11下载地址](https://jdk.java.net/11/) [安装指南](https://openjdk.java.net/install/index.html)
    - FISCO BCOS区块链环境搭建
     参考 `FISCO BCOS搭链教程 <https://fisco-doc.readthedocs.io/en/feature-2.0.0/docs/manual/build_chain.html>`_
    - 网络连通性
     检查Java SDK连接的FISCO BCOS节点channel_listen_port是否能telnet通，若telnet不通，需要检查网络连通性和安全策略。

```
