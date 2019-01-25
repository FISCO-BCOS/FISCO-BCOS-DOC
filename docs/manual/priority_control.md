# 权限控制操作文档

本文档描述权限控制的实践操作，有关权限控制的详细设计请参考[权限设计文档](../design/security_control/priority_control.md)。


## 1 操作内容

本文档分别对以下权限控制的6类表进行示例介绍：
- [_sys_tables_表示例](#41-_sys_tables_表示例)
- [用户表示例](#42-用户表示例)
- [_sys_miners_示例](#43-_sys_miners_示例)
- [_sys_cns_示例](#44-_sys_cns_示例)
- [_sys_config_示例](#45-_sys_config_示例)
- [_sys_table_access_表示例](#46-_sys_table_access_表示例)


## 2 环境配置  
① 配置并启动FISCO BCOS2.0的链节点，具体步骤参考 [一键搭链文档](./buildchain.md)。   
② 配置并启动Java SDK2.0的控制台，具体步骤参考 [控制台使用文档](./console.md)。

## 3 权限控制工具
### 3.1 Java SDK控制台工具
### 控制台权限控制命令
FISCO BCOS的分布式存储权限控制通过权限表来管理。通过提供控制台命令对权限表进行读写操作（针对开发者，可以调用sdk的AuthorityService接口操作权限表），其中有三个命令涉及权限表，如下所示。

|AuthorityService API|命令全称（缩写）|命令参数|含义|
|:--- |:---|:------|:---------|
|String add(String tableName, String addr) |addAuthority(aa)| table_name address | 增加控制的表名和外部账户地址
|String remove(String tableName, String addr) |removeAuthority(ra)| table_name address | 移除控制的表名和外部账户地址
|List<AuthorityInfo> query(String tableName) |queryAuthority(qa)| table_name |根据表名查询权限设置记录|

**注：**
 表名可以是用户表和系统表(\_sys_tables_, \_sys_table_access_, \_sys_miners_, \_sys_cns_和_sys_config_)的表名。  

### 3.2 示例客户端工具
提供权限控制示例客户端工具，该示例工具可以指定三个外部账户进行部署合约、创建用户表t_test以及对用户表t_test进行增删改查操作。其中三个账户的外部账户分别如下：
```
1： tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
2： tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
3： tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed
```
获取客户端工具：
```
curl -LO https://github.com/FISCO-BCOS/LargeFiles/raw/master/tools/bcosclient_authority2.0.tar.gz
```
解压客户端工具，具体使用参考目录中的使用说明文档。

## 4 权限控制示例

### 4.1 _sys_tables_表示例
_sys_tables_表保存所有表的字段信息，用于控制表的创建操作。创建合约需要对_sys_tables_表拥有写权限，因此将权限设置于_sys_tables_表可以控制合约的部署和表的创建。    

#### 4.1.1 默认权限示例
权限表默认没有设置关于_sys_tables_表的权限信息，因此所有外部账户均可部署合约和创建表。   
#### 4.1.1.1 合约部署示例
登录控制台，查询权限表中关于_sys_tables_表的权限设置内容。
```
> qa _sys_tables_
Empty set.
```
确认初始状态无_sys_tables_表相关的权限设置信息，默认所有外部账户均可以部署合约。   
**外部账户1部署合约：** 
进入客户端工具dist目录，运行部署合约命令：
```
$ ./run.sh 1 1 deploy
1 1 deploy
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
deploy contract address: 0x9e116ecf100be281ae9587c907cf5b450d51af1b
deploy contract successful!
```
外部账户1部署合约成功。   
**外部账户2部署合约：** 
```
$ ./run.sh 2 1 deploy
2 1 deploy
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
deploy contract address: 0xa5b85cf3728a15b67572af9b180e2ab4e449359a
deploy contract successful!
```
外部账户2部署合约成功。        
**外部账户3部署合约：** 
```
$ ./run.sh 3 1 deploy
3 1 deploy
tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed
deploy contract address: 0xd4ebb24ac68263e92335977c7ea968d5e770eb07
deploy contract successful!
```
外部账户3部署合约成功。  

#### 4.1.1.2 创建用户表示例
**外部账户1创建用户表t_test:**
进入客户端工具dist目录，运行创建用户表命令：
```
$ ./run.sh 1 1 create
1 1 create
create t_test table completed.
```
外部账户1创建t_test表成功，表明有权限创建用户表。类似的，外部账户2和3也可也创建用户表t_test。   
**注意:** 因为示例客户端中的示例合约是创建t_test表，FISCO BCOS不会创建重复的表，因此重复创建已有的表会失败。多账户创建同一张表，可以删除节点数据（恢复链的初始状态）后再测试。

#### 4.1.2 设置权限示例
登录sdk控制台，设置外部账户1可以对_sys_tables_表进行读写操作。
```
> aa _sys_tables_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}
```
```
> qa _sys_tables_
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      1                      |
---------------------------------------------------------------------------------------------
```
#### 4.1.2.1 合约部署示例
**外部账户1部署合约：** 
```
$ ./run.sh 1 1 deploy
1 1 deploy
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
deploy contract address: 0x31877d5864125b3aa3a5ae60022274d1a4130d00
deploy contract successful!
```
外部账户1部署合约成，有权限部署合约。   
**外部账户2部署合约：** 
```
$ ./run.sh 2 1 deploy
2 1 deploy
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
non-authorized to deploy contracts!
```
外部账户2部署合约失败，无权限部署合约。    
**外部账户3部署合约：** 
```
$ ./run.sh 3 1 deploy
3 1 deploy
tx.origin = 0x1600e34312edea101d8b41a3465f2e381b66baed
non-authorized to deploy contracts!
```
外部账户3部署合约失败，无权限部署合约。

#### 4.1.1.2 创建用户表示例
**外部账户2创建用户表t_test:**
```
$ ./run.sh 2 1 create
2 1 create
non-authorized to create t_test table.
```
外部账户2创建t_test表失败，表明无权限创建用户表。   
**外部账户3创建用户表t_test:**
```
$ ./run.sh 3 1 create
3 1 create
non-authorized to create t_test table.
```
外部账户3创建t_test表失败，表明无权限创建用户表。

**外部账户1创建用户表t_test:**
```
$ ./run.sh 1 1 create
1 1 create
create t_test table completed.
```
外部账户1创建t_test表成功，表明有权限创建用户表。

#### 4.1.3 去除权限示例
登录sdk控制台，去除设置的外部账户1权限，则外部账户1，2和3均可以部署合约和创建表。
```
> ra _sys_tables_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa _sys_tables_
Empty set.
```

### 4.2 用户表示例
#### 4.2.1 默认权限示例
默认所有表放开，无权限设置信息，所有外部账户均读写表。通过示例客户端分别指定三个外部账户进行t_test表的增删改查示例。首先登录sdk控制台，查询t_test表的权限设置内容。
```
> qa t_test
Empty set.
```
确认初始状态没有设置权限，因此默认所有外部账户均可以进行读写操作。
#### 4.2.1.1 账户1示例
指定外部账户1创建t_test表：
```
$ ./run.sh 1 1 create
1 1 create
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
create t_test table completed.
```
t_test表创建成功。   
1）通过指定外部账户1向t_test表插入记录：
```
$ ./run.sh 1 1 insert fruit 1 apple1
1 1 insert fruit 1 apple1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
insertCount = 1
```
t_test表插入记录成功。   
2）通过指定外部账户1向t_test表查询记录：
```
$ ./run.sh 1 1 select fruit
1 1 select fruit
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
record numbers = 1
name = fruit
item_id = 1
item_name = apple1
```
3）通过指定外部账户1向t_test表更新记录：
```
$ ./run.sh 1 1 update fruit 1 apple11
1 1 update fruit 1 apple11
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
updateCount = 1
```
t_test表更新记录成功，可以通过查询记录再次验证。       
4）通过指定外部账户1向t_test表删除记录：
```
$ ./run.sh 1 1 remove fruit 1
1 1 remove fruit 1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
removeCount = 1
```
t_test表删除记录成功，可以通过查询记录再次验证。     
#### 4.2.1.2 账户2和3示例
外部账户1已创建t_test表，无需再创建。与示例外部账户1类似，可以分别指定外部账户2和3对t_test表进行增删改查验证。

#### 4.2.2 设置权限示例
登录控制台，设置外部账户1可以对t_test表进行读写操作。
```
> aa t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                     11                      |
---------------------------------------------------------------------------------------------
```
设置完毕后，则外部账户1有权限对t_test表进行写操作，其他外部账户只可以对t_test表执行读操作。    
**外部账户1有权限操作t_test表示例：**    
1）通过指定外部账户1向t_test表插入记录：
```
$ ./run.sh 1 1 insert fruit 2 apple1
1 1 insert fruit 2 apple1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
insertCount = 1
```
t_test表插入记录成功。   
2）通过指定外部账户1向t_test表查询记录：
```
$ ./run.sh 1 1 select fruit
1 1 select fruit
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
record numbers = 1
name = fruit
item_id = 2
item_name = apple1
```
3）通过指定外部账户1向t_test表更新记录：
```
$ ./run.sh 1 1 update fruit 2 apple22
1 1 update fruit 2 apple22
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
updateCount = 1
```
t_test表更新记录成功，可以通过查询记录再次验证。    
4）通过指定外部账户1向t_test表删除记录：
```
$ ./run.sh 1 1 remove fruit 2
1 1 remove fruit 2
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
removeCount = 1
```
t_test表删除记录成功，可以通过查询记录再次验证。      

**外部账户2或3无权限操作t_test表示例：**   
1）通过指定外部账户2向t_test表插入记录：
```
$ ./run.sh 2 1 insert fruit 2 apple2
2 1 insert fruit 2 apple2
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
insertCount = -1
```
t_test表插入记录失败，可以通过查询记录再次验证。insertCount返回-1表示无权限插入记录。   
2）通过指定外部账户2向t_test表查询记录：
```
$ ./run.sh 2 1 read fruit
2 1 read fruit
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
record numbers = 0
```
3）通过指定外部账户1向t_test表插入记录：
```
$ ./run.sh 1 1 insert fruit 2 apple1
1 1 insert fruit 2 apple1
tx.origin = 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
insertCount = 1
```
t_test表插入记录成功，可以通过查询记录再次验证。外部账户1有权限插入记录。  
4）通过指定外部账户2向t_test表更新记录：
```
$ ./run.sh 2 1 update fruit 2 apple12
2 1 update fruit 2 apple12
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
updateCount = -1
```
t_test表更新记录失败，可以通过查询记录再次验证。updateCount返回-1表示无权限更新记录。   
5）通过指定外部账户2向t_test表删除记录：
```
$ ./run.sh 2 1 remove fruit 2
2 1 remove fruit 2
tx.origin = 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
removeCount = -1
```
t_test表删除记录失败，可以通过查询记录再次验证。removeCount返回-1表示无权限删除记录。

#### 4.2.3 去除权限示例
登录控制台，去除设置的外部账户1权限，则外部账户1，2和3均可以对t_test表进行读写操作。
```
> ra t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa t_test
Empty set.
```

### 4.3 _sys_miners_示例

_sys_miners_表用于管理节点类别信息。通过设置该表的写权限可以控制外部账户对节点类型的操作。控制台提供5个有关节点类型操作的命令，如下表所示：

|命令全称（缩写）|命令参数|含义|
|:---|:------|:---------|
|addMiner(am) | nodeID | 设置节点为共识节点 |
|addObserver(ao) | nodeID | 设置节点为观察节点 |
|removeNode(rn)  | nodeID | 设置节点退出群组|
|getMinerList(gml) | 无 | 查看共识节点列表|
|getObserverList(gol)  | 无 |查看观察节点列表|

**注：**     
其中am、ao和rn命令会写_sys_miners_表（权限可以控制），gml和gol命令只读_sys_miners_表（不受权限控制）

控制台启动时可以指定私钥（从而确定对应的外部账户地址）发送交易，因此通过控制台操作系统表时，可以指定相关外部账户，体验权限控制对系统表的使用。下面提供三个外部账户私钥及其对应的外部账户地址供使用：
```     
账户1：  
私钥：3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4  
地址: 0xf1585b8d0e08a0a00fff662e24d67ba95a438256    
账户2:    
私钥：ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e   
地址: 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d    
账户3：      
私钥：d0fee0a4e3c545a9394965042f8f891b6e5482c212a7428ec175d6aed121353a     
地址: 0x1600e34312edea101d8b41a3465f2e381b66baed
```

指定外部账户1登录控制台，其中数字1代表群组1，字符串3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4为外部账户1的私钥
```
./start 1 3bed914595c159cbce70ec5fb6aff3d6797e0c5ee5a7a9224a21cae8932d84a4
```

#### 4.3.1 默认示例
默认对所有外部账户放开权限，以外部账户1登陆控制台。             
查看共识节点列表：
```
> gml
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
> gol
[]
```
将第一个nodeID对应的节点设置为观察节点：
```
> ao 41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
{
	"code":1,
	"msg":"success"
}
```
设置成功。  

查看共识节点列表：
```
> gml
[
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
> gol
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
]
```
确认设置成功。       

将该观察节点设置为共识节点：
```
> am 41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
{
	"code":1,
	"msg":"success"
}
```
设置成功。

查看共识节点列表：
```
> gml
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
> gol
[]
```
确认设置成功。

类似，可以通过账户2和3登陆控制台均可以执行am、ao和rn命令。


#### 4.3.2 设置权限示例
以账户1登录控制台，设置账户1拥有_sys_miners_表的写权限。
```
> aa _sys_miners_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}
> qa _sys_miners_
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      1                      |
---------------------------------------------------------------------------------------------
```
则账户1可以执行am、ao和rn命令，操作见4.3.1。

现在以账户2登陆控制台： 
```
./start 1 ab40568a2f77b4cb70706b4c6119916a143eb75c0d618e5f69909af1f9f9695e   
```
查看共识节点列表：
```
> gml
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
> gol
[]
```
将第一个nodeID对应的节点设置为观察节点：
```
> ao 41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba
{
	"code":-1,
	"msg":"non-authorized"
}
```
设置失败，提示无权限信息。

查看共识节点列表：
```
> gml
[
	41285429582cbfe6eed501806391d2825894b3696f801e945176c7eb2379a1ecf03b36b027d72f480e89d15bacd43462d87efd09fb0549e0897f850f9eca82ba,
	d5b3a9782c6aca271c9642aea391415d8b258e3a6d92082e59cc5b813ca123745440792ae0b29f4962df568f8ad58b75fc7cea495684988e26803c9c5198f3f8,
	29c34347a190c1ec0c4507c6eed6a5bcd4d7a8f9f54ef26da616e81185c0af11a8cea4eacb74cf6f61820292b24bc5d9e426af24beda06fbd71c217960c0dff0,
	87774114e4a496c68f2482b30d221fa2f7b5278876da72f3d0a75695b81e2591c1939fc0d3fadb15cc359c997bafc9ea6fc37345346acaf40b6042b5831c97e1
]
```
查看观察节点列表：
```
> gol
[]
```
确认设置失败。

类似以账户3登陆控制台，均无权限执行am、ao和rn命令。

#### 4.3.3 去除权限示例
去除外部账户1的权限设置，命令如下：
```
ra _sys_miners_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
然后分别再以账户1、2、3对节点类型进行操作。

### 4.4 _sys_cns_示例
deployByCNS(dbc)用于管理CNS（Contract Name Service，FISCO BCOS提供的合约命名服务）的信息。通过设置该表的写权限可以控制外部账户是否可以使用CNS部署合约。控制台提供3个关于_sys_cns_表的命令，如下表所示：

|命令全称（缩写）|命令参数|含义|
|:---|:------|:---------|
|deployByCNS(dbc) | contractName contractVersion | 部署合约，其名称为contractName，合约版本为contractVersion |
|callByCNS(cbc) | contractName contractVersion    function parameters | 调用合约，其名称为contractName，合约版本为contractVersion，调用方法名为function，方法参数为parameters（多个参数空格分隔）|
|queryCNS(qcs)|contractName [contractVersion]|根据合约名和合约版本号(可选)查询CNS信息|

**注：**      
deployByCNS(dbc)命令需要_sys_cns_表的写权限，因此通过控制_sys_cns_表的写权限可以控制是否可以利用CNS部署合约。

#### 4.4.1 默认示例
默认对所有外部账户放开权限，以外部账户1登陆控制台，利用CNS部署Ok合约：
```
> dbc Ok 1.0
0x001815813a1460a0b989935963c27b90d8654216
```
合约部署成功。             
查询_sys_cns_表：
```
> qcs Ok
---------------------------------------------------------------------------------------------
|                   version                   |                   address                   |
|                     1.0                     | 0x001815813a1460a0b989935963c27b90d8654216  |
---------------------------------------------------------------------------------------------
```
确认部署合约的CNS信息写入成功。

类似以账户2和3登陆控制台，均可以利用CNS部署合约。

#### 4.4.2 设置权限示例
以账户1登录控制台，设置外部账户1拥有_sys_cns_表的写权限。
```
> aa _sys_cns_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa _sys_cns_
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      2                      |
---------------------------------------------------------------------------------------------
```
以账户2登陆控制台，利用CNS部署合约：
```
> dbc Ok 3.0
{
	"code":-1,
	"msg":"non-authorized"
}
```
部署失败，账户2无权限利用CNS部署合约。类似以账户3登陆控制台也将无权限利用CNS部署合约，以账户1登陆控制台则可以利用CNS部署合约。

#### 4.4.3 去除权限示例
去除外部账户1的权限设置，命令如下：
```
ra _sys_cns_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
然后分别再以账户1、2、3登陆控制台利用CNS部署合约操作。


### 4.5 _sys_config_示例
_sys_config_表用于管理系统配置信息。目前支持键为tx_count_limit和tx_gas_limit设置。通过设置该表的写权限可以控制外部账户是否可以进行系统配置的修改。控制台提供2个关于系统配置的命令，如下表所示：

|命令全称（缩写）|命令参数|含义|
|:---|:------|:---------|
|setSystemConfigByKey(ssc) | key value | 设置键为key，值为value的系统配置
|getSystemConfigByKey(gsc) | key | 根据key查询对应value

#### 4.5.1 默认示例
默认对所有外部账户放开权限，以账户1登陆控制台，首先查询系统字段tx_count_limit的值：
```
> gsc tx_count_limit
1000
```
修改系统字段tx_count_limit的值为2000：
```
> ssc tx_count_limit 2000
{
	"code":1,
	"msg":"success"
}
```
设置成功。    
查询系统字段tx_count_limit的值：
```
> gsc tx_count_limit
2000
```
确认设置成功。

类似以账户2和3登陆控制台，均可以设置系统字段的值。

#### 4.5.2 设置权限示例
以账户1登录控制台，设置外部账户1拥有_sys_config_表的写权限。
```
> aa _sys_config_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}
> qa _sys_config_
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      3                      |
---------------------------------------------------------------------------------------------
```
以外部账户2登陆控制台，设置系统字段tx_count_limit的值为3000：
```
> ssc tx_count_limit 3000
{
	"code":-1,
	"msg":"non-authorized"
}
```
设置失败。
查询系统字段tx_count_limit的值:
```
> gsc tx_count_limit
2000
```
确认设置失败，账户2无权限写_sys_config_表，因此修改不了tx_count_limit的值。

类似账户3登陆控制台也将无权限写_sys_config_表，而账户1登陆控制台有权限写_sys_config_表。

#### 4.5.3 去除权限示例
去除外部账户1的权限设置，命令如下：
```
ra _sys_config_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
然后分别再以外部账户1、2、3进行系统配置的操作。

### 4.6 _sys_table_access_表示例
权限表_sys_table_access_用于设置权限。初始状态无权限记录，即所有外部账户均可以设置权限。如果设置指定外部账户，则指定的外部账户可以称为管理员账户。管理员账户可以设置其他账户为管理员账户，即设置其他账户是否有权限写权限表。
   
#### 4.6.1 默认示例
默认对所有外部账户放开权限，分别以外部账户1，2，3启动控制台，均拥有对_sys_table_access_的读写权限。

#### 4.6.2 设置权限示例
设置外部账户1可以对_sys_table_access_表进行读写操作。即账户1成为管理员账户，其他用户为非管理员账户。
```
> aa _sys_table_access_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}

> qa _sys_table_access_
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      7                      |
---------------------------------------------------------------------------------------------
```
**外部账户1有权限操作_sys_table_access_表示例：**   
设置外部账户1有权限读写t_test表:
```
> aa t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}
```
设置成功，说明外部账户1有设置权限功能。
查询外部账户1设置的结果:
```
> qa t_test
---------------------------------------------------------------------------------------------
|                   address                   |                 enable_num                  |
| 0xf1585b8d0e08a0a00fff662e24d67ba95a438256  |                      8                      |
---------------------------------------------------------------------------------------------
```
删除外部账户1设置的结果:
```
> ra t_test 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
{
	"code":1,
	"msg":"success"
}
```
删除成功。    
**外部账户2无权限操作_sys_table_access_表示例：**    
以外部账户2登录控制台，设置外部账户2有权限读写t_test表:
```
> aa t_test 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
{
	"code":-1,
	"msg":"non-authorized"
}
```
设置失败。        
查询外部账户2设置的结果:
```
> qa t_test
Empty set.
```
查询记录为空，说明外部账户2设置失败。

**注：** 
外部账户3类似操作，也将无权限操作_sys_table_access_表，若需要让外部账户2和3对_sys_table_access_表有操作权限，可以让账户1登陆控制台，设置账户2和3对_sys_table_access_表有操作权限。设置命令如下：
```
aa _sys_table_access_ 0xc0d0e6ccc0b44c12196266548bec4a3616160e7d
aa _sys_table_access_ 0x1600e34312edea101d8b41a3465f2e381b66baed
```

#### 4.6.3 去除权限示例
去除外部账户1的权限设置，命令如下：
```
ra _sys_table_access_ 0xf1585b8d0e08a0a00fff662e24d67ba95a438256
```
**注意：**      
如果权限表中已没有关于_sys_table_access_表的权限记录，那么所有账户均可以操作权限表。

## 5 权限控制实践推荐

#### 5.1 推荐管理员机制
由于系统默认无权限记录，因此任何账户均可以读写权限表。当账户1设置权限只有账户1可以写_sys_tables_表（可以控制部署合约和创建用户表），账户2可以在权限表中添加账户2也可以写_sys_tables_表。那么账户1的设置将失去控制的意义，因为其他账户可以自由添加权限。因此，联盟链组建之前，推荐确定权限使用规则，可以设置管理员账户（即指定特定账户可以写_sys_table_access_表），只有管理员账户可以使用权限设置功能，非管理员账户无权限设置功能。

#### 5.2 推荐使用sdk的接口管理
设置权限将操作_sys_table_access_表，sdk提供AuthorityService类的接口，可以设置、移除和查询权限信息。虽然控制台提供简单的命令可以管理权限，但不适合自定义开发。推荐开发者调用AuthorityService类接口实现权限的控制。
