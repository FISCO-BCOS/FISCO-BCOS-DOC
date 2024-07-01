# FISCO BCOS "Python Blockchain Box"

Author: Tang Huifeng | FISCO BCOS Open Source Community Contributor

The author is a Python developer who encapsulates the FISCO BCOS Python SDK as an image to improve the speed of configuring the environment and improve ease of use。

## Foreword

As a Python developer, I've always wanted to learn about blockchain through Python.。By coincidence, at an open source annual meeting in 2019, I contacted and joined the FISCO BCOS open source community, and since then, I have been spending my spare time pondering the Python of FISCO BCOS.-SDK。

When configuring the environment, I spent some time, so I also came up with the idea of encapsulating the entire framework into a docker image, I named it "Python blockchain box," just like Minecraft's "workbench," which can improve the speed of configuring the environment and improve ease of use.。With that in mind, I started using my spare time to write Dockerfiles.。

![](../../../../images/articles/python_blockchain_box/IMG_5746.PNG)


I will build a good docker image to share with the students around the experience, "ready to use" feature response is very good, we will not be afraid because of the environment configuration is difficult, like Steve in Minecraft, put down the "workbench" can create a bunch of useful tools out.。

## What is a "Python Blockchain Box"?？

Before answering this question, let's take a look at Python.-SDK。This is open source by FISCO BCOS and helps developers develop components for blockchain applications using the Python language.。Since it is developed through the Python language, I believe it will have a continuous vitality。

![](../../../../images/articles/python_blockchain_box/IMG_5747.PNG)


Python's reading difficulty is relatively low, especially for students and beginners through Python.-SDK to understand and learn blockchain。You can try to install the building according to the following environmental requirements。

- Python environment: Python 3.6.3, 3.7.x
- FISCO BCOS node: Please refer to [FISCO BCOS Installation](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/installation.html)

About Python-SDK, click to refer to the development tutorial launched by the FISCO BCOS team。

- [The sparrow is small and has all five internal organs| From Python-SDK Talk about FISCO BCOS Multilingual SDK](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485291&idx=1&sn=c359380a89621d1a64856183568825ee&chksm=9f2ef577a8597c61e5dd5e458d489926138a42808a06517f4d6515d4666dc11a08646ccebea2&scene=21#wechat_redirect)
- [《Python-SDK's Past Lives](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485256&idx=1&sn=f1e70be6c53ea7e690392ce1ac8b5f5e&chksm=9f2ef554a8597c4278c630f60923a683b9e47499319e31aab41ccfd98715a9db2300e7d782e3&scene=21#wechat_redirect)

"Python Blockchain Box" is equivalent to Python that will be configured-The SDK and the deployed blockchain are packaged into a package, similar to organizing a large house into an RV。In this way, users do not need to pay attention to environment configuration issues, which can reduce deployment time - images can be obtained in less than a minute, which is convenient for developers to quickly get started and facilitate automated operation and maintenance.。You can also try to combine this service with JenKins to further optimize the operation and maintenance process.。The "Python blockchain box" can be used as a workbench for Python blockchain development, a ready-to-use toolbox, and can ensure a clean development environment, and most importantly, it is very light, just like the workbench in Minecraft.。

As long as you have a computer, you can open this toolbox anytime, anywhere。The process of installing the toolbox has also become enjoyable: you can get started with direct research and development with just one line of code, without paying too much attention to the complexity of the environment configuration, saving a lot of time and freeing your hands.。

## Get and Run the "Python Blockchain Box"

This "box" can be obtained through the following deployment。

```
docker run -it -p 20200:20200 --name python_sdk fiscoorg/playground:python_sdk
```

After entering the container, you first need to start the node. After starting the node, you can "eat" it。In this process, please be careful not to use sh。

```
bash /root/fisco/nodes/127.0.0.1/start_all.sh
```

Then, debug in / python _ sdk. Tips during debugging: console.py adds tab autocomplete。

```

# View SDK usage
./console.py usage

# Get Node Version
./console.py getNodeVersion
```

```
bash-5.0# ./console.py getNodeVersion

INFO >> user input : ['getNodeVersion']
INFO >> getNodeVersion >> { "Build Time": "20190923 13:22:09", "Build Type": "Linux/clang/Release", "Chain Id": "1", "FISCO-BCOS Version": "2.1.0", "Git Branch": "HEAD", "Git Commit Hash": "cb68124d4fbf3df563a57dfff5f0c6eedc1419cc", "Supported Version": "2.1.0" }
```

After completing these, it was successful, back and forth, equivalent to building an open source blockchain framework in a few seconds.。You can put your contract in / python-For more information about sdk / contracts, see [Python-How to use SDK](https://github.com/FISCO-BCOS/python-sdk)。This ready-to-use "blockchain box" is very helpful for developers who want to use Python to develop blockchain applications or learn blockchain.。Developers can do this by calling / python.-Functions in the sdk / client use the. / console.py command line and interact with the blockchain running in the box。

The following will be in Python-Flask development as an example to implement the function of calling the HelloWorld contract。

- Step1 into container

```
docker run -it -p 20200:20200 -p 80:80 --name flask_web fiscoorg/playground:python_sdk
```

- step2 Start Node

```
bash /root/fisco/nodes/127.0.0.1/start_all.sh
```

- step3 Deploying HelloWorld Contracts

/python-HelloWorld.sol is stored in the sdk / contract. You can directly use this contract for testing.。First, check the contents of the HelloWorld.sol contract。

```
pragma solidity ^0.4.24;

contract HelloWorld{
   string name;

   constructor() public{
      name = "Hello, World!";
  }

   function get() constant public returns(string){
       return name;
  }

   function set(string n) public{
       name = n;
  }
}
```

You can return the value of the string name through the get interface and update the value of the name through the set。Developers with a foundation in Solidity programming can also customize their own contract content。You can then deploy the HelloWorld contract。

```
$ ./console.py deploy HelloWorld save

INFO >> user input : ['deploy', 'HelloWorld', 'save']

backup [contracts/HelloWorld.abi] to [contracts/HelloWorld.abi.20190807102912]
backup [contracts/HelloWorld.bin] to [contracts/HelloWorld.bin.20190807102912]
INFO >> compile with solc compiler
deploy result  for [HelloWorld] is:
{
   "blockHash": "0x3912605dde5f7358fee40a85a8b97ba6493848eae7766a8c317beecafb2e279d",
   "blockNumber": "0x1",
   "contractAddress": "0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce",
   "from": "0x95198b93705e394a916579e048c8a32ddfb900f7",
   "gasUsed": "0x44ab3",
   "input": "0x6080604052... several lines omitted... c6f2c20576f726c6421000000000000000000000000000000",
   "logs": [],
   "logsBloom": "0x000... several lines omitted... 0000",
   "output": "0x",
   "status": "0x0",
   "to": "0x0000000000000000000000000000000000000000",
   "transactionHash": "0xb291e9ca38b53c897340256b851764fa68a86f2a53cb14b2ecdcc332e850bb91",
   "transactionIndex": "0x0"
}
on block : 1,address: 0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce
address save to file: bin/contract.ini
```

After completion, you can get the address of the HelloWord contract deployment, and call the function interface through this address.。

- step4 in / python-Edit app.py under the sdk folder

```
$ vi app.py

# -*- coding:utf-8 -*-
from client.common import transaction_common
from flask import Flask, request 
from jinja2 import escape

app = Flask(__name__)

tx_client = transaction_common.TransactionCommon("0x2d1c577e41809453c50e7e5c3f57d06f3cdd90ce","contracts","HelloWorld")
# tx_client = transaction_common.Transaction_Common("Address","Contract Path","Contract Name")

@app.route('/')
def index():
   '''
  Call the get interface to obtain the string of HelloWorld
  '''
   new_str = request.args.get('new_str')
   # Update the string of HelloWorld by the value of new _ str
   if new_str:
       tx_client.send_transaction_getReceipt("set",(new_str,))
       # Update string if new _ str is not empty
   return escape(str(tx_client.call_and_decode("get")))

if __name__ == '__main__':            
   app.run(host="0.0.0.0", port=80)
```

- step5 Install app.py Dependency / Runner

```
pip install flaskpython app.py
```

This is done through Python.-The Flask framework implements calling the HelloWorld contract, calling the get interface to view the string, and calling the set interface to update the string.。Python for FISCO BCOS-SDK is very suitable for students like me or beginners to study and understand blockchain technology。I'm looking forward to more developers to participate in it and use it to build more interesting and fun open source projects.。Dockerfile address please refer to the end of the article, recently I will also make some updates to it to improve its ease of operation, the latest operation manual and news will be released on GitHub, welcome everyone to pay attention。Click for reference [More Python Demo](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/sdk/python_sdk/demo.html)。

## 3. Submit pr experience

On how to submit pr in FISCO BCOS clickable reference open source community collated content, here is not much to explain。For details, please refer to [Uncovering FISCO BCOS Open Source Project Development Collaboration](http://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485380&idx=1&sn=1f32ddad49b542206d24739f3de98b95&chksm=9f2ef5d8a8597cce973c9321543174de0e9a0bfebf1750cef4f6ae1641f4d189ea22d616cf98&scene=21#wechat_redirect)

I would like to share the experience of an individual submitting a pr, the whole process is both novel and interesting。After initiating the idea of "blockchain box," I quickly wrote the first Dockerfile, and then submitted the pr。Soon the community's little brother Shi Xiang replied to me, and at first I thought it was a foreign friend, so I kept using it." Poor English "communicate with him。He is very welcome to my PR, but also continue to give me praise and support, he not only solved many problems in the configuration process, but also from time to time to share some small stories to encourage me.。In the process of merging my pr, I am very grateful to the team for their warm help, timely review comments to me, and carefully introduce me to the content functions that need to be added, which makes me fully feel the atmosphere of harmonious coexistence and mutual help in the FISCO BCOS community.。

At present, the "Python blockchain box" still has some areas to be optimized. For example, every time a container is started, manual operations are required.--> The machine exposes the required ports, the default is 20200,8045,30300。Later, you may consider optimizing the functionality of the default startup node, adding data volumes, and optimizing the container size so that fiscoorg / playground:python _ sdk more refined。If you have good optimization ideas, welcome to submit pr, to a fun novel pr experience

------

#### Reference Links

- [Dockerfile](https://github.com/FISCO-BCOS/python-sdk/blob/master/Dockerfile)

