# 下载安装

## 安装

Fisco generator依赖python, openssl, curl, nc工具。使用前请检查是否满足依赖，同时，需要先满足FISCO BCOS启动时的条件，参考[依赖安装](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-2.0.0/docs/manual/install.html?highlight=%E4%BE%9D%E8%B5%96#id4)

在使用本工具时，需要用户已经生成所需的`fisco-bcos`二进制文件（除demo命令外），`fisco-bcos`二进制文件的生成方式可以通过获取:

```bash
# 准备fisco-bcos二进制文件
$ bash <(curl -s https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/release-2.0.1/tools/ci/download_bin.sh)
# 检查二进制是否可执行 执行下述命令，看是否输出版本信息
$ ./bin/fisco-bcos -v
```

```eval_rst
.. 注解::
    二进制文件的获取可以参考 源码编译_
.. 源码编译_: https://github.com/FISCO-BCOS/generator
```

```shell
$ git clone https://github.com/FISCO-BCOS/generator.git
$ cd generator
$ bash ./scripts/install.sh
$ ./generator -h
```

## 快速开始

```shell
$ git clone https://github.com/FISCO-BCOS/generator.git
$ cd generator
$ ./generator --demo
```

此操作会在执行目前的所有操作

1. 按照./conf/mchain.ini中的配置在./meta下生成证书
2. 按照./conf/mchain.ini的配置在./data下生成安装包
3. 拷贝./meta下的私钥至./data的安装包
4. 按照./conf/expand.ini中的配置在./meta下生成证书
5. 按照./conf/expand.ini的配置在./expand下生成扩容安装包
6. 按照./conf/mgroup.ini的配置在./data下生成group2的相关配置

操作完成后，用户可以:

1. 在./data下运行start_all.sh启动所有节点，并查看节点配置
2. 在./expand下查看扩容生成的节点
3. 在./data下查看group2的相关信息