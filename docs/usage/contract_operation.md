# 合约入门

本文作为基础，供读者初步理解智能合约的编写、部署和调用。若需要开发应用，请使用高级的智能合约开发框架web3sdk。

## 准备

查看需要部署智能合约的链，对应的节点RPC端口

```
cd /mydata/FISCO-BCOS/tools/scripts/

#sh node_info.sh -d 要查看信息的节点目录
sh node_info.sh -d /mydata/node0/
```

得到端口

``` log
RPC address:		127.0.0.1:8545
```

设置需要操作的链的RPC端口，输入y回车确认。

```sh
cd /mydata/FISCO-BCOS/tools/script/

#sh set_proxy_address.sh -o 节点的RPC address
sh set_proxy_address.sh -o 127.0.0.1:8545 
```

此后，所有的操作都会发送到``` 127.0.0.1:8545```端口上，即node0上。

## 编写合约

``` shell
cd /mydata/FISCO-BCOS/tools/contract/
vim HelloWorld.sol
```

HelloWorld.sol的实现如下

``` solidity
pragma solidity ^0.4.2;
contract HelloWorld{
    string name;
    function HelloWorld(){
       name="Hi,Welcome!";
    }
    function get()constant returns(string){
        return name;
    }
    function set(string n){
        name=n;
    }
}
```

## 编译、部署合约

直接使用deploy.js，自动编译和部署合约。

``` shell
babel-node deploy.js HelloWorld #注意后面HelloWorld后面没有".sol"
```

输出，可看到合约地址，部署成功。

``` log
deploy.js  ........................Start........................
Soc File :HelloWorld
HelloWorldcomplie success！
send transaction success: 0xa8c1aeed8e85cc0308341081925d3dab80da394f6b22c76dc0e855c8735da481
HelloWorldcontract address 0xa807685dd3cf6374ee56963d3d95065f6f056372
HelloWorld deploy success!
```

## 调用合约

### 编写合约调用程序

用nodejs实现，具体实现方法请直接看demoHelloWorld.js源码。

``` shell
vim demoHelloWorld.js
```

### 调用合约

执行合约调用程序

``` shell
babel-node demoHelloWorld.js
```

可看到合约调用成功

``` log
{ HttpProvider: 'http://127.0.0.1:8545',
  Ouputpath: './output/',
  privKey: 'bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd',
  account: '0x64fa644d2a694681bd6addd6c5e36cccd8dcdde3' }
HelloWorldcontract address:0xa807685dd3cf6374ee56963d3d95065f6f056372
HelloWorld contract get function call first :Hi,Welcome!
send transaction success: 0x6463e0ea9db6c4aff1e3fc14d9bdb86b29306def73e6d951913a522347526435
HelloWorld contract set function call , (transaction hash ：0x6463e0ea9db6c4aff1e3fc14d9bdb86b29306def73e6d951913a522347526435)
HelloWorld contract get function call again :HelloWorld!
```

