# 权限控制

本文档描述权限控制的实践操作，有关权限控制的详细设计请参考[权限控制设计文档](../design/security_control/permission_control.md)。


## 操作内容

本文档分别对以下功能进行权限控制的操作介绍：
- 部署合约和创建用户表控制示例
- 用户表控制示例
- 节点类型管理控制示例
- CNS控制示例
- 系统参数控制示例
- 权限管理控制示例

## 环境配置  
配置并启动FISCO BCOS 2.0区块链节点和控制台，请参考[安装文档](../installation.md)。   

## 权限控制工具

### 权限控制相关命令

针对普通用户，FISCO BCOS提供控制台命令使用权限功能（针对开发者，可以调用[SDK API](../sdk/sdk.html#web3sdk-api)的PermissionService接口使用权限功能），其中涉及的权限控命令如下:

|命令名称|命令参数|功能|
|:----|:-----|:----|
|grantDeployAndCreateManager          |address               |增加外部账号地址的部署合约和创建用户表权限    |
|revokeDeployAndCreateManager       |address               |移除外部账号地址的部署合约和创建用户表权限    |
|listDeployAndCreateManager        |                      |查询拥有部署合约和创建用户表权限的权限记录列表|
|grantUserTableManager                |table_name address    |根据用户表名和外部账号地址设置权限信息        |
|revokeUserTableManager             |table_name address    |根据用户表名和外部账号地址移除权限信息        |
|listUserTableManager              |table_name            |根据用户表名查询设置的权限记录列表            |
|grantNodeManager                     |address               |增加外部账号地址的节点管理权限                |
|revokeNodeManager                  |address               |移除外部账号地址的节点管理权限                |
|listNodeManager                   |                      |查询拥有节点管理的权限记录列表                |
|grantCNSManager                      |address               |增加外部账号地址的使用CNS权限                 |
|revokeCNSManager                   |address               |移除外部账号地址的使用CNS权限                 |
|listCNSManager                    |                      |查询拥有使用CNS的权限记录列表                 |
|grantSysConfigManager                |address               |增加外部账号地址的系统参数管理权限            |
|revokeSysConfigManager             |address               |移除外部账号地址的系统参数管理权限            |
|listSysConfigManager              |                      |查询拥有系统参数管理的权限记录列表            |
|grantPermissionManager                |address               |增加外部账号地址的管理权限的权限              |
|revokePermissionManager             |address               |移除外部账号地址的管理权限的权限              |
|listPermissionManager              |                      |查询拥有管理权限的权限记录列表                |

### 权限控制示例外部账号
控制台启动时可以指定私钥（从而确定对应的外部账号地址）发送交易。因此，通过控制台可以指定外部账号，体验权限控制功能。下面提供三个外部账号私钥及其对应的外部账号地址供使用：
```bash     
# 账号1
私钥：3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4
地址: 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
```bash
# 账号2:    
私钥：ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e
地址: 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
```
```bash
# 账号3：      
私钥：d0fee0a4e3c545a9394965042f8f891b6e5482c212a7428ec175d6aed121353a
地址: 0x1600e34312edea101d8b41a3465f2e381b66baed
```
可以打开三个连接Linux的终端，分别以三个账号登录控制台。

指定外部账号1登录控制台(其中数字1代表群组1，字符串3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4为外部账号1的私钥)：
```bash
$ ./start.sh 1 3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4
```
指定外部账号2登录控制台：
```bash
$ ./start.sh 1 ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e
```
指定外部账号3登录控制台：
```bash
$ ./start.sh 1 d0fee0a4e3c545a9394965042f8f891b6e5482c212a7428ec175d6aed121353a
```
### 示例客户端工具
提供权限控制示例客户端工具，该示例工具可以指定三个外部账号进行部署合约、创建用户表t_test以及对用户表t_test进行增删改查操作。其中三个外部账号地址如下：
```
1： tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
2： tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
3： tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed
```
获取客户端工具：
```
curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/permission_util.tar.gz
```
解压客户端工具，具体使用参考解压目录中的使用说明文档。

## 权限控制示例

### 部署合约和创建用户表控制示例    

#### 默认权限示例
默认外部账号均可部署合约和创建表。   
#### 合约部署示例
进入以账户1登录的控制台，查询拥有部署合约和创建用户表权限的权限记录。
```
[group:1]> listDeployAndCreateManager
Empty set.
```
确认初始状态无部署合约和创建用户表权限的权限记录，默认所有外部账号均可以部署合约。   
- **外部账号1部署合约：** 
进入客户端工具dist目录，运行部署合约命令：
	```bash
	$ ./run.sh 1 1 deploy

	tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	deploy contract address: 0x9e116ecf100be281ae9587c907cf5b450d51af1b
	deploy contract successful!
	```
	外部账号1部署合约成功。   
- **外部账号2部署合约：** 
	```bash
	$ ./run.sh 2 1 deploy

	tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d

	deploy contract address: 0xa5b85cf3728a15b67572af9b180e2ab4e449359a
	deploy contract successful!
	```
	外部账号2部署合约成功。        
- **外部账号3部署合约：** 
	```bash
	$ ./run.sh 3 1 deploy

	tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed

	deploy contract address: 0xd4ebb24ac68263e92335977c7ea968d5e770eb07
	deploy contract successful!
	```
	外部账号3部署合约成功。  

#### 创建用户表示例
- **外部账号1创建用户表t_test:**
	进入客户端工具dist目录，运行创建用户表命令：
	```bash
	$ ./run.sh 1 1 create
  
  tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	create t_test table completed.
	```
	外部账号1创建t_test表成功，表明有权限创建用户表。类似的，外部账号2和3也可也创建用户表t_test。
	
#### 设置权限示例
进入账户1登录的控制台，设置外部账号1拥有部署合约和创建用户表的权限。
```
[group:1]> grantDeployAndCreateManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}
```
```
[group:1]> listDeployAndCreateManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      1                      |
---------------------------------------------------------------------------------------------
```
#### 合约部署示例
- **外部账号1部署合约：** 
	```bash
	$ ./run.sh 1 1 deploy

	tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	deploy contract address: 0x31877d5864125b3aa3a5ae60022274d1a4130d00
	deploy contract successful!
	```
	外部账号1部署合约成功，有权限部署合约。   
- **外部账号2部署合约：** 
	```bash
	$ ./run.sh 2 1 deploy

	tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d

	non-authorized to deploy contracts!
	```
	外部账号2部署合约失败，无权限部署合约。    
- **外部账号3部署合约：** 
	```bash
	$ ./run.sh 3 1 deploy

	tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed

	non-authorized to deploy contracts!
	```
	外部账号3部署合约失败，无权限部署合约。

#### 创建用户表示例
- **外部账号2创建用户表t_test:**
	```bash
	$ ./run.sh 2 1 create

  tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d

	non-authorized to create t_test table.
	```
	外部账号2创建t_test表失败，表明无权限创建用户表。   
- **外部账号3创建用户表t_test:**
	```bash
	$ ./run.sh 3 1 create

  tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed

	non-authorized to create t_test table.
	```
	外部账号3创建t_test表失败，表明无权限创建用户表。

- **外部账号1创建用户表t_test:**
	```bash
	$ ./run.sh 1 1 create

  tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	create t_test table completed.
	```
	外部账号1创建t_test表成功，表明有权限创建用户表。

#### 移除权限示例
进入账户1登录的控制台，移除设置的外部账号1权限，则外部账号1，2和3均可以部署合约和创建用户表。
```
[group:1]> revokeDeployAndCreateManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}

[group:1]> listDeployAndCreateManager
Empty set.
```

### 用户表控制示例
#### 默认权限示例
通过示例客户端分别指定三个外部账号进行t_test表的增删改查示例。首先登录控制台，查询t_test表的权限设置记录。
```
[group:1]> listUserTableManager t_test
Empty set.
```
确认初始状态没有设置权限，因此默认所有外部账号均可以对t_test进行读写操作。
#### 账号1示例
指定外部账号1创建t_test表：
```bash
$ ./run.sh 1 1 create

tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

create t_test table completed.
```
t_test表创建成功。   
- 通过指定外部账号1向t_test表插入记录：
	```bash
	$ ./run.sh 1 1 insert fruit 1 apple1

	tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	insertCount = 1
	```
	t_test表插入记录成功。   
- 通过指定外部账号1向t_test表查询记录：
	```bash
	$ ./run.sh 1 1 select fruit

	tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	record numbers = 1
	name = fruit
	item_id = 1
	item_name = apple1
	```
- 通过指定外部账号1向t_test表更新记录：
	```bash
	$ ./run.sh 1 1 update fruit 1 apple11

	tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	updateCount = 1
	```
	t_test表更新记录成功，可以通过查询记录再次验证。       
- 通过指定外部账号1向t_test表删除记录：
	```bash
	$ ./run.sh 1 1 remove fruit 1

	tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

	removeCount = 1
	```
	t_test表删除记录成功，可以通过查询记录再次验证。     
#### 账号2和3示例
外部账号1已创建t_test表，无需再创建。与示例外部账号1类似，可以分别指定外部账号2和3对t_test表进行增删改查验证。

#### 设置权限示例
进入账户1登录的控制台，设置外部账号1可以对t_test表进行读写操作。
```
[group:1]> grantUserTableManager t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}

[group:1]> listUserTableManager t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                     11                      |
---------------------------------------------------------------------------------------------
```
设置完毕后，则外部账号1有权限对t_test表进行读写操作，其他外部账号只可以对t_test表执行读操作。    
- **外部账号1有权限操作t_test表示例：**    
 	- 通过指定外部账号1向t_test表插入记录：
		```bash
		$ ./run.sh 1 1 insert fruit 2 apple1

		tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

		insertCount = 1
		```
		t_test表插入记录成功。   
	- 通过指定外部账号1向t_test表查询记录：
		```bash
		$ ./run.sh 1 1 select fruit

		tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

		record numbers = 1
		name = fruit
		item_id = 2
		item_name = apple1
		```
	- 通过指定外部账号1向t_test表更新记录：
		```bash
		$ ./run.sh 1 1 update fruit 2 apple22

		tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

		updateCount = 1
		```
		t_test表更新记录成功，可以通过查询记录再次验证。    
	- 通过指定外部账号1向t_test表删除记录：
		```bash
		$ ./run.sh 1 1 remove fruit 2

		tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

		removeCount = 1
		```
		t_test表删除记录成功，可以通过查询记录再次验证。      

- **外部账号2或3无权限操作t_test表示例：**   
	- 通过指定外部账号2向t_test表插入记录：
		```bash
		$ ./run.sh 2 1 insert fruit 2 apple2

		tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d

		insertCount = 50000
		```
		t_test表插入记录失败，可以通过查询记录再次验证。insertCount返回50000表示无权限插入记录。   
	- 通过指定外部账号1向t_test表插入记录：
		```bash
		$ ./run.sh 1 1 insert fruit 2 apple1

		tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256

		insertCount = 1
		```
		t_test表插入记录成功，可以通过查询记录再次验证。  
	- 通过指定外部账号2向t_test表更新记录：
		```bash
		$ ./run.sh 2 1 update fruit 2 apple12
	
		tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d

		updateCount = 50000
		```
		t_test表更新记录失败，可以通过查询记录再次验证。updateCount返回50000表示无权限更新记录。   
	- 通过指定外部账号2向t_test表删除记录：
		```bash
		$ ./run.sh 2 1 remove fruit 2

		tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d

		removeCount = 50000
		```
		t_test表删除记录失败，可以通过查询记录再次验证。removeCount返回50000表示无权限删除记录。

#### 移除权限示例
进入账户1登录的控制台，移除设置的外部账号1的权限，则外部账号1，2和3均可以对t_test表进行读写操作。
```
[group:1]> revokeUserTableManager t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}

[group:1]> listUserTableManager t_test
Empty set.
```
### 节点类型管理控制示例

控制台提供5个有关节点类型操作的命令，如下表所示：

```eval_rst

+----------------+--------+------------------+
|命令            |命令参数|含义              |
+================+========+==================+
|addSealer       |nodeId  |设置节点为共识节点|
+----------------+--------+------------------+
|addObserve      |nodeId  |设置节点为观察节点|
+----------------+--------+------------------+
|removeNode      |nodeId  |设置节点为游离节点|
+----------------+--------+------------------+
|getSealerList   |        |查询共识节点列表  |
+----------------+--------+------------------+
|getObserverList |        |查询观察节点列表  |
+----------------+--------+------------------+

```
**注：**     
其中addSealer、addObserver和removeNode命令受权限可以控制，getSealerList和getObserverList命令不受权限控制。

#### 默认示例
进入以外部账号1登陆的控制台，查看共识节点列表：
```
[group:1]> 
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
[group:1]> getObserverList
[]
```
将第一个nodeId对应的节点设置为观察节点：
```
[group:1]> addObserver 41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
{
	"code":0,
	"msg":"success"
}
```
设置成功。  

查看共识节点列表：
```
[group:1]> getSealerList
[
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
[group:1]> getObserverList
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
]
```
确认设置成功。       

将该观察节点设置为共识节点：
```
[group:1]> addSealer 41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
{
	"code":0,
	"msg":"success"
}
```
设置成功。

查看共识节点列表：
```
[group:1]> getSealerList
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
[group:1]> getObserverList
[]
```
确认设置成功。

类似，可以通过账号2和3登陆控制台均可以执行addSealer、addObserver和removeNode命令。

#### 设置权限示例
以账号1登录控制台，设置账号1拥有节点管理权限。
```
[group:1]> grantNodeManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}
[group:1]> listNodeManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      1                      |
---------------------------------------------------------------------------------------------
```
则账号1可以执行addSealer、addObserver和removeNode命令，操作见默认示例。

进入以账号2登陆的控制台，查看共识节点列表：
```
[group:1]> getSealerList
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
[group:1]> getObserverList
[]
```
将第一个nodeId对应的节点设置为观察节点：
```
[group:1]> addObserver 41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
{
	"code":50000,
	"msg":"permission denied"
}
```
设置失败，提示无权限信息。

查看共识节点列表：
```
[group:1]> getSealerList
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
[group:1]> getObserverList
[]
```
确认设置失败。

类似以账号3登陆控制台，均无权限执行addSealer、addObserver和removeNode命令。

#### 移除权限示例
移除外部账号1的权限设置，命令如下：
```
revokeNodeManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
然后分别再以账号1、2、3对节点类型进行操作。

### CNS控制示例
控制台提供3个涉及[CNS](../design/features/CNS_contract_name_service.md)的命令，如下所示：


```eval_rst

+-----------------+------------------------------------------------+---------------------+
|命令             |命令参数                                        |功能                 |
+=================+================================================+=====================+
|deployByCNS      |contractName contractVersion                    |利用CNS部署合约      |
+-----------------+------------------------------------------------+---------------------+
|callByCNS        |contractName contractVersion funcName params    |利用CNS调用合约      |
+-----------------+------------------------------------------------+---------------------+
|queryCNS         |contractName [contractVersion]                  |查询CNS信息          |
+-----------------+------------------------------------------------+---------------------+

```
**注：**     
其中deployByCNS命令受权限可以控制，**且同时需要部署合约和使用CNS的权限**，callByCNS和queryCNS命令不受权限控制。

#### 默认示例
利用CNS部署HelloWorld合约：
```
[group:1]> deployByCNS HelloWorld 1.0
0x001815813a1460a0b989935963c27b90d8654216
```
合约部署成功。             
查询HelloWorld合约的CNS信息：
```
[group:1]> queryCNS HelloWorld
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x001815813a1460a0b989935963c27b90d8654216  |
---------------------------------------------------------------------------------------------
```
确认部署合约的CNS信息写入成功。

类似以账号2和3登陆控制台，均可以利用CNS部署合约。

#### 设置权限示例
进入账号1登录的控制台，设置外部账号1拥有使用CNS的权限。
```
[group:1]> grantCNSManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}

[group:1]> listCNSManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      2                      |
---------------------------------------------------------------------------------------------
```
进入以账号2登陆的控制台，利用CNS部署合约：
```
[group:1]> deployByCNS HelloWorld 3.0
{
	"code":50000,
	"msg":"permission denied"
}
```
部署失败，账号2无权限利用CNS部署合约。类似以账号3登陆控制台也将无权限利用CNS部署合约，以账号1登陆控制台则可以利用CNS部署合约。

#### 移除权限示例
移除外部账号1的权限设置，命令如下：
```
revokeCNSManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
然后分别再以账号1、2、3登陆控制台利用CNS部署合约操作。


### 系统参数控制示例
控制台提供2个关于系统配置的命令，如下表所示：

```eval_rst

+--------------------------+-----------+--------------------------------+
|命令                      |命令参数   |功能                            |
+==========================+===========+================================+
|setSystemConfigByKey      |key value  |设置键为key，值为value的系统配置|
+--------------------------+-----------+--------------------------------+
|getSystemConfigByKey      |key        |根据key查询value                |
+--------------------------+-----------+--------------------------------+

```
**注：**     
目前支持键为tx_count_limit和tx_gas_limit的系统参数设置。其中setSystemConfigByKey命令受权限控制，getSystemConfigByKey命令不受权限控制。

#### 默认示例
进入账号1登陆的控制台，首先查询系统字段tx_count_limit的值：
```
[group:1]> getSystemConfigByKey tx_count_limit
1000
```
修改系统字段tx_count_limit的值为2000：
```
[group:1]> setSystemConfigByKey tx_count_limit 2000
{
	"code":0,
	"msg":"success"
}
```
设置成功。    
查询系统字段tx_count_limit的值：
```
[group:1]> getSystemConfigByKey tx_count_limit
2000
```
确认设置成功。

类似以账号2和3登陆控制台，均可以设置系统字段的值。

#### 设置权限示例
进入账号1登录的控制台，设置外部账号1拥有系统参数管理的权限。
```
[group:1]> grantSysConfigManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}
[group:1]> listSysConfigManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      3                      |
---------------------------------------------------------------------------------------------
```
进入以外部账号2的登陆控制台，设置系统字段tx_count_limit的值为3000：
```
[group:1]> setSystemConfigByKey tx_count_limit 3000
{
	"code":50000,
	"msg":"permission denied"
}
```
设置失败。
查询系统字段tx_count_limit的值:
```
[group:1]> getSystemConfigByKey tx_count_limit
2000
```
确认设置失败，账号2无权限修改系统参数。类似账号3登陆控制台也将无权限修改系统参数。

#### 移除权限示例
移除外部账号1的权限设置，命令如下：
```
revokeSysConfigManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
然后分别再以外部账号1、2、3进行系统参数设置操作。

### 权限管理控制示例
权利管理功能由权限机制本身控制，如果设置指定外部账号有权限管理功能，则指定的外部账号可以称为管理员账号。管理员账号可以设置其他账号为管理员账号，即设置其他账号是否有权限进行权限功能的设置。
   
#### 默认示例
默认所有外部账号均可以使用权限设置功能。以账号1登陆控制台，设置外部账号1有权限读写t_test表：
```
[group:1]> grantUserTableManager t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}
```
设置成功，说明外部账号1有设置权限功能。

查询外部账号1设置的结果:
```
[group:1]> listUserTableManager t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      8                      |
---------------------------------------------------------------------------------------------
```
删除外部账号1设置的结果:
```
[group:1]> revokeUserTableManager t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}
```
删除成功。类似以账号2和3登陆控制台，均拥有权限设置功能。

#### 设置权限示例
设置外部账号1拥有权限设置功能。即账号1成为管理员账号，其他用户为非管理员账号。
```
[group:1]> grantPermissionManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}

[group:1]> listPermissionManager
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      7                      |
---------------------------------------------------------------------------------------------
```
**外部账号1有权限设置功能示例：**   
设置外部账号1有权限读写t_test表:
```
[group:1]> grantUserTableManager t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}
```
设置成功，说明外部账号1有设置权限功能。
查询外部账号1设置的结果:
```
[group:1]> listUserTableManager t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      8                      |
---------------------------------------------------------------------------------------------
```
删除外部账号1设置的结果:
```
[group:1]> revokeUserTableManager t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":0,
	"msg":"success"
}
```
删除成功。
    
**外部账号2无权限设置功能示例：**    
进入以外部账号2登录的控制台，设置外部账号2有权限读写t_test表:
```
[group:1]> grantUserTableManager t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":50000,
	"msg":"permission denied"
}
```
设置失败。  

查询外部账号2设置的结果:
```
[group:1]> listUserTableManager t_test
Empty set.
```
查询记录为空，说明外部账号2无设置权限功能。

**注：** 
外部账号3也无设置权限功能，可以类似操作。若需要让外部账号2和3有权限设置功能，可以让账号1登陆控制台，设置账号2和3有权限设置功能。设置命令如下：
```
grantPermissionManager 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
grantPermissionManager 0x1600e34312edea101d8b41a3465f2e381b66baed
```

#### 移除权限示例
移除外部账号1的权限设置功能，命令如下：
```
revokePermissionManager 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
查询拥有管理权限的权限记录列表，命令如下：
```
[group:1]> listPermissionManager
Empty set.
```
查询记录为空，则恢复默认权限设置。如果查询的权限记录有其他账号地址，则查出的账号拥有权限设置功能，账号1无权限设置功能。

## 权限控制实践推荐

### 推荐管理员机制
由于系统默认无权限设置记录，因此任何账号均可以使用权限设置功能。例如当账号1设置账号1有权限部署合约，但是账号2也可以设置账号2有权限部署合约。那么账号1的设置将失去控制的意义，因为其他账号可以自由添加权限。因此，**搭建联盟链之前，推荐确定权限使用规则。** 可以设置管理员账号，即指定特定账号可以使用权限设置功能，非管理员账号无权限使用功能。

### 推荐使用sdk的接口管理
[SDK](../sdk/sdk.html#permissionservice)提供PermissionService类的接口，可以设置、移除和查询权限信息。虽然控制台提供简单的命令可以管理权限，但不适合自定义开发。推荐开发者调用PermissionService类接口实现权限的控制。
