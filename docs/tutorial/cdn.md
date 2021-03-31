# 国内镜像和CDN加速

标签：``CDN加速`` ``国内镜像`` ``gitee镜像`` ``访问GitHub慢``

----
本节为访问GitHub较慢的用户提供国内镜像下载地址，以及CDN加速访问介绍。

## FISCO BCOS源码与二进制程序

### 源码同步

FISCO BCOS当前所有仓库源码位于[https://github.com/FISCO-BCOS/FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)，每个新的版本发布会将代码合入master分支。

为了方便国内用户，我们同样在gitee上提供了镜像仓库[https://gitee.com/FISCO-BCOS/FISCO-BCOS](https://gitee.com/FISCO-BCOS/FISCO-BCOS)，每次新版本发布后，镜像仓库会同步GitHub上官方仓库的更新，如果从GitHub下载失败，请尝试使用gitee镜像仓库。

### 二进制程序

FISCO BCOS每个新版本发布会在[GitHub Release](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)或[Gitee Release](https://gitee.com/FISCO-BCOS/FISCO-BCOS/releases)中提供对应的二进制程序和部署工具，同时在CDN也会提供同样的二进制程序。当前所提供的二进制程序包括：

1. fisco-bcos.tar.gz ：静态二进制程序，支持CentOS 7 和Ubuntu 16.04以上版本
1. fisco-bcos-macOS.tar.gz ：对应macOS系统的二进制程序
1. build_chain.sh ：对应版本的开发部署工具，依赖openssl和curl，支持CentOS 7/Ubuntu 16.04以上/macOS 10.15以上版本

用户使用开发部署工具(build_chain)，工具先尝试从GitHub下载所需要的二进制程序，如果下载失败则尝试从官网下载。

用户运维部署工具(generator)的时候，工具默认从GitHub下载所需要的二进制程序，可以通过--cdn参数指定从官网下载。例如`./generator --download_fisco ./meta --cdn`

## FISCO BCOS文档镜像

FISCO BCOS文档使用readthedocs管理，全部开源于[https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/)，同样提供国内镜像[http://docs.fisco-bcos.org](http://docs.fisco-bcos.org)。

每个版本发布会为上个版本的文档打Tag，新版本的文档会合入主干分支，文档由于会持续改进，所以是下个版本发布才打上个版本的tag。readthedocs文档支持下载PDF格式，方便用户使用。

## FISCO BCOS配套工具获取

### 控制台

FISCO BCOS控制台是一个交互式命令行工具，使用Java开发，代码位于[https://github.com/FISCO-BCOS/console](https://github.com/FISCO-BCOS/console)，国内镜像[https://gitee.com/FISCO-BCOS/console](https://gitee.com/FISCO-BCOS/console)。

控制台每个版本发布会提供编译好的包，用户下载后配置后即可使用，为了下载控制台用户需要获取download_console.sh脚本。此脚本会从GitHub下载最新版本console.tar.gz，如果下载失败则自动尝试从官网CDN下载。下面的指令从国内镜像获取download_console.sh脚本并执行。

```bash
curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh && bash download_console.sh
```

### TASSL

FISCO BCOS国密版本需要使用TASSL生成国密版本的证书，部署工具会自动从GitHub下载，解压后放置于~/.fisco/tassl，如果碰到下载失败，请尝试从[https://gitee.com/FISCO-BCOS/LargeFiles/blob/master/tools/tassl.tar.gz](https://gitee.com/FISCO-BCOS/LargeFiles/blob/master/tools/tassl.tar.gz)下载并解压后，放置于`~/.fisco/tassl`。相应的macOS请下载[https://gitee.com/FISCO-BCOS/LargeFiles/blob/master/tools/tassl_mac.tar.gz](https://gitee.com/FISCO-BCOS/LargeFiles/blob/master/tools/tassl_mac.tar.gz)，ARM下请下载[https://gitee.com/FISCO-BCOS/LargeFiles/blob/master/tools/tassl-aarch64.tar.gz](https://gitee.com/FISCO-BCOS/LargeFiles/blob/master/tools/tassl-aarch64.tar.gz)

### 账户生成脚本

FISCO BCOS在国密模式下使用sm2曲线和对应签名算法，在非国密场景使用secp256k1曲线和ecdsa签名算法。为方便用户提供了生成脚本，脚本生成私钥并以账户地址命名，支持PEM和PKCS12两种格式。详情请参考这里[https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html)

get_account.sh脚本依赖于openssl指令，用于生成secp256k1私钥，如果从GitHub下载失败，可以尝试镜像地址[https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh](https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_account.sh)

get_gm_account.sh脚本用于生成sm2私钥，依赖于TASSL。如果从GitHub下载失败，可以尝试镜像地址[https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_gm_account.sh](https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/tools/get_gm_account.sh)


## 举例：使用国内镜像建链

本节以搭建2.7.1国密版本为例，使用国内镜像建链，非国密版本的操作类似，参考[https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)

### 下载开发部署工具

```bash
curl -#LO https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.7.2/build_chain.sh
```

如果下载失败请尝试`curl -#LO https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v2.7.2/build_chain.sh`

### 下载二进制程序

开发部署工具（build_chain）会自动下载二进制程序，下载失败自动切换官网CDN，不需要用户关注。用户也可以手动下载二进制程序或编译源码，通过开发部署工具的-e选项指定，此时工具不会再去下载。-e选项参考[https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/build_chain.html#e-optional](../manual/build_chain.html#e-optional)

### 搭建2.7.1国密FISCO BCOS链

搭建国密版本时，开发部署工具还依赖tassl，工具会自动下载，如果失败请用户参考TASSL手动下载方法，下载解压后放置于~/.fisco/tassl。执行下面的指令，输出All completed即表示执行成功。

```bash
bash build_chain.sh -l 127.0.0.1:4 -p 30300,20200,8545 -g -v 2.7.1
```

## 举例：使用国内源码镜像编译

本节以CentOS 7 为例，从gitee镜像下载源码并编译，其他操作系统编译流程类似，请参考[https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/get_executable.html#id2](../manual/get_executable.html#id2)

### 安装依赖

```bash
sudo yum install -y epel-release centos-release-scl
sudo yum install -y openssl-devel openssl cmake3 gcc-c++ git flex patch bison gmp-static devtoolset-7
```

### 下载源码

```bash
git clone https://gitee.com/FISCO-BCOS/FISCO-BCOS.git
```

### 下载依赖包

FISCO BCOS在编译时会自动下载依赖包，每个依赖包有多个源。如果在编译阶段下载依赖包失败，请根据提示从下面的国内镜像手动下载，放置于FISCO-BCOS/deps/src目录下，再次make。v2.5.0以上版本由于使用evmone作为虚拟机引擎，需要从GitHub下载部分依赖，需要保证机器能正常访问`githubusercontent.com`。

[https://gitee.com/FISCO-BCOS/LargeFiles/tree/master/libs](https://gitee.com/FISCO-BCOS/LargeFiles/tree/master/libs)

### 编译源码

1. 创建编译目录

```bash
cd FISCO-BCOS && mkdir build && cd build
```

2. 使用gcc7
```bash
source /opt/rh/devtoolset-7/enable
```

3. 编译
```bash
cmake3 ..
make -j2
```
