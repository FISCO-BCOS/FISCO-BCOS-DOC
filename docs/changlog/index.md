# 版本及兼容

## FISCO BCOS 1 系列

请参考： [FISCO BCOS 1.0 Release](<https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v1.0.0>)

## FISCO BCOS 2.0 release candidate 1 ([rc1](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v2.0.0-rc1))

**新增特性**

- [群组架构](../what_is_new.html#id2)
- [控制台](../what_is_new.html#id6)、[虚拟机](../what_is_new.html#id7)、[预编译合约](../what_is_new.html#id5)、[CRUD合约](../what_is_new.html#crud)、[密钥管理服务](../what_is_new.html#id8)、[准入控制](../what_is_new.html#id9)

**变更描述、兼容及升级说明**

* [FISCO BCOS 2.0 rc1](./2.0-rc1.md)



## FISCO BCOS 2.0 release candidate 2 ([rc2](https://github.com/FISCO-BCOS/FISCO-BCOS/releases/tag/v2.0.0-rc2))

**新增特性**

- [并行计算模型](../what_is_new.html#id4)
- [分布式存储](../what_is_new.html#id3)

**变更描述、兼容及升级说明**

* [FISCO BCOS 2.0 rc2](./2.0-rc2.md)



## 相关操作

**查看节点版本**

```shell
cd nodes/127.0.0.1/
./fisco-bcos --version
```

可看到版本号

```shell
FISCO-BCOS Version : 2.0.0-rc2
Build Time         : 20190425 14:34:50
Build Type         : Linux
Git Branch         : master
Git Commit Hash    : 4ddc1f61605c3b1450d0d348ec094f5668288eb5
```

**查看节点数据的版本**

通过查看节点的配置文件，可看到节点生成的文件版本

```shell
grep "supported_version" nodes/127.0.0.1/node0/config.ini
```

可看到节点数据的版本（若为空，表示默认为2.0.0-rc1版本）

```ini
supported_version=2.0.0-rc2
```



