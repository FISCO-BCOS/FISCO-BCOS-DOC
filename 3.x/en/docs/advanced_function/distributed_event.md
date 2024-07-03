#  4. Distributed Event Framework WeEvent
Tags: "distributed events"

-------

WeEvent is a trusted, reliable, and efficient cross-agency, cross-platform event notification mechanism that supports FISCO BCOS。

Specifically, WeEvent is a blockchain-based event middleware service that provides users with the ability to publish and subscribe to events。Events posted to WeEvent are permanently stored, immutable, and support post-event tracking and auditing。The producer publishes events through the WeEvent proxy service. The event content is recorded on the blockchain, such as FISCO-BCOS. The consumer subscribes to events from WeEvent。After the subscription is successful, as long as the producer publishes the event, the consumer will be notified in time。

The WeEvent service is developed using the Spring Boot framework. In terms of business integration, it not only supports directly loading independent JAR packages to use services, but also supports providing functions through proxy services。

Adhering to the design concept of peer-to-peer cooperation, intelligent collaboration and value sharing in the distributed business model, weEvent is committed to improving the efficiency of inter-agency cooperation, reducing cooperation costs, and opening up different platforms such as applications, the Internet of Things, cloud services and private services, and ultimately achieving cross-agency and cross-platform event notification and processing without changing the development language and access protocols of existing commercial systems。

For details, please refer to

- [WeEvent Online Documentation](https://weeventdoc.readthedocs.io/zh_CN/latest/index.html)

- [WeEvent github Home Page](https://github.com/WeBankBlockchain/WeEvent)