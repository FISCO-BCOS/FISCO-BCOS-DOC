# source code compilation

Tags: "c-sdk" "source code compilation"

----------

## Source code download

- github

```shell
git clone https://github.com/FISCO-BCOS/bcos-c-sdk.git
```

Poor network environment can use the domestic 'gitee' environment to obtain

- gitee

```shell
git clone https://gitee.com/FISCO-BCOS/bcos-c-sdk.git
```

## 'Linux 'Compile

```shell
export CFLAGS="${CFLAGS} -fPIC"
export CXXFLAGS="${CXXFLAGS} -fPIC"

#  source /opt/rh/devtoolset-7/enable # centos execution

cd bcos-c-sdk
mkdir build && cd build
cmake ../ -DBUILD_SAMPLE=ON  # Centos uses cmake3, BUILD _ SAMPLE to compile the sample program of the sample directory
```

Compile to generate 'libbcos-c-sdk.so'

```shell
-rw-r--r--   1 root  root  548896 12  9 17:27 libbcos-c-sdk.so
```

## 'macOs' Compile

```shell
cd bcos-c-sdk
mkdir build && cd build
cmake ../ -DBUILD_SAMPLE=ON # BUILD _ SAMPLE indicates the sample program for compiling the sample directory
```

Compile to generate 'libbcos-c-sdk.dylib'

```shell
-rw-r--r--   1 root  root  548896 12  9 17:27 libbcos-c-sdk.dylib
```

## 'Windows' Compile

```shell
# cmake
cmake -G "Visual Studio 15 2017" -A x64 ../ -DHUNTER_CONFIGURATION_TYPES=Release -DCMAKE_WINDOWS_EXPORT_ALL_SYMBOLS=ON

# Compile
MSBuild bcos-c-sdk.sln /p:Configuration=Release /p:Platform=x64
```

Compilation Results:

`build/Release/bcos-c-sdk.dll` # dynamic library

`build/Release/bcos-c-sdk.lib` # symbol table
