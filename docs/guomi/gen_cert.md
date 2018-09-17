# 证书生成

FISCO BCOS网络采用面向CA的准入机制，国密版FISCO BCOS要生成三级证书，包括：链证书，机构证书和节点证书。

## 生成链证书

FISCO BCOS提供generate_chain_cert.sh脚本生成链证书：

```bash
# 进入脚本所在目录(设FISCO-BCOS位于~/mydata目录)
cd ~/mydata/FISCO-BCOS/tools/scripts

# 在~/mydata目录下生成国密版FISCO BCOS链证书
#--------------------------------------------------------
#-o : 生成的链证书所在路径，这里是~/mydata
#-g ：生成国密链证书，这里必须设置
# 注: (若要手动输入链证书信息，请在下面命令最后加上-m选项)
#--------------------------------------------------------
$ bash ./generate_chain_cert.sh -o ~/mydata -g
# 此时/mydata目录下生成链证书私钥gmca.key、证书gmca.crt和参数gmsm2.param
$ ls ~/mydata/
gmca.crt  gmca.key  gmsm2.param

# 查看链证书脚本generate_chain_cert.sh使用方法:
$ bash ./generate_chain_cert.sh -h
Usage:
 -o <ca dir> Where ca.crt ca.key generate  # CA证书生成目录
 -m Input ca information manually    # 手动输入CA信息
 -g Generate chain certificates with guomi algorithms #生成国密版链证书
 -d The Path of Guomi Directory  #GM证书生成脚本所在路径，默认为FISCO-BCOS/cert/GM
 -h This help
Example:
 ./generate_chain_cert.sh -o /mydata -m  # 非国密版链证书生成用法，手动输入证书信息
 ./generate_chain_cert.sh -o /mydata     # 非国密版链证书生成用法，使用默认证书信息
guomi Example:
 ./generate_chain_cert.sh -o ~/mydata -m -g # 国密版链证书生成用法，手动输入证书信息
 ./generate_chain_cert.sh -o ~/mydata -g    # 国密版链证书生成用法，使用默认证书信息

```

## 生成机构证书

generate_agency_cert.sh 脚本用于生成机构证书：

```bash
# 进入脚本所在目录(设FISCO-BCOS位于~/mydata目录)
$ cd ~/mydata/FISCO-BCOS/tools/scripts

#--------------------------------------------------------
# -c: 链证书所在目录，这里是~/mydata
# -o: 生成的机构证书所在目录，这里是~/mydata
# -n: 机构证书名，这里是test_agency
# -g: 生成国密机构证书，这里必须设置
# 注: (若要手动输入机构证书信息，请在下面命令最后加上-m选项)
#--------------------------------------------------------
$ bash ./generate_agency_cert.sh -c ~/mydata -o ~/mydata -n test_agency -g 

# 此时~/mydata/test_agency目录下生成机构证书gmagency.crt和证书私钥gmagency.key
$ ls ~/mydata/test_agency/
gmagency.crt  gmagency.key  gmca.crt  gmsm2.param

# 查看generate_agency_cert.sh脚本用法
$ bash ./generate_agency_cert.sh -h
Usage:
 -c <ca dir> The dir of ca.crt and ca.key  # 指定颁发机构证书的CA证书和私钥所在目录
 -o <output dir> Where agency.crt agency.key generate # 指定机构证书输出目录
 -n <agency name> Name of agency  # 指定机构名
 -m Input agency information manually  # 若设置，表明手动输入机构证书信息，否则使用默认信息
 -g Generate agency certificates with guomi algorithms # 生成国密版机构证书
 -d The Path of Guomi Directory
 -h This help
Example:
 bash ./generate_agency_cert.sh -c /mydata -o /mydata -n test_agency # 非国密版机构证书生成示例
 bash ./generate_agency_cert.sh -c /mydata -o /mydata -n test_agency -m
guomi Example:          #国密版机构证书生成示例
 bash ./generate_agency_cert.sh -c ~/mydata -o ~/mydata -n test_agency -g
 bash ./generate_agency_cert.sh -c ~/mydata -o ~/mydata -n test_agency -m -g

```

## 生成节点证书

generate_node_cert.sh 脚本用于生成节点证书：

```bash
# 进入脚本所在目录(设FISCO-BCOS位于~/mydata目录)
$ cd ~/mydata/FISCO-BCOS/tools/scripts

# 使用~/mydata/test_agency目录下机构证书为节点node0颁发证书，生成的证书置于~/mydata/node0/data目录下
#--------------------------------------------------------
# -a: 机构证书名，这里是test_agency
# -d: 机构证书所在路径，这里是~/mydata/test_agency
# -n: 节点名称，这里是node0
# -o: 节点证书所在路径，这里是~/mydata/node0/data
# -s: sdk证书名，这里是sdk1
# -g: 生成国密节点证书，这里必须设置
# (若要手动输入节点证书信息，请在下面命令最后加上-m选项)
#--------------------------------------------------------
$ bash ./generate_node_cert.sh -a test_agency -d ~/mydata/test_agency -n node0 -o ~/mydata/node0/data -s sdk1 -g

# 此时在~/mydata/node0/data目录下生成节点证书&&客户端连接证书
 $ ls ~/mydata/node0/data -1
ca.crt      # 节点和客户端验证所需的CA证书
ca.key
client.keystore
gmagency.crt
gmca.crt
gmennode.crt
gmennode.key
gmnode.ca
gmnode.crt
gmnode.json
gmnode.key
gmnode.nodeid
gmnode.private
gmnode.serial
sdk1            #存储客户端连接节点证书
server.crt
server.key

# 设编译好的web3sdk位于~/mydata/web3sdk目录下，则将客户端证书拷贝到相应目录:
$ cp ~/mydata/node0/data/sdk1/* ~/mydata/web3sdk/dist/conf

# 查看节点证书脚本generate_node_cert.sh用法
$ bash ./generate_node_cert.sh  -h
Usage:
 -a <agency name> The agency name that the node belongs to # 为节点颁发证书的机构名
 -d <agency dir> The agency cert dir that the node belongs to # 机构证书所属目录
 -n <node name> Node name  # 节点名
 -o <output dir> Data dir of the node # 节点证书存放目录
 -m Input agency information manually # 手动输入节点证书信息
 -r The path of GM shell scripts directory 
 -g generate guomi cert # 产生国密版节点证书
 -s the sdk name to connect with the node # 产生国密版节点证书时，必须产生sdk证书，-s指定sdk名称
 -h This help
Example: # 非国密版节点证书生成示例
 bash ./generate_node_cert.sh -a test_agency -d /mydata/test_agency -n node0 -o /mydata/node0/data
 bash ./generate_node_cert.sh -a test_agency -d /mydata/test_agency -n node0 -o /mydata/node0/data -m
guomi Example: #国密版FISCO-BCOS节点证书生成示例
 bash ./generate_node_cert.sh -a test_agency -d ~/mydata/test_agency -n node0 -o ~/mydata/node0/data -s sdk1 -g
 bash ./generate_node_cert.sh -a test_agency -d ~/mydata/test_agency -n node0 -o ~/mydata/node0/data -m -s sdk1 -g

```

# SDK证书

生成节点证书时，会在节点目录下同时生成SDK证书，如上例中node0的sdk证书位于`/mydata/node0/data/sdk1`目录。
也可使用`gmsdk.sh` 脚本手动生成sdk证书，**但生成后需要**:

```bash
# 进入脚本所在目录(设FISCO-BCOS位于~/mydata目录)
$ cd ~/mydata/FISCO-BCOS/tools/cert/GM

# 执行国密版SDK生成脚本gmsdk
#--------------------------------------------------------
# 注: 生成SDK证书时，不能输入空口令，口令长度至少为6个字符
#--------------------------------------------------------
$ bash ./gmsdk.sh sdk1 #生成的sdk证书位于sdk1目录下

# 用新生成的sdk证书sdk要连接的节点相关证书(设sdk要连接node0)
$ cp sdk1/* ~/mydata/node0/data/

```

```eval_rst
.. admonition:: 注意事项

   FISCO-BCOS采用三级证书链【链证书--> 机构证书---> 节点证书】认证，同一条链的所有机构证书必须由同一个链证书颁发，节点间证书认证才能成功。
   
```

