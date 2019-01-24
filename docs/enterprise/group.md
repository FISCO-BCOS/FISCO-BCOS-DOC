## --create 划分新群组

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