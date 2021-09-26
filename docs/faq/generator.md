# 一键部署工具

## 没有网络怎么使用运维部署工具

运维部署工具依赖configparser等模块，建议在可以连接外网的情况下配置运维部署工具，并生成节点配置文件夹，推送至内网服务器

## 运维部署工具使用时出现找不到pip

运维部署工具依赖python pip，使用以下命令安装：
```
$ bash ./scripts/install.sh
```

## 运维部署工具使用时出现找不到configparse

```
Traceback (most recent call last):
   File "./generator", line 19, in <module>
    from pys.build import config
   File "/data/asherli/generator/pys/build/config.py", line 25, in <module>
     import configparse
```

系统缺少python configparser模块，请按照以下命令安装：

```bash
  $ pip install configparser
```

## 节点或SDK使用的OpenSSL证书过期了，如何续期？

证书续期操作可以参考[证书续期操作](../manual/certificates.md#id9)

## 使用下载命令提示certificate verify failed

在 ./pys/tool/utils.py 这个文件的开头中加入如下两行

```python
import ssl
ssl._create_default_https_context = ssl._create_unverified_context
```
