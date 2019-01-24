## --build 部署新节点及群组

-- build命令为部署新节点及群组的操作


指定参数为安装包输出路径，用户需配置mchain.ini，并在meta下配置相关节点的证书 ----可用于区块链生成后，创建新群组和新节点的场景

操作完成后，给定节点证书，和节点信息（端口号，ip），生成安装包，并加入一个群里，将打好的包生成到指定目录

操作示例
```
$ cp node0/node.crt ./meta/cert_127.0.0.1_30300.crt
...
$ cp noden/node.crt ./meta/cert_127.0.0.1_3030n.crt
$ vim ./conf/mchain.ini
$ ./generator --build ~/mydata
```
程序执行完成后，会在~/mydata文件夹下生成多个名为node_hostip_port的文件夹，推送到对应服务器解压后，拷贝私钥到节点conf下即可启动节点