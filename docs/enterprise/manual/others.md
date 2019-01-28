# 其他命令

## -h/--help

用户可以使用-h或--help命令查看帮助菜单

使用示例：
```
$ ./generator -h
```
使用后会显示相关提示
```
$ ./generator -h
usage: generator [-h] [--version] [--build data_dir]
                 [--expand conf_dir, data_dir conf_dir, data_dir]
                 [--create data_dir] [--chainca chain_dir]
                 [--agencyca agency_dir chain_dir agency_name]
                 [--nodeca node_dir agency_dir node_name] [--gm] [--demo]
                 [--certbuild cert_dir] [--certexpand cert_dir]
                 [--deploykey cert_dir, pkg_dir cert_dir, pkg_dir]
                 [--combine config.ini, config.ini config.ini, config.ini
```

## --combine

使用--combine命令可以合并两个config.ini中的p2p section

如 A目录下的config.ini文件的p2p section为
```
[p2p]
listen_ip = 127.0.0.1
listen_port = 30300
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 127.0.0.1:30302
node.3 = 127.0.0.1:30303
```

B目录下的config.ini文件的p2p section为
```
[p2p]
listen_ip = 127.0.0.1
listen_port = 30303
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30303
node.2 = 192.167.1.1:30300
node.3 = 192.167.1.1:30301
```
使用此命令后会成为：
```
[p2p]
listen_ip = 127.0.0.1
listen_port = 30304
node.0 = 127.0.0.1:30300
node.1 = 127.0.0.1:30301
node.2 = 192.167.1.1:30302
node.3 = 192.167.1.1:30303
node.4 = 192.167.1.1:30300
node.5 = 192.167.1.1:30301
```

使用示例
```
$ ./generator --combine ~/mydata/node_A/config.ini  ~/mydata/node_B/config.ini
```
使用成功后会将node_A和node_B的config.ini中p2p section合并与 ~/mydata/node_B/config.ini的文件中

## --deploykey

使用--deploykey可以将路径下名称相同的节点私钥导入到生成好的安装包中。

使用示例:

```
$./generator --deploykey ./cert ./data
```

如./cert下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的文件夹，文件夹中有节点私钥文件node.key

./data下有名为node_127.0.0.1_30300，node_127.0.0.1_30301的安装包

执行完成后可以将./cert下的对应的节点私钥导入./data的安装包中

## --version

使用--version命令查看当前部署工具的版本号
```
$ ./generator --version
```

## --gm

--gm命令为国密选项，此命令可以与任意命令组合，使用时打开国密开关

使用示例：

生成国密链
```
./generator --build ~/mydata --gm
```
扩容国密节点
```
./generator --expand ./expand ~/mydata --gm
```
生成国密证书
```
./genrator --nodeca ./agency_dir node_dir(SET) node_name --gm
```