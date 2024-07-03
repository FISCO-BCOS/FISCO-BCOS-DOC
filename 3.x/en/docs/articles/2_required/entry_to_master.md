# Eliminate all difficulties, from entry to mastery of blockchain

Author: Zhang Kaixiang ｜ Chief Architect, FISCO BCOS

At present, more and more people have entered or are ready to enter the field of blockchain, the process can not help but hold all kinds of doubts and problems。Remembering that I spent a few years ago, from "a little understanding" of the blockchain to all in, I also experienced a similar mental journey, this field does have some thresholds, but everything is difficult at the beginning, and the road to explore is far more than eighty-one difficult, here to sort out a few summary difficulties and insights, I would like to share。

## The difficulty of direction

Who am I, "" where am I, "" where am I going, "all from such a philosophy of three questions。What is Blockchain?？What can blockchain do?？Why is the blockchain so hot?？Can we not use the block chain?？These questions are full of the ultimate torture。

It's hard to answer these questions thoroughly because there are no standard answers。All things at the forefront of innovation are probably so, developing in doubt and turmoil, groping in darkness and desolation, essence and dross flying together, oasis and leek, until the tipping point bursts。If you swing from side to side because you're full of doubts, or if you're stuck, the experience will be bad and the results will not be good in the process of doing related work research。

Share a little personal experience: the blockchain field has attracted countless smart people from all over the world from the very beginning, including geeks, scholars and masters, who have carried out a lot of technical and social practices。This field contains the essence of mathematics, computer science, cryptography, game theory, economics, sociology and other disciplines, which is a world of intellectual flying and ideological agitation。At present, the entire industry is getting unprecedented attention, including the government, the industry giants are paying attention, a lot of attention and resources continue to pour in, blockchain ushered in the "best season."。

In a modern society that continues to evolve and has a diverse structure, the idea of distributed commerce has become a reality。There will be more connections and collaboration between people, institutions and institutions, information and value will flow rapidly in the new network model, and blockchain, as one of the representatives of distributed technology, has a great opportunity to become a stronghold for a new generation of infrastructure and innovation。

So, direction is not a problem。Even if you don't regard blockchain as a "belief," just looking at the fascinating technology itself and the opportunities for the deep integration of blockchain and the real economy can still give us confidence。

## Difficulty of concept

Among the three philosophical questions, "what is the blockchain" is the most obscure question, blocks, transactions, accounts, consensus, smart contracts, double flowers..？！When I first came into contact with the blockchain, I also had a feeling of being overturned。There are some articles that introduce blockchain, often focusing on the social and economic efficiency of blockchain, from the value concept, these are certainly necessary, but as the saying goes, "science should be qualitative and more quantitative," as engineers and technicians, we should pay more attention to the knowledge points and basic principles of blockchain, and then clarify the terminology, grasp the architecture, processing logic and program flow。

As mentioned earlier, blockchain contains the essence of a large number of disciplines, and there is also a saying in the industry: "**Blockchain didn't invent any new technology, it was a combination of mature technologies**”。

-These mature technologies include data structures, such as linked lists, trees, graphs, filters, etc., which are the basic knowledge of data structures in universities；
- Basic cryptography, including HASH and symmetric asymmetric encryption, digital signatures, etc., has been a classic technology for decades, and the new generation of cryptography in areas such as privacy protection has opened up a broad space for cryptography professionals to play；
- The discipline of distributed networks and systems itself is all-encompassing, covering the scope of knowledge of massive services, and many students who have been engaged in massive Internet service technology will have a good understanding of blockchain P2P networks and consensus algorithms, parallel computing models, and transactional consistency principles；
-Game theory and incentive compatibility are knowledge points in collaboration and an important part of "blockchain thinking," engineering students may need to turn over books, and students with a background in social science economics and management are expected to be familiar with it；
- As for smart contracts, such as solidity language, WebAssembly, etc., rarely heard of it？In fact, these languages and programming mode learning curve may not be as high as javascript, there are a few years of program foundation students can basically get started in a week, write a smooth smart contract to。

Blockchain makes people feel cognitive difficulties, because it is like a "basket," everything can be loaded into it, involving a complex technology, the combination of different ways and conventional technology routines。So to a certain extent, learners should first empty themselves, to avoid letting their thinking in the original field interfere with learning, in the enrichment of their knowledge at the same time, accept the blockchain "group consensus," "prevent tampering," "undeniable," "high consistency" and other magical logic, and then dive into each independent concept, and will not feel unattainable。

The main point of breaking through the difficulty of the concept is to eliminate the noise from various channels, some information is specious, or each saying their own words, the same thing with N kinds of speech, confusing the definition, blurring the essence, no help at the same time also bring more questions。The reliable method is to focus on reading the formal content of authoritative media, pay attention to the document library of some mainstream blockchain projects, carefully and comprehensively read through the technical documents, and then find a field of interest (such as consensus algorithm), combined with their own experience and knowledge for comparative research。

At the same time, you can also join the active open source community and technical circles, and experienced people to discuss more, the courage to throw out the problem, each term, each process to discuss thoroughly。In the early days of our research on blockchain, the team often argued bitterly about the definition of a concept for a long time, and finally reached a happy consensus when everyone felt refreshed。

In the conceptual stage, do not seek full blame, do not become a data collection machine, one bite can not be fat, based on reliable learning materials, clarify basic concepts, in practice to verify and explore new concepts, establish a methodology to discover and solve problems, and slowly be able to draw inferences from one another, maybe one day will be able to fill the top。

## Difficult to get started

Well, philosophical and conceptual issues are finally not going to get in the way of our learning, so how do we continue the "21-day entry to mastery" path?。As a technician, encounter new technology platforms, software systems, programming languages... That, of course, is: "Don't be unintelligent, just do it."！”

A few years ago, when we first started working on blockchain, we read through the code of several popular open source blockchain projects abroad and built a test network to analyze how these platforms could be used in complex financial businesses。There was a confusion at the time, if you develop applications directly based on the underlying platform, do you have to modify the underlying platform code directly when you need to implement more features?。

And when you see the "smart contract" this thing, the idea is opened: the use of smart contracts as a middle layer, in the contract to write business logic, and define a clear functional interface for the caller, so that the business can be well decoupled from the underlying, while the underlying platform is positioned as a powerful engine, through the architecture of decoupling, so that the whole development process becomes clear and reasonable, relaxed and happy, it feels like from the "C / S"。

In addition, we felt that the open source projects at the time were mainly in the form of public chains, which were not so well considered in terms of security, control and compliance, and were not suitable for financial scenarios。

So, without taking advantage of the platform, find a way to build one。Since then, the long road of deep-rooted technology and iterative application verification has been opened。This process has also established close cooperation with a number of partners in the open source community, which is "from open source and give back to open source." After several years of polishing by the open source working group, FISCO BCOS has become a comprehensive open source, safe and controllable, high-speed and stable, easy-to-use and friendly financial-grade underlying technology platform, providing a wealth of functions and various operating tools for finance and the broader industry。Rich and comprehensive documentation and easy-to-use experience can help developers from quick start to proficient, the overall technical threshold and development costs have become unprecedented low。

With the underlying underlying platform, downloading, installing, configuring, running, reading user manuals, writing hello world and business applications, Debug and analyzing logs... are all step by step jobs。

Our goal is that users can build their own blockchain network in a few minutes with one-click installation, docker, cloud services, etc., and can write a complete smart contract within a week through learning, and implement business logic based on SDKs supporting multiple languages (Java, Node.js, Python, Go... still being added), and release the business online to maintain stable operation。

To this end, we have been continuously optimizing the use of documentation, development manuals, and deployment and operations tools。As we all know, "code farmers" like to write code, and writing notes and documents is more painful, in order to hand over a beautiful homework to the community, we have devoted their lifetime language level, revised again and again, just wrote hundreds of thousands of words of the document library。

At the same time, the open source community has launched a series of offline and online salons, training, and extensive communication and technical support in a community way。In many live learning and hackathon competitions, we are pleased to see that developers can implement their ingenious project design based on FISCO BCOS in two or three days, and some developers have contributed their optimizations related to open source projects to Github。

To this extent, even for developers who have no experience in blockchain research and development, there is no problem getting started quickly, even if the bottom of the blockchain is still like a black box to be explored, but just like installing App on the computer, using mysql, tomcat and other software, it can be used to feel the charm of the blockchain。

## The difficulty of going deep

For technicians, there is no end to exploring the connotation of technology: participating in the development of the underlying blockchain, realizing large-scale blockchain applications, adding more useful features and tools to the blockchain ecology, and optimizing the performance of existing functions。

As mentioned earlier, blockchain system knowledge points and frameworks are widely covered, both in terms of knowledge and depth。If you quantify it, you can apply the 10,000-hour theory: if you study and work 8-10 hours a day, you can get a small percentage in a year, two years of familiarity, and three years of being an old driver... But the road ahead for an old driver is still long。We hope to shorten this process through continuous science, communication and practice, but after all, learning is a basic "proof of work" and there are no other shortcuts。

Learning methods, first of all, a lot of extensive reading, every morning a eye to the evening, you can see the continuous update of industry news, public number articles, technology big coffee blog, mail group discussion group, open source projects...... The process of reading may encounter the collision of different views, need to remove the false and keep the true, in an open mind at the same time, but also to maintain their own position and direction。

Then there is in-depth intensive reading, first select one or two directions of interest, study some classic papers such as cryptography, distributed theory, etc。The core consensus algorithm of FISCO BCOS uses the PBFT and RAFT algorithms, which are based on the research and interpretation of the original paper, and the implementation and optimization are done with a deep understanding。Cryptographic principles are widely used in blockchains, with changing scenarios and logic, and the principles may come from a "top meeting" paper。Intensive reading depth principle analysis of articles and academic papers, based on solid theory, in order to play according to their own needs, creatively solve engineering problems。

In fact, our online documents have hundreds of thousands of words of scale, all kinds of information, the technical community will regularly interpret the hot knowledge, as long as the reader carefully read the online technical documents, accept the public number of considerate push, and hands-on more practice, over time, will be able to deeply understand the technical principles of the blockchain, through the context of architecture design, the establishment of a solid knowledge system。

Finally, the object of intensive reading, of course, also includes the source code, after all, "Talk is cheap, show me the code," blockchain open source project code is mostly tens of thousands to hundreds of thousands of lines of level, reading the code is the most direct way to achieve the level of the cow。In the course of studying blockchain, we have many long nights to review the code, when we read a squint, the code is flying, all kinds of interfaces and objects dance, both elegant and regular, clear vein, that kind of pleasure is simply indescribable。This experience, before, will continue to have。

If you have reached this level, the field threshold has been basically crossed, the test is the developer's mental and physical strength。 

## Sustained Difficulty

In the past few years, due to a series of situations such as confusion, policies and regulations, technical obstacles, etc., blockchain will encounter challenges such as market volatility and delayed application landing。What is the future, although there is no prophet to tell us, but now we have seen the trend。This goes back to the first question: "direction," a clear and clear direction that answers not only "do you want to enter this field," but also "do you want to stick to it?"。We have been developing scenarios in the distributed business model, providing quality services to the public, and providing the industry with complete and easy-to-use open source technology, which has never changed from the beginning to the present and even in the future。

To be more specific, if we have deployed a business system on the blockchain, there are other issues that affect the lifecycle and sustainability of the system: operability, upgradeability, compatibility, data capacity, business performance capacity, and so on。

The process of communicating with friends in the community many times, who will ask questions, such as whether the new version is compatible with the old one？As your business grows, more and more data can be migrated and reused？This is the true voice of the user。The platform we have built must take the route of sustainable development, pay attention to the compatibility of the software system, have a reasonable release rhythm, and a comprehensive data migration and maintenance strategy, which can better protect the interests of community users and make users willing to develop together with the community for a long time。

In addition, the blockchain field is still in rapid development, a variety of new technologies, new ideas, new models, new policies are still emerging, this field has gathered a large number of smart people in the world, they are not only smart but also hard, never idle。So working in this field, every day there will be new knowledge, new stimulation, this is a kind of luck, on the other hand, it will make people extremely anxious。

How to digest such a huge amount of information, how to explore and master cutting-edge knowledge, how to better meet the needs of users and new challenges brought about by rapid development, and how to make effective breakthrough innovations, this is really a world where innovation and anxiety coexist。

As a practitioner, you must continue to read a lot, filter and absorb in various information streams, and constantly summarize / summarize / think / develop；Every requirement and user ISSUE feedback is a small goal, and every new release is a new starting point for the next version。The world of blockchain is no different from other technology fields. You must be sharp and running, curious and humble, constantly learning and practicing, revising short boards to seek breakthroughs, and sharing the results with the community。Just as systems need great resilience, people need great resilience。Mutual encouragement。

#### Recommended Links

[Blockchain that Friends and Family Can Understand](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485270&idx=1&sn=5a4a5990cd3229df132da6ecc6cd241d&chksm=9f2ef54aa8597c5cfa558ceb65283488fff972bab6cebcb1cf0c2e8155695f628f6958c7fd58&token=705851025&lang=zh_CN#rd)

[FISCO BCOS open source project github address](https://www.github.com/fisco-bcos)

[FISCO BCOS open source project gitee address](https://gitee.com/fisco-bcos)

[Key Concepts Read](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/tutorial/key_concepts.html)

[FISCO BCOS zero-based entry, five steps to easily build applications](https://mp.weixin.qq.com/s?__biz=MzA3MTI5Njg4Mw==&mid=2247485305&idx=1&sn=5a8dc012880aac6f5cd3dacd7db9f1d9&chksm=9f2ef565a8597c73b87fd248c41d1a5b9b0e6a6c6c527baf873498e351e3cb532b77eda9377a&token=705851025&lang=zh_CN#rd)

[In-depth block chain underlying platform FISCO BCOS](https://fisco-bcos-documentation.readthedocs.io/zh_CN/latest/docs/design/index.html)