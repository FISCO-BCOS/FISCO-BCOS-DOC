# 日志清理脚本rmlogs.sh

## 介绍
使用物料包(**[fisco-package-build-tool](https://fisco-bcos-documentation.readthedocs.io/zh_CN/feature-multichain/docs/tools/index.html)**)搭建的fisco-bcos环境, 最终在生成的build目录会有个rmlogs.sh脚本。
可以用来删除fisco-bcos生成的日志文件。

## 使用
```
bash rmlogs.sh
或者
bash rmlog.sh 需要保留日志的天数
```
rmlog.sh默认会删除三天之前生成的所有的日志文件, 可以通过传入需要保留的日志的天数来修改这个规则。

## crontab配置
建议将rmlogs.sh配置到crontab中定期执行, 防止fisco-bcos打印过多的日志导致磁盘爆满引起其他的各种问题。  crontab设置可以参考如下, 每天执行一次。
```
0 0 * * * /data/app/fisco-bcos/build/rmlogs.sh >> /data/app/fisco-bcos/build/rmlogs.log 2>&1
```