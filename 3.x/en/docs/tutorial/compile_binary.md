# 5. Node source code compilation

Tags: "executable program" "development manual" "precompiled program" "source code compilation" "compilation tutorial"

----------

```eval_rst
.. important::
    Related Software and Environment Release Notes！'Please check<https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/compatibility.html>`_
```

```eval_rst
.. note::
   - FISCO BCOS supports compilation on Linux, macOS and Galaxy Kirin operating systems; When compiling binaries for Linux and Kirin systems, the gcc version must be no less than 10; When compiling binaries in macOS, clang version is required to be no less than 12.0
   - FISCO BCOS 3.x supports macOS compilation with Apple Silicon, the compilation steps are the same as x86 _ 64。
   - FISCO BCOS 3.x compilation depends on the rust environment, please install the rust environment before compiling the source code
   -Source code compilation is suitable for users with rich development experience. Dependency libraries need to be downloaded during the compilation process. Please keep the network unblocked
   - FISCO BCOS compiles binaries for both the Air and Pro versions
```

FSICO-BCOS uses a general-purpose 'CMake' build system to generate platform-specific build files, which means that the workflow is very similar no matter what operating system you use:

1. Install build tools and dependencies (platform dependent)
2. Cloning code from FISCO BCOS
3. Run cmake to generate the build file and compile it

## 1. Installation Dependencies

- **Ubuntu**

Ubuntu version 22.04 is recommended。

```shell
sudo apt update
sudo apt install -y wget python3-dev git curl zip unzip tar
sudo apt install -y --no-install-recommends clang make build-essential cmake libssl-dev zlib1g-dev ca-certificates libgmp-dev flex bison patch libzstd-dev ninja-build pkg-config autoconf


# Install Rust
curl https://sh.rustup.rs -sSf | bash -s -- -y
source $HOME/.cargo/env
```

- **CentOS**

CentOS7 or above is recommended。

```shell
sudo yum install -y epel-release centos-release-scl flex bison patch gmp-static
sudo yum install -y devtoolset-10 devtoolset-11 llvm-toolset-7 rh-perl530-perl cmake3 zlib-devel ccache lcov python-devel python3-devel

# Update git to above 2.17
sudo yum reinstall -y https://packages.endpointdev.com/rhel/7/os/x86_64/endpoint-repo.x86_64.rpm
sudo yum install -y git 

# Install Rust
curl https://sh.rustup.rs -sSf | bash -s -- -y
source $HOME/.cargo/env
```

- **macOS**

```shell
# Install xcode development tools, if already installed can be skipped
xcode-select --install

brew install git zstd wget gmp
# Install Rust
curl https://sh.rustup.rs -sSf | bash -s -- -y
source $HOME/.cargo/env
```

- **kylinOS**

```shell
# Install Dependencies
sudo yum update
sudo yum install -y wget curl tar 
sudo yum install -y build-essential clang flex bison patch glibc-static glibc-devel libzstd-devel libmpc cpp 

# Check the gcc version. If the gcc version is lower than 10, install a gcc version higher than 10
gcc -v

# Check whether the cmake version is greater than or equal to 3.14. If not, install the cmake version that meets the requirements
cmake --version
```

## 2. Cloning Code

```shell
# Create source code compilation directory
mkdir -p ~/fisco && cd ~/fisco

# clone code
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git

# If the preceding command cannot be executed for a long time due to network problems, try the following command:
git clone https://gitee.com/FISCO-BCOS/FISCO-BCOS.git

# Switch to source directory
cd FISCO-BCOS
```

## 3. Compile

**The compiled Air version binary is located in the path 'FISCO-BCOS / build / fisco-bcos-air / fisco-bcos-air'。**

**Compile all binaries corresponding to the Pro version of the Rpc service, Gateway service, Executor service, and node service. The path is as follows:**

- Rpc service: 'FISCO-BCOS / build / fisco-bcos-tars-service / RpcService / main / BcosRpcService'
- Gateway service: 'FISCO-BCOS / build / fisco-bcos-tars-service / GatewayService / main / BcosGatewayService'
- Executor service: 'FISCO-BCOS / build / fisco-bcos-tars-service / ExecutorService / main / BcosExecutorService'
- Blockchain node services: 'FISCO-BCOS / build / fisco-bcos-tars-service / NodeService / main / BcosNodeService', 'FISCO-BCOS / build / fisco-bcos-tars-service / NodeService / main / BcosMaxNodeService'

**If it is too slow to pull dependencies from GitHub during compilation, you can do the following to speed up:**

- **Use GitHub images (recommended)**


Run the following command to use 'https://ghproxy.com/https:/ / github.com / 'alternative' https:/ / github.com / 'Acceleration Dependency Download:

```shell
$ cat > ~/.gitconfig << EOF
[url "https://ghproxy.com/https://github.com/"]
        insteadOf = https://github.com/
[http]
        sslVerify = false
EOF
```

- **Modifying DNS and Host**

Modifying the DNS host or adding the direct IP address of GitHub to the host can improve the access speed。You can refer to tools such as' SwitchHosts'。

- **Configure the vcpkg agent**

``` shell
export X_VCPKG_ASSET_SOURCES=x-azurl,http://106.15.181.5/
```

### 3.1 Ubuntu

**Ubuntu 22.04 recommended。**

```shell
# Enter source directory
cd ~/fisco/FISCO-BCOS

# Create Compile Directory
mkdir -p build && cd build
cmake -DBUILD_STATIC=ON .. || cat *.log

# If vcpkg fails during dependency compilation, check the error log according to the error message
# For network reasons, configure the vcpkg agent as prompted above

# Compile source code(High-performance machines can add -j4 using 4-core accelerated compilation)
make -j4

# generate tgz package
rm -rf fisco-bcos-tars-service/*.tgz && make tar
```

### 3.2 CentOS

**Recommended version not less than CentOS 7。**

#### linux x86_64

```shell
# Using gcc10
source /opt/rh/devtoolset-10/enable
source /opt/rh/rh-perl530/enable
export LIBCLANG_PATH=/opt/rh/llvm-toolset-7/root/lib64/
source /opt/rh/llvm-toolset-7/enable

# Enter the source code compilation directory
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake3 -DBUILD_STATIC=ON .. || cat *.log

# If vcpkg fails during dependency compilation, check the error log according to the error message
# For network reasons, configure the vcpkg agent as prompted above

# High-performance machines can add -j4 using 4-core accelerated compilation
make -j4
# generate tgz package
rm -rf fisco-bcos-tars-service/*.tgz && make tar
```

#### aarch64

```shell
# Install devtoolset-10
yum install -y devtoolset-10

# Using gcc10
source /opt/rh/devtoolset-10/enable
export LIBCLANG_PATH=/opt/rh/llvm-toolset-7.0/root/lib64/
source /opt/rh/llvm-toolset-7.0/enable

# Enter the source code compilation directory
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake3 -DBUILD_STATIC=ON .. || cat *.log

# If vcpkg fails during dependency compilation, check the error log according to the error message
# For network reasons, configure the vcpkg agent as prompted above

# High-performance machines can add -j4 using 4-core accelerated compilation
make -j4
# generate tgz package
rm -rf fisco-bcos-tars-service/*.tgz && make tar
```

### 3.3 macOS

**Recommended xcode11 above version。**

```shell
# Enter the source code compilation directory
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake -DBUILD_STATIC=ON ..|| cat *.log

# If vcpkg fails during dependency compilation, check the error log according to the error message
# For network reasons, configure the vcpkg agent as prompted above

# If an error occurs when you execute the preceding procedure, run the following command to specify SDKROOT
#rm -rf CMakeCache.txt && export SDKROOT=$(xcrun --sdk macosx --show-sdk-path) && CC=/usr/bin/clang CXX=/usr/bin/clang++ cmake ..

# High-performance machines can be added -j8 using 8-core accelerated compilation
make -j4

# generate tgz package
rm -rf fisco-bcos-tars-service/*.tgz && make tar
```

### 3.4 kylinOS
```shell
# Import clang compile file
export LIBCLANG_PATH=/usr/lib64/

# Enter the source code compilation directory
cd ~/fisco/FISCO-BCOS
mkdir -p build && cd build
cmake3 -DBUILD_STATIC=ON .. || cat *.log

# If vcpkg fails during dependency compilation, check the error log according to the error message
# For network reasons, configure the vcpkg agent as prompted above

# High-performance machines can add -j4 using 4-core accelerated compilation
make -j4
# generate tgz package
rm -rf fisco-bcos-tars-service/*.tgz && make tar
```


### Compile Option Description

--- FULLNODE compiles all nodes, enabled by default
- -- WITH _ LIGHTNODE compiles light nodes, enabled by default
- -- WITH _ TIKV Compile TIKV, enabled by default
- -- WITH _ TARS _ SERVICES Compile TARS service, enabled by default
--- WITH _ SM2 _ OPTIMIZE enables SM2 performance optimization, which is enabled by default
- -- WITH _ CPPSDK Compile C++SDK, enabled by default
--- WITH _ BENCHMARK compiles the performance test program, which is enabled by default