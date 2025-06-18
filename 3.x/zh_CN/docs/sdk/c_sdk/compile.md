# 源码编译

标签：``c-sdk`` ``源码编译``

----------

## 源码下载

- github

```shell
git clone https://github.com/FISCO-BCOS/bcos-c-sdk.git
```

网络环境不佳可以使用国内`gitee`环境获取

- gitee

```shell
git clone https://gitee.com/FISCO-BCOS/bcos-c-sdk.git
```

## `Linux`编译

```shell
export CFLAGS="${CFLAGS} -fPIC"
export CXXFLAGS="${CXXFLAGS} -fPIC"

#  source /opt/rh/devtoolset-7/enable # centos执行

cd bcos-c-sdk
mkdir build && cd build
cmake ../ -DBUILD_SAMPLE=ON  # centos使用cmake3, BUILD_SAMPLE表示编译sample目录的示例程序
```

编译生成`libbcos-c-sdk.so`

```shell
-rw-r--r--   1 root  root  548896 12  9 17:27 libbcos-c-sdk.so
```

## `macOs`编译

```shell
cd bcos-c-sdk
mkdir build && cd build
cmake ../ -DBUILD_SAMPLE=ON # BUILD_SAMPLE表示编译sample目录的示例程序
```

编译生成`libbcos-c-sdk.dylib`

```shell
-rw-r--r--   1 root  root  548896 12  9 17:27 libbcos-c-sdk.dylib
```

## `Windows`编译

```shell
# cmake
cmake -G "Visual Studio 15 2017" -A x64 ../ -DHUNTER_CONFIGURATION_TYPES=Release -DCMAKE_WINDOWS_EXPORT_ALL_SYMBOLS=ON

# 编译
MSBuild bcos-c-sdk.sln /p:Configuration=Release /p:Platform=x64
```

编译结果:

`build/Release/bcos-c-sdk.dll` # 动态库

`build/Release/bcos-c-sdk.lib` # 符号表
