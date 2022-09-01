# 源码编译

标签：``可执行程序`` ``开发手册`` ``预编译程序`` ``源码编译`` ``编译教程``

----------

```eval_rst
.. important::
    相关软件和环境版本说明！`请查看 <https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. note::
   - FISCO BCOS支持x86_64架构的Linux和macOS编译; Linux平台编译二进制时，要求g++版本不小于7.0; macOS系统编译二进制时，要求clang版本不小于12.0
   - FISCO BCOS 3.x支持带有Apple Silicon的macOS编译，编译步骤与x86_64相同。
   - FISCO BCOS 3.x的编译依赖rust环境，编译源码前请先安装rust环境
   - 源码编译适合于有丰富开发经验的用户，编译过程中需要下载依赖库，请保持网络畅通
   - FISCO BCOS会同时编译出Air版本和Pro版本的二进制
```

FSICO-BCOS使用通用`CMake`构建系统生成特定平台的构建文件，这意味着无论您使用什么操作系统工作流都非常相似：

1. 安装构建工具和依赖包（依赖于平台）
2. 从FISCO BCOS克隆代码
3. 运行cmake生成构建文件并编译

## 1. 安装依赖

- **Ubuntu**

推荐使用Ubuntu 22.04版本。

```shell
sudo apt install -y cmake g++ git curl build-essential autoconf texinfo cmake flex bison libzstd-dev libpython3-dev python-dev wget libgmp-dev

# 安装rust
curl https://sh.rustup.rs -sSf | bash -s -- -y
source $HOME/.cargo/env
```

- **CentOS**

推荐使用CentOS7以上版本。

```shell
sudo yum install -y epel-release centos-release-scl flex bison patch gmp-static
sudo yum install -y devtoolset-10 devtoolset-11 llvm-toolset-7 rh-perl530-perl cmake3 zlib-devel ccache lcov python-devel python3-devel
sudo yum install -y https://packages.endpointdev.com/rhel/7/os/x86_64/endpoint-repo.x86_64.rpm
sudo yum install -y git

# 安装rust
curl https://sh.rustup.rs -sSf | bash -s -- -y
source $HOME/.cargo/env
```

- **macOS**

```shell
# 安装xcode开发工具，若已经安装可略过
xcode-select --install

brew install git zstd wget gmp
# 安装rust
curl https://sh.rustup.rs -sSf | bash -s -- -y
source $HOME/.cargo/env
```

## 2. 克隆代码

```shell
# 创建源码编译目录
mkdir -p ~/fisco && cd ~/fisco

# 克隆代码
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git

# 若因为网络问题导致长时间无法执行上面的命令，请尝试下面的命令：
git clone https://gitee.com/FISCO-BCOS/FISCO-BCOS.git

# 切换到源码目录
cd FISCO-BCOS
```

## 3. 编译

**编译完的Air版本二进制位于`FISCO-BCOS/build/fisco-bcos-air/fisco-bcos-air`路径。**

**编译会产生Pro版本的Rpc服务、Gateway服务、Executor服务以及节点服务对应的所有二进制,路径如下：**

- Rpc服务：`FISCO-BCOS/build/fisco-bcos-tars-service/RpcService/main/BcosRpcService`
- Gateway服务：`FISCO-BCOS/build/fisco-bcos-tars-service/GatewayService/main/BcosGatewayService`
- Executor服务：`FISCO-BCOS/build/fisco-bcos-tars-service/ExecutorService/main/BcosExecutorService`
- 区块链节点服务：`FISCO-BCOS/build/fisco-bcos-tars-service/NodeService/main/BcosNodeService`、`FISCO-BCOS/build/fisco-bcos-tars-service/NodeService/main/BcosMaxNodeService`

**若编译过程中从GitHub拉取依赖太慢，可执行以下方式加速：**

- **使用GitHub镜像（推荐）**
  
执行如下命令使用`https://ghproxy.com/https://github.com/`替代`https://github.com/`加速依赖下载：

```shell
$ cat > ~/.gitconfig << EOF
[url "https://ghproxy.com/https://github.com/"]
        insteadOf = https://github.com/
[http]
        sslVerify = false
EOF
```

- **修改DNS和Host**

修改DNS主机，或者在Host加上GitHub的直连IP，可以大概率改善访问速度。可以参考`SwitchHosts`等工具。

- **配置vcpkg代理**

``` shell
export X_VCPKG_ASSET_SOURCES=x-azurl,http://106.15.181.5/
```

### 3.1 Ubuntu

**要求版本不小于Ubuntu 18.04。**

```shell
# 进入源码目录
cd ~/fisco/FISCO-BCOS

# 创建编译目录
mkdir -p build && cd build
cmake -DBUILD_STATIC=ON ..

# 若编译依赖过程中遇到了vcpkg失败的问题，请根据报错提示查看错误日志
# 若为网络原因可按照上文提示配置vcpkg代理

# 编译源码(高性能机器可添加-j4使用4核加速编译)
make -j4

# 生成tgz包
rm -rf *.tgz && make tar
```

### 3.2 CentOS

**推荐版本不小于CentOS 7。**

#### linux x86_64

```shell
# 使用gcc11
source /opt/rh/devtoolset-10/enable
source /opt/rh/rh-perl530/enable
export LIBCLANG_PATH=/opt/rh/llvm-toolset-7/root/lib64/
source /opt/rh/llvm-toolset-7/enable

# 进入源码编译目录
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake3 -DBUILD_STATIC=ON ..

# 若编译依赖过程中遇到了vcpkg失败的问题，请根据报错提示查看错误日志
# 若为网络原因可按照上文提示配置vcpkg代理

# 高性能机器可添加-j4使用4核加速编译
make -j4
# 生成tgz包
rm -rf *.tgz && make tar
```

#### aarch64

```shell
# 安装devtoolset-10
yum install -y devtoolset-10

# 使用gcc10
source /opt/rh/devtoolset-10/enable
export LIBCLANG_PATH=/opt/rh/llvm-toolset-7.0/root/lib64/
source /opt/rh/llvm-toolset-7.0/enable

# 进入源码编译目录
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake3 -DBUILD_STATIC=ON ..

# 若编译依赖过程中遇到了vcpkg失败的问题，请根据报错提示查看错误日志
# 若为网络原因可按照上文提示配置vcpkg代理

# 高性能机器可添加-j4使用4核加速编译
make -j4
# 生成tgz包
rm -rf *.tgz && make tar
```

### 3.3 macOS

**推荐xcode11以上版本。**

```shell
# 进入源码编译目录
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake -DBUILD_STATIC=ON ..

# 若编译依赖过程中遇到了vcpkg失败的问题，请根据报错提示查看错误日志
# 若为网络原因可按照上文提示配置vcpkg代理

# 若执行以上过程报错，请尝试执行如下命令指定SDKROOT
#rm -rf CMakeCache.txt && export SDKROOT=$(xcrun --sdk macosx --show-sdk-path) && CC=/usr/bin/clang CXX=/usr/bin/clang++ cmake ..

# 高性能机器可添加-j8使用8核加速编译
make -j4

# 生成tgz包
rm -rf *.tgz && make tar
```
