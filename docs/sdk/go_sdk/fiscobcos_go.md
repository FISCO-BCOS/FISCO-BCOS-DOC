# FISCOBCOS中的GoSDK操作---(搭建项目版本)



@[toc]
​																							**文档操作记录**

| 内容     | 作者         | 时间       | 版本号 | 联系方式                |
| -------- |------------| ---------- | ------ | ----------------------- |
| 文档创建 | 陈雅凯，高俭豪，谭俊 | 2024-11-11 | 1.0    | email:2040575063@qq.com |
| 文档更新 | 陈雅凯，高俭豪，谭俊 | 2024-11-13 | 2.0    | email:2040575063@qq.com |
| 文档更新 | 陈雅凯，高俭豪，谭俊 | 2024-11-16 | 3.0    | email:2040575063@qq.com |

**背景：**
基于FISCOBCOS（2.9.1）提供的GoSDK进行二次开发，再次封装使用，简化官方文档中的操作。简化部分为：避免每次合约更新后，通过abigen工具重复使用新的abi和bin，生成Go语言代码，再重新集成到项目中。
**相关资源：**
[https://gitee.com/kkvb/fisco-go-sdk-demo](https://gitee.com/kkvb/fisco-go-sdk-demo)			
[https://github.com/kkvbAugust/fisco-go-sdk-demo](https://github.com/kkvbAugust/fisco-go-sdk-demo)
**参考文档：**[https://fisco-bcos-documentation.readthedocs.io/zh-cn/latest/docs/sdk/go_sdk/index.html](https://fisco-bcos-documentation.readthedocs.io/zh-cn/latest/docs/sdk/go_sdk/index.html)
**使用FiscoBcos官方工具库：** [https://github.com/FISCO-BCOS/go-sdk/tree/master-FISCO-BCOS-v2](https://github.com/FISCO-BCOS/go-sdk/tree/master-FISCO-BCOS-v2)
**GoSDK版本为：1.22.8**

**注意事项：**
该项目已经封装成一个工具包，项目地址为：[https://github.com/kkvbAugust/fiscobcos-go](https://github.com/kkvbAugust/fiscobcos-go)

在GoLang使用时,直接：

```
go get -u github.com/kkvbAugust/fiscobcos-go
```
其中的目录结构和函数名称略有不同，详细可看：[简化FISCOBCOS中的GoSDK合约调用-自定义GoSDK封装](https://blog.csdn.net/bjll123/article/details/144072061?fromshare=blogdetail&sharetype=blogdetail&sharerId=144072061&sharerefer=PC&sharesource=bjll123&sharefrom=from_link)

## 一，GoWEB项目创建：

1.新建项目：

![image-20241111211334011](https://img-blog.csdnimg.cn/img_convert/ab0774ce6028b44c3e4a86bdbd0b99ab.png)



2.项目路径（随自己心意）：

![image-20241113215548137](https://img-blog.csdnimg.cn/img_convert/2d1329579f98fe32d3c143e0eeae2acb.png)



3.生成成功：

![image-20241113215722560](https://img-blog.csdnimg.cn/img_convert/96374d0a9c219455deb95ff95302e0ba.png)



## 二，项目书写：

### 1.创建目录

![image-20241113220903432](https://img-blog.csdnimg.cn/img_convert/b51c03108bd301507885add8eb426407.png)



### 2.合约配置文件的读取

书写配置文件settings.yaml(书写时，输入自己合约对应的name(合约名称),address,abi,bin。bin可以填写，也可以不用填写) ：

```yaml
contracts:
 contract1: (这里的设计只是一个键对应一个结构体数据)
  name:
  address:
  abi:
  bin:
 contract2: (这里的设计只是一个键对应一个结构体数据)
  name:
  address:
  abi:
  bin:
```

![image-20241113221418154](https://img-blog.csdnimg.cn/img_convert/308b3d10a47f7ccf48a8c982fa87ecfa.png)

​

**注意事项：**

​			在WeBASE-Front上编译合约获得abi和bin,部署合约获得address。



```solidity
pragma solidity >=0.4.24 <0.6.11;

contract HelloWorld {
    string name;

    constructor() public {
        name = "Hello, World!";
    }

    function get() public view returns (string memory) {
        return name;
    }

    function set(string memory n) public view returns(bool) {
        name = n;
        return true;
    }
}
```

![image-20241111233515558](https://img-blog.csdnimg.cn/img_convert/fc0bd413d19608ea8000e070c172a5e2.png)



```solidity
//SPDX-License-Identifier: MIT
pragma solidity ^0.6.10;
pragma experimental ABIEncoderV2;

contract Anonoucement {


    struct Anouncement {
        uint256 id;//宣言ID
        string userID;//用户编号
        string userPK;//用户的公钥
        string nounce;//签名的随机值
        string message;//消息
        string cipher;//消息的密文   --消息
        string attachment;//附件的摘要   --哈希
        uint256 signtime;//存证时间
    }

    mapping(uint256 => Anouncement) public announcements;
    uint256 nextId = 1;
    // uint256 nextline;
    // 定义查询条件结构体
    struct QueryConditions {
        // bool byId;
        // uint256 id;
        bool byUserID;
        string userID;
        bool byTimeRange;
        uint256 startTime;
        uint256 endTime;
        // 可以根据需要继续添加其他查询条件字段，比如按消息内容、公钥等查询
    }

    // 添加公告的函数
    // function addAnouncement(uint256 id, Anouncement memory anouncement) public returns (bool) {
    //     if (announcements[id].id == 0) {
    //         announcements[id] = anouncement;
    //         return true;
    //     }
    //     return false;
    // }



    function addAnouncement(string memory userID,string memory userPK,string memory nounce,
    string memory message,string memory cipher,string memory attachment,uint256 signtime) public returns (bool) {
        Anouncement memory anouncement = Anouncement(nextId,userID,userPK,nounce,message,cipher,attachment,signtime);
        announcements[nextId] = anouncement;
        nextId++;
        return true;
    }

    // 根据公告ID获取公告信息的函数
    function getAnouncement(uint256 id) public view returns (Anouncement memory) {
        return announcements[id];
    }
       

    // 根据查询条件、每页显示数量和起始位置返回公告列表的函数
    function listAnouncement(bool byUserID,string memory userID,bool byTimeRange,uint256 startTime,uint256 endTime,uint256 pageSize, uint256 startIndex) public view returns (Anouncement[] memory) {
        uint256[] memory ids = new uint256[](120);
        // uint256[] ids;

        uint256 foundCount = 0;
        uint256 totalMatched = 0;
        uint256 nextline = 0;
        
        // 遍历所有公告
        for (uint256 i = 1; i < nextId; i++) {
            // Anouncement storage ann = announcements[i];
            Anouncement memory ann = announcements[i];

            bool isMatch  = true;
            

            // 检查是否满足查询条件
            // if (byId && ann.id!= id) {
            //     isMatch  = false;
            // }

            if (byUserID && keccak256(abi.encodePacked(ann.userID))!= keccak256(abi.encodePacked(userID))) {
                isMatch  = false;
            }

            if (byTimeRange && (ann.signtime < startTime || ann.signtime > endTime)) {
                isMatch  = false;
            }

            // 如果满足查询条件
            if (isMatch) {
                totalMatched++;

                // 判断是否在当前页范围内
                if (totalMatched > startIndex && foundCount < pageSize) {
                    // ids.push(i);
                    ids[nextline] = i;
                    nextline ++;
                    foundCount++;
                }
            }
        }

        // 创建一个数组来存储最终要返回的公告列表
        Anouncement[] memory result = new Anouncement[] (foundCount);

        // 将满足条件的公告添加到结果数组中
        for (uint256 j = 0; j < foundCount; j++) {
            result[j] = announcements[ids[j]];
        }

        return result;
    }
}
```

![image-20241113221832674](https://img-blog.csdnimg.cn/img_convert/a99fae20ca6489db91f58eb221ca5280.png)

​

（1）这里的abi使用单引号将其包裹起来，填写到settings.yaml文件中。



​（2）也可以不用替换，直接将abi赋值给global.Config.Contract["contract1"].Abi。
(这里的"contract1"是一个map的键与settings配置文件中的保持一致) 换作其他的合约换成其他对应的 键 就好。


创建结构体：

```go
package config

type Contract struct {
	Name    string `yaml:"name"`
	Address string `yaml:"address"`
	Abi     string `yaml:"abi"`
	Bin     string `yaml:"bin"`
}
```

![image-20241113222325831](https://img-blog.csdnimg.cn/img_convert/1dc34e8d8a2e23a370d40ca4bbf58a1c.png)



```go
package config

type Config struct {
	Contract map[string]*Contract `yaml:"contracts"`
}
```

![image-20241116141328182](https://img-blog.csdnimg.cn/img_convert/c4100ce7fdf2efe4daf51c31d4e8b44b.png)



创建全局变量：

```go
package global

import "fisco-go-sdk/config"

var (
	Config *config.Config
)
```

![image-20241113222811906](https://img-blog.csdnimg.cn/img_convert/59ef032bb0cb0189e755221e2b0fc1ef.png)



拉取工具包：

![image-20241113222920011](https://img-blog.csdnimg.cn/img_convert/85f48cda4a79b09668f47b87c8eed437.png)



读取配置文件:

```go
package core

import (
	"fisco-go-sdk-demo/config"
	"fisco-go-sdk-demo/global"
	"fmt"
	"gopkg.in/yaml.v3"
	"io/ioutil"
	"log"
)

// InitConf 读取yaml文件配置
func InitConf() {
	const ConfigFile = "resources/settings.yaml"
	c := &config.Config{}
	yamlConf, err := ioutil.ReadFile(ConfigFile)
	if err != nil {
		panic(fmt.Errorf("get yamlConf error: %s", err))
	}
	err = yaml.Unmarshal(yamlConf, c)
	if err != nil {
		log.Fatalf("config Init Unmarshal: %v", err)
	}
	log.Println("config yamlFile load Init success")
	global.Config = c
}
```

![image-20241113223428460](https://img-blog.csdnimg.cn/img_convert/d9ca597a4dd238159994b060e9ec5034.png)



### 3.连接FISCOBCOS的网络配置文件读取

配置fiscobcos链的连接：

​	生成私钥：

![image-20241111221322139](https://img-blog.csdnimg.cn/img_convert/9c2a15c0e368364d1e3d79dde47cbfac.png)

![image-20241111221134898](https://img-blog.csdnimg.cn/img_convert/99ed6881b8a0e132b3032f82f3b45abd.png)

​	**或者从WeBASE-Front上，拿取，下载到项目目录的（fisco-go-sdk-demo/fiscobcos/accounts）中：**

​    **下载完之后，记得将私钥文件改个名字（与config.toml中的配置项保持一致）。**

​	![image-20241114202357687](https://img-blog.csdnimg.cn/img_convert/711783175d868c44e728d251f953fd55.png)

![image-20241114202442566](https://img-blog.csdnimg.cn/img_convert/fcdc4f5c1d1b97f0fdf14e27d493679a.png)

**注意：**

​	**这里导出后下载到自己的项目目录fisco-go-sdk-demo/fiscobcos/accounts）中。**

​	**记得改文件名称（与config.toml中的配置项保持一致）。**



​	拷贝节点证书文件：

![image-20241113223623764](https://img-blog.csdnimg.cn/img_convert/b571b76bd7c920514f9655c26232ccf1.png)

​	例如：![image-20241111221731453](https://img-blog.csdnimg.cn/img_convert/4de92683c087af986b4e4db45f687c33.png)



​	网络连接配置文件（有要修改的部分，认真看图）：

```toml
[Network]
#type rpc or channel
Type="channel"
# 三个节点证书，使用相对路径
CAFile="fiscobcos/sdk/ca.crt"
Cert="fiscobcos/sdk/sdk.crt"
Key="fiscobcos/sdk/sdk.key"
# if the certificate context is not empty, use it, otherwise read from the certificate file
# multi lines use triple quotes
CAContext=''''''
KeyContext=''''''
CertContext=''''''

[[Network.Connection]]
NodeURL="192.168.81.128:20200"  # 节点的地址
GroupID=1  # 群组id
# [[Network.Connection]]
# NodeURL="127.0.0.1:20200"
# GroupID=2

[Account]
# only support PEM format for now
KeyFile="fiscobcos/accounts/admin.pem"  #使用什么账户调用合约
#DynamicKey=true

[Chain]
ChainID=1 #链id
SMCrypto=false # 非国密

[log]
Path="./"
```

![image-20241113224522760](https://img-blog.csdnimg.cn/img_convert/993e08073943be6eaa1566dbe92a98f6.png)



拉取工具包：

![image-20241113224815041](https://img-blog.csdnimg.cn/img_convert/399d1433fbdbea26165705a8e5378f97.png)



创建结构体：

```go
package config

import (
	"github.com/FISCO-BCOS/go-sdk/abi/bind"
	"github.com/FISCO-BCOS/go-sdk/client"
)

type GoSdk struct {
	Client   *client.Client                 `json:"client"`
	Contract map[string]*bind.BoundContract `json:"contract"`
}

```

![image-20241115100003465](https://img-blog.csdnimg.cn/img_convert/909856c46004b1e121467bfc8d350b92.png)



加入全局变量：

```go
package global

import "fisco-go-sdk-demo/config"

var (
	Config *config.Config
	GoSdk  config.GoSdk
)
```

![image-20241115100112903](https://img-blog.csdnimg.cn/img_convert/02881251812a7eb308dd8fb09b6fa530.png)



初始化GoSDK对象：

```go
package core

import (
	"fisco-go-sdk-demo/global"
	"fmt"
	"github.com/FISCO-BCOS/go-sdk/abi"
	"github.com/FISCO-BCOS/go-sdk/abi/bind"
	"github.com/FISCO-BCOS/go-sdk/client"
	"github.com/FISCO-BCOS/go-sdk/conf"
	"github.com/ethereum/go-ethereum/common"
	"log"
	"strings"
)

/*
*
全局初始化client对象
*/
func InitClient() {
	configs, err := conf.ParseConfigFile("resources/config.toml")
	if err != nil {
		log.Fatal("resources.ParseConfigFile ERR==>", err)
	}
	config := &configs[0]
	clientObj, ok := client.Dial(config)
	if ok != nil {
		log.Fatal("client.Dial ERR===>", ok)
	}
	global.GoSdk.Client = clientObj
	//开辟空间
	global.GoSdk.Contract = make(map[string]*bind.BoundContract)
	fmt.Println("Client初始化完成")
}

/**
全局初始化sdk对象
*/

func InitSession(name string) {

	contract, mask := bindContract(global.Config.Contract[name].Abi, common.HexToAddress(global.Config.Contract[name].Address), global.GoSdk.Client, global.GoSdk.Client, global.GoSdk.Client)
	if mask != nil {
		fmt.Println("err==>", mask)
	}
	global.GoSdk.Contract[name] = contract
	fmt.Println("Session初始化完成")
}

/*
构造合约操作对象
*/
func bindContract(ABI string, address common.Address, caller bind.ContractCaller, transactor bind.ContractTransactor, filterer bind.ContractFilterer) (*bind.BoundContract, error) {
	parsed, err := abi.JSON(strings.NewReader(ABI))
	if err != nil {
		fmt.Println(err)
		return nil, err
	}
	return bind.NewBoundContract(address, parsed, caller, transactor, filterer), nil
}

```

![image-20241116142242111](https://img-blog.csdnimg.cn/img_convert/27716c263c651c8c88f6ae6f5e1f0883.png)



### 4.编写交易组装器

书写公用发送交易组装器：

```go
package utils

import (
	"fisco-go-sdk-demo/global"
	"fmt"
	"github.com/FISCO-BCOS/go-sdk/abi"
	"github.com/ethereum/go-ethereum/common"
	"strings"
)

/*
公用发送交易组装器
*/
func SendTransaction(name, method string, params ...interface{}) any {
	_, receipt, ok := global.GoSdk.Contract[name].Transact(global.GoSdk.Client.GetTransactOpts(), method, params...)

	if ok != nil {
		fmt.Println("txError=>", ok)
		return nil
	}
	json, wrong := abi.JSON(strings.NewReader(global.Config.Contract[name].Abi))
	if wrong != nil {
		fmt.Println("wrong==>", wrong)
	}

	var (
		result = new(any)
	)
	//合约方法名
	task := json.Unpack(&result, method, common.FromHex(receipt.Output))
	if task != nil {
		fmt.Println("task==>", task)
	}
	return *result
}

// SendCall 获取链上信息
func SendCall(name, method string, out interface{}, params ...interface{}) any {
	err := global.GoSdk.Contract[name].Call(global.GoSdk.Client.GetCallOpts(), out, method, params...)
	if err != nil {
		fmt.Println("SendCall err==>", err)
	}
	return out
}
```

![image-20241116142547071](https://img-blog.csdnimg.cn/img_convert/310832f92e4f1103df3ff636e36a40aa.png)
### 5.HelloWorld的调用

在main函数中调用WeBASE-Front上的合约：

```go
package main

import (
	"fisco-go-sdk-demo/core"
	"fisco-go-sdk-demo/fiscobcos/utils"
	"fmt"
)

const (
	Test_HelloWorld   = "contract1"
	Test_Announcement = "contract2"
)

func TestHelloWorld() {
	//获取string类型
	s1 := new(string)
	utils.SendCall(Test_HelloWorld, "get", s1)

	//更改string类型
	transaction := utils.SendTransaction(Test_HelloWorld, "set", "Hello,FISCO-BCOS")

	//获取string类型
	s2 := new(string)
	utils.SendCall(Test_HelloWorld, "get", s2)

	fmt.Println("SendCall1==>", *s1)
	fmt.Println("transaction2==>", transaction)
	fmt.Println("SendCall12==>", *s2)
}

func main() {

	core.InitConf()
	core.InitClient()

	core.InitSession(Test_HelloWorld)
	TestHelloWorld()

}
```

![image-20241116144852639](https://img-blog.csdnimg.cn/img_convert/2fe60b0c840c5ace482bfdd1c182db5c.png)



启动项目：

​	注意事项：

​	（1）启动程序时，如果报错了，执行go mod tidy。

​	（2）如果还是报错，根据报错信息拉取对应的工具包。

![image-20241116150924721](https://img-blog.csdnimg.cn/img_convert/294ceb7b98ebf9076e4c15764288580a.png)



### 6.Anonoucement的调用



addAnouncement（添加公告）功能测试：

```go
package main

import (
	"fisco-go-sdk-demo/core"
	"fisco-go-sdk-demo/fiscobcos/utils"
	"fmt"
	"math/big"
	"time"
)

const (
	Test_HelloWorld   = "contract1"
	Test_Announcement = "contract2"
)

func main() {
	core.InitConf()
	core.InitClient()
	core.InitSession(Test_Announcement)
	TestAnnouncement()
}

func TestAnnouncement() {
	//addAnouncement  添加公告
	now := time.Now()
	timestamp := now.Unix() // 获取当前时间的Unix时间戳
	fmt.Println("当前Unix时间戳:", timestamp)

	signtime := new(big.Int)
	signtime.SetInt64(timestamp)

	addAnouncementReturn := utils.SendTransaction("contract2", "addAnouncement", "10001", "userPK1", "nounce1", "message1", "cipher1", "attachment1", signtime)
	fmt.Println("addAnouncementReturn=>", addAnouncementReturn)

}
```

下图中的"contract2"可以替换为Test_Announcement

![image-20241116150320944](https://img-blog.csdnimg.cn/img_convert/6068499aab663782affa58ee0400a552.png)



执行结果：
![image-20241116150640196](https://img-blog.csdnimg.cn/img_convert/f5d98a8b0ce8f7665c653c756a82ac5f.png)

announcements功能测试：

（1）创建接受数据的结构体:

```go
package contract

import "math/big"

type Output struct {
	Id         *big.Int
	UserID     string
	UserPK     string
	Nounce     string
	Message    string
	Cipher     string
	Attachment string
	Signtime   *big.Int
}
```
![image-20241114143222885](https://img-blog.csdnimg.cn/img_convert/176a651fa53beb41cb67d065a408b098.png)


(2)运行的代码

```go
func TestAnnouncement() {
	//addAnouncement  添加公告
	//now := time.Now()
	//timestamp := now.Unix() // 获取当前时间的Unix时间戳
	//fmt.Println("当前Unix时间戳:", timestamp)
	//
	//signtime := new(big.Int)
	//signtime.SetInt64(timestamp)
	//
	//addAnouncementReturn := utils.SendTransaction(Test_Announcement, "addAnouncement", "10006", "userPK6", "nounce6", "message6", "cipher6", "attachment6", signtime)
	//fmt.Println("addAnouncementReturn=>", addAnouncementReturn)

	//announcements 查看公告
	index := new(big.Int)
	index.SetInt64(1)

	out := new(contract.Output)

	utils.SendCall(Test_Announcement, "announcements", out, index)
	fmt.Println("public method announcementsReturn =>", *out)
}
```



![image-20241116153056085](https://img-blog.csdnimg.cn/img_convert/c665534079b3e8921a816a5d3dcd8cd7.png)

![image-20241116151238464](https://img-blog.csdnimg.cn/img_convert/4c19b19942fc5d291fb53e57d3564040.png)



listAnouncement功能测试（在进行测试时，测试人员使用addAnouncement函数，上传了多个公告）：

​	（1）不用查询条件进行查询（返回所有数据）：

```go
func TestAnnouncement() {
	//addAnouncement  添加公告
	//now := time.Now()
	//timestamp := now.Unix() // 获取当前时间的Unix时间戳
	//fmt.Println("当前Unix时间戳:", timestamp)
	//
	//signtime := new(big.Int)
	//signtime.SetInt64(timestamp)
	//
	//addAnouncementReturn := utils.SendTransaction(Test_Announcement, "addAnouncement", "10006", "userPK6", "nounce6", "message6", "cipher6", "attachment6", signtime)
	//fmt.Println("addAnouncementReturn=>", addAnouncementReturn)

	//announcements 查看公告
	//index := new(big.Int)
	//index.SetInt64(6)
	//
	//out := new(contract.Output)
	//
	//utils.SendCall(Test_Announcement, "announcements", out, index)
	//fmt.Println("public method announcementsReturn =>", *out)

	//listAnouncement (分页获取数据)
	ans := new([]contract.Output)
	byUserID := false //是否使用UserID，来进行查询
	userID := "10005"

	byTimeRange := false //是否使用时间范围来查询
	startTime := new(big.Int)
	startTime.SetInt64(0) //设置起始时间

	end_now := time.Now()
	end_imestamp := end_now.Unix() // 获取当前时间的Unix时间戳
	fmt.Println("当前Unix时间戳:", end_imestamp)

	endTime := new(big.Int)
	endTime.SetInt64(end_imestamp) //设置结束时间
	pageSize := new(big.Int)       //每页大小
	pageSize.SetInt64(10)
	startIndex := new(big.Int) //起始索引
	startIndex.SetInt64(0)

	utils.SendCall(Test_Announcement, "listAnouncement", ans, byUserID, userID, byTimeRange, startTime, endTime, pageSize, startIndex)
	fmt.Println("listAnouncementReturn Length =>", len(*ans))
	fmt.Println("listAnouncementReturn =>", *ans)
}
```



![image-20241116154046671](https://img-blog.csdnimg.cn/img_convert/17aa4dec7db9f2290ae2ac7cce403797.png)

执行结果：

![image-20241116154244229](https://img-blog.csdnimg.cn/img_convert/8cff09a5d995496dc5c9d479da25fb5a.png)

​	（2）使用UserID进行查询：

**注意事项**：**将byUserID的值赋值为true**

![image-20241116154359736](https://img-blog.csdnimg.cn/img_convert/fde34d4612fa6a7d3f332523c4cc2d22.png)

​

​	（3）使用startTime,endTime进行查询：

**注意事项**：

**（1）将byTimeRange的值赋值为true，将byUserID的值赋值为false**

**（2）endTime（终止时间）的设置要注意**

![image-20241116154548801](https://img-blog.csdnimg.cn/img_convert/0ae9b5bcfc4a73cc36cdfc2e05f68c9b.png)

（4）使用UserID和startTime,endTime进行查询：

**注意事项**：

**（1）将byTimeRange和byUserID的值赋值为true**

**（2）会根据UserID和startTime,endTime进行查询，把所有符合条件的值返回**

![image-20241116154630150](https://img-blog.csdnimg.cn/img_convert/e02cd5b859a6f3582461ad85e375109e.png)



## 三，使用私钥发送交易

### 1.更改配置文件config.toml

![image-20241116133305258](https://img-blog.csdnimg.cn/img_convert/facd5f14b7937178fd4863dd655edde5.png)

### 2.增加私钥生成方法及计算公钥，计算地址方法

​	(1)生成文件路径：fisco-go-sdk-demo/fiscobcos/utils/secret.go

```go
package utils

import (
	"crypto/ecdsa"
	"fmt"
	"github.com/ethereum/go-ethereum/common/hexutil"
	"github.com/ethereum/go-ethereum/crypto"
	"log"
)

// GeneratePriKey  生成私钥
func GeneratePriKey() (*ecdsa.PrivateKey, error) {
	//SDK发送交易需要一个外部账户，导入go-sdk的`crypto`包，该包提供用于生成随机私钥的`GenerateKey`方法：
	privateKey, err := crypto.GenerateKey()
	if err != nil {
		log.Fatal(err)
		return nil, err
	}
	//然后我们可以通过导入golang`crypto/ecdsa`包并使用`FromECDSA`方法将其转换为字节：
	privateKeyBytes := crypto.FromECDSA(privateKey)

	//我们现在可以使用go-sdk的`common/hexutil`包将它转换为十六进制字符串，该包提供了一个带有字节切片的`Encode`方法。 然后我们在十六进制编码之后删除“0x”。
	fmt.Println("Figure PrivateKey: ", hexutil.Encode(privateKeyBytes)[2:]) // privateKey in hex without "0x"
	//这就是`用于签署交易的私钥，将被视为密码，永远不应该被共享给别人`。
	return privateKey, nil
}

// FigurePublicKey 根据私钥计算公钥
func FigurePublicKey(privateKey *ecdsa.PrivateKey) (*ecdsa.PublicKey, bool) {
	//由于公钥是从私钥派生的，加密私钥具有一个返回公钥的`Public`方法：
	publicKey := privateKey.Public()

	//将其转换为十六进制的过程与我们使用转化私钥的过程类似。 我们剥离了`0x`和前2个字符`04`，它始终是EC前缀，不是必需的。
	publicKeyECDSA, ok := publicKey.(*ecdsa.PublicKey)
	if !ok {
		log.Fatal("cannot assert type: publicKey is not of type *ecdsa.PublicKey")
		return nil, ok
	}
	publicKeyBytes := crypto.FromECDSAPub(publicKeyECDSA)
	fmt.Println("Figure PublicKey: ", hexutil.Encode(publicKeyBytes)[4:]) // publicKey in hex without "0x"
	return publicKeyECDSA, true
}

// FiguredAddress 根据公钥计算地址
func FiguredAddress(publicKeyECDSA *ecdsa.PublicKey) string {
	//现在我们拥有公钥，就可以轻松生成你经常看到的公共地址。 加密包里有一个`PubkeyToAddress`方法，它接受一个ECDSA公钥，并返回公共地址。
	address := crypto.PubkeyToAddress(*publicKeyECDSA).Hex()
	//fmt.Println("address: ", strings.ToLower(address)) // account address
	return address
}
```

![image-20241116161746729](https://img-blog.csdnimg.cn/img_convert/c959e769bdd3a2d226e321837610622a.png)

### 3.新增通过私钥发送交易的函数

​	（1）路径：fisco-go-sdk-demo/fiscobcos/utils/fisco.go

```go
package utils

import (
	goecdsa "crypto/ecdsa"
	"errors"
	"fisco-go-sdk-demo/global"
	"fmt"
	"github.com/FISCO-BCOS/go-sdk/abi"
	"github.com/FISCO-BCOS/go-sdk/abi/bind"
	"github.com/FISCO-BCOS/go-sdk/core/types"
	"github.com/ethereum/go-ethereum/common"
	"github.com/ethereum/go-ethereum/crypto"
	"strings"
)

/*
公用发送交易组装器
*/
func SendTransaction(name, method string, params ...interface{}) any {
	_, receipt, ok := global.GoSdk.Contract[name].Transact(global.GoSdk.Client.GetTransactOpts(), method, params...)

	if ok != nil {
		fmt.Println("txError=>", ok)
		return nil
	}
	json, wrong := abi.JSON(strings.NewReader(global.Config.Contract[name].Abi))
	if wrong != nil {
		fmt.Println("wrong==>", wrong)
	}

	var (
		result = new(any)
	)
	//合约方法名
	task := json.Unpack(&result, method, common.FromHex(receipt.Output))
	if task != nil {
		fmt.Println("task==>", task)
	}
	return *result
}

// SendCall 获取链上信息
func SendCall(name, method string, out interface{}, params ...interface{}) any {
	err := global.GoSdk.Contract[name].Call(global.GoSdk.Client.GetCallOpts(), out, method, params...)
	if err != nil {
		fmt.Println("SendCall err==>", err)
	}
	return out
}

func NewKeyedTransactor(key *goecdsa.PrivateKey) *bind.TransactOpts {
	//key, _ := ecdsa.GenerateKey(elliptic.P256(), rand.Reader)
	keyAddr := crypto.PubkeyToAddress(key.PublicKey)
	return &bind.TransactOpts{
		From: keyAddr,
		Signer: func(signer types.Signer, address common.Address, tx *types.Transaction) (*types.Transaction, error) {
			if address != keyAddr {
				return nil, errors.New("not authorized to sign this account")
			}
			signature, err := crypto.Sign(signer.Hash(tx).Bytes(), key)
			if err != nil {
				return nil, err
			}
			return tx.WithSignature(signer, signature)
		},
	}
}

// SendTransactionByKey 使用私钥发送交易
func SendTransactionByKey(name, method string, privateKey *goecdsa.PrivateKey, params ...interface{}) any {
	_, receipt, ok := global.GoSdk.Contract[name].Transact(NewKeyedTransactor(privateKey), method, params...)

	if ok != nil {
		fmt.Println("txError=>", ok)
		return nil
	}
	json, wrong := abi.JSON(strings.NewReader(global.Config.Contract[name].Abi))
	if wrong != nil {
		fmt.Println("wrong==>", wrong)
	}

	var (
		result = new(any)
	)
	//合约方法名
	task := json.Unpack(&result, method, common.FromHex(receipt.Output))
	if task != nil {
		fmt.Println("task==>", task)
	}
	return *result
}

// SendCallByKey SendCall 获取链上信息
func SendCallByKey(name, method string, privateKey *goecdsa.PrivateKey, out interface{}, params ...interface{}) any {
	clientCallOpts := &bind.CallOpts{From: NewKeyedTransactor(privateKey).From}
	err := global.GoSdk.Contract[name].Call(clientCallOpts, out, method, params...)
	if err != nil {
		fmt.Println("SendCall err==>", err)
	}
	return out
}
```

### 4.使用新增的函数生成私钥发送交易

**注意事项：（这里只做HelloWorld合约调用，其他合约的调用方式都差不多）**

```go
package main

import (
	"crypto/ecdsa"
	"fisco-go-sdk-demo/core"
	"fisco-go-sdk-demo/fiscobcos/utils"
	contract "fisco-go-sdk-demo/models/contracts"
	"fmt"
	"math/big"
	"time"
)

const (
	Test_HelloWorld   = "contract1"
	Test_Announcement = "contract2"
)

func TestHelloWorld(key *ecdsa.PrivateKey) {
	//获取string类型
	s1 := new(string)
	utils.SendCallByKey(Test_HelloWorld, "get", key, s1)

	//更改string类型
	transaction := utils.SendTransactionByKey(Test_HelloWorld, "set", key, "Hello,FISCO-BCOS")

	//获取string类型
	s2 := new(string)
	utils.SendCallByKey(Test_HelloWorld, "get", key, s2)

	fmt.Println("SendCall1==>", *s1)
	fmt.Println("transaction2==>", transaction)
	fmt.Println("SendCall12==>", *s2)
}
func main() {
	core.InitConf()
	core.InitClient()
	core.InitSession(Test_HelloWorld)
	//创建私钥
	ecdsaKey, err := utils.GeneratePriKey()
	if err != nil {
		fmt.Println("GeneratePriKey Failed==>", err)
	}
	TestHelloWorld(ecdsaKey)
}
```

![image-20241116155837624](https://img-blog.csdnimg.cn/img_convert/92f101b01b3b80fc7866c7e56ba4b219.png)

![image-20241116155555390](https://img-blog.csdnimg.cn/img_convert/f5e96cab4319f2ff24eb067e3925e05c.png)

### 5.通过WeBASE导出的私钥发送交易

**注意事项：**

​	**代码中不展示具体的调用函数过程，但会通过私钥计算出公钥和地址，与webase上导出的文件作对比**

​	**具体函数调用过程，只需要通过将各类型的私钥，转换成*ecdsa.PrivateKey类型的私钥，就可以调用SendCallByKey(将私钥作为参数传入就行)。**

写入新的工具包

路径：fisco-go-sdk-demo/fiscobcos/utils/trans_ecdsa_key.go

```go
package utils

import (
	"crypto/ecdsa"
	"encoding/asn1"
	"encoding/hex"
	"encoding/pem"
	"errors"
	"fmt"
	"github.com/ethereum/go-ethereum/crypto"
	"github.com/sirupsen/logrus"
	"math/big"
)

// 将十六进制私钥转换成ecdsa私钥
func HexConvertEcdsa(key string) *ecdsa.PrivateKey {
	// 将十六进制私钥字符串解码为字节切片
	byteArray, err := hex.DecodeString(key)
	if err != nil {
		fmt.Printf("Error decoding hex string: %v\n", err)
		return nil
	}
	privateKey, ok := crypto.ToECDSA(byteArray)
	if ok != nil {
		fmt.Println("covert invoke crypto.ToECDSA Failed", ok)
	}
	return privateKey
}

// 将十进制私钥转换成ecdsa私钥
func DeConvertEcdsa(key string) *ecdsa.PrivateKey {
	// 将十进制私钥字符串解码为字节切片
	// 使用 math/big 的 NewInt 方法将十进制字符串转换为大整数
	privateKeyInt := new(big.Int)
	var success bool
	privateKeyInt, success = privateKeyInt.SetString(key, 10) // 10表示十进制
	if !success {
		fmt.Println("Error parsing private key")
		return nil
	}

	// 将大整数转换为字节数组
	privateKeyBytes := privateKeyInt.Bytes()
	privateKey, err := crypto.ToECDSA(privateKeyBytes)
	if err != nil {
		fmt.Println("covert invoke crypto.ToECDSA Failed", err)
	}
	return privateKey
}

// 将pem私钥转换成ecdsa私钥

func PemConvertEcdsa(key string) *ecdsa.PrivateKey {
	IsSMCrypto := false //非国密设置
	keyBytes, curve, err := LoadECPrivateKeyFromPEM(key)
	if err != nil {
		fmt.Errorf("parse private key failed, err: %v", err)
	}
	if IsSMCrypto && curve != "sm2p256v1" {
		fmt.Errorf("smcrypto must use sm2p256v1 private key, but found %s", curve)
	}
	if !IsSMCrypto && curve != "secp256k1" {
		fmt.Errorf("must use secp256k1 private key, but found %s", curve)
	}
	privateKey, err := crypto.ToECDSA(keyBytes)
	if err != nil {
		logrus.Fatal(err)
	}
	return privateKey
}

// LoadECPrivateKeyFromPEM reads file, divides into key and certificates
func LoadECPrivateKeyFromPEM(key string) ([]byte, string, error) {
	// 移除PEM头尾，只保留Base64编码的部分
	block, _ := pem.Decode([]byte(key))

	if block == nil {
		return nil, "", fmt.Errorf("Failure reading pem from %s", key)
	}
	if block.Type != "PRIVATE KEY" {
		return nil, "", fmt.Errorf("Failure reading private key from %s", key)
	}
	ecPirvateKey, curveName, ok := parsePKCS8ECPrivateKey(block.Bytes)
	if ok != nil {
		return nil, "", fmt.Errorf("Failure reading private key from \"%s\": %s", key, ok)
	}
	return ecPirvateKey, curveName, nil
}

// parseECPrivateKey is a copy of x509.ParseECPrivateKey, supported secp256k1 and sm2p256v1
func parsePKCS8ECPrivateKey(der []byte) (keyHex []byte, curveName string, err error) {
	oidNamedCurveSm2p256v1 := asn1.ObjectIdentifier{1, 2, 156, 10197, 1, 301}
	oidNamedCurveSecp256k1 := asn1.ObjectIdentifier{1, 3, 132, 0, 10}

	oidPublicKeyECDSA := asn1.ObjectIdentifier{1, 2, 840, 10045, 2, 1}
	// AlgorithmIdentifier represents the ASN.1 structure of the same name. See RFC
	// 5280, section 4.1.1.2.
	type AlgorithmIdentifier struct {
		Algorithm  asn1.ObjectIdentifier
		Parameters asn1.RawValue `asn1:"optional"`
	}
	var pkcs8 struct {
		Version    int
		Algo       AlgorithmIdentifier
		PrivateKey []byte
		// optional attributes omitted.
	}
	var privKey struct {
		Version       int
		PrivateKey    []byte
		NamedCurveOID asn1.ObjectIdentifier `asn1:"optional,explicit,tag:0"`
		PublicKey     asn1.BitString        `asn1:"optional,explicit,tag:1"`
	}
	if _, err := asn1.Unmarshal(der, &pkcs8); err != nil {
		return nil, "", errors.New("x509: failed to parse EC private key embedded in PKCS#8: " + err.Error())
	}
	if !pkcs8.Algo.Algorithm.Equal(oidPublicKeyECDSA) {
		return nil, "", fmt.Errorf("x509: PKCS#8 wrapping contained private key with unknown algorithm: %v", pkcs8.Algo.Algorithm)
	}
	bytes := pkcs8.Algo.Parameters.FullBytes
	namedCurveOID := new(asn1.ObjectIdentifier)
	if _, err := asn1.Unmarshal(bytes, namedCurveOID); err != nil {
		namedCurveOID = nil
		return nil, "", fmt.Errorf("parse namedCurveOID failed")
	}
	if _, err := asn1.Unmarshal(pkcs8.PrivateKey, &privKey); err != nil {
		return nil, "", errors.New("x509: failed to parse EC private key: " + err.Error())
	}
	var curveOrder *big.Int

	switch {
	case namedCurveOID.Equal(oidNamedCurveSecp256k1):
		curveName = "secp256k1"
		curveOrder, _ = new(big.Int).SetString("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16)
	case namedCurveOID.Equal(oidNamedCurveSm2p256v1):
		curveName = "sm2p256v1"
		curveOrder, _ = new(big.Int).SetString("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16)
	default:
		return nil, "", fmt.Errorf("unknown namedCurveOID:%+v", namedCurveOID)
	}

	k := new(big.Int).SetBytes(privKey.PrivateKey)
	if k.Cmp(curveOrder) >= 0 {
		return nil, "", errors.New("x509: invalid elliptic curve private key value")
	}
	return privKey.PrivateKey, curveName, nil
}

```

![image-20241116162003950](https://img-blog.csdnimg.cn/img_convert/6ca39d0aacfd2588d67de5894c74ee37.png)

（1）十六进制私钥的使用

新增用户：

![image-20241116160740684](https://img-blog.csdnimg.cn/img_convert/abd4a5ba76d96a008e15155700c3601e.png)

![image-20241116160851606](https://img-blog.csdnimg.cn/img_convert/451805035f4f0dcefc7b3062a32bdb69.png)

导出私钥

![image-20241116160943253](https://img-blog.csdnimg.cn/img_convert/b16545a40319385d1c9db21a6c39565d.png)

打开这个文件：

**（它是一个键值的形式，找到privateKey这个键，后面的值，复制出来）**



编写代码：

**注意事项：**

**自己操作时，请操作自己导出的私钥**

```
package main

import (
	"crypto/ecdsa"
	"fisco-go-sdk-demo/core"
	"fisco-go-sdk-demo/fiscobcos/utils"
	contract "fisco-go-sdk-demo/models/contracts"
	"fmt"
	"math/big"
	"time"
)

const (
	Test_HelloWorld   = "contract1"
	Test_Announcement = "contract2"
)

func TestHelloWorld(key *ecdsa.PrivateKey) {
	//获取string类型
	s1 := new(string)
	utils.SendCallByKey(Test_HelloWorld, "get", key, s1)

	//更改string类型
	transaction := utils.SendTransactionByKey(Test_HelloWorld, "set", key, "Hello,FISCO-BCOS")

	//获取string类型
	s2 := new(string)
	utils.SendCallByKey(Test_HelloWorld, "get", key, s2)

	fmt.Println("SendCall1==>", *s1)
	fmt.Println("transaction2==>", transaction)
	fmt.Println("SendCall12==>", *s2)
}
func main() {
	core.InitConf()
	core.InitClient()
	core.InitSession(Test_HelloWorld)

	//将复制的私钥放到这
	privateKey := "9aa6caadc26030aebf7d5a00ba18156d1b20e490a84bee070f87858f8aa7e5fe"
	//将这个(十六进制的)私钥转换成 *ecdsa.PrivateKey格式的私钥
	ecdsaKey := utils.HexConvertEcdsa(privateKey)
	//计算公钥
	publicKey, _ := utils.FigurePublicKey(ecdsaKey)
	//计算地址
	address := utils.FiguredAddress(publicKey)
	fmt.Println("FiguredAddress==>", address)
	TestHelloWorld(ecdsaKey)
}
```

![image-20241116163916089](https://img-blog.csdnimg.cn/img_convert/dc634f51a20ce0e62e7271598848183d.png)

![image-20241116164821097](https://img-blog.csdnimg.cn/img_convert/bf9acfdad14b471fdef9d4e863e568e8.png)

（2）十进制私钥的使用

在WeBASE-Front的页面导出十进制的私钥，用文本文档打开，复制私钥。

更改主函数：

**注意事项：**

**自己操作时，请操作自己导出的私钥**

```go
func main() {
	core.InitConf()
	core.InitClient()
	core.InitSession(Test_HelloWorld)

	//将复制的私钥放到这
	privateKey := "69950874131879215642161436574072734187363931343533962012415567965774976378366"
	//将这个(十进制的)私钥转换成 *ecdsa.PrivateKey格式的私钥
	ecdsaKey := utils.DeConvertEcdsa(privateKey)
	//计算公钥
	publicKey, _ := utils.FigurePublicKey(ecdsaKey)
	//计算地址
	address := utils.FiguredAddress(publicKey)
	fmt.Println("FiguredAddress==>", address)
	TestHelloWorld(ecdsaKey)
}
```

![image-20241116165618891](https://img-blog.csdnimg.cn/img_convert/40c3921c0307eafe9a0b25ab4ed416d2.png)

执行成功：

**注意事项：**

**由于导出十进制私钥没有附带address,publicKey。所以用导出的txt形式做对比**

![image-20241116165812489](https://img-blog.csdnimg.cn/img_convert/2830bb47438475f21cca41ee31d9974b.png)

（3）pem格式私钥的使用

在WeBASE-Front的页面导出pem格式的私钥，用文本文档打开，复制私钥。

更改主函数:

**注意事项：**

**自己操作时，请操作自己导出的私钥**

```go
func main() {
	core.InitConf()
	core.InitClient()
	core.InitSession(Test_HelloWorld)

	//将复制的私钥放到这
	privateKey := "-----BEGIN PRIVATE KEY-----\nMIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgmqbKrcJgMK6/fVoAuhgV\nbRsg5JCoS+4HD4eFj4qn5f6gBwYFK4EEAAqhRANCAATan9lZU4g6+DXBScOZ0X5U\npkuMg+CAPi85MijzzFof7cY8NvrZX+fy5hrOw5SRnKHnyw4VQYbFzpaqU7L84Pa3\n-----END PRIVATE KEY-----"
	//将这个(pem格式的)私钥转换成 *ecdsa.PrivateKey格式的私钥
	ecdsaKey := utils.PemConvertEcdsa(privateKey)
	//计算公钥
	publicKey, _ := utils.FigurePublicKey(ecdsaKey)
	//计算地址
	address := utils.FiguredAddress(publicKey)
	fmt.Println("FiguredAddress==>", address)
	TestHelloWorld(ecdsaKey)
}
```

![image-20241116170242065](https://img-blog.csdnimg.cn/img_convert/a8e3a51fc9b7d2d7f5286bffdb5cdc5a.png)


执行成功：

**注意事项：**

**由于导出pem格式私钥没有附带address,publicKey。所以用导出的txt形式做对比**

![image-20241116170650246](https://img-blog.csdnimg.cn/img_convert/ae1daa9fcb738f8fae529008249a0ba3.png)

## 四，小结：

### 1.发送交易的函数

​	（1）在不使用动态私钥的情况：

```
SendTransaction  (作用：往链上存储信息，注意：合约函数的返回值最好设定为bool值。)
SendCall   (作用：获取链上信息,注意：合约函数的传入参数和返回值需要定义模型 )
```

​	（2）在使用动态私钥的情况：

```
SendTransactionByKey (需要传入私钥) (作用：往链上存储信息，注意：被调用合约函数的返回值最好设定为bool值。)
SendCallByKey  (需要传入私钥) (作用：获取链上信息,注意：合约函数的传入参数和返回值需要定义模型。 )
```



### 2.合约类型和go语言中的类型对应

| 合约中传入和返回参数       | Go语言中使用对应类型                                         |
| ------------------ | ------------------------------------------------------------ |
| string             | string或者*string                                            |
| uint256            | *big.Int                                                     |
| uint8              | *uint8                                                       |
| address            | *common.address(用这个包：github.com/ethereum/go-ethereum/common) |
| bool               | bool或者*bool                                                |
| uint256[]         | *[]big.Int                                                   |
| string[]           | *[]string                                                    |
| 结构体（例如：User）      | *User                                                        |
| 结构体数组（例如：User[]） | *[]User                                                      |

**注意事项：**

**go语言中声明传入、传出的参数的模型时，可以直接：new一个类型 赋值给 变量**

**例如：num:= new(big.Int)**

## 四.参与贡献

陈雅凯,高俭豪,谭俊



