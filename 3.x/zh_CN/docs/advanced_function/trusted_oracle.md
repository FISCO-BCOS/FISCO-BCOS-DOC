#  5. 可信预言机
标签： ``预言机``  ``Truora``

-----

Truora是一个基于FISCO-BCOS平台的预言机服务。支持FISCO BCOS2.x和3.x版本，Truora工作原理如下：

Truora通过在链下运行的Java服务，监听链上预言机合约事件，发起链下相关的资源访问和计算任务，并将结果返回到链上预言机合约，供链上使用。

Truora支持的特性：

1） 获取外部数据（http/https) 并将结果写回链上，供链上合约验证和使用。

2） 链下生成随机数,供链上验证使用,即VRF可验证随机数。

3） 面向对象的实现，可扩展更多链上事件监听、链下数据获取/计算并可信验证的逻辑。

详细介绍请查看

- [Truora在线文档](https://truora.readthedocs.io/zh_CN/main/)
  
- [Truora](https://github.com/WeBankBlockchain/Truora-Service)