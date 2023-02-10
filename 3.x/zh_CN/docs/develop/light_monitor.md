# 监控工具

标签：``监控`` ``monitor``

----

## light_monitor.sh

`FISCO-BCOS 3.0`区块链轻量级监控工具，可以监控区块链是否正常工作，也提供简单的接入用户告警系统的方式.

- 监控共识是否正常.
- 监控区块同步是否正常.
- 监控磁盘空间.
- 对接告警系统，发送告警信息.

### 使用

帮助:
`bash light_monitor.sh -h`

```shell
$ bash light_monitor.sh -h
Usage:
Optional:
    -g                  [Require]  group id
    -i                  [Require]  rpc server ip
    -p                  [Require]  rpc server port
    -t                  [Optional] block number far behind warning threshold, default: 30
    -d                  [Optional]  disk directory to be monitor
    -T                  [Optional] disk capacity alarm threshold, default: 5%
    -h                  Help.
Example:
    bash light_monitor.sh -i 127.0.0.1 -p 20200 -g group0
    bash light_monitor.sh -i 127.0.0.1 -p 20200 -g group0 -d /data -T 10
```

**参数:**

- `-g`: 监控的群组id，`rpc`接入多个群组时，可以部署多个light_monitor.sh，分别监控不同群组
- `-i`: rpc ip
- `-p`: rpc port
- `-t`: 区块同步告警的阈值，共识节点之间的区块高度差值超过该阈值，说明共识或者区块同步异常，默认值`30`
- `-d`: 磁盘容量需要监控的目录
- `-T`: 磁盘告警阈值，监控的磁盘剩余空间百分比小于该值时，触发告警，默认值`5%`
- `-h`: 帮助信息

#### 状态描述

**参数：**

- $config_ip: rpc ip
- $config_port：rpc port
- $group: 群组id
- $height: 区块高度

**```OK! $config_ip:$config_port $node:$group is working properly: height $height```**

群组`${group}`正常工作, 共识模块/区块同步正常工作

**```ERROR! Cannot connect to $config_ip:$config_port ${group}, method: xxxx```**

调用`rpc`接口`xxxx`失败，`rpc`服务宕机, 严重错误，此时需要重启`rpc`服务

**```ERROR! Consensus timeout $config_ip:$config_port ${group}:${node}```**

**群组共识超时，连续出现时为严重错误**。
排查网络连接是否正常。

**```ERROR! insufficient disk capacity, monitor disk directory: ${dir}, left disk space percent: ${disk_space_left_percent}%```**  

磁盘空间不足，剩余`${disk_space_left_percent}%`的空间

为了能够持续监控区块链节点的状态, 将`light_monitor.sh`配置到`crontab`定期执行.

```shell
# 每分钟执行一次，查看节点是否正常启动, 正常共识, 有无关键错误打印
*/1  * * * * /data/app/127.0.0.1/light_monitor.sh >> /data/app/127.0.0.1/light_monitor.log 2>&1
```

`light_monitor.log`保存`light_monitor.sh`的输出

**用户需要根据实际部署修改示例中的路径.**

### 对接告警系统

- 接口
`light_monitor.sh`对接告警系统的接口`alarm`, 默认实现如下：

```shell
alarm() {
        echo "$1"
        alert_msg="$1"
        alert_ip=$(/sbin/ifconfig eth0 | grep inet | grep -v inet6 | awk '{print $2}')
        alert_time=$(date "+%Y-%m-%d %H:%M:%S")

        # TODO: alarm the message, mail or phone

        # echo "[${alert_time}]:[${alert_ip}]:${alert_msg}"| mail -s "fisco-bcos alarm message" xxxxxx@qq.com
}
```

 `light_monitor.sh`执行触发的关键错误处都会调用该函数, 并将错误信息作为入参, 用户可以调用监控平台的API将错误信息发送至告警平台.

- 示例

 假设用户的告警系统

- API:
        `http://127.0.0.1:1111/alarm/request`
    POST参数:
        ```{'title':'告警主题','alert_ip':'告警服务器IP', 'alert_info':'告警内容'}```

 修改`alarm`函数：

```shell
alarm() {
        echo "$1"
        alert_msg="$1"
        alert_ip=$(/sbin/ifconfig eth0 | grep inet | grep -v inet6 | awk '{print $2}')
        alert_time=$(date "+%Y-%m-%d %H:%M:%S")

        # TODO: alarm the message, mail or phone

        curl -H "Content-Type: application/json" -X POST --data "{'title':'alarm','alert_ip':'${alert_ip}','alert_info':'${alert_msg}'}" http://127.0.0.1:1111/alarm/request
}
```



## **节点监控**

`FISCO-BCOS 3.0`区块链监控工具，可以监控区块链区块高度等一些指标,在图形化的界面显示.

涉及的组件包括grafana(用于展示指标),prometheus(用于采集指标信息),mtail(用于分析区块链日志信息获取指标).

###  安装搭建

​		监控工具可以在搭建区块链的时候选择是否随着搭建一起部署,相关参数如下(其他参数可以参考区块链网络搭建教程):

	### **`m`节点监控选项[**Optional**]**

可选参数，当区块链节点启用节点监控时，可通过`-m`选项来部署带监控的节点，若不选择该选项则只部署不带监控的节点。

部署开启监控的Air版本区块链示例如下：

```shell
[root@172 air]# bash build_chain.sh -p 30300,20200 -l 127.0.0.1:4 -o nodes -e ./fisco-bcos -t ./mtail -m
[INFO] Use binary ./fisco-bcos
[INFO] Use binary ./mtail
[INFO] Generate ca cert successfully!
Processing IP:127.0.0.1 Total:4
[INFO] Generate nodes/127.0.0.1/sdk cert successful!
[INFO] Generate nodes/127.0.0.1/node0/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node1/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node2/conf cert successful!
[INFO] Generate nodes/127.0.0.1/node3/conf cert successful!
[INFO] Begin generate uuid
[INFO] Generate uuid success: 1357cd37-6991-44c0-b14a-5ea81355c12c
[INFO] Begin generate uuid
[INFO] Generate uuid success: c68ebc3f-2258-4e34-93c9-ba5ab6d2f503
[INFO] Begin generate uuid
[INFO] Generate uuid success: 5311259c-02a5-4556-9726-daa1ee8fbefc
[INFO] Begin generate uuid
[INFO] Generate uuid success: d4e5701b-bbce-4dcc-a94f-21160425cdb9
==============================================================
[INFO] fisco-bcos Path     : ./fisco-bcos
[INFO] Auth Mode           : false
[INFO] Start Port          : 30300 20200
[INFO] Server IP           : 127.0.0.1:4
[INFO] SM Model            : false
[INFO] output dir          : nodes
[INFO] All completed. Files in nodes
```
提示All completed. Files in nodes,说明生成完区块链节点文件.

### 使用流程

#### 第一步. 启动FISCO BCOS链

- 启动所有节点

```shell
bash nodes/127.0.0.1/start_all.sh
```
启动成功会输出如下信息。否则请使用`netstat -an |grep tcp`检查机器`30300~30303, 20200~20203`端口是否被占用。

```shell
try to start node0
try to start node1
try to start node2
try to start node3
 node3 start successfully pid=36430
 node2 start successfully pid=36427
 node1 start successfully pid=36433
 node0 start successfully pid=36428
```

#### 第二步. 启动监控脚本

启动节点监控

```shell
sh nodes/monitor/start_monitor.sh
```

#### 第三步.根据提示登录grafana,查看指标

url启动脚本会打印对应的地址,默认用户名密码为admin/admin登录页面后导入Dashboard（[github源码](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/template/Dashboard.json)）

,配置prometheus源(http://ip:9090/),就可以查看各个指标实时展示。
