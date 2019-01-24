## 证书生成

fisco generator提供自签证书操作。示例如下：

### 生成根证书 --chainca命令
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