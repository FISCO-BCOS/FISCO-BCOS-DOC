# C# SDK Introduction

Tags: "FISCOBCOS CSharp SDK" "'C# sdk``

----

## Software Architecture

Software Architecture Description

FISCOBCOS C# Sdk uses net core 3.1, and the supporting development tools are vs Code and Visual Studio 2019。

## Function Introduction

1. Implement RPC synchronous / asynchronous requests.
2. Realize the generation of public and private keys and accounts of FISCO BCOS, expand the generation of Webase Front, import the user json, and directly import the Webase middleware。
3. Implement contract operation encapsulation, such as contract deployment, request parameter construction, transaction signature, RLP encoding conversion, etc.。
4. Realize contract deployment, contract trading, contract Call operation, contract transaction receipt acquisition, etc.。
5. Realize the analysis of contract input, output, event, etc.。
6. Unit test Demo for all operation configurations.。Can reference copy。
7. Realize state secret support, create state secret accounts, deploy contracts under state secrets, trade, etc.。

Note: Sending a transaction and returning a transaction receipt test synchronously has a certain chance of being empty because the underlying transaction is being packaged and consensus has not yet been completed.。At present, the latest code adds polling acquisition to optimize the transaction receipt method and improve the user experience.。

## Installation Tutorial

Note: You can also use webase-front blockchain middleware export contract to get abi and bin files。

1. Download the source code, vs2019 nuget package restore; Or use the nuget package to install, the installation command is as follows: Install-Package FISCOBCOS.CSharpSdk -Version 1.0.0.6
2. install the solidity plug-in in vs code and create a folder in vs code to store the original sol contract。
3. vs code executes the compilation command "compile current Solidity contract" according to F5, and abi and bin corresponding to the contract will be generated。
4. Put the abi and bin compiled above into your project and do the related operations.。

Reference:
![vs Code Compilation Contract Description](https://github.com/FISCO-BCOS/csharp-sdk/blob/master/Img/how-to-use-console-generator1.gif)

## Instructions for use

1. Configure the BaseConfig file in the FISCOBCOS. CSharpSdk class library and configure the corresponding underlying request DefaultUrl, such as: < http://127.0.0.1:8545> 。
2. Use ContractService and QueryApiService for related business operations.。
3. ContractService is mainly a package of operations such as contract calls, see the ContractTest.cs in the corresponding unit test in detail.。
4. QueryApiService is the underlying non-transactional Json RPC API encapsulation. For more information, see Unit test ApiServiceTest.cs.。
5. For more information, see RedisThreadWorkTest in the ConsoleTest project to enable multiple RedisSubClient projects for subscription.。
(This function can expand the specified contract, specified event, etc. according to the actual situation to obtain the resolution operation)。

Note: The general JSON RPC API is relatively simple and does not encapsulate the corresponding DTO entity. During operation, you can use online JSON to generate entities for business integration.。

## **State Secret Usage Instructions**

1. Configure IsSMCrypt = true in the BaseConfig.cs file;Adopt state secret signature and supporting communication。

2. Configure DefaultPrivateKeyPemPath as the default user private key pem file in the BaseConfig.cs file [Optional]。

3. Generate information such as national secret user accounts, which can be imported into webase-front, view unit tests。

               /// <summary>
               / / / Generate a pair of public and private keys. The generated json can be copied to a txt file and directly imported into components such as webase front.
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

4. Perform contract deployment, trading, etc., and view unit test GMContractTest.cs。

   ​

               /// <summary>
               / / / Call the contract method asynchronously. This test calls the contract set method, which can parse input and event
               / / / If the transaction hash is empty, the production environment uses a scheduled service or queue to obtain the transaction hash before obtaining the corresponding data.
               /// </summary>
               /// <returns></returns>
               [Fact]
               public async Task SendTranscationWithReceiptDecodeAsyncTest()
               {
              var contractService = new ContractService(BaseConfig.DefaultUrl, BaseConfig.DefaultRpcId, BaseConfig.DefaultChainId, BaseConfig.DefaultGroupId, privateKey);
               string contractAddress = "0x26cf8fcb783bbcc7b320a46b0d1dfff5fbb27feb";/ / Test the deployment contract above to get the contract address.
               var inputsParameters = new[] { BuildParams.CreateParam("string", "n") };
               var paramsValue = new object[] { "123" };
               string functionName = "set";/ / Call contract method
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

   5. Check the relevant source code for details about the generation, signature, encryption, sending transaction, etc. of the national secret account, as well as the video of the supporting station B.。

## Added features

1. Add mnemonic and wallet modules
2. Add BIP 32, EIP 55 and other features
3. Optimize the acquisition of transaction receipts
4. Add the thread pool to pull the specified block transaction, add the Redis subscription publication
4. State Secret Support
