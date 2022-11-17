# 拆解build_chain.sh解读FISCO-BCOS建链过程
作者：重庆电子工程职业学院 | 向键雄

这里是本教程的：[配套视频](https://space.bilibili.com/335373077)

# 起链
我们在这里就不完整的去讲起链的方式，挑重点讲如果大家对起链感兴趣或者不熟悉，可以去看我的另一篇文章
[【教程】完美的FISCO-BCOS区块链网络如何启动，单机四节点，联盟链](https://blog.csdn.net/qq_57309855/article/details/126180787?spm=1001.2014.3001.5501)

首先我会下载build_chain.sh脚本

使用建链命令进行建链（在这里使用一个节点目录方便教学）

```

bash build_chain.sh -l  127.0.0.1 -p 30300,20200,8545
我们可以先看一下窗口中返回的信息

```
![build_chain sh反馈截图](https://user-images.githubusercontent.com/111106471/184881953-bcacb07b-f6b5-4ab2-9166-20aebe5bcc37.png)




## 第一段

```

[INFO] Downloading fisco-bcos binary from https://github.com/FISCO-BCOS/FISCO-BCOS/releases/download/v2.9.0/fisco-bcos.tar.gz ... 
curl: (7) Failed to connect to github.com port 443: Connection refused ` 
 
[INFO] Download speed is too low, try https://osp-1257653870.cos.ap-guangzhou.myqcloud.com/FISCO-BCOS/FISCO-BCOS/releases/v2.9.0/fisco-bcos.tar.gz 

这里讲了我们去访问GitHub中下载FISCO-BCOS的压缩包，结果发现链接失败所以跳转到国内的码云中去下载

```

这里对应这我们build_chain.sh中的1633行
![download_bin代码截图](https://user-images.githubusercontent.com/111106471/184882007-e90f1b3c-0bb9-407d-b2ad-194c1c0a21d5.png)
![download_link代码截图](https://user-images.githubusercontent.com/111106471/184882027-38fe8614-632d-4335-9e50-7bc86f187529.png)



 这里的cdn_download_link在上面环境变量中有指定



 在下面有许多的判断就是为了预防在国内无法访问GitHub的情况发生

## 第二段

```

==============================================================
Generating CA key...
==============================================================

```
 
生成CA秘钥对应脚本1677行，在脚本内的运行流程就是找到${output_dir}下的cert目录将CA证书存放进去，${output_dir}在上面定义的是nodes目录，所以我们进入后就可以看见我们的CA证书，具体CA证书的生成会有专栏来讲解。

![prepareCA代码截图](https://user-images.githubusercontent.com/111106471/184881843-f81179d6-945f-4e2e-b64c-6b1e86a3cd50.png)
![output_dir截图](https://user-images.githubusercontent.com/111106471/184881884-f3ff536b-0d61-46de-af3b-832f2982b129.png)
![nodes目录截图](https://user-images.githubusercontent.com/111106471/184881899-d5980b81-7f54-487b-b280-5cb435df7c36.png)


 

## 第三段

```

============================================================== 
Generating keys and certificates ...
Processing IP=127.0.0.1 Total=1 Agency=agency Groups=1
==============================================================

```

生成秘钥和证书对应在脚本的1793行，脚本内的运行流程就是在输入起链命令后将他们赋给变量$OPTARG接收，确定起链模式，节点数量，确定IP，群组等参数，开始创建节点目录节点目录由node_count来确定这个node_count就是我们在起链时输入的节点数量我们是默认单节点，所以建起来的目录就是一个目录

​
![秘钥证书代码截图](https://user-images.githubusercontent.com/111106471/184882272-d8c63950-9576-42c8-bff5-5b11349f6b9c.png)
![秘钥证书生成代码截图2](https://user-images.githubusercontent.com/111106471/184882282-2bb1ed99-5605-478c-9d64-015e6b1a2ec9.png)
![127 0 0 1文件夹截图](https://user-images.githubusercontent.com/111106471/184882372-f5230d39-98aa-4b27-9b5b-b18a1212e134.png)


​

​

## 第四段

```

==============================================================
Generating configuration files ...
Processing IP=127.0.0.1 Total=1 Agency=agency Groups=1
==============================================================

```

生成配置文件对应在脚本的1925行，脚本内的运行流程就是先确定证书的输出目录的位置，用node_count和node_dir进行接收之后就将生成的证书发送到目录下，生成的证书有群组证书，group.X.genesis、group.x.ini、config.ini、和agency目录



## 第五段

```

==============================================================
 [INFO] Start Port      : 30300 20200 8545`
 [INFO] Server IP       : 127.0.0.1
 [INFO] Output Dir      : /home/fisco223/fisco/nodes
 [INFO] CA Path         : /home/fisco223/fisco/nodes/cert/
 [INFO] RSA channel     : true`
==============================================================

```

这里是将所有的端口和服务以及最终的工作目录反馈给使用者，用于自身确定是否达到预期以及预防工作量过大后配置文件等找不到的情况，对应脚本226行 
![生成配置文件代码截图](https://user-images.githubusercontent.com/111106471/184882563-01bbca48-4460-408d-878e-4214e5563777.png)



第六段

```

 ==============================================================
 [INFO] Execute the download_console.sh script in directory named by IP to get FISCO-BCOS console.
e.g.  bash /home/fisco223/fisco/nodes/127.0.0.1/download_console.sh -f 
 ==============================================================
 [INFO] All completed. Files in /home/fisco223/fisco/nodes
 
 ```
 
这里就是提醒使用者在IP命名的目录中使用sh脚本获取FISCO-BCOS控制台。并且举了个例子e.g.讲解了用法，最终提示用户所有的流程已经结束，搭建完成工作目录在${output_dir}下。
![反馈信息截图](https://user-images.githubusercontent.com/111106471/184882668-3f673308-042c-419d-9bc0-2b1fc49c3500.png)


