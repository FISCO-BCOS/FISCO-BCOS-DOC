# What is Trusted in Blockchain?？

Author: Zhang Kaixiang ｜ Chief Architect, FISCO BCOS

Currently, "Blockchain: The trust machine" has become a slogan, followed by a series of powerful-sounding terms such as "decentralization, group consensus, immutability, high consistency, security and privacy protection."。Exactly how much magic blockchain has to make people so trusting, or rather, we're saying "**Letters**What is the time to believe?。

Information, which refers to natural attributes and behavioral information such as identity, assets, prices, geographic location, etc., is not inherently trustworthy because the information is scattered, incomplete, may be false, and some may even take advantage of the asymmetry of the information for profit。


Organize the information into structured data, through data verification, to ensure that it can maintain integrity, network-wide consistency, traceability, and will not be maliciously tampered with；Through redundant storage, ensure that it is open, shared, accessible, and ensure that the data is always valid。Then, this information itself can be "trusted," thus becoming everyone's "public knowledge," become the whole network participants are recognized. "**greatest common divisor**”。

Information is commercially "creditworthy" if it embodies value and if it is recognized, recognized, quantifiable, has a tradable equivalent attribute, or may increase in value over time, or even recognized by judicial endorsement。

Just because we know someone doesn't mean we trust them。However, this person has always performed well, in the community words and deeds, gradually gained everyone's trust。Trust at this point is still not equal to credit, unless the person has considerable assets, or his or her personal history has the ability to make profits and repay, and the probability is that he or she will continue to hold assets and accept debt in the future, then this person has "**CREDIT**”。


The blockchain system is based on algorithms rather than human governance, and is expected to solidify information into everyone's**Trust**anchor point；It is expected to convert various real-world resources into redeemable digital assets through technical means, and launch a series of multi-party business collaboration activities, which is the so-called "information to trust to credit," and even because of the blockchain, the black technology, effective and incomprehensible mystery, the word "letter" seems to have been sublimated into "**Faith**”。

So what do we believe in when we say we believe in blockchain?？

## letter cryptography algorithm

Blockchain uses algorithms to achieve trust, and one of the most important algorithms is cryptography。The most basic cryptographic applications in blockchains are HASH digests, symmetric and asymmetric encryption algorithms, and related signature verification algorithms。

**HASH algorithm**The old version of has been proven to be hackable and discarded, and the algorithms currently in use, such as SHA256, are still unbreakable。The characteristic of HASH algorithm is to generate a fixed length of data from a pile of data in one direction, which basically does not collide, and can play the role of "fingerprint" of the original data。

**Digital Signature**Generally based on the public-private key system, signed with the private key, public key verification or vice versa。Digital signatures are derived from the reliability of cryptography, making it impossible for someone to forge someone else's private key signature, so a person with a private key can sign his assets through a digital signature to confirm the right, or in the transaction between the two parties, the use of the counterparty's public key to initiate the transaction, the transfer of assets to the other party, the other party with their own private key to verify the signature to unlock, in order to obtain ownership。

**AES, RSA, ECC Elliptic curves**Several symmetric and asymmetric algorithms are widely used in data encryption and decryption, secure communication and other scenarios, the security level depends on the algorithm itself and the key length, when AES uses 128 ~ 512 bit keys, RSA / ECC uses 1024 or even 2048 bit keys, the data it protects theoretically requires hundreds of millions of years of computing time for ordinary computers to brute force。These algorithms have been tested in business, science, and military。

There are also new directions in the field of cryptography, such as homomorphic encryption, zero-knowledge proof, ring signature group signature, lattice cryptography, etc., which are currently in the stage of development from theory to engineering, and are in the process of rapid optimization in terms of function, security strength and efficiency, and the possibility of landing can already be seen。At the same time, we also realize that cryptography usually needs to go through a long period of development, verification, stability before it can be widely recognized, either through a lot of tests in practice, or through the audit and certification of authoritative institutions, in order to shine in the field of production。From theory to engineering in cryptography, there are often long periods of time。

A basic philosophy of encryption algorithms is**Calculate cost**, is safe when the value of the asset protected by an algorithm is much lower than the cost of breaching the algorithm。But if you use an algorithm to protect a priceless treasure, someone will naturally attack and profit at no cost, so the security of cryptography is also dialectical and needs to be quantified。

With the rise of quantum computers and other theories, classical cryptography may undergo some challenges, but the theoretical improvement of quantum computers and engineering implementation will take time, at present, basically we can almost "unconditional" believe that the blockchain has been used in the cryptography algorithm, at the same time, the blockchain field practitioners are also introducing a variety of quantum-resistant cryptography algorithm, which is a continuous game。

## letter data

The data structure of the blockchain is nothing more than a block+Chain。The new block will be its own block height, transaction list, and the previous block's HASH, together to generate a HASH as the new block's identity, so the cycle, forming a interlocking data chain。Any byte or even a Bit in this chain is modified and will be checked and found because of the characteristics of the HASH algorithm。


At the same time, the block data is broadcast to all participants across the network, and the more participants there are, the stronger the scale effect。Even if a few people forcibly modify or delete their own block data, it is easy for others to check out the anomalies and reject them, and only the data approved by the majority are retained and circulated。In other words, the data is the form of people staring at people, and there are multiple copies, once landed, as long as the chain is still there, the data can be retained forever。

Based on easy-to-verify chain data structure, group redundancy preservation, common authentication, blockchain data is "difficult to tamper with," all people get the same data, information is open and transparent, public knowledge can be highlighted and solidified。

From another point of view, the data to achieve trust, but whether to achieve "credit" depends on the value of the data, that is, the information carried by the data itself, whether it can represent valuable assets, useful information, such as identity, transaction relationships, transaction behavior, big data, etc., can represent a certain commercial value。This data, if shared, is enough to build a complete business foundation。


But if it's in a scenario where privacy is overemphasized, there's very little information that people are willing to share, and it's hard to reach the "maximum common divisor" of credit。However, in the current business environment, information isolation and privacy protection are hard demands, and information sharing and privacy protection have become severe spears and shields unless the entire business relationship and business logic are revolutionized。


Therefore, research related to privacy protection has received a lot of attention, such as "multi-party secure computing," "zero-knowledge proof" theory is popular。Theoretically, it is possible to publish very little information and be verifiable, but the complexity and computational overhead is something to be addressed at the engineering level。

## letter game theory

The most mysterious part of the blockchain is the "consensus algorithm."。A consensus algorithm is defined as a mechanism within a group to coordinate bookkeeping, either together or in turn, to arrive at an uncontroversial, unique result and to ensure that the mechanism is sustainable。



In other words, we all maintain a ledger together and choose who is the bookkeeper？What makes you believe that the bookkeeper's actions are correct?？How to prevent the bookkeeper from doing evil？How to get an incentive if the bookkeeper keeps the book correctly？The consensus mechanism fully answers these questions。

The logic of consensus is happening online, but in reality, behind it is a real-world competitive game。

POW (Proof of Work) uses computing power to compete for the bookkeeper's seat and get the bookkeeper's reward。In real life, in order to build a competitive computing power plant, miners usually need to develop or purchase a large number of new models of mining machines, transport them to areas with stable and cheap electricity supply, consume a lot of electricity, network fees and other operating expenses, and move their families when they are regulated, travel around the world, and actually invest a lot of (real-world) money, effort and carry huge risks。If you want to get a stable and substantial income in the POW competition, the capital invested is easily hundreds of millions of dollars, no less than running a business。

POS and DPOS replace computing power consumption with proof of equity, which looks much more environmentally friendly。The token representing the rights and interests, in addition to the founding team's own issuance, the "miners" generally need to be obtained through currency exchange, or direct fiat currency purchase of digital currency, even if the currency exchange, out of the currency is often purchased in fiat currency, or at least these rights and interests can be priced in fiat currency, which is actually the real world of wealth injection and endorsement。

However, in contrast to real business relationships, consensus such as POW and POS does not have a legal and regulatory mechanism to cover the bottom, and is vulnerable to changing gaming situations, such as the size of the community, changes in miners, and changes in core technology operations teams。Slowly, people who were originally rich and capable may become richer and more powerful, decentralized networks may gradually become cartels, and the ties between miners and the technical community will continue to make waves, causing bifurcations, rollbacks, price rips, and cutting of leeks。

In general, people still trust "autonomy" on the blockchain, in which a single event (such as a transaction) is "probabilistic," while the whole network pursues "ultimate consistency" (consistency of the public ledger)。This short-term probabilistic and long-term certainty can, to some extent, achieve a dynamic "**Nash equilibrium**"The ecology that supports the chain evolves a mysterious sense of" faith. "。


On the other hand, the bookkeeper of the alliance chain is generally an institutional role。The alliance chain requires the identity of the bookkeeper to know that the participants are licensed to access the network and that they are a**cooperative game**。Alliance chains introduce real-world identity information as**Credit Endorsement**, such as industrial and commercial registration information, business reputation, acceptance credit, working capital, or industry status, practice license, legal identity, etc., all the actions of participants in the chain can be audited, traced, but also so that the relevant regulatory authorities can be targeted when necessary, precise punishment, enforcement, with a high deterrent。

In this environment, the participants of the alliance chain work together to maintain the network, share the necessary information, conduct transactions in an equal, transparent, secure and trusted network, and only need to prevent the risk of malicious operations by a small number of bookkeepers and avoid the availability risk on the system。By introducing the necessary trust endorsements in the real world, even though the alliance chain business logic is very complex, the trust model is more intuitive。

So, behind the so-called consensus mechanism is still the real-world competition for financial and material resources and credit endorsement, as well as the corresponding effective incentive and disciplinary mechanisms。

There is no such thing as a free lunch, and no such thing as plain love or hate。To "trust" a bookkeeper is to believe in the costs he has invested in the real world, the price he has paid, and the penalties that deter him, given that the whole mechanism has, and to believe that the bookkeeper will not destroy the network for no reason in order to continue to gain and add value。

## Letter Smart Contract

Smart contracts were proposed by the prolific cross-cutting legal scholar Nick Szabo。In several articles published on his website, he mentions the idea of smart contracts, defined as follows:

"A smart contract is a set of digitally defined commitments, including agreements on which contract participants can enforce those commitments.""。

Simply put, it can be understood as an electronic version of a paper contract, implemented in code, running indiscriminately at every node of the blockchain network, executing the established contract rules with consensus。

Smart contracts are typically based on a specially crafted virtual machine that runs in sandbox mode, shielding out features that could lead to inconsistencies。For example, the operation of obtaining system time may have different clocks on different machines, which may lead to problems with time-dependent business logic。Another example is random numbers, as well as external file systems, external website input, etc., which can lead to different virtual machine execution results and are isolated by the virtual machine sandbox environment。

If you want to write a contract in the Java language, either cut out the relevant functions in the JDK (system time, random numbers, network, files, etc.), or put it in a docker with strict permission control and isolation settings。Or simply design a new language, such as Ethereum's Solidity, that implements only specific instructions。Or give up some "smart" features and use a simple stack instruction sequence to complete the key verification judgment logic。

Therefore, the implementation of smart contracts on the blockchain, based on the sandbox mechanism control, with the consensus algorithm of the blockchain, to achieve the whole network consistent, difficult to tamper with, undeniable and other characteristics, the output of the results of the operation is a contract recognized by the whole network, known as "Code is Law"。

However, as long as it is code, there must be a probability of bugs or vulnerabilities, which may come from the underlying virtual machine and network vulnerabilities, and more from the logic implementation。Just search for "smart contract security vulnerabilities," there are a bunch of search results, including overflow, re-entry, permission errors, and even low-level errors。In recent years, these vulnerabilities have caused a variety of asset losses, most notably the DAO project code vulnerability, Parity's multi-sign wallet vulnerability, an Internet company's token trading process overflow to zero.....

Technical articles can refer to: [https://paper.seebug.org/601/](https://paper.seebug.org/601/)


At present, the security of smart contracts in the industry is also unique, including security companies and white hat review, formal proof, public testing, etc., the security issues will be improved to a certain extent。If there is another problem, either the hacker is too powerful, or the programmer can only be caught offering sacrifices to heaven:)

Therefore, the letter smart contract, is conditional, is to believe that after strict testing, long-term stable operation, in case of error there are ways to remedy (rather than desperate can only wait for the fork big move) contract。The smart contracts in the alliance chain are generally rigorously tested, and the grayscale verification process will be implemented when they are launched, the operation process will be monitored during operation, and measures such as accountability and remediation (correction, reconciliation, freezing...) will be designed according to the governance rules, which is more credible。

## letter to the middleman (？）

Note that there is a question mark in the title of this section, blockchain advocates the "to the center or multi-center, to the intermediary or weak intermediary" mode of operation, but due to the current development is not perfect, many scenarios actually introduce intermediaries, such as currency exchange usually need to go through the exchange, especially the centralized exchange。The trading principle is to require users to deposit assets into the exchange's account, the transaction is actually in the exchange's database for bookkeeping, only when depositing or withdrawing money, will interact with the blockchain network。

The trust model of the exchange is somewhat decoupled from the blockchain, when the qualifications of the exchange itself, the technical capabilities, security capabilities, asset credit and acceptance capabilities of the operator, are what users need to be most concerned about。Once there is a problem with the exchange, such as running away, bankruptcy, dark operations, self-theft, basically retail investors can only do leeks。

Don't say much, see the famous "Mentougou incident":[https://baike.baidu.com/item/Mt.Gox/3611884](https://baike.baidu.com/item/Mt.Gox/3611884)

So, believing in a custodian is a matter of opinion, except that in the current model, roles like exchanges still operate in certain areas。In 2018, there were more than 10,000 virtual digital asset exchanges worldwide, and how many of them can achieve high-standard security, standardized operations, and cleanliness... that depends on the situation。

One last point: the alliance chain does not have a virtual digital asset exchange like the public chain by default。

------

There are many details in the blockchain field, the above list of the main points, trust technology, trust consensus mechanism, trust scale community game, more than trust "people"。"People" is a kind of uncertainty, you can trust a person you are familiar with very old iron, you can also trust a large group of people who have a common idea and have a perfect mechanism to cooperate, but you can't trust a small group of people with malicious intentions, or become leeks in minutes:)

To summarize, in the blockchain world, one can build the following basic confidence:

- I hold assets and information that only I can access or disclose
- I can participate in transactions according to fair rules, share information, transfer in and out of assets
-Assets transferred to me by others must be valid and will not be invalidated by repeated spending
- Once the deal is done, it's a sure thing
- Everything that has happened is verifiable and traceable
- People who break the rules lose more
- The people who maintain the network will be properly rewarded for their work, and the whole model is sustainable

Based on these confidence and trust, under the premise of legal compliance, it would be an ideal state for people to inject various assets into the network and carry out complementary and mutually beneficial, transparent rules, open, fair and just business practices。
