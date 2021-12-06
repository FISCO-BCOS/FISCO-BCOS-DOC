# 源码编译

标签：``可执行程序`` ``开发手册`` ``预编译程序`` ``源码编译`` ``编译教程``

----------


```eval_rst
.. note::
   - FISCO BCOS支持x86_64架构的Linux和macOS编译; Linux平台编译二进制时，要求g++版本不小于7.0; macOS系统编译二进制时，要求clang版本不小于12.0
   - 源码编译适合于有丰富开发经验的用户，编译过程中需要下载依赖库，请保持网络畅通
   - FISCO BCOS会同时编译出Air版本和Pro版本的二进制
```

FSICO-BCOS使用通用`CMake`构建系统生成特定平台的构建文件，这意味着无论您使用什么操作系统工作流都非常相似：

1. 安装构建工具和依赖包（依赖于平台）
2. 从FISCO BCOS克隆代码
3. 运行cmake生成构建文件并编译


## 1. 安装依赖

- **Ubuntu**

要求使用Ubuntu 18.04及以上版本。

```bash
sudo apt install -y cmake g++ git curl build-essential autoconf texinfo cmake flex bison
```

- **CentOS**

要求使用CentOS7以上版本。

```bash
sudo yum install -y epel-release centos-release-scl
sudo yum install -y cmake3 gcc gcc-c++ glibc-static glibc-devel libzstd-devel zlib-devel python-devel python3-devel git flex bison devtoolset-7
```

- **macOS**

```bash
brew install git flex bison
```

## 2. 克隆代码

```eval_rst
.. note::
   FISCO BCOS的源码位于 https://github.com/FISCO-BCOS/FISCO-BCOS 仓库的 ``master-3.0`` 分支
```

```bash
# 创建源码编译目录
mkdir -p ~/fisco && cd ~/fisco

# 克隆代码
git clone git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git

# 若因为网络问题导致长时间无法执行上面的命令，请尝试下面的命令：
git clone https://gitee.com/FISCO-BCOS/FISCO-BCOS.git

# 切换到master-3.0分支
cd FISCO-BCOS && git checkout master-3.0
```

## 3. 编译

**编译完的Air版本二进制位于`FISCO-BCOS/build/fisco-bcos/fisco-bcos`路径。**

**编译会产生Pro版本的Rpc服务、Gateway服务以及节点服务对应的所有二进制,路径如下：**
- Rpc服务：`FISCO-BCOS/build/RpcService/main/BcosRpcService`
- Gateway服务：`FISCO-BCOS/build/GatewayService/main/BcosGatewayService`
- 区块链节点服务：`NodeService/main/BcosNodeService`

### 3.1 Ubuntu

**要求版本不小于Ubuntu 18.04。**

```bash
# 进入源码目录
cd ~/fisco/FISCO-BCOS

# 创建编译目录
mkdir -p build && cd build
cmake ..

# 编译源码(高性能机器可添加-j4使用4核加速编译)
make -j2

# 生成tgz包
rm -rf *.tgz && make tgz
```
### 3.2 CentOS

**要求版本不小于CentOS 7。**

```bash
# 使用gcc7
source /opt/rh/devtoolset-7/enable

# 进入源码编译目录
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake3 ..
# 高性能机器可添加-j4使用4核加速编译
make -j2
# 生成tgz包
rm -rf *.tgz && make tgz
```

### 3.3 macOS

**推荐xcode11以上版本。**

```bash
# 进入源码编译目录
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake ..
# 高性能机器可添加-j4使用4核加速编译
make -j2

# 生成tgz包
rm -rf *.tgz && make tgz
```