# CA黑白名单介绍

标签：``安全控制`` ``网络安全`` ``黑白名单``

----

本文档对黑、白名单进行介绍性说明，实践方法参见[《CA黑白名单操作手册》](../../manual/certificate_list.md)。

## 名词解释

**CA黑名单**

* 别称**证书拒绝列表**（certificate blacklist，简称CRL）。CA黑名单基于`config.ini`文件中`[certificate_blacklist]`配置的SSL握手ID进行判断，拒绝此SSL握手ID节点发起的连接。

**CA白名单**

* 别称**证书接受列表**（certificate whitelist，简称CAL）。CA白名单基于`config.ini`文件中`[certificate_whitelist]`配置的SSL握手ID进行判断，拒绝除白名单外所有节点发起的连接。

**CA黑、白名单所属的配置类型**

- 基于**作用范围**（网络配置/账本配置）维度可划分为**网络配置**，影响整个网络的节点连接建立过程；
- 基于**是否可改**（可改配置/固定配置）维度可划分为**可改配置**，内容可改，重启后生效；
- 基于**存放位置**（本地存储/链上存储）维度可划分为**本地存储**，内容记录在本地，不存于链上。

## 模块架构

下图表示CA黑名单所涉及的模块及其关系。图例A->B表示B模块依赖A模块的数据，同时B模块晚于A模块初始化。白名单的架构与黑名单相同。

![](../../../images/design/node_management_architecture.png)

<center>模块架构</center>

## 核心流程

底层实现SSL双向验证。节点在handshake过程中，通过对方提供的证书获取对方节点的SSL握手ID，检查该SSL握手ID与节点配置的黑、白名单是否有关系。如果根据黑、白名单的配置，拒绝该关闭的connection，继续后续流程。

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

节点`config.ini`配置中增加`[certificate_blacklist]`路径（`[certificate_blacklist]`在配置中可选）。CA黑名单内容为节点SSL握手ID列表，crl.X为本节点拒绝连接的对方节点SSL握手ID。CA黑名单的配置格式示例如下。

```ini
[certificate_blacklist]
    crl.0=a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d
    crl.1=9eaecd6ba0de0f8cf77f58bd6179112cac3cf1e7c4fd24a6a9958daf8ea289fedb651b87d802136969d57c700ee4443dbf25da3505b90f7c9fb75c8cf514d0298b65815178f366f2c3b14186367071e3e07ccdb07b61609c5d6cbad9f991012d4e15ab71f12e160c764b67f748943586c36c7ae4d27dbd17e83c9ffacf2e7fe59af6429f506ddf2f4e9ca8b528da4ab2a340b3b549898e71b4b2b05d656f98d37b292910bc0227146bf5f8c3ff8d80a3d6938a29a03961609ef3fa07f4b3d10917e5fa93518ef9078c883c2433ae91fa49df75115855ddc5a9eeab30aea7de15e887e4e53424c898cbd8b95daf3b08d61a4b2baef048a3bd30b7cf70a1c085df
   
```

**白名单**

节点`config.ini`配置中增加`[certificate_whitelist]`路径（`[certificate_whitelist]`在配置中可选）。CA白名单内容为节点SSL握手ID列表，cal.X为本节点可接受连接的对方节点SSL握手ID。CA白名单的配置格式示例如下。

``` ini
[certificate_whitelist]
    cal.0=a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d
    cal.1=9eaecd6ba0de0f8cf77f58bd6179112cac3cf1e7c4fd24a6a9958daf8ea289fedb651b87d802136969d57c700ee4443dbf25da3505b90f7c9fb75c8cf514d0298b65815178f366f2c3b14186367071e3e07ccdb07b61609c5d6cbad9f991012d4e15ab71f12e160c764b67f748943586c36c7ae4d27dbd17e83c9ffacf2e7fe59af6429f506ddf2f4e9ca8b528da4ab2a340b3b549898e71b4b2b05d656f98d37b292910bc0227146bf5f8c3ff8d80a3d6938a29a03961609ef3fa07f4b3d10917e5fa93518ef9078c883c2433ae91fa49df75115855ddc5a9eeab30aea7de15e887e4e53424c898cbd8b95daf3b08d61a4b2baef048a3bd30b7cf70a1c085df
```
