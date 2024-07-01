# Version Upgrade Guide
FISCO BCOS version iteration, designed to support compatibility upgrades between versions [Compatibility Scheme](../design/compatibility.md), support can be gray scale upgrade, and gray scale upgrade process, the system can be normal consensus, out of the block。

Specific system version upgrade steps are as follows:
1. Upgrade the binary: Stop the nodes that need to be upgraded, and gradually replace the binary of all nodes with the current version.。In order not to affect the business, the replacement process can be carried out in grayscale, replacing and restarting nodes one by one.。During the replacement process, the current chain continues to execute with the logic of the old data-compatible version number.。
2. Upgrade the data compatible version number: After the binary replacement of all nodes is completed and restarted, you need to use the console to modify the data compatible version number to the current version. The steps are as follows:
- Connect to the node through the console and run the upgrade compatibility command: "'setSystemConfigByKey compatibility _ version 3.x.x"'

```
[group0]: /apps>  setSystemConfigByKey compatibility_version 3.x.x
{
    "code":0,
    "msg":"success"
}

Note: If the permission governance function is enabled, you need to use the setSysConfigProposal command
```
- Set successfully, query again, get the current version has been upgraded

``` 
[group0]: /apps>  getSystemConfigByKey compatibility_version
3.x.x
```

The current chain has been upgraded, so far,**The chain continues to run with new logic**and supports new features。