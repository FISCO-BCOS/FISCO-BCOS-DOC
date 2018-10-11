[toc]
# 节点监控脚本monitor.sh

## 介绍
使用物料包(**[fisco-package-build-tool](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-multichain/docs/tools/index.html)**)搭建的fisco-bcos环境, 最终在生成的build目录会有个monitor.sh脚本.  
该脚本可以用来监控节点是否正常启动或者节点所在的区块链是否正常工作, 在节点挂掉或者整个区块链无法正常工作情况下重启节点.  

## 使用
monitor.sh脚本可以直接执行. 
```
./monitor.sh 
[2018-10-10 15:28:11]{"id":67,"jsonrpc":"2.0","result":"0x3"}
[2018-10-10 15:28:11]{"id":68,"jsonrpc":"2.0","result":"0x2ca"}
[2018-10-10 15:28:11]OK! 0.0.0.0:8545 is working properly: height 0x3 view 0x2ca
```

### 提示
```OK! $config_ip:$config_port is working properly: height $height view $view```  节点正常工作.

```ERROR! $config_ip:$config_port does not exist```  节点进程不存在, 节点可能已经宕机, 会自动拉起节点.

```ERROR! Cannot connect to $config_ip:$config_port```    RPC请求失败, 节点可能已经宕机, 会自动拉起节点.

```ERROR! $config_ip:$config_port is not working properly: height $height and view $view no change```  节点块高、视图都没有变化, 整条链可能无法正常工作, 会自动重启节点.

## 配置crontab
 建议将monitor.sh添加到crontab中，设置为每分钟执行一次，并将输出重定向到日志文件。可以日常扫描日志中的```ERROR!```字段就能找出节点服务异常的时段, 也可以在节点挂掉情况下及时将节点重启。  
 在crontab的配置可以参考如下：
 ```
 */1  * * * * /data/app/fisco-bcos/build/monitor.sh >> /data/app/fisco-bcos/build/monitor.log 2>&1
 ```
 用户在实际中使用时将monitor.sh、monitor.log的路径修改即可。