# AMOP Use Cases

Tag: "go-sdk`` ``AMOP``

----

AMOP (Advanced Messages Onchain Protocol) is an on-chain messenger protocol designed to provide a secure and efficient message channel for the consortium chain. All institutions in the consortium chain can use AMOP to communicate as long as they deploy blockchain nodes, whether they are consensus nodes or observation nodes. AMOP has the following advantages:

- Real-time: AMOP messages do not rely on blockchain transactions and consensus. Messages are transmitted between nodes in real time with a latency of milliseconds.。
- Reliable: When AMOP messages are transmitted, all feasible links in the blockchain network are automatically searched for communication, and as long as at least one link is available between the sending and receiving parties, the message is guaranteed to be reachable.。
- Efficient: AMOP message structure is simple, efficient processing logic, only a small amount of cpu occupation, can make full use of network bandwidth。
- Easy to use: when using AMOP, no need to do any additional configuration in the SDK。

To learn more about AMOP, please refer to: [On-Chain Messenger Protocol](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/manual/amop_protocol.html)。Case source code, please refer to: [go-sdk](https://github.com/FISCO-BCOS/go-sdk)

## Unicast Case

**Unicast** A node randomly selects a subscriber from multiple subscribers listening to the same topic to forward a message. For details about the process, see [Unicast Timing Diagram](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/p2p/p2p.html#id11)

- Start the AMOP message subscriber:

    ```shell
    # go run examples/amop/sub/subscriber.go [endpoint] [topic]
    > go run examples/amop/sub/subscriber.go 127.0.0.1:20201 hello

      Subscriber success
      2020/08/11 21:21:50 received: hello, FISCO BCOS, I am unique broadcast publisher! 0
      2020/08/11 21:21:52 received: hello, FISCO BCOS, I am unique broadcast publisher! 1
      2020/08/11 21:21:54 received: hello, FISCO BCOS, I am unique broadcast publisher! 2
      2020/08/11 21:21:56 received: hello, FISCO BCOS, I am unique broadcast publisher! 3
    ```

- Run AMOP Message Publisher:

    ```shell
    # go run examples/amop/unicast_pub/publisher.go [endpoint] [topic]
    > go run examples/amop/unicast_pub/publisher.go 127.0.0.1:20200 hello

      2020/08/11 21:21:50 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 0
      2020/08/11 21:21:52 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 1
      2020/08/11 21:21:54 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 2
      2020/08/11 21:21:56 publish message: hello, FISCO BCOS, I am unique broadcast publisher! 3
    ```

## Multicast Case

**Multicast** This means that the node forwards messages to all subscribers listening on the same topic.。As long as the network is normal, even if there is no subscriber listening to the topic, the message publisher will receive the response packet that the node message is pushed successfully. For details of the process, please refer to [Multicast Timing Diagram](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/p2p/p2p.html#id12)

- Start the AMOP message subscriber:

    ```shell
    # go run examples/amop/sub/subscriber.go [endpoint] [topic]
    > go run examples/amop/sub/subscriber.go 127.0.0.1:20201 hello

      Subscriber success
      2020/08/11 21:23:54 received: hello, FISCO BCOS, I am multi broadcast publisher! 0
      2020/08/11 21:23:56 received: hello, FISCO BCOS, I am multi broadcast publisher! 1
      2020/08/11 21:23:58 received: hello, FISCO BCOS, I am multi broadcast publisher! 2
      2020/08/11 21:24:00 received: hello, FISCO BCOS, I am multi broadcast publisher! 3
    ```

- Run AMOP Message Publisher:

    ```shell
    # go run examples/amop/multicast_pub/publisher.go [endpoint] [topic]
    > go run examples/amop/multicast_pub/publisher.go 127.0.0.1:20200 hello

      2020/08/11 21:23:54 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 0
      2020/08/11 21:23:56 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 1
      2020/08/11 21:23:58 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 2
      2020/08/11 21:24:00 publish message: hello, FISCO BCOS, I am multi broadcast publisher! 3
    ```
