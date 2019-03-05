# 案例分析

使用前建议优先阅读[教程-使用企业工具](../tutorial/enterprise_quick_start.md)

本节中我们将给出一个常见场景的例子。在本场景中，共有4家机构，分别为A、B、C、D。部署节点如下表所示

| 节点序号 |   P2P地址     |   RPC/channel地址     |   所属机构     |   所在群组     |
| :-----------: | :-------------: | :-------------: | :-------------: | :-------------: |
|   节点0     | 127.0.0.1:30300| 127.0.0.1:8545/:20200 | 机构A | 群组1、2、3 |
|   节点1     | 127.0.0.1:30301| 127.0.0.1:8546/:20201 | 机构B | 群组1 |
|   节点2     | 127.0.0.1:30302| 127.0.0.1:8547/:20202 | 机构C | 群组2 |
|   节点3     | 127.0.0.1:30303| 127.0.0.1:8548/:20203 | 机构D | 群组3 |

![](../../images/enterprise/star.png)

机构A、B、C、D分别维护一个节点，机构A的节点0同时处于三个群组中，接下来我们将讲解如何对等的部署上述模式的多群组联盟链。

## 证书协商

### 机构获取证书资格

1. 机构A、B、C、D在本机生成使用`agency.key`，和机构证书请求文件`agency.csr`。

2. 机构A、B、C、D向第三方权威机构申请联盟链组网资格，由第三方权威机构生成根证书`ca.crt`，同时发送`agency.csr`至第三方，得到机构证书`agency.crt`

生成私钥，生成证书请求文件，生成机构证书

### 协商节点证书

1. 机构A、B、C、D本别用自己的机构私钥`agency.key`和机构证书`agency.crt`生成自己节点的证书cert_127.0.0.1_30300.crt，cert_127.0.0.1_30301.crt，cert_127.0.0.1_30302.crt，cert_127.0.0.1_30303.crt。

2. 机构A分别与B、C、D协商节点证书，放置与meta文件夹下

```eval_rst
.. note::
    
    在这种场景中，A协商后需要有4个节点的所有证书，而B、C、D分别只需要机构A节点0的证书和自己的节点证书即可。如机构B需要cert_127.0.0.1_30300.crt和cert_127.0.0.1_30301.crt。
```

## 机构A、B生成group1

此时，机构A和B都没有生成过节点配置文件，组网流程如下：

![](../../images/enterprise/simple_star1.png)

1. 机构A、B分别将节点0，1证书，fisco-bcos可执行程序放置于meta文件夹下

2. 机构A、B分别修改`mchain.ini`中的信息，配置为节点0和节点1的信息

3. 机构A、B分别使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹

4. 机构A、B分别使用将节点私钥导入生成的节点配置文件中，启动节点

至此，完成机构A、B生成group1的操作

## 机构A、C生成group2

此时，机构A已经拥有节点配置文件，需要生成group2，机构C尚未拥有节点配置文件，组网流程如下：

![](../../images/enterprise/simple_star2.png)

1. 机构A需要将将节点0，2证书，fisco-bcos可执行程序放置于meta文件夹下

2. 机构C将节点0，2证书，fisco-bcos可执行程序放置于meta文件夹下

3. 机构A修改`mgroup.ini`中的信息，配置为节点0和节点2的信息

4. 机构C分别修改`mchain.ini`中的信息，配置为节点0和节点2的信息

5. 机构A使用[create_group_config](./operation.html#create-group-config-c)命令生成group2的配置文件，将group.2.genesis和group.2.ini拷贝到节点0安装包的conf目录下，从启节点

6. 机构B使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹，将私钥导入生成的节点配置文件中，启动节点

至此，完成机构A、C生成group3的操作

## 机构A、D生成group3

此时，机构A已经拥有节点配置文件，需要生成group3，机构D尚未拥有节点配置文件，组网流程与机构A、C生成新群组类似：

![](../../images/enterprise/star.png)

1. 机构A需要将将节点0，3证书，fisco-bcos可执行程序放置于meta文件夹下

2. 机构C将节点0，3证书，fisco-bcos可执行程序放置于meta文件夹下

3. 机构A修改`mgroup.ini`中的信息，配置为节点0和节点3的信息

4. 机构C分别修改`mchain.ini`中的信息，配置为节点0和节点3的信息

5. 机构A使用[create_group_config](./operation.html#create-group-config-c)命令生成group3的配置文件，将group.3.genesis和group.3.ini拷贝到节点0安装包的conf目录下，从启节点

6. 机构B使用[build_install_package](./operation.html#build-install-package-b)命令生成节点配置文件夹，将私钥导入生成的节点配置文件夹中，启动节点

至此，完成机构A、D生成group3的操作

## 配置SDK

完成上述初始组网操作后，机构业务需要完成sdk及控制台的配置，配置文档连接如下：

[配置web3sdk](../sdk/index.html)

[配置控制台](../manual/console.md)

## 机构E进入group1

在这种场景中，机构E需要扩容一个节点进入group1中，如下表所示

| 节点序号 |   P2P地址     |   RPC/channel地址     |   所属机构     |   所在群组     |
| :-----------: | :-------------: | :-------------: | :-------------: | :-------------: |
|   节点0     | 127.0.0.1:30300| 127.0.0.1:8545/:20200 | 机构A | 群组1、2、3 |
|   节点1     | 127.0.0.1:30301| 127.0.0.1:8546/:20201 | 机构B | 群组1 |
|   节点2     | 127.0.0.1:30302| 127.0.0.1:8547/:20202 | 机构C | 群组2 |
|   节点3     | 127.0.0.1:30303| 127.0.0.1:8548/:20203 | 机构D | 群组3 |
|   节点4     | 127.0.0.1:30304| 127.0.0.1:8549/:20204 | 机构E | 群组1 |

![](../../images/enterprise/simple_star3.png)

过程如下：

### 机构E获取证书资格

1. 机构E在本机生成使用`agency.key`，和机构证书请求文件`agency.csr`。

2. 机构E向第三方权威机构申请联盟链组网资格，由第三方权威机构根据group1的根证书`ca.crt`，签发`agency.crt`。

### 机构E生成节点证书

机构E用自己的机构私钥`agency.key`和机构证书`agency.crt`生成自己节点的证书cert_127.0.0.1_30304.crt。

## 机构E生成group1扩容节点配置文件

此时，机构E没有生成过节点配置文件，但group1已经存在，组网流程如下：

1. 机构E向机构A或B请求group1配置文件，获取group1中已经运行节点的`config.ini，group.1.genesis，group.1.ini

2. 机构E将节点4证书，fisco-bcos可执行程序，config.ini，group.1.genesis，group.1.ini放置于meta文件夹下

3. 机构E修改`mexpand.ini`中的信息，配置为节点4的信息

4. 机构E使用[build_expand_package](./operation.html#build-expand-package-e)命令生成扩容节点4配置文件夹，导入私钥，启动节点

5. 机构E向机构A或B发送节点4入网请求，等待机构A或B使用控制台或sdk发送节点入网交易，注册节点4入网。

至此，完成机构E扩容节点加入group1的操作。
