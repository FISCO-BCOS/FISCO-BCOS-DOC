# 配置CA黑白名单

标签：``CA黑白名单`` ``开发手册`` ``拒绝连接``

----

本文档描述CA黑、白名单的实践操作，建议阅读本操作文档前请先行了解[《CA黑白名单介绍》](../design/security_control/certificate_list.md)。

## 黑名单

通过配置黑名单，能够拒绝与指定的节点连接。

**配置方法**

查看节点SSL握手ID

```shell
$ cat node0/conf/ssl.nodeid (sm: cat node0/conf/sm_ssl.nodeid)
```

编辑`config.ini`

``` ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 512/128(sm)
    ;crl.0=
```

重启节点生效

``` shell
$ bash stop.sh && bash start.sh
```

查看节点连接

``` shell
$ curl -k --cert sdk/sdk.crt --key sdk/sdk.key -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[],"id":1}' https://127.0.0.1:20200 | jq
```

## 白名单

通过配置白名单，能够只与指定的节点连接，拒绝与白名单之外的节点连接。

**配置方法**

查看节点SSL握手ID

```shell
$ cat node0/conf/ssl.nodeid (sm: cat node0/conf/sm_ssl.nodeid)
```

编辑`config.ini`，**不配置表示白名单关闭，可与任意节点建立连接。**

```ini
[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 512/128(sm)
    cal.0=a525a88e43f696b1aaa5d7f6ea82f08763dd1097f031103a5911a18591e7e9887aeb7535ee717a3d76ba2d5a8ce05e95996871355321104b0acd5e8d2f7620fb180187ded42243c5f5762b8c63922f9285230c89fabb10fee18c6ff76a157679653a0fd22c00b5dd71f1e38243560a5669dd4adb169b7f7dc5f3664b28fab3d28456c8dbcc2f9832ac644c45f49663d30464329711d83da3a8b43448498a783f7dee3d7f5e064ca255741634cdb4e6f818f58d1cad9020320f4e8f330b90a056018f981c012db84850ae6585bff40afb833441ca929ddc64ee2d3cd1882711e3f12e7209916a5b9098fa71dc3e69a8e139c10cdb0d33454c8b4ddc6d28f02f27
    cal.1=a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d
```

若节点未启动，则直接启动节点，若节点已启动，需要重启节点（暂不支持动态刷新黑名单）。

查看节点连接

```shell
$ curl -k --cert sdk/sdk.crt --key sdk/sdk.key -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[],"id":1}' https://127.0.0.1:20200 | jq
```

## 使用场景：公共CA

所有用CFCA颁发证书搭的链，链的CA都是CFCA。此CA是共用的。必须启用白名单功能。使用公共CA搭的链，会存在两条链共用同一个CA的情况，造成无关的两条链的节点能彼此建立连接。此时需要配置白名单，拒绝与无关的链的节点建立连接。

**搭链操作步骤**

1. 用工具搭链
2. 查询所有节点的SSL握手ID
3. 将所有SSL握手ID配置入**每个**节点的白名单中
4. 启动节点或重启节点

**扩容操作步骤**

1. 用工具扩容一个节点
2. 查询此扩容节点的SSL握手ID
3. 将此SSL握手ID**追加**到入所有节点的白名单配置中
4. 将其他节点的白名单配置拷贝到新扩容的节点上
5. 重启/启动所有节点
7. 将扩容节点加成组员（addSealer 或 addObserver）

## 黑白名单操作举例

### 准备

搭一个四个节点的链

``` bash
bash build_chain.sh -l 127.0.0.1:4
```

查看四个节点的SSL握手ID

``` shell
$ cat node*/conf/ssl.nodeid
a525a88e43f696b1aaa5d7f6ea82f08763dd1097f031103a5911a18591e7e9887aeb7535ee717a3d76ba2d5a8ce05e95996871355321104b0acd5e8d2f7620fb180187ded42243c5f5762b8c63922f9285230c89fabb10fee18c6ff76a157679653a0fd22c00b5dd71f1e38243560a5669dd4adb169b7f7dc5f3664b28fab3d28456c8dbcc2f9832ac644c45f49663d30464329711d83da3a8b43448498a783f7dee3d7f5e064ca255741634cdb4e6f818f58d1cad9020320f4e8f330b90a056018f981c012db84850ae6585bff40afb833441ca929ddc64ee2d3cd1882711e3f12e7209916a5b9098fa71dc3e69a8e139c10cdb0d33454c8b4ddc6d28f02f27
a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d
9eaecd6ba0de0f8cf77f58bd6179112cac3cf1e7c4fd24a6a9958daf8ea289fedb651b87d802136969d57c700ee4443dbf25da3505b90f7c9fb75c8cf514d0298b65815178f366f2c3b14186367071e3e07ccdb07b61609c5d6cbad9f991012d4e15ab71f12e160c764b67f748943586c36c7ae4d27dbd17e83c9ffacf2e7fe59af6429f506ddf2f4e9ca8b528da4ab2a340b3b549898e71b4b2b05d656f98d37b292910bc0227146bf5f8c3ff8d80a3d6938a29a03961609ef3fa07f4b3d10917e5fa93518ef9078c883c2433ae91fa49df75115855ddc5a9eeab30aea7de15e887e4e53424c898cbd8b95daf3b08d61a4b2baef048a3bd30b7cf70a1c085df
de0cbdca344b6db755c4949c4138ce0c16992696321fbe28dbb88bd85e41283253d20e50d3ac494b8b41d35b5bd4919f98b459e77de8c960101df2d4447fe7f845c5c387a29552033869979d3c96e538b9b1303e20116a3cf98c5b3aaf8d1261be4ab06cf1f3d23e111f7ffa5567eabccf13433812ebee2ad3bec5fe6f67f75f1e1ad41ed143a969fdcc1dd2133f9d15f15767dfca4c5446f3ace20bc6f30220e785f34304e97cc5329ef16cbbafb066fb2c6eba636612e8c043e03ee4f66bad34b394eb555c64254f555689b019a7eb84322d86597d2ec12afe731d9b127d704116cb537d66d6df2c299ce1f801308900110035b2bdc80080253ba20bf23521
```

可得四个节点的SSL握手ID：

* **node0**: a525a88e....
* **node1**: a8cffe07....
* **node2**: 9eaecd6b....
* **node3**: de0cbdca....

启动所有节点

``` shell
$ cd node/127.0.0.1/
$ bash start_all.sh
```

查看连接，以node0为例。（20200是node0的rpc端口）

``` shell
$ curl -k --cert sdk/sdk.crt --key sdk/sdk.key -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[],"id":1}' https://127.0.0.1:20200 | jq
```

可看到连接信息，node0连接了除自身之外的其它三个节点。

``` json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "endPoint": "0.0.0.0:30300",
    "groupNodeIDInfo": [
      {
        "group": "group0",
        "nodeIDList": [
          "89a54f4d8f0bf80dd39012faafba407ff610cd4a2759d9d18d83df45e13f1787f90e123b6c1634ce1e80aa71c2e8159c04c9cfabd0e7a3b6bbbb89c8216059b0"
        ]
      }
    ],
    "p2pNodeID": "A525A88E43F696B1AAA5D7F6EA82F08763DD1097F031103A5911A18591E7E9887AEB7535EE717A3D76BA2D5A8CE05E95996871355321104B0ACD5E8D2F7620FB180187DED42243C5F5762B8C63922F9285230C89FABB10FEE18C6FF76A157679653A0FD22C00B5DD71F1E38243560A5669DD4ADB169B7F7DC5F3664B28FAB3D28456C8DBCC2F9832AC644C45F49663D30464329711D83DA3A8B43448498A783F7DEE3D7F5E064CA255741634CDB4E6F818F58D1CAD9020320F4E8F330B90A056018F981C012DB84850AE6585BFF40AFB833441CA929DDC64EE2D3CD1882711E3F12E7209916A5B9098FA71DC3E69A8E139C10CDB0D33454C8B4DDC6D28F02F27",
    "peers": [
        {
        "endPoint": "127.0.0.1:30301",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "1bde8ed7a8e3ee0ca78c76de72c8c578de4b58de291a7c7952c41cd384e826f5ce93b3953520eb532f3bf771419594876cc388c75e0f019ad745b72f7d6029b3"
            ]
          }
        ],
        "p2pNodeID": "A8CFFE07A6F55E5B6F84A8A9E8CD2F8B6AFDDBF8D851CCCE51516D978FC81AA56154C9998A0F178E91DF082F2F5634ED255C2B3083651BD8552E5A2378DD9DB61B17AF680716F708FC8F4D7C01EFD13F3903C5C34A87E0B6F4F45FD0046C2B7B129ABB5B300A24F01EFDAEA678106622FE28ABD5FF15EA848ACE68F5E4D6D38D560C812FFFA2D12A9B08B579981C03FF0FECD40AC6C9EF8182AA97D3BFF53DC9F2F85D8AA37C58C2A1A24E0AC5F7BFF626F07D89B3D540DF91B597F672703203BDDDD6BEFC7FD34DA71B684C33F378300948DD8B664269EB4CDF6CABF902459E876D52D724DA695017E29D504AD202ED366060BBEB5B3476C1E768AB4A93679D"
      },
      {
        "endPoint": "127.0.0.1:30302",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "1b33d04744c4e5b763dbbf3ec101394b9e1918790d3d9ac518dface162df2e7ecdd0fe27a447a86315da311e7de45602ee8d1a96405ee5e104263b7dac95b2e1"
            ]
          }
        ],
        "p2pNodeID": "9EAECD6BA0DE0F8CF77F58BD6179112CAC3CF1E7C4FD24A6A9958DAF8EA289FEDB651B87D802136969D57C700EE4443DBF25DA3505B90F7C9FB75C8CF514D0298B65815178F366F2C3B14186367071E3E07CCDB07B61609C5D6CBAD9F991012D4E15AB71F12E160C764B67F748943586C36C7AE4D27DBD17E83C9FFACF2E7FE59AF6429F506DDF2F4E9CA8B528DA4AB2A340B3B549898E71B4B2B05D656F98D37B292910BC0227146BF5F8C3FF8D80A3D6938A29A03961609EF3FA07F4B3D10917E5FA93518EF9078C883C2433AE91FA49DF75115855DDC5A9EEAB30AEA7DE15E887E4E53424C898CBD8B95DAF3B08D61A4B2BAEF048A3BD30B7CF70A1C085DF"
      },
      {
        "endPoint": "127.0.0.1:30303",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "67cb5123f6b49ad2de5a13578e0eaeda2792937a4195c0127c57481cca07cf637c6fb7f4420aef576230a1f2c2f5ff34d207c8eb69fe3609fbddf74dddbc6a4c"
            ]
          }
        ],
        "p2pNodeID": "DE0CBDCA344B6DB755C4949C4138CE0C16992696321FBE28DBB88BD85E41283253D20E50D3AC494B8B41D35B5BD4919F98B459E77DE8C960101DF2D4447FE7F845C5C387A29552033869979D3C96E538B9B1303E20116A3CF98C5B3AAF8D1261BE4AB06CF1F3D23E111F7FFA5567EABCCF13433812EBEE2AD3BEC5FE6F67F75F1E1AD41ED143A969FDCC1DD2133F9D15F15767DFCA4C5446F3ACE20BC6F30220E785F34304E97CC5329EF16CBBAFB066FB2C6EBA636612E8C043E03EE4F66BAD34B394EB555C64254F555689B019A7EB84322D86597D2EC12AFE731D9B127D704116CB537D66D6DF2C299CE1F801308900110035B2BDC80080253BA20BF23521"
      }
    ]
  }
}
```



### 配置黑名单：node0拒绝node1的连接

将node1的SSL握手ID写入node0的配置中

``` shell
vim node0/config.ini
```

需要进行的配置如下，白名单为空（默认关闭）

``` ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 512
    crl.0=a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 512
    ; cal.0=

```

重启节点生效

``` shell
$ cd node0
$ bash stop.sh && bash start.sh
```

查看节点连接

``` shell
$ curl -k --cert sdk/sdk.crt --key sdk/sdk.key -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[],"id":1}' https://127.0.0.1:20200 | jq
```

可看到只与两个节点建立的连接，未与node1建立连接

``` json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "endPoint": "0.0.0.0:30300",
    "groupNodeIDInfo": [
      {
        "group": "group0",
        "nodeIDList": [
          "89a54f4d8f0bf80dd39012faafba407ff610cd4a2759d9d18d83df45e13f1787f90e123b6c1634ce1e80aa71c2e8159c04c9cfabd0e7a3b6bbbb89c8216059b0"
        ]
      }
    ],
    "p2pNodeID": "A525A88E43F696B1AAA5D7F6EA82F08763DD1097F031103A5911A18591E7E9887AEB7535EE717A3D76BA2D5A8CE05E95996871355321104B0ACD5E8D2F7620FB180187DED42243C5F5762B8C63922F9285230C89FABB10FEE18C6FF76A157679653A0FD22C00B5DD71F1E38243560A5669DD4ADB169B7F7DC5F3664B28FAB3D28456C8DBCC2F9832AC644C45F49663D30464329711D83DA3A8B43448498A783F7DEE3D7F5E064CA255741634CDB4E6F818F58D1CAD9020320F4E8F330B90A056018F981C012DB84850AE6585BFF40AFB833441CA929DDC64EE2D3CD1882711E3F12E7209916A5B9098FA71DC3E69A8E139C10CDB0D33454C8B4DDC6D28F02F27",
    "peers": [
      {
        "endPoint": "127.0.0.1:30302",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "1b33d04744c4e5b763dbbf3ec101394b9e1918790d3d9ac518dface162df2e7ecdd0fe27a447a86315da311e7de45602ee8d1a96405ee5e104263b7dac95b2e1"
            ]
          }
        ],
        "p2pNodeID": "9EAECD6BA0DE0F8CF77F58BD6179112CAC3CF1E7C4FD24A6A9958DAF8EA289FEDB651B87D802136969D57C700EE4443DBF25DA3505B90F7C9FB75C8CF514D0298B65815178F366F2C3B14186367071E3E07CCDB07B61609C5D6CBAD9F991012D4E15AB71F12E160C764B67F748943586C36C7AE4D27DBD17E83C9FFACF2E7FE59AF6429F506DDF2F4E9CA8B528DA4AB2A340B3B549898E71B4B2B05D656F98D37B292910BC0227146BF5F8C3FF8D80A3D6938A29A03961609EF3FA07F4B3D10917E5FA93518EF9078C883C2433AE91FA49DF75115855DDC5A9EEAB30AEA7DE15E887E4E53424C898CBD8B95DAF3B08D61A4B2BAEF048A3BD30B7CF70A1C085DF"
      },
      {
        "endPoint": "127.0.0.1:30303",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "67cb5123f6b49ad2de5a13578e0eaeda2792937a4195c0127c57481cca07cf637c6fb7f4420aef576230a1f2c2f5ff34d207c8eb69fe3609fbddf74dddbc6a4c"
            ]
          }
        ],
        "p2pNodeID": "DE0CBDCA344B6DB755C4949C4138CE0C16992696321FBE28DBB88BD85E41283253D20E50D3AC494B8B41D35B5BD4919F98B459E77DE8C960101DF2D4447FE7F845C5C387A29552033869979D3C96E538B9B1303E20116A3CF98C5B3AAF8D1261BE4AB06CF1F3D23E111F7FFA5567EABCCF13433812EBEE2AD3BEC5FE6F67F75F1E1AD41ED143A969FDCC1DD2133F9D15F15767DFCA4C5446F3ACE20BC6F30220E785F34304E97CC5329EF16CBBAFB066FB2C6EBA636612E8C043E03EE4F66BAD34B394EB555C64254F555689B019A7EB84322D86597D2EC12AFE731D9B127D704116CB537D66D6DF2C299CE1F801308900110035B2BDC80080253BA20BF23521"
      }
    ]
  }
}
```



### 配置白名单：node0拒绝与node1，node2之外的节点连接

将node1和node2的SSL握手ID写入node0的配置中

```shell
$ vim node0/config.ini
```

需要进行的配置如下，黑名单置空，白名单配置上node1，node2

```ini
[certificate_blacklist]
    ; crl.0 should be nodeid, nodeid's length is 512
    ;crl.0=

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 512
    cal.0=a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d
    cal.1=9eaecd6ba0de0f8cf77f58bd6179112cac3cf1e7c4fd24a6a9958daf8ea289fedb651b87d802136969d57c700ee4443dbf25da3505b90f7c9fb75c8cf514d0298b65815178f366f2c3b14186367071e3e07ccdb07b61609c5d6cbad9f991012d4e15ab71f12e160c764b67f748943586c36c7ae4d27dbd17e83c9ffacf2e7fe59af6429f506ddf2f4e9ca8b528da4ab2a340b3b549898e71b4b2b05d656f98d37b292910bc0227146bf5f8c3ff8d80a3d6938a29a03961609ef3fa07f4b3d10917e5fa93518ef9078c883c2433ae91fa49df75115855ddc5a9eeab30aea7de15e887e4e53424c898cbd8b95daf3b08d61a4b2baef048a3bd30b7cf70a1c085df
```

重启节点生效

```shell
$ bash stop.sh && bash start.sh
```

查看节点连接

```shell
$ curl -k --cert sdk/sdk.crt --key sdk/sdk.key -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[],"id":1}' https://127.0.0.1:20200 | jq
```

可看到只与两个节点建立的连接，未与node3建立连接

``` json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "endPoint": "0.0.0.0:30300",
    "groupNodeIDInfo": [
      {
        "group": "group0",
        "nodeIDList": [
          "89a54f4d8f0bf80dd39012faafba407ff610cd4a2759d9d18d83df45e13f1787f90e123b6c1634ce1e80aa71c2e8159c04c9cfabd0e7a3b6bbbb89c8216059b0"
        ]
      }
    ],
    "p2pNodeID": "A525A88E43F696B1AAA5D7F6EA82F08763DD1097F031103A5911A18591E7E9887AEB7535EE717A3D76BA2D5A8CE05E95996871355321104B0ACD5E8D2F7620FB180187DED42243C5F5762B8C63922F9285230C89FABB10FEE18C6FF76A157679653A0FD22C00B5DD71F1E38243560A5669DD4ADB169B7F7DC5F3664B28FAB3D28456C8DBCC2F9832AC644C45F49663D30464329711D83DA3A8B43448498A783F7DEE3D7F5E064CA255741634CDB4E6F818F58D1CAD9020320F4E8F330B90A056018F981C012DB84850AE6585BFF40AFB833441CA929DDC64EE2D3CD1882711E3F12E7209916A5B9098FA71DC3E69A8E139C10CDB0D33454C8B4DDC6D28F02F27",
    "peers": [
        {
        "endPoint": "127.0.0.1:30301",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "1bde8ed7a8e3ee0ca78c76de72c8c578de4b58de291a7c7952c41cd384e826f5ce93b3953520eb532f3bf771419594876cc388c75e0f019ad745b72f7d6029b3"
            ]
          }
        ],
        "p2pNodeID": "A8CFFE07A6F55E5B6F84A8A9E8CD2F8B6AFDDBF8D851CCCE51516D978FC81AA56154C9998A0F178E91DF082F2F5634ED255C2B3083651BD8552E5A2378DD9DB61B17AF680716F708FC8F4D7C01EFD13F3903C5C34A87E0B6F4F45FD0046C2B7B129ABB5B300A24F01EFDAEA678106622FE28ABD5FF15EA848ACE68F5E4D6D38D560C812FFFA2D12A9B08B579981C03FF0FECD40AC6C9EF8182AA97D3BFF53DC9F2F85D8AA37C58C2A1A24E0AC5F7BFF626F07D89B3D540DF91B597F672703203BDDDD6BEFC7FD34DA71B684C33F378300948DD8B664269EB4CDF6CABF902459E876D52D724DA695017E29D504AD202ED366060BBEB5B3476C1E768AB4A93679D"
      },
      {
        "endPoint": "127.0.0.1:30302",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "1b33d04744c4e5b763dbbf3ec101394b9e1918790d3d9ac518dface162df2e7ecdd0fe27a447a86315da311e7de45602ee8d1a96405ee5e104263b7dac95b2e1"
            ]
          }
        ],
        "p2pNodeID": "9EAECD6BA0DE0F8CF77F58BD6179112CAC3CF1E7C4FD24A6A9958DAF8EA289FEDB651B87D802136969D57C700EE4443DBF25DA3505B90F7C9FB75C8CF514D0298B65815178F366F2C3B14186367071E3E07CCDB07B61609C5D6CBAD9F991012D4E15AB71F12E160C764B67F748943586C36C7AE4D27DBD17E83C9FFACF2E7FE59AF6429F506DDF2F4E9CA8B528DA4AB2A340B3B549898E71B4B2B05D656F98D37B292910BC0227146BF5F8C3FF8D80A3D6938A29A03961609EF3FA07F4B3D10917E5FA93518EF9078C883C2433AE91FA49DF75115855DDC5A9EEAB30AEA7DE15E887E4E53424C898CBD8B95DAF3B08D61A4B2BAEF048A3BD30B7CF70A1C085DF"
      }
    ]
  }
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
    ; crl.0 should be nodeid, nodeid's length is 512
    crl.0=a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d

[certificate_whitelist]
    ; cal.0 should be nodeid, nodeid's length is 512
    cal.0=a8cffe07a6f55e5b6f84a8a9e8cd2f8b6afddbf8d851ccce51516d978fc81aa56154c9998a0f178e91df082f2f5634ed255c2b3083651bd8552e5a2378dd9db61b17af680716f708fc8f4d7c01efd13f3903c5c34a87e0b6f4f45fd0046c2b7b129abb5b300a24f01efdaea678106622fe28abd5ff15ea848ace68f5e4d6d38d560c812fffa2d12a9b08b579981c03ff0fecd40ac6c9ef8182aa97d3bff53dc9f2f85d8aa37c58c2a1a24e0ac5f7bff626f07d89b3d540df91b597f672703203bdddd6befc7fd34da71b684c33f378300948dd8b664269eb4cdf6cabf902459e876d52d724da695017e29d504ad202ed366060bbeb5b3476c1e768ab4a93679d
    cal.1=9eaecd6ba0de0f8cf77f58bd6179112cac3cf1e7c4fd24a6a9958daf8ea289fedb651b87d802136969d57c700ee4443dbf25da3505b90f7c9fb75c8cf514d0298b65815178f366f2c3b14186367071e3e07ccdb07b61609c5d6cbad9f991012d4e15ab71f12e160c764b67f748943586c36c7ae4d27dbd17e83c9ffacf2e7fe59af6429f506ddf2f4e9ca8b528da4ab2a340b3b549898e71b4b2b05d656f98d37b292910bc0227146bf5f8c3ff8d80a3d6938a29a03961609ef3fa07f4b3d10917e5fa93518ef9078c883c2433ae91fa49df75115855ddc5a9eeab30aea7de15e887e4e53424c898cbd8b95daf3b08d61a4b2baef048a3bd30b7cf70a1c085df
```

重启节点生效

```shell
$ bash stop.sh && bash start.sh
```

查看节点连接

```shell
$ curl -k --cert sdk/sdk.crt --key sdk/sdk.key -X POST --data '{"jsonrpc":"2.0","method":"getPeers","params":[],"id":1}' https://127.0.0.1:20200 | jq
```

可看到虽然白名单上配置了node1，但由于node1在黑名单中也有配置，node0也不能与node1建立连接

```json
{
  "id": 1,
  "jsonrpc": "2.0",
  "result": {
    "endPoint": "0.0.0.0:30300",
    "groupNodeIDInfo": [
      {
        "group": "group0",
        "nodeIDList": [
          "89a54f4d8f0bf80dd39012faafba407ff610cd4a2759d9d18d83df45e13f1787f90e123b6c1634ce1e80aa71c2e8159c04c9cfabd0e7a3b6bbbb89c8216059b0"
        ]
      }
    ],
    "p2pNodeID": "A525A88E43F696B1AAA5D7F6EA82F08763DD1097F031103A5911A18591E7E9887AEB7535EE717A3D76BA2D5A8CE05E95996871355321104B0ACD5E8D2F7620FB180187DED42243C5F5762B8C63922F9285230C89FABB10FEE18C6FF76A157679653A0FD22C00B5DD71F1E38243560A5669DD4ADB169B7F7DC5F3664B28FAB3D28456C8DBCC2F9832AC644C45F49663D30464329711D83DA3A8B43448498A783F7DEE3D7F5E064CA255741634CDB4E6F818F58D1CAD9020320F4E8F330B90A056018F981C012DB84850AE6585BFF40AFB833441CA929DDC64EE2D3CD1882711E3F12E7209916A5B9098FA71DC3E69A8E139C10CDB0D33454C8B4DDC6D28F02F27",
    "peers": [
      {
        "endPoint": "127.0.0.1:30302",
        "groupNodeIDInfo": [
          {
            "group": "group0",
            "nodeIDList": [
              "1b33d04744c4e5b763dbbf3ec101394b9e1918790d3d9ac518dface162df2e7ecdd0fe27a447a86315da311e7de45602ee8d1a96405ee5e104263b7dac95b2e1"
            ]
          }
        ],
        "p2pNodeID": "9EAECD6BA0DE0F8CF77F58BD6179112CAC3CF1E7C4FD24A6A9958DAF8EA289FEDB651B87D802136969D57C700EE4443DBF25DA3505B90F7C9FB75C8CF514D0298B65815178F366F2C3B14186367071E3E07CCDB07B61609C5D6CBAD9F991012D4E15AB71F12E160C764B67F748943586C36C7AE4D27DBD17E83C9FFACF2E7FE59AF6429F506DDF2F4E9CA8B528DA4AB2A340B3B549898E71B4B2B05D656F98D37B292910BC0227146BF5F8C3FF8D80A3D6938A29A03961609EF3FA07F4B3D10917E5FA93518EF9078C883C2433AE91FA49DF75115855DDC5A9EEAB30AEA7DE15E887E4E53424C898CBD8B95DAF3B08D61A4B2BAEF048A3BD30B7CF70A1C085DF"
      }
    ]
  }
}
```