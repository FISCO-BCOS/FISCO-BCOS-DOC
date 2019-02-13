# 监控

fisco generator 生成的安装包提供了内置的监控脚本，用户可以通过对其进行配置，将节点的告警信息发送至指定地址。fisco generator会将monitir脚本放置于生成节点安装包的指定目录下，假设用户指定生成的文件夹名为data，则monitor脚本会在data目录下的monitor文件夹下

使用方式如下：

```
$ cd ./data/monitor
```

## 配置告警服务

用户使用前，首先需要配置告警信息服务，这里以server酱的微信推送为例，可以参考配置[server酱](http://sc.ftqq.com/3.version)

绑定自己的github账号，以及微信后，可以使用本脚本向微信发送告警信息，使用本脚本的-s命令 可以向指定微信发送告警信息

如果用户希望使用其他服务，可以修改monitor.sh中的alarm() {
    # do serverchan  
}函数，个性化配置为自己需要的服务


## help命令

使用monitor命令查看脚本使用方式

```shell
$ ./monitor.sh -h
Usage : bash monitor.sh
   -s : send alert to your address
   -m : monitor, statistics.  default : monitor .
   -f : log file to be analyzed.
   -o : dirpath
   -p : name of the monitored program , default is fisco-bcos
   -g : specified the group list to be analized
   -d : log analyze time range. default : 10(min), it should not bigger than max value : 60(min).
   -r : setting alert receiver
   -h : help.
 example :
   bash  monitor.sh -s yourwechatcode -o nodes -r your_name
   bash  monitor.sh -s yourwechatcode -m statistics -o nodes -r your_name
   bash  monitor.sh -s yourwechatcode -m statistics -f node0/log/log_2019021314.log -g 1 2 -r your_name
```

命令解释如下：

- -s 指定告警配置地址，可以配置为告警上报服务的ip
- -m 设定监控模式，可以配置为statistics和monitor两种模式，默认为monitor模式。
- -f 分析节点log
- -o 指定节点路径
- -p 设定监控上报名称，默认为fisco-bcos
- -g 指定监控群组，默认分析所有群组
- -d log分析时间范围，默认10分钟内的log，最大不超过60分钟
- -r 指定上报接收者名称
- -h 帮助命令

## 使用示例

- 使用脚本监控指定路径下节点，发送给接收者Alice

```shell
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -o alice/nodes -r Alice
```

- 使用脚本统计指定路径下节点信息，发送给接收者Alice

```shell
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -m statistics -o alice/nodes -r Alice
```

- 使用脚本统计指定路径下节点指定log指定群组1和群组2的信息，发送给接收者Alice

```shell
$ bash monitor.sh -s https://sc.ftqq.com/[SCKEY(登入后可见)].send -m statistics -f node0/log/log_2019021314.log -g 1 2 -o alice/nodes -r Alice
```