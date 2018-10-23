# 程序部署
## 获取代码

新建一个目录，例如/mydata

```shell
sudo mkdir -p /mydata
sudo chmod 777 /mydata
cd /mydata
```
clone 源码

``` shell
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
```

## 安装FISCO-BCOS

切换到FISCO-BCOS目录下，执行FISCO-BCOS安装脚本。`build.sh`默认从[GitHub](https://github.com/FISCO-BCOS/FISCO-BCOS/releases)下载官方`fisco-bcos`程序，使用`-b`选项从源码编译。

```bash
cd FISCO-BCOS 
bash build.sh
# build.sh Usage:
#   -b If set, build fisco-bcos from source and install. Default download fisco-bcos from GitHub
#   -g If set, build guomi-fisco-bcos build and install
#   -h Help
```

检查是否安装成功

```bash
fisco-bcos --version 
#成功: FISCO-BCOS version x.x.x
```

