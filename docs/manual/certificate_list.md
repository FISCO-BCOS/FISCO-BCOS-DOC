# 配置CA黑白名单

标签：``CA黑白名单`` ``开发手册`` ``拒绝连接``

----

本文档描述CA黑、白名单的实践操作，建议阅读本操作文档前请先行了解[《CA黑白名单介绍》](../design/security_control/certificate_list.md)。

## 黑名单

通过配置黑名单，能够拒绝与指定的节点连接。

**配置方法**

编辑`config.ini`

``` ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    ;crl.0=
```

重启节点生效

``` shell
$ bash stop.sh && bash start.sh
```

查看节点连接

``` shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

## 白名单

通过配置白名单，能够只与指定的节点连接，拒绝与白名单之外的节点连接。

**配置方法**

编辑`config.ini`，**不配置表示白名单关闭，可与任意节点建立连接。**

```ini
[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

若节点未启动，则直接启动节点，若节点已启动，可直接用脚本`reload_whitelist.sh`刷新白名单配置即可（暂不支持动态刷新黑名单）。

```shell
# 若节点未启动
$ bash start.sh
# 若节点已启动
$ cd scripts
$ bash reload_whitelist.sh
node_127.0.0.1_30300 is not running, use start.sh to start and enable whitelist directlly.
```

查看节点连接

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

## 使用场景：公共CA

所有用CFCA颁发证书搭的链，链的CA都是CFCA。此CA是共用的。必须启用白名单功能。使用公共CA搭的链，会存在两条链共用同一个CA的情况，造成无关的两条链的节点能彼此建立连接。此时需要配置白名单，拒绝与无关的链的节点建立连接。

**搭链操作步骤**

1. 用工具搭链
2. 查询所有节点的NodeID
3. 将所有NodeID配置入**每个**节点的白名单中
4. 启动节点或用脚本`reload_whitelist.sh`刷新节点白名单配置

**扩容操作步骤**

1. 用工具扩容一个节点
2. 查询此扩容节点的NodeID
3. 将此NodeID**追加**到入所有节点的白名单配置中
4. 将其他节点的白名单配置拷贝到新扩容的节点上
5. 用脚本`reload_whitelist.sh`刷新已启动的所有节点的白名单配置
6. 启动扩容节点
7. 将扩容节点加成组员（addSealer 或 addObserver）

## 黑白名单操作举例

### 准备

搭一个四个节点的链

``` bash
bash build_chain.sh -l 127.0.0.1:4
```

查看四个节点的NodeID

``` shell
$ cat node*/conf/node.nodeid
219b319ba7b2b3a1ecfa7130ea314410a52c537e6e7dda9da46dec492102aa5a43bad81679b6af0cd5b9feb7cfdc0b395cfb50016f56806a2afc7ee81bbb09bf
7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
38158ef34eb2d58ce1d31c8f3ef9f1fa829d0eb8ed1657f4b2a3ebd3265d44b243c69ffee0519c143dd67e91572ea8cb4e409144a1865f3e980c22d33d443296
```

可得四个节点的NodeID：

* **node0**: 219b319b....
* **node1**: 7718df20....
* **node2**: f306eb10....
* **node3**: 38158ef3....

启动所有节点

``` shell
$ cd node/127.0.0.1/
$ bash start_all.sh
```

查看连接，以node0为例。（8545是node0的rpc端口）

``` shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

可看到连接信息，node0连接了除自身之外的其它三个节点。

``` json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:62774",
      "Node": "node3",
      "NodeID": "38158ef34eb2d58ce1d31c8f3ef9f1fa829d0eb8ed1657f4b2a3ebd3265d44b243c69ffee0519c143dd67e91572ea8cb4e409144a1865f3e980c22d33d443296",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:62766",
      "Node": "node1",
      "NodeID": "7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    }
  ]
}
```



### 配置黑名单：node0拒绝node1的连接

将node1的NodeID写入node0的配置中

``` shell
vim node0/config.ini
```

需要进行的配置如下，白名单为空（默认关闭）

``` ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    crl.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    ; cal.0=

```

重启节点生效

``` shell
$ cd node0
$ bash stop.sh && bash start.sh
```

查看节点连接

``` shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

可看到只与两个节点建立的连接，未与node1建立连接

``` json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30303",
      "Node": "node3",
      "NodeID": "38158ef34eb2d58ce1d31c8f3ef9f1fa829d0eb8ed1657f4b2a3ebd3265d44b243c69ffee0519c143dd67e91572ea8cb4e409144a1865f3e980c22d33d443296",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    }
  ]
}
```



### 配置白名单：node0拒绝与node1，node2之外的节点连接

将node1和node2的NodeID写入node0的配置中

```shell
$ vim node0/config.ini
```

需要进行的配置如下，黑名单置空，白名单配置上node1，node2

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    ;crl.0=

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

重启节点生效

```shell
$ bash stop.sh && bash start.sh
```

查看节点连接

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

可看到只与两个节点建立的连接，未与node1建立连接

``` json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    },
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30301",
      "Node": "node1",
      "NodeID": "7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb",
      "Topic": []
    }
  ]
}
```



### 黑名单与白名单混合配置：黑名单优先级高于白名单，白名单配置的基础上拒绝与node1建立连接

编辑node0的配置

```shell
$ vim node0/config.ini
```

需要进行的配置如下，黑名单配置上node1，白名单配置上node1，node2

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 128
    crl.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 128
    cal.0=7718df20f0f7e27fdab97b3d69deebb6e289b07eb7799c7ba92fe2f43d2efb4c1250dd1f11fa5b5ce687c8283d65030aae8680093275640861bc274b1b2874cb
    cal.1=f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0
```

重启节点生效

```shell
$ bash stop.sh && bash start.sh
```

查看节点连接

```shell
$ curl -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[1],"id":1}' http://127.0.0.1:8545 |jq
```

可看到虽然白名单上配置了node1，但由于node1在黑名单中也有配置，node0也不能与node1建立连接

```json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": [
    {
      "Agency": "agency",
      "IPAndPort": "127.0.0.1:30302",
      "Node": "node2",
      "NodeID": "f306eb1066ceb9d46e3b77d2833a1bde2a9899cfc4d0433d64b01d03e79927aa60a40507c5739591b8122ee609cf5636e71b02ce5009f3b8361930ecc3a9abb0",
      "Topic": []
    }
  ]
}
```