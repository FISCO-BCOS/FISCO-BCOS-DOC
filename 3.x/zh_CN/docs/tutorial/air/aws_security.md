# 使用AWS保护节点密钥

标签：``存储安全`` ``存储加密`` ``落盘加密``

----

## 使用AWS KMS托管密钥

### 生成密钥

1. 登录[AWS 控制台](https://us-east-1.console.aws.amazon.com/console/home?region=us-east-1)
2. 在控制台主页选择 [Key Management Service](https://us-east-1.console.aws.amazon.com/kms/home?region=us-east-1) 如果没有点击查看所有服务
3. 点击客户管理的密钥--创建密钥
4. 密钥类型-对称，密钥使用情况-加密和解密，高级选项--默认，点击下一步
5. 创建别名，点击下一步
6. 选择允许操作的管理员，下一步
7. 选择密钥操作的用户，点击下一步
8. 确认密钥策略，点击完成
9. 在右上角记录管理密钥的地区，如us-east-1，不同区域的密钥互相不可见

### 授权访问

参考[AWS 示例文档](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys.html), [获取 access key](https://aws.amazon.com/cn/blogs/security/wheres-my-secret-access-key/)

1. 在IAM页面，访问密钥中，点击创建[访问密钥](https://us-east-1.console.aws.amazon.com/iam/home?region=us-east-1#/security_credentials?section=IAM_credentials)
2. 根据需求，点击如本地代码
3. 填写描述标签，点击下一步
4. 下载**csv文件，此为密钥文件，务必秘密保存**，点击已完成

### 使用kmstools加密节点密钥

1. 在[aws kms密钥管理页面](https://us-east-1.console.aws.amazon.com/kms/home?region=us-east-1#/kms/keys/)，可以看到key的id，如012345-12345-12345 ，和region 如 us-east-1，
2. 使用授权访问中的csv文件，作为access key
3. 加密密钥，命令如`/kms_tool <kms_type> <access_key> <secret_key> <region> <key_id> <inputFilePath> <outputFilePath>`, 加密成功后的文件会在outputFilePath中

```eval_rst
.. note::

    kmstools加密工具需要自己编译节点，参考[源码编译](../compile_binary.md)，需要开启tools
```

## 配置节点

需要节点`config.ini`配置如下：
```ini
# 节点密钥配置
[security]
    private_key_path=conf/node.pem
    kms_type=CLOUDKMS # 保护类型使用云KMS
    kms_connection_str=region:id:token # 链接url
    cloud_kms_type=AWS # 使用AWS KMS
```

```eval_rst
.. note::

    WS KMS只加密node.pem，不会对节点落盘数据进行加密
```

## 说明

FISCO BCOSv3.13将共识密钥加密，和节点落盘加密的功能进行分开，节点共识密钥解密支持CLOUDKMS、[HSM](../../design/hsm.md)、[BCOSKMS](../../design/storage/storage_security.md)三种方式

AWS KMS保护节点共识密钥可与落盘加密保护节点落盘数据融合使用，用户可通过以下配置同时开启两种特性

- 单独开启CLOUDKMS保护密钥

```ini
[security]
    private_key_path=conf/node.pem
    kms_type=CLOUDKMS # 保护类型使用云KMS
    kms_connection_str=region:id:token # 链接url
    cloud_kms_type=AWS # 使用AWS KMS
```

- 单独开启BCOSKMS保护密钥

```ini
[security]   
    private_key_path=conf/node.pem
    kms_type=BCOSKMS          
    kms_connection_str=127.0.0.1:8150          
    cipher_data_key=07045a6e9710198c3c335d9f021c2486
```

- 单独开启BCOSKMS保护落盘数据

```ini
[storage_security]
    ; enable data disk encryption or not, default is false
    enable=true
    ; url of the key center, in format of ip:port
    kms_type=BCOSKMS
    kms_connection_str=127.0.0.1:8150
    cipher_data_key=07045a6e9710198c3c335d9f021c2486
```


- 开启CLOUDKMS保护密钥+BCOSKMS保护落盘数据

```ini
[security]
    private_key_path=conf/node.pem
    kms_type=CLOUDKMS # 保护类型使用云KMS
    kms_connection_str=region:id:token # 链接url
    cloud_kms_type=AWS # 使用AWS KMS

[storage_security]
    ; enable data disk encryption or not, default is false
    enable=true
    ; url of the key center, in format of ip:port
    kms_type=BCOSKMS
    kms_connection_str=127.0.0.1:8150
    cipher_data_key=07045a6e9710198c3c335d9f021c2486
```

- 开启BCOSKMSKMS保护密钥+BCOSKMS保护落盘数据

```ini
[security]
    private_key_path=conf/node.pem
    kms_type=BCOSKMS          
    kms_connection_str=127.0.0.1:8150          
    cipher_data_key=07045a6e9710198c3c335d9f021c2486

[storage_security]
    ; enable data disk encryption or not, default is false
    enable=true
    ; url of the key center, in format of ip:port
    kms_type=BCOSKMS
    kms_connection_str=127.0.0.1:8150
    cipher_data_key=07045a6e9710198c3c335d9f021c2486
```


