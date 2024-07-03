# 2. Monitoring tools

Tags: "monitor" "monitor"

----

## light_monitor.sh

'FISCO-BCOS 3.0 'blockchain lightweight monitoring tool can monitor whether the blockchain is working properly, and also provides a simple way to access the user alarm system

- Monitor if consensus is normal
- Monitor whether the block synchronization is normal
- Monitor disk space
- Connect to the alarm system and send alarm information

### 使用

Help:
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

**Parameters:**

- `-g`: The id of the monitored group. When 'rpc' is connected to multiple groups, you can deploy multiple light _ monitor.sh to monitor different groups
- `-i`: rpc ip
- `-p`: rpc port
- `-t`: The threshold of the block synchronization alarm. If the block height difference between consensus nodes exceeds the threshold, consensus or block synchronization is abnormal. The default value is' 30'
- `-d`: Directory to be monitored for disk capacity
- `-T`: The disk alarm threshold. If the percentage of disk space is less than this value, an alarm is triggered. The default value is 5%
- `-h`: Help Information

#### Status Description

**Parameters:**

- $config_ip: rpc ip
- $config_port：rpc port
- $group: group id
- $height: Block height

**```OK! $config_ip:$config_port $node:$group is working properly: height $height```**

Group '${group}'Normal work, consensus module / block synchronization works normally

**```ERROR! Cannot connect to $config_ip:$config_port ${group}, method: xxxx```**

Failed to call the 'rpc' interface 'xxxx', the 'rpc' service is down, and a serious error occurs. Restart the 'rpc' service

**```ERROR! Consensus timeout $config_ip:$config_port ${group}:${node}```**

**Group consensus timeout, critical error on consecutive occurrences**。
Check whether the network connection is normal。

**```ERROR! insufficient disk capacity, monitor disk directory: ${dir}, left disk space percent: ${disk_space_left_percent}%```**  

Insufficient disk space, remaining '${disk_space_left_percent}% 'of space

To continuously monitor the status of blockchain nodes, configure 'light _ monitor.sh' to 'crontab' for periodic execution

```shell
# Execute once per minute to check whether the node is started normally, normal consensus, and whether there is critical error printing
*/1  * * * * /data/app/127.0.0.1/light_monitor.sh >> /data/app/127.0.0.1/light_monitor.log 2>&1
```

'light _ monitor.log 'saves the output of' light _ monitor.sh'

**You need to modify the path in the example based on the actual deployment**

### docking alarm system

- Interface
'light _ monitor.sh 'interfaces with alarm system. The default implementation is as follows:

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

 'light _ monitor.sh 'The function is called at all critical errors triggered by the execution, and the error message is used as an input parameter. The user can call the API of the monitoring platform to send the error message to the alarm platform

- Example

 Suppose the user's alarm system

- API:
        `http://127.0.0.1:1111/alarm/request`
    POST parameter:
        ```{'title':'Alarm Subject','alert_ip':'Alarm Server IP', 'alert_info':'Alarm content'}```

 Modify the alarm function:

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


## **Node Monitoring**

'FISCO-BCOS 3.0 'blockchain monitoring tool, you can monitor the blockchain block height and other indicators, displayed in the graphical interface

The components involved include grafana(Used to show indicators),prometheus(Used to collect indicator information),mtail(Used to analyze blockchain log information acquisition metrics).

###  Installation and construction

The monitoring tool can choose whether to deploy with the block chain when building it. The relevant parameters are as follows(For other parameters, please refer to [build _ chain.sh one-click chain building tool](./build_chain.md)):

### **'m 'Node Monitoring Options [**Optional**]**

Optional parameter. When node monitoring is enabled for blockchain nodes, you can use the '-m' option to deploy nodes with monitoring. If this option is not selected, only nodes without monitoring are deployed。

An example of deploying an Air version blockchain with monitoring enabled is as follows:

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
Prompt All completed.Files in nodes, indicating that the block chain node file has been generated

### Use process

#### Step 1. Start the FISCO BCOS chain

- Start all nodes

```shell
bash nodes/127.0.0.1/start_all.sh
```
Successful startup will output the following information。Otherwise use 'netstat -an|grep tcp 'check machine' 30300 ~ 30303,20200 ~ 20203 'ports are occupied。

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

#### Step 2. Start the monitoring script

Start Node Monitoring

```shell
sh nodes/monitor/start_monitor.sh
```

#### Step 3. Log in to grafana according to the prompt and view the indicators

The URL startup script prints the corresponding address. The default username and password are admin / admin([github source code](https://github.com/FISCO-BCOS/FISCO-BCOS/blob/master/tools/template/Dashboard.json))and configure the prometheus source(http://ip:9090/)You can view the real-time display of each indicator。