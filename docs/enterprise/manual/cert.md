# 证书生成相关命令

fisco generator提供自签证书操作。示例如下：


## 生成根证书 --chainca命令
用户可以指定目录，生成根证书
```
$ ./genrator --chainca ./dir_chain_ca(SET)
or
$ ./genrator --chainca ./dir_chain_ca(SET) --gm
```
执行完成后用户可以在指定文件夹下看到根证书ca.crt 和私钥ca.key。或者是国密版本的根证书gmca.crt 和私钥gmca.key。
## 生成机构证书 --agencyca命令
用户可以指定机构证书目录，链证书存放目录和机构名称，生成机构证书
```
$ ./genrator --agencyca ./dir_agency_ca(SET) ./chain_ca_dir The_Agency_Name
or
$ ./genrator --agencyca ./dir_agency_ca(SET) ./chain_ca_dir The_Agency_Name --gm
```
执行完成后可以在./dir_agency_ca(SET)路径下生成名为The_Agency_Name的文件夹，包含相应的机构证书

## 生成sdk证书 --sdkca
用户可以指定sdk存放目录，机构证书存放目录，生成sdk证书
```
$ ./genrator --sdkca ./dir_sdk_ca(SET) ./dir_agency_ca
or
$ ./genrator --sdkca ./dir_sdk_ca(SET) ./dir_agency_ca --gm
```
执行完成后可以在./dir_sdk_ca(SET)路径下生成名为sdk的文件夹，包含相应的sdk证书

## 生成节点证书 --nodeca
用户可以指定机构证书目录，节点存放目录和节点名称，生成节点证书
```
$ ./genrator --nodeca ./agency_dir node_dir(SET) node_name
or 
$ ./genrator --nodeca ./agency_dir node_dir(SET) node_name --gm
```
执行完成后可以在node_dir(SET) 路径下生成节点证书

## 根据配置文件生成相应证书

fisco generator支持用户根据配置文件夹生成相应证书与相应私钥

### --certbuild

用户使用此命令，可以在指定目录下根据mchain.ini的相关配置生成相应节点证书与私钥，并放置生成的节点证书与./meta文件夹下，用户生成安装包后可以结合[--deploykey]命令导入私钥到安装包启动节点

**注意** 此命令会根据meta目录下存放的ca.crt和ca.key生成相应的节点证书，如果没有根证书会自动生成

```
$ ./genrator --certbuild ./cert
```

执行完成后会在./cert文件夹下生成节点的相关证书与私钥，并拷贝节点证书放置于./meta下

### --certexpand

用户使用此命令，可以在指定目录下根据mexpand.ini的相关配置生成相应节点证书与私钥，并放置生成的节点证书与./meta文件夹下，用户生成安装包后可以结合[--deploykey]命令导入私钥到安装包启动节点

```
$ ./genrator --certexpand ./cert
```

**注意** 由于扩容节点与链本身节点的根证书必须相同，此命令会根据meta目录下存放的(ca.crt和ca.key)或(agency.crt和agency.key)生成相应的节点证书，如果没有根证书抛出异常

执行完成后会在./cert文件夹下生成节点的相关证书与私钥，并拷贝节点证书放置于./meta下
