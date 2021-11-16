## 设置SDK白名单

标签：``SDK访问控制`` ``SDK白名单`` ``SDK白配置`` 

----

FISCO BCOS 2.0开始支持多群组，但没有控制SDK对多群组的访问权限，SDK只要可以与节点建立连接，就可以访问节点的所有群组，会带来安全风险。

FISCO BCOS v2.6.0引入了群组级别的SDK白名单机制，控制SDK对群组的访问权限，并提供脚本支持SDK白名单列表的动态加载，进一步提升区块链系统的安全性。

```eval_rst
.. note::
    - 当配置项中的SDK白名单列表数目为0时，节点没有开启SDK白名单控制功能，任意SDK均可访问该群组；
    - SDK白名单是节点级别的访问控制机制，开启该功能的节点基于本地配置的白名单列表控制SDK对本节点群组的访问权限
    - SDK白名单机制控制SDK对节点所有群组级接口的访问权限
```

### 配置方法

群组级别的SDK白名单配置选项位于`group.{group_id}.ini` ，具体可参考 这里

```eval_rst
.. note::
    每个群组的SDK白名单配置位于 `group.{group_id}.ini` 配置文件的 `[sdk_allowlist]` 配置项中，详细可参考 `这里 <./configuration.html#id38>`_
```

### 获取SDK公钥
将sdk加入到白名单列表前，首先需要获取SDK的公钥，用于设置`group.*.ini`的`public_key`，各个版本获取SDK公钥的方法如下：

#### 新搭建的链

新搭建的链生成的SDK证书自带SDK私钥信息，非国密版为`sdk.publickey`，国密版为`gmsdk.publickey`

```bash
# 设证书已拷贝到SDK，则进入SDK目录，执行如下命令(设sdk位于~/fisco目录)
$ cd ~/fisco/java-sdk

# 获取国密版SDK公钥
$ cat dist/conf/sdk.publickey

# 获取非国密版SDK公钥
$ cat dist/conf/gmsdk.publickey
```

#### 旧链

旧链需要使用openssl或tassl命令生成`sdk.publickey`(国密版是`gmsdk.publickey`)，并从生成的文件中加载公钥，具体如下：

**非国密**

```bash
# 进入SDK目录，执行如下命令：
$ openssl ec -in dist/conf/sdk.key -text -noout 2> /dev/null | sed -n '7,11p' | tr -d ": \n" | awk '{print substr($0,3);}' | cat > dist/conf/sdk.publickey
# 获取SDK公钥
$ cat dist/conf/sdk.publickey
```

**国密链**

```bash
# 注：必须保证~/.fisco/tassl存在
$ ~/.fisco/tassl ec -in dist/conf/gmsdk.key -text -noout 2> /dev/null | sed -n '7,11p' | sed 's/://g' | tr "\n" " " | sed 's/ //g' | awk '{print substr($0,3);}'  | cat > dist/conf/gmsdk.publickey
# 获取SDK公钥
$ cat dist/conf/gmsdk.publickey
```

### 动态修改SDK白名单列表

为方便用户修改SDK白名单列表，每个节点的`scripts`目录下提供了`reload_sdk_allowlist.sh`脚本用于重新加载SDK白名单列表。

```eval_rst
.. note::
    旧链节点无 `reload_sdk_allowlist.sh` 脚本，可通过命令 `curl -#LO https://raw.githubusercontent.com/FISCO-BCOS/FISCO-BCOS/master/tools/reload_sdk_allowlist.sh` 下载该脚本。

    - 如果因为网络问题导致长时间无法下载 `reload_sdk_allowlist.sh` 脚本，请尝试 `curl -#LO https://gitee.com/FISCO-BCOS/FISCO-BCOS/raw/master/tools/reload_sdk_allowlist.sh`
```

### 示例

本节以将控制台加入和删除白名单列表为例，详细展示SDK白名单机制的使用方法。

**搭建区块链并拷贝证书到控制台**

请参考[安装](../installation.md)

**获取控制台公钥信息**

```bash
# 进入到控制台目录
$ cd ~/fisco/console/

# 通过sdk.publickey获取控制台公钥信息
$ cat conf/sdk.publickey
ebf5535c92f7116310ed9e0f9fc9bfc66a607415d4fa444d91f528485eff61b15e40a70bc5d73f0441d3959efbc7718c20bd452ac4beed5f6c4feb9fabc1f9f6
```

**开启SDK白名单机制**

将某控制台的公钥添加到`node0`的group.[group_id].ini配置文件白名单列表中：

```ini
[sdk_allowlist]
public_key.0=b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36
```

**重新加载SDK白名单列表**

```bash
$ bash node0/scripts/reload_sdk_allowlist.sh
 [INFO] node0 is trying to reload sdk allowlist. Check log for more information.

# 热加载SDK白名单列表成功后，节点输出了如下日志：
parseSDKAllowList,sdkAllowList=[b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36],enableSDKAllowListControl=true
```

**控制台访问节点**

```eval_rst
.. note::
    由于SDK白名单是节点级别的访问控制机制，为了展示 `node0` 对SDK的访问控制功能，控制台仅连接 `node0`
```
由于`node0`没有配置控制台对群组的访问权限，部署合约的返回结果如下：

```bash
# 控制台部署HelloWorld合约
[group:1]> deploy HelloWorld
sendRawTransaction The SDK is not allowed to access this group
```


**添加控制台到SDK白名单列表**

将控制台配置到`node0`的白名单列表中：

```ini
[sdk_allowlist]
public_key.0=b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36
public_key.1=ebf5535c92f7116310ed9e0f9fc9bfc66a607415d4fa444d91f528485eff61b15e40a70bc5d73f0441d3959efbc7718c20bd452ac4beed5f6c4feb9fabc1f9f6
```
重新加载SDK白名单列表：

```bash
$ bash node0/scripts/reload_sdk_allowlist.sh
 [INFO] node0 is trying to reload sdk allowlist. Check log for more information.

# 热加载SDK白名单列表成功后，节点输出了如下日志：
parseSDKAllowList,sdkAllowList=[b8acb51b9fe84f88d670646be36f31c52e67544ce56faf3dc8ea4cf1b0ebff0864c6b218fdcd9cf9891ebd414a995847911bd26a770f429300085f37e1131f36,ebf5535c92f7116310ed9e0f9fc9bfc66a607415d4fa444d91f528485eff61b15e40a70bc5d73f0441d3959efbc7718c20bd452ac4beed5f6c4feb9fabc1f9f6],enableSDKAllowListControl=true
```

**控制台访问节点**

`node0`加入控制台到白名单列表后，控制台可正常部署合约，如下：

```bash
[group:1]> deploy HelloWorld
contract address: 0xcd4ccd96c86fe8e4f27b056c0fdb7eb4ca201f0f
```