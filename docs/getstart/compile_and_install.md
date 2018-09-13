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

## 一键安装

切换到FISCO-BCOS目录下，执行一键安装脚本

``` shell
cd FISCO-BCOS 
sh build.sh
```

检查是否安装成功

```shell
fisco-bcos --version 
#成功: FISCO-BCOS version x.x.x
```

