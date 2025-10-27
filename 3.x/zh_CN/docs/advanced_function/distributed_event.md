#  4. 分布式事件框架 WeEvent
标签： ``分布式事件``

-------

WeEvent是的一套可信、可靠、高效的跨机构、跨平台事件通知机制，支持FISCO BCOS。

具体而言，WeEvent是一个基于区块链实现的事件中间件服务，面向用户提供事件发布订阅Publish/Subscribe功能。发布到WeEvent上的事件永久存储，不可篡改，支持事后跟踪和审计。生产者Producer通过WeEvent代理服务发布事件，事件内容会被记录到区块链如FISCO-BCOS上，消费者Consumer从WeEvent订阅事件。订阅成功后，只要生产者发布事件，消费者都会及时得到通知。

WeEvent服务使用Spring Boot框架开发，在业务集成上，既支持直接加载独立的JAR包使用服务，也支持通过代理服务来提供功能。

weEvent秉承分布式商业模式中对等合作、智能协同、价值共享的设计理念，致力于提升机构间合作效率，降低合作成本，同时打通应用程序、物联网、云服务和私有服务等不同平台，最终在不改变已有商业系统的开发语言、接入协议的情况下，做到跨机构、跨平台的事件通知与处理。

详细内容可参考

- [WeEvent在线文档](https://weeventdoc.readthedocs.io/zh_CN/latest/index.html)

- [WeEvent github主页](https://github.com/WeBankBlockchain/WeEvent)