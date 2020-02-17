# 隐私保护

隐私保护是联盟链的一大技术挑战。为了保护链上数据、保障联盟成员隐私，并且保证监管的有效性，FISCO BCOS以[预编译合约](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-2.0/docs/manual/smart_contract.html)的形式集成了同态加密、群/环签名、以及零知识证明，从多个维度，在不影响可用性的情况下保护联盟隐私。

## 同态加密

### 算法简介

同态加密(Homomorphic Encryption)是公钥密码系统领域的明珠之一，已有四十余年的研究历史，由于其绝妙的密码特性以及并不友好的计算复杂度，一直让研究者们和业界人士又爱又恨，欲罢不能。

1. 同态加密本质是一种公钥加密算法，即加密使用公钥pk，解密使用私钥sk；
2. 同态加密支持密文计算，即采用相同公钥加密生成的两个密文可以计算​f( )操作，生成的新密文解密后恰好是两个原始明文计算f( )操作后的结果；
3. 同态加密公式描述如下：

$C1=Encryption(m1, pk)$

$C2=Encryption(m2, pk)$

$C3=Homomorphic(C1, C2, f(), pk)$

$Decryption(C3, sk) = f(m1, m2)$

FISCO BCOS采用的是paillier加密方案，支持加法和数乘同态。选择该方案主要有两个原因：首先，隐私模块中同态功能所辅助的业务场景简单，只需要进行资产的转移；另外，不宜在合约中实现太过复杂的计算逻辑，会大幅度降低联盟链性能。基于功能和性能的平衡，paillier这种轻量级的加同态算法自然成了首选。

### 应用场景

就目前而言，区块链是同态加密落地的一大应用方向。在联盟链中，不同的业务场景需要配套不同的隐私保护策略。对于强隐私的业务，比如金融机构之间的对账，链上数据则不适合以明文的形式存储，因此，对资产数据进行加密是很有必要的。加密虽然提高了数据的机密性，但也大大降低了数据的可用性，比如该怎样处理加密资产的增减，同态加密便是一种公认的行之有效的加密计算技术。在FISCO BCOS中，用户可以通过客户端调用同态加密算法对资产进行加密，共识节点执行交易的时候调用隐私模块的同态加密预编译合约，并计算资产增减后的结果。

## 群签名

### 算法简介

​	群签名(Group Signature)是一种能保护签名者身份的具有相对匿名性的数字签名方案，用户可以代替自己所在的群对消息进行签名，而验证者可以验证该签名是否有效，但是并不知道签名属于哪一个群成员。同时，用户无法滥用这种匿名行为，因为群管理员可以通过群主私钥打开签名，暴露签名的归属信息。群签名的特性包括：

- 匿名性：群成员用群参数产生签名，其他人仅可验证签名的有效性，并通过签名知道签名者所属群组，却无法获取签名者身份信息；
- 不可伪造性：只有群成员才能生成有效可被验证的群签名；
- 不可链接性：给定两个签名，无法判断它们是否来自同一个签名者；
- 可追踪性：在监管介入的场景中，群主可通过签名获取签名者身份。

### 应用场景

- **场景1**： 机构内成员（C端用户）通过客户端groupsig-client访问机构内群签名服务，并在链上验证签名，保证成员的匿名性和签名的不可篡改，监管也可通过群主（可信机构）追踪签名者信息（如拍卖、匿名存证等场景）。

- **场景2**：机构内下属机构各部署一套群签名服务，通过上级联盟链机构成员，将签名信息写到区块链上，链上验证群签名，保证签名的匿名性和不可篡改；（如征信等场景）。

- **场景3**：B端用户部署群签名服务，生成群签名，通过AMOP将签名信息发送给上链机构(如groupsig-client)，上链机构将收集到的签名信息统一上链（如竞标、对账等场景）。

## 环签名

### 算法简介

环签名(Ring Signature)是一种特殊的群签名方案，但具备完全匿名性，即不再存在管理员这个角色，所有成员可主动加入环，且签名无法被打开。环签名的特性包括：

- 不可伪造性：环中其他成员不能伪造真实签名者签名；
- 完全匿名性：没有群主，只有环成员，其他人仅可验证环签名的有效性，但没有人可以获取签名者身份信息。

### 应用场景

- **场景1（匿名投票）**：机构内成员（C端用户）通过客户groupsig-client访问机构内环签名服务，对投票信息进行签名，并通过可信机构将签名信息和投票结果写到链上，其他人可验证签名和投票，仅可知道发布投票到链上的机构，却无法获取投票者身份信息。

- **场景2（匿名存证、征信）**：场景与群签名匿名存证、征信场景类似，唯一的区别是任何人都无法追踪签名者身份。

- **场景3（匿名交易）**：在UTXO模型下，可将环签名算法应用于匿名交易，任何人都无法追踪转账交易双方。

## 启用方法

由于隐私模块是通过预编译合约实现，并且默认配置为不启用，因此要启用这些功能需要重新编译源码，并开启`CRYPTO_EXTENSION`编译选项。步骤如下：

- 安装依赖

  不同操作系统命令略有差异，详见技术文档[安装依赖](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-2.0/docs/manual/get_executable.html#id3)部分的介绍。

- 克隆代码

```bash
git clone https://github.com/FISCO-BCOS/FISCO-BCOS.git
```

- 编译

```bash
cd FISCO-BCOS
git checkout feature-paillier
mkdir -p build && cd build
# 开启隐私模块编译选项，CentOS请使用cmake3
cmake -DCRYPTO_EXTENSION=ON ..
# 高性能机器可添加-j4使用4核加速编译
make 
```

- 搭建联盟链 

假设当前位于`FISCO-BCOS/build`目录下，则使用下面的指令搭建本机4节点的链指令如下，更多选项[参考这里](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-2.0/docs/manual/build_chain.html)。

```bash
bash ../tools/build_chain.sh -l "127.0.0.1:4" -e bin/fisco-bcos 
```

##  调用方式

### 一. 声明接口

隐私模块的代码和用户开发的预编译合约放在一起，位于`FISCO-BCOS/libprecompiled/extension`目录，因此隐私模块的调用方式和用户开发的预编译合约[调用流程](https://fisco-bcos-documentation.readthedocs.io/zh_CN/release-2.0/docs/manual/smart_contract.html#id12)一模一样，不过有两点需要注意：

1. 已为隐私模块的预编译合约分配了地址，无需另行注册。隐私模块实现的预编译合约列表以及地址分配如下：

   | 地址   | 功能       | [源码](https://github.com/FISCO-BCOS/FISCO-BCOS/tree/master/libprecompiled/extension) |
   | ------ | ---------- | ------------------------------------------------------------ |
   | 0x5003 | 同态加密   | PaillierPrecompiled.cpp                                      |
   | 0x5004 | 群签名     | GroupSigPrecompiled.cpp                                      |
   | 0x5005 | 环签名     | RingSigPrecompiled.cpp                                       |
   | 0x5006 | 零知识证明 | ZKsnarkPrecompiled.cpp                                       |

2. 需要通过`solidity`合约方式声明隐私模块预编译合约的接口，合约文件需保存在控制台合约目录`console/contracts/solidity`中，各个隐私功能的合约接口如下，可直接复制使用:

- 同态加密

  ```solidity
  // PaillierPrecompiled.sol
  pragma solidity ^0.4.24;
  contract PaillierPrecompiled{
      function paillierAdd(string cipher1, string cipher2) public constant returns(string);
  }
  ```

- 群签名

  ```solidity
  // GroupSigPrecompiled.sol
  pragma solidity ^0.4.24;
  contract GroupSigPrecompiled{
  		function groupSigVerify(string signature, string message, string gpkInfo, string paramInfo) public constant returns(bool);
  }
  ```

- 环签名

  ```solidity
  // RingSigPrecompiled.sol
  pragma solidity ^0.4.24;
  contract RingSigPrecompiled{
       function ringSigVerify(string signature, string message, string paramInfo) public constant returns(bool);
  }
  ```

- 零知识证明

  ```solidity
  // ZKsnarkPrecompiled.sol
  pragma solidity ^0.4.24;
  contract ZKsnarkPrecompiled{
      function 待定义;
  }
  ```

### 

### 二. 调用预编译合约

#### 2.1通过控制台调用

使用编译出的二进制搭建节点后，部署控制台v1.0.2以上版本，以调用同态加密为例，执行下面语句即可:

```solidity
// 在console目录下启动控制台
bash start.sh
// 调用合约
call PaillierPrecompiled.sol 0x5003 paillierAdd "0080932D5857D9FCFD8CEEDB7593F6EAF8CD192447C6CA2F5AAA27971A19CCE957CC5E30AE56FE79DD7EC125C4AC9DE23884C58F229D1A08B56DD6C4DA8844DDFBA51AE81DC45E5F280B65BC69404370E6617DB7CEF45C12912DB6FE0709B0FFF8008B13498516BAD7F6C7453ED7C7DD0D75283A3E1D8D21D453C8F159B82A96FEBF3502ADC325CEC5750DB8029E327642E75C03A30628525E05CF0D272536432977D3981E550ADC1B2A2ACCAEBB039B1F62F1D2359A7B1D9D4B5EA6854A417FD4695A81E0D7E29319888507EADC55FC49BA2B76CF86559C770D3DD06A669CE3AF248534C85289FAE7509DE40C0E8A55E2D83F5552C99679414D4C433313C7EB296CDD0037189B00C6E9DBC33A9595A222DB990A3B7F7D6658DD532251BB160FF0C23FE691AD3240BE7A2484722EFCBB8AE10DDB7CC719B9076E394C856800539EB71D3B82FAD9DA4529D7547BAA2EA258357A3EFE5B8B0F4F0FBD36FF0D3DD25213E78AD83198865DFBC7B818C1D2B561E1B00F1D81B1986B7B8C72A629BBF67F5D" "0080932D5857D9FCFD8CEEDB7593F6EAF8CD192447C6CA2F5AAA27971A19CCE957CC5E30AE56FE79DD7EC125C4AC9DE23884C58F229D1A08B56DD6C4DA8844DDFBA51AE81DC45E5F280B65BC69404370E6617DB7CEF45C12912DB6FE0709B0FFF8008B13498516BAD7F6C7453ED7C7DD0D75283A3E1D8D21D453C8F159B82A96FEBF40C3573D2FF963EFB422A7C71BEDC1A8C83CEC7518489F52F1126791C40EC29E46E11C4DF515B2BF259E16233BD27B6E73BFB30E7767A9148568C0276457E00199512DBD4E24714D35C73F79434283F3C45115837669AB4E5FA62B48503A960FCD5C3FDADBC7D8E946B3A536CF006910DC4CA50FB3044CC67B9741641B7DE3CA9F036DD58A9192FD589C36793CBB1F541386EE84F47AEC33A26B402A0716C89C50D9F73E62C6FC4237872C0FC43B9D1FBC8C5513E8BDD1DC8AA2C6A2EF9D186A66D5FCFBC3B55D43B9E5D428C07EB4D2AE3CC98FC1CF24BFD5BCD266A924810C7C48F6EAA81CA867BB27BCCD107779E1D3CCB411F34F48A484D7C99739948D5B"
// 返回结果
0080932D5857D9FCFD8CEEDB7593F6EAF8CD192447C6CA2F5AAA27971A19CCE957CC5E30AE56FE79DD7EC125C4AC9DE23884C58F229D1A08B56DD6C4DA8844DDFBA51AE81DC45E5F280B65BC69404370E6617DB7CEF45C12912DB6FE0709B0FFF8008B13498516BAD7F6C7453ED7C7DD0D75283A3E1D8D21D453C8F159B82A96FEBF30E42C32DA8637D04B0A1091638296FE9DB669610CF2575AB24549E34CEDEC725BE73A6888D4B0163BBF3360881EF7808C859EFF6363AF5D087B637EC2AE4908FDDC3DB91DBE8EF489AC6658378600154A0426E4580E003518C5D3E05060C998B29AA1D9E9A1B154E160E7FA3F14FF903837527A038C3973E0312D72EE43CD447CA8EFA4569BBF6A1C56D1FA99A7B494EABA641E977B3A063537092CF872C5FFF6C88B8BE7E92F1405EE22BAAEA60EF4B0DE755956D8981A193330E369D351790DA56504892094777D18D5F44626597C736B4BCC6ECE99A6CACE70137773F0E0DB737B5521F4525027032AF0D92721D723CAE9DCCF001250240012602AE3FFE2
```

#### 2.2 通过solidity合约调用

以调用同态加密为例，通过在`solidity`合约中创建预编译合约对象并调用其接口，在控制台`console/contracts/solidity`创建`CallPaillier.sol`文件，文件内容如下:

```solidity
// CallPaillier.sol
pragma solidity ^0.4.24;
import "./PaillierPrecompiled.sol";

contract CallPaillier {
    PaillierPrecompiled paillier;
    function CallPaillier() {
        // 调用PaillierPrecompiled预编译合约
        paillier = PaillierPrecompiled(0x5003); 
    }
    function add(string cipher1, string cipher2) public constant returns(string) {
        return paillier.paillierAdd(cipher1, cipher2);
    }
}
```

部署`CallPaillier`合约，然后调用`CallPaillier`合约的接口，结果如下 :

![](../../images/privacy/callpaillier.png)



###   三. 调用群/环签名完成服务

FISCO BCOS为用户提供了完整的群/环签名服务，可通过[签名客户端](https://github.com/FISCO-BCOS/sig-service-client/tree/dev-2.0)实现群和环的创建、签名生成、签名上链以及链上验证等功能，具体操作方法请参阅[客户端指南](https://github.com/FISCO-BCOS/sig-service-client/tree/dev-2.0)。