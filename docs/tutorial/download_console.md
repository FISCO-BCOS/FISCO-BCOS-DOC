# 下载控制台

`download_console.sh`脚本提供获取所有版本的控制台的功能，默认获取[2.6+版本的控制台](../manual/console_of_java_sdk.md)，可通过如下命令获取最新的`download_console.sh`脚本：

```bash
cd ~/fisco && curl -#LO https://github.com/FISCO-BCOS/console/releases/download/v2.6.0/download_console.sh && bash download_console.sh
```

```eval_rst
.. note::
    - 如果因为网络问题导致长时间无法下载，请尝试 `cd ~/fisco && curl -#LO https://gitee.com/FISCO-BCOS/console/raw/master/tools/download_console.sh`
```

## 帮助

```bash

 bash download_console.sh -h

Usage:
    -c <console version>   Specify the downloaded console version, download the latest version of the console by default
    -v <solc version>      Download the console with specific solc version, default is 0.4, 0.5 and 0.6 are supported
    -h Help
e.g
    download_console.sh -v 0.6

```

## 选项介绍

`download_console.sh`脚本提供了`-c`和`-v`选项，分别用于指定下载的控制台版本号，以及控制台依赖的solidity编译器版本号。

### `-c`选项

指定下载的控制台版本号，默认拉取最新版本控制台(`2.6+`版本)，若基于[Web3SDK](../sdk/java_sdk.md)开发应用，需下载`1.x`版本的控制台将`solidity`代码转换为`java`代码，可通过该选项指定下载的控制台版本，下载`1.2.0`版本控制台的示例如下：

```bash
# 下载1.2.0版本的控制台
$ bash download_console.sh -c 1.2.0
```

### `-v`选项

控制台`1.1.0`版本开始支持使用0.6版本的`solidity`编译器(**FISCO BCOS底层需要升级到`v2.5.0`及其以上版本**)，`-v`选项指定下载的控制台基于的`solidity`编译器版本，目前支持`0.4`, `0.5`和`0.6`三个版本的`solidity`编译器，默认使用`0.4.25`版本编译器。 下载支持`solidity 0.6`版本的控制台示例如下：
```bash
# 下载支持0.6.0版本solidity编译器的控制台
$ bash download_console.sh -v 0.6
```