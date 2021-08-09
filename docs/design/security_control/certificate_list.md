# CA黑白名单介绍

标签：``安全控制`` ``网络安全`` ``黑白名单``

----

本文档对黑、白名单进行介绍性说明，实践方法参见[《CA黑白名单操作手册》](../../manual/certificate_list.md)。

## 名词解释

**CA黑名单**

* 别称**证书拒绝列表**（certificate blacklist，简称CBL）。CA黑名单基于`config.ini`文件中`[certificate_blacklist]`配置的NodeID进行判断，拒绝此NodeID节点发起的连接。

**CA白名单**

* 别称**证书接受列表**（certificate whitelist，简称CAL）。CA白名单基于`config.ini`文件中`[certificate_whitelist]`配置的NodeID进行判断，拒绝除白名单外所有节点发起的连接。

**CA黑、白名单所属的配置类型**

- 基于**作用范围**（网络配置/账本配置）维度可划分为**网络配置**，影响整个网络的节点连接建立过程；
- 基于**是否可改**（可改配置/固定配置）维度可划分为**可改配置**，内容可改，重启后生效；
- 基于**存放位置**（本地存储/链上存储）维度可划分为**本地存储**，内容记录在本地，不存于链上。

## 模块架构

下图表示CA黑名单所涉及的模块及其关系。图例A->B表示B模块依赖A模块的数据，同时B模块晚于A模块初始化。白名单的架构与黑名单相同。

![](../../../images/node_management/architecture.png)

<center>模块架构</center>

## 核心流程

底层实现SSL双向验证。节点在handshake过程中，通过对方提供的证书获取对方节点的nodeID，检查该nodeID与节点配置的黑、白名单是否有关系。如果根据黑、白名单的配置，拒绝该关闭的connection，继续后续流程。

**拒绝逻辑**

* 黑名单：拒绝写在黑名单中的节点连接
* 白名单：拒绝所有未配置在白名单中的节点连接。白名单为空表示不开启，接受任何连接。

**优先级**

黑名单的优先级高于白名单。例如，白名单里配置了A，B，C，会拒绝掉D的连接，若黑名单里同时配了A，则A也会被拒绝连接。

## 影响范围

- CA黑、白名单对网络层的P2P节点连接及AMOP功能有显著影响，**使之失效**；
- 对账本层的共识和同步功能有潜在影响，**影响共识及同步消息/数据的转发**。

## 配置格式

**黑名单**

节点`config.ini`配置中增加`[certificate_blacklist]`路径（`[certificate_blacklist]`在配置中可选）。CA黑名单内容为节点NodeID列表，node.X为本节点拒绝连接的对方节点NodeID。CA黑名单的配置格式示例如下。

```ini
[certificate_blacklist]
    crl.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e3787c338a4
    crl.1=af57c506be9ae60df8a4a16823fa948a68550a9b6a5624df44afcd3f75ce3afc6bb1416bcb7018e1a22c5ecbd016a80ffa57b4a73adc1aeaff4508666c9b633a
   
```

**白名单**

节点`config.ini`配置中增加`[certificate_whitelist]`路径（`[certificate_whitelist]`在配置中可选）。CA白名单内容为节点NodeID列表，node.X为本节点可接受连接的对方节点NodeID。CA白名单的配置格式示例如下。

``` ini
[certificate_whitelist]
    cal.0=4d9752efbb1de1253d1d463a934d34230398e787b3112805728525ed5b9d2ba29e4ad92c6fcde5156ede8baa5aca372a209f94dc8f283c8a4fa63e3787c338a4
    cal.1=af57c506be9ae60df8a4a16823fa948a68550a9b6a5624df44afcd3f75ce3afc6bb1416bcb7018e1a22c5ecbd016a80ffa57b4a73adc1aeaff4508666c9b633a
```