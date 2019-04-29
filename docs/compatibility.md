# 版本及兼容

[FISCO BCOS Github Release List](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)

## 兼容性规则

FISCO BCOS的兼容包括

* 数据兼容：FISCO BCOS的二进制能跑在与其兼容节点的数据上
* 协议兼容：FISCO BCOS的二进制能与其兼容的节点进行通信

FISCO BCOS通过版本号定义兼容性：

* 主版本号不同，相互不兼容

* 次级版本号，向下兼容，高版本可跑在低版本的数据和协议中，但不会有高版本的功能

如：1.0与2.0的不兼容，1.3.1 兼容1.3.0， 2.0.0 rc2兼容2.0.0 rc1。

## 升级说明

**升级只能带来稳定性的提升，要使用新功能必须重新搭链。**

* 为了使用新功能的升级：不支持，必须重新搭链
* 为了稳定性提升的升级：支持次级版本号从低版本升级到高版本，不支持回滚，升级只能带来稳定性提升，无法使用新功能

## 相关操作

**查看节点版本**

``` shell
cd nodes/127.0.0.1/
./fisco-bcos --version
```

可看到版本号

``` shell
FISCO-BCOS Version : 2.0.0-rc2
Build Time         : 20190425 14:34:50
Build Type         : Linux
Git Branch         : master
Git Commit Hash    : 4ddc1f61605c3b1450d0d348ec094f5668288eb5
```

**查看节点数据的版本**

通过查看节点的配置文件，可看到节点生成的文件版本

``` shell
grep "supported_version" nodes/127.0.0.1/node0/config.ini
```

可看到节点数据的版本（若为空，表示默认为2.0.0-rc1版本）

``` ini
supported_version=2.0.0-rc2
```

