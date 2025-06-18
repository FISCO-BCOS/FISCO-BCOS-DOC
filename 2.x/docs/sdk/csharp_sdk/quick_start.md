# C# SDK 介绍

标签：``FISCOBCOS CSharp SDK`` ``C# sdk``

----

## 软件架构

软件架构说明

FISCOBCOS C# Sdk 采用 net core 3.1,配套开发工具是vs Code 和 Visual Studio 2019。

## 功能介绍

1. 实现 RPC 同步/异步请求
2. 实现FISCO BCOS公私钥、账户生成，拓展生成Webase Front导入用户json，可以直接导入Webase中间件。
3. 实现合约操作封装，如：合约部署、请求参数构建、交易签名、RLP编码转换等。
4. 实现合约部署、合约交易、合约Call操作、合约交易回执获取等。
5. 实现合约input、output、event等解析。
6. 所有操作配置对应的单元测试Demo。可以参考复制。
7. 实现国密支持，创建国密账户、国密下合约部署、交易等。

备注：发送交易并同步返回交易回执测试，有一定几率为空，那是因为底层交易在打包，还没完成共识。目前最新代码新增轮询获取，优化交易回执方法，提高用户体验。

## 安装教程

备注：也可以使用webase-front 区块链中间件导出合约获得abi和bin文件。

1. 下载源码，vs2019 nuget包还原; 或使用 nuget包安装，安装命令如下： Install-Package FISCOBCOS.CSharpSdk -Version 1.0.0.6
2. vs code 安装solidity 插件，在vs code创建一个文件夹存放原始sol合约。
3. vs code 按 F5 执行编译命令 “compile current Solidity contract”,会生成合约对应的abi和bin。
4. 将上面编译得到abi和bin 放到你的项目中，进行相关操作。

参考：
![vs Code 编译合约说明](https://github.com/FISCO-BCOS/csharp-sdk/blob/master/Img/how-to-use-console-generator1.gif)

## 使用说明

1. 在 FISCOBCOS.CSharpSdk 类库配置 BaseConfig 文件，配置好对应的底层请求DefaultUrl，如：<http://127.0.0.1:8545> 。
2. 使用ContractService 和QueryApiService进行相关业务操作。
3. ContractService 主要是合约调用等操作封装，详细看对应的单元测试中的ContractTest.cs。
4. QueryApiService是底层非交易的Json RPC API 封装，可参考单元测试ApiServiceTest.cs。
5. 如果使用redis 发布订阅，请参考ConsoleTest 项目中的RedisThreadWorkTest，开启多个RedisSubClient 项目进行订阅。
（该功能可以根据实际情况拓展指定的合约、指定事件等获取解析操作）。

备注：通用的Json RPC API 相对简单，没有封装对应的DTO 实体，操作时候可以通过在线json 生成实体进行业务结合。

## **国密使用说明**

1. 在BaseConfig.cs 文件中配置IsSMCrypt = true;采用国密签名及配套通信。

2. 在BaseConfig.cs 文件中配置DefaultPrivateKeyPemPath 为默认用户私钥pem 文件【可选】。

3. 生成国密用户账户等信息，可导入webase-front，查看单元测试。

               /// <summary>
               /// 国密生成一对公私钥，生成的json可以copy 到txt文件，直接导入webase front 等组件中
               /// </summary>
               [Fact]
               public void GMGeneratorAccountJsonTest() 
               {
                   var account = AccountUtils.GMGeneratorAccount("adminUser" + new Random().Next(100000, 1000000).ToString());
                   var accountString = account.ToJson();
                   // Debug.WriteLine(accountString);
                   _testOutput.WriteLine(accountString);
                   Assert.True(accountString.ToObject<AccountDto>().PublicKey.Length > 0);
               }

4. 进行合约部署、交易等，查看单元测试GMContractTest.cs。

   ​

               /// <summary>
               /// 异步调用合约方法,本测试调用合约set方法，可以解析input和event
               /// 遇到交易hash为空，生产环境采用定时服务/队列形式，先获取交易哈希，之后再去获取对应的数据
               /// </summary>
               /// <returns></returns>
               [Fact]
               public async Task SendTranscationWithReceiptDecodeAsyncTest()
               {
              var contractService = new ContractService(BaseConfig.DefaultUrl, BaseConfig.DefaultRpcId, BaseConfig.DefaultChainId, BaseConfig.DefaultGroupId, privateKey);
               string contractAddress = "0x26cf8fcb783bbcc7b320a46b0d1dfff5fbb27feb";//上面测试部署合约得到合约地址
               var inputsParameters = new[] { BuildParams.CreateParam("string", "n") };
               var paramsValue = new object[] { "123" };
               string functionName = "set";//调用合约方法
               ReceiptResultDto receiptResultDto = await contractService.SendTranscationWithReceiptAsync(abi, contractAddress, functionName, inputsParameters, paramsValue);
       
               Assert.NotEmpty(receiptResultDto.Output);
               Assert.NotEmpty(receiptResultDto.Input);
               Assert.NotEmpty(receiptResultDto.Logs);
               var solidityAbi = new SolidityABI(abi);
               var inputList = solidityAbi.InputDecode(functionName, receiptResultDto.Input);
               Assert.True(inputList[0].Parameter.Name == "n" && inputList[0].Result.ToString() == "123");
       
               string eventName = "SetEvent";
               var eventList = solidityAbi.EventDecode(eventName, receiptResultDto.Logs);
               var eventpramas1 = eventList[0].Event.Find(x => x.Parameter.Name == "paramsStr");
               var eventpramas2 = eventList[0].Event.Find(x => x.Parameter.Name == "operationTimeStamp");
               Assert.True(eventpramas1.Result.ToString() == "123");
               Assert.NotNull(eventpramas2.Result);
           }

   5. 关于国密账户生成、签名、加密、发送交易等细节查看相关源码，以及配套B站视频。

## 新增特性

1. 新增助记词、钱包模块
2. 新增BIP 32、新增EIP 55 等特性
3. 优化获取交易回执
4. 新增线程池拉取指定区块交易，新增Redis订阅发布
4. 国密支持
