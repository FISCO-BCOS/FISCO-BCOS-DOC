# 连不上节点怎么办

## 调用Java SDK提示init channel network error

当出现上述错误时，可以按照如下顺序检查

- 检查编译生成的dist路径是否包含证书与私钥。(ca.crt,sdk.crt,sdk.key)
- 检查java版本是否符合Java SDK的要求(JDK1.8 至JDK 14)
- 检查是否有引入web3sdk等低版本依赖(web3sdk2.6.1以下版本与Java SDK存在依赖包冲突)[杰哥和小白补一下路径]()

## 提示connect node failed与提示证书配置错误

当出现上述错误时，可以按照如下顺序检查

- 调用的java程序所处网络是否可以与节点进行连通
- 确定SDK与配置连接节点是否属于同一机构(查看node.crt中的第一级是否相同)
- 查看Java SDK配置的conf文件夹是否正确创建[杰哥和小白补一下路径]()
- 检查节点和SDK是否均处于国密或非国密配置
