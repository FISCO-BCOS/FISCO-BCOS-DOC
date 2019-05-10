# 基础配置

## 配置根证书

生成链的根证书

``` shell
cd /mydata/FISCO-BCOS/tools/scripts/

#bash generate_chain_cert.sh -o 根证书生成的目录
bash generate_chain_cert.sh -o /mydata
```

## 配置机构证书

生成机构（agency）证书，假设生成机构test_agency

```bash
cd /mydata/FISCO-BCOS/tools/scripts/

#bash generate_agency_cert.sh -c 生成机构证书所需的根证书所在目录 -o 机构证书生成目录 -n 机构名
bash generate_agency_cert.sh -c /mydata -o /mydata -n test_agency
```

## 配置SDK证书 

区块链环境搭建完成之后Web3SDK需要连接节点时需要SDK证书文件，SDK证书需要在机构证书生成之后才能生成，我们为上面生成的机构test_agency生成SDK证书
```bash
cd /mydata/FISCO-BCOS/tools/scripts/

# bash generate_sdk_cert.sh -d 机构证书的目录
bash generate_sdk_cert.sh -d /mydata/test_agency/
```
生成过程中需要输入一些列密码，测试环境的默认输入123456即可.
```bash
bash generate_sdk_cert.sh -d /mydata/test_agency/
Signature ok
subject=/C=CN/ST=GuangDong/L=ShenZhen/OU=test_org/CN=test_org
Getting CA Private Key
read EC key
writing EC key
Enter Export Password:
Verifying - Enter Export Password:
输入目标密钥库口令:
再次输入新口令:
输入源密钥库口令:
Generate success! SDK keys has been generated into "/mydata/test_agency/sdk"
```
生成的目录内容如下：
```bash
tree -L 1 sdk/
sdk/
├── ca.crt
├── client.keystore
├── keystore.p12
├── sdk.crt
├── sdk.csr
├── sdk.key
├── sdk.param
├── sdk.private
└── sdk.pubkey
```
使用Web3SDK时将ca.crt client.keystore文件拷贝到conf目录下即可。
