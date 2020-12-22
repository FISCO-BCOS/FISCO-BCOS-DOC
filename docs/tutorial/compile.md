# 源码编译

```eval_rst
.. note::

    源码编译适合于有丰富开发经验的用户，编译过程中需要下载依赖库，请保持网络畅通。受网络和机器配置影响，编译用时5-20分钟不等。请务必保证机器能够访问githubusercontent.com，否则可能会因为网络不通阻塞编译！
```

FSICO-BCOS使用通用[CMake](https://cmake.org)构建系统生成特定平台的构建文件，这意味着无论您使用什么操作系统工作流都非常相似：
1. 安装构建工具和依赖包（依赖于平台）。
1. 从[FISCO BCOS][FSICO-BCOS-GitHub]克隆代码。
1. 运行`cmake`生成构建文件并编译。

## 克隆代码

```bash
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git -b master
```

## 安装依赖

- Ubuntu

推荐Ubuntu 16.04以上版本，源码编译时需要先安装gcc等工具。

```bash
sudo apt install -y g++ libssl-dev openssl cmake git build-essential autoconf texinfo flex patch bison libgmp-dev zlib1g-dev automake libtool
```

- CentOS

推荐使用CentOS7以上版本。

```bash
sudo yum install -y epel-release centos-release-scl
sudo yum install -y openssl-devel openssl cmake3 gcc-c++ git flex patch bison gmp-static devtoolset-7
```

- macOS

推荐xcode10以上版本。macOS依赖包安装依赖于[Homebrew](https://brew.sh/)。

```bash
brew install openssl git flex patch bison gmp
```

## 编译

编译完成后二进制文件位于`FISCO-BCOS/build/bin/fisco-bcos`。

```bash
cd FISCO-BCOS
mkdir -p build && cd build
# source /opt/rh/devtoolset-7/enable  # CentOS请执行此命令，其他系统不需要
# CentOS请使用cmake3
cmake ..
# 高性能机器可添加-j4使用4核加速编译
# macOS 编译出现 "ld: warning: direct access" 提示时，可以忽略
make
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载依赖库，请尝试从 `https://gitee.com/FISCO-BCOS/LargeFiles/tree/master/libs` 下载，放在FISCO-BCOS/deps/src/
    - 如果编译过程中长时间没有相应，一般是不能访问githubusercontent.com导致依赖无法下载，请使用代理或修改hosts使得机器能够访问githubusercontent.com
```

## 编译选项介绍

- TESTS，默认off，单元测试编译开关。通过`cmake -DTESTS=on ..`打开单元测试开关。
- DEMO，默认off，测试程序编译开关。通过`cmake -DDEMO=on ..`打开单元测试开关。
- TOOL，默认off，工具程序编译开关。通过`cmake -DTOOL=on ..`打开工具开关，提供FISCO节点的rocksdb读取工具。
- ARCH_NATIVE，默认off，编译时根据本地CPU指令优化以获得更好的性能，在ARM架构上编译可以使用此选项。GCC9以上版本编译暂未适配完成，可以通过打开此编译选项避过问题，完成编译。
- BUILD_STATIC，默认off，静态编译开关，只支持Ubuntu。通过`cmake -DBUILD_STATIC=on ..`打开静态编译开关。
- CMAKE_BUILD_TYPE，默认RelWithDebInfo，编译类型，如要编译Release版本，通过`cmake -DCMAKE_BUILD_TYPE=Release ..`设置
- DEBUG，默认off，调试模式，编译后会打印将要提交的数据，性能大幅降低，仅用于开发查问题。

- 生成源码文档。
```bash
# 安装Doxygen
sudo apt install -y doxygen graphviz
# 生成源码文档 生成的源码文档位于build/doc
cmake ..
make doc
```