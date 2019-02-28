# 下载安装

FISCO BCOS generator依赖python, openssl, curl, nc工具。使用前请检查是否满足依赖，同时，需要先满足FISCO BCOS启动时的条件，参考[依赖安装](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-2.0.0/docs/manual/install.html?highlight=%E4%BE%9D%E8%B5%96#id4)

```bash
$ git clone https://github.com/FISCO-BCOS/generator.git
$ cd generator
$ bash ./scripts/install.sh
$ ./generator -h
```

在使用本工具时，需要在meta文件夹下放置`fisco-bcos`二进制程序（除demo命令外），`fisco-bcos`二进制程序的生成方式可以通过以下方式获取:

用户可以自由选择以下任一方式获取FISCO BCOS可执行程序。推荐从GitHub下载预编译二进制。

- 官方提供的静态链接的预编译文件，可以在Ubuntu 16.04和CentOS 7.2以上版本运行。
- 源码编译获取可执行程序，参考[源码编译](../manual/get_executable.md)。

```bash
# 准备fisco-bcos二进制文件
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh) -o ./meta
# 检查二进制是否可执行 执行下述命令，看是否输出版本信息
$ ./meta/fisco-bcos -v

