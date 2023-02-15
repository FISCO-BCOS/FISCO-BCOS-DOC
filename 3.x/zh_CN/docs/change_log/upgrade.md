# 升级指南
FISCO BCOS 版本迭代，为支持版本之间的兼容升级，设计了[兼容性方案](../design/compatibility.md), 支持可灰度升级，且灰度升级过程中，系统可以正常共识、出块。

具体系统版本升级步骤如下：
1. 升级二进制：停止需要升级版本的节点，需将所有节点的二进制逐步替换为当前版本。为不影响业务，替换过程能够以灰度方式进行，逐个替换并重启节点。替换过程中，当前的链仍然会以旧的数据兼容版本号的逻辑继续执行。
2. 升级数据兼容版本号：当所有节点二进制替换完成并重启后，需用控制台修改数据兼容版本号为当前版本，步骤如下：
- 通过控制台连接节点，执行升级兼容版本号命令：```setSystemConfigByKey compatibility_version 3.x.x```

```
[group0]: /apps>  setSystemConfigByKey compatibility_version 3.x.x
{
    "code":0,
    "msg":"success"
}

注：若开启权限治理功能，需要使用 setSysConfigProposal 命令
```
- 设置成功，再次查询，得到当前版本已升级

``` 
[group0]: /apps>  getSystemConfigByKey compatibility_version
3.x.x
```

当前链已经完成升级，至此，**链开始以新的逻辑继续运行**，并支持了新的特性。