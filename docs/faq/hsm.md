# 使用硬件加密模块时报错
### FISCO BCOS 生成节点证书时提示有错误。
```bash
bash build_gm_hsm_chain.sh -f ipAndKeyConfig -e ../build/bin/fisco-bcos -H 192.168.10.12,8008,XXXXX
Checking fisco-bcos binary...
Binary check passed.
==============================================================
Generating Guomi CA Cert...
[ERROR] swssl gm error!
```
#### 第一步，测试密码机是否能正常连接
这有可能是无法访问密码机导致的。请先测试密码机。
```bash
export OPENSSL_CONF=~/.fisco/swssl/ssl/swssl.cnf
export LD_LIBRARY_PATH=/home/maggie/.fisco/swssl/lib
~/.fisco/swssl/bin/swssl engine sdf -t
(sdf) Sansec SDF Engine
     [ available ]
140074572698688:error:8207C064:lib(130):engine_sdf_device_open:device module call:../engines/sdf/sdf_lib.c:274:SDF_OpenDevice error: 0x01010201
```
说明密码机访问不正常。

## 使用硬件加密库的Java 应用/Console无法成功启动
使用硬件加密库指的是，应用引入的是fisco-bcos-java-sdk-2.8.0-hsm.jar版本的Jar包。
#### 第一步，确保LD_LIBRARY_PATH设置了。

   ```bash
   # 确保存在/root/.fisco/swssl/lib路径。
   export LD_LIBRARY_PATH=/root/.fisco/swssl/lib
   ```

#### 第二步，如果您使用了内部密钥，请确保存在配置文件是否存在。
如果是Console，则在start.sh的同级目录下加入配置文件swsds.ini。如果使用Java SDK，不知道怎么放的情况下，把配置文件放在/etc/目录下。

```ini
[ErrorLog]
level=3
logfile=swsds.log
maxsize=10
[HSM1]
ip=192.168.10.12
port=8008
passwd=11111111
[Timeout]
connect=30
service=60
[ConnectionPool]
poolsize=2
```

#### 第三步，确保Java SDK 密钥配置正确，查看config.toml

```toml
[cryptoMaterial]

certPath = "conf"                           # The certification path
cryptoProvider = "hsm"                      # Use hard ware secure module
sslKeyIndex = "sm2_51"                          # HSM internal sign key index 
enSslKeyInde = "sm2_52"                         # HSM internal encrypt key index 
```

注意这里和文档中写的不同，文档中是写``sslKeyIndex = "51"`` , 这里请暂时先改成``sslKeyIndex = "sm2_51" ``。 后续我将这里调整一下，让用户还是按照文档中的配置数字就行了``sslKeyIndex = "51"`` ，这算是一个可以优化的项目，可以提个单子上去。

```toml
[account]
accountKeyIndex = "53"
password = "XXXXX"                
```

此外，也请注意确保account中配置的KeyIndex在密码机中存在该密钥。