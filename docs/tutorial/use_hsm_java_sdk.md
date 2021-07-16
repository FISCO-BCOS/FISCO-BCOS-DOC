# 使用基于硬件加密模块的Java SDK
当您的应用需要与使用2.8.0-hsm版本的节点建立，需要使用基于硬件加密模块的Java SDK

## 准备环境
请确保将符合了GMT0018-2012规范的头文件和库文件安装在了动态库默认的搜索路径中，请确保动态库libswsds.so的版本为v1.4.06。
1. 确保头文件``swsds.h``在目录``/usr/include``中，并保证所有用户都有读权限。
2. 如果您使用的是Ubuntu操作系统，请将库文件``libswsds.so``放在``/usr/lib``目录下，保重用户具有读和执行权限。
3. 如果您使用的是Centos操作系统，请将库文件``libswsds.so``以及``/lib64``目录下，保证用户具有读和执行权限。

下载swssl，请在``-H``后输入连接加密机的正确IP地址、端口、密码。
```bash
curl -#LO https://raw.githubusercontent.com/MaggieNgWu/FISCO-BCOS/newoct/tools/download_swssl.sh
bash download_swssl.sh -H 192.168.10.12,10000,XXXXX
```

## 构建项目
分别以Console和java-sdk-demo为例。

### 构建Console
