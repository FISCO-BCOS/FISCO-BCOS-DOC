# 运维部署工具无法使用

标签：``没有网络`` ``找不到pip`` ``运维部署工具``

----

## 没有网络
**问题描述**

在没有网络的情况下，如何使用运维部署工具？

**解决方法**

运维部署工具依赖configparser等模块，建议在可以连接外网的情况下配置运维部署工具，并生成节点配置文件夹，推送至内网服务器。
<hr>

## 找不到pip
**问题描述**

运维部署工具使用时出现找不到pip的错误。

**解决方法**

运维部署工具依赖python pip，需要使用以下命令安装：
```
$ bash ./scripts/install.sh
```
<hr>

## 找不到configparse
**问题描述**

运维部署工具使用时出现找不到configparse模块的错误。
```
Traceback (most recent call last):
   File "./generator", line 19, in <module>
    from pys.build import config
   File "/data/asherli/generator/pys/build/config.py", line 25, in <module>
     import configparse
```

**解决方法**

系统缺少python configparser模块，请按照以下命令安装：

```bash
  $ pip install configparser
```