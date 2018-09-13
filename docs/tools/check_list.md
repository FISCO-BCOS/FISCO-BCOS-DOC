# CheckList
通常我们推荐使用物料包[[FISCO BCOS物料包]](https://github.com/FISCO-BCOS/fisco-package-build-tool)搭建FISCO BCOS的环境, 可以屏蔽搭建过程中的一些繁琐细节。

物料包使用时, 本身即有一些依赖, FISCO BCOS对网络、yum源等外部环境也存在依赖, 为减少搭建过程中遇到的问题,建议在使用之前对整个搭建的环境进行检查, 特别是生产环境的搭建, 尤其推荐CheckList作为一个必备的流程。  

## 检查项
- 操作系统
- 网络
- java环境
- openssl版本
- yum/apt源检查

### **操作系统**  
```
支持操作系统：
CentOS 7.2 64位 、 Ubuntu 16.04 64位
```
- 检查系统是否为64位系统：  
  
使用**uname -m**命令, 64位系统的输出为x86_64, 32位系统的输出为i386或者i686.
```
$ uname -m
$ x86_64
```

- 操作系统版本检查：
```
CentOS
$ cat /etc/redhat-release 
$ CentOS Linux release 7.2.1511 (Core)

Ubuntu
$ cat /etc/os-release
$ NAME="Ubuntu"
$ VERSION="16.04.1 LTS (Xenial Xerus)"
$ ID=ubuntu
$ ID_LIKE=debian
$ PRETTY_NAME="Ubuntu 16.04.1 LTS"
$ VERSION_ID="16.04"
$ HOME_URL="http://www.ubuntu.com/"
$ SUPPORT_URL="http://help.ubuntu.com/"
$ BUG_REPORT_URL="http://bugs.launchpad.net/ubuntu/"
```

### **网络**  
FISCO BCOS单节点需要使用三个端口： rpc_port、channel_port、p2p_port
- rpc_port不会有远程访问
- channel_port需要被使用web3sdk的服务访问 
- p2p_port 节点之间通过互联组成p2p网络

**实际中, 需要考虑channel_port、p2p_port的网络访问策略, 节点的channel_port需要被使用区块链服务的应用所在服务器连接, 
每个节点的p2p_port需要能被其他节点所在服务器的连接。**

检查服务器A某一个端口p能够被另一台服务器B访问的简单方法：
*  在服务器A上执行
```
sudo nc -l p    //实际检查时, 将p替换为实际端口。
```
*  在服务器B上面执行telnet命令
```
$ telnet A p  //实际检查时, 将A替换服务器ip, 将p替换为实际端口。
Trying A...
Connected to A.
Escape character is '^]'.
```
上面的结果说明成功, 服务器B确实可以访问服务器A的端口p。

*  网络不通, 通常需要运维工程师协助解决。

### **java环境**  
####  版本检查
FISCO BCOS需求版本Oracle JDK 1.8(java 1.8)
- [&] CentOS/Ubuntu默认安装或者通过yum/apt安装的JDK为openJDK, 并不符合使用的要求。  
- [&] 可以通过java -version查看版本, Oracle JDK输出包含\"Java(TM) SE\"字样, OpenJDK输出包含\"OpenJDK\"的字样, 很容易区分。  
```
Oracle JDK 输出：
$ java -version
$ java version "1.8.0_144"
$ Java(TM) SE Runtime Environment (build 1.8.0_144-b01)
$ Java HotSpot(TM) 64-Bit Server VM (build 25.144-b01, mixed mode)

OpenJDK 输出：
$ java -version
$ openjdk version "1.8.0_171"
$ OpenJDK Runtime Environment (build 1.8.0_171-b10)
$ OpenJDK 64-Bit Server VM (build 25.171-b10, mixed mode)
```

####  Oracle JDK安装
当前系统如果没有安装JDK, 或者JDK的版本不符合预期, 可以参考[[Oracle JAVA 1.8 安装教程]](https://github.com/ywy2090/fisco-package-build-tool/blob/docker/doc/Oracle%20JAVA%201.8%20%E5%AE%89%E8%A3%85%E6%95%99%E7%A8%8B.md)。

### **openssl版本**
openssl需求版本为1.0.2, 可以使用 openssl version 查看。
```
$ openssl version
$ OpenSSL 1.0.2k-fips  26 Jan 2017
```

服务器如果没有安装openssl, 可以使用yum/apt进行安装。
```
sudo yum/apt -y install openssl
```
yum/apt不存在openssl, 可以参考下面的替换apt/yum源。

### **yum/apt源检查** 
物料包工作过程中会使用yum/apt安装一些依赖项, 当前yum/apt源无法下载到相关依赖时, 工作工程中可能会出现一些问题(fisco-bcos-package-tool内部已经做了相关处理, 在异常执行时给用户提示, 并停止工作, 但实际环境更加复杂, 不排除有遗漏)。

对此建议可以提前检查yum/apt源。 
#### 检查列表
在服务器上面依次执行下面命令:
```
CentOS 依赖
        sudo yum -y install bc
        sudo yum -y install gettext
        sudo yum -y install cmake3
        sudo yum -y install git
        sudo yum -y install gcc-c++
        sudo yum -y install openssl
        sudo yum -y install openssl-devel
        sudo yum -y install leveldb-devel
        sudo yum -y install curl-devel
        sudo yum -y install libmicrohttpd-devel
        sudo yum -y install gmp-devel
        sudo yum -y install lsof
        sudo yum -y install crudini
        sudo yum -y install libuuid-devel

Ubuntu 依赖
        sudo apt-get -y install gettext
        sudo apt-get -y install bc
        sudo apt-get -y install cmake
        sudo apt-get -y install git
        sudo apt-get -y install gcc-c++
        sudo apt-get -y install openssl
        sudo apt-get -y install build-essential libboost-all-dev
        sudo apt-get -y install libcurl4-openssl-dev libgmp-dev
        sudo apt-get -y install libleveldb-dev  libmicrohttpd-dev
        sudo apt-get -y install libminiupnpc-dev
        sudo apt-get -y install libssl-dev libkrb5-dev
        sudo apt-get -y install lsof
        sudo apt-get -y install crudini
        sudo apt-get -y install uuid-dev

```

如果apt/yum安装某些项失败, 说明apt/yum源不存在该依赖项。

#### 替换yum/apt源
yum/apt源如果不满足要求, 可以考虑将源替换为阿里云的源。

- CentOS更换阿里云yum源
```
1. 备份   
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base-bak.repo
2. 下载   
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
3. 更新yum缓存   
yum makecache
```
- Ubuntu 16.04更换阿里云源  
```
1. 备份  
sudo cp /etc/apt/sources.list /etc/apt/sources.list.old
2. 修改source.list 
sudo vim /etc/apt/source.list
添加以下信息
# deb cdrom:[Ubuntu 16.04 LTS _Xenial Xerus_ - Release amd64 (20160420.1)]/ xenial main restricted
deb-src http://archive.ubuntu.com/ubuntu xenial main restricted #Added by software-properties
deb http://mirrors.aliyun.com/ubuntu/ xenial main restricted
deb-src http://mirrors.aliyun.com/ubuntu/ xenial main restricted multiverse universe #Added by software-properties
deb http://mirrors.aliyun.com/ubuntu/ xenial-updates main restricted
deb-src http://mirrors.aliyun.com/ubuntu/ xenial-updates main restricted multiverse universe #Added by software-properties
deb http://mirrors.aliyun.com/ubuntu/ xenial universe
deb http://mirrors.aliyun.com/ubuntu/ xenial-updates universe
deb http://mirrors.aliyun.com/ubuntu/ xenial multiverse
deb http://mirrors.aliyun.com/ubuntu/ xenial-updates multiverse
deb http://mirrors.aliyun.com/ubuntu/ xenial-backports main restricted universe multiverse
deb-src http://mirrors.aliyun.com/ubuntu/ xenial-backports main restricted universe multiverse #Added by software-properties
deb http://archive.canonical.com/ubuntu xenial partner
deb-src http://archive.canonical.com/ubuntu xenial partner
deb http://mirrors.aliyun.com/ubuntu/ xenial-security main restricted
deb-src http://mirrors.aliyun.com/ubuntu/ xenial-security main restricted multiverse universe #Added by software-properties
deb http://mirrors.aliyun.com/ubuntu/ xenial-security universe
deb http://mirrors.aliyun.com/ubuntu/ xenial-security multiverse

3. 更新apt缓存    
sudo apt-get update
```