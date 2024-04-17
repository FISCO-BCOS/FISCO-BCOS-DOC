# AMOP 使用案例

标签：``go-sdk`` ``AMOP``

----

AMOP（Advanced Messages Onchain Protocol）即链上信使协议，旨在为联盟链提供一个安全高效的消息信道，联盟链中的各个机构，只要部署了区块链节点，无论是共识节点还是观察节点，均可使用AMOP进行通讯，AMOP有如下优势：

- 实时：AMOP消息不依赖区块链交易和共识，消息在节点间实时传输，延时在毫秒级。
- 可靠：AMOP消息传输时，自动寻找区块链网络中所有可行的链路进行通讯，只要收发双方至少有一个链路可用，消息就保证可达。
- 高效：AMOP消息结构简洁、处理逻辑高效，仅需少量cpu占用，能充分利用网络带宽。
- 易用：使用AMOP时，无需在SDK做任何额外配置。

进一步了解 AMOP，请参考：[链上信使协议](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/amop_protocol.html)。案例源码，请参考：[go-sdk](https://github.com/FISCO-BCOS/go-sdk)

## 单播案例

**单播** 指的是节点从监听相同 Topic 的多个订阅者中随机抽取一个订阅者转发消息，流程详细可参考 [单播时序图](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/p2p/p2p.html#id11)

- 启动 AMOP 消息订阅者：

    ```shell
    # go run examples/amop/sub/subscriber.go [endpoint] [topic]
    > go run examples/amop/sub/subscriber.go 127.0.0.1:20201 hello

      Subscriber success
      2020/08/11 21:21:50 received: hello, FISCO BCOS, I am unique broadcast publisher! 0
      2020/08/11 21:21:52 received: hello, FISCO BCOS, I am unique broadcast publisher! 1
      2020/08/11 21:21:54 received: hello, FISCO BCOS, I am unique broadcast publisher! 2
      2020/08/11 21:21:56 received: hello, FISCO BCOS, I am unique broadcast publisher! 3
    ```

- 运行 AMOP 消息发布者：

    ```shell
    # go run examples/amop/unicast_pub/publisher.go [endpoint] [topic]
    > go run examples/amop/unicast_pub/publisher.go 127.0.0.1:20200 hello

      2020/08/11 21:21:50 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 0
      2020/08/11 21:21:52 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 1
      2020/08/11 21:21:54 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 2
      2020/08/11 21:21:56 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 3
    ```

## 多播案例

**多播** 指的是节点向监听相同 Topic 的所有订阅者转发消息。只要网络正常，即使没有监听 Topic 的订阅者，消息发布者也会收到节点消息推送成功的响应包，流程详细可参考 [多播时序图](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/p2p/p2p.html#id12)

- 启动 AMOP 消息订阅者：

    ```shell
    # go run examples/amop/sub/subscriber.go [endpoint] [topic]
    > go run examples/amop/sub/subscriber.go 127.0.0.1:20201 hello

      Subscriber success
      2020/08/11 21:23:54 received: hello, FISCO BCOS, I am multi broadcast publisher! 0
      2020/08/11 21:23:56 received: hello, FISCO BCOS, I am multi broadcast publisher! 1
      2020/08/11 21:23:58 received: hello, FISCO BCOS, I am multi broadcast publisher! 2
      2020/08/11 21:24:00 received: hello, FISCO BCOS, I am multi broadcast publisher! 3
    ```

- 运行 AMOP 消息发布者：

    ```shell
    # go run examples/amop/multicast_pub/publisher.go [endpoint] [topic]
    > go run examples/amop/multicast_pub/publisher.go 127.0.0.1:20200 hello

      2020/08/11 21:23:54 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 0
      2020/08/11 21:23:56 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 1
      2020/08/11 21:23:58 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 2
      2020/08/11 21:24:00 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 3
    ```
