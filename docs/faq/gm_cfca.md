# 使用CFCA国密证书部署FISCO BCOS  
- 作者：王礼辉 (牛津(海南)区块链研究院有限公司)  
## 概述  
- 最近因为业务的需要，在研究使用cfca证书部署fisco bcos。一开始打算部署非国密版，后来发现cfca现在不签发EC算法的证书，所以如果使用cfca证书，只能部署国密版fisco。在向cfca申请sm2国密算法证书时，发现cfca返回的测试环境证书对用户不是很友好，返回的加密证书私钥是加了密的，需要自己进行解密，过程比较折腾，所以写下这篇文章，希望对有共同需求的开发者有所帮助。
## 申请CFCA证书

- 生成签名证书私钥  
    * 用以下命令分别生成四个节点的私钥文件：node1.key, node2.key, node3.key, node4.key  
	openssl ecparam -out node.param -name secp256k1  
    openssl genpkey -paramfile node.param -out node.key
- 填写证书申请模板-CSR
    * 用以下命令分别生成四个节点的证书请求文件：node1.csr, node2.csr, node3.csr, node4.csr  
	openssl req -new -sha256 -subj "/CN=node_127.0.0.1_30300/O=fisco-bcos/OU=node" -key node1.key -out node1.csr  
	openssl req -new -sha256 -subj "/CN=node_127.0.0.1_30301/O=fisco-bcos/OU=node" -key node1.key -out node2.csr  
	openssl req -new -sha256 -subj "/CN=node_127.0.0.1_30302/O=fisco-bcos/OU=node" -key node1.key -out node3.csr  
	openssl req -new -sha256 -subj "/CN=node_127.0.0.1_30303/O=fisco-bcos/OU=node" -key node1.key -out node4.csr  
- 获取CFCA签名证书，加密证书，加密证书私钥  
    * 发送以下内容到support@cfca.com.cn向cfca申请测试环境国密证书  
	> 1、测试证书类型：OCA31  设备证书SM2双证；  
      2、密钥长度：SM2256；   
      3、证书数量 ：4；  
      4、IP: 127.0.0.1, 127.0.0.1, 127.0.0.1, 127.0.0.1；
	* 一般在一个工作日内会收到申请结果邮件，里面会返回每个证书文件的序列号，授权码，用来下载证书 
	* 打开下载证书地址https://cstest.cfca.com.cn/cgi-bin/compoundCertDownload/v_input.do ,输入收到的证书序列号，授权码和上       面步骤生成的csr文件，就会生成签名证书，加密证书和加密私钥的字符串，分别保存成文件gmnode.crt, gmennode.crt, gmennode.key     

- 解析加密证书私钥  
    * 为了安全考虑，cfca在返回加密私钥时是加了密的，需要把加密私钥解密才能使用，以下是解密过程  
	> 1，获取密文，去掉“,”和前缀的数字字符后，进行Base64解码。  
	2，再进行ASN.1解码（ASN.1结构参考如下），即可取得“加密后的加密证书密钥对数据”。  
	>> EncryptedPrivatekey ::= SEQUENCE {
	      version INTEGER,  
		  encryptedPrivateKeyData OCTET STRING  
		  }  
	其中：  
	version: 版本号，本文档中取值为： 0x01.  
    encryptedPrivateKeyData: 加密后的加密证书密钥对数据。其密文格式为：C1||C3||C2.  
	解密后的明文格式为： 加密公钥X分量(32字节)||加密公钥Y分量(32字节)||加密私钥(32字节)  
	
	> 3，对以上步骤生成的encryptedPrivateKeyData，用之前本地生成的私钥进行解密，并把解密后的私钥保存成pem格式的私钥文件。可用两个命令导出公钥文件，查看两个公钥内容是否一致，检验私钥解密是否成功：  
	openssl pkey -in "gmennode.key" -pubout -out "gmennode.pubkey"  
	openssl x509 -outform PEM -in gmennode.crt -pubkey -out gmennode1.pubkey  
	> 4, 下载cfca的根证书和二级根证书(cfca_gmca.crt, cfca_gmagency.crt)  
	至此，得到了部署联盟链需要的根证书及四组双证证书的证书文件和私钥文件：cfca_gmnode.crt, cfca_gmnode.key, cfca_gmennode.crt, cfca_gmennode.key  
- 下载cfca二级根证书和根证书
    * 上cfca官网下载证书签发的二级根证书和根证书，用以下命令验证证书链：  
	TASSLCMD verify -CAfile cfca_gmca.crt -untrusted cfca_gmagency.crt cfca_gmnode.crt, TASSLCMD是tassl安装路径
## 使用证书生成节点

- 下载企业部署工具
    * 下载generator工具，并安装，下载相应版本的fisco-bcos。此步骤和正常部署安装国密联盟链相同，相关细节可查看官网文档，不再赘述。  
- 生成节点配置文件  
    * 1，按照正常部署国密版本的方法步骤生成链证书，机构证书，节点证书文件，以及相关的配置文件，不明白其中细节，可查阅官网文档。
	* 2，以上步骤会生成的所有的证书文件会存放在meta目录中，用前面步骤生成的cfca_gmca.crt根证书替换gmca.crt文件，cfca_gmagency.crt替换机构证书gmagency.crt文件，把cfca生成的签名证书cfca_gmennode.crt替换gmnode.crt文件，cfca生成的加密证书cfca_gmennode.crt替换gmennode.crt文件，注意要把cfca二级证书文件的内容拷贝连接在gmnode.crt和gmennode.crt文件后面。 
	* 3，执行以下命令构建链文件：  
	./generator --create_group_genesis ./group -g  
    ./generator --build_install_package ./meta/peers.txt ./nodes -g
- 启动节点  
    * cd nodes && ./start_all.sh 
- 验证节点启动
    * 查看进程  
	ps -ef | grep fisco  
	* 查看节点node0链接的节点数  
	tail -f nodes/node_127.0.0.1_30300/log/log*  | grep connected  
	* 检查是否在共识  
	tail -f nodes/node_127.0.0.1_30300/log/log*  | grep +++
## 注意事项

- 证书基本约束：签名证书，加密证书  
    * 在申请cfca证书时，要分清楚哪个是签名证书，哪个是加密证书，不要搞混。
- 证书基本用途：客户端，server端  
    * 向cfca申请证书是要确认申请的是设备双证类型证书，其他类型证书不能同时当客户端和服务器端证书使用。
