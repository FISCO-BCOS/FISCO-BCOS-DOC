# 下载安装

标签：``运维部署工具`` ``下载安装`` 

----
## 环境依赖

FISCO BCOS generator依赖如下：

| 依赖软件 | 支持版本 |
| :-: | :-: |
| python | 2.7+ 或 3.6+ |
| openssl | 1.0.2k+|
| curl | 默认版本 |
| nc | 默认版本 |

## 下载安装

下载

``` bash
$ git clone https://github.com/FISCO-BCOS/generator.git

# 若因为网络问题导致长时间无法下载，请尝试以下命令：
$ git clone https://gitee.com/FISCO-BCOS/generator.git
```

安装

``` bash
$ cd generator
$ bash ./scripts/install.sh
```

检查是否安装成功

``` bash
$ ./generator -h
# 若成功，输出 usage: generator xxx
```

## 拉取节点二进制

拉取最新fisco-bcos二进制文件到meta中

``` bash
$ ./generator --download_fisco ./meta
```

检查二进制版本

```bash
$ ./meta/fisco-bcos -v
# 若成功，输出 FISCO-BCOS Version : x.x.x-x
```

**PS**：[源码编译](../manual/get_executable.md)节点二进制的用户，只需要把编译出来的二进制放到``` meta ```文件夹下即可。