# 基础操作

## 部署新节点及群组 --build

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

## 扩容新节点 --expand

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

## 划分新群组 --create

--create命令为已有节点新建群组的操作

指定参数为生成的group.i.genesis和group.i.ini的存放路径

此命令会根据用户在meta下配置的证书，在指定目录生成新群组的配置

操作范例

```
$ cp node0/node.crt ./meta/cert_127.0.0.1_3030n.crt
...
$ vim ./conf/group.ini
$ ./generator --create ~/mydata
```
程序执行完成后，会在~/mydata文件夹下生成mgroup.ini中配置的group.i.genesis和group.i.ini

用户将生成的group.i.genesis和group.i.ini拷贝至对应节点的conf文件夹下，即可完成新群组划分操作