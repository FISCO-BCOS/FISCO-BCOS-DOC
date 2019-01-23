## 安装

### 二进制安装

请从[Release](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)页面下载最新的二进制程序，我们提供静态的二进制程序，在Ubuntu16.04和CentOS7经过测试。

### 源码编译

FSICO-BCOS使用通用[CMake](https://cmake.org)构建系统生成特定平台的构建文件，这意味着无论您使用什么操作系统工作流都非常相似：
1. 安装构建工具和依赖包（依赖于平台）
1. 从[FISCO-BCOS][FSICO-BCOS-GitHub]克隆代码
1. 运行`cmake`生成构建文件并编译

#### 依赖安装

- Ubuntu

推荐Ubuntu 16.04以上版本，16.04以下的版本没有经过测试，源码编译时依赖于编译工具、`leveldb`和`libssl`。

```bash
sudo apt install -y libssl-dev libleveldb-dev openssl cmake g++ git
```

- CentOS

推荐使用CentOS7以上版本。

```bash
sudo yum install -y epel-release
sudo yum install -y openssl-devel leveldb-devel openssl cmake3 gcc-c++ git
```

- macOS

推荐xcode10以上版本。macOS依赖包安装依赖于[Homebrew](https://brew.sh/)。

```bash
brew install -y leveldb openssl git
```

#### 克隆代码

```bash
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
```

#### 编译

编译完成后二进制文件位于`FISCO-BCOS/build/bin/fisco-bcos`。

```bash
cd FISCO-BCOS
mkdir build && cd build
# CentOS请使用cmake3
cmake ..
# 高性能机器可添加-j4使用4核加速编译
make
```

#### 编译选项介绍

- BUILD_GM，默认off，国密编译开关
- TESTS，默认off，单元测试编译开关
- STATIC_BUILD，默认off，静态编译开关，只支持Ubuntu

[FSICO-BCOS-GitHub]:https://github.com/FISCO-BCOS/FISCO-BCOS