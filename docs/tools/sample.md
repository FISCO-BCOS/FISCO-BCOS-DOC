# 部署区块链sample

这里提供一个非常简单的例子, 用来示例如何使用物料包以最快的速度搭建一条在单台服务器上运行4个节点的FISCO BCOS的测试环境。

## 下载物料包
```
$ git clone https://github.com/FISCO-BCOS/fisco-package-build-tool.git
```

## 生成安装包
```
$ cd fisco-package-build-tool
$ ./generate_installation_packages.sh build
......
//中间会有FISCO-BCOS下载、编译、安装, 时间会比较久, 执行成功最终在当前目录下会生成build目录。
......
```
查看生成的build目录结构
```
$ tree -L 1 build
build/
├── 127.0.0.1_with_0.0.0.0_genesis_installation_package
├── stderr.log
└── temp
```
其中 127.0.0.1_with_0.0.0.0_genesis_installation_package 即是生成的安装包.

## 安装
假定需要将FISCO BCOS安装在当前用户home目录下, 安装的目录名fisco-bcos。
```
$ mv build/127.0.0.1_with_0.0.0.0_genesis_installation_package ~/fisco-bcos
$ cd ~/fisco-bcos
$ ./install_node.sh
..........
执行成功会生成build目录
```

## 启动  
```
$ cd build
$ ./start.sh
start node0 ...
start node1 ...
start node2 ...
start node3 ...
check all node status => 
node0 is running.
node1 is running.
node2 is running.
node3 is running.
```

## 验证  
```
$ tail -f node0/log/log_2018080116.log | egrep "seal"
INFO|2018-08-01 16:52:18:362|+++++++++++++++++++++++++++ Generating seal on5b14215cff11d4b8624246de63bda850bcdead20e193b24889a5dff0d0e8a3c3#1tx:0,maxtx:1000,tq.num=0time:1533113538362
INFO|2018-08-01 16:52:22:432|+++++++++++++++++++++++++++ Generating seal on5e7589906bcbd846c03f5c6e806cced56f0a17526fb1e0c545b855b0f7722e14#1tx:0,maxtx:1000,tq.num=0time:1533113542432
```

## 部署成功  
验证之后，一条简单的测试链搭建成功。

## 问题排查
参考附录FAQ。