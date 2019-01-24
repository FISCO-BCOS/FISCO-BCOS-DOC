## --expand 扩容新节点加入现有群组

--expand命令为扩容新节点加入现有群组

nargs=2, 指定参数1.存放有原有群组信息的路径，2.生成安装包的路径  用户需配置mexpand.ini

给定原有group中节点的配置，和新节点的证书，生成安装包

```
$ vim ./conf/mexpand.ini
$ cp node0/node.crt ./meta/cert_127.0.0.1_30307.crt
$ cp /tmp/config.ini /tmp/group.1.genesis /tmp/group.1.ini ./expand
$ ./generator --expand ./expand ~/mydata
```
程序执行完成后，会在~/mydata文件夹下生成名为node_127.0.0.1_30307的文件夹，推送到对应服务器解压后，拷贝私钥到conf下即可启动节点

节点正常启动后，使用sdk将节点加入群组，完成扩容操作