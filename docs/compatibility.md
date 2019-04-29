# 版本及兼容性

[FISCO BCOS Github Release List](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)

## 兼容性说明

**FISCO-BCOS 2.0.0 RC2**

* 兼容 2.0.0 RC1版本，在老的节点数据目录下，直接将RC1的二进制替换成RC2的即可使用。能带来稳定性和性能的提升，**但不会开启RC2的新功能**（并行交易、分布式存储）
* 不兼容 FISCO BCOS 1.x

**FISCO-BCOS 2.0.0 RC1**

* 不兼容 FISCO BCOS 1.x

**FISCO-BCOS 1.x**

* 不兼容 FISCO BCOS 2.x

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

