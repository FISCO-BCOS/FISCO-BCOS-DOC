# 连接失败问题 - Failed to connect to nodes

若SDK连接节点失败，抛出错误`Failed to connect to nodes: [Connection refused:`，可从以下思路排查:

- **检查节点进程:** 通过`ps aux | grep fisco-bcos`命令检查节点进程是否启动
- **检查节点监听IP:** 若SDK与节点处于不同机器，检查节点的channel服务的监听端口`channel_listen_ip`(位于SDK直连节点的config.ini配置文件中)是否为`0.0.0.0`或外网IP(**注:** 此外网IP非云服务器的虚拟IP，必须是机器的网卡IP)
- **检查SDK的连接配置:** 检查SDK配置的节点连接是否正确，Web3SDK的节点连接配置参考[这里](../sdk/java_sdk.html#spring)；Java SDK的节点连接配置参考[这里](../sdk/java_sdk/configuration.html#id6)
- **检查SDK与节点之间连通性:** 下载telnet工具，使用命令`telnet ${nodeIP}:${channel_listen_port}`检查SDK与节点之间是否可连通，其中`${nodeIP}`为节点IP，`${channel_listen_ip}`为节点Channel服务的监听端口，可通过节点目录下的`config.ini`配置文件中`rpc.channel_listen_port`配置选项获取，若SDK与节点之间不连通，请检查是否开启了防火墙/安全组策略，需要在防火墙/安全组中开放FISCO BCOS节点所使用的channel端口。

# 连接失败问题 - Java SDK握手失败

若Java SDK连接节点时，显示握手失败，请按以下步骤排查: 
- **检查是否拷贝证书**: 若没有配置证书，请参考[Java SDK快速入门](../sdk/java_sdk/quick_start.html#sdk)配置SDK证书
- **检查SDK与直连节点是否处于相同机构**: FISCO BCOS v2.5.0版本起，限制SDK只能连接本机构的节点，请确保SDK证书与直连节点证书属于相同机构
- **检查JDK版本**: 请确认JDK版本不小于JDK 1.8且不大于JDK 14，具体可参考[这里](./java_sdk.html#java)
- **检查证书配置是否正确**: 检查证书是否拷贝到默认的配置路径，请参考[这里](./java_sdk.html#id1).
- **检查是否Jar包冲突**: 请参考[这里](./java_sdk.html#ssl)


# 连接失败问题 - Web3SDK握手失败

若Web3SDK连接节点时，显示握手失败，请按以下步骤排查: 

- **检查证书配置**: 若没有配置证书，请参考[Web3SDK证书配置](../sdk/java_sdk.html#id2)配置SDK证书
- **检查SDK与直连节点是否处于相同机构**: FISCO BCOS v2.5.0版本起，限制SDK只能连接本机构的节点，请确保SDK证书与直连节点证书属于相同机构
- **检查JDK版本**: Web3SDK的java版本要求请参考[这里](./sdk.html#java)
- 参考[Web3SDK常见的异常启动问题](./sdk.html#id1)排查
