使用Truffle创建区块链项目

<h4 id="jVXbf">**<font style="color:#000000;">Truffle 是什么？</font>**</h4>
+ **<font style="color:#000000;">Truffle</font>** 是以太坊生态系统中最流行的开发框架之一，专为构建、测试和部署智能合约而设计。

<h4 id="EX0Vj">Truffle的**<font style="color:#000000;">核心功能</font>**</h4>
+ **<font style="color:#000000;background-color:cyan;">智能合约开发与编译</font>****<font style="color:#000000;">：</font>**自动编译 Solidity 智能合约，支持多种版本管理。
+ **<font style="color:#000000;">开发环境与测试网络：</font>**集成 **<font style="color:#000000;">Truffle Develop</font>** 本地开发链，支持实时调试兼容 Ganache、Geth 等多种测试网络，可通过配置文件快速切换环境。
+ **<font style="color:#000000;">自动化测试框架：</font>**支持 JavaScript/TypeScript 和 Solidity 两种测试语言；内置断言库，可编写合约交互测试（如转账、权限验证）。

<h3 id="NXmg8">**<font style="color:#000000;">一、环境准备</font>**</h3>
<h4 id="CjTZU">**<font style="color:#000000;">1. 安装 Node.js 和 npm</font>**</h4>
<font style="color:#000000;">Node.j：</font>是JavaScript运行时环境，<font style="color:#0000FF;">为Truffle及区块链项目提供运行基础</font>，让你能用JavaScript开发、测试和部署智能合约，还能执行Truffle命令。

**<font style="color:#000000;">npm</font>**<font style="color:#000000;">：是 Node.js 的包管理器，可用于安装 Truffle 框架（npm install -g truffle ），还能管理项目依赖（如智能合约开发依赖的库、测试框架等 ），方便获取和更新开发所需资源 。</font>

<font style="background-color:cyan;">Node.js 提供运行环境，npm 负责装工具和管理依赖</font>

<font style="color:#000000;"># 添加Node.js仓库</font>

<font style="color:#FF5D4D;">curl</font><font style="color:#000000;"> </font><font style="color:#4DA621;">-sL</font><font style="color:#000000;"> </font>[<font style="color:#000000;">https://deb.nodesource.com/setup_20.x</font>](https://deb.nodesource.com/setup_20.x)<font style="color:#000000;"> | </font><font style="color:#FF5D4D;">sudo</font><font style="color:#000000;"> </font><font style="color:#4DA621;">-E</font><font style="color:#000000;"> </font><font style="color:#FF5D4D;">bash</font><font style="color:#000000;"> -</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237817970-fe6b5cd4-f600-4f18-b6d8-2ea5e742cb15.png)

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237818188-a8fdb89f-0079-48e3-8095-f0650d15cb97.png)

<font style="color:#000000;"># 安装Node.js和npm </font><font style="color:#000000;background-color:cyan;">（慢）</font>

<font style="color:#FF5D4D;">sudo</font><font style="color:#000000;"> </font><font style="color:#FF5D4D;">apt</font><font style="color:#000000;"> </font><font style="color:#FF5D4D;">install</font><font style="color:#000000;"> -y nodejs</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237818401-daa75b0b-7b12-4ad6-b948-0cd00824470f.png)

<font style="color:#000000;"># 验证安装</font>

<font style="color:#FF5D4D;">node</font><font style="color:#000000;"> </font><font style="color:#4DA621;">-v</font><font style="color:#000000;">  # 应输出v20.x.x </font>

<font style="color:#000000;">npm -v   # 应显示对应的npm版本</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237818605-9ce186d5-c5fd-4eda-9637-39d35198e5b7.png)







<h4 id="PxRCN">**<font style="color:#000000;">2. 安装 Truffle 框架</font>**</h4>
<font style="color:#FF5D4D;">sudo npm</font><font style="color:#000000;"> </font><font style="color:#FF5D4D;">install</font><font style="color:#000000;"> </font><font style="color:#4DA621;">-g</font><font style="color:#000000;"> truffle --verbose </font><font style="color:#000000;background-color:cyan;">（慢）</font>

+ **<font style="color:#000000;">-g</font>**<font style="color:#000000;">：全局安装。</font>
+ **<font style="color:#000000;">--verbose</font>**<font style="color:#000000;">：详细模式参数，显示安装过程中的所有日志信息。</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237819000-f4725926-8f27-4f9a-a29c-516f8b24ee2b.png)

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237819214-2c50e581-e385-4bb0-978c-46ce51f0b10c.png)

<font style="color:#000000;">truffle --version  # 验证安装</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237819397-841abb51-0631-4dd1-b7a8-838905a7c8ea.png)

<h4 id="zs03R">1 **<font style="color:#000000;">安装 Ganache 本地测试链</font>**</h4>
<h4 id="u6hRf"><font style="color:#FF5D4D;">sudo npm</font><font style="color:#000000;"> </font><font style="color:#FF5D4D;">install</font><font style="color:#000000;"> </font><font style="color:#4DA621;">-g</font><font style="color:#000000;"> ganache-cli --verbose</font></h4>
![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237819587-2760c5eb-8b53-499e-aa8d-5e1bd8801950.png)

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237819862-4b971bd4-c9d2-4085-91aa-5d5669c49193.png)

<font style="color:#000000;">ganache-cli </font><font style="color:#4DA621;">--version</font><font style="color:#000000;">  # 验证安装</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237820046-44526209-9bdc-4ac8-b0fd-8b7028a8d02c.png)



<font style="color:#000000;">4. 进入Fisco BCOS控制台目录</font>

<font style="color:#FF5D4D;">cd</font><font style="color:#000000;"> ~/Desktop/fisco</font>

<h3 id="SIpaC"><font style="color:#000000;">5. </font>编写node_list 搭链</h3>
<font style="color:#4DA621;">127.0.0.1:4</font><font style="color:#000000;"> </font><font style="color:#4DA621;">-p</font><font style="color:#000000;"> </font><font style="color:#E54595;">30300,20200</font><font style="color:#000000;">,8545</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237820229-2ab8d83f-1d91-459a-910d-8763ebf909ab.png)

<font style="color:#FF5D4D;">cd</font><font style="color:#000000;"> MyTruffle/127.0.0.1/</font>

<font style="color:#FF5D4D;">bash</font><font style="color:#000000;"> start_all.sh</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237820468-0b91d94b-4ff9-4c4f-a609-217326cb62e7.png)



<h4 id="Dnr6I"><font style="color:#000000;">6.</font>把console/conf目录下的所有文件和目录复制到sdk目录下</h4>
![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237820640-117da93c-446a-44d7-a7ea-7d58d30f46e0.png)

<h3 id="Nju0G">2 **<font style="color:#000000;">创建 Truffle 项目</font>**</h3>
<h4 id="SouZH">**<font style="color:#000000;">1. 初始化项目</font>**</h4>
<font style="color:#FF5D4D;">mkdir</font><font style="color:#000000;"> myblockchain</font>

<font style="color:#FF5D4D;">cd</font><font style="color:#000000;"> myblockchain</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237820887-9012a3d8-8eaf-4121-b5f0-d78c47af9880.png)

<font style="color:#000000;">truffle init</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237821039-384e6fa1-2128-45df-ad19-926ea03c4c8b.png)

**<font style="color:#000000;">2.项目结构（关键路径）</font>**：

<font style="color:#000000;">myblockchain/               # 项目根目录</font>

<font style="color:#000000;">├── contracts/              # 智能合约目录</font>

<font style="color:#000000;">├── migrations/             # 部署脚本目录</font>

<font style="color:#000000;">├── test/                   # 测试脚本目录</font>

<font style="color:#000000;">└── truffle-config.js       # 配置文件</font>

<h3 id="uF6Sq">**<font style="color:#000000;">三、编写智能合约（Truffle 方式）</font>**</h3>
<font style="color:#000000;">1.进入contracts目录</font>

<font style="color:#FF5D4D;">cd</font><font style="color:#000000;"> myblockchain/contracts/</font>



<font style="color:#000000;">2.进入~/Desktop/myblockchain/contracts，创建HelloFisco.sol合约文件</font>

<font style="color:#000000;">gedit HelloFisco.sol</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237821239-58219cd0-7c69-4fd7-9b23-89cad6131002.png)<font style="color:#000000;">   </font>



pragma solidity ^0.4.25;  // Fisco BCOS 2.x默认支持0.4.25



contract HelloFisco {

    string public message;



    constructor() public {

        message = "Hello Fisco BCOS!";

    }



    function setMessage(string _message) public {

        message = _message;

    }

    function getMessage() public view returns (string) {

        return message;

    }

}

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237821396-dd0e7963-b8c4-44ae-9d94-ba355ae4f07d.png)

<h3 id="EH7N5">**<font style="color:#000000;">四、修改 Truffle 配置</font>**</h3>
<font style="color:#000000;">1. 返回项目根目录</font>

<font style="color:#FF5D4D;">cd</font><font style="color:#000000;"> ..  # 路径：myblockchain/</font>

<font style="color:#000000;">2. 编辑truffle-config.js</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237822109-5b1fec9d-af38-4df6-8f5d-b0ebcf416557.png)

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237822520-e2ddf68f-209e-40e7-a3f2-8fc934ba400c.png)

<font style="color:#000000;">gedit truffle-config.js  # 添加Fisco BCOS网络配置</font>

<font style="color:#000000;">module.exports = {</font>

<font style="color:#000000;">  networks: {</font>

<font style="color:#000000;">    fisco: {</font>

<font style="color:#000000;">      // 这里使用Fisco BCOS控制台的JSON-RPC接口</font>

<font style="color:#000000;">     </font><font style="color:#000000;"> </font><font style="color:#C99100;">host</font><font style="color:#000000;">: </font><font style="color:#4DA621;">"127.0.0.1"</font><font style="color:#000000;">, // FISCO BCOS节点的IP地址（本地节点）</font>

<font style="color:#000000;">    </font><font style="color:#C99100;">port</font><font style="color:#000000;">: </font><font style="color:#E54595;">8545</font><font style="color:#000000;">,        // 节点RPC服务端口（默认 8545，与以太坊一致）</font>

<font style="color:#000000;">    </font><font style="color:#C99100;">network_id</font><font style="color:#000000;">: </font><font style="color:#4DA621;">"*"</font><font style="color:#000000;">,  // 匹配任何网络ID（FISCO BCOS 通常不强制校验 network_id）</font>

<font style="color:#000000;">    </font><font style="color:#C99100;">gas</font><font style="color:#000000;">: </font><font style="color:#E54595;">3000000</font><font style="color:#000000;">,     // 每笔交易的最大gas消耗量（根据合约复杂度调整）</font>

<font style="color:#000000;">    </font><font style="color:#C99100;">gasPrice</font><font style="color:#000000;">: </font><font style="color:#E54595;">0</font><font style="color:#000000;">,      // FISCO BCOS是联盟链，通常不收取交易费用，因此 gasPrice为0</font>

<font style="color:#000000;">    }</font>

<font style="color:#000000;">  },</font>

<font style="color:#000000;">};</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237822678-c967b0b1-285a-4429-b8e0-0615fcade9a7.png)

<h3 id="mFfUs">3 **<font style="color:#000000;">编译智能合约（项目路径）</font>**</h3>
<h3 id="Y9MVV"><font style="color:#000000;">1.在项目根目录执行编译命令</font></h3>
<font style="color:#000000;">truffle compile  # 路径：myblockchain/</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237822880-abbe4c94-2fea-47a0-8ee8-5d3f10d69792.png)

**2.查看****<font style="color:#000000;">编译结果路径</font>**

<font style="color:#000000;">myblockchain/build/contracts/HelloFisco.json  # 编译生成的ABI和字节码</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237823199-38eeba1a-6fcf-4387-8449-31df60fae63b.png)

**<font style="color:#000000;">七、复制编译结果到 Fisco BCOS 控制台</font>**

<font style="color:#1F2329;">1. 从Truffle项目复制编译结果到Fisco BCOS控制台 </font>

<font style="color:#1F2329;">cp ~/Desktop/myblockchain/contracts/HelloFisco.sol ~/Desktop/fisco/console/contracts/solidity/</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237823374-fb22e7e8-c553-44ac-aefc-3396be70d3c5.png)



<font style="color:#1F2329;">cp ~/Desktop/myblockchain/build/contracts/HelloFisco.json ~/Desktop/fisco/console/contracts/solidity/</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237823550-1e89d2c9-f0eb-4c33-aa76-6e50b57b9486.png)

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237823711-3831012a-75b9-4cfd-b6e9-bed20ee1403b.png)

<font style="color:#1F2329;">2.进入控制台目录</font>

<font style="color:#1F2329;">cd ~/Desktop/fisco/console</font>

<font style="color:#000000;">3.启动Fisco BCOS控制台</font>

<font style="color:#FF5D4D;">bash</font><font style="color:#000000;"> start.sh</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237823875-80f4bc26-5521-407d-95ea-dfc97dd635d4.png)

<h3 id="AIfpO">4 **<font style="color:#000000;">在 Fisco BCOS 控制台部署合约</font>**</h3>
<font style="color:#000000;">1.部署合约</font>

<font style="color:#000000;">deploy HelloFisco</font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237824115-7313b265-5615-4f0c-96c3-6a4cd8bc1ce9.png)

<font style="color:#000000;">2.调用合约</font>

<font style="color:#000000;">call HelloFisco [合约地址] getMessage </font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237824274-da42a4b5-ccd4-4af6-a4ac-a7fa71d3497e.png)

<font style="color:#000000;">3.发送交易</font>

<font style="color:#000000;">call HelloFisco [合约地址] setMessage </font><font style="color:#4DA621;">"Hello Fisco"</font><font style="color:#000000;"> </font>

![](https://cdn.nlark.com/yuque/0/2025/png/53946171/1750237824500-9397bbf3-5222-4985-a9b5-5e1baa240159.png)



