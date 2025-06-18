# 配置说明

标签：``Node.JS SDK`` ``配置`` 

----
Node.js SDK的配置文件为一个JSON文件，主要包括**通用配置**，**群组配置**，**通信配置**和**证书配置**。

## 通用配置

- `privateKey`: `object`，必需。外部账户的私钥，可以为一个256 bits的随机整数，也可以是一个pem或p12格式的私钥文件，后两者需要结合[get_account.sh](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/account.html)生成的私钥文件使用。`privateKey`包含两个必需字段，一个可选字段：
  - `type`: `string`，必需。用于指示私钥类型。`type`的值必需为下列三个值之一：
    - `ecrandom`：随机整数
    - `pem`：pem格式的文件
    - `p12`：p12格式的文件
  - `value`：`string`，必需。用于指示私钥具体的值：
    - 如果`type`为`ecrandom`，则`value`为一个长度为256 bits 的随机整数，其值介于1 ~ 0xFFFF FFFF FFFF FFFF FFFF FFFF FFFF FFFE BAAE DCE6 AF48 A03B BFD2 5E8C D036 4141之间。
    - 如果`type`为`pem`，则`value`为pem文件的路径，如果是相对路径，需要以配置文件所在的目录为相对路径起始位置。
    - 如果`type`为`p12`，则`value`为p12文件的路径，如果是相对路径，需要以配置文件所在的目录为相对路径起始位置。
  - `password`：`string`，可选。如果`type`为`p12`，则需要此字段以解密私钥，否则会忽略该字段。
- `timeout`: `number`。Node.js SDK所连节点可能会陷入停止响应的状态。为避免陷入无限等待，Node.js SDK的每一次API调用在`timeout`之后若仍没有得到结果，则强制返回一个错误对象。`timeout`的单位为毫秒。
- `solc`: `string`，可选。Node.js SDK已经自带0.4.26及0.5.10版本的Solidity编译器，如果您有特殊的编译器需求，可以设置本配置项为您的编译器的执行路径或全局命令

## 群组配置

- `groupID`: `number`。Node.js SDK访问的链的群组ID

## 通信配置

- `nodes`: `list`，必需。FISCO BCOS节点列表，Node.js SDK在访问节点时时会从该列表中随机挑选一个节点进行通信，要求节点数目必须 >= 1。在FISCO BCOS中，一笔交易上链并不代表网络中的所有节点都已同步到了最新的状态，如果Node.js SDK连接了多个节点，则可能会出现读取不到最新状态的情况，因此在对状态同步有较高要求的场合，请谨慎连接多个节点。每个节点包含两个字段：
  - `ip`: `string`，必需。FISCO BCOS节点的IP地址
  - `port`: `string`，必需，FISCO BCOS节点的Channel端口

## 证书配置
- `authentication`：`object`。必需，包含建立Channel通信时所需的认证信息，一般在建链过程中自动生成。`authentication`包含三个必需字段：
  - `key`: `string`，必需。私钥文件路径，如果是相对路径，需要以配置文件所在的目录为相对路径起始位置。
  - `cert`: `string`，必需。证书文件路径，如果是相对路径，需要以配置文件所在的目录为相对路径起始位置。
  - `ca`: `string`，必需。CA根证书文件路径，如果是相对路径，需要以配置文件所在的目录为相对路径起始位置。
