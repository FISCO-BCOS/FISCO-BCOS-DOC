# 问题定位

## 操作常见问题
- Traceback (most recent call last):
  File "./generator", line 19, in <module>
    from pys.build import config
  File "/data/asherli/generator/pys/build/config.py", line 25, in <module>
    import configparse

系统缺少python configparser模块

```
$ pip install configparser
```