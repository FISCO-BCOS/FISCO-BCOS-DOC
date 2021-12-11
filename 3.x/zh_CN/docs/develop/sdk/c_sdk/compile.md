# 源码编译

标签：``c-sdk`` ``源码编译``

----------

- `Windows`编译

```shell
cmake -G "Visual Studio 15 2017" -A x64 ../ -DHUNTER_CONFIGURATION_TYPES=Release
```

cmake成功之后,在`build`目录下生成windows工程文件:

```shell
bcos-c-sdk.sln
```

使用`Visual Studio`打开`bcos-c-sdk.sln`:

```shell
文件 => 打开 => 项目/解决方案
```

选择`bcos-c-sdk.sln`

打开成功之后,右键点击解决方案`ALL BUILD`,开始编译
编译生成`build/Release/c-sdk.lib`

- `Ubuntu`、`CentOS`、`macOS`编译

```shell
cd bcos-c-sdk
mkdir build && cd build
cmake ../  # centos使用cmake3 ../
```

编译生成`libc-sdk.a`

```shell
-rw-r--r--   1 root  root  548896 12  9 17:27 libc-sdk.a
```
